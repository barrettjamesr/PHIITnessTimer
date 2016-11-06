package com.jrb.phiitnesstimer_paid;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by James on 17/04/2015.
 */

public class TimeFunctions {


    // Get the number of minutes from a value of seconds
    public static String getMins (int Secs) {
        int minutes = Math.round(Secs / 60);
        if (minutes <10)
            return "0" + minutes;
        else
            return String.valueOf(minutes);

    }

    // Get the number of seconds after whole minutes have been counted from a value of seconds
    public static String getSecs (int Secs) {
        int seconds = Math.round(Secs % 60);
        if (seconds <10)
            return "0" + seconds;
        else
            return String.valueOf(seconds);

    }

    // Get the total number of seconds from a sum of minutes and seconds
    public static String totalSecs (String secs, String mins) {
        int seconds = 0;
        int minutes = 0;
        if(secs!=null && !secs.equals(""))
            seconds = Integer.valueOf(secs);
        if(mins!=null && !mins.equals(""))
            minutes = Integer.valueOf(mins);

        return String.valueOf(minutes * 60 + seconds);
    }

    public static String displayTime (long millis){
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        int deciseconds = (int) (millis % 1000 / 100);
        return "" + minutes + ":" + String.format("%02d", seconds) + "." + String.format("%01d", deciseconds);

    }

    public static Integer timeSecs (String displayTime){
        return Integer.valueOf(displayTime.substring(displayTime.indexOf(":") +1, displayTime.indexOf(".")));
    }
    public static Integer timeMins (String displayTime){
        return Integer.valueOf(displayTime.substring(0, displayTime.indexOf(":")));
    }

    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
