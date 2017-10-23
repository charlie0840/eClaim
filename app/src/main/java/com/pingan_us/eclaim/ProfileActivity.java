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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.images.WebImage;

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

/**
 * Created by Charlie0840 on 10/14/2017.
 */

public class ProfileActivity extends Activity implements View.OnClickListener {
    private ImageSwitcher IDImageSwitcher, VehicleImageSwitcher;
    private LinearLayout photo_section;
    private RelativeLayout info_section, claim_section, phone_section, id_section, vehicle_section, add_id, add_vehicle;
    private Button view_doc_btn, file_claim_btn;
    private ImageButton phone_btn;
    private Animation animationLOut, animationLIn;
    private ImageView switcherImageView, switcherImageView2;
    private static final int REQUEST_CAMERA = 0, SELECT_FILE = 1, MY_CAMERA_REQUEST_CODE = 1;
    private List<Drawable> IDPicList, vehiclePicList;
    private int counter, ID_or_Vehicle;
    private String userChoosenTask;
    private View nav_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();

        IDPicList = new ArrayList<Drawable>();
        vehiclePicList = new ArrayList<Drawable>();

        Bitmap icon1 = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.addphoto);
        Bitmap icon2 = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.driverlicense);
        Bitmap icon3 = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.insurance);
        Drawable p1 = new BitmapDrawable(icon1);
        Drawable p2 = new BitmapDrawable(icon2);
        Drawable p3 = new BitmapDrawable(icon3);
        IDPicList.add(p1);
        IDPicList.add(p2);
        IDPicList.add(p3);
        vehiclePicList.add(p1);
        vehiclePicList.add(p2);
        vehiclePicList.add(p3);

        nav_bar = findViewById(R.id.nav_layout);
        ImageView home_nav = (ImageView) nav_bar.findViewById(R.id.home_nav);
        ImageView claim_nav = (ImageView) nav_bar.findViewById(R.id.claims_nav);
        home_nav.setOnClickListener(this);
        claim_nav.setOnClickListener(this);

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

        view_doc_btn = (Button) findViewById(R.id.claim_button);
        file_claim_btn = (Button) findViewById(R.id.file_claim_button);
        phone_btn = (ImageButton) findViewById(R.id.assistance_phone_button);

        add_id.setOnClickListener(this);
        add_vehicle.setOnClickListener(this);


        file_claim_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FileClaim1Activity.class); //fixed
                startActivity(intent);
            }
        });

        phone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:123456789"));
                if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "please grant the access to phone call", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(callIntent);
                }
            }
        });


        //nextImageButton = (Button) findViewById(R.id.nextImageButton);

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

        Toast.makeText(getApplicationContext(), "touched", Toast.LENGTH_LONG).show();
        id_section.setOnTouchListener(new OnSwipeTouchListener(getBaseContext()) {
            int switcherImage = 0;

            @Override
            public void onSwipeRight() {
                switcherImage = IDPicList.size();
                Toast.makeText(getApplicationContext(), "right touched", Toast.LENGTH_LONG).show();
                counter++;
                if (counter == switcherImage)
                    counter = 0;
                IDImageSwitcher.setImageDrawable(IDPicList.get(counter));
            }

            @Override
            public void onSwipeLeft() {
                switcherImage = IDPicList.size();

                Toast.makeText(getApplicationContext(), "left touched with size " + switcherImage, Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "right touched with size " + switcherImage, Toast.LENGTH_LONG).show();
                counter++;
                if (counter == switcherImage)
                    counter = 0;
                VehicleImageSwitcher.setImageDrawable(vehiclePicList.get(counter));
            }

            @Override
            public void onSwipeLeft() {
                switcherImage = vehiclePicList.size();
                Toast.makeText(getApplicationContext(), "left touched", Toast.LENGTH_LONG).show();
                counter--;
                if (counter == -1)
                    counter = switcherImage - 1;
                VehicleImageSwitcher.setImageDrawable(vehiclePicList.get(counter));
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_section:
                ID_or_Vehicle = 0;
                Toast.makeText(getApplicationContext(), "add pic!", Toast.LENGTH_SHORT).show();
                selectImage();
                break;
            case R.id.vehicle_section:
                ID_or_Vehicle = 1;
                Toast.makeText(getApplicationContext(), "add pic!", Toast.LENGTH_SHORT).show();
                selectImage();
                break;
            case R.id.home_nav:
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.profile_nav:
                break;
            case R.id.claims_nav:
                Intent intent1 = new Intent(this, ViewClaimsActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent1);
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
        Toast.makeText(this, "gallery!!!!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bitmap resBitmap = Bitmap.createScaledBitmap(bm, bm.getWidth(), bm.getHeight(), true);
        Drawable d = new BitmapDrawable(getResources(), resBitmap);
        addToList(d);
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
        addToList(d);

    }

    public void addToList(Drawable pic) {

        Toast.makeText(getApplicationContext(), "add to list!", Toast.LENGTH_SHORT).show();
        if (ID_or_Vehicle == 0) {
            IDPicList.add(pic);

            Toast.makeText(getApplicationContext(), "id size " + IDPicList.size(), Toast.LENGTH_SHORT).show();
        } else {
            vehiclePicList.add(pic);
            Toast.makeText(getApplicationContext(), "vehicle size " + vehiclePicList.size(), Toast.LENGTH_SHORT).show();

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
    }
}
