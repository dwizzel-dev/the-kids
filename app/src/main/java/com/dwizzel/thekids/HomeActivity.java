package com.dwizzel.thekids;


import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dwizzel.utils.Utils;
import com.dwizzel.utils.FirestoreData;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "TheKids.HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.w(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void startMainActivity(boolean bFetchUserData){
        setContentView(R.layout.activity_home);
        setTitle(R.string.main_title);
        //setContent();
        setButton();
        createActionBar();
        if(bFetchUserData) {
            checkUserInfos();
        }
    }

    /*
    private void setContent(){
        TextView textView1 = findViewById(R.id.textViewHomeDescription1);
        textView1.setText(Html.fromHtml(getResources().getString(R.string.home_description_1)));
        TextView textView2 = findViewById(R.id.textViewHomeDescription2);
        textView2.setText(Html.fromHtml(getResources().getString(R.string.home_description_2)));
    }
    */

    private void setButton(){
        Button buttWatchOverMe = findViewById(R.id.buttWatchOverSomeone);
        //butt create
        buttWatchOverMe.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if(mTrackerService != null){
                            try {
                                long counter = mTrackerService.getCounter();
                                sUtils.showToastMsg(HomeActivity.this, String.format("counter: %d", counter));
                            }catch (DeadObjectException doe){
                                Log.w(TAG, "mTrackerService.exception: ", doe);
                            }catch (RemoteException re){
                                Log.w(TAG, "mTrackerService.exception: ", re);
                            }


                        }
                    }
                });
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
