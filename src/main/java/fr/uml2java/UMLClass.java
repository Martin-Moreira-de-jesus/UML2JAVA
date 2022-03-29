package fr.uml2java;

import fr.java2uml.UMLSourceTargetRelation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UMLClass extends UMLObject {
    private boolean isAbstract = false;

    private boolean isInterface = false;

	private ArrayList<UMLAttribute> attributes;
    private ArrayList<UMLOperation> operations;
    private ArrayList<UMLAssociation> associations;
    private ArrayList<UMLSourceTargetRelation> umlSourceTargetRelations;

    private String extendedClass = "";
    private final ArrayList<String> implementedClasses = new ArrayList<>();

	@Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        s = new StringBuilder(s.substring(0, s.length() - 2) + ((isAbstract()) ? " isAbstract;" : "") + " contains :\n");
        s.append("\tattributes : \n");
        for (UMLAttribute umlAttribute : getAttributes()) {
            s.append("\t\t").append(umlAttribute.toString());
        }
        s.append("\toprations : \n");
        for (UMLOperation umlOperation : getOperations()) {
            s.append("\t\t").append(umlOperation.toString());
        }
        s.append("\towned elements : \n");
        for (UMLAssociation umlAssociation : getAssociations()) {
            s.append("\t\t").append(umlAssociation.toString());
        }
        return s.toString();
    }

    public UMLClass() {
        super();
        setAttributes(new ArrayList<>());
        setOperations(new ArrayList<>());
        setAssociations(new ArrayList<>());
        setUmlSourceTargetRelations(new ArrayList<>());
    }

    public ArrayList<UMLAttribute> getAttributes() {
        return attributes;
    }

    public ArrayList<UMLOperation> getOperations() {
        return operations;
    }

    public ArrayList<UMLAssociation> getAssociations() {
        return associations;
    }

    public void setAttributes(ArrayList<UMLAttribute> attributes) {
        this.attributes = attributes;
    }

    public void setOperations(ArrayList<UMLOperation> operations) {
        this.operations = operations;
    }

    public void setAssociations(ArrayList<UMLAssociation> associations) {
        this.associations = associations;
    }
    
    public ArrayList<UMLSourceTargetRelation> getUmlSourceTargetRelations() {
		return umlSourceTargetRelations;
	}

	public void setUmlSourceTargetRelations(ArrayList<UMLSourceTargetRelation> umlSourceTargetRelations) {
		this.umlSourceTargetRelations = umlSourceTargetRelations;
	}

    public boolean isInterface() {
		return isInterface;
	}

	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
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

    public void addAttribute(UMLAttribute umlAttribute) {
        attributes.add(umlAttribute);
    }

    public void removeAttribute(UMLAttribute umlAttribute) {
        attributes.remove(umlAttribute);
    }
    
    public void addOperation(UMLOperation umlOperation) {
        operations.add(umlOperation);
    }

    public void addAssociation(UMLAssociation umlAssociation) {
        associations.add(umlAssociation);
    }
    
    public void addDependency(UMLSourceTargetRelation newDependency) {
        umlSourceTargetRelations.add(newDependency);
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract() {
        isAbstract = true;
    }

	public String getExtendedClass() {
		return extendedClass;
	}

	public void setExtendedClass(String implementedClass) {
		this.extendedClass = implementedClass;
	}

	public ArrayList<String> getImplementedClasses() {
		return implementedClasses;
	}

	public void addImplementedClass(String implementedClass) {
		this.implementedClasses.add(implementedClass);
	}

    public JSONObject toJson() throws JSONException {
        JSONObject umlClass = new JSONObject();
        umlClass.put("_type", isInterface() ? "UMLInterface" : "UMLClass");
        umlClass.put("_id", getId());
        JSONObject parent = new JSONObject();
        parent.put("$ref", "model_id");
        umlClass.put("parent", parent);
        umlClass.put("name", getName());
        umlClass.put("isAbstract", isAbstract());
        JSONArray ownedElements = new JSONArray();
        for(UMLAssociation association : getAssociations()){
            ownedElements.put(association.toJson());
        }
        for(UMLSourceTargetRelation sourceTargetRelation : getUmlSourceTargetRelations()){
            ownedElements.put(sourceTargetRelation.toJson());
        }
        umlClass.put("ownedElements", ownedElements);
        JSONArray attributes = new JSONArray();
        for(UMLAttribute attribute : getAttributes()){
            attributes.put(attribute.toJson());
        }
        umlClass.put("attributes", attributes);
        JSONArray operations = new JSONArray();
        for(UMLOperation operation : getOperations()){
            operations.put(operation.toJson());
        }
        umlClass.put("operations", operations);
        return umlClass;
    }

    public JSONObject toJsonView() throws JSONException {
        JSONObject classView = new JSONObject();
        classView.put("_type", isInterface() ? "UMLInterfaceView" : "UMLClassView");
        classView.put("_id", getId()+"view");
        JSONObject parent = new JSONObject();
        parent.put("$ref", "diagram_id");
        classView.put("_parent", parent);
        JSONObject model = new JSONObject();
        model.put("$ref", getId());
        classView.put("model", model);
        classView.put("containerChangeable", true);
        classView.put("left", 248);
        classView.put("top", 320);
        classView.put("width", 152);
        classView.put("height", 71);
        return classView;
    }


}
