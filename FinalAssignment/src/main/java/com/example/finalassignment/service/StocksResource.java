package com.example.finalassignment.service;

import jakarta.enterprise.inject.New;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;


@Path("/stock-data")
public class StocksResource {


    /**
     * @param filename Takes the name of one of the files in the resources folder e.g. "stocks.json"
     * @return the filepath of that file as java.nio.file.Path type
     * */
    private static java.nio.file.Path getFilePath(String filename) {
        /**
         * if there is no '/' at the beginning, the following function call will return `null`
         */
        String f;
        if (filename.charAt(0) != '/') {
            f = '/' + filename;
        } else {
            f = filename;
        }


        //get filepath
        java.nio.file.Path file = java.nio.file.Path.of(
                StocksResource.class.getResource(f)
                        .toString()
                        .substring(5));
        return file;

    }

    /**
     *
     * @return a response object for the front end that returns the contents of "stocks.json" in the resources folder
     * @throws IOException
     */
    @GET
    @Produces("application/json")
    @Path("/globalJson")
    public Response stocksJson() throws IOException {

        //read contents of "stocks.json" as string
        String val = readFileContents("/globalStocks.json");

        //create response object
        Response myResp = Response.status(200)
                .header("Content-Type", "application/json")
                .entity(val)
                .build();

        //return response object
        return myResp;
    }

    /**
     *
     * @return a response object for the front end that returns the contents of "globalStocks.json" in the
     * resources folder
     * @throws IOException
     */
    @GET
    @Produces("application/json")
    @Path("/stocksJson")
    public Response globalJson() throws IOException {

        //read contents of "globalStocks.json" as string
        String val = readFileContents("/stocks.json");

        //create response object
        Response myResp = Response.status(200)
                .header("Content-Type", "application/json")
                .entity(val)
                .build();

        //return response object
        return myResp;
    }

    /**
     * @return JSONObject of the stocks.json file which contains all stock symbols and prices
     */

    public static JSONObject jsonServer(String filename) throws IOException {
        return new JSONObject(readFileContents("/" + filename));
    }

    /**
     *
     * @param stocksHeld hashmap of all stocks as keys and values to replace old values of "globalStocks.json"
     * @throws IOException
     */
    public static void writeJsonGlobal(HashMap<String, Integer> stocksHeld) throws IOException {
        //create json object
        JSONObject stocks = new JSONObject();

        //get array from json object
        JSONArray stocksArray = stocks.getJSONArray("stocks");

        //iterate through hashmap and update all respective values
        for(HashMap.Entry<String, Integer> entry : stocksHeld.entrySet()) {
            //get key and value
            String key = entry.getKey();
            Integer value = entry.getValue();

            //update the json value
            stocks.put(key,value);
        }
        System.out.println(stocks);
        //write to the file
        writeFile("globalStocks.json", stocks.toString());
    }

    /**
     *
     * @param stocksHeld hashmap of all stocks as keys and values to replace old values of "stocks.json"
     * @throws IOException
     */
    public static void writeJsonStocks(HashMap<String, Double> stocksHeld) throws IOException {
        //create json object
        JSONObject stocks = new JSONObject();

        //get array from json object
        JSONArray stocksArray = stocks.getJSONArray("stocks");

        //iterate through hashmap and update all respective values
        for(HashMap.Entry<String, Double> entry : stocksHeld.entrySet()) {
            //get key and value
            String key = entry.getKey();
            Double value = entry.getValue();

            //update the json value
            stocks.put(key,value);
        }

        //write to the file
        writeFile("stocks.json", stocks.toString());
    }

    /**
     * @param filename Takes the string name of one of the files in the resources folder e.g. "stocks.json"
     * @return a string with the contents of the json
     * */
    public static String readFileContents(String filename) throws IOException {
        return Files.readString(getFilePath(filename));
    }

    /**
     * @param filename Takes the string name of one of the files in the resources folder e.g. "stocks.json"
     * @param content a string that contains the contents of the new json to be written to the file
     * @throws IOException
     */
    public static void writeFile(String filename, String content) throws IOException {
        //delete old file and replace it with a file of the same name containing new content
        //Files.delete(getFilePath(filename));
        //Files.writeString(getFilePath(filename), content);
    }

}