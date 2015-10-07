package com.pd.noteonthego.helper;

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
}
