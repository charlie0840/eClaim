package com.pingan_us.eclaim;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

public class MapActivity extends FragmentActivity implements View.OnClickListener, View.OnKeyListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener {

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private ImageButton btn;
    private EditText t;
    private Button search_btn, confirm_btn;
    private SupportMapFragment mapFragment;

    private boolean mLocationPermissionGranted = false;
    private double longitude = 0, latitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_test);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        t = (EditText) findViewById(R.id.loc_search_text);

        t.setOnKeyListener(this);

        btn = (ImageButton) findViewById(R.id.pinLoc_btn);
        search_btn = (Button) findViewById(R.id.loc_search_btn);
        confirm_btn = (Button) findViewById(R.id.loc_confirm_button);

        btn.setOnClickListener(this);
        search_btn.setOnClickListener(this);
        confirm_btn.setOnClickListener(this);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getCurrentLocation();
            }
        }, 500);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.loc_search_btn:
                onSearch();
                break;
            case R.id.pinLoc_btn:
                getCurrentLocation();
                break;
            case R.id.loc_confirm_button:
                LatLng finalLoc = new LatLng(latitude, longitude);
                Bundle bundle = new Bundle();
                bundle.putParcelable("LatLng", finalLoc);
                Intent intent = new Intent();
                intent.putExtra("bundle", bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            switch(keyCode) {
                case KeyEvent.KEYCODE_ENTER:
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    View view = this.getCurrentFocus();
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
            }
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng defLoc = new LatLng(34, -118);
        CameraPosition googlePlex = CameraPosition.builder()
                .target(defLoc)
                .zoom(12)
                .bearing(0)
                .tilt(45)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
        map.setOnMarkerClickListener(this);
        map.setOnMarkerDragListener(this);
        getCurrentLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getCurrentLocation();
                }
                else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        mMap.clear();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            if(googleApiClient != null)
                googleApiClient.connect();
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                //Getting longitude and latitude
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                //moving the map to location
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).draggable(true));
                moveMap();
            }

        }
    }

    private void moveMap() {
        /**
         * Creating the latlng object to store lat, long coordinates
         * adding marker to map
         * move the camera with animation
         */
        LatLng latLng = new LatLng(latitude, longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    public void onSearch() {
        String locStr = t.getText().toString();
        List<Address> addressList = null;
        if(locStr != null || !locStr.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(locStr, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(addressList.size() == 0)
                Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT).show();
            else {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                longitude = latLng.longitude;
                latitude = latLng.latitude;
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // mMap.clear();
        //mMap.clear();
        //mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // getting the Co-ordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //move to current position
        moveMap();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }
}

