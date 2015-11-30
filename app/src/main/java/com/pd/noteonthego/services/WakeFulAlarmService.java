package com.pd.noteonthego.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pd.noteonthego.R;
import com.pd.noteonthego.activities.NotesActivity;
import com.pd.noteonthego.helper.Globals;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.helper.NotePreferences;
import com.pd.noteonthego.helper.NoteType;
import com.pd.noteonthego.models.Note;
import com.pd.noteonthego.receivers.AlarmReceiver;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by pradey on 11/30/2015.
 */
public class WakeFulAlarmService extends IntentService {

    Context context = null;
    NotificationManager mNotifyMgr = null;

    public WakeFulAlarmService(){
        super("WakeFulAlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // At this point SimpleWakefulReceiver is still holding a wake lock
        // for us.  We can do whatever we need to here and then tell it that
        // it can release the wakelock.  This sample just does some slow work,
        // but more complicated implementations could take their own wake
        // lock here before releasing the receiver's.
        //
        // Note that when using this approach you should be aware that if your
        // service gets killed and restarted while in the middle of such work
        // (so the Intent gets re-delivered to perform the work again), it will
        // at that point no longer be holding a wake lock since we are depending
        // on SimpleWakefulReceiver to that for us.  If this is a concern, you can
        // acquire a separate wake lock here.

        context = getApplicationContext();

        int noteID = intent.getIntExtra("noteID_alarm_service", -1);

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

        AlarmReceiver.completeWakefulIntent(intent);
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
                }else {
                    values.put(NoteContentProvider.COLUMN_NOTES_IS_REMINDER_SET, 1);
                }
                context.getContentResolver().update(
                        NoteContentProvider.CONTENT_URI, values, whereClause, whereArgs);
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
                        .setSmallIcon(R.drawable.ic_stat_action_assignment)
                        .setContentTitle(note.getNoteTitle())
                        .setContentText(getNoteContent(note));


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

        /*
            a random number for differentiating this notification from other notifications
         */
        Random random = new Random();
        int mNotificationId = random.nextInt(9999 - 1000) + 1000;

        // Sets an ID for the notification
        // int mNotificationId = 001;
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

    /**
     *
     * @param note
     * @return note either a todo or a note
     */
    private String getNoteContent(Note note){
        if (note.getNoteType().equals(NoteType.TODO.toString())) {
            // it's a check list
            Gson gson = new Gson();

            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> checklistItemsArray = gson.fromJson(note.getNoteContent(), type);
            ArrayList<String> checkedPositions = gson.fromJson(note.getNoteTodoCheckedPositions(), type);

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < checklistItemsArray.size(); i++) {
                String s = checklistItemsArray.get(i);
                if(checkedPositions != null) {
                    if (checkedPositions.contains("" + i)) {
                        stringBuilder.append("\u2012 " + s);
                        if (i != checklistItemsArray.size() - 1) {
                            stringBuilder.append("\n");
                        }
                    } else {
                        stringBuilder.append("\u2022 " + s);
                        if (i != checklistItemsArray.size() - 1) {
                            stringBuilder.append("\n");
                        }
                    }
                }else {
                    stringBuilder.append("\u2022 " + s);
                    if (i != checklistItemsArray.size() - 1) {
                        stringBuilder.append("\n");
                    }
                }
            }
            return stringBuilder.toString();
        } else {
            // it's a note
            return note.getNoteContent();
        }
    }
}
