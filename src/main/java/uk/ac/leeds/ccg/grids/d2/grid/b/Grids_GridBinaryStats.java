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
package uk.ac.leeds.ccg.grids.d2.grid.b;

import java.io.IOException;
import java.util.Iterator;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_Stats;

/**
 * Used by Grids_GridBinary instances to access statistics. This class is to be
 * instantiated for Grids_GridBinary that keep statistic fields up to date as
 * the underlying data is changed. (Keeping statistic fields up to date as the
 * underlying data is changed can be expensive! Second order statistics like the
 * standard deviation would always require going through all the data again if
 * the values have changed.)
 *
 * @author Andy Turner
 * @version 1.0
 */
public class Grids_GridBinaryStats extends Grids_Stats {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance.
     * 
     * @param ge Grids_Environment
     */
    public Grids_GridBinaryStats(Grids_Environment ge) {
        super(ge);
    }

    /**
     * @return true iff the stats are kept up to date as the underlying data
     * change.
     */
    @Override
    public boolean isUpdated() {
        return true;
    }

    /**
     *
     * @return (Grids_GridBoolean) grid
     */
    @Override
    public Grids_GridBoolean getGrid() {
        return (Grids_GridBoolean) grid;
    }

    /**
     * Updates by going through all values in grid.
     *
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public void update() throws IOException, Exception, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        init();
        Grids_GridBoolean g = getGrid();
        Grids_GridBooleanIterator ite = g.iterator();
        while (ite.hasNext()) {
            if (ite.next()) {
                n ++;
            }
        }
    }

    /**
     * @return The total number of {@code true} and {@code false} values in the
     * grid.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public long getN() throws IOException, Exception, ClassNotFoundException {
        long r = 0;
        Grids_GridBoolean g = getGrid();
        Iterator<Grids_2D_ID_int> ite = g.iterator().getGridIterator();
        while (ite.hasNext()) {
            r += g.getChunk(ite.next()).getN();
            env.checkAndMaybeFreeMemory();
        }
        return r;
    }
}
