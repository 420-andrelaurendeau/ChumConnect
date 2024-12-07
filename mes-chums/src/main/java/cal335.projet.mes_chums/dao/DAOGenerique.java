package cal335.projet.mes_chums.dao;

import cal335.projet.mes_chums.modele.Contact;

import java.sql.SQLException;

public interface DAOGenerique<T> {
    Contact ajouter(T objet) throws SQLException;
    void supprimer(Integer id);
    void mettreAJour(T objet);
    T trouverParId(Integer id);
}
