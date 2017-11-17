package com.dwizzel.objects;

import com.dwizzel.Const;
import com.dwizzel.models.UserModel;

import java.util.Observable;


/**
 * Created by Dwizzel on 15/11/2017.
 */

public final class UserObject extends Observable{

    private static final String TAG = "UserObject";
    private String email;
    private String uid;
    private boolean active = true;
    private boolean gps = false;
    private PositionObject position = new PositionObject(0.0,0.0,0.0);
    private int type = Const.user.TYPE_EMAIL; //facebook, twitter, email, instagram, etc...

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

    public boolean isActive(){
        return active;
    }

    public int getType(){
        return type;
    }

    public boolean isGps() {
        return gps;
    }

    public PositionObject getPosition(){
        return position;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public void setType(int type){
        this.type = type;
    }

    public void setGps(boolean gps) {
        //alors on notify les observer
        boolean prevGps = this.gps;
        this.gps = gps;
        //si est maintenant a On alors qu'il etait a OFf
        if(!prevGps && gps) {
            setChanged();
            notifyObservers();
        }
    }

    public void setPosition(PositionObject position){
        this.position = position;
    }

    public UserModel toUserModel(){
        UserModel userModel = new UserModel(email, uid);
        userModel.setGps(gps);
        userModel.setActive(active);
        userModel.setPosition(position.getLongitude(), position.getLatitude(),
                    position.getAltitude());
        return userModel;
    }

}