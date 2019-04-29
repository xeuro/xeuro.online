(function () {

    'use strict';
    var controller = angular.module("app.admin", []);

// https://docs.angularjs.org/api/ng/provider/$logProvider
    controller.config(function ($logProvider) {
        // $logProvider.debugEnabled(false);
        $logProvider.debugEnabled(true);
    });

    controller.controller("app.admin", [
        'GAuth',
        'GApi',
        'GData',
        '$scope',
        '$rootScope',
        '$log',
        // '$http',
        '$timeout',
        function myAccCtrl(
            GAuth,
            GApi,
            GData,
            $scope,
            $rootScope,
            $log,
            // $http,
            $timeout
        ) {

            // TODO: for testing only:
            $log.debug("controller app.admin started");
            // window.myAdminSCOPE = $scope;

            (function setScopeInitialValues() {
                $rootScope.myUserDataFromServer = null;
                $scope.userNotLoggedIn = true;
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

                $scope.getMyUserDataFromServer = function () {

                    $scope.clearAllAlerts();

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop
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

                $scope.getDepositsForRevue = function () {

                    $scope.clearAllAlerts();

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop
                    GApi.executeAuth('adminAPI', 'depositsForRevue')
                        .then(function (result) {

                            $scope.depositsForRevue = result; //

                            $log.debug('$scope.depositsForRevue :');
                            $log.debug($scope.depositsForRevue);

                            // $rootScope.$apply(); // not needed here

                        })
                        .catch(function (error) {
                            $log.error('$scope.getDepositsForRevue error:');
                            $log.error(error);
                            $scope.setAlertDanger(error);
                        })
                        .finally(function () {
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        });
                };

                $scope.depositTransactions = {};
                $scope.getTransaction = function (transactionId) {

                    // $scope.clearAllAlerts();

                    if ($rootScope.stringIsNullUndefinedOrEmpty(transactionId)) {
                        $scope.setAlertDanger("transaction id is not valid");
                        return;
                    }

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop
                    GApi.executeAuth('adminAPI', 'getTransaction', {"transactionId": transactionId})
                        .then(function (result) {

                            if (result.result) {
                                $scope.depositTransactions[transactionId] = result.result;
                            } else {
                                $scope.depositTransactions[transactionId] = result;
                            }

                            $log.debug('transaction', transactionId, 'info:');
                            $log.debug($scope.depositTransactions[transactionId]);

                            // $rootScope.$apply(); // not needed here

                        })
                        .catch(function (error) {
                            $log.error('$scope.getTransaction error:');
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
                        });
                };

                $scope.mintTokensEventStructs = {};
                $scope.fiatInPaymentsToMintTokensEvent = function (transactionId) {

                    // $scope.clearAllAlerts();

                    if ($rootScope.stringIsNullUndefinedOrEmpty(transactionId)) {
                        $scope.setAlertDanger("transaction id is not valid");
                        return;
                    }

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop
                    GApi.executeAuth('adminAPI', 'fiatInPaymentsToMintTokensEvent', {"transactionId": transactionId})
                        .then(function (result) {

                            if (result.result) {
                                $scope.mintTokensEventStructs[transactionId] = result.result;
                            } else {
                                $scope.mintTokensEventStructs[transactionId] = result.result;
                            }

                            $log.debug('$scope.mintTokensEventStructs[', transactionId, '] :');
                            $log.debug($scope.mintTokensEventStructs[transactionId]);

                            // $rootScope.$apply(); // not needed here

                        })
                        .catch(function (error) {
                            $log.error('$scope.fiatInPaymentsToMintTokensEvent error:');
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
                        });
                };

                $scope.internalNotes = {};
                $scope.setDepositConfirmed = function (transactionId) {

                    $scope.clearAllAlerts();

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop
                    GApi.executeAuth('adminAPI', 'setDepositConfirmed', {
                        "transactionId": transactionId,
                        "internalNotes": $scope.internalNotes[transactionId]
                    })
                        .then(function (result) {

                            $log.debug('$scope.setDepositConfirmed result :');
                            $log.debug(result);

                            // $rootScope.$apply(); // not needed here

                        })
                        .catch(function (error) {
                            $log.error('$scope.setDepositConfirmed error:');
                            $log.error(error);
                            $scope.setAlertDanger(error);
                        })
                        .finally(function () {
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        });
                };

                $scope.checkAuth = function () {

                    $scope.clearAllAlerts();

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop
                    GAuth.checkAuth()
                        .then(function (user) {
                            // if (!user) {} // < never works, if !user > error

                            // if success:
                            $log.debug("$rootScope.gapi : ");
                            $log.debug($rootScope.gapi);

                            $scope.getDepositsForRevue();
                            $scope.getTokensInEventsForRevue();
                            $scope.getMyUserDataFromServer();
                        })
                        .catch(function (error) {

                            $log.error("[admin.js] GAuth.checkAuth() error:");
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

                $scope.getTokensInEventsForRevue = function () {

                    // $scope.clearAllAlerts();

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop
                    GApi.executeAuth('adminAPI', 'tokensInEventsForRevue')
                        .then(function (result) {

                            $scope.tokensInEventsForRevue = result; //

                            $log.debug('$scope.tokensInEventsForRevue:');
                            $log.debug($scope.tokensInEventsForRevue);

                            // $rootScope.$apply(); // not needed here

                        })
                        .catch(function (error) {
                            $log.error('$scope.getTokensInEventsForRevue error:');
                            $log.error(error);
                            $scope.setAlertDanger(error);
                        })
                        .finally(function () {
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        });
                };

                $scope.tokensInEventsInternalNotes = {};
                $scope.setTokensInEventConfirmed = function (tokensInEventId) {

                    $scope.clearAllAlerts();

                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop

                    $log.debug(
                        "start $scope.setTokensInEventConfirmed: \n tokensInEventsCounter:", tokensInEventId,
                        " internalNotes :", $scope.tokensInEventsInternalNotes[tokensInEventId]
                    );

                    GApi.executeAuth('adminAPI', 'setTokensInEventConfirmed', {
                        "tokensInEventsCounter": tokensInEventId,
                        "internalNotes": $scope.tokensInEventsInternalNotes[tokensInEventId]
                    })
                        .then(function (result) {

                            $log.debug('$scope.setDepositConfirmed result :');
                            $log.debug(result);

                            $scope.setAlertSuccess(result.result);

                            // $rootScope.$apply(); // not needed here

                        })
                        .catch(function (error) {
                            $log.error('$scope.setDepositConfirmed error:');
                            $log.error(error);
                            $scope.setAlertDanger(error);
                        })
                        .finally(function () {
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        });
                };

            })();

            (function run() {
                $scope.checkAuth();
            })();

        } // end myAccCtrl
    ]);
})();