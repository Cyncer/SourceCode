package com.location;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ApplicationController;
import com.app.android.cync.NavigationDrawerActivity;
import com.app.android.cync.R;
import com.utils.CommonClass;

public class LocationService extends Service {
	// private static LocationManager mLocationManager;
	private final static String TAG = "LocationService";
	Location lastlocation = new Location("last");
	Data data;
	int notificationID = 1011;
	double currentLon = 0;
	double currentLat = 0;
	double lastLon = 0;
	double lastLat = 0;
	long time = 0;
	PendingIntent contentIntent;
	Handler h = new Handler();
	Runnable r;
	Handler locationHandle = new Handler();
	Runnable locationrunable;
	GPSTrackerLD tracker;
	LocationDatabase db;
	static NotificationManager mNotificationManager;



	@Override
	public void onCreate() {
		/*
		 * mLocationManager = (LocationManager)
		 * this.getSystemService(Context.LOCATION_SERVICE);
		 * mLocationManager.addGpsStatusListener( this);
		 */

		db = new LocationDatabase(ApplicationController.getInstance());


		String distance=db.getDistance();
		double fDistance = 0.0f;

		if (distance.trim().length() > 0) {
			fDistance = Float.parseFloat(distance);
		}




		CommonClass.setPastDistance(ApplicationController.getInstance(), (float) fDistance);

		Log.d("amit", "rebindCalled last distance = "+CommonClass.getPastDistance(ApplicationController.getInstance()));
		data = new Data();
		data = new Data();
		data.setRunning(true);
		data.setFirstTime(true);
		if (data.isRunning()) {
			time = SystemClock.elapsedRealtime();

			String mDuration=db.getDuration();
			long mtime = 0;
			if (mDuration.trim().length() > 0) {
				mtime = Long.parseLong(mDuration);

			}

			//time=time+mtime;

			data.setTime(time);
		}
		tracker = new GPSTrackerLD(getApplicationContext());
		initHandler();
		initLocationHandler();
		// mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// CommonClass.getTrackingInterval(getBaseContext()), 0, this);
	}

	private void initHandler() {
		// TODO Auto-generated method stub
		h = new Handler();
		r = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (data.isRunning()) {
					long times = SystemClock.elapsedRealtime() - time;
					data.setTime(times);

				}
				h.postDelayed(this, 1000);
			}
		};
		h.postDelayed(r, 1000);

	}

	private void initLocationHandler() {
		// TODO Auto-generated method stub
		locationHandle = new Handler();
		locationrunable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (data.isRunning()) {
					try {

						double lat = tracker.getLatitude();
						double longt = tracker.getLongitude();
						double speed = tracker.getSpeed();

						Log.d("asd", "gps tracker = " + lat + " long " + longt);
						Location location = tracker.getLastLocation();
						currentLat = location.getLatitude();
						currentLon = location.getLongitude();

						if (data.isFirstTime()) {
							lastLat = currentLat;
							lastLon = currentLon;
							data.setFirstTime(false);
						}

						lastlocation.setLatitude(lastLat);
						lastlocation.setLongitude(lastLon);
						double distance = lastlocation.distanceTo(location);




						data.addDistance(distance);



						lastLat = currentLat;
						lastLon = currentLon;
//						if (location.getAccuracy() < distance) {
//							data.addDistance(distance);
//
//							lastLat = currentLat;
//							lastLon = currentLon;
//						}

						if (location.hasSpeed()) {
							data.setCurSpeed(location.getSpeed() * 3.6);
						}

						Log.d("amit","distance = " + data.getDistance());


						//Log.i(TAG,"kp distance = " +"Lat="+location.getLatitude()+" Lng="+location.getLongitude()+" Speed="+data.getCurrentSpeedKM());
						Log.i(TAG,"kp Duration = " +data.getTime());
						long time = SystemClock.elapsedRealtime();
						//Log.i(TAG, "run: === "+time);


						if(!CommonClass.getlocationServicepreference(
								ApplicationController.getInstance()).equalsIgnoreCase("false")) {

							createNotificationIcon();

							db.INSERT_NEW_MESSAGE("" + CommonClass.getUserpreference(ApplicationController.getInstance()).user_id, "" + location.getLatitude(),
									"" + location.getLongitude(), "", CommonClass.getCurrentTimeStamp(), data.getCurrentSpeedKM(), "" + data.getAverageSpeedInKM(), "" + data.getDistanceKm(), "" + data.getTime());


							Intent intent = new Intent("com.cync.location.data"); //FILTER is a string to identify this intent
							intent.putExtra("latitude", location.getLatitude());
							intent.putExtra("longitude", location.getLongitude());
							intent.putExtra("duration", data.getTime());
							sendBroadcast(intent);


						}
						else
						{
							cancellAllNotifications();
							db.UpdateLastRecord( "" + data.getTime(),"" + data.getDistanceKm());
						}



					//	Toast.makeText(getApplicationContext(), "Lat="+location.getLatitude()+"\nLng="+location.getLongitude()+"\nSpeed="+(location.getSpeed() * 3.6), Toast.LENGTH_SHORT).show();


//						if (location.hasSpeed()) {
//							SpannableString s = new SpannableString(
//									String.format("%.0f",
//											location.getSpeed() * 3.6) + "km/h");
//							s.setSpan(new RelativeSizeSpan(0.25f),
//									s.length() - 4, s.length(), 0);
//						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				locationHandle.postDelayed(this,
						CommonClass.getTrackingInterval(getBaseContext()));
			}
		};
		locationHandle.postDelayed(locationrunable,
				CommonClass.getTrackingInterval(getBaseContext()));

	}

	public void onLocationChanged(Location location) {

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// If we get killed, after returning from here, restart






		locationHandle.removeCallbacks(locationrunable);
		locationHandle.postDelayed(locationrunable,1000);
		// mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// CommonClass.getTrackingInterval(getBaseContext()), 0, this);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null

		return null;
	}



	/* Remove the locationlistener updates when Services is stopped */
	@Override
	public void onDestroy() {
		/*
		 * mLocationManager.removeUpdates(this);
		 * mLocationManager.removeGpsStatusListener(this);
		 */


			cancellAllNotifications();


		h.removeCallbacks(r);
		locationHandle.removeCallbacks(locationrunable);
		stopForeground(true);
	}


	private void createNotificationIcon() {
		// TODO Auto-generated method stub


		Intent intent = new Intent(ApplicationController.getContext(),
				NavigationDrawerActivity.class);
		intent.putExtra("notification_ride", "");

		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
		PendingIntent contentIntent = PendingIntent.getActivity(
				ApplicationController.getContext(), iUniqueId, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);



		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(getNotificationIcon())
				.setContentTitle("Cync Ride")
				.setOngoing(true)
				.setContentText("Your ride is in progress...");

		mBuilder.setContentIntent(contentIntent);

		Notification notification = mBuilder.getNotification();
		notification.contentView.setImageViewResource(android.R.id.icon,R.mipmap.ic_launcher);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notificationID, mBuilder.getNotification());

	}

	public   void cancellAllNotifications() {
		// TODO Auto-generated method stub
		if (mNotificationManager != null)
			mNotificationManager.cancel(notificationID);
	}

	private int getNotificationIcon() {
		boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
		return useWhiteIcon ? R.mipmap.notification_icon : R.mipmap.ic_launcher;
	}
}