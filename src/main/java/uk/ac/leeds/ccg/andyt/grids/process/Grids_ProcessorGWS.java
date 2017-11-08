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
package uk.ac.leeds.ccg.andyt.grids.process;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Kernel;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

/**
 * Class of methods for processing and generating geographically weighted
 Grids_GridDouble statistics.
 */
public class Grids_ProcessorGWS extends Grids_Processor {

    /**
     * Creates a new Grids_ProcessorGWS
     */
    protected Grids_ProcessorGWS() {}

    /*
     * Creates a new instance of Grids_ProcessorGWS.
     *
     */
    public Grids_ProcessorGWS(Grids_Environment ge) {
        super(ge);
    }

    //    /**
    //     * Returns an Grids_GridDouble[] result with elements based on
    //     * statistics, and values based on grid, distance, weightIntersect and
    //     * weightFactor.
    //     * @param grid the Grids_GridDouble to be processed
    //     * @param distance the distance defining the region within which values will
    //     *   be used
    //     * @param weightIntersect typically a number between 0 and 1 which controls
    //     *   the weight applied at the centre of the kernel
    //     * @param weightFactor
    //     *   = 0.0d all values within distance will be equally weighted
    //     *   > 0.0d means the edge of the kernel has a zero weight
    //     *   < 0.0d means that the edage of the kernel has a weight of 1
    //     *   > -1.0d && < 1.0d provides an inverse decay
    //     */
    //    public Vector regionUnivariateStatistics( Grids_GridDouble grid, Vector statistics, double distance, double weightIntersect, double weightFactor ) {
    //        try {
    //            return regionUnivariateStatistics( grid, statistics, distance, weightIntersect, weightFactor, new Grids_GridDoubleFactory() );
    //        } catch ( java.lang.OutOfMemoryError e ) {
    //            return regionUnivariateStatistics( grid, statistics, distance, weightIntersect, weightFactor, new Grid2DSquareCellDoubleFileFactory() );
    //        }
    //    }
    /**
     * Returns a Vector containing Grid2DSquareCellDoubles. Implements row
     * processing (see Grid2DSquareCellDoubleProcessor.getRowProcessData()).
     *
     * @param grid the Grids_GridDouble to be processed
     * @param statistics
     * @param distance the distance defining the region within which values will
     * be used. At distances weights if applied are zero
     * @param weightIntersect typically a number between 0 and 1 which controls
     * the weight applied at the centre of the kernel
     * @param weightFactor = 0.0d all values within distance will be equally
     * weighted > 0.0d means the edge of the kernel has a zero weight < 0.0d
     * means that the edge of the kernel has a weight of 1 > -1.0d && < 1.0d
     * provides an inverse decay
     * @param gridFactory the Abstract2DSquareCellDoubleFactory used to create
     * grids
     * @return 
     */
    public List<Grids_AbstractGridNumber> regionUnivariateStatistics(
            Grids_GridDouble grid,
            //Vector statistics,
            List<String> statistics,
            double distance,
            double weightIntersect,
            double weightFactor,
            Grids_GridDoubleFactory gridFactory) {
        boolean handleOutOfMemoryError = true;

        List<Grids_AbstractGridNumber> result = new ArrayList<>();
        //Vector result = new Vector();

        long ncols = grid.getNCols(handleOutOfMemoryError);
        long nrows = grid.getNRows(handleOutOfMemoryError);
        Grids_Dimensions dimensions = grid.getDimensions(handleOutOfMemoryError);
        double noDataValue = grid.getNoDataValue(handleOutOfMemoryError);
        int cellDistance = (int) Math.ceil(distance / grid.getCellsizeDouble(handleOutOfMemoryError));
        
        // @HACK If cellDistance is so great that data for a single kernel is
        // unlikely to fit in memory
        if (cellDistance > 1024) {
            return regionUnivariateStatisticsSlow(
                    grid,
                    statistics,
                    distance,
                    weightIntersect,
                    weightFactor,
                    gridFactory);
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

            if (((String) statistics.get(i)).equalsIgnoreCase("Sum")) {
                doSum = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WSum")) {
                doWSum = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("NWSum")) {
                doNWSum = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WSumN")) {
                doWSumN = true;
            }

            if (((String) statistics.get(i)).equalsIgnoreCase("Mean")) {
                doMean = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WMean1")) {
                doWMean1 = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WMean2")) {
                doWMean2 = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("NWMean")) {
                doNWMean = true;
            }
            if (((String) statistics.get(i)).equalsIgnoreCase("WMeanN")) {
                doWMeanN = true;
            }

            if (((String) statistics.get(i)).equalsIgnoreCase("SecondOrder")) {
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
            if (((String) statistics.get(i)).equalsIgnoreCase("WeightedSecondOrder")) {
                doNWMean = true;
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
            //      if ( ( ( String ) statistics.elementAt( i ) ).equalsIgnoreCase( "WVar" ) ) { doWVar = true; doWMean = true;  }
            if (((String) statistics.get(i)).equalsIgnoreCase("Skew")) {
                doSkew = true;
                doMean = true;
            }
            //    if ( ( ( String ) statistics.elementAt( i ) ).equalsIgnoreCase( "WSkew" ) ) { doWSkew = true; doWMean = true;  }
            if (((String) statistics.get(i)).equalsIgnoreCase("CVar")) {
                doCVar = true;
                doVar = true;
                doMean = true;
            }
            //     if ( ( ( String ) statistics.elementAt( i ) ).equalsIgnoreCase( "WCVar" ) ) { doWCVar = true; doWVar = true; doWMean = true;  }
            if (((String) statistics.get(i)).equalsIgnoreCase("CSkew")) {
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

        double cellX;
        double cellY;
        double thisDistance;
        double thisCellX;
        double thisCellY;
        double value;

        gridFactory.setNoDataValue(noDataValue);

        // First order stats ( Mean WMean Sum WSum  Density WDensity )
        if (doSum || doWSum || doNWSum || doWSumN || doMean || doWMean1 || doWMean2 || doNWMean || doWMeanN) {

            sumWeightGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);

            if (doSum) {
                sumGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWSum) {
                wSumGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doNWSum) {
                nWSumGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWSumN) {
                wSumNGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }

            if (doMean) {
                meanGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWMean1) {
                wMean1Grid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWMean2) {
                wMean2Grid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doNWMean) {
                nWMeanGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWMeanN) {
                wMeanNGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }

            double[] kernelParameters = Grids_Kernel.getKernelParameters(grid, cellDistance, distance, weightIntersect, weightFactor);
            double totalSumWeight = kernelParameters[ 0];
            double totalCells = kernelParameters[ 1];
            double weight;
            double sumWeight;
            double sumCells;

            double sum;
            double wSum;
            double nWSum;
            double wSumN;

            double wMean;
            double nWMean;
            //double wMeanN;
            long row;
            long col;
            int p;
            int q;
            double[][] kernel = Grids_Kernel.getKernelWeights(grid, distance, weightIntersect, weightFactor);
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
                        data = getRowProcessData(
                                grid,
                                data,
                                cellDistance,
                                row,
                                col);
                    }
                    sumCells = 0.0d;
                    sumWeight = 0.0d;

                    sum = 0.0d;
                    wSum = 0.0d;
                    nWSum = 0.0d;
                    wSumN = 0.0d;

                    wMean = 0.0d;
                    nWMean = 0.0d;
                    //wMeanN = 0.0d;

                    
                    // Error thrown from here!
                    // GC overhead limit exceeded
                    // java.lang.OutOfMemoryError: GC overhead limit exceeded
                    // There is probably a better doing way?
                    cellX = grid.getCellXDouble(
                            col,
                            ge.HandleOutOfMemoryErrorFalse);
                    cellY = grid.getCellYDouble(
                            row,
                            ge.HandleOutOfMemoryErrorFalse);
                    // Calculate sumWeights and non-weighted stats
                    for (p = 0; p <= cellDistance * 2; p++) {
                        for (q = 0; q <= cellDistance * 2; q++) {
                            value = data[ p][ q];
                            weight = kernel[ p][ q];
                            if (weight != noDataValue
                                    && value != noDataValue) {
                                sumWeight += weight;
                                sumCells += 1.0d;
                                sum += value;
                            }
                        }
                    }
                    // Calculate weighted stats and store results
                    if (sumCells > 0.0d && sumWeight > 0.0d) {
                        for (p = 0; p <= cellDistance * 2; p++) {
                            for (q = 0; q <= cellDistance * 2; q++) {
                                value = data[ p][ q];
                                weight = kernel[ p][ q];
                                if (weight != noDataValue
                                        && value != noDataValue) {
                                    sumWeight += weight;
                                    sumCells += 1.0d;
                                    sum += value;
                                    nWSum += value
                                            * (sumWeight / totalSumWeight)
                                            * weight;
                                    wSum += value * weight;
                                    wMean += (value / sumWeight) * weight;
                                }
                            }
                        }

                        sumWeightGrid.setCell(
                                row, col,
                                sumWeight / totalSumWeight,
                                ge.HandleOutOfMemoryErrorFalse);

                        //if ( doSum ) { sumGrid.setCell( row, col, sum ); }
                        if (doSum) {
                            sumGrid.setCell(
                                    row, col, 
                                    sum * sumCells / totalCells, 
                                    ge.HandleOutOfMemoryErrorFalse);
                        }
                        if (doWSum) {
                            wSumGrid.setCell(
                                    row, col,
                                    wSum, 
                                    ge.HandleOutOfMemoryErrorFalse);
                        }
                        if (doNWSum) {
                            nWSumGrid.setCell(
                                    row, col,
                                    nWSum,
                                    ge.HandleOutOfMemoryErrorFalse);
                        }
                        if (doWSumN) {
                            wSumNGrid.setCell(
                                    row, col, 
                                    wSum * sumWeight / totalSumWeight,
                                    ge.HandleOutOfMemoryErrorFalse);
                        }

                        if (doMean) {
                            meanGrid.setCell(
                                    row, col, 
                                    sum / sumCells,
                                    ge.HandleOutOfMemoryErrorFalse);
                        }
                        if (doWMean1) {
                            wMean1Grid.setCell(
                                    row, col, 
                                    wSum / sumWeight,
                                    ge.HandleOutOfMemoryErrorFalse);
                        }
                        if (doWMean2) {
                            wMean2Grid.setCell(
                                    row, col, 
                                    wMean,
                                    ge.HandleOutOfMemoryErrorFalse);
                        }
                        if (doNWMean) {
                            nWMeanGrid.setCell(
                                    row, col, 
                                    nWSum / sumWeight,
                                    ge.HandleOutOfMemoryErrorFalse);
                        }
                        if (doWMeanN) {
                            wMeanNGrid.setCell(
                                    row, col, 
                                    wMean * sumWeight / totalSumWeight,
                                    ge.HandleOutOfMemoryErrorFalse);
                        }

                    }
                }
            }
        }

        // Second order statistics ( coefficient of variation, skewness, kurtosis, zscore)
        if (doProp || doWProp || doVar || doWVar || doSkew || doWSkew || doWCVar || doCSkew || doWCSkew) {

            if (doProp) {
                propGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWProp) {
                wPropGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doVar) {
                varGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWVar) {
                wVarGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doSkew) {
                skewGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWSkew) {
                wSkewGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doCVar) {
                cVarGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWCVar) {
                wCVarGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doCSkew) {
                cSkewGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWCSkew) {
                wCSkewGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }

            double[] kernelParameters = Grids_Kernel.getKernelParameters(grid, cellDistance, distance, weightIntersect, weightFactor);
            double totalSumWeight = kernelParameters[ 0];
            double totalCells = kernelParameters[ 1];
            double weight;
            double sumWeight;
            double wMean = 0.0d;
            double sDWMean;
            double sDMean;
            double sDWMeanPow2;
            double sDWMeanPow3;
            double sDWMeanPow4;
            //double sumCells;
            //double mean = 0.0d;
            //double sDMeanPow2;
            //double sDMeanPow3;
            //double sDMeanPow4;
            double numerator;
            double denominator;
            long row;
            long col;
            int p;
            int q;
            double[][] kernel = Grids_Kernel.getKernelWeights(grid, distance, weightIntersect, weightFactor);
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
                    sDWMean = 0.0d;
                    sDWMeanPow2 = 0.0d;
                    sDWMeanPow3 = 0.0d;
                    sDWMeanPow4 = 0.0d;
                    sumWeight = 0.0d;
                    cellX = grid.getCellXDouble(
                            col, 
                            ge.HandleOutOfMemoryErrorFalse);
                    cellY = grid.getCellYDouble(
                            row, 
                            ge.HandleOutOfMemoryErrorFalse);
                    // Take moments
                    for (p = 0; p <= cellDistance * 2; p++) {
                        for (q = 0; q <= cellDistance * 2; q++) {
                            value = data[ p][ q];
                            wMean = wMeanData[ p][ q];
                            weight = kernel[ p][ q];
                            if (value != noDataValue && weight != noDataValue) {
                                sumWeight += weight;
                                sDWMean += (value - wMean) * weight;
                                sDWMeanPow2 += Math.pow((value - wMean), 2.0d) * weight;
                                sDWMeanPow3 += Math.pow((value - wMean), 3.0d) * weight;
                                sDWMeanPow4 += Math.pow((value - wMean), 4.0d) * weight;
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
                    if (sumWeight > 0.0d) {
                        //if ( doProp ) {
                        //    propGrid.setCell( row, col, ( sDMean / sumCells ) );
                        //}
                        if (doWProp) {
                            wPropGrid.setCell(
                                    row, col, 
                                    sDWMean / sumWeight,
                                    ge.HandleOutOfMemoryErrorFalse);
                        }
                        //if ( doVar ) {
                        //    varGrid.setCell( row, col, ( sDMeanPow2 / sumCells ) );
                        //}
                        if (doWVar) {
                            wVarGrid.setCell(
                                    row, col,
                                    sDWMeanPow2 / sumWeight,
                                    ge.HandleOutOfMemoryErrorFalse);
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
                            numerator = sDWMeanPow3 / sumWeight;
                            if (numerator > 0.0d) {
                                wSkewGrid.setCell(
                                        row, col,
                                        (Math.pow(numerator, 1.0d / 3.0d)),
                                        ge.HandleOutOfMemoryErrorFalse);
                            }
                            if (numerator == 0.0d) {
                                wSkewGrid.setCell(
                                        row, col, 
                                        numerator, 
                                        ge.HandleOutOfMemoryErrorFalse);
                            }
                            if (numerator < 0.0d) {
                                wSkewGrid.setCell(
                                        row, col, 
                                        -1.0d * (Math.pow(Math.abs(numerator), 1.0d / 3.0d)),
                                        ge.HandleOutOfMemoryErrorFalse);
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
                            denominator = wVarGrid.getCell(
                                    row, col, 
                                    ge.HandleOutOfMemoryErrorFalse);
                            if (denominator > 0.0d && denominator != noDataValue) {
                                numerator = wPropGrid.getCell(
                                        row, col, 
                                        ge.HandleOutOfMemoryErrorFalse);
                                if (numerator != noDataValue) {
                                    wCVarGrid.setCell(
                                            row, col, 
                                            (numerator / denominator), 
                                            ge.HandleOutOfMemoryErrorFalse);
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
                            denominator = wVarGrid.getCell(
                                    row, col, 
                                    ge.HandleOutOfMemoryErrorFalse);
                            if (denominator > 0.0d && denominator != noDataValue) {
                                numerator = sDWMeanPow3 / sumWeight;
                                if (numerator > 0.0d) {
                                    wCSkewGrid.setCell(
                                            row, col,
                                            (Math.pow(numerator, 1.0d / 3.0d)) / denominator,
                                            ge.HandleOutOfMemoryErrorFalse);
                                }
                                if (numerator == 0.0d) {
                                    wCSkewGrid.setCell(
                                            row, col, 
                                            numerator,
                                            ge.HandleOutOfMemoryErrorFalse);
                                }
                                if (numerator < 0.0d) {
                                    wCSkewGrid.setCell(
                                            row, col,
                                            (-1.0d * (Math.pow(Math.abs(numerator), 1.0d / 3.0d))) / denominator, 
                                            ge.HandleOutOfMemoryErrorFalse);
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

        sumWeightGrid.setName(
                "SumWeight_" + grid.getName(handleOutOfMemoryError),
                ge.HandleOutOfMemoryErrorFalse);
        result.add(sumWeightGrid);

        if (doSum) {
            sumGrid.setName(
                    "Sum_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(sumGrid);
        }
        if (doWSum) {
            wSumGrid.setName(
                    "WSum_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(wSumGrid);
        }
        if (doNWSum) {
            nWSumGrid.setName(
                    "NWSum_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(nWSumGrid);
        }
        if (doWSumN) {
            wSumNGrid.setName(
                    "WSumN_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(wSumNGrid);
        }

        if (doMean) {
            meanGrid.setName(
                    "Mean_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(meanGrid);
        }
        if (doWMean1) {
            wMean1Grid.setName(
                    "WMean1_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(wMean1Grid);
        }
        if (doWMean2) {
            wMean2Grid.setName(
                    "WMean2_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(wMean2Grid);
        }
        if (doNWMean) {
            nWMeanGrid.setName(
                    "NWMean_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(nWMeanGrid);
        }
        if (doWMeanN) {
            wMeanNGrid.setName(
                    "WMeanN_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(wMeanNGrid);
        }

        if (doProp) {
            propGrid.setName(
                    "Prop_" + grid.getName(handleOutOfMemoryError), 
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(propGrid);
        }
        if (doWProp) {
            wPropGrid.setName(
                    "WProp_" + grid.getName(handleOutOfMemoryError), 
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(wPropGrid);
        }
        if (doVar) {
            varGrid.setName(
                    "Var_" + grid.getName(handleOutOfMemoryError), 
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(varGrid);
        }
        if (doWVar) {
            wVarGrid.setName(
                    "WVar_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(wVarGrid);
        }
        if (doSkew) {
            skewGrid.setName(
                    "Skew_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(skewGrid);
        }
        if (doWSkew) {
            wSkewGrid.setName(
                    "WSkew_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(wSkewGrid);
        }
        if (doCVar) {
            cVarGrid.setName(
                    "CVar_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(cVarGrid);
        }
        if (doWCVar) {
            wCVarGrid.setName(
                    "WCVar_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(wCVarGrid);
        }
        if (doCSkew) {
            cSkewGrid.setName(
                    "CSkew" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(cSkewGrid);
        }
        if (doWCSkew) {
            wCSkewGrid.setName(
                    "WCSkew_" + grid.getName(handleOutOfMemoryError),
                    ge.HandleOutOfMemoryErrorFalse);
            result.add(wCSkewGrid);
        }

        return result;
    }

    /**
     * Returns a Vector containing Grid2DSquareCellDoubles
     *
     * @param grid the Grids_GridDouble to be processed
     * @param statistics
     * @param distance the distance defining the region within which values will
     * be used. At distances weights if applied are zero
     * @param weightIntersect typically a number between 0 and 1 which controls
     * the weight applied at the centre of the kernel
     * @param weightFactor:
     * <code>weightFactor = 0.0d</code> all values within distance will be
     * equally weighted;
     * <code>weightFactor > 0.0d</code> means the edge of the kernel has a zero
     * weight;
     * <code>weightFactor < 0.0d</code> means that the edage of the kernel has a
     * weight of 1;
     * <code>weightFactor > -1.0d && < 1.0d</code> provides an inverse decay.
     * @param gridFactory the Abstract2DSquareCellDoubleFactory used to create
     * grids
     * @return 
     */
    // public Vector regionUnivariateStatisticsSlow(
    public List<Grids_AbstractGridNumber> regionUnivariateStatisticsSlow(
            Grids_GridDouble grid,
            //Vector statistics,
            List<String> statistics,
            double distance,
            double weightIntersect,
            double weightFactor,
            Grids_GridDoubleFactory gridFactory) {
        boolean handleOutOfMemoryError = true;
        List<Grids_AbstractGridNumber> result = new ArrayList<>();
        //        Vector result = new Vector();
        long ncols = grid.getNCols(handleOutOfMemoryError);
        long nrows = grid.getNRows(handleOutOfMemoryError);
        Grids_Dimensions dimensions = grid.getDimensions(handleOutOfMemoryError);
        //double cellsize = dimensions[0].doubleValue();
        double noDataValue = grid.getNoDataValue(handleOutOfMemoryError);
        int cellDistance = (int) Math.ceil(distance / grid.getCellsizeDouble(handleOutOfMemoryError));
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
        double cellX;
        double cellY;
        double thisDistance;
        double thisCellX;
        double thisCellY;
        double value;
        gridFactory.setNoDataValue(noDataValue);
        // First order stats ( Mean WMean Sum WSum  Density WDensity )
        if (doMean || doWMean || doSum || doWSum) {
            if (doMean) {
                meanGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWMean) {
                wMeanGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doSum) {
                sumGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWSum) {
                wSumGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            double[] kernelParameters = Grids_Kernel.getKernelParameters(grid, cellDistance, distance, weightIntersect, weightFactor);
            double totalSumWeight = kernelParameters[ 0];
            double totalCells = kernelParameters[ 1];
            double weight;
            double sumWeight;
            double wMean;
            double mean;
            double sumCells;
            double wSum;
            double sum;
            for (row = 0; row < nrows; row++) {

                //debug
                System.out.println("processing row " + row + " out of " + nrows);


                for (col = 0; col < ncols; col++) {
                    sumWeight = 0.0d;
                    wMean = 0.0d;
                    sumCells = 0.0d;
                    wSum = 0.0d;
                    sum = 0.0d;
                    cellX = grid.getCellXDouble(col, ge.HandleOutOfMemoryErrorFalse);
                    cellY = grid.getCellYDouble(row, ge.HandleOutOfMemoryErrorFalse);
                    // Calculate sumWeights and non-weighted stats
                    for (int p = -cellDistance; p <= cellDistance; p++) {
                        for (int q = -cellDistance; q <= cellDistance; q++) {
                            value = grid.getCell(row + p, col + q, ge.HandleOutOfMemoryErrorFalse);
                            if (value != noDataValue) {
                                thisCellX = grid.getCellXDouble(col + q, ge.HandleOutOfMemoryErrorFalse);
                                thisCellY = grid.getCellYDouble(row + p, ge.HandleOutOfMemoryErrorFalse);
                                thisDistance = Grids_Utilities.distance(cellX, cellY, thisCellX, thisCellY);
                                if (thisDistance < distance) {
                                    sumWeight += Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance);
                                    sumCells += 1.0d;
                                    sum += value;
                                }
                            }
                        }
                    }
                    //sumWeightGrid.setCell( i, j, sumWeight );
                    //sumCellGrid.setCell( i, j, sumCells );
                    // Calculate weighted stats and store results
                    if (sumCells > 0.0d && sumWeight > 0.0d) {
                        for (int p = -cellDistance; p <= cellDistance; p++) {
                            for (int q = -cellDistance; q <= cellDistance; q++) {
                                value = grid.getCell(row + p, col + q, ge.HandleOutOfMemoryErrorFalse);
                                if (value != noDataValue) {
                                    thisCellX = grid.getCellXDouble(col + q, ge.HandleOutOfMemoryErrorFalse);
                                    thisCellY = grid.getCellYDouble(row + p, ge.HandleOutOfMemoryErrorFalse);
                                    thisDistance = Grids_Utilities.distance(cellX, cellY, thisCellX, thisCellY);
                                    if (thisDistance < distance) {
                                        weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance);
                                        //wMean += ( value / sumWeight ) * weight;
                                        //wMean += ( value / sumCells ) * weight;
                                        wSum += value * weight;
                                    }
                                }
                            }
                        }
                        if (doMean) {
                            meanGrid.setCell(row, col, sum / sumCells, ge.HandleOutOfMemoryErrorFalse);
                        }
                        if (doWMean) {
                            wMeanGrid.setCell(row, col, wSum / sumWeight, ge.HandleOutOfMemoryErrorFalse);
                        }
                        //if ( doSum ) { sumGrid.setCell( row, col, sum ); }
                        //if ( doWSum ) { wSumGrid.setCell( row, col, wSum ); }
                        if (doSum) {
                            sumGrid.setCell(row, col, sum * sumCells / totalCells, ge.HandleOutOfMemoryErrorFalse);
                        }
                        if (doWSum) {
                            wSumGrid.setCell(row, col, wSum * sumWeight / totalSumWeight, ge.HandleOutOfMemoryErrorFalse);
                        }
                    }
                }
            }
        }

        // Second order statistics ( coefficient of variation, skewness, kurtosis, zscore)
        if (doProp || doWProp || doVar || doWVar || doSkew || doWSkew || doWCVar || doCSkew || doWCSkew) {

            if (doProp) {
                propGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWProp) {
                wPropGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doVar) {
                varGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWVar) {
                wVarGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doSkew) {
                skewGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWSkew) {
                wSkewGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doCVar) {
                cVarGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWCVar) {
                wCVarGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doCSkew) {
                cSkewGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            if (doWCSkew) {
                wCSkewGrid = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }

            double[] kernelParameters = Grids_Kernel.getKernelParameters(grid, cellDistance, distance, weightIntersect, weightFactor);
            double totalSumWeight = kernelParameters[ 0];
            double totalCells = kernelParameters[ 1];
            double weight;
            double sumWeight;
            double sDWMean;
            double sDMean;
            double sDWMeanPow2;
            double sDWMeanPow3;
            double sDWMeanPow4;
            double sumCells;
            double wMean = 0.0d;
            double mean = 0.0d;
            double sDMeanPow2;
            double sDMeanPow3;
            double sDMeanPow4;
            double numerator;
            double denominator;

            for (row = 0; row < nrows; row++) {


                //debug
                System.out.println("processing row " + row + " out of " + nrows);


                for (col = 0; col < ncols; col++) {
                    sDMean = 0.0d;
                    sDMeanPow2 = 0.0d;
                    sDMeanPow3 = 0.0d;
                    sDMeanPow4 = 0.0d;
                    sumCells = 0.0d;
                    sDWMean = 0.0d;
                    sDWMeanPow2 = 0.0d;
                    sDWMeanPow3 = 0.0d;
                    sDWMeanPow4 = 0.0d;
                    sumWeight = 0.0d;
                    cellX = grid.getCellXDouble(col, ge.HandleOutOfMemoryErrorFalse);
                    cellY = grid.getCellYDouble(row, ge.HandleOutOfMemoryErrorFalse);
                    // Take moments
                    for (int p = -cellDistance; p <= cellDistance; p++) {
                        for (int q = -cellDistance; q <= cellDistance; q++) {
                            value = grid.getCell(row + p, col + q, ge.HandleOutOfMemoryErrorFalse);
                            if (value != noDataValue) {
                                thisCellX = grid.getCellXDouble(col + q, ge.HandleOutOfMemoryErrorFalse);
                                thisCellY = grid.getCellYDouble(row + p, ge.HandleOutOfMemoryErrorFalse);
                                thisDistance = Grids_Utilities.distance(cellX, cellY, thisCellX, thisCellY);
                                if (thisDistance < distance) {
                                    wMean = wMeanGrid.getCell(row + p, col + q, ge.HandleOutOfMemoryErrorFalse);
                                    weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance);
                                    sumWeight += weight;
                                    sDWMean += (value - wMean) * weight;
                                    sDWMeanPow2 += Math.pow((value - wMean), 2.0d) * weight;
                                    sDWMeanPow3 += Math.pow((value - wMean), 3.0d) * weight;
                                    sDWMeanPow4 += Math.pow((value - wMean), 4.0d) * weight;
                                    sumCells += 1.0d;
                                    if (doMean) {
                                        sDMean += (value - mean);
                                        sDMeanPow2 += Math.pow((value - mean), 2.0d);
                                        sDMeanPow3 += Math.pow((value - mean), 3.0d);
                                        sDMeanPow4 += Math.pow((value - mean), 4.0d);
                                    }
                                }
                            }
                        }
                    }
                    if (sumCells > 0.0d && sumWeight > 0.0d) {
                        if (doProp) {
                            propGrid.setCell(row, col, (sDMean / sumCells), ge.HandleOutOfMemoryErrorFalse);
                        }
                        if (doWProp) {
                            wPropGrid.setCell(row, col, (sDWMean / sumWeight), ge.HandleOutOfMemoryErrorFalse);
                        }
                        if (doVar) {
                            varGrid.setCell(row, col, (sDMeanPow2 / sumCells), ge.HandleOutOfMemoryErrorFalse);
                        }
                        if (doWVar) {
                            wVarGrid.setCell(row, col, (sDWMeanPow2 / sumWeight), ge.HandleOutOfMemoryErrorFalse);
                        }
                        if (doSkew) {
                            // Need to control for Math.pow as it does not do roots of negative numbers at all well!
                            numerator = sDMeanPow3 / sumCells;
                            if (numerator > 0.0d) {
                                skewGrid.setCell(row, col, (Math.pow(numerator, 1.0d / 3.0d)), ge.HandleOutOfMemoryErrorFalse);
                            }
                            if (numerator == 0.0d) {
                                skewGrid.setCell(row, col, numerator, ge.HandleOutOfMemoryErrorFalse);
                            }
                            if (numerator < 0.0d) {
                                skewGrid.setCell(row, col, -1.0d * (Math.pow(Math.abs(numerator), 1.0d / 3.0d)), ge.HandleOutOfMemoryErrorFalse);
                            }
                        }
                        if (doWSkew) {
                            // Need to control for Math.pow as it does not do roots of negative numbers at all well!
                            numerator = sDWMeanPow3 / sumWeight;
                            if (numerator > 0.0d) {
                                wSkewGrid.setCell(row, col, (Math.pow(numerator, 1.0d / 3.0d)), ge.HandleOutOfMemoryErrorFalse);
                            }
                            if (numerator == 0.0d) {
                                wSkewGrid.setCell(row, col, numerator, ge.HandleOutOfMemoryErrorFalse);
                            }
                            if (numerator < 0.0d) {
                                wSkewGrid.setCell(row, col, -1.0d * (Math.pow(Math.abs(numerator), 1.0d / 3.0d)), ge.HandleOutOfMemoryErrorFalse);
                            }
                        }
                        if (doCVar) {
                            denominator = varGrid.getCell(row, col, ge.HandleOutOfMemoryErrorFalse);
                            if (denominator > 0.0d && denominator != noDataValue) {
                                numerator = propGrid.getCell(row, col, ge.HandleOutOfMemoryErrorFalse);
                                if (numerator != noDataValue) {
                                    cVarGrid.setCell(row, col, (numerator / denominator), ge.HandleOutOfMemoryErrorFalse);
                                }
                            }
                        }
                        if (doWCVar) {
                            denominator = wVarGrid.getCell(row, col, ge.HandleOutOfMemoryErrorFalse);
                            if (denominator > 0.0d && denominator != noDataValue) {
                                numerator = wPropGrid.getCell(row, col, ge.HandleOutOfMemoryErrorFalse);
                                if (numerator != noDataValue) {
                                    wCVarGrid.setCell(row, col, (numerator / denominator), ge.HandleOutOfMemoryErrorFalse);
                                }
                            }
                        }
                        if (doCSkew) {
                            // Need to control for Math.pow as it does not do roots of negative numbers at all well!
                            denominator = varGrid.getCell(row, col, ge.HandleOutOfMemoryErrorFalse);
                            if (denominator > 0.0d && denominator != noDataValue) {
                                numerator = sDMeanPow3 / sumCells;
                                if (numerator > 0.0d) {
                                    cSkewGrid.setCell(row, col, (Math.pow(numerator, 1.0d / 3.0d)) / denominator, ge.HandleOutOfMemoryErrorFalse);
                                }
                                if (numerator == 0.0d) {
                                    cSkewGrid.setCell(row, col, numerator, ge.HandleOutOfMemoryErrorFalse);
                                }
                                if (numerator < 0.0d) {
                                    cSkewGrid.setCell(row, col, (-1.0d * (Math.pow(Math.abs(numerator), 1.0d / 3.0d))) / denominator, ge.HandleOutOfMemoryErrorFalse);
                                }
                            }
                        }
                        if (doWCSkew) {
                            // Need to control for Math.pow as it does not do roots of negative numbers at all well!
                            denominator = wVarGrid.getCell(row, col, ge.HandleOutOfMemoryErrorFalse);
                            if (denominator > 0.0d && denominator != noDataValue) {
                                numerator = sDWMeanPow3 / sumWeight;
                                if (numerator > 0.0d) {
                                    wCSkewGrid.setCell(row, col, (Math.pow(numerator, 1.0d / 3.0d)) / denominator, ge.HandleOutOfMemoryErrorFalse);
                                }
                                if (numerator == 0.0d) {
                                    wCSkewGrid.setCell(row, col, numerator, ge.HandleOutOfMemoryErrorFalse);
                                }
                                if (numerator < 0.0d) {
                                    wCSkewGrid.setCell(row, col, (-1.0d * (Math.pow(Math.abs(numerator), 1.0d / 3.0d))) / denominator, ge.HandleOutOfMemoryErrorFalse);
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
            sumGrid.setName("Sum", ge.HandleOutOfMemoryErrorFalse);
            result.add(sumGrid);
        }
        if (doWSum) {
            wSumGrid.setName("WSum", ge.HandleOutOfMemoryErrorFalse);
            result.add(wSumGrid);
        }
        if (doMean) {
            meanGrid.setName("Mean", ge.HandleOutOfMemoryErrorFalse);
            result.add(meanGrid);
        }
        if (doWMean) {
            wMeanGrid.setName("WMean", ge.HandleOutOfMemoryErrorFalse);
            result.add(wMeanGrid);
        }

        if (doProp) {
            propGrid.setName("Prop", ge.HandleOutOfMemoryErrorFalse);
            result.add(propGrid);
        }
        if (doWProp) {
            wPropGrid.setName("WProp", ge.HandleOutOfMemoryErrorFalse);
            result.add(wPropGrid);
        }
        if (doVar) {
            varGrid.setName("Var", ge.HandleOutOfMemoryErrorFalse);
            result.add(varGrid);
        }
        if (doWVar) {
            wVarGrid.setName("WVar", ge.HandleOutOfMemoryErrorFalse);
            result.add(wVarGrid);
        }
        if (doSkew) {
            skewGrid.setName("Skew", ge.HandleOutOfMemoryErrorFalse);
            result.add(skewGrid);
        }
        if (doWSkew) {
            wSkewGrid.setName("WSkew", ge.HandleOutOfMemoryErrorFalse);
            result.add(wSkewGrid);
        }
        if (doCVar) {
            cVarGrid.setName("CVar", ge.HandleOutOfMemoryErrorFalse);
            result.add(cVarGrid);
        }
        if (doWCVar) {
            wCVarGrid.setName("WCVar", ge.HandleOutOfMemoryErrorFalse);
            result.add(wCVarGrid);
        }
        if (doCSkew) {
            cSkewGrid.setName("CSkew", ge.HandleOutOfMemoryErrorFalse);
            result.add(cSkewGrid);
        }
        if (doWCSkew) {
            wCSkewGrid.setName("WCSkew", ge.HandleOutOfMemoryErrorFalse);
            result.add(wCSkewGrid);
        }

        return result;
    }

    /**
     * TODO
     *
     * @param grid
     * @param rowIndex the rowIndex of the cell about which the statistics are
     * returned
     * @param colIndex the rowIndex of the cell about which the statistics are
     * returned
     * @param statistic
     * @param distance
     * @param weightFactor
     * @param weightIntersect
     * @return 
     */
    public double[] regionUnivariateStatistics(
            Grids_GridDouble grid, 
            int rowIndex, int colIndex, 
            String statistic, 
            double distance, 
            double weightIntersect, 
            double weightFactor) {
        return null;
    }

    //    /**
    //     * TODO:
    //     * @param scaleIntersect typically a number between 0 and 1 which controls
    //     *                       the weight applied at the initial scale
    //     * @param scaleFactor = 0.0d all scales equally weighted
    //     *                    > 0.0d means that the last scale has a zero weight
    //     *                    < 0.0d means that the final scale has a weight of 1
    //     *                    > -1.0d && < 1.0d provides an inverse decay on scale weighting
    //     */
    //    public Vector regionUnivariateStatisticsCrossScale( Grids_GridDouble grid, Vector statistics, double distance, double weightIntersept, double weightFactor, double scaleIntersect, double scaleFactor ) {
    //        try {
    //            return regionUnivariateStatisticsCrossScale( grid, statistics, distance, weightIntersept, weightFactor, scaleIntersect, scaleFactor, new Grids_GridDoubleFactory() );
    //        } catch ( java.lang.OutOfMemoryError e ) {
    //            return regionUnivariateStatisticsCrossScale( grid, statistics, distance, weightIntersept, weightFactor, scaleIntersect, scaleFactor, new Grid2DSquareCellDoubleFileFactory() );
    //        }
    //    }
    /**
     * TODO
     *
     * @param grid
     * @param statistics
     * @param scaleIntersept typically a number between 0 and 1 which controls
     * the weight applied at the initial scale
     * @param distance
     * @param scaleFactor = 0.0d all scales equally weighted > 0.0d means that
     * the last scale has a zero weight < 0.0d means that the final scale has a
     * weight of 1 > -1.0d && < 1.0d provides an inverse decay on scale
     * weighting
     * @param gridFactory the Abstract2DSquareCellDoubleFactory used to create
     * grids
     * @param weightIntersept
     * @param weightFactor
     * @return 
     */
    public ArrayList regionUnivariateStatisticsCrossScale(
            Grids_GridDouble grid, 
            ArrayList statistics, double distance, 
            double weightIntersept, double weightFactor, 
            double scaleIntersept, double scaleFactor, 
            Grids_GridDoubleFactory gridFactory) {
        ArrayList result = new ArrayList();
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
     * Returns an Grids_GridDouble[] containing geometric density surfaces
 at a range of scales: result[ 0 ] - is the result at the first scale (
 double the cellsize of grid ) result[ 1 ] - if it exists is the result at
 the second scale ( double the cellsize of result[ 0 ] ) result[ n ] - if
 it exists is the result at the ( n + 1 )th scale ( double the cellsize of
 result[ n - 1 ] ) The algorithm used for generating a geometric density
 surface is described in: Turner A (2000) Density Data Generation for
 Spatial Data Mining Applications.
     * http://www.geog.leeds.ac.uk/people/a.turner/papers/geocomp00/gc_017.htm
     *
     * @param grid - the input Grids_GridDouble
     * @param distance - the distance limiting the maximum scale of geometric
     * density surface produced
     * @param gridFactory - the Grids_GridDoubleFactory to be used in
 processing
     * @return 
     */
    public Grids_GridDouble[] geometricDensity(
            Grids_GridDouble grid,
            double distance,
            Grids_GridDoubleFactory gridFactory) {
        boolean handleOutOfMemoryError = true;
        long n;
        n = grid.getStatistics(handleOutOfMemoryError).getN(handleOutOfMemoryError);
        //double sparseness = grid.getStatistics().getSparseness();
        long nrows = grid.getNRows(handleOutOfMemoryError);
        long ncols = grid.getNCols(handleOutOfMemoryError);
        //BigInteger cellCount = new BigInteger( Long.toString( nrows ) ).add( new BigInteger( Long.toString( ncols ) ) );

        Grids_Dimensions dimensions = grid.getDimensions(handleOutOfMemoryError);
        double cellsize = grid.getCellsizeDouble(handleOutOfMemoryError);
        double noDataValue = grid.getNoDataValue(handleOutOfMemoryError);
        int cellDistance = (int) Math.ceil(distance / cellsize);
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
        // If all values are noDataValues return noDataValue density results
        if (n == 0) {
            for (int i = 0; i < numberOfIterations; i++) {
                result[ i] = (Grids_GridDouble) gridFactory.create(grid);
            }
            return result;
        }
        // Initialise temporary numerator and normaliser grids
        Grids_GridDouble g2 = (Grids_GridDouble) gridFactory.create(nrows, ncols);
        Grids_GridDouble g3 = (Grids_GridDouble) gridFactory.create(nrows, ncols);
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                d1 = grid.getCell(row, col, ge.HandleOutOfMemoryErrorFalse);
                if (d1 != noDataValue) {
                    //g2.initCell( row, col, d1 );
                    //g3.initCell( row, col, 1.0d );
                    g2.setCell(row, col, d1, ge.HandleOutOfMemoryErrorFalse);
                    g3.setCell(row, col, 1.0d, ge.HandleOutOfMemoryErrorFalse);
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
                            d1 = g2.getCell(
                                    (row + p),
                                    (col + q),
                                    ge.HandleOutOfMemoryErrorFalse)
                                    + g2.getCell(
                                    (row + p),
                                    (col + q - doubler),
                                    ge.HandleOutOfMemoryErrorFalse)
                                    + g2.getCell(
                                    (row + p - doubler),
                                    (col + q),
                                    ge.HandleOutOfMemoryErrorFalse)
                                    + g2.getCell(
                                    (row + p - doubler),
                                    (col + q - doubler),
                                    ge.HandleOutOfMemoryErrorFalse);
                            //g4.initCell( ( row + p ), ( col + q ), d1 );
                            g4.setCell(
                                    (row + p),
                                    (col + q),
                                    d1,
                                    ge.HandleOutOfMemoryErrorFalse);
                            d2 = g3.getCell(
                                    (row + p),
                                    (col + q),
                                    ge.HandleOutOfMemoryErrorFalse)
                                    + g3.getCell(
                                    (row + p),
                                    (col + q - doubler),
                                    ge.HandleOutOfMemoryErrorFalse)
                                    + g3.getCell(
                                    (row + p - doubler),
                                    (col + q),
                                    ge.HandleOutOfMemoryErrorFalse)
                                    + g3.getCell(
                                    (row + p - doubler),
                                    (col + q - doubler),
                                    ge.HandleOutOfMemoryErrorFalse);
                            //g5.initCell( ( row + p ), ( col + q ), d2 );
                            g5.setCell((row + p), (col + q), d2, ge.HandleOutOfMemoryErrorFalse);
                            if (d2 != 0.0d) {
                                //g6.initCell( ( row + p ), ( col + q ), ( d1 / d2 ) );
                                g6.setCell(
                                        (row + p),
                                        (col + q),
                                        (d1 / d2),
                                        ge.HandleOutOfMemoryErrorFalse);
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
            gridFactory.setNoDataValue(noDataValue);
            density = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            for (row = 0; row < nrows; row += doubler) {
                for (int p = 0; p < doubler; p++) {
                    for (col = 0; col < ncols; col += doubler) {
                        for (int q = 0; q < doubler; q++) {
                            d1 = 0.0d;
                            d2 = 0.0d;
                            for (int a = 0; a < growth; a++) {
                                for (int b = 0; b < growth; b++) {
                                    if (g6.isInGrid((row + p + a), (col + q + b), ge.HandleOutOfMemoryErrorFalse)) {
                                        d1 += g6.getCell(
                                                (row + p + a),
                                                (col + q + b),
                                                ge.HandleOutOfMemoryErrorFalse);
                                        d2 += 1.0d;
                                    }
                                }
                            }
                            if (d2 != 0.0d) {
                                //density.addToCell( ( row + p ), ( col + q ), ( d1 / d2 ) );
                                //density.initCell( ( row + p ), ( col + q ), ( d1 / d2 ) );
                                density.setCell(
                                        (row + p),
                                        (col + q),
                                        (d1 / d2),
                                        ge.HandleOutOfMemoryErrorFalse);
                            } else {
                                //density.initCell( ( row + p ), ( col + q ), 0.0d );
                                density.setCell(
                                        (row + p),
                                        (col + q),
                                        0.0d,
                                        ge.HandleOutOfMemoryErrorFalse);
                            }
                        }
                    }
                }
            }
            //            g6.clear();
            result[ iteration] = density;
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
     * Returns an Grids_GridDouble[] result with elements based on
 statistics and values based on bivariate comparison of grid0 and grid1,
 distance, weightIntersect and weightFactor.
     *
     * @param grid0 the Grids_GridDouble to be regionBivariateStatisticsd
 with grid1
     * @param grid1 the Grids_GridDouble to be regionBivariateStatisticsd
 with grid0
     * @param statistics a String[] whose elements may be "diff", "abs", "corr1"
     * , "corr2", "zscore". If they are then the respective Geographically
     * Weighted Statistics (GWS) are returned in the result array
     * @param distance the distance defining the region within which values will
     * be used
     * @param weightIntersect typically a number between 0 and 1 which controls
     * the weight applied at the centre of the kernel
     * @param weightFactor = 0.0d all values within distance will be equally
     * weighted > 0.0d means the edge of the kernel has a zero weight < 0.0d
     * means that the edage of the kernel has a weight of 1 > -1.0d && < 1.0d
     * provides an inverse decay
     * @param gridFactory the Abstract2DSquareCellDoubleFactory used to create
     * grids TODO: Check and ensure that reasonable answers are returned for
     * grids with different spatial frames. (NB. Sensibly the two grids being
     * correlated should have the same no data space.)
     * @return 
     */
    public Grids_GridDouble[] regionBivariateStatistics(
            Grids_GridDouble grid0,
            Grids_GridDouble grid1,
            ArrayList statistics,
            double distance,
            double weightIntersect,
            double weightFactor,
            Grids_GridDoubleFactory gridFactory) {
        boolean handleOutOfMemoryError = true;
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
        long grid0Nrows = grid0.getNRows(handleOutOfMemoryError);
        long grid0Ncols = grid0.getNCols(handleOutOfMemoryError);
        Grids_Dimensions grid0Dimensions = grid0.getDimensions(handleOutOfMemoryError);
        double grid0NoDataValue = grid0.getNoDataValue(handleOutOfMemoryError);
        long grid1Nrows = grid1.getNRows(handleOutOfMemoryError);
        long grid1Ncols = grid1.getNCols(handleOutOfMemoryError);
        Grids_Dimensions grid1Dimensions = grid1.getDimensions(handleOutOfMemoryError);
        double grid1NoDataValue = grid1.getNoDataValue(handleOutOfMemoryError);
        double noDataValue = grid0NoDataValue;
        int grid0CellDistance = (int) Math.ceil(distance / grid1.getCellsizeDouble(handleOutOfMemoryError));

        // setNumberOfPairs is the number of pairs of values needed to calculate
        // the comparison statistics. It must be > 2
        int setNumberOfPairs = 20;

        int n;
        double thisDistance;
        double x0;
        double x1;
        double y0;
        double y1;
        double n0;
        double n1;
        double value0;
        double value1;
        double weight;
        double sumWeight;
        // Intersection check
        BigDecimal grid0Cellsize;
        BigDecimal grid1Cellsize;
        grid0Cellsize = grid0Dimensions.getCellsize();
        grid1Cellsize = grid1Dimensions.getCellsize();
        if ((grid1Dimensions.getXMin().compareTo(grid0Dimensions.getXMin().add(grid0Cellsize.multiply(new BigDecimal(Long.toString(grid0Ncols))))) == 1)
                || (grid1Dimensions.getXMax().compareTo(grid0Dimensions.getXMax().add(grid0Cellsize.multiply(new BigDecimal(Long.toString(grid0Nrows))))) == 1)
                || (grid1Dimensions.getYMin().add(grid1Cellsize.multiply(new BigDecimal(Long.toString(grid1Ncols)))).compareTo(grid0Dimensions.getYMin()) == -1)
                || (grid1Dimensions.getYMax().add(grid1Cellsize.multiply(new BigDecimal(Long.toString(grid1Nrows)))).compareTo(grid0Dimensions.getYMin()) == -1)) {
            //System.out.println( "Warning!!! No intersection in " + getClass().getName( handleOutOfMemoryError ) + " regionBivariateStatistics()" );
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
        double[] kernelParameters = Grids_Kernel.getKernelParameters(grid0, grid0CellDistance, distance, weightIntersect, weightFactor);
        double totalSumWeight = kernelParameters[ 0];

        // Difference
        if (dodiff) {
            gridFactory.setNoDataValue(grid0NoDataValue);

            diffGrid = (Grids_GridDouble) gridFactory.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            weightedDiffGrid = (Grids_GridDouble) gridFactory.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            normalisedDiffGrid = (Grids_GridDouble) gridFactory.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            weightedNormalisedDiffGrid = (Grids_GridDouble) gridFactory.create(grid0Nrows, grid0Ncols, grid0Dimensions);

            double max0;
            double max1;
            double min0;
            double min1;
            double range0;
            double range1;
            double diff;
            double weightedDiff;
            double normalisedDiff;
            double weightedNormalisedDiff;
            double dummy0;
            double dummy1;
            long row;
            long col;
            for (row = 0; row < grid0Nrows; row++) {
                for (col = 0; col < grid0Ncols; col++) {
                    max0 = Double.MIN_VALUE;
                    max1 = Double.MIN_VALUE;
                    min0 = Double.MAX_VALUE;
                    min1 = Double.MAX_VALUE;
                    x0 = grid0.getCellXDouble(col, ge.HandleOutOfMemoryErrorFalse);
                    y0 = grid0.getCellYDouble(row, ge.HandleOutOfMemoryErrorFalse);
                    diff = 0.0d;
                    weightedDiff = 0.0d;
                    normalisedDiff = 0.0d;
                    weightedNormalisedDiff = 0.0d;
                    sumWeight = 0.0d;
                    n = 0;
                    for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                        for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                            x1 = grid0.getCellXDouble(col + q, ge.HandleOutOfMemoryErrorFalse);
                            y1 = grid0.getCellYDouble(row + p, ge.HandleOutOfMemoryErrorFalse);
                            thisDistance = Grids_Utilities.distance(x0, y0, x1, y1);
                            if (thisDistance < distance) {
                                value0 = grid0.getCell(x1, y1, ge.HandleOutOfMemoryErrorFalse);
                                value1 = grid1.getCell(x1, y1, ge.HandleOutOfMemoryErrorFalse);
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
                                    weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance);
                                    sumWeight += weight;
                                    weightedDiff += (value0 - value1) * weight;
                                    diff += value0 - value1;
                                }
                            }
                        }
                    }
                    if (n > setNumberOfPairs) {
                        if (max0 != Double.MIN_VALUE && min0 != Double.MAX_VALUE && max1 != Double.MIN_VALUE && min1 != Double.MAX_VALUE) {
                            range0 = max0 - min0;
                            range1 = max1 - min1;
                            for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                                for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                                    x1 = grid0.getCellXDouble(col + q, ge.HandleOutOfMemoryErrorFalse);
                                    y1 = grid0.getCellYDouble(row + p, ge.HandleOutOfMemoryErrorFalse);
                                    thisDistance = Grids_Utilities.distance(x0, y0, x1, y1);
                                    if (thisDistance < distance) {
                                        value0 = grid0.getCell(x1, y1, ge.HandleOutOfMemoryErrorFalse);
                                        value1 = grid1.getCell(x1, y1, ge.HandleOutOfMemoryErrorFalse);
                                        if (value0 != grid0NoDataValue && value1 != grid1NoDataValue) {
                                            weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance);
                                            if (range0 > 0.0d) {
                                                dummy0 = (((value0 - min0) / range0) * 9.0d) + 1.0d;
                                            } else {
                                                dummy0 = 1.0d;
                                            }
                                            if (range1 > 0.0d) {
                                                dummy1 = (((value1 - min1) / range1) * 9.0d) + 1.0d;
                                            } else {
                                                dummy1 = 1.0d;
                                            }
                                            normalisedDiff += dummy0 - dummy1;
                                            weightedNormalisedDiff += (dummy0 - dummy1) * weight;
                                        }
                                    }
                                }
                            }
                        }
                        diffGrid.setCell(row, col, diff, ge.HandleOutOfMemoryErrorFalse);
                        weightedDiffGrid.setCell(row, col, weightedDiff * sumWeight / totalSumWeight, ge.HandleOutOfMemoryErrorFalse);
                        normalisedDiffGrid.setCell(row, col, normalisedDiff, ge.HandleOutOfMemoryErrorFalse);
                        weightedNormalisedDiffGrid.setCell(row, col, weightedNormalisedDiff * sumWeight / totalSumWeight, ge.HandleOutOfMemoryErrorFalse);
                    }
                }
            }
        }

        // Correlation and Zscore difference
        // temporarily fix range
        if (docorr || dozdiff) {
            gridFactory.setNoDataValue(grid0NoDataValue);
            weightedCorrelationGrid = (Grids_GridDouble) gridFactory.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            correlationGrid = (Grids_GridDouble) gridFactory.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            weightedZdiffGrid = (Grids_GridDouble) gridFactory.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            zdiffGrid = (Grids_GridDouble) gridFactory.create(grid0Nrows, grid0Ncols, grid0Dimensions);
            // setNumberOfPairs defines how many cells are needed to calculate correlation
            double max0;
            double max1;
            double min0;
            double min1;
            double range0;
            double range1;
            double sumWeight0;
            double sumWeight1;
            double weightedMean0;
            double weightedMean1;
            double weightedSum0Squared;
            double weightedSum1Squared;
            double weightedSum01;
            double weightedStandardDeviation0;
            double weightedStandardDeviation1;
            double weightedZdiff;
            double mean0;
            double mean1;
            double sum0Squared;
            double sum1Squared;
            double sum01;
            double standardDeviation0;
            double standardDeviation1;
            double zdiff;
            double denominator;
            double normaliser;
            double dummy0 = Double.MIN_VALUE;
            double dummy1 = Double.MIN_VALUE;
            long row;
            long col;
            for (row = 0; row < grid0Nrows; row++) {
                for (col = 0; col < grid0Ncols; col++) {
                    //if ( grid0.getCell( row, col ) != grid0NoDataValue ) {
                    x0 = grid0.getCellXDouble(col, ge.HandleOutOfMemoryErrorFalse);
                    y0 = grid0.getCellYDouble(row, ge.HandleOutOfMemoryErrorFalse);
                    max0 = Double.MIN_VALUE;
                    max1 = Double.MIN_VALUE;
                    min0 = Double.MAX_VALUE;
                    min1 = Double.MAX_VALUE;
                    sumWeight0 = 0.0d;
                    sumWeight1 = 0.0d;
                    weightedMean0 = 0.0d;
                    weightedMean1 = 0.0d;
                    weightedSum0Squared = 0.0d;
                    weightedSum1Squared = 0.0d;
                    weightedSum01 = 0.0d;
                    weightedStandardDeviation0 = 0.0d;
                    weightedStandardDeviation1 = 0.0d;
                    weightedZdiff = 0.0d;
                    mean0 = 0.0d;
                    mean1 = 0.0d;
                    sum0Squared = 0.0d;
                    sum1Squared = 0.0d;
                    sum01 = 0.0d;
                    standardDeviation0 = 0.0d;
                    standardDeviation1 = 0.0d;
                    zdiff = 0.0d;
                    n = 0;
                    n0 = 0.0d;
                    n1 = 0.0d;
                    // Calculate max min range sumWeight
                    for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                        for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                            x1 = grid0.getCellXDouble(col + q, ge.HandleOutOfMemoryErrorFalse);
                            y1 = grid0.getCellYDouble(row + p, ge.HandleOutOfMemoryErrorFalse);
                            thisDistance = Grids_Utilities.distance(x0, y0, x1, y1);
                            if (thisDistance < distance) {
                                weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance);
                                value0 = grid0.getCell(x1, y1, ge.HandleOutOfMemoryErrorFalse);
                                value1 = grid1.getCell(x1, y1, ge.HandleOutOfMemoryErrorFalse);
                                if (value0 != grid0NoDataValue) {
                                    max0 = Math.max(max0, value0);
                                    min0 = Math.min(min0, value0);
                                    n0 += 1.0d;
                                    sumWeight0 += weight;
                                }
                                if (value1 != grid1NoDataValue) {
                                    max1 = Math.max(max1, value1);
                                    min1 = Math.min(min1, value1);
                                    n1 += 1.0d;
                                    sumWeight1 += weight;
                                }
                                if (value0 != grid0NoDataValue && value1 != grid1NoDataValue) {
                                    n++;
                                }
                            }
                        }
                    }
                    if (n > setNumberOfPairs) {
                        if (max0 != Double.MIN_VALUE && min0 != Double.MAX_VALUE && max1 != Double.MIN_VALUE && min1 != Double.MAX_VALUE) {
                            range0 = max0 - min0;
                            range1 = max1 - min1;
                            for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                                for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                                    x1 = grid0.getCellXDouble(col + q, ge.HandleOutOfMemoryErrorFalse);
                                    y1 = grid0.getCellYDouble(row + p, ge.HandleOutOfMemoryErrorFalse);
                                    thisDistance = Grids_Utilities.distance(x0, y0, x1, y1);
                                    if (thisDistance < distance) {
                                        weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance);
                                        value0 = grid0.getCell(row + p, col + q, ge.HandleOutOfMemoryErrorFalse);
                                        value1 = grid1.getCell(row + p, col + q, ge.HandleOutOfMemoryErrorFalse);
                                        if (value0 != grid0NoDataValue) {
                                            if (range0 > 0.0d) {
                                                dummy0 = (((value0 - min0) / range0) * 9.0d) + 1.0d;
                                            } else {
                                                dummy0 = 1.0d;
                                            }
                                            weightedMean0 += (dummy0 / sumWeight0) * weight;
                                            mean0 += (dummy0 / n0);
                                        }
                                        if (value1 != grid1NoDataValue) {
                                            if (range1 > 0.0d) {
                                                dummy1 = (((value1 - min1) / range1) * 9.0d) + 1.0d;
                                            } else {
                                                dummy1 = 1.0d;
                                            }
                                            weightedMean1 += (dummy1 / sumWeight1) * weight;
                                            mean1 += (dummy1 / n1);
                                        }
                                    }
                                }
                            }
                            for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                                for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                                    x1 = grid0.getCellXDouble(col + q, ge.HandleOutOfMemoryErrorFalse);
                                    y1 = grid0.getCellYDouble(row + p, ge.HandleOutOfMemoryErrorFalse);
                                    thisDistance = Grids_Utilities.distance(x0, y0, x1, y1);
                                    if (thisDistance < distance) {
                                        weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance);
                                        value0 = grid0.getCell(x1, y1, ge.HandleOutOfMemoryErrorFalse);
                                        if (value0 != grid0NoDataValue) {
                                            if (range0 > 0.0d) {
                                                dummy0 = (((value0 - min0) / range0) * 9.0d) + 1.0d;
                                            } else {
                                                dummy0 = 1.0d;
                                            }
                                            standardDeviation0 += Math.pow((dummy0 - mean0), 2.0d);
                                            weightedStandardDeviation0 += Math.pow((dummy0 - weightedMean0), 2.0d) * weight;
                                        }
                                        value1 = grid1.getCell(x1, y1, ge.HandleOutOfMemoryErrorFalse);
                                        if (value1 != grid1NoDataValue) {
                                            if (range1 > 0.0d) {
                                                dummy1 = (((value1 - min1) / range1) * 9.0d) + 1.0d;
                                            } else {
                                                dummy1 = 1.0d;
                                            }
                                            standardDeviation1 += Math.pow((dummy1 - mean1), 2.0d);
                                            weightedStandardDeviation1 += Math.pow((dummy1 - weightedMean1), 2.0d) * weight;
                                        }
                                        if (value0 != grid0NoDataValue && value1 != grid1NoDataValue) {
                                            //weightedSum0Squared += Math.pow( ( ( value0 * weight ) - weightedMean0 ), 2.0d );
                                            //weightedSum1Squared += Math.pow( ( ( value1 * weight ) - weightedMean1 ), 2.0d );
                                            //weightedSum01 += ( ( value0 * weight ) - weightedMean0 ) * ( ( value1 * weight ) - weightedMean1 );
                                            weightedSum0Squared += Math.pow((dummy0 - weightedMean0), 2.0d) * weight;
                                            weightedSum1Squared += Math.pow((dummy1 - weightedMean1), 2.0d) * weight;
                                            weightedSum01 += (dummy0 - weightedMean0) * (dummy1 - weightedMean1) * weight;
                                            sum0Squared += Math.pow((dummy0 - mean0), 2.0d);
                                            sum1Squared += Math.pow((dummy1 - mean1), 2.0d);
                                            sum01 += (dummy0 - mean0) * (dummy1 - mean1);
                                        }
                                    }
                                }
                            }
                            denominator = Math.sqrt(weightedSum0Squared) * Math.sqrt(weightedSum1Squared);
                            if (denominator > 0.0d && denominator != noDataValue) {
                                weightedCorrelationGrid.setCell(row, col, weightedSum01 / denominator, ge.HandleOutOfMemoryErrorFalse);
                            }
                            denominator = Math.sqrt(sum0Squared) * Math.sqrt(sum1Squared);
                            if (denominator > 0.0d && denominator != noDataValue) {
                                correlationGrid.setCell(row, col, sum01 / denominator, ge.HandleOutOfMemoryErrorFalse);
                            }
                            weightedStandardDeviation0 = Math.sqrt(weightedStandardDeviation0 / (n0 - 1.0d));
                            standardDeviation0 = Math.sqrt(standardDeviation0 / (n0 - 1.0d));
                            weightedStandardDeviation1 = Math.sqrt(weightedStandardDeviation1 / (n1 - 1.0d));
                            standardDeviation1 = Math.sqrt(standardDeviation1 / (n1 - 1.0d));
                            // Calculate z scores and difference
                            if (weightedStandardDeviation0 > 0.0d && weightedStandardDeviation1 > 0.0d) {
                                for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                                    for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                                        x1 = grid0.getCellXDouble(col + q, ge.HandleOutOfMemoryErrorFalse);
                                        y1 = grid0.getCellYDouble(row + p, ge.HandleOutOfMemoryErrorFalse);
                                        thisDistance = Grids_Utilities.distance(x0, y0, x1, y1);
                                        if (thisDistance < distance) {
                                            value0 = grid0.getCell(x1, y1, ge.HandleOutOfMemoryErrorFalse);
                                            value1 = grid1.getCell(x1, y1, ge.HandleOutOfMemoryErrorFalse);
                                            if (value0 != grid0NoDataValue && value1 != grid1NoDataValue) {
                                                if (range0 > 0.0d) {
                                                    dummy0 = (((value0 - min0) / range0) * 9.0d) + 1.0d;
                                                } else {
                                                    dummy0 = 1.0d;
                                                }
                                                if (range1 > 0.0d) {
                                                    dummy1 = (((value1 - min1) / range1) * 9.0d) + 1.0d;
                                                } else {
                                                    dummy1 = 1.0d;
                                                }
                                                weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance);
                                                //weightedZdiff += ( ( ( ( value0 * weight ) - weightedMean0 ) / weightedStandardDeviation0 ) - ( ( ( value1 * weight ) - weightedMean1 ) / weightedStandardDeviation1 ) );
                                                weightedZdiff += (((dummy0 - weightedMean0) / weightedStandardDeviation0) - ((dummy1 - weightedMean1) / weightedStandardDeviation1)) * weight;
                                            }
                                        }
                                    }
                                }
                                weightedZdiffGrid.setCell(row, col, weightedZdiff, ge.HandleOutOfMemoryErrorFalse);
                            }
                            if (standardDeviation0 > 0.0d && standardDeviation1 > 0.0d) {
                                for (int p = -grid0CellDistance; p <= grid0CellDistance; p++) {
                                    for (int q = -grid0CellDistance; q <= grid0CellDistance; q++) {
                                        x1 = grid0.getCellXDouble(col + q, ge.HandleOutOfMemoryErrorFalse);
                                        y1 = grid0.getCellYDouble(row + p, ge.HandleOutOfMemoryErrorFalse);
                                        thisDistance = Grids_Utilities.distance(x0, y0, x1, y1);
                                        if (thisDistance < distance) {
                                            value0 = grid0.getCell(x1, y1, ge.HandleOutOfMemoryErrorFalse);
                                            value1 = grid1.getCell(x1, y1, ge.HandleOutOfMemoryErrorFalse);
                                            if (value0 != grid0NoDataValue && value1 != grid1NoDataValue) {
                                                if (range0 > 0.0d) {
                                                    dummy0 = (((value0 - min0) / range0) * 9.0d) + 1.0d;
                                                } else {
                                                    dummy0 = 1.0d;
                                                }
                                                if (range1 > 0.0d) {
                                                    dummy1 = (((value1 - min1) / range1) * 9.0d) + 1.0d;
                                                } else {
                                                    dummy1 = 1.0d;
                                                }
                                                zdiff += (((dummy0 - mean0) / standardDeviation0) - ((dummy1 - mean1) / standardDeviation1));
                                            }
                                        }
                                    }
                                }
                                zdiffGrid.setCell(row, col, zdiff, ge.HandleOutOfMemoryErrorFalse);
                            }
                        }
                    }
                }
            }
        }
        allStatistics = 0;
        if (dodiff) {
            diffGrid.setName(grid0.getName(handleOutOfMemoryError) + "_Diff_" + grid1.getName(handleOutOfMemoryError), ge.HandleOutOfMemoryErrorFalse);
            result[ allStatistics] = diffGrid;
            allStatistics++;
            weightedDiffGrid.setName(grid0.getName(handleOutOfMemoryError) + "_WDiff_" + grid1.getName(handleOutOfMemoryError), ge.HandleOutOfMemoryErrorFalse);
            result[ allStatistics] = weightedDiffGrid;
            allStatistics++;
            normalisedDiffGrid.setName(grid0.getName(handleOutOfMemoryError) + "_NDiff_" + grid1.getName(handleOutOfMemoryError), ge.HandleOutOfMemoryErrorFalse);
            result[ allStatistics] = normalisedDiffGrid;
            allStatistics++;
            weightedNormalisedDiffGrid.setName(grid0.getName(handleOutOfMemoryError) + "_NWDiff_" + grid1.getName(handleOutOfMemoryError), ge.HandleOutOfMemoryErrorFalse);
            result[ allStatistics] = weightedNormalisedDiffGrid;
            allStatistics++;
        }
        if (docorr) {
            weightedCorrelationGrid.setName(grid0.getName(handleOutOfMemoryError) + "_WCorr_" + grid1.getName(handleOutOfMemoryError), ge.HandleOutOfMemoryErrorFalse);
            result[ allStatistics] = weightedCorrelationGrid;
            allStatistics++;
            correlationGrid.setName(grid0.getName(handleOutOfMemoryError) + "_Corr_" + grid1.getName(handleOutOfMemoryError), ge.HandleOutOfMemoryErrorFalse);
            result[ allStatistics] = correlationGrid;
            allStatistics++;
        }
        if (dozdiff) {
            weightedZdiffGrid.setName(grid0.getName(handleOutOfMemoryError) + "_WZDiff_" + grid1.getName(handleOutOfMemoryError), ge.HandleOutOfMemoryErrorFalse);
            result[ allStatistics] = weightedZdiffGrid;
            allStatistics++;
            zdiffGrid.setName(grid0.getName(handleOutOfMemoryError) + "_ZDiff_" + grid1.getName(handleOutOfMemoryError), ge.HandleOutOfMemoryErrorFalse);
            result[ allStatistics] = zdiffGrid;
            allStatistics++;
        }

        return result;
    }
}