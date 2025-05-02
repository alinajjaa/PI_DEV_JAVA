package tn.esprit.entities;

public class Panier {
    private int id_panier;
    private int id_user;
    private double total_panier;
    private int id_prod;
    private int quantite_panier;
    private String nom_prod;
    private double prix_prod;
    private String image_prod;


    public Panier() {
    }

    public Panier(double total_panier, int id_prod,String nom_prod, double prix_prod, String image_prod) {

        this.total_panier = total_panier;
        this.id_prod = id_prod;
        this.nom_prod = nom_prod;
        this.prix_prod = prix_prod;
        this.image_prod = image_prod;
    }

    public Panier(int id_panier, int id_user, double total_panier, int id_prod, int quantite_panier) {
        this.id_panier = id_panier;
        this.id_user = id_user;
        this.total_panier = total_panier;
        this.id_prod = id_prod;
        this.quantite_panier = quantite_panier;
    }

    public Panier(int id_user, double total_panier, int id_prod, int quantite_panier) {
        this.id_user = id_user;
        this.total_panier = total_panier;
        this.id_prod = id_prod;
        this.quantite_panier = quantite_panier;
    }

    public Panier(int idPanier, int idUser, double totalPanier, int productId, int quantitePanier, String nomProd, double prixProd, String imageProd) {
    }

    public int getId_panier() {
        return id_panier;
    }

    public void setId_panier(int id_panier) {
        this.id_panier = id_panier;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public double getTotal_panier() {
        return total_panier;
    }

    public void setTotal_panier(double total_panier) {
        this.total_panier = total_panier;
    }

    public int getId_prod() {
        return id_prod;
    }

    public void setId_prod(int id_prod) {
        this.id_prod = id_prod;
    }

    public int getQuantite_panier() {
        return quantite_panier;
    }

    public void setQuantite_panier(int quantite_panier) {
        this.quantite_panier = quantite_panier;
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

    public String getImage_prod() {
        return image_prod;
    }

    public void setImage_prod(String image_prod) {
        this.image_prod = image_prod;
    }

    @Override
    public String toString() {
        return "Panier{" +
                "id_panier=" + id_panier +
                ", id_user=" + id_user +
                ", total_panier=" + total_panier +
                ", id_prod=" + id_prod +
                ", quantite_panier=" + quantite_panier +
                ", nom_prod='" + nom_prod + '\'' +
                ", prix_prod=" + prix_prod +
                ", image_prod='" + image_prod + '\'' +
                '}';
    }
}
