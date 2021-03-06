package com.dwizzel.thekids;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dwizzel.Const;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.objects.UserObject;
import com.dwizzel.observers.BooleanObserver;
import com.dwizzel.services.ITrackerBinderCallback;
import com.dwizzel.services.TrackerService;
import com.dwizzel.utils.Tracer;
import com.dwizzel.utils.Utils;

public class SignInUserWithEmailActivity extends AppCompatActivity {

    private static final String TAG = "SignInUserWithEmail";
    private String email;
    private String psw;
    private BooleanObserver mServiceBoundObservable = new BooleanObserver(false);
    public TrackerService.TrackerBinder mTrackerBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Tracer.log(TAG, "onServiceConnected");
            mTrackerBinder = (TrackerService.TrackerBinder)service;
            mTrackerBinder.registerCallback(mServiceCallback);
            mServiceBoundObservable.set(true);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Tracer.log(TAG, "onServiceDisconnected");
            mServiceBoundObservable.set(false);
            mTrackerBinder = null;
        }
    };

    private void bindToAuthService(){
        if(!mServiceBoundObservable.get()) {
            Intent intent = TrackerService.getIntent(SignInUserWithEmailActivity.this);
            startService(intent);
            //bind to the service, si pas de startService se ferme auto apres la femeture de L'appli
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ITrackerBinderCallback mServiceCallback = new ITrackerBinderCallback() {
        private static final String TAG = "SignInUserWithEmailActivity.ITrackerBinder";
        public void handleResponse(ServiceResponseObject sro){
            Tracer.log(TAG, "handleResponse", sro);
        }
        public void onSignedIn(ServiceResponseObject sro){
            Tracer.log(TAG, "onSignedIn");
            //on enleve le loader
            if(sro.getErr() != Const.error.NO_ERROR){
                //ppour aficher les erreurs sinon il continue au created
                showSpinner(false);
            }
            switch(sro.getErr()){
                case Const.error.NO_ERROR:
                    break;
                case Const.except.NO_CONNECTION:
                    Utils.getInstance().showToastMsg(SignInUserWithEmailActivity.this,
                            R.string.err_no_connectivity);
                    break;
                case Const.error.ERROR_INVALID_PASSWORD:
                    displayErrMsg(R.string.err_invalid_password);
                    break;
                case Const.error.ERROR_INVALID_CREDENTIALS:
                    displayErrMsg(R.string.err_invalid_credential);
                    break;
                default:
                    break;
            }
        }
        public void onSignedOut(ServiceResponseObject sro){
            Tracer.log(TAG, "onSignedOut");
        }
        public void onCreated(ServiceResponseObject sro){
            Tracer.log(TAG, "onCreated");
            //tout est beau on peut starter
            switch(sro.getErr()) {
                case Const.error.NO_ERROR:
                    userIsCreated();
                    break;
                default:
                    break;
            }
        }
    };

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        Tracer.log(TAG, "onDestroy");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindToAuthService();
        setContentView(R.layout.activity_sign_in_user_with_email);
        setTitle(R.string.signin_with_email_title);
        //butt create
        final Button buttSignIn = findViewById(R.id.buttSignIn);
        buttSignIn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        //get the email entered
                        String email = String.format("%s",((EditText)findViewById(R.id.userEmail)).getText());
                        String[] psw = new String[1];
                        psw[0] = String.format("%s",((EditText)findViewById(R.id.userPsw)).getText());
                        //problem set an error message
                        displayErrMsg(setEmailAndPsw(email, psw));

                    }
                });
    }

    private int setEmailAndPsw(String email, String[] psw){
        //check le email
        int err = Utils.getInstance().isValidEmail(email);
        if(err != 0){
            return err;
        }
        //le setter du email
        setEmail(email);
        //on check le psw
        err = Utils.getInstance().isValidPsw(psw);
        if(err != 0){
            return err;
        }
        //le setter du password
        setPsw(psw[0]);
        //pas d'erreur alors on va faire le sign in avec firebase
        signInUser();
        return 0;
    }

    private void displayErrMsg(int msgId){
        TextView txtView = findViewById(R.id.errMsg);
        if(msgId != 0) {
            txtView.setText(msgId);
        }else {
            txtView.setText("");
        }
    }

    private void userIsCreated(){
        //on affiche qu'il est logue
        try {
            Utils.getInstance().showToastMsg(
                    SignInUserWithEmailActivity.this,
                    getResources().getString(R.string.toast_connected_as_and_last,
                            UserObject.getInstance().getEmail(),
                            UserObject.getInstance().getLastConnection(SignInUserWithEmailActivity.this)));
            //on va a activity principal
            Intent intent = new Intent(SignInUserWithEmailActivity.this,
                    HomeActivity.class);
            //start activity and clear the backStack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }catch (NullPointerException npe){
            Tracer.log(TAG, "userIsCreated.NullPointerException: " , npe);
        }catch (Exception e){
            Tracer.log(TAG, "userIsCreated.Exception: " , e);
        }
    }

    private void signInUser() {
        //on va faire un listener sur le resultat
        if (mTrackerBinder != null) {
            //on met un loader
            showSpinner(true);
            //on call le service
            mTrackerBinder.signIn(email, psw);
        }
    }

    private void showSpinner(boolean show){
        //le bouton et le spinner
        ProgressBar progressBar = findViewById(R.id.loading_spinner);
        Button buttSignIn = findViewById(R.id.buttSignIn);
        if(show){
            progressBar.setVisibility(View.VISIBLE);
            buttSignIn.setVisibility(View.INVISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            buttSignIn.setVisibility(View.VISIBLE);
        }
    }

}