package com.dwizzel.thekids;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;

import com.dwizzel.Const;
import com.dwizzel.models.UserModel;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.observers.BooleanObserver;
import com.dwizzel.services.ITrackerBinderCallback;
import com.dwizzel.services.TrackerService;
import com.dwizzel.utils.Utils;

public class CreateUserWithEmailActivity extends AppCompatActivity {

    private static final String TAG = "TheKids.CreateUserWithEmail";
    private String email = "";
    private String psw = "";
    private Integer currFragmentNum;
    private FragmentManager mFragmentManager;
    private BooleanObserver mServiceBoundObservable = new BooleanObserver(false);
    public TrackerService.TrackerBinder mTrackerBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w(TAG, "onServiceConnected");
            mTrackerBinder = (TrackerService.TrackerBinder)service;
            mTrackerBinder.registerCallback(mServiceCallback);
            mServiceBoundObservable.set(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "onServiceDisconnected");
            mServiceBoundObservable.set(false);
            mTrackerBinder = null;
        }
    };

    public TrackerService.TrackerBinder getTrackerBinder(){
        return mTrackerBinder;
    }

    private void bindToAuthService(){
        if(!mServiceBoundObservable.get()) {
            Intent intent = TrackerService.getIntent(this);
            startService(intent);
            //bind to the service, si pas de startService se ferme auto apres la femeture de L'appli
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ITrackerBinderCallback mServiceCallback = new ITrackerBinderCallback() {

        private static final String TAG = "TheKids.ITrackerBinder";

        @Override
        public void handleResponse(long counter){
            //Log.d(TAG, String.format("thread counter: %d", counter));
        }
        @Override
        public void onSignedIn(Object obj){
            Log.d(TAG, "onSignedIn");
            //on enleve le loader
            Utils.getInstance().hideProgressDialog();
            //check les erreurs et exception
            int err = ((ServiceResponseObject)obj).getErr();
            switch(err){
                case Const.except.NO_CONNECTION:
                    Utils.getInstance().showToastMsg(CreateUserWithEmailActivity.this,
                            R.string.err_no_connectivity);
                    break;
                case Const.error.NO_ERROR:
                    userIsCreatedRoutine();
                    break;
                case Const.error.ERROR_WEAK_PASSWORD:
                    gotoFragmentAndShowErrors(0, R.string.psw_weak);
                    break;
                case Const.error.ERROR_EMAIL_EXIST:
                    gotoFragmentAndShowErrors(0, R.string.email_in_use);
                    break;
                case Const.error.ERROR_INVALID_CREDENTIALS:
                    gotoFragmentAndShowErrors(0, R.string.email_invalid);
                    break;
                default:
                    break;
            }
        }
        @Override
        public void onSignedOut(Object obj){
            Log.d(TAG, "onSignedOut");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindToAuthService();
        setContentView(R.layout.activity_create_user_with_email);
        setTitle(R.string.register_with_email_title);
        //fragment manager pour les anim transition
        mFragmentManager = getSupportFragmentManager();
        //on va setter le premier fragment du email
        if (findViewById(R.id.fragment_container) != null) {
            //si on fait juste un restore pas beson dereloader
            if (savedInstanceState != null) {
                return;
            }
            gotoFragment(0, null);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.getInstance().hideProgressDialog();
    }

    @Override
    protected void onDestroy(){
        Log.w(TAG, "onDestroy");
        super.onDestroy();
        //clear le binder
        if(mTrackerBinder != null) {
            unbindService(mConnection);
            //reset
            mServiceCallback = null;
            mServiceBoundObservable.set(false);
            mConnection = null;
        }
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private void gotoFragmentAndShowErrors(int fragNum, int msgId){
        Bundle bundle = new Bundle();
        bundle.putInt("msg", msgId);
        gotoFragment(fragNum, bundle);
    }

    protected int setEmailFromFragment(String email) {
        //set le email
        int err = Utils.getInstance().isValidEmail(email);
        if(err != 0){
            return err;
            }
        //le setter du email
        setEmail(email);
        //va au fragment de psw
        gotoFragment(1, null);
        //tout est ok
        return 0;
    }

    protected int setPswFromFragment(String[] psw) {
        //set le psw
        int err = Utils.getInstance().isValidPsw(psw);
        if(err != 0){
            return err;
            }
        //le setter du password
        setPsw(psw[0]);
        //on envoi ca et on check pour les erreurs
        createUser();
        //tout est ok
        return 0;
    }

    private void userIsCreatedRoutine(){
        //on affiche qu'il est logue
        Utils.getInstance().showToastMsg(CreateUserWithEmailActivity.this,
                getResources().getString(R.string.toast_connected_as,
                        ((UserModel)mTrackerBinder.getUser()).getEmail()));
        //on va a activity principal
        Intent intent = new Intent(CreateUserWithEmailActivity.this,
                HomeActivity.class);
        //start activity and clear the backStack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void createUser() {
        //on va faire un listener sur le resultat
        if (mTrackerBinder != null) {
            //on met un loader
            Utils.getInstance().showProgressDialog(this);
            //on call le service
            mTrackerBinder.createUser(email, psw);
        }
    }

    private void gotoFragment(int fragNum, Bundle bundle){
        //TODO; faire un meilleure gestion du addToBackStack()
        //si active on remove celui qui est visible
        if(currFragmentNum!= null){
            //les multiples transitions
            TransitionSet transitionSet = new TransitionSet()
                    .addTransition(new Slide(Gravity.LEFT))
                    .addTransition(new Fade(Fade.OUT));
            //le fragment precedent
            Fragment prevFragment = mFragmentManager.findFragmentById(R.id.fragment_container);
            prevFragment.setExitTransition(transitionSet);
        }

        currFragmentNum = new Integer(fragNum);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment;

        switch(fragNum){
            case 1:
                //le fragment du psw
                fragment = new CreateUserWithEmailFragment1();
                fragment.setArguments(bundle);
                //fragment1.setArguments(getIntent().getExtras());
                TransitionSet transitionSet = new TransitionSet()
                        .addTransition(new Slide(Gravity.RIGHT))
                        .addTransition(new Fade(Fade.IN));
                //la transition
                fragment.setEnterTransition(transitionSet);
                //on rajoute le fragment
                fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
                fragmentTransaction.addToBackStack(String.format("fragment%d", fragNum));
                break;

            default:
                //le fragment du email
                fragment = new CreateUserWithEmailFragment0();
                //les arguments si il y a
                fragment.setArguments(bundle);
                //fragment0.setArguments(getIntent().getExtras());
                //on rajoute le fragment
                fragmentTransaction.add(R.id.fragment_container, fragment).commit();
                //ft.addToBackStack(null);
                break;
        }
    }

}