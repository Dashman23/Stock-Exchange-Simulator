package com.example.finalassignment.service;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

@Path("/stock-data")
public class StocksResource {

    @GET
    @Produces("text/plain")
    public String hello() {
        return "Hello, World!";
    }

    /**
     * This function retrieves a file using Java's built-in reflection functions.
     * This is because Java doesn't look in the directory you think it does on start up, this
     * is a way of guaranteeing it will return the absolute path of the file you're trying to read from.
     * @param filename the name of the file
     * @return the file's contents
     */
    private static String readFileContents(String filename) {
        /**
         * if there is no '/' at the beginning, the following function call will return `null`
         */
        String f;
        if (filename.charAt(0) != '/') {
            f = '/' + filename;
        } else {
            f = filename;
        }

        /**
         * trying to open and read the file
         */
        try {
            java.nio.file.Path file = java.nio.file.Path.of(
                    StocksResource.class.getResource(f)
                            .toString()
                            .substring(5));
            return Files.readString(file);
        } catch (IOException e) {
            // something went wrong
            return "Did you forget to create the file?\n" +
                    "Is the file in the right location?\n" +
                    e.toString();
        }

//        File file = new File("../../../../resources/" + filename);
//
//        // Get the absolute path of the file
//        String filePath = file.getAbsolutePath();
//
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new FileReader(filePath));
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        StringBuilder stringBuilder = new StringBuilder();
//        String line;
//        while (true) {
//            try {
//                if (!((line = reader.readLine()) != null)) break;
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            stringBuilder.append(line);
//        }
//        try {
//            reader.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return stringBuilder.toString();


    }

    @GET
    @Produces("application/json")
    @Path("/json")
    public Response json() {

        String val = readFileContents("/stocks.json");

        Response myResp = Response.status(200)
                .header("Content-Type", "application/json")
                .entity(val)
                .build();

        return myResp;
    }

    /**
     *
     * @return JSONObject of the stocks.json file which contains all stock symbols and prices
     */

    public static JSONObject jsonServer(String filename) {
        return new JSONObject(readFileContents("/" + filename));
    }

    public static void writeJsonGlobal(HashMap<String, Integer> stocksHeld) {
        JSONObject stocks = jsonServer("globalStocks.json");

        JSONArray stocksArray = stocks.getJSONArray("stocks");

        for(HashMap.Entry<String, Integer> entry : stocksHeld.entrySet()) {
            //get key and value
            String key = entry.getKey();
            Integer value = entry.getValue();

            //update the json value
            stocks.put(key,value);
            System.out.println("Key: " + key + ", Value: " + value);
        }

    }

    public static void writeJsonStocks(HashMap<String, Double> stocksHeld) {
        JSONObject stocks = jsonServer("stocks.json");

        JSONArray stocksArray = stocks.getJSONArray("stocks");

        for(HashMap.Entry<String, Double> entry : stocksHeld.entrySet()) {
            //get key and value
            String key = entry.getKey();
            Double value = entry.getValue();

            //update the json value
            stocks.put(key,value);
            System.out.println("Key: " + key + ", Value: " + value);
        }

    }

//    public static String readFile(String filePath) throws IOException {
//        BufferedReader reader = new BufferedReader(new FileReader(filePath));
//        StringBuilder stringBuilder = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            stringBuilder.append(line);
//        }
//        reader.close();
//        return stringBuilder.toString();
//    }

    public static void writeFile(String filePath, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(content);
        writer.close();
    }

}