package com.dwizzel.thekids;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.Gravity;

import com.dwizzel.utils.Tracer;
import com.dwizzel.utils.Utils;

/*
* NOTES:
* http://stackandroid.com/tutorial/contact-picker-using-intent-android-tutorial/
* http://stackandroid.com/tutorial/android-reading-phone-contacts-example/
* */


public class SendInvitationForWatchingActivity extends BaseActivity {

    private static final String TAG = "SendInvitationForWatchingActivity";
    private Integer currFragmentNum;
    private boolean isActivityCreated = false;
    private FragmentManager mFragmentManager;

    public void onSubDestroy(){
        Tracer.log(TAG, "onSubDestroy");
        //si on a des chose a cleaner ici
    }

    public void onSubCreate(){
        Tracer.log(TAG, "onSubCreate");
        //pas qu'il recommence au onStart
        if(!isActivityCreated) {
            setContentView(R.layout.activity_send_invitation_for_watching);
            setTitle(R.string.watch_over_me_send_invitation);
            //fragment manager pour les anim transition
            mFragmentManager = getSupportFragmentManager();
            //on va setter le premier fragment du email
            if (findViewById(R.id.fragment_container) != null) {
                gotoFragment(0, null);
            }
        }
        isActivityCreated = true;
    }

    public void gotoFragment(int fragNum, Bundle bundle){
        Tracer.log(TAG, "gotoFragment: " + fragNum);
        //si active on remove celui qui est visible
        if(currFragmentNum!= null){
            //les multiples transitions
            TransitionSet transitionSet = new TransitionSet()
                    .addTransition(new Slide(Gravity.START))
                    .addTransition(new Fade(Fade.OUT));
            //le fragment precedent
            Fragment prevFragment = mFragmentManager.findFragmentById(R.id.fragment_container);
            prevFragment.setExitTransition(transitionSet);
        }

        currFragmentNum = fragNum;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment;

        switch(fragNum){
            case 1:
                fragment = new SendInvitationForWatchingFragment1();
                fragment.setArguments(bundle);
                TransitionSet transitionSet = new TransitionSet()
                        .addTransition(new Slide(Gravity.END))
                        .addTransition(new Fade(Fade.IN));
                //la transition
                fragment.setEnterTransition(transitionSet);
                //on rajoute le fragment
                fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
                fragmentTransaction.addToBackStack(String.format(
                        Utils.getInstance().getLocale(SendInvitationForWatchingActivity.this),
                        "fragment%d", fragNum));
                break;

            default:
                //le fragment du ask pour contact direct ou liste des contacts
                fragment = new SendInvitationForWatchingFragment0();
                //les arguments si il y a
                fragment.setArguments(bundle);
                //on rajoute le fragment
                fragmentTransaction.add(R.id.fragment_container, fragment).commit();
                break;
        }
    }


}
