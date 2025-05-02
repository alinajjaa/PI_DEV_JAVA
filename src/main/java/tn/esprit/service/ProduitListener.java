package tn.esprit.service;
import tn.esprit.entities.Produit;
public interface ProduitListener {
    void onDelete(Produit produit);
    void refreshList();
    void OnModifier(Produit produit);
}
