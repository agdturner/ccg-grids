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
package io.github.agdturner.grids.process;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.agdt.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import uk.ac.leeds.ccg.agdt.generic.util.Generic_Time;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.core.Grids_2D_ID_long;
import io.github.agdturner.grids.core.Grids_Dimensions;
import io.github.agdturner.grids.d2.grid.d.Grids_GridDouble;
import io.github.agdturner.grids.d2.grid.Grids_GridNumber;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkFactoryDouble;
import io.github.agdturner.grids.d2.grid.d.Grids_GridFactoryDouble;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkDouble;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkFactoryDoubleArray;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkFactoryDoubleMap;
import io.github.agdturner.grids.d2.grid.i.Grids_GridInt;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkInt;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkIntArrayFactory;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkFactoryIntMap;
import io.github.agdturner.grids.d2.grid.i.Grids_GridFactoryInt;
import io.github.agdturner.grids.core.Grids_Environment;
import io.github.agdturner.grids.core.Grids_Object;
import io.github.agdturner.grids.d2.chunk.b.Grids_ChunkFactoryBinary;
import io.github.agdturner.grids.d2.grid.b.Grids_GridBoolean;
import io.github.agdturner.grids.d2.grid.b.Grids_GridFactoryBoolean;
import io.github.agdturner.grids.d2.grid.d.Grids_GridDoubleIterator;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkFactoryInt;
import io.github.agdturner.grids.d2.chunk.b.Grids_ChunkFactoryBoolean;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkFactoryDoubleSinglet;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkFactoryIntSinglet;
import io.github.agdturner.grids.d2.grid.b.Grids_GridFactoryBinary;
import io.github.agdturner.grids.d2.stats.Grids_StatsNumber;
import io.github.agdturner.grids.d2.stats.Grids_StatsDouble;
import io.github.agdturner.grids.d2.stats.Grids_StatsNotUpdatedDouble;
import io.github.agdturner.grids.d2.stats.Grids_StatsInt;
import io.github.agdturner.grids.d2.stats.Grids_StatsNotUpdatedInt;
import io.github.agdturner.grids.io.Grids_ESRIAsciiGridExporter;
import io.github.agdturner.grids.io.Grids_Files;
import io.github.agdturner.grids.io.Grids_ImageExporter;
import io.github.agdturner.grids.util.Grids_Utilities;

/**
 * A class holding methods for processing individual or multiple grids.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_Processor extends Grids_Object {

    private static final long serialVersionUID = 1L;

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
     * For convenience.
     */
    protected Grids_Files files;

