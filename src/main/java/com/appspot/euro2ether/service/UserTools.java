package com.appspot.euro2ether.service;

import com.appspot.euro2ether.entities.AppUser;
import com.appspot.euro2ether.entities.CryptonomicaPGPPublicKeyAPIView;
import com.appspot.euro2ether.returns.UserData;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class UserTools {

    /* ---- Logger: */
    private static final Logger LOG = Logger.getLogger(UserTools.class.getName());

    public static Boolean isAdmin(final User googleUser) throws UnauthorizedException {

        AppUser appUser;
        Boolean result = Boolean.FALSE;

        if (googleUser != null) {

            Key<AppUser> appUserKey = Key.create(AppUser.class, googleUser.getUserId());
            appUser = ofy()
                    .load()
                    .key(appUserKey)
                    .now();

            if (appUser == null || appUser.getAdmin() == null || !appUser.getAdmin()) {
                throw new UnauthorizedException("User is not admin");
            } else if (appUser.getAdmin()) {
                result = Boolean.TRUE;
            }
        } else {
            throw new UnauthorizedException("User is not logged in");
        }

        return result;
    }

    public static Boolean isKeyCertificateValid(final User googleUser) {

        Boolean result = Boolean.FALSE;

        UserData userData = getUserData(googleUser);

        LOG.warning("[isKeyCertificateValid] userData for " + googleUser.getEmail());
        LOG.warning(userData.toString());

        CryptonomicaPGPPublicKeyAPIView pgpPublicKeyAPIView = userData.getPgpPublicKeyAPIView();

        if (pgpPublicKeyAPIView == null) {
            LOG.warning("NO KEY CERT: pgpPublicKeyAPIView==null");
        } else if (pgpPublicKeyAPIView.getRevoked() == Boolean.TRUE) {
            LOG.warning("KEY REVOKED: pgpPublicKeyAPIView.getRevoked() == Boolean.TRUE");
        } else if (pgpPublicKeyAPIView.getExp().before(new Date())) {
            LOG.warning("KEY EXPIRED: pgpPublicKeyAPIView.getExp().before(new Date())");
        } else {
            LOG.warning("KEY IS VALID");
            result = Boolean.TRUE;
        }

        return result;
    }

    public static AppUser getAppUser(final User googleUser) {

        if (googleUser == null) {
            throw new IllegalArgumentException("User is 'null'");
        }

        Key<AppUser> appUserKey = Key.create(
                AppUser.class,
                googleUser.getUserId()
        );
        AppUser appUser = ofy()
                .load()
                .key(appUserKey)
                .now();

        return appUser;

    }

    public static UserData getUserData(final User googleUser) {

        AppUser appUser = getAppUser(googleUser);

        UserData userData = new UserData();

        if (appUser != null) {

            userData.setAppUser(appUser);
            if (appUser.getPgpFingerprint() != null) {
                Key<CryptonomicaPGPPublicKeyAPIView> cryptonomicaPGPPublicKeyAPIViewKey = Key.create(
                        CryptonomicaPGPPublicKeyAPIView.class,
                        appUser.getPgpFingerprint()
                );

                CryptonomicaPGPPublicKeyAPIView pgpPublicKeyAPIView = ofy()
                        .load()
                        .key(cryptonomicaPGPPublicKeyAPIViewKey)
                        .now();
                userData.setPgpPublicKeyAPIView(pgpPublicKeyAPIView);

            }
        }

        LOG.warning("userData:");
        LOG.warning(userData.toString());

        return userData;
    }

}
