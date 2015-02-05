package com.yurkiv.materialnotes.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yurkiv on 02.02.2015.
 */
public class Note implements Serializable {
    private Long id;
    private String title;
    private String content;
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @Override
    public boolean equals(Object obj) {
        if (this==obj) return true;
        if (obj==null) return false;
        if (getClass()!=obj.getClass()) return false;
        Note note= (Note) obj;
        if (content==null){
            if (note.content!=null) return false;
        } else if (!content.equals(note.content)) return false;

        if (title==null){
            if (note.title!=null)return false;
        } else if (!title.equals(note.title)) return false;

        if (id==null){
            if (note.id!=null)return false;
        } else if (!id.equals(note.id)) return false;

        if (updatedAt==null){
            if (note.updatedAt!=null)return false;
        } else if (!updatedAt.equals(note.updatedAt)) return false;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Note [id=").append(id)
                .append(", title=").append(title)
                .append(", content").append(content)
                .append(", updatedAt").append(updatedAt)
                .append("]").toString();
    }

    @Override
    public int hashCode() {
        final int prime=31;
        int result=1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
        return result;
    }
}
