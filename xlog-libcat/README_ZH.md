# LibCat

[English](README.md)

拦截所有在 APP 代码里通过 `android.util.Log` 直接打印的日志，并把这些日志重定向到指定的 `Printer`。

大多数情况下，`LibCat` 被用来拦截第三方模块/库的日志，并通过指定 `FilePrinter`，把这些日志保存到文件中。

关于 `Printer`，请到 [XLog] 了解更多信息。

## 快速开始

请将下面的 `<latest-version>` 替换为 [JitPack](https://jitpack.io/#lmk26/xLog) 中最新的稳定版本号。

在 `settings.gradle` 中为插件和依赖添加 JitPack：

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

在根 `build.gradle` 中声明插件：

```groovy
plugins {
    id 'com.github.lmk26.xlog.libcat' version '<latest-version>' apply false
}
```

在应用模块中应用插件并添加 LibCat：

```groovy
apply plugin: 'com.github.lmk26.xlog.libcat'

dependencies {
    implementation 'com.github.lmk26:xlog-libcat:<latest-version>'
}
```

在初始化 APP 时进行配置

```java
LibCat.config(true, printer);

// 该调用会保留在 Logcat，并同时转发给 printer。
Log.d("LibCat", "This log is intercepted by LibCat");
```

此后，插件会在编译阶段将支持的 `android.util.Log` 调用重定向到 LibCat。

## 示例

* 在 `logcat` 和 `printer` 中都有日志

```java
LibCat.config(true, printer);
```

* 只在 `logcat` 有日志 (和没有使用 `LibCat` 时一样的现象)

```java
LibCat.config(true, null);
```

* 只在 `printer` 有日志

```java
LibCat.config(false, printer);
```

* 日志彻底消失

```java
LibCat.config(false, null);
```

## 注意

编译期间，LibCat Gradle 插件通过 AGP Instrumentation API 和 ASM，将支持的 `android.util.Log` 调用替换成 LibCat 调用，不会修改源码。项目代码和依赖库都会被插桩，同时排除 xLog 自身类以避免递归拦截。

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
[XLog]: ../README_ZH.md
