import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

public class Translator {
    public final String projectPath = "C:\\Users\\marti\\IdeaProjects\\UML2JAVA";

    public Translator(JSONObject umlProject) throws IOException {
        JSONDB.DATABASE = new JSONDB(umlProject);

        Linker linker = new Linker();

        linker.link();

        this.translate();
    }

    public void translate() throws IOException {
        JSONArray classes = JSONDB.DATABASE.fetchClasses();

        for (int i = 0; i < classes.length(); i++) {
            JSONObject currentClass = classes.getJSONObject(i);

            File createdFile = makeDirsAndFile(currentClass);

            PrintWriter writer = new PrintWriter(createdFile);
            writer.println(UML2Java(currentClass));
            writer.close();
        }
    }

    public File makeDirsAndFile(JSONObject currentClass) throws IOException {
        String filePath = "\\output\\src\\";
        if (currentClass.getString("_package").equals("null")) {
            filePath += "Main\\";
        } else {
            String[] dirs = currentClass.getString("_package").split("\\.");

            for (int j = 0; j < dirs.length; j++) {
                filePath += dirs[j] + "\\";
            }
        }

        File directory = new File(projectPath + filePath);

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Created directories: " + directory.getAbsolutePath());
            } else {
                System.out.println("Couldn't create directories");
            }
        }

        filePath += currentClass.getString("name") + ".java";

        File classFile = new File(projectPath + filePath);
        if (classFile.createNewFile()) {
            System.out.println("File created: " + classFile.getName());
        } else {
            System.out.println("upsie");
        }

        return classFile;
    }

    private String translateAttributes(JSONArray attributes) {
        String result = "";
        for (int i = 0; i < attributes.length(); i++) {
            JSONObject attribute = attributes.getJSONObject(i);
            result += "\t" + (attribute.has("visibility") ? attribute.getString("visibility") : "public") + " "
                    + attribute.getString("type") + " "
                    + attribute.getString("name") + ";\n";
        }
        result += '\n';
        return result;
    }

    private String translateParameters(JSONArray parameters) {
        String result = "";
        for (int j = 0; j < parameters.length(); j++) {
            JSONObject parameter = parameters.getJSONObject(j);

            result += parameter.getString("type") + " " + parameter.getString("name") + ", ";
        }

        result = result.substring(0, result.length() - 2);

        return result;
    }

    private String translateOperations(JSONArray operations) {
        String result = "";

        for (int i = 0; i < operations.length(); i++) {
            JSONObject operation = operations.getJSONObject(i);
            String operationType = getOperationType(operation);

            result += "\t" + (operation.has("visibility") ? operation.getString("visibility") : "public") + " "
                    + operationType + " "
                    + operation.getString("name") + "(";
            if (operation.has("parameters")) {
                result += translateParameters(operation.getJSONArray("parameters"));
            }
        }

        return result;
    }

    private String UML2Java(JSONObject umlClass) {
        if (umlClass.getString("_type").equals("UMLClass")) {
            String result = "";
            result += "public class " + umlClass.getString("name") + " ";

            if (umlClass.has("extends")) {
                result += "extends " + umlClass.getJSONObject("extends").getString("name") + " ";
            }

            result += "{\n";

            if (umlClass.has("attributes")) {
                result += translateAttributes(umlClass.getJSONArray("attributes"));
            }

            if (umlClass.has("operations")) {
                result += translateOperations(umlClass.getJSONArray("operations"));
                result += ") {\n\t\t//TODO\n\t}\n";
            }

            return result + "}";
        } else {
            return "";
        }
    }

    private String getOperationType(JSONObject operation) {
        if (operation.has("parameters")) {
            JSONArray parameters = operation.getJSONArray("parameters");
            for (int i = 0; i < operation.length(); i++) {
                if (parameters.getJSONObject(i).has("direction")) {
                    parameters.remove(i);
                    return parameters.getJSONObject(i).getString("type");
                }
            }
        }
        return "void";
    }
}
