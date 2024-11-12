package client;

import model.AuthToken;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

//Register tests
    @Test
    void testRegister() throws Exception {
        AuthToken token = facade.register("testUser", "testPass", "test@mail.com");
        assertNotNull(token);
        assertTrue(token.getToken().length() > 10);
    }

    @Test
    void testRegisterInvalidData() {
        assertThrows(Exception.class, () -> facade.register("", "testPass", "test@mail.com"));
        assertThrows(Exception.class, () -> facade.register("testUser", "", "test@mail.com"));
        assertThrows(Exception.class, () -> facade.register("testUser", "testPass", ""));
    }

    @Test
    void testRegisterDuplicateUser() throws Exception {
        // First, register the user
        facade.register("duplicateUser", "testPass", "test@mail.com");

        Exception exception = assertThrows(Exception.class, () -> facade.register("duplicateUser", "testPass", "test@mail.com"));

        assertTrue(exception.getMessage().contains("Registration failed"));
    }
//next will do Login tests
}
