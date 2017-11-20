package com.pingan_us.eclaim;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class photoActivity extends FragmentActivity {
    private RelativeLayout background;
    private ProgressBar progressBar;
    private Bitmap bmp;
    private ImageButton btn;
    private Intent intent;
    private Boolean isList;
    private String claimID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo);

        intent = getIntent();

        background = (RelativeLayout) findViewById(R.id.photo_background);
        progressBar = (ProgressBar) findViewById(R.id.photo_progressBar);

        background.setAlpha((float)0.7);

        isList = intent.getBooleanExtra("isList", false);
        claimID = intent.getStringExtra("claimData");
        //init();
    }

    private void init() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Claim");
        query.whereEqualTo("objectId", claimID);
        ParseObject object = null;
        byte[] bytes = null;
        try {
            object = query.getFirst();
        } catch (ParseException e) {
            finish();
        }

        if (!isList) {
            String imageName = intent.getStringExtra("imageData");
            ParseFile file = (ParseFile) object.get(imageName);
            try {
                bytes = file.getData();
            } catch (ParseException e) {
                finish();
            }
            background.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            int location = intent.getIntExtra("location", -1);
            if (location != -1) {
                List<String> byteList;
                try {
                    if (object.get("morePicturesID") != null) {
                        ImageGetterThread th = new ImageGetterThread((String) object.get("morePicturesID"));
                        Thread thread = new Thread(th);
                        thread.start();
                        try {
                            thread.join();
                            byteList = new ArrayList<String>(th.getList());
                            byte[] byteArray = Base64.decode(byteList.get(location), Base64.DEFAULT);
                            BitmapFactory.Options options1 = new BitmapFactory.Options();
                            bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options1);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                } catch (ClassCastException e2) {
                    e2.printStackTrace();
                }

                background.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        }



        SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) findViewById(R.id.zoom_image);
        imageView.setImage(ImageSource.bitmap(bmp));

        btn = (ImageButton) findViewById(R.id.zoom_close_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }
}
//        //byte[] imageByte = intent.getByteArrayExtra("imageData");
//        //bmp = Bitmap.createScaledBitmap(bmp1, (int)(bmp1.getWidth() * 0.9), (int)(bmp1.getHeight() * 0.9), true);
//        init();
//
//        layout = (RelativeLayout) findViewById(R.id.photo_layout);
//
//        Display display = getWindowManager().getDefaultDisplay();
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        display.getMetrics(outMetrics);
//
//        float density = getResources().getDisplayMetrics().density;
//        final float dpHeight = outMetrics.heightPixels/density;
//        final float dpWidth = outMetrics.widthPixels/density;
//
//        layout.setMinimumWidth((int)(dpWidth));
//        layout.setMinimumHeight((int)(dpHeight));
//        im_move_zoom_rotate = (ImageView) findViewById(R.id.zoom_image);
//        if(bmp != null)
//            im_move_zoom_rotate.setImageBitmap(bmp);
//        else
//            im_move_zoom_rotate.setImageDrawable(getResources().getDrawable(R.drawable.add));
//        //im_move_zoom_rotate.setScaleType(ImageView.ScaleType.CENTER);
//        im_move_zoom_rotate.setAdjustViewBounds(true);
//        im_move_zoom_rotate.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(dpWidth), (int)(dpHeight));
//
//        //layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//
//        layoutParams.leftMargin = 50;
//        layoutParams.topMargin = 50;
//        layoutParams.bottomMargin = 50;
//        layoutParams.rightMargin = 50;
//        im_move_zoom_rotate.setLayoutParams(layoutParams);
//        im_move_zoom_rotate.setScaleX((float)1.5);
//        im_move_zoom_rotate.setScaleY((float)1.5);
//        im_move_zoom_rotate.setOnTouchListener(new View.OnTouchListener() {
//
//            RelativeLayout.LayoutParams parms;
//            int width;
//            int height;
//            float rightY = 0;
//            float rightX = 0;
//            float dx = 0, dy = 0, x = 0, y = 0;
//            float angle = 0;
//            float moved = 0;
//            boolean zoomed = false;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                final ImageView view = (ImageView) v;
//
////                Display display = getWindowManager().getDefaultDisplay();
////                DisplayMetrics outMetrics = new DisplayMetrics ();
////                display.getMetrics(outMetrics);
////
////                float density  = getResources().getDisplayMetrics().density;
////                final float dpHeight = outMetrics.heightPixels / density;
////                final float dpWidth  = outMetrics.widthPixels / density;
////                final int xLimit = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
////                final int yLimit = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
//
//                ((BitmapDrawable) view.getDrawable()).setAntiAlias(true);
//
//                switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_DOWN:
//                        zoomed = false;
//                        //Log.d("touch down", event.getX() + " " + event.getY() + " " + event.getRawX() + " " + event.getRawY());
//                        parms = (RelativeLayout.LayoutParams) view.getLayoutParams();
//                        //width = v.getMeasuredWidth();
//                        //height = v.getMeasuredHeight();
//                        rightX = event.getRawX();
//                        rightY = event.getRawY();
//                        dx = event.getRawX() - parms.leftMargin;
//                        dy = event.getRawY() - parms.topMargin;
//                        //rightY = parms.topMargin + height;
//                        //rightX = parms.leftMargin + width;
//                        mode = DRAG;
//                        break;
//                    case MotionEvent.ACTION_POINTER_DOWN:
//                        zoomed = true;
//                        oldDist = spacing(event);
//                        if (oldDist > 10f) {
//                            mode = ZOOM;
//                        }
//                        d = rotation(event);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        if(moved  > MIN_DISTANCE) {
//                            moved = 0;
//                        }
//                        else if(!zoomed){
//                            Log.d("click", "x " + event.getX() + " rx " + event.getRawX() + " y " + event.getY() + " ry " + event.getRawY());
//                            finish();
//                        }
//                        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
////                        RelativeLayout root = (RelativeLayout) findViewById(R.id.photo_layout);
////                        DisplayMetrics dm = new DisplayMetrics();
////                        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
////                        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();
////
////                        int originalPos[] = new int[2];
////                        view.getLocationOnScreen( originalPos );
////
////                        int xDest = dm.widthPixels/2;
////                        xDest -= (view.getMeasuredWidth()/2);
////                        int yDest = dm.heightPixels/2 - (view.getMeasuredHeight()/2) - statusBarOffset;
//
////                        float xDest = dpWidth/2 - v.getWidth()*scale/2;
////                        float yDest = dpHeight/2 - v.getHeight()*scale/2;
////
////                        ObjectAnimator moveX = ObjectAnimator.ofFloat(v, "translationX", realX, xDest);
////                        ObjectAnimator moveY = ObjectAnimator.ofFloat(v, "translationY", realY, yDest);
////
////                        if(((realY + height*scale) >= dpHeight && realY >=0) || ((realX + width*scale) >= dpWidth && realX >= 0) ||
////                                ((realY + height*scale) <= dpHeight && realY <= 0) || ((realX + width*scale) <= dpWidth && realX <= 0)) {
////                            AnimatorSet animSet = new AnimatorSet();
////                            animSet.play(moveX).with(moveY);
////                            animSet.setDuration(500);
////                            animSet.start();
////                        }
//                        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                        break;
//                    case MotionEvent.ACTION_POINTER_UP:
//                        mode = NONE;
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        if (mode == DRAG) {
//                            x = event.getRawX();
//                            y = event.getRawY();
//                            float xt = event.getX();
//                            float yt = event.getY();
//                            moved+=Math.sqrt(Math.pow(x-rightX, 2) + Math.pow(y-rightY, 2));
//                            parms.leftMargin = (int) (x - dx);
//                            parms.topMargin = (int) (y - dy);
//
//                            parms.rightMargin = 0;
//                            parms.bottomMargin = 0;
//                            //parms.rightMargin = parms.leftMargin + (5 * parms.width);
//                            //parms.bottomMargin = parms.topMargin + (10 * parms.height);
//
//
//                            if (parms.topMargin < 0) parms.topMargin = 0;
//                            if (parms.topMargin + height > dpHeight) parms.topMargin = (int)dpHeight - height;
//                            if (parms.leftMargin < 0) parms.leftMargin = 0;
//                            if (parms.leftMargin + width > dpWidth) parms.leftMargin = (int)dpWidth - width;
//
//
//                            //realX = x + width/2 - width*scale/2;
//                            //realY = y + height/2 - height*scale/2;
//
//                            //Log.d("debug", "xL " + dpWidth + " yL " + dpHeight + " x " + x + "/" + xt + " y " + y + "/" + yt + " l " + parms.leftMargin + " t " + parms.topMargin + " w " + width + " h " + height );
//
//                            view.setLayoutParams(parms);
//                        }
//                        else if (mode == ZOOM) {
//                            if (event.getPointerCount() == 2) {
//                                newRot = rotation(event);
//                                float r = newRot - d;
//                                angle = r;
//
//                                x = event.getRawX();
//                                y = event.getRawY();
//
//                                float newDist = spacing(event);
//                                if (newDist > 10f) {
//                                    float scale = newDist / oldDist * view.getScaleX();
//                                    if (scale > 0.5 && scale < 6) {
//                                        scalediff = scale;
//                                        view.setScaleX(scale);
//                                        view.setScaleY(scale);
//                                    }
//                                }
//
//                                view.animate().rotationBy(angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();
//
//                                x = event.getRawX();
//                                y = event.getRawY();
//
//                                parms.leftMargin = (int) ((x - dx) + scalediff);
//                                parms.topMargin = (int) ((y - dy) + scalediff);
//
//                                parms.rightMargin = 0;
//                                parms.bottomMargin = 0;
//                                parms.rightMargin = parms.leftMargin + (5 * parms.width);
//                                parms.bottomMargin = parms.topMargin + (10 * parms.height);
//
//                                //rightY = parms.topMargin + height;
//                                //rightX = parms.leftMargin + width;
//
//                                view.setLayoutParams(parms);
//                            }
//                        }
//                        break;
//                }
//                return true;
//            }
//        });
//    }
//
//    private void init() {
//    }
//
//    private float spacing(MotionEvent event) {
//        float x = event.getX(0) - event.getX(1);
//        float y = event.getY(0) - event.getY(1);
//        return (float) Math.sqrt(x * x + y * y);
//    }
//
//    private float rotation(MotionEvent event) {
//        double delta_x = (event.getX(0) - event.getX(1));
//        double delta_y = (event.getY(0) - event.getY(1));
//        double radians = Math.atan2(delta_y, delta_x);
//        return (float) 0;//Math.toDegrees(radians);
//    }
//}
