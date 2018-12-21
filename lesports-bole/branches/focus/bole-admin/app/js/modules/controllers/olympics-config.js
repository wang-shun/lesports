/**=========================================================
 * Module: competition.js
 * Controller for competition
 =========================================================*/

App.controller('ConfigCtrl', ConfigCtrl);

function ConfigCtrl($scope, $filter, ngTableParams, $resource, $timeout, Notify, BOLE_API_HOST, BOLE_CONFIG_TYPE_LIST, BOLE_FORMATTER_TYPE_LIST, ngDialog) {
    'use strict';
    // required for inner references
    var vm = this;
    vm.configTypeList = BOLE_CONFIG_TYPE_LIST;
    gameFtypeList();

    vm.search = function () {
        vm.tableParams.reload();
    };
    vm.searchgameSType = function () {
        var parentId = vm.discipline_id;
        var Api = $resource(BOLE_API_HOST + '/bole/v1/olympics/dicts?caller=1001&parent_id=' + parentId);
        Api.get(function (data) {
            $timeout(function () {
                if (data.code == 200) {
                    vm.gameStypeList = data.data;
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
    vm.searchDiscipline = function () {
        var parentId = vm.gameFtype_id;
        var Api = $resource(BOLE_API_HOST + '/bole/v1/olympics/dicts?caller=1001&parent_id=' + parentId);
        Api.get(function (data) {
            $timeout(function () {
                if (data.code == 200) {
                    vm.disciplineList = data.data
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
    vm.copy = function () {
        var copyGameSTypeId = vm.copyStype_id;
        var currentGameSTypeId = vm.gameStype_id;
        console.log(copyGameSTypeId);
        if (typeof(copyGameSTypeId) == "undefined") {
            Notify.alert("请先选择所要复制的小项");
        }
        else if (typeof(currentGameSTypeId) == "undefined") {
            Notify.alert("请选择当前项目");
        }
        else {
            var result = confirm("复制功能会覆盖当前小项的配置，确认复制吗？")
            if (!result)return;
            var Api = $resource(BOLE_API_HOST + '/bole/v1/olympics/configCopy?_method=PUT&caller=1001&game_s_type=' + currentGameSTypeId + '&copy_id=' + copyGameSTypeId);
            console.log(Api);
            Api.get(function (data) {
                $timeout(function () {
                    if (data.code == 200 && data.data.result) {
                        Notify.alert("操作成功！");
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

    };
    vm.findPath = function (leftPath, op, rightpath) {
        var result = '路径1：' + leftPath + '\n';
        if(typeof (op)!="undefined"){
            result=result+'操作符号：' + op + '\n';
        }
        if(typeof (rightpath)!="undefined"){
            result=result+'路径2：' + rightpath + '\n';
        }
            confirm("确认当前路径为："+result);
//        confirm()
//        Notify.alert('路径1：' + leftPath + '\n' + '操作符号：' + op + '\n' + '路径2：' + rightpath + '\n')
    };


    function gameFtypeList() {
        var Api = $resource(BOLE_API_HOST + '/bole/v1/olympics/dicts?caller=1001&parent_id=109795000');
        console.log(Api);
        Api.get(function (data) {
            $timeout(function () {
                if (data.code == 200) {
                    vm.gameFtypeList = data.data;
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

    //公式窗口
    vm.clickOpOpen = function (leftElement, op, rightElement, attribute, rename, formatterType) {
        $scope.type = vm.type;
        $scope.gameStype_id = vm.gameStype_id;
        $scope.element_path = leftElement;
        $scope.op = op;
        $scope.right_element_path = rightElement;
        $scope.code = attribute;
        $scope.rename = rename;
        $scope.formatterType = formatterType;
        ngDialog.open({
            template: 'templateOpId',
            scope: $scope,
            controller: function ($scope) {
                $scope.element_path = $scope.$parent.element_path;
                $scope.op = $scope.$parent.op;
                $scope.right_element_path = $scope.$parent.right_element_path;
                $scope.code = $scope.$parent.code;
                $scope.annotation = $scope.$parent.rename;
                $scope.formatter_type = $scope.$parent.formatterType;
                $scope.saveConfig1 = function () {
                    var leftElement = $scope.element_path;
                    var op = $scope.op;
                    var rightElement = $scope.right_element_path;
                    var attribute = $scope.code;
                    var rename = $scope.annotation;
                    var formatterType = "";
                    console.log(gameSType, settingType, leftElement, op, rightElement, rename, attribute);
                    if (typeof(leftElement) == "undefined" || typeof(rename) == "undefined"|| typeof(rename) == "attribute") {
                        Notify.alert('左操作数和code，rename为必填字段，请检查')
                    }
                    else {
                        var settingType = $scope.$parent.type;
                        var gameSType = $scope.$parent.gameStype_id;
                        console.log(gameSType, settingType, leftElement, op, rightElement, rename, attribute);
                        if (typeof ($scope.$parent.code) != undefined) {
                            saveConfig(gameSType, settingType, leftElement, op, rightElement, rename, attribute, formatterType);
                        }
                        else {
                            updateConfig(gameSType, settingType, $scope.$parent.code, leftElement, op, rightElement, rename, attribute, formatterType);
                        }
                    }
                };

            }
        });
    }


    function saveConfig(gameSType, settingType, element_path, op, right_element_path, rename, attribute, formatterType) {
        var Api = $resource(BOLE_API_HOST + '/bole/v1/olympics/configs?_method=PUT&caller=1001&game_s_type=' + gameSType + '&name=' + settingType + '&element_path=' + element_path + '&op=' + op + '&right_element_path=' + right_element_path + '&attribute_re_name=' + rename + '&position_key=' + attribute + '&formatter_type=' + formatterType);
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

    vm.clickToOpen = function (currentPath, reName, formatterType) {
        $scope.type = vm.type;
        $scope.gameStype_id = vm.gameStype_id;
        $scope.typeList = BOLE_FORMATTER_TYPE_LIST;
        $scope.path = currentPath;
        if (typeof(currentPath) == "undefined") {
            $scope.condition_name1 = "";
            $scope.attribute_name1 = "";
            $scope.annotation1 = "";
            $scope.formatterType = "";
        }
        else {
            var r = /(?:@Code='(\w+)')/g;
            var list = [];
            currentPath.replace(r, function (m, $1) {
                list.push($1)
            });
            if (list.length > 0) {
                $scope.condition_name1 = list.length == 2 ? list[0] : "";
                $scope.attribute_name1 = list.length == 2 ? list[1] : list[0];
            }
            $scope.annotation1 = reName;
            $scope.formatterType = formatterType;
        }


        ngDialog.open({
            template: 'templateId',
            scope: $scope,
            controller: function ($scope) {
                $scope.formatter_list = $scope.$parent.typeList;
                $scope.formatter_type = $scope.$parent.formatterType;
                $scope.annotation = $scope.$parent.annotation1;
                getAttributeData();
                getConditionData();
                function getConditionData() {
                    var Api = $resource(BOLE_API_HOST + '/bole/v1/olympics/setting?caller=1001&&game_s_type=' + $scope.$parent.gameStype_id + '&setting_name=' + $scope.$parent.type + 1);
                    Api.get(function (data) {
                        $timeout(function () {
                            if (data.code == 200) {
                                $scope.condition_list = data.data;
                                $scope.condition_name = $scope.$parent.condition_name1;
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

                function getAttributeData() {
                    var Api = $resource(BOLE_API_HOST + '/bole/v1/olympics/setting?caller=1001&&game_s_type=' + $scope.$parent.gameStype_id + '&setting_name=' + $scope.$parent.type);
                    Api.get(function (data) {
                        $timeout(function () {
                            if (data.code == 200) {
                                $scope.attribute_list = data.data;
                                $scope.attribute_name = $scope.$parent.attribute_name1;
                            }
                            else {
                                $timeout(function () {
                                    Notify.alert('操作失败！' + data.msg, {
                                        status: 'warning'
                                    });
                                }, 500);
                            }
                        }, 500);
                    });
                }

                $scope.saveConfig = function () {
                    var attribute = $scope.attribute_name;
                    var attributePath = $scope.attribute_path;
                    if (typeof(attribute) == "undefined" && typeof(attributePath) == "undefined") {
                        Notify.alert('当前属性值不能为空')
                    }
                    else {
                        var settingType = $scope.$parent.type;
                        var gameSType = $scope.$parent.gameStype_id;
                        var conditionName = $scope.condition_name;
                        var attribute = $scope.attribute_name;
                        var newPath = $scope.attribute_path;
                        var rename = $scope.annotation;
                        var formatterType = $scope.formatter_type;
                        var element_path = typeof(newPath) == 'undefined' ? getElementPath(settingType, conditionName, attribute, formatterType) : newPath;
                        var currentPath = $scope.$parent.path;
                        saveSimpleConfig(gameSType, settingType, element_path, rename, attribute, formatterType);
                    }
                };

            }
        });
    }


    function getElementPath(settingType, conditionName, attribute, formatter) {
        var temp;
        if (settingType == 'match_config') {
            if (formatter == 'ListFormatter') {
                temp = '/OdfBody/Competition/ExtendedInfos/ExtendedInfo[@Code=\'' + attribute + '\'and @Pos=\'?\']/@Value';
            }
            temp = '/OdfBody/Competition/ExtendedInfos/ExtendedInfo[@Code=\'' + attribute + '\']/@Value';
        }
        else if (settingType == 'team_config') {
            temp = './Competitor/EventUnitEntry[@Code=\'' + attribute + '\']/@Value';
        }
        else if (settingType == 'player_config') {
            temp = './EventUnitEntry[@Code=\'' + attribute + '\']/@Value';
        }
        else if (settingType == 'team_stats_config') {
            if (typeof(conditionName) == "undefined" || conditionName == "" || conditionName == null || conditionName == "null") {
                temp = './Competitor/Stats/Stat[@Code=\'' + attribute + '\'and(@Pos=\'TOT\'or not(@Pos))]/@Value';
            }
            else {
                temp = './Competitor/Stats/Stat[@Code=\'' + conditionName + '\'and (@Pos=\'TOT\'or not(@Pos))]/ExtendedStat[@Code=\'' + attribute + '\']/@Value';
            }
        }
        else if (settingType == 'player_stats_config') {
            if (typeof(conditionName) == "undefined" || conditionName == "" || conditionName == null || conditionName == "null") {
                temp = './Stats/Stat[@Code=\'' + attribute + '\'and (@Pos=\'TOT\' or not(@Pos))]/@Value';
            }
            else {
                temp = './Stats/Stat[@Code=\'' + conditionName + '\'and (@Pos=\'TOT\'or not(@Pos))]/ExtendedStat[@Code=\'' + attribute + '\']/@Value';
            }
        }
        else {
            if (typeof(conditionName) == "undefined" || conditionName == "" || conditionName == null || conditionName == "null") {
                if (typeof(formatter) == "undefined" || formatter == "null" || formatter == "") {
                    temp = './Competitor/Composition/Athlete/ExtendedResults/ExtendedResult[@Code=\'' + attribute + '\']/@Value';
                }
                else {
                    temp = './Competitor/Composition/Athlete/ExtendedResults/ExtendedResult[@Code=\'' + attribute + '\'and @Pos=\'?\']/@Value';
                }
            }
            else {
                if (typeof(formatter) != "undefined" && formatter == 'ListFormatter') {
                    temp = './ExtendedResults/ExtendedResult[@Code=\'' + conditionName + '\'and @Pos=\'?\']/Extension[@Code=\'' + attribute + '\']/@Value';
                }

                temp = './ExtendedResults/ExtendedResult[@Code=\'' + conditionName + '\']/Extension[@Code=\'' + attribute + '\']/@Value';
            }
        }
        return temp;
    }

    function saveSimpleConfig(gameSType, settingType, element_path, rename, attribute, formatterType) {
        if (typeof(formatterType) == "undefined" || formatterType == "undefined" || formatterType == null || formatterType == "null") {
            formatterType = "";
        }
        var Api = $resource(BOLE_API_HOST + '/bole/v1/olympics/configs?_method=PUT&caller=1001&game_s_type=' + gameSType + '&name=' + settingType + '&element_path=' + element_path + '&attribute_re_name=' + rename + '&position_key=' + attribute + '&formatter_type=' + formatterType);
        Api.get(function (data) {
            if (data.code = 200 && data.data.result) {
                vm.search();
                $('.ngdialog').hide();
                Notify.alert('操作成功');
                status: 'success'
            }
            else if (!data.data.result) {
                vm.search();
                Notify.alert('属性已存在');
                status: 'success'
            }

            else {
                Notify.alert('操作失败！' + data.msg, {
                    status: 'warning'
                });
            }
        });
    }

    function updateConfig(gameSType, settingType, oldCode, leftElement, op, rightElement, rename, attribute, formatterType) {
        if (typeof(formatterType) == "undefined" || formatterType == "undefined" || formatterType == null || formatterType == "null") {
            formatterType = "";
        }
        var Api = $resource(BOLE_API_HOST + '/bole/v1/olympics/configs/update?_method=PUT&caller=1001&game_s_type=' + gameSType + '&name=' + settingType + '&old_path=' + oldCode + '&element_path=' + leftElement + '&op=' + op + '&right_element_path=' + rightElement + '&attribute_re_name=' + rename + '&position_key=' + attribute + '&formatter_type=' + formatterType);
        Api.get(function (data) {
            if (data.code = 200 && data.data.result) {
                vm.search();
                $('.ngdialog').hide();
                Notify.alert('操作成功');
                status: 'success'
            }
            else if (!data.data.result) {
                Notify.alert('属性已存在');
                status: 'success'
            }

            else {
                Notify.alert('操作失败！' + data.msg, {
                    status: 'warning'
                });
            }
        });
    }


    vm.delete = function (key) {
        var Api = $resource(BOLE_API_HOST + '/bole/v1/olympics/configs?&&_method=DELETE&caller=1001&key=' + key + '&name=' + vm.type + '&game_s_type=' + vm.gameStype_id);
        console.log(Api);
        Api.get(function (data) {
            if (data.code = 200 && data.data.result) {
                vm.search();
                Notify.alert('删除成功')
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
        total: 0,           // length of cooperation
        counts: [],         // hide page counts control
        getData: function ($defer, params) {
            var Api = $resource(BOLE_API_HOST + '/bole/v1/olympics?caller=1001&game_s_type=' + vm.gameStype_id + '&config_name=' + vm.type);
            Api.get(params.url(), function (data) {
                $timeout(function () {
                    $defer.resolve(data.data);
                }, 500);
            });
        }
    });
}


