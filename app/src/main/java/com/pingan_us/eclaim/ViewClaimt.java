package com.pingan_us.eclaim;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ViewClaimt extends AppCompatActivity implements View.OnClickListener{
    private int pos;
    private boolean noClaim = true;
    private List<String> claimList, claimIDList = new ArrayList<String>();

    private ListView claim_list;
    private ClaimCustomList adapter;
    private LinearLayout refresh_btn;
    private CheckBox injure_cb, drivable_cb, atScene_cb;
    private ImageView driver_license_pic, other_license_pic, other_insurance_pic;
    private TextView vehicleNum_txt, time_txt, loc_txt, vehicleType_txt,
                whoDrive_txt, phoneOfOther_txt;
    private static ViewClaimt activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewclaimt);

        claim_list = (ListView) findViewById(R.id.vc_claim_list);
        refresh_btn = (LinearLayout) findViewById(R.id.vc_refresh_btn);
        refresh_btn.setOnClickListener(this);

        View nav_bar = findViewById(R.id.nav_layout);

        ImageView home_nav = (ImageView) nav_bar.findViewById(R.id.home_nav);
        ImageView profile_nav = (ImageView) nav_bar.findViewById(R.id.profile_nav);
        home_nav.setOnClickListener(this);
        profile_nav.setOnClickListener(this);

        driver_license_pic = (ImageView) findViewById(R.id.vc_person_license_pic);
        other_license_pic = (ImageView) findViewById(R.id.vc_other_driver_license_pic);
        other_insurance_pic = (ImageView) findViewById(R.id.vc_other_insurance_card_pic);

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


        adapter = new ClaimCustomList(ViewClaimt.this, claimList);
        claim_list.setAdapter(adapter);
        claim_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(ViewClaimt.this, "position " + position + " You Clicked at " +claimList.get(+ position) + " position " + position + " size " + claimList.size(), Toast.LENGTH_SHORT).show();
                pos = position;
                fillClaim(position);
            }
        });
        getClaimList();

        activity = this;

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.home_nav:
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.profile_nav:
                Intent intent1 = new Intent(this, ProfileActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent1);
                break;
            case R.id.vc_refresh_btn:
                getClaimList();
                break;
        }
    }

    public void fillClaim(int position) {
        Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Claim");
        query.whereEqualTo("objectId", claimIDList.get(position));
        Toast.makeText(getApplicationContext(), "Looking for " + claimIDList.get(position), Toast.LENGTH_LONG).show();
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

                    String vehicleNum = (String)currClaim.get("vehicleNum");
                    String time = (String)currClaim.get("time");
                    String location = (String)currClaim.get("location");
                    String phone = (String)currClaim.get("phoneOther");

                    byte[] otherInsurByte = new byte[0];
                    try {
                        otherInsurByte = ((ParseFile)currClaim.get("otherInsurance")).getData();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    byte[] otherLicenseByte = new byte[0];
                    try {
                        otherLicenseByte = ((ParseFile)currClaim.get("otherLicense")).getData();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    byte[] driverLicenseByte = new byte[0];
                    if(!person) {
                        try {
                            driverLicenseByte = ((ParseFile) currClaim.get("driverLicense")).getData();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
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
                        Bitmap bmp = null;
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        bmp = BitmapFactory.decodeByteArray(driverLicenseByte, 0, driverLicenseByte.length, options);
                        Bitmap finalBmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth()/2, bmp.getHeight()/2, true);
                        bmp.recycle();
                        driver_license_pic.setImageBitmap(finalBmp);
                    }

                    Bitmap bmp1 = null;
                    BitmapFactory.Options options1 = new BitmapFactory.Options();
                    bmp1 = BitmapFactory.decodeByteArray(otherLicenseByte, 0, otherLicenseByte.length, options1);
                    Bitmap finalBmp1 = Bitmap.createScaledBitmap(bmp1, bmp1.getWidth()/2, bmp1.getHeight()/2, true);
                    bmp1.recycle();
                    other_license_pic.setImageBitmap(finalBmp1);

                    Bitmap bmp2 = null;
                    BitmapFactory.Options options2 = new BitmapFactory.Options();
                    bmp2 = BitmapFactory.decodeByteArray(otherInsurByte, 0, otherInsurByte.length, options2);
                    Bitmap finalBmp2 = Bitmap.createScaledBitmap(bmp2, bmp2.getWidth()/2, bmp2.getHeight()/2, true);
                    bmp2.recycle();
                    other_insurance_pic.setImageBitmap(finalBmp2);

                }
            }
        });
    }

    public void getClaimList() {
        ParseUser currUser = ParseUser.getCurrentUser();
        claimIDList = new ArrayList<String>();
        if(currUser.get("claimID") != null) {
            try {
                claimIDList = new ArrayList<String>((List<String>) currUser.get("claimID"));
            }
            catch (ClassCastException e) {

            }
        }
        if(claimIDList.size() != 0)
            noClaim = false;
        claimList.clear();
        for(int i = 0; i < claimIDList.size(); i++) {
            claimList.add("claim " + Integer.toString(i + 1));
        }
        adapter.notifyDataSetChanged();
    }

    public static ViewClaimt getInstance() {
        return activity;
    }
}
