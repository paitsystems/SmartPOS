package com.pait.smartpos.log;

// Created by anup on 4/21/2017.

public class LogBean {

    String datetime,classname, networkname, isConnected, isServiceStarted;

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getNetworkname() {
        return networkname;
    }

    public void setNetworkname(String networkname) {
        this.networkname = networkname;
    }

    public String getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(String isConnected) {
        this.isConnected = isConnected;
    }

    public String getIsServiceStarted() {
        return isServiceStarted;
    }

    public void setIsServiceStarted(String isServiceStarted) {
        this.isServiceStarted = isServiceStarted;
    }
}
