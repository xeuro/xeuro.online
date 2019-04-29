package com.appspot.euro2ether.service;

import com.appspot.euro2ether.constants.Constants;
import com.appspot.euro2ether.contracts.XEuro;
import com.appspot.euro2ether.entities.AppSettings;
import com.googlecode.objectify.Key;
import org.apache.http.client.utils.URIBuilder;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class Web3jTools {

    private static final Logger LOG = Logger.getLogger(Web3jTools.class.getName());
    // private static final Gson GSON = new Gson();

    public static Web3j getWeb3jObject() throws IOException, URISyntaxException {

        final String infuraProjectId = ofy()
                .load()
                .key(Key.create(AppSettings.class, "infuraProjectId"))
                .now()
                .getStringValue();

        final URI uri = new URIBuilder()
                .setScheme("https")
                .setHost(Constants.ethereumNetworkName + ".infura.io")
                .setPath("/" + Constants.infuraApiVersion + "/" + infuraProjectId)
                .build();

        // java.net.URL : https://docs.oracle.com/javase/8/docs/api/index.html?java/net/URL.html
        final URL url = uri.toURL();
        final String infuraEndpointUrlStr = String.valueOf(url);

        Web3j web3 = Web3j.build(new HttpService(infuraEndpointUrlStr));

        Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().send();

        LOG.warning("Ethereum node : " + web3ClientVersion.getWeb3ClientVersion());

        return web3;
    }

    private static Credentials getCredentialsObject() {

        final String xEuroAdminPrivateKey = ofy()
                .load()
                .key(Key.create(AppSettings.class, "xEuroAdminPrivateKey"))
                .now()
                .getStringValue();

        final String xEuroAdminAddress = ofy()
                .load()
                .key(Key.create(AppSettings.class, "xEuroAdminAddress"))
                .now()
                .getStringValue();

        Credentials credentials = Credentials.create(xEuroAdminPrivateKey);

        LOG.warning("ETH address from private key : " + credentials.getAddress());

        if (!xEuroAdminAddress.equalsIgnoreCase(credentials.getAddress())) {
            throw new IllegalStateException("ETH address created from private key (" + credentials.getAddress()
                    + ") does not much admin address from DB ("
                    + xEuroAdminAddress + ")"
            );
        }

        return credentials;
    }

    public static XEuro createXEuroContractInstance() throws IOException, URISyntaxException {

        Credentials credentials = Web3jTools.getCredentialsObject();
        Web3j web3 = Web3jTools.getWeb3jObject();
        final BigInteger GAS_PRICE = BigInteger.valueOf(30_000_000_000L);
        // see example on https://etherscan.io/tx/0x9774a4eef49870a0ae6f9c79235cc58a3bb1764025da3725aad5692ac2e89fe0
        // (Gas Used By Transaction: 381003)
        final BigInteger GAS_LIMIT_MAX = BigInteger.valueOf(1_000_000); //

        final XEuro xEuro = XEuro.load(
                Constants.xEuroSmartContractAddress,
                web3,
                credentials,
                GAS_PRICE,
                GAS_LIMIT_MAX
        );

        LOG.warning("created xEuro contract instance at address: " + xEuro.getContractAddress());

        return xEuro;
    }

}
