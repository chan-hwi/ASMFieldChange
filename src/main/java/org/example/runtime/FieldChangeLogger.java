package org.example.runtime;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

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

    private static final Map<String, Map<String, List<Map<String, Object>>>> fieldChanges = new HashMap<>();

    public static void logFieldChange(int branchId, Object instance, String owner) {
        try {
            Class<?> clazz = Class.forName(owner);

            String instanceKey = instance != null ? instance.toString() : owner;
            String branchKey = instanceKey + "." + branchId;

            Map<String, Object> fieldMap = new HashMap<>();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);

                Object value = field.get(instance);
                fieldMap.put(field.getName(), value);
            }
            synchronized (fieldChanges) {
                fieldChanges.computeIfAbsent(owner, k -> new HashMap<>())
                        .computeIfAbsent(branchKey, k -> new ArrayList<>())
                        .add(fieldMap);
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
                    XStream xstream = new XStream();
                    xstream.toXML(fieldChanges, resultFile);

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