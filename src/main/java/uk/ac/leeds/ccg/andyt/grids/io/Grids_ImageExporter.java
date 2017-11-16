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
package uk.ac.leeds.ccg.andyt.grids.io;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridInt;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;

/**
 * Class for exporting to images.
 */
//public class Grids_ImageExporter extends ErrorHandler {
public class Grids_ImageExporter extends Grids_Object implements Serializable {

    //private static final long serialVersionUID = 1L;
    protected Grids_ImageExporter() {
    }

    /**
     * Creates a new instance of ImageExporter
     *
     * @param ge
     */
    public Grids_ImageExporter(
            Grids_Environment ge) {
        super(ge);
    }

    /**
     * Writes this grid as a Grey scale image
     *
     * @param grid
     * @param processor
     * @param file The File exported to.
     * @param type The name of the type of image to be written e.g. "png",
     * "jpeg"
     * @param handleOutOfMemoryError
     */
    public void toGreyScaleImage(
            Grids_AbstractGridNumber grid,
            Grids_Processor processor,
            File file,
            String type,
            boolean handleOutOfMemoryError) {
        try {
            toGreyScaleImage(
                    grid,
                    processor,
                    file,
                    type);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (java.lang.OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                long swap = ge.swapChunks_Account(
                        handleOutOfMemoryError);
                if (swap < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                toGreyScaleImage(
                        grid,
                        processor,
                        file,
                        type,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Writes this grid as a Grey scale image
     *
     * @param g
     * @param processor
     * @param file The File exported to.
     * @param type The name of the type of image to be written e.g. "png",
     * "jpeg"
     */
    protected void toGreyScaleImage(
            Grids_AbstractGridNumber g,
            Grids_Processor processor,
            File file,
            String type) {
        // Initialisation
        boolean handleOutOfMemoryError = true;
        long nrows = g.getNRows(handleOutOfMemoryError);
        long ncols = g.getNCols(handleOutOfMemoryError);
//        System.out.println("nrows" + nrows);
//        System.out.println("ncols" + ncols);        
        // Check int precision OK here.
        if (nrows * ncols > Integer.MAX_VALUE) {
            System.err.println(
                    "Unable to export AbstractGrid2DSquareCell "
                    + g.toString(handleOutOfMemoryError) + " into a "
                    + "single image using "
                    + "toGreyScaleImage(AbstractGrid2DSquareCell,File,"
                    + "String) as _NRows * _Ncols > Integer.MAXVALUE");
            System.err.println(
                    "This method either needs development, or another does "
                    + "which should be called instead of this.");
            System.err.println(
                    "The images could be created in chunks by fixing the number"
                    + " range as parameters passed into the method.");
            return;
        }
        int size = (int) (ncols * nrows);
        long row;
        long col;
        int iValue = 0;
//        BigDecimal iValueBigDecimal = new BigDecimal("0.0");
//        BigDecimal valueBigDecimal = new BigDecimal("0.0");
//        BigDecimal bigDecimal_255 = new BigDecimal("255.0");
//        int scale = 20;
        int p;
//        int gridImageValue = Integer.MIN_VALUE;
        // Test what writers are available as this may vary on different systems!
        boolean writerAvailable = Grids_IO.isImageWriterAvailable(type);
        if (!writerAvailable) {
            System.out.println(
                    "Unable to export  using toGreyScaleImage("
                    + "AbstractGrid2DSquareCell,File,String) "
                    + "IO.isImageWriterAvailable(" + type + ") is not "
                    + "available.");
            String[] writerTypes = ImageIO.getWriterMIMETypes();
            System.out.println("WriterTypes:");
            for (int i = 0; i < writerTypes.length; i++) {
                System.out.println(writerTypes[i]);
            }
            return;
        }
        int[] gridImageArray;
        try {
            gridImageArray = new int[size];
            Arrays.fill(gridImageArray, 0);
        } catch (OutOfMemoryError e) {
            ge.clearMemoryReserve();
            long swap = ge.swapChunks_Account(handleOutOfMemoryError);
            if (swap < 1L) {
                throw e;
            }
            ge.initMemoryReserve(handleOutOfMemoryError);
            gridImageArray = new int[size];
            Arrays.fill(gridImageArray, 0);
        }
        ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        // If not already in the range 0 to 255, rescale grid into this range.
        Grids_GridDouble r = processor.rescale(
                g, null, 0.0d, 255.0d, handleOutOfMemoryError);
        double noDataValue = r.getNoDataValue(handleOutOfMemoryError);
//        System.out.println("r nrows " + r.getNRows(handleOutOfMemoryError));
//        System.out.println("r ncols " + r.getNCols(handleOutOfMemoryError));
//        System.out.println("r.getCell(0L, 0L) " + r.getCell(0L, 0L, handleOutOfMemoryError));
//        System.out.println("noDataValue " + noDataValue);
        int countNoDataValues = 0;
        double v;
        int vi;
        Grids_2D_ID_int chunkID;
        Color pixel;
        int chunkRow;
        int chunkCol;
        int cellRow;
        int cellCol;
        Grids_AbstractGridChunkDouble chunk;
        int chunkNRows;
        int chunkNCols;
        int nChunkRows = r.getNChunkRows(handleOutOfMemoryError);
        int nChunkCols = r.getNChunkCols(handleOutOfMemoryError);
        for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
            chunkNRows = r.getChunkNRows(chunkRow, handleOutOfMemoryError);
            for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                chunkNCols = r.getChunkNCols(chunkCol, handleOutOfMemoryError);
                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                ge.addToNotToSwapData(r, chunkID);
                chunk = (Grids_AbstractGridChunkDouble) r.getGridChunk(
                        chunkID, handleOutOfMemoryError);
                for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                    row = r.getRow(chunkRow, cellRow, handleOutOfMemoryError);
                    for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                        col = r.getCol(chunkCol, cellCol, handleOutOfMemoryError);
                        v = r.getCell(
                                chunk,
                                cellRow,
                                cellCol,
                                handleOutOfMemoryError);
                        p = (int) ((((nrows - 1) - row) * ncols) + col);
                        //p = (int) ((row * ncols) + col); // Upside down!
                        if (v == noDataValue) {
                            // Set noDataValue as blue
                            pixel = new Color(0, 0, 255);
                            countNoDataValues++;
                        } else {
                            if (v > 255) {
                                v = 255;
                            } else {
                                if (v < 0) {
                                    v = 0;
                                }
                            }
                            vi = (int) v;
                            pixel = new Color(vi, vi, vi);
                        }
                        gridImageArray[p] = pixel.getRGB();
                    }
                }
                ge.removeFromNotToSwapData(g, chunkID);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            }
        }
//        for (row = long_0; row < nrows; row++) {
//            //for ( row = nrows - 1; row > -1; row -- ) {
//            for (col = long_0; col < ncols; col++) {
//                try {
//                    rescaledValue = (int) rescaledGrid.getCell(
//                            row,
//                            col,
//                            handleOutOfMemoryError);
//                } catch (OutOfMemoryError e) {
//                    ge.clearMemoryReserve();
//                    chunkRow = rescaledGrid.getChunkRow(row, handleOutOfMemoryError);
//                    chunkCol = rescaledGrid.getChunkCol(col, handleOutOfMemoryError);
//                    chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
//                    ge.swapChunkExcept_Account(
//                            g, chunkID, handleOutOfMemoryError);
//                    rescaledValue = (int) rescaledGrid.getCell(
//                            row,
//                            col,
//                            handleOutOfMemoryError);
//                    ge.initMemoryReserve(handleOutOfMemoryError);
//                }
//                ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
//                pos = (int) ((((nrows - 1) - row) * ncols) + col);
//                if (rescaledValue == noDataValue) {
//                    // Set noDataValue as blue
//                    pixel = new Color(0, 0, 255);
//                    countNoDataValues++;
//                } else {
//                    if (rescaledValue > 255) {
//                        rescaledValue = 255;
//                    } else {
//                        if (rescaledValue < 0) {
//                            rescaledValue = 0;
//                        }
//                    }
//                    pixel = new Color(rescaledValue, rescaledValue, rescaledValue);
//                }
//                gridImageArray[pos] = pixel.getRGB();
//            }
//        }
        System.out.println("Number of NoDataValues " + countNoDataValues);
        if (countNoDataValues == ncols * nrows) {
            System.out.println("All values seem to be noDataValues!");
        }
        try {
            // Use gridImageArray to create a MemoryImageSource
            // Construct a BufferedImage using Toolkit
            // because Image class does not implement the RenderedImage
            // interface and imageio needs it.
            MemoryImageSource gridImageSource = new MemoryImageSource(
                    (int) ncols,
                    (int) nrows,
                    gridImageArray,
                    0,
                    (int) ncols);
            Image tempImage = Toolkit.getDefaultToolkit().createImage(gridImageSource);
            BufferedImage gridImage = new BufferedImage(
                    (int) ncols,
                    (int) nrows,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = (Graphics2D) gridImage.getGraphics();
            graphics.drawImage(tempImage, 0, 0, new java.awt.Panel());
            try {
                javax.imageio.ImageIO.write(gridImage, type, file);
            } catch (java.io.IOException e1) {
                e1.printStackTrace(System.err);
                System.out.println(
                        "Warning!!! Failed to write grid as "
                        + type + " to File(" + file.toString() + ")");
            }
            graphics.dispose();
            gridImage.flush();
            tempImage.flush();
            r = null;
        } catch (OutOfMemoryError e) {
            try {
                ge.clearMemoryReserve();
                if (ge.swapChunks_Account(handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                MemoryImageSource gridImageSource = new MemoryImageSource(
                        (int) ncols,
                        (int) nrows,
                        gridImageArray,
                        0,
                        (int) ncols);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                Image tempImage = Toolkit.getDefaultToolkit().createImage(gridImageSource);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                BufferedImage gridImage = new BufferedImage(
                        (int) ncols,
                        (int) nrows,
                        BufferedImage.TYPE_INT_RGB);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                Graphics2D graphics = (Graphics2D) gridImage.getGraphics();
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                graphics.drawImage(tempImage, 0, 0, new java.awt.Panel());
                try {
                    javax.imageio.ImageIO.write(gridImage, type, file);
                } catch (java.io.IOException e1) {
                    e1.printStackTrace(System.err);
                    System.out.println(
                            "Warning!!! Failed to write grid as "
                            + type + " to File(" + file.toString() + ")");
                }
                graphics.dispose();
                gridImage.flush();
                tempImage.flush();
            } catch (OutOfMemoryError e2) {
                System.err.println("Insufficient memory to write image.");
            }
        }
    }

    /**
     * Writes this grid as a Colour image using colours in the HashMap
     *
     * @param grid
     * @param processor
     * @param file The File exported to.
     * @param type The name of the type of image to be written e.g. "png",
     * "jpeg"
     * @param handleOutOfMemoryError
     */
    public void toColourImage(
            int duplication,
            TreeMap<Double, Color> colours,
            Color noDataValueColour,
            Grids_GridDouble grid,
            File file,
            String type,
            boolean handleOutOfMemoryError) {
        try {
            toColourImage(
                    duplication,
                    colours,
                    noDataValueColour,
                    grid,
                    file,
                    type);
            grid.ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
        } catch (java.lang.OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                long swap = ge.swapChunks_Account(
                        handleOutOfMemoryError);
                if (swap < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                toColourImage(
                        duplication,
                        colours,
                        noDataValueColour,
                        grid,
                        file,
                        type,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Writes this grid as a Grey scale image
     *
     * @param duplication This is for resampling, if duplication = 0 then pixels
     * are not duplicated. If duplication = 1 then 4 times as many pixels are
     * written out. If duplication = 2 then 9 times as many pixels are written
     * out.
     * @param grid
     * @param processor
     * @param file The File exported to.
     * @param type The name of the type of image to be written e.g. "png",
     * "jpeg"
     */
    protected void toColourImage(
            int duplication,
            TreeMap<Double, Color> colours,
            Color noDataValueColour,
            Grids_GridDouble grid,
            File file,
            String type) {
        // Initialisation
        boolean handleOutOfMemoryError = true;
        long nrows = grid.getNRows(handleOutOfMemoryError);
        long ncols = grid.getNCols(handleOutOfMemoryError);
        // Check int precision OK here.
        if (nrows * ncols * (duplication + 1) * (duplication + 1) > Integer.MAX_VALUE) {
            System.err.println(
                    "Unable to export AbstractGrid2DSquareCell "
                    + grid.toString(handleOutOfMemoryError) + " into a "
                    + "single image using "
                    + "toGreyScaleImage(AbstractGrid2DSquareCell,File,"
                    + "String) as _NRows * _Ncols > Integer.MAXVALUE");
            System.err.println(
                    "This method either needs development, or another does "
                    + "which should be called instead of this.");
            System.err.println(
                    "The images could be created in chunks by fixing the number"
                    + " range as parameters passed into the method.");
            return;
        }
        long row = Integer.MIN_VALUE;
        long col = Integer.MIN_VALUE;
        int duplicationnrows = ((int) nrows * (duplication + 1));
        int duplicationncols = ((int) ncols * (duplication + 1));
        int duplicationRow;
        int duplicationCol;
        int pixelRGB;
        int size = (int) (duplicationnrows * duplicationncols);
        int iValue = 0;
        long long_0 = 0L;
//        BigDecimal iValueBigDecimal = new BigDecimal("0.0");
//        BigDecimal valueBigDecimal = new BigDecimal("0.0");
//        BigDecimal bigDecimal_255 = new BigDecimal("255.0");
//        int scale = 20;
        int pos = Integer.MIN_VALUE;
        int gridImageValue = Integer.MIN_VALUE;
        // Test what writers are available as this may vary on different systems!
        boolean writerAvailable = Grids_IO.isImageWriterAvailable(type);
        if (!writerAvailable) {
            System.out.println(
                    "Unable to export  using toGreyScaleImage("
                    + "AbstractGrid2DSquareCell,File,String) "
                    + "IO.isImageWriterAvailable(" + type + ") is not "
                    + "available.");
            String[] writerTypes = ImageIO.getWriterMIMETypes();
            System.out.println("WriterTypes:");
            for (int i = 0; i < writerTypes.length; i++) {
                System.out.println(writerTypes[i]);
            }
            return;
        }
        int[] gridImageArray;
        try {
            gridImageArray = new int[size];
            Arrays.fill(gridImageArray, 0);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            ge.clearMemoryReserve();
            long swap = ge.swapChunks_Account(
                    handleOutOfMemoryError);
            if (swap < 1L) {
                throw a_OutOfMemoryError;
            }
            ge.initMemoryReserve(handleOutOfMemoryError);
            gridImageArray = new int[size];
            Arrays.fill(gridImageArray, 0);
        }
        ge.tryToEnsureThereIsEnoughMemoryToContinue(
                handleOutOfMemoryError);
        // If not already in the range 0 to 255, rescale grid into this range.
        int nChunkCols = grid.getNChunkCols(handleOutOfMemoryError);
        double noDataValue = grid.getNoDataValue(handleOutOfMemoryError);
        int countNoDataValues = 0;
        Grids_2D_ID_int chunkID;
        Color pixel;
        int chunkRowIndex;
        int chunkColIndex;
        double value;
//        boolean inGrid;
//        int chunkCellRowIndex;
//        int chunkCellColIndex;
//        Grids_AbstractGridChunkDouble grid2DSquareCellChunk;
//        int chunkNrows = gridDouble.getChunkNRows(handleOutOfMemoryError);
//        int chunkNcols = gridDouble.getChunkNCols(handleOutOfMemoryError);
//        for (chunkRowIndex = 0; chunkRowIndex < chunkNrows; chunkRowIndex++) {
//            for (chunkColIndex = 0; chunkColIndex < chunkNcols; chunkColIndex++) {
//                chunkID = new ID(
//                        nChunkCols, chunkRowIndex, chunkColIndex);
////                int chunkNCols = grid.getChunkNCols(
////                        chunkColIndex, handleOutOfMemoryError, chunkID);
////                int chunkNRows = grid.getChunkNRows(
////                        chunkRowIndex, handleOutOfMemoryError);
//                grid2DSquareCellChunk = rescaledGrid.getGridChunk(
//                        chunkID, handleOutOfMemoryError);
//                for (chunkCellRowIndex = 0; chunkCellRowIndex < chunkNrows; chunkCellRowIndex++) {
//                    row = chunkRowIndex * chunkNrows + chunkCellRowIndex;
//                    for (chunkCellColIndex = 0; chunkCellColIndex < chunkNcols; chunkCellColIndex++) {
//                        col = chunkColIndex * chunkNcols + chunkCellColIndex;
//                        inGrid = gridDouble.isInGrid(
//                                chunkRowIndex,
//                                chunkColIndex,
//                                chunkCellRowIndex,
//                                chunkCellColIndex,
//                                handleOutOfMemoryError);
//                        if (inGrid) {
//                            rescaledValue = (int) rescaledGrid.getCell(
//                                    grid2DSquareCellChunk,
//                                    chunkRowIndex,
//                                    chunkColIndex,
//                                    chunkCellRowIndex,
//                                    chunkCellColIndex,
//                                    handleOutOfMemoryError);
//                            pos = (int) ((((nrows - 1) - row) * ncols) + col);
//                            if (rescaledValue == noDataValue) {
//                                // Set noDataValue as blue
//                                pixel = new Color(0, 0, 255);
//                                countNoDataValues++;
//                            } else {
//                                if (rescaledValue > 255) {
//                                    rescaledValue = 255;
//                                } else {
//                                    if (rescaledValue < 0) {
//                                        rescaledValue = 0;
//                                    }
//                                }
//                                pixel = new Color(rescaledValue, rescaledValue, rescaledValue);
//                            }
//                            gridImageArray[pos] = pixel.getRGB();
//                        }
//                    }
//                }
//            }
//        }
        for (row = long_0; row < nrows; row++) {
            //for ( row = nrows - 1; row > -1; row -- ) {
            for (col = long_0; col < ncols; col++) {
                try {
                    value = grid.getCell(
                            row,
                            col,
                            handleOutOfMemoryError);
                } catch (OutOfMemoryError e) {
                    ge.clearMemoryReserve();
                    chunkRowIndex = grid.getChunkRow(row, handleOutOfMemoryError);
                    chunkColIndex = grid.getChunkCol(col, handleOutOfMemoryError);
                    chunkID = new Grids_2D_ID_int(chunkRowIndex, chunkColIndex);
                    ge.swapChunkExcept_Account(
                            grid, chunkID, handleOutOfMemoryError);
                    value = grid.getCell(
                            row,
                            col,
                            handleOutOfMemoryError);
                    ge.initMemoryReserve(handleOutOfMemoryError);
                }
                ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                if (value == noDataValue) {
                    pixel = noDataValueColour;
                } else {
                    if (Double.isNaN(value)) {
                        pixel = noDataValueColour;
                    } else {
                        pixel = getColor(value, colours);
                    }
                }
                pixelRGB = pixel.getRGB();
                if (duplication == 0) {
                    pos = (int) ((((nrows - 1) - row) * ncols) + col);
                    gridImageArray[pos] = pixelRGB;
                } else {
                    for (int i = 0; i <= duplication; i++) {
                        duplicationRow = (duplicationnrows - 1) - (((int) row * (duplication + 1)) + i);
                        for (int j = 0; j <= duplication; j++) {
                            duplicationCol = (int) (col * (duplication + 1)) + j;
                            pos = (int) ((duplicationRow * duplicationncols) + duplicationCol);
                            //pos = (int) ((((nrows - 1) - row + i) * ncols) + col + j);
                            gridImageArray[pos] = pixel.getRGB();
                        }
                    }
                }
            }
        }
        System.out.println("Number of NoDataValues " + countNoDataValues);
        if (countNoDataValues == ncols * nrows) {
            System.out.println("All values seem to be noDataValues!");
        }
        try {
            // Use gridImageArray to create a MemoryImageSource
            // Construct a BufferedImage using Toolkit
            // because Image class does not implement the RenderedImage
            // interface and imageio needs it.
            MemoryImageSource gridImageSource = new MemoryImageSource(
                    (int) duplicationncols,
                    (int) duplicationnrows,
                    gridImageArray,
                    0,
                    (int) duplicationncols);
            Image tempImage = Toolkit.getDefaultToolkit().createImage(gridImageSource);
            BufferedImage gridImage = new BufferedImage(
                    (int) duplicationncols,
                    (int) duplicationnrows,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = (Graphics2D) gridImage.getGraphics();
            g.drawImage(tempImage, 0, 0, new java.awt.Panel());
            try {
                javax.imageio.ImageIO.write(gridImage, type, file);
            } catch (java.io.IOException e1) {
                e1.printStackTrace();
                System.out.println(
                        "Warning!!! Failed to write grid as "
                        + type + " to File(" + file.toString() + ")");
            }
            g.dispose();
            gridImage.flush();
            tempImage.flush();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            try {
                ge.clearMemoryReserve();
                if (ge.swapChunks_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                MemoryImageSource gridImageSource = new MemoryImageSource(
                        (int) duplicationncols,
                        (int) duplicationnrows,
                        gridImageArray,
                        0,
                        (int) duplicationncols);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                Image tempImage = Toolkit.getDefaultToolkit().createImage(gridImageSource);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                BufferedImage gridImage = new BufferedImage(
                        (int) duplicationncols,
                        (int) duplicationnrows,
                        BufferedImage.TYPE_INT_RGB);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                Graphics2D g = (Graphics2D) gridImage.getGraphics();
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                g.drawImage(tempImage, 0, 0, new java.awt.Panel());
                try {
                    javax.imageio.ImageIO.write(gridImage, type, file);
                } catch (java.io.IOException e1) {
                    e1.printStackTrace();
                    System.out.println(
                            "Warning!!! Failed to write grid as "
                            + type + " to File(" + file.toString() + ")");
                }
                g.dispose();
                gridImage.flush();
                tempImage.flush();
            } catch (OutOfMemoryError _OutOfMemoryError2) {
                System.err.println("Insufficient memory to write image.");
            }
        }
    }

    Color getColor(double value, TreeMap<Double, Color> colors) {
        Iterator<Double> ite;
        ite = colors.keySet().iterator();
        Double v2 = null;
        while (ite.hasNext()) {
            v2 = ite.next();
            if (value <= v2) {
                return colors.get(v2);
            }
        }
        return colors.get(v2);
    }
}
