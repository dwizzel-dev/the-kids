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

    public InvitationModel(){

    }

    public InvitationModel(String inviteId, String name, String phone, String email){
        this.inviteId = inviteId;
        this.name = name;
        this.phone = phone;
        this.email = email;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
