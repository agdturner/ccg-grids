/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package uk.ac.leeds.ccg.andyt.grids.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import uk.ac.leeds.ccg.andyt.grids.core.AbstractGrid2DSquareCell.ChunkID;
import uk.ac.leeds.ccg.andyt.grids.exchange.ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.andyt.grids.utilities.Utilities;

/**
 * A class to represent and manipulate int precision AbstractGrid2DSquareCell
 * instances.
 */
public class Grid2DSquareCellInt
        extends AbstractGrid2DSquareCell
        implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new Grid2DSquareCellInt
     * @param a_Grids_Environment
     */
    public Grid2DSquareCellInt(
            Grids_Environment a_Grids_Environment) {
        initGrid2DSquareCell(a_Grids_Environment);
    }

    /**
     * Creates a new Grid2DSquareCellInt.
     * Warning!! Concurrent modification may occur if _Directory is in use.
     * If a completely new instance is wanted then use:
     * Grid2DSquareCellInt(
     *   File,
     *   Grid2DSquareCellIntAbstract,
     *   AbstractGrid2DSquareCellIntChunkFactory,
     *   int,
     *   int,
     *   long,
     *   long,
     *   long,
     *   long,
     *   double,
     *   HashSet)
     * which can be accessed via a Grid2DSquareCellIntFactory.
     * @param _Directory The File _Directory to be used for swapping.
     * @param gridFile The File _Directory containing the File names thisFile that
     *   the ois was constructed from.
     * @param ois The ObjectInputStream used in first attempt to construct this.
     * @param a_Grids_Environment
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    protected Grid2DSquareCellInt(
            File _Directory,
            File gridFile,
            ObjectInputStream ois,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        initGrid2DSquareCellInt(
                _Directory,
                gridFile,
                ois,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellInt with each cell value equal to
     * Integer.MinValue.
     * N.B. This method can be used to test which type of chunks are more
     * efficient in different situations. If this becomes clear then it may be
     * possible to automatically optimise chunk storage. Although, optimisiation
     * is going to be difficult owing to the trade off between fast access and
     * low memory.
     * @param _GridStatistics The AbstractGridStatistics to accompany this.
     * @param _Directory The File _Directory to be used for swapping.
     * @param _Grid2DSquareCellIntChunkFactory The
     *   AbstractGrid2DSquareCellIntChunkFactory prefered for creating chunks.
     * @param _ChunkNRows The number of rows of cells in any chunk.
     * @param _ChunkNCols The number of columns of cells in any chunk.
     * @param _NRows The number of rows of cells.
     * @param _NCols The number of columns of cells.
     * @param _Dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param a_Grids_Environment
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    protected Grid2DSquareCellInt(
            AbstractGridStatistics _GridStatistics,
            File _Directory,
            AbstractGrid2DSquareCellIntChunkFactory _Grid2DSquareCellIntChunkFactory,
            int _ChunkNRows,
            int _ChunkNCols,
            long _NRows,
            long _NCols,
            BigDecimal[] _Dimensions,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        initGrid2DSquareCellInt(
                _GridStatistics,
                _Directory,
                _Grid2DSquareCellIntChunkFactory,
                _ChunkNRows,
                _ChunkNCols,
                _NRows,
                _NCols,
                _Dimensions,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellInt based on values in _Grid2DSquareCell.
     * @param _GridStatistics The AbstractGridStatistics to accompany this.
     * @param _Directory The File _Directory to be used for swapping.
     * @param _Grid2DSquareCell The AbstractGrid2DSquareCell from which this will
     * be constructed.
     * @param _Grid2DSquareCellIntChunkFactory The
     *   AbstractGrid2DSquareCellIntChunkFactory prefered to construct chunks
     *   of this.
     * @param _ChunkNRows The number of rows of cells in any chunk.
     * @param _ChunkNCols The number of columns of cells in any chunk.
     * @param startRowIndex The _Grid2DSquareCell row index which is the bottom
     *   most row of this.
     * @param startColIndex The _Grid2DSquareCell column index which is the left
     *   most column of this.
     * @param endRowIndex The _Grid2DSquareCell row index which is the top most
     *   row of this.
     * @param endColIndex The _Grid2DSquareCell column index which is the right
     *   most column of this.
     * @param a_Grids_Environment
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    protected Grid2DSquareCellInt(
            AbstractGridStatistics _GridStatistics,
            File _Directory,
            AbstractGrid2DSquareCell _Grid2DSquareCell,
            AbstractGrid2DSquareCellIntChunkFactory _Grid2DSquareCellIntChunkFactory,
            int _ChunkNRows,
            int _ChunkNCols,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        initGrid2DSquareCellInt(
                _GridStatistics,
                _Directory,
                _Grid2DSquareCell,
                _Grid2DSquareCellIntChunkFactory,
                _ChunkNRows,
                _ChunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellInt with values obtained from gridFile.
     * Currently gridFile must be a _Directory of a Grid2DSquareCellDouble or
     * Grid2DSquareCellInt or a ESRI Asciigrid format Files with a _Name ending
     * ".asc".
     * @param _GridStatistics The AbstractGridStatistics to accompany the
     *   returned grid.
     * @param _Directory The File _Directory to be used for swapping.
     * @param gridFile Either a _Directory, or a formatted File with a specific
     *   extension containing the data and information about the
     *   Grid2DSquareCellDouble to be returned.
     * @param _Grid2DSquareCellIntChunkFactory The
     *   AbstractGrid2DSquareCellIntChunkFactory prefered to construct chunks
     *   of this.
     * @param _ChunkNRows
     * @param startRowIndex The _Grid2DSquareCell row index which is the bottom
     *   most row of this.
     * @param _ChunkNCols
     * @param startColIndex The _Grid2DSquareCell column index which is the left
     *   most column of this.
     * @param endRowIndex The _Grid2DSquareCell row index which is the top most
     *   row of this.
     * @param endColIndex The _Grid2DSquareCell column index which is the right
     *   most column of this.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @param a_Grids_Environment
     */
    protected Grid2DSquareCellInt(
            AbstractGridStatistics _GridStatistics,
            File _Directory,
            File gridFile,
            AbstractGrid2DSquareCellIntChunkFactory _Grid2DSquareCellIntChunkFactory,
            int _ChunkNRows,
            int _ChunkNCols,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        initGrid2DSquareCellInt(
                _GridStatistics,
                _Directory,
                gridFile,
                _Grid2DSquareCellIntChunkFactory,
                _ChunkNRows,
                _ChunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    /**
     * @return a string description of the instance. Basically the values of
     * each field.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public String toString(
            boolean handleOutOfMemoryError) {
        try {
            String result = "Grid2DSquareCellInt( "
                    + "_NoDataValue( " + getNoDataValue(handleOutOfMemoryError) + " ), "
                    + super.toString(0, Grids_Environment.HandleOutOfMemoryErrorFalse) + " )";
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                return toString(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Initialises this.
     * @param _Grid2DSquareCellInt The Grid2DSquareCellInt from which the fields
     *   of this are set.
     * @param initTransientFields Iff true then transient fields of this are set
     *   with those of _Grid2DSquareCellInt.
     */
    protected void initGrid2DSquareCellInt(
            Grid2DSquareCellInt _Grid2DSquareCellInt,
            boolean initTransientFields) {
        try {
            initGrid2DSquareCell(_Grid2DSquareCellInt);
            if (initTransientFields) {
                this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap = _Grid2DSquareCellInt._ChunkID_AbstractGrid2DSquareCellChunk_HashMap;
                //this._AbstractGrid2DSquareCell_HashSet = _Grid2DSquareCellInt._AbstractGrid2DSquareCell_HashSet;
                // Set the reference to this in the Grid Statistics
                this._GridStatistics.init(this);
                //this._GridStatistics._Grid2DSquareCell = this;
            }
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            _Grids_Environment.clear_MemoryReserve();
            if (_Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                throw a_OutOfMemoryError;
            }
            _Grids_Environment.init_MemoryReserve(
                    Grids_Environment.HandleOutOfMemoryErrorTrue);
            initGrid2DSquareCellInt(
                    _Grid2DSquareCellInt,
                    initTransientFields);
        }
    }

    /**
     * Initialises this.
     * @param _Directory The File _Directory to be used for swapping.
     * @param gridFile The File _Directory containing the File named thisFile that
     *   the ois was constructed from.
     * @param ois The ObjectInputStream used in first attempt to construct this.
     * @param _AbstractGrid2DSquareCell_HashSet A HashSet of swappable AbstractGrid2DSquareCell
     *   instances.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @see Grid2DSquareCellInt(
     *      File,
     *      File,
     *      ObjectInputStream,
     *      HashSet,
     *      boolean )
     */
    private void initGrid2DSquareCellInt(
            File _Directory,
            File gridFile,
            ObjectInputStream ois,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        try {
            _Grids_Environment = a_Grids_Environment;
            //init_Grid2DSquareCells_MemoryReserve(_Grids_Environment);
            this._Directory = _Directory;
            File thisFile = new File(
                    gridFile,
                    "thisFile");
            try {
                initGrid2DSquareCellInt(
                        (Grid2DSquareCellInt) ois.readObject(),
                        false);
                //true );
                ois.close();
                // Set the refernce to this in the Grid Chunks
                initGrid2DSquareCellChunks(gridFile);
                Iterator chunkIterator = this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.values().iterator();
                while (chunkIterator.hasNext()) {
                    AbstractGrid2DSquareCellChunk _Grid2DSquareCellChunkAbstract = (AbstractGrid2DSquareCellChunk) chunkIterator.next();
                    _Grid2DSquareCellChunkAbstract._Grid2DSquareCell = this;
                }
                // Set the reference to this in the Grid Statistics
                this._GridStatistics.init(this);
                //this._GridStatistics._Grid2DSquareCell = this;
            } catch (ClassCastException cce) {
                    try {
                        ois.close();
                        ois = new ObjectInputStream(
                                new BufferedInputStream(
                                new FileInputStream(thisFile)));
                        // If the object is a Grid2DSquareCellDouble
                        Grid2DSquareCellDoubleFactory _Grid2DSquareCellDoubleFactory = new Grid2DSquareCellDoubleFactory(
                                _Directory,
                                _Grids_Environment,
                                handleOutOfMemoryError);
                        Grid2DSquareCellDouble _Grid2DSquareCellDouble = (Grid2DSquareCellDouble) _Grid2DSquareCellDoubleFactory.create(
                                _Directory,
                                gridFile,
                                ois);
                        Grid2DSquareCellIntFactory _Grid2DSquareCellntFactory = new Grid2DSquareCellIntFactory(
                                _Directory,
                                _Grids_Environment,
                                handleOutOfMemoryError);
                        Grid2DSquareCellInt _Grid2DSquareCellInt = (Grid2DSquareCellInt) _Grid2DSquareCellntFactory.create(_Grid2DSquareCellDouble);
                        initGrid2DSquareCellInt(
                                _Grid2DSquareCellInt,
                                false);
                        initGrid2DSquareCellChunks(gridFile);
                        // Set the reference to this in the Grid Statistics
                        this._GridStatistics.init(this);
                        //this._GridStatistics._Grid2DSquareCell = this;
                    } catch (IOException ioe) {
                        System.err.println(ioe.getMessage());
                        //throw ioe;
                    }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                //throw e;
            }
            //initGrid2DSquareCellChunks( gridFile );
            _Grids_Environment._AbstractGrid2DSquareCell_HashSet.add(this);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                a_Grids_Environment.clear_MemoryReserve();
                if (a_Grids_Environment.swapToFile_Grid2DSquareCellChunks_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                a_Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                initGrid2DSquareCellInt(
                        _Directory,
                        gridFile,
                        ois,
                        a_Grids_Environment,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Initialises this.
     *
     * @param _GridStatistics The AbstractGridStatistics to accompany this.
     * @param _Directory The File _Directory to be used for swapping.
     * @param _Grid2DSquareCellIntChunkFactory The
     *   AbstractGrid2DSquareCellIntChunkFactory prefered for creating chunks.
     * @param _ChunkNRows The number of rows of cells in any chunk.
     * @param _ChunkNCols The number of columns of cells in any chunk.
     * @param _NRows The number of rows of cells.
     * @param _NCols The number of columns of cells.
     * @param _Dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param _AbstractGrid2DSquareCell_HashSet A HashSet of swappable AbstractGrid2DSquareCell
     *   instances.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @see Grid2DSquareCellInt(
     *      AbstractGridStatistics,
     *      File,
     *      AbstractGrid2DSquareCellIntChunkFactory,
     *      int,
     *      int,
     *      long,
     *      long ,
     *      BigDecimal[],
     *      HashSet,
     *      boolean );
     */
    private void initGrid2DSquareCellInt(
            AbstractGridStatistics _GridStatistics,
            File _Directory,
            AbstractGrid2DSquareCellIntChunkFactory _Grid2DSquareCellIntChunkFactory,
            int _ChunkNRows,
            int _ChunkNCols,
            long _NRows,
            long _NCols,
            BigDecimal[] _Dimensions,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        try {
            _Grids_Environment = a_Grids_Environment;
            //init_Grid2DSquareCells_MemoryReserve(_Grid2DSquareCells);
            this._Directory = _Directory;
            this._GridStatistics = _GridStatistics;
            // Set the reference to this in the Grid Statistics
            this._GridStatistics.init(this);
            //this._GridStatistics._Grid2DSquareCell = this;
            this._Directory = _Directory;
            this._ChunkNRows = _ChunkNRows;
            this._ChunkNCols = _ChunkNCols;
            this._NRows = _NRows;
            this._NCols = _NCols;
            this._Dimensions = _Dimensions;
            init_Dimensions(_Dimensions);
            //this._AbstractGrid2DSquareCell_HashSet = _AbstractGrid2DSquareCell_HashSet;
            _Grids_Environment._AbstractGrid2DSquareCell_HashSet.add(this);
            this._Name = _Directory.getName();
            init_NChunkRows();
            init_NChunkCols();
            long nChunks = getNChunks();
            this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap = new HashMap((int) nChunks);
            int _ChunkRowIndex = Integer.MIN_VALUE;
            int _ChunkColIndex = Integer.MIN_VALUE;
            int loadedChunkCount = 0;
            boolean isLoadedChunk = false;
            int cri0 = 0;
            int cci0 = 0;
            int cci1 = 0;
            String println0 = _Grids_Environment.initString(1000, handleOutOfMemoryError);
            String println = _Grids_Environment.initString(1000, handleOutOfMemoryError);
            ChunkID a_ChunkID = new ChunkID();
            AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellIntChunk = _Grid2DSquareCellIntChunkFactory.createGrid2DSquareCellIntChunk(
                    this,
                    a_ChunkID);
            for (_ChunkRowIndex = 0; _ChunkRowIndex < _NChunkRows; _ChunkRowIndex++) {
                for (_ChunkColIndex = 0; _ChunkColIndex < _NChunkCols; _ChunkColIndex++) {
                    do {
                        try {
                            // Try to load chunk.
                            a_ChunkID = new ChunkID(
                                    this._NChunkCols,
                                    _ChunkRowIndex,
                                    _ChunkColIndex);
                            _Grid2DSquareCellIntChunk = _Grid2DSquareCellIntChunkFactory.createGrid2DSquareCellIntChunk(
                                    this,
                                    a_ChunkID);
                            this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                                    a_ChunkID,
                                    _Grid2DSquareCellIntChunk);
                            isLoadedChunk = true;
                        } catch (OutOfMemoryError a_OutOfMemoryError) {
                            if (handleOutOfMemoryError) {
                                _Grids_Environment.clear_MemoryReserve();
//                                _Grids_Environment.handleOutOfMemoryErrorOrNot(
//                                        a_OutOfMemoryError,
//                                        this,_NChunkCols,
//                                        _ChunkRowIndex, 
//                                        _ChunkColIndex);
                                a_ChunkID = new ChunkID(
                                        this._NChunkCols,
                                        _ChunkRowIndex,
                                        _ChunkColIndex);
                                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(this, a_ChunkID) < 1L) {
                                    throw a_OutOfMemoryError;
                                }
                                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                            } else {
                                throw a_OutOfMemoryError;
                            }
                        }
                    } while (!isLoadedChunk);
                    isLoadedChunk = false;
                    loadedChunkCount++;
                    cci1 = _ChunkColIndex;
                }
                println = "Done chunkRow " + _ChunkRowIndex;
                println = _Grids_Environment.println(println, println0, handleOutOfMemoryError);
            }
            _Grids_Environment._AbstractGrid2DSquareCell_HashSet.add(this);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                a_Grids_Environment.clear_MemoryReserve();
                if (a_Grids_Environment.swapToFile_Grid2DSquareCellChunks_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                a_Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                initGrid2DSquareCellInt(
                        _GridStatistics,
                        _Directory,
                        _Grid2DSquareCellIntChunkFactory,
                        _ChunkNRows,
                        _ChunkNCols,
                        _NRows,
                        _NCols,
                        _Dimensions,
                        a_Grids_Environment,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Initialise this.
     *
     * @param _GridStatistics The AbstractGridStatistics to accompany this.
     * @param _Directory The File _Directory to be used for swapping.
     * @param _Grid2DSquareCell The AbstractGrid2DSquareCell from which this will
     * be constructed.
     * @param _Grid2DSquareCellIntChunkFactory The
     *   AbstractGrid2DSquareCellIntChunkFactory prefered to construct chunks
     *   of this.
     * @param _ChunkNRows The number of rows of cells in any chunk.
     * @param _ChunkNCols The number of columns of cells in any chunk.
     * @param startRowIndex The _Grid2DSquareCell row index which is the bottom
     *   most row of this.
     * @param startColIndex The _Grid2DSquareCell column index which is the left
     *   most column of this.
     * @param endRowIndex The _Grid2DSquareCell row index which is the top most
     *   row of this.
     * @param endColIndex The _Grid2DSquareCell column index which is the right
     *   most column of this.
     * @param a_Grids_Environment
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @see Grid2DSquareCellInt#initGrid2DSquareCellInt(
     *      AbstractGridStatistics,
     *      File,
     *      AbstractGrid2DSquareCell,
     *      AbstractGrid2DSquareCellIntChunkFactory,
     *      int,
     *      int,
     *      long,
     *      long,
     *      long,
     *      long,
     *      HashSet,
     *      boolean )
     */
    protected final void initGrid2DSquareCellInt(
            AbstractGridStatistics _GridStatistics,
            File _Directory,
            AbstractGrid2DSquareCell _Grid2DSquareCell,
            AbstractGrid2DSquareCellIntChunkFactory _Grid2DSquareCellIntChunkFactory,
            int _ChunkNRows,
            int _ChunkNCols,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        try {
            _Grids_Environment = a_Grids_Environment;
            this._Directory = _Directory;
            this._ChunkNRows = _ChunkNRows;
            this._ChunkNCols = _ChunkNCols;
            this._NRows = endRowIndex - startRowIndex + 1L;
            this._NCols = endColIndex - startColIndex + 1L;
            this._Name = _Directory.getName();
            //this._AbstractGrid2DSquareCell_HashSet = _AbstractGrid2DSquareCell_HashSet;
            BigDecimal[] _Grid2DSquareCellDimensions = _Grid2DSquareCell._Dimensions;
            this._Dimensions = new BigDecimal[_Grid2DSquareCellDimensions.length];
            init_NChunkRows();
            init_NChunkCols();
            long nChunks = getNChunks();
            this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap = new HashMap((int) nChunks);
            this._Dimensions[ 0] = new BigDecimal(_Grid2DSquareCellDimensions[ 0].toString());
            BigDecimal startColIndexBigDecimal = new BigDecimal((long) startColIndex);
            BigDecimal startRowIndexBigDecimal = new BigDecimal((long) startRowIndex);
            BigDecimal _NRowsBigDecimal = new BigDecimal(Long.toString(_NRows));
            BigDecimal _NColsBigDecimal = new BigDecimal(Long.toString(_NCols));
            this._Dimensions[ 1] = _Grid2DSquareCellDimensions[ 1].add(startColIndexBigDecimal.multiply(this._Dimensions[ 0]));
            this._Dimensions[ 2] = _Grid2DSquareCellDimensions[ 2].add(startRowIndexBigDecimal.multiply(this._Dimensions[ 0]));
            this._Dimensions[ 3] = this._Dimensions[ 1].add(_NColsBigDecimal.multiply(this._Dimensions[ 0]));
            this._Dimensions[ 4] = this._Dimensions[ 2].add(_NRowsBigDecimal.multiply(this._Dimensions[ 0]));
            init_Dimensions(_Dimensions);
            this._GridStatistics = _GridStatistics;
            // Set the reference to this in the Grid Statistics
            this._GridStatistics.init(this);
            //this._GridStatistics._Grid2DSquareCell = this;
            int _ChunkRowIndex = Integer.MIN_VALUE;
            int _ChunkColIndex = Integer.MIN_VALUE;
            int loadedChunkCount = 0;
            boolean isLoadedChunk = false;
            int chunkRow = Integer.MIN_VALUE;
            int chunkCol = Integer.MIN_VALUE;
            long row = Long.MIN_VALUE;
            long col = Long.MIN_VALUE;
            int cri0 = 0;
            int cci0 = 0;
            int cci1 = 0;
            int _intZero = 0;
            int cellInt = Integer.MIN_VALUE;
            String println0 = _Grids_Environment.initString(1000, handleOutOfMemoryError);
            String println = _Grids_Environment.initString(1000, handleOutOfMemoryError);
            ChunkID a_ChunkID = new ChunkID();
            AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellIntChunk = _Grid2DSquareCellIntChunkFactory.createGrid2DSquareCellIntChunk(
                    this,
                    a_ChunkID);
            if (_GridStatistics.getClass() == GridStatistics0.class) {
                //if ( ( _Grid2DSquareCell.getClass() == Grid2DSquareCellInt.class ) || ( ( ( int ) _Grid2DSquareCell.getNoDataValueDouble( false ) ) == Integer.MIN_VALUE ) ) {
                if (_Grid2DSquareCell.getClass() == Grid2DSquareCellInt.class) {
                    for (_ChunkRowIndex = _intZero; _ChunkRowIndex < this._NChunkRows; _ChunkRowIndex++) {
                        for (_ChunkColIndex = _intZero; _ChunkColIndex < this._NChunkCols; _ChunkColIndex++) {
                            do {
                                try {
                                    // Try to load chunk.
                                    a_ChunkID = new ChunkID(
                                            this._NChunkCols,
                                            _ChunkRowIndex,
                                            _ChunkColIndex);
                                    _Grid2DSquareCellIntChunk = _Grid2DSquareCellIntChunkFactory.createGrid2DSquareCellIntChunk(
                                            this,
                                            a_ChunkID);
                                    a_ChunkID = new ChunkID(
                                            this._NChunkCols,
                                            _ChunkRowIndex,
                                            _ChunkColIndex);
                                    this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                                            a_ChunkID,
                                            _Grid2DSquareCellIntChunk);
                                    row = ((long) _ChunkRowIndex * (long) this._ChunkNRows);
                                    for (chunkRow = 0; chunkRow < this._ChunkNRows; chunkRow++) {
                                        col = ((long) _ChunkColIndex * (long) this._ChunkNCols);
                                        for (chunkCol = 0; chunkCol < this._ChunkNCols; chunkCol++) {
                                            cellInt = _Grid2DSquareCell.getCellInt(
                                                    row,
                                                    col);
                                            initCell(
                                                    row,
                                                    col,
                                                    cellInt);
                                            col++;
                                        }
                                        row++;
                                    }
                                    isLoadedChunk = true;
                                } catch (OutOfMemoryError a_OutOfMemoryError) {
                                    if (handleOutOfMemoryError) {
                                        _Grids_Environment.clear_MemoryReserve();
                                        a_ChunkID = new ChunkID(
                                                this._NChunkCols,
                                                _ChunkRowIndex,
                                                _ChunkColIndex);
                                        if (_Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(this, a_ChunkID) < 1L) {
                                            throw a_OutOfMemoryError;
                                        }
                                        _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                                    } else {
                                        throw a_OutOfMemoryError;
                                    }
                                }
                            } while (!isLoadedChunk);
                            isLoadedChunk = false;
                            loadedChunkCount++;
                            cci1 = _ChunkColIndex;
                        }
                        println = null;
                        println = "Done chunkRow " + _ChunkRowIndex;
                        println = _Grids_Environment.println(println, println0, handleOutOfMemoryError);
                    }
                } else {
                    //( _Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class )
                    Grid2DSquareCellDouble _Grid2DSquareCellDouble = (Grid2DSquareCellDouble) _Grid2DSquareCell;
                    double _Grid2DSquareCellNoDataValue = _Grid2DSquareCellDouble._NoDataValue; //getNoDataValue( handleOutOfMemoryError );
                    double _Grid2DSquareCellValue;
                    for (_ChunkRowIndex = _intZero; _ChunkRowIndex < _NChunkRows; _ChunkRowIndex++) {
                        for (_ChunkColIndex = _intZero; _ChunkColIndex < _NChunkCols; _ChunkColIndex++) {
                            do {
                                try {
                                    // Try to load chunk.
                                    a_ChunkID = new ChunkID(
                                            this._NChunkCols,
                                            _ChunkRowIndex,
                                            _ChunkColIndex);
                                    _Grid2DSquareCellIntChunk = _Grid2DSquareCellIntChunkFactory.createGrid2DSquareCellIntChunk(
                                            this,
                                            a_ChunkID);
                                    a_ChunkID = new ChunkID(
                                            this._NChunkCols,
                                            _ChunkRowIndex,
                                            _ChunkColIndex);
                                    this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                                            a_ChunkID,
                                            _Grid2DSquareCellIntChunk);
                                    row = ((long) _ChunkRowIndex * (long) this._ChunkNRows);
                                    for (chunkRow = 0; chunkRow < this._ChunkNRows; chunkRow++) {
                                        col = ((long) _ChunkColIndex * (long) this._ChunkNCols);
                                        for (chunkCol = 0; chunkCol < this._ChunkNCols; chunkCol++) {
                                            //initCellFast( row, col, _Grid2DSquareCell.getCellInt( row, col ) );
                                            _Grid2DSquareCellValue = _Grid2DSquareCellDouble.getCell(
                                                    row,
                                                    col);
                                            if (_Grid2DSquareCellValue != _Grid2DSquareCellNoDataValue) {
                                                // TODO:
                                                // For robustness should test that
                                                // ( int ) _Grid2DSquareCellValue != Integer.MIN_VALUE
                                                // or indeed if it is out of range etc...
                                                cellInt = (int) _Grid2DSquareCellValue;
                                                initCell(
                                                        row,
                                                        col,
                                                        cellInt);
                                            } else {
                                                initCell(
                                                        row,
                                                        col,
                                                        Integer.MIN_VALUE);
                                            }
                                            col++;
                                        }
                                        row++;
                                    }
                                    isLoadedChunk = true;
                                } catch (OutOfMemoryError a_OutOfMemoryError) {
                                    if (handleOutOfMemoryError) {
                                        _Grids_Environment.clear_MemoryReserve();
                                        a_ChunkID = new ChunkID(
                                                this._NChunkCols,
                                                _ChunkRowIndex,
                                                _ChunkColIndex);
                                        if (_Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(this, a_ChunkID) < 1L) {
                                            throw a_OutOfMemoryError;
                                        }
                                        _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                                    } else {
                                        throw a_OutOfMemoryError;
                                    }
                                }
                            } while (!isLoadedChunk);
                            isLoadedChunk = false;
                            loadedChunkCount++;
                            cci1 = _ChunkColIndex;
                        }
                        println = null;
                        println = "Done chunkRow " + _ChunkRowIndex;
                        println = _Grids_Environment.println(println, println0, handleOutOfMemoryError);
                    }
                }
            } else {
                // _GridStatistics.getClass() == GridStatistics1.class
                // initCellFast(long,long,int) is to be used inplace of
                // initCell(long,long,int).
                if ((_Grid2DSquareCell.getClass() == Grid2DSquareCellInt.class)
                        || (((int) _Grid2DSquareCell.getNoDataValueBigDecimal(handleOutOfMemoryError).intValue()) == Integer.MIN_VALUE)) {
                    for (_ChunkRowIndex = _intZero; _ChunkRowIndex < _NChunkRows; _ChunkRowIndex++) {
                        for (_ChunkColIndex = _intZero; _ChunkColIndex < _NChunkCols; _ChunkColIndex++) {
                            do {
                                try {
                                    // Try to load chunk.
                                    a_ChunkID = new ChunkID(
                                            this._NChunkCols,
                                            _ChunkRowIndex,
                                            _ChunkColIndex);
                                    _Grid2DSquareCellIntChunk = _Grid2DSquareCellIntChunkFactory.createGrid2DSquareCellIntChunk(
                                            this,
                                            a_ChunkID);
                                    a_ChunkID = new ChunkID(
                                            this._NChunkCols,
                                            _ChunkRowIndex,
                                            _ChunkColIndex);
                                    this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                                            a_ChunkID,
                                            _Grid2DSquareCellIntChunk);
                                    row = ((long) _ChunkRowIndex * (long) this._ChunkNRows);
                                    for (chunkRow = _intZero; chunkRow < this._ChunkNRows; chunkRow++) {
                                        col = ((long) _ChunkColIndex * (long) this._ChunkNCols);
                                        for (chunkCol = _intZero; chunkCol < this._ChunkNCols; chunkCol++) {
                                            cellInt = _Grid2DSquareCell.getCellInt(
                                                    row,
                                                    col);
                                            initCellFast(
                                                    row,
                                                    col,
                                                    cellInt);
                                            col++;
                                        }
                                        row++;
                                    }
                                    isLoadedChunk = true;
                                } catch (OutOfMemoryError a_OutOfMemoryError) {
                                    if (handleOutOfMemoryError) {
                                        _Grids_Environment.clear_MemoryReserve();
                                        a_ChunkID = new ChunkID(
                                                this._NChunkCols,
                                                _ChunkRowIndex,
                                                _ChunkColIndex);
                                        if (_Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(this, a_ChunkID) < 1L) {
                                            throw a_OutOfMemoryError;
                                        }
                                        _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                                    } else {
                                        throw a_OutOfMemoryError;
                                    }
                                }
                            } while (!isLoadedChunk);
                            isLoadedChunk = false;
                            loadedChunkCount++;
                            cci1 = _ChunkColIndex;
                        }
                        println = null;
                        println = "Done chunkRow " + _ChunkRowIndex;
                        println = _Grids_Environment.println(println, println0, handleOutOfMemoryError);
                    }
                } else {
                    //( _Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class )
                    Grid2DSquareCellDouble _Grid2DSquareCellDouble = (Grid2DSquareCellDouble) _Grid2DSquareCell;
                    double _Grid2DSquareCellNoDataValue = _Grid2DSquareCellDouble._NoDataValue; //getNoDataValue( handleOutOfMemoryError );
                    double _Grid2DSquareCellValue;
                    for (_ChunkRowIndex = _intZero; _ChunkRowIndex < _NChunkRows; _ChunkRowIndex++) {
                        for (_ChunkColIndex = _intZero; _ChunkColIndex < _NChunkCols; _ChunkColIndex++) {
                            do {
                                try {
                                    // Try to load chunk.
                                    a_ChunkID = new ChunkID(
                                            this._NChunkCols,
                                            _ChunkRowIndex,
                                            _ChunkColIndex);
                                    _Grid2DSquareCellIntChunk = _Grid2DSquareCellIntChunkFactory.createGrid2DSquareCellIntChunk(
                                            this,
                                            a_ChunkID);
                                    a_ChunkID = new ChunkID(
                                            this._NChunkCols,
                                            _ChunkRowIndex,
                                            _ChunkColIndex);
                                    this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                                            a_ChunkID,
                                            _Grid2DSquareCellIntChunk);
                                    row = ((long) _ChunkRowIndex * (long) this._ChunkNRows);
                                    for (chunkRow = _intZero; chunkRow < this._ChunkNRows; chunkRow++) {
                                        col = ((long) _ChunkColIndex * (long) this._ChunkNCols);
                                        for (chunkCol = _intZero; chunkCol < this._ChunkNCols; chunkCol++) {
                                            //initCellFast( row, col, _Grid2DSquareCell.getCellInt( row, col ) );
                                            _Grid2DSquareCellValue = _Grid2DSquareCellDouble.getCell(
                                                    row,
                                                    col);
                                            if (_Grid2DSquareCellValue != _Grid2DSquareCellNoDataValue) {
                                                // TODO:
                                                // For robustness should test that
                                                // ( int ) _Grid2DSquareCellValue != Integer.MIN_VALUE
                                                // or indeed if it is out of range etc...
                                                cellInt = (int) _Grid2DSquareCellValue;
                                                initCellFast(
                                                        row,
                                                        col,
                                                        cellInt);
                                            } else {
                                                initCellFast(
                                                        row,
                                                        col,
                                                        Integer.MIN_VALUE);
                                            }
                                            col++;
                                        }
                                        row++;
                                    }
                                    isLoadedChunk = true;
                                } catch (OutOfMemoryError a_OutOfMemoryError) {
                                    if (handleOutOfMemoryError) {
                                        _Grids_Environment.clear_MemoryReserve();
                                        a_ChunkID = new ChunkID(
                                                this._NChunkCols,
                                                _ChunkRowIndex,
                                                _ChunkColIndex);
                                        if (_Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(this, a_ChunkID) < 1L) {
                                            throw a_OutOfMemoryError;
                                        }
                                        _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                                    } else {
                                        throw a_OutOfMemoryError;
                                    }
                                }
                            } while (!isLoadedChunk);
                            isLoadedChunk = false;
                            loadedChunkCount++;
                            cci1 = _ChunkColIndex;
                        }
                        println = null;
                        println = "Done chunkRow " + _ChunkRowIndex;
                        println = _Grids_Environment.println(println, println0, handleOutOfMemoryError);
                    }
                }
            }
            _Grids_Environment._AbstractGrid2DSquareCell_HashSet.add(this);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                a_Grids_Environment.clear_MemoryReserve();
                if (a_Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                a_Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                initGrid2DSquareCellInt(
                        _GridStatistics,
                        _Directory,
                        _Grid2DSquareCell,
                        _Grid2DSquareCellIntChunkFactory,
                        _ChunkNRows,
                        _ChunkNCols,
                        startRowIndex,
                        startColIndex,
                        endRowIndex,
                        endColIndex,
                        a_Grids_Environment,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Initialises this.
     *
     * @param _GridStatistics The AbstractGridStatistics to accompany the
     *   returned grid.
     * @param _Directory The File _Directory to be used for swapping.
     * @param gridFile Either a _Directory, or a formatted File with a specific
     *   extension containing the data and information about the
     *   Grid2DSquareCellDouble to be returned.
     * @param _Grid2DSquareCellIntChunkFactory The
     *   AbstractGrid2DSquareCellIntChunkFactory prefered to construct chunks
     *   of this.
     * @param _ChunkNRows
     * @param startRowIndex The _Grid2DSquareCell row index which is the bottom
     *   most row of this.
     * @param _ChunkNCols
     * @param startColIndex The _Grid2DSquareCell column index which is the left
     *   most column of this.
     * @param endRowIndex The _Grid2DSquareCell row index which is the top most
     *   row of this.
     * @param endColIndex The _Grid2DSquareCell column index which is the right
     *   most column of this.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @param a_Grids_Environment
     * @see Grid2DSquareCellInt#Grid2DSquareCellInt(
     *      AbstractGridStatistics,
     *      File,
     *      File,
     *      AbstractGrid2DSquareCellIntChunkFactory,
     *      int,
     *      int,
     *      long,
     *      long,
     *      long,
     *      long,
     *      HashSet,
     *      boolean )
     */
    protected final void initGrid2DSquareCellInt(
            AbstractGridStatistics _GridStatistics,
            File _Directory,
            File gridFile,
            AbstractGrid2DSquareCellIntChunkFactory _Grid2DSquareCellIntChunkFactory,
            int _ChunkNRows,
            int _ChunkNCols,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        try {
            _Grids_Environment = a_Grids_Environment;
            this._Directory = _Directory;
            String println0 = _Grids_Environment.initString(1000, handleOutOfMemoryError);
            String println = _Grids_Environment.initString(1000, handleOutOfMemoryError);
            if (gridFile.isDirectory()) {
                /* Initialise from
                 * new File(
                 *     gridFile,
                 *     "thisFile" );
                 */
                if (true) {
                    Grid2DSquareCellIntFactory _Grid2DSquareCellIntFactory = new Grid2DSquareCellIntFactory(
                            _Directory,
                            _ChunkNRows,
                            _ChunkNCols,
                            _Grid2DSquareCellIntChunkFactory,
                            _Grids_Environment,
                            handleOutOfMemoryError);
                    File thisFile = new File(
                            gridFile,
                            "thisFile");
                    ObjectInputStream ois = null;
                    try {
                        ois = new ObjectInputStream(
                                new BufferedInputStream(
                                new FileInputStream(thisFile)));
                        Grid2DSquareCellInt gridFileGrid2DSquareCellInt =
                                (Grid2DSquareCellInt) _Grid2DSquareCellIntFactory.create(
                                _Directory,
                                thisFile,
                                ois);
                        Grid2DSquareCellInt gridFileGrid2DSquareCell =
                                _Grid2DSquareCellIntFactory.create(
                                _Directory,
                                gridFileGrid2DSquareCellInt,
                                startRowIndex,
                                startColIndex,
                                endRowIndex,
                                endColIndex,
                                a_Grids_Environment,
                                handleOutOfMemoryError);
                        initGrid2DSquareCellInt(
                                gridFileGrid2DSquareCell,
                                false);
                    } catch (IOException ioe0) {
                        System.err.println(ioe0.getMessage());
                        //ioe0.printStackTrace();
                        //throw ioe0;
                    }
                }
                initGrid2DSquareCellChunks(gridFile);
            } else {
                this._ChunkNRows = _ChunkNRows;
                this._ChunkNCols = _ChunkNCols;
                this._NRows = endRowIndex - startRowIndex + 1L;
                this._NCols = endColIndex - startColIndex + 1L;
                this._Grids_Environment = a_Grids_Environment;
                this._Name = _Directory.getName();
                init_NChunkRows();
                init_NChunkCols();
                long nChunks = getNChunks();
                this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap = new HashMap((int) nChunks);
                this._Dimensions = new BigDecimal[5];
                this._GridStatistics = _GridStatistics;
                // Set the reference to this in the Grid Statistics
                this._GridStatistics.init(this);
                //this._GridStatistics._Grid2DSquareCell = this;
                String filename = gridFile.getName();
                int loadedChunkCount = 0;
                boolean isLoadedChunk = false;
                int value = Integer.MIN_VALUE;
                boolean _Grid2DSquareCellsSwapped = false;
                if (filename.endsWith("asc") || filename.endsWith("txt")) {
                    ESRIAsciiGridImporter _ESRIAsciigridImporter =
                            new ESRIAsciiGridImporter(
                            gridFile, _Grids_Environment);
                    Object[] header = _ESRIAsciigridImporter.readHeaderObject();
                    //long inputNcols = ( Long ) header[ 0 ];
                    //long inputNrows = ( Long ) header[ 1 ];
                    this._Dimensions[ 0] = (BigDecimal) header[ 4];
                    this._Dimensions[ 1] = ((BigDecimal) header[ 2]).add(_Dimensions[ 0].multiply(new BigDecimal(startColIndex)));
                    this._Dimensions[ 2] = ((BigDecimal) header[ 3]).add(_Dimensions[ 0].multiply(new BigDecimal(startRowIndex)));
                    this._Dimensions[ 3] = this._Dimensions[ 1].add(new BigDecimal(Long.toString(this._NCols)).multiply(this._Dimensions[ 0]));
                    this._Dimensions[ 4] = this._Dimensions[ 2].add(new BigDecimal(Long.toString(this._NRows)).multiply(this._Dimensions[ 0]));
                    double gridFileNoDataValue = (Double) header[ 5];
                    int _ChunkRowIndex = Integer.MIN_VALUE;
                    int _ChunkColIndex = Integer.MIN_VALUE;
                    int cachedAndClearedChunkCount = 0;
                    int cri0 = 0;
                    int cci0 = 0;
                    int cri1 = 0;
                    int cci1 = 0;
                    ChunkID a_ChunkID = new ChunkID();
                    boolean initialisedChunksToClear = true;
                    long row = Long.MIN_VALUE;
                    long col = Long.MIN_VALUE;
                    boolean isInitCellDone = false;
                    int reportN;
            reportN = (int) (endRowIndex - startRowIndex) / 10;
                    //ChunkID nextChunkToSwap = new ChunkID(
                    //        this._NChunkCols,
                    //        0,
                    //        0 );
                    AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellIntChunk = _Grid2DSquareCellIntChunkFactory.createGrid2DSquareCellIntChunk(
                            this,
                            a_ChunkID);
                    // Initialise Chunks
                    for (_ChunkRowIndex = 0; _ChunkRowIndex < _NChunkRows; _ChunkRowIndex++) {
                        //for ( _ChunkRowIndex = _NChunkRows - 1; _ChunkRowIndex >= 0; _ChunkRowIndex -- ) {
                        for (_ChunkColIndex = 0; _ChunkColIndex < _NChunkCols; _ChunkColIndex++) {
                            do {
                                try {
                                    a_ChunkID = new ChunkID(
                                            this._NChunkCols,
                                            _ChunkRowIndex,
                                            _ChunkColIndex);
                                    _Grid2DSquareCellIntChunk = _Grid2DSquareCellIntChunkFactory.createGrid2DSquareCellIntChunk(
                                            this,
                                            a_ChunkID);
                                    a_ChunkID = new ChunkID(
                                            this._NChunkCols,
                                            _ChunkRowIndex,
                                            _ChunkColIndex);
                                    this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                                            a_ChunkID,
                                            _Grid2DSquareCellIntChunk);
                                    isLoadedChunk = true;
                                } catch (OutOfMemoryError a_OutOfMemoryError) {
                                    if (handleOutOfMemoryError) {
                                        _Grids_Environment.clear_MemoryReserve();
                                        a_ChunkID = new ChunkID(
                                                this._NChunkCols,
                                                _ChunkRowIndex,
                                                _ChunkColIndex);
                                        if (_Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(this, a_ChunkID) < 1L) {
                                            throw a_OutOfMemoryError;
                                        }
                                        _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                                    } else {
                                        throw a_OutOfMemoryError;
                                    }
                                }
                            } while (!isLoadedChunk);
                            isLoadedChunk = false;
                            loadedChunkCount++;
                            cci1 = _ChunkColIndex;
                        }
                        println = null;
                        println = "Done chunkRow " + _ChunkRowIndex;
                        println = _Grids_Environment.println(println, println0, handleOutOfMemoryError);
                    }
                    println = null;
                    println = "First stage of initialisation complete. Reading data into initialised Chunks";
                    println = _Grids_Environment.println(println, println0, handleOutOfMemoryError);

                    // Read Data into Chunks
                    if ((int) gridFileNoDataValue == Integer.MIN_VALUE) {
                        if (_GridStatistics.getClass() == GridStatistics0.class) {
                            for (row = (this._NRows - 1); row > -1; row--) {
                                for (col = 0; col < this._NCols; col++) {
                                    value = _ESRIAsciigridImporter.readInt();
                                    do {
                                        try {
                                            initCell(
                                                    row,
                                                    col,
                                                    value);
                                            isInitCellDone = true;
                                        } catch (OutOfMemoryError a_OutOfMemoryError) {
                                            if (handleOutOfMemoryError) {
                                                _Grids_Environment.clear_MemoryReserve();
                                                a_ChunkID = new ChunkID(
                                                        this._NChunkCols,
                                                        _ChunkRowIndex,
                                                        _ChunkColIndex);
                                                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(this, a_ChunkID) < 1L) {
                                                    throw a_OutOfMemoryError;
                                                }
                                                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                                            } else {
                                                throw a_OutOfMemoryError;
                                            }
                                        }
                                    } while (!isInitCellDone);
                                    isInitCellDone = false;
                                }
                                if (row % reportN == 0) {
                                    println = null;
                                    println = "Done row " + row;
                                    println = _Grids_Environment.println(println, println0, handleOutOfMemoryError);
                                }
                            }
                        } else {
                            // _GridStatistics.getClass() == GridStatistics1.class
                            for (row = (this._NRows - 1); row > -1; row--) {
                                for (col = 0; col < this._NCols; col++) {
                                    value = _ESRIAsciigridImporter.readInt();
                                    do {
                                        try {
                                            initCellFast(
                                                    row,
                                                    col,
                                                    value);
                                            isInitCellDone = true;
                                        } catch (OutOfMemoryError a_OutOfMemoryError) {
                                            if (handleOutOfMemoryError) {
                                                _Grids_Environment.clear_MemoryReserve();
                                                a_ChunkID = new ChunkID(
                                                        this._NChunkCols,
                                                        _ChunkRowIndex,
                                                        _ChunkColIndex);
                                                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(this, a_ChunkID) < 1L) {
                                                    throw a_OutOfMemoryError;
                                                }
                                                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                                            } else {
                                                throw a_OutOfMemoryError;
                                            }
                                        }
                                    } while (!isInitCellDone);
                                    isInitCellDone = false;
                                }
                                if (row % reportN == 0) {
                                    println = null;
                                    println = "Done row " + row;
                                    println = _Grids_Environment.println(println, println0, handleOutOfMemoryError);
                                }
                            }
                        }
                    } else {
                        int _NoDataValue = getNoDataValue(handleOutOfMemoryError);
                        if (_GridStatistics.getClass() == GridStatistics0.class) {
                            for (row = (this._NRows - 1); row > -1; row--) {
                                for (col = 0; col < this._NCols; col++) {
                                    value = _ESRIAsciigridImporter.readInt();
                                    do {
                                        try {
                                            if (value != gridFileNoDataValue) {
                                                initCell(
                                                        row,
                                                        col,
                                                        value);
                                            } else {
                                                initCell(
                                                        row,
                                                        col,
                                                        _NoDataValue);
                                            }
                                            isInitCellDone = true;
                                        } catch (OutOfMemoryError a_OutOfMemoryError) {
                                            if (handleOutOfMemoryError) {
                                                _Grids_Environment.clear_MemoryReserve();
                                                a_ChunkID = new ChunkID(
                                                        this._NChunkCols,
                                                        _ChunkRowIndex,
                                                        _ChunkColIndex);
                                                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(this, a_ChunkID) < 1L) {
                                                    throw a_OutOfMemoryError;
                                                }
                                                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                                            } else {
                                                throw a_OutOfMemoryError;
                                            }
                                        }
                                    } while (!isInitCellDone);
                                    isInitCellDone = false;
                                }
                                if (row % reportN == 0) {
                                    println = null;
                                    println = "Done row " + row;
                                    println = _Grids_Environment.println(println, println0, handleOutOfMemoryError);
                                }
                            }
                        } else {
                            // _GridStatistics.getClass() == GridStatistics1.class
                            for (row = (this._NRows - 1); row > -1; row--) {
                                for (col = 0; col < this._NCols; col++) {
                                    value = _ESRIAsciigridImporter.readInt();
                                    do {
                                        try {
                                            if (value != gridFileNoDataValue) {
                                                initCellFast(
                                                        row,
                                                        col,
                                                        value);
                                            } else {
                                                initCellFast(
                                                        row,
                                                        col,
                                                        _NoDataValue);
                                            }
                                            isInitCellDone = true;
                                        } catch (OutOfMemoryError a_OutOfMemoryError) {
                                            if (handleOutOfMemoryError) {
                                                _Grids_Environment.clear_MemoryReserve();
                                                a_ChunkID = new ChunkID(
                                                        this._NChunkCols,
                                                        _ChunkRowIndex,
                                                        _ChunkColIndex);
                                                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunksExcept_Account(this, a_ChunkID) < 1L) {
                                                    throw a_OutOfMemoryError;
                                                }
                                                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                                            } else {
                                                throw a_OutOfMemoryError;
                                            }
                                        }
                                    } while (!isInitCellDone);
                                    isInitCellDone = false;
                                }
                                if (row % reportN == 0) {
                                    println = null;
                                    println = "Done row " + row;
                                    println = _Grids_Environment.println(println, println0, handleOutOfMemoryError);
                                }
                            }
                        }
                    }
                }
            }
            _Grids_Environment._AbstractGrid2DSquareCell_HashSet.add(this);
            this._GridStatistics = _GridStatistics;
            // Set the reference to this in the Grid Statistics
            //this._GridStatistics._Grid2DSquareCell = this;
            this._GridStatistics.init(this);
            //this._GridStatistics._Grid2DSquareCell = this;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                a_Grids_Environment.clear_MemoryReserve();
                if (a_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this) < 1L) {
                    throw a_OutOfMemoryError;
                }
                a_Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                initGrid2DSquareCellInt(
                        _GridStatistics,
                        _Directory,
                        gridFile,
                        _Grid2DSquareCellIntChunkFactory,
                        _ChunkNRows,
                        _ChunkNCols,
                        startRowIndex,
                        startColIndex,
                        endRowIndex,
                        endColIndex,
                        a_Grids_Environment,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _ChunkRowIndex
     * @param _ChunkColIndex
     * @return _Grid2DSquareCellIntChunks.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public AbstractGrid2DSquareCellIntChunk getGrid2DSquareCellIntChunk(
            int _ChunkRowIndex,
            int _ChunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            AbstractGrid2DSquareCellIntChunk result = getGrid2DSquareCellIntChunk(
                    _ChunkRowIndex,
                    _ChunkColIndex);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        _NChunkCols,
                        _ChunkRowIndex,
                        _ChunkColIndex);
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getGrid2DSquareCellIntChunk(
                        _ChunkRowIndex,
                        _ChunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _ChunkRowIndex
     * @param _ChunkColIndex
     * @return _Grid2DSquareCellIntChunks.
     */
    protected AbstractGrid2DSquareCellIntChunk getGrid2DSquareCellIntChunk(
            int _ChunkRowIndex,
            int _ChunkColIndex) {
        ChunkID a_ChunkID = new ChunkID(
                this._NChunkCols,
                _ChunkRowIndex,
                _ChunkColIndex);
        return getGrid2DSquareCellIntChunk(a_ChunkID);
    }

    /**
     * @return _Grid2DSquareCellIntChunksAbstract for the given ChunkID
     * @param a_ChunkID
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public AbstractGrid2DSquareCellIntChunk getGrid2DSquareCellIntChunk(
            ChunkID a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            AbstractGrid2DSquareCellIntChunk result = getGrid2DSquareCellIntChunk(a_ChunkID);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getGrid2DSquareCellIntChunk(
                        a_ChunkID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return _Grid2DSquareCellIntChunksAbstract for the given ChunkID
     * @param a_ChunkID
     */
    protected AbstractGrid2DSquareCellIntChunk getGrid2DSquareCellIntChunk(
            ChunkID a_ChunkID) {
        boolean isInGrid = isInGrid(
                a_ChunkID);
        if (isInGrid) {
            if (!this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.containsKey(a_ChunkID)) {
                loadIntoCacheChunk(
                        a_ChunkID);
            }
            return (AbstractGrid2DSquareCellIntChunk) this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.get(a_ChunkID);
        }
        return null;
    }

    /**
     * If newValue and oldValue are the same then statistics won't change. A test might be
     * appropriate in set cell so that this method is not called. Also want to keep track if
     * underlying data has changed for getting statistics of GridStatistics1 type.
     *
     * @param newValue The value replacing oldValue.
     * @param oldValue The value being replaced.
     * @param _NoDataValue this._NoDataValue
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    private void upDateGridStatistics(
            int newValue,
            int oldValue,
            int _NoDataValue,
            boolean handleOutOfMemoryError) {
        try {
            upDateGridStatistics(
                    newValue,
                    oldValue,
                    _NoDataValue);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                upDateGridStatistics(
                        newValue,
                        oldValue,
                        _NoDataValue,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * If newValue and oldValue are the same then statistics won't change. A test might be
     * appropriate in set cell so that this method is not called. Also want to keep track if
     * underlying data has changed for getting statistics of GridStatistics1 type.
     *
     * @param newValue The value replacing oldValue.
     * @param oldValue The value being replaced.
     * @param _NoDataValue this._NoDataValue
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    private void upDateGridStatistics(
            int newValue,
            int oldValue,
            int _NoDataValue) {
        if (this._GridStatistics instanceof GridStatistics0) {
            if (oldValue != _NoDataValue) {
                BigDecimal oldValueBigDecimal = new BigDecimal(oldValue);
                this._GridStatistics.nonNoDataValueCountBigInteger = this._GridStatistics.nonNoDataValueCountBigInteger.subtract(BigInteger.ONE);
                this._GridStatistics.sumBigDecimal = this._GridStatistics.sumBigDecimal.subtract(oldValueBigDecimal);
                if (oldValueBigDecimal.compareTo(this._GridStatistics.minBigDecimal) == 0) {
                    this._GridStatistics.minCountBigInteger = this._GridStatistics.minCountBigInteger.subtract(BigInteger.ONE);
                }
                if (oldValueBigDecimal.compareTo(this._GridStatistics.maxBigDecimal) == 0) {
                    this._GridStatistics.maxCountBigInteger = this._GridStatistics.maxCountBigInteger.subtract(BigInteger.ONE);
                }
            }
            if (newValue != _NoDataValue) {
                BigDecimal newValueBigDecimal = new BigDecimal(newValue);
                this._GridStatistics.nonNoDataValueCountBigInteger = this._GridStatistics.nonNoDataValueCountBigInteger.add(BigInteger.ONE);
                this._GridStatistics.sumBigDecimal = this._GridStatistics.sumBigDecimal.add(newValueBigDecimal);
                if (newValueBigDecimal.compareTo(this._GridStatistics.minBigDecimal) == -1) {
                    this._GridStatistics.minBigDecimal = newValueBigDecimal;
                    this._GridStatistics.minCountBigInteger = BigInteger.ONE;
                } else {
                    if (newValueBigDecimal.compareTo(this._GridStatistics.minBigDecimal) == 0) {
                        this._GridStatistics.minCountBigInteger = this._GridStatistics.minCountBigInteger.add(BigInteger.ONE);
                    } else {
                        if (this._GridStatistics.minCountBigInteger.compareTo(BigInteger.ONE) == -1) {
                            // The GridStatistics need recalculating
                            this._GridStatistics.update();
                        }
                    }
                }
                if (newValueBigDecimal.compareTo(this._GridStatistics.maxBigDecimal) == 1) {
                    this._GridStatistics.maxBigDecimal = newValueBigDecimal;
                    this._GridStatistics.maxCountBigInteger = BigInteger.ONE;
                } else {
                    if (newValueBigDecimal.compareTo(this._GridStatistics.maxBigDecimal) == 0) {
                        this._GridStatistics.maxCountBigInteger = this._GridStatistics.maxCountBigInteger.add(BigInteger.ONE);
                    } else {
                        if (this._GridStatistics.maxCountBigInteger.compareTo(BigInteger.ONE) == -1) {
                            // The GridStatistics need recalculating
                            this._GridStatistics.update();
                        }
                    }
                }
            }
        } else {
            if (newValue != oldValue) {
                ((GridStatistics1) this._GridStatistics).setIsUpToDate(false);
            }
        }
    }

    /**
     * @return this._NoDataValue.
     *
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public final int getNoDataValue(
            boolean handleOutOfMemoryError) {
        try {
            int result = getNoDataValue();
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                return getNoDataValue(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return this _NoDataValue Integer.MIN_VALUE.
     */
    protected final int getNoDataValue() {
        return Integer.MIN_VALUE;
    }

    /**
     * @return the this._NoDataValue converted to a BigDecimal.
     *
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public BigDecimal getNoDataValueBigDecimal(
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = BigDecimal.valueOf(getNoDataValue());
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                return getNoDataValueBigDecimal(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _CellRowIndex
     * @param _CellColIndex
     * @return Value at _CellRowIndex, _CellColIndex else returns _NoDataValue.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public int getCell(
            long _CellRowIndex,
            long _CellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCell(
                    _CellRowIndex,
                    _CellColIndex);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getCell(
                        _CellRowIndex,
                        _CellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _CellRowIndex
     * @param _CellColIndex
     * @return Value at _CellRowIndex, _CellColIndex else returns _NoDataValue.
     */
    protected int getCell(
            long _CellRowIndex,
            long _CellColIndex) {
        int _NoDataValue = getNoDataValue();
        boolean isInGrid = isInGrid(
                _CellRowIndex,
                _CellColIndex);
        if (isInGrid) {
            long _ChunkNrowsLong = this._ChunkNRows;
            long _ChunkNcolsLong = this._ChunkNCols;
            int _ChunkRowIndex = getChunkRowIndex(_CellRowIndex);
            int _ChunkColIndex = getChunkColIndex(_CellColIndex);
            long _ChunkRowIndexLong = _ChunkRowIndex;
            long _ChunkColIndexLong = _ChunkColIndex;
            int _ChunkCellRowIndex = (int) (_CellRowIndex - (_ChunkRowIndexLong * _ChunkNrowsLong));
            int _ChunkCellColIndex = (int) (_CellColIndex - (_ChunkColIndexLong * _ChunkNcolsLong));
            AbstractGrid2DSquareCellChunk _Grid2DSquareCellChunk = getGrid2DSquareCellChunk(
                    _ChunkRowIndex,
                    _ChunkColIndex);
            if (_Grid2DSquareCellChunk.getClass() == Grid2DSquareCellIntChunk64CellMap.class) {
                return ((Grid2DSquareCellIntChunk64CellMap) _Grid2DSquareCellChunk).getCell(
                        _ChunkCellRowIndex,
                        _ChunkCellColIndex,
                        _NoDataValue);
            } else {
                if (_Grid2DSquareCellChunk.getClass() == Grid2DSquareCellIntChunkArray.class) {
                    return ((Grid2DSquareCellIntChunkArray) _Grid2DSquareCellChunk).getCell(
                            _ChunkCellRowIndex,
                            _ChunkCellColIndex,
                            _NoDataValue);
                } else {
                    if (_Grid2DSquareCellChunk.getClass() == Grid2DSquareCellIntChunkJAI.class) {
                        return ((Grid2DSquareCellIntChunkJAI) _Grid2DSquareCellChunk).getCell(
                                _ChunkCellRowIndex,
                                _ChunkCellColIndex,
                                _NoDataValue);
                    } else {
                        return ((Grid2DSquareCellIntChunkMap) _Grid2DSquareCellChunk).getCell(
                                _ChunkCellRowIndex,
                                _ChunkCellColIndex,
                                _NoDataValue);
                    }
                }
            }
        }
        return _NoDataValue;
    }

    /**
     * @param _Grid2DSquareCellChunk
     * @return Value at position given by chunk row index _ChunkRowIndex,
     * chunk column index _ChunkColIndex, chunk cell row index chunkCellRowIndex,
     * chunk cell column index chunkCellColIndex.
     * @param _ChunkRowIndex The chunk row index of the cell thats value is
     *   returned.
     * @param _ChunkColIndex The chunk column index of the cell thats value is
     *   returned.
     * @param chunkCellRowIndex The chunk cell row index of the cell thats
     *   value is returned.
     * @param chunkCellColIndex The chunk cell column index of the cell thats
     *   value is returned.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public int getCell(
            AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellChunk,
            int _ChunkRowIndex,
            int _ChunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCell(
                    _Grid2DSquareCellChunk,
                    _ChunkRowIndex,
                    _ChunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        _ChunkRowIndex,
                        _ChunkColIndex);
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getCell(
                        _Grid2DSquareCellChunk,
                        _ChunkRowIndex,
                        _ChunkColIndex,
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _Grid2DSquareCellChunk
     * @return Value at position given by chunk row index _ChunkRowIndex,
     * chunk column index _ChunkColIndex, chunk cell row index chunkCellRowIndex,
     * chunk cell column index chunkCellColIndex.
     * @param _ChunkRowIndex The chunk row index of the cell thats value is
     *   returned.
     * @param _ChunkColIndex The chunk column index of the cell thats value is
     *   returned.
     * @param chunkCellRowIndex The chunk cell row index of the cell thats
     *   value is returned.
     * @param chunkCellColIndex The chunk cell column index of the cell thats
     *   value is returned.
     */
    protected int getCell(
            AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellChunk,
            int _ChunkRowIndex,
            int _ChunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        int _NoDataValue = getNoDataValue();
        if (_Grid2DSquareCellChunk.getClass() == Grid2DSquareCellIntChunk64CellMap.class) {
            return ((Grid2DSquareCellIntChunk64CellMap) _Grid2DSquareCellChunk).getCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    _NoDataValue);
        } else {
            if (_Grid2DSquareCellChunk.getClass() == Grid2DSquareCellIntChunkArray.class) {
                return ((Grid2DSquareCellIntChunkArray) _Grid2DSquareCellChunk).getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        _NoDataValue);
            } else {
                if (_Grid2DSquareCellChunk.getClass() == Grid2DSquareCellIntChunkJAI.class) {
                    return ((Grid2DSquareCellIntChunkJAI) _Grid2DSquareCellChunk).getCell(
                            chunkCellRowIndex,
                            chunkCellColIndex,
                            _NoDataValue);
                } else {
                    if (_Grid2DSquareCellChunk.getClass() == Grid2DSquareCellIntChunkMap.class) {
                        return ((Grid2DSquareCellIntChunkMap) _Grid2DSquareCellChunk).getCell(
                                chunkCellRowIndex,
                                chunkCellColIndex,
                                _NoDataValue);
                    } else {
                        return _NoDataValue;
                    }
                }
            }
        }
    }

    /**
     * For returning the value of the cell containing point given by
     * x-coordinate x, y-coordinate y as a int.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public final int getCell(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCell(
                    x,
                    y);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getCell(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the value of the cell containing point given by
     * x-coordinate x, y-coordinate y as a int.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return 
     */
    protected final int getCell(
            double x,
            double y) {
        return getCell(
                getCellRowIndex(y),
                getCellColIndex(x));
    }

    /**
     * @return int value of the cell with CellID _CellID.
     *
     * @param _CellID the CellID of the cell.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public final int getCell(
            CellID _CellID,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCell(
                    _CellID._CellRowIndex,
                    _CellID._CellColIndex);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(_CellID._CellRowIndex),
                        getChunkColIndex(_CellID._CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getCell(
                        _CellID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the value at x-coordinate x, y-coordinate y and setting it
     * to newValue.
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @param newValue
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public final int setCell(
            double x,
            double y,
            int newValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = setCell(
                    x,
                    y,
                    newValue);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return setCell(
                        x,
                        y,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the value at x-coordinate x, y-coordinate y and setting it
     * to newValue.
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @param newValue .
     * @return 
     */
    protected final int setCell(
            double x,
            double y,
            int newValue) {
        return setCell(
                getCellRowIndex(x),
                getCellColIndex(y),
                newValue);
    }

    /**
     * For returning the value of the cell with cell ID cellID and setting it
     * to newValue.
     * @param cellID the CellID of the cell.
     * @param newValue
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public final int setCell(
            CellID cellID,
            int newValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = setCell(
                    cellID._CellRowIndex,
                    cellID._CellColIndex,
                    newValue);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(cellID._CellRowIndex),
                        getChunkColIndex(cellID._CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return setCell(
                        cellID,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the value at _CellRowIndex, _CellColIndex as a double and setting it to
     * ( int ) newValue.
     * @param _CellRowIndex
     * @param _CellColIndex
     * @param newValue
     * @return 
     */
    @Override
    protected double setCell(
            long _CellRowIndex,
            long _CellColIndex,
            double newValue) {
        return setCell(
                _CellRowIndex,
                _CellColIndex,
                (int) newValue);
    }

    /**
     * For returning the value at _CellRowIndex, _CellColIndex and setting it to
     * newValue.
     * @param _CellRowIndex
     * @param _CellColIndex
     * @param newValue
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public int setCell(
            long _CellRowIndex,
            long _CellColIndex,
            int newValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = setCell(
                    _CellRowIndex,
                    _CellColIndex,
                    newValue);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return setCell(
                        _CellRowIndex,
                        _CellColIndex,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the value at _CellRowIndex, _CellColIndex and setting it to
     * newValue.
     * @param _CellRowIndex
     * @param _CellColIndex
     * @param newValue
     * @return 
     */
    protected int setCell(
            long _CellRowIndex,
            long _CellColIndex,
            int newValue) {
        int _ChunkRowIndex = getChunkRowIndex(_CellRowIndex);
        int _ChunkColIndex = getChunkColIndex(_CellColIndex);
        int chunkCellRowIndex = getChunkCellRowIndex(_CellRowIndex);
        int chunkCellColIndex = getChunkCellColIndex(_CellColIndex);
        //if ( inGrid( _CellRowIndex, _CellColIndex ) ) {
        AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellIntChunk = getGrid2DSquareCellIntChunk(
                _ChunkRowIndex,
                _ChunkColIndex);
        return setCell(
                _Grid2DSquareCellIntChunk,
                _ChunkRowIndex,
                _ChunkColIndex,
                chunkCellRowIndex,
                chunkCellColIndex,
                newValue);
        //} else {
        //    return getNoDataValue();
        //}
    }

    /**
     * For returning the value of the cell in chunk given by _ChunkRowIndex and
     * _ChunkColIndex and cell in the chunk given by chunkCellColIndex and
     * chunkCellRowIndex and setting it to newValue.
     * @param _ChunkRowIndex
     * @param _ChunkColIndex
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @param newValue
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public int setCell(
            int _ChunkRowIndex,
            int _ChunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int newValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = setCell(
                    _ChunkRowIndex,
                    _ChunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    newValue);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        _ChunkRowIndex,
                        _ChunkColIndex);
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return setCell(
                        _ChunkRowIndex,
                        _ChunkColIndex,
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the value of the cell in chunk given by _ChunkRowIndex and
     * _ChunkColIndex and cell in the chunk given by chunkCellColIndex and
     * chunkCellRowIndex and setting it to newValue.
     * @param _ChunkRowIndex
     * @param _ChunkColIndex
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @param newValue
     * @return 
     */
    protected int setCell(
            int _ChunkRowIndex,
            int _ChunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int newValue) {
        //if ( inGrid( _ChunkRowIndex, _ChunkColIndex, chunkCellRowIndex, chunkCellColIndex ) ) {
        AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellIntChunk = getGrid2DSquareCellIntChunk(
                _ChunkRowIndex,
                _ChunkColIndex);
        return setCell(
                _Grid2DSquareCellIntChunk,
                _ChunkRowIndex,
                _ChunkColIndex,
                chunkCellRowIndex,
                chunkCellColIndex,
                newValue);
        //} else {
        //    return getNoDataValue();
        //}
    }

    /**
     * @return Value at _CellRowIndex, _CellColIndex and sets it to newValue.
     * @param _Grid2DSquareCellChunk
     * @param _ChunkRowIndex
     * @param _ChunkColIndex
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @param newValue
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public int setCell(
            AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellChunk,
            int _ChunkRowIndex,
            int _ChunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int newValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = setCell(
                    _Grid2DSquareCellChunk,
                    _ChunkRowIndex,
                    _ChunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    newValue);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        _ChunkRowIndex,
                        _ChunkColIndex);
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return setCell(
                        _Grid2DSquareCellChunk,
                        _ChunkRowIndex,
                        _ChunkColIndex,
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return Value at _CellRowIndex, _CellColIndex and sets it to newValue.
     * @param _Grid2DSquareCellChunk
     * @param _ChunkRowIndex
     * @param _ChunkColIndex
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @param newValue
     */
    protected int setCell(
            AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellChunk,
            int _ChunkRowIndex,
            int _ChunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int newValue) {
        int _NoDataValue = getNoDataValue();
        int result = _NoDataValue;
        if (_Grid2DSquareCellChunk.getClass() == Grid2DSquareCellIntChunk64CellMap.class) {
            result = ((Grid2DSquareCellIntChunk64CellMap) _Grid2DSquareCellChunk).setCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    newValue,
                    result);
        } else {
            if (_Grid2DSquareCellChunk.getClass() == Grid2DSquareCellIntChunkArray.class) {
                result = ((Grid2DSquareCellIntChunkArray) _Grid2DSquareCellChunk).setCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        newValue,
                        result);
            } else {
                if (_Grid2DSquareCellChunk.getClass() == Grid2DSquareCellIntChunkJAI.class) {
                    result = ((Grid2DSquareCellIntChunkJAI) _Grid2DSquareCellChunk).setCell(
                            chunkCellRowIndex,
                            chunkCellColIndex,
                            newValue,
                            result);
                } else {
                    if (_Grid2DSquareCellChunk.getClass() == Grid2DSquareCellIntChunkMap.class) {
                        result = ((Grid2DSquareCellIntChunkMap) _Grid2DSquareCellChunk).setCell(
                                chunkCellRowIndex,
                                chunkCellColIndex,
                                newValue,
                                result);
                    } else {
                        System.err.println(
                                "Error in "
                                + getClass().getName() + ".setCell(Grid2DSquareCellIntChunkAbstract,int,int,int,int,double) \n"
                                + "unable to handle Grid2DSquareCellChunkAbstract " + _Grid2DSquareCellChunk.toString());
                        return result;
                    }
                }
            }
        }
        // Update Statistics
        upDateGridStatistics(
                newValue,
                result,
                _NoDataValue);
        return result;
    }

    /**
     * Initilises the value at _CellRowIndex, _CellColIndex.
     * @param _CellRowIndex
     * @param _CellColIndex
     * @param valueToInitialise
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    protected void initCell(
            long _CellRowIndex,
            long _CellColIndex,
            int valueToInitialise,
            boolean handleOutOfMemoryError) {
        try {
            initCell(
                    _CellRowIndex,
                    _CellColIndex,
                    valueToInitialise);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                initCell(
                        _CellRowIndex,
                        _CellColIndex,
                        valueToInitialise,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Initilises the value at _CellRowIndex, _CellColIndex.
     * @param _CellRowIndex
     * @param _CellColIndex
     * @param valueToInitialise
     */
    protected void initCell(
            long _CellRowIndex,
            long _CellColIndex,
            int valueToInitialise) {
        boolean isInGrid = isInGrid(
                _CellRowIndex,
                _CellColIndex);
        if (isInGrid) {
            int _ChunkRowIndex = getChunkRowIndex(_CellRowIndex);
            int _ChunkColIndex = getChunkColIndex(_CellColIndex);
            ChunkID a_ChunkID = new ChunkID(
                    this._NChunkCols,
                    _ChunkRowIndex,
                    _ChunkColIndex);
            //boolean containsKey = this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.containsKey( a_ChunkID ); // Debugging code
            AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellIntChunk = getGrid2DSquareCellIntChunk(a_ChunkID);
            _Grid2DSquareCellIntChunk.initCell(
                    (int) (_CellRowIndex - ((long) _ChunkRowIndex * (long) _ChunkNRows)),
                    (int) (_CellColIndex - ((long) _ChunkColIndex * (long) _ChunkNCols)),
                    valueToInitialise);
            // Update Statistics
            int _NoDataValue = getNoDataValue(
                    Grids_Environment.HandleOutOfMemoryErrorFalse);
            if (valueToInitialise != _NoDataValue) {
                BigDecimal cellBigDecimal = new BigDecimal(valueToInitialise);
                this._GridStatistics.nonNoDataValueCountBigInteger = this._GridStatistics.nonNoDataValueCountBigInteger.add(BigInteger.ONE);
                this._GridStatistics.sumBigDecimal = this._GridStatistics.sumBigDecimal.add(cellBigDecimal);
                if (cellBigDecimal.compareTo(this._GridStatistics.minBigDecimal) == -1) {
                    this._GridStatistics.minCountBigInteger = BigInteger.ONE;
                    this._GridStatistics.minBigDecimal = cellBigDecimal;
                } else {
                    if (cellBigDecimal.compareTo(this._GridStatistics.minBigDecimal) == 0) {
                        this._GridStatistics.minCountBigInteger = this._GridStatistics.minCountBigInteger.add(BigInteger.ONE);
                    }
                }
                if (cellBigDecimal.compareTo(this._GridStatistics.maxBigDecimal) == 1) {
                    this._GridStatistics.maxCountBigInteger = BigInteger.ONE;
                    this._GridStatistics.maxBigDecimal = cellBigDecimal;
                } else {
                    if (cellBigDecimal.compareTo(this._GridStatistics.maxBigDecimal) == 0) {
                        this._GridStatistics.maxCountBigInteger = this._GridStatistics.maxCountBigInteger.add(BigInteger.ONE);
                    }
                }
            }
        }
    }

    /**
     * Initilises the value at _CellRowIndex, _CellColIndex and does nothing about
     * this._GridStatistics.
     * @param _CellRowIndex
     * @param _CellColIndex
     * @param valueToInitialise
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    protected void initCellFast(
            long _CellRowIndex,
            long _CellColIndex,
            int valueToInitialise,
            boolean handleOutOfMemoryError) {
        try {
            initCellFast(
                    _CellRowIndex,
                    _CellColIndex,
                    valueToInitialise);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                initCellFast(
                        _CellRowIndex,
                        _CellColIndex,
                        valueToInitialise,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Initilises the value at _CellRowIndex, _CellColIndex and does nothing about
     * this._GridStatistics
     * @param a_CellRowIndex
     * @param a_CellColIndex
     * @param valueToInitialise
     */
    protected void initCellFast(
            long a_CellRowIndex,
            long a_CellColIndex,
            int valueToInitialise) {
        boolean isInGrid = isInGrid(
                a_CellRowIndex,
                a_CellColIndex);
        if (isInGrid) {
            int chunkRowIndex = getChunkRowIndex(a_CellRowIndex);
            int chunkColIndex = getChunkColIndex(a_CellColIndex);
            ChunkID a_ChunkID = new ChunkID(
                    this._NChunkCols,
                    chunkRowIndex,
                    chunkColIndex);
            AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellIntChunk = getGrid2DSquareCellIntChunk(a_ChunkID);
            _Grid2DSquareCellIntChunk.initCell(
                    (int) (a_CellRowIndex - ((long) chunkRowIndex * (long) _ChunkNRows)),
                    (int) (a_CellColIndex - ((long) chunkColIndex * (long) _ChunkNCols)),
                    valueToInitialise);
        }
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x the x-coordinate of the circle centre from which cell values are
     *   returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     *   returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public int[] getCells(
            double x,
            double y,
            double distance,
            boolean handleOutOfMemoryError) {
        try {
            int[] result = getCells(
                    x,
                    y,
                    distance);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                long _CellRowIndex = getCellRowIndex(y);
                long _CellColIndex = getCellColIndex(x);
                HashSet a_ChunkIDs = getChunkIDs(
                        distance,
                        x,
                        y,
                        _CellRowIndex,
                        _CellColIndex);
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkIDs) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkIDs,
                        handleOutOfMemoryError);
                return getCells(
                        x,
                        y,
                        distance,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x the x-coordinate of the circle centre from which cell values are
     *   returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     *   returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    protected int[] getCells(
            double x,
            double y,
            double distance) {
        return getCells(
                x,
                y,
                getCellRowIndex(y),
                getCellColIndex(x),
                distance);
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at centroid of cell given by cell row
     * index _CellRowIndex, cell column index _CellColIndex, and radius distance.
     * @param _CellRowIndex the row index for the cell thats centroid is the
     *   circle centre from which cell values are returned.
     * @param _CellColIndex
     * @param distance the radius of the circle for which intersected cell
     *   values are returned.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public int[] getCells(
            long _CellRowIndex,
            long _CellColIndex,
            double distance,
            boolean handleOutOfMemoryError) {
        try {
            int[] result = getCells(
                    _CellRowIndex,
                    _CellColIndex,
                    distance);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                double x = getCellXDouble(_CellColIndex);
                double y = getCellYDouble(_CellRowIndex);
                HashSet a_ChunkIDs = getChunkIDs(
                        distance,
                        x,
                        y,
                        _CellRowIndex,
                        _CellColIndex);
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkIDs) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkIDs,
                        handleOutOfMemoryError);
                return getCells(
                        _CellRowIndex,
                        _CellColIndex,
                        distance,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at centroid of cell given by cell row
     * index _CellRowIndex, cell column index _CellColIndex, and radius distance.
     * @param _CellRowIndex the row index for the cell thats centroid is the
     * @param _CellColIndex
     *   circle centre from which cell values are returned.
     * @param distance the radius of the circle for which intersected cell
     *   values are returned.
     */
    protected int[] getCells(
            long _CellRowIndex,
            long _CellColIndex,
            double distance) {
        return getCells(
                getCellXDouble(_CellColIndex),
                getCellYDouble(_CellRowIndex),
                _CellRowIndex,
                _CellColIndex,
                distance);
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x The x-coordinate of the circle centre from which cell values are
     *   returned.
     * @param y The y-coordinate of the circle centre from which cell values are
     *   returned.
     * @param _CellRowIndex The row index at y.
     * @param _CellColIndex
     * @param distance The radius of the circle for which intersected cell
     *   values are returned.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public int[] getCells(
            double x,
            double y,
            long _CellRowIndex,
            long _CellColIndex,
            double distance,
            boolean handleOutOfMemoryError) {
        try {
            return getCells(
                    x,
                    y,
                    _CellRowIndex,
                    _CellColIndex,
                    distance);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                HashSet a_ChunkIDs = getChunkIDs(
                        distance,
                        x,
                        y,
                        _CellRowIndex,
                        _CellColIndex);
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkIDs) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkIDs,
                        handleOutOfMemoryError);
                return getCells(
                        x,
                        y,
                        _CellRowIndex,
                        _CellColIndex,
                        distance,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x The x-coordinate of the circle centre from which cell values are
     *   returned.
     * @param y The y-coordinate of the circle centre from which cell values are
     *   returned.
     * @param _CellRowIndex The row index at y.
     * @param _CellColIndex
     * @param distance The radius of the circle for which intersected cell
     *   values are returned.
     */
    protected int[] getCells(
            double x,
            double y,
            long _CellRowIndex,
            long _CellColIndex,
            double distance) {
        int[] cells;
        int cellDistance = (int) Math.ceil(distance / _Dimensions[ 0].doubleValue());
        cells = new int[((2 * cellDistance) + 1) * ((2 * cellDistance) + 1)];
        long row = Long.MIN_VALUE;
        long col = Long.MIN_VALUE;
        double thisX = Double.MIN_VALUE;
        double thisY = Double.MIN_VALUE;
        int count = 0;
        for (row = _CellRowIndex - cellDistance; row <= _CellRowIndex + cellDistance; row++) {
            thisY = getCellYDouble(_CellRowIndex);
            for (col = _CellColIndex - cellDistance; col <= _CellColIndex + cellDistance; col++) {
                thisX = getCellXDouble(_CellColIndex);
                if (Utilities.distance(x, y, thisX, thisY) <= distance) {
                    cells[count] = getCell(
                            row,
                            col);
                    count++;
                }
            }
        }
        // Trim cells
        System.arraycopy(
                cells,
                0,
                cells,
                0,
                count);
        return cells;
    }

    /**
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y as a double.
     * @param x The x-coordinate of the point
     * @param y The y-coordinate of the point
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public double getNearestValueDouble(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            double result = getNearestValueDouble(
                    x,
                    y);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getNearestValueDouble(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y as a double.
     * @param x The x-coordinate of the point
     * @param y The y-coordinate of the point
     */
    protected double getNearestValueDouble(
            double x,
            double y) {
        int _NoDataValue = getNoDataValue();
        double result = getCell(
                x,
                y);
        if (result == _NoDataValue) {
            result = getNearestValueDouble(
                    x,
                    y,
                    getCellRowIndex(y),
                    getCellColIndex(x),
                    _NoDataValue);
        }
        return result;
    }

    /**
     * @param _CellColIndex
     * @return the average of the nearest data values to position given by row
     * index rowIndex, column index colIndex as a double.
     * @param _CellRowIndex The row index from which average of the nearest data
     *   values is returned.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public double getNearestValueDouble(
            long _CellRowIndex,
            long _CellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getNearestValueDouble(
                    _CellRowIndex,
                    _CellColIndex);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getNearestValueDouble(
                        _CellRowIndex,
                        _CellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _CellColIndex
     * @return the average of the nearest data values to position given by row
     * index rowIndex, column index colIndex as a double.
     * @param _CellRowIndex The row index from which average of the nearest data
     *   values is returned.
     */
    protected double getNearestValueDouble(
            long _CellRowIndex,
            long _CellColIndex) {
        int _NoDataValue = getNoDataValue();
        double result = getCell(
                _CellRowIndex,
                _CellColIndex);
        if (result == _NoDataValue) {
            result = getNearestValueDouble(
                    getCellXDouble(_CellColIndex),
                    getCellYDouble(_CellRowIndex),
                    _CellRowIndex,
                    _CellColIndex,
                    _NoDataValue);
        }
        return result;
    }

    /**
     * @param _CellColIndex
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex as a double.
     * @param x The x-coordinate of the point
     * @param y The y-coordinate of the point
     * @param _CellRowIndex The row index from which average of the nearest data
     *   values is returned.
     * @param _NoDataValue
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public double getNearestValueDouble(
            double x,
            double y,
            long _CellRowIndex,
            long _CellColIndex,
            int _NoDataValue,
            boolean handleOutOfMemoryError) {
        try {
            return getNearestValueDouble(
                    x,
                    y,
                    _CellRowIndex,
                    _CellColIndex,
                    _NoDataValue);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getNearestValueDouble(
                        x,
                        y,
                        _CellRowIndex,
                        _CellColIndex,
                        _NoDataValue,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _CellColIndex
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex as a double.
     * @param x The x-coordinate of the point
     * @param y The y-coordinate of the point
     * @param _CellRowIndex The row index from which average of the nearest data
     *   values is returned.
     * @param _NoDataValue
     */
    protected double getNearestValueDouble(
            double x,
            double y,
            long _CellRowIndex,
            long _CellColIndex,
            int _NoDataValue) {
        CellID nearestCellID = getNearestCellID(
                x,
                y,
                _CellRowIndex,
                _CellColIndex);
        double nearestValue = getCell(
                _CellRowIndex,
                _CellColIndex);
        if (nearestValue == _NoDataValue) {
            // Find a value Seeking outwards from nearestCellID
            // Initialise visitedSet1
            HashSet visitedSet = new HashSet();
            HashSet visitedSet1 = new HashSet();
            visitedSet.add(nearestCellID);
            visitedSet1.add(nearestCellID);
            // Initialise toVisitSet1
            HashSet toVisitSet1 = new HashSet();
            long row = Long.MIN_VALUE;
            long col = Long.MIN_VALUE;
            CellID cellID0 = new CellID();
            boolean isInGrid = false;
            for (row = -1; row < 2; row++) {
                for (col = -1; col < 2; col++) {
                    if (!(row == 0 && col == 0)) {
                        isInGrid = isInGrid(
                                _CellRowIndex + row,
                                _CellColIndex + col);
                        if (isInGrid) {
                            cellID0 = new CellID(
                                    _NCols,
                                    _CellRowIndex + row,
                                    _CellColIndex + col);
                            toVisitSet1.add(cellID0);
                        }
                    }
                }
            }
            // Seek
            boolean foundValue = false;
            double value;
            HashSet values = new HashSet();
            HashSet visitedSet2;
            HashSet toVisitSet2;
            Iterator iterator;
            CellID cellID1;
            while (!foundValue) {
                visitedSet2 = new HashSet();
                toVisitSet2 = new HashSet();
                iterator = toVisitSet1.iterator();
                while (iterator.hasNext()) {
                    cellID0 = (CellID) iterator.next();
                    visitedSet2.add(cellID0);
                    value = getCell(cellID0,
                            Grids_Environment.HandleOutOfMemoryErrorTrue);
                    if (value != _NoDataValue) {
                        foundValue = true;
                        values.add(cellID0);
                    } else {
                        // Add neighbours to toVisitSet2
                        for (row = -1; row < 2; row++) {
                            for (col = -1; col < 2; col++) {
                                if (!(row == 0 && col == 0)) {
                                    isInGrid = isInGrid(
                                            cellID0._CellRowIndex + row,
                                            cellID0._CellColIndex + col);
                                    if (isInGrid) {
                                        cellID1 = new CellID(
                                                _NCols,
                                                cellID0._CellRowIndex + row,
                                                cellID0._CellColIndex + col);
                                        toVisitSet2.add(cellID1);
                                    }
                                }
                            }
                        }
                    }
                }
                toVisitSet2.removeAll(visitedSet1);
                toVisitSet2.removeAll(visitedSet2);
                visitedSet.addAll(visitedSet2);
                visitedSet1 = visitedSet2;
                toVisitSet1 = toVisitSet2;
            }
            double distance;
            double minDistance = Integer.MAX_VALUE;
            // Go through values and find the closest
            HashSet closest = new HashSet();
            iterator = values.iterator();
            while (iterator.hasNext()) {
                cellID0 = (CellID) iterator.next();
                distance = Utilities.distance(
                        x,
                        y,
                        getCellXDouble(cellID0),
                        getCellYDouble(cellID0));
                if (distance < minDistance) {
                    closest.clear();
                    closest.add(cellID0);
                } else {
                    if (distance == minDistance) {
                        closest.add(cellID0);
                    }
                }
                minDistance = Math.min(
                        minDistance,
                        distance);
            }
            // Get cellIDs that are within distance of discovered value
            CellID[] cellIDs = getCellIDs(
                    x,
                    y,
                    minDistance);
            // Go through values and find the closest
            for (int i = 0; i < cellIDs.length; i++) {
                if (!visitedSet.contains(cellIDs[i])) {
                    if (getCell(
                            cellIDs[i],
                            Grids_Environment.HandleOutOfMemoryErrorTrue) !=
                            _NoDataValue) {
                        distance = Utilities.distance(
                                x,
                                y,
                                getCellXDouble(cellIDs[i]),
                                getCellYDouble(cellIDs[i]));
                        if (distance < minDistance) {
                            closest.clear();
                            closest.add(cellIDs[i]);
                        } else {
                            if (distance == minDistance) {
                                closest.add(cellIDs[i]);
                            }
                        }
                        minDistance = Math.min(
                                minDistance,
                                distance);
                    }
                }
            }
            // Go through the closest and calculate the average.
            value = 0;
            iterator = closest.iterator();
            while (iterator.hasNext()) {
                cellID0 = (CellID) iterator.next();
                value += getCell(
                        cellID0,
                        Grids_Environment.HandleOutOfMemoryErrorTrue);
            }
            nearestValue = value / (double) closest.size();
        }
        return nearestValue;
    }

    /**
     * @return a CellID[] - The CellIDs of the nearest cells with data values
     * to point given by x-coordinate x, y-coordinate y.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public CellID[] getNearestValuesCellIDs(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            CellID[] result = getNearestValuesCellIDs(
                    x,
                    y);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getNearestValuesCellIDs(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a CellID[] - The CellIDs of the nearest cells with data values
     * to point given by x-coordinate x, y-coordinate y.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */
    protected CellID[] getNearestValuesCellIDs(
            double x,
            double y) {
        int _NoDataValue = getNoDataValue();
        int value = getCell(
                x,
                y);
        if (value == _NoDataValue) {
            return getNearestValuesCellIDs(
                    x,
                    y,
                    getCellRowIndex(y),
                    getCellColIndex(x),
                    _NoDataValue);
        }
        CellID[] cellIDs = new CellID[1];
        cellIDs[ 0] = getCellID(
                x,
                y);
        return cellIDs;
    }

    /**
     * @param _CellColIndex
     * @return a CellID[] - The CellIDs of the nearest cells with data values
     * to position given by row index rowIndex, column index colIndex.
     * @param _CellRowIndex The row index from which the cell IDs of the nearest
     *   cells with data values are returned.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public CellID[] getNearestValuesCellIDs(
            long _CellRowIndex,
            long _CellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            CellID[] result = getNearestValuesCellIDs(
                    _CellRowIndex,
                    _CellColIndex);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getNearestValuesCellIDs(
                        _CellRowIndex,
                        _CellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a CellID[] - The CellIDs of the nearest cells with data values
     * to position given by row index rowIndex, column index colIndex.
     * @param _CellRowIndex The row index from which the cell IDs of the nearest
     *   cells with data values are returned.
     * @param _CellColIndex
     */
    protected CellID[] getNearestValuesCellIDs(
            long _CellRowIndex,
            long _CellColIndex) {
        int _NoDataValue = getNoDataValue();
        int value = getCell(
                _CellRowIndex,
                _CellColIndex);
        if (value == _NoDataValue) {
            return getNearestValuesCellIDs(
                    getCellXDouble(_CellColIndex),
                    getCellYDouble(_CellRowIndex),
                    _CellRowIndex,
                    _CellColIndex,
                    _NoDataValue);
        }
        CellID[] cellIDs = new CellID[1];
        cellIDs[ 0] = getCellID(
                _CellRowIndex,
                _CellColIndex);
        return cellIDs;
    }

    /**
     * @return a CellID[] - The CellIDs of the nearest cells with data values
     * nearest to point with position given by: x-coordinate x, y-coordinate y;
     * and, cell row index _CellRowIndex, cell column index _CellColIndex.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param _CellRowIndex The row index from which the cell IDs of the nearest
     *   cells with data values are returned.
     * @param _CellColIndex
     * @param _NoDataValue The no data value of the this.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public CellID[] getNearestValuesCellIDs(
            double x,
            double y,
            long _CellRowIndex,
            long _CellColIndex,
            int _NoDataValue,
            boolean handleOutOfMemoryError) {
        try {
            CellID[] result = getNearestValuesCellIDs(
                    x,
                    y,
                    _CellRowIndex,
                    _CellColIndex,
                    _NoDataValue);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getNearestValuesCellIDs(
                        x,
                        y,
                        _CellRowIndex,
                        _CellColIndex,
                        _NoDataValue,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a CellID[] - The CellIDs of the nearest cells with data values
     * nearest to point with position given by: x-coordinate x, y-coordinate y;
     * and, cell row index _CellRowIndex, cell column index _CellColIndex.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param _CellRowIndex The row index from which the cell IDs of the nearest
     *   cells with data values are returned.
     * @param _CellColIndex
     * @param _NoDataValue The no data value of the this.
     */
    protected CellID[] getNearestValuesCellIDs(
            double x,
            double y,
            long _CellRowIndex,
            long _CellColIndex,
            int _NoDataValue) {
        CellID[] nearestCellIDs = new CellID[1];
        nearestCellIDs[0] = getNearestCellID(
                x,
                y,
                _CellRowIndex,
                _CellColIndex);
        int nearestCellValue = getCell(
                _CellRowIndex,
                _CellColIndex);
        if (nearestCellValue == _NoDataValue) {
            // Find a value Seeking outwards from nearestCellID
            // Initialise visitedSet1
            HashSet visitedSet = new HashSet();
            HashSet visitedSet1 = new HashSet();
            visitedSet.add(nearestCellIDs[0]);
            visitedSet1.add(nearestCellIDs[0]);
            // Initialise toVisitSet1
            HashSet toVisitSet1 = new HashSet();
            long row;
            long col;
            boolean isInGrid;
            CellID cellID;
            for (row = -1; row < 2; row++) {
                for (col = -1; col < 2; col++) {
                    if (!(row == 0 && col == 0)) {
                        isInGrid = isInGrid(
                                _CellRowIndex + row,
                                _CellColIndex + col);
                        if (isInGrid) {
                            cellID = getCellID(
                                    _CellRowIndex + row,
                                    _CellColIndex + col);
                            toVisitSet1.add(cellID);
                        }
                    }
                }
            }
            // Seek
            boolean foundValue = false;
            double value;
            HashSet values = new HashSet();
            HashSet visitedSet2;
            HashSet toVisitSet2;
            Iterator iterator;
            while (!foundValue) {
                visitedSet2 = new HashSet();
                toVisitSet2 = new HashSet();
                iterator = toVisitSet1.iterator();
                while (iterator.hasNext()) {
                    cellID = (CellID) iterator.next();
                    visitedSet2.add(cellID);
                    value = getCell(
                            cellID,
                            Grids_Environment.HandleOutOfMemoryErrorTrue);
                    if (value != _NoDataValue) {
                        foundValue = true;
                        values.add(cellID);
                    } else {
                        // Add neighbours to toVisitSet2
                        for (row = -1; row < 2; row++) {
                            for (col = -1; col < 2; col++) {
                                if (!(row == 0 && col == 0)) {
                                    isInGrid = isInGrid(
                                            cellID._CellRowIndex + row,
                                            cellID._CellColIndex + col);
                                    if (isInGrid) {
                                        cellID = getCellID(
                                                cellID._CellRowIndex + row,
                                                cellID._CellColIndex + col);
                                        toVisitSet2.add(cellID);
                                    }
                                }
                            }
                        }
                    }
                }
                toVisitSet2.removeAll(visitedSet1);
                toVisitSet2.removeAll(visitedSet2);
                visitedSet.addAll(visitedSet2);
                visitedSet1 = visitedSet2;
                toVisitSet1 = toVisitSet2;
            }
            double distance;
            double minDistance = Double.MAX_VALUE;
            // Go through values and find the closest
            HashSet closest = new HashSet();
            iterator = values.iterator();
            while (iterator.hasNext()) {
                cellID = (CellID) iterator.next();
                distance = Utilities.distance(
                        x,
                        y,
                        getCellXDouble(cellID),
                        getCellYDouble(cellID));
                if (distance < minDistance) {
                    closest.clear();
                    closest.add(cellID);
                } else {
                    if (distance == minDistance) {
                        closest.add(cellID);
                    }
                }
                minDistance = Math.min(
                        minDistance,
                        distance);
            }
            // Get cellIDs that are within distance of discovered value
            CellID[] cellIDs = getCellIDs(
                    x,
                    y,
                    minDistance);
            // Go through values and find the closest
            for (int i = 0; i < cellIDs.length; i++) {
                if (!visitedSet.contains(cellIDs[i])) {
                    if (getCell(cellIDs[i], 
                            Grids_Environment.HandleOutOfMemoryErrorTrue) !=
                            _NoDataValue) {
                        distance = Utilities.distance(
                                x,
                                y,
                                getCellXDouble(cellIDs[i]),
                                getCellYDouble(cellIDs[i]));
                        if (distance < minDistance) {
                            closest.clear();
                            closest.add(cellIDs[i]);
                        } else {
                            if (distance == minDistance) {
                                closest.add(cellIDs[i]);
                            }
                        }
                        minDistance = Math.min(
                                minDistance,
                                distance);
                    }
                }
            }
            // Go through the closest and put into an array
            nearestCellIDs = new CellID[closest.size()];
            iterator = closest.iterator();
            int counter = 0;
            while (iterator.hasNext()) {
                nearestCellIDs[counter] = (CellID) iterator.next();
                counter++;
            }
        }
        return nearestCellIDs;
    }

    /**
     * @return the distance to the nearest data value from point given by
     * x-coordinate x, y-coordinate y.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public double getNearestValueDoubleDistance(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            double result = getNearestValueDoubleDistance(
                    x,
                    y);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getNearestValueDoubleDistance(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return the distance to the nearest data value from point given by
     * x-coordinate x, y-coordinate y.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    protected double getNearestValueDoubleDistance(
            double x,
            double y) {
        int _NoDataValue = getNoDataValue();
        double result = getCell(
                x,
                y);
        if (result == _NoDataValue) {
            result = getNearestValueDoubleDistance(
                    x,
                    y,
                    getCellRowIndex(y),
                    getCellColIndex(x),
                    _NoDataValue);
        }
        return result;
    }

    /**
     * @return the distance to the nearest data value from position given by
     * row index rowIndex, column index colIndex.
     * @param _CellRowIndex The cell row index of the cell from which the
     *   distance nearest to the nearest cell value is returned.
     * @param _CellColIndex
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public double getNearestValueDoubleDistance(
            long _CellRowIndex,
            long _CellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getNearestValueDoubleDistance(
                    _CellRowIndex,
                    _CellColIndex);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getNearestValueDoubleDistance(
                        _CellRowIndex,
                        _CellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return the distance to the nearest data value from position given by
     * row index rowIndex, column index colIndex.
     * @param _CellRowIndex The cell row index of the cell from which the
     *   distance nearest to the nearest cell value is returned.
     * @param _CellColIndex
     */
    protected double getNearestValueDoubleDistance(
            long _CellRowIndex,
            long _CellColIndex) {
        int _NoDataValue = getNoDataValue();
        double result = getCell(
                _CellRowIndex,
                _CellColIndex);
        if (result == _NoDataValue) {
            result = getNearestValueDoubleDistance(
                    getCellXDouble(_CellColIndex),
                    getCellYDouble(_CellRowIndex),
                    _CellRowIndex,
                    _CellColIndex,
                    _NoDataValue);
        }
        return result;
    }

    /**
     * @return the distance to the nearest data value from point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param _CellRowIndex The cell row index of the cell from which the
     *   distance nearest to the nearest cell value is returned.
     * @param _CellColIndex The cell column index of the cell from which the
     *   distance nearest to the nearest cell value is returned.
     * @param _NoDataValue
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public double getNearestValueDoubleDistance(
            double x,
            double y,
            long _CellRowIndex,
            long _CellColIndex,
            int _NoDataValue,
            boolean handleOutOfMemoryError) {
        try {
            return getNearestValueDoubleDistance(
                    x,
                    y,
                    _CellRowIndex,
                    _CellColIndex,
                    _NoDataValue);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getNearestValueDoubleDistance(
                        x,
                        y,
                        _CellRowIndex,
                        _CellColIndex,
                        _NoDataValue,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _NoDataValue
     * @return the distance to the nearest data value from point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param _CellRowIndex The cell row index of the cell from which the
     *   distance nearest to the nearest cell value is returned.
     * @param _CellColIndex The cell column index of the cell from which the
     *   distance nearest to the nearest cell value is returned.
     */
    protected double getNearestValueDoubleDistance(
            double x,
            double y,
            long _CellRowIndex,
            long _CellColIndex,
            int _NoDataValue) {
        //            CellID nearestCellID = getNearestCellID(
        //                    x,
        //                    y,
        //                    _CellRowIndex,
        //                    _CellColIndexe );
        double result = getCell(
                _CellRowIndex,
                _CellColIndex);
        if (result == _NoDataValue) {
            // Initialisation
            long long0 = Long.MIN_VALUE;
            long long1 = Long.MIN_VALUE;
            long longMinus1 = -1;
            long longTwo = 2;
            long longZero = 0;
            boolean boolean0 = false;
            boolean boolean1 = false;
            boolean boolean2 = false;
            double double0 = Double.NEGATIVE_INFINITY;
            double double1 = Double.NEGATIVE_INFINITY;
            CellID nearestCellID = getNearestCellID(
                    x,
                    y,
                    _CellRowIndex,
                    _CellColIndex);
            HashSet visitedSet = new HashSet();
            HashSet visitedSet1 = new HashSet();
            visitedSet.add(nearestCellID);
            visitedSet1.add(nearestCellID);
            HashSet toVisitSet1 = new HashSet();
            long row = Long.MIN_VALUE;
            long col = Long.MIN_VALUE;
            boolean isInGrid = false;
            CellID cellID = new CellID();
            boolean foundValue = false;
            double value = Double.NEGATIVE_INFINITY;
            HashSet values = new HashSet();
            HashSet visitedSet2 = new HashSet();
            HashSet toVisitSet2 = new HashSet();
            Iterator iterator = toVisitSet1.iterator();
            double distance = Double.NEGATIVE_INFINITY;
            double minDistance = Double.MAX_VALUE;
            HashSet closest = new HashSet();
            // Find a value Seeking outwards from nearestCellID
            // Initialise toVisitSet1
            for (row = longMinus1; row < longTwo; row++) {
                for (col = longMinus1; col < longTwo; col++) {
                    boolean0 = (row == longZero);
                    boolean1 = (col == longZero);
                    boolean2 = !(boolean0 && boolean1);
                    if (boolean2) {
                        long0 = _CellRowIndex + row;
                        long1 = _CellColIndex + col;
                        isInGrid = isInGrid(
                                long0,
                                long1,
                                Grids_Environment.HandleOutOfMemoryErrorTrue);
                        if (isInGrid) {
                            cellID = getCellID(
                                    long0,
                                    long1,
                                    Grids_Environment.HandleOutOfMemoryErrorTrue);
                            toVisitSet1.add(cellID);
                        }
                    }
                }
            }
            // Seek
            while (!foundValue) {
                visitedSet2 = new HashSet();
                toVisitSet2 = new HashSet();
                iterator = toVisitSet1.iterator();
                while (iterator.hasNext()) {
                    cellID = (CellID) iterator.next();
                    visitedSet2.add(cellID);
                    value = getCell(
                            cellID,
                            Grids_Environment.HandleOutOfMemoryErrorTrue);
                    if (value != _NoDataValue) {
                        foundValue = true;
                        values.add(cellID);
                    } else {
                        // Add neighbours to toVisitSet2
                        for (row = longMinus1; row < longTwo; row++) {
                            for (col = longMinus1; col < longTwo; col++) {
                                boolean0 = (row == longZero);
                                boolean1 = (col == longZero);
                                boolean2 = !(boolean0 && boolean1);
                                if (boolean2) {
                                    long0 = cellID._CellRowIndex + row;
                                    long1 = cellID._CellColIndex + col;
                                    isInGrid = isInGrid(
                                            long0,
                                            long1,
                                            Grids_Environment.HandleOutOfMemoryErrorTrue);
                                    if (isInGrid) {
                                        cellID = getCellID(
                                                long0,
                                                long1,
                                                Grids_Environment.HandleOutOfMemoryErrorTrue);
                                        toVisitSet2.add(cellID);
                                    }
                                }
                            }
                        }
                    }
                }
                toVisitSet2.removeAll(visitedSet1);
                toVisitSet2.removeAll(visitedSet2);
                visitedSet.addAll(visitedSet2);
                visitedSet1 = visitedSet2;
                toVisitSet1 = toVisitSet2;
            }
            // Go through values and find the closest
            iterator = values.iterator();
            while (iterator.hasNext()) {
                cellID = (CellID) iterator.next();
                double0 = getCellXDouble(
                        cellID,
                        Grids_Environment.HandleOutOfMemoryErrorTrue);
                double1 = getCellYDouble(
                        cellID,
                        Grids_Environment.HandleOutOfMemoryErrorTrue);
                distance = Utilities.distance(
                        x,
                        y,
                        double0,
                        double1);
                if (distance < minDistance) {
                    closest.clear();
                    closest.add(cellID);
                } else {
                    if (distance == minDistance) {
                        closest.add(cellID);
                    }
                }
                minDistance = Math.min(
                        minDistance,
                        distance);
            }
            // Get cellIDs that are within distance of discovered value
            CellID[] cellIDs = getCellIDs(
                    x,
                    y,
                    minDistance);
            // Go through values and find the closest
            for (int i = 0; i < cellIDs.length; i++) {
                if (!visitedSet.contains(cellIDs[i])) {
                    if (getCell(cellIDs[i], 
                            Grids_Environment.HandleOutOfMemoryErrorTrue) !=
                            _NoDataValue) {
                        distance = Utilities.distance(
                                x,
                                y,
                                getCellXDouble(cellIDs[i]),
                                getCellYDouble(cellIDs[i]));
                        minDistance = Math.min(
                                minDistance,
                                distance);
                    }
                }
            }
            result = minDistance;
        } else {
            result = 0.0d;
        }
        return result;
    }

    /**
     * @return current value of cell containing the point given by x-coordinate
     * x, y-coordinate y, and adds valueToAdd to that cell.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param valueToAdd the value to be added to the cell containing the point
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public final int addToCell(
            double x,
            double y,
            int valueToAdd,
            boolean handleOutOfMemoryError) {
        try {
            int result = addToCell(
                    x,
                    y,
                    valueToAdd);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return addToCell(
                        x,
                        y,
                        valueToAdd,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return current value of cell containing the point given by x-coordinate
     * x, y-coordinate y, and adds valueToAdd to that cell.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param valueToAdd the value to be added to the cell containing the point
     */
    protected final int addToCell(
            double x,
            double y,
            int valueToAdd) {
        return addToCell(
                getCellRowIndex(y),
                getCellColIndex(x),
                valueToAdd);
    }

    /**
     * @return Value of the cell with cell ID cellID and adds valueToAdd to
     * that cell.
     * @param cellID the CellID of the cell.
     * @param valueToAdd the value to be added to the cell containing the point
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public final int addToCell(
            CellID cellID,
            int valueToAdd,
            boolean handleOutOfMemoryError) {
        try {
            int result = addToCell(
                    cellID,
                    valueToAdd);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(cellID._CellRowIndex),
                        getChunkColIndex(cellID._CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return addToCell(
                        cellID,
                        valueToAdd,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return Value of the cell with cell ID cellID and adds valueToAdd to
     * that cell.
     * @param cellID the CellID of the cell.
     * @param valueToAdd the value to be added to the cell containing the point
     */
    protected final int addToCell(
            CellID cellID,
            int valueToAdd) {
        return addToCell(
                cellID._CellRowIndex,
                cellID._CellColIndex,
                valueToAdd);
    }

    /**
     * @return current value of cell with row index rowIndex and column index
     * colIndex and adds valueToAdd to that cell.
     * @param _CellRowIndex the row index of the cell.
     * @param _CellColIndex
     * @param valueToAdd the value to be added to the cell.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * NB1. If cell is not contained in this then then returns _NoDataValue.
     * NB2. Adding to _NoDataValue is done as if adding to a cell with value of
     *   0.
     * TODO: Check Arithmetic
     */
    public int addToCell(
            long _CellRowIndex,
            long _CellColIndex,
            int valueToAdd,
            boolean handleOutOfMemoryError) {
        try {
            int result = addToCell(
                    _CellRowIndex,
                    _CellColIndex,
                    valueToAdd);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                ChunkID a_ChunkID = new ChunkID(
                        this._NChunkCols,
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return addToCell(
                        _CellRowIndex,
                        _CellColIndex,
                        valueToAdd,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return current value of cell with row index rowIndex and column index
     * colIndex and adds valueToAdd to that cell.
     * @param cellRowIndex the row index of the cell.
     * @param cellColIndex the col index of the cell.
     * @param valueToAdd the value to be added to the cell.
     * NB1. If cell is not contained in this then then returns _NoDataValue.
     * NB2. Adding to _NoDataValue is done as if adding to a cell with value of
     *   0.
     * TODO: Check Arithmetic
     */
    protected int addToCell(
            long cellRowIndex,
            long cellColIndex,
            int valueToAdd) {
        int _NoDataValue = getNoDataValue(
                Grids_Environment.HandleOutOfMemoryErrorFalse);
        if (isInGrid(
                cellRowIndex,
                cellColIndex,
                Grids_Environment.HandleOutOfMemoryErrorFalse)) {
            int currentValue = getCell(
                    cellRowIndex,
                    cellColIndex);
            if (currentValue != _NoDataValue) {
                if (valueToAdd != _NoDataValue) {
                    return setCell(
                            cellRowIndex,
                            cellColIndex,
                            currentValue + valueToAdd);
                }
            } else {
                if (valueToAdd != _NoDataValue) {
                    return setCell(
                            cellRowIndex,
                            cellColIndex,
                            valueToAdd);
                }
            }
        }
        return _NoDataValue;
    }

    /**
     * @return an iterator over the cell value in this.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public Iterator iterator(
            boolean handleOutOfMemoryError) {
        try {
            Iterator result = new Grid2DSquareCellIntIterator(this);
            _Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                return iterator(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }
}
