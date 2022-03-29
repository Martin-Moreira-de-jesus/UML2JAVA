package fr.uml2java;

import org.json.JSONException;
import org.json.JSONObject;

public class UMLAttribute extends UMLObject {
    private String type;
    private String myClassId;
    private String visibility;
    private boolean isStatic;



	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	@Override
    public String toString() {
        String s = super.toString();
        s = s.substring(0, s.length() - 2);
        return s + " type : "  + getType() + '\n';
    }

    public UMLAttribute() {
        super();
    }

    public String getMyClassId() {
    	return myClassId;
    }

    public void setMyClassId(String myClassId) {
    	this.myClassId = myClassId;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
    
    public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

    public JSONObject toJson() throws JSONException {
        JSONObject attribute = new JSONObject();
        attribute.put("_type", "UMLAttribute");
        attribute.put("_id", getId());
        JSONObject parent = new JSONObject();
        parent.put("$ref", getMyClassId());
        attribute.put("_parent", parent);
        attribute.put("name", getName());
        attribute.put("visibility", getVisibility());
        attribute.put("isStatic", isStatic());
        attribute.put("type", getType());
        return attribute;
    }
}
