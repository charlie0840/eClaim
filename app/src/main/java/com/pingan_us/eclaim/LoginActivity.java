package com.pingan_us.eclaim;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ParseInstallation;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

    private Button login, register, logout;
    private EditText username, password;
    private String user_name, user_password;
    boolean enabled = false;

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
                enabled = true;
                logout.setVisibility(View.VISIBLE);
                username.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                login.setVisibility(View.GONE);
                register.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.logoutbutton:
                logout.setVisibility(View.GONE);
                username.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                login.setVisibility(View.VISIBLE);
                register.setVisibility(View.VISIBLE);
                break;
            case R.id.registerbutton:
                Intent intent3 = new Intent(getApplicationContext(), RegisterActivity.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent3);
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
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.icon);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), LoginActivity.class));
        sendBroadcast(shortcutintent);
    }

}
