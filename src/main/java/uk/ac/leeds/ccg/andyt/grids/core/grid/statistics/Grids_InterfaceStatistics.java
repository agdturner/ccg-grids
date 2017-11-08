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
package uk.ac.leeds.ccg.andyt.grids.core.grid.statistics;

/**
 * An interface to be implemented by classes that provide statistics about
 * raster data.
 */
public interface Grids_InterfaceStatistics {

    /**
     * For returning the number of cells with noDataValues.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Number getN(boolean handleOutOfMemoryError);

    /**
     * For returning the sum of all data values.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Number getSum(boolean handleOutOfMemoryError);

    /**
     * For returning the minimum of all data values.
     *
     * @param update If true then an update of the statistics is made.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Number getMin(boolean update, boolean handleOutOfMemoryError);

    /**
     * For returning the maximum of all data values.
     *
     * @param update If true then an update of the statistics is made.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Number getMax(boolean update, boolean handleOutOfMemoryError);

    /**
     * For returning the arithmetic mean of all data values.
     *
     * @param numberOfDecimalPlaces The result returned uses BigDecimal 
     * arithmetic to ensure the result is correct given a round scheme to this
     * many decimal places.  
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Number getArithmeticMean(int numberOfDecimalPlaces, boolean handleOutOfMemoryError);

//    @TODO
//    StandardDeviation
//    GeometricMean
//    HarmonicMean
//    Median
//    Diversity
}
