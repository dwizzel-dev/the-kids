package com.dwizzel.datamodels;


import com.dwizzel.Const;

import java.util.Date;

/**
 * Created by Dwizzel on 20/11/2017.
 */

public class InvitationModel{

    private String email;
    private String inviteId;
    private String phone;
    private String name;
    private String from;
    private String to;
    private Date updateTime;
    private Date createTime;
    private int state = Const.invitation.PENDING;

    public InvitationModel(){

    }

    public InvitationModel(String inviteId, String name, String phone, String email){
        this.inviteId = inviteId;
        this.name = name;
        this.phone = phone;
        this.email = email;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
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

}
