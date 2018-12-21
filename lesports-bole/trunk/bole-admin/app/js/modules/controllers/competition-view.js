/**=========================================================
 * Module: competition.js
 * Controller for competition
 =========================================================*/

App.controller('CompetitionViewCtrl', CompetitionViewCtrl);

function CompetitionViewCtrl($scope, $resource, $http, $timeout, Notify, ngDialog, BOLE_API_HOST) {
    'use strict';
    // required for inner references
    var vm = this;
    vm.tags = [];
    var dataImportHot = 'http://l0.57.10.122:8211';

    vm.getCompetitionId = function () {
        var href = window.location.href;
        vm.competitionId = href.substring(href.lastIndexOf("/") + 1);
        return vm.competitionId;
    }

    var RESOURCE_PREFIX = BOLE_API_HOST + '/bole/v1/competitions';
    var Api = $resource(RESOURCE_PREFIX + '/:id?caller=1001', {
        id: '@id'
    });
    vm.getDetail = function () {
        Api.get({
            id: vm.competitionId
        }, function (data) {
            $timeout(function () {
                console.log(data);
                if (data.code == 200) {
                    $scope.competition = data.data.competition;
                    $scope.sourceUrl = data.data.sourceUrl;

                    var attachId = $scope.competition.attachId;
                    vm.initTags(attachId);
                    vm.initSiteOrder($scope.competition);
                } else {
                    $timeout(function () {
                        Notify.alert('操作失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    };

    vm.initTags = function (attachId) {
        if (null != attachId && attachId > 0) {
            Api.get({
                id: attachId
            }, function (data) {
                $timeout(function () {
                    var name = data.data.competition.name;
                    var tag = {
                        "id": attachId,
                        "name": name
                    };
                    vm.tags.push(tag);
                }, 500);
            });
        }
    };

    vm.updateStatus = function (status) {
        var Api = $resource(RESOURCE_PREFIX + '/:id?_method=PUT&status=:status&caller=1001', {
            id: '@id',
            status: '@status'
        }, {
            update: {
                method: 'GET'
            }
        });

        Api.update({
            id: vm.competitionId,
            status: status
        }, function (data) {
            $timeout(function () {
                console.log(data);
                if (data.code == 200 && data.data.id) {
                    $scope.competition = data.data;
                    $timeout(function () {
                        Notify.alert('修改状态成功！如已导出赛事，请至SMS后台维护赛事/赛季信息！', {
                            status: 'success'
                        });
                    }, 500);
                } else {
                    $timeout(function () {
                        Notify.alert('操作失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    }
    $scope.loadCompetitions = function (query) {
        if (vm.tags[0] == null) {
            var searchUrl = BOLE_API_HOST + '/search/v1/s/bole/competitions?name=' + query + '&status=0&status=4&page=1&caller=1001';
            return $http.get(searchUrl).then(function (response) {
                var data = response.data;
                if (data.code == 200 && data.data.rows) {
                    return data.data.rows;
                }
                return [];
            });
        } else {
            return [];
        }
    }


    vm.attachSms = function () {
        if (!(vm.tags[0] && vm.tags[0].id)) {
            $timeout(function () {
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
            id: vm.competitionId,
            attachId: vm.tags[0].id
        }, function (data) {
            $timeout(function () {
                console.log(data);
                if (data.code == 200 && data.data.id) {
                    $scope.competition = data.data;
                    $timeout(function () {
                        Notify.alert('关联SMS成功！', {
                            status: 'success'
                        });
                    }, 500);
                } else {
                    $timeout(function () {
                        Notify.alert('操作失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    }

    vm.cancelAttachSms = function () {
        var Api = $resource(RESOURCE_PREFIX + '/:id?_method=PUT&attach_to=0&caller=1001', {
            id: '@id'
        }, {
            update: {
                method: 'GET'
            }
        });

        Api.update({
            id: vm.competitionId
        }, function (data) {
            $timeout(function () {
                console.log(data);
                if (data.code == 200 && data.data.id) {
                    $scope.competition = data.data;
                    vm.tags = [];
                    $timeout(function () {
                        Notify.alert('取消关联成功！', {
                            status: 'success'
                        });
                    }, 500);
                } else {
                    $timeout(function () {
                        Notify.alert('操作失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    }

    vm.openExport = function () {
        // get export data
        if ($scope.competition.status != 'CREATED') {
            vm.updateStatus(2);
            return;
        }

        var Api = $resource(RESOURCE_PREFIX + '/export?name=:name&caller=1001', {
            name: '@name'
        });
        Api.get({
            name: $scope.competition.name
        }, function (data) {
            $timeout(function () {
                if (data.code == 200 && data.data) {
                    if (data.data.length > 1) {
                        vm.exportData = data.data;
                        vm.doOpenExportDialog();
                    } else {
                        vm.updateStatus(2);
                    }
                } else {
                    $timeout(function () {
                        Notify.alert('查询导出赛事失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    }

    vm.doOpenExportDialog = function () {
        // add checked attribute, default true
        angular.forEach(vm.exportData, function (item) {
            item.checked = true;
        })

        ngDialog.openConfirm({
            template: 'exportDialog',
            className: 'ngdialog-theme-default',
            data: vm.exportData
        }).then(function (value) {
            vm.export();
        }, function (reason) {
        });
    }

    vm.export = function () {
        console.log(vm.exportData)
        var idsParam = '';
        angular.forEach(vm.exportData, function (item) {
            if (item.checked) {
                idsParam += 'id=' + item.id;
                idsParam += '&';
            }
        })
        var Api = $resource(RESOURCE_PREFIX + '/export?_method=PUT&' + idsParam + 'caller=1001');
        Api.get(function (data) {
            $timeout(function () {
                if (data.code == 200 && data.data) {
                    angular.forEach(data.data, function (item) {
                        // update attributes
                        if (item.id == vm.competitionId) {
                            $scope.competition.status = item.status;
                            $scope.competition.smsId = item.smsId;
                            $scope.competition.updateAt = item.updateAt;
                        }
                    })
                    $timeout(function () {
                        Notify.alert('导出赛事成功！请至SMS后台维护赛事/赛季信息！', {
                            status: 'success'
                        });
                    }, 500);
                } else {
                    $timeout(function () {
                        Notify.alert('导出赛事失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    }

    $scope.loadSites = function (query) {
        var url = BOLE_API_HOST + '/bole/v1/lives/sites?cid=' + vm.competitionId + '&caller=1001';
        return $http.get(url).then(function (response) {
            var data = response.data;
            if (data.code == 200 && data.data) {
                var sites = [];
                angular.forEach(data.data, function (site) {
                    sites.push({
                        "text": site
                    });
                })
                return sites;
            } else {
                return [];
            }
        });
    }

    vm.initSiteOrder = function (competition) {
        vm.siteOrder = [];
        angular.forEach(competition.siteOrder, function (site) {
            vm.siteOrder.push({
                "text": site
            });
        })
    }

    vm.setSiteOrder = function (useDefault) {
        var order = '';
        if (useDefault) {
            order = 'default=true&';
        } else {
            angular.forEach(vm.siteOrder, function (site) {
                order += 'order=' + site.text + '&';
            })
        }

        var Api = $resource(RESOURCE_PREFIX + '/:id/order?_method=PUT&' + order + 'caller=1001');
        Api.get({
            id: vm.competitionId
        }, function (data) {
            $timeout(function () {
                if (data.code == 200 && data.data) {
                    $scope.competition = data.data;
                    vm.initSiteOrder($scope.competition);
                    $timeout(function () {
                        Notify.alert('操作成功！', {
                            status: 'success'
                        });
                    }, 500);
                } else {
                    $timeout(function () {
                        Notify.alert('操作失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    }

    vm.getCompetitionId();
    vm.getDetail();
}
