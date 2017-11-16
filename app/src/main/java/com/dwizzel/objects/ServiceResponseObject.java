package com.dwizzel.objects;

/**
 * Created by Dwizzel on 15/11/2017.
 */

//pour les retour du service
public final class ServiceResponseObject{

    private Object args;
    private int err = 0;
    private String msg = "";

    public ServiceResponseObject() {

    }

    public ServiceResponseObject(int err, String msg) {
        this.err = err;
        this.msg = msg;
    }

    public ServiceResponseObject(int err) {
        this.err = err;
    }

    public ServiceResponseObject(Object obj) {
        this.args = obj;
    }

    public ServiceResponseObject(Object obj, String msg) {
        this.args = obj;
        this.msg = msg;
    }

    public int getErr(){
        return err;
    }

    public String getMsg(){
        return msg;
    }

    public Object getArgs(){
        return args;
    }

}

