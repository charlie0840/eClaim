package com.pingan_us.eclaim;

/**
 * Created by Charlie0840 on 10/13/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends Activity implements View.OnClickListener, View.OnKeyListener{
    private EditText USER_NAME, FIRST_NAME, LAST_NAME, USER_PASSWORD, CONFIRM_PASSWORD, EMAIL_ADDRESS, PHONE;
    private ImageView PHOTO;
    private ProgressBar spinner;
    private RelativeLayout background;
    private Button registerButton, cancelButton;

    private final int ACTIVITY_SELECT_IMAGE = 1234, MY_CAMERA_REQUEST_CODE = 1, REQUEST_CAMERA = 2, SELECT_FILE = 3;

    private String user_name, first_name, last_name, user_password, confirm_password, email_address, phone, userChoosenTask;
    private boolean reg = true;
    private Bitmap picBinary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        Parse.initialize(this);

        Intent intent = getIntent();
        reg = intent.getBooleanExtra("reg", true);

        PHOTO = (ImageView) findViewById(R.id.reg_photo);

        spinner = (ProgressBar) findViewById(R.id.reg_progressBar);
        spinner.setVisibility(View.GONE);

        background = (RelativeLayout) findViewById((R.id.reg_background));
        background.setVisibility(View.GONE);

        registerButton = (Button)findViewById(R.id.reg_confirm_btn);
        cancelButton = (Button)findViewById(R.id.reg_cancel_btn);

        USER_NAME = (EditText) findViewById(R.id.reg_username);
        FIRST_NAME = (EditText)findViewById(R.id.reg_firstname);
        LAST_NAME = (EditText)findViewById(R.id.reg_lastname);
        PHONE = (EditText)findViewById(R.id.reg_phone);

        USER_PASSWORD = (EditText)findViewById(R.id.reg_password);
        CONFIRM_PASSWORD = (EditText)findViewById(R.id.reg_confirmpassword);
        EMAIL_ADDRESS = (EditText)findViewById(R.id.reg_email);

        USER_PASSWORD.setOnKeyListener(this);
        CONFIRM_PASSWORD.setOnKeyListener(this);
        EMAIL_ADDRESS.setOnKeyListener(this);
        FIRST_NAME.setOnKeyListener(this);
        LAST_NAME.setOnKeyListener(this);
        USER_NAME.setOnKeyListener(this);
        PHONE.setOnKeyListener(this);

        registerButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        PHOTO.setOnClickListener(this);
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

    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            String path = Utility.getPath(getApplicationContext(), data.getData());
            Uri uri = Uri.parse(new File(path).toString());
            picBinary = Bitmap.createBitmap(Utility.compressImageUri(uri, 640, 480, getApplicationContext()));
            Bitmap yourSelectedImage = Bitmap.createScaledBitmap(picBinary, 640, 480, true);
            PHOTO.setImageBitmap(yourSelectedImage);
        }
        else {
            Toast.makeText(getApplicationContext(), "Failed to get picture", Toast.LENGTH_LONG).show();
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
        picBinary = Bitmap.createScaledBitmap(thumbnail, thumbnail.getWidth(), thumbnail.getHeight(), true);
        Bitmap yourSelectedImage = Bitmap.createScaledBitmap(picBinary, 640, 480, true);
        PHOTO.setImageBitmap(yourSelectedImage);
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
        switch(v.getId()) {
            case R.id.reg_confirm_btn:
                user_name = USER_NAME.getText().toString();
                first_name = FIRST_NAME.getText().toString();
                last_name = LAST_NAME.getText().toString();
                phone = PHONE.getText().toString();
                email_address = EMAIL_ADDRESS.getText().toString();
                user_password = USER_PASSWORD.getText().toString();
                confirm_password = CONFIRM_PASSWORD.getText().toString();
                if(user_name.length() == 0) {
                    Toast.makeText(getApplicationContext(), MyAppConstants.regUNEmpty, Toast.LENGTH_SHORT).show();
                    USER_PASSWORD.setText("");
                    CONFIRM_PASSWORD.setText("");
                }
                if(first_name.length() == 0) {
                    Toast.makeText(getApplicationContext(), MyAppConstants.regFNEmpty, Toast.LENGTH_SHORT).show();
                    USER_PASSWORD.setText("");
                    CONFIRM_PASSWORD.setText("");
                }
                else if(last_name.length() == 0) {
                    Toast.makeText(getApplicationContext(), MyAppConstants.regLNEmpty, Toast.LENGTH_SHORT).show();
                    USER_PASSWORD.setText("");
                    CONFIRM_PASSWORD.setText("");
                }
                else if(phone.length() == 0) {
                    Toast.makeText(getApplicationContext(), MyAppConstants.regPhoneEmpty, Toast.LENGTH_SHORT).show();
                    USER_PASSWORD.setText("");
                    CONFIRM_PASSWORD.setText("");
                }
                else if(email_address.length() == 0) {
                    Toast.makeText(getApplicationContext(), MyAppConstants.regEAEmpty, Toast.LENGTH_SHORT).show();
                    USER_PASSWORD.setText("");
                    CONFIRM_PASSWORD.setText("");
                }
                else if(user_password.length() < 2) {
                    Toast.makeText(getApplicationContext(), MyAppConstants.regPWLength, Toast.LENGTH_SHORT).show();
                    USER_PASSWORD.setText("");
                    CONFIRM_PASSWORD.setText("");
                }
                else if(picBinary == null) {
                    Toast.makeText(getApplicationContext(), MyAppConstants.regUPEmpty, Toast.LENGTH_SHORT).show();
                    USER_PASSWORD.setText("");
                    CONFIRM_PASSWORD.setText("");
                }
                else {
                    if (!user_password.equals(confirm_password)) {
                        Toast.makeText(getApplicationContext(), MyAppConstants.regPWMatch, Toast.LENGTH_SHORT).show();
                        USER_PASSWORD.setText("");
                        CONFIRM_PASSWORD.setText("");
                    }
                    else {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        spinner.setVisibility(View.VISIBLE);
                        background.setVisibility(View.VISIBLE);
                        background.setAlpha((float)0.7);
                        uploadImg();
                    }
                }
                break;
            case R.id.reg_cancel_btn:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.reg_photo:
                selectImage();
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(RegisterActivity.this);
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "No permission, please grant the access to gallery", Toast.LENGTH_LONG).show();
                    return;
                }
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

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager pm = getApplicationContext().getPackageManager();
        if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                startActivityForResult(intent, REQUEST_CAMERA);
            else
                Toast.makeText(getApplicationContext(), "No camera detected", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Permission of camera is needed", Toast.LENGTH_LONG).show();
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void uploadImg() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picBinary.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        final ParseFile file = new ParseFile("imageID", byteArray);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                    signUp(file);
            }
        });
    }

    private void signUp(ParseFile file) {
        ParseUser user = new ParseUser();
        user.setUsername(user_name);
        user.setPassword(user_password);
        user.setEmail(email_address);
        user.put("lastName", last_name);
        user.put("firstName", first_name);
        user.put("phoneNo", phone);
        user.put("idImage", file);
        Toast.makeText(getApplicationContext(), "Start to signup!", Toast.LENGTH_LONG).show();
        ParseUser currUser = ParseUser.getCurrentUser();
        currUser.logOut();
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                background.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                if(e == null) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                    alertDialogBuilder.setMessage("Registration successful");
                            alertDialogBuilder.setPositiveButton("Return to Login page",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                else {
                    e.printStackTrace();
                    switch(e.getCode()) {
                        case ParseException.USERNAME_TAKEN:
                            Toast.makeText(getApplicationContext(), "User name has been taken!", Toast.LENGTH_LONG).show();
                            USER_PASSWORD.setText("");
                            CONFIRM_PASSWORD.setText("");
                            break;
                        case ParseException.EMAIL_TAKEN:
                            Toast.makeText(getApplicationContext(), "Email address has been used!", Toast.LENGTH_LONG).show();
                            USER_PASSWORD.setText("");
                            CONFIRM_PASSWORD.setText("");
                            break;
                    }
                }
            }
        });
    }
}
