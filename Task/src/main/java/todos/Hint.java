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
public class Hint {
    
    public int id;
    public boolean isCode;
    public String title;
    private StringBuilder content = new StringBuilder();

    public String getContent() {
        return content.toString();
    }

    public void addContent(String content) {
        this.content.append(content);
    }
    
    
}
