/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;

/**
 *
 * @author Andy
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
     * For indicating which GridChunks that are not normally to be swapped to
     * release memory for processing.
     */
    protected transient HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> NotToSwapData;

    /**
     * For storing an instance of Generic_BigDecimal.
     */
    protected transient Generic_BigDecimal _Generic_BigDecimal;

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
        initNotToSwapData();
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
            File dataDirectory = new File(
                    Directory,
                    Strings.getString_data());
            dataDirectory.mkdirs();
            Files = new Grids_Files(dataDirectory);
        }
        return Files;
    }

    /**
     * @return the _Generic_BigDecimal
     */
    public Generic_BigDecimal get_Generic_BigDecimal() {
        if (_Generic_BigDecimal == null) {
            init_Generic_BigDecimal();
        }
        return _Generic_BigDecimal;
    }

    /**
     * @return the _Generic_BigDecimal
     */
    private void init_Generic_BigDecimal() {
        _Generic_BigDecimal = new Generic_BigDecimal();
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
    public final void initNotToSwapData() {
        NotToSwapData = new HashMap<>();
    }

    /**
     * Adds all the chunkIDs of g to NotToSwapData.
     *
     * @param g
     */
    public final void addToNotToSwapData(Grids_AbstractGrid g) {
        HashSet<Grids_2D_ID_int> chunkIDs;
        chunkIDs = g.getChunkIDs(HandleOutOfMemoryError);
        NotToSwapData.put(g, chunkIDs);
    }

    /**
     * Removes g from the NotToSwapData.
     *
     * @param g
     */
    public final void removeFromNotToSwapData(Grids_AbstractGrid g) {
        NotToSwapData.remove(g);
    }

    /**
     * Adds the chunkID of g to NotToSwapData.
     *
     * @param g
     * @param chunkID
     */
    public final void addToNotToSwapData(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        if (NotToSwapData.containsKey(g)) {
            NotToSwapData.get(g).add(chunkID);
        } else {
            HashSet<Grids_2D_ID_int> chunkIDs;
            chunkIDs = new HashSet<>();
            chunkIDs.add(chunkID);
            NotToSwapData.put(g, chunkIDs);
        }
    }

    /**
     * Adds the chunkID of each grid in g[] to NotToSwapData.
     *
     * @param g
     * @param chunkID
     */
    public final void addToNotToSwapData(
            Grids_AbstractGrid[] g,
            Grids_2D_ID_int chunkID) {
        for (Grids_AbstractGrid g1 : g) {
            addToNotToSwapData(g1, chunkID);
        }
    }

    /**
     * Remove the chunkID of each grid in g[] from NotToSwapData.
     *
     * @param g
     * @param chunkID
     */
    public final void removeFromNotToSwapData(
            Grids_AbstractGrid[] g,
            Grids_2D_ID_int chunkID) {
        for (Grids_AbstractGrid g1 : g) {
            addToNotToSwapData(g1, chunkID);
        }
    }

    /**
     * Adds all the chunkIDs of g to NotToSwapData.
     *
     * @param g
     * @param chunkIDs
     */
    public final void addToNotToSwapData(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        if (NotToSwapData.containsKey(g)) {
            NotToSwapData.get(g).addAll(chunkIDs);
        } else {
            NotToSwapData.put(g, chunkIDs);
        }
    }

    /**
     * Remove all the chunkID of g to NotToSwapData.
     *
     * @param g
     * @param chunkID
     */
    public final void removeFromNotToSwapData(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        if (NotToSwapData.containsKey(g)) {
            /**
             * @TODO Which is best: remove the chunk and don't worry about
             * removing g if the chunkIDs of g is empty, or remove it?
             */
            //NotToSwapData.get(g).remove(chunkID);
            HashSet<Grids_2D_ID_int> chunkIDs;
            chunkIDs = NotToSwapData.get(g);
            chunkIDs.remove(chunkID);
            if (chunkIDs.isEmpty()) {
                NotToSwapData.remove(g);
            }
        }
    }

    /**
     * Adds all the ChunkIDs of g that are within cellDistance of the chunk with
     * ChunkID to NotToSwapData.
     *
     * @param g The Grid.
     * @param chunkID Central ChunkID.
     * @param chunkRowIndex The chunkRowIndex of chunkID - provided for
     * convenience.
     * @param chunkColIndex The chunkColIndex of chunkID - provided for
     * convenience.
     * @param chunkNRows The normal number of rows in a chunk. (Sometimes the
     * last row has fewer.)
     * @param chunkNCols The normal number of columns in a chunk. (Sometimes the
     * last column has fewer.)
     * @param cellDistance The distance over which we want to be sure to include
     * all chunks in NotToSwapData.
     */
    public final void addToNotToSwapData(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkNRows,
            int chunkNCols,
            int cellDistance) {
        HashSet<Grids_2D_ID_int> chunkIDs;
        if (NotToSwapData.containsKey(g)) {
            chunkIDs = NotToSwapData.get(g);
        } else {
            chunkIDs = new HashSet<>();
            NotToSwapData.put(g, chunkIDs);
        }
        chunkIDs.add(chunkID);
        // Over how many chunkRows and how many chunkCols does cellDistance cover
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
        int chunkRow;
        int chunkCol;
        int k;
        int l;
        for (k = -i; k <= i; k++) {
            chunkRow = chunkRowIndex + k;
            for (l = -j; l <= j; j++) {
                chunkCol = chunkRowIndex + k;
                if (g.isInGrid(chunkRow, chunkCol, HandleOutOfMemoryError)) {
                    chunkIDs.add(new Grids_2D_ID_int(chunkRow, chunkCol));
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
            //System.err.println( this.getClass().getName() + ".initGrids(HashSet)" );
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
     * @param handleOutOfMemoryError
     */
    public void setGrids(
            HashSet<Grids_AbstractGrid> grids,
            boolean handleOutOfMemoryError) {
        try {
            Grids = grids;
            tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (swapChunk_Account() < 1L) {
                    throw e;
                }
                initMemoryReserve(handleOutOfMemoryError);
                setGrids(
                        grids,
                        handleOutOfMemoryError);
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
     * Set Grids = grids.
     *
     * @param grids
     */
    protected void setGrids(
            HashSet<Grids_AbstractGrid> grids) {
        Grids = grids;
    }

    /**
     * @param grids
     * @param handleOutOfMemoryError
     */
    public void initGridsAndMemoryReserve(
            HashSet<Grids_AbstractGrid> grids,
            boolean handleOutOfMemoryError) {
        try {
            initGridsAndMemoryReserve(grids);
            tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (swapChunk_Account() < 1L) {
                    throw e;
                }
                initGridsAndMemoryReserve(
                        grids,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param grids
     */
    protected void initGridsAndMemoryReserve(
            HashSet<Grids_AbstractGrid> grids) {
        initGrids(grids);
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        if (ite.hasNext()) {
            ite.next().ge.set_MemoryReserve(MemoryReserve);
        } else {
            initMemoryReserve();
        }
        //System.err.println( this.getClass().getName() + ".initGridsAndMemoryReserve(HashSet)" );
    }

    /**
     * Initialises MemoryReserve.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> initMemoryReserve_AccountDetail(
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result
                        = swapChunk_AccountDetail();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentailPartResult
                        = initMemoryReserve_AccountDetail(
                                handleOutOfMemoryError);
                combine(result,
                        potentailPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    @Override
    public long initMemoryReserve_Account(
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapChunk_Account();
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If an OutOfMemoryError is encountered this
     * calls swapChunksExcept_Account(_Grid2DSquareCell) (if that returns null
     * then it calls swapChunk()) then recurses.
     *
     * @param g
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public final void initMemoryReserve(
            Grids_AbstractGrid g,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            tryToEnsureThereIsEnoughMemoryToContinue(g, handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (swapChunkExcept_Account(g) < 1L) {
                    throw e;
                }
                initMemoryReserve(g, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If an OutOfMemoryError is encountered this
     * calls swapChunksExcept_AccountSuccess(_ChunkID,handleOutOfMemoryError)
     * then recurses.
     *
     * @param chunkID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public final void initMemoryReserve(
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            tryToEnsureThereIsEnoughMemoryToContinue(
                    chunkID,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (swapChunkExcept_Account(
                        chunkID) < 1L) {
                    throw e;
                }
                initMemoryReserve(
                        chunkID,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If an OutOfMemoryError is encountered this
     * calls swapChunksExcept_AccountSuccess(_ChunkID,handleOutOfMemoryError)
     * then recurses.
     *
     * @param chunkID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> initMemoryReserve_AccountDetail(
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    chunkID,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(
                        chunkID);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = initMemoryReserve_AccountDetail(
                        chunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    @Override
    public long initMemoryReserve_Account(
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    chunkID,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapChunkExcept_Account(chunkID);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(chunkID, handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If an OutOfMemoryError is encountered this
     * calls
     * swapChunksExcept_AccountSuccess(_Grid2DSquareCell,_ChunkID,handleOutOfMemoryError)
     * then recurses.
     *
     * @param g
     * @param chunkID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public final void initMemoryReserve(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (swapChunkExcept_Account(g, chunkID) < 1L) {
                    throw e;
                }
                initMemoryReserve(g, chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    @Override
    public long initMemoryReserve_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    g,
                    chunkID,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapChunkExcept_Account(g, chunkID);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(
                        g,
                        chunkID,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If an OutOfMemoryError is encountered this
     * calls
     * swapChunksExcept_AccountSuccess(_Grid2DSquareCell,_ChunkID,handleOutOfMemoryError)
     * then recurses.
     *
     * @param g
     * @param chunkID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> initMemoryReserve_AccountDetail(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    g,
                    chunkID,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(g, chunkID);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = initMemoryReserve_AccountDetail(
                        g,
                        chunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> initMemoryReserve_AccountDetail(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    g,
                    chunkIDs,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(g, chunkIDs);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = initMemoryReserve_AccountDetail(
                        g,
                        chunkIDs,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> initMemoryReserve_AccountDetail(
            Grids_AbstractGrid g,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    g,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(g);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = initMemoryReserve_AccountDetail(
                        g,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    @Override
    public long initMemoryReserve_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    m,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapChunkExcept_Account(m);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(
                        m,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    @Override
    public long initMemoryReserve_Account(
            Grids_AbstractGrid g,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    g,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapChunkExcept_Account(
                        g);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(
                        g,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    @Override
    public long initMemoryReserve_Account(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    g,
                    chunkIDs,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapChunkExcept_Account(g, chunkIDs);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(g, chunkIDs, handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve.
     *
     * @param g
     * @param chunkIDs HashSet of Grids_AbstractGrid.ChunkIDs
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public final void initMemoryReserve(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            tryToEnsureThereIsEnoughMemoryToContinue(
                    g,
                    chunkIDs,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (swapChunkExcept_Account(g, chunkIDs) < 1L) {
                    throw e;
                }
                initMemoryReserve(g, chunkIDs, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises MemoryReserve. If an OutOfMemoryError is encountered this
     * calls
     * swapChunksExcept_AccountSuccess(_Grid2DSquareCell_ChunkIDHashSet,handleOutOfMemoryError)
     * then recurses.
     *
     * @param m
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public void initMemoryReserve(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            tryToEnsureThereIsEnoughMemoryToContinue(
                    m,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (swapChunkExcept_Account(
                        m) < 1) {
                    throw e;
                }
                initMemoryReserve(
                        m,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> initMemoryReserve_AccountDetail(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> chunksNotToSwap,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    chunksNotToSwap,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapExcept_AccountDetail(
                        chunksNotToSwap);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = initMemoryReserve_AccountDetail(
                        chunksNotToSwap,
                        handleOutOfMemoryError);
                combine(result, potentialPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to try to ensure there is enough memory to continue. If not
     * enough data is found to swap then an OutOfMemoryError is thrown.
     *
     * @return
     */
    @Override
    public boolean tryToEnsureThereIsEnoughMemoryToContinue(
            boolean handleOutOfMemoryError) {
        try {
            if (tryToEnsureThereIsEnoughMemoryToContinue()) {
                return true;
            } else {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue(boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (!tryToEnsureThereIsEnoughMemoryToContinue()) {
                    throw e;
                }
                initMemoryReserve(
                        handleOutOfMemoryError);
                return true;
            } else {
                throw e;
            }
        }
    }

    @Override
    protected boolean tryToEnsureThereIsEnoughMemoryToContinue() {
        if (NotToSwapData.isEmpty()) {
            return tryToEnsureThereIsEnoughMemoryToContinueSwapAny();
        } else {
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapChunkExcept_Account(NotToSwapData) < 1) {
                    return false;
                }
            }
            return tryToEnsureThereIsEnoughMemoryToContinueSwapAny();
        }
    }

    protected boolean tryToEnsureThereIsEnoughMemoryToContinueSwapAny() {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (swapChunk_Account() < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to try to ensure there is enough memory to continue. No data is
     * swapped from g. If not enough data is found to swap then an
     * OutOfMemoryError is thrown.
     *
     * @param g
     * @param handleOutOfMemoryError
     */
    @Override
    public void tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid g,
            boolean handleOutOfMemoryError) {
        try {
            if (!tryToEnsureThereIsEnoughMemoryToContinue(g)) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue(AbstractGrid2DSquareCell,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                boolean isEnoughMemoryToContinue;
                isEnoughMemoryToContinue = tryToEnsureThereIsEnoughMemoryToContinue(g);
                if (!isEnoughMemoryToContinue) {
                    throw e;
                }
                initMemoryReserve(g, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid g) {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (swapChunkExcept_Account(g) < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to try to ensure there is enough memory to continue. The Chunk
     * with chunkID from g is not swapped. If not enough data is found to swap
     * then an OutOfMemoryError is thrown.
     *
     * @param g
     * @param chunkID
     * @param handleOutOfMemoryError
     */
    @Override
    public void tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            if (!tryToEnsureThereIsEnoughMemoryToContinue(g, chunkID)) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue(Grid_AbstractGrid,Grids_2D_ID_int,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                boolean enough;
                enough = tryToEnsureThereIsEnoughMemoryToContinue(
                        g,
                        chunkID);
                if (!enough) {
                    throw e;
                }
                initMemoryReserve(
                        g,
                        chunkID,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (swapChunkExcept_Account(g, chunkID) < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to try to ensure there is enough memory to continue. No data is
     * swapped with chunkID. If not enough data is found to swap then an
     * OutOfMemoryError is thrown.
     *
     * @param chunkID
     * @param handleOutOfMemoryError
     */
    @Override
    public void tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            if (!tryToEnsureThereIsEnoughMemoryToContinue(chunkID)) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue(Grids_2D_ID_int,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                boolean enough = tryToEnsureThereIsEnoughMemoryToContinue(
                        chunkID);
                if (!enough) {
                    throw e;
                }
                initMemoryReserve(chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_2D_ID_int chunkID) {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (swapChunkExcept_Account(chunkID) < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to try to ensure there is enough memory to continue. No data is
     * swapped as identified by m. If not enough data is found to swap then an
     * OutOfMemoryError is thrown.
     *
     * @param m
     * @param handleOutOfMemoryError
     */
    @Override
    public void tryToEnsureThereIsEnoughMemoryToContinue(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
            boolean handleOutOfMemoryError) {
        try {
            if (!tryToEnsureThereIsEnoughMemoryToContinue(m)) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue(HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                boolean enough = tryToEnsureThereIsEnoughMemoryToContinue(m);
                if (!enough) {
                    throw e;
                }
                initMemoryReserve(m, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (swapChunkExcept_Account(m) < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * in g that has an id in chunkIDs.
     *
     * @param g
     * @param handleOutOfMemoryError
     * @param chunkIDs
     */
    @Override
    public void tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs,
            boolean handleOutOfMemoryError) {
        try {
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapChunkExcept_Account(g, chunkIDs) < 1) {
                    System.out.println(
                            "Warning! Nothing to swap in "
                            + this.getClass().getName()
                            + ".tryToEnsureThereIsEnoughMemoryToContinue(AbstractGrid2DSquareCell,HashSet<ChunkID>,boolean)");
                    // Set to exit method with OutOfMemoryError
                    handleOutOfMemoryError = false;
                    throw new OutOfMemoryError();
                }
            }
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                boolean createdRoom = false;
                while (!createdRoom) {
                    if (swapChunkExcept_Account(g, chunkIDs) < 1L) {
                        System.out.println(
                                "Warning! Nothing to swap in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue(AbstractGrid2DSquareCell,HashSet<ChunkID>,boolean) after encountering an OutOfMemoryError");
                        throw e;
                    }
                    initMemoryReserve(
                            g,
                            chunkIDs,
                            handleOutOfMemoryError);
                    tryToEnsureThereIsEnoughMemoryToContinue(
                            g,
                            chunkIDs,
                            handleOutOfMemoryError);
                    createdRoom = true;
                }
            } else {
                throw e;
            }
        }
    }

    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (swapChunkExcept_Account(g, chunkIDs) < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to ensure there is enough memory to continue. An attempt at
     * Grids internal memory handling is performed if an OutOfMemoryError is
     * encountered and handleOutOfMemoryError is true. This method may throw an
     * OutOfMemoryError if there is not enough data to swap in Grids.
     *
     * @param handleOutOfMemoryError
     * @return Number of chunks swapped.
     */
    @Override
    public long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            boolean handleOutOfMemoryError) {
        try {
            Account test = tryToEnsureThereIsEnoughMemoryToContinue_Account();
            if (test == null) {
                return 0;
            }
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                        handleOutOfMemoryError);
                result += initMemoryReserve_Account(
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to try to ensure there is enough memory to continue. This method
     * should quickly return null if there is enough memory to continue. If
     * there is not enough memory to continue it will attempt to make room. This
     * will return an Account indicating the data that was swapped and whether
     * this created enough memory to continue.
     *
     * @return
     */
    protected Account tryToEnsureThereIsEnoughMemoryToContinue_Account() {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapChunk_Account() < 1) {
                    return result;
                } else {
                    result.Detail++;
                }
            }
            result.Success = true;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * from g.
     *
     * @param g
     * @param handleOutOfMemoryError
     * @return Number of chunks swapped.
     */
    @Override
    public long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid g,
            boolean handleOutOfMemoryError) {
        try {
            Account test = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    g);
            if (test == null) {
                return 0;
            }
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(AbstractGrid2DSquareCell,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                        g,
                        handleOutOfMemoryError);
                result += initMemoryReserve_Account(
                        g,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected Account tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid g) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapChunkExcept_Account(g) < 1) {
                    return result;
                } else {
                    result.Detail++;
                }
            }
            result.Success = true;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. The Chunk with
     * chunkID from g is not swapped.
     *
     * @param g
     * @param handleOutOfMemoryError
     * @param chunkID
     * @return Number of chunks swapped.
     */
    @Override
    public long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            Account test;
            test = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    g,
                    chunkID);
            if (test == null) {
                return 0;
            }
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account("
                        + g.getClass().getName() + ","
                        + chunkID.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                        g,
                        chunkID,
                        handleOutOfMemoryError);
                result += initMemoryReserve_Account(
                        g,
                        chunkID,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    public Account tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapChunkExcept_Account(g, chunkID) < 1) {
                    return result;
                } else {
                    result.Detail++;
                }
            }
            result.Success = true;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * with chunkID.
     *
     * @param chunkID
     * @param handleOutOfMemoryError
     * @return Number of chunks swapped.
     */
    @Override
    public long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            Account test = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    chunkID);
            if (test == null) {
                return 0;
            }
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account("
                        + chunkID.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                        chunkID,
                        handleOutOfMemoryError);
                result += initMemoryReserve_Account(
                        chunkID,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    public Account tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_2D_ID_int chunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapChunkExcept_Account(chunkID) < 1) {
                    return result;
                } else {
                    result.Detail++;
                }
            }
            result.Success = true;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * as identified by m.
     *
     * @param m
     * @param handleOutOfMemoryError
     * @return Number of chunks swapped.
     */
    @Override
    public long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
            boolean handleOutOfMemoryError) {
        try {
            Account test = tryToEnsureThereIsEnoughMemoryToContinue_Account(m);
            if (test == null) {
                return 0;
            }
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account("
                        + m.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                        m,
                        handleOutOfMemoryError);
                result += initMemoryReserve_Account(
                        m,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    public Account tryToEnsureThereIsEnoughMemoryToContinue_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapChunkExcept_Account(m) < 1) {
                    return result;
                } else {
                    result.Detail++;
                }
            }
            result.Success = true;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * from chunks in g.
     *
     * @param g
     * @param handleOutOfMemoryError
     * @param chunks
     * @return Number of chunks swapped.
     */
    @Override
    public long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunks,
            boolean handleOutOfMemoryError) {
        try {
            Account test = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    g,
                    chunks);
            if (test == null) {
                return 0;
            }
            if (!test.Success) {
                String message;
                message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account("
                        + g.getClass().getName() + ","
                        + chunks.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                return freeSomeMemoryAndResetReserve_Account(
                        g,
                        chunks,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    public Account tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Account result = new Account();
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapChunkExcept_Account(g, chunkIDs) < 1) {
                    return result;
                } else {
                    result.Detail++;
                }
            }
            result.Success = true;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. An attempt at
     * Grids internal memory handling is performed if an OutOfMemoryError is
     * encountered and handleOutOfMemoryError is true. This method may throw an
     * OutOfMemoryError if there is not enough data to swap in Grids.
     *
     * @param handleOutOfMemoryError
     * @return Number of chunks swapped. A method to ensure there is enough
     * memory to continue. For this to work accounting must be less expesive in
     * terms of data size than swapping data!
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    boolean handleOutOfMemoryError) {
        try {
            AccountDetail test;
            test = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail();
            if (test == null) {
                return null;
            }
            boolean test0 = test.Success;
            if (!test0) {
                String message;
                message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail("
                        + "boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                return freeSomeMemoryAndResetReserve_AccountDetails(
                        handleOutOfMemoryError);
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
    protected AccountDetail tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail() {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            while (getTotalFreeMemory() < Memory_Threshold) {
                potentialPartResult = swapChunk_AccountDetail();
                if (potentialPartResult.isEmpty()) {
                    result.Success = false;
                    return result;
                } else {
                    combine(result.Detail, potentialPartResult);
                }
            }
            result.Success = true;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * from g. For this to work accounting must be less expensive in terms of
     * data size than swapping data!
     *
     * @param g
     * @param handleOutOfMemoryError
     * @return HashMap identifying chunks swapped.
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    Grids_AbstractGrid g,
                    boolean handleOutOfMemoryError) {
        try {
            AccountDetail test;
            test = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(g);
            if (test == null) {
                return null;
            }
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail("
                        + g.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                return freeSomeMemoryAndResetReserve_AccountDetails(
                        g,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected AccountDetail tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_AbstractGrid g) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            while (getTotalFreeMemory() < Memory_Threshold) {
                potentialPartResult = swapChunk_AccountDetail();
                if (potentialPartResult.isEmpty()) {
                    return result;
                } else {
                    combine(result.Detail, potentialPartResult);
                }
            }
            result.Success = true;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. The Chunk with
     * chunkID from g is not swapped. For this to work accounting must be less
     * expensive in terms of data size than swapping data!
     *
     * @param g
     * @param handleOutOfMemoryError
     * @param chunkID
     * @return HashMap identifying chunks swapped.
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    Grids_AbstractGrid g,
                    Grids_2D_ID_int chunkID,
                    boolean handleOutOfMemoryError) {
        try {
            AccountDetail test = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    g,
                    chunkID);
            if (test == null) {
                return null;
            }
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail("
                        + g.getClass().getName() + ","
                        + chunkID.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                        g,
                        chunkID,
                        handleOutOfMemoryError);
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = initMemoryReserve_AccountDetail(
                        g,
                        chunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected AccountDetail tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            while (getTotalFreeMemory() < Memory_Threshold) {
                potentialPartResult = swapChunkExcept_AccountDetail(
                        g,
                        chunkID);
                if (potentialPartResult.isEmpty()) {
                    return result;
                } else {
                    combine(result.Detail, potentialPartResult);
                }
            }
            result.Success = true;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * with chunkID. For this to work accounting must be less expensive in terms
     * of data size than swapping data!
     *
     * @param chunkID
     * @param handleOutOfMemoryError
     * @return HashMap identifying chunks swapped.
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            AccountDetail result;
            result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(chunkID);
            if (result == null) {
                return null;
            }
            boolean resultPart0 = result.Success;
            if (!resultPart0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail("
                        + chunkID.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return result.Detail;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                        chunkID,
                        handleOutOfMemoryError);
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = initMemoryReserve_AccountDetail(
                        chunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected AccountDetail tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_2D_ID_int chunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
//            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> resultPart1;
//            resultPart1 = new HashMap<>(1);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            while (getTotalFreeMemory() < Memory_Threshold) {
                potentialPartResult = swapChunkExcept_AccountDetail(
                        chunkID);
                if (potentialPartResult.isEmpty()) {
                    result.Success = false;
                    return result;
                } else {
                    combine(result.Detail, potentialPartResult);
                }
            }
            result.Success = true;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * as identified by m. For this to work accounting must be less expensive in
     * terms of data size than swapping data!
     *
     * @param m
     * @param handleOutOfMemoryError
     * @return HashMap identifying chunks swapped.
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
                    boolean handleOutOfMemoryError) {
        try {
            AccountDetail test;
            test = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(m);
            if (test == null) {
                return null;
            }
            if (test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail("
                        + m.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                        m,
                        handleOutOfMemoryError);
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = initMemoryReserve_AccountDetail(
                        m,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected AccountDetail tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            while (getTotalFreeMemory() < Memory_Threshold) {
                potentialPartResult = swapExcept_AccountDetail(m);
                if (potentialPartResult.isEmpty()) {
                    result.Success = false;
                    return result;
                } else {
                    combine(result.Detail, potentialPartResult);
                }
            }
            result.Success = true;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No chunks are
     * swapped from g. For this to work accounting must be less expensive in
     * terms of data size than swapping data!
     *
     * @param g
     * @param handleOutOfMemoryError
     * @param chunkIDs
     * @return HashMap identifying chunks swapped.
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    Grids_AbstractGrid g,
                    HashSet<Grids_2D_ID_int> chunkIDs,
                    boolean handleOutOfMemoryError) {
        try {
            AccountDetail test;
            test = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    g,
                    chunkIDs);
            if (test == null) {
                return null;
            }
            if (!test.Success) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail("
                        + g.getClass().getName() + ","
                        + chunkIDs.getClass().getName() + ",boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return test.Detail;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                return freeSomeMemoryAndResetReserve_AccountDetails(
                        g,
                        chunkIDs,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected AccountDetail tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            AccountDetail result = new AccountDetail();
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            while (getTotalFreeMemory() < Memory_Threshold) {
                potentialPartResult = swapChunkExcept_AccountDetail(
                        g,
                        chunkIDs);
                if (potentialPartResult.isEmpty()) {
                    return result;
                } else {
                    combine(result.Detail, potentialPartResult);
                }
            }
            result.Success = true;
        }
        return null;
    }

    /**
     * Attempts to swap all Grids_AbstractGridChunk in this.Grids.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunks_AccountDetail(
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunks_AccountDetail();
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail();
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                    potentialPartResult = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                            handleOutOfMemoryError);
                    combine(result, potentialPartResult);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                return freeSomeMemoryAndResetReserve_AccountDetails(e, handleOutOfMemoryError);
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
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunks_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        while (ite.hasNext()) {
            partResult = ite.next().swapChunks_AccountDetail(
                    HandleOutOfMemoryErrorFalse);
            combine(result,
                    partResult);
        }
        return result;
    }

    /**
     * Attempts to swap all chunks in ge.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return A count of the number of chunks swapped.
     */
    public long swapChunks_Account(
            boolean handleOutOfMemoryError) {
        try {
            long result;
            try {
                result = swapChunks_Account();
                if (result < 1) {
                    Account account = tryToEnsureThereIsEnoughMemoryToContinue_Account();
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = freeSomeMemoryAndResetReserve_Account(e, handleOutOfMemoryError);
                result += swapChunks_Account(handleOutOfMemoryError);
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
        ite = this.getGrids().iterator();
        while (ite.hasNext()) {
            long partResult;
            Grids_AbstractGrid g;
            g = ite.next();
            partResult = swapChunks_Account(HandleOutOfMemoryErrorFalse);
            result += partResult;
        }
        dataToSwap = false;
        return result;
    }

    /**
     * Attempts to swap all Grids_AbstractGridChunk in this.Grids.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void swapChunks(boolean handleOutOfMemoryError) {
        try {
            boolean success = swapChunks();
            try {
                if (!success) {
                    tryToEnsureThereIsEnoughMemoryToContinue();
                } else {
                    tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (swapChunk_Account() < 1L) {
                    throw e;
                }
                initMemoryReserve(handleOutOfMemoryError);
                swapChunks();
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
    protected boolean swapChunks() {
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        while (ite.hasNext()) {
            ite.next().swapChunks(HandleOutOfMemoryErrorFalse);
        }
        dataToSwap = false;
        return true;
    }

    /**
     * Attempts to swap any Grids_AbstractGridChunk in this.Grids. This is the
     * lowest level of OutOfMemoryError handling in this class.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunk_AccountDetail(
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunk_AccountDetail();
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail();
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                    potentialPartResult = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                            handleOutOfMemoryError);
                    combine(result, potentialPartResult);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                return freeSomeMemoryAndResetReserve_AccountDetails(e, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     *
     * @param handleOutOfMemoryError
     */
    public void swapChunk(
            boolean handleOutOfMemoryError) {
        try {
            boolean success = swapChunk();
            try {
                if (!success) {
                    Account account = tryToEnsureThereIsEnoughMemoryToContinue_Account();
                    if (account != null) {
                        if (!account.Success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (swapChunk_Account() < 1) {
                    throw e;
                }
                initMemoryReserve(handleOutOfMemoryError);
                // No need for recursive call: swapChunk(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap any Grids_AbstractGridChunk in this.Grids.
     *
     * @return
     */
    protected boolean swapChunk() {
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        while (ite.hasNext()) {
            return ite.next().swapChunk_Account(HandleOutOfMemoryErrorFalse) >= 1;
        }
        dataToSwap = false;
        return false;
    }

    /**
     * Attempts to swap any Grids_AbstractGridChunk in grid
     *
     * @param grid
     * @return
     */
    protected boolean swapChunk(
            Grids_AbstractGrid grid) {
        return grid.swapChunk_Account(HandleOutOfMemoryErrorFalse) >= 1;
    }

//    /**
//     * Attempts to swap any Grids_AbstractGridChunk in grid
//     *
//     * @param grid
//     * @return
//     */
//    protected void swapChunk(
//            Grids_AbstractGrid grid,
//            Grids_2D_ID_int chunkID) {
//        grid.swapChunk(chunkID);
//    }
    public long swapChunk_Account(
            boolean handleOutOfMemoryError) {
        try {
            long result = swapChunk_Account();
            try {
                if (result < 1) {
                    Account account = tryToEnsureThereIsEnoughMemoryToContinue_Account();
                    if (account != null) {
                        if (!account.Success) {
                            throw new OutOfMemoryError();
                        } else {
                            result += account.Detail;
                        }
                    }
                } else {
                    long account = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            handleOutOfMemoryError);
                    result += account;
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                return freeSomeMemoryAndResetReserve_Account(e, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected long swapChunk_Account() {
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        while (ite.hasNext()) {
            if (ite.next().swapChunk_Account() > 0) {
                return 1L;
            }
        }
        dataToSwap = false;
        return 0L;
    }

    /**
     * Swap to File any GridChunk in Grids except one in g.
     *
     * @param g
     * @param handleOutOfMemoryError
     */
    public void swapChunkExcept(
            Grids_AbstractGrid g,
            boolean handleOutOfMemoryError) {
        try {
            boolean success = swapChunkExcept(g);
            try {
                if (!success) {
                    Account account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_Account(g);
                    if (account != null) {
                        if (!account.Success) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    tryToEnsureThereIsEnoughMemoryToContinue(
                            g,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (swapChunkExcept_Account(g) < 1L) {
                    throw e;
                }
                initMemoryReserve(g, handleOutOfMemoryError);
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
                if (bg.swapChunk_Account(HandleOutOfMemoryErrorFalse) > 0) {
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
                Set<Grids_AbstractGrid> toGetCombined_KeySet = toGetCombined.keySet();
                Set<Grids_AbstractGrid> toCombine_KeySet = toCombine.keySet();
                Iterator<Grids_AbstractGrid> toCombine_KeySet_Iterator = toCombine_KeySet.iterator();
                Grids_AbstractGrid g;
                while (toCombine_KeySet_Iterator.hasNext()) {
                    g = toCombine_KeySet_Iterator.next();
                    if (toGetCombined_KeySet.contains(g)) {
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
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunk_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid g = ite.next();
            result = g.swapChunk_AccountDetail(
                    HandleOutOfMemoryErrorFalse);
            if (!result.isEmpty()) {
                return result;
            }
        }
        dataToSwap = false;
        return null;
    }

    /**
     * @param handleOutOfMemoryError
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this.Grids except for those in with
     * Grids_AbstractGrid.ID = _ChunkID.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunkExcept_AccountDetail(
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunkExcept_AccountDetail(
                    chunkID);
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                            chunkID);
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                    potentialPartResult = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                            chunkID,
                            handleOutOfMemoryError);
                    combine(result, potentialPartResult);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(
                        chunkID);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = initMemoryReserve_AccountDetail(
                        chunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
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
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunkExcept_AccountDetail(
            Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        Grids_AbstractGrid g;
        while (ite.hasNext()) {
            g = ite.next();
            result = g.swapChunkExcept_AccountDetail(
                    chunkID, HandleOutOfMemoryErrorFalse);
            if (!result.isEmpty()) {
                HashSet<Grids_2D_ID_int> chunkIDs = new HashSet<>(1);
                chunkIDs.add(chunkID);
                result.put(
                        g,
                        chunkIDs);
                return result;
            }
        }
        return null;
    }

    public long swapChunkExcept_Account(
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapChunkExcept_Account(chunkID);
            try {
                if (result < 1) {
                    Account account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_Account(chunkID);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            chunkID,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            result += tryToEnsureThereIsEnoughMemoryToContinue_Account(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapChunkExcept_Account(chunkID);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(
                        chunkID,
                        handleOutOfMemoryError);
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
    protected long swapChunkExcept_Account(
            Grids_2D_ID_int chunkID) {
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        Grids_AbstractGrid g;
        while (ite.hasNext()) {
            g = ite.next();
            result += swapChunkExcept_Account(chunkID);
            if (result > 0L) {
                return result;
            }
        }
        return result;
    }

    /**
     * @param chunkID The id of the GridChunk not to be swapped.
     */
    protected void swapChunkExcept(
            Grids_2D_ID_int chunkID) {
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        Grids_AbstractGrid g;
        while (ite.hasNext()) {
            g = ite.next();
            if (swapChunkExcept_Account(chunkID) > 0) {
                return;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this.Grids except for those in
     * _Grid2DSquareCell_ChunkIDHashSet.
     * @param m HashMap with Grids_AbstractGrid as keys and a respective HashSet
     * of Grids_AbstractGrid.ChunkIDs. Identifying those ChunkIDs not to be
     * swapped from the Grids_AbstractGrid. TODO
     * tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(<Grids_AbstractGrid,
     * HashSet<ChunkID>>,boolean);
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunkExcept_AccountDetail(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapExcept_AccountDetail(m);
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(m);
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                    potentialPartResult = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                            m,
                            handleOutOfMemoryError);
                    combine(result,
                            potentialPartResult);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapExcept_AccountDetail(m);
                if (result.isEmpty()) {
                    throw e;
                }
                initMemoryReserve(m, handleOutOfMemoryError);
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
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapExcept_AccountDetail(
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
                chunkID = g.swapChunkExcept_AccountChunk(
                        chunkID_HashSet,
                        HandleOutOfMemoryErrorFalse);
                if (chunkID != null) {
                    result_ChunkID_HashSet.add(chunkID);
                    result.put(
                            g,
                            result_ChunkID_HashSet);
                    return result;
                }
            }
            chunkID = g.swapChunk_AccountChunk(HandleOutOfMemoryErrorFalse);
            if (chunkID != null) {
                result_ChunkID_HashSet.add(chunkID);
                result.put(
                        g,
                        result_ChunkID_HashSet);
                return result;
            }
        }
        return result; // If here then nothing could be swapped!
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunkExcept_AccountDetail(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite;
        ite = getGrids().iterator();
        Grids_AbstractGrid gb;
        HashSet<Grids_2D_ID_int> resultPart;
        resultPart = new HashSet<>(1);
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            gb = ite.next();
            if (g == gb) {
                chunkID = gb.swapChunkExcept_AccountChunk(
                        chunkIDs,
                        HandleOutOfMemoryErrorFalse);
                if (chunkID != null) {
                    resultPart.add(chunkID);
                    result.put(g, resultPart);
                    return result;
                }
            } else {
                chunkID = g.swapChunk_AccountChunk(HandleOutOfMemoryErrorFalse);
                if (chunkID != null) {
                    resultPart.add(chunkID);
                    result.put(g, resultPart);
                    return result;
                }
            }
        }
        return result; // If here then nothing could be swapped!
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunkExcept_AccountDetail(
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
                chunkIDb = gb.swapChunkExcept_AccountChunk(
                        chunkID,
                        HandleOutOfMemoryErrorFalse);
                if (chunkIDb != null) {
                    resultPart.add(chunkIDb);
                    result.put(g, resultPart);
                    return result;
                }
            } else {
                chunkIDb = g.swapChunk_AccountChunk(HandleOutOfMemoryErrorFalse);
                if (chunkIDb != null) {
                    resultPart.add(chunkIDb);
                    result.put(g, resultPart);
                    return result;
                }
            }
        }
        return result; // If here then nothing could be swapped!
    }

    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunkExcept_AccountDetail(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunkExcept_AccountDetail(
                    g,
                    chunkID);
            return result;
        } catch (java.lang.OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = swapChunkExcept_AccountDetail(
                        g,
                        chunkID);
                if (partResult.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = initMemoryReserve_AccountDetail(
                        g,
                        chunkID,
                        handleOutOfMemoryError);
                combine(result, partResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunkExcept_AccountDetail(
            Grids_AbstractGrid g) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite;
        ite = this.getGrids().iterator();
        Grids_AbstractGrid gb;
        HashSet<Grids_2D_ID_int> resultPart;
        resultPart = new HashSet<>(1);
        Grids_2D_ID_int b_ChunkID;
        while (ite.hasNext()) {
            gb = ite.next();
            if (g != gb) {
                b_ChunkID = gb.swapChunk_AccountChunk(HandleOutOfMemoryErrorFalse);
                if (b_ChunkID != null) {
                    resultPart.add(b_ChunkID);
                    result.put(g, resultPart);
                    return result;
                }
            }
        }
        return result; // If here then nothing could be swapped!
    }

    protected long swapChunkExcept_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        Grids_AbstractGrid g;
        HashSet<Grids_2D_ID_int> chunkIDs;
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            g = ite.next();
            if (m.containsKey(g)) {
                chunkIDs = m.get(g);
                chunkID = g.swapChunkExcept_AccountChunk(
                        chunkIDs,
                        HandleOutOfMemoryErrorFalse);
                if (chunkID != null) {
                    return 1L;
                }
            }
            chunkID = g.swapChunk_AccountChunk(HandleOutOfMemoryErrorFalse);
            if (chunkID != null) {
                return 1L;
            }
        }
        return result; // If here then nothing could be swapped!
    }

    protected void swapChunkExcept(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m) {
        Iterator<Grids_AbstractGrid> ite;
        ite = getGrids().iterator();
        Grids_AbstractGrid g;
        HashSet<Grids_2D_ID_int> chunkIDs;
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            g = ite.next();
            if (m.containsKey(g)) {
                chunkIDs = m.get(g);
                chunkID = g.swapChunkExcept_AccountChunk(
                        chunkIDs,
                        HandleOutOfMemoryErrorFalse);
                if (chunkID != null) {
                    return;
                }
            }
            chunkID = g.swapChunk_AccountChunk(HandleOutOfMemoryErrorFalse);
            if (chunkID != null) {
                return;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
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
            boolean handleOutOfMemoryError) {
        try {
            long result;
            result = swapChunkExcept_Account(g, chunkIDs);
            try {
                if (result < 1) {
                    Account account = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            g,
                            chunkIDs);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            g,
                            chunkIDs,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapChunkExcept_Account(g, chunkIDs);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(
                        g,
                        chunkIDs,
                        handleOutOfMemoryError);
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
        ite = getGrids().iterator();
        Grids_AbstractGrid gb;
        HashMap<Grids_2D_ID_int, Grids_AbstractGridChunk> m;
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
                            gb.swapChunk(chunkID, HandleOutOfMemoryErrorFalse);
                            return 1;
                        }
                    }
                }
            }
        }
        return 0L;
    }

    /**
     * @param handleOutOfMemoryError
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
            boolean handleOutOfMemoryError) {
        try {
            long result;
            result = swapChunkExcept_Account(g, chunkID);
            try {
                if (result < 1) {
                    Account account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            g,
                            chunkID);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            g,
                            chunkID,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapChunkExcept_Account(
                        g,
                        chunkID);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(
                        g,
                        chunkID,
                        handleOutOfMemoryError);
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
        long result = swapChunkExcept_Account(
                g);
        if (result < 1L) {
            result = g.swapChunkExcept_Account(
                    chunkID,
                    HandleOutOfMemoryErrorFalse);
        }
        return result;
    }

    /**
     * @param handleOutOfMemoryError
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this.Grids except for those in
     * _Grid2DSquareCell.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     */
    public long swapChunkExcept_Account(
            Grids_AbstractGrid g,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapChunkExcept_Account(
                    g);
            try {
                if (result < 1) {
                    Account account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_Account(g);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(g, handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapChunkExcept_Account(g);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(g, handleOutOfMemoryError);
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
    protected long swapChunkExcept_Account(
            Grids_AbstractGrid g) {
        Iterator<Grids_AbstractGrid> ite;
        ite = getGrids().iterator();
        Grids_AbstractGrid gb;
        while (ite.hasNext()) {
            gb = ite.next();
            if (gb != g) {
                Grids_2D_ID_int chunkID;
                chunkID = gb.swapChunk_AccountChunk(
                        HandleOutOfMemoryErrorFalse);
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
     * @param handleOutOfMemoryError
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunksExcept_AccountDetail(
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunksExcept_AccountDetail(chunkID);
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(chunkID);
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                    potentialPartResult = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                            chunkID, handleOutOfMemoryError);
                    combine(result, potentialPartResult);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(chunkID);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = initMemoryReserve_AccountDetail(
                        chunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                potentialPartResult = swapChunksExcept_AccountDetail(
                        chunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
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
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunksExcept_AccountDetail(
            Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite = Grids.iterator();
        while (ite.hasNext()) {
            Grids_AbstractGrid g = ite.next();
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            potentialPartResult = g.swapChunksExcept_AccountDetail(
                    chunkID, false);
            combine(result,
                    potentialPartResult);
        }
        return result;
    }

    /**
     * Attempts to Swap all Grids_AbstractGrid.ChunkIDs in this.Grids except
     * those with Grids_AbstractGrid.ID _ChunkID.
     *
     * @param handleOutOfMemoryError
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped. swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunksExcept_AccountDetail(
            Grids_AbstractGrid g,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunksExcept_AccountDetail(g);
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(g);
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                    potentialPartResult = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                            g, handleOutOfMemoryError);
                    combine(result, potentialPartResult);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(g);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = initMemoryReserve_AccountDetail(
                        g,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                potentialPartResult = swapChunksExcept_AccountDetail(
                        g,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
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
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunksExcept_AccountDetail(
            Grids_AbstractGrid g) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid gb;
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
        while (ite.hasNext()) {
            gb = ite.next();
            if (gb != g) {
                potentialPartResult = gb.swapChunks_AccountDetail(false);
                combine(result,
                        potentialPartResult);
            }
        }
        return result;
    }

    public long swapChunksExcept_Account(
            Grids_AbstractGrid g,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapChunksExcept_Account(g);
            try {
                if (result < 1) {
                    Account account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            g);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            g,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapChunkExcept_Account(g);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(
                        g,
                        handleOutOfMemoryError);
                result += swapChunksExcept_Account(
                        g,
                        handleOutOfMemoryError);
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
     * @param handleOutOfMemoryError
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunksExcept_AccountDetail(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunksExcept_AccountDetail(
                    g,
                    chunkID);
            try {
                if (result.isEmpty()) {
                    AccountDetail account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                            g,
                            chunkID);
                    if (account != null) {
                        if (account.Success) {
                            combine(result, account.Detail);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                            = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                                    g,
                                    chunkID,
                                    handleOutOfMemoryError);
                    combine(result, potentialPartResult);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(
                        g,
                        chunkID);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = initMemoryReserve_AccountDetail(
                        g,
                        chunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                potentialPartResult = swapChunksExcept_AccountDetail(
                        g,
                        chunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    public long swapChunksExcept_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapChunksExcept_Account(
                    g,
                    chunkID);
            try {
                if (result < 1) {
                    Account account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            g,
                            chunkID);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            g,
                            chunkID,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapChunkExcept_Account(
                        g,
                        chunkID);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(
                        chunkID,
                        handleOutOfMemoryError);
                result += swapChunkExcept_Account(
                        g,
                        chunkID);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected long swapChunksExcept_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        boolean handleOutOfMemoryError = false;
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid gb;
        while (ite.hasNext()) {
            gb = ite.next();
            if (gb != g) {
                int cri0 = 0;
                int cci0 = 0;
                int cri1 = gb.getNChunkRows(handleOutOfMemoryError) - 1;
                int cci1 = gb.getNChunkCols(handleOutOfMemoryError) - 1;
                result += gb.swapChunks_Account(
                        cri0, cci0, cri1, cci1, handleOutOfMemoryError);
            } else {
                result += gb.swapChunksExcept_Account(
                        chunkID, handleOutOfMemoryError);
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
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunksExcept_AccountDetail(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid bg;
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
        while (ite.hasNext()) {
            bg = ite.next();
            if (bg == g) {
                potentialPartResult = bg.swapChunksExcept_AccountDetail(
                        chunkID, false);
                combine(result,
                        potentialPartResult);
            } else {
                potentialPartResult = bg.swapChunks_AccountDetail(false);
                combine(result,
                        potentialPartResult);
            }
        }
        return result;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunksExcept_AccountDetail(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid gb;
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
        while (ite.hasNext()) {
            gb = ite.next();
            if (gb != g) {
                potentialPartResult = gb.swapChunks_AccountDetail(false);
                combine(result, potentialPartResult);
            } else {
                HashSet<Grids_2D_ID_int> chunks;
                chunks = g.getChunkIDs(false);
                Grids_2D_ID_int id;
                Iterator<Grids_2D_ID_int> ite2;
                ite2 = chunks.iterator();
                while (ite2.hasNext()) {
                    id = ite2.next();
                    if (!chunkIDs.contains(id)) {
                        potentialPartResult = swapChunksExcept_AccountDetail(
                                id, false);
                        combine(result,
                                potentialPartResult);
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
        boolean handleOutOfMemoryError = false;
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite;
        ite = Grids.iterator();
        Grids_AbstractGrid gb;
        while (ite.hasNext()) {
            gb = ite.next();
            if (gb != g) {
                int cri0 = 0;
                int cri1 = gb.getNChunkRows(handleOutOfMemoryError) - 1;
                int cci0 = 0;
                int cci1 = gb.getNChunkCols(handleOutOfMemoryError) - 1;
                result += gb.swapChunks_Account(
                        cri0, cci0, cri1, cci1, handleOutOfMemoryError);
            } else {
                result += gb.swapChunksExcept_Account(
                        chunkIDs, handleOutOfMemoryError);
            }
        }
        return result;
    }

    /**
     * @param m
     * @param handleOutOfMemoryError
     * @return
     */
    public long swapChunksExcept_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> m,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapChunksExcept_Account(m);
            try {
                if (result < 1) {
                    Account account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_Account(m);
                    if (account != null) {
                        if (account.Success) {
                            result += account.Detail;
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            m,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError e) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapChunkExcept_Account(m);
                if (result < 1L) {
                    throw e;
                }
                result += initMemoryReserve_Account(m, handleOutOfMemoryError);
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
            result += g.swapChunksExcept_Account(
                    chunkIDs, false);
        }
        return result;
    }

    public void swapData() {
        swapChunks();
    }

    public void swapData(boolean handleOutOfMemoryError) {
        swapChunks();
    }

    @Override
    public boolean swapDataAny(boolean handleOutOfMemoryError) {
        try {
            boolean result = swapChunk();
            try {
                if (!tryToEnsureThereIsEnoughMemoryToContinue()) {
                    throw new OutOfMemoryError();
                }
            } catch (OutOfMemoryError e) {
                // Exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw e;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                boolean result = swapDataAny(HandleOutOfMemoryErrorFalse);
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

    private boolean dataToSwap = true;

    public boolean isDataToSwap() {
        return dataToSwap;
    }

    public void setDataToSwap(boolean dataToSwap) {
        this.dataToSwap = dataToSwap;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    Grids_AbstractGrid g,
                    boolean handleOutOfMemoryError) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                g,
                handleOutOfMemoryError);
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
        potentialPartResult = initMemoryReserve_AccountDetail(
                g,
                handleOutOfMemoryError);
        combine(result,
                potentialPartResult);
        return result;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    Grids_AbstractGrid g,
                    HashSet<Grids_2D_ID_int> chunks,
                    boolean handleOutOfMemoryError) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                g,
                chunks,
                handleOutOfMemoryError);
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
        potentialPartResult = initMemoryReserve_AccountDetail(
                g,
                chunks,
                handleOutOfMemoryError);
        combine(result,
                potentialPartResult);
        return result;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    OutOfMemoryError e,
                    boolean handleOutOfMemoryError) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = swapChunk_AccountDetail();
        if (result.isEmpty()) {
            throw e;
        }
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
        potentialPartResult = initMemoryReserve_AccountDetail(handleOutOfMemoryError);
        combine(result,
                potentialPartResult);
        return result;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            freeSomeMemoryAndResetReserve_AccountDetails(
                    boolean handleOutOfMemoryError) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                handleOutOfMemoryError);
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
        potentialPartResult = initMemoryReserve_AccountDetail(
                handleOutOfMemoryError);
        combine(result,
                potentialPartResult);
        return result;
    }

    protected long freeSomeMemoryAndResetReserve_Account(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunks,
            boolean handleOutOfMemoryError) {
        long result = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                g,
                chunks,
                handleOutOfMemoryError);
        result += initMemoryReserve_Account(
                g,
                chunks,
                handleOutOfMemoryError);
        return result;
    }

    protected long freeSomeMemoryAndResetReserve_Account(
            OutOfMemoryError e,
            boolean handleOutOfMemoryError) {
        long result = swapChunk_Account();
        if (result < 1L) {
            throw e;
        }
        result += initMemoryReserve_Account(
                handleOutOfMemoryError);
        return result;
    }

    /**
     * @return the NotToSwapData
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> getNotToSwapData() {
        return NotToSwapData;
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
