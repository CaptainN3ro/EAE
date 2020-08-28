package com.hskl.nwt;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.hskl.nwt.model.Data;

public class PreActivity extends AppCompatActivity {

    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().hide();
        Data.createDataFile(getFilesDir());
        Data.loadData();

        start = findViewById(R.id.START_APP);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(start, PropertyValuesHolder.ofFloat("scaleX", 1.2f), PropertyValuesHolder.ofFloat("scaleY", 1.2f), PropertyValuesHolder.ofFloat("alpha", 1f));
        animator.setDuration(1000);

        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);

        animator.start();
        start.setOnClickListener(this::startApp);
    }

    public void startApp(View v){
        Intent intent = new Intent(PreActivity.this, MainActivity.class);
        startActivity(intent);
    }
}