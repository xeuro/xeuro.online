(function () {

    'use strict';

    var app = angular.module('app', [
            'ui.router',
            'angular-google-gapi', // (1.0.0) https://github.com/maximepvrt/angular-google-gapi/
            'yaru22.md', // https://github.com/yaru22/angular-md
            'ngProgress', // https://github.com/VictorBjelkholm/ngProgress
            'ngClipboard', // https://github.com/nico-val/ngClipboard
            // ---- my:
            'app.ui.router',
            'app.controllers',
            'app.directives'
        ]
    );

    app.config(function ($sceDelegateProvider) {
        $sceDelegateProvider.resourceUrlWhitelist([
            'self', // Allow same origin resource loads
            'https://lh3.googleusercontent.com/**' // files (photos)  from blob storage service
            // 'https://euro2ether.appspot.com',
            // 'https://xeuro.online',
            // 'https://www.xeuro.online'
        ]);
    });

// // see:
// // https://stackoverflow.com/questions/41460772/angularjs-how-to-remove-bang-prefix-from-url/41461312
// app.config(['$locationProvider', function ($locationProvider) {
//     $locationProvider.hashPrefix('');
// }]);

    app.run([
        'GAuth',
        'GApi',
        'GData',
        '$http',
        '$state',
        '$rootScope',
        '$window',
        '$sce',
        'ngProgressFactory',
        '$timeout', // for ngProgressFactory
        '$anchorScroll',
        '$location',
        '$log',
        function (
            GAuth,
            GApi,
            GData,
            $http,
            $state,
            $rootScope,
            $window,
            $sce,
            ngProgressFactory,
            $timeout,
            $anchorScroll,
            $location,
            $log) {

            // TODO: test only (!!!)
            // window.myROOTSCOPE = $rootScope;
            // -----------------------------

            $rootScope.progressbar = ngProgressFactory.createInstance();
            $rootScope.progressbar.setHeight('6px'); // any valid CSS value Eg '10px', '1em' or '1%'
            // $rootScope.progressbar.setColor('purple');
            // $rootScope.progressbar.setColor('#C800C8');
            $rootScope.progressbar.setColor('black');
            $rootScope.progressbar.start();
            // $timeout($rootScope.progressbar.complete(), 1000); // to stop

            $rootScope.webAppVersion = "1.3.0";
            $rootScope.webAppLastChange = "2019-06-19";
            $rootScope.showLabel = false;
            $rootScope.labelText = "IN TEST MODE. Version: " + $rootScope.webAppVersion;
            $log.debug("webapp started, ver.", $rootScope.webAppVersion, "(last change:", $rootScope.webAppLastChange + ")");

            // $rootScope.supportEmail = "admin@xeuro.online";
            // $rootScope.supportEmail = "support@xeuro.freshdesk.com";
            $rootScope.supportEmail = "support@xeuro.online";
            $rootScope.companyName = "Etna Development OÜ";
            $rootScope.xEuroBankAcc = "LT183510000088270907";
            $rootScope.contractAbiPath = "/app/smart-contract/xEuro.abi";
            $rootScope.contractAddress = "0xe577e0B200d00eBdecbFc1cd3F7E8E04C70476BE"; // MainNet
            $rootScope.contractDeployedOnBlock = 7660532;
            $rootScope.contractDeployedOnNetworkId = 1;
            $rootScope.currentNetwork = {};
            $rootScope.currentNetwork.etherscanLinkPrefix = "https://etherscan.io/"; // MainNet

            // $rootScope.working = true;

            (function setUpGapi() {

                /* === angular-google-gapi */
                /* see: https://github.com/maximepvrt/angular-google-gapi */

                // (!!!) this prevents errors
                var gapiCheck = function () {
                    $rootScope.progressbar.start(); // < (!) stop in $scope.getEURBalance
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop
                    // $rootScope.working = true;
                    $rootScope.$apply();
                    if (window.gapi && window.gapi.client) {
                        // $log.debug("[app.js] window.gapi.client:");
                        // $log.debug(window.gapi.client);
                    } else {
                        $log.error("[app.js] window.gapi.client is not loaded");
                        // setTimeout(gapiCheck, 1000); // check again in a second
                        setTimeout(gapiCheck, 100); //
                    }
                    // $rootScope.working = false;
                    $rootScope.$apply();

                };
                gapiCheck();

                $rootScope.gdata = GData;
                $rootScope.gaeProjectDomain = "euro2ether.appspot.com";

                // (WEB_CLIENT_ID)
                var CLIENT = '717784656331-tfnmot2b2q1d906bufafjn7rhi6i1c5c.apps.googleusercontent.com';
                var BASE = 'https://' + $rootScope.gaeProjectDomain + '/_ah/api';

                GApi.load('adminAPI', 'v1', BASE);              // 1
                GApi.load('testAPI', 'v1', BASE);               // 2
                GApi.load('usersAPI', 'v1', BASE);              // 3

                GAuth.setClient(CLIENT);
                GAuth.setScope(
                    'https://www.googleapis.com/auth/userinfo.email'
                );

                $rootScope.login = function () { // shows auth window from Google
                    $rootScope.working = true;
                    // see: https://github.com/maximepvrt/angular-google-gapi/tree/master#signup-with-google
                    GAuth.login()
                        .then(function (user) {
                            $log.debug('[app.js] google user:');
                            $log.debug(user);
                            $rootScope.googleUser = user;
                            // email, name (first name + last name), family_name, given_name, gender, id (google id),
                            // link (google+), picture (link to file), verified_email (bool)
                            // $rootScope.getMyUserDataFromServer(); //
                            return GAuth.getToken();

                        })
                        .then(function (result) {
                            $log.debug(result);
                            // $rootScope.$apply(); // not needed here
                            $state.go('myAccount');
                        })
                        .catch(function (error) {
                            $log.error(error)
                        })
                        .finally(function () {
                            $rootScope.working = false;
                        });
                };

                $rootScope.logout = function () { // <<< works!!!
                    $rootScope.working = true;
                    GAuth.logout()
                        .then(function (resp) {

                            $log.debug('[app.js] user logged out');
                            $log.debug(resp); // undefined

                            $rootScope.gapi.user = null;
                            $rootScope.gapi.login = false;
                            $rootScope.myUserDataFromServer = null;
                            // $rootScope.$apply(); // < nod needed here
                        })
                        .catch(function (error) {
                            $log.error(error)
                        })
                        .finally(function () {
                            $rootScope.working = false;
                        });
                };

                $rootScope.testApiGetUserEmail = function () {
                    GApi.executeAuth('testAPI', 'getUserEmail').then(
                        function (resp) {

                            $rootScope.testApiGetUserEmailResult = resp;
                            $rootScope.testApiGetUserEmailError = null;
                            $log.debug('$rootScope.testApiGetUserEmailResult :');
                            $log.debug($rootScope.testApiGetUserEmailResult);
                            // $rootScope.$apply(); // not needed here
                        }, function (error) {
                            $rootScope.testApiGetUserEmailResult = null;
                            $rootScope.testApiGetUserEmailError = error;
                            $log.debug('$rootScope.testApiGetUserEmailError :');
                            $log.debug($rootScope.testApiGetUserEmailError);
                        }
                    );
                };

                $rootScope.getEURBalance = function () {
                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop

                    $rootScope.EuroBalance = null;
                    // GApi.execute('usersAPI', 'getEURBalance') //
                    $http.get('https://' + $rootScope.gaeProjectDomain + "/eurobalance")
                        .then(function (result) {

                            // $log.debug("$http.get(https://" + $rootScope.gaeProjectDomain + "/eurobalance) :");
                            // $log.debug(result);

                            if (result && result.data && result.data.EUR) {
                                $rootScope.EuroBalance = result.data.EUR; // < com.appspot.euro2ether.returns.UserData
                            }

                            $log.debug('$rootScope.EuroBalance : EUR' + $rootScope.EuroBalance);
                        })
                        .catch(function (error) {
                            $log.error('$rootScope.getEURBalance error:');
                            $log.error(error);
                            // $scope.setAlertDanger(error.message);
                        })
                        .finally(function () {
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        });
                };

                $rootScope.getCommissions = function () {
                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop

                    $rootScope.commissions = null;
                    // GApi.execute('usersAPI', 'getCommissions') //
                    $http.get("https://euro2ether.appspot.com/_ah/api/usersAPI/v1/getCommissions")
                        .then(function (result) {

                            if (result && result.data) {
                                $rootScope.commissions = result.data;
                                $log.debug("$rootScope.commissions");
                                $log.debug($rootScope.commissions);
                            }
                        })
                        .catch(function (error) {
                            $log.error('$rootScope.getCommissions error:');
                            $log.error(error);
                            // $scope.setAlertDanger(error.message);
                        })
                        .finally(function () {
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        });
                };
                // run:
                $rootScope.getEURBalance();
                $rootScope.getCommissions();

                // $rootScope.working = false;
            })();

            (function setUpUtilityFunctions() {

                $rootScope.goTo = function (id) {
                    // set the location.hash to the id of
                    // the element you wish to scroll to.
                    // $location.hash('about');
                    $location.hash(id);
                    // call $anchorScroll()
                    $anchorScroll();
                };

                $rootScope.stringIsNullUndefinedOrEmpty = function (str) {
                    return typeof str === 'undefined' || str === null || str.length === 0;
                };

                $rootScope.unixTimeFromDate = function (date) {
                    return Math.round(date.getTime() / 1000);
                };

                $rootScope.dateFromUnixTime = function (unixTime) {
                    return new Date(unixTime * 1000);
                };

                $rootScope.notANumber = function (arg) {
                    return typeof arg !== 'number';
                }

            })();

            (function setUpWeb3() {

                $rootScope.networks = {
                    1: {
                        "networkName": "Main Ethereum Network",
                        "etherscanLinkPrefix": "https://etherscan.io/",
                        "etherscanApiLink": "https://api.etherscan.io/"
                    },
                    3: {
                        "networkName": "Ropsten TestNet",
                        "etherscanLinkPrefix": "https://ropsten.etherscan.io/",
                        "etherscanApiLink": "https://api-ropsten.etherscan.io/"
                    },
                    4: {        //
                        "networkName": "Rinkeby TestNet",
                        "etherscanLinkPrefix": "https://rinkeby.etherscan.io/",
                        "etherscanApiLink": "https://api-rinkeby.etherscan.io/"
                    },
                    42: {        //
                        "networkName": "Kovan TestNet",
                        "etherscanLinkPrefix": "https://kovan.etherscan.io/",
                        "etherscanApiLink": "https://api-kovan.etherscan.io/"
                    }
                }; // end of $rootScope.networks

                $rootScope.getNetworkInfo = function () {

                    // $rootScope.working = true;
                    $rootScope.progressbar.start();
                    // $timeout($rootScope.progressbar.complete(), 1000); // to stop

                    // https://web3js.readthedocs.io/en/1.0/web3-eth-net.html#getidmain();
                    // Promise returns Number: The network ID
                    $rootScope.web3.eth.net.getId()
                        .then(function (result) {

                            $rootScope.currentNetwork.network_id = result; //
                            if (result === 1 || result === 3 || result === 4 || result === 42) {
                                $rootScope.currentNetwork.networkName = $rootScope.networks[result].networkName;
                                $rootScope.currentNetwork.etherscanLinkPrefix = $rootScope.networks[result].etherscanLinkPrefix;
                                $rootScope.currentNetwork.etherscanApiLink = $rootScope.networks[result].etherscanApiLink;
                            } else {
                                $rootScope.currentNetwork.networkName = "unknown network";
                                $rootScope.currentNetwork.etherscanLinkPrefix = "blockchain_explorer";
                                $rootScope.currentNetwork.etherscanApiLink = "blockchainExplorerAPI";
                                $log.error("Unknown Ethereum network. Network ID: " + result);
                            }

                            $log.debug("$rootScope.currentNetwork.networkName:", $rootScope.currentNetwork.networkName);

                            // $rootScope.$apply(); // needed here /> moved to finally

                            return $rootScope.web3.eth.getAccounts();

                        })
                        .then(function (accounts) {

                            $log.debug("accounts:");
                            $log.debug(accounts);

                            if (!$rootScope.web3.eth.defaultAccount && accounts && accounts.length && accounts.length > 0) {
                                // https://web3js.readthedocs.io/en/1.0/web3-eth.html#id20
                                $rootScope.web3.eth.defaultAccount = accounts[0];
                                // $log.debug("$rootScope.web3.eth.defaultAccount:", $rootScope.web3.eth.defaultAccount);
                            }
                            if (!$rootScope.web3.eth.defaultAccount) {
                                $log.error("Ethereum account not recognized. If you use MetaMask please login to MetaMask and connect to this site");
                            }

                            return $rootScope.web3.eth.getBlockNumber();
                        })
                        .then(function (blockNumber) {
                            $rootScope.currentBlockNumber = blockNumber;
                            $log.debug("$rootScope.currentBlockNumber:", $rootScope.currentBlockNumber);
                        })
                        .catch(function (error) {
                            $log.error("$rootScope.web3.eth.net.getId() error:");
                            $log.error($rootScope.web3.eth.net.getId());
                        })
                        .finally(function () {
                            // $rootScope.working = false;
                            $rootScope.$apply();
                            // $rootScope.progressbar.start();
                            $timeout($rootScope.progressbar.complete(), 1000); // to stop
                        });
                };

            })();

            // $timeout($rootScope.progressbar.complete(), 1000);

        } // end main func
    ]);
})();