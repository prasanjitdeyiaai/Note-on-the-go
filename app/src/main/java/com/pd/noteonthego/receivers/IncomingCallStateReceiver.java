package com.pd.noteonthego.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;

import com.pd.noteonthego.HomeActivity;
import com.pd.noteonthego.R;

/**
 * Created by pradey on 8/12/2015.
 */
public class IncomingCallStateReceiver extends BroadcastReceiver {
    NotificationManager mNotifyMgr = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if(state != null) {
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    // ringing
                }

                if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    // call picked up
                    createNotificationForNote(context);
                }
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    // call ended
                    // remove notification
                    if (mNotifyMgr != null) {
                        mNotifyMgr.cancelAll();
                    }
                }
            }

        }
        catch(Exception e)
        {
            //your custom message
            e.printStackTrace();
        }
    }

    private void createNotificationForNote(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.add)
                        .setContentTitle("Note On The Go")
                        .setContentText("Add Note");

        Intent resultIntent = new Intent(context, HomeActivity.class);

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
