package com.pd.noteonthego.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pd.noteonthego.R;
import com.pd.noteonthego.adapters.CustomChecklistAdapter;
import com.pd.noteonthego.dialogs.NoteColorDialogFragment;
import com.pd.noteonthego.helper.DBHelper;
import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.helper.NoteContentProvider;
import com.pd.noteonthego.helper.NoteType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChecklistFragment.OnChecklistFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ChecklistFragment extends Fragment{

    private OnChecklistFragmentInteractionListener mListener;
    private ListView mChecklist;
    private EditText mChecklistItem;
    private ArrayList<String> tempChecklist;
    private CustomChecklistAdapter adapter;
    private Button mBtnAddItem;
    private EditText mNoteTitle;

    private RelativeLayout mChecklistContainer;

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

        mChecklist = (ListView)getActivity().findViewById(R.id.check_listview);
        mChecklistItem = (EditText)getActivity().findViewById(R.id.edt_list_item);
        tempChecklist = new ArrayList<String>();
        adapter = new CustomChecklistAdapter(getActivity(), tempChecklist);
        mChecklist.setAdapter(adapter);
        mBtnAddItem = (Button)getActivity().findViewById(R.id.btn_add_item);
        mBtnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChecklistItem();
            }
        });

        mChecklistContainer = (RelativeLayout) getActivity().findViewById(R.id.checklist_container);
        mNoteTitle = (EditText) getActivity().findViewById(R.id.checklist_title);
    }

    public void addChecklistItem(){
        tempChecklist.add(mChecklistItem.getText().toString());
        adapter.updateNoteAdapter(tempChecklist);
        mChecklistItem.setText("");
        mChecklistItem.requestFocus();
    }

    public void changeNoteColor() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoteColorDialogFragment();
        dialog.show(getFragmentManager(), "NoteColorDialogFragment");
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
        DBHelper dbHelper = new DBHelper(getActivity());

        String title = mNoteTitle.getText().toString();

        Gson gson = new Gson();
        String content = gson.toJson(tempChecklist);

        if(title.equals("") && content.equals("")){
            Toast.makeText(getActivity(), R.string.note_empty, Toast.LENGTH_SHORT).show();
            // close the activity
            getActivity().finish();

            return 0;
        }
        if(title.equals("")) {
            title = content;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
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

        Uri uri = getActivity().getContentResolver().insert(
                NoteContentProvider.CONTENT_URI, values);

        if (uri != null) {
            Toast.makeText(getActivity(), R.string.note_saved, Toast.LENGTH_SHORT).show();
        }

        // close the activity
        getActivity().finish();
        return 1;
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

}
