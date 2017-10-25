package com.pingan_us.eclaim;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ClaimFinishActivity extends AppCompatActivity {

    private Button finish_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claimfinish);

        finish_btn = (Button) findViewById(R.id.back_to_profile_button);

        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                FileClaim1Activity.getInstance().finish();
                FileClaim2Activity.getInstance().finish();
                FileClaim3Activity.getInstance().finish();
                finish();
            }
        });
    }
}
