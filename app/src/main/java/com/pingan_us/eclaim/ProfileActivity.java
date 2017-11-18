package com.pingan_us.eclaim;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Charlie0840 on 10/14/2017.
 */

public class ProfileActivity extends Activity implements View.OnClickListener {

    private Animation animationLOut, animationLIn;

    private View nav_bar;
    private ProgressBar progressBar;
    private CircleImageView profile_photo;
    private TextView name_text, phone_text, vehicle_text;
    private Button edit_btn, add_btn, delete_btn, logout_btn;
    private ImageSwitcher IDImageSwitcher, VehicleImageSwitcher;
    private ImageView switcherImageView, switcherImageView2, prev_btn, next_btn;
    private RelativeLayout id_section, vehicle_section, add_id, add_vehicle, vehicle_block,
            id_block, name_layer, phone_layer, background;

    private int counter, ID_or_Vehicle;
    private String userChoosenTask, phone_no, full_name, vehicleName;
    private List<Drawable> IDPicList, vehiclePicList;
    private byte[] imageByte = null, IDCardByte = null;
    private List<String> carList = new ArrayList<String>(), carIDList = new ArrayList<String>();
    private static final int REQUEST_CAMERA = 0, SELECT_FILE = 1, MY_CAMERA_REQUEST_CODE = 1, MY_CALL_REQUEST_CODE = 2,
            PROFILE_PHOTO = 3, ID = 0, VEHICLE = 1;
    private boolean resume = true, hasVehicle = false;
    private static ProfileActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        IDPicList = new ArrayList<Drawable>();
        vehiclePicList = new ArrayList<Drawable>();

        vehiclePicList.add(resize(getResources().getDrawable(R.drawable.insurance), 400, 400));

        nav_bar = findViewById(R.id.nav_layout);

        profile_photo = (CircleImageView) findViewById(R.id.profile_photo);
        Drawable p1 = resize(getResources().getDrawable(R.drawable.driverlicense), 400, 400);
        Drawable p2 = resize(getResources().getDrawable(R.drawable.insurance), 400, 400);
        IDPicList.add(p1);
        vehiclePicList.add(p2);


        RelativeLayout home_nav = (RelativeLayout) nav_bar.findViewById(R.id.home_nav);
        RelativeLayout claim_nav = (RelativeLayout) nav_bar.findViewById(R.id.claims_nav);
        home_nav.setOnClickListener(this);
        claim_nav.setOnClickListener(this);

        name_text = (TextView)findViewById(R.id.pro_name_text);
        phone_text = (TextView)findViewById(R.id.pro_phone_text);
        vehicle_text = (TextView)findViewById(R.id.profile_vehicle_name_text);
        phone_text.setText(phone_no);
        name_text.setText(full_name);

        progressBar = (ProgressBar) findViewById(R.id.profile_progressBar);

        IDImageSwitcher = (ImageSwitcher) findViewById(R.id.ID_switch);
        VehicleImageSwitcher = (ImageSwitcher) findViewById(R.id.vehicle_switch);

        add_id = (RelativeLayout) findViewById(R.id.id_section);
        add_vehicle = (RelativeLayout) findViewById(R.id.vehicle_section);
        id_section = (RelativeLayout) findViewById(R.id.entire_id_section);
        vehicle_section = (RelativeLayout) findViewById(R.id.entire_vehicle_section);
        vehicle_block = (RelativeLayout) findViewById(R.id.profile_vehicle_section);
        id_block = (RelativeLayout) findViewById(R.id.profile_id_section);
        name_layer = (RelativeLayout) findViewById(R.id.name_layout);
        phone_layer = (RelativeLayout) findViewById(R.id.phone_layout);
        background = (RelativeLayout) findViewById(R.id.profile_background);

        edit_btn = (Button) findViewById(R.id.id_add_button);
        logout_btn = (Button) findViewById(R.id.profile_logout_button);
        add_btn = (Button) findViewById(R.id.profile_add_vehicle_button);
        prev_btn = (ImageView) findViewById(R.id.profile_prev_vehicle_button);
        next_btn = (ImageView) findViewById(R.id.profile_next_vehicle_button);
        delete_btn = (Button) findViewById(R.id.profile_delete_vehicle_button);

        prev_btn.setOnClickListener(this);
        next_btn.setOnClickListener(this);
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

