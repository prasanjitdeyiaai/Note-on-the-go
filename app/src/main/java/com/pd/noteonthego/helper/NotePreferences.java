package com.pd.noteonthego.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.pd.noteonthego.R;

import java.util.ArrayList;

/**
 * Created by pradey on 9/1/2015.
 */
public class NotePreferences {

    private final Context mContext;
    private final SharedPreferences mSharedPrefs;
    private Gson gson = null;

    public NotePreferences(Context context) {
        mContext = context;
        mSharedPrefs = mContext.getSharedPreferences(
                mContext.getString(R.string.note_preference), Context.MODE_MULTI_PROCESS);
    }

    /**
     * save the request code for reminder pending intents
     * @param key
     * @param value
     */
    public void setRequestCodeForReminders(String key, String value){
        mSharedPrefs.edit().putString(key, value)
                .apply();
    }

    /**
     *
     * @param key
     * @return saved request code or empty string
     */
    public String getRequestCodeForReminders(String key){
        return mSharedPrefs.getString(key,
                "");
    }

    /**
     * save the widget id for updating
     * @param key
     * @param value
     */
    public void setWidgetIDForUpdate(String key, ArrayList<String> value){
        gson = new Gson();
        String valueToPut = gson.toJson(value);
        mSharedPrefs.edit().putString(key, valueToPut)
                .apply();
    }

    /**
     *
     * @param key
     * @return the widget id
     */
    public String getWidgetIDForUpdate(String key){
        return mSharedPrefs.getString(key,
                "");
    }

    /**
     * save the widget type
     * @param key
     * @param value
     */
    public void setWidgetType(String key, String value){
        mSharedPrefs.edit().putString(key, value)
                .apply();
    }

    /**
     *
     * @param key
     * @return the widget type
     */
    public String getWidgetType(String key){
        return mSharedPrefs.getString(key,
                "");
    }
}
