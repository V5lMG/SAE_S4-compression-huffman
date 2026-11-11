# Guide d'Utilisation

## 📋 Prérequis

- **Java JDK 21+** (JDK 23 recommandé et testé)
- **Maven** ou le wrapper fourni `./mvnw` / `mvnw.cmd`

Vérifiez votre environnement :
```powershell
java -version
```

---

## 🛠 Compilation et Tests

Pour compiler le projet et lancer les tests unitaires :
```powershell
# Définir JAVA_HOME si non configuré par défaut
$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"

# Compiler et tester
.\mvnw.cmd test

# Créer le package JAR exécutable
.\mvnw.cmd clean package
```

---

## 🚀 Lancement de l'Application

### 1. Mode Menu Interactif

Si aucun argument n'est fourni, l'application démarre son menu interactif console :

```powershell
java -cp target/classes fr.iutrodez.compresseurhuffman.ApplicationLigneCommande
```

Options disponibles :
1. **Compresser un fichier** : Saisir le chemin source et le dossier de destination.
2. **Décompresser un fichier** : Saisir le chemin du `.bin` (l'`arbreHuffman.txt` doit se trouver dans le même dossier).
3. **Créer uniquement l'arbre de Huffman** : Générer l'`arbreHuffman.txt` sans compresser les données.
4. **Quitter l'application**.

---

### 2. Mode Ligne de Commande (CLI)

Vous pouvez exécuter directement des commandes avec des arguments :

#### A. Compresser un fichier
```powershell
java -cp target/classes fr.iutrodez.compresseurhuffman.ApplicationLigneCommande compresser <chemin_source> <dossier_destination> <nom_base>
```
*Exemple :*
```powershell
java -cp target/classes fr.iutrodez.compresseurhuffman.ApplicationLigneCommande compresser "C:\data\texte.txt" "C:\data\output" "archive"
```
Génère :
- `C:\data\output\archive.bin`
- `C:\data\output\arbreHuffman.txt`

#### B. Décompresser un fichier
```powershell
java -cp target/classes fr.iutrodez.compresseurhuffman.ApplicationLigneCommande decompresser <chemin_archive_bin> <dossier_destination> <nom_fichier_restaure>
```
*Exemple :*
```powershell
java -cp target/classes fr.iutrodez.compresseurhuffman.ApplicationLigneCommande decompresser "C:\data\output\archive.bin" "C:\data\restaure" "texte_restaure.txt"
```

#### C. Générer l'arbre seul
```powershell
java -cp target/classes fr.iutrodez.compresseurhuffman.ApplicationLigneCommande construire <chemin_source> <dossier_destination> <nom_base>
```

#### D. Obtenir l'aide
```powershell
java -cp target/classes fr.iutrodez.compresseurhuffman.ApplicationLigneCommande help
```
