App.controller('ErrorController', ErrorController);

function ErrorController($scope, $stateParams) {
    'use strict';

    $scope.errorName = '系统未知错误';
    $scope.errorDescription = '请联系系统管理员处理';

    if ($stateParams.name) {
    	$scope.errorName = $stateParams.name;
    }
}
