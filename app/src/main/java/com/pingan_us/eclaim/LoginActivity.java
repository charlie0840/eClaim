package com.pingan_us.eclaim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    private Button login, register, unregister, logout;
    private EditText username, password;
    private String user_name, user_password;
    Context CTX = this;
    boolean enabled = false;
    private String type, final_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        createShortCut();

        login=(Button)findViewById(R.id.loginbutton);
        register = (Button)findViewById(R.id.registerbutton);
        unregister = (Button)findViewById(R.id.unregister_btn);
        logout = (Button)findViewById(R.id.logoutbutton);

        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        logout.setVisibility(View.GONE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_name = username.getText().toString();
                user_password = password.getText().toString();
                if(user_name.length() == 0) {
                    Toast.makeText(getApplicationContext(), "please enter user name", Toast.LENGTH_SHORT).show();
                    return;
                }
                type = "login";
                String retval = "";
                String result = "";
                NetCheck netCheck = new NetCheck();
                netCheck.execute();
                boolean retVal = false;
                try {
                    retVal = netCheck.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(retVal) {
                    BackgroundWorker backgroundWorker = new BackgroundWorker(CTX);
                    backgroundWorker.execute(type, user_name, user_password);
                    try {
                        result = backgroundWorker.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    String succ = "succeed";
                    String fail = "fail";
                    if(result == null)
                    {
                        Toast.makeText(getApplicationContext(), "Please re-log in", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (result.toLowerCase().indexOf(succ.toLowerCase()) != -1) {

                            retval = succ;
                            final_user = user_name;
                        } else
                            retval = fail;
                        if (retval.equals("succeed")) {

                            enabled = true;
                            logout.setVisibility(View.VISIBLE);
                            username.setVisibility(View.GONE);
                            password.setVisibility(View.GONE);
                            login.setVisibility(View.GONE);
                            register.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class); //fixed
                            intent.putExtra("user", final_user);
                            startActivity(intent);
                        }
                    }
                }
//				user_name = username.getText().toString();
//				user_password = password.getText().toString();
//				DatabaseOperations DOP = new DatabaseOperations(CTX);
//				Cursor CR = DOP.getInformation(DOP);
//				CR.moveToFirst();
//				boolean status = false;
//				String NAME = "";
//				do {
//					if(user_name.equals(CR.getString(0)) && user_password.equals(CR.getString(1))) {
//						Toast.makeText(getApplicationContext(), "Redirecting...",Toast.LENGTH_SHORT).show();
//						//beginButton.setEnabled(true);
//						//joinButton.setEnabled(true);
//						enabled = true;
//						beginButton.setVisibility(View.VISIBLE);
//						joinButton.setVisibility(View.VISIBLE);
//						logout.setVisibility(View.VISIBLE);
//						username.setVisibility(View.GONE);
//						password.setVisibility(View.GONE);
//						login.setVisibility(View.GONE);
//						register.setVisibility(View.GONE);
//						break;
//					}
//					else{
//						Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();
//					}
//
//				}while (CR.moveToNext());
            }
        });
        logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                logout.setVisibility(View.GONE);
                username.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                login.setVisibility(View.VISIBLE);
                register.setVisibility(View.VISIBLE);
            }
        });
        setUpUIElements();

    }

    /**
     * Async Task to check whether internet connection is working.
     **/

    private class NetCheck extends AsyncTask<String,String,Boolean> {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(LoginActivity.this);
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
            }
            else{
                nDialog.setMessage("not connected to network");
                Handler handler = null;
                handler = new Handler();
                handler.postDelayed(new Runnable(){
                    public void run(){
                        nDialog.cancel();
                        nDialog.dismiss();
                    }
                }, 1000);
            }
        }
    }

    private View.OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.registerbutton:
                    Intent intent3 = new Intent(getApplicationContext(), RegisterActivity.class);
                    intent3.putExtra("reg", true);
                    startActivity(intent3);
                    break;

                case R.id.unregister_btn:
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
                        Intent intent4 = new Intent(getApplicationContext(), RegisterActivity.class);
                        intent4.putExtra("reg", false);
                        startActivity(intent4);
                    }
                    break;
            }
        }
    };

    private void setUpUIElements() {
        register.setOnClickListener(onClickListener);
        unregister.setOnClickListener(onClickListener);
    }

    public void createShortCut(){
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutintent.putExtra("duplicate", false);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "eClaim");
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.icon);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), LoginActivity.class));
        sendBroadcast(shortcutintent);
    }

}