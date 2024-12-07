package cal335.projet.mes_chums.dao;





import cal335.projet.mes_chums.Main;
import cal335.projet.mes_chums.modele.Adresse;
import cal335.projet.mes_chums.modele.Contact;
import cal335.projet.mes_chums.modele.Coordonnees;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO implements DAOGenerique<Contact> {
    //private static final String URL = "jdbc:sqlite:src/main/resources/data.db";







    public static Connection obtenirConnexion() throws SQLException {
        String cheminBD = Main.class.getClassLoader().getResource("data.db").getPath();
        return DriverManager.getConnection("jdbc:sqlite:" + cheminBD);
    }

    public List<Contact> trouverFavoris() throws SQLException{
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
                    contact.setFavoris(rs.getBoolean("is_favoris"));

                    favoris.add(contact);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des favoris : " + e.getMessage());
        }
        return favoris;
    }




    @Override
    public Contact ajouter(Contact contact) throws SQLException {
        String insertContactSQL = "INSERT INTO Contact (nom, prenom, is_favoris) VALUES (?, ?, ?)";
        String insertAdresseSQL = "INSERT INTO Adresse (rue, ville, code_postal, pays, id_coordonnees) VALUES (?, ?, ?, ?, ?)";
        String insertContactAdresseSQL = "INSERT INTO ContactAdresse (id_contact, id_adresse) VALUES (?, ?)";

        try (Connection conn = obtenirConnexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement contactStmt = conn.prepareStatement(insertContactSQL, Statement.RETURN_GENERATED_KEYS)) {
                contactStmt.setString(1, contact.getNom());
                contactStmt.setString(2, contact.getPrenom());
                contactStmt.setBoolean(3, contact.isFavoris());
                contactStmt.executeUpdate();

                ResultSet generatedKeys = contactStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int contactId = generatedKeys.getInt(1);
                    contact.setId_contact(contactId);

                    try (PreparedStatement adresseStmt = conn.prepareStatement(insertAdresseSQL, Statement.RETURN_GENERATED_KEYS);
                         PreparedStatement contactAdresseStmt = conn.prepareStatement(insertContactAdresseSQL)) {
                        for (Adresse adresse : contact.getAdresses()) {
                            // Ajouter Coordonnees si nécessaire
                            Integer coordId = null;
                            if (adresse.getCoordonnees() != null) {
                                coordId = ajouterCoordonnees(conn, adresse.getCoordonnees());
                            }

                            adresseStmt.setString(1, adresse.getRue());
                            adresseStmt.setString(2, adresse.getVille());
                            adresseStmt.setString(3, adresse.getCodePostal());
                            adresseStmt.setString(4, adresse.getPays());
                            if (coordId != null) {
                                adresseStmt.setInt(5, coordId);
                            } else {
                                adresseStmt.setNull(5, Types.INTEGER);
                            }
                            adresseStmt.executeUpdate();

                            ResultSet adresseKeys = adresseStmt.getGeneratedKeys();
                            if (adresseKeys.next()) {
                                int adresseId = adresseKeys.getInt(1);
                                // Liaison ContactAdresse
                                contactAdresseStmt.setInt(1, contactId);
                                contactAdresseStmt.setInt(2, adresseId);
                                contactAdresseStmt.executeUpdate();
                            }
                        }
                    }
                } else {
                    conn.rollback();
                    throw new SQLException("Échec de l'ajout du contact, aucun ID généré.");
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }

        return contact;
    }




    public void ajouterAdresse(int contactId, Adresse adresse) throws SQLException {
        String insertCoordSQL = "INSERT INTO Coordonnees (latitude, longitude) VALUES (?, ?)";
        String insertAdresseSQL = "INSERT INTO Adresse (rue, ville, code_postal, pays, id_coordonnees) VALUES (?, ?, ?, ?, ?)";
        String insertContactAdresseSQL = "INSERT INTO ContactAdresse (id_contact, id_adresse) VALUES (?, ?)";

        try (Connection conn = obtenirConnexion()) {
            conn.setAutoCommit(false);
            try {
                // Ajouter Coordonnees si nécessaire
                Integer coordId = null;
                if (adresse.getCoordonnees() != null) {
                    coordId = ajouterCoordonnees(conn, adresse.getCoordonnees());
                }

                // Ajouter Adresse
                try (PreparedStatement adresseStmt = conn.prepareStatement(insertAdresseSQL, Statement.RETURN_GENERATED_KEYS)) {
                    adresseStmt.setString(1, adresse.getRue());
                    adresseStmt.setString(2, adresse.getVille());
                    adresseStmt.setString(3, adresse.getCodePostal());
                    adresseStmt.setString(4, adresse.getPays());
                    if (coordId != null) {
                        adresseStmt.setInt(5, coordId);
                    } else {
                        adresseStmt.setNull(5, Types.INTEGER);
                    }
                    adresseStmt.executeUpdate();

                    ResultSet adresseKeys = adresseStmt.getGeneratedKeys();
                    if (adresseKeys.next()) {
                        int adresseId = adresseKeys.getInt(1);
                        adresse.setId_adresse(adresseId);

                        // Liaison ContactAdresse
                        try (PreparedStatement contactAdresseStmt = conn.prepareStatement(insertContactAdresseSQL)) {
                            contactAdresseStmt.setInt(1, contactId);
                            contactAdresseStmt.setInt(2, adresseId);
                            contactAdresseStmt.executeUpdate();
                        }
                    } else {
                        throw new SQLException("Échec de l'ajout de l'adresse, aucun ID généré.");
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }





    private Integer ajouterCoordonnees(Connection conn, Coordonnees coordonnees) throws SQLException {
        String insertCoordSQL = "INSERT INTO coordonnees (latitude, longitude) VALUES (?, ?)";
        try (PreparedStatement coordStmt = conn.prepareStatement(insertCoordSQL, Statement.RETURN_GENERATED_KEYS)) {
            coordStmt.setDouble(1, coordonnees.getLatitude());
            coordStmt.setDouble(2, coordonnees.getLongitude());
            coordStmt.executeUpdate();

            ResultSet coordKeys = coordStmt.getGeneratedKeys();
            if (coordKeys.next()) {
                return coordKeys.getInt(1);
            } else {
                throw new SQLException("Échec de l'ajout des coordonnées, aucun ID généré.");
            }
        }
    }

    public List<Contact> listerTousLesContacts() {
        List<Contact> contacts = new ArrayList<>();
        String query = "SELECT * FROM contact";

        try (Connection conn = obtenirConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Contact contact = new Contact();
                contact.setId_contact(rs.getInt("id_contact"));
                contact.setNom(rs.getString("nom"));
                contact.setPrenom(rs.getString("prenom"));
                contact.setFavoris(rs.getBoolean("is_favoris"));

                // Charger les adresses associées
                List<Adresse> adresses = loadAdressesForContact(contact.getId_contact());
                contact.setAdresses(adresses);

                contacts.add(contact);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return contacts;
    }

    public List<Adresse> loadAdressesForContact(Integer contactId) {
        List<Adresse> adresses = new ArrayList<>();
        String query = "SELECT a.id_adresse, a.rue, a.ville, a.code_postal, a.pays, c.latitude, c.longitude " +
                "FROM adresse a " +
                "JOIN ContactAdresse ca ON a.id_adresse = ca.id_adresse " +
                "JOIN coordonnees c ON a.id_coordonnees = c.id_coordonnees " +
                "WHERE ca.id_contact = ?";
        try (Connection conn = obtenirConnexion();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, contactId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Adresse adresse = new Adresse();
                    adresse.setId_adresse(rs.getInt("id_adresse"));
                    adresse.setRue(rs.getString("rue"));
                    adresse.setVille(rs.getString("ville"));
                    adresse.setCodePostal(rs.getString("code_postal"));
                    adresse.setPays(rs.getString("pays"));

                    Coordonnees coord = new Coordonnees();
                    coord.setLatitude(rs.getDouble("latitude"));
                    coord.setLongitude(rs.getDouble("longitude"));
                    adresse.setCoordonnees(coord);

                    adresses.add(adresse);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des adresses : " + e.getMessage());
        }
        return adresses;
    }


    // méthode pour définir le statut favori d'un contact
    public void setFavoriteStatus(int contactId, boolean isFavorite) throws SQLException {
        String updateFavorisSQL = "UPDATE Contact SET is_favoris = ? WHERE id_contact = ?";
        try (Connection conn = obtenirConnexion();
             PreparedStatement pstmt = conn.prepareStatement(updateFavorisSQL)) {
            pstmt.setBoolean(1, isFavorite);
            pstmt.setInt(2, contactId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucun contact trouvé avec l'ID : " + contactId);
            }
        }
    }



    @Override
    public void supprimer(Integer id) {
        String sql = "DELETE FROM Contact WHERE id_contact = ?";
        try (Connection conn = obtenirConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Définir le paramètre de la requête (id_contact)
            pstmt.setInt(1, id);

            // Exécuter la requête de suppression
            int affectedRows = pstmt.executeUpdate();

            // Vérifier si la suppression a eu lieu
            if (affectedRows == 0) {
                throw new SQLException("La suppression a échoué, aucun contact trouvé avec l'ID : " + id);
            }

            System.out.println("Contact avec l'ID " + id + " supprimé avec succès.");

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du contact : " + e.getMessage());
            // Vous pouvez également re-lancer l'exception ou la gérer selon vos besoins
        }
    }


    @Override
    public void mettreAJour(Contact contact) {
        String sql = "UPDATE Contact SET nom = ?, prenom = ?, is_favoris = ? WHERE id_contact = ?";
        try (Connection conn = obtenirConnexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Définir les paramètres de la requête
            pstmt.setString(1, contact.getNom());
            pstmt.setString(2, contact.getPrenom());
            pstmt.setBoolean(3, contact.isFavoris());
            pstmt.setInt(4, contact.getId_contact());

            // Exécuter la requête de mise à jour
            int affectedRows = pstmt.executeUpdate();

            // Vérifier si la mise à jour a eu lieu
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour a échoué, aucun contact trouvé avec l'ID : " + contact.getId_contact());
            }

            System.out.println("Contact avec l'ID " + contact.getId_contact() + " mis à jour avec succès.");

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du contact : " + e.getMessage());
            // Vous pouvez également re-lancer l'exception ou la gérer selon vos besoins
        }
    }



    // méthode pour lister les contacts favoris
    public List<Contact> listerFavoris() {
        List<Contact> favoris = new ArrayList<>();
        String query = "SELECT * FROM contact WHERE is_favoris = 1";

        try (Connection conn = obtenirConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Contact contact = new Contact();
                contact.setId_contact(rs.getInt("id_contact"));
                contact.setNom(rs.getString("nom"));
                contact.setPrenom(rs.getString("prenom"));
                contact.setFavoris(rs.getBoolean("is_favoris"));

                // Charger les adresses associées
                List<Adresse> adresses = loadAdressesForContact(contact.getId_contact());
                contact.setAdresses(adresses);

                favoris.add(contact);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return favoris;
    }


    @Override
    public Contact trouverParId(Integer id) {
        String query = "SELECT * FROM contact WHERE id_contact = ?";
        try (Connection conn = obtenirConnexion();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Contact contact = new Contact();
                    contact.setId_contact(rs.getInt("id_contact"));
                    contact.setNom(rs.getString("nom"));
                    contact.setPrenom(rs.getString("prenom"));
                    contact.setFavoris(rs.getBoolean("is_favoris"));

                    // Charger les adresses associées
                    List<Adresse> adresses = loadAdressesForContact(contact.getId_contact());
                    contact.setAdresses(adresses);

                    return contact;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du contact par ID : " + e.getMessage());
        }
        return null;
    }
}