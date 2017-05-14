/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.task;

import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Dato
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaskService {
    
    public TaskService(){}
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response getAllTasks(){
        return Response.status(200).type(MediaType.TEXT_PLAIN).entity("getAllTasks").build();
    } 
    
    @POST
    public Response upload(InputStream stream){
        System.out.println(" --------------------- aq movida -----------------------");
        return Response.status(200).type(MediaType.TEXT_HTML).entity("UPLOAD FILE").build();
    }
}
