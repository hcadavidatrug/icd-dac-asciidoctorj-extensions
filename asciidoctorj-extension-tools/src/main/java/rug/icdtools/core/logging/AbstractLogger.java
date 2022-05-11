/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rug.icdtools.core.logging;

/**
 *
 * @author hcadavid
 */
public interface AbstractLogger {
    
    public void log(String detailedLog, Severity severity);
    
}
