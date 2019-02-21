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
package uk.ac.leeds.ccg.andyt.grids.core;

import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.math.Math_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;

/**
 * This is for a shared object amongst all classes that handle
 * OutOfMemoryErrors.
 */
public class Grids_Environment
        extends Grids_OutOfMemoryErrorHandler
        implements Serializable, Grids_OutOfMemoryErrorHandlerInterface {

    /**
     * Local Directory used for caching.
     */
    protected File Directory;

    /**
     * A HashSet of Grids_AbstractGrid objects that may have data that can be
     * swapped to release memory for processing.
     */
    protected transient HashSet<Grids_AbstractGrid> Grids;

    /**
     * For indicating which chunks are not swapped to release memory for
     * processing unless desperate.
     */
    protected transient HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> NotToSwap;

    /**
     * For storing an instance of Math_BigDecimal.
     */
    protected transient Math_BigDecimal _Generic_BigDecimal;

    /**
     * For storing a Grids_Processor.
     */
    protected transient Grids_Processor Processor;

    /**
     * For storing an instance of Grids_Files.
     */
    protected transient Grids_Files Files;

    /**
     * For storing an instance of Grids_Strings.
     */
    protected transient Grids_Strings Strings;

    protected Grids_Environment() {
    }

    public Grids_Environment(File directory) {
        initMemoryReserve(Default_Memory_Threshold);
        initGrids();
        initNotToSwap();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        Directory = directory;
    }

    /**
     * @return the Processor initialising first if it is null.
     */
    public Grids_Processor getProcessor() {
        if (Processor == null) {
            Processor = new Grids_Processor(this);
        }
        return Processor;
    }

    /**
     * @param processor
     */
    public void setProcessor(Grids_Processor processor) {
        Processor = processor;
    }

    /**
     * @return the Files initialising first if it is null.
     */
    public Grids_Files getFiles() {
        if (Files == null) {
            File dataDirectory = new File(Directory, getStrings().s_data);
            dataDirectory.mkdirs();
            Files = new Grids_Files(this, dataDirectory);
        }
        return Files;
    }

    /**
     * @return Strings initialising first if it is null.
     */
    public Grids_Strings getStrings() {
        if (Strings == null) {
            Strings = new Grids_Strings();
        }
        return Strings;
    }

    /**
     * @return the _Generic_BigDecimal
     */
    public Math_BigDecimal get_Generic_BigDecimal() {
        if (_Generic_BigDecimal == null) {
            init_Generic_BigDecimal();
        }
        return _Generic_BigDecimal;
    }

    /**
     * @return the _Generic_BigDecimal
     */
    private void init_Generic_BigDecimal() {
        _Generic_BigDecimal = new Math_BigDecimal();
    }

    /**
     * Initialises Grids.
     */
    protected final void initGrids() {
        if (Grids == null) {
            Grids = new HashSet<>();
        }
    }

    /**
     * Initialise or re-initialise a store of references to data that would
     * ideally not be swapped.
     */
    public final void initNotToSwap() {
        NotToSwap = new HashMap<>();
    }

    /**
     * Adds all the chunkIDs of g to NotToSwap.
     *
     * @param g
     */
    public final void addToNotToSwap(Grids_AbstractGrid g) {
        HashSet<Grids_2D_ID_int> chunkIDs;
        chunkIDs = g.getChunkIDs();
        NotToSwap.put(g, chunkIDs);
    }

    /**
     * Removes g from the NotToSwap.
     *
     * @param g
     */
    public final void removeFromNotToSwap(Grids_AbstractGrid g) {
        NotToSwap.remove(g);
    }

    /**
     * Adds the chunkID of g to NotToSwap.
     *
     * @param g
     * @param chunkRow
     */
    public final void addToNotToSwap(
            Grids_AbstractGrid g,
            int chunkRow) {
        int nChunkCols = g.getNChunkCols();
        Grids_2D_ID_int chunkID;
        for (int chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
            chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
            addToNotToSwap(g, chunkID);
        }
    }

    /**
     * Removes the chunkID of g to NotToSwap.
     *
     * @param g
     * @param chunkRow
     */
    public final void removeFromNotToSwap(
            Grids_AbstractGrid g,
            int chunkRow) {
        int nChunkCols = g.getNChunkCols();
        Grids_2D_ID_int chunkID;
        for (int chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
            chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
            removeFromNotToSwap(g, chunkID);
        }
    }

    /**
     * Adds the chunkID of g to NotToSwap.
     *
     * @param g
     * @param chunkID
     */
    public final void addToNotToSwap(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        if (NotToSwap.containsKey(g)) {
            NotToSwap.get(g).add(chunkID);
        } else {
            HashSet<Grids_2D_ID_int> chunkIDs;
            chunkIDs = new HashSet<>();
            chunkIDs.add(chunkID);
            NotToSwap.put(g, chunkIDs);
        }
    }

    /**
     * Adds the chunkID of each grid in g to NotToSwap.
     *
     * @param g
     * @param chunkID
     */
    public final void addToNotToSwap(
            Grids_AbstractGrid[] g,
            Grids_2D_ID_int chunkID) {
        for (Grids_AbstractGrid g1 : g) {
            addToNotToSwap(g1, chunkID);
        }
    }

    /**
     * Adds m to NotToSwap.
     *
     * @param m
     */
    public final void addToNotToSwap(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        Iterator<Grids_AbstractGrid> ite;
        ite = m.keySet().iterator();
        Grids_AbstractGrid g;
        while (ite.hasNext()) {
            g = ite.next();
            if (NotToSwap.containsKey(g)) {
                NotToSwap.get(g).addAll(m.get(g));
            } else {
                NotToSwap.put(g, m.get(g));
            }
        }
    }

    /**
     * Remove the chunkID of each grid in g[] from NotToSwap.
     *
     * @param g
     * @param chunkID
     */
    public final void removeFromNotToSwap(
            Grids_AbstractGrid[] g,
            Grids_2D_ID_int chunkID) {
        for (Grids_AbstractGrid g1 : g) {
            removeFromNotToSwap(g1, chunkID);
        }
    }

    /**
     * Removes m from NotToSwap.
     *
     * @param m
     */
    public final void removeFromNotToSwap(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        Iterator<Grids_AbstractGrid> ite;
        ite = m.keySet().iterator();
        Grids_AbstractGrid g;
        while (ite.hasNext()) {
            g = ite.next();
            if (NotToSwap.containsKey(g)) {
                NotToSwap.get(g).removeAll(m.get(g));
            }
        }
    }

    /**
     * Adds all the chunkIDs of g to NotToSwap.
     *
     * @param g
     * @param chunkIDs
     */
    public final void addToNotToSwap(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        if (NotToSwap.containsKey(g)) {
            NotToSwap.get(g).addAll(chunkIDs);
        } else {
            NotToSwap.put(g, chunkIDs);
        }
    }

    /**
     * Remove all the chunkID of g to NotToSwap.
     *
     * @param g
     * @param chunkID
     */
    public final void removeFromNotToSwap(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        if (NotToSwap.containsKey(g)) {
            /**
             * Decided that it is best not to remove g from NotToSwap if
             * NotToSwap.get(g).isEmpty(). So the empty HashSet remains and this
             * takes up a small amount of resource, but it is probably better to
             * keep it in case it is re-used rather than destroying it.
             */
            NotToSwap.get(g).remove(chunkID);
//            HashSet<Grids_2D_ID_int> chunkIDs;
//            chunkIDs = NotToSwap.get(g);
//            chunkIDs.remove(chunkID);
//            if (chunkIDs.isEmpty()) {
//                NotToSwap.remove(g);
//            }
        }
    }

    /**
     * Adds all the ChunkIDs of g that are within cellDistance of the chunk with
     * ChunkID to NotToSwap.
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
     * all chunks in NotToSwap.
     */
    public final void addToNotToSwap(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            int chunkRow,
            int chunkCol,
            int chunkNRows,
            int chunkNCols,
            int cellDistance) {
        HashSet<Grids_2D_ID_int> chunkIDs;
        if (NotToSwap.containsKey(g)) {
            chunkIDs = NotToSwap.get(g);
        } else {
            chunkIDs = new HashSet<>();
            NotToSwap.put(g, chunkIDs);
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
     * Initialises Grids.
     *
     * @param grids
     */
    protected void initGrids(
            HashSet<Grids_AbstractGrid> grids) {
        if (Grids == null) {
            Grids = grids;
        } else {
            //System.err.println(getClass().getName() + ".initGrids(HashSet)");
            if (grids == null) { // Debug
                Grids = new HashSet<>();
            } else {
                Grids = grids;
            }
        }
    }

    /**
     * @return the Directory
     */
    public File getDirectory() {
        return Directory;
    }

    /**
     * @return the Grids
     */
    public HashSet<Grids_AbstractGrid> getGrids() {
        return Grids;
    }

    /**
     * Sets Grids to be grids.
     *
     * @param grids
     * @param hoome
     */
    public void setGrids(
            HashSet<Grids_AbstractGrid> grids,
            boolean hoome) {
        try {
            Grids = grids;
            checkAndMaybeFreeMemory(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                if (!swapChunk()) {
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
     * Adds g to Grids.
     *
     * @param g
     */
    public void addGrid(Grids_AbstractGrid g) {
        Grids.add(g);
    }

    /**
     * Remove g from Grids.
     *
     * @param g
     */
    public void removeGrid(Grids_AbstractGrid g) {
        Grids.remove(g);
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
     * Initialises grids and memory reserve.
     *
     * @param grids
     */
    protected void initGridsAndMemoryReserve(
            HashSet<Grids_AbstractGrid> grids) {
        initGrids(grids);
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        if (ite.hasNext()) {
            ite.next().ge.setMemoryReserve(MemoryReserve);
        } else {
            initMemoryReserve();
        }
    }

    /**
     * Initialises MemoryReserve. An account of swapping is returned.
     *
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
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
                result = swapChunk_AccountDetail();
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
     * Initialises MemoryReserve. An account of swapping is returned.
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
                if (!swapChunk()) {
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this then no chunks are swapped from g. An account of swapping is
     * returned.
     *
     * @param g
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
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
                if (swapChunkExcept_Account(g) < 1L) {
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this then no chunks are swapped from grids that have chunkID. An account
     * of swapping is returned.
     *
     * @param chunkID
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
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
                if (swapChunkExcept_Account(chunkID) < 1L) {
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this then no chunks are swapped from grids that have chunkID. An account
     * of swapping is returned.
     *
     * @param chunkID
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
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
                result = swapChunkExcept_AccountDetail(chunkID);
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this then no chunks are swapped from grids that have chunkID. An account
     * of swapping is returned.
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
                long result = swapChunkExcept_Account(chunkID);
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this then the chunk with chunkID in g is not swapped.
     *
     * @param g
     * @param chunkID
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
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
                if (swapChunkExcept_Account(g, chunkID) < 1L) {
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this then the chunk with chunkID in g is not swapped. An account of
     * swapping is returned.
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
                long result = swapChunkExcept_Account(g, chunkID);
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this then the chunk with chunkID in g is not swapped. An account of
     * swapping is returned.
     *
     * @param g
     * @param chunkID
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
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
                result = swapChunkExcept_AccountDetail(g, chunkID);
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this then chunks with chunkID in chunkIDs in g are not swapped and. An
     * account of swapping is returned.
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
                result = swapChunkExcept_AccountDetail(g, chunkIDs);
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this no chunks from g are swapped. An account of swapping is returned.
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
                result = swapChunkExcept_AccountDetail(g);
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this no chunks given by m are swapped. An account of swapping is
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
                if (!swapChunkExcept(m)) {
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this no chunks from g are swapped. An account of swapping is returned.
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
                long result = swapChunkExcept_Account(g);
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this no chunks with ChunkIDs in chunkIDs from g are swapped. An account
     * of swapping is returned.
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
                long result = swapChunkExcept_Account(g, chunkIDs);
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this no chunks with ChunkIDs in chunkIDs from g are swapped. An account
     * of swapping is returned.
     *
     * @param g
     * @param chunkIDs
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
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
                if (swapChunkExcept_Account(g, chunkIDs) < 1L) {
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this no chunks given by m are swapped.
     *
     * @param m
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
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
                if (!swapChunkExcept(m)) {
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
     * Initialises MemoryReserve. If any swapping has to be done in order to do
     * this no chunks given by m are swapped.
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
                result = swapChunkExcept_AccountDetail(m);
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
     * swap some chunks. Chunks in NotToSwap are not swapped unless desperate.
     * If not enough data is found to swap then an OutOfMemoryError is thrown.
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
                String message = "Warning! Not enough data to swap in "
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
     * swap some chunks. Chunks in NotToSwap are not swapped unless desperate.
     *
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    @Override
    public boolean checkAndMaybeFreeMemory() {
        if (NotToSwap.isEmpty()) {
            return checkAndMaybeFreeMemory_SwapAny();
        } else {
            do {
                if (swapChunkExcept(NotToSwap)) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        return true;
                    }
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            return checkAndMaybeFreeMemory_SwapAny();
        }
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. If available fast access memory is not low then this simply returns
     * true. If available fast access memory is low, then an attempt is made to
     * swap some chunks. Chunks in NotToSwap are not swapped unless desperate.
     *
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    protected boolean checkAndMaybeFreeMemory_SwapAny() {
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
     * swap some chunks. Chunks in NotToSwap are not swapped unless desperate.
     * No chunk in g is swapped. If not enough data is found to swap then an
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
                String message = "Warning! Not enough data to swap in "
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
     * swap some chunks. Chunks in NotToSwap are not swapped unless desperate.
     * No chunk in g is swapped.
     *
     * @param g
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    protected boolean checkAndMaybeFreeMemory(Grids_AbstractGrid g) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            NotToSwap.put(g, g.getChunkIDs());
            do {
                if (!swapChunkExcept(NotToSwap)) {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                if (!swapChunkExcept(g)) {
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
     * swap some chunks. Chunks in NotToSwap are not swapped unless desperate.
     * No chunk with chunkId in g is swapped. If there is not enough free memory
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
                String message = "Warning! Not enough data to swap in "
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
     * swap some chunks. Chunks in NotToSwap are not swapped unless desperate.
     * No chunk with chunkId in g is swapped.
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
            addToNotToSwap(g, chunkID);
            do {
                if (!swapChunkExcept(NotToSwap)) {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                if (swapChunkExcept_Account(g, chunkID) < 1) {
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
     * swap some chunks. Chunks in NotToSwap are not swapped unless desperate.
     * No chunk with chunkID is swapped. If there is not enough free memory then
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
                String message = "Warning! Not enough data to swap in "
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
     * swap some chunks. Chunks in NotToSwap are not swapped unless desperate.
     * No chunk with chunkID is swapped.
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
            ite = Grids.iterator();
            while (ite.hasNext()) {
                g = ite.next();
                addToNotToSwap(g, chunkID);
                if (swapChunkExcept(NotToSwap)) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        return true;
                    }
                }
            }
            ite = Grids.iterator();
            while (ite.hasNext()) {
                if (swapChunkExcept(chunkID)) {
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
     * file. No data is swapped as identified by m and no data is swapped from
     * NotToSwap unless desperate. If not enough data is found to swap then an
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
                String message = "Warning! Not enough data to swap in "
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
     * file. No data is swapped as identified by m and no data is swapped from
     * NotToSwap unless desperate. If no data is found to swap then false is
     * returned otherwise true is returned.
     *
     * @param m
     * @return true if there is sufficient memory to continue and false
     * otherwise.
     */
    protected boolean checkAndMaybeFreeMemory(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            addToNotToSwap(m);
            do {
                if (!swapChunkExcept(NotToSwap)) {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                if (swapChunkExcept_Account(m) < 1) {
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
     * file. No data is swapped as identified by m and no data is swapped from
     * NotToSwap unless desperate. If no data is found to swap then false is
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
                if (swapChunkExcept_Account(g, chunkIDs) < 1) {
                    System.out.println(
                            "Warning! Nothing to swap in "
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
                    if (swapChunkExcept_Account(g, chunkIDs) < 1L) {
                        System.out.println(
                                "Warning! Nothing to swap in "
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
     * file. No chunks are swapped from g that have ChunkIDs in chunkIDs. If no
     * data is found to swap then false is returned otherwise true is returned.
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
            addToNotToSwap(g, chunkIDs);
            do {
                if (swapChunkExcept(NotToSwap)) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        return true;
                    }
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                if (swapChunkExcept_Account(g, chunkIDs) < 1) {
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
     * an OutOfMemoryError if there is not enough data to swap in Grids.
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
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account(boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
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
     * @return Account of data swapped.
     */
    protected Account checkAndMaybeFreeMemory_Account() {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            do {
                if (swapChunkExcept(NotToSwap)) {
                    result.Detail++;
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                result.Success = true;
            } else {
                do {
                    if (swapChunk()) {
                        result.Detail++;
                    } else {
                        break;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                result.Success = getTotalFreeMemory() < Memory_Threshold;
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
     * an OutOfMemoryError if there is not enough data to swap in Grids. No data
     * is swapped from g.
     *
     * @param g
     * @param hoome
     * @return Number of chunks swapped.
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
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account(" + g.getClass().getName()
                        + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
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
     * an OutOfMemoryError if there is not enough data to swap in Grids. No data
     * is swapped from g.
     *
     * @param g
     * @return Account of data swapped.
     */
    protected Account checkAndMaybeFreeMemory_Account(
            Grids_AbstractGrid g) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            addToNotToSwap(g);
            do {
                if (swapChunkExcept(NotToSwap)) {
                    result.Detail++;
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                result.Success = true;
            } else {
                do {
                    if (swapChunkExcept(g)) {
                        result.Detail++;
                    } else {
                        break;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                result.Success = getTotalFreeMemory() < Memory_Threshold;
            }
            return result;
        }
        return null;
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. This method may throw
     * an OutOfMemoryError if there is not enough data to swap in Grids. The
     * Chunk with chunkID from g is not swapped.
     *
     * @param g
     * @param hoome
     * @param chunkID
     * @return Number of chunks swapped.
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
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account("
                        + g.getClass().getName() + ","
                        + chunkID.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
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
     * an OutOfMemoryError if there is not enough data to swap in Grids. The
     * Chunk with chunkID from g is not swapped.
     *
     * @param g
     * @param chunkID
     * @return Account of data swapped.
     */
    public Account checkAndMaybeFreeMemory_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            addToNotToSwap(g, chunkID);
            do {
                if (swapChunkExcept(NotToSwap)) {
                    result.Detail++;
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                result.Success = true;
            } else {
                do {
                    long swaps = swapChunkExcept_Account(g, chunkID);
                    if (swaps < 1L) {
                        break;
                    } else {
                        result.Detail += swaps;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                result.Success = getTotalFreeMemory() < Memory_Threshold;
            }
            return result;
        }
        return null;
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. This method may throw
     * an OutOfMemoryError if there is not enough data to swap in Grids. No
     * Chunk with chunkID is not swapped.
     *
     * @param chunkID
     * @param hoome
     * @return Number of chunks swapped.
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
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account("
                        + chunkID.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
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
     * is not swapped.
     *
     * @param chunkID
     * @return Account of data swapped.
     */
    public Account checkAndMaybeFreeMemory_Account(
            Grids_2D_ID_int chunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            Grids_AbstractGrid g;
            Iterator<Grids_AbstractGrid> ite;
            ite = Grids.iterator();
            while (ite.hasNext()) {
                g = ite.next();
                addToNotToSwap(g, chunkID);
                long swap;
                swap = swapChunkExcept_Account(NotToSwap);
                result.Detail += swap;
                if (swap > 0L) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.Success = true;
                        return result;
                    }
                }
            }
            ite = Grids.iterator();
            while (ite.hasNext()) {
                g = ite.next();
                long swap;
                swap = swapChunkExcept_Account(g, chunkID);
                result.Detail += swap;
                if (swap > 0L) {
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.Success = true;
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
     * OutOfMemoryError is encountered and hoome is true. No data is swapped as
     * identified by m.
     *
     * @param m
     * @param hoome
     * @return Number of chunks swapped.
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
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account("
                        + m.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
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
     * OutOfMemoryError is encountered and hoome is true. No data is swapped as
     * identified by m.
     *
     * @param m
     * @return Account of data swapped.
     */
    public Account checkAndMaybeFreeMemory_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            addToNotToSwap(m);
            do {
                if (swapChunkExcept(NotToSwap)) {
                    result.Detail++;
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                result.Success = true;
            } else {
                do {
                    long swaps = swapChunkExcept_Account(m);
                    if (swaps < 1L) {
                        break;
                    } else {
                        result.Detail += swaps;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                result.Success = getTotalFreeMemory() < Memory_Threshold;
            }
            return result;
        }
        return null;
    }

    /**
     * A method to check and maybe free fast access memory by writing chunks to
     * file. An attempt at Grids internal memory handling is performed if an
     * OutOfMemoryError is encountered and hoome is true. No data is swapped as
     * identified by m. No data is swapped from chunks in g.
     *
     * @param g
     * @param hoome
     * @param chunks
     * @return Number of chunks swapped.
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
            if (!test.Success) {
                String message;
                message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_Account("
                        + g.getClass().getName() + ","
                        + chunks.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
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
     * OutOfMemoryError is encountered and hoome is true. No data is swapped as
     * identified by m. No data is swapped from chunks in g.
     *
     * @param g
     * @param chunkIDs
     * @return Account of data swapped.
     */
    public Account checkAndMaybeFreeMemory_Account(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            addToNotToSwap(g, chunkIDs);
            do {
                if (swapChunkExcept(NotToSwap)) {
                    result.Detail++;
                } else {
                    break;
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            if (getTotalFreeMemory() < Memory_Threshold) {
                result.Success = true;
            } else {
                do {
                    long swaps = swapChunkExcept_Account(g, chunkIDs);
                    if (swaps < 1L) {
                        break;
                    } else {
                        result.Detail += swaps;
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
                result.Success = getTotalFreeMemory() < Memory_Threshold;
            }
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. An attempt at
     * Grids internal memory handling is performed if an OutOfMemoryError is
     * encountered and hoome is true. This method may throw an OutOfMemoryError
     * if there is no grid chunk to swap in Grids.
     *
     * @param hoome
     * @return A map of the grid chunks swapped.
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
            boolean test0 = test.Success;
            if (!test0) {
                String message;
                message = "Warning! Not enough data to swap in "
                        + getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + "boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
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
                partResult = swapChunkExcept_AccountDetail(NotToSwap);
                if (partResult.isEmpty()) {
                    break;
                } else {
                    combine(result.Detail, partResult);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.Success = true;
                        return result;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                partResult = swapChunk_AccountDetail();
                if (partResult.isEmpty()) {
                    break;
                } else {
                    combine(result.Detail, partResult);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.Success = true;
                        return result;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            result.Success = false;
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
     * method may throw an OutOfMemoryError if there is not enough data to swap
     * in Grids. No data is swapped from g.
     *
     * @param g
     * @param hoome
     * @return HashMap identifying chunks swapped.
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
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + g.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
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
     * memory to continue. No data is swapped from g.
     *
     * @param g
     * @return
     */
    protected AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            Grids_AbstractGrid g) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            addToNotToSwap(g);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
            do {
                partResult = swapChunkExcept_AccountDetail(NotToSwap);
                if (partResult.isEmpty()) {
                    break;
                } else {
                    combine(result.Detail, partResult);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.Success = true;
                        return result;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                partResult = swapChunkExcept_AccountDetail(g);
                if (partResult.isEmpty()) {
                    break;
                } else {
                    combine(result.Detail, partResult);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.Success = true;
                        return result;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            result.Success = false;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. The Chunk with chunkID from g is not swapped.
     *
     * @param g
     * @param hoome
     * @param chunkID
     * @return HashMap identifying chunks swapped.
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
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + g.getClass().getName() + ","
                        + chunkID.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
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
     * memory to continue. The Chunk with chunkID from g is not swapped.
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
            addToNotToSwap(g, chunkID);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
            do {
                partResult = swapChunkExcept_AccountDetail(NotToSwap);
                if (partResult.isEmpty()) {
                    break;
                } else {
                    combine(result.Detail, partResult);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.Success = true;
                        return result;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                partResult = swapChunkExcept_AccountDetail(g, chunkID);
                if (partResult.isEmpty()) {
                    break;
                } else {
                    combine(result.Detail, partResult);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.Success = true;
                        return result;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            result.Success = false;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. The Chunk with chunkID from g is not swapped. No data
     * is swapped with chunkID.
     *
     * @param chunkID
     * @param hoome
     * @return HashMap identifying chunks swapped.
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
            boolean resultPart0 = result.Success;
            if (!resultPart0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + chunkID.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return result.Detail;
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
     * memory to continue. The Chunk with chunkID from g is not swapped. No data
     * is swapped with chunkID.
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
            ite = Grids.iterator();
            while (ite.hasNext()) {
                g = ite.next();
                addToNotToSwap(g, chunkID);
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                do {
                    partResult = swapChunkExcept_AccountDetail(NotToSwap);
                    if (partResult.isEmpty()) {
                        break;
                    } else {
                        combine(result.Detail, partResult);
                        if (getTotalFreeMemory() < Memory_Threshold) {
                            result.Success = true;
                            return result;
                        }
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
            }
            ite = Grids.iterator();
            while (ite.hasNext()) {
                g = ite.next();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                do {
                    partResult = swapChunkExcept_AccountDetail(g, chunkID);
                    if (partResult.isEmpty()) {
                        break;
                    } else {
                        combine(result.Detail, partResult);
                        if (getTotalFreeMemory() < Memory_Threshold) {
                            result.Success = true;
                            return result;
                        }
                    }
                } while (getTotalFreeMemory() < Memory_Threshold);
            }
            result.Success = false;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. The Chunk with chunkID from g is not swapped. No data
     * is swapped as identified by m.
     *
     * @param m Identifies data not to be swapped.
     * @param hoome If true then if an OutOfMemoryError is encountered then an
     * attempt is made to handle this otherwise not and the error is thrown.
     * @return HashMap identifying chunks swapped or null if nothing is swapped.
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
            if (test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + m.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
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
     * memory to continue. The Chunk with chunkID from g is not swapped. No data
     * is swapped as identified by m.
     *
     * @param m Identifies data not to be swapped.
     * @return HashMap identifying chunks swapped or null if nothing is swapped.
     */
    protected AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            addToNotToSwap(m);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
            do {
                partResult = swapChunkExcept_AccountDetail(NotToSwap);
                if (partResult.isEmpty()) {
                    break;
                } else {
                    combine(result.Detail, partResult);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.Success = true;
                        return result;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                partResult = swapChunkExcept_AccountDetail(m);
                if (partResult.isEmpty()) {
                    break;
                } else {
                    combine(result.Detail, partResult);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.Success = true;
                        return result;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            result.Success = false;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. This method will
     * quickly return null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room and will pass
     * back a detailed account of this and an indication if there is enough
     * memory to continue. The Chunk with chunkID from g is not swapped. No
     * chunks with ChunkID in chunkIDs are swapped from g.
     *
     * @param g
     * @param hoome
     * @param chunkIDs
     * @return HashMap identifying chunks swapped.
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
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".checkAndMaybeFreeMemory_AccountDetail("
                        + g.getClass().getName() + ","
                        + chunkIDs.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                hoome = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
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
     * memory to continue. The Chunk with chunkID from g is not swapped. No
     * chunks with ChunkID in chunkIDs are swapped from g.
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
            addToNotToSwap(g, chunkIDs);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
            do {
                partResult = swapChunkExcept_AccountDetail(NotToSwap);
                if (partResult.isEmpty()) {
                    break;
                } else {
                    combine(result.Detail, partResult);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.Success = true;
                        return result;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            do {
                partResult = swapChunkExcept_AccountDetail(g, chunkIDs);
                if (partResult.isEmpty()) {
                    break;
                } else {
                    combine(result.Detail, partResult);
                    if (getTotalFreeMemory() < Memory_Threshold) {
                        result.Success = true;
                        return result;
                    }
                }
            } while (getTotalFreeMemory() < Memory_Threshold);
            result.Success = false;
            return result;
        }
        return null;
    }

    /**
     * Attempts to swap all chunks in Grids.
     *
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then swap operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunks_AccountDetail(boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunks_AccountDetail(NotToSwap);
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail();
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
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
                return swapChunks_AccountDetail(hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap all Grids_AbstractGridChunk in this.Grids.
     *
     * @return
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunks_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        while (ite.hasNext()) {
            partResult = ite.next().swapChunks_AccountDetail();
            combine(result,
                    partResult);
        }
        return result;
    }

    /**
     * Attempts to swap all Grids_AbstractGridChunk in this.Grids.
     *
     * @param m
     * @return
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunks_AccountDetail(
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        Grids_AbstractGrid g;
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        while (ite.hasNext()) {
            g = ite.next();
            partResult = g.swapChunksExcept_AccountDetail(m.get(g));
            combine(result, partResult);
        }
        return result;
    }

    /**
     * Attempts to swap all chunks in ge.
     *
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then swap operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return A count of the number of chunks swapped.
     */
    public long swapChunks_Account(
            boolean hoome) {
        try {
            long result;
            try {
                result = swapChunks_Account();
                if (result < 1) {
                    Account account = checkAndMaybeFreeMemory_Account();
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
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
                result += swapChunks_Account(hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap all chunks in ge.
     *
     * @return
     */
    protected long swapChunks_Account() {
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        while (ite.hasNext()) {
            long partResult;
            Grids_AbstractGrid g;
            g = ite.next();
            partResult = g.swapChunks_Account();
            result += partResult;
        }
        DataToSwap = false;
        return result;
    }

    /**
     * Attempts to swap all Grids_AbstractGridChunk in this.Grids.
     *
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then swap operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public void swapChunks(boolean hoome) {
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
                clearMemoryReserve();
                if (!swapChunk()) {
                    throw e;
                }
                initMemoryReserve();
                swapChunks();
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap all Grids_AbstractGridChunk in Grids.
     *
     * @return
     */
    protected boolean swapChunks() {
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        while (ite.hasNext()) {
            ite.next().swapChunks();
        }
        DataToSwap = false;
        return true;
    }

    /**
     * Attempts to swap any Grids_AbstractGridChunk in this.Grids. This is the
     * lowest level of OutOfMemoryError handling in this class.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then swap operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunk_AccountDetail(boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunk_AccountDetail();
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail();
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
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
     * Attempts to swap any chunk in Grids trying first not to swap any in
     * NotToSwap.
     *
     * @param hoome
     * @return
     */
    public boolean swapChunk(boolean hoome) {
        try {
            boolean success = swapChunk();
            try {
                if (!success) {
                    Account account = checkAndMaybeFreeMemory_Account();
                    if (account != null) {
                        if (!account.Success) {
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
                if (!swapChunk()) {
                    throw e;
                }
                initMemoryReserve();
                // No need for recursive call: swapChunk(hoome);
                return true;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap any chunk in Grids trying first not to swap any in
     * NotToSwap.
     *
     * @return
     */
    protected boolean swapChunk() {
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        Grids_AbstractGrid g;
        while (ite.hasNext()) {
            g = ite.next();
            if (swapChunkExcept(NotToSwap)) {
                return true;
            }
            if (g.swapChunk()) {
                return true;
            }
        }
        DataToSwap = false;
        return false;
    }

    /**
     * Swap to File any GridChunk in Grids except one in g.
     *
     * @param g
     * @param hoome
     */
    public void swapChunkExcept(
            Grids_AbstractGrid g,
            boolean hoome) {
        try {
            boolean success = swapChunkExcept(g);
            try {
                if (!success) {
                    Account account;
                    account = checkAndMaybeFreeMemory_Account(g);
                    if (account != null) {
                        if (!account.Success) {
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
                if (swapChunkExcept_Account(g) < 1L) {
                    throw e;
                }
                initMemoryReserve(g, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Swap to File any GridChunk in Grids except one in g.
     *
     * @param g
     * @return
     */
    protected boolean swapChunkExcept(
            Grids_AbstractGrid g) {
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        Grids_AbstractGrid bg;
        while (ite.hasNext()) {
            bg = ite.next();
            if (bg != g) {
                if (bg.swapChunk()) {
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
     * Attempts to swap any Grids_AbstractGridChunk in this.Grids.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunk_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid g = ite.next();
            if (NotToSwap.containsKey(g)) {
                HashSet<Grids_2D_ID_int> chunkIDs;
                chunkIDs = NotToSwap.get(g);
                result = g.swapChunkExcept_AccountDetail(chunkIDs);
                if (!result.isEmpty()) {
                    return result;
                }
            }
        }
        DataToSwap = false;
        return null;
    }

    /**
     * @param hoome
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this.Grids except for those in with
     * Grids_AbstractGrid.ID = _ChunkID.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunkExcept_AccountDetail(
                    Grids_2D_ID_int chunkID,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunkExcept_AccountDetail(
                    chunkID);
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail(chunkID);
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                    partResult = checkAndMaybeFreeMemory_AccountDetail(chunkID, hoome);
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
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(chunkID);
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
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this.Grids except for those in with
     * Grids_AbstractGrid.ID = _ChunkID.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunkExcept_AccountDetail(Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        Grids_AbstractGrid g;
        while (ite.hasNext()) {
            g = ite.next();
            result = g.swapChunkExcept_AccountDetail(chunkID);
            if (!result.isEmpty()) {
                HashSet<Grids_2D_ID_int> chunkIDs = new HashSet<>(1);
                chunkIDs.add(chunkID);
                result.put(g, chunkIDs);
                return result;
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
    public long swapChunkExcept_Account(
            Grids_2D_ID_int chunkID,
            boolean hoome) {
        try {
            long result = swapChunkExcept_Account(chunkID);
            try {
                if (result < 1) {
                    Account account;
                    account = checkAndMaybeFreeMemory_Account(chunkID);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += checkAndMaybeFreeMemory_Account(chunkID, hoome);
                }
            } catch (OutOfMemoryError e) {
                // Set hoome = false to exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            result += checkAndMaybeFreeMemory_Account(hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                long result = swapChunkExcept_Account(chunkID);
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
     * @param chunkID The id of the GridChunk not to be swapped.
     * @return
     */
    protected long swapChunkExcept_Account(Grids_2D_ID_int chunkID) {
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        Grids_AbstractGrid g;
        while (ite.hasNext()) {
            g = ite.next();
            addToNotToSwap(g, chunkID);
            if (!swapChunkExcept(NotToSwap)) {
                result += swapChunkExcept_Account(chunkID);
            } else {
                result += 1L;
            }
            if (result > 0L) {
                return result;
            }
        }
        return result;
    }

    /**
     * @param chunkID The ID of the chunk not to be swapped.
     * @return
     */
    protected boolean swapChunkExcept(
            Grids_2D_ID_int chunkID) {
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        //Grids_AbstractGrid g;
        while (ite.hasNext()) {
            //g = ite.next();
            if (swapChunkExcept_Account(chunkID) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param hoome
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this.Grids except for those in
     * _Grid2DSquareCell_ChunkIDHashSet.
     * @param m HashMap with Grids_AbstractGrid as keys and a respective HashSet
     * of Grids_AbstractGrid.ChunkIDs as values. Collectively these identifying
     * those chunks not to be swapped from the Grids_AbstractGrid.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunkExcept_AccountDetail(
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunkExcept_AccountDetail(m);
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail(m);
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                    partResult = checkAndMaybeFreeMemory_AccountDetail(m, hoome);
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
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(m);
                if (result.isEmpty()) {
                    throw e;
                }
                initMemoryReserve(m, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * @param m
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this.Grids except for those in
     * _Grid2DSquareCell_ChunkIDHashSet.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunkExcept_AccountDetail(
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        Grids_AbstractGrid g;
        HashSet<Grids_2D_ID_int> result_ChunkID_HashSet;
        result_ChunkID_HashSet = new HashSet<>(1);
        HashSet<Grids_2D_ID_int> chunkID_HashSet;
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            g = ite.next();
            if (m.containsKey(g)) {
                chunkID_HashSet = m.get(g);
                chunkID = g.swapChunkExcept_AccountChunk(chunkID_HashSet);
                if (chunkID != null) {
                    result_ChunkID_HashSet.add(chunkID);
                    result.put(g, result_ChunkID_HashSet);
                    return result;
                }
            }
            chunkID = g.swapChunk_AccountChunk();
            if (chunkID != null) {
                result_ChunkID_HashSet.add(chunkID);
                result.put(g, result_ChunkID_HashSet);
                return result;
            }
        }
        return result; // If here then nothing could be swapped!
    }

    /**
     *
     * @param g
     * @param chunkIDs
     * @return
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunkExcept_AccountDetail(
                    Grids_AbstractGrid g,
                    HashSet<Grids_2D_ID_int> chunkIDs) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid gb;
        HashSet<Grids_2D_ID_int> resultPart;
        resultPart = new HashSet<>(1);
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            gb = ite.next();
            if (g == gb) {
                chunkID = gb.swapChunkExcept_AccountChunk(chunkIDs);
                if (chunkID != null) {
                    resultPart.add(chunkID);
                    result.put(g, resultPart);
                    return result;
                }
            } else {
                chunkID = g.swapChunk_AccountChunk();
                if (chunkID != null) {
                    resultPart.add(chunkID);
                    result.put(g, resultPart);
                    return result;
                }
            }
        }
        return result; // If here then nothing could be swapped!
    }

    /**
     *
     * @param g
     * @param chunkID
     * @return
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunkExcept_AccountDetail(
                    Grids_AbstractGrid g,
                    Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        Grids_AbstractGrid gb;
        HashSet<Grids_2D_ID_int> resultPart = new HashSet<>(1);
        Grids_2D_ID_int chunkIDb;
        while (ite.hasNext()) {
            gb = ite.next();
            if (g == gb) {
                chunkIDb = gb.swapChunkExcept_AccountChunk(chunkID);
                if (chunkIDb != null) {
                    resultPart.add(chunkIDb);
                    result.put(g, resultPart);
                    return result;
                }
            } else {
                chunkIDb = g.swapChunk_AccountChunk();
                if (chunkIDb != null) {
                    resultPart.add(chunkIDb);
                    result.put(g, resultPart);
                    return result;
                }
            }
        }
        return result; // If here then nothing could be swapped!
    }

    /**
     *
     * @param g
     * @param chunkID
     * @param hoome
     * @return
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunkExcept_AccountDetail(
                    Grids_AbstractGrid g,
                    Grids_2D_ID_int chunkID,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunkExcept_AccountDetail(g, chunkID);
            return result;
        } catch (java.lang.OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = swapChunkExcept_AccountDetail(g, chunkID);
                if (partResult.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = initMemoryReserve_AccountDetail(g, chunkID, hoome);
                combine(result, partResult);
                return result;
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
            swapChunkExcept_AccountDetail(Grids_AbstractGrid g) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid gb;
        HashSet<Grids_2D_ID_int> resultPart;
        resultPart = new HashSet<>(1);
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            gb = ite.next();
            if (g != gb) {
                chunkID = gb.swapChunk_AccountChunk();
                if (chunkID != null) {
                    resultPart.add(chunkID);
                    result.put(g, resultPart);
                    return result;
                }
            }
        }
        return result; // If here then nothing could be swapped!
    }

    /**
     *
     * @param m
     * @return
     */
    protected long swapChunkExcept_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        Grids_AbstractGrid g;
        HashSet<Grids_2D_ID_int> chunkIDs;
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            g = ite.next();
            if (m.containsKey(g)) {
                chunkIDs = m.get(g);
                chunkID = g.swapChunkExcept_AccountChunk(chunkIDs);
                if (chunkID != null) {
                    return 1L;
                }
            }
            chunkID = g.swapChunk_AccountChunk();
            if (chunkID != null) {
                return 1L;
            }
        }
        return 0L; // If here then nothing could be swapped!
    }

    protected boolean swapChunkExcept(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid g;
        HashSet<Grids_2D_ID_int> chunkIDs;
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            g = ite.next();
            if (m.containsKey(g)) {
                chunkIDs = m.get(g);
                chunkID = g.swapChunkExcept_AccountChunk(chunkIDs);
                if (chunkID != null) {
                    return true;
                }
            }
            chunkID = g.swapChunk_AccountChunk();
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
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this.Grids except for those in g with ChunkIDs
     * in chunkIDs.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     */
    public long swapChunkExcept_Account(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs,
            boolean hoome) {
        try {
            long result;
            result = swapChunkExcept_Account(g, chunkIDs);
            try {
                if (result < 1) {
                    Account account;
                    account = checkAndMaybeFreeMemory_Account(g, chunkIDs);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += checkAndMaybeFreeMemory_Account(g, chunkIDs, hoome);
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
                long result = swapChunkExcept_Account(g, chunkIDs);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(g, chunkIDs, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkIDs
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this.Grids except for those in g with ChunkIDs
     * in chunkIDs.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     */
    protected long swapChunkExcept_Account(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid gb;
        TreeMap<Grids_2D_ID_int, Grids_AbstractGridChunk> m;
        Set<Grids_2D_ID_int> chunkIDsb;
        Iterator<Grids_2D_ID_int> iteb;
        Grids_2D_ID_int chunkID;
        Grids_AbstractGridChunk chunkb;
        while (ite.hasNext()) {
            gb = ite.next();
            if (gb != g) {
                m = gb.getChunkIDChunkMap();
                chunkIDsb = m.keySet();
                iteb = chunkIDsb.iterator();
                while (iteb.hasNext()) {
                    chunkID = iteb.next();
                    if (!chunkIDs.contains(chunkID)) {
                        //Check it can be swapped
                        chunkb = m.get(chunkID);
                        if (chunkb != null) {
                            gb.swapChunk(chunkID);
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
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this.Grids except for that in
     * _Grid2DSquareCell with Grids_AbstractGrid._ChunkID _ChunkID.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    public long swapChunkExcept_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean hoome) {
        try {
            long result;
            result = swapChunkExcept_Account(g, chunkID);
            try {
                if (result < 1) {
                    Account account;
                    account = checkAndMaybeFreeMemory_Account(g, chunkID);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += checkAndMaybeFreeMemory_Account(g, chunkID, hoome);
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
                long result = swapChunkExcept_Account(g, chunkID);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(g, chunkID, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this.Grids except for that in
     * _Grid2DSquareCell with Grids_AbstractGrid._ChunkID _ChunkID.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    protected long swapChunkExcept_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        long result = swapChunkExcept_Account(g);
        if (result < 1L) {
            result = g.swapChunkExcept_Account(chunkID);
        }
        return result;
    }

    /**
     * @param hoome
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this.Grids except for those in g.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     */
    public long swapChunkExcept_Account(
            Grids_AbstractGrid g,
            boolean hoome) {
        try {
            long result = swapChunkExcept_Account(g);
            try {
                if (result < 1) {
                    Account account;
                    account = checkAndMaybeFreeMemory_Account(g);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += checkAndMaybeFreeMemory_Account(g, hoome);
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
                long result = swapChunkExcept_Account(g);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(g, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this.Grids except for those in
     * _Grid2DSquareCell.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     */
    protected long swapChunkExcept_Account(Grids_AbstractGrid g) {
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid gb;
        while (ite.hasNext()) {
            gb = ite.next();
            if (gb != g) {
                Grids_2D_ID_int chunkID;
                chunkID = gb.swapChunk_AccountChunk();
                if (chunkID != null) {
                    return 1L;
                }
            }
        }
        return 0L;
    }

    /**
     * Attempts to Swap all Grids_AbstractGrid.ChunkIDs in this.Grids except
     * those with Grids_AbstractGrid.ID _ChunkID.
     *
     * @param hoome
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunksExcept_AccountDetail(
                    Grids_2D_ID_int chunkID,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunksExcept_AccountDetail(chunkID);
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail(chunkID);
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                    partResult = checkAndMaybeFreeMemory_AccountDetail(chunkID, hoome);
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
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(chunkID);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = initMemoryReserve_AccountDetail(chunkID, hoome);
                combine(result, partResult);
                partResult = swapChunksExcept_AccountDetail(chunkID, hoome);
                combine(result, partResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to Swap all Grids_AbstractGrid.ChunkIDs in this.Grids except
     * those with Grids_AbstractGrid.ID _ChunkID.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunksExcept_AccountDetail(Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid g = ite.next();
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
            partResult = g.swapChunksExcept_AccountDetail(chunkID);
            combine(result, partResult);
        }
        return result;
    }

    /**
     * Attempts to Swap all Grids_AbstractGrid.ChunkIDs in this.Grids except
     * those with Grids_AbstractGrid.ID _ChunkID.
     *
     * @param hoome
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped. swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunksExcept_AccountDetail(
                    Grids_AbstractGrid g,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunksExcept_AccountDetail(g);
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail(g);
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                    partResult = checkAndMaybeFreeMemory_AccountDetail(g, hoome);
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
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(g);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = initMemoryReserve_AccountDetail(g, hoome);
                combine(result, partResult);
                partResult = swapChunksExcept_AccountDetail(g, hoome);
                combine(result, partResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to Swap all Grids_AbstractGrid.ChunkIDs in this.Grids except
     * those with Grids_AbstractGrid.ID _ChunkID.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunksExcept_AccountDetail(Grids_AbstractGrid g) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid gb;
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
        while (ite.hasNext()) {
            gb = ite.next();
            if (gb != g) {
                partResult = gb.swapChunks_AccountDetail();
                combine(result, partResult);
            }
        }
        return result;
    }

    public long swapChunksExcept_Account(
            Grids_AbstractGrid g,
            boolean hoome) {
        try {
            long result = swapChunksExcept_Account(g);
            try {
                if (result < 1) {
                    Account account;
                    account = checkAndMaybeFreeMemory_Account(g);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += checkAndMaybeFreeMemory_Account(g, hoome);
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
                long result = swapChunkExcept_Account(g);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(g, hoome);
                result += swapChunksExcept_Account(g, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected long swapChunksExcept_Account(
            Grids_AbstractGrid g) {
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        Grids_AbstractGrid gb;
        while (ite.hasNext()) {
            gb = ite.next();
            if (gb != g) {
                result += gb.ge.swapChunks_Account();
            }
        }
        return result;
    }

    /**
     * Attempts to Swap all Grids_AbstractGrid.ChunkIDs in this.Grids except
     * those with Grids_AbstractGrid.ID _ChunkID.
     *
     * @param hoome
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunksExcept_AccountDetail(
                    Grids_AbstractGrid g,
                    Grids_2D_ID_int chunkID,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunksExcept_AccountDetail(g, chunkID);
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = checkAndMaybeFreeMemory_AccountDetail(g, chunkID);
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                    partResult = checkAndMaybeFreeMemory_AccountDetail(g, chunkID, hoome);
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
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(g, chunkID);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = initMemoryReserve_AccountDetail(g, chunkID, hoome);
                combine(result, partResult);
                partResult = swapChunksExcept_AccountDetail(g, chunkID, hoome);
                combine(result, partResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    public long swapChunksExcept_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean hoome) {
        try {
            long result = swapChunksExcept_Account(g, chunkID);
            try {
                if (result < 1) {
                    Account account;
                    account = checkAndMaybeFreeMemory_Account(g, chunkID);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += checkAndMaybeFreeMemory_Account(g, chunkID, hoome);
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
                long result = swapChunkExcept_Account(g, chunkID);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(chunkID, hoome);
                result += swapChunkExcept_Account(g, chunkID);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected long swapChunksExcept_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid gb;
        while (ite.hasNext()) {
            gb = ite.next();
            if (gb != g) {
                int cri0 = 0;
                int cci0 = 0;
                int cri1 = gb.getNChunkRows() - 1;
                int cci1 = gb.getNChunkCols() - 1;
                result += gb.swapChunks_Account(cri0, cci0, cri1, cci1);
            } else {
                result += gb.swapChunksExcept_Account(chunkID);
            }
        }
        return result;
    }

    /**
     * Attempts to Swap all Grids_AbstractGrid.ChunkIDs in this.Grids except
     * those with Grids_AbstractGrid.ID _ChunkID.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunksExcept_AccountDetail(
                    Grids_AbstractGrid g,
                    Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid bg;
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
        while (ite.hasNext()) {
            bg = ite.next();
            if (bg == g) {
                partResult = bg.swapChunksExcept_AccountDetail(chunkID);
                combine(result, partResult);
            } else {
                partResult = bg.swapChunks_AccountDetail(false,
                        HOOMEF);
                combine(result, partResult);
            }
        }
        return result;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunksExcept_AccountDetail(
                    Grids_AbstractGrid g,
                    HashSet<Grids_2D_ID_int> chunkIDs) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid gb;
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
        while (ite.hasNext()) {
            gb = ite.next();
            if (gb != g) {
                partResult = gb.swapChunks_AccountDetail();
                combine(result, partResult);
            } else {
                HashSet<Grids_2D_ID_int> chunks;
                chunks = g.getChunkIDs();
                Grids_2D_ID_int chunkID;
                Iterator<Grids_2D_ID_int> ite2;
                ite2 = chunks.iterator();
                while (ite2.hasNext()) {
                    chunkID = ite2.next();
                    if (!chunkIDs.contains(chunkID)) {
                        partResult = swapChunksExcept_AccountDetail(chunkID);
                        combine(result, partResult);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Attempts to Swap all chunks except those in g with Chunk IDs in chunkIDs.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     * @param chunkIDs The chunk IDs in g not to be swapped.
     */
    protected long swapChunksExcept_Account(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid gb;
        while (ite.hasNext()) {
            gb = ite.next();
            if (gb != g) {
                int cri0 = 0;
                int cri1 = gb.getNChunkRows() - 1;
                int cci0 = 0;
                int cci1 = gb.getNChunkCols() - 1;
                result += gb.swapChunks_Account(cri0, cci0, cri1, cci1);
            } else {
                result += gb.swapChunksExcept_Account(chunkIDs);
            }
        }
        return result;
    }

    /**
     * @param m
     * @param hoome
     * @return
     */
    public long swapChunksExcept_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
            boolean hoome) {
        try {
            long result = swapChunksExcept_Account(m);
            try {
                if (result < 1) {
                    Account account;
                    account = checkAndMaybeFreeMemory_Account(m);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += checkAndMaybeFreeMemory_Account(m, hoome);
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
                if (!swapChunkExcept(m)) {
                    throw e;
                }
                long result = 1L;
                result += initMemoryReserve_Account(m, hoome);
                result += swapChunksExcept_Account(m);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected long swapChunksExcept_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid g;
        HashSet<Grids_2D_ID_int> chunkIDs;
        while (ite.hasNext()) {
            g = ite.next();
            chunkIDs = m.get(g);
            result += g.swapChunksExcept_Account(chunkIDs);
        }
        return result;
    }

    public void swapData() {
        swapChunks();
    }

    public void swapData(boolean hoome) {
        swapChunks();
    }

    @Override
    public boolean swapDataAny(boolean hoome) {
        try {
            boolean result = swapChunk();
            try {
                if (!checkAndMaybeFreeMemory()) {
                    throw new OutOfMemoryError();
                }
            } catch (OutOfMemoryError e) {
                // Exit method by throwing OutOfMemoryError
                hoome = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                boolean result = swapDataAny();
                initMemoryReserve();
                return result;
            } else {
                throw e;
            }
        }
    }

    @Override
    public boolean swapDataAny() {
        return swapChunk();
    }

    private boolean DataToSwap = true;

    public boolean isDataToSwap() {
        return DataToSwap;
    }

    public void setDataToSwap(boolean dataToSwap) {
        this.DataToSwap = dataToSwap;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    Grids_AbstractGrid g,
                    boolean hoome) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = checkAndMaybeFreeMemory_AccountDetail(g, hoome);
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
        partResult = initMemoryReserve_AccountDetail(g, hoome);
        combine(result, partResult);
        return result;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    Grids_AbstractGrid g,
                    HashSet<Grids_2D_ID_int> chunks,
                    boolean hoome) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = checkAndMaybeFreeMemory_AccountDetail(g, chunks, hoome);
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
        partResult = initMemoryReserve_AccountDetail(g, chunks, hoome);
        combine(result, partResult);
        return result;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    OutOfMemoryError e,
                    boolean hoome) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = swapChunk_AccountDetail();
        if (result.isEmpty()) {
            throw e;
        }
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
        partResult = initMemoryReserve_AccountDetail(hoome);
        combine(result, partResult);
        return result;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    boolean hoome) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = checkAndMaybeFreeMemory_AccountDetail(hoome);
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
        partResult = initMemoryReserve_AccountDetail(hoome);
        combine(result, partResult);
        return result;
    }

    protected long freeSomeMemoryAndResetReserve_Account(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunks,
            boolean hoome) {
        long result = checkAndMaybeFreeMemory_Account(g, chunks, hoome);
        result += initMemoryReserve_Account(g, chunks, hoome);
        return result;
    }

    protected long freeSomeMemoryAndResetReserve_Account(
            OutOfMemoryError e,
            boolean hoome) {
        if (!swapChunk()) {
            throw e;
        }
        long result = 1;
        result += initMemoryReserve_Account(hoome);
        return result;
    }

    /**
     * @return the NotToSwap
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> getNotToSwap() {
        return NotToSwap;
    }

    /**
     * Simple inner class for accounting memory swapping detail.
     */
    protected class AccountDetail {

        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> Detail;
        boolean Success;

        protected AccountDetail() {
            Detail = new HashMap<>();
            Success = false;
        }
    }

    /**
     * Simple inner class for accounting memory swapping.
     */
    protected class Account {

        long Detail;
        boolean Success;

        protected Account() {
            Detail = 0;
            Success = false;
        }
    }

}
