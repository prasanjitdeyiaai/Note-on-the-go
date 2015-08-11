package com.pd.noteonthego.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pd.noteonthego.models.Note;

import java.util.ArrayList;

/**
 * Created by pradey on 8/6/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "noteonthego.db";
    private static final int DATABASE_VERSION = 2;

    // notes table
    public static final String TABLE_NOTES = "notes";

    // notes table columns
    public static final String COLUMN_NOTES_ID = "notes_id";
    public static final String COLUMN_NOTES_TITLE = "notes_title";
    public static final String COLUMN_NOTES_TIMESTAMP = "notes_timestamp";
    public static final String COLUMN_NOTES_CONTENT = "notes_content";
    public static final String COLUMN_NOTES_COLOR = "notes_color";
    public static final String COLUMN_NOTES_TYPE = "notes_type";
    public static final String COLUMN_NOTES_IMAGE = "notes_image";
    public static final String COLUMN_NOTES_VIDEO = "notes_video";
    public static final String COLUMN_NOTES_AUDIO = "notes_audio";
    public static final String COLUMN_NOTES_IS_REMINDER_SET = "notes_is_reminder_set";
    public static final String COLUMN_NOTES_REMINDER_DATETIME = "notes_reminder_datetime";
    public static final String COLUMN_NOTES_REMINDER_TYPE = "notes_reminder_type";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_NOTES = "create table "
                + TABLE_NOTES + "(" + COLUMN_NOTES_ID
                + " integer primary key autoincrement, "
                + COLUMN_NOTES_TITLE
                + " text not null, "
                + COLUMN_NOTES_TIMESTAMP
                + " text not null, "
                + COLUMN_NOTES_CONTENT
                + " text not null, "
                + COLUMN_NOTES_COLOR
                + " text not null, "
                + COLUMN_NOTES_TYPE
                + " text not null, "
                + COLUMN_NOTES_IMAGE
                + " text not null, "
                + COLUMN_NOTES_VIDEO
                + " text not null, "
                + COLUMN_NOTES_AUDIO
                + " text not null, "
                + COLUMN_NOTES_IS_REMINDER_SET
                + " integer not null, "
                + COLUMN_NOTES_REMINDER_DATETIME
                + " text not null, "
                + COLUMN_NOTES_REMINDER_TYPE
                + " text not null);";

        db.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    /**
     *
     * @param note
     * @return number of rows added
     */
    public long addNote(Note note){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(COLUMN_NOTES_TITLE,note.getNoteTitle());
        initialValues.put(COLUMN_NOTES_TIMESTAMP, note.getNoteTimeStamp());
        initialValues.put(COLUMN_NOTES_CONTENT, note.getNoteContent());
        initialValues.put(COLUMN_NOTES_COLOR, note.getNoteColor());
        initialValues.put(COLUMN_NOTES_TYPE, note.getNoteType());
        initialValues.put(COLUMN_NOTES_IMAGE, note.getNoteImg());
        initialValues.put(COLUMN_NOTES_VIDEO, note.getNoteVideo());
        initialValues.put(COLUMN_NOTES_AUDIO, note.getNoteAudio());
        initialValues.put(COLUMN_NOTES_IS_REMINDER_SET, note.getIsReminderSet());
        initialValues.put(COLUMN_NOTES_REMINDER_DATETIME, note.getReminderDateTime());
        initialValues.put(COLUMN_NOTES_REMINDER_TYPE, note.getReminderType());

        return db.insert(TABLE_NOTES, null, initialValues);
    }

    /**
     *
     * @return all notes
     */
    public ArrayList<Note> getAllNotes(){
        ArrayList<Note> listOfNotes = new ArrayList<Note>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES, null);

        cursor.moveToFirst();

        while(cursor.isAfterLast() == false){

            Note note = new Note();

            note.setNoteTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_TITLE)));
            note.setNoteContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_CONTENT)));
            note.setNoteTimeStamp(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_TIMESTAMP)));
            note.setNoteColor(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_COLOR)));
            note.setNoteType(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_TYPE)));
            note.setNoteImg(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_IMAGE)));
            note.setNoteVideo(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_VIDEO)));
            note.setNoteAudio(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_AUDIO)));
            note.setIsReminderSet(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTES_IS_REMINDER_SET)));
            note.setReminderDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_REMINDER_DATETIME)));
            note.setReminderType(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_REMINDER_TYPE)));

            listOfNotes.add(note);

            cursor.moveToNext();
        }

        return listOfNotes;
    }
}
