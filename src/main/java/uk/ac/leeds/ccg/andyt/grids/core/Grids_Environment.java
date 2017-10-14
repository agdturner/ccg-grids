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
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;

/**
 *
 * @author Andy
 */
public class Grids_Environment
        extends Grids_OutOfMemoryErrorHandler
        implements Serializable, Grids_OutOfMemoryErrorHandlerInterface {

    /**
     * @return the _Directory
     */
    public File getDirectory() {
        return _Directory;
    }

    /**
     * @return the _AbstractGrid2DSquareCell_HashSet
     */
    public HashSet<Grids_AbstractGrid> getAbstractGrid2DSquareCell_HashSet() {
        return _AbstractGrid2DSquareCell_HashSet;
    }

    /**
     * @return the _Generic_BigDecimal
     */
    public Generic_BigDecimal getGeneric_BigDecimal() {
        return _Generic_BigDecimal;
    }

    /**
     * @return the _Grid2DSquareCellProcessor
     */
    public Grids_Processor getGrid2DSquareCellProcessor() {
        return _Grid2DSquareCellProcessor;
    }

    /**
     * Local _Directory used for caching. TODO If this were not transient upon
     * reloading, it would be possible to ascertain what it was which might be
     * useful.
     */
    private transient File _Directory;

    /**
     * A HashSet of Grids_AbstractGrid objects that have data that can be
     * swapped to release memory for processing.
     */
    private transient HashSet<Grids_AbstractGrid> _AbstractGrid2DSquareCell_HashSet;

    /**
     * A HashSet of Grids_AbstractGrid objects that have data that can be
     * swapped to release memory for processing.
     */
    private transient HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> _NotToSwapData;

    /**
     * Local _Directory used for caching. TODO If this were not transient upon
     * reloading, it would be possible to ascertain what it was which might be
     * useful.
     */
    private transient Generic_BigDecimal _Generic_BigDecimal;

    /**
     * For storing a Grids_Processor.
     */
    private transient Grids_Processor _Grid2DSquareCellProcessor;

    public Grids_Environment() {
        init_AbstractGrid2DSquareCell_HashSet();
        init_NotToSwapData();
    }

    /**
     * @return the _Grid2DSquareCellProcessor
     */
    public Grids_Processor get_Grid2DSquareCellProcessor() {
        if (getGrid2DSquareCellProcessor() == null) {
            init_Grid2DSquareCellProcessor();
        }
        return getGrid2DSquareCellProcessor();
    }

    /**
     * @param a_Grid2DSquareCellProcessor
     */
    public void setGrid2DSquareCellProcessor(Grids_Processor a_Grid2DSquareCellProcessor) {
        this._Grid2DSquareCellProcessor = a_Grid2DSquareCellProcessor;
    }

    /**
     * @return the _Grid2DSquareCellProcessor
     */
    private void init_Grid2DSquareCellProcessor() {
        _Grid2DSquareCellProcessor = new Grids_Processor(this);
    }

    /**
     * @return the _Generic_BigDecimal
     */
    public Generic_BigDecimal get_Generic_BigDecimal() {
        if (getGeneric_BigDecimal() == null) {
            init_Generic_BigDecimal();
        }
        return getGeneric_BigDecimal();
    }

    /**
     * @param a_Generic_BigDecimal
     */
    public void setGeneric_BigDecimal(Generic_BigDecimal a_Generic_BigDecimal) {
        this._Generic_BigDecimal = a_Generic_BigDecimal;
    }

    /**
     * @return the _Generic_BigDecimal
     */
    private void init_Generic_BigDecimal() {
        _Generic_BigDecimal = new Generic_BigDecimal();
    }

    /**
     * Initialises _AbstractGrid2DSquareCell_HashSet.
     */
    protected final void init_AbstractGrid2DSquareCell_HashSet() {
        if (this.getAbstractGrid2DSquareCell_HashSet() == null) {
            this._AbstractGrid2DSquareCell_HashSet = new HashSet<>();
        }
    }

    /**
     */
    protected final void init_NotToSwapData() {
        if (this.getNotToSwapData() == null) {
            this._NotToSwapData = new HashMap<>();
        }
    }

    /**
     * Initialises _AbstractGrid2DSquareCell_HashSet * <code>
     * if ( this._AbstractGrid2DSquareCell_HashSet == null ) {
     * this._AbstractGrid2DSquareCell_HashSet = a_AbstractGrid2DSquareCell_HashSet;
     * } else {
     * //System.err.println( this.getClass().getName() + ".init_AbstractGrid2DSquareCell_HashSet(HashSet)" );
     * if ( a_AbstractGrid2DSquareCell_HashSet == null ) { // Debug
     * this._AbstractGrid2DSquareCell_HashSet = new HashSet();
     * } else {
     * this._AbstractGrid2DSquareCell_HashSet = a_AbstractGrid2DSquareCell_HashSet;
     * }
     * }
     * </code>
     *
     * @param a_AbstractGrid2DSquareCell_HashSet
     */
    protected void init_AbstractGrid2DSquareCell_HashSet(
            HashSet<Grids_AbstractGrid> a_AbstractGrid2DSquareCell_HashSet) {
        if (this.getAbstractGrid2DSquareCell_HashSet() == null) {
            this._AbstractGrid2DSquareCell_HashSet = a_AbstractGrid2DSquareCell_HashSet;
        } else {
            //System.err.println( this.getClass().getName() + ".init_AbstractGrid2DSquareCell_HashSet(HashSet)" );
            if (a_AbstractGrid2DSquareCell_HashSet == null) { // Debug
                this._AbstractGrid2DSquareCell_HashSet = new HashSet<>();
            } else {
                this._AbstractGrid2DSquareCell_HashSet = a_AbstractGrid2DSquareCell_HashSet;
            }
        }
    }

    /**
     * @return this._AbstractGrid2DSquareCell_HashSet.
     */
    public HashSet<Grids_AbstractGrid> get_AbstractGrid2DSquareCell_HashSet() {
        return this.getAbstractGrid2DSquareCell_HashSet();
    }

    /**
     * Sets this._AbstractGrid2DSquareCell_HashSet to
     * _AbstractGrid2DSquareCell_HashSet
     *
     * @param a_AbstractGrid2DSquareCell_HashSet
     * @param handleOutOfMemoryError
     */
    public void set_AbstractGrid2DSquareCell_HashSet(
            HashSet<Grids_AbstractGrid> a_AbstractGrid2DSquareCell_HashSet,
            boolean handleOutOfMemoryError) {
        try {
            this._AbstractGrid2DSquareCell_HashSet = a_AbstractGrid2DSquareCell_HashSet;
            tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                if (swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw e;
                }
                init_MemoryReserve(handleOutOfMemoryError);
                set_AbstractGrid2DSquareCell_HashSet(
                        a_AbstractGrid2DSquareCell_HashSet,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * this._AbstractGrid2DSquareCell_HashSet =
     * a_AbstractGrid2DSquareCell_HashSet;
     *
     * @param a_AbstractGrid2DSquareCell_HashSet
     */
    protected void set_AbstractGrid2DSquareCell_HashSet(
            HashSet<Grids_AbstractGrid> a_AbstractGrid2DSquareCell_HashSet) {
        this._AbstractGrid2DSquareCell_HashSet = a_AbstractGrid2DSquareCell_HashSet;
    }

    /**
     * @param a_AbstractGrid2DSquareCell_HashSet
     * @param handleOutOfMemoryError
     */
    public void init_Grid2DSquareCells_MemoryReserve(
            HashSet<Grids_AbstractGrid> a_AbstractGrid2DSquareCell_HashSet,
            boolean handleOutOfMemoryError) {
        try {
            init_Grid2DSquareCells_MemoryReserve(a_AbstractGrid2DSquareCell_HashSet);
            tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                if (swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw e;
                }
                init_Grid2DSquareCells_MemoryReserve(
                        a_AbstractGrid2DSquareCell_HashSet,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param a_AbstractGrid2DSquareCell_HashSet
     */
    protected void init_Grid2DSquareCells_MemoryReserve(
            HashSet<Grids_AbstractGrid> a_AbstractGrid2DSquareCell_HashSet) {
        init_AbstractGrid2DSquareCell_HashSet(a_AbstractGrid2DSquareCell_HashSet);
        Iterator<Grids_AbstractGrid> a_Iterator = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        if (a_Iterator.hasNext()) {
            a_Iterator.next().ge.set_MemoryReserve(get_MemoryReserve());
        } else {
            init_MemoryReserve();
        }
        //System.err.println( this.getClass().getName() + ".init_Grid2DSquareCells_MemoryReserve(HashSet)" );
    }

    /**
     * Initialises _MemoryReserve.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> init_MemoryReserve_AccountDetail(
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result
                        = swapToFile_Grid2DSquareCellChunk_AccountDetail();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentailPartResult
                        = init_MemoryReserve_AccountDetail(
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
    public long init_MemoryReserve_Account(
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = swapToFile_Grid2DSquareCellChunk_Account();
                if (result < 1L) {
                    throw e;
                }
                result += init_MemoryReserve_Account(
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered this
     * calls swapToFile_Grid2DSquareCellChunksExcept_Account(_Grid2DSquareCell)
     * (if that returns null then it calls swapToFile_Grid2DSquareCellChunk())
     * then recurses.
     *
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public final void init_MemoryReserve(
            Grids_AbstractGrid a_Grid2DSquareCell,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            tryToEnsureThereIsEnoughMemoryToContinue(
                    a_Grid2DSquareCell,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                if (swapToFile_Grid2DSquareCellChunkExcept_Account(a_Grid2DSquareCell) < 1L) {
                    throw e;
                }
                init_MemoryReserve(
                        a_Grid2DSquareCell,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered this
     * calls
     * swapToFile_Grid2DSquareCellChunksExcept_AccountSuccess(_ChunkID,handleOutOfMemoryError)
     * then recurses.
     *
     * @param a_ChunkID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public final void init_MemoryReserve(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            tryToEnsureThereIsEnoughMemoryToContinue(
                    a_ChunkID,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                if (swapToFile_Grid2DSquareCellChunkExcept_Account(
                        a_ChunkID) < 1L) {
                    throw e;
                }
                init_MemoryReserve(
                        a_ChunkID,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered this
     * calls
     * swapToFile_Grid2DSquareCellChunksExcept_AccountSuccess(_ChunkID,handleOutOfMemoryError)
     * then recurses.
     *
     * @param a_ChunkID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> init_MemoryReserve_AccountDetail(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    a_ChunkID,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result
                        = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                                a_ChunkID);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                        = init_MemoryReserve_AccountDetail(
                                a_ChunkID,
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
    public long init_MemoryReserve_Account(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    a_ChunkID,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result
                        = swapToFile_Grid2DSquareCellChunkExcept_Account(
                                a_ChunkID);
                if (result < 1L) {
                    throw e;
                }
                result += init_MemoryReserve_Account(
                        a_ChunkID,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered this
     * calls
     * swapToFile_Grid2DSquareCellChunksExcept_AccountSuccess(_Grid2DSquareCell,_ChunkID,handleOutOfMemoryError)
     * then recurses.
     *
     * @param a_Grid2DSquareCell
     * @param a_ChunkID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public final void init_MemoryReserve(
            Grids_AbstractGrid a_Grid2DSquareCell,
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                if (swapToFile_Grid2DSquareCellChunkExcept_Account(
                        a_Grid2DSquareCell,
                        a_ChunkID) < 1L) {
                    throw e;
                }
                init_MemoryReserve(
                        a_Grid2DSquareCell,
                        a_ChunkID,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    @Override
    public long init_MemoryReserve_Account(
            Grids_AbstractGrid a_Grid2DSquareCell,
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    a_Grid2DSquareCell,
                    a_ChunkID,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result
                        = swapToFile_Grid2DSquareCellChunkExcept_Account(
                                a_Grid2DSquareCell,
                                a_ChunkID);
                if (result < 1L) {
                    throw e;
                }
                result += init_MemoryReserve_Account(
                        a_Grid2DSquareCell,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered this
     * calls
     * swapToFile_Grid2DSquareCellChunksExcept_AccountSuccess(_Grid2DSquareCell,_ChunkID,handleOutOfMemoryError)
     * then recurses.
     *
     * @param a_Grid2DSquareCell
     * @param a_ChunkID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> init_MemoryReserve_AccountDetail(
            Grids_AbstractGrid a_Grid2DSquareCell,
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    a_Grid2DSquareCell,
                    a_ChunkID,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result
                        = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                                a_Grid2DSquareCell,
                                a_ChunkID);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                        = init_MemoryReserve_AccountDetail(
                                a_Grid2DSquareCell,
                                a_ChunkID,
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
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> init_MemoryReserve_AccountDetail(
            Grids_AbstractGrid a_Grid2DSquareCell,
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    a_Grid2DSquareCell,
                    a_ChunkID_HashSet,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result
                        = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                                a_Grid2DSquareCell,
                                a_ChunkID_HashSet);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                        = init_MemoryReserve_AccountDetail(
                                a_Grid2DSquareCell,
                                a_ChunkID_HashSet,
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
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> init_MemoryReserve_AccountDetail(
            Grids_AbstractGrid a_Grid2DSquareCell,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    a_Grid2DSquareCell,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result
                        = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                                a_Grid2DSquareCell);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                        = init_MemoryReserve_AccountDetail(
                                a_Grid2DSquareCell,
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
    public long init_MemoryReserve_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result
                        = swapToFile_Grid2DSquareCellChunkExcept_Account(
                                a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap);
                if (result < 1L) {
                    throw e;
                }
                result += init_MemoryReserve_Account(
                        a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    @Override
    public long init_MemoryReserve_Account(
            Grids_AbstractGrid a_Grid2DSquareCell,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    a_Grid2DSquareCell,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result
                        = swapToFile_Grid2DSquareCellChunkExcept_Account(
                                a_Grid2DSquareCell);
                if (result < 1L) {
                    throw e;
                }
                result += init_MemoryReserve_Account(
                        a_Grid2DSquareCell,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    @Override
    public long init_MemoryReserve_Account(
            Grids_AbstractGrid a_Grid2DSquareCell,
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    a_Grid2DSquareCell,
                    a_ChunkID_HashSet,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result
                        = swapToFile_Grid2DSquareCellChunkExcept_Account(
                                a_Grid2DSquareCell,
                                a_ChunkID_HashSet);
                if (result < 1L) {
                    throw e;
                }
                result += init_MemoryReserve_Account(
                        a_Grid2DSquareCell,
                        a_ChunkID_HashSet,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered this
     * calls
     * swapToFile_Grid2DSquareCellChunksExcept_AccountSuccess(_Grid2DSquareCell,a_ChunkID_HashSet,handleOutOfMemoryError)
     * then recurses.
     *
     * @param a_Grid2DSquareCell
     * @param a_ChunkID_HashSet HashSet of Grids_AbstractGrid.ChunkIDs
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public final void init_MemoryReserve(
            Grids_AbstractGrid a_Grid2DSquareCell,
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            tryToEnsureThereIsEnoughMemoryToContinue(
                    a_Grid2DSquareCell,
                    a_ChunkID_HashSet,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                if (swapToFile_Grid2DSquareCellChunkExcept_Account(
                        a_Grid2DSquareCell,
                        a_ChunkID_HashSet) < 1L) {
                    throw e;
                }
                init_MemoryReserve(
                        a_Grid2DSquareCell,
                        a_ChunkID_HashSet,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises _MemoryReserve. If an OutOfMemoryError is encountered this
     * calls
     * swapToFile_Grid2DSquareCellChunksExcept_AccountSuccess(_Grid2DSquareCell_ChunkIDHashSet,handleOutOfMemoryError)
     * then recurses.
     *
     * @param a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public void init_MemoryReserve(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            tryToEnsureThereIsEnoughMemoryToContinue(
                    a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                if (swapToFile_Grid2DSquareCellChunkExcept_Account(
                        a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap) < 1) {
                    throw e;
                }
                init_MemoryReserve(
                        a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> init_MemoryReserve_AccountDetail(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> chunksNotToSwap,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    chunksNotToSwap,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                        chunksNotToSwap);
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = init_MemoryReserve_AccountDetail(
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
                clear_MemoryReserve();
                if (!tryToEnsureThereIsEnoughMemoryToContinue()) {
                    throw e;
                }
                init_MemoryReserve(
                        handleOutOfMemoryError);
                return true;
            } else {
                throw e;
            }
        }
    }

    @Override
    protected boolean tryToEnsureThereIsEnoughMemoryToContinue() {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to try to ensure there is enough memory to continue. No data is
     * swapped from a_Grid2DSquareCell. If not enough data is found to swap then
     * an OutOfMemoryError is thrown.
     *
     * @param gridNotToSwap
     * @param handleOutOfMemoryError
     */
    @Override
    public void tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid gridNotToSwap,
            boolean handleOutOfMemoryError) {
        try {
            if (!tryToEnsureThereIsEnoughMemoryToContinue(gridNotToSwap)) {
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
                clear_MemoryReserve();
                boolean isEnoughMemoryToContinue;
                isEnoughMemoryToContinue = tryToEnsureThereIsEnoughMemoryToContinue(
                        gridNotToSwap);
                if (!isEnoughMemoryToContinue) {
                    throw e;
                }
                init_MemoryReserve(
                        gridNotToSwap,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid a_Grid2DSquareCell) {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (swapToFile_Grid2DSquareCellChunkExcept_Account(a_Grid2DSquareCell) < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to try to ensure there is enough memory to continue. The Chunk
     * with a_ChunkID from a_Grid2DSquareCell is not swapped. If not enough data
     * is found to swap then an OutOfMemoryError is thrown.
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
                clear_MemoryReserve();
                boolean isEnoughMemoryToContinue = tryToEnsureThereIsEnoughMemoryToContinue(
                        g,
                        chunkID);
                if (!isEnoughMemoryToContinue) {
                    throw e;
                }
                init_MemoryReserve(
                        g,
                        chunkID,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid a_Grid2DSquareCell,
            Grids_2D_ID_int a_ChunkID) {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (swapToFile_Grid2DSquareCellChunkExcept_Account(a_Grid2DSquareCell, a_ChunkID) < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to try to ensure there is enough memory to continue. No data is
     * swapped with a_ChunkID. If not enough data is found to swap then an
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
                clear_MemoryReserve();
                boolean isEnoughMemoryToContinue = tryToEnsureThereIsEnoughMemoryToContinue(
                        chunkID);
                if (!isEnoughMemoryToContinue) {
                    throw e;
                }
                init_MemoryReserve(
                        chunkID,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_2D_ID_int a_ChunkID) {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (swapToFile_Grid2DSquareCellChunkExcept_Account(a_ChunkID) < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to try to ensure there is enough memory to continue. No data is
     * swapped as identified by
     * a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap. If not enough data is
     * found to swap then an OutOfMemoryError is thrown.
     *
     * @param a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap
     * @param handleOutOfMemoryError
     */
    @Override
    public void tryToEnsureThereIsEnoughMemoryToContinue(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
            boolean handleOutOfMemoryError) {
        try {
            if (!tryToEnsureThereIsEnoughMemoryToContinue(a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap)) {
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
                clear_MemoryReserve();
                boolean isEnoughMemoryToContinue = tryToEnsureThereIsEnoughMemoryToContinue(
                        a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap);
                if (!isEnoughMemoryToContinue) {
                    throw e;
                }
                init_MemoryReserve(
                        a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap) {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (swapToFile_Grid2DSquareCellChunkExcept_Account(a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap) < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * in a_Grid2DSquareCell that has Grids_2D_ID_int in a_ChunkID_HashSet.
     *
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * @param a_ChunkID_HashSet
     */
    @Override
    public void tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid a_Grid2DSquareCell,
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet,
            boolean handleOutOfMemoryError) {
        try {
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapToFile_Grid2DSquareCellChunkExcept_Account(a_Grid2DSquareCell, a_ChunkID_HashSet) < 1) {
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
                clear_MemoryReserve();
                boolean createdRoom = false;
                while (!createdRoom) {
                    if (swapToFile_Grid2DSquareCellChunkExcept_Account(a_Grid2DSquareCell, a_ChunkID_HashSet) < 1L) {
                        System.out.println(
                                "Warning! Nothing to swap in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue(AbstractGrid2DSquareCell,HashSet<ChunkID>,boolean) after encountering an OutOfMemoryError");
                        throw e;
                    }
                    init_MemoryReserve(
                            a_Grid2DSquareCell,
                            a_ChunkID_HashSet,
                            handleOutOfMemoryError);
                    tryToEnsureThereIsEnoughMemoryToContinue(
                            a_Grid2DSquareCell,
                            a_ChunkID_HashSet,
                            handleOutOfMemoryError);
                    createdRoom = true;
                }
            } else {
                throw e;
            }
        }
    }

    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid a_Grid2DSquareCell,
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet) {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (swapToFile_Grid2DSquareCellChunkExcept_Account(a_Grid2DSquareCell, a_ChunkID_HashSet) < 1) {
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
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_Account();
            if (test == null) {
                return 0;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return (Long) test[1];
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                        handleOutOfMemoryError);
                result += init_MemoryReserve_Account(
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * A method to try to ensure there is enough memory to continue. This method
     * quickly returns null if there is enough memory to continue. If there is
     * not enough memory to continue it will attempt to make room. If
     * successful, this will return an Object[] with the first element being a
     * Boolean with value true. The second element being a
     * HashMap<Grids_AbstractGrid, HashSet<ChunkID>> indicating the data that
     * was swapped. If unsuccessful, this will return an Object[] with the first
     * element being a Boolean with value false. The second element being a
     * HashMap<Grids_AbstractGrid, HashSet<ChunkID>> indicating the data that
     * was swapped.
     *
     * @return Either null or an Object[] of length 2 with first element a
     * Boolean and second element a HashMap<Grids_AbstractGrid,
     * HashSet<ChunkID>>.
     */
    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_Account() {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[2];
            long result1 = 0L;
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
                    result[0] = false;
                    result[1] = result1;
                    return result;
                } else {
                    result1++;
                }
            }
            result[0] = true;
            result[1] = result1;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * from a_Grid2DSquareCell.
     *
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * @return Number of chunks swapped.
     */
    @Override
    public long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid a_Grid2DSquareCell,
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    a_Grid2DSquareCell);
            if (test == null) {
                return 0;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(AbstractGrid2DSquareCell,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return (Long) test[1];
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                        a_Grid2DSquareCell,
                        handleOutOfMemoryError);
                result += init_MemoryReserve_Account(
                        a_Grid2DSquareCell,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid a_Grid2DSquareCell) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[2];
            long result1 = 0L;
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapToFile_Grid2DSquareCellChunkExcept_Account(a_Grid2DSquareCell) < 1) {
                    result[0] = false;
                    result[1] = result1;
                    return result;
                } else {
                    result1++;
                }
            }
            result[0] = true;
            result[1] = result1;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. The Chunk with
     * a_ChunkID from a_Grid2DSquareCell is not swapped.
     *
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * @param a_ChunkID
     * @return Number of chunks swapped.
     */
    @Override
    public long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid a_Grid2DSquareCell,
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    a_Grid2DSquareCell,
                    a_ChunkID);
            if (test == null) {
                return 0;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(AbstractGrid2DSquareCell,ChunkID,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return (Long) test[1];
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                        a_Grid2DSquareCell,
                        a_ChunkID,
                        handleOutOfMemoryError);
                result += init_MemoryReserve_Account(
                        a_Grid2DSquareCell,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    public Object[] tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid a_Grid2DSquareCell,
            Grids_2D_ID_int a_ChunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[2];
            long result1 = 0L;
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapToFile_Grid2DSquareCellChunkExcept_Account(a_Grid2DSquareCell, a_ChunkID) < 1) {
                    result[0] = false;
                    result[1] = result1;
                    return result;
                } else {
                    result1++;
                }
            }
            result[0] = true;
            result[1] = result1;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * with a_ChunkID.
     *
     * @param a_ChunkID
     * @param handleOutOfMemoryError
     * @return Number of chunks swapped.
     */
    @Override
    public long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    a_ChunkID);
            if (test == null) {
                return 0;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(ChunkID,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return (Long) test[1];
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                        a_ChunkID,
                        handleOutOfMemoryError);
                result += init_MemoryReserve_Account(
                        a_ChunkID,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    public Object[] tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_2D_ID_int a_ChunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[2];
            long result1 = 0L;
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapToFile_Grid2DSquareCellChunkExcept_Account(a_ChunkID) < 1) {
                    result[0] = false;
                    result[1] = result1;
                    return result;
                } else {
                    result1++;
                }
            }
            result[0] = true;
            result[1] = result1;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * as identified by a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap.
     *
     * @param a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap
     * @param handleOutOfMemoryError
     * @return Number of chunks swapped.
     */
    @Override
    public long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap);
            if (test == null) {
                return 0;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(HashMap<AbstractGrid2DSquareCell, HashSet<ChunkID>>,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return (Long) test[1];
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                        a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
                        handleOutOfMemoryError);
                result += init_MemoryReserve_Account(
                        a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    public Object[] tryToEnsureThereIsEnoughMemoryToContinue_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[2];
            long result1 = 0L;
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapToFile_Grid2DSquareCellChunkExcept_Account(a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap) < 1) {
                    result[0] = false;
                    result[1] = result1;
                    return result;
                } else {
                    result1++;
                }
            }
            result[0] = true;
            result[1] = result1;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * as identified by a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap.
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
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    g,
                    chunks);
            if (test == null) {
                return 0;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(AbstractGrid2DSquareCell,HashSet<ChunkID>,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return (Long) test[1];
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                return freeSomeMemoryAndResetReserve_Account(
                        g,
                        chunks,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    public Object[] tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid a_Grid2DSquareCell,
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[2];
            long result1 = 0L;
            while (getTotalFreeMemory() < Memory_Threshold) {
                if (swapToFile_Grid2DSquareCellChunkExcept_Account(a_Grid2DSquareCell, a_ChunkID_HashSet) < 1) {
                    result[0] = false;
                    result[1] = result1;
                    return result;
                } else {
                    result1++;
                }
            }
            result[0] = true;
            result[1] = result1;
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
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail();
            if (test == null) {
                return null;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return (HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>) test[1];
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
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
     * not enough memory to continue it will attempt to make room. If
     * successful, this will return an Object[] with the first element being a
     * Boolean with value true. The second element being a
     * HashMap<Grids_AbstractGrid, HashSet<ChunkID>> indicating the data that
     * was swapped. If unsuccessful, this will return an Object[] with the first
     * element being a Boolean with value false. The second element being a
     * HashMap<Grids_AbstractGrid, HashSet<ChunkID>> indicating the data that
     * was swapped.
     *
     * @return Either null or an Object[] of length 2 with first element a
     * Boolean and second element a HashMap<Grids_AbstractGrid,
     * HashSet<ChunkID>>.
     */
    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail() {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[2];
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result1;
            result1 = new HashMap<>(1);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            while (getTotalFreeMemory() < Memory_Threshold) {
                potentialPartResult = swapToFile_Grid2DSquareCellChunk_AccountDetail();
                if (potentialPartResult.isEmpty()) {
                    result[0] = false;
                    result[1] = result1;
                    return result;
                } else {
                    combine(result1,
                            potentialPartResult);
                }
            }
            result[0] = true;
            result[1] = result1;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * from a_Grid2DSquareCell. For this to work accounting must be less
     * expesive in terms of data size than swapping data!
     *
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * @return HashMap<Grids_AbstractGrid, HashSet<ChunkID>> identifying chunks
     * swapped.
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_AbstractGrid a_Grid2DSquareCell,
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    a_Grid2DSquareCell);
            if (test == null) {
                return null;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return (HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>) test[1];
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                return freeSomeMemoryAndResetReserve_AccountDetails(
                        a_Grid2DSquareCell,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_AbstractGrid a_Grid2DSquareCell) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[2];
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result1 = new HashMap<>(1);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            while (getTotalFreeMemory() < Memory_Threshold) {
                potentialPartResult = swapToFile_Grid2DSquareCellChunk_AccountDetail();
                if (potentialPartResult.isEmpty()) {
                    result[0] = false;
                    result[1] = result1;
                    return result;
                } else {
                    combine(result1,
                            potentialPartResult);
                }
            }
            result[0] = true;
            result[1] = result1;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. The Chunk with
     * a_ChunkID from a_Grid2DSquareCell is not swapped. For this to work
     * accounting must be less expesive in terms of data size than swapping
     * data!
     *
     * @param a_Grid2DSquareCell
     * @param handleOutOfMemoryError
     * @param a_ChunkID
     * @return HashMap<Grids_AbstractGrid, HashSet<ChunkID>> identifying chunks
     * swapped.
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_AbstractGrid a_Grid2DSquareCell,
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    a_Grid2DSquareCell,
                    a_ChunkID);
            if (test == null) {
                return null;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(AbstractGrid2DSquareCell,ChunkID,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return (HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>) test[1];
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                        a_Grid2DSquareCell,
                        a_ChunkID,
                        handleOutOfMemoryError);
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult = init_MemoryReserve_AccountDetail(
                        a_Grid2DSquareCell,
                        a_ChunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_AbstractGrid a_Grid2DSquareCell,
            Grids_2D_ID_int a_ChunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[2];
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result1 = new HashMap<>(1);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            while (getTotalFreeMemory() < Memory_Threshold) {
                potentialPartResult = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                        a_Grid2DSquareCell,
                        a_ChunkID);
                if (potentialPartResult.isEmpty()) {
                    result[0] = false;
                    result[1] = result1;
                    return result;
                } else {
                    combine(result1,
                            potentialPartResult);
                }
            }
            result[0] = true;
            result[1] = result1;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * with a_ChunkID. For this to work accounting must be less expesive in
     * terms of data size than swapping data!
     *
     * @param a_ChunkID
     * @param handleOutOfMemoryError
     * @return HashMap<Grids_AbstractGrid, HashSet<ChunkID>> identifying chunks
     * swapped.
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    a_ChunkID);
            if (test == null) {
                return null;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(ChunkID,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return (HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>) test[1];
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                        a_ChunkID,
                        handleOutOfMemoryError);
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult = init_MemoryReserve_AccountDetail(
                        a_ChunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_2D_ID_int a_ChunkID) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[2];
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result1 = new HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>(1);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult = null;
            while (getTotalFreeMemory() < Memory_Threshold) {
                potentialPartResult = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                        a_ChunkID);
                if (potentialPartResult.isEmpty()) {
                    result[0] = false;
                    result[1] = result1;
                    return result;
                } else {
                    combine(result1,
                            potentialPartResult);
                }
            }
            result[0] = true;
            result[1] = result1;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * as identified by a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap. For
     * this to work accounting must be less expesive in terms of data size than
     * swapping data!
     *
     * @param a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap
     * @param handleOutOfMemoryError
     * @return HashMap<Grids_AbstractGrid, HashSet<ChunkID>> identifying chunks
     * swapped.
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap);
            if (test == null) {
                return null;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(HashMap<AbstractGrid2DSquareCell, HashSet<ChunkID>>,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return (HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>) test[1];
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                        a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
                        handleOutOfMemoryError);
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult = init_MemoryReserve_AccountDetail(
                        a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[2];
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result1 = new HashMap<>(1);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            while (getTotalFreeMemory() < Memory_Threshold) {
                potentialPartResult = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                        a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap);
                if (potentialPartResult.isEmpty()) {
                    result[0] = false;
                    result[1] = result1;
                    return result;
                } else {
                    combine(result1,
                            potentialPartResult);
                }
            }
            result[0] = true;
            result[1] = result1;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue. No data is swapped
     * as identified by a_AbstractGrid2DSquareCell_ChunkID_HashSet_HashMap. For
     * this to work accounting must be less expesive in terms of data size than
     * swapping data!
     *
     * @param g
     * @param handleOutOfMemoryError
     * @param chunks
     * @return HashMap<Grids_AbstractGrid, HashSet<ChunkID>> identifying chunks
     * swapped.
     */
    @Override
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunks,
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    g,
                    chunks);
            if (test == null) {
                return null;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(AbstractGrid2DSquareCell,HashSet<ChunkID>,boolean)";
                System.out.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError(message);
            }
            return (HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>) test[1];
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                return freeSomeMemoryAndResetReserve_AccountDetails(
                        g,
                        chunks,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_AbstractGrid a_Grid2DSquareCell,
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet) {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[2];
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result1 = new HashMap<>(1);
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            while (getTotalFreeMemory() < Memory_Threshold) {
                potentialPartResult = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                        a_Grid2DSquareCell,
                        a_ChunkID_HashSet);
                if (potentialPartResult.isEmpty()) {
                    result[0] = false;
                    result[1] = result1;
                    return result;
                } else {
                    combine(result1,
                            potentialPartResult);
                }
            }
            result[0] = true;
            result[1] = result1;
        }
        return null;
    }

    /**
     * Attempts to swap all Grids_AbstractGridChunk in
     * this._AbstractGrid2DSquareCell_HashSet.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunks_AccountDetail(
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunks_AccountDetail();
            try {
                if (result.isEmpty()) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail();
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            combine(result,
                                    (HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>) account[1]);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                            = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
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
                clear_MemoryReserve();
                return freeSomeMemoryAndResetReserve_AccountDetails(e, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap all Grids_AbstractGridChunk in
     * this._AbstractGrid2DSquareCell_HashSet.
     *
     * @return
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunks_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
        Iterator<Grids_AbstractGrid> a_Iterator = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        while (a_Iterator.hasNext()) {
            partResult = a_Iterator.next().swapToFile_Grid2DSquareCellChunks_AccountDetail(HandleOutOfMemoryErrorFalse);
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
    public long swapToFile_Grid2DSquareCellChunks_Account(
            boolean handleOutOfMemoryError) {
        try {
            long result;
            result = swapToFile_Grid2DSquareCellChunks_Account(); // Should this really be here and not in the try loop?
            try {
                if (result < 1) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_Account();
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            result += (Long) account[1];
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
                clear_MemoryReserve();
                long result = freeSomeMemoryAndResetReserve_Account(e, handleOutOfMemoryError);
                result += swapToFile_Grid2DSquareCellChunks_Account(handleOutOfMemoryError);
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
    protected long swapToFile_Grid2DSquareCellChunks_Account() {
        long result = 0L;
        Iterator<Grids_AbstractGrid> a_Iterator;
        a_Iterator = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        while (a_Iterator.hasNext()) {
            long partResult;
            Grids_AbstractGrid g;
            g = a_Iterator.next();
            partResult = g.ge.swapToFile_Grid2DSquareCellChunks_Account(
                    HandleOutOfMemoryErrorFalse);
            result += partResult;
        }
        dataToSwap = false;
        return result;
    }

    /**
     * Attempts to swap all Grids_AbstractGridChunk in
     * this._AbstractGrid2DSquareCell_HashSet.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void swapToFile_Grid2DSquareCellChunks(
            boolean handleOutOfMemoryError) {
        try {
            boolean success = swapToFile_Grid2DSquareCellChunks();
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
                clear_MemoryReserve();
                if (swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw e;
                }
                init_MemoryReserve(handleOutOfMemoryError);
                swapToFile_Grid2DSquareCellChunks();
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap all Grids_AbstractGridChunk in
     * this._AbstractGrid2DSquareCell_HashSet.
     *
     * @return
     */
    protected boolean swapToFile_Grid2DSquareCellChunks() {
        Iterator<Grids_AbstractGrid> a_Iterator = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        while (a_Iterator.hasNext()) {
            a_Iterator.next().swapToFile_Grid2DSquareCellChunks(HandleOutOfMemoryErrorFalse);
        }
        dataToSwap = false;
        return true;
    }

    /**
     * Attempts to swap any Grids_AbstractGridChunk in
     * this._AbstractGrid2DSquareCell_HashSet. This is the lowest level of
     * OutOfMemoryError handling in this class.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunk_AccountDetail(
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunk_AccountDetail();
            try {
                if (result.isEmpty()) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail();
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            combine(result,
                                    (HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>) account[1]);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                            = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
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
                clear_MemoryReserve();
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
    public void swapToFile_Grid2DSquareCellChunk(
            boolean handleOutOfMemoryError) {
        try {
            boolean success = swapToFile_Grid2DSquareCellChunk();
            try {
                if (!success) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_Account();
                    if (account != null) {
                        if (!(Boolean) account[0]) {
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
                clear_MemoryReserve();
                if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
                    throw e;
                }
                init_MemoryReserve(handleOutOfMemoryError);
                // No need for recursive call: swapToFile_Grid2DSquareCellChunk(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap any Grids_AbstractGridChunk in
     * this._AbstractGrid2DSquareCell_HashSet.
     *
     * @return
     */
    protected boolean swapToFile_Grid2DSquareCellChunk() {
        Iterator<Grids_AbstractGrid> a_Iterator = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        while (a_Iterator.hasNext()) {
            if (a_Iterator.next().swapToFile_Grid2DSquareCellChunk_Account(HandleOutOfMemoryErrorFalse) < 1) {
                return false;
            } else {
                return true;
            }
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
    protected boolean swapToFile_Grid2DSquareCellChunk(
            Grids_AbstractGrid grid) {
        if (grid.swapToFile_Grid2DSquareCellChunk_Account(HandleOutOfMemoryErrorFalse) < 1) {
            return false;
        } else {
            return true;
        }
    }

//    /**
//     * Attempts to swap any Grids_AbstractGridChunk in grid
//     *
//     * @param grid
//     * @return
//     */
//    protected void swapToFile_Grid2DSquareCellChunk(
//            Grids_AbstractGrid grid,
//            Grids_2D_ID_int chunkID) {
//        grid.swapToFile_Grid2DSquareCellChunk(chunkID);
//    }
    public long swapToFile_Grid2DSquareCellChunk_Account(
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_Grid2DSquareCellChunk_Account();
            try {
                if (result < 1) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_Account();
                    if (account != null) {
                        if (!(Boolean) account[0]) {
                            throw new OutOfMemoryError();
                        } else {
                            result += (Long) account[1];
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
                clear_MemoryReserve();
                return freeSomeMemoryAndResetReserve_Account(e, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected long swapToFile_Grid2DSquareCellChunk_Account() {
        Iterator<Grids_AbstractGrid> a_Iterator = getAbstractGrid2DSquareCell_HashSet().iterator();
        while (a_Iterator.hasNext()) {
            if (a_Iterator.next().swapToFile_Grid2DSquareCellChunk_Account(HandleOutOfMemoryErrorFalse) > 0) {
                return 1L;
            }
        }
        dataToSwap = false;
        return 0L;
    }

    /**
     * Swap to File any Grids_AbstractGrid.Chunk in
     * this._AbstractGrid2DSquareCell_HashSet except one in
     * a_AbstractGrid2DSquareCell.
     *
     * @param a_AbstractGrid2DSquareCell
     * @param handleOutOfMemoryError
     */
    public void swapToFile_Grid2DSquareCellChunkExcept(
            Grids_AbstractGrid a_AbstractGrid2DSquareCell,
            boolean handleOutOfMemoryError) {
        try {
            boolean success = swapToFile_Grid2DSquareCellChunkExcept(
                    a_AbstractGrid2DSquareCell);
            try {
                if (!success) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            a_AbstractGrid2DSquareCell);
                    if (account != null) {
                        if (!(Boolean) account[0]) {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    tryToEnsureThereIsEnoughMemoryToContinue(
                            a_AbstractGrid2DSquareCell,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                if (swapToFile_Grid2DSquareCellChunkExcept_Account(
                        a_AbstractGrid2DSquareCell) < 1L) {
                    throw a_OutOfMemoryError;
                }
                init_MemoryReserve(
                        a_AbstractGrid2DSquareCell,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Swap to File any Grids_AbstractGrid.Chunk in
     * this._AbstractGrid2DSquareCell_HashSet except one in
     * a_AbstractGrid2DSquareCell.
     *
     * @param g
     * @return
     */
    protected boolean swapToFile_Grid2DSquareCellChunkExcept(
            Grids_AbstractGrid g) {
        Iterator<Grids_AbstractGrid> a_Iterator = getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid bg;
        while (a_Iterator.hasNext()) {
            bg = a_Iterator.next();
            if (bg != g) {
                if (bg.swapToFile_Grid2DSquareCellChunk_Account(HandleOutOfMemoryErrorFalse) > 0) {
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
    public static void combine(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> toGetCombined,
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> toCombine) {
        if (toCombine != null) {
            if (!toCombine.isEmpty()) {
                Set<Grids_AbstractGrid> toGetCombined_KeySet = toGetCombined.keySet();
                Set<Grids_AbstractGrid> toCombine_KeySet = toCombine.keySet();
                Iterator<Grids_AbstractGrid> toCombine_KeySet_Iterator = toCombine_KeySet.iterator();
                Grids_AbstractGrid a_AbstractGrid2DSquareCell;
                while (toCombine_KeySet_Iterator.hasNext()) {
                    a_AbstractGrid2DSquareCell = toCombine_KeySet_Iterator.next();
                    if (toGetCombined_KeySet.contains(a_AbstractGrid2DSquareCell)) {
                        toGetCombined.get(a_AbstractGrid2DSquareCell).addAll(toCombine.get(a_AbstractGrid2DSquareCell));
                    } else {
                        toGetCombined.put(a_AbstractGrid2DSquareCell, toCombine.get(a_AbstractGrid2DSquareCell));
                    }
                }
            }
        }
    }

    /**
     * Attempts to swap any Grids_AbstractGridChunk in
     * this._AbstractGrid2DSquareCell_HashSet.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunk_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        Iterator<Grids_AbstractGrid> a_Iterator = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        while (a_Iterator.hasNext()) {
            Grids_AbstractGrid a_Grid2DSquareCell = a_Iterator.next();
            result = a_Grid2DSquareCell.swapToFile_Grid2DSquareCellChunk_AccountDetail(
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
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet except
     * for those in with Grids_AbstractGrid.ID = _ChunkID.
     * @param a_ChunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                    a_ChunkID);
            try {
                if (result.isEmpty()) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                            a_ChunkID);
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            combine(result,
                                    (HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>) account[1]);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                            = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                                    a_ChunkID,
                                    handleOutOfMemoryError);
                    combine(result,
                            potentialPartResult);
                }
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result
                        = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                                a_ChunkID);
                if (result.isEmpty()) {
                    throw a_OutOfMemoryError;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                        = init_MemoryReserve_AccountDetail(
                                a_ChunkID,
                                handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet except
     * for those in with Grids_AbstractGrid.ID = _ChunkID.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
            Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        Iterator<Grids_AbstractGrid> a_Iterator = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid a_AbstractGrid2DSquareCell;
        while (a_Iterator.hasNext()) {
            a_AbstractGrid2DSquareCell = a_Iterator.next();
            result = a_AbstractGrid2DSquareCell.swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                    chunkID, HandleOutOfMemoryErrorFalse);
            if (!result.isEmpty()) {
                HashSet<Grids_2D_ID_int> a_ChunkID_HashSet = new HashSet<>(1);
                a_ChunkID_HashSet.add(chunkID);
                result.put(
                        a_AbstractGrid2DSquareCell,
                        a_ChunkID_HashSet);
                return result;
            }
        }
        return null;
    }

    public long swapToFile_Grid2DSquareCellChunkExcept_Account(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_Grid2DSquareCellChunkExcept_Account(
                    a_ChunkID);
            try {
                if (result < 1) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            a_ChunkID);
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            result += (Long) account[1];
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            a_ChunkID,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
            result += tryToEnsureThereIsEnoughMemoryToContinue_Account(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result
                        = swapToFile_Grid2DSquareCellChunkExcept_Account(
                                a_ChunkID);
                if (result < 1L) {
                    throw a_OutOfMemoryError;
                }
                result += init_MemoryReserve_Account(
                        a_ChunkID,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet except
     * for those in with Grids_AbstractGrid.ID = _ChunkID.
     * @param a_ChunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    protected long swapToFile_Grid2DSquareCellChunkExcept_Account(
            Grids_2D_ID_int a_ChunkID) {
        long result = 0L;
        Iterator<Grids_AbstractGrid> a_Iterator = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid a_AbstractGrid2DSquareCell;
        while (a_Iterator.hasNext()) {
            a_AbstractGrid2DSquareCell = a_Iterator.next();
            result += a_AbstractGrid2DSquareCell.ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                    a_ChunkID);
            if (result > 0L) {
                return result;
            }
        }
        return result;
    }

    /**
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    protected void swapToFile_Grid2DSquareCellChunkExcept(
            Grids_2D_ID_int chunkID) {
        Iterator<Grids_AbstractGrid> ite;
        ite = getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid g;
        while (ite.hasNext()) {
            g = ite.next();
            if (g.ge.swapToFile_Grid2DSquareCellChunkExcept_Account(chunkID) > 0) {
                return;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet except
     * for those in _Grid2DSquareCell_ChunkIDHashSet.
     * @param a_Grid2DSquareCell_ChunkIDHashSet HashMap with Grids_AbstractGrid
     * as keys and a respective HashSet of Grids_AbstractGrid.ChunkIDs.
     * Identifying those ChunkIDs not to be swapped from the Grids_AbstractGrid.
     * TODO
     * tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(<Grids_AbstractGrid,
     * HashSet<ChunkID>>,boolean);
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_Grid2DSquareCell_ChunkIDHashSet,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                    a_Grid2DSquareCell_ChunkIDHashSet);
            try {
                if (result.isEmpty()) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                            a_Grid2DSquareCell_ChunkIDHashSet);
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            combine(result,
                                    (HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>) account[1]);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                            = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                                    a_Grid2DSquareCell_ChunkIDHashSet,
                                    handleOutOfMemoryError);
                    combine(result,
                            potentialPartResult);
                }
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                        a_Grid2DSquareCell_ChunkIDHashSet);
                if (result.isEmpty()) {
                    throw e;
                }
                init_MemoryReserve(
                        a_Grid2DSquareCell_ChunkIDHashSet,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * @param a_Grid2DSquareCell_ChunkID_HashSet_HashMap
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet except
     * for those in _Grid2DSquareCell_ChunkIDHashSet.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_Grid2DSquareCell_ChunkID_HashSet_HashMap) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite;
        ite = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid a_Grid2DSquareCell;
        HashSet<Grids_2D_ID_int> result_ChunkID_HashSet;
        result_ChunkID_HashSet = new HashSet<>(1);
        HashSet<Grids_2D_ID_int> chunkID_HashSet;
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            a_Grid2DSquareCell = ite.next();
            if (a_Grid2DSquareCell_ChunkID_HashSet_HashMap.containsKey(a_Grid2DSquareCell)) {
                chunkID_HashSet = a_Grid2DSquareCell_ChunkID_HashSet_HashMap.get(a_Grid2DSquareCell);
                chunkID = a_Grid2DSquareCell.swapToFile_Grid2DSquareCellChunkExcept_AccountChunk(
                        chunkID_HashSet,
                        HandleOutOfMemoryErrorFalse);
                if (chunkID != null) {
                    result_ChunkID_HashSet.add(chunkID);
                    result.put(
                            a_Grid2DSquareCell,
                            result_ChunkID_HashSet);
                    return result;
                }
            }
            chunkID = a_Grid2DSquareCell.swapToFile_Grid2DSquareCellChunk_AccountChunk(HandleOutOfMemoryErrorFalse);
            if (chunkID != null) {
                result_ChunkID_HashSet.add(chunkID);
                result.put(
                        a_Grid2DSquareCell,
                        result_ChunkID_HashSet);
                return result;
            }
        }
        return result; // If the program gets here then nothing could be swapped.
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite;
        ite = getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid bg;
        HashSet<Grids_2D_ID_int> result_ChunkID_HashSet;
        result_ChunkID_HashSet = new HashSet<>(1);
        Grids_2D_ID_int b_ChunkID;
        while (ite.hasNext()) {
            bg = ite.next();
            if (g == bg) {
                b_ChunkID = bg.swapToFile_Grid2DSquareCellChunkExcept_AccountChunk(
                        chunkIDs,
                        HandleOutOfMemoryErrorFalse);
                if (b_ChunkID != null) {
                    result_ChunkID_HashSet.add(b_ChunkID);
                    result.put(
                            g,
                            result_ChunkID_HashSet);
                    return result;
                }
            } else {
                b_ChunkID = g.swapToFile_Grid2DSquareCellChunk_AccountChunk(HandleOutOfMemoryErrorFalse);
                if (b_ChunkID != null) {
                    result_ChunkID_HashSet.add(b_ChunkID);
                    result.put(
                            g,
                            result_ChunkID_HashSet);
                    return result;
                }
            }
        }
        return result; // If we get here then nothing could be swapped.
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite;
        ite = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid bg;
        HashSet<Grids_2D_ID_int> result_ChunkID_HashSet = new HashSet<>(1);
        Grids_2D_ID_int b_ChunkID;
        while (ite.hasNext()) {
            bg = ite.next();
            if (g == bg) {
                b_ChunkID = bg.swapToFile_Grid2DSquareCellChunkExcept_AccountChunk(
                        chunkID,
                        HandleOutOfMemoryErrorFalse);
                if (b_ChunkID != null) {
                    result_ChunkID_HashSet.add(b_ChunkID);
                    result.put(
                            g,
                            result_ChunkID_HashSet);
                    return result;
                }
            } else {
                b_ChunkID = g.swapToFile_Grid2DSquareCellChunk_AccountChunk(HandleOutOfMemoryErrorFalse);
                if (b_ChunkID != null) {
                    result_ChunkID_HashSet.add(b_ChunkID);
                    result.put(
                            g,
                            result_ChunkID_HashSet);
                    return result;
                }
            }
        }
        return result; // If we get here then nothing could be swapped.
    }

    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                    g,
                    chunkID);
            return result;
        } catch (java.lang.OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                        g,
                        chunkID);
                if (partResult.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = init_MemoryReserve_AccountDetail(
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

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
            Grids_AbstractGrid g) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        Iterator<Grids_AbstractGrid> ite;
        ite = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid bg;
        HashSet<Grids_2D_ID_int> result_ChunkID_HashSet;
        result_ChunkID_HashSet = new HashSet<>(1);
        Grids_2D_ID_int b_ChunkID;
        while (ite.hasNext()) {
            bg = ite.next();
            if (g != bg) {
                b_ChunkID = bg.swapToFile_Grid2DSquareCellChunk_AccountChunk(HandleOutOfMemoryErrorFalse);
                if (b_ChunkID != null) {
                    result_ChunkID_HashSet.add(b_ChunkID);
                    result.put(
                            g,
                            result_ChunkID_HashSet);
                    return result;
                }
            }
        }
        return result; // If we get here then nothing could be swapped.
    }

    protected long swapToFile_Grid2DSquareCellChunkExcept_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_Grid2DSquareCell_ChunkID_HashSet_HashMap) {
        long result = 0L;
        Iterator<Grids_AbstractGrid> a_Iterator = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid a_Grid2DSquareCell;
        HashSet<Grids_2D_ID_int> a_ChunkID_HashSet;
        Grids_2D_ID_int a_ChunkID;
        while (a_Iterator.hasNext()) {
            a_Grid2DSquareCell = a_Iterator.next();
            if (a_Grid2DSquareCell_ChunkID_HashSet_HashMap.containsKey(a_Grid2DSquareCell)) {
                a_ChunkID_HashSet = a_Grid2DSquareCell_ChunkID_HashSet_HashMap.get(a_Grid2DSquareCell);
                a_ChunkID = a_Grid2DSquareCell.swapToFile_Grid2DSquareCellChunkExcept_AccountChunk(
                        a_ChunkID_HashSet,
                        HandleOutOfMemoryErrorFalse);
                if (a_ChunkID != null) {
                    return 1L;
                }
            }
            a_ChunkID = a_Grid2DSquareCell.swapToFile_Grid2DSquareCellChunk_AccountChunk(HandleOutOfMemoryErrorFalse);
            if (a_ChunkID != null) {
                return 1L;
            }
        }
        return result; // If we get here then nothing could be swapped.
    }

    protected void swapToFile_Grid2DSquareCellChunkExcept(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_Grid2DSquareCell_ChunkID_HashSet_HashMap) {
        Iterator<Grids_AbstractGrid> a_Iterator = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid a_Grid2DSquareCell;
        HashSet<Grids_2D_ID_int> result_ChunkID_HashSet = new HashSet<>(1);
        HashSet<Grids_2D_ID_int> a_ChunkID_HashSet;
        Grids_2D_ID_int a_ChunkID;
        while (a_Iterator.hasNext()) {
            a_Grid2DSquareCell = a_Iterator.next();
            if (a_Grid2DSquareCell_ChunkID_HashSet_HashMap.containsKey(a_Grid2DSquareCell)) {
                a_ChunkID_HashSet = a_Grid2DSquareCell_ChunkID_HashSet_HashMap.get(a_Grid2DSquareCell);
                a_ChunkID = a_Grid2DSquareCell.swapToFile_Grid2DSquareCellChunkExcept_AccountChunk(
                        a_ChunkID_HashSet,
                        HandleOutOfMemoryErrorFalse);
                if (a_ChunkID != null) {
                    return;
                }
            }
            a_ChunkID = a_Grid2DSquareCell.swapToFile_Grid2DSquareCellChunk_AccountChunk(HandleOutOfMemoryErrorFalse);
            if (a_ChunkID != null) {
                result_ChunkID_HashSet.add(a_ChunkID);
                return;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
     * @param a_ChunkID_HashSet
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet except
     * for those in _Grid2DSquareCell with Grids_AbstractGrid._ChunkID in
     * a_ChunkID_HashSet.
     * @param a_Grid2DSquareCell Grids_AbstractGrid that's chunks are not to be
     * swapped.
     */
    public long swapToFile_Grid2DSquareCellChunkExcept_Account(
            Grids_AbstractGrid a_Grid2DSquareCell,
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_Grid2DSquareCellChunkExcept_Account(
                    a_Grid2DSquareCell,
                    a_ChunkID_HashSet);
            try {
                if (result < 1) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            a_Grid2DSquareCell,
                            a_ChunkID_HashSet);
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            result += (Long) account[1];
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            a_Grid2DSquareCell,
                            a_ChunkID_HashSet,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = swapToFile_Grid2DSquareCellChunkExcept_Account(
                        a_Grid2DSquareCell,
                        a_ChunkID_HashSet);
                if (result < 1L) {
                    throw a_OutOfMemoryError;
                }
                result += init_MemoryReserve_Account(
                        a_Grid2DSquareCell,
                        a_ChunkID_HashSet,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param a_ChunkID_HashSet
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet except
     * for those in _Grid2DSquareCell with Grids_AbstractGrid._ChunkID in
     * a_ChunkID_HashSet.
     * @param a_Grid2DSquareCell Grids_AbstractGrid that's chunks are not to be
     * swapped.
     */
    protected long swapToFile_Grid2DSquareCellChunkExcept_Account(
            Grids_AbstractGrid a_Grid2DSquareCell,
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet) {
        Iterator<Grids_AbstractGrid> a_Iterator = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid b_Grid2DSquareCell;
        HashMap<Grids_2D_ID_int, Grids_AbstractGridChunk> b_ChunkID_AbstractGrid2DSquareCellChunk_HashMap;
        Set<Grids_2D_ID_int> b_ChunkID_Set;
        Iterator<Grids_2D_ID_int> b_Iterator;
        Grids_2D_ID_int b_ChunkID;
        Grids_AbstractGridChunk b_AbstractGrid2DSquareCellChunk;
        while (a_Iterator.hasNext()) {
            b_Grid2DSquareCell = a_Iterator.next();
            if (b_Grid2DSquareCell != a_Grid2DSquareCell) {
                b_ChunkID_AbstractGrid2DSquareCellChunk_HashMap = b_Grid2DSquareCell.getChunkID_AbstractGrid2DSquareCellChunk_HashMap();
                b_ChunkID_Set = b_ChunkID_AbstractGrid2DSquareCellChunk_HashMap.keySet();
                b_Iterator = b_ChunkID_Set.iterator();
                while (b_Iterator.hasNext()) {
                    b_ChunkID = b_Iterator.next();
                    if (!a_ChunkID_HashSet.contains(b_ChunkID)) {
                        //Check it can be swapped
                        b_AbstractGrid2DSquareCellChunk = b_ChunkID_AbstractGrid2DSquareCellChunk_HashMap.get(b_ChunkID);
                        if (b_AbstractGrid2DSquareCellChunk != null) {
                            b_Grid2DSquareCell.swapToFile_Grid2DSquareCellChunk(b_ChunkID, HandleOutOfMemoryErrorFalse);
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
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet except
     * for that in _Grid2DSquareCell with Grids_AbstractGrid._ChunkID _ChunkID.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    public long swapToFile_Grid2DSquareCellChunkExcept_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_Grid2DSquareCellChunkExcept_Account(
                    g,
                    chunkID);
            try {
                if (result < 1) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            g,
                            chunkID);
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            result += (Long) account[1];
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
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = swapToFile_Grid2DSquareCellChunkExcept_Account(
                        g,
                        chunkID);
                if (result < 1L) {
                    throw e;
                }
                result += init_MemoryReserve_Account(
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
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet except
     * for that in _Grid2DSquareCell with Grids_AbstractGrid._ChunkID _ChunkID.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    protected long swapToFile_Grid2DSquareCellChunkExcept_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        long result = swapToFile_Grid2DSquareCellChunkExcept_Account(
                g);
        if (result < 1L) {
//            result = g.swapToFile_Grid2DSquareCellChunkExcept_Account(
//                    chunkID, 
//                    HandleOutOfMemoryErrorFalse);
            result = g.swapToFile_Grid2DSquareCellChunkExcept_Account(
                    chunkID,
                    false);
        }
        return result;
    }

    /**
     * @param handleOutOfMemoryError
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet except
     * for those in _Grid2DSquareCell.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     */
    public long swapToFile_Grid2DSquareCellChunkExcept_Account(
            Grids_AbstractGrid g,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_Grid2DSquareCellChunkExcept_Account(
                    g);
            try {
                if (result < 1) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            g);
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            result += (Long) account[1];
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            g,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = swapToFile_Grid2DSquareCellChunkExcept_Account(
                        g);
                if (result < 1L) {
                    throw a_OutOfMemoryError;
                }
                result += init_MemoryReserve_Account(
                        g,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped. Attempts to swap any
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet except
     * for those in _Grid2DSquareCell.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     */
    protected long swapToFile_Grid2DSquareCellChunkExcept_Account(
            Grids_AbstractGrid g) {
        Iterator<Grids_AbstractGrid> ite;
        ite = getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid bg;
        while (ite.hasNext()) {
            bg = ite.next();
            if (bg != g) {
                Grids_2D_ID_int chunkID;
                chunkID = bg.swapToFile_Grid2DSquareCellChunk_AccountChunk(
                        HandleOutOfMemoryErrorFalse);
                if (chunkID != null) {
                    return 1L;
                }
            }
        }
        return 0L;
    }

    /**
     * Attempts to Swap all Grids_AbstractGrid.ChunkIDs in
     * this._AbstractGrid2DSquareCell_HashSet except those with
     * Grids_AbstractGrid.ID _ChunkID.
     *
     * @param handleOutOfMemoryError
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param a_ChunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
                    a_ChunkID);
            try {
                if (result.isEmpty()) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                            a_ChunkID);
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            combine(result,
                                    (HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>) account[1]);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                            = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(a_ChunkID, handleOutOfMemoryError);
                    combine(result,
                            potentialPartResult);
                }
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                        a_ChunkID);
                if (result.isEmpty()) {
                    throw a_OutOfMemoryError;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                        = init_MemoryReserve_AccountDetail(
                                a_ChunkID,
                                handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                potentialPartResult = swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
                        a_ChunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Attempts to Swap all Grids_AbstractGrid.ChunkIDs in
     * this._AbstractGrid2DSquareCell_HashSet except those with
     * Grids_AbstractGrid.ID _ChunkID.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
            Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = new HashMap<>();
        Iterator<Grids_AbstractGrid> a_Iterator = this.getAbstractGrid2DSquareCell_HashSet().iterator();
        while (a_Iterator.hasNext()) {
            Grids_AbstractGrid a_Grid2DSquareCell = a_Iterator.next();
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            potentialPartResult = a_Grid2DSquareCell.swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
                    chunkID, false);
            combine(result,
                    potentialPartResult);
        }
        return result;
    }

    /**
     * Attempts to Swap all Grids_AbstractGrid.ChunkIDs in
     * this._AbstractGrid2DSquareCell_HashSet except those with
     * Grids_AbstractGrid.ID _ChunkID.
     *
     * @param handleOutOfMemoryError
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param a_Grid2DSquareCell Grids_AbstractGrid that's chunks are not to be
     * swapped. swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
            Grids_AbstractGrid a_Grid2DSquareCell,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
                    a_Grid2DSquareCell);
            try {
                if (result.isEmpty()) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                            a_Grid2DSquareCell);
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            combine(result,
                                    (HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>) account[1]);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                            = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(a_Grid2DSquareCell, handleOutOfMemoryError);
                    combine(result,
                            potentialPartResult);
                }
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                        a_Grid2DSquareCell);
                if (result.isEmpty()) {
                    throw a_OutOfMemoryError;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                        = init_MemoryReserve_AccountDetail(
                                a_Grid2DSquareCell,
                                handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                potentialPartResult = swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
                        a_Grid2DSquareCell,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Attempts to Swap all Grids_AbstractGrid.ChunkIDs in
     * this._AbstractGrid2DSquareCell_HashSet except those with
     * Grids_AbstractGrid.ID _ChunkID.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
            Grids_AbstractGrid g) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite;
        ite = getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid bg;
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
        while (ite.hasNext()) {
            bg = ite.next();
            if (bg != g) {
                potentialPartResult = bg.swapToFile_Grid2DSquareCellChunks_AccountDetail(false);
                combine(result,
                        potentialPartResult);
            }
        }
        return result;
    }

    public long swapToFile_Grid2DSquareCellChunksExcept_Account(
            Grids_AbstractGrid g,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_Grid2DSquareCellChunksExcept_Account(
                    g);
            try {
                if (result < 1) {
                    Object[] account;
                    account = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            g);
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            result += (Long) account[1];
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            g,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = swapToFile_Grid2DSquareCellChunkExcept_Account(
                        g);
                if (result < 1L) {
                    throw a_OutOfMemoryError;
                }
                result += init_MemoryReserve_Account(
                        g,
                        handleOutOfMemoryError);
                result += swapToFile_Grid2DSquareCellChunksExcept_Account(
                        g,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected long swapToFile_Grid2DSquareCellChunksExcept_Account(
            Grids_AbstractGrid a_Grid2DSquareCell) {
        long result = 0L;
        Iterator<Grids_AbstractGrid> a_Iterator = getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid b_Grid2DSquareCell;
        while (a_Iterator.hasNext()) {
            b_Grid2DSquareCell = a_Iterator.next();
            if (b_Grid2DSquareCell != a_Grid2DSquareCell) {
                result += b_Grid2DSquareCell.ge.swapToFile_Grid2DSquareCellChunks_Account();
            }
        }
        return result;
    }

    /**
     * Attempts to Swap all Grids_AbstractGrid.ChunkIDs in
     * this._AbstractGrid2DSquareCell_HashSet except those with
     * Grids_AbstractGrid.ID _ChunkID.
     *
     * @param handleOutOfMemoryError
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param a_Grid2DSquareCell Grids_AbstractGrid that's chunks are not to be
     * swapped.
     * @param a_ChunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
            Grids_AbstractGrid a_Grid2DSquareCell,
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
                    a_Grid2DSquareCell,
                    a_ChunkID);
            try {
                if (result.isEmpty()) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                            a_Grid2DSquareCell,
                            a_ChunkID);
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            combine(result,
                                    (HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>) account[1]);
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                            = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                                    a_Grid2DSquareCell,
                                    a_ChunkID,
                                    handleOutOfMemoryError);
                    combine(result,
                            potentialPartResult);
                }
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                        a_Grid2DSquareCell,
                        a_ChunkID);
                if (result.isEmpty()) {
                    throw a_OutOfMemoryError;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult = init_MemoryReserve_AccountDetail(
                        a_Grid2DSquareCell,
                        a_ChunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                potentialPartResult = swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
                        a_Grid2DSquareCell,
                        a_ChunkID,
                        handleOutOfMemoryError);
                combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public long swapToFile_Grid2DSquareCellChunksExcept_Account(
            Grids_AbstractGrid a_Grid2DSquareCell,
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_Grid2DSquareCellChunksExcept_Account(
                    a_Grid2DSquareCell,
                    a_ChunkID);
            try {
                if (result < 1) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            a_Grid2DSquareCell,
                            a_ChunkID);
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            result += (Long) account[1];
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            a_Grid2DSquareCell,
                            a_ChunkID,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = swapToFile_Grid2DSquareCellChunkExcept_Account(
                        a_Grid2DSquareCell,
                        a_ChunkID);
                if (result < 1L) {
                    throw a_OutOfMemoryError;
                }
                result += init_MemoryReserve_Account(
                        a_ChunkID,
                        handleOutOfMemoryError);
                result += swapToFile_Grid2DSquareCellChunkExcept_Account(
                        a_Grid2DSquareCell,
                        a_ChunkID);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected long swapToFile_Grid2DSquareCellChunksExcept_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite;
        ite = getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid bg;
        while (ite.hasNext()) {
            bg = ite.next();
            if (bg != g) {
                int cri0 = 0;
                int cci0 = 0;
                int cri1 = bg.getNChunkRows() - 1;
                int cci1 = bg.getNChunkCols() - 1;
                result += bg.swapToFile_Grid2DSquareCellChunks_Account(
                        cri0, cci0, cri1, cci1, false);
            } else {
                result += bg.swapToFile_Grid2DSquareCellChunksExcept_Account(
                        chunkID, false);
            }
        }
        return result;
    }

    /**
     * Attempts to Swap all Grids_AbstractGrid.ChunkIDs in
     * this._AbstractGrid2DSquareCell_HashSet except those with
     * Grids_AbstractGrid.ID _ChunkID.
     *
     * @return HashMap with: key as the Grids_AbstractGrid from which the
     * Grids_AbstractGridChunk was swapped; and, value as the
     * Grids_AbstractGridChunk._ChunkID swapped.
     * @param g Grids_AbstractGrid that's chunks are not to be swapped.
     * @param chunkID The Grids_AbstractGrid.ID not to be swapped.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite;
        ite = getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid bg;
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
        while (ite.hasNext()) {
            bg = ite.next();
            if (bg == g) {
                potentialPartResult = bg.swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
                        chunkID, false);
                combine(result,
                        potentialPartResult);
            } else {
                potentialPartResult = bg.swapToFile_Grid2DSquareCellChunks_AccountDetail(false);
                combine(result,
                        potentialPartResult);
            }
        }
        return result;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>();
        Iterator<Grids_AbstractGrid> ite;
        ite = getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid bg;
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
        while (ite.hasNext()) {
            bg = ite.next();
            if (bg != g) {
                potentialPartResult = bg.swapToFile_Grid2DSquareCellChunks_AccountDetail(false);
                combine(result, potentialPartResult);
            } else {
                HashSet<Grids_2D_ID_int> chunks;
                chunks = g.getGrid2DSquareCellChunkIDHashSet(false);
                Grids_2D_ID_int id;
                Iterator<Grids_2D_ID_int> ite2;
                ite2 = chunks.iterator();
                while (ite2.hasNext()) {
                    id = ite2.next();
                    if (!chunkIDs.contains(id)) {
                        potentialPartResult = swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
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
    protected long swapToFile_Grid2DSquareCellChunksExcept_Account(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunkIDs) {
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite;
        ite = getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid bg;
        while (ite.hasNext()) {
            bg = ite.next();
            if (bg != g) {
                int cri0 = 0;
                int cri1 = bg.getNChunkRows() - 1;
                int cci0 = 0;
                int cci1 = bg.getNChunkCols() - 1;
                result += bg.swapToFile_Grid2DSquareCellChunks_Account(
                        cri0, cci0, cri1, cci1, false);
            } else {
                result += bg.swapToFile_Grid2DSquareCellChunksExcept_Account(
                        chunkIDs, false);
            }
        }
        return result;
    }

    /**
     * @param a_Grid2DSquareCell_ChunkID_HashSet_HashMap
     * @param handleOutOfMemoryError
     * @return
     */
    public long swapToFile_Grid2DSquareCellChunksExcept_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_Grid2DSquareCell_ChunkID_HashSet_HashMap,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_Grid2DSquareCellChunksExcept_Account(
                    a_Grid2DSquareCell_ChunkID_HashSet_HashMap);
            try {
                if (result < 1) {
                    Object[] account = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            a_Grid2DSquareCell_ChunkID_HashSet_HashMap);
                    if (account != null) {
                        if ((Boolean) account[0]) {
                            result += (Long) account[1];
                        } else {
                            throw new OutOfMemoryError();
                        }
                    }
                } else {
                    result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                            a_Grid2DSquareCell_ChunkID_HashSet_HashMap,
                            handleOutOfMemoryError);
                }
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Set handleOutOfMemoryError = false to exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = swapToFile_Grid2DSquareCellChunkExcept_Account(
                        a_Grid2DSquareCell_ChunkID_HashSet_HashMap);
                if (result < 1L) {
                    throw a_OutOfMemoryError;
                }
                result += init_MemoryReserve_Account(
                        a_Grid2DSquareCell_ChunkID_HashSet_HashMap,
                        handleOutOfMemoryError);
                result += swapToFile_Grid2DSquareCellChunksExcept_Account(
                        a_Grid2DSquareCell_ChunkID_HashSet_HashMap);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected long swapToFile_Grid2DSquareCellChunksExcept_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_Grid2DSquareCell_ChunkID_HashSet_HashMap) {
        long result = 0L;
        Iterator<Grids_AbstractGrid> ite;
        ite = getAbstractGrid2DSquareCell_HashSet().iterator();
        Grids_AbstractGrid g;
        HashSet<Grids_2D_ID_int> a_ChunkID_HashSet;
        while (ite.hasNext()) {
            g = ite.next();
            a_ChunkID_HashSet = a_Grid2DSquareCell_ChunkID_HashSet_HashMap.get(g);
            result += g.swapToFile_Grid2DSquareCellChunksExcept_Account(
                    a_ChunkID_HashSet, false);
        }
        return result;
    }

    public void swapToFile_Data() {
        swapToFile_Grid2DSquareCellChunks();
    }

    public void swapToFile_Data(boolean handleOutOfMemoryError) {
        swapToFile_Grid2DSquareCellChunks();
    }

    @Override
    public boolean swapToFile_DataAny(boolean handleOutOfMemoryError) {
        try {
            boolean result = swapToFile_Grid2DSquareCellChunk();
            try {
                if (!tryToEnsureThereIsEnoughMemoryToContinue()) {
                    throw new OutOfMemoryError();
                }
            } catch (OutOfMemoryError a_OutOfMemoryError) {
                // Exit method by throwing OutOfMemoryError
                handleOutOfMemoryError = false;
                throw a_OutOfMemoryError;
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                boolean result = swapToFile_DataAny(
                        HandleOutOfMemoryErrorFalse);
                init_MemoryReserve();
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    @Override
    public boolean swapToFile_DataAny() {
        return swapToFile_Grid2DSquareCellChunk();
    }

    private boolean dataToSwap = true;

    public boolean isDataToSwap() {
        return dataToSwap;
    }

    public void setDataToSwap(boolean dataToSwap) {
        this.dataToSwap = dataToSwap;
    }

//    /**
//     * This method is called for handling OutOfMemoryErrors
//     * @param e
//     * @param grid
//     * @param _NChunkCols
//     * @param _ChunkRowIndex
//     * @param _ChunkColIndex 
//     */
//    protected void handleOutOfMemoryErrorOrNot(
//            OutOfMemoryError e,
//            Grids_AbstractGrid grid,
//            int _NChunkCols,
//            int _ChunkRowIndex,
//            int _ChunkColIndex) {
//        Grids_2D_ID_int a_ChunkID = new ID(
//                _NChunkCols,
//                _ChunkRowIndex,
//                _ChunkColIndex);
//        long numberOfSwappedChunks;
//        numberOfSwappedChunks = this.swapToFile_Grid2DSquareCellChunksExcept_Account(
//                grid, a_ChunkID);
//        if (numberOfSwappedChunks < 1L) {
//            throw e;
//        }
//    }
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> freeSomeMemoryAndResetReserve_AccountDetails(
            Grids_AbstractGrid g,
            boolean handleOutOfMemoryError) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                g,
                handleOutOfMemoryError);
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
        potentialPartResult = init_MemoryReserve_AccountDetail(
                g,
                handleOutOfMemoryError);
        combine(result,
                potentialPartResult);
        return result;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> freeSomeMemoryAndResetReserve_AccountDetails(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunks,
            boolean handleOutOfMemoryError) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                g,
                chunks,
                handleOutOfMemoryError);
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult = init_MemoryReserve_AccountDetail(
                g,
                chunks,
                handleOutOfMemoryError);
        combine(result,
                potentialPartResult);
        return result;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> freeSomeMemoryAndResetReserve_AccountDetails(
            OutOfMemoryError e,
            boolean handleOutOfMemoryError) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = swapToFile_Grid2DSquareCellChunk_AccountDetail();
        if (result.isEmpty()) {
            throw e;
        }
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult
                = init_MemoryReserve_AccountDetail(handleOutOfMemoryError);
        combine(result,
                potentialPartResult);
        return result;
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> freeSomeMemoryAndResetReserve_AccountDetails(
            boolean handleOutOfMemoryError) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                handleOutOfMemoryError);
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
        potentialPartResult = init_MemoryReserve_AccountDetail(
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
        result += init_MemoryReserve_Account(
                g,
                chunks,
                handleOutOfMemoryError);
        return result;
    }

    protected long freeSomeMemoryAndResetReserve_Account(
            OutOfMemoryError e,
            boolean handleOutOfMemoryError) {
        long result = swapToFile_Grid2DSquareCellChunk_Account();
        if (result < 1L) {
            throw e;
        }
        result += init_MemoryReserve_Account(
                handleOutOfMemoryError);
        return result;
    }

    /**
     * @return the _NotToSwapData
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> getNotToSwapData() {
        return _NotToSwapData;
    }
}
