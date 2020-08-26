package com.example.nwt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nwt.model.Dienst;
import com.example.nwt.model.Serie;
import com.example.nwt.scraping.DataScraper;
import com.example.nwt.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HinzufuegenActivity extends AppCompatActivity {

    Switch autoloadSwitch;
    TextView nameBox, seasonBox, providerBox;
    LinearLayout layoutSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(Html.fromHtml("<font color='#222222'>Hinzufügen</font>"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hinzufuegen);

        Button addButton = findViewById(R.id.BUTTON_ADD);
        Button backButton = findViewById(R.id.BUTTON_CANCEL_ADD);
        TextView hintText = findViewById(R.id.TEXT_HINT);
        nameBox = findViewById(R.id.INPUT_NAME);
        seasonBox = findViewById(R.id.INPUT_SEASONS);
        providerBox = findViewById(R.id.INPUT_PROVIDERS);
        autoloadSwitch = findViewById(R.id.SWITCH_AUTOLOAD);
        layoutSelection = findViewById(R.id.LAYOUT_SELECTION);


        autoloadSwitch.setOnCheckedChangeListener((button, state) -> {
            if(state) {
                providerBox.setVisibility(View.GONE);
                seasonBox.setVisibility(View.GONE);
                hintText.setVisibility(View.VISIBLE);
                addButton.setText("Suchen");
                layoutSelection.setVisibility(View.VISIBLE);
            } else {
                providerBox.setVisibility(View.VISIBLE);
                seasonBox.setVisibility(View.VISIBLE);
                hintText.setVisibility(View.GONE);
                addButton.setText("Hinzufügen");
                layoutSelection.setVisibility(View.GONE);
            }
        });

        backButton.setOnClickListener((v) -> finish());
        addButton.setOnClickListener(this::returnData);
    }

    public void returnData(View v) {
        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        String name = nameBox.getText().toString();
        AtomicReference<List<String>> seriesNames = new AtomicReference<>();
        new Thread(() -> seriesNames.set(DataScraper.scrapeSerien(name, 5))).start();
        while(seriesNames.get() == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        layoutSelection.removeAllViews();
        for(int i = 0; i < seriesNames.get().size(); i++) {
            Button b = new Button(this);
            b.setText(seriesNames.get().get(i));
            int finalI = i;
            b.setOnClickListener((button) -> addSerieWithName(seriesNames.get().get(finalI)));
            layoutSelection.addView(b);
        }
    }

    private void addSerieWithName(String name) {
        Intent resultIntent = new Intent();
        boolean autoload = autoloadSwitch.isChecked();
        Serie s = new Serie(name);
        if(!autoload) {
            s.setStaffeln(Util.parseInt(seasonBox.getText().toString()));
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
}