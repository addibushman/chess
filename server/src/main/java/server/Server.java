package server;

import spark.*;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;


public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

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
            // Create the database and tables if they don't exist
            DatabaseManager.createDatabase();
            //need to clear all data
            DatabaseManager.clearAllData();
        } catch (DataAccessException e) {
            System.err.println("Failed to initialize the database: " + e.getMessage());
            System.exit(1); // Exit if the database fails to initialize
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
