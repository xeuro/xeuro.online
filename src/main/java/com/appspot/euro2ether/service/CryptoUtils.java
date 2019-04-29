package com.appspot.euro2ether.service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class CryptoUtils {

    /* --- Logger: */
    private static final Logger LOG = Logger.getLogger(CryptoUtils.class.getName());

    /*
     * bitcoinus.io Merchant API reference:
     * signature (optional)​ - HMAC-SHA-256 hash of base currency code with API secret as encryption key
     * php code example:
     * signature' => hash_hmac('sha256',$data,$project_secret_key)
     * for testing php signatures: sudo apt install php7.2-cli
     * see: http://php.net/manual/en/features.commandline.interactive.php
     * run: php -a
     * $data = "data";
     * $project_secret_key="project_secret_key";
     * $signature = hash_hmac('sha256',$data,$project_secret_key);
     * echo $signature;
     * // a5bf94cfe47581d6c0f1b29e3ad86db4c33440060ba8e57ca2920899193f6d6f
     *
     * http://php.net/manual/en/function.hash-hmac.php :
     * hash_hmac ( string $algo , string $data , string $key [, bool $raw_output = FALSE ] ) : string
     * Returns a string containing the calculated message digest as lowercase hexits
     * unless raw_output is set to true in which case the raw binary representation of the message digest is returned
     *
     * Java:
     * https://stackoverflow.com/questions/1609899/java-equivalent-to-phps-hmac-sha1
     *
     * */

    public static String hmac(
            final String string,
            final String key,
            // see:
            // https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Mac
            String algorithm
    ) throws NoSuchAlgorithmException, InvalidKeyException {

        // https://docs.oracle.com/javase/8/docs/api/index.html?javax/crypto/class-use/Mac.html
        Mac mac = Mac.getInstance(algorithm);

        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), algorithm);
        mac.init(secret);
        byte[] digest = mac.doFinal(string.getBytes());

        // see: https://stackoverflow.com/a/5968994/1697878
        BigInteger hash = new BigInteger(1, digest);
        String hmac = hash.toString(16);
        if (hmac.length() % 2 != 0) {
            hmac = "0" + hmac;
        }

        // LOG.warning("string: " + string + "\n" + "signature: " + hmac);

        return hmac;
    }

    public static String hmacSHA256(final String string, final String key) throws InvalidKeyException, NoSuchAlgorithmException {
        // see: https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Mac
        return hmac(string, key, "HmacSHA256");
    }

    public static void main(String[] args) {

        // check signature:
        final String data = "";
        final String signature = "";
        try {
            System.out.println(hmacSHA256(data, signature));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
