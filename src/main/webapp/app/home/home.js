(function () {
    'use strict';
    var controller_name = "app.home";
    var controller = angular.module(controller_name, []);

// https://docs.angularjs.org/api/ng/provider/$logProvider
    controller.config(function ($logProvider) {
        // $logProvider.debugEnabled(false);
        $logProvider.debugEnabled(true);
    });

    controller.controller(controller_name, [
        '$scope',
        '$rootScope',
        '$log',
        '$timeout',
        '$state',
        function homeCtrl($scope,
                          $rootScope,
                          $log,
                          $timeout,
                          $state
        ) {
            $log.debug("controller app.home started");

            // $rootScope.progressbar.start();
            $timeout($rootScope.progressbar.complete(), 1000); // to stop

        } // end function homeCtl
    ]);
})();