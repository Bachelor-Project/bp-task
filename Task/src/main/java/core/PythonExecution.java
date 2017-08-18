/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import Interfaces.Execution;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import todos.ExecResult;
import todos.Task;

/**
 *
 * @author dato
 */
public class PythonExecution extends Execution {

    private final String[] imports = {"import resource", "import sys"};
    
    @Override
    public List<ExecResult> run(String codeFilePath, Task taskData, String tasksRealPath) {
        List<ExecResult> res = new ArrayList<>();
        
        try {
            System.out.println("path in run: " + projectDirectory + codeFilePath.substring(1));
            File codeFile = new File(projectDirectory + codeFilePath.substring(1));
            addImports(codeFile);
            addCommandInCode(codeFile, taskData.getMemoryLimit());
            
            String command = makeDockerCommand(codeFilePath, taskData, tasksRealPath);
            System.out.println("command: " + command);
            
////            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = Runtime.getRuntime().exec(command); // pb.start();
            p.waitFor();
            res = processResultStream(p.getInputStream());
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(JavaExecution.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    
    private void addImports(File file) throws UnsupportedEncodingException, IOException{
        Set<String> importSet = getImportsFrom(file);
        for (String imp : imports) {
            if (!importSet.contains(imp)){
                writeImportsTo(file, imp);
            }
        }
    }
   
    private void writeImportsTo(File file, String content) throws FileNotFoundException, IOException {
        StringBuilder strBuild = new StringBuilder();
        // Reads content:
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
            String line;
            while((line = reader.readLine()) != null){
                strBuild.append(line);
                strBuild.append("\n");
            }
        }
        
        // Writes new imports and old content:
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(content + "\n" + strBuild.toString());
            writer.flush();
        }
    }
    
    
    private Set<String> getImportsFrom(File file) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        Set<String> result = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
            String line;
            while((line = reader.readLine()) != null){
                String lws = line.trim();
                if (lws.contains("import")){
                    result.add(lws);
                }
            }
        }
        return result;
    }
    
    
    private void addCommandInCode(File file, int taskMemLimit) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        StringBuilder strBuild = new StringBuilder();
        // Reads content:
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
            String line;
            while((line = reader.readLine()) != null){
                if (!line.trim().isEmpty() && !line.contains("setrlimit")){
                    strBuild.append(line);
                    strBuild.append("\n");
                }
            }
        }
        
        String mainMethod = "if __name__ == \"__main__\":";
        String mainMethod2 = "if __name__ == '__main__':";
        long memLimit = taskMemLimit * 1024 * 1024;
        String rlimitCommand = "\trsrc = resource.RLIMIT_DATA\n" +
                               "\tsoft, hard = resource.getrlimit(rsrc)\n" +
                               "\tresource.setrlimit(rsrc, (" + memLimit + ", hard))\n";
        StringTokenizer tok = new StringTokenizer(strBuild.toString(), "\n");
        // Writes new imports and old content:
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            while (tok.hasMoreTokens()) {
                String entry = tok.nextToken();
                if (entry.contains(mainMethod) || entry.contains(mainMethod2)) {
//                    System.out.println("entry if: " + entry);
                    
                    writer.write(entry + "\n" + rlimitCommand + "\n");
                    writer.flush();
                }
                else {
//                    System.out.println("entry else: " + entry);
                    
                    writer.write(entry + "\n");
                    writer.flush();
                }
            }
        }
    }

    private String makeDockerCommand(String codeFilePath, Task taskData, String tasksRealPath) throws UnsupportedEncodingException{
        int mL = taskData.getMemoryLimit();
        int tL = taskData.getTimeLimit() * valueOfSecond;
        String runPythonCom = "python3 " + codeFilePath; //URLEncoder.encode(codeFilePath, "UTF-8");
        
        String testsDirPathInDocker = tasksDockerPath + taskData.getName() + File.separator + "tests";
        String testsDirPathReal = tasksRealPath + taskData.getName() + File.separator + "tests/";
        String testsNames = getTestsNameFrom(testsDirPathReal);
        
//        String runUserCode = String.format("java -jar ./codesData/Runner.jar -Xmx%dm %d %s %s %s", mL, tL, runJavaCom, testsDirPathInDocker, testsNames);
        String runUserCode = "java -jar ./codesData/Runner.jar " + mL + " " + tL + " " + runPythonCom + " " + testsDirPathInDocker + " " + testsNames;
        
        return runDockerImage + " " + runUserCode;
    }
    
    @Override
    public String getCodeFilePath(String username, String taskName) {
        String path = usersCodesPath + username + File.separator + taskName + ".py";
        System.out.println("getCodeFilePath: " + path);
        return path;
    }
    
    
    
}
