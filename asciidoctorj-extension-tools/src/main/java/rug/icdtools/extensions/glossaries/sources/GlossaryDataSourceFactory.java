/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.extensions.glossaries.sources;

import java.util.HashMap;

/**
 *
 * @author hcadavid
 */
public class GlossaryDataSourceFactory {

    private static final HashMap<String, String> acronyms = new HashMap<>();

    //Dummy data for a proof of concept
    static {
        acronyms.put("AIPS", "Astronomical Information Processing System or classical AIPS");
        acronyms.put("AIPS++", "The AIPS++ project was a project from the nineties supposed to replace the original Astronomical Information Processing System or classical AIPS. The ++ comes from it being mainly developed in C++. It’s also known as AIPS 2. It evolved into CASA, Casacore and Casarest (see those entries).");
        acronyms.put("APERTIF", "APERture Tile In Focus, Aperture Array receiver in the focal plane of the WSRT");
        acronyms.put("ARTEMIS", "Advanced Radio Transient Event Monitor and Identification System, GPU system applied at Nancay and Chilbolton International stations");
        acronyms.put("ARTS", "APERTIF Radio Transient System, Transient system attached to APERTIF");
        acronyms.put("ASTRON", "Netherlands Institute for Radio Astronomy");
        acronyms.put("CASA", "The Common Astronomy Software Applications package. User software for radioastronomy devel- oped out of the old AIPS++ project. The project is led by NRAO with contributions from ESO, CSIRO/ATNF, NAOJ and ASTRON. [?]");
        acronyms.put("Casacore", "The set of C++ libraries that form the basis of CASA and several other astronomical packages. It contains classes for storing and handling visibility and image data, RDBMS-like table system and handling coordinates. Mainly maintained by ASTRON and CSIRO/ATNF. [?]");
        acronyms.put("Casarest", "The libraries and tools from the old AIPS++ project that are not part of Casacore or CASA but still in use.");
        acronyms.put("CIT", "Centrum voor Informatie Techonolgy van de Rijksuniversiteit Groningen");
        acronyms.put("COBALT", "COrrelator and Beamforming Application platform for the Lofar Telescope");
        acronyms.put("COBALT2.0", "Successor to COBALT");
        acronyms.put("DAWN", "EoR KSP Computing cluster");
        acronyms.put("DDT", "Director Discretionary Time");
        acronyms.put("DUPLLO", "Digital Upgrade for Premier LOFAR Low-band Observing. Project to upgrade the Dutch LOFAR stations, including the central clock.");
        acronyms.put("EoR", "Epoch of Reionization");
        acronyms.put("ILT", "International Lofar Telescope consortium");
        acronyms.put("INAF", "Instituto Nazionale di Astrofisica");
        acronyms.put("KSP", "Key Science Program");
        acronyms.put("LOFAR", "The LOw Frequency ARray. LOFAR is a multipurpose sensor array; its main application is astronomy at low radio frequencies, but it also has geophysical and agricultural applications. [http://www.lofar.org/]");
        acronyms.put("LOFAR1", "LOFAR as it was built between 2009 and 2019");
        acronyms.put("LOFAR2.0'", "LOFAR after upgrading it; DUPLLO is part of the upgrade path");
        acronyms.put("LOFAR2.0 DUPLLO", "LOFAR after upgrading according to the DUPLLO plan");
        acronyms.put("LOFAR2.0 SW", "LOFAR after upgrading according to the DUPLLO plan and after the Space Weather proposal extension");
        acronyms.put("LOFAR4SW", "LOFAR for Space Weather project");
    }

    public static GlossaryDataSource getDataSource(){
        return new GlossaryDataSource() {
            @Override
            public String acronymMeaning(String acronym) {
                return acronyms.get(acronym);
            }

            @Override
            public String definition(String concept) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public String abbreviationMeaning(String abbr, String context) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };
    }
    
    
    
}
