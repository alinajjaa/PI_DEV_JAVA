package tn.esprit.service;

import tn.esprit.entities.Categorie;
import tn.esprit.entities.Panier;
import tn.esprit.entities.Produit;

import java.sql.SQLException;
import java.util.List;

public interface IService <T>{
    void ajouter(T t) throws SQLException;
    void modifier (T t) throws SQLException;
    List<Panier> supprimer(int id_prod) throws SQLException;
    List<T> recuperer() throws SQLException;

    List<Panier> fetchProduitDetails() throws SQLException;

    Categorie getById(int id) throws SQLException ;
    public Categorie getCategorieByCategorieId(int CategorieId) throws SQLException;
    public Produit getProduitByProduitId(int ProduitId) throws SQLException;

    Produit getByIdP(int id) throws SQLException ;


    }
