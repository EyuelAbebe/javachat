package com.readlearncode.dukechat.application;

import static java.lang.String.format;

import java.net.URI;
import java.util.Scanner;
import javax.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;

public class Client {

  public static void main(String[] args) throws Exception {

    // create the client instance
    ClientManager client = ClientManager.createClient();

    // Start welcome code
    String message;
    System.out.println("Welcome to Tiny Chat");
    System.out.println("What is your name?");

    Scanner scanner = new Scanner(System.in);
    String user = scanner.nextLine();
    // End welcome code

    // Connect to server endpoint
    Session session = client
        .connectToServer(ChatServerEndpoint.class, new URI("ws://localhost:8025/ws/chat"));

    System.out.println("You are logged in as " + user);

    do {
      // repeatedly read message and send to server (until quit)
      message = scanner.nextLine();

      // send message to server
      session.getBasicRemote().sendText(formatMessage(message, user));
    } while (!message.equalsIgnoreCase("quit"));
  }

  private static String formatMessage(String message, String user) {
    return format("%s: %s", message, user);
  }
}
