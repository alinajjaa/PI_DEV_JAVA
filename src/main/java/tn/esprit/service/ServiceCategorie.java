package tn.esprit.service;
import tn.esprit.entities.Categorie;
import tn.esprit.entities.Panier;
import tn.esprit.entities.Produit;
import tn.esprit.util.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ServiceCategorie implements IService<Categorie>{
    private Connection connection;

    public ServiceCategorie() {
        connection = MyDataBase.getInstance().getConnection();
    }


    @Override
    public void ajouter(Categorie categorie) throws SQLException {
        String sql = "INSERT INTO categorie (nom_categorie, description_categorie, type_categorie) VALUES ('"+categorie.getNom_categorie()+"', '"+categorie.getDescription_Categorie()+"', '"+categorie.getType_categorie()+"')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    @Override
    public void modifier(Categorie categorie) throws SQLException {
        String sql = "update categorie set  nom_categorie = ?, description_categorie = ?, type_categorie = ? where id_categorie = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, categorie.getNom_categorie());
        preparedStatement.setString(2, categorie.getDescription_Categorie());
        preparedStatement.setString(3, categorie.getType_categorie());
        preparedStatement.setInt(4, categorie.getId_categorie());
        preparedStatement.executeUpdate();
    }

    @Override
    public List<Panier> supprimer(int id_categorie) throws SQLException {
        String sql = "delete from categorie where id_categorie = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id_categorie);
        preparedStatement.executeUpdate();
        return null;
    }

    @Override
    public List<Categorie> recuperer() throws SQLException {
        List<Categorie> categories = new ArrayList<>();
        String sql ="Select * from categorie";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()){
            Categorie c = new Categorie();
            c.setId_categorie(rs.getInt("id_categorie"));
            c.setNom_categorie(rs.getString("nom_categorie"));
            c.setDescription_Categorie(rs.getString("description_categorie"));
            c.setType_categorie(rs.getString("type_categorie"));
            categories.add(c);
        }
        return categories;
    }

    @Override
    public List<Panier> fetchProduitDetails() throws SQLException {
        return null;
    }

    @Override
    public Categorie getById(int id) throws SQLException {
        Categorie categorie = new Categorie();
        String sql ="Select * from categorie where id_categorie = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();
        Categorie cat = null;

        if (resultSet.next()) {
            int idCategorie = resultSet.getInt("id_categorie");
            String categoryName = resultSet.getString("nom_categorie");
            String description = resultSet.getString("description_categorie");
            String type = resultSet.getString("type_categorie");
            cat = new Categorie(id, categoryName, description, type);
        }
        return cat;
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
    public Categorie getCategorieByCategorieId(int CategorieId) throws SQLException {
        String query = "SELECT * FROM categorie WHERE id_categorie = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, CategorieId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String nom_categorie = resultSet.getString("nom_categorie");
                String description_categorie = resultSet.getString("description_categorie");
                String type_categorie = resultSet.getString("type_categorie");

                return new Categorie(CategorieId, nom_categorie, description_categorie, type_categorie);
            }
        } catch (SQLException e) {
            throw new SQLException("Error searching for categorie: " + e.getMessage());
        }

        // Return null if no account is found
        return null;
    }

    @Override
    public Produit getProduitByProduitId(int ProduitId) throws SQLException {
        return null;
    }

    @Override
    public Produit getByIdP(int id) throws SQLException {
        return null;
    }
    public boolean existsByNomOrType(String nom, String type) throws SQLException {
        String query = "SELECT COUNT(*) FROM categorie WHERE nom_categorie = ? OR type_categorie = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }


}
