import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Translator {
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
        String filePath = System.getProperty("user.dir") + "/GeneratedProjet/src/";
        if (currentClass.getString("_package").equals("null")) {
            filePath += "Main/";
        } else {
            String[] dirs = currentClass.getString("_package").split("/.");

            for (int j = 0; j < dirs.length; j++) {
                filePath += dirs[j] + "/";
            }
        }

        File directory = new File(filePath);

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Created directories: " + directory.getAbsolutePath());
            } else {
                System.out.println("Couldn't create directories: " + directory.getAbsolutePath());
            }
        }

        filePath += currentClass.getString("name") + ".java";

        File classFile = new File(filePath);
        if (classFile.createNewFile()) {
            System.out.println("File created: " + classFile.getName());
        } else {
            System.out.println("Couldn't create file: " + classFile.getName());
        }

        return classFile;
    }

    private String addSettersGetters(JSONArray attributes) {
        String result = "";

        for (int i = 0; i < attributes.length(); i++) {
            JSONObject attribute = attributes.getJSONObject(i);

            System.out.println(attribute);

            if (!attribute.has("visibility") || attribute.getString("visibility").equals("public")) continue;

            result += "\tpublic "
                    + attribute.getString("type") + " "
                    + "get" + Character.toUpperCase(attribute.getString("name").charAt(0)) + attribute.getString("name").substring(1)
                    + "() {\n"
                    + "\t\treturn " + attribute.getString("name")
                    + ";\n\t}\n\n";

            if (!attribute.has("isReadOnly")) {
                result += "\tpublic "
                        + "void "
                        + "set" + Character.toUpperCase(attribute.getString("name").charAt(0))
                                + attribute.getString("name").substring(1)
                        + "("
                        + attribute.getString("type") + " " + attribute.getString("name")
                        + ") {\n"
                        + "\t\treturn this." + attribute.getString("name")
                        + ";\n\t}\n\n";
            }
        }

        return result;
    }

    private String translateAttributes(JSONArray attributes) {
        String result = "";
        for (int i = 0; i < attributes.length(); i++) {
            JSONObject attribute = attributes.getJSONObject(i);
            result += "\t" + (attribute.has("visibility") ? attribute.getString("visibility") : "public") + " "
                    + (attribute.has("isStatic") ? "static " : "")
                    + attribute.getString("type") + " "
                    + attribute.getString("name") + ";\n";
        }
        result += '\n';
        return result;
    }

    private String translateParameters(JSONArray parameters) {
        if (parameters.length() == 0) return "";
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
                    + (operation.has("isAbstract") ? "abstract " : "")
                    + operationType + " "
                    + operation.getString("name") + "(";
            if (operation.has("parameters")) {
                result += translateParameters(operation.getJSONArray("parameters"));
            }

            result += ") {\n\t\t//TODO\n\t}\n\n";
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
            }

            if (umlClass.has("attributes")) {
                result += addSettersGetters(umlClass.getJSONArray("attributes"));
            }

            return result + "}";
        } else {
            return "";
        }
    }

    private String getOperationType(JSONObject operation) {
        if (operation.has("parameters")) {
            JSONArray parameters = operation.getJSONArray("parameters");
            for (int i = 0; i < parameters.length(); i++) {
                if (parameters.getJSONObject(i).has("direction")) {
                    String type = parameters.getJSONObject(i).getString("type");
                    parameters.remove(i);
                    return type;
                }
            }
        }
        return "void";
    }
}
