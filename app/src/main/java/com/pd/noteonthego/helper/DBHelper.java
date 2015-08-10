package com.pd.noteonthego.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pd.noteonthego.models.Note;

/**
 * Created by pradey on 8/6/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "noteonthego.db";
    private static final int DATABASE_VERSION = 1;

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
                + " text not null);";

        db.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public long addNote(Note note){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(COLUMN_NOTES_TITLE,note.getNoteTitle());
        initialValues.put(COLUMN_NOTES_TIMESTAMP, note.getNoteTimeStamp());
        initialValues.put(COLUMN_NOTES_CONTENT, note.getNoteContent());
        initialValues.put(COLUMN_NOTES_COLOR, String.valueOf(note.getNoteColor()));
        initialValues.put(COLUMN_NOTES_TYPE, String.valueOf(note.getNoteType()));
        initialValues.put(COLUMN_NOTES_IMAGE, note.getNoteImg());
        initialValues.put(COLUMN_NOTES_VIDEO, note.getNoteVideo());
        initialValues.put(COLUMN_NOTES_AUDIO, note.getNoteAudio());

        return db.insert(TABLE_NOTES, null, initialValues);
    }
}
