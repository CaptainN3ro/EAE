package com.example.nwt.util;

import android.content.Context;
import android.util.DisplayMetrics;

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

    public static boolean parseBoolean(String text) {
        if(text.equals("")) {
            return false;
        }
        try {
            return Boolean.parseBoolean(text);
        } catch (Exception ex) {
            return false;
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

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}
