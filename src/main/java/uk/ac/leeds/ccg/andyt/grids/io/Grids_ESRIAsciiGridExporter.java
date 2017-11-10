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
package uk.ac.leeds.ccg.andyt.grids.io;

import java.io.File;
import java.io.PrintWriter;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridInt;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;

/**
 * Class for exporting ESRI Asciigrid.
 */
//public class Grids_ESRIAsciiGridExporter extends ErrorHandler {
public class Grids_ESRIAsciiGridExporter extends Grids_Object {

    String DefaultNoDataValue = "-9999.0d";

    /**
     * Creates a new instance of ESRIAsciiGridExporter
     *
     * @param env
     */
    public Grids_ESRIAsciiGridExporter(Grids_Environment env) {
        this.ge = env;
        //this.initMemoryReserve( handleOutOfMemoryError );
    }

    /**
     * Writes _Grid2DSquareCell out to file in ESRI Asciigrid format and returns
     * a the File to which it was written.
     *
     * @param g TheAbstractGrid2DSquareCelll for export.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public File toAsciiFile(
            Grids_AbstractGridNumber g,
            boolean handleOutOfMemoryError) {
        try {
            File result = toAsciiFile(g);
            g.ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                g.ge.clearMemoryReserve();
                if (g.ge.swapChunksExcept_Account(g, handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                g.ge.initMemoryReserve(g, handleOutOfMemoryError);
                return toAsciiFile(g, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Writes grid2DSquareCell out to file in ESRI Asciigrid format and returns
     * a the File to which it was written.
     *
     * @param g TheAbstractGrid2DSquareCelll for export.
     * @return
     */
    protected File toAsciiFile(
            Grids_AbstractGridNumber g) {
        boolean handleOutOfMemoryError = false;
        File directory = g.getDirectory(handleOutOfMemoryError);
        File file = new File(
                directory.getParentFile(),
                g.getName(handleOutOfMemoryError) + ".asc");
        return toAsciiFile(g, file);
    }

    /**
     * Writes _Grid2DSquareCell out to file in ESRI Asciigrid format and returns
     * file.
     *
     * @param g TheAbstractGrid2DSquareCelll for export.
     * @param file The File to export to.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public File toAsciiFile(
            Grids_AbstractGridNumber g,
            File file,
            boolean handleOutOfMemoryError) {
        try {
            File result = toAsciiFile(g, file);
            g.ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                g.ge.clearMemoryReserve();
                if (g.ge.swapChunksExcept_Account(g, handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                g.ge.initMemoryReserve(g, handleOutOfMemoryError);
                return toAsciiFile(g, file, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Writes _Grid2DSquareCell out to file in ESRI Asciigrid format and returns
     * file.
     *
     * @param g TheAbstractGrid2DSquareCelll for export.
     * @param file The File to export to.
     * @return
     */
    protected File toAsciiFile(
            Grids_AbstractGridNumber g,
            File file) {
        String noDataValue = "";
        if (g instanceof Grids_GridDouble) {
            noDataValue = "" + ((Grids_GridDouble) g).getNoDataValue(ge.HandleOutOfMemoryErrorFalse);
        } else if (g instanceof Grids_GridInt) {
            noDataValue = "" + ((Grids_GridInt) g).getNoDataValue(ge.HandleOutOfMemoryErrorFalse);
        }
        return toAsciiFile(
                g,
                file,
                noDataValue);
    }

