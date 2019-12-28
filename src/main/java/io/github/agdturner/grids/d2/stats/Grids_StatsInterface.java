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

import java.math.RoundingMode;

/**
 * An interface to be implemented by classes that provide statistics about
 * raster data.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public interface Grids_StatsInterface {

    /**
     * @return The number of cells with data values. (Or in some cases the
     * number of {@code true} values.)
     */
    public Number getN();

    /**
     * @return The sum of all data values.
     */
    public Number getSum();

    /**
     * @param update If true then an update of the statistics is made.
     * @return The minimum of all data values.
     */
    public Number getMin(boolean update);

    /**
     * @param update If true then an update of the statistics is made.
     * @return The maximum of all data values.
     */
    public Number getMax(boolean update);

    /**
     * @param dp The number of decimal places the result is rounded to if
     * necessary.
     * @param rm The Rounding mode for any necessary rounding.
     * @return The arithmetic mean of all data values.
     */
    public Number getArithmeticMean(int dp, RoundingMode rm);

//    @TODO
//    StandardDeviation
//    GeometricMean
//    HarmonicMean
//    Median
//    Diversity
}
