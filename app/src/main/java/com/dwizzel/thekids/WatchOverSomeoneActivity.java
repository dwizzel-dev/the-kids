package com.dwizzel.thekids;

import com.dwizzel.utils.Tracer;

public class WatchOverSomeoneActivity extends BaseActivity {

    private static final String TAG = "WatchOverSomeoneActivity";

    public void onSubDestroy(){
        Tracer.log(TAG, "onSubDestroy");
        //si on a des chose a cleaner ici
    }

    public void onSubCreate(){
        Tracer.log(TAG, "onSubCreate");
        setContentView(R.layout.activity_watch_over_someone);
        setTitle(R.string.watch_over_someone_title);
    }


}