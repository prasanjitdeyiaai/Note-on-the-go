package com.pd.noteonthego.activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.pd.noteonthego.R;
import com.pd.noteonthego.adapters.CustomWidgetAdapter;
import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.models.Note;

import java.util.ArrayList;

public class OneByOneWidgetConfigureActivity extends AppCompatActivity {
    int mAppWidgetId;
    private ListView noteListView;
    private CustomWidgetAdapter adapter;
    private ArrayList<Note> availableNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_by_one_widget_configure);

        noteListView = (ListView)findViewById(R.id.widget_list);

        // get the widget ID
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // Retrieve note records
        Uri notes = Uri.parse(NoteContentProvider.URL);

        Cursor c = getContentResolver().query(notes, null, null, null, null);
        availableNotes = NoteContentProvider.getNoteListFromCursor(c);

        adapter = new CustomWidgetAdapter(getApplicationContext(), availableNotes);
        noteListView.setAdapter(adapter);

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Note note = availableNotes.get(position);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

                RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(),
                        R.layout.onebyone_widget);
                views.setTextViewText(R.id.widget_title, note.getNoteTitle());
                views.setTextColor(R.id.widget_title, getResources().getColor(R.color.dark_holo_blue));

                String color = note.getNoteColor();
                if (color.equals(String.valueOf(NoteColor.YELLOW))) {
                    views.setInt(R.id.widget_container, "setBackgroundColor", getResources().getColor(R.color.note_yellow));
                } else if (color.equals(String.valueOf(NoteColor.BLUE))) {
                    views.setInt(R.id.widget_container, "setBackgroundColor", getResources().getColor(R.color.note_blue));
                } else if (color.equals(String.valueOf(NoteColor.GREEN))) {
                    views.setInt(R.id.widget_container, "setBackgroundColor", getResources().getColor(R.color.note_green));
                } else if (color.equals(String.valueOf(NoteColor.WHITE))) {
                    views.setInt(R.id.widget_container, "setBackgroundColor", getResources().getColor(R.color.note_white));
                } else {
                    views.setInt(R.id.widget_container, "setBackgroundColor", getResources().getColor(R.color.note_red));
                }

                appWidgetManager.updateAppWidget(mAppWidgetId, views);

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_one_by_one_widget_configure, menu);
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
}
