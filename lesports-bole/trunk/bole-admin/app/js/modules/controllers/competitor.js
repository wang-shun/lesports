/**=========================================================
 * Module: NGTableCtrl.js
 * Controller for ngTables
 =========================================================*/

App.controller('CompetitorCtrl', CompetitorCtrl);

function CompetitorCtrl($scope, $filter, ngTableParams, $resource, $timeout, BOLE_API_HOST, BOLE_STATUS_LIST) {
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

    vm.view = function(competitorId){
        console.log("competitorId : " + competitorId)
        window.open("http://" + window.location.host + "/#/app/competitor/view/" + competitorId);
    }

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
        count: 10           // count per page
    }, {
        total: 0,           // length of data
        counts: [],         // hide page counts control
        getData: function ($defer, params) {
            var q = {"q": vm.search_q,"status":vm.search_status};
            var Api = $resource(BOLE_API_HOST + '/search/v1/s/bole/competitors/?caller=1001', q);
            Api.get(params.url(), function (data) {
                $timeout(function () {
                    // update table params
                    params.total(data.data.total);
                    // set new data
                    $defer.resolve(data.data.rows);
                }, 500);
            });
        }
    });

}
