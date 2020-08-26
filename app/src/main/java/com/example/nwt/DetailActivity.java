package com.example.nwt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nwt.model.Data;
import com.example.nwt.model.Serie;

import java.util.Base64;

public class DetailActivity extends AppCompatActivity {

    LinearLayout checkedLayout;
    Serie s;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(Html.fromHtml("<font color='#222222'>Details</font>"));
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();

        checkedLayout = findViewById(R.id.LAYOUT_CHECKBOXES);

        if(intent.hasExtra("SERIENID")) {
            s = Data.getSerieById(intent.getIntExtra("SERIENID", -1));
            if(!s.getCover().equals("")) {
                byte[] b=Base64.getDecoder().decode(s.getCover());
                ((ImageView) findViewById(R.id.COVER)).setImageBitmap(BitmapFactory.decodeByteArray(b,0,b.length));
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
        if(resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        setResult(Activity.RESULT_OK, data);
        finish();
    }



    private void cancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}