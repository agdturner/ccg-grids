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

    public Grids_Strings() {
    }

    public String getString_Grids() {
        if (String_Grids == null) {
            String_Grids = "Grids";
        }
        return String_Grids;
    }

    
}
