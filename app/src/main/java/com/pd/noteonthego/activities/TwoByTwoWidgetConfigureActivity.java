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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pd.noteonthego.R;
import com.pd.noteonthego.adapters.CustomTwoByTwoWidgetAdapter;
import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.helper.NotePreferences;
import com.pd.noteonthego.helper.NoteType;
import com.pd.noteonthego.models.Note;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * whenever configure activity is created
 * widget provider class on update is not called for the first time
 * so click event is handled here in this class
 */
public class TwoByTwoWidgetConfigureActivity extends AppCompatActivity {
    int mAppWidgetId;
    private ListView noteListView;
    private CustomTwoByTwoWidgetAdapter adapter;
    private ArrayList<Note> availableNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_by_two_widget_configure);

        noteListView = (ListView)findViewById(R.id.widget_list_twobytwo);

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

        adapter = new CustomTwoByTwoWidgetAdapter(getApplicationContext(), availableNotes);
        noteListView.setAdapter(adapter);

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Note note = availableNotes.get(position);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

                // Create an Intent to launch ExampleActivity
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
                        R.layout.twobytwo_widget);
                views.setOnClickPendingIntent(R.id.widget_twobytwo_container, pendingIntent);
                views.setTextViewText(R.id.widget_twobytwo_title, note.getNoteTitle());

                if (note.getNoteType().equals(NoteType.TODO.toString())) {
                    // it's a check list
                    Gson gson = new Gson();

                    Type type = new TypeToken<ArrayList<String>>() {
                    }.getType();
                    ArrayList<String> checklistItemsArray = gson.fromJson(note.getNoteContent(), type);
                    ArrayList<String> checkedPositions = gson.fromJson(note.getNoteTodoCheckedPositions(), type);

                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < checklistItemsArray.size(); i++) {
                        String s = checklistItemsArray.get(i);
                        if(checkedPositions != null) {
                            if (checkedPositions.contains("" + i)) {
                                if (s.length() > 20) {
                                    stringBuilder.append("- " + s.substring(0, 21) + "... ");
                                } else {
                                    stringBuilder.append("- " + s);
                                }
                                if (i != checklistItemsArray.size() - 1) {
                                    stringBuilder.append("\n");
                                }
                            } else {
                                if (s.length() > 20) {
                                    stringBuilder.append("\u2022 " + s.substring(0, 21) + "... ");
                                } else {
                                    stringBuilder.append("\u2022 " + s);
                                }
                                if (i != checklistItemsArray.size() - 1) {
                                    stringBuilder.append("\n");
                                }
                            }
                        }else {
                            if (s.length() > 20) {
                                stringBuilder.append("\u2022 " + s.substring(0, 21) + "... ");
                            } else {
                                stringBuilder.append("\u2022 " + s);
                            }
                            if (i != checklistItemsArray.size() - 1) {
                                stringBuilder.append("\n");
                            }
                        }
                    }
                    views.setTextViewText(R.id.widget_twobytwo_content, stringBuilder);
                } else {
                    // it's a note
                    views.setTextViewText(R.id.widget_twobytwo_content, note.getNoteContent());
                }

                views.setTextColor(R.id.widget_twobytwo_title, getResources().getColor(R.color.dark_holo_blue));
                views.setTextColor(R.id.widget_twobytwo_content, getResources().getColor(R.color.note_text_color_dark));

                String color = note.getNoteColor();
                if (color.equals(String.valueOf(NoteColor.YELLOW))) {
                    views.setInt(R.id.widget_twobytwo_container, "setBackgroundColor", getResources().getColor(R.color.note_yellow));
                } else if (color.equals(String.valueOf(NoteColor.BLUE))) {
                    views.setInt(R.id.widget_twobytwo_container, "setBackgroundColor", getResources().getColor(R.color.note_blue));
                } else if (color.equals(String.valueOf(NoteColor.GREEN))) {
                    views.setInt(R.id.widget_twobytwo_container, "setBackgroundColor", getResources().getColor(R.color.note_green));
                } else if (color.equals(String.valueOf(NoteColor.WHITE))) {
                    views.setInt(R.id.widget_twobytwo_container, "setBackgroundColor", getResources().getColor(R.color.note_white));
                } else {
                    views.setInt(R.id.widget_twobytwo_container, "setBackgroundColor", getResources().getColor(R.color.note_red));
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
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
                    widgetIDsAlreadyPresent = gson.fromJson(valueFromPref, type);
                }
                // add the current one
                widgetIDsAlreadyPresent.add(mAppWidgetId);

                preferences.setWidgetType(String.valueOf(mAppWidgetId), getResources().getString(R.string.widget_twobytwo));
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
