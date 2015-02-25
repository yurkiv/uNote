package com.yurkiv.materialnotes.model;

/**
 * Created by yurkiv on 19.02.2015.
 */
public class Mention {
    private Long id;
    private String name;

    public Mention(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
