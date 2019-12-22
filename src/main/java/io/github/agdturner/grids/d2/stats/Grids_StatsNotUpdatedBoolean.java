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
package io.github.agdturner.grids.d2.stats;

import java.io.IOException;
import io.github.agdturner.grids.core.Grids_Environment;
import java.math.BigInteger;

/**
 * Used by Grids_AbstractGrid instances (grids) to access statistics. This class
 * is to be instantiated for grids that do not keep all
 * statistic fields up to date as the underlying data is changed. (Keeping
 * statistic fields up to date as the underlying data is changed can be
 * expensive, but also it can be expensive to calculate statistics often!)
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_StatsNotUpdatedBoolean extends Grids_StatsBoolean {

    private static final long serialVersionUID = 1L;

    /**
     * Is true iff fields are upToDate else is false.
     */
    protected boolean UpToDate;

    /**
     * Creates a new instance of Grids_GridBinaryStatsNotUpdated.
     *
     * @param ge
     */
    public Grids_StatsNotUpdatedBoolean(Grids_Environment ge) {
        super(ge);
    }

    /**
     * @return true iff the stats are kept up to date as the underlying data
     * change.
     */
    @Override
    public boolean isUpdated() {
        return false;
    }

    /**
     * Returns upToDate.
     *
     * @return
     */
    public boolean isUpToDate() {
        return UpToDate;
    }

    /**
     * Sets UpToDate to upToDate.
     *
     * @param upToDate
     */
    public void setUpToDate(
            boolean upToDate) {
        UpToDate = upToDate;
    }

    /**
     * Updates by going through all values in {@link #grid} if the fields are likely not
     * be up to date. (NB. After calling this it is inexpensive to convert to
     * Grids_GridIntStatistics.)
     */
    @Override
    public void update() throws IOException, Exception, ClassNotFoundException {
        if (!isUpToDate()) {
            super.update();
            setUpToDate(true);
        }
    }

    /**
     * For returning the number of cells with data values.
     *
     * @return
     */
    @Override
    public BigInteger getN() throws IOException, Exception, ClassNotFoundException {
        update();
        return n;
    }
}
