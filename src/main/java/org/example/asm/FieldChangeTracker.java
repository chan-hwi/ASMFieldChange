package org.example.asm;

import org.example.Instrumenter;
import org.example.runtime.FieldChangeLogger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class FieldChangeTracker extends ClassVisitor {
    private String className;

    public FieldChangeTracker(ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name.replace('/', '.');
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

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                super.visitFieldInsn(opcode, owner, name, descriptor);
                if (opcode == Opcodes.PUTFIELD || opcode == Opcodes.PUTSTATIC) {
                    // FieldChangeLogger.logFieldChange(this, className, fieldName);
                    if (opcode == Opcodes.PUTFIELD) {
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                    } else {
                        mv.visitInsn(Opcodes.ACONST_NULL);
                    }
                    mv.visitLdcInsn(owner.replace('/', '.'));
                    mv.visitLdcInsn(name);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                            FieldChangeLogger.STATE_CLASS_NAME.replace('.', '/'),
                            FieldChangeLogger.STATE_LOG_METHOD_NAME,
                            "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V", false);

                    Instrumenter.totalInstrumented++;
                }
            }
        };
    }
}