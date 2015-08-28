package com.pd.noteonthego.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.pd.noteonthego.helper.NoteContentProvider;
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
    private static int requestCodeForAlarm = 0;

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
            actionBar.setDisplayHomeAsUpEnabled(false);
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

        if(year == 0 || month == 0 || day == 0){
            // no need to set reminder
            // unless date is selected
        } else{
            setReminder();
        }
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
        if (id == R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setReminder() {

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("reminder-identification", noteID);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), ++requestCodeForAlarm, intent, 0);

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
                    10 * 60 * 1000, alarmIntent);
        }

        // update the note with reminder
        updateNoteWithReminder(calendar);
        ReminderActivity.this.finish();
    }

    private void updateNoteWithReminder(Calendar calendar) {
        // Update note

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
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
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.reminder_set) + " on " + datetime, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDateDialogPositiveClick(DialogFragment dialog, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        mReminderDate.setText(month + "/" + day + "/" + year);
    }

    @Override
    public void onTimeDialogPositiveClick(DialogFragment dialog, int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        mReminderTime.setText(hour + ":" + minute);
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