    /**
     * Writes _Grid2DSquareCell out to file in ESRI Asciigrid format and returns
     * file.
     *
     * @param g TheAbstractGrid2DSquareCelll for export.
     * @param file The File to export to.
     * @param noDataValue The value to be used or substituted as a noDataValue
     * for g.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public File toAsciiFile(
            Grids_AbstractGridNumber g,
            File file,
            String noDataValue,
            boolean handleOutOfMemoryError) {
        try {
            File result = toAsciiFile(
                    g,
                    file,
                    noDataValue);
            g.ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                g.ge.clearMemoryReserve();
                if (g.ge.swapChunksExcept_Account(
                        g,
                        handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                g.ge.initMemoryReserve(
                        g,
                        handleOutOfMemoryError);
                return toAsciiFile(
                        g,
                        file,
                        noDataValue,
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
     * @param g Grids_AbstractGridNumber for export.
     * @param file The File to export to.
     * @param noDataValue The value to be used or substituted as a noDataValue
     * for g.
     * @return
     */
    protected File toAsciiFile(
            Grids_AbstractGridNumber g,
            File file,
            String noDataValue) {
        boolean handleOutOfMemoryError = true;
        try (PrintWriter pw = Generic_StaticIO.getPrintWriter(file, false)) {
            Grids_Dimensions dimensions;
            dimensions = g.getDimensions(handleOutOfMemoryError);
            long nrows = g.getNRows(ge.HandleOutOfMemoryErrorFalse);
            long ncols = g.getNCols(ge.HandleOutOfMemoryErrorFalse);
            long nrows_minus_1 = nrows - 1L;
            long _long_minus_1 = - 1L;
            long _long_0 = 0L;
            pw.println("ncols " + ncols);
            pw.println("nrows " + nrows);
            pw.println("xllcorner " + dimensions.getXMin().toString());
            pw.println("yllcorner " + dimensions.getYMin().toString());
            pw.println("cellsize " + dimensions.getCellsize().toString());
            long row = 0L;
            long col = 0L;
            if (g.getClass() == Grids_GridInt.class) {
                Grids_GridInt gridInt = (Grids_GridInt) g;
                int gridNoDataValue = gridInt.getNoDataValue(handleOutOfMemoryError);
                pw.println("NODATA_Value " + noDataValue);
                int value = 0;
                for (row = nrows_minus_1; row > _long_minus_1; row--) {
                    for (col = _long_0; col < ncols; col++) {
                        try {
                            value = gridInt.getCell(
                                    row,
                                    col,
                                    handleOutOfMemoryError);
                            if (value == gridNoDataValue) {
                                pw.print(noDataValue + " ");
                            } else {
                                pw.print(value + " ");
                            }
                        } catch (OutOfMemoryError a_OutOfMemoryError) {
                            g.ge.clearMemoryReserve();
                            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                                    g.getChunkRow(row, handleOutOfMemoryError),
                                    g.getChunkCol(col, handleOutOfMemoryError));
                            if (g.ge.swapChunksExcept_Account(
                                    g,
                                    chunkID,
                                    handleOutOfMemoryError) < 1L) {
                                throw a_OutOfMemoryError;
                            }
                            g.ge.initMemoryReserve(handleOutOfMemoryError);

                            //pw.print( grid.getCell( row, col ) + " " );
                            value = gridInt.getCell(
                                    row,
                                    col,
                                    handleOutOfMemoryError);
                            if (value == gridNoDataValue) {
                                pw.print(noDataValue + " ");
                            } else {
                                pw.print(value + " ");
                            }
                        }
                    }
                    pw.println("");
                }
            } else {
                //_Grid2DSquareCell.getClass() == Grids_GridDouble.class
                Grids_GridDouble gridDouble = (Grids_GridDouble) g;
                double gridNoDataValue = gridDouble.getNoDataValue(handleOutOfMemoryError);
                if (Double.isInfinite(gridNoDataValue)) {
                    System.out.println(
                            "Warning!!! noDataValue = "
                            + noDataValue
                            + " in ESRIAsciigridExporter.toAsciiFile( \n"
                            + "Grid2DSquareCellAbstract( " + gridDouble.toString() + " ), \n"
                            + "File( " + file.toString() + " ) )");
                }
                pw.println("NODATA_Value " + noDataValue);
                double value = 0.0d;
                for (row = nrows_minus_1; row > _long_minus_1; row--) {
                    for (col = _long_0; col < ncols; col++) {
                        try {
                            //pw.print( grid.getCell( row, col ) + " " );
                            value = gridDouble.getCell(
                                    row,
                                    col,
                                    handleOutOfMemoryError);
                            if (Double.isNaN(value)) {
                                pw.print(noDataValue + " ");
                                System.out.println(
                                        "Warning!!! NaN encountered at "
                                        + "row " + row + ","
                                        + " column " + col + ""
                                        + " set to noDataValue " + noDataValue + ".");
                            } else {
                                if (value == gridNoDataValue) {
                                    pw.print(noDataValue + " ");
                                } else {
                                    pw.print(value + " ");
                                }
                            }
                        } catch (OutOfMemoryError a_OutOfMemoryError) {
                            g.ge.clearMemoryReserve();
                            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                                    g.getChunkRow(row, handleOutOfMemoryError),
                                    g.getChunkCol(col, handleOutOfMemoryError));
                            if (g.ge.swapChunksExcept_Account(
                                    g,
                                    chunkID,
                                    handleOutOfMemoryError) < 1L) {
                                throw a_OutOfMemoryError;
                            }
                            g.ge.initMemoryReserve(handleOutOfMemoryError);
                            value = gridDouble.getCell(
                                    row,
                                    col,
                                    handleOutOfMemoryError);
                            if (value == gridNoDataValue) {
                                pw.print(noDataValue + " ");
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
        }
        return file;
    }
}
