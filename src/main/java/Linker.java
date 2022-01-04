import org.json.JSONArray;
import org.json.JSONObject;

public class Linker {
    public void link(JSONDB database) {
        JSONArray links = database.getAllLinks();
        for (int i = 0; i < links.length(); ++i) {
            JSONObject link = (JSONObject) links.get(i);

            String linkType = link.getString("_type");
            if (linkType.equals("UMLGeneralization")) {
                JSONObject source = database.getById(link.getJSONObject("source").getString("$ref"));
                JSONObject mother = new JSONObject();
                mother.put("$ref", link.getJSONObject("target").getString("$ref"));
                mother.put("name", database.getById(mother.getString("$ref")).getString("name"));
                source.put("mother", mother);
                database.getDatabase().remove(i);
            }
            else if (linkType.equals("UMLDependency")) {

            }
        }
    }
}
