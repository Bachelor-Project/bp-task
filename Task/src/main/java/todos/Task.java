/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Dato
 */
public class Task {
    
    private int id, timeLimit, memoryLimit;
    private String name;
    private Level level = new Level();
    private MainTopic mainTopic = new MainTopic();
    
    public Task(){
    }

    public int getId() {
        return id;
    }

    public Level getLevel() {
        return level;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public String getName() {
        return name;
    }
    
    public MainTopic getMainTopic(){
        return mainTopic;
    }


    
    public void setId(int id) {
        this.id = id;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setMemoryLimit(int memeoryLimit) {
        this.memoryLimit = memeoryLimit;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setMainTopic(MainTopic mainTopic){
        this.mainTopic = mainTopic;
    }

//    public void setLanguages(List<Language> languages) {
//        this.languages = languages;
//    }
//
//    public void setTopics(List<MainTopic> topics) {
//        this.topics = topics;
//    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Task other = (Task) obj;
        if (this.timeLimit != other.timeLimit) {
            return false;
        }
        if (this.memoryLimit != other.memoryLimit) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return !Objects.equals(this.level, other.level);
    }
    
    private boolean compareLanguages(List<Language> langes1, List<Language> langes2){
        if (langes1.size() != langes2.size()){
            return false;
        }
        langes1.sort((Language lang1, Language lang2) -> lang1.compare(lang2));
        langes2.sort((Language lang1, Language lang2) -> lang1.compare(lang2));
        return Arrays.equals(langes1.toArray(), langes2.toArray());
    }
    
     private boolean compareTopics(List<MainTopic> topics1, List<MainTopic> topics2) {
        if (topics1.size() != topics2.size()){
            return false;
        }
        topics1.sort((MainTopic lang1, MainTopic lang2) -> lang1.compare(lang2));
        topics2.sort((MainTopic lang1, MainTopic lang2) -> lang1.compare(lang2));
        return Arrays.equals(topics1.toArray(), topics2.toArray());
     }
    
    @Override
    public String toString() {
        return "Task{" + "id=" + id + ", timeLimit=" + timeLimit + 
                        ", memeoryLimit=" + memoryLimit + ", name=" + name + 
                        ", level=" + level +
                        ", mainTopic=" + mainTopic + "}";
    }

}
