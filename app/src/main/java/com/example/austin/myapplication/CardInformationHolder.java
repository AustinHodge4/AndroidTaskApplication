package com.example.austin.myapplication;

import android.support.annotation.NonNull;
import android.util.Log;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hodge on 8/11/2017.
 */

class CardInformationHolder implements Comparable {
    enum CARDTYPE {
        REGULAR(0), LOCATION(1), PHONE(2);

        private final int id;
        CARDTYPE(int id) {
            this.id = id;
        }
        public int Id(){return id;}
    }
    private String title;
    private String subtitle;
    private Date date;
    private String location;
    private String phone;
    private CARDTYPE type;

    CardInformationHolder(String title, String subtitle, Date date, String location, String phone, CARDTYPE type){
        this.title = title;
        this.subtitle = subtitle;
        this.date = date;
        this.location = location;
        this.phone = phone;
        this.type = type;
    }
    String Title(){ return title;}
    String Subtitle(){
        if(subtitle == null)
            return "";
        return subtitle;}
    String DateString(){
        if(date == null)
            return "";
        String formattedDate = new SimpleDateFormat("E MM/dd", Locale.US).format(date);
        return formattedDate;
    }
    Date Date(){
        return date;
    }
    String Location(){return location;}
    String Phone(){return phone;}

    String EncodedLocation(){
        if(location.isEmpty()){return null;}
        try {
            Log.println(Log.INFO, "Google Map Link", "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(location, "UTF-8"));
            return "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(location, "UTF-8");
        }
        catch(Exception e){
            Log.println(Log.ERROR, "Error", e.getMessage());
            return null;
        }
    }
    CARDTYPE Type(){return type;}

    boolean hasDate(){return date != null;}
    boolean hasTime(){return true;}

    @Override
    public int compareTo(@NonNull Object o) {
        if(date == null){
            return 1;
        }
        return ((CardInformationHolder)o).Date().compareTo(this.date);
    }
}
