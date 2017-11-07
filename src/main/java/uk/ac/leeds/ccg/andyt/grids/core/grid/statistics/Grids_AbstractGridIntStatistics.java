/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.statistics;

import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridInt;

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGridIntStatistics
        extends Grids_AbstractGridNumberStatistics
        implements Serializable {

    /**
     * For storing the minimum value.
     */
    protected int Min;
    /**
     * For storing the maximum value.
     */
    protected int Max;

    protected Grids_AbstractGridIntStatistics() {
    }

    public Grids_AbstractGridIntStatistics(Grids_Environment ge) {
        super(ge);
        init();
    }

    public Grids_AbstractGridIntStatistics(Grids_AbstractGridNumber g) {
        super(g);
        init();
    }

    /**
     * For initialisation.
     */
    private void init() {
        Min = Integer.MIN_VALUE;
        Max = Integer.MAX_VALUE;
    }

    /**
     *
     * @return (Grids_GridInt) Grid
     */
    public Grids_GridInt getGrid() {
        return (Grids_GridInt) Grid;
    }

}
