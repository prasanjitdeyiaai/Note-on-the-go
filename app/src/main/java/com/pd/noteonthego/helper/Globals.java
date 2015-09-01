package com.pd.noteonthego.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Prasanjit on 31-08-2015.
 */
public class Globals {
    private static Globals ourInstance = new Globals();

    public static Globals getInstance() {
        return ourInstance;
    }

    private Globals() {
    }

    public Date convertToReadableDate(String oldDate){

        Date convertedDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa", Locale.getDefault());
        try {
            convertedDate = simpleDateFormat.parse(oldDate);
        }catch (ParseException pe){
            pe.printStackTrace();
        }

        return convertedDate;
    }
}
