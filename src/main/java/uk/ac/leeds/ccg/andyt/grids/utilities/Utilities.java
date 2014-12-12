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
package uk.ac.leeds.ccg.andyt.grids.utilities;

import java.awt.geom.Point2D;
import uk.ac.leeds.ccg.andyt.grids.core.AbstractGrid2DSquareCell;
import uk.ac.leeds.ccg.andyt.grids.core.Grid2DSquareCellDoubleFactory;

/**
 * This class holds miscellaneous general utility methods
 */
public class Utilities {

    /** Creates a new instance of Utilities */
    public Utilities() {
    }

    /**
     * Returns a string identifying the number of hours, minutes, seconds and
     * milliseconds for the input _ReportTime in milliseconds.
     * @param time
     * @return 
     */
    public static String _ReportTime(long time) {
        int hours = (int) Math.floor(time / 3600000.0d);
        int mins = (int) Math.floor((time - (3600000.0d * hours)) / 60000.0d);
        int secs = (int) Math.floor((time - (3600000.0d * hours) - (60000.0d * mins)) / 1000.0d);
        int milisecs = (int) Math.floor((time - (3600000.0d * hours) - (60000.0d * mins)) - (1000.0d * secs));
        return hours + " hours, " + mins + " mins, " + secs + " secs, " + milisecs + " milisecs";
    }

    /**
     * TODO: documentation
     * @param value
     * @return 
     */
    public static double getValueALittleBitLarger(double value) {
        return Math.nextUp(value);
//        if ( value == Double.MAX_VALUE ) {
//            System.out.println( "Warning: Returning Double.POSITIVE_INFINITY in uk.ac.leeds.ccg.grids.Utilities.getValueALittleBitLarger(double)." );
//            return Double.POSITIVE_INFINITY;
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
     * @param value
     * @return 
     */
    public static double getValueALittleBitSmaller(double value) {
        return Math.nextAfter(value, Double.NEGATIVE_INFINITY);
//        if ( value == Double.MIN_VALUE ) {
//            System.out.println( "Warning: Returning Double.NEGATIVE_INFINITY in uk.ac.leeds.ccg.grids.Utilities.getValueALittleBitSmaller(double)." );
//            return Double.NEGATIVE_INFINITY;
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
     * TODO: documentation
     * @param point point at centre of sampling
     * @param angle angle in radians to the vertical (y axis) for first sampling
     * @param maxDistance the maximum sampling distance
     * @param numberOfSamplingDistances the number of sampling distances upto
     *   maxDistance
     * @param numberOfSamplingAngles sets how many samplesPoints to take at each
     *   sample distance. Always the first sample is at angle from the vertical
     *   or y-axis. The angles at which samples are taken are all the same.
     * @return 
     *
     */
    public static Point2D.Double[] getSamplePoints(
            Point2D.Double point,
            double angle,
            double maxDistance,
            int numberOfSamplingDistances,
            int numberOfSamplingAngles) {
        double samplingAngle = 2.0d * Math.PI / (double) numberOfSamplingAngles;
        double samplingDistance = maxDistance / numberOfSamplingAngles;
        return getSamplePoints(
                point,
                angle,
                maxDistance,
                samplingDistance,
                samplingAngle,
                numberOfSamplingDistances * numberOfSamplingAngles);
    }

    /**
     * TODO: documentation
     * @param numberOfSamples
     * @param samplingAngle
     * @return 
     */
    public static Point2D.Double[] getSamplePoints(
            Point2D.Double point,
            double angle,
            double maxDistance,
            double samplingDistance,
            double samplingAngle,
            int numberOfSamples) {
        Point2D.Double[] points = new Point2D.Double[numberOfSamples];
        double xdiff;
        double ydiff;
        if (angle < Math.PI / 2.0d) {
            //xdiff = Math.sin( angle ) *
            //ydiff =
        } else {
            if (angle < Math.PI) {
            } else {
                if (angle < 3.0d * Math.PI / 2.0d) {
                } else {
                }
            }
        }
        return null;
    }

    /**
     * Returns the distance between a pair of coordinates
     * @param x1 - the x coordinate of one point
     * @param y1 - the y coordinate of one point
     * @param x2 - the x coordinate of another point
     * @param y2 - the y coordinate of another point
     * @return 
     */
    public static final double distance(
            double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2.0d) + Math.pow(y1 - y2, 2.0d));
    }

