rootProject.name = "AliucordPlugins"

// Ce fichier définit les projets inclus. Chaque fois que vous ajoutez un nouveau projet, vous devez l'ajouter
// aux inclusions ci-dessous.

// Les plugins sont inclus comme ceci
include(
    "MyFirstPatch"
)

// Ceci est nécessaire car les plugins se trouvent dans le sous-répertoire ExamplePlugins/kotlin.
//
// En supposant que vous mettiez tous vos plugins à la racine du projet, donc au même
// niveau que ce fichier, supprimez simplement tout ce qui se trouve en dessous.
// Sinon, si vous voulez une structure différente, par exemple tous les plugins dans un dossier nommé "plugins",
// puis changez simplement le chemin
rootProject.children.forEach {
    // Remplacez kotlin par java si vous préférez utiliser java
    it.projectDir = file("${rootProject.name}/kotlin/${it.name}")
}
