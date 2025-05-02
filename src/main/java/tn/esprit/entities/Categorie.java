package tn.esprit.entities;

public class Categorie {

    private int id_categorie;
    private String nom_categorie;
    private String description_Categorie;
    private String type_categorie;
    public static int fakeId;

    public Categorie() {
    }

    public Categorie(int id_categorie, String nom_categorie, String description_Categorie, String type_categorie) {
        this.id_categorie = id_categorie;
        this.nom_categorie = nom_categorie;
        this.description_Categorie = description_Categorie;
        this.type_categorie = type_categorie;
    }

    public Categorie(String nom_categorie, String description_Categorie, String type_categorie) {
        this.nom_categorie = nom_categorie;
        this.description_Categorie = description_Categorie;
        this.type_categorie = type_categorie;
    }


    public int getId_categorie() {
        return id_categorie;
    }

    public void setId_categorie(int id_categorie) {
        this.id_categorie = id_categorie;
    }

    public String getNom_categorie() {
        return nom_categorie;
    }

    public void setNom_categorie(String nom_categorie) {
        this.nom_categorie = nom_categorie;
    }

    public String getDescription_Categorie() {
        return description_Categorie;
    }

    public void setDescription_Categorie(String description_Categorie) {
        this.description_Categorie = description_Categorie;
    }

    public String getType_categorie() {
        return type_categorie;
    }

    public void setType_categorie(String type_categorie) {
        this.type_categorie = type_categorie;
    }

    @Override
    public String toString() {
        return "Categorie{" +
                "id_categorie=" + id_categorie +
                ", nom_categorie='" + nom_categorie + '\'' +
                ", description_Categorie='" + description_Categorie + '\'' +
                ", type_categorie='" + type_categorie + '\'' +
                '}';
    }

    public static int getFakeId() {
        return fakeId;
    }

    public static void setFakeId(int fakeId) {
        Categorie.fakeId = fakeId;
    }
}
