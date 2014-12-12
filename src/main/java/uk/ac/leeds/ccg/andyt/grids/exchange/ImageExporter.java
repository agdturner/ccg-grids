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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import javax.imageio.ImageIO;
import uk.ac.leeds.ccg.andyt.grids.core.AbstractGrid2DSquareCell;
import uk.ac.leeds.ccg.andyt.grids.core.AbstractGrid2DSquareCell.ChunkID;
import uk.ac.leeds.ccg.andyt.grids.core.Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grid2DSquareCellInt;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.process.Grid2DSquareCellProcessor;

/**
 * Class for exporting to images.
 */
//public class ImageExporter extends ErrorHandler {
public class ImageExporter implements Serializable {

    protected Grids_Environment _Grids_Environment;
    //private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of ImageExporter
     */
    public ImageExporter() {
        this._Grids_Environment = new Grids_Environment();
    }

    /**
     * Creates a new instance of ImageExporter
     *
     * @param a_Grids_Environment
     */
    public ImageExporter(
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
            AbstractGrid2DSquareCell grid,
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
            grid._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
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
            AbstractGrid2DSquareCell grid,
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
        boolean writerAvailable = IO.isImageWriterAvailable(type);
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
        grid._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                handleOutOfMemoryError);
        // If not already in the range 0 to 255, rescale grid into this range.
        Grid2DSquareCellDouble rescaledGrid = processor.rescale(
                grid, null, 0.0d, 255.0d, handleOutOfMemoryError);
        int nChunkCols = rescaledGrid.get_NChunkCols(handleOutOfMemoryError);
        int countNoDataValues = 0;
        int rescaledValue;

