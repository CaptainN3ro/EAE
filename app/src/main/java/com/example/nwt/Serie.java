package com.example.nwt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Serie implements Serializable {

    private static int ID_COUNTER = 0;

    private final int id;

    private boolean checked;
    private int staffeln;
    private String name;
    private List<Dienst> streamingDienste;

    public Serie(String name) {
        this();
        this.name = makeFirstCaps(name);
    }

    public Serie() {
        streamingDienste = new ArrayList<>();
        id = ++ID_COUNTER;
    }

    public Serie(String name, int staffeln, boolean checked) {
        this();
        this.name = makeFirstCaps(name);
        this.staffeln = staffeln;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = makeFirstCaps(name);
    }

    public int getStaffeln() {
        return staffeln;
    }

    public void setStaffeln(int staffeln) {
        this.staffeln = staffeln;
    }

    public List<Dienst> getStreamingDienste() {
        return streamingDienste;
    }

    public void setStreamingDienste(List<Dienst> streamingDienste) {
        this.streamingDienste = streamingDienste;
        for(Dienst d: streamingDienste) {
            d.setAnzeigeName(makeFirstCaps(d.getAnzeigeName()));
            d.setDienstName(makeFirstCaps(d.getDienstName()));
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    private String makeFirstCaps(String s) {
        String[] parts = s.split(" ");
        String newString = "";
        for(int i = 0; i < parts.length; i++) {
            if(i > 0) {
                newString += " ";
            }
            newString += parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1).toLowerCase();
        }
        return newString;
    }

    @Override
    public String toString() {
        return name;
    }
}
