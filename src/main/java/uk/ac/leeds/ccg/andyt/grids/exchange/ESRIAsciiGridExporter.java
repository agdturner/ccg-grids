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
package uk.ac.leeds.ccg.andyt.grids.exchange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.AbstractGrid2DSquareCell;
import uk.ac.leeds.ccg.andyt.grids.core.AbstractGrid2DSquareCell.ChunkID;
import uk.ac.leeds.ccg.andyt.grids.core.Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grid2DSquareCellInt;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;

/**
 * Class for exporting ESRI Asciigrid.
 */
//public class ESRIAsciiGridExporter extends ErrorHandler {
public class ESRIAsciiGridExporter {

    /** Creates a new instance of ESRIAsciiGridExporter */
    public ESRIAsciiGridExporter() {
        //this.init_MemoryReserve( handleOutOfMemoryError );
    }

    /**
     * Writes _Grid2DSquareCell out to file in ESRI Asciigrid format and returns
     * a the File to which it was written.
     * @param _Grid2DSquareCell TheAbstractGrid2DSquareCelll for export.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public File toAsciiFile(
            AbstractGrid2DSquareCell _Grid2DSquareCell,
            boolean handleOutOfMemoryError) {
        try {
            File result = toAsciiFile(_Grid2DSquareCell);
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (_Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(
                        _Grid2DSquareCell,
                        handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        _Grid2DSquareCell,
                        handleOutOfMemoryError);
                return toAsciiFile(
                        _Grid2DSquareCell,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Writes grid2DSquareCell out to file in ESRI Asciigrid format and returns
     * a the File to which it was written.
     * @param grid2DSquareCell TheAbstractGrid2DSquareCelll for export.
     * @return 
     */
    protected File toAsciiFile(
            AbstractGrid2DSquareCell grid2DSquareCell) {
        boolean handleOutOfMemoryError = false;
        File directory = grid2DSquareCell.get_Directory(handleOutOfMemoryError);
        File file = new File(
                directory.getParentFile(),
                grid2DSquareCell.get_Name(handleOutOfMemoryError) + ".asc");
        return toAsciiFile(
                grid2DSquareCell,
                file);
    }

    /**
     * Writes _Grid2DSquareCell out to file in ESRI Asciigrid format and returns
     * file.
     * @param _Grid2DSquareCell TheAbstractGrid2DSquareCelll for export.
     * @param file The File to export to.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public File toAsciiFile(
            AbstractGrid2DSquareCell _Grid2DSquareCell,
            File file,
            boolean handleOutOfMemoryError) {
        try {
            File result = toAsciiFile(
                    _Grid2DSquareCell,
                    file);
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (_Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(
                        _Grid2DSquareCell,
                        handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        _Grid2DSquareCell,
                        handleOutOfMemoryError);
                return toAsciiFile(
                        _Grid2DSquareCell,
                        file,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Writes _Grid2DSquareCell out to file in ESRI Asciigrid format and returns
     * file.
     * @param _Grid2DSquareCell TheAbstractGrid2DSquareCelll for export.
     * @param file The File to export to.
     * @return 
     */
    protected File toAsciiFile(
            AbstractGrid2DSquareCell _Grid2DSquareCell,
            File file) {
        return toAsciiFile(
                _Grid2DSquareCell,
                file,
                _Grid2DSquareCell.getNoDataValueBigDecimal(
                Grids_Environment.HandleOutOfMemoryErrorFalse));
    }

