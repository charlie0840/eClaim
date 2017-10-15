package com.pingan_us.eclaim;

/**
 * Created by Charlie0840 on 10/13/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends Activity {
    EditText FIRST_NAME, LAST_NAME, USER_PASSWORD, CONFIRM_PASSWORD, EMAIL_ADDRESS, PHONE;
    ImageView PHOTO;
    Button registerButton, cancelButton;
    String first_name, last_name, user_password, confirm_password, type, user_role = "none", email_address, phone;
    Context ctx = this;
    boolean reg = true;
    byte[] picBinary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();
        reg = intent.getBooleanExtra("reg", true);

        registerButton = (Button)findViewById(R.id.confirm_btn);
        cancelButton = (Button)findViewById(R.id.cancel_btn);

        FIRST_NAME = (EditText)findViewById(R.id.firstname);
        LAST_NAME = (EditText)findViewById(R.id.lastname);
        PHONE = (EditText)findViewById(R.id.phone);

        USER_PASSWORD = (EditText)findViewById(R.id.password);
        CONFIRM_PASSWORD = (EditText)findViewById(R.id.confpassword);
        EMAIL_ADDRESS = (EditText)findViewById(R.id.email_text);

        if(!reg)
            registerButton.setText("Unregister");

        registerButton.setOnClickListener(onClickListener);
        cancelButton.setOnClickListener(onClickListener);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1234:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();


                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    PHOTO.setImageBitmap(yourSelectedImage);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    picBinary = stream.toByteArray();
                }
        }
    };



    /**
     * Async Task to check whether internet connection is working.
     **/
    private class NetCheck extends AsyncTask<String,String,Boolean> {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(RegisterActivity.this);
            nDialog.setTitle("Checking Network");
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
        /**
         * Gets current device state and checks for working internet connection by trying Google.
         **/
        @Override
        protected Boolean doInBackground(String... args){
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;

        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                nDialog.dismiss();
                //BackgroundWorker backgroundWorker = new BackgroundWorker(ctx);
                //backgroundWorker.execute(type, user_name, user_password);
            }
            else{
                nDialog.dismiss();
            }
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.confirm_btn:
                    first_name = FIRST_NAME.getText().toString();
                    last_name = LAST_NAME.getText().toString();
                    phone = PHONE.getText().toString();
                    email_address = EMAIL_ADDRESS.getText().toString();
                    user_password = USER_PASSWORD.getText().toString();
                    confirm_password = CONFIRM_PASSWORD.getText().toString();
                    if(first_name.length() == 0) {
                        Toast.makeText(getApplicationContext(), "first name must not be empty", Toast.LENGTH_SHORT).show();
                        USER_PASSWORD.setText("");
                        CONFIRM_PASSWORD.setText("");
                    }
                    else if(last_name.length() == 0) {
                        Toast.makeText(getApplicationContext(), "last name must not be empty", Toast.LENGTH_SHORT).show();
                        USER_PASSWORD.setText("");
                        CONFIRM_PASSWORD.setText("");
                    }
                    else if(phone.length() == 0) {
                        Toast.makeText(getApplicationContext(), "phone number must not be empty", Toast.LENGTH_SHORT).show();
                        USER_PASSWORD.setText("");
                        CONFIRM_PASSWORD.setText("");
                    }
                    else if(email_address.length() == 0) {
                        Toast.makeText(getApplicationContext(), "email address must not be empty", Toast.LENGTH_SHORT).show();
                        USER_PASSWORD.setText("");
                        CONFIRM_PASSWORD.setText("");
                    }
                    else if(user_password.length() < 2) {
                        Toast.makeText(getApplicationContext(), "length of password must be at least 2", Toast.LENGTH_SHORT).show();
                        USER_PASSWORD.setText("");
                        CONFIRM_PASSWORD.setText("");
                    }
                    else if(picBinary == null) {
                        Toast.makeText(getApplicationContext(), "please upload the photo of your driver license", Toast.LENGTH_SHORT).show();
                        USER_PASSWORD.setText("");
                        CONFIRM_PASSWORD.setText("");
                    }
                    else {
                        if (reg)
                            type = "register";
                        else
                            type = "unregister";
                        if (!user_password.equals(confirm_password)) {
                            Toast.makeText(getApplicationContext(), "Passwordwords do not match", Toast.LENGTH_SHORT).show();
                            USER_PASSWORD.setText("");
                            CONFIRM_PASSWORD.setText("");
                        } else {
                            NetCheck netCheck = new NetCheck();
                            netCheck.execute();
                            boolean retVal = false;
                            try {
                                retVal = netCheck.get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            if(retVal) {
                                String uploadImage = Base64.encodeToString(picBinary, Base64.DEFAULT);
                                BackgroundWorker backgroundWorker = new BackgroundWorker(ctx);
                                backgroundWorker.execute(type, first_name, last_name, email_address, phone, user_password, uploadImage);
                            }
                        }
                    }
                    break;

                case R.id.cancel_btn:
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    break;
                case R.id.photo:
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    final int ACTIVITY_SELECT_IMAGE = 1234;
                    startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
                    break;
            }
        }
    };
}
