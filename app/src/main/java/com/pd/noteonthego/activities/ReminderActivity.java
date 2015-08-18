package com.pd.noteonthego.activities;

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
import android.widget.Spinner;
import android.widget.TextView;

import com.pd.noteonthego.R;
import com.pd.noteonthego.dialogs.DateDialogFragment;
import com.pd.noteonthego.dialogs.TimeDialogFragment;

public class ReminderActivity extends AppCompatActivity implements DateDialogFragment.DateDialogListener, TimeDialogFragment.TimeDialogListener, AdapterView.OnItemSelectedListener {

    private Spinner mReminderType;
    private String event = "";
    private TextView mReminderDate, mReminderTime;

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

        mReminderType = (Spinner) findViewById(R.id.spinner_reminder_type);
        mReminderDate = (TextView) findViewById(R.id.set_reminder_date);
        mReminderTime = (TextView) findViewById(R.id.set_reminder_time);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mReminderType.setAdapter(adapter);
        mReminderType.setOnItemSelectedListener(this);
    }

    public void setDate(View v) {
        DialogFragment dialogFragment = new DateDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void setTime(View v) {
        DialogFragment dialogFragment = new TimeDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "timePicker");
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
            NavUtils.navigateUpFromSameTask(this);
            setReminder();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setReminder() {
    }

    @Override
    public void onDateDialogPositiveClick(DialogFragment dialog, int year, int month, int day) {
        mReminderDate.setText(month + "-" + day + "-" + year);
    }

    @Override
    public void onTimeDialogPositiveClick(DialogFragment dialog, int hour, int minute) {
        mReminderTime.setText(hour + ":" + minute);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        event = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
