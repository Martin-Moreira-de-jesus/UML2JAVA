package fr.umlads.uml2java.db;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UML2Java {
    static final String OBJECT_TYPE = "_type";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String PARENT = "parent";
    public static final String REFERENCE = "reference";
    public static final String REF = "$ref";
    public static final String VISIBILITY = "visibility";
    public static final String MULTIPLICITY = "multiplicity";
    public static final String OWNED_ELEMENTS = "ownedElements";
    public static final String ATTRIBUTES = "attributes";
    public static final String OPERATIONS = "operations";
    public static final String PARAMETERS = "parameters";
    public static final String DIRECTION = "direction";
    public static final String END1 = "end1";
    public static final String END2 = "end2";
    public static final String NAVIGABLE = "navigable";
    public static final String AGGREGATION = "aggregation";
    public static final String IS_STATIC = "isStatic";
    public static final String IS_ABSTRACT = "isAbstract";
    public static final String IS_READ_ONLY = "isReadOnly";
    public static final String INTERFACES_REALIZED = "interfacesRealized";
    private final JSONObject project;
    private final String outputDirPath;
    private final boolean generateGettersAndSetters;
    private final String projectName;
    private String arrayType = "[]";

    public UML2Java(String umlFilePath,
                    String outputDirPath,
                    boolean generateGettersAndSetters,
                    String arrayType) throws IOException {
        String jsonFile = new String(Files.readAllBytes(Paths.get(umlFilePath)));
        this.project = new JSONObject(jsonFile);
        this.projectName = this.project.getString(UML2Java.NAME);
        this.outputDirPath = outputDirPath;
        this.generateGettersAndSetters = generateGettersAndSetters;
        if (!arrayType.equals("")) {
            this.arrayType = arrayType;
        }
    }

    public void generate() throws IOException {
        JSONDB.init(project);

        Linker linker = new Linker();
        linker.link();

        Translator translator = new Translator(this.projectName, this.outputDirPath, this.generateGettersAndSetters, this.arrayType);
        translator.translate();
    }
}
