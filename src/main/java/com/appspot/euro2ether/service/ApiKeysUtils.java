package com.appspot.euro2ether.service;

import com.appspot.euro2ether.entities.AppSettings;
import com.googlecode.objectify.Key;

import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Utilities to work with API Keys
 */
public class ApiKeysUtils {

    /* ---- Logger: */
    // private static final Logger LOG = Logger.getLogger(ApiKeysUtils.class.getName());

    public static String getApiKey(String apiKeyName) {

        String API_KEY = ofy()
                .load()
                .key(Key.create(AppSettings.class, apiKeyName))
                .now()
                .getStringValue();

        return API_KEY;
    } // end of getApiKey();

    public static Integer getIntegerValue(String apiKeyName) {

        Integer integer = ofy()
                .load()
                .key(Key.create(AppSettings.class, apiKeyName))
                .now()
                .getIntegerValue();

        return integer;
    } // end of getIntegerValue;

}
