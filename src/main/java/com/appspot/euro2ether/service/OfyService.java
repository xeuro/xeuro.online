package com.appspot.euro2ether.service;

import com.appspot.euro2ether.entities.*;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Logger;

public class OfyService implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(OfyService.class.getName());

    public void contextInitialized(ServletContextEvent event) {
        ObjectifyService.init();
        // ObjectifyService.register(YourEntity.class);
        // etc...
        try {ObjectifyService.register(TestEntity.class);} catch (Exception e) {LOG.warning(e.getMessage());} //.........................1
        try {ObjectifyService.register(AppUser.class);} catch (Exception e) {LOG.warning(e.getMessage());} //............................2
        try {ObjectifyService.register(AppSettings.class);} catch (Exception e) {LOG.warning(e.getMessage());} //........................3
        try {ObjectifyService.register(TokensInEvent.class);} catch (Exception e) {LOG.warning(e.getMessage());} //......................4
        try {ObjectifyService.register(CryptonomicaPGPPublicKeyAPIView.class);} catch (Exception e) {LOG.warning(e.getMessage());}//.....5
        try {ObjectifyService.register(CodeForEthereumAddressVerification.class);} catch (Exception e) {LOG.warning(e.getMessage());}//..6
        try {ObjectifyService.register(EthereumAddress.class);} catch (Exception e) {LOG.warning(e.getMessage());}//.....................7
        try {ObjectifyService.register(PaymentReference.class);} catch (Exception e) {LOG.warning(e.getMessage());}//....................8
        try {ObjectifyService.register(BitcoinusDeposit.class);} catch (Exception e) {LOG.warning(e.getMessage());}//....................9
        try {ObjectifyService.register(TokensInEvent.class);} catch (Exception e) {LOG.warning(e.getMessage());}//......................10

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
