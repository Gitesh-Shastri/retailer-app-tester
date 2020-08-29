package com.medicento.retailerappmedi.data;

import java.io.Serializable;

public class SalesPerson implements Serializable{
    private String mName;
    private Long mTotalSales;
    private int mNoOfOrder;
    private int mReturn;
    private float mEarnings;
    private String mId;
    private String mAllocatedAreaId, mAllocatedPharmaId, mAllocatedStateId, mAllocatedCityId;
    private String usercode;
    private String phone;
    private String email, address;
    private String type;
    private String area_name, city_name, state_name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState_name() {
        return state_name != null ? state_name : "";
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getCity_name() {
        return city_name != null ? city_name : "";
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getmAllocatedStateId() {
        return mAllocatedStateId;
    }

    public void setmAllocatedStateId(String mAllocatedStateId) {
        this.mAllocatedStateId = mAllocatedStateId;
    }

    public String getmAllocatedCityId() {
        return mAllocatedCityId;
    }

    public void setmAllocatedCityId(String mAllocatedCityId) {
        this.mAllocatedCityId = mAllocatedCityId;
    }

    public String getAddress() {
        return address != null ? address : "";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email != null ? email : "";
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

    public SalesPerson() {
        this.type = "Pharmacy";
    }

    public SalesPerson(String name, Long totalSales, int noOfOrder, int returns, float earnings, String id, String allocatedAreaId, String pId) {
        mName = name;
        mTotalSales = totalSales;
        mNoOfOrder = noOfOrder;
        mReturn = returns;
        mEarnings = earnings;
        mId = id;
        mAllocatedAreaId = allocatedAreaId;
        mAllocatedPharmaId = pId;
        this.type = "Pharmacy";
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getName() {
        return mName;
    }

    public float getTotalSales() {
        return mTotalSales;
    }

    public int getNoOfOrder() {return mNoOfOrder;}

    public int getReturn() {
        return mReturn;
    }

    public float getEarnings() {
        return mEarnings;
    }

    public String getId() {
        return mId;
    }

    public String getAllocatedArea() {
        return mId;
    }

    public String getmAllocatedPharmaId() {
        return mAllocatedPharmaId;
    }
}
