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
        
        try {
            String command = makeDockerCommand(codeFilePath, taskData, tasksRealPath);
//            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = Runtime.getRuntime().exec(command); // pb.start();
            p.waitFor();
            res = processResultStream(p.getInputStream());
//            System.out.println("----------  start Error stream");
//            res = processResultStream(p.getErrorStream());
//            System.out.println("----------  end Error stream");
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(JavaExecution.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    
    private String makeDockerCommand(String codeFilePath, Task taskData, String tasksRealPath){
        int mL = taskData.getMemoryLimit();
        int tL = taskData.getTimeLimit() * valueOfSecond;
        String runJavaCom = "java " + codeFilePath;
        String testsDirPathInDocker = tasksDockerPath + taskData.getName() + File.separator + "tests";
        String testsDirPathReal = tasksRealPath + taskData.getName() + File.separator + "tests/";
        String testsNames = getTestsNameFrom(testsDirPathReal);
        
        String runUserCode = String.format("java -jar ./codesData/Runner.jar -Xmx%dm %d %s %s %s", mL, tL, runJavaCom, testsDirPathInDocker, testsNames);
        
//        System.out.println("runUserCode: " + runUserCode);
        
//        return new String[]{"/bin/bash","-c","echo admin | " + runDockerImage + " " + runUserCode};
        return runDockerImage + " " + runUserCode;
//        return new String[]{runDockerImage + " " + runUserCode};
    }
    
    private String getTestsNameFrom(String dirPath){
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
    
    private List<ExecResult> processResultStream(InputStream strm) throws IOException{
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
//            System.out.println("--------------- ex Result variables ---------------------");
//            System.out.println(test);
//            System.out.println(exType.toString());
//            System.out.println(message);
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

    @Override
    public String getCodeFilePath(String username, String taskName) {
        String path = usersCodesPath + username + File.separator + taskName;
//        System.out.println("codeFilePath: " + path);
        return path;
    }
    
}
