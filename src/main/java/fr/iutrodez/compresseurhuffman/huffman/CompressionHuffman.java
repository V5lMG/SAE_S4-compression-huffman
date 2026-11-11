/*
 * Pas de copyright, ni de droit d'auteur.
 * CompressionHuffman.java                   27/05/2024
 */
package fr.iutrodez.compresseurhuffman.huffman;

import fr.iutrodez.compresseurhuffman.outils.GestionFichier;
import fr.iutrodez.compresseurhuffman.objets.Noeud;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.PriorityQueue;

import static java.lang.System.out;

/**
 * Classe de compression de fichiers utilisant l'algorithme de Huffman.
 * Cette classe permet de lire un fichier source,
 * de compter la fréquence des caractères, de construire l'arbre de Huffman,
 * et de compresser le fichier en une chaîne de bits
 * selon le codage de Huffman.
 * Le résultat est écrit dans un fichier de destination
 * avec les codes Huffman utilisés.
 * Un résumé des fréquences de caractère et
 * des codes de Huffman est également produit.
 *
 * @author V. Munier--Genie
 * @author R. Xaviertaborda
 * @author J. Seychelles
 * @author B. Thenieres
 * @version 2.0
 */
public class CompressionHuffman {

    /**
     * Chemin du fichier source à compresser.
     */
    private String cheminFichierSource;

    /**
     * Chemin du fichier destination où sera sauvegardé le fichier compressé.
     */
    private String cheminFichierDestination;

    /**
     * Constructeur qui initialise les chemins du fichier source
     * et du fichier destination.
     * Ces chemins sont utilisés pour la lecture du fichier à compresser
     * et l'écriture du fichier compressé.
     *
     * @param cheminFichierSource Chemin complet du fichier à compresser.
     * @param cheminFichierDestination Chemin complet où le fichier compressé
     *                                 sera enregistré.
     */
    public CompressionHuffman(String cheminFichierSource,
                              String cheminFichierDestination) {

        this.cheminFichierSource = cheminFichierSource;
        this.cheminFichierDestination = cheminFichierDestination;
    }

    /**
     * Compresse un fichier source en utilisant l'algorithme de Huffman
     * et écrit le résultat dans un fichier de destination.
     * <br>
     * La méthode réalise la compression en plusieurs étapes :
     * 1. Extraction des octets du fichier source.
     * 2. Analyse des fréquences des octets pour construire le codage Huffman.
     * 3. Conversion des octets en chaîne de bits Huffman.
     * 4. Ecriture du résultat compressé et du tableau de codage Huffman.
     *
     * @throws IOException Si une erreur d'entrée/sortie se produit
     *                     pendant les opérations de lecture ou d'écriture.
     */
    public void compresserFichier() throws IOException {
        byte[] octets = GestionFichier.recupererOctets(cheminFichierSource);

        if (octets.length == 0) {
            GestionFichier.ecrireChaineBinaireDansFichier("", cheminFichierDestination);
            String cheminArbreHuffman = determinerCheminArbre(cheminFichierDestination);
            GestionFichier.ecrireArbreHuffman(cheminArbreHuffman, new String[0]);
            return;
        }

        Map<Byte, Integer> occurencesDesCaracteres = preparerDonnees(octets);
        Map<Byte, String> codageHuffman =
                genererCodesHuffman(occurencesDesCaracteres);

        String codage = convertirOctetsEnCodeHuffman(octets, codageHuffman);
        ecrireResultats(codage, cheminFichierDestination, codageHuffman);
    }

    /**
     * Prépare les données en calculant et triant les occurrences
     * des octets dans la chaîne.
     *
     * @param octets Un tableau d'octets représentant les données à analyser.
     * @return Une map contenant les occurrences des octets.
     */
    private Map<Byte, Integer> preparerDonnees(byte[] octets) {
        Map<Byte, Integer> occurencesDesCaracteres =
                getFrequenceDesOctets(octets);

        afficherOccurencesTriees(occurencesDesCaracteres);
        return occurencesDesCaracteres;
    }

