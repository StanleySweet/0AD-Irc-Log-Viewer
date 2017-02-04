/*
 * Copyright <2017> <Stanislas Daniel Claude Dolcini>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.stan.recycler;

import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Stanislas Daniel Claude Dolcini on 02/02/17.
 * Defines the main activity of the application.
 */
public class MainActivity extends AppCompatActivity {
    SwipeRefreshLayout mSwipeRefreshLayout;
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean autoRefresh;
    private boolean isOnMainScreen;

    ArrayList<NavItem> mNavItems = new ArrayList<>();
    private Handler mHandler;
    Runnable refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.autoRefresh = false;

        InitHandler();
        InitSwiper();
        refreshItems();
        InitDrawer();
    }

    /**
     * Initialize the Handler to refresh every five seconds
     */
    private void InitHandler()
    {
        this.mHandler = new Handler();
        refresh = new Runnable() {
            public void run() {
                if(autoRefresh && isOnMainScreen)
                    refreshItems();
                mHandler.postDelayed(refresh, 10000);
            }
        };
        mHandler.post(refresh);
    }

    /**
     * Initialize the manual update functionality.
     */
    private void InitSwiper()
    {
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
    private void InitDrawer() {

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        try
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        catch (NullPointerException e)
        {
            Log.w("Error","Action bar is null" + e);
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


        mNavItems.add(new NavItem("Channel Log Viewer", "Meetup destination", R.drawable.ic_action_home));
        mNavItems.add(new NavItem("Preferences", "Change your preferences", R.drawable.ic_action_settings));
        mNavItems.add(new NavItem("About", "Get to know about us", R.drawable.ic_action_about));


        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

    }

    /**
     *
     * @param position the item position
     */
    private void selectItemFromDrawer(int position) {

        if ("Channel Log Viewer".equals(mNavItems.get(position).mTitle)) {
            this.isOnMainScreen = true;
            getFragmentManager().beginTransaction()
                    .remove(new SettingsFragment())
                    .commit();
            findViewById(R.id.container).setVisibility(LinearLayout.GONE);
            findViewById(R.id.About).setVisibility(LinearLayout.GONE);
            findViewById(R.id.mainContent).setVisibility(LinearLayout.VISIBLE);
        }

        if ("Preferences".equals(mNavItems.get(position).mTitle)) {
            this.isOnMainScreen = false;
            findViewById(R.id.container).setVisibility(LinearLayout.VISIBLE);
            getFragmentManager().beginTransaction()
                    .replace(R.id.mainContent, new SettingsFragment())
                    .commit();
            findViewById(R.id.About).setVisibility(LinearLayout.GONE);
            findViewById(R.id.mainContent).setVisibility(LinearLayout.GONE);
        }

        if ("About".equals(mNavItems.get(position).mTitle)) {
            this.isOnMainScreen = false;
            getFragmentManager().beginTransaction()
                    .remove(new SettingsFragment())
                    .commit();
            findViewById(R.id.About).setVisibility(LinearLayout.VISIBLE);
            findViewById(R.id.mainContent).setVisibility(LinearLayout.GONE);
            findViewById(R.id.container).setVisibility(LinearLayout.GONE);
        }


        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);


    }

    /**
     *
     */
    private void refreshItems() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean param1 = false;
        boolean param2 = false;

        for (Map.Entry<String, ?> entry : sharedPrefs.getAll().entrySet()) {

            Log.w("Key",entry.getKey());
            if ("wildfirerobotbot enabled".equals(entry.getKey()))
                param1 = Boolean.parseBoolean(entry.getValue().toString());

            if ("wildfirebot enabled".equals(entry.getKey()))
                param2 = Boolean.parseBoolean(entry.getValue().toString());

            if ("auto refresh enabled".equals(entry.getKey()))
                this.autoRefresh = Boolean.parseBoolean(entry.getValue().toString());
        }




        LogDownloader lg = new LogDownloader(param1, param2);
        Thread thread = new Thread(lg);
        thread.start();
        try {
            thread.join();

        } catch (InterruptedException e) {
            Log.w("Error",e);

        } finally {
            recyclerView.setAdapter(new LogMessageRecyclerAdapter(lg.getLogMessages()));
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


}
