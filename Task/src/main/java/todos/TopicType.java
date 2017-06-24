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
public class TopicType {
    private int id;
    private String descrip = "";
    
    public TopicType(){}
    
    public TopicType(int id, String descrip){
        this.id = id;
        this.descrip = descrip;
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

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final TopicType other = (TopicType) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.descrip, other.descrip)) {
            return false;
        }
        return true;
    }

    
    
    @Override
    public String toString() {
        return "TopicType{" + "id=" + id + ", descrip=" + descrip + '}';
    }

    

}
