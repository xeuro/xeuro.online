package com.appspot.euro2ether.inputs;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

/*
web3.js 0.20.6
{
  "address": "0xde3c9a39015375abc2627eb78eb9bfbd48e9b731",
  "blockHash": "0x9bde25fbb077dd24b7011605f51965ddd040c50462006513296457799260815f",
  "blockNumber": 5405245,
  "logIndex": 0,
  "removed": false,
  "transactionHash": "0x6ae90ff5cd02b03d4d7d578b578fb371e0ebda47045c98e36337637d5448d514",
  "transactionIndex": 0,
  "transactionLogIndex": "0x0",
  "type": "mined",
  "event": "TokensIn",
  "args": {
    "from": "0xf41ca2ce3a6cfd3097ba0ab13fd8ba18bd1a3430",
    "value": "13",
    "tokensInEventsCounter": "1"
  }
}
 * */
public class TokensInEventFromServer implements Serializable {

    private String address;
    private String blockHash;
    private Long blockNumber;
    private Long logIndex;
    private Boolean removed;
    private String transactionHash;
    private Long transactionIndex;
    private String transactionLogIndex;
    private String type;
    private String event;
    private Args args;

    /* --- Constructor */

    public TokensInEventFromServer() {
    }

    /* --- Methods */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* --- Getters and Setters */

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public Long getLogIndex() {
        return logIndex;
    }

    public void setLogIndex(Long logIndex) {
        this.logIndex = logIndex;
    }

    public Boolean getRemoved() {
        return removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = removed;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Long getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(Long transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getTransactionLogIndex() {
        return transactionLogIndex;
    }

    public void setTransactionLogIndex(String transactionLogIndex) {
        this.transactionLogIndex = transactionLogIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Args getArgs() {
        return args;
    }

    public void setArgs(Args args) {
        this.args = args;
    }

}
