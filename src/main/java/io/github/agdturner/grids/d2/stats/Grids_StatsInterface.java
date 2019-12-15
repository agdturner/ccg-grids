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

/**
 * An interface to be implemented by classes that provide statistics about
 * raster data.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public interface Grids_StatsInterface {

    /**
     * For returning the number of cells with data values.
     * @return
     */
    public Number getN();

    /**
     * For returning the sum of all data values.
     * @return
     */
    public Number getSum();

    /**
     * For returning the minimum of all data values.
     *
     * @param update If true then an update of the statistics is made.
     * @return
     */
    public Number getMin(boolean update);

    /**
     * For returning the maximum of all data values.
     *
     * @param update If true then an update of the statistics is made.
     * @return
     */
    public Number getMax(boolean update);

    /**
     * For returning the arithmetic mean of all data values.
     *
     * @param numberOfDecimalPlaces The result returned uses BigDecimal 
     * arithmetic to ensure the result is correct given a round scheme to this
     * many decimal places.
     * @return
     */
    public Number getArithmeticMean(int numberOfDecimalPlaces);

//    @TODO
//    StandardDeviation
//    GeometricMean
//    HarmonicMean
//    Median
//    Diversity
}