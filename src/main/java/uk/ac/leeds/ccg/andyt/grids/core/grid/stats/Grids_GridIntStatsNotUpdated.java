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

import java.math.BigDecimal;
import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;

/**
 * Used by Grids_AbstractGridNumber instances to access statistics. This class
 * is to be instantiated for Grids_AbstractGridNumber that do not keep all
 * statistic fields up to date as the underlying data is changed. (Keeping
 * statistic fields up to date as the underlying data is changed can be
 * expensive, but also it can be expensive to calculate statistics often!)
 */
public class Grids_GridIntStatsNotUpdated
        extends Grids_GridIntStats
        implements Serializable {

    /**
     * Is true iff fields are upToDate else is false.
     */
    protected boolean UpToDate;

    /**
     * Creates a new instance of Grids_GridIntStatisticsNotUpdated.
     *
     * @param ge
     */
    public Grids_GridIntStatsNotUpdated(Grids_Environment ge) {
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
     * Updates by going through all values in Grid if they might not be up to
     * date. (NB. After calling this it is inexpensive to convert to
     * Grids_GridIntStats.)
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

    /**
     * For returning the sum of all data values.
     *
     * @return
     */
    @Override
    public BigDecimal getSum() {
        update();
        return Sum;
    }

    /**
     * For returning the minimum of all data values.
     *
     * @param update If true then update() is called.
     * @return
     */
    @Override
    public Integer getMin(boolean update) {
        if (update) {
            update();
        }
        return Min;
    }

    /**
     * For returning the maximum of all data values.
     *
     * @param update If true then update() is called.
     * @return
     */
    @Override
    public Integer getMax(boolean update) {
        if (update) {
            update();
        }
        return Max;
    }

//    @Override
//    protected BigInteger getNonZeroN() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    protected BigDecimal getStandardDeviation() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
