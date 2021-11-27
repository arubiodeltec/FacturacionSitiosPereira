package com.example.location;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

public class LocationActivity extends Activity
implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener{
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	private boolean firstStarted = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocationClient = new LocationClient(this,this,this);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		mLocationRequest.setInterval(180000);
		mLocationRequest.setSmallestDisplacement(5);
		mLocationRequest.setFastestInterval(30000);

	}

	@Override
	protected void onStart() {
		super.onStart();
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS)
			mLocationClient.connect();
	}

	@Override
	public void onResume(){
		super.onResume();
		if(!firstStarted && mLocationClient.isConnected()){
			startUpdates();
		}
	}

	@Override
	public void onPause(){
		super.onPause();
		mLocationClient.removeLocationUpdates(this);		
	}

	@Override
	protected void onStop() {
		if(mLocationClient.isConnected())
			mLocationClient.disconnect();
		firstStarted = true;
		super.onStop();
	}

	@Override
	public void onConnected(Bundle bundle) {
		startUpdates();
		firstStarted = false;
	}

	private void startUpdates(){
		mLocationClient.requestLocationUpdates(mLocationRequest,this);
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
	}
}
