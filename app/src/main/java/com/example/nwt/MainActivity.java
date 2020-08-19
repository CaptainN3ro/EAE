package com.example.nwt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.ContactsContract;
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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.res.ResourcesCompat.getFont;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerDienste;
    LinearLayout serienLayout;

    List<Serie> serien;
    List<Dienst> dienste;
    Typeface font;
    File f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dienste = new ArrayList<>();
        serien = new ArrayList<>();

        spinnerDienste = findViewById(R.id.STREAMINGDIENSTE);
        serienLayout = findViewById(R.id.SERIEN_LAYOUT);
        setTitle(Html.fromHtml("<font color='#222222'>Never Watch Twice</font>"));
        font = getResources().getFont(R.font.raleway);

        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "UserData.csv";
        String filePath = baseDir + File.separator + fileName;
        f = new File(fileName);

        loadSavedData();
        update();

    }

    public void saveData() {
        try {
            deleteSavedData();
            OutputStreamWriter bw = new OutputStreamWriter(openFileOutput(f.getName(), MODE_APPEND));
            String zv="";
            for (int i = 0; i < serien.size(); i++) {
                Serie sw = serien.get(i);
                String dienste = "";
                for (int y = 0; y < sw.getStreamingDienste().size(); y++) {
                    if (y == sw.getStreamingDienste().size() - 1) {
                        dienste += sw.getStreamingDienste().get(y);
                    } else {
                        dienste += sw.getStreamingDienste().get(y) + ", ";
                    }
                }

                zv+= sw.getName() + ";" + sw.getStaffeln() + ";"+sw.isChecked() +";" + dienste;
                zv+="\n";
            }
            bw.write(zv);
            bw.close();
        }catch(Exception e){
            Log.e("NWT", e.getMessage());
            }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (1):
                if(resultCode == Activity.RESULT_OK) {
                    Serie s = (Serie) data.getSerializableExtra("SERIE");
                    boolean autoload = data.getBooleanExtra("AUTOLOAD", true);
                    Toast t = Toast.makeText(this, s.getName() + " erfolgreich hinzugefügt!", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.BOTTOM, 0, 50);
                    if(autoload) {
                        // TODO Daten aus web Holen und hinzufügen
                        DataScraper.scrapeData(s, this::saveData, t);
                    } else {
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

    private void update() {
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dienste);
        spinnerDienste.setAdapter(spinnerAdapter);
        spinnerDienste.setOnItemSelectedListener(this);
    }

    private void deleteSavedData(){
        try{
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(f.getName(), MODE_PRIVATE));
            deleteFile(out.getEncoding());
            out.close();
        }catch(Exception e){

        }
    }

    private void loadSavedData(){

        StringBuilder s= new StringBuilder();
        try{
            InputStreamReader in = new InputStreamReader(openFileInput(f.getName()));
            BufferedReader reader = new BufferedReader(in);
            String line;
            while((line=reader.readLine())!=null){
                s.append(line+"\n");
            }
            reader.close();
            String[] partz=s.toString().split("\n");
            for(int i = 0;i < partz.length;i++){
                String[] tokens=partz[i].split(";");
                Serie serie = new Serie(tokens[0],Integer.parseInt(tokens[1]),Boolean.parseBoolean(tokens[2]));
                if(tokens.length>3) {
                    String[] anbieter = tokens[3].split(",");
                    List<Dienst> dienstliste = new ArrayList<>();
                    for (int y = 0; y < anbieter.length; y++) {
                        dienstliste.add(new Dienst(anbieter[y].trim()));
                    }
                    serie.setStreamingDienste(dienstliste);
                }
                serien.add(serie);
            }

        } catch(Exception e) {
            Log.e("NWT", "Gespeicherte Datei enthält invalide oder keine Daten. Standardserien werden hinzugefügt!");
            Intent i = new Intent(this, HinzufuegenActivity.class);
            startActivityForResult(i, 1);
        }
        updateDienste();
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
        update();
    }


    private void fillData() {
        dienste.add(new Dienst("", "-Alle Serien-"));
        dienste.add(new Dienst("Amazon", "Amazon"));
        dienste.add(new Dienst("Netflix", "Netflix"));
        dienste.add(new Dienst("Disney +", "Disney +"));


        List<Dienst> anbieter_amazon = new ArrayList<>();
        anbieter_amazon.add(dienste.get(1));

        List<Dienst> anbieter_netflix = new ArrayList<>();
        anbieter_netflix.add(dienste.get(2));

        List<Dienst> anbieter_disney = new ArrayList<>();
        anbieter_disney.add(dienste.get(3));

        Serie s;
        //Amazon
        s = new Serie("The Boys", 1, false);
        s.setStreamingDienste(anbieter_amazon);
        serien.add(s);
        s = new Serie("Tom Clancy's Jack Ryan", 2, false);
        s.setStreamingDienste(anbieter_amazon);
        serien.add(s);
        s = new Serie("Upload", 1, false);
        s.setStreamingDienste(anbieter_amazon);
        serien.add(s);
        s = new Serie("Good Omens", 4, false);
        s.setStreamingDienste(anbieter_amazon);
        serien.add(s);
        s = new Serie("Mr. Robot", 4, false);
        s.setStreamingDienste(anbieter_amazon);
        serien.add(s);

        //Netflix
        s = new Serie("How to sell drugs online", 2, false);
        s.setStreamingDienste(anbieter_netflix);
        serien.add(s);
        s = new Serie("Lost in Space", 2, false);
        s.setStreamingDienste(anbieter_netflix);
        serien.add(s);
        s = new Serie("The Ranch", 7, false);
        s.setStreamingDienste(anbieter_netflix);
        serien.add(s);
        s = new Serie("You - Du wirst mich lieben", 2, false);
        s.setStreamingDienste(anbieter_netflix);
        serien.add(s);
        s = new Serie("Arrested Development", 5, false);
        s.setStreamingDienste(anbieter_netflix);
        serien.add(s);

        //Disney
        s = new Serie("Star Wars The Clone Wars", 7, false);
        s.setStreamingDienste(anbieter_disney);
        serien.add(s);
        s = new Serie("High School Musical: The Series", 1, false);
        s.setStreamingDienste(anbieter_disney);
        serien.add(s);
        s = new Serie("The Mendalorian", 1, false);
        s.setStreamingDienste(anbieter_disney);
        serien.add(s);
        s = new Serie("101 Dalmatian Street", 1, false);
        s.setStreamingDienste(anbieter_disney);
        serien.add(s);
        s = new Serie("Marvel Future Avengers", 2, false);
        s.setStreamingDienste(anbieter_disney);
        serien.add(s);
//        serien.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));
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
            cb.setTypeface(font);
            if(serie.isChecked()) {
                cb.setChecked(true);
            }


            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //Zum Überschreiben der Checkbox-Checkfunktion
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    serie.setChecked(b);
                    saveData();
                }
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


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        int Array_Count = serien.size();

        for (int z=0; z< Array_Count; z++){
            final CheckBox cb = new CheckBox(this);
            cb.setText(serien.get(z).getName());
            cb.setTypeface(font);

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

//    public void sendToast() {
//        try {
//            Looper.prepare();
//            Toast toast = Toast.makeText(getApplicationContext(), "1/3 Name geladen", Toast.LENGTH_SHORT);
//            toast.setMargin(50, 50);
//            toast.show();
//            Thread.sleep(2000);
//            Toast toast2 = Toast.makeText(getApplicationContext(), "2/3 Staffeln geladen", Toast.LENGTH_SHORT);
//            toast2.setMargin(50, 50);
//            toast2.show();
//            Thread.sleep(2000);
//            Toast toast3 = Toast.makeText(getApplicationContext(), "3/3 Dienste geladen", Toast.LENGTH_SHORT);
//            toast3.setMargin(50, 50);
//            toast3.show();
//        }catch(Exception e){
//            Log.e("NWT",e.toString());
//        }
//    }
}