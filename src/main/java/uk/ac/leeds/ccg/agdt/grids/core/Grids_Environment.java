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
package uk.ac.leeds.ccg.agdt.grids.core;

import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_AbstractGrid;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import uk.ac.leeds.ccg.agdt.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.agdt.math.Math_BigDecimal;
import uk.ac.leeds.ccg.agdt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.agdt.grids.process.Grids_Processor;

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
     * A HashSet of Grids_AbstractGrid objects that may have data that can be
     * cached to release memory for processing.
     */
    protected transient HashSet<Grids_AbstractGrid> grids;

    /**
     * For indicating which chunks are not cached to release memory for
     * processing unless desperate.
     */
    protected transient HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> notToCache;

    /**
     * For storing an instance of Math_BigDecimal.
     */
    public transient Math_BigDecimal bd;

    /**
     * For storing a Grids_Processor.
     */
    protected transient Grids_Processor processor;

    /**
     * For storing an instance of Grids_Files.
     */
    public transient Grids_Files files;

    /**
     * For storing an instance of Generic_Environment.
     */
    public transient final Generic_Environment env;

    /**
     * Defaults Generic_Environment to: {@code new Generic_Environment()).
     * {@link #Grids_Environment(Generic_Environment)}
     *
     * @throws IOException
     */
    public Grids_Environment() throws IOException {
        this(new Generic_Environment());
    }

    /**
     * Defaults dir to: {@link Grids_Files.getDefaultDir()).
     * {@link #Grids_Environment(Generic_Environment,File)}
     *
     * @param env The default.
     */
    public Grids_Environment(Generic_Environment env) throws IOException {
        this(env, Grids_Files.getDefaultDir());
    }

    /**
     *
     * @param env What {@link #env} is set to.
     * @param dir Used to initialise {@link #files} using
     * {@link Grids_Files(File)}.
     */
    public Grids_Environment(Generic_Environment env, File dir) throws IOException {
        this.env = env;
        initMemoryReserve(Default_Memory_Threshold);
        initGrids();
        initNotToCache();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        files = new Grids_Files(dir);
    }

    /**
     * @return the processor initialising first if it is null.
     */
    public Grids_Processor getProcessor() {
        if (processor == null) {
            try {
                processor = new Grids_Processor(this);
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
        return processor;
    }

    /**
     * @param processor
     */
    public void setProcessor(Grids_Processor processor) {
        this.processor = processor;
    }

    /**
     * Initialises grids.
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
    public final void addToNotToCache(Grids_AbstractGrid g) {
        notToCache.put(g, g.getChunkIDs());
    }

    /**
     * Removes g from the notToCache.
     *
     * @param g
     */
    public final void removeFromNotToCache(Grids_AbstractGrid g) {
        notToCache.remove(g);
    }

    /**
     * Adds the chunkID of g to notToCache.
     *
     * @param g
     * @param chunkRow
     */
    public final void addToNotToCache(Grids_AbstractGrid g, int chunkRow) {
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
    public final void removeFromNotToCache(Grids_AbstractGrid g, int chunkRow) {
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
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        if (notToCache.containsKey(g)) {
            notToCache.get(g).add(chunkID);
        } else {
            HashSet<Grids_2D_ID_int> chunkIDs;
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
            Grids_AbstractGrid[] g,
            Grids_2D_ID_int chunkID) {
        for (Grids_AbstractGrid g1 : g) {
            addToNotToCache(g1, chunkID);
        }
    }

    /**
     * Adds m to notToCache.
     *
     * @param m
     */
    public final void addToNotToCache(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        Iterator<Grids_AbstractGrid> ite;
        ite = m.keySet().iterator();
        Grids_AbstractGrid g;
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
            Grids_AbstractGrid[] g,
            Grids_2D_ID_int chunkID) {
        for (Grids_AbstractGrid g1 : g) {
            removeFromNotToCache(g1, chunkID);
        }
    }

    /**
     * Removes m from notToCache.
     *
     * @param m
     */
    public final void removeFromNotToCache(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        Iterator<Grids_AbstractGrid> ite;
        ite = m.keySet().iterator();
        Grids_AbstractGrid g;
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
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
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
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        if (notToCache.containsKey(g)) {
            /**
             * Decided that it is best not to remove g from NotToCache if
             * NotToCache.get(g).isEmpty(). So the empty HashSet remains and
             * this takes up a small amount of resource, but it is probably
             * better to keep it in case it is re-used rather than destroying
             * it.
             */
            notToCache.get(g).remove(chunkID);
//            HashSet<Grids_2D_ID_int> chunkIDs;
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
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            int chunkRow,
            int chunkCol,
            int chunkNRows,
            int chunkNCols,
            int cellDistance) {
        HashSet<Grids_2D_ID_int> chunkIDs;
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
            HashSet<Grids_AbstractGrid> grids) {
        if (this.grids == null) {
            this.grids = grids;
        } else {
            //System.err.println(getClass().getName() + ".initGrids(HashSet)");
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
    public HashSet<Grids_AbstractGrid> getGrids() {
        return grids;
    }

    /**
     * Sets grids to be grids.
     *
     * @param grids
     * @param hoome
     */
    public void setGrids(
            HashSet<Grids_AbstractGrid> grids,
            boolean hoome) {
        try {
            this.grids = grids;
            checkAndMaybeFreeMemory(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                if (!cacheChunk()) {
                    throw e;
                }
                initMemoryReserve();
                setGrids(grids, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Adds g to grids.
     *
     * @param g
     */
    public void addGrid(Grids_AbstractGrid g) {
        grids.add(g);
    }

    /**
     * Remove g from grids.
     *
     * @param g
     */
    public void removeGrid(Grids_AbstractGrid g) {
        grids.remove(g);
    }

    /**
     * Initialises grids and memory reserve.
     *
     * @param grids
     * @param hoome
     */
    public void initGridsAndMemoryReserve(HashSet<Grids_AbstractGrid> grids,
            boolean hoome) {
        try {
            initGridsAndMemoryReserve(grids);
            checkAndMaybeFreeMemory(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
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
            HashSet<Grids_AbstractGrid> grids) {
        initGrids(grids);
        Iterator<Grids_AbstractGrid> ite = this.grids.iterator();
        if (ite.hasNext()) {
            ite.next().env.setMemoryReserve(MemoryReserve);
        } else {
            initMemoryReserve();
        }
    }

    /**
     * Initialises MemoryReserve. An account of cacheping is returned.
     *
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(boolean hoome) {
        try {
            initMemoryReserve();
            return checkAndMaybeFreeMemory_AccountDetail(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = cacheChunk_AccountDetail();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = checkAndMaybeFreeMemory_AccountDetail(hoome);
                combine(result, partResult);
                partResult = initMemoryReserve_AccountDetail(hoome);
                combine(result, partResult);
                return result;
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
     */
    @Override
    public long initMemoryReserve_Account(
            boolean hoome) {
        try {
            initMemoryReserve();
            return checkAndMaybeFreeMemory_Account(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                if (!cacheChunk()) {
                    throw e;
                }
                long result = 1;
                result += checkAndMaybeFreeMemory_Account(hoome);
                result += initMemoryReserve_Account(hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this then no chunks are cached from g. An account of cacheping is
     * returned.
     *
     * @param g
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public final void initMemoryReserve(
            Grids_AbstractGrid g,
            boolean hoome) {
        try {
            initMemoryReserve();
            checkAndMaybeFreeMemory(g, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
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
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this then no chunks are cached from grids that have chunkID. An account
     * of cacheping is returned.
     *
     * @param chunkID
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public final void initMemoryReserve(
            Grids_2D_ID_int chunkID,
            boolean hoome) {
        try {
            initMemoryReserve();
            checkAndMaybeFreeMemory(chunkID, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
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
     * Initialises MemoryReserve. If any cacheping has to be done in order to do
     * this then no chunks are cached from grids that have chunkID. An account
     * of cacheping is returned.
     *
     * @param chunkID
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(
                    Grids_2D_ID_int chunkID,
                    boolean hoome) {
        try {
            initMemoryReserve();
            return checkAndMaybeFreeMemory_AccountDetail(
                    chunkID, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = cacheChunkExcept_AccountDetail(chunkID);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = initMemoryReserve_AccountDetail(chunkID, hoome);
                combine(result, partResult);
                return result;
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
    public long initMemoryReserve_Account(
            Grids_2D_ID_int chunkID,
            boolean hoome) {
        try {
            initMemoryReserve();
            return checkAndMaybeFreeMemory_Account(chunkID, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
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
    public final void initMemoryReserve(Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID, boolean hoome) {
        try {
            initMemoryReserve();
            checkAndMaybeFreeMemory(g, chunkID, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
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
    public long initMemoryReserve_Account(Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID, boolean hoome) {
        try {
            initMemoryReserve();
            return checkAndMaybeFreeMemory_Account(g, chunkID, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                long result = cacheChunkExcept_Account(g, chunkID);
                if (result < 1L) {
                    throw e;
                }
                result += checkAndMaybeFreeMemory_Account(g, chunkID, hoome);
                result += initMemoryReserve_Account(g, chunkID, hoome);
                return result;
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
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(
                    Grids_AbstractGrid g,
                    Grids_2D_ID_int chunkID,
                    boolean hoome) {
        try {
            initMemoryReserve();
            return checkAndMaybeFreeMemory_AccountDetail(g, chunkID, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = cacheChunkExcept_AccountDetail(g, chunkID);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = checkAndMaybeFreeMemory_AccountDetail(g, chunkID, hoome);
                combine(result, partResult);
                partResult = initMemoryReserve_AccountDetail(g, chunkID, hoome);
                combine(result, partResult);
                return result;
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
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(
                    Grids_AbstractGrid g,
                    HashSet<Grids_2D_ID_int> chunkIDs,
                    boolean hoome) {
        try {
            initMemoryReserve();
            return checkAndMaybeFreeMemory_AccountDetail(g, chunkIDs, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = cacheChunkExcept_AccountDetail(g, chunkIDs);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = checkAndMaybeFreeMemory_AccountDetail(g, chunkIDs, hoome);
                combine(result, partResult);
                partResult = initMemoryReserve_AccountDetail(g, chunkIDs, hoome);
                combine(result, partResult);
                return result;
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
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(Grids_AbstractGrid g, boolean hoome) {
        try {
            initMemoryReserve();
            return checkAndMaybeFreeMemory_AccountDetail(g, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = cacheChunkExcept_AccountDetail(g);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = checkAndMaybeFreeMemory_AccountDetail(g, hoome);
                combine(result, partResult);
                partResult = initMemoryReserve_AccountDetail(g, hoome);
                combine(result, partResult);
                return result;
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
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
            boolean hoome) {
        try {
            initMemoryReserve();
            return checkAndMaybeFreeMemory_Account(m, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                if (!cacheChunkExcept(m)) {
                    throw e;
                }
                long result = 1;
                result += checkAndMaybeFreeMemory_Account(m, hoome);
                result += initMemoryReserve_Account(m, hoome);
                return result;
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
    public long initMemoryReserve_Account(
            Grids_AbstractGrid g,
            boolean hoome) {
        try {
            initMemoryReserve();
            return checkAndMaybeFreeMemory_Account(g, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                long result = cacheChunkExcept_Account(g);
                if (result < 1L) {
                    throw e;
                }
                result += checkAndMaybeFreeMemory_Account(g, hoome);
                result += initMemoryReserve_Account(g, hoome);
                return result;
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
    public long initMemoryReserve_Account(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs,
            boolean hoome) {
        try {
            initMemoryReserve();
            return checkAndMaybeFreeMemory_Account(g, chunkIDs, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                long result = cacheChunkExcept_Account(g, chunkIDs);
                if (result < 1L) {
                    throw e;
                }
                result += checkAndMaybeFreeMemory_Account(g, chunkIDs, hoome);
                result += initMemoryReserve_Account(g, chunkIDs, hoome);
                return result;
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
    public final void initMemoryReserve(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs,
            boolean hoome) {
        try {
            initMemoryReserve();
            checkAndMaybeFreeMemory(g, chunkIDs, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
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
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
            boolean hoome) {
        try {
            initMemoryReserve();
            checkAndMaybeFreeMemory(m, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
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
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
                    boolean hoome) {
        try {
            initMemoryReserve();
            return checkAndMaybeFreeMemory_AccountDetail(m, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = cacheChunkExcept_AccountDetail(m);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = checkAndMaybeFreeMemory_AccountDetail(m, hoome);
                combine(result, partResult);
                partResult = initMemoryReserve_AccountDetail(m, hoome);
                combine(result, partResult);
                return result;
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
    public boolean checkAndMaybeFreeMemory(boolean hoome) {
        try {
            if (checkAndMaybeFreeMemory()) {
                return true;
            } else {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory(boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                if (!checkAndMaybeFreeMemory()) {
                    throw e;
                }
                initMemoryReserve();
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
     *
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    @Override
    public boolean checkAndMaybeFreeMemory() {
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

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. If available fast access memory is not low then this simply returns
     * true. If available fast access memory is low, then an attempt is made to
     * cache some chunks. Chunks in NotToCache are not cached unless desperate.
     *
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    protected boolean checkAndMaybeFreeMemory_CacheAny() {
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
    public boolean checkAndMaybeFreeMemory(
            Grids_AbstractGrid g,
            boolean hoome) {
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
                clearMemoryReserve();
                boolean isEnoughMemoryToContinue;
                isEnoughMemoryToContinue = checkAndMaybeFreeMemory(g);
                if (!isEnoughMemoryToContinue) {
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
    protected boolean checkAndMaybeFreeMemory(Grids_AbstractGrid g) {
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
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean hoome) {
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
                clearMemoryReserve();
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
    protected boolean checkAndMaybeFreeMemory(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
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
            boolean hoome) {
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
                clearMemoryReserve();
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
    protected boolean checkAndMaybeFreeMemory(
            Grids_2D_ID_int chunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_AbstractGrid g;
            Iterator<Grids_AbstractGrid> ite;
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
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
            boolean hoome) {
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
                clearMemoryReserve();
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
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
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
    public boolean checkAndMaybeFreeMemory(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs,
            boolean hoome) {
        try {
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (cacheChunkExcept_Account(g, chunkIDs) < 1) {
                    System.out.println(
                            "Warning! Nothing to cache in "
                            + this.getClass().getName()
                            + ".checkAndMaybeFreeMemory(" + g.getClass().getName()
                            + ",HashSet<ChunkID>,boolean)");
                    // Set to exit method with OutOfMemoryError
                    hoome = false;
                    throw new OutOfMemoryError();
                }
            }
            return true;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                boolean createdRoom = false;
                while (!createdRoom) {
                    if (cacheChunkExcept_Account(g, chunkIDs) < 1L) {
                        System.out.println(
                                "Warning! Nothing to cache in "
                                + this.getClass().getName()
                                + ".checkAndMaybeFreeMemory(" + g.getClass().getName()
                                + ",HashSet<ChunkID>,boolean) after encountering "
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
    protected boolean checkAndMaybeFreeMemory(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
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
            boolean hoome) {
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
                clearMemoryReserve();
                long result;
                result = checkAndMaybeFreeMemory_Account(hoome);
                result += initMemoryReserve_Account(hoome);
                return result;
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
    protected Account checkAndMaybeFreeMemory_Account() {
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
    public long checkAndMaybeFreeMemory_Account(
            Grids_AbstractGrid g,
            boolean hoome) {
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
                clearMemoryReserve();
                long result = checkAndMaybeFreeMemory_Account(
                        g, hoome);
                result += initMemoryReserve_Account(g, hoome);
                return result;
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
    protected Account checkAndMaybeFreeMemory_Account(
            Grids_AbstractGrid g) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            addToNotToCache(g);
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
                    if (cacheChunkExcept(g)) {
                        result.detail++;
                    } else {
                        break;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                result.success = getTotalFreeMemory() < Memory_Threshold;
            }
            return result;
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
    public long checkAndMaybeFreeMemory_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean hoome) {
        try {
            Account test;
            test = checkAndMaybeFreeMemory_Account(g, chunkID);
            if (test == null) {
                return 0;
            }
            if (!test.success) {
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
            return test.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                long result = checkAndMaybeFreeMemory_Account(g, chunkID, hoome);
                result += initMemoryReserve_Account(g, chunkID, hoome);
                return result;
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
    public Account checkAndMaybeFreeMemory_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            addToNotToCache(g, chunkID);
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
                    long caches = cacheChunkExcept_Account(g, chunkID);
                    if (caches < 1L) {
                        break;
                    } else {
                        result.detail += caches;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                result.success = getTotalFreeMemory() < Memory_Threshold;
            }
            return result;
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
    public long checkAndMaybeFreeMemory_Account(
            Grids_2D_ID_int chunkID,
            boolean hoome) {
        try {
            Account test = checkAndMaybeFreeMemory_Account(chunkID);
            if (test == null) {
                return 0;
            }
            if (!test.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account("
                        + chunkID.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                long result = checkAndMaybeFreeMemory_Account(chunkID, hoome);
                result += initMemoryReserve_Account(chunkID, hoome);
                return result;
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
    public Account checkAndMaybeFreeMemory_Account(
            Grids_2D_ID_int chunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            Grids_AbstractGrid g;
            Iterator<Grids_AbstractGrid> ite;
            ite = grids.iterator();
            while (ite.hasNext()) {
                g = ite.next();
                addToNotToCache(g, chunkID);
                long cache;
                cache = cacheChunkExcept_Account(notToCache);
                result.detail += cache;
                if (cache > 0L) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.success = true;
                        return result;
                    }
                }
            }
            ite = grids.iterator();
            while (ite.hasNext()) {
                g = ite.next();
                long cache;
                cache = cacheChunkExcept_Account(g, chunkID);
                result.detail += cache;
                if (cache > 0L) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.success = true;
                        return result;
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
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
            boolean hoome) {
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
                clearMemoryReserve();
                long result = checkAndMaybeFreeMemory_Account(m, hoome);
                result += initMemoryReserve_Account(m, hoome);
                return result;
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
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            addToNotToCache(m);
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
                    long caches = cacheChunkExcept_Account(m);
                    if (caches < 1L) {
                        break;
                    } else {
                        result.detail += caches;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                result.success = getTotalFreeMemory() < Memory_Threshold;
            }
            return result;
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
    public long checkAndMaybeFreeMemory_Account(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunks,
            boolean hoome) {
        try {
            Account test = checkAndMaybeFreeMemory_Account(g, chunks);
            if (test == null) {
                return 0;
            }
            if (!test.success) {
                String message;
                message = "Warning! Not enough data to cache in "
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
                clearMemoryReserve();
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
    public Account checkAndMaybeFreeMemory_Account(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            addToNotToCache(g, chunkIDs);
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
                    long caches = cacheChunkExcept_Account(g, chunkIDs);
                    if (caches < 1L) {
                        break;
                    } else {
                        result.detail += caches;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                result.success = getTotalFreeMemory() < Memory_Threshold;
            }
            return result;
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
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(boolean hoome) {
        try {
            AccountDetail test;
            test = checkAndMaybeFreeMemory_AccountDetail();
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
                clearMemoryReserve();
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
    protected AccountDetail checkAndMaybeFreeMemory_AccountDetail() {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
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
                partResult = cacheChunk_AccountDetail();
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
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(
                    Grids_AbstractGrid g, boolean hoome) {
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
                clearMemoryReserve();
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
            Grids_AbstractGrid g) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            addToNotToCache(g);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
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
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(
                    Grids_AbstractGrid g,
                    Grids_2D_ID_int chunkID,
                    boolean hoome) {
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
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = checkAndMaybeFreeMemory_AccountDetail(g, chunkID, hoome);
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = initMemoryReserve_AccountDetail(g, chunkID, hoome);
                combine(result, partResult);
                return result;
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
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            addToNotToCache(g, chunkID);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
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
                partResult = cacheChunkExcept_AccountDetail(g, chunkID);
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
     * memory to continue. The Chunk with chunkID from g is not cached. No data
     * is cached with chunkID.
     *
     * @param chunkID
     * @param hoome
     * @return HashMap identifying chunks cached.
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(
                    Grids_2D_ID_int chunkID,
                    boolean hoome) {
        try {
            AccountDetail result;
            result = checkAndMaybeFreeMemory_AccountDetail(chunkID);
            if (result == null) {
                return null;
            }
            boolean resultPart0 = result.success;
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
            return result.detail;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = checkAndMaybeFreeMemory_AccountDetail(chunkID, hoome);
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = initMemoryReserve_AccountDetail(chunkID, hoome);
                combine(result, partResult);
                return result;
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
            Grids_2D_ID_int chunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            Grids_AbstractGrid g;
            Iterator<Grids_AbstractGrid> ite;
            ite = grids.iterator();
            while (ite.hasNext()) {
                g = ite.next();
                addToNotToCache(g, chunkID);
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
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
            }
            ite = grids.iterator();
            while (ite.hasNext()) {
                g = ite.next();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                do {
                    partResult = cacheChunkExcept_AccountDetail(g, chunkID);
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
            }
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
     * memory to continue. The Chunk with chunkID from g is not cached. No data
     * is cached as identified by m.
     *
     * @param m Identifies data not to be cached.
     * @param hoome If true then if an OutOfMemoryError is encountered then an
     * attempt is made to handle this otherwise not and the error is thrown.
     * @return HashMap identifying chunks cached or null if nothing is cached.
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
                    boolean hoome) {
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
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = checkAndMaybeFreeMemory_AccountDetail(m, hoome);
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = initMemoryReserve_AccountDetail(m, hoome);
                combine(result, partResult);
                return result;
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
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            addToNotToCache(m);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
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
                partResult = cacheChunkExcept_AccountDetail(m);
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
     * memory to continue. The Chunk with chunkID from g is not cached. No
     * chunks with ChunkID in chunkIDs are cached from g.
     *
     * @param g
     * @param hoome
     * @param chunkIDs
     * @return HashMap identifying chunks cached.
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(
                    Grids_AbstractGrid g,
                    HashSet<Grids_2D_ID_int> chunkIDs,
                    boolean hoome) {
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
                clearMemoryReserve();
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
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            addToNotToCache(g, chunkIDs);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
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
                partResult = cacheChunkExcept_AccountDetail(g, chunkIDs);
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
     * Attempts to cache all chunks in grids.
     *
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then cache operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunks_AccountDetail(boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = cacheChunks_AccountDetail(notToCache);
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail();
                    if (account != null) {
                        if (account.success) {
                            combine(result, account.detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                    partResult = checkAndMaybeFreeMemory_AccountDetail(hoome);
                    combine(result, partResult);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                freeSomeMemoryAndResetReserve_AccountDetails(e, hoome);
                return cacheChunks_AccountDetail(hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to cache all Grids_AbstractGridChunk in this.grids.
     *
     * @return
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunks_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            partResult = ite.next().cacheChunks_AccountDetail();
            combine(result,
                    partResult);
        }
        return result;
    }

    /**
     * Attempts to cache all Grids_AbstractGridChunk in this.grids.
     *
     * @param m
     * @return
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunks_AccountDetail(
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        Grids_AbstractGrid g;
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            g = ite.next();
            partResult = g.cacheChunksExcept_AccountDetail(m.get(g));
            combine(result, partResult);
        }
        return result;
    }

    /**
     * Attempts to cache all chunks in env.
     *
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then cache operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return A count of the number of chunks cached.
     */
    public long cacheChunks_Account(
            boolean hoome) {
        try {
            long result;
            try {
                result = cacheChunks_Account();
                if (result < 1) {
                    Account account = checkAndMaybeFreeMemory_Account();
                    if (account != null) {
                        if (account.success) {
                            result += account.detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += checkAndMaybeFreeMemory_Account(hoome);
                }
            } catch (OutOfMemoryError e) {
                /**
                 * Set hoome = false to exit method by throwing OutOfMemoryError
                 */
                hoome = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                long result = freeSomeMemoryAndResetReserve_Account(e, hoome);
                result += cacheChunks_Account(hoome);
                return result;
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
    protected long cacheChunks_Account() {
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite;
        ite = grids.iterator();
        while (ite.hasNext()) {
            long partResult;
            Grids_AbstractGrid g;
            g = ite.next();
            partResult = g.cacheChunks_Account();
            result += partResult;
        }
        dataToCache = false;
        return result;
    }

    /**
     * Attempts to cache all Grids_AbstractGridChunk in this.grids.
     *
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then cache operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public void cacheChunks(boolean hoome) {
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
                clearMemoryReserve();
                if (!cacheChunk()) {
                    throw e;
                }
                initMemoryReserve();
                cacheChunks();
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to cache all Grids_AbstractGridChunk in grids.
     *
     * @return
     */
    protected boolean cacheChunks() {
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
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
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached.
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then cache operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunk_AccountDetail(boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = cacheChunk_AccountDetail();
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail();
                    if (account != null) {
                        if (account.success) {
                            combine(result, account.detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                    partResult = checkAndMaybeFreeMemory_AccountDetail(hoome);
                    combine(result, partResult);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
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
    public boolean cacheChunk(boolean hoome) {
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
                clearMemoryReserve();
                if (!cacheChunk()) {
                    throw e;
                }
                initMemoryReserve();
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
    protected boolean cacheChunk() {
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        Grids_AbstractGrid g;
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
    public void cacheChunkExcept(Grids_AbstractGrid g, boolean hoome) {
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
                clearMemoryReserve();
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
    protected boolean cacheChunkExcept(Grids_AbstractGrid g) {
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid bg = ite.next();
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
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> toGetCombined,
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> toCombine) {
        if (toCombine != null) {
            if (!toCombine.isEmpty()) {
                Set<Grids_AbstractGrid> toGetCombinedKS = toGetCombined.keySet();
                Set<Grids_AbstractGrid> toCombineKS = toCombine.keySet();
                Iterator<Grids_AbstractGrid> ite = toCombineKS.iterator();
                Grids_AbstractGrid g;
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
     * Attempts to cache any Grids_AbstractGridChunk in this.grids.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunk_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid g = ite.next();
            if (notToCache.containsKey(g)) {
                HashSet<Grids_2D_ID_int> chunkIDs;
                chunkIDs = notToCache.get(g);
                result = g.cacheChunkExcept_AccountDetail(chunkIDs);
                if (!result.isEmpty()) {
                    return result;
                }
            }
        }
        dataToCache = false;
        return null;
    }

    /**
     * @param hoome
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached. Attempts to cache any
     * Grids_AbstractGridChunk in this.grids except for those in with
     * Grids_AbstractGrid.ID = _ChunkID.
     * @param chunkID The Grids_AbstractGrid.ID not to be cached.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_2D_ID_int chunkID,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
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
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> pr;
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
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(chunkID);
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> pr;
                pr = initMemoryReserve_AccountDetail(chunkID, hoome);
                combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached. Attempts to cache any
     * Grids_AbstractGridChunk in this.grids except for those in with
     * Grids_AbstractGrid.ID = _ChunkID.
     * @param chunkID The Grids_AbstractGrid.ID not to be cached.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid g = ite.next();
            r = g.cacheChunkExcept_AccountDetail(chunkID);
            if (!r.isEmpty()) {
                HashSet<Grids_2D_ID_int> chunkIDs = new HashSet<>(1);
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
    public long cacheChunkExcept_Account(Grids_2D_ID_int chunkID, boolean hoome) {
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
                clearMemoryReserve();
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
    protected long cacheChunkExcept_Account(Grids_2D_ID_int chunkID) {
        long r = 0L;
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid g = ite.next();
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
    protected boolean cacheChunkExcept(Grids_2D_ID_int chunkID) {
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid g = ite.next();
            if (cacheChunkExcept_Account(g, chunkID) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param hoome
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached. Attempts to cache any
     * Grids_AbstractGridChunk in this.grids except for those in
     * _Grid2DSquareCell_ChunkIDHashSet.
     * @param m HashMap with Grids_AbstractGrid as keys and a respective HashSet
     * of Grids_AbstractGrid.ChunkIDs as values. Collectively these identifying
     * those chunks not to be cached from the Grids_AbstractGrid.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
            r = cacheChunkExcept_AccountDetail(m);
            try {
                if (r.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail(m);
                    if (account != null) {
                        if (account.success) {
                            combine(r, account.detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> pr;
                    pr = checkAndMaybeFreeMemory_AccountDetail(m, hoome);
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
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
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
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached. Attempts to cache any
     * Grids_AbstractGridChunk in this.grids except for those in
     * _Grid2DSquareCell_ChunkIDHashSet.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        r = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        HashSet<Grids_2D_ID_int> s = new HashSet<>(1);
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            Grids_AbstractGrid g = ite.next();
            if (m.containsKey(g)) {
                HashSet<Grids_2D_ID_int> chunkIDs = m.get(g);
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
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_AbstractGrid g,
                    HashSet<Grids_2D_ID_int> chunkIDs) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        r = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        HashSet<Grids_2D_ID_int> rp = new HashSet<>(1);
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            Grids_AbstractGrid gb = ite.next();
            if (g == gb) {
                chunkID = gb.cacheChunkExcept_AccountChunk(chunkIDs);
                if (chunkID != null) {
                    rp.add(chunkID);
                    r.put(g, rp);
                    return r;
                }
            } else {
                chunkID = g.cacheChunk_AccountChunk();
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
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_AbstractGrid g,
                    Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        r = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        HashSet<Grids_2D_ID_int> rp = new HashSet<>(1);
        Grids_2D_ID_int chunkIDb;
        while (ite.hasNext()) {
            Grids_AbstractGrid gb = ite.next();
            if (g == gb) {
                chunkIDb = gb.cacheChunkExcept_AccountChunk(chunkID);
                if (chunkIDb != null) {
                    rp.add(chunkIDb);
                    r.put(g, rp);
                    return r;
                }
            } else {
                chunkIDb = g.cacheChunk_AccountChunk();
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
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_AbstractGrid g,
                    Grids_2D_ID_int chunkID, boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
            r = cacheChunkExcept_AccountDetail(g, chunkID);
            return r;
        } catch (java.lang.OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> rp;
                rp = cacheChunkExcept_AccountDetail(g, chunkID);
                if (rp.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
                r = initMemoryReserve_AccountDetail(g, chunkID, hoome);
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
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_AbstractGrid g) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        r = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        HashSet<Grids_2D_ID_int> rp = new HashSet<>(1);
        while (ite.hasNext()) {
            Grids_AbstractGrid gb = ite.next();
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
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            Grids_AbstractGrid g = ite.next();
            if (m.containsKey(g)) {
                HashSet<Grids_2D_ID_int> chunkIDs = m.get(g);
                chunkID = g.cacheChunkExcept_AccountChunk(chunkIDs);
                if (chunkID != null) {
                    return 1L;
                }
            }
            chunkID = g.cacheChunk_AccountChunk();
            if (chunkID != null) {
                return 1L;
            }
        }
        return 0L; // If here then nothing could be cached!
    }

    protected boolean cacheChunkExcept(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        Iterator<Grids_AbstractGrid> ite;
        ite = grids.iterator();
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            Grids_AbstractGrid g = ite.next();
            if (m.containsKey(g)) {
                HashSet<Grids_2D_ID_int> chunkIDs = m.get(g);
                chunkID = g.cacheChunkExcept_AccountChunk(chunkIDs);
                if (chunkID != null) {
                    return true;
                }
            }
            chunkID = g.cacheChunk_AccountChunk();
            if (chunkID != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param hoome
     * @param chunkIDs
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached. Attempts to cache any
     * Grids_AbstractGridChunk in this.grids except for those in g with ChunkIDs
     * in chunkIDs.
     * @param g Grids_AbstractGrid that's chunks are not to be cached.
     */
    public long cacheChunkExcept_Account(Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs, boolean hoome) {
        try {
            long r = cacheChunkExcept_Account(g, chunkIDs);
            try {
                if (r < 1) {
                    Account account;
                    account = checkAndMaybeFreeMemory_Account(g, chunkIDs);
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
                clearMemoryReserve();
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
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached. Attempts to cache any
     * Grids_AbstractGridChunk in this.grids except for those in g with ChunkIDs
     * in chunkIDs.
     * @param g Grids_AbstractGrid that's chunks are not to be cached.
     */
    protected long cacheChunkExcept_Account(Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid gb = ite.next();
            if (gb != g) {
                TreeMap<Grids_2D_ID_int, Grids_AbstractGridChunk> m;
                m = gb.getChunkIDChunkMap();
                Set<Grids_2D_ID_int> chunkIDsb = m.keySet();
                Iterator<Grids_2D_ID_int> iteb = chunkIDsb.iterator();
                while (iteb.hasNext()) {
                    Grids_2D_ID_int chunkID = iteb.next();
                    if (!chunkIDs.contains(chunkID)) {
                        //Check it can be cached
                        Grids_AbstractGridChunk chunkb = m.get(chunkID);
                        if (chunkb != null) {
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
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached. Attempts to cache any
     * Grids_AbstractGridChunk in this.grids except for that in
     * _Grid2DSquareCell with Grids_AbstractGrid._ChunkID _ChunkID.
     * @param g Grids_AbstractGrid that's chunks are not to be cached.
     * @param chunkID The Grids_AbstractGrid.ID not to be cached.
     */
    public long cacheChunkExcept_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean hoome) {
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
                clearMemoryReserve();
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
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached. Attempts to cache any
     * Grids_AbstractGridChunk in this.grids except for that in
     * _Grid2DSquareCell with Grids_AbstractGrid._ChunkID _ChunkID.
     * @param g Grids_AbstractGrid that's chunks are not to be cached.
     * @param chunkID The Grids_AbstractGrid.ID not to be cached.
     */
    protected long cacheChunkExcept_Account(Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        long r = cacheChunkExcept_Account(g);
        if (r < 1L) {
            r = g.cacheChunkExcept_Account(chunkID);
        }
        return r;
    }

    /**
     * @param hoome
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached. Attempts to cache any
     * Grids_AbstractGridChunk in this.grids except for those in g.
     * @param g Grids_AbstractGrid that's chunks are not to be cached.
     */
    public long cacheChunkExcept_Account(Grids_AbstractGrid g,
            boolean hoome) {
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
                clearMemoryReserve();
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
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached. Attempts to cache any
     * Grids_AbstractGridChunk in this.grids except for those in
     * _Grid2DSquareCell.
     * @param g Grids_AbstractGrid that's chunks are not to be cached.
     */
    protected long cacheChunkExcept_Account(Grids_AbstractGrid g) {
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid gb = ite.next();
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
     * Attempts to Cache all Grids_AbstractGrid.ChunkIDs in this.grids except
     * those with Grids_AbstractGrid.ID _ChunkID.
     *
     * @param hoome
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached.
     * @param chunkID The Grids_AbstractGrid.ID not to be cached.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_2D_ID_int chunkID,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
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
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> rp;
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
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(chunkID);
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> rp;
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
     * Attempts to Cache all Grids_AbstractGrid.ChunkIDs in this.grids except
     * those with Grids_AbstractGrid.ID _ChunkID.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached.
     * @param chunkID The Grids_AbstractGrid.ID not to be cached.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        r = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid g = ite.next();
            combine(r, g.cacheChunksExcept_AccountDetail(chunkID));
        }
        return r;
    }

    /**
     * Attempts to Cache all Grids_AbstractGrid.ChunkIDs in this.grids except
     * those with Grids_AbstractGrid.ID _ChunkID.
     *
     * @param hoome
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached.
     * @param g Grids_AbstractGrid that's chunks are not to be cached. cached.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_AbstractGrid g,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
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
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> rp;
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
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(g);
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> rp;
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
     * Attempts to Cache all Grids_AbstractGrid.ChunkIDs in this.grids except
     * those with Grids_AbstractGrid.ID _ChunkID.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached.
     * @param g Grids_AbstractGrid that's chunks are not to be cached.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_AbstractGrid g) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        r = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid gb = ite.next();
            if (gb != g) {
                combine(r, gb.cacheChunks_AccountDetail());
            }
        }
        return r;
    }

    public long cacheChunksExcept_Account(Grids_AbstractGrid g, boolean hoome) {
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
                clearMemoryReserve();
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

    protected long cacheChunksExcept_Account(Grids_AbstractGrid g) {
        long r = 0L;
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid gb = ite.next();
            if (gb != g) {
                r += gb.env.cacheChunks_Account();
            }
        }
        return r;
    }

    /**
     * Attempts to Cache all Grids_AbstractGrid.ChunkIDs in this.grids except
     * those with Grids_AbstractGrid.ID _ChunkID.
     *
     * @param hoome
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached.
     * @param g Grids_AbstractGrid that's chunks are not to be cached.
     * @param chunkID The Grids_AbstractGrid.ID not to be cached.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_AbstractGrid g,
                    Grids_2D_ID_int chunkID, boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
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
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> rp;
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
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
                r = cacheChunkExcept_AccountDetail(g, chunkID);
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> rp;
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

    public long cacheChunksExcept_Account(Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID, boolean hoome) {
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
                clearMemoryReserve();
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

    protected long cacheChunksExcept_Account(Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        long r = 0L;
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid gb = ite.next();
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
     * Attempts to Cache all Grids_AbstractGrid.ChunkIDs in this.grids except
     * those with Grids_AbstractGrid.ID _ChunkID.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached.
     * @param g Grids_AbstractGrid that's chunks are not to be cached.
     * @param chunkID The Grids_AbstractGrid.ID not to be cached.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_AbstractGrid g,
                    Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        r = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        Grids_AbstractGrid bg;
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> rp;
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

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_AbstractGrid g,
                    HashSet<Grids_2D_ID_int> chunkIDs) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        r = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid gb = ite.next();
            if (gb != g) {
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> pr;
                pr = gb.cacheChunks_AccountDetail();
                combine(r, pr);
            } else {
                HashSet<Grids_2D_ID_int> chunks;
                chunks = g.getChunkIDs();
                Grids_2D_ID_int chunkID;
                Iterator<Grids_2D_ID_int> ite2;
                ite2 = chunks.iterator();
                while (ite2.hasNext()) {
                    chunkID = ite2.next();
                    if (!chunkIDs.contains(chunkID)) {
                        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> pr;
                        pr = cacheChunksExcept_AccountDetail(chunkID);
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
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was cached; and, value as the
     * Grids_AbstractGridChunk._ChunkID cached.
     * @param g Grids_AbstractGrid that's chunks are not to be cached.
     * @param chunkIDs The chunk IDs in g not to be cached.
     */
    protected long cacheChunksExcept_Account(Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        long r = 0L;
        Iterator<Grids_AbstractGrid> ite;
        ite = grids.iterator();
        Grids_AbstractGrid gb;
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
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
            boolean hoome) {
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
                clearMemoryReserve();
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
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        long r = 0L;
        Iterator<Grids_AbstractGrid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid g = ite.next();
            r += g.cacheChunksExcept_Account(m.get(g));
        }
        return r;
    }

    public void cacheData() {
        cacheChunks();
    }

    public void cacheData(boolean hoome) {
        cacheChunks();
    }

    @Override
    public boolean cacheDataAny(boolean hoome) {
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
                clearMemoryReserve();
                boolean r = cacheDataAny();
                initMemoryReserve();
                return r;
            } else {
                throw e;
            }
        }
    }

    @Override
    public boolean cacheDataAny() {
        return cacheChunk();
    }

    private boolean dataToCache = true;

    public boolean isDataToCache() {
        return dataToCache;
    }

    public void setDataToCache(boolean dataToCache) {
        this.dataToCache = dataToCache;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    Grids_AbstractGrid g,
                    boolean hoome) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        r = checkAndMaybeFreeMemory_AccountDetail(g, hoome);
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> rp;
        rp = initMemoryReserve_AccountDetail(g, hoome);
        combine(r, rp);
        return r;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    Grids_AbstractGrid g, HashSet<Grids_2D_ID_int> chunks,
                    boolean hoome) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        r = checkAndMaybeFreeMemory_AccountDetail(g, chunks, hoome);
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> rp;
        rp = initMemoryReserve_AccountDetail(g, chunks, hoome);
        combine(r, rp);
        return r;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    OutOfMemoryError e, boolean hoome) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        r = cacheChunk_AccountDetail();
        if (r.isEmpty()) {
            throw e;
        }
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> rp;
        rp = initMemoryReserve_AccountDetail(hoome);
        combine(r, rp);
        return r;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    boolean hoome) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        r = checkAndMaybeFreeMemory_AccountDetail(hoome);
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> rp;
        rp = initMemoryReserve_AccountDetail(hoome);
        combine(r, rp);
        return r;
    }

    protected long freeSomeMemoryAndResetReserve_Account(Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunks, boolean hoome) {
        long r = checkAndMaybeFreeMemory_Account(g, chunks, hoome);
        r += initMemoryReserve_Account(g, chunks, hoome);
        return r;
    }

    protected long freeSomeMemoryAndResetReserve_Account(OutOfMemoryError e,
            boolean hoome) {
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
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> getNotToCache() {
        return notToCache;
    }

    /**
     * Simple inner class for accounting memory cacheping detail.
     */
    protected class AccountDetail {

        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> detail;
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
