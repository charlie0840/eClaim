package com.pingan_us.eclaim;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

public class FileClaim1Activity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener {

    protected Button next_btn, time_btn, btn;
    protected CheckBox injure_box, present_box, drivable_box;
    protected RelativeLayout injure_section, present_section, drivable_section;
    protected Spinner vehicle_spinner;
    protected RadioButton I_rbtn, other_rbtn;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private double longitude = 0, latitude = 0;
    public static final int  PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private boolean mLocationPermissionGranted = false;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileclaim1);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        next_btn = (Button) findViewById(R.id.start_step2_button);
        time_btn = (Button) findViewById(R.id.time_picker_btn);

        injure_box = (CheckBox) findViewById(R.id.injure_checkbox);
        present_box = (CheckBox) findViewById(R.id.atscene_checkbox);
        drivable_box = (CheckBox) findViewById(R.id.drivable_checkbox);

        injure_section = (RelativeLayout) findViewById(R.id.injure_section);
        present_section = (RelativeLayout) findViewById(R.id.atscene_section);
        drivable_section = (RelativeLayout) findViewById(R.id.drivable_section);

        vehicle_spinner = (Spinner) findViewById(R.id.vehicle_pick_spinner);

        I_rbtn = (RadioButton) findViewById(R.id.I_pick_radiobutton);
        other_rbtn = (RadioButton) findViewById(R.id.other_pick_radiobutton);

        next_btn.setOnClickListener(this);
        time_btn.setOnClickListener(this);
        I_rbtn.setOnClickListener(this);
        other_rbtn.setOnClickListener(this);
        injure_section.setOnClickListener(this);
        present_section.setOnClickListener(this);
        drivable_section.setOnClickListener(this);

        I_rbtn.setChecked(true);
        other_rbtn.setChecked(false);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.start_step2_button:
                Intent intent = new Intent(this, FileClaim2Activity.class);
                this.startActivity(intent);
                break;
            case R.id.time_picker_btn:
                showDateTimePicker();
                break;
            case R.id.drivable_section:
                if(!drivable_box.isChecked())
                    drivable_box.setChecked(true);
                else
                    drivable_box.setChecked(false);
                break;
            case R.id.injure_section:
                if (!injure_box.isChecked())
                    injure_box.setChecked(true);
                else
                    injure_box.setChecked(false);
                break;
            case R.id.atscene_section:
                if(!present_box.isChecked())
                    present_box.setChecked(true);
                else
                    present_box.setChecked(false);
                break;
            case R.id.I_pick_radiobutton:
                if(!other_rbtn.isChecked())
                    other_rbtn.setChecked(true);
                else
                    other_rbtn.setChecked(false);
                break;
            case R.id.other_pick_radiobutton:
                if(!I_rbtn.isChecked())
                    I_rbtn.setChecked(true);
                else
                    I_rbtn.setChecked(false);
                break;
        }
    }

    Calendar date;
    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(getApplicationContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng defLoc = new LatLng(0,0);
        CameraPosition googlePlex = CameraPosition.builder()
                 .target(defLoc)
                 .zoom(16)
                 .bearing(0)
                 .tilt(45)
                 .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
        map.setOnMarkerClickListener(this);
        map.setOnMarkerDragListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
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
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                //Getting longitude and latitude
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                //moving the map to location
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
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Marker in India"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setZoomControlsEnabled(true);


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
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(FileClaim1Activity.this, "onMarkerDragStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Toast.makeText(FileClaim1Activity.this, "onMarkerDrag", Toast.LENGTH_SHORT).show();
    }

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
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(FileClaim1Activity.this, "onMarkerClick", Toast.LENGTH_SHORT).show();
        return true;
    }


}
