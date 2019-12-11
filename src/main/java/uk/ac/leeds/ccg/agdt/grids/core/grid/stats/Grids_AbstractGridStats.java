/*
 * Copyright 2019 Andy Turner, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.agdt.grids.core.grid.stats;

import uk.ac.leeds.ccg.agdt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Object;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_AbstractGrid;

/**
 *
 *
 * @author Andy Turner
 * @version 1.0.0
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
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public String toString(boolean hoome) {
        try {
            String r = toString();
            env.checkAndMaybeFreeMemory();
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                if (!env.cacheChunk(env.HOOMEF)) {
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
