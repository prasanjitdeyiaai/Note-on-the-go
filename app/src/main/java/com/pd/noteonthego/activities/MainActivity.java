package com.pd.noteonthego.activities;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.pd.noteonthego.R;
import com.pd.noteonthego.adapters.CustomNoteAdapter;
import com.pd.noteonthego.dialogs.SortDialogFragment;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.helper.NoteType;
import com.pd.noteonthego.models.Note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SortDialogFragment.SortDialogListener{

    private ListView noteListView;
    private CustomNoteAdapter noteAdapter;
    private ArrayList<Note> availableNotes, tempSortedNotes;
    private TextView mNoNotes;
    // private EditText mEdtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the default values in settings preference
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.all_notes);
            actionBar.setElevation(0);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // only for lollipop and newer versions
                actionBar.setElevation(0);
            }
        }

        noteListView = (ListView) findViewById(R.id.note_list);
        mNoNotes = (TextView)findViewById(R.id.no_notes);

        // get the saved sort order here and in on resume
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sortOrderValue = sharedPref.getString(SettingsActivity.KEY_SORT_ORDER, "0");

        // Retrieve note records
        Uri notes = Uri.parse(NoteContentProvider.URL);

        Cursor c = getContentResolver().query(notes, null, null, null, getSortOrder(sortOrderValue));

        availableNotes = new ArrayList<Note>();
        availableNotes.clear();
        availableNotes.addAll(getTempSortedNotes(sortOrderValue, c));

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

    /**
     * starred, notes first and todo first will use default sort from content provider
     * @param sortOrderValue
     * @return the sort order
     */
    private String getSortOrder(String sortOrderValue){
        String sortOrder = "";
        switch (Integer.parseInt(sortOrderValue)){
            case 0:
                // nothing selected so default sort
                sortOrder = "";
                break;
            case 1:
                // last created first
                sortOrder = NoteContentProvider.COLUMN_NOTES_CREATED_TIMESTAMP + " DESC";
                break;
            case 2:
                // last edited first
                sortOrder = NoteContentProvider.COLUMN_NOTES_lAST_MODIFIED_TIMESTAMP + " DESC";
                break;
            case 3:
                // always show starred first
                sortOrder = "";
                break;
            case 4:
                // always show notes first
                sortOrder = "";
                break;
            case 5:
                // always show todo first
                sortOrder = "";
                break;
            default:
                // nothing selected so default sort
                sortOrder = "";
                break;
        }
        return sortOrder;
    }

    /**
     * for starred, note only and todo only will use other temporary lists for sorting
     * @param sortOrderValue
     * @param c
     * @return a temporary list of notes
     */
    private ArrayList<Note> getTempSortedNotes(String sortOrderValue, Cursor c){
        List<Note> notesOnly = new ArrayList<Note>();
        List<Note> todoOnly = new ArrayList<Note>();

        if(sortOrderValue.equals("3")){
            // starred first
            List<Note> notesStarred = new ArrayList<Note>();
            List<Note> notesNotStarred = new ArrayList<Note>();
            tempSortedNotes = NoteContentProvider.getNoteListFromCursor(c);
            for(Note note: tempSortedNotes){
                if(note.getIsStarred() ==  1){
                    notesStarred.add(note);
                }else {
                    notesNotStarred.add(note);
                }
            }
            tempSortedNotes.clear();
            tempSortedNotes.addAll(notesStarred);
            tempSortedNotes.addAll(notesNotStarred);
            notesStarred.clear();
            notesNotStarred.clear();
        }else if(sortOrderValue.equals("4")){
            // notes first
            tempSortedNotes = NoteContentProvider.getNoteListFromCursor(c);
            for(Note note: tempSortedNotes){
                if(note.getNoteType().equals(NoteType.BLANK.toString())){
                    notesOnly.add(note);
                }else if(note.getNoteType().equals(NoteType.TODO.toString())){
                    todoOnly.add(note);
                }else {
                    // do nothing
                }
            }
            tempSortedNotes.clear();
            tempSortedNotes.addAll(notesOnly);
            tempSortedNotes.addAll(todoOnly);
            notesOnly.clear();
            todoOnly.clear();
        }else if(sortOrderValue.equals("5")){
            tempSortedNotes = NoteContentProvider.getNoteListFromCursor(c);
            for(Note note: tempSortedNotes){
                if(note.getNoteType().equals(NoteType.BLANK.toString())){
                    notesOnly.add(note);
                }else if(note.getNoteType().equals(NoteType.TODO.toString())){
                    todoOnly.add(note);
                }else {
                    // do nothing
                }
            }
            tempSortedNotes.clear();
            tempSortedNotes.addAll(todoOnly);
            tempSortedNotes.addAll(notesOnly);
            notesOnly.clear();
            todoOnly.clear();
        }else {
            tempSortedNotes = NoteContentProvider.getNoteListFromCursor(c);
        }
        return tempSortedNotes;
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

                                    String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
                                    String[] whereArgs = new String[]{String.valueOf(note.getNoteID())};
                                    int count = getContentResolver().delete(notes, whereClause, whereArgs);

                                    if (count > 0) {
                                        Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                                        if (noteAdapter != null) {

                                            // get the saved sort order
                                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            String sortOrderValue = sharedPref.getString(SettingsActivity.KEY_SORT_ORDER, "0");

                                            Cursor c = getContentResolver().query(notes, null, null, null, getSortOrder(sortOrderValue));

                                            availableNotes = new ArrayList<Note>();
                                            availableNotes.clear();
                                            availableNotes.addAll(getTempSortedNotes(sortOrderValue, c));

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

            // get the saved sort order
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String sortOrderValue = sharedPref.getString(SettingsActivity.KEY_SORT_ORDER, "0");

            // Retrieve note records
            Uri notes = Uri.parse(NoteContentProvider.URL);

            Cursor c = getContentResolver().query(notes, null, null, null, getSortOrder(sortOrderValue));

            availableNotes = new ArrayList<Note>();
            availableNotes.clear();
            availableNotes.addAll(getTempSortedNotes(sortOrderValue, c));

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

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                // this is your adapter that will be filtered
                noteAdapter.getFilter().filter(newText.toString().toLowerCase());
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);

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
                Intent iSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(iSettings);
                break;
            case R.id.action_note:
                addNote();
                break;
            case R.id.action_checklist:
                addChecklist();
                break;
            case R.id.action_about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
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
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new SortDialogFragment();
        dialog.show(getSupportFragmentManager(), "SortDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int selectedSortIndex) {
        ArrayList<Note> tempList = new ArrayList<Note>();
        List<Note> notesOnly = new ArrayList<Note>();
        List<Note> todoOnly = new ArrayList<Note>();

        switch (selectedSortIndex) {
            case 0:
                // last created first
                tempList.addAll(availableNotes);
                availableNotes.clear();
                Collections.sort(tempList, Note.noteLastCreatedAscComparator);
                availableNotes.addAll(tempList);
                noteAdapter.updateNoteAdapter(availableNotes);
                break;
            case 1:
                // last edited
                tempList.addAll(availableNotes);
                availableNotes.clear();
                Collections.sort(tempList, Note.noteLastEditedAscComparator);
                availableNotes.addAll(tempList);
                noteAdapter.updateNoteAdapter(availableNotes);
                break;
            case 2:
                // starred first
                List<Note> notesStarred = new ArrayList<Note>();
                List<Note> notesNotStarred = new ArrayList<Note>();
                for(Note note: availableNotes){
                    if(note.getIsStarred() ==  1){
                        notesStarred.add(note);
                    }else {
                        notesNotStarred.add(note);
                    }
                }
                availableNotes.clear();
                availableNotes.addAll(notesStarred);
                availableNotes.addAll(notesNotStarred);
                noteAdapter.updateNoteAdapter(availableNotes);
                notesStarred.clear();
                notesNotStarred.clear();
                break;
            case 3:
                // notes first
                for(Note note: availableNotes){
                    if(note.getNoteType().equals(NoteType.BLANK.toString())){
                        notesOnly.add(note);
                    }else if(note.getNoteType().equals(NoteType.TODO.toString())){
                        todoOnly.add(note);
                    }else {
                        // do nothing
                    }
                }
                availableNotes.clear();
                availableNotes.addAll(notesOnly);
                availableNotes.addAll(todoOnly);
                noteAdapter.updateNoteAdapter(availableNotes);
                notesOnly.clear();
                todoOnly.clear();
                break;
            case 4:
                // todo first
                for(Note note: availableNotes){
                    if(note.getNoteType().equals(NoteType.BLANK.toString())){
                        notesOnly.add(note);
                    }else if(note.getNoteType().equals(NoteType.TODO.toString())){
                        todoOnly.add(note);
                    }else {
                        // do nothing
                    }
                }
                availableNotes.clear();
                availableNotes.addAll(todoOnly);
                availableNotes.addAll(notesOnly);
                noteAdapter.updateNoteAdapter(availableNotes);
                notesOnly.clear();
                todoOnly.clear();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
