package com.pingan_us.eclaim;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class photoActivity extends FragmentActivity {
    ImageView im_move_zoom_rotate;
    RelativeLayout layout;
    float scalediff;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private Bitmap bmp;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Intent intent = getIntent();
        byte[] imageByte = intent.getByteArrayExtra("imageByte");
        bmp = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);

        init();

        layout = (RelativeLayout) findViewById(R.id.photo_layout);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels/density;
        float dpWidth = outMetrics.widthPixels/density;

        layout.setMinimumWidth((int)(dpWidth * 0.8));
        layout.setMinimumHeight((int)(dpHeight * 0.8));
        im_move_zoom_rotate = (ImageView) findViewById(R.id.zoom_image);
        im_move_zoom_rotate.setImageBitmap(bmp);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(dpWidth * 0.7), (int)(dpHeight * 0.7));
        layoutParams.leftMargin = 50;
        layoutParams.topMargin = 50;
        layoutParams.bottomMargin = 50;
        layoutParams.rightMargin = 50;
        im_move_zoom_rotate.setLayoutParams(layoutParams);
        im_move_zoom_rotate.setScaleX((float)1.5);
        im_move_zoom_rotate.setScaleY((float)1.5);
        im_move_zoom_rotate.setOnTouchListener(new View.OnTouchListener() {

            RelativeLayout.LayoutParams parms;
            int startwidth;
            int startheight;
            float dx = 0, dy = 0, x = 0, y = 0;
            float angle = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final ImageView view = (ImageView) v;

                ((BitmapDrawable) view.getDrawable()).setAntiAlias(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        parms = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        startwidth = parms.width;
                        startheight = parms.height;
                        dx = event.getRawX() - parms.leftMargin;
                        dy = event.getRawY() - parms.topMargin;
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            mode = ZOOM;
                        }
                        d = rotation(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            x = event.getRawX();
                            y = event.getRawY();
                            parms.leftMargin = (int) (x - dx);
                            parms.topMargin = (int) (y - dy);

                            parms.rightMargin = 0;
                            parms.bottomMargin = 0;
                            parms.rightMargin = parms.leftMargin + (5 * parms.width);
                            parms.bottomMargin = parms.topMargin + (10 * parms.height);

                            Log.d("testing", "" + parms.leftMargin + " " + parms.rightMargin + " " + parms.width + " " + parms.topMargin + " " + parms.bottomMargin + " " + parms.height);


                            view.setLayoutParams(parms);
                        }
                        else if (mode == ZOOM) {
                            if (event.getPointerCount() == 2) {
                                newRot = rotation(event);
                                float r = newRot - d;
                                angle = r;

                                x = event.getRawX();
                                y = event.getRawY();

                                float newDist = spacing(event);
                                if (newDist > 10f) {
                                    float scale = newDist / oldDist * view.getScaleX();
                                    if (scale > 1.5 && scale < 6) {
                                        scalediff = scale;
                                        view.setScaleX(scale);
                                        view.setScaleY(scale);
                                    }
                                }

                                view.animate().rotationBy(angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();

                                x = event.getRawX();
                                y = event.getRawY();

                                parms.leftMargin = (int) ((x - dx) + scalediff);
                                parms.topMargin = (int) ((y - dy) + scalediff);

                                parms.rightMargin = 0;
                                parms.bottomMargin = 0;
                                parms.rightMargin = parms.leftMargin + (5 * parms.width);
                                parms.bottomMargin = parms.topMargin + (10 * parms.height);

                                view.setLayoutParams(parms);
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void init() {
        btn = (Button) findViewById(R.id.close_image_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) 0;//Math.toDegrees(radians);
    }
}
