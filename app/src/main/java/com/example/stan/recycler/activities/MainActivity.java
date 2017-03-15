/*
 * Copyright <2017> <Stanislas Daniel Claude Dolcini>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.stan.recycler.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.stan.recycler.R;
import com.example.stan.recycler.adapters.DrawerListAdapter;
import com.example.stan.recycler.adapters.LogMessageRecyclerAdapter;
import com.example.stan.recycler.fragments.AboutFragment;
import com.example.stan.recycler.fragments.IRCChatFragment;
import com.example.stan.recycler.fragments.SettingsFragment;
import com.example.stan.recycler.model.LogDownloader;
import com.example.stan.recycler.model.NavItem;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Stanislas Daniel Claude Dolcini on 02/02/17.
 * Defines the main activity of the application.
 */
public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mDrawerList;
    private RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean autoRefresh, isOnMainScreen;
    private ArrayList<NavItem> mNavItems = new ArrayList<>();
    private Handler mHandler;
    private Runnable refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.autoRefresh = false;
        initHandler();
        initSwiper();
        refreshItems();
        initDrawer();
    }


    /**
     * @return the current state of the network.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    /**
     * Initialize the Handler to refresh every ten seconds
     */
    private void initHandler() {
        this.mHandler = new Handler();
        refresh = new Runnable() {
            @Override
            public void run() {
                if (autoRefresh && isOnMainScreen)
                {
                    if(isNetworkAvailable())
                    {
                        refreshItems();
                    }
                    mHandler.postDelayed(refresh, 10000);
                }
            }
        };
        mHandler.post(refresh);
    }

    /**
     * Initialize the manual update functionality.
     */
    private void initSwiper() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }

        });
    }

    /**
     * Initialize the left slider menu.
     */
    private void initDrawer() {

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.w("Error", "Action bar is null" + e);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d("TAG:", "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        // Add menu options
        addNavItem("Channel Log Viewer", "Meetup destination", R.drawable.ic_action_home);
        addNavItem("Preferences", "Change your preferences", R.drawable.ic_action_settings);
        addNavItem("IRC Chat", "Chat with the devs", R.drawable.ic_forum_black_24dp);
        addNavItem("About", "Get to know about us", R.drawable.ic_action_about);

        // Populate the Navigation Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerList.setAdapter(new DrawerListAdapter(this, mNavItems));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

    }

    /**
     * @param itemName
     * @param itemDescription
     * @param itemId
     */
    private void addNavItem(String itemName, String itemDescription, int itemId) {
        mNavItems.add(new NavItem(itemName, itemDescription, itemId));
    }

    /**
     * @param position the item position
     */
    private void selectItemFromDrawer(int position) {
        switch (mNavItems.get(position).getmTitle()) {
            case "Channel Log Viewer":
                this.isOnMainScreen = true;
                break;
            case "Preferences":
                this.isOnMainScreen = false;
                getFragmentManager().beginTransaction()
                        .replace(R.id.mainContent, new SettingsFragment())
                        .commit();
                break;
            case "IRC Chat":
                this.isOnMainScreen = false;
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new IRCChatFragment())
                        .commit();
                break;
            case "About":
                this.isOnMainScreen = false;
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new AboutFragment())
                        .commit();
                break;
        }

        findViewById(R.id.container).setVisibility(!this.isOnMainScreen ?
                LinearLayout.VISIBLE : LinearLayout.GONE);
        findViewById(R.id.mainContent).setVisibility(this.isOnMainScreen ?
                LinearLayout.VISIBLE : LinearLayout.GONE);

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).getmTitle());
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    private void refreshItems() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean wildfireRobotEnabled = false,wildfireBotEnabled = false;

        for (Map.Entry<String, ?> entry : sharedPrefs.getAll().entrySet()) {

            //Log.w("Key", entry.getKey());
            if ("wildfirerobotbot enabled".equals(entry.getKey()))
                wildfireRobotEnabled = Boolean.parseBoolean(entry.getValue().toString());

            if ("wildfirebot enabled".equals(entry.getKey()))
                wildfireBotEnabled = Boolean.parseBoolean(entry.getValue().toString());

            if ("auto refresh enabled".equals(entry.getKey()))
                this.autoRefresh = Boolean.parseBoolean(entry.getValue().toString());
        }


        LogDownloader lg = new LogDownloader(wildfireRobotEnabled, wildfireBotEnabled);
        Thread thread = new Thread(lg);
        thread.start();
        try {
            thread.join();

        } catch (InterruptedException e) {
            Log.w("Error", e);

        } finally {
            recyclerView.setAdapter(new LogMessageRecyclerAdapter(lg.getLogMessages()));
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
}
