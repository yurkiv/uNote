package com.yurkiv.unote.util;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.yurkiv.unote.model.Hashtag;
import com.yurkiv.unote.model.Mention;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yurkiv on 20.02.2015.
 */
public class Utility {

    private static Pattern patternHashtags = Pattern.compile("(#\\w+)");
    private static Pattern patternMentions = Pattern.compile("(@\\w+)");

    public static SpannableString styleText(String s) {
        SpannableString hashText = new SpannableString(s);
        Matcher matcher = patternHashtags.matcher(hashText);
        while (matcher.find()){
            hashText.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5722")), matcher.start(), matcher.end(), 0);
        }
        matcher = patternMentions.matcher(hashText);
        while (matcher.find()){
            hashText.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5722")), matcher.start(), matcher.end(), 0);
        }
        return hashText;
    }

    public static ArrayList<Hashtag> getHashtagsFromContent(String body){
        ArrayList<Hashtag> hashtags=new ArrayList<>();
        Matcher matcher = patternHashtags.matcher(body);
        Hashtag hashtag=null;
        while (matcher.find()) {
            hashtag=new Hashtag();
            hashtag.setName(matcher.group());
            hashtags.add(hashtag);
        }
        return hashtags;
    }

    public static ArrayList<Mention> getMentionsFromContent(String body){
        ArrayList<Mention> mentions=new ArrayList<>();
        Matcher matcher = patternMentions.matcher(body);
        while (matcher.find()) {
            mentions.add(new Mention(matcher.group()));
        }
        return mentions;
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