//    /**
//     * Workspace directory for the processing.
//     */
//    protected File Directory;
    /**
     * Grids_ChunkFactoryInt
     */
    public Grids_ChunkFactoryInt defaultChunkIntFactory;

    /**
     * Grids_ChunkFactoryIntSinglet
     */
    public Grids_ChunkFactoryBoolean chunkBooleanFactory;

    /**
     * Grids_ChunkFactoryIntSinglet
     */
    public Grids_ChunkFactoryBinary chunkBinaryFactory;

    /**
     * Grids_ChunkFactoryIntSinglet
     */
    public Grids_ChunkFactoryIntSinglet GridChunkIntFactory;

    /**
     * Grids_ChunkIntArrayFactory
     */
    public Grids_ChunkIntArrayFactory GridChunkIntArrayFactory;

    /**
     * Grids_ChunkFactoryIntMap
     */
    public Grids_ChunkFactoryIntMap GridChunkIntMapFactory;

    /**
     * Grids_GridFactoryBoolean
     */
    public Grids_GridFactoryBoolean GridBooleanFactory;

    /**
     * Grids_GridFactoryBoolean
     */
    public Grids_GridFactoryBinary GridBinaryFactory;

    /**
     * Grids_GridFactoryInt
     */
    public Grids_GridFactoryInt GridIntFactory;

    /**
     * Grids_ChunkFactoryDouble
     */
    public Grids_ChunkFactoryDouble DefaultGridChunkDoubleFactory;

    /**
     * Grids_ChunkFactoryDoubleSinglet
     */
    public Grids_ChunkFactoryDoubleSinglet GridChunkDoubleFactory;

    /**
     * Grids_ChunkFactoryDoubleArray
     */
    public Grids_ChunkFactoryDoubleArray GridChunkDoubleArrayFactory;

    /**
     * Grids_ChunkFactoryDoubleMap
     */
    public Grids_ChunkFactoryDoubleMap GridChunkDoubleMapFactory;

    /**
     * Grids_GridFactoryDouble
     */
    public Grids_GridFactoryDouble GridDoubleFactory;

    /**
     * Grids_StatsDouble
     */
    public Grids_StatsDouble GridDoubleStatistics;

    /**
     * Grids_StatsNotUpdatedDouble
     */
    public Grids_StatsNotUpdatedDouble GridDoubleStatisticsNotUpdated;

    /**
     * Grids_StatsInt
     */
    public Grids_StatsInt GridIntStatistics;

    /**
     * Grids_StatsNotUpdatedInt
     */
    public Grids_StatsNotUpdatedInt GridIntStatisticsNotUpdated;

    /**
     * Creates a new instance of Grids_Processor. The Log file in directory will
     * be overwritten if appendToLogFile is false.
     *
     * @param ge
     */
    public Grids_Processor(Grids_Environment ge) throws IOException,
            ClassNotFoundException {
        super(ge);
        StartTime = System.currentTimeMillis();
        files = ge.files;
        Path dir = Generic_IO.createNewFile(files.getGeneratedDir());
        Path logFile;
        logFile = Paths.get(dir.toString(), "log.txt");
        if (!Files.exists(logFile)) {
            Files.createFile(logFile);
        }
        Log = Generic_IO.getPrintWriter(logFile, true);
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
        GridBooleanFactory = new Grids_GridFactoryBoolean(env,
                chunkBooleanFactory, 512, 512);
        GridBinaryFactory = new Grids_GridFactoryBinary(env,
                chunkBinaryFactory, 512, 512);
        GridIntFactory = new Grids_GridFactoryInt(env, GridChunkIntFactory,
                defaultChunkIntFactory, 512, 512);
        GridDoubleFactory = new Grids_GridFactoryDouble(env,
                GridChunkDoubleFactory, DefaultGridChunkDoubleFactory, 512,
                512);
        initGridStatistics();
    }

    /**
     * Initialises chunk Factories.
     */
    private void initChunkFactories() {
        GridChunkIntArrayFactory = new Grids_ChunkIntArrayFactory();
        GridChunkIntMapFactory = new Grids_ChunkFactoryIntMap();
        GridChunkIntFactory = new Grids_ChunkFactoryIntSinglet(Integer.MIN_VALUE);
        defaultChunkIntFactory = GridChunkIntArrayFactory;
        GridChunkDoubleArrayFactory = new Grids_ChunkFactoryDoubleArray();
        GridChunkDoubleMapFactory = new Grids_ChunkFactoryDoubleMap();
        GridChunkDoubleFactory = new Grids_ChunkFactoryDoubleSinglet(
                -Double.MAX_VALUE);
        DefaultGridChunkDoubleFactory = GridChunkDoubleArrayFactory;
    }

    /**
     * Initialises Stats.
     */
    private void initGridStatistics() {
        GridDoubleStatistics = new Grids_StatsDouble(env);
        GridDoubleStatisticsNotUpdated = new Grids_StatsNotUpdatedDouble(
                env);
        GridIntStatistics = new Grids_StatsInt(env);
        GridIntStatisticsNotUpdated = new Grids_StatsNotUpdatedInt(env);
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
     * Writes string to Log file and the console (standard output) indenting
     * string by LogIndentation amount of white-space.
     *
     * @param logIndentation The indentation of string.
     * @param s The message to Log.
     */
    public final void log(int logIndentation, String s) {
        if (s.endsWith("}")) {
            logIndentation--;
            LogIndentation--;
        }
        for (int i = 0; i < logIndentation; i++) {
            System.out.print(" ");
            Log.write(" ");
        }
        Log.write(s);
        Log.write(System.getProperty("line.separator"));
        Log.flush();
        if (s.endsWith("{")) {
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
    protected final double distance(double x1, double y1, double x2,
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
    protected final double angle(double x1, double y1, double x2, double y2) {
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
     * @param g The Grids_GridNumber that the mask will be applied to.
     * @param mask The Grids_GridNumber to use as a mask.
     */
    public void mask(Grids_GridNumber g,
            Grids_GridNumber mask) throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        int chunkNRows;
        int chunkNCols;
        int chunkRow;
        int chunkCol;
        int cellRow;
        int cellCol;
        long row;
        long col;
        Grids_2D_ID_int chunkID;
        if (g instanceof Grids_GridInt) {
            Grids_GridInt grid = (Grids_GridInt) g;
            int noDataValue = grid.getNoDataValue();
            if (mask instanceof Grids_GridInt) {
                Grids_GridInt maskInt;
                maskInt = (Grids_GridInt) mask;
                int maskNoDataValue = maskInt.getNoDataValue();
                int value;
                Iterator<Grids_2D_ID_int> ite = maskInt.iterator().getGridIterator();
                while (ite.hasNext()) {
                    Grids_ChunkInt maskIntChunk = maskInt.getChunk(ite.next());
                    chunkID = maskIntChunk.getChunkID();
                    env.addToNotToCache(g, chunkID);
                    env.addToNotToCache(mask, chunkID);
                    env.checkAndMaybeFreeMemory();
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
                    env.removeFromNotToCache(g, chunkID);
                    env.removeFromNotToCache(mask, chunkID);
                }
            } else {
                // ( mask.getClass() == Grids_GridDouble.class )
                Grids_GridDouble maskDouble;
                maskDouble = (Grids_GridDouble) mask;
                double maskNoDataValue = maskDouble.getNoDataValue();
                double value;
                Iterator<Grids_2D_ID_int> ite = maskDouble.iterator().getGridIterator();
                while (ite.hasNext()) {
                    Grids_ChunkDouble maskChunk = maskDouble.getChunk(ite.next());
                    chunkID = maskChunk.getChunkID();
                    env.addToNotToCache(g, chunkID);
                    env.addToNotToCache(mask, chunkID);
                    env.checkAndMaybeFreeMemory();
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
                    env.removeFromNotToCache(g, chunkID);
                    env.removeFromNotToCache(mask, chunkID);
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
                Iterator<Grids_2D_ID_int> ite = maskInt.iterator().getGridIterator();
                while (ite.hasNext()) {
                    Grids_ChunkInt maskChunk = maskInt.getChunk(ite.next());
                    chunkID = maskChunk.getChunkID();
                    env.addToNotToCache(g, chunkID);
                    env.addToNotToCache(mask, chunkID);
                    env.checkAndMaybeFreeMemory();
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
                    env.removeFromNotToCache(g, chunkID);
                    env.removeFromNotToCache(mask, chunkID);
                }
            } else {
                // ( mask.getClass() == Grids_GridDouble.class )
                Grids_GridDouble maskDouble = (Grids_GridDouble) mask;
                double maskNoDataValue = maskDouble.getNoDataValue();
                double value;
                Iterator ite = maskDouble.getChunkIDs().iterator();
                Grids_ChunkDouble maskChunk;
                while (ite.hasNext()) {
                    maskChunk = (Grids_ChunkDouble) mask.getChunk(
                            (Grids_2D_ID_int) ite.next());
                    chunkID = maskChunk.getChunkID();
                    env.addToNotToCache(g, chunkID);
                    env.addToNotToCache(mask, chunkID);
                    env.checkAndMaybeFreeMemory();
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
                    env.removeFromNotToCache(g, chunkID);
                    env.removeFromNotToCache(mask, chunkID);
                }
            }
        }
        env.checkAndMaybeFreeMemory();
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
            Grids_GridNumber g,
            double min,
            double max) throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        int cellRow;
        int cellCol;
        int chunkNRows;
        int chunkNCols;
        Grids_2D_ID_int chunkID;
        if (g.getClass() == Grids_GridInt.class) {
            Grids_GridInt gi = (Grids_GridInt) g;
            int ndv = gi.getNoDataValue();
            int value;
            Iterator<Grids_2D_ID_int> ite = gi.iterator().getGridIterator();
            while (ite.hasNext()) {
                Grids_ChunkInt chunk = gi.getChunk(ite.next());
                chunkID = chunk.getChunkID();
                env.addToNotToCache(g, chunkID);
                env.checkAndMaybeFreeMemory();
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
                env.removeFromNotToCache(g, chunkID);
            }
        } else {
            // ( grid.getClass() == Grids_GridDouble.class )
            Grids_GridDouble gd = (Grids_GridDouble) g;
            double ndv = gd.getNoDataValue();
            double value;
            Iterator<Grids_2D_ID_int> ite = gd.iterator().getGridIterator();
            while (ite.hasNext()) {
                Grids_ChunkDouble chunk = gd.getChunk(ite.next());
                chunkID = chunk.getChunkID();
                env.addToNotToCache(g, chunkID);
                env.checkAndMaybeFreeMemory();
                chunkNRows = g.getChunkNRows(chunkID);
                chunkNCols = g.getChunkNCols(chunkID);
                for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                    for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                        value = gd.getCell(chunk, cellRow, cellCol);
                        if (value >= min && value <= max) {
                            gd.setCell(chunk, cellRow, cellCol, ndv);
                        }
                    }
                }
                env.removeFromNotToCache(g, chunkID);
            }
        }
        //grid.setName( grid.getName() + "_mask" );
        env.checkAndMaybeFreeMemory();
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
            Grids_GridNumber g,
            String type,
            double min,
            double max) throws IOException, ClassNotFoundException {
        if (g instanceof Grids_GridDouble) {
            return rescale((Grids_GridDouble) g, null, 0.0d, 255.0d);
        } else {
            return rescale((Grids_GridInt) g, null, 0.0d, 255.0d);
        }
    }

    /**
     * @param g
     * @param type If type == null then a linear rescale is done. If type ==
     * "Log" then a Log rescale is done.
     * @param min The minimum value in the rescaled range.
     * @param max The maximum value in the rescaled range.
     * @param hoome
     * @return A Grids_GridDouble with values from g either linearly rescaled
     * into the range [min,max] or scaled using log.
     * @TODO Improve log rescaling implementation.
     */
    public Grids_GridDouble rescale(
            Grids_GridDouble g,
            String type,
            double min,
            double max,
            boolean hoome) throws IOException, ClassNotFoundException {
        try {
            return rescale(g, type, min, max);
        } catch (java.lang.OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                if (env.cacheChunksExcept_Account(g, hoome) < 1) {
                    throw e;
                }
                env.initMemoryReserve();
                return rescale(g, type, min, max, hoome);
            } else {
                throw e;
            }
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
    protected Grids_GridDouble rescale(Grids_GridDouble g, String type,
            double min, double max) throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        Grids_GridDouble r;
        long nrows = g.getNRows();
        long ncols = g.getNCols();
        int nChunkCols = g.getNChunkCols();
        int nChunkRows = g.getNChunkRows();
        int chunkNCols;
        int chunkNRows;
        double ndv = g.getNoDataValue();
        double range = max - min;
        Grids_StatsNumber stats = g.getStats();
        double minGrid = stats.getMin(true).doubleValue();
        double maxGrid = stats.getMax(true).doubleValue();
        double rangeGrid = maxGrid - minGrid;
        double value;
        Generic_Path dir = new Generic_Path(Generic_IO.createNewFile(
                files.getGeneratedGridDoubleDir()));
        r = GridDoubleFactory.create(dir, g, 0, 0, nrows - 1, ncols - 1);
        r.setName(g.getName());
        System.out.println(r.toString());
        int chunkRow;
        int chunkCol;
        int cellRow;
        int cellCol;
        Grids_ChunkDouble gridChunk;
        Grids_ChunkDouble resultChunk;
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
                        env.addToNotToCache(g, chunkID);
                        env.addToNotToCache(r, chunkID);
                        env.checkAndMaybeFreeMemory();
                        chunkNCols = g.getChunkNCols(chunkCol);
                        gridChunk = g.getChunk(chunkID);
                        resultChunk = r.getChunk(chunkID);
                        for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                            for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                                value = gridChunk.getCell(cellRow, cellCol);
                                if (value != ndv) {
                                    r.setCell(resultChunk, cellRow,
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
                        env.addToNotToCache(g, chunkID);
                        env.addToNotToCache(r, chunkID);
                        env.checkAndMaybeFreeMemory();
                        chunkNCols = g.getChunkNCols(chunkCol);
                        gridChunk = g.getChunk(chunkID);
                        resultChunk = r.getChunk(chunkID);
                        for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                            for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                                value = gridChunk.getCell(cellRow, cellCol);
                                if (value != ndv) {
                                    v = (((value - minGrid)
                                            / rangeGrid) * range) + min;
                                    r.setCell(resultChunk, cellRow,
                                            cellCol, v);
                                }
                            }
                        }
                    }
                }
            }
            r.setName(g.getName() + "_linearRescale");
            env.checkAndMaybeFreeMemory();
        } else {
            // @TODO this implementation could be much improved...
            int row;
            int col;
            if (type.equalsIgnoreCase("log")) {
                r = rescale(r, null, 1.0d, 1000000.0d);
                // Probably better to do this by chunks
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        value = g.getCell(row, col);
                        if (value != ndv) {
                            r.setCell(row, col, Math.log(value));
                        }
                    }
                }
                r = rescale(r, null, min, max);
                r.setName(g.getName() + "_logRescale");
                env.checkAndMaybeFreeMemory();
            } else {
                try {
                    throw new Exception("Unable to rescale: type " + type
                            + "not recognised.");
                } catch (Exception ex) {
                    Logger.getLogger(Grids_Processor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return r;
    }

    /**
     * @param g
     * @param type If type == null then a linear rescale is done. If type ==
     * "Log" then a Log rescale is done.
     * @param min The minimum value in the rescaled range.
     * @param max The maximum value in the rescaled range.
     * @param hoome
     * @return A Grids_GridDouble with values from g either linearly rescaled
     * into the range [min,max] or scaled using log.
     * @TODO Improve log rescaling implementation.
     */
    public Grids_GridDouble rescale(Grids_GridInt g, String type, double min,
            double max, boolean hoome) throws IOException, ClassNotFoundException {
        try {
            return rescale(g, type, min, max);
        } catch (java.lang.OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                if (env.cacheChunksExcept_Account(g, hoome) < 1) {
                    throw e;
                }
                env.initMemoryReserve();
                return rescale(g, type, min, max, hoome);
            } else {
                throw e;
            }
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
    protected Grids_GridDouble rescale(Grids_GridInt g, String type, double min,
            double max) throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        long nrows = g.getNRows();
        long ncols = g.getNCols();
        int nChunkCols = g.getNChunkCols();
        int nChunkRows = g.getNChunkCols();
        int ndv = g.getNoDataValue();
        double range = max - min;
        Grids_StatsNumber stats = g.getStats();
        double minGrid = stats.getMin(true).doubleValue();
        double maxGrid = stats.getMax(true).doubleValue();
        double rangeGrid = maxGrid - minGrid;
        double value;
        double v;
        Grids_GridDouble r = GridDoubleFactory.create(
                new Generic_Path(Paths.get(g.getDirectory().getParent().toString(),
                        "Rescaled" + g.getName())),
                g, 0, 0, nrows - 1, ncols - 1);
        r.setName(g.getName());
        System.out.println(r.toString());
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
                        env.addToNotToCache(g, chunkID);
                        env.addToNotToCache(r, chunkID);
                        env.checkAndMaybeFreeMemory();
                        chunkNCols = g.getChunkNCols(chunkCol);
                        chunkNRows = g.getChunkNRows(chunkRow);
                        Grids_ChunkInt gridChunk;
                        gridChunk = g.getChunk(chunkID);
                        Grids_ChunkDouble outputGridChunk;
                        outputGridChunk = r.getChunk(chunkID);
                        for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                            for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                                value = gridChunk.getCell(cellRow, cellCol);
                                if (value != ndv) {
                                    r.setCell(outputGridChunk, cellRow,
                                            cellCol, min);
                                }
                            }
                        }
                        env.removeFromNotToCache(g, chunkID);
                        env.removeFromNotToCache(r, chunkID);
                        env.checkAndMaybeFreeMemory();
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
                        env.addToNotToCache(g, chunkID);
                        env.addToNotToCache(r, chunkID);
                        env.checkAndMaybeFreeMemory();
                        chunkNCols = g.getChunkNCols(chunkCol);
                        chunkNRows = g.getChunkNRows(chunkRow);
                        Grids_ChunkInt gridChunk;
                        gridChunk = g.getChunk(chunkID);
                        Grids_ChunkDouble outputGridChunk;
                        outputGridChunk = r.getChunk(chunkID);
                        for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                            for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                                value = gridChunk.getCell(cellRow, cellCol);
                                if (value != ndv) {
                                    v = (((value - minGrid) / rangeGrid)
                                            * range) + min;
                                    r.setCell(outputGridChunk,
                                            cellRow, cellCol, v);
                                }
                            }
                        }
                        env.removeFromNotToCache(g, chunkID);
                        env.removeFromNotToCache(r, chunkID);
                        env.checkAndMaybeFreeMemory();
                    }
                }
            }
            r.setName(g.getName() + "_linearRescale");
            env.checkAndMaybeFreeMemory();
        } else {
            // @TODO this is not a good implementation
            if (type.equalsIgnoreCase("log")) {
                r = rescale(r, null, 1.0d, 1000000.0d);
                long row;
                long col;
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        value = g.getCell(row, col);
                        if (value != ndv) {
                            r.setCell(row, col, Math.log(value));
                        }
                    }
                }
                r = rescale(r, null, min, max);
                //grid.setName( grid.getName() + "_logRescale" );
                env.checkAndMaybeFreeMemory();
            } else {
                System.out.println("Unable to rescale: type " + type
                        + "not recognised. Returning a Grid2DSquareCellDouble.");
            }
        }
        return r;
    }

    /**
     * Modifies grid so value of cells with CellIDs in cellIDs are set to a
     * value a little bit larger.
     *
     * @param g The Grids_GridDouble to be processed.
     * @param cellIDs The CellIDs of the cells to be processed.
     */
    public void setLarger(Grids_GridDouble g, HashSet<Grids_2D_ID_long> cellIDs)
            throws IOException, ClassNotFoundException {
        double ndv = g.getNoDataValue();
        Iterator<Grids_2D_ID_long> ite = cellIDs.iterator();
        while (ite.hasNext()) {
            Grids_2D_ID_long cellID = ite.next();
            double v = g.getCell(cellID.getRow(), cellID.getCol());
            if (v != ndv) {
                g.setCell(cellID.getRow(), cellID.getCol(), Math.nextUp(v));
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
    public void setSmaller(Grids_GridDouble g, HashSet<Grids_2D_ID_long> cellIDs)
            throws IOException, ClassNotFoundException {
        double ndv = g.getNoDataValue();
        Iterator<Grids_2D_ID_long> ite = cellIDs.iterator();
        while (ite.hasNext()) {
            Grids_2D_ID_long cellID = ite.next();
            double v = g.getCell(cellID.getRow(), cellID.getCol());
            if (v != ndv) {
                g.setCell(cellID.getRow(), cellID.getCol(), Math.nextDown(v));
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
     * then cache operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public void addToGrid(
            Grids_GridDouble grid,
            HashSet cellIDs,
            double value,
            boolean hoome) throws IOException, ClassNotFoundException {
        try {
            env.checkAndMaybeFreeMemory(hoome);
            Iterator iterator1 = cellIDs.iterator();
            while (iterator1.hasNext()) {
                //grid.addToCell( ( CellID ) iterator1.next(), value );
                Grids_2D_ID_long cellID = (Grids_2D_ID_long) iterator1.next();
                if (cellID != null) {
                    grid.addToCell(cellID, value);
                }
            }
            env.checkAndMaybeFreeMemory(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                if (!env.cacheChunk(env.HOOMEF)) {
                    throw e;
                }
                env.initMemoryReserve();
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
            double value) throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        long nrows = grid.getNRows();
        long ncols = grid.getNCols();
        long row;
        long col;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                grid.addToCell(row, col, value);
            }
        }
        env.checkAndMaybeFreeMemory();
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
            double value) throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        for (Grids_2D_ID_long cellID : cellIDs) {
            grid.addToCell(cellID.getRow(), cellID.getCol(), value);
        }
        env.checkAndMaybeFreeMemory();
    }

    /**
     * Add g2 to g with values from g2 multiplied by w.
     *
     * @param g Grid to be processed/modified.
     * @param g2 Grid from which values are added.
     * @param w Value g2 values are multiplied by.
     */
    public void addToGrid(Grids_GridDouble g, Grids_GridDouble g2, double w)
            throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        if (g2 != null) {
            addToGrid(g, g2, 0L, 0L, g2.getNRows() - 1L, g2.getNCols() - 1L, w);
        }
    }

    /**
     * Add g2 to g with values from g2 multiplied by w. Only values of g2 with
     * row between startRow and endRow, and column between startCol and endCol
     * are added.
     *
     * @param g Grid to be processed.
     * @param g2 Grid from which values are added.
     * @param startRow Index of the first row from which g2 values are added.
     * @param startCol Index of the first column from which g2 values are added.
     * @param endRow Index of the final row from which g2 values are added.
     * @param endCol Index of the final column from which g2 values are added.
     * @param w Value g2 values are multiplied by.
     */
    public void addToGrid(Grids_GridDouble g, Grids_GridDouble g2,
            long startRow, long startCol, long endRow, long endCol, double w)
            throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        Grids_Dimensions dimensions = g2.getDimensions();
        BigDecimal xMin;
        BigDecimal yMin;
        BigDecimal cellsize;
        cellsize = dimensions.getCellsize();
        xMin = dimensions.getXMin();
        yMin = dimensions.getYMin();
        BigDecimal[] dc = new BigDecimal[5];
        dc[1] = xMin.add(new BigDecimal(startCol).multiply(cellsize));
        dc[2] = yMin.add(new BigDecimal(startRow).multiply(cellsize));
        dc[3] = xMin.add(
                new BigDecimal(endCol - startCol + 1L).multiply(cellsize));
        dc[4] = yMin.add(
                new BigDecimal(endRow - startRow + 1L).multiply(cellsize));
        addToGrid(g, g2, startRow, startCol, endRow, endCol, dc, w);
        env.checkAndMaybeFreeMemory();
    }

    /**
     * Returns a Grids_GridDouble with values of g added with values from g2
     * (with row between startRow, endRow and column index between startCol,
     * endCol) multiplied by w.
     *
     * @param g Grid to be modified.
     * @param g2 Grid from which values are added.
     * @param startRow Index of the first row from which g2 values are added.
     * @param startCol Index of the first column from which g2 values are added.
     * @param endRow Index of the final row from which g2 values are added.
     * @param endCol Index of the final column from which g2 values are added.
     * @param dc Dimension constraints: XMin, YMin, XMax, YMax of the region of
     * g2 to be added.
     * @param w Value g2 values are multiplied by.
     */
    public void addToGrid(Grids_GridDouble g, Grids_GridDouble g2,
            long startRow, long startCol, long endRow, long endCol,
            BigDecimal[] dc, double w) throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        long nrows = g.getNRows();
        long ncols = g.getNCols();
        double noDataValue = g.getNoDataValue();
        Grids_Dimensions gDimensions = g.getDimensions();
        double g2NoDataValue = g2.getNoDataValue();
        Grids_Dimensions g2Dimensions = g2.getDimensions();
        Grids_GridFactoryDouble gf;
        gf = new Grids_GridFactoryDouble(env, GridChunkDoubleFactory,
                DefaultGridChunkDoubleFactory, g.getChunkNCols(),
                g.getChunkNRows());
        // If the region to be added is outside g then return.
        if ((dc[1].compareTo(gDimensions.getXMax()) == 1)
                || (dc[3].compareTo(gDimensions.getXMin()) == -1)
                || (dc[2].compareTo(gDimensions.getYMax()) == 1)
                || (dc[4].compareTo(gDimensions.getYMin()) == -1)) {
            return;
        }
        BigDecimal g2Cellsize;
        BigDecimal gCellsize;
        BigDecimal g2HalfCellsize;
        //BigDecimal gHalfCellsize;
        g2Cellsize = g2Dimensions.getCellsize();
        gCellsize = gDimensions.getCellsize();
        g2HalfCellsize = g2Dimensions.getHalfCellsize();
        //gHalfCellsize = gDimensions.getHalfCellsize();
        if (g2Cellsize.compareTo(gCellsize) == -1) {
            throw new UnsupportedOperationException();
        } else {
            // If g2Cellsize is the same as gCellsize g and g2 align
            if ((g2Cellsize.compareTo(gCellsize) == 0)
                    && ((g2Dimensions.getXMin().remainder(gCellsize)).compareTo(
                            (gDimensions.getXMin().remainder(gCellsize))) == 0)
                    && ((g2Dimensions.getYMin().remainder(gCellsize)).compareTo(
                            (gDimensions.getYMin().remainder(gCellsize))) == 0)) {
                //println( "grids Align!" );
                double x;
                double y;
                double value;
                long row;
                long col;
                // TODO: Control precision using xBigDecimal and yBigDecimal
                // rather than using x and y.
                for (row = startRow; row <= endRow; row++) {
                    env.checkAndMaybeFreeMemory();
                    y = g2.getCellYDouble(row);
                    for (col = startCol; col <= endCol; col++) {
                        x = g2.getCellXDouble(col);
                        value = g2.getCell(row, col);
                        if (value != g2NoDataValue) {
                            if (value != 0.0d) {
                                g.addToCell(x, y, value * w);
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
                Generic_Path dir;
                dir = new Generic_Path(Generic_IO.createNewFile(files.getGeneratedGridDoubleDir().getPath()));
                tg1 = gf.create(dir, nrows, ncols, gDimensions);
                dir = new Generic_Path(Generic_IO.createNewFile(files.getGeneratedGridDoubleDir().getPath()));
                tg2 = (Grids_GridDouble) gf.create(dir, nrows, ncols, gDimensions);
                // TODO:
                // Check scale and rounding appropriate
                int scale = 324;
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
                //double x;
                //double y;
                long r;
                long c;
                double areaProportion;
                double halfCellsize = g.getCellsizeDouble() / 2.0d;
                RoundingMode rm = RoundingMode.HALF_EVEN;
                // TODO:
                // precision checking and use of BigDecimal?
                for (r = 0; r < nrows; r++) {
                    env.checkAndMaybeFreeMemory();
                    for (c = 0; c < ncols; c++) {
                        bounds = g.getCellBoundsDoubleArray(halfCellsize, r, c);
                        //x = g.getCellXDouble(col);
                        //y = g.getCellYDouble(row);
                        cellID1 = g2.getCellID(bounds[0], bounds[3]);
                        cellID2 = g2.getCellID(bounds[2], bounds[3]);
                        cellID3 = g2.getCellID(bounds[0], bounds[1]);
                        cellID4 = g2.getCellID(bounds[2], bounds[1]);
                        d1 = g2.getCell(cellID1.getRow(), cellID1.getCol());
                        if (cellID1.equals(cellID2) && cellID2.equals(cellID3)) {
                            if (d1 != g2NoDataValue) {
                                areaProportion = (gCellsize.multiply(
                                        gCellsize).divide(g2CellsizeSquared, scale, rm)).doubleValue();
                                tg1.addToCell(r, c, d1 * areaProportion);
                                tg2.addToCell(r, c, areaProportion);
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
                                                g2CellsizeSquared, scale, rm)).doubleValue());
                                    } else {
                                        areaProportion = Math.abs(((((g2.getCellXBigDecimal(cellID1).add(
                                                g2HalfCellsize)).subtract(BigDecimal.valueOf(bounds[0]))).multiply(
                                                gCellsize)).divide(g2CellsizeSquared, scale, rm)).doubleValue());
                                    }
                                } else {
                                    areaProportion = Math.abs(
                                            ((BigDecimal.valueOf(bounds[3]).subtract(
                                                    g2.getCellYBigDecimal(cellID1).subtract(g2HalfCellsize))).multiply(
                                                    (g2.getCellXBigDecimal(cellID1).add(
                                                            g2HalfCellsize.subtract(BigDecimal.valueOf(bounds[0])))).divide(
                                                            g2CellsizeSquared, scale, rm))).doubleValue());
                                }
                                tg1.addToCell(r, c, d1 * areaProportion);
                                tg2.addToCell(r, c, areaProportion);
                            }
                            if (!g2.isInGrid(cellID2) && d2 != g2NoDataValue) {
                                if (cellID2.equals(cellID1)) {
                                    if (cellID2.equals(cellID4)) {
                                        areaProportion = Math.abs((((BigDecimal.valueOf(bounds[2]).subtract(
                                                g2.getCellXBigDecimal(cellID2).subtract(
                                                        g2HalfCellsize))).multiply(gCellsize)).divide(
                                                g2CellsizeSquared, scale, rm)).doubleValue());
                                    } else {
                                        areaProportion = Math.abs(((BigDecimal.valueOf(bounds[3]).subtract(
                                                g2.getCellYBigDecimal(cellID2).subtract(
                                                        g2HalfCellsize))).multiply(BigDecimal.valueOf(bounds[2]).subtract(
                                                        g2.getCellXBigDecimal(cellID2).subtract(
                                                                g2HalfCellsize))).divide(g2CellsizeSquared, scale, rm)).doubleValue());
                                    }
                                    tg1.addToCell(r, c, d2 * areaProportion);
                                    tg2.addToCell(r, c, areaProportion);
                                }
                            }
                            if (!g2.isInGrid(cellID3) && d3 != g2NoDataValue) {
                                if (!cellID3.equals(cellID1)) {
                                    if (cellID3.equals(cellID4)) {
                                        areaProportion = Math.abs(((((g2.getCellYBigDecimal(cellID3).add(
                                                g2HalfCellsize)).subtract(BigDecimal.valueOf(bounds[1]))).multiply(
                                                gCellsize)).divide(g2CellsizeSquared, scale, rm)).doubleValue());
                                    } else {
                                        areaProportion = Math.abs(((((g2.getCellYBigDecimal(cellID3).add(
                                                g2HalfCellsize)).subtract(BigDecimal.valueOf(bounds[1]))).multiply((g2.getCellXBigDecimal(cellID3).add(
                                                g2HalfCellsize)).subtract(BigDecimal.valueOf(bounds[0])))).divide(
                                                g2CellsizeSquared, scale, rm)).doubleValue());
                                    }
                                    tg1.addToCell(r, c, d3 * areaProportion);
                                    tg2.addToCell(r, c, areaProportion);
                                }
                            }
                            if (!g2.isInGrid(cellID4) && d4 != g2NoDataValue) {
                                if (cellID4 != cellID2 && cellID4 != cellID3) {
                                    areaProportion = Math.abs(((((g2.getCellYBigDecimal(cellID4).add(g2HalfCellsize)).subtract(
                                            BigDecimal.valueOf(bounds[1]))).multiply(
                                            BigDecimal.valueOf(bounds[2]).subtract(
                                                    (g2.getCellXBigDecimal(cellID4)).subtract(
                                                            g2HalfCellsize)))).divide(
                                                    g2CellsizeSquared, scale, rm)).doubleValue());
                                    tg1.addToCell(r, c, d4 * areaProportion);
                                    tg2.addToCell(r, c, areaProportion);
                                }
                            }
                        }
                    }
                }
                // The values are normalised by dividing the aggregate Grid sum by the proportion of cells with grid values.
                for (r = 0; r <= nrows; r++) {
                    env.checkAndMaybeFreeMemory();
                    for (c = 0; c <= ncols; c++) {
                        d1 = tg2.getCell(r, c);
                        if (!(d1 == 0.0d || d1 == noDataValue)) {
                            g.addToCell(r, c,
                                    w * tg1.getCell(r, c) / d1);
                        }
                    }
                }
            }
        }
        env.checkAndMaybeFreeMemory();
    }

    /**
     * Returns grid with values added from a file.
     *
     * @param g the Grids_GridDouble to be processed
     * @param file the file contining values to be added.
     * @param type the type of file. Supported types include "xyv", "xy", "idxy"
     */
    public void addToGrid(Grids_GridDouble g, Path file, String type)
            throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        if (type.equalsIgnoreCase("xyv")) {
            try {
                StreamTokenizer st;
                st = new StreamTokenizer(env.env.io.getBufferedReader(file));
                st.eolIsSignificant(false);
                st.parseNumbers();
                st.whitespaceChars(',', ',');
                st.wordChars('"', '"');
                int tokenType = st.nextToken();
                String alternator = "x";
                double x = 0.0d;
                double y = 0.0d;
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
                                    g.addToCell(x, y, st.nval);
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
                StreamTokenizer st;
                st = new StreamTokenizer(env.env.io.getBufferedReader(file));
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
                                g.addToCell(x, y, 1.0d);
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
                StreamTokenizer st = new StreamTokenizer(env.env.io.getBufferedReader(file));
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
                                    g.addToCell(x, y, 1.0d);
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
        env.checkAndMaybeFreeMemory();
    }

    /**
     * Multiply g0 and g1 and return a new grid. It is assumed that the
     * dimensions are all the same;
     *
     * @param g0
     * @param g1
     * @return
     */
    public Grids_GridDouble multiply(Grids_GridDouble g0, Grids_GridDouble g1)
            throws IOException, ClassNotFoundException {
        Grids_GridDouble r;
        long nRows = g0.getNRows();
        long nCols = g0.getNCols();
        Generic_Path dir = new Generic_Path(Generic_IO.createNewFile(
                files.getGeneratedGridDoubleDir().getPath()));
        r = GridDoubleFactory.create(dir, g0, 0L, 0L, nRows - 1, nCols - 1);
        double noDataValue0 = g0.getNoDataValue();
        double noDataValue1 = g1.getNoDataValue();
        for (long row = 0L; row < nRows; row++) {
            for (long col = 0L; col < nCols; col++) {
                double v0 = g0.getCell(row, col);
                double v1 = g1.getCell(row, col);
                if (v0 != noDataValue0) {
                    if (v1 != noDataValue1) {
                        r.setCell(row, col, v0 * v1);
                    }
                }
            }
        }
        return r;
    }

    /**
     * Divide g0 by g1 and return a new grid. It is assumed that the dimensions
     * are all the same;
     *
     * @param g0 Numerator
     * @param g1 Denominator
     * @return
     */
    public Grids_GridDouble divide(Grids_GridDouble g0, Grids_GridDouble g1)
            throws IOException, ClassNotFoundException {
        Grids_GridDouble result;
        long nRows = g0.getNRows();
        long nCols = g0.getNCols();
        Generic_Path dir = new Generic_Path(Generic_IO.createNewFile(
                files.getGeneratedGridDoubleDir()));
        result = GridDoubleFactory.create(dir, g0, 0L, 0L, nRows - 1,
                nCols - 1);
        double noDataValue0 = g0.getNoDataValue();
        double noDataValue1 = g1.getNoDataValue();
        for (long row = 0L; row < nRows; row++) {
            for (long col = 0L; col < nCols; col++) {
                double v0 = g0.getCell(row, col);
                double v1 = g1.getCell(row, col);
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
     * this method then cache operations are initiated prior to ret r y ing. If
     * false then OutOfMemoryErrors are caught and thrown. NB. In the
     * calculation of the sum and the mean if there is a cell in grid which has
     * a data value then the result which incorporates that cell has a data
     * value. For this result cell, any of the cells in grid which have
     * noDataValues their value is taken as that of the average of its nearest
     * cells with a value. In the calculation of the max and the min
     * noDataValues are simply ignored. Formerly noDataValues were treated as
     * the average of values within a result cell. TODO: implement median, mode
     * and variance aggregations. @return @param colOffset @param gridFactory
     * @param colOffset @param gridFactory @return
     */
    public Grids_GridDouble aggregate(Grids_GridNumber grid,
            int cellFactor, String statistic, int rowOffset, int colOffset,
            Grids_GridFactoryDouble gridFactory) throws IOException,
            ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
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
                        + " ), gridFactory( " + gridFactory + " ) )");
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
        Grids_Dimensions resultDimensions = new Grids_Dimensions(resultXMin,
                resultXMax, resultYMin, resultYMax, resultCellsize);
        // Initialise result
        gridFactory.setNoDataValue(noDataValue);
        Generic_Path dir = new Generic_Path(Generic_IO.createNewFile(
                files.getGeneratedGridDoubleDir()));
        Grids_GridDouble result = (Grids_GridDouble) gridFactory.create(dir,
                resultNrows, resultNcols, resultDimensions);

        long row;
        long col;
        double x;
        double y;
        double value;

        // sum
        if (statistic.equalsIgnoreCase("sum")) {
            dir = new Generic_Path(Generic_IO.createNewFile(
                    files.getGeneratedGridDoubleDir()));
            Grids_GridDouble count;
            count = (Grids_GridDouble) gridFactory.create(dir, resultNrows,
                    resultNcols, resultDimensions);
            dir = new Generic_Path(Generic_IO.createNewFile(
                    files.getGeneratedGridDoubleDir()));
            Grids_GridDouble normaliser;
            normaliser = (Grids_GridDouble) gridFactory.create(dir, resultNrows,
                    resultNcols, resultDimensions);
            for (row = 0; row < nrows; row++) {
                for (col = 0; col < ncols; col++) {
                    x = grid.getCellXDouble(col);
                    y = grid.getCellYDouble(row);
                    if (result.isInGrid(x, y)) {
                        value = grid.getCellDouble(row, col);
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
            dir = new Generic_Path(Generic_IO.createNewFile(
                    files.getGeneratedGridDoubleDir()));
            Grids_GridDouble numerator = gridFactory.create(dir, resultNrows,
                    resultNcols, resultDimensions);
            dir = new Generic_Path(Generic_IO.createNewFile(
                    files.getGeneratedGridDoubleDir()));
            Grids_GridDouble denominator = gridFactory.create(dir,
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
    //            return aggregate( grid, resultCellsize, statistic, resultXllcorner, resultYllcorner, new Grids_GridFactoryDouble() );
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
     * @param gf The Abstract2DSquareCellDoubleFactory used to create
     * _AbstractGrid2DSquareCell_HashSet Use this aggregate method if result is
     * to have a new spatial frame. NB. In the calculation of the sum and the
     * mean if there is a cell in grid which has a data value then the result
     * which incorporates that cell has a data value. For this result cell, any
     * of the cells in grid which have noDataValues their value is taken as that
     * of the average of its nearest cells with a value. In the calculation of
     * the max and the min noDataValues are simply ignored. Formerly
     * noDataValues were treated as the average of values within a result cell.
     * TODO: implement median, mode and variance aggregations.
     * <a name="aggregate(AbstractGrid2DSquareCell,
     * String,BigDecimal[],Grid2DSquareCellDoubleFactory,boolean)"></a>
     * @return
     */
    public Grids_GridDouble aggregate(Grids_GridNumber grid,
            String statistic, Grids_Dimensions resultDimensions,
            Grids_GridFactoryDouble gf) throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
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
        BigDecimal rCellsize = resultDimensions.getCellsize();
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
        if (rCellsize.compareTo(dimensionsCellsize) != 1) {
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
            RoundingMode rm = RoundingMode.HALF_EVEN;
            BigDecimal t0 = rCellsize.divide(dimensionsCellsize,
                    Math.max(rCellsize.scale(), dimensionsCellsize.scale()) + 2,
                    rm);
            BigDecimal t1 = resultXMin.divide(
                    dimensionsCellsize,
                    Math.max(resultXMin.scale(), dimensionsCellsize.scale()) + 2,
                    rm);
            BigDecimal t2 = dimensionsXMin.divide(
                    dimensionsCellsize,
                    Math.max(dimensionsXMin.scale(), dimensionsCellsize.scale()) + 2,
                    rm);
            BigDecimal t3 = resultYMin.divide(
                    dimensionsCellsize,
                    Math.max(resultYMin.scale(), dimensionsCellsize.scale()) + 2,
                    rm);
            BigDecimal t4 = dimensionsYMin.divide(
                    dimensionsCellsize,
                    Math.max(dimensionsYMin.scale(), dimensionsCellsize.scale()) + 2,
                    rm);
            if ((t0.compareTo(new BigDecimal(t0.toBigInteger().toString())) == 0)
                    && (t1.compareTo(new BigDecimal(t1.toBigInteger().toString())) == t2.compareTo(new BigDecimal(t2.toBigInteger().toString())))
                    && (t3.compareTo(new BigDecimal(t3.toBigInteger().toString())) == t4.compareTo(new BigDecimal(t4.toBigInteger().toString())))) {
                int cellFactor = rCellsize.divide(dimensionsCellsize, 2,
                        RoundingMode.UNNECESSARY).intValue();
                int rowOffset = dimensionsYMin.subtract(
                        resultYMin.divide(dimensionsCellsize, scale,
                                RoundingMode.HALF_EVEN)).intValue();
                int colOffset = dimensionsXMin.subtract(
                        resultXMin.divide(dimensionsCellsize, scale, 
                                RoundingMode.HALF_EVEN)).intValue();
                return aggregate(grid, cellFactor, statistic, rowOffset, colOffset, gf);
            }
        }
        // Calculate resultNrows and resultHeight
        long resultNrows = 1L;
        BigDecimal resultHeight = new BigDecimal(rCellsize.toString());
        //double resultHeight = resultCellsize;
        while (resultYMin.add(resultHeight).compareTo(dimensionsYMax) == -1) {
            resultNrows++;
            resultHeight = resultHeight.add(rCellsize);
        }
        //while ( ( resultYllcorner + resultHeight ) < ( yllcorner + height ) ) {
        //    resultNrows ++;
        //    resultHeight += resultCellsize;
        //}
        // Calculate resultNcols and resultWidth
        long resultNcols = 1L;
        BigDecimal resultWidth = new BigDecimal(rCellsize.toString());
        //double resultWidth = resultCellsize;
        while (resultXMin.add(resultWidth).compareTo(dimensionsXMax) == -1) {
            resultNrows++;
            resultWidth = resultWidth.add(rCellsize);
        }
        //while ( ( resultXllcorner + resultWidth ) < ( xllcorner + width ) ) {
        //    resultNcols ++;
        //    resultWidth += resultCellsize;
        //}
        resultXMax = dimensionsXMin.add(resultWidth);
        resultYMax = dimensionsYMin.add(resultHeight);
        // Initialise result
        gf.setNoDataValue(noDataValue);
        Generic_Path dir = new Generic_Path(Generic_IO.createNewFile(
                files.getGeneratedGridDoubleDir()));
        Grids_GridDouble result = gf.create(dir, resultNrows, resultNcols,
                resultDimensions);
        long row;
        long col;
        double x;
        double y;
        double value;
        double cellsize = dimensionsCellsize.doubleValue();
        double resultCellsized = rCellsize.doubleValue();
        // sum
        if (statistic.equalsIgnoreCase("sum")) {
            Grids_GridDouble totalValueArea;
            dir = new Generic_Path(Generic_IO.createNewFile(
                    files.getGeneratedGridDoubleDir()));
            totalValueArea = gf.create(dir, resultNrows, resultNcols, resultDimensions);
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
                    value = grid.getCellDouble(row, col);
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
            double denominator = (rCellsize.doubleValue() * rCellsize.doubleValue()) / (cellsize * cellsize);
            Grids_GridDouble sum = aggregate(grid, "sum", resultDimensions, gf);
            addToGrid(result, sum, 1.0d / denominator);
        }

        // max
        if (statistic.equalsIgnoreCase("max")) {
            double max;
            double[] bounds;
            double halfCellsize = grid.getCellsizeDouble() / 2.0d;
            for (row = 0; row < nrows; row++) {
                for (col = 0; col < ncols; col++) {
                    value = grid.getCellDouble(row, col);
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
                    value = grid.getCellDouble(row, col);
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
        env.checkAndMaybeFreeMemory();
        return result;
    }

    /**
     * Returns a double[][] of grid values
     *
     * @param g
     * @param row
     * @param cellDistance
     * @return
     */
    protected double[][] getRowProcessInitialData(Grids_GridDouble g,
            int cellDistance, long row) throws IOException, ClassNotFoundException {
        int l = (cellDistance * 2) + 1;
        double[][] result = new double[l][l];
        long col;
        long r;
        for (r = -cellDistance; r <= cellDistance; r++) {
            for (col = -cellDistance; col <= cellDistance; col++) {
                double value = g.getCell(r + row, col);
                result[(int) r + cellDistance][(int) col + cellDistance]
                        = value;
            }
        }
        return result;
    }

    /**
     * Returns a double[][] based on previous which has been shuffled
     *
     * @param g
     * @param col
     * @param previous
     * @param cellDistance
     * @param row
     * @return
     */
    protected double[][] getRowProcessData(Grids_GridDouble g,
            double[][] previous, int cellDistance, long row, long col)
            throws IOException, ClassNotFoundException {
        double[][] result = previous;
        if (col == 0) {
            return getRowProcessInitialData(g, cellDistance, row);
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
                        = g.getCell((long) i + row, (long) col + cellDistance);
            }
        }
        return result;
    }

    /**
     * For outputting g in various formats.
     *
     * @param g
     * @param outDir
     * @param ie
     * @param imageTypes
     * @param eage
     */
    public void output(Grids_GridNumber g, Path outDir,
            Grids_ImageExporter ie, String[] imageTypes,
            Grids_ESRIAsciiGridExporter eage)
            throws IOException, ClassNotFoundException {
        System.out.println("Output " + g.toString());
        if (ie == null) {
            ie = new Grids_ImageExporter(env);
        }
        if (imageTypes == null) {
            imageTypes = new String[1];
            imageTypes[0] = "PNG";
        }
        if (eage == null) {
            eage = new Grids_ESRIAsciiGridExporter(env);
        }
        //int _StringLength = 1000;
        String dotASC = ".asc";
        String noDataValue = "-9999.0";
        String s;
        Path file;
        int i;
        int l = imageTypes.length;
        for (i = 0; i < l; i++) {
            s = g.getName() + "." + imageTypes[i];
            file = new Generic_Path(Paths.get(outDir.toString(), s));
            ie.toGreyScaleImage(g, this, file, imageTypes[i]);
        }
        s = g.getName() + dotASC;
        file = Paths.get(outDir.toString(), s);
        eage.toAsciiFile(g, file, noDataValue);
    }

    /**
     *
     * @param g
     * @param outDir
     * @param ie
     * @param imageTypes
     * @param hoome
     */
    public void outputImage(Grids_GridNumber g, Generic_Path outDir,
            Grids_ImageExporter ie, String[] imageTypes, boolean hoome)
            throws IOException, ClassNotFoundException {
        try {
            System.out.println("Output " + g.toString());
            if (ie == null) {
                ie = new Grids_ImageExporter(env);
            }
            if (imageTypes == null) {
                imageTypes = new String[1];
                imageTypes[0] = "PNG";
            }
            String string;
            String string_DOT = ".";
            Path file;
            int i;
            int l = imageTypes.length;
            for (i = 0; i < l; i++) {
                string = g.getName() + string_DOT + imageTypes[i];
                file = Paths.get(outDir.toString(), string);
                ie.toGreyScaleImage(g, this, file, imageTypes[i]);
            }
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                if (env.cacheChunksExcept_Account(g, hoome) < 1) {
                    throw e;
                }
                env.initMemoryReserve();
                outputImage(g, outDir, ie, imageTypes, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     *
     * @param g
     * @param outDir
     * @param eage
     * @param hoome
     */
    public void outputESRIAsciiGrid(Grids_GridNumber g, Path outDir,
            Grids_ESRIAsciiGridExporter eage, boolean hoome) throws IOException,
            ClassNotFoundException {
        try {
            if (eage == null) {
                eage = new Grids_ESRIAsciiGridExporter(env);
            }
            String methodName = "outputESRIAsciiGrid("
                    + g.getClass().getName() + "(" + g.toString() + "),"
                    + outDir.getClass().getName() + "(" + outDir + "),"
                    + eage.getClass().getName() + "(" + eage.toString() + "),"
                    + "boolean(" + hoome + "))";
            System.out.println(methodName);
            String string_DotASC = ".asc";
            String ndv = "-9999.0";
            String string;
            Path file;
            string = g.getName() + string_DotASC;
            file = Paths.get(outDir.toString(), string);
            eage.toAsciiFile(g, file, ndv);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                if (!env.cacheChunk(env.HOOMEF)) {
                    throw e;
                }
                env.initMemoryReserve();
                outputESRIAsciiGrid(g, outDir, eage, hoome);
            } else {
                throw e;
            }
        }
    }

}