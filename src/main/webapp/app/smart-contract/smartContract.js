(function () {
    'use strict';
    var controller_name = "app.smartContract";
    var controller = angular.module(controller_name, []);

// https://docs.angularjs.org/api/ng/provider/$logProvider
    controller.config(function ($logProvider) {
        // $logProvider.debugEnabled(false);
        $logProvider.debugEnabled(true);
    });

    controller.controller(controller_name, [
        // 'GAuth',
        // 'GApi',
        // 'GData',
        '$http',
        // '$sce',
        // '$state',
        '$scope',
        '$rootScope',
        '$log',
        '$timeout',
        '$window',
        function scCtrl(
            // GAuth,
            // GApi,
            // GData,
            $http,
            // $sce,
            // $state,
            $scope,
            $rootScope,
            $log,
            $timeout,
            $window
        ) {
            $log.debug("app.smartContract started");

            // TODO: for test only
            // window.scCtrlSCOPE = $scope;
            // ------------------------

            (function alerts() {
                $scope.alertDanger = null;  // red
                $scope.alertWarning = null; // yellow
                $scope.alertInfo = null;    // blue
                $scope.alertSuccess = null; // green
                $scope.alertMessage = null; // grey

                $scope.setAlertDanger = function (message) {
                    $scope.alertDanger = message;
                    $log.debug("$scope.alertDanger:", $scope.alertDanger);
                    // $scope.$apply(); // < needed
                    $scope.goTo("alertDanger");
                };

                $scope.setAlertWarning = function (message) {
                    $scope.alertWarning = message;
                    $log.debug("$scope.alertWarning:", $scope.alertWarning);
                    // $scope.$apply(); //
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
                // grey message
                $scope.setAlertMessage = function (message, header) {
                    $scope.alertMessage = {};
                    $scope.alertMessage.header = header;
                    $scope.alertMessage.message = message;
                    $log.debug("$scope.alertMessage:", $scope.alertMessage);
                    // $scope.$apply();
                    $scope.goTo("alertMessage");
                };

                // test:
                // $scope.setAlertInfo("message to user");
            })();

            /* -- check user browser */
            // https://developer.mozilla.org/en-US/docs/Web/API/Window/navigator
            // https://developer.mozilla.org/en-US/docs/Web/API/Navigator
            // $log.debug("$window.navigator.userAgent:");
            // $log.debug($window.navigator.userAgent);
            if ($window.navigator.userAgent.indexOf("MSIE") !== -1 || $window.navigator.userAgent.indexOf("Trident") !== -1) {
                $scope.setAlertDanger(
                    "Microsoft Explorer is not supported. To use this web application please open it in modern browser"
                );
                return;
            }

            $rootScope.noConnectionToNodeError = true;

            // https://github.com/MetaMask/metamask-extension/issues/5699#issuecomment-445480857
            if (window.ethereum) {

                $rootScope.web3 = new Web3(ethereum);

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
                            $rootScope.noConnectionToNodeError = false;
                            main();

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
                            $scope.$apply(); // <<< needed here!
                        }

                    })();
                } else { // privacy mode of or already connected
                    $rootScope.noConnectionToNodeError = false;
                    main();
                }

            } else if ($window.web3) {
                $rootScope.web3 = new Web3(web3.currentProvider);
                // Accounts always exposed

                if (!$rootScope.web3.eth.defaultAccount && $rootScope.web3.eth.accounts && rootScope.web3.eth.accounts[0]) {
                    $rootScope.web3.eth.defaultAccount = rootScope.web3.eth.accounts[0];
                    // $log.debug("$rootScope.web3.eth.defaultAccount : ");
                    // $log.debug($rootScope.web3.eth.defaultAccount);
                }

                main();
            }
            // Non-dapp browsers:
            else {
                $log.error('Non-Ethereum browser detected. You should consider trying MetaMask!');
                // $scope.setAlertDanger("This web application requires Ethereum connection. Please install MetaMask.io browser plugin");
                // > use <no-connection-to-node-error></no-connection-to-node-error> directive
            }

            function main() {

                getNetworkInfo();

                $http.get($rootScope.contractAbiPath).then(function (value) {

                    // $log.debug("Contract ABI:");
                    // $log.debug(value);
                    // $log.debug(value.data); // < JSON already

                    // https://web3js.readthedocs.io/en/1.0/web3-eth-contract.html#new-contract
                    $scope.contract = new $rootScope.web3.eth.Contract(
                        value.data, // ABI
                        $rootScope.contractAddress
                    );

                    if (window.ethereum && window.ethereum.selectedAddress) {
                        $scope.contract.from = window.ethereum.selectedAddress;
                    } else if ($rootScope.web3 && $rootScope.web3.eth && $rootScope.web3.eth.defaultAccount) {
                        $scope.contract.from = $rootScope.web3.eth.defaultAccount;
                    }

                    // $log.debug("$scope.contract:");
                    // $log.debug($scope.contract);

                    contractFunctions();
                    watchEvents();

                });
            } // end of main()

            function getNetworkInfo() {
                $rootScope.getNetworkInfo();
            }

            function contractFunctions() {

                // $log.debug("contractFunctions() started");
                // $log.debug("$scope.contract :");
                // $log.debug($scope.contract);

                $scope.checkContractAdmin = function () {
                    if ($rootScope.web3.eth.defaultAccount) {
                        $scope.contract.methods.isAdmin($rootScope.web3.eth.defaultAccount).call()
                            .then(function (result) {
                                $scope.isAdmin = result;
                                $log.debug("$scope.isAdmin: " + $scope.isAdmin);
                            })
                            .catch(function (error) {
                                $log.debug("$scope.contract.isAdmin.call ERROR:");
                                $log.debug(error);
                            })
                            .finally(function () {
                                $scope.$apply();
                            })
                    } // end of: if ($rootScope.web3.eth.defaultAccount)
                };
                // $scope.checkContractAdmin();

                $scope.checkCanMint = function () {
                    if ($rootScope.web3.eth.defaultAccount) {
                        $scope.contract.methods.canMint($rootScope.web3.eth.defaultAccount).call()
                            .then(function (result) {
                                $scope.canMint = result;
                                $log.debug("$scope.canMint: " + $scope.canMint);
                            })
                            .catch(function (error) {
                                $log.debug("$scope.contract.canMint.call ERROR:");
                                $log.debug(error);
                            })
                            .finally(function () {
                                $scope.$apply();
                            })
                    } // end of  if($rootScope.web3.eth.defaultAccount){
                }; //
                // $scope.checkCanMint();

                $scope.checkCanTransferFromContract = function () {
                    if ($rootScope.web3.eth.defaultAccount) {
                        $scope.contract.methods.canTransferFromContract($rootScope.web3.eth.defaultAccount).call()
                            .then(function (result) {
                                $scope.canTransferFromContract = result;
                                $log.debug("$scope.canTransferFromContract: " + $scope.canTransferFromContract);
                            })
                            .catch(function (error) {
                                $log.debug("$scope.contract.canTransferFromContract.call ERROR:");
                                $log.debug(error);
                            })
                            .finally(function () {
                                $scope.$apply();
                            })
                    } // end of if ($rootScope.web3.eth.defaultAccount){
                }; //
                // $scope.checkCanTransferFromContract();

                $scope.checkCanBurn = function () {
                    if ($rootScope.web3.eth.defaultAccount) {
                        $scope.contract.methods.canBurn($rootScope.web3.eth.defaultAccount).call()
                            .then(function (result) {
                                $scope.canBurn = result;
                                $log.debug("$scope.canBurn: " + $scope.canBurn);
                            })
                            .catch(function (error) {
                                $log.debug("$scope.contract.canBurn.call ERROR:");
                                $log.debug(error);

                            })
                            .finally(function () {
                                $scope.$apply();
                            })
                    } // end of: if ($rootScope.web3.eth.defaultAccount){
                }; //
                // $scope.checkCanBurn();

                $scope.refreshTotalSupply = function () {
                    $scope.refreshTotalSupplyWorking = true;
                    $scope.contract.methods.totalSupply().call()
                        .then(function (result) {
                            $scope.totalSupply = result;
                        })
                        .catch(function (error) {
                            $log.debug("$scope.contract.totalSupply.call() ERROR:");
                            $log.debug(error);
                        })
                        .finally(function () {
                            $scope.refreshTotalSupplyWorking = false;
                            $scope.$apply();
                        })
                };
                // $scope.refreshTotalSupply();

                $scope.balanceOfDefaultAccount = "click to refresh";
                $scope.refreshBalanceOfDefaultAccount = function () {
                    if ($rootScope.web3.eth.defaultAccount) {
                        $scope.balanceOfDefaultAccountWorking = true;
                        $scope.balanceOfDefaultAccount = null;
                        $scope.contract.methods.balanceOf($rootScope.web3.eth.defaultAccount).call()
                            .then(function (result) {
                                $scope.balanceOfDefaultAccount = result;
                                $log.debug("$scope.balanceOfDefaultAccount :", $scope.balanceOfDefaultAccount);
                                // $log.debug($scope.balanceOfDefaultAccount);
                            })
                            .catch(function (error) {
                                $log.debug("$scope.refreshBalanceOfDefaultAccount ERROR:");
                                $log.debug(error);
                            })
                            .finally(function () {
                                $scope.balanceOfDefaultAccountWorking = false;
                                $scope.$apply();
                            })
                    }
                };
                // $scope.refreshBalanceOfDefaultAccount();

                $scope.refreshBalanceOfContract = function () {
                    if ($scope.contract) {
                        // $scope.balanceOfContract = null;
                        $scope.balanceOfContractWorking = true;
                        $scope.contract.methods.balanceOf($scope.contract._address).call()
                            .then(function (result) {
                                $scope.balanceOfContract = result;
                            })
                            .catch(function (error) {
                                $log.debug("$scope.contract.isAdmin.call ERROR:");
                                $log.debug(error);
                            })
                            .finally(function () {
                                $scope.balanceOfContractWorking = false;
                                $scope.$apply();
                            })
                    }
                };
                // $scope.refreshBalanceOfContract();

                $scope.mintTokensValue = 0;
                $scope.fiatInPaymentId = null;
                $scope.mintTokens = function () {
                    $scope.mintTokensWorking = true;
                    $scope.mintTokensTxHash = null;
                    $scope.mintTokensError = null;
                    // $log.debug("minting ", $scope.mintTokensValue, "xEUR");
                    $scope.contract.methods.mintTokens($scope.mintTokensValue, $scope.fiatInPaymentId).send({"from": $rootScope.web3.eth.defaultAccount})
                        .then(function (result) {
                            $log.debug("$scope.contract.mintTokens.sendTransaction result:");
                            $log.debug(result);
                            $scope.mintTokensTxHash = result;
                            $scope.refreshTotalSupply();
                            $scope.refreshBalanceOfContract();
                        })
                        .catch(function (error) {
                            $log.debug("$scope.contract.mintTokens.sendTransaction ERROR:");
                            $log.debug(error);
                            $scope.mintTokensError = error;
                        })
                        .finally(function () {
                            $scope.mintTokensWorking = false;
                            $scope.$apply();
                        })
                };

                // $scope.transferTokensFromContractValue = 0;
                $scope.transferTokensFromContractTo = $scope.contract._address;
                $scope.transferTokensFromContract = function () {
                    $scope.transferTokensFromContractWorking = true;
                    $scope.transferTokensFromContractError = null;
                    $scope.transferTokensFromContractTxHash = null;
                    // $scope.refreshBalanceOfContract();
                    if ($scope.transferTokensFromContractValue > $scope.balanceOfContract) {
                        $scope.transferTokensFromContractError = "insufficient funds (try to update balance clicking on 'Tokens on contract's own address')";
                        $scope.transferTokensFromContractWorking = false;
                        return;
                    }
                    $log.debug("transfer", $scope.transferTokensFromContractValue, "xEUR from", $scope.contract._address, "to", $scope.transferTokensFromContractTo);

                    $scope.contract.methods.transferFrom(
                        $scope.contract._address,
                        $scope.transferTokensFromContractTo,
                        $scope.transferTokensFromContractValue
                    ).send({"from": $rootScope.web3.eth.defaultAccount})
                        .then(function (result) {

                            $scope.transferTokensFromContractTxHash = result;
                            $log.debug("$scope.transferTokensFromContractTxHash :", $scope.transferTokensFromContractTxHash);
                            $scope.refreshBalanceOfContract();
                            $scope.refreshBalanceOfDefaultAccount();

                        })
                        .catch(function (error) {
                            $log.debug("$scope.contract.transferFrom.sendTransaction ERROR:");
                            $log.debug(error);
                            $scope.transferTokensFromContractError = error;
                        })
                        .finally(function () {
                            $scope.transferTokensFromContractWorking = false;
                            $scope.$apply();
                        })
                }; // end of transferTokensFromContract

                $scope.transferTokensToContractValue = 12;
                $scope.transferTokensToContract = function () {

                    $scope.transferTokensToContractWorking = true;
                    $scope.transferTokensToContractError = null;
                    $scope.transferTokensToContractTxHash = null;
                    // $scope.refreshBalanceOfContract();
                    if ($scope.transferTokensToContractValue > $scope.balanceOfDefaultAccount) {
                        $scope.transferTokensToContractError = "insufficient funds";
                        $scope.transferTokensToContractWorking = false;
                        return;
                    }
                    if ($scope.transferTokensToContractValue < 12) {
                        $scope.transferTokensToContractError = "minimum exchange amount is 12 xEUR";
                        $scope.transferTokensToContractWorking = false;
                        return;
                    }
                    $log.debug("transfer", $scope.transferTokensToContractValue, "xEUR from", $rootScope.web3.eth.defaultAccount, "to", $scope.contract._address);

                    $scope.contract.methods.transfer(
                        $scope.contract._address,
                        $scope.transferTokensToContractValue
                    ).send({"from": $rootScope.web3.eth.defaultAccount})
                        .then(function (result) {

                            $scope.transferTokensToContractTxHash = result.transactionHash;
                            $log.debug("$scope.transferTokensToContractTxHash : ", $scope.transferTokensToContractTxHash);
                            $scope.refreshBalanceOfContract();
                            $scope.refreshBalanceOfDefaultAccount();

                        })
                        .catch(function (error) {
                            $scope.transferTokensToContractError = error;
                            $log.debug("$scope.transferTokensToContractError : ");
                            $log.debug($scope.transferTokensToContractError);
                        })
                        .finally(function () {
                            $scope.transferTokensToContractWorking = false;
                            $scope.$apply();
                        })
                }; // end of $scope.transferTokensToContract

                // $scope.transferValue = 0;
                $scope.transferTo = $rootScope.web3.eth.defaultAccount;
                $scope.transfer = function () {
                    $scope.transferWorking = true;
                    $scope.transferError = null;
                    $scope.transferTxHash = null;
                    // $scope.refreshBalanceOfContract();
                    if ($scope.transferValue > $scope.balanceOfDefaultAccount) {
                        $scope.transferError = "insufficient funds";
                        $scope.transferWorking = false;
                        return;
                    }
                    $log.debug("transfer", $scope.transferValue, "xEUR from:", $rootScope.web3.eth.defaultAccount, "to:", $scope.transferTo);

                    $scope.contract.methods.transfer(
                        $scope.transferTo,
                        $scope.transferValue
                    ).send({"from": $rootScope.web3.eth.defaultAccount})
                        .then(function (result) {

                            $log.debug("$scope.contract.transfer.sendTransaction :");
                            $log.debug(result);
                            $scope.transferTxHash = result;
                            $scope.transferWorking = false;
                            $scope.refreshBalanceOfDefaultAccount();

                        })
                        .catch(function (error) {
                            $log.debug("$scope.contract.transfer.sendTransaction ERROR:");
                            $log.debug(error);
                            $scope.transferError = error;
                        })
                        .finally(function () {
                            $scope.transferWorking = false;
                            $scope.$apply();
                        });
                }; // end of $scope.transfer

                // $scope.burnTokensValue = 0;
                $scope.burnTokens = function () {
                    $scope.burnTokensWorking = true;
                    $scope.burnTokensTxHash = null;
                    $scope.burnTokensError = null;
                    if ($scope.burnTokensValue > $scope.balanceOfContract) {
                        $scope.burnTokensValueError = "insufficient funds";
                        $scope.burnTokensWorking = false;
                        return;
                    }
                    $log.debug("burn", $scope.burnTokensValue, "xEUR");
                    $scope.contract.methods.burnTokens($scope.burnTokensValue).send({"from": $rootScope.web3.eth.defaultAccount})
                        .then(function (result) {

                            $log.debug("$scope.contract.burnTokens.sendTransaction result:");
                            $log.debug(result);
                            $scope.burnTokensTxHash = result.transactionHash;
                            $scope.refreshTotalSupply();
                            $scope.refreshBalanceOfContract();

                        })
                        .catch(function (error) {
                            $log.debug("$scope.contract.burnTokens.sendTransaction ERROR:");
                            $log.debug(error);
                            $scope.burnTokensError = error;
                        })
                        .finally(function () {
                            $scope.burnTokensWorking = false;
                            $scope.$apply();
                        })
                }; // end of $scope.burnTokens

                $scope.refresh = function () {
                    // $log.debug("======== refreshing smart contract data ===");
                    $scope.refreshBalanceOfDefaultAccount();
                    $scope.checkContractAdmin();
                    $scope.checkCanMint();
                    $scope.checkCanBurn();
                    $scope.checkCanTransferFromContract();
                    $scope.refreshTotalSupply();
                    $scope.refreshBalanceOfContract();
                };

                $scope.refresh();

                // watchEvents();
            }

            /* ------------- Events: */

            function watchEvents() {

                // $log.debug("starting watchEvents()");
                // $log.debug("[watchEvents()] $scope.contract:");
                // $log.debug($scope.contract);

                $scope.events = [];
                $scope.showEvents = false; // < collapse events
                $scope.maxEventsNumber = 100;

                // https://web3js.readthedocs.io/en/1.0/web3-eth-contract.html#id38
                const eventsOptions = {
                    "fromBlock": $rootScope.contractDeployedOnBlock
                    // toBlock: 'latest'
                };

                // $log.debug("[watchEvents()] eventsOptions:");
                // $log.debug(eventsOptions);
                // const eventsCallback = function (error, event) {
                //     if (!error) {
                //         $log.debug(event);
                //     } else {
                //         $log.error(error);
                //     }
                // };

                // $scope.contract.events.allEvents(
                //     eventsOptions,
                //     eventsCallback
                // );

                $scope.contract.events.allEvents(
                    eventsOptions
                )
                    .on('data', function (event) {

                        // $log.debug("event:");
                        // $log.debug(event);

                        if ($scope.maxEventsNumber && $scope.events.length > $scope.maxEventsNumber) {
                            $scope.allEvents.shift();
                        }
                        $scope.events.push(event);
                        $scope.refresh(); // <<< after each event refresh smart contract's data
                    })
                    .on('error', function (error) {
                        $log.error("contract.events.allEvents error:");
                        $log.error(error);
                    });

            }

        } // end function smartContractCtrl

    ]);
})();