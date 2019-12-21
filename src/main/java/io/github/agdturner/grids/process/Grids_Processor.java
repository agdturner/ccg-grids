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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
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
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkFactoryIntArray;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkFactoryIntMap;
import io.github.agdturner.grids.d2.grid.i.Grids_GridFactoryInt;
import io.github.agdturner.grids.core.Grids_Environment;
import io.github.agdturner.grids.core.Grids_Object;
import io.github.agdturner.grids.core.Grids_Strings;
import io.github.agdturner.grids.d2.chunk.b.Grids_ChunkFactoryBinary;
import io.github.agdturner.grids.d2.grid.b.Grids_GridFactoryBoolean;
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
import uk.ac.leeds.ccg.agdt.generic.io.Generic_FileStore;

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
     * For convenience.
     */
    protected Grids_Files files;

    /**
     * Grids_ChunkFactoryInt
     */
    public Grids_ChunkFactoryInt defaultChunkIntFactory;

    /**
     * Grids_ChunkFactoryIntSinglet
     */
    //public Grids_ChunkFactoryBoolean chunkBooleanFactory;
    /**
     * Grids_ChunkFactoryIntSinglet
     */
    //public Grids_ChunkFactoryBinary chunkBinaryFactory;
    /**
     * Grids_ChunkFactoryIntSinglet
     */
    //public Grids_ChunkFactoryIntSinglet GridChunkIntFactory;
    /**
     * Grids_ChunkIntArrayFactory
     */
    //public Grids_ChunkIntArrayFactory GridChunkIntArrayFactory;
    /**
     * Grids_ChunkFactoryIntMap
     */
    //public Grids_ChunkFactoryIntMap GridChunkIntMapFactory;
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
    //public Grids_ChunkFactoryDouble DefaultGridChunkDoubleFactory;
    /**
     * Grids_ChunkFactoryDoubleSinglet
     */
    //public Grids_ChunkFactoryDoubleSinglet GridChunkDoubleFactory;
    /**
     * Grids_ChunkFactoryDoubleArray
     */
//    public Grids_ChunkFactoryDoubleArray GridChunkDoubleArrayFactory;
    /**
     * Grids_ChunkFactoryDoubleMap
     */
