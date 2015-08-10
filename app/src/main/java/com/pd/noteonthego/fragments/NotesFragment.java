package com.pd.noteonthego.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.pd.noteonthego.R;
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
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        public void onFragmentInteraction(Uri uri);
    }

    public void saveNoteToDatabase(){
        String title = mNoteTitle.getText().toString();
        String content = mNoteContent.getText().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String dateTime = simpleDateFormat.format(new Date());

        Note note = new Note(title, content, dateTime, NoteColor.YELLOW, NoteType.BLANK, "", "", "");
        DBHelper dbHelper = new DBHelper(getActivity());
        long rowsAdded = dbHelper.addNote(note);

        Toast.makeText(getActivity(), "rows added " + rowsAdded, Toast.LENGTH_SHORT).show();
    }

}
