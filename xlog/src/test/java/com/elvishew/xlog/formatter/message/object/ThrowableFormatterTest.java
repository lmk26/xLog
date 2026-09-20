/*
 * Copyright 2026 lmk26
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

package com.elvishew.xlog.formatter.message.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.PrintWriter;

public class ThrowableFormatterTest {

  private final ThrowableFormatter formatter = new ThrowableFormatter();

  @Test
  public void testFormatThrowable() {
    RuntimeException throwable = new RuntimeException(
        "outer", new IllegalArgumentException("inner"));

    String result = formatter.format(throwable);

    assertTrue(result.contains("java.lang.RuntimeException: outer"));
    assertTrue(result.contains("Caused by: java.lang.IllegalArgumentException: inner"));
  }

  @Test
  public void testFallbackToStringWhenStackTraceFormattingFails() {
    Throwable throwable = new Throwable("fallback") {
      @Override
      public void printStackTrace(PrintWriter writer) {
        throw new IllegalStateException("failed");
      }
    };

    assertEquals(throwable.toString(), formatter.format(throwable));
  }

  @Test
  public void testFormatNull() {
    assertEquals("null", formatter.format(null));
  }
}
