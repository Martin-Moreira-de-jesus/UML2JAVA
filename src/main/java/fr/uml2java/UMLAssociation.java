package fr.uml2java;


import org.json.JSONException;
import org.json.JSONObject;

public class UMLAssociation extends UMLObject {
    private UMLAssociationEnd end1;
    private UMLAssociationEnd end2;
    private String myClassId;

	@Override
    public String toString() {
        String s = super.toString();
        s = s.substring(0, s.length() - 2) + " with ends : \n";
        s += "\t\t\t" + end1.toString();
        s += "\t\t\t" + end2.toString();
        return s;
    }

    public String getMyClassId() {
        return myClassId;
    }

    public void setMyClassId(String myClassId) {
        this.myClassId = myClassId;
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

    public UMLAssociationEnd getEnd1() {
        return end1;
    }

    public void setEnd1(UMLAssociationEnd end1) {
        this.end1 = end1;
    }

    public UMLAssociationEnd getEnd2() {
        return end2;
    }

    public void setEnd2(UMLAssociationEnd end2) {
        this.end2 = end2;
    }

    public JSONObject toJsonView() throws JSONException {
        JSONObject associationView = new JSONObject();
        associationView.put("_type", "UMLAssociationView");
        associationView.put("_id", idGenerator.createId());
        JSONObject parent = new JSONObject();
        parent.put("$ref", "diagram_id");
        associationView.put("_parent", parent);
        JSONObject model = new JSONObject();
        model.put("$ref", getId());
        associationView.put("model", model);
        JSONObject head = new JSONObject();
        head.put("$ref", getEnd2().getAssociatedClassId()+"view");
        associationView.put("head", head);
        JSONObject tail = new JSONObject();
        tail.put("$ref", getEnd1().getAssociatedClassId()+"view");
        associationView.put("tail", tail);
        associationView.put("lineStyle", 1);
        associationView.put("points", "178:325;248:423");
        associationView.put("showVisibility", true);

        return associationView;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject association = new JSONObject();
        association.put("_type", "UMLAssociation");
        association.put("_id", getId());
        JSONObject parent = new JSONObject();
        parent.put("$ref", getMyClassId());
        association.put("parent", parent);
        association.put("name", getName());
        JSONObject end1 = new JSONObject();
        end1.put("_type", "UMLAssociationEnd");
        end1.put("_id", getEnd1().getId());
        JSONObject end1Parent = new JSONObject();
        end1Parent.put("$ref", getId());
        end1.put("_parent", end1Parent);
        end1.put("name", getEnd1().getName());
        JSONObject end1Reference = new JSONObject();
        end1Reference.put("$ref", getEnd1().getAssociatedClassId());
        end1.put("reference", end1Reference);
        end1.put("multiplicity", getEnd1().getMultiplicity());
        association.put("end1", end1);

        JSONObject end2 = new JSONObject();
        end2.put("_type", "UMLAssociationEnd");
        end2.put("_id", getEnd2().getId());
        JSONObject end2Parent = new JSONObject();
        end2Parent.put("$ref", getId());
        end2.put("_parent", end2Parent);
        end2.put("name", getEnd2().getName());
        JSONObject end2Reference = new JSONObject();
        end2Reference.put("$ref", getEnd2().getAssociatedClassId());
        end2.put("reference", end2Reference);
        end2.put("aggregation", getEnd2().getAggregationType());
        association.put("end2", end2);
        return association;
    }
}
