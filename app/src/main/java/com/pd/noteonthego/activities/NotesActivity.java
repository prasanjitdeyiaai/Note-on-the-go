package com.pd.noteonthego.activities;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pd.noteonthego.R;
import com.pd.noteonthego.adapters.CustomChecklistAdapter;
import com.pd.noteonthego.dialogs.NoteColorDialogFragment;
import com.pd.noteonthego.fragments.ChecklistFragment;
import com.pd.noteonthego.fragments.NotesFragment;
import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.helper.NoteType;

public class NotesActivity extends AppCompatActivity implements NotesFragment.OnFragmentInteractionListener, NoteColorDialogFragment.NoticeDialogListener, ChecklistFragment.OnChecklistFragmentInteractionListener, CustomChecklistAdapter.ClearItemListener{

    private String userSelectedNoteColor = String.valueOf(NoteColor.WHITE);
    private String noteTitleForEdit, noteTimestampForEdit;
    private boolean isNoteEditedForUpdate = false;
    private int noteID;
    private String noteType;
    NotesFragment notesFragment;
    ChecklistFragment checklistFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // actionBar.setTitle(getResources().getString(R.string.title_activity_notes));
            actionBar.setElevation(0);
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
            notesFragment = new NotesFragment();
            checklistFragment = new ChecklistFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            notesFragment.setArguments(getIntent().getExtras());
            checklistFragment.setArguments(getIntent().getExtras());
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // open for editing
            noteType = extras.getString("note-type");
            isNoteEditedForUpdate = extras.getBoolean("note-update");
            noteID = extras.getInt("note-id");

            /**
             * BELOW 2 ARE NOT USED
             */
            noteTitleForEdit = extras.getString("note-title");
            noteTimestampForEdit = extras.getString("note-timestamp");

            if(!extras.getString("note-color").equals("")) {
                userSelectedNoteColor = extras.getString("note-color");
            }
        }

        if(noteType.equals(NoteType.BLANK.toString())){
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, notesFragment).commit();
        }else {
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, checklistFragment).commit();
        }
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
        super.onCreateSupportNavigateUpTaskStack(builder);
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
            /*
            R SHOULD ALWAYS BE FROM ANDROID
             */
            case android.R.id.home:
                onBackPressed();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_delete_note:
                deleteNote();
                break;
            case R.id.action_set_reminder:
                if (isNoteEditedForUpdate) {
                    // for update
                    updateNote();
                } else {
                    saveNote();
                }
                setReminder();
                break;
            case R.id.action_change_color:
                changeColor();
                break;
            case R.id.action_share_note:
                shareNote();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isNoteEditedForUpdate) {
            // for update
            updateNote();
        } else {
            saveNote();
        }
    }

    private void shareNote() {

        if(noteType.equals(NoteType.BLANK.toString())){
            NotesFragment notesFragment = (NotesFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (notesFragment != null) {
                notesFragment.shareNoteUsingIntent();
            }
        }else {
            ChecklistFragment checklistFragment = (ChecklistFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (checklistFragment != null) {
                checklistFragment.shareNoteUsingIntent();
            }
        }
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
                                    if (count == 1) {
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
            if (isNoteEditedForUpdate)
                notesFragment.openNoteForViewing(noteID);
        }
    }

    private void saveNote() {

        if(noteType.equals(NoteType.BLANK.toString())){
            NotesFragment notesFragment = (NotesFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (notesFragment != null) {
                notesFragment.saveNoteToDatabase(userSelectedNoteColor);
            }
        }else {
            ChecklistFragment checklistFragment = (ChecklistFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (checklistFragment != null) {
                checklistFragment.saveNoteToDatabase(userSelectedNoteColor);
            }
        }
    }

    private void updateNote() {
        if(noteType.equals(NoteType.BLANK.toString())){
            NotesFragment notesFragment = (NotesFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (notesFragment != null) {
                notesFragment.updateNote(userSelectedNoteColor, noteID);
            }
        }else {
            ChecklistFragment checklistFragment = (ChecklistFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (checklistFragment != null) {
                checklistFragment.updateNote(userSelectedNoteColor, noteID);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        outState.putString("noteType", noteType);
        outState.putBoolean("isNoteEdited", isNoteEditedForUpdate);
        outState.putInt("noteID", noteID);
        outState.putString("noteTitle",noteTitleForEdit);
        outState.putString("noteTimestamp",noteTimestampForEdit);
        outState.putString("userSelectedNoteColor",userSelectedNoteColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // no need to check for null as this function is only called when savedInstance is not null
        noteType = savedInstanceState.getString("noteType");
        isNoteEditedForUpdate = savedInstanceState.getBoolean("isNoteEdited");
        noteID = savedInstanceState.getInt("noteID");
        noteTitleForEdit = savedInstanceState.getString("noteTitle");
        noteTimestampForEdit = savedInstanceState.getString("noteTimestamp");
        userSelectedNoteColor = savedInstanceState.getString("userSelectedNoteColor");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setReminder() {
        if(noteType.equals(NoteType.BLANK.toString())){
            NotesFragment notesFragment = (NotesFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (notesFragment != null) {
                notesFragment.setNoteReminder();
            }
        }else {
            ChecklistFragment checklistFragment = (ChecklistFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (checklistFragment != null) {
                checklistFragment.setNoteReminder();
            }
        }
    }

    private void changeColor() {

        if(noteType.equals(NoteType.BLANK.toString())){
            NotesFragment notesFragment = (NotesFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (notesFragment != null) {
                notesFragment.changeNoteColor();
            }
        }else {
            ChecklistFragment checklistFragment = (ChecklistFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (checklistFragment != null) {
                checklistFragment.changeNoteColor();
            }
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

        if(noteType.equals(NoteType.BLANK.toString())){
            NotesFragment notesFragment = (NotesFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (notesFragment != null) {
                notesFragment.changeNoteBackgroundColor(userSelectedNoteColor);
            }
        }else {
            ChecklistFragment checklistFragment = (ChecklistFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (checklistFragment != null) {
                checklistFragment.changeNoteBackgroundColor(userSelectedNoteColor);
            }
        }


    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onChecklistFragmentInteraction() {
        ChecklistFragment checklistFragment = (ChecklistFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (checklistFragment != null) {
            if (isNoteEditedForUpdate)
                checklistFragment.openNoteForViewing(noteID);
        }
    }

    @Override
    public void updateCheckedStatus(int position) {
        ChecklistFragment checklistFragment = (ChecklistFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (checklistFragment != null) {
            checklistFragment.uncheckDeletedItem(position);
        }
    }
}
