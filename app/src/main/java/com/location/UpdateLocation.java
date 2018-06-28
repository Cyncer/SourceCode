package com.location;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class UpdateLocation extends Service {

//	public static final String BROADCAST_SERVICE_STARTED = "com.app.innany.intent.action.service_started";
//	/**
//
//	 */
//	public static final String BROADCAST_SERVICE_STOPPED = "com.app.innany.intent.action.service_stopped";
//
//	public static final String CATEGORY_STATE_EVENTS = "com.app.innany.intent.category.service_start_stop_event";

	
	public static final String BROADCAST_SERVICE_STARTED = "com.twinone.locker.intent.action.service_started";
	/**

	 */
	public static final String BROADCAST_SERVICE_STOPPED = "com.twinone.locker.intent.action.service_stopped";

	public static final String CATEGORY_STATE_EVENTS = "com.twinone.locker.intent.category.service_start_stop_event";
	
	private static final int REQUEST_CODE = 0x1234AF;
	private static final String TAG = "iNanny";
	boolean isRunnning = false;

//	private static final String ACTION_STOP = "com.app.innany.intent.action.stop_lock_service";
//
//	private static final String ACTION_START = "com.app.innany.intent.action.start_lock_service";
//
//	private static final String ACTION_RESTART = "com.app.innany.intent.action.restart_lock_service";
//
//	private static final String EXTRA_FORCE_RESTART = "com.app.innany.intent.extra.force_restart";

	private static final String ACTION_STOP = "com.twinone.locker.intent.action.stop_lock_service";

	private static final String ACTION_START = "com.twinone.locker.intent.action.start_lock_service";

	private static final String ACTION_RESTART = "com.twinone.locker.intent.action.restart_lock_service";

	private static final String EXTRA_FORCE_RESTART = "com.twinone.locker.intent.extra.force_restart";
	private boolean mShowNotification;

	private boolean mExplicitStarted;
	private boolean mAllowDestroy;
	private boolean mAllowRestart;
	private Handler mHandler;
	private BroadcastReceiver mScreenReceiver;

	@Override
	public IBinder onBind(Intent i) {
		return new LocalBinder();
	}

	public class LocalBinder extends Binder {
		public UpdateLocation getInstance() {
			return UpdateLocation.this;
		}
	}

//	private final class ScreenReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//
//			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//				// Log.i(TAG, "Screen ON");
//				// Trigger package again
//				mLastPackageName = "";
//				startAlarm(UpdateLocation.this);
//			}
//			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//				// Log.i(TAG, "Screen OFF");
//				stopAlarm(UpdateLocation.this);
//
//			}
//		}
//	}
//
//	;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");

	}

	private boolean init() {
		Log.d(TAG, "init");

	 

		// mScreenReceiver = new ScreenReceiver();
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(Intent.ACTION_SCREEN_ON);
		// filter.addAction(Intent.ACTION_SCREEN_OFF);
		// registerReceiver(mScreenReceiver, filter);

		startAlarm(this);

		// Tell MainActivity we're done
		Intent i = new Intent(BROADCAST_SERVICE_STARTED);
		i.addCategory(CATEGORY_STATE_EVENTS);
		sendBroadcast(i);
		return true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Log.d(TAG, "test");
		if (intent == null || ACTION_START.equals(intent.getAction())) {
			if (!mExplicitStarted) {
				Log.d(TAG, "explicitStarted = false");
				if (init() == false) {
					doStopSelf();
					return START_NOT_STICKY;
				}
				mExplicitStarted = true;
			}
			if (!isRunnning) {
				checkPackageChanged();
			}
		} else if (ACTION_RESTART.equals(intent.getAction())) {
			if (mExplicitStarted
					|| intent.getBooleanExtra(EXTRA_FORCE_RESTART, false)) {
				Log.d(TAG,
						"ACTION_RESTART (force="
								+ intent.getBooleanExtra(EXTRA_FORCE_RESTART,
										false));
				// init();
				doRestartSelf(); // not allowed, so service will restart
			} else {
				doStopSelf();
			}
		} else if (ACTION_STOP.equals(intent.getAction())) {
			Log.d(TAG, "kpkACTION_STOP");
			doStopSelf();
		}

		return START_STICKY;
	}

	private String mLastPackageName;
	Handler handler = new Handler();
	Runnable runnable;

	// private String mLastCompleteName;

	private void checkPackageChanged() {

		handler.removeCallbacks(runnable);
		runnable = new Runnable() {
			@Override
			public void run() {
				isRunnning = true;

				/* and here comes the "trick" */
				GPSTrackerLD gps = new GPSTrackerLD(getBaseContext());

				if (gps.isGPSEnabled) {
					double lat = gps.getLatitude();
					double longt = gps.getLongitude();
					Calendar c = Calendar.getInstance(TimeZone
							.getTimeZone("PST"));
					Log.i(TAG, "Location=== lat="+lat);
					Log.i(TAG, "Location=== long="+longt);
					Log.i(TAG, "Location=== time="+c.getTime());
					Log.i(TAG, "Location=== --------------------------------");

					// LocationUpdateTask locationUpdateTask = new
					// LocationUpdateTask(
					// responder, getBaseContext());
					// locationUpdateTask.execute(String.valueOf(lat), String
					// .valueOf(longt), Utility
					// .getAppPrefString(getBaseContext(),
					// ConstantEyeWitness.PARAMS_USER_ID));

				} else {

				}
				handler.postDelayed(this, 10000);

			}
		};

		handler.post(runnable);

	}

	public static void start(Context c) {
		Log.i("Kp","kp start service call inside");
		startAlarm(c);
	}

	/**
	 * @param c
	 * @return The new state for the service, true for running, false for not
	 *         running
	 */
	public static boolean toggle(Context c) {
		if (isRunning(c)) {
			stop(c);
			return false;
		} else {
			start(c);
			return true;
		}
	}

	public static boolean isRunning(Context c) {
		ActivityManager manager = (ActivityManager) c
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (UpdateLocation.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Starts the service
	 */
	private static void startAlarm(Context c) {
		AlarmManager am = (AlarmManager) c.getSystemService(ALARM_SERVICE);
		PendingIntent pi = getRunIntent(c);
		String s = "2000";
		if (s.length() == 0)
			s = "0";
		long interval = Long.parseLong(s);
		Log.d(TAG, "Scheduling alarm (interval=" + interval + ")");
		long startTime = SystemClock.elapsedRealtime();
		am.setRepeating(AlarmManager.ELAPSED_REALTIME, startTime, interval, pi);
	}

	private static PendingIntent running_intent;

	private static PendingIntent getRunIntent(Context c) {
		if (running_intent == null) {
			Intent i = new Intent(c, UpdateLocation.class);
			i.setAction(ACTION_START);
			running_intent = PendingIntent.getService(c, REQUEST_CODE, i, 0);
		}
		return running_intent;
	}

	private static void stopAlarm(Context c) {
		AlarmManager am = (AlarmManager) c.getSystemService(ALARM_SERVICE);
		am.cancel(getRunIntent(c));
	}

	/**
	 * Stop this service, also stopping the alarm
	 */
	public static void stop(Context c) {
		stopAlarm(c);
		 
		Log.i("Kp","kp stop service call inside service");
		Intent i = new Intent(c, UpdateLocation.class);
		i.setAction(ACTION_STOP);
		c.startService(i);
	}

	/**
	 * Re-initialize everything.<br>
	 * This has only effect if the service was explicitly started using
	 * {@link #start(Context)}
	 */
	public static void restart(Context c) {
		Intent i = new Intent(c, UpdateLocation.class);
		i.setAction(ACTION_RESTART);
		c.startService(i);
	}

	/**
	 * Forces the service to stop and then start again. This means that if the
	 * service was already stopped, it will just start
	 */
	public static void forceRestart(Context c) {
		Intent i = new Intent(c, UpdateLocation.class);
		i.setAction(ACTION_RESTART);
		i.putExtra(EXTRA_FORCE_RESTART, true);
		c.startService(i);
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		Log.d(TAG, "onDestroy: (mAllowRestart=" + mAllowRestart + ")");
		if (mScreenReceiver != null)
			unregisterReceiver(mScreenReceiver);
		if (mShowNotification)
			stopForeground(true);

		if (mAllowRestart) {
			start(this);
			mAllowRestart = false;
			return;
		}

		Log.i(TAG, "onDestroy (mAllowDestroy=" + mAllowDestroy + ")");
		if (!mAllowDestroy) {
			Log.d(TAG, "Destroy not allowed, restarting service");
			start(this);
		} else {
			// Tell MainActivity we're stopping
			Intent i = new Intent(BROADCAST_SERVICE_STOPPED);
			i.addCategory(CATEGORY_STATE_EVENTS);
			sendBroadcast(i);
		}
		mAllowDestroy = false;
	}

	private void doStopSelf() {
		stopAlarm(getBaseContext());
		mAllowDestroy = true;
		stopForeground(true);
		stopSelf();
	}

	private void doRestartSelf() {
		Log.d(TAG, "Setting allowrestart to true");
		mAllowRestart = true;
		stopSelf();
	}

	// LocationUpdateTask.Responder responder = new
	// LocationUpdateTask.Responder() {
	//
	// @Override
	// public void onBegin() {
	//
	// }
	//
	// @Override
	// public void onComplete(boolean result, String message) {
	// try {
	// if (result) {
	//
	// } else {
	//
	// }
	// } catch (NullPointerException e) {
	//
	// }
	//
	// }
	//
	// };
}
