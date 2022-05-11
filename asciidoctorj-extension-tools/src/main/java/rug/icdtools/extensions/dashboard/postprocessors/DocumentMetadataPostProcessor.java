/*
 * Copyright (C) 2022 hcadavid
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implifed warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package rug.icdtools.extensions.dashboard.postprocessors;

import rug.icdtools.core.models.PublishedICDMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Postprocessor;
import rug.icdtools.extensions.dashboard.interfacing.docsapiclient.APIAccessException;
import rug.icdtools.extensions.dashboard.interfacing.docsapiclient.DashboardAPIClient;
import rug.icdtools.core.logging.AbstractLogger;
import rug.icdtools.core.logging.DocProcessLogger;
import rug.icdtools.core.logging.Severity;
import rug.icdtools.core.logging.loggers.InMemoryErrorLogger;
import rug.icdtools.core.logging.postprocessors.FailedErrorReportException;
import rug.icdtools.core.logging.postprocessors.PipelineFailureDetails;
import rug.icdtools.core.models.VersionedDocument;
import rug.icdtools.extensions.crossrefs.InternalDocumentCrossRefInlineMacroProcessor;
import rug.icdtools.extensions.dashboard.interfacing.docsapiclient.APIResources;

/**
 *
 * @author hcadavid
 */
public class DocumentMetadataPostProcessor extends Postprocessor {

    private static boolean visitingFirstDocument = true;

    private static Set<String> notVisited;

    @Override
    public String process(Document dcmnt, String output) {

        //This post-processor is used only if BACKEND_URL variable (dashboard API URL) is defined
        String backendURL = System.getProperty("BACKEND_URL");
        String backendCredentials = System.getProperty("BACKEND_CREDENTIALS");
        
        if (backendURL != null && !backendURL.trim().equals("")){
            if (backendCredentials != null && !backendCredentials.trim().equals("")) {
                try {
                    DocProcessLogger.getInstance().log("BACKEND_URL environment variable set to [" + backendURL + "]. BACKEND_CREDENTIALS also set.", Severity.INFO);
                    postToAPIWhenBuildingFinished(dcmnt, backendURL, backendCredentials);
                } catch (FailedMetadataReportException ex) {
                    //ex.printStackTrace();
                    //If this exception takes place, there are no means to post documentation
                    //metadata to the dashboard API, despite it has been configured to do so.
                    //Therefore, for consistency, it must exit with a non-zero result to make sure build process is
                    //reported as failed (no document is published)
                    DocProcessLogger.getInstance().log("Unable to publish metadata of the processed document. Building process must be stopped." + ex.getLocalizedMessage(), Severity.FATAL);
                    System.exit(1);
                }
            }
            else{
                DocProcessLogger.getInstance().log("BACKEND_URL environment variable was set, but no BACKEND_CREDENTIALS variable was defined. Building process must be stopped", Severity.FATAL);
            }
        }
        else {
            DocProcessLogger.getInstance().log("BACKEND_URL environment variable not set. Documentation dashboard API won't be used to post published document metadata.", Severity.INFO);
        }

        return output;

    }

