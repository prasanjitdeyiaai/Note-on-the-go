package com.pd.noteonthego.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pd.noteonthego.R;
import com.pd.noteonthego.dialogs.NoteColorDialogFragment;
import com.pd.noteonthego.helper.DBHelper;
import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.helper.NoteType;
import com.pd.noteonthego.models.Note;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NotesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private EditText mNoteTitle, mNoteContent;
    private String TAG = "Notes Fragment";

    private RelativeLayout mNoteContainer;

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

        mNoteContainer = (RelativeLayout) getActivity().findViewById(R.id.note_container);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            // mListener.onFragmentInteraction(uri);
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

    public void saveNoteToDatabase(String noteColor) {
        DBHelper dbHelper = new DBHelper(getActivity());

        String title = mNoteTitle.getText().toString();
        String content = mNoteContent.getText().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String dateTime = simpleDateFormat.format(new Date());

        Note note = new Note(title, content, dateTime, noteColor, String.valueOf(NoteType.BLANK), "", "", "", 0, "", "");
        long rowsAdded = dbHelper.addNote(note);

        if (rowsAdded > 0) {
            Toast.makeText(getActivity(), "Note Saved", Toast.LENGTH_SHORT).show();
        }

        // close the activity
        getActivity().finish();
    }

    public void setNoteReminder() {

    }


    public void changeNoteColor() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoteColorDialogFragment();
        dialog.show(getFragmentManager(), "NoteColorDialogFragment");
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

    public void openNoteForViewing(String noteTitle, String noteTimestamp) {
        DBHelper dbHelper = new DBHelper(getActivity());
        if (noteTitle != null && noteTimestamp != null) {
            Note note = dbHelper.getNote(noteTitle, noteTimestamp);

            // fill the edit texts
            mNoteTitle.setText(noteTitle);
            mNoteContent.setText(note.getNoteContent());
            changeNoteBackgroundColor(note.getNoteColor());
        }

    }
}
