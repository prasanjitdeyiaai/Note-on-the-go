package com.pd.noteonthego.activities;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pd.noteonthego.R;
import com.pd.noteonthego.dialogs.NoteColorDialogFragment;
import com.pd.noteonthego.fragments.NotesFragment;
import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.helper.NoteContentProvider;

public class NotesActivity extends AppCompatActivity implements NotesFragment.OnFragmentInteractionListener, NoteColorDialogFragment.NoticeDialogListener {

    private String userSelectedNoteColor = String.valueOf(NoteColor.WHITE);
    private String noteTitleForEdit, noteTimestampForEdit;
    private boolean isNoteEditedForUpdate = false;
    private int noteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // actionBar.setTitle(getResources().getString(R.string.title_activity_notes));
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // only for lollipop and newer versions
                actionBar.setElevation(0);
            }
            // not working
            actionBar.setDisplayHomeAsUpEnabled(true);
            // actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            NotesFragment notesFragment = new NotesFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            notesFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, notesFragment).commit();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // open for editing
            isNoteEditedForUpdate = extras.getBoolean("note-update");
            noteID = extras.getInt("note-id");
            noteTitleForEdit = extras.getString("note-title");
            noteTimestampForEdit = extras.getString("note-timestamp");
            userSelectedNoteColor = extras.getString("note-color");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            // Respond to the action bar's Up/Home button
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_save_note:
                if (isNoteEditedForUpdate) {
                    // for update
                    updateNote();
                } else {
                    saveNote();
                }
                break;
            case R.id.action_delete_note:
                deleteNote();
                break;
            case R.id.action_set_reminder:
                setReminder();
                break;
            case R.id.action_change_color:
                changeColor();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteNote() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                NotesActivity.this);

        // set title
        alertDialogBuilder.setTitle("Alert!");

        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.alert_delete_note))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.note_confirm_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                long count = -1;
                                try {

                                    // Retrieve note records
                                    Uri notes = Uri.parse(NoteContentProvider.URL);

                                    String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
                                    String[] whereArgs = new String[]{String.valueOf(noteID)};
                                    count = getContentResolver().delete(notes, whereClause, whereArgs);

                                    // DBHelper dbHelper = new DBHelper(NotesActivity.this);
                                    // count = dbHelper.deleteNoteByID(noteID);

                                    if (count > 0) {
                                        Toast.makeText(NotesActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (ArrayIndexOutOfBoundsException ai) {
                                    ai.printStackTrace();
                                } finally {
                                    dialog.cancel();
                                    if(count == 1) {
                                        // close the activity
                                        NotesActivity.this.finish();
                                    }
                                }

                            }
                        }

                )
                .

                        setNegativeButton(getResources()

                                        .

                                                getString(R.string.note_cancel_btn),

                                new DialogInterface.OnClickListener()

                                {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, just close
                                        // the dialog box and do nothing
                                        dialog.cancel();
                                    }
                                }

                        );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public void onFragmentInteraction() {
        NotesFragment notesFragment = (NotesFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (notesFragment != null) {
            if(isNoteEditedForUpdate)
                notesFragment.openNoteForViewing(noteID);
        }
    }

    private void saveNote() {
        NotesFragment notesFragment = (NotesFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (notesFragment != null) {
            notesFragment.saveNoteToDatabase(userSelectedNoteColor);
        }
    }

    private void updateNote() {
        NotesFragment notesFragment = (NotesFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (notesFragment != null) {
            notesFragment.updateNote(userSelectedNoteColor, noteID);
        }
    }

    private void setReminder() {
        NotesFragment notesFragment = (NotesFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (notesFragment != null) {
            notesFragment.setNoteReminder();
        }
    }

    private void changeColor() {
        NotesFragment notesFragment = (NotesFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (notesFragment != null) {
            notesFragment.changeNoteColor();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int selectedColor) {
        switch (selectedColor) {
            case 0:
                userSelectedNoteColor = NoteColor.YELLOW.toString();
                break;
            case 1:
                userSelectedNoteColor = NoteColor.BLUE.toString();
                break;
            case 2:
                userSelectedNoteColor = NoteColor.GREEN.toString();
                break;
            case 3:
                userSelectedNoteColor = NoteColor.RED.toString();
                break;
            case 4:
                userSelectedNoteColor = NoteColor.WHITE.toString();
                break;
            default:
                userSelectedNoteColor = NoteColor.WHITE.toString();
                break;
        }

        // change the note background
        changeNoteBackground(userSelectedNoteColor);
    }

    private void changeNoteBackground(String userSelectedNoteColor) {
        NotesFragment notesFragment = (NotesFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (notesFragment != null) {
            notesFragment.changeNoteBackgroundColor(userSelectedNoteColor);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
