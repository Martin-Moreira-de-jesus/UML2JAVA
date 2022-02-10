package fr.umlads.uml2java;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class Linker {
    public void link() {
        JSONArray links = JSONDB.DATABASE.getAllLinks();

        for (int i = 0; i < links.length(); ++i) {
            JSONObject link = (JSONObject) links.get(i);
            JSONObject source;
            JSONObject target;
            if (!link.getString("_type").equals("UMLAssociation")) {
                source = JSONDB.DATABASE.getById(link.getJSONObject("source").getString("$ref"));
                target = JSONDB.DATABASE.getById(link.getJSONObject("target").getString("$ref"));
            } else {
                source = link.getJSONObject("end1");
                target = link.getJSONObject("end2");
            }
            String linkType = link.getString("_type");

            switch (linkType) {
                case "UMLGeneralization" -> generateGeneralizationRelationship(source, target);
                case "UMLDependency" -> generateDependencyRelationship(source, target, link);
                case "UMLInterfaceRealization" -> generateInterfaceRealRelationship(source, target);
                case "UMLAssociation" -> generateAssociationRelationship(source, target);
            }

            JSONDB.DATABASE.remove(link);
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
            String name = JSONDB.DATABASE.getById(end.getJSONObject("reference").
                    getString("$ref")).getString("name");
            name = name.substring(0, 1).toLowerCase(Locale.ROOT) + name.substring(1);
            attribute.put("name", name);
        }
        String type = JSONDB.DATABASE.getById(end.getJSONObject("reference").
                getString("$ref")).getString("name");

        if (attribute.getString("multiplicity").contains("*") || attribute.getString("multiplicity").matches("[2-9]")) {
            type += "[]";
        }

        attribute.put("type", type);

        return attribute;
    }

    private void addAttribute(JSONObject end1, JSONObject end2) {
        String end1ID = end1.getJSONObject("reference").getString("$ref");
        String end2ID = end2.getJSONObject("reference").getString("$ref");

        boolean end1Navigable = end1.has("name") && !end1.getString("name").equals("") && (!end1.has("navigable") || !end1.getBoolean("navigable"));
        boolean end2Navigable = end2.has("name") && !end2.getString("name").equals("") && (!end2.has("navigable") || !end2.getBoolean("navigable"));

        JSONObject source = JSONDB.DATABASE.getById(end1ID);
        JSONObject target = JSONDB.DATABASE.getById(end2ID);

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

    private void generateAssociationRelationship(JSONObject end1, JSONObject end2) {
        addAttribute(end1, end2);
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
            operationName = operationName.substring(0, 1).toLowerCase() + operationName.substring(1);
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
