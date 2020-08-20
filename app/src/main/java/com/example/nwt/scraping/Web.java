package com.example.nwt.scraping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

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
            e.printStackTrace();
        }
        return false;
    }

    public void connect(String urlString) {
        disconnect();
        try {
            url = new URL(urlString);
            br = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch(Exception e) {
            e.printStackTrace();
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

    public String getLine() {
        return line;
    }

}
