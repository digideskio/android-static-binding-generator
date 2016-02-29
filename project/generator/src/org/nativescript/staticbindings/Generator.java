package org.nativescript.staticbindings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class Generator {
	
	private static final String CLASS_EXT = ".class";
	
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
	
	private void processRows(List<DataRow> rows, String outputDir, Map<String, JavaClass> classes) {
		for (DataRow r: rows) {
			String classname = r.getBaseClassname();
			boolean isJavaExtend = classes.containsKey(classname);
			if (isJavaExtend) {
				JavaClass clazz = classes.get(classname);

				Map<String, List<Method>> api = getPublicApi(clazz, classes);

				// TODO: use api
			}
		}
	}
	
	private Map<String, List<Method>> getPublicApi(JavaClass clazz, Map<String, JavaClass> classes) {
		Map<String, List<Method>> api = new HashMap<String, List<Method>>();
		JavaClass currentClass = clazz;
		while (true) {
			String currentClassname = currentClass.getClassName();
			Method[] methods = currentClass.getMethods();
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
}
