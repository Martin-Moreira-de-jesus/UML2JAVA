package fr.java2uml;

import org.json.JSONArray;
import org.json.JSONException;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MdjGenerator {

	UMLProject UMLProject;

	public MdjGenerator() {}

	public void generateUMLProject(UMLDiagram diagram) throws JSONException {
		UMLProject = new UMLProject(diagram);
	}

	public void generateJsonFileFromDiagram(String path, UMLDiagram diagram) throws JSONException {

		UMLOrganiser organiser = new UMLOrganiser();
		JSONArray classes = organiser.organizeClasses(diagram);
		generateUMLProject(diagram);

		generateUMLProject(diagram);
		try {
			File file = new File(path + "/Diagram.mdj");
			if (file.createNewFile()) {
				System.out.println("Fichier créé: " + file.getName());
			} else {
				System.out.println("Le fichier existe déjà.");
			}
			FileWriter writer = new FileWriter(path + "/Diagram.mdj");
			writer.write(UMLProject.toString(classes));
			writer.close();
			Desktop.getDesktop().open(file);
			Desktop.getDesktop().open(file.getParentFile());

		} catch (IOException e) {
			System.out.println("Erreur.");
			e.printStackTrace();
		}
	}

}
