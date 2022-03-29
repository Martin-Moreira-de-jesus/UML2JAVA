package fr.uml2java;

public class UMLAssociationEnd extends UMLObject {
    private String visibility = "public";
    private String multiplicity = "";
    private String reference = "";
    private String associatedClassId = "";
    private String aggregationType = "none";




	@Override
    public String toString() {
        String s = super.toString();
        s = s.substring(0, s.length() - 2);
        return  s + " with visibility : "
                + getVisibility() + "; with multiplicity : " + getMultiplicity()
                + "; pointing to class with id : " + getReference() + '\n';
    }

    public String getAggregationType() {
		return aggregationType;
	}

	public void setAggregationType(String aggregationType) {
		this.aggregationType = aggregationType;
	}
	
    public String getAssociatedClassId() {
		return associatedClassId;
	}

	public void setAssociatedClassId(String associatedClassId) {
		this.associatedClassId = associatedClassId;
	}
	
    public String getVisibility() {
        return visibility;
    }

    public String getMultiplicity() {
        return multiplicity;
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

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setMultiplicity(String multiplicity) {
        this.multiplicity = multiplicity;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
