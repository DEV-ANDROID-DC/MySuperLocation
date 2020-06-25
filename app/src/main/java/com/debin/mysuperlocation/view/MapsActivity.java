package com.debin.mysuperlocation.view;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.debin.mysuperlocation.R;
import com.debin.mysuperlocation.viewmodel.EnterAddressViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private final int PERMISSION_REQUEST = 101;
    private GoogleMap mMap;
    private LocationManager locationManager;
    //private Observer<Result> locationLatlong;
    private EnterAddressViewModel enterAddressViewModel;

    @BindView(R.id.location_textview)
    TextView locationTextView;

    @BindView(R.id.open_settings_button)
    Button settingsButton;

    @BindView(R.id.locate_imageview)
    ImageView locateImageView;

    @BindView(R.id.address_edittext)
    EditText addressEditText;

    @BindView(R.id.fab)
    FloatingActionButton directionFab;
    private static final String TAG = "MapsActivity";
    private LatLng currentLocation;
    private LatLng destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        Log.d(TAG, "onCreate");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        enterAddressViewModel = new ViewModelProvider(this).get(EnterAddressViewModel.class);

        locateImageView.setOnClickListener(v -> {
            if(!addressEditText.getText().toString().isEmpty()) {
                String address = addressEditText.getText().toString();
                enterAddressViewModel.getLatlong(address)
                        .observe(this, resultResponse -> {
                            locationTextView.setText("Latlong :: " + resultResponse.getResults().get(0).getGeometry().getLocation().getLat().toString()
                                    + "," + resultResponse.getResults().get(0).getGeometry().getLocation().getLng());
                            destination = new LatLng(resultResponse.getResults().get(0).getGeometry().getLocation().getLat(),
                                    resultResponse.getResults().get(0).getGeometry().getLocation().getLng());
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(destination).title(resultResponse.getResults().get(0).getFormattedAddress() + ""));
                        });
            }
        });

        directionFab.setOnClickListener(v -> {

        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        settingsButton.setOnClickListener(v -> {
            Intent settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri packageUri = Uri.fromParts("package", getPackageName(), "Permissions");
            settingIntent.setData(packageUri);
            startActivity(settingIntent);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        locationManager.removeUpdates(this);
    }

    private void requestLocationListenner() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000, 10, this
        );
    }

    @Override
    protected void onStart() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            requestLocationListenner();
        }
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {

                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationListenner();
                    settingsButton.setVisibility(View.GONE);

                } else {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                     requestLocationPermission();
                    } else {
                        locationTextView.setText(R.string.permission_requirement);
                        settingsButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String latLong = location.getLatitude() + "," + location.getLongitude();
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        enterAddressViewModel.getLocationDetails(latLong).observe(this, locationDetails -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currentLocation)
                    .title(locationDetails.getResults().get(0).getFormattedAddress() + ""));
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}