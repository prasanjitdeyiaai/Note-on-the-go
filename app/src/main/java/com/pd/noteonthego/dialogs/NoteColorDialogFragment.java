package com.pd.noteonthego.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.pd.noteonthego.R;

/**
 * Created by pradey on 8/11/2015.
 */
public class NoteColorDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int selectedColor);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    private int selectedNoteColor = -1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.note_color_dialog_title)
                .setItems(R.array.note_colors, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        selectedNoteColor = which;
                        if(selectedNoteColor == -1){
                            // please select a color
                            // or do nothing keep it default
                        }else{
                            // color selected, please proceed
                            // Send the positive button event back to the host activity
                            mListener.onDialogPositiveClick(NoteColorDialogFragment.this, selectedNoteColor);
                        }
                    }
                })
                .setNegativeButton(R.string.note_cancel_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        selectedNoteColor = -1;
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(NoteColorDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
