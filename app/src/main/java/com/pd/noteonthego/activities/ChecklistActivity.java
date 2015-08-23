package com.pd.noteonthego.activities;

import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.pd.noteonthego.R;
import com.pd.noteonthego.adapters.CustomChecklistAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChecklistActivity extends AppCompatActivity {

    private ListView mChecklist;
    private EditText mChecklistItem;
    private ArrayList<String> tempChecklist;
    private CustomChecklistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

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

        mChecklist = (ListView)findViewById(R.id.check_listview);
        mChecklistItem = (EditText)findViewById(R.id.edt_list_item);
        tempChecklist = new ArrayList<String>();
        adapter = new CustomChecklistAdapter(getApplicationContext(), tempChecklist);
        mChecklist.setAdapter(adapter);
    }

    public void addChecklistItem(View v){
        tempChecklist.add(mChecklistItem.getText().toString());
        adapter.updateNoteAdapter(tempChecklist);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_checklist, menu);
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
                /*if (isNoteEditedForUpdate) {
                    // for update
                    updateNote();
                } else {
                    saveNote();
                }*/
                break;
            case R.id.action_delete_note:
                //deleteNote();
                break;
            case R.id.action_set_reminder:
                //setReminder();
                break;
            case R.id.action_change_color:
                //changeColor();
                break;
            case R.id.action_share_note:
                //shareNote();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
