package com.yurkiv.materialnotes.model;

import java.io.Serializable;

public class Hashtag implements Serializable {
    private Long id;
    private String name;

    public Hashtag() {
    }

    public Hashtag(String name) {
        this.name = name;
    }

    public Hashtag(Long id, String name) {
        this.id = id;
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

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Hashtag [id=").append(id)
                .append(", name=").append(name)
                .append("]").toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj) return true;
        if (obj==null) return false;
        if (getClass()!=obj.getClass()) return false;
        Hashtag hashtag= (Hashtag) obj;
        if (name ==null){
            if (hashtag.name !=null) return false;
        } else if (!name.equals(hashtag.name)) return false;

        if (id==null){
            if (hashtag.id!=null)return false;
        } else if (!id.equals(hashtag.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime=31;
        int result=1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
}
