/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todos;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dato
 */
public class TaskFullData {
    
    private StringBuilder taskContent = new StringBuilder();
    private List<Test> tests = new ArrayList<>();
    private TaskAnalyze taskAnalyze = new TaskAnalyze();
    private List<Hint> hints = new ArrayList<>();
    private Task task;

    
    
    public String getTaskContent() {
        return taskContent.toString();
    }

    public void addTaskContent(String content) {
        this.taskContent.append(content);
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public TaskAnalyze getTaskAnalyze() {
        return taskAnalyze;
    }

    public void setTaskAnalyze(TaskAnalyze taskAnalyze) {
        this.taskAnalyze = taskAnalyze;
    }

    public List<Hint> getHints() {
        return hints;
    }

    public void setHints(List<Hint> hints) {
        this.hints = hints;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
    
    
    @Override
    public String toString() {
        return "TaskFullData{" + "taskContent=" + taskContent + ", tests=" + tests + ", taskAnalyze=" + taskAnalyze + ", hints=" + hints + ", taskName=" + task.toString() + '}';
    }
    
    
}
