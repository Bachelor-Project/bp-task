/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.task;

import Interfaces.DBManager;
import Interfaces.Execution;
import core.CPPExecution;
import core.JavaExecution;
import core.PythonExecution;
import db.DBManagerReal;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import todos.ExecResult;
import todos.Hint;
import todos.MainTopic;
import todos.RunCodeDTO;
import todos.Task;
import todos.TaskFullData;
import todos.Test;
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
        executionsMap.put("python", new PythonExecution());
        executionsMap.put("c_cpp", new CPPExecution());
    }
    
    @GET
    @Path("/test_get")
    public String testGET(){
        return "T E S T!";
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
    public Response getCountingMainTopicsForTopics (){
        return Response.status(200).entity(dbManager.getMainTopicsWithCountForTopics()).build();
    }
    
    @GET
    @Path("/tasks_counting_main_topics")
    public Response getCountingMainTopicsForTasks (){
        return Response.status(200).entity(dbManager.getMainTopicsWithCountForTasks()).build();
    }
    
    @GET
    @Path("/main_topics/{mainTopicId}")
    public Response getTasksFor(@PathParam("mainTopicId") int mainTopicID){
        List<Integer> tasksIds = dbManager.getTasksIdsFor(mainTopicID);
        return Response.status(200).entity(tasksIds).build();
    }
    
    @GET
    @Path("/tasks_min_data/{mt_id}")
    public Response getTasksMinData(@PathParam("mt_id") int mtID){
        System.out.println("main topic id: " + mtID);
        return Response.status(200).entity(dbManager.getTasksMinInfo(mtID)).build();
    }
    
    @GET
    @Path("/all_topics")
    public Response getAllTopics(){
        List<Topic> topics = dbManager.getTopics();
        return Response.status(200).entity(topics).build();
    }
    
    @GET
    @Path("/all_tasks")
    public Response getAllTasks(){
        List<Task> tasks = dbManager.getTasks();
        return Response.status(200).entity(tasks).build();
    }
    
    @GET
    @Path("/task_full_data/{task_id}")
    public Response getTaskFullData(@PathParam("task_id") int taskID){
        System.out.println("task_id: " + taskID);
        
        Task t = dbManager.getTaskBy(taskID);
        String taskDirPath = tasksDestination + t.getName();
        File taskFile = new File(taskDirPath + File.separator + "task.txt");
        File hintFile = new File(taskDirPath + File.separator + "hint.txt");
        File testsFolder = new File(taskDirPath + File.separator + "tests");
        File solutionsFolder = new File(taskDirPath + File.separator + "solutions");
        
        TaskFullData tfd = new TaskFullData();
        try {
            tfd.setTask(t);
            if (taskFile.exists())
                tfd.addTaskContent(readFile(taskFile));
            if(hintFile.exists())
                tfd.setHints(readHint(hintFile));
            if (testsFolder.exists())
                tfd.setTests(readTestFolder(testsFolder));
            if(solutionsFolder.exists()){
                List<Hint> soluTionHints = readSolutonFolder(tfd.getHints().size(), solutionsFolder);
                for (Hint soluTionHint : soluTionHints) {
                    tfd.getHints().add(soluTionHint);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FilesDataService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FilesDataService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(200).entity(tfd).build();
    }
    
    private String readFile(File file) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    private List<Hint> readHint(File file) throws FileNotFoundException, IOException {
        List<Hint> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
            String line;
            while((line = reader.readLine()) != null){
                String tLine = line.trim();
                if (tLine.startsWith("#")){
                    Hint h = new Hint();
                    
                    h.id = Integer.parseInt(tLine.substring(1));
                    h.title = "მითითება N " + h.id;
                    h.isCode = false;
                    result.add(h);
                }
                else {
                    Hint prevHint = result.get(result.size() - 1);
                    prevHint.addContent(line);
                }
            }
        }
        return result;
    }
    
    private List<Test> readTestFolder(File file) throws IOException {
        List<Test> tests = new ArrayList<>();
        File[] testOutputFiles = file.listFiles();
        for (File testOut : testOutputFiles) {
            String nameWithExt = testOut.getName();
            String name = nameWithExt.substring(0, nameWithExt.lastIndexOf("."));
            
            List<Test> existed = tests.stream().filter((t) -> t.name.equals(name)).collect(Collectors.toList());
            if (existed.isEmpty()){ // have not test
                Test test = new Test();
                test.name = name;
                fillTestInputOrOutput(nameWithExt.contains(".in"), testOut, test);
                tests.add(test);
            }
            else { // have test
                Test prevTest = existed.get(0);
                fillTestInputOrOutput(nameWithExt.contains(".in"), testOut, prevTest);
            }
        }
        return tests;
    }
    
    private void fillTestInputOrOutput(boolean isInput, File file, Test test) throws IOException{
        String fileContent = readFile(file);
        if (isInput){
            test.input = fileContent;
        }
        else {
            test.output = fileContent;
        }
    }
    
    private List<Hint> readSolutonFolder(int verbalHintsCount, File solFolder) throws FileNotFoundException, IOException{
        List<Hint> hints = new ArrayList<>();
        File[] files = solFolder.listFiles();
        int count = verbalHintsCount;
        for (File file : files) {
            count = count + 1;
            String content = readSolutionHintFile(file);
            
            Hint solHint = new Hint();
            solHint.id = count;
            solHint.isCode = true;
            String fileName = file.getName();
            solHint.title = fileName.substring(fileName.lastIndexOf(".") + 1);
            solHint.addContent(content);
            hints.add(solHint);
        }
        return hints;
    }
    
    private String readSolutionHintFile(File hintFile) throws FileNotFoundException, IOException{
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(hintFile), "UTF8"))) {
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line);
                sb.append("\n\n");
            }
        }
        return sb.toString();
    }
    
    
    @GET
    @Path("/pdf")
    public Response getPDF(@QueryParam("name") String name){
//        String folder = "$HOME/Documents/project/topics/";
        String pdfURL = topicsDestination + name;
        File pdf = new File(pdfURL);
        System.out.println("pdf: " + pdf.getAbsolutePath());
        return Response.ok(pdf).type("application/pdf").build();
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
            @FormDataParam("mainTopic") String mainTopicData,
            @FormDataParam("priority") int priority) {

            String fileName = fileDetail.getFileName();
            try {
                fileName = new String (fileDetail.getFileName().getBytes ("iso-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(FilesDataService.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("fileName: " + fileName);
            String uploadedFileLocation = topicsDestination + fileName;

            // save file in folder:
            writeToFile(uploadedInputStream, uploadedFileLocation);
            
            // save file data in DB:
            int pointIndex = fileName.lastIndexOf(".");
            String name = fileName.substring(0, pointIndex);
            String fileExt = fileName.substring(pointIndex + 1);
            
            String mainTopic = mainTopicData;
            if (isExistMainTopic(mainTopicData)){
                mainTopic = dbManager.getMainTopicNameBy(Integer.parseInt(mainTopicData));
            }
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
            try (OutputStream out = new FileOutputStream(new File(uploadedFileLocation))) {
                int read = 0;
                byte[] bytes = new byte[1024];
                
//                    out = new FileOutputStream(new File(uploadedFileLocation));
                while ((read = uploadedInputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
//                out.write("ababshshjfahjhajbajbvabv;".getBytes());
                out.flush();
            }
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
            try {
                name = new String (fileDetail.getFileName().getBytes ("iso-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(FilesDataService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
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
        task.setMemoryLimit(memoryLimit);
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
        System.out.println(runCodeRequest.getLang().toLowerCase());
        
        if (runCodeRequest.isCompiled()){
            String progLang = runCodeRequest.getLang().toLowerCase();
            if (executionsMap.containsKey(progLang)){
                Execution execution = executionsMap.get(progLang);
                Task task = dbManager.getTaskBy(runCodeRequest.getTaskId());
                String codeFilePath = execution.getCodeFilePath(runCodeRequest.getUsername(), task.getName());
                
                System.out.println("in service codeFilePath: " + codeFilePath);
                
                List<ExecResult> execRes = execution.run(codeFilePath, task, tasksDestination);
                
                System.out.println("execRes size: " + execRes.size());
                
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
    
    
    
    @POST
    @Path("/update_main_topic")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateMainTopic(@FormDataParam("main_topic_id") int mtID,
                                    @FormDataParam("main_topic_new_name") String mtName){
        System.out.println("mtID: " + mtID);
        System.out.println("mtName: " + mtName);
        
        try {
            dbManager.updateMainTopic(mtID, mtName);
        } catch (SQLException ex) {
            String resposneStr = "Update error: ";
            if (ex.getErrorCode() == 1062){ // Duplicate entry
                resposneStr += " Duplicate Entry!";
            }
            return Response.status(400).entity(resposneStr).build();
        }
        return Response.status(200).entity("Update success").build();
    }
    
    
    @POST
    @Path("/upload_main_topic")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadMainTopic(@FormDataParam("main_topic_name") String mtName){
        System.out.println("mtName: " + mtName);
        
        try {
            dbManager.save(mtName);
        } catch (SQLException ex) {
            String resposneStr = "Upload error: ";
            if (ex.getErrorCode() == 1062){ // Duplicate entry
                resposneStr += " Duplicate Entry!";
            }
            return Response.status(400).entity(resposneStr).build();
        }
        return Response.status(200).entity("Upload success").build();
    }
    
    @DELETE
    @Path("/delete_main_topic/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteMainTopic(@PathParam("id") int id){
        System.out.println("mt ID: " + id);
        
        try {
            dbManager.deleteMainTopic(id);
        } catch (SQLException ex) {
            String resposneStr = "Error during removing.";
            return Response.status(400).entity(resposneStr).build();
        }
        return Response.status(200).entity("Removed success").build();
    }
    
    @DELETE
    @Path("/delete_topic/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteTopic(@PathParam("name") String name){
        System.out.println("full path: " + topicsDestination + name);
        
        Response response = Response.status(204).build();
        File topicFile = new File(topicsDestination + name);
        if (topicFile.exists()){
            topicFile.delete();
            
            dbManager.deleteTopic(name.substring(0, name.lastIndexOf(".")));
        } else {
            response = Response.status(400).entity("File with name " + name + " does not exists!").build();
        }
        
        return response;
    }
    
    @DELETE
    @Path("/delete_task/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteTask(@PathParam("name") String name){
        System.out.println("full path: " + tasksDestination + name);
        
        Response response = Response.status(204).build();
        File taskFile = new File(tasksDestination + name);
        if (taskFile.exists()){
            try {
                FileUtils.deleteDirectory(taskFile);
            } catch (IOException ex) {
                Logger.getLogger(FilesDataService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            dbManager.deleteTaskByName(name);
        } else {
            response = Response.status(400).entity("File with name " + name + " does not exists!").build();
        }
        
        return response;
    }
    
    
}
