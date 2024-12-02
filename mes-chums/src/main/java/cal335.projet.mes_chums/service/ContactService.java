package cal335.projet.mes_chums.service;


import cal335.projet.mes_chums.modele.Contact;
import cal335.projet.mes_chums.modele.Coordonnees;

import java.util.*;

public class ContactService {
    private CacheService cache;

    public ContactService(CacheService cache) {
        this.cache = cache;
    }

    public List<Contact> rechercherContactsProches(Coordonnees coordonnees, double rayon) {
        List<Contact> resultat = new ArrayList<>();
        /*for (Contact contact : cache.getTousLesContacts()) {
            double distance = CalculateurDistance.calculerDistance(coordonnees, contact.getCoordonnees());
            if (distance < rayon) {
                resultat.add(contact);
            }
        }
        return resultat;
        }
         */
        return resultat;

    }

}
