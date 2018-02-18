package ru.spbau.mit.nedikov.teejava;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> files = new ArrayList<>();
        List<String> params = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith("-")) {
                params.add(arg);
            } else {
                files.add(arg);
            }
        }

        String[] paramsArray = new String[params.size()];

        final Argv argv = new Argv();
        try {
            JCommander jCommander = new JCommander(argv, params.toArray(paramsArray));
            jCommander.setProgramName("teeJava");
            if (argv.help) {
                jCommander.usage();
                return;
            }
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            System.out.println("Try 'java -jar teejava.jar --help' for more information.");
        }

        if (!argv.outputError.equals("warn") || argv.outputError.equals("exit")) {
            System.out.println("tee: invalid argument ‘" + argv.outputError + "’ for ‘--output-error’");
            System.out.println("Valid arguments are:");
            System.out.println("  - ‘warn‘  diagnose errors writing to any output");
            System.out.println("  - ‘exit‘  exit on error writing to any output");
            return;
        }

        boolean exitOnError = argv.outputError.equals("exit");


        final List<FileWriter> writers = new ArrayList<>();
        for(String file : files) {
            try {
                writers.add(new FileWriter(file, argv.appendMode));
            } catch (IOException e) {
                System.out.println("javaTee: " + e.getMessage());
                if (exitOnError) {
                    return;
                }
            }
        }


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (argv.ignoreInterrupts) {
                while (true) {}// stop this thread
            }

            for (FileWriter writer : writers) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("teeJava: " + e.getMessage());
                    if (exitOnError) {
                        return;
                    }
                }
            }
        }));

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            String line = null;
            try {
                line = input.readLine();
            } catch (IOException e) {
                System.out.println("teeJava: " + e.getMessage());
                if (exitOnError) {
                    return;
                }
            }
            if (line == null) {
                continue;
            }
            for (FileWriter writer : writers) {
                try {
                    writer.write(line);
                    writer.write('\n');
                    writer.flush();
                } catch (IOException e) {
                    System.out.println("teeJava: " + e.getMessage());
                    if (exitOnError) {
                        return;
                    }
                }
            }
            System.out.println(line);
            System.out.flush();
        }
    }
}
