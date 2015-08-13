package com.pd.noteonthego.activities;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.pd.noteonthego.helper.DBHelper;
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

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // only for lollipop and newer versions
                actionBar.setElevation(0);
            }
        }

        noteListView = (ListView) findViewById(R.id.note_list);
        mNoNotes = (TextView)findViewById(R.id.no_notes);

        DBHelper helper = new DBHelper(getApplicationContext());
        availableNotes = helper.getAllNotes();
        noteAdapter = new CustomNoteAdapter(getApplicationContext(), availableNotes);

        noteListView.setAdapter(noteAdapter);

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Note note = availableNotes.get(position);

                Intent editNote = new Intent(getApplicationContext(), NotesActivity.class);
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
                .setMessage("Do you want to continue deleting the note?")
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.note_confirm_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                try {
                                    Note note = availableNotes.get(position);

                                    DBHelper dbHelper = new DBHelper(MainActivity.this);
                                    long count = dbHelper.deleteNotes(new String[]{note.getNoteTitle(), note.getNoteCreatedTimeStamp()});

                                    if (count > 0) {
                                        Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                                        if (noteAdapter != null) {
                                            availableNotes = dbHelper.getAllNotes();
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
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            // update the availableNotes array list
            availableNotes = dbHelper.getAllNotes();
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
            case R.id.action_add_note:
                Intent iNotes = new Intent(getApplicationContext(), NotesActivity.class);
                startActivity(iNotes);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
