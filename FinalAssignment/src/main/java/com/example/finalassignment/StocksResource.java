package com.example.finalassignment;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

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
                            .substring(6));
            return Files.readString(file);
        } catch (IOException e) {
            // something went wrong
            return "Did you forget to create the file?\n" +
                    "Is the file in the right location?\n" +
                    e.toString();
        }
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
    public static JSONObject jsonServer() {
        return new JSONObject(readFileContents("/stocks.json"));
    }
}