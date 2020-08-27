package com.example.nwt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nwt.events.OnSwipeTouchListener;
import com.example.nwt.model.Data;
import com.example.nwt.model.Serie;
import com.example.nwt.util.Util;

import java.util.Base64;

public class DetailActivity extends AppCompatActivity {

    LinearLayout checkedLayout;
    Serie s;


    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(DetailActivity.this, MainActivity.class);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(Html.fromHtml("<font color='#222222'>Details</font>"));
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();

        checkedLayout = findViewById(R.id.LAYOUT_CHECKBOXES);

        ScrollView layout = findViewById(R.id.LAYOUT_SCROLL);

        if(intent.hasExtra("SERIENID")) {
            s = Data.getSerieById(intent.getIntExtra("SERIENID", -1));

            layout.setOnTouchListener(new OnSwipeTouchListener(this) {
                @Override
                public void onSwipeLeft() {
                    super.onSwipeLeft();
                    if(Data.isLastSerie(s.getId())) {
                        return;
                    }
                    Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                    intent.putExtra("SERIENID", Data.getNextSerie(s.getId()).getId());
                    startActivityForResult(intent, 2);
                    DetailActivity.this.overridePendingTransition(R.anim.animation_right_1, R.anim.animation_left_1);
                }

                @Override
                public void onSwipeRight() {
                    super.onSwipeLeft();
                    if(Data.isFirstSerie(s.getId())) {
                        return;
                    }
                    Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                    intent.putExtra("SERIENID", Data.getPrevSerie(s.getId()).getId());
                    startActivityForResult(intent, 2);
                    DetailActivity.this.overridePendingTransition(R.anim.animation_right, R.anim.animation_left);
                }
            });

            if(!s.getCover().equals("")) {
                byte[] b=Base64.getDecoder().decode(s.getCover());
                ((ImageView) findViewById(R.id.COVER)).setImageBitmap(BitmapFactory.decodeByteArray(b,0,b.length));
            }
            ((TextView) findViewById(R.id.INSERT_NAME)).setText(s.getName());
            ((TextView) findViewById(R.id.INSERT_STAFFELN)).setText(s.getStaffeln() + "");
            ((TextView) findViewById(R.id.INSERT_LAUFZEIT)).setText(s.getLaufzeit() + "");
            String diensteText = "";
            for(int i = 0; i < s.getStreamingDienste().size(); i++) {
                if(i > 0) {
                    diensteText += ", ";
                }
                diensteText += s.getStreamingDienste().get(i).getAnzeigeName();
            }
            ((TextView) findViewById(R.id.INSERT_DIENSTE)).setText(diensteText);
            boolean[] checked = s.getChecked();
            if(checked != null) {
                for(int i = 0; i < Math.min(checked.length, 5); i++) {
                    final CheckBox cb = new CheckBox(this);
                    cb.setText("Staffel " + (i + 1));
                    cb.setId(i);
                    cb.setChecked(checked[i]);
                    int finalI = i;
                    cb.setOnCheckedChangeListener((button, value) -> {
                        checked[finalI] = value;
                        Data.saveData();
                    });
                    checkedLayout.addView(cb);
                }
                if(checked.length > 5) {
                     Button b = new Button(this);
                     b.setBackgroundResource(R.drawable.button_blue_gradient);
                     b.setTextColor(Color.rgb(255, 255, 255));
                     LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                     int dp=(int) Util.convertDpToPixel(10, this);
                     params.setMargins(dp,dp,dp,dp);
                     b.setLayoutParams(params);
                     b.setText("Alle anzeigen");
                     b.setOnClickListener((v) -> {
                        checkedLayout.removeView(v);
                        for(int i = 5; i < checked.length; i++) {
                            final CheckBox cb = new CheckBox(this);
                            cb.setText("Staffel " + (i + 1));
                            cb.setId(i);
                            cb.setChecked(checked[i]);
                            int finalI = i;
                            cb.setOnCheckedChangeListener((button, value) -> {
                                checked[finalI] = value;
                                Data.saveData();
                            });
                            checkedLayout.addView(cb);
                        }
                    });
                    checkedLayout.addView(b);
                }
            }
        } else {
            cancel();
        }
        ((Button) findViewById(R.id.DT_BACK)).setOnClickListener((v) -> cancel());
        ((Button) findViewById(R.id.DT_EDIT)).setOnClickListener((view1) -> {
            Intent resultIntent = new Intent(this, BearbeitenActivity.class);
            resultIntent.putExtra("SERIENID", s.getId());
            startActivityForResult(resultIntent, 1);
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                return;
            }
        } else if(requestCode == 2) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        setResult(Activity.RESULT_OK, data);
        finish();
    }



    private void cancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}