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
package uk.ac.leeds.ccg.grids.core;

import uk.ac.leeds.ccg.grids.memory.Grids_Memory;
import uk.ac.leeds.ccg.grids.memory.Grids_MemoryManager;
import uk.ac.leeds.ccg.grids.memory.Grids_AccountDetail;
import uk.ac.leeds.ccg.grids.memory.Grids_Account;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.generic.io.Generic_Path;
import uk.ac.leeds.ccg.agdt.math.Math_BigDecimal;
import uk.ac.leeds.ccg.grids.io.Grids_Files;
import uk.ac.leeds.ccg.grids.process.Grids_Processor;

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
    protected transient HashMap<Grids_Grid, Set<Grids_2D_ID_int>> notToClear;

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
     * The Generic_Environment is initialised using:
     * {@code new Generic_Environment(new Generic_Defaults())}.
     *
     * @throws java.io.IOException If encountered.
     * @throws Exception If there is another problem setting up the file store.
     */
    public Grids_Environment() throws IOException, Exception {
        this(new Generic_Environment(new Generic_Defaults()));
    }

    /**
     * Creates a new Grids_Environment. {@link #files} is initialised from
     * {@code e.files.getDir()}.
     *
     * @param e The Generic_Environment.
     * @throws java.io.IOException If encountered.
     */
    public Grids_Environment(Generic_Environment e) throws IOException,
            Exception {
        this(e, e.files.getDir());
    }

    /**
     * Creates a new Grids_Environment.
     *
     * @param e What {@link #env} is set to.
     * @param dir Used to initialise {@link #files}.
     * @throws java.io.IOException If encountered.
     */
    public Grids_Environment(Generic_Environment e, Generic_Path dir)
            throws IOException, Exception {
        this.env = e;
        initMemoryReserve(Default_Memory_Threshold, e);
        initGrids();
        initNotToClear();
        Path p = e.getLogDir(Grids_Strings.s_grids);
        files = new Grids_Files(new Generic_Defaults(Paths.get(dir.toString(),
                Grids_Strings.s_grids)));
        e.files.setDir(p);
    }

    /**
     * If {@link #processor} is not {@code null}, it is returned. If
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
     * ideally not be cleared.
     */
    public final void initNotToClear() {
        notToClear = new HashMap<>();
    }

    /**
     * Adds the chunk IDs of {@code g} to {@link #notToClear}.
     *
     * @param g The grid to add to {@link #notToClear}.
     */
    public final void addToNotToClear(Grids_Grid g) {
        notToClear.put(g, g.getChunkIDs());
    }

    /**
     * Removes the chunk IDs of {@code g} from {@link #notToClear}.
     *
     * @param g The grid to remove from {@link #notToClear}.
     */
    public final void removeFromNotToClear(Grids_Grid g) {
        notToClear.remove(g);
    }

    /**
     * Adds chunk row {@code cr} of chunks of {@code g} to {@link #notToClear}.
     *
     * @param g The grid with chunks to add to {@link #notToClear}.
     * @param cr The row of chunks in {@code g} to add to {@link #notToClear}.
     */
    public final void addToNotToClear(Grids_Grid g, int cr) {
        int n = g.getNChunkCols();
        for (int c = 0; c < n; c++) {
            addToNotToClear(g, new Grids_2D_ID_int(cr, c));
        }
    }

    /**
     * Removed chunk row {@code cr} of chunks of {@code g} from
     * {@link #notToClear}.
     *
     * @param g The grid with chunks to remove from {@link #notToClear}.
     * @param cr The row of chunks in {@code g} to remove from
     * {@link #notToClear}.
     */
    public final void removeFromNotToClear(Grids_Grid g, int cr) {
        int n = g.getNChunkCols();
        for (int c = 0; c < n; c++) {
            removeFromNotToClear(g, new Grids_2D_ID_int(cr, c));
        }
    }

    /**
     * Adds the chunk ID {@code i} of {@code g} to {@link #notToClear}.
     *
     * @param g The grid containing chunk with chunk ID {@code i}.
     * @param i The chunk ID to add to {@link #notToClear}.
     */
    public final void addToNotToClear(Grids_Grid g, Grids_2D_ID_int i) {
        if (notToClear.containsKey(g)) {
            notToClear.get(g).add(i);
        } else {
            Set<Grids_2D_ID_int> chunkIDs = new HashSet<>();
            chunkIDs.add(i);
            notToClear.put(g, chunkIDs);
        }
    }

    /**
     * Adds the chunk ID {@code i} of each grid in {@code g} to
     * {@link #notToClear}.
     *
     * @param g The array of grids for which each chunk ID {@code i} is added to
     * {@link #notToClear}.
     * @param i The chunk ID to add to {@link #notToClear}.
     */
    public final void addToNotToClear(Grids_Grid[] g, Grids_2D_ID_int i) {
        for (Grids_Grid g1 : g) {
            addToNotToClear(g1, i);
        }
    }

    /**
     * Puts everything in {@code m} into {@link notToClear}.
     *
     * @param m The map contents to add to {@link #notToClear}.
     */
    public final void addToNotToClear(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) {
        Iterator<Grids_Grid> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            if (notToClear.containsKey(g)) {
                notToClear.get(g).addAll(m.get(g));
            } else {
                notToClear.put(g, m.get(g));
            }
        }
    }

    /**
     * Removes the chunk ID {@code i} of each grid in {@code g} from
     * {@link #notToClear}.
     *
     * @param g The array of grids for which each chunk ID {@code i} is removed
     * from {@link #notToClear}.
     * @param i The chunk ID to remove from {@link #notToClear}.
     */
    public final void removeFromNotToClear(Grids_Grid[] g, Grids_2D_ID_int i) {
        for (Grids_Grid g1 : g) {
            removeFromNotToClear(g1, i);
        }
    }

    /**
     * Removes everything in {@code m} from {@link notToClear}.
     *
     * @param m The map contents to remove from {@link #notToClear}.
     */
    public final void removeFromNotToClear(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) {
        Iterator<Grids_Grid> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            if (notToClear.containsKey(g)) {
                notToClear.get(g).removeAll(m.get(g));
            }
        }
    }

    /**
     * Adds all the chunk IDs in {@code chunkIDs} of chunks in {@code g} into
     * {@link #notToClear}.
     *
     * @param g The grid with chunk IDs to add to {@link #notToClear}.
     * @param s The chunk IDs to add to {@link #notToClear}.
     */
    public final void addToNotToClear(Grids_Grid g, Set<Grids_2D_ID_int> s) {
        if (notToClear.containsKey(g)) {
            notToClear.get(g).addAll(s);
        } else {
            notToClear.put(g, s);
        }
    }

    /**
     * Remove the chunk with chunk ID {@code i} in {@code g} from
     * {@link #notToClear}.
     *
     * @param g The grid with chunk ID {@code i} which will be removed from
     * {@link #notToClear} if it is there.
     * @param i The chunk ID of {@code g} to remove from {@link #notToClear}.
     */
    public final void removeFromNotToClear(Grids_Grid g, Grids_2D_ID_int i) {
        if (notToClear.containsKey(g)) {
            /**
             * Decided that it is best not to remove g from notToClear if
             * notToClear.get(g).isEmpty(). So the empty Set remains and this
             * takes up a small amount of resource, but it is probably better to
             * keep it in case it is re-used rather than destroying it.
             */
            notToClear.get(g).remove(i);
//            Set<Grids_2D_ID_int> chunkIDs = notToClear.get(g);
//            chunkIDs.remove(chunkID);
//            if (chunkIDs.isEmpty()) {
//                notToClear.remove(g);
//            }
        }
    }

    /**
     * Adds all the ChunkIDs of g that are within cellDistance of the chunk with
     * chunk ID {@code i} to {@link #notToClear}.
     *
     * @param g The Grid.
     * @param i Central chunk ID.
     * @param chunkRow The chunk row index of chunk with chunk ID {@code i} -
     * provided for convenience.
     * @param chunkCol The chunk column index of chunk with chunk ID {@code i} -
     * provided for convenience.
     * @param chunkNRows The normal number of rows in a chunk. (The last row may
     * have fewer.)
     * @param chunkNCols The normal number of columns in a chunk. (The last
     * column may have fewer.)
     * @param cellDistance The cell distance within which all chunk IDs in
     * {@code g} are added to {@link #notToClear}.
     */
    public final void addToNotToClear(Grids_Grid g, Grids_2D_ID_int i,
            int chunkRow, int chunkCol, int chunkNRows, int chunkNCols,
            int cellDistance) {
        Set<Grids_2D_ID_int> s;
        if (notToClear.containsKey(g)) {
            s = notToClear.get(g);
        } else {
            s = new HashSet<>();
            notToClear.put(g, s);
        }
        int t;
        int r = 0;
        t = 0;
        while (t < cellDistance) {
            t += chunkNRows;
            r++;
        }
        int j = 0;
        t = 0;
        while (t < cellDistance) {
            t += chunkNCols;
            j++;
        }
        for (int k = -r; k <= r; k++) {
            int cr = chunkRow + k;
            for (int l = -j; l <= j; l++) {
                int cc = chunkCol + l;
                if (g.isInGrid(cr, cc)) {
                    s.add(new Grids_2D_ID_int(cr, cc));
                }
            }
        }
    }

    /**
     * Initialises {@link #grids}.
     *
     * @param grids Used to initialise {@link #grids} unless it is {@code null}
     * in which case {@link #grids} is initialised as a new {@code HashSet}.
     */
    protected void initGrids(Set<Grids_Grid> grids) {
        if (this.grids == null) {
            this.grids = grids;
        } else {
            if (grids == null) {
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
     * @param grids What {@link #grids} is set to.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it.
     * @throws java.io.IOException If encountered.
     */
    public void initGridsAndMemoryReserve(Set<Grids_Grid> grids, boolean hoome)
            throws IOException, Exception {
        try {
            initGridsAndMemoryReserve(grids);
            checkAndMaybeFreeMemory(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!swapChunk()) {
                    throw e;
                }
                initGridsAndMemoryReserve(grids, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #grids} and sets the memory reserve in each to
     * {@link #MemoryReserve}.
     *
     * @param grids What {@link #grids} is set to.
     */
    protected void initGridsAndMemoryReserve(Set<Grids_Grid> grids) {
        initGrids(grids);
        Iterator<Grids_Grid> ite = this.grids.iterator();
        if (ite.hasNext()) {
            ite.next().env.setMemoryReserve(MemoryReserve);
        } else {
            initMemoryReserve(env);
        }
    }

    /**
     * Initialises {@link #MemoryReserve}. A detailed account of any clearing is
     * returned.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return A detailed account of any clearing.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_AccountDetail initMemoryReserve_AccountDetail(boolean hoome)
            throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_AccountDetail(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_AccountDetail r = swapChunk_AccountDetail();
                r.add(checkAndMaybeFreeMemory_AccountDetail(hoome));
                r.add(initMemoryReserve_AccountDetail(hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve}. An account of how many chunks were
     * cleared in the process is returned.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return An account of chunks cleared in the process of initialising
     * {@link #MemoryReserve}.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_Account initMemoryReserve_Account(boolean hoome)
            throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_Account(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!swapChunk()) {
                    throw e;
                }
                Grids_Account r = new Grids_Account();
                r.add(1);
                r.add(checkAndMaybeFreeMemory_Account(hoome));
                r.add(initMemoryReserve_Account(hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than any chunks in {@code g}) or other data. No account of
     * any clearing is returned.
     *
     * @param g A grid with chunks not to be swapped out in the event of any
     * memory handling.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
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
                if (swapChunkExcept_Account(g).detail < 1L) {
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
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than the chunk with ID {@code i}) or other data. No account
     * of any clearing is returned.
     *
     * @param i The ID of a chunk not to cleared in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public final void initMemoryReserve(Grids_2D_ID_int i, boolean hoome)
            throws IOException, Exception {
        try {
            initMemoryReserve(env);
            checkAndMaybeFreeMemory(i, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (swapChunkExcept_Account(i).detail < 1L) {
                    throw e;
                }
                checkAndMaybeFreeMemory(i, hoome);
                initMemoryReserve(i, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than the chunk with ID {@code i}) or other data. A detailed
     * account of any chunks cleared is returned.
     *
     * @param i The ID of a chunk not to cleared in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return A detailed account of any chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_AccountDetail initMemoryReserve_AccountDetail(
            Grids_2D_ID_int i, boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_AccountDetail(i, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(i);
                if (r.detail.isEmpty()) {
                    throw e;
                }
                r.add(initMemoryReserve_AccountDetail(i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than the chunk with ID {@code i}) or other data. An account
     * of any clearing is returned.
     *
     * @param i The ID of a chunk not to cleared in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return The number of chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_Account initMemoryReserve_Account(Grids_2D_ID_int i,
            boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_Account(i, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_Account r = swapChunkExcept_Account(i);
                if (r.detail < 1L) {
                    throw e;
                }
                r.add(initMemoryReserve_Account(i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than the chunk with ID {@code i} in grid {@code g}) or
     * other data. No account of any clearing is returned.
     *
     * @param g The grid in which chunk with ID {@code i} is not cleared.
     * @param i The ID of a chunk not to cleared in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public final void initMemoryReserve(Grids_Grid g, Grids_2D_ID_int i,
            boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            checkAndMaybeFreeMemory(g, i, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (swapChunkExcept(g, i)) {
                    throw e;
                }
                checkAndMaybeFreeMemory(g, i, hoome);
                initMemoryReserve(g, i, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than the chunk with ID {@code i} in grid {@code g}) or
     * other data. An account of any clearing is returned.
     *
     * @param g The grid in which chunk with ID {@code i} is not cleared.
     * @param i The ID of a chunk not to cleared in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return An account of any clearing.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_Account initMemoryReserve_Account(Grids_Grid g,
            Grids_2D_ID_int i, boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_Account(g, i, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_Account r = new Grids_Account();
                if (swapChunkExcept(g, i)) {
                    r.add();
                } else {
                    throw e;
                }
                r.add(checkAndMaybeFreeMemory_Account(g, i, hoome));
                r.add(initMemoryReserve_Account(g, i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than the chunk with ID {@code i} in grid {@code g}) or
     * other data. A detailed account of any clearing is returned.
     *
     * @param g The grid in which chunk with ID {@code i} is not cleared.
     * @param i The ID of a chunk not to cleared in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return A detailed account of any chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_AccountDetail initMemoryReserve_AccountDetail(Grids_Grid g,
            Grids_2D_ID_int i, boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_AccountDetail(g, i, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(g, i);
                if (r.detail.isEmpty()) {
                    throw e;
                }
                r.add(checkAndMaybeFreeMemory_AccountDetail(g, i, hoome));
                r.add(initMemoryReserve_AccountDetail(g, i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than those with chunk IDs in {@code s} in grid {@code g})
     * or other data. A detailed account of any clearing is returned.
     *
     * @param g The grid from which chunks with IDs in {@code s} are not
     * cleared.
     * @param s The set of chunk IDs of a chunk not to cleared from grid (@code
     * g )in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return A detailed account of any chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_AccountDetail initMemoryReserve_AccountDetail(Grids_Grid g,
            Set<Grids_2D_ID_int> s, boolean hoome) throws IOException,
            Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_AccountDetail(g, s, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(g, s);
                if (r.detail.isEmpty()) {
                    throw e;
                }
                r.add(checkAndMaybeFreeMemory_AccountDetail(g, s, hoome));
                r.add(initMemoryReserve_AccountDetail(g, s, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than those in grid {@code g}) or other data. A detailed
     * account of any clearing is returned.
     *
     * @param g The grid from which chunks with IDs in {@code s} are not
     * cleared.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return A detailed account of any chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_AccountDetail initMemoryReserve_AccountDetail(Grids_Grid g, boolean hoome)
            throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_AccountDetail(g, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(g);
                if (r.detail.isEmpty()) {
                    throw e;
                }
                r.add(checkAndMaybeFreeMemory_AccountDetail(g, hoome));
                r.add(initMemoryReserve_AccountDetail(g, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than those in {@code m}) or other data. An account of any
     * clearing is returned.
     *
     * @param m A map containing chunk IDs of a chunks not to cleared from grids
     * in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return The number of chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_Account initMemoryReserve_Account(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m, boolean hoome)
            throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_Account(m, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_Account r = new Grids_Account();
                if (Grids_Environment.this.swapChunkExcept(m)) {
                    throw e;
                }
                r.add(1);
                r.add(checkAndMaybeFreeMemory_Account(m, hoome));
                r.add(initMemoryReserve_Account(m, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than those with chunk IDs in {@code s} in grid {@code g})
     * or other data. An account of any clearing is returned.
     *
     * @param g The grid from which chunks are not cleared.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return The number of chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_Account initMemoryReserve_Account(Grids_Grid g, boolean hoome)
            throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_Account(g, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_Account r = swapChunkExcept_Account(g);
                if (r.detail < 1L) {
                    throw e;
                }
                r.add(checkAndMaybeFreeMemory_Account(g, hoome));
                r.add(initMemoryReserve_Account(g, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than those with chunk IDs in {@code s} in grid {@code g})
     * or other data. An account of any clearing is returned.
     *
     * @param g The grid from which chunks with IDs in {@code s} are not
     * cleared.
     * @param s The set of chunk IDs of a chunk not to cleared from grid (@code
     * g )in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return The number of chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_Account initMemoryReserve_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> s, boolean hoome) throws IOException,
            Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_Account(g, s, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_Account r = new Grids_Account();
                if (!Grids_Environment.this.swapChunkExcept(g, s)) {
                    throw e;
                }
                r.add(1);
                r.add(checkAndMaybeFreeMemory_Account(g, s, hoome));
                r.add(initMemoryReserve_Account(g, s, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than those with chunk IDs in {@code s} in grid {@code g})
     * or other data. No account of any clearing is returned.
     *
     * @param g The grid from which chunks with IDs in {@code s} are not
     * cleared.
     * @param s The set of chunk IDs of a chunk not to cleared from grid (@code
     * g )in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @throws IOException If encountered.
     */
    @Override
    public final void initMemoryReserve(Grids_Grid g, Set<Grids_2D_ID_int> s,
            boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            checkAndMaybeFreeMemory(g, s, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!Grids_Environment.this.swapChunkExcept(g, s)) {
                    throw e;
                }
                checkAndMaybeFreeMemory(g, s, hoome);
                initMemoryReserve(g, s, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than those with chunk IDs in {@code s} in grid {@code g})
     * or other data. No account of any clearing is returned.
     *
     * @param m The map of chunk IDs of a chunks not to cleared in any memory
     * management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public void initMemoryReserve(HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m,
            boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            checkAndMaybeFreeMemory(m, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!Grids_Environment.this.swapChunkExcept(m)) {
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
     * Initialises {@link #MemoryReserve} and checks memory potentially clearing
     * chunks (other than those in {@code m}) or other data. A detailed account
     * of any clearing is returned.
     *
     * @param m A map containing chunk IDs of a chunks not to cleared from grids
     * in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return A detailed account of any chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_AccountDetail initMemoryReserve_AccountDetail(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m,
            boolean hoome) throws IOException, Exception {
        try {
            initMemoryReserve(env);
            return checkAndMaybeFreeMemory_AccountDetail(m, hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(m);
                if (r.detail.isEmpty()) {
                    throw e;
                }
                r.add(checkAndMaybeFreeMemory_AccountDetail(m, hoome));
                r.add(initMemoryReserve_AccountDetail(m, hoome));
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
     * clear some chunks. Chunks in {@link #notToClear} are not cleared unless
     * desperate. If not enough data is found to clear then an OutOfMemoryError
     * is thrown.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return true if there is sufficient memory to continue and throws an
     * OutOfMemoryError otherwise.
     * @throws java.io.IOException If encountered.
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
     * returns {@code true}. If available fast access memory is low, then an
     * attempt is made to cache some chunks. Chunks in {@link #notToClear} are
     * not cleared unless desperate.
     *
     * @return {@code true} if there is sufficient memory to continue and
     * {@code false} otherwise.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public boolean checkAndMaybeFreeMemory() throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            if (notToClear.isEmpty()) {
                return checkAndMaybeFreeMemory_ClearAny();
            } else {
                do {
                    if (Grids_Environment.this.swapChunkExcept(notToClear)) {
                        if (getTotalFreeMemory() < Memory_Threshold) {
                            return true;
                        }
                    } else {
                        break;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                return checkAndMaybeFreeMemory_ClearAny();
            }
        }
        return false;
    }

    /**
     * Check and maybe free fast access memory by writing chunks to file. If
     * available fast access memory is not low then this simply returns true. If
     * available fast access memory is low, then an attempt is made to cache
     * some chunks. Chunks in NotToCache are not cached unless desperate.
     *
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     * @throws java.io.IOException If encountered.
     */
    protected boolean checkAndMaybeFreeMemory_ClearAny() throws IOException,
            Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            do {
                if (!swapChunk()) {
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
     * @param g The grid from which chunks are not cleared.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return {@code true} if there is sufficient memory to continue and throws
     * an OutOfMemoryError otherwise.
     * @throws java.io.IOException If encountered.
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
     * file. If available fast access memory is not low then this returns
     * {@code true}. If available fast access memory is low, then an attempt is
     * made to clear some chunks. Chunks in {@link #notToClear} are not cleared
     * unless desperate. No chunk in {@code g} is cleared.
     *
     * @param g The grid from which chunks are not cleared.
     * @return {@code true} if there is sufficient memory to continue and
     * {@code false} otherwise.
     * @throws java.io.IOException If encountered.
     */
    protected boolean checkAndMaybeFreeMemory(Grids_Grid g) throws IOException,
            Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            notToClear.put(g, g.getChunkIDs());
            do {
                if (!Grids_Environment.this.swapChunkExcept(notToClear)) {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                if (!Grids_Environment.this.swapChunkExcept(g)) {
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
     * file. The chunk with chunk ID {@code i} in {@code g} is not cleared. If
     * available fast access memory is not low then this simply returns
     * {@code true}. If available fast access memory is low, then an attempt is
     * made to clear chunks with IDs not in {@link #notToClear}. If this is
     * unsuccessful in clearing sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then chunks with IDs in
     * {@link #notToClear} are cleared. If this is unsuccessful in clearing
     * sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then {@code false} is
     * returned.
     *
     * @param g The grid from which the chunk with ID {@code i} is not cleared.
     * @param i The chunk from {@code g} that is not cleared.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return {@code true} if there is sufficient memory to continue and
     * {@code false} otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public boolean checkAndMaybeFreeMemory(Grids_Grid g, Grids_2D_ID_int i,
            boolean hoome) throws IOException, Exception {
        try {
            if (!checkAndMaybeFreeMemory(g, i)) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory(" + g.getClass().getName()
                        + ",Grids_2D_ID_int,boolean)";
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return true;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!checkAndMaybeFreeMemory(g, i)) {
                    throw e;
                }
                initMemoryReserve(g, i, hoome);
                return true;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by clearing chunks.
     * The chunk with chunk ID {@code i} in {@code g} is not cleared. If
     * available fast access memory is not low then this simply returns
     * {@code true}. If available fast access memory is low, then an attempt is
     * made to clear chunks with IDs not in {@link #notToClear}. If this is
     * unsuccessful in clearing sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then chunks with IDs in
     * {@link #notToClear} are cleared. If this is unsuccessful in clearing
     * sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then {@code false} is
     * returned.
     *
     * @param g The grid from which the chunk with ID {@code i} is not cleared.
     * @param i The chunk from {@code g} that is not cleared.
     * @return {@code true} if there is sufficient memory to continue and
     * {@code false} otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected boolean checkAndMaybeFreeMemory(Grids_Grid g,
            Grids_2D_ID_int i) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            addToNotToClear(g, i);
            do {
                if (!Grids_Environment.this.swapChunkExcept(notToClear)) {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                if (swapChunkExcept(g, i)) {
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
     * {@code true}. If available fast access memory is low, then an attempt is
     * made to clear chunks with IDs not in {@link #notToClear}. If this is
     * unsuccessful in clearing sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then chunks with IDs in
     * {@link #notToClear} are cleared. If this is unsuccessful in clearing
     * sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then {@code false} is
     * returned.
     *
     * @param i The chunk ID of chunks not to be cleared.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return {@code true} if there is sufficient memory to continue and
     * {@code false} otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public boolean checkAndMaybeFreeMemory(Grids_2D_ID_int i, boolean hoome)
            throws IOException, Exception {
        try {
            if (!checkAndMaybeFreeMemory(i)) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory(Grids_2D_ID_int,boolean)";
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return true;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!checkAndMaybeFreeMemory(i)) {
                    throw e;
                }
                initMemoryReserve(i, hoome);
                return true;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. No chunks with chunk ID {@code i} are cleared. If available fast
     * access memory is not low then this simply returns {@code true}. If
     * available fast access memory is low, then an attempt is made to clear
     * chunks with IDs not in {@link #notToClear}. If this is unsuccessful in
     * clearing sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then chunks with IDs in
     * {@link #notToClear} are cleared. If this is unsuccessful in clearing
     * sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then {@code false} is
     * returned.
     *
     * @param i The chunk ID of chunks not to be cleared.
     * @return {@code true} if {@code getTotalFreeMemory() < Memory_Threshold}
     * or if sufficient memory is freed so that this is the case.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected boolean checkAndMaybeFreeMemory(Grids_2D_ID_int i)
            throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Iterator<Grids_Grid> ite = grids.iterator();
            while (ite.hasNext()) {
                addToNotToClear(ite.next(), i);
                if (Grids_Environment.this.swapChunkExcept(notToClear)) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        return true;
                    }
                }
            }
            ite = grids.iterator();
            while (ite.hasNext()) {
                if (Grids_Environment.this.swapChunkExcept(i)) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. No data in {@code m} is cleared. If available fast access memory is
     * not low then this simply returns {@code true}. If available fast access
     * memory is low, then an attempt is made to clear chunks with IDs not in
     * {@link #notToClear}. If this is unsuccessful in clearing sufficient
     * memory such that {@code getTotalFreeMemory() < Memory_Threshold}, then
     * chunks with IDs in {@link #notToClear} are cleared. If this is
     * unsuccessful in clearing sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then {@code false} is
     * returned.
     *
     * @param m Indicates which chunks not to clear unless deperate.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return {@code true} if there is sufficient memory to continue and {code
     * false} otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public boolean checkAndMaybeFreeMemory(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m,
            boolean hoome) throws IOException, Exception {
        try {
            if (!checkAndMaybeFreeMemory(m)) {
                String message = "Warning! No data to clear in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory(" + m.getClass().getName()
                        + ",boolean)";
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return true;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!checkAndMaybeFreeMemory(m)) {
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
     * file. No data in {@code m} is cleared. If available fast access memory is
     * not low then this simply returns {@code true}. If available fast access
     * memory is low, then an attempt is made to clear chunks with IDs not in
     * {@link #notToClear}. If this is unsuccessful in clearing sufficient
     * memory such that {@code getTotalFreeMemory() < Memory_Threshold}, then
     * chunks with IDs in {@link #notToClear} are cleared. If this is
     * unsuccessful in clearing sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then {@code false} is
     * returned.
     *
     * @param m Indicates which chunks not to clear unless deperate.
     * @return {@code true} if there is sufficient memory to continue and {code
     * false} otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected boolean checkAndMaybeFreeMemory(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) throws IOException,
            Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            addToNotToClear(m);
            do {
                if (!Grids_Environment.this.swapChunkExcept(notToClear)) {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                if (swapChunkExcept(m)) {
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
     * file. No chunks from {@code g} with chunk IDs in {@code s} are cleared.
     * If available fast access memory is not low then this simply returns
     * {@code true}. If available fast access memory is low, then an attempt is
     * made to clear chunks with IDs not in {@link #notToClear}. If this is
     * unsuccessful in clearing sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then chunks with IDs in
     * {@link #notToClear} are cleared. If this is unsuccessful in clearing
     * sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then {@code false} is
     * returned.
     *
     * @param g The grid from which no chunks with chunk IDs in {@code s} are
     * cleared.
     * @param s The chunk IDs of chunks in {@code g} that are not cleared.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return {@code true} if there is sufficient memory to continue and
     * {@code false} otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public boolean checkAndMaybeFreeMemory(Grids_Grid g, Set<Grids_2D_ID_int> s,
            boolean hoome) throws IOException, Exception {
        try {
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapChunkExcept(g, s)) {
                    env.log("Warning! No data to clear in "
                            + this.getClass().getName()
                            + ".checkAndMaybeFreeMemory(" + g.getClass().getName()
                            + "," + "Set<Grids_2D_ID_int>,boolean)");
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
                    if (swapChunkExcept(g, s)) {
                        System.out.println(
                                "Warning! Nothing to cache in "
                                + this.getClass().getName()
                                + ".checkAndMaybeFreeMemory(" + g.getClass().getName()
                                + ",Set<ChunkID>,boolean) after encountering "
                                + "an OutOfMemoryError");
                        throw e;
                    }
                    initMemoryReserve(g, s, hoome);
                    checkAndMaybeFreeMemory(g, s, hoome);
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
     * file. No chunks from {@code g} with chunk IDs in {@code s} are cleared.
     * If available fast access memory is not low then this simply returns
     * {@code true}. If available fast access memory is low, then an attempt is
     * made to clear chunks with IDs not in {@link #notToClear}. If this is
     * unsuccessful in clearing sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then chunks with IDs in
     * {@link #notToClear} are cleared. If this is unsuccessful in clearing
     * sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then {@code false} is
     * returned.
     *
     * @param g The grid from which no chunks with chunk IDs in {@code s} are
     * cleared.
     * @param s The chunk IDs of chunks in {@code g} that are not cleared.
     * @return {@code true} if there is sufficient memory to continue and
     * {@code false} otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected boolean checkAndMaybeFreeMemory(Grids_Grid g,
            Set<Grids_2D_ID_int> s) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            addToNotToClear(g, s);
            do {
                if (swapChunkExcept(notToClear)) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        return true;
                    }
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                if (swapChunkExcept(g, s)) {
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
     * file. If available fast access memory is not low then this simply returns
     * {@code true}. If available fast access memory is low, then an attempt is
     * made to clear chunks with IDs not in {@link #notToClear}. If this is
     * unsuccessful in clearing sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then chunks with IDs in
     * {@link #notToClear} are cleared. If this is unsuccessful in clearing
     * sufficient memory such that
     * {@code getTotalFreeMemory() < Memory_Threshold}, then {@code false} is
     * returned.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return The number of chunks cleared.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public Grids_Account checkAndMaybeFreeMemory_Account(
            boolean hoome) throws IOException, Exception {
        try {
            Grids_Account test = checkAndMaybeFreeMemory_Account();
            if (!test.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account(boolean)";
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_Account r = checkAndMaybeFreeMemory_Account(hoome);
                r.add(initMemoryReserve_Account(hoome));
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
     * @return Grids_Account of data cached.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_Account checkAndMaybeFreeMemory_Account() throws IOException,
            Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_Account r = new Grids_Account();
            do {
                if (Grids_Environment.this.swapChunkExcept(notToClear)) {
                    r.detail++;
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                r.success = true;
            } else {
                do {
                    if (swapChunk()) {
                        r.detail++;
                    } else {
                        break;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                r.success = getTotalFreeMemory() < Memory_Threshold;
            }
            return r;
        } else {
            return null;
        }
    }

    /**
     * A method to check and maybe free fast access memory by swapping chunks.
     * This method may throw an OutOfMemoryError if there are not chunks to swap
     * and memory is low.
     *
     * @param g A grid from which chunks are not swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public Grids_Account checkAndMaybeFreeMemory_Account(Grids_Grid g,
            boolean hoome) throws IOException, Exception {
        try {
            Grids_Account r = checkAndMaybeFreeMemory_Account(g);
            if (r == null) {
                return r;
            }
            if (!r.success) {
                String message = "No data to clear.";
                //System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_Account r = checkAndMaybeFreeMemory_Account(g, hoome);
                r.add(initMemoryReserve_Account(g, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by swapping chunks.
     *
     * @param g A grid from which chunks are not swapped.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_Account checkAndMaybeFreeMemory_Account(Grids_Grid g)
            throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_Account r = new Grids_Account();
            addToNotToClear(g);
            do {
                if (Grids_Environment.this.swapChunkExcept(notToClear)) {
                    r.detail++;
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                r.success = true;
            } else {
                do {
                    if (Grids_Environment.this.swapChunkExcept(g)) {
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
     * file. This method may throw an OutOfMemoryError if there are not chunks
     * to swap and memory is low.
     *
     * @param g A grid from which the chunk with ID {@code i} is not swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @param i The chunk ID of a chunk in {@code g} that is not to be swapped.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public Grids_Account checkAndMaybeFreeMemory_Account(Grids_Grid g,
            Grids_2D_ID_int i, boolean hoome) throws IOException, Exception {
        try {
            Grids_Account r = checkAndMaybeFreeMemory_Account(g, i);
            if (r == null) {
                return r;
            }
            if (!r.success) {
                String message = "Not enough data to clear.";
                //System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_Account r = checkAndMaybeFreeMemory_Account(g, i, hoome);
                r.add(initMemoryReserve_Account(g, i, hoome));
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
     * @param g A grid from which the chunk with ID {@code i} is not swapped.
     * @param i The chunk ID of a chunk in {@code g} that is not to be swapped.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_Account checkAndMaybeFreeMemory_Account(Grids_Grid g,
            Grids_2D_ID_int i) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_Account r = new Grids_Account();
            addToNotToClear(g, i);
            do {
                if (swapChunkExcept(notToClear)) {
                    r.detail++;
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                r.success = true;
            } else {
                do {
                    if (swapChunkExcept(g, i)) {
                        break;
                    } else {
                        r.add();
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
     * file. This method may throw an OutOfMemoryError if there are not chunks
     * to swap and memory is low.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @param i The chunk ID of a chunks that are not to be swapped.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public Grids_Account checkAndMaybeFreeMemory_Account(Grids_2D_ID_int i,
            boolean hoome) throws IOException, Exception {
        try {
            Grids_Account r = checkAndMaybeFreeMemory_Account(i);
            if (r == null) {
                return r;
            }
            if (!r.success) {
                String message = "Not enough data to clear.";
                //System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_Account r = checkAndMaybeFreeMemory_Account(i, hoome);
                r.add(initMemoryReserve_Account(i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. This method may throw an OutOfMemoryError if there are not chunks
     * to swap and memory is low.
     *
     * @param i The chunk ID of a chunks that are not to be swapped.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_Account checkAndMaybeFreeMemory_Account(Grids_2D_ID_int i)
            throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_Account r = new Grids_Account();
            Iterator<Grids_Grid> ite = grids.iterator();
            while (ite.hasNext()) {
                Grids_Grid g = ite.next();
                addToNotToClear(g, i);
                if (swapChunkExcept(notToClear)) {
                    r.add();
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            }
            ite = grids.iterator();
            while (ite.hasNext()) {
                Grids_Grid g = ite.next();
                if (swapChunkExcept(g, i)) {
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
     * file. This method may throw an OutOfMemoryError if there are not chunks
     * to swap and memory is low.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @param m A map indicating the chunks that are not to be swapped.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public Grids_Account checkAndMaybeFreeMemory_Account(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m, boolean hoome)
            throws IOException, Exception {
        try {
            Grids_Account r = checkAndMaybeFreeMemory_Account(m);
            if (r == null) {
                return r;
            }
            if (!r.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account("
                        + m.getClass().getName() + ",boolean)";
                //System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_Account r = checkAndMaybeFreeMemory_Account(m, hoome);
                r.add(initMemoryReserve_Account(m, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. This method may throw an OutOfMemoryError if there are not chunks
     * to swap and memory is low.
     *
     * @param m A map indicating the chunks that are not to be swapped.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_Account checkAndMaybeFreeMemory_Account(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) throws IOException,
            Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_Account r = new Grids_Account();
            addToNotToClear(m);
            do {
                if (swapChunkExcept(notToClear)) {
                    r.add(1);
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                r.success = true;
            } else {
                do {
                    if (swapChunkExcept(m)) {
                        r.add(1);
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
     * file. This method may throw an OutOfMemoryError if there are not chunks
     * to swap and memory is low.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @param g The grid from which no chunks in {@code s} are swapped.
     * @param s The chunk IDs which are not to be swapped from {@code g}.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public Grids_Account checkAndMaybeFreeMemory_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> s, boolean hoome) throws IOException, Exception {
        try {
            Grids_Account a = checkAndMaybeFreeMemory_Account(g, s);
            if (!a.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account("
                        + g.getClass().getName() + ","
                        + s.getClass().getName() + ",boolean)";
                //System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return a;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                return freeSomeMemoryAndResetReserve_Account(g, s, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file.
     *
     * @param g The grid from which no chunks in {@code s} are swapped.
     * @param s The chunk IDs which are not to be swapped from {@code g}.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_Account checkAndMaybeFreeMemory_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> s) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_Account r = new Grids_Account();
            addToNotToClear(g, s);
            do {
                if (swapChunkExcept(notToClear)) {
                    r.add();
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                r.success = true;
            } else {
                do {
                    if (swapChunkExcept(g, s)) {
                        r.add();
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
     * file. This method may throw an OutOfMemoryError if there are not chunks
     * to swap and memory is low.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            boolean hoome) throws IOException, Exception {
        try {
            Grids_AccountDetail a = checkAndMaybeFreeMemory_AccountDetail();
            if (!a.success) {
                String message = "Warning! Not enough data to cache in "
                        + getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + "boolean)";
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return a;
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
     * quickly return {@code null} if there is enough memory to continue. If
     * there is not enough memory to continue it will attempt to make room and
     * will pass back a detailed account of this and an indication if there is
     * enough memory to continue.
     *
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail()
            throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_AccountDetail r = new Grids_AccountDetail();
            Grids_AccountDetail pr;
            do {
                pr = swapChunkExcept_AccountDetail(notToClear);
                if (pr.detail.isEmpty()) {
                    break;
                } else {
                    r.add(pr);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                pr = swapChunk_AccountDetail();
                if (pr.detail.isEmpty()) {
                    break;
                } else {
                    r.add(pr);
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
     * quickly return null if there is enough memory to continue. This method
     * may throw an OutOfMemoryError if sufficient memory cannot be made
     * available be swapping chunks.
     *
     * @param g No chunks from this grid are swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            Grids_Grid g, boolean hoome) throws IOException, Exception {
        try {
            Grids_AccountDetail r = checkAndMaybeFreeMemory_AccountDetail(g);
            if (r == null) {
                return null;
            }
            if (!r.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + g.getClass().getName() + ",boolean)";
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return r;
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
     * quickly return null if there is enough memory to continue. This method
     * may throw an OutOfMemoryError if sufficient memory cannot be made
     * available be swapping chunks.
     *
     * @param g No chunks from this grid are swapped.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            Grids_Grid g) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_AccountDetail r = new Grids_AccountDetail();
            addToNotToClear(g);
            Grids_AccountDetail pr;
            do {
                pr = swapChunkExcept_AccountDetail(notToClear);
                if (pr.detail.isEmpty()) {
                    break;
                } else {
                    r.add(pr);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                pr = swapChunkExcept_AccountDetail(g);
                if (pr.detail.isEmpty()) {
                    break;
                } else {
                    r.add(pr);
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
     * quickly return null if there is enough memory to continue. This method
     * may throw an OutOfMemoryError if sufficient memory cannot be made
     * available be swapping chunks.
     *
     * @param g The grid in which chunk with chunk ID {@code i} is not swapped.
     * @param i The chunk ID of the chunk in {@code g} that is not to be
     * swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            Grids_Grid g, Grids_2D_ID_int i, boolean hoome) throws IOException,
            Exception {
        try {
            Grids_AccountDetail r = checkAndMaybeFreeMemory_AccountDetail(g, i);
            if (r == null) {
                return null;
            }
            if (!r.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + g.getClass().getName() + ","
                        + i.getClass().getName() + ",boolean)";
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_AccountDetail r = checkAndMaybeFreeMemory_AccountDetail(g, i, hoome);
                r.add(initMemoryReserve_AccountDetail(g, i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. This method
     * may throw an OutOfMemoryError if sufficient memory cannot be made
     * available be swapping chunks.
     *
     * @param g The grid in which chunk with chunk ID {@code i} is not swapped.
     * @param i The chunk ID of the chunk in {@code g} that is not to be
     * swapped.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            Grids_Grid g, Grids_2D_ID_int i) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_AccountDetail r = new Grids_AccountDetail();
            addToNotToClear(g, i);
            Grids_AccountDetail pr;
            do {
                pr = swapChunkExcept_AccountDetail(notToClear);
                if (pr.detail.isEmpty()) {
                    break;
                } else {
                    r.add(pr);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                pr = swapChunkExcept_AccountDetail(g, i);
                if (pr.detail.isEmpty()) {
                    break;
                } else {
                    r.add(pr);
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
     * quickly return null if there is enough memory to continue. This method
     * may throw an OutOfMemoryError if sufficient memory cannot be made
     * available be swapping chunks.
     *
     * @param i The chunk ID that is not to be swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            Grids_2D_ID_int i, boolean hoome) throws IOException, Exception {
        try {
            Grids_AccountDetail r = checkAndMaybeFreeMemory_AccountDetail(i);
            if (r == null) {
                return null;
            }
            if (!r.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + i.getClass().getName() + ",boolean)";
                //System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_AccountDetail r = checkAndMaybeFreeMemory_AccountDetail(
                        i, hoome);
                r.add(initMemoryReserve_AccountDetail(i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. This method
     * may throw an OutOfMemoryError if sufficient memory cannot be made
     * available be swapping chunks.
     *
     * @param i The chunk ID that is not to be swapped.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            Grids_2D_ID_int i) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_AccountDetail r = new Grids_AccountDetail();
            Iterator<Grids_Grid> ite = grids.iterator();
            while (ite.hasNext()) {
                Grids_Grid g = ite.next();
                addToNotToClear(g, i);
                Grids_AccountDetail pr;
                do {
                    pr = swapChunkExcept_AccountDetail(notToClear);
                    if (pr.detail.isEmpty()) {
                        break;
                    } else {
                        r.add(pr);
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
                Grids_AccountDetail pr;
                do {
                    pr = swapChunkExcept_AccountDetail(g, i);
                    if (pr.detail.isEmpty()) {
                        break;
                    } else {
                        r.add(pr);
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
     * quickly return null if there is enough memory to continue. This method
     * may throw an OutOfMemoryError if sufficient memory cannot be made
     * available be swapping chunks.
     *
     * @param m Indicates the chunks ID that are not to be swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m, boolean hoome)
            throws IOException, Exception {
        try {
            Grids_AccountDetail r = checkAndMaybeFreeMemory_AccountDetail(m);
            if (r == null) {
                return null;
            }
            if (r.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + m.getClass().getName() + ",boolean)";
                //System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_AccountDetail r = checkAndMaybeFreeMemory_AccountDetail(m, hoome);
                r.add(initMemoryReserve_AccountDetail(m, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. This method
     * may throw an OutOfMemoryError if sufficient memory cannot be made
     * available be swapping chunks.
     *
     * @param m Indicates the chunks ID that are not to be swapped.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) throws IOException,
            Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_AccountDetail r = new Grids_AccountDetail();
            addToNotToClear(m);
            Grids_AccountDetail pr;
            do {
                pr = swapChunkExcept_AccountDetail(notToClear);
                if (pr.detail.isEmpty()) {
                    break;
                } else {
                    r.add(pr);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                pr = swapChunkExcept_AccountDetail(m);
                if (pr.detail.isEmpty()) {
                    break;
                } else {
                    r.add(pr);
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
     * For checking available memory and maybe freeing some memory. This method
     * will quickly return null if there is enough memory to continue. If there
     * is not enough memory to continue then there is an attempt to make room.
     * The chunks in grid {@code g} with chunk IDs in {@code s} are not swapped.
     *
     * @param g A grid in which chunks in {@code s} are not swapped.
     * @param s A set of chunk IDs for which chunks in {@code g} are not
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(Grids_Grid g,
            Set<Grids_2D_ID_int> s, boolean hoome) throws IOException,
            Exception {
        try {
            Grids_AccountDetail r = checkAndMaybeFreeMemory_AccountDetail(g, s);
            if (r == null) {
                return null;
            }
            if (!r.success) {
                String message = "Warning! Not enough data to cache in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + g.getClass().getName() + ","
                        + s.getClass().getName() + ",boolean)";
                //System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                return freeSomeMemoryAndResetReserve_AccountDetails(g, s,
                        hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * For checking available memory and maybe freeing some memory. This method
     * will quickly return null if there is enough memory to continue. If there
     * is not enough memory to continue then there is an attempt to make room.
     * The chunks in grid {@code g} with chunk IDs in {@code s} are not swapped.
     *
     * @param g A grid in which chunks in {@code s} are not swapped.
     * @param s A set of chunk IDs for which chunks in {@code g} are not
     * swapped.
     * @return A detailed account of any chunks cleared.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            Grids_Grid g, Set<Grids_2D_ID_int> s) throws IOException, Exception {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Grids_AccountDetail r = new Grids_AccountDetail();
            addToNotToClear(g, s);
            Grids_AccountDetail pr;
            do {
                pr = swapChunkExcept_AccountDetail(notToClear);
                if (pr.detail.isEmpty()) {
                    break;
                } else {
                    r.add(pr);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        r.success = true;
                        return r;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                pr = swapChunkExcept_AccountDetail(g, s);
                if (pr.detail.isEmpty()) {
                    break;
                } else {
                    r.add(pr);
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
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory. OutOfMemoryErrors are caught and thrown.
     * @return A detailed account of any chunks cleared.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_AccountDetail swapChunks_AccountDetail(
            boolean hoome) throws IOException, Exception, Exception {
        try {
            Grids_AccountDetail r = swapChunks_AccountDetail(notToClear);
            try {
                if (r.detail.isEmpty()) {
                    r = checkAndMaybeFreeMemory_AccountDetail();
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_AccountDetail(hoome));
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
                return swapChunks_AccountDetail(hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap all chunks in this grid.
     *
     * @return A detailed account of any chunks cleared.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail swapChunks_AccountDetail() throws IOException,
            Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            r.add(ite.next().swapChunks_AccountDetail());
        }
        return r;
    }

    /**
     * Attempts to cache all chunks in {@link #grids} excpet those in {@code m}.
     *
     * @param m A map of chunks not to swap.
     * @return A detailed account of any chunks cleared.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail swapChunks_AccountDetail(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) throws IOException,
            Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            g.swapChunksExcept(m.get(g), r);
        }
        return r;
    }

    /**
     * Attempts to swap all chunks in {@link #grids}.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory. OutOfMemoryErrors are caught and thrown.
     * @return An account of the chunks swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_Account swapChunks_Account(boolean hoome) throws IOException,
            Exception {
        try {
            Grids_Account r;
            try {
                r = swapChunks_Account();
                if (r.detail < 1) {
                    r = checkAndMaybeFreeMemory_Account();
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_Account(hoome));
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
                Grids_Account r = freeSomeMemoryAndResetReserve_Account(e, hoome);
                r.add(swapChunks_Account(hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to cache all chunks in {@link #grids}.
     *
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_Account swapChunks_Account() throws IOException, Exception {
        Grids_Account r = new Grids_Account();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            r.add(ite.next().swapChunks_Account());
        }
        dataToClear = false;
        return r;
    }

    /**
     * Attempts to cache all Grids_Chunk in this.grids.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public void swapChunks(boolean hoome) throws IOException, Exception {
        try {
            boolean success = swapChunks();
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
                if (!swapChunk()) {
                    throw e;
                }
                initMemoryReserve(env);
                swapChunks();
            } else {
                throw e;
            }
        }
    }

    /**
     * Swaps all chunks in {@link #grids}.
     *
     * @return {@code true}
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected boolean swapChunks() throws IOException, Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            ite.next().swapChunks();
        }
        dataToClear = false;
        return true;
    }

    /**
     * Attempts to swap any chunk.
     *
     * @return A detailed account of any swapping.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_AccountDetail swapChunk_AccountDetail(boolean hoome)
            throws IOException, Exception {
        try {
            Grids_AccountDetail r = swapChunk_AccountDetail();
            try {
                if (r.detail.isEmpty()) {
                    r = checkAndMaybeFreeMemory_AccountDetail();
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_AccountDetail(hoome));
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
     * Attempts to swap a chunk in {@link #grids} trying first not to cache any
     * in {@link #notToClear}.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return {@code true} if a chunk is swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public boolean swapChunk(boolean hoome) throws IOException, Exception {
        try {
            boolean success = swapChunk();
            try {
                if (!success) {
                    Grids_Account account = checkAndMaybeFreeMemory_Account();
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
                if (!swapChunk()) {
                    throw e;
                }
                initMemoryReserve(env);
                // No need for recursive call: swap(hoome);
                return true;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap a chunk in {@link #grids} trying first not to cache any
     * in {@link #notToClear}.
     *
     * @return {@code true} if a chunk is swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected boolean swapChunk() throws IOException, Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            if (notToClear.containsKey(g)) {
                Set<Grids_2D_ID_int> s = notToClear.get(g);
                if (!s.isEmpty()) {
                    Grids_2D_ID_int i = g.swapChunkExcept_AccountChunk(s);
                    if (i != null) {
                        return true;
                    }
                }
            } else {
                if (g.swapChunk() != null) {
                    return true;
                }
            }
        }
        dataToClear = false;
        return false;
    }

    /**
     * Swap a chunk in {@link #grids} except one in {@code g}.
     *
     * @param g The grid not to swap chunks from.
     * @param camfm If {@code true} then a check and maybe clear some memory
     * operation is performed.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from
     * memory.
     * @return {@code true} if a chunk is successfully swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public boolean swapChunkExcept(Grids_Grid g, boolean camfm, boolean hoome)
            throws IOException, Exception {
        try {
            boolean r = swapChunkExcept(g);
            if (camfm) {
                try {
                    checkAndMaybeFreeMemory();
                } catch (OutOfMemoryError e) {
                    hoome = false;
                    throw e;
                }
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                if (!Grids_Environment.this.swapChunkExcept(g)) {
                    throw e;
                }
                if (camfm) {
                    checkAndMaybeFreeMemory();
                }
                initMemoryReserve(g, hoome);
                return true;
            } else {
                throw e;
            }
        }
    }

    /**
     * Swap a chunk in {@link #grids} except one in {@code g}.
     *
     * @param g The grid not to swap chunks from.
     * @return {@code true} if a chunk is successfully swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected boolean swapChunkExcept(Grids_Grid g) throws IOException,
            Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid bg = ite.next();
            if (bg != g) {
                if (bg.swapChunk() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Swap a chunk in {@link #grids} except one in {@code g}.
     *
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail swapChunk_AccountDetail() throws IOException,
            Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            if (notToClear.containsKey(g)) {
                Grids_AccountDetail r = g.swapChunkExcept_AccountDetail(
                        notToClear.get(g));
                if (!r.detail.isEmpty()) {
                    return r;
                }
            }
        }
        dataToClear = false;
        return null;
    }

    /**
     * Swap a chunk in {@link #grids} except those with chunk ID {@code i}.
     *
     * @param i The chunk ID for chunks not to swap.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_AccountDetail swapChunkExcept_AccountDetail(
            Grids_2D_ID_int i, boolean hoome) throws IOException, Exception {
        try {
            Grids_AccountDetail r = swapChunkExcept_AccountDetail(i);
            try {
                if (r.detail.isEmpty()) {
                    r = checkAndMaybeFreeMemory_AccountDetail(i);
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_AccountDetail(i, hoome));
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
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(i);
                if (r.detail.isEmpty()) {
                    throw e;
                }
                r.add(initMemoryReserve_AccountDetail(i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Swap a chunk in {@link #grids} except those with chunk ID {@code i}.
     *
     * @param i The chunk ID for chunks not to swap.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail swapChunkExcept_AccountDetail(
            Grids_2D_ID_int i) throws IOException, Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            Grids_AccountDetail r = g.swapChunkExcept_AccountDetail(i);
            if (!r.detail.isEmpty()) {
                Set<Grids_2D_ID_int> s = new HashSet<>(1);
                s.add(i);
                r.detail.put(g, s);
                return r;
            }
        }
        return null;
    }

    /**
     * Swap a chunk in {@link #grids} except those with chunk ID {@code i}.
     *
     * @param i The chunk ID for chunks not to swap.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from
     * memory.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_Account swapChunkExcept_Account(Grids_2D_ID_int i, boolean hoome)
            throws IOException, Exception {
        try {
            Grids_Account r = swapChunkExcept_Account(i);
            try {
                if (r.detail < 1) {
                    r = checkAndMaybeFreeMemory_Account(i);
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_Account(i, hoome));
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            r.add(checkAndMaybeFreeMemory_Account(hoome));
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_Account r = swapChunkExcept_Account(i);
                if (r.detail < 1L) {
                    throw e;
                }
                r.add(initMemoryReserve_Account(i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Swap a chunk in {@link #grids} except those with chunk ID {@code i}.
     *
     * @param i The chunk ID for chunks not to swap.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_Account swapChunkExcept_Account(Grids_2D_ID_int i)
            throws IOException, Exception {
        Grids_Account r = new Grids_Account();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            addToNotToClear(g, i);
            if (swapChunkExcept(notToClear)) {
                r.add();
                return r;
            }
        }
        return r;
    }

    /**
     * Swap a chunk in {@link #grids} except those with chunk ID {@code i}.
     *
     * @param i The chunk ID for chunks not to swap.
     * @return {@code true} if a chunk is successfully swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected boolean swapChunkExcept(Grids_2D_ID_int i) throws IOException,
            Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            if (swapChunkExcept(g, i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Swap a chunk in {@link #grids} except those in {@code m}.
     *
     * @param m Indicates the chunk IDs for chunks not to swap.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_AccountDetail swapChunkExcept_AccountDetail(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m, boolean hoome)
            throws IOException, Exception {
        try {
            Grids_AccountDetail r = swapChunkExcept_AccountDetail(m);
            try {
                if (r.detail.isEmpty()) {
                    r = checkAndMaybeFreeMemory_AccountDetail(m);
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_AccountDetail(m, hoome));
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
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(m);
                if (r.detail.isEmpty()) {
                    throw e;
                }
                r.add(initMemoryReserve_AccountDetail(m, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Swap a chunk in {@link #grids} except those in {@code m}.
     *
     * @param m Indicates the chunk IDs for chunks not to swap.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail swapChunkExcept_AccountDetail(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) throws IOException,
            Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        Iterator<Grids_Grid> ite = grids.iterator();
        Set<Grids_2D_ID_int> s = new HashSet<>();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            if (m.containsKey(g)) {
                Grids_2D_ID_int i = g.swapChunkExcept_AccountChunk(m.get(g));
                if (i != null) {
                    s.add(i);
                    r.detail.put(g, s);
                    return r;
                }
            }
            Grids_2D_ID_int i = g.swapChunk_AccountChunk();
            if (i != null) {
                s.add(i);
                r.detail.put(g, s);
                return r;
            }
        }
        return r; // If here then nothing could be cached!
    }

    /**
     * Swap a chunk in {@link #grids} except those in {@code g} with chunk IDs
     * in {@code s}.
     *
     * @param g The grid in which the chunks in {@code s} are not to be swapped.
     * @param s Identifies the chunks in {@code g} not to be swapped.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail swapChunkExcept_AccountDetail(Grids_Grid g,
            Set<Grids_2D_ID_int> s) throws IOException, Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        Iterator<Grids_Grid> ite = grids.iterator();
        Set<Grids_2D_ID_int> s2 = new HashSet<>();
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (g == gb) {
                Grids_2D_ID_int i = gb.swapChunkExcept_AccountChunk(s);
                if (i != null) {
                    s2.add(i);
                    r.detail.put(g, s2);
                    return r;
                }
            } else {
                Grids_2D_ID_int i = g.swapChunk_AccountChunk();
                if (i != null) {
                    s2.add(i);
                    r.detail.put(g, s2);
                    return r;
                }
            }
        }
        return r; // If here then nothing could be cleared!
    }

    /**
     * Swap a chunk in {@link #grids} except those in {@code g} with chunk ID
     * {@code i}.
     *
     * @param g The grid in which the chunks in {@code s} are not to be swapped.
     * @param i Identifies the chunk in {@code g} not to be swapped.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail swapChunkExcept_AccountDetail(Grids_Grid g,
            Grids_2D_ID_int i) throws IOException, Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        Iterator<Grids_Grid> ite = grids.iterator();
        Set<Grids_2D_ID_int> s = new HashSet<>(1);
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (g == gb) {
                Grids_2D_ID_int i2 = gb.swapChunkExcept_AccountChunk(i);
                if (i2 != null) {
                    s.add(i2);
                    r.detail.put(g, s);
                    return r;
                }
            } else {
                Grids_2D_ID_int i2 = g.swapChunk_AccountChunk();
                if (i2 != null) {
                    s.add(i2);
                    r.detail.put(g, s);
                    return r;
                }
            }
        }
        return r; // If here then nothing could be cached!
    }

    /**
     * Swap a chunk in {@link #grids} except those in {@code g} with chunk ID
     * {@code i}.
     *
     * @param g The grid in which the chunks in {@code s} are not to be swapped.
     * @param i Identifies the chunk in {@code g} not to be swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_AccountDetail swapChunkExcept_AccountDetail(Grids_Grid g,
            Grids_2D_ID_int i, boolean hoome) throws IOException, Exception {
        try {
            Grids_AccountDetail r = swapChunkExcept_AccountDetail(g, i);
            return r;
        } catch (java.lang.OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve(env);
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(g, i);
                if (r.detail.isEmpty()) {
                    throw e;
                }
                r.add(initMemoryReserve_AccountDetail(g, i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Swap a chunk in {@link #grids} except those in {@code g}.
     *
     * @param g The grid with chunks not to swap.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail swapChunkExcept_AccountDetail(Grids_Grid g)
            throws IOException, Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        Iterator<Grids_Grid> ite = grids.iterator();
        Set<Grids_2D_ID_int> rp = new HashSet<>(1);
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (g != gb) {
                Grids_2D_ID_int i = gb.swapChunk_AccountChunk();
                if (i != null) {
                    rp.add(i);
                    r.detail.put(g, rp);
                    return r;
                }
            }
        }
        return r; // If here then nothing could be cached!
    }

    /**
     * Swap a chunk in {@link #grids} except those in {@code m}.
     *
     * @param m Identifies the chunks not to be swapped.
     * @return {@code true} if a chunk was swapped and {@code false} otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     *
     */
    protected boolean swapChunkExcept(HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m)
            throws IOException, Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            if (m.containsKey(g)) {
                Grids_2D_ID_int i = g.swapChunkExcept_AccountChunk(m.get(g));
                if (i != null) {
                    return true;
                }
            }
            Grids_2D_ID_int i = g.swapChunk_AccountChunk();
            if (i != null) {
                return true;
            }
        }
        return false; // If here then nothing could be cleared!
    }

    /**
     * Swap a chunk in {@link #grids} except those in {@code g} with chunk IDs
     * in {@code s}.
     *
     * @param g The grid that's chunks with chunk IDs in {@code s} are not to be
     * swapped.
     * @param s The chunk IDs of chunks not to be swapped from {@code g}.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_Account swapChunkExcept_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> s, boolean hoome) throws IOException,
            Exception {
        try {
            addToNotToClear(g, s);
            Grids_Account r = swapChunkExcept_Account(g);
            try {
                if (r.detail < 1) {
                    if (g.swapChunkExcept_AccountChunk(s) != null) {
                        r.add();
                    }
                    r = checkAndMaybeFreeMemory_Account(g, s);
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_Account(g, s, hoome));
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
                Grids_Account r = swapChunkExcept_Account(g);
                if (r.detail < 1L) {
                    throw e;
                }
                r.add(initMemoryReserve_Account(g, s, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Swap a chunk in {@link #grids} except those in {@code g} with chunk IDs
     * in {@code s}.
     *
     * @param g The grid that's chunks with chunk IDs in {@code s} are not to be
     * swapped.
     * @param s The chunk IDs of chunks not to be swapped from {@code g}.
     * @return {@code true} if a chunk was successfully swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected boolean swapChunkExcept(Grids_Grid g,
            Set<Grids_2D_ID_int> s) throws IOException, Exception {
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g2 = ite.next();
            if (g2 != g) {
                TreeMap<Grids_2D_ID_int, Grids_Chunk> m = g2.getData();
                Set<Grids_2D_ID_int> s2 = m.keySet();
                Iterator<Grids_2D_ID_int> iteb = s2.iterator();
                while (iteb.hasNext()) {
                    Grids_2D_ID_int i = iteb.next();
                    if (!s.contains(i)) {
                        //Check it can be cached
                        if (m.get(i) != null) {
                            if (g2.swapChunk(i)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Swap a chunk in {@link #grids} except that in {@code g} with chunk ID
     * {@code i}.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @param camfm If {@code true} that there is a check that may free memory.
     * @return An account of any swapping.
     * @param g A grid from which the chunk with chunk ID {@code i} is not
     * swapped.
     * @param i The chunk ID of the chunk in {@code g} that is not to be
     * swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_Account swapChunkExcept_Account(Grids_Grid g, Grids_2D_ID_int i,
            boolean camfm, boolean hoome) throws IOException, Exception {
        try {
            Grids_Account r = new Grids_Account();
            if (swapChunkExcept(g, i)) {
                r.add();
            }
            try {
                if (camfm) {
                    if (r.detail < 1L) {
                        r.add(checkAndMaybeFreeMemory_Account(g, i));
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    } else {
                        r.add(checkAndMaybeFreeMemory_Account(g, i, hoome));
                    }
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
                Grids_Account r = new Grids_Account();
                if (swapChunkExcept(g, i)) {
                    r.add();
                } else {
                    throw e;
                }
                r.add(initMemoryReserve_Account(g, i, hoome));
                r.add(checkAndMaybeFreeMemory_Account(g, i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Swap a chunk in {@link #grids} except that in {@code g} with chunk ID
     * {@code i}. First attempts to swap a chunk from a grid other than
     * {@code g} - if nothing is swapped, then an attempt is made to swap a
     * chunk in {@code g} other than that with chunk ID {@code i}.
     *
     * @param g The grid from which chunks are preferred not to be swapped.
     * @param i The chunk ID of the chunk in {@code g} not to be cleared.
     * @return {@code true} if a chunk is swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected boolean swapChunkExcept(Grids_Grid g,
            Grids_2D_ID_int i) throws IOException, Exception {
        if (swapChunkExcept(g)) {
            return true;
        }
        return g.swapChunkExcept(i);
    }

    /**
     * Swap a chunk in {@link #grids} except those in {@code g}.
     *
     * @param g Grids_Grid that's chunks are not to be cached.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_Account swapChunkExcept_Account(Grids_Grid g, boolean hoome)
            throws IOException, Exception {
        try {
            Grids_Account r = swapChunkExcept_Account(g);
            try {
                if (r.detail < 1) {
                    r = checkAndMaybeFreeMemory_Account(g);
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_Account(g, hoome));
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
                Grids_Account r = swapChunkExcept_Account(g);
                if (r.detail < 1L) {
                    throw e;
                }
                r.add(initMemoryReserve_Account(g, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Swap a chunk in {@link #grids} except that in {@code g}.
     *
     * @param g A grid that's chunks are not to be swapped.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_Account swapChunkExcept_Account(Grids_Grid g)
            throws IOException, Exception {
        Grids_Account r = new Grids_Account();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (gb != g) {
                Grids_2D_ID_int i = gb.swapChunk_AccountChunk();
                if (i != null) {
                    r.add();
                    return r;
                }
            }
        }
        return r;
    }

    /**
     * Attempts to swap all chunks except those with chunk ID {@code i}.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @param i The chunk ID of chunks not to be swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_AccountDetail swapChunksExcept_AccountDetail(Grids_2D_ID_int i,
            boolean hoome) throws IOException, Exception {
        try {
            Grids_AccountDetail r = swapChunksExcept_AccountDetail(i);
            try {
                if (r.detail.isEmpty()) {
                    r = checkAndMaybeFreeMemory_AccountDetail(i);
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_AccountDetail(i, hoome));
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
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(i);
                if (r.detail.isEmpty()) {
                    throw e;
                }
                r.add(initMemoryReserve_AccountDetail(i, hoome));
                r.add(swapChunksExcept_AccountDetail(i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap all chunks except those with chunk ID {@code i}.
     *
     * @return A detailed account of any swapping.
     * @param i The chunk ID of chunks not to be swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail swapChunksExcept_AccountDetail(
            Grids_2D_ID_int i) throws IOException, Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            r.add(g.swapChunksExcept_AccountDetail(i));
        }
        return r;
    }

    /**
     * Attempts to swap all chunks except those in grid {@code g}.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @param g The grid that's chunks are not to be swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_AccountDetail swapChunksExcept_AccountDetail(Grids_Grid g,
            boolean hoome) throws IOException, Exception {
        try {
            Grids_AccountDetail r = swapChunksExcept_AccountDetail(g);
            try {
                if (r.detail.isEmpty()) {
                    r = checkAndMaybeFreeMemory_AccountDetail(g);
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_AccountDetail(g, hoome));
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
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(g);
                if (r.detail.isEmpty()) {
                    throw e;
                }
                r.add(initMemoryReserve_AccountDetail(g, hoome));
                r.add(swapChunksExcept_AccountDetail(g, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap all chunks except those in grid {@code g}.
     *
     * @return A detailed account of any swapping.
     * @param g The grid that's chunks are not to be swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail swapChunksExcept_AccountDetail(Grids_Grid g)
            throws IOException, Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (gb != g) {
                r.add(gb.swapChunks_AccountDetail());
            }
        }
        return r;
    }

    /**
     * Attempts to swap all chunks except those in grid {@code g}.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return An account of any swapping.
     * @param g The grid that's chunks are not to be swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_Account swapChunksExcept_Account(Grids_Grid g, boolean hoome)
            throws IOException, Exception {
        try {
            Grids_Account r = swapChunksExcept_Account(g);
            try {
                if (r.detail < 1) {
                    r = checkAndMaybeFreeMemory_Account(g);
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_Account(g, hoome));
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
                Grids_Account r = swapChunkExcept_Account(g);
                if (r.detail < 1L) {
                    throw e;
                }
                r.add(initMemoryReserve_Account(g, hoome));
                r.add(swapChunksExcept_Account(g, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap all chunks except those in grid {@code g}.
     *
     * @return An account of any swapping.
     * @param g The grid that's chunks are not to be swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_Account swapChunksExcept_Account(Grids_Grid g)
            throws IOException, Exception {
        Grids_Account r = new Grids_Account();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (gb != g) {
                r.add(gb.env.swapChunks_Account());
            }
        }
        return r;
    }

    /**
     * Attempts to swap chunks except that with chunk ID {@code i} in grid
     * {@code g}.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @param g The grid for which the chunk with chunk ID {@code i} is not
     * swapped.
     * @param i The chunk ID of the chunk in {@code g} that is not to be
     * swapped.
     * @return A detailed account of any swapping.
     *
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_AccountDetail swapChunksExcept_AccountDetail(Grids_Grid g, Grids_2D_ID_int i,
            boolean hoome) throws IOException, Exception {
        try {
            Grids_AccountDetail r = swapChunksExcept_AccountDetail(g, i);
            try {
                if (r.detail.isEmpty()) {
                    r = checkAndMaybeFreeMemory_AccountDetail(g, i);
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_AccountDetail(g, i, hoome));
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
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(g, i);
                if (r.detail.isEmpty()) {
                    throw e;
                }
                r.add(initMemoryReserve_AccountDetail(g, i, hoome));
                r.add(swapChunksExcept_AccountDetail(g, i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap chunks except that with chunk ID {@code i} in grid
     * {@code g}.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @param g The grid for which the chunk with chunk ID {@code i} is not
     * swapped.
     * @param i The chunk ID of the chunk in {@code g} that is not to be
     * swapped.
     * @return An account of any swapping.
     *
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_Account swapChunksExcept_Account(Grids_Grid g, Grids_2D_ID_int i,
            boolean hoome) throws IOException, Exception {
        try {
            Grids_Account r = swapChunksExcept_Account(g, i);
            try {
                if (r.detail < 1) {
                    r = checkAndMaybeFreeMemory_Account(g, i);
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_Account(g, i, hoome));
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
                Grids_Account r = swapChunksExcept_Account(g, i);
                if (r.detail < 1L) {
                    throw e;
                }
                r.add(initMemoryReserve_Account(i, hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap chunks except that with chunk ID {@code i} in grid
     * {@code g}.
     *
     * @param g The grid for which the chunk with chunk ID {@code i} is not
     * swapped.
     * @param i The chunk ID of the chunk in {@code g} that is not to be
     * swapped.
     * @return An account of any swapping.
     *
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_Account swapChunksExcept_Account(Grids_Grid g,
            Grids_2D_ID_int i) throws IOException, Exception {
        Grids_Account r = new Grids_Account();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (gb != g) {
                int cri0 = 0;
                int cci0 = 0;
                int cri1 = gb.getNChunkRows() - 1;
                int cci1 = gb.getNChunkCols() - 1;
                r.add(gb.swapChunks_Account(cri0, cci0, cri1, cci1));
            } else {
                r.add(gb.swapChunksExcept(i));
            }
        }
        return r;
    }

    /**
     * For swapping all chunks except that with chunk ID {@code i} in grid
     * {@code g}.
     *
     * @param g The grid for which the chunk with chunk ID {@code i} is not
     * swapped.
     * @param i The chunk ID of the chunk in {@code g} that is not to be
     * swapped.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail swapChunksExcept_AccountDetail(Grids_Grid g,
            Grids_2D_ID_int i) throws IOException, Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        Iterator<Grids_Grid> ite = grids.iterator();
        Grids_Grid bg;
        while (ite.hasNext()) {
            bg = ite.next();
            if (bg == g) {
                r.add(bg.swapChunksExcept_AccountDetail(i));
            } else {
                r.add(bg.swapChunks_AccountDetail());
            }
        }
        return r;
    }

    /**
     * For swapping all chunks except those with chunk IDs in {@code s} in grid
     * {@code g}.
     *
     * @param g The grid for which the chunks with chunk IDs in {@code s} are
     * not swapped.
     * @param s The chunk IDs of chunks in {@code g} that are not to be swapped.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail swapChunksExcept_AccountDetail(Grids_Grid g,
            Set<Grids_2D_ID_int> s) throws IOException, Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (gb != g) {
                r.add(gb.swapChunks_AccountDetail());
            } else {
                Iterator<Grids_2D_ID_int> ite2 = g.getChunkIDs().iterator();
                while (ite2.hasNext()) {
                    Grids_2D_ID_int i = ite2.next();
                    if (!s.contains(i)) {
                        r.add(swapChunksExcept_AccountDetail(i));
                    }
                }
            }
        }
        return r;
    }

    /**
     * Attempts to swap all chunks except those in {@code g} with Chunk IDs in
     * {@code s}.
     *
     * @param g The grid for which the chunks with chunk IDs in {@code s} are
     * not swapped.
     * @param s The chunk IDs of chunks in {@code g} that are not to be swapped.
     * @return An account of what was swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_Account swapChunksExcept_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> s) throws IOException, Exception {
        Grids_Account r = new Grids_Account();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid gb = ite.next();
            if (gb != g) {
                int cri0 = 0;
                int cri1 = gb.getNChunkRows() - 1;
                int cci0 = 0;
                int cci1 = gb.getNChunkCols() - 1;
                r.add(gb.swapChunks_Account(cri0, cci0, cri1, cci1));
            } else {
                r.add(gb.swapChunksExcept(s));
            }
        }
        return r;
    }

    /**
     * Attempts to swap all chunks except those in {@code m}.
     *
     * @param m Details of chunks not to swap.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return An account of what was swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_Account swapChunksExcept_Account(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m,
            boolean hoome) throws IOException, Exception {
        try {
            Grids_Account r = swapChunksExcept_Account(m);
            try {
                if (r.detail < 1) {
                    r = checkAndMaybeFreeMemory_Account(m);
                    if (r != null) {
                        if (!r.success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    r.add(checkAndMaybeFreeMemory_Account(m, hoome));
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
                Grids_Account r = swapChunksExcept_Account(m);
                if (r.detail < 1L) {
                    throw e;
                }
                r.add(initMemoryReserve_Account(m, hoome));
                r.add(swapChunksExcept_Account(m));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap all chunks except those in {@code m}.
     *
     * @param m Details of chunks not to swap.
     * @return An account of what was swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_Account swapChunksExcept_Account(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> m) throws IOException,
            Exception {
        Grids_Account r = new Grids_Account();
        Iterator<Grids_Grid> ite = grids.iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            r.add(g.swapChunksExcept(m.get(g)));
        }
        return r;
    }

    /**
     * Attempts to swap a chunk.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return {@code true} if a chunk was swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public boolean swapSomeData(boolean hoome) throws IOException, Exception {
        try {
            boolean r = swapChunk(hoome);
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
                boolean r = swapSomeData();
                initMemoryReserve(env);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap a chunk.
     *
     * @return {@code true} if a chunk was swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public boolean swapSomeData() throws IOException, Exception {
        return swapChunk();
    }

    private boolean dataToClear = true;

    public boolean isDataToClear() {
        return dataToClear;
    }

    public void setDataToClear(boolean dataToClear) {
        this.dataToClear = dataToClear;
    }

    /**
     * For freeing some memory and resetting the memory reserve.
     *
     * @param g A grid with chunks not to be swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail freeSomeMemoryAndResetReserve_AccountDetails(
            Grids_Grid g, boolean hoome) throws IOException, Exception {
        Grids_AccountDetail r = checkAndMaybeFreeMemory_AccountDetail(g, hoome);
        r.add(initMemoryReserve_AccountDetail(g, hoome));
        return r;
    }

    /**
     * For freeing some memory and resetting the memory reserve.
     *
     * @param g A grid for which chunks with chunk IDs in {@code s} are not to
     * be swapped.
     * @param s A set of chunk IDs of hunks in {@code g} not to be swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail freeSomeMemoryAndResetReserve_AccountDetails(
            Grids_Grid g, Set<Grids_2D_ID_int> s, boolean hoome)
            throws IOException, Exception {
        Grids_AccountDetail r = checkAndMaybeFreeMemory_AccountDetail(g, s,
                hoome);
        r.add(initMemoryReserve_AccountDetail(g, s, hoome));
        return r;
    }

    /**
     * For freeing some memory and resetting the memory reserve. If no chunk is
     * swapped then {@code e} is thrown.
     *
     * @param e An OutOfMemoryError that may be thrown.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail freeSomeMemoryAndResetReserve_AccountDetails(
            OutOfMemoryError e, boolean hoome) throws IOException, Exception {
        Grids_AccountDetail r = swapChunk_AccountDetail();
        if (r.detail.isEmpty()) {
            throw e;
        }
        r.add(initMemoryReserve_AccountDetail(hoome));
        return r;
    }

    /**
     * For freeing some memory and resetting the memory reserve.
     *
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_AccountDetail freeSomeMemoryAndResetReserve_AccountDetails(
            boolean hoome) throws IOException, Exception {
        Grids_AccountDetail r = checkAndMaybeFreeMemory_AccountDetail(hoome);
        r.add(initMemoryReserve_AccountDetail(hoome));
        return r;
    }

    /**
     * For freeing some memory and resetting the memory reserve.
     *
     * @param g A grid for which chunks with chunk IDs in {@code s} are not to
     * be swapped.
     * @param s A set of chunk IDs of hunks in {@code g} not to be swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_Account freeSomeMemoryAndResetReserve_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> s, boolean hoome) throws IOException,
            Exception {
        Grids_Account r = checkAndMaybeFreeMemory_Account(g, s, hoome);
        r.add(initMemoryReserve_Account(g, s, hoome));
        return r;
    }

    /**
     * For freeing some memory and resetting the memory reserve. If no chunk is
     * swapped then {@code e} is thrown.
     *
     * @param e An OutOfMemoryError that may be thrown.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    protected Grids_Account freeSomeMemoryAndResetReserve_Account(
            OutOfMemoryError e, boolean hoome) throws IOException, Exception {
        if (!swapChunk()) {
            throw e;
        }
        Grids_Account r = new Grids_Account();
        r.add();
        r.add(initMemoryReserve_Account(hoome));
        return r;
    }

    /**
     * @return {@link #notToClear}
     */
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>> getNotToClear() {
        return notToClear;
    }

}
