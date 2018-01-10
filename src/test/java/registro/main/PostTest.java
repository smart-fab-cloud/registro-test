package registro.main;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import static org.junit.Assert.*;

public class PostTest {
    
    private final WebTarget reg;
    
    public PostTest()  { 
        // Creazione del client e connessione al servizio
        Client cli = ClientBuilder.newClient();
        reg = cli.target("http://localhost:50005/registro");
    }    
    
    private JSONObject creaStudenteDefault() {
        JSONObject studenteDefault = new JSONObject();
        studenteDefault.put("matricola", 437814);
        studenteDefault.put("cognome", "Rossi");
        studenteDefault.put("nome", "Mario");
        return studenteDefault;
    }
    
    @Test
    public void testPostCreated() throws ParseException {
        JSONObject stud = creaStudenteDefault();
        
        // Aggiunta studente
        Response rPost = reg.request()
                            .post(Entity.entity(stud.toJSONString(),MediaType.APPLICATION_JSON));
        // Reperimento studente aggiunto
        Response rGet = reg.path(String.valueOf(stud.get("matricola"))).request().get();
        
        // Eliminazione studente aggiunto
        reg.path(String.valueOf(stud.get("matricola"))).request().delete();
        
        // Verifica che rPost sia "201 Created"
        assertEquals(Status.CREATED.getStatusCode(), rPost.getStatus());
        // Verifica che lo studente sia stato creato correttamente
        assertEquals(Status.OK.getStatusCode(), rGet.getStatus());
        JSONParser p = new JSONParser();
        JSONObject studCreato = (JSONObject) p.parse(rGet.readEntity(String.class));
        Long matrStudCreato = (Long) studCreato.get("matricola");
        assertEquals(stud.get("matricola"), matrStudCreato.intValue());
        assertEquals(stud.get("cognome"), studCreato.get("cognome"));
        assertEquals(stud.get("nome"), studCreato.get("nome"));
        assertNotNull(studCreato.get("voti"));
    }
    
    @Test
    public void testPostBadRequest() {
        // Tentativo di post di studente con matricola negativa
        JSONObject stud1 = creaStudenteDefault();
        stud1.put("matricola", -5);
        Response rPost1 = reg.request()
                            .post(Entity.entity(stud1.toJSONString(),MediaType.APPLICATION_JSON));
        
        // Tentativo di post di studente con cognome vuoto
        JSONObject stud2 = creaStudenteDefault();
        stud2.put("cognome", "");
        Response rPost2 = reg.request()
                            .post(Entity.entity(stud2.toJSONString(),MediaType.APPLICATION_JSON));
        
        // Tentativo di post di studente con nome vuoto
        JSONObject stud3 = creaStudenteDefault();
        stud3.put("nome", "");
        Response rPost3 = reg.request()
                            .post(Entity.entity(stud3.toJSONString(),MediaType.APPLICATION_JSON));
        
        // Verifica che le rispose siano "400 Bad Request"
        assertEquals(Status.BAD_REQUEST.getStatusCode(), rPost1.getStatus());
        assertEquals(Status.BAD_REQUEST.getStatusCode(), rPost2.getStatus());
        assertEquals(Status.BAD_REQUEST.getStatusCode(), rPost3.getStatus());
        

    }
    
    @Test
    public void testPostConflict() {
        JSONObject stud = creaStudenteDefault();
        
        // Aggiunta studente
        reg.request().post(Entity.entity(stud.toJSONString(),MediaType.APPLICATION_JSON));
        
        // Tentativo di aggiunta di uno studente identico
        Response rPost = reg.request().post(Entity.entity(stud.toJSONString(),MediaType.APPLICATION_JSON));
        
        // Eliminazione studente aggiunto
        reg.path(String.valueOf(stud.get("matricola"))).request().delete();
        
        // Verifica che rPost sia "409 Conflict"
        assertEquals(Status.CONFLICT.getStatusCode(), rPost.getStatus());
    }
}
