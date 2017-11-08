package com.pingan_us.eclaim;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.images.WebImage;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Charlie0840 on 10/14/2017.
 */

public class ProfileActivity extends Activity implements View.OnClickListener {

    private Animation animationLOut, animationLIn;

    private View nav_bar;
    private CircleImageView profile_photo;
    private TextView name_text, phone_text, vehicle_text;
    private ImageView switcherImageView, switcherImageView2;
    private Button edit_btn, add_btn, delete_btn, logout_btn;
    private ImageSwitcher IDImageSwitcher, VehicleImageSwitcher;
    private RelativeLayout id_section, vehicle_section, add_id, add_vehicle;

    private int counter, ID_or_Vehicle;
    private String userChoosenTask, phone_no, full_name, vehicleName;
    private List<Drawable> IDPicList, vehiclePicList;
    private byte[] imageByte = null, IDCardByte = null;
    private List<String> carList = new ArrayList<String>(), carIDList = new ArrayList<String>();
    private static final int REQUEST_CAMERA = 0, SELECT_FILE = 1, MY_CAMERA_REQUEST_CODE = 1, MY_CALL_REQUEST_CODE = 2,
            PROFILE_PHOTO = 3, ID = 0, VEHICLE = 1;

    private static ProfileActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        IDPicList = new ArrayList<Drawable>();
        vehiclePicList = new ArrayList<Drawable>();

        vehiclePicList.add(getResources().getDrawable(R.drawable.add));

        nav_bar = findViewById(R.id.nav_layout);

