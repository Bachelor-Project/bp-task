/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import java.util.List;
import todos.Level;
import todos.MainTopic;
import todos.Task;

/**
 *
 * @author Dato
 */
public interface DBManager {
    
    public void save(MainTopic mt);
    public List<MainTopic> getMainTopics();
    public void updateMainTopic(int id, String name);
    public void deleteMainTopic(int id);
    
    public void save(Task t);
    public void updateTaskMainData(Task t);
    public boolean deleteTask(int id);
    public Task getTaskBy(int id);
    public List<Integer> getTasksIdsFor(int topicId);
    
    public List<Level> getLevels();
    
    public void addSupportedLanguage(int taskId, int langId);
    public void deleteSupportedLanguage(int taskId, int langId);
    
    public void addAssociatedTopic(int taskId, int topicId);
    public void deleteAssociatedTopic(int taskId, int topicId);
    
}
