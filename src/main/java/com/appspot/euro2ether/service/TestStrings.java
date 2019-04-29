package com.appspot.euro2ether.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.IBANValidator;

import java.math.BigInteger;
import java.util.Arrays;

public class TestStrings {

    public static void main(String[] args) {
        // createPaymentReference();
        // verifyEthereumAddress(createEthereumAddress(null));
        // validateIBAN("LT183510000088270907");
        // System.out.println(RandomStringUtils.randomAlphanumeric(40).toUpperCase());
        System.out.println(
                StringTools.findSubstringWithPrefixAndSuffix("389 d9d9 start djkj9dkd end", "start", "end")
        );
        System.out.println(
                StringTools.findStringBetweenPrefixAndSuffix("389 d9d9 start djkj9dkd end", "start", "end")
        );
    }

    private static void createVerificationCode() {

        final String code = RandomStringUtils.randomAlphanumeric(21);

        System.out.println("code:");
        System.out.println(code);
        System.out.println("code.length(): " + code.length());


        final String prefix = "[verification code to sign] ";
        final String codeString = prefix + code;

        System.out.println("codeString");
        System.out.println(codeString);
        System.out.println();

        final String codeExtracted = codeString.replace(prefix, "");

        System.out.println("codeExtracted");
        System.out.println(codeExtracted);
        System.out.println("code.length(): " + code.length());

    }

    private static void createPaymentReference() {

        final Integer length = 12;
        final String paymentReference = RandomStringUtils.randomAlphanumeric(length).toLowerCase();

        System.out.println("payment reference: " + paymentReference);
        System.out.println("length: " + paymentReference.length());

        final Double variants = Math.pow((26 + 10), length);

        System.out.println("variants:" + variants.toString());

    }

    private static String createEthereumAddress(String password) {

        String ethereumAddress = null;

        if (password == null) {
            password = "password";
        }

        try {
            // see: https://ethereum.stackexchange.com/a/62400/1964
            // https://ethereum.stackexchange.com/questions/62399/create-account-with-web3j
            org.web3j.crypto.ECKeyPair keyPair = org.web3j.crypto.Keys.createEcKeyPair();
            org.web3j.crypto.WalletFile wallet = org.web3j.crypto.Wallet.createStandard(password, keyPair);

            System.out.println("Private key: " + keyPair.getPrivateKey().toString(16));
            System.out.println("Account: " + wallet.getAddress());

            ethereumAddress = "0x" + wallet.getAddress();

            // tests:
            // System.out.println("wallet.getId(): " + wallet.getId());
            // System.out.println("wallet.getCrypto().getCipher(): " + wallet.getCrypto().getCipher());
            // System.out.println(wallet.getCrypto().getCiphertext());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ethereumAddress;
    }

    private static Boolean verifyEthereumAddress(String ethereumAddress) {

        Boolean result = Boolean.FALSE;

        result = org.web3j.crypto.WalletUtils.isValidAddress(ethereumAddress);

        System.out.println("Address: " + ethereumAddress + " is a valid ETH address: " + result);

        return result;

    }

    private static Boolean validateIBAN(String IBAN) {

        // https://en.wikipedia.org/wiki/International_Bank_Account_Number
        // https://commons.apache.org/proper/commons-validator/apidocs/index.html?org/apache/commons/validator/routines/IBANValidator.html
        IBANValidator ibanValidator = org.apache.commons.validator.routines.IBANValidator.getInstance();

        Boolean result = org.apache.commons.validator.routines.IBANValidator.getInstance().isValid(IBAN);
        System.out.println(IBAN + " is valid IBAN: " + result);

        return result;

    }

}
