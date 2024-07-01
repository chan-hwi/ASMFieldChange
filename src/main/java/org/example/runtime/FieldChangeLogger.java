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

    private static final Map<String, Map<String, List<Object>>> fieldChanges = new HashMap<>();

    public static void logFieldChange(Object instance, String owner, String fieldName) {
        try {
            Class<?> clazz = Class.forName(owner);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(instance);
            String instanceKey = instance != null ? instance.toString() : owner;

            synchronized (fieldChanges) {
                fieldChanges.computeIfAbsent(instanceKey, k -> new HashMap<>())
                        .computeIfAbsent(fieldName, k -> new ArrayList<>())
                        .add(value);
                logger.info("Field change logged: " + owner + "." + fieldName + " = " + value);
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
                    resultFile = new FileWriter("field_change_history.txt");
                    for (Map.Entry<String, Map<String, List<Object>>> fcEntry : fieldChanges.entrySet()) {
                        int total = 0;
                        for (List<Object> elem : fcEntry.getValue().values()) total += elem.size();
                        resultFile.write(fcEntry.getKey() + "(" + total + ")" + "\n");
                        for (Map.Entry<String, List<Object>> fchEntry : fcEntry.getValue().entrySet()) {
                            resultFile.write("\t" + fchEntry.getKey() + "(" + fchEntry.getValue().size() + ") = " + fchEntry.getValue() + "\n");
                        }
                    }
                    resultFile.close();
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