/**=========================================================
 * Module: competitor-view.js
 * Controller for competitor-view
 =========================================================*/

App.controller('CompetitorViewCtrl', CompetitorViewCtrl);

function CompetitorViewCtrl($scope, $http, $filter, $resource, $timeout, Notify, ngDialog, BOLE_API_HOST, BOLE_COMPETITOR_TYPE) {
    'use strict';
    // required for inner references
    var vm = this;
    var RESOURCE_PREFIX = BOLE_API_HOST + '/bole/v1/competitors';
    vm.tags = [];
    vm.types = BOLE_COMPETITOR_TYPE;

    vm.getCompetitorId = function() {
        var href = window.location.href;
        vm.competitorId = href.substring(href.lastIndexOf("/") + 1);
        return vm.competitorId;
    };

    vm.Api = $resource(RESOURCE_PREFIX + '/:id?caller=1001', {
        id: '@id'
    });
    vm.getDetail = function() {
        vm.Api.get({
            id: vm.competitorId
        }, function(data) {
            $timeout(function() {
                if (data.code == 200) {
                    console.log(data.data);
                    $scope.competitor = data.data.competitor;
                    $scope.sourceUrl = data.data.sourceUrl;

                    var attachId = $scope.competitor.attachId;
                    vm.initTags(attachId);
                } else {
                    $timeout(function() {
                        Notify.alert('操作失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    };

    vm.initTags = function(attachId) {
        if (null != attachId && attachId > 0) {
            vm.Api.get({
                id: attachId
            }, function(data) {
                $timeout(function() {
                    var name = data.data.competitor.name;
                    var tag = {
                        "id": attachId,
                        "name": name
                    };
                    vm.tags.push(tag);
                }, 500);
            });
        }
    };

    vm.confirmCreated = function() {
        vm.Api.update({
            id: vm.competitorId,
            status: 'CONFIRMED'
        }, function(data) {
            $timeout(function() {
                console.log(data.data);
                $scope.competitor = data.data;
            }, 500);
        });
    }

    vm.getDetail(vm.getCompetitorId());

    vm.loadTags = function(query) {
        if (vm.tags.length == 0) {
            var searchUrl = BOLE_API_HOST + '/search/v1/s/bole/competitors/?page=1&count=50&status=0&status=4&caller=1001&name=' + query;
            return $http.get(searchUrl).then(function(response) {
                var data = response.data;
                if (data.code == 200 && data.data.rows) {
                    return data.data.rows;
                }
                return [];
            });
        }
        return [];
    };

    vm.getTypeName = function(typeValue) {
        for (var i = 0; i < BOLE_COMPETITOR_TYPE.length; i++) {
            var item = BOLE_COMPETITOR_TYPE[i];
            if (item("id") == typeValue) {
                return item("name");
            }
        }
        return "";
    }

    vm.updateStatus = function(status) {
        var Api = $resource(RESOURCE_PREFIX + '/:id?_method=PUT&status=:status&type=:type&caller=1001', {
            id: '@id',
            status: '@status',
            type: '@type'
        }, {
            update: {
                method: 'GET'
            }
        });
        Api.update({
            id: vm.competitorId,
            status: status,
            type: $scope.competitor.type
        }, function(data) {
            $timeout(function() {
                console.log(data);
                if (data.code == 200 && data.data.id) {
                    $scope.competitor = data.data;
                    $timeout(function() {
                        Notify.alert('修改状态成功！如已导出对阵，请至SMS后台维护对阵信息！', {
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
    };

    vm.attachSms = function() {
        if (vm.tags.length == 0) {
            $timeout(function() {
                Notify.alert('请选择需要关联的赛事！', {
                    status: 'warning'
                });
            }, 500);
            return;
        }

        var Api = $resource(RESOURCE_PREFIX + '/:id?_method=PUT&attach_to=:attachId&caller=1001', {
            id: '@id',
            attachId: '@attachId'
        }, {
            update: {
                method: 'GET'
            }
        });

        Api.update({
            id: vm.competitorId,
            attachId: vm.tags[0].id
        }, function(data) {
            $timeout(function() {
                console.log(data);
                if (data.code == 200 && data.data.id) {
                    $scope.competitor = data.data;
                    $timeout(function() {
                        Notify.alert('关联SMS成功！', {
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

    vm.cancelAttachSms = function() {
        var Api = $resource(RESOURCE_PREFIX + '/:id?_method=PUT&attach_to=0&caller=1001', {
            id: '@id'
        }, {
            update: {
                method: 'GET'
            }
        });

        Api.update({
            id: vm.competitorId
        }, function(data) {
            $timeout(function() {
                console.log(data);
                if (data.code == 200 && data.data.id) {
                    $scope.competitor = data.data;
                    vm.tags = [];
                    $timeout(function() {
                        Notify.alert('取消关联成功！', {
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

    vm.openExport = function() {
        if(!vm.checkType()) {
            return;
        }
        if ($scope.competitor.status != 'CREATED') {
            vm.updateStatus(2);
            return;
        }
        // get export data
        //vm.exportData=[{"id":165401041,"name":"南安普敦","nickname":"南安普敦","gameFType":100015000,"status":"CREATED","sourceMatchId":"5684d1a80cf212bcf4140f09","smsId":0,"attachId":0,"mappingId":0,"createAt":"20151231145640","updateAt":"20151231145640"},{"id":165418041,"name":"南安普敦","nickname":"南安普敦","gameFType":100015000,"status":"CREATED","sourceMatchId":"5684d1ac0cf2c469281c9828","smsId":0,"attachId":0,"mappingId":0,"createAt":"20151231145644","updateAt":"20151231145644"}]
        //vm.doOpenExportDialog();
        var Api = $resource(RESOURCE_PREFIX + '/export?name=:name&caller=1001', {
            name: '@name'
        });
        Api.get({
            name: $scope.competitor.name
        }, function(data) {
            $timeout(function() {
                if (data.code == 200 && data.data) {
                    if (data.data.length > 1) {
                        vm.exportData = data.data;
                        vm.doOpenExportDialog();
                    } else {
                        vm.updateStatus(2);
                    }
                } else {
                    $timeout(function() {
                        Notify.alert('查询导出对阵失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    }

    vm.doOpenExportDialog = function() {
        // add checked attribute, default true
        angular.forEach(vm.exportData, function(item) {
            item.checked = true;
        })

        ngDialog.openConfirm({
            template: 'exportDialog',
            className: 'ngdialog-theme-default',
            data: vm.exportData
        }).then(function(value) {
            vm.export();
        }, function(reason) {});
    }

    vm.export = function() {
        console.log(vm.exportData)
        var idsParam = '';
        angular.forEach(vm.exportData, function(item) {
            if (item.checked) {
                idsParam += 'id=' + item.id;
                idsParam += '&';
            }
        })
        var Api = $resource(RESOURCE_PREFIX + '/export?_method=PUT&type=:type&' + idsParam + 'caller=1001', {
            type: '@type'
        });
        Api.get({
            type: $scope.competitor.type
        }, function(data) {
            $timeout(function() {
                if (data.code == 200 && data.data) {
                    angular.forEach(data.data, function(item) {
                        // update attributes
                        if (item.id == vm.competitorId) {
                            $scope.competitor.status = item.status;
                            $scope.competitor.smsId = item.smsId;
                            $scope.competitor.type = item.type;
                            $scope.competitor.updateAt = item.updateAt;
                        }
                    })
                    $timeout(function() {
                        Notify.alert('导出对阵成功！请至SMS后台维护对阵信息！', {
                            status: 'success'
                        });
                    }, 500);
                } else {
                    $timeout(function() {
                        Notify.alert('导出对阵失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    }

    vm.checkType = function() {
        console.log($scope.competitor.type)
        if (!$scope.competitor.type) {
            $timeout(function() {
                Notify.alert('请选择球员/球队！', {
                    status: 'warning'
                });
            }, 500);
            return false;
        }
        return true;
    }
}
