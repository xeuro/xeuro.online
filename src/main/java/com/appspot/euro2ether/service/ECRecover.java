package com.appspot.euro2ether.service;

import com.appspot.euro2ether.inputs.CheckEthereumSignatureRequest;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;

/*
 *
 * see:
 * https://raw.githubusercontent.com/web3j/web3j/51f88364b8899ff8d89fd7bb3671d7b3624afe4d/crypto/src/test/java/org/web3j/crypto/ECRecoverTest.java
 * */

public class ECRecover {

    private static final String PERSONAL_MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";

    public static void main(String[] args) {

        /* checked with web3.js 1.0 :
        final String message = "<<This is a text to sign. You can change it, paste new text or upload text file>>";
        final String signature = "0x36fd9e0dfc7376eb6094f042c6ad96ca43d3c6a72487c9298aed11f995b588fa7e7bcaa2d5aeafaa324d269ed9adf996224bba2caeee3dca4de00c9d4ed3b8fb1c";
        final String address = "0xd851d045d8aee53ef24890afba3d701163acbc8b";
        */

        /* checked with web3.js 1.0 , originally from web3j code
        //CHECKSTYLE:OFF
        String signature = "0x2c6401216c9031b9a6fb8cbfccab4fcec6c951cdf40e2320108d1856eb532250576865fbcd452bcdc4c57321b619ed7a9cfd38bd973c3e1e0243ac2777fe9d5b1b";
        //CHECKSTYLE:ON
        String address = "0x31b26e43651e9371c88af3d36c14cfd938baf4fd";
        String message = "v0G9u7huK4mJb2K1";
        */

        // recoverAddressFromSignature(message, signature, address);

        String ethereumAddress = "0xf41ca2ce3a6cfd3097ba0ab13fd8ba18bd1a3430";
        String ethereumSignature = "0xcfa4b1cc27d1bdd6aba34d8100c75dd3f2b34ce6e38da052e76d72738cd485ca0808cfbdcf56ea65af5c79e637e934c0cd536f778f19cd05320f102aaaba2c481b";
        String signedString = "QhHVWD6qcK6GPlMB4ump0"; // QhHVWD6qcK6GPlMB4ump0

        System.out.println(
                verifyEthereumSignature(signedString, ethereumSignature, ethereumAddress)
        );

    }

    public static Boolean verifyEthereumSignature(
            final String message,
            final String signature,
            String address) {

        // (!!!) lowercase (!!!)
        address = address.toLowerCase();

        String prefix = PERSONAL_MESSAGE_PREFIX + message.length();

        byte[] msgHash = Hash.sha3((prefix + message).getBytes());

        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        byte v = signatureBytes[64];
        if (v < 27) {
            v += 27;
        }

        SignatureData sd = new SignatureData(
                v,
                (byte[]) Arrays.copyOfRange(signatureBytes, 0, 32),
                (byte[]) Arrays.copyOfRange(signatureBytes, 32, 64));

        String addressRecovered = null;
        boolean match = false;

        // Iterate for each possible key to recover
        for (int i = 0; i < 4; i++) {
            BigInteger publicKey = Sign.recoverFromSignature(
                    (byte) i,
                    new ECDSASignature(new BigInteger(1, sd.getR()), new BigInteger(1, sd.getS())),
                    msgHash);

            if (publicKey != null) {
                addressRecovered = "0x" + Keys.getAddress(publicKey);

                if (addressRecovered.equals(address)) {
                    match = true;
                    break;
                }
            }
        }

        /*
        System.out.println("addressRecovered: " + addressRecovered);
        System.out.println("address: " + address);
        System.out.println("match: " + match);
        */

        return match;
    }

    public static Boolean verifyEthereumSignature(CheckEthereumSignatureRequest checkEthereumSignatureRequest) {
        return verifyEthereumSignature(
                checkEthereumSignatureRequest.getSignedString(),
                checkEthereumSignatureRequest.getEthereumSignature(),
                checkEthereumSignatureRequest.getEthereumAddress());
    }

}
