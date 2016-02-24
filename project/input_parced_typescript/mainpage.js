//__disableVerboseLogging();
//__log("starting tests");
//
//// methods that common tests need to run
//var testContent = "";
//__collect = gc;
//TNSClearOutput = function () {
//	testContent = "";
//}
//TNSLog = function (text) {
//	testContent += text;
//}
//TNSGetOutput = function () {
//	return testContent;
//}
//__approot = __dirname.substr(0, __dirname.length - 4);
//
//require("./shared");
//
//require("./tests/testMetadata");
//require("./tests/testAsserts");
//require("./tests/testWeakRef"); 
//require("./tests/tests");
//require("./tests/testMethodResolution");
//require("./tests/testArrays");
//require("./tests/testsForRuntimeBindingGenerator");
//require("./tests/testPrimitiveTypeConversion");
//require("./tests/numericConversionTests"); 
//require("./tests/inheritanceChainResolutionTest"); 
//require("./tests/exceptionHandlingTests");
//require("./tests/dispatchCallbacksOnUiThreadTests");
//require("./tests/stringConversionTests");
//require("./tests/testsForTypescript"); 
//require("./tests/testGC");
//require("./tests/testsMemoryManagement");
//require("./tests/testFieldGetSet");
//require("./tests/extendedClassesTests");
//require("./tests/extendClassNameTests");
//require("./tests/testJniReferenceLeak");
//require("./tests/testNativeModules");
//require("./tests/requireExceptionTests");
//require("./tests/java-array-test");
//require("./tests/field-access-test");

var MainActivity = {
    onCreate: function (bundle) {
    	__log("-----------> onCreate from js");
    	var k = this.toString();
    	__log("this.toString " + k);
    	this.super.onCreate(bundle);

    	require("./tests/testsWithContext").run(this);  	
    	execute(); //run jasmine
    	
    	var layout = new android.widget.LinearLayout(this);
    	layout.setOrientation(1);
    	this.setContentView(layout);
    	
		
		(function (_super) {
			__extends(MyClass, _super);
			
			function MyClass() {
				
			}
			
			MyClass.prototype.method123 = function () {
				
			}
			MyClass.prototype.method234 = function () {
				
			}
			
			MyClass = __decorate([
				JavaProxy("tralsad.asd.aASD.asASD")
			], MyClass);
		})(android.a.B);
		
		(function (_super) {
			__extends(MyClass, _super);
			
			function MyClass() {
				
			}
			
			MyClass.prototype.method123 = function () {
				
			}
			MyClass.prototype.method234 = function () {
				
			}
		})(android.a.B);
		
    	var textView = new android.widget.TextView(this);
    	textView.setText("Hit that sucker");
    	layout.addView(textView);
    	
    	var B = android.widget.Button.extend("asd",{
    		someMethod1: function(){},
    		someMethod2: function(){}
    	})
    	
		var B = android.widget.Button.extend("asd.ASD", {
    		someMethod1: function(){},
    		someMethod2: function(){}
    	})
		
		
    	var a = new B(this);
    	
    	var button = new android.widget.Button(this);
    	button.setText("Hit me");
    	layout.addView(button);
    	var counter = 0;
    	button.setOnClickListener(new android.view.View.OnClickListener("AppClickListener", {
    		onClick:  function() {
    			__log("onClick called");  
    			button.setText("Hit that sucker one more time " + ++counter);
    		}}));
			
			    	button.setOnClickListener(new android.view.View.OnClickListener({
    		onClick:  function() {
    			__log("onClick called");  
    			button.setText("Hit that sucker one more time " + ++counter);
    		}}));
    }
}; 

app.init({
	
	getActivity: function(activity) {
		var intent = activity.getIntent();
		__log("intent=" + intent)
		var action = intent.getAction();
		__log("action=" + action)
		return MainActivity;
	},
	
	onCreate: function() {
		__log("Application on create called");
	} 
});