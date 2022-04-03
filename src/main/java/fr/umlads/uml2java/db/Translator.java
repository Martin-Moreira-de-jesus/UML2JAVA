package fr.umlads.uml2java.db;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Translator {
    private final boolean addGettersAndSetters;
    private final String outputDirPath;
    private final String projectName;

    public Translator(String projectName, String outputDirPath, boolean addGettersAndSetters) {
        this.projectName = projectName;
        this.addGettersAndSetters = addGettersAndSetters;
        this.outputDirPath = outputDirPath;
    }

    public void translate() throws IOException {
        JSONArray classes = JSONDB.getInstance().fetchClasses();

        preProcess(classes);

        if (this.addGettersAndSetters) {
            addGettersAndSetters(classes);
        }

        for (int i = 0; i < classes.length(); i++) {
            JSONObject currentClass = classes.getJSONObject(i);

            File createdFile = makeDirsAndFile(currentClass);

            PrintWriter writer = new PrintWriter(createdFile);
            writer.println(UML2Java(currentClass));
            writer.close();
        }
    }

    private void addGettersAndSetters(JSONArray classes) {
        for (Object o : classes) {
            JSONObject umlClass = (JSONObject) o;
            if (!umlClass.has("attributes")) continue;
            JSONArray attributes = umlClass.getJSONArray("attributes");
            for (Object attribute : attributes) {
                JSONObject classAttribute = (JSONObject) attribute;

                if (classAttribute.optString("isStatic").equals("true")
                        || classAttribute.optString("visibility").equals("public")) continue;

                if (!umlClass.has("operations")) {
                    umlClass.put("operations", new JSONArray());
                }

                // getter operation
                JSONObject getter = new JSONObject();
                getter.put("name", "get" + classAttribute.getString("name").substring(0, 1).toUpperCase() + classAttribute.getString("name").substring(1));
                getter.put("visibility", "public");

                // parameter that represents the return value
                JSONObject returnParameter = new JSONObject();
                returnParameter.put("direction", "return");
                returnParameter.put("type", classAttribute.getString("type"));

                getter.put("parameters", new JSONArray());
                getter.getJSONArray("parameters").put(returnParameter);

                getter.put("content", "return " + classAttribute.getString("name") + ";");

                umlClass.getJSONArray("operations").put(getter);

                if (classAttribute.optString("isReadOnly").equals("true")) continue;

                JSONObject setter = new JSONObject();

                setter.put("name", "set" + classAttribute.getString("name").substring(0, 1).toUpperCase()  + classAttribute.getString("name").substring(1));
                setter.put("visibility", "public");
                setter.put("returnType", "void");

                returnParameter = new JSONObject();
                returnParameter.put("direction", "return");
                returnParameter.put("type", "void");

                JSONObject setterParameter = new JSONObject();
                setterParameter.put("name", classAttribute.getString("name"));
                setterParameter.put("type", classAttribute.getString("type"));

                setter.put("parameters", new JSONArray());
                setter.getJSONArray("parameters").put(returnParameter);
                setter.getJSONArray("parameters").put(setterParameter);

                setter.put("content", "this." + classAttribute.getString("name") + " = " + classAttribute.getString("name") + ";");

                umlClass.getJSONArray("operations").put(setter);
            }
        }
    }

    private void preProcess(JSONArray classes) {
        preProcessTemplateParameters(classes);

        //preProcessAttributes(classes); // to chose other kinds of collection types
    }

    private void preProcessAttributes(JSONArray classes) {
        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i < classes.length(); i++) {
            if (!classes.getJSONObject(i).has("attributes")) continue;
            JSONArray attributes = classes.getJSONObject(i).getJSONArray("attributes");

            for (int j = 0; j < attributes.length(); j++) {
                JSONObject attribute = attributes.getJSONObject(j);

                if (!attribute.getString("type").contains("[]")) continue;

                System.out.print("The attribute <" + attribute.getString("name") + "> from the class <"
                        + classes.getJSONObject(i).getString("name") + "> default type was put to <"
                        + attribute.getString("type") + ">. Type new type (leave empty to keep default type):");

                String type = scanner.nextLine();

                if (type.equals("")) {
                    continue;
                }

                attribute.put("type", type);
            }
        }
    }

    private void preProcessTemplateParameters(JSONArray classes) {
        classes.forEach(item -> {
            JSONObject umlClass = (JSONObject) item;
            if (umlClass.has("templateParameters")) {
                String className = umlClass.getString("name") + "<";

                JSONArray templateParameters = umlClass.getJSONArray("templateParameters");
                for (int i = 0; i < templateParameters.length(); i++) {
                    className += templateParameters.getJSONObject(i).getString("name") + ", ";
                }

                className = className.substring(0, className.length() - 2);

                className += ">";

                umlClass.remove("name");
                umlClass.put("name", className);
            }
        });
    }

    public File makeDirsAndFile(JSONObject currentClass) throws IOException {
        String filePath = (outputDirPath.equals("") ? System.getProperty("user.dir") : outputDirPath) + "/"
                + this.projectName + "/src/";
        if (currentClass.getString("_package").equals("null")) {
            filePath += "com/company/";
        } else {
            String[] dirs = currentClass.getString("_package").split("\\.");
            for (String dir : dirs) {
                filePath += dir + "/";
            }
        }

        File directory = new File(filePath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        filePath += currentClass.getString("name");

        if (filePath.contains("<"))
            filePath = filePath.substring(0, filePath.indexOf("<"));

        filePath += ".java";

        File classFile = new File(filePath);

        if (classFile.createNewFile()) {
            //System.out.println("File created: " + classFile.getName());
        } else {
            //System.out.println("Couldn't create file: " + classFile.getName());
        }

        return classFile;
    }

    private String translateAttributes(JSONArray attributes) {
        String result = "";
        for (int i = 0; i < attributes.length(); i++) {
            JSONObject attribute = attributes.getJSONObject(i);

            result += "\t" + (attribute.has("visibility") ? attribute.getString("visibility") : "public") + " "
                    + (attribute.has("isStatic") && (Boolean) attribute.get("isStatic") ? "static " : "")
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

            try {
                result += parameter.getString("type");
            } catch (JSONException e) {
                result += JSONDB.getInstance().getById(parameter.getJSONObject("type").getString("$ref")).getString("name");
            }
            result += " " + parameter.getString("name") + ", ";
        }

        result = result.substring(0, result.length() - 2);

        return result;
    }

    private String translateOperations(JSONArray operations, String type, String className) {
        String result = "";

        for (int i = 0; i < operations.length(); i++) {
            JSONObject operation = operations.getJSONObject(i);
            String operationType = getOperationType(operation);

            if (operation.has("keyword")) {
                result += "\t@" + operation.getString("keyword") + "\n";
            }

            result += "\t" + (operation.has("visibility") ? operation.getString("visibility") : "public") + " "
                    + (operation.has("isAbstract") && (Boolean) operation.get("isAbstract") ? "abstract " : "")
                    + (operation.getString("name").equals(operationType) && operationType.equals(className) ? "" : operationType + " ")
                    + operation.getString("name") + "(";

            if (operation.has("parameters")) {
                result += translateParameters(operation.getJSONArray("parameters"));
            }

            result += ")";
            if (type.equals("class")) {
                result += " {\n\t\t";

                if (operation.has("content")) {
                    result += operation.getString("content");
                } else {
                    result += "//TODO";
                }

                result += "\n\t}";
            } else {
                result += ";";
            }
            result += "\n\n";
        }
        return result;
    }

    private String UML2Java(JSONObject umlClass) {
        String type = "class";
        if (umlClass.getString("_type").equals("UMLInterface")) {
            type = "interface";
        }
        String result = "";
        result += "public " + (umlClass.has("isAbstract") && (Boolean) umlClass.get("isAbstract")
                ? "abstract " : "") + type + " " + umlClass.getString("name") + " ";

        if (umlClass.has("extends")) {
            result += "extends " + umlClass.getJSONObject("extends").getString("name") + " ";
        }

        if (umlClass.has("interfacesRealized")) {
            result += "implements ";
            for (Object o : umlClass.getJSONArray("interfacesRealized")) {
                result += ((JSONObject) o).getString("name") + ", ";
            }
            result = result.substring(0, result.length() - 2);

            result += " ";
        }

        result += "{\n";

        if (umlClass.has("attributes")) {
            result += translateAttributes(umlClass.getJSONArray("attributes"));
        }

        if (umlClass.has("operations")) {
            result += translateOperations(umlClass.getJSONArray("operations"), type, umlClass.getString("name"));
        }

        return result + "}";
    }

    private String getOperationType(JSONObject operation) {
        if (operation.has("parameters")) {
            JSONArray parameters = operation.getJSONArray("parameters");
            for (int i = 0; i < parameters.length(); i++) {
                if (parameters.getJSONObject(i).has("direction")) {
                    String type;
                    try {
                        type = parameters.getJSONObject(i).getString("type");
                    } catch (JSONException e) {
                        String reference = parameters.getJSONObject(i).getJSONObject("type").getString("$ref");
                        type = JSONDB.getInstance().getById(reference).getString("name");
                    }

                    parameters.remove(i);
                    return type;
                }
            }
        }
        return "void";
    }
}
