package com.dwizzel.objects;

/**
 * Created by Dwizzel on 15/11/2017.
 */

//pour les retour du service
public final class ObserverNotifObject{

    private int type;
    private Object value;
    private String msg = "";

    ObserverNotifObject(int type, Object value) {
        this.type = type;
        this.value = value;
    }

    ObserverNotifObject(int type, Object value, String msg) {
        this.type = type;
        this.value = value;
        this.msg = msg;
    }

    public Object getValue(){
        return value;
    }

    public int getType(){
        return type;
    }

    public String getMsg(){
        return msg;
    }

}

