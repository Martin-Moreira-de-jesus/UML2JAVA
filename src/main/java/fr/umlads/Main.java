package fr.umlads;

import fr.java2uml.JavaAnalyser;
import fr.umlads.uml2java.db.UML2Java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage : " + "umlads <uml2java|java2uml> <source> <target> [options]");
            System.exit(1);
        }

        String direction = args[0];
        String source = args[1];
        String target = args[2];

        if (!Files.exists(Paths.get(source))) {
            System.out.println("Source file does not exist");
            System.exit(1);
        } else if (!Files.exists(Paths.get(target)) && !target.equals(".")) {
            System.out.println("Target directory does not exist");
            System.exit(1);
        }

        if (direction.equals("uml2java")) {
            boolean generateGettersAndSetters = false;

            if (Arrays.asList(args).contains("-sg")) {
                generateGettersAndSetters = true;
            }
            try {
                UML2Java translator = new UML2Java(source, target, generateGettersAndSetters);
                translator.generate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (direction.equals("java2uml")) {
            JavaAnalyser analyser = new JavaAnalyser(source, target);
        }
        else {
            System.out.println("Unknown direction");
            System.exit(1);
        }
    }


}
