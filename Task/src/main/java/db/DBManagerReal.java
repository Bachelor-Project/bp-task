/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import Interfaces.DBManager;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import todos.Language;
import todos.Level;
import todos.MainTopic;
import todos.Task;
import todos.TopicType;

/**
 *
 * @author Dato
 */
public class DBManagerReal implements DBManager {

    private final String connectionState = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState";
    private final String statementFinalizer = "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";
    private DataSource datasource;
    
    public static DBManagerReal instance = new DBManagerReal();
    
    private DBManagerReal(){
        initDB();
    }
    
    private void initDB(){
        PoolProperties pool = new PoolProperties();
        pool.setUrl(DBData.DB_PATH);
        pool.setDriverClassName(DBData.JDBC_DRIVER);
        pool.setUsername(DBData.USERNAME);
        pool.setPassword(DBData.PASSWORD);
        pool.setMaxActive(256);
        pool.setInitialSize(16);
        pool.setMaxWait(10000);
        pool.setJdbcInterceptors(connectionState + ";" + statementFinalizer);
        
        datasource = new DataSource();
        datasource.setPoolProperties(pool);
    }
    
    @Override
    public void save(Task t) {
        try {
            Connection con = datasource.getConnection();
            saveTaskMainInfo(con, t);
            int taskId = getTaskIdBy(t.getName());
            for (Language lang : t.getLanguages()) {
                saveSupportedLanguage(con, taskId, lang.getId());
            }
            for (MainTopic topic : t.getTopics()) {
                saveMainTopic(con, taskId, topic.getId());
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
    }
    
    private void saveTaskMainInfo(Connection con, Task t) throws SQLException{
        try (CallableStatement stmt = con.prepareCall("call save_task(?, ?, ?, ?)")) {
            stmt.setString(1, t.getName());
            stmt.setInt(2, t.getLevel().getId());
            stmt.setInt(3, t.getTimeLimit());
            stmt.setInt(4, t.getMemeoryLimit());
            stmt.execute();
        }
    }
    
    private int getTaskIdBy(String name) {
        int result = 0;
        try {
            Connection con = datasource.getConnection();
            CallableStatement stmt = con.prepareCall("{?= call get_task_id(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, name);
            stmt.execute();
            
            result = stmt.getInt(1);
            con.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return result;
    }
    
    private void saveSupportedLanguage(Connection con, int taskId, int langId) throws SQLException {
        CallableStatement stmt = con.prepareCall("call save_supported_lang(?, ?)");
        stmt.setInt(1, taskId);
        stmt.setInt(2, langId);
        stmt.execute();
        stmt.close();
    }
    
    private void saveMainTopic(Connection con, int taskId, int topicId) throws SQLException {
        CallableStatement stmt = con.prepareCall("call save_associated_topic(?, ?)");
        stmt.setInt(1, taskId);
        stmt.setInt(2, topicId);
        stmt.execute();
        stmt.close();
    }

    @Override
    public void updateTaskMainData(Task t) {
        try {
            Connection con = datasource.getConnection();
            CallableStatement stmt = con.prepareCall("call update_config(?, ?, ?, ?, ?)");
            stmt.setInt(1, t.getId());
            stmt.setString(2, t.getName());
            stmt.setInt(3, t.getLevel().getId());
            stmt.setInt(4, t.getTimeLimit());
            stmt.setInt(5, t.getMemeoryLimit());
            stmt.execute();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean deleteTask(int id) {
        boolean result = true;
        try {
            Connection con = datasource.getConnection();
            CallableStatement stmt = con.prepareCall("call delete_task(?)");
            stmt.setInt(1, id);
            stmt.execute();
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            result = false;
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public Task getTaskBy(int id) {
        Task task = null;
        try {
            Connection con = datasource.getConnection();
            CallableStatement stmt = con.prepareCall("call select_task_info(?)");
            stmt.setInt(1, id);
            stmt.execute();
            
            ResultSet configRs = stmt.getResultSet();
            task = makeTaskFrom(configRs);
            
            stmt.getMoreResults();
            ResultSet languagesRs = stmt.getResultSet();
            task.setLanguages(makeLanguagesFrom(languagesRs));
            
            stmt.getMoreResults();
            ResultSet topicsRs = stmt.getResultSet();
            task.setTopics(makeMainTopicsFrom(topicsRs));
            
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        return task;
    }
    
    private Task makeTaskFrom(ResultSet set) throws SQLException{
        Task result = new Task();
        while (set.next()) {            
            result.setId(set.getInt(1));
            result.setName(set.getString(2));
            result.setTimeLimit(set.getInt(3));
            result.setMemeoryLimit(set.getInt(4));
            result.setLevel(new Level(set.getInt(5), set.getString(6)));
        }
        return result;
    }
    
    private List<Language> makeLanguagesFrom(ResultSet set) throws SQLException{
        List<Language> languages = new ArrayList<>();
        while(set.next()){
            Language lang = new Language();
            lang.setId(set.getInt(1));
            languages.add(lang);
        }
        return languages;
    }
    
    private List<MainTopic> makeMainTopicsFrom(ResultSet set) throws SQLException{
        List<MainTopic> topics = new ArrayList<>();
        while(set.next()){
            MainTopic topic = new MainTopic();
            topic.setId(set.getInt(1));
            topics.add(topic);
        }
        return topics;
    }

    @Override
    public List<Integer> getTasksIdsFor(int topicId) {
        List<Integer> ids = new ArrayList<>();
        try {
            Connection con = datasource.getConnection();
            CallableStatement stmt = con.prepareCall("call deleteLanguage(?)");
            stmt.setInt(1, topicId);
            stmt.execute();
            ResultSet set = stmt.getResultSet();
            while(set.next()){
                ids.add(set.getInt(1));
            }
            
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return ids;
    }

    @Override
    public List<Level> getLevels() {
        List<Level> levels = new ArrayList<>();
        try {
            Connection con = datasource.getConnection();
            CallableStatement stmt = con.prepareCall("call select_all_levels()");
            stmt.execute();
            levels = convertToLevels(stmt.getResultSet());
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return levels;
    }
    
    private List<Level> convertToLevels(ResultSet set) throws SQLException{
        List<Level> result = new ArrayList<>();
        while(set.next()){
            Level level = new Level(set.getInt(1), set.getString(2));
            result.add(level);
        }
        return result;
    }

    @Override
    public void addSupportedLanguage(int taskId, int langId) {
        try {
            Connection con = datasource.getConnection();
            saveSupportedLanguage(con, taskId, langId);
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    @Override
    public void deleteSupportedLanguage(int taskId, int langId) {
        try {
            Connection con = datasource.getConnection();
            deleteLanguage(con, taskId, langId);
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    private void deleteLanguage(Connection con, int taskId, int langId) throws SQLException{
        CallableStatement stmt = con.prepareCall("call delete_supported_languages(?, ?)");
            stmt.setInt(1, taskId);
            stmt.setInt(2, langId);
            stmt.execute();
            stmt.close();
    }

    @Override
    public void addAssociatedTopic(int taskId, int topicId) {
        try {
            Connection con = datasource.getConnection();
            saveMainTopic(con, taskId, topicId);
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void deleteAssociatedTopic(int taskId, int topicId) {
        try {
            Connection con = datasource.getConnection();
            deleteTopic(con, taskId, topicId);
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    private void deleteTopic(Connection con, int taskId, int topicId) throws SQLException{
        CallableStatement stmt = con.prepareCall("call delete_associated_topic(?, ?)");
        stmt.setInt(1, taskId);
        stmt.setInt(2, topicId);
        stmt.execute();
        stmt.close();
    }

    @Override
    public void save(MainTopic mt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<MainTopic> getMainTopics() {
        List<MainTopic> mainTopics = new ArrayList<>();
        try {
            Connection con = datasource.getConnection();
            
            System.out.println("call procedure");
            
            CallableStatement stmt = con.prepareCall("call select_all_main_topics()");
            stmt.execute();
            
            ResultSet rsSet = stmt.getResultSet();
            while(rsSet.next()){
                MainTopic mainTopic = new MainTopic(rsSet.getInt(1), rsSet.getString(2));
                mainTopics.add(mainTopic);
                
                System.out.println(mainTopic);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return mainTopics;
    }

    @Override
    public void updateMainTopic(int id, String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteMainTopic(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save(int mainTopicId, TopicType type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<TopicType> getTopicTypes(int mainTopicId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateTopicType(int mainTopicId, int topicId, String newDescrip) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteTopicType(int mainTopicId, int topicId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
