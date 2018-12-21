//由于使用了bower，有很多非必须资源。通过set project.files对象指定需要编译的文件夹和引用的资源
fis.set('project.files', ['app/**', 'vendor/**', 'index.html', 'map.json', 'server/*.json']);

//FIS modjs模块化方案，您也可以选择amd/commonjs等
//fis.hook('module', {
//    mode: 'mod'
//});

///*************************目录规范*****************************/

//modules下面都是模块化资源
//fis.match('modules/(**).js',{
//    isMod: true,
//    id: '$1', //id支持简写，去掉modules和.js后缀中间的部分
//})

///****************异构语言编译*****************/
//less的编译
//npm install [-g] fis-parser-less
fis.match('**/(bootstrap).scss', {
  rExt: '.css', // from .scss to .css
  parser: fis.plugin('sass', {
    //fis-parser-sass option
  }),
  release: '/app/css/$1'
});

fis.match('**/(app).scss', {
  rExt: '.css', // from .scss to .css
  parser: fis.plugin('sass', {
    //fis-parser-sass option
  }),
  release: '/app/css/$1'
});

fis.match('**/themes/(*).scss', {
  rExt: '.css', // from .scss to .css
  parser: fis.plugin('sass', {
    //fis-parser-sass option
  }),
  release: '/app/css/$1'
});

//
//打包与css sprite基础配置
fis.match('::packager', {
  // npm install [-g] fis3-postpackager-loader
  // 分析 __RESOURCE_MAP__ 结构，来解决资源加载问题
  postpackager: fis.plugin('loader', {
    resourceType: 'mod',
    useInlineMap: true // 资源映射表内嵌
  }),
  packager: fis.plugin('map'),
  spriter: fis.plugin('csssprites', {
    layout: 'matrix',
    margin: '15'
  })
});

fis.media('dev')
  .match('*.js', {
    preprocessor: [
      fis.plugin('replace', {
        from: /#CAS_HOST#|#BOLE_API_HOST#/g,
        to: function(m){
          if(m === '#BOLE_API_HOST#') return 'http://t.internal.api.lesports.com';
          if(m === '#CAS_HOST#') return '10.154.157.33';
        }
      })
    ]
  });

///**********************生产环境下CSS、JS压缩合并*****************/
//使用方法 fis3 release prod
fis.media('prod')
  //注意压缩时.async.js文件是异步加载的，不能直接用annotate解析
  .match('**!(.async).js', {
    preprocessor: fis.plugin('annotate'),
    optimizer: fis.plugin('uglify-js')
  })
  .match('bower_components/ng-tags-input/ng-tags-input.min.css', {
    packTo: '/app/pkg/css/vendor.css'
  })
  .match('bower_components/ng-table/dist/ng-table.min.css', {
    packTo: '/app/pkg/css/vendor.css'
  })
  .match('**.css', {
    optimizer: fis.plugin('clean-css')
  })
  // 无法打包新加的sccs?
  // .match('app/**.css', {
  //   useHash: true
  // })
  .match('bower_components/jquery/dist/jquery.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular/angular.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-route/angular-route.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-cookies/angular-cookies.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-animate/angular-animate.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-ui-router/release/angular-ui-router.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/ngstorage/ngStorage.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-ui-utils/ui-utils.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-sanitize/angular-sanitize.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-resource/angular-resource.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-translate/angular-translate.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-translate-loader-url/angular-translate-loader-url.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-translate-loader-static-files/angular-translate-loader-static-files.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-translate-storage-local/angular-translate-storage-local.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-translate-storage-cookie/angular-translate-storage-cookie.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/oclazyload/dist/ocLazyLoad.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-bootstrap/ui-bootstrap-tpls.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/angular-loading-bar/build/loading-bar.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/moment/min/moment-with-locales.min.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/ng-tags-input/ng-tags-input.min.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/ng-table/dist/ng-table.min.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('bower_components/ng-table-export/ng-table-export.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('vendor/fis3/mod.js', {
    packTo: '/app/pkg/js/vendor.js'
  })
  .match('app/js/**.js', {
    packTo: '/app/pkg/js/app.js'
  })
  .match('app/**.js', {
    //optimizer: fis.plugin('uglify-js', {
    //  compress: {
    //    drop_console: true
    //  }
    //}),
    useHash: true
  })
  .match('*.js', {
    preprocessor: [
      fis.plugin('replace', {
        from: /#CAS_HOST#|#BOLE_API_HOST#/g,
        to: function(m){
          if(m === '#BOLE_API_HOST#') return 'http://internal.api.lesports.com';
          if(m === '#CAS_HOST#') return '10.154.28.250';
        }
      })
    ]
  });

