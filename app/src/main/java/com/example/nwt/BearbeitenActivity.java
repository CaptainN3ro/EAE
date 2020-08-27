package com.example.nwt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nwt.model.Data;
import com.example.nwt.model.Dienst;
import com.example.nwt.model.Serie;
import com.example.nwt.util.Util;

import java.util.ArrayList;
import java.util.List;

public class BearbeitenActivity extends AppCompatActivity {

    TextView name, staffeln, dienste;
    Serie s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(Html.fromHtml("<font color='#222222'>Bearbeiten</font>"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bearbeiten);

        name = findViewById(R.id.TEXT_NAME);
        staffeln = findViewById(R.id.TEXT_STAFFELN);
        dienste = findViewById(R.id.TEXT_DIENSTE);

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
        } else {
            cancel();
        }


        zurueck.setOnClickListener((v) -> {
            cancel();
        });
        speichern.setOnClickListener((v) -> {
            if(!validateTextfields()) {
                Log.e("NWT", "Fehler beim Validieren der Daten!");
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
            b.setBackgroundResource(R.drawable.button_blue_gradient);
            b.setPadding(25, 0, 25, 0);
            b.setTextColor(Color.rgb(255, 255, 255));
        });
    }

    private void returnDelete() {
        Data.removeSerie(s);
        setResult(Activity.RESULT_OK);
        finish();
    }

    private boolean validateTextfields() {
        if(name.getText().toString().trim().equals("")) {
            return false;
        }
        if(Util.parseInt(staffeln.getText().toString()) < 0) {
            return false;
        }
        return true;
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
        Data.updateMainView();
    }

    private void cancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}