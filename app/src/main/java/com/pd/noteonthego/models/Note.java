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

    public Note(String noteTimeStamp, String noteContent, String noteColor,
                String noteType, String noteImg, String noteVideo,
                String noteAudio, String noteTitle) {
        this.noteTimeStamp = noteTimeStamp;
        this.noteContent = noteContent;
        this.noteColor = noteColor;
        this.noteType = noteType;
        this.noteImg = noteImg;
        this.noteVideo = noteVideo;
        this.noteAudio = noteAudio;
        this.noteTitle = noteTitle;
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
