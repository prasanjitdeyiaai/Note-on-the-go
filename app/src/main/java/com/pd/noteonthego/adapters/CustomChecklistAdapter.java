package com.pd.noteonthego.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.pd.noteonthego.R;

import java.util.ArrayList;

/**
 * Created by Prasanjit on 8/22/2015.
 */
public class CustomChecklistAdapter extends BaseAdapter {
    Context context = null;
    private LayoutInflater mInflater;
    private ArrayList<String> items;
    private ArrayList<Integer> selectedItems;

    public CustomChecklistAdapter(Context context, ArrayList<String> items) {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder = null;
        selectedItems = new ArrayList<Integer>();

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.custom_checklist_row, null);
            holder.chkItem = (CheckBox) convertView.findViewById(R.id.chk_item);
            holder.clearItem = (ImageView)convertView.findViewById(R.id.clear_item);
            holder.editItem = (ImageView)convertView.findViewById(R.id.edit_item);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.chkItem.setText(items.get(position));
        holder.clearItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.remove(position);
                updateNoteAdapter(items);
            }
        });

        holder.editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForEditingListItem(position);
            }
        });

        holder.chkItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedItems.add(position);
            }
        });
        return convertView;
    }

    public static class ViewHolder {

        public CheckBox chkItem;
        public ImageView clearItem, editItem;
    }

    public void updateNoteAdapter(ArrayList<String> noteArrayList){
        this.items = noteArrayList;
        notifyDataSetChanged();
    }

    /**
     * editing list item
     * @param position
     */
    private void openForEditingListItem(final int position) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText input = new EditText(context);
        input.setTextSize(14);
        input.setText(items.get(position));
        input.setSelection(items.get(position).length());
        input.setHint(R.string.edit_item);
        input.setBackgroundColor(Color.TRANSPARENT);
        // alert.setMessage(R.string.edit_item);
        alert.setTitle(R.string.edit_item);
        alert.setView(input);
        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String editedListItem = input.getText().toString().trim();
                if (!editedListItem.equals("")) {
                    items.remove(position);
                    items.add(position, editedListItem);
                    updateNoteAdapter(items);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    public ArrayList<Integer> getSelectedItems(){
        return selectedItems;
    };
}