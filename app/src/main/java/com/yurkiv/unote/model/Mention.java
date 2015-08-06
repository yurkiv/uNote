package com.yurkiv.unote.model;

import io.realm.RealmObject;

/**
 * Created by yurkiv on 19.02.2015.
 */
public class Mention extends RealmObject {
    private String name;

    public Mention() {
    }

    public Mention(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
