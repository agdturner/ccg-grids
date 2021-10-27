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
package uk.ac.leeds.ccg.grids.process;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_long;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridNumber;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDouble;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDoubleFactoryArray;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridInt;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkInt;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntFactoryArray;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridIntFactory;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.core.Grids_Object;
import uk.ac.leeds.ccg.grids.d2.grid.b.Grids_GridBooleanFactory;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDoubleFactorySinglet;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntFactorySinglet;
import uk.ac.leeds.ccg.grids.d2.grid.b.Grids_GridBinaryFactory;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsNumber;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.grids.io.Grids_Files;
import uk.ac.leeds.ccg.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.grids.d2.chunk.b.Grids_ChunkBinaryFactoryArray;
import uk.ac.leeds.ccg.grids.d2.chunk.b.Grids_ChunkBooleanFactoryArray;
import uk.ac.leeds.ccg.grids.d2.chunk.bd.Grids_ChunkBDFactoryArray;
import uk.ac.leeds.ccg.grids.d2.chunk.bd.Grids_ChunkBDFactorySinglet;
import uk.ac.leeds.ccg.grids.d2.grid.bd.Grids_GridBDFactory;
import uk.ac.leeds.ccg.io.IO_Cache;
import uk.ac.leeds.ccg.io.IO_Path;
import uk.ac.leeds.ccg.math.number.Math_BigRational;

