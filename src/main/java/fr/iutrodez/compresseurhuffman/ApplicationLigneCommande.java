/*
 * Pas de copyright, ni de droit d'auteur.
 * ApplicationLigneCommande.java               27/05/2024
 */
package fr.iutrodez.compresseurhuffman;

import fr.iutrodez.compresseurhuffman.huffman.CompressionHuffman;
import fr.iutrodez.compresseurhuffman.huffman.DecompressionHuffman;
import fr.iutrodez.compresseurhuffman.outils.GestionPrompt;
import fr.iutrodez.compresseurhuffman.outils.StatistiquesCompresseur;

import java.io.IOException;
import java.util.Scanner;

import static java.lang.System.out;

/**
 * Classe principale permettant de gérer l'application de compression et de
 * décompression de fichiers en ligne de commande.
 * Elle fournit un point d'entrée pour l'interaction avec l'utilisateur
 * via la console. Elle permet à l'utilisateur de choisir entre la compression
 * et la décompression de fichiers texte.
 * L'application s'exécute jusqu'à ce que l'utilisateur choisisse de quitter.
 *
 * @author V. Munier--Genie
 * @author R. Xaviertaborda
 * @author J. Seychelles
 * @author B. Thenieres
 * @version 2.0
 */
public class ApplicationLigneCommande {

    /**
     * Scanner pour la lecture sur console standard (fallback lorsque System.console() est null).
     */
    private static final Scanner SCANNER_CONSOLE = new Scanner(System.in);

    /**
     * Instance de {@link GestionPrompt} utilisée pour gérer
     * les opérations sur les fichiers.
     */
    public static GestionPrompt gestionnaireFichier = new GestionPrompt();

