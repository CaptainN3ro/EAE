package com.example.nwt.model;

import android.util.Log;

import com.example.nwt.util.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Data {

    private static Runnable updateCallback;
    public static final String dataFileName = "UserData.csv";
    private static File dataFile;

    private static List<Serie> serien = new ArrayList<>();
    private static List<Dienst> dienste = new ArrayList<>();
    private static List<Serie> filteredSerien = new ArrayList<>();

    private static Dienst lastFilter;

    public static void createDataFile(File storagePath) {
        dataFile = new File(storagePath, dataFileName);
    }

    public static int getLastFilterIndex() {
        if(lastFilter == null) {
            return 0;
        }
        for(int i = 0; i < dienste.size(); i++) {
            if(dienste.get(i).getDienstName().equals(lastFilter.getDienstName())) {
                return i;
            }
        }
        return 0;
    }

    public static void createUpdateCallback(Runnable runnable) {
        updateCallback = runnable;
    }

    public static List<Serie> getFilteredSerien() {
        return filteredSerien;
    }

    public static void applyFilter(Dienst d, Runnable runnable) {
        if(d == lastFilter) {
            return;
        }
        lastFilter = d;
        filteredSerien.clear();
        for(int i = 0; i < serien.size(); i++) {
            if(d.isIncluded(serien.get(i).getStreamingDienste())) {
                filteredSerien.add(serien.get(i));
            }
        }
        runnable.run();
    }

    public static void updateMainView() {
        if(updateCallback != null) {
            updateCallback.run();
            saveData();
        }
    }

    public static boolean isLastSerie(int id) {
        if(filteredSerien.size() == 0) {
            return true;
        }
        if(filteredSerien.get(filteredSerien.size()-1).getId() == id) {
            return true;
        }
        return false;
    }

    public static boolean isFirstSerie(int id) {
        if(filteredSerien.size() == 0) {
            return true;
        }
        if(filteredSerien.get(0).getId() == id) {
            return true;
        }
        return false;
    }

    public static Serie getNextSerie(int serienId) {
        for(int i = 0; i < filteredSerien.size(); i++) {
            if(filteredSerien.get(i).getId() == serienId) {
                return filteredSerien.get((i + 1) % filteredSerien.size());
            }
        }
        return null;
    }

    public static Serie getPrevSerie(int serienId) {
        for(int i = 0; i < filteredSerien.size(); i++) {
            if(filteredSerien.get(i).getId() == serienId) {
                if(i <= 0) {
                    return filteredSerien.get(filteredSerien.size() - 1);
                } else {
                    return filteredSerien.get(i - 1);
                }
            }
        }
        return null;
    }

    public static int getDiensteCount() {
        return dienste.size();
    }

    public static Dienst getDienst(int dienstIndex) {
        return dienste.get(dienstIndex);
    }

    public static List<Dienst> getDienste() {
        return dienste;
    }

    public static int getSerienCount() {
        return serien.size();
    }

    public static Serie getSerieByIndex(int index) {
        return serien.get(index);
    }

    public static void addSerie(Serie s) {
        serien.add(s);
        updateDienste();
    }

    public static void removeSerie(Serie s) {
        for(int i = 0; i < serien.size(); i++) {
            if(serien.get(i).getId() == s.getId()) {
                serien.remove(i);
                break;
            }
        }
        updateDienste();
    }

    public static Serie getSerieById(int id) {
        for(int i = 0; i < serien.size(); i++) {
            if(serien.get(i).getId() == id) {
                return serien.get(i);
            }
        }
        return null;
    }

    public static void updateDienste() {
        dienste.clear();
        dienste.add(new Dienst("", "-Alle Serien-"));
        for(int i = 0; i < serien.size(); i++) {
            Serie s = serien.get(i);
            for(Dienst d: s.getStreamingDienste()) {
                if(!d.isIncluded(dienste)) {
                    dienste.add(d);
                }
            }
        }
        updateMainView();
    }

    public static void saveData() {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataFile, false)));
            for(int serienIndex = 0; serienIndex < serien.size(); serienIndex++) {
                Serie s = serien.get(serienIndex);
                String diensteString = "";
                for(int i = 0; i < s.getStreamingDienste().size(); i++) {
                    if(i > 0) {
                        diensteString += ", ";
                    }
                    diensteString += s.getStreamingDienste().get(i);
                }
                String checkedString = "";
                if(s.getChecked() != null) {
                    for (int i = 0; i < s.getChecked().length; i++) {
                        if (i > 0) {
                            checkedString += ", ";
                        }
                        checkedString += s.getChecked()[i];
                    }
                }
                bw.write(s.getName() + ";" + s.getStaffeln() + ";" + checkedString + ";" + diensteString + ";" + s.getCover());
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadData() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
            String line;
            while((line = br.readLine()) != null) {
                addSerieFromCSV(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateDienste();
    }

    private static void addSerieFromCSV(String line) {
        Serie s = new Serie();
        String[] tokens = line.split(";");
        s.setName(tokens[0].trim());
        s.setStaffeln(Util.parseInt(tokens[1].trim()));
        String[] subTokens = tokens[2].split(",");
        for(int i = 0; i < subTokens.length; i++) {
            s.getChecked()[i] = Util.parseBoolean(subTokens[i].trim());
        }
        subTokens = tokens[3].split(",");
        for(int i = 0; i < subTokens.length; i++) {
            s.getStreamingDienste().add(new Dienst(subTokens[i].trim()));
        }
        if(tokens.length > 4) {
            s.setCover(tokens[4]);
        }
        serien.add(s);
    }
}
