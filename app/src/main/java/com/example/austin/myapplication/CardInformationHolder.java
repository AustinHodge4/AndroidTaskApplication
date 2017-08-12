package com.example.austin.myapplication;

import android.support.annotation.NonNull;
import android.util.Log;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hodge on 8/11/2017.
 */

class CardInformationHolder implements Comparable {
    enum CARDTYPE {
        REGULAR(0), LOCATION(1);

        private final int id;
        CARDTYPE(int id) {
            this.id = id;
        }
        public int Id(){return id;}
    }
    private String title;
    private Date date;
    private String location;
    private CARDTYPE type;

    CardInformationHolder(String title, Date date, String location, CARDTYPE type){
        this.title = title;
        this.date = date;
        this.location = location;
        this.type = type;
    }
    String Title(){ return title;}
    Date Date(){return date;}
    String Location(){return location;}

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
    @Override
    public int compareTo(@NonNull Object o) {
        return ((CardInformationHolder)o).Date().compareTo(this.date);
    }
}
