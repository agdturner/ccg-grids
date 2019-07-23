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
        this.env = env;
        //this.initMemoryReserve( hoome );
    }

    /**
     * Writes grid2DSquareCell out to file in ESRI Asciigrid format and returns
     * a the File to which it was written.
     *
     * @param g Grid for export.
     * @return
     */
    public File toAsciiFile(Grids_AbstractGridNumber g) {
        File directory = g.getDirectory();
        File file = new File(directory.getParentFile(), g.getName() + ".asc");
        return toAsciiFile(g, file);
    }

    /**
     * Writes g out to file in ESRI Asciigrid format and returns file.
     *
     * @param g TheAbstractGrid2DSquareCelll for export.
     * @param file The File to export to.
     * @return
     */
    public File toAsciiFile(Grids_AbstractGridNumber g, File file) {
        String noDataValue = "";
        if (g instanceof Grids_GridDouble) {
            noDataValue = "" + ((Grids_GridDouble) g).getNoDataValue();
        } else if (g instanceof Grids_GridInt) {
            noDataValue = "" + ((Grids_GridInt) g).getNoDataValue();
        }
        return toAsciiFile(g, file, noDataValue);
    }

    /**
     * Writes grid out to file in ESRI Asciigrid format and returns file.
     *
     * @param g Grid for export.
     * @param file The File to export to.
     * @param ndv The value to be used or substituted as a noDataValue for g.
     * @return
     */
    public File toAsciiFile(Grids_AbstractGridNumber g, File file, String ndv) {
        env.initNotToSwap();
        env.checkAndMaybeFreeMemory();
        try (PrintWriter pw = env.env.io.getPrintWriter(file, false)) {
            Grids_Dimensions dimensions;
            dimensions = g.getDimensions();
            long nrows = g.getNRows();
            long ncols = g.getNCols();
            long nrows_minus_1 = nrows - 1L;
            pw.println("ncols " + ncols);
            pw.println("nrows " + nrows);
            pw.println("xllcorner " + dimensions.getXMin().toString());
            pw.println("yllcorner " + dimensions.getYMin().toString());
            pw.println("cellsize " + dimensions.getCellsize().toString());
            long row;
            long col;
            int chunkRow0 = g.getChunkRow(nrows_minus_1);
            env.addToNotToSwap(g, chunkRow0);
            int chunkRow;
            if (g.getClass() == Grids_GridInt.class) {
                Grids_GridInt gridInt = (Grids_GridInt) g;
                int gridNoDataValue = gridInt.getNoDataValue();
                pw.println("NODATA_Value " + ndv);
                int value;
                for (row = nrows_minus_1; row >= 0; row--) {
                    chunkRow = g.getChunkRow(row);
                    if (chunkRow0 != chunkRow) {
                        env.removeFromNotToSwap(g, chunkRow0);
                        env.addToNotToSwap(g, chunkRow);
                        env.checkAndMaybeFreeMemory();
                        chunkRow0 = chunkRow;
                    }
                    for (col = 0; col < ncols; col++) {
                        //try {
                        value = gridInt.getCell(row, col);
                        if (value == gridNoDataValue) {
                            pw.print(ndv + " ");
                        } else {
                            pw.print(value + " ");
                        }
//                        } catch (OutOfMemoryError e) {
//                            g.env.clearMemoryReserve();
//                            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
//                                    g.getChunkRow(row, hoome),
//                                    g.getChunkCol(col, hoome));
//                            if (g.env.swapChunksExcept_Account(
//                                    g,
//                                    chunkID,
//                                    hoome) < 1L) {
//                                throw e;
//                            }
//                            g.env.initMemoryReserve(hoome);
//
//                            //pw.print( grid.getCell( row, col ) + " " );
//                            value = gridInt.getCell(
//                                    row,
//                                    col,
//                                    hoome);
//                            if (value == gridNoDataValue) {
//                                pw.print(noDataValue + " ");
//                            } else {
//                                pw.print(value + " ");
//                            }
//                        }
                    }
                    pw.println("");
                }
            } else {
                Grids_GridDouble gridDouble = (Grids_GridDouble) g;
                double gridNoDataValue = gridDouble.getNoDataValue();
                if (!Double.isFinite(gridNoDataValue)) {
                    System.out.println(
                            "Warning!!! noDataValue not finite in "
                            + "ESRIAsciigridExporter.toAsciiFile("
                            + gridDouble.getClass().getName()
                            + "(" + gridDouble.toString() + "),"
                            + "File(" + file.toString() + "))");
                }
                pw.println("NODATA_Value " + ndv);
                double value;
                for (row = nrows_minus_1; row >= 0; row--) {
                    for (col = 0; col < ncols; col++) {
                        chunkRow = g.getChunkRow(row);
                        if (chunkRow0 != chunkRow) {
                            env.removeFromNotToSwap(g, chunkRow0);
                            env.addToNotToSwap(g, chunkRow);
                            env.checkAndMaybeFreeMemory();
                            chunkRow0 = chunkRow;
                        }
//                        try {
                        //pw.print( grid.getCell( row, col ) + " " );
                        value = gridDouble.getCell(row, col);
                        if (!Double.isFinite(value)) {
                            pw.print(ndv + " ");
                            System.out.println(
                                    "Warning!!! Infinitity or NaN encountered at "
                                    + "row " + row + ","
                                    + " column " + col + ""
                                    + " set to noDataValue " + ndv + ".");
                        } else {
                            if (value == gridNoDataValue) {
                                pw.print(ndv + " ");
                            } else {
                                pw.print(value + " ");
                            }
                        }
//                        } catch (OutOfMemoryError e) {
//                            g.env.clearMemoryReserve();
//                            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
//                                    g.getChunkRow(row, hoome),
//                                    g.getChunkCol(col, hoome));
//                            if (g.env.swapChunksExcept_Account(
//                                    g,
//                                    chunkID,
//                                    hoome) < 1L) {
//                                throw e;
//                            }
//                            g.env.initMemoryReserve(hoome);
//                            value = gridDouble.getCell(
//                                    row,
//                                    col,
//                                    hoome);
//                            if (value == gridNoDataValue) {
//                                pw.print(noDataValue + " ");
//                            } else {
//                                pw.print(value + " ");
//                            }
//                        }
                    }
                    pw.println("");
                }
            }
            // Flush output
            pw.flush();
        }
        // No need to close pw as it implements AutoCloseable.
        return file;
    }
}
