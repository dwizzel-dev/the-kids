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
    private Date updateTime;
    private Date createTime;
    private int state = Const.invites.PENDING;

    public InvitationModel(){

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
