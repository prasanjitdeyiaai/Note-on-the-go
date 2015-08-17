package com.pd.noteonthego.helper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.pd.noteonthego.models.Note;

import java.util.ArrayList;
import java.util.HashMap;

public class NoteContentProvider extends ContentProvider {
    public NoteContentProvider() {
    }

    static final String PROVIDER_NAME = "com.pd.noteonthego.provider";
    public static final String URL = "content://" + PROVIDER_NAME + "/note";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    private static HashMap<String, String> NOTE_PROJECTION_MAP;

    static final int NOTE = 1;
    static final int NOTE_ID = 2;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "note", NOTE);
        uriMatcher.addURI(PROVIDER_NAME, "note/#", NOTE_ID);
    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "noteonthego.db";
    private static final int DATABASE_VERSION = 1;

    // notes table
    public static final String TABLE_NOTES = "note";

    // notes table columns
    public static final String COLUMN_NOTES_ID = "notes_ID";
    public static final String COLUMN_NOTES_TITLE = "notes_title";
    public static final String COLUMN_NOTES_CREATED_TIMESTAMP = "notes_created_timestamp";
    public static final String COLUMN_NOTES_lAST_MODIFIED_TIMESTAMP = "notes_last_modified_timestamp";
    public static final String COLUMN_NOTES_CONTENT = "notes_content";
    public static final String COLUMN_NOTES_COLOR = "notes_color";
    public static final String COLUMN_NOTES_TYPE = "notes_type";
    public static final String COLUMN_NOTES_IMAGE = "notes_image";
    public static final String COLUMN_NOTES_VIDEO = "notes_video";
    public static final String COLUMN_NOTES_AUDIO = "notes_audio";
    public static final String COLUMN_NOTES_IS_REMINDER_SET = "notes_is_reminder_set";
    public static final String COLUMN_NOTES_REMINDER_DATETIME = "notes_reminder_datetime";
    public static final String COLUMN_NOTES_REMINDER_TYPE = "notes_reminder_type";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DBHelper extends SQLiteOpenHelper {

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
                    + COLUMN_NOTES_CREATED_TIMESTAMP
                    + " text not null, "
                    + COLUMN_NOTES_lAST_MODIFIED_TIMESTAMP
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
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case NOTE:
                count = db.delete(TABLE_NOTES, selection, selectionArgs);
                break;

            case NOTE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(TABLE_NOTES, COLUMN_NOTES_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        /**
         * Add a new note record
         */
        long rowID = db.insert(TABLE_NOTES, "", values);

        /**
         * If record is added successfully
         */

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        Context context = getContext();
        DBHelper dbHelper = new DBHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NOTES);

        switch (uriMatcher.match(uri)) {
            case NOTE:
                qb.setProjectionMap(NOTE_PROJECTION_MAP);
                break;

            case NOTE_ID:
                qb.appendWhere(COLUMN_NOTES_ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder == "") {
            /**
             * By default sort on note titles
             */
            sortOrder = COLUMN_NOTES_CREATED_TIMESTAMP + " DESC";
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case NOTE:
                count = db.update(TABLE_NOTES, values, selection, selectionArgs);
                break;

            case NOTE_ID:
                count = db.update(TABLE_NOTES, values, COLUMN_NOTES_ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * @param cursor
     * @return list of notes
     */
    public static ArrayList<Note> getNoteListFromCursor(Cursor cursor) {
        ArrayList<Note> listOfNotes = new ArrayList<Note>();

        try {
            if (cursor != null) {
                cursor.moveToFirst();

                while (cursor.isAfterLast() == false) {

                    Note note = new Note();

                    note.setNoteID(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTES_ID)));
                    note.setNoteTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_TITLE)));
                    note.setNoteContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_CONTENT)));
                    note.setNoteCreatedTimeStamp(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_CREATED_TIMESTAMP)));
                    note.setNoteLastModifiedTimeStamp(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_lAST_MODIFIED_TIMESTAMP)));
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
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }

        return listOfNotes;
    }

    /**
     * @param cursor
     * @return a note
     */
    public static Note getNoteFromCursor(Cursor cursor) {
        Note note = new Note();

        try {
            if (cursor != null) {
                cursor.moveToFirst();

                while (cursor.isAfterLast() == false) {

                    note.setNoteID(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTES_ID)));
                    note.setNoteTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_TITLE)));
                    note.setNoteContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_CONTENT)));
                    note.setNoteCreatedTimeStamp(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_CREATED_TIMESTAMP)));
                    note.setNoteLastModifiedTimeStamp(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_lAST_MODIFIED_TIMESTAMP)));
                    note.setNoteColor(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_COLOR)));
                    note.setNoteType(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_TYPE)));
                    note.setNoteImg(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_IMAGE)));
                    note.setNoteVideo(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_VIDEO)));
                    note.setNoteAudio(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_AUDIO)));
                    note.setIsReminderSet(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTES_IS_REMINDER_SET)));
                    note.setReminderDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_REMINDER_DATETIME)));
                    note.setReminderType(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_REMINDER_TYPE)));

                    cursor.moveToNext();
                }
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }
        return note;
    }
}
