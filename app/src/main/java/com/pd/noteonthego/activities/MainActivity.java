package com.pd.noteonthego.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pd.noteonthego.R;
import com.pd.noteonthego.adapters.CustomNoteAdapter;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.helper.NoteType;
import com.pd.noteonthego.models.Note;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView noteListView;
    private CustomNoteAdapter noteAdapter;
    private ArrayList<Note> availableNotes;
    private TextView mNoNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.all_notes);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // only for lollipop and newer versions
                actionBar.setElevation(0);
            }
        }

        noteListView = (ListView) findViewById(R.id.note_list);
        mNoNotes = (TextView)findViewById(R.id.no_notes);

        // DBHelper helper = new DBHelper(getApplicationContext());
        // availableNotes = helper.getAllNotes();

        // Retrieve note records
        Uri notes = Uri.parse(NoteContentProvider.URL);

        Cursor c = getContentResolver().query(notes, null, null, null, null);
        availableNotes = NoteContentProvider.getNoteListFromCursor(c);

        noteAdapter = new CustomNoteAdapter(getApplicationContext(), availableNotes);

        noteListView.setAdapter(noteAdapter);

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Note note = availableNotes.get(position);

                Intent editNote = new Intent(getApplicationContext(), NotesActivity.class);
                editNote.putExtra("note-type", note.getNoteType());
                editNote.putExtra("note-update", true);
                editNote.putExtra("note-id", note.getNoteID());
                editNote.putExtra("note-title", note.getNoteTitle());
                editNote.putExtra("note-timestamp", note.getNoteCreatedTimeStamp());
                editNote.putExtra("note-color", note.getNoteColor());
                startActivity(editNote);
            }
        });

        noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                alertUserForDeletion(position);
                return true;
            }
        });
    }

    private void alertUserForDeletion(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        // set title
        alertDialogBuilder.setTitle("Alert!");

        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.alert_delete_note))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.note_confirm_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // Retrieve note records
                                Uri notes = Uri.parse(NoteContentProvider.URL);

                                try {
                                    Note note = availableNotes.get(position);

                                    // DBHelper dbHelper = new DBHelper(MainActivity.this);
                                    // long count = dbHelper.deleteNoteByTitleAndTimestamp(new Strng[]{note.getNoteTitle(), note.getNoteCreatedTimeStamp()});
                                    String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
                                    String[] whereArgs = new String[]{String.valueOf(note.getNoteID())};
                                    int count = getContentResolver().delete(notes, whereClause, whereArgs);

                                    if (count > 0) {
                                        Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                                        if (noteAdapter != null) {

                                            Cursor c = getContentResolver().query(notes, null, null, null, null);
                                            availableNotes = NoteContentProvider.getNoteListFromCursor(c);

                                            //availableNotes = dbHelper.getAllNotes();
                                            noteAdapter.updateNoteAdapter(availableNotes);
                                        }
                                    }
                                } catch (ArrayIndexOutOfBoundsException ai) {
                                    ai.printStackTrace();
                                } finally {
                                    dialog.cancel();
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
    protected void onResume() {
        super.onResume();

        if (noteAdapter != null) {
            // DBHelper dbHelper = new DBHelper(getApplicationContext());
            // update the availableNotes array list
            // availableNotes = dbHelper.getAllNotes();

            // Retrieve note records
            Uri notes = Uri.parse(NoteContentProvider.URL);

            Cursor c = getContentResolver().query(notes, null, null, null, null);
            availableNotes = NoteContentProvider.getNoteListFromCursor(c);

            noteAdapter.updateNoteAdapter(availableNotes);
        }
        if(availableNotes.size() < 1){
            mNoNotes.setVisibility(View.VISIBLE);
        }else {
            mNoNotes.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                /*Intent iSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(iSettings);*/
                break;
            case R.id.action_note:
                addNote();
                break;
            case R.id.action_checklist:
                addChecklist();
                break;
            case R.id.action_sort:
                sortNotes();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void askNoteType() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        // set title
        alertDialogBuilder.setTitle("Add Note");

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setItems(R.array.note_type, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        if(which == 0){
                            // open note
                            Intent iNotes = new Intent(getApplicationContext(), NotesActivity.class);
                            iNotes.putExtra("note-type", NoteType.BLANK.toString());
                            iNotes.putExtra("note-update", false);
                            iNotes.putExtra("note-id", -1);
                            iNotes.putExtra("note-title", "");
                            iNotes.putExtra("note-timestamp", "");
                            iNotes.putExtra("note-color", "");
                            startActivity(iNotes);
                        }else {
                            // open checklist
                            // Intent iChecklist = new Intent(getApplicationContext(), ChecklistActivity.class);
                            // startActivity(iChecklist);
                            Intent iNotes = new Intent(getApplicationContext(), NotesActivity.class);
                            iNotes.putExtra("note-type", NoteType.TODO.toString());
                            iNotes.putExtra("note-update", false);
                            iNotes.putExtra("note-id", -1);
                            iNotes.putExtra("note-title", "");
                            iNotes.putExtra("note-timestamp", "");
                            iNotes.putExtra("note-color", "");
                            startActivity(iNotes);
                        }
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void addNote(){
        // open note
        Intent iNotes = new Intent(getApplicationContext(), NotesActivity.class);
        iNotes.putExtra("note-type", NoteType.BLANK.toString());
        iNotes.putExtra("note-update", false);
        iNotes.putExtra("note-id", -1);
        iNotes.putExtra("note-title", "");
        iNotes.putExtra("note-timestamp", "");
        iNotes.putExtra("note-color", "");
        startActivity(iNotes);
    }

    public void addChecklist(){
        // open checklist
        Intent iNotes = new Intent(getApplicationContext(), NotesActivity.class);
        iNotes.putExtra("note-type", NoteType.TODO.toString());
        iNotes.putExtra("note-update", false);
        iNotes.putExtra("note-id", -1);
        iNotes.putExtra("note-title", "");
        iNotes.putExtra("note-timestamp", "");
        iNotes.putExtra("note-color", "");
        startActivity(iNotes);
    }

    public void sortNotes(){

    }
}
