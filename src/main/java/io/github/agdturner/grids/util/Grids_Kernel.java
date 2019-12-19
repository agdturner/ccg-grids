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
package io.github.agdturner.grids.util;

import java.awt.geom.Point2D;
import io.github.agdturner.grids.d2.grid.Grids_GridNumber;
import java.math.BigDecimal;
import java.math.RoundingMode;
import uk.ac.leeds.ccg.agdt.math.Math_BigDecimal;

/**
 * Class of methods to do with kernels.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_Kernel {

    /**
     * @param value
     * @param mean
     * @param variance
     * @return double expected value of value for a normal distribution with
     * mean and variance.
     */
    public static double getNormalDistributionKernelWeight(double value,
            double mean, double variance) {
        return (1.0d / (variance * Math.sqrt(2.0d * Math.PI)))
                * Math.pow(Math.E, (-1.0d * (((value - mean) * (value - mean))
                        / (2.0d * variance * variance))));
    }

    /**
     * @param cellsize
     * @param distance
     * @return double[] of kernel weights based on the normal distribution. The
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
                    numberOfCentroids++;
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
                    numberOfCentroids++;
                    r[i + delta][j + delta] = getNormalDistributionKernelWeight(
                            Math.sqrt((double) distance2), mean,
                            variance);
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
     * @param distance Bandwidth of the kernel.
     * @param weightIntersect Weight at the centre of the kernel.
     * @param weightFactor Warning: If less than 1 then strange things could
     * happen!!!!
     * @param thisDistance The distance from the centre of the kernel that the
     * weight result is returned.
     */
    public static BigDecimal getKernelWeight(BigDecimal distance,
            BigDecimal weightIntersect, int weightFactor,
            BigDecimal thisDistance, int dp, RoundingMode rm) {
        // The following weight is just one example of a kernel that can be used!
        // It provides a general monotonic curve based on distance over bandwidth.
        /*
        double weight;
        if ( weightFactor > 0.0d ) {
            weight = ( Math.pow( 1.0d - ( Math.pow( thisDistance , 2.0d ) 
        / Math.pow( distance, 2.0d ) ), weightFactor ) * weightIntersect );
            //weight = Math.pow( 1.0d - ( Math.pow( thisDistance, distanceWeightFactor ) 
        / Math.pow( distance, distanceWeightFactor ) ), distanceWeightFactor );
        } else {
            if ( weightFactor < 0.0d ) {
                weight = ( ( 1.0d - Math.pow( 1.0d - ( Math.pow( thisDistance , 2.0d ) 
        / Math.pow( distance, 2.0d ) ), Math.abs( weightFactor ) ) * ( 1.0d - weightIntersect ) ) + weightIntersect );
                //weight = 1.0d - Math.pow( 1.0d - ( Math.pow( thisDistance, Math.abs( distanceWeightFactor ) ) / Math.pow( distance, Math.abs( distanceWeightFactor ) ) ), Math.abs( distanceWeightFactor ) );
            } else {
                weight = weightIntersect;
            }
        }
        return weight;
         */
        //if ( distance == thisDistance ) { return 0.0d; }

        return BigDecimal.ONE.subtract((Math_BigDecimal.divideRoundIfNecessary(thisDistance.pow(2), distance.pow(2), dp, rm))).pow(weightFactor).multiply(weightIntersect);
        //return Math.pow(1.0d - (Math.pow(thisDistance, 2.0d) / Math.pow(distance, 2.0d)), weightFactor) * weightIntersect;
        //return Math.pow( 1.0d - ( Math.pow( thisDistance, distanceWeightFactor ) / Math.pow( distance, distanceWeightFactor ) ), distanceWeightFactor );
    }

    /**
     * Returns a double[] of kernel weights.
     *
     * @param g
     * @param distance
     * @param weightIntersect
     * @param weightFactor
     * @return
     */
    public static BigDecimal[][] getKernelWeights(Grids_GridNumber g,
            BigDecimal distance, BigDecimal weightIntersect, int weightFactor,
            int dp, RoundingMode rm) {
        BigDecimal cellsize = g.getCellsize();
        int delta = distance.divideToIntegralValue(cellsize).intValueExact();
        BigDecimal[][] weights = new BigDecimal[(delta * 2) + 1][(delta * 2) + 1];
        // The following weight is just one example of a kernel that can be used!
        // It provides a general monotonic curve based on distance over bandwidth.
        BigDecimal x0 = g.getCellXBigDecimal(0L);
        BigDecimal y0 = g.getCellYBigDecimal(0L);
        BigDecimal x1;
        BigDecimal y1;
        BigDecimal thisDistance;
        int row;
        int col;
        for (row = -delta; row <= delta; row++) {
            for (col = -delta; col <= delta; col++) {
                x1 = g.getCellXBigDecimal(col);
                y1 = g.getCellYBigDecimal(row);
                thisDistance = Grids_Utilities.distance(x0, y0, x1, y1, dp, rm);
                //if ( thisDistance <= distance ) {
                if (thisDistance.compareTo(distance) == -1) {
                    weights[row + delta][col + delta] = getKernelWeight(
                            distance, weightIntersect,
                            weightFactor, thisDistance, dp, rm);
                } else {
                    //weights[ i + cellDistance ][ j + cellDistance ] = noDataValue;
                    weights[row + delta][col + delta] = BigDecimal.ZERO;
                }
            }
        }
        return weights;
    }

    /**
     * Returns a double[] of kernel weights.
     *
     * @param g
     * @param rowIndex
     * @param colIndex
     * @param distance
     * @param weightIntersect
     * @param weightFactor
     * @param points
     * @return
     */
    public static BigDecimal[] getKernelWeights(Grids_GridNumber g, long rowIndex,
            long colIndex, BigDecimal distance, BigDecimal weightIntersect,
            int weightFactor, Point2D.Double[] points,
            int dp, RoundingMode rm) {
        BigDecimal[] weights = new BigDecimal[points.length];
        // The following weight is just one example of a kernel that can be used!
        // It provides a general monotonic curve based on distance over bandwidth.
        Point2D.Double centroid = new Point2D.Double(
                g.getCellXBigDecimal(colIndex).doubleValue(),
                g.getCellYBigDecimal(rowIndex).doubleValue());
        for (int i = 0; i < points.length; i++) {
            BigDecimal thisDistance = Grids_Utilities.distance(
                    g.getCellXBigDecimal(colIndex), g.getCellYBigDecimal(rowIndex),
                    BigDecimal.valueOf(points[i].x), BigDecimal.valueOf(points[i].y), dp, rm);
            if (thisDistance.compareTo(distance) == -1) {
                weights[i] = getKernelWeight(distance, weightIntersect,
                        weightFactor, thisDistance, dp, rm);
            }
        }
        return weights;
    }

    /**
     * Returns double[] result of kernel weights.
     *
     * @param centroid
     * @param distance
     * @param weightIntersect
     * @param weightFactor
     * @param points
     * @return
     */
    public static BigDecimal[] getKernelWeights(Point2D.Double centroid,
            BigDecimal distance, BigDecimal weightIntersect, int weightFactor,
            Point2D.Double[] points,
            int dp, RoundingMode rm) {
        BigDecimal[] weights = new BigDecimal[points.length];
        // The following weight is just one example of a kernel that can be used!
        // It provides a general monotonic curve based on distance over bandwidth.
        for (int i = 0; i < points.length; i++) {
            BigDecimal thisDistance = Grids_Utilities.distance(
                    BigDecimal.valueOf(centroid.x), BigDecimal.valueOf(centroid.y),
                    BigDecimal.valueOf(points[i].x), BigDecimal.valueOf(points[i].y), dp, rm);
            if (thisDistance.compareTo(distance) == -1) {
                weights[i] = getKernelWeight(distance, weightIntersect,
                        weightFactor, thisDistance, dp, rm);
            }
        }
        return weights;
    }

    /**
     * Returns double[] result of kernel parameters where: result[0] = The total
     * sum of all the weights for a given kernel; result[1] = The total number
     * of cells thats centroids are within distance of an arbitrary cell
     * centroid of grid2DSquareCell.
     *
     * @param g Grids_GridNumber for which kernel parameters are returned
     * @param cellDistance
     * @param distance
     * @param weightIntersect
     * @param weightFactor
     * @return
     */
    public static BigDecimal[] getKernelParameters(Grids_GridNumber g,
            int cellDistance, BigDecimal distance, BigDecimal weightIntersect,
            int weightFactor,
            int dp, RoundingMode rm) {
        BigDecimal kernelParameters[] = new BigDecimal[2];
        kernelParameters[0] = BigDecimal.ZERO;
        kernelParameters[1] = BigDecimal.ZERO;
        BigDecimal x0 = g.getCellXBigDecimal(0);
        BigDecimal y0 = g.getCellYBigDecimal(0);
        for (int p = -cellDistance; p <= cellDistance; p++) {
            for (int q = -cellDistance; q <= cellDistance; q++) {
                BigDecimal x1 = g.getCellXBigDecimal(q);
                BigDecimal y1 = g.getCellYBigDecimal(p);
                BigDecimal thisDistance = Grids_Utilities.distance(x0, y0, x1, y1, dp, rm);
                //if ( thisDistance <= distance ) {
                if (thisDistance.compareTo(distance) == -1) {
                    kernelParameters[0] = kernelParameters[0].add(getKernelWeight(distance,
                            weightIntersect, weightFactor, thisDistance, dp, rm));
                    kernelParameters[1] = kernelParameters[1].add(BigDecimal.ONE);
                }
            }
        }
        return kernelParameters;
    }

    /**
     * Returns a double representing an adaptive kernel weight.
     *
     * @param distance
     * @param bandwidth
     * @param sumWeights
     * @param precision
     * @param weightIntersect
     * @param weightFactor
     * @return
     */
    public static BigDecimal getAdaptiveKernelWeight(
            BigDecimal distance, BigDecimal bandwidth,
            BigDecimal sumWeights, int precision,
            BigDecimal weightIntersect, int weightFactor,
            int dp, RoundingMode rm) {
        //int sectionSize = ( int ) Math.ceil( bandwidth / ( double ) precision );
        //double sectionArea = Math.pow( Math.ceil( bandwidth / ( double ) precision ), 2.0d );
        BigDecimal kernelVolume = getKernelVolume(bandwidth, precision, weightIntersect, weightFactor, dp, rm);
        BigDecimal kernelWeight = getKernelWeight(bandwidth, weightIntersect, weightFactor, distance, dp, rm);
        return Math_BigDecimal.divideRoundIfNecessary(kernelWeight.multiply(sumWeights), kernelVolume, dp, rm);
    }

    /**
     * Returns a double representing the kernel volume.
     *
     * @param bandwidth
     * @param precision
     * @param weightIntersect
     * @param weightFactor
     * @return
     */
    public static BigDecimal getKernelVolume(
            BigDecimal bandwidth, int precision,
            BigDecimal weightIntersect, int weightFactor,
            int dp, RoundingMode rm) {
        BigDecimal sumKernelWeights = BigDecimal.ZERO;
        BigDecimal[] dar = bandwidth.divideAndRemainder(BigDecimal.valueOf(precision));
        BigDecimal sectionSize = dar[0];
        if (dar[1].compareTo(BigDecimal.ZERO) == 1) {
            sectionSize = sectionSize.add(BigDecimal.ONE);
        }
        BigDecimal sectionArea = sectionSize.multiply(sectionSize);
        //int sectionCount = 0;
        for (int p = 1; p < precision; p++) {
            for (int q = 0; q < precision; q++) {
                BigDecimal thisDistance = Grids_Utilities.distance(
                        BigDecimal.ZERO, BigDecimal.ZERO,
                        BigDecimal.valueOf(p).multiply(sectionSize),
                        BigDecimal.valueOf(q).multiply(sectionSize), dp, rm);
                if (thisDistance.compareTo(bandwidth) == -1) {
                    sumKernelWeights = sumKernelWeights.add(
                            getKernelWeight(bandwidth, weightIntersect, 
                                    weightFactor, thisDistance, dp, rm));
                    //sectionCount ++;
                }
            }
        }
        // Multiply by 4 for all quadrants
        // Add kernelCentroid weight
        // Multiply result be sectionArea to get volume
        return (sumKernelWeights.multiply(BigDecimal.valueOf(4)).add(weightIntersect)).multiply(sectionArea);
    }

}
