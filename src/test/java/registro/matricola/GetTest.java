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

public class GetTest {
    
    private final WebTarget reg;
    private final JSONObject stud;
    private final String matricola;
    
    public GetTest() {
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
    public void testGetOk() throws ParseException {
        // Reperimento stato studente
        Response rGet = reg.path(matricola).request().get();
        JSONParser p  = new JSONParser();
        JSONObject statoStud = (JSONObject) p.parse(rGet.readEntity(String.class));
        
        // Verifica che la risposta sia "200 Ok"
        assertEquals(Status.OK.getStatusCode(), rGet.getStatus());
        // Verifica che lo stato sia corretto
        assertEquals(stud.get("cognome"), statoStud.get("cognome"));
        assertEquals(stud.get("nome"), statoStud.get("nome"));
        assertNotNull(statoStud.get("voti"));
    }
    
    @Test
    public void testGetNotFound() {
        // Tentativo di aggiunta voto a studente inesistente
        Response rGet = reg.path(matricola + "1").request().get();
        
        // Verifica di ottenimento di "404 Not Found"
        assertEquals(Status.NOT_FOUND.getStatusCode(), rGet.getStatus());
    }
    
    @After
    public void eliminazioneStudente() {
        reg.path(matricola).request().delete();
    }
}
