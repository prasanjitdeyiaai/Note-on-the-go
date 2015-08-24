package com.pd.noteonthego.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pd.noteonthego.R;

import java.util.ArrayList;

/**
 * Created by Prasanjit on 8/22/2015.
 */
public class CustomChecklistAdapter extends BaseAdapter {
    Context context = null;
    private LayoutInflater mInflater;
    private ArrayList<String> items;

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

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.custom_checklist_row, null);
            holder.chkItem = (TextView) convertView.findViewById(R.id.chk_item);
            holder.clearItem = (ImageView)convertView.findViewById(R.id.clear_item);

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

        return convertView;
    }

    public static class ViewHolder {

        public TextView chkItem;
        public ImageView clearItem;
    }

    public void updateNoteAdapter(ArrayList<String> noteArrayList){
        this.items = noteArrayList;
        notifyDataSetChanged();
    }
}