package com.example.nwt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HinzufuegenActivity extends AppCompatActivity {

    Button addButton;
    TextView nameBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(Html.fromHtml("<font color='#222222'>Hinzufügen</font>"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        addButton = findViewById(R.id.BUTTON_ADD);
        nameBox = findViewById(R.id.INPUT_NAME);

        addButton.setOnClickListener(this::returnData);
    }

    public void returnData(View v) {
        Intent resultIntent = new Intent();

        resultIntent.putExtra("NAME", nameBox.getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}