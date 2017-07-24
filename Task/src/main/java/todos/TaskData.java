/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todos;

/**
 *
 * @author dato
 */
public class TaskData {
    
    private int taskId;
    private String taskName;
    private int timelimit;
    private int memoryLimit;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTimelimit() {
        return timelimit;
    }

    public void setTimelimit(int timelimit) {
        this.timelimit = timelimit;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    @Override
    public String toString() {
        return "TaskData{" + "taskId=" + taskId + ", taskName=" + taskName + ", timelimit=" + timelimit + ", memoryLimit=" + memoryLimit + '}';
    }
    
}
