package org.nativescript.staticbindings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

public class Generator {
	private static final String JAVA_EXT = ".java";
	
	private static final String CLASS_EXT = ".class";
	
	private static final String DEFAULT_PACKAGE_NAME = "com.tns.gen";
	
	public static void main(String[] args) {
		try {
			new Generator().go(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go(String[] args) throws IOException {
		if (args.length < 3) {
			throw new IllegalArgumentException("Expects at least three arguments");
		}
		String filename = args[0];
		String outputDir = args[1];
		String[] libs = Arrays.copyOfRange(args, 2, args.length);
		
		List<DataRow> rows = getRows(filename);
		
		Map<String, JavaClass> classes = readClasses(libs);
		
		processRows(rows, outputDir, classes);
	}
	
	private List<DataRow> getRows(String filename) throws IOException {
		List<DataRow> rows = new ArrayList<DataRow>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line;
			while ((line = br.readLine()) != null) {
				DataRow row = new DataRow(line);
				rows.add(row);
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return rows;
	}
	
	private void processRows(List<DataRow> rows, String outputDir, Map<String, JavaClass> classes) throws IOException {
		for (DataRow r: rows) {
			String classname = r.getBaseClassname();
			boolean isJavaExtend = classes.containsKey(classname);
			if (isJavaExtend) {
				JavaClass clazz = classes.get(classname);

				Map<String, List<Method>> api = getPublicApi(clazz, classes);

				boolean hasSpecifiedName = !r.getFilename().isEmpty();
				String packageName = hasSpecifiedName ? getBaseDir(r.getFilename()) : (DEFAULT_PACKAGE_NAME + "." + clazz.getPackageName());
				String baseDirPath = packageName.replace('.', '/');
				
				File baseDir = new File(outputDir, baseDirPath);
				if (!baseDir.exists()) {
					baseDir.mkdirs();
				}
				
				String simpleName = getSimpleClassname(classname);
				String name = simpleName;
				if (!hasSpecifiedName) {
					name += r.getSuffix();
				}
				
				File outputFile = new File(baseDir,  name + JAVA_EXT);
				writeFile(r, packageName, name, clazz, api, outputFile);
			}
		}
	}
	
	private Map<String, List<Method>> getPublicApi(JavaClass clazz, Map<String, JavaClass> classes) {
		Map<String, List<Method>> api = new HashMap<String, List<Method>>();
		JavaClass currentClass = clazz;
		while (true) {
			String currentClassname = currentClass.getClassName();
			List<Method> methods = new ArrayList<Method>();
			for (Method m: currentClass.getMethods()) {
				methods.add(m);
			}
			collectInterfaceMethods(clazz, methods, classes);
			for (Method m: methods) {
				if (!m.isSynthetic() && (m.isPublic() || m.isProtected())) {
					String name = m.getName();
					
					List<Method> methodGroup;
					if (api.containsKey(name)) {
						methodGroup = api.get(name);
					} else {
						methodGroup = new ArrayList<Method>();
						api.put(name, methodGroup);
					}
					boolean found = false;
					String methodSig = m.getSignature();
					for (Method m1: methodGroup) {
						found = methodSig.equals(m1.getSignature());
						if (found) {
							break;
						}
					}
					if (!found) {
						methodGroup.add(m);
					}
				}
			}
			
			if (currentClassname.equals("java.lang.Object")) {
				break;
			} else {
				currentClass = classes.get(currentClass.getSuperclassName());
			}
		}
		return api;
	}
	
	private Map<String, JavaClass> readClasses(String[] libs) throws FileNotFoundException, IOException {
		Map<String, JavaClass> map = new HashMap<String, JavaClass>();
		for (String lib: libs) {
			Map<String, JavaClass> classes = readJar(lib);
			map.putAll(classes);
		}
		return map;
	}
	
	private Map<String, JavaClass> readJar(String path) throws FileNotFoundException, IOException {
		Map<String, JavaClass> classes = new HashMap<String, JavaClass>();
		JarInputStream jis = null;
		try {
			jis = new JarInputStream(new FileInputStream(path));
			for (ZipEntry ze = jis.getNextEntry(); ze != null; ze = jis
					.getNextEntry()) {
				String name = ze.getName();
				if (name.endsWith(CLASS_EXT)) {
					name = name.substring(0, name.length() - CLASS_EXT.length()).replace('/', '.').replace('$', '.');
					ClassParser cp = new ClassParser(jis, name);
					JavaClass clazz = cp.parse();
					classes.put(name, clazz);
				}
			}
		} finally {
			if (jis != null) {
				jis.close();
			}
		}
		return classes;
	}
	
	private String getBaseDir(String classname) {
		int idx = classname.lastIndexOf('.');
		String baseDir = classname.substring(0, idx);
		return baseDir;
	}
	
	private String getSimpleClassname(String classname) {
		int idx = classname.lastIndexOf('.');
		String name = classname.substring(idx + 1, classname.length());
		return name;
	}
	
	private void writeFile(DataRow data, String packageName, String name, JavaClass clazz, Map<String, List<Method>> api, File outputFile) throws IOException {
		PrintStream ps = null;
		Writer w = new Writer();
		
		try {
			ps = new PrintStream(outputFile);
			
			w.writeln("package " + packageName + ";");
			w.writeln();
			w.write("public class " + name);
			boolean isInterface = clazz.isInterface();
			String extendKeyword = isInterface ? " implements " : " extends ";
			w.write(extendKeyword);
			w.write(clazz.getClassName().replace('$', '.'));
			w.writeln(" {");
			writeConstructors(clazz, name, w);
			for (String methodName: data.getMethods()) {
				if (api.containsKey(methodName)) {
					List<Method> methodGroup = api.get(methodName);
					for (Method m: methodGroup) {
						String visibility = m.isPublic() ? "public" : "protected";
						w.write("\t");
						w.write(visibility);
						w.write(" ");
						writeType(m.getReturnType(), w);
						w.write(" ");
						w.write(m.getName());
						writeMethodSignature(m, w);
						w.write(" ");
						writeThrowsClause(m, w);
						w.writeln(" {");
						writeMethodBody(m, w);
						w.writeln("\t}");
						w.writeln();
					}
				}
			}
			w.writeln("}");
			
			ps.append(w.getSting());
			
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
		
		//outputFile.setLastModified(0);
	}
	
	private void writeMethodSignature(Method m, Writer w) {
		w.write('(');
		Type[] args = m.getArgumentTypes();
		for (int i=0; i<args.length; i++) {
			if (i > 0) {
				w.write(", ");
			}
			writeType(args[i], w);
			w.write(" param_");
			w.write(i);
		}
		w.write(')');
	}
	
	private void writeThrowsClause(Method m, Writer w) {
	}
	
	
	private void writeConstructors(JavaClass clazz, String classname, Writer w) {
		boolean isInterface = clazz.isInterface();
		if (isInterface) {
			w.write("\tpublic ");
			w.write(classname);
			w.writeln("() {");
			w.writeln("\t\tcom.tns.Platform.initInstance(this);");
			w.writeln("\t}");
			w.writeln();
		} else {
			List<Method> ctors = new ArrayList<Method>();
			for (Method m: clazz.getMethods()) {
				if (m.getName().equals("<init>")) {
					ctors.add(m);
				}
			}
			for (Method c: ctors) {
				String visibility =  c.isPublic() ? "public" : "protected";
				w.write("\t");
				w.write(visibility);
				w.write(" ");
				w.write(classname);
				writeMethodSignature(c, w);
				w.writeln("{");
				w.write("\t\tsuper(");
				Type[] ctorArgs = c.getArgumentTypes();
				for (int i=0; i<ctorArgs.length; i++) {
					if (i > 0) {
						w.write(", ");
					}
					w.write("param_");
					w.write(i);
				}
				w.writeln(");");
				w.writeln("\t\tcom.tns.Platform.initInstance(this);");
				w.writeln("\t}");
				w.writeln();
			}
		}
	}
	
	private void writeMethodBody(Method m, Writer w) {
		Type[] args = m.getArgumentTypes();
		w.write("\t\tjava.lang.Object[] args = ");
		if (args.length == 0) {
			w.writeln("null;");
		} else {
			w.write("new java.lang.Object[");
			w.write(args.length);
			w.writeln("];");
		}
		for (int i=0; i<args.length; i++) {
			w.write("\t\targs[");
			w.write(i);
			w.write("] = param_");
			w.write(i);
			w.writeln(";");
		}
		w.write("\t\t");
		Type ret = m.getReturnType();
		if (!ret.equals(Type.VOID)) {
			w.write("return (");
			writeType(ret, w);
			w.write(')');
		}
		w.write("com.tns.Platform.callJSMethod(this, \"");
		w.write(m.getName());
		w.write("\", ");
		writeType(ret, w);
		w.writeln(".class, args);");
	}
	
	private void writeType(Type t, Writer w) {
		String type = t.toString().replace('$', '.');
		w.write(type);
	}
	
	private void collectInterfaceMethods(JavaClass clazz, List<Method> methods, Map<String, JavaClass> classes) {
		JavaClass currentClass = clazz;
		while (true) {
			String currentClassname = currentClass.getClassName();
			
			Queue<String> queue = new ArrayDeque<String>();
			for (String name: clazz.getInterfaceNames()) {
				queue.add(name);	
			}

			while (!queue.isEmpty()) {
				String ifaceName = queue.poll();
				JavaClass currentInterface = classes.get(ifaceName.replace('$', '.'));
				Method[] ifaceMethods = currentInterface.getMethods();
				for (Method m: ifaceMethods) {
					methods.add(m);
				}
				for (String name: currentInterface.getInterfaceNames()) {
					queue.add(name);	
				}
			}
			
			if (currentClassname.equals("java.lang.Object")) {
				break;
			} else {
				currentClass = classes.get(currentClass.getSuperclassName());
			}
		}
	}
}