    /**
     * Calcule et renvoie la fréquence de chaque byte dans un tableau d'octets.
     * Cette méthode utilise une HashMap pour stocker
     * et compter la fréquence de chaque byte rencontré.
     *
     * @param octets Le tableau d'octets dont les fréquences
     *              doivent être calculées.
     * @return Une HashMap où chaque clé est un byte et chaque valeur
     *         est la fréquence de ce byte dans le tableau.
     */
    public HashMap<Byte, Integer> getFrequenceDesOctets(byte[] octets) {
        HashMap<Byte, Integer> frequenceDesOctets = new HashMap<>();
        for (byte octet : octets) {
            frequenceDesOctets.put(octet,
                    frequenceDesOctets.getOrDefault(octet, 0) + 1);
        }
        return frequenceDesOctets;
    }

    /**
     * Compresse les données en construisant un arbre
     * de Huffman à partir des occurrences des caractères
     * et génère les codes de Huffman correspondants.
     *
     * @param occurencesDesCaracteres Une map contenant les occurrences
     *                                des octets.
     * @return Une map contenant les octets et
     *         leurs codes de Huffman correspondants.
     */
    public Map<Byte, String> genererCodesHuffman(
            Map<Byte, Integer> occurencesDesCaracteres
    ) {
        Noeud racine = construireArbreAvecOccurences(occurencesDesCaracteres);
        Map<Byte, String> codes = new HashMap<>();
        if (racine != null) {
            recursiveCodeHuffman(racine, "", codes);
        }
        return codes;
    }

    /**
     * Construit l'arbre de Huffman à partir des occurrences des caractères
     * et retourne la racine de l'arbre.
     *
     * @param occurencesDesCaracteres Une map contenant les occurrences
     *                                des octets.
     * @return La racine de l'arbre de Huffman.
     */
    public Noeud construireArbreAvecOccurences(
            Map<Byte, Integer> occurencesDesCaracteres
    ) {
        Noeud[] tableau = new Noeud[occurencesDesCaracteres.size()];
        int index = 0;
        for (Byte key : occurencesDesCaracteres.keySet()) {
            Integer value = occurencesDesCaracteres.get(key);
            tableau[index++] = new Noeud(key, value);
        }
        return construireArbre(tableau);
    }

    /**
     * Génère les codes de Huffman de manière récursive à partir
     * d'un nœud donné et les stocke dans une map.
     *
     * @param noeud  Le nœud actuel de l'arbre de Huffman.
     * @param chemin Le chemin de bits (code) jusqu'au nœud actuel.
     * @param codes  La map qui stocke les caractères et leurs codes
     *               de Huffman correspondants.
     */
    private void recursiveCodeHuffman(Noeud noeud, String chemin,
                                      Map<Byte, String> codes) {

        if (noeud == null) {
            return;
        }
        if (noeud.isFeuille()) {
            // Dans le cas d'un symbole unique, le chemin peut être initialement vide : on lui donne "0"
            codes.put(noeud.getCaractere(), chemin.isEmpty() ? "0" : chemin);
        } else {
            if (noeud.getGauche() != null) {
                recursiveCodeHuffman(noeud.getGauche(),
                                     chemin + "0", codes);
            }
            if (noeud.getDroite() != null) {
                recursiveCodeHuffman(noeud.getDroite(),
                                     chemin + "1", codes);
            }
        }
    }

    /**
     * Détermine le chemin du fichier d'arbre Huffman associé au fichier de destination.
     * Compatible avec tous les systèmes de fichiers (multiplateforme).
     *
     * @param cheminFichierDestination Chemin du fichier de destination.
     * @return Le chemin complet vers arbreHuffman.txt.
     */
    private static String determinerCheminArbre(String cheminFichierDestination) {
        File fichierDest = new File(cheminFichierDestination);
        File dossierParent = fichierDest.getParentFile();
        return (dossierParent != null)
                ? new File(dossierParent, "arbreHuffman.txt").getPath()
                : "arbreHuffman.txt";
    }

