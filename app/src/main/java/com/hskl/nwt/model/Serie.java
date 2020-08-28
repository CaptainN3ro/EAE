package com.hskl.nwt.model;

import com.hskl.nwt.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Serie implements Serializable {

    private static int ID_COUNTER = 0;

    private final int id;

    private boolean[] checked;
    private int staffeln;
    private String name;
    private List<Dienst> streamingDienste;
    private String cover;
    private String laufzeit;


    public Serie(String name) {
        this();
        this.name = Util.makeFirstCaps(name);
    }

    public Serie() {
        this.laufzeit = "";
        this.cover = "";
        streamingDienste = new ArrayList<>();
        id = ++ID_COUNTER;
    }

    public Serie(String name, int staffeln) {
        this();
        this.name = Util.makeFirstCaps(name);
        this.staffeln = staffeln;
        this.checked = new boolean[staffeln];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Util.makeFirstCaps(name);
    }

    public int getStaffeln() {
        return staffeln;
    }

    public void setStaffeln(int staffeln) {
        this.staffeln = staffeln;
        this.checked = new boolean[staffeln];
    }

    public List<Dienst> getStreamingDienste() {
        return streamingDienste;
    }

    public void setStreamingDienste(List<Dienst> streamingDienste) {
        this.streamingDienste = streamingDienste;
        for(Dienst d: streamingDienste) {
            d.setAnzeigeName(Util.makeFirstCaps(d.getAnzeigeName()));
            d.setDienstName(Util.makeFirstCaps(d.getDienstName()));
        }
    }

    public boolean[] getChecked() {
        return checked;
    }

    public void setChecked(boolean[] checked) {
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setLaufzeit(String laufzeit){
        this.laufzeit = laufzeit;
    }
    public String getLaufzeit(){
        return laufzeit;
    }

    @Override
    public String toString() {
        return name;
    }
}
