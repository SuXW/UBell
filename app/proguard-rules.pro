# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/dftx/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
 -dontskipnonpubliclibraryclassmembers
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#-libraryjars   libs/android-support-v4.jar

#-libraryjars   libs/android-async-http-1.4.4.jar
#-libraryjars   libs/isoviewer-1.0-RC-28.jar
#-libraryjars   libs/mframework.jar
#-libraryjars   libs/photoview.jar
#-libraryjars   libs/MiPush_SDK_Client_3_5_2.jar
#-libraryjars   libs/square-otto-1.3.2.jar
##-libraryjars   libs/ubiahttp.jar
#-libraryjars   libs/universal-image-loader-1.9.1.jar
#-libraryjars   libs/zxing.jar
#-libraryjars   libs/org.apache.http.legacy.jar

-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.my.IOTC.**{ *;}
-keep public class com.tutk.IOTC.**{ *;}
-keep public class cn.ubia.**{ *;}
-keep public class voice.**{ *;}
-keep public class  com.xiaomi.**{ *;}
-keep public class  com.keeplife.**{ *;}

-keep class com.baidu.push.DemoMessageReceiver {*;}
-dontwarn uk.co.senab.photoview.**
-dontwarn com.loopj.android.http.**
 -keep class com.loopj.android.http.**{ *;}

-dontwarn  org.apache.http.**
 -keep class org.apache.http.**{ *;}

-dontwarn com.my.IOTC.**
 -keep class com.my.IOTC.**{ *;}

-dontwarn com.jeremyfeinstein.**
 -keep class com.jeremyfeinstein.** { *;}

-dontwarn  com.actionbarsherlock.**
 -keep class  com.actionbarsherlock.** { *;}

 -dontwarn   com.google.zxing.**
 -keep class   com.google.zxing.** { *;}


 -dontwarn   android.support.**
 -keep class   android.support.** { *;}

 -dontwarn    com.coremedia.**
 -keep class    com.coremedia.** { *;}


 -dontwarn org.jdesktop.**
 -keep class  org.jdesktop.** { *;}

 -dontwarn  com.googlecode.**
 -keep class  com.googlecode.** { *;}

 -dontwarn com.xiaomi.**
 -keep class   com.xiaomi.** { *;}

 -dontwarn com.decoder.util.**
 -keep class   com.decoder.util.** { *;}
 -dontwarn com.sinowave.ddp**
-keep class   com.sinowave.ddp** { *;}

 -dontwarn    com.ubia.**
 -keep class    com.ubia.** { *;}

 -dontwarn    voice.**
 -keep class    voice.** { *;}

 -dontwarn com.keeplife.**
 -keep class   com.keeplife.** { *;}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.** {* ;}
-keep class com.tencent.mid.** {* ;}
-keep public class * extends com.qq.taf.jce.JceStruct{*;}