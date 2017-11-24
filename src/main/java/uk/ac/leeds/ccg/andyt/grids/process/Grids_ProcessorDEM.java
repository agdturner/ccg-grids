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

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridIntFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Kernel;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

/**
 * A class of methods relevant to the processing of Digital Elevation Model
 * Data.
 */
public class Grids_ProcessorDEM
        extends Grids_Processor {

    /**
     * Creates a new Grids_ProcessorDEM
     */
    protected Grids_ProcessorDEM() {
    }

    /**
     * Creates a new instance of Grids_ProcessorDEM. By default the logs are
     * appended to the end of the Log _File if it exists. To overwrite the Log
     * _File use: Grids_ProcessorDEM( Directory, false );
     *
     * @param ge
     */
    public Grids_ProcessorDEM(Grids_Environment ge) {
        super(ge);
    }

    /**
     * Calculates and returns measures of the slope and aspect for the
     * Grids_AbstractGridNumber _Grid2DSquareCell passed in.
     *
     * @param g The Grids_AbstractGridNumber to be processed.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then caching operations are initiated prior to retrying.
     * If false then OutOfMemoryErrors are caught and thrown. Defaults: kernel
     * to have distance = ( _Grid2DSquareCell.getDimensions(
     * handleOutOfMemoryError )[ 0 ].doubleValue() ) * ( 3.0d / 2.0d );
     * weightIntersect = 1.0d; weightFactor = 0.0d;
     * @return Grids_GridDouble[] _SlopeAndAspect.
     * @throws java.io.IOException
     */
    public Grids_GridDouble[] getSlopeAspect(
            Grids_AbstractGridNumber g,
            boolean handleOutOfMemoryError)
            throws IOException {
        try {
            ge.getGrids().add(g);
            return getSlopeAspect(g);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return getSlopeAspect(
                        g,
                        handleOutOfMemoryError);
            }
            throw e;
        }
    }

    /**
     * Calculates and returns measures of the slope and aspect for the
     * Grids_AbstractGridNumber _Grid2DSquareCell passed in.
     *
     * @param g The Grids_AbstractGridNumber to be processed. Defaults: kernel
     * to have distance = ( _Grid2DSquareCell.getDimensions(
     * handleOutOfMemoryError )[ 0 ].doubleValue() ) * ( 3.0d / 2.0d );
     * weightIntersect = 1.0d; weightFactor = 0.0d;
     * @return Grids_GridDouble[] _SlopeAndAspect. /n
     * @throws java.io.IOException
     */
    protected Grids_GridDouble[] getSlopeAspect(
            Grids_AbstractGridNumber g)
            throws IOException {
        boolean handleOutOfMemoryError = true;
        // Default distance to contain centroids of immediate neighbours
        // ( ( square root of 2 ) * cellsize ) < distance < ( 2 * cellsize ).
        Grids_Dimensions dimensions = g.getDimensions(handleOutOfMemoryError);
        double distance = (dimensions.getCellsize().doubleValue()) * (3.0d / 2.0d);
        double weightIntersect = 1.0d;
        double weightFactor = 0.0d;
        return getSlopeAspect(
                g,
                distance,
                weightIntersect,
                weightFactor,
                handleOutOfMemoryError);
    }

    public double[][] getNormalDistributionKernelWeights(
            double cellsize,
            double distance,
            boolean handleOutOfMemoryError) {
        try {
            return Grids_Kernel.getNormalDistributionKernelWeights(
                    cellsize, distance);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return getNormalDistributionKernelWeights(
                        cellsize, distance, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param g The Grids_AbstractGridNumber to be processed.
     * @param distance the distance which defines the aggregate region.
     * @param weightIntersect The kernel weighting weight at centre.
     * @param weightFactor The kernel weighting distance decay.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then caching operations are initiated prior to retrying.
     * If false then OutOfMemoryErrors are caught and thrown. (NB. There are
     * various strategies to reduce bias caused by noDataValues. Here: If the
     * cell in grid for which _SlopeAndAspect is being calculated is a
     * noDataValue then the cells in _SlopeAndAspect are assigned their
     * noDataValue. If one of the cells in the calculation of slope and aspect
     * is a noDataValue then its height is taken as the nearest cell value.
     * (Formerly the difference in its height was taken as the average
     * difference in height for those cells with values.) )
     * @return Grids_GridDouble[] _SlopeAndAspect where: _SlopeAndAspect[0] Is
     * the distance weighted aggregate slope over the region. This is normalised
     * by the sum of the weights used and the average distance to give a
     * proportional measure. _SlopeAndAspect[1] Is the distance weighted
     * aggregate aspect over the region. This is the clockwize angle from the y
     * axis (usually North). _SlopeAndAspect[2] Is the sine of
     * _SlopeAndAspect[1]. _SlopeAndAspect[3] Is the sine of _SlopeAndAspect[1]
     * + ( ( Pi * 1 ) / 8). _SlopeAndAspect[4] Is the sine of _SlopeAndAspect[1]
     * + ( ( Pi * 2 ) / 8). _SlopeAndAspect[5] Is the sine of _SlopeAndAspect[1]
     * + ( ( Pi * 3 ) / 8). _SlopeAndAspect[6] Is the sine of _SlopeAndAspect[1]
     * + ( ( Pi * 4 ) / 8). _SlopeAndAspect[7] Is the sine of _SlopeAndAspect[1]
     * + ( ( Pi * 5 ) / 8). _SlopeAndAspect[8] Is the sine of _SlopeAndAspect[1]
     * + ( ( Pi * 6 ) / 8). _SlopeAndAspect[9] Is the sine of _SlopeAndAspect[1]
     * + ( ( Pi * 7 ) / 8).
     * @throws java.io.IOException
     */
    public Grids_GridDouble[] getSlopeAspect(
            Grids_AbstractGridNumber g,
            double distance,
            double weightIntersect,
            double weightFactor,
            boolean handleOutOfMemoryError)
            throws IOException {
        try {
            System.out.println("getSlopeAspect(AbstractGrid2DSquareCell,double,double,double,boolean)");
            ge.getGrids().add(g);
            Grids_AbstractGridChunkDouble chunkDouble;
            Grids_GridDouble gridDouble;
            Grids_AbstractGridChunkInt chunk;
            Grids_GridInt gridInt;
            int slopeAndAspectSize = 10;
            Grids_GridDouble[] slopeAndAspect = new Grids_GridDouble[slopeAndAspectSize];
            boolean shortName = true; // Filenames that are too long are problematic!
            boolean swapToFileCache;
            // Initialisation
            long ncols = g.getNCols(handleOutOfMemoryError);
            long nrows = g.getNRows(handleOutOfMemoryError);
            Grids_Dimensions dimensions = g.getDimensions(handleOutOfMemoryError);
            double cellsize = g.getCellsizeDouble(handleOutOfMemoryError);
            long cellDistance = (long) Math.ceil(distance / cellsize);
            double thisDistance;
            double x = 0.0d;
            double y = 0.0d;
            double thisX;
            double thisY;
            double diffX;
            double diffY;
            double diffHeight;
            double angle;
            double sinAngle;
            double cosAngle;
            double slope;
            double aspect;
            double[][] weights = getNormalDistributionKernelWeights(
                    g.getCellsizeDouble(handleOutOfMemoryError),
                    distance,
                    handleOutOfMemoryError);
            double weight;
            long cellRowIndex;
            long cellColIndex;
            long p;
            long q;
            int chunkCellRowIndex;
            int chunkCellColIndex;
            int cri;
            int cci;
            int chunkRows = g.getNChunkRows(handleOutOfMemoryError);
            int chunkCols = g.getNChunkCols(handleOutOfMemoryError);
            int chunkNrows;
            int chunkNcols;
            double double0;
            double double1;
            double double3;
            double PI = Math.PI;
            double doubleOne = 1.0d;
            double doubleTwo = 2.0d;
            double doubleThree = 3.0d;
            double doubleFour = 4.0d;
            double doubleFive = 5.0d;
            double doubleSix = 6.0d;
            double doubleSeven = 7.0d;
            double doubleEight = 8.0d;
            double doubleOneHundred = 100.0d;
            double weightSum = 0.0d;
            double distanceSum = 0.0d;
            double numberObservations = 0.0d;
            double averageDistance;
            long long0;
            int int0;
            int int1;
            for (p = -cellDistance; p <= cellDistance; p++) {
                thisY = p * cellsize;
                for (q = -cellDistance; q <= cellDistance; q++) {
                    if (!(p == 0 && q == 0)) {
                        long0 = p + cellDistance;
                        int0 = (int) long0;
                        long0 = q + cellDistance;
                        int1 = (int) (long0);
                        thisX = q * cellsize;
                        thisDistance = distance(
                                x,
                                y,
                                thisX,
                                thisY,
                                handleOutOfMemoryError);
                        if (thisDistance <= distance) {
                            weight = weights[int0][int1];
                            weightSum += weight;
                            distanceSum += thisDistance;
                            numberObservations++;
                        }
                    }
                }
            }
            averageDistance = distanceSum / numberObservations;
            String _Grid2DSquareCellName = g.getName(handleOutOfMemoryError);
            int _FilenameLength = 1000;
            String filename;
            File dir;
            //Grid2DSquareCellDouble _Grid2DSquareCellDouble = new Grids_GridDouble( _AbstractGrid2DSquareCell_HashSet );
            double noDataValueDoubleInt;
            double heightDoubleInt;
            double thisHeightDoubleInt;
            int noDataValueInt;
            int heightInt;
            int thisHeightInt;
            Object[] _NewFileResult = new Object[2];
            File directory = getDirectory(handleOutOfMemoryError);
            System.out.println("Initialising _SlopeAndAspect[ 0 ]");
            if (shortName) {
                filename = "slope_" + averageDistance;
            } else {
                filename = _Grid2DSquareCellName
                        + "__SlopeAndAspect[slope,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            dir = ge.initFileDirectory(directory, filename, handleOutOfMemoryError);
            this.GridDoubleFactory.setDirectory(dir);
            slopeAndAspect[0] = (Grids_GridDouble) GridDoubleFactory.create(dir,
                    nrows,
                    ncols,
                    dimensions,
                    handleOutOfMemoryError);
            slopeAndAspect[0].setName(
                    filename,
                    handleOutOfMemoryError);
            swapToFileCache = true;
            try {
                slopeAndAspect[0].writeToFile(
                        swapToFileCache,
                        handleOutOfMemoryError);
            } catch (IOException ioe0) {
                System.err.println(ioe0.getMessage());
            }
            ge.getGrids().add(slopeAndAspect[0]);
            System.out.println(slopeAndAspect[0].toString(handleOutOfMemoryError));
            System.out.println("Initialising _SlopeAndAspect[ 1 ]");
            if (shortName) {
                filename = "aspect_N_" + averageDistance;
            } else {
                filename = _Grid2DSquareCellName
                        + "__SlopeAndAspect[aspect_N,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            dir = ge.initFileDirectory(directory, filename, handleOutOfMemoryError);
            GridDoubleFactory.setDirectory(dir);
            slopeAndAspect[1] = (Grids_GridDouble) GridDoubleFactory.create(dir,
                    nrows,
                    ncols,
                    dimensions,
                    handleOutOfMemoryError);
            slopeAndAspect[1].setName(
                    filename, //string0,
                    handleOutOfMemoryError);
            swapToFileCache = true;
            try {
                slopeAndAspect[1].writeToFile(
                        swapToFileCache,
                        handleOutOfMemoryError);
            } catch (IOException ioe0) {
                System.err.println(ioe0.getMessage());
            }
            ge.getGrids().add(slopeAndAspect[1]);
            System.out.println("Initialising _SlopeAndAspect[ 2 ]");
            if (shortName) {
                filename = "sin_aspect_N_" + averageDistance;
            } else {
                filename = _Grid2DSquareCellName
                        + "__SlopeAndAspect[sin_aspect_N,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            dir = ge.initFileDirectory(directory, filename, handleOutOfMemoryError);
            GridDoubleFactory.setDirectory(dir);
            slopeAndAspect[2] = (Grids_GridDouble) GridDoubleFactory.create(
                    dir,
                    nrows,
                    ncols,
                    dimensions,
                    handleOutOfMemoryError);
            slopeAndAspect[2].setName(
                    filename, //string0,
                    handleOutOfMemoryError);
            swapToFileCache = true;
            try {
                slopeAndAspect[2].writeToFile(
                        swapToFileCache,
                        handleOutOfMemoryError);
            } catch (IOException ioe0) {
                System.err.println(ioe0.getMessage());
                //ioe0.printStackTrace();
            }
            ge.getGrids().add(slopeAndAspect[2]);
            System.out.println(slopeAndAspect[2].toString(handleOutOfMemoryError));
            System.out.println("Initialising _SlopeAndAspect[ 3 ]");
            if (shortName) {
                filename = "sin_aspect_NNE_" + averageDistance;
            } else {
                filename = _Grid2DSquareCellName
                        + "__SlopeAndAspect[sin_aspect_NNE,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            dir = ge.initFileDirectory(directory, filename, handleOutOfMemoryError);
            GridDoubleFactory.setDirectory(dir);
            slopeAndAspect[3] = (Grids_GridDouble) GridDoubleFactory.create(dir,
                    nrows,
                    ncols,
                    dimensions,
                    handleOutOfMemoryError);
            slopeAndAspect[3].setName(
                    filename, //string0,
                    handleOutOfMemoryError);
            swapToFileCache = true;
            try {
                slopeAndAspect[3].writeToFile(
                        swapToFileCache,
                        handleOutOfMemoryError);
            } catch (IOException ioe0) {
                System.err.println(ioe0.getMessage());
                //ioe0.printStackTrace();
            }
            ge.getGrids().add(slopeAndAspect[3]);
            System.out.println(slopeAndAspect[3].toString(handleOutOfMemoryError));
            System.out.println("Initialising _SlopeAndAspect[ 4 ]");
            if (shortName) {
                filename = "sin_aspect_NE_" + averageDistance;
            } else {
                filename = _Grid2DSquareCellName
                        + "__SlopeAndAspect[sin_aspect_NE,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            dir = ge.initFileDirectory(directory, filename, handleOutOfMemoryError);
            GridDoubleFactory.setDirectory(dir);
            slopeAndAspect[4] = (Grids_GridDouble) GridDoubleFactory.create(dir,
                    nrows,
                    ncols,
                    dimensions,
                    handleOutOfMemoryError);
            slopeAndAspect[4].setName(
                    filename, //string0,
                    handleOutOfMemoryError);
            swapToFileCache = true;
            try {
                slopeAndAspect[4].writeToFile(
                        swapToFileCache,
                        handleOutOfMemoryError);
            } catch (IOException ioe0) {
                System.err.println(ioe0.getMessage());
//                ioe0.printStackTrace();
            }
            ge.getGrids().add(slopeAndAspect[4]);
            System.out.println(slopeAndAspect[4].toString(handleOutOfMemoryError));
            System.out.println("Initialising _SlopeAndAspect[ 5 ]");
            if (shortName) {
                filename = "sin_aspect_ENE_" + averageDistance;
            } else {
                filename = _Grid2DSquareCellName
                        + "__SlopeAndAspect[sin_aspect_ENE,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            dir = ge.initFileDirectory(directory, filename, handleOutOfMemoryError);
            GridDoubleFactory.setDirectory(dir);
            slopeAndAspect[5] = (Grids_GridDouble) GridDoubleFactory.create(dir,
                    nrows,
                    ncols,
                    dimensions,
                    handleOutOfMemoryError);
            slopeAndAspect[5].setName(
                    filename, //string0,
                    handleOutOfMemoryError);
            swapToFileCache = true;
            try {
                slopeAndAspect[5].writeToFile(
                        swapToFileCache,
                        handleOutOfMemoryError);
            } catch (IOException ioe0) {
                System.err.println(ioe0.getMessage());
            }
            ge.getGrids().add(slopeAndAspect[5]);
            System.out.println(slopeAndAspect[5].toString(handleOutOfMemoryError));
            System.out.println("Initialising _SlopeAndAspect[ 6 ]");
            if (shortName) {
                filename = "sin_aspect_E_" + averageDistance;
            } else {
                filename = _Grid2DSquareCellName
                        + "__SlopeAndAspect[sin_aspect_E,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            dir = ge.initFileDirectory(directory, filename, handleOutOfMemoryError);
            GridDoubleFactory.setDirectory(dir);
            slopeAndAspect[6] = (Grids_GridDouble) GridDoubleFactory.create(dir,
                    nrows,
                    ncols,
                    dimensions,
                    handleOutOfMemoryError);
            slopeAndAspect[6].setName(
                    filename, //string0,
                    handleOutOfMemoryError);
            swapToFileCache = true;
            try {
                slopeAndAspect[6].writeToFile(
                        swapToFileCache,
                        handleOutOfMemoryError);
            } catch (IOException ioe0) {
                System.err.println(ioe0.getMessage());
            }
            ge.getGrids().add(slopeAndAspect[6]);
            System.out.println(slopeAndAspect[6].toString(handleOutOfMemoryError));
            System.out.println("Initialising _SlopeAndAspect[ 7 ]");
            if (shortName) {
                filename = "sin_aspect_ESE_" + averageDistance;
            } else {
                filename = _Grid2DSquareCellName
                        + "__SlopeAndAspect[sin_aspect_ESE,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            dir = ge.initFileDirectory(directory, filename, handleOutOfMemoryError);
            GridDoubleFactory.setDirectory(dir);
            slopeAndAspect[7] = (Grids_GridDouble) GridDoubleFactory.create(dir,
                    nrows,
                    ncols,
                    dimensions,
                    handleOutOfMemoryError);
            slopeAndAspect[7].setName(
                    filename, //string0,
                    handleOutOfMemoryError);
            swapToFileCache = true;
            try {
                slopeAndAspect[7].writeToFile(
                        swapToFileCache,
                        handleOutOfMemoryError);
            } catch (IOException ioe0) {
                System.err.println(ioe0.getMessage());
            }
            ge.getGrids().add(slopeAndAspect[7]);
            System.out.println(slopeAndAspect[7].toString(handleOutOfMemoryError));
            System.out.println("Initialising _SlopeAndAspect[ 8 ]");
            if (shortName) {
                filename = "sin_aspect_SE_" + averageDistance;
            } else {
                filename = _Grid2DSquareCellName
                        + "__SlopeAndAspect[sin_aspect_SE,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            dir = ge.initFileDirectory(directory, filename, handleOutOfMemoryError);
            GridDoubleFactory.setDirectory(dir);
            slopeAndAspect[8] = (Grids_GridDouble) GridDoubleFactory.create(dir,
                    nrows,
                    ncols,
                    dimensions,
                    handleOutOfMemoryError);
            slopeAndAspect[8].setName(
                    filename, //string0,
                    handleOutOfMemoryError);
            swapToFileCache = true;
            try {
                slopeAndAspect[8].writeToFile(
                        swapToFileCache,
                        handleOutOfMemoryError);
            } catch (IOException ioe0) {
                System.err.println(ioe0.getMessage());
            }
            ge.getGrids().add(slopeAndAspect[8]);
            System.out.println(slopeAndAspect[8].toString(handleOutOfMemoryError));
            System.out.println("Initialising _SlopeAndAspect[ 9 ]");
            if (shortName) {
                filename = "sin_aspect_SSE_" + averageDistance;
            } else {
                filename = _Grid2DSquareCellName
                        + "__SlopeAndAspect[sin_aspect_SSE,"
                        + "averageDistance(" + averageDistance + "),"
                        + "weightIntersect(" + weightIntersect + "),"
                        + "weightFactor(" + weightFactor + ")]";
            }
            dir = ge.initFileDirectory(directory, filename, handleOutOfMemoryError);
            GridDoubleFactory.setDirectory(dir);
            slopeAndAspect[9] = (Grids_GridDouble) GridDoubleFactory.create(dir,
                    nrows,
                    ncols,
                    dimensions,
                    handleOutOfMemoryError);
            slopeAndAspect[9].setName(
                    filename, //string0,
                    handleOutOfMemoryError);
            swapToFileCache = true;
            try {
                slopeAndAspect[9].writeToFile(
                        swapToFileCache,
                        handleOutOfMemoryError);
            } catch (IOException ioe0) {
                System.err.println(ioe0.getMessage());
            }
            ge.getGrids().add(slopeAndAspect[9]);
            System.out.println("Initialised Results");
            System.out.println(g.toString(handleOutOfMemoryError));

            if (g.getClass() == Grids_GridDouble.class) {
                gridDouble = (Grids_GridDouble) g;
                double noDataValue = gridDouble.getNoDataValue(handleOutOfMemoryError);
                double h;
                double h2;
                for (cri = 0; cri < chunkRows; cri++) {
                    for (cci = 0; cci < chunkCols; cci++) {
                        chunkDouble = (Grids_AbstractGridChunkDouble) gridDouble.getGridChunk(
                                cri, cci, handleOutOfMemoryError);
                        chunkNrows = g.getChunkNRows(cri, handleOutOfMemoryError);
                        chunkNcols = g.getChunkNCols(cci, handleOutOfMemoryError);
                        for (chunkCellRowIndex = 0; chunkCellRowIndex < chunkNrows; chunkCellRowIndex++) {
                            cellRowIndex = g.getRow(cri, chunkCellRowIndex, handleOutOfMemoryError);
                            y = g.getCellYDouble(cellRowIndex, handleOutOfMemoryError);
                            for (chunkCellColIndex = 0; chunkCellColIndex < chunkNcols; chunkCellColIndex++) {
                                cellColIndex = g.getCol(cci, chunkCellColIndex, handleOutOfMemoryError);
                                x = g.getCellXDouble(cellColIndex, handleOutOfMemoryError);
                                h = chunkDouble.getCell(
                                        chunkCellRowIndex,
                                        chunkCellColIndex,
                                        handleOutOfMemoryError);
                                if (h != noDataValue) {
                                    diffX = 0.0d;
                                    diffY = 0.0d;
                                    slope = 0.0d;
                                    weightSum = 0.0d;
                                    distanceSum = 0.0d;
                                    numberObservations = 0.0d;
                                    for (p = -cellDistance; p <= cellDistance; p++) {
                                        long0 = cellRowIndex + p;
                                        thisY = g.getCellYDouble(long0, handleOutOfMemoryError);
                                        for (q = -cellDistance; q <= cellDistance; q++) {
                                            if (!(p == 0 && q == 0)) {
                                                long0 = cellColIndex + q;
                                                thisX = g.getCellXDouble(long0, handleOutOfMemoryError);
                                                thisDistance = distance(x, y, thisX, thisY, chunkCols, cri, cci, handleOutOfMemoryError);
                                                if (thisDistance <= distance) {
                                                    h2 = gridDouble.getCell(
                                                            thisX,
                                                            thisY,
                                                            handleOutOfMemoryError);
                                                    if (h2 != noDataValue) {
                                                        long0 = p + cellDistance;
                                                        int0 = (int) long0;
                                                        long0 = q + cellDistance;
                                                        int1 = (int) (long0);
                                                        weight = weights[int0][int1];
                                                        weightSum += weight;
                                                        distanceSum += thisDistance;
                                                        numberObservations++;
                                                        double0 = h - h2;
                                                        diffHeight = double0 * weight;
                                                        double0 = x - thisX;
                                                        diffX += double0 * diffHeight;
                                                        double0 = y - thisY;
                                                        diffY += double0 * diffHeight;
                                                        slope += diffHeight;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (numberObservations > 0) {
                                        averageDistance = (distanceSum / numberObservations);
                                        double0 = weightSum * averageDistance;
                                        slope /= double0;
                                        slope *= doubleOneHundred;
                                        slopeAndAspect[0].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                slope,
                                                handleOutOfMemoryError);
                                        double0 = x + diffX;
                                        double1 = y + diffY;
                                        angle = angle(x, y, double0, double1,
                                                chunkCols, cri,
                                                cci, handleOutOfMemoryError);
                                        slopeAndAspect[1].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                angle,
                                                handleOutOfMemoryError);
                                        sinAngle = ge.sin(angle, handleOutOfMemoryError);
                                        slopeAndAspect[2].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI / doubleEight);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[3].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI / doubleFour);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[4].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI * doubleThree / doubleEight);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[5].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI / doubleTwo);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[6].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI * doubleFive / doubleEight);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[7].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI * doubleSix / doubleEight);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[8].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI * doubleSeven / doubleEight);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[9].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                    }
                                }
                            }
                        }
                        System.out.println("Done Chunk ( " + cri + ", " + cci + " )");
                    }
                }
            } else {
                // ( g.getClass() == Grids_GridInt.class )
                gridInt = (Grids_GridInt) g;
                noDataValueInt = gridInt.getNoDataValue(handleOutOfMemoryError);
//                Grids_GridIntIterator ite;
//                ite = gridInt.iterator();
//                Grids_AbstractGridChunkNumberRowMajorOrderIterator chunkIte;
//                while (ite.hasNext()) {
//                    chunkIte = ite.getChunkIterator();
//                    chunkIte.
//                }
                for (cri = 0; cri < chunkRows; cri++) {
                    chunkNrows = g.getChunkNRows(cri, handleOutOfMemoryError);
                    for (cci = 0; cci < chunkCols; cci++) {
                        chunkNcols = g.getChunkNCols(cci, handleOutOfMemoryError);
                        chunk = (Grids_AbstractGridChunkInt) gridInt.getGridChunk(
                                cri, cci, handleOutOfMemoryError);
                        for (chunkCellRowIndex = 0; chunkCellRowIndex < chunkNrows; chunkCellRowIndex++) {
                            cellRowIndex = g.getRow(cri, chunkCellRowIndex, handleOutOfMemoryError);
                            y = g.getCellYDouble(cellRowIndex, handleOutOfMemoryError);
                            for (chunkCellColIndex = 0; chunkCellColIndex < chunkNcols; chunkCellColIndex++) {
                                cellColIndex = g.getCol(cci, chunkCellColIndex, handleOutOfMemoryError);
                                x = g.getCellXDouble(cellColIndex, handleOutOfMemoryError);
                                heightInt = chunk.getCell(
                                        chunkCellRowIndex,
                                        chunkCellColIndex,
                                        handleOutOfMemoryError,
                                        chunk.getChunkID(handleOutOfMemoryError));
                                if (heightInt != noDataValueInt) {
                                    diffX = 0.0d;
                                    diffY = 0.0d;
                                    slope = 0.0d;
                                    weightSum = 0.0d;
                                    distanceSum = 0.0d;
                                    numberObservations = 0.0d;
                                    for (p = -cellDistance; p <= cellDistance; p++) {
                                        long0 = cellRowIndex + p;
                                        thisY = g.getCellYDouble(long0, handleOutOfMemoryError);
                                        for (q = -cellDistance; q <= cellDistance; q++) {
                                            if (!(p == 0 && q == 0)) {
                                                long0 = cellColIndex + q;
                                                thisX = g.getCellXDouble(long0, handleOutOfMemoryError);
                                                thisDistance = distance(x, y, thisX,
                                                        thisY, chunkCols, cri,
                                                        cci, handleOutOfMemoryError);
                                                if (thisDistance <= distance) {
                                                    thisHeightInt = gridInt.getCell(
                                                            thisX, thisY, handleOutOfMemoryError);
                                                    if (thisHeightInt != noDataValueInt) {
                                                        long0 = p + cellDistance;
                                                        int0 = (int) long0;
                                                        long0 = q + cellDistance;
                                                        int1 = (int) (long0);
                                                        weight = weights[int0][int1];
                                                        weightSum += weight;
                                                        distanceSum += thisDistance;
                                                        numberObservations++;
                                                        double0 = (double) heightInt - thisHeightInt;
                                                        diffHeight = double0 * weight;
                                                        double0 = x - thisX;
                                                        diffX += double0 * diffHeight;
                                                        double0 = y - thisY;
                                                        diffY += double0 * diffHeight;
                                                        slope += diffHeight;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (numberObservations > 0) {
                                        averageDistance = (distanceSum / numberObservations);
                                        double0 = weightSum * averageDistance;
                                        slope /= double0;
                                        slope *= doubleOneHundred;
                                        slopeAndAspect[0].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                slope,
                                                handleOutOfMemoryError);
                                        double0 = x + diffX;
                                        double1 = y + diffY;
                                        angle = angle(x, y, double0, double1,
                                                chunkCols, cri, cci, handleOutOfMemoryError);
                                        slopeAndAspect[1].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                angle,
                                                handleOutOfMemoryError);
                                        sinAngle = ge.sin(angle, handleOutOfMemoryError);
                                        slopeAndAspect[2].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI / doubleEight);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[3].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI / doubleFour);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[4].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI * doubleThree / doubleEight);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[5].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI / doubleTwo);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[6].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI * doubleFive / doubleEight);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[7].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI * doubleSix / doubleEight);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[8].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                        double3 = angle + (PI * doubleSeven / doubleEight);
                                        sinAngle = ge.sin(double3, handleOutOfMemoryError);
                                        slopeAndAspect[9].setCell(
                                                cellRowIndex,
                                                cellColIndex,
                                                sinAngle,
                                                handleOutOfMemoryError);
                                    }
                                }
                            }
                        }
                        System.out.println("Done Chunk ( " + cri + ", " + cci + " )");
                    }
                }
            }
            return slopeAndAspect;
        } catch (OutOfMemoryError _OutOfMemoryError0) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                    throw _OutOfMemoryError0;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return getSlopeAspect(
                        g,
                        distance,
                        weightIntersect,
                        weightFactor,
                        handleOutOfMemoryError);
            }
            throw _OutOfMemoryError0;
        }
    }

    /**
     * Returns a double[] _SlopeAndAspect where: _SlopeAndAspect[0] is the
     * aggregate slope over the region weighted by distance, weightIntersect and
     * weightFactor; _SlopeAndAspect[1] is the aggregate aspect over the region
     * weighted by distance, weightIntersect and weightFactor. This is the
     * clockwize angle from north. _SlopeAndAspect[2] is the aggregate aspect
     * over the region weighted by distance, weightIntersect and weightFactor.
     * This is the sine of the clockwize angle from north. _SlopeAndAspect[3] is
     * the aggregate aspect over the region weighted by distance,
     * weightIntersect and weightFactor. This is the cosine of the clockwize
     * angle from north.
     *
     * @param _Grid2DSquareCell the Grids_GridDouble to be processed.
     * @param rowIndex the rowIndex where _SlopeAndAspect is calculated.
     * @param colIndex the colIndex where _SlopeAndAspec
     * @param handleOutOfMemoryError
     * @return t is calculated.
     * @param distance the distance which defines the aggregate region.
     * @param weightIntersect the kernel weighting weight at centre.
     * @param weightFactor the kernel weighting distance decay.
     */
    protected double[] getSlopeAspect(
            Grids_AbstractGridNumber _Grid2DSquareCell,
            long rowIndex,
            long colIndex,
            double distance,
            double weightIntersect,
            double weightFactor,
            boolean handleOutOfMemoryError) {
        try {
            ge.getGrids().add(_Grid2DSquareCell);
            return getSlopeAspect(
                    _Grid2DSquareCell,
                    rowIndex,
                    colIndex,
                    _Grid2DSquareCell.getCellXDouble(colIndex, handleOutOfMemoryError),
                    _Grid2DSquareCell.getCellYDouble(rowIndex, handleOutOfMemoryError),
                    distance,
                    weightIntersect,
                    weightFactor,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                    throw a_OutOfMemoryError;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return getSlopeAspect(
                        _Grid2DSquareCell,
                        rowIndex,
                        colIndex,
                        distance,
                        weightIntersect,
                        weightFactor,
                        handleOutOfMemoryError);
            }
            throw a_OutOfMemoryError;
        }
    }

    /**
     * Returns a double[] _SlopeAndAspect where: _SlopeAndAspect[0] is the
     * aggregate slope over the region weighted by distance, weightIntersect and
     * weightFactor; _SlopeAndAspect[1] is the aggregate aspect over the region
     * weighted by distance, weightIntersect and weightFactor. This is the
     * clockwize angle from north. _SlopeAndAspect[2] is the aggregate aspect
     * over the region weighted by distance, weightIntersect and weightFactor.
     * This is the sine of the clockwize angle from north. _SlopeAndAspect[3] is
     * the aggregate aspect over the region weighted by distance,
     * weightIntersect and weightFactor. This is the cosine of the clockwize
     * angle from north.
     *
     * @param _Grid2DSquareCell the Grids_GridDouble to be processed.
     * @param x the x coordinate from where the aspect is calculated
     * @param y the y coordinate from where the aspect is calculated
     * @param distance the distance which defines the aggregate region.
     * @param weightIntersect the kernel weighting weight at centre.
     * @param weightFactor the kernel weighting distance decay.
     * @param handleOutOfMemoryError
     * @return
     */
    protected double[] getSlopeAspect(
            Grids_AbstractGridNumber _Grid2DSquareCell,
            double x,
            double y,
            double distance,
            double weightIntersect,
            double weightFactor,
            boolean handleOutOfMemoryError) {
        try {
            ge.getGrids().add(_Grid2DSquareCell);
            return getSlopeAspect(
                    _Grid2DSquareCell,
                    _Grid2DSquareCell.getRow(y, handleOutOfMemoryError),
                    _Grid2DSquareCell.getCol(x, handleOutOfMemoryError),
                    x,
                    y,
                    distance,
                    weightIntersect,
                    weightFactor,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError _OutOfMemoryError0) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                    throw _OutOfMemoryError0;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return getSlopeAspect(
                        _Grid2DSquareCell,
                        x,
                        y,
                        distance,
                        weightIntersect,
                        weightFactor,
                        handleOutOfMemoryError);
            }
            throw _OutOfMemoryError0;
        }
    }

    /**
     * Returns a double[] _SlopeAndAspect where: _SlopeAndAspect[0] is the
     * aggregate slope over the region weighted by distance, weightIntersect and
     * weightFactor; _SlopeAndAspect[1] is the aggregate aspect over the region
     * weighted by distance, weightIntersect and weightFactor. This is the
     * clockwize angle from north. _SlopeAndAspect[2] is the aggregate aspect
     * over the region weighted by distance, weightIntersect and weightFactor.
     * This is the sine of the clockwize angle from north. _SlopeAndAspect[3] is
     * the aggregate aspect over the region weighted by distance,
     * weightIntersect and weightFactor. This is the cosine of the clockwize
     * angle from north.
     *
     * @param g The Grids_GridDouble to be processed
     * @param rowIndex the rowIndex where the result is calculated
     * @param colIndex the colIndex where the result is calculated
     * @param x the x coordinate from where the aspect is calculated
     * @param y the y coordinate from where the aspect is calculated
     * @param distance the distance which defines the region
     * @param weightIntersect
     * @param weightFactor NB. If grid.getCell( x, y ) == grid.getNoDataValue()
     * then; result[ 0 ] = grid.getNoDataValue() result[ 1 ] =
     * grid.getNoDataValue() TODO: x and y can be offset from a cell centroid so
     * consider interpolation
     * @param handleOutOfMemoryError
     * @return
     */
    protected double[] getSlopeAspect(
            Grids_AbstractGridNumber g,
            long rowIndex,
            long colIndex,
            double x,
            double y,
            double distance,
            double weightIntersect,
            double weightFactor,
            boolean handleOutOfMemoryError) {
        try {
            ge.getGrids().add(g);
            if (g.getClass() == Grids_GridInt.class) {
                Grids_GridInt _Grid2DSquareCellInt = (Grids_GridInt) g;
                int noDataValue = _Grid2DSquareCellInt.getNoDataValue(true);
                double[] _SlopeAndAspect = new double[2];
                _SlopeAndAspect[0] = noDataValue;
                _SlopeAndAspect[1] = noDataValue;
                _SlopeAndAspect[2] = noDataValue;
                _SlopeAndAspect[3] = noDataValue;
                int height = _Grid2DSquareCellInt.getCell(x, y, handleOutOfMemoryError);
                if (height != noDataValue) {
                    double cellsize = g.getCellsizeDouble(handleOutOfMemoryError);
                    int cellDistance = (int) Math.ceil((distance + cellsize) / cellsize);
                    double thisDistance;
                    double weight;
                    double thisX;
                    double thisY;
                    int thisHeight;
                    double diffX = 0.0d;
                    double diffY = 0.0d;
                    double diffHeight;
                    double slope = 0.0d;
                    double aspect;
                    // Calculate slope and aspect
                    for (int p = -cellDistance; p <= cellDistance; p++) {
                        thisY = y + (p * distance);
                        for (int q = -cellDistance; q <= cellDistance; q++) {
                            thisX = x + (q * distance);
                            thisDistance = distance(x, y, thisX, thisY, handleOutOfMemoryError);
                            if (thisDistance <= distance) {
                                weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance);
                                thisHeight = _Grid2DSquareCellInt.getCell(thisX, thisY, handleOutOfMemoryError);
                                //thisHeight = _Grid2DSquareCellInt.getNearestValueDouble( thisX, thisY, handleOutOfMemoryError );
                                if (thisHeight != noDataValue) {
                                    diffHeight = (double) (height - thisHeight) * weight;
                                    diffX += (x - thisX) * diffHeight;
                                    diffY += (y - thisY) * diffHeight;
                                    slope += diffHeight;
                                }
                            }
                        }
                    }
                    _SlopeAndAspect[0] = slope;
                    _SlopeAndAspect[1] = angle(x, y, (x + diffX), (y + diffY), handleOutOfMemoryError);
                    _SlopeAndAspect[2] = Math.sin(angle(x, y, (x + diffX), (y + diffY), handleOutOfMemoryError));
                    _SlopeAndAspect[3] = Math.cos(angle(x, y, (x + diffX), (y + diffY), handleOutOfMemoryError));
                }
                return _SlopeAndAspect;
            } else {
                // ( _Grid2DSquareCell.getClass() == Grids_GridDouble.class )
                Grids_GridDouble _Grid2DSquareCellDouble = (Grids_GridDouble) g;
                double noDataValue = _Grid2DSquareCellDouble.getNoDataValue(handleOutOfMemoryError);
                double value;
                double[] _SlopeAndAspect = new double[2];
                _SlopeAndAspect[0] = noDataValue;
                _SlopeAndAspect[1] = noDataValue;
                _SlopeAndAspect[2] = noDataValue;
                _SlopeAndAspect[3] = noDataValue;
                double height = _Grid2DSquareCellDouble.getCell(x, y, handleOutOfMemoryError);
                if (height != noDataValue) {
                    double cellsize = g.getCellsizeDouble(handleOutOfMemoryError);
                    int cellDistance = (int) Math.ceil((distance + cellsize) / cellsize);
                    double thisDistance;
                    double weight;
                    double thisX;
                    double thisY;
                    double thisHeight;
                    double diffX = 0.0d;
                    double diffY = 0.0d;
                    double diffHeight;
                    double slope = 0.0d;
                    double aspect;
                    // Calculate slope and aspect
                    for (int p = -cellDistance; p <= cellDistance; p++) {
                        thisY = y + (p * distance);
                        for (int q = -cellDistance; q <= cellDistance; q++) {
                            thisX = x + (q * distance);
                            thisDistance = distance(x, y, thisX, thisY, handleOutOfMemoryError);
                            if (thisDistance <= distance) {
                                weight = Grids_Kernel.getKernelWeight(distance, weightIntersect, weightFactor, thisDistance);
                                thisHeight = _Grid2DSquareCellDouble.getCell(thisX, thisY, handleOutOfMemoryError);
                                //thisHeight = _Grid2DSquareCellDouble.getNearestValueDouble( thisX, thisY, handleOutOfMemoryError );
                                if (thisHeight != noDataValue) {
                                    diffHeight = (height - thisHeight) * weight;
                                    diffX += (x - thisX) * diffHeight;
                                    diffY += (y - thisY) * diffHeight;
                                    slope += diffHeight;
                                }
                            }
                        }
                    }
                    _SlopeAndAspect[0] = slope;
                    _SlopeAndAspect[1] = angle(x, y, (x + diffX), (y + diffY), handleOutOfMemoryError);
                    _SlopeAndAspect[2] = Math.sin(angle(x, y, (x + diffX), (y + diffY), handleOutOfMemoryError));
                    _SlopeAndAspect[3] = Math.cos(angle(x, y, (x + diffX), (y + diffY), handleOutOfMemoryError));
                }
                return _SlopeAndAspect;
            }
        } catch (OutOfMemoryError _OutOfMemoryError0) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                    throw _OutOfMemoryError0;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                getSlopeAspect(g, rowIndex, colIndex, x, y, distance, weightIntersect, weightFactor, handleOutOfMemoryError);
            }
            throw _OutOfMemoryError0;
        }
    }

    /**
     *
     *
     *
     * @param _Grid2DSquareCell Grids_AbstractGridNumber to be processed.
     * @param gdf
     * @param outflowHeight
     * @param maxIterations
     * @param handleOutOfMemoryError
     * @param _TreatNoDataValueAsOutflow
     * @param outflowCellIDsSet
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
     */
    public Grids_GridDouble getHollowFilledDEM(
            Grids_AbstractGridNumber _Grid2DSquareCell,
            Grids_GridDoubleFactory gdf,
            double outflowHeight,
            int maxIterations,
            HashSet outflowCellIDsSet,
            boolean _TreatNoDataValueAsOutflow,
            boolean handleOutOfMemoryError) {
        try {
            ge.getGrids().add(_Grid2DSquareCell);
            // Intitialise variables
            Grids_GridDouble result;
            long _NRows;
            long _NCols;
//            int chunkNrows = _Grid2DSquareCell.getChunkNRows(
//                    handleOutOfMemoryError );
//            int chunkNcols = _Grid2DSquareCell.getChunkNCols(
//                    handleOutOfMemoryError );
            //String resultName = _Grid2DSquareCell.getName( handleOutOfMemoryError ) + "_HollowFilledDEM_" + maxIterations;
            String resultName = "_HollowFilledDEM_" + maxIterations;
            result = (Grids_GridDouble) gdf.create(_Grid2DSquareCell);
            result.setName(resultName, handleOutOfMemoryError);
            _NRows = result.getNRows(handleOutOfMemoryError);
            _NCols = result.getNCols(handleOutOfMemoryError);
            double minHeight = result.getStatistics(handleOutOfMemoryError).getMin(
                    true, handleOutOfMemoryError).doubleValue();
            if (outflowHeight < minHeight) {
                outflowHeight = minHeight;
            }
            if (_Grid2DSquareCell.getClass() == Grids_GridInt.class) {
                Grids_GridInt _Grid2DSquareCellInt = (Grids_GridInt) _Grid2DSquareCell;
                int noDataValue = _Grid2DSquareCellInt.getNoDataValue(true);
                double height;
                // Initialise outflowCellIDs
                HashSet outflowCellIDs = getHollowFilledDEMOutflowCellIDs(
                        outflowCellIDsSet,
                        outflowHeight,
                        _Grid2DSquareCellInt,
                        _NRows,
                        _NCols,
                        _TreatNoDataValueAsOutflow,
                        handleOutOfMemoryError);
                // Initialise hollowsHashSet
                HashSet hollowsHashSet = getHollowFilledDEMInitialHollowsHashSet(
                        _Grid2DSquareCellInt,
                        _NRows,
                        _NCols,
                        _TreatNoDataValueAsOutflow,
                        handleOutOfMemoryError);
                // Remove outflowCellIDs from hollowsHashSet
                hollowsHashSet.removeAll(outflowCellIDs);
                HashSet hollows2 = hollowsHashSet;
                int numberOfHollows = hollowsHashSet.size();
                boolean calculated1 = false;
                boolean calculated2;
                if (numberOfHollows == 0) {
                    calculated1 = true;
                }
                int iteration1 = 0;
                int iteration2;
                int counter1 = 0;
                long row;
                long col;
                long p;
                long q;
                long r;
                long s;
                //int cellID1;
                //int cellID2;
                //int cellID3;
                Iterator iterator1;
                Iterator iterator2;
                //Integer cellID1;
                //Integer cellID2;
                //Integer cellID3;
                Grids_2D_ID_long[] cellIDs = new Grids_2D_ID_long[3];
                HashSet toVisitSet1;
                HashSet toVisitSet2;
                HashSet toVisitSet3;
                HashSet visitedSet1;
                HashSet visitedSet2;
                HashSet hollows1;
                HashSet hollowsVisited;
                HashSet hollowSet;
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
                            visitedSet1 = new HashSet();
                            hollowsVisited = new HashSet();
                            //hollowsVisited.addAll( outflowCellIDs );
                            // Raise all hollows by a small amount
                            setValueALittleBitLarger(
                                    result,
                                    hollows2,
                                    handleOutOfMemoryError);
                            // Recalculate hollows in hollows2 neighbourhood
                            toVisitSet1 = new HashSet();
                            iterator1 = hollows2.iterator();
                            while (iterator1.hasNext()) {
                                cellIDs[0] = (Grids_2D_ID_long) iterator1.next();
                                row = cellIDs[0].getRow();
                                col = cellIDs[0].getCol();
                                for (p = -1; p < 2; p++) {
                                    for (q = -1; q < 2; q++) {
                                        //if ( ! ( p == 0 && q == 0 ) ) {
                                        if (_Grid2DSquareCell.isInGrid(row + p, col + q, handleOutOfMemoryError)) {
                                            toVisitSet1.add(_Grid2DSquareCell.getCellID(row + p, col + q, handleOutOfMemoryError));
                                        }
                                        //}
                                    }
                                }
                            }
                            hollows1 = getHollowsInNeighbourhood(
                                    result,
                                    toVisitSet1,
                                    _TreatNoDataValueAsOutflow,
                                    handleOutOfMemoryError);
                            hollows1.removeAll(outflowCellIDs);
                            hollows2.clear();
                            toVisitSet1.clear();
                            /*
                             hollows1 = getHollowFilledDEMCalculateHollowsInNeighbourhood( result, hollows2 );
                             hollows1.removeAll( outflowCellIDs );
                             hollows2.clear();
                             */
                            // Trace bottom of each hollow and raise to the height of the lowest cell around it.
                            iterator1 = hollows1.iterator();
                            while (iterator1.hasNext()) {
                                cellIDs[0] = (Grids_2D_ID_long) iterator1.next();
                                if (!hollowsVisited.contains(cellIDs[0])) {
                                    hollowSet = new HashSet();
                                    hollowSet.add(cellIDs[0]);
                                    row = cellIDs[0].getRow();
                                    col = cellIDs[0].getCol();
                                    toVisitSet1 = new HashSet();
                                    // Step 1: Add all cells in adjoining hollows to hollowSet
                                    for (p = -1; p < 2; p++) {
                                        for (q = -1; q < 2; q++) {
                                            if (!(p == 0 && q == 0)) {
                                                if (_Grid2DSquareCell.isInGrid(row + p, col + q, handleOutOfMemoryError)) {
                                                    cellIDs[1] = _Grid2DSquareCell.getCellID(row + p, col + q, handleOutOfMemoryError);
                                                    toVisitSet1.add(cellIDs[1]);
                                                }
                                            }
                                        }
                                    }
                                    toVisitSet1.removeAll(outflowCellIDs);
                                    visitedSet2 = new HashSet();
                                    visitedSet2.add(cellIDs[0]);
                                    toVisitSet3 = new HashSet();
                                    toVisitSet3.addAll(toVisitSet1);
                                    calculated2 = false;
                                    while (!calculated2) {
                                        toVisitSet2 = new HashSet();
                                        iterator2 = toVisitSet1.iterator();
                                        while (iterator2.hasNext()) {
                                            cellIDs[1] = (Grids_2D_ID_long) iterator2.next();
                                            visitedSet2.add(cellIDs[1]);
                                            row = cellIDs[1].getRow();
                                            col = cellIDs[1].getCol();
                                            for (p = -1; p < 2; p++) {
                                                for (q = -1; q < 2; q++) {
                                                    if (!(p == 0 && q == 0)) {
                                                        if (_Grid2DSquareCell.isInGrid(row + p, col + q, handleOutOfMemoryError)) {
                                                            cellIDs[2] = _Grid2DSquareCell.getCellID(row + p, col + q, handleOutOfMemoryError);
                                                            visitedSet1.add(cellIDs[2]);
                                                            // If a hollow then add to hollow set and visit neighbours if not done already
                                                            if (hollows1.contains(cellIDs[2])) {
                                                                hollowSet.add(cellIDs[2]);
                                                                for (r = -1; r < 2; r++) {
                                                                    for (s = -1; s < 2; s++) {
                                                                        if (!(r == 0 && s == 0)) { // Is this correct?
                                                                            if (_Grid2DSquareCell.isInGrid(row + p + r, col + q + s, handleOutOfMemoryError)) {
                                                                                toVisitSet2.add(_Grid2DSquareCell.getCellID(row + p + r, col + q + s, handleOutOfMemoryError));
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
                                    height0 = result.getCell(row, col, handleOutOfMemoryError);
                                    while (!calculated2) {
                                        toVisitSet2 = new HashSet();
                                        //toVisitSet2.addAll( toVisitSet3 );
                                        iterator2 = toVisitSet3.iterator();
                                        noDataCount = 0;
                                        outflowCellCount = 0;
                                        // Step 2.1 Calculate height of the lowest neighbour minHeight // (that is not an outflow cell???)
                                        while (iterator2.hasNext()) {
                                            cellIDs[1] = (Grids_2D_ID_long) iterator2.next();
                                            row = cellIDs[1].getRow();
                                            col = cellIDs[1].getCol();
                                            height = result.getCell(row, col, handleOutOfMemoryError);
                                            if (height == noDataValue) {
                                                noDataCount++;
                                            } else {
                                                if (outflowCellIDs.contains(cellIDs[1])) {
                                                    outflowCellCount++;
                                                } else {
                                                    minHeight = Math.min(minHeight, height);
                                                }
                                                // Is this correct?
                                                //minHeight = Math.min( minHeight, height );
                                            }
                                        }
                                        if (noDataCount + outflowCellCount == toVisitSet3.size()) {
                                            // ge.println("Hollow surrounded by noDataValue or outflow cells!!!");
                                            // Add _CellIDs of this hollow to outflowCellIDs so that it is not revisited.
                                            outflowCellIDs.addAll(hollowSet);
                                            calculated2 = true;
                                        } else {
                                            // Step 2.2 Treat cells:
                                            // If minHeight is higher then add cells with this height to the
                                            // hollow set and their neighbours to toVisitSet2
                                            if (minHeight > height0) {
                                                iterator2 = toVisitSet3.iterator();
                                                while (iterator2.hasNext()) {
                                                    cellIDs[1] = (Grids_2D_ID_long) iterator2.next();
                                                    row = cellIDs[1].getRow();
                                                    col = cellIDs[1].getCol();
                                                    height = result.getCell(row, col, handleOutOfMemoryError);
                                                    if (height == minHeight) {
                                                        hollowSet.add(cellIDs[1]);
                                                        toVisitSet2.remove(cellIDs[1]);
                                                        for (r = -1; r < 2; r++) {
                                                            for (s = -1; s < 2; s++) {
                                                                if (!(r == 0L && s == 0L)) {
                                                                    if (_Grid2DSquareCell.isInGrid(row + r, col + s, handleOutOfMemoryError)) {
                                                                        toVisitSet2.add(_Grid2DSquareCell.getCellID(row + r, col + s, handleOutOfMemoryError));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                height0 = minHeight;
                                                toVisitSet2.removeAll(hollowSet);
                                                //toVisitSet2.removeAll( outflowCellIDs );
                                                toVisitSet3 = toVisitSet2;
                                            } else {
                                                calculated2 = true;
                                            }
                                        }
                                    }
                                    // Step 3 Raise all cells in hollowSet
                                    hollowSet.removeAll(outflowCellIDs);
                                    iterator2 = hollowSet.iterator();
                                    while (iterator2.hasNext()) {
                                        cellIDs[1] = (Grids_2D_ID_long) iterator2.next();
                                        row = cellIDs[1].getRow();
                                        col = cellIDs[1].getCol();
                                        result.setCell(row, col, Grids_Utilities.getValueALittleBitLarger(height0), handleOutOfMemoryError);
                                    }
                                    hollowsVisited.addAll(hollowSet);
                                    visitedSet1.addAll(hollowSet);
                                }
                            }
                            hollows2 = getHollowsInNeighbourhood(
                                    result,
                                    visitedSet1,
                                    _TreatNoDataValueAsOutflow,
                                    handleOutOfMemoryError);
                        } else {
                            calculated1 = true;
                        }
                    } else {
                        calculated1 = true;
                    }
                }
            } else {
                // ( _Grid2DSquareCell.getClass() == Grids_GridDouble.class )
                Grids_GridDouble _Grid2DSquareCellDouble = (Grids_GridDouble) _Grid2DSquareCell;
                double noDataValue = _Grid2DSquareCellDouble.getNoDataValue(true);
                double height;
                double heightDouble;
                double resultNoDataValue = result.getNoDataValue(handleOutOfMemoryError);
                // Initialise outflowCellIDs
                HashSet outflowCellIDs = getHollowFilledDEMOutflowCellIDs(
                        outflowCellIDsSet,
                        outflowHeight,
                        _Grid2DSquareCellDouble,
                        _NRows,
                        _NCols,
                        _TreatNoDataValueAsOutflow,
                        handleOutOfMemoryError);
                // Initialise hollowsHashSet
                HashSet hollowsHashSet = getHollowFilledDEMInitialHollowsHashSet(
                        _Grid2DSquareCellDouble,
                        _NRows,
                        _NCols,
                        _TreatNoDataValueAsOutflow,
                        handleOutOfMemoryError);
                // Remove outflowCellIDs from hollowsHashSet
                hollowsHashSet.removeAll(outflowCellIDs);
                HashSet hollows2 = hollowsHashSet;
                int numberOfHollows = hollowsHashSet.size();
                boolean calculated1 = false;
                boolean calculated2;
                if (numberOfHollows == 0) {
                    calculated1 = true;
                }
                int iteration1 = 0;
                int iteration2;
                int counter1 = 0;
                long row;
                long col;
                long p;
                long q;
                long r;
                long s;
                //int cellID1;
                //int cellID2;
                //int cellID3;
                Iterator iterator1;
                Iterator iterator2;
                //Integer cellID1;
                //Integer cellID2;
                //Integer cellID3;
                Grids_2D_ID_long[] cellIDs = new Grids_2D_ID_long[3];
                HashSet toVisitSet1;
                HashSet toVisitSet2;
                HashSet toVisitSet3;
                HashSet visitedSet1;
                HashSet visitedSet2;
                HashSet hollows1;
                HashSet hollowsVisited;
                HashSet hollowSet;
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
                            visitedSet1 = new HashSet();
                            hollowsVisited = new HashSet();
                            //hollowsVisited.addAll( outflowCellIDs );
                            // Raise all hollows by a small amount
                            setValueALittleBitLarger(
                                    result,
                                    hollows2,
                                    handleOutOfMemoryError);
                            // Recalculate hollows in hollows2 neighbourhood
                            toVisitSet1 = new HashSet();
                            iterator1 = hollows2.iterator();
                            while (iterator1.hasNext()) {
                                cellIDs[0] = (Grids_2D_ID_long) iterator1.next();
                                row = cellIDs[0].getRow();
                                col = cellIDs[0].getCol();
                                for (p = -1; p < 2; p++) {
                                    for (q = -1; q < 2; q++) {
                                        //if ( ! ( p == 0 && q == 0 ) ) {
                                        if (_Grid2DSquareCell.isInGrid(row + p, col + q, handleOutOfMemoryError)) {
                                            toVisitSet1.add(_Grid2DSquareCell.getCellID(row + p, col + q, handleOutOfMemoryError));
                                        }
                                        //}
                                    }
                                }
                            }
                            hollows1 = getHollowsInNeighbourhood(
                                    result,
                                    toVisitSet1,
                                    _TreatNoDataValueAsOutflow,
                                    handleOutOfMemoryError);
                            hollows1.removeAll(outflowCellIDs);
                            hollows2.clear();
                            toVisitSet1.clear();
                            /*
                             hollows1 = getHollowFilledDEMCalculateHollowsInNeighbourhood( result, hollows2 );
                             hollows1.removeAll( outflowCellIDs );
                             hollows2.clear();
                             */
                            // Trace bottom of each hollow and raise to the height of the lowest cell around it.
                            iterator1 = hollows1.iterator();
                            while (iterator1.hasNext()) {
                                cellIDs[0] = (Grids_2D_ID_long) iterator1.next();
                                if (!hollowsVisited.contains(cellIDs[0])) {
                                    hollowSet = new HashSet();
                                    hollowSet.add(cellIDs[0]);
                                    row = cellIDs[0].getRow();
                                    col = cellIDs[0].getCol();
                                    toVisitSet1 = new HashSet();
                                    // Step 1: Add all cells in adjoining hollows to hollowSet
                                    for (p = -1; p < 2; p++) {
                                        for (q = -1; q < 2; q++) {
                                            if (!(p == 0 && q == 0)) {
                                                if (_Grid2DSquareCell.isInGrid(row + p, col + q, handleOutOfMemoryError)) {
                                                    cellIDs[1] = _Grid2DSquareCell.getCellID(row + p, col + q, handleOutOfMemoryError);
                                                    toVisitSet1.add(cellIDs[1]);
                                                }
                                            }
                                        }
                                    }
                                    toVisitSet1.removeAll(outflowCellIDs);
                                    visitedSet2 = new HashSet();
                                    visitedSet2.add(cellIDs[0]);
                                    toVisitSet3 = new HashSet();
                                    toVisitSet3.addAll(toVisitSet1);
                                    calculated2 = false;
                                    while (!calculated2) {
                                        toVisitSet2 = new HashSet();
                                        iterator2 = toVisitSet1.iterator();
                                        while (iterator2.hasNext()) {
                                            cellIDs[1] = (Grids_2D_ID_long) iterator2.next();
                                            visitedSet2.add(cellIDs[1]);
                                            row = cellIDs[1].getRow();
                                            col = cellIDs[1].getCol();
                                            for (p = -1; p < 2; p++) {
                                                for (q = -1; q < 2; q++) {
                                                    if (!(p == 0 && q == 0)) {
                                                        if (_Grid2DSquareCell.isInGrid(row + p, col + q, handleOutOfMemoryError)) {
                                                            cellIDs[2] = _Grid2DSquareCell.getCellID(row + p, col + q, handleOutOfMemoryError);
                                                            visitedSet1.add(cellIDs[2]);
                                                            // If a hollow then add to hollow set and visit neighbours if not done already
                                                            if (hollows1.contains(cellIDs[2])) {
                                                                hollowSet.add(cellIDs[2]);
                                                                for (r = -1; r < 2; r++) {
                                                                    for (s = -1; s < 2; s++) {
                                                                        if (!(r == 0 && s == 0)) { // Is this correct?
                                                                            if (_Grid2DSquareCell.isInGrid(row + p + r, col + q + s, handleOutOfMemoryError)) {
                                                                                toVisitSet2.add(_Grid2DSquareCell.getCellID(row + p + r, col + q + s, handleOutOfMemoryError));
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
                                    height0 = result.getCell(row, col, handleOutOfMemoryError);
                                    while (!calculated2) {
                                        toVisitSet2 = new HashSet();
                                        //toVisitSet2.addAll( toVisitSet3 );
                                        iterator2 = toVisitSet3.iterator();
                                        noDataCount = 0;
                                        outflowCellCount = 0;
                                        // Step 2.1 Calculate height of the lowest neighbour minHeight // (that is not an outflow cell???)
                                        while (iterator2.hasNext()) {
                                            cellIDs[1] = (Grids_2D_ID_long) iterator2.next();
                                            row = cellIDs[1].getRow();
                                            col = cellIDs[1].getCol();
                                            heightDouble = result.getCell(row, col, handleOutOfMemoryError);
                                            if (heightDouble == resultNoDataValue) {
                                                noDataCount++;
                                            } else {
                                                if (outflowCellIDs.contains(cellIDs[1])) {
                                                    outflowCellCount++;
                                                } else {
                                                    minHeight = Math.min(minHeight, heightDouble);
                                                }
                                                // Is this correct?
                                                //minHeight = Math.min( minHeight, heightDouble );
                                            }
                                        }
                                        if (noDataCount + outflowCellCount == toVisitSet3.size()) {
                                            // ge.println("Hollow surrounded by noDataValue or outflow cells!!!");
                                            // Add _CellIDs of this hollow to outflowCellIDs so that it is not revisited.
                                            outflowCellIDs.addAll(hollowSet);
                                            calculated2 = true;
                                        } else {
                                            // Step 2.2 Treat cells:
                                            // If minHeight is higher then add cells with this height to the
                                            // hollow set and their neighbours to toVisitSet2
                                            if (minHeight > height0) {
                                                iterator2 = toVisitSet3.iterator();
                                                while (iterator2.hasNext()) {
                                                    cellIDs[1] = (Grids_2D_ID_long) iterator2.next();
                                                    row = cellIDs[1].getRow();
                                                    col = cellIDs[1].getCol();
                                                    heightDouble = result.getCell(row, col, handleOutOfMemoryError);
                                                    if (heightDouble == minHeight) {
                                                        hollowSet.add(cellIDs[1]);
                                                        toVisitSet2.remove(cellIDs[1]);
                                                        for (r = -1; r < 2; r++) {
                                                            for (s = -1; s < 2; s++) {
                                                                if (!(r == 0L && s == 0L)) {
                                                                    if (_Grid2DSquareCell.isInGrid(row + r, col + s, handleOutOfMemoryError)) {
                                                                        toVisitSet2.add(_Grid2DSquareCell.getCellID(row + r, col + s, handleOutOfMemoryError));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                height0 = minHeight;
                                                toVisitSet2.removeAll(hollowSet);
                                                //toVisitSet2.removeAll( outflowCellIDs );
                                                toVisitSet3 = toVisitSet2;
                                            } else {
                                                calculated2 = true;
                                            }
                                        }
                                    }
                                    // Step 3 Raise all cells in hollowSet
                                    hollowSet.removeAll(outflowCellIDs);
                                    iterator2 = hollowSet.iterator();
                                    while (iterator2.hasNext()) {
                                        cellIDs[1] = (Grids_2D_ID_long) iterator2.next();
                                        row = cellIDs[1].getRow();
                                        col = cellIDs[1].getCol();
                                        result.setCell(row, col, Grids_Utilities.getValueALittleBitLarger(height0), handleOutOfMemoryError);
                                    }
                                    hollowsVisited.addAll(hollowSet);
                                    visitedSet1.addAll(hollowSet);
                                }
                            }
                            hollows2 = getHollowsInNeighbourhood(
                                    result,
                                    visitedSet1,
                                    _TreatNoDataValueAsOutflow,
                                    handleOutOfMemoryError);
                        } else {
                            calculated1 = true;
                        }
                    } else {
                        calculated1 = true;
                    }
                }
            }
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError0) {
            ge.clearMemoryReserve();
            if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                throw _OutOfMemoryError0;
            }
            ge.initMemoryReserve(handleOutOfMemoryError);
            return getHollowFilledDEM(
                    _Grid2DSquareCell,
                    gdf,
                    outflowHeight,
                    maxIterations,
                    outflowCellIDsSet,
                    _TreatNoDataValueAsOutflow,
                    handleOutOfMemoryError);
        }
    }

    /**
     * @param outflowCellIDsSet
     * @param outflowHeight The value below which cells in _Grid2DSquareCell are
     * regarded as outflow cells.
     * @param _Grid2DSquareCell Grids_AbstractGridNumber to process.
     * @param nrows Number of rows in _Grid2DSquareCell.
     * @param ncols Number of columns in _Grid2DSquareCell.
     * @param handleOutOfMemoryError If true then encountered OutOfMemeroyErrors
     * are handled. If false then an encountered OutOfMemeroyError is thrown.
     * @return HashSet containing Grids_AbstractGridNumber.CellIDs of those
     * cells in _Grid2DSquareCell that are to be regarded as outflow cells.
     * Outflow cells are those: with a value <= outflowHeight; those with CellID
     * in outflowCellIDsSet; and if _TreatNoDataValueAsOutflow is true then any
     * cell with a value of NoDataValue.
     */
    private HashSet getHollowFilledDEMOutflowCellIDs(
            HashSet outflowCellIDsSet,
            double outflowHeight,
            Grids_AbstractGridNumber _Grid2DSquareCell,
            long nrows,
            long ncols,
            boolean _TreatNoDataValueAsOutflow,
            boolean handleOutOfMemoryError) {
        try {
            ge.getGrids().add(_Grid2DSquareCell);
            HashSet outflowCellIDs = new HashSet();
            if (!(outflowCellIDsSet == null)) {
                outflowCellIDs.addAll(outflowCellIDsSet);
            }
            long row;
            long col;
            if (_Grid2DSquareCell.getClass() == Grids_GridInt.class) {
                Grids_GridInt _Grid2DSquareCellInt = (Grids_GridInt) _Grid2DSquareCell;
                int noDataValue = _Grid2DSquareCellInt.getNoDataValue(handleOutOfMemoryError);
                int height;
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        height = _Grid2DSquareCellInt.getCell(row, col, handleOutOfMemoryError);
                        if (_TreatNoDataValueAsOutflow) {
                            if ((height == noDataValue) || (height <= outflowHeight)) {
                                outflowCellIDs.add(_Grid2DSquareCellInt.getCellID(row, col, handleOutOfMemoryError));
                            }
                        } else {
                            if ((height != noDataValue) && (height <= outflowHeight)) {
                                outflowCellIDs.add(_Grid2DSquareCellInt.getCellID(row, col, handleOutOfMemoryError));
                            }
                        }
                    }
                }
            } else {
                // ( _Grid2DSquareCell.getClass() == Grids_GridDouble.class )
                Grids_GridDouble _Grid2DSquareCellDouble = (Grids_GridDouble) _Grid2DSquareCell;
                double noDataValue = _Grid2DSquareCellDouble.getNoDataValue(handleOutOfMemoryError);
                double height;
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        height = _Grid2DSquareCellDouble.getCell(row, col, handleOutOfMemoryError);
                        if (_TreatNoDataValueAsOutflow) {
                            if ((height == noDataValue) || (height <= outflowHeight)) {
                                outflowCellIDs.add(_Grid2DSquareCellDouble.getCellID(row, col, handleOutOfMemoryError));
                            }
                        } else {
                            if ((height != noDataValue) && (height <= outflowHeight)) {
                                outflowCellIDs.add(_Grid2DSquareCellDouble.getCellID(row, col, handleOutOfMemoryError));
                            }
                        }
                    }
                }
            }
            return outflowCellIDs;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                    throw a_OutOfMemoryError;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                getHollowFilledDEMOutflowCellIDs(
                        outflowCellIDsSet,
                        outflowHeight,
                        _Grid2DSquareCell,
                        nrows,
                        ncols,
                        _TreatNoDataValueAsOutflow,
                        handleOutOfMemoryError);
            }
            throw a_OutOfMemoryError;
        }
    }

    /**
     *
     *
     *
     * @param _Grid2DSquareCell Grids_AbstractGridNumber to be processed.
     * @param nrows Number of rows in _Grid2DSquareCell.
     * @param ncols Number of columns in _Grid2DSquareCell.
     * @param handleOutOfMemoryError If true then encountered OutOfMemeroyErrors
     * are handled. If false then an encountered OutOfMemeroyError is thrown.
     * @return HashSet containing _CellIDs which identifies cells which are
     * hollows. If _TreatNoDataValueAsOutflow is true then hollows are cells for
     * which all neighbouring cells in the immediate 8 cell neighbourhood are
     * either the same value or higher. If _TreatNoDataValueAsOutflow is false
     * then hollows are cells for which all neighbouring cells in the immediate
     * 8 cell neighbourhood are either the same value or higher or noDataValues.
     */
    private HashSet getHollowFilledDEMInitialHollowsHashSet(
            Grids_AbstractGridNumber _Grid2DSquareCell,
            long nrows,
            long ncols,
            boolean _TreatNoDataValueAsOutflow,
            boolean handleOutOfMemoryError) {
        try {
            ge.getGrids().add(_Grid2DSquareCell);
            HashSet initialHollowsHashSet = new HashSet();
            int k;
            // Initialise hollows
            long row;
            long col;
            long p;
            long q;
            if (_Grid2DSquareCell.getClass() == Grids_GridInt.class) {
                Grids_GridInt _Grid2DSquareCellInt = (Grids_GridInt) _Grid2DSquareCell;
                int noDataValue = _Grid2DSquareCellInt.getNoDataValue(handleOutOfMemoryError);
                int[] heights = new int[9];
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        heights[0] = _Grid2DSquareCellInt.getCell(row, col, handleOutOfMemoryError);
                        if (heights[0] != noDataValue) {
                            k = 0;
                            for (p = -1; p < 2; p++) {
                                for (q = -1; q < 2; q++) {
                                    if (!(p == 0 && q == 0)) {
                                        k++;
                                        heights[k] = _Grid2DSquareCellInt.getCell(row + p, col + q, handleOutOfMemoryError);
                                    }
                                }
                            }
                            if (_TreatNoDataValueAsOutflow) {
                                if ((heights[1] >= heights[0])
                                        && (heights[2] >= heights[0])
                                        && (heights[3] >= heights[0])
                                        && (heights[4] >= heights[0])
                                        && (heights[5] >= heights[0])
                                        && (heights[6] >= heights[0])
                                        && (heights[7] >= heights[0])
                                        && (heights[8] >= heights[0])) {
                                    initialHollowsHashSet.add(_Grid2DSquareCell.getCellID(row, col, handleOutOfMemoryError));
                                }
                            } else {
                                if ((heights[1] >= heights[0] || heights[1] == noDataValue)
                                        && (heights[2] >= heights[0] || heights[2] == noDataValue)
                                        && (heights[3] >= heights[0] || heights[3] == noDataValue)
                                        && (heights[4] >= heights[0] || heights[4] == noDataValue)
                                        && (heights[5] >= heights[0] || heights[5] == noDataValue)
                                        && (heights[6] >= heights[0] || heights[6] == noDataValue)
                                        && (heights[7] >= heights[0] || heights[7] == noDataValue)
                                        && (heights[8] >= heights[0] || heights[8] == noDataValue)) {
                                    initialHollowsHashSet.add(_Grid2DSquareCell.getCellID(row, col, handleOutOfMemoryError));
                                }
                            }
                        }
                    }
                }
            } else {
                // ( _Grid2DSquareCell.getClass() == Grids_GridDouble.class )
                Grids_GridDouble _Grid2DSquareCellDouble = (Grids_GridDouble) _Grid2DSquareCell;
                double noDataValue = _Grid2DSquareCellDouble.getNoDataValue(handleOutOfMemoryError);
                double[] heights = new double[9];
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        heights[0] = _Grid2DSquareCellDouble.getCell(row, col, handleOutOfMemoryError);
                        if (heights[0] != noDataValue) {
                            k = 0;
                            for (p = -1; p < 2; p++) {
                                for (q = -1; q < 2; q++) {
                                    if (!(p == 0 && q == 0)) {
                                        k++;
                                        heights[k] = _Grid2DSquareCellDouble.getCell(row + p, col + q, handleOutOfMemoryError);
                                    }
                                }
                            }
                            if (_TreatNoDataValueAsOutflow) {
                                if ((heights[1] >= heights[0])
                                        && (heights[2] >= heights[0])
                                        && (heights[3] >= heights[0])
                                        && (heights[4] >= heights[0])
                                        && (heights[5] >= heights[0])
                                        && (heights[6] >= heights[0])
                                        && (heights[7] >= heights[0])
                                        && (heights[8] >= heights[0])) {
                                    initialHollowsHashSet.add(_Grid2DSquareCell.getCellID(row, col, handleOutOfMemoryError));
                                }
                            } else {
                                if ((heights[1] >= heights[0] || heights[1] == noDataValue)
                                        && (heights[2] >= heights[0] || heights[2] == noDataValue)
                                        && (heights[3] >= heights[0] || heights[3] == noDataValue)
                                        && (heights[4] >= heights[0] || heights[4] == noDataValue)
                                        && (heights[5] >= heights[0] || heights[5] == noDataValue)
                                        && (heights[6] >= heights[0] || heights[6] == noDataValue)
                                        && (heights[7] >= heights[0] || heights[7] == noDataValue)
                                        && (heights[8] >= heights[0] || heights[8] == noDataValue)) {
                                    initialHollowsHashSet.add(_Grid2DSquareCell.getCellID(row, col, handleOutOfMemoryError));
                                }
                            }
                        }
                    }
                }
            }
            return initialHollowsHashSet;
        } catch (OutOfMemoryError _OutOfMemoryError0) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                    throw _OutOfMemoryError0;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return getHollowFilledDEMInitialHollowsHashSet(
                        _Grid2DSquareCell,
                        nrows,
                        ncols,
                        _TreatNoDataValueAsOutflow,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError0;
            }
        }
    }

    /**
     * Returns a HashSet of _CellIDs for identifying any cells that might be
     * hollows in grid, only those cells with IDs in the neighbourhood of those
     * cells with IDs in _CellIDs need be checked.
     *
     *
     *
     * @param _Grid2DSquareCell The Grids_AbstractGridNumber to be processed.
     * @param _CellIDs the HashSet storing _CellIDs that must be examined.
     */
    private HashSet getHollowsInNeighbourhood(
            Grids_AbstractGridNumber _Grid2DSquareCell,
            HashSet _CellIDs,
            boolean _TreatNoDataValueAsOutflow,
            boolean handleOutOfMemoryError) {
        try {
            ge.getGrids().add(_Grid2DSquareCell);
            HashSet result = new HashSet();
            HashSet visited1 = new HashSet();
            Grids_2D_ID_long cellID;
            long row;
            long col;
            long a;
            long b;
            long p;
            long q;
            int k;
            Iterator iterator1 = _CellIDs.iterator();
            if (_Grid2DSquareCell.getClass() == Grids_GridInt.class) {
                Grids_GridInt _Grid2DSquareCellInt = (Grids_GridInt) _Grid2DSquareCell;
                int noDataValue = _Grid2DSquareCellInt.getNoDataValue(handleOutOfMemoryError);
                int[] heights = new int[9];
                while (iterator1.hasNext()) {
                    cellID = (Grids_2D_ID_long) iterator1.next();
                    if (!visited1.contains(cellID)) {
                        row = cellID.getRow();
                        col = cellID.getCol();
                        // Examine neighbourhood
                        for (a = -1; a < 2; a++) {
                            for (b = -1; b < 2; b++) {
                                visited1.add(_Grid2DSquareCellInt.getCellID(row + a, col + b, handleOutOfMemoryError));
                                heights[0] = _Grid2DSquareCellInt.getCell(row + a, col + b, handleOutOfMemoryError);
                                if (heights[0] != noDataValue) {
                                    k = 0;
                                    for (p = -1; p < 2; p++) {
                                        for (q = -1; q < 2; q++) {
                                            if (!(p == 0 && q == 0)) {
                                                k++;
                                                heights[k] = _Grid2DSquareCellInt.getCell(
                                                        row + a + p,
                                                        col + b + q,
                                                        handleOutOfMemoryError);
                                            }
                                        }
                                    }
                                    if (_TreatNoDataValueAsOutflow) {
                                        if ((heights[1] >= heights[0])
                                                && (heights[2] >= heights[0])
                                                && (heights[3] >= heights[0])
                                                && (heights[4] >= heights[0])
                                                && (heights[5] >= heights[0])
                                                && (heights[6] >= heights[0])
                                                && (heights[7] >= heights[0])
                                                && (heights[8] >= heights[0])) {
                                            result.add(_Grid2DSquareCell.getCellID(row + a, col + b, handleOutOfMemoryError));
                                        }
                                    } else {
                                        if ((heights[1] >= heights[0] || heights[1] == noDataValue)
                                                && (heights[2] >= heights[0] || heights[2] == noDataValue)
                                                && (heights[3] >= heights[0] || heights[3] == noDataValue)
                                                && (heights[4] >= heights[0] || heights[4] == noDataValue)
                                                && (heights[5] >= heights[0] || heights[5] == noDataValue)
                                                && (heights[6] >= heights[0] || heights[6] == noDataValue)
                                                && (heights[7] >= heights[0] || heights[7] == noDataValue)
                                                && (heights[8] >= heights[0] || heights[8] == noDataValue)) {
                                            result.add(_Grid2DSquareCellInt.getCellID(row + a, col + b, handleOutOfMemoryError));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // ( _Grid2DSquareCell.getClass() == Grids_GridDouble.class )
                Grids_GridDouble _Grid2DSquareCellDouble = (Grids_GridDouble) _Grid2DSquareCell;
                double noDataValue = _Grid2DSquareCellDouble.getNoDataValue(handleOutOfMemoryError);
                double[] heights = new double[9];
                while (iterator1.hasNext()) {
                    cellID = (Grids_2D_ID_long) iterator1.next();
                    if (!visited1.contains(cellID)) {
                        row = cellID.getRow();
                        col = cellID.getCol();
                        // Examine neighbourhood
                        for (a = -1; a < 2; a++) {
                            for (b = -1; b < 2; b++) {
                                visited1.add(_Grid2DSquareCellDouble.getCellID(row + a, col + b, handleOutOfMemoryError));
                                heights[0] = _Grid2DSquareCellDouble.getCell(row + a, col + b, handleOutOfMemoryError);
                                if (heights[0] != noDataValue) {
                                    k = 0;
                                    for (p = -1; p < 2; p++) {
                                        for (q = -1; q < 2; q++) {
                                            if (!(p == 0 && q == 0)) {
                                                k++;
                                                heights[k] = _Grid2DSquareCellDouble.getCell(
                                                        row + a + p,
                                                        col + b + q,
                                                        handleOutOfMemoryError);
                                            }
                                        }
                                    }
                                    if (_TreatNoDataValueAsOutflow) {
                                        if ((heights[1] >= heights[0])
                                                && (heights[2] >= heights[0])
                                                && (heights[3] >= heights[0])
                                                && (heights[4] >= heights[0])
                                                && (heights[5] >= heights[0])
                                                && (heights[6] >= heights[0])
                                                && (heights[7] >= heights[0])
                                                && (heights[8] >= heights[0])) {
                                            result.add(_Grid2DSquareCell.getCellID(row + a, col + b, handleOutOfMemoryError));
                                        }
                                    } else {
                                        if ((heights[1] >= heights[0] || heights[1] == noDataValue)
                                                && (heights[2] >= heights[0] || heights[2] == noDataValue)
                                                && (heights[3] >= heights[0] || heights[3] == noDataValue)
                                                && (heights[4] >= heights[0] || heights[4] == noDataValue)
                                                && (heights[5] >= heights[0] || heights[5] == noDataValue)
                                                && (heights[6] >= heights[0] || heights[6] == noDataValue)
                                                && (heights[7] >= heights[0] || heights[7] == noDataValue)
                                                && (heights[8] >= heights[0] || heights[8] == noDataValue)) {
                                            result.add(_Grid2DSquareCellDouble.getCellID(row + a, col + b, handleOutOfMemoryError));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError0) {
            ge.clearMemoryReserve();
            if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                throw _OutOfMemoryError0;
            }
            ge.initMemoryReserve(handleOutOfMemoryError);
            return getHollowsInNeighbourhood(
                    _Grid2DSquareCell,
                    _CellIDs,
                    _TreatNoDataValueAsOutflow,
                    handleOutOfMemoryError);
        }
    }

    private HashSet getHollowFilledDEMCalculateHollows(
            Grids_AbstractGridNumber _Grid2DSquareCell,
            HashSet cellIDs,
            boolean handleOutOfMemoryError) {
        try {
            ge.getGrids().add(_Grid2DSquareCell);

            if ((_Grid2DSquareCell.getNCols(handleOutOfMemoryError) * _Grid2DSquareCell.getNRows(handleOutOfMemoryError)) / 4 < cellIDs.size()) {
                // return getInitialHollowsHashSet( grid );
            }
            HashSet result = new HashSet();
            Grids_2D_ID_long cellID;

            long row;

            long col;

            long p;

            long q;

            int k;
            //int noDataCount;
            Iterator iterator1 = cellIDs.iterator();

            if (_Grid2DSquareCell.getClass() == Grids_GridInt.class) {
                Grids_GridInt _Grid2DSquareCellInt = (Grids_GridInt) _Grid2DSquareCell;
                int noDataValue = _Grid2DSquareCellInt.getNoDataValue(handleOutOfMemoryError);
                int[] heights = new int[9];
                while (iterator1.hasNext()) {
                    cellID = (Grids_2D_ID_long) iterator1.next();
                    row = cellID.getRow();
                    col = cellID.getCol();
                    heights[0] = _Grid2DSquareCellInt.getCell(row, col, handleOutOfMemoryError);
                    if (heights[0] != noDataValue) {
                        //noDataCount = 0;
                        k = 0;
                        for (p = -1; p < 2; p++) {
                            for (q = -1; q < 2; q++) {
                                if (!(p == 0 && q == 0)) {
                                    k++;
                                    heights[k] = _Grid2DSquareCellInt.getCell(row + p, col + q, handleOutOfMemoryError);
                                    //if ( heights[ k ] == noDataValue ) {
                                    //    noDataCount ++;
                                    //}
                                }
                            }
                        }
                        // This deals with single isolated cells surrounded by noDataValues
                        //if ( noDataCount < 8 ) {
                        if ((heights[1] >= heights[0] || heights[1] == noDataValue)
                                && (heights[2] >= heights[0] || heights[2] == noDataValue)
                                && (heights[3] >= heights[0] || heights[3] == noDataValue)
                                && (heights[4] >= heights[0] || heights[4] == noDataValue)
                                && (heights[5] >= heights[0] || heights[5] == noDataValue)
                                && (heights[6] >= heights[0] || heights[6] == noDataValue)
                                && (heights[7] >= heights[0] || heights[7] == noDataValue)
                                && (heights[8] >= heights[0] || heights[8] == noDataValue)) {
                            result.add(cellID);
                        }
                        //}
                    }
                }
            } else { // ( _Grid2DSquareCell.getClass() == Grids_GridDouble.class )
                Grids_GridDouble _Grid2DSquareCellDouble = (Grids_GridDouble) _Grid2DSquareCell;

                double noDataValue = _Grid2DSquareCellDouble.getNoDataValue(handleOutOfMemoryError);

                double[] heights = new double[9];

                while (iterator1.hasNext()) {
                    cellID = (Grids_2D_ID_long) iterator1.next();
                    row = cellID.getRow();
                    col = cellID.getCol();
                    heights[0] = _Grid2DSquareCellDouble.getCell(row, col, handleOutOfMemoryError);

                    if (heights[0] != noDataValue) {
                        //noDataCount = 0;
                        k = 0;

                        for (p = -1; p
                                < 2; p++) {
                            for (q = -1; q
                                    < 2; q++) {
                                if (!(p == 0 && q == 0)) {
                                    k++;
                                    heights[k] = _Grid2DSquareCellDouble.getCell(row + p, col + q, handleOutOfMemoryError);
                                    //if ( heights[ k ] == noDataValue ) {
                                    //    noDataCount ++;
                                    //}

                                }
                            }
                        }
                        // This deals with single isolated cells surrounded by noDataValues
                        //if ( noDataCount < 8 ) {
                        if ((heights[1] >= heights[0] || heights[1] == noDataValue)
                                && (heights[2] >= heights[0] || heights[2] == noDataValue)
                                && (heights[3] >= heights[0] || heights[3] == noDataValue)
                                && (heights[4] >= heights[0] || heights[4] == noDataValue)
                                && (heights[5] >= heights[0] || heights[5] == noDataValue)
                                && (heights[6] >= heights[0] || heights[6] == noDataValue)
                                && (heights[7] >= heights[0] || heights[7] == noDataValue)
                                && (heights[8] >= heights[0] || heights[8] == noDataValue)) {
                            result.add(cellID);

                        } //}
                    }
                }
            }
            return result;

        } catch (OutOfMemoryError _OutOfMemoryError0) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                    throw _OutOfMemoryError0;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                getHollowFilledDEMCalculateHollows(
                        _Grid2DSquareCell, cellIDs, handleOutOfMemoryError);
            }
            throw _OutOfMemoryError0;
        }
    }

    private boolean isGridInt(int i) {
        return i == 0 || i == 13 || i == 24 || i == 35 || i == 46 || i == 57 || i == 63;
    }

    /**
     * Returns an Grids_GridDouble[] metrics1 where: metrics1[0] = no data
     * count; metrics1[1] = flatness; metrics1[2] = roughness; metrics1[3] =
     * slopyness; metrics1[4] = levelness; metrics1[5] = totalDownness;
     * metrics1[6] = averageDownness; metrics1[7] = totalUpness; metrics1[8] =
     * averageUpness; metrics1[9] = maxd_hhhh [ sum of distance weighted maximum
     * height differences ]; metrics1[10] = mind_hhhh [ sum of distance weighted
     * minimum height differences ]; metrics1[11] = sumd_hhhh [ sum of distance
     * weighted height differences ]; metrics1[12] = aved_hhhh [ sum of distance
     * weighted average height difference ]; metrics1[13] = count_hhhh [ count
     * ]; metrics1[14] = w_hhhh [ sum of distance weights ]; metrics1[15] =
     * mind_hxhx_ai_hhhl [ sum of distance weighted ( minimum difference of
     * cells adjacent to lower cell ) ]; metrics1[16] = maxd_hxhx_ai_hhhl [ sum
     * of distance weighted ( maximum difference of cells adjacent to lower cell
     * ) ]; metrics1[17] = sumd_hxhx_ai_hhhl [ sum of distance weighted ( sum of
     * differences of cells adjacent to lower cell ) ]; metrics1[18] =
     * d_xhxx_ai_hhhl [ sum of distance weighted ( difference of cell opposite
     * lower cell ) ]; metrics1[19] = d_xxxl_ai_hhhl [ sum of distance weighted
     * ( difference of lower cell ) ]; metrics1[20] = sumd_xhxl_ai_hhhl [ sum of
     * distance weighted ( sum of differences of lower cell and cell opposite )
     * ]; metrics1[21] = mind_abs_xhxl_ai_hhhl [ sum of distance weighted (
     * minimum difference magnitude of lower cell and cell opposite ) ];
     * metrics1[22] = maxd_abs_xhxl_ai_hhhl [ sum of distance weighted ( maximum
     * difference magnitude of lower cell and cell opposite ) ]; metrics1[23] =
     * sumd_abs_xhxl_ai_hhhl [ sum of distance weighted ( sum of difference
     * magnitudes of lower cell and cell opposite ) ]; metrics1[24] = count_hhhl
     * [ count ]; metrics1[25] = w_hhhl [ sum of distance weights ];
     * metrics1[26] = mind_hxhx_ai_hlhl [ sum of distance weighted ( minimum
     * difference of higher cells ) ]; metrics1[27] = maxd_hxhx_ai_hlhl [ sum of
     * distance weighted ( maximum difference of higher cells ) ]; metrics1[28]
     * = sumd_hxhx_ai_hlhl [ sum of distance weighted ( sum differences of
     * higher cells ) ]; metrics1[29] = mind_xlxl_ai_hlhl [ sum of distance
     * weighted ( minimum difference of lower cells ) ]; metrics1[30] =
     * maxd_xlxl_ai_hlhl [ sum of distance weighted ( maximum difference of
     * lower cells ) ]; metrics1[31] = sumd_xlxl_ai_hlhl [ sum of distance
     * weighted ( sum of differences of lower cells ) ]; metrics1[32] =
     * mind_abs_hlhl [ sum of distance weighted ( minimum difference magnitude
     * of cells ) ]; metrics1[33] = maxd_abs_hlhl [ sum of distance weighted (
     * maximum difference magnitude of cells ) ]; metrics1[34] = sumd_abs_hlhl [
     * sum of distance weighted ( sum of difference magnitudes of cells ) ];
     * metrics1[35] = count_hlhl [ count ]; metrics1[36] = w_hlhl [ sum of
     * distance weights ]; metrics1[37] = mind_hhxx_ai_hhll [ sum of distance
     * weighted ( minimum difference of higher cells ) ]; metrics1[38] =
     * maxd_hhxx_ai_hhll [ sum of distance weighted ( maximum difference of
     * higher cells ) ]; metrics1[39] = sumd_hhxx_ai_hhll [ sum of distance
     * weighted ( sum of differences of higher cells ) ]; metrics1[40] =
     * mind_xxll_ai_hhll [ sum of distance weighted ( minimum difference of
     * lower cells ) ]; metrics1[41] = maxd_xxll_ai_hhll [ sum of distance
     * weighted ( maximum difference of lower cells ) ]; metrics1[42] =
     * sumd_xxll_ai_hhll [ sum of distance weighted ( sum of differences of
     * lower cells ) ]; metrics1[43] = mind_abs_hhll [ sum of distance weighted
     * ( minimum difference magnitude of cells ) ]; metrics1[44] = maxd_abs_hhll
     * [ sum of distance weighted ( maximum difference magnitude of cells ) ];
     * metrics1[45] = sumd_abs_hhll [ sum of distance weighted ( sum of
     * difference magnitudes of cells ) ]; metrics1[46] = count_hhll [ count ];
     * metrics1[47] = w_hhll [ sum of distance weights ]; metrics1[48] =
     * mind_lxlx_ai_lllh [ sum of distance weighted ( minimum difference of
     * cells adjacent to higher cell ) ]; metrics1[49] = maxd_lxlx_ai_lllh [ sum
     * of distance weighted ( maximum difference of cells adjacent to higher
     * cell ) ]; metrics1[50] = sumd_lxlx_ai_lllh [ sum of distance weighted (
     * sum of differences of cells adjacent to higher cell ) ]; metrics1[51] =
     * d_xlxx_ai_lllh [ sum of distance weighted ( difference of cell opposite
     * higher cell ) ]; metrics1[52] = d_xxxh_ai_lllh [ sum of distance weighted
     * ( difference of higher cell ) ]; metrics1[53] = sumd_xlxh_ai_lllh [ sum
     * of distance weighted ( sum of differences of higher cell and cell
     * opposite ) ]; metrics1[54] = mind_abs_xlxh_ai_lllh [ sum of distance
     * weighted ( minimum difference magnitude of higher cell and cell opposite
     * ) ]; metrics1[55] = maxd_abs_xlxh_ai_lllh [ sum of distance weighted (
     * maximum difference magnitude of higher cell and cell opposite ) ];
     * metrics1[56] = sumd_abs_xlxh_ai_lllh [ sum of distance weighted ( sum of
     * difference magnitudes of higher cell and cell opposite ) ]; metrics1[57]
     * = count_lllh [ count ]; metrics1[58] = w_lllh [ sum of distance weights
     * ]; metrics1[59] = maxd_llll [ sum of distance weighted maximum height
     * differences ]; metrics1[60] = mind_llll [ sum of distance weighted
     * minimum height differences ]; metrics1[61] = sumd_llll [ sum of distance
     * weighted height differences ]; metrics1[62] = aved_llll [ sum of distance
     * weighted average height difference ]; metrics1[63] = count_llll [ count
     * ]; metrics1[64] = w_llll [ sum of distance weights ];
     *
     * @param g the Grids_GridDouble to be processed
     * @param distance the distance within which metrics will be calculated
     * @param weightIntersect kernel parameter ( weight at the centre )
     * @param weightFactor kernel parameter ( distance decay )
     * @param gridDoubleFactory The Grids_GridDoubleFactory for creating grids
     * @param gridIntFactory
     * @param swapOutInitialisedFiles
     * @param swapOutProcessedChunks
     * @param handleOutOfMemoryError
     * @return
     * @throws java.io.IOException
     */
    public Grids_AbstractGridNumber[] getMetrics1(
            Grids_AbstractGridNumber g,
            double distance,
            double weightIntersect,
            double weightFactor,
            Grids_GridDoubleFactory gridDoubleFactory,
            Grids_GridIntFactory gridIntFactory,
            boolean swapOutInitialisedFiles,
            boolean swapOutProcessedChunks,
            boolean handleOutOfMemoryError)
            throws IOException {
        try {
            ge.getGrids().add(g);
            if (gridDoubleFactory.getChunkNCols() != gridIntFactory.getChunkNCols()
                    || gridDoubleFactory.getChunkNRows() != gridIntFactory.getChunkNRows()) {
                log("Warning! ((gridDoubleFactory.getChunkNcols() "
                        + "!= gridIntFactory.getChunkNcols()) || "
                        + "(gridDoubleFactory.getChunkNrows() != "
                        + "gridIntFactory.getChunkNrows()))",
                        handleOutOfMemoryError);
            }
            Grids_AbstractGridNumber[] metrics1 = new Grids_AbstractGridNumber[65];
            long ncols = g.getNCols(handleOutOfMemoryError);
            long nrows = g.getNRows(handleOutOfMemoryError);
            Grids_Dimensions dimensions = g.getDimensions(handleOutOfMemoryError);
            boolean isInitialised = false;
            String[] metrics1Names = getMetrics1Names();
            File file;
            File directory = getDirectory(handleOutOfMemoryError);
            for (int i = 0; i < metrics1.length; i++) {
                file = ge.initFileDirectory(
                        directory,
                        metrics1Names[i],
                        handleOutOfMemoryError);
                do {
                    try {
                        if (isGridInt(i)) {
                            metrics1[i] = (Grids_GridInt) gridIntFactory.create(
                                    file,
                                    nrows,
                                    ncols,
                                    dimensions,
                                    handleOutOfMemoryError);
                            if (swapOutInitialisedFiles) {
                                metrics1[i].writeToFile(
                                        true, handleOutOfMemoryError);
                            }
                        } else {
                            metrics1[i] = (Grids_GridDouble) gridDoubleFactory.create(
                                    file,
                                    nrows,
                                    ncols,
                                    dimensions,
                                    handleOutOfMemoryError);
                        }
                        metrics1[i].setName(metrics1Names[i], handleOutOfMemoryError);
                        ge.getGrids().add(metrics1[i]);
                        isInitialised = true;
                    } catch (OutOfMemoryError e) {
                        ge.clearMemoryReserve();
                        System.err.println("OutOfMemoryError in getMetrics1(...) initialisation");
                        if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                            throw e;
                        }
                        ge.initMemoryReserve(handleOutOfMemoryError);
                    }
                    System.out.println("Initialised result[" + i + "]");
                } while (!isInitialised);
            }
            return getMetrics1(
                    metrics1,
                    g,
                    dimensions,
                    distance,
                    weightIntersect,
                    weightFactor,
                    swapOutProcessedChunks,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return getMetrics1(
                        g,
                        distance,
                        weightIntersect,
                        weightFactor,
                        gridDoubleFactory,
                        gridIntFactory,
                        swapOutInitialisedFiles,
                        swapOutProcessedChunks,
                        handleOutOfMemoryError);
            } else {
                //_OutOfMemoryError0.printStackTrace();
                //println( "getMetrics1(Grids_AbstractGridNumber,double,double,double,Grids_GridDoubleFactory,Grids_GridIntFactory,boolean" );
                throw e;
            }
        }
    }

//    public Grids_AbstractGridNumber get_Roughness(
//            Grids_AbstractGridNumber _Grid2DSquareCell,
//            double distance,
//            double weightIntersect,
//            double weightFactor,
//            Grids_GridDoubleFactory GridDoubleFactory,
//            Grids_GridIntFactory GridIntFactory,
//            boolean handleOutOfMemoryError )
//            throws IOException {
//        try {
//            ge.getGrids().add( _Grid2DSquareCell );
//            int _MessageLength = 1000;
//            String _Message0 = ge.initString( _MessageLength, handleOutOfMemoryError );
//            String _Message = ge.initString( _MessageLength, handleOutOfMemoryError );
//            if ( GridDoubleFactory.getChunkNCols() != GridIntFactory.getChunkNCols() ||
//                    GridDoubleFactory.getChunkNRows() != GridIntFactory.getChunkNRows() ) {
//                Log( "Warning! ( GridDoubleFactory.getChunkNcols() != GridIntFactory.getChunkNcols() || GridDoubleFactory.getChunkNrows() != GridIntFactory.getChunkNrows() )", handleOutOfMemoryError );
//            }
//            Grids_GridDouble _Roughness;
//            long ncols = _Grid2DSquareCell.getNCols( handleOutOfMemoryError );
//            long nrows = _Grid2DSquareCell.getNRows( handleOutOfMemoryError );
//            BigDecimal[] dimensions = _Grid2DSquareCell.getDimensions( handleOutOfMemoryError );
//            int cachedIndex = 0;
//            boolean isInitialised = false;
//            String[] _Metrics1Names = getMetrics1Names();
//            int _FilenameLength = 5000;
//            String _FileString;
//            File _File;
//            File Directory = getDirectory( handleOutOfMemoryError );
//            int _intZero = 0;
//            int i = 0;
//            boolean _iForInt = false;
//            _File = ge.initFileDirectory(
//                        Directory,
//                        _Metrics1Names[ i ],
//                        handleOutOfMemoryError );
//                do {
//                    try {
//                        isInitialised = false;
//                        _Roughness = ( Grids_GridDouble ) GridDoubleFactory.create(
//                                    _File,
//                                    nrows,
//                                    ncols,
//                                    dimensions,
//                                    ge,
//                                    handleOutOfMemoryError );
//                        ge.getGrids().add( _Roughness );
//                        isInitialised = true;
//                    } catch ( OutOfMemoryError _OutOfMemoryError0 ) {
//                        if ( handleOutOfMemoryError ) {
//                            clearMemoryReserve();
//                            swapChunks( handleOutOfMemoryError );
//                            initMemoryReserve( handleOutOfMemoryError );
//                        } else {
//                            throw _OutOfMemoryError0;
//                        }
//                    }
//                } while ( ! isInitialised );
//            checkAndMaybeFreeMemory();
//            return get_Roughness(
//                    _Roughness,
//                    _Grid2DSquareCell,
//                    dimensions,
//                    distance,
//                    weightIntersect,
//                    weightFactor,
//                    handleOutOfMemoryError );
//        } catch ( OutOfMemoryError _OutOfMemoryError0 ) {
//            if ( handleOutOfMemoryError ) {
//                clearMemoryReserve();
//                ge.getGrids().add( _Grid2DSquareCell );
//                swapChunk_AccountDetail( handleOutOfMemoryError );
//                initMemoryReserve( handleOutOfMemoryError );
//                return get_Roughness(
//                        _Grid2DSquareCell,
//                        distance,
//                        weightIntersect,
//                        weightFactor,
//                        GridDoubleFactory,
//                        GridIntFactory,
//                        handleOutOfMemoryError );
//            } else {
//                //_OutOfMemoryError0.printStackTrace();
//                //println( "getMetrics1(Grids_AbstractGridNumber,double,double,double,Grids_GridDoubleFactory,Grids_GridIntFactory,boolean" );
//                throw _OutOfMemoryError0;
//            }
//        }
//    }
    /**
     * TODO
     *
     * @return
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
     * Returns an Grids_GridDouble[] metrics1 where: \n metrics1[0] = no data
     * count; \n metrics1[1] = flatness; \n metrics1[2] = roughness; \n
     * metrics1[3] = slopyness; \n metrics1[4] = levelness; \n metrics1[5] =
     * totalDownness; \n metrics1[6] = averageDownness; \n metrics1[7] =
     * totalUpness; \n metrics1[8] = averageUpness; \n metrics1[9] = maxd_hhhh [
     * sum of distance weighted maximum height differences ]; \n metrics1[10] =
     * mind_hhhh [ sum of distance weighted minimum height differences ]; \n
     * metrics1[11] = sumd_hhhh [ sum of distance weighted height differences ];
     * \n metrics1[12] = aved_hhhh [ sum of distance weighted average height
     * difference ]; \n metrics1[13] = count_hhhh [ count ]; \n metrics1[14] =
     * w_hhhh [ sum of distance weights ]; \n metrics1[15] = mind_hxhx_ai_hhhl [
     * sum of distance weighted ( minimum difference of cells adjacent to lower
     * cell ) ]; \n metrics1[16] = maxd_hxhx_ai_hhhl [ sum of distance weighted
     * ( maximum difference of cells adjacent to lower cell ) ]; \n metrics1[17]
     * = sumd_hxhx_ai_hhhl [ sum of distance weighted ( sum of differences of
     * cells adjacent to lower cell ) ]; \n metrics1[18] = d_xhxx_ai_hhhl [ sum
     * of distance weighted ( difference of cell opposite lower cell ) ]; \n
     * metrics1[19] = d_xxxl_ai_hhhl [ sum of distance weighted ( difference of
     * lower cell ) ]; \n metrics1[20] = sumd_xhxl_ai_hhhl [ sum of distance
     * weighted ( sum of differences of lower cell and cell opposite ) ]; \n
     * metrics1[21] = mind_abs_xhxl_ai_hhhl [ sum of distance weighted ( minimum
     * difference magnitude of lower cell and cell opposite ) ]; \n metrics1[22]
     * = maxd_abs_xhxl_ai_hhhl [ sum of distance weighted ( maximum difference
     * magnitude of lower cell and cell opposite ) ]; \n metrics1[23] =
     * sumd_abs_xhxl_ai_hhhl [ sum of distance weighted ( sum of difference
     * magnitudes of lower cell and cell opposite ) ]; \n metrics1[24] =
     * count_hhhl [ count ]; \n metrics1[25] = w_hhhl [ sum of distance weights
     * ]; \n metrics1[26] = mind_hxhx_ai_hlhl [ sum of distance weighted (
     * minimum difference of higher cells ) ]; \n metrics1[27] =
     * maxd_hxhx_ai_hlhl [ sum of distance weighted ( maximum difference of
     * higher cells ) ]; \n metrics1[28] = sumd_hxhx_ai_hlhl [ sum of distance
     * weighted ( sum differences of higher cells ) ]; \n metrics1[29] =
     * mind_xlxl_ai_hlhl [ sum of distance weighted ( minimum difference of
     * lower cells ) ]; \n metrics1[30] = maxd_xlxl_ai_hlhl [ sum of distance
     * weighted ( maximum difference of lower cells ) ]; \n metrics1[31] =
     * sumd_xlxl_ai_hlhl [ sum of distance weighted ( sum of differences of
     * lower cells ) ]; \n metrics1[32] = mind_abs_hlhl [ sum of distance
     * weighted ( minimum difference magnitude of cells ) ]; \n metrics1[33] =
     * maxd_abs_hlhl [ sum of distance weighted ( maximum difference magnitude
     * of cells ) ]; \n metrics1[34] = sumd_abs_hlhl [ sum of distance weighted
     * ( sum of difference magnitudes of cells ) ]; \n metrics1[35] = count_hlhl
     * [ count ]; \n metrics1[36] = w_hlhl [ sum of distance weights ]; \n
     * metrics1[37] = mind_hhxx_ai_hhll [ sum of distance weighted ( minimum
     * difference of higher cells ) ]; \n metrics1[38] = maxd_hhxx_ai_hhll [ sum
     * of distance weighted ( maximum difference of higher cells ) ]; \n
     * metrics1[39] = sumd_hhxx_ai_hhll [ sum of distance weighted ( sum of
     * differences of higher cells ) ]; \n metrics1[40] = mind_xxll_ai_hhll [
     * sum of distance weighted ( minimum difference of lower cells ) ]; \n
     * metrics1[41] = maxd_xxll_ai_hhll [ sum of distance weighted ( maximum
     * difference of lower cells ) ]; \n metrics1[42] = sumd_xxll_ai_hhll [ sum
     * of distance weighted ( sum of differences of lower cells ) ]; \n
     * metrics1[43] = mind_abs_hhll [ sum of distance weighted ( minimum
     * difference magnitude of cells ) ]; \n metrics1[44] = maxd_abs_hhll [ sum
     * of distance weighted ( maximum difference magnitude of cells ) ]; \n
     * metrics1[45] = sumd_abs_hhll [ sum of distance weighted ( sum of
     * difference magnitudes of cells ) ]; \n metrics1[46] = count_hhll [ count
     * ]; \n metrics1[47] = w_hhll [ sum of distance weights ]; \n metrics1[48]
     * = mind_lxlx_ai_lllh [ sum of distance weighted ( minimum difference of
     * cells adjacent to higher cell ) ]; \n metrics1[49] = maxd_lxlx_ai_lllh [
     * sum of distance weighted ( maximum difference of cells adjacent to higher
     * cell ) ]; \n metrics1[50] = sumd_lxlx_ai_lllh [ sum of distance weighted
     * ( sum of differences of cells adjacent to higher cell ) ]; \n
     * metrics1[51] = d_xlxx_ai_lllh [ sum of distance weighted ( difference of
     * cell opposite higher cell ) ]; \n metrics1[52] = d_xxxh_ai_lllh [ sum of
     * distance weighted ( difference of higher cell ) ]; \n metrics1[53] =
     * sumd_xlxh_ai_lllh [ sum of distance weighted ( sum of differences of
     * higher cell and cell opposite ) ]; \n metrics1[54] =
     * mind_abs_xlxh_ai_lllh [ sum of distance weighted ( minimum difference
     * magnitude of higher cell and cell opposite ) ]; \n metrics1[55] =
     * maxd_abs_xlxh_ai_lllh [ sum of distance weighted ( maximum difference
     * magnitude of higher cell and cell opposite ) ]; \n metrics1[56] =
     * sumd_abs_xlxh_ai_lllh [ sum of distance weighted ( sum of difference
     * magnitudes of higher cell and cell opposite ) ]; \n metrics1[57] =
     * count_lllh [ count ]; \n metrics1[58] = w_lllh [ sum of distance weights
     * ]; \n metrics1[59] = maxd_llll [ sum of distance weighted maximum height
     * differences ]; \n metrics1[60] = mind_llll [ sum of distance weighted
     * minimum height differences ]; \n metrics1[61] = sumd_llll [ sum of
     * distance weighted height differences ]; \n metrics1[62] = aved_llll [ sum
     * of distance weighted average height difference ]; \n metrics1[63] =
     * count_llll [ count ]; \n metrics1[64] = w_llll [ sum of distance weights
     * ]; \n
     *
     * @param metrics1 an Grids_GridDouble[] for storing result \n
     * @param g the Grids_GridDouble to be processed \n
     * @param dimensions
     * @param distance the distance within which metrics will be calculated \n
     * @param weightIntersect kernel parameter ( weight at the centre ) \n
     * @param weightFactor kernel parameter ( distance decay ) \n Going directly
     * to this method is useful if the initialisation of the metrics1 is slow
     * and has already been done.
     * @param swapOutProcessedChunks If this is true, then intermediate swapping
     * is done to try to prevent OutOfMemoryErrors Being Encountered. Perhaps
     * set this to true for large grids if the process seems to get stuck.
     * @param handleOutOfMemoryError
     * @return
     */
    public Grids_AbstractGridNumber[] getMetrics1(
            Grids_AbstractGridNumber[] metrics1,
            Grids_AbstractGridNumber g,
            Grids_Dimensions dimensions,
            double distance,
            double weightIntersect,
            double weightFactor,
            boolean swapOutProcessedChunks,
            boolean handleOutOfMemoryError) {
        try {
            String methodName;
            methodName = "getMetrics1("
                    + "Grids_AbstractGridNumber[],Grids_AbstractGridNumber,"
                    + "Grids_Dimensions,double,double,double,boolean, boolean)";
            System.out.println(methodName);
            /**
             * Try to ensure there is enough memory to continue.
             */
            ge.checkAndMaybeFreeMemory(handleOutOfMemoryError);
            String name;
            String underScore = "_";
            double cellsize = dimensions.getCellsize().doubleValue();
            int cellDistance = (int) Math.ceil(distance / cellsize);
            double[] heights = new double[4];
            heights[0] = 0.0d;
            heights[1] = 0.0d;
            heights[2] = 0.0d;
            heights[3] = 0.0d;
            double[] diff = new double[4];
            diff[0] = 0.0d;
            diff[1] = 0.0d;
            diff[2] = 0.0d;
            diff[3] = 0.0d;
            double[] dummyDiff = new double[4];
            dummyDiff[0] = 0.0d;
            dummyDiff[1] = 0.0d;
            dummyDiff[2] = 0.0d;
            dummyDiff[3] = 0.0d;
            double[][] weights = getNormalDistributionKernelWeights(
                    g.getCellsizeDouble(handleOutOfMemoryError),
                    distance,
                    handleOutOfMemoryError);
            double[] metrics1ForCell = new double[metrics1.length];
            for (int i = 0; i < metrics1.length; i++) {
                metrics1ForCell[i] = 0.0d;
            }
            long row;
            long col;
            double x;
            double y;
            Grids_2D_ID_int chunkID;
            int nChunkRows = g.getNChunkRows(handleOutOfMemoryError);
            int nChunkCols = g.getNChunkCols(handleOutOfMemoryError);
            int chunkNRows;
            int chunkNCols;
            int chunkRow;
            int chunkCol;
            int cellRow;
            int cellCol;
            int i;
            String[] names = getMetrics1Names();
            int normalChunkNRows = g.getChunkNRows(0, handleOutOfMemoryError);
            int normalChunkNCols = g.getChunkNCols(0, handleOutOfMemoryError);
            if (g.getClass() == Grids_GridDouble.class) {
                Grids_GridDouble gridDouble;
                gridDouble = (Grids_GridDouble) g;
                double noDataValue = gridDouble.getNoDataValue(handleOutOfMemoryError);
                double height;
                Grids_AbstractGridChunkDouble gridChunkDouble;
                for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
                    ge.checkAndMaybeFreeMemory(handleOutOfMemoryError);
                    System.out.println("chunkRow(" + chunkRow + ")");
                    chunkNRows = g.getChunkNRows(chunkRow, handleOutOfMemoryError);
                    for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                        ge.checkAndMaybeFreeMemory(handleOutOfMemoryError);
                        System.out.println("chunkCol(" + chunkCol + ")");
                        chunkID = new Grids_2D_ID_int(
                                chunkRow, chunkCol);
                        ge.initNotToSwap();
                        ge.addToNotToSwap(g, chunkID, chunkRow, chunkCol,
                                normalChunkNRows, normalChunkNCols, cellDistance);
                        ge.addToNotToSwap(g, chunkID);
                        ge.addToNotToSwap(metrics1, chunkID);
                        gridChunkDouble = (Grids_AbstractGridChunkDouble) gridDouble.getGridChunk(
                                chunkRow,
                                chunkCol,
                                handleOutOfMemoryError);
                        boolean doLoop = true;
                        if (gridChunkDouble instanceof Grids_GridChunkDouble) {
                            if (((Grids_GridChunkDouble) gridChunkDouble).Value == noDataValue) {
                                doLoop = false;
                            }
                        }
                        if (doLoop) {
                            chunkNCols = g.getChunkNCols(
                                    chunkCol,
                                    handleOutOfMemoryError,
                                    chunkID);
                            ge.checkAndMaybeFreeMemory(handleOutOfMemoryError);
                            for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                                row = g.getRow(
                                        chunkRow,
                                        cellRow,
                                        chunkID,
                                        handleOutOfMemoryError);
                                y = g.getCellYDouble(
                                        row,
                                        handleOutOfMemoryError);
                                for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                                    col = g.getCol(
                                            chunkCol,
                                            cellCol,
                                            chunkID,
                                            handleOutOfMemoryError);
                                    x = gridDouble.getCellXDouble(
                                            col,
                                            handleOutOfMemoryError);
                                    //height = _Grid2DSquareCellDouble.getCell( cellRowIndex, cellColIndex, handleOutOfMemoryError );
                                    height = gridChunkDouble.getCell(
                                            cellRow,
                                            cellCol,
                                            handleOutOfMemoryError);
                                    if (height != noDataValue) {
                                        metrics1Calculate_All(
                                                gridDouble,
                                                cellsize,
                                                row,
                                                col,
                                                x,
                                                y,
                                                distance,
                                                cellDistance,
                                                weights,
                                                metrics1ForCell,
                                                heights,
                                                diff,
                                                dummyDiff);
                                        for (i = 0; i < metrics1.length; i++) {
                                            if (metrics1[i] instanceof Grids_GridInt) {
                                                ((Grids_GridInt) metrics1[i]).setCell(
                                                        row,
                                                        col,
                                                        (int) metrics1ForCell[i],
                                                        handleOutOfMemoryError);
                                            } else {
                                                ((Grids_GridDouble) metrics1[i]).setCell(
                                                        row,
                                                        col,
                                                        metrics1ForCell[i],
                                                        handleOutOfMemoryError);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        System.out.println("Done Chunk (" + chunkRow + ", " + chunkCol + ")");
                        if (swapOutProcessedChunks) {
                            for (i = 0; i < metrics1.length; i++) {
                                metrics1[i].swapChunk(chunkID, handleOutOfMemoryError);
                            }
                        }
                    }
                }
            } else {
                // (g.getClass() == Grids_GridInt.class)
                Grids_GridInt gridInt = (Grids_GridInt) g;
                int noDataValue = gridInt.getNoDataValue(handleOutOfMemoryError);
                int height;
                Grids_AbstractGridChunkInt gridChunkInt;
                for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
                    ge.checkAndMaybeFreeMemory(handleOutOfMemoryError);
                    chunkNRows = g.getChunkNRows(chunkRow, handleOutOfMemoryError);
                    System.out.println("chunkRow(" + chunkRow + ")");
                    for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                        ge.checkAndMaybeFreeMemory(handleOutOfMemoryError);
                        System.out.println("chunkColIndex(" + chunkCol + ")");
                        chunkID = new Grids_2D_ID_int(
                                chunkRow, chunkCol);
                        ge.initNotToSwap();
                        ge.addToNotToSwap(g, chunkID, chunkRow, chunkCol,
                                normalChunkNRows, normalChunkNCols, cellDistance);
                        ge.addToNotToSwap(g, chunkID);
                        ge.addToNotToSwap(metrics1, chunkID);
                        gridChunkInt = (Grids_AbstractGridChunkInt) gridInt.getGridChunk(
                                chunkRow,
                                chunkCol,
                                handleOutOfMemoryError);
                        boolean doLoop = true;
                        if (gridChunkInt instanceof Grids_GridChunkInt) {
                            if (((Grids_GridChunkInt) gridChunkInt).Value == noDataValue) {
                                doLoop = false;
                            }
                        }
                        if (doLoop) {
                            chunkNCols = g.getChunkNCols(
                                    chunkCol,
                                    handleOutOfMemoryError);
                            for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                                row = g.getRow(chunkRow, cellRow, chunkID,
                                        handleOutOfMemoryError);
                                y = g.getCellYDouble(
                                        row,
                                        handleOutOfMemoryError);
                                for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                                    col = g.getCol(
                                            chunkCol,
                                            cellCol,
                                            chunkID,
                                            handleOutOfMemoryError);
                                    x = gridInt.getCellXDouble(
                                            cellCol,
                                            handleOutOfMemoryError);
                                    //height = _Grid2DSquareCellDouble.getCell( cellRowIndex, cellColIndex, handleOutOfMemoryError );
                                    height = gridChunkInt.getCell(
                                            cellRow,
                                            cellCol,
                                            handleOutOfMemoryError,
                                            chunkID);
                                    if (height != noDataValue) {
                                        metrics1Calculate_All(
                                                gridInt,
                                                cellsize,
                                                row,
                                                col,
                                                x,
                                                y,
                                                distance,
                                                cellDistance,
                                                weights,
                                                metrics1ForCell,
                                                heights,
                                                diff,
                                                dummyDiff);
                                        for (i = 0; i < metrics1.length; i++) {
                                            if (metrics1[i] instanceof Grids_GridInt) {
                                                ((Grids_GridInt) metrics1[i]).setCell(
                                                        row,
                                                        col,
                                                        (int) metrics1ForCell[i],
                                                        handleOutOfMemoryError);
                                            } else {
                                                ((Grids_GridDouble) metrics1[i]).setCell(
                                                        row,
                                                        col,
                                                        metrics1ForCell[i],
                                                        handleOutOfMemoryError);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        System.out.println(
                                "Done Chunk (" + chunkRow + ", " + chunkCol + ")");
                        if (swapOutProcessedChunks) {
                            for (i = 0; i < metrics1.length; i++) {
                                metrics1[i].swapChunk(chunkID, handleOutOfMemoryError);
                            }
                        }
                    }
                }
            }
            for (i = 0; i < names.length; i++) {
                name = ge.initString(
                        ge.initString(names[i], underScore, handleOutOfMemoryError),
                        ge.toString(distance, handleOutOfMemoryError),
                        handleOutOfMemoryError);
                metrics1[i].setName(name, handleOutOfMemoryError);
            }
            return metrics1;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                    throw e;
                }
                return getMetrics1(
                        metrics1,
                        g,
                        dimensions,
                        distance,
                        weightIntersect,
                        weightFactor,
                        swapOutProcessedChunks,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

//    public Grids_GridDouble get_Roughness(
//            Grids_GridDouble _Roughness,
//            Grids_AbstractGridNumber _Grid2DSquareCell,
//            BigDecimal[] dimensions,
//            double distance,
//            double weightIntersect,
//            double weightFactor,
//            boolean handleOutOfMemoryError ) {
//        try {
//            ge.getGrids().add( _Grid2DSquareCell );
//            int _MessageLength = 1000;
//            String _Message0 = ge.initString( _MessageLength, handleOutOfMemoryError );
//            String _Message = ge.initString( _MessageLength, handleOutOfMemoryError );
//            int _NameLength = 1000;
//            String _Name = ge.initString( _NameLength, handleOutOfMemoryError );
//            _Name = ge.initString( _NameLength, handleOutOfMemoryError );
//            String _UnderScore = "_";
//            boolean _boolean1 = true;
//            long ncols = _Grid2DSquareCell.getNCols( handleOutOfMemoryError );
//            long nrows = _Grid2DSquareCell.getNRows( handleOutOfMemoryError );
//            double cellsize = dimensions[0].doubleValue();
//            int cellDistance = ( int ) Math.ceil( distance / cellsize );
//            double value = 0.0d;
//            double[] heights = new double[ 4 ];
//            heights[ 0 ] = 0.0d;
//            heights[ 1 ] = 0.0d;
//            heights[ 2 ] = 0.0d;
//            heights[ 3 ] = 0.0d;
//            double[] diff = new double[ 4 ];
//            diff[ 0 ] = 0.0d;
//            diff[ 1 ] = 0.0d;
//            diff[ 2 ] = 0.0d;
//            diff[ 3 ] = 0.0d;
//            double[] dummyDiff = new double[ 4 ];
//            dummyDiff[ 0 ] = 0.0d;
//            dummyDiff[ 1 ] = 0.0d;
//            dummyDiff[ 2 ] = 0.0d;
//            dummyDiff[ 3 ] = 0.0d;
//            double[][] weights = getNormalDistributionKernelWeights(
//                    _Grid2DSquareCell,
//                    distance,
//                    handleOutOfMemoryError );
//            double _RoughnessValue = 0.0d;
//            long cellRowIndex = 0L;
//            long cellColIndex = 0L;
//            double x = 0.0d;
//            double y = 0.0d;
//            ID _ChunkID = new ID();
//            int _NChunkRows = _Grid2DSquareCell.getNChunkRows( handleOutOfMemoryError );
//            int nChunkCols = _Grid2DSquareCell.getNChunkCols( handleOutOfMemoryError );
//            int chunkNrows = _Grid2DSquareCell.getChunkNRows( handleOutOfMemoryError );
//            int chunkNcols = _Grid2DSquareCell.getChunkNCols( handleOutOfMemoryError );
//            int _ChunkRowIndex = 0;
//            int _ChunkColIndex = 0;
//            int chunkCellRowIndex = 0;
//            int chunkCellColIndex = 0;
//            int i = 0;
//            int _int_0 = 0;
//            if ( _Grid2DSquareCell.getClass() == Grids_GridDouble.class ) {
//                Grids_GridDouble _Grid2DSquareCellDouble = ( Grids_GridDouble ) _Grid2DSquareCell;
//                double noDataValue = _Grid2DSquareCellDouble.getNoDataValue( handleOutOfMemoryError );
//                double heightInt;
//                double thisHeightInt;;
//                Grids_AbstractGridChunkDouble _Grid2DSquareCellDoubleChunk = _Grid2DSquareCellDouble.getGridChunk( 0, 0, handleOutOfMemoryError );
//                for ( _ChunkRowIndex = _int_0; _ChunkRowIndex < _NChunkRows; _ChunkRowIndex ++ ) {
//                    chunkNrows = _Grid2DSquareCell.getChunkNRows(
//                            _ChunkRowIndex,
//                            handleOutOfMemoryError );
//                    for ( _ChunkColIndex = _int_0; _ChunkColIndex < nChunkCols; _ChunkColIndex ++ ) {
//                        _ChunkID = new ID( _NChunkRows, _ChunkRowIndex, _ChunkColIndex );
//                        try {
//                            _Grid2DSquareCellDoubleChunk = _Grid2DSquareCellDouble.getGridChunk(
//                                    _ChunkRowIndex,
//                                    _ChunkColIndex,
//                                    handleOutOfMemoryError );
//                            chunkNcols = _Grid2DSquareCell.getChunkNCols(
//                                    _ChunkColIndex,
//                                    handleOutOfMemoryError,
//                                    _ChunkID );
//                        } catch ( OutOfMemoryError _OutOfMemoryError ) {
//                            throw _OutOfMemoryError;
//                        }
//                        try {
//                            for ( chunkCellRowIndex = _int_0; chunkCellRowIndex < chunkNrows; chunkCellRowIndex ++ ) {
//                                try {
//                                    cellRowIndex = _Grid2DSquareCell.getRow(
//                                            _ChunkRowIndex,
//                                            chunkCellRowIndex,
//                                            handleOutOfMemoryError,
//                                            _ChunkID );
//                                    y = _Grid2DSquareCell.getCellYDouble(
//                                            cellRowIndex,
//                                            handleOutOfMemoryError,
//                                            _ChunkID );
//                                } catch ( OutOfMemoryError _OutOfMemoryError ) {
//                                    clearMemoryReserve();
//                                    System.out.println("Problem!!!");
//                                    ID _chunkID2 = new ID(
//                                            _Roughness.getNChunkCols( handleOutOfMemoryError ),
//                                            _Roughness.getChunkRow( cellRowIndex, handleOutOfMemoryError ),
//                                            _Roughness.getChunkCol( cellColIndex, handleOutOfMemoryError ) );
//                                    _Roughness.swapToFileGrid2DSquareCellChunkExcept(
//                                            _chunkID2,
//                                            handleOutOfMemoryError );
//                                    initMemoryReserve( handleOutOfMemoryError );
//                                    cellRowIndex = _Grid2DSquareCell.getRow( _ChunkRowIndex, chunkCellRowIndex, handleOutOfMemoryError );
//                                    y = _Grid2DSquareCell.getCellYDouble( cellRowIndex, handleOutOfMemoryError );
//                                }
//                                try {
//                                    for ( chunkCellColIndex = _int_0; chunkCellColIndex < chunkNcols; chunkCellColIndex ++ ) {
//                                        try {
//                                            try {
//                                                cellColIndex = _Grid2DSquareCell.getCellCol(
//                                                        _ChunkColIndex,
//                                                        chunkCellColIndex,
//                                                        handleOutOfMemoryError,
//                                                        _ChunkID );
//                                                x = _Grid2DSquareCellDouble.getCellXDouble(
//                                                        cellColIndex,
//                                                        handleOutOfMemoryError,
//                                                        _ChunkID );
//                                                //height = _Grid2DSquareCellDouble.getCell( cellRowIndex, cellColIndex, handleOutOfMemoryError );
//                                                height = _Grid2DSquareCellDoubleChunk.getCell(
//                                                        chunkCellRowIndex,
//                                                        chunkCellColIndex,
//                                                        noDataValue,
//                                                        handleOutOfMemoryError,
//                                                        _ChunkID );
//                                            } catch ( OutOfMemoryError _OutOfMemoryError ) {
//                                                clearMemoryReserve();
//                                                ID _chunkID2 = new ID(
//                                                        _Roughness.getNChunkCols( handleOutOfMemoryError ),
//                                                        _Roughness.getChunkRow( cellRowIndex, handleOutOfMemoryError ),
//                                                        _Roughness.getChunkCol( cellColIndex, handleOutOfMemoryError ) );
//                                                _Roughness.swapToFileGrid2DSquareCellChunkExcept(
//                                                        _chunkID2, handleOutOfMemoryError );
//                                                initMemoryReserve( handleOutOfMemoryError );
//                                                height = _Grid2DSquareCellDoubleChunk.getCell(
//                                                        chunkCellRowIndex,
//                                                        chunkCellColIndex,
//                                                        noDataValue,
//                                                        handleOutOfMemoryError );
//                                                cellColIndex = _Grid2DSquareCell.getCellCol( _ChunkColIndex, chunkCellColIndex, handleOutOfMemoryError );
//                                                x = _Grid2DSquareCellDouble.getCellXDouble( cellColIndex, handleOutOfMemoryError );
//                                            }
//                                            _boolean1 = height != noDataValue;
//                                        } catch ( OutOfMemoryError _OutOfMemoryError ) {
//                                            throw _OutOfMemoryError;
//                                        }
//                                        try {
//                                            if ( _boolean1 ) {
//                                                try {
//                                                    _RoughnessValue = _calculateRoughness(
//                                                            _Grid2DSquareCellDouble,
//                                                            cellsize,
//                                                            cellRowIndex,
//                                                            cellColIndex,
//                                                            x,
//                                                            y,
//                                                            distance,
//                                                            cellDistance,
//                                                            weights,
//                                                            _RoughnessValue,
//                                                            heights,
//                                                            diff,
//                                                            dummyDiff,
//                                                            handleOutOfMemoryError,
//                                                            _ChunkID );
//                                                } catch ( OutOfMemoryError _OutOfMemoryError ) {
//                                                    throw _OutOfMemoryError;
//                                                }
//                                                try {
//                                                    try {
//                                                            _Roughness.setCell(
//                                                                    cellRowIndex,
//                                                                    cellColIndex,
//                                                                    _RoughnessValue,
//                                                                    handleOutOfMemoryError,
//                                                                    _ChunkID );
//                                                        } catch ( OutOfMemoryError _OutOfMemoryError ) {
//                                                            clearMemoryReserve();
//                                                            ID _chunkID2 = new ID(
//                                                                    _Roughness.getNChunkCols( handleOutOfMemoryError ),
//                                                                    _Roughness.getChunkRow( cellRowIndex, handleOutOfMemoryError ),
//                                                                    _Roughness.getChunkCol( cellColIndex, handleOutOfMemoryError ) );
//                                                            _Roughness.swapToFileGrid2DSquareCellChunkExcept(
//                                                                    _chunkID2,
//                                                                    handleOutOfMemoryError );
//                                                            initMemoryReserve( handleOutOfMemoryError );
//                                                            _Roughness.setCell(
//                                                                    cellRowIndex,
//                                                                    cellColIndex,
//                                                                    _RoughnessValue,
//                                                                    handleOutOfMemoryError );
//                                                        }
//                                                } catch ( OutOfMemoryError _OutOfMemoryError ) {
//                                                    throw _OutOfMemoryError;
//                                                }
//                                            }
//                                        } catch ( OutOfMemoryError _OutOfMemoryError ) {
//                                            throw _OutOfMemoryError;
//                                        }
//                                    }
//                                } catch ( OutOfMemoryError _OutOfMemoryError ) {
//                                    throw _OutOfMemoryError;
//                                }
//                            }
//                        } catch ( OutOfMemoryError _OutOfMemoryError ) {
//                            throw _OutOfMemoryError;
//                        }
//                        try {
//                            _Message = "Done Chunk ( " + _ChunkRowIndex + ", " + _ChunkColIndex + " )";
//                            _Message = ge.println( _Message, _Message0 , handleOutOfMemoryError);
//                        } catch ( OutOfMemoryError _OutOfMemoryError ) {
//                            throw _OutOfMemoryError;
//                        }
//                    }
//                }
//            } else {
//                // Needs work to handle memory as well as Grids_GridDouble above!
//                // ( _Grid2DSquareCell.getClass() == Grids_GridInt.class )
//                Grids_GridInt _Grid2DSquareCellInt = ( Grids_GridInt ) _Grid2DSquareCell;
//                int noDataValue = _Grid2DSquareCellInt.getNoDataValue(
//                        handleOutOfMemoryError );
//                int height = Integer.MIN_VALUE;
//                int thisHeight = Integer.MIN_VALUE;
//                Grids_AbstractGridChunkInt _Grid2DSquareCellIntChunk = _Grid2DSquareCellInt.getGridChunk( 0, 0, handleOutOfMemoryError );
//                for ( _ChunkRowIndex = _int_0; _ChunkRowIndex < _NChunkRows; _ChunkRowIndex ++ ) {
//                    chunkNrows = _Grid2DSquareCell.getChunkNRows(
//                            _ChunkRowIndex,
//                            handleOutOfMemoryError );
//                    for ( _ChunkColIndex = _int_0; _ChunkColIndex < nChunkCols; _ChunkColIndex ++ ) {
//                        _Grid2DSquareCellIntChunk = _Grid2DSquareCellInt.getGridChunk(
//                                _ChunkRowIndex,
//                                _ChunkColIndex,
//                                handleOutOfMemoryError );
//                        chunkNcols = _Grid2DSquareCell.getChunkNCols(
//                                _ChunkColIndex,
//                                handleOutOfMemoryError );
//                        for ( chunkCellRowIndex = _int_0; chunkCellRowIndex < chunkNrows; chunkCellRowIndex ++ ) {
//                            cellRowIndex = _Grid2DSquareCell.getRow(
//                                    _ChunkRowIndex,
//                                    chunkCellRowIndex,
//                                    handleOutOfMemoryError );
//                            y = _Grid2DSquareCell.getCellYDouble(
//                                    cellRowIndex,
//                                    handleOutOfMemoryError );
//                            for ( chunkCellColIndex = _int_0; chunkCellColIndex < chunkNcols; chunkCellColIndex ++ ) {
//                                cellColIndex = _Grid2DSquareCell.getCellCol(
//                                        _ChunkColIndex,
//                                        chunkCellColIndex,
//                                        handleOutOfMemoryError );
//                                x = _Grid2DSquareCellInt.getCellXDouble(
//                                        cellColIndex,
//                                        handleOutOfMemoryError );
//                                height = _Grid2DSquareCellIntChunk.getCell(
//                                        chunkCellRowIndex,
//                                        chunkCellColIndex,
//                                        noDataValue,
//                                        handleOutOfMemoryError );
//                                _boolean1 = height != noDataValue;
//                                if ( _boolean1 ) {
//                                    _RoughnessValue = _calculateRoughness(
//                                            _Grid2DSquareCellInt,
//                                            cellsize,
//                                            cellRowIndex,
//                                            cellColIndex,
//                                            x,
//                                            y,
//                                            distance,
//                                            cellDistance,
//                                            weights,
//                                            _RoughnessValue,
//                                            heights,
//                                            diff,
//                                            dummyDiff,
//                                            handleOutOfMemoryError );
//                                     _RoughnessValue.setCell(
//                                                cellRowIndex,
//                                                cellColIndex,
//                                                _RoughnessValue,
//                                                handleOutOfMemoryError );
//                                }
//                            }
//                        }
//                        _Message = "Done Chunk ( " + _ChunkRowIndex + ", " + _ChunkColIndex + " )";
//                        _Message = ge.println( _Message, _Message0 , handleOutOfMemoryError);
//                    }
//                }
//            }
//            try {
//                _Name = ge.initString(
//                            ge.initString( name, _UnderScore, handleOutOfMemoryError ),
//                            toString( distance, handleOutOfMemoryError ),
//                            handleOutOfMemoryError );
//                    _Roughness.setName( _Name, handleOutOfMemoryError );
//                    _Name = ge.initString( _NameLength, handleOutOfMemoryError );
//            } catch ( OutOfMemoryError _OutOfMemoryError ) {
//                throw _OutOfMemoryError;
//            }
//            return metrics1;
//        } catch ( OutOfMemoryError _OutOfMemoryError0 ) {
//            clearMemoryReserve();
//            int _MessageLength = 1000;
//            String _Message0 = ge.initString( _MessageLength, handleOutOfMemoryError );
//            String _Message = ge.initString( _MessageLength, handleOutOfMemoryError );
//            _Message = "OutOfMemoryError in " + this.getClass().getName() + ".getMetrics1(Grid2DSquareCellAbstract[],Grid2DSquareCellAbstract,BigDecimal[],double,double,double,boolean)";
//            _Message = ge.println( _Message, _Message0 , handleOutOfMemoryError);
//            _OutOfMemoryError0.printStackTrace();
//            throw _OutOfMemoryError0;
//        }
//    }
    /**
     * Returns a double[] metrics1 of the cells in grid upto distance from a
     * cell given by rowIndex and colIndex. The elements of metrics1 do not
     * explicitly take into account any axis such as that which can be defined
     * from a metric of slope (general slope direction). Distance weighting is
     * done via a kernel precalculated as weights. Some elements of metrics1 are
     * weighted based on the difference in value (height) of the cell at
     * (rowIndex,colIndex) and other cell values within distance. Within
     * distance equidistant cells in 4 orthoganol directions are accounted for
     * in the metrics1. NB. Every cell is either higher, lower or the same
     * height as the cell at (rowIndex,colIndex). Some DEMs will have few cells
     * in distance with the same value. 9 basic metrics: metrics1[0] = no data
     * count; metrics1[1] = flatness; metrics1[2] = roughness; metrics1[3] =
     * slopyness; metrics1[4] = levelness; metrics1[5] = totalDownness;
     * metrics1[6] = averageDownness; metrics1[7] = totalUpness; metrics1[8] =
     * averageUpness; 6 metrics with all cells higher or same: metrics1[9] =
     * maxd_hhhh [ sum of distance weighted maximum height differences ];
     * metrics1[10] = mind_hhhh [ sum of distance weighted minimum height
     * differences ]; metrics1[11] = sumd_hhhh [ sum of distance weighted height
     * differences ]; metrics1[12] = aved_hhhh [ sum of distance weighted
     * average height difference ]; metrics1[13] = count_hhhh [ count ]; 11
     * metrics with one cell lower or same: metrics1[14] = w_hhhh [ sum of
     * distance weights ]; metrics1[15] = mind_hxhx_ai_hhhl [ sum of distance
     * weighted ( minimum difference of cells adjacent to lower cell ) ];
     * metrics1[16] = maxd_hxhx_ai_hhhl [ sum of distance weighted ( maximum
     * difference of cells adjacent to lower cell ) ]; metrics1[17] =
     * sumd_hxhx_ai_hhhl [ sum of distance weighted ( sum of differences of
     * cells adjacent to lower cell ) ]; metrics1[18] = d_xhxx_ai_hhhl [ sum of
     * distance weighted ( difference of cell opposite lower cell ) ];
     * metrics1[19] = d_xxxl_ai_hhhl [ sum of distance weighted ( difference of
     * lower cell ) ]; metrics1[20] = sumd_xhxl_ai_hhhl [ sum of distance
     * weighted ( sum of differences of lower cell and cell opposite ) ];
     * metrics1[21] = mind_abs_xhxl_ai_hhhl [ sum of distance weighted ( minimum
     * difference magnitude of lower cell and cell opposite ) ]; metrics1[22] =
     * maxd_abs_xhxl_ai_hhhl [ sum of distance weighted ( maximum difference
     * magnitude of lower cell and cell opposite ) ]; metrics1[23] =
     * sumd_abs_xhxl_ai_hhhl [ sum of distance weighted ( sum of difference
     * magnitudes of lower cell and cell opposite ) ]; metrics1[24] = count_hhhl
     * [ count ]; 22 metrics with two cells lower: (N.B. Could have more metrics
     * e.g. minimum magnitude of minimum of higher and maximum of lower cells.)
     * Metrics with opposite cells lower/higher: metrics1[25] = w_hhhl [ sum of
     * distance weights ]; metrics1[26] = mind_hxhx_ai_hlhl [ sum of distance
     * weighted ( minimum difference of higher cells ) ]; metrics1[27] =
     * maxd_hxhx_ai_hlhl [ sum of distance weighted ( maximum difference of
     * higher cells ) ]; metrics1[28] = sumd_hxhx_ai_hlhl [ sum of distance
     * weighted ( sum differences of higher cells ) ]; metrics1[29] =
     * mind_xlxl_ai_hlhl [ sum of distance weighted ( minimum difference of
     * lower cells ) ]; metrics1[30] = maxd_xlxl_ai_hlhl [ sum of distance
     * weighted ( maximum difference of lower cells ) ]; metrics1[31] =
     * sumd_xlxl_ai_hlhl [ sum of distance weighted ( sum of differences of
     * lower cells ) ]; metrics1[32] = mind_abs_hlhl [ sum of distance weighted
     * ( minimum difference magnitude of cells ) ]; metrics1[33] = maxd_abs_hlhl
     * [ sum of distance weighted ( maximum difference magnitude of cells ) ];
     * metrics1[34] = sumd_abs_hlhl [ sum of distance weighted ( sum of
     * difference magnitudes of cells ) ]; metrics1[35] = count_hlhl [ count ];
     * metrics1[36] = w_hlhl [ sum of distance weights ]; Metrics with adjacent
     * cells lower/higher: metrics1[37] = mind_hhxx_ai_hhll [ sum of distance
     * weighted ( minimum difference of higher cells ) ]; metrics1[38] =
     * maxd_hhxx_ai_hhll [ sum of distance weighted ( maximum difference of
     * higher cells ) ]; metrics1[39] = sumd_hhxx_ai_hhll [ sum of distance
     * weighted ( sum of differences of higher cells ) ]; metrics1[40] =
     * mind_xxll_ai_hhll [ sum of distance weighted ( minimum difference of
     * lower cells ) ]; metrics1[41] = maxd_xxll_ai_hhll [ sum of distance
     * weighted ( maximum difference of lower cells ) ]; metrics1[42] =
     * sumd_xxll_ai_hhll [ sum of distance weighted ( sum of differences of
     * lower cells ) ]; metrics1[43] = mind_abs_hhll [ sum of distance weighted
     * ( minimum difference magnitude of cells ) ]; metrics1[44] = maxd_abs_hhll
     * [ sum of distance weighted ( maximum difference magnitude of cells ) ];
     * metrics1[45] = sumd_abs_hhll [ sum of distance weighted ( sum of
     * difference magnitudes of cells ) ]; metrics1[46] = count_hhll [ count ];
     * metrics1[47] = w_hhll [ sum of distance weights ]; 11 metrics with one
     * cell higher: metrics1[48] = mind_lxlx_ai_lllh [ sum of distance weighted
     * ( minimum difference of cells adjacent to higher cell ) ]; metrics1[49] =
     * maxd_lxlx_ai_lllh [ sum of distance weighted ( maximum difference of
     * cells adjacent to higher cell ) ]; metrics1[50] = sumd_lxlx_ai_lllh [ sum
     * of distance weighted ( sum of differences of cells adjacent to higher
     * cell ) ]; metrics1[51] = d_xlxx_ai_lllh [ sum of distance weighted (
     * difference of cell opposite higher cell ) ]; metrics1[52] =
     * d_xxxh_ai_lllh [ sum of distance weighted ( difference of higher cell )
     * ]; metrics1[53] = sumd_xlxh_ai_lllh [ sum of distance weighted ( sum of
     * differences of higher cell and cell opposite ) ]; metrics1[54] =
     * mind_abs_xlxh_ai_lllh [ sum of distance weighted ( minimum difference
     * magnitude of higher cell and cell opposite ) ]; metrics1[55] =
     * maxd_abs_xlxh_ai_lllh [ sum of distance weighted ( maximum difference
     * magnitude of higher cell and cell opposite ) ]; metrics1[56] =
     * sumd_abs_xlxh_ai_lllh [ sum of distance weighted ( sum of difference
     * magnitudes of higher cell and cell opposite ) ]; metrics1[57] =
     * count_lllh [ count ]; metrics1[58] = w_lllh [ sum of distance weights ];
     * 6 metrics with all cells higher: metrics1[59] = maxd_llll [ sum of
     * distance weighted maximum height differences ]; metrics1[60] = mind_llll
     * [ sum of distance weighted minimum height differences ]; metrics1[61] =
     * sumd_llll [ sum of distance weighted height differences ]; metrics1[62] =
     * aved_llll [ sum of distance weighted average height difference ];
     * metrics1[63] = count_llll [ count ]; metrics1[64] = w_llll [ sum of
     * distance weights ];
     *
     * @param grid the Grids_GridDouble being processed
     * @param row the row index of the cell being classified
     * @param col the column index of the cell being classified
     * @param distance the distance within which metrics1 will be calculated
     * @param weights an array of kernel weights for weighting metrics1
     * @param chunkID This is a ID for those AbstractGrid2DSquareCells not to be
     * swapped if possible when an OutOfMemoryError is encountered.
     */
    private void metrics1Calculate_All(
            Grids_GridDouble g,
            double cellsize,
            long row,
            long col,
            double cellX,
            double cellY,
            double distance,
            int cellDistance,
            double[][] weights,
            double[] metrics1,
            double[] heights,
            double[] diff,
            double[] dummyDiff) {
        for (int i = 0; i < metrics1.length; i++) {
            metrics1[i] = 0.0d;
        }
        double x;
        double y;
        double weight;
        double upCount;
        double downCount;
        double upness;
        double downness;
        double averageDiff;
        double averageHeight;
        double noDataCount;
        double xDiff;
        double yDiff;
        double sumWeight;
        int p;
        int q;
        double noDataValue = g.getNoDataValue(ge.HandleOutOfMemoryErrorTrue);
        double cellHeight = g.getCell(row, col, ge.HandleOutOfMemoryErrorTrue);
        for (p = 0; p <= cellDistance; p++) {
            y = g.getCellYDouble(row + p, ge.HandleOutOfMemoryErrorTrue);
            yDiff = y - cellY;
            for (q = 1; q <= cellDistance; q++) {
                //if ( p == 2 && q == 2 ) { int debug2 = 0; }
                noDataCount = 0.0d;
                x = g.getCellXDouble(col + q, ge.HandleOutOfMemoryErrorTrue);
                weight = weights[p][q];
                if (weight > 0) {
                    xDiff = x - cellX;
                    heights[0] = g.getCell(x, y, ge.HandleOutOfMemoryErrorTrue);
                    if (heights[0] == noDataValue) {
                        heights[0] = cellHeight;
                        noDataCount += 1.0d;
                    }
                    heights[1] = g.getCell(cellX + yDiff, cellY - xDiff, ge.HandleOutOfMemoryErrorTrue);
                    if (heights[1] == noDataValue) {
                        heights[1] = cellHeight;
                        noDataCount += 1.0d;
                    }
                    heights[2] = g.getCell(cellX - xDiff, cellY - yDiff, ge.HandleOutOfMemoryErrorTrue);
                    if (heights[2] == noDataValue) {
                        heights[2] = cellHeight;
                        noDataCount += 1.0d;
                    }
                    heights[3] = g.getCell(cellX - yDiff, cellY + xDiff, ge.HandleOutOfMemoryErrorTrue);
                    if (heights[3] == noDataValue) {
                        heights[3] = cellHeight;
                        noDataCount += 1.0d;
                    }
                    metrics1[0] += noDataCount;
                    if (noDataCount < 4.0d) {
                        // height[1]   height[0]
                        //      cellHeight
                        // height[2]   height[3]

                        // Calculate basic metrics
                        averageHeight = 0.0d;
                        averageDiff = 0.0d;
                        downCount = 0.0d;
                        upCount = 0.0d;
                        upness = 0.0d;
                        downness = 0.0d;
                        for (int r = 0; r < 4; r++) {
                            averageHeight += heights[r];
                            diff[r] = heights[r] - cellHeight;
                            averageDiff += diff[r];
                            if (diff[r] > 0.0d) {
                                downness += diff[r];
                                downCount += 1.0d;
                            } else {
                                if (diff[r] < 0.0d) {
                                    upness += diff[r];
                                    upCount += 1.0d;

                                } else {
                                    metrics1[1] += weight; // flatness
                                }
                            }
                            metrics1[2] += weight * Math.abs(diff[r]); // roughness
                        }
                        averageHeight /= (4.0d - noDataCount);
                        averageDiff /= (4.0d - noDataCount);
                        metrics1[5] += weight * downness; // totalDownness
                        if (downCount > 0.0d) {
                            metrics1[6] += metrics1[5] / downCount; // averageDownness
                        }
                        metrics1[7] += weight * upness; // totalUpness
                        if (upCount > 0.0d) {
                            metrics1[8] += metrics1[7] / upCount; // averageUpness
                        }
                        // Slopyness and levelness similar to slope in getSlopeAspect
                        // slopyness
                        metrics1[3] += weight * Math.sqrt(
                                ((diff[0] - diff[2]) * (diff[0] - diff[2]))
                                + ((diff[1] - diff[3]) * (diff[1] - diff[3])));
                        //levelness
                        metrics1[4] += weight * averageDiff;
                        //levelness += weight * Math.abs( averageHeight - cellsize );
                        // diff[1]   diff[0]
                        //    cellHeight
                        // diff[2]   diff[3]
                        metrics1Calculate_Complex(
                                metrics1,
                                diff,
                                dummyDiff,
                                weight,
                                averageDiff);
                    }
                }
            }
        }
    }

    /**
     * Returns a double[] metrics1 of the cells in grid upto distance from a
     * cell given by rowIndex and colIndex. The elements of metrics1 do not
     * explicitly take into account any axis such as that which can be defined
     * from a metric of slope (general slope direction). Distance weighting is
     * done via a kernel precalculated as weights. Some elements of metrics1 are
     * weighted based on the difference in value (height) of the cell at
     * (rowIndex,colIndex) and other cell values within distance. Within
     * distance equidistant cells in 4 orthoganol directions are accounted for
     * in the metrics1. NB. Every cell is either higher, lower or the same
     * height as the cell at (rowIndex,colIndex). Some DEMs will have few cells
     * in distance with the same value. 9 basic metrics: metrics1[0] = no data
     * count; metrics1[1] = flatness; metrics1[2] = roughness; metrics1[3] =
     * slopyness; metrics1[4] = levelness; metrics1[5] = totalDownness;
     * metrics1[6] = averageDownness; metrics1[7] = totalUpness; metrics1[8] =
     * averageUpness; 6 metrics with all cells higher or same: metrics1[9] =
     * maxd_hhhh [ sum of distance weighted maximum height differences ];
     * metrics1[10] = mind_hhhh [ sum of distance weighted minimum height
     * differences ]; metrics1[11] = sumd_hhhh [ sum of distance weighted height
     * differences ]; metrics1[12] = aved_hhhh [ sum of distance weighted
     * average height difference ]; metrics1[13] = count_hhhh [ count ]; 11
     * metrics with one cell lower or same: metrics1[14] = w_hhhh [ sum of
     * distance weights ]; metrics1[15] = mind_hxhx_ai_hhhl [ sum of distance
     * weighted ( minimum difference of cells adjacent to lower cell ) ];
     * metrics1[16] = maxd_hxhx_ai_hhhl [ sum of distance weighted ( maximum
     * difference of cells adjacent to lower cell ) ]; metrics1[17] =
     * sumd_hxhx_ai_hhhl [ sum of distance weighted ( sum of differences of
     * cells adjacent to lower cell ) ]; metrics1[18] = d_xhxx_ai_hhhl [ sum of
     * distance weighted ( difference of cell opposite lower cell ) ];
     * metrics1[19] = d_xxxl_ai_hhhl [ sum of distance weighted ( difference of
     * lower cell ) ]; metrics1[20] = sumd_xhxl_ai_hhhl [ sum of distance
     * weighted ( sum of differences of lower cell and cell opposite ) ];
     * metrics1[21] = mind_abs_xhxl_ai_hhhl [ sum of distance weighted ( minimum
     * difference magnitude of lower cell and cell opposite ) ]; metrics1[22] =
     * maxd_abs_xhxl_ai_hhhl [ sum of distance weighted ( maximum difference
     * magnitude of lower cell and cell opposite ) ]; metrics1[23] =
     * sumd_abs_xhxl_ai_hhhl [ sum of distance weighted ( sum of difference
     * magnitudes of lower cell and cell opposite ) ]; metrics1[24] = count_hhhl
     * [ count ]; 22 metrics with two cells lower: (N.B. Could have more metrics
     * e.g. minimum magnitude of minimum of higher and maximum of lower cells.)
     * Metrics with opposite cells lower/higher: metrics1[25] = w_hhhl [ sum of
     * distance weights ]; metrics1[26] = mind_hxhx_ai_hlhl [ sum of distance
     * weighted ( minimum difference of higher cells ) ]; metrics1[27] =
     * maxd_hxhx_ai_hlhl [ sum of distance weighted ( maximum difference of
     * higher cells ) ]; metrics1[28] = sumd_hxhx_ai_hlhl [ sum of distance
     * weighted ( sum differences of higher cells ) ]; metrics1[29] =
     * mind_xlxl_ai_hlhl [ sum of distance weighted ( minimum difference of
     * lower cells ) ]; metrics1[30] = maxd_xlxl_ai_hlhl [ sum of distance
     * weighted ( maximum difference of lower cells ) ]; metrics1[31] =
     * sumd_xlxl_ai_hlhl [ sum of distance weighted ( sum of differences of
     * lower cells ) ]; metrics1[32] = mind_abs_hlhl [ sum of distance weighted
     * ( minimum difference magnitude of cells ) ]; metrics1[33] = maxd_abs_hlhl
     * [ sum of distance weighted ( maximum difference magnitude of cells ) ];
     * metrics1[34] = sumd_abs_hlhl [ sum of distance weighted ( sum of
     * difference magnitudes of cells ) ]; metrics1[35] = count_hlhl [ count ];
     * metrics1[36] = w_hlhl [ sum of distance weights ]; Metrics with adjacent
     * cells lower/higher: metrics1[37] = mind_hhxx_ai_hhll [ sum of distance
     * weighted ( minimum difference of higher cells ) ]; metrics1[38] =
     * maxd_hhxx_ai_hhll [ sum of distance weighted ( maximum difference of
     * higher cells ) ]; metrics1[39] = sumd_hhxx_ai_hhll [ sum of distance
     * weighted ( sum of differences of higher cells ) ]; metrics1[40] =
     * mind_xxll_ai_hhll [ sum of distance weighted ( minimum difference of
     * lower cells ) ]; metrics1[41] = maxd_xxll_ai_hhll [ sum of distance
     * weighted ( maximum difference of lower cells ) ]; metrics1[42] =
     * sumd_xxll_ai_hhll [ sum of distance weighted ( sum of differences of
     * lower cells ) ]; metrics1[43] = mind_abs_hhll [ sum of distance weighted
     * ( minimum difference magnitude of cells ) ]; metrics1[44] = maxd_abs_hhll
     * [ sum of distance weighted ( maximum difference magnitude of cells ) ];
     * metrics1[45] = sumd_abs_hhll [ sum of distance weighted ( sum of
     * difference magnitudes of cells ) ]; metrics1[46] = count_hhll [ count ];
     * metrics1[47] = w_hhll [ sum of distance weights ]; 11 metrics with one
     * cell higher: metrics1[48] = mind_lxlx_ai_lllh [ sum of distance weighted
     * ( minimum difference of cells adjacent to higher cell ) ]; metrics1[49] =
     * maxd_lxlx_ai_lllh [ sum of distance weighted ( maximum difference of
     * cells adjacent to higher cell ) ]; metrics1[50] = sumd_lxlx_ai_lllh [ sum
     * of distance weighted ( sum of differences of cells adjacent to higher
     * cell ) ]; metrics1[51] = d_xlxx_ai_lllh [ sum of distance weighted (
     * difference of cell opposite higher cell ) ]; metrics1[52] =
     * d_xxxh_ai_lllh [ sum of distance weighted ( difference of higher cell )
     * ]; metrics1[53] = sumd_xlxh_ai_lllh [ sum of distance weighted ( sum of
     * differences of higher cell and cell opposite ) ]; metrics1[54] =
     * mind_abs_xlxh_ai_lllh [ sum of distance weighted ( minimum difference
     * magnitude of higher cell and cell opposite ) ]; metrics1[55] =
     * maxd_abs_xlxh_ai_lllh [ sum of distance weighted ( maximum difference
     * magnitude of higher cell and cell opposite ) ]; metrics1[56] =
     * sumd_abs_xlxh_ai_lllh [ sum of distance weighted ( sum of difference
     * magnitudes of higher cell and cell opposite ) ]; metrics1[57] =
     * count_lllh [ count ]; metrics1[58] = w_lllh [ sum of distance weights ];
     * 6 metrics with all cells higher: metrics1[59] = maxd_llll [ sum of
     * distance weighted maximum height differences ]; metrics1[60] = mind_llll
     * [ sum of distance weighted minimum height differences ]; metrics1[61] =
     * sumd_llll [ sum of distance weighted height differences ]; metrics1[62] =
     * aved_llll [ sum of distance weighted average height difference ];
     * metrics1[63] = count_llll [ count ]; metrics1[64] = w_llll [ sum of
     * distance weights ];
     *
     * @param grid the Grids_GridDouble being processed
     * @param row the row index of the cell being classified
     * @param col the column index of the cell being classified
     * @param distance the distance within which metrics1 will be calculated
     * @param weights an array of kernel weights for weighting metrics1
     * @param chunkID This is a ID for those AbstractGrid2DSquareCells not to be
     * swapped if possible when an OutOfMemoryError is encountered.
     */
    private void metrics1Calculate_All(
            Grids_GridInt g,
            double cellsize,
            long row,
            long col,
            double cellX,
            double cellY,
            double distance,
            int cellDistance,
            double[][] weights,
            double[] metrics1,
            double[] heights,
            double[] diff,
            double[] dummyDiff) {
        for (int i = 0; i < metrics1.length; i++) {
            metrics1[i] = 0.0d;
        }
        double weight;
        double upCount;
        double downCount;
        double upness;
        double downness;
        double averageDiff;
//        double averageHeight;
        double noDataCount;
        int p;
        int q;
        int pPlusCellDistance;
        int qPlusCellDistance;
        long r;
        long c;
        int noDataValue = g.getNoDataValue(ge.HandleOutOfMemoryErrorFalse);
        double cellHeight = g.getCell(row, col, ge.HandleOutOfMemoryErrorFalse);
        for (p = -cellDistance; p <= cellDistance; p++) {
            r = row + p;
            pPlusCellDistance = p + cellDistance;
            for (q = -cellDistance; q <= cellDistance; q++) {
                c = col + q;
                qPlusCellDistance = q + cellDistance;
                noDataCount = 0.0d;
                weight = weights[pPlusCellDistance][qPlusCellDistance];
                if (weight > 0) {
                    heights[0] = g.getCell(r, c, ge.HandleOutOfMemoryErrorFalse);
                    if (heights[0] == noDataValue) {
                        heights[0] = cellHeight;
                        noDataCount += 1.0d;
                    }
                    heights[1] = g.getCell(r, c, ge.HandleOutOfMemoryErrorFalse);
                    if (heights[1] == noDataValue) {
                        heights[1] = cellHeight;
                        noDataCount += 1.0d;
                    }
                    heights[2] = g.getCell(r, c, ge.HandleOutOfMemoryErrorFalse);
                    if (heights[2] == noDataValue) {
                        heights[2] = cellHeight;
                        noDataCount += 1.0d;
                    }
                    heights[3] = g.getCell(r, c, ge.HandleOutOfMemoryErrorFalse);
                    if (heights[3] == noDataValue) {
                        heights[3] = cellHeight;
                        noDataCount += 1.0d;
                    }
                    metrics1[0] += noDataCount;
                    if (noDataCount < 4.0d) {
                        // height[1]   height[0]
                        //      cellHeight
                        // height[2]   height[3]

                        // Calculate basic metrics
//                    averageHeight = 0.0d;
                        averageDiff = 0.0d;
                        downCount = 0.0d;
                        upCount = 0.0d;
                        upness = 0.0d;
                        downness = 0.0d;
                        for (int n = 0; n < 4; n++) {
//                        averageHeight += heights[n];
                            diff[n] = heights[n] - cellHeight;
                            averageDiff += diff[n];
                            if (diff[n] > 0.0d) {
                                downness += diff[n];
                                downCount += 1.0d;
                            } else {
                                if (diff[n] < 0.0d) {
                                    upness += diff[n];
                                    upCount += 1.0d;

                                } else {
                                    metrics1[1] += weight; // flatness
                                }
                            }
                            metrics1[2] += weight * Math.abs(diff[n]); // roughness
                        }
//                    averageHeight /= (4.0d - noDataCount);
                        averageDiff /= (4.0d - noDataCount);
                        metrics1[5] += weight * downness; // totalDownness
                        if (downCount > 0.0d) {
                            metrics1[6] += metrics1[5] / downCount; // averageDownness
                        }
                        metrics1[7] += weight * upness; // totalUpness
                        if (upCount > 0.0d) {
                            metrics1[8] += metrics1[7] / upCount; // averageUpness
                        }
                        // Slopyness and levelness similar to slope in getSlopeAspect
                        // slopyness
                        metrics1[3] += weight * Math.sqrt(
                                ((diff[0] - diff[2]) * (diff[0] - diff[2]))
                                + ((diff[1] - diff[3]) * (diff[1] - diff[3])));
                        //levelness
                        metrics1[4] += weight * averageDiff;
                        //levelness += weight * Math.abs( averageHeight - cellsize );
                        // diff[1]   diff[0]
                        //    cellHeight
                        // diff[2]   diff[3]
                        metrics1Calculate_Complex(
                                metrics1,
                                diff,
                                dummyDiff,
                                weight,
                                averageDiff);
                    }
                }
            }
        }
    }

    private void metrics1Calculate_Complex(
            double[] metrics1,
            double[] diff,
            double[] dummyDiff,
            double weight,
            double averageDiff) {
        int caseSwitch = metrics1Calculate_CaseSwitch(diff);
        // 81 cases
        // Each orthoganal equidistant cell is either heigher, lower, or
        // the same height as the cell at centre.

        switch (caseSwitch) {
            case 0:
                // hhhh
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);

                break;

            case 1:
                // hhhl
                metrics1Calculate_hhhl(metrics1, diff, weight);

                break;

            case 2:
                // hhhs
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                metrics1Calculate_hhhl(
                        metrics1, diff, weight);
                //count_hhhs += 1.0d;
                //w_hhhs += weight;

                break;

            case 3:
                // hhlh
                // Shuffle diff once for hhhl
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);

                break;

            case 4:
                // hhll
                metrics1Calculate_hhll(metrics1, diff, weight);

                break;

            case 5:
                // hhls
                // Shuffle diff once for hhhl
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, diff, weight);
                //count_hhsl += 1.0d;
                //w_hhsl += weight;

                break;

            case 6:
                // hhsh
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                // Shuffle diff once for hhhl
                metrics1Shuffle1(
                        dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                //count_hhhs += 1.0d;
                //w_hhhs += weight;

                break;

            case 7:
                // hhsl
                metrics1Calculate_hhhl(metrics1, diff, weight);
                metrics1Calculate_hhll(
                        metrics1, diff, weight);
                //count_hhsl += 1.0d;
                //w_hhsl += weight;

                break;

            case 8:
                // hhss
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                metrics1Calculate_hhhl(
                        metrics1, diff, weight);
                metrics1Calculate_hhll(
                        metrics1, diff, weight);
                //count_hhss += 1.0d;
                //w_hhss += weight;

                break;

            case 9:
                // hlhh
                // Shuffle diff twice for hhhl
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);

                break;

            case 10:
                // metrics1Calculate_hlhl
                metrics1Calculate_hlhl(metrics1, diff, weight);

                break;

            case 11:
                // hlhs
                // Shuffle diff twice for hhhl
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlhl(
                        metrics1, diff, weight);
                //count_hshl += 1.0d;
                //w_hshl += weight;

                break;

            case 12:
                // hllh
                // Shuffle diff once for hhll
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);

                break;

            case 13:
                // hlll
                metrics1Calculate_hlll(metrics1, diff, weight);

                break;

            case 14:
                // hlls
                // Shuffle diff once for hhll
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, diff, weight);
                //count_hsll += 1.0d;
                //w_hsll += weight;

                break;

            case 15:
                // hlsh
                // Shuffle diff twice for hhll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                // Shuffle diff once for hhll
                metrics1Shuffle1(
                        dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                //count_hhsl += 1.0d;
                //w_hhsl += weight;

                break;

            case 16:
                // hlsl
                metrics1Calculate_hlhl(metrics1, diff, weight);
                metrics1Calculate_hlll(
                        metrics1, diff, weight);
                //count_hlsl += 1.0d;
                //w_hlsl += weight;

                break;

            case 17:
                // hlss
                // Shuffle diff twice for hhhl
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlhl(
                        metrics1, diff, weight);
                // Shuffle diff once for hhll
                metrics1Shuffle1(
                        dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, diff, weight);

                break;

            case 18:
                // hshh
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                // Shuffle diff twice for hhhl
                metrics1Shuffle2(
                        dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);

                break;

            case 19:
                // hshl
                metrics1Calculate_hhhl(metrics1, diff, weight);
                metrics1Calculate_hlhl(
                        metrics1, diff, weight);

                break;

            case 20:
                // hshs
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                metrics1Calculate_hhhl(
                        metrics1, diff, weight);
                metrics1Calculate_hlhl(
                        metrics1, diff, weight);

                break;

            case 21:
                // hslh
                // Shuffle diff once for hhhl and hhll
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);

                break;

            case 22:
                // hsll
                metrics1Calculate_hhll(metrics1, diff, weight);
                metrics1Calculate_hlll(
                        metrics1, diff, weight);

                break;

            case 23:
                // hsls
                // Shuffle diff once for hhhl
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, diff, weight);
                metrics1Calculate_hlll(
                        metrics1, diff, weight);

                break;

            case 24:
                // hssh
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                // Shuffle diff once for hhhl and hhll
                metrics1Shuffle1(
                        dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);

                break;

            case 25:
                // hssl
                metrics1Calculate_hhhl(metrics1, diff, weight);
                metrics1Calculate_hhll(
                        metrics1, diff, weight);
                metrics1Calculate_hlhl(
                        metrics1, diff, weight);
                metrics1Calculate_hlll(
                        metrics1, diff, weight);

                break;

            case 26:
                // metrics1Calculate_hsss
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                metrics1Calculate_hhhl(
                        metrics1, diff, weight);
                metrics1Calculate_hhll(
                        metrics1, diff, weight);
                metrics1Calculate_hlhl(
                        metrics1, diff, weight);
                metrics1Calculate_hlll(
                        metrics1, diff, weight);

                break;

            case 27:
                // lhhh
                // Shuffle diff thrice for hhhl
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);

                break;

            case 28:
                // lhhl
                // Shuffle diff thrice for hhll
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);

                break;

            case 29:
                // lhhs
                // Shuffle diff thrice for hhhl and hhll
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);

                break;

            case 30:
                // lhlh
                // Shuffle once for hlhl
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);

                break;

            case 31:
                // lhll
                // Shuffle diff thrice for hlll
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 32:
                // lhls
                // Shuffle diff once for hlhl
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);
                // Shuffle diff thrice for hlll
                metrics1Shuffle3(
                        dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 33:
                // lhsh
                // Shuffle diff thrice for hhhl
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                // Shuffle diff once for hlhl
                metrics1Shuffle1(
                        dummyDiff, diff);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);

                break;

            case 34:
                // lhsl
                // Shuffle diff thrice for hhll and hlll
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 35:
                // lhss
                // Shuffle diff thrice for hhhl, hhll, hlhl, hlll
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);

                break;

            case 36:
                // llhh
                // Shuffle diff twice for hhll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);

                break;

            case 37:
                // llhl
                // Shuffle diff twice for hlll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 38:
                // llhs
                // Shuffle diff twice for hhll and hlll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 39:
                // lllh
                // Shuffle diff once for hlll
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 40:
                // llll
                metrics1Calculate_llll(metrics1, diff, weight, averageDiff);

                break;

            case 41:
                // llls
                // Shuffle diff once for hlll
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

                break;

            case 42:
                // llsh
                // Shuffle diff twice for hhll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                // Shuffle diff once for hlll
                metrics1Shuffle1(
                        dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 43:
                // llsl
                // Shuffle diff twice for hlll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

                break;

            case 44:
                // llss
                // Shuffle diff twice for hhll hlll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

                break;

            case 45:
                // lshh
                // Shuffle diff thrice for hhhl
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                // Shuffle diff twice for hhll
                metrics1Shuffle2(
                        dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);

                break;

            case 46:
                // lshl
                // Shuffle diff thrice for hhll
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                // Shuffle diff twice for hlll
                metrics1Shuffle2(
                        dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 47:
                // lshs
                // Shuffle diff thrice for hhhl
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                // Shuffle diff twice for hhll hlll
                metrics1Shuffle2(
                        dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 48:
                // lslh
                // Shuffle diff once for hlhl and hlll
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 49:
                // lsll
                // Shuffle diff thrice for hlll
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

                break;

            case 50:
                // lsls
                // Shuffle diff once for hlhl hlll
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

                break;

            case 51:
                // lssh
                // Shuffle diff thrice for hhhl
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                // Shuffle diff twice for hhll
                metrics1Shuffle2(
                        dummyDiff, diff);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);
                // Shuffle diff once for hlll
                metrics1Shuffle1(
                        dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 52:
                // lssl
                // Shuffle diff thrice for hhll hlll
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

                break;

            case 53:
                // lsss
                // Shuffle diff thrice for hhhl hhll hlll
                metrics1Shuffle3(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

                break;

            case 54:
                // shhh
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                // Shuffle diff thrice for hhhl
                metrics1Shuffle3(
                        dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);

                break;

            case 55:
                // shhl
                metrics1Calculate_hhhl(metrics1, diff, weight);
                // Shuffle diff thrice for hhll
                metrics1Shuffle3(
                        dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);

                break;

            case 56:
                // shhs
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                metrics1Calculate_hhhl(
                        metrics1, diff, weight);
                // Shuffle diff twice for hhll
                metrics1Shuffle2(
                        dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);

                break;

            case 57:
                // shlh
                // Shuffle diff once for hhhl hlhl
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);

                break;

            case 58:
                // shll
                metrics1Calculate_hhll(metrics1, diff, weight);
                // Shuffle diff thrice for hlll
                metrics1Shuffle3(
                        dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 59:
                // shls
                // Shuffle diff once for hhhl
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, diff, weight);
                // Shuffle diff thrice for hlll
                metrics1Shuffle3(
                        dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 60:
                // shsh
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                // Shuffle diff once for hhhl hlhl
                metrics1Shuffle1(
                        dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);

                break;

            case 61:
                // shsl
                metrics1Calculate_hhhl(metrics1, diff, weight);
                metrics1Calculate_hhll(
                        metrics1, diff, weight);
                // Shuffle diff thrice for hlll
                metrics1Shuffle3(
                        dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 62:
                // shss
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                // Shuffle diff thrice for hhhl hhll hlhl hlll
                metrics1Shuffle3(
                        dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 63:
                // slhh
                // Shuffle diff twice for hhhl hhll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);

                break;

            case 64:
                // slhl
                metrics1Calculate_hlhl(metrics1, diff, weight);
                // Shuffle diff twice for hlll
                metrics1Shuffle2(
                        dummyDiff, diff);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 65:
                // slhs
                // Shuffle diff twice for hhhl hhll hlhl hlll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 66:
                // sllh
                // Shuffle diff twice for hhll hlll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 67:
                // slll
                metrics1Calculate_hlll(metrics1, diff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

                break;

            case 68:
                // slls
                // Shuffle diff once for hhll
                metrics1Shuffle1(dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, diff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

                break;

            case 69:
                // slsh
                // Shuffle diff twice for hhhl hhll hlll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 70:
                // slsl
                metrics1Calculate_hlhl(metrics1, diff, weight);
                metrics1Calculate_hlll(
                        metrics1, diff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

                break;

            case 71:
                // slss
                // Shuffle diff twice for hhhl hhll hlll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

                break;

            case 72:
                // sshh
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                // Shuffle diff twice for hhhl hhll
                metrics1Shuffle2(
                        dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);

                break;

            case 73:
                // sshl
                metrics1Calculate_hhhl(metrics1, dummyDiff, weight);
                // Shuffle diff thrice for hhll
                metrics1Shuffle3(
                        dummyDiff, diff);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                // Shuffle diff twice for hlhl hlll
                metrics1Shuffle2(
                        dummyDiff, diff);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 74:
                // sshs
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                // Shuffle diff twice for hhhl hhll hlhl hlll
                metrics1Shuffle2(
                        dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 75:
                // sslh
                // Shuffle diff once for hhhl hhll hlhl hlll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 76:
                // ssll
                metrics1Calculate_hhll(metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

                break;

            case 77:
                // ssls
                // Shuffle diff once for hhhl hhll hlhl hlll
                metrics1Shuffle2(dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

                break;

            case 78:
                // sssh
                metrics1Calculate_hhhh(metrics1, diff, weight, averageDiff);
                // Shuffle diff once for hhhl hhll hlhl hlll
                metrics1Shuffle2(
                        dummyDiff, diff);
                metrics1Calculate_hhhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);

                break;

            case 79:
                // sssl
                metrics1Calculate_hhhl(metrics1, dummyDiff, weight);
                metrics1Calculate_hhll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlhl(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_hlll(
                        metrics1, dummyDiff, weight);
                metrics1Calculate_llll(
                        metrics1, diff, weight, averageDiff);

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
     * @param diff the array of height differences
     */
    private int metrics1Calculate_CaseSwitch(
            double[] diff) {
        if (diff[0] > 0.0d) {
            if (diff[1] > 0.0d) {
                if (diff[2] > 0.0d) {
                    if (diff[3] > 0.0d) {
                        return 0; // metrics1Calculate_hhhh

                    } else {
                        if (diff[3] < 0.0d) {
                            return 1; // metrics1Calculate_hhhl

                        } else {
                            return 2; // metrics1Calculate_hhhs

                        }
                    }
                } else {
                    if (diff[2] < 0.0d) {
                        if (diff[3] > 0.0d) {
                            return 3; // metrics1Calculate_hhlh

                        } else {
                            if (diff[3] < 0.0d) {
                                return 4; // metrics1Calculate_hhll

                            } else {
                                return 5; // metrics1Calculate_hhls

                            }
                        }
                    } else {
                        if (diff[3] > 0.0d) {
                            return 6; // metrics1Calculate_hhsh

                        } else {
                            if (diff[3] < 0.0d) {
                                return 7; // metrics1Calculate_hhsl

                            } else {
                                return 8; // metrics1Calculate_hhss

                            }
                        }
                    }
                }
            } else {
                if (diff[1] < 0.0d) {
                    if (diff[2] > 0.0d) {
                        if (diff[3] > 0.0d) {
                            return 9; // metrics1Calculate_hlhh

                        } else {
                            if (diff[3] < 0.0d) {
                                return 10; // metrics1Calculate_hlhl

                            } else {
                                return 11; // metrics1Calculate_hlhs

                            }
                        }
                    } else {
                        if (diff[2] < 0.0d) {
                            if (diff[3] > 0.0d) {
                                return 12; // metrics1Calculate_hllh

                            } else {
                                if (diff[3] < 0.0d) {
                                    return 13; // metrics1Calculate_hlll

                                } else {
                                    return 14; // metrics1Calculate_hlls

                                }
                            }
                        } else {
                            if (diff[3] > 0.0d) {
                                return 15; // metrics1Calculate_hlsh

                            } else {
                                if (diff[3] < 0.0d) {
                                    return 16; // metrics1Calculate_hlsl

                                } else {
                                    return 17; // metrics1Calculate_hlss

                                }
                            }
                        }
                    }
                } else {
                    if (diff[2] > 0.0d) {
                        if (diff[3] > 0.0d) {
                            return 18; // metrics1Calculate_hshh

                        } else {
                            if (diff[3] < 0.0d) {
                                return 19; // metrics1Calculate_hshl

                            } else {
                                return 20; // metrics1Calculate_hshs

                            }
                        }
                    } else {
                        if (diff[2] < 0.0d) {
                            if (diff[3] > 0.0d) {
                                return 21; // metrics1Calculate_hslh

                            } else {
                                if (diff[3] < 0.0d) {
                                    return 22; // metrics1Calculate_hsll

                                } else {
                                    return 23; // metrics1Calculate_hsls

                                }
                            }
                        } else {
                            if (diff[3] > 0.0d) {
                                return 24; // metrics1Calculate_hssh

                            } else {
                                if (diff[3] < 0.0d) {
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
            if (diff[0] < 0.0d) {
                if (diff[1] > 0.0d) {
                    if (diff[2] > 0.0d) {
                        if (diff[3] > 0.0d) {
                            return 27; // metrics1Calculate_lhhh

                        } else {
                            if (diff[3] < 0.0d) {
                                return 28; // metrics1Calculate_lhhl

                            } else {
                                return 29; // metrics1Calculate_lhhs

                            }
                        }
                    } else {
                        if (diff[2] < 0.0d) {
                            if (diff[3] > 0.0d) {
                                return 30; // metrics1Calculate_lhlh

                            } else {
                                if (diff[3] < 0.0d) {
                                    return 31; // metrics1Calculate_lhll

                                } else {
                                    return 32; // metrics1Calculate_lhls

                                }
                            }
                        } else {
                            if (diff[3] > 0.0d) {
                                return 33; // metrics1Calculate_lhsh

                            } else {
                                if (diff[3] < 0.0d) {
                                    return 34; // metrics1Calculate_lhsl

                                } else {
                                    return 35; // metrics1Calculate_lhss

                                }
                            }
                        }
                    }
                } else {
                    if (diff[1] < 0.0d) {
                        if (diff[2] > 0.0d) {
                            if (diff[3] > 0.0d) {
                                return 36; // metrics1Calculate_llhh

                            } else {
                                if (diff[3] < 0.0d) {
                                    return 37; // metrics1Calculate_llhl

                                } else {
                                    return 38; // metrics1Calculate_llhs

                                }
                            }
                        } else {
                            if (diff[2] < 0.0d) {
                                if (diff[3] > 0.0d) {
                                    return 39; // metrics1Calculate_lllh

                                } else {
                                    if (diff[3] < 0.0d) {
                                        return 40; // metrics1Calculate_llll

                                    } else {
                                        return 41; // metrics1Calculate_llls

                                    }
                                }
                            } else {
                                if (diff[3] > 0.0d) {
                                    return 42; // metrics1Calculate_llsh

                                } else {
                                    if (diff[3] < 0.0d) {
                                        return 43; // metrics1Calculate_llsl

                                    } else {
                                        return 44; // metrics1Calculate_llss

                                    }
                                }
                            }
                        }
                    } else {
                        if (diff[2] > 0.0d) {
                            if (diff[3] > 0.0d) {
                                return 45; // metrics1Calculate_lshh

                            } else {
                                if (diff[3] < 0.0d) {
                                    return 46; // metrics1Calculate_lshl

                                } else {
                                    return 47; // metrics1Calculate_lshs

                                }
                            }
                        } else {
                            if (diff[2] < 0.0d) {
                                if (diff[3] > 0.0d) {
                                    return 48; // metrics1Calculate_lslh

                                } else {
                                    if (diff[3] < 0.0d) {
                                        return 49; // metrics1Calculate_lsll

                                    } else {
                                        return 50; // metrics1Calculate_lsls

                                    }
                                }
                            } else {
                                if (diff[3] > 0.0d) {
                                    return 51; // metrics1Calculate_lssh

                                } else {
                                    if (diff[3] < 0.0d) {
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
                if (diff[1] > 0.0d) {
                    if (diff[2] > 0.0d) {
                        if (diff[3] > 0.0d) {
                            return 54; // metrics1Calculate_shhh

                        } else {
                            if (diff[3] < 0.0d) {
                                return 55; // metrics1Calculate_shhl

                            } else {
                                return 56; // metrics1Calculate_shhs

                            }
                        }
                    } else {
                        if (diff[2] < 0.0d) {
                            if (diff[3] > 0.0d) {
                                return 57; // metrics1Calculate_shlh

                            } else {
                                if (diff[3] < 0.0d) {
                                    return 58; // metrics1Calculate_shll

                                } else {
                                    return 59; // metrics1Calculate_shls

                                }
                            }
                        } else {
                            if (diff[3] > 0.0d) {
                                return 60; // metrics1Calculate_shsh

                            } else {
                                if (diff[3] < 0.0d) {
                                    return 61; // metrics1Calculate_shsl

                                } else {
                                    return 62; // metrics1Calculate_shss

                                }
                            }
                        }
                    }
                } else {
                    if (diff[1] < 0.0d) {
                        if (diff[2] > 0.0d) {
                            if (diff[3] > 0.0d) {
                                return 63; // metrics1Calculate_slhh

                            } else {
                                if (diff[3] < 0.0d) {
                                    return 64; // metrics1Calculate_slhl

                                } else {
                                    return 65; // metrics1Calculate_slhs

                                }
                            }
                        } else {
                            if (diff[2] < 0.0d) {
                                if (diff[3] > 0.0d) {
                                    return 66; // metrics1Calculate_sllh

                                } else {
                                    if (diff[3] < 0.0d) {
                                        return 67; // metrics1Calculate_slll

                                    } else {
                                        return 68; // metrics1Calculate_slls

                                    }
                                }
                            } else {
                                if (diff[3] > 0.0d) {
                                    return 69; // metrics1Calculate_slsh

                                } else {
                                    if (diff[3] < 0.0d) {
                                        return 70; // metrics1Calculate_slsl

                                    } else {
                                        return 71; // metrics1Calculate_slss

                                    }
                                }
                            }
                        }
                    } else {
                        if (diff[2] > 0.0d) {
                            if (diff[3] > 0.0d) {
                                return 72; // metrics1Calculate_sshh

                            } else {
                                if (diff[3] < 0.0d) {
                                    return 73; // metrics1Calculate_sshl

                                } else {
                                    return 74; // metrics1Calculate_sshs

                                }
                            }
                        } else {
                            if (diff[2] < 0.0d) {
                                if (diff[3] > 0.0d) {
                                    return 75; // metrics1Calculate_sslh

                                } else {
                                    if (diff[3] < 0.0d) {
                                        return 76; // metrics1Calculate_ssll

                                    } else {
                                        return 77; // metrics1Calculate_ssls

                                    }
                                }
                            } else {
                                if (diff[3] > 0.0d) {
                                    return 78; // metrics1Calculate_sssh

                                } else {
                                    if (diff[3] < 0.0d) {
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
     * Shuffles dummyDiff such that: dummyDiff[0] = diff[3] dummyDiff[1] =
     * diff[0] dummyDiff[2] = diff[1] dummyDiff[3] = diff[2]
     */
    private void metrics1Shuffle1(double[] dummyDiff, double[] diff) {
        dummyDiff[0] = diff[3];
        dummyDiff[1] = diff[0];
        dummyDiff[2] = diff[1];
        dummyDiff[3] = diff[2];

    }

    /**
     * Shuffles dummyDiff such that: dummyDiff[0] = diff[2] dummyDiff[1] =
     * diff[3] dummyDiff[2] = diff[0] dummyDiff[3] = diff[1]
     */
    private void metrics1Shuffle2(double[] dummyDiff, double[] diff) {
        dummyDiff[0] = diff[2];
        dummyDiff[1] = diff[3];
        dummyDiff[2] = diff[0];
        dummyDiff[3] = diff[1];

    }

    /**
     * Shuffles dummyDiff such that: dummyDiff[0] = diff[2] dummyDiff[1] =
     * diff[3] dummyDiff[2] = diff[0] dummyDiff[3] = diff[1]
     */
    private void metrics1Shuffle3(double[] dummyDiff, double[] diff) {
        dummyDiff[0] = diff[1];
        dummyDiff[1] = diff[2];
        dummyDiff[2] = diff[3];
        dummyDiff[3] = diff[0];

    }

    /**
     * For processing 6 metrics with all cells higher or same.
     *
     * @param metrics1 the array of metrics to be processed
     * @param diff the array of differences of cell values
     * @param weight the weight to be applied to weighted metrics
     * @param averageDiff the average difference in height for diff (N.B This is
     * passed in rather than calculated here because of cell values that were
     * noDataValue in the Grids_GridDouble for which metrics1 are being
     * processed.
     */
    private void metrics1Calculate_hhhh(
            double[] metrics1,
            double[] diff,
            double weight,
            double averageDiff) {
        metrics1[9] += weight * Math.max(Math.max(diff[0], diff[1]), Math.max(diff[2], diff[3]));
        metrics1[10] += weight * Math.min(Math.min(diff[0], diff[1]), Math.min(diff[2], diff[3]));
        metrics1[11] += weight * (diff[0] + diff[1] + diff[2] + diff[3]);
        metrics1[12] += weight * averageDiff;
        metrics1[13] += 1.0d;
        metrics1[14] += weight;
    }

    /**
     * For processing a11 metrics with one cell lower or same.
     *
     * @param metrics1 the array of metrics to be processed
     * @param diff the array of differences of cell values
     * @param weight the weight to be applied to weighted metrics
     *
     */
    private void metrics1Calculate_hhhl(
            double[] metrics1,
            double[] diff,
            double weight) {
        metrics1[15] += weight * Math.min(diff[0], diff[2]);
        metrics1[16] += weight * Math.max(diff[0], diff[2]);
        metrics1[17] += weight * (diff[0] + diff[2]);
        metrics1[18] += weight * diff[1];
        metrics1[19] += weight * diff[3];
        metrics1[20] += weight * (diff[1] + diff[3]);
        metrics1[21] += weight * Math.min(diff[1], Math.abs(diff[3]));
        metrics1[22] += weight * Math.max(diff[1], Math.abs(diff[3]));
        metrics1[23] += weight * (diff[1] + Math.abs(diff[3]));
        metrics1[24] += 1.0d;
        metrics1[25] += weight;
    }

    /**
     * For processing 11 metrics with opposite cells lower/higher or same.
     *
     * @param metrics1 the array of metrics to be processed
     * @param diff the array of differences of cell values
     * @param weight the weight to be applied to weighted metrics
     *
     */
    private void metrics1Calculate_hlhl(
            double[] metrics1,
            double[] diff,
            double weight) {
        metrics1[26] += weight * Math.min(diff[0], diff[2]);
        metrics1[27] += weight * Math.max(diff[0], diff[2]);
        metrics1[28] += weight * (diff[0] + diff[2]);
        metrics1[29] += weight * Math.min(diff[1], diff[3]);
        metrics1[30] += weight * Math.max(diff[1], diff[3]);
        metrics1[31] += weight * (diff[1] + diff[3]);
        metrics1[32] += weight * (Math.min(Math.abs(Math.max(diff[1], diff[3])), Math.min(diff[0], diff[2])));
        metrics1[33] += weight * (Math.max(Math.abs(Math.min(diff[1], diff[3])), Math.max(diff[0], diff[2])));
        metrics1[34] += weight * (diff[0] + Math.abs(diff[1]) + diff[2] + Math.abs(diff[3]));
        metrics1[35] += 1.0d;
        metrics1[36] += weight;
    }

    /**
     * For processing a11 metrics with adjacent cells lower/higher or same.
     *
     * @param metrics1 the array of metrics to be processed
     * @param diff the array of differences of cell values
     * @param weight the weight to be applied to weighted metrics
     *
     */
    private void metrics1Calculate_hhll(
            double[] metrics1,
            double[] diff,
            double weight) {
        metrics1[37] += weight * Math.min(diff[0], diff[1]);
        metrics1[38] += weight * Math.max(diff[0], diff[1]);
        metrics1[39] += weight * (diff[0] + diff[1]);
        metrics1[40] += weight * Math.min(diff[2], diff[3]);
        metrics1[41] += weight * Math.max(diff[2], diff[3]);
        metrics1[42] += weight * (diff[2] + diff[3]);
        metrics1[43] += weight * (Math.min(Math.abs(Math.max(diff[2], diff[3])), Math.min(diff[1], diff[0])));
        metrics1[44] += weight * (Math.max(Math.abs(Math.min(diff[2], diff[3])), Math.max(diff[1], diff[0])));
        metrics1[45] += weight * (diff[1] + Math.abs(diff[2]) + diff[0] + Math.abs(diff[3]));
        metrics1[46] += 1.0d;
        metrics1[47] += weight;
    }

    /**
     * For processing a11 metrics with one cell higher or same.
     *
     * @param metrics1 the array of metrics to be processed
     * @param diff the array of differences of cell values
     * @param weight the weight to be applied to weighted metrics
     * @param averageDiff the average difference in height for diff (N.B This is
     * passed in rather than calculated here because of cell values that were
     * noDataValue in the Grids_GridDouble for which metrics1 are being
     * processed.
     *
     */
    private void metrics1Calculate_hlll(
            double[] metrics1,
            double[] diff,
            double weight) {
        metrics1[48] += weight * Math.min(diff[1], diff[3]);
        metrics1[49] += weight * Math.max(diff[1], diff[3]);
        metrics1[50] += weight * (diff[1] + diff[3]);
        metrics1[51] += weight * diff[2];
        metrics1[52] += weight * diff[0];
        metrics1[53] += weight * (diff[2] + diff[0]);
        metrics1[54] = weight * Math.min(diff[0], Math.abs(diff[2]));
        metrics1[55] = weight * Math.max(diff[0], Math.abs(diff[2]));
        metrics1[56] = weight * (diff[0] + Math.abs(diff[2]));
        metrics1[57] += 1.0d;
        metrics1[58] += weight;
    }

    /**
     * For processing 6 metrics with all cells lower or same.
     */
    private void metrics1Calculate_llll(
            double[] metrics1,
            double[] diff,
            double weight,
            double averageDiff) {
        metrics1[59] += weight * Math.max(Math.max(diff[0], diff[1]), Math.max(diff[2], diff[3]));
        metrics1[60] += weight * Math.min(Math.min(diff[0], diff[1]), Math.min(diff[2], diff[3]));
        metrics1[61] += weight * (diff[0] + diff[1] + diff[2] + diff[3]);
        metrics1[62] += weight * averageDiff;
        metrics1[63] += 1.0d;
        metrics1[64] += weight;
    }

    /**
     * Returns an Grids_GridDouble[] metrics2 where: TODO: metrics2 is a mess.
     * Need to decide what to do with regard to contour tracing and profile
     * trace for axes and comparisons. metrics2[0] = slope; metrics2[1] =
     * aspect; metrics2[2] = no data count; metrics2[3] = contourConcavity;
     * metrics2[4] = contourConvexity; metrics2[5] = profileConcavity;
     * metrics2[6] = profileConvexity;
     *
     * @param grid
     * @param distance
     * @param weightIntersect
     * @param weightFactor
     * @param handleOutOfMemoryError
     * @param samplingDensity
     * @param gridFactory
     * @return
     */
    public Grids_GridDouble[] getMetrics2(
            Grids_GridDouble grid,
            double distance,
            double weightIntersect,
            double weightFactor,
            int samplingDensity,
            Grids_GridDoubleFactory gridFactory,
            boolean handleOutOfMemoryError) {
        try {
            ge.getGrids().add(grid);
            Grids_GridDouble[] result = new Grids_GridDouble[7];
            long ncols = grid.getNCols(handleOutOfMemoryError);
            long nrows = grid.getNRows(handleOutOfMemoryError);
            Grids_Dimensions dimensions = grid.getDimensions(handleOutOfMemoryError);
            double gridNoDataValue = grid.getNoDataValue(handleOutOfMemoryError);
            Grids_GridDouble[] slopeAndAspect = null;
            //Grid2DSquareCellDouble[] _SlopeAndAspect = getSlopeAspect( grid, distance, weightIntersect, weightFactor, grid, gridFactory );
            result[0] = slopeAndAspect[0];
            result[1] = slopeAndAspect[1];
            for (int i = 0; i < result.length; i++) {
                result[i] = (Grids_GridDouble) gridFactory.create(nrows, ncols, dimensions);
            }
            double[] metrics2;
            double slope;
            double aspect;
            Point2D.Double[] metrics2Points;
            double[] weights;
            long row;
            long col;
            for (row = 0; row < nrows; row++) {
                for (col = 0; col < ncols; col++) {
                    if (grid.getCell(row, col, handleOutOfMemoryError) != gridNoDataValue) {
                        slope = result[0].getCell(row, col, handleOutOfMemoryError);
                        aspect = result[1].getCell(row, col, handleOutOfMemoryError);
                        metrics2Points = getMetrics2Points(slopeAndAspect, distance, samplingDensity);
                        weights = Grids_Kernel.getKernelWeights(grid, row, col, distance, weightIntersect, weightFactor, metrics2Points);
                        metrics2 = getMetrics2(grid, row, col, slopeAndAspect, distance, weights);
                        for (int i = 0; i < result.length; i++) {
                            result[i].setCell(row, col, metrics2[i], handleOutOfMemoryError);
                        }
                    }
                }
                System.out.println("Done row " + row);

            }
            result[2].setName("", handleOutOfMemoryError);
            result[3].setName("", handleOutOfMemoryError);
            result[4].setName("", handleOutOfMemoryError);
            result[5].setName("", handleOutOfMemoryError);
            result[6].setName("", handleOutOfMemoryError);

            return result;

        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                getMetrics2(
                        grid,
                        distance,
                        weightIntersect,
                        weightFactor,
                        samplingDensity,
                        gridFactory,
                        handleOutOfMemoryError);

            }
            throw _OutOfMemoryError;

        }
    }

    /**
     * Returns a Point2D.Double[] points that are sample points based on a
     * regular sampling around slope If samplingDensity
     *
     *
     */
    private Point2D.Double[] getMetrics2Points(
            Grids_GridDouble[] _SlopeAndAspect,
            double distance,
            int samplingDensity) {
        Point2D.Double[] metrics2Points = null;

        return metrics2Points;

    }

    private double[] getMetrics2(
            Grids_GridDouble grid,
            long row,
            long col,
            Grids_GridDouble[] _SlopeAndAspect,
            double distance,
            double[] weights) {
        double[] metrics2 = null;

        return metrics2;

    }

    /**
     * Returns an Grids_GridDouble result containing values which indicate the
     * direction of the maximum down slope for the immediate 8 cell
     * neighbourhood. 1 2 3 4 0 5 6 7 8 If there is no downhill slope then the
     * flow direction is 0.
     *
     * @param grid the Grids_GridDouble to be processed
     * @param gridFactory the Grids_GridDoubleFactory used to create result
     * @param handleOutOfMemoryError
     * @return
     */
    public Grids_GridDouble getMaxFlowDirection(
            Grids_GridDouble grid,
            Grids_GridDoubleFactory gridFactory,
            boolean handleOutOfMemoryError) {
        try {
            ge.getGrids().add(grid);

            long nrows = grid.getNRows(handleOutOfMemoryError);

            long ncols = grid.getNCols(handleOutOfMemoryError);

            double noDataValue = grid.getNoDataValue(handleOutOfMemoryError);
            Grids_GridDouble result = (Grids_GridDouble) gridFactory.create(nrows, ncols, grid.getDimensions(handleOutOfMemoryError));
            Grids_2D_ID_long cellID;
            long row;
            long col;
            int k;
            int[] flowDirections = new int[9];
            int flowDirection;
            double[] z = new double[9];
            double minz;
            int minzCount;
            int minzCountNoDataValue;
            long p;
            long q;
            for (row = 0; row < nrows; row++) {
                for (col = 0; col < ncols; col++) {
                    z[0] = grid.getCell(row, col, handleOutOfMemoryError);
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
                                    z[k] = grid.getCell(row + p, col + q, handleOutOfMemoryError);
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
                            flowDirection = min[(int) Math.floor(random * (minzCount + minzCountNoDataValue))];
                        }
                        result.setCell(row, col, (double) flowDirection, handleOutOfMemoryError);
                    }
                }
            }
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                return getMaxFlowDirection(
                        grid,
                        gridFactory,
                        handleOutOfMemoryError);

            } else {
                throw _OutOfMemoryError;

            }
        }
    }

    /**
     * Returns an Grids_GridDouble[] each element of which corresponds to a
     * metrics of up slope cells of grid - a DEM The steeper the slope the
     * higher the runoff?
     *
     * @param grid
     * @param distance
     * @param weightFactor
     * @param handleOutOfMemoryError
     * @param weightIntersect
     * @param gridFactory
     * @return
     */
    public Grids_GridDouble getUpSlopeAreaMetrics(
            Grids_GridDouble grid,
            double distance,
            double weightFactor,
            double weightIntersect,
            Grids_GridDoubleFactory gridFactory,
            boolean handleOutOfMemoryError) {
        try {
            ge.getGrids().add(grid);
            Grids_GridDouble upSlopeAreaMetrics = (Grids_GridDouble) gridFactory.create(
                    grid.getNRows(handleOutOfMemoryError),
                    grid.getNCols(handleOutOfMemoryError),
                    grid.getDimensions(handleOutOfMemoryError));
            // Get Peaks and set their value to 1.0d
            HashSet initialPeaksHashSet = getInitialPeaksHashSetAndSetTheirValue(grid, upSlopeAreaMetrics, handleOutOfMemoryError);
            // For each Peak find its neighbours and add a proportional value to
            // them based on slope. If the slope is zero then the neighbour is still
            // passed a proportion. This can be configured based on infiltration
            // rates or slope dependent distance decay stuff.
            //        HashSet neighboursOfInitialPeaksHashSet = getNeighboursOfInitialPeaksHashSetAndSetTheirValue( initialPeaksHashSet, grid, upSlopeAreaMetrics );
            // Add to neighbouring cells a value based on the amount of slope
            //        upSlopeMetricsAddToNeighbours( grid, peaks );

            return upSlopeAreaMetrics;

        } catch (OutOfMemoryError _OutOfMemoryError0) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();

                if (!ge.swapChunk(ge.HandleOutOfMemoryErrorFalse)) {
                    throw _OutOfMemoryError0;

                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                getUpSlopeAreaMetrics(
                        grid,
                        distance,
                        weightFactor,
                        weightIntersect,
                        gridFactory,
                        handleOutOfMemoryError);

            }
            throw _OutOfMemoryError0;

        }
    }

    /**
     * Returns a HashSet containing _CellIDs which identifies cells for which
     * neighbouring cells in the immediate 8 cell neighbourhood that are either
     * the same value, lower or noDataValues
     *
     * @param grid - the Grids_GridDouble to be processed
     * @param upSlopeAreaMetrics
     * @param handleOutOfMemoryError
     * @return
     */
    public HashSet getInitialPeaksHashSetAndSetTheirValue(
            Grids_GridDouble grid,
            Grids_GridDouble upSlopeAreaMetrics,
            boolean handleOutOfMemoryError) {
        try {
            ge.getGrids().add(grid);
            HashSet initialPeaksHashSet = new HashSet();

            long nrows = grid.getNRows(handleOutOfMemoryError);

            long ncols = grid.getNCols(handleOutOfMemoryError);

            double noDataValue = grid.getNoDataValue(handleOutOfMemoryError);

            double[] heights = new double[9];

            int k;

            for (int row = 0; row
                    < nrows; row++) {
                for (int col = 0; col
                        < ncols; col++) {
                    heights[0] = grid.getCell(row, col, handleOutOfMemoryError);

                    if (heights[0] != noDataValue) {
                        k = 0;

                        for (int p = -1; p
                                < 2; p++) {
                            for (int q = -1; q
                                    < 2; q++) {
                                if (!(p == 0 && q == 0)) {
                                    k++;
                                    heights[k] = grid.getCell(row + p, col + q, handleOutOfMemoryError);

                                }
                            }
                        }
                        // This deals with single isolated cells surrounded by noDataValues
                        if ((heights[1] <= heights[0] || heights[1] == noDataValue)
                                && (heights[2] <= heights[0] || heights[2] == noDataValue)
                                && (heights[3] <= heights[0] || heights[3] == noDataValue)
                                && (heights[4] <= heights[0] || heights[4] == noDataValue)
                                && (heights[5] <= heights[0] || heights[5] == noDataValue)
                                && (heights[6] <= heights[0] || heights[6] == noDataValue)
                                && (heights[7] <= heights[0] || heights[7] == noDataValue)
                                && (heights[8] <= heights[0] || heights[8] == noDataValue)) {
                            initialPeaksHashSet.add(grid.getCellID(row, col, handleOutOfMemoryError));
                            upSlopeAreaMetrics.addToCell(row, col, 1.0d, handleOutOfMemoryError);

                        }
                    }
                }
            }
            return initialPeaksHashSet;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                return getInitialPeaksHashSetAndSetTheirValue(
                        grid,
                        upSlopeAreaMetrics,
                        handleOutOfMemoryError);

            } else {
                throw _OutOfMemoryError;

            }
        }
    }
    /**
     * @param grid the Grid2DSquareCellDouble to be processed
     */
    /*protected HashSet getNeighboursOfInitialPeaksHashSetAndSetTheirValue( HashSet initialPeaksHashSet, Grids_GridDouble grid, Grids_GridDouble upSlopeAreaMetrics ) {
     double noDataValue = grid.getNoDataValue();
     double[ ] heights = new double[ 9 ];
     double[ ] diff = new double[ 9 ];
     HashSet neighboursOfInitialPeaksHashSet = Grids_Utilities.
     Iterator ite = hashSet.iterator();
     Integer cellID;
     int cellID;
     int row;
     int col;
     int k;
     int lowerCount = 0;
     double lowerHeight = 0.0d;
     while ( ite.hasNext() ) {
     cellID = ( Integer ) ite.next();
     cellID = cellID.intValue();
     row = grid.getRowIndex( cellID );
     col = grid.getColIndex( cellID );
     heights[ 0 ] = grid.getCell( row, col );
     if ( heights[ 0 ] != noDataValue ) {
     k = 0;
     for ( int p = -1; p < 2; p ++ ) {
     for ( int q = -1; q < 2; q ++ ) {
     if ( ! ( p == 0 && q == 0 ) ) {
     k ++;
     heights[ k ] = grid.getCell( row + p, col + q );
     if ( heights[ k ] != noDataValue ) {
     diff[ k ] = heights[ k ] - heights[ 0 ];
     if ( diff[ k ] >= 0.0d ) {
     lowerCount ++;
     lowerHeight += diff[ k ];
     }
     }
     }
     }
     // This deals with single isolated cells surrounded by noDataValues
     if ( ( heights[ 1 ] <= heights[ 0 ] || heights[ 1 ] == noDataValue ) &&
     ( heights[ 2 ] <= heights[ 0 ] || heights[ 2 ] == noDataValue ) &&
     ( heights[ 3 ] <= heights[ 0 ] || heights[ 3 ] == noDataValue ) &&
     ( heights[ 4 ] <= heights[ 0 ] || heights[ 4 ] == noDataValue ) &&
     ( heights[ 5 ] <= heights[ 0 ] || heights[ 5 ] == noDataValue ) &&
     ( heights[ 6 ] <= heights[ 0 ] || heights[ 6 ] == noDataValue ) &&
     ( heights[ 7 ] <= heights[ 0 ] || heights[ 7 ] == noDataValue ) &&
     ( heights[ 8 ] <= heights[ 0 ] || heights[ 8 ] == noDataValue ) ) {
     }
     }
     }
     }
     return;
     }*/
//    /**
//     * There are many estimates of flow that can be generated and many models
//     * developed in hydrology. These methods are simplistic but are based on
//     * the work of others. The basics are that discharge from any cell is a
//     * simple mutliple of velocity and depth. A measure of velocity can be
//     * obtained by measuring slope and the depth of discharge itself where the
//     * slope is given by the change in height divided by the distance.
//     * The algorithm is this:
//     * An Grids_GridDouble height is initialised using grid
//     * A coincident Grids_GridDouble accumulation is initialised
//     * Step 1: A value of rainfall is added to all cells in accumulation.
//     * Step 2: A proportion of this rainfall is then distributed to neighbouring
//     *         cells based on Mannings discharge equations.
//     *
//     * proportionally based on the difference in height of
//     *         neighbouring cells which are down slope. If no immediate
//     *         neighbours are downslope then the height cell is raised by value.
//     * Step 3: Repeat Steps 2 and 3 iterations number of times.
//     * Step 4: Return height and accumulation.
//     * NB Care needs to be taken to specify outflow cells
//     * TODO:
//     * 1. Change precipitation to be a grid
//     * 2. Variable frictionFactor
//     */
//    public Grids_GridDouble getFlowAccumulation(
//            Grids_GridDouble grid,
//            int iterations,
//            double precipitation,
//            HashSet outflowCellIDs,
//            Grids_GridDoubleFactory gridFactory,
//            boolean handleOutOfMemoryError ) {
//        int _MessageLength = 1000;
//        String _Message0 = ge.initString( _MessageLength, handleOutOfMemoryError );
//        String _Message = ge.initString( _MessageLength, handleOutOfMemoryError );
//        Grids_GridDouble flowAccumulation = getInitialFlowAccumulation(
//                grid,
//                precipitation,
//                outflowCellIDs,
//                gridFactory,
//                handleOutOfMemoryError );
//        _Message = "intitialFlowAccumulation";
//        _Message = ge.println( _Message, _Message0 );
//        _Message = flowAccumulation.toString();
//        _Message = ge.println( _Message, _Message0 );
//        for ( int iteration = 0; iteration < iterations; iteration ++ ) {
//            doFlowAccumulation(
//                    flowAccumulation,
//                    grid,
//                    precipitation,
//                    outflowCellIDs,
//                    gridFactory,
//                    handleOutOfMemoryError );
//            _Message = "flowAccumulation iteration " + ( iteration + 1 );
//            _Message = ge.println( _Message, _Message0 );
//            _Message = flowAccumulation.toString();
//            _Message = ge.println( _Message, _Message0 );
//        }
//        return flowAccumulation;
//    }
//    /**
//     * TODO: docs
//     * frictionFactor = 75.0d;
//     * constant = 8.0d * 9.81d / frictionFactor;
//     * velocity = Math.sqrt( constant * waterDepth * changeInDepth / ChangeInLength );
//     * discharge = velocity * waterDepth
//     */
//    public Grids_GridDouble getInitialFlowAccumulation(
//            Grids_GridDouble grid,
//            double precipitation,
//            HashSet outflowCellIDs,
//            Grids_GridDoubleFactory gridFactory,
//            boolean handleOutOfMemoryError ) {
//        //double constant = 8.0d * 9.81d / 75.0d ;
//        double constant = 1.0d;
//        long nrows = grid.getNRows( handleOutOfMemoryError );
//        long ncols = grid.getNCols( handleOutOfMemoryError );
//        BigDecimal[] dimensions = grid.getDimensions( handleOutOfMemoryError );
//        double noDataValue = grid.getNoDataValue( handleOutOfMemoryError );
//        // Precipitate
//        Grids_GridDouble flowAccumulation = ( Grids_GridDouble ) gridFactory.create( nrows, ncols, dimensions );
//        flowAccumulation = addToGrid( flowAccumulation, precipitation, handleOutOfMemoryError );
//        flowAccumulation = ( Grids_GridDouble ) mask( flowAccumulation, grid, gridFactory, handleOutOfMemoryError );
//        Grids_GridDouble tempFlowAccumulation = ( Grids_GridDouble ) gridFactory.create( flowAccumulation );
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
//        while ( ite.hasNext() ) {
//            cellID = ( CellID ) ite.next();
//            row = cellID.getRow();
//            col = cellID.getCellCol();
//            waterDepth = tempFlowAccumulation.getCell( row, col, handleOutOfMemoryError );
//            flowAccumulation.addToCell( row, col, - waterDepth / 2.0d, handleOutOfMemoryError );
//        }
//        for ( row = 0; row < nrows; row ++ ) {
//            for ( col = 0; col < ncols; col ++ ) {
//                surfaceHeights[1][1] = grid.getCell( row, col, handleOutOfMemoryError );
//                if ( surfaceHeights[1][1] != noDataValue ) {
//                    waterDepth = tempFlowAccumulation.getCell( row, col, handleOutOfMemoryError );
//                    surfaceHeights[1][1] += waterDepth;
//                    numberOfDownSlopes = 0.0d;
//                    sumDischarge = 0.0d;
//                    totalDischarge = 0.0d;
//                    for ( p = 0; p < 3; p ++ ) {
//                        for ( q = 0; q < 3; q ++ ) {
//                            if ( ! ( p == 1 && q == 1 ) ) {
//                                surfaceHeights[p][q] = grid.getCell( row + p - 1, col + q - 1, handleOutOfMemoryError );
//                                movingWaterDepth = Math.min( waterDepth, surfaceHeights[1][1] - surfaceHeights[p][q] );
//                                if ( ( surfaceHeights[p][q] != noDataValue ) && ( surfaceHeights[p][q] < surfaceHeights[1][1] ) ) {
//                                    numberOfDownSlopes += 1.0d;
//                                    if ( p == q || ( p == 0 && q == 2 ) || ( p == 2 && q == 0 ) ) {
//                                        slope = surfaceHeights[1][1] - surfaceHeights[p][q] / ( Math.sqrt( 2.0d ) );
//                                    } else {
//                                        slope = surfaceHeights[1][1] - surfaceHeights[p][q];
//                                    }
//                                    velocity = Math.sqrt( constant * movingWaterDepth * slope );
//                                    discharge[p][q] = velocity * movingWaterDepth;
//                                    sumDischarge += discharge[p][q];
//                                }
//                            }
//                        }
//                    }
//                    if ( numberOfDownSlopes > 0.0d ) {
//                        for ( p = 0; p < 3; p ++ ) {
//                            for ( q = 0; q < 3; q ++ ) {
//                                if ( ! ( p == 1 && q == 1 ) ) {
//                                    if ( surfaceHeights[p][q] != noDataValue && surfaceHeights[p][q] < surfaceHeights[1][1] ) {
//                                        movingWaterDepth = Math.min( waterDepth, surfaceHeights[1][1] - surfaceHeights[p][q] );
//                                        discharge[p][q] = ( discharge[p][q] / sumDischarge ) * ( movingWaterDepth / 2.0d ); // 50%
//                                        totalDischarge += discharge[p][q];
//                                        flowAccumulation.addToCell( row + p - 1, col + q - 1, discharge[p][q], handleOutOfMemoryError );
//                                    }
//                                }
//                            }
//                        }
//                        flowAccumulation.addToCell( row, col, - totalDischarge, handleOutOfMemoryError );
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
//     * velocity = Math.sqrt( constant * waterDepth * changeInDepth / ChangeInLength );
//     * discharge = velocity * waterDepth
//     */
//    public Grids_GridDouble doFlowAccumulation(
//            Grids_GridDouble flowAccumulation,
//            Grids_GridDouble grid,
//            double precipitation,
//            HashSet outflowCellIDs,
//            //Grid2DSquareCellDoubleFactory gridFactory,
//            boolean handleOutOfMemoryError ) {
//        //double constant = 8.0d * 9.81d / 75.0d ;
//        double constant = 1.0d;
//        long nrows = grid.getNRows( handleOutOfMemoryError );
//        long ncols = grid.getNCols( handleOutOfMemoryError );
//        BigDecimal[] dimensions = grid.getDimensions( handleOutOfMemoryError );
//        double noDataValue = grid.getNoDataValue( handleOutOfMemoryError );
//        int gridStatisticsType = 1;
//        // Precipitate
//        addToGrid(
//                flowAccumulation,
//                precipitation,
//                handleOutOfMemoryError );
//        mask(
//                flowAccumulation,
//                grid,
//                handleOutOfMemoryError );
//        Grids_GridDouble tempFlowAccumulation =
//                ( Grids_GridDouble ) gridFactory.create( flowAccumulation );
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
//        for ( row = 0; row < nrows; row ++ ) {
//            for ( col = 0; col < ncols; col ++ ) {
//                surfaceHeights[1][1] = grid.getCell( row, col, handleOutOfMemoryError );
//                if ( surfaceHeights[1][1] != noDataValue ) {
//                    waterDepth = tempFlowAccumulation.getCell( row, col, handleOutOfMemoryError );
//                    surfaceHeights[1][1] += waterDepth;
//                    numberOfDownSlopes = 0.0d;
//                    sumDischarge = 0.0d;
//                    totalDischarge = 0.0d;
//                    if ( outflowCellIDs.contains( grid.getCellID( row, col, handleOutOfMemoryError ) ) ) {
//                        // Simply lose a proportion of waterDepth (consider a friction factor)
//                        flowAccumulation.addToCell( row, col, - waterDepth / 2.0d, handleOutOfMemoryError );
//                        /*for ( int p = 0; p < 3; p ++ ) {
//                            for ( int q = 0; q < 3; q ++ ) {
//                                if ( ! ( p == 1 && q == 1 ) ) {
//                                    if ( grid.getCell( row + p - 1, col + q - 1 ) == noDataValue ) {
//                                        numberOfDownSlopes += 1.0d;
//                                        if ( p == q || ( p == 0 && q == 2 ) || ( p == 2 && q == 0 ) ) {
//                                            slope = waterDepth / ( Math.sqrt( 2.0d ) );
//                                        } else {
//                                            slope = waterDepth;
//                                        }
//                                        velocity = Math.sqrt( constant * waterDepth * slope );
//                                        discharge[p][q] = velocity * waterDepth;
//                                        sumDischarge += discharge[p][q];
//                                    }
//                                }
//                            }
//                        }
//                        if ( numberOfDownSlopes > 0.0d ) {
//                            for ( int p = 0; p < 3; p ++ ) {
//                                for ( int q = 0; q < 3; q ++ ) {
//                                    if ( ! ( p == 1 && q == 1 ) ) {
//                                        if ( grid.getCell( row + p - 1, col + q - 1 ) == noDataValue ) {
//                                            discharge[p][q] = ( discharge[p][q] / sumDischarge ) * ( waterDepth / 2.0d ); // 50%
//                                            totalDischarge += discharge[p][q];
//                                        }
//                                    }
//                                }
//                            }
//                            flowAccumulation.addToCell( row, col, - totalDischarge );
//                        }*/
//                    } else {
//                        for ( int p = 0; p < 3; p ++ ) {
//                            for ( int q = 0; q < 3; q ++ ) {
//                                if ( ! ( p == 1 && q == 1 ) ) {
//                                    surfaceHeights[p][q] = grid.getCell( row + p - 1, col + q - 1, handleOutOfMemoryError );
//                                    if ( surfaceHeights[p][q] != noDataValue ) {
//                                        surfaceHeights[p][q] += tempFlowAccumulation.getCell( row + p - 1, col + q - 1, handleOutOfMemoryError );
//                                        if ( surfaceHeights[p][q] < surfaceHeights[1][1] ) {
//                                            movingWaterDepth = Math.min( waterDepth, ( surfaceHeights[1][1] - surfaceHeights[p][q] ) );
//                                            numberOfDownSlopes += 1.0d;
//                                            if ( p == q || ( p == 0 && q == 2 ) || ( p == 2 && q == 0 ) ) {
//                                                slope = ( surfaceHeights[1][1] - surfaceHeights[p][q] ) / ( Math.sqrt( 2.0d ) );
//                                            } else {
//                                                slope = ( surfaceHeights[1][1] - surfaceHeights[p][q] );
//                                            }
//                                            velocity = Math.sqrt( constant * movingWaterDepth * slope );
//                                            discharge[p][q] = velocity * movingWaterDepth;
//                                            sumDischarge += discharge[p][q];
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        if ( numberOfDownSlopes > 0.0d ) {
//                            for ( int p = 0; p < 3; p ++ ) {
//                                for ( int q = 0; q < 3; q ++ ) {
//                                    if ( ! ( p == 1 && q == 1 ) ) {
//                                        if ( surfaceHeights[p][q] != noDataValue && surfaceHeights[p][q] < surfaceHeights[1][1] ) {
//                                            movingWaterDepth = Math.min( waterDepth, ( surfaceHeights[1][1] - surfaceHeights[p][q] ) );
//                                            discharge[p][q] = ( discharge[p][q] / sumDischarge ) * ( movingWaterDepth / 2.0d ); // 50%
//                                            totalDischarge += discharge[p][q];
//                                            flowAccumulation.addToCell( row + p - 1, col + q - 1, discharge[p][q], handleOutOfMemoryError );
//                                        }
//                                    }
//                                }
//                            }
//                            flowAccumulation.addToCell( row, col, - totalDischarge, handleOutOfMemoryError );
//                        }
//                    }
//                }
//            }
//        }
//        return flowAccumulation;
//    }
}
