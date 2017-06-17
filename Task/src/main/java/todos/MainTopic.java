/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todos;

import java.util.Objects;

/**
 *
 * @author Dato
 */
public class MainTopic {
    
    private int id;
    private String descrip = "";
    
    public MainTopic(){}
    
    public MainTopic(int id, String name){
        this.id = id;
        this.descrip = name;
    }

    public int getId() {
        return id;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescrip(String name) {
        this.descrip = name;
    }

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
        final MainTopic other = (MainTopic) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.descrip, other.descrip)) {
            return false;
        }
        return true;
    }
    
    public int compare(MainTopic other){
        return this.getId() - other.getId();
    }

    @Override
    public String toString() {
        return "MainTopic{" + "id=" + id + ", name=" + descrip + '}';
    }
    
}
