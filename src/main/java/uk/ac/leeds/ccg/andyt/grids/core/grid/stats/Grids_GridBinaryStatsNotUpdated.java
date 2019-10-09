/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.stats;

import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;

/**
 * Used by Grids_AbstractGrid instances (grids) to access statistics. This class
 * is to be instantiated for grids that do not keep all
 * statistic fields up to date as the underlying data is changed. (Keeping
 * statistic fields up to date as the underlying data is changed can be
 * expensive, but also it can be expensive to calculate statistics often!)
 */
public class Grids_GridBinaryStatsNotUpdated
        extends Grids_GridBinaryStats {

    /**
     * Is true iff fields are upToDate else is false.
     */
    protected boolean UpToDate;

    /**
     * Creates a new instance of Grids_GridBinaryStatsNotUpdated.
     *
     * @param ge
     */
    public Grids_GridBinaryStatsNotUpdated(Grids_Environment ge) {
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
    public void update() {
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
    public long getN() {
        update();
        return n;
    }
}
