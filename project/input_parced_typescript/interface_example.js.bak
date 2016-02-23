var appModule = require("./application-common");
var dts = require("application");
var frame = require("ui/frame");
var types = require("utils/types");
var observable = require("data/observable");
var enums = require("ui/enums");
var fileResolverModule = require("file-system/file-name-resolver");
global.moduleMerge(appModule, exports);

app.init({
    getActivity: function (activity) {
        var intent = activity.getIntent();
        return exports.android.getActivity(intent);
    },
    onCreate: function () {
        exports.android.init(this);
    }
});
var AndroidApplication = (function (_super) {
    __extends(AndroidApplication, _super);
    function AndroidApplication() {
        _super.apply(this, arguments);
        this._registeredReceivers = {};
        this._pendingReceiverRegistrations = new Array();
    }
    AndroidApplication.prototype.getActivity = function (intent) {
        if (intent && intent.getAction() === android.content.Intent.ACTION_MAIN) {
            if (exports.onLaunch) {
                exports.onLaunch(intent);
            }
            exports.notify({ eventName: dts.launchEvent, object: this, android: intent });
            setupOrientationListener(this);
        }
        var topFrame = frame.topmost();
        if (!topFrame) {
            var navParam = dts.mainEntry;
            if (!navParam) {
                navParam = dts.mainModule;
            }
            if (navParam) {
                topFrame = new frame.Frame();
                topFrame.navigate(navParam);
            }
            else {
                throw new Error("A Frame must be used to navigate to a Page.");
            }
        }
        return topFrame.android.onActivityRequested(intent);
    };
    AndroidApplication.prototype.init = function (nativeApp) {
        this.nativeApp = nativeApp;
        this.packageName = nativeApp.getPackageName();
        this.context = nativeApp.getApplicationContext();
        this._eventsToken = initEvents();
        this.nativeApp.registerActivityLifecycleCallbacks(this._eventsToken);
        this._registerPendingReceivers();
    };
    AndroidApplication.prototype._registerPendingReceivers = function () {
        if (this._pendingReceiverRegistrations) {
            var i = 0;
            var length = this._pendingReceiverRegistrations.length;
            for (; i < length; i++) {
                var registerFunc = this._pendingReceiverRegistrations[i];
                registerFunc(this.context);
            }
            this._pendingReceiverRegistrations = new Array();
        }
    };
    AndroidApplication.prototype.registerBroadcastReceiver = function (intentFilter, onReceiveCallback) {
        var that = this;
        var registerFunc = function (context) {
            var receiver = new BroadcastReceiver(onReceiveCallback);
            context.registerReceiver(receiver, new android.content.IntentFilter(intentFilter));
            that._registeredReceivers[intentFilter] = receiver;
        };
        if (this.context) {
            registerFunc(this.context);
        }
        else {
            this._pendingReceiverRegistrations.push(registerFunc);
        }
    };
    AndroidApplication.prototype.unregisterBroadcastReceiver = function (intentFilter) {
        var receiver = this._registeredReceivers[intentFilter];
        if (receiver) {
            this.context.unregisterReceiver(receiver);
            this._registeredReceivers[intentFilter] = undefined;
            delete this._registeredReceivers[intentFilter];
        }
    };
    AndroidApplication.activityCreatedEvent = "activityCreated";
    AndroidApplication.activityDestroyedEvent = "activityDestroyed";
    AndroidApplication.activityStartedEvent = "activityStarted";
    AndroidApplication.activityPausedEvent = "activityPaused";
    AndroidApplication.activityResumedEvent = "activityResumed";
    AndroidApplication.activityStoppedEvent = "activityStopped";
    AndroidApplication.saveActivityStateEvent = "saveActivityState";
    AndroidApplication.activityResultEvent = "activityResult";
    AndroidApplication.activityBackPressedEvent = "activityBackPressed";
    return AndroidApplication;
})(observable.Observable);
exports.AndroidApplication = AndroidApplication;
var BroadcastReceiver = (function (_super) {
    __extends(BroadcastReceiver, _super);
    function BroadcastReceiver(onReceiveCallback) {
        _super.call(this);
        this._onReceiveCallback = onReceiveCallback;
        return global.__native(this);
    }
    BroadcastReceiver.prototype.onReceive = function (context, intent) {
        if (this._onReceiveCallback) {
            this._onReceiveCallback(context, intent);
        }
    };
    return BroadcastReceiver;
})(android.content.BroadcastReceiver);
global.__onUncaughtError = function (error) {
    if (types.isFunction(exports.onUncaughtError)) {
        exports.onUncaughtError(error);
    }
    exports.notify({ eventName: dts.uncaughtErrorEvent, object: appModule.android, android: error });
};
exports.start = function () {
    dts.loadCss();
};
exports.android = new AndroidApplication();
var currentOrientation;
function setupOrientationListener(androidApp) {
    androidApp.registerBroadcastReceiver(android.content.Intent.ACTION_CONFIGURATION_CHANGED, onConfigurationChanged);
    currentOrientation = androidApp.context.getResources().getConfiguration().orientation;
}
function onConfigurationChanged(context, intent) {
    var orientation = context.getResources().getConfiguration().orientation;
    if (currentOrientation !== orientation) {
        currentOrientation = orientation;
        var newValue;
        switch (orientation) {
            case android.content.res.Configuration.ORIENTATION_LANDSCAPE:
                newValue = enums.DeviceOrientation.landscape;
                break;
            case android.content.res.Configuration.ORIENTATION_PORTRAIT:
                newValue = enums.DeviceOrientation.portrait;
                break;
            default:
                newValue = enums.DeviceOrientation.unknown;
                break;
        }
        exports.notify({
            eventName: dts.orientationChangedEvent,
            android: context,
            newValue: newValue,
            object: exports.android,
        });
    }
}
global.__onLiveSync = function () {
    if (exports.android && exports.android.paused) {
        return;
    }
    fileResolverModule.clearCache();
    appModule.loadCss();
    frame.reloadPage();
};

