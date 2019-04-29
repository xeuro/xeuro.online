package com.appspot.euro2ether.service;

import com.appspot.euro2ether.constants.Constants;
import com.appspot.euro2ether.contracts.XEuro;
import com.appspot.euro2ether.entities.BitcoinusDeposit;
import com.appspot.euro2ether.entities.PaymentReference;
import com.appspot.euro2ether.returns.MintTokensEventStruct;
import com.appspot.euro2ether.returns.bitcoinus.DepositCallbackData;
import com.googlecode.objectify.Key;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple5;

import javax.servlet.ServletException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class SmartContractService {

    private static final Logger LOG = Logger.getLogger(SmartContractService.class.getName());

    public static MintTokensEventStruct getMintTokensEventStruct(Integer transactionId) throws Exception {

        XEuro xEuro = Web3jTools.createXEuroContractInstance();

        /* Solidity Code:
        struct MintTokensEvent {
        address mintedBy; //
        uint256 fiatInPaymentId;
        uint value;   //
        uint on;    // UnixTime
        uint currentTotalSupply;
        }
        */
        Tuple5 tuple5 = xEuro.fiatInPaymentsToMintTokensEvent(
                BigDecimal.valueOf(transactionId).toBigInteger()
        ).send();

        MintTokensEventStruct result = new MintTokensEventStruct(tuple5);

        LOG.warning(result.toString());

        return result;
    }

    public static Boolean mintAndSendTokens(DepositCallbackData depositCallbackData) throws ServletException, IOException, URISyntaxException {

        /* ---- get BitcoinusDeposit entity from DB: */

        BitcoinusDeposit bitcoinusDeposit = ofy().load().key(
                Key.create(
                        BitcoinusDeposit.class,
                        depositCallbackData.tid
                )
        ).now();

        if (bitcoinusDeposit == null) {

            // debug:
            LOG.warning("bitcoinusDeposit == null (new deposit received)");

            // TODO: (!) ensure that if tx mining time is shorted than callback period, there will be no double spend from acc.
            // !!! create BitcoinusDeposit entity before tx
            // if tx fails we have to check it manually
            bitcoinusDeposit = new BitcoinusDeposit(depositCallbackData);
            bitcoinusDeposit.setEntityCreated(new Date());

            Key<BitcoinusDeposit> bitcoinusDepositKey = ofy().save().entity(bitcoinusDeposit).now(); // !!! < should be not async !!!
            LOG.warning("New BitcoinusDeposit entity created: " + bitcoinusDepositKey.toString());

            // after this no other automatic 'mint and transfer' will be allowed

            /* --- check if payment reference is valid */

            String paymentReferenceStr;
            PaymentReference paymentReference;


            if (depositCallbackData.reference == null
                    || depositCallbackData.reference.isEmpty()
                    || !depositCallbackData.reference.contains(Constants.ourPaymentReferenceOnBitcoinus) // "3x1176x"
                    || !depositCallbackData.reference.contains(Constants.paymentReferenceStringSuffix) // "(#)"
            ) {

                LOG.severe("depositCallbackData.reference: " + depositCallbackData.reference);
                bitcoinusDeposit.setInternalNotes("[error] reference: " + depositCallbackData.reference);
                return saveBitcoinusDepositNeedsManualRevue(bitcoinusDeposit); // payment reference not valid, just save for manual review

            } else {

                // String paymentReferenceStr = depositCallbackData.reference.replace("3x1176x", "");
                paymentReferenceStr = StringTools.findSubstringWithPrefixAndSuffix(
                        depositCallbackData.reference,
                        Constants.ourPaymentReferenceOnBitcoinus,
                        Constants.paymentReferenceStringSuffix
                );

                paymentReference = ofy().load().key(
                        Key.create(PaymentReference.class, paymentReferenceStr)
                ).now();

                if (paymentReference == null) {
                    bitcoinusDeposit.setInternalNotes("[error] PaymentReference entity is null");
                    LOG.severe("payment reference: " + paymentReferenceStr + " is not registered");
                    TelegramBot.sendMessageToAdminsChat("[Error] payment reference: " + paymentReferenceStr + " is not registered");
                    return saveBitcoinusDepositNeedsManualRevue(bitcoinusDeposit); // payment reference not valid, just save for manual review
                }

                LOG.warning("payment reference: " + paymentReference.getPaymentReference() + " > Ethereum address: " + paymentReference.getEthereumAddress());
            }

            /* --- Calculate commission */

            // This method returns the largest integer that is less than or equal to the argument. Returned as a double:
            Double aDouble = Math.floor(depositCallbackData.amount);
            Commission commission = new Commission(
                    Constants.commissionIn,
                    Constants.minCommissionIn
            );
            commission.calculateExchangeCommission(aDouble.intValue());

            if (commission.getAmountFinalSum() > 0) {

                /* --- create Tx: */

                // throws IOException, URISyntaxException:
                XEuro xEuro = Web3jTools.createXEuroContractInstance(); // > sends info to log
                TransactionReceipt transactionReceipt = null;

                try {
                    /* ====== send transaction to smart contract > */
                    String sendingTxMessage = "sending tx to " + xEuro.getContractAddress() + ":\n"
                            + "value: " + commission.getAmountFinalSum().toString() + " xEUR tokens\n"
                            + "fiat deposit tx id: " + depositCallbackData.tid.toString() + "\n"
                            + "to: " + paymentReference.getEthereumAddress() + "\n";
                    LOG.warning(sendingTxMessage);
                    TelegramBot.sendMessageToAdminsChat(sendingTxMessage);

                    /*
                    RemoteCall<TransactionReceipt> transactionReceiptRemoteCall = xEuro.mintAndTransfer(
                            BigDecimal.valueOf(commission.getAmountFinalSum()).toBigInteger(), // value
                            BigDecimal.valueOf(depositCallbackData.tid).toBigInteger(), // fiatInPaymentId
                            paymentReference.getEthereumAddress() // to
                    );*/

                    // throws Exception
                    transactionReceipt = xEuro.mintAndTransfer(
                            BigDecimal.valueOf(commission.getAmountFinalSum()).toBigInteger(), // value
                            BigDecimal.valueOf(depositCallbackData.tid).toBigInteger(), // fiatInPaymentId
                            paymentReference.getEthereumAddress() // to
                    ).send(); // sync

                    LOG.warning(transactionReceipt.toString());

                    String txOnEtherscan = Constants.etherscanLinkPrefix + "tx/" + transactionReceipt.getTransactionHash();
                    TelegramBot.sendMessageToAdminsChat(
                            "Transaction sent:\n"
                                    + "value: " + commission.getAmountFinalSum().toString() + " xEUR tokens\n"
                                    + "commission: " + commission.getCommissionSum().toString() + " EUR\n"
                                    + "to: " + paymentReference.getEthereumAddress() + "\n"
                                    + "fiat deposit tx id: " + depositCallbackData.tid.toString() + "\n"
                                    + txOnEtherscan + "\n"
                                    + "status: " + transactionReceipt.getStatus() + "\n"
                                    + "block: " + transactionReceipt.getBlockNumber().toString() + "\n"
                    );

                    bitcoinusDeposit.setMintAndTransferTokensTxHash(transactionReceipt.getTransactionHash());
                    bitcoinusDeposit.addToTxHashes(transactionReceipt.getTransactionHash());

                    /*
                    if (!transactionReceipt.isStatusOK()) { // does not work
                        bitcoinusDeposit.setNeedsManualRevue(Boolean.TRUE);
                    }*/

                    // For blocks where block.number >= METROPOLIS_FORK_BLKNUM, the intermediate state root is
                    // replaced by a status code, 0 indicating failure (due to any operation that can cause the
                    // transaction or top-level call to revert) and 1 indicating success.
                    // https://ethereum.stackexchange.com/a/29471/1964
                    //
                    if (!transactionReceipt.getStatus().equalsIgnoreCase("0x1")) {
                        bitcoinusDeposit.setNeedsManualRevue(Boolean.TRUE);
                    }

                }
                /*
                catch (java.lang.InterruptedException e) {
                }
                */ catch (Exception e) { // < always throws exception, can be null

                    if (e.getMessage() != null) {

                        TelegramBot.sendMessageToAdminsChat("Send tx ERROR: " + e.getMessage());
                        LOG.severe("Send tx ERROR: " + e.getMessage());

                        bitcoinusDeposit.setTxError(e.getMessage());
                        bitcoinusDeposit.setNeedsManualRevue(Boolean.TRUE);

                        if (transactionReceipt != null && transactionReceipt.getTransactionHash() != null) {
                            String checkTxMessage = "check tx: " + Constants.etherscanLinkPrefix + transactionReceipt.getTransactionHash();
                            TelegramBot.sendMessageToAdminsChat(checkTxMessage);
                            bitcoinusDeposit.setInternalNotes(checkTxMessage);
                        }
                        bitcoinusDeposit.setEntityChanged(new Date());
                        ofy().save().entity(bitcoinusDeposit).now(); // sync;
                    }

                    // we get exception even if tx is successful, so we check here if tx is successful and
                    // set error status only if it's not
                    if (transactionReceipt != null && !transactionReceipt.getStatus().equalsIgnoreCase("0x1")) {

                        bitcoinusDeposit.setTxError(e.getMessage());
                        bitcoinusDeposit.setNeedsManualRevue(Boolean.TRUE);
                        bitcoinusDeposit.setEntityChanged(new Date());
                        ofy().save().entity(bitcoinusDeposit).now(); // sync;
                    }

                }

                bitcoinusDeposit.setEntityChanged(new Date());
                ofy().save().entity(bitcoinusDeposit).now(); // sync;

            } else {
                TelegramBot.sendMessageToAdminsChat("amount is to small to mint tokens (" + depositCallbackData.amount + ")");
                bitcoinusDeposit.setInternalNotes("amount is to small to mint tokens (" + depositCallbackData.amount + ")");
            }
        } else {
            // debug:
            LOG.warning("bitcoinusDeposit != null");
            LOG.warning(bitcoinusDeposit.toString());
            LOG.warning("this payment (tid: " + depositCallbackData.tid + ") was already processed");

            TelegramBot.sendMessageToAdminsChat("this payment (tid: " + depositCallbackData.tid + ") was already processed");
        }

        return Boolean.TRUE; // no more callbacks
    }

    private static Boolean saveBitcoinusDepositNeedsManualRevue(@org.jetbrains.annotations.NotNull BitcoinusDeposit bitcoinusDeposit) {

        bitcoinusDeposit.setNeedsManualRevue(Boolean.TRUE);
        bitcoinusDeposit.setEntityChanged(new Date());
        ofy().save().entity(bitcoinusDeposit).now();

        return Boolean.TRUE; // no more callbacks
    }

}
