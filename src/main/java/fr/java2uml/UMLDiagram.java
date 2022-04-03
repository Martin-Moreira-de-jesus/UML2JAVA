package fr.java2uml;

import fr.uml2java.UMLAssociation;
import fr.uml2java.UMLClass;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UMLDiagram {

	private List<UMLClass> myClasses = new ArrayList<>();

	public List<UMLClass> getMyClasses() {
		return myClasses;
	}

	public void setMyClasses(List<UMLClass> myClasses) {
		this.myClasses = myClasses;
	}
	
	public UMLClass getClassWithName(String className) {
		for(UMLClass c : getMyClasses()) {
			if(className.contains("[]")) {
				StringBuilder trueClassName = new StringBuilder();
				for(int i = 0; i < className.length(); i++) {
					if(Character.isLetter(className.charAt(i)) || Character.isDigit(className.charAt(i))) {
						trueClassName.append(className.charAt(i));
					}
				}
				return getClassWithName(trueClassName.toString());
			}
			else if(className.contains("<") && className.contains(">")){
				StringBuilder trueClassName = new StringBuilder();
				for(int i = className.indexOf('<'); i < className.indexOf('>'); i++) {
					if(Character.isLetter(className.charAt(i)) || Character.isDigit(className.charAt(i))) {
						trueClassName.append(className.charAt(i));
					}
				}
				return getClassWithName(trueClassName.toString());
			}
			else if(c.getName().equals(className)) {
				return c;
			}
		}
		return null;
	}


	public JSONObject toJson() throws JSONException {
		JSONObject diagram = new JSONObject();
		diagram.put("_type", "UMLClassDiagram");
		diagram.put("_id", "diagram_id");
		JSONObject parentDiagram = new JSONObject();
		parentDiagram.put("$ref", "model_id");
		diagram.put("_parent", parentDiagram);
		diagram.put("name", "MyDiagram");
		diagram.put("defaultDiagram", true);
		JSONArray ownedViewsDiagram = new JSONArray();
		for (UMLClass umlClass : getMyClasses()){
			ownedViewsDiagram.put(umlClass.toJsonView());
			for(UMLAssociation umlAssociation : umlClass.getAssociations()){
				ownedViewsDiagram.put(umlAssociation.toJsonView());
			}
			for(UMLSourceTargetRelation umlSourceTargetRelation : umlClass.getUmlSourceTargetRelations()){
				ownedViewsDiagram.put(umlSourceTargetRelation.toJsonView());
			}
		}
		diagram.put("ownedViews", ownedViewsDiagram);
		return diagram;
	}

	// pour le placement des classes
	public JSONObject toJson(JSONArray classes) throws JSONException {
		JSONObject diagram = new JSONObject();
		diagram.put("_type", "UMLClassDiagram");
		diagram.put("_id", "diagram_id");
		JSONObject parentDiagram = new JSONObject();
		parentDiagram.put("$ref", "model_id");
		diagram.put("_parent", parentDiagram);
		diagram.put("name", "MyDiagram");
		diagram.put("defaultDiagram", true);
		JSONArray ownedViewsDiagram = new JSONArray();
		for (UMLClass umlClass : getMyClasses()){
			ownedViewsDiagram.put(umlClass.toJsonView());
			for(UMLAssociation umlAssociation : umlClass.getAssociations()){
				ownedViewsDiagram.put(umlAssociation.toJsonView());
			}
			for(UMLSourceTargetRelation umlSourceTargetRelation : umlClass.getUmlSourceTargetRelations()){
				ownedViewsDiagram.put(umlSourceTargetRelation.toJsonView());
			}
		}
		diagram.put("ownedViews", classes);
		return diagram;
	}

	public List<JSONObject> getJsonClasses() throws JSONException {
		List<JSONObject> JsonClasses = new ArrayList<>();
		for(UMLClass umlClass : getMyClasses()){
			JsonClasses.add(umlClass.toJson());
		}
		return JsonClasses;
	}
}
