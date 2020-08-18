package com.example.nwt;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

public class PreActivity extends AppCompatActivity {

    Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pre);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        start = findViewById(R.id.START_APP);

        start.setOnClickListener((v) -> {
            startApp();
        });
    }

    public void startApp(){
        Intent intent = new Intent(PreActivity.this, MainActivity.class);
        startActivity(intent);
    }
}