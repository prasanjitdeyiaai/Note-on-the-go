package com.pd.noteonthego.fragments;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobeta.android.dslv.DragSortListView;
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
import java.util.Collections;
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
    private DragSortListView mChecklist;
    private EditText mChecklistItem;
    private ArrayList<String> tempChecklist, selectedItems;
    private CustomChecklistAdapter adapter;
    private EditText mNoteTitle;

    private RelativeLayout mChecklistContainer;
    private TextView mNoteExtras, mNoteExtrasReminder;

    private int noteID = -1;
    private int isStarred = 0;

    private ImageView mNoteStarred;
    private boolean isNoteEditedByUser = false;
    private String oldNoteTitle, editedNoteTitle;
    private int oldListCount, editedListCount, oldSelectedItemsCount, editedSelectedItemsCount;
    private ArrayList<String> oldTempChecklist;

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

        mChecklist = (DragSortListView) getActivity().findViewById(R.id.check_listview);
        mChecklistItem = (EditText) getActivity().findViewById(R.id.edt_list_item);
        mChecklistItem.requestFocus();

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
                    } else {
                        // everything else should work here
                        addChecklistItem();
                        return false;
                    }  // Let system handle all other null KeyEvents
                } else if (actionId == EditorInfo.IME_NULL) {
                    // MULTI LINE
                    // Capture most soft enters in multi-line EditTexts and all hard enters.
                    // They supply a zero actionId and a valid KeyEvent rather than
                    // a non-zero actionId and a null event like the previous cases.
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        // We capture the event when key is first pressed.
                    } else {
                        return true;
                    }   // We consume the event when the key is released.
                } else {
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
        oldTempChecklist = new ArrayList<String>();
        selectedItems = new ArrayList<String>();
        adapter = new CustomChecklistAdapter(getActivity(), tempChecklist);
        mChecklist.setAdapter(adapter);
        mChecklist.setDropListener(onDrop);

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
        mNoteExtras.setText(Globals.getInstance().convertToReadableDateShort(dateTime));
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

            // scroll to the end of the list
            mChecklist.post(new Runnable() {
                @Override
                public void run() {
                    mChecklist.smoothScrollToPosition(adapter.getCount() - 1);
                }
            });

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

        SparseBooleanArray checked = mChecklist.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i)) {
                selectedItems.add(String.valueOf(position));
            }
        }

        String title = mNoteTitle.getText().toString();

        Gson gson = new Gson();
        String content = gson.toJson(tempChecklist);
        String checkedContent = "";
        if(selectedItems.size() > 0) {
            checkedContent = gson.toJson(selectedItems);
        }else {
            checkedContent = "";
        }

        if (title.equals("") && tempChecklist.size() < 1) {
            Toast.makeText(getActivity(), R.string.note_empty, Toast.LENGTH_SHORT).show();
            // close the activity
            getActivity().finish();

            return 0;
        }
        if (title.equals("")) {
            title = tempChecklist.get(0);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault());
        String dateTime = simpleDateFormat.format(new Date());

        // Add a new note
        ContentValues values = new ContentValues();

        values.put(NoteContentProvider.COLUMN_NOTES_TITLE, title);
        values.put(NoteContentProvider.COLUMN_NOTES_CONTENT, content);
        values.put(NoteContentProvider.COLUMN_NOTES_TODO_CHECKED_POSITIONS, checkedContent);
        values.put(NoteContentProvider.COLUMN_NOTES_CREATED_TIMESTAMP, dateTime);

        values.put(NoteContentProvider.COLUMN_NOTES_lAST_MODIFIED_TIMESTAMP, dateTime);
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
            // note saved successfully
            this.noteID = (int) ContentUris.parseId(uri);
        }

        // close the activity
        getActivity().finish();
        return 1;
    }

    public void shareNoteUsingIntent() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : tempChecklist) {
            stringBuilder.append("\u2022 " + s + "\n");
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mNoteTitle.getText().toString() + " \n" + stringBuilder);
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
            ArrayList<String> checkedItemsArray = gson.fromJson(note.getNoteTodoCheckedPositions(), type);
            ArrayList<String> checklistItemsArray = gson.fromJson(note.getNoteContent(), type);
            // clear the list before adding new data
            // issue occurs when returning after sharing the checklist
            tempChecklist.clear();

            tempChecklist.addAll(checklistItemsArray);

            oldTempChecklist.addAll(tempChecklist);

            oldListCount = tempChecklist.size();
            adapter.updateNoteAdapter(tempChecklist);

            if(checkedItemsArray != null) {
                oldSelectedItemsCount = checkedItemsArray.size();
                for (String s : checkedItemsArray) {
                    mChecklist.setItemChecked(Integer.parseInt(s), true);
                }
            }

            if (!note.getNoteLastModifiedTimeStamp().equals("")) {
                mNoteExtras.setText(Globals.getInstance().convertToReadableDateShort(note.getNoteLastModifiedTimeStamp()));
            } else {
                mNoteExtras.setText(Globals.getInstance().convertToReadableDateShort(note.getNoteCreatedTimeStamp()));
            }
            if (note.getIsReminderSet() == 1) {
                if(Globals.getInstance().getDateDifference(note.getReminderDateTime()).equals("0")){
                    if(note.getReminderType().toLowerCase().equals("once")){
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " today " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }else {
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " from today " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }
                }
                else if(Globals.getInstance().getDateDifference(note.getReminderDateTime()).equals("1")){
                    if(note.getReminderType().toLowerCase().equals("once")){
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " tomorrow " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }else {
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " from tomorrow " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                    }
                }
                else {
                    if(note.getReminderType().toLowerCase().equals("once")){
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " on " + Globals.getInstance().convertToReadableDateShort(note.getReminderDateTime()));
                    }else {
                        mNoteExtrasReminder.setText(note.getReminderType().toLowerCase() + " from " + Globals.getInstance().convertToReadableDateShort(note.getReminderDateTime()));
                    }
                }
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

        SparseBooleanArray checked = mChecklist.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i)) {
                selectedItems.add(String.valueOf(position));
            }
        }

        editedSelectedItemsCount = selectedItems.size();

        editedNoteTitle = mNoteTitle.getText().toString();

        if(!oldNoteTitle.equals(editedNoteTitle)){
            isNoteEditedByUser = true;
        }
        editedListCount = tempChecklist.size();
        // if size is different update
        if(oldListCount != editedListCount){
            isNoteEditedByUser = true;
        }
        // if list items are different update
        if(!areListsEqual(oldTempChecklist, tempChecklist)){
            isNoteEditedByUser = true;
        }

        if(oldSelectedItemsCount != editedSelectedItemsCount){
            isNoteEditedByUser = true;
        }

        if(isNoteEditedByUser) {
            String title = mNoteTitle.getText().toString();

            Gson gson = new Gson();
            String content = gson.toJson(tempChecklist);
            String checkedContent = "";
            if(selectedItems.size() > 0) {
                checkedContent = gson.toJson(selectedItems);
            }else {
                checkedContent = "";
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault());
            String dateTime = simpleDateFormat.format(new Date());

            // Update note
            ContentValues values = new ContentValues();

            String whereClause = NoteContentProvider.COLUMN_NOTES_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(noteID)};

            values.put(NoteContentProvider.COLUMN_NOTES_TITLE, title);
            values.put(NoteContentProvider.COLUMN_NOTES_CONTENT, content);
            values.put(NoteContentProvider.COLUMN_NOTES_TODO_CHECKED_POSITIONS, checkedContent);

            values.put(NoteContentProvider.COLUMN_NOTES_lAST_MODIFIED_TIMESTAMP, dateTime);
            values.put(NoteContentProvider.COLUMN_NOTES_COLOR, noteColor);
            values.put(NoteContentProvider.COLUMN_NOTES_STARRED, isStarred);

            long rowsUpdated = getActivity().getContentResolver().update(
                    NoteContentProvider.CONTENT_URI, values, whereClause, whereArgs);

            if (rowsUpdated > 0) {
                // Toast.makeText(getActivity(), R.string.note_updated, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), R.string.star_note, Toast.LENGTH_SHORT).show();
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_toggle_star));
        } else {
            isStarred = 0;
            Toast.makeText(getActivity(), R.string.unstar_note, Toast.LENGTH_SHORT).show();
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_toggle_star_outline));
        }
    }

    public void updateStar() {
        if (isStarred == 0) {
            mNoteStarred.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_toggle_star_outline));
        } else {
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

    /**
     * check if the two arraylists are equal
     * @param oldList
     * @param newList
     * @return
     */
    public  boolean areListsEqual(ArrayList<String> oldList, ArrayList<String> newList){
        if (oldList == null && newList == null){
            return true;
        }

        if((oldList == null && newList != null)
                || oldList != null && newList == null
                || oldList.size() != newList.size()){
            return false;
        }

        ArrayList<String> oldListTemp, newListTemp;
        //to avoid messing the order of the lists we will use a copy
        oldListTemp = new ArrayList<String>(oldList);
        newListTemp = new ArrayList<String>(newList);

        Collections.sort(oldListTemp);
        Collections.sort(newListTemp);
        return oldListTemp.equals(newListTemp);
    }

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        String item = (String) adapter.getItem(from);
                        tempChecklist.remove(item);
                        tempChecklist.add(to, item);
                        adapter.updateNoteAdapter(tempChecklist);
                        mChecklist.moveCheckState(from, to);

                        isNoteEditedByUser = true;
                    }
                }
            };
}
