package cal335.projet.mes_chums.modele;

import java.util.ArrayList;
import java.util.List;

public class Contact {
    private Integer id_contact;
    private String nom;
    private String prenom;
    private boolean favoris;
    private List<Adresse> adresses;

    public Contact() {
        this.adresses = new ArrayList<>();
    }

    public Contact(String nom, String prenom, boolean favoris) {
        this.nom = nom;
        this.prenom = prenom;
        this.favoris = favoris;
        this.adresses = new ArrayList<>();
    }

    // Getters et Setters
    public Integer getId_contact() {
        return id_contact;
    }

    public void setId_contact(Integer id_contact) {
        this.id_contact = id_contact;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public boolean isFavoris() {
        return favoris;
    }

    public void setFavoris(boolean favoris) {
        this.favoris = favoris;
    }

    public List<Adresse> getAdresses() {
        return adresses;
    }

    public void setAdresses(List<Adresse> adresses) {
        this.adresses = adresses;
    }

    public List<Coordonnees> getAdressesCoordonnees() {
        List<Coordonnees> coords = new ArrayList<>();
        for (Adresse adresse : adresses) {
            coords.add(adresse.getCoordonnees());
        }
        return coords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        return id_contact != null ? id_contact.equals(contact.id_contact) : contact.id_contact == null;
    }

    @Override
    public int hashCode() {
        return id_contact != null ? id_contact.hashCode() : 0;
    }
}
