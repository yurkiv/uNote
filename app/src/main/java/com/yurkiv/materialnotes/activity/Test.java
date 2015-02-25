package com.yurkiv.materialnotes.activity;

import com.yurkiv.materialnotes.model.Hashtag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yurkiv on 23.02.2015.
 */
public class Test {
    public static void main(String[] args) {
        System.out.println("sfs");

        String cause="";
        StringBuilder stringBuilder=new StringBuilder();
        List<Hashtag> items=new ArrayList<>();
        items.add(new Hashtag((long) 1, "#hashtag 0"));
        items.add(new Hashtag((long) 2, "#hashtag 1"));
        items.add(new Hashtag((long) 3, "#hashtag 2"));
        items.add(new Hashtag((long) 4, "#hashtag 3"));
        items.add(new Hashtag((long) 5, "#hashtag 3"));
        items.add(new Hashtag((long) 6, "#hashtag 3"));


        for (int i = 0; i < items.size(); i++) {
            stringBuilder.append(items.get(i).getId());
            if (i!=items.size()-1){
                stringBuilder.append(",");
            }
        }
        cause=stringBuilder.toString();
        System.out.println(cause);
    }
}
