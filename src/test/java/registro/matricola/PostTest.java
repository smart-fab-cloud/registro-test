package registro.matricola;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class PostTest {
    
    private final WebTarget reg;
    private final JSONObject stud;
    private final String matricola;
    
    public PostTest() {
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
    public void testPostNotAllowed() {
        Response rPost = reg.path(matricola)
                            .request()
                            .post(Entity.entity("", MediaType.TEXT_PLAIN));
        assertEquals(405, rPost.getStatus());
    }
    
    @After
    public void eliminazioneStudente() {
        reg.path(matricola).request().delete();
    }
}
