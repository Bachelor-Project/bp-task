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
public class Language {
    
    private int id;
    private String name = "", descrip = "";
    
    public Language(){}
    
    public Language(int id, String name, String descrip){
        this.id = id;
        this.name = name;
        this.descrip = descrip;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
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
        final Language other = (Language) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.descrip, other.descrip)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Language{" + "id=" + id + ", name=" + name + ", descrip=" + descrip + '}';
    }
    
    public int compare(Language other){
        return this.getId() - other.getId();
    }
    
}
