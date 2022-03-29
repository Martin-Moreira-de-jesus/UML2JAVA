package fr.uml2java;

import org.json.JSONException;
import org.json.JSONObject;

public class UMLParameter extends UMLAttribute {
    boolean isReturn;
    private String myOperationId;
   
    
	@Override
    public String toString() {
        String s = super.toString();
        s = s.substring(0, s.length() - 1);
        return s + " and " + (isReturn ? "is return type" : "is not return type") + '\n';
    }

    public boolean isReturn() {
        return isReturn;
    }

    public void setReturn(boolean isReturn) {
        this.isReturn = isReturn;
    }
    
    public String getMyOperationId() {
		return myOperationId;
	}

	public void setMyOperationId(String myOperationId) {
		this.myOperationId = myOperationId;
	}


    public String getType() {
        return super.getType();
    }

    public void setType(String type) {
        super.setType(type);
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

    public JSONObject toJson() throws JSONException {
        JSONObject parameter = new JSONObject();
        parameter.put("_type", "UMLParameter");
        parameter.put("_id", getId());
        JSONObject parent = new JSONObject();
        parent.put("$ref", getMyOperationId());
        parameter.put("_parent", parent);
        parameter.put("name", getName());
        parameter.put("type", getType());
        return parameter;
    }
}