    /**
     * Writes _Grid2DSquareCell out to file in ESRI Asciigrid format and returns
     * file.
     * @param _Grid2DSquareCell TheAbstractGrid2DSquareCelll for export.
     * @param file The File to export to.
     * @param noDataValueBigDecimal The value to be used or substituted as a
     *   noDataValue for _Grid2DSquareCell.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public File toAsciiFile(
            AbstractGrid2DSquareCell _Grid2DSquareCell,
            File file,
            BigDecimal noDataValueBigDecimal,
            boolean handleOutOfMemoryError) {
        try {
            File result = toAsciiFile(
                    _Grid2DSquareCell,
                    file,
                    noDataValueBigDecimal);
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (_Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(
                        _Grid2DSquareCell,
                        handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        _Grid2DSquareCell,
                        handleOutOfMemoryError);
                return toAsciiFile(
                        _Grid2DSquareCell,
                        file,
                        noDataValueBigDecimal,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Writes _Grid2DSquareCell out to file in ESRI Asciigrid format and returns
     * file.
     *
     * @param _Grid2DSquareCell TheAbstractGrid2DSquareCelll for export.
     * @param file The File to export to.
     * @param noDataValueBigDecimal The value to be used or substituted as a
     *   noDataValue for _Grid2DSquareCell.
     * @return 
     */
    protected File toAsciiFile(
            AbstractGrid2DSquareCell _Grid2DSquareCell,
            File file,
            BigDecimal noDataValueBigDecimal) {
        boolean handleOutOfMemoryError = true;
        String noDataValueBigDecimalString;
        if (noDataValueBigDecimal == null) {
            noDataValueBigDecimalString = "-9999.0";
        } else {
            noDataValueBigDecimalString = noDataValueBigDecimal.toString();
        }
        try {
            PrintWriter pw = new PrintWriter(
                    new BufferedWriter(
                    new OutputStreamWriter(
                    new FileOutputStream(file))));
            BigDecimal[] dimensions = _Grid2DSquareCell.get_Dimensions(
                    handleOutOfMemoryError);
            long nrows = _Grid2DSquareCell.get_NRows(
                    Grids_Environment.HandleOutOfMemoryErrorFalse);
            long ncols = _Grid2DSquareCell.get_NCols(
                    Grids_Environment.HandleOutOfMemoryErrorFalse);
            long nrows_minus_1 = nrows - 1L;
            long _long_minus_1 = - 1L;
            long _long_0 = 0L;
            pw.println("ncols " + ncols);
            pw.println("nrows " + nrows);
            pw.println("xllcorner " + dimensions[1].toString());
            pw.println("yllcorner " + dimensions[2].toString());
            pw.println("cellsize " + dimensions[0].toString());
            long row = 0L;
            long col = 0L;

            if (_Grid2DSquareCell.getClass() == Grid2DSquareCellInt.class) {
                Grid2DSquareCellInt _Grid2DSquareCellInt = (Grid2DSquareCellInt) _Grid2DSquareCell;
                int noDataValue = _Grid2DSquareCellInt.getNoDataValue(
                        handleOutOfMemoryError);
                pw.println("NODATA_Value " + noDataValueBigDecimalString);
                int value = 0;
                for (row = nrows_minus_1; row > _long_minus_1; row--) {
                    for (col = _long_0; col < ncols; col++) {
                        try {
                            value = _Grid2DSquareCellInt.getCell(
                                    row,
                                    col,
                                    handleOutOfMemoryError);
                            if (value == noDataValue) {
                                pw.print(noDataValueBigDecimalString + " ");
                            } else {
                                pw.print(value + " ");
                            }
                        } catch (OutOfMemoryError a_OutOfMemoryError) {
                            _Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                            ChunkID a_ChunkID = new ChunkID(
                                    _Grid2DSquareCell.get_NChunkCols(handleOutOfMemoryError),
                                    _Grid2DSquareCell.getChunkRowIndex(row, handleOutOfMemoryError),
                                    _Grid2DSquareCell.getChunkColIndex(col, handleOutOfMemoryError));
                            if (_Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(
                                    _Grid2DSquareCell,
                                    a_ChunkID,
                                    handleOutOfMemoryError) < 1L) {
                                throw a_OutOfMemoryError;
                            }
                            _Grid2DSquareCell._Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);

                            //pw.print( grid.getCell( row, col ) + " " );
                            value = _Grid2DSquareCellInt.getCell(
                                    row,
                                    col,
                                    handleOutOfMemoryError);
                            if (value == noDataValue) {
                                pw.print(noDataValueBigDecimalString + " ");
                            } else {
                                pw.print(value + " ");
                            }
                        }
                    }
                    pw.println("");
                }
            } else {
                //_Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class
                Grid2DSquareCellDouble _Grid2DSquareCellDouble = (Grid2DSquareCellDouble) _Grid2DSquareCell;
                double noDataValue = _Grid2DSquareCellDouble.get_NoDataValue(handleOutOfMemoryError);
                if (Double.isInfinite(noDataValue)) {
                    System.out.println(
                            "Warning!!! noDataValue = "
                            + noDataValue
                            + " in ESRIAsciigridExporter.toAsciiFile( \n"
                            + "Grid2DSquareCellAbstract( " + _Grid2DSquareCellDouble.toString() + " ), \n"
                            + "File( " + file.toString() + " ) )");
                }
                pw.println("NODATA_Value " + noDataValueBigDecimalString);
                double value = 0.0d;
                for (row = nrows_minus_1; row > _long_minus_1; row--) {
                    for (col = _long_0; col < ncols; col++) {
                        try {
                            //pw.print( grid.getCell( row, col ) + " " );
                            value = _Grid2DSquareCellDouble.getCell(
                                    row,
                                    col,
                                    handleOutOfMemoryError);
                            if (Double.isNaN(value)) {
                                pw.print(noDataValueBigDecimalString + " ");
                                System.out.println("Warning!!! NaN encountered at row " + row + ", column " + col + " set to noDataValue " + noDataValueBigDecimalString + ".");
                            } else {
                                if (value == noDataValue) {
                                    pw.print(noDataValueBigDecimalString + " ");
                                } else {
                                    pw.print(value + " ");
                                }
                            }
                        } catch (OutOfMemoryError a_OutOfMemoryError) {
                            _Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                            ChunkID a_ChunkID = new ChunkID(
                                    _Grid2DSquareCell.get_NChunkCols(handleOutOfMemoryError),
                                    _Grid2DSquareCell.getChunkRowIndex(row, handleOutOfMemoryError),
                                    _Grid2DSquareCell.getChunkColIndex(col, handleOutOfMemoryError));
                            if (_Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(
                                    _Grid2DSquareCell,
                                    a_ChunkID,
                                    handleOutOfMemoryError) < 1L) {
                                throw a_OutOfMemoryError;
                            }
                            _Grid2DSquareCell._Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                            value = _Grid2DSquareCellDouble.getCell(
                                    row,
                                    col,
                                    handleOutOfMemoryError);
                            if (value == noDataValue) {
                                pw.print(noDataValueBigDecimalString + " ");
                            } else {
                                pw.print(value + " ");
                            }
                        }
                    }
                    pw.println("");
                }
            }
            // Close output
            pw.flush();
            pw.close();
        } catch (FileNotFoundException fnfe0) {
            System.out.println(fnfe0.toString());
            fnfe0.printStackTrace();
        }
        return file;
    }
}
