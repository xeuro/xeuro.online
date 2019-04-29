package com.appspot.euro2ether.service;

import org.apache.commons.lang3.ArrayUtils;

public class SEPA {

    // data from: https://www.iban.com/structure
    // + https://en.wikipedia.org/wiki/Single_Euro_Payments_Area
    private static final String[] sepaCountryCodes = {
            "AD" // Andorra
            , "AT" // Austria
            , "BE" // Belgium
            , "BG" // Bulgaria
            , "HR" // Croatia
            , "CY" // Cyprus
            , "CZ" // Czech Republic
            , "DK" // Denmark
            , "EE" // Estonia
            , "FI" // Finland
            , "FR" // France
            , "DE" // Germany
            , "GI" // Gibraltar (U.K.)
            , "GR" // Greece
            , "HU" // Hungary
            , "IS" // Iceland
            , "IE" // Ireland
            , "IT" // Italy
            , "LV" // Latvia
            , "LI" // Liechtenstein
            , "LT" // Lithuania
            , "LU" // Luxembourg
            , "MT" // Malta
            , "MC" // Monaco
            , "NL" // Netherlands
            , "NO" // Norway
            , "PL" // Poland
            , "PT" // Portugal
            , "RO" // Romania
            , "SM" // San Marino
            , "SK" // Slovakia
            , "SI" // Slovenia
            , "ES" // Spain
            , "SE" // Sweden
            , "CH" // Switzerland
            , "GB" // United Kingdom
            , "VA" // Vatican City
    };

    public static Boolean isSEPA(String iban) {

        return ArrayUtils.contains(sepaCountryCodes, iban.substring(0, 2).toUpperCase());
    }

}
