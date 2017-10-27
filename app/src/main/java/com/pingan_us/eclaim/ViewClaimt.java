package com.pingan_us.eclaim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ViewClaimt extends AppCompatActivity {
    private int pos;

    private List<String> claimList;

    private ListView claim_list;
    private ClaimCustomList adapter;
    private CheckBox injure_cb, drivable_cb, atScene_cb;
    private ImageView driver_license_pic, other_license_pic, other_insurance_pic;
    private TextView vehicleNum_txt, time_txt, loc_txt, vehicleType_txt,
                whoDrive_txt, phoneOfOther_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewclaimt);

        claim_list = (ListView) findViewById(R.id.vc_claim_list);

        driver_license_pic = (ImageView) findViewById(R.id.vc_person_license_pic);
        other_license_pic = (ImageView) findViewById(R.id.vc_other_driver_license_pic);
        other_insurance_pic = (ImageView) findViewById(R.id.vc_other_insurance_card_pic);

        injure_cb = (CheckBox) findViewById(R.id.vc_injure_checkbox);
        atScene_cb = (CheckBox) findViewById(R.id.vc_atscene_checkbox);
        drivable_cb = (CheckBox) findViewById(R.id.vc_drivable_checkbox);

        time_txt = (TextView) findViewById(R.id.vc_time_text);
        loc_txt = (TextView) findViewById(R.id.vc_loc_indicate_text);
        whoDrive_txt = (TextView) findViewById(R.id.vc_person_pick_text);
        vehicleNum_txt = (TextView) findViewById(R.id.vc_vehicle_num_text);
        vehicleType_txt = (TextView) findViewById(R.id.vc_vehicle_pick_text);
        phoneOfOther_txt = (TextView) findViewById(R.id.vc_other_driver_phone_text);

        claimList = new ArrayList<String>();

        claimList.add("claim 1");
        claimList.add("claim 2");

        adapter = new ClaimCustomList(ViewClaimt.this, claimList);
        claim_list.setAdapter(adapter);
        claim_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(ViewClaimt.this, "position " + position + " You Clicked at " +claimList.get(+ position) + " position " + position + " size " + claimList.size(), Toast.LENGTH_SHORT).show();
                pos = position;
                fillClaim();
            }
        });


    }

    public void fillClaim() {
        Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
    }
}
