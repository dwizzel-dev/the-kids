package com.dwizzel.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dwizzel on 20/11/2017.
 * /invites/Ykd3PiB15IRoF7oaVcBh/infos/GAGPWfDW9QYEIGNol1hLjinfoTF3
 */

public class InviteInfoModel implements Parcelable {

    private String inviteId;
    private String from;
    private String to;
    private String code;
    private String email;
    private String phone;
    private String name;

    public InviteInfoModel(){}

    public InviteInfoModel(String inviteId, String code, String from, String to){
        this.inviteId = inviteId;
        this.code = code;
        this.from = from;
        this.to = to;
    }

    public InviteInfoModel(Parcel data){
        inviteId = data.readString();
        from = data.readString();
        to = data.readString();
        code = data.readString();
        email = data.readString();
        phone = data.readString();
        name = data.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inviteId);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(code);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(name);
    }

    public static final Creator<InviteInfoModel> CREATOR = new Creator<InviteInfoModel>() {
        @Override
        public InviteInfoModel createFromParcel(Parcel data) {
            return new InviteInfoModel(data);
        }
        @Override
        public InviteInfoModel[] newArray(int size) {
            return new InviteInfoModel[size];
        }
    };

    @Override
    public int describeContents(){
        return 0;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
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
