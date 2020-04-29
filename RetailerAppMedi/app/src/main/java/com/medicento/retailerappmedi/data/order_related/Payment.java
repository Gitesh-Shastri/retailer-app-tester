package com.medicento.retailerappmedi.data.order_related;

import com.medicento.retailerappmedi.data.OrderItem;

import java.util.ArrayList;

public class Payment {

    private String grand_total, content, summary, date, order_id, net_due, invoice_ids, paid_status, days;
    private boolean isDate;
    private ArrayList<OrderItem> orderItems;

    public Payment(String grand_total, String content, String summary, String date, String invoice_ids, boolean isDate, String setNet_due) {
        this.grand_total = grand_total;
        this.content = content;
        this.summary = summary;
        this.date = date;
        this.invoice_ids = invoice_ids;
        this.isDate = isDate;
        this.net_due = setNet_due;
        this.orderItems = new ArrayList<>();
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getPaid_status() {
        return paid_status;
    }

    public void setPaid_status(String paid_status) {
        this.paid_status = paid_status;
    }

    public String getInvoice_ids() {
        return invoice_ids;
    }

    public Payment setInvoice_ids(String invoice_ids) {
        this.invoice_ids = invoice_ids;
        return this;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getNet_due() {
        return net_due;
    }

    public Payment setNet_due(String net_due) {
        this.net_due = net_due;
        return this;
    }

    public Payment() {
    }

    public String getGrand_total() {
        return grand_total;
    }

    public String getContent() {
        return content;
    }

    public String getSummary() {
        return summary;
    }

    public String getDate() {
        return date;
    }

    public boolean isDate() {
        return isDate;
    }

    public Payment setGrand_total(String grand_total) {
        this.grand_total = grand_total;
        return this;
    }

    public Payment setContent(String content) {
        this.content = content;
        return this;
    }

    public Payment setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public Payment setDate(String date) {
        this.date = date;
        return this;
    }

    public Payment setIsDate(boolean isDate) {
        this.isDate = isDate;
        return this;
    }

    public String getOrder_id() {
        return order_id;
    }

    public Payment setOrder_id(String order_id) {
        this.order_id = order_id;
        return this;
    }

    public void setDate(boolean date) {
        isDate = date;
    }

    public Payment createPayment() {
        return new Payment(grand_total, content, summary, date, invoice_ids, isDate, net_due);
    }
}
