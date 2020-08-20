package com.example.nwt;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class PreActivity extends AppCompatActivity {

    Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pre);

        getSupportActionBar().hide();

        start = findViewById(R.id.START_APP);

        start.setOnClickListener(this::startApp);
    }

    public void startApp(View v){
        Intent intent = new Intent(PreActivity.this, MainActivity.class);
        startActivity(intent);
    }
}