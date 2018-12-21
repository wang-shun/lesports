/**=========================================================
 * Module: main.js
 * Main Application Controller
 =========================================================*/

App.controller('AppController',
  ['$rootScope', '$scope', '$state', '$translate', '$window', '$localStorage', '$timeout', 'toggleStateService', 'colors', 'browser', 'cfpLoadingBar', '$http', 'BOLE_AUTHENTICATION', 'BOLE_API_HOST',
  function($rootScope, $scope, $state, $translate, $window, $localStorage, $timeout, toggle, colors, browser, cfpLoadingBar, $http, BOLE_AUTHENTICATION, BOLE_API_HOST) {
    "use strict";

    // Setup the layout mode
    $rootScope.app.layout.horizontal = ( $rootScope.$stateParams.layout == 'app-h') ;

    // Loading bar transition
    // ----------------------------------- 
    var thBar;
    $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
        if($('.wrapper > section').length) // check if bar container exists
          thBar = $timeout(function() {
            cfpLoadingBar.start();
          }, 0); // sets a latency Threshold
    });
    $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams) {
        event.targetScope.$watch("$viewContentLoaded", function () {
          $timeout.cancel(thBar);
          cfpLoadingBar.complete();
        });
    });


    // Hook not found
    $rootScope.$on('$stateNotFound',
      function(event, unfoundState, fromState, fromParams) {
          console.log(unfoundState.to); // "lazy.state"
          console.log(unfoundState.toParams); // {a:1, b:2}
          console.log(unfoundState.options); // {inherit:false} + default options
      });
    // Hook error
    $rootScope.$on('$stateChangeError',
      function(event, toState, toParams, fromState, fromParams, error){
        console.log(error);
      });
    // Hook success
    $rootScope.$on('$stateChangeSuccess',
      function(event, toState, toParams, fromState, fromParams) {
        // display new view from top
        $window.scrollTo(0, 0);
        // Save the route title
        $rootScope.currTitle = $state.current.title;
      });

    $rootScope.currTitle = $state.current.title;
    $rootScope.pageTitle = function() {
      var title = $rootScope.app.name + ' - ' + ($rootScope.currTitle || $rootScope.app.description);
      document.title = title;
      return title; 
    };

    // iPad may presents ghost click issues
    // if( ! browser.ipad )
      // FastClick.attach(document.body);

    // Close submenu when sidebar change from collapsed to normal
    $rootScope.$watch('app.layout.isCollapsed', function(newValue, oldValue) {
      if( newValue === false )
        $rootScope.$broadcast('closeSidebarMenu');
    });

    // Restore layout settings
    if( angular.isDefined($localStorage.layout) )
      $scope.app.layout = $localStorage.layout;
    else
      $localStorage.layout = $scope.app.layout;

    $rootScope.$watch("app.layout", function () {
      $localStorage.layout = $scope.app.layout;
    }, true);

    
    // Allows to use branding color with interpolation
    // {{ colorByName('primary') }}
    $scope.colorByName = colors.byName;

    // Internationalization
    // ----------------------

    $scope.language = {
      // Handles language dropdown
      listIsOpen: false,
      // list of available languages
      available: {
        'zh':    '中文',
        'en':       'English'
      },
      // display always the current ui language
      init: function () {
        var proposedLanguage = $translate.proposedLanguage() || $translate.use();
        var preferredLanguage = $translate.preferredLanguage(); // we know we have set a preferred one in app.config
        $scope.language.selected = $scope.language.available[ (proposedLanguage || preferredLanguage) ];
      },
      set: function (localeId, ev) {
        // Set the new idiom
        $translate.use(localeId);
        // save a reference for the current language
        $scope.language.selected = $scope.language.available[localeId];
        // finally toggle dropdown
        $scope.language.listIsOpen = ! $scope.language.listIsOpen;
      }
    };

    $scope.language.init();

    // Restore application classes state
    toggle.restoreState( $(document.body) );

    // cancel click event easily
    $rootScope.cancel = function($event) {
      $event.stopPropagation();
    };

    // Authentication begin
    $scope.getAuthentication = function() {
        var boleAuth = localStorage.getItem(BOLE_AUTHENTICATION);
        if (boleAuth == null) {
            return null;
        }
        boleAuth = JSON.parse(boleAuth);
        var now = new Date().getTime();
        if (now > boleAuth.timestamp + 60000 * 60) {
            localStorage.removeItem(BOLE_AUTHENTICATION);
            return null;
        }
        return boleAuth.auth;
    }

    $scope.checkPermission = function(state, auth) {
        var path = 'config',
            permission;
        angular.forEach(['app.competition', 'app.competitor', 'app.match'], function(data) {
            if (state.startsWith(data)) {
                path = 'data';
            }
        });

        var permission = window.encodeURIComponent(JSON.stringify(auth.permission));
        var url = BOLE_API_HOST + '/bole/v1/auth/check?caller=1001&path=' + path + '&permission=' + permission;
        $http.get(url).then(function(response) {
            var data = response.data;
            if (data.code != 200) {
                $state.go("page.error", {
                    name: '权限不足'
                });
            }
        });
        //$state.go("page.error", {name:'权限不足'});
    }

    $scope.exceptionStates = ['app.login', 'page.logout', 'page.error'];
    // 页面刷新时验证
    if ($.inArray($state.name, $scope.exceptionStates) < 0) {
        var auth = $scope.getAuthentication();
        if (auth) {
            console.log(auth)
            $scope.checkPermission($state.current.name, auth);
        } else {
            console.log('go state in main controller')
            $state.go("app.login", {
                state: $state.current.name
            });
        }
    }
    // 状态改变时验证
    $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
        if ($.inArray(toState.name, $scope.exceptionStates) >= 0) {
            return;
        }

        var auth = $scope.getAuthentication();
        if (auth) {
            console.log(auth)
            $scope.checkPermission(toState.name, auth);
        } else {
            event.preventDefault(); //defaultPrevented 
            console.log('go state in stateChangeStart')
            $state.go("app.login", {
                state: toState.name
            });
        }
    });
    // Authentication end
}]);
