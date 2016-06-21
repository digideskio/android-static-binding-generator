package org.nativescript.staticbindinggenerator.test;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;
import org.nativescript.staticbindinggenerator.Binding;
import org.nativescript.staticbindinggenerator.DataRow;
import org.nativescript.staticbindinggenerator.Generator;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneratorTest {
    @Test
    public void testCanCompileBinding() throws Exception {
        URL u = com.tns.Runtime.class.getResource('/' + com.tns.Runtime.class.getName().replace('.', '/') + ".class");
        File f = new File(u.toURI()).getParentFile().getParentFile().getParentFile();

        File rt = new File(System.getProperty("java.home"), "lib/rt.jar");
        Assert.assertTrue(rt.exists());

        String s = IOUtils.toString(this.getClass().getResource("datarow-named-extend.txt"), "UTF-8");
        List<String> lines = IOUtils.readLines(new StringReader(s));
        DataRow dataRow = new DataRow(lines.get(0));

        String outputDir = null;
        String[] libs = {rt.getAbsolutePath()};
        Generator generator = new Generator(outputDir, libs);
        Binding binding = generator.generateBinding(dataRow);

        StringBuffer sourceCode = new StringBuffer();
        sourceCode.append(binding.getContent());

        Iterable<String> options = new ArrayList<String>(Arrays.asList("-cp", f.getAbsolutePath()));
        Class<?> helloClass = InMemoryJavaCompiler.compile(binding.getClassname(), sourceCode.toString(), options);

        Assert.assertNotNull(helloClass);
        Assert.assertEquals(3, helloClass.getDeclaredMethods().length);
    }
}
