package cal335.projet.mes_chums.controleur;


import cal335.projet.mes_chums.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class ContactControleur implements HttpHandler {
    //private ContactService contactService = new ContactService();
    //public TacheControleur(TacheService tache){this.tacheService = tache;}
    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void handle(HttpExchange exchange) throws IOException {


        String method = exchange.getRequestMethod();

        if (method.equalsIgnoreCase("GET")){
           // String reponse = objectMapper.writeValueAsString(contactService.getListeDesContacts());
            //exchange.sendResponseHeaders(200,reponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            //os.write(reponse.getBytes());
            os.close();
        } /*else if (method.equalsIgnoreCase("POST")) {
            Tache tache = objectMapper.readValue(exchange.getRequestBody(), Tache.class);
            tacheService.addTache(tache);
            String reponse = "Tâche créée avec succès";
            exchange.sendResponseHeaders(201,reponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(reponse.getBytes());
            os.close();
        }
        */
    }
}

