package com.careerdevs.gateway.gorest.models;

public class GoRestResponse {

    private GoRestMeta meta;
    private Object data;

    public Object getMeta() {
        return meta;
    }

    public void setMeta(GoRestMeta meta) {
        this.meta = meta;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
