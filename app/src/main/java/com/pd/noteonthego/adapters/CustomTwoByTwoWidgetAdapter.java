package com.pd.noteonthego.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pd.noteonthego.R;
import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.helper.NoteType;
import com.pd.noteonthego.models.Note;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Prasanjit on 9/18/2015.
 */
public class CustomTwoByTwoWidgetAdapter extends BaseAdapter {

    Context context = null;
    ArrayList<Note> notes;
    private LayoutInflater mInflater;

    public CustomTwoByTwoWidgetAdapter() {

    }

    public CustomTwoByTwoWidgetAdapter(Context context, ArrayList<Note> notes) {
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

            convertView = mInflater.inflate(R.layout.custom_widget_selector_list_row_2x2, null);
            holder.notecContainer = (RelativeLayout) convertView.findViewById(R.id.widget_configure_container_twobytwo);
            holder.noteTitle = (TextView) convertView.findViewById(R.id.widget_configure_title_twobytwo);
            holder.noteContent = (TextView) convertView.findViewById(R.id.widget_configure_content_twobytwo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = notes.get(i);
        holder.noteTitle.setText(note.getNoteTitle());

        if (note.getNoteType().equals(NoteType.TODO.toString())) {
            // it's a check list
            Gson gson = new Gson();

            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> checklistItemsArray = gson.fromJson(note.getNoteContent(), type);
            ArrayList<String> checkedPositions = gson.fromJson(note.getNoteTodoCheckedPositions(), type);

            StringBuilder stringBuilder = new StringBuilder();
            for (int k = 0; k < checklistItemsArray.size(); k++) {
                String s = checklistItemsArray.get(k);
                if(checkedPositions != null) {
                    if (checkedPositions.contains("" + k)) {
                        if (s.length() > 20) {
                            stringBuilder.append("\u2713 " + s.substring(0, 21) + "... ");
                        } else {
                            stringBuilder.append("\u2713 " + s);
                        }
                        if (k != checklistItemsArray.size() - 1) {
                            stringBuilder.append("\n");
                        }
                    } else {
                        if (s.length() > 20) {
                            stringBuilder.append("\u25CF  " + s.substring(0, 21) + "... ");
                        } else {
                            stringBuilder.append("\u25CF  " + s);
                        }
                        if (k != checklistItemsArray.size() - 1) {
                            stringBuilder.append("\n");
                        }
                    }
                }else {
                    if (s.length() > 20) {
                        stringBuilder.append("\u25CF  " + s.substring(0, 21) + "... ");
                    } else {
                        stringBuilder.append("\u25CF  " + s);
                    }
                    if (k != checklistItemsArray.size() - 1) {
                        stringBuilder.append("\n");
                    }
                }
            }
            holder.noteContent.setText(stringBuilder);
        } else {
            // it's a note
            holder.noteContent.setText(note.getNoteContent());
        }

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

        public TextView noteTitle, noteContent;
        public RelativeLayout notecContainer;
    }
}
