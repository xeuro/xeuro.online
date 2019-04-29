package com.appspot.euro2ether.service;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// xEuroBot
// http://t.me/xEuroBot
public class TelegramBot {

    private static final Logger LOG = Logger.getLogger(TelegramBot.class.getName());
    private static final Gson GSON = new Gson();

    final static private  String telegramBotToken = ApiKeysUtils.getApiKey("TelegramBotToken");
    final static  private  String adminsChatId = ApiKeysUtils.getApiKey("AdminsChatId");
    final static private String urlAddress = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage";

    public static HTTPResponse sendMessageToAdminsChat(final String message) {

        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("chat_id", adminsChatId);
        parameterMap.put("text", message);

        // https://cloud.google.com/appengine/docs/standard/java/javadoc/com/google/appengine/api/urlfetch/HTTPResponse
        HTTPResponse httpResponse = HttpTools.makePostRequestWithParametersMap(urlAddress, parameterMap);

        byte[] httpResponseContentBytes = httpResponse.getContent();
        String httpResponseContentString = null;

        if (httpResponseContentBytes != null) {
            httpResponseContentString = new String(httpResponseContentBytes, StandardCharsets.UTF_8);

            LOG.warning(
                    "httpResponse.getFinalUrl() : " + httpResponse.getFinalUrl() + "\n"
                            + "httpResponse.getResponseCode(): " + httpResponse.getResponseCode() + "\n"
                            + "httpResponse.getContent(): " + httpResponseContentString + "\n"
            );

//            LOG.warning("headers: ");
//            for (HTTPHeader httpHeader : httpResponse.getHeaders()) {
//                String headerName = httpHeader.getName();
//                String headerValue = httpHeader.getValue();
//                LOG.warning(headerName + " : " + headerValue);
//            }

        } else {
            LOG.severe(
                    "httpResponse.getContent() is null"
            );
        }

        return httpResponse;
    }


    public static HTTPResponse sendMessageWithParameterMap(Map<String, String> parameterMap) {

        // https://cloud.google.com/appengine/docs/standard/java/javadoc/com/google/appengine/api/urlfetch/HTTPResponse
        HTTPResponse httpResponse = HttpTools.makePostRequestWithParametersMap(urlAddress, parameterMap);

        return httpResponse;
    }

}
