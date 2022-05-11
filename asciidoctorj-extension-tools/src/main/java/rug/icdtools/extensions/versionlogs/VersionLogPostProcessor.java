/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.extensions.versionlogs;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Postprocessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import rug.icdtools.core.logging.DocProcessLogger;
import rug.icdtools.core.logging.Severity;
import rug.icdtools.core.models.VersionTagEntry;

/**
 *
 * @author hcadavid
 */
public class VersionLogPostProcessor extends Postprocessor {

    
    private static final String LOG_TAGS_FILE_PATH = "LOG_TAGS_FILE_PATH";
    
    private static final String COL_GROUP = "<colgroup> <col style=\"width: 10%;\"> <col style=\"width: 20%;\"> <col style=\"width: 70%;\"> </colgroup>";
    private static final String TABLE_HEADER = "<thead><tr><th class=\"tableblock halign-left valign-top\">%s</th><th class=\"tableblock halign-left valign-top\">%s</th><th class=\"tableblock halign-left valign-top\">%s</th></tr></thead> ";
    private static final String TAB_BODY_OPEN = "<tbody>";
    private static final String TAB_ROW = "<tr> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td></tr>";
    private static final String TAB_BODY_CLOSE = "</tbody>";

    
    @Override
    public String process(Document dcmnt, String output) {

        String logTagsFilePath = System.getProperty(LOG_TAGS_FILE_PATH);

        
        if (logTagsFilePath != null && !logTagsFilePath.trim().equals("")) {

            org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(output, "UTF-8");
            Element versionLogPlaceholder = jsoupDoc.getElementById(VersionLogPlacementBlockProcessor.VERSION_LOG_PLACEMENT_ID);
            
            if (versionLogPlaceholder != null) {
                List<VersionTagEntry> tagEntries;
                try {
                    tagEntries = getTagsList(logTagsFilePath);
                } catch (LogHistoryExtractionException ex) {
                    DocProcessLogger.getInstance().log("Log information given in the file "+logTagsFilePath+" (given in variable "+LOG_TAGS_FILE_PATH+") couldn't be processed:"+ex.getLocalizedMessage(), Severity.FATAL);
                    return output;
                }
                versionLogPlaceholder.text(buildVersionLogHTMLCode(tagEntries));
                return jsoupDoc.html().replace("&lt;", "<").replace("&gt;", ">");
            }
            else{
                DocProcessLogger.getInstance().log("No macro for adding version log in the document was included. Skipping version log generation.", Severity.INFO);
                return output;
            }

        } else {
            DocProcessLogger.getInstance().log(LOG_TAGS_FILE_PATH + " environment variable not set. Skipping version log generation.", Severity.INFO);
            return output;
        }
     
    
      

    }
       
    private String buildVersionLogHTMLCode(List<VersionTagEntry> tags){
        StringBuilder sb = new StringBuilder();
        sb.append(COL_GROUP);
        sb.append(String.format(TABLE_HEADER, "Version","Release date","Description"));
        sb.append(TAB_BODY_OPEN);
        for (VersionTagEntry tag : tags) {
            sb.append(String.format(TAB_ROW, tag.getTag(), tag.getCreated(),tag.getSubject()));
        }
        sb.append(TAB_BODY_CLOSE);
        return sb.toString();
    }

        
    
    private List<VersionTagEntry> getTagsList(String filePath) throws LogHistoryExtractionException{
        File logsFilePath = new File(filePath);

        ObjectMapper jsonMapper = new ObjectMapper();

        String line=null;
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(logsFilePath));
            line = reader.readLine();
            VersionTagEntry entry;
            List<VersionTagEntry> tagsList = new LinkedList<>();
            while (line != null) {
                entry = jsonMapper.readValue(line, VersionTagEntry.class);
                tagsList.add(entry);
                line = reader.readLine();
            }
            reader.close();

            return tagsList;

        } catch (FileNotFoundException ex) {
            throw new LogHistoryExtractionException("Logs file [" + filePath + "] does not exist.",ex);
        } catch (JsonParseException | JsonMappingException ex) {
            throw new LogHistoryExtractionException("Malformed log in file [" + filePath +"]:"+line,ex);
        } catch (IOException ex) {
            throw new LogHistoryExtractionException("I/O error while reading Logs file " + filePath + ".",ex);
        }

    }

}
