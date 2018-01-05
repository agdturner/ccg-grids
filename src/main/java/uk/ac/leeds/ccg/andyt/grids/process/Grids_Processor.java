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

import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Time;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleMapFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntMapFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridIntFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkIntFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_AbstractGridNumberStats;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridDoubleStats;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridDoubleStatsNotUpdated;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridIntStats;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridIntStatsNotUpdated;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

/**
 * A class holding methods for processing individual or multiple grids.
 */
public class Grids_Processor extends Grids_Object {

    /**
     * For storing the start time of the processing.
     */
    public final long StartTime;

    /**
     * The Log for recording progress and information about the processing.
     */
    protected PrintWriter Log;

    /**
     * The Log indentation (how many spaces before a Log message line is
     * output).
     */
    protected int LogIndentation;

    /**
     * Workspace directory for the processing.
     */
    protected File Directory;

    /**
     * Grids_AbstractGridChunkIntFactory
     */
    public Grids_AbstractGridChunkIntFactory DefaultGridChunkIntFactory;

    /**
     * Grids_GridChunkIntFactory
     */
    public Grids_GridChunkIntFactory GridChunkIntFactory;

    /**
     * Grids_GridChunkIntArrayFactory
     */
    public Grids_GridChunkIntArrayFactory GridChunkIntArrayFactory;

    /**
     * Grids_GridChunkIntMapFactory
     */
    public Grids_GridChunkIntMapFactory GridChunkIntMapFactory;

    /**
     * Grids_GridIntFactory
     */
    public Grids_GridIntFactory GridIntFactory;

    /**
     * Grids_AbstractGridChunkDoubleFactory
     */
    public Grids_AbstractGridChunkDoubleFactory DefaultGridChunkDoubleFactory;

    /**
     * Grids_GridChunkDoubleFactory
     */
    public Grids_GridChunkDoubleFactory GridChunkDoubleFactory;

    /**
     * Grids_GridChunkDoubleArrayFactory
     */
    public Grids_GridChunkDoubleArrayFactory GridChunkDoubleArrayFactory;

    /**
     * Grids_GridChunkDoubleMapFactory
     */
    public Grids_GridChunkDoubleMapFactory GridChunkDoubleMapFactory;

    /**
     * Grids_GridDoubleFactory
     */
    public Grids_GridDoubleFactory GridDoubleFactory;

    /**
     * Grids_GridDoubleStats
     */
    public Grids_GridDoubleStats GridDoubleStatistics;

    /**
     * Grids_GridDoubleStatsNotUpdated
     */
    public Grids_GridDoubleStatsNotUpdated GridDoubleStatisticsNotUpdated;

    /**
     * Grids_GridIntStats
     */
    public Grids_GridIntStats GridIntStatistics;

    /**
     * Grids_GridIntStatsNotUpdated
     */
    public Grids_GridIntStatsNotUpdated GridIntStatisticsNotUpdated;

    protected Grids_Processor() {
        StartTime = System.currentTimeMillis();
    }

    /*
     * Creates a new instance of Grids_Processor.
     **/
    public Grids_Processor(
            Grids_Environment ge) {
        this(ge,
                new File(ge.getDirectory(),
                        "Grids_Processor"));
    }

