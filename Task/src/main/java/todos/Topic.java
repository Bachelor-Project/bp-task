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
public class Topic {
    
    private String name, fielExt;
    private MainTopic mainTopic = new MainTopic();
    private int id, priority;
    
    public Topic(){}

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFielExt() {
        return fielExt;
    }

    public void setFileExt(String fielExt) {
        this.fielExt = fielExt;
    }

    public MainTopic getMainTopic() {
        return mainTopic;
    }

    public void setMainTopic(MainTopic mainTopic) {
        this.mainTopic = mainTopic;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Topic{" + "name=" + name + ", fielExt=" + fielExt + ", mainTopic=" + mainTopic + ", priority=" + priority + '}';
    }
    
    
}
