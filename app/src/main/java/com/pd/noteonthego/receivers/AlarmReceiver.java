package com.pd.noteonthego.receivers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.pd.noteonthego.services.WakeFulAlarmService;

public class AlarmReceiver extends WakefulBroadcastReceiver {

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

        Intent service = new Intent(context, WakeFulAlarmService.class);
        service.putExtra("noteID_alarm_service", noteID);
        startWakefulService(context, service);

    }
}
