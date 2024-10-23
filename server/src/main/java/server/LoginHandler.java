package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.LoginService;
import dataaccess.DataAccessException;
import request.LoginRequest;
import model.LoginResult;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LoginHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            String username = "testUser";
            String password = "testPass";

            LoginRequest request = new LoginRequest(username, password);

            LoginService loginService = new LoginService();
            LoginResult result;

            try {
                result = loginService.login(request);
                if (result.isSuccess()) {
                    exchange.sendResponseHeaders(200, 0);
                    OutputStream os = exchange.getResponseBody();
                    os.write(result.toJSON().getBytes(StandardCharsets.UTF_8));
                    os.close();
                } else {
                    exchange.sendResponseHeaders(400, 0);
                }
            } catch (DataAccessException e) {
                exchange.sendResponseHeaders(500, 0);
            }
        }
    }
}
