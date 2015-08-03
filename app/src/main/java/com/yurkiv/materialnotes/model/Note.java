package com.yurkiv.materialnotes.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by yurkiv on 02.02.2015.
 */
public class Note extends RealmObject {

    private String id;

    private String title;

    private String content;

    private Date updatedAt;

    private int color;

    private RealmList<Hashtag> hashtags;

    public Note(String title) {
        this.title = title;
    }

    public Note() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public RealmList<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(RealmList<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
