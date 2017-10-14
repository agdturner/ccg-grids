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
import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * An interface to be implemented by classes that provide statistics about 
 * raster data.
 */
public interface Grids_GridStatisticsInterface {
    
    /**
     * For returning the number of cells with noDataValues as a BigInteger
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public BigInteger getNonNoDataValueCountBigInteger(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the number of cells with noDataValues as a long
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public long getNonNoDataValueCountLong(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the number of cells with noDataValues as a int
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public int getNonNoDataValueCountInt(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the sum of all non noDataValues as a BigDecimal
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public BigDecimal getSumBigDecimal(
            boolean handleOutOfMemoryError );

    /**
     * For returning the sum of all non noDataValues as a BigInteger
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public BigInteger getSumBigInteger(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the sum of all non noDataValues as a double
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public double getSumDouble(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the sum of all non noDataValues as a long
     * @param handleOutOfMemoryError
     * @return 
     */
    public long getSumLong(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the sum of all non noDataValues as a int
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public int getSumInt(
            boolean handleOutOfMemoryError );
    
    
    /**
     * For returning the minimum of all non noDataValues as a BigDecimal
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public BigDecimal getMinBigDecimal(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the minimum of all non noDataValues as a BigInteger
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public BigInteger getMinBigInteger(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the minimum of all non noDataValues as a double
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public double getMinDouble(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the minimum of all non noDataValues as a long
     * @param handleOutOfMemoryError
     * @return 
     */
    public long getMinLong(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the minimum of all non noDataValues as a int
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public int getMinInt(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the maximum of all non noDataValues as a BigDecimal
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public BigDecimal getMaxBigDecimal(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the maximum of all non noDataValues as a BigInteger
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public BigInteger getMaxBigInteger(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the minimum of all non noDataValues as a double
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public double getMaxDouble(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the minimum of all non noDataValues as a long
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public long getMaxLong(
            boolean handleOutOfMemoryError );
    
    /**
     * For returning the minimum of all non noDataValues as a int
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public int getMaxInt(
            boolean handleOutOfMemoryError );
        
    /**
     * For returning the arithmetic mean of all non noDataValues as a BigDecimal
     * @param numberOfDecimalPlaces The number of decimal places to which 
     *   the result is precise.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are 
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public BigDecimal getArithmeticMeanBigDecimal( 
            int numberOfDecimalPlaces,
            boolean handleOutOfMemoryError );
    
//    /**
//     * For returning the arithmetic mean of all non noDataValues as a double
//     * @param handleOutOfMemoryError
//     *   If true then OutOfMemoryErrors are caught, swap operations are 
//     *     initiated, then the method is re-called.
//     *   If false then OutOfMemoryErrors are caught and thrown.
//     */
//    public double getArithmeticMeanDouble(
//            boolean handleOutOfMemoryError );
//    
//    /**
//     * For returning the standard deviation of all non noDataValues as a double
//     * @param handleOutOfMemoryError
//     *   If true then OutOfMemoryErrors are caught, swap operations are 
//     *     initiated, then the method is re-called.
//     *   If false then OutOfMemoryErrors are caught and thrown.
//     */
//    public double getStandardDeviationDouble(
//            boolean handleOutOfMemoryError );
//
//    /**
//     * For returning the geometric mean of all non noDataValues as a double
//     * @param handleOutOfMemoryError
//     *   If true then OutOfMemoryErrors are caught, swap operations are 
//     *     initiated, then the method is re-called.
//     *   If false then OutOfMemoryErrors are caught and thrown.
//     */
//    public double getGeometricMeanDouble(
//            boolean handleOutOfMemoryError );
//
//    /**
//     * For returning the arithmetic mean of all non noDataValues as a double
//     * @param handleOutOfMemoryError
//     *   If true then OutOfMemoryErrors are caught, swap operations are 
//     *     initiated, then the method is re-called.
//     *   If false then OutOfMemoryErrors are caught and thrown.
//     */
//    public double getHarmonicMeanDouble(
//            boolean handleOutOfMemoryError );
//
//    /**
//     * For returning the median of all non noDataValues as a double
//     * @param handleOutOfMemoryError
//     *   If true then OutOfMemoryErrors are caught, swap operations are 
//     *     initiated, then the method is re-called.
//     *   If false then OutOfMemoryErrors are caught and thrown.
//     */
//    public double getMedianDouble(
//            boolean handleOutOfMemoryError );
//   
//    /**
//     * For returning the number of different values.
//     * @param handleOutOfMemoryError
//     *   If true then OutOfMemoryErrors are caught, swap operations are 
//     *     initiated, then the method is re-called.
//     *   If false then OutOfMemoryErrors are caught and thrown.
//     */
//    public BigInteger getDiversity(
//            boolean handleOutOfMemoryError );
    
}
