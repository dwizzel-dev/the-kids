package com.dwizzel.objects;

import com.dwizzel.datamodels.InviteInfoModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dwizzel on 15/11/2017.
 */

//pour les retour du service
public final class ServiceResponseObject{

    private Object obj;
    private int err = 0;
    private int msg = 0;
    private String errMsg = "";
    private Map<String, Object> args = new HashMap<>();

    public ServiceResponseObject() {}

    public ServiceResponseObject(int msg) {
        this.msg = msg;
    }

    public ServiceResponseObject(int err, String msg) {
        this.err = err;
        this.errMsg = msg;
    }

    public ServiceResponseObject(Object obj) {
        this.obj = obj;
    }

    public ServiceResponseObject(int msg, HashMap<String, Object> args) {
        this.msg = msg;
        this.args.putAll(args);
    }

    public ServiceResponseObject(int msg, InviteInfoModel inviteInfoModel) {
        this.msg = msg;
        this.obj = inviteInfoModel;
    }

    public int getErr(){
        return err;
    }

    public String getErrMsg(){
        return errMsg;
    }

    public int getMsg(){
        return msg;
    }

    public Map<String, Object> getArgs(){
        return args;
    }

    public Object getObj(){
        return obj;
    }

}

