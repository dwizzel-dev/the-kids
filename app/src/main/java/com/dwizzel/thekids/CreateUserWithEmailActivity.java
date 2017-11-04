package com.dwizzel.thekids;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.util.Patterns;
import java.util.regex.Pattern;

import com.dwizzel.utils.Auth;
import com.dwizzel.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
/*
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
*/

import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class CreateUserWithEmailActivity extends AppCompatActivity {

    private static final String TAG = "THEKIDS::";
    private String email = "";
    private String psw = "";
    private Integer currFragmentNum;
    private FragmentManager mFragmentManager;
    private ProgressDialog mProgressDialog;
    //private FirebaseAuth mAuth;
    private Auth mAuth;
    private Utils mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_with_email);
        setTitle(R.string.register_with_email);
        //fragment manager pour les anim transition
        mFragmentManager = getSupportFragmentManager();
        //le auth de firebase
        //mAuth = FirebaseAuth.getInstance();
        mAuth = Auth.getInstance();
        //utilitaires de base pour messages et autres
        mUtils = Utils.getInstance();
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
        hideProgressDialog();
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

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void gotoFragmentAndShowErrors(int fragNum, int msgId){
        Bundle bundle = new Bundle();
        bundle.putInt("msg", msgId);
        gotoFragment(fragNum, bundle);
    }

    protected int setEmailFromFragment(String email) {
        //set le email
        if(email.equals("") || !isValidEmail(email)){
            return R.string.email_invalid;
            }
        //le setter du email
        setEmail(email);
        //va au fragment de psw
        gotoFragment(1, null);
        //tout est ok
        return 0;
    }

    protected int setPswFromFragment(String[] psw) {
        Log.w(TAG, String.format("%s : %s", psw[0], psw[1]));
        if(!psw[0].equals(psw[1])){
            return R.string.psw_not_the_same;
        }else if(psw[0].length() < 6){
            return R.string.psw_too_short;
            }
        //le setter du password
        setPsw(psw[0]);
        //on envoi ca et on check pour les erreurs
        createUser();
        //tout est ok
        return 0;
    }

    private void userRegistrationFinished(){
        //on affiche qu'il est logue
        String email = mAuth.getUserInfos().getEmail();
        mUtils.showToastMsg(CreateUserWithEmailActivity.this, email);
        //on va a activity principal
        Intent intent = new Intent(CreateUserWithEmailActivity.this, HomeActivity.class);
        //start activity
        startActivity(intent);
    }

    private void createUser() {
        boolean isConnected = mUtils.checkConnectivity(getApplicationContext());
        //avertir si pas connecte
        if(!isConnected){
            mUtils.showToastMsg(CreateUserWithEmailActivity.this, R.string.err_no_connectivity);
        }else {
            //le progress bar
            showProgressDialog();
            //on va faire un listener sur le resultat
            /*
            mAuth.setOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Log.w(TAG, task.getException());
                }
            });
            */
            mAuth.createUser(CreateUserWithEmailActivity.this, email, psw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task){
                            Log.w(TAG, "CREATE::onComplete[001]");
                            //on hide le loader
                            hideProgressDialog();
                            //handling errors
                            if (!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthUserCollisionException existEmail) {
                                    //email exist
                                    Log.w(TAG, existEmail);
                                    gotoFragmentAndShowErrors(0, R.string.email_in_use);
                                } catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                                    //invalid email
                                    Log.w(TAG, invalidEmail);
                                    gotoFragmentAndShowErrors(0, R.string.email_invalid);
                                } catch (Exception e) {
                                    //whatever else
                                }
                            }else{
                                //pas erreur alors on continue
                                userRegistrationFinished();
                            }
                        }
                    });

            /*
            mAuth.createUserWithEmailPsw(CreateUserWithEmailActivity.this, email, psw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.w(TAG, "createUserWithEmailPsw::onComplete[1]");


                        }
                    });
            */
            /*
            mAuth.createUserWithEmailAndPassword(email, psw)
                    .addOnCompleteListener(CreateUserWithEmailActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String exceptionMsg = "";
                            if (!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthUserCollisionException existEmail) {
                                    //email exist
                                    exceptionMsg = existEmail.getMessage();
                                    gotoFragmentAndShowErrors(0, R.string.email_in_use);
                                } catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                                    //invalid email
                                    exceptionMsg = invalidEmail.getMessage();
                                    gotoFragmentAndShowErrors(0, R.string.email_invalid);
                                } catch (Exception e) {
                                    //whatever else
                                    exceptionMsg = e.getMessage();
                                }

                            }
                            //on hide le loader
                            hideProgressDialog();
                            //on check les exceptions
                            Log.w(TAG, exceptionMsg);
                            //si pas exception alors on signin le user
                            if(exceptionMsg.isEmpty()) {
                                userRegistrationFinished();
                            }

                        }
                    });
            */

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

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.dialog_loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }





}
