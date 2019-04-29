package com.appspot.euro2ether.service;

import com.appspot.euro2ether.service.objects.EmailMessage;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class EmailTools {

    public static EmailMessage sendEmail(EmailMessage emailMessage) {

        final Queue queue = QueueFactory.getDefaultQueue();

        // Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
        queue.add(
                TaskOptions.Builder
                        .withUrl("/_ah/SendGridServlet")
                        .param("email", emailMessage.getTo())
                        .param("messageSubject", emailMessage.getSubject())
                        .param("messageText", emailMessage.getMessageText())
        );

        return emailMessage;
    }

}
