import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.crypto.Data;

public class JSONDB {
    private String name;
    public JSONArray db = new JSONArray();
    public static JSONDB DATABASE;

    private JSONArray getAllClasses(JSONArray classes) {
        JSONArray umlClasses = new JSONArray(); // return variable with all the UMLClasses

        for (int i = 0; i < classes.length(); ++i) {
            JSONObject umlObject = classes.getJSONObject(i); // either a class or a package
            if (umlObject.getString("_type").matches("UMLClass|UMLPackage")) {
                // if its a package, we add all the classes to the list of classes
                if (umlObject.getString("_type").equals("UMLPackage")) {
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

    public JSONDB(JSONObject jsonObject) {
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

    public  JSONArray getDatabase() {
        return db;
    }

    public JSONArray getAllLinks() {
        JSONArray links = new JSONArray();
        for (Object dbObject : db) {
            if (!((JSONObject) dbObject).getString("_type").equals("UMLClass")) {
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
            if (this.db.getJSONObject(i).getString("_type").matches("UMLClass|UMLInterface")) {
                result.put(this.db.getJSONObject(i));
            }
        }

        return result;
    }
}
