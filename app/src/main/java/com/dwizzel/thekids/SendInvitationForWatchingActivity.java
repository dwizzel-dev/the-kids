package com.dwizzel.thekids;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.Manifest;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.Gravity;

import android.telephony.SmsManager;

import com.dwizzel.Const;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.services.ITrackerBinderCallback;
import com.dwizzel.services.TrackerService;
import com.dwizzel.utils.Tracer;
import com.dwizzel.utils.Utils;

/*
* NOTES:
* http://stackandroid.com/tutorial/contact-picker-using-intent-android-tutorial/
* http://stackandroid.com/tutorial/android-reading-phone-contacts-example/
* */


public class SendInvitationForWatchingActivity extends BaseActivity {

    private static final String TAG = "SendInvitationForWatchingActivity";
    private TrackerService.TrackerBinder mTrackerBinder;
    private static final int PERMISSION_REQUEST_SEND_SMS = 28400;
    private Integer currFragmentNum;
    private boolean isActivityCreated = false;
    private FragmentManager mFragmentManager;

    private String mPhone = "";
    private String mName = "";
    private String mEmail = "";
    private String mMessage = "";
    private String mInviteId;

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
            //set un nouveau callback au lieu de celui de BaseActivity
            //vu qu'il va recevoir une notif quand aura ca liste de Watchers et de Invites
            setTrackerBinderCallback();
            //fragment manager pour les anim transition
            mFragmentManager = getSupportFragmentManager();
            //on va setter le premier fragment du email
            if (findViewById(R.id.fragment_container) != null) {
                gotoFragment(0, null);
            }
        }
        isActivityCreated = true;
    }

    private void setTrackerBinderCallback(){
        //on peut le caller sinon l'activity ne serait meme pas partis
        //on overwrite celui de BaseActivity
        ITrackerBinderCallback serviceCallback = new ITrackerBinderCallback() {
            private static final String TAG = "SendInvitationForWatchingActivity.ITrackerBinder";
            public void handleResponse(ServiceResponseObject sro){
                Tracer.log(TAG, "handleResponse: " + sro);
                if(sro.getErr() == 0){
                    Tracer.log(TAG, "handleResponse: " + sro.getMsg());
                    switch(sro.getMsg()){
                        case Const.response.ON_INVITE_ID_CREATED:
                            mInviteId = sro.getArg();
                            //on a le inviteId genere par le serveur
                            createBundleAndGotoFragment(Const.error.NO_ERROR);
                            break;
                        case Const.response.ON_INVITATION_CREATED:
                            //on a l'invitation dans la DB alors on retourne a la liste des watchers
                            gotoWatchersList();
                            break;
                        default:
                            break;
                    }
                }else if(sro.getErr() == Const.conn.NOT_CONNECTED){
                    Tracer.log(TAG, "handleResponse: NOT_CONNECTED");
                }else if(sro.getErr() == Const.conn.RECONNECTED){
                    Tracer.log(TAG, "handleResponse: RECONNECTED");
                }
            }
            public void onSignedIn(ServiceResponseObject sro){}
            public void onSignedOut(ServiceResponseObject sro){}
            public void onCreated(ServiceResponseObject sro){}
        };
        //get le binder
        mTrackerBinder = getTrackerBinder();
        //on enleve le precedenet callback de BaseActivity
        mTrackerBinder.unregisterCallback();
        //set le nouveau callback qui overwrite celui de BaseActivity
        mTrackerBinder.registerCallback(serviceCallback);
    }

    protected void createInviteId(String phone, String name, String message) {
        Tracer.log(TAG, "createInviteId");
        mPhone = phone;
        mName = name;
        mMessage = message;
        //on call le service pour qu'il fassse la demande de numero d'invitation
        if(mInviteId != null) {
            //on en a deja un alors on utilise le meme au lieu d'en creer un
            createBundleAndGotoFragment(Const.error.NO_ERROR);
        }else{
            mTrackerBinder.createInviteId();
        }
    }

    protected void gotoWatchersList(){
        Tracer.log(TAG, "gotoWatchersList");
        //on fait un toat pour dire que c'est ok
        Utils.getInstance().showToastMsg(SendInvitationForWatchingActivity.this, R.string.toast_sms_sent);
        //start activity
        Intent intent = new Intent(SendInvitationForWatchingActivity.this,
                WatchOverMeActivity.class);
        //start activity and clear the backStack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void createBundleAndGotoFragment(int err){
        Tracer.log(TAG, "createBundleAndGotoFragment: " + err);
        mMessage = getResources().getString(R.string.sms_invitation_message,
                mMessage, mInviteId);
        //on enleve le loader et on change de fragment pour l'envoi final
        Bundle bundle = new Bundle();
        bundle.putString("phone", mPhone);
        bundle.putString("message", mMessage);
        //si on a une erreur la rajouter
        if(err != Const.error.NO_ERROR){
            bundle.putInt("msg", R.string.err_sms_not_sent);
        }

        gotoFragment(2, bundle);
    }

    protected void sendSMSMessage() {
        Tracer.log(TAG, "sendSMSMessage");
        if (ContextCompat.checkSelfPermission(
                SendInvitationForWatchingActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Tracer.log(TAG, "sendSMSMessage.checkSelfPermission: FAILED");
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    SendInvitationForWatchingActivity.this,
                    Manifest.permission.SEND_SMS)) {
                Tracer.log(TAG, "sendSMSMessage.shouldShowRequestPermissionRationale");
            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        PERMISSION_REQUEST_SEND_SMS);
                Tracer.log(TAG, "sendSMSMessage.requestPermissions");
            }
        }else{
            Tracer.log(TAG, "sendSMSMessage.checkSelfPermission: OK");
            //send the sms
            sendSMSMessageToInvites();
        }
    }

    private void sendSMSMessageToInvites(){
        Tracer.log(TAG, "sendSMSMessageToInvites");
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mPhone, null, mMessage, null, null);
            createInvitation(Const.error.NO_ERROR);
        }catch(Exception e){
            Tracer.log(TAG, "sendSMSMessageToInvites.Exception: ", e);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Tracer.log(TAG, "onRequestPermissionsResult");
        switch (requestCode){
            case PERMISSION_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMSMessageToInvites();
                } else {
                    Utils.getInstance().showToastMsg(
                            SendInvitationForWatchingActivity.this, R.string.toast_sms_not_sent);
                    //on affiche l'erreur et enleve le loader
                    createInvitation(Const.error.ERROR_SMS_NOT_SENT);
                }
            }
        }
    }

    protected void createInvitation(int err){
        Tracer.log(TAG, "createInvitation: " + err);
        //Utils.getInstance().showToastMsg(SendInvitationForWatchingActivity.this, R.string.toast_sms_sent);
        // et on revient a la liste avec le pending ajoute
        switch(err){
            case Const.error.NO_ERROR:
                //on creer l'invitation du user dans la DB
                //le retour du service fera le reste
                mTrackerBinder.createInvitation(mInviteId, mName, mPhone, mEmail);
                break;
            case Const.error.ERROR_SMS_NOT_SENT:
                //on set le message et on dit que l'on a eu un probleme
                createBundleAndGotoFragment(err);
                break;
            default:
                break;

        }



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

            case 2:
                fragment = new SendInvitationForWatchingFragment2();
                fragment.setArguments(bundle);
                TransitionSet transitionSet2 = new TransitionSet()
                        .addTransition(new Slide(Gravity.END))
                        .addTransition(new Fade(Fade.IN));
                //la transition
                fragment.setEnterTransition(transitionSet2);
                //on rajoute le fragment
                fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
                fragmentTransaction.addToBackStack(String.format(
                        Utils.getInstance().getLocale(SendInvitationForWatchingActivity.this),
                        "fragment%d", fragNum));
                //TODO: peut etre un back stack de moins
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
