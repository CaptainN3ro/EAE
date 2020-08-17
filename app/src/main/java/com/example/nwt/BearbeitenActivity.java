package com.example.nwt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BearbeitenActivity extends AppCompatActivity {

    Button zurueck, speichern;
    TextView name, staffeln, dienste;
    Serie s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(Html.fromHtml("<font color='#222222'>Bearbeiten</font>"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        name = findViewById(R.id.TEXT_NAME);
        staffeln = findViewById(R.id.TEXT_STAFFELN);
        dienste = findViewById(R.id.TEXT_DIENSTE);

        zurueck = findViewById(R.id.BUTTON_ZURUECK);
        speichern = findViewById(R.id.BUTTON_SPEICHERN);

        Intent intent = getIntent();

        if(intent.hasExtra("SERIE")) {
            s = (Serie) intent.getSerializableExtra("SERIE");
            name.setText(s.getName());
            staffeln.setText(s.getStaffeln() + "");
            String diensteText = "";
            for(int i = 0; i < s.getStreamingDienste().size(); i++) {
                if(i > 0) {
                    diensteText += ", ";
                }
                diensteText += s.getStreamingDienste().get(i).getAnzeigeName();
            }
            dienste.setText(diensteText);
        } else {
            cancel();
        }


        zurueck.setOnClickListener((v) -> {
            cancel();
        });
        speichern.setOnClickListener((v) -> {
            if(!validateTextfields()) {
                //Todo Show Error
                return;
            }
            updateSeriesData();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SERIE", s);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    private boolean validateTextfields() {
        if(name.getText().toString().trim().equals("")) {
            return false;
        }
        if(parseInt(staffeln.getText().toString()) < 0) {
            return false;
        }
        return true;
    }

    private void updateSeriesData() {
        s.setName(name.getText().toString());
        s.setStaffeln(parseInt(staffeln.getText().toString()));
        List<Dienst> diensteList = new ArrayList<>();
        for(String s: dienste.getText().toString().split(",")) {
            diensteList.add(new Dienst(s.trim()));
        }
        s.setStreamingDienste(diensteList);
    }

    private int parseInt(String text) {
        if(text.equals("")) {
            return 0;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private void cancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}