package com.example.drcomputer;

public class AddressItemModel {
    private String location;
    private String city;
    private String state;
    private String pincode;
    private String addressID;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public AddressItemModel(String location, String city, String state, String pincode, String addressID) {
        this.location = location;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.addressID = addressID;
    }

    public String getAddressID() {
        return addressID;
    }

    public void setAddressID(String addressID) {
        this.addressID = addressID;
    }
}
