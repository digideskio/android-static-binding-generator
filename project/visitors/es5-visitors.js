var es5_visitors = (function () {

	var t = require("babel-types"),

		defaultExtendDecoratorName = "JavaProxy",
		customExtendsArr = [],
		normalExtendsArr = [],
		interfacesArr = [];

	/* 	ENTRY POINT!
	*	Traverses each passed node with several visitors.
	*	Result from visit can be got from static methods.
	*
	*	Input parameters: 
	*		path - node to visit
	*		config - filename, decorator name ...
	*/
	function es5Visitor(path, config) {
		if(!config.filePath) {
			config.filePath = "No file path provided";
		}

		if(path.node.skipMeOnVisit) {
			return;
		}

	// ES5 Syntax
		// anchor is extend (normal extend pattern + custom extend pattern) 
		if (t.isMemberExpression(path) && path.node.property.name === "extend") {
			traverseEs5Extend(path, config);
		}

		//anchor is new keyword (interface pattern)
		if(t.isNewExpression(path)) {
			traverseInterface(path, config);
		}
	// Parsed Typescript to ES5 Syntax (normal extend pattern + custom extend pattern)
		// anchor is __extends
		if(t.isIdentifier(path) && path.node.name === "__extends") {
			traverseTsExtend(path, config);
		}

		// Maybe it's not a good idea to expose this scenario because it can be explicitly covered
		// //anchor is JavaProxy (optional)
		// var customDecoratorName = config.extendDecoratorName === undefined ? defaultExtendDecoratorName : config.extendDecoratorName;
		// if(t.isIdentifier(path) && path.node.name === customDecoratorName) {
			// if(path.node.skipMeOnVisit) {
			// 	return;
			// }
			// console.log("enters because there is a java proxy down the way")
			// traverseJavaProxyExtend(path, config, customDecoratorName);
		// }

	}

	/*
	*	Returns the custom extends array generated from visitor
	*/
	es5Visitor.getProxyExtendInfo = function () {
		return customExtendsArr;
	}

	/*
	*	Returns the common extends array generated from visitor
	*/
	es5Visitor.getCommonExtendInfo = function () {
		return normalExtendsArr;
	}

	/*
	*	Returns the extended interfaces array generated from visitor
	*/
	es5Visitor.getInterfaceInfo = function() {
		return interfacesArr;
	}

	/* 
	*	Traverses the typescript extend case (__extends())
	*	Write results in "normalExtendsArr" or "customExtendsArr".
	*/
	function traverseTsExtend(path, config) {

		//this is the information for a normal extend
		var extendClass = _getArgumentFromNodeAsString(path, 5, config)
		var overriddenMethodNames = _getOverriddenMethodsTypescript(path, 3)

		// todo: check the found names in some list with predefined classes

		// check for _decorate (normal typescript extend + java proxy)
		var isDecorated = traverseToFindDecorate(path, config, extendClass, overriddenMethodNames);
		if(!isDecorated) {
			var lineToWrite = _generateLineToWrite("", extendClass, overriddenMethodNames);
			if(config.logger) {
				config.logger.info(lineToWrite)
			}
			normalExtendsArr.push(lineToWrite);
		}
	}

	/* 
	*	Traverses the passed node to find if there is a __decorate inside it.
	*	If there is __decorate identifier, then the whole node is treated as a custom extend
	*	This node is skipped next time because it's already traversed once here.
	*/
	function traverseToFindDecorate(path, config, extendClass, overriddenMethodNames) {
		var iifeRoot = _getParrent(path, 3)
		var body = iifeRoot.node.body;
		for(var index in body) {
			var ci = body[index];
			if(t.isExpressionStatement(ci) && 
					t.isAssignmentExpression(ci.expression) &&
					ci.expression.right.callee && 
					ci.expression.right.callee.name === "__decorate" &&
					ci.expression.right.arguments &&
					t.isArrayExpression(ci.expression.right.arguments[0])) {

				for(var i in ci.expression.right.arguments[0].elements) {
					var currentDecorator = ci.expression.right.arguments[0].elements[i]

					if(t.isCallExpression) {
						if(currentDecorator.callee.name === config.extendDecoratorName) {
							currentDecorator.callee.skipMeOnVisit = true;
							var customDecoratorName = config.extendDecoratorName === undefined ? defaultExtendDecoratorName : config.extendDecoratorName;
							traverseJavaProxyExtend(currentDecorator._paths[0], config, customDecoratorName,  extendClass, overriddenMethodNames);
							return true;
						}
					}
				}
			}
		}
	}

	/* 
	*	Traverses the node, which is a "new" expression and find if it's a native interface or not.
	*	Write results in "interfacesArr".
	*/
	function traverseInterface(path, config) {
		if(!config.interfaceNames) {
			throw "No interface names are provided! You can pass them in config.interfaceNames as an array!"
		}

		var o = path.node.callee,
			interfaceArr = _getWholeName(o),
			foundInterface = false,
			interfaceNames = config.interfaceNames

		var currentInterface = interfaceArr.reverse().join(".");
		for(var i in interfaceNames) {
			var interfaceName = interfaceNames[i].trim();
			if(interfaceName === currentInterface) {
				currentInterface = interfaceName;
				foundInterface = true;
				break;
			}
		}

		if(foundInterface) {
			var arg0 = path.node.arguments[0];
			var overriddenInterfaceMethods = _getOverriddenMethods(arg0, config);
			var lineToWrite = _generateLineToWrite("", currentInterface, overriddenInterfaceMethods.join(","));
			if(config.logger) {
				config.logger.info(lineToWrite)
			}
			interfacesArr.push(lineToWrite)
		}
	}

	/* 
	*	Finds the java proxy name from custom class decorator.
	*	Write results in "customExtendsArr"
	*/
	function traverseJavaProxyExtend(path, config, customDecoratorName, extendClass, overriddenMethodNames) {
		if(config.logger) {
			config.logger.info("\t+in "+customDecoratorName+" anchor");
		}

		var classNameFromDecorator = _getDecoratorArgument(path, config, customDecoratorName);

		var lineToWrite = _generateLineToWrite(classNameFromDecorator, extendClass, overriddenMethodNames);
		if(config.logger) {
			config.logger.info(lineToWrite)
		}
		customExtendsArr.push(lineToWrite)
	}

	/* 
	*	Finds the normal extend name, overridden methods and possibly java proxy name from passed node.
	*	Writes to "customExtendsArr" or "normalExtendsArr".
	*	Left whole for readability.
	*/
	function traverseEs5Extend(path, config) {
		var overriddenMethodNames = [],
			extendClass = [],
			className = "No decorator name found";

		var callee = path.parent.callee;
		// if(!callee) {
		// 	throw {
		// 		message: "You need to call 'extend'. Example: '...extend(\"a.b.C\", {...overrides...})'), file: " + config.filePath,
		// 		errCode: 1
		// 	}
		// }

		if(callee) {
			var o = callee.object
			extendClass = _getWholeName(o);

			var arg0 = path.parent.arguments[0];
			if (t.isStringLiteral(arg0)) {
				className = arg0.value;
			}
			else {
				throw {
					message: "The 'extend' you are trying to make needs to have a string as a first parameter. Example: '...extend(\"a.b.C\", {...overrides...})', file: " + config.filePath,
					errCode: 1
				}
			}

			var isCorrectExtendClassName = _testJavaProxyName(className);
			var isCorrectClassname = _testClassName(className)
			console.log("---isCorrectExtendClassName=" + isCorrectExtendClassName)
			console.log("---isCorrectClassname=" + isCorrectClassname)

			if(!isCorrectExtendClassName && !isCorrectClassname) {
				throw {
					message: "The 'extend' you are trying to make has an invalid name, file: " + config.filePath,
					errCode: 1
				}
			}
			//if we don't throw this exception multiple extends will be allowed in one file (think if this is necessary)
			// if(!isCorrectExtendClassName) {
			// 	throw {
			// 		message: "The first argument '" + className + "' of the 'extend' function is not following the right pattern which is: 'namespace.[(namespace.)]ClassName'. Example: '...extend(\"a.b.ClassName\", {overrides...})', file: " + config.filePath,
			// 		errCode: 1
			// 	}
			// }

			var arg1 = path.parent.arguments[1];
			overriddenMethodNames = _getOverriddenMethods(arg1, config);

			var lineToWrite = _generateLineToWrite(isCorrectExtendClassName ? className : "", extendClass.reverse().join("."), overriddenMethodNames);
			if(isCorrectExtendClassName) {
				if(config.logger) {
					config.logger.info(lineToWrite)
				}
				
				customExtendsArr.push(lineToWrite)
			}
			if(isCorrectClassname) {
				if(config.logger) {
					config.logger.info(lineToWrite)
				}
				
				normalExtendsArr.push(lineToWrite)
			}
		}
	}

/* 
*	HELPER METHODS
*/
	function _getOverriddenMethods(node, config) {
		var overriddenMethodNames = [];
		if(t.isObjectExpression(node)) {
			var objectProperties = node.properties;
			for(var index in objectProperties) {
				overriddenMethodNames.push(objectProperties[index].key.name)
			}
		}

		return overriddenMethodNames;
	}

	function _getWholeName(node) {
		var arr = [],
			isAndroidInterface = false;

		while (node !== undefined) {
			if (!t.isMemberExpression(node)) {					
				if(isAndroidInterface) {
					arr.push(node.name)
				}
				break;
			}

			isAndroidInterface = true;
			arr.push(node.property.name)
			node = node.object
		}

		return arr;
	}

	function _getArgumentFromNodeAsString(path, count, config) {

		var extClassArr = [];
		var extendedClass =  _getParrent(path, count, config);

		if(extendedClass) {
			if(t.isCallExpression(extendedClass.node)) {
				var o = extendedClass.node.arguments[0];	
			}
			else {
				throw {
					message: "Node type is not a call expression. File" + config.filePath,
					errCode: 1
				}
			}
		}

		extClassArr = _getWholeName(o);

		return extClassArr.reverse().join(".");
	}

	function _getDecoratorArgument(path, config, customDecoratorName) {
		if(path.parent && t.isCallExpression(path.parent)) {

			if(path.parent.arguments && path.parent.arguments.length > 0) {

				var classNameFromDecorator = path.parent.arguments[0].value
				var isCorrectExtendClassName = _testJavaProxyName(classNameFromDecorator);
				if(isCorrectExtendClassName) {
					return path.parent.arguments[0].value;
				}
				else {
					throw {
						message: "The first argument '" + classNameFromDecorator + "' of the "+customDecoratorName+" decorator is not following the right pattern which is: '[namespace.]ClassName'. Example: '"+customDecoratorName+"(\"a.b.ClassName\", {overrides...})', file: " + config.filePath,
						errCode: 1
					}
				}
			}
			else {
				throw {
					message: "No arguments passed to "+customDecoratorName+" decorator. Example: '"+customDecoratorName+"(\"a.b.ClassName\", {overrides...})', file: " + config.filePath,
					errCode: 1
				}
			}
		}
		else {
			throw { 
				message: "Decorator "+customDecoratorName+" must be called with parameters: Example: '"+customDecoratorName+"(\"a.b.ClassName\", {overrides...})', file: " + config.filePath,
				errCode: 1
			}
		}
		return undefined;
	}

	function _getOverriddenMethodsTypescript(path, count) {
		var methods = [];

		var cn = _getParrent(path, count)

		// this pattern follows typescript generated syntax
		for(var item in cn.node.body) {
			var ci = cn.node.body[item];
			if(t.isExpressionStatement(ci)) {
				if(t.isAssignmentExpression(ci.expression)) {
					if(ci.expression.left.property) {
						methods.push(ci.expression.left.property.name)
					}
				}
			}
		}

		return methods;
	}

	function _getParrent(node, numberOfParrents, config) {
		if(!node) {
			throw {
				message: "No parent found for node in file: " + config.filePath,
				errCode: 1
			}
		}
		if(numberOfParrents === 0) {
			return node;
		}

		return _getParrent(node.parentPath, --numberOfParrents)
	}

	function _testJavaProxyName(name) {
		return /^((\w+\.)+\w+)$/.test(name)
	}

	function _testClassName(name) {
		return /^(\w+)$/.test(name)
	}

	function _generateLineToWrite(classNameFromDecorator, extendClass, overriddenMethodNames) {
		var lineToWrite = "EXTEND_CLASS: " + extendClass + " - OVERRIDDEN_METHODS: " + overriddenMethodNames + " - JAVA_FILE: " + classNameFromDecorator;
		return lineToWrite;
	}

	return {
		es5Visitor: es5Visitor
	}
})();

module.exports = es5_visitors;