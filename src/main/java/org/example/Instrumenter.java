package org.example;

import org.example.asm.FieldChangeTracker;
import org.example.asm.InstrumentClassWriter;
import org.example.utils.Path;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

public class Instrumenter {
    public static int totalInstrumented = 0;

    private final Map<String, Double> timeMap = new HashMap<>();

    //    private final String sourcePath;
    private final String targetPath;
//    private final String testPath;

    //    private final Map<String, ClassReader> sourceNodes = new HashMap<String, ClassReader>();
    private final Map<String, ClassReader> targetNodes = new HashMap<String, ClassReader>();
//    private final Map<String, ClassReader> testNodes = new HashMap<String, ClassReader>();

    private void loadFiles(final String path, Map<String, ClassReader> nodeMap) throws IOException {
        List<String> files = Path.getAllSources(new File(path));
        for (String file : files) {
            nodeMap.put(Path.removeSrcPath(file, path), new ClassReader(new FileInputStream(file)));
        }
    }

    public Instrumenter(String targetPath) throws IOException {
//        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
//        this.testPath = testPath;

//        loadFiles(sourcePath, sourceNodes);
        loadFiles(targetPath, targetNodes);
//        loadFiles(testPath, testNodes);
    }

    public void instrument(String timeFileOutput) throws IOException {
        for (Map.Entry<String, ClassReader> entry : targetNodes.entrySet()) {
            long start = Calendar.getInstance().getTimeInMillis();
            InstrumentClassWriter cw = new InstrumentClassWriter(targetPath, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            FieldChangeTracker fct = new FieldChangeTracker(cw);
            entry.getValue().accept(fct, ClassReader.EXPAND_FRAMES);

            final byte[] transformed = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream(targetPath + File.separator + entry.getKey());
            fos.write(transformed);
            fos.close();

            double totalTime = (Calendar.getInstance().getTimeInMillis() - start) / 1000.0;
            timeMap.put(entry.getKey(), totalTime);
        }

        // Output total time consumed for each instrument - Code By Youngjae Kim
        if (!timeFileOutput.equals("")) {
            FileWriter writer=new FileWriter(timeFileOutput);
            for (Map.Entry<String,Double> entry : timeMap.entrySet()){
                writer.write(entry.getKey()+","+entry.getValue()+"\n");
            }
            writer.close();
        }

        Main.LOGGER.info("Total instrumented: " + totalInstrumented);
    }
}
