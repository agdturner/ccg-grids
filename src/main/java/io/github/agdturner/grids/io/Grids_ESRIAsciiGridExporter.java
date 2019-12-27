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
package io.github.agdturner.grids.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import io.github.agdturner.grids.core.Grids_Dimensions;
import io.github.agdturner.grids.d2.grid.Grids_GridNumber;
import io.github.agdturner.grids.d2.grid.d.Grids_GridDouble;
import io.github.agdturner.grids.d2.grid.i.Grids_GridInt;
import io.github.agdturner.grids.core.Grids_Environment;
import io.github.agdturner.grids.core.Grids_Object;

/**
 * Class for exporting ESRI Asciigrid.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ESRIAsciiGridExporter extends Grids_Object {

    private static final long serialVersionUID = 1L;

    String DefaultNoDataValue = "-9999.0d";

    /**
     * Creates a new instance of ESRIAsciiGridExporter
     *
     * @param env
     */
    public Grids_ESRIAsciiGridExporter(Grids_Environment env) {
        super(env);
    }

    /**
     * Writes grid2DSquareCell out to file in ESRI Asciigrid format and returns
     * a the File to which it was written.
     *
     * @param g Grid for export.
     * @return
     */
    public Path toAsciiFile(Grids_GridNumber g) throws IOException, Exception, 
            ClassNotFoundException {
        Path file = Paths.get(g.getDirectory().getParent().toString(), 
                g.getName() + ".asc");
        return toAsciiFile(g, file);
    }

    /**
     * Writes g out to file in ESRI Asciigrid format and returns file.
     *
     * @param g TheAbstractGrid2DSquareCelll for export.
     * @param file The File to export to.
     * @return
     */
    public Path toAsciiFile(Grids_GridNumber g, Path file) 
            throws IOException, Exception, ClassNotFoundException {
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
    public Path toAsciiFile(Grids_GridNumber g, Path file, String ndv) 
            throws IOException, Exception, ClassNotFoundException {
        env.initNotToClear();
        env.checkAndMaybeFreeMemory();
        try (PrintWriter pw = env.env.io.getPrintWriter(file, false)) {
            Grids_Dimensions d = g.getDimensions();
            long nrows = g.getNRows();
            long ncols = g.getNCols();
            long nrows_minus_1 = nrows - 1L;
            pw.println("ncols " + ncols);
            pw.println("nrows " + nrows);
            pw.println("xllcorner " + d.getXMin().toString());
            pw.println("yllcorner " + d.getYMin().toString());
            pw.println("cellsize " + d.getCellsize().toString());
            long row;
            long col;
            int chunkRow0 = g.getChunkRow(nrows_minus_1);
            env.addToNotToClear(g, chunkRow0);
            int chunkRow;
            if (g.getClass() == Grids_GridInt.class) {
                Grids_GridInt gridInt = (Grids_GridInt) g;
                int gridNoDataValue = gridInt.getNoDataValue();
                pw.println("NODATA_Value " + ndv);
                int value;
                for (row = nrows_minus_1; row >= 0; row--) {
                    chunkRow = g.getChunkRow(row);
                    if (chunkRow0 != chunkRow) {
                        env.removeFromNotToClear(g, chunkRow0);
                        env.addToNotToClear(g, chunkRow);
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
//                            if (g.env.swapChunksExcept(
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
                            env.removeFromNotToClear(g, chunkRow0);
                            env.addToNotToClear(g, chunkRow);
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
//                            if (g.env.swapChunksExcept(
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
