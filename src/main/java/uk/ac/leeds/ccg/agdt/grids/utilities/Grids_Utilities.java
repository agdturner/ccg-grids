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
package uk.ac.leeds.ccg.agdt.grids.utilities;

import java.awt.geom.Point2D;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridDoubleIterator;
import uk.ac.leeds.ccg.agdt.math.Math_BigDecimal;

/**
 * This class holds miscellaneous general utility methods
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_Utilities {

    /**
     * Creates a new instance of Utilities
     */
    public Grids_Utilities() {
    }

    /**
     * Returns a string identifying the number of hours, minutes, seconds and
     * milliseconds for the input getTime in milliseconds.
     *
     * @param time
     * @return
     */
    public static String getTime(long time) {
        int hours = (int) Math.floor(time / 3600000.0d);
        int mins = (int) Math.floor((time - (3600000.0d * hours)) / 60000.0d);
        int secs = (int) Math.floor((time - (3600000.0d * hours) - (60000.0d * mins)) / 1000.0d);
        int millisecs = (int) Math.floor((time - (3600000.0d * hours) - (60000.0d * mins)) - (1000.0d * secs));
        return hours + " hours, " + mins + " mins, " + secs + " secs, " + millisecs + " millisecs";
    }

    /**
     * TODO: documentation
     *
     * @param value
     * @return
     */
    public static double getLarger(double value) {
        return Math.nextUp(value);
//        if ( value == Double.MAX_VALUE ) {
//            return value; // Issue warning?
//        } else {
//            double difference = 1.0d;
//            double counter = 1.0d;
//            boolean calculated;
//            if ( value != 0.0d ) {
//                difference = Math.abs( value );
//                counter = value;
//            }
//            for ( int i = 2048; i > 2; i /= 2 ) {
//                calculated = false;
//                while ( ! calculated ) {
//                    if ( ( value + difference ) == value ) {
//                        calculated = true;
//                    } else {
//                        counter *= ( double ) i;
//                        difference = Math.abs( 1.0d / counter );
//                    }
//                }
//                counter /= ( double ) i;
//                difference = Math.abs( 1.0d / counter );
//            }
//            //System.out.println( value + difference );
//            return value + difference;
//        }
    }

    /**
     * TODO: documentation
     *
     * @param value
     * @return
     */
    public static double getSmaller(double value) {
        return Math.nextAfter(value, Double.NEGATIVE_INFINITY);
//        if (value == -Double.MAX_VALUE) {
//            return value; // Issue warning?
//        } else {
//            double difference = Math.abs( value );
//            double counter = value;
//            boolean calculated;
//            int ite = 0;
//            for ( int i = 2048; i > 2; i /= 2 ) {
//                calculated = false;
//                while ( ! calculated ) {
//                    if ( ( value - difference ) == value ) {
//                        calculated = true;
//                    } else {
//                        counter *= ( double ) i;
//                        difference = Math.abs( 1.0d / counter );
//                    }
//                }
//                counter /= ( double ) i;
//                difference = Math.abs( 1.0d / counter );
//            }
//            return value - difference;
//        }
    }

    /**
     * For a given point at first from an angle from the y axis and a distance
     * maxDistance a new sample point is created. Sample points at nDistances
     * from maxDistance towards point from the first sample point are then
     * created and added to the result. The process is repeated for nAngles
     * going around clockwise from the x-y plane.
     *
     * @param point point at centre of sampling
     * @param angle angle in radians to the vertical (y axis) for first sampling
     * @param maxDistance the maximum sampling distance
     * @param nDistances the number of sampling distances upto maxDistance
     * @param nAngles sets how many samplesPoints to take at each sample
     * distance. Always the first sample is at angle from the vertical or
     * y-axis. The angles at which samples are taken are all the same.
     * @return
     *
     */
    public static Point2D.Double[][] getSamplePoints(
            Point2D.Double point, double angle, double maxDistance,
            int nDistances, int nAngles) {
        Point2D.Double[][] result;
        result = new Point2D.Double[nAngles][nDistances];
        double sAngle = 2.0d * Math.PI / (double) nAngles;
        double sDistance = maxDistance / (double) nDistances;
        double xdiff;
        double ydiff;
        double sina;
        double cosa;
        double distance;
        int d;
        for (int a = 0; a < nAngles; a++) {
            sina = Math.sin(angle + sAngle * a);
            cosa = Math.cos(angle + sAngle * a);
            distance = maxDistance;
            d = 0;
            while (distance >= sDistance) {
                xdiff = sina * distance;
                ydiff = cosa * distance;
                result[a][d] = new Point2D.Double(
                        point.x + xdiff,
                        point.y + ydiff);
                distance -= sDistance;
                d++;
            }
        }
        return result;
    }

    /**
     * @param x1 The x coordinate of the first point.
     * @param y1 The y coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     * @param dp The number of decimal places the result is to be accurate to.
     * @param rm The {@link RoundingMode} to use when rounding the result.
     *
     * @return The distance between two points calculated using
     * {@link BigDecimal} arithmetic.
     */
    public static final BigDecimal distance(BigDecimal x1, BigDecimal y1,
            BigDecimal x2, BigDecimal y2, int dp, RoundingMode rm) {
        return Math_BigDecimal.sqrt(((x1.subtract(x2)).pow(2))
                .add((y1.subtract(y2)).pow(2)), dp, rm);
    }

    /**
     * @param x1 The x coordinate of the first point.
     * @param y1 The y coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     * @return The distance between two points calculated using {@code double}
     * precision floating point numbers.
     */
    public static final double distance(
            double x1, double y1, double x2, double y2) {
        return Math.hypot((x1 - x2), (y1 - y2));
    }

    /**
     * Returns the clockwise angle in radians to the y axis of the line from: {@code x1},
     * {@code y1}; to, {@code x2}, {@code y2}.
     *
     * @param x1 The x coordinate of the first point.
     * @param y1 The y coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     * @return The clockwise angle in radians to the y axis of the line from x1,
     * y1 to x2, y2 calculated using {@code double}
     * precision floating point numbers.
     */
    public static final double angle(double x1, double y1, double x2, 
            double y2) {
        double xdiff = x1 - x2;
        double ydiff = y1 - y2;
        double angle;
        if (xdiff == 0.0d && ydiff == 0.0d) {
            angle = -1.0d;
        } else {
            if (xdiff <= 0.0d) {
                if (xdiff == 0.0d) {
                    if (ydiff <= 0.0d) {
                        angle = 0.0d;
                    } else {
                        angle = Math.PI;
                    }
                } else {
                    if (ydiff <= 0.0d) {
                        if (ydiff == 0.0d) {
                            angle = Math.PI / 2.0d;
                        } else {
                            angle = Math_BigDecimal.atan(Math.abs(xdiff / ydiff));
                        }
                    } else {
                        angle = Math.PI - Math.atan(Math.abs(xdiff / ydiff));
                    }
                }
            } else {
                if (ydiff <= 0.0d) {
                    if (ydiff == 0.0d) {
                        angle = 3.0d * Math.PI / 2.0d;
                    } else {
                        angle = (2.0d * Math.PI) - Math.atan(Math.abs(xdiff / ydiff));
                    }
                } else {
                    angle = Math.PI + Math.atan(Math.abs(xdiff / ydiff));
                }
            }
        }
        return angle;
    }
    
    /**
     * Returns the clockwise angle in radians to the y axis of the line from: {@code x1},
     * {@code y1}; to, {@code x2}, {@code y2}.
     *
     * @param x1 The x coordinate of the first point.
     * @param y1 The y coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     * @return The clockwise angle in radians to the y axis of the line from x1,
     * y1 to x2, y2 calculated using {@code double}
     * precision floating point numbers.
     */
    public static final double angle(double x1, double y1, double x2, 
            double y2) {
        double xdiff = x1 - x2;
        double ydiff = y1 - y2;
        double angle;
        if (xdiff == 0.0d && ydiff == 0.0d) {
            angle = -1.0d;
        } else {
            if (xdiff <= 0.0d) {
                if (xdiff == 0.0d) {
                    if (ydiff <= 0.0d) {
                        angle = 0.0d;
                    } else {
                        angle = Math.PI;
                    }
                } else {
                    if (ydiff <= 0.0d) {
                        if (ydiff == 0.0d) {
                            angle = Math.PI / 2.0d;
                        } else {
                            angle = Math.atan(Math.abs(xdiff / ydiff));
                        }
                    } else {
                        angle = Math.PI - Math.atan(Math.abs(xdiff / ydiff));
                    }
                }
            } else {
                if (ydiff <= 0.0d) {
                    if (ydiff == 0.0d) {
                        angle = 3.0d * Math.PI / 2.0d;
                    } else {
                        angle = (2.0d * Math.PI) - Math.atan(Math.abs(xdiff / ydiff));
                    }
                } else {
                    angle = Math.PI + Math.atan(Math.abs(xdiff / ydiff));
                }
            }
        }
        return angle;
    }

    /**
     * Returns a density plot of xGrid values against yGrid values. A density
     * plot is like a scatterplot, but rather than plotting individual points,
     * each point is aggregated to a cells and the result is a plot of the
     * density of points in the cells. The values of yGrid are scaled to be in
     * the same range as the values of xGrid and the number of divisions for
     * each axis is given by divisions. NB1 For this implementation xGrid and
     * yGrid must have the same spatial frame. NB2 The result returned has a set
     * cellsize of 1 and origin at ( 0, 0 ) (This enables easy comparison with
     * other density plots)
     *
     * @param xGrid
     * @param yGrid
     * @param factory
     * @param divisions
     * @return
     */
    public static Object[] densityPlot(
            Grids_GridDouble xGrid,
            Grids_GridDouble yGrid,
            int divisions,
            Grids_GridDoubleFactory factory) {
        Object[] result = new Object[4];
        boolean hoome;
        hoome = false;
        long nrows = xGrid.getNRows();
        long ncols = xGrid.getNCols();
        Grids_Dimensions dimensions = xGrid.getDimensions();
        double xllcorner = dimensions.getXMin().doubleValue();
        double yllcorner = dimensions.getYMin().doubleValue();
        double xGridNoDataValue = xGrid.getNoDataValue();
        double yGridNoDataValue = yGrid.getNoDataValue();
        double minx = xGrid.getStats().getMin(true);
        double maxx = xGrid.getStats().getMax(true);
        double miny = yGrid.getStats().getMin(true);
        double maxy = yGrid.getStats().getMax(true);
        double cellsize = (maxy - miny) / (double) divisions;
        Grids_GridDouble xGridRescaled = null;
        double value;
        double v;
        long row;
        long col;
//        if (minx == miny && maxx == maxy) {
//            File dir = Generic_IO.createNewFile(this.getDirectory());
//            xGridRescaled = (Grids_GridDouble) factory.create(dir,yGrid);
//        } else {
//            xGridRescaled = (Grids_GridDouble) factory.create(xGrid);
//            Grids_GridDoubleIterator ite;
//            for (row = 0; row < nrows; row++) {
//                for (col = 0; col < ncols; col++) {
//                    value = xGrid.getCell(row, col);
//                    if (value != yGridNoDataValue) {
//                        v = (((value - minx) / (maxx - minx))
//                                * (maxy - miny)) + miny;
//                        xGridRescaled.setCell(row, col, v);
//                    }
//                }
//            }
//        }
        // This code is broken and wants fixing.
//        //System.out.println( "yGridRescaled" );
//        Grids_GridDouble temp1 = (Grids_GridDouble) factory.create(
//                divisions, divisions,
//                new Grids_Dimensions(divisions, divisions));
//        double x;
//        double y;
//        int division;
//        double[] sumy = new double[divisions];
//        double[] numy = new double[divisions];
//        double[] sumysq = new double[divisions];
//        for (int j = 0; j < divisions; j++) {
//            sumy[j] = 0.0d;
//            numy[j] = 0.0d;
//            sumysq[j] = 0.0d;
//        }
//        for (row = 0; row < nrows; row++) {
//                for (col = 0; col < ncols; col++) {
//                    x = xGridRescaled.getCell(row, col, hoome);
//                    y = yGrid.getCell(row, col, hoome);
//                if (y != yGridNoDataValue) {
//                    if (x != xGridNoDataValue) {
//                        temp1.addToCell(x, y, 1.0d);
//                        division = temp1.getColIndex(x);
//                        if (division == divisions) {
//                            division = divisions - 1;
//                        }//System.out.println(division);
//                        sumy[division] += y;
//                        numy[division] += 1.0d;
//                        sumysq[division] += (y * y);
//                    }
//                }
//            }
//        }
//        double[] stdevy = new double[divisions];
//        double[] meany = new double[divisions];
//        for (int j = 0; j < divisions; j++) {
//            if (numy[j] > 0.0d) {
//                meany[j] = sumy[j] / numy[j];
//                if (numy[j] > 1.0d) {
//                    stdevy[j] = Math.sqrt(((numy[j] * sumysq[j]) - (sumy[j] * sumy[j])) / (numy[j] * (numy[j] - 1)));
//                }
//            }
//        }
//        result[0] = stdevy;
//        result[1] = meany;
//        result[2] = numy;
//        double[] normalisers = new double[divisions];
//        //double d1 = 0.0d;
//        //double d2 = 0.0d;
//        for (int j = 0; j < divisions; j++) {
//            normalisers[j] = 0.0d;
//            for (int i = 0; i < divisions; i++) {
//                value = temp1.getCell(i, j);
//                if (value != xnoDataValue) {
//                    normalisers[j] += value;
//                    //d1 += value;
//                    //d2 += 1.0d;
//                }
//            }
//        }
//        AbstractGrid2DSquareCellDouble densityPlotGrid = factory.createGrid2DSquareCellDouble(divisions, divisions, minx, minx, cellsize, xnoDataValue);
//        //double average = d1 / d2;
//        for (int i = 0; i < divisions; i++) {
//            for (int j = 0; j < divisions; j++) {
//                if (normalisers[j] != 0.0d) {
//                    value = temp1.getCell(i, j);
//                    if (value != xnoDataValue) {
//                        //densityPlotGrid.setCell( i, j, temp1.getCell( i, j ) / ( normalisers[ j ] + average ) );
//                        densityPlotGrid.setCell(i, j, temp1.getCell(i, j) / normalisers[j]);
//                    }
//                }
//            }
//        }
//        densityPlotGrid.setCellsize(1.0d);
//        densityPlotGrid.setXllcorner(0.0d);
//        densityPlotGrid.setYllcorner(0.0d);
//        temp1.clear();
//        if (!(minx == miny && maxx == maxy)) {
//            xGridRescaled.clear();
//        }
//        result[3] = densityPlotGrid;
        return result;
    }
    /**
     * Generates a CSV file for a cumulative gains chart of observed and
     * indicator NB1. observed and indicator must have same spatial frame.
     */
    /*public static void toGainsChartCSV( AbstractGrid2DSquareCellDouble observed, AbstractGrid2DSquareCellDouble indicator, int divisions, File csvFile ) {
    int nrows = observed.getNrows();
    int ncols = observed.getNcols();
    AbstractGridStatistics indicatorStats = indicator.getGridStatistics();
    //System.out.println( "mean indicator " + indicatorStats.getMean() );
    double numberOfCellsPerDivision = ( nrows * ncols * ( 1 - indicatorStats.getSparseness() ) ) / divisions ;
    //System.out.println( "numberOfCellsPerDivision " + numberOfCellsPerDivision );
    double indicatorValue;
    double indicatorNoDataValue = indicator.getNoDataValue();
    double observedValue;
    double cumulativeObservedValue = 0;
    double observedNoDataValue = observed.getNoDataValue();
    TreeSet treeSet = new TreeSet();
    for ( int i = 0; i < nrows * ncols; i ++ ) {
    indicatorValue = indicator.getCell( i );
    if ( indicatorValue != indicatorNoDataValue ) {
    treeSet.add( new Double( indicator.getCell( i ) ) );
    }
    }
    double[] observedValueSum = new double[ divisions ];
    double[] percentageCumulativeObservedValueSum = new double[ divisions ];
    for ( int i = 0; i < divisions; i ++ ) {
    observedValueSum[ i ] = 0.0d;
    percentageCumulativeObservedValueSum[ i ] = 0.0d;
    }
    int division = divisions - 1;
    double cellCounter = 0;
    Iterator iterator = treeSet.iterator();
    while ( iterator.hasNext() ) {
    indicatorValue = ( ( Double ) iterator.next() ).doubleValue();
    for ( int i = 0; i < nrows; i ++ ) {
    for ( int j = 0; j < ncols; j ++ ) {
    if ( indicator.getCell( i, j ) == indicatorValue ) {
    observedValue = observed.getCell( i, j );
    if ( observedValue != observedNoDataValue ) {
    cellCounter ++;
    if ( cellCounter >= numberOfCellsPerDivision ) {
    division --;
    if ( division <= 0 ) {
    division = 0;
    }
    cellCounter -= numberOfCellsPerDivision;
    }
    observedValueSum[ division ] += observedValue;
    }
    }
    }
    }
    }
    AbstractGridStatistics observedStats = observed.getGridStatistics();
    double sumObserved = observedStats.getSum();
    //System.out.println( "mean observed = " + observedStats.getMean() );
    //System.out.println( "sum observed = " + sumObserved );
    //// Check
    //double observedSum = 0.0d;
    //for ( int i = 0; i < divisions; i ++ ) {
    //    observedSum += observedValueSum[ i ];
    //}
    //if ( observedSum != sumObserved ) {
    //    System.out.println( "Warning!!!: observedSum != sumObserved" );
    //}
    //// End Check
    percentageCumulativeObservedValueSum[ 0 ] = observedValueSum[ 0 ] * 100.0d / sumObserved;
    for ( int i = 1; i < divisions; i ++ ) {
    percentageCumulativeObservedValueSum[ i ] = ( observedValueSum[ i ] * 100.0d / sumObserved ) + percentageCumulativeObservedValueSum[ i - 1 ];
    }
    PrintWriter pw = null;
    try {
    pw = new PrintWriter( new FileOutputStream( csvFile ) );
    } catch ( java.io.IOException e ) {
    System.out.println( e );
    e.printStackTrace();
    }
    pw.println( "%cumulativeObservedValueSum,%indicator" );
    pw.println( "0,0" );
    for ( int i = 0; i < divisions; i ++ ) {
    pw.println( percentageCumulativeObservedValueSum[ i ] + "," + ( ( i + 1 ) * 100.0d / divisions ) );
    }
    pw.flush();
    pw.close();
    }*/
}
