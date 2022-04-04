
# UML2JAVA - Java2UML

Cet executable destiné à être éxécuté avec des fichiers .mdj utilisé par l'application de modélisation d'application [StarUML](https://staruml.io/). Il traduis des diagrammes de classes générés par ce dernier en code Java ainsi que l'inverse.

## Sommaire

* [Utilisation](#utilisation)
* [Téléchargement](#tlchargement)
* [Génération de code Java](#gnration-de-code-java-uml2java)
  * [Fonctionnalités StarUML supportées](#fonctionnalits-staruml-supportes)
  * [UMLPackage](#umlpackage)
  * [UMLClass](#umlclass)
  * [UMLInterface](#umlinterface)
  * [UMLTemplateParameter](#umltemplateparameter)
  * [UMLAttribute](#umlattribute)
  * [UMLOperation](#umloperation)
  * [UMLAssociation](#umlassociation)
  * [UMLAssociationClassLink](#umlassociationclasslink-classes-associatives)
  * [UMLDependency](#umldependency)
  * [UMLGeneralization](#umlgeneralization)
  * [UMLInterfaceRealization](#umlinterfacerealization)
* [Génération de diagramme UML](#génération-de-diagramme-uml-java2uml)
  * [Fonctionnalités Java supportées](#fonctionnalités-java-supportées)
  * [IdGenerator](#idgenerator)
  * [JavaAnalyser](#javaanalyser)
  * [MdjGenerator](#mdjgenerator)
  * [UMLDiagram](#umldiagram)
  * [UMLProject](#umlproject)
  * [UMLSourceTargetRelation](#umlsourcetargetrelation)

## Utilisation

### UML2Java

`java umlads.jar uml2java [source] [destination] [options]`

#### Options

* -sg
  * Ajoute les getters et setters (désactivé par défaut)
* -a <Type>
  * Changer le type des arrays (doit contenir <> à la fin)

#### Java2UML

`java umlads.jar uml2java [source] [destination]`

## Téléchargement

[Télécharger](https://github.com/Martin-Moreira-de-jesus/UML2JAVA/blob/main/target/umlads.jar?raw=true)

## Génération de code Java (UML2Java)

### Fonctionnalités StarUML supportées

* Class
* Interface
* Associations
* DirectedAssociation
* Aggregation
* Composition
* Dependency
* Generalization
* Interface Realization
* Package
* Template Parameters

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

### UMLInterface

* Convertie en interface java (fichier `.java`)
* Toutes les méthodes sont complétée par un point virgule
* Le reste est le même que pour une [UMLClass](#umlclass)

### UMLTemplateParameter

* Les type génériques seront ajoutés à au nom de la classe à la quelle ils ont été ajoutés mais il ne seront pas ajoutés
  lorsqu'on y fait référence ailleurs, par exemple si une autre classe à un objet du type de cette classe générique, les
  types génériques n'y apparaitront pas.

### UMLAttribute

* Convertis en variable de classe
* Propriété UML `visibility` convertie en visibilité de la variable : `public`, `protected`, `private`
* Propriété UML `isAbstract` convertie en mot-clé `abstract`
* Propriété `name` convertie en nom de la variable
* Propriété `type` convertie en type de la variable
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
  * `aggregation` car elles sont interprétées dans les [UMLAssociations](#umlassociation)

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

### UMLAssociation

* Convertie en premier temps en [UMLAttribute](#umlattribute) puis en variable de classe java
* Propriété UML `visibility` convertie en visibilité de l'attribut : `public`, `protected`, `private`
* Les `UMLAssociationEnd` sont converties en [UMLAttribute](#umlattribute) en fonction des données suivantes :
  * Propriété `name` convertie en nom de la variable
  * Propriété `type` convertie en type de la variable
  * Propriété `multiplicity` est simplement transférée à un [UMLAttribute](#umlattribute) qui sera traduit
  * Propriété `reference` utilisée pour déterminer à quelle classe l'`UMLAssociationEnd` fait référence

### UMLAssociationClassLink (classes associatives)

* Même procédé qu'une [UMLAssociation](#umlassociation) sauf que toutes les variables sont ajoutée à la classe associative

### UMLDependency

* Converties en un premier temps en [UMLOperation](#umloperation) puis en méthode de classe java
* Propriété UML `visibility` convertie en visibilité de l'attribut : `public`, `protected`, `private`
* Le nom de la méthode prendra le `name` de l'`UMLDependency` si il est spécifié, sinon la méthode s'appellera "`use` + le nom de la classe qui sera utilisée"
* Le type sera `void` et il y aura le nom de la classe utilisée en paramètre

### UMLGeneralization

* Convertie en héritage Java (`extends`)
* <span style="color: orange">Attention : </span>les erreurs ne sont pas gérée donc une interface peux hériter d'une classe sans erreurs

### UMLInterfaceRealization

* Convertie en implémentation d'interface (`implements`)
* <span style="color: orange">Attention : </span> comme pour l'[UMLGeneralization](#umlgeneralization) les erreurs n'y sont pas gérées

## Génération de diagramme UML (Java2UML)

Notons que dans cette partie nous nous servons également d'une partie des classes citées précédemment.

### Fonctionnalités Java supportées
* Classes java
* Classes abstraites
* Interfaces
* Variables de tout types privées, publiques, statiques ou abstraites
* Collections de tout types
* Constructeurs
* Fonctions de tout types de retour privées, publiques, statiques ou abstraites
* Paramètres de fonctions
* Héritages
* Aggrégations
* Dépendances
* Associations

### IdGenerator
* Classe singleton permettant la génération d'identifiants uniques UUID (immutable universally unique identifier) grâce à la méthode `createId()`

### JavaAnalyser
* Permet de générer un objet de type [UMLDiagram](#umldiagram) à partir d'un dossier contenant un projet java ou des fichiers java
* <span style="color: orange">Attention : </span> Cet analyseur ne permet pas de vérifier la syntaxte du code Java, il faudra veiller à fournir un code correct syntaxiquement
* <span style="color: orange">Attention : </span> L'analyse se limite à une classe par fichier java

### MdjGenerator
* Permet de générer un digramme StarUML à partir d'un objet de type UMLProject
* L'objet de type [UMLProject](#umlproject) sera instancié à partir d'un objet de type [UMLDiagram](#umldiagram) grâce à la méthode `generateUMLProject()`

### UMLDiagram
* Contient l'ensemble des classes du projet Java2UML dans `myClasses`
* Implémente une méthode `toJson()` permettant d'obtenir le format attendu par StarUML

### UMLProject
* Contient un objet [UMLDiagram](#umldiagram) nommé `diagram`
* Implémente une méthode `toJson()` permettant d'obtenir le format final lisible par StarUML

### UMLSourceTargetRelation
* Permet de manipuler toutes les relations UML incluant une source et une destination (dépendences, génralisations, réalisations d'interfaces)
* Le type de relation est précisé dans `sourceTargetType`
* `source` et `target` font référence aux indentifiants de la classe source et de la classe destination de la relation
* C'est la classe source qui hébergera la relation dans le fichier JSON du diagramme


