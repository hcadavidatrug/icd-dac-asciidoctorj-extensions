/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.extensions.sysrdl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BaseProcessor;
import rug.icdtools.interfacing.localcommands.CommandExecutionException;
import rug.icdtools.interfacing.localcommands.CommandGeneratedException;
import rug.icdtools.interfacing.localcommands.CommandRunner;

/**
 *
 * @author hcadavid
 */
public class SystemRDL2AsciidocConverter {

    public static void convertAndAddToOutput(String registryMapName, File input, StructuralNode parent, BaseProcessor asccidocProcessor) throws CommandGeneratedException, FileNotFoundException, IOException, CommandExecutionException {

        File tmpFile = File.createTempFile("sysardl2adoc-", ".converter_output", null);

        CommandRunner.runCommand("sh", "sysrdl2jinja/convert_to_adoc.sh", input.getAbsolutePath(), tmpFile.getAbsolutePath());

        List<String> newOutputAsciidocLines;
        try ( Scanner s = new Scanner(tmpFile)) {
            newOutputAsciidocLines = new LinkedList<>();
            while (s.hasNext()) {
                newOutputAsciidocLines.add(s.next());
            }
        }

        //generate a C header and a link to it
        Path outputPath = Paths.get(System.getProperty("OUTPUT_PATH"));
        String headerFileName = registryMapName + ".h";

        //Creates a C header and its SHA256 checksum
        CommandRunner.runCommand("sh", "sysrdl2jinja/convert_to_cheader.sh", input.getAbsolutePath(), outputPath.resolve(headerFileName).toFile().getAbsolutePath());
        
        //newOutputAsciidocLines.add(String.format("pass:[<input type=\"text\" id=\"header\" value=\"window.location.href.substring(0, window.location.href.lastIndexOf('/'))+'/%s'\" readonly>]",headerFileName+".sha"));
        
        
        String copyHeaderButtonAction =  
                "var headerName='%s';var url = window.location.href.substring(0, window.location.href.lastIndexOf('/'))+'/'+headerName;" +
                "navigator.clipboard.writeText(url);"+
                "if (confirm('Copy the location of the generated header file to your clipboard? ('+url+')') == true) {"+
                "    navigator.clipboard.writeText(url); }";
                
        String copyChecksumButtonAction =  
                "var checksumName='%s';" +
                "var url = window.location.href.substring(0, window.location.href.lastIndexOf('/'))+'/'+checksumName;" +
                "var verUrl = url.substring(0,url.lastIndexOf('/'));" +
                "var docRoot = verUrl.substring(0,verUrl.lastIndexOf('/'));" +
                "url = docRoot+'/latest/'+checksumName;"+
                "navigator.clipboard.writeText(url);"+
                "if (confirm('Copy the location of the register map checksum to your clipboard? ('+url+')') == true) {"+
                "    navigator.clipboard.writeText(url); }";
                
        
        
        newOutputAsciidocLines.add(String.format("pass:[<button title =\"Copy header's checksum file location to your clipboard\" onClick=\""+copyChecksumButtonAction+"\">Copy header's checksum file location</button>]",headerFileName+".sha"));
        
        
        newOutputAsciidocLines.add(String.format("pass:[<button title =\"Copy generated header file location to your clipboard\"          onClick=\""+copyHeaderButtonAction+"\">Copy header's file location</button>]",headerFileName));
                
        asccidocProcessor.parseContent(parent, newOutputAsciidocLines);

    }

}
