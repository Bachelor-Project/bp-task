/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todos;

import anums.ExceptionType;

/**
 *
 * @author dato
 */
public class ExecResult {
    
    private String testName;
    private ExceptionType exType;
    private String Message;
    
    public ExecResult(){}

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public ExceptionType getExType() {
        return exType;
    }

    public void setExType(ExceptionType exType) {
        this.exType = exType;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    @Override
    public String toString() {
        return "ExecResult{" + "testName=" + testName + ", exType=" + exType + ", Message=" + Message + '}';
    }
    
}
