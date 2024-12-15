package com.simurg.workclock.entity;

public class Employee {
    private String subdivision;
    private String code;

    @Override
    public String toString() {
        return "Employee{" +
                "subdivision='" + subdivision + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getSubdivision() {
        return subdivision;
    }

    public void setSubdivision(String subdivision) {
        this.subdivision = subdivision;
    }

}
