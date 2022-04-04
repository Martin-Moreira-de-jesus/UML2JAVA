package fr.umlads.uml2java.db;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONDB {
    private String name;
    private JSONArray db = new JSONArray();
    private static JSONDB DATABASE;

    public static void init(JSONObject jsonObject) {
        DATABASE = new JSONDB(jsonObject);
    }

    private JSONArray getAllClasses(JSONArray classes) {
        JSONArray umlClasses = new JSONArray(); // return variable with all the UMLClasses

        for (int i = 0; i < classes.length(); ++i) {
            JSONObject umlObject = classes.getJSONObject(i); // either a class or a package
            if (umlObject.getString(UML2Java.OBJECT_TYPE).matches("UMLClass|UMLPackage|UMLInterface")) {
                // if it's a package, we add all the classes to the list of classes
                if (umlObject.getString(UML2Java.OBJECT_TYPE).equals("UMLPackage")) {
                    JSONArray packageClasses = umlObject.getJSONArray("ownedElements");
                    // add package name to all of them
                    for (int k = 0; k < packageClasses.length(); ++k) {
                        packageClasses.getJSONObject(k).put("_package", umlObject.getString("name"));
                    }
                    umlClasses.putAll(umlObject.getJSONArray("ownedElements"));
                } else {
                    umlObject.put("_package", "null");
                    umlClasses.put(umlObject);
                }
            }
        }
        return umlClasses;
    }

    public static JSONObject getObjectWith(String key, String value) {
        for (Object object : DATABASE.db) {
            JSONObject jsonObject = (JSONObject) object;
            if (jsonObject.has(key) && jsonObject.getString(key).equals(value)) {
                return jsonObject;
            }
        }
        return null;
    }

    private JSONDB(JSONObject jsonObject) {
        JSONArray classes = new JSONArray();

        JSONArray models = jsonObject.getJSONArray("ownedElements");

        for (int i = 0; i < models.length(); i++) {
            classes = this.getAllClasses(models.getJSONObject(i).getJSONArray("ownedElements"));
        }

        // Separates associations from classes
        for (int i = 0; i < classes.length(); ++i) {
            JSONObject umlClass = classes.getJSONObject(i);
            if (umlClass.has("ownedElements")) {
                JSONArray associations = umlClass.getJSONArray("ownedElements");
                for (Object association : associations) {
                    classes.put((JSONObject) association);
                }
            }
            umlClass.remove("ownedElements");
        }
        this.db.putAll(classes);
    }

    public JSONArray getAllLinks() {
        JSONArray links = new JSONArray();
        for (Object dbObject : db) {
            if (!((JSONObject) dbObject).getString(UML2Java.OBJECT_TYPE).matches("UMLClass|UMLInterface")) {
                links.put((JSONObject) dbObject);
            }
        }
        return links;
    }

    public JSONObject getById(String id) {
        for (Object object : db) {
            if (((JSONObject) object).getString("_id").equals(id)) {
                return (JSONObject) object;
            }
        }
        return null;
    }

    public JSONArray getByType(String type) {
        JSONArray jsonArray = new JSONArray();
        for (Object object : db) {
            if (((JSONObject) object).getString("_id").equals(type)) {
                jsonArray.put((JSONObject) object);
            }
        }
        return jsonArray;
    }

    public void remove(JSONObject object) {
        for (int i = 0; i < this.db.length(); i++) {
            if (this.db.getJSONObject(i) == object) {
                this.db.remove(i);
            }
        }
    }

    public JSONArray fetchClasses() {
        JSONArray result = new JSONArray();

        for (int i = 0; i < this.db.length(); ++i) {
            if (this.db.getJSONObject(i).getString(UML2Java.OBJECT_TYPE).matches("UMLClass|UMLInterface")) {
                result.put(this.db.getJSONObject(i));
            }
        }

        return result;
    }

    public static JSONDB getInstance() {
        return DATABASE;
    }
}
