'use strict';

/**
 * see example:
 * https://github.com/maximepvrt/angular-google-gapi/blob/gh-pages/app/router.js
 */

var router = angular.module('app.ui.router', []);

router.config(['$urlRouterProvider',
    function ($urlRouterProvider) {
        $urlRouterProvider.otherwise("/");
    }
]);

router.config(['$stateProvider',
    function ($stateProvider) {
        $stateProvider
            .state('home', {
                url: '/',
                controller: 'app.home',
                templateUrl: 'app/home/home.html'
            })
            .state('faq', {
                url: '/faq',
                controller: 'app.faq',
                templateUrl: 'app/faq/faq.html'
            })
            .state('help', {
                url: '/help',
                controller: 'app.help',
                templateUrl: 'app/help/help.html'
            })
            .state('about', {
                url: '/about',
                controller: 'app.about',
                templateUrl: 'app/about/about.html'
            })
            .state('smartContract', {
                url: '/smartContract',
                controller: 'app.smartContract',
                templateUrl: 'app/smart-contract/smartContract.html'
            })
            .state('myAccount', {
                url: '/myAccount',
                controller: 'app.myAccount',
                templateUrl: 'app/myAccount/myAccount.html'
            })
            .state('admin', {
                url: '/admin',
                controller: 'app.admin',
                templateUrl: 'app/admin/admin.html'
            })
            .state('termsOfService', {
                url: '/termsOfService',
                controller: 'app.home',
                templateUrl: 'app/terms/terms.html'
            })
            .state('privacyPolicy', {
                url: '/privacyPolicy',
                controller: 'app.home',
                templateUrl: 'app/terms/privacyPolicy.html'
            })
    } // end of function ($stateProvider)..
]);
