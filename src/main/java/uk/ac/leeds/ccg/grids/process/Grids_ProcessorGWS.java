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
package uk.ac.leeds.ccg.grids.process;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridNumber;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.util.Grids_Kernel;
import uk.ac.leeds.ccg.grids.d2.util.Grids_Utilities;
import java.math.RoundingMode;
import uk.ac.leeds.ccg.io.IO_Path;
import uk.ac.leeds.ccg.math.number.Math_BigRational;
import uk.ac.leeds.ccg.math.number.Math_BigRationalSqrt;

/**
 * Class of methods for processing and generating geographically weighted
 * Grids_GridDouble statistics.
 *
 * @author Andy Turner
 * @version 1.1
 */
public class Grids_ProcessorGWS extends Grids_Processor {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance.
     * @param e Grids_Environment
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_ProcessorGWS(Grids_Environment e) throws IOException,
            ClassNotFoundException, Exception {
        super(e);
    }

    /**
     * For getting region uni-variate statistics.
     *
     * @param grid The grid to be processed
     * @param statistics A list of the statistics to generate.
     * @param distance The distance defining the region within which values will
     * be used.
     * @param weightIntersect Typically a number between 0 and 1 which controls
     * the weight applied at the centre of the kernel
     * @param weightFactor
     * <ul>
     * <li>{@code 0.0d} all values within distance will be equally weighted</li>
     * <li>{@code > 0.0d} means the edge of the kernel has a zero weight</li>
     * <li>{@code < 0.0d} means that the edge of the kernel has a weight of
     * 1</li>
     * <li>{@code > -1.0d && < 1.0d} provides an inverse decay</li>
     * </ul>
     * @param gf The grid facory for creating result grids.
     * @param oom Order Of Magnitude for any rounding.
     * @param rm The Rounding Mode
     * @return A set of grid based region univariate statistics.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public List<Grids_GridNumber> regionUnivariateStatistics(
            Grids_GridDouble grid, List<String> statistics, 
            Math_BigRationalSqrt distance,
            Math_BigRational weightIntersect, int weightFactor,
            Grids_GridDoubleFactory gf, int oom, RoundingMode rm) throws
            IOException, ClassNotFoundException, Exception {
        List<Grids_GridNumber> r = new ArrayList<>();
        long ncols = grid.getNCols();
        long nrows = grid.getNRows();
        Grids_Dimensions dimensions = grid.getDimensions();
        BigDecimal ndv = grid.ndv;
        double ndvd = grid.getNoDataValue();
        int cellDistance = grid.getCellDistance(distance);
        // @HACK If cellDistance is so great that data for a single kernel is
        // unlikely to fit in memory
        if (cellDistance > 1024) {
            return regionUnivariateStatisticsSlow(
                    grid,
                    statistics,
                    distance,
                    weightIntersect,
                    weightFactor,
                    gf, oom, rm);
        }

        boolean doSum = false;
        boolean doWSum = false;
        boolean doNWSum = false;
        boolean doWSumN = false;

        boolean doMean = false;
        boolean doWMean1 = false;
        boolean doWMean2 = false;
        boolean doNWMean = false;
        boolean doWMeanN = false;

        boolean doProp = false;
        boolean doWProp = false;
        boolean doVar = false;
        boolean doWVar = false;
        boolean doSkew = false;
        boolean doWSkew = false;
        boolean doCVar = false;
        boolean doWCVar = false;
        boolean doCSkew = false;
        boolean doWCSkew = false;

        //boolean doZscore = false;
        //boolean doWZscore = false;
        for (int i = 0; i < statistics.size(); i++) {

            //if ( ( ( String ) statistics.elementAt( i ) ).equalsIgnoreCase( "FirstOrder" ) ) { doMean = true; doWMean = true; doNWMean = true; doWMeanN = true; doSum = true; doNWSum = true; doWSumN = true; }
            //if ( ( ( String ) statistics.elementAt( i ) ).equalsIgnoreCase( "WeightedFirstOrder" ) ) { doWMean = true; doNWMean = true; doWMeanN = true; doNWSum = true; doWSumN = true; }
            if (statistics.get(i).equalsIgnoreCase("Sum")) {
                doSum = true;
            }
            if (statistics.get(i).equalsIgnoreCase("WSum")) {
                doWSum = true;
            }
            if (statistics.get(i).equalsIgnoreCase("NWSum")) {
                doNWSum = true;
            }
            if (statistics.get(i).equalsIgnoreCase("WSumN")) {
                doWSumN = true;
            }

            if (statistics.get(i).equalsIgnoreCase("Mean")) {
                doMean = true;
            }
            if (statistics.get(i).equalsIgnoreCase("WMean1")) {
                doWMean1 = true;
            }
            if (statistics.get(i).equalsIgnoreCase("WMean2")) {
                doWMean2 = true;
            }
            if (statistics.get(i).equalsIgnoreCase("NWMean")) {
                doNWMean = true;
            }
            if (statistics.get(i).equalsIgnoreCase("WMeanN")) {
                doWMeanN = true;
            }

            if (statistics.get(i).equalsIgnoreCase("SecondOrder")) {
                doMean = true;
                doNWMean = true;
                doProp = true;
                doWProp = true;
                doVar = true;
                doWVar = true;
                doSkew = true;
                doWSkew = true;
                doCVar = true;
                doWCVar = true;
                doCSkew = true;
                doWCSkew = true;
            }
            if (statistics.get(i).equalsIgnoreCase("WeightedSecondOrder")) {
                doNWMean = true;
                doWProp = true;
                doWVar = true;
                doWSkew = true;
                doWCVar = true;
                doWCSkew = true;
            }
            if (statistics.get(i).equalsIgnoreCase("Prop")) {
                doProp = true;
                doMean = true;
            }
            if (statistics.get(i).equalsIgnoreCase("WProp")) {
                doWProp = true;
                doMean = true;
            }
            if (statistics.get(i).equalsIgnoreCase("Var")) {
                doVar = true;
                doMean = true;
            }
            //      if ( ( ( String ) statistics.elementAt( i ) ).equalsIgnoreCase( "WVar" ) ) { doWVar = true; doWMean = true;  }
            if (statistics.get(i).equalsIgnoreCase("Skew")) {
                doSkew = true;
                doMean = true;
            }
            //    if ( ( ( String ) statistics.elementAt( i ) ).equalsIgnoreCase( "WSkew" ) ) { doWSkew = true; doWMean = true;  }
            if (statistics.get(i).equalsIgnoreCase("CVar")) {
                doCVar = true;
                doVar = true;
                doMean = true;
            }
            //     if ( ( ( String ) statistics.elementAt( i ) ).equalsIgnoreCase( "WCVar" ) ) { doWCVar = true; doWVar = true; doWMean = true;  }
            if (statistics.get(i).equalsIgnoreCase("CSkew")) {
                doCSkew = true;
                doSkew = true;
                doVar = true;
                doMean = true;
            }
            //   if ( ( ( String ) statistics.elementAt( i ) ).equalsIgnoreCase( "WCSkew" ) ) { doWCSkew = true; doWSkew = true; doWVar = true; doWMean = true;   }
        }

        Grids_GridDouble sumWeightGrid = null;

        Grids_GridDouble sumGrid = null;
        Grids_GridDouble wSumGrid = null;
        Grids_GridDouble nWSumGrid = null;
        Grids_GridDouble wSumNGrid = null;

        Grids_GridDouble meanGrid = null;
        Grids_GridDouble wMean1Grid = null;
        Grids_GridDouble wMean2Grid = null;
        Grids_GridDouble nWMeanGrid = null;
        Grids_GridDouble wMeanNGrid = null;

        Grids_GridDouble propGrid = null;
        Grids_GridDouble wPropGrid = null;
        Grids_GridDouble varGrid = null;
        Grids_GridDouble wVarGrid = null;
        Grids_GridDouble skewGrid = null;
        Grids_GridDouble wSkewGrid = null;
        Grids_GridDouble cVarGrid = null;
        Grids_GridDouble wCVarGrid = null;
        Grids_GridDouble cSkewGrid = null;
        Grids_GridDouble wCSkewGrid = null;

        //Grid2DSquareCellDouble zscoreGrid = null;
        //Grid2DSquareCellDouble weightedZscoreGrid = null;
        gf.setNoDataValue(ndvd);

        // First order stats ( Mean WMean Sum WSum  Density WDensity )
        if (doSum || doWSum || doNWSum || doWSumN || doMean || doWMean1
                || doWMean2 || doNWMean || doWMeanN) {
            sumWeightGrid = gf.create(nrows, ncols, dimensions);
            if (doSum) {
                sumGrid = gf.create(nrows, ncols, dimensions);
            }
            if (doWSum) {
                wSumGrid = gf.create(nrows, ncols, dimensions);
            }
            if (doNWSum) {
                nWSumGrid = gf.create(nrows, ncols, dimensions);
            }
            if (doWSumN) {
                wSumNGrid = gf.create(nrows, ncols, dimensions);
            }
            if (doMean) {
                meanGrid = gf.create(nrows, ncols, dimensions);
            }
            if (doWMean1) {
                wMean1Grid = gf.create(nrows, ncols, dimensions);
            }
            if (doWMean2) {
                wMean2Grid = gf.create(nrows, ncols, dimensions);
            }
            if (doNWMean) {
                nWMeanGrid = gf.create(nrows, ncols, dimensions);
            }
            if (doWMeanN) {
                wMeanNGrid = gf.create(nrows, ncols, dimensions);
            }
            Math_BigRational[] kernelParameters = Grids_Kernel.getKernelParameters(grid,
                    cellDistance, distance, weightIntersect, weightFactor, oom);
            Math_BigRational totalSumWeight = kernelParameters[0];
            Math_BigRational totalCells = kernelParameters[1];
            long row;
            long col;
            int p;
            int q;
            Math_BigRational[][] kernel = Grids_Kernel.getKernelWeights(grid,
                    distance, weightIntersect, weightFactor, oom);
            double[][] data = getRowProcessInitialData(grid, cellDistance, 0);
            for (row = 0; row < nrows; row++) {
//                //debug
//                System.out.println("row " + row);
                for (col = 0; col < ncols; col++) {
//                    //debug
//                    if (row == 21) {
//                        System.out.println("col " + col);
//                    }
                    if (!(row == 0 && col == 0)) {
                        data = getRowProcessData(grid, data, cellDistance, row,
                                col);
                    }
                    Math_BigRational sumCells = Math_BigRational.ZERO;
                    Math_BigRational sumWeight = Math_BigRational.ZERO;
                    Math_BigRational sum = Math_BigRational.ZERO;
                    Math_BigRational wSum = Math_BigRational.ZERO;
                    Math_BigRational nWSum = Math_BigRational.ZERO;
                    Math_BigRational wSumN = Math_BigRational.ZERO;
                    Math_BigRational wMean = Math_BigRational.ZERO;
                    Math_BigRational nWMean = Math_BigRational.ZERO;
                    //wMeanN = 0.0d;
                    // Error thrown from here!
                    // GC overhead limit exceeded
                    // java.lang.OutOfMemoryError: GC overhead limit exceeded
                    // There is probably a better doing way?
                    Math_BigRational cellX = grid.getCellX(col);
                    Math_BigRational cellY = grid.getCellY(row);
                    // Calculate sumWeights and non-weighted stats
                    for (p = 0; p <= cellDistance * 2; p++) {
                        for (q = 0; q <= cellDistance * 2; q++) {
                            double v = data[p][q];
                            Math_BigRational weight = kernel[p][q];
                            if ((weight.toBigDecimal().compareTo(ndv) != 0) && v != ndvd) {
                                sumWeight = sumWeight.add(weight);
                                sumCells = sumCells.add(Math_BigRational.ONE);
                                sum = sum.add(Math_BigRational.valueOf(v));
                            }
                        }
                    }
                    // Calculate weighted stats and store results
                    if ((sumCells.compareTo(Math_BigRational.ZERO) == 1)
                            && (sumWeight.compareTo(Math_BigRational.ZERO) == 1)) {
                        for (p = 0; p <= cellDistance * 2; p++) {
                            for (q = 0; q <= cellDistance * 2; q++) {
                                double v = data[p][q];
                                Math_BigRational weight = kernel[p][q];
                                if ((weight.toBigDecimal().compareTo(ndv) != 0) && v != ndvd) {
                                    Math_BigRational vbd = Math_BigRational.valueOf(v);
                                    sumWeight = sumWeight.add(weight);
                                    sumCells = sumCells.add(Math_BigRational.ONE);
                                    sum = sum.add(vbd);
//                                    nWSum = nWSum.add(vbd.multiply(
//                                            Math_BigDecimal
//                                                    .divideRoundIfNecessary(
//                                                            sumWeight, totalSumWeight, oom, rm)).multiply(weight));
                                    nWSum = nWSum.add(vbd.multiply(
                                            sumWeight.divide(totalSumWeight)).multiply(weight));
                                    wSum = wSum.add(vbd.multiply(weight));
                                    wMean = wMean.add(vbd.divide(sumWeight).multiply(weight));
                                }
                            }
                        }
                        sumWeightGrid.setCell(row, col,
                                sumWeight.doubleValue() / totalSumWeight.doubleValue());
                        //if ( doSum ) { sumGrid.setCell( row, col, sum ); }
                        if (doSum) {
                            sumGrid.setCell(row, col,
                                    sum.multiply(sumCells).doubleValue() / totalCells.doubleValue());
                        }
                        if (doWSum) {
                            wSumGrid.setCell(row, col, wSum.doubleValue());
                        }
                        if (doNWSum) {
                            nWSumGrid.setCell(row, col, nWSum.doubleValue());
                        }
                        if (doWSumN) {
                            wSumNGrid.setCell(row, col,
                                    wSum.multiply(sumWeight).doubleValue() / totalSumWeight.doubleValue());
                        }
                        if (doMean) {
                            meanGrid.setCell(row, col, sum.doubleValue() / sumCells.doubleValue());
                        }
                        if (doWMean1) {
                            wMean1Grid.setCell(row, col, wSum.doubleValue() / sumWeight.doubleValue());
                        }
                        if (doWMean2) {
                            wMean2Grid.setCell(row, col, wMean.doubleValue());
                        }
                        if (doNWMean) {
                            nWMeanGrid.setCell(row, col, nWSum.doubleValue() / sumWeight.doubleValue());
                        }
                        if (doWMeanN) {
                            wMeanNGrid.setCell(row, col,
                                    (wMean.multiply(sumWeight)).doubleValue() / totalSumWeight.doubleValue());
                        }

                    }
                }
            }
        }

        // Second order statistics ( coefficient of variation, skewness, kurtosis, zscore)
        if (doProp || doWProp || doVar || doWVar || doSkew || doWSkew || doWCVar || doCSkew || doWCSkew) {
            IO_Path dir;
            if (doProp) {
                propGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doWProp) {
                wPropGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doVar) {
                varGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doWVar) {
                wVarGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doSkew) {
                skewGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doWSkew) {
                wSkewGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doCVar) {
                cVarGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doWCVar) {
                wCVarGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doCSkew) {
                cSkewGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doWCSkew) {
                wCSkewGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            Math_BigRational[] kernelParameters = Grids_Kernel.getKernelParameters(grid,
                    cellDistance, distance, weightIntersect, weightFactor, oom);
            Math_BigRational totalSumWeight = kernelParameters[0];
            Math_BigRational totalCells = kernelParameters[1];
            double numerator;
            double denominator;
            long row;
            long col;
            int p;
            int q;
            Math_BigRational[][] kernel = Grids_Kernel.getKernelWeights(grid, distance, weightIntersect, weightFactor, oom);
            double[][] data = getRowProcessInitialData(grid, cellDistance, 0);
            //double[][] meanData = getRowProcessInitialData( meanGrid, cellDistance, 0 );
            double[][] wMeanData = getRowProcessInitialData(wMean1Grid, cellDistance, 0);
            for (row = 0; row < nrows; row++) {
                for (col = 0; col < ncols; col++) {
                    if (row != 0 && col != 0) {
                        data = getRowProcessData(
                                grid, data, cellDistance, row, col);
                        wMeanData = getRowProcessData(
                                wMean1Grid, wMeanData, cellDistance, row, col);
                        //meanData = getRowProcessData( meanGrid, meanData, cellDistance, row, col );
                    }
                    //sDMean = 0.0d;
                    //sDMeanPow2 = 0.0d;
                    //sDMeanPow3 = 0.0d;
                    //sDMeanPow4 = 0.0d;
                    //sumCells = 0.0d;
                    Math_BigRational sDWMean = Math_BigRational.ZERO;
                    Math_BigRational sDWMeanPow2 = Math_BigRational.ZERO;
                    Math_BigRational sDWMeanPow3 = Math_BigRational.ZERO;
                    Math_BigRational sDWMeanPow4 = Math_BigRational.ZERO;
                    Math_BigRational sumWeight = Math_BigRational.ZERO;
                    Math_BigRational cellX = grid.getCellX(col);
                    Math_BigRational cellY = grid.getCellY(row);
                    // Take moments
                    for (p = 0; p <= cellDistance * 2; p++) {
                        for (q = 0; q <= cellDistance * 2; q++) {
                            double v = data[p][q];
                            Math_BigRational wMean = Math_BigRational.valueOf(wMeanData[p][q]);
                            Math_BigRational weight = kernel[p][q];
                            if (v != ndvd && (weight.toBigDecimal().compareTo(ndv) != 0)) {
                                Math_BigRational vbd = Math_BigRational.valueOf(v);
                                sumWeight = sumWeight.add(weight);
                                sDWMean = sDWMean.add((vbd.subtract(wMean)).multiply(weight));
                                sDWMeanPow2 = sDWMeanPow2.add((vbd.subtract(wMean)).pow(2).multiply(weight));
                                sDWMeanPow3 = sDWMeanPow3.add((vbd.subtract(wMean)).pow(3).multiply(weight));
                                sDWMeanPow4 = sDWMeanPow4.add((vbd.subtract(wMean)).pow(4).multiply(weight));
                                //sumCells += 1.0d;
                                //if ( doMean ) {
                                //    sDMean += ( value - mean );
                                //    sDMeanPow2 += Math.pow( ( value - mean ), 2.0d );
                                //    sDMeanPow3 += Math.pow( ( value - mean ), 3.0d );
                                //    sDMeanPow4 += Math.pow( ( value - mean ), 4.0d );
                                //}
                            }
                        }
                    }
                    //if ( sumCells > 0.0d && sumWeight > 0.0d ) {
                    if (sumWeight.compareTo(Math_BigRational.ZERO) == 1) {
                        //if ( doProp ) {
                        //    propGrid.setCell( row, col, ( sDMean / sumCells ) );
                        //}
                        if (doWProp) {
                            wPropGrid.setCell(row, col, sDWMean.doubleValue() / sumWeight.doubleValue());
                        }
                        //if ( doVar ) {
                        //    varGrid.setCell( row, col, ( sDMeanPow2 / sumCells ) );
                        //}
                        if (doWVar) {
                            wVarGrid.setCell(row, col, sDWMeanPow2.doubleValue() / sumWeight.doubleValue());
                        }
                        //if ( doSkew ) {
                        //    // Need to control for Math.pow as it does not do roots of negative numbers at all well!
                        //    numerator = sDMeanPow3 / sumCells;
                        //    if ( numerator > 0.0d ) {
                        //        skewGrid.setCell(row, col, ( Math.pow( numerator, 1.0d / 3.0d ) ) );
                        //    }
                        //    if ( numerator == 0.0d ) {
                        //        skewGrid.setCell( row, col, numerator );
                        //    }
                        //    if ( numerator < 0.0d ) {
                        //        skewGrid.setCell( row, col, -1.0d * ( Math.pow( Math.abs( numerator ), 1.0d / 3.0d ) ) );
                        //    }
                        //}
                        if (doWSkew) {
                            // Need to control for Math.pow as it does not do roots of negative numbers at all well!
                            numerator = sDWMeanPow3.doubleValue() / sumWeight.doubleValue();
                            if (numerator > 0.0d) {
                                wSkewGrid.setCell(row, col,
                                        (Math.pow(numerator, 1.0d / 3.0d)));
                            }
                            if (numerator == 0.0d) {
                                wSkewGrid.setCell(row, col, numerator);
                            }
                            if (numerator < 0.0d) {
                                wSkewGrid.setCell(row, col,
                                        -1.0d * (Math.pow(Math.abs(numerator), 1.0d / 3.0d)));
                            }
                        }
                        //if ( doCVar ) {
                        //    denominator = varGrid.getCell( row, col );
                        //    if ( denominator > 0.0d && denominator != noDataValue) {
                        //        numerator = propGrid.getCell( row, col );
                        //        if ( numerator != noDataValue ) {
                        //            cVarGrid.setCell( row, col, ( numerator / denominator ) );
                        //        }
                        //    }
                        //}
                        if (doWCVar) {
                            denominator = wVarGrid.getCell(row, col);
                            if (denominator > 0.0d && denominator != ndvd) {
                                numerator = wPropGrid.getCell(row, col);
                                if (numerator != ndvd) {
                                    wCVarGrid.setCell(row, col,
                                            (numerator / denominator));
                                }
                            }
                        }
                        //if ( doCSkew ) {
                        //    // Need to control for Math.pow as it does not do roots of negative numbers at all well!
                        //    denominator = varGrid.getCell( row, col );
                        //    if ( denominator > 0.0d && denominator != noDataValue ) {
                        //        numerator = sDMeanPow3 / sumCells;
                        //        if ( numerator > 0.0d ) {
                        //            cSkewGrid.setCell( row, col, ( Math.pow( numerator, 1.0d / 3.0d ) ) / denominator );
                        //        }
                        //        if ( numerator == 0.0d ) {
                        //            cSkewGrid.setCell( row, col, numerator );
                        //        }
                        //        if ( numerator < 0.0d ) {
                        //            cSkewGrid.setCell( row, col, ( -1.0d * ( Math.pow( Math.abs( numerator ), 1.0d / 3.0d ) ) ) / denominator );
                        //        }
                        //    }
                        //}
                        if (doWCSkew) {
                            // Need to control for Math.pow as it does not do roots of negative numbers at all well!
                            denominator = wVarGrid.getCell(row, col);
                            if (denominator > 0.0d && denominator != ndvd) {
                                numerator = sDWMeanPow3.doubleValue() / sumWeight.doubleValue();
                                if (numerator > 0.0d) {
                                    wCSkewGrid.setCell(row, col,
                                            (Math.pow(numerator, 1.0d / 3.0d)) / denominator);
                                }
                                if (numerator == 0.0d) {
                                    wCSkewGrid.setCell(row, col, numerator);
                                }
                                if (numerator < 0.0d) {
                                    wCSkewGrid.setCell(row, col,
                                            (-1.0d * (Math.pow(Math.abs(numerator), 1.0d / 3.0d))) / denominator);
                                }
                            }
                        }
                    }
                }
            }
        }

        /*
         * if ( doZscore || doWZscore ) { if ( doZscore ) { zscoreGrid =
         * gridFactory.createGrid2DSquareCellDouble( nrows, ncols, xllcorner,
         * yllcorner, cellsize, noDataValue ); } if ( doWZscore ) {
         * weightedZscoreGrid = gridFactory.createGrid2DSquareCellDouble( nrows,
         * ncols, xllcorner, yllcorner, cellsize, noDataValue ); } double
         * weightedMean; double standardDeviation; double
         * weightedStandardDeviation; for ( int i = 0; i < nrows; i ++ ) { for (
         * int j = 0; j < ncols; j ++ ) { value = grid.getCell( i, j ); if (
         * value != noDataValue ) { standardDeviation =
         * standardDeviationGrid.getCell( i, j ); if ( standardDeviation !=
         * noDataValue && standardDeviation > 0.0d ) { zscoreGrid.setCell( i, j,
         * ( value - meanGrid.getCell( i, j ) ) / standardDeviation ); }
         * weightedStandardDeviation = weightedStandardDeviationGrid.getCell( i,
         * j ); if ( weightedStandardDeviation != noDataValue &&
         * weightedStandardDeviation > 0.0d ) { weightedZscoreGrid.setCell( i,
         * j, ( value - weightedMeanGrid.getCell( i, j ) ) /
         * weightedStandardDeviation ); } } } } Vector secondOrderStatistics =
         * new Vector( 1 ); secondOrderStatistics.add( new String( "Mean" ) );
         * Grids_GridDouble[] meanWeightedZscoreGrid =
         * regionUnivariateStatistics( weightedZscoreGrid,
         * secondOrderStatistics, distance, weightIntersect, weightFactor,
         * gridFactory ); Grids_GridDouble[] meanZscoreGrid =
         * regionUnivariateStatistics( zscoreGrid, secondOrderStatistics,
         * distance, weightIntersect, weightFactor, gridFactory );
         * weightedZscoreGrid = meanWeightedZscoreGrid[ 0 ]; zscoreGrid =
         * meanZscoreGrid[ 0 ]; }
         */
        sumWeightGrid.setName("SumWeight_" + grid.getName());
        r.add(sumWeightGrid);

