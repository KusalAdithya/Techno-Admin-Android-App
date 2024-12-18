package com.waka.techoadmin.model;

public class User {
    private String email, firstName,lastName, mobile,addressNo,addressLine,city,userDp;

    public User() {
    }

    public User(String email, String firstName, String lastName, String mobile, String addressNo, String addressLine, String city, String userDp) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.addressNo = addressNo;
        this.addressLine = addressLine;
        this.city = city;
        this.userDp = userDp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddressNo() {
        return addressNo;
    }

    public void setAddressNo(String addressNo) {
        this.addressNo = addressNo;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUserDp() {
        return userDp;
    }

    public void setUserDp(String userDp) {
        this.userDp = userDp;
    }
}
