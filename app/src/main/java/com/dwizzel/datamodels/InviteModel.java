package com.dwizzel.datamodels;


import com.dwizzel.Const;

import java.util.Date;

/**
 * Created by Dwizzel on 20/11/2017.
 */

public class InviteModel{

    private String from;
    private Date updateTime;
    private int code = Const.invitation.DEFAULT_CODE;

    public InviteModel(){}

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

}
