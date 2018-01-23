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
    
    protected String S_Grids;
    protected String S_GridInt;
    protected String S_GridDouble;
    protected String S_Processor;

    public Grids_Strings() {
    }

    public String getS_Grids() {
        if (S_Grids == null) {
            S_Grids = "Grids";
        }
        return S_Grids;
    }

    public String getS_GridDouble() {
        if (S_GridDouble == null) {
            S_GridDouble = "GridDouble";
        }
        return S_GridDouble;
    }

    public String getS_GridInt() {
        if (S_GridInt == null) {
            S_GridInt = "GridInt";
        }
        return S_GridInt;
    }
    
    public String getS_Processor() {
        if (S_Processor == null) {
            S_Processor = "Processor";
        }
        return S_Processor;
    }
    
}