    /**
     * Point d'entrée principal de l'application de ligne de commande.
     * Cette méthode évalue les arguments de la ligne de commande
     * pour déterminer le mode de fonctionnement.
     * Si des arguments sont passés, elle tente de les traiter pour
     * des opérations de commande spécifiques.
     * Sinon, elle affiche un menu interactif pour permettre
     * à l'utilisateur de choisir entre différentes actions.
     *
     * @param args Les arguments de la ligne de commande (optionnel).
     */
    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                ArgumentLigneCommande.gererArgumentsEnLigneCommande(args);
            } else {
                menu();
            }
        } catch (Exception e) {
            out.println("Erreur durant l'exécution de l'application: "
                        + e.getMessage());
        }
    }

    /**
     * Affiche un menu interactif et permet à l'utilisateur de choisir
     * entre différentes options de traitement :
     * Compression de fichiers,
     * décompression de fichiers,
     * création d'arbre de Huffman
     * ou fermeture l'application.
     * Chaque choix est traité par appel de
     * la méthode correspondante (compression ou décompression).
     * Des validations et des messages d'erreur sont présentés
     * si un choix invalide est effectué.
     */
    private static void menu() {
        afficherSeparateur();
        out.println("L'application est lancée.");

        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            int choix = demanderChoixUtilisateur();

            switch (choix) {
                case 1:
                    lancerCompression();
                    break;
                case 2:
                    lancerDecompression();
                    break;
                case 3:
                    lancerCreationArbre();
                    break;
                case 4:
                    continuer = false;
                    break;
                default:
                    out.println("Choix invalide. " +
                                "Veuillez saisir un numéro valide.");
                    break;
            }
        }
        afficherSeparateur();
        out.println("Fin de l'application.");
        afficherSeparateur();
    }

    /**
     * Lance le processus de compression d'un fichier texte en utilisant
     * l'algorithme de Huffman.
     * Le fichier source doit être un fichier texte (.txt) et
     * la destination sera un fichier binaire (.bin).
     * Affiche des informations de compression,
     * y compris des statistiques de réduction de taille.
     * Gère également les erreurs de fichier et
     * affiche les messages appropriés en cas d'erreur.
     */
    private static void lancerCompression() {
        String source = gestionnaireFichier.getFichierSource("txt");
        if (source == null) {
            return;
        }

        String destination = gestionnaireFichier.getFichierDestination("bin");
        if (destination == null) {
            return;
        }

        CompressionHuffman compresser =
                new CompressionHuffman(source, destination);

        try {
            long tempsCompression = System.currentTimeMillis();
            compresser.compresserFichier();
            afficherSeparateur();
            StatistiquesCompresseur.resumeCompression(source,
                                                      destination,
                                                      tempsCompression);
        } catch (IOException erreur) {
            out.println("Erreur lors de la compression du fichier : "
                        + erreur.getMessage());
        }
    }

    /**
     * Lance le processus de décompression d'un fichier binaire
     * compressé en utilisant l'algorithme de Huffman.
     * Le fichier source doit être un fichier binaire (.bin) et
     * la destination sera un fichier texte (.txt).
     * Affiche le résumé de la décompression,
     * y compris le temps pris pour décompresser.
     * Gère les erreurs de fichier et
     * affiche les messages appropriés en cas d'erreur.
     */
    private static void lancerDecompression() {
        String source = gestionnaireFichier.getFichierSource("bin");
        if (source == null) {
            return;
        }

        String destination = gestionnaireFichier.getFichierDestination("txt");
        if (destination == null) {
            return;
        }

        DecompressionHuffman decompresser =
                new DecompressionHuffman(source, destination);

        try {
            long tempsDecompression = System.currentTimeMillis();
            decompresser.decompresserFichier();

            afficherSeparateur();
            StatistiquesCompresseur.resumeDecompression(source,
                                                        destination,
                                                        tempsDecompression);
        } catch (IOException erreur) {
            out.println("Erreur lors de la décompression du fichier : "
                        + erreur.getMessage());
        }
    }

    /**
     * Initie le processus de création et d'enregistrement
     * d'un arbre de Huffman.
     * Obtient les chemins de fichier source et destination via
     * un gestionnaire de fichiers, puis utilise la classe
     * {@link CompressionHuffman} pour construire l'arbre de Huffman et
     * l'enregistrer dans le fichier de destination. Affiche un séparateur
     * en cas de succès et gère les exceptions d'entrée/sortie,
     * en affichant des messages d'erreur appropriés.
     */
    private static void lancerCreationArbre() {
        String source = gestionnaireFichier.getFichierSource("txt");
        if (source == null) {
            return;
        }

        String destination = gestionnaireFichier.getFichierDestination("txt");
        if (destination == null) {
            return;
        }

        CompressionHuffman creerArbre =
                new CompressionHuffman(source, destination);

        try {
            creerArbre.genererEtEnregistrerArbreHuffman();
            afficherSeparateur();
        } catch (IOException erreur) {
            out.println("Erreur lors de la compression du fichier : "
                    + erreur.getMessage());
        }
    }

    /**
     * Affiche un menu à l'utilisateur lui permettant de choisir entre
     * une compression de fichier,
     * une décompression de fichier
     * ou quitter l'application.
     */
    private static void afficherMenu() {
        afficherSeparateur();
        out.println("Choisissez une action :");
        out.println("1. Compression de fichier");
        out.println("2. Décompression de fichier");
        out.println("3. Créer un arbre de Huffman");
        out.println("4. Quitter l'application");
        afficherSeparateur();
    }

    /**
     * Demande à l'utilisateur de saisir un choix valide.
     * Répète la demande jusqu'à ce qu'un choix valide soit saisi.
     * Gère les environnements avec ou sans console native (IDE, redirections).
     *
     * @return le choix de l'utilisateur.
     */
    private static int demanderChoixUtilisateur() {
        out.print("Votre choix : ");
        String input = null;
        if (System.console() != null) {
            input = System.console().readLine();
        } else if (SCANNER_CONSOLE.hasNextLine()) {
            input = SCANNER_CONSOLE.nextLine();
        }

        if (input == null) {
            return 4; // Quitter si le flux d'entrée est terminé
        }

        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            out.println("Veuillez saisir un numéro valide.");
            return demanderChoixUtilisateur();
        }
    }

    /**
     * Affiche un séparateur horizontal pour
     * structurer visuellement l'interface en console.
     */
    public static void afficherSeparateur() {
        out.println("--------------------------------------------------------");
    }
}
