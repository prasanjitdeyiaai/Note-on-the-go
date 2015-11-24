package com.pd.noteonthego.activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import com.pd.noteonthego.helper.NotePreferences;
import com.pd.noteonthego.models.Note;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * whenever configure activity is created
 * widget provider class on update is not called for the first time
 * so click event is handled here in this class
 */
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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Select a note");
            actionBar.setElevation(0);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // only for lollipop and newer versions
                actionBar.setElevation(0);
            }
            // not working
            actionBar.setDisplayHomeAsUpEnabled(false);
            // actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }

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

                Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                intent.putExtra("note-type", note.getNoteType());
                intent.putExtra("note-update", true);
                intent.putExtra("note-id", note.getNoteID());
                intent.putExtra("note-title", note.getNoteTitle());
                intent.putExtra("note-timestamp", note.getNoteCreatedTimeStamp());
                intent.putExtra("note-color", note.getNoteColor());
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), mAppWidgetId, intent, 0);

                // Get the layout for the App Widget and attach an on-click listener
                // to the container (entire widget)

                RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(),
                        R.layout.onebyone_widget);
                views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);
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

                // save the note id and widget id in shared preferences
                NotePreferences preferences = new NotePreferences(getApplicationContext());

                String valueFromPref = preferences.getWidgetIDForUpdate(String.valueOf(note.getNoteID()));

                ArrayList<Integer> widgetIDsAlreadyPresent = new ArrayList<Integer>();
                if(valueFromPref.equals("")){
                    // no widget saved yet
                }else {
                    StringTokenizer st = new StringTokenizer(valueFromPref, ",");

                    while(st.hasMoreTokens()) {
                        widgetIDsAlreadyPresent.add(Integer.parseInt(st.nextToken()));
                    }

                    /*Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
                    widgetIDsAlreadyPresent = gson.fromJson(valueFromPref, type);*/

                }
                // add the current one
                widgetIDsAlreadyPresent.add(mAppWidgetId);

                preferences.setWidgetType(String.valueOf(mAppWidgetId), getResources().getString(R.string.widget_onebyone));
                preferences.setWidgetIDForUpdate(String.valueOf(note.getNoteID()), widgetIDsAlreadyPresent);
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
