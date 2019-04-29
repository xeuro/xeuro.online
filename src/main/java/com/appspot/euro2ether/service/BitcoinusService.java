package com.appspot.euro2ether.service;

import com.appspot.euro2ether.constants.Constants;
import com.appspot.euro2ether.returns.bitcoinus.GetBalanceResponse;
import com.appspot.euro2ether.returns.bitcoinus.SendFundsResponse;
import com.appspot.euro2ether.returns.bitcoinus.SignedResponceObject;
import com.appspot.euro2ether.service.objects.BitcoinusGetTransactionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.gson.Gson;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.logging.Logger;

public class BitcoinusService {

    private static final Logger LOG = Logger.getLogger(BitcoinusService.class.getName());
    private static final Gson GSON = new Gson();

    private static final String clientid = ApiKeysUtils.getApiKey("bitcoinusApiClientId");
    private static final String bitcoinusApiSecretKey = ApiKeysUtils.getApiKey("bitcoinusApiSecretKey");

    /*
     * Merchant API reference :
     * POST/GET request to /api/v1/command
     * GET getTransaction
     * Get transaction information
     * PARAMETERS:
     * id​ - Transaction ID
     * clientid​ - Client ID
     * signature​ - HMAC-SHA-256 hash of transaction ID with API secret as encryption key
     * */
    public static BitcoinusGetTransactionResponse getTransaction(final String transactionId)
            throws URISyntaxException, IOException, NoSuchAlgorithmException, InvalidKeyException {

        // https://pay.bitcoinus.io/api/v1/getTransaction

        // signature​ - HMAC-SHA-256 hash of transaction ID with API secret as encryption key
        String signature = CryptoUtils.hmacSHA256(transactionId, bitcoinusApiSecretKey);

        final URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("pay.bitcoinus.io")
                .setPath("/api/v1/getTransaction")
                .setParameter("id", transactionId)
                .setParameter("clientid", clientid)
                .setParameter("signature", signature)
                .build();
        // java.net.URL : https://docs.oracle.com/javase/8/docs/api/index.html?java/net/URL.html
        final URL url = uri.toURL();

        final String httpResponseContentString = HttpTools.urlFetchGetHttpContentString(url);

        LOG.warning("getTransaction result for transactionId " + transactionId + ": " + httpResponseContentString);

        if (httpResponseContentString.equalsIgnoreCase("Invalid transaction ID.")
                || httpResponseContentString.contains("Invalid transaction ID")
        ) {
            throw new IllegalArgumentException("Invalid transaction ID");
        }

        /*
         * @JsonProperty("signature")
         * public String signature;
         * @JsonProperty("response")
         * public String response;
         * */
        SignedResponceObject signedResponceObject = new ObjectMapper().readValue(
                httpResponseContentString,
                SignedResponceObject.class
        );

        signedResponceObject.verifySignature(bitcoinusApiSecretKey);

        BitcoinusGetTransactionResponse response = new ObjectMapper().readValue(
                signedResponceObject.response,
                BitcoinusGetTransactionResponse.class
        );

        return response;
    }

    public static HashMap<String, Double> getBalance(final String currency)
            throws URISyntaxException, IOException, NoSuchAlgorithmException, InvalidKeyException {

        // https://pay.bitcoinus.io/api/v1/getBalance
        //
        // currency="all"
        // clientid=" "
        // signature=" "
        // curl -X GET -H "Accept: application/json" "https://pay.bitcoinus.io/api/v1/getBalance?currency=${currency}&clientid=${clientid}&signature=${signature}"

        // String clientid = ApiKeysUtils.getApiKey("bitcoinusApiClientId");
        // String bitcoinusApiSecretKey = ApiKeysUtils.getApiKey("bitcoinusApiSecretKey");

        // String currency = "all";

        // signature - ​HMAC-SHA-256 hash of (string) currency+clientid with API secret as encryption key
        String signature = CryptoUtils.hmacSHA256(currency + clientid, bitcoinusApiSecretKey);

        final URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("pay.bitcoinus.io")
                .setPath("/api/v1/getBalance")
                .setParameter("currency", currency)
                .setParameter("clientid", clientid)
                .setParameter("signature", signature)
                .build();
        // java.net.URL : https://docs.oracle.com/javase/8/docs/api/index.html?java/net/URL.html
        final URL url = uri.toURL();

        final String httpResponseContentString = HttpTools.urlFetchGetHttpContentString(url);

        SignedResponceObject signedResponceObject = new ObjectMapper().readValue(
                httpResponseContentString,
                SignedResponceObject.class
        );

        signedResponceObject.verifySignature(bitcoinusApiSecretKey);

        GetBalanceResponse response = new ObjectMapper().readValue(
                signedResponceObject.response,
                GetBalanceResponse.class
        );

        return response.balances;
    }

