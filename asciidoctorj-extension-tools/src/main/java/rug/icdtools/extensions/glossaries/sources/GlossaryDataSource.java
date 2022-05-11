/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.extensions.glossaries.sources;

/**
 *
 * @author hcadavid
 */
public interface GlossaryDataSource {
    
    public String acronymMeaning(String acronym);
    
    public String definition(String concept);
    
    public String abbreviationMeaning(String abbr, String context);    
    
}
