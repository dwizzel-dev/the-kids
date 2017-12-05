package com.dwizzel.objects;

import com.dwizzel.datamodels.InviteInfoModel;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dwizzel on 15/11/2017.
 */

//pour les retour du service
public final class ServiceResponseObject{

    private Object obj;
    private int err = 0;
    private String msg = "";
    private String arg = "";
    private Map<String, Object> args = new HashMap<String, Object>();

    public ServiceResponseObject() {

    }

    public ServiceResponseObject(String msg) {
        this.msg = msg;
    }

    public ServiceResponseObject(int err, String msg) {
        this.err = err;
        this.msg = msg;
    }

    public ServiceResponseObject(int err) {
        this.err = err;
    }

    public ServiceResponseObject(Object obj) {
        this.obj = obj;
    }

    public ServiceResponseObject(Object obj, String msg) {
        this.obj = obj;
        this.msg = msg;
    }

    public ServiceResponseObject(String msg, String arg) {
        this.msg = msg;
        this.arg = arg;
    }

    public ServiceResponseObject(String msg, HashMap<String, Object> args) {
        this.msg = msg;
        this.args.putAll(args);
    }

    public ServiceResponseObject(String msg, InviteInfoModel inviteInfoModel) {
        this.msg = msg;
        this.obj = inviteInfoModel;
    }

    public int getErr(){
        return err;
    }

    public String getMsg(){
        return msg;
    }

    public String getArg(){
        return arg;
    }

    public Map<String, Object> getArgs(){
        return args;
    }

    public Object getObj(){
        return obj;
    }

}

