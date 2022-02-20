package com.readlearncode.dukechat.application;

import static com.readlearncode.dukechat.utils.Messages.WELCOME_MESSAGE;
import static com.readlearncode.dukechat.utils.Messages.objectify;

import com.readlearncode.dukechat.domain.Message;
import com.readlearncode.dukechat.domain.Room;
import com.readlearncode.dukechat.infrastructure.MessageDecoder;
import com.readlearncode.dukechat.infrastructure.MessageEncoder;

import java.io.IOException;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex Theedom www.readlearncode.com
 * @version 1.0
 */

public class ChatServerEndpoint extends Endpoint {

  private final static Logger log = Logger.getLogger(ChatServerEndpoint.class.getSimpleName());
  private static final Map<String, Room> rooms = Collections
      .synchronizedMap(new HashMap<String, Room>());

  private static final String[] roomNames = {"Java EE 7", "Java SE 8", "Websockets", "JSON"};

  @PostConstruct
  public void initialise() {
    Arrays.stream(roomNames)
        .forEach(roomName -> rooms.computeIfAbsent(roomName, new Room(roomName)));
  }

  @Override
  public void onOpen(Session session, EndpointConfig config) {

    // add message handler
    session.addMessageHandler((MessageHandler.Whole<Message>) msg ->
        {
          System.out.println("message " + msg);
          rooms.get(extractRoomFrom(session)).sendMessage(msg);
        }
    );

    // set session level configuration
    String roomName = session.getPathParameters().get("roomName");
    String userName = session.getPathParameters().get("userName");
    session.getUserProperties().putIfAbsent("roomName", roomName);
    session.getUserProperties().putIfAbsent("userName", userName);
    session.setMaxIdleTimeout(5 * 60 * 1000);

    // store session
    Room room = rooms.get(roomName);
    room.join(session);

    // send welcome message
    try {
      session.getBasicRemote().sendObject(objectify(WELCOME_MESSAGE));
    } catch (IOException | EncodeException e) {
      log.info("Welcome message not sesnt");
    }
  }


  /**
   * Extracts the room from the session
   *
   * @param session the session object
   * @return the room name
   */
  private String extractRoomFrom(Session session) {
    return ((String) session.getUserProperties().get("roomName"));
  }

  /**
   * Returns the list of rooms in chat application
   *
   * @return Map of room names to room instances
   */
  static Map<String, Room> getRooms() {
    return rooms;
  }

}
