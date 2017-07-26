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
import todos.MainTopicCounter;
import todos.MainTopicPriorityPair;
import todos.Task;
import todos.Topic;
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
            try (Connection con = datasource.getConnection(); CallableStatement stmt = con.prepareCall("call save_task(?, ?, ?, ?, ?)")) {
                stmt.setString(1, t.getName());
                stmt.setString(2, t.getLevel().getDescrip());
                stmt.setString(3, t.getMainTopic().getDescrip());
                stmt.setInt(4, t.getTimeLimit());
                stmt.setInt(5, t.getMemeoryLimit());
                stmt.execute();
            }
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
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean deleteTask(int id) {
        boolean result = true;
        try {
            try (Connection con = datasource.getConnection(); CallableStatement stmt = con.prepareCall("call delete_task(?)")) {
                stmt.setInt(1, id);
                stmt.execute();
            }
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
            try (Connection con = datasource.getConnection(); CallableStatement stmt = con.prepareCall("call select_task_info(?)")) {
                stmt.setInt(1, id);
                stmt.execute();
                ResultSet configRs = stmt.getResultSet();
                task = makeTaskFrom(configRs);
//            stmt.getMoreResults();
//            ResultSet languagesRs = stmt.getResultSet();
//            
//            stmt.getMoreResults();
//            ResultSet topicsRs = stmt.getResultSet();
            }
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
            try (Connection con = datasource.getConnection(); CallableStatement stmt = con.prepareCall("call deleteLanguage(?)")) {
                stmt.setInt(1, topicId);
                stmt.execute();
                ResultSet set = stmt.getResultSet();
                while(set.next()){
                    ids.add(set.getInt(1));
                }
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return ids;
    }

    @Override
    public List<Level> getLevels() {
        List<Level> levels = new ArrayList<>();
        try {
            try (Connection con = datasource.getConnection(); CallableStatement stmt = con.prepareCall("call select_all_levels()")) {
                stmt.execute();
                levels = convertToLevels(stmt.getResultSet());
            }
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
            try (Connection con = datasource.getConnection()) {
                saveSupportedLanguage(con, taskId, langId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    @Override
    public void deleteSupportedLanguage(int taskId, int langId) {
        try {
            try (Connection con = datasource.getConnection()) {
                deleteLanguage(con, taskId, langId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    private void deleteLanguage(Connection con, int taskId, int langId) throws SQLException{
        try (CallableStatement stmt = con.prepareCall("call delete_supported_languages(?, ?)")) {
            stmt.setInt(1, taskId);
            stmt.setInt(2, langId);
            stmt.execute();
        }
    }

    @Override
    public void addAssociatedTopic(int taskId, int topicId) {
        try {
            try (Connection con = datasource.getConnection()) {
                saveMainTopic(con, taskId, topicId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void deleteAssociatedTopic(int taskId, int topicId) {
        try {
            try (Connection con = datasource.getConnection()) {
                deleteTopic(con, taskId, topicId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    private void deleteTopic(Connection con, int taskId, int topicId) throws SQLException{
        try (CallableStatement stmt = con.prepareCall("call delete_associated_topic(?, ?)")) {
            stmt.setInt(1, taskId);
            stmt.setInt(2, topicId);
            stmt.execute();
        }
    }

    @Override
    public void save(String mainTopicName) {
        
    }

    @Override
    public List<MainTopic> getMainTopics() {
        List<MainTopic> mainTopics = new ArrayList<>();
        try {
            try (Connection con = datasource.getConnection()) {
                
                try (CallableStatement stmt = con.prepareCall("call select_all_main_topics()")) {
                    stmt.execute();
                    
                    ResultSet rsSet = stmt.getResultSet();
                    while(rsSet.next()){
                        MainTopic mainTopic = new MainTopic(rsSet.getInt(1), rsSet.getString(2));
                        mainTopics.add(mainTopic);
                    }
                }
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

    @Override
    public void save(Topic topic) {
        try {
            try (Connection con = datasource.getConnection(); CallableStatement stmt = con.prepareCall("call save_topic(?, ?, ?, ?)")) {
                stmt.setString(1, topic.getName());
                stmt.setString(2, topic.getFielExt());
                stmt.setString(3, topic.getMainTopic().getDescrip());
                stmt.setInt(4, topic.getPriority());
                stmt.execute();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<MainTopicPriorityPair> getTopicsWithPriorityFrom(int mainTopicID) {
        List<MainTopicPriorityPair> result = new ArrayList();
        try {
            try (Connection con = datasource.getConnection(); CallableStatement stmt = con.prepareCall("call select_topics_by_priority_from(?)")) {
                stmt.setInt(1, mainTopicID);
                stmt.execute();
                
                ResultSet rsSet = stmt.getResultSet();
                while(rsSet.next()){
                    MainTopicPriorityPair p = new MainTopicPriorityPair();
                    p.id = rsSet.getInt(1);
                    p.descrip = rsSet.getString(2);
                    p.priority = rsSet.getInt(3);
                    result.add(p);
                    
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public List<MainTopicCounter> getMainTopicsWithCount() {
        List<MainTopicCounter> result = new ArrayList<>();
        try {
            try (Connection con = datasource.getConnection(); CallableStatement stmt = con.prepareCall("call select_main_topics_with_count_for_topics()")) {
                stmt.execute();
                
                ResultSet rsSet = stmt.getResultSet();
                while(rsSet.next()){
                    MainTopic mt = new MainTopic(rsSet.getInt(1), rsSet.getString(2));
                    MainTopicCounter counter = new MainTopicCounter();
                    counter.mainTopic = mt;
                    counter.count = rsSet.getInt(3);
                    result.add(counter);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public String getMainTopicNameBy(int mainTopicId) {
        String result = "";
        try {
            try (Connection con = datasource.getConnection(); CallableStatement stmt = con.prepareCall("call select_main_topic_name_by(?)")) {
                stmt.setInt(1, mainTopicId);
                stmt.execute();
                
                ResultSet rsSet = stmt.getResultSet();
                while(rsSet.next()){
                    result = rsSet.getString(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManagerReal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        return result;
    }
}
