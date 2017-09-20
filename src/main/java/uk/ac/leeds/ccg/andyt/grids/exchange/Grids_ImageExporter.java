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
package uk.ac.leeds.ccg.andyt.grids.exchange;

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
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCell;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCellChunk;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCellDoubleChunk;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCellIntChunk;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellInt;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.process.Grid2DSquareCellProcessor;

/**
 * Class for exporting to images.
 */
//public class Grids_ImageExporter extends ErrorHandler {
public class Grids_ImageExporter implements Serializable {

    protected Grids_Environment _Grids_Environment;
    //private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of ImageExporter
     */
    //public Grids_ImageExporter() {
    //    this.ge = new ge();
    //}
    /**
     * Creates a new instance of ImageExporter
     *
     * @param a_Grids_Environment
     */
    public Grids_ImageExporter(
            Grids_Environment a_Grids_Environment) {
        this._Grids_Environment = a_Grids_Environment;
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
            Grids_AbstractGrid2DSquareCell grid,
            Grid2DSquareCellProcessor processor,
            File file,
            String type,
            boolean handleOutOfMemoryError) {
        try {
            toGreyScaleImage(
                    grid,
                    processor,
                    file,
                    type);
            grid.ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
        } catch (java.lang.OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                long swap = _Grids_Environment.swapToFile_Grid2DSquareCellChunks_Account(
                        handleOutOfMemoryError);
                if (swap < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                toGreyScaleImage(
                        grid,
                        processor,
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
     * @param grid
     * @param processor
     * @param file The File exported to.
     * @param type The name of the type of image to be written e.g. "png",
     * "jpeg"
     */
    protected void toGreyScaleImage(
            Grids_AbstractGrid2DSquareCell grid,
            Grid2DSquareCellProcessor processor,
            File file,
            String type) {
        // Initialisation
        boolean handleOutOfMemoryError = true;
        long nrows = grid.get_NRows(handleOutOfMemoryError);
        long ncols = grid.get_NCols(handleOutOfMemoryError);
        // Check int precision OK here.
        if (nrows * ncols > Integer.MAX_VALUE) {
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
        int size = (int) (ncols * nrows);
        long row = Integer.MIN_VALUE;
        long col = Integer.MIN_VALUE;
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
            _Grids_Environment.clear_MemoryReserve();
            long swap = _Grids_Environment.swapToFile_Grid2DSquareCellChunks_Account(
                    handleOutOfMemoryError);
            if (swap < 1L) {
                throw a_OutOfMemoryError;
            }
            _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
            gridImageArray = new int[size];
            Arrays.fill(gridImageArray, 0);
        }
        _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                handleOutOfMemoryError);
        // If not already in the range 0 to 255, rescale grid into this range.
        Grids_Grid2DSquareCellDouble rescaledGrid = processor.rescale(
                grid, null, 0.0d, 255.0d, handleOutOfMemoryError);
        int nChunkCols = rescaledGrid.get_NChunkCols(handleOutOfMemoryError);
        double noDataValue = rescaledGrid.get_NoDataValue(handleOutOfMemoryError);
        int countNoDataValues = 0;
        int rescaledValue;
        Grids_2D_ID_int chunkID;
        Color pixel;
        int chunkRowIndex;
        int chunkColIndex;
//        boolean inGrid;
//        int chunkCellRowIndex;
//        int chunkCellColIndex;
//        Grids_AbstractGrid2DSquareCellDoubleChunk grid2DSquareCellChunk;
//        int chunkNrows = gridDouble.get_ChunkNRows(handleOutOfMemoryError);
//        int chunkNcols = gridDouble.get_ChunkNCols(handleOutOfMemoryError);
//        for (chunkRowIndex = 0; chunkRowIndex < chunkNrows; chunkRowIndex++) {
//            for (chunkColIndex = 0; chunkColIndex < chunkNcols; chunkColIndex++) {
//                chunkID = new ID(
//                        nChunkCols, chunkRowIndex, chunkColIndex);
////                int chunkNCols = grid.get_ChunkNCols(
////                        chunkColIndex, handleOutOfMemoryError, chunkID);
////                int chunkNRows = grid.get_ChunkNRows(
////                        chunkRowIndex, handleOutOfMemoryError);
//                grid2DSquareCellChunk = rescaledGrid.getGrid2DSquareCellDoubleChunk(
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
                    rescaledValue = (int) rescaledGrid.getCell(
                            row,
                            col,
                            handleOutOfMemoryError);
                } catch (OutOfMemoryError e) {
                    _Grids_Environment.clear_MemoryReserve();
                    chunkRowIndex = rescaledGrid.getChunkRowIndex(row, handleOutOfMemoryError);
                    chunkColIndex = rescaledGrid.getChunkColIndex(col, handleOutOfMemoryError);
                    chunkID = new Grids_2D_ID_int(chunkRowIndex, chunkColIndex);
                    _Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                            grid, chunkID, handleOutOfMemoryError);
                    rescaledValue = (int) rescaledGrid.getCell(
                            row,
                            col,
                            handleOutOfMemoryError);
                    _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                }
                _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                pos = (int) ((((nrows - 1) - row) * ncols) + col);
                if (rescaledValue == noDataValue) {
                    // Set noDataValue as blue
                    pixel = new Color(0, 0, 255);
                    countNoDataValues++;
                } else {
                    if (rescaledValue > 255) {
                        rescaledValue = 255;
                    } else {
                        if (rescaledValue < 0) {
                            rescaledValue = 0;
                        }
                    }
                    pixel = new Color(rescaledValue, rescaledValue, rescaledValue);
                }
                gridImageArray[pos] = pixel.getRGB();
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
            rescaledGrid = null;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            try {
                _Grids_Environment.clear_MemoryReserve();
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunks_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                MemoryImageSource gridImageSource = new MemoryImageSource(
                        (int) ncols,
                        (int) nrows,
                        gridImageArray,
                        0,
                        (int) ncols);
                _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                Image tempImage = Toolkit.getDefaultToolkit().createImage(gridImageSource);
                _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                BufferedImage gridImage = new BufferedImage(
                        (int) ncols,
                        (int) nrows,
                        BufferedImage.TYPE_INT_RGB);
                _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                Graphics2D g = (Graphics2D) gridImage.getGraphics();
                _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
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
            Grids_Grid2DSquareCellDouble grid,
            Grid2DSquareCellProcessor processor,
            File file,
            String type,
            boolean handleOutOfMemoryError) {
        try {
            toColourImage(
                    duplication,
                    colours,
                    noDataValueColour,
                    grid,
                    processor,
                    file,
                    type);
            grid.ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
        } catch (java.lang.OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                long swap = _Grids_Environment.swapToFile_Grid2DSquareCellChunks_Account(
                        handleOutOfMemoryError);
                if (swap < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                toColourImage(
                        duplication,
                        colours,
                        noDataValueColour,
                        grid,
                        processor,
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
            Grids_Grid2DSquareCellDouble grid,
            Grid2DSquareCellProcessor processor,
            File file,
            String type) {
        // Initialisation
        boolean handleOutOfMemoryError = true;
        long nrows = grid.get_NRows(handleOutOfMemoryError);
        long ncols = grid.get_NCols(handleOutOfMemoryError);
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
            _Grids_Environment.clear_MemoryReserve();
            long swap = _Grids_Environment.swapToFile_Grid2DSquareCellChunks_Account(
                    handleOutOfMemoryError);
            if (swap < 1L) {
                throw a_OutOfMemoryError;
            }
            _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
            gridImageArray = new int[size];
            Arrays.fill(gridImageArray, 0);
        }
        _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                handleOutOfMemoryError);
        // If not already in the range 0 to 255, rescale grid into this range.
        int nChunkCols = grid.get_NChunkCols(handleOutOfMemoryError);
        double noDataValue = grid.get_NoDataValue(handleOutOfMemoryError);
        int countNoDataValues = 0;
        Grids_2D_ID_int chunkID;
        Color pixel;
        int chunkRowIndex;
        int chunkColIndex;
        double value;
//        boolean inGrid;
//        int chunkCellRowIndex;
//        int chunkCellColIndex;
//        Grids_AbstractGrid2DSquareCellDoubleChunk grid2DSquareCellChunk;
//        int chunkNrows = gridDouble.get_ChunkNRows(handleOutOfMemoryError);
//        int chunkNcols = gridDouble.get_ChunkNCols(handleOutOfMemoryError);
//        for (chunkRowIndex = 0; chunkRowIndex < chunkNrows; chunkRowIndex++) {
//            for (chunkColIndex = 0; chunkColIndex < chunkNcols; chunkColIndex++) {
//                chunkID = new ID(
//                        nChunkCols, chunkRowIndex, chunkColIndex);
////                int chunkNCols = grid.get_ChunkNCols(
////                        chunkColIndex, handleOutOfMemoryError, chunkID);
////                int chunkNRows = grid.get_ChunkNRows(
////                        chunkRowIndex, handleOutOfMemoryError);
//                grid2DSquareCellChunk = rescaledGrid.getGrid2DSquareCellDoubleChunk(
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
                    _Grids_Environment.clear_MemoryReserve();
                    chunkRowIndex = grid.getChunkRowIndex(row, handleOutOfMemoryError);
                    chunkColIndex = grid.getChunkColIndex(col, handleOutOfMemoryError);
                    chunkID = new Grids_2D_ID_int(chunkRowIndex, chunkColIndex);
                    _Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                            grid, chunkID, handleOutOfMemoryError);
                    value = grid.getCell(
                            row,
                            col,
                            handleOutOfMemoryError);
                    _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                }
                _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
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
                _Grids_Environment.clear_MemoryReserve();
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunks_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                MemoryImageSource gridImageSource = new MemoryImageSource(
                        (int) duplicationncols,
                        (int) duplicationnrows,
                        gridImageArray,
                        0,
                        (int) duplicationncols);
                _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                Image tempImage = Toolkit.getDefaultToolkit().createImage(gridImageSource);
                _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                BufferedImage gridImage = new BufferedImage(
                        (int) duplicationncols,
                        (int) duplicationnrows,
                        BufferedImage.TYPE_INT_RGB);
                _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
                Graphics2D g = (Graphics2D) gridImage.getGraphics();
                _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
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
