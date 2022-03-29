package fr.uml2java;

import fr.java2uml.IdGenerator;

public class UMLObject {
    private String name = "";
    private String id = "";
    private String parent = "";
    private boolean derived;
    private boolean _static;
    protected IdGenerator idGenerator = IdGenerator.getInstance();

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "; name : " + name + "; id : " + id
                + "; parent : " + parent + ((derived) ? " derived;" : "") + ((_static) ? " static;" : "") + "; \n";
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getParent() {
        return parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isDerived() {
        return derived;
    }

    public void setDerived() {
        this.derived = true;
    }

    public boolean is_static() {
        return _static;
    }

    public void setStatic() {
        this._static = true;
    }
}
