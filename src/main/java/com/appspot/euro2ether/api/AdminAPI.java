package com.appspot.euro2ether.api;

import com.appspot.euro2ether.constants.Constants;
import com.appspot.euro2ether.entities.AppSettings;
import com.appspot.euro2ether.entities.BitcoinusDeposit;
import com.appspot.euro2ether.entities.TokensInEvent;
import com.appspot.euro2ether.returns.MintTokensEventStruct;
import com.appspot.euro2ether.returns.StringWrapper;
import com.appspot.euro2ether.returns.bitcoinus.SendFundsResponse;
import com.appspot.euro2ether.service.*;
import com.appspot.euro2ether.service.objects.BitcoinusGetTransactionResponse;
import com.appspot.euro2ether.service.objects.EmailMessage;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.utils.SystemProperty;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * explore on: {project name}.appspot.com/_ah/api/explorer
 * ! - API should be registered in  web.xml (<param-name>services</param-name>)
 * ! - on frontend API should be loaded in app.js - app.run()
 */
@Api(
        name = "adminAPI", // The api name must match '[a-z]+[A-Za-z0-9]*'
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constants.WEB_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID},
        description = "functions for admins"
)
public class AdminAPI {

    /* --- Logger: */
    private static final Logger LOG = Logger.getLogger(AdminAPI.class.getName());

