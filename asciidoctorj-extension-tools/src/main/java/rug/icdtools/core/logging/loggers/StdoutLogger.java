/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.core.logging.loggers;

import rug.icdtools.core.logging.AbstractLogger;
import rug.icdtools.core.logging.Severity;

/**
 *
 * @author hcadavid
 */
public class StdoutLogger implements AbstractLogger {

    @Override
    public void log(String log, Severity severity) {
        System.out.println(String.format("*[%s] - %s", severity, log));
    }

}
