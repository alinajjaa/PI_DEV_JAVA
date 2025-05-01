package org.agritrace.services;


import org.agritrace.entities.Service;
import org.agritrace.interfaces.IService;
import org.agritrace.tools.MyConnection;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceServices implements IService<Service> {
    @Override
    public void addEntity(Service service) {
        String query = "INSERT INTO service (nom, type, description, prix, status, adresse, image, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setString(1, service.getNom());
            pst.setString(2, service.getType());
            pst.setString(3, service.getDescription());
            pst.setInt(4, service.getPrix());
            pst.setString(5, service.getStatus());
            pst.setString(6, service.getAdresse());
            pst.setString(7, service.getImage());
            pst.setTimestamp(8, Timestamp.valueOf(service.getUpdatedAt()));
            pst.executeUpdate();
            System.out.println("Service added");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(Service service) {
        String query = "DELETE FROM service WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setInt(1, service.getId());
            pst.executeUpdate();
            System.out.println("Service deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateEntity(Service service, int id) {
        String query = "UPDATE service SET nom = ?, type = ?, description = ?, prix = ?, status = ?, adresse = ?, image = ?, updated_at = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setString(1, service.getNom());
            pst.setString(2, service.getType());
            pst.setString(3, service.getDescription());
            pst.setInt(4, service.getPrix());
            pst.setString(5, service.getStatus());
            pst.setString(6, service.getAdresse());
            pst.setString(7, service.getImage());
            pst.setTimestamp(8, Timestamp.valueOf(service.getUpdatedAt()));
            pst.setInt(9, id);
            pst.executeUpdate();
            System.out.println("Service updated");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Service> getAllData() {
        List<Service> data = new ArrayList<>();
        String query = "SELECT * FROM service";
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Service s = new Service();
                s.setId(rs.getInt("id"));
                s.setNom(rs.getString("nom"));
                s.setType(rs.getString("type"));
                s.setDescription(rs.getString("description"));
                s.setPrix(rs.getInt("prix"));
                s.setStatus(rs.getString("status"));
                s.setAdresse(rs.getString("adresse"));
                s.setImage(rs.getString("image"));
                Timestamp timestamp = rs.getTimestamp("updated_at");
                if (timestamp != null) {
                    s.setUpdatedAt(timestamp.toLocalDateTime());
                }
                data.add(s);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return data;
    }
}
