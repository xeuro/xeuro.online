package com.appspot.euro2ether.api;

import com.appspot.euro2ether.constants.Constants;
import com.appspot.euro2ether.contracts.XEuro;
import com.appspot.euro2ether.entities.AppSettings;
import com.appspot.euro2ether.entities.EthereumAddress;
import com.appspot.euro2ether.entities.TokensInEvent;
import com.appspot.euro2ether.inputs.Args;
import com.appspot.euro2ether.inputs.TokensInEventFromServer;
import com.appspot.euro2ether.returns.StringWrapper;
import com.appspot.euro2ether.returns.bitcoinus.SendFundsResponse;
import com.appspot.euro2ether.service.*;
import com.appspot.euro2ether.service.objects.EmailMessage;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * explore on: {project name}.appspot.com/_ah/api/explorer
 * ! - API should be registered in  web.xml (<param-name>services</param-name>)
 * ! - on frontend API should be loaded in app.js - app.run()
 */

@Api(
        name = "eventsAPI", // The api name must match '[a-z]+[A-Za-z0-9]*'
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constants.WEB_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID},
        description = "receive events from smart contracts"
)
public class EventsAPI {

    private static final Logger LOG = Logger.getLogger(EventsAPI.class.getName());
    private static final Gson GSON = new Gson();

