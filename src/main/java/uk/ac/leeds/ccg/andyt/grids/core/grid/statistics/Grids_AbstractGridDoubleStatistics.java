/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.statistics;

import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGridDoubleStatistics
        extends Grids_AbstractGridNumberStatistics 
        implements Serializable {
    
    /**
     * For storing the minimum value.
     */
    protected double Min;
    /**
     * For storing the maximum value.
     */
    protected double Max;

    protected Grids_AbstractGridDoubleStatistics() {
    }

    public Grids_AbstractGridDoubleStatistics(Grids_Environment ge) {
        super(ge);
        init();
    }

    public Grids_AbstractGridDoubleStatistics(Grids_AbstractGridNumber g) {
        super(g);
        init();
    }

    /**
     * For initialisation.
     */
    private void init() {
        Min = Double.MAX_VALUE;
        Max = -Double.MAX_VALUE;
    }

    /**
     *
     * @return (Grids_GridDouble) Grid
     */
    public Grids_GridDouble getGrid() {
        return (Grids_GridDouble) Grid;
    }
    
}
