package com.pd.noteonthego.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;

import com.pd.noteonthego.HomeActivity;
import com.pd.noteonthego.R;
import com.pd.noteonthego.activities.SettingsActivity;

/**
 * Created by pradey on 8/12/2015.
 */
public class OutgoingCallStateReceiver extends BroadcastReceiver {

    NotificationManager mNotifyMgr = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (state != null) {
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                // call ended
                // remove notification
                if (mNotifyMgr != null) {
                    mNotifyMgr.cancelAll();
                }
            }
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isCallNotificationEnabled = sharedPref.getBoolean(SettingsActivity.KEY_CALL_NOTIFICATION, true);

        if(isCallNotificationEnabled) {
            createNotificationForNote(context);
        }
    }

    /**
     * display notification
     * @param context
     */
    private void createNotificationForNote(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat_action_assignment)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText(context.getResources().getString(R.string.add_note));

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
