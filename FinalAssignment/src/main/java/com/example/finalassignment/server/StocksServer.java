package com.example.finalassignment.server;

import com.example.finalassignment.service.StocksResource;
import com.example.finalassignment.util.Profile;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * This class represents a web socket server, a new connection is created
 * **/
@ServerEndpoint(value="/ws/stocks")
public class StocksServer {

    //users stores the userId and matches it to their profile class to store data
    private static HashMap<String, Profile> users = new HashMap<>();
    //currentPrices stores the current prices for all stocks in the json for easy access
    private static HashMap<String, Double> currentPrices = new HashMap<>();
    //globalSharesHeld stores how many shares are held for all stocks currently
    private static HashMap<String, Integer> globalSharesHeld = new HashMap<>();
    //lastTick stores whether the price of a stock increased or decreased last tick (creates more fluent graph)
    private static HashMap<String, Boolean> lastTick = new HashMap<>();

    @OnOpen
    public void open(Session session) throws IOException, EncodeException {
        //initialize our prices locally using the json file for current prices, and set global shares held to 0
        currentPrices = pullCurrentPrices();

        //initializing these only when a user joins the server with no other connected users
        if(users.isEmpty()){
            for (String key : currentPrices.keySet()) {
                //give the hashmaps all the keys as well as initial values
                globalSharesHeld.put(key, 0);
                lastTick.put(key, true);
            }
            StocksResource.writeJsonGlobal(globalSharesHeld);
        }
        //user variables
        String userId = session.getId();
        Profile profile = new Profile(userId);

        //storing user data locally
        users.put(userId, profile);
    }

    @OnClose
    public void close(Session session) throws IOException, EncodeException {
        //user variable
        String userId = session.getId();

        //cleanup when a user disconnects
        if (users.containsKey(userId)) {
            users.remove(userId);
        }
    }

    @OnMessage
    public void handleMessage(String tradeQuants, Session session) throws IOException, EncodeException {
        //useful variables
        String userId = session.getId();
        Profile profile = users.get(userId);
        //double balance = profile.getBalance();
        HashMap<String, Integer> requestedTrades = new HashMap<>();

        JSONObject quants = new JSONObject(tradeQuants);
        //to filter request types
        String type = quants.get("type").toString();

        //this happens on each tick
        if (type.equals("update")) {
            //send new data back to frontend to be displayed
            returnInfo(session);

            //this condition makes sure that the stocks only update once per 'tick', not once per active user
            //only 1 user will pass this condition each time, so it will only be called once
            Object[] a = users.keySet().toArray();
            Arrays.sort(a);
            if (userId.equals(a[0])) {
                updatePrices();
            }
            return;
        }

        JSONArray quantsArray = quants.getJSONArray("quantities");
        //loop through json array
        for (int i = 0; i < quantsArray.length(); i++) {
            JSONObject stock = quantsArray.getJSONObject(i);
            String stockSymbol = stock.getString("symbol");
            Integer quantity = stock.getInt("quantity");
            if (quantity == null) {
                quantity = 0;
            }
            //add to requested trades
            requestedTrades.put(stockSymbol, quantity);
        }

        //ensures that user has the resources to buy/sell requested stocks
        boolean valid = verifyRequest(userId, requestedTrades);

        if (valid) { //only if user has the resources to buy/sell requested stocks
            updateProfileShares(profile, requestedTrades);
            updateGlobalShares(requestedTrades);

            //write updated values into global shares json
            StocksResource.writeJsonGlobal(globalSharesHeld);
        }
    }

    //storing updated number of stocks and balance in profile objects
    public void updateProfileShares(Profile profile, HashMap<String, Integer> trades) {
        double cost = 0;
        for(String key: trades.keySet()) {
            //cost times how many purchased or sold
            cost += trades.get(key)*currentPrices.get(key);
            //if statement for safety, user might not have this stock in hashmap
            if (profile.stockProfile.containsKey(key)) {
                profile.stockProfile.put(key, profile.stockProfile.get(key)+trades.get(key));
            } else {
                profile.stockProfile.put(key, trades.get(key));
            }
        }
        //locally update user balance
        profile.setBalance(profile.getBalance()-cost);
    }

