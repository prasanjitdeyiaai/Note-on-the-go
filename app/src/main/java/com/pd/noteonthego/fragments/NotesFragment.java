package com.pd.noteonthego.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pd.noteonthego.R;
import com.pd.noteonthego.activities.ReminderActivity;
import com.pd.noteonthego.dialogs.NoteColorDialogFragment;
import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.helper.NotePreferences;
import com.pd.noteonthego.helper.NoteType;
import com.pd.noteonthego.models.Note;
import com.pd.noteonthego.receivers.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    private ImageView mNoteStarred, mRemoveReminder;

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
        mNoteExtras = (TextView) getActivity().findViewById(R.id.note_extras);
        mNoteExtrasReminder = (TextView)getActivity().findViewById(R.id.note_extras_reminder);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault());
        String dateTime = simpleDateFormat.format(new Date());
        mNoteExtras.setText(dateTime);

        mNoteContainer = (RelativeLayout) getActivity().findViewById(R.id.note_container);
        mNoteStarred = (ImageView)getActivity().findViewById(R.id.note_star);
        mNoteStarred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStar();
            }
        });

        mRemoveReminder = (ImageView)getActivity().findViewById(R.id.note_remove_reminder);
        mRemoveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeReminder();
            }
        });
    }

    private void removeReminder() {
        NotePreferences preferences = new NotePreferences(getActivity());
        String requestCode = preferences.getRequestCodeForReminders(String.valueOf(noteID));

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(getActivity(), Integer.parseInt(requestCode), intent, 0);

        // And cancel the alarm.
        AlarmManager am = (AlarmManager)getActivity().getSystemService(getActivity().ALARM_SERVICE);
        am.cancel(sender);

        mNoteExtrasReminder.setText(R.string.no_reminder);
        mRemoveReminder.setVisibility(View.GONE);

        // update database
        updateNoteWithReminder();
    }

    private void updateNoteWithReminder() {
        // Update note

        ContentValues values = new ContentValues();

        String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(noteID)};

        values.put(NoteContentProvider.COLUMN_NOTES_IS_REMINDER_SET, 0);
        values.put(NoteContentProvider.COLUMN_NOTES_REMINDER_TYPE, "");
        values.put(NoteContentProvider.COLUMN_NOTES_REMINDER_DATETIME, "");

        long rowsUpdated = getActivity().getContentResolver().update(
                NoteContentProvider.CONTENT_URI, values, whereClause, whereArgs);

        if (rowsUpdated > 0) {
            Toast.makeText(getActivity(), "Alarm removed", Toast.LENGTH_SHORT).show();
        }
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
            title = content;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault());
        String dateTime = simpleDateFormat.format(new Date());

        // Note note = new Note(title, content, dateTime, "", noteColor, String.valueOf(NoteType.BLANK), "", "", "", 0, "", "");
        // long rowsAdded = dbHelper.addNote(note);

        // Add a new note
        ContentValues values = new ContentValues();

        values.put(NoteContentProvider.COLUMN_NOTES_TITLE, title);
        values.put(NoteContentProvider.COLUMN_NOTES_CONTENT, content);
        values.put(NoteContentProvider.COLUMN_NOTES_CREATED_TIMESTAMP, dateTime);

        values.put(NoteContentProvider.COLUMN_NOTES_lAST_MODIFIED_TIMESTAMP, "");
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
            Toast.makeText(getActivity(), R.string.note_saved, Toast.LENGTH_SHORT).show();
        }

        // close the activity
        getActivity().finish();
        return 1;
    }

    public void updateNote(String noteColor, int noteID) {
        //DBHelper dbHelper = new DBHelper(getActivity());

        editedNoteTitle = mNoteTitle.getText().toString();
        editedNoteContent = mNoteContent.getText().toString();

        if(!oldNoteTitle.equals(editedNoteTitle) || !oldNoteContent.equals(editedNoteContent)){
            isNoteEditedByUser = true;
        }else{
            isNoteEditedByUser = false;
        }

        // update only if user actually updates the app
        if(isNoteEditedByUser) {
            String title = mNoteTitle.getText().toString();
            String content = mNoteContent.getText().toString();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault());
            String dateTime = simpleDateFormat.format(new Date());

            // Note note = new Note(title, content, "", dateTime, noteColor, String.valueOf(NoteType.BLANK), "", "", "", 0, "", "");
            // long rowsUpdated = dbHelper.updateNote(noteID, note);

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
                Toast.makeText(getActivity(), R.string.note_updated, Toast.LENGTH_SHORT).show();
            }

            // close the activity
            getActivity().finish();
        }
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
            if(!note.getNoteLastModifiedTimeStamp().equals("")){
                mNoteExtras.setText("Created: " + note.getNoteCreatedTimeStamp() + "    Edited: " + note.getNoteLastModifiedTimeStamp());
            }else {
                mNoteExtras.setText("Created: " + note.getNoteCreatedTimeStamp());
            }
            if(note.getIsReminderSet() == 1){
                mNoteExtrasReminder.setText(getResources().getString(R.string.reminder_set) + ": " + note.getReminderDateTime() + "    " + note.getReminderType());
                mRemoveReminder.setVisibility(View.VISIBLE);
            }else {
                mNoteExtrasReminder.setText(R.string.no_reminder);
                mRemoveReminder.setVisibility(View.GONE);
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
            Toast.makeText(getActivity(), "Starred", Toast.LENGTH_SHORT).show();
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.star_white));
        }
        else {
            isStarred = 0;
            Toast.makeText(getActivity(), "Star removed", Toast.LENGTH_SHORT).show();
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.star_white_border));
        }
    }

    public void updateStar(){
        if(isStarred == 0) {
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.star_white_border));
        }
        else {
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.star_white));
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
