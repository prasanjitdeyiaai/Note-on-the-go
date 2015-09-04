package com.pd.noteonthego.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pd.noteonthego.R;
import com.pd.noteonthego.activities.ReminderActivity;
import com.pd.noteonthego.adapters.CustomChecklistAdapter;
import com.pd.noteonthego.dialogs.NoteColorDialogFragment;
import com.pd.noteonthego.helper.Globals;
import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.helper.NoteType;
import com.pd.noteonthego.models.Note;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChecklistFragment.OnChecklistFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ChecklistFragment extends Fragment {

    private OnChecklistFragmentInteractionListener mListener;
    private ListView mChecklist;
    private EditText mChecklistItem;
    private ArrayList<String> tempChecklist;
    private CustomChecklistAdapter adapter;
    private EditText mNoteTitle;

    private RelativeLayout mChecklistContainer;
    private TextView mNoteExtras, mNoteExtrasReminder;

    private int noteID = -1;
    private int isStarred = 0;

    private ImageView mNoteStarred;
    private boolean isNoteEditedByUser = false;
    private String oldNoteTitle, editedNoteTitle;
    private int oldListCount, editedListCount;

    public ChecklistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checklist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mChecklist = (ListView) getActivity().findViewById(R.id.check_listview);
        mChecklistItem = (EditText) getActivity().findViewById(R.id.edt_list_item);
        mChecklistItem.setImeActionLabel("Add", KeyEvent.KEYCODE_ENTER);
        mChecklistItem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (event == null) {
                    // SINGLE LINE
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // Capture soft enters in a singleLine EditText that is the last EditText.
                        addChecklistItem();
                    } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        // Capture soft enters in other singleLine EditTexts
                        addChecklistItem();
                        Log.e("Checklist Fragment", "Pressed next");
                    } else {
                        Log.e("Checklist Fragment", "Pressed something else");
                        return false;
                    }  // Let system handle all other null KeyEvents
                } else if (actionId == EditorInfo.IME_NULL) {
                    // MULTI LINE
                    // Capture most soft enters in multi-line EditTexts and all hard enters.
                    // They supply a zero actionId and a valid KeyEvent rather than
                    // a non-zero actionId and a null event like the previous cases.
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        // We capture the event when key is first pressed.
                        Log.e("Checklist Fragment", "Pressed something else");
                    } else {
                        Log.e("Checklist Fragment", "Pressed something else");
                        return true;
                    }   // We consume the event when the key is released.
                } else {
                    Log.e("Checklist Fragment", "Pressed something else");
                    return false;
                }
                // We let the system handle it when the listener
                // is triggered by something that wasn't an enter.

                // Code from this point on will execute whenever the user
                // presses enter in an attached view, regardless of position,
                // keyboard, or singleLine status.

                return true;   // Consume the event
            }
        });

        tempChecklist = new ArrayList<String>();
        adapter = new CustomChecklistAdapter(getActivity(), tempChecklist);
        mChecklist.setAdapter(adapter);
        mChecklistContainer = (RelativeLayout) getActivity().findViewById(R.id.checklist_container);
        mNoteTitle = (EditText) getActivity().findViewById(R.id.checklist_title);

        mNoteExtras = (TextView) getActivity().findViewById(R.id.checklist_extras);
        mNoteExtrasReminder = (TextView) getActivity().findViewById(R.id.checklist_extras_reminder);

        mNoteStarred = (ImageView) getActivity().findViewById(R.id.note_star);
        mNoteStarred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStar();
            }
        });

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault());
        String dateTime = simpleDateFormat.format(new Date());
        mNoteExtras.setText(dateTime);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.onChecklistFragmentInteraction();
        }
    }

    public void addChecklistItem() {
        // only when text box is not empty
        if (!mChecklistItem.getText().toString().trim().equals("")) {
            tempChecklist.add(mChecklistItem.getText().toString());
            adapter.updateNoteAdapter(tempChecklist);
            mChecklistItem.setText("");
            mChecklistItem.requestFocus();
        }
    }

    public void changeNoteColor() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoteColorDialogFragment();
        dialog.show(getFragmentManager(), "NoteColorDialogFragment");

        isNoteEditedByUser = true;
    }

    public void changeNoteBackgroundColor(String backgroundColor) {

        if (mChecklistContainer != null) {
            if (backgroundColor.equals(NoteColor.YELLOW.toString())) {
                mChecklistContainer.setBackgroundColor(getResources().getColor(R.color.note_yellow));
            } else if (backgroundColor.equals(NoteColor.BLUE.toString())) {
                mChecklistContainer.setBackgroundColor(getResources().getColor(R.color.note_blue));
            } else if (backgroundColor.equals(NoteColor.GREEN.toString())) {
                mChecklistContainer.setBackgroundColor(getResources().getColor(R.color.note_green));
            } else if (backgroundColor.equals(NoteColor.WHITE.toString())) {
                mChecklistContainer.setBackgroundColor(getResources().getColor(R.color.note_white));
            } else {
                mChecklistContainer.setBackgroundColor(getResources().getColor(R.color.note_red));
            }
        }
    }

    public int saveNoteToDatabase(String noteColor) {
        // DBHelper dbHelper = new DBHelper(getActivity());

        String title = mNoteTitle.getText().toString();

        Gson gson = new Gson();
        String content = gson.toJson(tempChecklist);

        if (title.equals("") && content.equals("")) {
            Toast.makeText(getActivity(), R.string.note_empty, Toast.LENGTH_SHORT).show();
            // close the activity
            getActivity().finish();

            return 0;
        }
        if (title.equals("")) {
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
        values.put(NoteContentProvider.COLUMN_NOTES_TYPE, String.valueOf(NoteType.TODO));

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

    public void shareNoteUsingIntent() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : tempChecklist) {
            stringBuilder.append("-" + s + "\n");
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mNoteTitle.getText().toString() + " \n " + stringBuilder);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.send_to)));
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

            // fill the title
            mNoteTitle.setText(note.getNoteTitle());

            Gson gson = new Gson();

            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> checklistItemsArray = gson.fromJson(note.getNoteContent(), type);
            // clear the list before adding new data
            // issue occurs when returning after sharing the checklist
            tempChecklist.clear();

            tempChecklist.addAll(checklistItemsArray);
            oldListCount = tempChecklist.size();
            adapter.updateNoteAdapter(tempChecklist);

            if (!note.getNoteLastModifiedTimeStamp().equals("")) {
                mNoteExtras.setText("Created: " + Globals.getInstance().convertToReadableDate(note.getNoteCreatedTimeStamp()) + "    Edited: " + Globals.getInstance().convertToReadableDate(note.getNoteLastModifiedTimeStamp()));
            } else {
                mNoteExtras.setText("Created: " + Globals.getInstance().convertToReadableDate(note.getNoteCreatedTimeStamp()));
            }
            if (note.getIsReminderSet() == 1) {
                mNoteExtrasReminder.setText(getResources().getString(R.string.reminder_set) + ": " + note.getReminderDateTime() + "     " + note.getReminderType());
            } else {
                mNoteExtrasReminder.setText(R.string.no_reminder);
            }
            // update the star
            isStarred = note.getIsStarred();
            updateStar();
            changeNoteBackgroundColor(note.getNoteColor());
        }

    }

    public void updateNote(String noteColor, int noteID) {
        //DBHelper dbHelper = new DBHelper(getActivity());

        editedNoteTitle = mNoteTitle.getText().toString();

        if(!oldNoteTitle.equals(editedNoteTitle)){
            isNoteEditedByUser = true;
        }
        editedListCount = tempChecklist.size();
        if(oldListCount != editedListCount){
            isNoteEditedByUser = true;
        }

        if(isNoteEditedByUser) {
            String title = mNoteTitle.getText().toString();

            Gson gson = new Gson();
            String content = gson.toJson(tempChecklist);

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnChecklistFragmentInteractionListener) activity;
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
    public interface OnChecklistFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onChecklistFragmentInteraction();
    }

    public void addStar() {
        isNoteEditedByUser = true;
        if (isStarred == 0) {
            isStarred = 1;
            Toast.makeText(getActivity(), "Starred", Toast.LENGTH_SHORT).show();
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.star_white));
        } else {
            isStarred = 0;
            Toast.makeText(getActivity(), "Star removed", Toast.LENGTH_SHORT).show();
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.star_white_border));
        }
    }

    public void updateStar() {
        if (isStarred == 0) {
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.star_white_border));
        } else {
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
