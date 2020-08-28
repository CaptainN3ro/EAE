package com.hskl.nwt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hskl.nwt.model.Data;
import com.hskl.nwt.model.Dienst;
import com.hskl.nwt.model.Serie;
import com.hskl.nwt.util.Util;

import java.util.ArrayList;
import java.util.List;

public class BearbeitenActivity extends AppCompatActivity {

    TextView name, staffeln, dienste, laufzeit;
    Serie s;
    LinearLayout errorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(Html.fromHtml("<font color='#222222'>Bearbeiten</font>"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bearbeiten);

        name = findViewById(R.id.TEXT_NAME);
        laufzeit = findViewById(R.id.TEXT_LAUFZEIT);
        staffeln = findViewById(R.id.TEXT_STAFFELN);
        dienste = findViewById(R.id.TEXT_DIENSTE);
        errorLayout = findViewById(R.id.LAYOUT_ERROR);

        Button zurueck = findViewById(R.id.BUTTON_ZURUECK);
        Button speichern = findViewById(R.id.BUTTON_SPEICHERN);
        Button entfernen = findViewById(R.id.DT_DELETE);

        Intent intent = getIntent();

        if(intent.hasExtra("SERIENID")) {
            s = Data.getSerieById(intent.getIntExtra("SERIENID", -1));
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
            laufzeit.setText(s.getLaufzeit());
        } else {
            cancel();
        }


        zurueck.setOnClickListener((v) -> {
            cancel();
        });
        speichern.setOnClickListener((v) -> {
            if(!Util.validateData(name.getText().toString(), staffeln.getText().toString(), laufzeit.getText().toString())) {
                Log.e("NWT", "Fehler beim Validieren der Daten!");
                errorLayout.removeAllViews();
                TextView errorText = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                int dp = (int) Util.convertDpToPixel(10, this);
                params.setMargins(dp, dp, dp, dp);
                errorText.setLayoutParams(params);
                errorText.setText("Die eingegebenen Daten sind invalide!");
                errorText.setTextColor(Color.rgb(200, 50, 50));
                errorText.setTypeface(null, Typeface.BOLD);
                errorLayout.addView(errorText);
                return;
            }
            updateSeriesData();
            setResult(Activity.RESULT_OK);
            finish();
        });

        entfernen.setOnClickListener((view) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Sicher, dass die Serie gelöscht werden soll? Dies kann nicht rückgängig gemacht werden!")
                    .setPositiveButton("Löschen!", (dialog, id) -> returnDelete())
                    .setNegativeButton("Abbrechen", (dialog, id) -> {return;});
            AlertDialog dialog = builder.create();
            dialog.show();
            Button b = dialog.getButton(Dialog.BUTTON_POSITIVE);
            b.setBackgroundResource(R.drawable.button_delete);
            b.setPadding(25, 0, 25, 0);
            b.setTextColor(Color.rgb(255, 255, 255));
        });
    }

    private void returnDelete() {
        Data.removeSerie(s);
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void updateSeriesData() {
        boolean[] checkedOld = s.getChecked();
        s.setName(name.getText().toString());
        s.setStaffeln(Util.parseInt(staffeln.getText().toString()));
        List<Dienst> diensteList = new ArrayList<>();
        for(String s: dienste.getText().toString().split(",")) {
            diensteList.add(new Dienst(s.trim()));
        }
        s.setStreamingDienste(diensteList);
        for(int i = 0; i < Math.min(checkedOld.length, s.getChecked().length); i++) {
            s.getChecked()[i] = checkedOld[i];
        }
        s.setLaufzeit(laufzeit.getText().toString());
        Data.updateMainView();
    }

    private void cancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}