package com.pd.noteonthego.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.helper.NotePreferences;
import com.pd.noteonthego.models.Note;
import com.pd.noteonthego.receivers.AlarmReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by pradey on 11/23/2015.
 */
public class AlarmService extends Service {

    private static int count = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Retrieve note records
        Uri notes = Uri.parse(NoteContentProvider.URL);

        Cursor c = getContentResolver().query(notes, null, null, null, null);
        ArrayList<Note> noteArrayList = NoteContentProvider.getNoteListFromCursor(c);

        for (Note note : noteArrayList) {
            // if reminder is set, update it
            if(note.getIsReminderSet() == 1){
                try {
                    updateReminder(note);
                }catch (Exception e){
                    try {
                        File myFile = new File(Environment.getExternalStorageDirectory() + File.separator + "noteonthego.txt");
                        myFile.createNewFile();
                        FileOutputStream fOut = new FileOutputStream(myFile);
                        OutputStreamWriter myOutWriter =
                                new OutputStreamWriter(fOut);
                        myOutWriter.append(e.getMessage());
                        myOutWriter.close();
                        fOut.close();
                    }catch (IOException e1){
                        Toast.makeText(getApplicationContext(), e1.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateReminder(Note note) throws Exception{

        count++;

        int noteID = note.getNoteID();
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // get the old request code for reminder
        NotePreferences preferences = new NotePreferences(getApplicationContext());
        String requestCode = preferences.getRequestCodeForReminders(String.valueOf(noteID));

        // create a six digit code using day hour min and sec
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("reminder-identification", noteID);

        PendingIntent alarmIntent;

        // update the alarm
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(requestCode), intent, 0);
        preferences.setRequestCodeForReminders(String.valueOf(noteID), requestCode);

        String oldDate = note.getReminderDateTime();

        String year = oldDate.substring(6, 10);
        String month = oldDate.substring(0, 2);
        String day = oldDate.substring(3,5);
        String hour = oldDate.substring(11, 13);
        String minute = oldDate.substring(14, 16);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
        // month starts from 0 hence -1
        calendar.set(Integer.parseInt(year), (Integer.parseInt(month) - 1), Integer.parseInt(day));

        // setRepeating() lets you specify a precise custom interval
        if(note.getReminderType().toLowerCase().equals("once")){
            // one time alarm
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        } else if(note.getReminderType().toLowerCase().equals("daily")){
            // daily alarm
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        } else if(note.getReminderType().toLowerCase().equals("weekly")){
            // weekly alarm
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, alarmIntent);
        } else{
            //  monthly alarm
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }
        /*try {
            File myFile = new File(Environment.getExternalStorageDirectory() + File.separator + "noteonthegoReminder_" + count + "_.txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            myOutWriter.append(note.getReminderDateTime() + ", " + note.getReminderType() + ", " + note.getNoteTitle() +
            ", " + day + "-" + month + "-" + year + " " + hour + ":" + minute);
            myOutWriter.close();
            fOut.close();
        }catch (IOException e1){
            Toast.makeText(getApplicationContext(), "Reminder " + e1.getMessage(), Toast.LENGTH_LONG).show();
        }*/
        stopSelf();
    }
}
