/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.extensions.glossaries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Postprocessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import rug.icdtools.core.logging.DocProcessLogger;
import rug.icdtools.core.logging.Severity;
import rug.icdtools.extensions.glossaries.sources.GlossaryDataSourceFactory;

/**
 *
 * @author hcadavid
 */
public class GlossaryPostProcessor extends Postprocessor {

    
    private static final String COL_GROUP = "<colgroup> <col style=\"width: 20%;\"> <col style=\"width: 80%;\"></colgroup>";
    private static final String TABLE_HEADER = "<thead><tr><th class=\"tableblock halign-left valign-top\">%s</th><th class=\"tableblock halign-left valign-top\">%s</th></tr></thead> ";
    private static final String TAB_BODY_OPEN = "<tbody>";
    private static final String TAB_ROW = "<tr id=\"%s\"> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td></tr>";
    private static final String TAB_BODY_CLOSE = "</tbody>";

    
    @Override
    public String process(Document dcmnt, String output) {
               
        
        org.jsoup.nodes.Document doc = Jsoup.parse(output, "UTF-8");

        Element glossaryPlaceholder = doc.getElementById(GlossaryPlacementBlockProcessor.GLOSSARY_PLACEMENT_ID);

        StringBuilder sb = new StringBuilder();
        sb.append(COL_GROUP);
        sb.append(String.format(TABLE_HEADER, "Acronym", "Description"));
        sb.append(TAB_BODY_OPEN);

        
        Set<String> docInlineAcronyms = AcronymInlineMacroProcessor.acronymsInstances();
        List<String> sortedAcronyms = new ArrayList<>(docInlineAcronyms);
        Collections.sort(sortedAcronyms);
        
        DocProcessLogger.getInstance().log("Generating glossary with "+sortedAcronyms.size()+" terms.", Severity.DEBUG);
        
        for (String acronym:sortedAcronyms){
            
            String meaning = GlossaryDataSourceFactory.getDataSource().acronymMeaning(acronym);
            
            sb.append(String.format(TAB_ROW,acronym,acronym,meaning!=null?meaning:"Not defined in the centralized glossary."));            
            
        }


        sb.append(TAB_BODY_CLOSE);

        if (glossaryPlaceholder != null) {
            glossaryPlaceholder.text(sb.toString());
        }

        //Reset acronyms instances so existing ones are not included
        //on other documents processed during the build process.
        AcronymInlineMacroProcessor.resetAcronymsInstancesSet();
        
        return doc.html().replace("&lt;", "<").replace("&gt;", ">");

    }

}
