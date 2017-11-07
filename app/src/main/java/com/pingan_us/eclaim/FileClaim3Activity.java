package com.pingan_us.eclaim;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class FileClaim3Activity extends AppCompatActivity implements View.OnClickListener{
    private ClaimBundle claim;
    private ProgressBar progressBar;
    private RelativeLayout background;
    private Spinner auto_repair_spinner;
    private Button confirm_btn, cancel_btn;
    private static FileClaim3Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileclaim3);

        progressBar = (ProgressBar) findViewById(R.id.fc3_progressBar);
        background = (RelativeLayout) findViewById(R.id.fc3_background);
        progressBar.setVisibility(View.GONE);

        claim = (ClaimBundle) getIntent().getParcelableExtra("ClaimBundle");

        activity = this;

        confirm_btn = (Button) findViewById(R.id.finish_step_button);
        cancel_btn = (Button) findViewById(R.id.step3_cancel_button);

        auto_repair_spinner = (Spinner) findViewById(R.id.auto_repair_spinner);

        cancel_btn.setOnClickListener(this);
        confirm_btn.setOnClickListener(this);
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
            case R.id.step3_cancel_button:
                deleteImageList();
                break;
        }
    }

    public static FileClaim3Activity getInstance() {
        return activity;
    }

    @Override
    public void onBackPressed() {}

    public void deleteImageList() {
        String id = claim.getImageListID();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ImageList");
        query.whereEqualTo("ObjectId", id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null)
                    try {
                        object.delete();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
            }
        });
    }
}
