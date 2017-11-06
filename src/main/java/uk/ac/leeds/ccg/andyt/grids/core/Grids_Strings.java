/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.core;

import uk.ac.leeds.ccg.andyt.generic.core.Generic_Strings;

/**
 *
 * @author geoagdt
 */
public class Grids_Strings extends Generic_Strings {
    
    protected String String_Grids;
    protected String String_GridInt;
    protected String String_GridDouble;
    protected String String_GridIntFactory;
    protected String String_GridDoubleFactory;

    public Grids_Strings() {
    }

    public String getString_Grids() {
        if (String_Grids == null) {
            String_Grids = "Grids";
        }
        return String_Grids;
    }

    public String getString_GridDouble() {
        if (String_GridDouble == null) {
            String_GridDouble = "GridDouble";
        }
        return String_GridDouble;
    }

    public String getString_GridInt() {
        if (String_GridInt == null) {
            String_GridInt = "GridInt";
        }
        return String_GridInt;
    }

    public String getString_GridDoubleFactory() {
        if (String_GridDoubleFactory == null) {
            String_GridDoubleFactory = "GridDoubleFactory";
        }
        return String_GridDoubleFactory;
    }

    public String getString_GridIntFactory() {
        if (String_GridIntFactory == null) {
            String_GridIntFactory = "GridIntFactory";
        }
        return String_GridIntFactory;
    }
    
}
