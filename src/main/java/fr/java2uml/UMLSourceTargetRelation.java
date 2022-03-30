package fr.java2uml;


import fr.uml2java.UMLObject;
import org.json.JSONException;
import org.json.JSONObject;

public class UMLSourceTargetRelation extends UMLObject {
    private String target;
	private String source;
	private String sourceTargetType = "";

    public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
    public String toString() {
        String s = super.toString();
        s = s.substring(0, s.length() - 2) + " with ends : \n";
        s += "\t\t\t" + target;
        s += "\t\t\t" + source;
        return s;
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

	public String getSourceTargetType() {
		return sourceTargetType;
	}

	public void setSourceTargetType(String sourceTargetType) {
		this.sourceTargetType = sourceTargetType;
	}

    public JSONObject toJsonView() throws JSONException {
        JSONObject sourceTargetRelationView = new JSONObject();
        sourceTargetRelationView.put("_type", getSourceTargetType()+"View");
        sourceTargetRelationView.put("_id", idGenerator.createId());
        JSONObject parent = new JSONObject();
        parent.put("$ref", "diagram_id");
        sourceTargetRelationView.put("_parent", parent);
        JSONObject model = new JSONObject();
        model.put("$ref", getId());
        sourceTargetRelationView.put("model", model);
        JSONObject head = new JSONObject();
        head.put("$ref", getTarget()+"view");
        sourceTargetRelationView.put("head", head);
        JSONObject tail = new JSONObject();
        tail.put("$ref", getSource()+"view");
        sourceTargetRelationView.put("tail", tail);
        sourceTargetRelationView.put("lineStyle", 1);
        sourceTargetRelationView.put("points", "178:325;248:423");
        sourceTargetRelationView.put("showVisibility", true);

        return sourceTargetRelationView;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject sourceTargetRelation = new JSONObject();
        sourceTargetRelation.put("_type", getSourceTargetType());
        sourceTargetRelation.put("_id", getId());
        JSONObject parent = new JSONObject();
        parent.put("$ref", getSource());
        sourceTargetRelation.put("_parent", parent);
        sourceTargetRelation.put("name", getName());
        JSONObject source = new JSONObject();
        source.put("$ref", getSource());
        sourceTargetRelation.put("source", source);
        JSONObject target = new JSONObject();
        target.put("$ref", getTarget());
        sourceTargetRelation.put("target", target);
        return sourceTargetRelation;

    }
}