    /*
     * Merchant API reference : POST/GET request to /api/v1/command
     * POST sendFunds
     * Send funds
     * PARAMETERS:
     * amount​ - Amount
     * currency - ​currency code
     * address​ - Recipient account number or crypto address
     * name (required for FIAT transfers only)​ - Recipient name
     * reference (optional)​ - Reference (sent as Destination Tag or Memo if used with crypto)
     * clientid - ​Client ID
     * signature​ - HMAC-SHA-256 hash of (string) amount+address with API secret as encryption key
     * */
    public static SendFundsResponse sendFunds(
            final Long amount,
            final String currency,
            final String address,
            final String name,
            final String reference
    )
            throws URISyntaxException, IOException, NoSuchAlgorithmException, InvalidKeyException {

        TelegramBot.sendMessageToAdminsChat(
                "Sending payment:\n"
                        + "to: " + name + "\n"
                        + "account: " + address + "\n"
                        + "amount: " + currency + " " + amount + "\n"
                        + "reference: " + reference
        );

        String signature = CryptoUtils.hmacSHA256(amount + address, bitcoinusApiSecretKey);

        final URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("pay.bitcoinus.io")
                // .setPath("/api/v1/sendFunds")
                .setPath(Constants.sendFundsPath) // ("/api/v1/sendTestFunds") // < for testing
                .build();
        // java.net.URL : https://docs.oracle.com/javase/8/docs/api/index.html?java/net/URL.html
        final URL url = uri.toURL();

        StringBuffer payload = new StringBuffer();
        payload.append("&").append("amount").append("=").append(amount);
        payload.append("&").append("currency").append("=").append(currency);
        payload.append("&").append("address").append("=").append(address);
        payload.append("&").append("name").append("=").append(name);
        payload.append("&").append("reference").append("=").append(reference);
        payload.append("&").append("clientid").append("=").append(clientid);
        payload.append("&").append("signature").append("=").append(signature);

        HTTPResponse httpResponse = HttpTools.makePostRequest(
                url.toString(), // TODO:
                payload.toString()
        );

        String httpResponseContentString = new String(
                httpResponse.getContent(),
                StandardCharsets.UTF_8
        );

        final Integer httpResponseCode = httpResponse.getResponseCode();

        if (httpResponseCode != 200) {
            throw new IOException(
                    "[sendFunds Error] "
                            + url.getHost()
                            + " sent response : "
                            + httpResponse.getResponseCode()
                            + " "
                            + httpResponseContentString
            );
        }

        SignedResponceObject signedResponceObject = new ObjectMapper().readValue(
                httpResponseContentString,
                SignedResponceObject.class
        );

        LOG.warning("signedResponceObject.response:");
        LOG.warning(signedResponceObject.response);

        signedResponceObject.verifySignature(bitcoinusApiSecretKey);

        SendFundsResponse sendFundsResponse = new ObjectMapper().readValue(
                signedResponceObject.response,
                SendFundsResponse.class
        );

        LOG.warning("sendFundsResponse : " + sendFundsResponse);

        return sendFundsResponse;
    }

}
