package com.example.nwt.scraping;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.nwt.model.Data;
import com.example.nwt.model.Dienst;
import com.example.nwt.model.Serie;
import com.example.nwt.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataScraper {

    private static final String IMDB_URL = "https://www.imdb.com/find?q=NAME&s=tt&ttype=tv&ref_=fn_tv";
    private static final String WERSTREAMTES_URL = "https://www.werstreamt.es/serien/?q=NAME";
    private static final int TIMEOUT_IN_SECONDS = 5;
    private static boolean doneName = true, done = true;
    private static Serie serie;
    private static Toast finalToast;

    public static List<String> scrapeSerien(String name, int maxCount) {
        List<String> serienListe = new ArrayList<>();
        Web w = new Web(IMDB_URL.replace("NAME", name));
        if(w.gotoLineThatContains("findList")) {
            if (w.gotoLineThatContains("result_text")) {
                String line = w.getLine();
                int currentIndex = 1;
                while(serienListe.size() < maxCount) {
                    String[] parts = line.split("</a>");
                    if(parts.length <= currentIndex) {
                        break;
                    }
                    String[] tokens = parts[currentIndex].split(">");
                    if(!serienListe.contains(tokens[tokens.length-1])) {
                        serienListe.add(tokens[tokens.length-1]);
                    }
                    currentIndex += 2;
                }
            }
        }
        return serienListe;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void scrapeData(Serie serie, Activity activity) {
        while(!done) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        DataScraper.serie = serie;
        done = false;

        new Thread(() -> scrapeSerie()).start();

        finalToast = Toast.makeText(activity, serie.getName() + " Daten werden geladen...", Toast.LENGTH_SHORT);
        finalToast.setGravity(Gravity.BOTTOM, 0, 50);
        finalToast.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void scrapeSerie() {
        Web w = new Web(IMDB_URL.replace("NAME", serie.getName()));
        String urlString = "";
        if(w.gotoLineThatContains("findList")) {
            if(w.gotoLineThatContains("result_text")) {
                String line = w.getLine();
                int currentIndex = 1;
                while(urlString.equals("")) {
                    String[] names = line.split("</a>");
                    String[] tokens = names[currentIndex].split(">");
                    if(tokens[tokens.length-1].equals(serie.getName()) || names.length <= currentIndex + 2) {
                        String[] parts = tokens[tokens.length-2].split("\"");
                        urlString = parts[parts.length-2];
                    }
                    currentIndex += 2;
                }
            }
        }

        w.connect(IMDB_URL.split(".com")[0] + ".com" + urlString);
        if(w.gotoLineThatContains("poster")) {
            if(w.gotoLineThatContains("src=")){
                String imageUrl = w.getLine().split("src=\"")[1].split("\"")[0];

                serie.setCover(Web.getBase64FromURL(imageUrl));
            }
        }

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

        Data.saveData();
        done = true;
    }
}
