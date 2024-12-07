package cal335.projet.mes_chums.service;

import cal335.projet.mes_chums.dao.ContactDAO;
import cal335.projet.mes_chums.dto.AdresseDTO;
import cal335.projet.mes_chums.dto.ContactDTO;
import cal335.projet.mes_chums.modele.Adresse;
import cal335.projet.mes_chums.modele.CalculateurDistance;
import cal335.projet.mes_chums.modele.Contact;
import cal335.projet.mes_chums.modele.Coordonnees;
import cal335.projet.mes_chums.mapper.ContactMapper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactService {
    private final ContactDAO contactDAO;
    private final CacheService cacheService;
    private final OpenWeatherMapService weatherService;
    private final ContactMapper contactMapper;

    public ContactService(ContactDAO contactDAO, CacheService cacheService, OpenWeatherMapService weatherService, ContactMapper contactMapper) {
        this.contactDAO = contactDAO;
        this.cacheService = cacheService;
        this.weatherService = weatherService;
        this.contactMapper = contactMapper;
    }

    // Getter pour ContactMapper
    public ContactMapper getContactMapper() {
        return contactMapper;
    }

    public ContactDTO ajouterContact(ContactDTO dtoContact) throws IOException, SQLException {
        System.out.println("Début de l'ajout du contact : " + dtoContact.getNom() + " " + dtoContact.getPrenom());

        // Convertir DTO en Entité sans les adresses
        Contact contact = contactMapper.dtoToContactBasic(dtoContact);
        System.out.println("Contact de base converti en entité.");

        // Ajouter le contact à la base de données
        Contact contactAjoute = contactDAO.ajouter(contact);
        System.out.println("Contact ajouté à la base de données avec ID : " + contactAjoute.getId_contact());

        // Ajouter les adresses avec gestion des coordonnées
        List<AdresseDTO> adressesDTO = dtoContact.getAdresses();
        if (adressesDTO != null && !adressesDTO.isEmpty()) {
            for (AdresseDTO adresseDTO : dtoContact.getAdresses()) {
                String ville = adresseDTO.getVille() ;

                // Appel à OpenWeatherMapService pour obtenir les coordonnées
                double[] coordonneesArray = weatherService.getCoordoneByCity(ville);
                if (coordonneesArray[0] == 0 && coordonneesArray[1] == 0) {
                    System.out.println("Impossible d'obtenir les coordonnées pour la ville : " + ville);
                    continue; // Passer à la prochaine adresse si aucune coordonnée n'est trouvée
                }

                // Mapper les coordonnées en objet Coordonnees
                Coordonnees coordonnees = new Coordonnees(coordonneesArray[0], coordonneesArray[1]);
                adresseDTO.setCoordonnees(coordonnees);


                // Ajouter l'adresse au contact via le DAO
                contactDAO.ajouterAdresse(contactAjoute.getId_contact(), contactMapper.dtoToAdresse(adresseDTO));
                System.out.println("Adresse ajoutée : " + adresseDTO.getRue() + ", " + adresseDTO.getVille());
            }
        } else {
            System.out.println("Aucune adresse à ajouter pour ce contact.");
        }
        // Si le contact est favori, l'ajouter au cache
        if (contactAjoute.isFavoris()) {
            cacheService.ajouterFavori(contactAjoute, contactAjoute.getAdressesCoordonnees());
            System.out.println("Contact ajouté au cache des favoris.");
        }

        // Convertir l'entité en DTO pour la réponse
        ContactDTO dtoRetour = contactMapper.contactToDto(contactAjoute);
        System.out.println("Contact converti en DTO pour la réponse.");

        return dtoRetour;
    }

    public void ajouterAdresse(int contactId, AdresseDTO adresseDTO) throws IOException, SQLException {

        System.out.println("Début de l'ajout de l'adresse : " + adresseDTO.getRue() + ", " + adresseDTO.getVille());

        // Appel à OpenWeatherMapService pour obtenir les coordonnées
        String ville = adresseDTO.getVille() ;
        double[] coordonneesArray = weatherService.getCoordoneByCity(ville);
        if (coordonneesArray[0] == 0 && coordonneesArray[1] == 0) {
            System.out.println("Impossible d'obtenir les coordonnées pour la ville : " + ville);
            throw new IOException("Coordonnées introuvables pour la ville : " + ville);
        }






        // Mapper les coordonnées en objet Coordonnees
        Coordonnees coordonnees = new Coordonnees(coordonneesArray[0], coordonneesArray[1]);
        adresseDTO.setCoordonnees(coordonnees);

        // Convertir DTO en entité Adresse
        Adresse adresse = contactMapper.dtoToAdresse(adresseDTO);
        System.out.println("Adresse convertie en entité.");

        // Ajouter l'adresse au contact via le DAO
        contactDAO.ajouterAdresse(contactId, adresse);
        System.out.println("Adresse ajoutée à la base de données.");

        // Récupérer le contact pour vérifier s'il est favori
        Contact contact = contactDAO.trouverParId(contactId);
        if (contact != null && contact.isFavoris()) {
            cacheService.ajouterFavori(contact, contact.getAdressesCoordonnees());
            System.out.println("Cache mis à jour pour le contact favori.");
        }
    }


    // méthode pour marquer un contact comme favori
    public void marquerFavori(int contactId) throws SQLException {
        System.out.println("Début du marquage du contact ID " + contactId + " comme favori.");
        contactDAO.setFavoriteStatus(contactId, true);
        System.out.println("Contact ID " + contactId + " marqué comme favori.");

        // Mettre à jour le cache si nécessaire
        Contact contact = contactDAO.trouverParId(contactId);
        if (contact != null) {
            if (contact.isFavoris()) {
                cacheService.ajouterFavori(contact, contact.getAdressesCoordonnees());
                System.out.println("Contact ajouté au cache des favoris.");
            }
        }
    }

    // méthode pour retirer un contact des favoris
    public void retirerFavori(int contactId) throws SQLException {
        System.out.println("Début du retrait du contact ID " + contactId + " des favoris.");
        contactDAO.setFavoriteStatus(contactId, false);
        System.out.println("Contact ID " + contactId + " retiré des favoris.");

        // Mettre à jour le cache si nécessaire
        Contact contact = contactDAO.trouverParId(contactId);
        if (contact != null) {
            if (!contact.isFavoris()) {
                cacheService.supprimerFavori(contactId);
                System.out.println("Contact retiré du cache des favoris.");
            }
        }
    }


    public List<ContactDTO> rechercherProximite(String ville, double rayon) throws IOException, SQLException {
        System.out.println("Début de la recherche à proximité pour la ville : " + ville + " avec un rayon de " + rayon + " km.");

        // Obtenir les coordonnées de la ville de référence
        double[] coordVille = weatherService.getCoordoneByCity(ville);
        if (coordVille[0] == 0 && coordVille[1] == 0) {
            throw new IOException("Coordonnées introuvables pour la ville : " + ville);
        }
        Coordonnees coordonneesVille = new Coordonnees(coordVille[0], coordVille[1]);
        System.out.println("Coordonnées de " + ville + " : Latitude=" + coordonneesVille.getLatitude() + ", Longitude=" + coordonneesVille.getLongitude());

        // Initialiser la liste des résultats
        List<ContactDTO> listeResultats = new ArrayList<>();

        // Récupérer les contacts favoris depuis le cache
        Map<Contact, List<Coordonnees>> contactsFavoris = cacheService.getContactsFavoris();
        System.out.println("Nombre de contacts favoris dans le cache : " + contactsFavoris.size());

        // Parcourir chaque contact dans le cache
        for (Map.Entry<Contact, List<Coordonnees>> entry : contactsFavoris.entrySet()) {
            Contact contact = entry.getKey();
            List<Coordonnees> coordonneesList = entry.getValue();

            // Vérifier si au moins une adresse du contact est dans le rayon spécifié
            for (Coordonnees coordContact : coordonneesList) {
                double distance = CalculateurDistance.calculerDistance(coordonneesVille, coordContact);
                System.out.println("Distance entre " + ville + " et " + contact.getNom() + " " + contact.getPrenom() + " : " + distance + " km.");
                if (distance <= rayon) {
                    // Convertir le contact en DTO
                    ContactDTO contactDTO = contactMapper.contactToDto(contact);
                    // Ajouter le contact à la liste des résultats
                    listeResultats.add(contactDTO);
                    // Une fois qu'une adresse correspond, passer au contact suivant
                    break;
                }
            }
        }

        System.out.println("Recherche à proximité terminée. Nombre de résultats trouvés : " + listeResultats.size());
        return listeResultats;
    }



    // méthode pour trouver un contact par ID
    public ContactDTO trouverContactParId(int contactId) throws SQLException, IOException {
        Contact contact = contactDAO.trouverParId(contactId);
        if (contact == null) {
            return null;
        }
        return contactMapper.contactToDto(contact);
    }

    public List<Contact> getListeDesContacts() {
        return contactDAO.listerTousLesContacts();
    }

    public List<Contact> getFavoris() {
        return contactDAO.listerFavoris();
    }
}
