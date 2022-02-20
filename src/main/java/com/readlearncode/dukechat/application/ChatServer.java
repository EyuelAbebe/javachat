package com.readlearncode.dukechat.application;

import java.util.Scanner;
import javax.websocket.DeploymentException;
import org.glassfish.tyrus.server.Server;

public class ChatServer {

  public static void main(String[] args) {
    Server server = new Server("localhost", 8025, "/ws", ChatServerEndpoint.class);

    try {
      server.start();
      System.out.println("Press any key to stop server...");
      new Scanner(System.in).nextLine();
    } catch (DeploymentException e) {
      throw new RuntimeException(e);
    } finally {
      server.stop();
    }
  }

}
