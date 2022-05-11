/*
 * Copyright (C) 2022 hcadavid
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package rug.icdtools.registry;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;
import rug.icdtools.extensions.crossrefs.InternalDocumentCrossRefInlineMacroProcessor;
import rug.icdtools.extensions.crossrefs.ReferencesPlacementBlockProcessor;
import rug.icdtools.extensions.crossrefs.ReferencesPostProcessor;
import rug.icdtools.extensions.dashboard.postprocessors.DocumentMetadataPostProcessor;
import rug.icdtools.extensions.sysrdl.SystemRDLBlockMacroProcessor;
import rug.icdtools.extensions.sysrdl.SystemRDLBlockProcessor;
import rug.icdtools.extensions.glossaries.AcronymInlineMacroProcessor;
import rug.icdtools.extensions.glossaries.GlossaryPlacementBlockProcessor;
import rug.icdtools.extensions.glossaries.GlossaryPostProcessor;
import rug.icdtools.core.logging.postprocessors.JsonErrorLoggerPostProcessor;
import rug.icdtools.extensions.versionlogs.VersionLogPlacementBlockProcessor;
import rug.icdtools.extensions.versionlogs.VersionLogPostProcessor;

/**
 *
 * @author hcadavid
 */
public class ExtensionToolsRegistry implements ExtensionRegistry {

    @Override
    public void register(Asciidoctor asciidoctor) {
        JavaExtensionRegistry javaExtensionRegistry = asciidoctor.javaExtensionRegistry();        
        javaExtensionRegistry.postprocessor(JsonErrorLoggerPostProcessor.class);
        javaExtensionRegistry.postprocessor(DocumentMetadataPostProcessor.class);
        
        javaExtensionRegistry.blockMacro("systemrdl", SystemRDLBlockMacroProcessor.class);
        javaExtensionRegistry.block("systemrdl", SystemRDLBlockProcessor.class);        
        
        javaExtensionRegistry.inlineMacro("acr", AcronymInlineMacroProcessor.class);        
        javaExtensionRegistry.blockMacro("glossary", GlossaryPlacementBlockProcessor.class);
        javaExtensionRegistry.postprocessor(GlossaryPostProcessor.class);
        
        javaExtensionRegistry.inlineMacro("docref", InternalDocumentCrossRefInlineMacroProcessor.class);
        javaExtensionRegistry.blockMacro("references",ReferencesPlacementBlockProcessor.class);
        javaExtensionRegistry.postprocessor(ReferencesPostProcessor.class);
        
        javaExtensionRegistry.blockMacro("tagshistory",VersionLogPlacementBlockProcessor.class);
        javaExtensionRegistry.postprocessor(VersionLogPostProcessor.class);
        
        
    }

}
