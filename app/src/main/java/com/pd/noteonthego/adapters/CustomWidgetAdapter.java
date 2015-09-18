package com.pd.noteonthego.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pd.noteonthego.R;
import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.models.Note;

import java.util.ArrayList;

/**
 * Created by Prasanjit on 9/18/2015.
 */
public class CustomWidgetAdapter extends BaseAdapter {

    Context context = null;
    ArrayList<Note> notes;
    private LayoutInflater mInflater;

    public CustomWidgetAdapter() {

    }

    public CustomWidgetAdapter(Context context, ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int i) {
        return notes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.custom_widget_selector_list_row, null);
            holder.notecContainer = (RelativeLayout) convertView.findViewById(R.id.widget_configure_container);
            holder.noteTitle = (TextView) convertView.findViewById(R.id.widget_configure_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = notes.get(i);
        holder.noteTitle.setText(note.getNoteTitle());

        String color = note.getNoteColor();
        if (color.equals(String.valueOf(NoteColor.YELLOW))) {
            holder.notecContainer.setBackgroundColor(context.getResources().getColor(R.color.note_yellow));
        } else if (color.equals(String.valueOf(NoteColor.BLUE))) {
            holder.notecContainer.setBackgroundColor(context.getResources().getColor(R.color.note_blue));
        } else if (color.equals(String.valueOf(NoteColor.GREEN))) {
            holder.notecContainer.setBackgroundColor(context.getResources().getColor(R.color.note_green));
        } else if (color.equals(String.valueOf(NoteColor.WHITE))) {
            holder.notecContainer.setBackgroundColor(context.getResources().getColor(R.color.note_white));
        } else {
            holder.notecContainer.setBackgroundColor(context.getResources().getColor(R.color.note_red));
        }

        return convertView;
    }

    public static class ViewHolder {

        public TextView noteTitle;
        public RelativeLayout notecContainer;
    }
}
