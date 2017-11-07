package com.pingan_us.eclaim;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

public class FileClaim1Activity extends FragmentActivity implements View.OnClickListener, View.OnKeyListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static FileClaim1Activity activity;

    private Window w;

    private ClaimBundle claim = new ClaimBundle();

    protected Spinner vehicle_spinner, vehicle_num_spinner;
    protected RadioButton I_rbtn, other_rbtn;
    protected Button next_btn, cancel_btn, time_btn, set_btn, add_person_btn;
    protected CheckBox injure_box, present_box, drivable_box;


    private Calendar date;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private AlertDialog alertDialog;

    private GoogleMap mMap;
    private View fg,dialogView;
    private TextView loc_indicate_txt, loc_indicate_txt_1;
    private SupportMapFragment mapFragment;
    private EditText other_driver_phone_txt;
    private GoogleApiClient googleApiClient;
    private ImageView other_drive_pic, other_insur_pic, add_person_pic;
    private RelativeLayout injure_section, present_section, drivable_section, loc_section, background;

    private int vehicle_id;
    private long time;
    private ArrayAdapter<String> adapter;
    private List<byte[]> byteList = new ArrayList<byte[]>();
    private List<String> carIDList = new ArrayList<String>();
    private List<String> vehicleList = new ArrayList<String>();
    private String location_txt = "", time_txt = "", claim_id = "", phone_txt = "";
    private int drive_or_insur;
    private double longitude = 0, latitude = 0;
    private boolean mLocationPermissionGranted = false;
    private Bitmap p1 = null, p2 = null, p3 = null;
    private ParseFile f1 = null, f2 = null, f3 = null;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final int GET_LOC_REQUEST = 1, ACTIVITY_SELECT_IMAGE = 2, DRIVE = 1, INSUR = 2, ADD_PERSON = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileclaim1);

        activity = this;

        for(int i = 0; i < 3; i++)
            byteList.add(null);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        loc_indicate_txt = (TextView) findViewById(R.id.loc_indicate_text);
        loc_indicate_txt_1 = (TextView) findViewById(R.id.loc_indicate_text_1);
        other_driver_phone_txt = (EditText) findViewById(R.id.other_driver_phone);

        I_rbtn = (RadioButton) findViewById(R.id.I_pick_radiobutton);
        other_rbtn = (RadioButton) findViewById(R.id.other_pick_radiobutton);
        add_person_btn = (Button) findViewById(R.id.add_person_pick_button);
        next_btn = (Button) findViewById(R.id.start_step2_button);
        cancel_btn = (Button) findViewById(R.id.step1_cancel_button);
        time_btn = (Button) findViewById(R.id.time_picker_btn);

        other_drive_pic = (ImageView) findViewById(R.id.other_driver_license_pic);
        other_insur_pic = (ImageView) findViewById(R.id.other_insurance_card_pic);
        add_person_pic = (ImageView) findViewById(R.id.add_person_license_pic);

        injure_box = (CheckBox) findViewById(R.id.injure_checkbox);
        present_box = (CheckBox) findViewById(R.id.atscene_checkbox);
        drivable_box = (CheckBox) findViewById(R.id.drivable_checkbox);

        background = (RelativeLayout) findViewById(R.id.fc1_background);
        loc_section = (RelativeLayout) findViewById(R.id.loc_section);
        injure_section = (RelativeLayout) findViewById(R.id.injure_section);
        present_section = (RelativeLayout) findViewById(R.id.atscene_section);
        drivable_section = (RelativeLayout) findViewById(R.id.drivable_section);

        fg = (View) findViewById(R.id.map);

        if(ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        vehicleList = new ArrayList<String>();
        vehicle_spinner = (Spinner) findViewById(R.id.vehicle_pick_spinner);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vehicleList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicle_spinner.setAdapter(adapter);

        getVehicles();

        vehicle_num_spinner = (Spinner)findViewById(R.id.vehiclenum_spinner);

        add_person_pic.setVisibility(View.GONE);
        add_person_btn.setVisibility(View.GONE);

        other_driver_phone_txt.setOnKeyListener(this);

        next_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        I_rbtn.setOnClickListener(this);
        other_rbtn.setOnClickListener(this);
        time_btn.setOnClickListener(this);
        add_person_btn.setOnClickListener(this);

        injure_section.setOnClickListener(this);
        present_section.setOnClickListener(this);
        drivable_section.setOnClickListener(this);

        other_drive_pic.setOnClickListener(this);
        other_insur_pic.setOnClickListener(this);

        loc_indicate_txt.setOnClickListener(this);

        I_rbtn.setChecked(true);
        other_rbtn.setChecked(false);

        dialogView = View.inflate(this, R.layout.date_time_picker, null);
        alertDialog = new AlertDialog.Builder(this).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                time = calendar.getTimeInMillis();
                Date date = new Date(time);
                time_txt = date.toString();

                time_btn.setText(date.toString());
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        w = getWindow();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time_picker_btn:
                alertDialog.show();
                break;
            case R.id.start_step2_button:
                phone_txt = other_driver_phone_txt.getText().toString();
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if(phone_txt.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please input the phone number", Toast.LENGTH_LONG).show();
                    break;
                }
                if(time_txt.equals("")){
                    Toast.makeText(getApplicationContext(), "Please select the time", Toast.LENGTH_LONG).show();
                    break;
                }
                if(location_txt.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please select the location", Toast.LENGTH_LONG).show();
                    break;
                }
//                if(f1 == null && other_rbtn.isChecked()) {
//                    Toast.makeText(getApplicationContext(), "Please select picture for driver license", Toast.LENGTH_LONG).show();
//                    break;
//                }
//                if(f2 == null) {
//                    Toast.makeText(getApplicationContext(), "Please select picture for driver license of other driver", Toast.LENGTH_LONG).show();
//                    break;
//                }
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                uploadData();
                break;
            case R.id.step1_cancel_button:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
                break;
            case R.id.drivable_section:
                if (!drivable_box.isChecked())
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
                if (!present_box.isChecked())
                    present_box.setChecked(true);
                else
                    present_box.setChecked(false);
                break;
            case R.id.I_pick_radiobutton:
                add_person_btn.setVisibility(View.GONE);
                add_person_pic.setVisibility(View.GONE);
                byteList.set(0, null);
                if (other_rbtn.isChecked())
                    other_rbtn.setChecked(false);
                break;
            case R.id.other_pick_radiobutton:
                add_person_btn.setVisibility(View.VISIBLE);
                if (I_rbtn.isChecked())
                    I_rbtn.setChecked(false);
                break;
            case R.id.loc_indicate_text:
                Intent intent2 = new Intent(this, MapActivity.class);
                startActivityForResult(intent2, GET_LOC_REQUEST);
                break;
            case R.id.other_driver_license_pic:
                drive_or_insur = DRIVE;
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select File"), ACTIVITY_SELECT_IMAGE);
                break;
            case R.id.other_insurance_card_pic:
                drive_or_insur = INSUR;
                Intent i1 = new Intent();
                i1.setType("image/*");
                i1.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i1, "Select File"), ACTIVITY_SELECT_IMAGE);
                break;
            case R.id.add_person_pick_button:
                drive_or_insur = ADD_PERSON;
                Intent i2 = new Intent();
                i2.setType("image/*");
                i2.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i2, "Select File"), ACTIVITY_SELECT_IMAGE);
                break;
        }
    }

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
        mMap = map;
        map.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng defLoc = new LatLng(-13, 76);
        CameraPosition googlePlex = CameraPosition.builder()
                .target(defLoc)
                .zoom(16)
                .bearing(0)
                .tilt(45)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
        map.addMarker(new MarkerOptions().position(defLoc));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
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
        if(mMap == null){
            //Toast.makeText(getApplicationContext(), "map null!!!!", Toast.LENGTH_LONG).show();
            return;
        }
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(getApplicationContext(), "REQUEST CODE IS       " + requestCode, Toast.LENGTH_SHORT).show();
        if(requestCode == GET_LOC_REQUEST) {
            if(resultCode == RESULT_OK) {
                LatLng currLoc = null;
                Bundle bundle = data.getParcelableExtra("bundle");
                currLoc = (LatLng) bundle.getParcelable("LatLng");
                longitude = currLoc.longitude;
                latitude = currLoc.latitude;
                location_txt = getAddress(latitude, longitude);
                loc_indicate_txt_1.setText(location_txt);
                moveMap();
            }
        }
        else if(requestCode == ACTIVITY_SELECT_IMAGE) {
            if(resultCode == RESULT_OK){
                Bitmap bm=null;
                if (data != null) {
                        bm = Utility.compressImageUri(data.getData(), 1024, 768, getApplicationContext());
                        //MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    if(bm == null) {
                        Toast.makeText(getApplicationContext(), "Wrong !!!!!!!", Toast.LENGTH_LONG).show();
                    }
                }
                if(drive_or_insur == DRIVE) {
                    if(bm == null) {
                        Toast.makeText(getApplicationContext(), "Wrong !!!!!!!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        p2 = Bitmap.createScaledBitmap(bm, bm.getWidth(), bm.getHeight(), true);
                        uploadImg(p2, 2);
                        Bitmap yourSelectedImage = Bitmap.createScaledBitmap(bm, bm.getWidth() / 2, bm.getHeight() / 2, true);
                        other_drive_pic.setImageBitmap(yourSelectedImage);
                    }
                }
                else if(drive_or_insur == INSUR) {
                    p3 = Bitmap.createScaledBitmap(bm, bm.getWidth() / 2, bm.getHeight() / 2, true);
                    uploadImg(p3, 3);
                    Bitmap yourSelectedImage = Bitmap.createScaledBitmap(bm, bm.getWidth()/2, bm.getHeight()/2, true);
                    other_insur_pic.setImageBitmap(yourSelectedImage);
                }
                else {
                    p1 = Bitmap.createScaledBitmap(bm, bm.getWidth(), bm.getHeight(), true);
                    uploadImg(p1, 1);
                    Bitmap yourSelectedImage = Bitmap.createScaledBitmap(bm, bm.getWidth()/2, bm.getHeight()/2, true);
                    add_person_pic.setVisibility(View.VISIBLE);
                    add_person_pic.setImageBitmap(yourSelectedImage);
                }
            }
        }
    }

    public String getAddress(double lat, double lng) {
        String retStr = "";
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if(addresses.size() == 0)
                return "No location selected, please click here to pick location";
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();
            retStr = add;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return retStr;
    }

    public static FileClaim1Activity getInstance() {
        return activity;
    }

    public void uploadData() {//final Bitmap bmp) {
//        Toast.makeText(getApplicationContext(), "uploading Data!!!", Toast.LENGTH_LONG).show();
//        final ParseUser currUser = ParseUser.getCurrentUser();
//        final ParseObject Claim = new ParseObject("Claim");
//
//        Claim.put("injured", injure_box.isChecked());
//        Claim.put("drivable", drivable_box.isChecked());
//        Claim.put("atScene", present_box.isChecked());
//        Claim.put("vehicleNum", vehicle_num_spinner.getSelectedItem().toString());
//        Claim.put("time", time_txt);
//        Claim.put("person", I_rbtn.isChecked());
//        Claim.put("location", location_txt);
//        Claim.put("vehicleID", vehicle_id);
//        Claim.put("phoneOther", phone_txt);
//        if(f1 != null)
//            Claim.put("driverLicense", f1);
//        Claim.put("otherLicense", f2);
//        Claim.put("otherInsurance", f3);
//
//        Claim.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if(e == null) {
//                    Toast.makeText(getApplicationContext(), "claim uploading all good!!!!!!!", Toast.LENGTH_LONG).show();
//                    final String objectID = Claim.getObjectId();
//                    List<String> claimList = new ArrayList<String>();
//                    if(currUser.get("claimID") != null) {
//                        try {
//                            claimList = new ArrayList<String>((List<String>) currUser.get("claimID"));
//                        }
//                        catch (ClassCastException e1) {
//                        }
//                    }
//                    claimList.add(objectID);
//                    currUser.put("claimID", claimList);
//                    currUser.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if(e == null) {
//                                Toast.makeText(getApplicationContext(), "user update all good!!!!!!", Toast.LENGTH_LONG).show();
//                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                Intent intent = new Intent(getApplicationContext(), FileClaim2Activity.class);
//                                intent.putExtra("claimID", objectID);
//                                startActivity(intent);
//                            }
//                            else{
//                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Claim");
//                                query.whereEqualTo("objectID", objectID);
//                                query.getInBackground(objectID, new GetCallback<ParseObject>() {
//                                    public void done(ParseObject object, ParseException e) {
//                                        if (e == null) {
//                                            object.deleteInBackground();
//                                        }
//                                    }
//                                });
//                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                Toast.makeText(getApplicationContext(), "user update went wrong!!! " + e.toString(), Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//                }
//                else {
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                    Toast.makeText(getApplicationContext(), "claim uploading went wrong!!! " + e.toString(), Toast.LENGTH_LONG).show();
//                }
//            }
//        });
        claim.setStep1Bundle(injure_box.isChecked(), drivable_box.isChecked(), present_box.isChecked(), I_rbtn.isChecked(),
                time_txt, location_txt, vehicle_spinner.getSelectedItem().toString(), vehicle_num_spinner.getSelectedItem().toString(), phone_txt);
        background.setAlpha((float) 0.5);
        claim.uploadStep1Image(byteList, w, getApplicationContext(), background);

        //       Intent intent = new Intent(getApplicationContext(), FileClaim2Activity.class);
   //     intent.putExtra("ClaimBundle", claim);
     //   startActivity(intent);
    }

    private void uploadImg(final Bitmap bmp, final int action) {
        if(bmp == null) {
            Toast.makeText(getApplicationContext(), "Please select picture of insurance card of other driver", Toast.LENGTH_LONG).show();
            return;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] byteArray = stream.toByteArray();
        if(action == 1)
            byteList.add(0, byteArray);
        else if(action == 2)
            byteList.add(1, byteArray);
        else if(action == 3)
            byteList.add(2, byteArray);
    }

    @Override
    public void onBackPressed(){}

    public void getVehicles() {
        ParseUser user = ParseUser.getCurrentUser();
        final List<String> strList = new ArrayList<String>();
        List<String> vehicleIDList = new ArrayList<String>();

        if(user.get("vehicleID") != null)
            vehicleIDList = new ArrayList<String>((List<String>)user.get("vehicleID"));

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Vehicle");
        query.whereContainedIn("objectId", vehicleIDList);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    if(objects != null) {
                        for(ParseObject object:objects) {
                            String name = (String) object.get("modelMake");
                            vehicleList.add(name);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
