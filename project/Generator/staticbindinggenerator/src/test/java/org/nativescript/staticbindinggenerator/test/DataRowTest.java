package org.nativescript.staticbindinggenerator.test;

import org.apache.commons.io.IOUtils;
import org.nativescript.staticbindinggenerator.DataRow;
import org.nativescript.staticbindinggenerator.Generator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class DataRowTest {
    @Test
    public void testParseInterface() throws IOException {
        String s = IOUtils.toString(this.getClass().getResource("datarow-interface.txt"), "UTF-8");
        List<String> lines = IOUtils.readLines(new StringReader(s));

        DataRow row = new DataRow(lines.get(0));

        Assert.assertEquals(row.getBaseClassname(), "java.lang.Runnable");
        Assert.assertEquals(row.getSuffix(), "_fapp_tns_modules_timer_timer_l14_c20__");
        Assert.assertEquals(row.getFilename(), "");
        Assert.assertEquals(row.getJsFilename(), "");
        String[] methods = row.getMethods();
        Assert.assertEquals(methods.length, 1);
        Assert.assertEquals(methods[0], "run");
    }

    @Test
    public void testParseNamedExtend() throws IOException {
        String s = IOUtils.toString(this.getClass().getResource("datarow-named-extend.txt"), "UTF-8");
        List<String> lines = IOUtils.readLines(new StringReader(s));

        DataRow row = new DataRow(lines.get(0));

        Assert.assertEquals(row.getBaseClassname(), "java.lang.Object");
        Assert.assertEquals(row.getSuffix(), "_frnal_prepareExtend_l62_c37__HelloWorldModel");
        Assert.assertEquals(row.getFilename(), "a.b.c.MyObject");
        Assert.assertEquals(row.getJsFilename(), "some/full/path/myobj.js");
        String[] methods = row.getMethods();
        Assert.assertEquals(methods.length, 1);
        Assert.assertEquals(methods[0], "hashCode");
    }
}
