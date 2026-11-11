/*
 * Pas de copyright, ni de droit d'auteur.
 * DecompressionHuffman.java                 27/05/2024
 */
package fr.iutrodez.compresseurhuffman.huffman;

import fr.iutrodez.compresseurhuffman.outils.GestionFichier;
import fr.iutrodez.compresseurhuffman.objets.Noeud;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe permettant la décompression de fichiers
 * utilisant l'algorithme de Huffman.
 * Cette classe prend en charge le processus de décompression
 * d'un fichier compressé, en utilisant un arbre de Huffman
 * stocké séparément pour déterminer la table des codes
 * utilisée lors de la compression.
 * Le processus inclut la lecture d'un fichier binaire compressé,
 * la reconstruction de l'arbre de Huffman à partir d'un fichier texte externe,
 * et la transformation des données binaires compressées
 * en leur format d'origine.
 *
 * @author V. Munier--Genie
 * @author R. Xaviertaborda
 * @author J. Seychelles
 * @author B. Thenieres
 * @version 2.0
 */
public class DecompressionHuffman {

    /**
     * Chemin vers le fichier compressé qui doit être décompressé.
     */
    private String cheminFichierSource;

    /**
     * Chemin de destination où le fichier décompressé sera sauvegardé.
     */
    private String cheminFichierDestination;

    /**
     * Construit une instance de DecompressionHuffman avec
     * les chemins spécifiés pour le fichier source et destination.
     *
     * @param cheminFichierSource Le chemin vers le fichier
     *                            compressé à décompresser.
     * @param cheminFichierDestination Le chemin où le fichier
     *                                 décompressé sera sauvegardé.
     */
    public DecompressionHuffman(String cheminFichierSource,
                                String cheminFichierDestination) {
        this.cheminFichierSource = cheminFichierSource;
        this.cheminFichierDestination = cheminFichierDestination;
    }

    /**
     * Lance le processus de décompression du fichier spécifié.
     *
     * @throws IOException Si une erreur se produit pendant la décompression.
     */
    public void decompresserFichier() throws IOException {
        String cheminArbre = trouverCheminArbre();
        Noeud racine = preparerArbreHuffman(cheminArbre);
        decompresser(racine);
    }

    /**
     * Détermine le chemin vers le fichier de l'arbre Huffman basé
     * sur le chemin du fichier source. Compatible tous systèmes d'exploitation.
     *
     * @return Le chemin du fichier de l'arbre Huffman.
     * @throws IOException Si le fichier de l'arbre n'est pas trouvé.
     */
    private String trouverCheminArbre() throws IOException {
        File fichierSource = new File(cheminFichierSource);
        File dossierParent = fichierSource.getParentFile();
        File fichierArbre = (dossierParent != null)
                ? new File(dossierParent, "arbreHuffman.txt")
                : new File("arbreHuffman.txt");

        if (!fichierArbre.exists()) {
            throw new IOException("Erreur: fichier de l'arbre d'Huffman "
                    + "introuvable. Veuillez le placer dans le même "
                    + "répertoire que le fichier à décompresser avec le nom "
                    + "\"arbreHuffman.txt\"");
        }
        return fichierArbre.getPath();
    }

    /**
     * Prépare l'arbre de Huffman à partir d'un fichier donné.
     *
     * @param cheminArbre Le chemin du fichier contenant la description
     *                    de l'arbre de Huffman.
     * @return La racine de l'arbre de Huffman reconstruit.
     */
    private Noeud preparerArbreHuffman(String cheminArbre) {
        String[] arbreHuffman =
                GestionFichier.lireFichierArbreHuffman(cheminArbre);

        Map<Byte, String> codageHuffman =
                genererTableDeCodeHuffman(arbreHuffman);
        return construireArbreHuffmanDepuisMap(codageHuffman);
    }

