package com.doubledai.android_hw7;

import android.graphics.Bitmap;

/**
 * Created by Dai on 2017/6/13.
 */

public class Hotel {

    private Bitmap imgUrl;

    public Bitmap getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(Bitmap imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    private String Name;
    private String Phone;
    private String Address;

}
