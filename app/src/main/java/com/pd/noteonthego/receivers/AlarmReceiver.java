package com.pd.noteonthego.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.pd.noteonthego.R;
import com.pd.noteonthego.activities.NotesActivity;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.models.Note;

public class AlarmReceiver extends BroadcastReceiver {

    NotificationManager mNotifyMgr = null;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        int noteID = -1;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            noteID = extras.getInt("reminder-identification");
        }

        // show notification
        showNotification(context, noteID);
        // play sound
        playSound();
    }

    private void playSound() {
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
                        .setSmallIcon(R.drawable.add)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                                // show note title
                        .setContentText(note.getNoteTitle());


        Intent resultIntent = new Intent(context, NotesActivity.class);
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

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }
}
