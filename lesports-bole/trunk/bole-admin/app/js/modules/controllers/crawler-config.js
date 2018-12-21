/**=========================================================
 * Module: crawler-config.js
 * Controller for config
 =========================================================*/

App.controller('CrawlerConfigCtrl', CrawlerConfigCtrl);

function CrawlerConfigCtrl($scope, $resource, $http, $timeout, Notify, BOLE_API_HOST) {
    'use strict';
    // required for inner references
    var vm = this;

    var RESOURCE_PREFIX = BOLE_API_HOST + '/bole/v1/config/crawler';
    vm.config = function(source, content, $event) {
        var enabled = $($event.target).is(':checked');
        var Api = $resource(RESOURCE_PREFIX + '?_method=PUT&source=:source&content=:content&enabled=:enabled&caller=1001', {
            source: '@source'
        }, {
            content: '@content'
        }, {
            enabled: '@enabled'
        });
        Api.get({
            source: source,
            content: content,
            enabled: enabled
        }, function(data) {
            $timeout(function() {
                console.log(data);
                if (data.code == 200) {
                    $timeout(function() {
                        Notify.alert('操作成功！', {
                            status: 'success'
                        });
                    }, 500);
                } else {
                    $timeout(function() {
                        Notify.alert('操作失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    }

    vm.getConfig = function() {
        var Api = $resource(RESOURCE_PREFIX + '?caller=1001');
        Api.get(function(data) {
            $timeout(function() {
                if (data.code == 200 && data.data) {
                    angular.forEach(data.data, function(config) {
                        if (config.source == "ZHIBO8") {
                            vm.matchZhibo8 = config.enabled;
                        } else if (config.source == "SINA") {
                            vm.matchSina = config.enabled;
                        } else if (config.source == "QQ") {
                            vm.matchQQ = config.enabled;
                        }
                    })
                } else {
                    $timeout(function() {
                        Notify.alert('查询配置失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    }

    vm.getConfig();
    // angular.forEach([{
    //     "id": "56809a40a9baa8ae138b4568",
    //     "source": "ZHIBO8",
    //     "content": "MATCH",
    //     "enabled": false,
    //     "thread": 5
    // }, {
    //     "id": "56809a4ea9baa858368b4567",
    //     "source": "SINA",
    //     "content": "MATCH",
    //     "enabled": true,
    //     "thread": 5
    // }, {
    //     "id": "56809a56a9baa855368b4567",
    //     "source": "QQ",
    //     "content": "MATCH",
    //     "enabled": true,
    //     "thread": 5
    // }], function(config) {

    //     if (config.source == "ZHIBO8") {
    //         vm.matchZhibo8 = config.enabled;
    //     } else if (config.source == "SINA") {
    //         vm.matchSina = config.enabled;
    //     } else if (config.source == "QQ") {
    //         vm.matchQQ = config.enabled;
    //     }
    // })
}
