package com.example.finalassignment;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/format")
public class APIFormat {

    @POST
    @Path("/json")
    @Consumes("application/json")
    public void json(String body) {
//        Student[] students;


        // rejecting if the data is empty
//        if (body.isEmpty()) {
//            return Response.status(400)
//                    .entity("No data passed in the body.")
//                    .build();
//        }
//
//        try {
//            students = Student.fromHTML(body);
//        } catch (RuntimeException e) {
//            System.out.println(e);
//            return Response.status(400).entity("Bad data passed to the API\n" + e).build();
//        }
//
//        String response = new FileFormatter(students).toJSON();
//
//        return Response.status(200)
//                .header("Content-Disposition", "attachment;filename=\"students.json\"")
//                .entity(response)
//                .build();
    }
}
