package com.yurkiv.unote.model;

import io.realm.RealmObject;

public class Hashtag extends RealmObject {
    private String name;

    public Hashtag() {
    }

    public Hashtag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
