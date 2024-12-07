package cal335.projet.mes_chums.modele;



public class Adresse {
    private Integer id_adresse;
    private String rue;
    private String ville;
    private String codePostal;
    private String pays;
    private Coordonnees coordonnees;

    public Adresse() {
    }

    public Adresse(Integer id_adresse, String rue, String ville, String codePostal, String pays, Coordonnees coordonnees) {
        this.id_adresse = id_adresse;
        this.rue = rue;
        this.ville = ville;
        this.codePostal = codePostal;
        this.pays = pays;
        this.coordonnees = coordonnees;
    }

    public Integer getId_adresse() {
        return id_adresse;
    }

    public String getRue() {
        return rue;
    }

    public String getVille() {
        return ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public String getPays() {
        return pays;
    }

    public Coordonnees getCoordonnees() {
        return coordonnees;
    }

    public void setId_adresse(Integer id_adresse) {
        this.id_adresse = id_adresse;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public void setCoordonnees(Coordonnees coordonnees) {
        this.coordonnees = coordonnees;
    }


    public String toString() {
        return "Adresse{" +
                "id_adresse=" + id_adresse +
                ", rue='" + rue + '\'' +
                ", ville='" + ville + '\'' +
                ", codePostal='" + codePostal + '\'' +
                ", pays='" + pays + '\'' +
                ", coordonnees=" + coordonnees +
                '}';
    }
}
