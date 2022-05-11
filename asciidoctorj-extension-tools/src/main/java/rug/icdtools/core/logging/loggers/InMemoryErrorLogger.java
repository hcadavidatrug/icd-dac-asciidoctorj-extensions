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
package rug.icdtools.core.logging.loggers;

import java.util.LinkedList;
import java.util.List;
import rug.icdtools.core.logging.AbstractLogger;
import rug.icdtools.core.logging.Severity;

/**
 *
 * @author hcadavid
 */
public class InMemoryErrorLogger implements AbstractLogger {

    //TODO for a proof-of-concept
    //use other intermediate persistence approaches (for potentially larger logs)
    private static final List<String> errors = new LinkedList<>();
    private static final List<String> fatalErrors = new LinkedList<>();
    private static final List<String> failedQualityGates = new LinkedList<>();
    private static int globalErrorsCount = 0;
    private static int globalFailedQualityGatesCount = 0;
    private static int globalFatalErrorsCount = 0;

    private static final int STDOUD_MAX_MESSAGE_LENGTH = 1300;

    public int getGlobalFatalErrorsCount() {
        return globalFatalErrorsCount;
    }

    public int getGlobalErrorsCount() {
        return globalErrorsCount;
    }

    public int getGlobalFailedQualityGatesCount() {
        return globalFailedQualityGatesCount;
    }

    public List<String> getFailedQualityGates() {
        return failedQualityGates;
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getFatalErrors() {
        return fatalErrors;
    }

    public void resetErrorLogs() {
        errors.clear();
        fatalErrors.clear();
    }

    public boolean isErrorLogsEmpty() {
        return errors.isEmpty() && fatalErrors.isEmpty() && failedQualityGates.isEmpty();
    }

    @Override
    public void log(String log, Severity severity) {

        String stdoutMessage = log.length() > STDOUD_MAX_MESSAGE_LENGTH ? String.format("*[%s] - %s... (full details in log files)", severity, subString(log, STDOUD_MAX_MESSAGE_LENGTH)) : String.format("*[%s] - %s", severity, log);

        switch (severity) {
            case FAILED_QGATE:
                System.out.println(stdoutMessage);
                failedQualityGates.add(log);
                globalFailedQualityGatesCount++;
                break;
            case ERROR:
                System.out.println(stdoutMessage);
                errors.add(log);
                globalErrorsCount++;
                break;
            case FATAL:
                System.out.println(stdoutMessage);
                fatalErrors.add(log);
                globalFatalErrorsCount++;
                break;
            default:
                System.out.println(stdoutMessage);
                break;
        }

    }

    private String subString(String s, int maxChars) {
        return s.substring(0, Math.min(s.length(), maxChars));
    }

}