    //adds or subtracts purchased/sold shares from the global count between users
    public void updateGlobalShares(HashMap<String, Integer> trades) {
        for(String key: trades.keySet()) {
            globalSharesHeld.put(key, globalSharesHeld.get(key)+trades.get(key));
        }
    }

    //pull prices from stocks.json, and storing them locally
    public HashMap<String, Double> pullCurrentPrices() throws IOException {
        HashMap<String, Double> pulledPrices = new HashMap<>();

        //iterating through passed json object
        JSONObject json = StocksResource.jsonServer("stocks.json");
        JSONArray stocks = json.getJSONArray("stocks");

        for (int i = 0; i < stocks.length(); i++) {
            JSONObject stock = stocks.getJSONObject(i);
            String stockSymbol = stock.getString("symbol");
            Double price = stock.getDouble("price");

            //log in hashmap
            pulledPrices.put(stockSymbol, price);
        }

        //returned to static variable
        return pulledPrices;
    }

    public boolean verifyRequest(String userId, HashMap<String, Integer> requestedTrades) {
        //useful variables
        Profile profile = users.get(userId);
        double balance = profile.getBalance();
        double sum = 0; //will hold total cost of purchasing requested stocks

        //iterating through all trades
        for(String key: requestedTrades.keySet()) {
            if (requestedTrades.get(key) < 0) {
                if (profile.stockProfile.get(key) + requestedTrades.get(key) < 0) {
                    //immediately end if we do not have enough stocks to sell desired amount
                    return false;
                }
            } else {
                sum += requestedTrades.get(key)*(currentPrices.get(key));
            }
        }
        //whether we have enough money to purchase these stocks
        return balance>sum;
    }

    //updates stock prices
    public void updatePrices() throws IOException {
        boolean inc; //determines whether stock should increase or decrease
        double multiplier;

        //iterate through each stock to update
        for (String key : currentPrices.keySet()) {
            Random rand = new Random();

            //70% likely-hood to continue previous tick's behaviour
            if (rand.nextDouble() > 0.3) {
                inc = lastTick.get(key);
            } else {
                inc = !lastTick.get(key);
            }

            // Obtain a number [0, 0.25]
            double n = rand.nextDouble()/4.0;

            //unless the stock is extremely high or low, the stock will be multiplied by a number in [0.75, 1.25]
            if (inc) {
                multiplier = 1.0 + n;
                lastTick.put(key,true);
                //stop stocks from snowballing too high
                if (currentPrices.get(key)*multiplier > 2000) {
                    multiplier = 1.0 - (n*2);
                    lastTick.put(key, false);
                }
            } else {
                multiplier = 1.0 - n;
                lastTick.put(key,false);
                //stops stocks from staying low for a prolonged time
                if (currentPrices.get(key)*multiplier < 25) {
                    multiplier = 1.0 + (n*3);
                    lastTick.put(key, true);
                }
            }

            //log our change locally
            currentPrices.put(key, currentPrices.get(key)*multiplier);
        }
        //write these values to the json file for stock prices
        StocksResource.writeJsonStocks(currentPrices);
    }

    //returns to user on each update
    public void returnInfo(Session session) throws IOException {
        //useful variables
        String userId = session.getId();
        Profile profile = users.get(userId);
        String message = "";

        //creating stringified json
        message += "{\n\t\"stocks\": [\n";
        int count = 0;
        for (String key : profile.stockProfile.keySet()) {
            message += "\t\t{\n";
            message += "\t\t\t\"symbol\":\"" + key + "\",\n";
            message += "\t\t\t\"held\":\"" + profile.stockProfile.get(key) + "\"\n";
            message += "\t\t}";
            if (count < profile.stockProfile.keySet().size()-1) {
                message += ",";
            }
            message += "\n";
            count += 1;
        }
        message += "\t],\n";
        message += "\t\"balance\":\"" + profile.getBalance() + "\"\n}";

        session.getBasicRemote().sendText(message);
        //return stringified json with user's stock profile and balance
    }
}
