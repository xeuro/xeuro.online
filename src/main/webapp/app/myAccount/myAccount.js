(function () {

    'use strict';
    var controller_name = "app.myAccount";
    var controller = angular.module(controller_name, []);

// https://docs.angularjs.org/api/ng/provider/$logProvider
    controller.config(function ($logProvider) {
        // $logProvider.debugEnabled(false);
        $logProvider.debugEnabled(true);
    });

    controller.controller(controller_name, [
        'GAuth',
        'GApi',
        'GData',
        '$scope',
        '$rootScope',
        '$log',
        '$http',
        '$timeout',
        function myAccCtrl(
            GAuth,
            GApi,
            GData,
            $scope,
            $rootScope,
            $log,
            $http,
            $timeout
        ) {

            // TODO: for testing only:
            $log.debug("controller app.myAccount started");
            // window.myAccCtrlSCOPE = $scope;

            (function setScopeInitialValues() {
                $scope.activeTab = 1;
                //
                $rootScope.myUserDataFromServer = null;
                $scope.userNotLoggedIn = true;
                $scope.fingerprint = null;
            })();

            (function setAlerts() {

                /* --- Alerts */
                $scope.alertDanger = null;  // red
                $scope.alertWarning = null; // yellow
                $scope.alertInfo = null;    // blue
                $scope.alertSuccess = null; // green
                $scope.alertMessage = null; // grey

                $scope.setAlertDanger = function (message) {
                    $scope.alertDanger = message;
                    $log.debug("$scope.alertDanger:", $scope.alertDanger);
                    // $scope.$apply(); not here
                    $scope.goTo("alertDanger");
                };

                $scope.setAlertWarning = function (message) {
                    $scope.alertWarning = message;
                    $log.debug("$scope.alertWarning:", $scope.alertWarning);
                    // $scope.$apply();
                    $scope.goTo("alertWarning");
                };

                $scope.setAlertInfo = function (message) {
                    $scope.alertInfo = message;
                    $log.debug("$scope.alertInfo:", $scope.alertInfo);
                    // $scope.$apply();
                    $scope.goTo("alertInfo");
                };

                $scope.setAlertSuccess = function (message) {
                    $scope.alertSuccess = message;
                    $log.debug("$scope.alertSuccess:", $scope.alertSuccess);
                    // $scope.$apply();
                    $scope.goTo("alertSuccess");
                };

                $scope.setAlertMessage = function (message, header) {
                    $scope.alertMessage = {};
                    $scope.alertMessage.header = header;
                    $scope.alertMessage.message = message;
                    $log.debug("$scope.alertMessage:", $scope.alertMessage);
                    // $scope.$apply();
                    $scope.goTo("alertMessage");
                };

                $scope.clearAllAlerts = function () {
                    $scope.alertDanger = null;  // red
                    $scope.alertWarning = null; // yellow
                    $scope.alertInfo = null;    // blue
                    $scope.alertSuccess = null; // green
                    $scope.alertMessage = null; // grey
                }

            })();

            (function setFunctions() {

                $scope.fingerprintFormValidation = function (fingerprint) {
                    if (!fingerprint && fingerprint.length !== 40) {
                        $log.debug("invalid input:", fingerprint);
                        $scope.fingerprintFormValidationMessage = "Fingerprint must be exacly 40 characters, no empty spaces or special symbols";
                        return false;
                    } else {
                        $scope.fingerprintFormValidationMessage = null;
                        return true;
                    }
                    // $scope.$apply(); // not here
                };

                $scope.checkSEPA = function (iban) {
                    var SEPAcountryCodes = [
                        "AT"
                        , "BE"
                        , "BG"
                        , "HR"
                        , "CY"
                        , "CZ"
                        , "FO"
                        , "GL"
                        , "DK"
                        , "EE"
                        , "FI"
                        , "FR"
                        , "DE"
                        , "GI"
                        , "GR"
                        , "HU"
                        , "IS"
                        , "IE"
                        , "IT"
                        , "LV"
                        , "LI"
                        , "LT"
                        , "LU"
                        , "MT"
                        , "MC"
                        , "NL"
                        , "NO"
                        , "PL"
                        , "PT"
                        , "RO"
                        , "SM"
                        , "SK"
                        , "SI"
                        , "ES"
                        , "SE"
                        , "CH"
                        , "GB"
                    ];
                    return SEPAcountryCodes.includes(iban.substring(0, 2).toUpperCase());
                };

                $scope.checkFingerprintOnCryptonomica = function (fingerprint) {

                    $scope.clearAllAlerts();

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop

                    $scope.clearAllAlerts();

                    // $log.debug("$scope.checkFingerprintOnCryptonomica() fingerprint:");
                    // $log.debug(fingerprint);

                    if (!$scope.fingerprintFormValidation(fingerprint)) {
                        $rootScope.working = false;
                        return;
                    }
                    GApi.executeAuth('usersAPI', 'checkFingerprintOnCryptonomica', {"fingerprint": fingerprint}) // UserData
                        .then(function (result) {
                            $rootScope.myUserDataFromServer = result; // < com.appspot.euro2ether.returns.UserData
                            $log.debug('$rootScope.myUserDataFromServer :');
                            $log.debug($rootScope.myUserDataFromServer);

                            if ($rootScope.myUserDataFromServer.pgpPublicKeyAPIView) {
                                $scope.dateNow = new Date();
                                $rootScope.myUserDataFromServer.pgpPublicKeyAPIView.expired = $rootScope.myUserDataFromServer.pgpPublicKeyAPIView.exp < new Date();
                            }

                            $scope.setAlertSuccess(
                                "User " + $rootScope.myUserDataFromServer.appUser.email
                                + "  was identified as: "
                                + $rootScope.myUserDataFromServer.pgpPublicKeyAPIView.firstName + " " + $rootScope.myUserDataFromServer.pgpPublicKeyAPIView.lastName
                                + ", key ID: " + $rootScope.myUserDataFromServer.pgpPublicKeyAPIView.keyID
                            )

                        })
                        .catch(function (error) {
                            $log.error('$scope.checkFingerprintOnCryptonomica() error:');
                            $log.error(error);
                            $scope.setAlertDanger(error.message);
                        })
                        .finally(function () {
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        });

                };

                $scope.receivePromoCodeFromCryptonomica = function () {

                    // $scope.clearAllAlerts();

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop
                    $scope.promoCode = null;

                    GApi.executeAuth('usersAPI', 'receivePromoCodeFromCryptonomica')
                        .then(function (promoCode) { // com.appspot.euro2ether.returns.cryptonomica.PromoCode
                            $scope.promoCode = promoCode; // < com.appspot.euro2ether.returns.UserData
                            if ($scope.promoCode.promoCode) {
                                $scope.promoCode.link = "https://www.cryptonomica.net/#!/promocode/" + $scope.promoCode.promoCode;
                            }


                            $log.debug('$scope.promoCode :');
                            $log.debug($scope.promoCode);
                        })
                        .catch(function (error) {
                            $log.error('$scope.receivePromoCodeFromCryptonomica() error:');
                            $log.error(error);
                            $scope.setAlertDanger(error.message);
                        })
                        .finally(function () {
                            // $scope.$apply(); // << error
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        });

                };

                $scope.getMyUserDataFromServer = function () {

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop

                    $scope.clearAllAlerts();

                    GApi.executeAuth('usersAPI', 'getMyUserDataFromServer')
                        .then(function (result) {

                            $rootScope.myUserDataFromServer = result; // < com.appspot.euro2ether.returns.UserData

                            $log.debug('$rootScope.myUserDataFromServer :');
                            $log.debug($rootScope.myUserDataFromServer);

                            if ($rootScope.myUserDataFromServer.pgpPublicKeyAPIView) {
                                $scope.dateNow = new Date();
                                $rootScope.myUserDataFromServer.pgpPublicKeyAPIView.expired = $rootScope.myUserDataFromServer.pgpPublicKeyAPIView.exp < new Date();
                            }

                            // $rootScope.$apply(); // not needed here

                        })
                        .catch(function (error) {
                            $log.error('$scope.getMyUserDataFromServer error:');
                            $log.error(error);
                            $scope.setAlertDanger(error);
                        })
                        .finally(function () {
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        });
                };

                $scope.checkAuth = function () {

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop

                    $scope.clearAllAlerts();

                    GAuth.checkAuth()
                        .then(function (user) {
                            // if (!user) {} // < never works, if !user > error

                            // if success:
                            $log.debug("$rootScope.gapi : ");
                            $log.debug($rootScope.gapi);

                            return $scope.getMyUserDataFromServer();

                        })
                        .then(function (result) {

                            $rootScope.progressbar.start();

                            $log.debug("return $scope.getMyUserDataFromServer() result:");
                            $log.debug(result);

                        })
                        .catch(function (error) {

                            $log.error("[myAccount.js] GAuth.checkAuth() error:");
                            $log.error(error);

                            $scope.setAlertDanger(error);

                        })
                        .finally(function () {
                            // $scope.$apply();// < error
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        });
                };

                $scope.loginScope = function () {

                    $scope.clearAllAlerts();

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop

                    $scope.clearAllAlerts();
                    // see: https://github.com/maximepvrt/angular-google-gapi/tree/master#signup-with-google
                    GAuth.login()
                        .then(function (user) {
                            $log.debug('[myAccount.js] google user:');
                            $log.debug(user);
                            $rootScope.googleUser = user;
                            // email, name (first name + last name), family_name, given_name, gender, id (google id),
                            // link (google+), picture (link to file), verified_email (bool)
                            // $rootScope.getMyUserDataFromServer(); //

                            // $rootScope.$apply(); // < not needed here

                            // return $scope.checkAuth();
                            $scope.checkAuth();

                        })
                        .catch(function (error) {
                            $log.error("$scope.loginScope() error:");
                            $log.error(error);
                            $scope.setAlertDanger(error);
                        })
                        .finally(function () {
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        });
                };

                $scope.myAccConnectMetaMask = function () {

                    $scope.clearAllAlerts();

                    // https://github.com/MetaMask/metamask-extension/issues/5699#issuecomment-445480857
                    if (window.ethereum) {

                        $log.debug("window.ethereum:");
                        $log.debug(window.ethereum);

                        //
                        $rootScope.web3 = new Web3(ethereum); // <<<
                        //

                        $log.debug('web3: ');
                        $log.debug($rootScope.web3);

                        if (typeof window.ethereum.selectedAddress === 'undefined') { // privacy mode on

                            (async function () {
                                try {
                                    // Request account access if needed
                                    // This method returns a Promise that resolves to an array of user accounts once access is approved for a given dapp.
                                    await window.ethereum.enable();
                                    // Accounts now exposed
                                    // $rootScope.web3.eth.accounts[0] > does not work
                                    // but you have already: web3.eth.defaultAccount
                                    // $log.debug("$rootScope.web3.eth.defaultAccount : ");
                                    // $log.debug($rootScope.web3.eth.defaultAccount);

                                    // $rootScope.noConnectionToNodeError = false;

                                    /// $rootScope.metaMaskConnected = true;

                                    $rootScope.getNetworkInfo(); // < $rootScope.working will be set there

                                } catch (error) {
                                    // if user denied account access:
                                    // {"message":"User denied account authorization","code":4001}
                                    $log.error(error);
                                    // $scope.setAlertDanger(
                                    //     "User denied MetaMask connect. Web application can not be started."
                                    //     + "If you wish to use this webapp, please, reload the page and connect MetaMask"
                                    // );
                                    if (error.message && error.code === 4001) {
                                        error = "User denied MetaMask connect. Web application can not be started."
                                            + " If you wish to use this webapp, please, reload the page and connect MetaMask";
                                    }
                                    $scope.setAlertDanger(error);
                                    $rootScope.working = false;
                                    $scope.$apply(); // <<< needed here!
                                }

                            })();

                        } else { // privacy mode off or already connected

                            $log.debug("privacy mode off or already connected");

                            $rootScope.getNetworkInfo(); // < $rootScope.working will be set there

                        }

                    }
                    // Old web3 browser:
                    else if (window.web3) {

                        $log.debug("old web3 browser detected");
                        $scope.setAlertDanger("Your Ethereum client not supported. Please use MetaMask plugin with the last version of Chrome, Firefox, Opera or Brave web browser");
                        $rootScope.working = false;

                    }
                    // Non-dapp browsers:
                    else {
                        $log.error('Non-Ethereum browser detected. You should consider trying MetaMask!');
                        $scope.setAlertDanger("This web application requires Ethereum connection. Please install MetaMask.io browser plugin.");
                        // > or use <no-connection-to-node-error></no-connection-to-node-error> directive
                        $rootScope.working = false;
                    }

                };

                $scope.verifyEthereumAddress = function () {

                    $scope.clearAllAlerts();

                    if (!$rootScope.web3.eth.defaultAccount) {
                        var errorMessage = "$rootScope.web3.eth.defaultAccount not set up";
                        $log.error(errorMessage);
                        $scope.setAlertDanger("Ethereum account not recognized");
                        return;
                    }

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop

                    $scope.checkEthereumSignatureRequest = null;
                    // $rootScope.working = true;

                    GApi.executeAuth('usersAPI', 'requestCodeForEthereumAddressVerification')
                        .then(function (result) {
                            $log.debug("GApi.executeAuth('usersAPI', 'requestCodeForEthereumAddressVerification') result:");
                            $log.debug(result);
                            $scope.stringToSign = result.code;

                            return $rootScope.web3.eth.personal.sign($scope.stringToSign, $rootScope.web3.eth.defaultAccount)

                        })
                        .then(function (ethereumSignature) {

                            $log.debug("ethereumSignature:");
                            $log.debug(ethereumSignature);

                            $scope.checkEthereumSignatureRequest = {
                                "signedString": $scope.stringToSign,
                                "ethereumSignature": ethereumSignature,
                                "ethereumAddress": $rootScope.web3.eth.defaultAccount
                            };

                            $log.debug("$scope.checkEthereumSignatureRequest:");
                            $log.debug($scope.checkEthereumSignatureRequest);

                            return GApi.executeAuth('usersAPI', 'checkEthereumSignature', $scope.checkEthereumSignatureRequest);
                        })
                        .then(function (userData) {

                            $rootScope.myUserDataFromServer = userData; // < com.appspot.euro2ether.returns.UserData

                            $log.debug('$rootScope.myUserDataFromServer :');
                            $log.debug($rootScope.myUserDataFromServer);

                            if ($rootScope.myUserDataFromServer.pgpPublicKeyAPIView) {
                                $scope.dateNow = new Date();
                                $rootScope.myUserDataFromServer.pgpPublicKeyAPIView.expired = $rootScope.myUserDataFromServer.pgpPublicKeyAPIView.exp < new Date();
                            }

                        })
                        .catch(function (error) {
                            $log.error(" $scope.verifyEthereumAddress() error:");
                            $log.error(error);
                            if (error.message) {
                                $scope.setAlertDanger(error.message);
                            } else {
                                $scope.setAlertDanger(error);
                            }

                        })
                        .finally(function () {
                            // $rootScope.working = false;
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        })
                };

                $scope.addIBAN = function () {

                    $scope.clearAllAlerts();

                    if (!IBAN.isValid($scope.iban)) {
                        $scope.setAlertDanger("This is not a valid IBAN");
                        return;
                    }

                    if (!$scope.checkSEPA($scope.iban)) {
                        $scope.setAlertDanger("This IBAN not belongs to bank from SEPA country");
                        return;
                    }

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop

                    GApi.executeAuth('usersAPI', 'addIBAN', {
                        "IBAN": $scope.iban,
                        "accountOwnerName": $scope.accountOwnerName
                    })
                        .then(function (userData) {

                            $rootScope.myUserDataFromServer = userData; // < com.appspot.euro2ether.returns.UserData

                            $log.debug('[$scope.addIBAN()] $rootScope.myUserDataFromServer :');
                            $log.debug($rootScope.myUserDataFromServer);

                            if ($rootScope.myUserDataFromServer.pgpPublicKeyAPIView) {
                                $scope.dateNow = new Date();
                                $rootScope.myUserDataFromServer.pgpPublicKeyAPIView.expired = $rootScope.myUserDataFromServer.pgpPublicKeyAPIView.exp < new Date();
                            }

                        })
                        .catch(function (error) {
                            $log.error("$scope.addIBAN() error:");
                            $log.error(error);
                            if (error.message) {
                                $scope.setAlertDanger(error.message);
                            } else {
                                $scope.setAlertDanger(error);
                            }

                        })
                        .finally(function () {
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        })
                }

            })();

            (function run() {
                $scope.checkAuth();
            })();

        } // end myAccCtrl
    ]);
})();