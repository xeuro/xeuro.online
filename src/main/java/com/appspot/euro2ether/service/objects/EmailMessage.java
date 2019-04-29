package com.appspot.euro2ether.service.objects;

import com.google.gson.Gson;

public class EmailMessage {

    private String to;
    private String cc;
    private String subject;
    private String messageText;

    /* --- Constructor */

    public EmailMessage() {
    }

    public EmailMessage(String to, String cc, String subject, String messageText) {
        this.to = to;
        this.cc = cc;
        this.subject = subject;
        this.messageText = messageText;
    }

    /* --- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* --- Getters and Setters */

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

}
