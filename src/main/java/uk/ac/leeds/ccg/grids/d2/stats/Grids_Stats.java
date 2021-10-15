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
package uk.ac.leeds.ccg.grids.d2.stats;

import java.io.IOException;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.core.Grids_Object;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;

/**
 * Grids stats.
 *
 * @author Andy Turner
 * @version 1.0
 */
public abstract class Grids_Stats extends Grids_Object {

    private static final long serialVersionUID = 1L;

    /**
     * A reference to the Grid.
     */
    public transient Grids_Grid grid;

    /**
     * For storing the number of cells with values.
     */
    protected long n;

    /**
     * Create a new instance.
     * @param ge The Grids_Environment.
     */
    public Grids_Stats(Grids_Environment ge) {
        super(ge);
        init0();
    }

    /**
     * Initialises the statistics by setting n equal to 0.
     */
    private void init0() {
        n = 0;
    }

    /**
     * Initialises the statistics by setting n equal to 0.
     */
    protected void init() {
        init0();
    }

    /**
     * For returning the number of values.
     *
     * @return The number of values.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public abstract long getN() throws IOException, Exception,
            ClassNotFoundException;

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
     *
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected abstract void update() throws IOException, Exception,
            ClassNotFoundException;

    /**
     * Update
     * @param stats The statistics.
     */
    public void update(Grids_Stats stats) {
        n = stats.n;
    }

    /**
     * @return {@link #grid} cast accordingly.
     */
    public abstract Grids_Grid getGrid();

    /**
     *
     * @param g What {@link #grid} is set to.
     */
    public final void setGrid(Grids_Grid g) {
        grid = g;
    }

    /**
     * @return A text description of this.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public String getFieldsDescription() throws IOException, Exception,
            ClassNotFoundException {
        return "N=" + n;
    }

    /**
     * @return A text description of this.
     */
    @Override
    public String toString() {
        try {
            return getClass().getSimpleName() + "[" + getFieldsDescription() + "]";
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            env.env.log(ex.getMessage());
        }
        return null;
    }

}
