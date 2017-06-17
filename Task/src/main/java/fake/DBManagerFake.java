/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fake;

import Interfaces.DBManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import todos.Level;
import todos.MainTopic;
import todos.Task;

/**
 *
 * @author Dato
 */
public class DBManagerFake implements DBManager {

    private Map<Integer, MainTopic> mainTopics = new HashMap<>();
    private Level[] levels = {new Level(1, "მარტივი"), new Level(2, "საშუალო"), new Level(3, "რთული")};
    
    public static final DBManager instance = new DBManagerFake();
    
    private DBManagerFake() {
        mainTopics.put(1, new MainTopic(1, "გრაფი"));
        mainTopics.put(2, new MainTopic(2, "გეომეტრია"));
        
    }
    
    @Override
    public void save(MainTopic mt){
        mainTopics.put(mt.getId(), mt);
    }
    
    @Override
    public List<MainTopic> getMainTopics(){
        List<MainTopic> result = new ArrayList<>();
        Iterator<Integer> keys = mainTopics.keySet().iterator();
        while(keys.hasNext()){
            int key = keys.next();
            result.add(mainTopics.get(key));
        }
        return result;
    }
    
    @Override
    public void updateMainTopic(int id, String name){
        if (mainTopics.containsKey(id)){
            mainTopics.get(id).setDescrip(name);
        }
    }
    
    @Override
    public void deleteMainTopic(int id){
        if (mainTopics.containsKey(id)){
            mainTopics.remove(id);
        }
    }
    
    
    @Override
    public void save(Task t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateTaskMainData(Task t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteTask(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Task getTaskBy(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getTasksIdsFor(int topicId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Level> getLevels() {
        return Arrays.asList(levels);
    }

    @Override
    public void addSupportedLanguage(int taskId, int langId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteSupportedLanguage(int taskId, int langId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addAssociatedTopic(int taskId, int topicId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteAssociatedTopic(int taskId, int topicId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
