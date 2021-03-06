package org.nativescript.staticbindinggenerator.test;

import com.example.Guest;
import com.example.MyInterface;

import org.apache.bcel.classfile.Method;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;
import org.nativescript.staticbindinggenerator.Binding;
import org.nativescript.staticbindinggenerator.DataRow;
import org.nativescript.staticbindinggenerator.Generator;

import java.io.File;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneratorTest {
    private static String dependenciesDir;
    private static String runtimePath;

    @BeforeClass
    public static void setUp() {
        URL u = com.tns.Runtime.class.getResource('/' + com.tns.Runtime.class.getName().replace('.', '/') + ".class");
        try {
            dependenciesDir = new File(u.toURI()).getParentFile().getParentFile().getParentFile().getAbsolutePath();
            File runtimePathDir = new File(System.getProperty("java.home"), "lib/rt.jar");
            Assert.assertTrue(runtimePathDir.exists());
            runtimePath = runtimePathDir.getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCanCompileBinding() throws Exception {
        List<String> lines = Utils.getDataRowsFromResource("datarow-named-extend.txt");
        DataRow dataRow = new DataRow(lines.get(0));

        String outputDir = null;
        String[] libs = {runtimePath};
        Generator generator = new Generator(outputDir, libs);
        Binding binding = generator.generateBinding(dataRow);

        StringBuffer sourceCode = new StringBuffer();
        sourceCode.append(binding.getContent());

        Iterable<String> options = new ArrayList<String>(Arrays.asList("-cp", dependenciesDir));
        Class<?> helloClass = InMemoryJavaCompiler.compile(binding.getClassname(), sourceCode.toString(), options);

        Assert.assertNotNull(helloClass);
        Assert.assertEquals(3, helloClass.getDeclaredMethods().length);
    }

    @Test
    public void testCanCompileBindingOfInterfaceWithStaticInitializer() throws Exception {

        File rt = new File(System.getProperty("java.home"), "lib/rt.jar");
        Assert.assertTrue(rt.exists());

        List<String> lines = Utils.getDataRowsFromResource("datarow-classctor.txt");
        DataRow dataRow = new DataRow(lines.get(0));

        String outputDir = null;
        String[] libs = {rt.getAbsolutePath(), dependenciesDir};
        Generator generator = new Generator(outputDir, libs);
        Binding binding = generator.generateBinding(dataRow);

        StringBuffer sourceCode = new StringBuffer();
        sourceCode.append(binding.getContent());

        Iterable<String> options = new ArrayList<String>(Arrays.asList("-cp", dependenciesDir));
        Class<?> helloClass = InMemoryJavaCompiler.compile("com.tns.gen.com.example.MyInterface", sourceCode.toString(), options);

        Assert.assertNotNull(helloClass);
        Assert.assertEquals(1, helloClass.getDeclaredMethods().length);
    }

    @Test
    public void testCanCompileBindingClassImplementingMultipleInterfaces() throws Exception {
        List<String> lines = Utils.getDataRowsFromResource("datarow-class-extends-interfaces.txt");
        DataRow dataRow = new DataRow(lines.get(0));

        String outputDir = null;
        String[] libs = {runtimePath};
        Generator generator = new Generator(outputDir, libs);
        Binding binding = generator.generateBinding(dataRow);

        StringBuffer sourceCode = new StringBuffer();
        sourceCode.append(binding.getContent());

        Iterable<String> options = new ArrayList<String>(Arrays.asList("-cp", dependenciesDir));
        Class<?> ComplexClass = InMemoryJavaCompiler.compile(binding.getClassname(), sourceCode.toString(), options);

        Assert.assertNotNull(ComplexClass);
        Assert.assertEquals(5, ComplexClass.getInterfaces().length); // 4 + 1 (hashcodeprovider)
    }

    @Test
    public void testCanCompileBindingClassImplementingMultipleInterfacesWithTheSameSignature() throws Exception {
        List<String> lines = Utils.getDataRowsFromResource("datarow-class-extends-interfaces-with-same-method.txt");
        DataRow dataRow = new DataRow(lines.get(0));

        String outputDir = "generated";
        String[] libs = {runtimePath, dependenciesDir};
        Generator generator = new Generator(outputDir, libs);
        Binding binding = generator.generateBinding(dataRow);

        StringBuffer sourceCode = new StringBuffer();
        sourceCode.append(binding.getContent());

        Iterable<String> options = new ArrayList<String>(Arrays.asList("-cp", dependenciesDir));
        Class<?> MyClassWithPresent = InMemoryJavaCompiler.compile(binding.getClassname(), sourceCode.toString(), options);

        Assert.assertNotNull(MyClassWithPresent);
        Assert.assertEquals(3, MyClassWithPresent.getInterfaces().length); // 2 + 1 (hashcodeprovider)

        java.lang.reflect.Method[] methods = MyClassWithPresent.getMethods();
        boolean foundPresent = false;
        for (java.lang.reflect.Method method : methods) {
            if(method.getName() == "present") {
                if(foundPresent) {
                    Assert.fail("Only 1 void method 'present' should be written.");
                } else {
                    foundPresent = true;
                }
            }
        }

        Assert.assertTrue(foundPresent);
    }

    @Test
    public void testWillThrowWhenTryingToCompileBindingClassImplementingMultipleInterfacesWithTheMethodNameButDifferentReturnType() throws Exception {
        List<String> lines = Utils.getDataRowsFromResource("datarow-extends-interfaces-with-similar-methods-different-return.txt");
        DataRow dataRow = new DataRow(lines.get(0));

        String outputDir = "generated";
        String[] libs = {runtimePath, dependenciesDir};
        Generator generator = new Generator(outputDir, libs);
        Binding binding = generator.generateBinding(dataRow);

        StringBuffer sourceCode = new StringBuffer();
        sourceCode.append(binding.getContent());

        Iterable<String> options = new ArrayList<String>(Arrays.asList("-cp", dependenciesDir));
        Class<?> MyClassWithPresent = null;
        boolean caughtException = false;

        try {
            MyClassWithPresent = InMemoryJavaCompiler.compile(binding.getClassname(), sourceCode.toString(), options);
        } catch (java.lang.ClassFormatError exception) {
            caughtException = true;
        }

        Assert.assertTrue(caughtException);
        Assert.assertNull(MyClassWithPresent);
    }
}
