package server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.*;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import websocket.commands.UserGameCommand;

@WebSocket
public class Server {


    private WebSocketServer webSocketServer;
    public Server(){
        webSocketServer = new WebSocketServer();

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", webSocketServer);
        System.out.println("WebSocket server started at ws://localhost:8080/ws");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", new RegisterHandler());
        Spark.delete("/db", new ClearHandler());
        Spark.delete("/session", new LogoutHandler());
        Spark.post("/session", new LoginHandler());
        Spark.get("/game", new ListGamesHandler());
        Spark.post("/game", new CreateGameHandler());
        Spark.put("/game", new JoinGameHandler());

        Spark.init();

        initializeDatabase();

        Spark.awaitInitialization();
        return Spark.port();
    }


    private void initializeDatabase() {
        try {
            DatabaseManager.createDatabase();

        } catch (DataAccessException e) {
            System.err.println("Failed to initialize the database: " + e.getMessage());
            System.exit(1);
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}

