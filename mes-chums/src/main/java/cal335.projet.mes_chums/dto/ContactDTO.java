package cal335.projet.mes_chums.dto;



import java.util.ArrayList;
import java.util.List;

public class ContactDTO {
    private String nom;
    private String prenom;
    private boolean favoris;
    private List<AdresseDTO> adresses;

    // Getters et Setters
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

    public List<AdresseDTO> getAdresses() {
        return adresses;
    }

    public void setAdresses(List<AdresseDTO> adresses) {
        this.adresses = (adresses != null) ? adresses : new ArrayList<>(); // Gestion des valeurs null
    }
}