/**
 * General methods for processing individual or multiple grids.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_Processor extends Grids_Object {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the start time of the processing.
     */
    public final long startTime;

    /**
     * For convenience.
     */
    protected Grids_Files files;

    /**
     * Grids_GridBooleanFactory
     */
    public Grids_GridBooleanFactory gridFactoryBoolean;

    /**
     * Grids_GridBooleanFactory
     */
    public Grids_GridBinaryFactory gridFactoryBinary;

    /**
     * Grids_GridIntFactory
     */
    public Grids_GridIntFactory gridFactoryInt;

    /**
     * Grids_GridDoubleFactory
     */
    public Grids_GridDoubleFactory gridFactoryDouble;

    /**
     * Grids_GridBDFactory
     */
    public Grids_GridBDFactory gridFactoryBD;

    /**
     * @param e The grids environment.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_Processor(Grids_Environment e) throws Exception, IOException,
            ClassNotFoundException {
        super(e);
        startTime = System.currentTimeMillis();
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
        Path dir = Paths.get(files.getGeneratedGridBooleanDir().toString());
        IO_Cache fs = IO_Cache.getFileStore(dir);
        gridFactoryBoolean = new Grids_GridBooleanFactory(env, fs,
                new Grids_ChunkBooleanFactoryArray(), chunkNRows, chunkNCols);
        // Binary
        dir = Paths.get(files.getGeneratedGridBinaryDir().toString());
        fs = IO_Cache.getFileStore(dir);
        gridFactoryBinary = new Grids_GridBinaryFactory(env, fs,
                new Grids_ChunkBinaryFactoryArray(), chunkNRows, chunkNCols);
        // Int
        dir = Paths.get(files.getGeneratedGridIntDir().toString());
        fs = IO_Cache.getFileStore(dir);
        gridFactoryInt = new Grids_GridIntFactory(env, fs,
                new Grids_ChunkIntFactorySinglet(Integer.MIN_VALUE),
                new Grids_ChunkIntFactoryArray(),
                chunkNRows, chunkNCols);
        // Double
        dir = Paths.get(files.getGeneratedGridDoubleDir().toString());
        fs = IO_Cache.getFileStore(dir);
        gridFactoryDouble = new Grids_GridDoubleFactory(env, fs,
                new Grids_ChunkDoubleFactorySinglet(-Double.MAX_VALUE),
                new Grids_ChunkDoubleFactoryArray(),
                chunkNRows, chunkNCols);
        // BigDecimal
        dir = Paths.get(files.getGeneratedGridBigDecimalDir().toString());
        fs = IO_Cache.getFileStore(dir);
        gridFactoryBD = new Grids_GridBDFactory(env, fs,
                new Grids_ChunkBDFactorySinglet(BigDecimal.valueOf(-Double.MAX_VALUE)),
                new Grids_ChunkBDFactoryArray(),
                chunkNRows, chunkNCols);
    }

    /**
     * @param dir dir
     * @param s s
     * @return The cache.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected IO_Cache getStore(Path dir, String s) throws IOException,
            Exception {
        IO_Cache r;
        if (Files.exists(dir)) {
            r = new IO_Cache(dir);
        } else {
            r = new IO_Cache(files.getGeneratedDir(), s);
        }
        return r;
    }

    /**
     * @return {@link #startTime}
     */
    public long getTime0() {
        return startTime;
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
        if (g instanceof Grids_GridInt) {
            Grids_GridInt grid = (Grids_GridInt) g;
            int noDataValue = grid.getNoDataValue();
            if (mask instanceof Grids_GridInt) {
                Grids_GridInt m = (Grids_GridInt) mask;
                int maskNoDataValue = m.getNoDataValue();
                Iterator<Grids_2D_ID_int> ite = m.iterator().getGridIterator();
                while (ite.hasNext()) {
                    Grids_ChunkInt maskIntChunk = m.getChunk(ite.next());
                    Grids_2D_ID_int i = maskIntChunk.getId();
                    env.addToNotToClear(g, i);
                    env.addToNotToClear(mask, i);
                    env.checkAndMaybeFreeMemory();
                    int cnr = m.getChunkNRows(i);
                    int cnc = m.getChunkNCols(i);
                    int cr = i.getRow();
                    int cc = i.getCol();
                    for (int ccr = 0; ccr < cnr; ccr++) {
                        for (int ccc = 0; ccc < cnc; ccc++) {
                            int v = maskIntChunk.getCell(ccr, ccc);
                            if (v == maskNoDataValue) {
                                long r = ((long) cr * (long) cnr) + (long) ccr;
                                long c = ((long) cc * (long) cnc) + (long) ccc;
                                grid.setCell(r, c, noDataValue);
                            }
                        }
                    }
                    env.removeFromNotToClear(g, i);
                    env.removeFromNotToClear(mask, i);
                }
            } else {
                // ( mask.getClass() == Grids_GridDouble.class )
                Grids_GridDouble m = (Grids_GridDouble) mask;
                double maskNoDataValue = m.getNoDataValue();
                Iterator<Grids_2D_ID_int> ite = m.iterator().getGridIterator();
                while (ite.hasNext()) {
                    Grids_ChunkDouble maskChunk = m.getChunk(ite.next());
                    Grids_2D_ID_int i = maskChunk.getId();
                    env.addToNotToClear(g, i);
                    env.addToNotToClear(mask, i);
                    env.checkAndMaybeFreeMemory();
                    int cnr = m.getChunkNRows(i);
                    int cnc = m.getChunkNCols(i);
                    int cr = i.getRow();
                    int cc = i.getCol();
                    for (int ccr = 0; ccr < cnr; ccr++) {
                        for (int ccc = 0; ccc < cnc; ccc++) {
                            double v = maskChunk.getCell(ccr, ccc);
                            if (v == maskNoDataValue) {
                                long r = ((long) cr * (long) cnr) + (long) ccr;
                                long c = ((long) cc * (long) cnc) + (long) ccc;
                                grid.setCell(r, c, noDataValue);
                            }
                        }
                    }
                    env.removeFromNotToClear(g, i);
                    env.removeFromNotToClear(mask, i);
                }
            }
        } else {
            Grids_GridDouble grid = (Grids_GridDouble) g;
            double resultNoDataValue = grid.getNoDataValue();
            if (mask.getClass() == Grids_GridInt.class) {
                Grids_GridInt maskInt = (Grids_GridInt) mask;
                int maskNoDataValue = maskInt.getNoDataValue();
                Iterator<Grids_2D_ID_int> ite = maskInt.iterator().getGridIterator();
                while (ite.hasNext()) {
                    Grids_ChunkInt maskChunk = maskInt.getChunk(ite.next());
                    Grids_2D_ID_int i = maskChunk.getId();
                    env.addToNotToClear(g, i);
                    env.addToNotToClear(mask, i);
                    env.checkAndMaybeFreeMemory();
                    int cnr = maskInt.getChunkNRows(i);
                    int cnc = maskInt.getChunkNCols(i);
                    int cr = i.getRow();
                    int cc = i.getCol();
                    for (int ccr = 0; ccr < cnr; ccr++) {
                        for (int ccc = 0; ccc < cnc; ccc++) {
                            int v = maskChunk.getCell(ccr, ccc);
                            if (v == maskNoDataValue) {
                                long r = ((long) cr * (long) cnr) + (long) ccr;
                                long c = ((long) cc * (long) cnc) + (long) ccc;
                                grid.setCell(r, c, resultNoDataValue);
                            }
                        }
                    }
                    env.removeFromNotToClear(g, i);
                    env.removeFromNotToClear(mask, i);
                }
            } else {
                // ( mask.getClass() == Grids_GridDouble.class )
                Grids_GridDouble maskDouble = (Grids_GridDouble) mask;
                double maskNoDataValue = maskDouble.getNoDataValue();
                Iterator<Grids_2D_ID_int> ite = maskDouble.getChunkIDs().iterator();
                Grids_ChunkDouble maskChunk;
                while (ite.hasNext()) {
                    maskChunk = (Grids_ChunkDouble) mask.getChunk(ite.next());
                    Grids_2D_ID_int i = maskChunk.getId();
                    env.addToNotToClear(g, i);
                    env.addToNotToClear(mask, i);
                    env.checkAndMaybeFreeMemory();
                    int cnr = maskDouble.getChunkNRows(i);
                    int cnc = maskDouble.getChunkNCols(i);
                    int cr = i.getRow();
                    int cc = i.getCol();
                    for (int ccr = 0; ccr < cnr; ccr++) {
                        for (int ccc = 0; ccc < cnc; ccc++) {
                            double value = maskChunk.getCell(ccr, ccc);
                            if (value == maskNoDataValue) {
                                long r = ((long) cr * (long) cnr) + (long) ccr;
                                long c = ((long) cc * (long) cnc) + (long) ccc;
                                grid.setCell(r, c, resultNoDataValue);
                            }
                        }
                    }
                    env.removeFromNotToClear(g, i);
                    env.removeFromNotToClear(mask, i);
                }
            }
        }
        env.checkAndMaybeFreeMemory();
    }

    /**
     * Modifies grid {@code g} by setting all cells with values in the range
     * [min,max] to it's noDataValue.
     *
     * @param g The grid to be masked.
     * @param min The minimum value in the range.
     * @param max The maximum value in the range.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void mask(Grids_GridNumber g, BigDecimal min, BigDecimal max)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        if (g.getClass() == Grids_GridInt.class) {
            Grids_GridInt gi = (Grids_GridInt) g;
            int ndv = gi.getNoDataValue();
            Iterator<Grids_2D_ID_int> ite = gi.iterator().getGridIterator();
            while (ite.hasNext()) {
                Grids_ChunkInt chunk = gi.getChunk(ite.next());
                Grids_2D_ID_int i = chunk.getId();
                int cr = i.getRow();
                int cc = i.getCol();
                env.addToNotToClear(g, i);
                env.checkAndMaybeFreeMemory();
                int cnr = gi.getChunkNRows(i);
                int cnc = gi.getChunkNCols(i);
                for (int ccr = 0; ccr < cnr; ccr++) {
                    for (int ccc = 0; ccc < cnc; ccc++) {
                        BigDecimal v = gi.getCellBigDecimal(chunk, cr, cc, ccr,
                                ccc);
                        if (v.compareTo(min) != -1 && v.compareTo(max) != 1) {
                            gi.setCell(chunk, ccr, ccc, ndv);
                        }
                    }
                }
                env.removeFromNotToClear(g, i);
            }
        } else {
            // ( grid.getClass() == Grids_GridDouble.class )
            Grids_GridDouble gd = (Grids_GridDouble) g;
            double ndv = gd.getNoDataValue();
            Iterator<Grids_2D_ID_int> ite = gd.iterator().getGridIterator();
            while (ite.hasNext()) {
                Grids_ChunkDouble chunk = gd.getChunk(ite.next());
                Grids_2D_ID_int i = chunk.getId();
                int cr = i.getRow();
                int cc = i.getCol();
                env.addToNotToClear(g, i);
                env.checkAndMaybeFreeMemory();
                int cnr = g.getChunkNRows(i);
                int cnc = g.getChunkNCols(i);
                for (int ccr = 0; ccr < cnr; ccr++) {
                    for (int ccc = 0; ccc < cnc; ccc++) {
                        BigDecimal v = gd.getCellBigDecimal(chunk, cr, cc, ccr,
                                ccc);
                        if (v.compareTo(min) != -1 && v.compareTo(max) != 1) {
                            gd.setCell(chunk, ccr, ccc, ndv);
                        }
                    }
                }
                env.removeFromNotToClear(g, i);
            }
        }
        //grid.setName( grid.getName() + "_mask" );
        env.checkAndMaybeFreeMemory();
    }

    /**
     * @param g The grid.
     * @return a new Grids_GridDouble Values are either linearly rescaled into
     * the range [min,max]. Or some Log rescaling is done
     * @param type If type == null then a linear rescale is done. If type ==
     * "Log" then a Log rescale is done.
     * @param min The minimum value in the rescaled range.
     * @param max The maximum value in the rescaled range.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridDouble rescale(Grids_GridNumber g, String type, double min,
            double max) throws IOException, ClassNotFoundException, Exception {
        if (g instanceof Grids_GridDouble) {
            return rescale((Grids_GridDouble) g, null, 0.0d, 255.0d);
        } else {
            return rescale((Grids_GridInt) g, null, 0.0d, 255.0d);
        }
    }

    /**
     * For rescaling the {@code double} type grid {@code g}. The type of
     * rescaling is determined by {@code type}. If {@code type == null} the a
     * linear rescaling is done. If {@code type = "log"} a log rescaling is
     * done. For any other value of type this will throw an exception.
     *
     * There are other rescaling implementation that might be useful that are
     * not currently implemented.
     *
     * @param g The grid for rescaling.
     * @param type If {@code null} then a linear rescale is done. If
     * {@code "log"}, then a log rescale is done.
     *
     * @param min The minimum value in the rescaled range.
     * @param max The maximum value in the rescaled range.
     * @return A grid which are the rescaled values of {@code g}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridDouble rescale(Grids_GridDouble g, String type, double min,
            double max) throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        int ncc = g.getNChunkCols();
        int ncr = g.getNChunkCols();
        double ndv = g.getNoDataValue();
        double range = max - min;
        Grids_StatsNumber stats = g.getStats();
        double minGrid = stats.getMin(true).doubleValue();
        double maxGrid = stats.getMax(true).doubleValue();
        double rangeGrid = maxGrid - minGrid;
        Grids_GridDouble r = gridFactoryDouble.create(g, 0, 0, g.getNRows() - 1,
                g.getNCols() - 1);
        env.env.log("Initialised rescaled grid.");
        r.setName(g.getName());
        System.out.println(r.toString());
        /**
         * If range of either input or output range is zero return min for all
         * non noDataValues.
         */
        if (rangeGrid == 0.0d || range == 0.0d) {
            /**
             * Better to go through chunks rather than rows. Though it does
             * assume that the chunk structure of the grid and outputGrid are
             * the same.
             */
            for (int cr = 0; cr < ncr; cr++) {
                env.env.log("chunk row " + cr);
                for (int cc = 0; cc < ncc; cc++) {
                    Grids_2D_ID_int i = new Grids_2D_ID_int(cr, cc);
                    env.addToNotToClear(g, i);
                    env.addToNotToClear(r, i);
                    env.checkAndMaybeFreeMemory();
                    int cnc = g.getChunkNCols(cc);
                    int cnr = g.getChunkNRows(cr);
                    Grids_ChunkDouble gc = g.getChunk(i);
                    Grids_ChunkDouble ogc = r.getChunk(i);
                    for (int ccr = 0; ccr < cnr; ccr++) {
                        for (int ccc = 0; ccc < cnc; ccc++) {
                            double v = gc.getCell(ccr, ccc);
                            if (v != ndv) {
                                r.setCell(ogc, ccr, ccc, min);
                            }
                        }
                    }
                    env.removeFromNotToClear(g, i);
                    env.removeFromNotToClear(r, i);
                    env.checkAndMaybeFreeMemory();
                }
            }
        } else {
            if (type == null) {
                /**
                 * Better to go through chunks rather than rows. Though it does
                 * assume that the structure of the grid and outputGrid are the
                 * same.
                 */
                for (int cr = 0; cr < ncr; cr++) {
                    env.env.log("chunk row " + cr);
                    for (int cc = 0; cc < ncc; cc++) {
                        Grids_2D_ID_int i = new Grids_2D_ID_int(cr, cc);
                        env.addToNotToClear(g, i);
                        env.addToNotToClear(r, i);
                        env.checkAndMaybeFreeMemory();
                        int cnc = g.getChunkNCols(cc);
                        int cnr = g.getChunkNRows(cr);
                        Grids_ChunkDouble gc = g.getChunk(i);
                        Grids_ChunkDouble ogc = r.getChunk(i);
                        for (int ccr = 0; ccr < cnr; ccr++) {
                            for (int ccc = 0; ccc < cnc; ccc++) {
                                double v = gc.getCell(ccr, ccc);
                                if (v != ndv) {
                                    v = (((v - minGrid) / rangeGrid)
                                            * range) + min;
                                    r.setCell(ogc, ccr, ccc, v);
                                }
                            }
                        }
                        env.removeFromNotToClear(g, i);
                        env.removeFromNotToClear(r, i);
                        env.checkAndMaybeFreeMemory();
                    }
                }
                r.setName(g.getName() + "_linearRescale");
                env.checkAndMaybeFreeMemory();
            } else if (type.equalsIgnoreCase("log")) {
                r = rescale(r, null, 1.0d, 1000000.0d);
                for (int cr = 0; cr < ncr; cr++) {
                    env.env.log("chunk row " + cr);
                    for (int cc = 0; cc < ncc; cc++) {
                        Grids_2D_ID_int i = new Grids_2D_ID_int(cr, cc);
                        env.addToNotToClear(g, i);
                        env.addToNotToClear(r, i);
                        env.checkAndMaybeFreeMemory();
                        int cnc = g.getChunkNCols(cc);
                        int cnr = g.getChunkNRows(cr);
                        Grids_ChunkDouble gc = g.getChunk(i);
                        Grids_ChunkDouble ogc = r.getChunk(i);
                        for (int ccr = 0; ccr < cnr; ccr++) {
                            for (int ccc = 0; ccc < cnc; ccc++) {
                                double v = gc.getCell(ccr, ccc);
                                if (v != ndv) {
                                    r.setCell(ogc, ccr, ccc, Math.log(v));
                                }
                            }
                        }
                    }
                }
                r = rescale(r, null, min, max);
                //grid.setName( grid.getName() + "_logRescale" );
                env.checkAndMaybeFreeMemory();
            } else {
                throw new Exception("Type " + type + "not recognised.");
            }
        }
        return r;
    }

    /**
     * For rescaling the {@code int} type grid {@code g}. The type of rescaling
     * is determined by {@code type}. If {@code type == null} the a linear
     * rescaling is done. If {@code type = "log"} a log rescaling is done. For
     * any other value of type this will throw an exception.
     *
     * There are other rescaling implementation that might be useful that are
     * not currently implemented.
     *
     * @param g The grid for rescaling.
     * @param type If {@code null} then a linear rescale is done. If
     * {@code "log"}, then a log rescale is done.
     *
     * @param min The minimum value in the rescaled range.
     * @param max The maximum value in the rescaled range.
     * @return A grid which are the rescaled values of {@code g}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridDouble rescale(Grids_GridInt g, String type, double min,
            double max) throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        int ncc = g.getNChunkCols();
        int ncr = g.getNChunkCols();
        int ndv = g.getNoDataValue();
        double range = max - min;
        Grids_StatsNumber stats = g.getStats();
        double minGrid = stats.getMin(true).doubleValue();
        double maxGrid = stats.getMax(true).doubleValue();
        double rangeGrid = maxGrid - minGrid;
        Grids_GridDouble r = gridFactoryDouble.create(g, 0, 0, g.getNRows() - 1,
                g.getNCols() - 1);
        r.setName(g.getName());
        System.out.println(r.toString());
        /**
         * If range of either input or output range is zero return min for all
         * non noDataValues.
         */
        if (rangeGrid == 0.0d || range == 0.0d) {
            /**
             * Better to go through chunks rather than rows. Though it does
             * assume that the chunk structure of the grid and outputGrid are
             * the same.
             */
            for (int cr = 0; cr < ncr; cr++) {
                for (int cc = 0; cc < ncc; cc++) {
                    Grids_2D_ID_int i = new Grids_2D_ID_int(cr, cc);
                    env.addToNotToClear(g, i);
                    env.addToNotToClear(r, i);
                    env.checkAndMaybeFreeMemory();
                    int cnc = g.getChunkNCols(cc);
                    int cnr = g.getChunkNRows(cr);
                    Grids_ChunkInt gc = g.getChunk(i);
                    Grids_ChunkDouble ogc = r.getChunk(i);
                    for (int ccr = 0; ccr < cnr; ccr++) {
                        for (int ccc = 0; ccc < cnc; ccc++) {
                            double v = gc.getCell(ccr, ccc);
                            if (v != ndv) {
                                r.setCell(ogc, ccr, ccc, min);
                            }
                        }
                    }
                    env.removeFromNotToClear(g, i);
                    env.removeFromNotToClear(r, i);
                    env.checkAndMaybeFreeMemory();
                }
            }
        } else {
            if (type == null) {
                /**
                 * Better to go through chunks rather than rows. Though it does
                 * assume that the structure of the grid and outputGrid are the
                 * same.
                 */
                for (int cr = 0; cr < ncr; cr++) {
                    for (int cc = 0; cc < ncc; cc++) {
                        Grids_2D_ID_int i = new Grids_2D_ID_int(cr, cc);
                        env.addToNotToClear(g, i);
                        env.addToNotToClear(r, i);
                        env.checkAndMaybeFreeMemory();
                        int cnc = g.getChunkNCols(cc);
                        int cnr = g.getChunkNRows(cr);
                        Grids_ChunkInt gc = g.getChunk(i);
                        Grids_ChunkDouble ogc = r.getChunk(i);
                        for (int ccr = 0; ccr < cnr; ccr++) {
                            for (int ccc = 0; ccc < cnc; ccc++) {
                                double v = gc.getCell(ccr, ccc);
                                if (v != ndv) {
                                    v = (((v - minGrid) / rangeGrid)
                                            * range) + min;
                                    r.setCell(ogc, ccr, ccc, v);
                                }
                            }
                        }
                        env.removeFromNotToClear(g, i);
                        env.removeFromNotToClear(r, i);
                        env.checkAndMaybeFreeMemory();
                    }
                }
                r.setName(g.getName() + "_linearRescale");
                env.checkAndMaybeFreeMemory();
            } else if (type.equalsIgnoreCase("log")) {
                r = rescale(r, null, 1.0d, 1000000.0d);
                for (int cr = 0; cr < ncr; cr++) {
                    for (int cc = 0; cc < ncc; cc++) {
                        Grids_2D_ID_int i = new Grids_2D_ID_int(cr, cc);
                        env.addToNotToClear(g, i);
                        env.addToNotToClear(r, i);
                        env.checkAndMaybeFreeMemory();
                        int cnc = g.getChunkNCols(cc);
                        int cnr = g.getChunkNRows(cr);
                        Grids_ChunkInt gc = g.getChunk(i);
                        Grids_ChunkDouble ogc = r.getChunk(i);
                        for (int ccr = 0; ccr < cnr; ccr++) {
                            for (int ccc = 0; ccc < cnc; ccc++) {
                                double v = gc.getCell(ccr, ccc);
                                if (v != ndv) {
                                    r.setCell(ogc, ccr, ccc, Math.log(v));
                                }
                            }
                        }
                    }
                }
                r = rescale(r, null, min, max);
                //grid.setName( grid.getName() + "_logRescale" );
                env.checkAndMaybeFreeMemory();
            } else {
                throw new Exception("Type " + type + "not recognised.");
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
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
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
    public void addToGrid(Grids_GridNumber g, HashSet<Grids_2D_ID_long> cellIDs,
            BigDecimal v) throws IOException, ClassNotFoundException,
            Exception {
        env.checkAndMaybeFreeMemory();
        Iterator<Grids_2D_ID_long> ite = cellIDs.iterator();
        while (ite.hasNext()) {
            g.addToCell(ite.next(), v);
            env.checkAndMaybeFreeMemory();
        }
    }

    /**
     * Adds value {@code v} to every cell of grid {@code g}.
     *
     * @param g The grid which is to have value {@code v} added.
     * @param v The value to be added
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToGrid(Grids_GridNumber g, BigDecimal v) throws IOException,
            ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        int ncr = g.getNChunkRows();
        int ncc = g.getNChunkCols();
        for (int cr = 0; cr < ncr; cr++) {
            for (int cc = 0; cc < ncc; cc++) {
                env.checkAndMaybeFreeMemory();
                int cnr = g.getChunkNRows(cr);
                int cnc = g.getChunkNCols(cc);
                for (int ccr = 0; ccr < cnr; ccr++) {
                    for (int ccc = 0; ccc < cnc; ccc++) {
                        g.addToCell(cr, cc, ccr, ccc, v);
                    }
                }
            }
        }
        env.checkAndMaybeFreeMemory();
    }

    /**
     * Adds value to grid for cells with CellID in _CellIDs
     *
     * @param g The Grids_GridDouble to be processed
     * @param cellIDs Array of CellIDs.
     * @param value The value to be added.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToGrid(Grids_GridNumber g, Grids_2D_ID_long[] cellIDs,
            BigDecimal value) throws IOException, ClassNotFoundException,
            Exception {
        env.checkAndMaybeFreeMemory();
        for (Grids_2D_ID_long cellID : cellIDs) {
            g.addToCell(cellID.getRow(), cellID.getCol(), value);
        }
        env.checkAndMaybeFreeMemory();
    }

    /**
     * Add g2 to g with values from g2 multiplied by w.
     *
     * @param g Grid to be processed/modified.
     * @param g2 Grid from which values are added.
     * @param w Value g2 values are multiplied by.
     * @param oom The Order of Magnitude for the precision.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToGrid(Grids_GridNumber g, Grids_GridNumber g2, BigDecimal w,
            int oom)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        if (g2 != null) {
            addToGrid(g, g2, 0L, 0L, g2.getNRows() - 1L, g2.getNCols() - 1L, w, oom);
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
     * @param oom The Order of Magnitude for the precision.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToGrid(Grids_GridNumber g, Grids_GridNumber g2,
            long startRow, long startCol, long endRow, long endCol, BigDecimal w,
            int oom) throws IOException, ClassNotFoundException,
            Exception {
        env.checkAndMaybeFreeMemory();
        Grids_Dimensions dimensions = g2.getDimensions();
        Math_BigRational xMin = dimensions.getXMin();
        Math_BigRational yMin = dimensions.getYMin();
        Math_BigRational c = dimensions.getCellsize();
        Math_BigRational[] dc = new Math_BigRational[5];
        dc[1] = xMin.add(Math_BigRational.valueOf(startCol).multiply(c));
        dc[2] = yMin.add(Math_BigRational.valueOf(startRow).multiply(c));
        dc[3] = xMin.add(Math_BigRational.valueOf(endCol - startCol + 1L).multiply(c));
        dc[4] = yMin.add(Math_BigRational.valueOf(endRow - startRow + 1L).multiply(c));
        addToGrid(g, g2, startRow, startCol, endRow, endCol, dc, w, oom);
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
     * @param oom The Order of Magnitude for the precision.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToGrid(Grids_GridNumber g, Grids_GridNumber g2,
            long startRow, long startCol, long endRow, long endCol,
            Math_BigRational[] dc, BigDecimal w, int oom)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        long nrows = g.getNRows();
        long ncols = g.getNCols();
        BigDecimal noDataValue = getNoDataValueBigDecimal(g);
        Grids_Dimensions gD = g.getDimensions();
        BigDecimal g2NoDataValue = getNoDataValueBigDecimal(g);
        Grids_Dimensions g2D = g2.getDimensions();
        Grids_GridDoubleFactory gf = this.gridFactoryDouble;
        // If the region to be added is outside g then return.
        if ((dc[1].compareTo(gD.getXMax()) == 1)
                || (dc[3].compareTo(gD.getXMin()) == -1)
                || (dc[2].compareTo(gD.getYMax()) == 1)
                || (dc[4].compareTo(gD.getYMin()) == -1)) {
            return;
        }
        Math_BigRational gC = gD.getCellsize();
        Math_BigRational g2C = g2D.getCellsize();
        Math_BigRational g2CH = g2D.getHalfCellsize();
        if (g2C.compareTo(gC) == -1) {
            throw new UnsupportedOperationException();
        } else {
            // If g2Cellsize is the same as gCellsize g and g2 align
            if ((g2C.compareTo(gC) == 0)
                    && ((g2D.getXMin().divide(gC)).fractionPart().compareTo(
                            gD.getXMin().divide(gC).fractionPart()) == 0)
                    && ((g2D.getYMin().divide(gC)).fractionPart().compareTo(
                            gD.getYMin().divide(gC).fractionPart()) == 0)) {
                //println( "grids Align!" );
                // TODO: Control precision using xBigDecimal and yBigDecimal
                // rather than using x and y.
                for (long row = startRow; row <= endRow; row++) {
                    env.checkAndMaybeFreeMemory();
                    Math_BigRational y = g2.getCellY(row);
                    for (long col = startCol; col <= endCol; col++) {
                        Math_BigRational x = g2.getCellX(col);
                        BigDecimal v = g2.getCellBigDecimal(row, col);
                        if (v.compareTo(g2NoDataValue) != 0) {
                            if (v.compareTo(BigDecimal.ZERO) != 0) {
                                g.addToCell(x, y, v.multiply(w));
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
                Math_BigRational[] bounds;
                Grids_2D_ID_long i0;
                Grids_2D_ID_long i1;
                Grids_2D_ID_long i2;
                Grids_2D_ID_long i3;
                // gCellsize halved
                //BigDecimal gCH = g.getCellsize().divide(BigDecimal.valueOf(2));
                // gCellsize squared
                Math_BigRational gCS = gC.multiply(gC);
                // g2Cellsize squared
                Math_BigRational g2CS = g2C.multiply(g2C);
                // Area proportions
                BigDecimal aP1 = gCS.divide(g2CS).toBigDecimal(oom);
                for (int r = 0; r < nrows; r++) {
                    env.checkAndMaybeFreeMemory();
                    for (int c = 0; c < ncols; c++) {
                        bounds = g.getCellBounds(r, c);
                        //x = g.getCellXDouble(col);
                        //y = g.getCellYDouble(row);
                        i0 = g2.getCellID(bounds[0], bounds[3]);
                        i1 = g2.getCellID(bounds[2], bounds[3]);
                        i2 = g2.getCellID(bounds[0], bounds[1]);
                        i3 = g2.getCellID(bounds[2], bounds[1]);
                        BigDecimal d0 = g2.getCellBigDecimal(i0.getRow(), i0.getCol());
                        if (i0.equals(i1) && i1.equals(i2)) {
                            if (d0 != g2NoDataValue) {
                                tg1.addToCell(r, c, d0.multiply(aP1));
                                tg2.addToCell(r, c, aP1);
                            }
                        } else {
                            BigDecimal d1 = g2.getCellBigDecimal(i1.getRow(), i1.getCol());
                            BigDecimal d2 = g2.getCellBigDecimal(i2.getRow(), i2.getCol());
                            BigDecimal d3 = g2.getCellBigDecimal(i3.getRow(), i3.getCol());
                            if (!g2.isInGrid(i0.getRow(), i0.getCol())
                                    && d0 != g2NoDataValue) {
                                BigDecimal aP = getAP(bounds, g2, i0, i1, i2,
                                        gC, g2CS, g2CH).toBigDecimal(oom);
                                tg1.addToCell(r, c, d0.multiply(aP));
                                tg2.addToCell(r, c, aP);
                            }
                            if (!g2.isInGrid(i1) && d1 != g2NoDataValue) {
                                if (i1.equals(i0)) {
                                    BigDecimal aP = getAP13(bounds, g2, i1, i3, gC, g2CS, g2CH).toBigDecimal(oom);
                                    tg1.addToCell(r, c, d1.multiply(aP));
                                    tg2.addToCell(r, c, aP);
                                }
                            }
                            if (!g2.isInGrid(i2) && d2 != g2NoDataValue) {
                                if (!i2.equals(i0)) {
                                    BigDecimal aP = getAP23(bounds, g2, i2, i3, gC, g2CS, g2CH).toBigDecimal(oom);
                                    tg1.addToCell(r, c, d2.multiply(aP));
                                    tg2.addToCell(r, c, aP);
                                }
                            }
                            if (!g2.isInGrid(i3) && d3 != g2NoDataValue) {
                                if (i3 != i1 && i3 != i2) {
                                    BigDecimal aP = getAP3(bounds, g2, i3, gC, g2CS, g2CH).toBigDecimal(oom);
                                    tg1.addToCell(r, c, d3.multiply(aP));
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
                        double d0 = tg2.getCell(r, c);
                        if (!(d0 != 0.0d || d0 != noDataValue.doubleValue())) {
                            g.addToCell(r, c, w.multiply(BigDecimal
                                    .valueOf(tg1.getCell(r, c) / d0)));
                        }
                    }
                }
            }
        }
        env.checkAndMaybeFreeMemory();
    }

    /**
     * @param bounds bounds
     * @param g2 g2
     * @param i1 i1
     * @param i2 i2
     * @param i3 i3
     * @param gC gC
     * @param g2CS g2CS
     * @param g2CH g2CH
     * @return The Area Proportion
     */
    protected Math_BigRational getAP(Math_BigRational[] bounds,
            Grids_GridNumber g2, Grids_2D_ID_long i1, Grids_2D_ID_long i2,
            Grids_2D_ID_long i3, Math_BigRational gC, Math_BigRational g2CS,
            Math_BigRational g2CH) {
        Math_BigRational aP;
        if (i1.equals(i2) || i1.equals(i3)) {
            if (i1.equals(i2)) {
                aP = (((bounds[3]).subtract(g2.getCellY(i1).subtract(g2CH))
                        .multiply(gC)).divide(g2CS)).abs();
            } else {
                aP = ((((g2.getCellX(i1).add(g2CH)).subtract((bounds[0])))
                        .multiply(gC)).divide(g2CS)).abs();
            }
        } else {
            aP = (((bounds[3]).subtract(g2.getCellY(i1).subtract(g2CH)))
                    .multiply((g2.getCellX(i1).add(g2CH.subtract((bounds[0]))))
                            .divide(g2CS))).abs();
        }
        return aP;
    }
    
    /**
     * @param bounds bounds
     * @param g2 g2
     * @param i1 i1
     * @param i3 i3
     * @param gC gC
     * @param g2CS g2CS
     * @param g2CH g2CH
     * @return Area Proportion
     */
    protected Math_BigRational getAP13(Math_BigRational[] bounds,
            Grids_GridNumber g2, Grids_2D_ID_long i1, Grids_2D_ID_long i3,
            Math_BigRational gC, Math_BigRational g2CS, Math_BigRational g2CH) {
        Math_BigRational aP;
        if (i1.equals(i3)) {
            aP = ((((bounds[2]).subtract(g2.getCellX(i1).subtract(g2CH)))
                    .multiply(gC)).divide(g2CS)).abs();
        } else {
            aP = (((bounds[3]).subtract(g2.getCellY(i1).subtract(g2CH)))
                    .multiply((bounds[2]).subtract(g2.getCellX(i1)
                            .subtract(g2CH))).divide(g2CS)).abs();
        }
        return aP;
    }

    /**
     * @param bounds bounds
     * @param g2 g2
     * @param i2 i2
     * @param i3 i3
     * @param gC gC
     * @param g2CS g2CS
     * @param g2CH g2CH
     * @return Area Proportion
     */
    protected Math_BigRational getAP23(Math_BigRational[] bounds, Grids_GridNumber g2,
            Grids_2D_ID_long i2, Grids_2D_ID_long i3, Math_BigRational gC,
            Math_BigRational g2CS, Math_BigRational g2CH) {
        Math_BigRational aP;
        if (i2.equals(i3)) {
            aP = ((((g2.getCellY(i2).add(g2CH)).subtract((bounds[1])))
                    .multiply(gC)).divide(g2CS)).abs();
        } else {
            aP = ((((g2.getCellY(i2).add(g2CH)).subtract((bounds[1])))
                    .multiply((g2.getCellX(i2).add(g2CH))
                            .subtract((bounds[0])))).divide(g2CS))
                    .abs();
        }
        return aP;
    }

    /**
     * @param bounds bounds
     * @param g2 g2
     * @param i3 i3
     * @param gC gC
     * @param g2CS g2CS
     * @param g2CH g2CH
     * @return Area Proportion
     */
    protected Math_BigRational getAP3(Math_BigRational[] bounds, Grids_GridNumber g2,
            Grids_2D_ID_long i3, Math_BigRational gC, Math_BigRational g2CS,
            Math_BigRational g2CH) {
        return ((((g2.getCellY(i3).add(g2CH)).subtract((bounds[1])))
                .multiply((bounds[2]).subtract((g2.getCellX(i3))
                        .subtract(g2CH)))).divide(g2CS)).abs();
    }

    /**
     * Multiply g0 and g1 and return a new grid.
     *
     * @param type Determines the type of Grid returned. BigDecimal, double or
     * int.
     * @param g0 The first grid to multiply.
     * @param g1 The second grid to multiply
     * @param oom The Order of Magnitude for the precision.
     * @return g0 * g1
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridNumber multiply(Number type, Grids_GridNumber g0,
            Grids_GridNumber g1, int oom)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        Grids_GridNumber r;
        long nRows = g0.getNRows();
        long nCols = g0.getNCols();
        if (type instanceof BigDecimal) {
            r = gridFactoryBD.create(g0, 0L, 0L, nRows - 1, nCols - 1);
        } else if (type instanceof Double) {
            r = gridFactoryDouble.create(g0, 0L, 0L, nRows - 1, nCols - 1);
        } else if (type instanceof Integer) {
            r = gridFactoryInt.create(g0, 0L, 0L, nRows - 1, nCols - 1);
        } else {
            throw new Exception("Unknown type!");
        }
        BigDecimal ndv0 = g0.ndv;
        BigDecimal ndv1 = g1.ndv;
        int ncr = g0.getNChunkRows();
        int ncc = g0.getNChunkCols();
        if (g0.isCoincident(g1)) {
            if (g0.isSameDimensionsAndChunks(g1)) {
                if (g0.isSameDimensionsAndChunks(r)) {
                    /**
                     * Grids are coincident and have the same chunks.
                     */
                    for (int cr = 0; cr < ncr; cr++) {
                        for (int cc = 0; cc < ncc; cc++) {
                            Grids_2D_ID_int i = new Grids_2D_ID_int(cr, cc);
                            env.addToNotToClear(g0, i);
                            env.addToNotToClear(g1, i);
                            env.addToNotToClear(r, i);
                            env.checkAndMaybeFreeMemory();
                            int cnr = g0.getChunkNRows(cr);
                            int cnc = g0.getChunkNCols(cc);
                            for (int ccr = 0; ccr < cnr; ccr++) {
                                for (int ccc = 0; ccc < cnc; ccc++) {
                                    BigDecimal v0 = g0.getCellBigDecimal(cr, cc,
                                            ccr, ccc);
                                    if (v0.compareTo(ndv0) != 0) {
                                        BigDecimal v1 = g1.getCellBigDecimal(cr,
                                                cc, ccr, ccc);
                                        if (v1.compareTo(ndv1) == 0) {
                                            r.setCell(cr, cc, ccr, ccc, ndv0);
                                        } else {
                                            r.setCell(cr, cc, ccr, ccc,
                                                    v0.multiply(v1));
                                        }
                                    }
                                }
                            }
                            env.removeFromNotToClear(g0, i);
                            env.removeFromNotToClear(g1, i);
                            env.removeFromNotToClear(r, i);
                        }
                    }
                } else {
                    /**
                     * Input grids are coincident and have the same chunks, but
                     * the result has different chunks.
                     */
                    for (int cr = 0; cr < ncr; cr++) {
                        int cnr = g0.getChunkNRows(cr);
                        /**
                         * Prefer to keep a row of r chunks in memory.
                         */
                        for (int ccr = 0; ccr < cnr; ccr++) {
                            long row = r.getRow(cr, ccr);
                            env.addToNotToClear(r, r.getChunkRow(row));
                        }
                        for (int cc = 0; cc < ncc; cc++) {
                            Grids_2D_ID_int i = new Grids_2D_ID_int(cr, cc);
                            env.addToNotToClear(g0, i);
                            env.addToNotToClear(g1, i);
                            env.checkAndMaybeFreeMemory();
                            int cnc = g0.getChunkNCols(cc);
                            for (int ccr = 0; ccr < cnr; ccr++) {
                                long row = r.getRow(cr, ccr);
                                env.addToNotToClear(r, r.getChunkRow(row));
                                for (int ccc = 0; ccc < cnc; ccc++) {
                                    BigDecimal v0 = g0.getCellBigDecimal(cr, cc,
                                            ccr, ccc);
                                    if (v0.compareTo(ndv0) != 0) {
                                        BigDecimal v1 = g1.getCellBigDecimal(cr, cc,
                                                ccr, ccc);
                                        if (v1.compareTo(ndv1) != 0) {
                                            r.setCell(row, r.getCol(cc, ccc),
                                                    v0.multiply(v1));
                                        }
                                    }
                                }
                            }
                            env.removeFromNotToClear(g0, i);
                            env.removeFromNotToClear(g1, i);
                        }
                        /**
                         * Allow the row of r chunks to be swapped.
                         */
                        for (int ccr = 0; ccr < cnr; ccr++) {
                            long row = r.getRow(cr, ccr);
                            env.removeFromNotToClear(r, r.getChunkRow(row));
                        }
                    }
                }
                return r;
            } else {
                multiply(g0, g1, r, ncr, ncc, ndv0, ndv1);
                return r;
            }
        } else {
            Grids_Dimensions dimg0 = g0.getDimensions();
            Grids_Dimensions dimg1 = g1.getDimensions();
            Math_BigRational cg0 = dimg0.getCellsize();
            Math_BigRational cg1 = dimg1.getCellsize();

            /**
             * Deal with all cases.
             */
            if (cg0.compareTo(cg1) == 1) {
                Grids_GridDouble ag1 = aggregate(g1, "mean", dimg0, oom);
                return multiply(type, g0, ag1, oom);
            } else {
                /**
                 * (cg0.compareTo(cg1) == 0) can be treated in the same way as
                 * (cg0.compareTo(cg1) == -1). If the frameworks align and g1 is
                 * a neat aggregation, then we can simply multiply the value of
                 * g1 at the centroid for each cell value of g0. Otherwise some
                 * intersection is needed to calculate some result cell values.
                 * The intersection can be achieved using a disaggregation to a
                 * cellsize smaller than g0 and then an aggregation.
                 */
                Math_BigRational div = cg1.divide(cg0);
                if (div.isInteger()) {
                    Math_BigRational x0 = dimg0.getXMin();
                    Math_BigRational x1 = dimg1.getXMin();
                    boolean doSimple = false;
                    switch (x1.compareTo(x0)) {
                        case 0:
                            doSimple = true;
                            break;
                        case -1:
                            if (x0.subtract(x1).isInteger()) {
                                doSimple = true;
                            }
                            break;
                        default:
                            if (x0.subtract(x1).isInteger()) {
                                doSimple = true;
                            }
                            break;
                    }
                    if (doSimple) {
                        multiply(g0, g1, r, ncr, ncc, ndv0, ndv1);
                        return r;
                    }
                }
                int factor = div.integerPart().intValue() + 1;
                Grids_GridDouble dg1 = disaggregate(g1, factor);
                Grids_GridDouble adg1 = aggregate(dg1, "mean", dimg0, oom);
                return multiply(type, g0, adg1, oom);
            }
        }
    }

    /**
     * Multiply the values in g0 by g1.
     * @param g0 g0
     * @param g1 g1
     * @param r r
     * @param ncr ncr
     * @param ncc ncc
     * @param ndv0 ndv0
     * @param ndv1 ndv1
     * @throws Exception If encountered.
     */
    protected void multiply(Grids_GridNumber g0, Grids_GridNumber g1,
            Grids_GridNumber r, int ncr, int ncc, BigDecimal ndv0,
            BigDecimal ndv1) throws Exception {
        for (int cr = 0; cr < ncr; cr++) {
            int cnr = g0.getChunkNRows(cr);
            /**
             * Prefer to keep a row of g0 and r chunks in memory.
             */
            for (int ccr = 0; ccr < cnr; ccr++) {
                long row = g0.getRow(cr, ccr);
                Math_BigRational y = g0.getCellY(row);
                env.addToNotToClear(g1, g1.getChunkRow(y));
                env.addToNotToClear(r, r.getChunkRow(y));
            }
            for (int cc = 0; cc < ncc; cc++) {
                Grids_2D_ID_int i = new Grids_2D_ID_int(cr, cc);
                env.addToNotToClear(g0, i);
                env.checkAndMaybeFreeMemory();
                int cnc = g0.getChunkNCols(cc);
                for (int ccr = 0; ccr < cnr; ccr++) {
                    long row = r.getRow(cr, ccr);
                    Math_BigRational y = g0.getCellY(row);
                    for (int ccc = 0; ccc < cnc; ccc++) {
                        long col = r.getCol(cc, ccc);
                        BigDecimal v0 = g0.getCellBigDecimal(cr, cc,
                                ccr, ccc);
                        if (v0.compareTo(ndv0) != 0) {
                            Math_BigRational x = g0.getCellX(col);
                            BigDecimal v1 = g1.getCellBigDecimal(x, y);
                            if (v1.compareTo(ndv1) == 0) {
                                r.setCell(x, y, ndv0);
                            } else {
                                r.setCell(x, y, v0.multiply(v1));
                            }
//Debugging code
//                            env.env.log("result so far");
//                            env.env.log("x=" + x.toPlainString());
//                            env.env.log("y=" + y.toPlainString());
//                            env.env.log("row=" + row);
//                            env.env.log("col=" + col);
//                            env.env.log("v0=" + v0.toPlainString());
//                            env.env.log("v1=" + v1.toPlainString());
//                            r.log(10, 10);
                        }
                    }
                }
                env.removeFromNotToClear(g0, i);
            }
            /**
             * Allow the row of g0 and r chunks to be swapped.
             */
            for (int ccr = 0; ccr < cnr; ccr++) {
                long row = g0.getRow(cr, ccr);
                Math_BigRational y = g0.getCellY(row);
                env.addToNotToClear(g1, g1.getChunkRow(y));
                env.addToNotToClear(r, r.getChunkRow(y));
            }
        }
    }

    /**
     * This returns a grid with a {@code factor} greater resolution than
     * {@code g}. If the cellsize of the result cannot be stored precisely, then
     * this should result in a warning. IF the cellsize is divided by an even
     * factor, in all cases except for very extreme cases it can be stored
     * exactly. However, dividing by an odd power might result in a number that
     * cannot be stored precisely as a decimal number. This in fact is a reason
     * why cellsize should be stored not as a single BigDecimal Number, but as a
     * fraction or rational number in two parts - a numerator and a denominator.
     *
     * @param g The grid on which the result values are based.
     * @param factor The number of times smaller the cellsize of the result is.
     * @return A grid with a {@code factor} greater resolution than {@code g}.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     * @throws ArithmeticException If the cellsize of {@code g} cannot be
     * divided by {@code factor} and stored exactly.
     * @throws Exception If encountered.
     */
    public Grids_GridDouble disaggregate(Grids_GridNumber g, int factor)
            throws IOException, ClassNotFoundException, Exception {
        Grids_Dimensions dim = g.getDimensions();
        Math_BigRational cellsize = dim.getCellsize();
        Math_BigRational rcellsize = cellsize.divide(factor);
        if (g instanceof Grids_GridDouble) {
            return null;
        } else if (g instanceof Grids_GridInt) {
            return null;
        } else {
            return null;
        }
    }

    /**
     * Divide g0 by g1 and return a new grid. It is assumed that the dimensions
     * are all the same;
     *
     * @param g0 Numerator
     * @param g1 Denominator
     * @return g0 / g1
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridDouble divide(Grids_GridDouble g0, Grids_GridDouble g1)
            throws IOException, ClassNotFoundException, Exception {
        Grids_GridDouble r;
        long nRows = g0.getNRows();
        long nCols = g0.getNCols();
        r = gridFactoryDouble.create(g0, 0L, 0L, nRows - 1, nCols - 1);
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
     * For creating an aggregated Grids_GridDouble at a lower level of
     * resolution than the grid {@code g}. The result values are either the sum,
     * mean, max or min of values in grid depending on {@code statistic}.
     *
     * @param grid the Grids_GridDouble to be processed
     * @param cellFactor the number of times wider/higher the aggregated grid
     * cells are to be
     * @param statistic "sum", "mean", "max", or "min" depending on what
     * aggregate of values are wanted
     * @param rowOffset The number of rows above or below the origin of grid
     * where the aggregation is to start.
     * <ul>
     * <li>If {@code rowOffset > 0} then the result yllcorner will be above grid
     * yllcorner</li>
     * <li>If {@code rowOffset < 0} result yllcorner will be below grid
     * yllcorner</li>
     * </ul>
     * @param colOffset The number of columns above or below the origin of grid
     * where the aggregation is to start.
     * <ul>
     * <li>If {@code colOffset > 0} result xllcorner will be right of grid
     * xllcorner</li>
     * <li>If {@code colOffset < 0} result xllcorner will be left of grid
     * xllcorner</li>
     * </ul>
     * @return An aggregated Grids_GridDouble at a lower level of resolution
     * than the grid {@code g}. The result values are either the sum, mean, max
     * or min of values in grid depending on {@code statistic}.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridDouble aggregate(Grids_GridNumber grid, int cellFactor,
            String statistic, int rowOffset, int colOffset) throws IOException,
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
        Math_BigRational cellsize = dimensions.getCellsize();
        Math_BigRational xMin = dimensions.getXMin();
        Math_BigRational yMin = dimensions.getYMin();
        Math_BigRational xMax = dimensions.getXMax();
        Math_BigRational yMax = dimensions.getYMax();
        BigDecimal ndv = getNoDataValueBigDecimal(grid);
        Math_BigRational rC = cellsize.multiply(cellFactor);
        Math_BigRational rXMin = xMin.add(cellsize.multiply(colOffset));
        Math_BigRational rYMin = yMin.add(cellsize.multiply(rowOffset));

        //double resultCellsize = cellsize * ( double ) cellFactor;
        //double width = cellsize * ncols;
        //double height = cellsize * nrows;
        //double resultXllcorner = xllcorner + ( colOffset * cellsize );
        //double resultYllcorner = yllcorner + ( rowOffset * cellsize );
        // Calculate resultNrows and resultHeight
        long rNrows = 1L;
        Math_BigRational rH = Math_BigRational.valueOf(rC.toString());
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
        Math_BigRational rWidth = Math_BigRational.valueOf(rC.toString());
        //double resultWidth = resultCellsize;
        while (rXMin.add(rWidth).compareTo(xMax) == -1) {
            rNrows++;
            rWidth = rWidth.add(rC);
        }
        //while ( ( resultXllcorner + resultWidth ) < ( xllcorner + width ) ) {
        //    resultNcols ++;
        //    resultWidth += resultCellsize;
        //}
        Math_BigRational rXMax = rXMin.add(rWidth);
        Math_BigRational rYMax = rYMin.add(rH);
        Grids_Dimensions rD = new Grids_Dimensions(rXMin,
                rXMax, rYMin, rYMax, rC);
        // Initialise result
        double ndvd = ndv.doubleValue();
        gridFactoryDouble.setNoDataValue(ndv.doubleValue());
        Grids_GridDouble r = gridFactoryDouble.create(rNrows, rNcols, rD);

        // sum
        if (statistic.equalsIgnoreCase("sum")) {
            Grids_GridDouble count = gridFactoryDouble.create(rNrows, rNcols, rD);
            Grids_GridDouble normaliser = gridFactoryDouble.create(rNrows, rNcols, rD);
            for (long row = 0; row < nrows; row++) {
                for (long col = 0; col < ncols; col++) {
                    Math_BigRational x = grid.getCellX(col);
                    Math_BigRational y = grid.getCellY(row);
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
            Grids_GridDouble numerator = gridFactoryDouble.create(rNrows, rNcols, rD);
            Grids_GridDouble denominator = gridFactoryDouble.create(rNrows, rNcols, rD);
            for (int row = 0; row < nrows; row++) {
                for (int col = 0; col < ncols; col++) {
                    Math_BigRational x = grid.getCellX(col);
                    Math_BigRational y = grid.getCellY(row);
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
                    Math_BigRational x = grid.getCellX(col);
                    Math_BigRational y = grid.getCellY(row);
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
                    Math_BigRational x = grid.getCellX(col);
                    Math_BigRational y = grid.getCellY(row);
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
    //            return aggregate( grid, resultCellsize, statistic, resultXllcorner, resultYllcorner, new Grids_GridDoubleFactory() );
    //        } catch ( OutOfMemoryError e ) {
    //            return aggregate( grid, resultCellsize, statistic, resultXllcorner, resultYllcorner, new Grid2DSquareCellDoubleFileFactory() );
    //        }
    //    }
    
    /**
     * @param g The grid
     * @return the NoDataValue of g as a BigDecimal.
     * @throws Exception If encountered.
     */
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
     * depending on statistic. Use this aggregate method if result is to have a
     * new spatial frame. NB. In the calculation of the sum and the mean if
     * there is a cell in grid which has a data value then the result which
     * incorporates that cell has a data value. For this result cell, any of the
     * cells in grid which have noDataValues their value is taken as that of the
     * average of its nearest cells with a value. In the calculation of the max
     * and the min noDataValues are simply ignored. Formerly noDataValues were
     * treated as the average of values within a result cell.
     *
     * @param g The stats to be processed
     * @param stats "sum", "mean", "max", or "min" depending on what aggregate
     * of values are wanted
     * @param rD result dimensions.
     * @param oom The Order of Magnitude for the precision.
     * @return An aggregate grid.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridDouble aggregate(Grids_GridNumber g, String stats,
            Grids_Dimensions rD, int oom)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        // Initialistaion
        long nrows = g.getNRows();
        long ncols = g.getNCols();
        Grids_Dimensions dim = g.getDimensions();
        BigDecimal ndv = getNoDataValueBigDecimal(g);
        double ndvd = ndv.doubleValue();
        Math_BigRational rC = rD.getCellsize();
        Math_BigRational rXMin = rD.getXMin();
        Math_BigRational rYMin = rD.getYMin();
        //BigDecimal rXMax = rD.getXMax();
        //BigDecimal rYMax = rD.getYMax();
        Math_BigRational rCH = rC.divide(2);
        Math_BigRational rCS = rC.multiply(rC);

        Math_BigRational c = dim.getCellsize();
        Math_BigRational xMin = dim.getXMin();
        Math_BigRational yMin = dim.getYMin();
        Math_BigRational xMax = dim.getXMax();
        Math_BigRational yMax = dim.getYMax();
        Math_BigRational cS = c.multiply(c);
        Math_BigRational cH = c.divide(2);
        //double width = cellsize * ncols;
        //double height = cellsize * nrows;
        // Test this is an aggregation
        if (rC.compareTo(c) != 1) {
            env.env.log("!!!Warning: Not an aggregation as "
                    + "resultCellsize < cellsize. Returning null!");
            return null;
        }
        // Test for intersection
        if ((rXMin.compareTo(xMin.add(c.multiply(Math_BigRational.valueOf(ncols)))) == 1)
                || (rYMin.compareTo(yMin.add(c.multiply(Math_BigRational.valueOf(nrows)))) == 1)) {
            System.err.println(
                    "!!!Warning: No intersection for aggregation. Returning null!");
            return null;
        }
        /**
         * If rC is an integer multiple of c and g aligns with r then return a
         * cellFactor aggregation as it should be faster.
         */
        if (true) {
            Math_BigRational t0 = rC.divide(c);
            Math_BigRational t1 = rXMin.divide(c);
            Math_BigRational t2 = xMin.divide(c);
            Math_BigRational t3 = rYMin.divide(c);
            Math_BigRational t4 = yMin.divide(c);
            if ((t0.compareTo(Math_BigRational.valueOf(t0.toBigDecimal(oom).toBigInteger())) == 0)
                    && (t1.compareTo(Math_BigRational.valueOf(t1.toBigDecimal(oom).toBigInteger().toString()))
                    == t2.compareTo(Math_BigRational.valueOf(t2.toBigDecimal(oom).toBigInteger().toString())))
                    && (t3.compareTo(Math_BigRational.valueOf(t3.toBigDecimal(oom).toBigInteger().toString()))
                    == t4.compareTo(Math_BigRational.valueOf(t4.toBigDecimal(oom).toBigInteger().toString())))) {
                int cellFactor = rC.divide(c).intValue();
                int rowOffset = yMin.subtract(rYMin.divide(c)).intValue();
                int colOffset = xMin.subtract(rXMin.divide(c)).intValue();
                return aggregate(g, cellFactor, stats, rowOffset, colOffset);
            }
        }
        // Calculate number of rows and height of result.
        long rNrows = 1L;
        Math_BigRational rH = Math_BigRational.valueOf(rC.toString());
        while (rYMin.add(rH).compareTo(yMax) == -1) {
            rNrows++;
            rH = rH.add(rC);
        }
        // Calculate number of columns and width of result.
        long rNcols = 1L;
        Math_BigRational rW = Math_BigRational.valueOf(rC.toString());
        while (rXMin.add(rW).compareTo(xMax) == -1) {
            rNrows++;
            rW = rW.add(rC);
        }
        //rXMax = xMin.add(rW);
        //rYMax = yMin.add(rH);
        // Initialise result
        gridFactoryDouble.setNoDataValue(ndvd);
        Grids_GridDouble r = gridFactoryDouble.create(rNrows, rNcols, rD);
        // sum
        if (stats.equalsIgnoreCase("sum")) {
            Grids_GridDouble totalValueArea = gridFactoryDouble.create(rNrows, rNcols, rD);
            Grids_2D_ID_long[] cellIDs = new Grids_2D_ID_long[4];
            for (int row = 0; row < nrows; row++) {
                for (int col = 0; col < ncols; col++) {
                    Math_BigRational[] bounds = g.getCellBounds(row, col);
                    cellIDs[0] = r.getCellID(bounds[0], bounds[3]);
                    cellIDs[1] = r.getCellID(bounds[2], bounds[3]);
                    cellIDs[2] = r.getCellID(bounds[0], bounds[1]);
                    cellIDs[3] = r.getCellID(bounds[2], bounds[1]);
                    BigDecimal value = g.getCellBigDecimal(row, col);
                    if (value != ndv) {
                        if (cellIDs[0].equals(cellIDs[1]) && cellIDs[1].equals(
                                cellIDs[2])) {
                            r.addToCell(cellIDs[0], value.doubleValue());
                            totalValueArea.addToCell(cellIDs[0], 1.0d);
                        } else {
                            BigDecimal aP = getAP(bounds, r, cellIDs[0], cellIDs[1],
                                    cellIDs[2], c, rCS, rCH).toBigDecimal(oom);
                            r.addToCell(cellIDs[0], value.multiply(aP));
                            totalValueArea.addToCell(cellIDs[0], aP);
                        }
                        if (!cellIDs[1].equals(cellIDs[0])) {
                            BigDecimal aP = getAP13(bounds, r, cellIDs[1], cellIDs[3], c,
                                    rCS, rCH).toBigDecimal(oom);
                            r.addToCell(cellIDs[1], value.multiply(aP));
                            totalValueArea.addToCell(cellIDs[0], aP);
                        }
                        if (!cellIDs[2].equals(cellIDs[0])) {
                            BigDecimal aP = getAP23(bounds, r, cellIDs[2], cellIDs[3], c,
                                    rCS, rCH).toBigDecimal(oom);
                            r.addToCell(cellIDs[2], value.multiply(aP));
                        }
                        if (!cellIDs[3].equals(cellIDs[1]) && !cellIDs[3]
                                .equals(cellIDs[2])) {
                            BigDecimal aP = getAP3(bounds, r, cellIDs[3], c, rCS, rCH).toBigDecimal(oom);
                            r.addToCell(cellIDs[3], value.multiply(aP));
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

        // mean
        if (stats.equalsIgnoreCase("mean")) {
            double denominator = (rC.doubleValue() * rC.doubleValue())
                    / (c.doubleValue() * c.doubleValue());
            Grids_GridDouble sum = aggregate(g, "sum", rD, oom);
            addToGrid(r, sum, BigDecimal.valueOf(1.0d / denominator), oom);
        }

        // max
        if (stats.equalsIgnoreCase("max")) {
            for (long row = 0; row < nrows; row++) {
                for (long col = 0; col < ncols; col++) {
                    BigDecimal value = g.getCellBigDecimal(row, col);
                    if (value.compareTo(ndv) != 0) {
                        //BigDecimal x = g.getCellX(col);
                        //BigDecimal y = g.getCellY(row);
                        Math_BigRational[] bounds = g.getCellBounds(row, col);
                        double max = r.getCell(bounds[0], bounds[3]);
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
        if (stats.equalsIgnoreCase("min")) {
            double min;
            Math_BigRational[] bounds;
            double halfCellsize = cH.doubleValue();
            for (long row = 0; row < nrows; row++) {
                for (long col = 0; col < ncols; col++) {
                    BigDecimal value = g.getCellBigDecimal(row, col);
                    if (value.compareTo(ndv) != 0) {
                        double vD = value.doubleValue();
                        Math_BigRational x = g.getCellX(col);
                        Math_BigRational y = g.getCellY(row);
                        bounds = g.getCellBounds(row, col);
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
     * @param g The grid.
     * @param row The row.
     * @param cellDistance The cell distance.
     * @return A double[][] of all cells within cellDistance.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected double[][] getRowProcessInitialData(Grids_GridDouble g,
            int cellDistance, long row) throws IOException, Exception,
            ClassNotFoundException {
        int l = (cellDistance * 2) + 1;
        double[][] r = new double[l][l];
        for (long r2 = -cellDistance; r2 <= cellDistance; r2++) {
            for (long c = -cellDistance; c <= cellDistance; c++) {
                r[(int) r2 + cellDistance][(int) c + cellDistance]
                        = g.getCell(row + r2, c);
            }
        }
        return r;
    }

    /**
     * Returns a double[][] based on {@code previous} which has had the left
     * most column removed and a new column added at the right.
     *
     * @param g The grid.
     * @param col The column.
     * @param previous The previous data.
     * @param cellDistance The cell distance.
     * @param row The row.
     * @return A double[][] based on {@code previous} which has had the left
     * most column removed and a new column added at the right.
     *
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
     * @param g The grid to output.
     * @param outDir The output directory.
     * @param ie The image exporter.
     * @param imageTypes The image types.
     * @param eage The ESRI AsciiGrid exporter.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
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
            file = new IO_Path(Paths.get(outDir.toString(), s));
            ie.toGreyScaleImage(g, this, file, imageTypes[i]);
        }
        s = g.getName() + dotASC;
        file = Paths.get(outDir.toString(), s);
        eage.toAsciiFile(g, file, noDataValue);
    }

    /**
     * @param g The grid to output.
     * @param outDir The output directory.
     * @param ie The image exporter.
     * @param imageTypes The image types.
     * @param hoome If {@code true} then an attempt to handle OutOfMemory Errors
     * is made.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void outputImage(Grids_GridNumber g, IO_Path outDir,
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
                if (env.swapChunksExcept_Account(g, hoome).detail < 1) {
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
     * For outputting grid {@code g} in ESRIAsciiGrid format. NoDataValues is
     * set to -9999.0,
     *
     * @param g The grid to output
     * @param outDir The directory to output to.
     * @param eage The exporter.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void outputESRIAsciiGrid(Grids_GridNumber g, Path outDir,
            Grids_ESRIAsciiGridExporter eage) throws IOException,
            ClassNotFoundException, Exception {
        if (eage == null) {
            eage = new Grids_ESRIAsciiGridExporter(env);
        }
        String ndv = "-9999.0";
        String fn = g.getName() + ".asc";
        Path file = Paths.get(outDir.toString(), fn);
        eage.toAsciiFile(g, file, ndv);
    }

}
