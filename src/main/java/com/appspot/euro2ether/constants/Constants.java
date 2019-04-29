package com.appspot.euro2ether.constants;

import com.google.api.server.spi.Constant;
import com.google.appengine.api.utils.SystemProperty;

/**
 * Contains the client IDs and scopes for allowed clients consuming your API.
 */
public class Constants {

    public static final Boolean production = Boolean.TRUE; //

    // ---- GAE
    // https://console.cloud.google.com/apis/credentials/oauthclient?project=euro2ether
    public static final String WEB_CLIENT_ID = "717784656331-tfnmot2b2q1d906bufafjn7rhi6i1c5c.apps.googleusercontent.com";
    public static final String EMAIL_SCOPE = Constant.API_EMAIL_SCOPE;
    public static final String API_EXPLORER_CLIENT_ID = Constant.API_EXPLORER_CLIENT_ID;
    //
    public static final String applicationId = SystemProperty.applicationId.get();
    public static final String GAE_PROJECT_DOMAIN = applicationId + ".appspot.com";
    public final static String GAE_PROJECT_URL = "https://" + applicationId + ".appspot.com";
    public final static String CLOUDSTORAGE_DEFAULT_BUCKET = GAE_PROJECT_DOMAIN;

    // --- Infura
    // public static final String ethereumNetworkName = "ropsten";
    public static final String ethereumNetworkName = "mainnet";
    public static final String etherscanLinkPrefix = "https://etherscan.io/";
    public static final String xEuroSmartContractAddress = "0xe577e0B200d00eBdecbFc1cd3F7E8E04C70476BE";
    public static final String infuraApiVersion = "v3";

    // --- Bitcoinus
    public static final int paymentReferenceStrLength = 7;
    // public static final String paymentReferenceStringPrefix = "(@";
    public static final String paymentReferenceStringSuffix = "(x)";
    public static final String ourPaymentReferenceOnBitcoinus = "3x1176x";
    // public static final String sendFundsPath = "/api/v1/sendTestFunds";
    public static final String sendFundsPath = "/api/v1/sendFunds";
    public static final Integer commissionIn = 10; // 1%
    public static final Integer minCommissionIn = 1; // 1 EUR
    public static final Integer commissionOut = 10; // 1%
    public static final Integer minCommissionOut = 1; // 1 EUR
    public static final Integer minAmountToExchange = 12; //

    // --- Email
    public static final String emailSubjectPrefix = "[xEUR]";
    public static final String emailFrom = "admin@xeuro.online";

}
