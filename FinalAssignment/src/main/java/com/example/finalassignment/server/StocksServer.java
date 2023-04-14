package com.example.finalassignment.server;

import com.example.finalassignment.service.StocksResource;
import com.example.finalassignment.util.Profile;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


import static com.example.finalassignment.service.StocksResource.writeJsonStocks;
import static com.example.finalassignment.service.StocksResource.writeJsonGlobal;
import static com.example.finalassignment.service.StocksResource.jsonServer;
import static com.example.finalassignment.service.StocksResource.writeFile;

/**
 * This class represents a web socket server, a new connection is created
 * **/
@ServerEndpoint(value="/ws/stocks")
public class StocksServer {

    //users stores the userId and matches it to their profile class to store data
    private HashMap<String, Profile> users = new HashMap<>();
    //currentPrices stores the current prices for all stocks in the json for easy access
    private HashMap<String, Double> currentPrices = pullCurrentPrices();
    //globalSharesHeld stores how many shares are held for all stocks currently
    private HashMap<String, Integer> globalSharesHeld = new HashMap<>();

    public StocksServer() throws IOException {
    }

    @OnOpen
    public void open(Session session) throws IOException, EncodeException {
        RemoteEndpoint.Basic out = session.getBasicRemote();
        String userId = session.getId();
        Profile profile = new Profile(userId);

        users.put(userId, profile);
        out.sendText("Server Connected.");
    }

    @OnClose
    public void close(Session session) throws IOException, EncodeException {
        //useful variables
        String userId = session.getId();

        if (users.containsKey(userId)) {
            users.remove(userId);
        }
    }

    @OnMessage
    public void handleMessage(String tradeQuants, Session session) throws IOException, EncodeException {
        //useful variables
        String userId = session.getId();
        Profile profile = users.get(userId);
        double balance = profile.getBalance();
        HashMap<String, Integer> requestedTrades = new HashMap<>();

        JSONObject quants = new JSONObject(tradeQuants);
        String type = quants.get("type").toString();


        if (type.equals("balance request")) {
            session.getBasicRemote().sendText("{\"balance\":\"" + balance + "\"}");
            return;
        }

        if (type.equals("update")) {
            returnInfo(session);

            //if condition necessary
            updatePrices();
            return;
        }


        JSONArray quantsArray = quants.getJSONArray("quantities");
        //loop through json array
        for (int i = 0; i < quantsArray.length(); i++) {
            JSONObject stock = quantsArray.getJSONObject(i);
            String stockSymbol = stock.getString("symbol");
            String quantity = stock.getString("quantity");
            Integer intQuantity = Integer.parseInt(quantity);

            requestedTrades.put(stockSymbol, intQuantity);
        }

        boolean valid = verifyRequest(userId, requestedTrades);

        if (valid) {
            updateProfileShares(profile, requestedTrades);
            updateGlobalShares(requestedTrades);

            StocksResource.writeJsonGlobal(globalSharesHeld);
        }

    }

    public void updateProfileShares(Profile profile, HashMap<String, Integer> trades) {
        double cost = 0;
        for(String key: trades.keySet()) {
            cost += trades.get(key)*currentPrices.get(key);
            if (profile.stockProfile.containsKey(key)) {
                profile.stockProfile.put(key, profile.stockProfile.get(key)+trades.get(key));
            } else {
                profile.stockProfile.put(key, trades.get(key));
            }
        }
        profile.setBalance(profile.getBalance()+cost);
    }

    public void updateGlobalShares(HashMap<String, Integer> trades) {
        for(String key: trades.keySet()) {
            globalSharesHeld.put(key, globalSharesHeld.get(key)+trades.get(key));
        }
    }

    public HashMap<String, Double> pullCurrentPrices() throws IOException {
        HashMap<String, Double> currentPrices = new HashMap<>();


        JSONObject json = jsonServer("stocks.json");
        JSONArray stocks = json.getJSONArray("stocks");

        for (int i = 0; i < stocks.length(); i++) {
            JSONObject stock = stocks.getJSONObject(i);
            String stockSymbol = stock.getString("symbol");
            String price = stock.getString("price");
            Double doublePrice = Double.parseDouble(price);

            currentPrices.put(stockSymbol, doublePrice);
        }
        return currentPrices;
    }

    public boolean verifyRequest(String userId, HashMap<String, Integer> requestedTrades) {

        Profile profile = users.get(userId);
        double balance = profile.getBalance();
        double sum = 0;

        for(String key: requestedTrades.keySet()) {
            if (requestedTrades.get(key) < 0) {
                if (profile.stockProfile.get(key) + requestedTrades.get(key) < 0) {
                    return false;
                }
            } else {
                sum += requestedTrades.get(key)*(currentPrices.get(key));
            }
        }

        return balance>sum;
    }

    public void updatePrices() {
        for (String key : currentPrices.keySet()) {
            currentPrices.put(key, currentPrices.get(key)+1.0);
        }

        writeJsonStocks(currentPrices);
    }
    public void returnInfo(Session session) throws IOException {
        String userId = session.getId();
        Profile profile = users.get(userId);
        String message = "";



        session.getBasicRemote().sendText(message);
        //return json object with users stock profile and balance
    }
}

