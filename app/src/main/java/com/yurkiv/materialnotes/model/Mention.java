package com.yurkiv.materialnotes.model;

/**
 * Created by yurkiv on 19.02.2015.
 */
public class Mention {
    private Long id;
    private String text;

    public Mention(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
