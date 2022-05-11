/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.extensions.glossaries;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.PhraseNode;
import org.asciidoctor.extension.InlineMacroProcessor;
import rug.icdtools.core.logging.DocProcessLogger;
import rug.icdtools.core.logging.Severity;

/**
 *
 * @author hcadavid
 */
public class AcronymInlineMacroProcessor extends InlineMacroProcessor {

    private final static Set<String> acronymsInstancesSet = new LinkedHashSet<>();
    
    public static Set<String> acronymsInstances(){
        return acronymsInstancesSet;
    }
    
    public static void resetAcronymsInstancesSet(){
        acronymsInstancesSet.clear();
    }
    
   @Override
    public Object process(ContentNode contentNode, String term, Map<String, Object> attributes) {

        DocProcessLogger.getInstance().log("Adding/formatting glossary term:"+term, Severity.DEBUG);
        // Define options for an 'anchor' element:
        Map<String, Object> options = new HashMap<>();
        options.put("type", ":link");
        options.put("target", "#"+term);
        acronymsInstancesSet.add(term);

        // Create the 'anchor' node:
        PhraseNode glossaryAnchorLink = createPhraseNode(contentNode, "anchor", term, attributes, options);

        // Convert to String value:
        return glossaryAnchorLink.convert();
    }

}
