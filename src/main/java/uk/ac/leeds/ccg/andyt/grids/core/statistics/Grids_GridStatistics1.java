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
package uk.ac.leeds.ccg.andyt.grids.core.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid2DSquareCell;

/**
 * Used by Grids_AbstractGrid2DSquareCell instances to access statistics. This
 * class to be instantiated for Grids_AbstractGrid2DSquareCell that do not keep
 * all statistic fields up to date as the underlying data is changed. (Keeping
 * statistic fields up to date as the underlying data is changed can be
 * expensive.)
 */
public class Grids_GridStatistics1
        extends Grids_AbstractGridStatistics
        implements Serializable {

    //private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of GridStatistics1
     */
    public Grids_GridStatistics1() {
    }

    public Grids_GridStatistics1(Grids_Environment ge) {
        super(ge);
    }
    
    /**
     * Creates a new instance of GridStatistics1
     *
     * @param g
     */
    public Grids_GridStatistics1(
            Grids_AbstractGrid2DSquareCell g) {
        super(g.ge);
        init(g);
    }

    /**
     * Is true iff fields are upToDate else is false.
     */
    protected boolean isUpToDate;

    /**
     * Returns upToDate
     *
     * @return
     */
    protected boolean getIsUpToDate() {
        return isUpToDate;
    }

    /**
     * Sets this.isUpToDate to isUpToDate
     *
     * @param isUpToDate
     */
    public void setIsUpToDate(
            boolean isUpToDate) {
        this.isUpToDate = isUpToDate;
    }

    /**
     * Updates fields (statistics) by going through all values in
     * this.grid2DSquareCellAbstract if they might not be up to date. (NB. After
     * calling this it is inexpensive to convert to GridStatistics0.)
     */
    public @Override
    void update() {
        if (!getIsUpToDate()) {
            init();
            super.update(
                    this.Grid2DSquareCell.get_NRows(this.ge._HandleOutOfMemoryError_boolean),
                    this.Grid2DSquareCell.get_NCols(this.ge._HandleOutOfMemoryError_boolean));
            setIsUpToDate(true);
        }
    }

    /**
     * For returning the number of cells with noDataValues as a BigInteger
     *
     * @return
     */
    protected @Override
    BigInteger getNonNoDataValueCountBigInteger() {
        update();
        return this.nonNoDataValueCountBigInteger;
    }

    /**
     * For returning the sum of all non noDataValues as a BigDecimal
     *
     * @return
     */
    protected @Override
    BigDecimal getSumBigDecimal() {
        update();
        return this.sumBigDecimal;
    }

    /**
     * For returning the minimum of all non noDataValues as a BigDecimal
     *
     * @return
     */
    protected @Override
    BigDecimal getMinBigDecimal() {
        update();
        return this.minBigDecimal;
    }

    /**
     * For returning the maximum of all non noDataValues as a BigDecimal
     *
     * @return
     */
    protected @Override
    BigDecimal getMaxBigDecimal() {
        update();
        return this.maxBigDecimal;
    }

    /**
     * For returning the arithmetic mean of all non noDataValues as a BigDecimal
     * Throws an ArithmeticException if nonNoDataValueCountBigInteger is equal
     * to zero.
     *
     * @return
     */
    protected @Override
    BigDecimal getArithmeticMeanBigDecimal(
            int numberOfDecimalPlaces) {
        update();
        return this.sumBigDecimal.divide(
                new BigDecimal(this.nonNoDataValueCountBigInteger),
                numberOfDecimalPlaces,
                BigDecimal.ROUND_HALF_EVEN);
    }

}
