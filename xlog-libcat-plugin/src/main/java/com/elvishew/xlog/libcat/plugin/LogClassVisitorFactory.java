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

package com.elvishew.xlog.libcat.plugin;

import com.android.build.api.instrumentation.AsmClassVisitorFactory;
import com.android.build.api.instrumentation.ClassContext;
import com.android.build.api.instrumentation.ClassData;
import com.android.build.api.instrumentation.InstrumentationParameters;

import org.objectweb.asm.ClassVisitor;

/**
 * Creates visitors that replace supported android.util.Log calls with LibCat calls.
 */
public abstract class LogClassVisitorFactory
    implements AsmClassVisitorFactory<InstrumentationParameters.None> {

  private static final String XLOG_PACKAGE = "com.elvishew.xlog.";

  @Override
  public boolean isInstrumentable(ClassData classData) {
    return !classData.getClassName().startsWith(XLOG_PACKAGE);
  }

  @Override
  public ClassVisitor createClassVisitor(ClassContext classContext, ClassVisitor nextClassVisitor) {
    return new LogClassVisitor(nextClassVisitor);
  }
}
