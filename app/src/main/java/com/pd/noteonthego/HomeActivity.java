package com.pd.noteonthego;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.helper.NoteType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends Activity {

    // the text box
    private EditText mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mEditor = (EditText) findViewById(R.id.editor);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditor, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save the edit text contents
        String noteToSave = mEditor.getText().toString();

        // only if note is not blank
        if (!noteToSave.trim().equals("")) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault());
            String currentDateAndTime = simpleDateFormat.format(new Date());

            // make the date easier to read
            String month = currentDateAndTime.substring(0,2);
            String date = currentDateAndTime.substring(3,5);
            // String year = currentDateAndTime.substring(6,10);
            String hour = currentDateAndTime.substring(11,13);
            String min = currentDateAndTime.substring(14,16);
            String sec = currentDateAndTime.substring(17,19);

            // Add a new note
            ContentValues values = new ContentValues();

            values.put(NoteContentProvider.COLUMN_NOTES_TITLE, "Note_" + month + date + hour + min + sec);
            values.put(NoteContentProvider.COLUMN_NOTES_CONTENT, noteToSave);
            values.put(NoteContentProvider.COLUMN_NOTES_TODO_CHECKED_POSITIONS, "");
            values.put(NoteContentProvider.COLUMN_NOTES_CREATED_TIMESTAMP, currentDateAndTime);

            values.put(NoteContentProvider.COLUMN_NOTES_lAST_MODIFIED_TIMESTAMP, currentDateAndTime);
            values.put(NoteContentProvider.COLUMN_NOTES_COLOR, String.valueOf(NoteColor.WHITE));
            values.put(NoteContentProvider.COLUMN_NOTES_TYPE, String.valueOf(NoteType.BLANK));

            values.put(NoteContentProvider.COLUMN_NOTES_IMAGE, "");
            values.put(NoteContentProvider.COLUMN_NOTES_VIDEO, "");
            values.put(NoteContentProvider.COLUMN_NOTES_AUDIO, "");

            values.put(NoteContentProvider.COLUMN_NOTES_IS_REMINDER_SET, 0);
            values.put(NoteContentProvider.COLUMN_NOTES_REMINDER_DATETIME, "");
            values.put(NoteContentProvider.COLUMN_NOTES_REMINDER_TYPE, "");
            // default star is set to 0 now
            values.put(NoteContentProvider.COLUMN_NOTES_STARRED, 0);

            Uri uri = getContentResolver().insert(
                    NoteContentProvider.CONTENT_URI, values);

            if (uri != null) {
                Toast.makeText(getApplicationContext(), R.string.note_saved, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * check for external directory
     *
     * @return true if available and writable
     */
    private boolean checkForExternalDirectory() {

        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        if (mExternalStorageAvailable && mExternalStorageWriteable) {
            return true;
        } else {
            return false;
        }
    }
}
