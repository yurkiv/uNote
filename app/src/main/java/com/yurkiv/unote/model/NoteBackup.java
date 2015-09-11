package com.yurkiv.unote.model;

import java.util.Date;

/**
 * Created by yurkiv on 11.09.2015.
 */
public class NoteBackup {
    private String title;
    private String content;
    private Date updatedAt;
    private int color;

    public NoteBackup() {
    }

    public NoteBackup(String title, String content, Date updatedAt, int color) {
        this.title = title;
        this.content = content;
        this.updatedAt = updatedAt;
        this.color = color;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
