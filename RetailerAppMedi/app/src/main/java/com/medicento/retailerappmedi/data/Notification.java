package com.medicento.retailerappmedi.data;

public class Notification {

    private String type, title, message, order_id, status, id, created_at, time;

    public Notification() {
    }

    public Notification(String type, String title, String message, String order_id, String status, String id, String created_at, String time) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.order_id = order_id;
        this.status = status;
        this.id = id;
        this.created_at = created_at;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public Notification setId(String id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getStatus() {
        return status;
    }

    public Notification setType(String type) {
        this.type = type;
        return this;
    }

    public Notification setTitle(String title) {
        this.title = title;
        return this;
    }

    public Notification setMessage(String message) {
        this.message = message;
        return this;
    }

    public Notification setOrder_id(String order_id) {
        this.order_id = order_id;
        return this;
    }


    public Notification setStatus(String status) {
        this.status = status;
        return this;
    }


    public Notification createNotification() {
        return new Notification(type, title, message, order_id, status, id, created_at, time);
    }

    public String getCreated_at() {
        return created_at;
    }

    public Notification setCreated_at(String created_at) {
        this.created_at = created_at;
        return this;
    }

    public String getTime() {
        return time;
    }

    public Notification setTime(String time) {
        this.time = time;
        return this;
    }
}
