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

    public String convertToReadableDate(String oldDate){
        String newDate;
        String month = oldDate.substring(0, 2);
        if(month.equals("08")){
            month = "100";
        }
        if(month.equals("09")){
            month = "101";
        }
        String year = oldDate.substring(8, 10);
        String day = oldDate.substring(3,5);
        String hour = oldDate.substring(11, 13);
        String minute = oldDate.substring(14, 16);
        String ampm = oldDate.substring(20,22);
        newDate = day + "-" + convertMonthToString(Integer.parseInt(month)) + "-" + year + " " + hour + ":" + minute + " " + ampm.toLowerCase();
        return newDate;
    }

    public String convertToReadableDateShort(String oldDate){
        String newDate;
        String month = oldDate.substring(0, 2);
        if(month.equals("08")){
            month = "100";
        }
        if(month.equals("09")){
            month = "101";
        }
        String day = oldDate.substring(3,5);
        String hour = oldDate.substring(11, 13);
        String minute = oldDate.substring(14, 16);
        String ampm = oldDate.substring(20,22);
        newDate = day + " " + convertMonthToString(Integer.parseInt(month)) + " " + hour + ":" + minute + " " + ampm.toLowerCase();
        return newDate;
    }

    public String convertToReadableDateTimeYear(String oldDate){
        String newDate;
        String year = oldDate.substring(8, 10);
        String month = oldDate.substring(0, 2);
        if(month.equals("08")){
            month = "100";
        }
        if(month.equals("09")){
            month = "101";
        }
        String day = oldDate.substring(3,5);
        newDate = day + " " + convertMonthToString(Integer.parseInt(month)) + " " + year;
        return newDate;
    }

    public String convertToReadableDateExtraShort(String oldDate){
        String newDate;
        String month = oldDate.substring(0, 2);
        if(month.equals("08")){
            month = "100";
        }
        if(month.equals("09")){
            month = "101";
        }
        String day = oldDate.substring(3,5);
        newDate = day + "\n" + convertMonthToString(Integer.parseInt(month));
        return newDate;
    }

    public String convertToReadableDateForTime(String oldDate){
        String newDate;
        String hour = oldDate.substring(11, 13);
        String minute = oldDate.substring(14, 16);
        String ampm = oldDate.substring(20,22);
        newDate = hour + ":" + minute + " " + ampm.toLowerCase();
        return newDate;
    }

    public String convertToReadableDateOnly(String oldDate){
        String newDate;
        String day = oldDate.substring(3,5);
        newDate = day;
        return newDate;
    }

    public String convertMonthToString(int month){

        String monthInString = "";
        switch (month){
            case 01:
                monthInString = "Jan";
                break;
            case 02:
                monthInString = "Feb";
                break;
            case 03:
                monthInString = "Mar";
                break;
            case 04:
                monthInString = "Apr";
                break;
            case 05:
                monthInString = "May";
                break;
            case 06:
                monthInString = "Jun";
                break;
            case 07:
                monthInString = "Jul";
                break;
            case 100:
                monthInString = "Aug";
                break;
            case 101:
                monthInString = "Sep";
                break;
            case 10:
                monthInString = "Oct";
                break;
            case 11:
                monthInString = "Nov";
                break;
            case 12:
                monthInString = "Dec";
                break;
        }
        return monthInString;
    }

    public String getDateDifference(String dateToCompare) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault());
        String currentDate = simpleDateFormat.format(new Date());

        SimpleDateFormat simpleDateFormat24Hours = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String hour = simpleDateFormat24Hours.format(new Date());

        Date date1, date2;

        long diffDays = 0;
        long diffHours = 0;
        try {
            date1 = simpleDateFormat.parse(currentDate);
            date2 = simpleDateFormat.parse(dateToCompare);

            //in milliseconds
            long diff = 0;
            if(date2.after(date1)){
                diff = date2.getTime() - date1.getTime();
            }else {
                return "-1";
            }

            diffHours = diff / (60 * 60 * 1000);
            diffDays = diff / (24 * 60 * 60 * 1000);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(diffDays == 0 && diffHours > (24 - Integer.parseInt(hour.substring(0,2)))){
            return "1";
        }else if(diffDays == 0 && diffHours < (24 - Integer.parseInt(hour.substring(0,2)))){
            return "0";
        }else {
            return "" + diffDays;
        }
    }
}
