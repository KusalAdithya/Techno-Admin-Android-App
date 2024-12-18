package com.waka.techoadmin.model;

public class Admin {
    private String email,fName,lName,mobile,adminDp;

    public Admin() {
    }

    public Admin(String email, String fName, String lName, String mobile, String adminDp) {
        this.email = email;
        this.fName = fName;
        this.lName = lName;
        this.mobile = mobile;
        this.adminDp = adminDp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAdminDp() {
        return adminDp;
    }

    public void setAdminDp(String adminDp) {
        this.adminDp = adminDp;
    }
}
