App.controller('LoginController', LoginController);
App.controller('LogoutController', LogoutController);

function LoginController($state, $stateParams, $http, BOLE_AUTHENTICATION, CAS_HOST, BOLE_API_HOST) {
    'use strict';
    var vm = this;

    vm.setAuthentication = function(auth) {
        var boleAuth = {};
        boleAuth.auth = auth;
        boleAuth.timestamp = new Date().getTime();
        localStorage.setItem(BOLE_AUTHENTICATION, JSON.stringify(boleAuth));
    }

    if (!$stateParams.cas_ticket) {
        // 获取cas_ticket
        var retUrl = 'http://' + window.location.host + '/#/app/login';
        if ($stateParams.state) {
            retUrl += '?state=' + $stateParams.state;
        }
        retUrl = window.encodeURIComponent(retUrl);
        var ssoUrl = 'http://' + CAS_HOST + '/sso/login/?ret_url=' + retUrl + '&app=sms';
        window.location.href = ssoUrl;
    } else {
        // 根据cas_ticket获取Authentication
        console.log($stateParams.cas_ticket);
        var url = BOLE_API_HOST + '/bole/v1/auth/auth?caller=1001&cas_ticket=' + $stateParams.cas_ticket;
        $http.get(url).then(function(response) {
            var data = response.data;
            if (data.code == 200 && data.data) {
                console.log(data.data);
                vm.setAuthentication(data.data);
                var toState = $stateParams.state;
                if (!toState || toState == 'app.login') {
                    toState = 'app.competition';
                }
                $state.go(toState);
            }
        });
    }
}

function LogoutController($state, BOLE_AUTHENTICATION) {
    'use strict';

    var vm = this;

    vm.logout = function() {
        localStorage.removeItem(BOLE_AUTHENTICATION);
    }

    vm.logout();
    $state.go("app.login");
}
