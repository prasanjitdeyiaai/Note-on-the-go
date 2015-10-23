package com.pd.noteonthego.fragments;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pd.noteonthego.R;
import com.pd.noteonthego.activities.NotesActivity;
import com.pd.noteonthego.activities.ReminderActivity;
import com.pd.noteonthego.dialogs.NoteColorDialogFragment;
import com.pd.noteonthego.helper.Globals;
import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.helper.NotePreferences;
import com.pd.noteonthego.helper.NoteType;
import com.pd.noteonthego.models.Note;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NotesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private EditText mNoteTitle, mNoteContent;
    private TextView mNoteExtras, mNoteExtrasReminder;
    private String TAG = "Notes Fragment";

    private RelativeLayout mNoteContainer;

    private int noteID = -1;
    private int isStarred = 0;

    private ImageView mNoteStarred;

    private boolean isNoteEditedByUser = false;
    private String oldNoteTitle, editedNoteTitle, oldNoteContent, editedNoteContent;

    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNoteTitle = (EditText) getActivity().findViewById(R.id.note_title);
        mNoteContent = (EditText) getActivity().findViewById(R.id.note_content);
        mNoteContent.requestFocus();

        mNoteExtras = (TextView) getActivity().findViewById(R.id.note_extras);
        mNoteExtrasReminder = (TextView)getActivity().findViewById(R.id.note_extras_reminder);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault());
        String dateTime = simpleDateFormat.format(new Date());
        mNoteExtras.setText(Globals.getInstance().convertToReadableDateShort(dateTime));

        mNoteContainer = (RelativeLayout) getActivity().findViewById(R.id.note_container);
        mNoteStarred = (ImageView)getActivity().findViewById(R.id.note_star);
        mNoteStarred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStar();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            mListener = (OnFragmentInteractionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction();
    }

    public int saveNoteToDatabase(String noteColor) {
        // DBHelper dbHelper = new DBHelper(getActivity());

        String title = mNoteTitle.getText().toString();
        String content = mNoteContent.getText().toString();

        if(title.equals("") && content.equals("")){
            Toast.makeText(getActivity(), R.string.note_empty, Toast.LENGTH_SHORT).show();
            // close the activity
            getActivity().finish();

            return 0;
        }
        if(title.equals("")) {
            if(content.length() > 20) {
                title = content.substring(0, 21);
            }else{
                title = content;
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault());
        String dateTime = simpleDateFormat.format(new Date());

        // Note note = new Note(title, content, dateTime, "", noteColor, String.valueOf(NoteType.BLANK), "", "", "", 0, "", "");
        // long rowsAdded = dbHelper.addNote(note);

        // Add a new note
        ContentValues values = new ContentValues();

        values.put(NoteContentProvider.COLUMN_NOTES_TITLE, title);
        values.put(NoteContentProvider.COLUMN_NOTES_CONTENT, content);
        values.put(NoteContentProvider.COLUMN_NOTES_TODO_CHECKED_POSITIONS, "");
        values.put(NoteContentProvider.COLUMN_NOTES_CREATED_TIMESTAMP, dateTime);

        values.put(NoteContentProvider.COLUMN_NOTES_lAST_MODIFIED_TIMESTAMP, dateTime);
        values.put(NoteContentProvider.COLUMN_NOTES_COLOR, noteColor);
        values.put(NoteContentProvider.COLUMN_NOTES_TYPE, String.valueOf(NoteType.BLANK));

        values.put(NoteContentProvider.COLUMN_NOTES_IMAGE, "");
        values.put(NoteContentProvider.COLUMN_NOTES_VIDEO, "");
        values.put(NoteContentProvider.COLUMN_NOTES_AUDIO, "");

        values.put(NoteContentProvider.COLUMN_NOTES_IS_REMINDER_SET, 0);
        values.put(NoteContentProvider.COLUMN_NOTES_REMINDER_DATETIME, "");
        values.put(NoteContentProvider.COLUMN_NOTES_REMINDER_TYPE, "");
        values.put(NoteContentProvider.COLUMN_NOTES_STARRED, isStarred);

        Uri uri = getActivity().getContentResolver().insert(
                NoteContentProvider.CONTENT_URI, values);

        if (uri != null) {
            // note saved successfully
            this.noteID = (int) ContentUris.parseId(uri);
        }

        // close the activity
        getActivity().finish();
        return 1;
    }

    public void updateNote(String noteColor, int noteID) {

        editedNoteTitle = mNoteTitle.getText().toString();
        editedNoteContent = mNoteContent.getText().toString();

        if(!oldNoteTitle.equals(editedNoteTitle) || !oldNoteContent.equals(editedNoteContent)){
            isNoteEditedByUser = true;
        }

        // update only if user actually updates the app
        if(isNoteEditedByUser) {
            String title = mNoteTitle.getText().toString();
            String content = mNoteContent.getText().toString();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault());
            String dateTime = simpleDateFormat.format(new Date());

            // Update note
            ContentValues values = new ContentValues();

            String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(noteID)};

            values.put(NoteContentProvider.COLUMN_NOTES_TITLE, title);
            values.put(NoteContentProvider.COLUMN_NOTES_CONTENT, content);

            values.put(NoteContentProvider.COLUMN_NOTES_lAST_MODIFIED_TIMESTAMP, dateTime);
            values.put(NoteContentProvider.COLUMN_NOTES_COLOR, noteColor);
            values.put(NoteContentProvider.COLUMN_NOTES_STARRED, isStarred);

            long rowsUpdated = getActivity().getContentResolver().update(
                    NoteContentProvider.CONTENT_URI, values, whereClause, whereArgs);

            if (rowsUpdated > 0) {
                // Toast.makeText(getActivity(), R.string.note_updated, Toast.LENGTH_SHORT).show();
                updateWidgets(noteID);
            }

            // close the activity
            getActivity().finish();
        }
    }

    private void updateWidgets(int noteID) {

        Gson gson = new Gson();
        String mAppWidgetIDs = "";
        ArrayList<Integer> mAppWidgetIDList = new ArrayList<Integer>();

        NotePreferences notePreferences = new NotePreferences(getActivity());
        if(notePreferences.getWidgetIDForUpdate(String.valueOf(noteID)).equals("")){
            // no widget to update
            Log.e("NotesFragment", "No widget");
        }else {
            mAppWidgetIDs = notePreferences.getWidgetIDForUpdate(String.valueOf(noteID));
            Log.e("Notes Frag string ", mAppWidgetIDs);

            StringTokenizer st = new StringTokenizer(mAppWidgetIDs, ",");

            while(st.hasMoreTokens()) {
                mAppWidgetIDList.add(Integer.parseInt(st.nextToken()));
            }

            /*Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
            mAppWidgetIDList = gson.fromJson(mAppWidgetIDs, type);*/

            // Retrieve note records
            Uri notes = Uri.parse(NoteContentProvider.URL);

            String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(noteID)};
            Cursor c = getActivity().getContentResolver().query(notes, null, whereClause, whereArgs, null);
            Note note = NoteContentProvider.getNoteFromCursor(c);

            for(int i=0; i<mAppWidgetIDList.size(); i++){
                if(notePreferences.getWidgetType(String.valueOf(mAppWidgetIDList.get(i))).equals(getResources().getString(R.string.widget_onebyone))){
                    updateOneByOneWidget(mAppWidgetIDList.get(i), note);
                }else {
                    updateTwoByTwoWidget(mAppWidgetIDList.get(i), note);
                }
            }
        }
    }

    private void updateOneByOneWidget(int widgetID, Note note){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());

        // Create an Intent to launch ExampleActivity
        Intent intent = new Intent(getActivity(), NotesActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        intent.putExtra("note-type", note.getNoteType());
        intent.putExtra("note-update", true);
        intent.putExtra("note-id", note.getNoteID());
        intent.putExtra("note-title", note.getNoteTitle());
        intent.putExtra("note-timestamp", note.getNoteCreatedTimeStamp());
        intent.putExtra("note-color", note.getNoteColor());
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), widgetID, intent, 0);

        RemoteViews views = new RemoteViews(getActivity().getPackageName(),
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
        appWidgetManager.updateAppWidget(widgetID, views);
    }

    private void updateTwoByTwoWidget(int widgetID, Note note){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());

        // Create an Intent to launch ExampleActivity
        Intent intent = new Intent(getActivity(), NotesActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        intent.putExtra("note-type", note.getNoteType());
        intent.putExtra("note-update", true);
        intent.putExtra("note-id", note.getNoteID());
        intent.putExtra("note-title", note.getNoteTitle());
        intent.putExtra("note-timestamp", note.getNoteCreatedTimeStamp());
        intent.putExtra("note-color", note.getNoteColor());
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), widgetID, intent, 0);

        RemoteViews views = new RemoteViews(getActivity().getPackageName(),
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
                if (checkedPositions != null) {
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
        appWidgetManager.updateAppWidget(widgetID, views);
    }

    public void setNoteReminder() {
        Intent iReminder = new Intent(getActivity(), ReminderActivity.class);
        iReminder.putExtra("note-id-reminder", noteID);
        startActivity(iReminder);
    }


    public void changeNoteColor() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoteColorDialogFragment();
        dialog.show(getFragmentManager(), "NoteColorDialogFragment");

        isNoteEditedByUser = true;
    }

    public void changeNoteBackgroundColor(String backgroundColor) {

        if (mNoteContainer != null) {
            if (backgroundColor.equals(NoteColor.YELLOW.toString())) {
                mNoteContainer.setBackgroundColor(getResources().getColor(R.color.note_yellow));
            } else if (backgroundColor.equals(NoteColor.BLUE.toString())) {
                mNoteContainer.setBackgroundColor(getResources().getColor(R.color.note_blue));
            } else if (backgroundColor.equals(NoteColor.GREEN.toString())) {
                mNoteContainer.setBackgroundColor(getResources().getColor(R.color.note_green));
            } else if (backgroundColor.equals(NoteColor.WHITE.toString())) {
                mNoteContainer.setBackgroundColor(getResources().getColor(R.color.note_white));
            } else {
                mNoteContainer.setBackgroundColor(getResources().getColor(R.color.note_red));
            }
        }
    }

    public void openNoteForViewing(int noteID) {
        // DBHelper dbHelper = new DBHelper(getActivity());
        if (noteID != -1) {

            // first update the noteID for use in setting reminder
            this.noteID = noteID;

            // Retrieve note records
            Uri notes = Uri.parse(NoteContentProvider.URL);

            String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(noteID)};
            Cursor c = getActivity().getContentResolver().query(notes, null, whereClause, whereArgs, null);
            Note note = NoteContentProvider.getNoteFromCursor(c);

            // Note note = dbHelper.getNote(noteTitle, noteTimestamp);

            oldNoteTitle = note.getNoteTitle();
            oldNoteContent = note.getNoteContent();

            // fill the edit texts
            mNoteTitle.setText(note.getNoteTitle());
            mNoteContent.setText(note.getNoteContent());
            mNoteContent.setSelection(note.getNoteContent().length());

            if(!note.getNoteLastModifiedTimeStamp().equals("")){
                mNoteExtras.setText(Globals.getInstance().convertToReadableDateShort(note.getNoteLastModifiedTimeStamp()));
            }else {
                mNoteExtras.setText(Globals.getInstance().convertToReadableDateShort(note.getNoteCreatedTimeStamp()));
            }
            if(note.getIsReminderSet() == 1){
                if(Globals.getInstance().getDateDifference(note.getReminderDateTime()).equals("0")){
                    // TODAY
                    if(note.getReminderType().toLowerCase().equals("once")){
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " today " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }else if(note.getReminderType().toLowerCase().equals("daily")){
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }else if(note.getReminderType().toLowerCase().equals("weekly")){
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " on wednesdays " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }else {
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " on " + Globals.getInstance().convertToReadableDateOnly(note.getReminderDateTime()) + " " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }
                }else if(Globals.getInstance().getDateDifference(note.getReminderDateTime()).equals("1")){
                    // TOMORROW
                    if(note.getReminderType().toLowerCase().equals("once")){
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " tomorrow " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }else if(note.getReminderType().toLowerCase().equals("daily")){
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }else if(note.getReminderType().toLowerCase().equals("weekly")){
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " on wednesdays " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }else {
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " on " + Globals.getInstance().convertToReadableDateOnly(note.getReminderDateTime()) + " " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }
                }else if(Globals.getInstance().getDateDifference(note.getReminderDateTime()).equals("-1")){
                    // YESTERDAY
                    if(note.getReminderType().toLowerCase().equals("once")){
                        // do nothing here as it is completed
                    }else if(note.getReminderType().toLowerCase().equals("daily")){
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }else if(note.getReminderType().toLowerCase().equals("weekly")){
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " on wednesdays " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }else {
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " on " + Globals.getInstance().convertToReadableDateOnly(note.getReminderDateTime()) + " " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }
                }else {
                    if(note.getReminderType().toLowerCase().equals("once")){
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " on " + Globals.getInstance().convertToReadableDateShort(note.getReminderDateTime()));
                    }else {
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " from " + Globals.getInstance().convertToReadableDateShort(note.getReminderDateTime()));
                    }
                }

            }else {
                mNoteExtrasReminder.setText(R.string.no_reminder);
            }
            // update the star
            isStarred = note.getIsStarred();
            updateStar();
            changeNoteBackgroundColor(note.getNoteColor());
        }

    }

    public void shareNoteUsingIntent(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mNoteTitle.getText().toString() + " - " +mNoteContent.getText().toString());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.send_to)));
    }

    public void addStar(){
        isNoteEditedByUser = true;
        if(isStarred == 0) {
            isStarred = 1;
            Toast.makeText(getActivity(), R.string.star_note, Toast.LENGTH_SHORT).show();
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_toggle_star));
        }
        else {
            isStarred = 0;
            Toast.makeText(getActivity(), R.string.unstar_note, Toast.LENGTH_SHORT).show();
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_toggle_star_outline));
        }
    }

    public void updateStar(){
        if(isStarred == 0) {
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_toggle_star_outline));
        }
        else {
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_toggle_star));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
