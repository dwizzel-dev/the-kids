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
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.objects.UserObject;
import com.dwizzel.utils.Tracer;
import com.dwizzel.utils.Utils;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
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
 *
 * TODO: il faut un triger dans la DB pour faire le cleanup de ceux qui ne ce sont pas deconnecte
 * normalement, genre appli force stop, une pile morte, plus de reseaux, etc...
 */

public class TrackerService extends Service implements ITrackerService{

    private final static String TAG = "TrackerService";

    private ITrackerBinderCallback mBinderCallback;
    private IAuthService mAuthService;
    private IDatabaseService mDatabaseService;
    private IGpsService mGpsService;
    private UserObject mUser;
    private final IBinder mTrackerBinder = new TrackerBinder();
    //timer
    private HandlerThread mThTimer;
    private Handler mHandlerTimer;
    private Runnable mRunnableTimer;
    private long mTimer = 0;
    //internet
    private boolean mHasConnectivity;

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
            mHasConnectivity = Utils.getInstance().checkConnectivity(TrackerService.this);
            mUser = UserObject.getInstance();
            mAuthService = new AuthService(TrackerService.this, TrackerService.this);
            mDatabaseService = new DatabaseService(TrackerService.this);
            mGpsService = new GpsService(TrackerService.this, TrackerService.this);
            //start running keep alive timer
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss",
                Utils.getInstance().getLocale(TrackerService.this));
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

    private void changeUserStatus(int status){
        Tracer.log(TAG, "changeUserStatus: " + status);
        try {
            mUser.setStatus(status);
            mDatabaseService.activateUser();
        }catch (Exception e){
            Tracer.log(TAG, "activateUser.exception: ", e);
        }
    }

    private void setUser(){
        Tracer.log(TAG, "setUser");
        //on va checker si est deja logue et on set les infos ou pas
        //car peu etre un Restart de l'app ou comme un new signIn
        if(mAuthService.isSignedIn()){
            //on creer le user de base
            mUser.setEmail(mAuthService.getEmail());
            mUser.setUid(mAuthService.getUserID());
            mUser.setSigned(true);
            mUser.setActive(true);
            mUser.setStatus(Const.status.ONLINE);
            //on va chercher les infos du user sur la DB ou on les creer
            mDatabaseService.getUserInfos();
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
        mUser.setActive(false);
        mUser.setGps(false);
        mDatabaseService.deactivateUser();
        //on stop le gps
        mGpsService.stopLocationUpdate();

        /*
        //le probleme avec firestore c'est qu'il y a va par batch alors ce n'est pas vraiment live
        //si on fait un signout du mauth alors on a un probleme car plus de droit de changer le status
        //on va trigger le firestore pour appeler onUserSignout pour essayer
        // firebaseAuthentification
        mAuthService.signOut();
        //on reset les infos
        mUser.resetUser();
        //on call l'activity appelante pour le signOut
        if(mBinderCallback != null) {
            mBinderCallback.onSignedOut(null);
        }
        */

        //NOTE: le OnUnbind va s'occuper de stopper le thread et clearer le mBinderCallback
    }

    private void keepActive(){
        Tracer.log(TAG, "keepActive");
        if(mAuthService.isSignedIn()){
            //on va checke les permissions du gps si etait OFF, peut-etre maintenant il est ON
            //car quand on enleve des permissions il restart, mais si on les redonne il ne fait rien
            if(!mUser.isGps() && mGpsService.checkGpsStatus() == Const.error.NO_ERROR){
                //NOTE: on va juste setter la derniere position
                //pour avoir le tracking live il faudrait activer le mGpsService.startLocationUpdate()
                GeoPoint position = mGpsService.getLastPosition();
                if(position != null){
                    mUser.setPosition(position);
                }
                mUser.setGps(true);
            }
            //update les infos de l'usager
            try {
                mDatabaseService.activateUser();
            }catch (Exception e){
                Tracer.log(TAG, "activateUser.exception: ", e);
            }
        }
        //for debug check user object
        Tracer.log(TAG, "User: ", mUser);
    }

    public void onUserSignedIn(ServiceResponseObject sro){
        Tracer.log(TAG, "onUserSignedIn");
        //on set le user de base du service si on a pas d'erreur sinon on les refill au caller
        if(sro.getErr() == 0) {
            setUser();
            }
        //on tranmet la reponse object au caller
        if(mBinderCallback != null) {
            mBinderCallback.onSignedIn(sro);
        }

    }

    public void onUserCreated(ServiceResponseObject sro){
        Tracer.log(TAG, "onUserCreated");
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
                GeoPoint position = mGpsService.getLastPosition();
                if(position != null){
                    mUser.setPosition(position);
                }
                mUser.setGps(true);
                break;
        }
        //on a les infos de firestore et on a crer les user
        //on peut maintenant ouvrir la appz
        //on tranmet la reponse au activy qui a caller si il y a
        if(mBinderCallback != null) {
            mBinderCallback.onCreated(sro);
        }
        //on update les infos dans la DB et on met active en meme temps avec un batch write
        mDatabaseService.updateUserInfos();

    }

    public void onUserWatchersList(ServiceResponseObject sro){
        Tracer.log(TAG, "onUserWatchersList");
        //on tranmet la reponse au activy qui a caller si il y a
        if(mBinderCallback != null) {
            mBinderCallback.handleResponse(sro);
        }
    }

    public void onUserInvitationsList(ServiceResponseObject sro){
        Tracer.log(TAG, "onUserInvitationsList");
        //on tranmet la reponse au activy qui a caller si il y a
        if(mBinderCallback != null) {
            mBinderCallback.handleResponse(sro);
        }
    }

    public void onUserSignedOut(ServiceResponseObject sro){
        Tracer.log(TAG, "onUserSignedOut");
        //NOTE: est appele par databaseService quand le deactivate est fini
        // firebaseAuthentification
        mAuthService.signOut();
        //on reset les infos
        mUser.resetUser();
        //on call l'activity appelante pour le signOut
        if(mBinderCallback != null) {
            mBinderCallback.onSignedOut(sro);
        }
    }

    public void onGpsPositionUpdate(){
        Tracer.log(TAG, "onGpsPositionUpdate");
        //on fait un update du user et celui de la DB
        setUserNewPosition();
    }

    private void onConnectivityChange(ServiceResponseObject sro){
        Tracer.log(TAG, "onConnectivityChange");
        //on call Interface si jamais elle a besoin de connectivity elle le gerera
        //on call l'activity appelante pour le signOut
        if(mBinderCallback != null) {
            mBinderCallback.handleResponse(sro);
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
            //on reset le user et le service s'occupe du reste
            // pour faire le callback quand c'est termine
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
        public void getWatchersList(){
            Tracer.log(TAG, "TrackerBinder.getWatchersList");
            //on cherche la liste de nos watchers si la notre est a null
            if(mUser.getWatchers() == null) {
                mDatabaseService.getWatchersList();
            }else{
                //sinon il l'a deja alors on repond tout de suite
                if(mBinderCallback != null) {
                    mBinderCallback.handleResponse(
                            new ServiceResponseObject(Const.response.ON_WATCHERS_LIST));
                }
            }
        }
        public void getInvitationsList(){
            Tracer.log(TAG, "TrackerBinder.getInvitationsList");
            //on cherche la liste de nos watchers si la notre est a null
            if(mUser.getInvitations() == null) {
                mDatabaseService.getInvitationsList();
            }else{
                //sinon il l'a deja alors on repond tout de suite
                if(mBinderCallback != null) {
                    mBinderCallback.handleResponse(
                            new ServiceResponseObject(Const.response.ON_INVITES_LIST));
                }
            }
        }

    }

    //--------------------------------------------------------------------------------------------
    //NESTED CLASS

    class TimerRunnable implements Runnable{
        private final static String TAG = "TimerRunnable";
        private boolean loop = true;
        private int keepAliveDelay = 6000; //10 minutes
        private int checkConnectivityDelay = 30; //30 secondes
        private int sleepDelay = 1000;
        TimerRunnable(){}
        @Override
        public void run() {
            try {
                while (loop) {
                    Thread.sleep(sleepDelay);
                    mTimer++;
                    //keep alive on the server
                    if (mTimer % keepAliveDelay == 0) {
                        keepActive();
                    }
                    //check connectivity
                    if (mTimer % checkConnectivityDelay == 0) {
                        boolean conn = Utils.getInstance().checkConnectivity(TrackerService.this);
                        if (mHasConnectivity && !conn){
                            onConnectivityChange(new ServiceResponseObject(Const.conn.NOT_CONNECTED));
                        }else if(!mHasConnectivity && conn){
                            onConnectivityChange(new ServiceResponseObject(Const.conn.RECONNECTED));
                        }
                        mHasConnectivity = conn;
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
