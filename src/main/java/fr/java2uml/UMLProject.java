package fr.java2uml;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UMLProject {

    UMLDiagram diagram;

    public UMLProject(UMLDiagram diagram){
        this.diagram = diagram;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject project = new JSONObject();
        project.put("_type", "Project");
        project.put("_id", "project_id");
        project.put("_name", "MyProject");
        JSONArray ownedElementsProject = new JSONArray();
        JSONObject model = new JSONObject();
        model.put("_type", "UMLModel");
        model.put("_id", "model_id");
        JSONObject parentModel = new JSONObject();
        parentModel.put("$ref", "project_id");
        model.put("_parent", parentModel);
        model.put("name", "MyModel");
        JSONArray ownedElementsModel = new JSONArray();
        ownedElementsModel.put(diagram.toJson());
        for(JSONObject UMLClass : diagram.getJsonClasses()){
            ownedElementsModel.put(UMLClass);
        }
        model.put("ownedElements",ownedElementsModel);
        ownedElementsProject.put(model);
        project.put("ownedElements", ownedElementsProject);
        return project;
    }

    @Override
    public String toString() {
        try {
            return toJson().toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
































