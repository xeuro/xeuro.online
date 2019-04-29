package com.appspot.euro2ether.service.objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Date;

/*
    "timestamp": {
      "date": "2019-03-15 11:40:42.000000",
      "timezone_type": 3,
      "timezone": "Europe/Vilnius"
    }
* */
public class BitcoinusTimestamp implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    @JsonProperty("date")
    public Date date;

    @JsonProperty("timezone_type")
    public Integer timezone_type;

    @JsonProperty("timezone")
    public String timezone;

    /* --- Constructor */

    public BitcoinusTimestamp() {
    }

    /* --- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* --- Getters and Setters */

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getTimezone_type() {
        return timezone_type;
    }

    public void setTimezone_type(Integer timezone_type) {
        this.timezone_type = timezone_type;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

}
