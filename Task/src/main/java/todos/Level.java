/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todos;

/**
 *
 * @author Dato
 */
public class Level {
    
    private int id;
    private String descrip = "";
    
    public Level(){}
    
    public Level(int id, String descrip){
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
        final Level other = (Level) obj;
        if (this.id != other.id) {
            return false;
        }
        return this.descrip.equals(other.getDescrip());
    }

    @Override
    public String toString() {
        return "Level{" + "id=" + id + ", descrip=" + descrip + '}';
    }
    
    
}
