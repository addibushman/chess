package ui;

import javax.websocket.*;
import java.net.URI;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;

public class WebSocketServerFacade extends Endpoint {

    private Session session;

    public static void main(String[] args) throws Exception {
        WebSocketServerFacade facade = new WebSocketServerFacade();
        facade.connect();
    }

    public void connect() throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                System.out.println("Received from server: " + message);
                handleServerMessage(message);
            }
        });

        System.out.println("Connected to the WebSocket server.");
    }

    private void handleServerMessage(String message) {
        Gson gson = new Gson();
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

        if (serverMessage != null) {
            if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                System.out.println("Successfully connected to the game.");
            }
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("WebSocket connection opened.");
    }

}
