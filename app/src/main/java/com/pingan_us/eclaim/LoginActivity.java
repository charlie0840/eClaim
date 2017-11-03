package com.pingan_us.eclaim;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseInstallation;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{
    private View nav_bar;
    private Button login, register, logout;
    private EditText username, password;
    private String user_name, user_password;
    private boolean enabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parse.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        createShortCut();

        login=(Button)findViewById(R.id.loginbutton);
        register = (Button)findViewById(R.id.registerbutton);
        logout = (Button)findViewById(R.id.logoutbutton);

        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        logout.setVisibility(View.GONE);
        register.setVisibility(View.VISIBLE);

        login.setOnClickListener(this);
        logout.setOnClickListener(this);
        register.setOnClickListener(this);

        username.setOnKeyListener(this);
        password.setOnKeyListener(this);

        nav_bar = findViewById(R.id.nav_layout);
        ImageView profile_nav = (ImageView) nav_bar.findViewById(R.id.profile_nav);
        ImageView claim_nav = (ImageView) nav_bar.findViewById(R.id.claims_nav);
        profile_nav.setOnClickListener(this);
        claim_nav.setOnClickListener(this);
        nav_bar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.loginbutton:
                user_name = username.getText().toString();
                user_password = password.getText().toString();
                if(user_name.length() == 0) {
                    Toast.makeText(getApplicationContext(), "please enter user name", Toast.LENGTH_SHORT).show();
                    return;
                }
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                logIn();
                break;
            case R.id.logoutbutton:
                ParseUser currUser = ParseUser.getCurrentUser();
                currUser.logOut();
                ProfileActivity.getInstance().finish();
                if(ViewClaimt.getInstance() != null)
                    ViewClaimt.getInstance().finish();
                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                break;
            case R.id.registerbutton:
                Intent intent3 = new Intent(getApplicationContext(), RegisterActivity.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent3);
                break;
            case R.id.profile_nav:
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.claims_nav:
                Intent intent1 = new Intent(this, ViewClaimt.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
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

    public void createShortCut(){
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutintent.putExtra("duplicate", false);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "eClaim");
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.app);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), LoginActivity.class));
        sendBroadcast(shortcutintent);
    }

    public void logIn() {
        ParseUser.logInInBackground(user_name, user_password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user == null) {
                    Toast.makeText(getApplicationContext(), "User name or password incorrect!", Toast.LENGTH_LONG).show();
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    enabled = true;
                    logout.setVisibility(View.VISIBLE);
                    username.setVisibility(View.GONE);
                    password.setVisibility(View.GONE);
                    login.setVisibility(View.GONE);
                    register.setVisibility(View.GONE);
                    nav_bar.setVisibility(View.VISIBLE);

                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
                if(e != null) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
