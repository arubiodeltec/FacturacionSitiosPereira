package com.example.location;

import java.util.LinkedList;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.Logueo.MainActivity;
import com.example.gestiondeltec.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationManager implements
GooglePlayServicesClient.OnConnectionFailedListener,
GooglePlayServicesClient.ConnectionCallbacks,
LocationListener {
	private Context mContext;
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	private static LocationManager locationManager;
	private List<LocationListener> locationListeners;
	private static final String PREF = "location";
	private static final String PREF_FILE = "LOCATION_PREF_FILE";
	private Location location;
	private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
	private LocationSyncActivity activity;


	public static LocationManager getInstance(Context context){
		if (locationManager == null)
			locationManager = new LocationManager(context);
		return locationManager;
	}

	private LocationManager(Context context){
		this.mContext = context;
		locationListeners = new LinkedList<LocationListener>();
	}

	public void setStartUpdates(int priority, long interval, long fastestInterval, int smallestDisplacement){
		if(mLocationClient != null && mLocationClient.isConnected()){
			retartUpdates(priority, interval,  fastestInterval,  smallestDisplacement);
			return;
		}
		mLocationClient = new LocationClient(mContext,this,this);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(priority);
		mLocationRequest.setInterval(interval);
		mLocationRequest.setFastestInterval(fastestInterval);
		mLocationRequest.setSmallestDisplacement(smallestDisplacement);
		mLocationClient.connect();		
	}
	
	private void retartUpdates(int priority, long interval, long fastestInterval, int smallestDisplacement) {
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(priority);
		mLocationRequest.setInterval(interval);
		mLocationRequest.setFastestInterval(fastestInterval);
		mLocationRequest.setSmallestDisplacement(smallestDisplacement);
		mLocationClient.requestLocationUpdates(mLocationRequest,this);
	}

	public boolean hasLocationUpdatesEnabled(){
		return mLocationClient!= null && mLocationClient.isConnected();
	}

	public void setLocation(LocationSyncActivity locationSyncActivity) {
		this.activity = locationSyncActivity;
	}

	@Override
	public void onConnected(Bundle bundle) {


		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {

			if (ActivityCompat.checkSelfPermission ( mContext.getApplicationContext() ,
					Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
					&& ActivityCompat.checkSelfPermission ( mContext.getApplicationContext() ,
					Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {

				ActivityCompat.requestPermissions(activity
						,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
						REQUEST_CODE_LOCATION_PERMISSION
				);

				Log.i("Sin permisos","Pedir permiso");

			}else{
				requestUpdates();
			}
		} else {
			// Solicite dinámicamente el permiso android.permission.ACCESS_BACKGROUND_LOCATION además de los permisos anteriores si el nivel de API es superior a 28.
			if (ActivityCompat.checkSelfPermission ( mContext.getApplicationContext() ,
					Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
					&& ActivityCompat.checkSelfPermission ( mContext.getApplicationContext() ,
					Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
					&& ActivityCompat.checkSelfPermission ( mContext.getApplicationContext(),
					"android.permission.ACCESS_BACKGROUND_LOCATION" )!= PackageManager.PERMISSION_GRANTED) {

				ActivityCompat.requestPermissions(activity
						,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
						REQUEST_CODE_LOCATION_PERMISSION
				);

				Log.i("Sin permisos","Pedir permiso");


			}else{
				requestUpdates();
			}
		}





	}



	private void requestUpdates() {

		mLocationClient.requestLocationUpdates(mLocationRequest,this);
	}
	public void stopUpdates(){
		if(mLocationClient.isConnected())
			mLocationClient.removeLocationUpdates(this);
	}

	@Override
	public void onDisconnected() {

	}
	public synchronized void addLocationListener(LocationListener listener){
		
		locationListeners.add(listener);
	}
	public synchronized void removeLocationListener(LocationListener listener){
		
		locationListeners.remove(listener); 
	}
	public boolean hasLocationListenerSubscribed(LocationListener listener){
		return locationListeners.contains(listener);
	}
	
	@Override
	public synchronized void onLocationChanged(Location location) {
		this.location = location;
		storeLocation(location);
		for(LocationListener listener: locationListeners){
			listener.onLocationChanged(location);
		}
	}
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Toast.makeText(mContext, mContext.getString(R.string.connection_error),Toast.LENGTH_SHORT).show();
	}

	private Location getStoredLocation(){
		String storedLocation = mContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).getString(PREF, "");
		if(storedLocation.isEmpty()){
			return null;
		}
		return this.location = new Location(storedLocation);
	}
	private void storeLocation(Location location){
		mContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit().putString(PREF, location.toString()).commit();
	}

	public Location getLastLocation(){
		if(location != null)
			return location;
		else
			return getStoredLocation();
	}

	public void finish() {
		if(mLocationClient != null && mLocationClient.isConnected()){
			mLocationClient.removeLocationUpdates(this);
			mLocationClient.disconnect();
		}
		locationListeners = null;
		mLocationClient = null;
		mLocationRequest = null;
	}
}