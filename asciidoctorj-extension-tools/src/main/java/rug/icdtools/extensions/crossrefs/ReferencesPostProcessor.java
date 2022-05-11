/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.extensions.crossrefs;

import java.util.LinkedHashMap;
import rug.icdtools.core.models.VersionedDocument;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Postprocessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import rug.icdtools.core.logging.DocProcessLogger;
import rug.icdtools.core.logging.Severity;
import rug.icdtools.core.models.PublishedICDMetadata;
import rug.icdtools.extensions.dashboard.interfacing.docsapiclient.APIAccessException;
import rug.icdtools.extensions.dashboard.interfacing.docsapiclient.APIResources;
import rug.icdtools.extensions.dashboard.interfacing.docsapiclient.DashboardAPIClient;
import rug.icdtools.extensions.dashboard.interfacing.docsapiclient.DocumentMetadataFields;
import rug.icdtools.extensions.dashboard.postprocessors.FailedMetadataReportException;
import rug.icdtools.extensions.dashboard.postprocessors.InvalidDocumentReferenceException;

/**
 *
 * @author hcadavid
 */
public class ReferencesPostProcessor extends Postprocessor {

    
    private static final String COL_GROUP = "<colgroup> <col style=\"width: 15%;\"> <col style=\"width: 15%;\"> <col style=\"width: 10%;\"> <col style=\"width: 60%;\"></colgroup>";
    private static final String TABLE_HEADER = "<thead><tr><th class=\"tableblock halign-left valign-top\">%s</th><th class=\"tableblock halign-left valign-top\">%s</th><th class=\"tableblock halign-left valign-top\">%s</th><th class=\"tableblock halign-left valign-top\">%s</th></tr></thead> ";
    private static final String TAB_BODY_OPEN = "<tbody>";
    private static final String TAB_ROW = "<tr id=\"%s\"> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td><td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td> </tr>";
    private static final String TAB_BODY_CLOSE = "</tbody>";

    
    @Override
    public String process(Document dcmnt, String output) {
                       
        org.jsoup.nodes.Document doc = Jsoup.parse(output, "UTF-8");

        Element glossaryPlaceholder = doc.getElementById(ReferencesPlacementBlockProcessor.REFERENCES_PLACEMENT_ID);

        StringBuilder sb = new StringBuilder();
        sb.append(COL_GROUP);
        sb.append(String.format(TABLE_HEADER, "Reference", "Document name","Version", "URL"));
        sb.append(TAB_BODY_OPEN);
       
        Map<VersionedDocument,String> docRefLabels = InternalDocumentCrossRefInlineMacroProcessor.getDocNameToRefLabelMap();
        List<VersionedDocument> orderedRefDocs = InternalDocumentCrossRefInlineMacroProcessor.getRefDetailsOrderedList();
        
        DocProcessLogger.getInstance().log("Generating references table with "+docRefLabels.size()+" terms.", Severity.INFO);
        
        String backendURL = System.getProperty("BACKEND_URL");
        String backendCredentials = System.getProperty("BACKEND_CREDENTIALS");
        
        if (backendURL != null && !backendURL.trim().equals("") && backendCredentials != null && !backendCredentials.trim().equals("")){            
            try {
                DocProcessLogger.getInstance().log("Pulling referenced documents details to add details on the references section.", Severity.INFO);
                DashboardAPIClient apiClient = new DashboardAPIClient(backendURL, backendCredentials);
                for (VersionedDocument refEntry:orderedRefDocs){
                    PublishedICDMetadata refMetadata = apiClient.getResource(String.format(APIResources.DOCUMENT_RESOURCE_URL, refEntry.getDocName(),refEntry.getVersionTag()), PublishedICDMetadata.class);
                    String refUrl = refMetadata.getMetadata().get(DocumentMetadataFields.DEPLOYMENT_URL);
                    String description;
                    if (refUrl!=null){
                        description = String.format("<a href=\"%s\">%s</a> ",refUrl,refUrl);
                    }
                    else{
                        description = "Not available";
                    }
                    
                    sb.append(String.format(TAB_ROW,refEntry.toString(),docRefLabels.get(refEntry),refEntry.getDocName(),refEntry.getVersionTag(),description));
                }
            } catch (APIAccessException ex) {
                DocProcessLogger.getInstance().log("Error while accessing referenced documents data:"+ex.getLocalizedMessage()+". No references table will be generated.", Severity.FATAL);                
            }
            
        }
        else {
            DocProcessLogger.getInstance().log("Referenced document details won't be pulled (details will be ommited): BACKEND_URL or BACKEND_CREDENTIALS environment variable were not set. ", Severity.INFO);
            for (VersionedDocument refEntry:orderedRefDocs){
                String description = "";
                sb.append(String.format(TAB_ROW,refEntry.toString(),docRefLabels.get(refEntry),refEntry.getDocName(),refEntry.getVersionTag(),description));                                                            
            }

        }


        sb.append(TAB_BODY_CLOSE);

        if (glossaryPlaceholder != null) {
            glossaryPlaceholder.text(sb.toString());
        }

        //Reset references map so existing ones are not included
        //on other documents processed during the build process.
        InternalDocumentCrossRefInlineMacroProcessor.resetDocumentReferences();
        
        return doc.html().replace("&lt;", "<").replace("&gt;", ">");

    }
    
    
    

}
