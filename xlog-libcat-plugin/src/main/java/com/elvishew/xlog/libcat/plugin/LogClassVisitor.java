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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class LogClassVisitor extends ClassVisitor {

  static final String ANDROID_LOG = "android/util/Log";
  static final String LIBCAT = "com/elvishew/xlog/libcat/internal/Cat";

  private static final Set<String> SUPPORTED_METHODS = new HashSet<>(Arrays.asList(
      signature("v", "(Ljava/lang/String;Ljava/lang/String;)I"),
      signature("v", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I"),
      signature("d", "(Ljava/lang/String;Ljava/lang/String;)I"),
      signature("d", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I"),
      signature("i", "(Ljava/lang/String;Ljava/lang/String;)I"),
      signature("i", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I"),
      signature("w", "(Ljava/lang/String;Ljava/lang/String;)I"),
      signature("w", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I"),
      signature("w", "(Ljava/lang/String;Ljava/lang/Throwable;)I"),
      signature("e", "(Ljava/lang/String;Ljava/lang/String;)I"),
      signature("e", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I"),
      signature("println", "(ILjava/lang/String;Ljava/lang/String;)I")
  ));

  LogClassVisitor(ClassVisitor classVisitor) {
    super(Opcodes.ASM9, classVisitor);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
      String[] exceptions) {
    MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
    return new MethodVisitor(api, methodVisitor) {
      @Override
      public void visitMethodInsn(int opcode, String owner, String methodName,
          String methodDescriptor, boolean isInterface) {
        if (opcode == Opcodes.INVOKESTATIC
            && ANDROID_LOG.equals(owner)
            && SUPPORTED_METHODS.contains(signature(methodName, methodDescriptor))) {
          owner = LIBCAT;
        }
        super.visitMethodInsn(opcode, owner, methodName, methodDescriptor, isInterface);
      }
    };
  }

  private static String signature(String name, String descriptor) {
    return name + descriptor;
  }
}
