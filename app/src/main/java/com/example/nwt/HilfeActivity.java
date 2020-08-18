package com.example.nwt;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class HilfeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hilfe);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Button b = findViewById(R.id.BUTTON_VERSTANDEN);
        b.setOnClickListener((V) -> finish());
    }
}