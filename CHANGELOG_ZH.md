# 更新日志

[English](CHANGELOG.md)

本文件记录项目的主要变更。

## 2.0.0 - 2026-09-20

### 新增

- 新增 `AsyncAndroidPrinter`，通过单工作线程依次打印完整日志，避免并发多行日志相互交错。
- 新增基于 AGP Instrumentation API 和 ASM 实现的 `com.github.lmk26.xlog.libcat` Gradle 插件。
- 新增 LibCat 插桩测试和打印器并发测试。
- Android 默认对象格式化器新增 `ThrowableFormatter`，直接打印 `Throwable` 对象时会输出完整堆栈，格式化失败时回退到 `Throwable.toString()`。
- 为 `xlog`、`xlog-libcat` 和 `xlog-libcat-plugin` 增加 Maven Publish 与 JitPack 构建配置。
- 源码构建及 sample 新增 `project`、`mavenLocal` 和 `jitpack` 三种依赖模式。

### 变更

- 将已停止维护的 AspectJX LibCat 实现替换为 AGP 字节码插桩。现在必须应用 LibCat Gradle 插件，才能拦截项目及依赖类中受支持的 `android.util.Log` 调用。
- 保持 `AndroidPrinter` 为同步实现，并将 `AsyncAndroidPrinter` 作为明确的异步选择。
- Android 日志分块由 Java 字符数量改为按 UTF-8 字节大小计算，避免中文、Emoji 等多字节字符导致日志内容丢失。
- 将新版文件备份和平铺器能力合并到不带数字后缀的接口及实现中。
- 更新 Android 与 Gradle 构建配置，并将仓库声明集中到 `settings.gradle`。

### 移除

- 移除 LibCat 的 AspectJ 实现及 AspectJ Runtime 依赖。
- 移除已弃用的兼容 API，包括 `XLog.Log`、`t()`、`st()`、`b()` 等配置简写方法及过时的初始化重载。
- 移除已被替代的 `Flattener2`、`BackupStrategy2`、`FileSizeBackupStrategy2`、`SystemPrinter`、`LogFlattener` 和 `DefaultLogFlattener`。

### 迁移说明

- 将已移除的简写方法替换为含义明确的方法，例如 `enableThreadInfo()`、`enableStackTrace(...)` 和 `enableBorder()`。
- 将 `BackupStrategy2`、`FileSizeBackupStrategy2` 替换为 `BackupStrategy`、`FileSizeBackupStrategy`。
- 将直接使用的 `XLog.Log` 兼容 API 替换为 XLog API，或应用 LibCat Gradle 插件，对受支持的 `android.util.Log` 调用进行插桩。
- 通过 JitPack 使用多模块制品时，应使用 JitPack 模块 group：`com.github.lmk26.xLog`。
