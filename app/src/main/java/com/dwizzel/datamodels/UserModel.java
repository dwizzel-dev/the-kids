package com.dwizzel.datamodels;

import java.util.Date;

/**
 * Created by Dwizzel on 20/11/2017.
 */

//---------------------------------------------------------------------------------------------
//Firestore Data Object

public class UserModel{

    private Date createTime;
    private Date updateTime;
    private int loginType;
    private String email;
    private String uid;
    private int status;

    UserModel(){

    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.status = loginType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }




}
