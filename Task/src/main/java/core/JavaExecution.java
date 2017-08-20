/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import Interfaces.Execution;
import anums.ExceptionType;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import todos.ExecResult;
import todos.Task;

/**
 *
 * @author dato
 */
public class JavaExecution extends Execution {
    
    public JavaExecution(){
        
    }
    
    public JavaExecution(String tasksPath){
        tasksDockerPath = tasksPath;
    }

    @Override
    public List<ExecResult> run(String codeFilePath, Task taskData, String tasksRealPath) {
        List<ExecResult> res = new ArrayList<>();
        System.out.println("in run codeFilePath: " + codeFilePath);
        
        try {
            String command = makeDockerCommand(codeFilePath, taskData, tasksRealPath);
            System.out.println("command: " + command);
            
//            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = Runtime.getRuntime().exec(command); // pb.start();
            p.waitFor();
            res = processResultStream(p.getInputStream());
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(JavaExecution.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    
    private String makeDockerCommand(String codeFilePath, Task taskData, String tasksRealPath) throws UnsupportedEncodingException{
        int mL = taskData.getMemoryLimit();
        int tL = taskData.getTimeLimit() * valueOfSecond;
//        String runJavaCom = "java " + codeFilePath; //URLEncoder.encode(codeFilePath, "UTF-8");
        String runJavaCom = "java ./codesData/users/დავითი/Money"; //URLEncoder.encode(codeFilePath, "UTF-8");
        
        System.out.println("in makeDockerCommand codeFilePath: " + codeFilePath);
        
        String testsDirPathInDocker = tasksDockerPath + taskData.getName() + File.separator + "tests";
        String testsDirPathReal = tasksRealPath + taskData.getName() + File.separator + "tests/";
        String testsNames = getTestsNameFrom(testsDirPathReal);
        
//        String runUserCode = String.format("java -jar ./codesData/Runner.jar -Xmx%dm %d %s %s %s", mL, tL, runJavaCom, testsDirPathInDocker, testsNames);
        String runUserCode = "java -jar ./codesData/Runner.jar -Xmx" + mL + "m " + tL + " " + runJavaCom + " " + testsDirPathInDocker + " " + testsNames;
        System.out.println("in makeDockerCommand - runUserCode: " + runUserCode);
        
//        return new String[]{"/bin/bash","-c","echo admin | " + runDockerImage + " " + runUserCode};
        return runDockerImage + " " + runUserCode;
    }
    
    @Override
    public String getCodeFilePath(String username, String taskName) {
        String path = usersCodesPath + username + File.separator + taskName;
//        System.out.println("codeFilePath: " + path);
        return path;
    }
    
}
