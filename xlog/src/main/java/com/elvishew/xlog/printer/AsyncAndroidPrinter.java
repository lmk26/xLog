/*
 * Copyright 2021 Elvis Hew
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elvishew.xlog.printer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An asynchronous {@link AndroidPrinter} which prints logs sequentially on a worker thread.
 *
 * <p>Each complete log is added to a queue, so chunks from concurrently submitted logs will not
 * interleave.
 */
public class AsyncAndroidPrinter extends AndroidPrinter {

  private final BlockingQueue<LogItem> logs = new LinkedBlockingQueue<>();

  /**
   * Constructor.
   */
  public AsyncAndroidPrinter() {
    this(false, DEFAULT_MAX_CHUNK_SIZE);
  }

  /**
   * Constructor.
   *
   * @param autoSeparate whether logs should be separated by line separator automatically
   */
  public AsyncAndroidPrinter(boolean autoSeparate) {
    this(autoSeparate, DEFAULT_MAX_CHUNK_SIZE);
  }

  /**
   * Constructor.
   *
   * @param maxChunkSize the max size of each log chunk
   */
  public AsyncAndroidPrinter(int maxChunkSize) {
    this(false, maxChunkSize);
  }

  /**
   * Constructor.
   *
   * @param autoSeparate whether logs should be separated by line separator automatically
   * @param maxChunkSize the max size of each log chunk
   */
  public AsyncAndroidPrinter(boolean autoSeparate, int maxChunkSize) {
    super(autoSeparate, maxChunkSize);
    Thread worker = new Thread(new Worker(), "XLog-AsyncAndroidPrinter");
    worker.setDaemon(true);
    worker.start();
  }

  @Override
  public void println(int logLevel, String tag, String msg) {
    logs.offer(new LogItem(logLevel, tag, msg));
  }

  private void print(LogItem log) {
    super.println(log.level, log.tag, log.msg);
  }

  private class Worker implements Runnable {

    @Override
    public void run() {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          print(logs.take());
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  private record LogItem(int level, String tag, String msg) {
  }
}
