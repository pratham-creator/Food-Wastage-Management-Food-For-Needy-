package com.example.foodforneedy;

import java.io.Serializable;

public class User implements Serializable {
    String email,userType,mobile,ngo_name,district,profileImageUrl;
    String donationNo;

    public String getEmail() {
        return email;
    }

    public String getUserType() {
        return userType;
    }

    public String getMobile() {
        return mobile;
    }

    public String getNgo_name() {
        return ngo_name;
    }

    public String getDistrict() {
        return district;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setNgo_name(String ngo_name) {
        this.ngo_name = ngo_name;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getDonationNo() {
        return donationNo;
    }

    public void setDonationNo(String donationNo) {
        this.donationNo = donationNo;
    }

    public User(){}

    public User(String email,String userType,String mobile,String ngo_name,String district,String uri,String donationNo){
        this.email=email;
        this.userType=userType;
        this.mobile=mobile;
        this.ngo_name=ngo_name;
        this.district=district;
        this.profileImageUrl=uri;
        this.donationNo=donationNo;
    }
}
