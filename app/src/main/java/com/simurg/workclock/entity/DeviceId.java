package com.simurg.workclock.entity;

import android.os.DeadObjectException;

public class DeviceId {
    private String id;

    public DeviceId(String id){
        this.id = id;
    }
    public boolean isValid() {
        return isNotNullOrEmpty() && isNumeric() && isCorrectLength();
    }

    private boolean isNotNullOrEmpty() {
        return id != null && !id.trim().isEmpty();
    }

    private boolean isNumeric() {
        return id.matches("\\d+");
    }

    private boolean isCorrectLength() {
        return id.length() >= 5 && id.length() <= 30;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
