/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

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
    protected String runDockerImage = "sudo -S docker run --rm -v /home/dato/Documents/project:/test -w /test oracle-java";
    
    
    public abstract List<ExecResult> run(String codeFilePath, Task taskData, String tasksRealPath);
    public abstract String getCodeFilePath(String username, String taskName);
}