    @SuppressWarnings("unused")
    @ApiMethod(
            name = "addEvent",
            path = "addEvent",
            httpMethod = ApiMethod.HttpMethod.POST
    ) // https://euro2ether.appspot.com/_ah/api/eventsAPI/v1/addEvent
    public StringWrapper addEvent(
            final HttpServletRequest httpServletRequest,
            final TokensInEventFromServer event
    ) throws Exception {

        LOG.warning(event.toString());

        ArrayList<String> eventsMonitoringServersIPs = ofy().load().key(Key.create(AppSettings.class, "eventsMonitoringServersIPs")).now().getStringsArrayList();
        String remoteIP = httpServletRequest.getRemoteAddr();

        if (!eventsMonitoringServersIPs.contains(remoteIP)) {
            LOG.severe("request from : " + remoteIP + " This IP is not contained in allowed IPs list: " + GSON.toJson(eventsMonitoringServersIPs));
            throw new UnauthorizedException("access not allowed from " + remoteIP);
        }

        String apiKeyFromHttpServletRequest = httpServletRequest.getHeader("apiKey");

        if (apiKeyFromHttpServletRequest == null || apiKeyFromHttpServletRequest.isEmpty()) {
            LOG.severe("api key required");
            throw new UnauthorizedException("api key required");
        }

        String xEuroNodeApiKey = ApiKeysUtils.getApiKey("xEuroNodeApiKey");

        if (!xEuroNodeApiKey.equals(apiKeyFromHttpServletRequest)) {
            LOG.severe("api key " + apiKeyFromHttpServletRequest + " is invalid");
            throw new UnauthorizedException("api key is invalid");
        }

        LOG.warning(event.toString());


        Args args = event.getArgs();
        if (args == null) {
            throw new Exception("event object does not contain required data");
        }

        Long tokensInEventsCounter = args.getTokensInEventsCounter();
        LOG.warning("tokensInEventsCounter: " + tokensInEventsCounter);

        TelegramBot.sendMessageToAdminsChat(
                ">>>>> Event received:\n"
                        + "event: " + event.getEvent() + "\n"
                        + "Tx: " + Constants.etherscanLinkPrefix + "tx/" + event.getTransactionHash() + "\n"
                        + "tokensInEventsCounter: " + tokensInEventsCounter + "\n"
                        + "value: " + args.getValue() + "\n"
                        + "from: " + Constants.etherscanLinkPrefix + "address/" + args.getFrom() + "\n"
        );

        TokensInEvent tokensInEvent = ofy().load().key(
                Key.create(
                        TokensInEvent.class,
                        tokensInEventsCounter
                )
        ).now(); // > null if not found

        LOG.warning("tokensInEvent: " + String.valueOf(tokensInEvent)); // > null or .toString()

        if (tokensInEvent == null) {

            // we get values from smart contract itself not just from request received
            XEuro xEuro = null;
            Tuple3 tuple3 = null;
            try {

                xEuro = Web3jTools.createXEuroContractInstance();

                tuple3 = xEuro.tokensInTransfer( // mapping(uint256 => TokensInTransfer) public tokensInTransfer;
                        BigDecimal.valueOf(tokensInEventsCounter).toBigInteger()
                ).send();

                // debug only:
                // TelegramBot.sendMessageToAdminsChat("tuple3: " + tuple3.toString());
                // LOG.warning("tuple3: " + tuple3.toString());

            } catch (IOException | URISyntaxException e) {

                TelegramBot.sendMessageToAdminsChat(e.getMessage());
                LOG.severe(e.getMessage());
                throw new Exception(e.getMessage());

            }

                    /* Solidity code:
                    uint public tokensInEventsCounter = 0;//
                    struct TokensInTransfer {// <<< used in 'transfer'
                        address from; //
                        uint value;   //
                        uint receivedOn; // UnixTime
                    }
                    // in transfer function:
                    if (_to == address(this) && _value > 0) {
                    tokensInEventsCounter++;
                    tokensInTransfer[tokensInEventsCounter].from = msg.sender; //
                    tokensInTransfer[tokensInEventsCounter].value = _value; //
                    tokensInTransfer[tokensInEventsCounter].receivedOn = now;
                    emit TokensIn(_from, _value, tokensInEventsCounter);
                    }
                    */

            LOG.warning("tokensInTransfer struct #" + tokensInEventsCounter + ":");
            LOG.warning(tuple3.toString());

            String from = String.valueOf(tuple3.getValue1()).toLowerCase();
            Long value = Long.parseLong(String.valueOf(tuple3.getValue2()));
            // debug:
            LOG.warning(
                    "from: " + from + "\n"
                            + "value: " + value
            );

            tokensInEvent = new TokensInEvent(tokensInEventsCounter);
            tokensInEvent.setEntityCreated(new Date());
            tokensInEvent.setFrom(from.toLowerCase());
            // tokensInEvent.setNetworkId(event.getNetworkId());
            tokensInEvent.setTokensInEventsCounter(tokensInEventsCounter);
            tokensInEvent.setTxHash(event.getTransactionHash());
            tokensInEvent.setValue(value);

            ofy().save().entity(tokensInEvent).now();

            /* --- load Ethereum address entity from DB */
            EthereumAddress ethereumAddress = ofy().load().key(
                    Key.create(
                            EthereumAddress.class,
                            from.toLowerCase()
                    )
            ).now();

            if (ethereumAddress == null) {
                String errorMessage = "Can not made payment for tokens received. Ethereum address : " + from + " is not registered";
                LOG.severe(errorMessage);
                tokensInEvent.setInternalNotes(errorMessage);
                tokensInEvent.setNeedsManualRevue(Boolean.TRUE);
                ofy().save().entity(tokensInEvent).now();

                TelegramBot.sendMessageToAdminsChat(errorMessage);
                throw new Exception(errorMessage);
            }

            if (ethereumAddress.getIBAN() == null) {
                String errorMessage = "Can not made payment for tokens received. No IBAN for Ethereum address : " + from + " registered";
                LOG.severe(errorMessage);
                tokensInEvent.setInternalNotes(errorMessage);
                tokensInEvent.setNeedsManualRevue(Boolean.TRUE);
                ofy().save().entity(tokensInEvent).now();
                TelegramBot.sendMessageToAdminsChat(errorMessage);
                throw new Exception(errorMessage);
            }

            Commission commission = new Commission(
                    Constants.commissionOut,
                    Constants.minCommissionOut
            );
            commission.calculateExchangeCommission(value.intValue());

            String messageToTelegramAdminsChat =
                    "Tokens for exchange to fiat received:\n"
                            // + "network: " + tokensInEvent.getNetworkId() + "\n"
                            + "index number: " + tokensInEvent.getTokensInEventsCounter().toString() + "\n"
                            + "database id: " + tokensInEvent.getId().toString() + " ( " + tokensInEvent.getEntityCreated().toGMTString() + ")\n"
                            + "from address: " + tokensInEvent.getFrom() + " ( " + Constants.etherscanLinkPrefix + "address/" + tokensInEvent.getFrom() + " )\n"
                            + "value: " + tokensInEvent.getValue() + " xEUR \n"
                            + "commission: " + commission.getCommissionSum() + "\n"
                            + "value less commission: " + commission.getAmountFinalSum() + "\n"
                            + "transaction hash: " + tokensInEvent.getTxHash() + " ( " + Constants.etherscanLinkPrefix + "tx/" + tokensInEvent.getTxHash() + " )\n";
            TelegramBot.sendMessageToAdminsChat(messageToTelegramAdminsChat);

            /* ---- send payment */

            try {

                SendFundsResponse sendFundsResponse = BitcoinusService.sendFunds(
                        commission.getAmountFinalSum().longValue(),
                        "EUR",
                        ethereumAddress.getIBAN(),
                        ethereumAddress.getIbanAccountOwnerName(),
                        "xEUR exchange"
                );

                tokensInEvent.setSendFundsTxId(sendFundsResponse.id);
                tokensInEvent.setSendFundsResponse(sendFundsResponse);
                tokensInEvent.setEntityChanged(new Date());
                ofy().save().entity(tokensInEvent).now();

                TelegramBot.sendMessageToAdminsChat(">> payment sent:\n" + sendFundsResponse.toString());

                EmailMessage emailMessage = new EmailMessage();
                emailMessage.setTo(ethereumAddress.getUserEmail());
                emailMessage.setSubject(Constants.emailSubjectPrefix + " payment sent to " + ethereumAddress.getIBAN());
                emailMessage.setMessageText(
                        "Payment EUR " + sendFundsResponse.amount_dst + " sent " + sendFundsResponse.timestamp + " to: \n"
                                + ethereumAddress.getIbanAccountOwnerName() + ", IBAN: " + ethereumAddress.getIBAN() + "\n"
                                + "from Etna Development OÜ. \n\n"
                                + "Description: exchange of xEUR virtual currency to fiat EUR \n"
                );
                EmailTools.sendEmail(emailMessage);

                /* ---- burn tokens */

                TransactionReceipt transactionReceipt = null;
                try {

                    transactionReceipt = xEuro.burnTokens(
                            BigDecimal.valueOf(value).toBigInteger(),
                            BigDecimal.valueOf(tokensInEvent.getId()).toBigInteger(),
                            BigDecimal.valueOf(sendFundsResponse.id).toBigInteger()
                    ).send();

                    TelegramBot.sendMessageToAdminsChat(">>>> Burn tokens tx:\n"
                            + Constants.etherscanLinkPrefix + "tx/" + transactionReceipt.getTransactionHash()
                    );

                    tokensInEvent.setBurnTokensTxReceipt(transactionReceipt);
                    if (!transactionReceipt.getStatus().equalsIgnoreCase("0x1")) {
                        tokensInEvent.setNeedsManualRevue(Boolean.TRUE);
                        tokensInEvent.setEntityChanged(new Date());
                    }

                } catch (Exception e) {

                    LOG.severe(e.getMessage());
                    TelegramBot.sendMessageToAdminsChat("burnTokens tx error:\n"
                            + e.getMessage()
                    );
                    tokensInEvent.setInternalNotes(
                            tokensInEvent.getInternalNotes() + " > Tokens not burned"
                    );
                    tokensInEvent.setNeedsManualRevue(Boolean.TRUE);
                    tokensInEvent.setEntityChanged(new Date());
                    ofy().save().entity(tokensInEvent).now();
                }

            } catch (Exception e) {

                tokensInEvent.setNeedsManualRevue(Boolean.TRUE);
                tokensInEvent.setInternalNotes(e.getMessage());

                LOG.severe(e.getMessage());
                TelegramBot.sendMessageToAdminsChat(e.getMessage());
                if (tokensInEvent != null) {
                    tokensInEvent.setEntityChanged(new Date());
                    ofy().save().entity(tokensInEvent).now();
                }
                throw new Exception(e.getMessage());
            }

        } else { // tokensInEvent != null > already exists

            String message = "payment for tokensIn event #" + tokensInEventsCounter.toString() + " already requested";
            TelegramBot.sendMessageToAdminsChat(message);
            LOG.severe(message);

            if (tokensInEvent != null) {
                tokensInEvent.setEntityChanged(new Date());
                ofy().save().entity(tokensInEvent).now();
            }

            throw new Exception(message);
        }
        // return the same data:
        StringWrapper result = new StringWrapper();
        result.setData(event.toString());
        return result;

    }

}
