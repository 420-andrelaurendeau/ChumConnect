package cal335.projet.mes_chums.dao;

import cal335.projet.mes_chums.modele.Coordonnees;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CoordonneesDAO {
    public List<Coordonnees> obtenirCoordonneesParContact(Integer contactId) {
        List<Coordonnees> coordonneesList = new ArrayList<>();
        String sql = "SELECT c.id_coordonnees, c.latitude, c.longitude FROM Coordonnees c " +
                "JOIN Adresse a ON c.id_coordonnees = a.id_coordonnees " +
                "JOIN ContactAdresse ca ON a.id_adresse = ca.id_adresse " +
                "WHERE ca.id_contact = ?";
        try (Connection conn = ContactDAO.obtenirConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, contactId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Coordonnees coordonnees = new Coordonnees();
                    coordonnees.setId_coordonnees(rs.getInt("id_coordonnees"));
                    coordonnees.setLatitude(rs.getDouble("latitude"));
                    coordonnees.setLongitude(rs.getDouble("longitude"));
                    coordonneesList.add(coordonnees);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des coordonnées : " + e.getMessage());
        }
        return coordonneesList;
    }
}

