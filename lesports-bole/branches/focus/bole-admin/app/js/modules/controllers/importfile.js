/**=========================================================
 * Module: competition.js
 * Controller for competition
 =========================================================*/

App.controller('ImportFileCtrl', ImportFileCtrl);

function ImportFileCtrl($scope, $filter, ngTableParams, $resource, $timeout, Notify, BOLE_API_HOST, BOLE__FILE_DATA_LIST, BOLE_DATA_IMPORT_FILE_TYPE_LIST, BOLE_DATA_PARTNER_TYPE, ngDialog) {
    'use strict';
    // required for inner references
    var vm = this;
    vm.tags = [];
    var dataImportHot = 'http://l0.57.10.226:8211';
    vm.typeList = BOLE_DATA_IMPORT_FILE_TYPE_LIST;
    vm.partnertypeList = BOLE_DATA_PARTNER_TYPE;

    vm.loadCompetitions = function (query) {
        if (vm.tags[0] == null) {
            var Api = $resource(dataImportHot + '/api/sms/data/dataImport/competitions?name=' + query + '&caller=1001');
            console.log(Api);
            Api.get(function (data) {
                if (data.code == 200) {
                    return data.data;
                }
                return [];
            });

        } else {
            return [];
        }
    }
    vm.search = function () {
        vm.tableParams.reload();
    };
    vm.importData = function (fileType, fileName, filePartertype) {
        var Api = $resource(dataImportHot + '/api/sms/data/parse?&caller=1001&file_name=' + fileName + '&file_type=' + fileType + '&parnter_type=' + filePartertype);
        Api.get(function (data) {
            if (data.code = 200 && data.data.result) {
                vm.search();
                $('.ngdialog').hide();
                Notify.alert('操作成功');
                status: 'success'
            }

            else {
                Notify.alert('操作失败！' + data.msg, {
                    status: 'warning'
                });
            }
        });
    };
    //文件列表
    vm.addNewFile = function (fileType, fileName, filePartertype) {
        $scope.competitonId1 = this.competitionId2;
        $scope.fileType1 = fileType;
        $scope.fileName1 = fileName;
        $scope.filePartertype1 = filePartertype;
        ngDialog.open({
            template: 'templateId',
            scope: $scope,
            controller: function ($scope) {
                $scope.file_type = $scope.$parent.fileType1;
                $scope.file_name = $scope.$parent.fileName1;
                $scope.partner_type = $scope.$parent.filePartertype1;
                $scope.searchFiles();
                $scope.searchFiles = function () {
                    var type = $scope.file_type;
                    var Api = $resource(dataImportHot + '/api/sms/data/parse/files?caller=1001&file_name=' + type);
                    Api.get(function (data) {
                        $timeout(function () {
                            if (data.code == 200) {
                                $scope.filenameList = data.data;
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


                $scope.savefile = function () {
                    var cid = $scope.$parent.competitonId1;
                    var fileType = $scope.file_type;
                    var fileName = $scope.file_name;
                    var partner_type = $scope.partner_type;
                    if (typeof(fileType) == "undefined" || typeof(fileName) == "undefined" || typeof(rightElement) == "attribute") {
                        Notify.alert('存在未填写字段，请检查')
                    }
                    saveConfig(cid, fileType, fileName, partner_type);
                };

            }
        });
    }


    function saveConfig(id, fileType, fileName, filePartertype) {
        var Api = $resource(BOLE_API_HOST + '/bole/v1/competitions/files?&&_method=POST&caller=1001&cid=' + id + '&file_name=' + fileName + '&file_type=' + fileType + '&partner_type=' + filePartertype);
        Api.get(function (data) {
            if (data.code = 200 && data.data.result) {
                vm.search();
                $('.ngdialog').hide();
                Notify.alert('操作成功');
                status: 'success'
            }

            else {
                Notify.alert('操作失败！' + data.msg, {
                    status: 'warning'
                });
            }
        });
    }

    vm.offline = function (key) {
        var cid = this.competitionId2;
        var Api = $resource(BOLE_API_HOST + '/bole/v1/competitions/files/offline?&&_method=PUT&caller=1001&cid=' + cid + '&file_name=' + key);
        console.log(Api);
        Api.get(function (data) {
            if (data.code = 200 && data.data.result) {
                vm.search();
                Notify.alert('操作成功')
                status: 'success'
            } else {
                Notify.alert('操作失败！' + data.msg, {
                    status: 'warning'
                });
            }
        });
    };
    vm.online = function (key) {
        var cid = this.competitionId2;
        var Api = $resource(BOLE_API_HOST + '/bole/v1/competitions/files/online?&&_method=PUT&caller=1001&cid=' + cid + '&file_name=' + key);
        console.log(Api);
        Api.get(function (data) {
            if (data.code = 200 && data.data.result) {
                vm.search();
                Notify.alert('操作成功')
                status: 'success'
            } else {
                Notify.alert('操作失败！' + data.msg, {
                    status: 'warning'
                });
            }
        });

    };
    vm.offline = function (key) {
        var cid = this.competitionId2;
        var Api = $resource(BOLE_API_HOST + '/bole/v1/competitions/files/online?&&_method=PUT&caller=1001&cid=' + cid + '&file_name=' + key);
        console.log(Api);
        Api.get(function (data) {
            if (data.code = 200 && data.data.result) {
                vm.search();
                Notify.alert('操作成功')
                status: 'success'
            } else {
                Notify.alert('操作失败！' + data.msg, {
                    status: 'warning'
                });
            }
        });

    };
    vm.tableParams = new ngTableParams({
        page: 1,            // show first page
        count: 50           // count per page
    }, {
        total: 0,           // length of data
        counts: [],         // hide page counts control
        getData: function ($defer, params) {
            var cid = vm.competitionId2;
            if (typeof (cid) == 'undefined') return;
            var Api = $resource(BOLE_API_HOST + '/bole/v1/competitions/files/' + cid + '?caller=1001');
            Api.get(params.url(), function (data) {
                $timeout(function () {
                    params.total(data.data.total);
                    $defer.resolve(data.data);
                }, 500);
            });
        }
    });

}