        if (doSum) {
            sumGrid.setName("Sum_" + grid.getName());
            r.add(sumGrid);
        }
        if (doWSum) {
            wSumGrid.setName("WSum_" + grid.getName());
            r.add(wSumGrid);
        }
        if (doNWSum) {
            nWSumGrid.setName("NWSum_" + grid.getName());
            r.add(nWSumGrid);
        }
        if (doWSumN) {
            wSumNGrid.setName("WSumN_" + grid.getName());
            r.add(wSumNGrid);
        }

        if (doMean) {
            meanGrid.setName("Mean_" + grid.getName());
            r.add(meanGrid);
        }
        if (doWMean1) {
            wMean1Grid.setName("WMean1_" + grid.getName());
            r.add(wMean1Grid);
        }
        if (doWMean2) {
            wMean2Grid.setName("WMean2_" + grid.getName());
            r.add(wMean2Grid);
        }
        if (doNWMean) {
            nWMeanGrid.setName("NWMean_" + grid.getName());
            r.add(nWMeanGrid);
        }
        if (doWMeanN) {
            wMeanNGrid.setName("WMeanN_" + grid.getName());
            r.add(wMeanNGrid);
        }

        if (doProp) {
            propGrid.setName("Prop_" + grid.getName());
            r.add(propGrid);
        }
        if (doWProp) {
            wPropGrid.setName("WProp_" + grid.getName());
            r.add(wPropGrid);
        }
        if (doVar) {
            varGrid.setName("Var_" + grid.getName());
            r.add(varGrid);
        }
        if (doWVar) {
            wVarGrid.setName("WVar_" + grid.getName());
            r.add(wVarGrid);
        }
        if (doSkew) {
            skewGrid.setName("Skew_" + grid.getName());
            r.add(skewGrid);
        }
        if (doWSkew) {
            wSkewGrid.setName("WSkew_" + grid.getName());
            r.add(wSkewGrid);
        }
        if (doCVar) {
            cVarGrid.setName("CVar_" + grid.getName());
            r.add(cVarGrid);
        }
        if (doWCVar) {
            wCVarGrid.setName("WCVar_" + grid.getName());
            r.add(wCVarGrid);
        }
        if (doCSkew) {
            cSkewGrid.setName("CSkew" + grid.getName());
            r.add(cSkewGrid);
        }
        if (doWCSkew) {
            wCSkewGrid.setName("WCSkew_" + grid.getName());
            r.add(wCSkewGrid);
        }

