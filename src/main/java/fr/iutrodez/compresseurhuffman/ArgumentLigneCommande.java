/*
 * Pas de copyright, ni de droit d'auteur.
 * ArgumentLigneCommande.java                01/06/2024
 */
package fr.iutrodez.compresseurhuffman;

import fr.iutrodez.compresseurhuffman.huffman.CompressionHuffman;
import fr.iutrodez.compresseurhuffman.huffman.DecompressionHuffman;
import fr.iutrodez.compresseurhuffman.outils.GestionPrompt;
import fr.iutrodez.compresseurhuffman.outils.StatistiquesCompresseur;

import java.io.File;
import java.io.IOException;

import static java.lang.System.out;

/**
 * Classe destinée à gérer et interpréter les arguments de ligne de commande
 * pour l'application de compression Huffman.
 *
 * @author V. Munier--Genie
 * @author R. Xaviertaborda
 * @author J. Seychelles
 * @author B. Thenieres
 * @version 2.0
 */
public class ArgumentLigneCommande {

    /**
     * Traite les arguments de ligne de commande fournis à l'application.
     * Détermine l'action appropriée basée sur le premier argument
     * et appelle les méthodes concernées.
     *
     * @param args les arguments de ligne de commande
     *             passés à l'application principale.
     */
    public static void gererArgumentsEnLigneCommande(String[] args) {

        String commande = args[0].toLowerCase();
        ApplicationLigneCommande.afficherSeparateur();

        switch (commande) {
            case "compresser":
                if (args.length < 4) {
                    out.println("Arguments manquants. Utilisation : "
                                + "compresser <chemin_fichier_a_compresser> "
                                + "<nom_dossier_destination> <nom_fichier>");
                } else {
                    compresserFichier(args[1], args[2], args[3]);
                }
                break;

            case "decompresser":
                if (args.length < 4) {
                    out.println("Arguments manquants. Utilisation : "
                                + "decompresser <chemin_fichier_a_decompresser>"
                                + " <nom_dossier_destination> <nom_fichier>");
                } else {
                    decompresserFichier(args[1], args[2], args[3]);
                }
                break;

            case "construire":
                if (args.length < 4) {
                    out.println("Arguments manquants. Utilisation : "
                                + "construire <chemin_fichier_a_decompresser>"
                                + " <nom_dossier_destination> <nom_fichier>");
                } else {
                    genererArbreHuffman(args[1], args[2], args[3]);
                }
                break;

            case "help":
                afficherAide();
                break;

            default:
                out.println("\n\nCommande invalide. Utilisez 'help' pour obtenir "
                            + "des informations sur l'utilisation.\n\n");
                break;
        }
        ApplicationLigneCommande.afficherSeparateur();
    }

    /**
     * Compresse un fichier en utilisant l'algorithme de Huffman
     * et stocke le résultat dans le dossier spécifié.
     * La méthode crée le dossier de destination si nécessaire
     * et ajoute l'extension '.bin' au fichier compressé.
     * En cas d'échec lors de la création du dossier ou de la compression,
     * une exception est lancée.
     *
     * @param cheminFichierSource Chemin complet du fichier source à compresser.
     * @param cheminDossierDestination Chemin du dossier où le fichier
     *                                 compressé sera enregistré.
     * @param nomFichierCompresse Nom de base du fichier
     *                            compressé (sans l'extension).
     * @throws RuntimeException Si une erreur d'entrée/sortie se produit
     *                          pendant le processus.
     */
    private static void compresserFichier(String cheminFichierSource,
                                          String cheminDossierDestination,
                                          String nomFichierCompresse) {

        out.println("Compression du fichier : " + cheminFichierSource);
        File sousDossier = new File(cheminDossierDestination, nomFichierCompresse);

        try {
            GestionPrompt.creerDossierPourCompilation(sousDossier.getPath());
        } catch (IOException erreur) {
            throw new RuntimeException("Erreur lors de la création du dossier"
                                       + " : " + erreur.getMessage());
        }
        File fichierDestination = new File(sousDossier, nomFichierCompresse + ".bin");

        CompressionHuffman compresser =
                new CompressionHuffman(cheminFichierSource,
                                       fichierDestination.getPath());
        try {
            long tempsCompression = System.currentTimeMillis();
            compresser.compresserFichier();

            ApplicationLigneCommande.afficherSeparateur();
            StatistiquesCompresseur.resumeCompression(cheminFichierSource,
                                                      fichierDestination.getPath(),
                                                      tempsCompression);
            ApplicationLigneCommande.afficherSeparateur();
            out.println();
        } catch (IOException erreur) {
            out.println("Erreur lors de la compression du fichier : "
                        + erreur.getMessage());
        }
    }

