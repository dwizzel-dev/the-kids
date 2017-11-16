package com.dwizzel.objects;

import com.dwizzel.Const;
import com.dwizzel.models.PositionModel;
import com.dwizzel.models.UserModel;
import com.dwizzel.utils.Tracer;

import java.util.Observable;

/**
 * Created by Dwizzel on 15/11/2017.
 */

public final class UserObject extends Observable{

    private static final String TAG = "UserObject";
    private String email;
    private String uid;
    private boolean active;
    private PositionModel position = new PositionModel(0.00,0.00,0.00);
    private int type = Const.user.TYPE_EMAIL;

    public UserObject(String username, String uid) {
        this.email = username;
        this.uid = uid;
    }

    public UserObject(String username, String uid, int type) {
        this.email = username;
        this.uid = uid;
        this.type = type;
    }

    public String getEmail(){
        return email;
    }

    public String getUserId(){
        return uid;
    }

    public boolean getActive(){
        return active;
    }

    public int getType(){
        return type;
    }

    public PositionModel getPosition(){
        return position;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public void setType(int type){
        this.type = type;
    }

    public void setPosition(PositionModel position){
        this.position = position;
    }

    public UserModel toUserModel(){
        UserModel userModel = new UserModel(email, uid);
        userModel.setActive(active);
        userModel.setPosition(position.getLongitude(), position.getLatitude(),
                    position.getAltitude());
        return userModel;
    }

}