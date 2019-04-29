package com.appspot.euro2ether.returns;

import com.google.gson.Gson;

import java.io.Serializable;

public class StringWrapper implements Serializable {

    private String message;
    private String data;
    private String error;
    private Integer integerValue;

    /* -- Constructors */

    public StringWrapper() {
    }

    public StringWrapper(String message) {
        this.message = message;
    }

    /* -- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* -- Getters and Setters */

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

}
