package com.dwizzel.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dwizzel.Const;
import com.dwizzel.objects.PositionObject;
import com.dwizzel.objects.UserObject;
import com.dwizzel.utils.Tracer;
import com.dwizzel.utils.Utils;
import com.google.firebase.auth.AuthCredential;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;

/**
 * Created by Dwizzel on 10/11/2017.
 * https://developer.android.com/guide/components/services.html
 * https://github.com/AltBeacon/android-beacon-library/issues/304
 * https://android.googlesource.com/platform/frameworks/base/+/483f3b06ea84440a082e21b68ec2c2e54046f5a6/services/java/com/android/server/am/ActivityManagerService.java
 * http://mylifewithandroid.blogspot.ca/2008/01/about-binders.html
 * https://developer.android.com/guide/components/aidl.html#CreateAidl
 * https://developer.android.com/guide/components/bound-services.html#Messenger
 * https://developer.android.com/reference/android/app/Service.html
 *
 *
 * NOTES: on va le faire en IBinder sans le AIDL, car pas besoin d'IPC, et j'aimerais mieux partager
 * des objets plus complexes, mais bon on l'aura pratique haha!
 *
 * NOTES: vu que le service peut repartir sans l'application on va chercher les infos de base et on set
 * les infos du user dans le service
 */

public class TrackerService extends Service{

    private final static String TAG = "TrackerService";

    //timer
    private HandlerThread mThTimer;
    private Handler mHandlerTimer;
    private Runnable mRunnableTimer;
    private long mTimer = 0;

    private ITrackerBinderCallback mBinderCallback;
    private AuthService mAuthService;
    private FirestoreService mFirestoreService;
    private GpsService mGpsService;
    private UserObject mUser;
    private final IBinder mTrackerBinder = new TrackerBinder();

