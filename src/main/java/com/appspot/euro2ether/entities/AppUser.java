package com.appspot.euro2ether.entities;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
import com.google.gson.Gson;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/*
 * all strings, except OpenPGP fingerprints and IBANs,  should be stored as lowercase
 * this entity created after first login
 *
 * */

@Entity // -> com.appspot.euro2ether.service.OfyService
// @Cache // -- may be should not stored in cache to be able view changes faster
public class AppUser implements Serializable {

    // the same as Google user ID ( googleUser.getUserId() )
    // = com.google.appengine.api.users.User (java.lang.String userId)
    @Id
    private String userId; //.....................................................................1
    @Index
    // https://cloud.google.com/appengine/docs/java/javadoc/com/google/appengine/api/users/User
    private User googleUser; //...................................................................2
    @Index
    private String email; // .....................................................................3
    // https://cloud.google.com/appengine/docs/java/javadoc/com/google/appengine/api/datastore/Text
    private Text userInfo; // ....................................................................4
    @Index
    private Boolean isAdmin; //...................................................................5
    @Index
    private Boolean isManager; //.................................................................6
    @Index
    private Date entityCreatedOn; //..............................................................7
    @Index
    private Date entityChangedOn; //..............................................................8
    @Index
    private User entityCreatedBy; //..............................................................9
    @Index
    private String pgpFingerprint;// .............................................................10
    private ArrayList<String> oldPgpFingerprints;//...............................................11
    @Index
    private String ethAddress;//..................................................................12
    private ArrayList<String> oldEthAddresses;//..................................................13
    @Index
    private String IBAN;//........................................................................14
    //
    private String ibanAccountOwnerName; //.......................................................15
    //
    private ArrayList<String> oldIBANs;//.........................................................16
    //
    private ArrayList<String> oldAccountOwnerNames;//.............................................17
    @Index
    // after user verifies his/her/its eth address :
    // RandomStringUtils.randomAlphanumeric().toLowerCase()
    private String paymentReference;//............................................................18
    private ArrayList<String> oldPaymentReferences; //............................................19
    @Index
    private Boolean representsLegalPerson;//......................................................20
    @Index
    private Long legalPersonId;//.................................................................21

    /* --- Constructors: */

    public AppUser() {
    }

    public AppUser(String userId) {
        this.userId = userId;
    }

    public AppUser(User googleUser) {
        this.userId = googleUser.getUserId();
        this.googleUser = googleUser;
        this.email = googleUser.getEmail();
    }

    /* ---- methods */

    public Boolean addToOldPgpFingerprints(String oldPgpFingerprint) {
        return this.oldPgpFingerprints.add(oldPgpFingerprint);
    }

    public Boolean addToOldEthAddresses(String oldEthAddress) {
        return this.oldEthAddresses.add(oldEthAddress);
    }

    public Boolean addToOldIBANs(String oldIBAN) {
        return this.oldIBANs.add(oldIBAN);
    }

    public Boolean addToOldAccountOwnerNames(String oldAccountOwnerName) {
        return this.oldAccountOwnerNames.add(oldAccountOwnerName);
    }

    public Boolean addToOldPaymentReferences(String oldPaymentReference) {
        return this.oldPaymentReferences.add(oldPaymentReference);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* ----- Getters and Setters:   */

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getGoogleUser() {
        return googleUser;
    }

    public void setGoogleUser(User googleUser) {
        this.googleUser = googleUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Text getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Text userInfo) {
        this.userInfo = userInfo;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getManager() {
        return isManager;
    }

    public void setManager(Boolean manager) {
        isManager = manager;
    }

    public Date getEntityCreatedOn() {
        return entityCreatedOn;
    }

    public void setEntityCreatedOn(Date entityCreatedOn) {
        this.entityCreatedOn = entityCreatedOn;
    }

    public Date getEntityChangedOn() {
        return entityChangedOn;
    }

    public void setEntityChangedOn(Date entityChangedOn) {
        this.entityChangedOn = entityChangedOn;
    }

    public User getEntityCreatedBy() {
        return entityCreatedBy;
    }

    public void setEntityCreatedBy(User entityCreatedBy) {
        this.entityCreatedBy = entityCreatedBy;
    }

    public String getPgpFingerprint() {
        return pgpFingerprint;
    }

    public void setPgpFingerprint(String pgpFingerprint) {
        this.pgpFingerprint = pgpFingerprint;
    }

    public ArrayList<String> getOldPgpFingerprints() {
        return oldPgpFingerprints;
    }

    public void setOldPgpFingerprints(ArrayList<String> oldPgpFingerprints) {
        this.oldPgpFingerprints = oldPgpFingerprints;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
    }

    public ArrayList<String> getOldEthAddresses() {
        return oldEthAddresses;
    }

    public void setOldEthAddresses(ArrayList<String> oldEthAddresses) {
        this.oldEthAddresses = oldEthAddresses;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public String getIbanAccountOwnerName() {
        return ibanAccountOwnerName;
    }

    public void setIbanAccountOwnerName(String ibanAccountOwnerName) {
        this.ibanAccountOwnerName = ibanAccountOwnerName;
    }

    public ArrayList<String> getOldIBANs() {
        return oldIBANs;
    }

    public void setOldIBANs(ArrayList<String> oldIBANs) {
        this.oldIBANs = oldIBANs;
    }

    public ArrayList<String> getOldAccountOwnerNames() {
        return oldAccountOwnerNames;
    }

    public void setOldAccountOwnerNames(ArrayList<String> oldAccountOwnerNames) {
        this.oldAccountOwnerNames = oldAccountOwnerNames;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public ArrayList<String> getOldPaymentReferences() {
        return oldPaymentReferences;
    }

    public void setOldPaymentReferences(ArrayList<String> oldPaymentReferences) {
        this.oldPaymentReferences = oldPaymentReferences;
    }

    public Boolean getRepresentsLegalPerson() {
        return representsLegalPerson;
    }

    public void setRepresentsLegalPerson(Boolean representsLegalPerson) {
        this.representsLegalPerson = representsLegalPerson;
    }

    public Long getLegalPersonId() {
        return legalPersonId;
    }

    public void setLegalPersonId(Long legalPersonId) {
        this.legalPersonId = legalPersonId;
    }

}
