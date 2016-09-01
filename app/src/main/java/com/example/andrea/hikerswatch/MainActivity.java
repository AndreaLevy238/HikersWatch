package com.example.andrea.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private LocationManager locationManager;
    private String provider;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private TextView latNum;
    private TextView lngNum;
    private TextView accuracyNum;
    private TextView speedNum;
    private TextView bearingNum;
    private TextView addressText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set up numbers to be put in
        latNum = (TextView) findViewById(R.id.latitudeNumber);
        lngNum = (TextView) findViewById(R.id.longitudeNumber);
        accuracyNum = (TextView) findViewById(R.id.accuracyNumber);
        speedNum = (TextView) findViewById(R.id.speedNumber);
        bearingNum = (TextView) findViewById(R.id.bearingNumber);
        addressText = (TextView) findViewById(R.id.address);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        try{
            locationManager.requestLocationUpdates(provider, 0, 0, this);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                Log.i("Location Info", "Location achieved!");
                onLocationChanged(location);
            }
            else {
                Log.i("Location Info", "No location :(");
            }

        }
        catch (SecurityException e)
        {
            checkLocationPermission();
            e.printStackTrace();

        }

    }



    @Override
    public void onLocationChanged(Location location) {
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(lat, lng, 1);
            accuracyNum.setText(Float.toString(location.getAccuracy()) + "m");
            speedNum.setText(Float.toString(location.getSpeed()) + "m/s");
            bearingNum.setText(Float.toString(location.getBearing()));
            if(listAddresses != null && listAddresses.size() > 0)
            {
                Address address = listAddresses.get(0);
                Log.i("PlaceInfo", address.toString());
                latNum.setText(Double.toString(address.getLatitude()));
                lngNum.setText(Double.toString(address.getLongitude()));
                String theAddress = "Address: \n";
                for (int i=0; i<= address.getMaxAddressLineIndex(); i++)
                {
                    theAddress += address.getAddressLine(i) + "\n";
                }
                addressText.setText(theAddress);

            }
            else
            {
                latNum.setText(lat.toString());
                lngNum.setText(lng.toString());
                addressText.setText(R.string.addressNotAvailable);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //do nothing
    }

    @Override
    public void onProviderEnabled(String s) {
        //do nothing
    }

    @Override
    public void onProviderDisabled(String s) {
        // do nothing
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block this thread waiting for the user's response!
                // After the user sees the explanation, try again to request the permission.
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else {
            return true;
        }
    }
}
