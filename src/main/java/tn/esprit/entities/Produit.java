package tn.esprit.entities;

import javafx.scene.control.TextField;

public class Produit {
    private int id_prod;
    private String nom_prod;
    private double prix_prod;
    private String description_prod;
    private int quantite_prod;
    private String image_prod;
    private int id_categorie;
    private String nomCategorie;
    public static int fakeIdP;

    public Produit() {
    }

    public Produit(int id_prod, String nom_prod, double prix_prod, String description_prod, int quantite_prod, String image_prod, int id_categorie) {
        this.id_prod = id_prod;
        this.nom_prod = nom_prod;
        this.prix_prod = prix_prod;
        this.description_prod = description_prod;
        this.quantite_prod = quantite_prod;
        this.image_prod = image_prod;
        this.id_categorie = id_categorie;
    }

    public Produit(String nom_prod, double prix_prod, String description_prod, int quantite_prod,String image_prod, int id_categorie) {
        this.nom_prod = nom_prod;
        this.prix_prod = prix_prod;
        this.description_prod = description_prod;
        this.quantite_prod = quantite_prod;
        this.image_prod = image_prod;
        this.id_categorie = id_categorie;
    }

    public Produit(String text, int i, String text1) {
        this.nom_prod = nom_prod;
        this.prix_prod = prix_prod;
        this.description_prod = description_prod;
        this.quantite_prod = quantite_prod;
        this.image_prod = image_prod;
    }

    public Produit(String text, int i, String text1, String string) {
        this.nom_prod = nom_prod;
        this.prix_prod = prix_prod;
        this.description_prod = description_prod;

    }

    public Produit(String nom_prod, double prix_prod, String description_prod, int quantite_prod, String image_prod, String nomCategorie) {
        this.nom_prod = nom_prod;
        this.prix_prod = prix_prod;
        this.description_prod = description_prod;
        this.quantite_prod = quantite_prod;
        this.image_prod = image_prod;
        this.nomCategorie = nomCategorie;
    }

    public Produit(String nom_prod, double prix_prod, String description_prod, int quantite_prod, String nomCategorie) {
        this.nom_prod = nom_prod;
        this.prix_prod = prix_prod;
        this.description_prod = description_prod;
        this.quantite_prod = quantite_prod;
        this.nomCategorie = nomCategorie;
    }

    public Produit(int idProd, TextField nomProd, double prix, TextField descriptionProd, TextField quantiteProd, String path) {
    }

    public Produit(int produitId, String nomProd, int prixProd, String descriptionProd, Double quantiteProd, String imageProd, int idCategorie) {
    }

    public String getNomCategorie() {
        return nomCategorie;
    }

    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    public int getId_prod() {
        return id_prod;
    }

    public void setId_prod(int id_prod) {
        this.id_prod = id_prod;
    }

    public String getNom_prod() {
        return nom_prod;
    }

    public void setNom_prod(String nom_prod) {
        this.nom_prod = nom_prod;
    }

    public double getPrix_prod() {
        return prix_prod;
    }

    public void setPrix_prod(double prix_prod) {
        this.prix_prod = prix_prod;
    }

    public String getDescription_prod() {
        return description_prod;
    }

    public void setDescription_prod(String description_prod) {
        this.description_prod = description_prod;
    }

    public int getQuantite_prod() {
        return quantite_prod;
    }

    public void setQuantite_prod(int quantite_prod) {
        this.quantite_prod = quantite_prod;
    }

    public String getImage_prod() {
        return image_prod;
    }

    public void setImage_prod(String image_prod) {
        this.image_prod = image_prod;
    }

    public int getId_categorie() {
        return id_categorie;
    }

    public static int getFakeIdP() {
        return fakeIdP;
    }

    public static void setFakeIdP(int fakeIdP) {
        Produit.fakeIdP = fakeIdP;
    }

    public void setId_categorie(int id_categorie) {
        this.id_categorie = id_categorie;
    }

    @Override
    public String toString() {
        return "produit{" +
                "id_prod=" + id_prod +
                ", nom_prod='" + nom_prod + '\'' +
                ", prix_prod=" + prix_prod +
                ", description_prod='" + description_prod + '\'' +
                ", quantite_prod=" + quantite_prod +
                ", image_prod='" + image_prod + '\'' +
                ", id_categorie=" + id_categorie +
                '}';
    }
}
