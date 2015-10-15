package com.pd.noteonthego.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
            holder.noteReminder = (TextView) convertView.findViewById(R.id.list_note_reminder);
            holder.noteStarred = (ImageView) convertView.findViewById(R.id.list_note_starred);
            holder.bigDate = (TextView) convertView.findViewById(R.id.big_date);
            holder.separator = (View)convertView.findViewById(R.id.separator);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = filteredNotes.get(position);
        if(note.getNoteTitle().length() > 20) {
            holder.noteTitle.setText(note.getNoteTitle().substring(0, 21));
        }else{
            holder.noteTitle.setText(note.getNoteTitle());
        }

        if (note.getNoteType().equals(NoteType.TODO.toString())) {
            // it's a check list
            Gson gson = new Gson();

            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> checklistItemsArray = gson.fromJson(note.getNoteContent(), type);
            ArrayList<String> checkedPositions = gson.fromJson(note.getNoteTodoCheckedPositions(), type);

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < checklistItemsArray.size(); i++) {
                String s = checklistItemsArray.get(i);
                if(checkedPositions != null) {
                    if (checkedPositions.contains("" + i)) {
                        if (s.length() > 20) {
                            stringBuilder.append("- " + s.substring(0, 21) + "... ");
                        } else {
                            stringBuilder.append("- " + s);
                        }
                        if (i != checklistItemsArray.size() - 1) {
                            stringBuilder.append("\n");
                        }
                    } else {
                        if (s.length() > 20) {
                            stringBuilder.append("\u2022 " + s.substring(0, 21) + "... ");
                        } else {
                            stringBuilder.append("\u2022 " + s);
                        }
                        if (i != checklistItemsArray.size() - 1) {
                            stringBuilder.append("\n");
                        }
                    }
                }else {
                    if (s.length() > 20) {
                        stringBuilder.append("\u2022 " + s.substring(0, 21) + "... ");
                    } else {
                        stringBuilder.append("\u2022 " + s);
                    }
                    if (i != checklistItemsArray.size() - 1) {
                        stringBuilder.append("\n");
                    }
                }
            }
            // holder.noteTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
            holder.noteContent.setText(stringBuilder);
        } else {
            // it's a note
            // holder.noteTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.noteContent.setText(note.getNoteContent());
        }

        // show last edit date time if edited
        /*if (note.getNoteLastModifiedTimeStamp().equals("")) {
            holder.bigDate.setText(Globals.getInstance().convertToReadableDateExtraShort(note.getNoteCreatedTimeStamp()));
        } else {
            holder.bigDate.setText(Globals.getInstance().convertToReadableDateExtraShort(note.getNoteLastModifiedTimeStamp()));
        }*/
        holder.bigDate.setText(Globals.getInstance().convertToReadableDateExtraShort(note.getNoteLastModifiedTimeStamp()));

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
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.note_red));
        }

        if (note.getIsReminderSet() == 1) {
            holder.noteReminder.setVisibility(View.VISIBLE);
            holder.separator.setVisibility(View.VISIBLE);
            if(Globals.getInstance().getDateDifference(note.getReminderDateTime()).equals("0")){
                // TODAY
                if(note.getReminderType().toLowerCase().equals("once")){
                    holder.noteReminder.setText(note.getReminderType().toLowerCase() + " today " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                }else if(note.getReminderType().toLowerCase().equals("daily")){
                    holder.noteReminder.setText(note.getReminderType().toLowerCase() + " " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                }else if(note.getReminderType().toLowerCase().equals("weekly")){
                    holder.noteReminder.setText(note.getReminderType().toLowerCase() + " on wednesdays " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                }else {
                    holder.noteReminder.setText(note.getReminderType().toLowerCase() + " on " + Globals.getInstance().convertToReadableDateOnly(note.getReminderDateTime()) + " " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                }
            }else if(Globals.getInstance().getDateDifference(note.getReminderDateTime()).equals("1")){
                // TOMORROW
                if(note.getReminderType().toLowerCase().equals("once")){
                    holder.noteReminder.setText(note.getReminderType().toLowerCase() + " tomorrow " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                }else if(note.getReminderType().toLowerCase().equals("daily")){
                    holder.noteReminder.setText(note.getReminderType().toLowerCase() + " " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                }else if(note.getReminderType().toLowerCase().equals("weekly")){
                    holder.noteReminder.setText(note.getReminderType().toLowerCase() + " on wednesdays " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                }else {
                    holder.noteReminder.setText(note.getReminderType().toLowerCase() + " on " + Globals.getInstance().convertToReadableDateOnly(note.getReminderDateTime()) + " " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                }
            }else if(Globals.getInstance().getDateDifference(note.getReminderDateTime()).equals("-1")){
                // YESTERDAY
                if(note.getReminderType().toLowerCase().equals("once")){
                    // do nothing here as it is completed
                }else if(note.getReminderType().toLowerCase().equals("daily")){
                    holder.noteReminder.setText(note.getReminderType().toLowerCase() + " " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                }else if(note.getReminderType().toLowerCase().equals("weekly")){
                    holder.noteReminder.setText(note.getReminderType().toLowerCase() + " on wednesdays " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                }else {
                    holder.noteReminder.setText(note.getReminderType().toLowerCase() + " on " + Globals.getInstance().convertToReadableDateOnly(note.getReminderDateTime()) + " " + Globals.getInstance().convertToReadableDateForTime(note.getReminderDateTime()));
                }
            }else {
                if(note.getReminderType().toLowerCase().equals("once")){
                    holder.noteReminder.setText(note.getReminderType().toLowerCase() + " on " + Globals.getInstance().convertToReadableDateShort(note.getReminderDateTime()));
                }else {
                    holder.noteReminder.setText(note.getReminderType().toLowerCase() + " from " + Globals.getInstance().convertToReadableDateShort(note.getReminderDateTime()));
                }
            }

        } else {
            holder.noteReminder.setVisibility(View.GONE);
            holder.separator.setVisibility(View.GONE);
        }

        // ADD STAR FOR NOTE
        if (note.getIsStarred() == 1) {
            holder.noteStarred.setVisibility(View.VISIBLE);
        } else {
            holder.noteStarred.setVisibility(View.GONE);
        }

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.custom_fadein_animation);
        animation.setDuration(500);
        convertView.startAnimation(animation);
        animation = null;

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public static class ViewHolder {

        public TextView noteTitle;
        public TextView noteContent;
        public TextView bigDate;
        public RelativeLayout container;
        public TextView noteReminder;
        public ImageView noteStarred;
        public View separator;
    }

    public void updateNoteAdapter(ArrayList<Note> noteArrayList) {
        this.filteredNotes = noteArrayList;
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
