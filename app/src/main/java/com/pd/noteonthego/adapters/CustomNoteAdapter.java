package com.pd.noteonthego.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pd.noteonthego.R;
import com.pd.noteonthego.helper.Globals;
import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.helper.NoteType;
import com.pd.noteonthego.models.Note;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by pradey on 8/10/2015.
 */
public class CustomNoteAdapter extends BaseAdapter implements Filterable {

    Context context = null;
    ArrayList<Note> notes, filteredNotes;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public CustomNoteAdapter() {

    }

    public CustomNoteAdapter(Context context, ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;
        this.filteredNotes = notes;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return filteredNotes.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredNotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
            holder.noteCreatedDate = (TextView) convertView.findViewById(R.id.list_note_created_date);
            holder.noteReminder = (TextView) convertView.findViewById(R.id.list_note_reminder);
            holder.noteStarred = (ImageView) convertView.findViewById(R.id.list_note_starred);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = filteredNotes.get(position);
        holder.noteTitle.setText(note.getNoteTitle());

        if (note.getNoteType().equals(NoteType.TODO.toString())) {
            // it's a check list
            Gson gson = new Gson();

            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> checklistItemsArray = gson.fromJson(note.getNoteContent(), type);

            StringBuilder stringBuilder = new StringBuilder();
            for (String s : checklistItemsArray) {
                stringBuilder.append("-" + s + "\n");
            }
            holder.noteTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
            holder.noteContent.setText(stringBuilder);
        } else {
            // it's a note
            holder.noteTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.noteContent.setText(note.getNoteContent());
        }

        // show last edit date time if edited
        if (note.getNoteLastModifiedTimeStamp().equals("")) {
            holder.noteCreatedDate.setText(Globals.getInstance().convertToReadableDateShort(note.getNoteCreatedTimeStamp()));
        } else {
            holder.noteCreatedDate.setText(Globals.getInstance().convertToReadableDateShort(note.getNoteLastModifiedTimeStamp()));
        }

        String color = note.getNoteColor();
        if (color.equals(String.valueOf(NoteColor.YELLOW))) {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.note_yellow));
            //holder.container.setBackground(context.getResources().getDrawable(R.drawable.shadow_yellow));
        } else if (color.equals(String.valueOf(NoteColor.BLUE))) {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.note_blue));
            //holder.container.setBackground(context.getResources().getDrawable(R.drawable.shadow_blue));
        } else if (color.equals(String.valueOf(NoteColor.GREEN))) {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.note_green));
            //holder.container.setBackground(context.getResources().getDrawable(R.drawable.shadow_green));
        } else if (color.equals(String.valueOf(NoteColor.WHITE))) {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.note_white));
            //holder.container.setBackground(context.getResources().getDrawable(R.drawable.shadow_white));
            // holder.noteCreatedDate.setTextColor(context.getResources().getColor(R.color.note_text_color_dark));
            // holder.noteReminder.setTextColor(context.getResources().getColor(R.color.note_text_color_dark));
        } else {
            //holder.container.setBackground(context.getResources().getDrawable(R.drawable.custom_note_list_background_selector));
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.note_red));
            //holder.container.setBackground(context.getResources().getDrawable(R.drawable.shadow_red));
        }

        if (note.getIsReminderSet() == 1) {
            holder.noteReminder.setVisibility(View.VISIBLE);
            holder.noteReminder.setText("Reminds " + note.getReminderType().toLowerCase() + " " + Globals.getInstance().convertToReadableDateShort(note.getReminderDateTime()));
        } else {
            holder.noteReminder.setVisibility(View.GONE);
        }

        // ADD STAR FOR NOTE
        if (note.getIsStarred() == 1) {
            holder.noteStarred.setVisibility(View.VISIBLE);
        } else {
            holder.noteStarred.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public static class ViewHolder {

        public TextView noteTitle;
        public TextView noteContent;
        public TextView noteCreatedDate;
        public RelativeLayout container;
        public TextView noteReminder;
        public ImageView noteStarred;
    }

    public void updateNoteAdapter(ArrayList<Note> noteArrayList) {
        this.notes = noteArrayList;
        notifyDataSetChanged();
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<Note> list = notes;

            int count = list.size();

            final ArrayList<Note> nlist = new ArrayList<Note>(count);

            String filterableTitle, filterableContent;

            for (int i = 0; i < count; i++) {
                Note note = list.get(i);
                filterableTitle = note.getNoteTitle();
                filterableContent = note.getNoteContent();
                if (filterableContent.toLowerCase().contains(filterString) || filterableTitle.toLowerCase().contains(filterString)) {
                    nlist.add(note);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredNotes = (ArrayList<Note>) results.values;
            notifyDataSetChanged();
        }

    }
}
