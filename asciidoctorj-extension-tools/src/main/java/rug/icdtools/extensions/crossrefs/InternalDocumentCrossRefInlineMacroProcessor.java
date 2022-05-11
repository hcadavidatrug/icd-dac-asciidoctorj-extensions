/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.extensions.crossrefs;

import rug.icdtools.core.models.VersionedDocument;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
public class InternalDocumentCrossRefInlineMacroProcessor extends InlineMacroProcessor {


    private static final Set<VersionedDocument> overallReferencedDocuments = new LinkedHashSet<>();
    private static final Map<VersionedDocument,String> docNameToRefLabel=new HashMap<>();
    private static final List<VersionedDocument> refDetailsOrderedList = new LinkedList<>();

    public static Set<VersionedDocument> getOverallReferencedDocuments() {
        return overallReferencedDocuments;
    }
    
    public static void resetDocumentReferences(){
        docNameToRefLabel.clear();
        refDetailsOrderedList.clear();
    }
    
    public static Map<VersionedDocument, String> getDocNameToRefLabelMap() {
        return docNameToRefLabel;
    }
    public static List<VersionedDocument> getRefDetailsOrderedList() {
        return refDetailsOrderedList;
    }

    
    @Override
    public Object process(ContentNode contentNode, String docName, Map<String, Object> attributes) {

        
        String versionTag=(String)attributes.get("version");       

        DocProcessLogger.getInstance().log("Adding/formatting external reference:"+docName, Severity.DEBUG);
        
        if (docName != null && !docName.isEmpty()) {
            
            VersionedDocument docKey =new VersionedDocument(docName, versionTag);                    
            
            String docRefLabel = docNameToRefLabel.get(docKey);                       
                        
            if (docRefLabel==null){
                docRefLabel = "(REF"+(docNameToRefLabel.size()+1)+")";
                docNameToRefLabel.put(docKey, docRefLabel);
                refDetailsOrderedList.add(docKey);
                overallReferencedDocuments.add(docKey);
            }                                                                      

            // Define options for an 'anchor' element:
            Map<String, Object> options = new HashMap<>();
            options.put("type", ":link");
            options.put("target", "#"+docKey.toString());            
            
            // Create the 'anchor' node:
            PhraseNode inlineRefDocLink = createPhraseNode(contentNode, "anchor", docRefLabel, attributes, options);
            return inlineRefDocLink.convert();
        } else {
            throw new RuntimeException();
        }


    }
    
}

