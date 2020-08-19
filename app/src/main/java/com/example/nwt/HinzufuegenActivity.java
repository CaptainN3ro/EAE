package com.example.nwt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HinzufuegenActivity extends AppCompatActivity {

    Button addButton, backButton;
    Switch autoloadSwitch;
    TextView nameBox, seasonBox, providerBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(Html.fromHtml("<font color='#222222'>Hinzufügen</font>"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        addButton = findViewById(R.id.BUTTON_ADD);
        nameBox = findViewById(R.id.INPUT_NAME);
        seasonBox = findViewById(R.id.INPUT_SEASONS);
        providerBox = findViewById(R.id.INPUT_PROVIDERS);
        backButton = findViewById(R.id.BUTTON_CANCEL_ADD);

        autoloadSwitch = findViewById(R.id.SWITCH_AUTOLOAD);

        autoloadSwitch.setOnCheckedChangeListener((button, state) -> {
            if(state) {
                providerBox.setVisibility(View.GONE);
                seasonBox.setVisibility(View.GONE);
            } else {
                providerBox.setVisibility(View.VISIBLE);
                seasonBox.setVisibility(View.VISIBLE);
            }
        });

        backButton.setOnClickListener((v) -> finish());
        addButton.setOnClickListener(this::returnData);
    }

    public void returnData(View v) {
        Intent resultIntent = new Intent();
        String name = nameBox.getText().toString();
        boolean autoload = autoloadSwitch.isChecked();
        Serie s = new Serie(name);
        if(!autoload) {
            s.setStaffeln(parseInt(seasonBox.getText().toString()));
            List<Dienst> dienste = new ArrayList<>();
            String[] parts = providerBox.getText().toString().split(",");
            for(String dienst: parts) {
                if(dienst.trim().equals("")) {
                    continue;
                }
                dienste.add(new Dienst(dienst.trim()));
            }
            s.setStreamingDienste(dienste);
        }
        resultIntent.putExtra("SERIE", s);
        resultIntent.putExtra("AUTOLOAD", autoload);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
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
}