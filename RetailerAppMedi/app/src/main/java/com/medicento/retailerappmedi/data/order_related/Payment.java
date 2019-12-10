package com.medicento.retailerappmedi.data.order_related;

public class Payment {

    private String grand_total, content, summary, date, order_id;
    private boolean isDate;

    public Payment(String grand_total, String content, String summary, String date, String order_id, boolean isDate) {
        this.grand_total = grand_total;
        this.content = content;
        this.summary = summary;
        this.date = date;
        this.order_id = order_id;
        this.isDate = isDate;
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
        return new Payment(grand_total, content, summary, date, order_id, isDate);
    }
}
