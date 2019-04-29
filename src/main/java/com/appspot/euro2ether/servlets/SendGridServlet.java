package com.appspot.euro2ether.servlets;

import com.appspot.euro2ether.constants.Constants;
import com.appspot.euro2ether.entities.AppSettings;
import com.googlecode.objectify.Key;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * see: https://cloud.google.com/appengine/docs/java/mail/sendgrid
 * https://github.com/sendgrid/sendgrid-google-java
 */
@WebServlet(name = "SendGridServlet")
public class SendGridServlet extends HttpServlet {

    /* --- Logger: */
    private static final Logger LOG = Logger.getLogger(SendGridServlet.class.getName());

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // (?) ofy() should be used from HttpServletRequest context, not as static method in servlet class
        final String SENDGRID_API_KEY = ofy()
                .load()
                .key(Key.create(AppSettings.class, "SendGridApiKey"))
                .now()
                .getStringValue();

        /* --- get parameters */
        String emailTO = req.getParameter("email");
        String emailCC = req.getParameter("emailCC");
        String messageSubject = req.getParameter("messageSubject");
        String messageText = req.getParameter("messageText");
        // String type = req.getParameter("type");

        /* --- log parameters */
        LOG.warning("emailTO: " + emailTO);
        LOG.warning("emailCC: " + emailCC);
        LOG.warning("messageText: " + messageText);
        LOG.warning("messageSubject: " + messageSubject);

        /* initialize the SendGrid object with your SendGrid credentials */
        SendGrid sendgrid = new SendGrid(SENDGRID_API_KEY);

        /* create mail*/
        SendGrid.Email email = new SendGrid.Email();
        email.addTo(emailTO);
        email.setFrom(Constants.emailFrom);

        if (emailCC != null) {
            email.addCc(emailCC);
        }

        email.setSubject(messageSubject);
        email.setText(messageText);

        try {
            SendGrid.Response response = sendgrid.send(email);

            LOG.warning(response.getMessage());

        } catch (SendGridException e) {

            LOG.warning(e.getMessage());

        }

    }

    // protected void doGet(HttpServletRequest request,
    //                      HttpServletResponse response)
    //         throws ServletException, IOException {
    //     doPost(request, response); // --?
    // }

}
