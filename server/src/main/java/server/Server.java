package server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private HttpServer httpServer;

    public void run(int port) {
        try {
            // Create a new HTTP server on the specified port here
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);

            // Set up endpoints and handlers here
            httpServer.createContext("/user", new UserHandler()); // Your custom handler for users
            httpServer.createContext("/game", new GameHandler()); // Placeholder for game-related logic

            // Start the server here
            httpServer.start();
            System.out.println("Server running on port " + port);

        } catch (IOException e) {
            System.out.println("Error starting the server: " + e.getMessage());
        }
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0); // Stop the server gracefully
            System.out.println("Server stopped");
        }
    }
}
