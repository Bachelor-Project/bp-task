/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todos;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dato
 */
public class RunCodeDTO {
    
    private String lang;
    private String username;
    private int taskId;
    private boolean compiled;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public boolean isCompiled() {
        return compiled;
    }

    public void setCompiled(boolean compiled) {
        this.compiled = compiled;
    }

    @Override
    public String toString() {
        return "RunCodeDTO{" + "lang=" + lang + ", username=" + username + ", taskId=" + 
                taskId + ", compiled=" + compiled + '}';
    }
    
}
