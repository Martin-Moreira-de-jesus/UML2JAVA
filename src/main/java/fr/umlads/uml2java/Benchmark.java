package fr.umlads.uml2java;

import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Benchmark {
    public static void main(String[] args) {
        /*
        File directory = new File("BenchmarkFiles/");
        ArrayList<String> files = new ArrayList<String>(List.of(Objects.requireNonNull(directory.list())));

        long average = 0;

        try {
            File output = new File("jsondb_benchmark_results.csv");
            if (!output.exists()) {
                if (!output.createNewFile()) {
                    throw new IOException("Couldn't create file");
                }
            }
            PrintWriter writer = new PrintWriter(new FileWriter(output), true);
            writer.println("classes,associations,time,avg");
            for (String file : files) {
                writer.print(file.substring(0, file.indexOf("c")) + ","
                        + file.substring(file.indexOf("_") + 1, file.indexOf("asso")) + ',');

                long start = System.currentTimeMillis();
                for (int i = 0; i < 1000; i++) {
                    String jsonFile = new String(Files.readAllBytes(Path.of("BenchmarkFiles/", file)));
                    JSONObject project = new JSONObject(jsonFile);
                    JSONDB.init(project);
                    Translator translator = new Translator();
                    translator.translate();
                }
                long end = System.currentTimeMillis();
                writer.println(end - start + ",");

                average += end - start;
            }
            writer.println(",,," + average/files.size());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

    }
}
