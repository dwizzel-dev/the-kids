package com.dwizzel.thekids;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dwizzel.objects.UserObject;
import com.dwizzel.utils.Tracer;
import com.dwizzel.utils.Utils;

public abstract class ACommonSignInActivity extends AppCompatActivity {

    private static final String TAG = "ACommonSignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        Tracer.log(TAG, "onDestroy");
        super.onDestroy();
    }

    protected void userIsCreated(){
        //on affiche qu'il est logue
        try {
            Utils.getInstance().showToastMsg(
                    getApplicationContext(),
                    getResources().getString(R.string.toast_connected_as_and_last,
                            UserObject.getInstance().getEmail(),
                            UserObject.getInstance().getLastConnection(getApplicationContext())
                    )
            );
            //on va a activity principal
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            //start activity and clear the backStack
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );
            startActivity(intent);
        }catch (NullPointerException npe){
            Tracer.log(TAG, "userIsCreated.NullPointerException: " , npe);
        }catch (Exception e){
            Tracer.log(TAG, "userIsCreated.Exception: " , e);
        }
    }

}
