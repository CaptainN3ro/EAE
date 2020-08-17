package com.example.nwt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerDienste;
    LinearLayout serienLayout;

    List<Serie> serien;
    List<Dienst> dienste;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerDienste = findViewById(R.id.STREAMINGDIENSTE);
        serienLayout = findViewById(R.id.SERIEN_LAYOUT);
        setTitle(Html.fromHtml("<font color='#222222'>Never Watch Twice</font>"));
        fillData();
        //loadSavedData();
        update();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.ITEM_HILFE:
                return true;
            case R.id.ITEM_HINZUFUEGEN:
                Intent intent = new Intent(this, HinzufuegenActivity.class);
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (1):
                if(resultCode == Activity.RESULT_OK) {
                    // TODO Daten aus web Holen und hinzufügen
                    String text = data.getStringExtra("NAME");
                    if(!text.equals("")) {
                        serien.add(new Serie(text, 5, false));
                    } else {
                        // TODO Show Error
                    }
                    update();
                }
                break;
            case (2):
                if(resultCode == Activity.RESULT_OK) {
                    Serie s = (Serie) data.getSerializableExtra("SERIE");
                    for(Serie tmpSerie: serien) {
                        if(tmpSerie.getId() == s.getId()) {
                            tmpSerie.setName(s.getName());
                            tmpSerie.setStaffeln(s.getStaffeln());
                            tmpSerie.setStreamingDienste(s.getStreamingDienste());
                            update();
                        }
                    }
                }
                break;
            default:
                break;
        }
        // TODO Save
    }

    private void update() {
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dienste);
        spinnerDienste.setAdapter(spinnerAdapter);
        spinnerDienste.setOnItemSelectedListener(this);
    }

    private void loadSavedData(){
        //Hier .txt einlesen in Array "Saved"
    }


    private void fillData() {
        dienste = new ArrayList<>();

        dienste.add(new Dienst("", "-Alle Serien-"));
        dienste.add(new Dienst("Amazon", "Amazon"));
        dienste.add(new Dienst("Netflix", "Netflix"));
        dienste.add(new Dienst("Disney +", "Disney +"));

        serien = new ArrayList<>();

        List<Dienst> anbieter_amazon = new ArrayList<>();
        anbieter_amazon.add(dienste.get(1));

        List<Dienst> anbieter_netflix = new ArrayList<>();
        anbieter_netflix.add(dienste.get(2));

        List<Dienst> anbieter_disney = new ArrayList<>();
        anbieter_disney.add(dienste.get(3));

        Serie s;
        //Amazon
        s = new Serie("The Boys", 3, false);
        s.setStreamingDienste(anbieter_amazon);
        serien.add(s);
        s = new Serie("Supernatural", 14, false);
        s.setStreamingDienste(anbieter_amazon);
        serien.add(s);
        s = new Serie("Mr. Robot", 3, true);
        s.setStreamingDienste(anbieter_amazon);
        serien.add(s);

        //Netflix
        s = new Serie("How to sell drugs online", 1, false);
        s.setStreamingDienste(anbieter_netflix);
        serien.add(s);
        s = new Serie("Death Note", 3, true);
        s.setStreamingDienste(anbieter_netflix);
        serien.add(s);
        s = new Serie("The Ranch", 4, false);
        s.setStreamingDienste(anbieter_netflix);
        serien.add(s);

        //Disney
        s = new Serie("Star Wars The Clone Wars", 10, false);
        s.setStreamingDienste(anbieter_disney);
        serien.add(s);
        s = new Serie("Into the Woods", 3, true);
        s.setStreamingDienste(anbieter_disney);
        serien.add(s);

        //serien.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int dienstIndex, long l) {
        serienLayout.removeAllViews();
        int Array_Count = serien.size();

        for (int i=0; i< Array_Count; i++){
            Serie serie = serien.get(i);
            if(!dienste.get(dienstIndex).isIncluded(serie.getStreamingDienste())) {
                continue;
            }
            final CheckBox cb = new CheckBox(this);
            cb.setText(serie.getName());
            cb.setId(i);
            if(serie.isChecked()) {
                cb.setChecked(true);
            }


            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //Zum Überschreiben der Checkbox-Checkfunktion
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    serie.setChecked(b);
                    // TODO Save
                }
            });
            cb.setOnLongClickListener((view1) -> {
                Intent intent = new Intent(this, BearbeitenActivity.class);
                intent.putExtra("SERIE", serie);
                startActivityForResult(intent, 2);
                return true;
            });

            serienLayout.addView(cb);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        int Array_Count = serien.size();

        for (int z=0; z< Array_Count; z++){
            final CheckBox cb = new CheckBox(this);
            cb.setText(serien.get(z).getName());
            cb.setId(z);

            if(serien.get(z).isChecked()) {
                cb.setChecked(true);
            }


            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //Zum Überschreiben der Checkbox-Checkfunktion
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    for(Serie s: serien) {
                        if(s.getName().equals(cb.getText().toString())) {
                            s.setChecked(b);
                        }
                    }
                }
            });

            serienLayout.addView(cb);

        }
    }
}