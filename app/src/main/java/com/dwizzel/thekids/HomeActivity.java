package com.dwizzel.thekids;

import android.os.Bundle;

public class HomeActivity extends BaseActivity {

    @Override
    protected void startMainApp(){
        super.startMainApp();
        setContentView(R.layout.activity_home);
        setTitle(R.string.main_title);
    }

}
