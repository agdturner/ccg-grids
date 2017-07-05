/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.ccg.andyt.grids.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCell.ChunkID;

/**
 *
 * @author Andy
 */
public interface Grids_OutOfMemoryErrorHandlerInterface
        extends Serializable {

    /**
     * A method to ensure there is enough memory to continue.
     * @param handleOutOfMemoryError
     * @return 
     */
    boolean tryToEnsureThereIsEnoughMemoryToContinue(boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * from a_Grid2DSquareCell.
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     */
    void tryToEnsureThereIsEnoughMemoryToContinue(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue. The Chunk with
     * a_ChunkID from a_Grid2DSquareCell is not swapped.
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * @param a_ChunkID
     */
    void tryToEnsureThereIsEnoughMemoryToContinue(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, ChunkID a_ChunkID, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * with a_ChunkID.
     * @param a_ChunkID
     * @param handleOutOfMemoryError
     */
    void tryToEnsureThereIsEnoughMemoryToContinue(ChunkID a_ChunkID, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * as identified by a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap.
     * @param a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap
     * @param handleOutOfMemoryError
     */
    void tryToEnsureThereIsEnoughMemoryToContinue(HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * in a_Grid2DSquareCell that has ChunkID in a_ChunkID_HashSet.
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * @param a_ChunkID_HashSet
     */
    void tryToEnsureThereIsEnoughMemoryToContinue(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, HashSet<ChunkID> a_ChunkID_HashSet, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue.
     * @param handleOutOfMemoryError
     * @return Number of chunks swapped.
     */
    long tryToEnsureThereIsEnoughMemoryToContinue_Account(boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * from a_Grid2DSquareCell.
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * @return Number of chunks swapped.
     */
    long tryToEnsureThereIsEnoughMemoryToContinue_Account(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue. The Chunk with
     * a_ChunkID from a_Grid2DSquareCell is not swapped.
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * @param a_ChunkID
     * @return Number of chunks swapped.
     */
    long tryToEnsureThereIsEnoughMemoryToContinue_Account(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, ChunkID a_ChunkID, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * with a_ChunkID.
     * @param a_ChunkID
     * @param handleOutOfMemoryError
     * @return Number of chunks swapped.
     */
    long tryToEnsureThereIsEnoughMemoryToContinue_Account(ChunkID a_ChunkID, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * as identified by a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap.
     * @param a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap
     * @param handleOutOfMemoryError
     * @return Number of chunks swapped.
     */
    long tryToEnsureThereIsEnoughMemoryToContinue_Account(HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * as identified by a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap.
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * @param a_ChunkID_HashSet
     * @return Number of chunks swapped.
     */
    long tryToEnsureThereIsEnoughMemoryToContinue_Account(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, HashSet<ChunkID> a_ChunkID_HashSet, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue. For this to work
     * accounting must be less expesive in terms of data size than swapping
     * data!
     * @param handleOutOfMemoryError
     * @return HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> identifying
     * chunks swapped.
     */
    HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue.  No data is
     * swapped from a_Grid2DSquareCell. For this to work accounting must be less
     * expesive in terms of data size than swapping data!
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * @return HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> identifying
     * chunks swapped.
     */
    HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue. The Chunk with
     * a_ChunkID from a_Grid2DSquareCell is not swapped. For this to work
     * accounting must be less expesive in terms of data size than swapping
     * data!
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * @param a_ChunkID
     * @return HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> identifying
     * chunks swapped.
     */
    HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, ChunkID a_ChunkID, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue.  No data is
     * swapped with a_ChunkID. For this to work accounting must be less
     * expesive in terms of data size than swapping data!
     * @param a_ChunkID
     * @param handleOutOfMemoryError
     * @return HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> identifying
     * chunks swapped.
     */
    HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(ChunkID a_ChunkID, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue.  No data is swapped
     * as identified by a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap. For
     * this to work accounting must be less expesive in terms of data size than
     * swapping data!
     * @param a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap
     * @param handleOutOfMemoryError
     * @return HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> identifying
     * chunks swapped.
     */
    HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap, boolean handleOutOfMemoryError);

    /**
     * A method to ensure there is enough memory to continue.  No data is swapped
     * as identified by a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap. For
     * this to work accounting must be less expesive in terms of data size than
     * swapping data!
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * @param a_ChunkID_HashSet
     * @return HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> identifying
     * chunks swapped.
     */
    HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, HashSet<ChunkID> a_ChunkID_HashSet, boolean handleOutOfMemoryError);

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered
     * this calls swapToFile_Grid2DSquareCellChunksExcept_Account(_Grid2DSquareCell)
     * (if that returns null then it calls swapToFile_Grid2DSquareCellChunk())
     * then recurses.
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * If true then OutOfMemoryErrors are caught, swap operations are initiated,
     * then the method is re-called.
     * If false then OutOfMemoryErrors are caught and thrown.
     */
    void init_MemoryReserve(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, boolean handleOutOfMemoryError);

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered
     * this calls
     * swapToFile_Grid2DSquareCellChunksExcept_AccountSuccess(_ChunkID,handleOutOfMemoryError)
     * then recurses.
     * @param a_ChunkID
     * @param handleOutOfMemoryError
     * If true then OutOfMemoryErrors are caught, swap operations are initiated,
     * then the method is re-called.
     * If false then OutOfMemoryErrors are caught and thrown.
     */
    void init_MemoryReserve(ChunkID a_ChunkID, boolean handleOutOfMemoryError);

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered
     * this calls
     * swapToFile_Grid2DSquareCellChunksExcept_AccountSuccess(_Grid2DSquareCell,_ChunkID,handleOutOfMemoryError)
     * then recurses.
     * @param a_Grid2DSquareCell
     * @param a_ChunkID
     * @param handleOutOfMemoryError
     * If true then OutOfMemoryErrors are caught, swap operations are initiated,
     * then the method is re-called.
     * If false then OutOfMemoryErrors are caught and thrown.
     */
    void init_MemoryReserve(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, ChunkID a_ChunkID, boolean handleOutOfMemoryError);

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered
     * this calls
     * swapToFile_Grid2DSquareCellChunksExcept_AccountSuccess(_Grid2DSquareCell,a_ChunkID_HashSet,handleOutOfMemoryError)
     * then recurses.
     * @param a_Grid2DSquareCell
     * @param a_ChunkID_HashSet HashSet of Grids_AbstractGrid2DSquareCell.ChunkIDs
     * @param handleOutOfMemoryError
     * If true then OutOfMemoryErrors are caught, swap operations are initiated,
     * then the method is re-called.
     * If false then OutOfMemoryErrors are caught and thrown.
     */
    void init_MemoryReserve(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, HashSet<ChunkID> a_ChunkID_HashSet, boolean handleOutOfMemoryError);

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered
     * this calls
     * swapToFile_Grid2DSquareCellChunksExcept_AccountSuccess(_Grid2DSquareCell_ChunkIDHashSet,handleOutOfMemoryError)
     * then recurses.
     * @param a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap
     * @param handleOutOfMemoryError
     * If true then OutOfMemoryErrors are caught, swap operations are initiated,
     * then the method is re-called.
     * If false then OutOfMemoryErrors are caught and thrown.
     */
    void init_MemoryReserve(HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap, boolean handleOutOfMemoryError);

    long init_MemoryReserve_Account(boolean handleOutOfMemoryError);

    long init_MemoryReserve_Account(ChunkID a_ChunkID, boolean handleOutOfMemoryError);

    long init_MemoryReserve_Account(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, ChunkID a_ChunkID, boolean handleOutOfMemoryError);

    long init_MemoryReserve_Account(HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap, boolean handleOutOfMemoryError);

    long init_MemoryReserve_Account(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, boolean handleOutOfMemoryError);

    long init_MemoryReserve_Account(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, HashSet<ChunkID> a_ChunkID_HashSet, boolean handleOutOfMemoryError);

    /**
     * Initialises _MemoryReserve.
     * @param handleOutOfMemoryError
     * If true then OutOfMemoryErrors are caught, swap operations are initiated,
     * then the method is re-called.
     * If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> init_MemoryReserve_AccountDetail(boolean handleOutOfMemoryError);

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered
     * this calls
     * swapToFile_Grid2DSquareCellChunksExcept_AccountSuccess(_ChunkID,handleOutOfMemoryError)
     * then recurses.
     * @param a_ChunkID
     * @param handleOutOfMemoryError
     * If true then OutOfMemoryErrors are caught, swap operations are initiated,
     * then the method is re-called.
     * If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> init_MemoryReserve_AccountDetail(ChunkID a_ChunkID, boolean handleOutOfMemoryError);

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered
     * this calls
     * swapToFile_Grid2DSquareCellChunksExcept_AccountSuccess(_Grid2DSquareCell,_ChunkID,handleOutOfMemoryError)
     * then recurses.
     * @param a_Grid2DSquareCell
     * @param a_ChunkID
     * @param handleOutOfMemoryError
     * If true then OutOfMemoryErrors are caught, swap operations are initiated,
     * then the method is re-called.
     * If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> init_MemoryReserve_AccountDetail(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, ChunkID a_ChunkID, boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> init_MemoryReserve_AccountDetail(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, HashSet<ChunkID> a_ChunkID_HashSet, boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> init_MemoryReserve_AccountDetail(Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell, boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> init_MemoryReserve_AccountDetail(HashMap<Grids_AbstractGrid2DSquareCell, HashSet<ChunkID>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap, boolean handleOutOfMemoryError);

}
