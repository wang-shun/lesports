/**=========================================================
 * Module: config.js
 * App routes and resources configuration
 =========================================================*/

App.config(['$stateProvider', '$locationProvider', '$urlRouterProvider', 'RouteHelpersProvider',
    function($stateProvider, $locationProvider, $urlRouterProvider, helper) {
        'use strict';

        // Set the following to true to enable the HTML5 Mode
        // You may have to set <base> tag in index and a routing configuration in your server
        $locationProvider.html5Mode(false);

        // default route
        $urlRouterProvider.otherwise('/app/competition');

        //
        // Application Routes
        // -----------------------------------
        $stateProvider
            .state('app', {
                url: '/app',
                abstract: true,
                templateUrl: helper.basepath('app.html'),
                controller: 'AppController',
                resolve: helper.resolveFor('modernizr', 'icons')
            })
            .state('app.singleview', {
                url: '/singleview',
                title: 'Single View',
                templateUrl: helper.basepath('singleview.html')
            })
            .state('app.submenu', {
                url: '/submenu',
                title: 'Submenu',
                templateUrl: helper.basepath('submenu.html')
            })
            .state('app.login', {
                url: '/login?state&cas_ticket',
                controller: 'LoginController'
            })
            .state('app.competition', {
                url: '/competition',
                templateUrl: helper.basepath('competition/competition-list.html'),
                resolve: helper.resolveFor('ngTable', 'ngTableExport')
            })
            .state('app.competition-view', {
                url: '/competition/view/{cid:[0-9]*}',
                templateUrl: helper.basepath('competition/competition-view.html'),
                resolve: helper.resolveFor('ngDialog')
            })
            .state('app.competitor', {
                url: '/competitor',
                templateUrl: helper.basepath('competitor/competitor-list.html'),
                resolve: helper.resolveFor('ngTable', 'ngTableExport')
            })
            .state('app.match', {
                url: '/match',
                templateUrl: helper.basepath('match/match-list.html'),
                resolve: helper.resolveFor('ngTable', 'ngTableExport')
            })
            .state('app.match-view', {
                url: '/match/view/{mid:[0-9]*}',
                title: 'view match',
                templateUrl: helper.basepath('match/match-view.html'),
                resolve: helper.resolveFor('ngDialog')
            })
            .state('app.competitor-view', {
                url: '/competitor/view/{mid:[0-9]*}',
                title: 'view competitor',
                templateUrl: helper.basepath('competitor/competitor-view.html'),
                resolve: helper.resolveFor('ngDialog')
            })
            .state('app.crawler-source', {
                url: '/crawler/source',
                templateUrl: helper.basepath('crawler/source-config.html'),
                resolve: helper.resolveFor('ngDialog')
            })
            .state('app.crawler-config', {
                url: '/crawler/config',
                title: 'view config',
                templateUrl: helper.basepath('crawler/crawler-config.html')

            })
            .state('app.olympics-config', {
                url: '/olympics/config',
                title: 'view config',
                templateUrl: helper.basepath('olympics/olympics-config.html'),
                resolve: helper.resolveFor('ngDialog')
            })
            .state('app.importfile', {
                url: '/competition/files/{cid:[0-9]*}',
                templateUrl: helper.basepath('importfile/importfile-list.html'),
                resolve: helper.resolveFor('ngDialog')
            })
            .state('page', {
                url: '/page',
                templateUrl: helper.basepath('pages/page.html'),
                resolve: helper.resolveFor('modernizr', 'icons'),
                controller: ["$rootScope", function($rootScope) {
                    $rootScope.app.layout.isBoxed = false;
                }]
            })
            .state('page.error', {
                url: '/error?name',
                templateUrl: helper.basepath('pages/error.html'),
                controller: 'ErrorController'
            })
            .state('page.logout', {
                url: '/logout',
                controller: 'LogoutController'
            })
            //
            // CUSTOM RESOLVES
            //   Add your own resolves properties
            //   following this object extend
            //   method
            // -----------------------------------
            // .state('app.someroute', {
            //   url: '/some_url',
            //   templateUrl: 'path_to_template.html',
            //   controller: 'someController',
            //   resolve: angular.extend(
            //     helper.resolveFor(), {
            //     // YOUR RESOLVES GO HERE
            //     }
            //   )
            // })
        ;


    }
]).config(['$ocLazyLoadProvider', 'APP_REQUIRES', function($ocLazyLoadProvider, APP_REQUIRES) {
    'use strict';

    // Lazy Load modules configuration
    $ocLazyLoadProvider.config({
        debug: false,
        events: true,
        modules: APP_REQUIRES.modules
    });

}]).config(['$controllerProvider', '$compileProvider', '$filterProvider', '$provide',
    function($controllerProvider, $compileProvider, $filterProvider, $provide) {
        'use strict';
        // registering components after bootstrap
        App.controller = $controllerProvider.register;
        App.directive = $compileProvider.directive;
        App.filter = $filterProvider.register;
        App.factory = $provide.factory;
        App.service = $provide.service;
        App.constant = $provide.constant;
        App.value = $provide.value;

    }
]).config(['$translateProvider', function($translateProvider) {

    $translateProvider.useStaticFilesLoader({
        prefix: 'app/i18n/',
        suffix: '.json'
    });
    $translateProvider.preferredLanguage('zh');
    $translateProvider.useLocalStorage();
    $translateProvider.usePostCompiling(true);

}]).config(['cfpLoadingBarProvider', function(cfpLoadingBarProvider) {
    cfpLoadingBarProvider.includeBar = true;
    cfpLoadingBarProvider.includeSpinner = false;
    cfpLoadingBarProvider.latencyThreshold = 500;
    cfpLoadingBarProvider.parentSelector = '.wrapper > section';
}]).config(['$tooltipProvider', function($tooltipProvider) {

    $tooltipProvider.options({
        appendToBody: true
    });

}]);
