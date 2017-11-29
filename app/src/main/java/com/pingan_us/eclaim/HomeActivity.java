package com.pingan_us.eclaim;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private final static int MY_CALL_REQUEST_CODE = 1;
    private byte[] imageByte;
    private String full_name, phone_no, email_addr;
    private View nav_bar;
    private ImageView phone_btn, profile_photo;
    private RelativeLayout file_claim_btn, view_claim_btn;
    private TextView username_txt, email_txt, assistance_phone_txt;
    private static HomeActivity activity = getActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        getData();

        Bitmap profileImage = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        profileImage = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, options);

        profile_photo = findViewById(R.id.home_profile_photo);
        profile_photo.setImageBitmap(profileImage);
        profile_photo.setVisibility(View.INVISIBLE);

        nav_bar = findViewById(R.id.nav_layout);

        RelativeLayout profile_nav = (RelativeLayout) nav_bar.findViewById(R.id.profile_nav);
        RelativeLayout claim_nav = (RelativeLayout) nav_bar.findViewById(R.id.claims_nav);
        profile_nav.setOnClickListener(this);
        claim_nav.setOnClickListener(this);

        assistance_phone_txt = findViewById(R.id.home_road_assistance_phone);
        username_txt = findViewById(R.id.home_username);
        email_txt = findViewById(R.id.home_email);

        username_txt.setText(full_name);
        email_txt.setText(email_addr);

        phone_btn = findViewById(R.id.home_assistance_phone_btn);
        file_claim_btn = findViewById(R.id.home_file_claim_btn);
        view_claim_btn = findViewById(R.id.home_view_claim_btn);

        phone_btn.setOnClickListener(this);
        file_claim_btn.setOnClickListener(this);
        view_claim_btn.setOnClickListener(this);

        file_claim_btn.setVisibility(View.INVISIBLE);
        view_claim_btn.setVisibility(View.INVISIBLE);
        username_txt.setVisibility(View.INVISIBLE);
        email_txt.setVisibility(View.INVISIBLE);

        doAnimation();
    }

    @TargetApi(23)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_nav:
                Intent intent = new Intent(this, ProfileActivity.class);
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
            case R.id.home_assistance_phone_btn:
                if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, MY_CALL_REQUEST_CODE);
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:6262678903"));
                    startActivity(callIntent);
                }
                break;
            case R.id.home_file_claim_btn:
                Intent intent2 = new Intent(this, FileClaim1Activity.class); //fixed
                intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent2);
                if(ViewClaimt.getInstance() != null)
                    ViewClaimt.getInstance().finish();
                if(ProfileActivity.getInstance() != null)
                    ProfileActivity.getInstance().finish();
                this.finish();
                break;
            case R.id.home_view_claim_btn:
                Intent intent3 = new Intent(this, ViewClaimt.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                profile_photo.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_CALL_REQUEST_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + assistance_phone_txt.getText().toString()));
                if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                    startActivity(callIntent);
            }
            else
                Toast.makeText(this, "Calling permission denied", Toast.LENGTH_LONG).show();
        }
    }

    public void getData() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        full_name = (String)currentUser.get("lastName") + "," + (String)currentUser.get("firstName");
        email_addr = (String)currentUser.get("email");
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

    public void doAnimation() {
        ScrollView scrollView = findViewById(R.id.home_scroll_view);

        final Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up_in);
        final Animation alpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        final Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
        scrollView.startAnimation(slideUp);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                profile_photo.setVisibility(View.VISIBLE);
                file_claim_btn.setVisibility(View.VISIBLE);
                view_claim_btn.setVisibility(View.VISIBLE);
                username_txt.setVisibility(View.VISIBLE);
                email_txt.setVisibility(View.VISIBLE);
                profile_photo.startAnimation(scale);
                username_txt.startAnimation(alpha);
                email_txt.startAnimation(alpha);
                file_claim_btn.startAnimation(alpha);
                view_claim_btn.startAnimation(alpha);
            }
        }, 1000);
    }

    @Override
    public void onBackPressed() {}

    public static HomeActivity getActivity() {
        return activity;
    }
}
