/**=========================================================
 * Module: source-config.js
 * Controller for config
 =========================================================*/

App.controller('SourceConfigCtrl', SourceConfigCtrl);

function SourceConfigCtrl($scope, $resource, $http, $timeout, Notify, ngDialog, BOLE_API_HOST, SOURCE, CONTENT, OUTPUT_OPTION) {
    'use strict';
    // required for inner references
    var vm = this;
    vm.configMap = {};

    var RESOURCE_PREFIX = BOLE_API_HOST + '/bole/v1/config/output';

    vm.getConfig = function() {
        var Api = $resource(RESOURCE_PREFIX + '?caller=1001');
        //var Api = $resource('server/source-config.json');
        Api.get(function(data) {
            $timeout(function() {
                console.log(data)
                if (data.code == 200 && data.data) {
                    vm.configs = data.data;
                    angular.forEach(data.data, function(config) {
                        vm.configMap[config.site] = config;
                        if (config.outputOption == 'BLACKLIST' || config.outputOption == 'WHITELIST') {
                            vm.initTags(config.site, config.cids);
                        }
                    })
                } else {
                    $timeout(function() {
                        Notify.alert('查询配置失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 200);
                }
            }, 500);
        });
    }

    vm.loadCompetitions = function(query) {
        var searchUrl = BOLE_API_HOST + '/search/v1/s/bole/competitions?q=' + query + '&status=0&status=4&page=1&caller=1001';
        return $http.get(searchUrl).then(function(response) {
            var data = response.data;
            if (data.code == 200 && data.data.rows) {
                return data.data.rows;
            }
            return [];
        });
    }

    vm.initTags = function(site, cids) {
        var Api = $resource(BOLE_API_HOST + '/bole/v1/competitions/:id?caller=1001', {
            id: '@id'
        });
        vm.configMap[site].tags = [];
        angular.forEach(cids, function(cid) {
            Api.get({
                id: cid
            }, function(data) {
                $timeout(function() {
                    if (data.code == 200) {
                        var tag = {
                            "id": data.data.competition.id,
                            "name": data.data.competition.name
                        };
                        vm.configMap[site].tags.push(tag);
                    } else {
                        $timeout(function() {
                            Notify.alert('初始化标签失败！' + data.msg, {
                                status: 'warning'
                            });
                        }, 200);
                    }
                }, 500);
            });
        })
    };

    vm.configOne = function(site, content) {
        // 从tagMap中取cid列表
        var cids = [];
        angular.forEach(vm.configMap[site].tags, function(tag) {
                cids.push(tag.id);
            })
            //console.log(cids)
            //console.log(OUTPUT_OPTION[vm.radioMap[source]])
        var Api = $resource(RESOURCE_PREFIX + '?_method=PUT&caller=1001&site=:site&content=:content&output_option=:option&cids=:cids&priority=:priority&icon_name=:name&icon_url=:url', {
            site: '@site',
            content: '@content',
            option: '@option',
            cids: '@cids',
            priority:'@priority',
            name:'@name',
            url:'@url'
        });
        Api.get({
            site: site,
            content: CONTENT[content],
            option: OUTPUT_OPTION[vm.configMap[site].outputOption],
            cids: cids,
            priority:vm.configMap[site].priority?vm.configMap[site].priority:0,
            name:vm.configMap[site].iconName,
            url:vm.configMap[site].iconUrl
        }, function(data) {
            $timeout(function() {
                if (data.code == 200) {
                    $timeout(function() {
                        Notify.alert('操作成功！', {
                            status: 'success'
                        });
                    }, 200);
                } else {
                    $timeout(function() {
                        Notify.alert('操作失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 200);
                }
            }, 500);
        });
    }

    vm.deleteConfig = function(site, content) {
        var Api = $resource(RESOURCE_PREFIX + '?_method=DELETE&caller=1001&site=:site&content=:content', {
            site: '@site',
            content: '@content'
        });
        Api.get({
            site: site,
            content: CONTENT[content]
        }, function(data) {
            $timeout(function() {
                if (data.code == 200) {
                    $("#" + site).fadeOut(300, function() {
                        $(this).remove();
                    });
                } else {
                    $timeout(function() {
                        Notify.alert('操作失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 200);
                }
            }, 500);
        });
    }

    vm.openConfirm = function(site, content) {
        ngDialog.openConfirm({
            template: 'confirmDialog',
            className: 'ngdialog-theme-default'
        }).then(function(value) {
            vm.deleteConfig(site, content);
        }, function(reason) {});
    }
    vm.getConfig();
}
