package com.example.studiengaenge;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerDienste;
    LinearLayout my_layout;

    String[] streamingdienste;
    List<String> Amazon;
    List<String> Netflix;
    List<String> Disney;
    List<String> alleSerien;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setTitle(" NWT - Never Watch Twice ");
        spinnerDienste = findViewById(R.id.STREAMINGDIENSTE);
        my_layout = findViewById(R.id.SERIEN);


        fillData();

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, streamingdienste);
        spinnerDienste.setAdapter(spinnerAdapter);
        spinnerDienste.setOnItemSelectedListener(this);
    }


    private void fillData(){
        streamingdienste = new String[] {
          "-Alle Serien-", "Amazon","Netflix","Disney +"
        };

        Amazon = new ArrayList<>();
        Amazon.add("The Boys");
        Amazon.add("Supernatural");
        Amazon.add("Mr. Robot");

        Netflix = new ArrayList<>();
        Netflix.add("How to sell drugs online");
        Netflix.add("Death Note");
        Netflix.add("The Ranch");

        Disney = new ArrayList<>();
        Disney.add("Star Wars The Clone Wars");
        Disney.add("Into the Woods");

        alleSerien = new ArrayList<>();
        alleSerien.addAll(Amazon);
        alleSerien.addAll(Disney);
        alleSerien.addAll(Netflix);
        Collections.sort(alleSerien, String.CASE_INSENSITIVE_ORDER);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i==0){

            int Array_Count = alleSerien.size();

            for (int z=0; z< Array_Count; z++){
                CheckBox cb = new CheckBox(this);
                cb.setText(alleSerien.get(z));
                cb.setId(z+6);
                my_layout.addView(cb);

            }
            //ArrayAdapter listViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, alleSerien);
            //ListViewDienste.setAdapter(listViewAdapter);
        }
        if(i==1){

            my_layout.removeAllViews();
            int Array_Count = Amazon.size();

            for (int z=0; z< Array_Count; z++) {
                CheckBox cb = new CheckBox(this);
                cb.setText(Amazon.get(z));
                cb.setId(z + 6);
                my_layout.addView(cb);
                //ArrayAdapter listViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Netflix);
                //ListViewDienste.setAdapter(listViewAdapter);
            }
            }
        if(i==2){
            my_layout.removeAllViews();
            int Array_Count = Netflix.size();

            for (int z=0; z< Array_Count; z++){
                CheckBox cb = new CheckBox(this);
                cb.setText(Netflix.get(z));
                cb.setId(z+6);
                my_layout.addView(cb);
        }
            //ArrayAdapter listViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Amazon);
            //ListViewDienste.setAdapter(listViewAdapter);
        }
        if(i==3){
            my_layout.removeAllViews();
            int Array_Count = Disney.size();

            for (int z=0; z< Array_Count; z++){
                CheckBox cb = new CheckBox(this);
                cb.setText(Disney.get(z));
                cb.setId(z+6);
                my_layout.addView(cb);
        }
            //ArrayAdapter listViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Disney);
            //ListViewDienste.setAdapter(listViewAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        int Array_Count = alleSerien.size();

        for (int z=0; z< Array_Count; z++){
            CheckBox cb = new CheckBox(this);
            cb.setText(alleSerien.get(z));
            cb.setId(z+6);
            my_layout.addView(cb);

        }
    }
}