package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.GameService;
import dataaccess.DataAccessException;

import java.io.IOException;
import java.io.OutputStream;

public class GameHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("post")) {
            GameService gameService = new GameService();
            boolean success;
            String gameId = "newGame";

            try {
                success = gameService.createGame(gameId);
                if (success) {
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    exchange.sendResponseHeaders(400, 0);
                }
            } catch (DataAccessException e) {
                exchange.sendResponseHeaders(500, 0);
            }

            OutputStream os = exchange.getResponseBody();
            os.close();
        }
    }
}

