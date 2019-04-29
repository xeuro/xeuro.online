(function () {

    'use strict';
    var controller_name = "app.faq";
    var controller = angular.module("app.faq", []);

// https://docs.angularjs.org/api/ng/provider/$logProvider
    controller.config(function ($logProvider) {
        // $logProvider.debugEnabled(false);
        $logProvider.debugEnabled(true);
    });

    controller.controller("app.faq", [
        '$scope',
        '$rootScope',
        '$log',
        '$timeout', // for ngProgressFactory
        '$state',
        function faqCtrl($scope,
                         $rootScope,
                         $log,
                         $timeout,
                         $state
        ) {

            $log.debug("controller app.faq started");

            // $rootScope.progressbar.start();
            $timeout($rootScope.progressbar.complete(), 1000); // to stop

        } // end function faqCtrl
    ]);
})();