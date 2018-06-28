package com.location;

import java.util.List;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSTrackerLD extends Service implements LocationListener 
{

	private final Context mContext;

	// flag for GPS status
	public boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude
	double speed;// speed
	String provider;
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 500 ; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSTrackerLD(Context context) 
	{
		this.mContext = context;
		getLocation();
	}

	public Location getLocation() 
	{
		
		
		
	 
         final Criteria criteria = new Criteria();
         
         criteria.setAccuracy(Criteria.ACCURACY_FINE);
         criteria.setSpeedRequired(true);
         criteria.setAltitudeRequired(false);
         criteria.setBearingRequired(false);
         criteria.setCostAllowed(true);
         criteria.setPowerRequirement(Criteria.POWER_LOW);
         
         
         

		
		
		
		
		
		
		
		
		try 
		{
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);
			// getting GPS status
						isGPSEnabled = locationManager
								.isProviderEnabled(LocationManager.GPS_PROVIDER);

						// getting network status
						isNetworkEnabled = locationManager
								.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			   final String bestProvider = locationManager.getBestProvider(criteria, true);
			   
			   if (bestProvider != null && bestProvider.length() > 0)
		         {
				   locationManager.requestLocationUpdates(bestProvider,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				   
				   if (locationManager != null) 
					{
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) 
						{
							latitude = location.getLatitude();
							longitude = location.getLongitude();
							speed=location.getSpeed();
							Log.i("GPSTrackerLD", "GPS DATA= Lat="+latitude);
						}
					}
				   
				   
		         }
		         else
		         {
		        		if (!isGPSEnabled && !isNetworkEnabled) 
		    			{
		    				// no network provider is enabled
		    			}
		    			else 
		    			{
		    				this.canGetLocation = true;
		    				if (isGPSEnabled) 
		    				{
		    					if (location == null) 
		    					{
		    						locationManager.requestLocationUpdates
		    						(
		    								LocationManager.GPS_PROVIDER,
		    								MIN_TIME_BW_UPDATES,
		    								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		    						if (locationManager != null) 
		    						{
		    							location = locationManager
		    									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		    							if (location != null) 
		    							{
		    								latitude = location.getLatitude();
		    								longitude = location.getLongitude();
		    								provider="GPS PROVIDER";
		    								
		    								
		    								Log.i("GPSTrackerLD", "GPS DATA= Lat="+latitude);
		    								Log.i("GPSTrackerLD", "GPS DATA= Lng="+longitude);
		    							}
		    						}
		    					}
		    				}
		    				else if (isNetworkEnabled) 
		    				{
		    					locationManager.requestLocationUpdates(
		    							LocationManager.NETWORK_PROVIDER,
		    							MIN_TIME_BW_UPDATES,
		    							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		    					if (locationManager != null) 
		    					{
		    						location = locationManager
		    								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		    						if (location != null) 
		    						{
		    							latitude = location.getLatitude();
		    							longitude = location.getLongitude();
		    							speed=location.getSpeed();
		    							provider="NETWORK PROVIDER";
		    							Log.i("GPSTrackerLD", "NETWORK DATA= Lat="+latitude);
	    								Log.i("GPSTrackerLD", "NETWORK DATA= Lng="+longitude);
		    						}
		    					}
		    				}
		    				// if GPS Enabled get lat/long using GPS Services
		    				
		         }
			   
			   

			

		
				
				// this.updateSpeed(null);
			}

		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return location;
	}
	
	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopUsingGPS()
	{
		if(locationManager != null)
		{
			locationManager.removeUpdates(GPSTrackerLD.this);
		}		
	}
	
	
	
//	 private void updateSpeed(Location mlocation) {
//         // TODO Auto-generated method stub
//         float nCurrentSpeed = 0;
//
//         if(mlocation != null)
//         {
//                
//               nCurrentSpeed = mlocation.getSpeed();
//         }
//
//         Formatter fmt = new Formatter(new StringBuilder());
//         fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
//         String strCurrentSpeed = fmt.toString();
//         strCurrentSpeed = strCurrentSpeed.replace(' ', '0');
//
//         speed=Float.parseFloat(strCurrentSpeed);
//         
//         location.setSpeed((float) speed);
//         
//          
// 
//   }
	 
	 
	/**
	 * Function to get latitude
	 * */
	public double getLatitude()
	{
		if(location != null)
		{
			latitude = location.getLatitude();
		}
		
		// return latitude
		return latitude;
	}
	public Location getLastLocation()
	{
		if(location != null)
		{
			return this.location;
		}
		
		// return latitude
		return null;
	}
	
	/**
	 * Function to get Speed
	 * */
	public double getSpeed()
	{
		if(location != null)
		{
			speed = location.getSpeed();
		}
		
		// return latitude
		return speed;
	}
	
	public String getProviderType()
	{
		 
		
		// return latitude
		return provider;
	}
	
	
	/**
	 * Function to get longitude
	 * */
	public double getLongitude()
	{
		if(location != null)
		{
			longitude = location.getLongitude();
		}
		
		// return longitude
		return longitude;
	}
	
	/**
	 * Function to check GPS/wifi enabled
	 * @return boolean
	 * */
	public boolean canGetLocation() 
	{
		return this.canGetLocation;
	}
	
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
   	 
        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");
 
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
 
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog,int which) 
            {
            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	mContext.startActivity(intent);
            	dialog.dismiss();
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            System.exit(13);
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
	}

	public void onLocationChanged(Location location) 
	{
//		if(location!=null)
//			updateSpeed(location);
		this.location = location;
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	}

	public void onProviderDisabled(String provider) 
	{
	}

	public void onProviderEnabled(String provider) 
	{
	}

	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
	}

	public IBinder onBind(Intent arg0) 
	{
		return null;
	}

}
