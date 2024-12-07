package cal335.projet.mes_chums.controleur;

import cal335.projet.mes_chums.dto.AdresseDTO;
import cal335.projet.mes_chums.dto.ContactDTO;
import cal335.projet.mes_chums.modele.Contact;
import cal335.projet.mes_chums.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ContactControleur implements HttpHandler {
    private final ContactService contactService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ContactControleur(ContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        System.out.println("Requête reçue : " + method + " " + exchange.getRequestURI());

        if (method.equalsIgnoreCase("POST")) {
            if (path.matches("/contacts/\\d+/adresses")) {
                // Ajouter une adresse à un contact existant
                ajouterAdresse(exchange, path);
            } else if (path.equals("/contacts")) {
                // Ajouter un nouveau contact
                ajouterContact(exchange);
            } else if (path.matches("/contacts/\\d+/favoris")) {
                // Marquer un contact comme favori
                marquerFavori(exchange, path);
            } else {
                // Path non supporté
                methodeNonAutorisee(exchange, method);
            }
        } else if (method.equalsIgnoreCase("DELETE")) {
            if (path.matches("/contacts/\\d+/favoris")) {
                // Retirer un contact des favoris
                retirerFavori(exchange, path);
            } else {
                // Path non supporté
                methodeNonAutorisee(exchange, method);
            }

        } else if (method.equalsIgnoreCase("GET")) {
            if (path.equals("/contacts/proximite")) {
                rechercherProximite(exchange);
            } else if (path.equals("/contacts")) {
                listerContacts(exchange);
            } else if (path.matches("/contacts/\\d+")) {
                // Rechercher un contact par ID
                rechercherContactParId(exchange, path);
            } else if (path.equals("/contacts/favoris")) {
                // Récupérer la liste des contacts favoris
                listerFavoris(exchange);
            } else {
                // Path non supporté
                methodeNonAutorisee(exchange, method);
            }
        } else {
            // Méthode non supportée
            methodeNonAutorisee(exchange, method);
        }
    }

    private void ajouterContact(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            ContactDTO dtoContact = objectMapper.readValue(is, ContactDTO.class);
            System.out.println("ContactDTO désérialisé : " + dtoContact.getNom() + " " + dtoContact.getPrenom());

            ContactDTO contactAjoute = contactService.ajouterContact(dtoContact);
            String reponse = objectMapper.writeValueAsString(contactAjoute);

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, reponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(reponse.getBytes());
            os.close();

            System.out.println("Contact ajouté avec succès : " + contactAjoute.getNom() + " " + contactAjoute.getPrenom());
        } catch (Exception e) {
            e.printStackTrace();
            String erreur = "Erreur lors de l'ajout du contact : " + e.getMessage();
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(500, erreur.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(erreur.getBytes());
            os.close();
        }
    }

    private void ajouterAdresse(HttpExchange exchange, String path) throws IOException {
        // Extraire l'ID du contact depuis le chemin
        String[] segments = path.split("/");
        if (segments.length != 4) { // ['', 'contacts', '{id}', 'adresses']
            methodeNonAutorisee(exchange, "POST");
            return;
        }

        int contactId;
        try {
            contactId = Integer.parseInt(segments[2]);
        } catch (NumberFormatException e) {
            String erreur = "ID de contact invalide.";
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(400, erreur.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(erreur.getBytes());
            os.close();
            return;
        }

        try {
            InputStream is = exchange.getRequestBody();
            AdresseDTO adresseDTO = objectMapper.readValue(is, AdresseDTO.class);
            System.out.println("AdresseDTO désérialisé : " + adresseDTO.getRue() + ", " + adresseDTO.getVille());

            contactService.ajouterAdresse(contactId, adresseDTO);
            String reponse = "Adresse ajoutée avec succès au contact ID " + contactId;

            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(201, reponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(reponse.getBytes());
            os.close();

            System.out.println("Adresse ajoutée avec succès au contact ID " + contactId);
        } catch (Exception e) {
            e.printStackTrace();
            String erreur = "Erreur lors de l'ajout de l'adresse : " + e.getMessage();
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(500, erreur.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(erreur.getBytes());
            os.close();
        }
    }



    private void rechercherContactParId(HttpExchange exchange, String path) throws IOException {
        // Extraire l'ID du contact depuis le chemin
        String[] segments = path.split("/");
        if (segments.length != 3) { // ['', 'contacts', '{id}']
            methodeNonAutorisee(exchange, "GET");
            return;
        }

        int contactId;
        try {
            contactId = Integer.parseInt(segments[2]);
        } catch (NumberFormatException e) {
            String erreur = "ID de contact invalide.";
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(400, erreur.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(erreur.getBytes());
            os.close();
            return;
        }

        try {
            ContactDTO contactDTO = contactService.trouverContactParId(contactId);
            if (contactDTO == null) {
                String erreur = "Contact avec ID " + contactId + " non trouvé.";
                exchange.getResponseHeaders().add("Content-Type", "text/plain");
                exchange.sendResponseHeaders(404, erreur.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(erreur.getBytes());
                os.close();
                return;
            }

            String reponse = objectMapper.writeValueAsString(contactDTO);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, reponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(reponse.getBytes());
            os.close();

            System.out.println("Contact ID " + contactId + " récupéré avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            String erreur = "Erreur lors de la récupération du contact : " + e.getMessage();
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(500, erreur.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(erreur.getBytes());
            os.close();
        }
    }


    private void listerFavoris(HttpExchange exchange) throws IOException {
        try {
            List<Contact> favoris = contactService.getFavoris();
            List<ContactDTO> dtoList = contactService.getContactMapper().contactsToDto(favoris); // Conversion en DTOs
            String reponse = objectMapper.writeValueAsString(dtoList);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, reponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(reponse.getBytes());
            os.close();
            System.out.println("Liste des favoris envoyée.");
        } catch (Exception e) {
            e.printStackTrace();
            String erreur = "Erreur lors de la récupération des favoris : " + e.getMessage();
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(500, erreur.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(erreur.getBytes());
            os.close();
        }
    }




    private void listerContacts(HttpExchange exchange) throws IOException {
        try {
            List<Contact> contacts = contactService.getListeDesContacts();
            List<ContactDTO> dtoList = contactService.getContactMapper().contactsToDto(contacts); // Conversion en DTOs
            String reponse = objectMapper.writeValueAsString(dtoList);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, reponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(reponse.getBytes());
            os.close();
            System.out.println("Liste des contacts envoyée.");
        } catch (Exception e) {
            e.printStackTrace();
            String erreur = "Erreur lors de la récupération des contacts : " + e.getMessage();
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(500, erreur.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(erreur.getBytes());
            os.close();
        }
    }


    private void marquerFavori(HttpExchange exchange, String path) throws IOException {
        // Extraire l'ID du contact depuis le chemin
        String[] segments = path.split("/");
        if (segments.length != 4) { // ['', 'contacts', '{id}', 'favoris']
            methodeNonAutorisee(exchange, "POST");
            return;
        }

        int contactId;
        try {
            contactId = Integer.parseInt(segments[2]);
        } catch (NumberFormatException e) {
            String erreur = "ID de contact invalide.";
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(400, erreur.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(erreur.getBytes());
            os.close();
            return;
        }

        try {
            contactService.marquerFavori(contactId);
            String reponse = "Contact ID " + contactId + " marqué comme favori.";

            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, reponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(reponse.getBytes());
            os.close();

            System.out.println("Contact ID " + contactId + " marqué comme favori.");
        } catch (Exception e) {
            e.printStackTrace();
            String erreur = "Erreur lors du marquage comme favori : " + e.getMessage();
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(500, erreur.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(erreur.getBytes());
            os.close();
        }
    }


    private void retirerFavori(HttpExchange exchange, String path) throws IOException {
        // Extraire l'ID du contact depuis le chemin
        String[] segments = path.split("/");
        if (segments.length != 4) { // ['', 'contacts', '{id}', 'favoris']
            methodeNonAutorisee(exchange, "DELETE");
            return;
        }

        int contactId;
        try {
            contactId = Integer.parseInt(segments[2]);
        } catch (NumberFormatException e) {
            String erreur = "ID de contact invalide.";
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(400, erreur.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(erreur.getBytes());
            os.close();
            return;
        }

        try {
            contactService.retirerFavori(contactId);
            String reponse = "Contact ID " + contactId + " retiré des favoris.";

            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, reponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(reponse.getBytes());
            os.close();

            System.out.println("Contact ID " + contactId + " retiré des favoris.");
        } catch (Exception e) {
            e.printStackTrace();
            String erreur = "Erreur lors du retrait des favoris : " + e.getMessage();
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(500, erreur.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(erreur.getBytes());
            os.close();
        }
    }


    // Nouvelle méthode pour gérer la recherche à proximité
    private void rechercherProximite(HttpExchange exchange) throws IOException {
        try {
            // Extraire les paramètres de requête
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            String ville = params.get("ville");
            String rayonStr = params.get("rayon");

            if (ville == null || rayonStr == null) {
                String erreur = "Paramètres 'ville' et 'rayon' requis.";
                exchange.getResponseHeaders().add("Content-Type", "text/plain");
                exchange.sendResponseHeaders(400, erreur.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(erreur.getBytes());
                os.close();
                return;
            }

            double rayon;
            try {
                rayon = Double.parseDouble(rayonStr);
                if (rayon <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                String erreur = "Le paramètre 'rayon' doit être un nombre positif.";
                exchange.getResponseHeaders().add("Content-Type", "text/plain");
                exchange.sendResponseHeaders(400, erreur.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(erreur.getBytes());
                os.close();
                return;
            }

            // Appeler le service pour effectuer la recherche à proximité
            List<ContactDTO> resultats = contactService.rechercherProximite(ville, rayon);

            // Sérialiser la liste des contacts en JSON
            String reponse = objectMapper.writeValueAsString(resultats);

            // Envoyer la réponse
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, reponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(reponse.getBytes());
            os.close();

            System.out.println("Recherche à proximité effectuée avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            String erreur = "Erreur lors de la recherche à proximité : " + e.getMessage();
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(500, erreur.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(erreur.getBytes());
            os.close();
        }
    }

    // Méthode utilitaire pour parser les paramètres de requête
    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new java.util.HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }



    private void methodeNonAutorisee(HttpExchange exchange, String method) throws IOException {
        String methodeNonAutorisee = "Méthode non autorisée.";
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        exchange.sendResponseHeaders(405, methodeNonAutorisee.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(methodeNonAutorisee.getBytes());
        os.close();
        System.out.println("Méthode non autorisée : " + method);
    }

    private void methodeNonAutorisee(HttpExchange exchange, int method) throws IOException {
        String methodeNonAutorisee = "Méthode non autorisée.";
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        exchange.sendResponseHeaders(405, methodeNonAutorisee.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(methodeNonAutorisee.getBytes());
        os.close();
        System.out.println("Méthode non autorisée : " + method);
    }
}