    private void postToAPIWhenBuildingFinished(Document dcmnt, String backendURL, String backendCredentials) throws FailedMetadataReportException {
        StructuralNode stdoc = (StructuralNode) dcmnt;

        String currentFilePath = Paths.get(stdoc.getSourceLocation().getDir()).resolve(stdoc.getSourceLocation().getFile()).toString();

        //identify the documents to be visited by the post-processor so it can be identified which is the last one
        if (visitingFirstDocument) {

            try {
                try ( Stream<Path> walk = Files.walk(Paths.get(((StructuralNode) dcmnt).getSourceLocation().getDir()))) {
                    notVisited = walk
                            .filter(p -> !Files.isDirectory(p))
                            //.peek(p -> System.out.println(p+","+p.toString().endsWith(".adoc")))
                            .filter(f -> f.toString().endsWith(".adoc"))
                            .map(p -> p.toString())
                            .collect(Collectors.toSet());
                }

            } catch (IOException ex) {
                Logger.getLogger(DocumentMetadataPostProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                visitingFirstDocument = false;
                DocProcessLogger.getInstance().log("Documents to be visited before executing the post-processor:" + notVisited.size(), Severity.DEBUG);
            }
        }
        boolean currentDocVisited = notVisited.remove(currentFilePath);
        DocProcessLogger.getInstance().log("Removing from documents to be visited :" + currentFilePath + ":" + currentDocVisited, Severity.DEBUG);

        //True if this is the last document of the building process
        if (notVisited.isEmpty()) {
            AbstractLogger logger = DocProcessLogger.getInstance();

            //Post metadata to the dashboard API as succesfully published document
            if (logger instanceof InMemoryErrorLogger) {
                InMemoryErrorLogger mlogger = (InMemoryErrorLogger) logger;
                if (mlogger.getGlobalFatalErrorsCount()==0 && mlogger.getGlobalErrorsCount()==0 && mlogger.getGlobalFailedQualityGatesCount()==0) {
                    DocProcessLogger.getInstance().log("Documents built with no internal errors, document building errors, or failed quality gates. Performing online consistency checks." + backendURL, Severity.INFO);
                    postToAPI(backendURL,backendCredentials);
                    DocProcessLogger.getInstance().log("Consistency checks passed. Metadata posted to " + backendURL, Severity.INFO);
                }
                else{
                    DocProcessLogger.getInstance().log(String.format("Documents built with %d internal errors, %d document building error, and %d failed quality gates. No metadata will be posted to the API.",mlogger.getGlobalFatalErrorsCount(),mlogger.getGlobalErrorsCount(),mlogger.getGlobalFailedQualityGatesCount()), Severity.INFO);
                }
            }

        }

    }

    private void postToAPI(String backendURL, String backendCredentials) throws FailedMetadataReportException {

        String[] envVars = new String[]{
            "PIPELINE_ID",
            "PROJECT_NAME",
            "PIPELINE_ID",
            "DEPLOYMENT_URL",
            "SOURCE_URL",
            "COMMIT_AUTHOR",
            "CREATION_DATE",
            "COMMIT_TAG"
        };

        Map<String, String> cicdEnvProperties = new HashMap<>();
        for (String envVar : envVars) {
            String varValue = System.getProperty(envVar);
            if (varValue == null) {
                throw new FailedMetadataReportException("The document build process was expected to report published document's metadata to [" + backendURL + "], but required environment variables was not set:" + envVar);
            } else {
                cicdEnvProperties.put(envVar, varValue);
            }
        }

        ObjectMapper mapper = new ObjectMapper();

        PublishedICDMetadata icdMetadata = new PublishedICDMetadata();
        icdMetadata.setMetadata(cicdEnvProperties);
               
        //Collect reference documents data from CrossRef extension        
        Set<VersionedDocument> referencedDocs = InternalDocumentCrossRefInlineMacroProcessor.getOverallReferencedDocuments();
        
        //TODO check for complexity
        //If the check of referenced document fails due to API access errors 
        //(APIAccessException) throw an exception that will abort the building process.
        //If the check fails due to invalid doc cross-references, a last error 
        //report is posted before sending such exception.
        //If such report fails, make the overall building process fail.
        try {
            //check wether the referenced documents/versions exist
            Map<VersionedDocument,PublishedICDMetadata> refDocsDetails = checkReferencedDocuments(referencedDocs,backendURL,backendCredentials);
            
        } catch (APIAccessException ex) {
            throw new FailedMetadataReportException("Unable to access documentation management API (" + backendURL + ")");
        } catch (InvalidDocumentReferenceException ex) {            
            try {
                List<String> errorsList = new LinkedList<>();
                errorsList.add(ex.getLocalizedMessage());
                postErrorsToAPI(errorsList, "all files", backendURL);                
                throw new FailedMetadataReportException("Post-processing error: references to non registered documents reported:"+ex.getLocalizedMessage());
            } catch (FailedErrorReportException ex1) {
                DocProcessLogger.getInstance().log("Unable to report previous errors. Building process must be stopped."+ex.getLocalizedMessage(), Severity.FATAL);
                System.exit(1);
            }
        }
        
        icdMetadata.setReferencedDocs(referencedDocs);
                
        try {
            //PUT to https://[apiurl]/v1/icds/{icdid}/current")
            String jsonIcdMetadata = mapper.writeValueAsString(icdMetadata);
            DashboardAPIClient apiClient = new DashboardAPIClient(backendURL,backendCredentials);
            String urlPath = String.format("/v1/icds/%s/current", cicdEnvProperties.get("PROJECT_NAME"));            
            apiClient.putResource(urlPath, jsonIcdMetadata);
            DocProcessLogger.getInstance().log("Metadata posted to " + backendURL+urlPath, Severity.INFO);
            
        } catch (JsonProcessingException | APIAccessException ex) {
            throw new FailedMetadataReportException("The document build process was expected to report errors to the API at " + backendURL + ", but the request failed or coldn't be performed:" + ex.getLocalizedMessage(), ex);
        }

    }
    
    /**
     * 
     * @param referencedDocs
     * @param backendUrl
     * @param backendCredentials
     * @return
     * @throws APIAccessException if the connection to the backend failed
     * @throws InvalidDocumentReferenceException if a referenced document is not available
     */
    public Map<VersionedDocument,PublishedICDMetadata> checkReferencedDocuments(Set<VersionedDocument> referencedDocs, String backendUrl, String backendCredentials) throws APIAccessException, InvalidDocumentReferenceException {
        DashboardAPIClient apiClient = new DashboardAPIClient(backendUrl, backendCredentials);
        Map<VersionedDocument,PublishedICDMetadata> docsDetails = new LinkedHashMap<>();
        for (VersionedDocument refdoc:referencedDocs){
            try {
                docsDetails.put(refdoc, apiClient.getResource(String.format(APIResources.DOCUMENT_RESOURCE_URL, refdoc.getDocName(),refdoc.getVersionTag()), PublishedICDMetadata.class));
            } catch (APIAccessException ex) {
                throw new InvalidDocumentReferenceException(refdoc.getDocName(),refdoc.getVersionTag(),"Referenced document "+refdoc.getDocName()+", version "+refdoc.getVersionTag()+" is not registered on the documentation management system.");
            }
        }
        return docsDetails;
        
    }
    

    private void postErrorsToAPI(List<String> errors,String docFileName, String backendURL) throws FailedErrorReportException{
        
            String credentials = System.getProperty("BACKEND_CREDENTIALS"); 
            String pipelineId = System.getProperty("PIPELINE_ID");
            String icdId = System.getProperty("PROJECT_NAME");
            String versionTag = System.getProperty("COMMIT_TAG");

           
            if (credentials==null){
                throw new FailedErrorReportException("The document build process was expected to report errors to the API at ["+backendURL+"], but no credentials were provided (BACKEND_CREDENTIALS sysenv)");
            }
            else if (pipelineId == null || icdId == null){
                throw new FailedErrorReportException("The document build process was expected to report errors to the API at ["+backendURL+"], but required GitLab environment variables were not found (the process is expected to run within a GitLab CI/CD environment)");                
            }
            else{
                ObjectMapper mapper = new ObjectMapper();

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                PipelineFailureDetails docBuildingFailureDetails = new PipelineFailureDetails();
                docBuildingFailureDetails.setDate(dtf.format(now));
                docBuildingFailureDetails.setdocName(docFileName);
                docBuildingFailureDetails.setErrors(new LinkedList<>());
                docBuildingFailureDetails.setFatalErrors(errors);
                docBuildingFailureDetails.setFailedQualityGates(new LinkedList<>());

                String jsonObject;
                try {
                    //Posting to https://[apiurl]/v1/icds/{icdid}/{version}/{pipelineid}/errors")
                    jsonObject = mapper.writeValueAsString(docBuildingFailureDetails);
                    DashboardAPIClient apiClient = new DashboardAPIClient(backendURL,credentials);
                    String urlPath = String.format("/v1/icds/%s/%s/%s/errors",icdId,versionTag,pipelineId);
                    apiClient.postResource(urlPath, jsonObject);
                    DocProcessLogger.getInstance().log("Post-processing errors reported to "+ backendURL, Severity.INFO);

                } catch (JsonProcessingException | APIAccessException ex) {
                    throw new FailedErrorReportException("The document build process was expected to report errors to the API at " + backendURL + ", but the request failed or coldn't be performed:" + ex.getLocalizedMessage(), ex);
                }

            }
        
        
    }

    
    
}

