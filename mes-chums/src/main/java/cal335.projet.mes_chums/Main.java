package cal335.projet.mes_chums;

import cal335.projet.mes_chums.controleur.ContactControleur;
import cal335.projet.mes_chums.dao.ContactDAO;
import cal335.projet.mes_chums.modele.Contact;
import cal335.projet.mes_chums.modele.Coordonnees;
import cal335.projet.mes_chums.service.CacheService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        try {

            ContactDAO contactDAO = new ContactDAO();
            //Contact contactMokhtar = new Contact("Mokhtar", "Moussa", true);
            Connection connexion = contactDAO.obtenirConnexion();
            System.out.println("Connexion réussie !");
            //contactDAO.ajouter(contactMokhtar);
            CacheService cacheService = new CacheService();
            List<Contact> favoris = contactDAO.trouverFavoris();
            cacheService.initialiserCache(favoris);

            // Processing each favorite
            // Traitement de chaque favori
            for (Contact contact : favoris) {
                Contact favoriteContact = cacheService.trouverFavori(contact.getId_contact());
                if (favoriteContact != null) {
                    System.out.println("Contact Favori: " + favoriteContact.getNom() + " " + favoriteContact.getPrenom());
                    List<Coordonnees> coordonneesList = cacheService.getContactsFavoris().get(favoriteContact);
                    for (Coordonnees coord : coordonneesList) {
                        System.out.println("Coordonnées: Latitude=" + coord.getLatitude() + ", Longitude=" + coord.getLongitude());
                    }
                }
            }

            connexion.close();

        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/contacts", new ContactControleur());
        server.start();
        System.out.println("Serveur démarré avec succès !");
    }
}