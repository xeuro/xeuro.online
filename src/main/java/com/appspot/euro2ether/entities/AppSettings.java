package com.appspot.euro2ether.entities;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Used to store in Datastore API keys, passwords ect - data that needed by the App,
 * but should not be exposed in open source code
 * Entities should be created only manually in DS by admins
 */
@Entity // ->  service.OfyService
@Cache // ! should be cashed
public class AppSettings implements Serializable {
    @Id
    private String name; //
    private String stringValue;
    private Integer integerValue;
    private ArrayList<String> stringsArrayList;
    private String info;
    //
    @Index
    private Date createdOn;
    @Index
    private Date changedOn;
    @Index
    private User changedBy;

    /* --- Constructors: */
    public AppSettings() {
    }

    public AppSettings(String name) {
        this.name = name;
    }

    /* --- Methods */

    public Boolean addToStringsArrayList(String str) {
        if (this.stringsArrayList == null) {
            this.stringsArrayList = new ArrayList<>();
        }
        return this.stringsArrayList.add(str);
    }

    /* --- Getters and Setters: */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public ArrayList<String> getStringsArrayList() {
        return stringsArrayList;
    }

    public void setStringsArrayList(ArrayList<String> stringsArrayList) {
        this.stringsArrayList = stringsArrayList;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getChangedOn() {
        return changedOn;
    }

    public void setChangedOn(Date changedOn) {
        this.changedOn = changedOn;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

}
