package com.dwizzel.services;

import com.google.firebase.auth.AuthCredential;

/**
 * Created by Dwizzel on 19/11/2017.
 */

public interface IAuthService {

    boolean isSignedIn();
    void signOut();
    String getEmail();
    String getUserID();
    void signInUser(String email, String psw);
    void createUser(String email, String psw);
    void signInUser(AuthCredential token);

}