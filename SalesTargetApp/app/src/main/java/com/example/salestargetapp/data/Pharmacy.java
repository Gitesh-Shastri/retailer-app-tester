package com.example.salestargetapp.data;

import java.io.Serializable;

public class Pharmacy implements Serializable {

    String name, id, pharma_code, mobile_no;

    public Pharmacy(String name, String id, String pharma_code, String mobile_no) {
        this.name = name;
        this.id = id;
        this.pharma_code = pharma_code;
        this.mobile_no = mobile_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPharma_code() {
        return pharma_code;
    }

    public void setPharma_code(String pharma_code) {
        this.pharma_code = pharma_code;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }
}
