/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.task;

import Interfaces.DBManager;
import fake.DBManagerFake;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
    
    private DBManager dbManager;
    
    public TaskService(){
//        dbManager = DBUtils.getInstance();
        dbManager = DBManagerFake.instance;
    }

    public TaskService(DBManager dbManager){
        this.dbManager = dbManager;
    }
    
    @GET
    @Path("main_topics")
    public Response getMainTopics(){
        return Response.status(200).entity(dbManager.getMainTopics()).build();
    }
    
//    @PUT
//    @Path("")
//    public Response updateMainTopic(MainTopic mt){
//        
//        return Response.ok().build();
//    }
    
    @GET
    @Path("levels")
    public Response getLevels(){
        return Response.status(200).entity(dbManager.getLevels()).build();
    }
    
}
