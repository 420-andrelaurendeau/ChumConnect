package cal335.projet.mes_chums;

import cal335.projet.mes_chums.controleur.ContactControleur;
import cal335.projet.mes_chums.dao.ContactDAO;
import cal335.projet.mes_chums.dao.CoordonneesDAO;
import cal335.projet.mes_chums.mapper.ContactMapper;
import cal335.projet.mes_chums.modele.Adresse;
import cal335.projet.mes_chums.modele.Contact;
import cal335.projet.mes_chums.modele.Coordonnees;
import cal335.projet.mes_chums.service.CacheService;
import cal335.projet.mes_chums.service.ContactService;
import cal335.projet.mes_chums.service.OpenWeatherMapService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        ContactDAO contactDAO = new ContactDAO();
        CoordonneesDAO coordonneesDAO = new CoordonneesDAO(contactDAO);
        CacheService cacheService = new CacheService(coordonneesDAO);
        ContactMapper contactMapper = new ContactMapper();
        OpenWeatherMapService weatherService = new OpenWeatherMapService();
        ContactService contactService = new ContactService(contactDAO, cacheService, weatherService, contactMapper);

        try {
            Connection connexion = contactDAO.obtenirConnexion();
            System.out.println("Connexion réussie !");
            connexion.close();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            return;
        }

        try {
            List<Contact> favoris = contactDAO.trouverFavoris();
            cacheService.initialiserCache(favoris);
            System.out.println("Cache initialisé avec les favoris.");

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
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation du cache : " + e.getMessage());
        }

        List<Adresse> adresses = contactDAO.loadAdressesForContact(6);
        System.out.println("Adresses pour le contact 1 : " + adresses);
        // Démarrer le serveur HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/contacts", new ContactControleur(contactService));
        server.start();
        System.out.println("Serveur démarré avec succès sur le port 8080 !");
    }
}
