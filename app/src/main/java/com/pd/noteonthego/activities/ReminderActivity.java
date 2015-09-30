package com.pd.noteonthego.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.pd.noteonthego.R;
import com.pd.noteonthego.dialogs.DateDialogFragment;
import com.pd.noteonthego.dialogs.TimeDialogFragment;
import com.pd.noteonthego.helper.Globals;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.helper.NotePreferences;
import com.pd.noteonthego.models.Note;
import com.pd.noteonthego.receivers.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReminderActivity extends AppCompatActivity implements DateDialogFragment.DateDialogListener, TimeDialogFragment.TimeDialogListener, AdapterView.OnItemSelectedListener {

    private Spinner mReminderType;
    private String event = "";
    private TextView mReminderDate, mReminderTime;
    int month, day, year, hour, minute;
    private Button mBtnSetReminder;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private int noteID = -1;
    private int requestCodeForAlarm = 0;

    private TextView mReminderExtras;
    private Button mBtnDismiss;

    private boolean isDateSelected = false, isTimeSelected = false, isPreviousReminderSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.add_reminder_title);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // only for lollipop and newer versions
                actionBar.setElevation(0);
            }
            // not working
            actionBar.setDisplayHomeAsUpEnabled(true);
            // actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // get the note ID for setting reminder
            noteID = extras.getInt("note-id-reminder");
        }

        mReminderType = (Spinner) findViewById(R.id.spinner_reminder_type);
        mReminderDate = (TextView) findViewById(R.id.set_reminder_date);
        mReminderTime = (TextView) findViewById(R.id.set_reminder_time);
        mBtnSetReminder = (Button) findViewById(R.id.btn_reminder_set);

        mReminderExtras = (TextView) findViewById(R.id.reminder_already_set);
        mBtnDismiss = (Button) findViewById(R.id.reminder_dismiss);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mReminderType.setAdapter(adapter);
        mReminderType.setOnItemSelectedListener(this);
        mReminderType.setSelection(1);

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        getPreviousReminder();
    }

    /**
     * get previously set reminder
     */
    public void getPreviousReminder(){
        if (noteID != -1) {

            // Retrieve note records
            Uri notes = Uri.parse(NoteContentProvider.URL);

            String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(noteID)};
            Cursor c = getContentResolver().query(notes, null, whereClause, whereArgs, null);
            Note note = NoteContentProvider.getNoteFromCursor(c);

            if(note.getIsReminderSet() == 1){
                if(note.getReminderType().toLowerCase().equals("once")){
                    mReminderExtras.setText( getResources().getString(R.string.reminder_set) + ": " + note.getReminderType().toLowerCase() + " on " +
                            Globals.getInstance().convertToReadableDateShort(note.getReminderDateTime()));
                }else {
                    mReminderExtras.setText( getResources().getString(R.string.reminder_set) + ": " + note.getReminderType().toLowerCase() + " from " +
                            Globals.getInstance().convertToReadableDateShort(note.getReminderDateTime()));
                }

                mBtnDismiss.setVisibility(View.VISIBLE);
                isPreviousReminderSet = true;
            }else {
                mReminderExtras.setText(R.string.no_reminder);
                mBtnDismiss.setVisibility(View.GONE);
            }
        }
    }

    /**
     * dismiss reminder
     * @param view
     */
    public void dismissReminder(View view){
        NotePreferences preferences = new NotePreferences(getApplicationContext());
        String requestCode = preferences.getRequestCodeForReminders(String.valueOf(noteID));

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(requestCode), intent, 0);

        // And cancel the alarm.
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(sender);

        mReminderExtras.setText(R.string.no_reminder);
        mBtnDismiss.setVisibility(View.GONE);

        // update database
        updateNoteWithReminder();
    }

    /**
     * update database
     * for the deleted reminder
     */
    private void updateNoteWithReminder() {
        // Update note

        ContentValues values = new ContentValues();

        String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(noteID)};

        values.put(NoteContentProvider.COLUMN_NOTES_IS_REMINDER_SET, 0);
        values.put(NoteContentProvider.COLUMN_NOTES_REMINDER_TYPE, "");
        values.put(NoteContentProvider.COLUMN_NOTES_REMINDER_DATETIME, "");

        long rowsUpdated = getContentResolver().update(
                NoteContentProvider.CONTENT_URI, values, whereClause, whereArgs);

        if (rowsUpdated > 0) {
            Toast.makeText(getApplicationContext(), R.string.reminder_removed, Toast.LENGTH_SHORT).show();
        }
    }

    public void setDate(View v) {
        DialogFragment dialogFragment = new DateDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void setTime(View v) {
        DialogFragment dialogFragment = new TimeDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void setReminder(View v) {
        // ADD A CHECK HERE
        if(isPreviousReminderSet){
            // previous reminder found
            updateReminder();
        }else {
            setReminder(-1);
        }
    }

    private void updateReminder() {
        // get the old request code for reminder
        NotePreferences preferences = new NotePreferences(getApplicationContext());
        String requestCode = preferences.getRequestCodeForReminders(String.valueOf(noteID));

        // create a new alarm
        setReminder(Integer.parseInt(requestCode));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Respond to the action bar's Up/Home button
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setReminder(int oldRequestCode) {

        final Calendar c = Calendar.getInstance();
        int dayCode  = c.get(Calendar.DATE);
        int hourCode = c.get(Calendar.HOUR_OF_DAY);
        int minuteCode = c.get(Calendar.MINUTE);
        int secondCode = c.get(Calendar.SECOND);

        String code = dayCode + "" + hourCode + "" + minuteCode + "" + secondCode;
        // convert code to int
        requestCodeForAlarm = Integer.parseInt(code);

        // create a six digit code using day hour min and sec
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("reminder-identification", noteID);

        NotePreferences preferences = new NotePreferences(getApplicationContext());
        if(oldRequestCode != -1){
            // update the alarm
            alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), oldRequestCode, intent, 0);
            preferences.setRequestCodeForReminders(String.valueOf(noteID), String.valueOf(oldRequestCode));
        }else {
            // create a new alarm
            alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCodeForAlarm, intent, 0);
            preferences.setRequestCodeForReminders(String.valueOf(noteID), String.valueOf(requestCodeForAlarm));
        }

        // Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        // month starts from 0 hence -1
        calendar.set(year, month - 1, day);

        // setRepeating() lets you specify a precise custom interval
        if(getReminderTypeInLong(event) == 0){
            // one time alarm
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }else{
            // 10 MIN INTERVAL FOR TESTING
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    // 10 * 60 * 1000, alarmIntent);
                    getReminderTypeInLong(event), alarmIntent);
        }

        // update the note with reminder
        updateNoteWithReminder(calendar);
        ReminderActivity.this.finish();
    }

    /**
     * creates a new reminder
     * updates database with alarm time
     * @param calendar
     */
    private void updateNoteWithReminder(Calendar calendar) {
        // Update note

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault());
        String datetime = simpleDateFormat.format(calendar.getTime());

        ContentValues values = new ContentValues();

        String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(noteID)};

        values.put(NoteContentProvider.COLUMN_NOTES_IS_REMINDER_SET, 1);
        values.put(NoteContentProvider.COLUMN_NOTES_REMINDER_TYPE, event);
        values.put(NoteContentProvider.COLUMN_NOTES_REMINDER_DATETIME, datetime);

        long rowsUpdated = getContentResolver().update(
                NoteContentProvider.CONTENT_URI, values, whereClause, whereArgs);

        if (rowsUpdated > 0) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.reminder_set) + " on " + Globals.getInstance().convertToReadableDateShort(datetime), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDateDialogPositiveClick(DialogFragment dialog, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        if(month == 8){
            month = 100;
        }
        if(month == 9){
            month = 101;
        }
        mReminderDate.setText(day + " " + Globals.getInstance().convertMonthToString(month) + " " + year);
        isDateSelected = true;
        if(isTimeSelected){
            mBtnSetReminder.setEnabled(true);
        }else {
            mBtnSetReminder.setEnabled(false);
        }
    }

    @Override
    public void onTimeDialogPositiveClick(DialogFragment dialog, int hour, int minute) {
        this.hour = hour;
        this.minute = minute;

        if(hour < 12){
            mReminderTime.setText(hour + " : " + minute + " am");
        }else {
            mReminderTime.setText((hour - 12) + " : " + minute + " pm");
        }
        isTimeSelected = true;
        if(isDateSelected){
            mBtnSetReminder.setEnabled(true);
        }else {
            mBtnSetReminder.setEnabled(false);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        event = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public long getReminderTypeInLong(String event) {
        if (event.equals("Daily")) {
            return 1000 * 60 * 60 * 24;
        } else if (event.equals("Weekly")) {
            return 1000 * 60 * 60 * 24 * 7;
        } else if (event.equals("Monthly")) {
            return 1000 * 60 * 60 * 24 * 30;
        } else {
            // for one time event return 0;
            return 0;
        }
    }
}
