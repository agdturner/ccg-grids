/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.stats;

import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid;

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGridStats extends Grids_Object {

    /**
     * A reference to the Grid.
     */
    public transient Grids_AbstractGrid grid;

    /**
     * For storing the number of cells with values.
     */
    protected long n;

    public Grids_AbstractGridStats(Grids_Environment ge) {
        super(ge);
        n = 0;
    }

    protected void init() {
        n = 0;
    }

    public abstract long getN();

    /**
     * @param n to set n to.
     */
    public void setN(long n) {
        this.n = n;
    }

    /**
     * @return true iff the stats are kept up to date as the underlying data
     * change.
     */
    public abstract boolean isUpdated();

    /**
     * Updates by going through all values in grid if the fields are likely not
     * be up to date.
     */
    protected abstract void update();

    public void update(Grids_AbstractGridStats stats) {
        n = stats.n;
    }
        
    /**
     * @return {@link #grid} cast accordingly.
     */
    public abstract Grids_AbstractGrid getGrid();

    /**
     *
     * @param g What {@link #grid} is set to.
     */
    public final void setGrid(Grids_AbstractGrid g) {
        grid = g;
    }

    /**
     * Returns a String describing this instance
     *
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public String toString(
            boolean hoome) {
        try {
            String result = toString();
            env.checkAndMaybeFreeMemory();
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                if (!env.swapChunk(env.HOOMEF)) {
                    throw e;
                }
                env.initMemoryReserve();
                return toString(hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Override to provide a more detailed fields description.
     *
     * @return
     */
    public String getFieldsDescription() {
        return "N=" + n;
    }

    /**
     * Returns a string describing this instance.
     *
     * @return
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getFieldsDescription() + "]";
    }

}
