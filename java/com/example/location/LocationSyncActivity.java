package com.example.location;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.Logueo.MainActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
 
public class LocationSyncActivity extends Activity implements LocationListener {
    private LocationManager locationManager;
    //CONFIG PARAMETERS
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = LocationManager.getInstance(this);
        locationManager.setLocation(LocationSyncActivity.this);

    }
 
    @Override
    protected void onStart() {
        super.onStart();
        if(!locationManager.hasLocationUpdatesEnabled())
        	locationManager.setStartUpdates(LocationRequest.PRIORITY_HIGH_ACCURACY, 
        			LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS, LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS,
        			LocationUtils.SMALLEST_DISPLACEMENT);
            //locationManager.setStartUpdates(LocationService.PRIORITY, LocationService.INTERVAL, LocationService.FASTEST_INTERVAL,LocationService.SMALLEST_DISPLACEMENT);
        
    }
 
    @Override
    protected void onResume() { 
        super.onResume();
        locationManager.addLocationListener(this);
    }
 
    @Override
    protected void onPause() { 
        super.onPause();
        locationManager.removeLocationListener(this);
    }
 
    @Override
    public void onLocationChanged(Location location) {
    	
    }
}
