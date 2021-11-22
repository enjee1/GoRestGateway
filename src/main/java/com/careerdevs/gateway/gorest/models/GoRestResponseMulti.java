package com.careerdevs.gateway.gorest.models;

public class GoRestResponseMulti {

    private GoRestMeta meta;
    private GoRestUser[] data;

    public GoRestMeta getMeta() {
        return meta;
    }

    public void setMeta(GoRestMeta meta) {
        this.meta = meta;
    }

    public GoRestUser[] getData() {
        return data;
    }

    public void setData(GoRestUser[] data) {
        this.data = data;
    }
}
