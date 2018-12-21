App.filter('moment_yyyymmddhhmmss', function () {
  return function (input, momentFn /*, param1, param2, ...param n */) {
    var args = Array.prototype.slice.call(arguments, 2),
      momentObj = moment(input, "YYYYMMDDHHmmss");
    return momentObj[momentFn].apply(momentObj, args);
  };
});