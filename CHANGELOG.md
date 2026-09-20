# Changelog

[简体中文](CHANGELOG_ZH.md)

All notable changes to this project are documented in this file.

## 2.0.0 - 2026-09-20

### Added

- Added `AsyncAndroidPrinter`, which queues complete log messages and prints them sequentially on a single worker thread to prevent concurrent multiline logs from interleaving.
- Added the `com.github.lmk26.xlog.libcat` Gradle plugin, implemented with the AGP Instrumentation API and ASM.
- Added LibCat instrumentation tests and concurrent printer tests.
- Added `ThrowableFormatter` to Android's built-in object formatters. Logging a `Throwable` object now prints its complete stack trace and falls back to `Throwable.toString()` if formatting fails.
- Added Maven Publish and JitPack build configuration for `xlog`, `xlog-libcat`, and `xlog-libcat-plugin`.
- Added `project`, `mavenLocal`, and `jitpack` dependency modes for source builds and the sample.

### Changed

- Replaced the unmaintained AspectJX-based LibCat implementation with AGP bytecode instrumentation. Applying the LibCat Gradle plugin is now required to intercept supported `android.util.Log` calls in project and dependency classes.
- Kept `AndroidPrinter` synchronous and provided `AsyncAndroidPrinter` as an explicit asynchronous alternative.
- Changed Android log chunking to use UTF-8 byte size instead of Java character count, preventing content loss for Chinese text, emoji, and other multibyte characters.
- Consolidated the newer file backup and flattener APIs into the non-suffixed interfaces and implementations.
- Updated the Android and Gradle build configuration and centralized repository declarations in `settings.gradle`.

### Removed

- Removed the AspectJ implementation and AspectJ runtime dependency from LibCat.
- Removed deprecated compatibility APIs, including `XLog.Log`, shorthand configuration methods such as `t()`, `st()`, and `b()`, and obsolete initialization overloads.
- Removed superseded APIs including `Flattener2`, `BackupStrategy2`, `FileSizeBackupStrategy2`, `SystemPrinter`, `LogFlattener`, and `DefaultLogFlattener`.

### Migration notes

- Replace removed shorthand methods with their descriptive equivalents, such as `enableThreadInfo()`, `enableStackTrace(...)`, and `enableBorder()`.
- Replace `BackupStrategy2` and `FileSizeBackupStrategy2` with `BackupStrategy` and `FileSizeBackupStrategy`.
- Replace direct `XLog.Log` compatibility usage with XLog APIs, or apply the LibCat Gradle plugin to instrument supported `android.util.Log` calls.
- When consuming the multi-module build from JitPack, use the JitPack module group `com.github.lmk26.xLog`.
