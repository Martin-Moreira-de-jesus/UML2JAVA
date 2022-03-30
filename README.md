# UML2JAVA - Java2UML

Cet executable destiné à être éxécuté avec des fichiers .mdj utilisé par l'application de modélisation d'application [StarUML](https://staruml.io/). Il traduis des diagrammes de classes générés par ce dernier en code Java ainsi que l'inverse.

## Sommaire


## Génération de code Java (UML2Java)

### UMLPackage

* Convertit en package java (en fichiers)
* Si il n'y a pas de package, le nom de package par défaut `com.company` sera choisi

### UMLClass

* Convertie en classe java (fichier `.java`)
* Propriété UML `visibility` convertie en visibilité de la classe : `public`, `protected`, `private`
* Propriété UML `isAbstract` convertier en mot-clé `abstract`
* No default constructors are generated
* No documentation
* Les accesseurs et modifieurs sont générés (si précisé dans la commande) pour chaque attribut
  * Non généré pour des attribut dont la `visibility` est publique
  * Si l'attribut est précisé `isReadOnly` seul l'accesseur est créé
  * La méthode d'accès prends le nom de l'attribut avec un `get` ou `set` placé devant et la première lettre capitalisé
* <span style="color: orange">Attention : </span>Nous ne vérifions pas la validité du diagramme, si vous créez une classe 
qui hérite d'une interface par exemple, cet héritage sera interprété 

### UMLAttribute

* Convertis en variable de classe
* Propriété UML `visibility` convertie en visibilité de la variable : `public`, `protected`, `private`
* Propriété UML `isAbstract` convertie en mot-clé `abstract`
* Propriété `name` convertie en nom de la variable
* Propriété `type` convertier en type de la variable
* Propriété `multiplicity` interprétée et modifiée en type Java Array en fonction de celui-ci :
  * Si elle contient une *, n, ou qu'elle est supérieur à 2 le type de la variable deviens de type Java Array ( type + [] )
  * Sinon, son type reste le même
  * Par défaut la `multiplicity` est à 1
* Propriété non interprétée :
  * `isLeaf`
  * `isOrdered`
  * `isUnique`
  * `defaultValue`
  * `isDerived`
  * `isID`
  * `aggregation` car elles sont interprétées dans les [UMLAssociations](#UMLAssociations)

### UMLOperation

* Convertie en méthode java
* Propriété UML `visibility` convertie en visibilité de la méthode : `public`, `protected`, `private`
* Propriété UML `isAbstract` convertie en mot-clé `abstract`
* Propriété `name` convertie en nom de la méthode
* `UMLParameter` converti en type de la méthode et en paramètres de la méthode
* Le `UMLParameter` avec l'attribut `direction` de valeur `return` deviens le type de la méthode, si il n'y en a pas, le type est void
* Les autres `UMLParameter` deviennent les paramètres de la fonction :
  * `name` devient le nom du paramètre
  * `type` devient le type du paramètre
  * Les paramètres son séparés par des virgules et un espace
* Si la méthode est abstraire on y mets un point virgule à la fin, sinon on rempli son corps avec `// TODO`

### UMLInterface

* Convertie en interface java (fichier `.java`)
* 