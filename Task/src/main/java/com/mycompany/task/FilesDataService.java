/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.task;

import Interfaces.DBManager;
import Interfaces.Execution;
import core.JavaExecution;
import db.DBData;
import db.DBManagerReal;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import todos.ExecResult;
import todos.MainTopic;
import todos.RunCodeDTO;
import todos.Task;
import todos.Topic;

/**
 *
 * @author Dato
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FilesDataService {
    
    private final DBManager dbManager;
    
    public FilesDataService(){
        this(DBManagerReal.instance);
    }

    public FilesDataService(DBManager dbManager){
        this.dbManager = dbManager;
        executionsMap.put("java", new JavaExecution());
    }
    
    @GET
    @Path("main_topics")
    public Response getMainTopics(){
        return Response.status(200).entity(dbManager.getMainTopics()).build();
    }
    
    @GET
    @Path("name_main_topic")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getMainTopicNameBy(@QueryParam("id") int mainTopicID){
        Response res = Response.status(200).entity(dbManager.getMainTopicNameBy(mainTopicID)).build();
        return res;
    }
    
    @GET
    @Path("priorities")
    public Response getPriorities(@QueryParam("main_topic") int mainTopicID){
        return Response.status(200).entity(dbManager.getTopicsWithPriorityFrom(mainTopicID)).build();
    }
    
    @GET
    @Path("levels")
    public Response getLevels(){
        return Response.status(200).entity(dbManager.getLevels()).build();
    }
    
    @GET
    @Path("/counting_main_topics")
    public Response getCountingMainTopics (){
        return Response.status(200).entity(dbManager.getMainTopicsWithCount()).build();
    }
    
    @GET
    @Path("/main_topics/{mainTopicId}")
    public Response getTasksFor(@PathParam("mainTopicId") int mainTopicID){
        List<Integer> tasksIds = dbManager.getTasksIdsFor(mainTopicID);
        return Response.status(200).entity(tasksIds).build();
    }
    
    @GET
    @Path("tasks/{id}")
    public Response getTaskFor(@PathParam("id") int taskId){
        return Response.status(200).
                type(MediaType.TEXT_PLAIN) // ?????????????
                .entity("TaskData test").build();
    } 
    
    private final String topicsDestination = "/home/dato/Documents/project/topics/";
    
    @POST
    @Path("/uploadTopic")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadTopic(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("mainTopic") String mainTopic,
            @FormDataParam("priority") int priority) {

            String fileName = fileDetail.getFileName();
            System.out.println("fileName: " + fileName);
            String uploadedFileLocation = topicsDestination + fileName;

            // save file in folder:
            writeToFile(uploadedInputStream, uploadedFileLocation);
            
            // save file data in DB:
            int pointIndex = fileName.lastIndexOf(".");
            String name = fileName.substring(0, pointIndex);
            String fileExt = fileName.substring(pointIndex + 1);
            
            Topic topic = makeTopicFrom(name, fileExt, mainTopic, priority);
            dbManager.save(topic);

            return Response.status(200).entity("Upload Success").build();

    }
    
    private Topic makeTopicFrom(String fileName, String ext, String mainTopic, int p){
        Topic topic = new Topic();
        topic.setName(fileName);
        topic.setFileExt(ext);
        topic.setMainTopic(new MainTopic(0, mainTopic));
        topic.setPriority(p);
        return topic;
    }
    
    
    // save uploaded file to new location
    private void writeToFile(InputStream uploadedInputStream,
            String uploadedFileLocation) {

            try {
                    OutputStream out = new FileOutputStream(new File(
                                    uploadedFileLocation));
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    out = new FileOutputStream(new File(uploadedFileLocation));
                    while ((read = uploadedInputStream.read(bytes)) != -1) {
                            out.write(bytes, 0, read);
                    }
                    out.flush();
                    out.close();
            } catch (IOException e) {

                    e.printStackTrace();
            }
    }
    
    private final String tasksDestination = "/home/dato/Documents/project/tasks/";
    
    @POST
    @Path("/uploadTask")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadTask(
                            @FormDataParam("file") InputStream fileStream,
                            @FormDataParam("file") FormDataContentDisposition fileDetail,
                            @FormDataParam("time_lm") int timeLm,
                            @FormDataParam("memory_lm") int memoryLm,
                            @FormDataParam("mainTopic") String mainTopicData,
                            @FormDataParam("level") String level){
        try {
            String name = fileDetail.getFileName();
            String nameWithoutExt = name.substring(0, name.lastIndexOf("."));
            String params = String.format("%s %d %d %s %s", 
                                name, timeLm, memoryLm, mainTopicData, level);
            System.out.println("params: " + params);
            String mainTopic = mainTopicData;
            if (isExistMainTopic(mainTopicData)){
                mainTopic = dbManager.getMainTopicNameBy(Integer.parseInt(mainTopicData));
            }

            // save in folder:
            unzipAndSave(fileStream, nameWithoutExt);
            
            // save in DB:
            Task task = makeTaskFrom(nameWithoutExt, timeLm, memoryLm, mainTopic, level);
            dbManager.save(task);
            
            return Response.status(200).entity("Upload success").build();
        } catch (IOException ex) {
            Logger.getLogger(FilesDataService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(400).entity("unzip exception").build();
    }
    
    private boolean isExistMainTopic(String mainTopic){
        boolean res = true;
        for (int i = 0; i < mainTopic.length(); i++){
            if (!Character.isDigit(mainTopic.charAt(i))){
                res = false;
                break;
            }
        }
        return res;
    }
    
    private Task makeTaskFrom(String name, int timeLimit, int memoryLimit, String mainTopic, String level){
        Task task = new Task();
        task.setName(name);
        task.setTimeLimit(timeLimit);
        task.setMemeoryLimit(memoryLimit);
        task.setMainTopic(new MainTopic(0, mainTopic));
        task.setLevel(new todos.Level(0, level));
        return task;
    }
    
    private void unzipAndSave(InputStream zipFileStream, String taskZipName) throws IOException {
        String newFolderPath = tasksDestination + taskZipName + File.separator;
        File destDir = new File(newFolderPath);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        
        try (ZipInputStream zipInStream = new ZipInputStream(zipFileStream)) {
            ZipEntry entry = zipInStream.getNextEntry();
            while (entry != null) {
                String filePath = newFolderPath + entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(zipInStream, filePath);
                } else {
                    File dir = new File(filePath);
                    if (!dir.exists()){
                        dir.mkdir();
                    }
                }
                zipInStream.closeEntry();
                entry = zipInStream.getNextEntry();
            }
        }
    }
    private static final int BUFFER_SIZE = 4096;
    
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
    
    private final Map<String, Execution> executionsMap = new HashMap<>();
    
    @POST
    @Path("run_code")
    public Response runCode(RunCodeDTO runCodeRequest){
        String params = String.format("%s", runCodeRequest);
        System.out.println("params: " + params);
        
        if (runCodeRequest.isCompiled()){
            if (executionsMap.containsKey(runCodeRequest.getLang())){
                Execution execution = executionsMap.get(runCodeRequest.getLang());
                Task task = dbManager.getTaskBy(runCodeRequest.getTaskId());
                System.out.println("Executoin: " + execution);
                System.out.println("runCodeRequest: " + runCodeRequest);
                String codeFilePath = execution.getCodeFilePath(runCodeRequest.getUsername(), task.getName());
                
                List<ExecResult> execRes = execution.run(codeFilePath, task, tasksDestination);
                return Response.status(200).
//                        type(MediaType.TEXT_PLAIN). // ------------
                        entity(execRes).build();
            }
            else {
                return Response.status(400).type(MediaType.TEXT_PLAIN).entity("No Language support.").build();
            }
        }
        return Response.status(400).type(MediaType.TEXT_PLAIN).entity("No Compiled").build();

    }
    
}