    /**
     * Écrit les résultats de la compression,
     * incluant le codage binaire et l'arbre de Huffman,
     * dans les fichiers de destination.
     *
     * @param codage La chaîne de bits résultante de la compression.
     * @param cheminFichierDestination Le chemin du fichier où écrire
     *                                 le codage binaire compressé.
     * @param codageHuffman La map des octets et leurs codes
     *                      de Huffman correspondants.
     * @throws IOException Si une erreur d'entrée/sortie se produit
     *                     pendant l'écriture des fichiers.
     */
    private void ecrireResultats(String codage,
                                 String cheminFichierDestination,
                                 Map<Byte, String> codageHuffman)
            throws IOException {

        GestionFichier.ecrireChaineBinaireDansFichier(codage,
                                                      cheminFichierDestination);

        String cheminArbreHuffman = determinerCheminArbre(cheminFichierDestination);
        ecrireArbreHuffmanTrie(cheminArbreHuffman, codageHuffman);
    }

    /**
     * Affiche les occurrences des caractères triées
     * par ordre décroissant de fréquence.
     *
     * @param occurences La map des occurrences à trier.
     */
    private void afficherOccurencesTriees(Map<Byte, Integer> occurences) {
        out.println("Occurrences des caractères :");

        int totalOccurrences = 0;
        for (int occurence : occurences.values()) {
            totalOccurrences += occurence;
        }

        String[][] tableauOccurences = new String[occurences.size()][3];

        int i = 0;
        for (Byte indice : occurences.keySet()) {
            int occurrences = occurences.get(indice);
            double frequence = (double) occurrences / totalOccurrences;
            char symbole = (char) indice.byteValue();
            tableauOccurences[i][0] = String.valueOf(symbole);
            tableauOccurences[i][1] = String.valueOf(occurrences);
            tableauOccurences[i][2] = String.format("%.6f", frequence);
            i++;
        }

        Arrays.sort(tableauOccurences,
                (a, b) -> Double.compare(
                        Double.parseDouble(b[2].replace(',', '.')),
                        Double.parseDouble(a[2].replace(',', '.'))
                ));

        for (String[] donneeAAfficher : tableauOccurences) {
            out.printf("Caractère : %s ; Occurrences : %s ; Fréquence : %s\n",
                    donneeAAfficher[0], donneeAAfficher[1], donneeAAfficher[2]);
        }
    }

    /**
     * Encode une chaîne d'octets en utilisant
     * un mappage spécifié de codes Huffman.
     * La méthode parcourt chaque byte du tableau
     * et utilise le mappage de Huffman pour convertir chaque byte
     * en sa représentation de chaîne de bits correspondante.
     * Ces chaînes de bits sont ensuite concaténées pour former
     * une seule chaîne binaire.
     *
     * @param chaineOctets Le tableau d'octets à encoder.
     * @param mappageHuffman Le mappage de chaque byte à sa chaîne
     *                      de code Huffman correspondante.
     * @return La chaîne binaire complète résultante de l'encodage de tous
     *         les octets de la chaîne initiale.
     */
    public String convertirOctetsEnCodeHuffman(byte[] chaineOctets,
                                               Map<Byte, String> mappageHuffman) {

        StringBuilder codeHuffman = new StringBuilder();
        for (byte octet : chaineOctets) {
            codeHuffman.append(mappageHuffman.get(octet));
        }
        return codeHuffman.toString();
    }

