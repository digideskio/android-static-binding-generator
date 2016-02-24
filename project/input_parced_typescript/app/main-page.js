var vmModule = require("./main-view-model");
var app = require("application")
var context = app.android.context;
function pageLoaded(args) {
    var page = args.object;
    page.bindingContext = vmModule.mainViewModel;
}
exports.pageLoaded = pageLoaded;
