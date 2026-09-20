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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.elvishew.xlog.LogLevel;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AsyncAndroidPrinterTest {

  @Test
  public void testConcurrentMultilineMessagesDoNotInterleave() throws Exception {
    final List<String> chunks = Collections.synchronizedList(new ArrayList<String>());
    final CountDownLatch chunksPrinted = new CountDownLatch(4);
    final CountDownLatch start = new CountDownLatch(1);
    final AsyncAndroidPrinter printer = new AsyncAndroidPrinter(true) {
      @Override
      void printChunk(int logLevel, String tag, String msg) {
        chunks.add(msg);
        chunksPrinted.countDown();
      }
    };

    Thread firstThread = createPrintThread(printer, start, "first-1\nfirst-2");
    Thread secondThread = createPrintThread(printer, start, "second-1\nsecond-2");
    firstThread.start();
    secondThread.start();
    start.countDown();

    firstThread.join(1000);
    secondThread.join(1000);
    assertFalse(firstThread.isAlive());
    assertFalse(secondThread.isAlive());
    assertTrue(chunksPrinted.await(1, TimeUnit.SECONDS));

    List<String> firstThenSecond = Arrays.asList("first-1", "first-2", "second-1", "second-2");
    List<String> secondThenFirst = Arrays.asList("second-1", "second-2", "first-1", "first-2");
    assertTrue(chunks.equals(firstThenSecond) || chunks.equals(secondThenFirst));
  }

  @Test
  public void testPrintAsynchronously() throws Exception {
    final CountDownLatch printStarted = new CountDownLatch(1);
    final CountDownLatch continuePrinting = new CountDownLatch(1);
    AsyncAndroidPrinter printer = new AsyncAndroidPrinter() {
      @Override
      void printChunk(int logLevel, String tag, String msg) {
        printStarted.countDown();
        try {
          continuePrinting.await();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    };

    printer.println(LogLevel.DEBUG, "tag", "message");
    assertTrue(printStarted.await(1, TimeUnit.SECONDS));
    assertEquals(1, continuePrinting.getCount());
    continuePrinting.countDown();
  }

  private Thread createPrintThread(final AsyncAndroidPrinter printer,
                                   final CountDownLatch start, final String msg) {
    return new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          start.await();
          printer.println(LogLevel.DEBUG, "tag", msg);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });
  }
}
