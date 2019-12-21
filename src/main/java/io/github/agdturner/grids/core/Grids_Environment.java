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
package io.github.agdturner.grids.core;

import io.github.agdturner.grids.d2.chunk.Grids_Chunk;
import io.github.agdturner.grids.d2.grid.Grids_Grid;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import uk.ac.leeds.ccg.agdt.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import uk.ac.leeds.ccg.agdt.math.Math_BigDecimal;
import io.github.agdturner.grids.io.Grids_Files;
import io.github.agdturner.grids.process.Grids_Processor;

/**
 * Grids_Environment.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_Environment extends Grids_MemoryManager
        implements Grids_Memory {

    private static final long serialVersionUID = 1L;

    /**
     * A set of all the grids. These may have chunks stored in the fast access
     * memory that can be cached (stored) elsewhere and cleared from the fast
     * access memory (swapped out). If a cache of the chunk already exists and
     * is effectively no different to the copy of the chunk in the fast access
     * memory, then the chunk can be cleared from the fast access memory by
     * setting the reference to this to null. The chunk may not be immediately
     * cleared, the timing of this is left up to normal garbage collection.
     *
     * If chunks are not in the fast access memory when they are wanted, they
     * can be loaded from a cache as needed - although this may necessitate
     * other data being swapped out in order to manage with the available fast
     * access memory.
     */
    protected transient Set<Grids_Grid> grids;

    /**
     * For indicating which chunks of which grids are not to be cleared from the
     * fast access memory. This map is modified by data processing algorithms to
     * to try to ensure a reasonable efficiency and prevent chunks being cleared
     * only to be reloaded shortly afterwards, when there were either: other
     * chunks to clear that would not have been reloaded in the same frame; or,
     * some other data from some other environment that would have been better
     * to clear instead.
     *
     * Maintaining this map has a marginal efficiency cost which may be
     * noticeable when all the data fits easily into the available fast access
     * memory and comparing the processing functionality of this library with
     * other libraries which essentially generate the same processed results.
     * However this library is more geared to supporting the processing of large
     * volumes of data (when it is typically not the case that all the data will
     * fit in the fast access memory of the machine, and processing may involve
     * a significant amount of caching, clearing and reloading of data). In such
     * circumstances it would be more interesting to compare the performance of
     * this library with other software that is also geared are not geared for
     * such memory management.
     *
     * For some methods cell values from a set of neighbouring chunks are wanted
     * along with other nearby cell values or chunk statistics from other grids.
     * An example case is calculating geographically weighted statistics such as
     * the difference between two grids, where the statistic takes in values
     * from each grid within a specific distance of any cell to produce a result
     * for a given cell in a new output grid.
     *
     * There are additional maps and sets that help improve algorithmic
     * efficiency further, including a set of very lightweight chunks which are
     * hardly worth clearing unless options are very limited.
     *
     * In some cases it might be best for the method to throw an Exception
     * rather than to slowly grind on and potentially taking overly long to
     * produce a result when restructuring the data into smaller or larger
     * chunks or storing the data in chunks of different types may be a
     * worthwhile step.
     *
     * An overhead is involved in changing chunk types and chunk restructuring.
     * What is best to do depends on many factors, but generally what is wanted
     * is a good solution or a solution that works rather than the best solution
     * - the one that creates a result in the fastest and most efficient way.
     */
    protected transient HashMap<Grids_Grid, Set<Grids_2D_ID_int>> notToCache;

    /**
     * For storing a {@link Math_BigDecimal} instance.
     */
    public transient Math_BigDecimal bd;

    /**
     * For storing a {@link Grids_Processor} instance.
     */
    protected transient Grids_Processor processor;

    /**
     * For storing a {@link Grids_Files} instance.
     */
    public transient Grids_Files files;

    /**
     * For storing a {@link Generic_Environment} instance.
     */
    public transient final Generic_Environment env;

    /**
     * Creates a new Grids_Environment.
     *
     * @see #Grids_Environment(Generic_Environment). The Generic_Environment is
     * initialised using:
     * {@code new Generic_Environment(new Generic_Defaults())}.
     *
     * @throws java.io.IOException If encountered.
     * @throws Exception If there is another problem setting up the file store.
     */
    public Grids_Environment() throws IOException, Exception {
        this(new Generic_Environment(new Generic_Defaults()));
    }

    /**
     * Creates a new Grids_Environment.
     *
     * @see #Grids_Environment(Generic_Environment, Generic_Path). The
     * Generic_Path is obtained from {@code e}.
     *
     * @param e The Generic_Environment.
     * @throws java.io.IOException If encountered.
     */
    public Grids_Environment(Generic_Environment e) throws IOException, Exception,
            Exception {
        this(e, e.files.getDir());
    }

    /**
     * Creates a new Grids_Environment.
     *
     * @param e What {@link #env} is set to.
     * @param dir Used to initialise {@link #files} using
     * {@link Grids_Files(Generic_Path)}.
     * @throws java.io.IOException If encountered.
     */
    public Grids_Environment(Generic_Environment e, Generic_Path dir)
            throws IOException, Exception {
        this.env = e;
        initMemoryReserve(Default_Memory_Threshold, e);
        initGrids();
        initNotToCache();
        Path p = e.getLogDir(Grids_Strings.s_grids);
//        Path p = dir.getPath();
//        if (!Files.exists(p)) {
//            Files.createDirectory(p);
//        }
//        files = new Grids_Files(new Generic_Defaults(Paths.get(dir.toString(),
//                Grids_Strings.s_grids)));
        files = new Grids_Files(new Generic_Defaults(Paths.get(dir.toString(),
                Grids_Strings.s_grids)));
        e.files.setDir(p);
    }

    /**
     * If {@link #processor} is not {@code null}, it is returned.If
     * {@link #processor} is {@code null} it is initialised and then returned.
     *
     * @return {@link #processor} initialising first if it is {@code null}.
     * @throws java.io.IOException If encountered initialising
     * {@link #processor}.
     * @throws java.lang.ClassNotFoundException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public Grids_Processor getProcessor() throws IOException,
            ClassNotFoundException, Exception {
        if (processor == null) {
            processor = new Grids_Processor(this);
        }
        return processor;
    }

    /**
     * @param p What {@link #processor} is set to.
     */
    public void setProcessor(Grids_Processor p) {
        this.processor = p;
    }

    /**
     * If {@link #grids} is {@code null} it is initialised as a new
     * {@link java.util.Set}.
     */
    protected final void initGrids() {
        if (grids == null) {
            grids = new HashSet<>();
        }
    }

    /**
     * Initialise or re-initialise a store of references to data that would
     * ideally not be cached.
     */
    public final void initNotToCache() {
        notToCache = new HashMap<>();
    }

    /**
     * Adds all the chunkIDs of g to notToCache.
     *
     * @param g
     */
    public final void addToNotToCache(Grids_Grid g) {
        notToCache.put(g, g.getChunkIDs());
    }

    /**
     * Removes g from the notToCache.
     *
     * @param g
     */
    public final void removeFromNotToCache(Grids_Grid g) {
        notToCache.remove(g);
    }

    /**
     * Adds the specific {@code chunkRow} of chunks of {@code g} to notToCache.
     *
     * @param g
     * @param chunkRow
     */
    public final void addToNotToCache(Grids_Grid g, int chunkRow) {
        int n = g.getNChunkCols();
        for (int c = 0; c < n; c++) {
            addToNotToCache(g, new Grids_2D_ID_int(chunkRow, c));
        }
    }

    /**
     * Removes the chunkID of g to notToCache.
     *
     * @param g
     * @param chunkRow
     */
    public final void removeFromNotToCache(Grids_Grid g, int chunkRow) {
        int n = g.getNChunkCols();
        for (int c = 0; c < n; c++) {
            removeFromNotToCache(g, new Grids_2D_ID_int(chunkRow, c));
        }
    }

    /**
     * Adds the chunkID of g to notToCache.
     *
     * @param g
     * @param chunkID
     */
    public final void addToNotToCache(
            Grids_Grid g,
            Grids_2D_ID_int chunkID) {
        if (notToCache.containsKey(g)) {
            notToCache.get(g).add(chunkID);
        } else {
            Set<Grids_2D_ID_int> chunkIDs;
            chunkIDs = new HashSet<>();
            chunkIDs.add(chunkID);
            notToCache.put(g, chunkIDs);
        }
    }

    /**
     * Adds the chunkID of each grid in g to notToCache.
     *
     * @param g
     * @param chunkID
     */
    public final void addToNotToCache(
            Grids_Grid[] g,
            Grids_2D_ID_int chunkID) {
        for (Grids_Grid g1 : g) {
            addToNotToCache(g1, chunkID);
        }
    }

    /**
     * Adds m to notToCache.
     *
     * @param m
     */
    public final void addToNotToCache(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) {
        Iterator<Grids_Grid> ite;
        ite = m.keySet().iterator();
        Grids_Grid g;
        while (ite.hasNext()) {
            g = ite.next();
            if (notToCache.containsKey(g)) {
                notToCache.get(g).addAll(m.get(g));
            } else {
                notToCache.put(g, m.get(g));
            }
        }
    }

    /**
     * Remove the chunkID of each grid in g[] from notToCache.
     *
     * @param g
     * @param chunkID
     */
    public final void removeFromNotToCache(
            Grids_Grid[] g,
            Grids_2D_ID_int chunkID) {
        for (Grids_Grid g1 : g) {
            removeFromNotToCache(g1, chunkID);
        }
    }

    /**
     * Removes m from notToCache.
     *
     * @param m
     */
    public final void removeFromNotToCache(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) {
        Iterator<Grids_Grid> ite;
        ite = m.keySet().iterator();
        Grids_Grid g;
        while (ite.hasNext()) {
            g = ite.next();
            if (notToCache.containsKey(g)) {
                notToCache.get(g).removeAll(m.get(g));
            }
        }
    }

    /**
     * Adds all the chunkIDs of g to notToCache.
     *
     * @param g
     * @param chunkIDs
     */
    public final void addToNotToCache(
            Grids_Grid g,
            Set<Grids_2D_ID_int> chunkIDs) {
        if (notToCache.containsKey(g)) {
            notToCache.get(g).addAll(chunkIDs);
        } else {
            notToCache.put(g, chunkIDs);
        }
    }

    /**
     * Remove all the chunkID of g to notToCache.
     *
     * @param g
     * @param chunkID
     */
    public final void removeFromNotToCache(
            Grids_Grid g,
            Grids_2D_ID_int chunkID) {
        if (notToCache.containsKey(g)) {
            /**
             * Decided that it is best not to remove g from NotToCache if
             * NotToCache.get(g).isEmpty(). So the empty Set remains and
             * this takes up a small amount of resource, but it is probably
             * better to keep it in case it is re-used rather than destroying
             * it.
             */
            notToCache.get(g).remove(chunkID);
//            Set<Grids_2D_ID_int> chunkIDs;
//            chunkIDs = notToCache.get(g);
//            chunkIDs.remove(chunkID);
//            if (chunkIDs.isEmpty()) {
//                notToCache.remove(g);
//            }
        }
    }

    /**
     * Adds all the ChunkIDs of g that are within cellDistance of the chunk with
     * ChunkID to notToCache.
     *
     * @param g The Grid.
     * @param chunkID Central ChunkID.
     * @param chunkRow The chunkRowIndex of chunkID - provided for convenience.
     * @param chunkCol The chunkColIndex of chunkID - provided for convenience.
     * @param chunkNRows The normal number of rows in a chunk. (Sometimes the
     * last row has fewer.)
     * @param chunkNCols The normal number of columns in a chunk. (Sometimes the
     * last column has fewer.)
     * @param cellDistance The distance over which we want to be sure to include
     * all chunks in notToCache.
     */
    public final void addToNotToCache(
            Grids_Grid g,
            Grids_2D_ID_int chunkID,
            int chunkRow,
            int chunkCol,
            int chunkNRows,
            int chunkNCols,
            int cellDistance) {
        Set<Grids_2D_ID_int> chunkIDs;
        if (notToCache.containsKey(g)) {
            chunkIDs = notToCache.get(g);
        } else {
            chunkIDs = new HashSet<>();
            notToCache.put(g, chunkIDs);
        }
        int t;
        int i = 0;
        t = 0;
        while (t < cellDistance) {
            t += chunkNRows;
            i++;
        }
        int j = 0;
        t = 0;
        while (t < cellDistance) {
            t += chunkNCols;
            j++;
        }
        int cr;
        int cc;
        int k;
        int l;
        for (k = -i; k <= i; k++) {
            cr = chunkRow + k;
            for (l = -j; l <= j; l++) {
                cc = chunkCol + l;
                if (g.isInGrid(cr, cc)) {
                    chunkIDs.add(new Grids_2D_ID_int(cr, cc));
                }
            }
        }
    }

    /**
     * Initialises grids.
     *
     * @param grids
     */
    protected void initGrids(
            Set<Grids_Grid> grids) {
        if (this.grids == null) {
            this.grids = grids;
        } else {
            //System.err.println(getClass().getName() + ".initGrids(Set)");
            if (grids == null) { // Debug
                this.grids = new HashSet<>();
            } else {
                this.grids = grids;
            }
        }
    }

    /**
     * @return the grids
     */
    public Set<Grids_Grid> getGrids() {
        return grids;
    }

    /**
     * Sets grids to be grids.
     *
     * @param grids
     * @param hoome
     * @throws java.io.IOException If encountered.
     */
    public void setGrids(Set<Grids_Grid> grids, boolean hoome)
            throws IOException, Exception {
        try {
            this.grids = grids;
            checkAndMaybeFreeMemory(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!cacheChunk()) {
                    throw e;
                }
                initMemoryReserve(env);
                setGrids(grids, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Adds {@code g} to {@link #grids}.
     *
     * @param g The grid to add to {@link #grids}.
     */
    public void addGrid(Grids_Grid g) {
        grids.add(g);
    }

    /**
     * Remove {@code g} from {@link #grids}.
     *
     * @param g The grid to remove from {@link #grids}.
     */
    public void removeGrid(Grids_Grid g) {
        grids.remove(g);
    }

    /**
     * Initialises grids and memory reserve.
     *
     * @param grids
     * @param hoome
     * @throws java.io.IOException If encountered.
     */
    public void initGridsAndMemoryReserve(Set<Grids_Grid> grids,
            boolean hoome) throws IOException, Exception {
        try {
            initGridsAndMemoryReserve(grids);
            checkAndMaybeFreeMemory(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!cacheChunk()) {
                    throw e;
                }
                initGridsAndMemoryReserve(grids, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises grids and memory reserve.
     *
     * @param grids
     */
    protected void initGridsAndMemoryReserve(
            Set<Grids_Grid> grids) {
        initGrids(grids);
        Iterator<Grids_Grid> ite = this.grids.iterator();
        if (ite.hasNext()) {
            ite.next().env.setMemoryReserve(MemoryReserve);
        } else {
            initMemoryReserve(env);
        }
    }

    /**
     * Initialises MemoryReserve. An account of swapping-out is returned.
     *
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_AccountDetail(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r
                        = cacheChunk_AccountDetail();
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr
                        = checkAndMaybeFreeMemory_AccountDetail(hoome);
                combine(r, pr);
                pr = initMemoryReserve_AccountDetail(hoome);
                combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. An account of cacheping is returned.
     *
     * @param hoome
     * @return
     * @throws java.io.IOException
     */
    @Override
    public long initMemoryReserve_Account(boolean hoome) throws IOException,
            Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_Account(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!cacheChunk()) {
                    throw e;
                }
                long r = 1;
                r += checkAndMaybeFreeMemory_Account(hoome);
                r += initMemoryReserve_Account(hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve and checks memory potentially swapping out data
     * other than any chunks in {@code g}.No account of any swapping out done is
     * returned.
     *
     * @param g A grid with chunks not to be swapped out in the event of any
     * memory handling.
     * @param hoome If true then OutOfMemoryErrors are caught, swapping out
     * operations are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public final void initMemoryReserve(Grids_Grid g, boolean hoome)
            throws IOException, Exception {
        try {
            initMemoryReserve(env);
            checkAndMaybeFreeMemory(g, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (cacheChunkExcept_Account(g) < 1L) {
                    throw e;
                }
                checkAndMaybeFreeMemory(g, hoome);
                initMemoryReserve(g, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve and checks memory potentially swapping out data
     * other than the chunk with {@code chunkID}. No account of any swapping out
     * done is returned.
     *
     * @param chunkID The identifier of a chunk not to swapped out in the event
     * of any memory handling.
     * @param hoome If true then OutOfMemoryErrors are caught, swapping out
     * operations are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public final void initMemoryReserve(Grids_2D_ID_int chunkID,
            boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            checkAndMaybeFreeMemory(chunkID, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (cacheChunkExcept_Account(chunkID) < 1L) {
                    throw e;
                }
                checkAndMaybeFreeMemory(chunkID, hoome);
                initMemoryReserve(chunkID, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve and checks memory potentially swapping out data
     * other than the chunk with {@code chunkID}. A detailed account of any
     * swapping out done is returned.
     *
     * @param chunkID The identifier of a chunk not to swapped out in the event
     * of any memory handling.
     * @param hoome If true then OutOfMemoryErrors are caught, swapping out
     * operations are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return A detailed account of any swapping out done.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(Grids_2D_ID_int chunkID,
                    boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_AccountDetail(chunkID, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(chunkID);
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> partResult;
                partResult = initMemoryReserve_AccountDetail(chunkID, hoome);
                combine(r, partResult);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this then no chunks are cached from grids that have chunkID. An account
     * of cacheping is returned.
     *
     * @param chunkID
     * @param hoome
     * @return
     */
    @Override
    public long initMemoryReserve_Account(Grids_2D_ID_int chunkID,
            boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_Account(chunkID, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long result = cacheChunkExcept_Account(chunkID);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(chunkID, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this then the chunk with chunkID in g is not cached.
     *
     * @param g
     * @param chunkID
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public final void initMemoryReserve(Grids_Grid g,
            Grids_2D_ID_int chunkID, boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            checkAndMaybeFreeMemory(g, chunkID, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (cacheChunkExcept_Account(g, chunkID) < 1L) {
                    throw e;
                }
                checkAndMaybeFreeMemory(g, chunkID, hoome);
                initMemoryReserve(g, chunkID, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this then the chunk with chunkID in g is not cached. An account of
     * cacheping is returned.
     *
     * @param g
     * @param chunkID
     * @param hoome
     * @return
     */
    @Override
    public long initMemoryReserve_Account(Grids_Grid g,
            Grids_2D_ID_int chunkID, boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_Account(g, chunkID, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = cacheChunkExcept_Account(g, chunkID);
                if (r < 1L) {
                    throw e;
                }
                r += checkAndMaybeFreeMemory_Account(g, chunkID, hoome);
                r += initMemoryReserve_Account(g, chunkID, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this then the chunk with chunkID in g is not cached. An account of
     * cacheping is returned.
     *
     * @param g
     * @param chunkID
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(Grids_Grid g,
                    Grids_2D_ID_int chunkID, boolean hoome) throws IOException,
            Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_AccountDetail(g, chunkID, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(g, chunkID);
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
                pr = checkAndMaybeFreeMemory_AccountDetail(g, chunkID, hoome);
                combine(r, pr);
                pr = initMemoryReserve_AccountDetail(g, chunkID, hoome);
                combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this then chunks with chunkID in chunkIDs in g are not cached and. An
     * account of cacheping is returned.
     *
     * @param g
     * @param chunkIDs
     * @param hoome
     * @return
     */
    @Override
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(Grids_Grid g,
                    Set<Grids_2D_ID_int> chunkIDs, boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_AccountDetail(g, chunkIDs, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(g, chunkIDs);
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
                pr = checkAndMaybeFreeMemory_AccountDetail(g, chunkIDs, hoome);
                combine(r, pr);
                pr = initMemoryReserve_AccountDetail(g, chunkIDs, hoome);
                combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this no chunks from g are cached. An account of cacheping is returned.
     *
     * @param g
     * @param hoome
     * @return
     */
    @Override
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(Grids_Grid g, boolean hoome)
            throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_AccountDetail(g, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(g);
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
                pr = checkAndMaybeFreeMemory_AccountDetail(g, hoome);
                combine(r, pr);
                pr = initMemoryReserve_AccountDetail(g, hoome);
                combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this no chunks given by m are cached. An account of cacheping is
     * returned.
     *
     * @param m
     * @param hoome
     * @return
     */
    @Override
    public long initMemoryReserve_Account(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m,
            boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_Account(m, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!cacheChunkExcept(m)) {
                    throw e;
                }
                long r = 1;
                r += checkAndMaybeFreeMemory_Account(m, hoome);
                r += initMemoryReserve_Account(m, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this no chunks from g are cached. An account of cacheping is returned.
     *
     * @param g
     * @param hoome
     * @return
     */
    @Override
    public long initMemoryReserve_Account(Grids_Grid g, boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_Account(g, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = cacheChunkExcept_Account(g);
                if (r < 1L) {
                    throw e;
                }
                r += checkAndMaybeFreeMemory_Account(g, hoome);
                r += initMemoryReserve_Account(g, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this no chunks with ChunkIDs in chunkIDs from g are cached. An account of
     * cacheping is returned.
     *
     * @param g
     * @param chunkIDs
     * @param hoome
     * @return
     */
    @Override
    public long initMemoryReserve_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> chunkIDs, boolean hoome)
            throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_Account(g, chunkIDs, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = cacheChunkExcept_Account(g, chunkIDs);
                if (r < 1L) {
                    throw e;
                }
                r += checkAndMaybeFreeMemory_Account(g, chunkIDs, hoome);
                r += initMemoryReserve_Account(g, chunkIDs, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this no chunks with ChunkIDs in chunkIDs from g are cached. An account of
     * cacheping is returned.
     *
     * @param g
     * @param chunkIDs
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public final void initMemoryReserve(Grids_Grid g,
            Set<Grids_2D_ID_int> chunkIDs,
            boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            checkAndMaybeFreeMemory(g, chunkIDs, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (cacheChunkExcept_Account(g, chunkIDs) < 1L) {
                    throw e;
                }
                checkAndMaybeFreeMemory(g, chunkIDs, hoome);
                initMemoryReserve(g, chunkIDs, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this no chunks given by m are cached.
     *
     * @param m
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public void initMemoryReserve(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m,
            boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            checkAndMaybeFreeMemory(m, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!cacheChunkExcept(m)) {
                    throw e;
                }
                checkAndMaybeFreeMemory(m, hoome);
                initMemoryReserve(m, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this no chunks given by m are cached.
     *
     * @param m
     * @param hoome
     * @return
     */
    @Override
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(
                    HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m,
                    boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_AccountDetail(m, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(m);
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
                pr = checkAndMaybeFreeMemory_AccountDetail(m, hoome);
                combine(r, pr);
                pr = initMemoryReserve_AccountDetail(m, hoome);
                combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. If available fast access memory is not low then this simply returns
     * true. If available fast access memory is low, then an attempt is made to
     * cache some chunks. Chunks in NotToCache are not cached unless desperate.
     * If not enough data is found to cache then an OutOfMemoryError is thrown.
     *
     * @param hoome
     * @return true if there is sufficient memory to continue and throws an
     * OutOfMemoryError otherwise.
     */
    @Override
    public boolean checkAndMaybeFreeMemory(boolean hoome) throws IOException, 
            Exception {
        try {
            if (checkAndMaybeFreeMemory()) {
                return true;
            } else {
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError("No more fast access memory that "
                        + "grids is using is avaialble to clear, try clearing "
                        + "data from other environments.");
            }
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!checkAndMaybeFreeMemory()) {
                    throw e;
                }
                initMemoryReserve(env);
                return true;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by clearing chunks
     * from memory. If available fast access memory is not low then this simply
     * returns true. If available fast access memory is low, then an attempt is
     * made to cache some chunks. Chunks in NotToCache are not cached unless
     * desperate.
     *
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    @Override
    public boolean checkAndMaybeFreeMemory() throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            if (notToCache.isEmpty()) {
                return checkAndMaybeFreeMemory_CacheAny();
            } else {
                do {
                    if (cacheChunkExcept(notToCache)) {
                        if (getTotalFreeMemory() < Memory_Threshold) {
                            return true;
                        }
                    } else {
                        break;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                return checkAndMaybeFreeMemory_CacheAny();
            }
        }
        return true;
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. If available fast access memory is not low then this simply returns
     * true. If available fast access memory is low, then an attempt is made to
     * cache some chunks. Chunks in NotToCache are not cached unless desperate.
     *
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    protected boolean checkAndMaybeFreeMemory_CacheAny() throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            do {
                if (!cacheChunk()) {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            return getTotalFreeMemory() < Memory_Threshold;
        } else {
            return true;
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. If available fast access memory is not low then this simply returns
     * true. If available fast access memory is low, then an attempt is made to
     * cache some chunks. Chunks in NotToCache are not cached unless desperate.
     * No chunk in g is cached. If not enough data is found to cache then an
     * OutOfMemoryError is thrown.
     *
     * @param g
     * @param hoome
     * @return true if there is sufficient memory to continue and throws an
     * OutOfMemoryError otherwise.
     */
    @Override
    public boolean checkAndMaybeFreeMemory(Grids_Grid g, boolean hoome)
            throws IOException, Exception {
        try {
            if (!checkAndMaybeFreeMemory(g)) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory(" + g.getClass().getName()
                        + ",boolean)";
//                System.out.println(message);
//                // Set to exit method with OutOfMemoryError
//                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return true;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!checkAndMaybeFreeMemory(g)) {
                    throw e;
                }
                initMemoryReserve(g, hoome);
                return true;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. If available fast access memory is not low then this simply returns
     * true. If available fast access memory is low, then an attempt is made to
     * cache some chunks. Chunks in NotToCache are not cached unless desperate.
     * No chunk in g is cached.
     *
     * @param g
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    protected boolean checkAndMaybeFreeMemory(Grids_Grid g) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            notToCache.put(g, g.getChunkIDs());
            do {
                if (!cacheChunkExcept(notToCache)) {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                if (!cacheChunkExcept(g)) {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            return getTotalFreeMemory() < Memory_Threshold;
        } else {
            return true;
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. If available fast access memory is not low then this simply returns
     * true. If available fast access memory is low, then an attempt is made to
     * cache some chunks. Chunks in NotToCache are not cached unless desperate.
     * No chunk with chunkId in g is cached. If there is not enough free memory
     * then an OutOfMemoryError is thrown.
     *
     * @param g
     * @param chunkID
     * @param hoome
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    @Override
    public boolean checkAndMaybeFreeMemory(
            Grids_Grid g,
            Grids_2D_ID_int chunkID,
            boolean hoome) throws IOException, Exception {
        try {
            if (!checkAndMaybeFreeMemory(g, chunkID)) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory(" + g.getClass().getName()
                        + ",Grids_2D_ID_int,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return true;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                boolean enough;
                enough = checkAndMaybeFreeMemory(g, chunkID);
                if (!enough) {
                    throw e;
                }
                initMemoryReserve(g, chunkID, hoome);
                return true;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. If available fast access memory is not low then this simply returns
     * true. If available fast access memory is low, then an attempt is made to
     * cache some chunks. Chunks in NotToCache are not cached unless desperate.
     * No chunk with chunkId in g is cached.
     *
     * @param g
     * @param chunkID
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    protected boolean checkAndMaybeFreeMemory(Grids_Grid g,
            Grids_2D_ID_int chunkID) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            addToNotToCache(g, chunkID);
            do {
                if (!cacheChunkExcept(notToCache)) {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                if (cacheChunkExcept_Account(g, chunkID) < 1) {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            return getTotalFreeMemory() < Memory_Threshold;
        } else {
            return true;
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. If available fast access memory is not low then this simply returns
     * true. If available fast access memory is low, then an attempt is made to
     * cache some chunks. Chunks in NotToCache are not cached unless desperate.
     * No chunk with chunkID is cached. If there is not enough free memory then
     * an OutOfMemoryError is thrown.
     *
     * @param chunkID
     * @param hoome
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    @Override
    public boolean checkAndMaybeFreeMemory(
            Grids_2D_ID_int chunkID,
            boolean hoome) throws IOException, Exception {
        try {
            if (!checkAndMaybeFreeMemory(chunkID)) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory(Grids_2D_ID_int,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return true;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                boolean enough = checkAndMaybeFreeMemory(chunkID);
                if (!enough) {
                    throw e;
                }
                initMemoryReserve(chunkID, hoome);
                return true;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. If available fast access memory is not low then this simply returns
     * true. If available fast access memory is low, then an attempt is made to
     * cache some chunks. Chunks in NotToCache are not cached unless desperate.
     * No chunk with chunkID is cached.
     *
     * @param chunkID
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    protected boolean checkAndMaybeFreeMemory(Grids_2D_ID_int chunkID)
            throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_Grid g;
            Iterator<Grids_Grid> ite;
            ite = grids.iterator();
            while (ite.hasNext()) {
                g = ite.next();
                addToNotToCache(g, chunkID);
                if (cacheChunkExcept(notToCache)) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        return true;
                    }
                }
            }
            ite = grids.iterator();
            while (ite.hasNext()) {
                if (cacheChunkExcept(chunkID)) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        return true;
                    }
                }
            }
            return getTotalFreeMemory() < Memory_Threshold;
        } else {
            return true;
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. No data is cached as identified by m and no data is cached from
     * NotToCache unless desperate. If not enough data is found to cache then an
     * OutOfMemoryError is thrown.
     *
     * @param m
     * @param hoome
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    @Override
    public boolean checkAndMaybeFreeMemory(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m,
            boolean hoome) throws IOException, Exception {
        try {
            if (!checkAndMaybeFreeMemory(m)) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory(" + m.getClass().getName()
                        + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return true;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                boolean enough = checkAndMaybeFreeMemory(m);
                if (!enough) {
                    throw e;
                }
                initMemoryReserve(m, hoome);
                return true;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. No data is cached as identified by m and no data is cached from
     * NotToCache unless desperate. If no data is found to cache then false is
     * returned otherwise true is returned.
     *
     * @param m
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    protected boolean checkAndMaybeFreeMemory(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            addToNotToCache(m);
            do {
                if (!cacheChunkExcept(notToCache)) {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                if (cacheChunkExcept_Account(m) < 1) {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            return getTotalFreeMemory() < Memory_Threshold;
        } else {
            return true;
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. No data is cached as identified by m and no data is cached from
     * NotToCache unless desperate. If no data is found to cache then false is
     * returned otherwise true is returned.
     *
     * @param g
     * @param hoome
     * @param chunkIDs
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    @Override
    public boolean checkAndMaybeFreeMemory(Grids_Grid g,
            Set<Grids_2D_ID_int> chunkIDs,
            boolean hoome) throws IOException, Exception {
        try {
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (cacheChunkExcept_Account(g, chunkIDs) < 1) {
                    System.out.println(
                            "Warning! Nothing to cache in "
                            + this.getClass().getName()
                            + ".checkAndMaybeFreeMemory(" + g.getClass().getName()
                            + ",Set<ChunkID>,boolean)");
                    // Set to exit method with OutOfMemoryError
                    hoome = false;
                    throw new OutOfMemoryError();
                }
            }
            return true;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                boolean createdRoom = false;
                while (!createdRoom) {
                    if (cacheChunkExcept_Account(g, chunkIDs) < 1L) {
                        System.out.println(
                                "Warning! Nothing to cache in "
                                + this.getClass().getName()
                                + ".checkAndMaybeFreeMemory(" + g.getClass().getName()
                                + ",Set<ChunkID>,boolean) after encountering "
                                + "an OutOfMemoryError");
                        throw e;
                    }
                    initMemoryReserve(g, chunkIDs, hoome);
                    checkAndMaybeFreeMemory(g, chunkIDs, hoome);
                    //createdRoom = true;
                    return true;
                }
            } else {
                throw e;
            }
        }
        return false;
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. No chunks are cached from g that have ChunkIDs in chunkIDs. If no
     * data is found to cache then false is returned otherwise true is returned.
     *
     * @param g
     * @param chunkIDs
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    protected boolean checkAndMaybeFreeMemory(Grids_Grid g,
            Set<Grids_2D_ID_int> chunkIDs) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            addToNotToCache(g, chunkIDs);
            do {
                if (cacheChunkExcept(notToCache)) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        return true;
                    }
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                if (cacheChunkExcept_Account(g, chunkIDs) < 1) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        return true;
                    }
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            return getTotalFreeMemory() < Memory_Threshold;
        } else {
            return true;
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. This method may throw
     * an OutOfMemoryError if there is not enough data to cache in Grids.
     *
     * @param hoome
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    @Override
    public long checkAndMaybeFreeMemory_Account(
            boolean hoome) throws IOException, Exception {
        try {
            Account test = checkAndMaybeFreeMemory_Account();
            if (test == null) {
                return 0;
            }
            if (!test.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account(boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = checkAndMaybeFreeMemory_Account(hoome);
                r += initMemoryReserve_Account(hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file.
     *
     * @return Account of data cached.
     */
    protected Account checkAndMaybeFreeMemory_Account() throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            do {
                if (cacheChunkExcept(notToCache)) {
                    result.detail++;
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                result.success = true;
            } else {
                do {
                    if (cacheChunk()) {
                        result.detail++;
                    } else {
                        break;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                result.success = getTotalFreeMemory() < Memory_Threshold;
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. This method may throw
     * an OutOfMemoryError if there is not enough data to cache in Grids. No
     * data is cached from g.
     *
     * @param g
     * @param hoome
     * @return Number of chunks cached.
     */
    @Override
    public long checkAndMaybeFreeMemory_Account(Grids_Grid g,
            boolean hoome) throws IOException, Exception {
        try {
            Account test = checkAndMaybeFreeMemory_Account(g);
            if (test == null) {
                return 0;
            }
            if (!test.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account(" + g.getClass().getName()
                        + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = checkAndMaybeFreeMemory_Account(g, hoome);
                r += initMemoryReserve_Account(g, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. This method may throw
     * an OutOfMemoryError if there is not enough data to cache in Grids. No
     * data is cached from g.
     *
     * @param g
     * @return Account of data cached.
     */
    protected Account checkAndMaybeFreeMemory_Account(Grids_Grid g)
            throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account r = new Account();
            addToNotToCache(g);
            do {
                if (cacheChunkExcept(notToCache)) {
                    r.detail++;
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                r.success = true;
            } else {
                do {
                    if (cacheChunkExcept(g)) {
                        r.detail++;
                    } else {
                        break;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                r.success = getTotalFreeMemory() < Memory_Threshold;
            }
            return r;
        }
        return null;
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. This method may throw
     * an OutOfMemoryError if there is not enough data to cache in Grids. The
     * Chunk with chunkID from g is not cached.
     *
     * @param g
     * @param hoome
     * @param chunkID
     * @return Number of chunks cached.
     */
    @Override
    public long checkAndMaybeFreeMemory_Account(Grids_Grid g,
            Grids_2D_ID_int chunkID, boolean hoome) throws IOException, Exception {
        try {
            Account r = checkAndMaybeFreeMemory_Account(g, chunkID);
            if (r == null) {
                return 0;
            }
            if (!r.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account("
                        + g.getClass().getName() + ","
                        + chunkID.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return r.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = checkAndMaybeFreeMemory_Account(g, chunkID, hoome);
                r += initMemoryReserve_Account(g, chunkID, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. This method may throw
     * an OutOfMemoryError if there is not enough data to cache in Grids. The
     * Chunk with chunkID from g is not cached.
     *
     * @param g
     * @param chunkID
     * @return Account of data cached.
     */
    public Account checkAndMaybeFreeMemory_Account(Grids_Grid g,
            Grids_2D_ID_int chunkID) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account r = new Account();
            addToNotToCache(g, chunkID);
            do {
                if (cacheChunkExcept(notToCache)) {
                    r.detail++;
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                r.success = true;
            } else {
                do {
                    long caches = cacheChunkExcept_Account(g, chunkID);
                    if (caches < 1L) {
                        break;
                    } else {
                        r.detail += caches;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                r.success = getTotalFreeMemory() < Memory_Threshold;
            }
            return r;
        }
        return null;
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. This method may throw
     * an OutOfMemoryError if there is not enough data to cache in Grids. No
     * Chunk with chunkID is not cached.
     *
     * @param chunkID
     * @param hoome
     * @return Number of chunks cached.
     */
    @Override
    public long checkAndMaybeFreeMemory_Account(Grids_2D_ID_int chunkID,
            boolean hoome) throws IOException, Exception {
        try {
            Account r = checkAndMaybeFreeMemory_Account(chunkID);
            if (r == null) {
                return 0;
            }
            if (!r.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account("
                        + chunkID.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return r.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = checkAndMaybeFreeMemory_Account(chunkID, hoome);
                r += initMemoryReserve_Account(chunkID, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. No Chunk with chunkID
     * is not cached.
     *
     * @param chunkID
     * @return Account of data cached.
     */
    public Account checkAndMaybeFreeMemory_Account(Grids_2D_ID_int chunkID) 
            throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account r = new Account();
            Iterator<Grids_Grid> ite = grids.iterator();
            while (ite.hasNext()) {
                Grids_Grid g = ite.next();
                addToNotToCache(g, chunkID);
                long cache = cacheChunkExcept_Account(notToCache);
                r.detail += cache;
                if (cache > 0L) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            }
            ite = grids.iterator();
            while (ite.hasNext()) {
                Grids_Grid g = ite.next();
                long cache;
                cache = cacheChunkExcept_Account(g, chunkID);
                r.detail += cache;
                if (cache > 0L) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            }
        }
        return null;
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. No data is cached as
     * identified by m.
     *
     * @param m
     * @param hoome
     * @return Number of chunks cached.
     */
    @Override
    public long checkAndMaybeFreeMemory_Account(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m,
            boolean hoome) throws IOException, Exception {
        try {
            Account test = checkAndMaybeFreeMemory_Account(m);
            if (test == null) {
                return 0;
            }
            if (!test.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account("
                        + m.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = checkAndMaybeFreeMemory_Account(m, hoome);
                r += initMemoryReserve_Account(m, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. No data is cached as
     * identified by m.
     *
     * @param m
     * @return Account of data cached.
     */
    public Account checkAndMaybeFreeMemory_Account(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account r = new Account();
            addToNotToCache(m);
            do {
                if (cacheChunkExcept(notToCache)) {
                    r.detail++;
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                r.success = true;
            } else {
                do {
                    long caches = cacheChunkExcept_Account(m);
                    if (caches < 1L) {
                        break;
                    } else {
                        r.detail += caches;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                r.success = getTotalFreeMemory() < Memory_Threshold;
            }
            return r;
        }
        return null;
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. No data is cached as
     * identified by m. No data is cached from chunks in g.
     *
     * @param g
     * @param hoome
     * @param chunks
     * @return Number of chunks cached.
     */
    @Override
    public long checkAndMaybeFreeMemory_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> chunks, boolean hoome) throws IOException, Exception {
        try {
            Account test = checkAndMaybeFreeMemory_Account(g, chunks);
            if (test == null) {
                return 0;
            }
            if (!test.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account("
                        + g.getClass().getName() + ","
                        + chunks.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                return freeSomeMemoryAndResetReserve_Account(g, chunks, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. No data is cached as
     * identified by m. No data is cached from chunks in g.
     *
     * @param g
     * @param chunkIDs
     * @return Account of data cached.
     */
    public Account checkAndMaybeFreeMemory_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> chunkIDs) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account r = new Account();
            addToNotToCache(g, chunkIDs);
            do {
                if (cacheChunkExcept(notToCache)) {
                    r.detail++;
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                r.success = true;
            } else {
                do {
                    long caches = cacheChunkExcept_Account(g, chunkIDs);
                    if (caches < 1L) {
                        break;
                    } else {
                        r.detail += caches;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                r.success = getTotalFreeMemory() < Memory_Threshold;
            }
            return r;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. An attempt at
     * Grids internal memory handling is performed if an OutOfMemoryError is
     * encountered and hoome is true. This method may throw an OutOfMemoryError
     * if there is no grid chunk to cache in Grids.
     *
     * @param hoome
     * @return A map of the grid chunks cached.
     */
    @Override
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(boolean hoome) throws IOException, Exception {
        try {
            AccountDetail test = checkAndMaybeFreeMemory_AccountDetail();
            if (test == null) {
                return null;
            }
            boolean test0 = test.success;
            if (!test0) {
                String message;
                message = "Warning! Not enough data to cache in "
                        + getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + "boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                return freeSomeMemoryAndResetReserve_AccountDetails(hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue.
     *
     * @return
     */
    protected AccountDetail checkAndMaybeFreeMemory_AccountDetail() throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail r = new AccountDetail();
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
            do {
                pr = cacheChunkExcept_AccountDetail(notToCache);
                if (pr.isEmpty()) {
                    break;
                } else {
                    combine(r.detail, pr);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                pr = cacheChunk_AccountDetail();
                if (pr.isEmpty()) {
                    break;
                } else {
                    combine(r.detail, pr);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            r.success = false;
            return r;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. An attempt at Grids internal memory handling is
     * performed if an OutOfMemoryError is encountered and hoome is true. This
     * method may throw an OutOfMemoryError if there is not enough data to cache
     * in Grids. No data is cached from g.
     *
     * @param g
     * @param hoome
     * @return HashMap identifying chunks cached.
     */
    @Override
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(
                    Grids_Grid g, boolean hoome) throws IOException, Exception {
        try {
            AccountDetail test;
            test = checkAndMaybeFreeMemory_AccountDetail(g);
            if (test == null) {
                return null;
            }
            if (!test.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + g.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                return freeSomeMemoryAndResetReserve_AccountDetails(g, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. No data is cached from g.
     *
     * @param g
     * @return
     */
    protected AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            Grids_Grid g) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            addToNotToCache(g);
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> partResult;
            do {
                partResult = cacheChunkExcept_AccountDetail(notToCache);
                if (partResult.isEmpty()) {
                    break;
                } else {
                    combine(result.detail, partResult);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.success = true;
                        return result;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                partResult = cacheChunkExcept_AccountDetail(g);
                if (partResult.isEmpty()) {
                    break;
                } else {
                    combine(result.detail, partResult);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.success = true;
                        return result;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            result.success = false;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. The Chunk with chunkID from g is not cached.
     *
     * @param g
     * @param hoome
     * @param chunkID
     * @return HashMap identifying chunks cached.
     */
    @Override
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(
                    Grids_Grid g,
                    Grids_2D_ID_int chunkID,
                    boolean hoome) throws IOException, Exception {
        try {
            AccountDetail test;
            test = checkAndMaybeFreeMemory_AccountDetail(g, chunkID);
            if (test == null) {
                return null;
            }
            if (!test.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + g.getClass().getName() + ","
                        + chunkID.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
                r = checkAndMaybeFreeMemory_AccountDetail(g, chunkID, hoome);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
                pr = initMemoryReserve_AccountDetail(g, chunkID, hoome);
                combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. The Chunk with chunkID from g is not cached.
     *
     * @param g
     * @param chunkID
     * @return
     */
    protected AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            Grids_Grid g, Grids_2D_ID_int chunkID) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail r = new AccountDetail();
            addToNotToCache(g, chunkID);
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
            do {
                pr = cacheChunkExcept_AccountDetail(notToCache);
                if (pr.isEmpty()) {
                    break;
                } else {
                    combine(r.detail, pr);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                pr = cacheChunkExcept_AccountDetail(g, chunkID);
                if (pr.isEmpty()) {
                    break;
                } else {
                    combine(r.detail, pr);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            r.success = false;
            return r;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. The Chunk with chunkID from g is not cached. No data
     * is cached with chunkID.
     *
     * @param chunkID
     * @param hoome
     * @return HashMap identifying chunks cached.
     */
    @Override
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(Grids_2D_ID_int chunkID,
                    boolean hoome) throws IOException, Exception {
        try {
            AccountDetail r = checkAndMaybeFreeMemory_AccountDetail(chunkID);
            if (r == null) {
                return null;
            }
            boolean resultPart0 = r.success;
            if (!resultPart0) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + chunkID.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return r.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
                r = checkAndMaybeFreeMemory_AccountDetail(chunkID, hoome);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
                pr = initMemoryReserve_AccountDetail(chunkID, hoome);
                combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. The Chunk with chunkID from g is not cached. No data
     * is cached with chunkID.
     *
     * @param chunkID
     * @return
     */
    protected AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            Grids_2D_ID_int chunkID) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail r = new AccountDetail();
            Iterator<Grids_Grid> ite = grids.iterator();
            while (ite.hasNext()) {
                Grids_Grid g = ite.next();
                addToNotToCache(g, chunkID);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> partResult;
                do {
                    partResult = cacheChunkExcept_AccountDetail(notToCache);
                    if (partResult.isEmpty()) {
                        break;
                    } else {
                        combine(r.detail, partResult);
                        if (getTotalFreeMemory() < Memory_Threshold) {
                            r.success = true;
                            return r;
                        }
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
            }
            ite = grids.iterator();
            while (ite.hasNext()) {
                Grids_Grid g = ite.next();
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
                do {
                    pr = cacheChunkExcept_AccountDetail(g, chunkID);
                    if (pr.isEmpty()) {
                        break;
                    } else {
                        combine(r.detail, pr);
                        if (getTotalFreeMemory() < Memory_Threshold) {
                            r.success = true;
                            return r;
                        }
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
            }
            r.success = false;
            return r;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. The Chunk with chunkID from g is not cached. No data
     * is cached as identified by m.
     *
     * @param m Identifies data not to be cached.
     * @param hoome If true then if an OutOfMemoryError is encountered then an
     * attempt is made to handle this otherwise not and the error is thrown.
     * @return HashMap identifying chunks cached or null if nothing is cached.
     */
    @Override
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(
                    HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m,
                    boolean hoome) throws IOException, Exception {
        try {
            AccountDetail test;
            test = checkAndMaybeFreeMemory_AccountDetail(m);
            if (test == null) {
                return null;
            }
            if (test.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + m.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
                r = checkAndMaybeFreeMemory_AccountDetail(m, hoome);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
                pr = initMemoryReserve_AccountDetail(m, hoome);
                combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. The Chunk with chunkID from g is not cached. No data
     * is cached as identified by m.
     *
     * @param m Identifies data not to be cached.
     * @return HashMap identifying chunks cached or null if nothing is cached.
     */
    protected AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail r = new AccountDetail();
            addToNotToCache(m);
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
            do {
                pr = cacheChunkExcept_AccountDetail(notToCache);
                if (pr.isEmpty()) {
                    break;
                } else {
                    combine(r.detail, pr);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                pr = cacheChunkExcept_AccountDetail(m);
                if (pr.isEmpty()) {
                    break;
                } else {
                    combine(r.detail, pr);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            r.success = false;
            return r;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. The Chunk with chunkID from g is not cached. No
     * chunks with ChunkID in chunkIDs are cached from g.
     *
     * @param g
     * @param hoome
     * @param chunkIDs
     * @return HashMap identifying chunks cached.
     */
    @Override
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(
                    Grids_Grid g,
                    Set<Grids_2D_ID_int> chunkIDs,
                    boolean hoome) throws IOException, Exception {
        try {
            AccountDetail test;
            test = checkAndMaybeFreeMemory_AccountDetail(g, chunkIDs);
            if (test == null) {
                return null;
            }
            if (!test.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + g.getClass().getName() + ","
                        + chunkIDs.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                return freeSomeMemoryAndResetReserve_AccountDetails(g, chunkIDs, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. The Chunk with chunkID from g is not cached. No
     * chunks with ChunkID in chunkIDs are cached from g.
     *
     * @param g
     * @param chunkIDs
     * @return
     */
    protected AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            Grids_Grid g,
            Set<Grids_2D_ID_int> chunkIDs) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail r = new AccountDetail();
            addToNotToCache(g, chunkIDs);
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
            do {
                pr = cacheChunkExcept_AccountDetail(notToCache);
                if (pr.isEmpty()) {
                    break;
                } else {
                    combine(r.detail, pr);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                pr = cacheChunkExcept_AccountDetail(g, chunkIDs);
                if (pr.isEmpty()) {
                    break;
                } else {
                    combine(r.detail, pr);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            r.success = false;
            return r;
        }
        return null;
    }

    /**
     * Attempts to cache all chunks in grids.
     *
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then cache operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunks_AccountDetail(boolean hoome) throws IOException, Exception,
            Exception {
        try {
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r
                    = cacheChunks_AccountDetail(notToCache);
            try {
                if (r.isEmpty()) {
                    AccountDetail account
                            = checkAndMaybeFreeMemory_AccountDetail();
                    if (account != null) {
                        if (account.success) {
                            combine(r, account.detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr
                            = checkAndMaybeFreeMemory_AccountDetail(hoome);
                    combine(r, pr);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                freeSomeMemoryAndResetReserve_AccountDetails(e, hoome);
                return cacheChunks_AccountDetail(hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to cache all Grids_Chunk in this.grids.
     *
     * @return
     */
    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunks_AccountDetail() throws IOException, Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r = new HashMap<>();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            combine(r, ite.next().cacheChunks_AccountDetail());
        }
        return r;
    }

    /**
     * Attempts to cache all Grids_Chunk in this.grids.
     *
     * @param m
     * @return
     */
    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunks_AccountDetail(
                    HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m)
            throws IOException, Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r = new HashMap<>();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr
                    = g.cacheChunksExcept_AccountDetail(m.get(g));
            combine(r, pr);
        }
        return r;
    }

    /**
     * Attempts to cache all chunks in env.
     *
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then cache operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return A count of the number of chunks cached.
     */
    public long cacheChunks_Account(boolean hoome) throws IOException,
            Exception {
        try {
            long r;
            try {
                r = cacheChunks_Account();
                if (r < 1) {
                    Account account = checkAndMaybeFreeMemory_Account();
                    if (account != null) {
                        if (account.success) {
                            r += account.detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r += checkAndMaybeFreeMemory_Account(hoome);
                }
            } catch (OutOfMemoryError e) {
                /**
                 * Set hoome = false to exit method by throwing OutOfMemoryError
                 */
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = freeSomeMemoryAndResetReserve_Account(e, hoome);
                r += cacheChunks_Account(hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to cache all chunks in env.
     *
     * @return
     */
    protected long cacheChunks_Account() throws IOException, Exception {
        long r = 0L;
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            r += ite.next().cacheChunks_Account();
        }
        dataToCache = false;
        return r;
    }

    /**
     * Attempts to cache all Grids_Chunk in this.grids.
     *
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then cache operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public void cacheChunks(boolean hoome) throws IOException, Exception {
        try {
            boolean success = cacheChunks();
            try {
                if (!success) {
                    checkAndMaybeFreeMemory();
                } else {
                    checkAndMaybeFreeMemory(hoome);
                }
            } catch (OutOfMemoryError e) {
                /**
                 * Set hoome = false to exit method by throwing OutOfMemoryError
                 */
                hoome = false;
                throw e;
            }
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!cacheChunk()) {
                    throw e;
                }
                initMemoryReserve(env);
                cacheChunks();
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to cache all Grids_Chunk in grids.
     *
     * @return
     */
    protected boolean cacheChunks() throws IOException, Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            ite.next().cacheChunks();
        }
        dataToCache = false;
        return true;
    }

    /**
     * Attempts to cache any Grids_AbstractGridChunk in this.Grids. This is the
     * lowest level of OutOfMemoryError handling in this class.
     *
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached.
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then cache operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunk_AccountDetail(boolean hoome) throws IOException,
            Exception {
        try {
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r
                    = cacheChunk_AccountDetail();
            try {
                if (r.isEmpty()) {
                    AccountDetail account
                            = checkAndMaybeFreeMemory_AccountDetail();
                    if (account != null) {
                        if (account.success) {
                            combine(r, account.detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    combine(r, checkAndMaybeFreeMemory_AccountDetail(hoome));
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                return freeSomeMemoryAndResetReserve_AccountDetails(e, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to cache any chunk in grids trying first not to cache any in
     * notToCache.
     *
     * @param hoome
     * @return
     */
    public boolean cacheChunk(boolean hoome) throws IOException, Exception {
        try {
            boolean success = cacheChunk();
            try {
                if (!success) {
                    Account account = checkAndMaybeFreeMemory_Account();
                    if (account != null) {
                        if (!account.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    checkAndMaybeFreeMemory(hoome);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return true;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!cacheChunk()) {
                    throw e;
                }
                initMemoryReserve(env);
                // No need for recursive call: cacheChunk(hoome);
                return true;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to cache any chunk in grids trying first not to cache any in
     * notToCache.
     *
     * @return
     */
    protected boolean cacheChunk() throws IOException, Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        Grids_Grid g;
        while (ite.hasNext()) {
            g = ite.next();
            if (cacheChunkExcept(notToCache)) {
                return true;
            }
            if (g.cacheChunk()) {
                return true;
            }
        }
        dataToCache = false;
        return false;
    }

    /**
     * Cache to File any GridChunk in grids except one in g.
     *
     * @param g
     * @param hoome
     */
    public void cacheChunkExcept(Grids_Grid g, boolean hoome)
            throws IOException, Exception {
        try {
            boolean success = cacheChunkExcept(g);
            try {
                if (!success) {
                    Account account = checkAndMaybeFreeMemory_Account(g);
                    if (account != null) {
                        if (!account.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    checkAndMaybeFreeMemory(g, hoome);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (cacheChunkExcept_Account(g) < 1L) {
                    throw e;
                }
                initMemoryReserve(g, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Cache to File any GridChunk in grids except one in g.
     *
     * @param g
     * @return
     */
    protected boolean cacheChunkExcept(Grids_Grid g) throws IOException,
            Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid bg = ite.next();
            if (bg != g) {
                if (bg.cacheChunk()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Modifies toGetCombined by adding all mapping from toCombine if they have
     * new keys. If they don't then this adds the contents of the values
     *
     * @param toGetCombined
     * @param toCombine
     */
    public void combine(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> toGetCombined,
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> toCombine) {
        if (toCombine != null) {
            if (!toCombine.isEmpty()) {
                Set<Grids_Grid> toGetCombinedKS = toGetCombined.keySet();
                Set<Grids_Grid> toCombineKS = toCombine.keySet();
                Iterator<Grids_Grid> ite = toCombineKS.iterator();
                Grids_Grid g;
                while (ite.hasNext()) {
                    g = ite.next();
                    if (toGetCombinedKS.contains(g)) {
                        toGetCombined.get(g).addAll(toCombine.get(g));
                    } else {
                        toGetCombined.put(g, toCombine.get(g));
                    }
                }
            }
        }
    }

    /**
     * Attempts to cache any Grids_Chunk in this.grids.
     *
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached.
     */
    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunk_AccountDetail() throws IOException, Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            if (notToCache.containsKey(g)) {
                Set<Grids_2D_ID_int> chunkIDs;
                chunkIDs = notToCache.get(g);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r
                        = g.cacheChunkExcept_AccountDetail(chunkIDs);
                if (!r.isEmpty()) {
                    return r;
                }
            }
        }
        dataToCache = false;
        return null;
    }

    /**
     * @param hoome
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached. Attempts to
     * cache any Grids_Chunk in this.grids except for those in with
     * Grids_Grid.ID = _ChunkID.
     * @param chunkID The Grids_Grid.ID not to be cached.
     */
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_2D_ID_int chunkID,
                    boolean hoome) throws IOException, Exception {
        try {
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
            r = cacheChunkExcept_AccountDetail(chunkID);
            try {
                if (r.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail(chunkID);
                    if (account != null) {
                        if (account.success) {
                            combine(r, account.detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
                    pr = checkAndMaybeFreeMemory_AccountDetail(chunkID, hoome);
                    combine(r, pr);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(chunkID);
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
                pr = initMemoryReserve_AccountDetail(chunkID, hoome);
                combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached. Attempts to
     * cache any Grids_Chunk in this.grids except for those in with
     * Grids_Grid.ID = _ChunkID.
     * @param chunkID The Grids_Grid.ID not to be cached.
     */
    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_2D_ID_int chunkID)
            throws IOException, Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r
                    = g.cacheChunkExcept_AccountDetail(chunkID);
            if (!r.isEmpty()) {
                Set<Grids_2D_ID_int> chunkIDs = new HashSet<>(1);
                chunkIDs.add(chunkID);
                r.put(g, chunkIDs);
                return r;
            }
        }
        return null;
    }

    /**
     *
     * @param chunkID
     * @param hoome
     * @return
     */
    public long cacheChunkExcept_Account(Grids_2D_ID_int chunkID, boolean hoome)
            throws IOException, Exception {
        try {
            long r = cacheChunkExcept_Account(chunkID);
            try {
                if (r < 1) {
                    Account account = checkAndMaybeFreeMemory_Account(chunkID);
                    if (account != null) {
                        if (account.success) {
                            r += account.detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r += checkAndMaybeFreeMemory_Account(chunkID, hoome);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            r += checkAndMaybeFreeMemory_Account(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = cacheChunkExcept_Account(chunkID);
                if (r < 1L) {
                    throw e;
                }
                r += initMemoryReserve_Account(chunkID, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkID The id of the GridChunk not to be cached.
     * @return
     */
    protected long cacheChunkExcept_Account(Grids_2D_ID_int chunkID)
            throws IOException, Exception {
        long r = 0L;
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            addToNotToCache(g, chunkID);
            if (!cacheChunkExcept(notToCache)) {
                r += cacheChunkExcept_Account(chunkID);
            } else {
                r += 1L;
            }
            if (r > 0L) {
                return r;
            }
        }
        return r;
    }

    /**
     * @param chunkID The ID of the chunk not to be cached.
     * @return
     */
    protected boolean cacheChunkExcept(Grids_2D_ID_int chunkID) throws IOException, Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            if (cacheChunkExcept_Account(g, chunkID) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param hoome
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached. Attempts to
     * cache any Grids_Chunk in this.grids except for those in
     * _Grid2DSquareCell_ChunkIDSet.
     * @param m HashMap with Grids_Grid as keys and a respective Set of
     * Grids_Grid.ChunkIDs as values. Collectively these identifying those
     * chunks not to be cached from the Grids_Grid.
     */
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(
                    HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m,
                    boolean hoome) throws IOException, Exception {
        try {
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r
                    = cacheChunkExcept_AccountDetail(m);
            try {
                if (r.isEmpty()) {
                    AccountDetail account
                            = checkAndMaybeFreeMemory_AccountDetail(m);
                    if (account != null) {
                        if (account.success) {
                            combine(r, account.detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    combine(r, checkAndMaybeFreeMemory_AccountDetail(m, hoome));
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(m);
                if (r.isEmpty()) {
                    throw e;
                }
                initMemoryReserve(m, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * @param m
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached. Attempts to
     * cache any Grids_Chunk in this.grids except for those in
     * _Grid2DSquareCell_ChunkIDSet.
     */
    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(
                    HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) throws IOException, Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
        r = new HashMap<>(1);
        Iterator<Grids_Grid> ite = grids.iterator();
        Set<Grids_2D_ID_int> s = new HashSet<>(1);
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            if (m.containsKey(g)) {
                Set<Grids_2D_ID_int> chunkIDs = m.get(g);
                chunkID = g.cacheChunkExcept_AccountChunk(chunkIDs);
                if (chunkID != null) {
                    s.add(chunkID);
                    r.put(g, s);
                    return r;
                }
            }
            chunkID = g.cacheChunk_AccountChunk();
            if (chunkID != null) {
                s.add(chunkID);
                r.put(g, s);
                return r;
            }
        }
        return r; // If here then nothing could be cached!
    }

    /**
     *
     * @param g
     * @param chunkIDs
     * @return
     */
    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_Grid g,
                    Set<Grids_2D_ID_int> chunkIDs) throws IOException, Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r
                = new HashMap<>(1);
        Iterator<Grids_Grid> ite = grids.iterator();
        Set<Grids_2D_ID_int> rp = new HashSet<>(1);
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (g == gb) {
                Grids_2D_ID_int chunkID = gb.cacheChunkExcept_AccountChunk(
                        chunkIDs);
                if (chunkID != null) {
                    rp.add(chunkID);
                    r.put(g, rp);
                    return r;
                }
            } else {
                Grids_2D_ID_int chunkID = g.cacheChunk_AccountChunk();
                if (chunkID != null) {
                    rp.add(chunkID);
                    r.put(g, rp);
                    return r;
                }
            }
        }
        return r; // If here then nothing could be cached!
    }

    /**
     *
     * @param g
     * @param chunkID
     * @return
     */
    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_Grid g,
                    Grids_2D_ID_int chunkID) throws IOException, Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r
                = new HashMap<>(1);
        Iterator<Grids_Grid> ite = grids.iterator();
        Set<Grids_2D_ID_int> rp = new HashSet<>(1);
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (g == gb) {
                Grids_2D_ID_int chunkIDb = gb.cacheChunkExcept_AccountChunk(
                        chunkID);
                if (chunkIDb != null) {
                    rp.add(chunkIDb);
                    r.put(g, rp);
                    return r;
                }
            } else {
                Grids_2D_ID_int chunkIDb = g.cacheChunk_AccountChunk();
                if (chunkIDb != null) {
                    rp.add(chunkIDb);
                    r.put(g, rp);
                    return r;
                }
            }
        }
        return r; // If here then nothing could be cached!
    }

    /**
     *
     * @param g
     * @param chunkID
     * @param hoome
     * @return
     */
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_Grid g,
                    Grids_2D_ID_int chunkID, boolean hoome) throws IOException,
            Exception {
        try {
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r
                    = cacheChunkExcept_AccountDetail(g, chunkID);
            return r;
        } catch (java.lang.OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> rp;
                rp = cacheChunkExcept_AccountDetail(g, chunkID);
                if (rp.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r
                        = initMemoryReserve_AccountDetail(g, chunkID, hoome);
                combine(r, rp);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     *
     * @param g
     * @return
     */
    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_Grid g) throws IOException,
            Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r
                = new HashMap<>(1);
        Iterator<Grids_Grid> ite = grids.iterator();
        Set<Grids_2D_ID_int> rp = new HashSet<>(1);
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (g != gb) {
                Grids_2D_ID_int chunkID = gb.cacheChunk_AccountChunk();
                if (chunkID != null) {
                    rp.add(chunkID);
                    r.put(g, rp);
                    return r;
                }
            }
        }
        return r; // If here then nothing could be cached!
    }

    /**
     *
     * @param m
     * @return
     */
    protected long cacheChunkExcept_Account(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) throws IOException,
            Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            if (m.containsKey(g)) {
                Set<Grids_2D_ID_int> chunkIDs = m.get(g);
                Grids_2D_ID_int chunkID = g.cacheChunkExcept_AccountChunk(
                        chunkIDs);
                if (chunkID != null) {
                    return 1L;
                }
            }
            Grids_2D_ID_int chunkID = g.cacheChunk_AccountChunk();
            if (chunkID != null) {
                return 1L;
            }
        }
        return 0L; // If here then nothing could be cached!
    }

    protected boolean cacheChunkExcept(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) throws IOException,
            Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            if (m.containsKey(g)) {
                Set<Grids_2D_ID_int> chunkIDs = m.get(g);
                if (g.cacheChunkExcept_AccountChunk(chunkIDs) != null) {
                    return true;
                }
            }
            if (g.cacheChunk_AccountChunk() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param hoome
     * @param chunkIDs
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached. Attempts to
     * cache any Grids_Chunk in this.grids except for those in g with ChunkIDs
     * in chunkIDs.
     * @param g Grids_Grid that's chunks are not to be cached.
     */
    public long cacheChunkExcept_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> chunkIDs, boolean hoome)
            throws IOException, Exception {
        try {
            long r = cacheChunkExcept_Account(g, chunkIDs);
            try {
                if (r < 1) {
                    Account account
                            = checkAndMaybeFreeMemory_Account(g, chunkIDs);
                    if (account != null) {
                        if (account.success) {
                            r += account.detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r += checkAndMaybeFreeMemory_Account(g, chunkIDs, hoome);
                }
            } catch (OutOfMemoryError e) {
                /**
                 * Set hoome = false to exit method by throwing OutOfMemoryError
                 */
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = cacheChunkExcept_Account(g, chunkIDs);
                if (r < 1L) {
                    throw e;
                }
                r += initMemoryReserve_Account(g, chunkIDs, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkIDs
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached. Attempts to
     * cache any Grids_Chunk in this.grids except for those in g with ChunkIDs
     * in chunkIDs.
     * @param g Grids_Grid that's chunks are not to be cached.
     */
    protected long cacheChunkExcept_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> chunkIDs) throws IOException, Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (gb != g) {
                TreeMap<Grids_2D_ID_int, Grids_Chunk> m
                        = gb.getChunkIDChunkMap();
                Set<Grids_2D_ID_int> chunkIDsb = m.keySet();
                Iterator<Grids_2D_ID_int> iteb = chunkIDsb.iterator();
                while (iteb.hasNext()) {
                    Grids_2D_ID_int chunkID = iteb.next();
                    if (!chunkIDs.contains(chunkID)) {
                        //Check it can be cached
                        if (m.get(chunkID) != null) {
                            gb.cacheChunk(chunkID);
                            return 1;
                        }
                    }
                }
            }
        }
        return 0L;
    }

    /**
     * @param hoome
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached. Attempts to
     * cache any Grids_Chunk in this.grids except for that in _Grid2DSquareCell
     * with Grids_Grid._ChunkID _ChunkID.
     * @param g Grids_Grid that's chunks are not to be cached.
     * @param chunkID The Grids_Grid.ID not to be cached.
     */
    public long cacheChunkExcept_Account(Grids_Grid g,
            Grids_2D_ID_int chunkID, boolean hoome) throws IOException, Exception {
        try {
            long r = cacheChunkExcept_Account(g, chunkID);
            try {
                if (r < 1) {
                    Account account;
                    account = checkAndMaybeFreeMemory_Account(g, chunkID);
                    if (account != null) {
                        if (account.success) {
                            r += account.detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r += checkAndMaybeFreeMemory_Account(g, chunkID, hoome);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = cacheChunkExcept_Account(g, chunkID);
                if (r < 1L) {
                    throw e;
                }
                r += initMemoryReserve_Account(g, chunkID, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached. Attempts to
     * cache any Grids_Chunk in this.grids except for that in _Grid2DSquareCell
     * with Grids_Grid._ChunkID _ChunkID.
     * @param g Grids_Grid that's chunks are not to be cached.
     * @param chunkID The Grids_Grid.ID not to be cached.
     */
    protected long cacheChunkExcept_Account(Grids_Grid g,
            Grids_2D_ID_int chunkID) throws IOException, Exception {
        long r = cacheChunkExcept_Account(g);
        if (r < 1L) {
            r = g.cacheChunkExcept_Account(chunkID);
        }
        return r;
    }

    /**
     * @param hoome
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached. Attempts to
     * cache any Grids_Chunk in this.grids except for those in g.
     * @param g Grids_Grid that's chunks are not to be cached.
     */
    public long cacheChunkExcept_Account(Grids_Grid g,
            boolean hoome) throws IOException, Exception {
        try {
            long r = cacheChunkExcept_Account(g);
            try {
                if (r < 1) {
                    Account account = checkAndMaybeFreeMemory_Account(g);
                    if (account != null) {
                        if (account.success) {
                            r += account.detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r += checkAndMaybeFreeMemory_Account(g, hoome);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = cacheChunkExcept_Account(g);
                if (r < 1L) {
                    throw e;
                }
                r += initMemoryReserve_Account(g, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached. Attempts to
     * cache any Grids_Chunk in this.grids except for those in
     * _Grid2DSquareCell.
     * @param g Grids_Grid that's chunks are not to be cached.
     */
    protected long cacheChunkExcept_Account(Grids_Grid g) throws IOException, Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (gb != g) {
                Grids_2D_ID_int chunkID = gb.cacheChunk_AccountChunk();
                if (chunkID != null) {
                    return 1L;
                }
            }
        }
        return 0L;
    }

    /**
     * Attempts to Cache all Grids_Grid.ChunkIDs in this.grids except those with
     * Grids_Grid.ID _ChunkID.
     *
     * @param hoome
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached.
     * @param chunkID The Grids_Grid.ID not to be cached.
     */
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_2D_ID_int chunkID,
                    boolean hoome) throws IOException, Exception {
        try {
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
            r = cacheChunksExcept_AccountDetail(chunkID);
            try {
                if (r.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail(chunkID);
                    if (account != null) {
                        if (account.success) {
                            combine(r, account.detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_Grid, Set<Grids_2D_ID_int>> rp;
                    rp = checkAndMaybeFreeMemory_AccountDetail(chunkID, hoome);
                    combine(r, rp);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(chunkID);
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> rp;
                rp = initMemoryReserve_AccountDetail(chunkID, hoome);
                combine(r, rp);
                rp = cacheChunksExcept_AccountDetail(chunkID, hoome);
                combine(r, rp);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to Cache all Grids_Grid.ChunkIDs in this.grids except those with
     * Grids_Grid.ID _ChunkID.
     *
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached.
     * @param chunkID The Grids_Grid.ID not to be cached.
     */
    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_2D_ID_int chunkID) throws IOException, Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
        r = new HashMap<>();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            combine(r, g.cacheChunksExcept_AccountDetail(chunkID));
        }
        return r;
    }

    /**
     * Attempts to Cache all Grids_Grid.ChunkIDs in this.grids except those with
     * Grids_Grid.ID _ChunkID.
     *
     * @param hoome
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached.
     * @param g Grids_Grid that's chunks are not to be cached. cached.
     */
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_Grid g,
                    boolean hoome) throws IOException, Exception {
        try {
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
            r = cacheChunksExcept_AccountDetail(g);
            try {
                if (r.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail(g);
                    if (account != null) {
                        if (account.success) {
                            combine(r, account.detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_Grid, Set<Grids_2D_ID_int>> rp;
                    rp = checkAndMaybeFreeMemory_AccountDetail(g, hoome);
                    combine(r, rp);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(g);
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> rp;
                rp = initMemoryReserve_AccountDetail(g, hoome);
                combine(r, rp);
                rp = cacheChunksExcept_AccountDetail(g, hoome);
                combine(r, rp);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to Cache all Grids_Grid.ChunkIDs in this.grids except those with
     * Grids_Grid.ID _ChunkID.
     *
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached.
     * @param g Grids_Grid that's chunks are not to be cached.
     */
    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_Grid g) throws IOException, Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
        r = new HashMap<>();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (gb != g) {
                combine(r, gb.cacheChunks_AccountDetail());
            }
        }
        return r;
    }

    public long cacheChunksExcept_Account(Grids_Grid g, boolean hoome) throws IOException, Exception {
        try {
            long r = cacheChunksExcept_Account(g);
            try {
                if (r < 1) {
                    Account account = checkAndMaybeFreeMemory_Account(g);
                    if (account != null) {
                        if (account.success) {
                            r += account.detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r += checkAndMaybeFreeMemory_Account(g, hoome);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = cacheChunkExcept_Account(g);
                if (r < 1L) {
                    throw e;
                }
                r += initMemoryReserve_Account(g, hoome);
                r += cacheChunksExcept_Account(g, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    protected long cacheChunksExcept_Account(Grids_Grid g) throws IOException, Exception {
        long r = 0L;
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (gb != g) {
                r += gb.env.cacheChunks_Account();
            }
        }
        return r;
    }

    /**
     * Attempts to Cache all Grids_Grid.ChunkIDs in this.grids except those with
     * Grids_Grid.ID _ChunkID.
     *
     * @param hoome
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached.
     * @param g Grids_Grid that's chunks are not to be cached.
     * @param chunkID The Grids_Grid.ID not to be cached.
     */
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_Grid g,
                    Grids_2D_ID_int chunkID, boolean hoome) throws IOException, Exception {
        try {
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
            r = cacheChunksExcept_AccountDetail(g, chunkID);
            try {
                if (r.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail(g, chunkID);
                    if (account != null) {
                        if (account.success) {
                            combine(r, account.detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_Grid, Set<Grids_2D_ID_int>> rp;
                    rp = checkAndMaybeFreeMemory_AccountDetail(g, chunkID, hoome);
                    combine(r, rp);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(g, chunkID);
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> rp;
                rp = initMemoryReserve_AccountDetail(g, chunkID, hoome);
                combine(r, rp);
                rp = cacheChunksExcept_AccountDetail(g, chunkID, hoome);
                combine(r, rp);
                return r;
            } else {
                throw e;
            }
        }
    }

    public long cacheChunksExcept_Account(Grids_Grid g,
            Grids_2D_ID_int chunkID, boolean hoome) throws IOException, Exception {
        try {
            long r = cacheChunksExcept_Account(g, chunkID);
            try {
                if (r < 1) {
                    Account account;
                    account = checkAndMaybeFreeMemory_Account(g, chunkID);
                    if (account != null) {
                        if (account.success) {
                            r += account.detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r += checkAndMaybeFreeMemory_Account(g, chunkID, hoome);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                long r = cacheChunkExcept_Account(g, chunkID);
                if (r < 1L) {
                    throw e;
                }
                r += initMemoryReserve_Account(chunkID, hoome);
                r += cacheChunkExcept_Account(g, chunkID);
                return r;
            } else {
                throw e;
            }
        }
    }

    protected long cacheChunksExcept_Account(Grids_Grid g,
            Grids_2D_ID_int chunkID) throws IOException, Exception {
        long r = 0L;
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (gb != g) {
                int cri0 = 0;
                int cci0 = 0;
                int cri1 = gb.getNChunkRows() - 1;
                int cci1 = gb.getNChunkCols() - 1;
                r += gb.cacheChunks_Account(cri0, cci0, cri1, cci1);
            } else {
                r += gb.cacheChunksExcept_Account(chunkID);
            }
        }
        return r;
    }

    /**
     * Attempts to Cache all Grids_Grid.ChunkIDs in this.grids except those with
     * Grids_Grid.ID _ChunkID.
     *
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached.
     * @param g Grids_Grid that's chunks are not to be cached.
     * @param chunkID The Grids_Grid.ID not to be cached.
     */
    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_Grid g,
                    Grids_2D_ID_int chunkID) throws IOException, Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
        r = new HashMap<>();
        Iterator<Grids_Grid> ite = grids.iterator();
        Grids_Grid bg;
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> rp;
        while (ite.hasNext()) {
            bg = ite.next();
            if (bg == g) {
                rp = bg.cacheChunksExcept_AccountDetail(chunkID);
                combine(r, rp);
            } else {
                rp = bg.cacheChunks_AccountDetail(false, HOOMEF);
                combine(r, rp);
            }
        }
        return r;
    }

    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_Grid g,
                    Set<Grids_2D_ID_int> chunkIDs) throws IOException, Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r = new HashMap<>();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (gb != g) {
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr
                        = gb.cacheChunks_AccountDetail();
                combine(r, pr);
            } else {
                Set<Grids_2D_ID_int> chunks = g.getChunkIDs();
                Iterator<Grids_2D_ID_int> ite2 = chunks.iterator();
                while (ite2.hasNext()) {
                    Grids_2D_ID_int i = ite2.next();
                    if (!chunkIDs.contains(i)) {
                        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> pr;
                        pr = cacheChunksExcept_AccountDetail(i);
                        combine(r, pr);
                    }
                }
            }
        }
        return r;
    }

    /**
     * Attempts to Cache all chunks except those in g with Chunk IDs in
     * chunkIDs.
     *
     * @return HashMap with: key as the Grids_Grid from which the Grids_Chunk
     * was cached; and, value as the Grids_Chunk._ChunkID cached.
     * @param g Grids_Grid that's chunks are not to be cached.
     * @param chunkIDs The chunk IDs in g not to be cached.
     */
    protected long cacheChunksExcept_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> chunkIDs) throws IOException, Exception {
        long r = 0L;
        Iterator<Grids_Grid> ite;
        ite = grids.iterator();
        Grids_Grid gb;
        while (ite.hasNext()) {
            gb = ite.next();
            if (gb != g) {
                int cri0 = 0;
                int cri1 = gb.getNChunkRows() - 1;
                int cci0 = 0;
                int cci1 = gb.getNChunkCols() - 1;
                r += gb.cacheChunks_Account(cri0, cci0, cri1, cci1);
            } else {
                r += gb.cacheChunksExcept_Account(chunkIDs);
            }
        }
        return r;
    }

    /**
     * @param m
     * @param hoome
     * @return
     */
    public long cacheChunksExcept_Account(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m,
            boolean hoome) throws IOException, Exception {
        try {
            long r = cacheChunksExcept_Account(m);
            try {
                if (r < 1) {
                    Account account = checkAndMaybeFreeMemory_Account(m);
                    if (account != null) {
                        if (account.success) {
                            r += account.detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r += checkAndMaybeFreeMemory_Account(m, hoome);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!cacheChunkExcept(m)) {
                    throw e;
                }
                long r = 1L;
                r += initMemoryReserve_Account(m, hoome);
                r += cacheChunksExcept_Account(m);
                return r;
            } else {
                throw e;
            }
        }
    }

    protected long cacheChunksExcept_Account(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) throws IOException, Exception {
        long r = 0L;
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            r += g.cacheChunksExcept_Account(m.get(g));
        }
        return r;
    }

    public void cacheData() throws IOException, Exception {
        cacheChunks();
    }

    public void cacheData(boolean hoome) throws IOException, Exception {
        cacheChunks();
    }

    @Override
    public boolean cacheDataAny(boolean hoome) throws IOException, Exception {
        try {
            boolean r = cacheChunk();
            try {
                if (!checkAndMaybeFreeMemory()) {
                    throw new OutOfMemoryError();
                }
            } catch (OutOfMemoryError e) {
                // Exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                boolean r = cacheDataAny();
                initMemoryReserve(env);
                return r;
            } else {
                throw e;
            }
        }
    }

    @Override
    public boolean cacheDataAny() throws IOException, Exception {
        return cacheChunk();
    }

    private boolean dataToCache = true;

    public boolean isDataToCache() {
        return dataToCache;
    }

    public void setDataToCache(boolean dataToCache) {
        this.dataToCache = dataToCache;
    }

    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(Grids_Grid g,
                    boolean hoome) throws IOException, Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
        r = checkAndMaybeFreeMemory_AccountDetail(g, hoome);
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> rp;
        rp = initMemoryReserve_AccountDetail(g, hoome);
        combine(r, rp);
        return r;
    }

    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    Grids_Grid g, Set<Grids_2D_ID_int> chunks,
                    boolean hoome) throws IOException, Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
        r = checkAndMaybeFreeMemory_AccountDetail(g, chunks, hoome);
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> rp;
        rp = initMemoryReserve_AccountDetail(g, chunks, hoome);
        combine(r, rp);
        return r;
    }

    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    OutOfMemoryError e, boolean hoome) throws IOException, Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
        r = cacheChunk_AccountDetail();
        if (r.isEmpty()) {
            throw e;
        }
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> rp;
        rp = initMemoryReserve_AccountDetail(hoome);
        combine(r, rp);
        return r;
    }

    protected HashMap<Grids_Grid, Set<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(boolean hoome) throws IOException, Exception {
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> r;
        r = checkAndMaybeFreeMemory_AccountDetail(hoome);
        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> rp;
        rp = initMemoryReserve_AccountDetail(hoome);
        combine(r, rp);
        return r;
    }

    protected long freeSomeMemoryAndResetReserve_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> chunks, boolean hoome) throws IOException, Exception {
        long r = checkAndMaybeFreeMemory_Account(g, chunks, hoome);
        r += initMemoryReserve_Account(g, chunks, hoome);
        return r;
    }

    protected long freeSomeMemoryAndResetReserve_Account(OutOfMemoryError e,
            boolean hoome) throws IOException, Exception {
        if (!cacheChunk()) {
            throw e;
        }
        long r = 1;
        r += initMemoryReserve_Account(hoome);
        return r;
    }

    /**
     * @return the notToCache
     */
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>> getNotToCache() {
        return notToCache;
    }

    /**
     * Simple inner class for accounting memory cacheping detail.
     */
    protected class AccountDetail {

        HashMap<Grids_Grid, Set<Grids_2D_ID_int>> detail;
        boolean success;

        protected AccountDetail() {
            detail = new HashMap<>();
            success = false;
        }
    }

    /**
     * Simple inner class for accounting memory cacheping.
     */
    protected class Account {

        long detail;
        boolean success;

        protected Account() {
            detail = 0;
            success = false;
        }
    }

}
