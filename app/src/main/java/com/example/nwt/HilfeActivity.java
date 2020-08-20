package com.example.nwt;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.widget.Button;

public class HilfeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hilfe);

        getSupportActionBar().hide();

        Button b = findViewById(R.id.BUTTON_VERSTANDEN);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(b, PropertyValuesHolder.ofFloat("scaleX",  0.8f), PropertyValuesHolder.ofFloat("scaleY", 0.8f), PropertyValuesHolder.ofFloat("alpha", 0.5f));
        animator.setDuration(1000);

        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);

        animator.start();

        b.setOnClickListener((V) -> finish());
    }
}