package com.pd.noteonthego.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.pd.noteonthego.R;
import com.pd.noteonthego.activities.NotesActivity;
import com.pd.noteonthego.helper.Globals;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.helper.NotePreferences;
import com.pd.noteonthego.models.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    NotificationManager mNotifyMgr = null;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        int noteID = 0;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            noteID = extras.getInt("reminder-identification");
        }

        // Retrieve note records
        Uri notes = Uri.parse(NoteContentProvider.URL);

        String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(noteID)};
        Cursor c = context.getContentResolver().query(notes, null, whereClause, whereArgs, null);
        Note note = NoteContentProvider.getNoteFromCursor(c);

        // CHECK IF THE NOTE IS NOT DELETED
        if(checkForExistingNote(context, noteID)) {
            // update the reminder based on event type
            updateReminder(context, noteID);
            // show notification

            String eventType = note.getReminderType();
            if(eventType.equals("Monthly")){
                // no need to show notification
            }else {
                showNotification(context, noteID);
            }
        }
    }

    private void updateReminder(final Context context, final int noteID) {
        new Thread(){
            @Override
            public void run() {
                super.run();

                // Retrieve note records
                Uri notes = Uri.parse(NoteContentProvider.URL);

                String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
                String[] whereArgs = new String[]{String.valueOf(noteID)};
                Cursor c = context.getContentResolver().query(notes, null, whereClause, whereArgs, null);
                Note note = NoteContentProvider.getNoteFromCursor(c);

                boolean isReminderCompleted = false;

                String eventType = note.getReminderType();
                if(eventType.equals("Once")){
                    // clear the reminder and update isReminderSet = 0;
                    isReminderCompleted = true;
                }else if(eventType.equals("Daily")){
                    isReminderCompleted = false;
                }else if(eventType.equals("Weekly")){
                    isReminderCompleted = false;
                }else if(eventType.equals("Monthly")){
                    isReminderCompleted = false;
                    checkForTodayAlarmsAndBehaveAppropriately(context, note);
                }

                ContentValues values = new ContentValues();

                if(isReminderCompleted){
                    values.put(NoteContentProvider.COLUMN_NOTES_IS_REMINDER_SET, 0);

                    context.getContentResolver().update(
                            NoteContentProvider.CONTENT_URI, values, whereClause, whereArgs);
                }
                /*else {
                    values.put(NoteContentProvider.COLUMN_NOTES_IS_REMINDER_SET, 1);
                }*/
            }
        }.start();

    }

    private void checkForTodayAlarmsAndBehaveAppropriately(Context context, Note note) {
        int dateDiff = Integer.parseInt(Globals.getInstance().getDateDifference(note.getReminderDateTime()));
        if(dateDiff == 0){
            // show alarm
            showNotification(context, note.getNoteID());
            updateMonthlyReminder(context, note);

        } else if(dateDiff == -1){
            // show alarm
            showNotification(context, note.getNoteID());
            updateMonthlyReminder(context, note);
        } else {
            //reschedule me to check again tomorrow

            NotePreferences preferences = new NotePreferences(context);
            String requestCode = preferences.getRequestCodeForReminders(String.valueOf(note.getNoteID()));

            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, Integer.parseInt(requestCode), intent, 0);
            AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            // And cancel the alarm.
            AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(sender);

            // schedule alarm for today + 1 day
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);

            // schedule the alarm
            alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }

    /**
     * add 1 month to reminder and update the database
     * @param context
     * @param note
     */
    private void updateMonthlyReminder(Context context, Note note) {
        String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(note.getNoteID())};

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault());
        String newDateTime = "";
        Calendar c = Calendar.getInstance();

        try {
            // add 1 month
            c.setTime(simpleDateFormat.parse(note.getReminderDateTime()));
            c.add(Calendar.MONTH, 1);
        }catch (ParseException pe){
            pe.printStackTrace();
        }
        newDateTime = simpleDateFormat.format(c.getTime());

        ContentValues values = new ContentValues();
        values.put(NoteContentProvider.COLUMN_NOTES_REMINDER_DATETIME, newDateTime);

        context.getContentResolver().update(
                NoteContentProvider.CONTENT_URI, values, whereClause, whereArgs);
    }

    /**
     * display notification on status bar
     *
     * @param context
     */
    private void showNotification(Context context, int noteID) {

        // Retrieve note records
        Uri notes = Uri.parse(NoteContentProvider.URL);

        String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(noteID)};
        Cursor c = context.getContentResolver().query(notes, null, whereClause, whereArgs, null);
        Note note = NoteContentProvider.getNoteFromCursor(c);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_action_assignment)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                                // show note title
                        .setContentText(note.getNoteTitle());


        Intent resultIntent = new Intent(context, NotesActivity.class);
        resultIntent.putExtra("note-type", note.getNoteType());
        resultIntent.putExtra("note-update", true);
        resultIntent.putExtra("note-id", note.getNoteID());
        resultIntent.putExtra("note-title", note.getNoteTitle());
        resultIntent.putExtra("note-timestamp", note.getNoteCreatedTimeStamp());
        resultIntent.putExtra("note-color", note.getNoteColor());

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        // play default sound
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }

    public boolean checkForExistingNote(Context context, int noteID){
        // Retrieve note records
        Uri notes = Uri.parse(NoteContentProvider.URL);

        String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(noteID)};
        Cursor c = context.getContentResolver().query(notes, null, whereClause, whereArgs, null);
        Note note = NoteContentProvider.getNoteFromCursor(c);
        if(note.getNoteTitle() == null){
            // need to cancel the alarm

            return false;
        }else {
            return true;
        }
    }
}