    /* --- Gson */
    private static final Gson GSON = new Gson();

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "getBalance",
            path = "getBalance",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public HashMap<String, Double> getBalance(
            // final HttpServletRequest request,
            final User googleUser,
            final @Named("currency") String currency // ​currency code (“all” for all currencies balances)
    ) throws UnauthorizedException, IOException, NoSuchAlgorithmException, InvalidKeyException, URISyntaxException {

        if (!UserTools.isAdmin(googleUser)) {
            throw new UnauthorizedException("[ERROR] User is not admin");
        }

        HashMap<String, Double> balance = BitcoinusService.getBalance(currency);

        TelegramBot.sendMessageToAdminsChat("current balance: ");
        TelegramBot.sendMessageToAdminsChat(GSON.toJson(balance));

        return balance;

    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "getBalanceAll",
            path = "getBalanceAll",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public HashMap<String, Double> getBalanceAll(
            // final HttpServletRequest request,
            final User googleUser
    ) throws UnauthorizedException, IOException, NoSuchAlgorithmException, InvalidKeyException, URISyntaxException {

        if (!UserTools.isAdmin(googleUser)) {
            throw new UnauthorizedException("[ERROR] User is not admin");
        }

        // (“all” for all currencies balances)
        HashMap<String, Double> balance = BitcoinusService.getBalance("all");
        TelegramBot.sendMessageToAdminsChat("current balances: ");
        TelegramBot.sendMessageToAdminsChat(GSON.toJson(balance));

        return balance;
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "getTransaction",
            path = "getTransaction",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public BitcoinusGetTransactionResponse getTransaction(
            // final HttpServletRequest request,
            final User googleUser,
            final @Named("transactionId") String transactionId
    ) throws UnauthorizedException, IOException, NoSuchAlgorithmException, InvalidKeyException, URISyntaxException, IllegalArgumentException {

        if (!UserTools.isAdmin(googleUser)) {
            throw new UnauthorizedException("[ERROR] User is not admin");
        }

        return BitcoinusService.getTransaction(transactionId);
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "fiatInPaymentsToMintTokensEvent",
            path = "fiatInPaymentsToMintTokensEvent",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public MintTokensEventStruct fiatInPaymentsToMintTokensEvent(
            // final HttpServletRequest request,
            final User googleUser,
            final @Named("transactionId") Integer transactionId
    ) throws Exception {

        if (!UserTools.isAdmin(googleUser)) {
            throw new UnauthorizedException("[ERROR] User is not admin");
        }

        return SmartContractService.getMintTokensEventStruct(transactionId);
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "sendFunds",
            path = "sendFunds",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public SendFundsResponse sendFunds(
            // final HttpServletRequest request,
            final User googleUser,
            final @Named("amount") Long amount,
            final @Named("currency") String currency,
            final @Named("address") String address,
            final @Named("name") String name,
            final @Named("reference") String reference
    ) throws UnauthorizedException, IOException, NoSuchAlgorithmException, InvalidKeyException, URISyntaxException {

        if (!UserTools.isAdmin(googleUser)) {
            throw new UnauthorizedException("[ERROR] User is not admin");
        }

        SendFundsResponse sendFundsResponse = BitcoinusService.sendFunds(
                amount,
                currency,
                address,
                name,
                reference
        );

        TelegramBot.sendMessageToAdminsChat(sendFundsResponse.toString());

        return sendFundsResponse;
    }


    @SuppressWarnings("unused")
    @ApiMethod(
            name = "networkInfo",
            path = "networkInfo",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public HashMap<String, String> networkInfo(
            // final HttpServletRequest request,
            final User googleUser
    ) throws UnauthorizedException, IOException, URISyntaxException {

        if (!UserTools.isAdmin(googleUser)) {
            throw new UnauthorizedException("[ERROR] User is not admin");
        }

        HashMap<String, String> result = new HashMap<>();

        if (SystemProperty.environment != null && SystemProperty.environment.value() != null
                && SystemProperty.version != null
                && SystemProperty.applicationVersion != null
                && SystemProperty.applicationId != null) {
            result.put("SystemProperty.version", SystemProperty.version.get());
            result.put("SystemProperty.applicationVersion", SystemProperty.applicationVersion.get());
            result.put("SystemProperty.applicationId", SystemProperty.applicationId.get());
            result.put("SystemProperty.environment.value()", SystemProperty.environment.value().value());
        }

        // Web3j web3 = Web3jInfura.getWeb3();
        Web3j web3 = Web3jTools.getWeb3jObject();
        //  Web3j web3 = Web3jInfura.getWeb3jObject(Constants.ethereumNetworkName);

        result.put("web3jClientVersion", web3.web3ClientVersion().send().getWeb3ClientVersion());
        result.put("Ethereum network ID", web3.netVersion().send().getNetVersion());
        result.put("eth protocol version", web3.ethProtocolVersion().send().getProtocolVersion());
        result.put("netPeerCount()", web3.netPeerCount().send().getQuantity().toString());

        return result;
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "depositsForRevue",
            path = "depositsForRevue",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public ArrayList<BitcoinusDeposit> depositsForRevue(
            // final HttpServletRequest request,
            final User googleUser
    ) throws UnauthorizedException, IOException, URISyntaxException {

        if (!UserTools.isAdmin(googleUser)) {
            throw new UnauthorizedException("[ERROR] User is not admin");
        }

        ArrayList<BitcoinusDeposit> result = new ArrayList<>();
        List<BitcoinusDeposit> bitcoinusDepositList = ofy().load().type(BitcoinusDeposit.class)
                .filter("needsManualRevue", true)
                .list();
        if (bitcoinusDepositList != null) {
            result = new ArrayList<>(bitcoinusDepositList);
        }

        List<BitcoinusDeposit> noTxList = ofy().load().type(BitcoinusDeposit.class)
                .filter("mintAndTransferTokensTxHash", null)
                .list();

        // bitcoinusDepositList.addAll(noTxList);
        for (BitcoinusDeposit bitcoinusDeposit : noTxList) {
            if (!bitcoinusDepositList.contains(bitcoinusDeposit)) {
                bitcoinusDepositList.add(bitcoinusDeposit);
            }
        }

        LOG.warning(result.toString());

        return result;
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "setDepositConfirmed",
            path = "setDepositConfirmed",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public BitcoinusDeposit setDepositConfirmed(
            // final HttpServletRequest request,
            final User googleUser,
            final @Named("transactionId") Long transactionId,
            final @Named("internalNotes") String internalNotes
    ) throws UnauthorizedException {

        /* --- check authorization */

        if (!UserTools.isAdmin(googleUser)) {
            throw new UnauthorizedException("[ERROR] User is not admin");
        }

        /* --- get entity from DB */

        BitcoinusDeposit bitcoinusDeposit = ofy().load().key(Key.create(BitcoinusDeposit.class, transactionId)).now();

        /* --- change entity and save to DB */
        bitcoinusDeposit.setInternalNotes(internalNotes);
        bitcoinusDeposit.setNeedsManualRevue(Boolean.FALSE);
        bitcoinusDeposit.setTokensTransferConfirmed(Boolean.TRUE);
        bitcoinusDeposit.setConfirmedBy(googleUser);
        bitcoinusDeposit.setEntityChanged(new Date());
        ofy().save().entity(bitcoinusDeposit).now();

        return bitcoinusDeposit;
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "tokensInEventsForRevue",
            path = "tokensInEventsForRevue",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public ArrayList<TokensInEvent> tokensInEventsForRevue(
            // final HttpServletRequest request,
            final User googleUser
    ) throws UnauthorizedException {

        if (!UserTools.isAdmin(googleUser)) {
            throw new UnauthorizedException("[ERROR] User is not admin");
        }

        ArrayList<TokensInEvent> result = new ArrayList<>();

        List<TokensInEvent> tokensInEventList = ofy().load().type(TokensInEvent.class)
                .filter("needsManualRevue", true)
                .list();

        if (tokensInEventList != null) {
            result = new ArrayList<>(tokensInEventList);
        }

        LOG.warning(result.toString());

        return result;
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "setTokensInEventConfirmed",
            path = "setTokensInEventConfirmed",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public TokensInEvent setTokensInEventConfirmed(
            // final HttpServletRequest request,
            final User googleUser,
            final @Named("tokensInEventsCounter") Long tokensInEventsCounter,
            final @Named("internalNotes") String internalNotes
    ) throws UnauthorizedException, IOException, URISyntaxException {

        /* --- check authorization */

        if (!UserTools.isAdmin(googleUser)) {
            throw new UnauthorizedException("[ERROR] User is not admin");
        }

        /* --- get entity from DB */

        TokensInEvent tokensInEvent = ofy().load().key(Key.create(TokensInEvent.class, tokensInEventsCounter)).now();

        tokensInEvent.setInternalNotes(internalNotes);
        tokensInEvent.setNeedsManualRevue(Boolean.FALSE);
        tokensInEvent.setConfirmed(Boolean.TRUE);
        tokensInEvent.setConfirmedBy(googleUser);
        tokensInEvent.setEntityChanged(new Date());
        ofy().save().entity(tokensInEvent).now();

        LOG.warning(tokensInEvent.toString());

        return tokensInEvent;
    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "addMonitoringServerIP",
            path = "addMonitoringServerIP",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public ArrayList<String> addMonitoringServerIP(
            // final HttpServletRequest request,
            final User googleUser,
            final @Named("monitoringServerIP") String monitoringServerIP
    ) throws UnauthorizedException, IOException, URISyntaxException {

        /* --- check authorization */

        if (!UserTools.isAdmin(googleUser)) {
            throw new UnauthorizedException("[ERROR] User is not admin");
        }

        /* --- get entity from DB */

        AppSettings eventsMonitoringServersIPs = ofy().load().key(Key.create(AppSettings.class, "eventsMonitoringServersIPs")).now();
        if (eventsMonitoringServersIPs == null) {
            eventsMonitoringServersIPs = new AppSettings("eventsMonitoringServersIPs");
            eventsMonitoringServersIPs.setCreatedOn(new Date());
        }

        eventsMonitoringServersIPs.addToStringsArrayList(monitoringServerIP);
        eventsMonitoringServersIPs.setChangedOn(new Date());
        eventsMonitoringServersIPs.setChangedBy(googleUser);

        ofy().save().entity(eventsMonitoringServersIPs).now();

        return ofy().load().key(Key.create(AppSettings.class, "eventsMonitoringServersIPs")).now().getStringsArrayList();

    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "addAppSettings",
            path = "addAppSettings",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public AppSettings addAppSettings(
            // final HttpServletRequest httpServletRequest,
            final User googleUser,
            final @Named("name") String name,
            final @Named("stringValue") @Nullable String stringValue,
            final @Named("integerValue") @Nullable Integer integerValue,
            final @Named("info") @Nullable String info
            // final AppSettings appSettings
    ) throws UnauthorizedException, IllegalArgumentException {

        /* --- check authorization */

        if (!UserTools.isAdmin(googleUser)) {
            throw new UnauthorizedException("[ERROR] User is not admin");
        }

        AppSettings appSettings = ofy().load().key(Key.create(AppSettings.class, name)).now();
        if (appSettings != null) {
            throw new IllegalArgumentException("AppSettings entity with the name '" + name + "' already exists");
        }

        appSettings = new AppSettings(name);
        appSettings.setStringValue(stringValue);
        appSettings.setIntegerValue(integerValue);
        appSettings.setInfo(info);
        appSettings.setCreatedOn(new Date());
        appSettings.setChangedBy(googleUser);

        ofy().save().entity(appSettings).now();
        return ofy().load().key(Key.create(AppSettings.class, name)).now();

    }

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "sendEmailTo",
            path = "sendEmailTo",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public EmailMessage sendEmailTo(
            // final HttpServletRequest httpServletRequest,
            final User googleUser,
            final @Named("emailTO") String emailTO,
            final @Named("emailCC") @Nullable String emailCC,
            final @Named("messageSubject") @Nullable String messageSubject,
            final @Named("messageText") @Nullable String messageText
    ) throws UnauthorizedException, IllegalArgumentException {

        /* --- check authorization */

        if (!UserTools.isAdmin(googleUser)) {
            throw new UnauthorizedException("[ERROR] User is not admin");
        }

        EmailMessage emailMessage = new EmailMessage(
                emailTO,
                emailCC,
                messageSubject,
                messageText
        );

        EmailTools.sendEmail(emailMessage);

        LOG.warning("email message sent: " + emailMessage.toString());

        return emailMessage;

    }

}
