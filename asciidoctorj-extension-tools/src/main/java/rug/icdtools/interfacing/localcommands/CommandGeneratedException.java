/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rug.icdtools.interfacing.localcommands;

/**
 *
 * @author hcadavid
 */
public class CommandGeneratedException extends Exception{

    private final String localizedMessage;
    
    public CommandGeneratedException(String message, Throwable cause, String localizedMessage) {
        super(message, cause);
        this.localizedMessage = localizedMessage;
    }

    public CommandGeneratedException(String message, String localizedMessage) {
        super(message);
        this.localizedMessage = localizedMessage;

    }

    @Override
    public String getLocalizedMessage() {
        return localizedMessage;
    }

    
    
}
