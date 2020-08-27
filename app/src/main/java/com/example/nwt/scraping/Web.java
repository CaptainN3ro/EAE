package com.example.nwt.scraping;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

public class Web {

    private BufferedReader br;
    private URL url;

    private String line;

    public Web() {

    }

    public Web(String urlString) {
        connect(urlString);
    }

    public boolean gotoLineThatContains(String token) {
        try {
            while((line = br.readLine()) != null) {
                if(line.contains(token)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public void connect(String urlString) {
        disconnect();
        try {
            url = new URL(urlString);
            br = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch(Exception e) {

        }
    }

    public void disconnect() {
        try {
            if(br != null) {
                br.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getBase64FromURL(String src) {
        try {
            Log.e("NWT", src);
            java.net.URL url = new java.net.URL(src);
            HttpsURLConnection connection = (HttpsURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            if(myBitmap!=null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                return Base64.getEncoder().encodeToString(byteArray);
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getLine() {
        return line;
    }

}
