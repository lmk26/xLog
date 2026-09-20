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

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LogClassVisitorTest {

  @Test
  public void replacesSupportedLogCallsOnly() {
    ClassWriter source = new ClassWriter(0);
    source.visit(Opcodes.V17, Opcodes.ACC_PUBLIC, "test/Logger", null, "java/lang/Object", null);
    MethodVisitor method = source.visitMethod(
        Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "log", "()V", null, null);
    method.visitCode();
    method.visitLdcInsn("tag");
    method.visitLdcInsn("message");
    method.visitMethodInsn(Opcodes.INVOKESTATIC, LogClassVisitor.ANDROID_LOG, "d",
        "(Ljava/lang/String;Ljava/lang/String;)I", false);
    method.visitInsn(Opcodes.POP);
    method.visitLdcInsn("tag");
    method.visitInsn(Opcodes.ICONST_3);
    method.visitMethodInsn(Opcodes.INVOKESTATIC, LogClassVisitor.ANDROID_LOG, "isLoggable",
        "(Ljava/lang/String;I)Z", false);
    method.visitInsn(Opcodes.POP);
    method.visitInsn(Opcodes.RETURN);
    method.visitMaxs(2, 0);
    method.visitEnd();
    source.visitEnd();

    ClassWriter transformed = new ClassWriter(0);
    new ClassReader(source.toByteArray()).accept(new LogClassVisitor(transformed), 0);

    List<String> owners = new ArrayList<>();
    new ClassReader(transformed.toByteArray()).accept(new ClassVisitor(Opcodes.ASM9) {
      @Override
      public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
          String[] exceptions) {
        return new MethodVisitor(Opcodes.ASM9) {
          @Override
          public void visitMethodInsn(int opcode, String owner, String methodName,
              String methodDescriptor, boolean isInterface) {
            owners.add(owner);
          }
        };
      }
    }, 0);

    assertEquals(Arrays.asList(LogClassVisitor.LIBCAT, LogClassVisitor.ANDROID_LOG), owners);
  }
}
