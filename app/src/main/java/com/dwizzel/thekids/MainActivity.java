package com.dwizzel.thekids;

import android.os.Bundle;

public class MainActivity extends BaseActivity {

    @Override
    protected void startMainActivity(){
        setContentView(R.layout.activity_main);
        setTitle(R.string.main_title);
    }
}
