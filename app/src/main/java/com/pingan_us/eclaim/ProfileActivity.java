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
    private ImageSwitcher IDImageSwitcher, VehicleImageSwitcher;
    private LinearLayout photo_section;

    private RelativeLayout info_section, claim_section, phone_section, id_section, vehicle_section, add_id, add_vehicle;
    private Button view_doc_btn, file_claim_btn, claim_btn;
    private ImageButton phone_btn;

    private Animation animationLOut, animationLIn;

    private TextView name_text, phone_text;
    private CircleImageView profile_photo;
    private ImageView switcherImageView, switcherImageView2;
    private static final int REQUEST_CAMERA = 0, SELECT_FILE = 1, MY_CAMERA_REQUEST_CODE = 1, MY_CALL_REQUEST_CODE = 2, PROFILE_PHOTO = 3, ID = 0, VEHICLE = 1;
    private List<Drawable> IDPicList, vehiclePicList;
    private ArrayList<String> carList;
    private int counter, ID_or_Vehicle;
    private byte[] imageByte = null;
    private String userChoosenTask, claimID, vehicleID, user_name, phone_no, full_name, vehicleName;
    private static ProfileActivity activity;
    private View nav_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getData();

        Bitmap profileImage = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        profileImage = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, options);

        IDPicList = new ArrayList<Drawable>();
        vehiclePicList = new ArrayList<Drawable>();

        Bitmap icon1 = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.addphoto);
        Drawable p1 = new BitmapDrawable(icon1);

        IDPicList.add(p1);

        getVehicleList();

        vehiclePicList.add(p1);

        nav_bar = findViewById(R.id.nav_layout);

        profile_photo = (CircleImageView) findViewById(R.id.profile_photo);
        profile_photo.setImageBitmap(profileImage);

        ImageView home_nav = (ImageView) nav_bar.findViewById(R.id.home_nav);
        ImageView claim_nav = (ImageView) nav_bar.findViewById(R.id.claims_nav);
        home_nav.setOnClickListener(this);
        claim_nav.setOnClickListener(this);

        name_text = (TextView)findViewById(R.id.pro_name_text);
        phone_text = (TextView)findViewById(R.id.pro_phone_text);
        phone_text.setText(phone_no);
        name_text.setText(full_name);

        IDImageSwitcher = (ImageSwitcher) findViewById(R.id.ID_switch);
        VehicleImageSwitcher = (ImageSwitcher) findViewById(R.id.vehicle_switch);

        photo_section = (LinearLayout) findViewById(R.id.second_layer);
        info_section = (RelativeLayout) findViewById(R.id.first_layer);
        claim_section = (RelativeLayout) findViewById(R.id.third_layer);
        phone_section = (RelativeLayout) findViewById(R.id.fourth_layout);
        id_section = (RelativeLayout) findViewById(R.id.entire_id_section);
        vehicle_section = (RelativeLayout) findViewById(R.id.entire_vehicle_section);
        add_id = (RelativeLayout) findViewById(R.id.id_section);
        add_vehicle = (RelativeLayout) findViewById(R.id.vehicle_section);

        claim_btn = (Button) findViewById(R.id.claim_button);
        view_doc_btn = (Button) findViewById(R.id.claim_button);
        file_claim_btn = (Button) findViewById(R.id.file_claim_button);
        phone_btn = (ImageButton) findViewById(R.id.assistance_phone_button);

        claim_btn.setOnClickListener(this);
        add_id.setOnClickListener(this);
        add_vehicle.setOnClickListener(this);
        phone_btn.setOnClickListener(this);
        file_claim_btn.setOnClickListener(this);
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
        //addImageForSwitcher(IDImageSwitcher);

        animationLOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animationLIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        IDImageSwitcher.setOutAnimation(animationLOut);
        IDImageSwitcher.setInAnimation(animationLIn);
        VehicleImageSwitcher.setOutAnimation(animationLOut);
        VehicleImageSwitcher.setInAnimation(animationLIn);

        //准备把左右滑动加上

        //Toast.makeText(getApplicationContext(), "touched", Toast.LENGTH_LONG).show();
        id_section.setOnTouchListener(new OnSwipeTouchListener(getBaseContext()) {
            int switcherImage = 0;

            @Override
            public void onSwipeRight() {
                switcherImage = IDPicList.size();
                //Toast.makeText(getApplicationContext(), "right touched", Toast.LENGTH_LONG).show();
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
            public void onSwipeRight() {
                switcherImage = vehiclePicList.size();
                //Toast.makeText(getApplicationContext(), "right touched with size " + switcherImage, Toast.LENGTH_LONG).show();
                counter++;
                if (counter == switcherImage)
                    counter = 0;
                VehicleImageSwitcher.setImageDrawable(vehiclePicList.get(counter));
            }

            @Override
            public void onSwipeLeft() {
                switcherImage = vehiclePicList.size();
                //Toast.makeText(getApplicationContext(), "left touched", Toast.LENGTH_LONG).show();
                counter--;
                if (counter == -1)
                    counter = switcherImage - 1;
                VehicleImageSwitcher.setImageDrawable(vehiclePicList.get(counter));
            }
        });

        activity = this;
    }

    @TargetApi(23)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_section:
                ID_or_Vehicle = ID;
                //Toast.makeText(getApplicationContext(), "add pic!", Toast.LENGTH_SHORT).show();
                selectImage();
                break;
            case R.id.vehicle_section:
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
            case R.id.profile_photo:
                ID_or_Vehicle = PROFILE_PHOTO;
                selectImage();
                break;
            case R.id.home_nav:
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.claims_nav:
                Intent intent1 = new Intent(this, ViewClaimt.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.assistance_phone_button:
                if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, MY_CALL_REQUEST_CODE);
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:123456789"));
                    startActivity(callIntent);
                }
                break;
            case R.id.file_claim_button:
                Intent intent2 = new Intent(getApplicationContext(), FileClaim1Activity.class); //fixed
                intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent2);
                break;
            case R.id.claim_button:
                Intent intent3 = new Intent(this, ViewClaimt.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
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

        //Toast.makeText(getApplicationContext(), "add to list!", Toast.LENGTH_SHORT).show();
        if (ID_or_Vehicle == ID) {
            IDPicList.add(pic);
            //Toast.makeText(getApplicationContext(), "id size " + IDPicList.size(), Toast.LENGTH_SHORT).show();
        } else if (ID_or_Vehicle == VEHICLE){
            //vehiclePicList.add(pic);
            uploadImg(bmp);
            //Toast.makeText(getApplicationContext(), "vehicle size " + vehiclePicList.size(), Toast.LENGTH_SHORT).show();
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
        try {
            if (ParseUser.getCurrentUser() == null) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        } catch ( NullPointerException e) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        }
        ParseUser currentUser = ParseUser.getCurrentUser();
        final Bundle bundle = new Bundle();

        full_name = (String)currentUser.get("lastName") + "," + (String)currentUser.get("firstName");
        phone_no = (String)currentUser.get("phoneNo");

        ParseFile bmp = null;
        try {
            bmp = currentUser.getParseFile("idImage");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            imageByte = bmp.getData();
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
        final ParseFile file = new ParseFile("imageID", byteArray);
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
                        List<String> carList = new ArrayList<String>();
                        if(currUser.get("vehicleID") != null) {
                            try {
                                carList = new ArrayList<String>((List<String>) currUser.get("vehicleID"));
                            }
                            catch (ClassCastException e1) {
                            }
                        }
                        carList.add(objectID);
                        currUser.put("vehicleID", carList);
                        currUser.saveInBackground();
                    }
                }
            });

        }
        else {
            currUser.put("idImage", file);
            currUser.saveInBackground();
        }
    }

    public void getVehicleList() {
        ParseUser currUser = ParseUser.getCurrentUser();
        vehiclePicList = new ArrayList<Drawable>();
        List<String> vehicleList = new ArrayList<String>();
        List<Bitmap> picList = new ArrayList<Bitmap>();

        if(currUser.get("vehicleID") != null) {
            try {
                vehicleList = new ArrayList<String>((List<String>) currUser.get("vehicleID"));
            }
            catch (ClassCastException e) {
            }
        }
        vehiclePicList.clear();
        getBitmapList(vehicleList);
    }

    public void getBitmapList(List<String> list) {
        if(list.size() == 0) {
            Bitmap icon1 = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.addphoto);
            Drawable p1 = new BitmapDrawable(icon1);
            vehiclePicList.add(p1);
        }
        while(!list.isEmpty()) {
            String currStr = list.get(0);
            list.remove(0);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vehicle");
            query.whereEqualTo("objectId", currStr);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e == null) {
                        ParseFile file = (ParseFile) object.get("image");
                        byte[] byteArray = new byte[0];
                        try {
                            byteArray = file.getData();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        Drawable d = new BitmapDrawable(getResources(), bmp);
                        vehiclePicList.add(d);
                        switcherImageView2.setImageDrawable(vehiclePicList.get(0));
                    }
                    else {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
