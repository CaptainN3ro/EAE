package com.hskl.nwt;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.hskl.nwt.model.Data;
import com.hskl.nwt.model.Serie;
import com.hskl.nwt.scraping.DataScraper;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerDienste;
    LinearLayout serienLayout;

    private AtomicBoolean backpressed = new AtomicBoolean();

    @Override
    public void onBackPressed() {
        if(!backpressed.get()) {
            Toast t = Toast.makeText(MainActivity.this, "Zurück Button erneut drücken, um die App zu beenden!", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.BOTTOM, 0, 50);
            t.show();
            backpressed.set(true);
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                backpressed.set(false);
            }).start();
        } else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Data.createUpdateCallback(this::update);
        setTitle(Html.fromHtml("<font color='#222222'>Never Watch Twice</font>"));

        spinnerDienste = findViewById(R.id.STREAMINGDIENSTE);
        serienLayout = findViewById(R.id.SERIEN_LAYOUT);

        if(Data.getSerienCount() == 0) {
            Intent intent = new Intent(this, HinzufuegenActivity.class);
            startActivityForResult(intent, 1);
        }

        Data.updateDienste();
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
                        DataScraper.scrapeData(s, this);
                    } else {
                        Toast t = Toast.makeText(this, s.getName() + " erfolgreich hinzugefügt!", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.BOTTOM, 0, 50);
                        t.show();
                    }
                    Data.addSerie(s);
                }
                break;
            case (2):
                createCheckboxes();
            default:
                break;
        }
        Data.updateDienste();
    }

    private void updateSpinner() {
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Data.getDienste());
        spinnerDienste.setAdapter(spinnerAdapter);
        spinnerDienste.setOnItemSelectedListener(this);
        spinnerDienste.setSelection(Data.getLastFilterIndex());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int dienstIndex, long l) {
        Data.applyFilter(Data.getDienst(dienstIndex), this::createCheckboxes);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Data.applyFilter(Data.getDienst(0), this::createCheckboxes);
    }

    private void update() {
        updateSpinner();
        createCheckboxes();
    }

    private void createCheckboxes() {

        serienLayout.removeAllViews();

        List<Serie> filteredSerien = Data.getFilteredSerien();

        for (int i=0; i< filteredSerien.size(); i++){
            Serie serie = filteredSerien.get(i);
            final CheckBox cb = new CheckBox(this);
            cb.setText(serie.getName());
            cb.setId(i);

            boolean allChecked = true;
            if(serie.getChecked() != null) {
                for (boolean b : serie.getChecked()) {
                    allChecked &= b;
                }
            } else {
                allChecked = false;
            }
            cb.setChecked(allChecked);

            cb.setOnCheckedChangeListener((b, v) -> {
                cb.setChecked(!v);
                serienLayout.jumpDrawablesToCurrentState();
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra("SERIENID", serie.getId());
                startActivityForResult(intent, 2);
            });

            serienLayout.addView(cb);
        }
    }

}