    /**
     * Returns the clockwise angle in radians to the y axis of the line from x1, y1 to x2, y2
     * @param x1 - the x coordinate of one point
     * @param y1 - the y coordinate of one point
     * @param x2 - the x coordinate of another point
     * @param y2 - the y coordinate of another point
     * @return 
     */
    public static final double angle(
            double x1, double y1, double x2, double y2) {
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
     * Returns a density plot of xGrid values against yGrid values.
     * A density plot is like a scatterplot, but rather than plotting individual point these are
     * aggregated first to cells. The values of yGrid are scaled to be in the same range as the
     * values of xGrid and the number of divisions for each axis is given by divisions.
     * NB1 For this implementation xGrid and yGrid must have the same spatial frame.
     * NB2 The result returned has a set cellsize of 1 and origin at ( 0, 0 )
     *     (This enables easy comparison with other density plots)
     * @param gridFactory
     * @param divisions
     * @return 
     */
    public static Object[] densityPlot(AbstractGrid2DSquareCell xGrid, AbstractGrid2DSquareCell yGrid, int divisions, Grid2DSquareCellDoubleFactory gridFactory) {
        //        Object[] result = new Object[ 4 ];
        //        long nrows = xGrid.getNrows();
        //        long ncols = xGrid.getNcols();
        //        BigDecimal[] dimensions = xGrid.getDimensions();
        //        double xllcorner = dimensions[ 1 ].doubleValue();
        //        double yllcorner = dimensions[ 2 ].doubleValue();
        //        double xGridNoDataValue = xGrid.getNoDataValue();
        //        double yGridNoDataValue = yGrid.getNoDataValue();
        //        double minx = xGrid.getMin();
        //        double maxx = xGrid.getMax();
        //        double miny = yGrid.getMin();
        //        double maxy = yGrid.getMax();
        //        double cellsize = ( maxy - miny ) / ( double ) divisions;
        //        Grid2DSquareCellDoubleAbstract xGridRescaled = null;
        //        double value;
        //        if ( minx == miny && maxx == maxy ) {
        //            xGridRescaled = yGrid;
        //        } else {
        //            xGridRescaled = gridFactory.createGrid2DSquareCellDouble( nrows, ncols, xllcorner, yllcorner, yGrid.getCellsize(), ynoDataValue );
        //            for ( int i = 0; i < nrows * ncols; i ++ ) {
        //                value = xGrid.getCell( i );
        //                if ( value != ynoDataValue ) {
        //                    xGridRescaled.setCell( i, ( ( ( value - minx ) / ( maxx - minx ) ) * ( maxy - miny ) ) + miny );
        //                }
        //            }
        //        }
        //        //System.out.println( "yGridRescaled" );
        //        AbstractGrid2DSquareCellDouble temp1 = gridFactory.createGrid2DSquareCellDouble( divisions, divisions, minx, minx, cellsize, xnoDataValue );
        //        double x;
        //        double y;
        //        int division;
        //        double[] sumy = new double[ divisions ];
        //        double[] numy = new double[ divisions ];
        //        double[] sumysq = new double[ divisions ];
        //        for ( int j = 0; j < divisions; j ++ ) {
        //            sumy[ j ] = 0.0d;
        //            numy[ j ] = 0.0d;
        //            sumysq[ j ] = 0.0d;
        //        }
        //        for ( int i = 0; i < nrows; i ++ ) {
        //            for ( int j = 0; j < ncols; j ++ ) {
        //                x = xGridRescaled.getCell( i, j );
        //                y = yGrid.getCell( i, j );
        //                if ( y != ynoDataValue ) {
        //                    if ( x != xnoDataValue ) {
        //                        temp1.addToCell( x, y, 1.0d );
        //                        division = temp1.getColIndex( x );
        //                        if ( division == divisions ) {
        //                            division = divisions - 1;
        //                        }//System.out.println(division);
        //                        sumy[ division ] += y;
        //                        numy[ division ] += 1.0d;
        //                        sumysq[ division ] += ( y * y );
        //                    }
        //                }
        //            }
        //        }
        //        double[] stdevy = new double[ divisions ];
        //        double[] meany = new double[ divisions ];
        //        for ( int j = 0; j < divisions; j ++ ) {
        //            if ( numy[ j ] > 0.0d ) {
        //                meany[ j ] = sumy[ j ] / numy[ j ];
        //                if ( numy[ j ] > 1.0d ) {
        //                    stdevy[ j ] = Math.sqrt( ( ( numy[ j ] * sumysq[ j ] ) - ( sumy[ j ] * sumy[ j ] ) )  / ( numy[ j ] * ( numy[ j ] - 1 ) ) );
        //                }
        //            }
        //        }
        //        result[ 0 ] = stdevy;
        //        result[ 1 ] = meany;
        //        result[ 2 ] = numy;
        //        double[] normalisers = new double[ divisions ];
        //        //double d1 = 0.0d;
        //        //double d2 = 0.0d;
        //        for ( int j = 0; j < divisions; j ++ ) {
        //            normalisers[ j ] = 0.0d;
        //            for ( int i = 0; i < divisions; i ++ ) {
        //                value = temp1.getCell( i, j );
        //                if ( value != xnoDataValue ) {
        //                    normalisers[ j ] += value;
        //                    //d1 += value;
        //                    //d2 += 1.0d;
        //                }
        //            }
        //        }
        //        AbstractGrid2DSquareCellDouble densityPlotGrid = gridFactory.createGrid2DSquareCellDouble( divisions, divisions, minx, minx, cellsize, xnoDataValue );
        //        //double average = d1 / d2;
        //        for ( int i = 0; i < divisions; i ++ ) {
        //            for ( int j = 0; j < divisions; j ++ ) {
        //                if ( normalisers[ j ] != 0.0d ) {
        //                    value = temp1.getCell( i, j );
        //                    if ( value != xnoDataValue ) {
        //                        //densityPlotGrid.setCell( i, j, temp1.getCell( i, j ) / ( normalisers[ j ] + average ) );
        //                        densityPlotGrid.setCell( i, j, temp1.getCell( i, j ) / normalisers[ j ] );
        //                    }
        //                }
        //            }
        //        }
        //        densityPlotGrid.setCellsize( 1.0d );
        //        densityPlotGrid.setXllcorner( 0.0d );
        //        densityPlotGrid.setYllcorner( 0.0d );
        //        temp1.clear();
        //        if ( ! ( minx == miny && maxx == maxy ) ) {
        //            xGridRescaled.clear();
        //        }
        //        result[ 3 ] = densityPlotGrid;
        //        return result;
        return null;
    }
    /**
     * Generates a CSV file for a cumulative gains chart of observed and indicator
     * NB1. observed and indicator must have same spatial frame.
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
