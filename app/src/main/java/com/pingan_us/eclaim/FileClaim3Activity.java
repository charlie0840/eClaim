package com.pingan_us.eclaim;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FileClaim3Activity extends AppCompatActivity implements View.OnClickListener{

    private List<String> repairList = new ArrayList<>();

    private ClaimBundle claim;
    private ImageView cancel_btn;
    private ProgressBar progressBar;
    private RelativeLayout background;
    private Spinner auto_repair_spinner;
    private Button confirm_btn, back_btn;
    private static FileClaim3Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fileclaim3);

        progressBar = (ProgressBar) findViewById(R.id.fc3_progressBar);
        background = (RelativeLayout) findViewById(R.id.fc3_background);
        progressBar.setVisibility(View.GONE);

        claim = (ClaimBundle) getIntent().getParcelableExtra("ClaimBundle");

        activity = this;

        back_btn = (Button) findViewById(R.id.step3_back_button);
        confirm_btn = (Button) findViewById(R.id.finish_step_button);
        cancel_btn = (ImageView) findViewById(R.id.fc3_cancel_button);

        auto_repair_spinner = (Spinner) findViewById(R.id.auto_repair_spinner);

        repairList.add("ATS Mobile Bumper Repair");
        repairList.add("Collision Consultants Auto Repair");
        repairList.add("Legit Automitive");
        repairList.add("Rick's Auto Body");
        repairList.add("Yosemite Auto Body Shop");

        //ClaimCustomList adapter = new ClaimCustomList(FileClaim3Activity.this, repairList);
        //auto_repair_spinner.setAdapter(adapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, repairList);
        auto_repair_spinner.setAdapter(adapter);

        back_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        confirm_btn.setOnClickListener(this);

        doAnimation();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.finish_step_button:
                background.setAlpha((float) 0.5);
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                claim.uploadClaimBundle(getWindow(), getApplicationContext());
                break;
            case R.id.step3_back_button:
                deleteImageList(false);
                Intent intent = new Intent(this, FileClaim2Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                break;
            case R.id.fc3_cancel_button:
                deleteImageList(true);
                break;
        }
    }

    public static FileClaim3Activity getInstance() {
        return activity;
    }

    public void deleteImageList(final boolean isCancel) {
        String id = claim.getImageListID();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ImageList");
        query.whereEqualTo("ObjectId", id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null)
                    try {
                        object.delete();
                        if(isCancel) {
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
            }
        });
    }

    public void doAnimation() {
        RelativeLayout scrollView = findViewById(R.id.fc3_view);

        final Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_in);
        final Animation alpha = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        scrollView.startAnimation(slideUp);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                back_btn.setVisibility(View.VISIBLE);
                confirm_btn.setVisibility(View.VISIBLE);
                back_btn.startAnimation(alpha);
                confirm_btn.startAnimation(alpha);
            }
        }, 1000);
    }
}
