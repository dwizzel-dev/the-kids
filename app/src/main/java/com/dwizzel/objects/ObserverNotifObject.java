package com.dwizzel.objects;

/**
 * Created by Dwizzel on 15/11/2017.
 */

//pour les retour du service
public final class ObserverNotifObject{

    private int prop;
    private Object value;
    private String msg = "";

    public ObserverNotifObject(int property, Object value) {
        this.prop = property;
        this.value = value;
    }

    public ObserverNotifObject(int property, Object value, String msg) {
        this.prop = property;
        this.value = value;
        this.msg = msg;
    }

    public Object getValue(){
        return value;
    }

    public int getProp(){
        return prop;
    }

    public String getMsg(){
        return msg;
    }

}

