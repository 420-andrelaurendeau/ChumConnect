package cal335.projet.mes_chums.modele;


import java.util.List;

public class Contact {
    private Integer id_contact;
    private String nom;
    private String prenom;
    private boolean isFavoris;
    private List<Adresse> adresses;

    public Contact() {
    }

    public Contact(String nom, String prenom, boolean isFavoris) {
        this.id_contact = id_contact;
        this.nom = nom;
        this.prenom = prenom;
        this.isFavoris = isFavoris;
        this.adresses = adresses;
    }

    public Integer getId_contact() {
        return id_contact;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public boolean isFavoris() {
        return isFavoris;
    }

    public List<Adresse> getAdresses() {
        return adresses;
    }

    public void setId_contact(Integer id_contact) {
        this.id_contact = id_contact;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setFavoris(boolean isFavoris) {
        this.isFavoris = isFavoris;
    }

    public void setAdresses(List<Adresse> adresses) {
        this.adresses = adresses;
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

