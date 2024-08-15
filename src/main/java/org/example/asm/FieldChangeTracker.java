package org.example.asm;

import org.example.Instrumenter;
import org.example.runtime.FieldChangeLogger;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;

import java.util.ArrayList;
import java.util.List;

public class FieldChangeTracker extends ClassVisitor {
    private String className;
    private int branchCounter = 0;

    public FieldChangeTracker(ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name.replace('/', '.');
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv == null) {
            return null;
        }
        return new AdviceAdapter(Opcodes.ASM9, mv, access, name, descriptor) {
            @Override
            protected void onMethodEnter() {
                // check if the current method is a constructor
                if (name.equals("<init>")) {
                    // if (!FieldChangeLogger.isInitialized) initialize();
                    Label exit = new Label();
                    mv.visitFieldInsn(Opcodes.GETSTATIC,
                            FieldChangeLogger.STATE_CLASS_NAME.replace('.', '/'),
                            FieldChangeLogger.STATE_IS_INITIALIZED, "Z");
                    mv.visitJumpInsn(Opcodes.IFNE, exit);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                            FieldChangeLogger.STATE_CLASS_NAME.replace('.', '/'),
                            FieldChangeLogger.STATE_INIT, "()V", false);
                    mv.visitLabel(exit);
                }

                super.onMethodEnter();
            }

            protected void onMethodExit(int opcode) {
                mv.visitLdcInsn(branchCounter++);
                if ((this.methodAccess & ACC_STATIC) != 0) {
                    // static method call
                    mv.visitInsn(Opcodes.ACONST_NULL);
                } else {
                    // instance method call
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                }
                mv.visitLdcInsn(className.replace('/', '.'));
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        FieldChangeLogger.STATE_CLASS_NAME.replace('.', '/'),
                        FieldChangeLogger.STATE_LOG_METHOD_NAME,
                        "(ILjava/lang/Object;Ljava/lang/String;)V", false);
            }

//            @Override
//            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
//                super.visitFieldInsn(opcode, owner, name, descriptor);
//                if (opcode == Opcodes.PUTFIELD || opcode == Opcodes.PUTSTATIC) {
//                    // FieldChangeLogger.logFieldChange(this, className, fieldName);
//                    if (opcode == Opcodes.PUTFIELD) {
//                        mv.visitVarInsn(Opcodes.ALOAD, 0);
//                    } else {
//                        mv.visitInsn(Opcodes.ACONST_NULL);
//                    }
//                    mv.visitLdcInsn(owner.replace('/', '.'));
//                    mv.visitLdcInsn(name);
//                    mv.visitMethodInsn(Opcodes.INVOKESTATIC,
//                            FieldChangeLogger.STATE_CLASS_NAME.replace('.', '/'),
//                            FieldChangeLogger.STATE_LOG_METHOD_NAME,
//                            "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V", false);
//
//                    Instrumenter.totalInstrumented++;
//                }
//            }

            @Override
            public void visitJumpInsn(int opcode, Label label) {
                mv.visitLdcInsn(branchCounter++);
                if ((this.methodAccess & ACC_STATIC) != 0) {
                    // static method call
                    mv.visitInsn(Opcodes.ACONST_NULL);
                } else {
                    // instance method call
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                }
                mv.visitLdcInsn(className.replace('/', '.'));
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        FieldChangeLogger.STATE_CLASS_NAME.replace('.', '/'),
                        FieldChangeLogger.STATE_LOG_METHOD_NAME,
                        "(ILjava/lang/Object;Ljava/lang/String;)V", false);
                super.visitJumpInsn(opcode, label);
            }
        };
    }
}