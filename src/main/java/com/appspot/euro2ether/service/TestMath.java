package com.appspot.euro2ether.service;

import java.math.BigDecimal;
import java.util.Date;

public class TestMath {

    public static void main(String[] args) {

        System.out.println(1 == 1.0000000000000001);
        // true
        System.out.println(0.1 + 0.2);
        // 0.30000000000000004

        BigDecimal x = new BigDecimal(0.1);
        BigDecimal y = new BigDecimal(0.2);
        System.out.println(x.add(y));
        // 0.3000000000000000166533453693773481063544750213623046875

        System.out.println(
                new Date().getTime() / 1000
        );
    }

}
