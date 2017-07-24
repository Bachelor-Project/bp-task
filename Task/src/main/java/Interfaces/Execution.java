/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import java.util.List;
import todos.ExecResult;
import todos.TaskData;

/**
 *
 * @author dato
 */
public abstract class Execution {
    
    public abstract ExecResult[] run(String codeFilePath, TaskData taskData, String taskDir, List<String> testsIds);
    
}
