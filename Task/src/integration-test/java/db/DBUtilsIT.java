/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import todos.Language;
import todos.Level;
import todos.MainTopic;
import todos.Task;
import static org.junit.Assert.*;


/**
 *
 * @author Dato
 */
public class DBUtilsIT {
    
    private final DBManagerReal instance = DBManagerReal.getInstance();
    
    public DBUtilsIT() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    private Task makeTask(String name, Level level, int time, int memory){
        Task task = new Task();
        task.setName(name);
        task.setLevel(level);
        task.setTimeLimit(time);
        task.setMemeoryLimit(memory);
        return task;
    }
    
    private void addLanguages(Task t, Language... lang){
        List<Language> lanuages = Arrays.asList(lang);
        t.setLanguages(lanuages);
    }
    
    private void addTopics(Task t, MainTopic... topics) {
        List<MainTopic> mainTopics = Arrays.asList(topics);
        t.setTopics(mainTopics);
    }
            
    
    /**
     * Test of save method, of class DBManagerReal.
     */
    @Test
    public void testSave() {
        Task t = makeTask("task_2", new Level(1, ""), 5, 256);
        addLanguages(t, new Language(1, "", ""), new Language(3, "", ""));
        addTopics(t, new MainTopic(2, "main_top_1"), new MainTopic(4, "main_top_2"));
        instance.save(t);
        
    }

    /**
     * Test of updateTaskMainData method, of class DBManagerReal.
     */
    @Test @Ignore
    public void testUpdate() {
        System.out.println("update");
        Task t = null;
        DBManagerReal instance = null;
        Task expResult = null;
    }

    /**
     * Test of deleteTask method, of class DBManagerReal.
     */
    @Test
    public void testDelete() {
        int id = 2;
        boolean expResult = false;
        boolean result = instance.deleteTask(id);
    }

    /**
     * Test of getTaskBy method, of class DBManagerReal.
     */
    @Test
    public void testGetTaskBy() {
        int id = 2;
        Task expResult = makeTask("task_2", new Level(1, "მარტივი"), 5, 256);
        expResult.setId(id);
        addLanguages(expResult, new Language(1, "", ""), new Language(3, "", ""));
        addTopics(expResult, new MainTopic(2, ""), new MainTopic(4, ""));
        
        Task result = instance.getTaskBy(id);
        assertEquals(expResult, result);
    }

    /**
     * Test of getTasksIdsFor method, of class DBManagerReal.
     */
    @Test @Ignore
    public void testGetTasksIdsFor() {
        int topicId = 2;
        DBManagerReal instance = null;
        List<Integer> expResult = null;
        List<Integer> result = instance.getTasksIdsFor(topicId);
    }

    /**
     * Test of getLevels method, of class DBManagerReal.
     */
    @Test @Ignore
    public void testGetLevels() {
        System.out.println("getLevels");
        DBManagerReal instance = null;
        List<Level> expResult = null;
        List<Level> result = instance.getLevels();
    }
    
}
