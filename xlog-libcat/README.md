# LibCat

[简体中文](README_ZH.md)

Intercept the logs directly logged by `android.util.Log` within whole app's code, and redirect the logs to specified `Printer`.

Mostly, `LibCat` is used to intercept the logs from third party modules/libraries, and save the logs to the log file, by specifying a `FilePrinter`.

About `Printer`s, see more in [XLog].

## Quick Start

Replace `<latest-version>` below with the latest stable version shown on [JitPack](https://jitpack.io/#lmk26/xLog).

Add JitPack to plugin and dependency resolution in `settings.gradle`:

```groovy
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url 'https://jitpack.io' }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Declare the plugin in the root `build.gradle`:

```groovy
plugins {
    id 'com.github.lmk26.xlog.libcat' version '<latest-version>' apply false
}
```

Apply the plugin and add LibCat in the app module:

```groovy
apply plugin: 'com.github.lmk26.xlog.libcat'

dependencies {
    implementation 'com.github.lmk26:xlog-libcat:<latest-version>'
}
```

Config when initializing app

```java
LibCat.config(true, printer);

// This call remains in Logcat and is also forwarded to printer.
Log.d("LibCat", "This log is intercepted by LibCat");
```

The plugin will then redirect supported `android.util.Log` calls to LibCat during compilation.

## Examples

* Logs in `logcat` and `printer`

```java
LibCat.config(true, printer);
```

* Logs in `logcat` only (exactly like the situation before using `LibCat`)

```java
LibCat.config(true, null);
```

* Logs in `printer` only

```java
LibCat.config(false, printer);
```

* Logs disappear totally

```java
LibCat.config(false, null);
```

## Attention

During compilation, the LibCat Gradle plugin uses the AGP Instrumentation API and ASM to replace supported `android.util.Log` calls with LibCat calls. Source code is not changed. Both project classes and library dependencies are instrumented, while xLog's own classes are excluded to avoid recursive interception.

## License

<pre>
Copyright 2021 Elvis Hew

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>

[Printer]: ../xlog/src/main/java/com/elvishew/xlog/printer/Printer.java
[XLog]: ../README.md