        profile_photo = (CircleImageView) findViewById(R.id.profile_photo);
        Bitmap icon1 = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.add);
        Drawable p1 = new BitmapDrawable(icon1);
        IDPicList.add(p1);
        vehiclePicList.add(p1);


        ImageView home_nav = (ImageView) nav_bar.findViewById(R.id.home_nav);
        ImageView claim_nav = (ImageView) nav_bar.findViewById(R.id.claims_nav);
        home_nav.setOnClickListener(this);
        claim_nav.setOnClickListener(this);

        name_text = (TextView)findViewById(R.id.pro_name_text);
        phone_text = (TextView)findViewById(R.id.pro_phone_text);
        vehicle_text = (TextView)findViewById(R.id.profile_vehicle_name_text);
        phone_text.setText(phone_no);
        name_text.setText(full_name);

        IDImageSwitcher = (ImageSwitcher) findViewById(R.id.ID_switch);
        VehicleImageSwitcher = (ImageSwitcher) findViewById(R.id.vehicle_switch);

        add_id = (RelativeLayout) findViewById(R.id.id_section);
        add_vehicle = (RelativeLayout) findViewById(R.id.vehicle_section);
        id_section = (RelativeLayout) findViewById(R.id.entire_id_section);
        vehicle_section = (RelativeLayout) findViewById(R.id.entire_vehicle_section);

        edit_btn = (Button) findViewById(R.id.id_add_button);
        logout_btn = (Button) findViewById(R.id.profile_logout_button);
        add_btn = (Button) findViewById(R.id.profile_add_vehicle_button);
        delete_btn = (Button) findViewById(R.id.profile_delete_vehicle_button);

        edit_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);
        add_id.setOnClickListener(this);
        add_vehicle.setOnClickListener(this);
        logout_btn.setOnClickListener(this);
        profile_photo.setOnClickListener(this);

        IDImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                switcherImageView = new ImageView(getApplicationContext());
                switcherImageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT
                ));
                switcherImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                switcherImageView.setImageDrawable(IDPicList.get(0));
                return switcherImageView;
            }
        });

        VehicleImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                switcherImageView2 = new ImageView(getApplicationContext());
                switcherImageView2.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT
                ));
                switcherImageView2.setScaleType(ImageView.ScaleType.FIT_CENTER);
                switcherImageView2.setImageDrawable(vehiclePicList.get(0));
                return switcherImageView2;
            }
        });

        animationLOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animationLIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        IDImageSwitcher.setOutAnimation(animationLOut);
        IDImageSwitcher.setInAnimation(animationLIn);
        VehicleImageSwitcher.setOutAnimation(animationLOut);
        VehicleImageSwitcher.setInAnimation(animationLIn);

        id_section.setOnTouchListener(new OnSwipeTouchListener(getBaseContext()) {
            int switcherImage = 0;

            @Override
            public void onClick() {
                Toast.makeText(getApplicationContext(), "Click!", Toast.LENGTH_LONG).show();
                switcherImage = IDPicList.size();
                counter++;
                if (counter == switcherImage)
                    counter = 0;
                IDImageSwitcher.setImageDrawable(IDPicList.get(counter));
            }

            @Override
            public void onSwipeRight() {
                switcherImage = IDPicList.size();
                counter++;
                if (counter == switcherImage)
                    counter = 0;
                IDImageSwitcher.setImageDrawable(IDPicList.get(counter));
            }

            @Override
            public void onSwipeLeft() {
                switcherImage = IDPicList.size();
                //Toast.makeText(getApplicationContext(), "left touched with size " + switcherImage, Toast.LENGTH_LONG).show();
                counter--;
                if (counter == -1)
                    counter = switcherImage - 1;
                IDImageSwitcher.setImageDrawable(IDPicList.get(counter));
            }
        });

        vehicle_section.setOnTouchListener(new OnSwipeTouchListener(getBaseContext()) {
            int switcherImage = 0;

            @Override
            public void onClick() {
                Toast.makeText(getApplicationContext(), "Click!", Toast.LENGTH_LONG).show();
                switcherImage = IDPicList.size();
                counter++;
                if (counter == switcherImage)
                    counter = 0;
                VehicleImageSwitcher.setImageDrawable(vehiclePicList.get(counter));
            }

            @Override
            public void onSwipeRight() {
                switcherImage = vehiclePicList.size();
                counter++;
                if (counter == switcherImage)
                    counter = 0;
                VehicleImageSwitcher.setImageDrawable(vehiclePicList.get(counter));
                vehicle_text.setText(carList.get(counter));
            }

            @Override
            public void onSwipeLeft() {
                switcherImage = vehiclePicList.size();
                //Toast.makeText(getApplicationContext(), "left touched", Toast.LENGTH_LONG).show();
                counter--;
                if (counter == -1)
                    counter = switcherImage - 1;
                VehicleImageSwitcher.setImageDrawable(vehiclePicList.get(counter));
                vehicle_text.setText(carList.get(counter));

            }
        });

        activity = this;
    }

    @TargetApi(23)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_add_button:
                ID_or_Vehicle = ID;
                //Toast.makeText(getApplicationContext(), "add pic!", Toast.LENGTH_SHORT).show();
                selectImage();
                break;
            case R.id.profile_add_vehicle_button:
                ID_or_Vehicle = VEHICLE;
                //Toast.makeText(getApplicationContext(), "add pic!", Toast.LENGTH_SHORT).show();
                final CharSequence[] items = { "Confirm",
                        "Cancel" };
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

                LayoutInflater inflater=ProfileActivity.this.getLayoutInflater();
                //this is what I did to added the layout to the alert dialog
                View layout=inflater.inflate(R.layout.popup_window,null);
                builder.setView(layout);

                builder.setTitle("Add Vehicle");
                final EditText editText = (EditText) layout.findViewById(R.id.editTextDialogUserInput);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result=Utility.checkPermission(ProfileActivity.this);
                        if(!result)
                            Toast.makeText(getApplicationContext(), "no permission!!!!", Toast.LENGTH_LONG).show();
                        if (items[item].equals("Confirm")) {
                            vehicleName = editText.getText().toString();
                            dialog.dismiss();
                            selectImage();
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                //selectImage();
                break;
            case R.id.profile_delete_vehicle_button:
                deleteVehicle();
                break;
            case R.id.profile_photo:
                ID_or_Vehicle = PROFILE_PHOTO;
                selectImage();
                break;
            case R.id.home_nav:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.claims_nav:
                Intent intent1 = new Intent(this, ViewClaimt.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.profile_logout_button:
                ParseUser currUser = ParseUser.getCurrentUser();
                currUser.logOut();
                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class); //fixed
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(ProfileActivity.this);
                if (!result)
                    Toast.makeText(getApplicationContext(), "no permission!!!!", Toast.LENGTH_LONG).show();
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @TargetApi(23)
    private void cameraIntent() {
        Toast.makeText(this, "camera!!!!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager pm = getApplicationContext().getPackageManager();
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        } else {
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                startActivityForResult(intent, REQUEST_CAMERA);
            else
                Toast.makeText(getApplicationContext(), "No camera detected", Toast.LENGTH_LONG).show();
        }
    }

    private void galleryIntent() {
        //Toast.makeText(this, "gallery!!!!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
                bm = Bitmap.createBitmap(Utility.compressImageUri(data.getData(), 1024, 768, getApplicationContext()));
                //MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
        }
        Bitmap resBitmap = Bitmap.createScaledBitmap(bm, bm.getWidth(), bm.getHeight(), true);
        Drawable d = new BitmapDrawable(getResources(), resBitmap);
        addToList(d, resBitmap);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap resBitmap = Bitmap.createScaledBitmap(thumbnail, thumbnail.getWidth(), thumbnail.getHeight(), true);
        Drawable d = new BitmapDrawable(getResources(), resBitmap);
        addToList(d, resBitmap);

    }

    public void addToList(Drawable pic, Bitmap bmp) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (ID_or_Vehicle == ID) {
            IDPicList.set(0, pic);
            uploadImg(bmp);
        } else if (ID_or_Vehicle == VEHICLE){
            vehiclePicList.add(pic);
            carList.add(vehicleName);
            switcherImageView2.setImageDrawable(pic);
            vehicle_text.setText(vehicleName);
            counter = carList.size() - 1;
            uploadImg(bmp);
        }
        else {
            profile_photo.setImageDrawable(pic);
            uploadImg(bmp);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                PackageManager pm = getApplicationContext().getPackageManager();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    startActivityForResult(intent, REQUEST_CAMERA);
                else
                    Toast.makeText(getApplicationContext(), "No camera detected", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode == MY_CALL_REQUEST_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:123456789"));
                if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                    startActivity(callIntent);
            }
            else
                Toast.makeText(this, "Calling permission denied", Toast.LENGTH_LONG).show();
        }
    }

    public void getData() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        full_name = (String)currentUser.get("lastName") + "," + (String)currentUser.get("firstName");
        phone_no = (String)currentUser.get("phoneNo");

        ParseFile IDCard = null;
        ParseFile bmp = null;
        try {
            bmp = currentUser.getParseFile("idImage");
            IDCard = currentUser.getParseFile("IDCard");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            imageByte = bmp.getData();
            if(IDCard != null) {
                IDCardByte = IDCard.getData();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static ProfileActivity getInstance() {
        return activity;
    }

    private void uploadImg(final Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //Bitmap bmp1= Bitmap.createBitmap(Utility.compressImage(bmp, 1024, 768, getApplicationContext(), false));
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        final byte[] byteArray = stream.toByteArray();

        final ParseFile file;
        if(ID_or_Vehicle == ID) {
            file = new ParseFile("IDCard", byteArray);
        }
        else {
            file = new ParseFile("imageID", byteArray);
        }
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    uploadData(file);
                    getVehicleList();
                }
                else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getApplicationContext(), "pic uploading went wrong!!! " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void uploadData(ParseFile file) {
        final ParseUser currUser = ParseUser.getCurrentUser();
        if(ID_or_Vehicle == VEHICLE) {
            final ParseObject object = new ParseObject("Vehicle");
            object.put("image", file);
            object.put("modelMake", vehicleName );
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        final String objectID = object.getObjectId();
                        List<String> idList = new ArrayList<String>();
                        if(currUser.get("vehicleID") != null) {
                            try {
                                idList = new ArrayList<String>((List<String>) currUser.get("vehicleID"));
                            }
                            catch (ClassCastException e1) {
                            }
                        }
                        idList.add(objectID);
                        carIDList.add(objectID);
                        currUser.put("vehicleID", idList);
                        currUser.saveInBackground();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            });

        }
        else if(ID_or_Vehicle == ID) {
            currUser.put("IDCard", file);
            currUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
        }
        else {
            currUser.put("idImage", file);
            currUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
        }
    }

    public void getVehicleList() {
        ParseUser currUser = ParseUser.getCurrentUser();
        vehiclePicList = new ArrayList<Drawable>();
        if(currUser.get("vehicleID") != null) {
            try {
                carIDList = new ArrayList<String>((List<String>) currUser.get("vehicleID"));
            }
            catch (ClassCastException e) {
            }
        }
        vehiclePicList.clear();
        getBitmapList(carIDList);
    }

    public void getBitmapList(List<String> list) {
        if(list.size() == 0) {
            vehiclePicList.add(getResources().getDrawable(R.drawable.add));
            carList.add("Add a vehicle");
            return;
        }
        int i = 0;
        while(i < list.size()) {
            String currStr = list.get(i);
            i++;
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vehicle");
            query.whereEqualTo("objectId", currStr);
            try {
                ParseObject object = query.getFirst();
                ParseFile file = (ParseFile) object.get("image");
                String name = (String) object.get("modelMake");
                byte[] byteArray = new byte[0];
                try {
                    byteArray = file.getData();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 640, 480, false);
                Drawable d = new BitmapDrawable(getResources(), scaledBmp);
                bmp.recycle();
                vehiclePicList.add(d);
                carList.add(name);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d("DEBUG!!!!!", "while " + currStr + " " + e.toString());
            }
        }
        if(vehiclePicList.get(0) != null)
            switcherImageView2.setImageDrawable(vehiclePicList.get(0));
    }

    public void deleteVehicle() {
        if(carList.size() == 0) {
            return;
        }
        carList.remove(counter);
        vehiclePicList.remove(counter);
        String id = carIDList.get(counter);
        carIDList.remove(counter);
        counter = 0;
        if(vehiclePicList.size() != 0) {
            switcherImageView2.setImageDrawable(vehiclePicList.get(0));
            vehicle_text.setText(carList.get(0));
        }
        else {
            switcherImageView2.setImageDrawable(getResources().getDrawable(R.drawable.add));
            vehicle_text.setText("No vehicle");
        }
        ParseUser currUser = ParseUser.getCurrentUser();
        currUser.put("vehicleID", carIDList);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Vehicle");
        query.whereEqualTo("objectId", id);
        try {
            ParseObject obj = query.getFirst();
            obj.delete();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("DEBUG!!!!!", "delete " + e.toString());
        }
    }

    public void loadContent() {
        getData();
        getVehicleList();
        Bitmap profileImage = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        profileImage = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, options);
        if(IDCardByte != null) {
            IDPicList.clear();
            BitmapFactory.Options options1 = new BitmapFactory.Options();
            Bitmap cardBmp = BitmapFactory.decodeByteArray(IDCardByte, 0, IDCardByte.length, options1);
            Drawable d = new BitmapDrawable(getResources(), cardBmp);
            IDPicList.add(d);
            switcherImageView.setImageDrawable(d);
        }
        profile_photo.setImageBitmap(profileImage);
    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onResume() {
        super.onResume();
        loadContent();
        vehicle_text.setText(carList.get(0));
        /*
        MotionEvent event = MotionEvent.obtain(0.1, 0.1, ,
                x, y, pressure, size,
                metaState, xPrecision, yPrecision,
                deviceId, edgeFlags);
        onTouchEvent(event);*/
        switcherImageView.performClick();
        switcherImageView2.performClick();
    }
}
