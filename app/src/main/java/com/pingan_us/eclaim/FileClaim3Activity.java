package com.pingan_us.eclaim;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.List;

public class FileClaim3Activity extends AppCompatActivity {
    private ClaimBundle claim;
    private Button confirm_btn;
    private Spinner auto_repair_spinner;
    private RelativeLayout background;
    private static FileClaim3Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileclaim3);

        background = (RelativeLayout) findViewById(R.id.fc3_background);

        claim = (ClaimBundle) getIntent().getParcelableExtra("ClaimBundle");

        List<byte[]> list = (List<byte[]>) getIntent().getSerializableExtra("morePictures");

        activity = this;

        confirm_btn = (Button) findViewById(R.id.finish_step_button);
        auto_repair_spinner = (Spinner) findViewById(R.id.auto_repair_spinner);

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setAlpha((float) 0.5);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);



                claim.uploadClaimBundle(getWindow(), getApplicationContext());
//                Intent intent = new Intent(getApplicationContext(), ClaimFinishActivity.class);
//                startActivity(intent);
            }
        });
    }

    public static FileClaim3Activity getInstance() {
        return activity;
    }

    @Override
    public void onBackPressed() {}
}
