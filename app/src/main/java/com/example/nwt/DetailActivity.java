package com.example.nwt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nwt.model.Serie;

import java.util.Base64;

public class DetailActivity extends AppCompatActivity {

    Serie s;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTitle(Html.fromHtml("<font color='#222222'>Details</font>"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();

        if(intent.hasExtra("SERIE")) {
            s = (Serie) intent.getSerializableExtra("SERIE");
            if(s.getCover()!=null) {
                byte[] b=Base64.getDecoder().decode(s.getCover());
                ((ImageView) findViewById(R.id.COVER)).setImageBitmap(BitmapFactory.decodeByteArray(b,0,b.length));
            }else{
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ((ImageView) findViewById(R.id.COVER)).setLayoutParams(params);
            }
            ((TextView) findViewById(R.id.INSERT_NAME)).setText(s.getName());
            ((TextView) findViewById(R.id.INSERT_STAFFELN)).setText(s.getStaffeln() + "");
            String diensteText = "";
            for(int i = 0; i < s.getStreamingDienste().size(); i++) {
                if(i > 0) {
                    diensteText += ", ";
                }
                diensteText += s.getStreamingDienste().get(i).getAnzeigeName();
            }
            ((TextView) findViewById(R.id.INSERT_DIENSTE)).setText(diensteText);
        } else {
            cancel();
        }
        ((Button) findViewById(R.id.DT_BACK)).setOnClickListener((v) -> cancel());
        ((Button) findViewById(R.id.DT_EDIT)).setOnClickListener((view1) -> {
            Intent resultIntent = new Intent(this, BearbeitenActivity.class);
            resultIntent.putExtra("SERIE", s);
            startActivityForResult(resultIntent, 2);

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            setResult(Activity.RESULT_OK, data);
            finish();
        }else if(resultCode == RESULT_FIRST_USER){
            setResult(Activity.RESULT_FIRST_USER, data);
            finish();
        }
    }



    private void cancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}