/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.task;

import Interfaces.DBManager;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import fake.DBManagerFake;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    @GET
    @Path("levels")
    public Response getLevels(){
        return Response.status(200).entity(dbManager.getLevels()).build();
    }
    
    
//    @POST
//    @Path("upload")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response upload(@FormDataParam("file") InputStream fileInputStream,
//                            @FormDataParam("file") FormDataContentDisposition fileDetail){
//        
//        System.out.println(" --------------------- aq movida -----------------------");
//        System.out.println("filename: " + fileDetail.getFileName());
//        return Response.status(200).type(MediaType.TEXT_HTML).entity("UPLOAD FILE").build();
//    }
    
    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response upload2(FormDataMultiPart form){
        System.out.println("movidaaaaaaaaaaaaaaaaaaaaaaaaaa");
        
        String destPath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator;

        FormDataBodyPart filePart = form.getField("file");
        ContentDisposition headerOfFilePart =  filePart.getContentDisposition();
        InputStream fileInputStream = filePart.getValueAs(InputStream.class);
        String filePath = destPath + headerOfFilePart.getFileName();
        
        System.out.println("filePath: " + filePath);
        
        saveFile(fileInputStream, filePath);
        String output = "File saved to server location using FormDataMultiPart : " + filePath;
        return Response.status(200).entity(output).build();
    }
    
    private void saveFile(InputStream fileInputStream, String filePath) {
        try {
            OutputStream out = new FileOutputStream(new File(filePath));
            int read = 0;
            while((read = fileInputStream.read()) != -1){
                out.write(read);
            }
            out.flush();
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TaskService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TaskService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
