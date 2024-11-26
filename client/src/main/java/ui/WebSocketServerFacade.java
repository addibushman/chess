package ui;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

public class WebSocketServerFacade extends Endpoint {


    public void main(String[] args) throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
            }
        });
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a message you want to echo");
        while (true) {
            send.send(scanner.nextLine());
        }


    }
    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public Session session;

//    public WSClient() throws Exception {
//        URI uri = new URI("ws://localhost:8080/ws");
//        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//        this.session = container.connectToServer(this, uri);
//
//        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
//            public void onMessage(String message) {
//                System.out.println(message);
//            }
//        });
    }

//    public void send(String msg) throws Exception {
//        this.session.getBasicRemote().sendText(msg);
//    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}