package fr.umlads.uml2java;

import org.json.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage : " + "uml2java <source> <target>");
            System.exit(1);
        }

        boolean generateGettersAndSetters = false;
        
        if (Arrays.asList(args).contains("-sg")) {
            generateGettersAndSetters = true;
        }

        String source = args[0];
        String target = args[1];

        if (!Files.exists(Paths.get(source))) {
            System.out.println("Source file does not exist");
            System.exit(1);
        } else if (!Files.exists(Paths.get(target))) {
            System.out.println("Target directory does not exist");
            System.exit(1);
        }

        try {
            UML2Java translator = new UML2Java(source, target, generateGettersAndSetters);
            translator.generate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
