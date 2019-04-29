package com.appspot.euro2ether.api;


import com.appspot.euro2ether.constants.Constants;
import com.appspot.euro2ether.entities.TestEntity;
import com.appspot.euro2ether.returns.HttpRequestWrapper;
import com.appspot.euro2ether.returns.StringWrapper;
import com.appspot.euro2ether.service.ServletUtils;
import com.appspot.euro2ether.service.TelegramBot;
import com.appspot.euro2ether.service.UserTools;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.users.User;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * explore on: {project name}.appspot.com/_ah/api/explorer
 * ! - API should be registered in  web.xml (<param-name>services</param-name>)
 * ! - on frontend API should be loaded in app.js - app.run()
 */

@Api(
        name = "testAPI", // The api name must match '[a-z]+[A-Za-z0-9]*'
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constants.WEB_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID},
        description = "for testing"
)
public class TestAPI {

    /* --- Logger: */
    private static final Logger LOG = Logger.getLogger(TestAPI.class.getName());
    /* --- Gson */
    private static final Gson GSON = new Gson();

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "getUserEmail",
            path = "getUserEmail",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public Email getUserEmail(final HttpServletRequest httpServletRequest,
                              final User googleUser
    ) throws UnauthorizedException {

        if (googleUser == null) {
            throw new UnauthorizedException("Invalid credentials");
        }

        Email email = new Email(googleUser.getEmail());
        LOG.warning(email.getEmail());

        return email;

    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "addTestEntity",
            path = "addTestEntity",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public TestEntity addTestEntity(final HttpServletRequest httpServletRequest,
                                    final User googleUser,
                                    @Named("stringValue") final String stringValue,
                                    @Named("integerValue") final Integer integerValue
    ) throws UnauthorizedException {

        if (UserTools.isAdmin(googleUser)) {
            TestEntity testEntity = new TestEntity(stringValue, integerValue);
            Key<TestEntity> testEntityKey = ofy().save().entity(testEntity).now();
            TestEntity testEntitySaved = ofy().load().key(testEntityKey).now();
            LOG.warning(testEntitySaved.toString());
            return testEntitySaved;
        }

        return null;

    }

    /* -i > display info
     * curl -i -H "Content-Type: application/json" -X POST -d '{"message":"test message"}' https://euro2ether.appspot.com/_ah/api/testAPI/v1/returnPostRequestData
     * curl -H "Content-Type: application/json" -X POST -d '{"message":"test message"}' https://euro2ether.appspot.com/_ah/api/testAPI/v1/returnPostRequestData
     * */
    @SuppressWarnings("unused")
    @ApiMethod(
            name = "returnPostRequestData",
            path = "returnPostRequestData",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public HttpRequestWrapper returnMessage(
            final HttpServletRequest request,
            final @Named("message") String message
    ) {

        HttpRequestWrapper result = ServletUtils.getAllRequestData(request);
        result.setMessage(message);

        LOG.warning(result.toString());

        return result;
    }

    /* -i > display info
     * curl -i -H "Content-Type: application/json" -X GET https://euro2ether.appspot.com/_ah/api/testAPI/v1/returnPostRequestDataGet?message=this+is+a+test+message
     * curl -H "Content-Type: application/json" -X GET https://euro2ether.appspot.com/_ah/api/testAPI/v1/returnPostRequestDataGet?message=this+is+a+test+message
     *
     * */
    @SuppressWarnings("unused")
    @ApiMethod(
            name = "returnPostRequestDataGet",
            path = "returnPostRequestDataGet",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public HttpRequestWrapper returnMessageGET(
            final HttpServletRequest request,
            final @Named("message") String message
    ) {
        HttpRequestWrapper result = ServletUtils.getAllRequestData(request);
        result.setMessage(message);
        LOG.warning(result.toString());
        return result;
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "sendMessageToAdminsChat",
            path = "sendMessageToAdminsChat",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public StringWrapper sendMessageToAdminsChat(
            final HttpServletRequest request,
            final User googleUser,
            final @Named("message") String message
    ) throws UnauthorizedException {

        StringWrapper result = new StringWrapper();

        if (UserTools.isAdmin(googleUser)) { // throws UnauthorizedException
            TelegramBot.sendMessageToAdminsChat(message);
            result.setMessage("message sent to xEuro.Admins chat");
            result.setData(message);
        }

        return result;
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "getMyData",
            path = "getMyData",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public HashMap<String, String> getMyData(
            final HttpServletRequest request
            // final User googleUser,
            // final @Named("message") String message
    ) throws Exception {

        HashMap<String, String> result = new HashMap<>();

        result.put("ip", request.getRemoteAddr());
        result.put("host", request.getRemoteHost());

        return result;
    }

}
