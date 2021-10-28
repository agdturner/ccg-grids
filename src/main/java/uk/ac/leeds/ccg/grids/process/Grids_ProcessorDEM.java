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
import java.util.HashSet;
import java.util.Iterator;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_long;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridNumber;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDouble;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridInt;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkInt;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridIntFactory;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDoubleSinglet;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntSinglet;
import uk.ac.leeds.ccg.grids.d2.util.Grids_Kernel;
import uk.ac.leeds.ccg.grids.d2.util.Grids_Utilities;
import java.math.BigDecimal;
import java.math.RoundingMode;
import uk.ac.leeds.ccg.math.number.Math_BigRational;
import uk.ac.leeds.ccg.math.number.Math_BigRationalSqrt;

/**
 * A class of methods relevant to the processing of Digital Elevation Model
 * Data.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ProcessorDEM extends Grids_Processor {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of Grids_ProcessorDEM.
     *
     * @param e The grids environment.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_ProcessorDEM(Grids_Environment e) throws IOException,
            ClassNotFoundException, Exception {
        super(e);
    }

    /**
     * @param g The Grids_GridNumber to be processed.
     * @param distance the distance which defines the aggregate region.
     * @param weightIntersect The kernel weighting weight at centre.
     * @param weightFactor The kernel weighting distance decay.
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then caching operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown. (NB. There are various
     * strategies to reduce bias caused by noDataValues. Here: If the cell in
     * grid for which slopeAndAspect is being calculated is a noDataValue then
     * the cells in slopeAndAspect are assigned their noDataValue. If one of the
     * cells in the calculation of slope and aspect is a noDataValue then its
     * height is taken as the nearest cell v. (Formerly the difference in its
     * height was taken as the average difference in height for those cells with
     * values.))
     * @return Grids_GridDouble[] slopeAndAspect where: slopeAndAspect[0] Is the
     * distance weighted aggregate slope over the region. This is normalised by
     * the sum of the weights used and the average distance to give a
     * proportional measure. slopeAndAspect[1] Is the distance weighted
     * aggregate aspect over the region. This is the clockwise angle from the y
     * axis (usually North). slopeAndAspect[2] Is the sine of slopeAndAspect[1].
     * slopeAndAspect[3] Is the sine of slopeAndAspect[1] + ((Pi * 1) / 8).
     * slopeAndAspect[4] Is the sine of slopeAndAspect[1] + ((Pi * 2) / 8).
     * slopeAndAspect[5] Is the sine of slopeAndAspect[1] + ((Pi * 3) / 8).
     * slopeAndAspect[6] Is the sine of slopeAndAspect[1] + ((Pi * 4) / 8).
     * slopeAndAspect[7] Is the sine of slopeAndAspect[1] + ((Pi * 5) / 8).
     * slopeAndAspect[8] Is the sine of slopeAndAspect[1] + ((Pi * 6) / 8).
     * slopeAndAspect[9] Is the sine of slopeAndAspect[1] + ((Pi * 7) / 8).
     * @param oom The Order of Magnitude for the precision.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridDouble[] getSlopeAspect(Grids_GridNumber g,
            Math_BigRationalSqrt distance, Math_BigRational weightIntersect,
            Math_BigRational weightFactor, int oom, boolean hoome)
            throws IOException, ClassNotFoundException, Exception {
        try {
            env.checkAndMaybeFreeMemory();
            String methodName = "getSlopeAspect(" + g.getClass().getName()
                    + ",double,double,double,boolean)";
            System.out.println(methodName);
            Grids_ChunkDouble cd;
            Grids_GridDouble gd;
            Grids_ChunkInt ci;
            Grids_GridInt gridInt;
            int slopeAndAspectSize = 10;
            Grids_GridDouble[] slopeAndAspect = new Grids_GridDouble[slopeAndAspectSize];
            boolean shortName = true; // Filenames that are too long are problematic!
            // Initialisation
            long ncols = g.getNCols();
            long nrows = g.getNRows();
            Grids_Dimensions dimensions = g.getDimensions();
            Math_BigRational cellsize = g.getCellsize();
            int cellDistance = g.getCellDistance(distance, oom);
            double diffX;
            double diffY;
            double diffHeight;
            double angle;
            double sinAngle;
            double cosAngle;
            double slope;
            double aspect;
            double[][] weights;
            weights = Grids_Kernel.getNormalDistributionKernelWeights(
                    cellsize, distance, oom);
            double weight;
            long row;
            long col;
            long p;
            long q;
            int cellRow;
            int cellCol;
            int cri;
            int cci;
            int chunkRows = g.getNChunkRows();
            int chunkCols = g.getNChunkCols();
            int chunkNrows;
            int chunkNcols;
            double d;
            double double1;
            double double3;
            double PI = Math.PI;
            double slopeFactor = 100.0d;
            double weightSum = 0.0d;
            double distanceSum = 0.0d;
            double numberObservations = 0.0d;
            double averageDistance;
            long long0;
            int int0;
            int int1;
            for (p = -cellDistance; p <= cellDistance; p++) {
                Math_BigRational thisY = Math_BigRational.valueOf(p)
                        .multiply(cellsize);
                for (q = -cellDistance; q <= cellDistance; q++) {
                    if (!(p == 0 && q == 0)) {
                        long0 = p + cellDistance;
                        int0 = (int) long0;
                        long0 = q + cellDistance;
                        int1 = (int) (long0);
                        Math_BigRational thisX = Math_BigRational.valueOf(q)
                                .multiply(cellsize);
                        Math_BigRationalSqrt thisDistance
                                = Grids_Utilities.distance(Math_BigRational.ZERO,
                                        Math_BigRational.ZERO, thisX, thisY, oom);
                        if (thisDistance.compareTo(distance) != 1) {
                            weight = weights[int0][int1];
                            weightSum += weight;
                            distanceSum += thisDistance.getSqrt(oom).doubleValue();
                            numberObservations++;
                        }
                    }
                }
            }
            averageDistance = distanceSum / numberObservations;
            String gName = g.getName();
            String filename;
            int noDataValueInt;
            int heightInt;
            int thisHeightInt;
            System.out.println("Initialising slopeAndAspect[0]");
            if (shortName) {
                filename = "slope_" + averageDistance;
            } else {
                filename = gName + "slopeAndAspect[slope,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            slopeAndAspect[0] = gridFactoryDouble.create(nrows, ncols,
                    dimensions);
            slopeAndAspect[0].setName(filename);
            System.out.println(slopeAndAspect[0].toString());
            System.out.println("Initialising slopeAndAspect[1]");
            if (shortName) {
                filename = "aspect_N_" + averageDistance;
            } else {
                filename = gName + "slopeAndAspect[aspect_N,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            slopeAndAspect[1] = gridFactoryDouble.create(
                    nrows, ncols, dimensions);
            slopeAndAspect[1].setName(filename);
            env.getGrids().add(slopeAndAspect[1]);
            System.out.println("Initialising slopeAndAspect[2]");
            if (shortName) {
                filename = "sin_aspect_N_" + averageDistance;
            } else {
                filename = gName + "slopeAndAspect[sin_aspect_N,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            slopeAndAspect[2] = gridFactoryDouble.create(nrows, ncols, dimensions);
            slopeAndAspect[2].setName(filename);
            System.out.println(slopeAndAspect[2].toString());
            System.out.println("Initialising slopeAndAspect[3]");
            if (shortName) {
                filename = "sin_aspect_NNE_" + averageDistance;
            } else {
                filename = gName + "slopeAndAspect[sin_aspect_NNE,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            slopeAndAspect[3] = gridFactoryDouble.create(
                    nrows, ncols, dimensions);
            slopeAndAspect[3].setName(filename);
            System.out.println(slopeAndAspect[3].toString());
            System.out.println("Initialising slopeAndAspect[4]");
            if (shortName) {
                filename = "sin_aspect_NE_" + averageDistance;
            } else {
                filename = gName + "slopeAndAspect[sin_aspect_NE,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            slopeAndAspect[4] = gridFactoryDouble.create(
                    nrows, ncols, dimensions);
            slopeAndAspect[4].setName(filename);
            System.out.println(slopeAndAspect[4].toString());
            System.out.println("Initialising slopeAndAspect[5]");
            if (shortName) {
                filename = "sin_aspect_ENE_" + averageDistance;
            } else {
                filename = gName + "slopeAndAspect[sin_aspect_ENE,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            slopeAndAspect[5] = gridFactoryDouble.create(
                    nrows, ncols, dimensions);
            slopeAndAspect[5].setName(filename);
            System.out.println(slopeAndAspect[5].toString());
            System.out.println("Initialising slopeAndAspect[6]");
            if (shortName) {
                filename = "sin_aspect_E_" + averageDistance;
            } else {
                filename = gName + "slopeAndAspect[sin_aspect_E,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            slopeAndAspect[6] = gridFactoryDouble.create(nrows, ncols, dimensions);
            slopeAndAspect[6].setName(filename);
            System.out.println(slopeAndAspect[6].toString());
            System.out.println("Initialising slopeAndAspect[7]");
            if (shortName) {
                filename = "sin_aspect_ESE_" + averageDistance;
            } else {
                filename = gName + "slopeAndAspect[sin_aspect_ESE,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            slopeAndAspect[7] = gridFactoryDouble.create(
                    nrows, ncols, dimensions);
            slopeAndAspect[7].setName(filename);
            System.out.println(slopeAndAspect[7].toString());
            System.out.println("Initialising slopeAndAspect[8]");
            if (shortName) {
                filename = "sin_aspect_SE_" + averageDistance;
            } else {
                filename = gName + "slopeAndAspect[sin_aspect_SE,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            slopeAndAspect[8] = (Grids_GridDouble) gridFactoryDouble.create(
                    nrows, ncols, dimensions);
            slopeAndAspect[8].setName(filename);
            System.out.println(slopeAndAspect[8].toString());
            System.out.println("Initialising slopeAndAspect[9]");
            if (shortName) {
                filename = "sin_aspect_SSE_" + averageDistance;
            } else {
                filename = gName + "slopeAndAspect[sin_aspect_SSE,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            slopeAndAspect[9] = (Grids_GridDouble) gridFactoryDouble.create(
                    nrows, ncols, dimensions);
            slopeAndAspect[9].setName(filename);
            System.out.println("Initialised Results");
            System.out.println(g.toString());
            Grids_2D_ID_int chunkID;
            if (g.getClass() == Grids_GridDouble.class) {
                gd = (Grids_GridDouble) g;
                double noDataValue = gd.getNoDataValue();
                double h;
                double h2;
                for (cri = 0; cri < chunkRows; cri++) {
                    for (cci = 0; cci < chunkCols; cci++) {
                        cd = (Grids_ChunkDouble) gd.getChunk(cri, cci);
                        chunkID = cd.getId();
                        env.addToNotToClear(g, chunkID);
                        env.checkAndMaybeFreeMemory();
                        chunkNrows = g.getChunkNRows(cri);
                        chunkNcols = g.getChunkNCols(cci);
                        for (cellRow = 0; cellRow < chunkNrows; cellRow++) {
                            row = g.getRow(cri, cellRow);
                            double y = g.getCellY(row).doubleValue();
                            for (cellCol = 0; cellCol < chunkNcols; cellCol++) {
                                col = g.getCol(cci, cellCol);
                                double x = g.getCellX(col).doubleValue();
                                h = cd.getCell(cellRow, cellCol);
                                if (h != noDataValue) {
                                    diffX = 0.0d;
                                    diffY = 0.0d;
                                    slope = 0.0d;
                                    weightSum = 0.0d;
                                    distanceSum = 0.0d;
                                    numberObservations = 0.0d;
                                    for (p = -cellDistance; p <= cellDistance; p++) {
                                        long0 = row + p;
                                        Math_BigRational thisY = g.getCellY(long0);
                                        for (q = -cellDistance; q <= cellDistance; q++) {
                                            if (!(p == 0 && q == 0)) {
                                                Math_BigRational thisX = Math_BigRational.valueOf(q).multiply(cellsize);
                                                Math_BigRationalSqrt thisDistance = Grids_Utilities.distance(Math_BigRational.ZERO,
                                                        Math_BigRational.ZERO, thisX, thisY, oom);
                                                if (thisDistance.compareTo(distance) != 1) {
                                                    h2 = gd.getCell(thisX, thisY);
                                                    if (h2 != noDataValue) {
                                                        long0 = p + cellDistance;
                                                        int0 = (int) long0;
                                                        long0 = q + cellDistance;
                                                        int1 = (int) (long0);
                                                        weight = weights[int0][int1];
                                                        weightSum += weight;
                                                        distanceSum += thisDistance.toBigDecimal(oom).doubleValue();
                                                        numberObservations++;
                                                        d = h - h2;
                                                        diffHeight = d * weight;
                                                        d = x - thisX.doubleValue();
                                                        diffX += d * diffHeight;
                                                        d = y - thisY.doubleValue();
                                                        diffY += d * diffHeight;
                                                        slope += diffHeight;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (numberObservations > 0) {
                                        averageDistance = (distanceSum / numberObservations);
                                        d = weightSum * averageDistance;
                                        slope /= d;
                                        slope *= slopeFactor;
                                        slopeAndAspect[0].setCell(row, col, slope);
                                        d = x + diffX;
                                        double1 = y + diffY;
                                        angle = Grids_Utilities.angle(x, y, d, double1);
                                        slopeAndAspect[1].setCell(row, col, angle);
                                        sinAngle = Math.sin(angle);
                                        slopeAndAspect[2].setCell(row, col, sinAngle);
                                        double3 = angle + (PI / 8.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[3].setCell(row, col, sinAngle);
                                        double3 = angle + (PI / 4.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[4].setCell(row, col, sinAngle);
                                        double3 = angle + (PI * 3.0d / 8.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[5].setCell(row, col, sinAngle);
                                        double3 = angle + (PI / 2.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[6].setCell(row, col, sinAngle);
                                        double3 = angle + (PI * 5.0d / 8.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[7].setCell(row, col, sinAngle);
                                        double3 = angle + (PI * 6.0d / 8.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[8].setCell(row, col, sinAngle);
                                        double3 = angle + (PI * 7.0d / 8.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[9].setCell(row, col, sinAngle);
                                    }
                                }
                            }
                        }
                        env.removeFromNotToClear(g, chunkID);
                        System.out.println("Done Chunk (" + cri + ", " + cci + ")");
                    }
                }
            } else {
                // (g.getClass() == Grids_GridInt.class)
                gridInt = (Grids_GridInt) g;
                noDataValueInt = gridInt.getNoDataValue();
//                Grids_GridIntIterator ite;
//                ite = gridInt.iterator();
//                Grids_AbstractGridChunkNumberRowMajorOrderIterator chunkIte;
//                while (ite.hasNext()) {
//                    chunkIte = ite.getChunkIterator();
//                    chunkIte.
//                }
                for (cri = 0; cri < chunkRows; cri++) {
                    chunkNrows = g.getChunkNRows(cri);
                    for (cci = 0; cci < chunkCols; cci++) {
                        chunkNcols = g.getChunkNCols(cci);
                        ci = (Grids_ChunkInt) gridInt.getChunk(cri, cci);
                        chunkID = ci.getId();
                        env.addToNotToClear(g, chunkID);
                        env.checkAndMaybeFreeMemory();
                        for (cellRow = 0; cellRow < chunkNrows; cellRow++) {
                            row = g.getRow(cri, cellRow);
                            Math_BigRational y = g.getCellY(row);
                            for (cellCol = 0; cellCol < chunkNcols; cellCol++) {
                                env.checkAndMaybeFreeMemory();
                                col = g.getCol(cci, cellCol);
                                Math_BigRational x = g.getCellX(col);
                                heightInt = ci.getCell(cellRow, cellCol);
                                if (heightInt != noDataValueInt) {
                                    diffX = 0.0d;
                                    diffY = 0.0d;
                                    slope = 0.0d;
                                    weightSum = 0.0d;
                                    distanceSum = 0.0d;
                                    numberObservations = 0.0d;
                                    for (p = -cellDistance; p <= cellDistance; p++) {
                                        long0 = row + p;
                                        Math_BigRational thisY = g.getCellY(long0);
                                        for (q = -cellDistance; q <= cellDistance; q++) {
                                            if (!(p == 0 && q == 0)) {
                                                long0 = col + q;
                                                Math_BigRational thisX = g.getCellX(long0);
                                                Math_BigRationalSqrt thisDistance = Grids_Utilities.distance(x, y, thisX, thisY, oom);
                                                if (thisDistance.compareTo(distance) != 1) {
                                                    thisHeightInt = gridInt.getCell(
                                                            thisX, thisY);
                                                    if (thisHeightInt != noDataValueInt) {
                                                        long0 = p + cellDistance;
                                                        int0 = (int) long0;
                                                        long0 = q + cellDistance;
                                                        int1 = (int) (long0);
                                                        weight = weights[int0][int1];
                                                        weightSum += weight;
                                                        distanceSum += thisDistance.toBigDecimal(oom).doubleValue();
                                                        numberObservations++;
                                                        d = (double) heightInt - thisHeightInt;
                                                        diffHeight = d * weight;
                                                        d = x.subtract(thisX).doubleValue();
                                                        diffX += d * diffHeight;
                                                        d = y.subtract(thisY).doubleValue();
                                                        diffY += d * diffHeight;
                                                        slope += diffHeight;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (numberObservations > 0) {
                                        averageDistance = (distanceSum / numberObservations);
                                        d = weightSum * averageDistance;
                                        slope /= d;
                                        slope *= slopeFactor;
                                        slopeAndAspect[0].setCell(row, col, slope);
                                        d = x.doubleValue() + diffX;
                                        double1 = y.doubleValue() + diffY;
                                        angle = Grids_Utilities.angle(x.doubleValue(), y.doubleValue(), d, double1);
                                        slopeAndAspect[1].setCell(row, col, angle);
                                        sinAngle = Math.sin(angle);
                                        slopeAndAspect[2].setCell(row, col, sinAngle);
                                        double3 = angle + (PI / 8.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[3].setCell(row, col, sinAngle);
                                        double3 = angle + (PI / 4.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[4].setCell(row, col, sinAngle);
                                        double3 = angle + (PI * 3.0d / 8.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[5].setCell(row, col, sinAngle);
                                        double3 = angle + (PI / 2.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[6].setCell(row, col, sinAngle);
                                        double3 = angle + (PI * 5.0d / 8.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[7].setCell(row, col, sinAngle);
                                        double3 = angle + (PI * 6.0d / 8.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[8].setCell(row, col, sinAngle);
                                        double3 = angle + (PI * 7.0d / 8.0d);
                                        sinAngle = Math.sin(double3);
                                        slopeAndAspect[9].setCell(row, col, sinAngle);
                                    }
                                }
                            }
                        }
                        env.removeFromNotToClear(g, chunkID);
                        System.out.println("Done Chunk (" + cri + ", " + cci + ")");
                    }
                }
            }
            return slopeAndAspect;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                if (!env.swapChunk(env.HOOMEF)) {
                    throw e;
                }
                env.initMemoryReserve(env.env);
                return getSlopeAspect(g, distance, weightIntersect, weightFactor, oom, hoome);
            }
            throw e;
        }
    }

    /**
     * Returns a double[] slopeAndAspect where: slopeAndAspect[0] is the
     * aggregate slope over the region weighted by distance, weightIntersect and
     * weightFactor; slopeAndAspect[1] is the aggregate aspect over the region
     * weighted by distance, weightIntersect and weightFactor. This is the
     * clockwise angle from north. slopeAndAspect[2] is the aggregate aspect
     * over the region weighted by distance, weightIntersect and weightFactor.
     * This is the sine of the clockwise angle from north. slopeAndAspect[3] is
     * the aggregate aspect over the region weighted by distance,
     * weightIntersect and weightFactor. This is the cosine of the clockwise
     * angle from north.
     *
     * @param g the Grids_GridDouble to be processed.
     * @param x the x coordinate from where the aspect is calculated
     * @param y the y coordinate from where the aspect is calculated
     * @param distance the distance which defines the aggregate region.
     * @param wi The weight intersect - the kernel weighting weight at centre.
     * @param wf The weight factor - the kernel weighting distance decay.
     * @return The slope and aspect.
     * @param oom The Order of Magnitude for the precision.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected double[] getSlopeAspect(Grids_GridNumber g, Math_BigRational x,
            Math_BigRational y, Math_BigRationalSqrt distance,
            Math_BigRational wi, int wf, int oom)
            throws IOException, ClassNotFoundException, Exception {
        return getSlopeAspect(g, g.getRow(y), g.getCol(x), x, y, distance, wi,
                wf, oom);
    }

    /**
     * Returns a double[] slopeAndAspect where: slopeAndAspect[0] is the
     * aggregate slope over the region weighted by distance, weightIntersect and
     * weightFactor; slopeAndAspect[1] is the aggregate aspect over the region
     * weighted by distance, weightIntersect and weightFactor. This is the
     * clockwise angle from north. slopeAndAspect[2] is the aggregate aspect
     * over the region weighted by distance, weightIntersect and weightFactor.
     * This is the sine of the clockwise angle from north. slopeAndAspect[3] is
     * the aggregate aspect over the region weighted by distance,
     * weightIntersect and weightFactor. This is the cosine of the clockwise
     * angle from north.
     *
     * @param g The Grids_GridDouble to be processed
     * @param rowIndex the rowIndex where the result is calculated
     * @param colIndex the colIndex where the result is calculated
     * @param x the x coordinate from where the aspect is calculated
     * @param y the y coordinate from where the aspect is calculated
     * @param distance the distance which defines the region
     * @param wi The weight intersect.
     * @param wf The weight factor. NB. If grid.getCell(x, y) ==
     * grid.getNoDataValue() then; result[0] = grid.getNoDataValue() result[1] =
     * grid.getNoDataValue()
     * @param oom The Order of Magnitude for the precision.
     * @return Slope and aspect.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected double[] getSlopeAspect(Grids_GridNumber g, long rowIndex,
            long colIndex, Math_BigRational x, Math_BigRational y,
            Math_BigRationalSqrt distance, Math_BigRational wi, int wf, int oom)
            throws IOException, ClassNotFoundException, Exception {
        env.getGrids().add(g);
        if (g.getClass() == Grids_GridInt.class) {
            Grids_GridInt gi = (Grids_GridInt) g;
            int noDataValue = gi.getNoDataValue();
            double[] slopeAndAspect = new double[2];
            slopeAndAspect[0] = noDataValue;
            slopeAndAspect[1] = noDataValue;
            slopeAndAspect[2] = noDataValue;
            slopeAndAspect[3] = noDataValue;
            getSlopeAspect(slopeAndAspect, g, x, y, distance, wi, wf, oom);
            return slopeAndAspect;
        } else {
            // (g.getClass() == Grids_GridDouble.class)
            Grids_GridDouble gd = (Grids_GridDouble) g;
            double noDataValue = gd.getNoDataValue();
            double[] slopeAndAspect = new double[2];
            slopeAndAspect[0] = noDataValue;
            slopeAndAspect[1] = noDataValue;
            slopeAndAspect[2] = noDataValue;
            slopeAndAspect[3] = noDataValue;
            getSlopeAspect(slopeAndAspect, g, x, y, distance, wi, wf, oom);
            return slopeAndAspect;
        }
    }

    /**
     * @param slopeAndAspect slopeAndAspect
     * @param g g
     * @param x x
     * @param y y
     * @param distance distance
     * @param wi wi
     * @param wf wf
     * @param oom The Order of Magnitude for the precision.
     * @throws Exception If encountered.
     */
    protected void getSlopeAspect(double[] slopeAndAspect, Grids_GridNumber g,
            Math_BigRational x, Math_BigRational y,
            Math_BigRationalSqrt distance, Math_BigRational wi, int wf, int oom)
            throws Exception {
        BigDecimal height = g.getCellBigDecimal(x, y);
        if (height.compareTo(g.ndv) != 0) {
            int cellDistance = g.getCellDistance(distance, oom);
            Math_BigRational diffX = Math_BigRational.ZERO;
            Math_BigRational diffY = Math_BigRational.ZERO;
            Math_BigRational slope = Math_BigRational.ZERO;
            // Calculate slope and aspect
            Math_BigRational d = distance.getSqrt(oom);
            for (long p = -cellDistance; p <= cellDistance; p++) {
                Math_BigRational thisY = y.add(Math_BigRational.valueOf(p).multiply(d));
                for (long q = -cellDistance; q <= cellDistance; q++) {
                    Math_BigRational thisX = x.add(Math_BigRational.valueOf(q).multiply(d));
                    Math_BigRationalSqrt thisDistance = Grids_Utilities.distance(x, y, thisX, thisY, oom);
                    if (thisDistance.compareTo(distance) != 1) {
                        BigDecimal weight = Grids_Kernel.getKernelWeight(distance,
                                wi, wf, thisDistance, oom).toBigDecimal(oom);
                        BigDecimal thisHeight = g.getCellBigDecimal(thisX, thisY);
                        //thisHeight = gi.getNearestValueDouble(thisX, thisY, hoome);
                        if (thisHeight.compareTo(g.ndv) != 0) {
                            Math_BigRational diffHeight = Math_BigRational.valueOf((height.subtract(thisHeight)).multiply(weight));
                            diffX = diffX.add((x.subtract(thisX)).multiply(diffHeight));
                            diffY = diffY.add((y.subtract(thisY)).multiply(diffHeight));
                            slope = slope.add(diffHeight);
                        }
                    }
                }
            }
            slopeAndAspect[0] = slope.doubleValue();
            double angle = Grids_Utilities.angle(x.doubleValue(),
                    y.doubleValue(), x.add(diffX).doubleValue(),
                    y.add(diffY).doubleValue());
            slopeAndAspect[1] = angle;
            slopeAndAspect[2] = Math.sin(angle);
            slopeAndAspect[3] = Math.cos(angle);
        }
    }

    /**
     * @param g Grids_GridNumber to be processed.
     * @param gdf The grids double factory.
     * @param outflowHeight The outflowHeight
     * @param maxIterations Maximum iterations.
     * @param treatNoDataValueAsOutflow If true the ndv cells are treated as
     * outflow cells.
     * @param outflowCellIDsSet The set of outflow cells.
     * @return Grids_GridDouble which has cell values as in _Grid2DSquareCell
     * except with hollows raised. The attempt to raise hollows may not remove
     * all hollows. The process of removing hollows works iteratively.
     * Essentially, the algorithm is as follows:
     * <ol>
     * <li>Identify all hollows.</li>
     * <li>Raise all hollows by a small amount.</li>
     * <li>Identify all hollows.</li>
     * <li>Trace bottom of each hollow and raise to the height of the lowest
     * cell around it.</li>
     * <li>Repeat 2 to 5 until there are no hollows or until maxIterations
     * reached.</li>
     * </ol>
     * This algorithm was optimised by processing each hollow in turn and
     * dealing with the situation around each hollow.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridDouble getHollowFilledDEM(Grids_GridNumber g,
            Grids_GridDoubleFactory gdf, double outflowHeight,
            int maxIterations, HashSet<Grids_2D_ID_long> outflowCellIDsSet,
            boolean treatNoDataValueAsOutflow) throws IOException,
            ClassNotFoundException, Exception {
        env.getGrids().add(g);
        Grids_GridDouble res = (Grids_GridDouble) gdf.create(g);
        String rName = "HollowFilledDEM_" + maxIterations;
        res.setName(rName);
        long nRows = res.getNRows();
        long nCols = res.getNCols();
        double minHeight = res.getStats().getMin(true);
        if (outflowHeight < minHeight) {
            outflowHeight = minHeight;
        }
        if (g.getClass() == Grids_GridInt.class) {
            Grids_GridInt gi = (Grids_GridInt) g;
            int noDataValue = gi.getNoDataValue();
            double height;
            // Initialise outflowCellIDs
            HashSet<Grids_2D_ID_long> outflowCellIDs
                    = getHollowFilledDEMOutflowCellIDs(outflowCellIDsSet,
                            outflowHeight, gi, nRows, nCols,
                            treatNoDataValueAsOutflow);
            // Initialise hollowsHashSet
            HashSet<Grids_2D_ID_long> hollowsHashSet
                    = getHollowFilledDEMInitialHollowsHashSet(gi, nRows, nCols,
                            treatNoDataValueAsOutflow);
            // Remove outflowCellIDs from hollowsHashSet
            hollowsHashSet.removeAll(outflowCellIDs);
            HashSet<Grids_2D_ID_long> hollows2 = hollowsHashSet;
            int numberOfHollows = hollowsHashSet.size();
            boolean calculated1 = false;
            boolean calculated2;
            if (numberOfHollows == 0) {
                calculated1 = true;
            }
            int iteration1 = 0;
            Iterator<Grids_2D_ID_long> ite1;
            Iterator<Grids_2D_ID_long> ite2;
            Grids_2D_ID_long[] cellIDs = new Grids_2D_ID_long[3];
            double height0;
            int noDataCount;
            int outflowCellCount;
            // Fill in hollows
            while (!calculated1) {
                if (iteration1 < maxIterations) {
                    iteration1++;
                    numberOfHollows = hollows2.size();
                    System.out.println("Iteration " + iteration1
                            + " out of a maximum " + maxIterations
                            + ": Number of hollows " + numberOfHollows);
                    if (numberOfHollows > 0) {
                        HashSet<Grids_2D_ID_long> visitedSet1 = new HashSet<>();
                        HashSet<Grids_2D_ID_long> hollowsVisited = new HashSet<>();
                        //hollowsVisited.addAll(outflowCellIDs);
                        // Raise all hollows by a small amount
                        setLarger(res, hollows2);
                        // Recalculate hollows in hollows2 neighbourhood
                        HashSet<Grids_2D_ID_long> toVisitSet1 = new HashSet<>();
                        ite1 = hollows2.iterator();
                        while (ite1.hasNext()) {
                            cellIDs[0] = ite1.next();
                            long row = cellIDs[0].getRow();
                            long col = cellIDs[0].getCol();
                            for (long p = -1; p < 2; p++) {
                                for (long q = -1; q < 2; q++) {
                                    //if (! (p == 0 && q == 0)) {
                                    if (g.isInGrid(row + p, col + q)) {
                                        toVisitSet1.add(g.getCellID(row + p,
                                                col + q));
                                    }
                                    //}
                                }
                            }
                        }
                        HashSet<Grids_2D_ID_long> hollows1
                                = getHollowsInNeighbourhood(res, toVisitSet1,
                                        treatNoDataValueAsOutflow);
                        hollows1.removeAll(outflowCellIDs);
                        hollows2.clear();
                        toVisitSet1.clear();
                        /*
                             hollows1 = getHollowFilledDEMCalculateHollowsInNeighbourhood(
                                    res, hollows2);
                             hollows1.removeAll(outflowCellIDs);
                             hollows2.clear();
                         */
                        // Trace bottom of each hollow and raise to the height of the lowest cell around it.
                        ite1 = hollows1.iterator();
                        while (ite1.hasNext()) {
                            cellIDs[0] = ite1.next();
                            if (!hollowsVisited.contains(cellIDs[0])) {
                                HashSet<Grids_2D_ID_long> hollowSet = new HashSet<>();
                                hollowSet.add(cellIDs[0]);
                                long row = cellIDs[0].getRow();
                                long col = cellIDs[0].getCol();
                                toVisitSet1 = new HashSet<>();
                                // Step 1: Add all cells in adjoining hollows to hollowSet
                                for (long p = -1; p < 2; p++) {
                                    for (long q = -1; q < 2; q++) {
                                        if (!(p == 0 && q == 0)) {
                                            if (g.isInGrid(row + p, col + q)) {
                                                cellIDs[1] = g.getCellID(
                                                        row + p, col + q);
                                                toVisitSet1.add(cellIDs[1]);
                                            }
                                        }
                                    }
                                }
                                toVisitSet1.removeAll(outflowCellIDs);
                                HashSet<Grids_2D_ID_long> visitedSet2 = new HashSet<>();
                                visitedSet2.add(cellIDs[0]);
                                HashSet<Grids_2D_ID_long> toVisitSet3 = new HashSet<>();
                                toVisitSet3.addAll(toVisitSet1);
                                calculated2 = false;
                                while (!calculated2) {
                                    HashSet<Grids_2D_ID_long> toVisitSet2 = new HashSet<>();
                                    ite2 = toVisitSet1.iterator();
                                    while (ite2.hasNext()) {
                                        cellIDs[1] = ite2.next();
                                        visitedSet2.add(cellIDs[1]);
                                        row = cellIDs[1].getRow();
                                        col = cellIDs[1].getCol();
                                        for (long p = -1; p < 2; p++) {
                                            for (long q = -1; q < 2; q++) {
                                                if (!(p == 0 && q == 0)) {
                                                    if (g.isInGrid(row + p, col + q)) {
                                                        cellIDs[2] = g.getCellID(row + p, col + q);
                                                        visitedSet1.add(cellIDs[2]);
                                                        // If a hollow then add to hollow set and visit neighbours if not done already
                                                        if (hollows1.contains(cellIDs[2])) {
                                                            hollowSet.add(cellIDs[2]);
                                                            for (long r = -1; r < 2; r++) {
                                                                for (long s = -1; s < 2; s++) {
                                                                    if (!(r == 0 && s == 0)) { // Is this correct?
                                                                        if (g.isInGrid(row + p + r, col + q + s)) {
                                                                            toVisitSet2.add(g.getCellID(row + p + r, col + q + s));
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    toVisitSet2.removeAll(outflowCellIDs);
                                    toVisitSet3.addAll(toVisitSet2);
                                    toVisitSet1 = toVisitSet2;
                                    toVisitSet1.removeAll(visitedSet2);
                                    if (toVisitSet1.isEmpty()) {
                                        calculated2 = true;
                                    }
                                }
                                // Step 2 Examine neighbours of each hollow
                                toVisitSet3.removeAll(hollowSet);
                                // NB. toVisitSet3 contains all cells which neighbour the traced hollow
                                calculated2 = false;
                                minHeight = Double.MAX_VALUE;
                                height0 = res.getCell(row, col);
                                while (!calculated2) {
                                    HashSet<Grids_2D_ID_long> toVisitSet2 = new HashSet<>();
                                    //toVisitSet2.addAll(toVisitSet3);
                                    ite2 = toVisitSet3.iterator();
                                    noDataCount = 0;
                                    outflowCellCount = 0;
                                    // Step 2.1 Calculate height of the lowest neighbour minHeight // (that is not an outflow cell???)
                                    while (ite2.hasNext()) {
                                        cellIDs[1] = ite2.next();
                                        row = cellIDs[1].getRow();
                                        col = cellIDs[1].getCol();
                                        height = res.getCell(row, col);
                                        if (height == noDataValue) {
                                            noDataCount++;
                                        } else {
                                            if (outflowCellIDs.contains(cellIDs[1])) {
                                                outflowCellCount++;
                                            } else {
                                                minHeight = Math.min(minHeight, height);
                                            }
                                            // Is this correct?
                                            //minHeight = Math.min(minHeight, height);
                                        }
                                    }
                                    if (noDataCount + outflowCellCount == toVisitSet3.size()) {
                                        // env.println("Hollow surrounded by noDataValue or outflow cells!!!");
                                        // Add _CellIDs of this hollow to outflowCellIDs so that it is not revisited.
                                        outflowCellIDs.addAll(hollowSet);
                                        calculated2 = true;
                                    } else {
                                        // Step 2.2 Treat cells:
                                        // If minHeight is higher then add cells with this height to the
                                        // hollow set and their neighbours to toVisitSet2
                                        if (minHeight > height0) {
                                            ite2 = toVisitSet3.iterator();
                                            while (ite2.hasNext()) {
                                                cellIDs[1] = ite2.next();
                                                row = cellIDs[1].getRow();
                                                col = cellIDs[1].getCol();
                                                height = res.getCell(row, col);
                                                if (height == minHeight) {
                                                    hollowSet.add(cellIDs[1]);
                                                    toVisitSet2.remove(cellIDs[1]);
                                                    for (long r = -1; r < 2; r++) {
                                                        for (long s = -1; s < 2; s++) {
                                                            if (!(r == 0L && s == 0L)) {
                                                                if (g.isInGrid(row + r, col + s)) {
                                                                    toVisitSet2.add(g.getCellID(row + r, col + s));
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            height0 = minHeight;
                                            toVisitSet2.removeAll(hollowSet);
                                            //toVisitSet2.removeAll(outflowCellIDs);
                                            toVisitSet3 = toVisitSet2;
                                        } else {
                                            calculated2 = true;
                                        }
                                    }
                                }
                                // Step 3 Raise all cells in hollowSet
                                hollowSet.removeAll(outflowCellIDs);
                                ite2 = hollowSet.iterator();
                                while (ite2.hasNext()) {
                                    cellIDs[1] = ite2.next();
                                    row = cellIDs[1].getRow();
                                    col = cellIDs[1].getCol();
                                    res.setCell(row, col, Math.nextUp(height0));
                                }
                                hollowsVisited.addAll(hollowSet);
                                visitedSet1.addAll(hollowSet);
                            }
                        }
                        hollows2 = getHollowsInNeighbourhood(res,
                                visitedSet1, treatNoDataValueAsOutflow);
                    } else {
                        calculated1 = true;
                    }
                } else {
                    calculated1 = true;
                }
            }
        } else {
            // (g.getClass() == Grids_GridDouble.class)
            Grids_GridDouble gd = (Grids_GridDouble) g;
            double heightDouble;
            double resultNoDataValue = res.getNoDataValue();
            // Initialise outflowCellIDs
            HashSet<Grids_2D_ID_long> outflowCellIDs
                    = getHollowFilledDEMOutflowCellIDs(outflowCellIDsSet,
                            outflowHeight, gd, nRows, nCols,
                            treatNoDataValueAsOutflow);
            // Initialise hollowsHashSet
            HashSet<Grids_2D_ID_long> hollowsHashSet
                    = getHollowFilledDEMInitialHollowsHashSet(gd, nRows, nCols,
                            treatNoDataValueAsOutflow);
            // Remove outflowCellIDs from hollowsHashSet
            hollowsHashSet.removeAll(outflowCellIDs);
            HashSet<Grids_2D_ID_long> hollows2 = hollowsHashSet;
            int numberOfHollows = hollowsHashSet.size();
            boolean calculated1 = false;
            boolean calculated2;
            if (numberOfHollows == 0) {
                calculated1 = true;
            }
            int iteration1 = 0;
            Iterator<Grids_2D_ID_long> ite1;
            Iterator<Grids_2D_ID_long> ite2;
            Grids_2D_ID_long[] cellIDs = new Grids_2D_ID_long[3];
            double height0;
            int noDataCount;
            int outflowCellCount;
            // Fill in hollows
            while (!calculated1) {
                if (iteration1 < maxIterations) {
                    iteration1++;
                    numberOfHollows = hollows2.size();
                    System.out.println("Iteration " + iteration1
                            + " out of a maximum " + maxIterations
                            + ": Number of hollows " + numberOfHollows);
                    if (iteration1 > 100) {
                        boolean _DEBUG;
                    }
                    if (numberOfHollows > 0) {
                        HashSet<Grids_2D_ID_long> visitedSet1 = new HashSet<>();
                        HashSet<Grids_2D_ID_long> hollowsVisited = new HashSet<>();
                        //hollowsVisited.addAll(outflowCellIDs);
                        // Raise all hollows by a small amount
                        setLarger(res, hollows2);
                        // Recalculate hollows in hollows2 neighbourhood
                        HashSet<Grids_2D_ID_long> toVisitSet1 = new HashSet<>();
                        ite1 = hollows2.iterator();
                        while (ite1.hasNext()) {
                            cellIDs[0] = ite1.next();
                            long row = cellIDs[0].getRow();
                            long col = cellIDs[0].getCol();
                            for (long p = -1; p < 2; p++) {
                                for (long q = -1; q < 2; q++) {
                                    //if (! (p == 0 && q == 0)) {
                                    if (g.isInGrid(row + p, col + q)) {
                                        toVisitSet1.add(g.getCellID(row + p, col + q));
                                    }
                                    //}
                                }
                            }
                        }
                        HashSet<Grids_2D_ID_long> hollows1
                                = getHollowsInNeighbourhood(res, toVisitSet1,
                                        treatNoDataValueAsOutflow);
                        hollows1.removeAll(outflowCellIDs);
                        hollows2.clear();
                        toVisitSet1.clear();
                        /*
                             hollows1 = getHollowFilledDEMCalculateHollowsInNeighbourhood(result, hollows2);
                             hollows1.removeAll(outflowCellIDs);
                             hollows2.clear();
                         */
                        // Trace bottom of each hollow and raise to the height of the lowest cell around it.
                        ite1 = hollows1.iterator();
                        while (ite1.hasNext()) {
                            cellIDs[0] = ite1.next();
                            if (!hollowsVisited.contains(cellIDs[0])) {
                                HashSet<Grids_2D_ID_long> hollowSet = new HashSet<>();
                                hollowSet.add(cellIDs[0]);
                                long row = cellIDs[0].getRow();
                                long col = cellIDs[0].getCol();
                                toVisitSet1 = new HashSet<>();
                                // Step 1: Add all cells in adjoining hollows to hollowSet
                                for (long p = -1; p < 2; p++) {
                                    for (long q = -1; q < 2; q++) {
                                        if (!(p == 0 && q == 0)) {
                                            if (g.isInGrid(row + p, col + q)) {
                                                cellIDs[1] = g.getCellID(row + p, col + q);
                                                toVisitSet1.add(cellIDs[1]);
                                            }
                                        }
                                    }
                                }
                                toVisitSet1.removeAll(outflowCellIDs);
                                HashSet<Grids_2D_ID_long> visitedSet2 = new HashSet<>();
                                visitedSet2.add(cellIDs[0]);
                                HashSet<Grids_2D_ID_long> toVisitSet3 = new HashSet<>();
                                toVisitSet3.addAll(toVisitSet1);
                                calculated2 = false;
                                while (!calculated2) {
                                    HashSet<Grids_2D_ID_long> toVisitSet2 = new HashSet<>();
                                    ite2 = toVisitSet1.iterator();
                                    while (ite2.hasNext()) {
                                        cellIDs[1] = ite2.next();
                                        visitedSet2.add(cellIDs[1]);
                                        row = cellIDs[1].getRow();
                                        col = cellIDs[1].getCol();
                                        for (long p = -1; p < 2; p++) {
                                            for (long q = -1; q < 2; q++) {
                                                if (!(p == 0 && q == 0)) {
                                                    if (g.isInGrid(row + p, col + q)) {
                                                        cellIDs[2] = g.getCellID(row + p, col + q);
                                                        visitedSet1.add(cellIDs[2]);
                                                        // If a hollow then add to hollow set and visit neighbours if not done already
                                                        if (hollows1.contains(cellIDs[2])) {
                                                            hollowSet.add(cellIDs[2]);
                                                            for (long r = -1; r < 2; r++) {
                                                                for (long s = -1; s < 2; s++) {
                                                                    if (!(r == 0 && s == 0)) { // Is this correct?
                                                                        if (g.isInGrid(row + p + r, col + q + s)) {
                                                                            toVisitSet2.add(g.getCellID(row + p + r, col + q + s));
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    toVisitSet2.removeAll(outflowCellIDs);
                                    toVisitSet3.addAll(toVisitSet2);
                                    toVisitSet1 = toVisitSet2;
                                    toVisitSet1.removeAll(visitedSet2);
                                    if (toVisitSet1.isEmpty()) {
                                        calculated2 = true;
                                    }
                                }
                                // Step 2 Examine neighbours of each hollow
                                toVisitSet3.removeAll(hollowSet);
                                // NB. toVisitSet3 contains all cells which neighbour the traced hollow
                                calculated2 = false;
                                minHeight = Double.MAX_VALUE;
                                height0 = res.getCell(row, col);
                                while (!calculated2) {
                                    HashSet<Grids_2D_ID_long> toVisitSet2 = new HashSet<>();
                                    //toVisitSet2.addAll(toVisitSet3);
                                    ite2 = toVisitSet3.iterator();
                                    noDataCount = 0;
                                    outflowCellCount = 0;
                                    // Step 2.1 Calculate height of the lowest neighbour minHeight // (that is not an outflow cell???)
                                    while (ite2.hasNext()) {
                                        cellIDs[1] = ite2.next();
                                        row = cellIDs[1].getRow();
                                        col = cellIDs[1].getCol();
                                        heightDouble = res.getCell(row, col);
                                        if (heightDouble == resultNoDataValue) {
                                            noDataCount++;
                                        } else {
                                            if (outflowCellIDs.contains(cellIDs[1])) {
                                                outflowCellCount++;
                                            } else {
                                                minHeight = Math.min(minHeight, heightDouble);
                                            }
                                            // Is this correct?
                                            //minHeight = Math.min(minHeight, heightDouble);
                                        }
                                    }
                                    if (noDataCount + outflowCellCount == toVisitSet3.size()) {
                                        // env.println("Hollow surrounded by noDataValue or outflow cells!!!");
                                        // Add _CellIDs of this hollow to outflowCellIDs so that it is not revisited.
                                        outflowCellIDs.addAll(hollowSet);
                                        calculated2 = true;
                                    } else {
                                        // Step 2.2 Treat cells:
                                        // If minHeight is higher then add cells with this height to the
                                        // hollow set and their neighbours to toVisitSet2
                                        if (minHeight > height0) {
                                            ite2 = toVisitSet3.iterator();
                                            while (ite2.hasNext()) {
                                                cellIDs[1] = ite2.next();
                                                row = cellIDs[1].getRow();
                                                col = cellIDs[1].getCol();
                                                heightDouble = res.getCell(row, col);
                                                if (heightDouble == minHeight) {
                                                    hollowSet.add(cellIDs[1]);
                                                    toVisitSet2.remove(cellIDs[1]);
                                                    for (long r = -1; r < 2; r++) {
                                                        for (long s = -1; s < 2; s++) {
                                                            if (!(r == 0L && s == 0L)) {
                                                                if (g.isInGrid(row + r, col + s)) {
                                                                    toVisitSet2.add(g.getCellID(row + r, col + s));
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            height0 = minHeight;
                                            toVisitSet2.removeAll(hollowSet);
                                            //toVisitSet2.removeAll(outflowCellIDs);
                                            toVisitSet3 = toVisitSet2;
                                        } else {
                                            calculated2 = true;
                                        }
                                    }
                                }
                                // Step 3 Raise all cells in hollowSet
                                hollowSet.removeAll(outflowCellIDs);
                                ite2 = hollowSet.iterator();
                                while (ite2.hasNext()) {
                                    cellIDs[1] = ite2.next();
                                    row = cellIDs[1].getRow();
                                    col = cellIDs[1].getCol();
                                    res.setCell(row, col, Math.nextUp(height0));
                                }
                                hollowsVisited.addAll(hollowSet);
                                visitedSet1.addAll(hollowSet);
                            }
                        }
                        hollows2 = getHollowsInNeighbourhood(res, visitedSet1,
                                treatNoDataValueAsOutflow);
                    } else {
                        calculated1 = true;
                    }
                } else {
                    calculated1 = true;
                }
            }
        }
        return res;
    }

    /**
     * @param outflowCellIDsSet
     * @param outflowHeight The v below which cells in _Grid2DSquareCell are
     * regarded as outflow cells.
     * @param g Grids_GridNumber to process.
     * @param nrows Number of rows in _Grid2DSquareCell.
     * @param ncols Number of columns in _Grid2DSquareCell.
     * @param hoome If true then encountered OutOfMemeroyErrors are handled. If
     * false then an encountered OutOfMemeroyError is thrown.
     * @return HashSet containing cell IDs of those cells in {@code g} that are
     * to be regarded as outflow cells. Outflow cells are those: with
     * {@code v <= outflowHeight}; those with CellID in outflowCellIDsSet; and
     * if {@code treatNoDataValueAsOutflow} is {@code true} then any cell with a
     * v of noDataValue.
     */
    private HashSet<Grids_2D_ID_long> getHollowFilledDEMOutflowCellIDs(
            HashSet<Grids_2D_ID_long> outflowCellIDsSet, double outflowHeight,
            Grids_GridNumber g, long nrows, long ncols,
            boolean treatNoDataValueAsOutflow) throws IOException,
            ClassNotFoundException, Exception {
        HashSet<Grids_2D_ID_long> outflowCellIDs = new HashSet<>();
        if (!(outflowCellIDsSet == null)) {
            outflowCellIDs.addAll(outflowCellIDsSet);
        }
        if (g.getClass() == Grids_GridInt.class) {
            Grids_GridInt gi = (Grids_GridInt) g;
            int ndv = gi.getNoDataValue();
            for (int row = 0; row < nrows; row++) {
                for (int col = 0; col < ncols; col++) {
                    int height = gi.getCell(row, col);
                    if (treatNoDataValueAsOutflow) {
                        if ((height == ndv) || (height <= outflowHeight)) {
                            outflowCellIDs.add(gi.getCellID(row, col));
                        }
                    } else {
                        if ((height != ndv) && (height <= outflowHeight)) {
                            outflowCellIDs.add(gi.getCellID(row, col));
                        }
                    }
                }
            }
        } else {
            // (_Grid2DSquareCell.getClass() == Grids_GridDouble.class)
            Grids_GridDouble gd = (Grids_GridDouble) g;
            double ndv = gd.getNoDataValue();
            for (int row = 0; row < nrows; row++) {
                for (int col = 0; col < ncols; col++) {
                    double height = gd.getCell(row, col);
                    if (treatNoDataValueAsOutflow) {
                        if ((height == ndv) || (height <= outflowHeight)) {
                            outflowCellIDs.add(gd.getCellID(row, col));
                        }
                    } else {
                        if ((height != ndv) && (height <= outflowHeight)) {
                            outflowCellIDs.add(gd.getCellID(row, col));
                        }
                    }
                }
            }
        }
        return outflowCellIDs;
    }

    /**
     * @param g Grids_GridNumber to be processed.
     * @param nrows Number of rows in _Grid2DSquareCell.
     * @param ncols Number of columns in _Grid2DSquareCell.
     * @param hoome If true then encountered OutOfMemeroyErrors are handled. If
     * false then an encountered OutOfMemeroyError is thrown.
     * @param treatNoDataValueAsOutflow If {@code true} then hollows are cells
     * for which all neighbouring cells in the immediate 8 cell neighbourhood
     * are either the same v or higher. If {@code false} then hollows are cells
     * for which all neighbouring cells in the immediate 8 cell neighbourhood
     * are either the same v or higher or noDataValues.
     * @return Set of cell IDs which identifies cells which are hollows.
     */
    private HashSet<Grids_2D_ID_long> getHollowFilledDEMInitialHollowsHashSet(
            Grids_GridNumber g, long nrows, long ncols,
            boolean treatNoDataValueAsOutflow) throws IOException,
            ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        HashSet<Grids_2D_ID_long> initialHollowsHashSet = new HashSet<>();
        int k;
        // Initialise hollows
        long row;
        long col;
        long p;
        long q;
        if (g.getClass() == Grids_GridInt.class) {
            Grids_GridInt gi = (Grids_GridInt) g;
            int ndv = gi.getNoDataValue();
            int[] h = new int[9];
            for (row = 0; row < nrows; row++) {
                for (col = 0; col < ncols; col++) {
                    h[0] = gi.getCell(row, col);
                    if (h[0] != ndv) {
                        k = 0;
                        for (p = -1; p < 2; p++) {
                            for (q = -1; q < 2; q++) {
                                if (!(p == 0 && q == 0)) {
                                    k++;
                                    h[k] = gi.getCell(row + p, col + q);
                                }
                            }
                        }
                        if (treatNoDataValueAsOutflow) {
                            if ((h[1] >= h[0])
                                    && (h[2] >= h[0])
                                    && (h[3] >= h[0])
                                    && (h[4] >= h[0])
                                    && (h[5] >= h[0])
                                    && (h[6] >= h[0])
                                    && (h[7] >= h[0])
                                    && (h[8] >= h[0])) {
                                initialHollowsHashSet.add(g.getCellID(row, col));
                            }
                        } else {
                            if ((h[1] >= h[0] || h[1] == ndv)
                                    && (h[2] >= h[0] || h[2] == ndv)
                                    && (h[3] >= h[0] || h[3] == ndv)
                                    && (h[4] >= h[0] || h[4] == ndv)
                                    && (h[5] >= h[0] || h[5] == ndv)
                                    && (h[6] >= h[0] || h[6] == ndv)
                                    && (h[7] >= h[0] || h[7] == ndv)
                                    && (h[8] >= h[0] || h[8] == ndv)) {
                                initialHollowsHashSet.add(g.getCellID(row, col));
                            }
                        }
                    }
                }
            }
        } else {
            // (_Grid2DSquareCell.getClass() == Grids_GridDouble.class)
            Grids_GridDouble gd = (Grids_GridDouble) g;
            double ndv = gd.getNoDataValue();
            double[] h = new double[9];
            for (row = 0; row < nrows; row++) {
                for (col = 0; col < ncols; col++) {
                    h[0] = gd.getCell(row, col);
                    if (h[0] != ndv) {
                        k = 0;
                        for (p = -1; p < 2; p++) {
                            for (q = -1; q < 2; q++) {
                                if (!(p == 0 && q == 0)) {
                                    k++;
                                    h[k] = gd.getCell(row + p, col + q);
                                }
                            }
                        }
                        if (treatNoDataValueAsOutflow) {
                            if ((h[1] >= h[0])
                                    && (h[2] >= h[0])
                                    && (h[3] >= h[0])
                                    && (h[4] >= h[0])
                                    && (h[5] >= h[0])
                                    && (h[6] >= h[0])
                                    && (h[7] >= h[0])
                                    && (h[8] >= h[0])) {
                                initialHollowsHashSet.add(g.getCellID(row, col));
                            }
                        } else {
                            if ((h[1] >= h[0] || h[1] == ndv)
                                    && (h[2] >= h[0] || h[2] == ndv)
                                    && (h[3] >= h[0] || h[3] == ndv)
                                    && (h[4] >= h[0] || h[4] == ndv)
                                    && (h[5] >= h[0] || h[5] == ndv)
                                    && (h[6] >= h[0] || h[6] == ndv)
                                    && (h[7] >= h[0] || h[7] == ndv)
                                    && (h[8] >= h[0] || h[8] == ndv)) {
                                initialHollowsHashSet.add(g.getCellID(row, col));
                            }
                        }
                    }
                }
            }
        }
        return initialHollowsHashSet;
    }

    /**
     * Returns a Set of cell IDs for cells that might be hollows in grid.
     *
     * @param g The Grids_GridNumber to be processed.
     * @param cellIDs the HashSet storing _CellIDs that must be examined.
     */
    private HashSet<Grids_2D_ID_long> getHollowsInNeighbourhood(
            Grids_GridNumber g, HashSet<Grids_2D_ID_long> cellIDs,
            boolean treatNoDataValueAsOutflow) throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        HashSet<Grids_2D_ID_long> r = new HashSet<>();
        HashSet<Grids_2D_ID_long> visited1 = new HashSet<>();
        Grids_2D_ID_long cellID;
        long row;
        long col;
        long a;
        long b;
        long p;
        long q;
        int k;
        Iterator<Grids_2D_ID_long> ite1 = cellIDs.iterator();
        if (g.getClass() == Grids_GridInt.class) {
            Grids_GridInt gi = (Grids_GridInt) g;
            int ndv = gi.getNoDataValue();
            int[] h = new int[9];
            while (ite1.hasNext()) {
                cellID = ite1.next();
                if (!visited1.contains(cellID)) {
                    row = cellID.getRow();
                    col = cellID.getCol();
                    // Examine neighbourhood
                    for (a = -1; a < 2; a++) {
                        for (b = -1; b < 2; b++) {
                            visited1.add(gi.getCellID(row + a, col + b));
                            h[0] = gi.getCell(row + a, col + b);
                            if (h[0] != ndv) {
                                k = 0;
                                for (p = -1; p < 2; p++) {
                                    for (q = -1; q < 2; q++) {
                                        if (!(p == 0 && q == 0)) {
                                            k++;
                                            h[k] = gi.getCell(
                                                    row + a + p,
                                                    col + b + q);
                                        }
                                    }
                                }
                                if (treatNoDataValueAsOutflow) {
                                    if ((h[1] >= h[0])
                                            && (h[2] >= h[0])
                                            && (h[3] >= h[0])
                                            && (h[4] >= h[0])
                                            && (h[5] >= h[0])
                                            && (h[6] >= h[0])
                                            && (h[7] >= h[0])
                                            && (h[8] >= h[0])) {
                                        r.add(g.getCellID(row + a, col + b));
                                    }
                                } else {
                                    if ((h[1] >= h[0] || h[1] == ndv)
                                            && (h[2] >= h[0] || h[2] == ndv)
                                            && (h[3] >= h[0] || h[3] == ndv)
                                            && (h[4] >= h[0] || h[4] == ndv)
                                            && (h[5] >= h[0] || h[5] == ndv)
                                            && (h[6] >= h[0] || h[6] == ndv)
                                            && (h[7] >= h[0] || h[7] == ndv)
                                            && (h[8] >= h[0] || h[8] == ndv)) {
                                        r.add(gi.getCellID(row + a, col + b));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // (_Grid2DSquareCell.getClass() == Grids_GridDouble.class)
            Grids_GridDouble gd = (Grids_GridDouble) g;
            double ndv = gd.getNoDataValue();
            double[] h = new double[9];
            while (ite1.hasNext()) {
                cellID = ite1.next();
                if (!visited1.contains(cellID)) {
                    row = cellID.getRow();
                    col = cellID.getCol();
                    // Examine neighbourhood
                    for (a = -1; a < 2; a++) {
                        for (b = -1; b < 2; b++) {
                            visited1.add(gd.getCellID(row + a, col + b));
                            h[0] = gd.getCell(row + a, col + b);
                            if (h[0] != ndv) {
                                k = 0;
                                for (p = -1; p < 2; p++) {
                                    for (q = -1; q < 2; q++) {
                                        if (!(p == 0 && q == 0)) {
                                            k++;
                                            h[k] = gd.getCell(
                                                    row + a + p,
                                                    col + b + q);
                                        }
                                    }
                                }
                                if (treatNoDataValueAsOutflow) {
                                    if ((h[1] >= h[0])
                                            && (h[2] >= h[0])
                                            && (h[3] >= h[0])
                                            && (h[4] >= h[0])
                                            && (h[5] >= h[0])
                                            && (h[6] >= h[0])
                                            && (h[7] >= h[0])
                                            && (h[8] >= h[0])) {
                                        r.add(g.getCellID(row + a, col + b));
                                    }
                                } else {
                                    if ((h[1] >= h[0] || h[1] == ndv)
                                            && (h[2] >= h[0] || h[2] == ndv)
                                            && (h[3] >= h[0] || h[3] == ndv)
                                            && (h[4] >= h[0] || h[4] == ndv)
                                            && (h[5] >= h[0] || h[5] == ndv)
                                            && (h[6] >= h[0] || h[6] == ndv)
                                            && (h[7] >= h[0] || h[7] == ndv)
                                            && (h[8] >= h[0] || h[8] == ndv)) {
                                        r.add(gd.getCellID(row + a, col + b));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return r;
    }

    private HashSet<Grids_2D_ID_long> getHollowFilledDEMCalculateHollows(
            Grids_GridNumber g, HashSet<Grids_2D_ID_long> cellIDs)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        if ((g.getNCols() * g.getNRows()) / 4 < cellIDs.size()) {
            // return getInitialHollowsHashSet(grid);
        }
        HashSet<Grids_2D_ID_long> r = new HashSet<>();
        Grids_2D_ID_long cellID;
        long row;
        long col;
        long p;
        long q;
        int k;
        //int noDataCount;
        Iterator<Grids_2D_ID_long> ite1 = cellIDs.iterator();
        if (g.getClass() == Grids_GridInt.class) {
            Grids_GridInt gi = (Grids_GridInt) g;
            int ndv = gi.getNoDataValue();
            int[] h = new int[9];
            while (ite1.hasNext()) {
                cellID = ite1.next();
                row = cellID.getRow();
                col = cellID.getCol();
                h[0] = gi.getCell(row, col);
                if (h[0] != ndv) {
                    //noDataCount = 0;
                    k = 0;
                    for (p = -1; p < 2; p++) {
                        for (q = -1; q < 2; q++) {
                            if (!(p == 0 && q == 0)) {
                                k++;
                                h[k] = gi.getCell(row + p, col + q);
                                //if (heights[k] == noDataValue) {
                                //    noDataCount ++;
                                //}
                            }
                        }
                    }
                    // This deals with single isolated cells surrounded by noDataValues
                    //if (noDataCount < 8) {
                    if ((h[1] >= h[0] || h[1] == ndv)
                            && (h[2] >= h[0] || h[2] == ndv)
                            && (h[3] >= h[0] || h[3] == ndv)
                            && (h[4] >= h[0] || h[4] == ndv)
                            && (h[5] >= h[0] || h[5] == ndv)
                            && (h[6] >= h[0] || h[6] == ndv)
                            && (h[7] >= h[0] || h[7] == ndv)
                            && (h[8] >= h[0] || h[8] == ndv)) {
                        r.add(cellID);
                    }
                    //}
                }
            }
        } else { // (_Grid2DSquareCell.getClass() == Grids_GridDouble.class)
            Grids_GridDouble gd = (Grids_GridDouble) g;
            double ndv = gd.getNoDataValue();
            double[] h = new double[9];
            while (ite1.hasNext()) {
                cellID = ite1.next();
                row = cellID.getRow();
                col = cellID.getCol();
                h[0] = gd.getCell(row, col);
                if (h[0] != ndv) {
                    //noDataCount = 0;
                    k = 0;
                    for (p = -1; p < 2; p++) {
                        for (q = -1; q < 2; q++) {
                            if (!(p == 0 && q == 0)) {
                                k++;
                                h[k] = gd.getCell(row + p, col + q);
                                //if (heights[k] == noDataValue) {
                                //    noDataCount ++;
                                //}

                            }
                        }
                    }
                    // This deals with single isolated cells surrounded by noDataValues
                    //if (noDataCount < 8) {
                    if ((h[1] >= h[0] || h[1] == ndv)
                            && (h[2] >= h[0] || h[2] == ndv)
                            && (h[3] >= h[0] || h[3] == ndv)
                            && (h[4] >= h[0] || h[4] == ndv)
                            && (h[5] >= h[0] || h[5] == ndv)
                            && (h[6] >= h[0] || h[6] == ndv)
                            && (h[7] >= h[0] || h[7] == ndv)
                            && (h[8] >= h[0] || h[8] == ndv)) {
                        r.add(cellID);

                    } //}
                }
            }
        }
        return r;
    }

    /**
     * Returns a grids[] where:
     * <ul>
     *
     * <li>9 basic metrics:
     * <ul>
     * <li>[0] = no data count;</li>
     * <li>[1] = flatness;</li>
     * <li>[2] = roughness</li>
     * <li>[3] = slopyness</li>
     * <li>[4] = levelness</li>
     * <li>[5] = totalDownness</li>
     * <li>[6] = averageDownness</li>
     * <li>[7] = totalUpness</li>
     * <li>[8] = averageUpness</li>
     * </ul></li>
     *
     * <li>6 metrics with all cells higher or same:
     * <ul>
     * <li>[9] = maxd_hhhh [sum of distance weighted maximum height
     * differences]</li>
     * <li>[10] = mind_hhhh [sum of distance weighted minimum height
     * differences]</li>
     * <li>[11] = sumd_hhhh [sum of distance weighted height differences]</li>
     * <li>[12] = aved_hhhh [sum of distance weighted average height
     * difference]</li>
     * <li>[13] = count_hhhh [count]</li>
     * <li>[14] = w_hhhh [sum of distance weights]</li>
     * </ul></li>
     *
     * <li>11 metrics with one cell lower or the same:
     * <ul>
     * <li>[15] = mind_hxhx_ai_hhhl [sum of distance weighted (minimum
     * difference of cells adjacent to lower cell)]</li>
     * <li>[16] = maxd_hxhx_ai_hhhl [sum of distance weighted (maximum
     * difference of cells adjacent to lower cell)]</li>
     * <li>[17] = sumd_hxhx_ai_hhhl [sum of distance weighted (sum of
     * differences of cells adjacent to lower cell)]</li>
     * <li>[18] = d_xhxx_ai_hhhl [sum of distance weighted (difference of cell
     * opposite lower cell)]</li>
     * <li>[19] = d_xxxl_ai_hhhl [sum of distance weighted (difference of lower
     * cell)]</li>
     * <li>[20] = sumd_xhxl_ai_hhhl [sum of distance weighted (sum of
     * differences of lower cell and cell opposite)]</li>
     * <li>[21] = mind_abs_xhxl_ai_hhhl [sum of distance weighted (minimum
     * difference magnitude of lower cell and cell opposite)]</li>
     * <li>[22] = maxd_abs_xhxl_ai_hhhl [sum of distance weighted (maximum
     * difference magnitude of lower cell and cell opposite)]</li>
     * <li>[23] = sumd_abs_xhxl_ai_hhhl [sum of distance weighted (sum of
     * difference magnitudes of lower cell and cell opposite)]</li>
     * <li>[24] = count_hhhl [count]</li>
     * <li>[25] = w_hhhl [sum of distance weights]</li>
     * </ul></li>
     *
     * <li>22 metrics with two cells lower: (N.B. Could have more metrics e.g.
     * minimum magnitude of minimum of higher and maximum of lower cells.)
     * <ul>
     * <li>11 Metrics with opposite cells lower/higher
     * <ul>
     * <li>[26] = mind_hxhx_ai_hlhl [sum of distance weighted (minimum
     * difference of higher cells)]</li>
     * <li>[27] = maxd_hxhx_ai_hlhl [sum of distance weighted (maximum
     * difference of higher cells)]</li>
     * <li>[28] = sumd_hxhx_ai_hlhl [sum of distance weighted (sum differences
     * of higher cells)]</li>
     * <li>[29] = mind_xlxl_ai_hlhl [sum of distance weighted (minimum
     * difference of lower cells)]</li>
     * <li>[30] = maxd_xlxl_ai_hlhl [sum of distance weighted (maximum
     * difference of lower cells)]</li>
     * <li>[31] = sumd_xlxl_ai_hlhl [sum of distance weighted (sum of
     * differences of lower cells)]</li>
     * <li>[32] = mind_abs_hlhl [sum of distance weighted (minimum difference
     * magnitude of cells)]</li>
     * <li>[33] = maxd_abs_hlhl [sum of distance weighted (maximum difference
     * magnitude of cells)]</li>
     * <li>[34] = sumd_abs_hlhl [sum of distance weighted (sum of difference
     * magnitudes of cells)]</li>
     * <li>[35] = count_hlhl [count]</li>
     * <li>[36] = w_hlhl [sum of distance weights]</li></ul>
     * <li>11 Metrics with adjacent cells lower/higher:
     * <ul>
     * <li>[37] = mind_hhxx_ai_hhll [sum of distance weighted (minimum
     * difference of higher cells)]</li>
     * <li>[38] = maxd_hhxx_ai_hhll [sum of distance weighted (maximum
     * difference of higher cells)]</li>
     * <li>[39] = sumd_hhxx_ai_hhll [sum of distance weighted (sum of
     * differences of higher cells)]</li>
     * <li>[40] = mind_xxll_ai_hhll [sum of distance weighted (minimum
     * difference of lower cells)]</li>
     * <li>[41] = maxd_xxll_ai_hhll [sum of distance weighted (maximum
     * difference of lower cells)]</li>
     * <li>[42] = sumd_xxll_ai_hhll [sum of distance weighted (sum of
     * differences of lower cells)]</li>
     * <li>[43] = mind_abs_hhll [sum of distance weighted (minimum difference
     * magnitude of cells)]</li>
     * <li>[44] = maxd_abs_hhll [sum of distance weighted (maximum difference
     * magnitude of cells)]</li>
     * <li>[45] = sumd_abs_hhll [sum of distance weighted (sum of difference
     * magnitudes of cells)]</li>
     * <li>[46] = count_hhll [count]</li>
     * <li>[47] = w_hhll [sum of distance weights]</li></ul>
     * </ul></li>
     *
     * <li>11 metrics with one cell higher:
     * <ul>
     * <li>[48] = mind_lxlx_ai_lllh [sum of distance weighted (minimum
     * difference of cells adjacent to higher cell)]</li>
     * <li>[49] = maxd_lxlx_ai_lllh [sum of distance weighted (maximum
     * difference of cells adjacent to higher cell)]</li>
     * <li>[50] = sumd_lxlx_ai_lllh [sum of distance weighted (sum of
     * differences of cells adjacent to higher cell)]</li>
     * <li>[51] = d_xlxx_ai_lllh [sum of distance weighted (difference of cell
     * opposite higher cell)]</li>
     * <li>[52] = d_xxxh_ai_lllh [sum of distance weighted (difference of higher
     * cell)]</li>
     * <li>[53] = sumd_xlxh_ai_lllh [sum of distance weighted (sum of
     * differences of higher cell and cell opposite)]</li>
     * <li>[54] = mind_abs_xlxh_ai_lllh [sum of distance weighted (minimum
     * difference magnitude of higher cell and cell opposite)]</li>
     * <li>[55] = maxd_abs_xlxh_ai_lllh [sum of distance weighted (maximum
     * difference magnitude of higher cell and cell opposite)]</li>
     * <li>[56] = sumd_abs_xlxh_ai_lllh [sum of distance weighted (sum of
     * difference magnitudes of higher cell and cell opposite)]</li>
     * <li>[57] = count_lllh [count]</li>
     * <li>[58] = w_lllh [sum of distance weights]</li></ul>
     *
     * <li>6 metrics with all cells higher:
     * <ul>
     * <li>[59] = maxd_llll [sum of distance weighted maximum height
     * differences]</li>
     * <li>[60] = mind_llll [sum of distance weighted minimum height
     * differences]</li>
     * <li>[61] = sumd_llll [sum of distance weighted height differences]</li>
     * <li>[62] = aved_llll [sum of distance weighted average height
     * difference]</li>
     * <li>[63] = count_llll [count]</li>
     * <li>[64] = w_llll [sum of distance weights];</li></ul>
     * </ul>
     *
     * @param g the Grids_GridDouble to be processed
     * @param distance the distance within which metrics will be calculated
     * @param weightIntersect kernel parameter (weight at the centre)
     * @param weightFactor kernel parameter (distance decay)
     * @param gdf The factory for creating double grids.
     * @param gif The factory for creating int grids.
     * @param swapInitialisedFiles For memory management.
     * @param swapProcessedChunks For memory management.
     * @param oom The Order of Magnitude for the precision.
     * @return metrics 1.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridNumber[] getMetrics1(Grids_GridNumber g,
            Math_BigRationalSqrt distance, double weightIntersect, double weightFactor,
            Grids_GridDoubleFactory gdf, Grids_GridIntFactory gif,
            boolean swapInitialisedFiles, boolean swapProcessedChunks, int oom)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        if (gdf.getChunkNCols() != gif.getChunkNCols()
                || gdf.getChunkNRows() != gif.getChunkNRows()) {
            env.env.log("Warning! ((gridDoubleFactory.getChunkNcols() "
                    + "!= gridIntFactory.getChunkNcols()) || "
                    + "(gridDoubleFactory.getChunkNrows() != "
                    + "gridIntFactory.getChunkNrows()))");
        }
        Grids_GridNumber[] metrics1 = new Grids_GridNumber[65];
        long ncols = g.getNCols();
        long nrows = g.getNRows();
        Grids_Dimensions dimensions = g.getDimensions();
        boolean isInitialised = false;
        String[] metrics1Names = getMetrics1Names();
        for (int i = 0; i < metrics1.length; i++) {
            env.checkAndMaybeFreeMemory();
            do {
                try {
                    metrics1[i] = gdf.create(nrows, ncols, dimensions);
                    if (swapInitialisedFiles) {
                        metrics1[i].cache();
                    }
                    metrics1[i].setName(metrics1Names[i]);
                    env.addGrid(metrics1[i]);
                    isInitialised = true;
                } catch (OutOfMemoryError e) {
                    env.clearMemoryReserve(env.env);
                    System.err.println("OutOfMemoryError in getMetrics1(...) initialisation");
                    if (!env.swapChunk(env.HOOMEF)) {
                        throw e;
                    }
                    env.initMemoryReserve(env.env);
                }
                System.out.println("Initialised result[" + i + "]");

            } while (!isInitialised);
        }
        return getMetrics1(metrics1, g, dimensions, distance, weightIntersect,
                weightFactor, swapProcessedChunks, oom);
    }

    /**
     * @return Metrics 1 names.
     */
    protected String[] getMetrics1Names() {
        String[] names = new String[65];
        names[0] = "noDataCount";
        names[1] = "flatness";
        names[2] = "roughness";
        names[3] = "slopyness";
        names[4] = "levelness";
        names[5] = "totalDownness";
        names[6] = "averageDownness";
        names[7] = "totalUpness";
        names[8] = "averageUpness";
        names[9] = "maxd_hhhh";
        names[10] = "mind_hhhh";
        names[11] = "sumd_hhhh";
        names[12] = "aved_hhhh";
        names[13] = "count_hhhh";
        names[14] = "w_hhhh";
        names[15] = "mind_hxhx_ai_hhhl";
        names[16] = "maxd_hxhx_ai_hhhl";
        names[17] = "sumd_hxhx_ai_hhhl";
        names[18] = "d_xhxx_ai_hhhl";
        names[19] = "d_xxxl_ai_hhhl";
        names[20] = "sumd_xhxl_ai_hhhl";
        names[21] = "mind_abs_xhxl_ai_hhhl";
        names[22] = "maxd_abs_xhxl_ai_hhhl";
        names[23] = "sumd_abs_xhxl_ai_hhhl";
        names[24] = "count_hhhl";
        names[25] = "w_hhhl";
        names[26] = "mind_hxhx_ai_hlhl";
        names[27] = "maxd_hxhx_ai_hlhl";
        names[28] = "sumd_hxhx_ai_hlhl";
        names[29] = "mind_xlxl_ai_hlhl";
        names[30] = "maxd_xlxl_ai_hlhl";
        names[31] = "sumd_xlxl_ai_hlhl";
        names[32] = "mind_abs_hlhl";
        names[33] = "maxd_abs_hlhl";
        names[34] = "sumd_abs_hlhl";
        names[35] = "count_hlhl";
        names[36] = "w_hlhl";
        names[37] = "mind_hhxx_ai_hhll";
        names[38] = "maxd_hhxx_ai_hhll";
        names[39] = "sumd_hhxx_ai_hhll";
        names[40] = "mind_xxll_ai_hhll";
        names[41] = "maxd_xxll_ai_hhll";
        names[42] = "sumd_xxll_ai_hhll";
        names[43] = "mind_abs_hhll";
        names[44] = "maxd_abs_hhll";
        names[45] = "sumd_abs_hhll";
        names[46] = "count_hhll";
        names[47] = "w_hhll";
        names[48] = "mind_lxlx_ai_lllh";
        names[49] = "maxd_lxlx_ai_lllh";
        names[50] = "sumd_lxlx_ai_lllh";
        names[51] = "d_xlxx_ai_lllh";
        names[52] = "d_xxxh_ai_lllh";
        names[53] = "sumd_xlxh_ai_lllh";
        names[54] = "mind_abs_xlxh_ai_lllh";
        names[55] = "maxd_abs_xlxh_ai_lllh";
        names[56] = "sumd_abs_xlxh_ai_lllh";
        names[57] = "count_lllh";
        names[58] = "w_lllh";
        names[59] = "maxd_llll";
        names[60] = "mind_llll";
        names[61] = "sumd_llll";
        names[62] = "aved_llll";
        names[63] = "count_llll";
        names[64] = "w_llll";
        return names;
    }

    /**
     * Returns an Grids_GridDouble[].
     *
     * <ul>
     *
     * <li>9 basic metrics:
     * <ul>
     * <li>[0] = no data count;</li>
     * <li>[1] = flatness;</li>
     * <li>[2] = roughness</li>
     * <li>[3] = slopyness</li>
     * <li>[4] = levelness</li>
     * <li>[5] = totalDownness</li>
     * <li>[6] = averageDownness</li>
     * <li>[7] = totalUpness</li>
     * <li>[8] = averageUpness</li>
     * </ul></li>
     *
     * <li>6 metrics with all cells higher or same:
     * <ul>
     * <li>[9] = maxd_hhhh [sum of distance weighted maximum height
     * differences]</li>
     * <li>[10] = mind_hhhh [sum of distance weighted minimum height
     * differences]</li>
     * <li>[11] = sumd_hhhh [sum of distance weighted height differences]</li>
     * <li>[12] = aved_hhhh [sum of distance weighted average height
     * difference]</li>
     * <li>[13] = count_hhhh [count]</li>
     * <li>[14] = w_hhhh [sum of distance weights]</li>
     * </ul></li>
     *
     * <li>11 metrics with one cell lower or the same:
     * <ul>
     * <li>[15] = mind_hxhx_ai_hhhl [sum of distance weighted (minimum
     * difference of cells adjacent to lower cell)]</li>
     * <li>[16] = maxd_hxhx_ai_hhhl [sum of distance weighted (maximum
     * difference of cells adjacent to lower cell)]</li>
     * <li>[17] = sumd_hxhx_ai_hhhl [sum of distance weighted (sum of
     * differences of cells adjacent to lower cell)]</li>
     * <li>[18] = d_xhxx_ai_hhhl [sum of distance weighted (difference of cell
     * opposite lower cell)]</li>
     * <li>[19] = d_xxxl_ai_hhhl [sum of distance weighted (difference of lower
     * cell)]</li>
     * <li>[20] = sumd_xhxl_ai_hhhl [sum of distance weighted (sum of
     * differences of lower cell and cell opposite)]</li>
     * <li>[21] = mind_abs_xhxl_ai_hhhl [sum of distance weighted (minimum
     * difference magnitude of lower cell and cell opposite)]</li>
     * <li>[22] = maxd_abs_xhxl_ai_hhhl [sum of distance weighted (maximum
     * difference magnitude of lower cell and cell opposite)]</li>
     * <li>[23] = sumd_abs_xhxl_ai_hhhl [sum of distance weighted (sum of
     * difference magnitudes of lower cell and cell opposite)]</li>
     * <li>[24] = count_hhhl [count]</li>
     * <li>[25] = w_hhhl [sum of distance weights]</li>
     * </ul></li>
     *
     * <li>22 metrics with two cells lower: (N.B. Could have more metrics e.g.
     * minimum magnitude of minimum of higher and maximum of lower cells.)
     * <ul>
     * <li>11 Metrics with opposite cells lower/higher
     * <ul>
     * <li>[26] = mind_hxhx_ai_hlhl [sum of distance weighted (minimum
     * difference of higher cells)]</li>
     * <li>[27] = maxd_hxhx_ai_hlhl [sum of distance weighted (maximum
     * difference of higher cells)]</li>
     * <li>[28] = sumd_hxhx_ai_hlhl [sum of distance weighted (sum differences
     * of higher cells)]</li>
     * <li>[29] = mind_xlxl_ai_hlhl [sum of distance weighted (minimum
     * difference of lower cells)]</li>
     * <li>[30] = maxd_xlxl_ai_hlhl [sum of distance weighted (maximum
     * difference of lower cells)]</li>
     * <li>[31] = sumd_xlxl_ai_hlhl [sum of distance weighted (sum of
     * differences of lower cells)]</li>
     * <li>[32] = mind_abs_hlhl [sum of distance weighted (minimum difference
     * magnitude of cells)]</li>
     * <li>[33] = maxd_abs_hlhl [sum of distance weighted (maximum difference
     * magnitude of cells)]</li>
     * <li>[34] = sumd_abs_hlhl [sum of distance weighted (sum of difference
     * magnitudes of cells)]</li>
     * <li>[35] = count_hlhl [count]</li>
     * <li>[36] = w_hlhl [sum of distance weights]</li></ul>
     * <li>11 Metrics with adjacent cells lower/higher:
     * <ul>
     * <li>[37] = mind_hhxx_ai_hhll [sum of distance weighted (minimum
     * difference of higher cells)]</li>
     * <li>[38] = maxd_hhxx_ai_hhll [sum of distance weighted (maximum
     * difference of higher cells)]</li>
     * <li>[39] = sumd_hhxx_ai_hhll [sum of distance weighted (sum of
     * differences of higher cells)]</li>
     * <li>[40] = mind_xxll_ai_hhll [sum of distance weighted (minimum
     * difference of lower cells)]</li>
     * <li>[41] = maxd_xxll_ai_hhll [sum of distance weighted (maximum
     * difference of lower cells)]</li>
     * <li>[42] = sumd_xxll_ai_hhll [sum of distance weighted (sum of
     * differences of lower cells)]</li>
     * <li>[43] = mind_abs_hhll [sum of distance weighted (minimum difference
     * magnitude of cells)]</li>
     * <li>[44] = maxd_abs_hhll [sum of distance weighted (maximum difference
     * magnitude of cells)]</li>
     * <li>[45] = sumd_abs_hhll [sum of distance weighted (sum of difference
     * magnitudes of cells)]</li>
     * <li>[46] = count_hhll [count]</li>
     * <li>[47] = w_hhll [sum of distance weights]</li></ul>
     * </ul></li>
     *
     * <li>11 metrics with one cell higher:
     * <ul>
     * <li>[48] = mind_lxlx_ai_lllh [sum of distance weighted (minimum
     * difference of cells adjacent to higher cell)]</li>
     * <li>[49] = maxd_lxlx_ai_lllh [sum of distance weighted (maximum
     * difference of cells adjacent to higher cell)]</li>
     * <li>[50] = sumd_lxlx_ai_lllh [sum of distance weighted (sum of
     * differences of cells adjacent to higher cell)]</li>
     * <li>[51] = d_xlxx_ai_lllh [sum of distance weighted (difference of cell
     * opposite higher cell)]</li>
     * <li>[52] = d_xxxh_ai_lllh [sum of distance weighted (difference of higher
     * cell)]</li>
     * <li>[53] = sumd_xlxh_ai_lllh [sum of distance weighted (sum of
     * differences of higher cell and cell opposite)]</li>
     * <li>[54] = mind_abs_xlxh_ai_lllh [sum of distance weighted (minimum
     * difference magnitude of higher cell and cell opposite)]</li>
     * <li>[55] = maxd_abs_xlxh_ai_lllh [sum of distance weighted (maximum
     * difference magnitude of higher cell and cell opposite)]</li>
     * <li>[56] = sumd_abs_xlxh_ai_lllh [sum of distance weighted (sum of
     * difference magnitudes of higher cell and cell opposite)]</li>
     * <li>[57] = count_lllh [count]</li>
     * <li>[58] = w_lllh [sum of distance weights]</li></ul>
     *
     * <li>6 metrics with all cells higher:
     * <ul>
     * <li>[59] = maxd_llll [sum of distance weighted maximum height
     * differences]</li>
     * <li>[60] = mind_llll [sum of distance weighted minimum height
     * differences]</li>
     * <li>[61] = sumd_llll [sum of distance weighted height differences]</li>
     * <li>[62] = aved_llll [sum of distance weighted average height
     * difference]</li>
     * <li>[63] = count_llll [count]</li>
     * <li>[64] = w_llll [sum of distance weights];</li></ul>
     * </ul>
     *
     * @param metrics1 an Grids_GridDouble[] for storing result \n
     * @param g the Grids_GridDouble to be processed \n
     * @param dim The dimensions.
     * @param distance The distance within which metrics will be calculated.
     * @param wi The weight intersect kernel parameter (weight at the centre).
     * @param wf The weight factor kernel parameter (distance decay).
     * @param swapProcessedChunks If {@code true}, then preemptive swapping is
     * done for memory management.
     * @param oom The Order of Magnitude for the precision.
     * @return metrics 1.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridNumber[] getMetrics1(Grids_GridNumber[] metrics1,
            Grids_GridNumber g, Grids_Dimensions dim,
            Math_BigRationalSqrt distance,
            double wi, double wf, boolean swapProcessedChunks, int oom)
            throws IOException, ClassNotFoundException, Exception {
        String methodName = "getMetrics1("
                + "Grids_AbstractGridNumber[],Grids_AbstractGridNumber,"
                + "Grids_Dimensions,double,double,double,boolean)";
        System.out.println(methodName);
        env.checkAndMaybeFreeMemory();
        String name;
        String underScore = "_";
        Math_BigRational cellsize = dim.getCellsize();
        int cellDistance = distance.divide(cellsize, oom).getSqrt(oom).ceil().intValue();
        BigDecimal[] heights = new BigDecimal[4];
        heights[0] = BigDecimal.ZERO;
        heights[1] = BigDecimal.ZERO;
        heights[2] = BigDecimal.ZERO;
        heights[3] = BigDecimal.ZERO;
        BigDecimal[] diff = new BigDecimal[4];
        diff[0] = BigDecimal.ZERO;
        diff[1] = BigDecimal.ZERO;
        diff[2] = BigDecimal.ZERO;
        diff[3] = BigDecimal.ZERO;
        BigDecimal[] dummyDiff = new BigDecimal[4];
        dummyDiff[0] = BigDecimal.ZERO;
        dummyDiff[1] = BigDecimal.ZERO;
        dummyDiff[2] = BigDecimal.ZERO;
        dummyDiff[3] = BigDecimal.ZERO;
        double[][] weights;
        weights = Grids_Kernel.getNormalDistributionKernelWeights(
                g.getCellsize(), distance, oom);
        double[] metrics1ForCell = new double[metrics1.length];
        for (int i = 0; i < metrics1.length; i++) {
            metrics1ForCell[i] = 0.0d;
        }
        Grids_2D_ID_int chunkID;
        int nChunkRows = g.getNChunkRows();
        int nChunkCols = g.getNChunkCols();
        int chunkNRows;
        int chunkNCols;
        int chunkRow;
        int chunkCol;
        int cellRow;
        int cellCol;
        int i;
        String[] names = getMetrics1Names();
        int normalChunkNRows = g.getChunkNRows(0);
        int normalChunkNCols = g.getChunkNCols(0);
        BigDecimal ndv = g.ndv;

        if (g.getClass() == Grids_GridDouble.class) {
            Grids_GridDouble gd = (Grids_GridDouble) g;
            double ndvd = gd.getNoDataValue();
            for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
                System.out.println("chunkRow(" + chunkRow + ")");
                chunkNRows = g.getChunkNRows(chunkRow);
                for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                    System.out.println("chunkCol(" + chunkCol + ")");
                    chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                    env.initNotToClear();
                    env.addToNotToClear(g, chunkID, chunkRow, chunkCol,
                            normalChunkNRows, normalChunkNCols, cellDistance);
                    //ge.addToNotToClear(g, chunkID);
                    env.addToNotToClear(metrics1, chunkID);
                    env.checkAndMaybeFreeMemory();
                    Grids_ChunkDouble gcd = (Grids_ChunkDouble) gd.getChunk(
                            chunkRow, chunkCol);
                    boolean doLoop = true;
                    if (gcd instanceof Grids_ChunkDoubleSinglet) {
                        if (((Grids_ChunkDoubleSinglet) gcd).getV() == ndvd) {
                            doLoop = false;
                        }
                    }
                    if (doLoop) {
                        chunkNCols = g.getChunkNCols(chunkCol);
                        for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                            long row = g.getRow(chunkRow, cellRow);
                            Math_BigRational y = g.getCellY(row);
                            for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                                long col = g.getCol(chunkCol, cellCol);
                                Math_BigRational x = gd.getCellX(col);
                                //height = _Grid2DSquareCellDouble.getCell(cellRowIndex, cellColIndex, hoome);
                                BigDecimal cellHeight = gcd.getCellBigDecimal(
                                        cellRow, cellCol);
                                if (cellHeight.compareTo(ndv) != 0) {
                                    env.checkAndMaybeFreeMemory();
                                    metrics1Calculate_All(gd, ndv, row,
                                            col, x, y, cellHeight, cellDistance,
                                            weights, metrics1ForCell, heights,
                                            diff, dummyDiff);
                                    for (i = 0; i < metrics1.length; i++) {
                                        if (metrics1[i] instanceof Grids_GridInt) {
                                            ((Grids_GridInt) metrics1[i]).setCell(
                                                    row, col,
                                                    (int) metrics1ForCell[i]);
                                        } else {
                                            ((Grids_GridDouble) metrics1[i]).setCell(
                                                    row, col,
                                                    metrics1ForCell[i]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    env.env.log("Done Chunk (" + chunkRow + ", " + chunkCol + ")");

                    env.env.log("swapProcessedChunks");
                    env.initNotToClear();

                    if (swapProcessedChunks) {
                        for (i = 0; i < metrics1.length; i++) {
                            env.checkAndMaybeFreeMemory();
                            metrics1[i].swap(chunkID, true, env.HOOME);
                        }
                    }
                }
            }
        } else {
            // (g.getClass() == Grids_GridInt.class)
            Grids_GridInt gridInt = (Grids_GridInt) g;
            int noDataValue = gridInt.getNoDataValue();
            int ndvd = gridInt.getNoDataValue();
            Grids_ChunkInt gridChunkInt;
            for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
                chunkNRows = g.getChunkNRows(chunkRow);
                System.out.println("chunkRow(" + chunkRow + ")");
                for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                    System.out.println("chunkColIndex(" + chunkCol + ")");
                    chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                    env.initNotToClear();
                    env.addToNotToClear(g, chunkID, chunkRow, chunkCol,
                            normalChunkNRows, normalChunkNCols, cellDistance);
                    //ge.addToNotToClear(g, chunkID);
                    env.addToNotToClear(metrics1, chunkID);
                    env.checkAndMaybeFreeMemory();
                    gridChunkInt = (Grids_ChunkInt) gridInt.getChunk(
                            chunkRow, chunkCol);
                    boolean doLoop = true;
                    if (gridChunkInt instanceof Grids_ChunkIntSinglet) {
                        if (((Grids_ChunkIntSinglet) gridChunkInt).v == noDataValue) {
                            doLoop = false;
                        }
                    }
                    if (doLoop) {
                        chunkNCols = g.getChunkNCols(chunkCol);
                        for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                            long row = g.getRow(chunkRow, cellRow);
                            Math_BigRational y = g.getCellY(row);
                            for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                                long col = g.getCol(chunkCol, cellCol);
                                Math_BigRational x = gridInt.getCellX(cellCol);
                                BigDecimal cellHeight = gridChunkInt.getCellBigDecimal(cellRow, cellCol);
                                if (cellHeight.compareTo(ndv) != 0) {
                                    env.checkAndMaybeFreeMemory();
                                    metrics1Calculate_All(gridInt, ndv, row,
                                            col, x, y, cellHeight, cellDistance,
                                            weights, metrics1ForCell, heights,
                                            diff, dummyDiff);
                                    for (i = 0; i < metrics1.length; i++) {
                                        if (metrics1[i] instanceof Grids_GridInt) {
                                            ((Grids_GridInt) metrics1[i]).setCell(
                                                    row, col,
                                                    (int) metrics1ForCell[i]);
                                        } else {
                                            ((Grids_GridDouble) metrics1[i]).setCell(
                                                    row, col,
                                                    metrics1ForCell[i]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    System.out.println(
                            "Done Chunk (" + chunkRow + ", " + chunkCol + ")");
                    if (swapProcessedChunks) {
                        for (i = 0; i < metrics1.length; i++) {
                            env.checkAndMaybeFreeMemory();
                            metrics1[i].swap(chunkID, true, env.HOOME);
                        }
                    }
                }
            }
        }
        for (i = 0; i < names.length; i++) {
            name = names[i] + underScore + distance;
            metrics1[i].setName(name);
        }
        return metrics1;
    }

    /**
     * Returns a double[] of the cells in grid upto distance from a cell given
     * by rowIndex and colIndex. The elements of metrics1 do not explicitly take
     * into account any axis such as that which can be defined from a metric of
     * slope (general slope direction). Distance weighting is done via a kernel
     * pre-calculated as weights. Some elements of metrics1 are weighted based
     * on the difference in value (height) of the cell at (rowIndex,colIndex)
     * and other cell values within distance. Within distance equidistant cells
     * in 4 orthogonal directions are accounted for in the metrics1. NB. Every
     * cell is either higher, lower or the same height as the cell at cell row
     * {@code row}, cell col {@code col}. Some DEMs will have few cells in
     * distance with the same v.
     *
     * <ul>
     *
     * <li>9 basic metrics:
     * <ul>
     * <li>[0] = no data count;</li>
     * <li>[1] = flatness;</li>
     * <li>[2] = roughness</li>
     * <li>[3] = slopyness</li>
     * <li>[4] = levelness</li>
     * <li>[5] = totalDownness</li>
     * <li>[6] = averageDownness</li>
     * <li>[7] = totalUpness</li>
     * <li>[8] = averageUpness</li>
     * </ul></li>
     *
     * <li>6 metrics with all cells higher or same:
     * <ul>
     * <li>[9] = maxd_hhhh [sum of distance weighted maximum height
     * differences]</li>
     * <li>[10] = mind_hhhh [sum of distance weighted minimum height
     * differences]</li>
     * <li>[11] = sumd_hhhh [sum of distance weighted height differences]</li>
     * <li>[12] = aved_hhhh [sum of distance weighted average height
     * difference]</li>
     * <li>[13] = count_hhhh [count]</li>
     * <li>[14] = w_hhhh [sum of distance weights]</li>
     * </ul></li>
     *
     * <li>11 metrics with one cell lower or the same:
     * <ul>
     * <li>[15] = mind_hxhx_ai_hhhl [sum of distance weighted (minimum
     * difference of cells adjacent to lower cell)]</li>
     * <li>[16] = maxd_hxhx_ai_hhhl [sum of distance weighted (maximum
     * difference of cells adjacent to lower cell)]</li>
     * <li>[17] = sumd_hxhx_ai_hhhl [sum of distance weighted (sum of
     * differences of cells adjacent to lower cell)]</li>
     * <li>[18] = d_xhxx_ai_hhhl [sum of distance weighted (difference of cell
     * opposite lower cell)]</li>
     * <li>[19] = d_xxxl_ai_hhhl [sum of distance weighted (difference of lower
     * cell)]</li>
     * <li>[20] = sumd_xhxl_ai_hhhl [sum of distance weighted (sum of
     * differences of lower cell and cell opposite)]</li>
     * <li>[21] = mind_abs_xhxl_ai_hhhl [sum of distance weighted (minimum
     * difference magnitude of lower cell and cell opposite)]</li>
     * <li>[22] = maxd_abs_xhxl_ai_hhhl [sum of distance weighted (maximum
     * difference magnitude of lower cell and cell opposite)]</li>
     * <li>[23] = sumd_abs_xhxl_ai_hhhl [sum of distance weighted (sum of
     * difference magnitudes of lower cell and cell opposite)]</li>
     * <li>[24] = count_hhhl [count]</li>
     * <li>[25] = w_hhhl [sum of distance weights]</li>
     * </ul></li>
     *
     * <li>22 metrics with two cells lower: (N.B. Could have more metrics e.g.
     * minimum magnitude of minimum of higher and maximum of lower cells.)
     * <ul>
     * <li>11 Metrics with opposite cells lower/higher
     * <ul>
     * <li>[26] = mind_hxhx_ai_hlhl [sum of distance weighted (minimum
     * difference of higher cells)]</li>
     * <li>[27] = maxd_hxhx_ai_hlhl [sum of distance weighted (maximum
     * difference of higher cells)]</li>
     * <li>[28] = sumd_hxhx_ai_hlhl [sum of distance weighted (sum differences
     * of higher cells)]</li>
     * <li>[29] = mind_xlxl_ai_hlhl [sum of distance weighted (minimum
     * difference of lower cells)]</li>
     * <li>[30] = maxd_xlxl_ai_hlhl [sum of distance weighted (maximum
     * difference of lower cells)]</li>
     * <li>[31] = sumd_xlxl_ai_hlhl [sum of distance weighted (sum of
     * differences of lower cells)]</li>
     * <li>[32] = mind_abs_hlhl [sum of distance weighted (minimum difference
     * magnitude of cells)]</li>
     * <li>[33] = maxd_abs_hlhl [sum of distance weighted (maximum difference
     * magnitude of cells)]</li>
     * <li>[34] = sumd_abs_hlhl [sum of distance weighted (sum of difference
     * magnitudes of cells)]</li>
     * <li>[35] = count_hlhl [count]</li>
     * <li>[36] = w_hlhl [sum of distance weights]</li></ul>
     * <li>11 Metrics with adjacent cells lower/higher:
     * <ul>
     * <li>[37] = mind_hhxx_ai_hhll [sum of distance weighted (minimum
     * difference of higher cells)]</li>
     * <li>[38] = maxd_hhxx_ai_hhll [sum of distance weighted (maximum
     * difference of higher cells)]</li>
     * <li>[39] = sumd_hhxx_ai_hhll [sum of distance weighted (sum of
     * differences of higher cells)]</li>
     * <li>[40] = mind_xxll_ai_hhll [sum of distance weighted (minimum
     * difference of lower cells)]</li>
     * <li>[41] = maxd_xxll_ai_hhll [sum of distance weighted (maximum
     * difference of lower cells)]</li>
     * <li>[42] = sumd_xxll_ai_hhll [sum of distance weighted (sum of
     * differences of lower cells)]</li>
     * <li>[43] = mind_abs_hhll [sum of distance weighted (minimum difference
     * magnitude of cells)]</li>
     * <li>[44] = maxd_abs_hhll [sum of distance weighted (maximum difference
     * magnitude of cells)]</li>
     * <li>[45] = sumd_abs_hhll [sum of distance weighted (sum of difference
     * magnitudes of cells)]</li>
     * <li>[46] = count_hhll [count]</li>
     * <li>[47] = w_hhll [sum of distance weights]</li></ul>
     * </ul></li>
     *
     * <li>11 metrics with one cell higher:
     * <ul>
     * <li>[48] = mind_lxlx_ai_lllh [sum of distance weighted (minimum
     * difference of cells adjacent to higher cell)]</li>
     * <li>[49] = maxd_lxlx_ai_lllh [sum of distance weighted (maximum
     * difference of cells adjacent to higher cell)]</li>
     * <li>[50] = sumd_lxlx_ai_lllh [sum of distance weighted (sum of
     * differences of cells adjacent to higher cell)]</li>
     * <li>[51] = d_xlxx_ai_lllh [sum of distance weighted (difference of cell
     * opposite higher cell)]</li>
     * <li>[52] = d_xxxh_ai_lllh [sum of distance weighted (difference of higher
     * cell)]</li>
     * <li>[53] = sumd_xlxh_ai_lllh [sum of distance weighted (sum of
     * differences of higher cell and cell opposite)]</li>
     * <li>[54] = mind_abs_xlxh_ai_lllh [sum of distance weighted (minimum
     * difference magnitude of higher cell and cell opposite)]</li>
     * <li>[55] = maxd_abs_xlxh_ai_lllh [sum of distance weighted (maximum
     * difference magnitude of higher cell and cell opposite)]</li>
     * <li>[56] = sumd_abs_xlxh_ai_lllh [sum of distance weighted (sum of
     * difference magnitudes of higher cell and cell opposite)]</li>
     * <li>[57] = count_lllh [count]</li>
     * <li>[58] = w_lllh [sum of distance weights]</li></ul>
     *
     * <li>6 metrics with all cells higher:
     * <ul>
     * <li>[59] = maxd_llll [sum of distance weighted maximum height
     * differences]</li>
     * <li>[60] = mind_llll [sum of distance weighted minimum height
     * differences]</li>
     * <li>[61] = sumd_llll [sum of distance weighted height differences]</li>
     * <li>[62] = aved_llll [sum of distance weighted average height
     * difference]</li>
     * <li>[63] = count_llll [count]</li>
     * <li>[64] = w_llll [sum of distance weights];</li></ul>
     * </ul>
     *
     * @param g The Grids_GridDouble being processed.
     * @param row The row index of the cell being classified.
     * @param col The column index of the cell being classified.
     * @param distance The distance within which metrics1 will be calculated.
     * @param w An array of kernel weights for weighting metrics1.
     */
    private void metrics1Calculate_All(Grids_GridNumber g,
            BigDecimal noDataValue, long row, long col, Math_BigRational cellX,
            Math_BigRational cellY, BigDecimal cellHeight, int cellDistance,
            double[][] w, double[] m, BigDecimal[] h, BigDecimal[] d,
            BigDecimal[] dd) throws IOException, ClassNotFoundException,
            Exception {
        for (int i = 0; i < m.length; i++) {
            m[i] = 0.0d;
        }
        double weight;
        double upCount;
        double downCount;
        double upness;
        double downness;
        double averageDiff;
        //double averageHeight;
        double noDataCount;
        //double sumWeight;
        int p;
        int q;
        for (p = 0; p <= cellDistance; p++) {
            Math_BigRational y = g.getCellY(row + p);
            Math_BigRational yDiff = y.subtract(cellY);
            for (q = 1; q <= cellDistance; q++) {
                noDataCount = 0.0d;
                Math_BigRational x = g.getCellX(col + q);
                weight = w[p][q];
                if (weight > 0) {
                    Math_BigRational xDiff = x.subtract(cellX);
                    h[0] = g.getCellBigDecimal(x, y);
                    if (h[0] == noDataValue) {
                        h[0] = cellHeight;
                        noDataCount += 1.0d;
                    }
                    h[1] = g.getCellBigDecimal(cellX.add(yDiff), cellY.subtract(xDiff));
                    if (h[1] == noDataValue) {
                        h[1] = cellHeight;
                        noDataCount += 1.0d;
                    }
                    h[2] = g.getCellBigDecimal(cellX.subtract(xDiff), cellY.subtract(yDiff));
                    if (h[2] == noDataValue) {
                        h[2] = cellHeight;
                        noDataCount += 1.0d;
                    }
                    h[3] = g.getCellBigDecimal(cellX.subtract(yDiff), cellY.add(xDiff));
                    if (h[3] == noDataValue) {
                        h[3] = cellHeight;
                        noDataCount += 1.0d;
                    }
                    m[0] += noDataCount;
                    if (noDataCount < 4.0d) {
                        // height[1]   height[0]
                        //      cellHeight
                        // height[2]   height[3]

                        // Calculate basic metrics
                        //averageHeight = 0.0d;
                        averageDiff = 0.0d;
                        downCount = 0.0d;
                        upCount = 0.0d;
                        upness = 0.0d;
                        downness = 0.0d;
                        for (int r = 0; r < 4; r++) {
                            //averageHeight += heights[r];
                            d[r] = h[r].subtract(cellHeight);
                            averageDiff += d[r].doubleValue();
                            if (d[r].compareTo(BigDecimal.ZERO) == 1) {
                                downness += d[r].doubleValue();
                                downCount += 1.0d;
                            } else {
                                if (d[r].compareTo(BigDecimal.ZERO) == -1) {
                                    upness += d[r].doubleValue();
                                    upCount += 1.0d;

                                } else {
                                    m[1] += weight; // flatness
                                }
                            }
                            m[2] += weight * Math.abs(d[r].doubleValue()); // roughness
                        }
                        //averageHeight /= (4.0d - noDataCount);
                        averageDiff /= (4.0d - noDataCount);
                        m[5] += weight * downness; // totalDownness
                        if (downCount > 0.0d) {
                            m[6] += m[5] / downCount; // averageDownness
                        }
                        m[7] += weight * upness; // totalUpness
                        if (upCount > 0.0d) {
                            m[8] += m[7] / upCount; // averageUpness
                        }
                        // Slopyness and levelness similar to slope in getSlopeAspect
                        // slopyness
                        m[3] += weight * Math.sqrt(
                                ((d[0].subtract(d[2])).multiply((d[0]
                                        .subtract(d[2])))).add(((d[1]
                                        .subtract(d[3])).multiply(
                                        (d[1].subtract(d[3]))))).doubleValue());
                        //levelness
                        m[4] += weight * averageDiff;
                        //levelness += weight * Math.abs(averageHeight - cellsize);
                        // diff[1]   diff[0]
                        //    cellHeight
                        // diff[2]   diff[3]
                        metrics1Calculate_Complex(m, d, dd, weight, averageDiff);
                    }
                }
            }
        }
    }

    /**
     *
     * @param m The array of metrics to be processed.
     * @param dbd The array of differences of cell values.
     * @param ddbd The dummy array of differences of cell values.
     * @param w The weight to be applied to weighted metrics.
     * @param ad The average difference in height for diff (N.B This is passed
     * in rather than calculated here because of cell values that were
     * noDataValue in the grid for which metrics1 are being processed.
     */
    private void metrics1Calculate_Complex(double[] m, BigDecimal[] dbd,
            BigDecimal[] ddbd, double w, double ad) {

        // Hack
        int l = dbd.length;
        double[] d = new double[l];
        double[] dd = new double[l];
        for (int i = 0; i < l; i++) {
            d[i] = dbd[i].doubleValue();
            dd[i] = ddbd[i].doubleValue();
        }

        int caseSwitch = metrics1Calculate_CaseSwitch(d);
        // 81 cases
        // Each orthoganal equidistant cell is either heigher, lower, or
        // the same height as the cell at centre.

        switch (caseSwitch) {
            case 0:
                // hhhh
                metrics1Calculate_hhhh(m, d, w, ad);
                break;
            case 1:
                // hhhl
                metrics1Calculate_hhhl(m, d, w);
                break;
            case 2:
                // hhhs
                metrics1Calculate_hhhh(m, d, w, ad);
                metrics1Calculate_hhhl(m, d, w);
                //count_hhhs += 1.0d;
                //w_hhhs += weight;
                break;
            case 3:
                // hhlh
                // Shuffle diff once for hhhl
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                break;
            case 4:
                // hhll
                metrics1Calculate_hhll(m, d, w);
                break;
            case 5:
                // hhls
                // Shuffle diff once for hhhl
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, d, w);
                //count_hhsl += 1.0d;
                //w_hhsl += weight;
                break;
            case 6:
                // hhsh
                metrics1Calculate_hhhh(m, d, w, ad);
                // Shuffle diff once for hhhl
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                //count_hhhs += 1.0d;
                //w_hhhs += weight;
                break;
            case 7:
                // hhsl
                metrics1Calculate_hhhl(m, d, w);
                metrics1Calculate_hhll(m, d, w);
                //count_hhsl += 1.0d;
                //w_hhsl += weight;
                break;

            case 8:
                // hhss
                metrics1Calculate_hhhh(m, d, w, ad);
                metrics1Calculate_hhhl(m, d, w);
                metrics1Calculate_hhll(m, d, w);
                //count_hhss += 1.0d;
                //w_hhss += weight;
                break;
            case 9:
                // hlhh
                // Shuffle diff twice for hhhl
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                break;
            case 10:
                // metrics1Calculate_hlhl
                metrics1Calculate_hlhl(m, d, w);
                break;
            case 11:
                // hlhs
                // Shuffle diff twice for hhhl
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hlhl(m, d, w);
                //count_hshl += 1.0d;
                //w_hshl += weight;
                break;
            case 12:
                // hllh
                // Shuffle diff once for hhll
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                break;
            case 13:
                // hlll
                metrics1Calculate_hlll(m, d, w);
                break;
            case 14:
                // hlls
                // Shuffle diff once for hhll
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, d, w);
                //count_hsll += 1.0d;
                //w_hsll += weight;
                break;
            case 15:
                // hlsh
                // Shuffle diff twice for hhll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                // Shuffle diff once for hhll
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                //count_hhsl += 1.0d;
                //w_hhsl += weight;
                break;
            case 16:
                // hlsl
                metrics1Calculate_hlhl(m, d, w);
                metrics1Calculate_hlll(m, d, w);
                //count_hlsl += 1.0d;
                //w_hlsl += weight;
                break;
            case 17:
                // hlss
                // Shuffle diff twice for hhhl
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hlhl(m, d, w);
                // Shuffle diff once for hhll
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, d, w);
                break;
            case 18:
                // hshh
                metrics1Calculate_hhhh(m, d, w, ad);
                // Shuffle diff twice for hhhl
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                break;
            case 19:
                // hshl
                metrics1Calculate_hhhl(m, d, w);
                metrics1Calculate_hlhl(m, d, w);
                break;
            case 20:
                // hshs
                metrics1Calculate_hhhh(m, d, w, ad);
                metrics1Calculate_hhhl(m, d, w);
                metrics1Calculate_hlhl(m, d, w);
                break;
            case 21:
                // hslh
                // Shuffle diff once for hhhl and hhll
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                break;
            case 22:
                // hsll
                metrics1Calculate_hhll(m, d, w);
                metrics1Calculate_hlll(m, d, w);
                break;
            case 23:
                // hsls
                // Shuffle diff once for hhhl
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, d, w);
                metrics1Calculate_hlll(m, d, w);
                break;
            case 24:
                // hssh
                metrics1Calculate_hhhh(m, d, w, ad);
                // Shuffle diff once for hhhl and hhll
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                break;
            case 25:
                // hssl
                metrics1Calculate_hhhl(m, d, w);
                metrics1Calculate_hhll(m, d, w);
                metrics1Calculate_hlhl(m, d, w);
                metrics1Calculate_hlll(m, d, w);
                break;
            case 26:
                // metrics1Calculate_hsss
                metrics1Calculate_hhhh(m, d, w, ad);
                metrics1Calculate_hhhl(m, d, w);
                metrics1Calculate_hhll(m, d, w);
                metrics1Calculate_hlhl(m, d, w);
                metrics1Calculate_hlll(m, d, w);
                break;
            case 27:
                // lhhh
                // Shuffle diff thrice for hhhl
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                break;
            case 28:
                // lhhl
                // Shuffle diff thrice for hhll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                break;
            case 29:
                // lhhs
                // Shuffle diff thrice for hhhl and hhll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                break;
            case 30:
                // lhlh
                // Shuffle once for hlhl
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hlhl(m, dd, w);
                break;
            case 31:
                // lhll
                // Shuffle diff thrice for hlll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 32:
                // lhls
                // Shuffle diff once for hlhl
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hlhl(m, dd, w);
                // Shuffle diff thrice for hlll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 33:
                // lhsh
                // Shuffle diff thrice for hhhl
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                // Shuffle diff once for hlhl
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hlhl(m, dd, w);
                break;
            case 34:
                // lhsl
                // Shuffle diff thrice for hhll and hlll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 35:
                // lhss
                // Shuffle diff thrice for hhhl, hhll, hlhl, hlll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                metrics1Calculate_hlhl(m, dd, w);
                break;
            case 36:
                // llhh
                // Shuffle diff twice for hhll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                break;
            case 37:
                // llhl
                // Shuffle diff twice for hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 38:
                // llhs
                // Shuffle diff twice for hhll and hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 39:
                // lllh
                // Shuffle diff once for hlll
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 40:
                // llll
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 41:
                // llls
                // Shuffle diff once for hlll
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 42:
                // llsh
                // Shuffle diff twice for hhll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhll(
                        m, dd, w);
                // Shuffle diff once for hlll
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 43:
                // llsl
                // Shuffle diff twice for hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 44:
                // llss
                // Shuffle diff twice for hhll hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 45:
                // lshh
                // Shuffle diff thrice for hhhl
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                // Shuffle diff twice for hhll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                break;
            case 46:
                // lshl
                // Shuffle diff thrice for hhll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                // Shuffle diff twice for hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 47:
                // lshs
                // Shuffle diff thrice for hhhl
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                // Shuffle diff twice for hhll hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 48:
                // lslh
                // Shuffle diff once for hlhl and hlll
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hlhl(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 49:
                // lsll
                // Shuffle diff thrice for hlll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 50:
                // lsls
                // Shuffle diff once for hlhl hlll
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hlhl(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 51:
                // lssh
                // Shuffle diff thrice for hhhl
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                // Shuffle diff twice for hhll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hlhl(m, dd, w);
                // Shuffle diff once for hlll
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 52:
                // lssl
                // Shuffle diff thrice for hhll hlll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 53:
                // lsss
                // Shuffle diff thrice for hhhl hhll hlll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 54:
                // shhh
                metrics1Calculate_hhhh(m, d, w, ad);
                // Shuffle diff thrice for hhhl
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                break;
            case 55:
                // shhl
                metrics1Calculate_hhhl(m, d, w);
                // Shuffle diff thrice for hhll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                break;
            case 56:
                // shhs
                metrics1Calculate_hhhh(m, d, w, ad);
                metrics1Calculate_hhhl(m, d, w);
                // Shuffle diff twice for hhll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                break;
            case 57:
                // shlh
                // Shuffle diff once for hhhl hlhl
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hlhl(m, dd, w);
                break;
            case 58:
                // shll
                metrics1Calculate_hhll(m, d, w);
                // Shuffle diff thrice for hlll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 59:
                // shls
                // Shuffle diff once for hhhl
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, d, w);
                // Shuffle diff thrice for hlll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 60:
                // shsh
                metrics1Calculate_hhhh(m, d, w, ad);
                // Shuffle diff once for hhhl hlhl
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hlhl(m, dd, w);
                break;
            case 61:
                // shsl
                metrics1Calculate_hhhl(m, d, w);
                metrics1Calculate_hhll(m, d, w);
                // Shuffle diff thrice for hlll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 62:
                // shss
                metrics1Calculate_hhhh(m, d, w, ad);
                // Shuffle diff thrice for hhhl hhll hlhl hlll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlhl(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 63:
                // slhh
                // Shuffle diff twice for hhhl hhll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                break;
            case 64:
                // slhl
                metrics1Calculate_hlhl(m, d, w);
                // Shuffle diff twice for hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 65:
                // slhs
                // Shuffle diff twice for hhhl hhll hlhl hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlhl(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 66:
                // sllh
                // Shuffle diff twice for hhll hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 67:
                // slll
                metrics1Calculate_hlll(m, d, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 68:
                // slls
                // Shuffle diff once for hhll
                metrics1Shuffle1(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, d, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 69:
                // slsh
                // Shuffle diff twice for hhhl hhll hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 70:
                // slsl
                metrics1Calculate_hlhl(m, d, w);
                metrics1Calculate_hlll(m, d, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 71:
                // slss
                // Shuffle diff twice for hhhl hhll hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 72:
                // sshh
                metrics1Calculate_hhhh(m, d, w, ad);
                // Shuffle diff twice for hhhl hhll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                break;
            case 73:
                // sshl
                metrics1Calculate_hhhl(m, dd, w);
                // Shuffle diff thrice for hhll
                metrics1Shuffle3(dd, d);
                metrics1Calculate_hhll(m, dd, w);
                // Shuffle diff twice for hlhl hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hlhl(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 74:
                // sshs
                metrics1Calculate_hhhh(m, d, w, ad);
                // Shuffle diff twice for hhhl hhll hlhl hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlhl(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 75:
                // sslh
                // Shuffle diff once for hhhl hhll hlhl hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlhl(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 76:
                // ssll
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 77:
                // ssls
                // Shuffle diff once for hhhl hhll hlhl hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlhl(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 78:
                // sssh
                metrics1Calculate_hhhh(m, d, w, ad);
                // Shuffle diff once for hhhl hhll hlhl hlll
                metrics1Shuffle2(dd, d);
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlhl(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                break;
            case 79:
                // sssl
                metrics1Calculate_hhhl(m, dd, w);
                metrics1Calculate_hhll(m, dd, w);
                metrics1Calculate_hlhl(m, dd, w);
                metrics1Calculate_hlll(m, dd, w);
                metrics1Calculate_llll(m, d, w, ad);
                break;
            case 80:
                // ssss
                // This case should not happen!
                break;
        }
    }

    /**
     * @return Case identifier for the 81 different cases of higher, lower or
     * same height orthogonal equidistant cells.
     *
     * @param d The array of height differences.
     */
    private int metrics1Calculate_CaseSwitch(double[] d) {
        if (d[0] > 0.0d) {
            if (d[1] > 0.0d) {
                if (d[2] > 0.0d) {
                    if (d[3] > 0.0d) {
                        return 0; // metrics1Calculate_hhhh
                    } else {
                        if (d[3] < 0.0d) {
                            return 1; // metrics1Calculate_hhhl
                        } else {
                            return 2; // metrics1Calculate_hhhs
                        }
                    }
                } else {
                    if (d[2] < 0.0d) {
                        if (d[3] > 0.0d) {
                            return 3; // metrics1Calculate_hhlh
                        } else {
                            if (d[3] < 0.0d) {
                                return 4; // metrics1Calculate_hhll
                            } else {
                                return 5; // metrics1Calculate_hhls
                            }
                        }
                    } else {
                        if (d[3] > 0.0d) {
                            return 6; // metrics1Calculate_hhsh
                        } else {
                            if (d[3] < 0.0d) {
                                return 7; // metrics1Calculate_hhsl
                            } else {
                                return 8; // metrics1Calculate_hhss

                            }
                        }
                    }
                }
            } else {
                if (d[1] < 0.0d) {
                    if (d[2] > 0.0d) {
                        if (d[3] > 0.0d) {
                            return 9; // metrics1Calculate_hlhh
                        } else {
                            if (d[3] < 0.0d) {
                                return 10; // metrics1Calculate_hlhl
                            } else {
                                return 11; // metrics1Calculate_hlhs
                            }
                        }
                    } else {
                        if (d[2] < 0.0d) {
                            if (d[3] > 0.0d) {
                                return 12; // metrics1Calculate_hllh
                            } else {
                                if (d[3] < 0.0d) {
                                    return 13; // metrics1Calculate_hlll
                                } else {
                                    return 14; // metrics1Calculate_hlls
                                }
                            }
                        } else {
                            if (d[3] > 0.0d) {
                                return 15; // metrics1Calculate_hlsh
                            } else {
                                if (d[3] < 0.0d) {
                                    return 16; // metrics1Calculate_hlsl
                                } else {
                                    return 17; // metrics1Calculate_hlss

                                }
                            }
                        }
                    }
                } else {
                    if (d[2] > 0.0d) {
                        if (d[3] > 0.0d) {
                            return 18; // metrics1Calculate_hshh
                        } else {
                            if (d[3] < 0.0d) {
                                return 19; // metrics1Calculate_hshl
                            } else {
                                return 20; // metrics1Calculate_hshs
                            }
                        }
                    } else {
                        if (d[2] < 0.0d) {
                            if (d[3] > 0.0d) {
                                return 21; // metrics1Calculate_hslh
                            } else {
                                if (d[3] < 0.0d) {
                                    return 22; // metrics1Calculate_hsll
                                } else {
                                    return 23; // metrics1Calculate_hsls
                                }
                            }
                        } else {
                            if (d[3] > 0.0d) {
                                return 24; // metrics1Calculate_hssh
                            } else {
                                if (d[3] < 0.0d) {
                                    return 25; // metrics1Calculate_hssl
                                } else {
                                    return 26; // metrics1Calculate_hsss
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (d[0] < 0.0d) {
                if (d[1] > 0.0d) {
                    if (d[2] > 0.0d) {
                        if (d[3] > 0.0d) {
                            return 27; // metrics1Calculate_lhhh
                        } else {
                            if (d[3] < 0.0d) {
                                return 28; // metrics1Calculate_lhhl
                            } else {
                                return 29; // metrics1Calculate_lhhs
                            }
                        }
                    } else {
                        if (d[2] < 0.0d) {
                            if (d[3] > 0.0d) {
                                return 30; // metrics1Calculate_lhlh
                            } else {
                                if (d[3] < 0.0d) {
                                    return 31; // metrics1Calculate_lhll
                                } else {
                                    return 32; // metrics1Calculate_lhls
                                }
                            }
                        } else {
                            if (d[3] > 0.0d) {
                                return 33; // metrics1Calculate_lhsh
                            } else {
                                if (d[3] < 0.0d) {
                                    return 34; // metrics1Calculate_lhsl
                                } else {
                                    return 35; // metrics1Calculate_lhss
                                }
                            }
                        }
                    }
                } else {
                    if (d[1] < 0.0d) {
                        if (d[2] > 0.0d) {
                            if (d[3] > 0.0d) {
                                return 36; // metrics1Calculate_llhh
                            } else {
                                if (d[3] < 0.0d) {
                                    return 37; // metrics1Calculate_llhl
                                } else {
                                    return 38; // metrics1Calculate_llhs
                                }
                            }
                        } else {
                            if (d[2] < 0.0d) {
                                if (d[3] > 0.0d) {
                                    return 39; // metrics1Calculate_lllh
                                } else {
                                    if (d[3] < 0.0d) {
                                        return 40; // metrics1Calculate_llll
                                    } else {
                                        return 41; // metrics1Calculate_llls
                                    }
                                }
                            } else {
                                if (d[3] > 0.0d) {
                                    return 42; // metrics1Calculate_llsh
                                } else {
                                    if (d[3] < 0.0d) {
                                        return 43; // metrics1Calculate_llsl
                                    } else {
                                        return 44; // metrics1Calculate_llss
                                    }
                                }
                            }
                        }
                    } else {
                        if (d[2] > 0.0d) {
                            if (d[3] > 0.0d) {
                                return 45; // metrics1Calculate_lshh
                            } else {
                                if (d[3] < 0.0d) {
                                    return 46; // metrics1Calculate_lshl
                                } else {
                                    return 47; // metrics1Calculate_lshs
                                }
                            }
                        } else {
                            if (d[2] < 0.0d) {
                                if (d[3] > 0.0d) {
                                    return 48; // metrics1Calculate_lslh
                                } else {
                                    if (d[3] < 0.0d) {
                                        return 49; // metrics1Calculate_lsll
                                    } else {
                                        return 50; // metrics1Calculate_lsls
                                    }
                                }
                            } else {
                                if (d[3] > 0.0d) {
                                    return 51; // metrics1Calculate_lssh
                                } else {
                                    if (d[3] < 0.0d) {
                                        return 52; // metrics1Calculate_lssl
                                    } else {
                                        return 53; // metrics1Calculate_lsss
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (d[1] > 0.0d) {
                    if (d[2] > 0.0d) {
                        if (d[3] > 0.0d) {
                            return 54; // metrics1Calculate_shhh
                        } else {
                            if (d[3] < 0.0d) {
                                return 55; // metrics1Calculate_shhl
                            } else {
                                return 56; // metrics1Calculate_shhs
                            }
                        }
                    } else {
                        if (d[2] < 0.0d) {
                            if (d[3] > 0.0d) {
                                return 57; // metrics1Calculate_shlh
                            } else {
                                if (d[3] < 0.0d) {
                                    return 58; // metrics1Calculate_shll
                                } else {
                                    return 59; // metrics1Calculate_shls
                                }
                            }
                        } else {
                            if (d[3] > 0.0d) {
                                return 60; // metrics1Calculate_shsh
                            } else {
                                if (d[3] < 0.0d) {
                                    return 61; // metrics1Calculate_shsl
                                } else {
                                    return 62; // metrics1Calculate_shss
                                }
                            }
                        }
                    }
                } else {
                    if (d[1] < 0.0d) {
                        if (d[2] > 0.0d) {
                            if (d[3] > 0.0d) {
                                return 63; // metrics1Calculate_slhh
                            } else {
                                if (d[3] < 0.0d) {
                                    return 64; // metrics1Calculate_slhl
                                } else {
                                    return 65; // metrics1Calculate_slhs
                                }
                            }
                        } else {
                            if (d[2] < 0.0d) {
                                if (d[3] > 0.0d) {
                                    return 66; // metrics1Calculate_sllh
                                } else {
                                    if (d[3] < 0.0d) {
                                        return 67; // metrics1Calculate_slll
                                    } else {
                                        return 68; // metrics1Calculate_slls
                                    }
                                }
                            } else {
                                if (d[3] > 0.0d) {
                                    return 69; // metrics1Calculate_slsh
                                } else {
                                    if (d[3] < 0.0d) {
                                        return 70; // metrics1Calculate_slsl
                                    } else {
                                        return 71; // metrics1Calculate_slss
                                    }
                                }
                            }
                        }
                    } else {
                        if (d[2] > 0.0d) {
                            if (d[3] > 0.0d) {
                                return 72; // metrics1Calculate_sshh
                            } else {
                                if (d[3] < 0.0d) {
                                    return 73; // metrics1Calculate_sshl
                                } else {
                                    return 74; // metrics1Calculate_sshs
                                }
                            }
                        } else {
                            if (d[2] < 0.0d) {
                                if (d[3] > 0.0d) {
                                    return 75; // metrics1Calculate_sslh
                                } else {
                                    if (d[3] < 0.0d) {
                                        return 76; // metrics1Calculate_ssll
                                    } else {
                                        return 77; // metrics1Calculate_ssls
                                    }
                                }
                            } else {
                                if (d[3] > 0.0d) {
                                    return 78; // metrics1Calculate_sssh
                                } else {
                                    if (d[3] < 0.0d) {
                                        return 79; // metrics1Calculate_sssl
                                    } else {
                                        return 80; // metrics1Calculate_ssss
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Shuffles dummyDiff such that: dd[0] = d[3]; dd[1] = d[0]; dd[2] = d[1];
     * dd[3] = d[2].
     */
    private void metrics1Shuffle1(double[] dd, double[] d) {
        dd[0] = d[3];
        dd[1] = d[0];
        dd[2] = d[1];
        dd[3] = d[2];
    }

    /**
     * Shuffles dummyDiff such that: dd[0] = d[2]; dd[1] = d[3]; dd[2] = d[0];
     * dd[3] = d[1].
     */
    private void metrics1Shuffle2(double[] dd, double[] d) {
        dd[0] = d[2];
        dd[1] = d[3];
        dd[2] = d[0];
        dd[3] = d[1];
    }

    /**
     * Shuffles dd such that: d[2]; dd[1] = d[3]; dd[2] = d[0]; dd[3] = d[1].
     */
    private void metrics1Shuffle3(double[] dd, double[] d) {
        dd[0] = d[1];
        dd[1] = d[2];
        dd[2] = d[3];
        dd[3] = d[0];

    }

    /**
     * For processing 6 metrics with all cells higher or same.
     *
     * @param m The array of metrics to be processed.
     * @param d The array of differences of cell values.
     * @param w The weight to be applied to weighted metrics.
     * @param ad The average difference in height for diff (N.B This is passed
     * in rather than calculated here because of cell values that were
     * noDataValue in the grid for which metrics1 are being processed.
     */
    private void metrics1Calculate_hhhh(double[] m, double[] d, double w,
            double ad) {
        m[9] += w * Math.max(Math.max(d[0], d[1]), Math.max(d[2], d[3]));
        m[10] += w * Math.min(Math.min(d[0], d[1]), Math.min(d[2], d[3]));
        m[11] += w * (d[0] + d[1] + d[2] + d[3]);
        m[12] += w * ad;
        m[13] += 1.0d;
        m[14] += w;
    }

    /**
     * For processing a11 metrics with one cell lower or same.
     *
     * @param m The array of metrics to be processed.
     * @param d The array of differences of cell values.
     * @param w The weight to be applied to weighted metrics.
     */
    private void metrics1Calculate_hhhl(double[] m, double[] d, double w) {
        m[15] += w * Math.min(d[0], d[2]);
        m[16] += w * Math.max(d[0], d[2]);
        m[17] += w * (d[0] + d[2]);
        m[18] += w * d[1];
        m[19] += w * d[3];
        m[20] += w * (d[1] + d[3]);
        m[21] += w * Math.min(d[1], Math.abs(d[3]));
        m[22] += w * Math.max(d[1], Math.abs(d[3]));
        m[23] += w * (d[1] + Math.abs(d[3]));
        m[24] += 1.0d;
        m[25] += w;
    }

    /**
     * For processing 11 metrics with opposite cells lower/higher or same.
     *
     * @param m The array of metrics to be processed.
     * @param d The array of differences of cell values.
     * @param w The weight to be applied to weighted metrics.
     *
     */
    private void metrics1Calculate_hlhl(double[] m, double[] d, double w) {
        m[26] += w * Math.min(d[0], d[2]);
        m[27] += w * Math.max(d[0], d[2]);
        m[28] += w * (d[0] + d[2]);
        m[29] += w * Math.min(d[1], d[3]);
        m[30] += w * Math.max(d[1], d[3]);
        m[31] += w * (d[1] + d[3]);
        m[32] += w * (Math.min(Math.abs(Math.max(d[1], d[3])), Math.min(d[0], d[2])));
        m[33] += w * (Math.max(Math.abs(Math.min(d[1], d[3])), Math.max(d[0], d[2])));
        m[34] += w * (d[0] + Math.abs(d[1]) + d[2] + Math.abs(d[3]));
        m[35] += 1.0d;
        m[36] += w;
    }

    /**
     * For processing a11 metrics with adjacent cells lower/higher or same.
     *
     * @param m The array of metrics to be processed.
     * @param d The array of differences of cell values.
     * @param w The weight to be applied to weighted metrics.
     */
    private void metrics1Calculate_hhll(double[] m, double[] d, double w) {
        m[37] += w * Math.min(d[0], d[1]);
        m[38] += w * Math.max(d[0], d[1]);
        m[39] += w * (d[0] + d[1]);
        m[40] += w * Math.min(d[2], d[3]);
        m[41] += w * Math.max(d[2], d[3]);
        m[42] += w * (d[2] + d[3]);
        m[43] += w * (Math.min(Math.abs(Math.max(d[2], d[3])), Math.min(d[1], d[0])));
        m[44] += w * (Math.max(Math.abs(Math.min(d[2], d[3])), Math.max(d[1], d[0])));
        m[45] += w * (d[1] + Math.abs(d[2]) + d[0] + Math.abs(d[3]));
        m[46] += 1.0d;
        m[47] += w;
    }

    /**
     * For processing a11 metrics with one cell higher or same.
     *
     * @param m The array of metrics to be processed.
     * @param d The array of differences of cell values.
     * @param w The weight to be applied to weighted metrics.
     */
    private void metrics1Calculate_hlll(double[] m, double[] d, double w) {
        m[48] += w * Math.min(d[1], d[3]);
        m[49] += w * Math.max(d[1], d[3]);
        m[50] += w * (d[1] + d[3]);
        m[51] += w * d[2];
        m[52] += w * d[0];
        m[53] += w * (d[2] + d[0]);
        m[54] = w * Math.min(d[0], Math.abs(d[2]));
        m[55] = w * Math.max(d[0], Math.abs(d[2]));
        m[56] = w * (d[0] + Math.abs(d[2]));
        m[57] += 1.0d;
        m[58] += w;
    }

    /**
     * For processing 6 metrics with all cells lower or same.
     *
     * @param m The array of metrics to be processed.
     * @param d The array of differences of cell values.
     * @param w The weight to be applied to weighted metrics.
     */
    private void metrics1Calculate_llll(double[] m, double[] d, double w,
            double averageDiff) {
        m[59] += w * Math.max(Math.max(d[0], d[1]), Math.max(d[2], d[3]));
        m[60] += w * Math.min(Math.min(d[0], d[1]), Math.min(d[2], d[3]));
        m[61] += w * (d[0] + d[1] + d[2] + d[3]);
        m[62] += w * averageDiff;
        m[63] += 1.0d;
        m[64] += w;
    }

//    /**
//     * Get a set of grids metrics2 where: TODO: metrics2 is a mess.
//     * Need to decide what to do with regard to contour tracing and profile
//     * trace for axes and comparisons. metrics2[0] = slope; metrics2[1] =
//     * aspect; metrics2[2] = no data count; metrics2[3] = contourConcavity;
//     * metrics2[4] = contourConvexity; metrics2[5] = profileConcavity;
//     * metrics2[6] = profileConvexity;
//     *
//     * @param g
//     * @param distance
//     * @param weightIntersect
//     * @param weightFactor
//     * @param hoome
//     * @param samplingDensity
//     * @param gf
//     * @return
//     * @throws java.io.IOException If encountered.
//     * @throws java.lang.ClassNotFoundException If encountered.
//     */
//    public Grids_GridDouble[] getMetrics2(Grids_GridDouble g, BigDecimal distance,
//            BigDecimal weightIntersect, int weightFactor, int samplingDensity,
//            Grids_GridDoubleFactory gf, int dp, RoundingMode rm, boolean hoome)
//            throws IOException,
//            ClassNotFoundException, Exception {
//        try {
//            env.checkAndMaybeFreeMemory();
//            Grids_GridDouble[] r = new Grids_GridDouble[7];
//            long ncols = g.getNCols();
//            long nrows = g.getNRows();
//            Grids_Dimensions dimensions = g.getDimensions();
//            double gridNoDataValue = g.getNoDataValue();
//            Grids_GridDouble[] slopeAndAspect = null;
//            //Grids_GridDouble[] slopeAndAspect = getSlopeAspect(g, distance,
//            //    weightIntersect, weightFactor, hoome);
//            r[0] = slopeAndAspect[0];
//            r[1] = slopeAndAspect[1];
//            for (int i = 0; i < r.length; i++) {
//                r[i] = gf.create(nrows, ncols, dimensions);
//            }
//            double[] metrics2;
//            Point2D.Double[] metrics2Points;
//            BigDecimal[] weights;
//            long row;
//            long col;
//            for (row = 0; row < nrows; row++) {
//                for (col = 0; col < ncols; col++) {
//                    if (g.getCell(row, col) != gridNoDataValue) {
//                        double slope = r[0].getCell(row, col);
//                        double aspect = r[1].getCell(row, col);
//                        metrics2Points = getMetrics2Points(slopeAndAspect,
//                                distance, samplingDensity);
//                        weights = Grids_Kernel.getKernelWeights(g, row, col,
//                                distance, weightIntersect, weightFactor,
//                                metrics2Points, dp, rm);
//                        metrics2 = getMetrics2(g, row, col, slopeAndAspect,
//                                distance, weights, dp, rm);
//                        for (int i = 0; i < r.length; i++) {
//                            r[i].setCell(row, col, metrics2[i]);
//                        }
//                    }
//                }
//                System.out.println("Done row " + row);
//            }
//            r[2].setName("");
//            r[3].setName("");
//            r[4].setName("");
//            r[5].setName("");
//            r[6].setName("");
//            return r;
//        } catch (OutOfMemoryError e) {
//            if (hoome) {
//                getMetrics2(g, distance, weightIntersect, weightFactor,
//                        samplingDensity, gf, dp, rm, hoome);
//            }
//            throw e;
//        }
//    }
//
//    /**
//     * Returns a Point2D.Double[] points that are sample points based on a
//     * regular sampling around slope If samplingDensity
//     *
//     *
//     */
//    private Point2D.Double[] getMetrics2Points(Grids_GridDouble[] slopeAndAspect,
//            BigDecimal distance, int samplingDensity) {
//        Point2D.Double[] metrics2Points = null;
//        return metrics2Points;
//    }
//
//    private double[] getMetrics2(Grids_GridDouble g, long row, long col,
//            Grids_GridDouble[] slopeAndAspect, BigDecimal distance,
//            BigDecimal[] weights, int dp, RoundingMode rm) {
//        double[] metrics2 = null;
//        return metrics2;
//    }
    /**
     * Get a grid containing values which indicate the direction (1 2 3 4 0 5 6
     * 7 8) of the maximum down slope for the immediate 8 cell neighbourhood. If
     * there is no downhill slope then the flow direction is 0.
     *
     * @param g the Grids_GridDouble to be processed
     * @param gf the Grids_GridDoubleFactory used to create result
     * @return A grid containing values which indicate the direction (1 2 3 4 0
     * 5 6 7 8) of the maximum down slope for the immediate 8 cell
     * neighbourhood. If there is no downhill slope then the flow direction is
     * 0.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridDouble getMaxFlowDirection(Grids_GridDouble g,
            Grids_GridDoubleFactory gf) throws IOException,
            ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        long nrows = g.getNRows();
        long ncols = g.getNCols();
        double noDataValue = g.getNoDataValue();
        Grids_GridDouble r = gf.create(nrows, ncols, g.getDimensions());
        long row;
        long col;
        int k;
        int flowDirection;
        double[] z = new double[9];
        double minz;
        int minzCount;
        int minzCountNoDataValue;
        long p;
        long q;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                z[0] = g.getCell(row, col);
                if (z[0] != noDataValue) {
                    minz = Double.MAX_VALUE;
                    minzCount = 0;
                    minzCountNoDataValue = 0;
                    flowDirection = 0;
                    k = 0;
                    for (p = -1; p < 2; p++) {
                        for (q = -1; q < 2; q++) {
                            if (!(p == 0 && q == 0)) {
                                k++;
                                z[k] = g.getCell(row + p, col + q);
                                if (z[k] != noDataValue) {
                                    if (z[k] <= minz && z[k] < z[0]) {
                                        if (z[k] == minz) {
                                            minzCount++;
                                        } else {
                                            minz = z[k];
                                            minzCount = 1;
                                            flowDirection = k;
                                        }
                                    }
                                } else {
                                    minzCountNoDataValue++;
                                }
                            }
                        }
                    }
                    // If more than one flowDirection randomly assign one
                    if (minzCount + minzCountNoDataValue > 1) {
                        int[] min = new int[minzCount + minzCountNoDataValue];
                        int minID = 0;
                        double random = Math.random();
                        for (int k2 = 1; k2 < z.length; k2++) {
                            if (z[k2] == minz || z[k2] == noDataValue) {
                                min[minID] = k2;
                                minID++;
                            }
                        }
                        flowDirection = min[(int) Math.floor(random
                                * (minzCount + minzCountNoDataValue))];
                    }
                    r.setCell(row, col, (double) flowDirection);
                }
            }
        }
        return r;
    }

    /**
     * Returns a Set of cell ID for cells for which neighbouring cells in the
     * immediate 8 cell neighbourhood are either the same, lower or
     * noDataValues.
     *
     * @param g The grid to process.
     * @return A Set of cell ID for cells for which neighbouring cells in the
     * immediate 8 cell neighbourhood are either the same, lower or
     * noDataValues.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public HashSet<Grids_2D_ID_long> getPeakGridCells(Grids_GridDouble g)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        HashSet<Grids_2D_ID_long> r = new HashSet<>();
        double ndv = g.getNoDataValue();
        double[] h = new double[9];
        int k;
        int nChunkRows = g.getNChunkRows();
        int nChunkCols = g.getNChunkCols();
        for (int cr = 0; cr < nChunkRows; cr++) {
            for (int cc = 0; cc < nChunkCols; cc++) {
                Grids_2D_ID_int cid = new Grids_2D_ID_int(cr, cc);
                Grids_ChunkDouble c = g.getChunk(cid, cr, cc);
                int chunkNRows = g.getChunkNRows(cr);
                int chunkNCols = g.getChunkNCols(cc);
                for (int row = 0; row < chunkNRows; row++) {
                    for (int col = 0; col < chunkNCols; col++) {
                        h[0] = c.getCell(row, col);
                        if (h[0] != ndv) {
                            k = 0;
                            for (int p = -1; p < 2; p++) {
                                for (int q = -1; q < 2; q++) {
                                    if (!(p == 0 && q == 0)) {
                                        k++;
                                        //g.getCellRow(longnrows)
                                        h[k] = g.getCell(row + p, col + q);
                                    }
                                }
                            }
                            // This deals with single isolated cells surrounded by noDataValues
                            if ((h[1] <= h[0] || h[1] == ndv)
                                    && (h[2] <= h[0] || h[2] == ndv)
                                    && (h[3] <= h[0] || h[3] == ndv)
                                    && (h[4] <= h[0] || h[4] == ndv)
                                    && (h[5] <= h[0] || h[5] == ndv)
                                    && (h[6] <= h[0] || h[6] == ndv)
                                    && (h[7] <= h[0] || h[7] == ndv)
                                    && (h[8] <= h[0] || h[8] == ndv)) {
                                r.add(g.getCellID(row, col));
                            }
                        }
                    }
                }
            }
        }
        return r;
    }

//    /**
//     * There are many estimates of flow that can be generated and many models
//     * developed in hydrology. These methods are simplistic. The basics are 
//     * that discharge from any cell is a simple mutliple of velocity and 
//     * depth.
//     * 
//     * A measure of velocity is obtained by measuring slope and the depth of 
//     * discharge itself where the slope is given by the change in height 
//     * divided by the distance.
//     * 
//     * The algorithm:
//     * A height grid is initialised using grid.
//     * A coincident accumulation grid is initialised
//     * Step 1: A v of rainfall is added to all cells in accumulation.
//     * Step 2: A proportion of this rainfall is then distributed to neighbouring
//     *         cells based on Mannings discharge equations. This acumulates a
//     *         proportion based on the difference in height of neighbouring 
//     *         cells which are down slope. If no immediate neighbours are 
//     *         downslope then the height cell is raised by v.
//     * Step 3: Repeat Steps 2 and 3 iterations a number of times.
//     * Step 4: Return height and accumulation.
//     * NB Care needs to be taken to specify outflow cells
//     * TODO:
//     * 1. Change precipitation to be a grid
//     * 2. Have a variable frictionFactor
//     */
//    public Grids_GridDouble getFlowAccumulation(Grids_GridDouble grid,
//            int iterations, double precipitation, HashSet outflowCellIDs,
//            Grids_GridDoubleFactory gridFactory, boolean hoome) {
//        int _MessageLength = 1000;
//        String _Message0 = env.initString(_MessageLength, hoome);
//        String _Message = env.initString(_MessageLength, hoome);
//        Grids_GridDouble flowAccumulation = getInitialFlowAccumulation(
//                grid,
//                precipitation,
//                outflowCellIDs,
//                gridFactory,
//                hoome);
//        _Message = "intitialFlowAccumulation";
//        _Message = env.println(_Message, _Message0);
//        _Message = flowAccumulation.toString();
//        _Message = env.println(_Message, _Message0);
//        for (int iteration = 0; iteration < iterations; iteration ++) {
//            doFlowAccumulation(
//                    flowAccumulation,
//                    grid,
//                    precipitation,
//                    outflowCellIDs,
//                    gridFactory,
//                    hoome);
//            _Message = "flowAccumulation iteration " + (iteration + 1);
//            _Message = env.println(_Message, _Message0);
//            _Message = flowAccumulation.toString();
//            _Message = env.println(_Message, _Message0);
//        }
//        return flowAccumulation;
//    }
//    /**
//     * frictionFactor = 75.0d;
//     * constant = 8.0d * 9.81d / frictionFactor;
//     * velocity = Math.sqrt(constant * waterDepth * changeInDepth / ChangeInLength);
//     * discharge = velocity * waterDepth
//     */
//    public Grids_GridDouble getInitialFlowAccumulation(
//            Grids_GridDouble grid,
//            double precipitation,
//            HashSet outflowCellIDs,
//            Grids_GridDoubleFactory gridFactory,
//            boolean hoome) {
//        //double constant = 8.0d * 9.81d / 75.0d ;
//        double constant = 1.0d;
//        long nrows = grid.getNRows(hoome);
//        long ncols = grid.getNCols(hoome);
//        BigDecimal[] dimensions = grid.getDimensions(hoome);
//        double noDataValue = grid.getNoDataValue(hoome);
//        // Precipitate
//        Grids_GridDouble flowAccumulation = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
//        flowAccumulation = addToGrid(flowAccumulation, precipitation, hoome);
//        flowAccumulation = (Grids_GridDouble) mask(flowAccumulation, grid, gridFactory, hoome);
//        Grids_GridDouble tempFlowAccumulation = (Grids_GridDouble) gridFactory.create(flowAccumulation);
//        double[][] surfaceHeights = new double[3][3];
//        double[][] discharge = new double[3][3];
//        double slope;
//        double velocity;
//        double waterDepth;
//        double movingWaterDepth;
//        double numberOfDownSlopes;
//        double totalDischarge;
//        double sumDischarge;
//        long row;
//        long col;
//        int p;
//        int q;
//        // Deal with outflowCellIDs
//        Iterator ite = outflowCellIDs.iterator();
//        CellID cellID;
//        while (ite.hasNext()) {
//            cellID = (CellID) ite.next();
//            row = cellID.getRow();
//            col = cellID.getCellCol();
//            waterDepth = tempFlowAccumulation.getCell(row, col, hoome);
//            flowAccumulation.addToCell(row, col, - waterDepth / 2.0d, hoome);
//        }
//        for (row = 0; row < nrows; row ++) {
//            for (col = 0; col < ncols; col ++) {
//                surfaceHeights[1][1] = grid.getCell(row, col, hoome);
//                if (surfaceHeights[1][1] != noDataValue) {
//                    waterDepth = tempFlowAccumulation.getCell(row, col, hoome);
//                    surfaceHeights[1][1] += waterDepth;
//                    numberOfDownSlopes = 0.0d;
//                    sumDischarge = 0.0d;
//                    totalDischarge = 0.0d;
//                    for (p = 0; p < 3; p ++) {
//                        for (q = 0; q < 3; q ++) {
//                            if (! (p == 1 && q == 1)) {
//                                surfaceHeights[p][q] = grid.getCell(row + p - 1, col + q - 1, hoome);
//                                movingWaterDepth = Math.min(waterDepth, surfaceHeights[1][1] - surfaceHeights[p][q]);
//                                if ((surfaceHeights[p][q] != noDataValue) && (surfaceHeights[p][q] < surfaceHeights[1][1])) {
//                                    numberOfDownSlopes += 1.0d;
//                                    if (p == q || (p == 0 && q == 2) || (p == 2 && q == 0)) {
//                                        slope = surfaceHeights[1][1] - surfaceHeights[p][q] / (Math.sqrt(2.0d));
//                                    } else {
//                                        slope = surfaceHeights[1][1] - surfaceHeights[p][q];
//                                    }
//                                    velocity = Math.sqrt(constant * movingWaterDepth * slope);
//                                    discharge[p][q] = velocity * movingWaterDepth;
//                                    sumDischarge += discharge[p][q];
//                                }
//                            }
//                        }
//                    }
//                    if (numberOfDownSlopes > 0.0d) {
//                        for (p = 0; p < 3; p ++) {
//                            for (q = 0; q < 3; q ++) {
//                                if (! (p == 1 && q == 1)) {
//                                    if (surfaceHeights[p][q] != noDataValue && surfaceHeights[p][q] < surfaceHeights[1][1]) {
//                                        movingWaterDepth = Math.min(waterDepth, surfaceHeights[1][1] - surfaceHeights[p][q]);
//                                        discharge[p][q] = (discharge[p][q] / sumDischarge) * (movingWaterDepth / 2.0d); // 50%
//                                        totalDischarge += discharge[p][q];
//                                        flowAccumulation.addToCell(row + p - 1, col + q - 1, discharge[p][q], hoome);
//                                    }
//                                }
//                            }
//                        }
//                        flowAccumulation.addToCell(row, col, - totalDischarge, hoome);
//                    }
//                }
//            }
//        }
//        return flowAccumulation;
//    }
//    /**
//     * TODO: docs
//     * frictionFactor = 75.0d;
//     * constant = 8.0d * 9.81d / frictionFactor;
//     * velocity = Math.sqrt(constant * waterDepth * changeInDepth / ChangeInLength);
//     * discharge = velocity * waterDepth
//     */
//    public Grids_GridDouble doFlowAccumulation(
//            Grids_GridDouble flowAccumulation,
//            Grids_GridDouble grid,
//            double precipitation,
//            HashSet outflowCellIDs,
//            //Grid2DSquareCellDoubleFactory gridFactory,
//            boolean hoome) {
//        //double constant = 8.0d * 9.81d / 75.0d ;
//        double constant = 1.0d;
//        long nrows = grid.getNRows(hoome);
//        long ncols = grid.getNCols(hoome);
//        BigDecimal[] dimensions = grid.getDimensions(hoome);
//        double noDataValue = grid.getNoDataValue(hoome);
//        int gridStatisticsType = 1;
//        // Precipitate
//        addToGrid(
//                flowAccumulation,
//                precipitation,
//                hoome);
//        mask(
//                flowAccumulation,
//                grid,
//                hoome);
//        Grids_GridDouble tempFlowAccumulation =
//                (Grids_GridDouble) gridFactory.create(flowAccumulation);
//        double waterDepth;
//        double movingWaterDepth;
//        double[][] surfaceHeights = new double[3][3];
//        double[][] discharge = new double[3][3];
//        double slope;
//        double velocity;
//        double numberOfDownSlopes;
//        double totalDischarge;
//        double sumDischarge;
//        long row;
//        long col;
//        for (row = 0; row < nrows; row ++) {
//            for (col = 0; col < ncols; col ++) {
//                surfaceHeights[1][1] = grid.getCell(row, col, hoome);
//                if (surfaceHeights[1][1] != noDataValue) {
//                    waterDepth = tempFlowAccumulation.getCell(row, col, hoome);
//                    surfaceHeights[1][1] += waterDepth;
//                    numberOfDownSlopes = 0.0d;
//                    sumDischarge = 0.0d;
//                    totalDischarge = 0.0d;
//                    if (outflowCellIDs.contains(grid.getCellID(row, col, hoome))) {
//                        // Simply lose a proportion of waterDepth (consider a friction factor)
//                        flowAccumulation.addToCell(row, col, - waterDepth / 2.0d, hoome);
//                        /*for (int p = 0; p < 3; p ++) {
//                            for (int q = 0; q < 3; q ++) {
//                                if (! (p == 1 && q == 1)) {
//                                    if (grid.getCell(row + p - 1, col + q - 1) == noDataValue) {
//                                        numberOfDownSlopes += 1.0d;
//                                        if (p == q || (p == 0 && q == 2) || (p == 2 && q == 0)) {
//                                            slope = waterDepth / (Math.sqrt(2.0d));
//                                        } else {
//                                            slope = waterDepth;
//                                        }
//                                        velocity = Math.sqrt(constant * waterDepth * slope);
//                                        discharge[p][q] = velocity * waterDepth;
//                                        sumDischarge += discharge[p][q];
//                                    }
//                                }
//                            }
//                        }
//                        if (numberOfDownSlopes > 0.0d) {
//                            for (int p = 0; p < 3; p ++) {
//                                for (int q = 0; q < 3; q ++) {
//                                    if (! (p == 1 && q == 1)) {
//                                        if (grid.getCell(row + p - 1, col + q - 1) == noDataValue) {
//                                            discharge[p][q] = (discharge[p][q] / sumDischarge) * (waterDepth / 2.0d); // 50%
//                                            totalDischarge += discharge[p][q];
//                                        }
//                                    }
//                                }
//                            }
//                            flowAccumulation.addToCell(row, col, - totalDischarge);
//                        }*/
//                    } else {
//                        for (int p = 0; p < 3; p ++) {
//                            for (int q = 0; q < 3; q ++) {
//                                if (! (p == 1 && q == 1)) {
//                                    surfaceHeights[p][q] = grid.getCell(row + p - 1, col + q - 1, hoome);
//                                    if (surfaceHeights[p][q] != noDataValue) {
//                                        surfaceHeights[p][q] += tempFlowAccumulation.getCell(row + p - 1, col + q - 1, hoome);
//                                        if (surfaceHeights[p][q] < surfaceHeights[1][1]) {
//                                            movingWaterDepth = Math.min(waterDepth, (surfaceHeights[1][1] - surfaceHeights[p][q]));
//                                            numberOfDownSlopes += 1.0d;
//                                            if (p == q || (p == 0 && q == 2) || (p == 2 && q == 0)) {
//                                                slope = (surfaceHeights[1][1] - surfaceHeights[p][q]) / (Math.sqrt(2.0d));
//                                            } else {
//                                                slope = (surfaceHeights[1][1] - surfaceHeights[p][q]);
//                                            }
//                                            velocity = Math.sqrt(constant * movingWaterDepth * slope);
//                                            discharge[p][q] = velocity * movingWaterDepth;
//                                            sumDischarge += discharge[p][q];
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        if (numberOfDownSlopes > 0.0d) {
//                            for (int p = 0; p < 3; p ++) {
//                                for (int q = 0; q < 3; q ++) {
//                                    if (! (p == 1 && q == 1)) {
//                                        if (surfaceHeights[p][q] != noDataValue && surfaceHeights[p][q] < surfaceHeights[1][1]) {
//                                            movingWaterDepth = Math.min(waterDepth, (surfaceHeights[1][1] - surfaceHeights[p][q]));
//                                            discharge[p][q] = (discharge[p][q] / sumDischarge) * (movingWaterDepth / 2.0d); // 50%
//                                            totalDischarge += discharge[p][q];
//                                            flowAccumulation.addToCell(row + p - 1, col + q - 1, discharge[p][q], hoome);
//                                        }
//                                    }
//                                }
//                            }
//                            flowAccumulation.addToCell(row, col, - totalDischarge, hoome);
//                        }
//                    }
//                }
//            }
//        }
//        return flowAccumulation;
//    }
}
