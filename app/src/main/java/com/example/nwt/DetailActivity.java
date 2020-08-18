package com.example.nwt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    TextView name_inhalt, staffeln_inhalt, dienste_inhalt;
    Button bearbeiten, zurueck;
    Serie s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(Html.fromHtml("<font color='#222222'>Details</font>"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        name_inhalt = findViewById(R.id.INSERT_NAME);
        staffeln_inhalt = findViewById(R.id.INSERT_STAFFELN);
        dienste_inhalt = findViewById(R.id.INSERT_DIENSTE);
        bearbeiten = findViewById(R.id.DT_EDIT);
        zurueck = findViewById(R.id.DT_BACK);
        Intent intent = getIntent();

        if(intent.hasExtra("SERIE")) {
            s = (Serie) intent.getSerializableExtra("SERIE");
            name_inhalt.setText(s.getName());
            staffeln_inhalt.setText(s.getStaffeln() + "");
            String diensteText = "";
            for(int i = 0; i < s.getStreamingDienste().size(); i++) {
                if(i > 0) {
                    diensteText += ", ";
                }
                diensteText += s.getStreamingDienste().get(i).getAnzeigeName();
            }
            dienste_inhalt.setText(diensteText);
        } else {
            cancel();
        }
        zurueck.setOnClickListener((v) -> {
            cancel();
        });

        bearbeiten.setOnClickListener((view1) -> {
            Intent resultIntent = new Intent(this, BearbeitenActivity.class);
            resultIntent.putExtra("SERIE", s);
            startActivityForResult(resultIntent, 2);

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }



    private void cancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}