package com.pd.noteonthego.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

/**
 * Created by pradey on 8/12/2015.
 */
public class IncomingCallStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
                // ringing
            }

            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
                // call picked up
            }

        }
        catch(Exception e)
        {
            //your custom message
            e.printStackTrace();
        }
    }
}
