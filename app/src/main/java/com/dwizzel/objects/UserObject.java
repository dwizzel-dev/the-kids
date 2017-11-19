package com.dwizzel.objects;

import com.dwizzel.Const;
import com.dwizzel.models.UserModel;
import com.dwizzel.utils.Tracer;


/**
 * Created by Dwizzel on 15/11/2017.
 */

public class UserObject{

    private static final String TAG = "UserObject";
    private static UserObject sInst;
    private static int sRefCount = 0;
    private String email = "";
    private String uid = "";
    private boolean created = false;
    private boolean signed = false;
    private boolean active = false;
    private boolean gps = false;
    private Object data = "";
    private PositionObject position = new PositionObject(0.0,0.0,0.0);
    private int loginType = 0; //facebook, twitter, email, instagram, etc...

    private UserObject(){
        //default
    }

    public static UserObject getInstance(){
        Tracer.log(TAG, "getInstance: " + (sRefCount++));
        if(sInst == null){
            sInst = new UserObject();
        }
        return  sInst;
    }

    public void resetUser(){
        email = "";
        uid = "";
        active = false;
        gps = false;
        created = false;
        signed = false;
        data = null;
        position = new PositionObject(0.0,0.0,0.0);
        loginType = 0;
    }

    public String getEmail(){
        return email;
    }

    public String getUserId(){
        return uid;
    }

    public Object getData(){
        return data;
    }

    public boolean isActive(){
        return active;
    }

    public int getLoginType(){
        return loginType;
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

    public void setLoginType(int type){
        this.loginType = type;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setData(Object data){
        this.data = data;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getUid(){
        return uid;
    }

    public boolean isSigned(){
        return signed;
    }

    public void setSigned(boolean signed){
        this.signed = signed;
    }

    public boolean isCreated(){
        return created;
    }

    public void setCreated(boolean created){
        this.created = created;
    }

    public void setGps(boolean gps) {
        this.gps = gps;
    }

    public void setPosition(PositionObject position){
        this.position = position;
    }

    public UserModel toUserModel(){
        UserModel userModel = new UserModel(email, uid);
        userModel.setGps(gps);
        userModel.setActive(active);
        userModel.setLoginType(loginType);
        userModel.setPosition(position.getLongitude(), position.getLatitude(),
                    position.getAltitude());
        return userModel;
    }

    //-----------------------------------------------------------------------------------------

    public class Obj {

        private int type;
        private Object value;

        public Obj(int type, Object value){
            this.type = type;
            this.value = value;
        }

        public int getType(){
            return type;
        }

        public Object getValue(){
            return value;
        }

    }

}