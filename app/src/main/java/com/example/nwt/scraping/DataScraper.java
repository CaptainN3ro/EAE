package com.example.nwt.scraping;

import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

import com.example.nwt.Dienst;
import com.example.nwt.Serie;
import com.example.nwt.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DataScraper {

    private static final String IMDB_URL = "https://www.imdb.com/find?q=NAME&s=tt&ttype=tv&ref_=fn_tv";
    private static final String WERSTREAMTES_URL = "https://www.werstreamt.es/serien/?q=NAME";
    private static final int TIMEOUT_IN_SECONDS = 5;
    private static boolean doneName = true, done = true;
    private static Serie serie;
    private static Toast finalToast;


    public static void scrapeData(Serie serie, Runnable saveCallback, Activity activity) {
        while(!done) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        DataScraper.serie = serie;
        done = false;
        doneName = false;

        new Thread(() -> scrapeSerie(saveCallback)).start();
        int cnt = 0;
        while(!doneName) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(cnt++ > TIMEOUT_IN_SECONDS * 10) {
                return;
            }
            continue;
        }

        finalToast = Toast.makeText(activity, serie.getName() + " Daten werden geladen...", Toast.LENGTH_SHORT);
        finalToast.setGravity(Gravity.BOTTOM, 0, 50);
        finalToast.show();
    }

    private static void scrapeSerie(Runnable saveCallback) {
        Web w = new Web(IMDB_URL.replace("NAME", serie.getName()));
        String urlString = "";
        if(w.gotoLineThatContains("findList")) {
            if(w.gotoLineThatContains("/title/")) {
                String line = w.getLine();
                urlString = line.split("href=\"")[1].split("\"")[0];
                serie.setName(line.split("result_text")[1].split("</a")[0].split(">")[2]);
            }
        }
        doneName = true;

        w.connect(IMDB_URL.split(".com")[0] + ".com" + urlString);
        if(w.gotoLineThatContains("Seasons")) {
            if(w.gotoLineThatContains("season=")) {
                String seasons = w.getLine().split("season=")[1].split("&")[0];
                serie.setStaffeln(Util.parseInt(seasons));
            }
        }

        w.connect(WERSTREAMTES_URL.replace("NAME", serie.getName()));
        if(w.gotoLineThatContains("serie/details")) {
            urlString = w.getLine().split("href=\"")[1].split("\"")[0];
        }

        w.connect(WERSTREAMTES_URL.split(".es")[0] + ".es/" + urlString);
        if(w.gotoLineThatContains("Die Serie ist aktuell bei")) {
            String dienste = w.getLine().split("Die Serie ist aktuell bei")[1].split("verfügbar")[0];
            List<Dienst> diensteList = new ArrayList<>();
            for(String s: dienste.split(",")) {
                diensteList.add(new Dienst(s.trim()));
            }
            serie.setStreamingDienste(diensteList);
        }
        w.disconnect();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finalToast.setText(serie.getName() + " Daten erfolgreich geladen!");
        finalToast.show();

        saveCallback.run();
        done = true;
    }
}
