package registro.main;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.junit.Test;
import static org.junit.Assert.*;

public class PutTest {
    
    private final WebTarget reg;
    
    public PutTest()  { 
        // Creazione del client e connessione al servizio
        Client cli = ClientBuilder.newClient();
        reg = cli.target("http://localhost:50005/registro");
    }    
    
    @Test
    public void testPutNotAllowed() {
        Response rPut = reg.request().delete();
        assertEquals(405, rPut.getStatus());
    }
    
}
