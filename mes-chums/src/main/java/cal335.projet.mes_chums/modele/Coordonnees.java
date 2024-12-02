package cal335.projet.mes_chums.modele;

public class Coordonnees {
    private double latitude;
    private double longitude;
    private Integer id_coordonnees;


    public Coordonnees() {
    }
    public Coordonnees(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setId_coordonnees(Integer id_coordonnees) {
        this.id_coordonnees = id_coordonnees;
    }

}
