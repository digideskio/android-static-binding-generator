module.exports = function(grunt) {
	
	var config = {
		rootDir: ".",
		interfaceGenPath: 'interface-name-generator/interface_name_grator.jar',
		jarsDir: grunt.option('jarsDir') || './../jars',
		interfaceNamesFile: 'interface-names.txt',
		outputFilePath: 'interface-name-generator/out/',
		astParserDir: 'ast-parser',
		bindingsFileName: 'bindings.txt',
		outDir: 'dist',
		relativeOutDir: grunt.option('outDir') || './../../../dist',
		userCode: 'JSCODE',
		relativeUserCode: grunt.option('inputDir') || './../jscode'
	}
	
	grunt.initConfig({
		exec: {
			generateInterfaceNameList: {
				cmd: 'java -jar ' + config.interfaceGenPath + ' ' + config.jarsDir + ' ' + config.outputFilePath + config.interfaceNamesFile,
				cwd: '.'
			},
			npmInstallInParseProject: {
				cmd: 'npm install',
				cwd: './ast-parser'
			},
			extractExtendNames: {
				cmd: 'node traverse_files.js ' +  './../' + config.outputFilePath + config.interfaceNamesFile + ' ' + config.relativeUserCode + ' ./' + config.bindingsFileName,
				cwd: './ast-parser'
			},
			runBindingGenerator: {
				cmd: 'java -jar static_bindings_generator.jar ' + config.jarsDir + ' ' + './../../../' + config.astParserDir + '/' + config.bindingsFileName + ' ' + config.relativeOutDir,
				cwd: './binding-generator/build/libs'
			}
		},
		clean: {
			outDir: {
				src: config.outDir + '/*'
			},
			jsCodeDir: {
				src: config.userCode + '/*'
			}
		},
		mkdir: {
			outDir: {
				options: {
					create: [config.outDir]
				}
			},
			jsCodeDir: {
				options: {
					create: [config.userCode]
				}
			}
		}
	});
	
	grunt.registerTask('default', [
						'clean:outDir',
						'mkdir:outDir',
						'exec:generateInterfaceNameList',
						'exec:npmInstallInParseProject',
						'exec:extractExtendNames',
						'exec:runBindingGenerator'
					]);
	
	//load modules
	grunt.loadNpmTasks('grunt-exec');
	grunt.loadNpmTasks('grunt-contrib-clean');
	grunt.loadNpmTasks('grunt-mkdir');
}
//generates interfaces
// java -jar interface_generator/interface_name_grator.jar ./../jars interface-names.txt

//call parser
// node traverse_files.js ./../_interface_name_generator/out/interface-names.txt ./../JSCODE ./out.txt

//generate bindings
// java -jar extend_classes_grator.jar ./../JARS ./../_ast_parser/out.txt ./out