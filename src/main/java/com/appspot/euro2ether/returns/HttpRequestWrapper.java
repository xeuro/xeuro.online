package com.appspot.euro2ether.returns;

import com.google.gson.Gson;

import javax.servlet.http.Cookie;
import java.io.Serializable;
import java.util.HashMap;

public class HttpRequestWrapper implements Serializable {

    private HashMap<String, String> headers;
    private HashMap<String, Cookie> cookies;
    private HashMap<String, String> parameters;
    private String message;
    private String error;

    /* --- Constructor */

    public HttpRequestWrapper() {
    }

    public HttpRequestWrapper(
            HashMap<String, String> headers,
            HashMap<String, Cookie> cookies,
            HashMap<String, String> parameters) {
        this.headers = headers;
        this.cookies = cookies;
        this.parameters = parameters;
    }

    /* ---- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* -- Getters and Setters */

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public HashMap<String, Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(HashMap<String, Cookie> cookies) {
        this.cookies = cookies;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
