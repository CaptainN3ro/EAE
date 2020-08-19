package com.example.nwt;

import android.provider.ContactsContract;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DataScraper {

    private static final String scrapeUrl = "https://www.imdb.com/find?q=NAME&s=tt&ttype=tv&ref_=fn_tv";
    private static final String scrapeUrlDienste = "https://www.werstreamt.es/serien/?q=NAME";
    private static boolean done = false;
    private static Serie serie;

    public static void scrapeData(Serie serie) {
        DataScraper.serie = serie;
        done = false;
        new Thread(() -> scrapeSerie()).start();
        int cnt = 0;
        while(!done) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(cnt++ > 20) {
                return;
            }
            continue;
        }
    }

    private static void scrapeSerie() {
        try {
            URL url = new URL(scrapeUrl.replace("NAME", serie.getName()));
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String urlString = "";
            boolean successful = false;
            boolean foundList = false;
            while((line = br.readLine()) != null) {
                if(line.contains("findList")) {
                    foundList = true;
                }
                if(foundList && line.contains("/title/")) {
                    urlString = line.split("href=\"")[1].split("\"")[0];
                    serie.setName(line.split("result_text")[1].split("</a")[0].split(">")[2]);
                    done = true;
                    successful = true;
                    break;
                }
            }
            conn.disconnect();
            if(!successful) {
                done = true;
                return;
            }
            url = new URL(scrapeUrl.split(".com")[0] + ".com" + urlString);
            conn = (HttpsURLConnection) url.openConnection();
            conn.connect();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            line = "";
            successful = false;
            boolean foundSeasons = false;
            boolean foundTitle = false;
            String seasons = "";
            while((line = br.readLine()) != null) {
                if(line.contains("title_wrapper")) {
                    line = br.readLine();
                    // If get Name fails in first step uncomment this!
//                    serie.setName(line.split(">")[1].split("&nbsp")[0]);
//                    done = true;
                    foundTitle = true;
                }
                if(foundTitle && line.contains("Seasons")) {
                    foundSeasons = true;
                }
                if(foundTitle && foundSeasons && line.contains("season=")) {
                    seasons = line.split("season=")[1].split("&")[0];
                    successful = true;
                    break;
                }
            }
            conn.disconnect();
            if(!successful) {
                done = true;
                return;
            }
            serie.setStaffeln(Integer.parseInt(seasons));
            url = new URL(scrapeUrlDienste.replace("NAME", serie.getName()));
            conn = (HttpsURLConnection) url.openConnection();
            conn.connect();
            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            line = "";
            successful = false;
            while((line = br.readLine()) != null) {
                if(line.contains("serie/details")) {
                    successful = true;
                    urlString = line.split("href=\"")[1].split("\"")[0];
                    break;
                }
            }
            conn.disconnect();
            if(!successful) {
                done = true;
                return;
            }
            url = new URL(scrapeUrlDienste.split(".es")[0] + ".es/" + urlString);
            conn = (HttpsURLConnection) url.openConnection();
            conn.connect();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            line = "";
            successful = false;
            String dienste = "";
            while((line = br.readLine()) != null) {
                if(line.contains("Die Serie ist aktuell bei")) {
                    successful = true;
                    dienste = line.split("Die Serie ist aktuell bei")[1].split("verfügbar")[0];
                    break;
                } else if(line.contains("Jetzt Verfügbarkeit von")) {
                    successful = true;
                    dienste = "";
                    break;
                }
            }
            conn.disconnect();
            if(!successful) {
                done = true;
                return;
            }
            List<Dienst> diensteList = new ArrayList<>();
            for(String s: dienste.split(",")) {
                diensteList.add(new Dienst(s.trim()));
            }
            serie.setStreamingDienste(diensteList);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        done = true;
    }
}
