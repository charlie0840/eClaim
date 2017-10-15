package com.pingan_us.eclaim;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Charlie0840 on 10/14/2017.
 */

public class ProfileActivity extends Activity{
    private ImageSwitcher IDImageSwitcher, VehicleImageSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();
        IDImageSwitcher = (ImageSwitcher) findViewById(R.id.ID_switch);
        //nextImageButton = (Button) findViewById(R.id.nextImageButton);

        IDImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView switcherImageView = new ImageView(getApplicationContext());
                switcherImageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT
                ));
                switcherImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                switcherImageView.setImageResource(R.drawable.addphoto);
                return switcherImageView;
            }
        });
        addImageForSwitcher(IDImageSwitcher);

        Animation animationLOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        Animation animationLIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        IDImageSwitcher.setOutAnimation(animationLOut);
        IDImageSwitcher.setInAnimation(animationLIn);


        //准备把左右滑动加上
     /*   IDImageSwitcher.setOnTouchListener(new OnSwipeTouchListener(getBaseContext()) {
            @Override
            void onSwipeRight() {
                counter++;
                if (counter == switcherImage)
                    counter = 0;
                myImageSwitcher.setImageResource(imageSwitcherImages[counter]);
            }

            @Override
            void onSwipeLeft() {
                counter++;
                if (counter == switcherImage)
                    counter = 0;
                myImageSwitcher.setImageResource(imageSwitcherImages[counter]);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }

        });*/






    }

    protected  void addImageForSwitcher(ImageSwitcher switcher){//List<Bitmap> list){

        //switcher.setImageDrawable(new BitmapDrawable(list.get(index)));


        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView switcherImageView = new ImageView(getApplicationContext());
                switcherImageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT
                ));
                switcherImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                switcherImageView.setImageResource(R.drawable.icon);
                return switcherImageView;
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {

        }
    };
}
