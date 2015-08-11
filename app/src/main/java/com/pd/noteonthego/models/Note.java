package com.pd.noteonthego.models;

/**
 * Created by pradey on 8/6/2015.
 */
public class Note {

    private String noteTitle;
    private String noteTimeStamp;
    private String noteContent;
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

    public Note() {

    }

    public Note(String noteTitle, String noteContent, String noteTimeStamp, String noteColor,
                String noteType, String noteImg, String noteVideo,
                String noteAudio, int isReminderSet, String reminderDateTime, String reminderType) {
        this.noteTimeStamp = noteTimeStamp;
        this.noteContent = noteContent;
        this.noteColor = noteColor;
        this.noteType = noteType;
        this.noteImg = noteImg;
        this.noteVideo = noteVideo;
        this.noteAudio = noteAudio;
        this.noteTitle = noteTitle;
        this.isReminderSet = isReminderSet;
        this.reminderDateTime = reminderDateTime;
        this.reminderType = reminderType;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteTimeStamp() {
        return noteTimeStamp;
    }

    public void setNoteTimeStamp(String noteTimeStamp) {
        this.noteTimeStamp = noteTimeStamp;
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
}