    /**
     * Construit un arbre de Huffman à partir d'une liste de nœuds en utilisant
     * une file de priorité (PriorityQueue), garantissant une complexité optimale en O(N log N).
     * Les deux nœuds avec les plus petites fréquences sont extraits et combinés
     * jusqu'à ce qu'il ne reste qu'un seul nœud racine.
     *
     * @param listeDeNoeuds Tableau initial de nœuds, où chaque nœud contient
     *                     un caractère (ou byte) et sa fréquence.
     * @return La racine de l'arbre de Huffman,
     *         représentant la hiérarchie complète des fusions.
     */
    private Noeud construireArbre(Noeud[] listeDeNoeuds) {
        if (listeDeNoeuds == null || listeDeNoeuds.length == 0) {
            return null;
        }

        // Cas limite : un seul symbole dans le fichier
        if (listeDeNoeuds.length == 1) {
            Noeud racineUnique = new Noeud(listeDeNoeuds[0].getFrequence());
            racineUnique.setGauche(listeDeNoeuds[0]);
            return racineUnique;
        }

        PriorityQueue<Noeud> filePriorite = new PriorityQueue<>();
        Collections.addAll(filePriorite, listeDeNoeuds);

        while (filePriorite.size() > 1) {
            Noeud premierNoeud = filePriorite.poll();
            Noeud deuxiemeNoeud = filePriorite.poll();

            Noeud parent = new Noeud(premierNoeud.getFrequence()
                                     + deuxiemeNoeud.getFrequence());

            parent.setGauche(premierNoeud);
            parent.setDroite(deuxiemeNoeud);

            filePriorite.add(parent);
        }
        return filePriorite.poll();
    }

    /**
     * Écrit l'arbre de Huffman trié dans un fichier texte.
     *
     * @param cheminFichierDestination Le chemin du fichier où écrire l'arbre.
     * @param codageHuffman La map des codes Huffman à écrire.
     */
    private void ecrireArbreHuffmanTrie(String cheminFichierDestination,
                                        Map<Byte, String> codageHuffman) {

        List<Byte> clesTriees = new ArrayList<>(codageHuffman.keySet());

        clesTriees.sort((bits1, bits2) -> comparerCodesHuffman(codageHuffman,
                                                               bits1,
                                                               bits2));

        String[] tableHuffmanTrie = new String[clesTriees.size()];

        int indexeDesCle = 0;
        for (Byte cle : clesTriees) {
            String valeur = codageHuffman.get(cle);
            String encode = getUTF8String(cle);
            tableHuffmanTrie[indexeDesCle++] = "codeHuffman = " + valeur + " ; "
                                             + "encode = "      + encode + " ; "
                                             + "symbole = " + (char) (byte) cle;
        }

        GestionFichier.ecrireArbreHuffman(cheminFichierDestination,
                                          tableHuffmanTrie);
    }

    /**
     * Compare deux bytes en fonction de leurs codes Huffman.
     *
     * @param bits1 Le premier byte à comparer.
     * @param bits2 Le deuxième byte à comparer.
     * @return Un entier négatif, zéro ou un entier positif
     *         si le premier argument est inférieur,
     *         égal ou supérieur au deuxième.
     */
    private int comparerCodesHuffman(Map<Byte, String> codageHuffman,
                                     Byte bits1, Byte bits2) {
        String code1 = codageHuffman.get(bits1);
        String code2 = codageHuffman.get(bits2);
        int compareLength = Integer.compare(code1.length(), code2.length());
        if (compareLength == 0) {
            return code1.compareTo(code2);
        } else {
            return compareLength;
        }
    }

    /**
     * Récupère la représentation UTF-8 en chaîne de caractères d'un byte.
     *
     * @param value Le byte à convertir.
     * @return La représentation UTF-8 en chaîne de caractères.
     */
    private String getUTF8String(byte value) {
        return String.format("%8s", Integer.toBinaryString(value & 0xFF))
                     .replace(' ', '0');
    }

    /**
     * Méthode pour générer et enregistrer l'arbre de Huffman en
     * utilisant les données du fichier source et en écrivant
     * l'arbre dans le fichier destination.
     *
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void genererEtEnregistrerArbreHuffman() throws IOException {
        byte[] octets = GestionFichier.recupererOctets(cheminFichierSource);

        if (octets.length == 0) {
            GestionFichier.ecrireArbreHuffman(cheminFichierDestination, new String[0]);
            return;
        }

        Map<Byte, Integer> occurencesDesCaracteres =
                getFrequenceDesOctets(octets);

        Map<Byte, String> codageHuffman =
                genererCodesHuffman(occurencesDesCaracteres);

        ecrireArbreHuffmanTrie(cheminFichierDestination, codageHuffman);
    }
}
