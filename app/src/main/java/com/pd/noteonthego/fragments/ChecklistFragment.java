package com.pd.noteonthego.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.pd.noteonthego.R;
import com.pd.noteonthego.adapters.CustomChecklistAdapter;

import java.util.ArrayList;

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
    private Button mBtnAddItem;

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
    }

    public void addChecklistItem(){
        tempChecklist.add(mChecklistItem.getText().toString());
        adapter.updateNoteAdapter(tempChecklist);
        mChecklistItem.setText("");
        mChecklistItem.requestFocus();
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
