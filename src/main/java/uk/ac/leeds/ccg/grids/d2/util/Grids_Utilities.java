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

import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.core.Grids_Object;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDouble;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridFactoryDouble;
import uk.ac.leeds.ccg.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.BigInteger;
import uk.ac.leeds.ccg.generic.util.Generic_Time;
import uk.ac.leeds.ccg.math.arithmetic.Math_BigDecimal;
import uk.ac.leeds.ccg.math.number.Math_BigRational;
import uk.ac.leeds.ccg.math.number.Math_BigRationalSqrt;

/**
 * Grid utility methods.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_Utilities extends Grids_Object {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of Utilities.
     *
     * @param e The grids environment.
     */
    public Grids_Utilities(Grids_Environment e) {
        super(e);
    }

    /**
     * @param time Time in milliseconds.
     * @return A string representing the number of days, hours, minutes, seconds
     * and milliseconds in the input time.
     */
    public static String getTime(long time) {
        long milliSecondsInDay = 24L * Generic_Time.MilliSecondsInHour;
        long days = Math.floorDiv(time, milliSecondsInDay);
        int t = (int) (time - (days * milliSecondsInDay));
        int hours = Math.floorDiv(t, Generic_Time.MilliSecondsInHour);
        t -= hours * Generic_Time.MilliSecondsInHour;
        int milliSecondsInMinute = Generic_Time.MilliSecondsInSecond * 60;
        int mins = Math.floorDiv(t, milliSecondsInMinute);
        t -= mins * milliSecondsInMinute;
        int secs = Math.floorDiv(t, Generic_Time.MilliSecondsInSecond);
        t -= secs * Generic_Time.MilliSecondsInSecond;
        return "days=" + days + ", hours=" + hours + ", mins=" + mins
                + ", secs=" + secs + ", millisecs=" + t;
    }

    /**
     * @param time in milliseconds.
     * @return A string representing the number of days, hours, minutes, seconds
     * and milliseconds in the input time.
     */
    public static String getTime(BigInteger time) {
        BigInteger milliSecondsInDay = BigInteger.valueOf(24L
                * Generic_Time.MilliSecondsInHour);
        BigInteger days = time.divideAndRemainder(milliSecondsInDay)[0];
        BigInteger t = time.subtract(days.multiply(milliSecondsInDay));
        BigInteger msh = BigInteger.valueOf(Generic_Time.MilliSecondsInHour);
        BigInteger hours = t.divideAndRemainder(msh)[0];
        t = t.subtract(hours.multiply(msh));
        BigInteger msm = BigInteger.valueOf(Generic_Time.MilliSecondsInSecond
                * 60);
        BigInteger mins = t.divideAndRemainder(msm)[0];
        t = t.subtract(mins.multiply(msm));
        BigInteger mss = BigInteger.valueOf(1000);
        BigInteger secs = t.divideAndRemainder(mss)[0];
        t = t.subtract(secs.multiply(mss));
        return "days=" + days + ", hours=" + hours + ", mins=" + mins
                + ", secs=" + secs + ", millisecs=" + t;
    }

    /**
     * Deprecated since we can now simply use
     * {@link java.lang.Math#nextUp(double)}.
     *
     * @param v The value for which a larger value is returned.
     * @return A number immediately larger than value in the double range.
     */
    @Deprecated
    public static double getLarger(double v) {
        return Math.nextUp(v);
    }

    /**
     * Deprecated since we can now simply use
     * {@link java.lang.Math#nextDown(double)}.
     *
     * @param v The value for which a smaller value is returned.
     * @return A number immediately smaller than value in the double range.
     */
    @Deprecated
    public static double getSmaller(double v) {
        return Math.nextDown(v);
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
     * @return Sample points.
     */
    public static Point2D.Double[][] getSamplePoints(Point2D.Double point,
            double angle, double maxDistance, int nDistances, int nAngles) {
        Point2D.Double[][] r = new Point2D.Double[nAngles][nDistances];
        double sAngle = 2.0d * Math.PI / (double) nAngles;
        double sDistance = maxDistance / (double) nDistances;
        for (int a = 0; a < nAngles; a++) {
            double sina = Math.sin(angle + sAngle * a);
            double cosa = Math.cos(angle + sAngle * a);
            double distance = maxDistance;
            int d = 0;
            while (distance >= sDistance) {
                double xdiff = sina * distance;
                double ydiff = cosa * distance;
                r[a][d] = new Point2D.Double(point.x + xdiff, point.y + ydiff);
                distance -= sDistance;
                d++;
            }
        }
        return r;
    }

    /**
     * @param x1 The x coordinate of the first point.
     * @param y1 The y coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     *
     * @return The distance squared between two points.
     */
    public static final Math_BigRational distance2(Math_BigRational x1, 
            Math_BigRational y1, Math_BigRational x2, Math_BigRational y2) {
        return ((x1.subtract(x2)).pow(2)).add((y1.subtract(y2)).pow(2));
    }
    
    /**
     * @param x1 The x coordinate of the first point.
     * @param y1 The y coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     * @param oom The Order of Magnitude initially used to calculate the square 
     * root.
     * 
     * @return The distance between two points.
     */
    public static final Math_BigRationalSqrt distance(Math_BigRational x1, 
            Math_BigRational y1, Math_BigRational x2, Math_BigRational y2,
            int oom) {
        return new Math_BigRationalSqrt(((x1.subtract(x2)).pow(2))
                .add((y1.subtract(y2)).pow(2)), oom);
    }

    /**
     * @param x1 The x coordinate of the first point.
     * @param y1 The y coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     * @return The distance between two points calculated using {@code double}
     * precision floating point numbers.
     */
    public static final double distance(double x1, double y1, double x2,
            double y2) {
        return Math.hypot((x1 - x2), (y1 - y2));
    }

    /**
     * Returns the clockwise angle in radians to the y axis of the line from:
     * {@code x1}, {@code y1}; to, {@code x2}, {@code y2}.
     *
     * @param x1 The x coordinate of the first point.
     * @param y1 The y coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     * @return The clockwise angle in radians to the y axis of the line from x1,
     * y1 to x2, y2 calculated using {@code double} precision floating point
     * numbers.
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
     * Returns a density plot of xGrid values against yGrid values.A density
     * plot is like a scatterplot, but rather than plotting individual points,
     * each point is aggregated to a cells and the result is a plot of the
     * density of points in the cells.The values of yGrid are scaled to be in
     * the same range as the values of xGrid and the number of divisions for
     * each axis is given by divisions. NB1 For this implementation xGrid and
     * yGrid must have the same spatial frame. NB2 The result returned has a set
     * cellsize of 1 and origin at ( 0, 0 ) (This enables easy comparison with
     * other density plots)
     *
     * @param xGrid The grid with x values.
     * @param yGrid The grid with y values
     * @param gp The processor.
     * @param divisions The number of divisions in the plot. This has to be less
     * than the square root of {@link Integer#MAX_VALUE} ~ 46341 but should in
     * practice be smaller than this. A sufficiently detailed picture can often
     * be produced from 512.
     * @return r[4] where:
     * <ul>
     * <li>r[0] is a = stdevy</li>
     * <li>r[1] = meany</li>
     * <li>r[2] = numy</li>
     * <li>r[3] = densityPlotGrid;</li>
     * </ul>
     * @param oom Order Of Magnitude for any rounding.
     * @param rm RoundingMode for BigDecimal arithmetic.
     *
     * @throws Exception If encountered.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    public static Object[] densityPlot(Grids_GridDouble xGrid,
            Grids_GridDouble yGrid, int divisions, Grids_Processor gp, int oom,
            RoundingMode rm)
            throws IOException, ClassNotFoundException, Exception {
        Object[] r = new Object[4];
        Grids_GridFactoryDouble gfd = gp.gridFactoryDouble;
        long nrows = xGrid.getNRows();
        long ncols = xGrid.getNCols();
        double xGridNoDataValue = xGrid.getNoDataValue();
        double yGridNoDataValue = yGrid.getNoDataValue();
        double minx = xGrid.getStats().getMin(true);
        double maxx = xGrid.getStats().getMax(true);
        double miny = yGrid.getStats().getMin(true);
        double maxy = yGrid.getStats().getMax(true);
        double cellsize = (maxy - miny) / (double) divisions;
        Grids_Dimensions newDimensions = new Grids_Dimensions(
                Math_BigRational.valueOf(minx), Math_BigRational.valueOf(maxx),
                Math_BigRational.valueOf(miny), Math_BigRational.valueOf(maxy),
                Math_BigRational.valueOf(cellsize));
        Grids_GridDouble xGridRescaled;
        double value;
        double v;
        if (minx == miny && maxx == maxy) {
            xGridRescaled = (Grids_GridDouble) gfd.create(xGrid);
        } else {
            xGridRescaled = (Grids_GridDouble) gfd.create(xGrid);
            int ncr = xGridRescaled.getNChunkRows();
            int ncc = xGridRescaled.getNChunkCols();
            for (int cr = 0; cr < ncr; cr++) {
                int cnr = xGridRescaled.getChunkNRows(cr);
                for (int cc = 0; cc < ncc; cc++) {
                    Grids_2D_ID_int cid = new Grids_2D_ID_int(cr, cc);
                    Grids_ChunkDouble chunk = xGridRescaled.getChunk(cid);
                    int cnc = xGridRescaled.getChunkNCols(cc);
                    for (int row = 0; row < cnr; row++) {
                        for (int col = 0; col < cnc; col++) {
                            value = chunk.getCell(row, col);
                            if (value != yGridNoDataValue) {
                                v = (((value - minx) / (maxx - minx))
                                        * (maxy - miny)) + miny;
                                chunk.setCell(row, col, v);
                            }
                        }
                    }
                }
            }
        }
        BigDecimal[] sumy = new BigDecimal[divisions];
        BigDecimal[] numy = new BigDecimal[divisions];
        BigDecimal[] sumysq = new BigDecimal[divisions];
        for (int j = 0; j < divisions; j++) {
            sumy[j] = BigDecimal.ZERO;
            numy[j] = BigDecimal.ZERO;
            sumysq[j] = BigDecimal.ZERO;
        }
        Grids_GridDouble temp1 = gfd.create(divisions, divisions, newDimensions);
        for (long row = 0; row < nrows; row++) {
            for (long col = 0; col < ncols; col++) {
                double x = xGridRescaled.getCell(row, col);
                double y = yGrid.getCell(row, col);
                if (y != yGridNoDataValue) {
                    if (x != xGridNoDataValue) {
                        Math_BigRational xBD = Math_BigRational.valueOf(x);
                        temp1.addToCell(xBD, Math_BigRational.valueOf(y), 1.0d);
                        int division = (int) temp1.getCol(xBD);
                        if (division >= divisions) {
                            division = divisions - 1;
                        }
                        if (division < 0) {
                            division = 0;
                        }
                        //System.out.println(division);
                        BigDecimal yd = BigDecimal.valueOf(y);
                        sumy[division] = sumy[division].add(yd);
                        numy[division] = numy[division].add(BigDecimal.ONE);
                        sumysq[division] = sumysq[division].add(yd.multiply(yd));
                    }
                }
            }
        }
        BigDecimal[] stdevy = new BigDecimal[divisions];
        BigDecimal[] meany = new BigDecimal[divisions];
        for (int j = 0; j < divisions; j++) {
            if (numy[j].compareTo(BigDecimal.ZERO) == 1) {
                meany[j] = Math_BigDecimal.divide(
                        sumy[j], numy[j], oom, rm);
                if (numy[j].compareTo(BigDecimal.ONE) == 1) {
                    stdevy[j] = Math_BigDecimal.sqrt(Math_BigDecimal.divide(
                            ((numy[j].multiply(sumysq[j])).subtract(sumy[j].multiply(sumy[j]))),
                            ((numy[j].multiply(numy[j].subtract(BigDecimal.ONE)))), oom, rm), oom, rm);
                }
            }
//            if (numy[j] > 0.0d) {
//                meany[j] = sumy[j] / numy[j];
//                if (numy[j] > 1.0d) {
//                    stdevy[j] = Math.sqrt(((numy[j] * sumysq[j])
//                            - (sumy[j] * sumy[j]))
//                            / (numy[j] * (numy[j] - 1)));
//                }
//            }
        }
        r[0] = stdevy;
        r[1] = meany;
        r[2] = numy;
        double[] normalisers = new double[divisions];
        //double d1 = 0.0d;
        //double d2 = 0.0d;
        for (int j = 0; j < divisions; j++) {
            normalisers[j] = 0.0d;
            for (int i = 0; i < divisions; i++) {
                value = temp1.getCell(i, j);
                if (value != xGridNoDataValue) {
                    normalisers[j] += value;
                    //d1 += value;
                    //d2 += 1.0d;
                }
            }
        }
        Grids_Dimensions newdimensions = new Grids_Dimensions(
                Math_BigRational.ZERO, Math_BigRational.ZERO, 
                Math_BigRational.valueOf(divisions),
                Math_BigRational.valueOf(divisions), Math_BigRational.ONE);
        Grids_GridDouble densityPlotGrid = gfd.create(divisions, divisions, newdimensions);
        //double average = d1 / d2;
        for (int i = 0; i < divisions; i++) {
            for (int j = 0; j < divisions; j++) {
                if (normalisers[j] != 0.0d) {
                    value = temp1.getCell(i, j);
                    if (value != xGridNoDataValue) {
                        //densityPlotGrid.setCell( i, j, temp1.getCell( i, j )
                        //        / ( normalisers[ j ] + average ) );
                        densityPlotGrid.setCell(i, j, temp1.getCell(i, j)
                                / normalisers[j]);
                    }
                } else {
                    densityPlotGrid.setCell(i, j, 0.0d);
                }
            }
        }
        r[3] = densityPlotGrid;
        return r;
    }

    /**
     * Generates a CSV file for a cumulative gains chart of observed and
     * indicator NB1. observed and indicator must have same spatial frame.
     */
    /*
    public static void toGainsChartCSV(Grids_Grid observed, 
    Grids_Grid indicator, int divisions, File csvFile ) {
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
