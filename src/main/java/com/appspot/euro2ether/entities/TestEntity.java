package com.appspot.euro2ether.entities;

import com.google.gson.Gson;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;
import java.util.Date;

/*
 * see:
 * https://github.com/objectify/objectify/wiki/Entities
 * */
@Entity // -> com.appspot.euro2ether.service.OfyService
//@Cache // do not cache for tests
public class TestEntity implements Serializable {

    @Id
    private Long Id; // ..........................................1
    @Index
    private Date entityCreated; //................................2
    private String stringValue; //................................3
    private Integer integerValue; //..............................4

    /* --- Constructors: */

    public TestEntity() {

    }

    public TestEntity(String stringValue, Integer integerValue) {
        this.entityCreated = new Date();
        this.stringValue = stringValue;
        this.integerValue = integerValue;
    }

    /* ---- methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* --- Getters and Setters: */

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Date getEntityCreated() {
        return entityCreated;
    }

    public void setEntityCreated(Date entityCreated) {
        this.entityCreated = entityCreated;
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
}
