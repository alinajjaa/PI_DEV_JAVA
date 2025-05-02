package tn.esprit.service;
import tn.esprit.entities.Categorie;
import tn.esprit.entities.Panier;
import tn.esprit.entities.Produit;
import tn.esprit.util.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceProduit implements IService<Produit> {
    private Connection connection;
    private final ServiceCategorie serviceCategorie = new ServiceCategorie();
    private static ServiceProduit instance;

    public ServiceProduit() {
        connection = MyDataBase.getInstance().getConnection();
    }
    //stat
    public Map<String, Integer> getNombreProduitsParCategorie() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String req = "SELECT c.nom_categorie, COUNT(*) as total FROM produit p " +
                "JOIN categorie c ON p.id_categorie = c.id_categorie " +
                "GROUP BY c.nom_categorie";
        try (Statement stm = connection.createStatement(); ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                stats.put(rs.getString("nom_categorie"), rs.getInt("total"));
            }
        }
        return stats;
    }

    public static ServiceProduit getInstance() throws SQLException{
        if(instance==null)
            instance=new ServiceProduit();
        return instance;
    }
    public boolean produitExiste(String nomProduit) throws SQLException {
        String sql = "SELECT COUNT(*) FROM produit WHERE LOWER(nom_prod) = LOWER(?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nomProduit.trim());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public void ajouter(Produit produit) throws SQLException {
        if (connection == null) {
            throw new SQLException("Connection is null. Make sure the database connection is established.");
        }

        String sql = "INSERT INTO produit (nom_prod, prix_prod, description_prod, quantite_prod, image_prod, id_categorie) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, produit.getNom_prod());
        preparedStatement.setDouble(2, produit.getPrix_prod());
        preparedStatement.setString(3, produit.getDescription_prod());
        preparedStatement.setInt(4, produit.getQuantite_prod());
        preparedStatement.setString(5, produit.getImage_prod());
        preparedStatement.setInt(6, produit.getId_categorie());
        preparedStatement.executeUpdate();
    }

    @Override
    public void modifier(Produit produit) throws SQLException {
        String sql = "update produit set nom_prod = ?, prix_prod = ?, description_prod = ?, quantite_prod = ?, image_prod = ?, id_categorie = ? where id_prod = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, produit.getNom_prod());
        preparedStatement.setDouble(2, produit.getPrix_prod());
        preparedStatement.setString(3, produit.getDescription_prod());
        preparedStatement.setInt(4, produit.getQuantite_prod());
        preparedStatement.setString(5, produit.getImage_prod());
        preparedStatement.setInt(6, produit.getId_categorie());
        preparedStatement.setInt(7, produit.getId_prod());
        preparedStatement.executeUpdate();
    }

    @Override
    public List<Panier> supprimer(int id_prod) throws SQLException {
        String sql = "delete from produit where id_prod = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id_prod);
        preparedStatement.executeUpdate();
        return null;
    }

    @Override
    public List<Produit> recuperer() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql ="Select * from produit";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            Produit p = new Produit();
            p.setId_prod(rs.getInt("id_prod"));
            p.setNom_prod(rs.getString("nom_prod"));
            p.setPrix_prod(rs.getDouble("prix_prod"));
            p.setDescription_prod(rs.getString("description_prod"));
            p.setQuantite_prod(rs.getInt("quantite_prod"));
            p.setImage_prod(rs.getString("image_prod"));
            p.setId_categorie(rs.getInt("id_categorie"));
          //  Categorie categorie =serviceCategorie.getById(p.getId_categorie());
        //    p.setNomCategorie(categorie.getNom_categorie());
            produits.add(p);
        }
        return produits;
    }


    @Override
    public List<Panier> fetchProduitDetails() throws SQLException {
        return null;
    }

    @Override
    public Categorie getById(int id) throws SQLException {
        return null;
    }

    public List<String> getAllCategories() throws SQLException {
        List<String> categoryNames = new ArrayList<>();

        String query = "SELECT nom_categorie FROM categorie";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String categoryName = rs.getString("nom_categorie");
                categoryNames.add(categoryName);
            }
        }

        return categoryNames;
    }


    @Override
    public Categorie getCategorieByCategorieId(int CategorieId) throws SQLException {
        return null;
    }

    public int fetchCategoryIdByName(String categoryName) throws SQLException {

        String query = "SELECT id_categorie FROM categorie WHERE nom_categorie = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, categoryName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_categorie");
                }
            }
        }
        return -1;
    }
    @Override
    public Produit getProduitByProduitId(int ProduitId) throws SQLException {
        String query = "SELECT * FROM produit WHERE id_prod = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, ProduitId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String nom_prod = resultSet.getString("nom_prod");
                Double prix_prod = (double)Integer.parseInt(resultSet.getString("prix_prod"));
                String description_prod = resultSet.getString("description_prod");
                int quantite_prod = Integer.parseInt(resultSet.getString("quantite_prod"));
                String image_prod = resultSet.getString("image_prod");
                int id_categorie = Integer.parseInt(resultSet.getString("id_categorie"));

                return new Produit(ProduitId, nom_prod, prix_prod, description_prod,quantite_prod,image_prod,id_categorie);
            }
        } catch (SQLException e) {
            throw new SQLException("Error searching for categorie: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Produit getByIdP(int id) throws SQLException {
        return null;
    }


}