    /**
     * Creates a new instance of Grids_Processor. The Log file in directory will
     * be overwritten if appendToLogFile is false.
     *
     * @param ge
     * @param directory
     */
    public Grids_Processor(
            Grids_Environment ge,
            File directory) {
        super(ge);
        StartTime = System.currentTimeMillis();
        File logFile;
        Directory = directory;
        if (!Directory.exists()) {
            Directory.mkdirs();
        }
        logFile = new File(Directory, "log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Grids_Processor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Log = Generic_StaticIO.getPrintWriter(logFile, true);
        LogIndentation = 0;
        log(LogIndentation, this.getClass().getName() + " set up "
                + Generic_Time.getTime(StartTime));
        initFactories();
    }

    /**
     * Initialises factories.
     */
    private void initFactories() {
        initChunkFactories();
        Grids_Files gf;
        gf = ge.getFiles();
        GridIntFactory = new Grids_GridIntFactory(
                ge,
                gf.getGeneratedGridIntDir(),
                GridChunkIntFactory,
                DefaultGridChunkIntFactory,
                512,
                512);
        GridDoubleFactory = new Grids_GridDoubleFactory(
                ge,
                gf.getGeneratedGridDoubleDir(),
                GridChunkDoubleFactory,
                DefaultGridChunkDoubleFactory,
                512,
                512);
        initGridStatistics();
    }

    /**
     * Initialises chunk Factories.
     */
    private void initChunkFactories() {
        GridChunkIntArrayFactory = new Grids_GridChunkIntArrayFactory();
        GridChunkIntMapFactory = new Grids_GridChunkIntMapFactory();
        GridChunkIntFactory = new Grids_GridChunkIntFactory(Integer.MIN_VALUE);
        DefaultGridChunkIntFactory = GridChunkIntArrayFactory;
        GridChunkDoubleArrayFactory = new Grids_GridChunkDoubleArrayFactory();
        GridChunkDoubleMapFactory = new Grids_GridChunkDoubleMapFactory();
        GridChunkDoubleFactory = new Grids_GridChunkDoubleFactory(-Double.MAX_VALUE);
        DefaultGridChunkDoubleFactory = GridChunkDoubleArrayFactory;
    }

    /**
     * Initialises Stats.
     */
    private void initGridStatistics() {
        GridDoubleStatistics = new Grids_GridDoubleStats(ge);
        GridDoubleStatisticsNotUpdated = new Grids_GridDoubleStatsNotUpdated(ge);
        GridIntStatistics = new Grids_GridIntStats(ge);
        GridIntStatisticsNotUpdated = new Grids_GridIntStatsNotUpdated(ge);
    }

    /**
     * Returns a copy of StartTime.
     *
     * @return
     */
    public long getTime0() {
        return StartTime;
    }

    /**
     * Returns a copy of Directory
     *
     * @return
     */
    public File getDirectory() {
        return new File(Directory.toString());
    }

    /**
     * Changes Directory, GridDoubleFactory.Directory, GridIntFactory.Directory
     * to Directory. Does not copy the logfile from the existing Directory. To
     * do this use: setDirectory( Directory, true )
     *
     * @param directory The Directory to change to.
     */
    protected void setDirectory(
            File directory) {
        Directory = directory;
        GridDoubleFactory.setDirectory(directory);
        GridIntFactory.setDirectory(directory);
    }

    /**
     * Changes Directory to that passed in if it can be created. If copyLogFile
     * is true, this copies the logfile from the existing Directory and sets up
     * the Log to append in the new location.
     *
     * @param directory The Directory to change to.
     * @param copyLogFile
     */
    public void setDirectory(
            File directory,
            boolean copyLogFile) {
//            boolean mkdirSuccess = false;
        File newLog = new File(directory, "log.txt");
        try {
            if (!directory.exists()) {
//                    mkdirSuccess = directory.mkdir();
                if (copyLogFile) {
                    copyAndSetUpNewLog(newLog);
                } else {
                    newLog.createNewFile();
                    Log = Generic_StaticIO.getPrintWriter(newLog, true);
                }
            } else {
                if (!newLog.exists()) {
                    newLog.createNewFile();
                    Log = Generic_StaticIO.getPrintWriter(newLog, true);
                } else {
                    if (copyLogFile) {
                        Log = Generic_StaticIO.getPrintWriter(newLog, true);
                    } else {
                        Log = Generic_StaticIO.getPrintWriter(newLog, false);
                    }
                }
            }
        } catch (IOException ioe0) {
            System.err.println(ioe0.getMessage());
            //ioe0.printStackTrace();
        }
        Directory = directory;
        GridDoubleFactory.setDirectory(directory);
        GridIntFactory.setDirectory(directory);
    }

    /**
     * Copies and sets up a new Log.
     *
     * @param newLog
     * @throws java.io.IOException
     */
    public void copyAndSetUpNewLog(
            File newLog)
            throws IOException {
        Log.flush();
        Log.close();
        File workspace = getDirectory();
        File oldLog = new File(workspace, "log.txt");
        BufferedInputStream bis;
        bis = Generic_StaticIO.getBufferedInputStream(oldLog);
        BufferedOutputStream bos;
        bos = Generic_StaticIO.getBufferedOutputStream(newLog);
        for (int i = 0; i < oldLog.length(); i++) {
            bos.write(bis.read());
        }
        bos.flush();
        bos.close();
        bis.close();
        Log = Generic_StaticIO.getPrintWriter(newLog, true);
        log(0, "log file copied from " + oldLog.toString() + " "
                + Calendar.getInstance().toString());
        ge.checkAndMaybeFreeMemory();
    }

    /**
     * Writes string to Log file and the console (standard output) indenting
     * string by LogIndentation amount of white-space.
     *
     * @param logIndentation The indentation of string.
     * @param string The message to Log.
     */
    public final void log(
            int logIndentation,
            String string) {
        if (string.endsWith("}")) {
            logIndentation--;
            LogIndentation--;
        }
        for (int i = 0; i < logIndentation; i++) {
            System.out.print(" ");
            Log.write(" ");
        }
        Log.write(string);
        Log.write(System.getProperty("line.separator"));
        Log.flush();
        if (string.endsWith("{")) {
            LogIndentation++;
        }
    }

    /**
     * Returns the distance between a pair of coordinates.
     *
     * @param x1 The x coordinate of one point.
     * @param y1 The y coordinate of one point.
     * @param x2 The x coordinate of another point.
     * @param y2 The y coordinate of another point.
     * @return
     */
    protected final double distance(
            double x1,
            double y1,
            double x2,
            double y2) {
        return Math.hypot(x1 - x2, y1 - y2);
    }

    /**
     * Returns the clockwise angle in radians to the y axis of the line from
     * (x1,y1) to (x2,y2).
     *
     * @param x1 The x coordinate of one point.
     * @param y1 The y coordinate of one point.
     * @param x2 The x coordinate of another point.
     * @param y2 The y coordinate of another point.
     * @return
     */
    protected final double angle(
            double x1,
            double y1,
            double x2,
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
     * Modifies grid by setting to grid.noDataValue those cells coincident with
     * mask.noDataValue cells. Warning!!! The grid and mask are assumed to be
     * coincident have the same origin and the same chunk structure. @TODO add
     * flexibility so the mask can have a different chunk structure to g.
     *
     * @param g The Grids_AbstractGridNumber that the mask will be applied to.
     * @param mask The Grids_AbstractGridNumber to use as a mask.
     */
    public void mask(
            Grids_AbstractGridNumber g,
            Grids_AbstractGridNumber mask) {
        ge.checkAndMaybeFreeMemory();
        ge.getGrids().add(g);
        ge.getGrids().add(mask);
        Grids_2D_ID_int chunkID;
        int chunkNRows;
        int chunkNCols;
        int chunkRow;
        int chunkCol;
        int cellRow;
        int cellCol;
        long row;
        long col;
        if (g instanceof Grids_GridInt) {
            Grids_GridInt grid = (Grids_GridInt) g;
            int noDataValue = grid.getNoDataValue();
            if (mask instanceof Grids_GridInt) {
                Grids_GridInt maskInt;
                maskInt = (Grids_GridInt) mask;
                int maskNoDataValue = maskInt.getNoDataValue();
                int value;
                Iterator ite = maskInt.iterator();
                Grids_AbstractGridChunkInt maskIntChunk;
                while (ite.hasNext()) {
                    maskIntChunk = (Grids_AbstractGridChunkInt) ite.next();
                    chunkID = maskIntChunk.getChunkID();
                    ge.addToNotToSwap(g, chunkID);
                    ge.addToNotToSwap(mask, chunkID);
                    ge.checkAndMaybeFreeMemory();
                    chunkNRows = maskInt.getChunkNRows(chunkID);
                    chunkNCols = maskInt.getChunkNCols(chunkID);
                    chunkRow = chunkID.getRow();
                    chunkCol = chunkID.getCol();
                    for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                        for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                            value = maskIntChunk.getCell(cellRow, cellCol);
                            if (value == maskNoDataValue) {
                                row = ((long) chunkRow * (long) chunkNRows)
                                        + (long) cellRow;
                                col = ((long) chunkCol * (long) chunkNCols)
                                        + (long) cellCol;
                                grid.setCell(row, col, noDataValue);
                            }
                        }
                    }
                    ge.removeFromNotToSwap(g, chunkID);
                    ge.removeFromNotToSwap(mask, chunkID);
                }
            } else {
                // ( mask.getClass() == Grids_GridDouble.class )
                Grids_GridDouble maskDouble;
                maskDouble = (Grids_GridDouble) mask;
                double maskNoDataValue = maskDouble.getNoDataValue();
                double value;
                Iterator ite = maskDouble.iterator();
                Grids_AbstractGridChunkDouble maskChunk;
                while (ite.hasNext()) {
                    maskChunk = (Grids_AbstractGridChunkDouble) ite.next();
                    chunkID = maskChunk.getChunkID();
                    ge.addToNotToSwap(g, chunkID);
                    ge.addToNotToSwap(mask, chunkID);
                    ge.checkAndMaybeFreeMemory();
                    chunkNRows = maskDouble.getChunkNRows(chunkID);
                    chunkNCols = maskDouble.getChunkNCols(chunkID);
                    chunkRow = chunkID.getRow();
                    chunkCol = chunkID.getCol();
                    for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                        for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                            value = maskChunk.getCell(cellRow, cellCol);
                            if (value == maskNoDataValue) {
                                row = ((long) chunkRow * (long) chunkNRows)
                                        + (long) cellRow;
                                col = ((long) chunkCol * (long) chunkNCols)
                                        + (long) cellCol;
                                grid.setCell(row, col, noDataValue);
                            }
                        }
                    }
                    ge.removeFromNotToSwap(g, chunkID);
                    ge.removeFromNotToSwap(mask, chunkID);
                }
            }
        } else {
            Grids_GridDouble grid = (Grids_GridDouble) g;
            double resultNoDataValue;
            resultNoDataValue = grid.getNoDataValue();
            if (mask.getClass() == Grids_GridInt.class) {
                Grids_GridInt maskInt = (Grids_GridInt) mask;
                int maskNoDataValue = maskInt.getNoDataValue();
                int value;
                Iterator iterator = maskInt.iterator();
                Grids_AbstractGridChunkInt maskChunk;
                while (iterator.hasNext()) {
                    maskChunk = (Grids_AbstractGridChunkInt) iterator.next();
                    chunkID = maskChunk.getChunkID();
                    ge.addToNotToSwap(g, chunkID);
                    ge.addToNotToSwap(mask, chunkID);
                    ge.checkAndMaybeFreeMemory();
                    chunkNRows = maskInt.getChunkNRows(chunkID);
                    chunkNCols = maskInt.getChunkNCols(chunkID);
                    chunkRow = chunkID.getRow();
                    chunkCol = chunkID.getCol();
                    for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                        for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                            value = maskChunk.getCell(cellRow, cellCol);
                            if (value == maskNoDataValue) {
                                row = ((long) chunkRow * (long) chunkNRows)
                                        + (long) cellRow;
                                col = ((long) chunkCol * (long) chunkNCols)
                                        + (long) cellCol;
                                grid.setCell(row, col, resultNoDataValue);
                            }
                        }
                    }
                    ge.removeFromNotToSwap(g, chunkID);
                    ge.removeFromNotToSwap(mask, chunkID);
                }
            } else {
                // ( mask.getClass() == Grids_GridDouble.class )
                Grids_GridDouble maskDouble = (Grids_GridDouble) mask;
                double maskNoDataValue = maskDouble.getNoDataValue();
                double value;
                Iterator ite = maskDouble.getChunkIDs().iterator();
                Grids_AbstractGridChunkDouble maskChunk;
                while (ite.hasNext()) {
                    maskChunk = (Grids_AbstractGridChunkDouble) mask.getChunk(
                            (Grids_2D_ID_int) ite.next());
                    chunkID = maskChunk.getChunkID();
                    ge.addToNotToSwap(g, chunkID);
                    ge.addToNotToSwap(mask, chunkID);
                    ge.checkAndMaybeFreeMemory();
                    chunkNRows = maskDouble.getChunkNRows(chunkID);
                    chunkNCols = maskDouble.getChunkNCols(chunkID);
                    chunkRow = chunkID.getRow();
                    chunkCol = chunkID.getCol();
                    for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                        for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                            value = maskChunk.getCell(cellRow, cellCol);
                            if (value == maskNoDataValue) {
                                row = ((long) chunkRow * (long) chunkNRows)
                                        + (long) cellRow;
                                col = ((long) chunkCol * (long) chunkNCols)
                                        + (long) cellCol;
                                grid.setCell(row, col, resultNoDataValue);
                            }
                        }
                    }
                    ge.removeFromNotToSwap(g, chunkID);
                    ge.removeFromNotToSwap(mask, chunkID);
                }
            }
        }
        ge.checkAndMaybeFreeMemory();
    }

    /**
     * Modifies grid with the values of cells in the range [min,max] set to its
     * g. (Existing noDataValue cells in grid remain as noDataValue.)
     *
     * @param g The Grids_GridDouble to be masked.
     * @param min The minimum value in the range.
     * @param max The maximum value in the range.
     */
    public void mask(
            Grids_AbstractGridNumber g,
            double min,
            double max) {
        ge.getGrids().add(g);
        ge.checkAndMaybeFreeMemory();
        int cellRow;
        int cellCol;
        int chunkNRows;
        int chunkNCols;
        Grids_2D_ID_int chunkID;
        if (g.getClass() == Grids_GridInt.class) {
            Grids_GridInt gi = (Grids_GridInt) g;
            int ndv = gi.getNoDataValue();
            int value;
            Iterator ite = gi.iterator();
            Grids_AbstractGridChunkInt chunk;
            while (ite.hasNext()) {
                chunk = (Grids_AbstractGridChunkInt) ite.next();
                chunkID = chunk.getChunkID();
                ge.addToNotToSwap(g, chunkID);
                ge.checkAndMaybeFreeMemory();
                chunkNRows = gi.getChunkNRows(chunkID);
                chunkNCols = gi.getChunkNCols(chunkID);
                for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                    for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                        value = gi.getCell(chunk, cellRow, cellCol);
                        if (value >= min && value <= max) {
                            gi.setCell(chunk, cellRow, cellCol, ndv);
                        }
                    }
                }
                ge.removeFromNotToSwap(g, chunkID);
            }
        } else {
            // ( grid.getClass() == Grids_GridDouble.class )
            Grids_GridDouble gd = (Grids_GridDouble) g;
            double ndv = gd.getNoDataValue();
            double value;
            Iterator iterator = g.iterator();
            Grids_AbstractGridChunkDouble gridChunk;
            //Iterator gridChunkIterator;
            while (iterator.hasNext()) {
                gridChunk = (Grids_AbstractGridChunkDouble) iterator.next();
                chunkID = gridChunk.getChunkID();
                ge.addToNotToSwap(g, chunkID);
                ge.checkAndMaybeFreeMemory();
                chunkNRows = g.getChunkNRows(chunkID);
                chunkNCols = g.getChunkNCols(chunkID);
                for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                    for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                        value = gd.getCell(gridChunk, cellRow, cellCol);
                        if (value >= min && value <= max) {
                            gd.setCell(gridChunk, cellRow, cellCol, ndv);
                        }
                    }
                }
                ge.removeFromNotToSwap(g, chunkID);
            }
        }
        //grid.setName( grid.getName() + "_mask" );
        ge.checkAndMaybeFreeMemory();
    }

    /**
     * @param g
     * @return a new Grids_GridDouble Values are either linearly rescaled into
     * the range [min,max]. Or some Log rescaling is done
     * @param type If type == null then a linear rescale is done. If type ==
     * "Log" then a Log rescale is done.
     * @param min The minimum value in the rescaled range.
     * @param max The maximum value in the rescaled range.
     */
    public Grids_GridDouble rescale(
            Grids_AbstractGridNumber g,
            String type,
            double min,
            double max) {
        if (g instanceof Grids_GridDouble) {
            return rescale((Grids_GridDouble) g, null, 0.0d, 255.0d);
        } else {
            return rescale((Grids_GridInt) g, null, 0.0d, 255.0d);
        }
    }

    /**
     * @param g
     * @return a new Grids_GridDouble Values are either linearly rescaled into
     * the range [min,max]. Or some Log rescaling is done
     * @param type If type == null then a linear rescale is done. If type ==
     * "Log" then a Log rescale is done.
     * @param min The minimum value in the rescaled range.
     * @param max The maximum value in the rescaled range.
     * @TODO Improve log rescaling implementation.
     */
    public Grids_GridDouble rescale(
            Grids_GridDouble g,
            String type,
            double min,
            double max) {
        Grids_GridDouble result;
        boolean hoome = ge.HOOME;
        ge.checkAndMaybeFreeMemory();
        ge.getGrids().add(g);
        long nrows = g.getNRows();
        long ncols = g.getNCols();
        int nChunkCols = g.getNChunkCols();
        int nChunkRows = g.getNChunkRows();
        int chunkNCols;
        int chunkNRows;
        double ndv = g.getNoDataValue();
        double range = max - min;
        Grids_AbstractGridNumberStats stats = g.getStats();
        double minGrid = stats.getMin(true).doubleValue();
        double maxGrid = stats.getMax(true).doubleValue();
        double rangeGrid = maxGrid - minGrid;
        double value;
        //outputGrid = GridDoubleFactory.create(grid);
        result = GridDoubleFactory.create(new File(Directory, "Rescaled"), g, 0,
                0, nrows - 1, ncols - 1);
//        System.out.println("NoDataValue " + result.getNoDataValue(hoome));
//        System.out.println("r.getCell(0L, 0L) " + result.getCell(0L, 0L, hoome));
        result.setName(g.getName());
        System.out.println(result.toString());
        ge.getGrids().add(result);
        int chunkRow;
        int chunkCol;
        int cellRow;
        int cellCol;
        Grids_AbstractGridChunkDouble gridChunk;
        Grids_AbstractGridChunkDouble resultChunk;
        Grids_2D_ID_int chunkID;
        if (type == null) {
            // if range of either input or output range is zero return min for all non noDataValues
            if (rangeGrid == 0.0d || range == 0.0d) {
                // Better to go through chunks rather than rows. Though it 
                // does assume that the structure of the grid and outputGrid 
                // are the same.
                for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
                    chunkNRows = g.getChunkNRows(chunkRow);
                    for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                        chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                        ge.addToNotToSwap(g, chunkID);
                        ge.addToNotToSwap(result, chunkID);
                        ge.checkAndMaybeFreeMemory();
                        chunkNCols = g.getChunkNCols(chunkCol);
                        gridChunk = g.getChunk(chunkID);
                        resultChunk = result.getChunk(chunkID);
                        for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                            for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                                value = gridChunk.getCell(cellRow, cellCol);
                                if (value != ndv) {
                                    result.setCell(resultChunk, cellRow,
                                            cellCol, min);
                                }
                            }
                        }
                    }
                }
            } else {
                // Better to go through chunks rather than rows. Though this 
                // assumes that the structure of the grid and outputGrid are the
                // same.
                double v;
                for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
                    chunkNRows = g.getChunkNRows(chunkRow);
                    for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                        chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                        ge.addToNotToSwap(g, chunkID);
                        ge.addToNotToSwap(result, chunkID);
                        ge.checkAndMaybeFreeMemory();
                        chunkNCols = g.getChunkNCols(chunkCol);
                        gridChunk = g.getChunk(chunkID);
                        resultChunk = result.getChunk(chunkID);
                        for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                            for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                                value = gridChunk.getCell(cellRow, cellCol);
                                if (value != ndv) {
                                    v = (((value - minGrid)
                                            / rangeGrid) * range) + min;
                                    result.setCell(resultChunk, cellRow,
                                            cellCol, v);
                                }
                            }
                        }
                    }
                }
            }
            result.setName(g.getName() + "_linearRescale");
            ge.checkAndMaybeFreeMemory(hoome);
        } else {
            // @TODO this implementation could be much improved...
            int row;
            int col;
            if (type.equalsIgnoreCase("log")) {
                result = rescale(result, null, 1.0d, 1000000.0d);
                // Probably better to do this by chunks
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        value = g.getCell(row, col);
                        if (value != ndv) {
                            result.setCell(row, col, Math.log(value));
                        }
                    }
                }
                result = rescale(result, null, min, max);
                result.setName(g.getName() + "_logRescale");
                ge.checkAndMaybeFreeMemory(hoome);
            } else {
                try {
                    throw new Exception("Unable to rescale: type " + type
                            + "not recognised.");
                } catch (Exception ex) {
                    Logger.getLogger(Grids_Processor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    /**
     * @param g
     * @return a new Grids_GridDouble Values are either linearly rescaled into
     * the range [min,max]. Or some Log rescaling is done
     * @param type If type == null then a linear rescale is done. If type ==
     * "Log" then a Log rescale is done.
     * @param min The minimum value in the rescaled range.
     * @param max The maximum value in the rescaled range.
     * @TODO Improve log rescaling implementation.
     */
    public Grids_GridDouble rescale(
            Grids_GridInt g,
            String type,
            double min,
            double max) {
        ge.checkAndMaybeFreeMemory();
        ge.getGrids().add(g);
        long nrows = g.getNRows();
        long ncols = g.getNCols();
        int nChunkCols = g.getNChunkCols();
        int nChunkRows = g.getNChunkCols();
        int ndv = g.getNoDataValue();
        double range = max - min;
        Grids_AbstractGridNumberStats stats = g.getStats();
        double minGrid = stats.getMin(true).doubleValue();
        double maxGrid = stats.getMax(true).doubleValue();
        double rangeGrid = maxGrid - minGrid;
        double value;
        double v;
        Grids_GridDouble outputGrid;
        outputGrid = (Grids_GridDouble) GridDoubleFactory.create(g);
        outputGrid.setName(g.getName());
        ge.getGrids().add(outputGrid);
        int chunkNCols;
        int chunkNRows;
        int chunkRow;
        int chunkCol;
        int cellRow;
        int cellCol;
        if (type == null) {
            /**
             * If range of either input or output range is zero return min for
             * all non noDataValues.
             */
            if (rangeGrid == 0.0d || range == 0.0d) {
                /**
                 * Better to go through chunks rather than rows. Though it does
                 * assume that the structure of the grid and outputGrid are the
                 * same.
                 */
                for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
                    for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                        Grids_2D_ID_int chunkID;
                        chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                        ge.addToNotToSwap(g, chunkID);
                        ge.addToNotToSwap(outputGrid, chunkID);
                        ge.checkAndMaybeFreeMemory();
                        chunkNCols = g.getChunkNCols(chunkCol);
                        chunkNRows = g.getChunkNRows(chunkRow);
                        Grids_AbstractGridChunkInt gridChunk;
                        gridChunk = g.getChunk(chunkID);
                        Grids_AbstractGridChunkDouble outputGridChunk;
                        outputGridChunk = outputGrid.getChunk(chunkID);
                        for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                            for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                                value = gridChunk.getCell(cellRow, cellCol);
                                if (value != ndv) {
                                    outputGrid.setCell(outputGridChunk, cellRow,
                                            cellCol, min);
                                }
                            }
                        }
                        ge.removeFromNotToSwap(g, chunkID);
                        ge.removeFromNotToSwap(outputGrid, chunkID);
                        ge.checkAndMaybeFreeMemory();
                    }
                }
            } else {
                /**
                 * Better to go through chunks rather than rows. Though it does
                 * assume that the structure of the grid and outputGrid are the
                 * same.
                 */
                for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
                    for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                        Grids_2D_ID_int chunkID;
                        chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                        ge.addToNotToSwap(g, chunkID);
                        ge.addToNotToSwap(outputGrid, chunkID);
                        ge.checkAndMaybeFreeMemory();
                        chunkNCols = g.getChunkNCols(chunkCol);
                        chunkNRows = g.getChunkNRows(chunkRow);
                        Grids_AbstractGridChunkInt gridChunk;
                        gridChunk = g.getChunk(chunkID);
                        Grids_AbstractGridChunkDouble outputGridChunk;
                        outputGridChunk = outputGrid.getChunk(chunkID);
                        for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                            for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                                value = gridChunk.getCell(cellRow, cellCol);
                                if (value != ndv) {
                                    v = (((value - minGrid) / rangeGrid)
                                            * range) + min;
                                    outputGrid.setCell(outputGridChunk,
                                            cellRow, cellCol, v);
                                }
                            }
                        }
                        ge.removeFromNotToSwap(g, chunkID);
                        ge.removeFromNotToSwap(outputGrid, chunkID);
                        ge.checkAndMaybeFreeMemory();
                    }
                }
            }
            outputGrid.setName(g.getName() + "_linearRescale");
            ge.checkAndMaybeFreeMemory();
        } else {
            // @TODO this is not a good implementation
            if (type.equalsIgnoreCase("log")) {
                outputGrid = rescale(outputGrid, null, 1.0d, 1000000.0d);
                long row;
                long col;
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        value = g.getCell(row, col);
                        if (value != ndv) {
                            outputGrid.setCell(row, col, Math.log(value));
                        }
                    }
                }
                outputGrid = rescale(outputGrid, null, min, max);
                //grid.setName( grid.getName() + "_logRescale" );
                ge.checkAndMaybeFreeMemory();
            } else {
                System.out.println("Unable to rescale: type " + type
                        + "not recognised. Returning a Grid2DSquareCellDouble.");
            }
        }
        return outputGrid;
    }

    /**
     * Modifies grid so value of cells with CellIDs in cellIDs are set to a
     * value a little bit larger.
     *
     * @param g The Grids_GridDouble to be processed.
     * @param cellIDs The CellIDs of the cells to be processed.
     */
    public void setLarger(Grids_GridDouble g, HashSet cellIDs) {
        Grids_2D_ID_long cellID;
        double noDataValue = g.getNoDataValue();
        Iterator iterator1 = cellIDs.iterator();
        double thisValue;
        while (iterator1.hasNext()) {
            cellID = (Grids_2D_ID_long) iterator1.next();
            thisValue = g.getCell(cellID.getRow(), cellID.getCol());
            if (thisValue != noDataValue) {
                g.setCell(cellID.getRow(), cellID.getCol(),
                        Grids_Utilities.getLarger(thisValue));
            }
        }
    }

    /**
     * Modifies grid so value of cells with CellIDs in _CellIDs are set to a
     * value a little bit smaller.
     *
     * @param g The Grids_GridDouble to be processed.
     * @param cellIDs The CellIDs of the cells to be processed.
     */
    public void setSmaller(Grids_GridDouble g, HashSet cellIDs) {
        Grids_2D_ID_long cellID;
        double noDataValue = g.getNoDataValue();
        Iterator iterator1 = cellIDs.iterator();
        double thisValue;
        while (iterator1.hasNext()) {
            cellID = (Grids_2D_ID_long) iterator1.next();
            thisValue = g.getCell(cellID.getRow(), cellID.getCol());
            if (thisValue != noDataValue) {
                g.setCell(cellID.getRow(), cellID.getCol(),
                        Grids_Utilities.getSmaller(thisValue));
            }
        }
    }

    /**
     * Adds value to grid for cells with CellID in _CellIDs
     *
     * @param grid The Grids_GridDouble to be processed
     * @param cellIDs A HashSet containing CellIDs.
     * @param value The value to be added.
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then swap operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public void addToGrid(
            Grids_GridDouble grid,
            HashSet cellIDs,
            double value,
            boolean hoome) {
        try {
            ge.getGrids().add(grid);
            Iterator iterator1 = cellIDs.iterator();
            while (iterator1.hasNext()) {
                //grid.addToCell( ( CellID ) iterator1.next(), value );
                Grids_2D_ID_long cellID = (Grids_2D_ID_long) iterator1.next();
                if (cellID != null) {
                    grid.addToCell(cellID, value);
                }
            }
            ge.checkAndMaybeFreeMemory(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HOOMEF)) {
                    throw e;
                }
                ge.initMemoryReserve();
                addToGrid(grid, cellIDs, value, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Adds value to every cell of grid.
     *
     * @param grid The Grids_GridDouble to be processed
     * @param value The value to be added
     */
    public void addToGrid(
            Grids_GridDouble grid,
            double value) {
        ge.getGrids().add(grid);
        long nrows = grid.getNRows();
        long ncols = grid.getNCols();
        long row;
        long col;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                grid.addToCell(row, col, value);
            }
        }
        ge.checkAndMaybeFreeMemory();
    }

    /**
     * Adds value to grid for cells with CellID in _CellIDs
     *
     * @param grid The Grids_GridDouble to be processed
     * @param cellIDs Array of CellIDs.
     * @param value The value to be added.
     */
    public void addToGrid(
            Grids_GridDouble grid,
            Grids_2D_ID_long[] cellIDs,
            double value) {
        ge.getGrids().add(grid);
        for (Grids_2D_ID_long cellID : cellIDs) {
            grid.addToCell(cellID.getRow(), cellID.getCol(), value);
        }
        ge.checkAndMaybeFreeMemory();
    }

    /**
     * Add gridToAdd to grid with values from gridToAdd multiplied by weight.
     *
     * @param g The Grids_GridDouble to be processed/modified.
     * @param g2 The Grids_GridDouble from which values are added.
     * @param weight The value gridToAdd values are multiplied by.
     */
    public void addToGrid(
            Grids_GridDouble g,
            Grids_GridDouble g2,
            double weight) {
        ge.getGrids().add(g);
        ge.getGrids().add(g2);
        addToGrid(g, g2, 0L, 0L, g2.getNRows() - 1L,
                g2.getNCols() - 1L, weight);
    }

    /**
     * Add gridToAdd to grid with values from gridToAdd multiplied by weight.
     * Only values of gridToAdd with row index between startRowIndex and
     * endRowIndex, and column index between startColIndex and endColIndex are
     * added.
     *
     * @param g The Grids_GridDouble to be processed.
     * @param g2 The Grids_GridDouble from which values are added.
     * @param startRow The index of the first row from which gridToAdd values
     * are added.
     * @param startCol the index of the first column from which gridToAdd values
     * are added.
     * @param endRow the index of the final row from which gridToAdd values are
     * added.
     * @param endCol the index of the final column from which gridToAdd values
     * are added.
     * @param weight The value gridToAdd values are multiplied by.
     */
    public void addToGrid(
            Grids_GridDouble g,
            Grids_GridDouble g2,
            long startRow,
            long startCol,
            long endRow,
            long endCol,
            double weight) {
        ge.getGrids().add(g);
        ge.getGrids().add(g2);
        Grids_Dimensions dimensions = g2.getDimensions();
        BigDecimal xMin;
        BigDecimal yMin;
        BigDecimal cellsize;
        cellsize = dimensions.getCellsize();
        xMin = dimensions.getXMin();
        yMin = dimensions.getYMin();
        BigDecimal[] dimensionConstraints = new BigDecimal[5];
        dimensionConstraints[1] = xMin.add(
                new BigDecimal(startCol).multiply(cellsize));
        dimensionConstraints[2] = yMin.add(
                new BigDecimal(startRow).multiply(cellsize));
        dimensionConstraints[3] = xMin.add(
                new BigDecimal(endCol - startCol + 1L).multiply(cellsize));
        dimensionConstraints[4] = yMin.add(
                new BigDecimal(endRow - startRow + 1L).multiply(cellsize));
        addToGrid(g, g2, startRow, startCol, endRow, endCol,
                dimensionConstraints, weight);
        ge.checkAndMaybeFreeMemory();
    }

    /**
     * Returns a Grids_GridDouble with values of grid added with values from
     * gridToAdd (with row index between startRowIndex, endRowIndex and column
     * index between startColIndex, endColIndex) multiplied by weight.
     *
     * @param g The Grids_GridDouble to be processed.
     * @param g2 The Grids_GridDouble from which values are added.
     * @param startRow The index of the first row from which gridToAdd values
     * are added.
     * @param startCol The index of the first column from which gridToAdd values
     * are added.
     * @param endRow The index of the final row from which gridToAdd values are
     * added.
     * @param endCol The index of the final column from which gridToAdd values
     * are added.
     * @param constraintDimensions
     * @param weight The value gridToAdd values are multiplied by.
     *
     * @todo work needed to handle OutOfMemoryErrors...
     */
    public void addToGrid(
            Grids_GridDouble g,
            Grids_GridDouble g2,
            long startRow,
            long startCol,
            long endRow,
            long endCol,
            BigDecimal[] constraintDimensions,
            double weight) {
        ge.checkAndMaybeFreeMemory();
        ge.getGrids().add(g);
        ge.getGrids().add(g2);
        long nrows = g.getNRows();
        long ncols = g.getNCols();
        double noDataValue = g.getNoDataValue();
        Grids_Dimensions gDimensions = g.getDimensions();
        double g2NoDataValue = g2.getNoDataValue();
        Grids_Dimensions g2Dimensions = g2.getDimensions();
        Grids_GridDoubleFactory gf;
        gf = new Grids_GridDoubleFactory(
                ge,
                ge.getFiles().getGeneratedGridDoubleDir(),
                GridChunkDoubleFactory,
                DefaultGridChunkDoubleFactory,
                g.getChunkNCols(),
                g.getChunkNRows());
        if ((constraintDimensions[1].compareTo(gDimensions.getXMax()) == 1)
                || (constraintDimensions[3].compareTo(gDimensions.getXMin()) == -1)
                || (constraintDimensions[2].compareTo(gDimensions.getYMax()) == 1)
                || (constraintDimensions[4].compareTo(gDimensions.getYMin()) == -1)) {
            return;
        }
        BigDecimal g2Cellsize;
        BigDecimal gCellsize;
        BigDecimal g2HalfCellsize;
        BigDecimal gHalfCellsize;
        g2Cellsize = g2Dimensions.getCellsize();
        gCellsize = gDimensions.getCellsize();
        g2HalfCellsize = g2Dimensions.getHalfCellsize();
        gHalfCellsize = gDimensions.getHalfCellsize();
        if (g2Cellsize.compareTo(gCellsize) == -1) {
            throw new UnsupportedOperationException();
        } else {
            // If g2Cellsize is the same as gCellsize g and g2 align
            if ((g2Cellsize.compareTo(gCellsize) == 0)
                    && ((g2Dimensions.getXMin().remainder(gCellsize)).compareTo((gDimensions.getXMin().remainder(gCellsize))) == 0)
                    && ((g2Dimensions.getYMin().remainder(gCellsize)).compareTo((gDimensions.getYMin().remainder(gCellsize))) == 0)) {
                //println( "Grids Align!" );
                double x;
                double y;
                double value;
                long row;
                long col;
                // TODO: Control precision using xBigDecimal and yBigDecimal
                // rather than using x and y.
                for (row = startRow; row <= endRow; row++) {
                    ge.checkAndMaybeFreeMemory();
                    y = g2.getCellYDouble(row);
                    for (col = startCol; col <= endCol; col++) {
                        x = g2.getCellXDouble(col);
                        value = g2.getCell(row, col);
                        if (value != g2NoDataValue) {
                            if (value != 0.0d) {
                                g.addToCell(x, y, value * weight);
                            }
                        }
                    }
                }
                return;
            } else {
                // println("Intersection!!!!");
                // Need to intersect
                // TODO:
                // Clipping gridToAdd might improve matters here.
                // Check
                Grids_GridDouble tg1;
                Grids_GridDouble tg2;
                tg1 = (Grids_GridDouble) gf.create(nrows, ncols, gDimensions);
                tg2 = (Grids_GridDouble) gf.create(nrows, ncols, gDimensions);
                // TODO:
                // Check scale and rounding appropriate
                int scale = 324;
                int roundingMode = BigDecimal.ROUND_HALF_EVEN;
                BigDecimal g2CellsizeSquared;
                g2CellsizeSquared = g2Cellsize.multiply(g2Cellsize);
                double[] bounds;
                Grids_2D_ID_long cellID1;
                Grids_2D_ID_long cellID2;
                Grids_2D_ID_long cellID3;
                Grids_2D_ID_long cellID4;
                double d1;
                double d2;
                double d3;
                double d4;
                double x;
                double y;
                long row;
                long col;
                double areaProportion;
                double halfCellsize = g.getCellsizeDouble() / 2.0d;
                // TODO:
                // precision checking and use of BigDecimal?
                for (row = 0; row < nrows; row++) {
                    ge.checkAndMaybeFreeMemory();
                    for (col = 0; col < ncols; col++) {
                        bounds = g.getCellBoundsDoubleArray(halfCellsize, row, col);
                        x = g.getCellXDouble(col);
                        y = g.getCellYDouble(row);
                        cellID1 = g2.getCellID(bounds[0], bounds[3]);
                        cellID2 = g2.getCellID(bounds[2], bounds[3]);
                        cellID3 = g2.getCellID(bounds[0], bounds[1]);
                        cellID4 = g2.getCellID(bounds[2], bounds[1]);
                        d1 = g2.getCell(cellID1.getRow(), cellID1.getCol());
                        if (cellID1.equals(cellID2) && cellID2.equals(cellID3)) {
                            if (d1 != g2NoDataValue) {
                                areaProportion = (gCellsize.multiply(gCellsize).divide(g2CellsizeSquared, scale, roundingMode)).doubleValue();
                                tg1.addToCell(row, col, d1 * areaProportion);
                                tg2.addToCell(row, col, areaProportion);
                            }
                        } else {
                            d2 = g2.getCell(cellID2.getRow(), cellID2.getCol());
                            d3 = g2.getCell(cellID3.getRow(), cellID3.getCol());
                            d4 = g2.getCell(cellID4.getRow(), cellID4.getCol());
                            if (!g2.isInGrid(cellID1.getRow(), cellID1.getCol()) && d1 != g2NoDataValue) {
                                if (cellID1.equals(cellID2) || cellID1.equals(cellID3)) {
                                    if (cellID1.equals(cellID2)) {
                                        areaProportion = Math.abs(((BigDecimal.valueOf(bounds[3]).subtract(
                                                g2.getCellYBigDecimal(cellID1).subtract(
                                                        g2HalfCellsize)).multiply(gCellsize)).divide(
                                                g2CellsizeSquared, scale, roundingMode)).doubleValue());
                                    } else {
                                        areaProportion = Math.abs(((((g2.getCellXBigDecimal(cellID1).add(
                                                g2HalfCellsize)).subtract(BigDecimal.valueOf(bounds[0]))).multiply(
                                                gCellsize)).divide(g2CellsizeSquared, scale, roundingMode)).doubleValue());
                                    }
                                } else {
                                    areaProportion = Math.abs(((BigDecimal.valueOf(bounds[3]).subtract(g2.getCellYBigDecimal(cellID1).subtract(g2HalfCellsize))).multiply((g2.getCellXBigDecimal(cellID1).add(g2HalfCellsize.subtract(BigDecimal.valueOf(bounds[0])))).divide(g2CellsizeSquared, scale, roundingMode))).doubleValue());
                                }
                                tg1.addToCell(row, col, d1 * areaProportion);
                                tg2.addToCell(row, col, areaProportion);
                            }
                            if (!g2.isInGrid(cellID2) && d2 != g2NoDataValue) {
                                if (cellID2.equals(cellID1)) {
                                    if (cellID2.equals(cellID4)) {
                                        areaProportion = Math.abs((((BigDecimal.valueOf(bounds[2]).subtract(
                                                g2.getCellXBigDecimal(cellID2).subtract(
                                                        g2HalfCellsize))).multiply(gCellsize)).divide(
                                                g2CellsizeSquared, scale, roundingMode)).doubleValue());
                                    } else {
                                        areaProportion = Math.abs(((BigDecimal.valueOf(bounds[3]).subtract(
                                                g2.getCellYBigDecimal(cellID2).subtract(
                                                        g2HalfCellsize))).multiply(BigDecimal.valueOf(bounds[2]).subtract(
                                                        g2.getCellXBigDecimal(cellID2).subtract(
                                                                g2HalfCellsize))).divide(g2CellsizeSquared, scale, roundingMode)).doubleValue());
                                    }
                                    tg1.addToCell(row, col, d2 * areaProportion);
                                    tg2.addToCell(row, col, areaProportion);
                                }
                            }
                            if (!g2.isInGrid(cellID3) && d3 != g2NoDataValue) {
                                if (!cellID3.equals(cellID1)) {
                                    if (cellID3.equals(cellID4)) {
                                        areaProportion = Math.abs(((((g2.getCellYBigDecimal(cellID3).add(
                                                g2HalfCellsize)).subtract(BigDecimal.valueOf(bounds[1]))).multiply(
                                                gCellsize)).divide(g2CellsizeSquared, scale, roundingMode)).doubleValue());
                                    } else {
                                        areaProportion = Math.abs(((((g2.getCellYBigDecimal(cellID3).add(
                                                g2HalfCellsize)).subtract(BigDecimal.valueOf(bounds[1]))).multiply((g2.getCellXBigDecimal(cellID3).add(
                                                g2HalfCellsize)).subtract(BigDecimal.valueOf(bounds[0])))).divide(
                                                g2CellsizeSquared, scale, roundingMode)).doubleValue());
                                    }
                                    tg1.addToCell(row, col, d3 * areaProportion);
                                    tg2.addToCell(row, col, areaProportion);
                                }
                            }
                            if (!g2.isInGrid(cellID4) && d4 != g2NoDataValue) {
                                if (cellID4 != cellID2 && cellID4 != cellID3) {
                                    areaProportion = Math.abs(((((g2.getCellYBigDecimal(cellID4).add(g2HalfCellsize)).subtract(
                                            BigDecimal.valueOf(bounds[1]))).multiply(
                                            BigDecimal.valueOf(bounds[2]).subtract(
                                                    (g2.getCellXBigDecimal(cellID4)).subtract(
                                                            g2HalfCellsize)))).divide(
                                                    g2CellsizeSquared, scale, roundingMode)).doubleValue());
                                    tg1.addToCell(row, col, d4 * areaProportion);
                                    tg2.addToCell(row, col, areaProportion);
                                }
                            }
                        }
                    }
                }
                // The values are normalised by dividing the aggregate Grid sum by the proportion of cells with grid values.
                for (row = 0; row <= nrows; row++) {
                    ge.checkAndMaybeFreeMemory();
                    for (col = 0; col <= ncols; col++) {
                        d1 = tg2.getCell(row, col);
                        if (!(d1 == 0.0d || d1 == noDataValue)) {
                            g.addToCell(row, col,
                                    weight * tg1.getCell(row, col) / d1);
                        }
                    }
                }
            }
        }
        ge.checkAndMaybeFreeMemory();
    }

    /**
     * Returns grid with values added from a file.
     *
     * @param grid the Grids_GridDouble to be processed
     * @param file the file contining values to be added.
     * @param type the type of file. Supported types include "xyv", "xy", "idxy"
     */
    public void addToGrid(
            Grids_GridDouble grid,
            File file,
            String type) {
        ge.getGrids().add(grid);
        if (type.equalsIgnoreCase("xyv")) {
            try {
                StreamTokenizer st;
                st = new StreamTokenizer(Generic_StaticIO.getBufferedReader(file));
                st.eolIsSignificant(false);
                st.parseNumbers();
                st.whitespaceChars(',', ',');
                st.wordChars('"', '"');
                int tokenType = st.nextToken();
                String alternator = "x";
                double x = 0.0d;
                double y = 0.0d;
                double value = 0.0d;
                while (tokenType != StreamTokenizer.TT_EOF) {
                    switch (tokenType) {
                        case StreamTokenizer.TT_NUMBER:
                            switch (alternator) {
                                case "x":
                                    x = st.nval;
                                    alternator = "y";
                                    break;
                                case "y":
                                    y = st.nval;
                                    alternator = "value";
                                    break;
                                default:
                                    grid.addToCell(x, y, st.nval);
                                    alternator = "x";
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                    tokenType = st.nextToken();
                }
            } catch (java.io.IOException e) {
                e.printStackTrace(System.err);
            }
        }
        if (type.equalsIgnoreCase("xy")) {
            try {
                StreamTokenizer st = new StreamTokenizer(Generic_StaticIO.getBufferedReader(file));
                st.eolIsSignificant(false);
                st.parseNumbers();
                st.whitespaceChars(',', ',');
                st.wordChars('"', '"');
                int tokenType = st.nextToken();
                String alternator = "x";
                double x = 0.0d;
                double y;
                while (tokenType != StreamTokenizer.TT_EOF) {
                    switch (tokenType) {
                        case StreamTokenizer.TT_NUMBER:
                            if (alternator.equals("x")) {
                                x = st.nval;
                                alternator = "y";
                            } else {
                                y = st.nval;
                                grid.addToCell(x, y, 1.0d);
                                alternator = "x";
                            }
                            break;
                        default:
                            break;
                    }
                    tokenType = st.nextToken();
                }
            } catch (java.io.IOException e) {
                e.printStackTrace(System.err);
            }
        }
        if (type.equalsIgnoreCase("idxy")) {
            try {
                StreamTokenizer st = new StreamTokenizer(Generic_StaticIO.getBufferedReader(file));
                st.eolIsSignificant(false);
                st.parseNumbers();
                st.ordinaryChar('e');
                st.ordinaryChar('d');
                st.ordinaryChar('E');
                st.ordinaryChar('D');
                int tokenType = st.nextToken();
                int nextTokenType;
                String alternator = "id";
                double x = 0.0d;
                double y;
                while (tokenType != StreamTokenizer.TT_EOF) {
                    switch (tokenType) {
                        case StreamTokenizer.TT_NUMBER:
                            if (alternator.equals("id")) {
                                //id = (int) st.nval;
                                alternator = "x";
                            } else {
                                if (alternator.equals("x")) {
                                    x = st.nval;
                                    nextTokenType = st.nextToken();
                                    if (nextTokenType != StreamTokenizer.TT_NUMBER) {
                                        st.nextToken();
                                        st.nextToken();
                                        x = x * Math.pow(10.0, st.nval);
                                    } else {
                                        st.pushBack();
                                    }
                                    alternator = "y";
                                } else {
                                    y = st.nval;
                                    nextTokenType = st.nextToken();
                                    if (nextTokenType != StreamTokenizer.TT_NUMBER) {
                                        st.nextToken();
                                        st.nextToken();
                                        y = y * Math.pow(10.0, st.nval);
                                    } else {
                                        st.pushBack();
                                    }
                                    alternator = "id";
                                    //println( " x, y = " + x + ", " + y );
                                    grid.addToCell(x, y, 1.0d);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    tokenType = st.nextToken();
                }
            } catch (java.io.IOException e) {
                e.printStackTrace(System.err);
            }
        }
        ge.checkAndMaybeFreeMemory();
    }

    /**
     * Multiply g0 and g1 and return a new grid. It is assumed that the
     * dimensions are all the same;
     *
     * @param g0
     * @param g1
     * @return
     */
    public Grids_GridDouble multiply(
            Grids_GridDouble g0,
            Grids_GridDouble g1) {
        Grids_GridDouble result;
        boolean hoome = false;
        long nRows = g0.getNRows();
        long nCols = g0.getNCols();
        result = GridDoubleFactory.create(getDirectory(), g0, 0L, 0L,
                nRows - 1, nCols - 1);
        double v0;
        double v1;
        double noDataValue0 = g0.getNoDataValue();
        double noDataValue1 = g1.getNoDataValue();
        for (long row = 0L; row < nRows; row++) {
            for (long col = 0L; col < nCols; col++) {
                v0 = g0.getCell(row, col);
                v1 = g1.getCell(row, col);
                if (v0 != noDataValue0) {
                    if (v1 != noDataValue1) {
                        result.setCell(row, col, v0 * v1);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Divide g0 by g1 and return a new grid. It is assumed that the dimensions
     * are all the same;
     *
     * @param g0 Numerator
     * @param g1 Denominator
     * @return
     */
    public Grids_GridDouble divide(
            Grids_GridDouble g0,
            Grids_GridDouble g1) {
        Grids_GridDouble result;
        boolean hoome = false;
        long nRows = g0.getNRows();
        long nCols = g0.getNCols();
        result = GridDoubleFactory.create(getDirectory(),
                g0, 0L, 0L, nRows - 1, nCols - 1);
        double v0;
        double v1;
        double noDataValue0 = g0.getNoDataValue();
        double noDataValue1 = g1.getNoDataValue();
        for (long row = 0L; row < nRows; row++) {
            for (long col = 0L; col < nCols; col++) {
                v0 = g0.getCell(row, col);
                v1 = g1.getCell(row, col);
                if (v0 != noDataValue0) {
                    if (v1 != noDataValue1) {
                        if (v1 != 0) {
                            result.setCell(row, col, v0 / v1);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns an Grids_GridDouble at a lower level of resolution than grid. The
     * result values are either the sum, mean, max or min of values in grid
     * depending on statistic.
     *
     * @param grid the Grids_GridDouble to be processed
     * @param cellFactor the number of times wider/higher the aggregated grid
     * cells are to be
     * @param statistic "sum", "mean", "max", or "min" depending on what
     * aggregate of values are wanted
     * @param rowOffset the number of rows above or below the origin of grid
     * where the aggregation is to start. > 0 result yllcorner will be above
     * grid yllcorner < 0 result yllcorner will be below grid yllcorner @param
     * colOffset the number of columns ab ove or below the origin of grid where
     * the aggregation is to start. > 0 result xllcorner will be right of grid
     * xllcorner < 0 result xllcorner will be left of grid xllcorner @param
     * gridFactory the Abstract2DSquareCell DoubleFactory used to create result
     * and temporary AbstractGrid2DSquareCellDoubles. @param colOffset @param
     * gridFactory @param hoome If true then OutOfMemoryErrors are caught in
     * this method then swap operations are initiated prior to ret r y ing. If
     * false then OutOfMemoryErrors are caught and thrown. NB. In the
     * calculation of the sum and the mean if there is a cell in grid which has
     * a data value then the result which incorporates that cell has a data
     * value. For this result cell, any of the cells in grid which have
     * noDataValues their value is taken as that of the average of its nearest
     * cells with a value. In the calculation of the max and the min
     * noDataValues are simply ignored. Formerly noDataValues were treated as
     * the average of values within a result cell. TODO: implement median, mode
     * and variance aggregations. @return @param colOffset @param gridFactory
     * @param colOffset @param gridFactory @param hoome @return
     */
    public Grids_GridDouble aggregate(
            Grids_AbstractGridNumber grid,
            int cellFactor,
            String statistic,
            int rowOffset,
            int colOffset,
            Grids_GridDoubleFactory gridFactory,
            boolean hoome) {
        try {
            ge.getGrids().add(grid);
            int _MessageLength = 1000;
            // Initial tests
            if (cellFactor <= 0) {
                System.err.println("Warning!!! cellFactor <= 0 : Returning!");
                return null;
            }
            // Initialisation
            long nrows = grid.getNRows();
            long ncols = grid.getNCols();
            Grids_Dimensions dimensions = grid.getDimensions();
            BigDecimal cellsize = dimensions.getCellsize();
            BigDecimal xMin = dimensions.getXMin();
            BigDecimal yMin = dimensions.getYMin();
            BigDecimal xMax = dimensions.getXMax();
            BigDecimal yMax = dimensions.getYMax();
            double noDataValue = Double.NEGATIVE_INFINITY;
            if (grid.getClass() == Grids_GridInt.class) {
                noDataValue = (double) ((Grids_GridInt) grid).getNoDataValue();
            } else {
                if (grid.getClass() == Grids_GridDouble.class) {
                    noDataValue = ((Grids_GridDouble) grid).getNoDataValue();
                } else {
                    System.err.println("Grid2DSquareCellAbstract not recognised"
                            + " in aggregate( Grid2DSquareCellAbstract( "
                            + grid.toString() + ", cellFactor( " + cellFactor
                            + " ), statistic( " + statistic + " ), rowOffset( "
                            + rowOffset + " ), colOffset( " + colOffset
                            + " ), gridFactory( " + gridFactory
                            + " ),  hoome( " + hoome + " ) )");
                }
            }
            BigDecimal resultCellsize = cellsize.multiply(new BigDecimal(Integer.toString(cellFactor)));
            BigDecimal resultXMin = xMin.add(cellsize.multiply(new BigDecimal(Integer.toString(colOffset))));
            BigDecimal resultYMin = yMin.add(cellsize.multiply(new BigDecimal(Integer.toString(rowOffset))));

            //double resultCellsize = cellsize * ( double ) cellFactor;
            //double width = cellsize * ncols;
            //double height = cellsize * nrows;
            //double resultXllcorner = xllcorner + ( colOffset * cellsize );
            //double resultYllcorner = yllcorner + ( rowOffset * cellsize );
            // Calculate resultNrows and resultHeight
            long resultNrows = 1L;
            BigDecimal resultHeight = new BigDecimal(resultCellsize.toString());
            //double resultHeight = resultCellsize;
            while (resultYMin.add(resultHeight).compareTo(yMax) == -1) {
                resultNrows++;
                resultHeight = resultHeight.add(resultCellsize);
            }
            //while ( ( resultYllcorner + resultHeight ) < ( yllcorner + height ) ) {
            //    resultNrows ++;
            //    resultHeight += resultCellsize;
            //}
            // Calculate resultNcols and resultWidth
            long resultNcols = 1L;
            BigDecimal resultWidth = new BigDecimal(resultCellsize.toString());
            //double resultWidth = resultCellsize;
            while (resultXMin.add(resultWidth).compareTo(xMax) == -1) {
                resultNrows++;
                resultWidth = resultWidth.add(resultCellsize);
            }
            //while ( ( resultXllcorner + resultWidth ) < ( xllcorner + width ) ) {
            //    resultNcols ++;
            //    resultWidth += resultCellsize;
            //}
            BigDecimal resultXMax = resultXMin.add(resultWidth);
            BigDecimal resultYMax = resultYMin.add(resultHeight);
            Grids_Dimensions resultDimensions = new Grids_Dimensions(
                    resultXMin,
                    resultXMax,
                    resultYMin,
                    resultYMax,
                    resultCellsize);
            // Initialise result
            gridFactory.setNoDataValue(noDataValue);
            Grids_GridDouble result = (Grids_GridDouble) gridFactory.create(
                    resultNrows, resultNcols, resultDimensions);

            long row;
            long col;
            double x;
            double y;
            double value;

            // sum
            if (statistic.equalsIgnoreCase("sum")) {
                Grids_GridDouble count = (Grids_GridDouble) gridFactory.create(
                        resultNrows, resultNcols, resultDimensions);
                Grids_GridDouble normaliser = (Grids_GridDouble) gridFactory.create(
                        resultNrows, resultNcols, resultDimensions);
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        x = grid.getCellXDouble(col);
                        y = grid.getCellYDouble(row);
                        if (result.isInGrid(x, y)) {
                            value = grid.getCellDouble(row, col, hoome);
                            if (value != noDataValue) {
                                count.addToCell(x, y, 1.0d);
                                result.addToCell(x, y, value);
                            }
                            normaliser.addToCell(x, y, 1.0d);
                        }
                    }
                }
                //            // Add the nearest values for the noDataValues so long as there is a value
                //            for ( row = 0; row < nrows; row ++ ) {
                //                for ( col = 0; col < ncols; col ++ ) {
                //                    x = grid.getCellXDouble( col, hoome );
                //                    y = grid.getCellYDouble( row, hoome );
                //                    if ( result.inGrid( x, y, hoome ) ) {
                //                        if ( dataCount.getCell( x, y, hoome ) != noDataValue ) {
                //                            result.addToCell( x, y, grid.getNearestValueDouble( row, col, hoome ), hoome );
                //                        }
                //                    }
                //                }
                //            }
                // Normalise
                double count0;
                for (row = 0; row < resultNrows; row++) {
                    for (col = 0; col < resultNcols; col++) {
                        count0 = count.getCell(row, col);
                        if (count0 != 0.0d) {
                            result.setCell(row, col,
                                    ((result.getCell(row, col)
                                    * normaliser.getCell(row, col)) / count0));
                        }
                    }
                }
            }

            // mean
            if (statistic.equalsIgnoreCase("mean")) {
                Grids_GridDouble numerator = (Grids_GridDouble) gridFactory.create(
                        resultNrows, resultNcols, resultDimensions);
                Grids_GridDouble denominator = (Grids_GridDouble) gridFactory.create(
                        resultNrows, resultNcols, resultDimensions);
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        x = grid.getCellXDouble(col);
                        y = grid.getCellYDouble(row);
                        if (result.isInGrid(x, y)) {
                            value = grid.getCellDouble(row, col);
                            if (value != noDataValue) {
                                numerator.addToCell(x, y, value);
                                denominator.addToCell(x, y, 1.0d);
                            }
                        }
                    }
                }
                for (row = 0; row < resultNrows; row++) {
                    for (col = 0; col < resultNcols; col++) {
                        value = numerator.getCell(row, col);
                        if (value != noDataValue) {
                            result.setCell(row, col,
                                    value / denominator.getCell(row, col));
                        }
                    }
                }
            }

            // min
            if (statistic.equalsIgnoreCase("min")) {
                double min;
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        x = grid.getCellXDouble(col);
                        y = grid.getCellYDouble(row);
                        if (result.isInGrid(x, y)) {
                            value = grid.getCellDouble(row, col);
                            if (value != noDataValue) {
                                min = result.getCell(x, y);
                                if (min != noDataValue) {
                                    result.setCell(x, y, Math.min(min, value));
                                } else {
                                    result.setCell(x, y, value);
                                }
                            }
                        }
                    }
                }
            }

            // max
            if (statistic.equalsIgnoreCase("max")) {
                double max;
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        x = grid.getCellXDouble(col);
                        y = grid.getCellYDouble(row);
                        if (result.isInGrid(x, y)) {
                            value = grid.getCellDouble(row, col);
                            if (value != noDataValue) {
                                max = result.getCell(x, y);
                                if (max != noDataValue) {
                                    result.setCell(x, y, Math.max(max, value));
                                } else {
                                    result.setCell(x, y, value);
                                }
                            }
                        }
                    }
                }
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HOOMEF)) {
                    throw e;
                }
                ge.initMemoryReserve();
                return aggregate(grid, cellFactor, statistic, rowOffset,
                        colOffset, gridFactory, hoome);
            } else {
                throw e;
            }

        }
    }

    //    /**
    //     * Returns an Grids_GridDouble at a lower level of resolution than grid.  The result values
    //     * are either the sum, mean, max or min of values in grid depending on statistic.
    //     * @param grid - the Grids_GridDouble to be processed
    //     * @param resultCellsize - output grid cellsize
    //     * @param statistic - "sum", "mean", "max", or "min" depending on what aggregate of values are wanted
    //     * @param resultXllcorner - the x-coordinate of the aggregate grid lower left corner
    //     * @param resultYllcorner - the y-coordinate of the aggregate grid lower left corner
    //     * Use this aggregate method to force origin of the result to be ( resultXllcorner, resultYllcorner ) and
    //     * if resultCellsize is not an integer multiple of cellsize.
    //     * NB. In the calculation of the sum and the mean if there is a cell in grid which has a data value then
    //     *     the result which incorporates that cell has a data value.  For this result cell, any of the cells in
    //     *     grid which have noDataValues their value is taken as that of the average of its nearest cells with
    //     *     a value.
    //     *     In the calculation of the max and the min noDataValues are simply ignored.
    //     *     Formerly noDataValues were treated as the average of values within a result cell.
    //     * TODO: implement median, mode and variance aggregations.
    //     */
    //    public Grids_GridDouble aggregate( Grids_GridDouble grid, double resultCellsize, String statistic, double resultXllcorner, double resultYllcorner ) {
    //        try {
    //            return aggregate( grid, resultCellsize, statistic, resultXllcorner, resultYllcorner, new Grids_GridDoubleFactory() );
    //        } catch ( OutOfMemoryError e ) {
    //            return aggregate( grid, resultCellsize, statistic, resultXllcorner, resultYllcorner, new Grid2DSquareCellDoubleFileFactory() );
    //        }
    //    }
    /**
     * Returns an Grids_GridDouble at a lower level of resolution than grid. The
     * result values are either the sum, mean, max or min of values in grid
     * depending on statistic.
     *
     * @param grid The Grids_GridDouble to be processed
     * @param statistic "sum", "mean", "max", or "min" depending on what
     * aggregate of values are wanted
     * @param resultDimensions
     * @param gridFactory The Abstract2DSquareCellDoubleFactory used to create
     * _AbstractGrid2DSquareCell_HashSet
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then swap operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown. Use this aggregate method if
     * result is to have a new spatial frame. NB. In the calculation of the sum
     * and the mean if there is a cell in grid which has a data value then the
     * result which incorporates that cell has a data value. For this result
     * cell, any of the cells in grid which have noDataValues their value is
     * taken as that of the average of its nearest cells with a value. In the
     * calculation of the max and the min noDataValues are simply ignored.
     * Formerly noDataValues were treated as the average of values within a
     * result cell. TODO: implement median, mode and variance aggregations.
     * <a name="aggregate(AbstractGrid2DSquareCell,
     * String,BigDecimal[],Grid2DSquareCellDoubleFactory,boolean)"></a>
     * @return
     */
    public Grids_GridDouble aggregate(
            Grids_AbstractGridNumber grid,
            String statistic,
            Grids_Dimensions resultDimensions,
            Grids_GridDoubleFactory gridFactory,
            boolean hoome) {
        try {
            ge.getGrids().add(grid);
            int scale = 325;
            // Initialistaion
            long nrows = grid.getNRows();
            long ncols = grid.getNCols();
            Grids_Dimensions dimensions = grid.getDimensions();
            double noDataValue = Double.NEGATIVE_INFINITY;
            if (grid.getClass() == Grids_GridInt.class) {
                noDataValue = (double) ((Grids_GridInt) grid).getNoDataValue();
            } else {
                if (grid.getClass() == Grids_GridDouble.class) {
                    noDataValue = ((Grids_GridDouble) grid).getNoDataValue();
                }
            }
            BigDecimal resultCellsize = resultDimensions.getCellsize();
            BigDecimal resultXMin = resultDimensions.getXMin();
            BigDecimal resultYMin = resultDimensions.getYMin();
            BigDecimal resultXMax = resultDimensions.getXMax();
            BigDecimal resultYMax = resultDimensions.getYMax();

            BigDecimal dimensionsCellsize = dimensions.getCellsize();
            BigDecimal dimensionsXMin = dimensions.getXMin();
            BigDecimal dimensionsYMin = dimensions.getYMin();
            BigDecimal dimensionsXMax = dimensions.getXMax();
            BigDecimal dimensionsYMax = dimensions.getYMax();
            //double width = cellsize * ncols;
            //double height = cellsize * nrows;
            // Test this is an aggregation
            if (resultCellsize.compareTo(dimensionsCellsize) != 1) {
                System.err.println(
                        "!!!Warning: Not an aggregation as "
                        + "resultCellsize < cellsize. Returning null!");
                return null;
            }
            // Test for intersection
            if ((resultXMin.compareTo(dimensionsXMin.add(dimensionsCellsize.multiply(new BigDecimal(Long.toString(ncols))))) == 1)
                    || (resultYMin.compareTo(dimensionsYMin.add(dimensionsCellsize.multiply(new BigDecimal(Long.toString(nrows))))) == 1)) {
                System.err.println(
                        "!!!Warning: No intersection for aggregation. Returning null!");
                return null;
            }
            // If resultCellsize is an integer multiple of cellsize and grid aligns with result then use
            // a cellFactor aggregation as it should be faster.
            //println("resultCellsize % cellsize == " + ( resultCellsize % cellsize ) );
            //println("resultXllcorner % cellsize = " + ( resultXllcorner % cellsize ) + ", xllcorner % cellsize = " + ( xllcorner % cellsize ) );
            //println("resultYllcorner % cellsize = " + ( resultYllcorner % cellsize ) + ", yllcorner % cellsize = " + ( yllcorner % cellsize ) );
            if (true) {
                BigDecimal t0 = resultCellsize.divide(
                        dimensionsCellsize,
                        Math.max(resultCellsize.scale(), dimensionsCellsize.scale()) + 2,
                        BigDecimal.ROUND_HALF_EVEN);
                BigDecimal t1 = resultXMin.divide(
                        dimensionsCellsize,
                        Math.max(resultXMin.scale(), dimensionsCellsize.scale()) + 2,
                        BigDecimal.ROUND_HALF_EVEN);
                BigDecimal t2 = dimensionsXMin.divide(
                        dimensionsCellsize,
                        Math.max(dimensionsXMin.scale(), dimensionsCellsize.scale()) + 2,
                        BigDecimal.ROUND_HALF_EVEN);
                BigDecimal t3 = resultYMin.divide(
                        dimensionsCellsize,
                        Math.max(resultYMin.scale(), dimensionsCellsize.scale()) + 2,
                        BigDecimal.ROUND_HALF_EVEN);
                BigDecimal t4 = dimensionsYMin.divide(
                        dimensionsCellsize,
                        Math.max(dimensionsYMin.scale(), dimensionsCellsize.scale()) + 2,
                        BigDecimal.ROUND_HALF_EVEN);
                if ((t0.compareTo(new BigDecimal(t0.toBigInteger().toString())) == 0)
                        && (t1.compareTo(new BigDecimal(t1.toBigInteger().toString())) == t2.compareTo(new BigDecimal(t2.toBigInteger().toString())))
                        && (t3.compareTo(new BigDecimal(t3.toBigInteger().toString())) == t4.compareTo(new BigDecimal(t4.toBigInteger().toString())))) {
                    int cellFactor = resultCellsize.divide(
                            dimensionsCellsize,
                            2,
                            BigDecimal.ROUND_UNNECESSARY).intValue();
                    int rowOffset = dimensionsYMin.subtract(
                            resultYMin.divide(
                                    dimensionsCellsize,
                                    scale,
                                    BigDecimal.ROUND_HALF_EVEN)).intValue();
                    int colOffset = dimensionsXMin.subtract(
                            resultXMin.divide(
                                    dimensionsCellsize,
                                    scale,
                                    BigDecimal.ROUND_HALF_EVEN)).intValue();
                    return aggregate(grid, cellFactor, statistic, rowOffset, colOffset, gridFactory, hoome);
                }
            }
            // Calculate resultNrows and resultHeight
            long resultNrows = 1L;
            BigDecimal resultHeight = new BigDecimal(resultCellsize.toString());
            //double resultHeight = resultCellsize;
            while (resultYMin.add(resultHeight).compareTo(dimensionsYMax) == -1) {
                resultNrows++;
                resultHeight = resultHeight.add(resultCellsize);
            }
            //while ( ( resultYllcorner + resultHeight ) < ( yllcorner + height ) ) {
            //    resultNrows ++;
            //    resultHeight += resultCellsize;
            //}
            // Calculate resultNcols and resultWidth
            long resultNcols = 1L;
            BigDecimal resultWidth = new BigDecimal(resultCellsize.toString());
            //double resultWidth = resultCellsize;
            while (resultXMin.add(resultWidth).compareTo(dimensionsXMax) == -1) {
                resultNrows++;
                resultWidth = resultWidth.add(resultCellsize);
            }
            //while ( ( resultXllcorner + resultWidth ) < ( xllcorner + width ) ) {
            //    resultNcols ++;
            //    resultWidth += resultCellsize;
            //}
            resultXMax = dimensionsXMin.add(resultWidth);
            resultYMax = dimensionsYMin.add(resultHeight);

            // Initialise result
            gridFactory.setNoDataValue(noDataValue);
            Grids_GridDouble result;
            result = (Grids_GridDouble) gridFactory.create(
                    resultNrows, resultNcols, resultDimensions);

            long row;
            long col;
            double x;
            double y;
            double value;

            double cellsize = dimensionsCellsize.doubleValue();
            double resultCellsized = resultCellsize.doubleValue();

            // sum
            if (statistic.equalsIgnoreCase("sum")) {
                Grids_GridDouble totalValueArea;
                totalValueArea = (Grids_GridDouble) gridFactory.create(
                        resultNrows, resultNcols, resultDimensions);
                double areaProportion;
                double[] bounds;
                Grids_2D_ID_long[] cellIDs = new Grids_2D_ID_long[4];
                double halfCellsize = grid.getCellsizeDouble() / 2.0d;
                double count0;
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        bounds = grid.getCellBoundsDoubleArray(halfCellsize, row, col);
                        cellIDs[0] = result.getCellID(bounds[0], bounds[3]);
                        cellIDs[1] = result.getCellID(bounds[2], bounds[3]);
                        cellIDs[2] = result.getCellID(bounds[0], bounds[1]);
                        cellIDs[3] = result.getCellID(bounds[2], bounds[1]);
                        value = grid.getCellDouble(row, col, hoome);
                        if (value != noDataValue) {
                            if (cellIDs[0].equals(cellIDs[1]) && cellIDs[1].equals(cellIDs[2])) {
                                result.addToCell(cellIDs[0], value);
                                totalValueArea.addToCell(cellIDs[0], 1.0d);
                            } else {
                                if (cellIDs[0].equals(cellIDs[1]) || cellIDs[0].equals(cellIDs[2])) {
                                    if (cellIDs[0].equals(cellIDs[1])) {
                                        areaProportion = (Math.abs(bounds[3]
                                                - (result.getCellYDouble(cellIDs[0]) - (resultCellsized / 2.0d))) * cellsize) / (cellsize * cellsize);
                                    } else {
                                        areaProportion = (Math.abs((result.getCellXDouble(cellIDs[0]) + (resultCellsized / 2.0d)) - bounds[0]) * cellsize) / (cellsize * cellsize);
                                    }
                                } else {
                                    areaProportion = ((Math.abs(bounds[3] - (result.getCellYDouble(cellIDs[0]) - (resultCellsized / 2.0d))) * Math.abs((result.getCellXDouble(cellIDs[0]) + (resultCellsized / 2.0d)) - bounds[0])) / (cellsize * cellsize));
                                }
                                result.addToCell(cellIDs[0], value * areaProportion);
                                totalValueArea.addToCell(cellIDs[0], areaProportion);
                            }
                            if (!cellIDs[1].equals(cellIDs[0])) {
                                if (cellIDs[1].equals(cellIDs[3])) {
                                    areaProportion = (Math.abs(bounds[2] - (result.getCellXDouble(cellIDs[1]) - (resultCellsized / 2.0d))) * cellsize) / (cellsize * cellsize);
                                } else {
                                    areaProportion = ((Math.abs(bounds[3] - (result.getCellYDouble(cellIDs[1]) - (resultCellsized / 2.0d))) * Math.abs(bounds[2] - (result.getCellXDouble(cellIDs[1]) - (resultCellsized / 2.0d)))) / (cellsize * cellsize));
                                }
                                result.addToCell(cellIDs[1], value * areaProportion);
                                totalValueArea.addToCell(cellIDs[0], areaProportion);
                            }
                            if (!cellIDs[2].equals(cellIDs[0])) {
                                if (!cellIDs[2].equals(cellIDs[3])) {
                                    areaProportion = (Math.abs((result.getCellYDouble(cellIDs[2]) + (resultCellsized / 2.0d)) - bounds[1]) * cellsize) / (cellsize * cellsize);
                                } else {
                                    areaProportion = ((Math.abs((result.getCellYDouble(cellIDs[2]) + (resultCellsized / 2.0d)) - bounds[1]) * Math.abs((result.getCellXDouble(cellIDs[2]) + (resultCellsized / 2.0d)) - bounds[0])) / (cellsize * cellsize));
                                }
                                result.addToCell(cellIDs[2], value * areaProportion);
                            }
                            if (!cellIDs[3].equals(cellIDs[1]) && !cellIDs[3].equals(cellIDs[2])) {
                                areaProportion = ((Math.abs((result.getCellYDouble(cellIDs[3]) + (resultCellsized / 2.0d)) - bounds[1]) * Math.abs(bounds[2] - (result.getCellXDouble(cellIDs[3]) - (resultCellsized / 2.0d)))) / (cellsize * cellsize));
                                result.addToCell(cellIDs[3], value * areaProportion);
                                totalValueArea.addToCell(cellIDs[0], areaProportion);
                            }
                        }
                    }
                }
                // Normalise
                double totalValueArea0;
                for (row = 0; row < resultNrows; row++) {
                    for (col = 0; col < resultNcols; col++) {
                        totalValueArea0 = totalValueArea.getCell(row, col);
                        if (totalValueArea0 != 0.0d) {
                            result.setCell(row, col, ((result.getCell(row, col) * ((resultCellsized - cellsize) * (resultCellsized - cellsize))) / totalValueArea.getCell(row, col)));
                        }
                    }
                }

            }
            //        // Add the nearest values for the noDataValues so long as there is a value
            //            for ( int i = 0; i < nrows; i ++ ) {
            //                for ( int j = 0; j < ncols; j ++ ) {
            //                    bounds = grid.getCellBounds( i, j );
            //                    cellID1 = result.getCellID( bounds[ 0 ], bounds[ 3 ] );
            //                    cellID2 = result.getCellID( bounds[ 2 ], bounds[ 3 ] );
            //                    cellID3 = result.getCellID( bounds[ 0 ], bounds[ 1 ] );
            //                    cellID4 = result.getCellID( bounds[ 2 ], bounds[ 1 ] );
            //                    if ( dataCount.getCell( bounds[ 0 ], bounds[ 3 ] ) != noDataValue ||
            //                    dataCount.getCell( bounds[ 2 ], bounds[ 3 ] ) != noDataValue ||
            //                    dataCount.getCell( bounds[ 0 ], bounds[ 1 ] ) != noDataValue ||
            //                    dataCount.getCell( bounds[ 2 ], bounds[ 1 ] ) != noDataValue ) {
            //                        value = grid.getNearestValueDouble( i, j );
            //                        if ( cellID1 == cellID2 && cellID2 == cellID3 ) {
            //                            if ( cellID1 != Integer.MIN_VALUE ) {
            //                                result.addToCell( cellID1, value );
            //                            }
            //                        } else {
            //                            if ( cellID1 != Integer.MIN_VALUE ) {
            //                                if ( cellID1 == cellID2 || cellID1 == cellID3 ) {
            //                                    if ( cellID1 == cellID2 ) {
            //                                        areaProportion = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * cellsize ) / ( cellsize * cellsize );
            //                                    } else {
            //                                        areaProportion = ( Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) * cellsize ) / ( cellsize * cellsize );
            //                                    }
            //                                } else {
            //                                    areaProportion = ( ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) ) / ( cellsize * cellsize ) );
            //                                }
            //                                result.addToCell( cellID1, value * areaProportion );
            //                            }
            //                            if ( cellID2 != Integer.MIN_VALUE ) {
            //                                if ( cellID2 != cellID1 ) {
            //                                    if ( cellID2 == cellID4 ) {
            //                                        areaProportion = ( Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * cellsize ) / ( cellsize * cellsize );
            //                                    } else {
            //                                        areaProportion = ( ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) ) / ( cellsize * cellsize ) );
            //                                    }
            //                                    result.addToCell( cellID2, value * areaProportion );
            //                                }
            //                            }
            //                            if ( cellID3 != Integer.MIN_VALUE ) {
            //                                if ( cellID3 != cellID1 ) {
            //                                    if ( cellID3 == cellID4 ) {
            //                                        areaProportion = ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * cellsize ) / ( cellsize * cellsize );
            //                                    } else {
            //                                        areaProportion = ( ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( ( result.getCellXDouble( cellID3 ) + ( resultCellsize / 2.0d) ) - bounds[0] ) ) / ( cellsize * cellsize ) );
            //                                    }
            //                                    result.addToCell( cellID3, value * areaProportion );
            //                                }
            //                            }
            //                            if ( cellID4 != Integer.MIN_VALUE ) {
            //                                if ( cellID4 != cellID2 && cellID4 != cellID3 ) {
            //                                    areaProportion = ( ( Math.abs( ( result.getCellYDouble( cellID4 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID4 ) - ( resultCellsize / 2.0d ) ) ) ) / ( cellsize * cellsize ) );
            //                                    result.addToCell( cellID4, value * areaProportion );
            //                                }
            //                            }
            //                        }
            //                    }
            //                }
            //            }
            //        }

            // mean
            if (statistic.equalsIgnoreCase("mean")) {
                double denominator = (resultCellsize.doubleValue() * resultCellsize.doubleValue()) / (cellsize * cellsize);
                Grids_GridDouble sum = aggregate(grid, "sum", resultDimensions, gridFactory, hoome);
                addToGrid(result, sum, 1.0d / denominator);
            }

            // max
            if (statistic.equalsIgnoreCase("max")) {
                double max;
                double[] bounds;
                double halfCellsize = grid.getCellsizeDouble() / 2.0d;
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        value = grid.getCellDouble(row, col, hoome);
                        if (value != noDataValue) {
                            x = grid.getCellXDouble(col);
                            y = grid.getCellYDouble(row);
                            bounds = grid.getCellBoundsDoubleArray(halfCellsize, row, col);
                            max = result.getCell(bounds[0], bounds[3]);
                            if (max != noDataValue) {
                                result.setCell(bounds[0], bounds[3], Math.max(max, value));
                            } else {
                                result.setCell(bounds[0], bounds[3], value);
                            }
                            max = result.getCell(bounds[2], bounds[3]);
                            if (max != noDataValue) {
                                result.setCell(bounds[2], bounds[3], Math.max(max, value));
                            } else {
                                result.setCell(bounds[2], bounds[3], value);
                            }
                            max = result.getCell(bounds[0], bounds[1]);
                            if (max != noDataValue) {
                                result.setCell(bounds[0], bounds[1], Math.max(max, value));
                            } else {
                                result.setCell(bounds[0], bounds[1], value);
                            }
                            max = result.getCell(bounds[2], bounds[1]);
                            if (max != noDataValue) {
                                result.setCell(bounds[2], bounds[1], Math.max(max, value));
                            } else {
                                result.setCell(bounds[2], bounds[1], value);
                            }
                        }
                    }
                }
            }

            // min
            if (statistic.equalsIgnoreCase("min")) {
                double min;
                double[] bounds;
                double halfCellsize = grid.getCellsizeDouble() / 2.0d;
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        value = grid.getCellDouble(row, col, hoome);
                        if (value != noDataValue) {
                            x = grid.getCellXDouble(col);
                            y = grid.getCellYDouble(row);
                            bounds = grid.getCellBoundsDoubleArray(halfCellsize, row, col);
                            min = result.getCell(bounds[0], bounds[3]);
                            if (min != noDataValue) {
                                result.setCell(bounds[0], bounds[3], Math.min(min, value));
                            } else {
                                result.setCell(bounds[0], bounds[3], value);
                            }
                            min = result.getCell(bounds[2], bounds[3]);
                            if (min != noDataValue) {
                                result.setCell(bounds[2], bounds[3], Math.min(min, value));
                            } else {
                                result.setCell(bounds[2], bounds[3], value);
                            }
                            min = result.getCell(bounds[0], bounds[1]);
                            if (min != noDataValue) {
                                result.setCell(bounds[0], bounds[1], Math.min(min, value));
                            } else {
                                result.setCell(bounds[0], bounds[1], value);
                            }
                            min = result.getCell(bounds[2], bounds[1]);
                            if (min != noDataValue) {
                                result.setCell(bounds[2], bounds[1], Math.min(min, value));
                            } else {
                                result.setCell(bounds[2], bounds[1], value);
                            }
                        }
                    }
                }
            }

            /*
             // Initialistaion
             int nrows = grid.getNRows();
             int ncols = grid.getNCols();
             double xllcorner = grid.getXllcorner();
             double yllcorner = grid.getYllcorner();
             double cellsize = grid.getCellsize();
             double noDataValue = grid.getNoDataValue();
             double width = cellsize * ncols;
             double height = cellsize * nrows;
             // Test this is an aggregation
             if ( resultCellsize <= cellsize ) {
             println( "!!!Warning: resultCellsize <= cellsize in aggregate( cellsize( " + resultCellsize + " ), statistic( " + statistic + " ), resultXllcorner( " + resultXllcorner + " ), resultYllcorner( " + resultYllcorner + " ), noDataValue( " + noDataValue + " ), gridFactory( " + gridFactory.toString() + " ) ). Returning null!" );
             return null;
             }
             // Test for intersection
             if ( ( resultXllcorner > xllcorner + ( ( double ) ncols * cellsize ) ) || ( resultYllcorner > yllcorner + ( ( double ) nrows * cellsize ) ) ) {
             println( "!!!Warning: No intersection in aggregate( cellsize( " + resultCellsize + " ), statistic( " + statistic + " ), resultXllcorner( " + resultXllcorner + " ), resultYllcorner( " + resultYllcorner + " ), noDataValue( " + noDataValue + " ), gridFactory( " + gridFactory.toString() + " ) ). Returning null!" );
             return null;
             }
             // If resultCellsize is an integer multiple of cellsize and grid aligns with result then use
             // a cellFactor aggregation as it should be faster.
             //println("resultCellsize % cellsize == " + ( resultCellsize % cellsize ) );
             //println("resultXllcorner % cellsize = " + ( resultXllcorner % cellsize ) + ", xllcorner % cellsize = " + ( xllcorner % cellsize ) );
             //println("resultYllcorner % cellsize = " + ( resultYllcorner % cellsize ) + ", yllcorner % cellsize = " + ( yllcorner % cellsize ) );
             if ( ( resultCellsize % cellsize == 0.0d ) && ( ( resultXllcorner % cellsize ) == ( xllcorner % cellsize ) ) && ( ( resultYllcorner % cellsize ) == ( yllcorner % cellsize ) ) ) {
             int cellFactor = ( int ) ( resultCellsize / cellsize );
             int rowOffset = ( int ) ( yllcorner - resultYllcorner / cellsize );
             int colOffset = ( int ) ( xllcorner - resultXllcorner / cellsize );
             return aggregate( grid, cellFactor, statistic, rowOffset, colOffset, gridFactory );
             }
             // Calculate resultNrows and resultHeight
             int resultNrows = 1;
             double resultHeight = resultCellsize;
             while ( ( resultYllcorner + resultHeight ) < ( yllcorner + height ) ) {
             resultNrows ++;
             resultHeight += resultCellsize;
             }
             // Calculate resultNcols and resultWidth
             int resultNcols = 1;
             double resultWidth = resultCellsize;
             while ( ( resultXllcorner + resultWidth ) < ( xllcorner + width ) ) {
             resultNcols ++;
             resultWidth += resultCellsize;
             }
             //println( "resultNcols " + resultNcols + ", resultNrows " + resultNrows );
             //println( "gridToAddNcols " + ncols + ", gridToAddNrows " + nrows );
             // Initialise result
             Grids_GridDouble result = gridFactory.createGrid2DSquareCellDouble( resultNrows, resultNcols, resultXllcorner, resultYllcorner, resultCellsize, noDataValue );
            
             // sum
             if ( statistic.equalsIgnoreCase( "sum" ) ) {
             Grids_GridDouble tempGrid1 = gridFactory.createGrid2DSquareCellDouble( resultNrows, resultNcols, resultXllcorner, resultYllcorner, resultCellsize, noDataValue, 1 );
             Grids_GridDouble tempGrid2 = gridFactory.createGrid2DSquareCellDouble( resultNrows, resultNcols, resultXllcorner, resultYllcorner, resultCellsize, noDataValue, 1 );
             double x = 0.0d;
             double y = 0.0d;
             double d1 = noDataValue;
             double areaProportion = 0.0d;
             double[] bounds = new double[ 4 ];
             int cellID1 = -1;
             int cellID2 = -1;
             int cellID3 = -1;
             int cellID4 = -1;
             //double totalArea = 0;
             for ( int i = 0; i < nrows * ncols; i ++ ) {
             d1 = grid.getCell( i );
             if ( d1 != noDataValue ) {
             x = grid.getCellXDouble( i );
             y = grid.getCellYDouble( i );
             bounds = grid.getCellBounds( i );
             cellID1 = result.getCellID( bounds[ 0 ], bounds[ 3 ] );
             cellID2 = result.getCellID( bounds[ 2 ], bounds[ 3 ] );
             cellID3 = result.getCellID( bounds[ 0 ], bounds[ 1 ] );
             cellID4 = result.getCellID( bounds[ 2 ], bounds[ 1 ] );
             if ( cellID1 == cellID2 && cellID2 == cellID3 ) {
             if ( cellID1 != Integer.MIN_VALUE ) {
             areaProportion = 1.0d;
             tempGrid1.addToCell( x, y, d1 * areaProportion );
             tempGrid2.addToCell( x, y, areaProportion );
             }
             } else {
             if ( cellID1 != Integer.MIN_VALUE ) {
             if ( cellID1 == cellID2 || cellID1 == cellID3 ) {
             if ( cellID1 == cellID2 ) {
             areaProportion = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * cellsize ) / ( cellsize * cellsize );
             } else {
             areaProportion = ( Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) * cellsize ) / ( cellsize * cellsize );
             }
             } else {
             areaProportion = ( ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) ) / ( cellsize * cellsize ) );
             }
             tempGrid1.addToCell( cellID1, d1 * areaProportion );
             tempGrid2.addToCell( cellID1, areaProportion );
             }
             if ( cellID2 != Integer.MIN_VALUE ) {
             if ( cellID2 != cellID1 ) {
             if ( cellID2 == cellID4 ) {
             areaProportion = ( Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * cellsize ) / ( cellsize * cellsize );
             } else {
             areaProportion = ( ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) ) / ( cellsize * cellsize ) );
             }
             tempGrid1.addToCell( cellID2, d1 * areaProportion );
             tempGrid2.addToCell( cellID2, areaProportion );
             }
             }
             if ( cellID3 != Integer.MIN_VALUE ) {
             if ( cellID3 != cellID1 ) {
             if ( cellID3 == cellID4 ) {
             areaProportion = ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * cellsize ) / ( cellsize * cellsize );
             } else {
             areaProportion = ( ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( ( result.getCellXDouble( cellID3 ) + ( resultCellsize / 2.0d) ) - bounds[0] ) ) / ( cellsize * cellsize ) );
             }
             tempGrid1.addToCell( cellID3, d1 * areaProportion );
             tempGrid2.addToCell( cellID3, areaProportion );
             }
             }
             if ( cellID4 != Integer.MIN_VALUE ) {
             if ( cellID4 != cellID2 && cellID4 != cellID3 ) {
             areaProportion = ( ( Math.abs( ( result.getCellYDouble( cellID4 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID4 ) - ( resultCellsize / 2.0d ) ) ) ) / ( cellsize * cellsize ) );
             tempGrid1.addToCell( cellID4, d1 * areaProportion );
             tempGrid2.addToCell( cellID4, areaProportion );
             }
             }
             // Check fails due to rounding errors!
             //if ( cellID1 != Integer.MIN_VALUE && cellID2 != Integer.MIN_VALUE && cellID3 != Integer.MIN_VALUE && cellID4 != Integer.MIN_VALUE && totalArea != 1.0 ) { println( "id = " + i + " : totalArea = " + totalArea + " (cellID1,cellID2,cellID3,cellID4) = (" + cellID1 + "," + cellID2 + "," + cellID3 + "," + cellID4 + ")" );
             //    throw an exception!!!
             //}
             }
             }
             }
             // The values are normalised by dividing the aggregate Grid sum by the proportion of cells with grid values.
             for ( int i = 0; i < resultNrows * resultNcols; i ++ ) {
             d1 = tempGrid2.getCell( i );
             if ( d1 != 0.0d && d1 != noDataValue ) {
             result.setCell( i, tempGrid1.getCell( i ) / ( Math.pow( ( resultCellsize / cellsize ), 2.0d ) / d1 ) );
             }
             }
             tempGrid1.clear();
             tempGrid2.clear();
             }
            
             // mean
             if ( statistic.equalsIgnoreCase( "mean" ) ) {
             // To calculate the mean and cope with NODATA it is necessary to pass
             // through the data twice or for each aggregated cell get all
             // intersecting cells. This is because each cells area as a proportion
             // of the non noDataValue area of an aggregated cell is needed. This
             // cannot be simply done as it all depends on NODATA. In the
             // implementation below the data is read through twice. First read
             // involves calculating NODATA in each aggregated cell.
             Grids_GridDouble tempGrid1 = gridFactory.createGrid2DSquareCellDouble( resultNrows, resultNcols, resultXllcorner, resultYllcorner, resultCellsize, noDataValue, 1 );
             Grids_GridDouble tempGrid2 = gridFactory.createGrid2DSquareCellDouble( resultNrows, resultNcols, resultXllcorner, resultYllcorner, resultCellsize, noDataValue, 1 );
             double x;
             double y;
             double d1;
             double area;
             double[] bounds = new double[4];
             int cellID1;
             int cellID2;
             int cellID3;
             int cellID4;
             //double totalArea = 0;
             for ( int i = 0; i < nrows * ncols; i ++ ) {
             d1 = grid.getCell( i );
             if ( d1 != noDataValue ) {
             x = grid.getCellXDouble( i );
             y = grid.getCellYDouble( i );
             bounds = grid.getCellBounds( i );
             cellID1 = result.getCellID( bounds[0], bounds[3] );
             cellID2 = result.getCellID( bounds[2], bounds[3] );
             cellID3 = result.getCellID( bounds[0], bounds[1] );
             cellID4 = result.getCellID( bounds[2], bounds[1] );
             if ( cellID1 == cellID2 && cellID2 == cellID3 ) {
             if ( cellID1 != Integer.MIN_VALUE ) {
             area = cellsize * cellsize;
             tempGrid1.addToCell( x, y, area );
             }
             } else {
             if ( cellID1 != Integer.MIN_VALUE ) {
             if ( cellID1 == cellID2 || cellID1 == cellID3 ) {
             if ( cellID1 == cellID2 ) {
             area = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * cellsize );
             } else {
             area = ( Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) * cellsize );
             }
             } else {
             area = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) );
             }
             tempGrid1.addToCell( cellID1, area );
             }
             if ( cellID2 != Integer.MIN_VALUE ) {
             if ( cellID2 != cellID1 ) {
             if ( cellID2 == cellID4 ) {
             area = ( Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * cellsize );
             } else {
             area = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) );
             }
             tempGrid1.addToCell( cellID2, area );
             }
             }
             if ( cellID3 != Integer.MIN_VALUE ) {
             if ( cellID3 != cellID1 ) {
             if ( cellID3 == cellID4 ) {
             area = ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * cellsize );
             } else {
             area = ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( ( result.getCellXDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) );
             }
             tempGrid1.addToCell( cellID3, area );
             }
             }
             if ( cellID4 != Integer.MIN_VALUE ) {
             if ( cellID4 != cellID2 && cellID4 != cellID3 ) {
             area = ( Math.abs( ( result.getCellYDouble( cellID4 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID4 ) - ( resultCellsize / 2.0d ) ) ) );
             tempGrid1.addToCell( cellID4, area );
             }
             }
             }
             }
             }
             for ( int i = 0; i < nrows * ncols; i ++ ) {
             double areaIntersect;
             d1 = grid.getCell( i );
             if ( d1 != noDataValue ) {
             x = grid.getCellXDouble( i );
             y = grid.getCellYDouble( i );
             bounds = grid.getCellBounds( i );
             cellID1 = result.getCellID( bounds[0], bounds[3] );
             cellID2 = result.getCellID( bounds[2], bounds[3] );
             cellID3 = result.getCellID( bounds[0], bounds[1] );
             cellID4 = result.getCellID( bounds[2], bounds[1] );
             if ( cellID1 == cellID2 && cellID2 == cellID3 ) {
             if ( cellID1 != Integer.MIN_VALUE ) {
             area = tempGrid1.getCell( x, y );
             result.addToCell( x, y, d1 * ( cellsize * cellsize ) / area );
             }
             } else {
             if ( cellID1 != Integer.MIN_VALUE ) {
             if ( cellID1 == cellID2 || cellID1 == cellID3 ) {
             if ( cellID1 == cellID2 ) {
             areaIntersect = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * cellsize );
             } else {
             areaIntersect = ( Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) * cellsize );
             }
             } else {
             areaIntersect = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) );
             }
             area = tempGrid1.getCell( cellID1 );
             result.addToCell( cellID1, d1 * areaIntersect / area );
             }
             if ( cellID2 != Integer.MIN_VALUE ) {
             if ( cellID2 != cellID1 ) {
             if ( cellID2 == cellID4 ) {
             areaIntersect = ( Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * cellsize );
             } else {
             areaIntersect = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) );
             }
             area = tempGrid1.getCell( cellID2 );
             result.addToCell( cellID2, d1 * areaIntersect / area );
             }
             }
             if ( cellID3 != Integer.MIN_VALUE ) {
             if ( cellID3 != cellID1 ) {
             if ( cellID3 == cellID4 ) {
             areaIntersect = ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * cellsize );
             } else {
             areaIntersect = ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( ( result.getCellXDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) );
             }
             area = tempGrid1.getCell( cellID3 );
             result.addToCell( cellID3, d1 * areaIntersect / area );
             }
             }
             if ( cellID4 != Integer.MIN_VALUE ) {
             if ( cellID4 != cellID2 && cellID4 != cellID3 ) {
             areaIntersect = ( Math.abs( ( result.getCellYDouble( cellID4 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID4 ) - ( resultCellsize / 2.0d ) ) ) );
             area = tempGrid1.getCell( cellID4 );
             result.addToCell( cellID4, d1 * areaIntersect / area );
             }
             }
             }
             }
             }
             tempGrid1.clear();
             }
            
             // max
             if ( statistic.equalsIgnoreCase( "max" ) ) {
             double x;
             double y;
             double d1;
             double d2;
             double[] bounds = new double[4];
             for ( int i = 0; i < nrows * ncols; i ++ ) {
             d1 = grid.getCell( i );
             if ( d1 != noDataValue ) {
             x = grid.getCellXDouble( i );
             y = grid.getCellYDouble( i );
             bounds = grid.getCellBounds( i );
             d2 = result.getCell( bounds[0], bounds[3] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[0], bounds[3], Math.max( d2, d1 ) );
             } else {
             result.setCell( bounds[0], bounds[3], d1 );
             }
             d2 = result.getCell( bounds[2], bounds[3] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[2], bounds[3], Math.max( d2, d1 ) );
             } else {
             result.setCell( bounds[2], bounds[3], d1 );
             }
             d2 = result.getCell( bounds[0], bounds[1] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[0], bounds[1], Math.max( d2, d1 ) );
             } else {
             result.setCell( bounds[0], bounds[1], d1 );
             }
             d2 = result.getCell( bounds[2], bounds[1] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[2], bounds[1], Math.max( d2, d1 ) );
             } else {
             result.setCell( bounds[2], bounds[1], d1 );
             }
             }
             }
             }
            
             // min
             if ( statistic.equalsIgnoreCase( "min" ) ) {
             double x;
             double y;
             double d1;
             double d2;
             double[] bounds = new double[4];
             for ( int i = 0; i < nrows * ncols; i ++ ) {
             d1 = grid.getCell( i );
             if ( d1 != noDataValue ) {
             x = grid.getCellXDouble( i );
             y = grid.getCellYDouble( i );
             bounds = grid.getCellBounds( i );
             d2 = result.getCell( bounds[0], bounds[3] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[0], bounds[3], Math.min( d2, d1 ) );
             } else {
             result.setCell( bounds[0], bounds[3], d1 );
             }
             d2 = result.getCell( bounds[2], bounds[3] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[2], bounds[3], Math.min( d2, d1 ) );
             } else {
             result.setCell( bounds[2], bounds[3], d1 );
             }
             d2 = result.getCell( bounds[0], bounds[1] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[0], bounds[1], Math.min( d2, d1 ) );
             } else {
             result.setCell( bounds[0], bounds[1], d1 );
             }
             d2 = result.getCell( bounds[2], bounds[1] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[2], bounds[1], Math.min( d2, d1 ) );
             } else {
             result.setCell( bounds[2], bounds[1], d1 );
             }
             }
             }
             }
             */
            ge.checkAndMaybeFreeMemory(hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HOOMEF)) {
                    throw e;
                }
                ge.initMemoryReserve();
                return aggregate(
                        grid,
                        statistic,
                        resultDimensions,
                        gridFactory,
                        hoome);
            } else {
                throw e;
            }
        }
    }

    //    TODO: Move to a Extended Stats class
    //    /**
    //     * Returns a double global statistic for an Grids_GridDouble grid
    //     * NB. Only for _AbstractGrid2DSquareCell_HashSet in the same spatial frame.
    //     */
    //    public double globalBivariateStatistics( Grids_GridDouble grid0, Grids_GridDouble grid1, String comparator  ) {
    //        // Initialisation
    //        int grid0Nrows = grid0.getNRows();
    //        int grid0Ncols = grid0.getNCols();
    //        double grid0Xllcorner = grid0.getXllcorner();
    //        double grid0Yllcorner = grid0.getYllcorner();
    //        double grid0Cellsize = grid0.getCellsize();
    //        double grid0NoDataValue = grid0.getNoDataValue();
    //        int grid1Nrows = grid1.getNRows();
    //        int grid1Ncols = grid1.getNCols();
    //        double grid1Xllcorner = grid1.getXllcorner();
    //        double grid1Yllcorner = grid1.getYllcorner();
    //        double grid1Cellsize = grid1.getCellsize();
    //        double grid1NoDataValue = grid1.getNoDataValue();
    //        AbstractGridStatistics grid0Statistics = grid0.getStats();
    //        AbstractGridStatistics grid1Statistics = grid1.getStats();
    //        // TODO: Check spatial frame
    //
    //        // Calculation
    //        double thisDistance;
    //        double x0;
    //        double x1;
    //        double y0;
    //        double y1;
    //        double value0;
    //        double value1;
    //        // diff: The sum of all the differences between the grid cells
    //        if ( comparator.equalsIgnoreCase( "diff" ) ) {
    //            double diff = 0.0d;
    //            for ( int i = 0; i < grid0Nrows; i ++ ) {
    //                for ( int j = 0; j < grid0Ncols; j ++ ) {
    //                    value0 = grid0.getCell( i, j );
    //                    if ( value0 != grid0NoDataValue ) {
    //                        value1 = grid0.getCell( i, j );
    //                        if ( value1 != grid1NoDataValue ) {
    //                            diff += ( value0 - value1 );
    //                        }
    //                    }
    //                }
    //            }
    //            return diff;
    //        }
    //
    //        // abs: The sum of all the absolute differences between the grid cells
    //        if ( comparator.equalsIgnoreCase( "abs" ) ) {
    //            double abs = 0.0d;
    //            for ( int i = 0; i < grid0Nrows; i ++ ) {
    //                for ( int j = 0; j < grid0Ncols; j ++ ) {
    //                    value0 = grid0.getCell( i, j );
    //                    if ( value0 != grid0NoDataValue ) {
    //                        value1 = grid0.getCell( i, j );
    //                        if ( value1 != grid1NoDataValue ) {
    //                            abs += Math.abs( value0 - value1 );
    //                        }
    //                    }
    //                }
    //            }
    //            return abs;
    //        }
    //
    //        // pearsons: The persons correlation coefficient
    //        if ( comparator.equalsIgnoreCase( "pearsons" ) ) {
    //            double pearsons = grid0NoDataValue;
    //            double sum0 = 0.0d;
    //            double sum1 = 0.0d;
    //            double sum0Squared = 0.0d;
    //            double sum1Squared = 0.0d;
    //            double sum01 = 0.0d;
    //            double n = 0.0d;
    //            for ( int i = 0; i < grid0Nrows; i ++ ) {
    //                for ( int j = 0; j < grid0Ncols; j ++ ) {
    //                    value0 = grid0.getCell( i, j );
    //                    value1 = grid1.getCell( i, j );
    //                    if ( value0 != grid0NoDataValue && value1 != grid0NoDataValue ) {
    //                        sum0 += value0;
    //                        sum1 += value1;
    //                        sum0Squared += Math.pow( value0, 2.0d );
    //                        sum1Squared += Math.pow( value1, 2.0d );
    //                        sum01 += value0 * value1;
    //                        n += 1.0d;
    //                    }
    //                }
    //            }
    //            double numerator = ( n * sum01 ) - ( sum0 * sum1 );
    //            double denominator = Math.sqrt( Math.abs( (  ( n * sum0Squared ) - Math.pow( sum0, 2.0d )  ) * ( ( n * sum1Squared ) - Math.pow( sum1, 2.0d ) ) ) );
    //            if ( denominator != 0.0d ) {
    //                pearsons = numerator / denominator;
    //            }
    //            return pearsons;
    //        }
    //
    //        // momentcorrelation: The moment correlation coefficient
    //        if ( comparator.equalsIgnoreCase( "momentCorrelation" ) ) {
    //            double momentCorrelation = grid0NoDataValue;
    //            double mean0 = grid0Statistics.getMean();
    //            double mean1 = grid1Statistics.getMean();
    //            double sd0 = 0.0d;
    //            double sd1 = 0.0d;
    //            double m01 = 0.0d;
    //            double n = 0.0d;
    //            double denominator;
    //            for ( int i = 0; i < grid0Nrows; i ++ ) {
    //                for ( int j = 0; j < grid0Ncols; j ++ ) {
    //                    value0 = grid0.getCell( i, j );
    //                    value1 = grid1.getCell( i, j );
    //                    if ( value0 != grid0NoDataValue && value1 != grid1NoDataValue ) {
    //                        n += 1.0d;
    //                        sd0 += Math.pow( value0 - mean0, 2.0d );
    //                        sd1 += Math.pow( value1 - mean1, 2.0d );
    //                        m01 += ( value0 - mean0 ) * ( value1 - mean1 );
    //                    }
    //                }
    //            }
    //            denominator = Math.sqrt( sd0 ) * Math.sqrt( sd1 );
    //            if ( denominator != 0.0d ) {
    //                momentCorrelation = m01 / denominator;
    //            }
    //            return momentCorrelation;
    //        }
    //        return grid0NoDataValue;
    //    }
    //    /**
    //     * Returns a new Grids_GridDouble the values of which are the distance to the nearest data value
    //     * TODO: Optimise as it is currently very slow and inefficient!!!
    //     * @param hoome If true then OutOfMemoryErrors are caught
    //     *   in this method then swap operations are initiated prior to retrying.
    //     *   If false then OutOfMemoryErrors are caught and thrown.
    //     */
    //    public Grids_GridDouble distanceToDataValue( Grids_GridDouble grid0, Grids_GridDoubleFactory gridFactory, boolean hoome ) {
    //        long nrows = grid0.getNRows();
    //        long ncols = grid0.getNCols();
    //        BigDecimal[] dimensions0 = grid0.getDimensions();
    //        double noDataValue = grid0.getNoDataValue();
    //        Grids_GridDouble result = gridFactory.create( grid0.getChunkNRows(hoomeFalse), grid0.getChunkNCols(hoomeFalse), nrows, ncols, dimensions0, noDataValue );
    //        // Calculate distances
    //        long row;
    //        long col;
    //        for ( row = 0; row < nrows; row ++ ) {
    //            for ( col = 0; col < ncols; col ++ ) {
    //                result.setCell( row, col, grid0.getNearestValueDoubleDistance( row, col, hoome ), hoome );
    //            }
    //        }
    //        return result;
    //    }
    //    TODO:
    //    /**
    //     * Returns a new Grids_GridDouble which is a copy of grid0 but
    //     * with all noDataValues replaced as follows:
    //     * At each iteration we deal with the nearest band of noDataValues.
    //     * (NB. If we dealt only with the nearest noDataValues it may take a very long time to compute!)
    //     * We replace these nearest noDataValues with the average values within distance.
    //     * We then repeat until all cells have a value.
    //     */
    //    public Grids_GridDouble replaceNoDataValues( Grids_GridDouble grid0, double distance, Grids_GridDoubleFactory gridFactory ) {
    //        long nrows = grid0.getNRows();
    //        long ncols = grid0.getNCols();
    //        BigDecimal[] dimensions = grid0.getDimensions();
    //        double noDataValue = grid0.getNoDataValue();
    //        Grids_GridDouble temp1 = gridFactory.createGrid2DSquareCellDouble( grid0 );
    //        Grids_GridDouble temp2 = gridFactory.createGrid2DSquareCellDouble( grid0 );
    //        // Get distances
    //        Grids_GridDouble distanceGrid = distanceToDataValue( grid0, gridFactory );
    //        AbstractGridStatistics distanceGridStatistics = distanceGrid.getStats();
    //        double maxDistance = distanceGridStatistics.getMax();
    //
    //        AbstractGrid2DSquareCellDouble[] geometricDensity = Grids_ProcessorGWS.geometricDensity( grid0, maxDistance, gridFactory );
    //        //return geometricDensity[ geometricDensity.length - 1 ];
    //        return geometricDensity[ geometricDensity.length / 2 ];
    //        /*
    //        int maxCellDistance = ( int ) Math.ceil( maxDistance / cellsize );
    //        boolean alternator = true;
    //        Grids_GridDouble thisGrid = null;
    //        Grids_GridDouble thatGrid = null;
    //        double thisValue;
    //        double thatValue;
    //        double thisDistance;
    //        for ( int iterations = 1; iterations <= maxCellDistance; iterations ++ ) {
    //            for ( int i = 0; i < nrows; i ++ ) {
    //                for ( int j = 0; j < ncols; j ++ ) {
    //                    thisValue = thisGrid.getCell( i, j );
    //                    thatValue = thatGrid.getCell( i, j );
    //                    thisDistance = distanceGrid.getCell( i, j );
    //                    if ( thatValue != noDataValue ) {
    //                        thisGrid.setCell( i, j, thatValue );
    //                    } else {
    //                        if ( thatValue == noDataValue && thisDistance < ( iterations * cellsize ) ) {
    //                            thisGrid.setCell( i, j, Grids_Processor.regionUnivariateStatistics( thatGrid, i, j, "mean", distance, 1.0d, 1.0d, gridFactory ) );
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //        thatGrid.clear();
    //        return thisGrid;
    //         */
    //
    //    }
    /**
     * Shuffles dummyDiff numberOfShuffles places
     */
    private void shuffle(double[] dummyDiff, double[] diff, int numberOfShuffles) {
        for (int i = 0; i < dummyDiff.length - numberOfShuffles; i++) {
            dummyDiff[i] = diff[i + numberOfShuffles];
        }
        for (int i = dummyDiff.length - 1; i > dummyDiff.length - 1 + numberOfShuffles; i--) {
            dummyDiff[i] = diff[i - dummyDiff.length + numberOfShuffles];
        }
    }

    /**
     * Returns a double[][] of grid values
     *
     * @param grid
     * @param row
     * @param cellDistance
     * @return
     */
    protected double[][] getRowProcessInitialData(
            Grids_GridDouble grid,
            int cellDistance,
            long row) {
        double[][] result = new double[(cellDistance * 2) + 1][(cellDistance * 2) + 1];
        long col;
        long r;
        for (r = -cellDistance; r <= cellDistance; r++) {
            for (col = -cellDistance; col <= cellDistance; col++) {
                try {
                    double value = grid.getCell(r + row, col);
                    result[(int) r + cellDistance][(int) col + cellDistance]
                            = value;
                } catch (ArrayIndexOutOfBoundsException e) {
                    int debug = 1;
                    double value = grid.getCell(r + row, col);
                }
            }
        }
        return result;
    }

    /**
     * Returns a double[][] based on previous which has been shuffled
     *
     * @param grid
     * @param col
     * @param previous
     * @param cellDistance
     * @param row
     * @return
     */
    protected double[][] getRowProcessData(
            Grids_GridDouble grid,
            double[][] previous,
            int cellDistance,
            long row,
            long col) {
        double[][] result = previous;
        if (col == 0) {
            return getRowProcessInitialData(grid, cellDistance, row);
        } else {
            // shift columns one left
            for (int i = 0; i <= cellDistance * 2; i++) {
                for (int j = 0; j <= (cellDistance * 2) - 1; j++) {
                    result[i][j] = previous[i][j + 1];
                }
            }
            // getLastColumn
            for (int i = -cellDistance; i <= cellDistance; i++) {
                result[i + cellDistance][cellDistance * 2]
                        = grid.getCell((long) i + row, (long) col + cellDistance);
            }
        }
        return result;
    }

    //    // TODO:
    //    // tests
    //    private double[][] getChunkProcessInitialData( Grids_GridDouble grid, int chunkCells, int rowChunk ) {
    //        double[][] result = new double[ chunkCells * 3 ][ chunkCells * 3 ];
    //        for ( int i = -chunkCells; i <= ( chunkCells * 2 ) - 1; i ++ ) {
    //            for ( int j = -chunkCells; j <= ( chunkCells * 2 ) - 1; j ++ ) {
    //                result[ i + chunkCells ][ j + chunkCells ] = grid.getCell( i + ( rowChunk * chunkCells ), j );
    //            }
    //        }
    //        return result;
    //    }
    //
    //    // Needs testing!!
    //    private double[][] getChunkProcessData( Grids_GridDouble grid, double[][] previous, int chunkCells, int rowChunk, int colChunk ) {
    //        double[][] result = previous;
    //        if ( colChunk == 0 ) {
    //            return getRowProcessInitialData( grid, chunkCells, rowChunk );
    //        } else {
    //            // shift end columns to start columns
    //            for ( int i = -chunkCells; i <= ( chunkCells * 2 ) - 1; i ++ ) {
    //                for ( int j = 0; j <= chunkCells; j ++ ) {
    //                    result[ i + chunkCells ][ j ] = previous[ i + ( chunkCells * 2 ) ][ j + ( chunkCells * 2 ) ];
    //                }
    //            }
    //            // getOtherData
    //            for ( int i = -chunkCells; i <= ( chunkCells * 2 ) - 1; i ++ ) {
    //                for ( int j = 0; i <= ( chunkCells * 2 ) - 1; i ++ ) {
    //                    result[ i + chunkCells ][ j + chunkCells ] = grid.getCell( i + ( rowChunk * chunkCells ), j + ( colChunk * chunkCells ) );
    //                }
    //            }
    //        }
    //        return result;
    //    }
    /**
     * <a
     * name="output(AbstractGrid2DSquareCell,File,ImageExporter,String[],ESRIAsciiGridExporter,boolean)"></a>
     * For outputting _Grid2DSquareCell to various formats of file. It might be
     * better to have this in exchange.IO class.
     *
     * @param grid
     * @param outputDirectory
     * @param ie
     * @param imageTypes
     * @param eage
     * @throws java.io.IOException
     */
    public void output(
            Grids_AbstractGridNumber grid,
            File outputDirectory,
            Grids_ImageExporter ie,
            String[] imageTypes,
            Grids_ESRIAsciiGridExporter eage)
            throws IOException {
        System.out.println("Output " + grid.toString());
        if (ie == null) {
            ie = new Grids_ImageExporter(ge);
        }
        if (imageTypes == null) {
            imageTypes = new String[1];
            imageTypes[0] = "PNG";
        }
        if (eage == null) {
            eage = new Grids_ESRIAsciiGridExporter(ge);
        }
        //int _StringLength = 1000;
        String dotASC = ".asc";
        String noDataValue = "-9999.0";
        String s;
        File file;
        int i;
        int l = imageTypes.length;
        for (i = 0; i < l; i++) {
            s = grid.getName() + "." + imageTypes[i];
            file = ge.initFile(outputDirectory, s);
            ie.toGreyScaleImage(grid, this, file, imageTypes[i]);
        }
        s = grid.getName() + dotASC;
        file = ge.initFile(outputDirectory, s);
        eage.toAsciiFile(grid, file, noDataValue);
    }

    /**
     *
     * @param grid
     * @param outputDirectory
     * @param ie
     * @param imageTypes
     * @param hoome
     * @throws IOException
     */
    public void outputImage(
            Grids_AbstractGridNumber grid,
            File outputDirectory,
            Grids_ImageExporter ie,
            String[] imageTypes,
            boolean hoome)
            throws IOException {
        try {
            System.out.println("Output " + grid.toString());
            if (ie == null) {
                ie = new Grids_ImageExporter(ge);
            }
            if (imageTypes == null) {
                imageTypes = new String[1];
                imageTypes[0] = "PNG";
            }
            String string;
            String string_DOT = ".";
            File file;
            int i;
            int l = imageTypes.length;
            for (i = 0; i < l; i++) {
                string = grid.getName() + string_DOT + imageTypes[i];
                file = ge.initFile(outputDirectory, string);
                ie.toGreyScaleImage(grid, this, file, imageTypes[i]);
            }
        } catch (OutOfMemoryError e) {
            if (hoome) {
                ge.clearMemoryReserve();
                if (ge.swapChunksExcept_Account(grid, hoome) < 1) {
                    throw e;
                }
                ge.initMemoryReserve();
                outputImage(grid, outputDirectory, ie, imageTypes, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     *
     * @param g
     * @param outputDirectory
     * @param eage
     * @param hoome
     * @throws IOException
     */
    public void outputESRIAsciiGrid(
            Grids_AbstractGridNumber g,
            File outputDirectory,
            Grids_ESRIAsciiGridExporter eage,
            boolean hoome)
            throws IOException {
        try {
            if (eage == null) {
                eage = new Grids_ESRIAsciiGridExporter(ge);
            }
            String methodName = "outputESRIAsciiGrid("
                    + g.getClass().getName() + "(" + g.toString() + "),"
                    + outputDirectory.getClass().getName() + "(" + outputDirectory + "),"
                    + eage.getClass().getName() + "(" + eage.toString() + "),"
                    + "boolean(" + hoome + "))";
            System.out.println(methodName);
            String string_DotASC = ".asc";
            String noDataValue = "-9999.0";
            String string;
            File file;
            string = g.getName() + string_DotASC;
            file = ge.initFile(outputDirectory, string);
            eage.toAsciiFile(g, file, noDataValue);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HOOMEF)) {
                    throw e;
                }
                ge.initMemoryReserve();
                outputESRIAsciiGrid(g, outputDirectory, eage, hoome);
            } else {
                throw e;
            }
        }
    }

}
