package com.dwizzel.datamodels;


import com.dwizzel.Const;

import java.util.Date;

/**
 * Created by Dwizzel on 20/11/2017.
 */

public class InviteModel{

    private String from;
    private String to;
    private Date updateTime;
    private int code = Const.invitation.DEFAULT_CODE;
    private int state = Const.invitation.PENDING;

    public InviteModel(){

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


}
