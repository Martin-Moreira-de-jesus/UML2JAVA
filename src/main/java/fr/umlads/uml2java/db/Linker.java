package fr.umlads.uml2java.db;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class Linker {
    public void link() {
        JSONArray links = JSONDB.getInstance().getAllLinks();

        for (int i = 0; i < links.length(); ++i) {
            JSONObject link = (JSONObject) links.get(i);
            JSONObject source = null;
            JSONObject target = null;

            if (!link.getString(UML2Java.OBJECT_TYPE).matches("UMLAssociation|UMLAssociationClassLink")) {
                source = JSONDB.getInstance().getById(link.getJSONObject("source").getString("$ref"));
                target = JSONDB.getInstance().getById(link.getJSONObject("target").getString("$ref"));
            }

            String linkType = link.getString(UML2Java.OBJECT_TYPE);

            switch (linkType) {
                case "UMLGeneralization" -> {
                    assert source != null;
                    generateGeneralizationRelationship(source, target);
                }
                case "UMLDependency" -> generateDependencyRelationship(source, target, link);
                case "UMLInterfaceRealization" -> {
                    assert source != null;
                    generateInterfaceRealRelationship(source, target);
                }
                case "UMLAssociation" -> generateAssociationRelationship(link);
            }

            JSONDB.getInstance().remove(link);
        }
    }

    private JSONObject generateAttributeFromEnd(JSONObject end) {
        JSONObject attribute = new JSONObject();

        if (end.has("visibility")) {
            attribute.put("visibility", end.getString("visibility"));
        } else {
            attribute.put("visibility", "private");
        }

        if (end.has("multiplicity")) {
            attribute.put("multiplicity", end.getString("multiplicity"));
        } else {
            attribute.put("multiplicity", "1");
        }

        if (end.has("name")) {
            attribute.put("name", end.getString("name"));
        } else {
            String name = JSONDB.getInstance().getById(end.getJSONObject("reference").
                    getString("$ref")).getString("name");
            name = name.substring(0, 1).toLowerCase(Locale.ROOT) + name.substring(1);
            attribute.put("name", name);
        }
        String type = JSONDB.getInstance().getById(end.getJSONObject("reference").
                getString("$ref")).getString("name");

        String multiplicity = end.has("multiplicity") ? end.getString("multiplicity") : "";

        if (multiplicity.contains("*") || multiplicity.contains("n") || multiplicity.matches("[2-9]")) {
            type += "[]";
        }

        attribute.put("type", type);

        return attribute;
    }

    private void addAttribute(JSONObject end1, JSONObject end2) {
        String end1ID = end1.getJSONObject("reference").getString("$ref");
        String end2ID = end2.getJSONObject("reference").getString("$ref");

        // boolean end1Navigable = end1.has("name") && !end1.getString("name").equals("") && (!end1.has("navigable") || !end1.getBoolean("navigable"));
        // boolean end2Navigable = end2.has("name") && !end2.getString("name").equals("") && (!end2.has("navigable") || !end2.getBoolean("navigable"));

        boolean end1Navigable =  !end1.has("navigable") || !end1.getString("navigable").equals("false");
        boolean end2Navigable = !end2.has("navigable") || !end2.getString("navigable").equals("false");

        JSONObject source = JSONDB.getInstance().getById(end1ID);
        JSONObject target = JSONDB.getInstance().getById(end2ID);

        if (!target.has("attributes")) {
            target.put("attributes", new JSONArray());
        }

        if (!source.has("attributes")) {
            source.put("attributes", new JSONArray());
        }

        JSONObject targetAttribute = null;
        JSONObject sourceAttribute = null;

        if (source.getString("_id").equals(end1ID)) {
            if (end1Navigable) {
                targetAttribute = generateAttributeFromEnd(end1);
            }
            if (end2Navigable) {
                sourceAttribute = generateAttributeFromEnd(end2);
            }
        } else {
            if (end2Navigable) {
                sourceAttribute = generateAttributeFromEnd(end2);
            }
            if (end1Navigable) {
                targetAttribute = generateAttributeFromEnd(end1);
            }
        }

        if (targetAttribute != null) target.getJSONArray("attributes").put(targetAttribute);
        if (sourceAttribute != null) source.getJSONArray("attributes").put(sourceAttribute);
    }

    private void generateAssociationRelationship(JSONObject link) {
        JSONObject classLink = JSONDB.getObjectWith(UML2Java.OBJECT_TYPE, "UMLAssociationClassLink");
        if (classLink != null
                && classLink.getJSONObject("associationSide").getString("$ref").equals(link.getString("_id"))) {
            addAttributeToAssociativeClass(link, classLink);
        } else {
            JSONObject end1 = link.getJSONObject("end1");
            JSONObject end2 = link.getJSONObject("end2");
            addAttribute(end1, end2);
        }
    }

    private void addAttributeToAssociativeClass(JSONObject link, JSONObject classLink) {
        JSONObject UMLClass = JSONDB.getObjectWith("_id", classLink.getJSONObject("classSide").getString("$ref"));
        JSONObject end1 = link.getJSONObject("end1");
        JSONObject end2 = link.getJSONObject("end2");

        JSONObject attribute1 = generateAttributeFromEnd(end1);
        JSONObject attribute2 = generateAttributeFromEnd(end2);

        if (!UMLClass.has("attributes")) {
            UMLClass.put("attributes", new JSONArray());
        }

        UMLClass.getJSONArray("attributes").put(attribute1);
        UMLClass.getJSONArray("attributes").put(attribute2);
    }

    private void generateGeneralizationRelationship(JSONObject source, JSONObject target) {
        JSONObject motherClass = new JSONObject();

        motherClass.put("name", target.getString("name"));
        motherClass.put("$ref", target.getString("_id"));

        source.put("extends", motherClass);

        if (!target.has("operations")) return; // exiting method if there's no abstract operations to add

        if (!source.has("operations")) {
            source.put("operations", new JSONArray());
        }

        JSONArray targetOperations = target.getJSONArray("operations"); // abstract operations of the mother
        for (int i = 0; i < targetOperations.length(); ++i) {
            if (targetOperations.getJSONObject(i).has("isAbstract")) {
                source.getJSONArray("operations").put(targetOperations.getJSONObject(i));
            }
        }
    }

    private void generateInterfaceRealRelationship(JSONObject source, JSONObject target) {
        JSONObject interfaceReal = new JSONObject();
        if (!source.has("interfacesRealized")) {
            source.put("interfacesRealized", new JSONArray());
        }

        interfaceReal.put("name", target.getString("name"));
        interfaceReal.put("$ref", target.getString("_id"));

        source.getJSONArray("interfacesRealized").put(interfaceReal);

        if (!source.has("operations")) {
            source.put("operations", new JSONArray());
        }
        if (target.has("operations")) {
            JSONArray operations = new JSONArray(target.getJSONArray("operations").toString());

            for (int i = 0; i < operations.length(); i++) {
                operations.getJSONObject(i).put("keyword", "Override");
            }

            source.getJSONArray("operations").putAll(operations);
        }
    }

    private void generateDependencyRelationship(JSONObject source, JSONObject target, JSONObject dependency) {
        JSONObject operation = new JSONObject(); // operation built with the target object to put in the source object

        if (dependency.has("name")) {
            operation.put("name", dependency.getString("name"));
        } else {
            String operationName = target.getString("name");
            operationName = "use" + operationName.substring(0, 1).toUpperCase() + operationName.substring(1);
            operation.put("name", operationName);
        }

        JSONObject parameter = new JSONObject();

        parameter.put("type", target.getString("name"));
        String paramName = target.getString("name");
        paramName = paramName.substring(0, 1).toLowerCase() + paramName.substring(1);
        parameter.put("name", paramName);

        operation.put("parameters", new JSONArray());
        operation.getJSONArray("parameters").put(parameter);

        if (!source.has("operations")) {
            source.put("operations", new JSONArray());
        }

        source.getJSONArray("operations").put(operation);
    }
}
