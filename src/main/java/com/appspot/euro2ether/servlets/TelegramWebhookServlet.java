package com.appspot.euro2ether.servlets;

import com.appspot.euro2ether.service.ApiKeysUtils;
import com.appspot.euro2ether.constants.Constants;
import com.appspot.euro2ether.service.ServletUtils;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.gson.Gson;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet(name = "TelegramWebhookServlet")
public class TelegramWebhookServlet extends HttpServlet {

    /* --- Logger: */
    // https://docs.oracle.com/javase/8/docs/api/java/util/logging/Logger.html
    // Find or create a logger for a named subsystem. If a logger has already been created with the given name it is returned.
    // Otherwise a new logger is created.
    // - When running Tomcat on unixes, the console output is usually redirected to the file named catalina.out
    private static final String className = TelegramWebhookServlet.class.getName();
    private static final Logger LOG = Logger.getLogger(className);

    /* --- Gson: */
    private static final Gson GSON = new Gson();

    /*
     * to receive information using Telegram webhook ( Update object)
     * Telegram: "Whenever there is an update for the bot, we will send an HTTPS POST request to the specified url,
     * containing a JSON-serialized Update. // see: Update object description on https://core.telegram.org/bots/api#update
     * In case of an unsuccessful request, we will give up after a reasonable amount of attempts"
     * https://core.telegram.org/bots/api#setwebhook
     *
     * https://api.telegram.org/bot<token>/setWebhook?url=<url>
     *
     *
     * */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // final String botName = "xEuro.Bot";
        final String botToken = ApiKeysUtils.getApiKey("TelegramBotToken");
        final String adminsChatId = ApiKeysUtils.getApiKey("AdminsChatId");
        final Long adminsChatIdL = Long.parseLong(adminsChatId);

        final String urlKeyReceived = ServletUtils.getUrlKey(request);

        if (!urlKeyReceived.contains(botToken)) {
            LOG.warning("wrong url key:");
            LOG.warning(urlKeyReceived);
            throw new ServletException();
        }

        // see:
        // https://github.com/pengrad/java-telegram-bot-api#webhook
        java.io.Reader reader = request.getReader();
        Update update = BotUtils.parseUpdate(reader); // or from java.io.Reader

        Message message = update.message();
        if (message != null) {

            String messageText = message.text();
            Integer message_id = message.messageId();
            Chat chat = message.chat();
            Long chat_id = chat.id();
            String chatIdStr = String.valueOf(chat_id);

            String startMessage = "Hi, I'm xEuroBot.\n"
                    + "Nice to meet you, "
                    + message.from().firstName() + " " + message.from().lastName() + ". "
                    + "if you need more info, type: /moreInfo \n"
                    + "if you want to start from the beginning, type: /start \n";

            String textToSend = null;

            if (messageText != null) {
                if (messageText.equalsIgnoreCase("/start")) {
                    textToSend = startMessage;
                } else if (messageText.equalsIgnoreCase("/moreInfo")) {
                    textToSend = "please visit my web-app: " + Constants.GAE_PROJECT_URL;
                } else if (!message.chat().id().equals(adminsChatIdL)) {
                    textToSend = startMessage;
                    // textToSend = "really, " + message.text() + "?\n";
                }
            }

            if (textToSend != null) {
                LOG.warning(
                        "sending message: " + textToSend
                                + " to chat: " + message.chat().id()
                                + " in respond to message '" + messageText + "' from " + message.from().username()
                );

                // >>> works faster that method from the lib

                // see:
                // https://core.telegram.org/bots/api#sendmessage
                Map<String, String> parameterMap = new HashMap<>();
                parameterMap.put("text", textToSend);
                // parameterMap.put("chat_id", Integer.toString(chat_id));
                parameterMap.put("chat_id", chatIdStr);
                parameterMap.put("reply_to_message_id", Integer.toString(message_id));

                HTTPResponse httpResponse = com.appspot.euro2ether.service.TelegramBot.sendMessageWithParameterMap(parameterMap);
                LOG.warning("httpResponse: " + httpResponse);
            }

            ServletUtils.sendJsonResponse(response, "O.K.");

        } // end of if (message != null)

    } // end of doPost

}
