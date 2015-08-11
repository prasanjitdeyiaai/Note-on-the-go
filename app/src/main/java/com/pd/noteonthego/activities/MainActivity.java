package com.pd.noteonthego.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.pd.noteonthego.R;
import com.pd.noteonthego.adapters.CustomNoteAdapter;
import com.pd.noteonthego.helper.DBHelper;

public class MainActivity extends AppCompatActivity {

    private ListView noteListView;
    private CustomNoteAdapter noteAdapter;

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

        noteListView = (ListView)findViewById(R.id.note_list);

        DBHelper helper = new DBHelper(getApplicationContext());
        noteAdapter = new CustomNoteAdapter(getApplicationContext(), helper.getAllNotes());

        noteListView.setAdapter(noteAdapter);
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
        switch (id){
            case R.id.action_settings:
                Intent iSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(iSettings);
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
