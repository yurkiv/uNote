package com.yurkiv.materialnotes.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.yurkiv.materialnotes.model.Hashtag;
import com.yurkiv.materialnotes.model.Mention;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yurkiv on 20.02.2015.
 */
public class Utility {

    public static ArrayList<Hashtag> getHashtagsFromContent(String body){
        ArrayList<Hashtag> hashtags=new ArrayList<>();
        char prefix = '#';
        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            hashtags.add(new Hashtag(matcher.group()));
        }
        return hashtags;
    }

    public static ArrayList<Mention> getMentionsFromContent(String body){
        ArrayList<Mention> mentions=new ArrayList<>();
        char prefix = '@';
        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            mentions.add(new Mention(matcher.group()));
        }
        return mentions;
    }

    public ArrayList<String> getSpans(String body, char prefix) {
        ArrayList<String> spans = new ArrayList<>();

        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);

        // Check all occurrences
        while (matcher.find()) {
//            int[] currentSpan = new int[2];
//            currentSpan[0] = matcher.start();
//            currentSpan[1] = matcher.end();

            spans.add(matcher.group());
        }

        return  spans;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
