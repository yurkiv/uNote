package com.yurkiv.materialnotes.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.yurkiv.materialnotes.R;
import com.yurkiv.materialnotes.adapter.HashtagAdapter;
import com.yurkiv.materialnotes.adapter.MentionAdapter;
import com.yurkiv.materialnotes.model.Hashtag;
import com.yurkiv.materialnotes.model.Mention;
import com.yurkiv.materialnotes.util.HashtagCallbacks;
import com.yurkiv.materialnotes.util.MentionCallbacks;
import com.yurkiv.materialnotes.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yurkiv on 18/02/2015.
 */
public class NavigationDrawerFragment extends Fragment {
    private static final String TAG = NavigationDrawerFragment.class.getSimpleName();
    private HashtagCallbacks hashtagCallbacks;
    private MentionCallbacks mentionCallbacks;

    private ListView listHashtags;
    private ListView listMentions;

    private List<Hashtag> hashtagItems;
    private List<Mention> mentionItems;

    private HashtagAdapter hashtagAdapter;
    private MentionAdapter mentionAdapter;

    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        listHashtags = (ListView) view.findViewById(R.id.hashtagList);
        listMentions = (ListView) view.findViewById(R.id.mentionList);
        hashtagItems = getHashtagList();
        mentionItems = getMentionsList();
        hashtagAdapter = new HashtagAdapter(hashtagItems);
        mentionAdapter = new MentionAdapter(mentionItems);
        listHashtags.setAdapter(hashtagAdapter);
        listMentions.setAdapter(mentionAdapter);
        Utility.setListViewHeightBasedOnChildren(listHashtags);
        Utility.setListViewHeightBasedOnChildren(listMentions);

        listHashtags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                }
                if (hashtagCallbacks != null) {
                    hashtagCallbacks.onHashtagItemSelected(hashtagItems.get(position));
                }
            }
        });

//        listMentions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (mDrawerLayout != null) {
//                    mDrawerLayout.closeDrawer(mFragmentContainerView);
//                }
//                if (mentionCallbacks != null) {
//                    mentionCallbacks.onMentionItemSelected(position);
//                }
//            }
//        });

        final ScrollView sv = (ScrollView) view.findViewById(R.id.svScroll);
        ViewTreeObserver vto = sv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                sv.scrollTo(0, 0);
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            hashtagCallbacks = (HashtagCallbacks) activity;
            mentionCallbacks = (MentionCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public void setActionBarDrawerToggle(ActionBarDrawerToggle actionBarDrawerToggle) {
        mActionBarDrawerToggle = actionBarDrawerToggle;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.myPrimaryDarkColor));

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu();
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hashtagCallbacks = null;
        mentionCallbacks = null;
    }

    public void updateNavigationDrawerHashtagList(List<Hashtag> hashtags){
        hashtagItems.clear();
        hashtagItems.addAll(hashtags);
        Log.i(TAG, hashtagItems.toString());
        hashtagAdapter.notifyDataSetChanged();
        Utility.setListViewHeightBasedOnChildren(listHashtags);

    }

    public List<Hashtag> getHashtagList() {
        List<Hashtag> items = new ArrayList<>();
        items.add(new Hashtag("#hash1"));
//        items.add(new Hashtag("#hash2"));
//        items.add(new Hashtag("#hash3"));
//        items.add(new Hashtag("#hash4"));
//        items.add(new Hashtag("#hash5"));
        return items;
    }

    public List<Mention> getMentionsList() {
        List<Mention> items = new ArrayList<>();
        items.add(new Mention("@mention 0"));
        items.add(new Mention("@mention 1"));
        items.add(new Mention("@mention 2"));
        items.add(new Mention("@mention 3"));
        items.add(new Mention("@mention 4"));
        items.add(new Mention("@mention 5"));
        return items;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
    }

}
