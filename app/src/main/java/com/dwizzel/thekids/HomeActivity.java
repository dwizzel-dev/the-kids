package com.dwizzel.thekids;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dwizzel.utils.Auth;
import com.dwizzel.utils.FirestoreData;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "TheKids.HomeActivity";

    @Override
    protected void startMainActivity(){
        setContentView(R.layout.activity_home);
        setTitle(R.string.main_title);
        //la action bar
        createActionBar();
        //check the user infos if some
        checkUserInfos();
    }

    private void checkUserInfos(){
        FirestoreData firestoreData = FirestoreData.getInstance();
        try {
            firestoreData.getUserinfos();
        }catch (Exception e){
            Log.w(TAG, "checkUserInfos.exception: ", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
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

    //creer la top tool bar
    private void createActionBar(){
        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //set the title
        toolbar.setTitle(R.string.app_name);
        //set has the action bar
        setSupportActionBar(toolbar);
    }

}
