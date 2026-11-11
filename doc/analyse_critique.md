# Analyse Critique & Axes d'Amélioration

Ce document présente une revue technique approfondie du code source du projet **Compresseur Huffman**, détaillant ses points forts, ses limites actuelles et des pistes d'amélioration concrètes.

---

## 🌟 1. Points Forts

1. **Véritable encodage binaire par décalage de bits**
   - Contrairement à de nombreuses implémentations pédagogiques qui se contentent d'écrire des `'0'` et `'1'` sous forme de caractères texte ASCII (ce qui agrandit le fichier au lieu de le compresser), ce projet utilise de véritables opérations bit-à-bit (`1 << (7 - (i % 8))`) et sauvegarde la longueur exacte en bits dans les 4 premiers octets (`DataOutputStream.writeInt`).
2. **Support universel au niveau de l'octet (`byte`)**
   - En travaillant sur des `byte` plutôt que sur des `char` ou des `String`, l'algorithme est intrinsèquement compatible avec n'importe quel type de fichier (images, exécutables, archives, texte UTF-8), et pas seulement des fichiers texte ASCII.
3. **Double mode d'interaction bien pensé**
   - Présence d'un menu console interactif très clair pour les débutants, complété par une interface en ligne de commande (CLI) scriptable (`compresser`, `decompresser`, `construire`).
4. **Qualité de la documentation de code**
   - Javadoc très complète, rigoureuse et homogène sur les classes et méthodes publiques.

---

## ⚠️ 2. Faiblesses et Dettes Techniques

### 2.1 Portabilité des chemins système (Hardcoded `\\`)
Dans `CompressionHuffman.java` et `DecompressionHuffman.java` :
```java
int dernierSeparateur = cheminFichierDestination.lastIndexOf("\\");
```
- **Problème** : L'anti-slash `\` est spécifique à Windows. Sur Linux, macOS ou si un utilisateur passe un chemin avec des slashes `/`, `lastIndexOf("\\")` renvoie `-1`. Cela provoque un crash `StringIndexOutOfBoundsException` ou une écriture erronée.
- **Solution recommandée** : Utiliser `java.nio.file.Path` (`Path.of(chemin).getParent()`) ou `File.separator`.

---

### 2.2 Consommation mémoire excessive (Passage par des chaînes de bits)
Dans `convertirOctetsEnCodeHuffman` et `lireFichierBinaire` :
```java
StringBuilder codeHuffman = new StringBuilder();
for (byte octet : chaineOctets) {
    codeHuffman.append(mappageHuffman.get(octet));
}
```
- **Problème** : Pour un fichier source de 50 Mo, cette chaîne contiendra environ 300 à 400 millions de caractères `char`. Sachant qu'un `char` Java prend 2 octets en mémoire, le `StringBuilder` consommera **600 à 800 Mo de RAM**, risquant un `OutOfMemoryError` sur des fichiers volumineux.
- **Solution recommandée** : Écrire et lire au fil de l'eau (streaming) via un `BitOutputStream` et `BitInputStream` personnalisés, sans jamais stocker l'intégralité des bits sous forme de `String` en mémoire.

---

### 2.3 Complexité algorithmique de la construction de l'arbre
Dans `construireArbre` :
```java
while (noeuds.size() > 1) {
    noeuds.sort(Comparator.comparingInt(Noeud::getFrequence));
    Noeud premierNoeud = noeuds.remove(0);
    Noeud deuxiemeNoeud = noeuds.remove(0);
    ...
    noeuds.add(parent);
}
```
- **Problème** : À chaque fusion, la liste complète est retriée via `noeuds.sort(...)`.
  - Trier une liste de $N$ éléments prend $O(N \log N)$.
  - Fait $N$ fois, la complexité totale devient **$O(N^2 \log N)$**, avec des `remove(0)` coûteux sur une `ArrayList` ($O(N)$ pour décaler les éléments).
- **Solution recommandée** : Utiliser une file de priorité `PriorityQueue<Noeud>`. L'extraction des deux minimums et l'insertion du parent se font alors en $O(\log N)$, ramenant la complexité totale à **$O(N \log N)$**.

---

### 2.4 Fichier d'arbre séparé (`arbreHuffman.txt`)
- **Problème** : L'arbre est enregistré dans un fichier texte tiers nommé en dur `arbreHuffman.txt` dans le même dossier. Si ce fichier est déplacé, renommé ou si plusieurs fichiers sont compressés dans le même dossier, les arbres s'écrasent ou la décompression échoue.
- **Solution recommandée** : Intégrer la table des fréquences ou l'arbre canonique sérialisé directement dans l'en-tête du fichier compressé `.bin`. Ainsi, chaque fichier `.bin` est totalement autonome (comme le font `gzip` ou `zip`).

---

### 2.5 Cas limites non gérés (Fichier vide ou symbole unique)
- **Fichier vide** (0 octet) : La table est vide, `noeuds.get(0)` lance `IndexOutOfBoundsException`.
- **Fichier à symbole unique** (ex: un fichier contenant seulement "AAAA") : L'arbre ne possède qu'une racine ou un seul nœud, le code généré est une chaîne vide `""`, rendant la compression et décompression impossibles sans gestion spéciale.

---

### 2.6 Configuration Maven & Exécution des Tests
- Dans le `pom.xml`, le plugin `maven-surefire-plugin` n'est pas déclaré avec une version récente. Maven utilise donc par défaut la version 2.12.4 (2012), incapable de détecter automatiquement les classes de test JUnit 5 package-private (comme `TestGestionFichier`). Seul `TestNoeud` était exécuté lors d'un `mvn test`.
- Déclarer `maven-surefire-plugin` en version 3.2.5+ règle immédiatement le problème.

---

## 💡 3. Synthèse et Note Globale

| Critère | Évaluation | Commentaire |
| :--- | :---: | :--- |
| **Algorithme & Logique** | 🟢 8/10 | Respect parfait des principes de Huffman et vrai codage binaire. |
| **Qualité du code & Style** | 🟢 8.5/10 | Excellente lisibilité, convention Java respectée, Javadoc exemplaire. |
| **Architecture & Modularité** | 🟡 7/10 | Bonne séparation, mais couplage rigide sur les chemins Windows et le fichier d'arbre séparé. |
| **Performance & Scalabilité** | 🟠 5.5/10 | Tri répété et passage par de volumineux `StringBuilder` en mémoire limitant la taille des fichiers traitables. |
| **Couverture de tests** | 🟡 6.5/10 | Bons tests unitaires existants mais mal exécutés à cause du plugin Maven. |
