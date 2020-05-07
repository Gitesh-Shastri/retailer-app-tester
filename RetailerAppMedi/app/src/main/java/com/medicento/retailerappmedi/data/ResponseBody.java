package com.medicento.retailerappmedi.data;

public class ResponseBody {

    private String saved_url;

    public ResponseBody(String saved_url) {
        this.saved_url = saved_url;
    }

    public String getSaved_url() {
        return saved_url;
    }

    public void setSaved_url(String saved_url) {
        this.saved_url = saved_url;
    }
}
