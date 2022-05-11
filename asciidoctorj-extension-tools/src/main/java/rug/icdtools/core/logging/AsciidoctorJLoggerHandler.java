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
package rug.icdtools.core.logging;

import java.util.HashMap;
import org.asciidoctor.log.LogHandler;
import org.asciidoctor.log.LogRecord;
import org.asciidoctor.log.Severity;

/**
 * 
 * @author hcadavid
 */
public class AsciidoctorJLoggerHandler implements LogHandler {

    private static final HashMap<org.asciidoctor.log.Severity,rug.icdtools.core.logging.Severity> loggerLevelsMap = new HashMap<>();

    public AsciidoctorJLoggerHandler() {
        loggerLevelsMap.put(Severity.DEBUG, rug.icdtools.core.logging.Severity.DEBUG);
        loggerLevelsMap.put(Severity.ERROR, rug.icdtools.core.logging.Severity.ERROR);
        loggerLevelsMap.put(Severity.FATAL, rug.icdtools.core.logging.Severity.FATAL);
        loggerLevelsMap.put(Severity.INFO, rug.icdtools.core.logging.Severity.INFO);
        loggerLevelsMap.put(Severity.WARN, rug.icdtools.core.logging.Severity.WARN);
        loggerLevelsMap.put(Severity.UNKNOWN, rug.icdtools.core.logging.Severity.UNKNOWN);
    }
    
    
    
    @Override
    public void log(LogRecord logRecord) {        
        DocProcessLogger.getInstance().log("[From Asciidoctor core] "+logRecord.getMessage(),loggerLevelsMap.get(logRecord.getSeverity()));
        
    }
    

}
