package fr.java2uml;

import fr.uml2java.*;
import org.json.JSONException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JavaAnalyser {
	private final List<File> files = new ArrayList<>();
	private final String outputFolder;
	private final File folderToAnalyse;
	private final IdGenerator idGenerator;

	public JavaAnalyser(String inputFolder, String outputFolder) throws IOException {
		this.outputFolder = outputFolder;
		folderToAnalyse = new File(inputFolder);
		idGenerator = IdGenerator.getInstance();
		startAnalyse();
	}

	public String getFileExtension(File file){
		String fileName = file.getName();
		for (int i = 0; i < fileName.length(); i++){
			if(fileName.charAt(i) == '.'){
				return fileName.substring(i);
			}
		}
		return "";
	}

	public void listFilesForFolder(File folder) {
		for (File fileEntry : Objects.requireNonNull(folder.listFiles())) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if(Objects.equals(getFileExtension(fileEntry), ".java")){
					files.add(fileEntry);
				}
			}
		}
	}

	public UMLClass detectClassName(String classString, UMLClass returnClass) {
		String[] keyWords = classString.split(" ");
		String step = "init";

		for (String word : keyWords) {
			if (word.length() == 0) {
				continue;
			}
			boolean breakAfter = false;
			if (word.equals("class")) {
				step = "name";
			} else if (word.equals("interface")) {
				step = "name";
				returnClass.setInterface(true);
			} else if (word.equals("abstract")) {
				returnClass.setAbstract();
			} else if (word.equals("implements")) {
				returnClass.setAbstract();
				step = "implements";
			} else if (word.equals("extends")) {
				returnClass.setAbstract();
				step = "extends";
			} else if (step.equals("name") && !word.equals("public") && !word.equals("private") && !word.equals("protected") && !word.equals("final")) {
				if (word.contains("{")) {
					breakAfter = true;
					word = word.replace("{", "");
				}
				returnClass.setName(word);
				step = "";
				if (breakAfter) {
					break;
				}
			} else if (step.equals("extends") && !word.equals("public") && !word.equals("private") && !word.equals("protected") && !word.equals("final")) {
				if (word.contains("{")) {
					breakAfter = true;
					word = word.replace("{", "");
				}
				returnClass.setExtendedClass(word);
				step = "";
				if (breakAfter) {
					break;
				}
			} else if (step.equals("implements") && !word.equals("public") && !word.equals("private") && !word.equals("protected") && !word.equals("final") && !word.equals(",")) {

				word = word.replace("{", "");
				String[] implementedClasses = word.split(",");
				List<String> filtredImplementedClasses = new ArrayList<>();
				for (String implementedClass : implementedClasses) {
					if (implementedClass.length() > 0)
						filtredImplementedClasses.add(implementedClass);
				}
				for (String implementedClass : filtredImplementedClasses) {
					returnClass.addImplementedClass(implementedClass);
				}
				break;
			}

		}
		return returnClass;
	}

	public UMLClass analyseOperation(String pieceOfCode, UMLClass returnClass) {
		UMLOperation newOperation = new UMLOperation();
		newOperation.setId(idGenerator.createId());
		newOperation.setMyClassId(returnClass.getId());
		boolean returnTypeBoolean = false;
		String[] keyWords = pieceOfCode.substring(0, pieceOfCode.indexOf("(")).split(" ");
		boolean allInfos = false;
		for (String word : keyWords) {
			if (!allInfos) {
				switch (word) {
				case "static":
					newOperation.setStatic(true);
					break;

				case "abstract":
					newOperation.setAbstract();
					break;

				case "public":
				case "private":
				case "protected":
					newOperation.setVisibility(word);
					break;
				default:
					if (!returnTypeBoolean) {
						if (word.equals(returnClass.getName())) {
							newOperation.setReturnType(word);
							newOperation.setName(word);
							allInfos = true;
						} else {
							returnTypeBoolean = true;
							newOperation.setReturnType(word);
						}

					} else {
						newOperation.setName(word);
						allInfos = true;
					}
					break;
				}
			} else {
				break;
			}

		}
		newOperation = analyseParameters(newOperation, pieceOfCode);
		returnClass.addOperation(newOperation);
		return returnClass;
	}

	public UMLOperation analyseParameters(UMLOperation operation, String operationString) {
		String parametersString = operationString.substring(operationString.indexOf("(") + 1,
				operationString.indexOf(")"));
		parametersString = parametersString.trim();
		String[] parameters = parametersString.split(" ");
		boolean returnType = false;
		UMLParameter newParameter = new UMLParameter();
		for (String word : parameters) {
			if (!returnType) {
				newParameter.setType(word);
				returnType = true;
			} else {
				newParameter.setName(word.replace(",", ""));
				newParameter.setId(idGenerator.createId());
				newParameter.setMyClassId(operation.getMyClassId());
				newParameter.setMyOperationId(operation.getId());

				operation.addParameter(newParameter);
				returnType = false;
				newParameter = new UMLParameter();
			}
		}
		return operation;
	}

	public UMLClass analyseAttribute(String pieceOfCode, UMLClass returnClass) {
		UMLAttribute newAttribute = new UMLAttribute();
		newAttribute.setId(idGenerator.createId());
		newAttribute.setMyClassId(returnClass.getId());
		String[] words = pieceOfCode.split(" ");
		boolean returnType = false;
		for (String word : words) {

			if (word.equals("static")) {
				newAttribute.setStatic(true);
			} else if (word.equals("private") || word.equals("public") || word.equals("protected")) {
				newAttribute.setVisibility(word);
			} else if (!returnType) {
				newAttribute.setType(word);
				returnType = true;
			} else {
				newAttribute.setName(word);
			}
		}
		returnClass.addAttribute(newAttribute);
		return returnClass;
	}

	public UMLClass analyseClassOperationsAndAttributes(String classString, UMLClass returnClass, int startIndex) {
		String pieceOfCodetoAnalyse = classString.substring(startIndex);
		int endOfPieceOfCodeToAnalyse = pieceOfCodetoAnalyse.length() - 1;
		int startOfNextPiece = pieceOfCodetoAnalyse.length() - 1;

		for (int i = 0; i < endOfPieceOfCodeToAnalyse; i++) {
			if (pieceOfCodetoAnalyse.charAt(i) == ';') {
				startOfNextPiece = startIndex + i + 1;
				if (pieceOfCodetoAnalyse.substring(0, i).contains("=")) {
					endOfPieceOfCodeToAnalyse = pieceOfCodetoAnalyse.substring(0, i).indexOf("=");
				} else {
					endOfPieceOfCodeToAnalyse = i;
				}
				break;
			} else if (pieceOfCodetoAnalyse.charAt(i) == '{') {
				endOfPieceOfCodeToAnalyse = i;
				int bracketsToClose = 1;
				for (int j = i + 1; j < pieceOfCodetoAnalyse.length(); j++) {
					if (pieceOfCodetoAnalyse.charAt(j) == '{') {
						bracketsToClose += 1;
					} else if (pieceOfCodetoAnalyse.charAt(j) == '}') {
						bracketsToClose -= 1;
					}
					if (bracketsToClose == 0) {
						startOfNextPiece = startIndex + j + 1;
						break;
					}
				}
				break;
			}
		}
		if (endOfPieceOfCodeToAnalyse == pieceOfCodetoAnalyse.length() - 1)
			return returnClass;
		pieceOfCodetoAnalyse = classString.substring(startIndex, startIndex + endOfPieceOfCodeToAnalyse);
		if (pieceOfCodetoAnalyse.contains("(")) {
			returnClass = analyseOperation(pieceOfCodetoAnalyse.trim(), returnClass);
		} else {
			returnClass = analyseAttribute(pieceOfCodetoAnalyse.trim(), returnClass);
		}

		return analyseClassOperationsAndAttributes(classString, returnClass, startOfNextPiece);
	}

	public UMLClass analyseClass(String classString, UMLClass returnClass) {
		int classbegining = 0;
		returnClass = detectClassName(classString, returnClass);
		while (classString.charAt(classbegining) != '{') {
			classbegining += 1;
		}
		classbegining += 1; // On ne souhaite pas considerer la première accolade

		return analyseClassOperationsAndAttributes(classString, returnClass, classbegining);
	}

	public String formatClassString(String classString) {
		classString = classString.replaceAll("\s+", " ").strip();
		classString = classString.replaceAll("\\s+>\\s+", "> ");
		classString = classString.replaceAll(">", "> ");
		classString = classString.replaceAll("\\s+>", "> ");
		classString = classString.replaceAll("\\s+<\\s+", "< ");
		classString = classString.replaceAll("<", "< ");
		classString = classString.replaceAll("\\s+<", "< ");
		classString = classString.replaceAll("<\\s+", "<");
		classString = classString.replaceAll("[(]", " ( ");
		classString = classString.replaceAll("[)]", " ) ");
		classString = classString.replaceAll("[{]", " { ");
		classString = classString.replaceAll("[}]", " } ");
		classString = classString.replaceAll("\s+[)]\s+", " ) ");
		classString = classString.replaceAll("\s+[(]\s+", " ( ");
		classString = classString.replaceAll("\s+[{]\s+", " { ");
		classString = classString.replaceAll("\s+[}]\s+", " } ");
		classString = classString.replaceAll("\s+", " ").strip();
		return classString;
	}

	public UMLDiagram analyseFiles() throws IOException {
		UMLDiagram newDiagram = new UMLDiagram();
		for (File file : files) {
			StringBuilder classString = new StringBuilder();
			UMLClass newClass = new UMLClass();
			newClass.setId(idGenerator.createId());
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			while (reader.ready()) {
				String line = reader.readLine();
				classString.append(line);
			}

			classString = new StringBuilder(formatClassString(classString.toString()));
			newClass = analyseClass(classString.toString(), newClass);
			List<UMLClass> newClasses = newDiagram.getMyClasses();
			newClasses.add(newClass);
			newDiagram.setMyClasses(newClasses);
		}
		detectAssociations(newDiagram);
		return newDiagram;
	}

	public void detectAssociations(UMLDiagram diagram) {
		for (UMLClass c : diagram.getMyClasses()) {
			List<UMLAttribute> attributesToRemove = new ArrayList<>();
			for (UMLAttribute a : c.getAttributes()) {
				if (diagram.getClassWithName(a.getType()) != null) {
					UMLAssociation newAssociation = new UMLAssociation();
					newAssociation.setMyClassId(c.getId());
					newAssociation.setId(idGenerator.createId());
					newAssociation.setName("Est compose de");
					UMLAssociationEnd end1 = new UMLAssociationEnd();
					UMLAssociationEnd end2 = new UMLAssociationEnd();
					end2.setId(idGenerator.createId());
					end2.setAssociatedClassId(c.getId());
					end1.setId(idGenerator.createId());
					end1.setAssociatedClassId(diagram.getClassWithName(a.getType()).getId());
					end1.setName(a.getName());
					end2.setAggregationType("shared");
					if (a.getType().contains("[]") || (a.getType().contains("<") && a.getType().contains(">"))) {
						end1.setMultiplicity("*");
					}
					else {
						end1.setMultiplicity("1");
					}
					newAssociation.setEnd1(end1);
					newAssociation.setEnd2(end2);
					c.addAssociation(newAssociation);
					attributesToRemove.add(a);
				}
			}
			for(UMLAttribute attribute : attributesToRemove) {
				c.removeAttribute(attribute);
			}
			
			for (UMLOperation o : c.getOperations()) {
				for (UMLParameter p : o.getUmlParameters()) {
					if (diagram.getClassWithName(p.getType()) != null) {
						boolean isAlsoAnAttribute = false;
						for (UMLAttribute a : c.getAttributes()) {
							if (Objects.equals(a.getType(), p.getType())) {
								isAlsoAnAttribute = true;
							}
						}
						if (!isAlsoAnAttribute) {
							UMLSourceTargetRelation newDependency = new UMLSourceTargetRelation();
							newDependency.setId(idGenerator.createId());
							newDependency.setSourceTargetType("UMLDependency");
							newDependency.setName("Utilise");
							newDependency.setSource(c.getId());
							newDependency.setTarget(diagram.getClassWithName(p.getType()).getId());
							c.addDependency(newDependency);
						}
					}
				}
			}
			if (!c.getExtendedClass().equals("")) {
				UMLSourceTargetRelation newDependency = new UMLSourceTargetRelation();
				newDependency.setId(idGenerator.createId());
				newDependency.setSourceTargetType("UMLGeneralization");
				newDependency.setName("Hérite de");
				newDependency.setSource(c.getId());
				newDependency.setTarget(diagram.getClassWithName(c.getExtendedClass()).getId());
				c.addDependency(newDependency);
			}

			
			for (String implementedClass : c.getImplementedClasses()) {
				UMLSourceTargetRelation newDependency = new UMLSourceTargetRelation();
				newDependency.setId(idGenerator.createId());
				newDependency.setSourceTargetType("UMLInterfaceRealization");
				newDependency.setName("Implémente");
				newDependency.setSource(c.getId());
				newDependency.setTarget(diagram.getClassWithName(implementedClass).getId());
				c.addDependency(newDependency);
			}

		}
	}

	public void startAnalyse() throws IOException, JSONException {
		listFilesForFolder(folderToAnalyse);
		UMLDiagram d = analyseFiles();
		generateJsonFile(d);
	}

	public void generateJsonFile(UMLDiagram diagram) throws JSONException {
		MdjGenerator j = new MdjGenerator();
		j.generateJsonFileFromDiagram(outputFolder, diagram);
	}





}
