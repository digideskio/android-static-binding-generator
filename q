[1mdiff --git a/project/Generator/app/src/androidTest/java/org/nativescript/staticbindinggenerator/ApplicationTest.java b/project/Generator/app/src/androidTest/java/org/nativescript/staticbindinggenerator/ApplicationTest.java[m
[1mdeleted file mode 100644[m
[1mindex 8c75474..0000000[m
[1m--- a/project/Generator/app/src/androidTest/java/org/nativescript/staticbindinggenerator/ApplicationTest.java[m
[1m+++ /dev/null[m
[36m@@ -1,13 +0,0 @@[m
[31m-package org.nativescript.staticbindinggenerator;[m
[31m-[m
[31m-import android.app.Application;[m
[31m-import android.test.ApplicationTestCase;[m
[31m-[m
[31m-/**[m
[31m- * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>[m
[31m- */[m
[31m-public class ApplicationTest extends ApplicationTestCase<Application> {[m
[31m-    public ApplicationTest() {[m
[31m-        super(Application.class);[m
[31m-    }[m
[31m-}[m
\ No newline at end of file[m
[1mdiff --git a/project/Generator/app/src/main/AndroidManifest.xml b/project/Generator/app/src/main/AndroidManifest.xml[m
[1mdeleted file mode 100644[m
[1mindex b6a2e0d..0000000[m
[1m--- a/project/Generator/app/src/main/AndroidManifest.xml[m
[1m+++ /dev/null[m
[36m@@ -1,13 +0,0 @@[m
[31m-<manifest xmlns:android="http://schemas.android.com/apk/res/android"[m
[31m-    package="org.nativescript.staticbindinggenerator">[m
[31m-[m
[31m-    <application[m
[31m-        android:allowBackup="true"[m
[31m-        android:icon="@mipmap/ic_launcher"[m
[31m-        android:label="@string/app_name"[m
[31m-        android:supportsRtl="true"[m
[31m-        android:theme="@style/AppTheme">[m
[31m-[m
[31m-    </application>[m
[31m-[m
[31m-</manifest>[m
[1mdiff --git a/project/Generator/app/src/main/res/mipmap-hdpi/ic_launcher.png b/project/Generator/app/src/main/res/mipmap-hdpi/ic_launcher.png[m
[1mdeleted file mode 100644[m
[1mindex cde69bc..0000000[m
Binary files a/project/Generator/app/src/main/res/mipmap-hdpi/ic_launcher.png and /dev/null differ
[1mdiff --git a/project/Generator/app/src/main/res/mipmap-mdpi/ic_launcher.png b/project/Generator/app/src/main/res/mipmap-mdpi/ic_launcher.png[m
[1mdeleted file mode 100644[m
[1mindex c133a0c..0000000[m
Binary files a/project/Generator/app/src/main/res/mipmap-mdpi/ic_launcher.png and /dev/null differ
[1mdiff --git a/project/Generator/app/src/main/res/mipmap-xhdpi/ic_launcher.png b/project/Generator/app/src/main/res/mipmap-xhdpi/ic_launcher.png[m
[1mdeleted file mode 100644[m
[1mindex bfa42f0..0000000[m
Binary files a/project/Generator/app/src/main/res/mipmap-xhdpi/ic_launcher.png and /dev/null differ
[1mdiff --git a/project/Generator/app/src/main/res/mipmap-xxhdpi/ic_launcher.png b/project/Generator/app/src/main/res/mipmap-xxhdpi/ic_launcher.png[m
[1mdeleted file mode 100644[m
[1mindex 324e72c..0000000[m
Binary files a/project/Generator/app/src/main/res/mipmap-xxhdpi/ic_launcher.png and /dev/null differ
[1mdiff --git a/project/Generator/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png b/project/Generator/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png[m
[1mdeleted file mode 100644[m
[1mindex aee44e1..0000000[m
Binary files a/project/Generator/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png and /dev/null differ
[1mdiff --git a/project/Generator/app/src/main/res/values/colors.xml b/project/Generator/app/src/main/res/values/colors.xml[m
[1mdeleted file mode 100644[m
[1mindex 2a12c47..0000000[m
[1m--- a/project/Generator/app/src/main/res/values/colors.xml[m
[1m+++ /dev/null[m
[36m@@ -1,6 +0,0 @@[m
[31m-<?xml version="1.0" encoding="utf-8"?>[m
[31m-<resources>[m
[31m-    <color name="colorPrimary">#3F51B5</color>[m
[31m-    <color name="colorPrimaryDark">#303F9F</color>[m
[31m-    <color name="colorAccent">#FF4081</color>[m
[31m-</resources>[m
[1mdiff --git a/project/Generator/app/src/main/res/values/strings.xml b/project/Generator/app/src/main/res/values/strings.xml[m
[1mdeleted file mode 100644[m
[1mindex 660069f..0000000[m
[1m--- a/project/Generator/app/src/main/res/values/strings.xml[m
[1m+++ /dev/null[m
[36m@@ -1,3 +0,0 @@[m
[31m-<resources>[m
[31m-    <string name="app_name">Static Binding Generator</string>[m
[31m-</resources>[m
[1mdiff --git a/project/Generator/app/src/main/res/values/styles.xml b/project/Generator/app/src/main/res/values/styles.xml[m
[1mdeleted file mode 100644[m
[1mindex 6f19b47..0000000[m
[1m--- a/project/Generator/app/src/main/res/values/styles.xml[m
[1m+++ /dev/null[m
[36m@@ -1,11 +0,0 @@[m
[31m-<resources>[m
[31m-[m
[31m-    <!-- Base application theme. -->[m
[31m-    <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">[m
[31m-        <!-- Customize your theme here. -->[m
[31m-        <item name="colorPrimary">@color/colorPrimary</item>[m
[31m-        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>[m
[31m-        <item name="colorAccent">@color/colorAccent</item>[m
[31m-    </style>[m
[31m-[m
[31m-</resources>[m
[1mdiff --git a/project/Generator/app/src/test/java/org/nativescript/staticbindinggenerator/ExampleUnitTest.java b/project/Generator/app/src/test/java/org/nativescript/staticbindinggenerator/ExampleUnitTest.java[m
[1mdeleted file mode 100644[m
[1mindex 2743ca9..0000000[m
[1m--- a/project/Generator/app/src/test/java/org/nativescript/staticbindinggenerator/ExampleUnitTest.java[m
[1m+++ /dev/null[m
[36m@@ -1,15 +0,0 @@[m
[31m-package org.nativescript.staticbindinggenerator;[m
[31m-[m
[31m-import org.junit.Test;[m
[31m-[m
[31m-import static org.junit.Assert.*;[m
[31m-[m
[31m-/**[m
[31m- * To work on unit tests, switch the Test Artifact in the Build Variants view.[m
[31m- */[m
[31m-public class ExampleUnitTest {[m
[31m-    @Test[m
[31m-    public void addition_isCorrect() throws Exception {[m
[31m-        assertEquals(4, 2 + 2);[m
[31m-    }[m
[31m-}[m
\ No newline at end of file[m
