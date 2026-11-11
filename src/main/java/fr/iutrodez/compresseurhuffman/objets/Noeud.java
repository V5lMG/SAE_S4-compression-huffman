/*
 * Pas de copyright, ni de droit d'auteur.
 * Noeud.java                                27/05/2024
 */
package fr.iutrodez.compresseurhuffman.objets;

/**
 * Représente un nœud dans l'arbre de Huffman.
 * Chaque nœud peut avoir un caractère, une fréquence,
 * un enfant gauche et un enfant droit.
 * Cette classe inclut des méthodes pour générer des codes de Huffman
 * pour les feuilles de l'arbre.
 *
 * @author V. Munier--Genie
 * @author R. Xaviertaborda
 * @author J. Seychelles
 * @author B. Thenieres
 * @version 2.0
 */
public class Noeud implements Comparable<Noeud> {
    /**
     * Le caractère représenté par ce nœud.
     */
    private byte caractere;

    /**
     * La fréquence d'apparition de ce caractère.
     */
    private int frequence;

    /**
     * L'enfant gauche de ce nœud.
     */
    private Noeud gauche;

    /**
     * L'enfant droit de ce nœud.
     */
    private Noeud droite;

    /**
     * Construit un nœud avec un caractère et une fréquence spécifiés.
     *
     * @param caractere Le caractère représenté par ce nœud.
     * @param frequence La fréquence d'apparition de ce caractère.
     */
    public Noeud(byte caractere, int frequence) {
        this.caractere = caractere;
        this.frequence = frequence;
    }

    /**
     * Construit un nœud avec une fréquence spécifiée.
     * Utilisé pour les noeuds intermédiaires dans l'arbre de Huffman.
     *
     * @param frequence La fréquence d'apparition des caractères
     *                  dans ce sous-arbre.
     */
    public Noeud(int frequence) {
        this.frequence = frequence;
    }

    /**
     * Construit un nœud vide.
     */
    public Noeud() {

    }

    /* ********** GETTERS ********** */
    /**
     * Retourne le caractère représenté par ce nœud.
     *
     * @return Le caractère représenté par ce nœud.
     */
    public byte getCaractere() {
        return caractere;
    }

    /**
     * Retourne la fréquence d'apparition du caractère représenté par ce nœud.
     *
     * @return La fréquence d'apparition du caractère.
     */
    public int getFrequence() {
        return frequence;
    }

    /**
     * Retourne l'enfant gauche de ce nœud.
     *
     * @return L'enfant gauche de ce nœud.
     */
    public Noeud getGauche() {
        return gauche;
    }

    /**
     * Retourne l'enfant droit de ce nœud.
     *
     * @return L'enfant droit de ce nœud.
     */
    public Noeud getDroite() {
        return droite;
    }


    /* ********** SETTERS ********** */
    /**
     * Définit le caractère représenté par ce nœud.
     *
     * @param caractere Le caractère à définir.
     */
    public void setCaractere(byte caractere) {
        this.caractere = caractere;
    }

    /**
     * Définit l'enfant gauche de ce nœud.
     *
     * @param gauche L'enfant gauche à définir.
     */
    public void setGauche(Noeud gauche) {
        this.gauche = gauche;
    }

    /**
     * Définit l'enfant droit de ce nœud.
     *
     * @param droite L'enfant droit à définir.
     */
    public void setDroite(Noeud droite) {
        this.droite = droite;
    }

    /**
     * Vérifie si ce nœud est une feuille (n'a pas d'enfants).
     *
     * @return true si ce nœud est une feuille, sinon false.
     */
    public boolean isFeuille() {
        return gauche == null && droite == null;
    }

    /**
     * Compare deux nœuds en fonction de leur fréquence.
     *
     * @param autre Le second nœud à comparer.
     * @return Valeur négative, zéro ou positive selon l'ordre des fréquences.
     */
    @Override
    public int compareTo(Noeud autre) {
        if (autre == null) {
            return 1;
        }
        return Integer.compare(this.frequence, autre.frequence);
    }
}
