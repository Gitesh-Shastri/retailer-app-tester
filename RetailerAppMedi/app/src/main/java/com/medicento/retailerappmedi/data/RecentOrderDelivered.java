package com.medicento.retailerappmedi.data;

import java.io.Serializable;
import java.util.ArrayList;

public class RecentOrderDelivered implements Serializable {

    private String pOrderId;
    private String pDate, status;
    private int total;

    public RecentOrderDelivered(String pOrderId, String pDate, String status, int total, ArrayList<RecentOrderMedicine> medicines) {
        this.pOrderId = pOrderId;
        this.pDate = pDate;
        this.status = status;
        this.total = total;
        this.medicines = medicines;
    }

    public RecentOrderDelivered(String pOrderId, String pDate, String status, int total) {
        this.pOrderId = pOrderId;
        this.pDate = pDate;
        this.status = status;
        this.total = total;
        this.medicines = new ArrayList<>();
    }

    public void setpOrderId(String pOrderId) {
        this.pOrderId = pOrderId;
    }

    public void setpDate(String pDate) {
        this.pDate = pDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    private ArrayList<RecentOrderMedicine> medicines;

    public ArrayList<RecentOrderMedicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(ArrayList<RecentOrderMedicine> medicines) {
        this.medicines = medicines;
    }

    public RecentOrderDelivered(String pOrderId, String pDate, int total) {
        this.pOrderId = pOrderId;
        this.pDate = pDate;
        this.total = total;
    }

    public String getpOrderId() {
        return pOrderId;
    }

    public String getpDate() {
        return pDate;
    }

    public int getTotal() {
        return total;
    }
}
