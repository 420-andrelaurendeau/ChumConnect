package cal335.projet.mes_chums.dao;





import cal335.projet.mes_chums.modele.Contact;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO implements DAOGenerique<Contact> {
    private static final String URL = "jdbc:sqlite:C:/Users/mokht/Bureau/tp-java-final/TP-JAVA-FINAL/mes-chums/src/main/resources/data.db";

    //jdbc:sqlite:C:/Users/mokht/Bureau/tp-java-final/TP-JAVA-FINAL/mes_chums/src/main/ressources/data.db



    public static Connection obtenirConnexion() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public List<Contact> trouverFavoris() {
        List<Contact> favoris = new ArrayList<>();
        String sql = "SELECT * FROM Contact WHERE is_favoris = ?";
        try (Connection conn = obtenirConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, true);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Contact contact = new Contact();
                    contact.setId_contact(rs.getInt("id_contact"));
                    contact.setNom(rs.getString("nom"));
                    contact.setPrenom(rs.getString("prenom"));

                    favoris.add(contact);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des favoris : " + e.getMessage());
        }
        return favoris;
    }




    @Override
    public void ajouter(Contact contact) {
        String sql = "INSERT INTO Contact (nom, prenom, is_favoris) VALUES (?, ?, ?)";
        try (Connection conn = obtenirConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, contact.getNom());
            pstmt.setString(2, contact.getPrenom());
            pstmt.setBoolean(3, contact.isFavoris());
            pstmt.executeUpdate();
            System.out.println("Contact ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du contact : " + e.getMessage());
        }
    }


    @Override
    public void supprimer(Integer id) {
        // Code JDBC pour supprimer un contact
    }

    @Override
    public void mettreAJour(Contact contact) {
        // Code JDBC pour mettre à jour un contact
    }

    @Override
    public Contact trouverParId(Integer id) {
        // Code JDBC pour trouver un contact par ID
        return null;
    }
}