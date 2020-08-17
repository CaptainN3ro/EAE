package com.example.nwt;

import java.io.Serializable;
import java.util.List;

public class Dienst implements Serializable {

    private String dienstName;
    private String anzeigeName;

    public Dienst() {
    }

    public Dienst(String name) {
        this(name, name);
    }

    public Dienst(String dienstName, String anzeigeName) {
        this.dienstName = dienstName;
        this.anzeigeName = anzeigeName;
    }

    public String getDienstName() {
        return dienstName;
    }

    public void setDienstName(String dienstName) {
        this.dienstName = dienstName;
    }

    public String getAnzeigeName() {
        return anzeigeName;
    }

    public void setAnzeigeName(String anzeigeName) {
        this.anzeigeName = anzeigeName;
    }

    public boolean isIncluded(List<Dienst> dienste) {
        if(dienstName.equals("")) {
            return true;
        }
        for(int i = 0; i < dienste.size(); i++) {
            if(dienste.get(i).dienstName.equals(dienstName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return anzeigeName;
    }
}
