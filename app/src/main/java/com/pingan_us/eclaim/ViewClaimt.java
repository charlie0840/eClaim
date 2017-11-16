package com.pingan_us.eclaim;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ViewClaimt extends AppCompatActivity implements View.OnClickListener{

    private static final int WIDTH = 200, HEIGHT = 120;

    private int pos, limit = 5;
    private List<Bitmap> picList = new ArrayList<Bitmap>();
    private List<String> byteList = new ArrayList<>();
    private List<String> claimList, claimIDList = new ArrayList<String>(), claimIDASCList = new ArrayList<>();
    private boolean noClaim = true, slideIn = true, isFirstPage = true;

    private ImageView imHide;
    private ClaimCustomList adapter;
    private ProgressBar progressBar;
    private CustomListt adapter_pic;
    private LinearLayout refresh_btn;
    private Button prev_btn, next_btn;
    private ListView claim_list, pic_list;
    private CheckBox injure_cb, drivable_cb, atScene_cb;
    private RelativeLayout list_section, claim_section, background,
            other_driver_section;
    private TextView vehicleNum_txt, time_txt, loc_txt, vehicleType_txt,
            whoDrive_txt, phoneOfOther_txt, claim_list_title;
    private ImageView driver_license_pic, other_license_pic, other_insurance_pic,
            whole_scene_pic, your_plate_pic, other_plate_pic;
    private static ViewClaimt activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewclaimt);

        progressBar = (ProgressBar) findViewById(R.id.vc_progressBar);
        background = (RelativeLayout) findViewById(R.id.vc_background1);
        progressBar.setVisibility(View.GONE);

        claim_list = (ListView) findViewById(R.id.vc_claim_list);
        pic_list = (ListView) findViewById(R.id.vc_photo_list);
        refresh_btn = (LinearLayout) findViewById(R.id.vc_refresh_btn);
        prev_btn = (Button) findViewById(R.id.vc_prev_btn);
        next_btn = (Button) findViewById(R.id.vc_next_btn);
        refresh_btn.setOnClickListener(this);
        prev_btn.setOnClickListener(this);
        next_btn.setOnClickListener(this);

        View nav_bar = findViewById(R.id.nav_layout);

        RelativeLayout home_nav = (RelativeLayout) nav_bar.findViewById(R.id.home_nav);
        RelativeLayout profile_nav = (RelativeLayout) nav_bar.findViewById(R.id.profile_nav);
        home_nav.setOnClickListener(this);
        profile_nav.setOnClickListener(this);

        list_section = (RelativeLayout) findViewById(R.id.vc_list_section);
        claim_section = (RelativeLayout) findViewById(R.id.vc_claim_section);
        other_driver_section = (RelativeLayout) findViewById(R.id.vc_other_driver_section);
        claim_list_title = (TextView) findViewById(R.id.vc_claim_list_title);
        imHide  = (ImageView) findViewById(R.id.vc_drawer_btn);
        claim_list.setVisibility(View.GONE);
        refresh_btn.setVisibility(View.GONE);
        claim_list_title.setVisibility(View.GONE);
        imHide.setImageResource(R.drawable.drawerout);
        final RelativeLayout vc_background = (RelativeLayout) findViewById(R.id.vc_background);
        final Animation leftIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_left_in);
        final Animation rightOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_right_in);
        imHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if(slideIn) {
                    slideIn = false;
                    list_section.startAnimation(leftIn);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            claim_list.setVisibility(View.GONE);
                            refresh_btn.setVisibility(View.GONE);
                            claim_list_title.setVisibility(View.GONE);
                            imHide.setImageResource(R.drawable.drawerout);
                            vc_background.setVisibility(View.INVISIBLE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }, 1000);
                }
                else {
                    slideIn = true;
                    vc_background.setVisibility(View.VISIBLE);
                    claim_list.setVisibility(View.VISIBLE);
                    refresh_btn.setVisibility(View.VISIBLE);
                    claim_list_title.setVisibility(View.VISIBLE);
                    list_section.startAnimation(rightOut);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imHide.setImageResource(R.drawable.drawerback);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }, 1000);
                }
                progressBar.setVisibility(View.GONE);
                background.setAlpha((float) 0);
            }
        });
        slideIn = false;
        imHide.performClick();


        driver_license_pic = (ImageView) findViewById(R.id.vc_person_license_pic);
        whole_scene_pic = (ImageView) findViewById(R.id.vc_whole_scene_pic);
        your_plate_pic = (ImageView) findViewById(R.id.vc_your_plate_pic);
        other_plate_pic = (ImageView) findViewById(R.id.vc_other_plate_pic);
        other_license_pic = (ImageView) findViewById(R.id.vc_other_driver_license_pic);
        other_insurance_pic = (ImageView) findViewById(R.id.vc_other_insurance_card_pic);

        driver_license_pic.setOnClickListener(this);
        whole_scene_pic.setOnClickListener(this);
        your_plate_pic.setOnClickListener(this);
        other_plate_pic.setOnClickListener(this);
        other_license_pic.setOnClickListener(this);
        other_insurance_pic.setOnClickListener(this);

        injure_cb = (CheckBox) findViewById(R.id.vc_injure_checkbox);
        atScene_cb = (CheckBox) findViewById(R.id.vc_atscene_checkbox);
        drivable_cb = (CheckBox) findViewById(R.id.vc_drivable_checkbox);

        injure_cb.setClickable(false);
        atScene_cb.setClickable(false);
        drivable_cb.setClickable(false);

        time_txt = (TextView) findViewById(R.id.vc_time_text);
        loc_txt = (TextView) findViewById(R.id.vc_loc_indicate_text);
        whoDrive_txt = (TextView) findViewById(R.id.vc_person_pick_text);
        vehicleNum_txt = (TextView) findViewById(R.id.vc_vehicle_num_text);
        vehicleType_txt = (TextView) findViewById(R.id.vc_vehicle_pick_text);
        phoneOfOther_txt = (TextView) findViewById(R.id.vc_other_driver_phone_text);

        claimList = new ArrayList<String>();

        adapter_pic = new CustomListt(ViewClaimt.this, picList);

        pic_list.setAdapter(adapter_pic);

        pic_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int loc = 0;
                Intent intent = new Intent(getApplicationContext(), photoActivity.class);
                intent.putExtra("isList", true);
                intent.putExtra("claimData", claimIDList.get(pos));
                if(isFirstPage) {
                    //Bitmap bmp = picList.get(position);
                    //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    //byte[] byteArray = stream.toByteArray();
                    intent.putExtra("location", position);
                    //intent.putExtra("imageByte",byteArray);
                    startActivity(intent);
                }
                else if(pic_list.getChildCount() != 0) {
                    loc = position + 5;
                    //Bitmap bmp = picList.get(loc);
                    //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    //byte[] byteArray = stream.toByteArray();
                    intent.putExtra("location", loc);
                    //intent.putExtra("imageByte",byteArray);
                    startActivity(intent);
                }
            }
        });

        adapter = new ClaimCustomList(ViewClaimt.this, claimList);
        claim_list.setAdapter(adapter);

        claim_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int num_of_visible_view = claim_list.getLastVisiblePosition() - claim_list.getFirstVisiblePosition();
                int first_loc = claim_list.getFirstVisiblePosition();
                int last_loc = claim_list.getLastVisiblePosition();
                if(pos >= first_loc && pos <= last_loc) {
                    claim_list.getChildAt(pos - first_loc).setBackgroundColor(getResources().getColor(R.color.colorBrightGreen));
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });

        claim_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int num_of_visible_view = claim_list.getLastVisiblePosition() - claim_list.getFirstVisiblePosition();
                int first_loc = claim_list.getFirstVisiblePosition();
                for(int i = 0; i <= num_of_visible_view; i++) {
                    claim_list.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.colorWhite));
                }
                claim_list.getChildAt(position - first_loc).setBackgroundColor(getResources().getColor(R.color.colorBrightGreen));
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                pos = position;
                picList.clear();
                adapter_pic.notifyDataSetChanged();
                fillClaim(position);
                background.setAlpha((float) 0.5);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        getClaimList();

        doAnimation();

        activity = this;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.vc_whole_scene_pic:
                openZoom(R.id.vc_whole_scene_pic);
                break;
            case R.id.vc_your_plate_pic:
                openZoom(R.id.vc_your_plate_pic);
                break;
            case R.id.vc_other_plate_pic:
                openZoom(R.id.vc_other_plate_pic);
                break;
            case R.id.vc_other_driver_license_pic:
                openZoom(R.id.vc_other_driver_license_pic);
                break;
            case R.id.vc_other_insurance_card_pic:
                openZoom(R.id.vc_other_insurance_card_pic);
                break;
            case R.id.vc_person_license_pic:
                openZoom(R.id.vc_person_license_pic);
                break;
            case R.id.home_nav:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                claim_list.setVisibility(View.GONE);
                refresh_btn.setVisibility(View.GONE);
                claim_list_title.setVisibility(View.GONE);
                finish();
                break;
            case R.id.profile_nav:
                background.setAlpha((float) 0.5);
                Intent intent1 = new Intent(this, ProfileActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                break;
            case R.id.vc_refresh_btn:
                getClaimList();
                break;
            case R.id.vc_next_btn:
                isFirstPage = false;
                loadMorePictures(limit, 9);
                break;
            case R.id.vc_prev_btn:
                isFirstPage = true;
                loadMorePictures(0, limit);
                break;
        }
    }

    public void openZoom(int id) {
        String picToGet = "";
        switch(id) {
            case R.id.vc_whole_scene_pic:
                picToGet = "wholeScene";
                break;
            case R.id.vc_your_plate_pic:
                picToGet = "yourPlate";
                break;
            case R.id.vc_other_plate_pic:
                picToGet = "otherPlate";
                break;
            case R.id.vc_other_driver_license_pic:
                picToGet = "otherLicense";
                break;
            case R.id.vc_other_insurance_card_pic:
                picToGet = "otherInsurance";
                break;
            case R.id.vc_person_license_pic:
                picToGet = "driverLicense";
                break;
        }
        Intent intent = new Intent(getApplicationContext(), photoActivity.class);
        intent.putExtra("isList", false);
        intent.putExtra("imageData", picToGet);
        intent.putExtra("claimData", claimIDList.get(pos));
        startActivity(intent);
    }

    public void fillClaim(int position) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Claim");
        query.whereEqualTo("objectId", claimIDList.get(position));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> currClaims, ParseException e) {
                if(e == null) {
                    if(currClaims.size() == 0)
                        return;
                    ParseObject currClaim = currClaims.get(0);
                    boolean injured = (boolean)currClaim.get("injured");
                    boolean drivable = (boolean)currClaim.get("drivable");
                    boolean atScene = (boolean)currClaim.get("atScene");
                    boolean person = (boolean)currClaim.get("person");
                    boolean multiVehicle =false;

                    String vehicleNum = (String)currClaim.get("vehicleNum");
                    if(vehicleNum.equals("1")) {
                        multiVehicle = false;
                    }
                    else
                        multiVehicle = true;
                    String time = (String)currClaim.get("time");
                    String location = (String)currClaim.get("location");
                    String phone = (String)currClaim.get("phoneOther");

                    if(multiVehicle) {
                        other_driver_section.setVisibility(View.VISIBLE);
                        byte[] otherInsurByte = new byte[0];
                        try {
                            otherInsurByte = ((ParseFile) currClaim.get("otherInsurance")).getData();
                            fillImageView(otherInsurByte, other_insurance_pic);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        byte[] otherLicenseByte = new byte[0];
                        try {
                            otherLicenseByte = ((ParseFile) currClaim.get("otherLicense")).getData();
                            fillImageView(otherLicenseByte, other_license_pic);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    else {
                        other_driver_section.setVisibility(View.GONE);
                    }
                    byte[] driverLicenseByte = new byte[0];
                    if(!person) {
                        try {
                            driverLicenseByte = ((ParseFile) currClaim.get("driverLicense")).getData();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    byte[] wholeSceneByte = new byte[0];
                    try {
                        if(currClaim.get("wholeScene") != null) {
                            wholeSceneByte = ((ParseFile) currClaim.get("wholeScene")).getData();
                            fillImageView(wholeSceneByte, whole_scene_pic);
                        }
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    byte[] yourPlateByte = new byte[0];
                    try {
                        if(currClaim.get("yourPlate") != null) {
                            yourPlateByte = ((ParseFile) currClaim.get("yourPlate")).getData();
                            fillImageView(yourPlateByte, your_plate_pic);
                        }
                        } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    byte[] otherPlateByte = new byte[0];
                    try {
                        if(currClaim.get("otherPlate") != null) {
                            otherPlateByte = ((ParseFile) currClaim.get("otherPlate")).getData();
                            fillImageView(otherPlateByte, other_plate_pic);
                        }
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        background.setAlpha((float) 0);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                    byteList = new ArrayList<String>();
                    try {
                        if(currClaim.get("morePicturesID") != null) {
                            ImageGetterThread th = new ImageGetterThread((String)currClaim.get("morePicturesID"));
                            Thread thread = new Thread(th);
                            thread.start();
                            try {
                                thread.join();
                                byteList = new ArrayList<String>(th.getList());
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (ClassCastException e2) {
                        e2.printStackTrace();
                        background.setAlpha((float) 0);
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                    injure_cb.setChecked(injured);
                    drivable_cb.setChecked(drivable);
                    atScene_cb.setChecked(atScene);
                    vehicleNum_txt.setText(vehicleNum);
                    time_txt.setText(time);
                    loc_txt.setText(location);
                    phoneOfOther_txt.setText(phone);

                    if(person) {
                        whoDrive_txt.setText("I");
                        driver_license_pic.setVisibility(View.GONE);
                    }
                    else{
                        driver_license_pic.setVisibility(View.VISIBLE);
                        whoDrive_txt.setText("other");
                        fillImageView(driverLicenseByte, driver_license_pic);
                    }
                    loadMorePictures(0, limit);
                }
            }
        });
    }

    public void loadMorePictures(int start, int end) {
        picList.clear();
        if(byteList.size() > start) {
            for (int i = start; i < end; i++) {
                if (i == byteList.size())
                    break;
                byte[] bytes = Base64.decode(byteList.get(i), Base64.DEFAULT);
                BitmapFactory.Options options1 = new BitmapFactory.Options();
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options1);
                Bitmap finalBmp = Bitmap.createScaledBitmap(bmp, 240, 180, true);
                bmp.recycle();
                picList.add(finalBmp);
            }
        }
        adapter_pic.notifyDataSetChanged();

        background.setAlpha((float) 0);
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void fillImageView(byte[] bytes, ImageView view) {
        Bitmap bmp;
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options1);
        Bitmap finalBmp = Bitmap.createScaledBitmap(bmp, 240, 180, true);
        bmp.recycle();
        view.setImageBitmap(finalBmp);
    }

    public void getClaimList() {
        ParseUser currUser = ParseUser.getCurrentUser();
        claimIDList = new ArrayList<>();
        claimIDASCList = new ArrayList<>();
        if(currUser.get("claimID") != null) {
            try {
                claimIDList = new ArrayList<String>((List<String>) currUser.get("claimID"));
                for(int i = 0; i < claimIDList.size(); i++) {
                    String curr = "";
                    for(char c:claimIDList.get(i).toCharArray())
                        curr+=Integer.toString((int)c);
                    claimIDASCList.add(curr);
                }
            }
            catch (ClassCastException e) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressBar.setVisibility(View.GONE);
                background.setAlpha((float) 0);
            }
        }
        if(claimIDList.size() != 0)
            noClaim = false;
        claimList.clear();
        adapter.notifyDataSetInvalidated();
        for(int i = 0; i < claimIDASCList.size(); i++) {
            claimList.add("NO: " + claimIDASCList.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    public static ViewClaimt getInstance() {
        return activity;
    }

    public void doAnimation() {
        slideIn = false;
        imHide.performClick();
    }

    @Override
    public void onBackPressed() {}
}
