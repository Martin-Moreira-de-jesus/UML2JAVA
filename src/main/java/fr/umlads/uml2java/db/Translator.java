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
    private String arrayType = "default";

    public Translator(String projectName, String outputDirPath, boolean addGettersAndSetters, String arrayType) {
        this.projectName = projectName;
        this.addGettersAndSetters = addGettersAndSetters;
        this.outputDirPath = outputDirPath;
        if (!arrayType.equals("")) {
            if (!arrayType.contains("<>")) {
                System.out.println("Invalid array type, the array type must contain <>");
                System.exit(1);
            }
            this.arrayType = arrayType;
        }
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
            if (!umlClass.has(UML2Java.ATTRIBUTES)) continue;
            JSONArray attributes = umlClass.getJSONArray(UML2Java.ATTRIBUTES);
            for (Object attribute : attributes) {
                JSONObject classAttribute = (JSONObject) attribute;

                if (classAttribute.optString(UML2Java.IS_STATIC).equals("true")
                        || classAttribute.optString(UML2Java.VISIBILITY).equals("public")) continue;

                if (!umlClass.has(UML2Java.OPERATIONS)) {
                    umlClass.put(UML2Java.OPERATIONS, new JSONArray());
                }

                // getter operation
                JSONObject getter = new JSONObject();
                getter.put(UML2Java.NAME, "get" + classAttribute.getString(UML2Java.NAME).substring(0, 1).toUpperCase() + classAttribute.getString(UML2Java.NAME).substring(1));
                getter.put(UML2Java.VISIBILITY, "public");

                // parameter that represents the return value
                JSONObject returnParameter = new JSONObject();
                returnParameter.put(UML2Java.DIRECTION, "return");
                returnParameter.put(UML2Java.TYPE, classAttribute.getString(UML2Java.TYPE));

                getter.put(UML2Java.PARAMETERS, new JSONArray());
                getter.getJSONArray(UML2Java.PARAMETERS).put(returnParameter);

                getter.put("content", "return " + classAttribute.getString(UML2Java.NAME) + ";");

                umlClass.getJSONArray(UML2Java.OPERATIONS).put(getter);

                if (classAttribute.optString(UML2Java.IS_READ_ONLY).equals("true")) continue;

                JSONObject setter = new JSONObject();

                setter.put(UML2Java.NAME, "set" + classAttribute.getString(UML2Java.NAME).substring(0, 1).toUpperCase()  + classAttribute.getString(UML2Java.NAME).substring(1));
                setter.put(UML2Java.VISIBILITY, "public");
                setter.put("returnType", "void");

                returnParameter = new JSONObject();
                returnParameter.put(UML2Java.DIRECTION, "return");
                returnParameter.put(UML2Java.TYPE, "void");

                JSONObject setterParameter = new JSONObject();
                setterParameter.put(UML2Java.NAME, classAttribute.getString(UML2Java.NAME));
                setterParameter.put(UML2Java.TYPE, classAttribute.getString(UML2Java.TYPE));

                setter.put(UML2Java.PARAMETERS, new JSONArray());
                setter.getJSONArray(UML2Java.PARAMETERS).put(returnParameter);
                setter.getJSONArray(UML2Java.PARAMETERS).put(setterParameter);

                setter.put("content", "this." + classAttribute.getString(UML2Java.NAME) + " = " + classAttribute.getString(UML2Java.NAME) + ";");

                umlClass.getJSONArray(UML2Java.OPERATIONS).put(setter);
            }
        }
    }

    private void preProcess(JSONArray classes) {
        preProcessTemplateParameters(classes);

        preProcessAttributes(classes);
    }

    private void preProcessAttributes(JSONArray classes) {
        for (int i = 0; i < classes.length(); i++) {
            if (!classes.getJSONObject(i).has(UML2Java.ATTRIBUTES)) continue;
            JSONArray attributes = classes.getJSONObject(i).getJSONArray(UML2Java.ATTRIBUTES);

            for (int j = 0; j < attributes.length(); j++) {
                JSONObject attribute = attributes.getJSONObject(j);
                if (attribute.getString(UML2Java.TYPE).contains("[]") && !this.arrayType.equals("default")) {
                    String type = this.arrayType;
                    String oldType = attribute.getString(UML2Java.TYPE);
                    oldType = oldType.replace("[]", "");
                    type = type.substring(0, type.indexOf("<") + 1) + oldType + ">";
                    attribute.put(UML2Java.TYPE, type);
                }
            }
        }
    }

    private void preProcessTemplateParameters(JSONArray classes) {
        classes.forEach(item -> {
            JSONObject umlClass = (JSONObject) item;
            if (umlClass.has("templateParameters")) {
                String className = umlClass.getString(UML2Java.NAME) + "<";

                JSONArray templateParameters = umlClass.getJSONArray("templateParameters");
                for (int i = 0; i < templateParameters.length(); i++) {
                    className += templateParameters.getJSONObject(i).getString(UML2Java.NAME) + ", ";
                }

                className = className.substring(0, className.length() - 2);

                className += ">";

                umlClass.remove(UML2Java.NAME);
                umlClass.put(UML2Java.NAME, className);
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

        filePath += currentClass.getString(UML2Java.NAME);

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

            result += "\t" + (attribute.has(UML2Java.VISIBILITY) ? attribute.getString(UML2Java.VISIBILITY) : "public") + " "
                    + (attribute.has(UML2Java.IS_STATIC) && (Boolean) attribute.get(UML2Java.IS_STATIC) ? "static " : "")
                    + attribute.getString(UML2Java.TYPE) + " "
                    + attribute.getString(UML2Java.NAME) + ";\n";
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
                result += parameter.getString(UML2Java.TYPE);
            } catch (JSONException e) {
                result += JSONDB.getInstance().getById(parameter.getJSONObject(UML2Java.TYPE).getString(UML2Java.REF)).getString(UML2Java.NAME);
            }
            result += " " + parameter.getString(UML2Java.NAME) + ", ";
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

            result += "\t" + (operation.has(UML2Java.VISIBILITY) ? operation.getString(UML2Java.VISIBILITY) : "public") + " "
                    + (operation.has(UML2Java.IS_ABSTRACT) && (Boolean) operation.get(UML2Java.IS_ABSTRACT) ? "abstract " : "")
                    + (operation.getString(UML2Java.NAME).equals(operationType) && operationType.equals(className) ? "" : operationType + " ")
                    + operation.getString(UML2Java.NAME) + "(";

            if (operation.has(UML2Java.PARAMETERS)) {
                result += translateParameters(operation.getJSONArray(UML2Java.PARAMETERS));
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
        if (umlClass.getString(UML2Java.OBJECT_TYPE).equals("UMLInterface")) {
            type = "interface";
        }
        String result = "";
        result += "public " + (umlClass.has(UML2Java.IS_ABSTRACT) && (Boolean) umlClass.get(UML2Java.IS_ABSTRACT)
                ? "abstract " : "") + type + " " + umlClass.getString(UML2Java.NAME) + " ";

        if (umlClass.has("extends")) {
            result += "extends " + umlClass.getJSONObject("extends").getString(UML2Java.NAME) + " ";
        }

        if (umlClass.has(UML2Java.INTERFACES_REALIZED)) {
            result += "implements ";
            for (Object o : umlClass.getJSONArray(UML2Java.INTERFACES_REALIZED)) {
                result += ((JSONObject) o).getString(UML2Java.NAME) + ", ";
            }
            result = result.substring(0, result.length() - 2);

            result += " ";
        }

        result += "{\n";

        if (umlClass.has(UML2Java.ATTRIBUTES)) {
            result += translateAttributes(umlClass.getJSONArray(UML2Java.ATTRIBUTES));
        }

        if (umlClass.has(UML2Java.OPERATIONS)) {
            result += translateOperations(umlClass.getJSONArray(UML2Java.OPERATIONS), type, umlClass.getString(UML2Java.NAME));
        }

        return result + "}";
    }

    private String getOperationType(JSONObject operation) {
        if (operation.has(UML2Java.PARAMETERS)) {
            JSONArray parameters = operation.getJSONArray(UML2Java.PARAMETERS);
            for (int i = 0; i < parameters.length(); i++) {
                if (parameters.getJSONObject(i).has(UML2Java.DIRECTION)) {
                    String type;
                    try {
                        type = parameters.getJSONObject(i).getString(UML2Java.TYPE);
                    } catch (JSONException e) {
                        String reference = parameters.getJSONObject(i).getJSONObject(UML2Java.TYPE).getString(UML2Java.REF);
                        type = JSONDB.getInstance().getById(reference).getString(UML2Java.NAME);
                    }

                    parameters.remove(i);
                    return type;
                }
            }
        }
        return "void";
    }
}