//////////// extended interface
var initEvents = function () {
    var androidApp = exports.android;
    var lifecycleCallbacks = new android.app.Application.ActivityLifecycleCallbacks({
        onActivityCreated: function (activity, bundle) {},
        onActivityDestroyed: function (activity) {},
        onActivityPaused: function (activity) {},
        onActivityResumed: function (activity) {},
        onActivitySaveInstanceState: function (activity, bundle) {},
        onActivityStarted: function (activity) {},
        onActivityStopped: function (activity) {}
    });
    return lifecycleCallbacks;
};


////////// ts parsed //////////
	//java proxy with extends
var TestClass = (function (_super) {
    __extends(TestClass, _super);
    function TestClass() {
        _super.apply(this, arguments);
    }
    TestClass.prototype.TestClassMethod1 = function () {
    };
    TestClass.prototype.TestClassMethod2 = function () {
    };
    TestClass = __decorate([
        JavaProxy("javaProxy.path.Present")
    ], TestClass);
    return TestClass;
})(passed.extend.Present);

	//java proxy no extend (we wont support this scenario)
    // if you want to have java proxy you will have to extend a speciffic class
var TestClass = (function () {
    function TestClass() {
    }
    TestClass.prototype.TestClassMethod1 = function () {
    };
    TestClass.prototype.TestClassMethod2 = function () {
    };
    TestClass = __decorate([
        JavaProxy("javaProxy.path.Present")
    ], TestClass);
    return TestClass;
})();

	//no java proxy with extends
var TestClass = (function (_super) {
    __extends(ZZZZZ, _super);
    function TestClass() {
        _super.apply(this, arguments);
    }
    TestClass.prototype.TestClassMethod1 = function () {
    };
    TestClass.prototype.TestClassMethod2 = function () {
    };
    return TestClass;
})(passed.extend.Present);

	//no java proxy no extends
var SSSSSS = (function () {
    function SSSSSS() {
    }
    SSSSSS.prototype.z = function () {
        console.log("smth");
    };
    SSSSSS.prototype.SSSSSSSSSS111111111 = function () {
        console.log("smth");
    };
    return SSSSSS;
})();


///////// es5 //////////
	//custom java proxy extends
(function () {
    var MyButton = android.widget.Button.extend("my.custom.TestClass", {
        onClick1: function () {
        },
        onClick2: function () {
        }
    });
})();

var MyButton = android.widget.Button1111.extend("my.custom.ClassFromSubFolder", {
    onClick111111111: function () {
        console.log('click happened');
    },
    onClick111111111111111: function () {
        console.log('click happened');
    }
});

	// common extend
var MyButton = android.widget.Button333.extend("TestClass333", {
    onClick1333: function () {
    },
    onClick2333: function () {
    }
})

