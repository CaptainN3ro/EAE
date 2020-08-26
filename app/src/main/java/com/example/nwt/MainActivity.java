package com.example.nwt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nwt.model.Dienst;
import com.example.nwt.model.Serie;
import com.example.nwt.scraping.DataScraper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerDienste;
    LinearLayout serienLayout;

    List<Serie> serien;
    List<Dienst> dienste;

    File dataFile;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(Html.fromHtml("<font color='#222222'>Never Watch Twice</font>"));

        dienste = new ArrayList<>();
        serien = new ArrayList<>();
        dataFile = new File("UserData.csv");

        spinnerDienste = findViewById(R.id.STREAMINGDIENSTE);
        serienLayout = findViewById(R.id.SERIEN_LAYOUT);



        loadData();
    }

    public void saveData() {
        deleteData();
        String outputDataString = "";
        for(Serie s: serien) {
            String diensteString = "";
            for(int i = 0; i < s.getStreamingDienste().size(); i++) {
                if(i > 0) {
                    diensteString += ", ";
                }
                diensteString += s.getStreamingDienste().get(i);
            }

            outputDataString += s.getName() + ";" + s.getStaffeln() + ";" + s.isChecked() + ";" + diensteString + ";" + s.getCover() + "\n";

        }
        try {

            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(dataFile.getName(), MODE_APPEND));
            out.write(outputDataString);
            out.close();
        } catch (Exception e) {
            Log.e("NWT", e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadData() {
        try {
            InputStreamReader in = new InputStreamReader(openFileInput(dataFile.getName()));
            BufferedReader reader = new BufferedReader(in);
            String line;
            boolean loaded = false;
            while((line=reader.readLine())!=null){
                if(addSerieFromCSVString(line)) {
                    loaded = true;
                }
            }
            if(!loaded) {
                Log.e("NWT", "Gespeicherte Datei enthält invalide oder keine Daten. Serie hinzufügen wird geöffnet!");
                Intent i = new Intent(this, HinzufuegenActivity.class);
                startActivityForResult(i, 1);
            }
            reader.close();
        } catch (Exception e) {
            Log.e("NWT", "Gespeicherte Datei enthält invalide oder keine Daten. Serie hinzufügen wird geöffnet!");
            Intent i = new Intent(this, HinzufuegenActivity.class);
            startActivityForResult(i, 1);
        }

        updateDienste();
    }

    private void deleteData(){
        try{
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(dataFile.getName(), MODE_PRIVATE));
            deleteFile(out.getEncoding());
            out.close();

            for(Serie s: serien){
                out = new OutputStreamWriter(openFileOutput(s.getName()+".jpg", MODE_PRIVATE));
                deleteFile(out.getEncoding());
                out.close();
            }
        }catch(Exception e){
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean addSerieFromCSVString(String csvString) {
        String[] serienCSV = csvString.split("\n");
        if(serienCSV.length == 0) {
            return false;
        }
        for(String s: serienCSV) {
            String[] tokens = s.split(";");
            if(tokens.length < 3) {
                return false;
            }
            Serie serie = new Serie(tokens[0], Integer.parseInt(tokens[1]), Boolean.parseBoolean(tokens[2]));
            if(tokens.length > 3) {
                String[] anbieterCSV = tokens[3].split(",");
                List<Dienst> dienstListe = new ArrayList<>();
                for (int i = 0; i < anbieterCSV.length; i++) {
                    dienstListe.add(new Dienst(anbieterCSV[i].trim()));
                }

                serie.setStreamingDienste(dienstListe);
            }
            if(tokens.length > 4){
                serie.setCover(tokens[4]);
            }

            serien.add(serie);
        }
        return true;
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
                Intent intent = new Intent(this, HilfeActivity.class);
                startActivity(intent);
                return true;
            case R.id.ITEM_HINZUFUEGEN:
                Intent i = new Intent(this, HinzufuegenActivity.class);
                startActivityForResult(i, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (1):
                if(resultCode == Activity.RESULT_OK) {
                    Serie s = (Serie) data.getSerializableExtra("SERIE");
                    boolean autoload = data.getBooleanExtra("AUTOLOAD", true);
                    if(autoload) {
                        DataScraper.scrapeData(s, this::saveData, this);
                    } else {
                        Toast t = Toast.makeText(this, s.getName() + " erfolgreich hinzugefügt!", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.BOTTOM, 0, 50);
                        t.show();
                    }
                    serien.add(s);
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
                        }
                    }
                }else if(resultCode == Activity.RESULT_FIRST_USER){
                    Serie s = (Serie) data.getSerializableExtra("SERIE");
                    for(int i=0;i<serien.size();i++){
                        if(serien.get(i).getId()==s.getId()){
                            serien.remove(i);
                        }
                    }
                }
                break;
            default:
                break;
        }
        updateDienste();
        saveData();
    }

    private void updateSpinner() {
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dienste);
        spinnerDienste.setAdapter(spinnerAdapter);
        spinnerDienste.setOnItemSelectedListener(this);
    }

    private void updateDienste() {
        dienste.clear();
        dienste.add(new Dienst("", "-Alle Serien-"));
        for(int i = 0; i < serien.size(); i++) {
            Serie s = serien.get(i);
            for(Dienst d: s.getStreamingDienste()) {
                if(!d.isIncluded(dienste)) {
                    dienste.add(d);
                }
            }
        }
        updateSpinner();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int dienstIndex, long l) {
        createCheckboxes(dienstIndex);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        createCheckboxes(0);
    }

    private void createCheckboxes(int dienstIndex) {
        if(dienstIndex > dienste.size()) {
            finish();
        }

        serienLayout.removeAllViews();

        for (int i=0; i< serien.size(); i++){
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
            cb.setOnCheckedChangeListener((button, value) -> {
                serie.setChecked(value);
                saveData();
            });
            cb.setOnLongClickListener((view1) -> {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra("SERIE", serie);
                startActivityForResult(intent, 2);
                return true;
            });
            serienLayout.addView(cb);
        }
    }

}