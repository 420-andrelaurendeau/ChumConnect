package cal335.projet.mes_chums.mapper;

import cal335.projet.mes_chums.dto.AdresseDTO;
import cal335.projet.mes_chums.dto.ContactDTO;
import cal335.projet.mes_chums.modele.Adresse;
import cal335.projet.mes_chums.modele.Contact;
import cal335.projet.mes_chums.modele.Coordonnees;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Convertir ContactDTO en Contact sans les adresses
    public Contact dtoToContactBasic(ContactDTO dto) throws IOException {
        Contact contact = new Contact();
        contact.setNom(dto.getNom());
        contact.setPrenom(dto.getPrenom());
        contact.setFavoris(dto.isFavoris());
        // Les adresses seront ajoutées séparément
        return contact;
    }

    // Convertir Contact en ContactDTO
    public ContactDTO contactToDto(Contact contact) {
        ContactDTO dto = new ContactDTO();
        dto.setNom(contact.getNom());
        dto.setPrenom(contact.getPrenom());
        dto.setFavoris(contact.isFavoris());

        List<AdresseDTO> adresseDTOs = new ArrayList<>();
        for (Adresse adresse : contact.getAdresses()) {
            AdresseDTO adresseDTO = new AdresseDTO();
            adresseDTO.setRue(adresse.getRue());
            adresseDTO.setVille(adresse.getVille());
            adresseDTO.setCodePostal(adresse.getCodePostal());
            adresseDTO.setPays(adresse.getPays());
            adresseDTO.setCoordonnees(adresse.getCoordonnees());
            adresseDTOs.add(adresseDTO);
        }
        dto.setAdresses(adresseDTOs);

        return dto;
    }

    // Convertir AdresseDTO en Adresse
    public Adresse dtoToAdresse(AdresseDTO dto) throws IOException {
        Adresse adresse = new Adresse();
        adresse.setRue(dto.getRue());
        adresse.setVille(dto.getVille());
        adresse.setCodePostal(dto.getCodePostal());
        adresse.setPays(dto.getPays());
        adresse.setCoordonnees(dto.getCoordonnees());

        return adresse;
    }

    // Convertir une liste de Contacts en une liste de ContactDTOs
    public List<ContactDTO> contactsToDto(List<Contact> contacts) {
        List<ContactDTO> dtoList = new ArrayList<>();
        for (Contact contact : contacts) {
            dtoList.add(contactToDto(contact));
        }
        return dtoList;
    }
}
