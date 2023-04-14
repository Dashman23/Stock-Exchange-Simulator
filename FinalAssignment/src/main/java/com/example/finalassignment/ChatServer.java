package com.example.finalassignment;


import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class represents a web socket server, a new connection is created and it receives a roomID as a parameter
 * **/
@ServerEndpoint(value="/ws/{roomID}")
public class ChatServer {

    // contains a static List of ChatRoom used to control the existing rooms and their users
    private static List<ChatRoom> roomList = new ArrayList<>();
    //sessions tracks which active ids are in which active room
    private static Map<String,ChatRoom> sessions = new HashMap<>();
    //usernames stores the username of each active id for chat outputs
    private Map<String, String> usernames = new HashMap<String, String>();

    @OnOpen
    public void open(@PathParam("roomID") String roomID, Session session) throws IOException, EncodeException {
        RemoteEndpoint.Basic out = session.getBasicRemote();

        //getRoom function will check if there is already a ChatRoom object tracking a room
        ChatRoom room = getRoom(roomID);
        //if we need a new ChatRoom object, we create one and add it to our list of active rooms
        if(room == null){
            room = new ChatRoom(roomID, session.getId());
            roomList.add(room);
        }

        //tracking which room each id is in
        sessions.put(session.getId(),room);
        //welcome message
        out.sendText(createMessage("Server "+roomID,
                "Welcome to the server. Please enter a username."));
    }

    @OnClose
    public void close(Session session) throws IOException, EncodeException {
        //useful variables
        String userId = session.getId();
        String username = usernames.get(userId);

        if (sessions.containsKey(userId)) {
            ChatRoom room = sessions.get(userId);
            // remove this user from the ChatRoom object and our username/id map
            usernames.remove(userId);
            room.removeUser(userId);

            // broadcasting it to peers in the same room
            for (Session peer : session.getOpenSessions()){ //broadcast this person left the server

                System.out.println(peer.getId());

                if(room.inRoom(peer.getId())) { // broadcast only to those in the same room
                    peer.getBasicRemote().sendText(
                            createMessage("Server " + room.getCode(),
                                    username + " left the chat room.")
                    );
                }
            }

            //removes room from roomList if no one is using the room anymore
            if (room.getUsers().keySet().size()==0) {
                roomList.remove(room);
            }
        }
    }

    @OnMessage
    public void handleMessage(String comm, Session session) throws IOException, EncodeException {
        //useful variables
        String userId = session.getId();
        JSONObject msg = new JSONObject(comm);
        ChatRoom room = sessions.get(userId);
        String message = msg.get("message").toString();

        // typical message handling
        if(usernames.containsKey(userId)){ // not their first message
            String username = usernames.get(userId);

            // broadcasting it to users in the same room
            for(Session peer: session.getOpenSessions()) {
                if (room.inRoom(peer.getId())) {
                    peer.getBasicRemote().sendText("{\"message\":\"(" + username + "): " + message + "\"}");
                }

            }
            return;
        }

        // login
        //if user sent their first message in this room
        String username = message.trim();
        //updates our usernames list
        usernames.put(userId, username);
        //room object also tracks users currently in its room
        room.setUserName(userId, username);
        // send welcome message to new user
        session.getBasicRemote().sendText(
                createMessage("Server "+room.getCode(),"Welcome, "+username+"!"));
        //send welcome message to all other users in the same room
        for(Session peer : session.getOpenSessions()) {
            if (room.inRoom(peer.getId()) && !peer.getId().equals(userId)) {
                peer.getBasicRemote().sendText(createMessage("Server "+room.getCode(),
                        username+" has joined the chat room. Everybody say hi!"));
            }
        }
    }

    //used to ensure each roomID has 1 unique room object, not 1 per session
    public ChatRoom getRoom(String roomID){
        for(ChatRoom room : roomList){
            if(room.getCode().equals(roomID)){
                return room;
            }
        }
        return null;
    }

    //optionally can use this function to auto-format
    public String createMessage(String user, String text){
        return "{\"message\":\"("+user+"): "+text+"\"}";
    }
}
