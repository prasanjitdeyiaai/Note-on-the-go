package com.pd.noteonthego.models;

import java.util.Comparator;

/**
 * Created by pradey on 8/6/2015.
 */
public class Note implements Comparator<Note>{

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    private int noteID;
    private String noteTitle;
    private String noteCreatedTimeStamp;

    public String getNoteLastModifiedTimeStamp() {
        return noteLastModifiedTimeStamp;
    }

    public void setNoteLastModifiedTimeStamp(String noteLastModifiedTimeStamp) {
        this.noteLastModifiedTimeStamp = noteLastModifiedTimeStamp;
    }

    private String noteLastModifiedTimeStamp;
    private String noteContent;

    public String getNoteTodoCheckedPositions() {
        return noteTodoCheckedPositions;
    }

    public void setNoteTodoCheckedPositions(String noteTodoCheckedPositions) {
        this.noteTodoCheckedPositions = noteTodoCheckedPositions;
    }

    private String noteTodoCheckedPositions;
    private String noteColor;
    private String noteType;
    private String noteImg;
    private String noteVideo;

    public int getIsReminderSet() {
        return isReminderSet;
    }

    public void setIsReminderSet(int isReminderSet) {
        this.isReminderSet = isReminderSet;
    }

    public String getReminderDateTime() {
        return reminderDateTime;
    }

    public void setReminderDateTime(String reminderDateTime) {
        this.reminderDateTime = reminderDateTime;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    private int isReminderSet;
    private String reminderDateTime;
    private String reminderType;

    public int getIsStarred() {
        return isStarred;
    }

    public void setIsStarred(int isStarred) {
        this.isStarred = isStarred;
    }

    private int isStarred;

    public Note() {

    }

    public Note(String noteTitle, String noteContent, String noteTodoCheckedPositions, String noteTimeStamp, String noteLastModifiedTimeStamp, String noteColor,
                String noteType, String noteImg, String noteVideo,
                String noteAudio, int isReminderSet, String reminderDateTime, String reminderType, int isStarred) {
        this.noteCreatedTimeStamp = noteTimeStamp;
        this.noteContent = noteContent;
        this.noteTodoCheckedPositions = noteTodoCheckedPositions;
        this.noteLastModifiedTimeStamp = noteLastModifiedTimeStamp;
        this.noteColor = noteColor;
        this.noteType = noteType;
        this.noteImg = noteImg;
        this.noteVideo = noteVideo;
        this.noteAudio = noteAudio;
        this.noteTitle = noteTitle;
        this.isReminderSet = isReminderSet;
        this.reminderDateTime = reminderDateTime;
        this.reminderType = reminderType;
        this.isStarred = isStarred;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteCreatedTimeStamp() {
        return noteCreatedTimeStamp;
    }

    public void setNoteCreatedTimeStamp(String noteCreatedTimeStamp) {
        this.noteCreatedTimeStamp = noteCreatedTimeStamp;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteColor() {
        return noteColor;
    }

    public void setNoteColor(String noteColor) {
        this.noteColor = noteColor;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String getNoteImg() {
        return noteImg;
    }

    public void setNoteImg(String noteImg) {
        this.noteImg = noteImg;
    }

    public String getNoteVideo() {
        return noteVideo;
    }

    public void setNoteVideo(String noteVideo) {
        this.noteVideo = noteVideo;
    }

    public String getNoteAudio() {
        return noteAudio;
    }

    public void setNoteAudio(String noteAudio) {
        this.noteAudio = noteAudio;
    }

    private String noteAudio;

    @Override
    public int compare(Note lhs, Note rhs) {
        return 0;
    }

    /*
    sort notes using last edited in ascending order
     */
    public static Comparator<Note> noteLastEditedAscComparator
            = new Comparator<Note>() {

        public int compare(Note note1, Note note2) {

            String noteEdited1 = note1.getNoteLastModifiedTimeStamp().toUpperCase();
            String noteEdited2 = note2.getNoteLastModifiedTimeStamp().toUpperCase();

            //ascending order
            return noteEdited2.compareTo(noteEdited1);
        }
    };

    /*
    sort notes using last created in ascending order
     */
    public static Comparator<Note> noteLastCreatedAscComparator
            = new Comparator<Note>() {

        public int compare(Note note1, Note note2) {

            String noteEdited1 = note1.getNoteCreatedTimeStamp().toUpperCase();
            String noteEdited2 = note2.getNoteCreatedTimeStamp().toUpperCase();

            //ascending order
            return noteEdited1.compareTo(noteEdited2);
        }
    };
}
