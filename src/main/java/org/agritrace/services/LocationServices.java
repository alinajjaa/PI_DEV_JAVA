package org.agritrace.services;

import org.agritrace.entities.Location;
import org.agritrace.interfaces.IService;
import org.agritrace.tools.MyConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LocationServices implements IService<Location> {
    @Override
    public void addEntity(Location location) {
        String query = "INSERT INTO location (service_id, iduser, details, prix_total, date_debut, date_fin) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setInt(1, location.getServiceId());
            pst.setInt(2, location.getIdUser());
            pst.setString(3, location.getDetails());
            pst.setDouble(4, location.getPrixTotal());
            pst.setDate(5, Date.valueOf(location.getDateDebut()));
            pst.setDate(6, Date.valueOf(location.getDateFin()));
            pst.executeUpdate();
            System.out.println("Location added");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(Location location) {
        String query = "DELETE FROM location WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setInt(1, location.getId());
            pst.executeUpdate();
            System.out.println("Location deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateEntity(Location location, int id) {
        String query = "UPDATE location SET service_id = ?, iduser = ?, details = ?, prix_total = ?, date_debut = ?, date_fin = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setInt(1, location.getServiceId());
            pst.setInt(2, location.getIdUser());
            pst.setString(3, location.getDetails());
            pst.setDouble(4, location.getPrixTotal());
            pst.setDate(5, Date.valueOf(location.getDateDebut()));
            pst.setDate(6, Date.valueOf(location.getDateFin()));
            pst.setInt(7, id);
            pst.executeUpdate();
            System.out.println("Location updated");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Location> getAllData() {
        List<Location> data = new ArrayList<>();
        String query = "SELECT * FROM location";
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Location loc = new Location();
                loc.setId(rs.getInt("id"));
                loc.setServiceId(rs.getInt("service_id"));
                loc.setIdUser(rs.getInt("iduser"));
                loc.setDetails(rs.getString("details"));
                loc.setPrixTotal(rs.getDouble("prix_total"));
                loc.setDateDebut(rs.getDate("date_debut").toLocalDate());
                loc.setDateFin(rs.getDate("date_fin").toLocalDate());
                data.add(loc);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return data;
    }
}
