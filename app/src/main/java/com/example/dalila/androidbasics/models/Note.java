package com.example.dalila.androidbasics.models;

public class Note {

    private String title;
    private String content;
    private String timeStamp;

    public Note(String title, String content, String timeStamp) {
        this.title = title;
        this.content = content;
        this.timeStamp = timeStamp;
    }

    public Note() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