        id_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int switcherImage = IDPicList.size();
                counter++;
                if (counter == switcherImage)
                    counter = 0;
                IDImageSwitcher.setImageDrawable(IDPicList.get(counter));

            }
        });

        vehicle_section.setOnTouchListener(new OnSwipeTouchListener(getBaseContext()) {
            int switcherImage = 0;

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
                counter--;
                if (counter == -1)
                    counter = switcherImage - 1;
                VehicleImageSwitcher.setImageDrawable(vehiclePicList.get(counter));
                vehicle_text.setText(carList.get(counter));
            }
        });
        activity = this;

        progressBar.setVisibility(View.GONE);
        profile_photo.setVisibility(View.INVISIBLE);
        logout_btn.setVisibility(View.INVISIBLE);
        vehicle_block.setVisibility(View.INVISIBLE);
        id_block.setVisibility(View.INVISIBLE);
        name_layer.setVisibility(View.INVISIBLE);
        phone_layer.setVisibility(View.INVISIBLE);
        //doAnimation();
    }

    @TargetApi(23)
    @Override
    public void onClick(View v) {
        int switcherImage = 0;
        switch (v.getId()) {
            case R.id.id_add_button:
                ID_or_Vehicle = ID;
                selectImage();
                break;
            case R.id.profile_add_vehicle_button:
                ID_or_Vehicle = VEHICLE;

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

                LayoutInflater inflater=ProfileActivity.this.getLayoutInflater();
                //this is what I did to added the layout to the alert dialog
                View layout=inflater.inflate(R.layout.popup_window,null);
                builder.setView(layout);
                final EditText editText = (EditText) layout.findViewById(R.id.editTextDialogUserInput);
                Button confirm_btn = (Button) layout.findViewById(R.id.pop_confirm_button);
                Button cancel_btn = (Button) layout.findViewById(R.id.pop_cancel_button);

                final AlertDialog dialog = builder.create();
                dialog.show();

                confirm_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vehicleName = editText.getText().toString();
                        dialog.dismiss();
                        selectImage();
                    }
                });
                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.profile_prev_vehicle_button:
                switcherImage = vehiclePicList.size();
                if(switcherImage == 0) {
                    break;
                }
                counter--;
                if (counter == -1)
                    counter = switcherImage - 1;
                VehicleImageSwitcher.setImageDrawable(vehiclePicList.get(counter));
                vehicle_text.setText(carList.get(counter));
                break;
            case R.id.profile_next_vehicle_button:
                switcherImage = vehiclePicList.size();
                if(switcherImage == 0) {
                    break;
                }
                counter++;
                if (counter == switcherImage)
                    counter = 0;
                VehicleImageSwitcher.setImageDrawable(vehiclePicList.get(counter));
                vehicle_text.setText(carList.get(counter));
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
                finish();
                break;
            case R.id.claims_nav:
                Intent intent1 = new Intent(this, ViewClaimt.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                break;
            case R.id.profile_logout_button:
                ParseUser currUser = ParseUser.getCurrentUser();
                currUser.logOut();
                if(HomeActivity.getActivity() != null)
                    HomeActivity.getActivity().finish();
                if(ViewClaimt.getInstance() != null)
                    ViewClaimt.getInstance().finish();
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
        if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
        Bitmap resBitmap = null;
        if (data != null) {
            String path = Utility.getPath(getApplicationContext(), data.getData());
            Uri uri = Uri.parse(new File(path).toString());
            bm = Bitmap.createBitmap(Utility.compressImageUri(uri, 1024, 768, getApplicationContext()));
            if(bm != null) {
                resBitmap = Bitmap.createScaledBitmap(bm, bm.getWidth(), bm.getHeight(), true);
                addToList(resBitmap);
            }
            else {
                Toast.makeText(getApplicationContext(), "Failed to load image!", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Failed to load image", Toast.LENGTH_LONG).show();
        }
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
        addToList(resBitmap);

    }

    public void addToList(Bitmap bmp) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
        background.setAlpha((float) 0.5);
        Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 600, 350, true);
        Drawable pic = new BitmapDrawable(getResources(), scaledBmp);
        if (ID_or_Vehicle == ID) {
            IDPicList.set(0, pic);
            switcherImageView.setImageDrawable(pic);
            uploadImg(bmp);
        } else if (ID_or_Vehicle == VEHICLE){
            if(!hasVehicle) {
                vehiclePicList.clear();
                carList.clear();
                hasVehicle = true;
            }
            vehiclePicList.add(pic);
            if(vehiclePicList.size() > 1)
                counter = vehiclePicList.size() - 2;
            carList.add(vehicleName);
            next_btn.performClick();
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

        name_text.setText(full_name);
        phone_text.setText(phone_no);

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
                    bmp.recycle();
                }
                else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    background.setAlpha((float) 0);
                    progressBar.setVisibility(View.GONE);
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
                        try {
                            currUser.save();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        background.setAlpha((float) 0);
                        progressBar.setVisibility(View.GONE);
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
                    background.setAlpha((float) 0);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        else {
            currUser.put("idImage", file);
            currUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    background.setAlpha((float) 0);
                    progressBar.setVisibility(View.GONE);
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
            vehiclePicList.add(getResources().getDrawable(R.drawable.insurance));
            carList.add("Add a vehicle");
            hasVehicle = false;
            return;
        }
        hasVehicle = true;
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
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 600, 350, false);
                Drawable d = new BitmapDrawable(getResources(), scaledBmp);
                bmp.recycle();
                vehiclePicList.add(d);
                carList.add(name);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(vehiclePicList.get(0) != null)
            switcherImageView2.setImageDrawable(vehiclePicList.get(0));
    }

    public void deleteVehicle() {
        if(carIDList.size() == 0) {
            return;
        }
        carList.remove(counter);
        vehiclePicList.remove(counter);
        String id = carIDList.get(counter);
        carIDList.remove(counter);
        if(carIDList.size() == 0)
            hasVehicle = false;
        counter = 0;
        if(vehiclePicList.size() == 0) {
            vehiclePicList.add(getResources().getDrawable(R.drawable.insurance));
            carList.add("No vehicle");
        }
        next_btn.performClick();
        ParseUser currUser = ParseUser.getCurrentUser();
        currUser.put("vehicleID", carIDList);
        try {
            currUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Vehicle");
        query.whereEqualTo("objectId", id);
        try {
            ParseObject obj = query.getFirst();
            obj.delete();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void loadContent() {
        getData();
        getVehicleList();
        Bitmap profileImage = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        profileImage = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, options);
        Bitmap finalBmp = Bitmap.createScaledBitmap(profileImage, 320, 240, true);
        profileImage.recycle();
        if(IDCardByte != null) {
            IDPicList.clear();
            BitmapFactory.Options options1 = new BitmapFactory.Options();
            Bitmap bmp = BitmapFactory.decodeByteArray(IDCardByte, 0, IDCardByte.length, options1);
            Bitmap cardBmp = Bitmap.createScaledBitmap(bmp, 600, 350, true);
            bmp.recycle();
            Drawable d = new BitmapDrawable(getResources(), cardBmp);
            IDPicList.add(d);
            switcherImageView.setImageDrawable(d);
        }
        profile_photo.setImageBitmap(finalBmp);
    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onResume() {
        super.onResume();
        if(resume) {
            loadContent();
            vehicle_text.setText(carList.get(0));
            id_section.performClick();
            id_section.setClickable(false);
            next_btn.performClick();
            resume = false;
            doAnimation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void doAnimation() {
        ScrollView scrollView = findViewById(R.id.profile_scrollview);

        final Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_in);
        final Animation alpha = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        final Animation scale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        scrollView.startAnimation(slideUp);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                profile_photo.setVisibility(View.VISIBLE);
                logout_btn.setVisibility(View.VISIBLE);
                vehicle_block.setVisibility(View.VISIBLE);
                id_block.setVisibility(View.VISIBLE);
                phone_layer.setVisibility(View.VISIBLE);
                name_layer.setVisibility(View.VISIBLE);
                profile_photo.startAnimation(scale);
                logout_btn.startAnimation(alpha);
                name_layer.startAnimation(alpha);
                phone_layer.startAnimation(alpha);
                id_block.startAnimation(alpha);
                vehicle_block.startAnimation(alpha);
            }
        }, 1200);
    }

    private Drawable resize(Drawable image, int height, int width) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, height, width, true);
        //b.recycle();
        return new BitmapDrawable(getResources(), bitmapResized);
    }
}
