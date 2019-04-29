package com.appspot.euro2ether.service;

public class StringTools {

    public static String findSubstringWithPrefixAndSuffix(String str, String prefix, String suffix) {
        return str.substring(
                str.indexOf(prefix),
                str.indexOf(suffix) + suffix.length()
        );
    }

    public static String findStringBetweenPrefixAndSuffix(String str, String prefix, String suffix) {
        return str.substring(
                str.indexOf(prefix) + prefix.length(),
                str.indexOf(suffix)
        );
    }

}
