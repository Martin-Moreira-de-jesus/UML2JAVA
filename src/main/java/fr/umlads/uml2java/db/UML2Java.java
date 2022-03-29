package fr.umlads.uml2java.db;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UML2Java {
    private final JSONObject project;
    private final String outputDirPath;
    private final boolean generateGettersAndSetters;
    private final String projectName;


    public UML2Java(String umlFilePath,
                    String outputDirPath,
                    boolean generateGettersAndSetters) throws IOException {
        String jsonFile = new String(Files.readAllBytes(Paths.get(umlFilePath)));
        this.project = new JSONObject(jsonFile);
        this.projectName = this.project.getString("name");
        this.outputDirPath = outputDirPath;
        this.generateGettersAndSetters = generateGettersAndSetters;
    }

    public void generate() throws IOException {
        JSONDB.init(project);

        Linker linker = new Linker();
        linker.link();

        Translator translator = new Translator(this.projectName, this.outputDirPath, this.generateGettersAndSetters);
        translator.translate();
    }
}