//    public Grids_ChunkFactoryDoubleMap GridChunkDoubleMapFactory;
    /**
     * Grids_GridFactoryDouble
     */
    public Grids_GridFactoryDouble GridDoubleFactory;

    /**
     * @param e The grids environment.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_Processor(Grids_Environment e) throws Exception, IOException,
            ClassNotFoundException {
        super(e);
        StartTime = System.currentTimeMillis();
        files = e.files;
        int chunkNRows = 512;
        int chunkNCols = 512;
        initFactoriesAndFileStores(chunkNRows, chunkNCols);
    }

    /**
     * Initialises factories and file stores.
     */
    private void initFactoriesAndFileStores(int chunkNRows, int chunkNCols)
            throws Exception {
        // Boolean
        String s = Grids_Strings.s_GridBoolean;
        Path dir = Paths.get(files.getGeneratedGridBooleanDir().toString());
        Generic_FileStore store;
        if (Files.exists(dir)) {
            store = new Generic_FileStore(dir);
        } else {
            store = new Generic_FileStore(files.getGeneratedDir(), s);
        }
        GridBooleanFactory = new Grids_GridFactoryBoolean(env, store,
                new Grids_ChunkFactoryBoolean(), chunkNRows, chunkNCols);
        // Binary
        s = Grids_Strings.s_GridBinary;
        dir = Paths.get(files.getGeneratedGridBinaryDir().toString());
        if (Files.exists(dir)) {
            store = new Generic_FileStore(dir);
        } else {
            store = new Generic_FileStore(files.getGeneratedDir(), s);
        }
        GridBinaryFactory = new Grids_GridFactoryBinary(env, store,
                new Grids_ChunkFactoryBinary(), chunkNRows, chunkNCols);
        // Int
        s = Grids_Strings.s_GridInt;
        dir = Paths.get(files.getGeneratedGridIntDir().toString());
        if (Files.exists(dir)) {
            store = new Generic_FileStore(dir);
        } else {
            store = new Generic_FileStore(files.getGeneratedDir(), s);
        }
        GridIntFactory = new Grids_GridFactoryInt(env, store,
                new Grids_ChunkFactoryIntSinglet(Integer.MIN_VALUE),
                new Grids_ChunkFactoryIntArray(),
                chunkNRows, chunkNCols);
        // Double
        store = getStore(Paths.get(files.getGeneratedGridDoubleDir().toString()),
                Grids_Strings.s_GridDouble);
        GridDoubleFactory = new Grids_GridFactoryDouble(env, store,
                new Grids_ChunkFactoryDoubleSinglet(-Double.MAX_VALUE),
                new Grids_ChunkFactoryDoubleArray(),
                chunkNRows, chunkNCols);
    }

    protected Generic_FileStore getStore(Path dir, String s) throws IOException,
            Exception {
        Generic_FileStore r;
        if (Files.exists(dir)) {
            r = new Generic_FileStore(dir);
        } else {
            r = new Generic_FileStore(files.getGeneratedDir(), s);
        }
        return r;
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
     * Modifies grid by setting to grid.noDataValue those cells coincident with
     * mask.noDataValue cells. Warning!!! The grid and mask are assumed to be
     * coincident have the same origin and the same chunk structure. @TODO add
     * flexibility so the mask can have a different chunk structure to g.
     *
     * @param g The Grids_GridNumber that the mask will be applied to.
     * @param mask The Grids_GridNumber to use as a mask.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void mask(Grids_GridNumber g, Grids_GridNumber mask)
            throws IOException, ClassNotFoundException, Exception {
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
                Grids_GridDouble maskDouble = (Grids_GridDouble) mask;
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
                Iterator<Grids_2D_ID_int> ite = maskDouble.getChunkIDs().iterator();
                Grids_ChunkDouble maskChunk;
                while (ite.hasNext()) {
                    maskChunk = (Grids_ChunkDouble) mask.getChunk(ite.next());
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
            double max) throws IOException, ClassNotFoundException, Exception {
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
            double max) throws IOException, ClassNotFoundException, Exception {
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
    public Grids_GridDouble rescale(Grids_GridDouble g, String type, double min,
            double max, boolean hoome) throws IOException,
            ClassNotFoundException, Exception {
        try {
            return rescale(g, type, min, max);
        } catch (java.lang.OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                if (env.cacheChunksExcept_Account(g, hoome) < 1) {
                    throw e;
                }
                env.initMemoryReserve(env.env);
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
            double min, double max) throws IOException, ClassNotFoundException,
            Exception {
        env.checkAndMaybeFreeMemory();
        Grids_GridDouble r;
        long nrows = g.getNRows();
        long ncols = g.getNCols();
        int nChunkCols = g.getNChunkCols();
        int nChunkRows = g.getNChunkRows();
        double ndv = g.getNoDataValue();
        double range = max - min;
        Grids_StatsNumber stats = g.getStats();
        double minGrid = stats.getMin(true).doubleValue();
        double maxGrid = stats.getMax(true).doubleValue();
        double rangeGrid = maxGrid - minGrid;
        r = GridDoubleFactory.create(g, 0, 0, nrows - 1, ncols - 1);
        r.setName(g.getName());
        System.out.println(r.toString());
        Grids_ChunkDouble rChunk;
        Grids_2D_ID_int chunkID;
        if (type == null) {
            // if range of either input or output range is zero return min for all non noDataValues
            if (rangeGrid == 0.0d || range == 0.0d) {
                // Better to go through chunks rather than rows. Though it 
                // does assume that the structure of the grid and outputGrid 
                // are the same.
                for (int cr = 0; cr < nChunkRows; cr++) {
                    int chunkNRows = g.getChunkNRows(cr);
                    for (int cc = 0; cc < nChunkCols; cc++) {
                        chunkID = new Grids_2D_ID_int(cr, cc);
                        env.addToNotToCache(g, chunkID);
                        env.addToNotToCache(r, chunkID);
                        env.checkAndMaybeFreeMemory();
                        int chunkNCols = g.getChunkNCols(cc);
                        Grids_ChunkDouble gChunk = g.getChunk(chunkID);
                        rChunk = r.getChunk(chunkID);
                        for (int ccr = 0; ccr < chunkNRows; ccr++) {
                            for (int ccc = 0; ccc < chunkNCols; ccc++) {
                                double value = gChunk.getCell(ccr, ccc);
                                if (value != ndv) {
                                    r.setCell(rChunk, ccr, ccc, min);
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
                for (int cr = 0; cr < nChunkRows; cr++) {
                    int chunkNRows = g.getChunkNRows(cr);
                    for (int cc = 0; cc < nChunkCols; cc++) {
                        chunkID = new Grids_2D_ID_int(cr, cc);
                        env.addToNotToCache(g, chunkID);
                        env.addToNotToCache(r, chunkID);
                        env.checkAndMaybeFreeMemory();
                        int chunkNCols = g.getChunkNCols(cc);
                        Grids_ChunkDouble gChunk = g.getChunk(chunkID);
                        rChunk = r.getChunk(chunkID);
                        for (int ccr = 0; ccr < chunkNRows; ccr++) {
                            for (int ccc = 0; ccc < chunkNCols; ccc++) {
                                double value = gChunk.getCell(ccr, ccc);
                                if (value != ndv) {
                                    v = (((value - minGrid)
                                            / rangeGrid) * range) + min;
                                    r.setCell(rChunk, ccr, ccc, v);
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
                        double value = g.getCell(row, col);
                        if (value != ndv) {
                            r.setCell(row, col, Math.log(value));
                        }
                    }
                }
                r = rescale(r, null, min, max);
                r.setName(g.getName() + "_logRescale");
                env.checkAndMaybeFreeMemory();
            } else {
                throw new Exception("Unable to rescale: type " + type
                        + "not recognised.");
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
            double max, boolean hoome) throws IOException, ClassNotFoundException, Exception {
        try {
            return rescale(g, type, min, max);
        } catch (java.lang.OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                if (env.cacheChunksExcept_Account(g, hoome) < 1) {
                    throw e;
                }
                env.initMemoryReserve(env.env);
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
            double max) throws IOException, ClassNotFoundException, Exception {
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
        Grids_GridDouble r = GridDoubleFactory.create(g, 0, 0, nrows - 1,
                ncols - 1);
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
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void setLarger(Grids_GridDouble g, HashSet<Grids_2D_ID_long> cellIDs)
            throws IOException, ClassNotFoundException, Exception {
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
            throws IOException, ClassNotFoundException, Exception {
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
     * Adds value to grid for cells with cell ID in cellIDs.
     *
     * @param g The grid to be processed.
     * @param cellIDs The cell IDs.
     * @param v The value to be added.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToGrid(Grids_GridDouble g, HashSet<Grids_2D_ID_long> cellIDs,
            double v) throws IOException, ClassNotFoundException,
            Exception {
        env.checkAndMaybeFreeMemory();
        Iterator<Grids_2D_ID_long> ite = cellIDs.iterator();
        while (ite.hasNext()) {
            g.addToCell(ite.next(), v);
            env.checkAndMaybeFreeMemory();
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
            double value) throws IOException, ClassNotFoundException, Exception {
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
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToGrid(
            Grids_GridDouble grid,
            Grids_2D_ID_long[] cellIDs,
            double value) throws IOException, ClassNotFoundException, Exception {
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
    public void addToGrid(Grids_GridDouble g, Grids_GridDouble g2, double w, int dp, RoundingMode rm)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        if (g2 != null) {
            addToGrid(g, g2, 0L, 0L, g2.getNRows() - 1L, g2.getNCols() - 1L, w, dp, rm);
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
            long startRow, long startCol, long endRow, long endCol, double w, int dp, RoundingMode rm)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        Grids_Dimensions dimensions = g2.getDimensions();
        BigDecimal xMin = dimensions.getXMin();
        BigDecimal yMin = dimensions.getYMin();
        BigDecimal c = dimensions.getCellsize();
        BigDecimal[] dc = new BigDecimal[5];
        dc[1] = xMin.add(new BigDecimal(startCol).multiply(c));
        dc[2] = yMin.add(new BigDecimal(startRow).multiply(c));
        dc[3] = xMin.add(new BigDecimal(endCol - startCol + 1L).multiply(c));
        dc[4] = yMin.add(new BigDecimal(endRow - startRow + 1L).multiply(c));
        addToGrid(g, g2, startRow, startCol, endRow, endCol, dc, w, dp, rm);
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
            BigDecimal[] dc, double w, int dp, RoundingMode rm) throws IOException,
            ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        long nrows = g.getNRows();
        long ncols = g.getNCols();
        double noDataValue = g.getNoDataValue();
        Grids_Dimensions gD = g.getDimensions();
        double g2NoDataValue = g2.getNoDataValue();
        Grids_Dimensions g2D = g2.getDimensions();
        Grids_GridFactoryDouble gf = this.GridDoubleFactory;
        // If the region to be added is outside g then return.
        if ((dc[1].compareTo(gD.getXMax()) == 1)
                || (dc[3].compareTo(gD.getXMin()) == -1)
                || (dc[2].compareTo(gD.getYMax()) == 1)
                || (dc[4].compareTo(gD.getYMin()) == -1)) {
            return;
        }
        BigDecimal gC = gD.getCellsize();
        BigDecimal g2C = g2D.getCellsize();
        BigDecimal g2CH = g2D.getHalfCellsize();
        if (g2C.compareTo(gC) == -1) {
            throw new UnsupportedOperationException();
        } else {
            // If g2Cellsize is the same as gCellsize g and g2 align
            if ((g2C.compareTo(gC) == 0)
                    && ((g2D.getXMin().remainder(gC)).compareTo(
                            (gD.getXMin().remainder(gC))) == 0)
                    && ((g2D.getYMin().remainder(gC)).compareTo(
                            (gD.getYMin().remainder(gC))) == 0)) {
                //println( "grids Align!" );
                // TODO: Control precision using xBigDecimal and yBigDecimal
                // rather than using x and y.
                for (long row = startRow; row <= endRow; row++) {
                    env.checkAndMaybeFreeMemory();
                    BigDecimal y = g2.getCellYBigDecimal(row);
                    for (long col = startCol; col <= endCol; col++) {
                        BigDecimal x = g2.getCellXBigDecimal(col);
                        double v = g2.getCell(row, col);
                        if (v != g2NoDataValue) {
                            if (v != 0.0d) {
                                g.addToCell(x, y, v * w);
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
                tg1 = gf.create(nrows, ncols, gD);
                tg2 = gf.create(nrows, ncols, gD);
                BigDecimal[] bounds;
                Grids_2D_ID_long i0;
                Grids_2D_ID_long i1;
                Grids_2D_ID_long i2;
                Grids_2D_ID_long i3;
                double d0;
                double d1;
                double d2;
                double d3;
                // gCellsize halved
                BigDecimal gCH = g.getCellsize().divide(BigDecimal.valueOf(2));
                // gCellsize squared
                BigDecimal gCS = gC.multiply(gC);
                // g2Cellsize squared
                BigDecimal g2CS = g2C.multiply(g2C);
                // Area proportions
                double aP1 = (gCS.divide(g2CS, dp, rm)).doubleValue();
                double aP;
                for (int r = 0; r < nrows; r++) {
                    env.checkAndMaybeFreeMemory();
                    for (int c = 0; c < ncols; c++) {
                        bounds = g.getCellBounds(gCH, r, c);
                        //x = g.getCellXDouble(col);
                        //y = g.getCellYDouble(row);
                        i0 = g2.getCellID(bounds[0], bounds[3]);
                        i1 = g2.getCellID(bounds[2], bounds[3]);
                        i2 = g2.getCellID(bounds[0], bounds[1]);
                        i3 = g2.getCellID(bounds[2], bounds[1]);
                        d0 = g2.getCell(i0.getRow(), i0.getCol());
                        if (i0.equals(i1) && i1.equals(i2)) {
                            if (d0 != g2NoDataValue) {
                                tg1.addToCell(r, c, d0 * aP1);
                                tg2.addToCell(r, c, aP1);
                            }
                        } else {
                            d1 = g2.getCell(i1.getRow(), i1.getCol());
                            d2 = g2.getCell(i2.getRow(), i2.getCol());
                            d3 = g2.getCell(i3.getRow(), i3.getCol());
                            if (!g2.isInGrid(i0.getRow(), i0.getCol())
                                    && d0 != g2NoDataValue) {
                                aP = getAP(bounds, g2, i0, i1, i2, gC, g2CS,
                                        g2CH, dp, rm);
                                tg1.addToCell(r, c, d0 * aP);
                                tg2.addToCell(r, c, aP);
                            }
                            if (!g2.isInGrid(i1) && d1 != g2NoDataValue) {
                                if (i1.equals(i0)) {
                                    aP = getAP13(bounds, g2, i1, i3, gC, g2CS, g2CH, dp, rm);
                                    tg1.addToCell(r, c, d1 * aP);
                                    tg2.addToCell(r, c, aP);
                                }
                            }
                            if (!g2.isInGrid(i2) && d2 != g2NoDataValue) {
                                if (!i2.equals(i0)) {
                                    aP = getAP23(bounds, g2, i2, i3, gC, g2CS, g2CH, dp, rm);
                                    tg1.addToCell(r, c, d2 * aP);
                                    tg2.addToCell(r, c, aP);
                                }
                            }
                            if (!g2.isInGrid(i3) && d3 != g2NoDataValue) {
                                if (i3 != i1 && i3 != i2) {
                                    aP = getAP3(bounds, g2, i3, gC, g2CS, g2CH, dp, rm);
                                    tg1.addToCell(r, c, d3 * aP);
                                    tg2.addToCell(r, c, aP);
                                }
                            }
                        }
                    }
                }
                // The values are normalised by dividing the aggregate Grid 
                // sum by the proportion of cells with grid values.
                for (long r = 0; r < nrows; r++) {
                    env.checkAndMaybeFreeMemory();
                    for (long c = 0; c < ncols; c++) {
                        d0 = tg2.getCell(r, c);
                        if (!(d0 == 0.0d || d0 == noDataValue)) {
                            g.addToCell(r, c, w * tg1.getCell(r, c) / d0);
                        }
                    }
                }
            }
        }
        env.checkAndMaybeFreeMemory();
    }

    protected double getAP(BigDecimal[] bounds, Grids_GridNumber g2,
            Grids_2D_ID_long i1, Grids_2D_ID_long i2, Grids_2D_ID_long i3,
            BigDecimal gC, BigDecimal g2CS, BigDecimal g2CH, int dp,
            RoundingMode rm) {
        double aP;
        if (i1.equals(i2) || i1.equals(i3)) {
            if (i1.equals(i2)) {
                aP = Math.abs((((bounds[3]).subtract(
                        g2.getCellYBigDecimal(i1).subtract(
                                g2CH)).multiply(gC)).divide(
                        g2CS, dp, rm)).doubleValue());
            } else {
                aP = Math.abs(((((g2.getCellXBigDecimal(i1).add(
                        g2CH)).subtract((bounds[0]))).multiply(
                        gC)).divide(g2CS, dp, rm)).doubleValue());
            }
        } else {
            aP = Math.abs(
                    (((bounds[3]).subtract(
                            g2.getCellYBigDecimal(i1).subtract(g2CH))).multiply(
                            (g2.getCellXBigDecimal(i1).add(
                                    g2CH.subtract((bounds[0])))).divide(
                                    g2CS, dp, rm))).doubleValue());
        }
        return aP;
    }

    protected double getAP13(BigDecimal[] bounds, Grids_GridNumber g2,
            Grids_2D_ID_long i1, Grids_2D_ID_long i3,
            BigDecimal gC, BigDecimal g2CS, BigDecimal g2CH, int dp,
            RoundingMode rm) {
        double aP;
        if (i1.equals(i3)) {
            aP = Math.abs(((((bounds[2]).subtract(
                    g2.getCellXBigDecimal(i1).subtract(
                            g2CH))).multiply(gC)).divide(
                    g2CS, dp, rm)).doubleValue());
        } else {
            aP = Math.abs((((bounds[3]).subtract(
                    g2.getCellYBigDecimal(i1).subtract(
                            g2CH))).multiply((bounds[2]).subtract(
                            g2.getCellXBigDecimal(i1).subtract(
                                    g2CH))).divide(g2CS, dp, rm)).doubleValue());
        }
        return aP;
    }

    protected double getAP23(BigDecimal[] bounds, Grids_GridNumber g2,
            Grids_2D_ID_long i2, Grids_2D_ID_long i3,
            BigDecimal gC, BigDecimal g2CS, BigDecimal g2CH, int dp,
            RoundingMode rm) {
        double aP;
        if (i2.equals(i3)) {
            aP = Math.abs(((((g2.getCellYBigDecimal(i2).add(
                    g2CH)).subtract((bounds[1]))).multiply(
                    gC)).divide(g2CS, dp, rm)).doubleValue());
        } else {
            aP = Math.abs(((((g2.getCellYBigDecimal(i2).add(
                    g2CH)).subtract((bounds[1]))).multiply(
                    (g2.getCellXBigDecimal(i2).add(g2CH))
                            .subtract((bounds[0])))).divide(g2CS, dp, rm))
                    .doubleValue());
        }
        return aP;
    }

    protected double getAP3(BigDecimal[] bounds, Grids_GridNumber g2,
            Grids_2D_ID_long i3, BigDecimal gC, BigDecimal g2CS,
            BigDecimal g2CH, int dp, RoundingMode rm) {
        double aP;
        aP = Math.abs(((((g2.getCellYBigDecimal(i3).add(g2CH)).subtract(
                (bounds[1]))).multiply((bounds[2]).subtract(
                        (g2.getCellXBigDecimal(i3)).subtract(
                                g2CH)))).divide(g2CS, dp, rm)).doubleValue());
        return aP;
    }

    /**
     * Multiply g0 and g1 and return a new grid. It is assumed that the
     * dimensions are all the same;
     *
     * @param g0 The first grid to multiply.
     * @param g1 The second grid to mulitply
     * @return
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridDouble multiply(Grids_GridDouble g0, Grids_GridDouble g1)
            throws IOException, ClassNotFoundException, Exception {
        Grids_GridDouble r;
        long nRows = g0.getNRows();
        long nCols = g0.getNCols();
        r = GridDoubleFactory.create(g0, 0L, 0L, nRows - 1, nCols - 1);
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
            throws IOException, ClassNotFoundException, Exception {
        Grids_GridDouble r;
        long nRows = g0.getNRows();
        long nCols = g0.getNCols();
        r = GridDoubleFactory.create(g0, 0L, 0L, nRows - 1, nCols - 1);
        double noDataValue0 = g0.getNoDataValue();
        double noDataValue1 = g1.getNoDataValue();
        for (long row = 0L; row < nRows; row++) {
            for (long col = 0L; col < nCols; col++) {
                double v0 = g0.getCell(row, col);
                double v1 = g1.getCell(row, col);
                if (v0 != noDataValue0) {
                    if (v1 != noDataValue1) {
                        if (v1 != 0) {
                            r.setCell(row, col, v0 / v1);
                        }
                    }
                }
            }
        }
        return r;
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
     * @param colOffset @param gridFactory @return @throws java.lang.Exception
     * If encountered. @throws java.io.IOException If encountered. @throws
     * java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridDouble aggregate(Grids_GridNumber grid,
            int cellFactor, String statistic, int rowOffset, int colOffset,
            Grids_GridFactoryDouble gridFactory) throws IOException,
            ClassNotFoundException, Exception {
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
        BigDecimal ndv = getNoDataValueBigDecimal(grid);
        BigDecimal rC = cellsize.multiply(new BigDecimal(Integer.toString(cellFactor)));
        BigDecimal rXMin = xMin.add(cellsize.multiply(BigDecimal.valueOf(colOffset)));
        BigDecimal rYMin = yMin.add(cellsize.multiply(BigDecimal.valueOf(rowOffset)));

        //double resultCellsize = cellsize * ( double ) cellFactor;
        //double width = cellsize * ncols;
        //double height = cellsize * nrows;
        //double resultXllcorner = xllcorner + ( colOffset * cellsize );
        //double resultYllcorner = yllcorner + ( rowOffset * cellsize );
        // Calculate resultNrows and resultHeight
        long rNrows = 1L;
        BigDecimal rH = new BigDecimal(rC.toString());
        while (rYMin.add(rH).compareTo(yMax) == -1) {
            rNrows++;
            rH = rH.add(rC);
        }
        //while ( ( resultYllcorner + resultHeight ) < ( yllcorner + height ) ) {
        //    resultNrows ++;
        //    resultHeight += resultCellsize;
        //}
        // Calculate resultNcols and resultWidth
        long rNcols = 1L;
        BigDecimal rWidth = new BigDecimal(rC.toString());
        //double resultWidth = resultCellsize;
        while (rXMin.add(rWidth).compareTo(xMax) == -1) {
            rNrows++;
            rWidth = rWidth.add(rC);
        }
        //while ( ( resultXllcorner + resultWidth ) < ( xllcorner + width ) ) {
        //    resultNcols ++;
        //    resultWidth += resultCellsize;
        //}
        BigDecimal rXMax = rXMin.add(rWidth);
        BigDecimal rYMax = rYMin.add(rH);
        Grids_Dimensions rD = new Grids_Dimensions(rXMin,
                rXMax, rYMin, rYMax, rC);
        // Initialise result
        double ndvd = ndv.doubleValue();
        gridFactory.setNoDataValue(ndv.doubleValue());
        Grids_GridDouble r = gridFactory.create(rNrows, rNcols, rD);

        // sum
        if (statistic.equalsIgnoreCase("sum")) {
            Grids_GridDouble count = gridFactory.create(rNrows,
                    rNcols, rD);
            Grids_GridDouble normaliser = gridFactory.create(rNrows,
                    rNcols, rD);
            for (long row = 0; row < nrows; row++) {
                for (long col = 0; col < ncols; col++) {
                    BigDecimal x = grid.getCellXBigDecimal(col);
                    BigDecimal y = grid.getCellYBigDecimal(row);
                    if (r.isInGrid(x, y)) {
                        BigDecimal value = grid.getCellBigDecimal(row, col);
                        if (value.compareTo(ndv) != 0) {
                            count.addToCell(x, y, 1.0d);
                            r.addToCell(x, y, value.doubleValue());
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
            for (int row = 0; row < rNrows; row++) {
                for (int col = 0; col < rNcols; col++) {
                    count0 = count.getCell(row, col);
                    if (count0 != 0.0d) {
                        r.setCell(row, col, ((r.getCell(row, col)
                                * normaliser.getCell(row, col)) / count0));
                    }
                }
            }
        }

        // mean
        if (statistic.equalsIgnoreCase("mean")) {
            Grids_GridDouble numerator = gridFactory.create(rNrows,
                    rNcols, rD);
            Grids_GridDouble denominator = gridFactory.create(
                    rNrows, rNcols, rD);
            for (int row = 0; row < nrows; row++) {
                for (int col = 0; col < ncols; col++) {
                    BigDecimal x = grid.getCellXBigDecimal(col);
                    BigDecimal y = grid.getCellYBigDecimal(row);
                    if (r.isInGrid(x, y)) {
                        BigDecimal value = grid.getCellBigDecimal(row, col);
                        if (value.compareTo(ndv) != 0) {
                            numerator.addToCell(x, y, value.doubleValue());
                            denominator.addToCell(x, y, 1.0d);
                        }
                    }
                }
            }
            for (int row = 0; row < rNrows; row++) {
                for (int col = 0; col < rNcols; col++) {
                    BigDecimal value = numerator.getCellBigDecimal(row, col);
                    if (value.compareTo(ndv) != 0) {
                        r.setCell(row, col, value.doubleValue()
                                / denominator.getCell(row, col));
                    }
                }
            }
        }

        // min
        if (statistic.equalsIgnoreCase("min")) {
            for (int row = 0; row < nrows; row++) {
                for (int col = 0; col < ncols; col++) {
                    BigDecimal x = grid.getCellXBigDecimal(col);
                    BigDecimal y = grid.getCellYBigDecimal(row);
                    if (r.isInGrid(x, y)) {
                        BigDecimal value = grid.getCellBigDecimal(row, col);
                        if (value.compareTo(ndv) != 0) {
                            double min = r.getCell(x, y);
                            if (min != ndvd) {
                                r.setCell(x, y, Math.min(min, value.doubleValue()));
                            } else {
                                r.setCell(x, y, value.doubleValue());
                            }
                        }
                    }
                }
            }
        }

        // max
        if (statistic.equalsIgnoreCase("max")) {
            double max;
            for (int row = 0; row < nrows; row++) {
                for (int col = 0; col < ncols; col++) {
                    BigDecimal x = grid.getCellXBigDecimal(col);
                    BigDecimal y = grid.getCellYBigDecimal(row);
                    if (r.isInGrid(x, y)) {
                        BigDecimal value = grid.getCellBigDecimal(row, col);
                        if (value.compareTo(ndv) != 0) {
                            max = r.getCell(x, y);
                            if (max != ndvd) {
                                r.setCell(x, y, Math.max(max, value.doubleValue()));
                            } else {
                                r.setCell(x, y, value.doubleValue());
                            }
                        }
                    }
                }
            }
        }
        return r;
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
    public BigDecimal getNoDataValueBigDecimal(Grids_GridNumber g) throws Exception {
        BigDecimal r = BigDecimal.valueOf(-Double.MAX_VALUE);
        if (g.getClass() == Grids_GridInt.class) {
            r = BigDecimal.valueOf(((Grids_GridInt) g).getNoDataValue());
        } else {
            if (g.getClass() == Grids_GridDouble.class) {
                r = BigDecimal.valueOf(((Grids_GridDouble) g).getNoDataValue());
            } else {
                throw new Exception("Unrecognized Grids_Number type.");
            }
        }
        return r;
    }

    /**
     * Returns an Grids_GridDouble at a lower level of resolution than grid. The
     * result values are either the sum, mean, max or min of values in grid
     * depending on statistic.
     *
     * @param grid The Grids_GridDouble to be processed
     * @param statistic "sum", "mean", "max", or "min" depending on what
     * aggregate of values are wanted
     * @param rD
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
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridDouble aggregate(Grids_GridNumber grid,
            String statistic, Grids_Dimensions rD,
            Grids_GridFactoryDouble gf, int dp, RoundingMode rm)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        // Initialistaion
        long nrows = grid.getNRows();
        long ncols = grid.getNCols();
        Grids_Dimensions dimensions = grid.getDimensions();
        BigDecimal ndv = getNoDataValueBigDecimal(grid);
        double ndvd = ndv.doubleValue();
        BigDecimal rC = rD.getCellsize();
        BigDecimal rXMin = rD.getXMin();
        BigDecimal rYMin = rD.getYMin();
        BigDecimal rXMax = rD.getXMax();
        BigDecimal rYMax = rD.getYMax();
        BigDecimal rCH = rC.divide(BigDecimal.valueOf(2));
        BigDecimal rCS = rC.multiply(rC);

        BigDecimal c = dimensions.getCellsize();
        BigDecimal xMin = dimensions.getXMin();
        BigDecimal yMin = dimensions.getYMin();
        BigDecimal xMax = dimensions.getXMax();
        BigDecimal yMax = dimensions.getYMax();
        BigDecimal cS = c.multiply(c);
        BigDecimal cH = c.divide(BigDecimal.valueOf(2));
        //double width = cellsize * ncols;
        //double height = cellsize * nrows;
        // Test this is an aggregation
        if (rC.compareTo(c) != 1) {
            System.err.println(
                    "!!!Warning: Not an aggregation as "
                    + "resultCellsize < cellsize. Returning null!");
            return null;
        }
        // Test for intersection
        if ((rXMin.compareTo(xMin.add(c.multiply(BigDecimal.valueOf(ncols)))) == 1)
                || (rYMin.compareTo(yMin.add(c.multiply(BigDecimal.valueOf(nrows)))) == 1)) {
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
            BigDecimal t0 = rC.divide(c, Math.max(rC.scale(), c.scale()) + 2, rm);
            BigDecimal t1 = rXMin.divide(c, Math.max(rXMin.scale(), c.scale()) + 2, rm);
            BigDecimal t2 = xMin.divide(c, Math.max(xMin.scale(), c.scale()) + 2, rm);
            BigDecimal t3 = rYMin.divide(c, Math.max(rYMin.scale(), c.scale()) + 2, rm);
            BigDecimal t4 = yMin.divide(c, Math.max(yMin.scale(), c.scale()) + 2, rm);
            if ((t0.compareTo(new BigDecimal(t0.toBigInteger().toString())) == 0)
                    && (t1.compareTo(new BigDecimal(t1.toBigInteger().toString())) == t2.compareTo(new BigDecimal(t2.toBigInteger().toString())))
                    && (t3.compareTo(new BigDecimal(t3.toBigInteger().toString())) == t4.compareTo(new BigDecimal(t4.toBigInteger().toString())))) {
                int cellFactor = rC.divide(c, 2, RoundingMode.UNNECESSARY).intValue();
                int rowOffset = yMin.subtract(rYMin.divide(c, dp, rm)).intValue();
                int colOffset = xMin.subtract(rXMin.divide(c, dp, rm)).intValue();
                return aggregate(grid, cellFactor, statistic, rowOffset, colOffset, gf);
            }
        }
        // Calculate resultNrows and resultHeight
        long rNrows = 1L;
        BigDecimal rH = new BigDecimal(rC.toString());
        //double resultHeight = resultCellsize;
        while (rYMin.add(rH).compareTo(yMax) == -1) {
            rNrows++;
            rH = rH.add(rC);
        }
        //while ( ( resultYllcorner + resultHeight ) < ( yllcorner + height ) ) {
        //    resultNrows ++;
        //    resultHeight += resultCellsize;
        //}
        // Calculate resultNcols and resultWidth
        long rNcols = 1L;
        BigDecimal rW = new BigDecimal(rC.toString());
        //double resultWidth = resultCellsize;
        while (rXMin.add(rW).compareTo(xMax) == -1) {
            rNrows++;
            rW = rW.add(rC);
        }
        //while ( ( resultXllcorner + resultWidth ) < ( xllcorner + width ) ) {
        //    resultNcols ++;
        //    resultWidth += resultCellsize;
        //}
        rXMax = xMin.add(rW);
        rYMax = yMin.add(rH);
        // Initialise result
        gf.setNoDataValue(ndvd);
        Grids_GridDouble r = gf.create(rNrows, rNcols, rD);
        // sum
        double aP;
        if (statistic.equalsIgnoreCase("sum")) {
            Grids_GridDouble totalValueArea;
            totalValueArea = gf.create(rNrows, rNcols, rD);
            BigDecimal[] bounds;
            Grids_2D_ID_long[] cellIDs = new Grids_2D_ID_long[4];
            double count0;
            for (int row = 0; row < nrows; row++) {
                for (int col = 0; col < ncols; col++) {
                    bounds = grid.getCellBounds(cH, row, col);
                    cellIDs[0] = r.getCellID(bounds[0], bounds[3]);
                    cellIDs[1] = r.getCellID(bounds[2], bounds[3]);
                    cellIDs[2] = r.getCellID(bounds[0], bounds[1]);
                    cellIDs[3] = r.getCellID(bounds[2], bounds[1]);
                    BigDecimal value = grid.getCellBigDecimal(row, col);
                    if (value != ndv) {
                        if (cellIDs[0].equals(cellIDs[1]) && cellIDs[1].equals(cellIDs[2])) {
                            r.addToCell(cellIDs[0], value.doubleValue());
                            totalValueArea.addToCell(cellIDs[0], 1.0d);
                        } else {
                            aP = getAP(bounds, r, cellIDs[0], cellIDs[1], cellIDs[2], c, rCS,
                                    rCH, dp, rm);
                            r.addToCell(cellIDs[0], value.doubleValue() * aP);
                            totalValueArea.addToCell(cellIDs[0], aP);
                        }
                        if (!cellIDs[1].equals(cellIDs[0])) {
                            aP = getAP13(bounds, r, cellIDs[1], cellIDs[3], c, rCS,
                                    rCH, dp, rm);
                            r.addToCell(cellIDs[1], value.doubleValue() * aP);
                            totalValueArea.addToCell(cellIDs[0], aP);
                        }
                        if (!cellIDs[2].equals(cellIDs[0])) {
                            aP = getAP23(bounds, r, cellIDs[2], cellIDs[3], c, rCS,
                                    rCH, dp, rm);
                            r.addToCell(cellIDs[2], value.doubleValue() * aP);
                        }
                        if (!cellIDs[3].equals(cellIDs[1]) && !cellIDs[3].equals(cellIDs[2])) {
                            aP = getAP3(bounds, r, cellIDs[3], c, rCS, rCH, dp, rm);
                            r.addToCell(cellIDs[3], value.doubleValue() * aP);
                            totalValueArea.addToCell(cellIDs[0], aP);
                        }
                    }
                }
            }
            // Normalise
            double totalValueArea0;
            double dCDiff2 = rC.subtract(c).pow(2).doubleValue();
            for (int row = 0; row < rNrows; row++) {
                for (int col = 0; col < rNcols; col++) {
                    totalValueArea0 = totalValueArea.getCell(row, col);
                    if (totalValueArea0 != 0.0d) {
                        r.setCell(row, col, ((r.getCell(row, col) * dCDiff2)
                                / totalValueArea.getCell(row, col)));
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
            double denominator = (rC.doubleValue() * rC.doubleValue()) / (c.doubleValue() * c.doubleValue());
            Grids_GridDouble sum = aggregate(grid, "sum", rD, gf, dp, rm);
            addToGrid(r, sum, 1.0d / denominator, dp, rm);
        }

        // max
        if (statistic.equalsIgnoreCase("max")) {
            double max;
            BigDecimal[] bounds;
            double halfCellsize = cH.doubleValue();
            for (long row = 0; row < nrows; row++) {
                for (long col = 0; col < ncols; col++) {
                    BigDecimal value = grid.getCellBigDecimal(row, col);
                    if (value.compareTo(ndv) != 0) {
                        BigDecimal x = grid.getCellXBigDecimal(col);
                        BigDecimal y = grid.getCellYBigDecimal(row);
                        bounds = grid.getCellBounds(cH, row, col);
                        max = r.getCell(bounds[0], bounds[3]);
                        if (max != ndvd) {
                            r.setCell(bounds[0], bounds[3], Math.max(max, value.doubleValue()));
                        } else {
                            r.setCell(bounds[0], bounds[3], value.doubleValue());
                        }
                        max = r.getCell(bounds[2], bounds[3]);
                        if (max != ndvd) {
                            r.setCell(bounds[2], bounds[3], Math.max(max, value.doubleValue()));
                        } else {
                            r.setCell(bounds[2], bounds[3], value.doubleValue());
                        }
                        max = r.getCell(bounds[0], bounds[1]);
                        if (max != ndvd) {
                            r.setCell(bounds[0], bounds[1], Math.max(max, value.doubleValue()));
                        } else {
                            r.setCell(bounds[0], bounds[1], value.doubleValue());
                        }
                        max = r.getCell(bounds[2], bounds[1]);
                        if (max != ndvd) {
                            r.setCell(bounds[2], bounds[1], Math.max(max, value.doubleValue()));
                        } else {
                            r.setCell(bounds[2], bounds[1], value.doubleValue());
                        }
                    }
                }
            }
        }

        // min
        if (statistic.equalsIgnoreCase("min")) {
            double min;
            BigDecimal[] bounds;
            double halfCellsize = cH.doubleValue();
            for (long row = 0; row < nrows; row++) {
                for (long col = 0; col < ncols; col++) {
                    BigDecimal value = grid.getCellBigDecimal(row, col);
                    if (value.compareTo(ndv) != 0) {
                        double vD = value.doubleValue();
                        BigDecimal x = grid.getCellXBigDecimal(col);
                        BigDecimal y = grid.getCellYBigDecimal(row);
                        bounds = grid.getCellBounds(cH, row, col);
                        min = r.getCell(bounds[0], bounds[3]);
                        if (min != ndvd) {
                            r.setCell(bounds[0], bounds[3], Math.min(min, vD));
                        } else {
                            r.setCell(bounds[0], bounds[3], vD);
                        }
                        min = r.getCell(bounds[2], bounds[3]);
                        if (min != ndvd) {
                            r.setCell(bounds[2], bounds[3], Math.min(min, vD));
                        } else {
                            r.setCell(bounds[2], bounds[3], vD);
                        }
                        min = r.getCell(bounds[0], bounds[1]);
                        if (min != ndvd) {
                            r.setCell(bounds[0], bounds[1], Math.min(min, vD));
                        } else {
                            r.setCell(bounds[0], bounds[1], vD);
                        }
                        min = r.getCell(bounds[2], bounds[1]);
                        if (min != ndvd) {
                            r.setCell(bounds[2], bounds[1], Math.min(min, value.doubleValue()));
                        } else {
                            r.setCell(bounds[2], bounds[1], vD);
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
        return r;
    }

    /**
     * Returns a double[][] of grid values
     *
     * @param g
     * @param row
     * @param cellDistance
     * @return
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected double[][] getRowProcessInitialData(Grids_GridDouble g,
            int cellDistance, long row) throws IOException, Exception, ClassNotFoundException {
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
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected double[][] getRowProcessData(Grids_GridDouble g,
            double[][] previous, int cellDistance, long row, long col)
            throws IOException, Exception, ClassNotFoundException {
        double[][] r = previous;
        if (col == 0) {
            return getRowProcessInitialData(g, cellDistance, row);
        } else {
            // shift columns one left
            for (int i = 0; i <= cellDistance * 2; i++) {
                for (int j = 0; j <= (cellDistance * 2) - 1; j++) {
                    r[i][j] = previous[i][j + 1];
                }
            }
            // getLastColumn
            for (int i = -cellDistance; i <= cellDistance; i++) {
                r[i + cellDistance][cellDistance * 2]
                        = g.getCell((long) i + row, (long) col + cellDistance);
            }
        }
        return r;
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
            throws IOException, ClassNotFoundException, Exception {
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
            throws IOException, ClassNotFoundException, Exception {
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
                env.clearMemoryReserve(env.env);
                if (env.cacheChunksExcept_Account(g, hoome) < 1) {
                    throw e;
                }
                env.initMemoryReserve(env.env);
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
            ClassNotFoundException, Exception {
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
                env.clearMemoryReserve(env.env);
                if (!env.cacheChunk(env.HOOMEF)) {
                    throw e;
                }
                env.initMemoryReserve(env.env);
                outputESRIAsciiGrid(g, outDir, eage, hoome);
            } else {
                throw e;
            }
        }
    }

}
