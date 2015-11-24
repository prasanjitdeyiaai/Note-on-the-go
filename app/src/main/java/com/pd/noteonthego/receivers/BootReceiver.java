package com.pd.noteonthego.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pd.noteonthego.services.AlarmService;

/**
 * Created by pradey on 11/24/2015.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context,AlarmService.class);
        context.startService(serviceIntent);
    }
}
