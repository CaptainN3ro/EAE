package com.example.nwt.util;

public class Util {


    public static int parseInt(String text) {
        if(text.equals("")) {
            return 0;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public static String makeFirstCaps(String s) {
        String[] parts = s.split(" ");
        String newString = "";
        for(int i = 0; i < parts.length; i++) {
            if(i > 0) {
                newString += " ";
            }
            if(parts[i].length() > 0) {
                newString += parts[i].substring(0, 1).toUpperCase();
            }
            if(parts[i].length() > 1) {
                newString += parts[i].substring(1).toLowerCase();
            }
        }
        return newString;
    }

}
