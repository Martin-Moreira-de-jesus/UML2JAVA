package fr.umlads.uml2java;

import org.json.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static String target = "";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage : " + "uml2java <source>");
            System.exit(1);
        }
        String source = args[0];

        if (args.length > 1) {
            target = args[1];
        }

        try {
            String filePath = System.getProperty("user.dir") + (source.charAt(0) == '/' ? "" : "/") + source;
            String jsonFile = new String(Files.readAllBytes(Paths.get(source)));
            JSONObject project = new JSONObject(jsonFile);
            Translator translator = new Translator(project);
            System.out.println(JSONDB.DATABASE.getDatabase());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
