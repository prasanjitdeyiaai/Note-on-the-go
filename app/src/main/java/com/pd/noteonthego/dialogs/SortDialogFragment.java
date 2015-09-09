package com.pd.noteonthego.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.pd.noteonthego.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SortDialogFragment.SortDialogListener} interface
 * to handle interaction events.
 */
public class SortDialogFragment extends DialogFragment {

    private SortDialogListener mListener;
    private int selectedSortIndex = -1;

    public SortDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.note_sort_dialog_title)
                .setItems(R.array.note_sort_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        selectedSortIndex = which;
                        if(selectedSortIndex == -1){
                            // please select a color
                            // or do nothing keep it default
                        }else{
                            // color selected, please proceed
                            // Send the positive button event back to the host activity
                            mListener.onDialogPositiveClick(SortDialogFragment.this, selectedSortIndex);
                        }
                    }
                })
                .setNegativeButton(R.string.note_cancel_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        selectedSortIndex = -1;
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(SortDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SortDialogListener) activity;
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

    /* The activity that creates an instance of this dialog fragment must
    * implement this interface in order to receive event callbacks.
    * Each method passes the DialogFragment in case the host needs to query it. */
    public interface SortDialogListener {
        public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog, int selectedSortIndex);
        public void onDialogNegativeClick(android.support.v4.app.DialogFragment dialog);
    }

}
