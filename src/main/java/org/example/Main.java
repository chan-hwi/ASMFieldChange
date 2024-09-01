package org.example;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.logging.Logger;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static final Logger LOGGER = Logger.getGlobal();

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar ASMTest.jar <original program classpath> <patched program classpath>");
            System.exit(1);
        }

        Options options = new Options();
        options.addOption("t", "time-output-file", true, "Output file path for each time to instrument file");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String timeOutputFile = cmd.getOptionValue("t", "");

        String[] parsedArgs = cmd.getArgs();

        // Convert Windows path separators (\\) to single backslash
        final String sourcePath = parsedArgs[0].replace("\\\\", "\\");
        final String targetPath = parsedArgs[1].replace("\\\\", "\\");

        try {
            Instrumenter instrumenter = new Instrumenter(sourcePath, targetPath);
            instrumenter.instrument(timeOutputFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}