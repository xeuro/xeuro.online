package com.appspot.euro2ether.servlets;

import com.appspot.euro2ether.constants.Constants;
import com.appspot.euro2ether.returns.HttpRequestWrapper;
import com.appspot.euro2ether.returns.bitcoinus.DepositCallbackData;
import com.appspot.euro2ether.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Logger;

/*
 * this will be called when new payment received to Bitcoinus account
 *
 * */
@WebServlet(name = "BitcoinusWebhookServlet")
public class BitcoinusWebhookServlet extends HttpServlet {

    /* --- Logger: */
    private final static Logger LOG = Logger.getLogger(BitcoinusWebhookServlet.class.getName());

    /* --- Gson: */
    // private static final Gson GSON = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        /* --- get API key from DB */

        final String bitcoinusApiSecretKey = ApiKeysUtils.getApiKey("bitcoinusApiSecretKey");

        /* --- parse request */

        HttpRequestWrapper httpRequestWrapper = ServletUtils.getAllRequestData(request);

        // for debug:
        LOG.warning(httpRequestWrapper.toString());

        String signature = httpRequestWrapper.getParameters().get("signature");
        LOG.warning("signature: " + "\n" + signature);
        String dataStr = httpRequestWrapper.getParameters().get("data");
        LOG.warning("data: " + "\n" + dataStr);

        /* ----- check signature */

        Boolean signatureVerificationResult = false;
        try {
            signatureVerificationResult = CryptoUtils.hmacSHA256(dataStr, bitcoinusApiSecretKey).equalsIgnoreCase(signature);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            LOG.severe(e.getMessage());
            throw new IOException(e.getMessage());
        }

        if (!signatureVerificationResult) {
            throw new ServletException("Signature verification result: " + signatureVerificationResult);
        }

        /* --- process data */

        DepositCallbackData depositCallbackData = new ObjectMapper().readValue(
                httpRequestWrapper.getParameters().get("data"),
                DepositCallbackData.class
        );


        /* --- For test callbacks: */

        if (!Constants.production) {
            if (depositCallbackData.tid == null) {
                depositCallbackData.tid = new Date().getTime() / 1000;
                LOG.warning("generated depositCallbackData.tid for test transaction  " + depositCallbackData.tid);
            }
        }

        /* --- log and send info to Telegram chat */

        TelegramBot.sendMessageToAdminsChat("===== [webhook] callback: =====");
        TelegramBot.sendMessageToAdminsChat(depositCallbackData.toString());
        // for debug:
        // LOG.warning("depositCallbackData.toString():");
        LOG.warning(depositCallbackData.toString());

        if (Constants.production && depositCallbackData.tid == null) {
            LOG.severe("depositCallbackData.tid is null");
            throw new IllegalArgumentException("depositCallbackData.tid is null"); // TODO: handle this case
        }

        /* --- process payment and mint tokens */

        if (depositCallbackData.currency.equalsIgnoreCase("EUR")) {
            try {

                if (SmartContractService.mintAndSendTokens(depositCallbackData)) {

                    response.setStatus(200);
                    ServletUtils.sendJsonResponse(response, "{\"response\":\"OK\"}");

                } else {
                    LOG.severe("SmartContractService.mintAndSendTokens(depositCallbackData) returned FALSE");
                    throw new ServletException("Execution failed");
                }

            } catch (URISyntaxException e) {
                LOG.severe(e.getMessage());
                throw new ServletException(e.getMessage());
            }
        } else { // deposit not in EUR
            response.setStatus(200);
            ServletUtils.sendJsonResponse(response, "{\"response\":\"OK\"}");
        }

    }

    /*
    https://xeuro.online/bitcoinusWebhook/?param0=foo&param1=bar
    to test:
    curl -X GET -H "Accept: application/json" https://xeuro.online/bitcoinusWebhook?param0=foo&param1=bar
    * */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpRequestWrapper httpRequestWrapper = ServletUtils.getAllRequestData(request);
        ServletUtils.sendJsonResponse(
                response,
                httpRequestWrapper.toString()
        );
    }
}
