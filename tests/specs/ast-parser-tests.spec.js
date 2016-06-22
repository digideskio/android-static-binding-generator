var exec = require("child_process").exec,
    path = require("path"),
    fs = require("fs"),
    prefix = path.resolve(__dirname, "../cases/"),
    interfaceNames = path.resolve(__dirname, "../../project/interface-name-generator/interfaces-names.txt"),
    parser = path.resolve(__dirname, "../../project/ast-parser/js_parser.js");

describe("ast-parser/js_parser tests", function () {
    describe("js_parser tests", function () {
        it("Analyse files only in the correct folder structure", function (done) {
            var input = prefix + "/mini_app",
                output = prefix + "/mini_app/bindings.txt";

            exec("node " + parser + " " + input + " " + output + " " + interfaceNames, function (err) {
                var bindingsContent = fs.readFileSync(output, "utf-8").toString().trim();

                var expectedExtends = [
                    "a.B.C.ButtonThatWillAlwaysBeExtended",
                    "a.B.C.ButtonThatIsExtendedComponent",
                    "a.B.C.ButtonThatIsExtendedComponent.Subcomponent",
                    "a.B.C.ButtonThatIsExtendedComponent1",
                    "a.B.C.ButtonThatIsExtendedComponent2",
                    "a.B.C.ButtonThatIsExtendedComponent2.Subcomponent21",
                    
                ];
                var notExpectedExtends = [
                    "a.B.C.ButtonThatShouldNotBeExtended",
                    "a.B.C.ButtonThatShouldNotBeExtended.Submodule",
                    "a.B.C.ButtonThatShouldNotBeExtendedComponent.Subcomponent"
                ];    

                for(var entry in expectedExtends) {
                    var expectedValue = expectedExtends[entry] + "*";
                    expect(bindingsContent).toContain(expectedValue);
                }

                for(var entry in notExpectedExtends) {
                    var notExpectedValue = notExpectedExtends[entry] + "*";
                    expect(bindingsContent).toNotContain(notExpectedExtends);
                }

                done();
            });
        })
    });

    describe("es5-visitors tests", function () {
        it("Generated metadata for bindings should match the pattern JavaClass*extendPrefixLoc*methods*NewClass*Location ---> /([a-zA-Z_$][a-zA-Z\d_$]*\.)*[a-zA-Z_$][a-zA-Z\d_$]*\*([\_A-z0-9]*)\*([\_A-z][A-z0-9]*)*(\,[\_A-z][A-z0-9]*)*\*(([a-zA-Z_$][a-zA-Z\d_$]*\.)*[a-zA-Z_$][a-zA-Z\d_$]*)*\*((?:[a-zA-Z]\:){0,1}(?:[\\/]*[\w.]+){1,})*/", function (done) {
            var pattern = /(([a-zA-Z_$][a-zA-Z\d_$]*\.)*[a-zA-Z_$][a-zA-Z\d_$]*\*([\_A-z0-9]*)\*([\_A-z][A-z0-9]*)*(\,[\_A-z][A-z0-9]*)*\*(([a-zA-Z_$][a-zA-Z\d_$]*\.)*[a-zA-Z_$][a-zA-Z\d_$]*)*\*((?:[a-zA-Z]\:){0,1}(?:[\\/]*[\w.]+){1,})*)+/mi;

            var input = prefix + "/normal_ns_extends",
                output = prefix + "/normal_ns_extends/bindings.txt";

            exec("node " + parser + " " + input + " " + output + " " + interfaceNames, function (err) {
                var bindingsContent = fs.readFileSync(output, "utf-8").toString().trim().split('\n');

                var allLines = [];

                for(var line in bindingsContent) {
                    var res = pattern.test(bindingsContent[line].trim());
                    allLines.push(res);
                }

                allLines = allLines.filter(function(val) {
                    if(val) {
                        return false;
                    }  else {
                        return true;
                    }
                });

                expect(allLines.length).toBe(0);
                done();
            });
        });
    });
});