        if (grid instanceof Grid2DSquareCellDouble) {
            Grid2DSquareCellDouble gridDouble;
            gridDouble = (Grid2DSquareCellDouble) grid;
            double noDataValue = gridDouble.get_NoDataValue(handleOutOfMemoryError);
            double value = noDataValue;
            for (row = long_0; row < nrows; row++) {
                //for ( row = nrows - 1; row > -1; row -- ) {
                for (col = long_0; col < ncols; col++) {
                    try {
                        rescaledValue = (int) rescaledGrid.getCell(
                                row,
                                col,
                                handleOutOfMemoryError);
                        value = gridDouble.getCell(
                                row,
                                col,
                                handleOutOfMemoryError);
                    } catch (OutOfMemoryError e) {
                        _Grids_Environment.clear_MemoryReserve();
                        int chunkRowIndex = rescaledGrid.getChunkRowIndex(row, handleOutOfMemoryError);
                        int chunkRColIndex = rescaledGrid.getChunkColIndex(row, handleOutOfMemoryError);
                        ChunkID chunkID = new ChunkID(nChunkCols, chunkRowIndex, chunkRColIndex);
                        _Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(grid, chunkID, handleOutOfMemoryError);
                        rescaledValue = (int) rescaledGrid.getCell(
                                row,
                                col,
                                handleOutOfMemoryError);
                        _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                    }
                    _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                    //pos = ( int ) ( ( row * ncols ) + col );
                    pos = (int) ((((nrows - 1) - row) * ncols) + col);
                    // Construct an RGB integer by byte operation : 32 bytes, first 8 bytes is transparency value
                    // second, third, forth 8 byte is Red, green, blue value, which will be used by MemoryImageSource
                    // class.
                    // Set noDataValue as blue
                    if (rescaledValue == noDataValue) {
                        //gridImageValue = (255 << 24) | (0 << 16) | (0 << 8) | iValue;
                        //gridImageValue = (255 << 24) | iValue;
                        gridImageValue = (255 << 24) | 255;
                        //gridImageArray[pos] = gridImageValue;
                        gridImageArray[pos] = gridImageValue;
                        countNoDataValues++;
                    } else {
                        if (rescaledValue > 255) {
                            rescaledValue = 255;
                        } else {
                            if (rescaledValue < 0) {
                                rescaledValue = 0;
                            }
                        }

                        // debug
                        if (value != 0.0d) {
                            int debug = 1;
                        }

                        gridImageValue = (255 << 24) | (rescaledValue << 16) | (rescaledValue << 8) | rescaledValue;
//                        gridImageValue = rescaledValue
//                        gridImageValue = (rescaledValue << 24) | (rescaledValue << 16) | (rescaledValue << 8) | rescaledValue;
                        gridImageArray[pos] = gridImageValue;
                    }
                }
            }
            System.out.println("Number of NoDataValues " + countNoDataValues);
        } else {
            if (grid instanceof Grid2DSquareCellInt) {
                Grid2DSquareCellInt gridInt;
                gridInt = (Grid2DSquareCellInt) grid;
                int noDataValue = gridInt.getNoDataValue(handleOutOfMemoryError);
                int value = noDataValue;
                for (row = long_0; row < nrows; row++) {
                    //for ( row = nrows - 1; row > -1; row -- ) {
                    for (col = long_0; col < ncols; col++) {
                        try {
                            rescaledValue = (int) rescaledGrid.getCell(
                                    row,
                                    col,
                                    handleOutOfMemoryError);
                            value = gridInt.getCell(
                                    row,
                                    col,
                                    handleOutOfMemoryError);
                        } catch (OutOfMemoryError e) {
                            _Grids_Environment.clear_MemoryReserve();
                            int chunkRowIndex = rescaledGrid.getChunkRowIndex(row, handleOutOfMemoryError);
                            int chunkRColIndex = rescaledGrid.getChunkColIndex(row, handleOutOfMemoryError);
                            ChunkID chunkID = new ChunkID(nChunkCols, chunkRowIndex, chunkRColIndex);
                            _Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(grid, chunkID, handleOutOfMemoryError);
                            rescaledValue = (int) rescaledGrid.getCell(
                                    row,
                                    col,
                                    handleOutOfMemoryError);
                            _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                        }
                        _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                        //pos = ( int ) ( ( row * ncols ) + col );
                        pos = (int) ((((nrows - 1) - row) * ncols) + col);
                        // Construct an RGB integer by byte operation : 32 bytes, first 8 bytes is transparency value
                        // second, third, forth 8 byte is Red, green, blue value, which will be used by MemoryImageSource
                        // class.
                        // Set noDataValue as blue
                        if (rescaledValue == noDataValue) {
                            //gridImageValue = (255 << 24) | (0 << 16) | (0 << 8) | iValue;
                            //gridImageValue = (255 << 24) | iValue;
                            gridImageValue = (255 << 24) | 255;
                            //gridImageArray[pos] = gridImageValue;
                            gridImageArray[pos] = gridImageValue;
                            countNoDataValues++;
                        } else {
                            if (rescaledValue > 255) {
                                rescaledValue = 255;
                            } else {
                                if (rescaledValue < 0) {
                                    rescaledValue = 0;
                                }
                            }
                            gridImageValue = (255 << 24) | (rescaledValue << 16) | (rescaledValue << 8) | rescaledValue;
//                        gridImageValue = rescaledValue
//                            gridImageValue = (255 << 24) | (255 << 16) | (255 << 8) | 255; //white
//                          gridImageValue = (rescaledValue << 24) | (rescaledValue << 16) | (rescaledValue << 8) | rescaledValue;
//                          //gridImageValue = (255 << 24) | (rescaledValue << 16) | (rescaledValue << 8) | rescaledValue;
                            gridImageArray[pos] = gridImageValue;
                        }
                    }
                }

            }
        }
//            
//            Grid2DSquareCellInt gridInt = (Grid2DSquareCellInt) grid;
//            int noDataValue = gridInt.getNoDataValue(handleOutOfMemoryError);
//            BigDecimal maxBigDecimal = gridInt.getGridStatistics(
//                    handleOutOfMemoryError).getMaxBigDecimal(handleOutOfMemoryError);
//            BigDecimal minBigDecimal = gridInt.getGridStatistics(
//                    handleOutOfMemoryError).getMinBigDecimal(handleOutOfMemoryError);
//            BigDecimal rangeBigDecimal = maxBigDecimal.subtract(minBigDecimal);
//            //int max = _Grid2DSquareCellInt.getGridStatistics().getMaxInt( handleOutOfMemoryError );
//            //int min = _Grid2DSquareCellInt.getGridStatistics().getMinInt( handleOutOfMemoryError );
//            int value = Integer.MIN_VALUE;
//            for (row = long_0; row < nrows; row++) {
//                //for ( row = nrows - 1; row > -1; row -- ) {
//                for (col = long_0; col < ncols; col++) {
//                    try {
//                        value = gridInt.getCell(
//                                row,
//                                col,
//                                handleOutOfMemoryError);
//                        //pos = ( int ) ( ( row * ncols ) + col );
//                        pos = (int) ((((nrows - 1) - row) * ncols) + col);
//                        // Construct an RGB integer by byte operation : 32 bytes, first 8 bytes is transparency value
//                        // second, third, forth 8 byte is Red, green, blue value, which will be used by MemoryImageSource
//                        // class.
//                        iValue = 255;
//                        // Set noDataValue as blue
//                        if (value == noDataValue) {
//                            //gridImageValue = (255 << 24) | (0 << 16) | (0 << 8) | iValue;
//                            gridImageValue = (255 << 24) | iValue;
//                            gridImageArray[pos] = gridImageValue;
//                            countNoDataValues++;
//                        } else {
//                            valueBigDecimal = new BigDecimal(String.valueOf(value));
//                            if (maxBigDecimal.compareTo(minBigDecimal) != 0) {
//                                //if ( max != min ) {
//                                // The imprecision in the following calculation may cause problems, if more presision is need then a greater scale can be set.
//                                //iValueBigDecimal = aBigDecimal255.multiply( ( valueBigDecimal.subtract( minBigDecimal ) ) ).divide( rangeBigDecimal, scale, BigDecimal.ROUND_HALF_UP );
//                                iValueBigDecimal = bigDecimal_255.multiply((valueBigDecimal.subtract(minBigDecimal))).divide(rangeBigDecimal, scale, BigDecimal.ROUND_HALF_UP);
//                                //iValueDouble = 255.0d * ( ( ( double ) value - min ) / ( double ) ( max - min ) );
//                                // The imprecision in the following integerisation may cause problems
//                                iValue = iValueBigDecimal.intValue();
//
//                                //DEBUG if this does happen, iValue should be very close to 255 or 0
//                                if (iValue > 255) {
//                                    iValue = 255;
//                                } else {
//                                    if (iValue < 0) {
//                                        iValue = 0;
//                                    }
//                                }
//
//                                //gridImageValue = ( iValue << 24 ) | ( iValue << 16 ) | ( iValue << 8 ) | iValue;
//                                gridImageValue = (255 << 24) | (iValue << 16) | (iValue << 8) | iValue;
//                                //gridImageValue = ( 255 << 24 ) | ( 255 << 16 ) | ( iValue << 8 ) | iValue;
//                                //gridImageValue = ( 255 << 24 ) | ( 255 << 16 ) | ( 255 << 8 ) | iValue;
//                                gridImageArray[pos] = gridImageValue;
//                            } else {
//                                // Set white
//                                gridImageValue = (255 << 24) | (255 << 16) | (255 << 8) | 255;
//                                gridImageArray[pos] = gridImageValue;
//                            }
//                        }
//                    } catch (OutOfMemoryError a_OutOfMemoryError) {
//                        _Grids_Environment.clear_MemoryReserve();
//                        if (_Grids_Environment.swapToFile_Grid2DSquareCellChunks_Account(handleOutOfMemoryError) < 1L) {
//                            throw a_OutOfMemoryError;
//                        }
//
//                        System.out.println("countNoDataValues is unreliable");
//
//                        _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
//                        value = gridInt.getCell(
//                                row,
//                                col,
//                                handleOutOfMemoryError);
//                        //pos = ( int ) ( ( row * ncols ) + col );
//                        pos = (int) ((((nrows - 1) - row) * ncols) + col);
//                        // Construct an RGB integer by byte operation : 32 bytes, first 8 bytes is transparency value
//                        // second, third, forth 8 byte is Red, green, blue value, which will be used by MemoryImageSource
//                        // class.
//                        iValue = 255;
//                        // Set noDataValue as blue
//                        if (value == noDataValue) {
//                            //gridImageValue = (255 << 24) | (0 << 16) | (0 << 8) | iValue;
//                            gridImageValue = (255 << 24) | iValue;
//                            gridImageArray[pos] = gridImageValue;
//                            countNoDataValues++;
//                        } else {
//                            valueBigDecimal = new BigDecimal(String.valueOf(value));
//                            if (maxBigDecimal.compareTo(minBigDecimal) != 0) {
//                                //if ( max != min ) {
//                                // The imprecision in the following calculation may cause problems, if more presision is need then a greater scale can be set.
//                                //iValueBigDecimal = aBigDecimal255.multiply( ( valueBigDecimal.subtract( minBigDecimal ) ) ).divide( rangeBigDecimal, scale, BigDecimal.ROUND_HALF_UP );
//                                iValueBigDecimal = bigDecimal_255.multiply((valueBigDecimal.subtract(minBigDecimal))).divide(rangeBigDecimal, scale, BigDecimal.ROUND_HALF_UP);
//                                //iValueDouble = 255.0d * ( ( ( double ) value - min ) / ( double ) ( max - min ) );
//                                // The imprecision in the following integerisation may cause problems
//                                iValue = iValueBigDecimal.intValue();
//
//                                //DEBUG if this does happen, iValue should be very close to 255 or 0
//                                if (iValue > 255) {
//                                    iValue = 255;
//                                } else {
//                                    if (iValue < 0) {
//                                        iValue = 0;
//                                    }
//                                }
//
//                                //gridImageValue = ( iValue << 24 ) | ( iValue << 16 ) | ( iValue << 8 ) | iValue;
//                                gridImageValue = (255 << 24) | (iValue << 16) | (iValue << 8) | iValue;
//                                //gridImageValue = ( 255 << 24 ) | ( 255 << 16 ) | ( iValue << 8 ) | iValue;
//                                //gridImageValue = ( 255 << 24 ) | ( 255 << 16 ) | ( 255 << 8 ) | iValue;
//                                gridImageArray[pos] = gridImageValue;
//                            } else {
//                                // Set white
//                                gridImageValue = (255 << 24) | (255 << 16) | (255 << 8) | 255;
//                                gridImageArray[pos] = gridImageValue;
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//
//            System.out.println("Output Grid2DSquareCellDouble");
//
//            //_Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class
//            Grid2DSquareCellDouble _Grid2DSquareCellDouble = (Grid2DSquareCellDouble) grid;
//            double noDataValue = _Grid2DSquareCellDouble.get_NoDataValue(
//                    handleOutOfMemoryError);
////                  if (  Double.isInfinite( noDataValue ) ) {
////                    System.out.println(
////                            "Warning!!! noDataValue = " + noDataValue +
////                            " in ESRIAsciigridExporter.toGreyScaleImage( AbstractGrid2DSquareCell( " +
////                            _Grid2DSquareCellDouble.toString( handleOutOfMemoryError ) + " ), File( " +
////                            file.toString() + " ) )" );
////                }
//            BigDecimal maxBigDecimal = _Grid2DSquareCellDouble.getGridStatistics(
//                    handleOutOfMemoryError).getMaxBigDecimal(handleOutOfMemoryError);
//            BigDecimal minBigDecimal = _Grid2DSquareCellDouble.getGridStatistics(
//                    handleOutOfMemoryError).getMinBigDecimal(handleOutOfMemoryError);
//            BigDecimal rangeBigDecimal = maxBigDecimal.subtract(minBigDecimal);
//            // Read all data into an array and scale all values into the range [ 0, 255 ]
//            double value = Double.MIN_VALUE;
//            for (row = long_0; row < nrows; row++) {
//                //for ( row = nrows - 1; row > -1; row -- ) {
//                for (col = long_0; col < ncols; col++) {
//                    try {
//                        value = _Grid2DSquareCellDouble.getCell(
//                                row,
//                                col,
//                                handleOutOfMemoryError);
//                        //pos = ( int ) ( ( row * ncols ) + col );
//                        pos = (int) ((((nrows - 1) - row) * ncols) + col);
//                        // Construct an RGB integer by byte operation : 32 bytes, first 8 bytes is transparency value
//                        // second, third, forth 8 byte is Red, green, blue value, which will be used by MemoryImageSource
//                        // class.
//                        iValue = 255;
//                        // Set noDataValue as blue
//                        if (value == noDataValue) {
////                            gridImageValue = (255 << 24) | (0 << 16) | (0 << 8) | 255;
//                            gridImageValue = (255 << 24) | 255;
//                            gridImageArray[pos] = gridImageValue;
//                            countNoDataValues++;
//                        } else {
//                            valueBigDecimal = new BigDecimal(String.valueOf(value));
//                            if (maxBigDecimal.compareTo(minBigDecimal) != 0) {
//                                //if ( max != min ) {
//                                // The imprecision in the following calculation may cause problems, if more presision is need then a greater scale can be set.
//                                //iValueBigDecimal = aBigDecimal255.multiply( ( valueBigDecimal.subtract( minBigDecimal ) ) ).divide( rangeBigDecimal, scale, BigDecimal.ROUND_HALF_UP );
//                                iValueBigDecimal = bigDecimal_255.multiply((valueBigDecimal.subtract(minBigDecimal)).divide(rangeBigDecimal, scale, BigDecimal.ROUND_HALF_UP));
//                                //iValueDouble = 255.0d * ( ( ( double ) value - min ) / ( double ) ( max - min ) );
//                                iValue = iValueBigDecimal.intValue();
//
//                                //DEBUG if this does happen, iValue should be very close to 255 or 0
//                                if (iValue > 255) {
//                                    iValue = 255;
//                                } else {
//                                    if (iValue < 0) {
//                                        iValue = 0;
//                                    }
//                                }
//
//                                //gridImageValue = ( iValue << 24 ) | ( iValue << 16 ) | ( iValue << 8 ) | iValue;
//                                gridImageValue = (255 << 24) | (iValue << 16) | (iValue << 8) | iValue;
//                                //gridImageValue = ( 255 << 24 ) | ( 255 << 16 ) | ( iValue << 8 ) | iValue;
//                                //gridImageValue = ( 255 << 24 ) | ( 255 << 16 ) | ( 255 << 8 ) | iValue;
//                                gridImageArray[pos] = gridImageValue;
//                            } else {
//                                // Set white
//                                gridImageValue = (255 << 24) | (255 << 16) | (255 << 8) | 255;
//                                gridImageArray[pos] = gridImageValue;
//                            }
//                        }
//                    } catch (OutOfMemoryError a_OutOfMemoryError) {
//                        _Grids_Environment.clear_MemoryReserve();
//                        if (_Grids_Environment.swapToFile_Grid2DSquareCellChunks_Account(handleOutOfMemoryError) < 1L) {
//                            throw a_OutOfMemoryError;
//                        }
//
//                        System.out.println("countNoDataValues is unreliable");
//
//                        _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
//                        //_Grid2DSquareCell.init_MemoryReserve( _Grid2DSquareCell.handleOutOfMemoryErrorTrue ); Moved to after retry
//                        value = _Grid2DSquareCellDouble.getCell(
//                                row,
//                                col,
//                                handleOutOfMemoryError);
//                        //pos = ( int ) ( ( row * ncols ) + col );
//                        pos = (int) ((((nrows - 1) - row) * ncols) + col);
//                        // Construct an RGB integer by byte operation : 32 bytes, first 8 bytes is transparency value
//                        // second, third, forth 8 byte is Red, green, blue value, which will be used by MemoryImageSource
//                        // class.
//                        iValue = 255;
//                        // Set noDataValue as blue
//                        if (value == noDataValue) {
////                            gridImageValue = (255 << 24) | (0 << 16) | (0 << 8) | 255;
//                            gridImageValue = (255 << 24) | 255;
//                            gridImageArray[pos] = gridImageValue;
//                            countNoDataValues++;
//                        } else {
//                            valueBigDecimal = new BigDecimal(String.valueOf(value));
//                            if (maxBigDecimal.compareTo(minBigDecimal) != 0) {
//                                //if ( max != min ) {
//                                // The imprecision in the following calculation may cause problems, if more presision is need then a greater scale can be set.
//                                //iValueBigDecimal = aBigDecimal255.multiply( ( valueBigDecimal.subtract( minBigDecimal ) ) ).divide( rangeBigDecimal, scale, BigDecimal.ROUND_HALF_UP );
//                                iValueBigDecimal = bigDecimal_255.multiply((valueBigDecimal.subtract(minBigDecimal)).divide(rangeBigDecimal, scale, BigDecimal.ROUND_HALF_UP));
//                                //iValueDouble = 255.0d * ( ( ( double ) value - min ) / ( double ) ( max - min ) );
//                                iValue = iValueBigDecimal.intValue();
//
//                                //DEBUG if this does happen, iValue should be very close to 255 or 0
//                                if (iValue > 255) {
//                                    iValue = 255;
//                                } else {
//                                    if (iValue < 0) {
//                                        iValue = 0;
//                                    }
//                                }
//
//                                //gridImageValue = ( iValue << 24 ) | ( iValue << 16 ) | ( iValue << 8 ) | iValue;
//                                gridImageValue = (255 << 24) | (iValue << 16) | (iValue << 8) | iValue;
//                                //gridImageValue = ( 255 << 24 ) | ( 255 << 16 ) | ( iValue << 8 ) | iValue;
//                                //gridImageValue = ( 255 << 24 ) | ( 255 << 16 ) | ( 255 << 8 ) | iValue;
//                                gridImageArray[pos] = gridImageValue;
//                            } else {
//                                // Set white
//                                gridImageValue = (255 << 24) | (255 << 16) | (255 << 8) | 255;
//                                gridImageArray[pos] = gridImageValue;
//                            }
//                        }
//                    }
//                }
//            }
//        }
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
            } catch (OutOfMemoryError _OutOfMemoryError2) {
                System.err.println("Insufficient memory to write image.");
            }
        }
    }
}
