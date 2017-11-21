package com.dwizzel.thekids;

import com.dwizzel.utils.Tracer;

public class WatchOverMeActivity extends BaseActivity {

    private static final String TAG = "WatchOverMeActivity";

    public void onSubDestroy(){
        Tracer.log(TAG, "onSubDestroy");
        //si on a des chose a cleaner ici
    }

    public void onSubCreate(){
        Tracer.log(TAG, "onSubCreate");
        setContentView(R.layout.activity_watch_over_me);
        setTitle(R.string.watch_over_me_title);
        setButton();
        //on cherche la list
        getTrackerBinder().getWatchersList();
    }

    private void setButton(){
        /*
        Button buttWatchOverMe = findViewById(R.id.buttWatchOverSomeone);
        //butt create
        buttWatchOverMe.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if(getTrackerBinder() != null) {
                            Utils.getInstance().showToastMsg(WatchOverMeActivity.this,
                                    String.format(Utils.getInstance().getLocale(WatchOverMeActivity.this),
                                            "counter: %d", getTrackerBinder().getCounter()));
                        }
                    }
                });
        */
    }

}
