package com.pingan_us.eclaim;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class FileClaim3Activity extends AppCompatActivity {

    private Button confirm_btn;
    private Spinner auto_repair_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileclaim3);

        confirm_btn = (Button) findViewById(R.id.finish_step_button);
        auto_repair_spinner = (Spinner) findViewById(R.id.auto_repair_spinner);

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ClaimFinishActivity.class);
                startActivity(intent);
            }
        });
    }
}