        return r;
    }

    /**
     * Get region uni-variate statistics.
     *
     * @param g The grid to be processed.
     * @param statistics The statistics to generate.
     * @param d The distance defining the region within which values will be
     * used. At distances weights if applied are zero
     * @param wi The weight intersect - typically a number between 0 and 1 which
     * controls the weight applied at the centre of the kernel
     * @param wf The weight factor.
     * @param gf The factory used to create grids.
     * @param oom Order Of Magnitude for any rounding.
     * @param rm The {@link RoundingMode} to use for BigDecimal arithmetic.
     * @return Region uni-variate statistics.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public List<Grids_GridNumber> regionUnivariateStatisticsSlow(
            Grids_GridDouble g, List<String> statistics, Math_BigRationalSqrt d,
            Math_BigRational wi, int wf, Grids_GridDoubleFactory gf, int oom,
            RoundingMode rm)
            throws IOException, ClassNotFoundException, Exception {
        List<Grids_GridNumber> result = new ArrayList<>();
        long ncols = g.getNCols();
        long nrows = g.getNRows();
        Grids_Dimensions dimensions = g.getDimensions();
        double noDataValue = g.getNoDataValue();
        int cellDistance = g.getCellDistance(d);
        boolean doMean = false;
        boolean doWMean = false;
        boolean doSum = false;
        boolean doWSum = false;
        boolean doProp = false;
        boolean doWProp = false;
        boolean doVar = false;
        boolean doWVar = false;
        boolean doSkew = false;
        boolean doWSkew = false;
        boolean doCVar = false;
        boolean doWCVar = false;
        boolean doCSkew = false;
        boolean doWCSkew = false;
        //boolean doZscore = false;
        //boolean doWZscore = false;
        long row;
        long col;
        for (int i = 0; i < statistics.size(); i++) {
            if (((String) statistics.get(i)).equalsIgnoreCase("FirstOrder")) {
                doMean = true;
                doWMean = true;
                doSum = true;
                doWSum = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WeightedFirstOrder")) {
                doWMean = true;
                doWSum = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("Mean")) {
                doMean = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WMean")) {
                doWMean = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("Sum")) {
                doSum = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WSum")) {
                doWSum = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("SecondOrder")) {
                doMean = true;
                doWMean = true;
                doProp = true;
                doWProp = true;
                doVar = true;
                doWVar = true;
                doSkew = true;
                doWSkew = true;
                doCVar = true;
                doWCVar = true;
                doCSkew = true;
                doWCSkew = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WeightedSecondOrder")) {
                doWMean = true;
                doWProp = true;
                doWVar = true;
                doWSkew = true;
                doWCVar = true;
                doWCSkew = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("Prop")) {
                doProp = true;
                doMean = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WProp")) {
                doWProp = true;
                doMean = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("Var")) {
                doVar = true;
                doMean = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WVar")) {
                doWVar = true;
                doWMean = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("Skew")) {
                doSkew = true;
                doMean = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WSkew")) {
                doWSkew = true;
                doWMean = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("CVar")) {
                doCVar = true;
                doVar = true;
                doMean = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WCVar")) {
                doWCVar = true;
                doWVar = true;
                doWMean = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("CSkew")) {
                doCSkew = true;
                doSkew = true;
                doVar = true;
                doMean = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WCSkew")) {
                doWCSkew = true;
                doWSkew = true;
                doWVar = true;
                doWMean = true;
            }
        }
        Grids_GridDouble meanGrid = null;
        Grids_GridDouble wMeanGrid = null;
        Grids_GridDouble sumGrid = null;
        Grids_GridDouble wSumGrid = null;
        Grids_GridDouble propGrid = null;
        Grids_GridDouble wPropGrid = null;
        Grids_GridDouble varGrid = null;
        Grids_GridDouble wVarGrid = null;
        Grids_GridDouble skewGrid = null;
        Grids_GridDouble wSkewGrid = null;
        Grids_GridDouble cVarGrid = null;
        Grids_GridDouble wCVarGrid = null;
        Grids_GridDouble cSkewGrid = null;
        Grids_GridDouble wCSkewGrid = null;
        gf.setNoDataValue(noDataValue);
        // First order stats ( Mean WMean Sum WSum  Density WDensity )
        if (doMean || doWMean || doSum || doWSum) {
            IO_Path dir;
            if (doMean) {
                meanGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doWMean) {
                wMeanGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doSum) {
                sumGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doWSum) {
                wSumGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            Math_BigRational[] kernelParameters = Grids_Kernel.getKernelParameters(g, cellDistance, d, wi, wf, oom);
            Math_BigRational totalSumWeight = kernelParameters[0];
            Math_BigRational totalCells = kernelParameters[1];
            for (row = 0; row < nrows; row++) {
                //debug
                System.out.println("processing row " + row + " out of " + nrows);
                for (col = 0; col < ncols; col++) {
                    Math_BigRational sumWeight = Math_BigRational.ZERO;
                    Math_BigRational wMean = Math_BigRational.ZERO;
                    Math_BigRational sumCells = Math_BigRational.ZERO;
                    Math_BigRational wSum = Math_BigRational.ZERO;
                    Math_BigRational sum = Math_BigRational.ZERO;
                    Math_BigRational cellX = g.getCellX(col);
                    Math_BigRational cellY = g.getCellY(row);
                    // Calculate sumWeights and non-weighted stats
                    for (int p = -cellDistance; p <= cellDistance; p++) {
                        for (int q = -cellDistance; q <= cellDistance; q++) {
                            double v = g.getCell(row + p, col + q);
                            if (v != noDataValue) {
                                Math_BigRational thisCellX = g.getCellX(col + q);
                                Math_BigRational thisCellY = g.getCellY(row + p);
                                Math_BigRationalSqrt thisDistance = Grids_Utilities.distance(cellX, cellY, thisCellX, thisCellY, oom);
                                if (thisDistance.getX().compareTo(d.getX()) == -1) {
                                    sumWeight = sumWeight.add(Grids_Kernel.getKernelWeight(d, wi, wf, thisDistance, oom));
                                    sumCells = sumCells.add(Math_BigRational.ONE);
                                    sum = sum.add(Math_BigRational.valueOf(v));
                                }
                            }
                        }
                    }
                    //sumWeightGrid.setCell( i, j, sumWeight );
                    //sumCellGrid.setCell( i, j, sumCells );
                    // Calculate weighted stats and store results
                    if (sumCells.compareTo(Math_BigRational.ZERO) == 1 && sumWeight.compareTo(Math_BigRational.ZERO) == 1) {
                        for (int p = -cellDistance; p <= cellDistance; p++) {
                            for (int q = -cellDistance; q <= cellDistance; q++) {
                                double v = g.getCell(row + p, col + q);
                                if (v != noDataValue) {
                                    Math_BigRational thisCellX = g.getCellX(col + q);
                                    Math_BigRational thisCellY = g.getCellY(row + p);
                                    Math_BigRationalSqrt thisDistance = Grids_Utilities.distance(cellX, cellY, thisCellX, thisCellY, oom);
                                    if (thisDistance.compareTo(d) == -1) {
                                        Math_BigRational weight = Grids_Kernel.getKernelWeight(d, wi, wf, thisDistance, oom);
                                        //wMean += ( value / sumWeight ) * weight;
                                        //wMean += ( value / sumCells ) * weight;
                                        wSum = wSum.add(Math_BigRational.valueOf(v).multiply(weight));
                                    }
                                }
                            }
                        }
                        if (doMean) {
                            meanGrid.setCell(row, col, sum.doubleValue() / sumCells.doubleValue());
                        }
                        if (doWMean) {
                            wMeanGrid.setCell(row, col, wSum.doubleValue() / sumWeight.doubleValue());
                        }
                        //if ( doSum ) { sumGrid.setCell( row, col, sum ); }
                        //if ( doWSum ) { wSumGrid.setCell( row, col, wSum ); }
                        if (doSum) {
                            sumGrid.setCell(row, col, (sum.multiply(sumCells)).doubleValue() / totalCells.doubleValue());
                        }
                        if (doWSum) {
                            wSumGrid.setCell(row, col, (wSum.multiply(sumWeight)).doubleValue() / totalSumWeight.doubleValue());
                        }
                    }
                }
            }
        }

        // Second order statistics ( coefficient of variation, skewness, kurtosis, zscore)
        if (doProp || doWProp || doVar || doWVar || doSkew || doWSkew || doWCVar || doCSkew || doWCSkew) {
            IO_Path dir;
            if (doProp) {
                propGrid = gf.create(nrows, ncols, dimensions);
            }
            if (doWProp) {
                wPropGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doVar) {
                varGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doWVar) {
                wVarGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doSkew) {
                skewGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doWSkew) {
                wSkewGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doCVar) {
                cVarGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doWCVar) {
                wCVarGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doCSkew) {
                cSkewGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            if (doWCSkew) {
                wCSkewGrid = (Grids_GridDouble) gf.create(nrows, ncols, dimensions);
            }
            Math_BigRational[] kernelParameters = Grids_Kernel.getKernelParameters(g, cellDistance, d, wi, wf, oom);
            Math_BigRational totalSumWeight = kernelParameters[0];
            Math_BigRational totalCells = kernelParameters[1];
            Math_BigRational mean = Math_BigRational.ZERO;
            for (row = 0; row < nrows; row++) {
                //debug
                System.out.println("processing row " + row + " out of " + nrows);
                for (col = 0; col < ncols; col++) {
                    Math_BigRational sDMean = Math_BigRational.ZERO;
                    Math_BigRational sDMeanPow2 = Math_BigRational.ZERO;
                    Math_BigRational sDMeanPow3 = Math_BigRational.ZERO;
                    Math_BigRational sDMeanPow4 = Math_BigRational.ZERO;
                    Math_BigRational sumCells = Math_BigRational.ZERO;
                    Math_BigRational sDWMean = Math_BigRational.ZERO;
                    Math_BigRational sDWMeanPow2 = Math_BigRational.ZERO;
                    Math_BigRational sDWMeanPow3 = Math_BigRational.ZERO;
                    Math_BigRational sDWMeanPow4 = Math_BigRational.ZERO;
                    Math_BigRational sumWeight = Math_BigRational.ZERO;
                    Math_BigRational cellX = g.getCellX(col);
                    Math_BigRational cellY = g.getCellY(row);
                    // Take moments
                    for (int p = -cellDistance; p <= cellDistance; p++) {
                        for (int q = -cellDistance; q <= cellDistance; q++) {
                            double v = g.getCell(row + p, col + q);
                            if (v != noDataValue) {
                                Math_BigRational thisCellX = g.getCellX(col + q);
                                Math_BigRational thisCellY = g.getCellY(row + p);
                                Math_BigRationalSqrt thisDistance = Grids_Utilities.distance(cellX, cellY, thisCellX, thisCellY, oom);
                                if (thisDistance.compareTo(d) == -1) {
                                    Math_BigRational vbd = Math_BigRational.valueOf(v);
                                    Math_BigRational wMean = Math_BigRational.valueOf(wMeanGrid.getCell(row + p, col + q));
                                    Math_BigRational weight = Grids_Kernel.getKernelWeight(d, wi, wf, thisDistance, oom);
                                    sumWeight = sumWeight.add(weight);
                                    Math_BigRational delta = vbd.subtract(wMean);
                                    sDWMean = sDWMean.add(delta.multiply(weight));
                                    sDWMeanPow2 = sDWMeanPow2.add(delta.pow(2).multiply(weight));
                                    sDWMeanPow3 = sDWMeanPow3.add(delta.pow(3).multiply(weight));
                                    sDWMeanPow4 = sDWMeanPow4.add(delta.pow(4).multiply(weight));
                                    sumCells = sumCells.add(Math_BigRational.ONE);
                                    if (doMean) {
                                        sDMean = sDMean.add((vbd.subtract(mean)));
                                        sDMeanPow2 = sDMeanPow2.add(delta.pow(2));
                                        sDMeanPow3 = sDMeanPow3.add(delta.pow(3));
                                        sDMeanPow4 = sDMeanPow4.add(delta.pow(4));
                                    }
                                }
                            }
                        }
                    }
                    if (sumCells.compareTo(Math_BigRational.ZERO) == 1 && sumWeight.compareTo(Math_BigRational.ZERO) == 1) {
                        if (doProp) {
                            propGrid.setCell(row, col, (sDMean.doubleValue() / sumCells.doubleValue()));
                        }
                        if (doWProp) {
                            wPropGrid.setCell(row, col, (sDWMean.doubleValue() / sumWeight.doubleValue()));
                        }
                        if (doVar) {
                            varGrid.setCell(row, col, (sDMeanPow2.doubleValue() / sumCells.doubleValue()));
                        }
                        if (doWVar) {
                            wVarGrid.setCell(row, col, (sDWMeanPow2.doubleValue() / sumWeight.doubleValue()));
                        }
                        if (doSkew) {
                            // Need to control for Math.pow as it does not do roots of negative numbers at all well!
                            double numerator = sDMeanPow3.doubleValue() / sumCells.doubleValue();
                            if (numerator > 0.0d) {
                                skewGrid.setCell(row, col, (Math.pow(numerator, 1.0d / 3.0d)));
                            }
                            if (numerator == 0.0d) {
                                skewGrid.setCell(row, col, numerator);
                            }
                            if (numerator < 0.0d) {
                                skewGrid.setCell(row, col, -1.0d * (Math.pow(Math.abs(numerator), 1.0d / 3.0d)));
                            }
                        }
                        if (doWSkew) {
                            // Need to control for Math.pow as it does not do roots of negative numbers at all well!
                            double numerator = sDWMeanPow3.doubleValue() / sumWeight.doubleValue();
                            if (numerator > 0.0d) {
                                wSkewGrid.setCell(row, col, (Math.pow(numerator, 1.0d / 3.0d)));
                            }
                            if (numerator == 0.0d) {
                                wSkewGrid.setCell(row, col, numerator);
                            }
                            if (numerator < 0.0d) {
                                wSkewGrid.setCell(row, col, -1.0d * (Math.pow(Math.abs(numerator), 1.0d / 3.0d)));
                            }
                        }
                        if (doCVar) {
                            double denominator = varGrid.getCell(row, col);
                            if (denominator > 0.0d && denominator != noDataValue) {
                                double numerator = propGrid.getCell(row, col);
                                if (numerator != noDataValue) {
                                    cVarGrid.setCell(row, col, (numerator / denominator));
                                }
                            }
                        }
                        if (doWCVar) {
                            double denominator = wVarGrid.getCell(row, col);
                            if (denominator > 0.0d && denominator != noDataValue) {
                                double numerator = wPropGrid.getCell(row, col);
                                if (numerator != noDataValue) {
                                    wCVarGrid.setCell(row, col, (numerator / denominator));
                                }
                            }
                        }
                        if (doCSkew) {
                            // Need to control for Math.pow as it does not do roots of negative numbers at all well!
                            double denominator = varGrid.getCell(row, col);
                            if (denominator > 0.0d && denominator != noDataValue) {
                                double numerator = sDMeanPow3.doubleValue() / sumCells.doubleValue();
                                if (numerator > 0.0d) {
                                    cSkewGrid.setCell(row, col, (Math.pow(numerator, 1.0d / 3.0d)) / denominator);
                                }
                                if (numerator == 0.0d) {
                                    cSkewGrid.setCell(row, col, numerator);
                                }
                                if (numerator < 0.0d) {
                                    cSkewGrid.setCell(row, col, (-1.0d * (Math.pow(Math.abs(numerator), 1.0d / 3.0d))) / denominator);
                                }
                            }
                        }
                        if (doWCSkew) {
                            // Need to control for Math.pow as it does not do roots of negative numbers at all well!
                            double denominator = wVarGrid.getCell(row, col);
                            if (denominator > 0.0d && denominator != noDataValue) {
                                double numerator = sDWMeanPow3.doubleValue() / sumWeight.doubleValue();
                                if (numerator > 0.0d) {
                                    wCSkewGrid.setCell(row, col, (Math.pow(numerator, 1.0d / 3.0d)) / denominator);
                                }
                                if (numerator == 0.0d) {
                                    wCSkewGrid.setCell(row, col, numerator);
                                }
                                if (numerator < 0.0d) {
                                    wCSkewGrid.setCell(row, col, (-1.0d * (Math.pow(Math.abs(numerator), 1.0d / 3.0d))) / denominator);
                                }
                            }
                        }
                    }
                }
            }
        }

        /*
         * if ( doZscore || doWZscore ) { if ( doZscore ) { zscoreGrid =
         * gridFactory.createGrid2DSquareCellDouble( nrows, ncols, xllcorner,
         * yllcorner, cellsize, noDataValue ); } if ( doWZscore ) {
         * weightedZscoreGrid = gridFactory.createGrid2DSquareCellDouble( nrows,
         * ncols, xllcorner, yllcorner, cellsize, noDataValue ); } double
         * weightedMean; double standardDeviation; double
         * weightedStandardDeviation; for ( int i = 0; i < nrows; i ++ ) { for (
         * int j = 0; j < ncols; j ++ ) { value = grid.getCell( i, j ); if (
         * value != noDataValue ) { standardDeviation =
         * standardDeviationGrid.getCell( i, j ); if ( standardDeviation !=
         * noDataValue && standardDeviation > 0.0d ) { zscoreGrid.setCell( i, j,
         * ( value - meanGrid.getCell( i, j ) ) / standardDeviation ); }
         * weightedStandardDeviation = weightedStandardDeviationGrid.getCell( i,
         * j ); if ( weightedStandardDeviation != noDataValue &&
         * weightedStandardDeviation > 0.0d ) { weightedZscoreGrid.setCell( i,
         * j, ( value - weightedMeanGrid.getCell( i, j ) ) /
         * weightedStandardDeviation ); } } } } Vector secondOrderStatistics =
         * new Vector( 1 ); secondOrderStatistics.add( new String( "Mean" ) );
         * Grids_GridDouble[] meanWeightedZscoreGrid =
         * regionUnivariateStatistics( weightedZscoreGrid,
         * secondOrderStatistics, distance, weightIntersect, weightFactor,
         * gridFactory ); Grids_GridDouble[] meanZscoreGrid =
         * regionUnivariateStatistics( zscoreGrid, secondOrderStatistics,
         * distance, weightIntersect, weightFactor, gridFactory );
         * weightedZscoreGrid = meanWeightedZscoreGrid[ 0 ]; zscoreGrid =
         * meanZscoreGrid[ 0 ]; }
         */
        if (doSum) {
            sumGrid.setName("Sum");
            result.add(sumGrid);
        }
        if (doWSum) {
            wSumGrid.setName("WSum");
            result.add(wSumGrid);
        }
        if (doMean) {
            meanGrid.setName("Mean");
            result.add(meanGrid);
        }
        if (doWMean) {
            wMeanGrid.setName("WMean");
            result.add(wMeanGrid);
        }

        if (doProp) {
            propGrid.setName("Prop");
            result.add(propGrid);
        }
        if (doWProp) {
            wPropGrid.setName("WProp");
            result.add(wPropGrid);
        }
        if (doVar) {
            varGrid.setName("Var");
            result.add(varGrid);
        }
        if (doWVar) {
            wVarGrid.setName("WVar");
            result.add(wVarGrid);
        }
        if (doSkew) {
            skewGrid.setName("Skew");
            result.add(skewGrid);
        }
        if (doWSkew) {
            wSkewGrid.setName("WSkew");
            result.add(wSkewGrid);
        }
        if (doCVar) {
            cVarGrid.setName("CVar");
            result.add(cVarGrid);
        }
        if (doWCVar) {
            wCVarGrid.setName("WCVar");
            result.add(wCVarGrid);
        }
        if (doCSkew) {
            cSkewGrid.setName("CSkew");
            result.add(cSkewGrid);
        }
        if (doWCSkew) {
            wCSkewGrid.setName("WCSkew");
            result.add(wCSkewGrid);
        }

        return result;
    }

    /**
     * Returns an Grid2DSquareCellDouble[] containing geometric density
     * surfaces. The algorithm used for generating a geometric density surfaces
     * is described in: Turner A (2000) Density Data Generation for Spatial Data
     * Mining Applications.
     * http://www.geog.leeds.ac.uk/people/a.turner/papers/geocomp00/gc_017.htm
     *
     * @param grid - the input Grid2DSquareCellDouble used
     * @param distance - the distance limiting the maximum scale density surface
     */
    /*
     * public Grids_GridDouble[] geometricDensity( Grids_GridDouble
     * grid, double distance ) { int nrows = grid.getNRows(); int ncols =
     * grid.getNCols(); double cellsize = grid.getCellsize(); double
     * noDataValue = grid.getNoDataValue(); Grids_GridDouble[] result =
     * null; try { result = ( new Grids_GridDoubleFactory()
     * ).createGrid2DSquareCellDouble( nrows, ncols, grid.getXllcorner(),
     * grid.getYllcorner(), cellsize, noDataValue ); } catch (
     * java.lang.OutOfMemoryError e0 ) { try { if ( grid instanceof
     * Grid2DSquareCellDoubleFile ) { result = ( new
     * Grid2DSquareCellDoubleFileFactory( ( ( Grid2DSquareCellDoubleFile ) grid
     * ).getDataDirectory() ) ).createGrid2DSquareCellDouble( nrows, ncols,
     * grid.getXllcorner(), cellsize, grid.getYllcorner(), noDataValue ); } else
     * { result = ( new Grid2DSquareCellDoubleFileFactory()
     * ).createGrid2DSquareCellDouble( nrows, ncols, grid.getXllcorner(),
     * cellsize, grid.getYllcorner(), noDataValue ); } //} catch (
     * java.io.IOException e1 ) { } catch ( java.lang.Exception e1 ) {
     * System.out.println( e1 ); boolean set = false; while ( ! set ) { try {
     * System.out.println( "Please try setting a different directory for storing
     * the data." ); result = ( new Grid2DSquareCellDoubleFileFactory(
     * Grids_Utilities.setDirectory() ) ).createGrid2DSquareCellDouble( nrows, ncols,
     * grid.getXllcorner(), cellsize, grid.getYllcorner(), noDataValue ); set =
     * true; //} catch ( java.io.IOException e2 ) { } catch (
     * java.lang.Exception e2 ) { System.out.println( e1 ); } } } } int
     * cellDistance = ( int ) Math.ceil( distance / cellsize ); double weight =
     * 1.0d; double d1; boolean chunkProcess = false; try { densityArray =
     * geometricDensity( grid, distance, new Grids_GridDoubleFactory() ) ;
     * } catch ( java.lang.OutOfMemoryError e ) { if ( cellDistance < (
     * Math.max( nrows, ncols ) / 2 ) ) { System.out.println( e.toString() +
     * "... Attempting to process in chunks..." ); int chunkSize = cellDistance;
     * density = geometricDensity( grid, distance, chunkSize ); for ( int cellID
     * = 0; cellID < nrows * ncols; cellID ++ ) { result.setCell( cellID,
     * density.getCell( cellID ) ); } chunkProcess = true; } else {
     * System.out.println( e.toString() + "... Processing using available
     * filespace..." ); try { if ( grid instanceof Grid2DSquareCellDoubleFile )
     * { densityArray = geometricDensity( grid, distance, new
     * Grid2DSquareCellDoubleFileFactory( ( ( Grid2DSquareCellDoubleFile ) grid
     * ).getDataDirectory() ) ); } else { densityArray = geometricDensity( grid,
     * distance, new Grid2DSquareCellDoubleFileFactory() ); } //} catch (
     * java.io.IOException e1 ) { } catch ( java.lang.Exception e1 ) {
     * System.out.println( e1 ); boolean set = false; while ( ! set ) { try {
     * System.out.println( "Please try setting a different directory for storing
     * the data." ); densityArray = geometricDensity( grid, distance, new
     * Grid2DSquareCellDoubleFileFactory( Grids_Utilities.setDirectory() ) ); set =
     * true; //} catch ( java.io.IOException e2 ) { } catch (
     * java.lang.Exception e2 ) { System.out.println( e1 ); } } } } } if ( !
     * chunkProcess ) { double thisDistance = cellsize; for ( int i = 0; i <
     * densityArray.length; i ++ ) { for ( int cellID = 0; cellID < nrows *
     * ncols; cellID ++ ) { d1 = densityArray[ i ].getCell( cellID ); if (
     * grid.getCell( cellID ) != noDataValue ) { result.addToCell( cellID, d1 *
     * weight ); } } thisDistance *= 2.0d; } } return result; }
     */
    /**
     * Returns an Grids_GridDouble[] containing geometric density surfaces at a
     * range of scales: result[ 0 ] - is the result at the first scale ( double
     * the cellsize of grid ) result[ 1 ] - if it exists is the result at the
     * second scale ( double the cellsize of result[ 0 ] ) result[ n ] - if it
     * exists is the result at the ( n + 1 )th scale ( double the cellsize of
     * result[ n - 1 ] ) The algorithm used for generating a geometric density
     * surface is described in: Turner A (2000) Density Data Generation for
     * Spatial Data Mining Applications.
     * http://www.geog.leeds.ac.uk/people/a.turner/papers/geocomp00/gc_017.htm
     *
     * @param grid - the input Grids_GridDouble
     * @param distance - the distance limiting the maximum scale of geometric
     * density surface produced
     * @param gridFactory - the Grids_GridDoubleFactory to be used in processing
     * @return geometric density.
     * @throws Exception If encountered.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    public Grids_GridDouble[] geometricDensity(Grids_GridDouble grid,
            Math_BigRationalSqrt distance, Grids_GridDoubleFactory gridFactory)
            throws IOException, ClassNotFoundException, Exception {
        long n = grid.getStats().getN();
        long nrows = grid.getNRows();
        long ncols = grid.getNCols();
        Grids_Dimensions dimensions = grid.getDimensions();
        double ndv = grid.getNoDataValue();
        int cellDistance = grid.getCellDistance(distance);
        double d1;
        double d2;
        double d3;
        long height = nrows;
        long width = ncols;
        int i1;
        int numberOfIterations;
        int doubler = 1;
        int growth = 1;
        long row;
        long col;
        // Calculate number of iterations and initialise result.
        numberOfIterations = 0;
        i1 = Math.min(cellDistance, (int) Math.floor(Math.max(nrows, ncols) / 2));
        for (int i = 0; i < cellDistance; i++) {
            if (i1 > 1) {
                i1 = i1 / 2;
                numberOfIterations++;
            } else {
                break;
            }
        }
        Grids_GridDouble[] result = new Grids_GridDouble[numberOfIterations];
        IO_Path dir;
        // If all values are noDataValues return noDataValue density results
        if (n == 0) {
            for (int i = 0; i < numberOfIterations; i++) {
                result[i] = (Grids_GridDouble) gridFactory.create(grid);
            }
            return result;
        }
        // Initialise temporary numerator and normaliser grids
        Grids_GridDouble g2 = (Grids_GridDouble) gridFactory.create(nrows, ncols);
        Grids_GridDouble g3 = (Grids_GridDouble) gridFactory.create(nrows, ncols);
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                d1 = grid.getCell(row, col);
                if (d1 != ndv) {
                    //g2.initCell( row, col, d1 );
                    //g3.initCell( row, col, 1.0d );
                    g2.setCell(row, col, d1);
                    g3.setCell(row, col, 1.0d);
                }
            }
        }
        // Densification
        Grids_GridDouble g4;
        Grids_GridDouble g5;
        Grids_GridDouble g6;
        Grids_GridDouble g7;
        Grids_GridDouble density;
        for (int iteration = 0; iteration < numberOfIterations; iteration++) {
            //System.out.println( "Iteration " + ( iteration + 1 ) + " out of " + numberOfIterations );
            height += doubler;
            width += doubler;
            growth *= 2;
            // Step 1: Aggregate
            g4 = (Grids_GridDouble) gridFactory.create(nrows, ncols);
            g5 = (Grids_GridDouble) gridFactory.create(nrows, ncols);
            g6 = (Grids_GridDouble) gridFactory.create(nrows, ncols);
            for (int p = 0; p < doubler; p++) {
                for (int q = 0; q < doubler; q++) {
                    for (row = 0; row < height; row += doubler) {
                        for (col = 0; col < width; col += doubler) {
                            d1 = g2.getCell((row + p),
                                    (col + q))
                                    + g2.getCell((row + p),
                                            (col + q - doubler))
                                    + g2.getCell((row + p - doubler),
                                            (col + q))
                                    + g2.getCell((row + p - doubler),
                                            (col + q - doubler));
                            //g4.initCell( ( row + p ), ( col + q ), d1 );
                            g4.setCell((row + p),
                                    (col + q),
                                    d1);
                            d2 = g3.getCell((row + p),
                                    (col + q))
                                    + g3.getCell((row + p),
                                            (col + q - doubler))
                                    + g3.getCell((row + p - doubler),
                                            (col + q))
                                    + g3.getCell((row + p - doubler),
                                            (col + q - doubler));
                            //g5.initCell( ( row + p ), ( col + q ), d2 );
                            g5.setCell((row + p), (col + q), d2);
                            if (d2 != 0.0d) {
                                //g6.initCell( ( row + p ), ( col + q ), ( d1 / d2 ) );
                                g6.setCell((row + p),
                                        (col + q),
                                        (d1 / d2));
                            }
                        }
                    }
                }
            }
            //            g2.clear();
            //            g3.clear();
            // Step 2: Average over output region.
            // 1. This is probably the slowest part of the algorithm and gets slower
            //    with each iteration. Using alternative data structures and
            //    processing strategies this step can probably be speeded up a lot.
            //density = gridFactory.createGrid2DSquareCellDouble( nrows, ncols, 0.0d, 0.0d, cellsize, 0.0d );
            gridFactory.setNoDataValue(ndv);
            density = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            for (row = 0; row < nrows; row += doubler) {
                for (int p = 0; p < doubler; p++) {
                    for (col = 0; col < ncols; col += doubler) {
                        for (int q = 0; q < doubler; q++) {
                            d1 = 0.0d;
                            d2 = 0.0d;
                            for (int a = 0; a < growth; a++) {
                                for (int b = 0; b < growth; b++) {
                                    if (g6.isInGrid((row + p + a), (col + q + b))) {
                                        d1 += g6.getCell((row + p + a),
                                                (col + q + b));
                                        d2 += 1.0d;
                                    }
                                }
                            }
                            if (d2 != 0.0d) {
                                //density.addToCell( ( row + p ), ( col + q ), ( d1 / d2 ) );
                                //density.initCell( ( row + p ), ( col + q ), ( d1 / d2 ) );
                                density.setCell((row + p), (col + q), (d1 / d2));
                            } else {
                                //density.initCell( ( row + p ), ( col + q ), 0.0d );
                                density.setCell((row + p), (col + q), 0.0d);
                            }
                        }
                    }
                }
            }
            //            g6.clear();
            result[iteration] = density;
            doubler *= 2;
            g2 = g4;
            g3 = g5;
        }
        return result;
    }

    //    /**
    //     * Returns an Grids_GridDouble[] containing geometric density surfaces at a range of
    //     * scales:
    //     * result[ 0 ] - is the result at the first scale ( double the cellsize of grid )
    //     * result[ 1 ] - if it exists is the result at the second scale ( double the cellsize of result[ 0 ] )
    //     * result[ n ] - if it exists is the result at the ( n + 1 )th scale ( double the cellsize of result[ n - 1 ] )
    //     * The algorithm used for generating a geometric density surface is described in:
    //     * Turner A (2000) Density Data Generation for Spatial Data Mining Applications.
    //     * http://www.geog.leeds.ac.uk/people/a.turner/papers/geocomp00/gc_017.htm
    //     * @param grid - the input Grids_GridDouble
    //     * @param distance - the distance limiting the maximum scale of geometric density surface produced
    //     * @param ff - an Grid2DSquareCellDoubleFileFactory to be used in the event of running out of memory
    //     * @param chunksize - the number of rows/columns in largest chunks processed
    //     */
    //    public Grids_GridDouble[] geometricDensity( Grids_GridDouble grid, double distance, Grid2DSquareCellDoubleFileFactory ff, int chunkSize ) {
    //        // Allocate some memory for management
    //        int[] memoryGrab = Grids_Utilities.memoryAllocation( 10000 );
    //        boolean outOfMemoryTrigger0 = false;
    //        Grids_GridDouble[] result = null;
    //        Grid2DSquareCellDoubleJAIFactory jf = new Grid2DSquareCellDoubleJAIFactory();
    //        int nrows = grid.getNRows();
    //        int ncols = grid.getNCols();
    //        double xllcorner = grid.getXllcorner();
    //        double yllcorner = grid.getYllcorner();
    //        double cellsize = grid.getCellsize();
    //        double noDataValue = grid.getNoDataValue();
    //        int cellDistance = ( int ) Math.ceil( distance / cellsize );
    //        // Check chunkSize
    //        if ( chunkSize < ( cellDistance * 3 ) ) {
    //            chunkSize = cellDistance * 3;
    //        }
    //        // Calculate number of iterations and initialise result.
    //        int numberOfIterations = 0;
    //        int i1 = cellDistance;
    //        for ( int i = 0; i < cellDistance; i ++ ) {
    //            if ( i1 > 1 ) {
    //                i1 = i1 / 2;
    //                numberOfIterations ++;
    //            } else {
    //                break;
    //            }
    //        }
    //        result = new Grids_GridDouble[ numberOfIterations ];
    //        for ( int i = 0; i < numberOfIterations; i ++ ) {
    //            result[ i ] = ff.createGrid2DSquareCellDouble( nrows, ncols, xllcorner, yllcorner, cellsize, noDataValue, 1 );
    //            //System.out.println( result[ i ].toString() );
    //        }
    //        int colChunks = ( int ) Math.ceil( ( double ) ncols / ( double ) chunkSize );
    //        int rowChunks = ( int ) Math.ceil( ( double ) nrows / ( double ) chunkSize );
    //        int startRowIndex = 0 - cellDistance;
    //        int endRowIndex = chunkSize - 1 + cellDistance;
    //        int startColIndex = 0 - cellDistance;
    //        int endColIndex = chunkSize - 1 + cellDistance;
    //        Grids_GridDouble chunk;
    //        Grids_GridDouble chunkDensity[];
    //        boolean outOfMemoryTrigger1 = false;
    //        for ( int rowChunk = 0; rowChunk < rowChunks; rowChunk ++ ) {
    //            if ( endRowIndex > nrows - 1 + cellDistance ) {
    //                endRowIndex = nrows - 1 + cellDistance;
    //            }
    //            for ( int colChunk = 0; colChunk < colChunks; colChunk ++ ) {
    //                System.out.println( "Processing chunk " + ( ( rowChunk * colChunks ) + colChunk + 1 ) + " out of " + ( ( rowChunks * colChunks ) - 1 ) + "..." );
    //                if ( endColIndex > ncols - 1 + cellDistance ) {
    //                    endColIndex = ncols - 1 + cellDistance;
    //                }
    //                if ( ! outOfMemoryTrigger0 ) {
    //                    try {
    //                        chunk = jf.createGrid2DSquareCellDouble( grid, startRowIndex, startColIndex, endRowIndex, endColIndex, noDataValue, 1 );
    //                    } catch ( java.lang.OutOfMemoryError e ) {
    //                        outOfMemoryTrigger0 = true;
    //                        chunk = ff.createGrid2DSquareCellDouble( grid, startRowIndex, startColIndex, endRowIndex, endColIndex, noDataValue, 1 );
    //                    }
    //                } else {
    //                    chunk = ff.createGrid2DSquareCellDouble( grid, startRowIndex, startColIndex, endRowIndex, endColIndex, noDataValue, 1 );
    //                }
    //                //System.out.println( "chunk" );
    //                //System.out.println( chunk.toString() );
    //                // Process chunk
    //                if ( ! outOfMemoryTrigger1 ) {
    //                    try {
    //                        chunkDensity = geometricDensity( chunk, distance, jf );
    //                    } catch ( java.lang.OutOfMemoryError e ) {
    //                        memoryGrab = null;
    //                        System.gc();
    //                        outOfMemoryTrigger1 = true;
    //                        chunkDensity = geometricDensity( chunk, distance, ff );
    //                    }
    //                } else {
    //                    chunkDensity = geometricDensity( chunk, distance, ff );
    //                }
    //                // Tidy
    //                chunk.clear();
    //                // Add central part of chunkDensity to result
    //                for ( int i = 0; i < numberOfIterations; i ++ ) {
    //                    //System.out.println( "chunkDensity[ " + i + " ] nrows( " + chunkDensity[ i ].getNRows() + " ), ncols( " + chunkDensity[ i ].getNCols() + " )" );
    //                    //System.out.println( "Scale " + i + " GeometricDensity " + chunkDensity[ i ].toString() );
    //                    addToGrid( result[ i ], chunkDensity[ i ], cellDistance, cellDistance, chunkDensity[ i ].getNRows() - 1 - cellDistance, chunkDensity[ i ].getNCols() - 1 - cellDistance, 1.0d );
    //                    //System.out.println( "result[ " + i + " ]" );
    //                    //System.out.println( result[ i ].toString() );
    //                    // Tidy
    //                    chunkDensity[ i ].clear();
    //                }
    //                startColIndex += chunkSize;
    //                endColIndex += chunkSize;
    //            }
    //            startColIndex = 0 - cellDistance;
    //            endColIndex = chunkSize - 1 + cellDistance;
    //            startRowIndex += chunkSize;
    //            endRowIndex += chunkSize;
    //        }
    //        return result;
    //    }
    //    /**
    //     * Returns an Grids_GridDouble[] result with elements based on
    //     * statistics and values based on bivariate comparison of grid0 and grid1,
    //     * distance, weightIntersect and weightFactor.
    //     * @param grid0 the Grids_GridDouble to be regionBivariateStatisticsd with grid1
    //     * @param grid1 the Grids_GridDouble to be regionBivariateStatisticsd with grid0
    //     * @param statistics a String[] whose elements may be "diff", "correlation",
    //     *                   "zdiff", "density". If they are then the respective
    //     *                   Geographically Weighted Statistics (GWS) are returned
    //     *                   in the result array
    //     * @param distance the distance defining the region within which values will
    //     *                 be used
    //     * @param weightIntersect typically a number between 0 and 1 which controls
    //     *                        the weight applied at the centre of the kernel
    //     * @param weightFactor = 0.0d all values within distance will be equally weighted
    //     *                     > 0.0d means the edge of the kernel has a zero weight
    //     *                     < 0.0d means that the edage of the kernel has a weight of 1
    //     *                     > -1.0d && < 1.0d provides an inverse decay
    //     * TODO:
    //     * 1. Check and ensure that reasonable answers are returned for grids with different spatial frames.
    //     */
    //    public Grids_GridDouble[] regionBivariateStatistics( Grids_GridDouble grid0, Grids_GridDouble grid1, Vector statistics ) {
    //        double distance = grid0.getCellsize() * 5.0d;
    //        double weightIntercept = 1.0d;
    //        double weightFactor = 2.0d;
    //        try {
    //            return regionBivariateStatistics( grid0, grid1, statistics, distance, weightIntercept, weightFactor, new Grids_GridDoubleFactory() );
    //        } catch ( OutOfMemoryError _OutOfMemoryError1 ) {
    //            String dataDirectory = System.getProperty( "java.io.tmpdir" );
    //            if ( grid1 instanceof Grid2DSquareCellDoubleFile ) {
    //                dataDirectory = ( ( Grid2DSquareCellDoubleFile ) grid1 ).getDataDirectory();
    //            }
    //            System.out.println( "Run out of memory! Attempting to reprocess using filespace in directory " + dataDirectory );
    //            return regionBivariateStatistics( grid0, grid1, statistics, distance, weightIntercept, weightFactor, new Grid2DSquareCellDoubleFileFactory( dataDirectory ) );
    //        }
    //    }
    /**
     * Returns an Grids_GridDouble[] result with elements based on statistics
     * and values based on bivariate comparison of grid0 and grid1, distance,
     * weightIntersect and weightFactor.
     *
     * @param grid0 the Grids_GridDouble to be regionBivariateStatisticsd with
     * grid1
     * @param grid1 the Grids_GridDouble to be regionBivariateStatisticsd with
     * grid0
     * @param statistics a String[] whose elements may be "diff", "abs", "corr1"
     * , "corr2", "zscore". If they are then the respective Geographically
     * Weighted Statistics (GWS) are returned in the result array
     * @param distance the distance defining the region within which values will
     * be used
     * @param weightIntersect typically a number between 0 and 1 which controls
     * the weight applied at the centre of the kernel
     * @param weightFactor {@code = 0.0d all values within distance will be equally
     * weighted > 0.0d means the edge of the kernel has a zero weight < 0.0d
     * means that the edage of the kernel has a weight of 1 > -1.0d && < 1.0d
     * provides an inverse decay @param gridFactory the Abstract
     * 2DSquareCellDoubleFactory used to create grids TODO: Check and ensure
     * that reasonable answers are returned for grids with different spatial
     * frames. (NB. Sensibly the two grids being correlated should have the same
     * no data space.)}
     * @param gf Grid fatory.
     * @param oom The number of decimal places for BigDecimal arithmetic..
     * @param rm The {@link RoundingMode} to use for BigDecimal arithmetic.
     * @return Grids_GridDouble[]
     * @throws Exception If encountered.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    public Grids_GridDouble[] regionBivariateStatistics(Grids_GridDouble grid0,
            Grids_GridDouble grid1, ArrayList statistics, Math_BigRationalSqrt distance,
            Math_BigRational weightIntersect, int weightFactor,
            Grids_GridDoubleFactory gf, int oom, RoundingMode rm)
            throws IOException, ClassNotFoundException, Exception {
        boolean hoome = true;
        // Initialisation
        boolean dodiff = false;
        boolean doabs = false;
        boolean docorr = false;
        boolean dozdiff = false;
        int allStatistics = 0;
        for (int i = 0; i < statistics.size(); i++) {
            if (((String) statistics.get(i)).equalsIgnoreCase("diff")) {
                if (!dodiff) {
                    dodiff = true;
                    allStatistics += 4;
                }
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("corr")) {
                if (!docorr) {
                    docorr = true;
                    allStatistics += 2;
                }
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("zdiff")) {
                if (!dozdiff) {
                    dozdiff = true;
                    allStatistics += 2;
                }
            }
        }
        Grids_GridDouble[] result = new Grids_GridDouble[allStatistics];
        Grids_GridDouble diffGrid = null;
        Grids_GridDouble weightedDiffGrid = null;
        Grids_GridDouble normalisedDiffGrid = null;
        Grids_GridDouble weightedNormalisedDiffGrid = null;
        Grids_GridDouble weightedCorrelationGrid = null;
        Grids_GridDouble correlationGrid = null;
        Grids_GridDouble weightedZdiffGrid = null;
        Grids_GridDouble zdiffGrid = null;
        long grid0Nrows = grid0.getNRows();
        long grid0Ncols = grid0.getNCols();
        Grids_Dimensions grid0Dimensions = grid0.getDimensions();
        double grid0NoDataValue = grid0.getNoDataValue();
        long grid1Nrows = grid1.getNRows();
        long grid1Ncols = grid1.getNCols();
        Grids_Dimensions grid1Dimensions = grid1.getDimensions();
        double grid1NoDataValue = grid1.getNoDataValue();
        double noDataValue = grid0NoDataValue;
        int grid0CellDistance = grid0.getCellDistance(distance);

        // setNumberOfPairs is the number of pairs of values needed to calculate
        // the comparison statistics. It must be > 2
        int setNumberOfPairs = 20;

        int n;
        // Intersection check
        Math_BigRational grid0Cellsize = grid0Dimensions.getCellsize();
        Math_BigRational grid1Cellsize = grid1Dimensions.getCellsize();
        if ((grid1Dimensions.getXMin().compareTo(grid0Dimensions.getXMin().add(grid0Dimensions.getWidth())) == 1)
                || (grid1Dimensions.getXMax().compareTo(grid0Dimensions.getXMax().add(grid0Dimensions.getHeight())) == 1)
                || (grid1Dimensions.getYMin().add(grid1Cellsize.multiply(grid1Dimensions.getWidth())).compareTo(grid0Dimensions.getYMin()) == -1)
                || (grid1Dimensions.getYMax().add(grid1Cellsize.multiply(grid1Dimensions.getHeight())).compareTo(grid0Dimensions.getYMin()) == -1)) {
            //System.out.println( "Warning!!! No intersection in " + getClass().getName( hoome ) + " regionBivariateStatistics()" );
            return result;
        }
        //        if ( ( grid1Xllcorner > grid0Xllcorner + ( ( double ) grid0Ncols * grid0Cellsize ) ) ||
        //        ( grid1Yllcorner > grid0Yllcorner + ( ( double ) grid0Nrows * grid0Cellsize ) ) ||
        //        ( grid1Xllcorner + ( ( double ) grid1Ncols * grid1Cellsize ) < grid0Xllcorner ) ||
        //        ( grid1Yllcorner + ( ( double ) grid1Nrows * grid1Cellsize ) < grid0Yllcorner ) ) {
        //            System.out.println( "Warning!!! No intersection in regionBivariateStatistics()" );
        //            return result;
        //        }

        // Set the total sum of all the weights (totalSumWeights) in a
        // region that would have no noDataValues
        Math_BigRational[] kernelParameters = Grids_Kernel.getKernelParameters(grid0, grid0CellDistance, distance, weightIntersect, weightFactor, oom);
        Math_BigRational totalSumWeight = kernelParameters[0];

        // Difference
        if (dodiff) {
            gf.setNoDataValue(grid0NoDataValue);
            diffGrid = (Grids_GridDouble) gf.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            weightedDiffGrid = (Grids_GridDouble) gf.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            normalisedDiffGrid = (Grids_GridDouble) gf.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            weightedNormalisedDiffGrid = (Grids_GridDouble) gf.create(grid0Nrows, grid0Ncols, grid0Dimensions);

            long row;
            long col;
            for (row = 0; row < grid0Nrows; row++) {
                for (col = 0; col < grid0Ncols; col++) {
                    double max0 = Double.MIN_VALUE;
                    double max1 = Double.MIN_VALUE;
                    double min0 = Double.MAX_VALUE;
                    double min1 = Double.MAX_VALUE;
                    Math_BigRational x0 = grid0.getCellX(col);
                    Math_BigRational y0 = grid0.getCellY(row);
                    Math_BigRational diff = Math_BigRational.ZERO;
                    Math_BigRational weightedDiff = Math_BigRational.ZERO;
                    Math_BigRational normalisedDiff = Math_BigRational.ZERO;
                    Math_BigRational weightedNormalisedDiff = Math_BigRational.ZERO;
                    Math_BigRational sumWeight = Math_BigRational.ZERO;
                    n = 0;
                    for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                        for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                            Math_BigRational x1 = grid0.getCellX(col + q);
                            Math_BigRational y1 = grid0.getCellY(row + p);
                            Math_BigRationalSqrt thisDistance = Grids_Utilities.distance(x0, y0, x1, y1, oom);
                            if (thisDistance.compareTo(distance) == -1) {
                                double value0 = grid0.getCell(x1, y1);
                                double value1 = grid1.getCell(x1, y1);
                                if (value0 != grid0NoDataValue) {
                                    max0 = Math.max(max0, value0);
                                    min0 = Math.min(min0, value0);
                                }
                                if (value1 != grid1NoDataValue) {
                                    max1 = Math.max(max1, value1);
                                    min1 = Math.min(min1, value1);
                                }
                                if (value0 != grid0NoDataValue && value1 != grid1NoDataValue) {
                                    n++;
                                    Math_BigRational weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance, oom);
                                    sumWeight = sumWeight.add(weight);
                                    Math_BigRational diff2 = Math_BigRational.valueOf(value0).subtract(Math_BigRational.valueOf(value1));
                                    weightedDiff = weightedDiff.add(diff2.multiply(weight));
                                    diff = diff.add(diff2);
                                }
                            }
                        }
                    }
                    if (n > setNumberOfPairs) {
                        if (max0 != Double.MIN_VALUE && min0 != Double.MAX_VALUE && max1 != Double.MIN_VALUE && min1 != Double.MAX_VALUE) {
                            double range0 = max0 - min0;
                            double range1 = max1 - min1;
                            for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                                for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                                    Math_BigRational x1 = grid0.getCellX(col + q);
                                    Math_BigRational y1 = grid0.getCellY(row + p);
                                    Math_BigRationalSqrt thisDistance = Grids_Utilities.distance(x0, y0, x1, y1, oom);
                                    if (thisDistance.compareTo(distance) == -1) {
                                        double v0 = grid0.getCell(x1, y1);
                                        double v1 = grid1.getCell(x1, y1);
                                        if (v0 != grid0NoDataValue && v1 != grid1NoDataValue) {
                                            Math_BigRational weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance, oom);
                                            double dummy0;
                                            if (range0 > 0.0d) {
                                                dummy0 = (((v0 - min0) / range0) * 9.0d) + 1.0d;
                                            } else {
                                                dummy0 = 1.0d;
                                            }
                                            double dummy1;
                                            if (range1 > 0.0d) {
                                                dummy1 = (((v1 - min1) / range1) * 9.0d) + 1.0d;
                                            } else {
                                                dummy1 = 1.0d;
                                            }
                                            Math_BigRational ddiff = Math_BigRational.valueOf(dummy0 - dummy1);
                                            normalisedDiff = normalisedDiff.add(ddiff);
                                            weightedNormalisedDiff = weightedNormalisedDiff.add(ddiff.multiply(weight));
                                        }
                                    }
                                }
                            }
                        }
                        diffGrid.setCell(row, col, diff.doubleValue());
                        weightedDiffGrid.setCell(row, col, weightedDiff.multiply(sumWeight).doubleValue() / totalSumWeight.doubleValue());
                        normalisedDiffGrid.setCell(row, col, normalisedDiff.doubleValue());
                        weightedNormalisedDiffGrid.setCell(row, col, (weightedNormalisedDiff.multiply(sumWeight)).doubleValue() / totalSumWeight.doubleValue());
                    }
                }
            }
        }

        // Correlation and Zscore difference
        // temporarily fix range
        if (docorr || dozdiff) {
            gf.setNoDataValue(grid0NoDataValue);
            weightedCorrelationGrid = (Grids_GridDouble) gf.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            correlationGrid = (Grids_GridDouble) gf.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            weightedZdiffGrid = (Grids_GridDouble) gf.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            zdiffGrid = (Grids_GridDouble) gf.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            // setNumberOfPairs defines how many cells are needed to calculate correlation
            double dummy0 = Double.MIN_VALUE;
            double dummy1 = Double.MIN_VALUE;
            long row;
            long col;
            for (row = 0; row < grid0Nrows; row++) {
                for (col = 0; col < grid0Ncols; col++) {
                    //if ( grid0.getCell( row, col ) != grid0NoDataValue ) {
                    Math_BigRational x0 = grid0.getCellX(col);
                    Math_BigRational y0 = grid0.getCellY(row);
                    double max0 = Double.MIN_VALUE;
                    double max1 = Double.MIN_VALUE;
                    double min0 = Double.MAX_VALUE;
                    double min1 = Double.MAX_VALUE;
                    Math_BigRational sumWeight0 = Math_BigRational.ZERO;
                    Math_BigRational sumWeight1 = Math_BigRational.ZERO;
                    Math_BigRational weightedMean0 = Math_BigRational.ZERO;
                    Math_BigRational weightedMean1 = Math_BigRational.ZERO;
                    Math_BigRational weightedSum0Squared = Math_BigRational.ZERO;
                    Math_BigRational weightedSum1Squared = Math_BigRational.ZERO;
                    Math_BigRational weightedSum01 = Math_BigRational.ZERO;
                    Math_BigRational weightedStandardDeviation0 = Math_BigRational.ZERO;
                    Math_BigRational weightedStandardDeviation1 = Math_BigRational.ZERO;
                    Math_BigRational weightedZdiff = Math_BigRational.ZERO;
                    Math_BigRational mean0 = Math_BigRational.ZERO;
                    Math_BigRational mean1 = Math_BigRational.ZERO;
                    Math_BigRational sum0Squared = Math_BigRational.ZERO;
                    Math_BigRational sum1Squared = Math_BigRational.ZERO;
                    Math_BigRational sum01 = Math_BigRational.ZERO;
                    Math_BigRational standardDeviation0 = Math_BigRational.ZERO;
                    Math_BigRational standardDeviation1 = Math_BigRational.ZERO;
                    Math_BigRational zdiff = Math_BigRational.ZERO;
                    n = 0;
                    double n0 = 0.0d;
                    double n1 = 0.0d;
                    // Calculate max min range sumWeight
                    for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                        for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                            Math_BigRational x1 = grid0.getCellX(col + q);
                            Math_BigRational y1 = grid0.getCellY(row + p);
                            Math_BigRationalSqrt thisDistance = Grids_Utilities.distance(x0, y0, x1, y1, oom);
                            if (thisDistance.compareTo(distance) == -1) {
                                Math_BigRational weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance, oom);
                                double v0 = grid0.getCell(x1, y1);
                                double v1 = grid1.getCell(x1, y1);
                                if (v0 != grid0NoDataValue) {
                                    max0 = Math.max(max0, v0);
                                    min0 = Math.min(min0, v0);
                                    n0 += 1.0d;
                                    sumWeight0 = sumWeight0.add(weight);
                                }
                                if (v1 != grid1NoDataValue) {
                                    max1 = Math.max(max1, v1);
                                    min1 = Math.min(min1, v1);
                                    n1 += 1.0d;
                                    sumWeight1 = sumWeight1.add(weight);
                                }
                                if (v0 != grid0NoDataValue && v1 != grid1NoDataValue) {
                                    n++;
                                }
                            }
                        }
                    }
                    if (n > setNumberOfPairs) {
                        if (max0 != Double.MIN_VALUE && min0 != Double.MAX_VALUE && max1 != Double.MIN_VALUE && min1 != Double.MAX_VALUE) {
                            double range0 = max0 - min0;
                            double range1 = max1 - min1;
                            for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                                for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                                    Math_BigRational x1 = grid0.getCellX(col + q);
                                    Math_BigRational y1 = grid0.getCellY(row + p);
                                    Math_BigRationalSqrt thisDistance = Grids_Utilities.distance(x0, y0, x1, y1, oom);
                                    if (thisDistance.compareTo(distance) == -1) {
                                        Math_BigRational weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance, oom);
                                        double v0 = grid0.getCell(row + p, col + q);
                                        double v1 = grid1.getCell(row + p, col + q);
                                        if (v0 != grid0NoDataValue) {
                                            if (range0 > 0.0d) {
                                                dummy0 = (((v0 - min0) / range0) * 9.0d) + 1.0d;
                                            } else {
                                                dummy0 = 1.0d;
                                            }
                                            weightedMean0 = weightedMean0.add(Math_BigRational.valueOf(dummy0 / sumWeight0.doubleValue()).multiply(weight));
                                            mean0 = mean0.add(Math_BigRational.valueOf(dummy0 / n0));
                                        }
                                        if (v1 != grid1NoDataValue) {
                                            if (range1 > 0.0d) {
                                                dummy1 = (((v1 - min1) / range1) * 9.0d) + 1.0d;
                                            } else {
                                                dummy1 = 1.0d;
                                            }
                                            weightedMean1 = weightedMean1.add(Math_BigRational.valueOf(dummy1 / sumWeight1.doubleValue()).multiply(weight));
                                            mean1 = mean1.add(Math_BigRational.valueOf(dummy1 / n1));
                                        }
                                    }
                                }
                            }
                            for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                                for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                                    Math_BigRational x1 = grid0.getCellX(col + q);
                                    Math_BigRational y1 = grid0.getCellY(row + p);
                                    Math_BigRationalSqrt thisDistance = Grids_Utilities.distance(x0, y0, x1, y1, oom);
                                    if (thisDistance.compareTo(distance) == -1) {
                                        Math_BigRational weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance, oom);
                                        double v0 = grid0.getCell(x1, y1);
                                        if (v0 != grid0NoDataValue) {
                                            if (range0 > 0.0d) {
                                                dummy0 = (((v0 - min0) / range0) * 9.0d) + 1.0d;
                                            } else {
                                                dummy0 = 1.0d;
                                            }
                                            standardDeviation0 = standardDeviation0.add(Math_BigRational.valueOf(Math.pow((dummy0 - mean0.doubleValue()), 2.0d)));
                                            weightedStandardDeviation0 = weightedStandardDeviation0.add(Math_BigRational.valueOf(Math.pow((dummy0 - weightedMean0.doubleValue()), 2.0d)).multiply(weight));
                                        }
                                        double v1 = grid1.getCell(x1, y1);
                                        if (v1 != grid1NoDataValue) {
                                            if (range1 > 0.0d) {
                                                dummy1 = (((v1 - min1) / range1) * 9.0d) + 1.0d;
                                            } else {
                                                dummy1 = 1.0d;
                                            }
                                            standardDeviation1 = standardDeviation1.add(Math_BigRational.valueOf(Math.pow((dummy1 - mean1.doubleValue()), 2.0d)));
                                            weightedStandardDeviation1 = weightedStandardDeviation1.add(Math_BigRational.valueOf(Math.pow((dummy1 - weightedMean1.doubleValue()), 2.0d)).multiply(weight));
                                        }
                                        if (v0 != grid0NoDataValue && v1 != grid1NoDataValue) {
                                            //weightedSum0Squared += Math.pow( ( ( value0 * weight ) - weightedMean0 ), 2.0d );
                                            //weightedSum1Squared += Math.pow( ( ( value1 * weight ) - weightedMean1 ), 2.0d );
                                            //weightedSum01 += ( ( value0 * weight ) - weightedMean0 ) * ( ( value1 * weight ) - weightedMean1 );
                                            weightedSum0Squared = weightedSum0Squared.add(Math_BigRational.valueOf(Math.pow((dummy0 - weightedMean0.doubleValue()), 2.0d)).multiply(weight));
                                            weightedSum1Squared = weightedSum1Squared.add(Math_BigRational.valueOf(Math.pow((dummy1 - weightedMean1.doubleValue()), 2.0d)).multiply(weight));
                                            weightedSum01 = weightedSum01.add(Math_BigRational.valueOf((dummy0 - weightedMean0.doubleValue()) * (dummy1 - weightedMean1.doubleValue())).multiply(weight));
                                            sum0Squared = sum0Squared.add(Math_BigRational.valueOf(Math.pow((dummy0 - mean0.doubleValue()), 2.0d)));
                                            sum1Squared = sum1Squared.add(Math_BigRational.valueOf(Math.pow((dummy1 - mean1.doubleValue()), 2.0d)));
                                            sum01 = sum01.add(Math_BigRational.valueOf((dummy0 - mean0.doubleValue()) * (dummy1 - mean1.doubleValue())));
                                        }
                                    }
                                }
                            }
                            Math_BigRational denominator = new Math_BigRationalSqrt(weightedSum0Squared, oom).getSqrt(oom).multiply(new Math_BigRationalSqrt(weightedSum1Squared, oom).getSqrt(oom));
                            if (denominator.compareTo(Math_BigRational.ZERO) == 1 && denominator.doubleValue() != noDataValue) {
                                weightedCorrelationGrid.setCell(row, col, weightedSum01.doubleValue() / denominator.doubleValue());
                            }
                            denominator = new Math_BigRationalSqrt(sum0Squared, oom).getSqrt(oom).multiply(new Math_BigRationalSqrt(sum1Squared, oom).getSqrt(oom));
                            if (denominator.compareTo(Math_BigRational.ZERO) == 1 && denominator.doubleValue() != noDataValue) {
                                correlationGrid.setCell(row, col, sum01.doubleValue() / denominator.doubleValue());
                            }
                            weightedStandardDeviation0 = Math_BigRational.valueOf(Math.sqrt(weightedStandardDeviation0.doubleValue() / (n0 - 1.0d)));
                            standardDeviation0 = Math_BigRational.valueOf(Math.sqrt(standardDeviation0.doubleValue() / (n0 - 1.0d)));
                            weightedStandardDeviation1 = Math_BigRational.valueOf(Math.sqrt(weightedStandardDeviation1.doubleValue() / (n1 - 1.0d)));
                            standardDeviation1 = Math_BigRational.valueOf(Math.sqrt(standardDeviation1.doubleValue() / (n1 - 1.0d)));
                            // Calculate z scores and difference
                            if (weightedStandardDeviation0.compareTo(Math_BigRational.ZERO) == 1 && weightedStandardDeviation1.compareTo(Math_BigRational.ZERO) == 1) {
                                for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                                    for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                                        Math_BigRational x1 = grid0.getCellX(col + q);
                                        Math_BigRational y1 = grid0.getCellY(row + p);
                                        Math_BigRationalSqrt thisDistance = Grids_Utilities.distance(x0, y0, x1, y1, oom);
                                        if (thisDistance.compareTo(distance) == -1) {
                                            double v0 = grid0.getCell(x1, y1);
                                            double v1 = grid1.getCell(x1, y1);
                                            if (v0 != grid0NoDataValue && v1 != grid1NoDataValue) {
                                                if (range0 > 0.0d) {
                                                    dummy0 = (((v0 - min0) / range0) * 9.0d) + 1.0d;
                                                } else {
                                                    dummy0 = 1.0d;
                                                }
                                                if (range1 > 0.0d) {
                                                    dummy1 = (((v1 - min1) / range1) * 9.0d) + 1.0d;
                                                } else {
                                                    dummy1 = 1.0d;
                                                }
                                                Math_BigRational weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance, oom);
                                                //weightedZdiff += ( ( ( ( value0 * weight ) - weightedMean0 ) / weightedStandardDeviation0 ) - ( ( ( value1 * weight ) - weightedMean1 ) / weightedStandardDeviation1 ) );
                                                weightedZdiff = weightedZdiff.add(Math_BigRational.valueOf(((((dummy0 - weightedMean0.doubleValue()) / weightedStandardDeviation0.doubleValue())
                                                        - ((dummy1 - weightedMean1.doubleValue()) / weightedStandardDeviation1.doubleValue())) * weight.doubleValue())));
//                                                weightedZdiff += (((dummy0 - weightedMean0) / weightedStandardDeviation0)
//                                                        - ((dummy1 - weightedMean1) / weightedStandardDeviation1)) * weight;
                                            }
                                        }
                                    }
                                }
                                weightedZdiffGrid.setCell(row, col, weightedZdiff.doubleValue());
                            }
                            if (standardDeviation0.doubleValue() > 0.0d && standardDeviation1.doubleValue() > 0.0d) {
                                for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                                    for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                                        Math_BigRational x1 = grid0.getCellX(col + q);
                                        Math_BigRational y1 = grid0.getCellY(row + p);
                                        Math_BigRationalSqrt thisDistance = Grids_Utilities.distance(x0, y0, x1, y1, oom);
                                        if (thisDistance.compareTo(distance) == -1) {
                                            double v0 = grid0.getCell(x1, y1);
                                            double v1 = grid1.getCell(x1, y1);
                                            if (v0 != grid0NoDataValue && v1 != grid1NoDataValue) {
                                                if (range0 > 0.0d) {
                                                    dummy0 = (((v0 - min0) / range0) * 9.0d) + 1.0d;
                                                } else {
                                                    dummy0 = 1.0d;
                                                }
                                                if (range1 > 0.0d) {
                                                    dummy1 = (((v1 - min1) / range1) * 9.0d) + 1.0d;
                                                } else {
                                                    dummy1 = 1.0d;
                                                }
                                                zdiff = zdiff.add(Math_BigRational.valueOf((((dummy0 - mean0.doubleValue()) / standardDeviation0.doubleValue()) - ((dummy1 - mean1.doubleValue()) / standardDeviation1.doubleValue()))));
                                                //zdiff += (((dummy0 - mean0) / standardDeviation0) - ((dummy1 - mean1) / standardDeviation1));
                                            }
                                        }
                                    }
                                }
                                zdiffGrid.setCell(row, col, zdiff.doubleValue());
                            }
                        }
                    }
                }
            }
        }
        allStatistics = 0;
        if (dodiff) {
            diffGrid.setName(grid0.getName() + "_Diff_" + grid1.getName());
            result[allStatistics] = diffGrid;
            allStatistics++;
            weightedDiffGrid.setName(grid0.getName() + "_WDiff_" + grid1.getName());
            result[allStatistics] = weightedDiffGrid;
            allStatistics++;
            normalisedDiffGrid.setName(grid0.getName() + "_NDiff_" + grid1.getName());
            result[allStatistics] = normalisedDiffGrid;
            allStatistics++;
            weightedNormalisedDiffGrid.setName(grid0.getName() + "_NWDiff_" + grid1.getName());
            result[allStatistics] = weightedNormalisedDiffGrid;
            allStatistics++;
        }
        if (docorr) {
            weightedCorrelationGrid.setName(grid0.getName() + "_WCorr_" + grid1.getName());
            result[allStatistics] = weightedCorrelationGrid;
            allStatistics++;
            correlationGrid.setName(grid0.getName() + "_Corr_" + grid1.getName());
            result[allStatistics] = correlationGrid;
            allStatistics++;
        }
        if (dozdiff) {
            weightedZdiffGrid.setName(grid0.getName() + "_WZDiff_" + grid1.getName());
            result[allStatistics] = weightedZdiffGrid;
            allStatistics++;
            zdiffGrid.setName(grid0.getName() + "_ZDiff_" + grid1.getName());
            result[allStatistics] = zdiffGrid;
            allStatistics++;
        }

        return result;
    }
}
