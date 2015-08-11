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
 * Created by pradey on 8/10/2015.
 */
public class CustomNoteAdapter extends BaseAdapter {

    Context context = null;
    ArrayList<Note> notes;
    private LayoutInflater mInflater;

    public CustomNoteAdapter() {

    }

    public CustomNoteAdapter(Context context, ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.custom_notes_list_row, null);
            holder.container = (RelativeLayout) convertView.findViewById(R.id.note_container);
            holder.noteTitle = (TextView) convertView.findViewById(R.id.list_note_title);
            holder.noteContent = (TextView) convertView.findViewById(R.id.list_note_content);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = notes.get(position);
        holder.noteTitle.setText(note.getNoteTitle());
        holder.noteContent.setText(note.getNoteContent());
        String color = note.getNoteColor();
        if (color.equals(String.valueOf(NoteColor.YELLOW))) {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.note_yellow));
        } else if (color.equals(String.valueOf(NoteColor.BLUE))) {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.note_blue));
        } else if (color.equals(String.valueOf(NoteColor.GREEN))) {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.note_green));
        } else if (color.equals(String.valueOf(NoteColor.WHITE))) {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.note_white));
        } else {
            holder.container.setBackground(context.getResources().getDrawable(R.drawable.custom_note_list_background_selector));
            //holder.container.setBackgroundColor(context.getResources().getColor(R.color.note_red));
        }

        return convertView;
    }

    public static class ViewHolder {

        public TextView noteTitle;
        public TextView noteContent;
        public RelativeLayout container;
    }
}