    /**
     * Décompresse un fichier en utilisant l'arbre de Huffman.
     *
     * @param racine La racine de l'arbre de Huffman utilisé
     *               pour la décompression.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors
     *                     de la lecture ou de l'écriture des fichiers.
     */
    private void decompresser(Noeud racine) throws IOException {
        String code =
                GestionFichier.lireFichierBinaire(cheminFichierSource);

        byte[] bytes = transformerChaineEnBytes(code, racine);

        GestionFichier.ecrireFichierDestination(bytes,
                cheminFichierDestination);
    }

    /**
     * Génère une table de codage Huffman à partir des données
     * de l'arbre Huffman. Chaque ligne de l'arbre contient un
     * mappage entre un byte et son code Huffman correspondant.
     *
     * @param arbre Un tableau de chaînes représentant l'arbre Huffman.
     *              Chaque élément contient un byte et son code Huffman
     *              séparés par un point-virgule.
     * @return Une map où chaque clé est un byte et chaque valeur
     *         est le code Huffman correspondant.
     */
    private Map<Byte, String> genererTableDeCodeHuffman(String[] arbre) {
        Map<Byte, String> tableDeCodeHuffman = new HashMap<>();
        for (String line : arbre) {
            if (!line.isBlank()) {
                String[] separateur = line.split(";");

                byte cle = (byte) Integer.parseInt(separateur[1]
                                                   .split("=")[1]
                                                   .trim(), 2);

                String valeur = separateur[0].split("=")[1].trim();
                tableDeCodeHuffman.put(cle, valeur);
            }
        }
        return tableDeCodeHuffman;
    }

    /**
     * Construit un arbre de Huffman à partir d'une map de codes Huffman.
     * Chaque entrée de la map représente un caractère et son chemin binaire
     * correspondant dans l'arbre de Huffman.
     *
     * @param codeHuffmanMap Une map où chaque clé est un byte (caractère)
     *                       et chaque valeur est une chaîne de bits
     *                       représentant le chemin dans l'arbre de Huffman.
     * @return La racine de l'arbre de Huffman reconstruit.
     */
    private static Noeud construireArbreHuffmanDepuisMap(
            Map<Byte, String> codeHuffmanMap
    ) {
        Noeud racine = new Noeud();

        for (Byte caractere : codeHuffmanMap.keySet()) {
            String chemin = codeHuffmanMap.get(caractere);

            Noeud noeudCourant = racine;

            for (char direction : chemin.toCharArray()) {
                if (direction == '0') {
                    if (noeudCourant.getGauche() == null) {
                        noeudCourant.setGauche(new Noeud());
                    }
                    noeudCourant = noeudCourant.getGauche();
                } else if (direction == '1') {
                    if (noeudCourant.getDroite() == null) {
                        noeudCourant.setDroite(new Noeud());
                    }
                    noeudCourant = noeudCourant.getDroite();
                }
            }
            noeudCourant.setCaractere(caractere);
        }
        return racine;
    }

    /**
     * Décode une chaîne binaire en un tableau de bytes en utilisant
     * l'arbre de Huffman fourni.
     * Chaque chemin binaire dans la chaîne représente un caractère dans
     * l'arbre de Huffman.
     *
     * @param bits La chaîne binaire à décoder.
     * @param racine La racine de l'arbre de Huffman utilisé pour
     *               le décodage.
     * @return Un tableau de bytes représentant les caractères décodés.
     */
    private static byte[] transformerChaineEnBytes(String bits,
                                                   Noeud racine) {
        if (bits == null || bits.isEmpty() || racine == null) {
            return new byte[0];
        }

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        Noeud noeudCourant = racine;

        for (char bit : bits.toCharArray()) {
            noeudCourant = (bit == '0') ? noeudCourant.getGauche()
                    : noeudCourant.getDroite();

            if (noeudCourant != null && noeudCourant.isFeuille()) {
                byteStream.write(noeudCourant.getCaractere());
                noeudCourant = racine;
            }
        }

        return byteStream.toByteArray();
    }
}
