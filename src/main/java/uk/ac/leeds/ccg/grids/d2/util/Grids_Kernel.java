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
package uk.ac.leeds.ccg.grids.d2.util;

import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridNumber;
import java.math.BigDecimal;
import java.math.RoundingMode;
import uk.ac.leeds.ccg.math.arithmetic.Math_BigDecimal;
import uk.ac.leeds.ccg.grids.d2.Grids_Point;

/**
 * For kernels.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_Kernel {

    /**
     * @param value The value.
     * @param mean The mean.
     * @param variance The variance.
     * @param PI Pi
     * @param E euler-macheroni constant
     * @param oom Order Of Magnitude for any rounding.
     * @param rm RoundingMode for BigDecimal arithmetic.
     * @return Expected value of value for a normal distribution with mean and
     * variance.
     */
    public static BigDecimal getNormalDistributionKernelWeight(BigDecimal value,
            BigDecimal mean, BigDecimal variance, BigDecimal PI, BigDecimal E,
            int oom, RoundingMode rm) {
        BigDecimal TWO = BigDecimal.valueOf(2);
        return Math_BigDecimal.divide(BigDecimal.ONE,
                variance.multiply(Math_BigDecimal.sqrt(TWO.multiply(PI), oom, rm)), oom, rm)
                .multiply(Math_BigDecimal.power(E, (BigDecimal.ONE.negate().multiply(
                        Math_BigDecimal.divide(value.subtract(mean).pow(2),
                                 TWO.multiply(variance.pow(2)), oom, rm))), oom, rm));
    }

    /**
     * @param value The value.
     * @param mean The mean.
     * @param variance The variance.
     * @return Expected value of value for a normal distribution with mean and
     * variance.
     */
    public static double getNormalDistributionKernelWeight(double value,
            double mean, double variance) {
        return (1.0d / (variance * Math.sqrt(2.0d * Math.PI)))
                * Math.pow(Math.E, (-1.0d * (((value - mean) * (value - mean))
                        / (2.0d * variance * variance))));
    }

    /**
     * @param cellsize The cellsize.
     * @param distance The distance.
     * @return Kernel weights based on the normal distribution. The
     * mean and variance of the normal distribution is given by the distances to
     * the centroids of the cells that are within distance.
     */
    public static double[][] getNormalDistributionKernelWeights(double cellsize,
            double distance) {
        double[][] r;
        int delta = (int) Math.ceil(distance / cellsize);
        int squareSize = (delta * 2) + 1;
        r = new double[squareSize][squareSize];
        int distance2;
        int i;
        int j;
        double meanSquared = 0.0d;
        double mean;
        double varianceSquared = 0.0d;
        double variance;
        double numberOfCentroids = 0.0d;
        int delta2 = delta * delta;

        // Calculate mean distance
        for (i = -delta; i <= delta; i++) {
            for (j = -delta; j <= delta; j++) {
                distance2 = (i * i) + (j * j);
                if (distance2 <= delta2) {
                    numberOfCentroids++;
                    meanSquared += distance2;
                }
            }
        }
        meanSquared /= numberOfCentroids;
        mean = Math.sqrt(meanSquared);

        // Calculate the variance of distance
        for (i = -delta; i <= delta; i++) {
            for (j = -delta; j <= delta; j++) {
                distance2 = (i * i) + (j * j);
                if (distance2 <= delta2) {
                    varianceSquared += (distance2 - meanSquared)
                            * (distance2 - meanSquared);
                }
            }
        }
        variance = Math.sqrt(varianceSquared);
        variance /= (numberOfCentroids - 1.0d);

        // Calculate the weights (expected values)
        for (i = -delta; i <= delta; i++) {
            for (j = -delta; j <= delta; j++) {
                distance2 = (i * i) + (j * j);
                if (distance2 <= delta2) {
                    r[i + delta][j + delta] = getNormalDistributionKernelWeight(
                            Math.sqrt((double) distance2), mean, variance);
                } else {
                    //weights[ i + delta ][ j + delta ] = noDataValue;
                    r[i + delta][j + delta] = 0.0d;
                }
            }
        }
        return r;
    }

    /**
     * @return A value for the height of a kernel at thisDistance from the
     * centre of a kernel with; bandwidth distance, weight at the centre of
     * weightIntersect and distance decay of weightFactor.
     *
     * @param d Bandwidth of the kernel.
     * @param wi Weight at the centre of the kernel.
     * @param wf Warning: If less than 1 then strange things could happen!!!!
     * @param td The distance from the centre of the kernel that the weight
     * result is returned.
     * @param oom Order Of Magnitude for any rounding.
     * @param rm RoundingMode for BigDecimal arithmetic.
     */
    public static BigDecimal getKernelWeight(BigDecimal d, BigDecimal wi,
            int wf, BigDecimal td, int oom, RoundingMode rm) {
        return BigDecimal.ONE.subtract((Math_BigDecimal.divide(
                td.pow(2), d.pow(2), oom, rm))).pow(wf).multiply(wi);
    }

    /**
     * Get kernel weights.
     *
     * @param g The grid.
     * @param d The distance.
     * @param wi The weight intersect.
     * @param wf The weight factor.
     * @param oom Order Of Magnitude for any rounding.
     * @param rm RoundingMode for BigDecimal arithmetic.
     * @return Kernel weights.
     */
    public static BigDecimal[][] getKernelWeights(Grids_GridNumber g,
            BigDecimal d, BigDecimal wi, int wf, int oom, RoundingMode rm) {
        BigDecimal cellsize = g.getCellsize();
        int delta = d.divideToIntegralValue(cellsize).intValueExact();
        BigDecimal[][] weights = new BigDecimal[(delta * 2) + 1][(delta * 2) + 1];
        /**
         * The following weight is just one example of a kernel that can be
         * used! It provides a general monotonic curve based on distance over
         * bandwidth.
         */
        BigDecimal x0 = g.getCellX(0L);
        BigDecimal y0 = g.getCellY(0L);
        BigDecimal x1;
        BigDecimal y1;
        BigDecimal thisDistance;
        int row;
        int col;
        for (row = -delta; row <= delta; row++) {
            for (col = -delta; col <= delta; col++) {
                x1 = g.getCellX(col);
                y1 = g.getCellY(row);
                thisDistance = Grids_Utilities.distance(x0, y0, x1, y1, oom, rm);
                //if ( thisDistance <= distance ) {
                if (thisDistance.compareTo(d) == -1) {
                    weights[row + delta][col + delta] = getKernelWeight(
                            d, wi,
                            wf, thisDistance, oom, rm);
                } else {
                    //weights[ i + cellDistance ][ j + cellDistance ] = noDataValue;
                    weights[row + delta][col + delta] = BigDecimal.ZERO;
                }
            }
        }
        return weights;
    }

    /**
     * Get kernel weights.
     *
     * @param g The grid.
     * @param row The cell row.
     * @param col The cell column.
     * @param d The distance.
     * @param wi The weight intersect.
     * @param wf The weight factor.
     * @param points The points.
     * @param oom Order Of Magnitude for any rounding.
     * @param rm RoundingMode for BigDecimal arithmetic.
     * @return Kernel weights.
     */
    public static BigDecimal[] getKernelWeights(Grids_GridNumber g, long row,
            long col, BigDecimal d, BigDecimal wi, int wf, Grids_Point[] points,
            int oom, RoundingMode rm) {
        BigDecimal[] weights = new BigDecimal[points.length];
        /**
         * The following weight is just one example of a kernel that can be
         * used! It provides a general monotonic curve based on distance over
         * bandwidth.
         */
        BigDecimal x = g.getCellX(col);
        BigDecimal y = g.getCellY(row);
        for (int i = 0; i < points.length; i++) {
            BigDecimal td = Grids_Utilities.distance(x, y, points[i].x,
                    points[i].y, oom, rm);
            if (td.compareTo(d) == -1) {
                weights[i] = getKernelWeight(d, wi, wf, td, oom, rm);
            }
        }
        return weights;
    }

    /**
     * Get kernel weights.
     *
     * @param centroid The centroid.
     * @param d The distance.
     * @param wi The weight intersect.
     * @param wf The weight factor.
     * @param points The points.
     * @param oom Order Of Magnitude for any rounding.
     * @param rm RoundingMode for BigDecimal arithmetic.
     * @return Kernel weights.
     */
    public static BigDecimal[] getKernelWeights(Grids_Point centroid,
            BigDecimal d, BigDecimal wi, int wf, Grids_Point[] points, int oom,
            RoundingMode rm) {
        BigDecimal[] weights = new BigDecimal[points.length];
        /**
         * The following weight is just one example of a kernel that can be
         * used! It provides a general monotonic curve based on distance over
         * bandwidth.
         */
        for (int i = 0; i < points.length; i++) {
            BigDecimal td = Grids_Utilities.distance(centroid.x, centroid.y,
                    points[i].x, points[i].y, oom, rm);
            if (td.compareTo(d) == -1) {
                weights[i] = getKernelWeight(d, wi, wf, td, oom, rm);
            }
        }
        return weights;
    }

    /**
     * Get kernel parameters:
     * <ul>
     * <li>[0] = The total sum of all the weights for a given kernel</li>
     * <li>[1] = The total number of cells that's centroids are within distance
     * of an arbitrary cell centroid of grid.</li>
     * </ul>
     *
     * @param g The grid.
     * @param cd The cell distance.
     * @param d The distance.
     * @param wi The weight intersect.
     * @param wf The weight factor.
     * @param oom Order Of Magnitude for any rounding.
     * @param rm RoundingMode for BigDecimal arithmetic.
     * @return Kernel parameters.
     */
    public static BigDecimal[] getKernelParameters(Grids_GridNumber g, int cd,
            BigDecimal d, BigDecimal wi, int wf, int oom, RoundingMode rm) {
        BigDecimal r[] = new BigDecimal[2];
        r[0] = BigDecimal.ZERO;
        r[1] = BigDecimal.ZERO;
        BigDecimal x0 = g.getCellX(0);
        BigDecimal y0 = g.getCellY(0);
        for (int p = -cd; p <= cd; p++) {
            for (int q = -cd; q <= cd; q++) {
                BigDecimal x1 = g.getCellX(q);
                BigDecimal y1 = g.getCellY(p);
                BigDecimal td = Grids_Utilities.distance(x0, y0, x1, y1, oom, rm);
                if (td.compareTo(d) == -1) {
                    r[0] = r[0].add(getKernelWeight(d, wi, wf, td, oom, rm));
                    r[1] = r[1].add(BigDecimal.ONE);
                }
            }
        }
        return r;
    }

    /**
     * Get adaptive kernel weight.
     *
     * @param d The distance.
     * @param bw The bandwidth.
     * @param sw The sum weights.
     * @param p The precision.
     * @param wi The weight intersect.
     * @param wf The weight factor.
     * @param oom Order Of Magnitude for any rounding.
     * @param rm RoundingMode for BigDecimal arithmetic.
     * @return Adaptive kernel weight.
     */
    public static BigDecimal getAdaptiveKernelWeight(BigDecimal d,
            BigDecimal bw, BigDecimal sw, int p, BigDecimal wi, int wf,
            int oom, RoundingMode rm) {
        BigDecimal v = getKernelVolume(bw, p, wi, wf, oom, rm);
        BigDecimal w = getKernelWeight(bw, wi, wf, d, oom, rm);
        return Math_BigDecimal.divide(w.multiply(sw), v, oom, rm);
    }

    /**
     * Get the kernel volume.
     *
     * @param bw The bandwidth.
     * @param p The precision.
     * @param wi The weight intersect.
     * @param wf The weight factor.
     * @param oom Order Of Magnitude for any rounding.
     * @param rm RoundingMode for BigDecimal arithmetic.
     * @return The kernel volume.
     */
    public static BigDecimal getKernelVolume(BigDecimal bw, int p,
            BigDecimal wi, int wf, int oom, RoundingMode rm) {
        BigDecimal r = BigDecimal.ZERO;
        BigDecimal[] dar = bw.divideAndRemainder(BigDecimal.valueOf(p));
        BigDecimal sectionSize = dar[0];
        if (dar[1].compareTo(BigDecimal.ZERO) == 1) {
            sectionSize = sectionSize.add(BigDecimal.ONE);
        }
        BigDecimal sectionArea = sectionSize.multiply(sectionSize);
        //int sectionCount = 0;
        for (int row = 1; row < p; row++) {
            for (int col = 0; col < p; col++) {
                BigDecimal td = Grids_Utilities.distance(BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.valueOf(row)
                                .multiply(sectionSize),
                        BigDecimal.valueOf(col).multiply(sectionSize), oom, rm);
                if (td.compareTo(bw) == -1) {
                    r = r.add(
                            getKernelWeight(bw, wi, wf, td, oom, rm));
                    //sectionCount ++;
                }
            }
        }
        // Multiply by 4 for all quadrants
        // Add kernelCentroid weight
        // Multiply result be sectionArea to get volume
        return (r.multiply(BigDecimal.valueOf(4)).add(wi)).multiply(sectionArea);
    }

}
