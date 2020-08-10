package com.example.studiengaenge;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerDienste;
    LinearLayout my_layout;
    Button addButton;
    TextView nameTextView;

    String[] streamingdienste;

    List<String> Saved; //Hier liegen die Serien, die gecheckt wurden
    List<String> Hinzugefuegt; //Die eigenen Serien
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
        addButton = findViewById(R.id.BUTTON_ADD);
        nameTextView = findViewById(R.id.ADD_NAME_SERIE);

        addButton.setOnClickListener(this::onClickAdd);

        Saved = new ArrayList<>();


        //loadSavedData();

        fillData();
        update();
        registerForContextMenu(my_layout);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //MenuInflater inflater = new MenuInflater();
        // TODO Kontextmenu erstellen und hinzufügen (Blatt 3 Vid 2)

    }

    private void update() {
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, streamingdienste);
        spinnerDienste.setAdapter(spinnerAdapter);
        spinnerDienste.setOnItemSelectedListener(this);
    }

    private void loadSavedData(){
        //Hier .txt einlesen in Array "Saved"
    }


    private void fillData() {
        streamingdienste = new String[]{
                "-Alle Serien-", "Amazon", "Netflix", "Disney +"
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

    public void onClickAdd(View v) {
        if(v == addButton) {
            String text = nameTextView.getText().toString();
            if(!text.equals("")) {
                alleSerien.add(text);
            } else {
                // TODO Show Error
            }
            nameTextView.setText("");
            update();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i==0){
            my_layout.removeAllViews();
            int Array_Count = alleSerien.size();

            for (int z=0; z< Array_Count; z++){
                final CheckBox cb = new CheckBox(this);
                cb.setText(alleSerien.get(z));
                cb.setId(z+6);

                for(int y = 0; y< Saved.size(); y++){   //Zum Haken setzen der gespeicherten
                    if((Saved.get(y)).equals(cb.getText().toString())){
                        cb.setChecked(true);
                    }
                }


                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //Zum Überschreiben der Checkbox-Checkfunktion
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            Saved.add(cb.getText().toString());
                        }else{
                            for(int i=0;i<Saved.size();i++){
                                if(cb.getText().toString().equals(Saved.get(i))){
                                    Saved.remove(i);
                                }
                            }
                        }
                    }
                });


                my_layout.addView(cb);

            }
            //ArrayAdapter listViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, alleSerien);
            //ListViewDienste.setAdapter(listViewAdapter);
        }
        if(i==1){

            my_layout.removeAllViews();
            int Array_Count = Amazon.size();

            for (int z=0; z< Array_Count; z++) {
                final CheckBox cb = new CheckBox(this);
                cb.setText(Amazon.get(z));
                cb.setId(z + 6);

                for(int y = 0; y< Saved.size(); y++){   //Zum Haken setzen der gespeicherten
                    if((Saved.get(y)).equals(cb.getText().toString())){
                        cb.setChecked(true);
                    }
                }


                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //Zum Überschreiben der Checkbox-Checkfunktion
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            Saved.add(cb.getText().toString());
                        }else{
                            for(int i=0;i<Saved.size();i++){
                                if(cb.getText().toString().equals(Saved.get(i))){
                                    Saved.remove(i);
                                }
                            }
                        }
                    }
                });

                my_layout.addView(cb);
                //ArrayAdapter listViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Netflix);
                //ListViewDienste.setAdapter(listViewAdapter);
            }
            }
        if(i==2){


            my_layout.removeAllViews();
            int Array_Count = Netflix.size();

            for (int z=0; z< Array_Count; z++){
                final CheckBox cb = new CheckBox(this);
                cb.setText(Netflix.get(z));
                cb.setId(z+6);

                for(int y = 0; y< Saved.size(); y++){   //Zum Haken setzen der gespeicherten
                    if((Saved.get(y)).equals(cb.getText().toString())){
                        cb.setChecked(true);
                    }
                }


                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //Zum Überschreiben der Checkbox-Checkfunktion
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            Saved.add(cb.getText().toString());
                        }else{
                            for(int i=0;i<Saved.size();i++){
                                if(cb.getText().toString().equals(Saved.get(i))){
                                    Saved.remove(i);
                                }
                            }
                        }
                    }
                });

                my_layout.addView(cb);
        }
            //ArrayAdapter listViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Amazon);
            //ListViewDienste.setAdapter(listViewAdapter);
        }
        if(i==3){
            my_layout.removeAllViews();
            int Array_Count = Disney.size();

            for (int z=0; z< Array_Count; z++){
                final CheckBox cb = new CheckBox(this);
                cb.setText(Disney.get(z));
                cb.setId(z+6);

                for(int y = 0; y< Saved.size(); y++){   //Zum Haken setzen der gespeicherten
                    if((Saved.get(y)).equals(cb.getText().toString())){
                        cb.setChecked(true);
                    }
                }


                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //Zum Überschreiben der Checkbox-Checkfunktion
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            Saved.add(cb.getText().toString());
                        }else{
                            for(int i=0;i<Saved.size();i++){
                                if(cb.getText().toString().equals(Saved.get(i))){
                                    Saved.remove(i);
                                }
                            }
                        }
                    }
                });

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
            final CheckBox cb = new CheckBox(this);
            cb.setText(alleSerien.get(z));
            cb.setId(z+6);

            for(int y = 0; y< Saved.size(); y++){   //Zum Haken setzen der gespeicherten
                if((Saved.get(y)).equals(cb.getText().toString())){
                    cb.setChecked(true);
                }
            }


            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //Zum Überschreiben der Checkbox-Checkfunktion
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        Saved.add(cb.getText().toString());
                    }else{
                        for(int i=0;i<Saved.size();i++){
                            if(cb.getText().toString().equals(Saved.get(i))){
                                Saved.remove(i);
                            }
                        }
                    }
                }
            });

            my_layout.addView(cb);

        }
    }
}