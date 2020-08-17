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

    public Serie() {
        streamingDienste = new ArrayList<>();
        id = ++ID_COUNTER;
    }

    public Serie(String name, int staffeln, boolean checked) {
        this();
        this.name = name;
        this.staffeln = staffeln;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return name;
    }
}
