/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package uk.ac.leeds.ccg.andyt.grids.core.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;

/**
 * Used by Grids_AbstractGridNumber instances to access statistics. This class
 * is to be instantiated for Grids_AbstractGridNumber that keep all statistic
 * fields up to date as the underlying data is changed. (Keeping statistic
 * fields up to date as the underlying data is changed can be expensive!)
 */
public class Grids_GridStatistics0
        extends Grids_AbstractGridStatistics
        implements Serializable {

    //private static final long serialVersionUID = 1L; 
    /**
     * Creates a new instance of GridStatistics0
     */
    protected Grids_GridStatistics0() {
    }

    public Grids_GridStatistics0(Grids_Environment ge) {
        super(ge);
    }

    /**
     * Creates a new instance of GridStatistics0
     *
     * @param g
     */
    public Grids_GridStatistics0(
            Grids_AbstractGridNumber g) {
        super(g.ge);
        init(g);
    }

    /**
     * Updates fields (statistics) by going through all values in
     * this.grid2DSquareCellAbstract if they might not be up to date. (NB. After
     * calling this it is inexpensive to convert to Grids_GridStatistics0.)
     */
    public @Override
    void update() {
        init();
        super.update(
                this.Grid.getNRows(this.ge.HandleOutOfMemoryError),
                this.Grid.getNCols(this.ge.HandleOutOfMemoryError));
    }

    /**
     * For returning the number of cells with noDataValues as a BigInteger
     *
     * @return
     */
    @Override
    protected BigInteger getNonNoDataValueCount() {
        return this.NonNoDataValueCount;
    }

    /**
     * For returning the sum of all non noDataValues as a BigDecimal
     *
     * @return
     */
    @Override
    protected BigDecimal getSum() {
        return this.Sum;
    }

    /**
     * For returning the minimum of all non noDataValues as a BigDecimal
     *
     * @return
     */
    @Override
    protected BigDecimal getMin(boolean update) {
        if (this.getMinCount().compareTo(BigInteger.ONE) == -1) {
            if (update) {
                update();
            }
        }
        return this.Min;
    }

    /**
     * For returning the maximum of all non noDataValues as a BigDecimal.
     *
     * @return
     */
    @Override
    protected BigDecimal getMax(boolean update) {
        if (this.getMaxCount().compareTo(BigInteger.ONE) == -1) {
            if (update) {
                update();
            }
        }
        return this.Max;
    }

    /**
     * For returning the arithmetic mean of all non noDataValues as a BigDecimal
     * Throws an ArithmeticException if NonNoDataValueCount is equal to zero.
     *
     * @return
     */
    protected @Override
    BigDecimal getArithmeticMeanBigDecimal(
            int numberOfDecimalPlaces) {
        return this.Sum.divide(new BigDecimal(this.NonNoDataValueCount),
                numberOfDecimalPlaces,
                BigDecimal.ROUND_HALF_EVEN);
    }

}
