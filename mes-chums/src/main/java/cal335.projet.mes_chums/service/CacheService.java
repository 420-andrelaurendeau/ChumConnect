package cal335.projet.mes_chums.service;

import cal335.projet.mes_chums.dao.CoordonneesDAO;
import cal335.projet.mes_chums.modele.Contact;
import cal335.projet.mes_chums.modele.Coordonnees;

import java.util.*;

public class CacheService {
    private final Map<Contact, List<Coordonnees>> contactsFavoris = new HashMap<>();
    private final CoordonneesDAO coordonneesDAO;

    public CacheService(CoordonneesDAO coordonneesDAO) {
        this.coordonneesDAO = coordonneesDAO;
    }

    public void initialiserCache(List<Contact> favoris) {
        for (Contact contact : favoris) {
            List<Coordonnees> coordonneesList = coordonneesDAO.obtenirCoordonneesParContact(contact.getId_contact());
            contactsFavoris.put(contact, coordonneesList);
        }
    }

    public Contact trouverFavori(Integer id) {
        for (Contact contact : contactsFavoris.keySet()) {
            if (contact.getId_contact().equals(id)) {
                return contact;
            }
        }
        return null;
    }

    public void ajouterFavori(Contact contact, List<Coordonnees> coordonnees) {
        contactsFavoris.put(contact, coordonnees);
    }

    public void supprimerFavori(Integer id) {
        contactsFavoris.keySet().removeIf(contact -> contact.getId_contact().equals(id));
    }

    public Map<Contact, List<Coordonnees>> getContactsFavoris() {
        return Collections.unmodifiableMap(contactsFavoris);
    }
}
