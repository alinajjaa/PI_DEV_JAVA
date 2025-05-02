package tn.esprit.test;

import tn.esprit.entities.Categorie;
import tn.esprit.entities.Panier;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceCategorie;
import tn.esprit.service.ServicePanier;
import tn.esprit.service.ServiceProduit;
import tn.esprit.util.MyDataBase;

import java.sql.SQLException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        byte[] imageBytes = null;

        ServiceProduit sp = new ServiceProduit();
        ServicePanier spa = new ServicePanier();
        ServiceCategorie c = new ServiceCategorie();
        Produit produit = new Produit("bar fixe", 10.99, "bar fixe", 5, "", 1);
        System.out.println("Produit Ajouté avec succés");
        try {
            sp.modifier(new Produit(1,"bare fixe", 200, "barfixe barfixe", 7, "imageBytes", 2));
            System.out.println("Produit Modifié avec succés");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        /** try {
         sp.supprimer(3);
         System.out.println("Produit supprimé avec succés");
         } catch (SQLException e) {
         System.out.println(e.getMessage());
         }*/
        try {
            System.out.println(sp.recuperer());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try {
            spa.ajouter(new Panier(1, 200, 1, 56));
            System.out.println("Panier Ajouté avec succés");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try {
            c.ajouter(new Categorie(1, "musculation", "fafzefze", "sport"));
            c.ajouter(new Categorie(1, "nutrition", "fafzefze", "nutrition"));

            System.out.println("categorie Ajouté avec succés");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}