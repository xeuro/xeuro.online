package com.appspot.euro2ether.api;

import com.appspot.euro2ether.constants.Constants;
import com.appspot.euro2ether.entities.*;
import com.appspot.euro2ether.inputs.CheckEthereumSignatureRequest;
import com.appspot.euro2ether.returns.CommissionsReturn;
import com.appspot.euro2ether.returns.UserData;
import com.appspot.euro2ether.returns.cryptonomica.PromoCode;
import com.appspot.euro2ether.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.users.User;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.IBANValidator;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import static com.appspot.euro2ether.service.UserTools.isKeyCertificateValid;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * explore on: {project name}.appspot.com/_ah/api/explorer
 * ! - API should be registered in  web.xml (<param-name>services</param-name>)
 * ! - on frontend API should be loaded in app.js - app.run()
 */

@Api(
        name = "usersAPI", // The api name must match '[a-z]+[A-Za-z0-9]*'
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constants.WEB_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID},
        description = "api to manage user data"
)
public class UsersAPI {

    private static final Logger LOG = Logger.getLogger(UsersAPI.class.getName());
    private static final Gson GSON = new Gson();

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "getMyUserDataFromServer",
            path = "getMyUserDataFromServer",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public UserData getMyUserDataFromServer(
            // final HttpServletRequest httpServletRequest,
            final User googleUser
    ) throws UnauthorizedException {

        if (googleUser == null) {
            throw new UnauthorizedException("user not logged in");
        }

        UserData userData = new UserData();

        /* --- AppUser : */

        // https://github.com/objectify/objectify/wiki/BasicOperations#obtaining-an-objectify-instance
        Key<AppUser> appUserKey = Key.create(AppUser.class, googleUser.getUserId());

        AppUser appUser = ofy().load().key(appUserKey).now(); // .now() return null if not found, .safe() throws NotFoundException

        if (appUser == null) {
            appUser = new AppUser(googleUser);
            appUser.setEntityCreatedOn(new Date());
            ofy().save().entity(appUser).now();
        }

        userData.setAppUser(appUser);

        /* --- CryptonomicaPGPPublicKeyAPIView : */

        CryptonomicaPGPPublicKeyAPIView pgpPublicKeyAPIView = null;

        if (appUser.getPgpFingerprint() != null) {
            Key<CryptonomicaPGPPublicKeyAPIView> cryptonomicaPGPPublicKeyAPIViewKey = Key.create(CryptonomicaPGPPublicKeyAPIView.class, appUser.getPgpFingerprint());
            pgpPublicKeyAPIView = ofy().load().key(cryptonomicaPGPPublicKeyAPIViewKey).now();
        }
        userData.setPgpPublicKeyAPIView(pgpPublicKeyAPIView);

        return userData;
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "receivePromoCodeFromCryptonomica",
            path = "receivePromoCodeFromCryptonomica",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public PromoCode receivePromoCodeFromCryptonomica(
            // final HttpServletRequest httpServletRequest,
            final User googleUser
    ) throws UnauthorizedException, URISyntaxException, IOException {

        if (googleUser == null) {
            throw new UnauthorizedException("user not logged in");
        }

        // String cryptonomicaServiceName = ApiKeysUtils.getApiKey("cryptonomicaServiceName");
        final String cryptonomicaApiKeyString = ApiKeysUtils.getApiKey("cryptonomicaApiKeyString");

        final URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("cryptonomica-server.appspot.com")
                .setPath("_ah/api/stripePaymentsAPI/v1/createPromoCodeWithApiKey")
                .setParameter("serviceName", "xeuro")
                .setParameter("apiKeyString", cryptonomicaApiKeyString)
                .build();
        // java.net.URL : https://docs.oracle.com/javase/8/docs/api/index.html?java/net/URL.html
        final URL url = uri.toURL();

        HashMap<String, String> postRequestParameters = new HashMap<>();
        postRequestParameters.put("serviceName", "xeuro");
        postRequestParameters.put("apiKeyString", cryptonomicaApiKeyString);
        HTTPResponse httpResponse = HttpTools.makePostRequestWithParametersMap(
                "https://cryptonomica-server.appspot.com/_ah/api/stripePaymentsAPI/v1/createPromoCodeWithApiKey",
                postRequestParameters
        );

        String httpResponseContentString = new String(
                httpResponse.getContent(),
                StandardCharsets.UTF_8
        );

        LOG.warning("cryptonomica responce to promo code request:");
        LOG.warning(httpResponseContentString);

        if (httpResponse.getResponseCode() != 200) {
            throw new IOException(
                    "[error] "
                            + url.getHost()
                            + " sent responce : "
                            + httpResponse.getResponseCode()
                            + " "
                            + httpResponseContentString
            );
        }

        PromoCode promoCode = new ObjectMapper().readValue(
                httpResponseContentString,
                PromoCode.class
        );

        LOG.warning(promoCode.toString());

        return promoCode;
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "checkFingerprintOnCryptonomica",
            path = "checkFingerprintOnCryptonomica",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public UserData checkFingerprintOnCryptonomica(
            // final HttpServletRequest httpServletRequest,
            final User googleUser,
            final @Named("fingerprint") String fingerprint
    ) throws UnauthorizedException, URISyntaxException, IOException {

        if (googleUser == null) {
            throw new UnauthorizedException("user not logged in");
        }

        UserData userData = new UserData();

        // String cryptonomicaServiceName = ApiKeysUtils.getApiKey("cryptonomicaServiceName");
        final String cryptonomicaApiKeyString = ApiKeysUtils.getApiKey("cryptonomicaApiKeyString");


        final URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("cryptonomica-server.appspot.com")
                .setPath("/_ah/api/pgpPublicKeyAPI/v1/getPGPPublicKeyByFingerprintWithApiKey")
                .setParameter("serviceName", "xeuro")
                .setParameter("apiKeyString", cryptonomicaApiKeyString)
                .setParameter("fingerprint", fingerprint)
                .build();
        // java.net.URL : https://docs.oracle.com/javase/8/docs/api/index.html?java/net/URL.html
        final URL url = uri.toURL();

        // debug:
        LOG.warning("url: " + url.toString());

        String httpResponseContentString = HttpTools.urlFetchGetHttpContentString(url);

        LOG.warning("httpResponseContentString from Cryptonomica:");
        LOG.warning(httpResponseContentString);

        CryptonomicaPGPPublicKeyAPIView pgpPublicKeyAPIView = null;

        try {

            pgpPublicKeyAPIView = new ObjectMapper().readValue(
                    httpResponseContentString,
                    CryptonomicaPGPPublicKeyAPIView.class
            );

            if (pgpPublicKeyAPIView == null) {
                throw new IOException("OpenPGP public key data can not be read");
            }

            LOG.warning("pgpPublicKeyAPIView.toString()");
            LOG.warning(pgpPublicKeyAPIView.toString());

        } catch (Exception e) {

            LOG.severe(e.getMessage());
            throw new IOException("Error processing OpenPGP public key");

        }

        if (!googleUser.getEmail().equalsIgnoreCase(pgpPublicKeyAPIView.getUserEmail())) {
            throw new UnauthorizedException(
                    "You are not allowed to use fingerprint "
                            + fingerprint
                            + ", because key with this fingerprint has user id with another email address");
        }

        boolean keyVerifiedOnline = false;
        if (pgpPublicKeyAPIView.getVerifiedOnline() != null && pgpPublicKeyAPIView.getVerifiedOnline()) {
            keyVerifiedOnline = true;
        }

        boolean keyVerifiedOffline = false;
        if (pgpPublicKeyAPIView.getVerifiedOffline() != null && pgpPublicKeyAPIView.getVerifiedOffline()) {
            keyVerifiedOffline = true;
        }

        if (!keyVerifiedOnline && !keyVerifiedOffline) {
            throw new UnauthorizedException(
                    "key " + pgpPublicKeyAPIView.getFingerprint() + " is not yet verified on cryptonomica.net"
            );
        }

        if (pgpPublicKeyAPIView.getExp().before(new Date())) {
            throw new IllegalArgumentException("Key " + fingerprint + " is expired");
        }

        Boolean isRevoked = false;
        try {
            if (pgpPublicKeyAPIView.getRevoked()) {
                isRevoked = true;
            }
        } catch (NullPointerException e) {
            LOG.warning(e.getMessage());
        }

        if (isRevoked) {
            throw new IllegalArgumentException("Key " + fingerprint + " is revoked");
        }

        /* --- AppUser : */

        // https://github.com/objectify/objectify/wiki/BasicOperations#obtaining-an-objectify-instance

        AppUser appUser = ofy().load().key(
                Key.create(AppUser.class, googleUser.getUserId())
        ).now(); // .now() return null if not found, .safe() throws NotFoundException

        if (appUser == null) {
            appUser = new AppUser(googleUser);
            appUser.setEntityCreatedOn(new Date());
            ofy().save().entity(appUser).now();
        }

        userData.setAppUser(appUser);

        /* --- CryptonomicaPGPPublicKeyAPIView : */

        if (appUser.getPgpFingerprint() != null && !appUser.getPgpFingerprint().equalsIgnoreCase(fingerprint)) {
            LOG.warning("User changed fingerprint from " + appUser.getPgpFingerprint() + " to " + fingerprint);
            if (appUser.getOldPgpFingerprints() == null) {
                appUser.setOldPgpFingerprints(new ArrayList<>());
            }
            appUser.addToOldPgpFingerprints(appUser.getPgpFingerprint());
        }

        appUser.setPgpFingerprint(fingerprint);
        appUser.setEntityChangedOn(new Date());

        LOG.warning("appUser:");
        LOG.warning(appUser.toString());

        ofy().save().entity(appUser).now();
        ofy().save().entity(pgpPublicKeyAPIView).now();

        /* --- UserData: */

        userData.setPgpPublicKeyAPIView(pgpPublicKeyAPIView);

        LOG.warning("userData:");
        LOG.warning(userData.toString());

        return userData;
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "requestCodeForEthereumAddressVerification",
            path = "requestCodeForEthereumAddressVerification",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public HashMap<String, String> requestCodeForEthereumAddressVerification(
            // final HttpServletRequest httpServletRequest,
            final User googleUser
    ) throws UnauthorizedException {

        if (googleUser == null) {
            throw new UnauthorizedException("user not logged in");
        } else if (!isKeyCertificateValid(googleUser)) {
            throw new IllegalStateException("User " + googleUser.getEmail() + " has no valid key certificate from Cryptonomica");
        }

        HashMap<String, String> result = new HashMap<>();

        String code = RandomStringUtils.randomAlphanumeric(21);

        CodeForEthereumAddressVerification codeForEthereumAddressVerification = new CodeForEthereumAddressVerification(
                code,
                null,
                googleUser,
                new Date()
        );
        ofy().save().entity(codeForEthereumAddressVerification).now();

        result.put("code", code);

        LOG.warning(result.toString());

        return result;

    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "checkEthereumSignature",
            path = "checkEthereumSignature",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public UserData checkEthereumSignature(
            // final HttpServletRequest httpServletRequest,
            final User googleUser,
            final CheckEthereumSignatureRequest checkEthereumSignatureRequest
    ) throws UnauthorizedException {

        LOG.warning("checkEthereumSignatureRequest object:");
        LOG.warning(checkEthereumSignatureRequest.toString());

        if (googleUser == null) {
            throw new UnauthorizedException("user not logged in");
        } else if (!isKeyCertificateValid(googleUser)) {
            throw new IllegalStateException("User " + googleUser.getEmail() + " has no valid key certificate from Cryptonomica");
        } else if (!org.web3j.crypto.WalletUtils.isValidAddress(checkEthereumSignatureRequest.getEthereumAddress())) {
            throw new IllegalArgumentException(checkEthereumSignatureRequest.getEthereumAddress() + " is not a valid Ethereum address");
        } else if (!ECRecover.verifyEthereumSignature(checkEthereumSignatureRequest)) {
            throw new IllegalArgumentException("Signature check failed");
        }

        /* --- check checkEthereumSignatureRequest */
        final String storedCode = checkEthereumSignatureRequest.getSignedString(); // if prefix used, remove prefix from signed string
        CodeForEthereumAddressVerification codeForEthereumAddressVerification = ofy()
                .load()
                .key(Key.create(CodeForEthereumAddressVerification.class, storedCode))
                .now();

        // check if user provided signature for string sent to this user
        if (!codeForEthereumAddressVerification.getIssuedFor().equalsIgnoreCase(googleUser.getUserId())) {
            throw new IllegalArgumentException("Signed string is not valid for user " + googleUser.getEmail());
        }

        // check if string to sign sent in last our, and not before
        final long oneHourInMilliseconds = (60 * 60) * 1000L;
        if (new Date().getTime() - codeForEthereumAddressVerification.getIssuedOn().getTime() > oneHourInMilliseconds) {
            throw new IllegalStateException("Verification string is to old, please request a new verification string to sign");
        }

        codeForEthereumAddressVerification.setVerifiedForEthereumAddress(checkEthereumSignatureRequest.getSignedString());
        codeForEthereumAddressVerification.setVerifiedOn(new Date());
        ofy().save().entity(codeForEthereumAddressVerification); // async

        /* --- update AppUser entity: */

        AppUser appUser = UserTools.getAppUser(googleUser);
        if (appUser.getEthAddress() != null && !appUser.getEthAddress().equalsIgnoreCase(checkEthereumSignatureRequest.getEthereumAddress())) {
            if (appUser.getOldEthAddresses() == null) {
                appUser.setOldEthAddresses(new ArrayList<>());
            }
            appUser.addToOldEthAddresses(appUser.getEthAddress());
        }
        appUser.setEthAddress(checkEthereumSignatureRequest.getEthereumAddress().toLowerCase());

        // create payment reference entity and save to DB:

        String paymentReferenceStr;

        do {
            paymentReferenceStr =
                    Constants.ourPaymentReferenceOnBitcoinus
                            + RandomStringUtils.randomAlphanumeric(Constants.paymentReferenceStrLength).toLowerCase()
                            + Constants.paymentReferenceStringSuffix;
        } while (
            // check if this payment reference string exists (should not)
                ofy().load().key(Key.create(PaymentReference.class, paymentReferenceStr)).now() != null
        );

        LOG.warning(
                "payment reference for "
                        + checkEthereumSignatureRequest.getEthereumAddress() + " (" + googleUser.getEmail() + "):"
                        + paymentReferenceStr
        );
        if (appUser.getPaymentReference() != null && !appUser.getPaymentReference().equalsIgnoreCase(paymentReferenceStr)) {
            if (appUser.getOldPaymentReferences() == null) {
                appUser.setOldPaymentReferences(new ArrayList<>());
            }
            appUser.addToOldPaymentReferences(appUser.getPaymentReference());
        }
        PaymentReference paymentReference = new PaymentReference(
                paymentReferenceStr,
                checkEthereumSignatureRequest.getEthereumAddress().toLowerCase(),
                googleUser,
                new Date()
        );
        ofy().save().entity(paymentReference).now();

        // add payment reference to AppUser entity and save
        appUser.setPaymentReference(paymentReferenceStr);
        appUser.setEntityChangedOn(new Date());
        ofy().save().entity(appUser).now();

        /* --- create and save EthereumAddress entity */
        EthereumAddress ethereumAddress = new EthereumAddress(
                checkEthereumSignatureRequest.getEthereumAddress().toLowerCase(), // < lower case
                googleUser,
                paymentReferenceStr,
                new Date()
        );
        if (appUser.getIBAN() != null) {
            ethereumAddress.setIBAN(appUser.getIBAN());
        }
        if (appUser.getIbanAccountOwnerName() != null) {
            ethereumAddress.setIbanAccountOwnerName(appUser.getIbanAccountOwnerName());
        }
        ofy().save().entity(ethereumAddress).now();

        // get from DB:
        final UserData userData = UserTools.getUserData(googleUser);

        return userData;
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "addIBAN",
            path = "addIBAN",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public UserData addIBAN(
            // final HttpServletRequest httpServletRequest,
            final User googleUser,
            final @Named("IBAN") String IBAN,
            final @Named("accountOwnerName") String accountOwnerName
    ) throws UnauthorizedException {

        if (googleUser == null) {
            throw new UnauthorizedException("user not logged in");
        } else if (!isKeyCertificateValid(googleUser)) {
            throw new IllegalStateException("User " + googleUser.getEmail() + " has no valid key certificate from Cryptonomica");
        }

        /* --- validate IBAN */
        // https://en.wikipedia.org/wiki/International_Bank_Account_Number
        // https://commons.apache.org/proper/commons-validator/apidocs/index.html?org/apache/commons/validator/routines/IBANValidator.html
        IBANValidator ibanValidator = org.apache.commons.validator.routines.IBANValidator.getInstance();
        if (!ibanValidator.isValid(IBAN)) {
            throw new IllegalArgumentException(IBAN + " is not a valid IBAN");
        }

        if (!SEPA.isSEPA(IBAN)) {
            throw new IllegalArgumentException(IBAN + " not belongs to bank from SEPA country");
        }

        if (accountOwnerName == null || accountOwnerName.isEmpty()) {
            throw new IllegalArgumentException("Account owner name should not be empty");
        }

        /* --- update AppUser entity: */

        AppUser appUser = UserTools.getAppUser(googleUser);

        if (appUser.getIBAN() != null) {

            if (appUser.getOldIBANs() == null) {
                appUser.setOldIBANs(new ArrayList<>());
            }

            if (appUser.getOldAccountOwnerNames() == null) {
                appUser.setOldAccountOwnerNames(new ArrayList<>());
            }

            appUser.addToOldIBANs(appUser.getIBAN());
            appUser.addToOldAccountOwnerNames(appUser.getIbanAccountOwnerName());
        }

        appUser.setIBAN(IBAN);
        appUser.setIbanAccountOwnerName(accountOwnerName);
        appUser.setEntityChangedOn(new Date());
        ofy().save().entity(appUser).now();

        /* --- update EthereumAddress entity */

        EthereumAddress ethereumAddress = ofy()
                .load()
                .key(Key.create(EthereumAddress.class, appUser.getEthAddress().toLowerCase()))
                .now();
        ethereumAddress.setIBAN(IBAN);
        ethereumAddress.setIbanAccountOwnerName(accountOwnerName);
        ethereumAddress.setChangedOn(new Date());
        ofy().save().entity(ethereumAddress).now();

        /* --- get updated UserData object */
        final UserData userData = UserTools.getUserData(googleUser);

        LOG.warning(userData.toString());

        return userData;
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "getBalance",
            path = "getBalance",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public HashMap<String, Double> getEURBalance(
            // final HttpServletRequest request,
            // final User googleUser, // (!!!) public method <<
            // final @Named("currency") String currency // ​currency code (“all” for all currencies balances)
    ) throws UnauthorizedException, IOException, NoSuchAlgorithmException, InvalidKeyException, URISyntaxException {

        HashMap<String, Double> balance = BitcoinusService.getBalance("EUR");

        // TelegramBot.sendMessageToAdminsChat("current balance: ");
        // TelegramBot.sendMessageToAdminsChat(GSON.toJson(balance));

        return balance;

    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "getCommissions",
            path = "getCommissions",
            httpMethod = ApiMethod.HttpMethod.GET
    ) // https://euro2ether.appspot.com/_ah/api/usersAPI/v1/getCommissions
    public CommissionsReturn getCommissions(
            // final HttpServletRequest request,
            // final User googleUser, // (!!!) public method <<
            // final @Named("currency") String currency // ​currency code (“all” for all currencies balances)
    ) {

        CommissionsReturn result = new CommissionsReturn();

        result.setCommissionIn(Constants.commissionIn.floatValue() / 10);
        result.setMinCommissionIn(Constants.minCommissionIn);
        result.setCommissionOut(Constants.commissionOut.floatValue() / 10);
        result.setMinCommissionOut(Constants.minCommissionOut);
        result.setMinAmountToExchange(Constants.minAmountToExchange);

        LOG.warning(result.toString());

        return result;

    }

}
