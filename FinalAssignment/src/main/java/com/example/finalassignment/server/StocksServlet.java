//package com.example.finalassignment.server;
//
//import java.io.*;
//import java.util.HashSet;
//import java.util.Set;
//
//import jakarta.servlet.http.*;
//import jakarta.servlet.annotation.*;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.json.JSONObject;
//
///**
// * This is a class that has services
// * In our case, we are using this to generate unique room IDs**/
//@WebServlet(name = "stocksServlet", value = "/stocks-servlet")
//public class StocksServlet extends HttpServlet {
//    private String message;
//
//    //static so this set is unique
//    public static Set<String> rooms = new HashSet<>();
//
//    /**
//     * Method generates unique room codes
//     * **/
//    public String generatingRandomUpperAlphanumericString(int length) {
//        String generatedString = RandomStringUtils.randomAlphanumeric(length).toUpperCase();
//        // generating unique room code
//        while (rooms.contains(generatedString)){
//            generatedString = RandomStringUtils.randomAlphanumeric(length).toUpperCase();
//        }
//        rooms.add(generatedString);
//
//        return generatedString;
//    }
//
//    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        PrintWriter out = response.getWriter();
//        response.setContentType("application/json");
//        JSONObject respData = new JSONObject();
//        // query string has form: /chat-servlet?add={boolean}
//        boolean add = Boolean.parseBoolean(request.getParameter("add"));
//        String roomCode;
//        // only creates a new room when told
//        if(add){
//            // send the random code as the response's content
//            roomCode = generatingRandomUpperAlphanumericString(5);
//            respData.put("roomId",roomCode);
//        }
//        // always returns list of rooms
//        respData.put("roomList",rooms);
//        out.print(respData);
//    }
//
//    public void destroy() {
//    }
//}