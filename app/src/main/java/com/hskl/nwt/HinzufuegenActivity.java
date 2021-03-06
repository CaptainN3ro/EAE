package com.hskl.nwt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.hskl.nwt.model.Dienst;
import com.hskl.nwt.model.Serie;
import com.hskl.nwt.scraping.DataScraper;
import com.hskl.nwt.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HinzufuegenActivity extends AppCompatActivity {

    Switch autoloadSwitch;
    TextView nameBox, seasonBox, providerBox, runtimeBox;
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
        runtimeBox = findViewById(R.id.INPUT_LAUFZEIT);
        autoloadSwitch = findViewById(R.id.SWITCH_AUTOLOAD);
        layoutSelection = findViewById(R.id.LAYOUT_SELECTION);


        autoloadSwitch.setOnCheckedChangeListener((button, state) -> {
            if(state) {
                providerBox.setVisibility(View.GONE);
                seasonBox.setVisibility(View.GONE);
                runtimeBox.setVisibility(View.GONE);
                hintText.setVisibility(View.VISIBLE);
                addButton.setText("Suchen");
                layoutSelection.removeAllViews();
            } else {
                providerBox.setVisibility(View.VISIBLE);
                seasonBox.setVisibility(View.VISIBLE);
                runtimeBox.setVisibility(View.VISIBLE);
                hintText.setVisibility(View.GONE);
                addButton.setText("Hinzufügen");
                layoutSelection.removeAllViews();
            }
        });

        backButton.setOnClickListener((v) -> finish());
        addButton.setOnClickListener(this::returnData);
    }

    public void returnData(View v) {
        layoutSelection.removeAllViews();
        String name = nameBox.getText().toString();
        boolean autoload = autoloadSwitch.isChecked();
        if(!autoload) {
            if(!Util.validateData(name, seasonBox.getText().toString(), runtimeBox.getText().toString())) {
                Log.e("NWT", "Fehler beim Validieren der Daten!");
                TextView errorText = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                int dp = (int) Util.convertDpToPixel(10, this);
                params.setMargins(dp, dp, dp, dp);
                errorText.setLayoutParams(params);
                errorText.setText("Die eingegebenen Daten sind invalide!");
                errorText.setTextColor(Color.rgb(200, 50, 50));
                errorText.setTypeface(null, Typeface.BOLD);
                layoutSelection.addView(errorText);
                return;
            }
            addSerieWithName(name);
            return;
        }
        if(getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        AtomicReference<List<String>> seriesNames = new AtomicReference<>();
        new Thread(() -> seriesNames.set(DataScraper.scrapeSerien(name, 5))).start();
        while(seriesNames.get() == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(seriesNames.get().size() == 0) {
            TextView errorText = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int dp = (int) Util.convertDpToPixel(10, this);
            params.setMargins(dp, dp, dp, dp);
            errorText.setLayoutParams(params);
            errorText.setText("Keine Serie mit dem namen: \"" + name + "\" konnte gefunden werden!");
            errorText.setTextColor(Color.rgb(200, 50, 50));
            errorText.setTypeface(null, Typeface.BOLD);
            layoutSelection.addView(errorText);
        }
        for(int i = 0; i < seriesNames.get().size(); i++) {
            Button b = new Button(this);
            b.setText(seriesNames.get().get(i));
            int finalI = i;
            b.setBackgroundResource(R.drawable.button_black_edge);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int dp=(int) Util.convertDpToPixel(5, this);
            params.setMargins(dp,dp,dp,dp);
            b.setLayoutParams(params);
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
            s.setLaufzeit(runtimeBox.getText().toString());
        }
        resultIntent.putExtra("SERIE", s);
        resultIntent.putExtra("AUTOLOAD", autoload);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}