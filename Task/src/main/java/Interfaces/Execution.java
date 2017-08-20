/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import anums.ExceptionType;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import todos.ExecResult;
import todos.Task;

/**
 *
 * @author dato
 */
public abstract class Execution {
    
    protected String usersCodesPath = "./codesData/users/";
    protected String tasksDockerPath = "./tasks/";
    protected String allTaskTestsFolderName = "tests";
    protected String testsFilesExtention = "in";
    protected int valueOfSecond = 1000;
    protected String projectDirectory = "/home/dato/Documents/project";
    protected String runDockerImage = "/usr/bin/docker run --rm -v " + projectDirectory + ":/test -w /test ubuntu-16.04";
    
    
    public abstract List<ExecResult> run(String codeFilePath, Task taskData, String tasksRealPath);
    public abstract String getCodeFilePath(String username, String taskName);
    
    protected String getTestsNameFrom(String dirPath){
        File testsFolder = new File(dirPath);
        File[] files = testsFolder.listFiles();
        StringBuilder result = new StringBuilder();
        for (File file : files) {
            if (file.isFile()){
                String fileName = file.getName();
                String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
                if (fileExt.equals(testsFilesExtention)){
                    String elem = fileName + ",";
                    result.append(elem);
                }
            }
        }
        return result.toString().substring(0, result.toString().length() - 1);
    }

    
    protected List<ExecResult> processResultStream(InputStream strm) throws IOException{
        List<ExecResult> results = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(strm));
        String line;
        System.out.println("------------start--------------");
        String test = "", message = "";
        ExceptionType exType = ExceptionType.NoError;
        while((line = reader.readLine()) != null){
            if (line.toLowerCase().startsWith("test:")){
                test = line.substring(line.indexOf("Test: ") + "Test: ".length());
            }
            else if (line.toLowerCase().startsWith("error:")){
                exType = ExceptionType.valueOf(line.substring(line.indexOf("Error: ") + "Error: ".length()));
            }
            else if (line.toLowerCase().startsWith("message:")){
                message = line.substring(line.indexOf("Message: ") + "Message: ".length());
            }
            
            System.out.println("line: " + line);
            
            if (line.isEmpty()) {
                ExecResult exRes = new ExecResult();
                exRes.setTestName(test);
                exRes.setExType(exType);
                exRes.setMessage(message);
                results.add(exRes);
            }
        }
        System.out.println("-------------end-------------");
        return results;
    }
}
