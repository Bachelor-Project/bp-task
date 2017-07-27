/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import java.util.List;
import todos.Comment;
import todos.Level;
import todos.MainTopic;
import todos.MainTopicCounter;
import todos.MainTopicPriorityPair;
import todos.Task;
import todos.TaskMin;
import todos.Topic;
import todos.TopicType;

/**
 *
 * @author Dato
 */
public interface DBManager {
    
    public void save(String mainTopicName);
    public List<MainTopic> getMainTopics();
    public void updateMainTopic(int id, String newDescrip);
    public void deleteMainTopic(int id);
    
    public void save(int mainTopicId, TopicType type);
    public List<TopicType> getTopicTypes(int mainTopicId);
    public void updateTopicType(int mainTopicId, int topicId, String newDescrip);
    public void deleteTopicType(int mainTopicId, int topicId);
        
    
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

    public void save(Topic topic);
    public List<MainTopicPriorityPair> getTopicsWithPriorityFrom(int mainTopicID);

    public List<MainTopicCounter> getMainTopicsWithCountForTopics();
    public List<MainTopicCounter> getMainTopicsWithCountForTasks();
    public String getMainTopicNameBy(int mainTopicId);
    
    public List<TaskMin> getTasksMinInfo(int mtID);
}