    /**
     * Décompresse un fichier binaire en utilisant l'algorithme de Huffman
     * et enregistre le texte décompressé.
     * La méthode ajoute l'extension '.txt' au nom du fichier décompressé.
     * Enregistre le fichier décompressé dans le dossier spécifié et
     * affiche le temps de décompression.
     *
     * @param cheminFichierSource Chemin complet du fichier binaire
     *                            à décompresser.
     * @param cheminDossierDestination Chemin du dossier de destination
     *                                 pour le fichier décompressé.
     * @param nomFichierDecompresse Nom de base du fichier décompressé
     *                              (sans l'extension).
     */
    private static void decompresserFichier(String cheminFichierSource,
                                            String cheminDossierDestination,
                                            String nomFichierDecompresse) {

        out.println("Décompression du fichier : " + cheminFichierSource);
        File fichierDestination = new File(cheminDossierDestination, nomFichierDecompresse + ".txt");

        DecompressionHuffman decompresser =
                new DecompressionHuffman(cheminFichierSource,
                                         fichierDestination.getPath());
        try {
            long tempsDecompression = System.currentTimeMillis();
            decompresser.decompresserFichier();

            ApplicationLigneCommande.afficherSeparateur();
            StatistiquesCompresseur.resumeDecompression(
                                                      cheminFichierSource,
                                                      fichierDestination.getPath(),
                                                      tempsDecompression
                                                      );
            ApplicationLigneCommande.afficherSeparateur();
            out.println();
        } catch (IOException erreur) {
            out.println("Erreur lors de la décompression du fichier : "
                        + erreur.getMessage());
        }
    }

    /**
     * Construit et enregistre un arbre de Huffman à partir
     * d'un fichier texte source.
     * Cette méthode prépare le chemin complet de la destination,
     * crée le dossier nécessaire, et utilise {@link CompressionHuffman}
     * pour générer et sauvegarder l'arbre dans un fichier texte.
     * Affiche des messages pour le suivi et gère les erreurs d'entrée/sortie.
     *
     * @param cheminFichierSource Chemin du fichier source.
     * @param cheminDossierDestination Dossier de destination pour
     *                                 le fichier compressé.
     * @param nomFichierCompresse Nom du fichier compressé (sans extension).
     */
    private static void genererArbreHuffman(String cheminFichierSource,
                                            String cheminDossierDestination,
                                            String nomFichierCompresse) {

        out.println("Création de l'arbre de Huffman à l'aide du fichier : "
                    + cheminFichierSource);
        File sousDossier = new File(cheminDossierDestination, nomFichierCompresse);

        try {
            GestionPrompt.creerDossierPourCompilation(sousDossier.getPath());
        } catch (IOException erreur) {
            throw new RuntimeException("Erreur lors de la création du dossier"
                    + " : " + erreur.getMessage());
        }
        File fichierDestination = new File(sousDossier, nomFichierCompresse + ".txt");

        CompressionHuffman creerArbre =
                new CompressionHuffman(cheminFichierSource,
                                       fichierDestination.getPath());
        try {
            creerArbre.genererEtEnregistrerArbreHuffman();
            out.println();
        } catch (IOException erreur) {
            out.println("Erreur lors de la compression du fichier : "
                    + erreur.getMessage());
        }
    }

    /**
     * Affiche les instructions pour l'utilisation
     * des commandes de l'application.
     */
    private static void afficherAide() {
        out.println("Utilisation des commandes de l'application de compression"
                    + " Huffman :");
        out.println("----  compresser <chemin_fichier_a_compresser> "
                    + "<nom_dossier_destination> <nom_fichier>  ----");
        out.println("     Compresse le fichier spécifié et enregistre le "
                    + "résultat dans le dossier de destination sous le nouveau"
                    + " nom de fichier.");
        out.println("----  decompresser <chemin_fichier_a_decompresser> "
                    + "<nom_dossier_destination> <nom_fichier>   ----");
        out.println("     Décompresse le fichier spécifié et enregistre "
                    + "le résultat dans le dossier de destination sous le "
                    + "nouveau nom de fichier.");
        out.println("----  construire <chemin_fichier_a_decompresser> "
                    + "<nom_dossier_destination> <nom_fichier>   ----");
        out.println("     Créer un arbre de Huffman à l'aide d'un "
                    + "fichier source fournit.");
        out.println("--------  help   --------");
        out.println("     Affiche ce message d'aide, expliquant comment "
                    + "utiliser toutes les commandes disponibles.");
    }
}
