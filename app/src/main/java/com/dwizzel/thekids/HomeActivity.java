package com.dwizzel.thekids;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.dwizzel.utils.Tracer;
import com.dwizzel.utils.Utils;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "HomeActivity";

    public void onSubDestroy(){
        Tracer.log(TAG, "onSubDestroy");
        //si on a des chose a cleaner ici
    }

    public void onSubCreate(){
        Tracer.log(TAG, "onSubCreate");
        setContentView(R.layout.activity_home);
        createActionBar();
        setButton();
    }

    public void createActionBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        //set the title
        toolbar.setTitle(R.string.app_name);
        //set has the action bar
        setSupportActionBar(toolbar);
    }

    private void setButton() {
        Button buttWatchOverMe = findViewById(R.id.buttWatchOverMe);
        Button buttWatchOverSomeone = findViewById(R.id.buttWatchOverSomeone);
        Button buttWatchCurrent = findViewById(R.id.buttWatchCurrent);
        //butt create
        buttWatchOverSomeone.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this,
                                WatchOverSomeoneActivity.class);
                        //start activity
                        startActivity(intent);
                    }
                });
        buttWatchOverMe.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this,
                                WatchOverMeActivity.class);
                        //start activity
                        startActivity(intent);
                    }
                });
        buttWatchCurrent.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (getTrackerBinder() != null) {
                            Utils.getInstance().showToastMsg(HomeActivity.this,
                                    String.format(Utils.getInstance().getLocale(HomeActivity.this),
                                            "%d / %d",
                                            getTrackerBinder().getCounter(),
                                            getTrackerBinder().getTimeDiff()));
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_action_settings:
                break;
            case R.id.menu_action_about:
                break;
            case R.id.menu_action_logout:
                signOutUser();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
