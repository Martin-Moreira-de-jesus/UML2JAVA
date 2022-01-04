import org.json.JSONArray;
import org.json.JSONObject;

public class JSONDB {
    private String name;
    private JSONArray database = new JSONArray();

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
                    umlClasses.put(umlObject);
                }
            }
        }
        return classes;
    }

    public JSONDB(JSONObject jsonObject) {
        JSONArray classes = new JSONArray();

        JSONArray models = jsonObject.getJSONArray("ownedElements");
        for (int i = 0; i < models.length(); ++i) {

            JSONArray modelContents = models.getJSONObject(i).getJSONArray("ownedElements");
            for (int j = 0; j < modelContents.length(); ++j) {
                JSONObject content = modelContents.getJSONObject(j);
                if (content.getString("_type").matches("UMLClass|UMLPackage")) {
                    classes.put(content);
                }
            }
        }

        for (int i = 0; i < classes.length(); ++i) {
            JSONObject umlClass = classes.getJSONObject(i);
            if (umlClass.has("ownedElements")) {
                JSONArray associations = umlClass.getJSONArray("ownedElements");
                for (Object association : associations) {
                    this.database.put((JSONObject) association);
                }
            }
            umlClass.remove("ownedElements");
        }
        this.database.putAll(classes);
    }

    public JSONArray getDatabase() {
        return database;
    }

    public JSONArray getAllLinks() {
        JSONArray links = new JSONArray();
        for (Object dbObject : database) {
            if (!((JSONObject) dbObject).getString("_type").equals("UMLClass")) {
                 links.put((JSONObject) dbObject);
            }
        }
        return links;
    }

    public JSONObject getById(String id) {
        for (Object object : database) {
            if (((JSONObject) object).getString("_id").equals(id)) {
                return (JSONObject) object;
            }
        }
        return null;
    }

    public JSONArray getByType(String type) {
        JSONArray jsonArray = new JSONArray();
        for (Object object : database) {
            if (((JSONObject) object).getString("_id").equals(type)) {
                jsonArray.put((JSONObject) object);
            }
        }
        return jsonArray;
    }
}
