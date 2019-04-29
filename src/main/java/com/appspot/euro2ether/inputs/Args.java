package com.appspot.euro2ether.inputs;

import com.google.gson.Gson;

public class Args {

    public Long tokensInEventsCounter;
    public String from;
    public Long value;

    public Args() {
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public Long getTokensInEventsCounter() {
        return tokensInEventsCounter;
    }

    public void setTokensInEventsCounter(Long tokensInEventsCounter) {
        this.tokensInEventsCounter = tokensInEventsCounter;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

}
