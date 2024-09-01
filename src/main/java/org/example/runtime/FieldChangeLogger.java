package org.example.runtime;

import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class FieldChangeLogger {
    public static final String STATE_CLASS_NAME = "org.example.runtime.FieldChangeLogger";

    public static final String STATE_LOG_METHOD_NAME = "logFieldChange";

    public static final String STATE_IS_INITIALIZED = "isInitialized";

    public static final String STATE_INIT = "initialize";

    public static final Logger logger = Logger.getLogger("org.example.runtime.FieldChangeLogger");

    public static boolean isInitialized = false;

    public static FileWriter resultFile = null;

    public static FileWriter resultHashFile = null;

    public static XStreamFactory xStreamFactory = new XStreamFactory();

    private static final Map<String, List<Object>> fieldChanges = new HashMap<>();

    private static final Map<String, Integer> fieldChangeHashes = new HashMap<>();

    public static void logFieldChange(Object instance, String owner) {
        try {
            Class<?> clazz = Class.forName(owner);

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.getType().isPrimitive()) continue;

                final String fieldKey = owner + "::" + field.getName();
                final Object value = field.get(instance);
                fieldChanges.computeIfAbsent(fieldKey, k -> new ArrayList<>()).add(value);
                fieldChangeHashes.put(fieldKey, fieldChangeHashes.computeIfAbsent(fieldKey, k -> 0) * 31 + value.hashCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initialize() {
        isInitialized = true;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    resultFile = new FileWriter("field_change_history.xml");
                    resultHashFile = new FileWriter("field_change_hash.xml");

                    xStreamFactory.getXStream(XStreamFactory.XStreamType.HISTORY).toXML(fieldChanges, resultFile);
                    xStreamFactory.getXStream(XStreamFactory.XStreamType.HASH).toXML(fieldChangeHashes, resultHashFile);

                    resultFile.close();
                    resultHashFile.close();
                } catch (Exception e) {
                    FileWriter fw;
                    try {
                        fw = new FileWriter("/tmp/fcl.err");
                        fw.write(e.getMessage());
                        fw.close();
                    } catch (Exception e1) {
                        System.err.println("Cannot open error file: /tmp/fcl.err");
                        e1.printStackTrace();
                    }
                }
            }
        }));
    }
}