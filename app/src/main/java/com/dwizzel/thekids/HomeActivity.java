package com.dwizzel.thekids;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.dwizzel.utils.Utils;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "TheKids.HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.w(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy(){
        Log.w(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void startActivity(){
        Log.w(TAG, "startActivity");
        super.startActivity();
        setContentView(R.layout.activity_home);
        setTitle(R.string.main_title);
        setButton();
        createActionBar();
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

    private void setButton(){
        Button buttWatchOverMe = findViewById(R.id.buttWatchOverSomeone);
        //butt create
        buttWatchOverMe.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if(getTrackerBinder() != null) {
                            Utils.getInstance().showToastMsg(HomeActivity.this,
                                    String.format("counter: %d", getTrackerBinder().getCounter()));
                        }
                    }
                });
    }

    private void createActionBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        //set the title
        toolbar.setTitle(R.string.app_name);
        //set has the action bar
        setSupportActionBar(toolbar);
    }

}
