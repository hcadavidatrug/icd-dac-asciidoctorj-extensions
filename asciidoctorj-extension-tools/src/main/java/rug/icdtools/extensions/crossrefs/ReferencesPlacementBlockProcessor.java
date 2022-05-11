/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.extensions.crossrefs;

import java.util.Map;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;

/**
 *
 * @author hcadavid
 */
public class ReferencesPlacementBlockProcessor extends BlockMacroProcessor{

    public static final String REFERENCES_PLACEMENT_ID="rug_icdtools_ReferencesListPlaceholder";
    
    @Override
    public Object process(StructuralNode parent, String string, Map<String, Object> map) {
        String output = String.format("<table class=\"tableblock frame-all grid-all stretch\" id=\"%s\"></table>", REFERENCES_PLACEMENT_ID);
        return createBlock(parent,"pass",output);
    }
    
}

