package com.pd.noteonthego.models;

import com.pd.noteonthego.helper.NoteColor;
import com.pd.noteonthego.helper.NoteType;

/**
 * Created by pradey on 8/6/2015.
 */
public class Note {

    private String noteTitle;
    private String noteTimeStamp;
    private String noteContent;
    private NoteColor noteColor;
    private NoteType noteType;
    private String noteImg;
    private String noteVideo;

    public Note(String noteTitle, String noteContent, String noteTimeStamp, NoteColor noteColor,
                NoteType noteType, String noteImg, String noteVideo,
                String noteAudio) {
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

    public NoteColor getNoteColor() {
        return noteColor;
    }

    public void setNoteColor(NoteColor noteColor) {
        this.noteColor = noteColor;
    }

    public NoteType getNoteType() {
        return noteType;
    }

    public void setNoteType(NoteType noteType) {
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
