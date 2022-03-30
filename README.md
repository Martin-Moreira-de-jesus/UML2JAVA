# UML2JAVA - Java2UML

Cet executable destiné à être éxécuté avec des fichiers .mdj utilisé par l'application de modélisation d'application [StarUML](https://staruml.io/). Il traduis des diagrammes de classes générés par ce dernier en code Java ainsi que l'inverse.

## Sommaire


## Génération de code Java (UML2Java)

### UMLPackage

*Convertit en package java (en fichier)

### UMLClass

*Convertie en classe java (fichier .java)
*propriété UML `visibility` convertie en visibilité de la classe : `public`, `protected`, `private`
*propriété UML `isAbstract` convertier en mot-clé `abstract`
*No default constructors are generated
*No documentation
*Les accesseurs et modifieurs sont générés (si précisé dans la commande) pour chaque attribut
  *Non généré pour des attribut dont la `visibility` est publique
  *Si l'attribut est précisé `isReadOnly` seul l'accesseur est créé
  *La méthode d'accès prends le nom de l'attribut 
