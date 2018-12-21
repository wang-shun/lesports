/**=========================================================
 * Module: NGTableCtrl.js
 * Controller for ngTables
 =====================View====================================*/

App.controller('MatchListCtrl', MatchListCtrl);
App.controller('MatchViewCtrl', MatchViewCtrl);

function MatchListCtrl($scope, $filter, ngTableParams, $resource, $timeout, Notify,  BOLE_API_HOST, BOLE_STATUS_LIST) {
    'use strict';
    // required for inner references
    var vm = this;
    vm.statusList = BOLE_STATUS_LIST;

    vm.search = function () {
        var page = vm.tableParams.page();
        if (page != 1) {
            vm.tableParams.page(1);
        } else {
            vm.tableParams.reload();
        }
    }

    vm.view = function (matchId) {
        console.log("matchId : " + matchId)
        window.open("http://" + window.location.host + "/#/app/match/view/" + matchId);
    };

    vm.getStatusName = function(id){
        for (var i = 0; i < vm.statusList.length; i++) {
            var item = vm.statusList[i];
            if (item["id"] == id) {
                return item["name"];
            }
        }
        return  "";
    };

    vm.tableParams = new ngTableParams({
        page: 1,            // show first page
        count: 50           // count per page
    }, {
        total: 0,           // length of data
        counts: [],         // hide page counts control
        getData: function ($defer, params) {
            var q = {"q": vm.search_q,"status":vm.search_status};
            var Api = $resource(BOLE_API_HOST + '/search/v1/s/bole/matches/?caller=1001', q);
            Api.get(params.url(), function (data) {
                $timeout(function () {
                    params.total(data.data.total);
                    $defer.resolve(data.data.rows);
                }, 500);
            });
        }
    });

}

function MatchViewCtrl($scope, $filter, $http, $resource, $timeout, $stateParams, ngDialog, Notify, BOLE_API_HOST) {
    'use strict';
    // required for inner references
    var vm = this;
    var RESOURCE_PREFIX = BOLE_API_HOST + '/bole/v1/matches';
    vm.tags = [];

    vm.getMatchId = function () {
        vm.matchId = $stateParams.mid;
        return vm.matchId;
    };

    var Match = $resource(RESOURCE_PREFIX + '/:matchId?caller=1001', {matchId: '@id'});
    Match.get({"matchId": vm.getMatchId()}, function (data) {
        $timeout(function () {
            if (data.code == '200') {
                vm.match = data.data;
                $scope.sourceUrl = data.data.sourceUrl;
            } else {
                $timeout(function () {
                    Notify.alert('操作失败！' + data.msg,{status: 'warning'});
                }, 500);                
            }
        }, 500);
    });

    vm.attachSms = function () {
        if (vm.tags.length == 0) {
            $timeout(function(){
                Notify.alert('请选择需要关联的赛事！',{status: 'warning'});
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

        Api.update({id: vm.matchId, attachId: vm.tags[0].id}, function (data) {
            $timeout(function () {
                console.log(data);
                if (data.code == 200 && data.data.id) {
                    vm.match.match = data.data;
                    $timeout(function(){
                        Notify.alert('关联SMS成功！',{status: 'success'});
                    }, 500);
                } else {
                    $timeout(function(){
                        Notify.alert('操作失败！' + data.msg,{status: 'warning'});
                    }, 500);
                }
            }, 500);
        });
    }

    vm.loadTags = function (query) {
        if (vm.tags.length == 0) {
            var searchUrl = BOLE_API_HOST + '/search/v1/s/bole/matches/?page=1&status=0&status=4&caller=1001&name=' + query;
            return $http.get(searchUrl).then(function (response) {
                var data = response.data;
                if (data.code == 200 && data.data.rows) {
                    return data.data.rows;
                }
                return [];
            });
        }
        return [];
    };

    vm.updateStatus = function (status) {
        var Api = $resource(RESOURCE_PREFIX + '/:id?_method=PUT&status=:status&caller=1001', {id: '@id', status: '@status'}, {
            update: {
                method: 'GET'
            }
        });
        Api.update({id: vm.matchId, status: status}, function (data) {
            $timeout(function () {
                console.log(data);
                if (data.code == 200 && data.data.id) {
                    vm.match.match = data.data;
                    $timeout(function(){
                        Notify.alert('更新状态成功！如已导出比赛，请至SMS后台维护比赛信息！',{status: 'success'});
                    }, 500);
                } else {
                    $timeout(function(){
                        Notify.alert('操作失败！' + data.msg,{status: 'warning'});
                    }, 500);
                }
            }, 500);
        });
    };

  vm.cancelAttachSms = function() {
    var Api = $resource(RESOURCE_PREFIX + '/:id?_method=PUT&attach_to=0&caller=1001', {id:'@id'}, {
      update : {
        method: 'GET'
      }
    });

    Api.update({id:vm.matchId}, function(data) {
        $timeout(function() {
          console.log(data);
          if (data.code == 200 && data.data.id) {
            vm.match.match = data.data;
            vm.tags = [];
            $timeout(function(){
              Notify.alert('取消关联成功！',{status: 'success'});
            }, 500);
          } else {
            $timeout(function(){
              Notify.alert('操作失败！' + data.msg,{status: 'warning'});
            }, 500);
          }
        }, 500);
      });    
  }

  vm.openExport = function() {
        if (vm.match.match.status != 'CREATED') {
            vm.updateStatus(2);
            return;
        }
        // get export data
        var Api = $resource(RESOURCE_PREFIX + '/export?start_time=:startTime&com1=:com1&com2=:com2&caller=1001', {
            startTime: '@startTime',
            com1: '@com1',
            com2: '@com2',
        });
        Api.get({
            startTime: vm.match.match.startTime,
            com1: vm.match.match.competitors[0],
            com2: vm.match.match.competitors[1],
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
                        Notify.alert('查询导出比赛失败！' + data.msg, {
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
        var Api = $resource(RESOURCE_PREFIX + '/export?_method=PUT&' + idsParam + 'caller=1001');
        Api.get(function(data) {
            $timeout(function() {
                if (data.code == 200 && data.data) {
                    angular.forEach(data.data, function(item) {
                        // update attributes
                        if (item.id == vm.match.match.id) {
                            vm.match.match.status = item.status;
                            vm.match.match.smsId = item.smsId;
                            vm.match.match.updateAt = item.updateAt;
                        }
                    })
                    $timeout(function() {
                        Notify.alert('导出比赛成功！请至SMS后台维护比赛信息！', {
                            status: 'success'
                        });
                    }, 500);
                } else {
                    $timeout(function() {
                        Notify.alert('导出比赛失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    }

    vm.statusBtnClass = {"ONLINE":"btn btn-xs btn-success","OFFLINE":"btn btn-xs btn-warning"};
    vm.turnLiveOnlineStatus = function(index,live) {
        var Api = $resource(BOLE_API_HOST + '/bole/v1/lives/:id?_method=PUT&status=:status&caller=1001', {
            id: '@id',
            status: '@status'
        });

        var targetStatus = live.status=='ONLINE'?'OFFLINE':'ONLINE';
        Api.get({
            id: live.id,
            status: targetStatus
        }, function(data) {
            $timeout(function() {
                if (data.code == 200 && data.data) {
                    vm.match.lives[index] = data.data;
                    $timeout(function() {
                        Notify.alert('更新状态成功！' + data.msg, {
                            status: 'success'
                        });
                    }, 500);
                } else {
                    $timeout(function() {
                        Notify.alert('更新状态失败！' + data.msg, {
                            status: 'warning'
                        });
                    }, 500);
                }
            }, 500);
        });
    }
}
