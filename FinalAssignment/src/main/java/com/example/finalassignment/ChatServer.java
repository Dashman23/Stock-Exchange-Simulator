package com.example.finalassignment;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a web socket server, a new connection is created
 * **/
@ServerEndpoint(value="/ws/stocks")
public class ChatServer {


    //usernames stores the username of each active id for chat outputs
    private Map<String, Profile> users = new HashMap<>();

    @OnOpen
    public void open(Session session) throws IOException, EncodeException {
        RemoteEndpoint.Basic out = session.getBasicRemote();
        String userId = session.getId();
        Profile profile = new Profile(userId);

        users.put(userId, profile);

        //out.sendText(createMessage("Server "+roomID, "Welcome to the server. Please enter a username."));
    }

    @OnClose
    public void close(Session session) throws IOException, EncodeException {
        //useful variables
        String userId = session.getId();

        if (users.containsKey(userId)) {
            users.remove(userId);
        }
    }

    public void initiate() {

    }

    @OnMessage
    public void handleMessage(String tradeQuants, Session session) throws IOException, EncodeException {
        //useful variables
        String userId = session.getId();
        HashMap<String, Integer> requestedTrades = new HashMap<>();

        JSONObject quants = new JSONObject(tradeQuants);
        JSONArray quantsArray = quants.getJSONArray("quantities");

        for(int i = 0; i < quantsArray.length(); i++) {
            JSONObject quantity = quantsArray.getJSONObject(i);
            String q = quantity.getString("quantity");
            Double doubleQ = Double.parseDouble(q);

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
