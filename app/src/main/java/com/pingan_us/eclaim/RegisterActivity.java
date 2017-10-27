package com.pingan_us.eclaim;

/**
 * Created by Charlie0840 on 10/13/2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends Activity implements View.OnClickListener, View.OnKeyListener{
    private EditText USER_NAME, FIRST_NAME, LAST_NAME, USER_PASSWORD, CONFIRM_PASSWORD, EMAIL_ADDRESS, PHONE;
    private ImageView PHOTO;
    private Button registerButton, cancelButton;

    private final int ACTIVITY_SELECT_IMAGE = 1234;

    private String user_name, first_name, last_name, user_password, confirm_password, email_address, phone;
    private boolean reg = true;
    private byte[] picBinary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Parse.initialize(this);

        Intent intent = getIntent();
        reg = intent.getBooleanExtra("reg", true);

        PHOTO = (ImageView) findViewById(R.id.reg_photo);

        registerButton = (Button)findViewById(R.id.reg_confirm_btn);
        cancelButton = (Button)findViewById(R.id.reg_cancel_btn);

        USER_NAME = (EditText) findViewById(R.id.reg_username);
        FIRST_NAME = (EditText)findViewById(R.id.reg_firstname);
        LAST_NAME = (EditText)findViewById(R.id.reg_lastname);
        PHONE = (EditText)findViewById(R.id.reg_phone);

        USER_PASSWORD = (EditText)findViewById(R.id.reg_password);
        CONFIRM_PASSWORD = (EditText)findViewById(R.id.reg_confpassword);
        EMAIL_ADDRESS = (EditText)findViewById(R.id.reg_email_text);

        USER_PASSWORD.setOnKeyListener(this);
        CONFIRM_PASSWORD.setOnKeyListener(this);
        EMAIL_ADDRESS.setOnKeyListener(this);
        FIRST_NAME.setOnKeyListener(this);
        LAST_NAME.setOnKeyListener(this);
        PHONE.setOnKeyListener(this);

        registerButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        PHOTO.setOnClickListener(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1234:
                if(resultCode == RESULT_OK){
                    Bitmap bm=null;
                    if (data != null) {
                        try {
                            bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Bitmap yourSelectedImage = Bitmap.createScaledBitmap(bm, bm.getWidth()/2, bm.getHeight()/2, true);

                    bm.recycle();

                    PHOTO.setImageBitmap(yourSelectedImage);
                }
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
                        checkDuplicateUser(user_name);

                    }
                }
                Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent1);
                break;

            case R.id.reg_cancel_btn:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.reg_photo:
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select File"), ACTIVITY_SELECT_IMAGE);
                break;
        }
    }

    private void checkDuplicateUser(String user) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", user_name);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null) {
                    signUp();
                }
            }
        });
    }

    private void signUp() {
        ParseUser user = new ParseUser();
        user.setUsername(user_name);
        user.put("last_name", last_name);
        user.put("first_name", first_name);
        user.put("phone", phone);
        user.put("email", email_address);
        user.put("password", user_password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                    alertDialogBuilder.setMessage("Registration successful");
                            alertDialogBuilder.setPositiveButton("Return to Login page",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            startActivity(intent);
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });
    }
}
