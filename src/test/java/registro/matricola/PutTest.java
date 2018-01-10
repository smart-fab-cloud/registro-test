package registro.matricola;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import jdk.net.SocketFlow;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class PutTest {
    
    private final WebTarget reg;
    private final JSONObject stud;
    private final String matricola;
    
    public PutTest() {
        // Creazione del client e connessione al servizio
        Client cli = ClientBuilder.newClient();
        reg = cli.target("http://localhost:50005/registro");
        // e inizializzazione dati test
        stud = new JSONObject();
        stud.put("matricola", 5555);
        stud.put("cognome", "Bianchi");
        stud.put("nome", "Bruno");
        matricola = String.valueOf(stud.get("matricola"));
        
    }
    
    @Before
    public void aggiuntaStudente() {
        reg.request().post(Entity.entity(stud.toJSONString(),MediaType.APPLICATION_JSON));
    }
    
    @Test
    public void testPutOk() throws ParseException {
        // Voto da aggiungere
        int voto = 8;
        String materia = "matematica";
        
        // Aggiunta voto
        Response rPut = reg.path(matricola)
                            .queryParam("materia", materia)
                            .queryParam("voto", voto)
                            .request()
                            .put(Entity.entity("", MediaType.TEXT_PLAIN));
        
        // Recupera i voti dello studente (aggiornati)
        Response rGet = reg.path(matricola).request().get();
        JSONParser p  = new JSONParser();
        JSONObject statoStud = (JSONObject) p.parse(rGet.readEntity(String.class));
        JSONObject voti = (JSONObject) statoStud.get("voti");
        JSONArray votiMateria = (JSONArray) voti.get(materia);

        // Verifica che l'aggiornamento sia stato effettuato
        // (Nota: inizialmente lo studente non ha alcun voto registrato)
        assertEquals(Status.OK.getStatusCode(), rPut.getStatus());
        Long votoAggiunto = new Long(voto);
        assertTrue(votiMateria.contains(votoAggiunto));
    }
    
    @Test
    public void testPutBadRequest() {
        // Tentativo di aggiunta voto a studente con matricola non-positiva
        Response rPut0 = reg.path("0")
                            .queryParam("materia", "matematica")
                            .queryParam("voto", 8)
                            .request()
                            .put(Entity.entity("", MediaType.TEXT_PLAIN));
        
        // Tentativo di aggiunta voto senza specificare "materia"
        Response rPut1 = reg.path(matricola)
                            .queryParam("voto", 8)
                            .request()
                            .put(Entity.entity("", MediaType.TEXT_PLAIN));
        
        // Tentativo di aggiunta voto senza specificare "voto"
        Response rPut2 = reg.path(matricola)
                            .queryParam("materia", "matematica")
                            .request()
                            .put(Entity.entity("", MediaType.TEXT_PLAIN));
        // Tentativo di aggiunta voto per materia non supportata
        Response rPut3 = reg.path(matricola)
                            .queryParam("materia", "nientologia")
                            .queryParam("voto", 8)
                            .request()
                            .put(Entity.entity("", MediaType.TEXT_PLAIN));
        // Tentativo di aggiunta voto negativo
        Response rPut4 = reg.path(matricola)
                            .queryParam("materia", "matematica")
                            .queryParam("voto", -1)
                            .request()
                            .put(Entity.entity("", MediaType.TEXT_PLAIN));
        
        // Verifica di ottenimento "400 Bad Request"
        assertEquals(Status.BAD_REQUEST.getStatusCode(), rPut0.getStatus());
        assertEquals(Status.BAD_REQUEST.getStatusCode(), rPut1.getStatus());
        assertEquals(Status.BAD_REQUEST.getStatusCode(), rPut2.getStatus());
        assertEquals(Status.BAD_REQUEST.getStatusCode(), rPut3.getStatus());
        assertEquals(Status.BAD_REQUEST.getStatusCode(), rPut4.getStatus());
    }
    
    @Test
    public void testPutNotFound() {
        // Tentativo di aggiunta voto a studente inesistente
        Response rPut = reg.path(matricola + "1")
                            .queryParam("materia", "matematica")
                            .queryParam("voto", 8)
                            .request()
                            .put(Entity.entity("", MediaType.TEXT_PLAIN));
        
        // Verifica di ottenimento di "404 Not Found"
        assertEquals(Status.NOT_FOUND.getStatusCode(), rPut.getStatus());
    }
    
    @After
    public void eliminazioneStudente() {
        reg.path(matricola).request().delete();
    }
}