    @NonNull
    public static Intent getIntent(Context context) {
        Tracer.log(TAG, "getIntent");
        return new Intent(context, TrackerService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Tracer.log(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Tracer.log(TAG, "onBind:" + intent);
        return mTrackerBinder;
    }

    @Override
    public void onDestroy(){
        Tracer.log(TAG, "onDestroy");
        super.onDestroy();
        //clea le timer
        if(mHandlerTimer != null){
            mHandlerTimer.removeCallbacks(mRunnableTimer);
            mHandlerTimer = null;
        }
        if(mThTimer != null){
            mThTimer.quitSafely();
            mThTimer = null;
        }
    }

    @Override
    public void onCreate(){
        Tracer.log(TAG, "onCreate");
        super.onCreate();
        try {
            mUser = UserObject.getInstance();
            mAuthService = new AuthService(TrackerService.this, mTrackerBinder);
            mFirestoreService = FirestoreService.getInstance();
            mGpsService = new GpsService(TrackerService.this, mTrackerBinder);
            //start running time elapsed
            startRunningTime();
            //check pour user infos si etait deja connecte avant le restart du tracker service
            setUser();
        }catch(Exception e){
            Tracer.log(TAG, "onCreate.exception", e);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Tracer.log(TAG, "onUnbind:" + intent);
        //si clear le callback
        try {
            ((TrackerService.TrackerBinder) mTrackerBinder).unregisterCallback();
        }catch (Exception e){
            Tracer.log(TAG, "onUnbind.Exception: ", e);
        }
        //si a TRUE alors il va faire un rebind au lieu d'un bind
        //donc il va reprendre ou il etait
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Tracer.log(TAG, "onRebind: " + intent);
    }

    private String getTimer(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Utils.getInstance().getLocale(TrackerService.this));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(new Date(mTimer*1000));
    }

    private void startRunningTime() {
        Tracer.log(TAG, "startRunningTime");
        if(mThTimer == null) {
            try {
                mThTimer = new HandlerThread("mThTimer");
                mThTimer.start();
                mHandlerTimer = new TimerHandler(mThTimer.getLooper());
                mRunnableTimer = new TimerRunnable();
                mHandlerTimer.post(mRunnableTimer);
            }catch (Exception e){
                Tracer.log(TAG, "startTimer.Exception: ", e);
            }
        }
    }

    private void getUserInfos(){
        Tracer.log(TAG, "getUserInfos");
        try {
           mFirestoreService.getUserInfos();
        }catch (Exception e){
            Tracer.log(TAG, "getUserInfos.exception: ", e);
        }
    }

    private void activateUser(){
        Tracer.log(TAG, "activateUser");
        try {
            mUser.setActive(true);
            mFirestoreService.activateUser();
        }catch (Exception e){
            Tracer.log(TAG, "activateUser.exception: ", e);
        }
    }

    private void deactivateUser(){
        Tracer.log(TAG, "deactivateUser");
        try {
            mUser.setActive(false);
            mUser.setGps(false);
            mFirestoreService.activateUser();
        }catch (Exception e){
            Tracer.log(TAG, "deactivateUser.exception: ", e);
        }
    }

    private void setUser(){
        Tracer.log(TAG, "setUser");
        //on va checker si est deja logue et on set les infos ou pas du UserModel
        //car peu etre un Restart de l'app ou comme un new signIn
        if(mAuthService.isSignedIn()){
            //on creer le user de base
            mUser.setEmail(mAuthService.getEmail());
            mUser.setUid(mAuthService.getUserID());
            mUser.setSigned(true);
            mUser.setActive(true);
            //on check le type de authentification
            //mUser.setLoginType(Const.user.TYPE_EMAIL);
            //on enleve les observer precedent si il etait logue IN, OUT, IN, OUT etc...
            mUser.deleteObservers();
            //observer pour quand on change quelque chose au user
            mUser.addObserver(new Observer() {
                @Override
                public void update(Observable observable, Object o) {
                    Tracer.log(TAG, "setUser.mUser.update: \n" + observable + "\n" + o);
                    try{
                        switch(((UserObject.Obj)o).getType()){
                            case Const.notif.TYPE_NOTIF_CREATED :
                                //il est cree ou ete cree dans la DB alors on update les infos
                                if((boolean)((UserObject.Obj)o).getValue()){
                                    //on peut maintenant setter le gps
                                    switch(mGpsService.checkGpsStatus()){
                                        case Const.gps.NO_PERMISSION :
                                            Tracer.log(TAG, "NO GPS PERMISSIONS ++++");
                                            break;
                                        case Const.gps.NO_PROVIDER :
                                            Tracer.log(TAG, "NO GPS PROVIDER ++++");
                                            break;
                                        default:
                                            //on check la derniere postion si possible
                                            PositionObject positionObject = mGpsService.getLastPosition();
                                            if(positionObject != null){
                                                mUser.setPosition(positionObject);
                                            }
                                            mUser.setGps(true);
                                            break;
                                    }
                                    //on update les infos dans la DB
                                    mFirestoreService.updateUserInfos();
                                }
                                break;
                            default:
                                break;
                        }
                    }catch (NullPointerException npe){
                        Tracer.log(TAG, "setUser.mUser.update.NullPointerException: ", npe);
                    }
                }
            });
            //on va chercher les infos du user sur la DB ou on les creer
            getUserInfos();
        }
    }

    private void setUserNewPosition(){
        Tracer.log(TAG, "setUserNewPosition");
        //set le user
        mUser.setPosition(mGpsService.getPosition());
        //set la db
        //NOTE: si on veut ca live il faut activer le mGpsService.startLocationUpdate() pour avoir
        // les infos en direct a haque mouvement,
        // si on veut ca live dans la DB,
        // il faut activer la ligne ci-dessous
        // sinon il va se faire au updateUserInfo du keepAlive
        //mFirestoreService.updateUserPosition();
    }

    private void resetUser(){
        Tracer.log(TAG, "resetUser");
        // NOTE: est appele quand on fait un signOut
        // on enleve de la table actif de la DB avant
        deactivateUser();
        //on stop le gps
        mGpsService.stopLocationUpdate();
        // firebaseAuthentification
        mAuthService.signOut();
        //on reset les infos
        mUser.resetUser();
        //on call l'activity appelante pour le signOut
        if(mBinderCallback != null) {
            mBinderCallback.onSignedOut(null);
        }
        //NOTE: le OnUnbind va s'occuper de stopper le thread et clearer le mBinderCallback
    }

    private void keepActive(){
        Tracer.log(TAG, "keepActive");
        if(mAuthService.isSignedIn()){
            //on va checke les permissions du gps si etait OFF, peut-etre maintenant il est ON
            //car quand on enleve des permissions il restart, mais si on les redonne il ne fait rien
            if(!mUser.isGps() && mGpsService.checkGpsStatus() == Const.gps.NO_ERROR){
                //NOTE: on va juste setter la derniere position
                //pour avoir le tracking live il faudrait activer le mGpsService.startLocationUpdate()
                PositionObject positionObject = mGpsService.getLastPosition();
                if(positionObject != null){
                    mUser.setPosition(positionObject);
                }
                mUser.setGps(true);
            }
            //update les infos de l'usager
            try {
                mFirestoreService.updateUserInfos();
            }catch (Exception e){
                Tracer.log(TAG, "activateUser.exception: ", e);
            }
        }

    }


    //--------------------------------------------------------------------------------------------
    //NESTED CLASS

    public class TrackerBinder extends Binder implements ITrackerBinder {
        public long getCounter() {
            Tracer.log(TAG, "TrackerBinder.getCounter");
            return mTimer;
        }
        public void registerCallback(ITrackerBinderCallback callback){
            Tracer.log(TAG, "TrackerBinder.registerCallback");
            mBinderCallback = callback;
            //si il fait un rebind() en faisant un back dans l'application
            //il ne passera pas par unbind() alors il faut stoper le thread precedent
            //avant de le repartir, sinon il y aura autant de thread que de call
            //a registerCallback() sans unregisterCallback()
            //tant que le callstack n'est pas cleare quand il passe d'une activity a l'autre
        }
        public void unregisterCallback(){
            Tracer.log(TAG, "TrackerBinder.unregisterCallback");
            mBinderCallback = null;
        }
        public boolean isSignedIn(){
            Tracer.log(TAG, "TrackerBinder.isSignedIn");
            return mAuthService.isSignedIn();
        }
        public void signOut(){
            Tracer.log(TAG,"TrackerBinder.signOut");
            //on reset le user
            resetUser();
        }
        public void signIn(String email, String psw){
            Tracer.log(TAG, "TrackerBinder.signIn[email]");
            mAuthService.signInUser(email, psw);
        }
        public void signIn(AuthCredential authCredential){
            Tracer.log(TAG, "TrackerBinder.signIn[credentials]");
            mAuthService.signInUser(authCredential);
        }
        public void createUser(String email, String psw){
            Tracer.log(TAG, "TrackerBinder.createUser");
            mAuthService.createUser(email, psw);
        }
        public void onSignedIn(Object obj){
            Tracer.log(TAG, "TrackerBinder.onSignedIn");
            //on set le user de base du service
            setUser();
            //on tranmet la reponse object au caller
            mBinderCallback.onSignedIn(obj);
        }
        public void onSignedOut(Object obj){
            Tracer.log(TAG, "TrackerBinder.onSignedOut");
            //on tranmet la reponse object au caller
            mBinderCallback.onSignedOut(obj);
        }
        public void onGpsPositionUpdate(){
            Tracer.log(TAG, "TrackerBinder.onGpsPositionUpdate");
            //on fait un update du user et celui de la DB
            setUserNewPosition();
        }

    }

    //--------------------------------------------------------------------------------------------
    //NESTED CLASS

    class TimerRunnable implements Runnable{
        private final static String TAG = "TimerRunnable";
        private boolean loop = true;
        private int keepAliveDelay = 60;
        private int sleepDelay = 1000;
        TimerRunnable(){}
        @Override
        public void run() {
            try {
                while (loop) {
                    Thread.sleep(sleepDelay);
                    mTimer++;
                    if (mTimer % keepAliveDelay == 0) {
                        keepActive();
                    }
                    Tracer.tog("run: ", getTimer());
                }
            } catch (InterruptedException ie) {
                Tracer.log(TAG, "run.InterruptedException");
            } catch (Exception e) {
                Tracer.log(TAG, "run.Exception: ", e);
            }
        }
    }

    //--------------------------------------------------------------------------------------------
    //NESTED CLASS

    class TimerHandler extends Handler {
        private final static String TAG = "TimerHandler";
        TimerHandler(Looper looper) {super(looper);}
        @Override
        public void handleMessage(Message msg) {
            Tracer.log(TAG, "handleMessage: " + msg);
        }
    }
}
