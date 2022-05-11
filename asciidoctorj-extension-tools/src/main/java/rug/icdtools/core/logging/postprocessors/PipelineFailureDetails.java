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
package rug.icdtools.core.logging.postprocessors;

import java.io.Serializable;
import java.util.List;

public class PipelineFailureDetails implements Serializable {

    String date;

    String adocName;

    List<String> errors;

    List<String> fatalErrors;

    List<String> failedQualityGates;

    public List<String> getFailedQualityGates() {
        return failedQualityGates;
    }

    public void setFailedQualityGates(List<String> failedQualityGates) {
        this.failedQualityGates = failedQualityGates;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getFatalErrors() {
        return fatalErrors;
    }

    public void setFatalErrors(List<String> fatalErrors) {
        this.fatalErrors = fatalErrors;
    }

    public String getdocName() {
        return adocName;
    }

    public void setdocName(String adocName) {
        this.adocName = adocName;
    }

}
