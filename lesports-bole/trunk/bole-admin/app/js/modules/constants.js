/**=========================================================
 * Module: constants.js
 * Define constants to inject across the application
 =========================================================*/
App
  .constant('APP_COLORS', {
    'primary':                '#5d9cec',
    'success':                '#27c24c',
    'info':                   '#23b7e5',
    'warning':                '#ff902b',
    'danger':                 '#f05050',
    'inverse':                '#131e26',
    'green':                  '#37bc9b',
    'pink':                   '#f532e5',
    'purple':                 '#7266ba',
    'dark':                   '#3a3f51',
    'yellow':                 '#fad732',
    'gray-darker':            '#232735',
    'gray-dark':              '#3a3f51',
    'gray':                   '#dde6e9',
    'gray-light':             '#e4eaec',
    'gray-lighter':           '#edf1f2'
  })
  .constant('APP_MEDIAQUERY', {
    'desktopLG':             1200,
    'desktop':                992,
    'tablet':                 768,
    'mobile':                 480
  })
  .constant('BOLE_API_HOST', "#BOLE_API_HOST#")
  .constant('CAS_HOST', "10.154.157.33")
  .constant('BOLE_AUTHENTICATION', "bole_authentication")
  .constant('BOLE_STATUS_LIST', [
    {"id":1,"name":"新建"},
    {"id":0,"name":"导入"},
    {"id":2,"name":"确认"},
    {"id":3,"name":"无效"},
    {"id":4,"name":"导出"},
    {"id":5,"name":"已关联"}
    ])
    .constant('BOLE_CONFIG_TYPE_LIST', [
        {"type":"match_config","name":"比赛配置"},
        {"type":"team_result_config","name":"成绩配置"},
        {"type":"team_config","name":"运动队配置"},
        {"type":"player_config","name":"运动员配置"},
        {"type":"team_stats_config","name":"运动队技术统计"},
        {"type":"player_stats_config","name":"运动员技术统计"}
    ])
    .constant('BOLE_FORMATTER_TYPE_LIST', [
        {"type":"sectionFormatter","name":"节次字典ID"},
        {"type":"PercentFormatter","name":"百分数"},
        {"type":"BooleanFormatter","name":"布尔值"},
        {"type":"HomeAwayFormatter","name":"主客队枚举"},
        {"type":"ListFormatter","name":"分节列表"}
    ])
    .constant('BOLE__CID_DATA_LIST', [
        { "type": "100060001", "name":"欧洲超级杯"},
        { "type": "101121001","name":"英格兰社区盾"},
        { "type": "72001","name":"中甲联赛"}
    ])
    .constant('BOLE_DATA_IMPORT_FILE_TYPE_LIST', [
        { "type": "SCHEDULE_BK", "name":"scheduleandresult_Basketball"},
        { "type": "SCHEDULE_FB", "name":"scheduleandresult_Soccer"},
        { "type": "SCHEDULE_BB", "name":"scheduleandresult_baseball"},
        { "type": "TOPLIST_BK", "name":"leaguetable_Basketball"},
        { "type": "TOPLIST_FB", "name":"leaguetable_Soccer"},
        { "type": "TOPLIST_BB", "name":"leaguetable_baseball"},
        { "type": "GOAlSCORE", "name":"goalscore_soccer"}
    ])
    .constant('BOLE__FILE_DATA_LIST', [
        { "type": "schedulesandresult_Soccer.England.CommunityShield.2016.CommunityShield2016.9717703.xml", "name":"schedulesandresult_Soccer.England.CommunityShield.2016.CommunityShield2016.9717703.xml"},
        { "type": "schedulesandresult_Soccer.InternationalClubs.UEFASuperCup.2016.UEFASuperCup2016.9535581","name":"schedulesandresult_Soccer.InternationalClubs.UEFASuperCup.2016.UEFASuperCup2016.9535581"},
        { "type": "leaguetable_Soccer.China.ChinaLeague.ChinaLeague2016.xml","name":"leaguetable_Soccer.China.ChinaLeague.ChinaLeague2016.xml"}
    ])
    .constant('BOLE_DATA_PARTNER_TYPE', [
      { "type": "469", "name":"Sportrard"},
      {"type": "499",  "name":"stats"}
    ])
    .constant('SOURCE', {
      "ZHIBO8": 1,
      "QQ": 2,
      "SINA": 3
    })
    .constant('CONTENT', {
      "MATCH": 1,
      "NEWS": 2
    })
    .constant('OUTPUT_OPTION', {
      "ALL": 1,
      "BLACKLIST": 2,
      "WHITELIST": 3,
      "NONE": 4
    })
  .constant('APP_REQUIRES', {
    // jQuery based and standalone scripts
    scripts: {
      'modernizr':          ['vendor/modernizr/modernizr.js'],
      'icons':              ['vendor/fontawesome/css/font-awesome.min.css',
        'vendor/simple-line-icons/css/simple-line-icons.css']
    },
    // Angular based script (use the right module name)
    modules: [
      {name: 'toaster',                   files: ['vendor/angularjs-toaster/toaster.js',
        'vendor/angularjs-toaster/toaster.css']},
      {name: 'localytics.directives',     files: ['vendor/chosen_v1.2.0/chosen.jquery.min.js',
        'vendor/chosen_v1.2.0/chosen.min.css',
        'vendor/angular-chosen-localytics/chosen.js']},
      {name: 'ngDialog',                  files: ['vendor/ngDialog/js/ngDialog.min.js',
        'vendor/ngDialog/css/ngDialog.min.css',
        'vendor/ngDialog/css/ngDialog-theme-default.min.css'] },
      {name: 'ngWig',                     files: ['vendor/ngWig/dist/ng-wig.min.js'] },
      {name: 'ngTable',                   files: ['bower_components/ng-table/dist/ng-table.min.js',
        'bower_components/ng-table/dist/ng-table.min.css']},
      {name: 'ngTableExport',             files: ['bower_components/ng-table-export/ng-table-export.js']},
      {name: 'angularBootstrapNavTree',   files: ['vendor/angular-bootstrap-nav-tree/dist/abn_tree_directive.js',
        'vendor/angular-bootstrap-nav-tree/dist/abn_tree.css']},
      {name: 'htmlSortable',              files: ['vendor/html.sortable/dist/html.sortable.js',
        'vendor/html.sortable/dist/html.sortable.angular.js']},
      {name: 'xeditable',                 files: ['vendor/angular-xeditable/dist/js/xeditable.js',
        'vendor/angular-xeditable/dist/css/xeditable.css']},
      {name: 'angularFileUpload',         files: ['vendor/angular-file-upload/angular-file-upload.js']},
      {name: 'ngImgCrop',                 files: ['vendor/ng-img-crop/compile/unminified/ng-img-crop.js',
        'vendor/ng-img-crop/compile/unminified/ng-img-crop.css']},
      {name: 'ui.select',                 files: ['vendor/angular-ui-select/dist/select.js',
        'vendor/angular-ui-select/dist/select.css']},
      {name: 'ui.codemirror',             files: ['vendor/angular-ui-codemirror/ui-codemirror.js']},
      {name: 'angular-carousel',          files: ['vendor/angular-carousel/dist/angular-carousel.css',
        'vendor/angular-carousel/dist/angular-carousel.js']},
      {name: 'ngGrid',                    files: ['vendor/ng-grid/build/ng-grid.min.js',
        'vendor/ng-grid/ng-grid.css' ]},
      {name: 'infinite-scroll',           files: ['vendor/ngInfiniteScroll/build/ng-infinite-scroll.js']},
      {name: 'ui.bootstrap-slider',       files: ['vendor/seiyria-bootstrap-slider/dist/bootstrap-slider.min.js',
        'vendor/seiyria-bootstrap-slider/dist/css/bootstrap-slider.min.css',
        'vendor/angular-bootstrap-slider/slider.js']},
      {name: 'ui.grid',                   files: ['vendor/angular-ui-grid/ui-grid.min.css',
        'vendor/angular-ui-grid/ui-grid.min.js']},
      {name: 'textAngularSetup',          files: ['vendor/textAngular/src/textAngularSetup.js']},
      {name: 'textAngular',               files: ['vendor/textAngular/dist/textAngular-rangy.min.js',
        'vendor/textAngular/src/textAngular.js',
        'vendor/textAngular/src/textAngularSetup.js',
        'vendor/textAngular/src/textAngular.css'], serie: true},
      {name: 'angular-rickshaw',          files: ['vendor/d3/d3.min.js',
        'vendor/rickshaw/rickshaw.js',
        'vendor/rickshaw/rickshaw.min.css',
        'vendor/angular-rickshaw/rickshaw.js'], serie: true},
      {name: 'angular-chartist',          files: ['vendor/chartist/dist/chartist.min.css',
        'vendor/chartist/dist/chartist.js',
        'vendor/angular-chartist.js/dist/angular-chartist.js'], serie: true},
      {name: 'ui.map',                    files: ['vendor/angular-ui-map/ui-map.js']},
      {name: 'datatables',                files: ['vendor/datatables/media/css/jquery.dataTables.css',
        'vendor/datatables/media/js/jquery.dataTables.js',
        'vendor/angular-datatables/dist/angular-datatables.js'], serie: true},
      {name: 'angular-jqcloud',           files: ['vendor/jqcloud2/dist/jqcloud.css',
        'vendor/jqcloud2/dist/jqcloud.js',
        'vendor/angular-jqcloud/angular-jqcloud.js']},
      {name: 'angularGrid',               files: ['vendor/ag-grid/dist/angular-grid.css',
        'vendor/ag-grid/dist/angular-grid.js',
        'vendor/ag-grid/dist/theme-dark.css',
        'vendor/ag-grid/dist/theme-fresh.css']},
      {name: 'ng-nestable',               files: ['vendor/ng-nestable/src/angular-nestable.js',
        'vendor/nestable/jquery.nestable.js']},
      {name: 'akoenig.deckgrid',          files: ['vendor/angular-deckgrid/angular-deckgrid.js']}
    ]

  })
;