package fr.uml2java;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UMLOperation extends UMLObject {

    private List<UMLParameter> umlParameters;
    private boolean isAbstract = false;
    private boolean isStatic = false;
    private String myClassId;
    private String returnType;
    private String visibility;

    public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public UMLOperation() {
        setUmlParameters(new ArrayList<>());
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        s = new StringBuilder(s.substring(0, s.length() - 2) + ((isAbstract) ? " isAbstract;" : "") + " with parameters :\n");
        for (UMLParameter umlParameter : umlParameters) {
            s.append("\t\t\t").append(umlParameter.toString());
        }
        return s.toString();
    }

    public void addParameter(UMLParameter umlParameter) {
        umlParameters.add(umlParameter);
    }
    
    public String getMyClassId() {
    	return myClassId;
    }

    public void setMyClassId(String myClassId) {
    	this.myClassId = myClassId;
    }
    
    public List<UMLParameter> getUmlParameters() {
        return umlParameters;
    }

    public void setUmlParameters(List<UMLParameter> umlParameters) {
        this.umlParameters = umlParameters;
    }

    public String getName() {
        return super.getName();
    }

    public String getId() {
        return super.getId();
    }

    public String getParent() {
        return super.getParent();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public void setId(String id) {
        super.setId(id);
    }

    public void setParent(String parent) {
        super.setParent(parent);
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract() {
        isAbstract = true;
    }

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

    public JSONObject toJson() throws JSONException {
        JSONObject operation = new JSONObject();
        operation.put("_type", "UMLOperation");
        operation.put("_id", getId());
        JSONObject parent = new JSONObject();
        parent.put("$ref", getMyClassId());
        operation.put("_parent", parent);
        operation.put("name", getName());
        operation.put("isStatic", isStatic());
        operation.put("isAbstract", isAbstract());
        operation.put("visibility", getVisibility());
        JSONArray parameters = new JSONArray();
        for(UMLParameter parameter : getUmlParameters()){
            parameters.put(parameter.toJson());
        }
        parameters.put(getJsonReturnParameter());
        operation.put("parameters", parameters);
        return operation;
    }

    public JSONObject getJsonReturnParameter() throws JSONException {
        JSONObject returnParameter = new JSONObject();
        returnParameter.put("_type", "UMLParameter");
        returnParameter.put("_id", idGenerator.createId());
        JSONObject parent = new JSONObject();
        parent.put("$ref", getId());
        returnParameter.put("_parent", parent);
        returnParameter.put("type", getReturnType());
        returnParameter.put("direction", "return");

        return returnParameter;

    }
}
