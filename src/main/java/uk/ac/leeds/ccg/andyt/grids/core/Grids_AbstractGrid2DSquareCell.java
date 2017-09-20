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

import java.awt.geom.Point2D;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_FileCreator;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_UnsignedLongPowersOf2;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

/**
 * Contains Grids_2D_ID_long and Grids_2D_ID_int classes, referencing and general geometry
 methods. It also controls what methods extended classes must implement acting
 * like an interface.
 *
 * The basic geometries are ordered in set numbers of rows and columns and are
 * arranged sequentially as their base two-dimensional orthogonal coordinate
 * axes. The sequential arrangement goes along the x-axis row by row from the
 * y-axis, then up the y-axis taking each row in turn.
 *
 * TODO: Handling for NumberFormatExceptions and ArithmeticExceptions in
 * calculations
 */
public abstract class Grids_AbstractGrid2DSquareCell
        extends Grids_Object
        implements Serializable {

//    /**
//     * A version number for confidence in reloading serialised instances.
//     */
//    private static final long serialVersionUID = 1L;
    /**
     * Local _Directory used for caching. TODO If this were not transient upon
     * reloading, it would be possible to ascertain what it was which could be
     * useful.
     */
    protected transient File _Directory;
    /**
     * The Grids_AbstractGrid2DSquareCellChunk data cache. A collection is used
     * rather than an array because an element of a collection set to null is
     * available for garbage collection whereas an element of an array set to
     * null seems not to be.
     */
    protected transient HashMap<Grids_2D_ID_int, Grids_AbstractGrid2DSquareCellChunk> _ChunkID_AbstractGrid2DSquareCellChunk_HashMap;
    /**
     * A reference to the grid Statistics Object.
     */
    protected Grids_AbstractGridStatistics _GridStatistics;
    /**
     * For storing the number of chunk rows.
     */
    protected int _NChunkRows;
    /**
     * For storing the number of chunk columns.
     */
    protected int _NChunkCols;
    /**
     * For storing the (usual) number of rows of cells in a chunk. The number of
     * rows in the final chunk row may be less.
     */
    protected int _ChunkNRows;
    /**
     * For storing the (usual) number of columns of cells in a chunk. The number
     * of columns in the final chunk column may be less.
     */
    protected int _ChunkNCols;
    /**
     * For storing the number of rows in the grid.
     */
    protected long _NRows;
    /**
     * For storing the number of columns in the grid.
     */
    protected long _NCols;

    /**
     * For storing the _Name of the grid.
     */
    protected String _Name;

    /**
     * For storing cellsize, minx, miny, maxx, maxy. Although maxx and maxy
     * could be easily calculated if required, for convenience they are
     * calculated and stored by default during construction.
     */
    protected BigDecimal[] _Dimensions;

    /**
     * For storing the minimum number of decimal places used to store
     * _Dimensions. TODO Set this based on input data (Default value wanted?)
     */
    protected int _DimensionsScale = 10;

    /**
     * For storing individual locations mapped to a binary encoded long. This is
     * only used in Grids_Grid2DSquareCellDoubleChunk64CellMap and
     * Grids_Grid2DSquareCellIntChunk64CellMap. It is stored in this to save it
     * being stored in every chunk or calculated on the fly.
     */
    protected static Grids_UnsignedLongPowersOf2 _UnsignedLongPowersOf2;

    /**
     * Initialises this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap by first
     * attempting to load from new File( grid_File, "cache" );
     *
     * @param grid_File The File directory that from which a file called cache
     * is attempted to be loaded
     * @param handleOutOfMemoryError
     */
    protected void initGrid2DSquareCellChunks(
            File grid_File,
            boolean handleOutOfMemoryError) {
        try {
            initGrid2DSquareCellChunks(grid_File);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                initGrid2DSquareCellChunks(
                        grid_File,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap by first
     * attempting to load from new File( grid_File, "cache" );
     *
     * @param grid_File The File directory that from which a file called cache
     * is attempted to be loaded
     */
    protected void initGrid2DSquareCellChunks(
            File grid_File) {
        File cache = new File(
                grid_File,
                "cache");
        if (cache.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(
                        new BufferedInputStream(
                                new FileInputStream(cache)));
                this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap = (HashMap) ois.readObject();
                ois.close();
            } catch (Exception e0) {
                System.err.println(
                        e0.getMessage()
                        + " in initGrid2DSquareCellChunks("
                        + "File(" + grid_File.toString() + "))");
            }
        } else {
            this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap = new HashMap();
        }
    }

    /**
     * Initialises this from a_Grids_Environment.
     *
     * @param ge
     * @param handleOutOfMemoryError
     */
    protected void initGrid2DSquareCell(
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        try {
            initGrid2DSquareCell(ge);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                initGrid2DSquareCell(
                        ge,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected void initGrid2DSquareCell(
            Grids_Environment ge) {
        this.env = ge;
        this.env.init_Grid2DSquareCells_MemoryReserve(
                ge._AbstractGrid2DSquareCell_HashSet);
        this._ChunkNCols = 1;
        this._ChunkNRows = 1;
        this._Dimensions = new BigDecimal[5];
        this._Dimensions[0] = new BigDecimal(1L);
        this._Dimensions[1] = new BigDecimal(0L);
        this._Dimensions[2] = new BigDecimal(0L);
        this._Dimensions[3] = new BigDecimal(_ChunkNCols);
        this._Dimensions[4] = new BigDecimal(_ChunkNRows);
        init_Dimensions(_Dimensions);
        //this._Directory = Grids_FileCreator.createNewFile();
        this._Name = "DefaultName_" + getBasicDescription();
        this._Directory = Grids_FileCreator.createNewFile(
                ge._Directory,
                _Name);
        this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap = new HashMap();
        //this._AbstractGrid2DSquareCell_HashSet = new HashSet();
        this._GridStatistics = new Grids_GridStatistics0();
        // Set the reference to this in the Grid Statistics
        this._GridStatistics.init(this);
        //this._GridStatistics.grid2DSquareCell = this;
        this._NChunkCols = 1;
        this._NCols = 1L;
        this._NRows = 1L;
        ge._AbstractGrid2DSquareCell_HashSet.add(this);
    }

    /**
     * Initialises non transient Grids_AbstractGrid2DSquareCell fields from
     * _Grid2DSquareCell.
     *
     * @param a_Grid2DSquareCell The Grids_AbstractGrid2DSquareCell from which
     * the non transient Grids_AbstractGrid2DSquareCell fields of this are set.
     */
    protected void initGrid2DSquareCell(
            Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell) {
        this._ChunkNCols = a_Grid2DSquareCell._ChunkNCols;
        this._ChunkNRows = a_Grid2DSquareCell._ChunkNRows;
        this._Dimensions = a_Grid2DSquareCell._Dimensions;
        this._DimensionsScale = a_Grid2DSquareCell._DimensionsScale;
        //this._Directory = _Grid2DSquareCell._Directory;
        this._GridStatistics = a_Grid2DSquareCell._GridStatistics;
        // Set the reference to this in the Grid Statistics
        this._GridStatistics.init(this);
        //this._GridStatistics._Grid2DSquareCell = this;
        this._Name = a_Grid2DSquareCell._Name;
        this._NChunkCols = a_Grid2DSquareCell._NChunkCols;
        this._NChunkRows = a_Grid2DSquareCell._NChunkRows;
        this._NCols = a_Grid2DSquareCell._NCols;
        this._NRows = a_Grid2DSquareCell._NRows;
        //this._UnsignedLongPowersOf2 = _Grid2DSquareCell._UnsignedLongPowersOf2;
        //init_AbstractGrid2DSquareCell_HashSet( _Grid2DSquareCell._AbstractGrid2DSquareCell_HashSet );
        //this._AbstractGrid2DSquareCell_HashSet = _Grid2DSquareCell._AbstractGrid2DSquareCell_HashSet;
        env._AbstractGrid2DSquareCell_HashSet.add(this);
    }

    /**
     * @param handleOutOfMemoryError
     * @return a reference to this._AbstractGrid2DSquareCell_HashSet
     */
    public HashSet<Grids_AbstractGrid2DSquareCell> getGrid2DSquareCells(
            boolean handleOutOfMemoryError) {
        try {
            HashSet<Grids_AbstractGrid2DSquareCell> result = env.get_AbstractGrid2DSquareCell_HashSet();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getGrid2DSquareCells(
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
     * @return this._GridStatistics TODO: For safety, this method should either
     * be removed and this class be made implement GridStatisticsInterface. This
     * done the methods introduced would be made to call the relevant ones in
     * this._GridStatistics. Or the _GridStatistics need to be made safe in that
     * only copies of fields are passed.
     */
    public Grids_AbstractGridStatistics getGridStatistics(
            boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractGridStatistics result = getGridStatistics();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getGridStatistics(
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return this._GridStatistics TODO: For safety, this method should either
     * be removed and this class be made implement GridStatisticsInterface. This
     * done the methods introduced would be made to call the relevant ones in
     * this._GridStatistics. Or the _GridStatistics need to be made safe in that
     * only copies of fields are passed.
     */
    protected Grids_AbstractGridStatistics getGridStatistics() {
//        if ( this._GridStatistics.grid2DSquareCell != this ) {
//            boolean DEBUG = true;
//        }
        return this._GridStatistics;
    }

    /**
     * @return the Grids_AbstractGrid2DSquareCellChunk with
 ID._Row equal to chunkRowIndex and ID._Col
 equal to chunkColIndex.
     * @param chunkRowIndex The ID._Row of the returned
 Grids_AbstractGrid2DSquareCellChunk.
     * @param chunkColIndex The ID._Col of the returned
 Grids_AbstractGrid2DSquareCellChunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected Grids_AbstractGrid2DSquareCellChunk getGrid2DSquareCellChunk(
            int chunkRowIndex,
            int chunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            return getGrid2DSquareCellChunk(
                    chunkRowIndex,
                    chunkColIndex);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(chunkRowIndex, chunkColIndex, a_OutOfMemoryError);
                return getGrid2DSquareCellChunk(
                        chunkRowIndex,
                        chunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return the Grids_AbstractGrid2DSquareCellChunk with
 ID.chunkRowIndex equal to chunkRowIndex and ID.chunkColIndex
 equal to chunkColIndex.
     * @param chunkRowIndex The ID.chunkRowIndex of the returned
 Grids_AbstractGrid2DSquareCellChunk.
     * @param chunkColIndex The ID.chunkColIndex of the returned
 Grids_AbstractGrid2DSquareCellChunk.
     */
    protected Grids_AbstractGrid2DSquareCellChunk getGrid2DSquareCellChunk(
            int chunkRowIndex,
            int chunkColIndex) {
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                chunkRowIndex,
                chunkColIndex);
        return getGrid2DSquareCellChunk(chunkID);
    }

    /**
     * @return the Grids_AbstractGrid2DSquareCellChunk with Grids_2D_ID_int equal to
 _ChunkID.
     * @param a_ChunkID The Grids_2D_ID_int of the returned
 Grids_AbstractGrid2DSquareCellChunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_AbstractGrid2DSquareCellChunk getGrid2DSquareCellChunk(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractGrid2DSquareCellChunk result
                    = getGrid2DSquareCellChunk(a_ChunkID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getGrid2DSquareCellChunk(
                        a_ChunkID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return the Grids_AbstractGrid2DSquareCellChunk with Grids_2D_ID_int equal to
 _ChunkID.
     *
     *
     * @param chunkID The Grids_2D_ID_int of the returned
 Grids_AbstractGrid2DSquareCellChunk.
     */
    protected Grids_AbstractGrid2DSquareCellChunk getGrid2DSquareCellChunk(
            Grids_2D_ID_int chunkID) {
        boolean isInGrid = isInGrid(chunkID);
        if (isInGrid) {
            boolean grid2DSquareCellChunksContainsKey
                    = this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.containsKey(chunkID);
            if (!grid2DSquareCellChunksContainsKey) {
                loadIntoCacheChunk(
                        chunkID);
            }
            return (Grids_AbstractGrid2DSquareCellChunk) this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.get(
                    chunkID);
        }
        return null;
    }

    /**
     * @return HashSet containing all
 _ChunkID_AbstractGrid2DSquareCellChunk_HashMap.ID's.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public HashSet<Grids_2D_ID_int> getGrid2DSquareCellChunkIDHashSet(
            boolean handleOutOfMemoryError) {
        try {
            HashSet<Grids_2D_ID_int> result = getGrid2DSquareCellChunkIDHashSet();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getGrid2DSquareCellChunkIDHashSet(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return HashSet containing all
 _ChunkID_AbstractGrid2DSquareCellChunk_HashMap.ID's.
     */
    protected HashSet<Grids_2D_ID_int> getGrid2DSquareCellChunkIDHashSet() {
        HashSet<Grids_2D_ID_int> result = new HashSet<Grids_2D_ID_int>();
        result.addAll(this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.keySet());
        return result;
    }

    /**
     * @param handleOutOfMemoryError
     * @return Iterator over the cell values in this.
     */
    public abstract Iterator iterator(
            boolean handleOutOfMemoryError);

    /**
     * @param handleOutOfMemoryError
     * @return String description of this.
     */
    public abstract String toString(
            boolean handleOutOfMemoryError);

    /**
     * @return String description of this.
     * @param flag This is ignored. It is simply to distinguish this method from
     * the abstract method toString(boolean).
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public String toString(
            int flag,
            boolean handleOutOfMemoryError) {
        try {
            String result = toString(flag);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return toString(
                        flag,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a string description of the Abstract fields of this instance.
     * @param flag This is ignored. It is simply to distinguish this method from
     * the abstract method toString(boolean).
     */
    protected String toString(
            int flag) {
        String result
                = "chunkNcols( " + this._ChunkNCols + " ), "
                + "chunkNrows( " + this._ChunkNRows + " ), "
                + "dimensionsScale( " + this._DimensionsScale + " ), "
                + "_NChunkCols( " + this._NChunkCols + " ), "
                + "_NChunkRows( " + this._NChunkRows + " ), "
                + "ncols( " + this._NCols + " ), "
                + "nrows( " + this._NRows + " ), "
                + "directory( " + this._Directory + " ), "
                + "name( " + this._Name + " ), "
                + "dimensions( " + this._Dimensions[0].toString();
        for (int dimensionsID = 1; dimensionsID
                < this._Dimensions.length; dimensionsID++) {
            result += ", " + _Dimensions[dimensionsID].toString();
        }
        if (this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap == null) {
            result += " ), grid2DSquareCellChunks null";
        } else {
            result += " ), grid2DSquareCellChunks.size( " + this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.size();
        } //result += " ), grid2DSquareCellIntProcessor( " + this.grid2DSquareCellIntProcessor.toString();
        if (_UnsignedLongPowersOf2 == null) {
            result += " ), unsignedLongPowersOf2( null ), ";
        } else {
            result += " ), unsignedLongPowersOf2( " + _UnsignedLongPowersOf2.toString();
        }
        result += " ), Statistics( " + this._GridStatistics.toString(true);
        if (env.get_AbstractGrid2DSquareCell_HashSet() == null) {
            result += " ), grid2DSquareCells( null )";
        } else {
            result += " ), grid2DSquareCells.size( " + env.get_AbstractGrid2DSquareCell_HashSet().size() + " )";
        }
        return result;
    }

    /**
     * @return a copy of _Directory.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public File get_Directory(
            boolean handleOutOfMemoryError) {
        try {
            File result = get_Directory();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return get_Directory(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a copy of _Directory.
     */
    protected File get_Directory() {
        return new File(this._Directory.getPath());
    }

    /**
     * @return a copy of this._Name.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public String get_Name(
            boolean handleOutOfMemoryError) {
        try {
            String result = get_Name();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return get_Name(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a copy of this._Name.
     */
    protected String get_Name() {
        return this._Name;
    }

    /**
     * Sets this._Name to _Name.
     *
     * @param _Name The String this._Name is set to.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public void set_Name(
            String _Name,
            boolean handleOutOfMemoryError) {
        try {
            set_Name(_Name);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                Grids_AbstractGrid2DSquareCell.this.set_Name(
                        _Name,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Sets this._Name to _Name.
     *
     * @param _Name The String this._Name is set to.
     */
    protected void set_Name(String _Name) {
        this._Name = _Name;
    }

    /**
     * @return a basic description of this instance.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public String getBasicDescription(
            boolean handleOutOfMemoryError) {
        try {
            //return getBasicDescription();
            String result = getBasicDescription();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getBasicDescription(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a basic description of this instance.
     */
    protected String getBasicDescription() {
        return "className(" + this.getClass().getName()
                + ")_directory(" + this._Directory
                + ")_nrows(" + this._NRows
                + ")_ncols(" + this._NCols
                + ")_chunkNrows(" + this._ChunkNRows
                + ")_chunkNcols(" + this._ChunkNCols + ")";
    }

    /**
     * Sets the Grids_AbstractGrid2DSquareCellChunk with Grids_2D_ID_int equal to
 _ChunkID to _Grid2DSquareCellChunk.
     *
     * @param a_Grid2DSquareCellChunk
     * @param a_ChunkID The Grids_2D_ID_int of the Grids_AbstractGrid2DSquareCellChunk
 that is set.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public void setChunk(
            Grids_AbstractGrid2DSquareCellChunk a_Grid2DSquareCellChunk,
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                    a_ChunkID,
                    a_Grid2DSquareCellChunk);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(this, a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                setChunk(
                        a_Grid2DSquareCellChunk,
                        a_ChunkID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a copy of this._NCols.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final long get_NCols(
            boolean handleOutOfMemoryError) {
        try {
            long result = this._NCols;
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return get_NCols(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a copy of this._NRows.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final long get_NRows(
            boolean handleOutOfMemoryError) {
        try {
            long result = this._NRows;
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return get_NRows(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a copy of this._NChunkRows.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int get_NChunkRows(
            boolean handleOutOfMemoryError) {
        try {
            int result = get_NChunkRows();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return get_NChunkRows(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a copy of this._NChunkRows.
     */
    public final int get_NChunkRows() {
        return this._NChunkRows;
    }

    /**
     * Initialises this._NChunkRows.
     */
    protected final void init_NChunkRows() {
        long chunkNrows_long = (long) this._ChunkNRows;
        if ((this._NRows % chunkNrows_long) != 0) {
            this._NChunkRows = (int) (this._NRows / chunkNrows_long) + 1;
        } else {
            this._NChunkRows = (int) (this._NRows / chunkNrows_long);
        }
    }

    /**
     * @return a copy of this._NChunkCols.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int get_NChunkCols(
            boolean handleOutOfMemoryError) {
        try {
            int result = get_NChunkCols();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return get_NChunkCols(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a copy of this._NChunkCols.
     */
    public final int get_NChunkCols() {
        return this._NChunkCols;

    }

    /**
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return The number of Grid2DSquareCellDoubleChunkAbstracts in this as a
     * long.
     */
    public final long getNChunks(
            boolean handleOutOfMemoryError) {
        try {
            long result = getNChunks();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getNChunks(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return the number of Grid2DSquareCellDoubleChunkAbstracts in this as a
     * long.
     */
    protected final long getNChunks() {
        long nChunks = (long) this._NChunkRows * (long) this._NChunkCols;
        if (nChunks > Integer.MAX_VALUE) {
            System.err.println(
                    "Error totalChunkCount > Integer.MAX_VALUE in \n"
                    + "initGrid2DSquareCellDouble( \n"
                    + "  GridStatisticsAbstract, \n"
                    + "  File, \n"
                    + "  Grid2DSquareCellDoubleChunkAbstractFactory, \n"
                    + "  int, \n"
                    + "  int, \n"
                    + "  long, \n"
                    + "  long, \n"
                    + "  BigDecimal[], \n"
                    + "  double )");
            throw new Error();
        }
        return nChunks;
    }

    /**
     * Initialises this._NChunkCols.
     */
    protected final void init_NChunkCols() {
        long chunkNcols_long = (long) this._ChunkNCols;
        if ((_NCols % chunkNcols_long) != 0) {
            this._NChunkCols = (int) (this._NCols / chunkNcols_long) + 1;
        } else {
            this._NChunkCols = (int) (this._NCols / chunkNcols_long);
        }
    }

    /**
     * @return this._ChunkNRows.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int get_ChunkNRows(
            boolean handleOutOfMemoryError) {
        try {
            int result = get_ChunkNRows();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return get_ChunkNRows(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return this.getChunkNRows.
     */
    protected final int get_ChunkNRows() {
        return this._ChunkNRows;
    }

    /**
     * @param chunkRowIndex
     * @return _ChunkNRows, the number of rows in
 Grids_AbstractGrid2DSquareCellChunk with ID._Row equal to
 _Row.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int get_ChunkNRows(
            int chunkRowIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = get_ChunkNRows(chunkRowIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return get_ChunkNRows(
                        chunkRowIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @return _ChunkNRows, the number of rows in
 Grids_AbstractGrid2DSquareCellChunk with ID._Row equal to
 _Row.
     */
    protected final int get_ChunkNRows(
            int chunkRowIndex) {
        if (chunkRowIndex > -1 && chunkRowIndex < this._NChunkRows) {
            if (chunkRowIndex == (this._NChunkRows - 1)) {
                return getChunkNrowsFinalRowChunks();
            } else {
                return this._ChunkNRows;
            }
        } else {
            return 0;
        }
    }

    /**
     * @return this._ChunkNCols.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int get_ChunkNCols(
            boolean handleOutOfMemoryError) {
        try {
            int result = get_ChunkNCols();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return get_ChunkNCols(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return this._ChunkNCols.
     */
    protected final int get_ChunkNCols() {
        return this._ChunkNCols;
    }

    /**
     * @param chunkColIndex
     * @return _ChunkNCols, the number of columns in
 Grids_AbstractGrid2DSquareCellChunk with ID._Col equal to
 _Col.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int get_ChunkNCols(
            int chunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = get_ChunkNCols(chunkColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return get_ChunkNCols(
                        chunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param chunkColIndex
     * @return _ChunkNCols, the number of columns in
 Grids_AbstractGrid2DSquareCellChunk with ID._Col equal to
 _Col.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @param a_ChunkID This is a Grids_2D_ID_int for those AbstractGrid2DSquareCells
 not to be swapped if possible when an OutOfMemoryError is encountered.
     */
    public final int get_ChunkNCols(
            int chunkColIndex,
            boolean handleOutOfMemoryError,
            Grids_2D_ID_int a_ChunkID) {
        try {
            int result = get_ChunkNCols(chunkColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(this, a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return get_ChunkNCols(
                        chunkColIndex,
                        handleOutOfMemoryError,
                        a_ChunkID);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param chunkColIndex
     * @return _ChunkNCols, the number of columns in
 Grids_AbstractGrid2DSquareCellChunk with ID._Col equal to
 _Col.
     */
    protected final int get_ChunkNCols(
            int chunkColIndex) {
        if (chunkColIndex > -1 && chunkColIndex < this._NChunkCols) {
            if (chunkColIndex == (this._NChunkCols - 1)) {
                return getChunkNcolsFinalColChunks();
            } else {
                return this._ChunkNCols;
            }
        } else {
            return 0;
        }
    }

    /**
     * @return the number of rows in the final row Chunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected final int getChunkNrowsFinalRowChunks(
            boolean handleOutOfMemoryError) {
        try {
            return getChunkNrowsFinalRowChunks();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getChunkNrowsFinalRowChunks(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return the number of rows in the final row Chunk.
     */
    protected final int getChunkNrowsFinalRowChunks() {
        long longNChunkRowsMinusOne = (long) (this._NChunkRows - 1);
        long longChunkNrows = (long) this._ChunkNRows;
        return (int) (this._NRows - (longNChunkRowsMinusOne * longChunkNrows));
    }

    /**
     * @return the number of cols in the final col Chunk
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected final int getChunkNcolsFinalColChunks(
            boolean handleOutOfMemoryError) {
        try {
            return getChunkNcolsFinalColChunks();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getChunkNcolsFinalColChunks(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return the number of cols in the final col Chunk
     */
    protected final int getChunkNcolsFinalColChunks() {
        long nChunkColsMinusOne_long = (long) (this._NChunkCols - 1);
        long chunkNcols_long = (long) this._ChunkNCols;
        return (int) (this._NCols - (nChunkColsMinusOne_long * chunkNcols_long));
    }

    /**
     * @return _ChunkNRows, the number of rows in
 Grids_AbstractGrid2DSquareCellChunk with Grids_2D_ID_int equal to _ChunkID
     * @param a_ChunkID The Grids_2D_ID_int of the Grids_AbstractGrid2DSquareCellChunk
 thats number of rows is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkNRows(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            int result = getChunkNRows(a_ChunkID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(this, a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getChunkNRows(
                        a_ChunkID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return _ChunkNRows, the number of rows in
 Grids_AbstractGrid2DSquareCellChunk with Grids_2D_ID_int equal to _ChunkID
     * @param a_ChunkID The Grids_2D_ID_int of the Grids_AbstractGrid2DSquareCellChunk
 thats number of rows is returned.
     */
    protected final int getChunkNRows(
            Grids_2D_ID_int a_ChunkID) {
        if (a_ChunkID._Row < (this._NChunkRows - 1)) {
            return this._ChunkNRows;
        } else {
            return getChunkNrowsFinalRowChunks();
        }
    }

    /**
     * @return _ChunkNCols, the number of columns in
 Grids_AbstractGrid2DSquareCellChunk with Grids_2D_ID_int equal to _ChunkID
     * @param a_ChunkID The Grids_2D_ID_int of the Grids_AbstractGrid2DSquareCellChunk
 thats number of columns is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkNCols(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            int result = getChunkNCols(a_ChunkID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(this, a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getChunkNCols(
                        a_ChunkID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return _ChunkNCols, the number of columns in
 Grids_AbstractGrid2DSquareCellChunk with Grids_2D_ID_int equal to _ChunkID
     * @param a_ChunkID The Grids_2D_ID_int of the Grids_AbstractGrid2DSquareCellChunk
 thats number of columns is returned.
     */
    protected final int getChunkNCols(
            Grids_2D_ID_int a_ChunkID) {
        if (a_ChunkID._Col < (this._NChunkCols - 1)) {
            return this._ChunkNCols;
        } else {
            return getChunkNcolsFinalColChunks();
        }
    }

    /**
     * @return A copy of this._Dimensions (which stores cellsize, minx, miny,
     * maxx, maxy).
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final BigDecimal[] get_Dimensions(
            boolean handleOutOfMemoryError) {
        try {
            //double dimensions0 = this._Dimensions[0].doubleValue(); // For debugging
            //double dimensions1 = this._Dimensions[1].doubleValue(); // For debugging
            //double dimensions2 = this._Dimensions[2].doubleValue(); // For debugging
            //double dimensions3 = this._Dimensions[3].doubleValue(); // For debugging
            //double dimensions4 = this._Dimensions[4].doubleValue(); // For debugging
            int length = _Dimensions.length;
            BigDecimal[] result = new BigDecimal[length];
            System.arraycopy(_Dimensions, 0, result, 0, length);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return get_Dimensions(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return double equal to this._Dimensions[0].doubleValue.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellsizeDouble(
            boolean handleOutOfMemoryError) {
        try {
            double result = this._Dimensions[0].doubleValue();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellsizeDouble(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return this._DimensionsScale.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected final int get_DimensionsScale(
            boolean handleOutOfMemoryError) {
        try {
            return this._DimensionsScale;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return get_DimensionsScale(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Initialises this._Dimensions and this.dimensionScale.
     *
     * @param a_Dimensions
     */
    protected final void init_Dimensions(
            BigDecimal[] a_Dimensions) {
        this._Dimensions = a_Dimensions;
        this._DimensionsScale = Integer.MIN_VALUE;
        for (int i = 0; i < a_Dimensions.length; i++) {
            this._DimensionsScale = Math.max(
                    this._DimensionsScale,
                    a_Dimensions[i].scale());
        }
    }

    /**
     * @return Grids_AbstractGrid2DSquareCellChunk cell value at at
     * Point2D.Double point as a double.
     * @param point The Point2D.Double for which the cell value is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getCellDouble(
            Point2D.Double point,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(point);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(point.getY()),
                        getChunkColIndex(point.getX()));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(
                        point,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Grids_AbstractGrid2DSquareCellChunk cell value at at
     * Point2D.Double point as a double.
     * @param point The Point2D.Double for which the cell value is returned.
     */
    protected double getCellDouble(
            Point2D.Double point) {
        return getCellDouble(
                getChunkRowIndex(point.getY()),
                getChunkColIndex(point.getX()),
                getChunkCellRowIndex(point.getY()),
                getChunkCellColIndex(point.getX()));
    }

    /**
     * @return Grids_AbstractGrid2DSquareCellChunk cell value at at point given
     * by x-coordinate x and y-coordinate y as a double.
     * @param x The x coordinate of the point at which the cell value is
     * returned.
     * @param y The y coordinate of the point at which the cell value is
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getCellDouble(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(
                    x,
                    y);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Grids_AbstractGrid2DSquareCellChunk cell value at at point given
     * by x-coordinate x and y-coordinate y as a double.
     * @param x The x coordinate of the point at which the cell value is
     * returned.
     * @param y The y coordinate of the point at which the cell value is
     * returned.
     */
    protected double getCellDouble(
            double x,
            double y) {
        return getCellDouble(
                getChunkRowIndex(y),
                getChunkColIndex(x),
                getChunkCellRowIndex(y),
                getChunkCellColIndex(x));
    }

    /**
     * @param cellRowIndex
     * @param cellColIndex
     * @return Grids_AbstractGrid2DSquareCellChunk cell value at cell row index
     * equal to _CellRowIndex, cell col index equal to _CellColIndex as a
     * double.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getCellDouble(
            long cellRowIndex,
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(
                    cellRowIndex,
                    cellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellRowIndex),
                        getChunkColIndex(cellColIndex));
                freeSomeMemoryAndResetReserve(chunkID, a_OutOfMemoryError);
                return getCellDouble(
                        cellRowIndex,
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;

            }
        }
    }

    /**
     * @param cellRowIndex
     * @param cellColIndex
     * @return Grids_AbstractGrid2DSquareCellChunk cell value at cell row index
     * equal to _CellRowIndex, cell col index equal to _CellColIndex as a
     * double.
     * @param cellRowIndex The cell row index of the .
     */
    protected double getCellDouble(
            long cellRowIndex,
            long cellColIndex) {
        return getCellDouble(
                getChunkRowIndex(cellRowIndex),
                getChunkColIndex(cellColIndex),
                getChunkCellRowIndex(cellRowIndex),
                getChunkCellColIndex(cellColIndex));
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return Grids_AbstractGrid2DSquareCellChunk cell value at cell row index
     * equal to _CellRowIndex, cell col index equal to _CellColIndex as a
     * double.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getCellDouble(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(
                    chunkRowIndex,
                    chunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRowIndex,
                        chunkColIndex);
                freeSomeMemoryAndResetReserve(chunkID, a_OutOfMemoryError);
                return getCellDouble(
                        chunkRowIndex,
                        chunkColIndex,
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * TODO
     *
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return Grids_AbstractGrid2DSquareCellChunk cell value at cell row index
     * equal to _CellRowIndex, cell col index equal to _CellColIndex as a
     * double.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     */
    protected double getCellDouble(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        if (!isInGrid(chunkRowIndex, chunkColIndex)) {
            if (this instanceof Grids_Grid2DSquareCellDouble) {
                return ((Grids_Grid2DSquareCellDouble) this)._NoDataValue;
            } else {
                //this instanceof Grids_Grid2DSquareCellInt
                return ((Grids_Grid2DSquareCellInt) this).getNoDataValue();
            }
        }
        Grids_AbstractGrid2DSquareCellChunk grid2DSquareCellChunk
                = getGrid2DSquareCellChunk(
                        chunkRowIndex,
                        chunkColIndex);
        if (grid2DSquareCellChunk == null) {
            return this.getNoDataValueBigDecimal(false).doubleValue();
        }
        return getCellDouble(
                grid2DSquareCellChunk,
                chunkRowIndex,
                chunkColIndex,
                chunkCellRowIndex,
                chunkCellColIndex);
    }

    /**
     * @param chunkColIndex
     * @param chunkRowIndex
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
 col index chunkCellColIndex of Grids_AbstractGrid2DSquareCellChunk given
 by chunk row index _Row, chunk col index _Col as a
 double.
     * @param grid2DSquareCellChunk The Grids_AbstractGrid2DSquareCellChunk
     * containing the cell.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getCellDouble(
            Grids_AbstractGrid2DSquareCellChunk grid2DSquareCellChunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(
                    grid2DSquareCellChunk,
                    chunkRowIndex,
                    chunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRowIndex,
                        chunkColIndex);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(
                        grid2DSquareCellChunk,
                        chunkRowIndex,
                        chunkColIndex,
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * TODO
     *
     * @param chunkColIndex
     * @param chunkRowIndex
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
 col index chunkCellColIndex of Grids_AbstractGrid2DSquareCellChunk given
 by chunk row index _Row, chunk col index _Col as a
 double.
     * @param grid2DSquareCellChunk The Grids_AbstractGrid2DSquareCellChunk
     * containing the cell.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     */
    protected double getCellDouble(
            Grids_AbstractGrid2DSquareCellChunk grid2DSquareCellChunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        if (grid2DSquareCellChunk instanceof Grids_AbstractGrid2DSquareCellDoubleChunk) {
            Grids_AbstractGrid2DSquareCellDoubleChunk grid2DSquareCellDoubleChunk
                    = (Grids_AbstractGrid2DSquareCellDoubleChunk) grid2DSquareCellChunk;
            Grids_Grid2DSquareCellDouble grid2DSquareCellDouble
                    = grid2DSquareCellDoubleChunk.getGrid2DSquareCellDouble();
            double noDataValue = grid2DSquareCellDouble._NoDataValue;
            if (grid2DSquareCellChunk.getClass()
                    == Grids_Grid2DSquareCellDoubleChunk64CellMap.class) {
                Grids_Grid2DSquareCellDoubleChunk64CellMap grid2DSquareCellDoubleChunk64CellMap
                        = (Grids_Grid2DSquareCellDoubleChunk64CellMap) grid2DSquareCellDoubleChunk;
                return grid2DSquareCellDoubleChunk.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkArray.class) {
                Grids_Grid2DSquareCellDoubleChunkArray grid2DSquareCellDoubleChunkArray
                        = (Grids_Grid2DSquareCellDoubleChunkArray) grid2DSquareCellDoubleChunk;
                return grid2DSquareCellDoubleChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkJAI.class) {
                Grids_Grid2DSquareCellDoubleChunkJAI grid2DSquareCellDoubleChunkJAI
                        = (Grids_Grid2DSquareCellDoubleChunkJAI) grid2DSquareCellDoubleChunk;
                return grid2DSquareCellDoubleChunkJAI.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkMap.class) {
                Grids_Grid2DSquareCellDoubleChunkMap grid2DSquareCellDoubleChunkMap
                        = (Grids_Grid2DSquareCellDoubleChunkMap) grid2DSquareCellDoubleChunk;
                return grid2DSquareCellDoubleChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkRAF.class) {
                Grids_Grid2DSquareCellDoubleChunkRAF grid2DSquareCellDoubleChunkRAF
                        = (Grids_Grid2DSquareCellDoubleChunkRAF) grid2DSquareCellDoubleChunk;
                return grid2DSquareCellDoubleChunkRAF.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            return noDataValue;
        } else {
            //( grid2DSquareCellChunk instanceof Grids_AbstractGrid2DSquareCellIntChunk )
            Grids_AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk
                    = (Grids_AbstractGrid2DSquareCellIntChunk) grid2DSquareCellChunk;
            Grids_Grid2DSquareCellInt grid2DSquareCellInt
                    = grid2DSquareCellIntChunk.getGrid2DSquareCellInt();
            int noDataValue = grid2DSquareCellInt.getNoDataValue(true);
            if (grid2DSquareCellChunk.getClass()
                    == Grids_Grid2DSquareCellIntChunk64CellMap.class) {
                Grids_Grid2DSquareCellIntChunk64CellMap grid2DSquareCellIntChunk64CellMap
                        = (Grids_Grid2DSquareCellIntChunk64CellMap) grid2DSquareCellIntChunk;
                return (double) grid2DSquareCellIntChunk.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkArray.class) {
                Grids_Grid2DSquareCellIntChunkArray grid2DSquareCellIntChunkArray
                        = (Grids_Grid2DSquareCellIntChunkArray) grid2DSquareCellIntChunk;
                return (double) grid2DSquareCellIntChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkJAI.class) {
                Grids_Grid2DSquareCellIntChunkJAI grid2DSquareCellIntChunkJAI
                        = (Grids_Grid2DSquareCellIntChunkJAI) grid2DSquareCellIntChunk;
                return (double) grid2DSquareCellIntChunkJAI.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkMap.class) {
                Grids_Grid2DSquareCellIntChunkMap grid2DSquareCellIntChunkMap
                        = (Grids_Grid2DSquareCellIntChunkMap) grid2DSquareCellIntChunk;
                return (double) grid2DSquareCellIntChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkRAF.class) {
                Grids_Grid2DSquareCellIntChunkRAF grid2DSquareCellIntChunkRAF
                        = (Grids_Grid2DSquareCellIntChunkRAF) grid2DSquareCellIntChunk;
                return (double) grid2DSquareCellIntChunkRAF.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            return (double) noDataValue;
        }
    }

    /**
     * @param cellRowIndex
     * @param cellColIndex
     * @return Grids_AbstractGrid2DSquareCellChunk cell value at cell row index
     * equal to cellRowIndex, cell col index equal to cellColIndex as a int.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int getCellInt(
            long cellRowIndex,
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCellInt(
                    cellRowIndex,
                    cellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellRowIndex),
                        getChunkColIndex(cellColIndex));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellInt(
                        cellRowIndex,
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Grids_AbstractGrid2DSquareCellChunk cell value at cell row index
     * equal to cellRowIndex, cell col index equal to cellColIndex as a int.
     * @param cellRowIndex The cell row index.
     * @param cellColIndex The cell column index.
     */
    protected int getCellInt(
            long cellRowIndex,
            long cellColIndex) {
        return getCellInt(
                getChunkRowIndex(cellRowIndex),
                getChunkColIndex(cellColIndex),
                getChunkCellRowIndex(cellRowIndex),
                getChunkCellColIndex(cellColIndex));
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
     * col index chunkCellColIndex of Grids_AbstractGrid2DSquareCellChunk given
     * by chunk row index chunkRowIndex, chunk col index chunkColIndex as a int.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int getCellInt(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCellInt(
                    chunkRowIndex,
                    chunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRowIndex,
                        chunkColIndex);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellInt(
                        chunkRowIndex,
                        chunkColIndex,
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
     * col index chunkCellColIndex of Grids_AbstractGrid2DSquareCellChunk given
     * by chunk row index chunkRowIndex, chunk col index chunkColIndex as a int.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     */
    protected int getCellInt(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        Grids_AbstractGrid2DSquareCellChunk grid2DSquareCellChunk = getGrid2DSquareCellChunk(
                chunkRowIndex,
                chunkColIndex);
        return getCellInt(
                grid2DSquareCellChunk,
                chunkRowIndex,
                chunkColIndex,
                chunkCellRowIndex,
                chunkCellColIndex);
    }

    /**
     * @param chunkColIndex
     * @param chunkRowIndex
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
     * col index chunkCellColIndex of Grids_AbstractGrid2DSquareCellChunk given
     * by chunk row index chunkRowIndex, chunk col index chunkColIndex as a int.
     * @param grid2DSquareCellChunk The Grids_AbstractGrid2DSquareCellChunk
     * containing the cell.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int getCellInt(
            Grids_AbstractGrid2DSquareCellChunk grid2DSquareCellChunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCellInt(
                    grid2DSquareCellChunk,
                    chunkRowIndex,
                    chunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRowIndex,
                        chunkColIndex);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellInt(
                        grid2DSquareCellChunk,
                        chunkRowIndex,
                        chunkColIndex,
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkColIndex
     * @param chunkRowIndex
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
     * col index chunkCellColIndex of Grids_AbstractGrid2DSquareCellChunk given
     * by chunk row index chunkRowIndex, chunk col index chunkColIndex as a int.
     * @param grid2DSquareCellChunk The Grids_AbstractGrid2DSquareCellChunk
     * containing the cell.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     */
    protected int getCellInt(
            Grids_AbstractGrid2DSquareCellChunk grid2DSquareCellChunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        if (grid2DSquareCellChunk instanceof Grids_AbstractGrid2DSquareCellDoubleChunk) {
            Grids_AbstractGrid2DSquareCellDoubleChunk grid2DSquareCellDoubleChunk
                    = (Grids_AbstractGrid2DSquareCellDoubleChunk) grid2DSquareCellChunk;
            Grids_Grid2DSquareCellDouble grid2DSquareCellDouble
                    = grid2DSquareCellDoubleChunk.getGrid2DSquareCellDouble();
            double noDataValue = grid2DSquareCellDouble._NoDataValue;
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunk64CellMap.class) {
                Grids_Grid2DSquareCellDoubleChunk64CellMap grid2DSquareCellDoubleChunk64CellMap
                        = (Grids_Grid2DSquareCellDoubleChunk64CellMap) grid2DSquareCellDoubleChunk;
                return (int) grid2DSquareCellDoubleChunk64CellMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkArray.class) {
                Grids_Grid2DSquareCellDoubleChunkArray grid2DSquareCellDoubleChunkArray
                        = (Grids_Grid2DSquareCellDoubleChunkArray) grid2DSquareCellDoubleChunk;
                return (int) grid2DSquareCellDoubleChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkJAI.class) {
                Grids_Grid2DSquareCellDoubleChunkJAI grid2DSquareCellDoubleChunkJAI
                        = (Grids_Grid2DSquareCellDoubleChunkJAI) grid2DSquareCellDoubleChunk;
                return (int) grid2DSquareCellDoubleChunkJAI.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkMap.class) {
                Grids_Grid2DSquareCellDoubleChunkMap grid2DSquareCellDoubleChunkMap
                        = (Grids_Grid2DSquareCellDoubleChunkMap) grid2DSquareCellDoubleChunk;
                return (int) grid2DSquareCellDoubleChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkRAF.class) {
                Grids_Grid2DSquareCellDoubleChunkRAF grid2DSquareCellDoubleChunkRAF
                        = (Grids_Grid2DSquareCellDoubleChunkRAF) grid2DSquareCellDoubleChunk;
                return (int) grid2DSquareCellDoubleChunkRAF.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            return (int) noDataValue;
        } else {
            //if ( grid2DSquareCellChunk instanceof Grids_AbstractGrid2DSquareCellIntChunk ) {
            Grids_AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk
                    = (Grids_AbstractGrid2DSquareCellIntChunk) grid2DSquareCellChunk;
            Grids_Grid2DSquareCellInt grid2DSquareCellInt
                    = grid2DSquareCellIntChunk.getGrid2DSquareCellInt();
            int noDataValue = grid2DSquareCellInt.getNoDataValue();
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunk64CellMap.class) {
                Grids_Grid2DSquareCellIntChunk64CellMap grid2DSquareCellIntChunk64CellMap
                        = (Grids_Grid2DSquareCellIntChunk64CellMap) grid2DSquareCellIntChunk;
                return grid2DSquareCellIntChunk64CellMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkArray.class) {
                Grids_Grid2DSquareCellIntChunkArray grid2DSquareCellIntChunkArray
                        = (Grids_Grid2DSquareCellIntChunkArray) grid2DSquareCellIntChunk;
                return grid2DSquareCellIntChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkJAI.class) {
                Grids_Grid2DSquareCellIntChunkJAI grid2DSquareCellIntChunkJAI
                        = (Grids_Grid2DSquareCellIntChunkJAI) grid2DSquareCellIntChunk;
                return grid2DSquareCellIntChunkJAI.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkMap.class) {
                Grids_Grid2DSquareCellIntChunkMap grid2DSquareCellIntChunkMap
                        = (Grids_Grid2DSquareCellIntChunkMap) grid2DSquareCellIntChunk;
                return grid2DSquareCellIntChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkRAF.class) {
                Grids_Grid2DSquareCellIntChunkRAF grid2DSquareCellIntChunkRAF
                        = (Grids_Grid2DSquareCellIntChunkRAF) grid2DSquareCellIntChunk;
                return grid2DSquareCellIntChunkRAF.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            return noDataValue;
        }
    }

    /**
     * @param valueToSet
     * @return the value at _CellRowIndex, _CellColIndex as a double and sets it
     * to valueToSet.
     * @param cellRowIndex The cell row index.
     * @param cellColIndex The cell column index.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double setCell(
            long cellRowIndex,
            long cellColIndex,
            double valueToSet,
            boolean handleOutOfMemoryError) {
        try {
            double result = setCell(
                    cellRowIndex,
                    cellColIndex,
                    valueToSet);
            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                    getChunkRowIndex(cellRowIndex),
                    getChunkColIndex(cellColIndex));
            env.tryToEnsureThereIsEnoughMemoryToContinue(
                    this,
                    chunkID,
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellRowIndex),
                        getChunkColIndex(cellColIndex));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return setCell(
                        cellRowIndex,
                        cellColIndex,
                        valueToSet,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Sets the value at _CellRowIndex, _CellColIndex to valueToSet.
     *
     * @param cellRowIndex The cell row index.
     * @param cellColIndex The cell column index.
     * @param valueToSet The value set.
     * @return
     */
    protected abstract double setCell(
            long cellRowIndex,
            long cellColIndex,
            double valueToSet);

    /**
     * @return HashSet of ChunkIDs for cells thats centroids are intersected by
     * circle with centre at x-coordinate x, y-coordinate y, and radius
     * distance.
     * @param x The x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y The y-coordinate of the circle centre from which cell values are
     * returned.
     * @param cellRowIndex The row index at y.
     * @param cellColIndex The col index at x.
     * @param distance The radius of the circle for which intersected cell
     * values are returned.
     */
    protected HashSet<Grids_2D_ID_int> getChunkIDs(
            double distance,
            double x,
            double y,
            long cellRowIndex,
            long cellColIndex) {
        HashSet<Grids_2D_ID_int> result = new HashSet<Grids_2D_ID_int>();
        long p;
        long q;
        long cellDistance = (long) Math.ceil(distance / _Dimensions[0].doubleValue());
        double thisCellX;
        double thisCellY;
        double thisDistance;
        for (p = -cellDistance; p
                <= cellDistance; p++) {
            thisCellY = getCellYDouble(cellRowIndex + p);
            for (q = -cellDistance; q
                    <= cellDistance; q++) {
                thisCellX = getCellXDouble(cellColIndex + q);
                thisDistance = Grids_Utilities.distance(thisCellX, thisCellY, x, y);
                if (thisDistance < distance) {
                    Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                            getChunkRowIndex((long) cellRowIndex + p),
                            getChunkColIndex((long) cellColIndex + q));
                    result.add(chunkID);
                }
            }
        }
        return result;
    }

    /**
     * @return Chunk column index for the Grids_AbstractGrid2DSquareCellChunk
     * intersecting the x-coordinate x.
     *
     * @param x The x-coordinate of the line intersecting the chunk column index
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkColIndex(
            double x,
            boolean handleOutOfMemoryError) {
        try {
            int result = getChunkColIndex(x);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkColIndex(
                        x,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Chunk column index for the Grids_AbstractGrid2DSquareCellChunk
     * intersecting the x-coordinate x.
     * @param x The x-coordinate of the line intersecting the chunk column index
     * returned.
     */
    protected final int getChunkColIndex(
            double x) {
        return getChunkColIndex(getCellColIndex(x));
    }

    /**
     * @return Chunk column index for the Grids_AbstractGrid2DSquareCellChunk
     * intersecting the cell column index _CellColIndex.
     * @param cellColIndex
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkColIndex(
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getChunkColIndex(cellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkColIndex(
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Chunk column index for the Grids_AbstractGrid2DSquareCellChunk
     * intersecting the cell column index _CellColIndex.
     *
     * @param cellColIndex
     */
    protected final int getChunkColIndex(
            long cellColIndex) {
        return (int) (cellColIndex / (long) this._ChunkNCols);
    }

    /**
     * @return Cell column Index for the cell column that intersect the
     * x-coordinate x.
     * @param x The x-coordinate of the line intersecting the cell column index
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final long getCellColIndex(
            double x,
            boolean handleOutOfMemoryError) {
        try {
            long result = getCellColIndex(x);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellColIndex(
                        x,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Cell column Index for the cell column that intersect the
     * x-coordinate x.
     * @param x The x-coordinate of the line intersecting the cell column index
     * returned.
     */
    protected final long getCellColIndex(
            double x) {
        return getCellColIndex(BigDecimal.valueOf(x));
    }

    /**
     * @return Cell column Index for the cell column that intersect the
     * x-coordinate xBigDecimal.
     * @param xBigDecimal The x-coordinate of the line intersecting the cell
     * column index returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final long getCellColIndex(
            BigDecimal xBigDecimal,
            boolean handleOutOfMemoryError) {
        try {
            long result = getCellColIndex(xBigDecimal);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellColIndex(
                        xBigDecimal,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param x_BigDecimal
     * @return Cell column Index for the cell column that intersect the
     * x-coordinate xBigDecimal.
     */
    protected final long getCellColIndex(
            BigDecimal x_BigDecimal) {
        BigDecimal xMinusMinX_BigDecimal = x_BigDecimal.subtract(
                this._Dimensions[1]);
        BigDecimal tmp;
        tmp = Generic_BigDecimal.divideRoundIfNecessary(xMinusMinX_BigDecimal, this._Dimensions[0], 0, RoundingMode.DOWN);
        return tmp.toBigInteger().longValue();
//        return xMinusMinX_BigDecimal.divide(
//                this._Dimensions[0]).toBigInteger().longValue();
    }

    /**
     * @param chunkColIndex
     * @return Cell column index for the cells in chunk column index
 _Col chunk cell column index chunkCellColIndex.
     * @param chunkCellColIndex
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final long getCellColIndex(
            int chunkColIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            long result = getCellColIndex(
                    chunkColIndex,
                    chunkCellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellColIndex(
                        chunkColIndex,
                        chunkCellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;

            }
        }
    }

    /**
     * @param chunkColIndex
     * @return Cell column index for the cells in chunk column index
 _Col chunk cell column index chunkCellColIndex.
     * @param chunkCellColIndex
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @param chunkID This is a Grids_2D_ID_int for those AbstractGrid2DSquareCells not
 to be swapped if possible when an OutOfMemoryError is encountered.
     */
    public final long getCellColIndex(
            int chunkColIndex,
            int chunkCellColIndex,
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            long result = getCellColIndex(
                    chunkColIndex,
                    chunkCellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(this, chunkID, handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellColIndex(
                        chunkColIndex,
                        chunkCellColIndex,
                        chunkID,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkColIndex
     * @return Cell column index for the cells in chunk column index
 _Col chunk cell column index chunkCellColIndex.
     * @param chunkCellColIndex
     */
    protected final long getCellColIndex(
            int chunkColIndex,
            int chunkCellColIndex) {
        return ((long) chunkColIndex * (long) this._ChunkNCols) + (long) chunkCellColIndex;
    }

    /**
     * @return Chunk cell column Index of the cells that intersect the
     * x-coordinate x.
     * @param x The x-coordinate of the line intersecting the chunk cell column
     * index returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkCellColIndex(
            double x,
            boolean handleOutOfMemoryError) {
        try {
            int result = getChunkCellColIndex(x);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkCellColIndex(
                        x,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Chunk cell column Index of the cells that intersect the
     * x-coordinate x.
     * @param x The x-coordinate of the line intersecting the chunk cell column
     * index returned.
     */
    protected final int getChunkCellColIndex(
            double x) {
        return getChunkCellColIndex(getCellColIndex(x));
    }

    /**
     * @return Chunk cell column index of the cells in the cell column index
     * _CellColIndex.
     * @param cellColIndex The cell column index of the cell thats chunk cell
     * column index is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkCellColIndex(
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getChunkCellColIndex(cellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkCellColIndex(
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Chunk cell column index of the cells in the cell column index
     * _CellColIndex.
     * @param cellColIndex The cell column index of the cell thats chunk cell
     * column index is returned.
     */
    protected final int getChunkCellColIndex(
            long cellColIndex) {
        return (int) (cellColIndex - ((cellColIndex / this._ChunkNCols) * this._ChunkNCols));
    }

    /**
     * @param a_Random
     * @param handleOutOfMemoryError
     * @return A Random CellColIndex.
     */
    public final long getCellColIndex(
            Random a_Random,
            boolean handleOutOfMemoryError) {
        try {
            long result = getCellColIndex(
                    a_Random);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellColIndex(
                        a_Random,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param a_Random
     * @return A Random CellColIndex.
     */
    protected final long getCellColIndex(
            Random a_Random) {
        if (_NCols < Integer.MAX_VALUE) {
            return a_Random.nextInt((int) _NCols);
        } else {
            long col = 0;
            long colMax = 0;
            while (colMax < _NCols) {
                colMax += Integer.MAX_VALUE;
                if (colMax < _NCols) {
                    col += a_Random.nextInt();
                } else {
                    int colInt = (int) (colMax - _NCols);
                    if (colInt > 0) {
                        col += a_Random.nextInt();
                    }
                }
            }
            return col;
        }
    }

    /**
     * @return Chunk row index for the chunks intersecting the line given by
     * y-coordinate y.
     * @param y The y-coordinate of the line for which the chunk row index is
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkRowIndex(
            double y,
            boolean handleOutOfMemoryError) {
        try {
            int result = getChunkRowIndex(y);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkRowIndex(
                        y,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Chunk row index for the chunks intersecting the line given by
     * y-coordinate y.
     * @param y The y-coordinate of the line for which the chunk row index is
     * returned.
     */
    protected final int getChunkRowIndex(
            double y) {
        return getChunkRowIndex(getCellRowIndex(y));
    }

    /**
     * @return Chunk row index for the chunk intersecting the cells with cell
     * row index _CellRowIndex.
     * @param cellRowIndex The cell row index of the cells thats chunk row index
     * is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkRowIndex(
            long cellRowIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getChunkRowIndex(cellRowIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkRowIndex(
                        cellRowIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Chunk row index for the chunk intersecting the cells with cell
     * row index _CellRowIndex.
     * @param cellRowIndex The cell row index of the cells thats chunk row index
     * is returned.
     */
    protected final int getChunkRowIndex(
            long cellRowIndex) {
        return (int) (cellRowIndex / (long) this._ChunkNRows);
    }

    /**
     * @return Cell row Index for the cells that intersect the line with
     * y-coordinate y.
     * @param y The y-coordinate of the line thats cell row index is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final long getCellRowIndex(
            double y,
            boolean handleOutOfMemoryError) {
        try {
            long result = getCellRowIndex(y);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellRowIndex(
                        y,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Cell row Index for the cells that intersect the line with
     * y-coordinate y.
     * @param y The y-coordinate of the line thats cell row index is returned.
     */
    protected final long getCellRowIndex(
            double y) {
        return getCellRowIndex(BigDecimal.valueOf(y));
    }

    /**
     * @return Cell row Index for the cells that intersect the line with
     * y-coordinate yBigDecimal.
     * @param yBigDecimal The y-coordinate of the line for which the cell row
     * index is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final long getCellRowIndex(
            BigDecimal yBigDecimal,
            boolean handleOutOfMemoryError) {
        try {
            long result = getCellRowIndex(yBigDecimal);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellRowIndex(
                        yBigDecimal,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param y_BigDecimal
     * @return Cell row Index for the cells that intersect the line with
     * y-coordinate yBigDecimal.
     */
    protected final long getCellRowIndex(
            BigDecimal y_BigDecimal) {
        BigDecimal yMinusMinY_BigDecimal = y_BigDecimal.subtract(
                this._Dimensions[2]);
        BigDecimal tmp;
        tmp = Generic_BigDecimal.divideRoundIfNecessary(yMinusMinY_BigDecimal, this._Dimensions[0], 0, RoundingMode.DOWN);
        return tmp.toBigInteger().longValue();
//        return yMinusMinY_BigDecimal.divide(
//                this._Dimensions[0]).toBigInteger().longValue();
    }

    /**
     * @param chunkRowIndex
     * @param chunkCellRowIndex
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return long CellRowIndex, the cell row index for the cells in chunk row
 index _Row chunk cell row index chunkCellRowIndex.
     */
    public final long getCellRowIndex(
            int chunkRowIndex,
            int chunkCellRowIndex,
            boolean handleOutOfMemoryError) {
        try {
            long result = getCellRowIndex(
                    chunkRowIndex,
                    chunkCellRowIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellRowIndex(
                        chunkRowIndex,
                        chunkCellRowIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @param chunkCellRowIndex
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @param chunkID This is a Grids_2D_ID_int for those AbstractGrid2DSquareCells not
 to be swapped if possible when an OutOfMemoryError is encountered.
     * @return long CellRowIndex, the cell row index for the cells in chunk row
 index _Row chunk cell row index chunkCellRowIndex.
     */
    public final long getCellRowIndex(
            int chunkRowIndex,
            int chunkCellRowIndex,
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            long result = getCellRowIndex(
                    chunkRowIndex,
                    chunkCellRowIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(this, chunkID, handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellRowIndex(
                        chunkRowIndex,
                        chunkCellRowIndex,
                        chunkID,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * If OutOfMemoryError is caught then an attempt is made to swap an
     * Grids_AbstractGrid2DSquareCellChunk not in
     * _Grid2DSquareCell_ChunkIDHashSet. If this is not done then any
     * Grids_AbstractGrid2DSquareCellChunk is swapped and a warning is printed
     * to sout
     *
     * @param chunkRowIndex
     * @param chunkCellRowIndex
     * @param a_Grid2DSquareCell_ChunkID_HashSet_HashMap
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return long CellRowIndex, the cell row index for the cells in chunk row
 index _Row chunk cell row index chunkCellRowIndex.
     */
    public final long getCellRowIndex(
            int chunkRowIndex,
            int chunkCellRowIndex,
            HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> a_Grid2DSquareCell_ChunkID_HashSet_HashMap,
            boolean handleOutOfMemoryError) {
        try {
            long result = getCellRowIndex(
                    chunkRowIndex,
                    chunkCellRowIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(a_Grid2DSquareCell_ChunkID_HashSet_HashMap, handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(a_Grid2DSquareCell_ChunkID_HashSet_HashMap, e);
                return getCellRowIndex(
                        chunkRowIndex,
                        chunkCellRowIndex,
                        a_Grid2DSquareCell_ChunkID_HashSet_HashMap,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @return CellRowIndex for the cells in chunk _Row, chunk cell
 column index chunkCellRowIndex.
     * @param chunkCellRowIndex
     */
    protected final long getCellRowIndex(
            int chunkRowIndex,
            int chunkCellRowIndex) {
        return ((long) chunkRowIndex * (long) this._ChunkNRows) + (long) chunkCellRowIndex;
    }

    /**
     * @param a_Random
     * @param handleOutOfMemoryError
     * @return A Random CellRowIndex.
     */
    public final long getCellRowIndex(
            Random a_Random,
            boolean handleOutOfMemoryError) {
        try {
            long result = getCellRowIndex(
                    a_Random);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellRowIndex(
                        a_Random,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param a_Random
     * @return A Random CellRowIndex.
     */
    protected final long getCellRowIndex(
            Random a_Random) {
        if (_NRows < Integer.MAX_VALUE) {
            return a_Random.nextInt((int) _NRows);
        } else {
            long row = 0;
            long rowMax = 0;
            while (rowMax < _NRows) {
                rowMax += Integer.MAX_VALUE;
                if (rowMax < _NRows) {
                    row += a_Random.nextInt();
                } else {
                    int rowInt = (int) (rowMax - _NRows);
                    if (rowInt > 0) {
                        row += a_Random.nextInt();
                    }
                }
            }
            return row;
        }
    }

    /**
     * @return Chunk cell row Index of the cells that intersects the line with
     * y-coordinate y.
     * @param y The y-coordinate of the line for which the chunk cell row index
     * is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkCellRowIndex(
            double y,
            boolean handleOutOfMemoryError) {
        try {
            int result = getChunkCellRowIndex(y);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getChunkCellRowIndex(
                        y,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return Chunk cell row Index of the cells that intersects the line with
     * y-coordinate y.
     * @param y The y-coordinate of the line for which the chunk cell row index
     * is returned.
     */
    protected final int getChunkCellRowIndex(
            double y) {
        return getChunkCellRowIndex(getCellRowIndex(y));
    }

    /**
     * @param a_CellRowIndex
     * @return Chunk cell row index of the cells with cell row index equal to
     * _CellRowIndex.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkCellRowIndex(
            long a_CellRowIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getChunkCellRowIndex(a_CellRowIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getChunkCellRowIndex(
                        a_CellRowIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return Chunk cell row index of the cells with cell row index equal to
     * _CellRowIndex.
     * @param cellRowIndex The cell row index of the cells that chunk cell row
     * index is returned.
     */
    protected final int getChunkCellRowIndex(
            long cellRowIndex) {
        return (int) (cellRowIndex - ((cellRowIndex / this._ChunkNRows) * this._ChunkNRows));
    }

    /**
     * @param a_CellRowIndex
     * @param a_CellColIndex
     * @return Grids_2D_ID_long of the cell given by cell row index _CellRowIndex, cell
     * column index _CellColIndex. A Grids_2D_ID_long is returned even if that cell would
     * not be in the grid.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long getCellID(
            long a_CellRowIndex,
            long a_CellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long result = getCellID(
                    a_CellRowIndex,
                    a_CellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellID(
                        a_CellRowIndex,
                        a_CellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param cellRowIndex
     * @param cellColIndex
     * @return Grids_2D_ID_long of the cell given by cell row index _CellRowIndex, cell
     * column index _CellColIndex. A Grids_2D_ID_long is returned even if that cell would
     * not be in the grid.
     * @param cellRowIndex The cell row index.
     */
    protected final Grids_2D_ID_long getCellID(
            long cellRowIndex,
            long cellColIndex) {
        return new Grids_2D_ID_long(cellRowIndex, cellColIndex);
    }

    /**
     * @return Grids_2D_ID_long of the cell given by x-coordinate x, y-coordinate y even
     * if that cell would not be in the grid.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long getCellID(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long result = getCellID(
                    x,
                    y);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellID(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long of the cell given by x-coordinate x, y-coordinate y even
     * if that cell would not be in the grid.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    protected final Grids_2D_ID_long getCellID(
            double x,
            double y) {
        return new Grids_2D_ID_long(
                getCellRowIndex(y),
                getCellColIndex(x));
    }

    /**
     * @return Grids_2D_ID_long of the cell given by x-coordinate x, y-coordinate y even
     * if that cell would not be in the grid.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long getCellID(
            BigDecimal x,
            BigDecimal y,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long result = getCellID(
                    x,
                    y);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellID(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long of the cell given by x-coordinate x, y-coordinate y even
     * if that cell would not be in the grid.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    protected final Grids_2D_ID_long getCellID(
            BigDecimal x,
            BigDecimal y) {
        return new Grids_2D_ID_long(
                getCellRowIndex(y),
                getCellColIndex(x));
    }

    /**
     * Attempts to write this instance to Files located in the _Directory
     * returned by get_Directory(). First attempts to do this without swapping
     * out data, but if this fails because an OutOfMemoryError is encountered
     * then it retires swapping out chunks as it goes.
     *
     * @param swapToFileCache Iff true then
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap is written to new
     * File( get_Directory(), "cache" ).
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @throws java.io.IOException
     */
    public final void writeToFile(
            boolean swapToFileCache,
            boolean handleOutOfMemoryError)
            throws IOException {
        try {
            writeToFile(swapToFileCache);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                writeToFileSwapping(
                        swapToFileCache);
                env.init_MemoryReserve(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Attempts to write this instance to Files located in the _Directory
     * returned by get_Directory(). Chunks are all written but no data is
     * swapped
     *
     * @param swapToFileCache Iff true then
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap is written to new
     * File(get_Directory(),"cache").
     * @throws java.io.IOException
     */
    protected void writeToFile(
            boolean swapToFileCache)
            throws IOException {
        try {
            writeToFileGrid2DSquareCellChunks();
            if (swapToFileCache) {
                // Write out thisCache
                File file = new File(
                        this._Directory,
                        "cache");
                ObjectOutputStream oos = new ObjectOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(file)));
                oos.writeObject(this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap);
                oos.flush();
                oos.close();
            }
            // Write out this.
            File file = new File(
                    this._Directory,
                    "thisFile");
            ObjectOutputStream oos = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(file)));
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch (IOException ioe0) {
            System.err.println(ioe0.getMessage());
            throw ioe0;
        }
    }

    /**
     * Attempts to write this instance to Files located in the _Directory
     * returned by get_Directory(). Chunks are all swapped to file.
     *
     * @param swapToFileCache Iff true then
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap is written to new
     * File(get_Directory(),"cache").
     * @throws java.io.IOException
     */
    protected void writeToFileSwapping(
            boolean swapToFileCache)
            throws IOException {
        try {
            swapToFile_Grid2DSquareCellChunks();
            if (swapToFileCache) {
                // Write out thisCache
                File file = new File(
                        this._Directory,
                        "cache");
                ObjectOutputStream oos = new ObjectOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(file)));
                oos.writeObject(this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap);
                oos.flush();
                oos.close();
            }
            // Write out this.
            File file = new File(
                    this._Directory,
                    "thisFile");
            ObjectOutputStream oos = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(file)));
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch (IOException ioe0) {
            System.err.println(ioe0.getMessage());
            throw ioe0;
        }
    }

    /**
     * Attempts to write to File a seriailized version of a
     * Grids_AbstractGrid2DSquareCellChunk in
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap. The first
     * Grids_AbstractGrid2DSquareCellChunk attempted to be written is that with
     * a chunk row index of 0, and a chunk column index of 0.
     *
     * @return Grids_2D_ID_int of the Grids_AbstractGrid2DSquareCellChunk which was
 swapped or null.
     */
    protected final Grids_2D_ID_int writeToFileGrid2DSquareCellChunk() {
        if (this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.isEmpty()) {
            return null;
        }
        Grids_2D_ID_int a_ChunkID = this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.keySet().iterator().next();
        writeToFileGrid2DSquareCellChunk(a_ChunkID);
        return a_ChunkID;
    }

    /**
     * Attempts to write to File a seriailized version of the
 Grids_AbstractGrid2DSquareCellChunk in
 this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap given by; chunk row
 index _Row, chunk column index _Col.
     *
     * @param chunkRowIndex The chunk row index of the
     * Grids_AbstractGrid2DSquareCellChunk to be written.
     * @param chunkColIndex The chunk column index of the
     * Grids_AbstractGrid2DSquareCellChunk to be written.
     * @return True if Grids_AbstractGrid2DSquareCellChunk on file is up to
     * date.
     */
    protected final boolean writeToFileGrid2DSquareCellChunk(
            int chunkRowIndex,
            int chunkColIndex) {
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                chunkRowIndex,
                chunkColIndex);
        return writeToFileGrid2DSquareCellChunk(chunkID);
    }

    /**
     * Attempts to write to File a seriailized version of the
     * Grids_AbstractGrid2DSquareCellChunk in
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap given by _ChunkID.
     *
     * @param a_ChunkID The _ChunkID of the Grids_AbstractGrid2DSquareCellChunk
     * to be written.
     * @return True if Grids_AbstractGrid2DSquareCellChunk on file is up to
     * date.
     */
    protected final boolean writeToFileGrid2DSquareCellChunk(
            Grids_2D_ID_int a_ChunkID) {
        boolean result = true;
        Grids_AbstractGrid2DSquareCellChunk grid2DSquareCellChunk = this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.get(a_ChunkID);
        if (grid2DSquareCellChunk != null) {
            if (!grid2DSquareCellChunk.getIsSwapUpToDate()) {
                File file = new File(
                        this._Directory,
                        a_ChunkID.getRow() + "_" + a_ChunkID.getCol());
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                    ObjectOutputStream oos = new ObjectOutputStream(
                            new BufferedOutputStream(
                                    new FileOutputStream(file)));
                    oos.writeObject(grid2DSquareCellChunk);
                    oos.flush();
                    oos.close();
                } catch (IOException ioe0) {
                    //ioe0.printStackTrace();
                    System.err.println(ioe0.getMessage());
                }
                System.gc();
                grid2DSquareCellChunk.setIsSwapUpToDate(true);
            }
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Attempts to write to File seriailized versions of all
     * Grids_AbstractGrid2DSquareCellChunk in
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.
     */
    protected final void writeToFileGrid2DSquareCellChunks() {
        Iterator iterator = this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.keySet().iterator();
        Grids_2D_ID_int a_ChunkID;
        while (iterator.hasNext()) {
            a_ChunkID = (Grids_2D_ID_int) iterator.next();
            if (isInCache(a_ChunkID)) {
                writeToFileGrid2DSquareCellChunk(a_ChunkID);
            }
        }
    }

    /**
     * Attempts to write to File seriailized versions of those
 Grids_AbstractGrid2DSquareCellChunk in
 this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap that have ID
 equal to those in _ChunkIDs.
     *
     * @param a_ChunkID_HashSet A HashSet containing the Grids_2D_ID_int of the
 Grids_AbstractGrid2DSquareCellChunk to be written to file.
     */
    protected final void writeToFileGrid2DSquareCellChunks(
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet) {
        Iterator<Grids_2D_ID_int> iterator = a_ChunkID_HashSet.iterator();
        Grids_2D_ID_int a_ChunkID;
        while (iterator.hasNext()) {
            a_ChunkID = iterator.next();
            if (isInCache(a_ChunkID)) {
                writeToFileGrid2DSquareCellChunk(a_ChunkID);
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache a
     * Grids_AbstractGrid2DSquareCellChunk in this. This is one of the lowest
     * level memory handling operation of this class.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return HashMap<Grids_AbstractGrid2DSquareCell,ID> for accounting
     * what was swapped.
     */
    public HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunk_AccountDetail(
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunk_AccountDetail();
            HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> potentialPartResult
                    = env.tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(handleOutOfMemoryError);
            env.combine(
                    result,
                    potentialPartResult);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunk_AccountDetail();
                if (result.isEmpty()) {
                    throw a_OutOfMemoryError;
                }
                HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> potentialPartResult = env.init_MemoryReserve_AccountDetail(
                        handleOutOfMemoryError);
                env.combine(result,
                        potentialPartResult);
                potentialPartResult = swapToFile_Grid2DSquareCellChunk_AccountDetail(
                        handleOutOfMemoryError);
                env.combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunk_AccountDetail() {
        HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result = new HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>>(1);
        Grids_2D_ID_int a_ChunkID = writeToFileGrid2DSquareCellChunk();
        clearFromCacheGrid2DSquareCellChunk(a_ChunkID);
        HashSet<Grids_2D_ID_int> a_ChunkID_Hashset = new HashSet<Grids_2D_ID_int>(1);
        a_ChunkID_Hashset.add(a_ChunkID);
        result.put(
                this,
                a_ChunkID_Hashset);
        return result;
    }

    /**
     * @param handleOutOfMemoryError
     * @return
     */
    public Grids_2D_ID_int swapToFile_Grid2DSquareCellChunk_AccountChunk(
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_int result = swapToFile_Grid2DSquareCellChunk_AccountChunk();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                Grids_2D_ID_int result = swapToFile_Grid2DSquareCellChunk_AccountChunk();
                if (result == null) {
                    if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                        throw a_OutOfMemoryError;
                    }
                }
                env.init_MemoryReserve(
                        handleOutOfMemoryError);
                return swapToFile_Grid2DSquareCellChunk_AccountChunk(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected Grids_2D_ID_int swapToFile_Grid2DSquareCellChunk_AccountChunk() {
        Grids_2D_ID_int a_ChunkID = writeToFileGrid2DSquareCellChunk();
        if (a_ChunkID != null) {
            clearFromCacheGrid2DSquareCellChunk(a_ChunkID);
        }
        return a_ChunkID;
    }

    protected long swapToFile_Grid2DSquareCellChunks_Account() {
        long result = 0L;
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri
                < this._NChunkRows; cri++) {
            for (cci = 0; cci
                    < this._NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(
                        cri,
                        cci);
                if (writeToFileGrid2DSquareCellChunk(chunkID)) {
                    clearFromCacheGrid2DSquareCellChunk(chunkID);
                    result++;
                }
            }
        }
        return result;
    }

    protected long swapToFile_Grid2DSquareCellChunks_Account(Set<Grids_2D_ID_int> b_ChunkID_Set) {
        long result = 0L;
        Iterator<Grids_2D_ID_int> a_Iterator = b_ChunkID_Set.iterator();
        Grids_2D_ID_int a_ChunkID;
        while (a_Iterator.hasNext()) {
            a_ChunkID = a_Iterator.next();
            if (writeToFileGrid2DSquareCellChunk(a_ChunkID)) {
                clearFromCacheGrid2DSquareCellChunk(a_ChunkID);
                result++;
            }
        }
        return result;
    }

    public Grids_2D_ID_int swapToFile_Grid2DSquareCellChunkExcept_AccountChunk(
            HashSet<Grids_2D_ID_int> chunkIDs,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_int result = swapToFile_Grid2DSquareCellChunkExcept_AccountChunk(chunkIDs);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                Grids_2D_ID_int result = swapToFile_Grid2DSquareCellChunkExcept_AccountChunk(chunkIDs);
                if (result == null) {
                    if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                        throw a_OutOfMemoryError;
                    }
                }
                env.init_MemoryReserve(
                        handleOutOfMemoryError);

                return result;

            } else {
                throw a_OutOfMemoryError;

            }
        }
    }

    public Grids_2D_ID_int swapToFile_Grid2DSquareCellChunkExcept_AccountChunk(
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet) {
        Grids_2D_ID_int result = null;
        int cri;
        int cci;
        Grids_2D_ID_int chunkID = null;
        for (cri = 0; cri
                < this._NChunkRows; cri++) {
            for (cci = 0; cci
                    < this._NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(
                        cri,
                        cci);
                if (!a_ChunkID_HashSet.contains(chunkID)) {
                    if (isInCache(chunkID)) {
                        writeToFileGrid2DSquareCellChunk(chunkID);
                        clearFromCacheGrid2DSquareCellChunk(
                                chunkID);

                        return chunkID;

                    }
                }
            }
        }
        return result;
    }

    public void swapToFile_Grid2DSquareCellChunk(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            swapToFile_Grid2DSquareCellChunk(a_ChunkID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(
                        handleOutOfMemoryError);
                swapToFile_Grid2DSquareCellChunk(
                        a_ChunkID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;

            }
        }
    }

    protected void swapToFile_Grid2DSquareCellChunk(
            Grids_2D_ID_int a_ChunkID) {
        if (writeToFileGrid2DSquareCellChunk(a_ChunkID)) {
            clearFromCacheGrid2DSquareCellChunk(a_ChunkID);
        }
    }

    /**
     * Attempts to write to file and clear from the cache a
     * Grids_AbstractGrid2DSquareCellChunk in this. This is one of the lowest
     * level memory handling operation of this class.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public void swapToFile_Grid2DSquareCellChunk(
            boolean handleOutOfMemoryError) {
        try {
            swapToFile_Grid2DSquareCellChunk();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(
                        handleOutOfMemoryError);
                swapToFile_Grid2DSquareCellChunk(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected void swapToFile_Grid2DSquareCellChunk() {
        Grids_2D_ID_int a_ChunkID = writeToFileGrid2DSquareCellChunk();
        if (a_ChunkID != null) {
            clearFromCacheGrid2DSquareCellChunk(a_ChunkID);
        }
    }

    public long swapToFile_Grid2DSquareCellChunk_Account(
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_Grid2DSquareCellChunk_Account();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                long result = swapToFile_Grid2DSquareCellChunk_Account();
                if (result < 1L) {
                    if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                        throw e;
                    }
                }
                result += env.init_MemoryReserve_Account(
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected long swapToFile_Grid2DSquareCellChunk_Account() {
        Grids_2D_ID_int a_ChunkID = writeToFileGrid2DSquareCellChunk();
        if (a_ChunkID != null) {
            clearFromCacheGrid2DSquareCellChunk(a_ChunkID);
            return 1L;
        }
        return 0L;
    }

    /**
     * Attempts to write to file and clear from the cache a
     * Grids_AbstractGrid2DSquareCellChunk in
     * this._AbstractGrid2DSquareCell_HashSet.
     *
     * @param chunkID A Grids_2D_ID_int not to be swapped
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return The Grids_2D_ID_int of Grids_AbstractGrid2DSquareCellChunk swapped or
 null.
     */
    public HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(chunkID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result;
                result = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                        chunkID);
                if (result.isEmpty()) {
                    result = env.swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                            this,
                            chunkID);
                }
                HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> potentialPartResult
                        = env.init_MemoryReserve_AccountDetail(
                                this,
                                chunkID,
                                handleOutOfMemoryError);
                Grids_Environment.combine(result,
                        potentialPartResult);
                potentialPartResult = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                        chunkID);
                Grids_Environment.combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;

            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache a
     * Grids_AbstractGrid2DSquareCellChunk in
     * this._AbstractGrid2DSquareCell_HashSet.
     *
     * @param chunkID
     * @return The Grids_2D_ID_int of Grids_AbstractGrid2DSquareCellChunk swapped or
 null.
     */
    protected HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
            Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result = new HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>>(1);

        int cri;

        int cci;
        Grids_2D_ID_int b_ChunkID = null;

        for (cri = 0; cri
                < this._NChunkRows; cri++) {
            for (cci = 0; cci
                    < this._NChunkCols; cci++) {
                b_ChunkID = new Grids_2D_ID_int(
                        cri,
                        cci);
                if (!b_ChunkID.equals(chunkID)) {
                    if (isInCache(b_ChunkID)) {
                        writeToFileGrid2DSquareCellChunk(b_ChunkID);
                        clearFromCacheGrid2DSquareCellChunk(
                                b_ChunkID);
                        HashSet<Grids_2D_ID_int> a_ChunkID_HashSet = new HashSet<Grids_2D_ID_int>(1);
                        a_ChunkID_HashSet.add(b_ChunkID);
                        result.put(this, a_ChunkID_HashSet);
                        return result;
                    }
                }
            }
        }
        return result;

    }

    public Grids_2D_ID_int swapToFile_Grid2DSquareCellChunkExcept_AccountChunk(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_int result = swapToFile_Grid2DSquareCellChunkExcept_AccountChunk(
                    a_ChunkID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                Grids_2D_ID_int result = swapToFile_Grid2DSquareCellChunkExcept_AccountChunk(
                        a_ChunkID);
                if (result == null) {
                    if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                            this,
                            a_ChunkID) < 1L) {
                        throw e;
                    }
                }
                env.init_MemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected Grids_2D_ID_int swapToFile_Grid2DSquareCellChunkExcept_AccountChunk(
            Grids_2D_ID_int a_ChunkID) {
        Grids_2D_ID_int result = null;

        int cri;

        int cci;

        for (cri = 0; cri
                < this._NChunkRows; cri++) {
            for (cci = 0; cci
                    < this._NChunkCols; cci++) {
                result = new Grids_2D_ID_int(
                        cri,
                        cci);

                if (!result.equals(a_ChunkID)) {
                    if (isInCache(result)) {
                        writeToFileGrid2DSquareCellChunk(result);
                        clearFromCacheGrid2DSquareCellChunk(
                                result);

                        return result;

                    }
                }
            }
        }
        return result;

    }

    /**
     * Attempts to write to file and clear from the cache all
 Grid2DSquareCellChunkAbstracts in this except that with ID
 a_ChunkID.
     *
     * @param a_ChunkID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGrid2DSquareCellChunk swapped.
     */
    public final HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(a_ChunkID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);

            return result;

        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                        a_ChunkID);

                if (result.isEmpty()) {
                    result = env.swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
                            this,
                            a_ChunkID);

                    if (result.isEmpty()) {
                        throw a_OutOfMemoryError;

                    }
                }
                HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> potentialPartResult
                        = env.init_MemoryReserve_AccountDetail(
                                this,
                                a_ChunkID,
                                handleOutOfMemoryError);
                env.combine(result,
                        potentialPartResult);
                potentialPartResult = swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
                        a_ChunkID,
                        handleOutOfMemoryError);
                env.combine(result,
                        potentialPartResult);

                return result;

            } else {
                throw a_OutOfMemoryError;

            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache all
 Grid2DSquareCellChunkAbstracts in this except that with Grids_2D_ID_int _ChunkID.
     *
     * @param _ChunkID
     * @return A HashSet with the ChunkIDs of those
     * Grids_AbstractGrid2DSquareCellChunk swapped.
     */
    protected final HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
            Grids_2D_ID_int _ChunkID) {
        // Using default as not sure if what to use as initialCapacity for result...
        HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result = new HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>>(1);
        HashSet<Grids_2D_ID_int> a_HashSet_ChunkID = new HashSet<Grids_2D_ID_int>();

        int cri;

        int cci;
        Grids_2D_ID_int a_ChunkIDToSwap = null;

        for (cri = 0; cri
                < this._NChunkRows; cri++) {
            for (cci = 0; cci
                    < this._NChunkCols; cci++) {
                a_ChunkIDToSwap = new Grids_2D_ID_int(
                        cri,
                        cci);

                if (!_ChunkID.equals(a_ChunkIDToSwap)) {
                    if (isInCache(a_ChunkIDToSwap)) {
                        writeToFileGrid2DSquareCellChunk(a_ChunkIDToSwap);
                        clearFromCacheGrid2DSquareCellChunk(
                                a_ChunkIDToSwap);
                        a_HashSet_ChunkID.add(a_ChunkIDToSwap);

                    }
                }
            }
        }
        result.put(
                this,
                a_HashSet_ChunkID);

        return result;

    }

    public void swapToFile_Grid2DSquareCellChunks(
            boolean handleOutOfMemoryError) {
        try {
            swapToFile_Grid2DSquareCellChunks();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                swapToFile_Grid2DSquareCellChunks(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected void swapToFile_Grid2DSquareCellChunks() {
        int cri;
        int cci;
        Grids_2D_ID_int chunkID = null;
        for (cri = 0; cri
                < this._NChunkRows; cri++) {
            for (cci = 0; cci
                    < this._NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(
                        cri,
                        cci);
                if (isInCache(chunkID)) {
                    writeToFileGrid2DSquareCellChunk(chunkID);
                    clearFromCacheGrid2DSquareCellChunk(
                            chunkID);
                }
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache all
 Grid2DSquareCellChunkAbstracts in this except that with ID
 _ChunkIDs.
     *
     * @param a_ChunkID_HashSet
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGrid2DSquareCellChunk swapped.
     */
    public long swapToFile_Grid2DSquareCellChunksExcept_Account(
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_Grid2DSquareCellChunksExcept_Account(a_ChunkID_HashSet);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                long result = env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this,
                        a_ChunkID_HashSet);
                if (result < 1L) {
                    result = env.swapToFile_Grid2DSquareCellChunkExcept_Account(this, a_ChunkID_HashSet);
                    if (result < 1L) {
                        throw a_OutOfMemoryError;
                    }
                }
                result += env.init_MemoryReserve_Account(
                        this,
                        a_ChunkID_HashSet,
                        handleOutOfMemoryError);
                result += swapToFile_Grid2DSquareCellChunksExcept_Account(
                        a_ChunkID_HashSet,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache all
 Grid2DSquareCellChunkAbstracts in this except that with Grids_2D_ID_int _ChunkID.
     *
     * @param a_ChunkID_HashSet HashSet of
     * Grids_AbstractGrid2DSquareCellChunk.ChunkIDs not to be swapped.
     * @return A HashSet with the ChunkIDs of those
     * Grids_AbstractGrid2DSquareCellChunk swapped.
     */
    protected final HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunksExcept_AccountDetail(
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet) {
        HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result
                = new HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>>(1);
        HashSet<Grids_2D_ID_int> b_ChunkID_HashSet = new HashSet<Grids_2D_ID_int>();
        int cri;
        int cci;
        Grids_2D_ID_int chunkID = null;
        for (cri = 0; cri
                < this._NChunkRows; cri++) {
            for (cci = 0; cci
                    < this._NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(
                        cri,
                        cci);
                if (!a_ChunkID_HashSet.contains(chunkID)) {
                    if (isInCache(chunkID)) {
                        writeToFileGrid2DSquareCellChunk(chunkID);
                        clearFromCacheGrid2DSquareCellChunk(
                                chunkID);
                        b_ChunkID_HashSet.add(chunkID);
                    }
                }
            }
        }
        result.put(this,
                b_ChunkID_HashSet);
        return result;
    }

    protected long swapToFile_Grid2DSquareCellChunkExcept_Account(
            Grids_2D_ID_int a_ChunkID) {
        int cri;
        int cci;
        Grids_2D_ID_int chunkID = null;
        for (cri = 0; cri
                < this._NChunkRows; cri++) {
            for (cci = 0; cci
                    < this._NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(
                        cri,
                        cci);
                if (a_ChunkID != chunkID) {
                    if (isInCache(a_ChunkID)) {
                        writeToFileGrid2DSquareCellChunk(a_ChunkID);
                        clearFromCacheGrid2DSquareCellChunk(
                                a_ChunkID);
                        return 1L;
                    }
                }
            }
        }
        return 0L;
    }

    protected long swapToFile_Grid2DSquareCellChunksExcept_Account(
            Grids_2D_ID_int a_ChunkID) {
        long result = 0L;
        int cri;
        int cci;
        Grids_2D_ID_int b_ChunkID = null;
        for (cri = 0; cri
                < this._NChunkRows; cri++) {
            for (cci = 0; cci
                    < this._NChunkCols; cci++) {
                b_ChunkID = new Grids_2D_ID_int(
                        cri,
                        cci);
                if (a_ChunkID != b_ChunkID) {
                    if (isInCache(a_ChunkID)) {
                        writeToFileGrid2DSquareCellChunk(a_ChunkID);
                        clearFromCacheGrid2DSquareCellChunk(
                                a_ChunkID);
                        result++;
                    }
                }
            }
        }
        return result;
    }

    protected long swapToFile_Grid2DSquareCellChunksExcept_Account(
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet) {
        long result = 0L;
        int cri;
        int cci;
        Grids_2D_ID_int chunkID = null;
        for (cri = 0; cri
                < this._NChunkRows; cri++) {
            for (cci = 0; cci
                    < this._NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(
                        cri,
                        cci);
                if (!a_ChunkID_HashSet.contains(chunkID)) {
                    if (isInCache(chunkID)) {
                        writeToFileGrid2DSquareCellChunk(chunkID);
                        clearFromCacheGrid2DSquareCellChunk(
                                chunkID);
                        result++;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Attempts to write to file and clear from the cache a
     * Grids_AbstractGrid2DSquareCellChunk in
     * this._AbstractGrid2DSquareCell_HashSet.
     *
     * @param a_ChunkID_HashSet
     * @return The Grids_2D_ID_int of the Grids_AbstractGrid2DSquareCellChunk swapped or
 null.
     */
    protected final HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet) {
        HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>>(1);
        //HashSet<ChunkID> result_ChunkID_HashSet = new HashSet<ChunkID>(1);
        int cri;
        int cci;
        Grids_2D_ID_int a_ChunkID = null;
        for (cri = 0; cri
                < this._NChunkRows; cri++) {
            for (cci = 0; cci
                    < this._NChunkCols; cci++) {
                a_ChunkID = new Grids_2D_ID_int(
                        cri,
                        cci);
                if (!a_ChunkID_HashSet.contains(a_ChunkID)) {
                    if (isInCache(a_ChunkID)) {
                        writeToFileGrid2DSquareCellChunk(a_ChunkID);
                        clearFromCacheGrid2DSquareCellChunk(
                                a_ChunkID);
                        //result_ChunkID_HashSet.add(a_ChunkID);
                        result.put(this, a_ChunkID_HashSet);
                        return result;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Attempts to swap seriailsed version of all Grid2DSquareCellChunks in this
     * only. To swap seriailsed version of all Grid2DSquareCellChunks in
     * grid2DsquareCells use
     * <code>swapToFileGrid2DSquareCellGrid2DSquareCellChunks(boolean)</code>
     * Swapping involves writing to files and then clearing them from the cache.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGrid2DSquareCellChunk swapped.
     */
    public final HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunks_AccountDetail(
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunks_AccountDetail();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result = swapToFile_Grid2DSquareCellChunks_AccountDetail();
                if (result.isEmpty()) {
                    result = env.swapToFile_Grid2DSquareCellChunk_AccountDetail();
                    if (result.isEmpty()) {
                        throw a_OutOfMemoryError;
                    }
                }
                HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> potentialPartResult
                        = env.init_MemoryReserve_AccountDetail(
                                handleOutOfMemoryError);
                env.combine(result,
                        potentialPartResult);
                potentialPartResult = swapToFile_Grid2DSquareCellChunks_AccountDetail(
                        handleOutOfMemoryError);
                env.combine(result,
                        potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Attempts to swap seriailsed version of all
     * _ChunkID_AbstractGrid2DSquareCellChunk_HashMap. This involves writing
     * them to files and then clearing them from the cache.
     *
     * @return The number of Grids_AbstractGrid2DSquareCellChunk swapped.
     */
    protected HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> swapToFile_Grid2DSquareCellChunks_AccountDetail() {
        HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> result = new HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>>(1);
        HashSet<Grids_2D_ID_int> a_ChunkID_HashSet = new HashSet<Grids_2D_ID_int>();
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri
                < this._NChunkRows; cri++) {
            for (cci = 0; cci
                    < this._NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(
                        cri,
                        cci);
                if (isInCache(chunkID)) {
                    writeToFileGrid2DSquareCellChunk(chunkID);
                    clearFromCacheGrid2DSquareCellChunk(
                            chunkID);
                    a_ChunkID_HashSet.add(chunkID);
                }
            }
        }
        if (a_ChunkID_HashSet.isEmpty()) {
            return result;
        }
        result.put(
                this,
                a_ChunkID_HashSet);
        return result;
    }

    /**
     * Attempts to swap seriailsed version of all
     * Grids_AbstractGrid2DSquareCellChunk from (cri0, cci0) to (cri1, cci1) in
     * row major order. This involves writing them to files and then clearing
     * them from the cache.
     *
     * @param cri0 The chunk row index of the first
     * Grids_AbstractGrid2DSquareCellChunk to be swapped.
     * @param cci0 The chunk column index of the first
     * Grids_AbstractGrid2DSquareCellChunk to be swapped.
     * @param cri1 The chunk row index of the last
     * Grids_AbstractGrid2DSquareCellChunk to be swapped.
     * @param cci1 The chunk column index of the last
     * Grids_AbstractGrid2DSquareCellChunk to be swapped.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGrid2DSquareCellChunk swapped.
     */
    public final long swapToFile_Grid2DSquareCellChunks_Account(
            int cri0,
            int cci0,
            int cri1,
            int cci1,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_Grid2DSquareCellChunks_Account(
                    cri0,
                    cci0,
                    cri1,
                    cci1);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                long result = swapToFile_Grid2DSquareCellChunk_Account();
                if (result < 1) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                result += swapToFile_Grid2DSquareCellChunks_Account(
                        cri0,
                        cci0,
                        cri1,
                        cci1,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Attempts to swap seriailsed version of all
     * Grids_AbstractGrid2DSquareCellChunk from (cri0, cci0) to (cri1, cci1) in
     * row major order. This involves writing them to files and then clearing
     * them from the cache.
     *
     * @param cri0 The chunk row index of the first Grid2DSquareCellChunks to be
     * swapped.
     * @param cci0 The chunk column index of the first Grid2DSquareCellChunks to
     * be swapped.
     * @param cri1 The chunk row index of the last Grid2DSquareCellChunks to be
     * swapped.
     * @param cci1 The chunk column index of the last Grid2DSquareCellChunks to
     * be swapped.
     * @return The number of Grids_AbstractGrid2DSquareCellChunk swapped.
     */
    protected final long swapToFile_Grid2DSquareCellChunks_Account(
            int cri0,
            int cci0,
            int cri1,
            int cci1) {
        Grids_2D_ID_int chunkID = null;
        long result = 0L;
        if (cri0 != cri1) {
            for (int cci = cci0; cci
                    < this._NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(
                        cri0,
                        cci);
                if (isInCache(chunkID)) {
                    writeToFileGrid2DSquareCellChunk(chunkID);
                    clearFromCacheGrid2DSquareCellChunk(
                            chunkID);
                    result++;
                }
            }
            for (int cri = cri0 + 1; cri
                    < cri1; cri++) {
                for (int cci = 0; cci
                        < this._NChunkCols; cci++) {
                    chunkID = new Grids_2D_ID_int(
                            cri,
                            cci);
                    if (isInCache(chunkID)) {
                        writeToFileGrid2DSquareCellChunk(chunkID);
                        clearFromCacheGrid2DSquareCellChunk(
                                chunkID);
                        result++;
                    }
                }
            }
            for (int cci = 0; cci
                    < cci1; cci++) {
                chunkID = new Grids_2D_ID_int(
                        cri1,
                        cci);
                if (isInCache(chunkID)) {
                    writeToFileGrid2DSquareCellChunk(chunkID);
                    clearFromCacheGrid2DSquareCellChunk(
                            chunkID);
                    result++;
                }
            }
        } else {
            for (int cci = cci0; cci
                    < cci1 + 1; cci++) {
                chunkID = new Grids_2D_ID_int(
                        cri0,
                        cci);
                if (isInCache(chunkID)) {
                    writeToFileGrid2DSquareCellChunk(chunkID);
                    clearFromCacheGrid2DSquareCellChunk(
                            chunkID);
                    result++;

                }
            }
        }
        return result;
    }

    /**
     * @return true iff grid2DSquareCellChunk given by _ChunkID is swapToFiled.
     * This must be an upToDate swapToFile.
     * @param a_ChunkID The Grids_2D_ID_int of the grid2DSquareCellChunk tested to see
 if it is swapToFiled.
     */
    protected final boolean isInCache(
            Grids_2D_ID_int a_ChunkID) {
        return this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.containsKey(a_ChunkID);
    }

    /**
     * For releasing a grid2DSquareCellChunk stored in memory. This is usually
 only done after the equivallent of swapToFileChunk(ID) has been
 called.
     *
     * @param a_ChunkID The Grids_2D_ID_int of the grid2DSquareCellChunk to be cleared.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected final void clearFromCacheGrid2DSquareCellChunk(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            clearFromCacheGrid2DSquareCellChunk(a_ChunkID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                clearFromCacheGrid2DSquareCellChunk(
                        a_ChunkID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For releasing a grid2DSquareCellChunk stored in memory. This is usually
 only done after the equivallent of swapToFileChunk(ID) has been
 called.
     *
     * @param a_ChunkID The Grids_2D_ID_int of the grid2DSquareCellChunk to be cleared.
     */
    protected final void clearFromCacheGrid2DSquareCellChunk(
            Grids_2D_ID_int a_ChunkID) {
        this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.remove(a_ChunkID);
        System.gc();
    }

    /**
     * For releasing all Grids_AbstractGrid2DSquareCellChunk in
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected final void clearFromCacheGrid2DSquareCellChunks(
            boolean handleOutOfMemoryError) {
        try {
            clearFromCacheGrid2DSquareCellChunks();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                clearFromCacheGrid2DSquareCellChunks(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For releasing all Grids_AbstractGrid2DSquareCellChunk in
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.
     */
    protected final void clearFromCacheGrid2DSquareCellChunks() {
        _ChunkID_AbstractGrid2DSquareCellChunk_HashMap = new HashMap<Grids_2D_ID_int, Grids_AbstractGrid2DSquareCellChunk>();
        System.gc();
    }

    /**
     * Attempts to load into the memory cache a
 Grids_AbstractGrid2DSquareCellChunk with ID
     *
     * @param a_ChunkID The Grids_2D_ID_int of the grid2DSquareCellChunk to be restored.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final void loadIntoCacheChunk(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            loadIntoCacheChunk(a_ChunkID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(a_ChunkID, handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                loadIntoCacheChunk(
                        a_ChunkID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Attempts to load into the memory cache
 Grids_AbstractGrid2DSquareCellChunk with Grids_2D_ID_int equal to _ChunkID.
     *
     *
     * @param a_ChunkID The Grids_2D_ID_int of the Grids_AbstractGrid2DSquareCellChunk
 to be restored.
     */
    protected final void loadIntoCacheChunk(
            Grids_2D_ID_int a_ChunkID) {
        boolean isInCache = isInCache(
                a_ChunkID);
        if (!isInCache) {
            File file = new File(
                    this._Directory,
                    "" + a_ChunkID.getRow()
                    + "_" + a_ChunkID.getCol());

            Object a_Object = Generic_StaticIO.readObject(file);
            if (this.getClass() == Grids_Grid2DSquareCellInt.class) {
                if (a_Object.getClass() == Grids_Grid2DSquareCellIntChunk64CellMap.class) {
                    Grids_Grid2DSquareCellIntChunk64CellMap grid2DSquareCellIntChunk64CellMap
                            = (Grids_Grid2DSquareCellIntChunk64CellMap) a_Object;
                    grid2DSquareCellIntChunk64CellMap.initGrid2DSquareCell(
                            this);
                    grid2DSquareCellIntChunk64CellMap.initChunkID(a_ChunkID);
                    this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                            a_ChunkID,
                            grid2DSquareCellIntChunk64CellMap);
                    this.env.dataToSwap = true;
                    return;
                }
                if (a_Object.getClass() == Grids_Grid2DSquareCellIntChunkArray.class) {
                    Grids_Grid2DSquareCellIntChunkArray grid2DSquareCellIntChunkArray
                            = (Grids_Grid2DSquareCellIntChunkArray) a_Object;
                    grid2DSquareCellIntChunkArray.initGrid2DSquareCell(
                            this);
                    grid2DSquareCellIntChunkArray.initChunkID(a_ChunkID);
                    this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                            a_ChunkID,
                            grid2DSquareCellIntChunkArray);
                    this.env.dataToSwap = true;
                    return;
                }
                if (a_Object.getClass() == Grids_Grid2DSquareCellIntChunkMap.class) {
                    Grids_Grid2DSquareCellIntChunkMap grid2DSquareCellIntChunkMap
                            = (Grids_Grid2DSquareCellIntChunkMap) a_Object;
                    grid2DSquareCellIntChunkMap.initGrid2DSquareCell(
                            this);
                    grid2DSquareCellIntChunkMap.initChunkID(a_ChunkID);
                    this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                            a_ChunkID,
                            grid2DSquareCellIntChunkMap);
                    this.env.dataToSwap = true;
                    return;
                }
                if (a_Object.getClass() == Grids_Grid2DSquareCellIntChunkJAI.class) {
                    Grids_Grid2DSquareCellIntChunkJAI grid2DSquareCellIntChunkJAI
                            = (Grids_Grid2DSquareCellIntChunkJAI) a_Object;
                    grid2DSquareCellIntChunkJAI.initGrid2DSquareCell(
                            this);
                    grid2DSquareCellIntChunkJAI.initChunkID(a_ChunkID);
                    this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                            a_ChunkID,
                            grid2DSquareCellIntChunkJAI);
                    this.env.dataToSwap = true;
                    return;
                }
                System.err.println(
                        "Unrecognised type of Grid2DSquareCellIntChunkAbstract or null "
                        + this.getClass().getName()
                        + ".loadIntoCacheChunk( ChunkID( " + a_ChunkID.toString() + " ) )");
            } else {
                if (a_Object.getClass() == Grids_Grid2DSquareCellDoubleChunk64CellMap.class) {
                    Grids_Grid2DSquareCellDoubleChunk64CellMap grid2DSquareCellDoubleChunk64CellMap
                            = (Grids_Grid2DSquareCellDoubleChunk64CellMap) a_Object;
                    grid2DSquareCellDoubleChunk64CellMap.initGrid2DSquareCell(
                            this);
                    grid2DSquareCellDoubleChunk64CellMap.initChunkID(a_ChunkID);
                    this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                            a_ChunkID,
                            grid2DSquareCellDoubleChunk64CellMap);
                    this.env.dataToSwap = true;
                    return;
                }
                if (a_Object.getClass() == Grids_Grid2DSquareCellDoubleChunkArray.class) {
                    Grids_Grid2DSquareCellDoubleChunkArray grid2DSquareCellDoubleChunkArray
                            = (Grids_Grid2DSquareCellDoubleChunkArray) a_Object;
                    grid2DSquareCellDoubleChunkArray.initGrid2DSquareCell(
                            this);
                    grid2DSquareCellDoubleChunkArray.initChunkID(a_ChunkID);
                    this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                            a_ChunkID,
                            grid2DSquareCellDoubleChunkArray);
                    this.env.dataToSwap = true;
                    return;
                }
                if (a_Object.getClass() == Grids_Grid2DSquareCellDoubleChunkMap.class) {
                    Grids_Grid2DSquareCellDoubleChunkMap grid2DSquareCellDoubleChunkMap
                            = (Grids_Grid2DSquareCellDoubleChunkMap) a_Object;
                    grid2DSquareCellDoubleChunkMap.initGrid2DSquareCell(
                            this);
                    grid2DSquareCellDoubleChunkMap.initChunkID(a_ChunkID);
                    this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                            a_ChunkID,
                            grid2DSquareCellDoubleChunkMap);
                    this.env.dataToSwap = true;
                    return;
                }
                if (a_Object.getClass() == Grids_Grid2DSquareCellDoubleChunkJAI.class) {
                    Grids_Grid2DSquareCellDoubleChunkJAI grid2DSquareCellDoubleChunkJAI
                            = (Grids_Grid2DSquareCellDoubleChunkJAI) a_Object;
                    grid2DSquareCellDoubleChunkJAI.initGrid2DSquareCell(
                            this);
                    grid2DSquareCellDoubleChunkJAI.initChunkID(a_ChunkID);
                    this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.put(
                            a_ChunkID,
                            grid2DSquareCellDoubleChunkJAI);
                    this.env.dataToSwap = true;
                    return;
                }
                System.err.println(
                        "Unrecognised type of Grid2DSquareCellDoubleChunkAbstract or null "
                        + this.getClass().getName()
                        + ".loadIntoCacheChunk( ChunkID( " + a_ChunkID.toString() + " ) )");
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long[] - the cell IDs for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long[] getCellIDs(
            double x,
            double y,
            double distance,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long[] result = getCellIDs(
                    x,
                    y,
                    distance);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellIDs(
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
     * @return a Grids_2D_ID_long[] - the cell IDs for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    protected final Grids_2D_ID_long[] getCellIDs(
            double x,
            double y,
            double distance) {
        return getCellIDs(
                x,
                y,
                getCellRowIndex(y),
                getCellColIndex(x),
                distance);
    }

    /**
     * @return a Grids_2D_ID_long[] - the cell IDs for cells thats centroids would be
     * intersected by circle with centre at centroid of cell given by cell row
     * index _CellRowIndex, cell column index _CellColIndex, and radius
     * distance.
     * @param cellRowIndex the row index for the cell thats centroid is the
     * circle centre from which cell values are returned.
     * @param cellColIndex the column index for the cell thats centroid is the
     * circle centre from which cell values are returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long[] getCellIDs(
            long cellRowIndex,
            long cellColIndex,
            double distance,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long[] result = getCellIDs(
                    cellRowIndex,
                    cellColIndex,
                    distance);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellIDs(
                        cellRowIndex,
                        cellColIndex,
                        distance,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long[] - the cell IDs for cells thats centroids would be
     * intersected by circle with centre at centroid of cell given by cell row
     * index _CellRowIndex, cell column index _CellColIndex, and radius
     * distance.
     * @param cellRowIndex the row index for the cell thats centroid is the
     * circle centre from which cell values are returned.
     * @param cellColIndex the column index for the cell thats centroid is the
     * circle centre from which cell values are returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    protected final Grids_2D_ID_long[] getCellIDs(
            long cellRowIndex,
            long cellColIndex,
            double distance) {
        return getCellIDs(
                getCellXDouble(cellColIndex),
                getCellYDouble(cellRowIndex),
                cellRowIndex,
                cellColIndex,
                distance);
    }

    /**
     * @return double[] cells - the values for cells thats centroids would be
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
     * @param cellRowIndex the row index at y.
     * @param cellColIndex the col index at x.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_2D_ID_long[] getCellIDs(
            double x,
            double y,
            long cellRowIndex,
            long cellColIndex,
            double distance,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long[] result = getCellIDs(
                    x,
                    y,
                    cellRowIndex,
                    cellColIndex,
                    distance);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellIDs(
                        x,
                        y,
                        cellRowIndex,
                        cellColIndex,
                        distance,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * TODO: remove need for copy to new array.
     *
     * @return double[] cells - the values for cells thats centroids would be
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
     * @param cellRowIndex the row index at y.
     * @param cellColIndex the col index at x.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    protected Grids_2D_ID_long[] getCellIDs(
            double x,
            double y,
            long cellRowIndex,
            long cellColIndex,
            double distance) {
        Grids_2D_ID_long[] a_CellIDs0;
        int cellDistance = (int) Math.ceil(distance / this._Dimensions[0].doubleValue());
        int limit = ((2 * cellDistance) + 1) * ((2 * cellDistance) + 1);
        a_CellIDs0 = new Grids_2D_ID_long[limit];
        long row;
        long col;
        double thisX;
        double thisY;
        int count = 0;
        //if ( limit > 0 ) {
        for (row = cellRowIndex - cellDistance; row
                <= cellRowIndex + cellDistance; row++) {
            thisY = getCellYDouble(row);
            for (col = cellColIndex - cellDistance; col
                    <= cellColIndex + cellDistance; col++) {
                thisX = getCellXDouble(col);
                if (Grids_Utilities.distance(x, y, thisX, thisY) <= distance) {
                    a_CellIDs0[count] = new Grids_2D_ID_long(
                            row,
                            col);
                    //cellIDs0[ count ] = new Grids_2D_ID_long( row, col );
                    count++;
                }
            }
        }
        Grids_2D_ID_long[] a_CellIDs = new Grids_2D_ID_long[count];
        System.arraycopy(a_CellIDs0, 0, a_CellIDs, 0, count);
        return a_CellIDs;
        //}
        //return _CellIDs0;
    }

    /**
     * @return Nearest cells _CellRowIndex and _CellColIndex as a long[] from ID
     * to point given by x-coordinate x, y-coordinate y.
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_2D_ID_long getNearestCellID(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long result = getNearestCellID(
                    x,
                    y);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getNearestCellID(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return Nearest cells _CellRowIndex and _CellColIndex as a long[] from ID
     * to point given by x-coordinate x, y-coordinate y.
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     */
    protected Grids_2D_ID_long getNearestCellID(
            double x,
            double y) {
        return getNearestCellID(
                x,
                y, getCellRowIndex(y),
                getCellColIndex(x));
    }

    /**
     * @return Nearest cells _CellRowIndex and _CellColIndex as a long[] from ID
     * to point given by cell row index _CellRowIndex, cell column index
     * _CellColIndex.
     * @param cellRowIndex the row index from which nearest cell Grids_2D_ID_int is returned.
     * @param cellColIndex the column index from which nearest cell Grids_2D_ID_int is
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown. TODO: return Grids_2D_ID_long[] as
     * could be more than one nearest CellID
     */
    public Grids_2D_ID_long getNearestCellID(
            long cellRowIndex,
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long result = getNearestCellID(
                    cellRowIndex,
                    cellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getNearestCellID(
                        cellRowIndex,
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return Nearest cells _CellRowIndex and _CellColIndex as a long[] from ID
     * to point given by cell row index _CellRowIndex, cell column index
     * _CellColIndex.
     * @param cellRowIndex the row index from which nearest cell Grids_2D_ID_int is returned.
     * @param cellColIndex the column index from which nearest cell Grids_2D_ID_int is
     * returned. TODO: return Grids_2D_ID_long[] as could be more than one nearest CellID
     */
    protected Grids_2D_ID_long getNearestCellID(
            long cellRowIndex,
            long cellColIndex) {
        return getNearestCellID(
                getCellXDouble(cellColIndex),
                getCellYDouble(cellRowIndex),
                cellRowIndex,
                cellColIndex);
    }

    /**
     * @return Nearest Grids_2D_ID_long to point given by x-coordinate x, y-coordinate y
     * in position given by _CellRowIndex, _CellColIndex.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param cellRowIndex The cell row index of cell containing point.
     * @param cellColIndex The cell column index of cell containing point.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_2D_ID_long getNearestCellID(
            double x,
            double y,
            long cellRowIndex,
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long result = getNearestCellID(
                    x,
                    y,
                    cellRowIndex,
                    cellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getNearestCellID(
                        x,
                        y,
                        cellRowIndex,
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return Nearest Grids_2D_ID_long to point given by x-coordinate x, y-coordinate y
     * in position given by _CellRowIndex, _CellColIndex.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param cellRowIndex The cell row index of cell containing point.
     * @param cellColIndex The cell column index of cell containing point.
     */
    protected Grids_2D_ID_long getNearestCellID(
            double x,
            double y,
            long cellRowIndex,
            long cellColIndex) {
        Grids_2D_ID_long a_CellID;
        boolean isInGrid = isInGrid(
                x,
                y);
        if (!isInGrid) {
            long row;
            long col;
            if (x >= this._Dimensions[3].doubleValue()) {
                col = this._NCols - 1;
                if (y > this._Dimensions[4].doubleValue()) {
                    row = 0;
                } else {
                    if (y < this._Dimensions[2].doubleValue()) {
                        row = this._NRows - 1;
                    } else {
                        row = getCellRowIndex(y);
                    }
                }
            } else {
                if (x < this._Dimensions[1].doubleValue()) {
                    col = 0;
                    if (y >= this._Dimensions[4].doubleValue()) {
                        row = 0;
                    } else {
                        if (y < this._Dimensions[2].doubleValue()) {
                            row = this._NRows - 1;
                        } else {
                            row = getCellRowIndex(y);
                        }
                    }
                } else {
                    col = getCellColIndex(x);
                    if (y >= _Dimensions[4].doubleValue()) {
                        row = 0;
                    } else {
                        row = this._NRows - 1;
                    }
                }
            }
            a_CellID = new Grids_2D_ID_long(
                    row,
                    col);
        } else {
            a_CellID = new Grids_2D_ID_long(
                    cellRowIndex,
                    cellColIndex);
        }
        return a_CellID;
    }

    /**
     * @return Height of the grid.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getHeightDouble(
            boolean handleOutOfMemoryError) {
        try {
            double result = getHeightDouble();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getHeightDouble(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return Height of the grid.
     */
    protected final double getHeightDouble() {
        return getHeightBigDecimal().doubleValue();
    }

    /**
     * @return Height of the grid.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final BigDecimal getHeightBigDecimal(
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getHeightBigDecimal();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getHeightBigDecimal(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return Height of the grid.
     */
    protected final BigDecimal getHeightBigDecimal() {
        return (this._Dimensions[4].subtract(this._Dimensions[2]));
    }

    /**
     * @return Width of the grid as a double.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getWidthDouble(
            boolean handleOutOfMemoryError) {
        try {
            double result = getWidthDouble();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getWidthDouble(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return Width of the grid as a double.
     */
    protected final double getWidthDouble() {
        return getWidthBigDecimal().doubleValue();
    }

    /**
     * @return Width of the grid as a BigDecimal.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final BigDecimal getWidthBigDecimal(
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getWidthBigDecimal();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getWidthBigDecimal(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return Width of the grid as a BigDecimal.
     */
    protected final BigDecimal getWidthBigDecimal() {
        return (this._Dimensions[3].subtract(this._Dimensions[1]));
    }

    /**
     * @return true iff point given by x-coordinate x, y-coordinate y is in the
     * Grid. Anything on the boundary is considered to be in.
     * @param xBigDecimal The x-coordinate of the point.
     * @param yBigDecimal The y-coordinate of the point.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final boolean isInGrid(
            BigDecimal xBigDecimal,
            BigDecimal yBigDecimal,
            boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(
                    xBigDecimal,
                    yBigDecimal);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return isInGrid(
                        xBigDecimal,
                        yBigDecimal,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return true iff point given by x-coordinate x, y-coordinate y is in the
     * Grid. Anything on the boundary is considered to be in.
     * @param xBigDecimal The x-coordinate of the point.
     * @param yBigDecimal The y-coordinate of the point.
     */
    protected final boolean isInGrid(
            BigDecimal xBigDecimal,
            BigDecimal yBigDecimal) {
        return (xBigDecimal.compareTo(this._Dimensions[1]) != -1
                && yBigDecimal.compareTo(this._Dimensions[2]) != -1
                && xBigDecimal.compareTo(this._Dimensions[3]) != 1
                && yBigDecimal.compareTo(this._Dimensions[4]) != 1);
    }

    /**
     * @return true iff point given by x-coordinate x, y-coordinate y is in the
     * Grid. Anything on the boundary is considered to be in.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final boolean isInGrid(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(
                    x,
                    y);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return isInGrid(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return true iff point given by x-coordinate x, y-coordinate y is in the
     * Grid. Anything on the boundary is considered to be in.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    protected final boolean isInGrid(
            double x,
            double y) {
        return isInGrid(
                BigDecimal.valueOf(x),
                BigDecimal.valueOf(y));
    }

    /**
     * @return true iff position given by cell row index _CellRowIndex, cell
     * column index _CellColIndex is in the Grid.
     * @param cellRowIndex The cell row index to test.
     * @param cellColIndex The cell column index to test.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final boolean isInGrid(
            long cellRowIndex,
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(
                    cellRowIndex,
                    cellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return isInGrid(
                        cellRowIndex,
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return true iff position given by cell row index _CellRowIndex, cell
     * column index _CellColIndex is in the Grid.
     * @param cellRowIndex The cell row index to test.
     * @param cellColIndex The cell column index to test.
     */
    protected final boolean isInGrid(
            long cellRowIndex,
            long cellColIndex) {
        return (cellRowIndex >= 0
                && cellRowIndex < this._NRows
                && cellColIndex >= 0
                && cellColIndex < this._NCols);
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return true iff position given by chunk row index _Row, chunk
 column index _Col is in the Grid.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final boolean isInGrid(
            int chunkRowIndex,
            int chunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(
                    chunkRowIndex,
                    chunkColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return isInGrid(
                        chunkRowIndex,
                        chunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return true iff position given by chunk row index _Row, chunk
 column index _Col is in the Grid.
     */
    protected final boolean isInGrid(
            int chunkRowIndex,
            int chunkColIndex) {
        return (chunkRowIndex >= 0
                && chunkRowIndex < this._NRows
                && chunkColIndex >= 0
                && chunkColIndex < this._NCols);
    }

    /**
     * @return true iff cell given by _CellID is in the Grid.
     * @param a_CellID The Grids_2D_ID_long of a cell to test.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final boolean isInGrid(
            Grids_2D_ID_long a_CellID,
            boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(a_CellID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return isInGrid(
                        a_CellID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return true iff cell given by _CellID is in the Grid.
     * @param i The Grids_2D_ID_long of a cell to test.
     */
    protected final boolean isInGrid(
            Grids_2D_ID_long i) {
        return isInGrid(
                i.getRow(),
                i.getCol());
    }

    /**
     * @return true iff Grids_2D_ID_int _ChunkID is in the Grid.
     * @param a_ChunkID The Grids_2D_ID_int of a cell to test.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final boolean isInGrid(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(a_ChunkID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return isInGrid(
                        a_ChunkID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return true iff Grids_2D_ID_int chunkID is in the Grid.
     * @param chunkID The Grids_2D_ID_int of a cell to test.
     */
    protected final boolean isInGrid(
            Grids_2D_ID_int chunkID) {
        int chunkRowIndex = chunkID._Row;
        int chunkColIndex = chunkID._Col;
        return chunkRowIndex > -1
                && chunkRowIndex < this._NChunkRows
                && chunkColIndex > -1
                && chunkColIndex < this._NChunkCols;
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return true iff cell given by _Row, _Col,
 chunkCellRowIndex, chunkCellColIndex is in the Grid.
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final boolean isInGrid(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(
                    chunkRowIndex,
                    chunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return isInGrid(
                        chunkRowIndex,
                        chunkColIndex,
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _ChunkRowIndex
     * @param _ChunkColIndex
     * @return true iff cell given by _Row, _Col,
 chunkCellRowIndex, chunkCellColIndex is in the Grid.
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     */
    protected final boolean isInGrid(
            int _ChunkRowIndex,
            int _ChunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        return isInGrid(
                ((long) _ChunkRowIndex * (long) this._ChunkNRows) + (long) chunkCellRowIndex,
                ((long) _ChunkColIndex * (long) this._ChunkNCols) + (long) chunkCellColIndex);
    }

    /**
     * @return the x-coordinate of the centroid for cells with column index
     * _CellColIndex as a BigDecimal.
     * @param cellColIndex The cell column index thats centroid x-coordinate is
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final BigDecimal getCellXBigDecimal(
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellXBigDecimal(cellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellXBigDecimal(
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param chunkColIndex
     * @param chunkRowIndex
     * @return x-coordinate of the centroid for cells with column index
     * _CellColIndex as a BigDecimal.
     * @param cellColIndex The cell column index thats centroid x-coordinate is
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final BigDecimal getCellXBigDecimal(
            long cellColIndex,
            int chunkRowIndex,
            int chunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellXBigDecimal(cellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellXBigDecimal(
                        cellColIndex,
                        chunkRowIndex,
                        chunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _CellColIndex
     * @return the x-coordinate of the centroid for cells with column index
     * _CellColIndex as a BigDecimal.
     */
    protected final BigDecimal getCellXBigDecimal(
            long _CellColIndex) {
        BigDecimal offSetFromOrigin_BigDecimal = _Dimensions[1].add(
                _Dimensions[0].multiply(BigDecimal.valueOf(_CellColIndex)));
        BigDecimal halfCellsize = getHalfCellsize();
        return offSetFromOrigin_BigDecimal.add(halfCellsize);
    }

    public BigDecimal getHalfCellsize() {
        BigDecimal result = _Dimensions[0].divide(
                new BigDecimal("2"),
                _Dimensions[0].scale() + 1,
                BigDecimal.ROUND_UNNECESSARY);
        return result;
    }

    /**
     * @return x-coordinate of the centroid for cells with column index
     * _CellColIndex as a double.
     * @param cellColIndex The cell column index thats centroid x-coordinate is
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellXDouble(
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellXDouble(
                    cellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellXDouble(
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return x-coordinate of the centroid for cells with column index
     * _CellColIndex as a double.
     * @param cellColIndex The cell column index thats centroid x-coordinate is
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @param a_ChunkID This is a Grids_2D_ID_int for those AbstractGrid2DSquareCells
 not to be swapped if possible when an OutOfMemoryError is encountered.
     */
    public final double getCellXDouble(
            long cellColIndex,
            boolean handleOutOfMemoryError,
            Grids_2D_ID_int a_ChunkID) {
        try {
            double result = getCellXDouble(
                    cellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(a_ChunkID, handleOutOfMemoryError);
                return getCellXDouble(
                        cellColIndex,
                        handleOutOfMemoryError,
                        a_ChunkID);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param chunkColIndex
     * @param chunkRowIndex
     * @return x-coordinate of the centroid for cells with column index
     * _CellColIndex as a double.
     * @param chunkCellColIndex The cell column index thats centroid
     * x-coordinate is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellXDouble(
            int chunkCellColIndex,
            int chunkRowIndex,
            int chunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellXDouble(
                    chunkCellColIndex,
                    chunkColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellXDouble(
                        chunkCellColIndex,
                        chunkRowIndex,
                        chunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param chunkColIndex
     * @return x-coordinate of the centroid for cells with column index
     * _CellColIndex as a double.
     * @param chunkCellColIndex The cell column index thats centroid
     * x-coordinate is returned.
     */
    protected final double getCellXDouble(
            int chunkCellColIndex,
            int chunkColIndex) {
        return getCellXDouble(getCellColIndex(chunkCellColIndex, chunkColIndex));
    }

    /**
     * @return x-coordinate of the centroid for cells with column index
     * _CellColIndex as a double.
     * @param cellColIndex The cell column index thats centroid x-coordinate is
     * returned.
     */
    protected final double getCellXDouble(
            long cellColIndex) {
        return getCellXBigDecimal(cellColIndex).doubleValue();
    }

    /**
     * @return x-coordinate of the centroid for cell with cell Grids_2D_ID_int _CellID as a
     * BigDecimal.
     * @param a_CellID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final BigDecimal getCellXBigDecimal(
            Grids_2D_ID_long a_CellID,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellXBigDecimal(a_CellID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellXBigDecimal(
                        a_CellID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param a_CellID The Grids_2D_ID_long of the cell thats centroid is returned.
     * @param chunkRowIndex
     * @param a_ChunkRowIndex The chunk row index of the
     * Grids_AbstractGrid2DSquareCellChunk not to be swapped if an
     * OutOfMemoryError is thrown.
     * @param chunkColIndex
     * @param a_ChunkColIndex The chunk column index of the
     * Grids_AbstractGrid2DSquareCellChunk not to be swapped if an
     * OutOfMemoryError is thrown.
     * @param handleOutOfMemoryError
     * @return x-coordinate of the centroid of cell with Grids_2D_ID_long _CellID as a
     * getCellXBigDecimal.
     */
    public final BigDecimal getCellXBigDecimal(
            Grids_2D_ID_long a_CellID,
            int chunkRowIndex,
            int chunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellXBigDecimal(a_CellID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellXBigDecimal(
                        a_CellID,
                        chunkRowIndex,
                        chunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return x-coordinate of the centroid for cell with cell Grids_2D_ID_int _CellID as a
     * BigDecimal.
     * @param a_CellID
     */
    protected final BigDecimal getCellXBigDecimal(
            Grids_2D_ID_long a_CellID) {
        return getCellXBigDecimal(a_CellID._Col);
    }

    /**
     * @return x-coordinate of the centroid for cell with cell Grids_2D_ID_int _CellID as a
     * double
     * @param a_CellID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellXDouble(
            Grids_2D_ID_long a_CellID,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellXDouble(a_CellID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellXDouble(
                        a_CellID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return x-coordinate of the centroid of cell with Grids_2D_ID_long _CellID as a
     * double.
     * @param a_CellID The Grids_2D_ID_long of the cell thats centroid is returned.
     */
    public final double getCellXDouble(
            Grids_2D_ID_long a_CellID,
            int chunkRowIndex,
            int chunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellXDouble(a_CellID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellXDouble(
                        a_CellID,
                        chunkRowIndex,
                        chunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _CellID
     * @return x-coordinate of the centroid for cell with cell Grids_2D_ID_int _CellID as a
     * double
     */
    protected final double getCellXDouble(
            Grids_2D_ID_long _CellID) {
        return getCellXBigDecimal(_CellID).doubleValue();
    }

    /**
     * @return y-coordinate of the centroid for cells with row index
     * _CellRowIndex as a BigDecimal.
     * @param cellRowIndex the cell column index thats centroid y-coordinate is
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final BigDecimal getCellYBigDecimal(
            long cellRowIndex,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellYBigDecimal(cellRowIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellYBigDecimal(
                        cellRowIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return y-coordinate of the centroid for cells with row index
     * _CellRowIndex as a BigDecimal.
     * @param cellRowIndex the cell column index thats centroid y-coordinate is
     * returned.
     */
    public final BigDecimal getCellYBigDecimal(
            long cellRowIndex,
            int chunkRowIndex,
            int chunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellYBigDecimal(cellRowIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellYBigDecimal(
                        cellRowIndex,
                        chunkRowIndex,
                        chunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _CellRowIndex
     * @return y-coordinate of the centroid for cells with row index
     * _CellRowIndex as a BigDecimal.
     */
    protected final BigDecimal getCellYBigDecimal(
            long _CellRowIndex) {
        BigDecimal offSetFromOrigin_BigDecimal = _Dimensions[2].add(
                _Dimensions[0].multiply(BigDecimal.valueOf(_CellRowIndex)));
        BigDecimal halfCellsize = getHalfCellsize();
        return offSetFromOrigin_BigDecimal.add(halfCellsize);
    }

    /**
     * @return y-coordinate of the centroid for cells with row index
     * _CellRowIndex as a double.
     * @param cellRowIndex the cell column index thats centroid y-coordinate is
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellYDouble(
            long cellRowIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellYDouble(cellRowIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellYDouble(
                        cellRowIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return y-coordinate of the centroid for cells with row index
     * _CellRowIndex as a double.
     * @param cellRowIndex the cell column index thats centroid y-coordinate is
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @param a_ChunkID This is a Grids_2D_ID_int for those AbstractGrid2DSquareCells
 not to be swapped if possible when an OutOfMemoryError is encountered.
     */
    public final double getCellYDouble(
            long cellRowIndex,
            boolean handleOutOfMemoryError,
            Grids_2D_ID_int a_ChunkID) {
        try {
            double result = getCellYDouble(cellRowIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellYDouble(
                        cellRowIndex,
                        handleOutOfMemoryError,
                        a_ChunkID);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _ChunkColIndex
     * @param _ChunkRowIndex
     * @return y-coordinate of the centroid for cells with row index
     * _CellRowIndex as a double.
     * @param chunkCellRowIndex the chunk cell column index thats centroid
     * y-coordinate is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellYDouble(
            int chunkCellRowIndex,
            int _ChunkRowIndex,
            int _ChunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellYDouble(chunkCellRowIndex, _ChunkRowIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellYDouble(
                        chunkCellRowIndex,
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
     * @return y-coordinate of the centroid for cells with row index
     * _CellRowIndex as a double.
     * @param chunkCellRowIndex the chunk cell column index thats centroid
     * y-coordinate is returned.
     */
    protected final double getCellYDouble(
            int chunkCellRowIndex,
            int _ChunkRowIndex) {
        return getCellYDouble(getCellRowIndex(chunkCellRowIndex, _ChunkRowIndex));
    }

    /**
     * @param _CellRowIndex
     * @return y-coordinate of the centroid for cells with row index
     * _CellRowIndex as a double.
     */
    protected final double getCellYDouble(
            long _CellRowIndex) {
        return getCellYBigDecimal(_CellRowIndex).doubleValue();
    }

    /**
     * @return y-coordinate of the centroid of cell with Grids_2D_ID_long _CellID as a
     * BigDecimal.
     * @param a_CellID The Grids_2D_ID_long of the cell thats centroid is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final BigDecimal getCellYBigDecimal(
            Grids_2D_ID_long a_CellID,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellYBigDecimal(a_CellID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellYBigDecimal(
                        a_CellID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param a_CellID The Grids_2D_ID_long of the cell thats centroid is returned.
     * @param chunkRowIndex
     * @param chunkColIndex
     * @param handleOutOfMemoryError
     * @return y-coordinate of the centroid of cell with Grids_2D_ID_long _CellID as a
     * BigDecimal.
     */
    public final BigDecimal getCellYBigDecimal(
            Grids_2D_ID_long a_CellID,
            int chunkRowIndex,
            int chunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellYBigDecimal(a_CellID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellYBigDecimal(
                        a_CellID,
                        chunkRowIndex,
                        chunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _CellID
     * @return y-coordinate of the centroid of cell with Grids_2D_ID_long _CellID as a
     * BigDecimal.
     */
    protected final BigDecimal getCellYBigDecimal(
            Grids_2D_ID_long _CellID) {
        return getCellYBigDecimal(_CellID._Row);
    }

    /**
     * @return y-coordinate of the centroid of cell with Grids_2D_ID_long _CellID as a
     * double.
     * @param a_CellID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellYDouble(
            Grids_2D_ID_long a_CellID,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellYDouble(a_CellID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellYDouble(
                        a_CellID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return y-coordinate of the centroid of cell with Grids_2D_ID_long _CellID as a
     * double.
     * @param a_CellID The Grids_2D_ID_long of the cell thats centroid is returned.
     */
    public final double getCellYDouble(
            Grids_2D_ID_long a_CellID,
            int chunkRowIndex,
            int chunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellYDouble(a_CellID);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellYDouble(
                        a_CellID,
                        chunkRowIndex,
                        chunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param _CellID
     * @return the y-coordinate of the centroid of cell with Grids_2D_ID_long _CellID as a
     * double.
     */
    protected final double getCellYDouble(
            Grids_2D_ID_long _CellID) {
        return getCellYBigDecimal(_CellID).doubleValue();
    }

    /**
     * TODO: Are bounds in double range? Is there more than cellsize difference
     * with precision? Throw appropriate exceptions.
     *
     * @return gridBounds (the bounding box of the grid) as a double[] where;
     * gridBounds[0] xmin, left most x-coordinate of this gridBounds[1] ymin,
     * lowest y-coordinate of this gridBounds[2] xmax, right most x-coordinate
     * of this gridBounds[3] ymax, highest y-coordinate of this
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double[] getGridBounds(
            boolean handleOutOfMemoryError) {
        try {
            double[] result = getGridBounds();
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getGridBounds(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return gridBounds (the bounding box of the grid) as a double[] where;
     * gridBounds[0] xmin, left most x-coordinate of this gridBounds[1] ymin,
     * lowest y-coordinate of this gridBounds[2] xmax, right most x-coordinate
     * of this gridBounds[3] ymax, highest y-coordinate of this TODO: Are bounds
     * in double range? Is there more than cellsize difference with precision?
     * Throw appropriate exceptions.
     */
    protected final double[] getGridBounds() {
        double[] result = new double[4];
        result[0] = _Dimensions[1].doubleValue();
        result[1] = _Dimensions[2].doubleValue();
        result[2] = _Dimensions[3].doubleValue();
        result[3] = _Dimensions[4].doubleValue();
        return result;
    }

    /**
     * @return double[] where; double[0] xmin, left most x-coordinate of cell at
     * (rowIndex,colIndex) double[1] ymin, lowest y-coordinate of cell at
     * (rowIndex,colIndex) double[2] xmax, right most x-coordinate of cell at
     * (rowIndex,colIndex) double[3] ymax, highest y-coordinate of cell at
     * (rowIndex,colIndex)
     * @param cellRowIndex the row index of the cell for which the bounds are
     * returned
     * @param cellColIndex the column index of the cell for which the bounds are
     * returned
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double[] getCellBoundsDoubleArray(
            long cellRowIndex,
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double[] result = getCellBoundsDoubleArray(
                    cellRowIndex,
                    cellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellBoundsDoubleArray(
                        cellRowIndex,
                        cellColIndex,
                        handleOutOfMemoryError);

            } else {
                throw a_OutOfMemoryError;

            }
        }
    }

    /**
     * @param _CellRowIndex
     * @param _CellColIndex
     * @return double[] where; double[0] xmin, left most x-coordinate of cell at
     * (rowIndex,colIndex) double[1] ymin, lowest y-coordinate of cell at
     * (rowIndex,colIndex) double[2] xmax, right most x-coordinate of cell at
     * (rowIndex,colIndex) double[3] ymax, highest y-coordinate of cell at
     * (rowIndex,colIndex)
     * @param cellRowIndex the row index of the cell for which the bounds are
     * returned
     */
    protected final double[] getCellBoundsDoubleArray(
            long _CellRowIndex,
            long _CellColIndex) {
        return getCellBoundsDoubleArray(
                getCellXDouble(_CellColIndex),
                getCellYDouble(_CellRowIndex));
    }

    /**
     * Precision may compromise result. More precision is available via
     *
     * @return double[] where; double[0] xmin, left most x-coordinate of cell
     * that intersects point at (x,y) double[1] ymin, lowest y-coordinate of
     * cell that intersects point at (x,y) double[2] xmax, right most
     * x-coordinate of cell that intersects point at (x,y) double[3] ymax,
     * highest y-coordinate of cell that intersects point at (x,y)
     * @param x the x-coordinate in the cell for which the bounds are returned
     * @param y the y-coordinate in the cell for which the bounds are returned
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double[] getCellBoundsDoubleArray(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            double[] result = getCellBoundsDoubleArray(
                    x,
                    y);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellBoundsDoubleArray(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Precision may compromise result. More precision is available via
     * BigDecimal arithmetic.
     *
     * @return double[] where; double[0] xmin, left most x-coordinate of cell
     * that intersects point at (x,y) double[1] ymin, lowest y-coordinate of
     * cell that intersects point at (x,y) double[2] xmax, right most
     * x-coordinate of cell that intersects point at (x,y) double[3] ymax,
     * highest y-coordinate of cell that intersects point at (x,y)
     * @param x the x-coordinate in the cell for which the bounds are returned
     * @param y the y-coordinate in the cell for which the bounds are returned
     */
    protected final double[] getCellBoundsDoubleArray(
            double x,
            double y) {
        double[] cellBounds = new double[4];
        double halfCellsize = _Dimensions[0].doubleValue() / 2.0d;
        cellBounds[0] = x - halfCellsize;
        cellBounds[1] = y - halfCellsize;
        cellBounds[2] = x + halfCellsize;
        cellBounds[3] = y + halfCellsize;
        return cellBounds;
    }

    /**
     * @param handleOutOfMemoryError
     * @return BigDecimal[] cellBounds_BigDecimalArray;
     * cellBounds_BigDecimalArray[0] xmin, left most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[1] ymin, lowest
     * y-coordinate of cell that intersects point at (x,y)
     * cellBounds_BigDecimalArray[2] xmax, right most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[3] ymax, highest
     * y-coordinate of cell that intersects point at (x,y)
     * @param x the centroid x-coordinate of the cell for which the bounds are
     * returned.
     * @param y the centroid y-coordinate of the cell for which the bounds are
     * returned.
     */
    public final BigDecimal[] getCellBounds_BigDecimalArray(
            BigDecimal x,
            BigDecimal y,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal[] result = getCellBounds_BigDecimalArray(
                    x,
                    y);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellBounds_BigDecimalArray(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return BigDecimal[] cellBounds_BigDecimalArray;
     * cellBounds_BigDecimalArray[0] xmin, left most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[1] ymin, lowest
     * y-coordinate of cell that intersects point at (x,y)
     * cellBounds_BigDecimalArray[2] xmax, right most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[3] ymax, highest
     * y-coordinate of cell that intersects point at (x,y)
     * @param x the centroid x-coordinate of the cell for which the bounds are
     * returned.
     * @param y the centroid y-coordinate of the cell for which the bounds are
     * returned.
     */
    protected final BigDecimal[] getCellBounds_BigDecimalArray(
            BigDecimal x,
            BigDecimal y) {
        BigDecimal[] cellBounds_BigDecimalArray = new BigDecimal[4];
        //TODO probably do this a lot, so move to a static field to speed up
        BigDecimal halfCellsize = _Dimensions[0].divide(new BigDecimal("2.0"));
        cellBounds_BigDecimalArray[0] = x.subtract(halfCellsize);
        cellBounds_BigDecimalArray[1] = y.subtract(halfCellsize);
        cellBounds_BigDecimalArray[2] = x.add(halfCellsize);
        cellBounds_BigDecimalArray[3] = y.add(halfCellsize);
        return cellBounds_BigDecimalArray;
    }

    /**
     * @param _CellRowIndex
     * @param handleOutOfMemoryError
     * @param _CellColIndex
     * @return BigDecimal[] cellBounds_BigDecimalArray;
     * cellBounds_BigDecimalArray[0] xmin, left most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[1] ymin, lowest
     * y-coordinate of cell that intersects point at (x,y)
     * cellBounds_BigDecimalArray[2] xmax, right most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[3] ymax, highest
     * y-coordinate of cell that intersects point at (x,y)
     */
    public final BigDecimal[] getCellBounds_BigDecimalArray(
            long _CellRowIndex,
            long _CellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal[] result = getCellBounds_BigDecimalArray(
                    _CellRowIndex,
                    _CellColIndex);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(handleOutOfMemoryError);
                return getCellBounds_BigDecimalArray(
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
     * @return BigDecimal[] cellBounds_BigDecimalArray;
     * cellBounds_BigDecimalArray[0] xmin, left most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[1] ymin, lowest
     * y-coordinate of cell that intersects point at (x,y)
     * cellBounds_BigDecimalArray[2] xmax, right most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[3] ymax, highest
     * y-coordinate of cell that intersects point at (x,y)
     */
    protected final BigDecimal[] getCellBounds_BigDecimalArray(
            long _CellRowIndex,
            long _CellColIndex) {
        return getCellBounds_BigDecimalArray(
                getCellXBigDecimal(_CellColIndex),
                getCellYBigDecimal(_CellRowIndex));
    }

    /**
     * @param handleOutOfMemoryError
     * @return the _NoDataValue of this as a BigDecimal.
     */
    public abstract BigDecimal getNoDataValueBigDecimal(
            boolean handleOutOfMemoryError);

    /**
     * @return the next Grids_2D_ID_int in row major order from _ChunkID, or null.
     * @param a_ChunkID
     * @param nChunkRows
     * @param nChunkCols
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_2D_ID_int getNextChunk(
            Grids_2D_ID_int a_ChunkID,
            int nChunkRows,
            int nChunkCols,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_int result = getNextChunk(
                    a_ChunkID,
                    nChunkRows,
                    nChunkCols);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(this, a_ChunkID) < 1L) {
                    throw a_OutOfMemoryError;
                }
                env.init_MemoryReserve(this, a_ChunkID, handleOutOfMemoryError);
                return getNextChunk(
                        a_ChunkID,
                        nChunkRows,
                        nChunkCols,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return the next Grids_2D_ID_int in row major order from _ChunkID, or null.
     * @param chunkID
     * @param nChunkRows
     * @param nChunkCols
     */
    protected Grids_2D_ID_int getNextChunk(
            Grids_2D_ID_int chunkID,
            int nChunkRows,
            int nChunkCols) {
        int chunkRowIndex = chunkID.getRow();
        int chunkColIndex = chunkID.getCol();
        if (chunkColIndex < nChunkCols - 1) {
            return new Grids_2D_ID_int(
                    chunkRowIndex,
                    chunkColIndex + 1);
        } else {
            if (chunkRowIndex < nChunkRows - 1) {
                return new Grids_2D_ID_int(
                        chunkRowIndex + 1,
                        0);
            }
        }
        return null;
    }

    /**
     * @return the next Grids_2D_ID_int in row major order from this, or null.
     * @param chunkID
     * @param nChunkRows
     * @param nChunkCols
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_2D_ID_int getPreviousChunk(
            Grids_2D_ID_int chunkID,
            int nChunkRows,
            int nChunkCols,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_int result = getPreviousChunk(
                    chunkID,
                    nChunkRows,
                    nChunkCols);
            env.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clear_MemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getPreviousChunk(
                        chunkID,
                        nChunkRows,
                        nChunkCols,
                        handleOutOfMemoryError);
            } else {
                throw e;

            }
        }
    }

    /**
     * @param chunkID
     * @param nChunkCols
     * @param nChunkRows
     * @return the next Grids_2D_ID_int in row major order from this, or null.
     */
    protected Grids_2D_ID_int getPreviousChunk(
            Grids_2D_ID_int chunkID,
            int nChunkRows,
            int nChunkCols) {
        int chunkRowIndex = chunkID.getRow();
        int chunkColIndex = chunkID.getCol();
        if (chunkColIndex > 0) {
            return new Grids_2D_ID_int(
                    chunkRowIndex,
                    chunkColIndex - 1);
        } else {
            if (chunkRowIndex > 0) {
                return new Grids_2D_ID_int(
                        chunkRowIndex - 1,
                        0);
            }
        }
        return null;
    }

    /**
     *
     * @param a_ChunkID
     * @param otherGridWithChunkIDNotToSwap
     * @param e
     */
    protected void freeSomeMemoryAndResetReserve(
            Grids_2D_ID_int a_ChunkID,
            Grids_AbstractGrid2DSquareCell otherGridWithChunkIDNotToSwap,
            OutOfMemoryError e) {
        HashSet<Grids_2D_ID_int> notToSwapChunks;
        notToSwapChunks = new HashSet<Grids_2D_ID_int>();
        notToSwapChunks.add(a_ChunkID);
        env._NotToSwapData.put(this, notToSwapChunks);
        env._NotToSwapData.put(otherGridWithChunkIDNotToSwap, notToSwapChunks);
        if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(env._NotToSwapData) < 1) {
            throw e;
        }
        env.init_MemoryReserve(this, a_ChunkID, env.HandleOutOfMemoryErrorTrue);
    }

    /**
     *
     * @param chunksNotToSwapToFile
     * @param e
     */
    protected void freeSomeMemoryAndResetReserve(
            HashMap<Grids_AbstractGrid2DSquareCell, HashSet<Grids_2D_ID_int>> chunksNotToSwapToFile,
            OutOfMemoryError e) {
        if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(chunksNotToSwapToFile) < 1) {
            throw e;
        }
        env.init_MemoryReserve(chunksNotToSwapToFile, env.HandleOutOfMemoryErrorTrue);
    }

    protected void freeSomeMemoryAndResetReserve(int chunkRowIndex, int chunkColIndex, OutOfMemoryError e) {
        //env.clear_MemoryReserve();
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRowIndex, chunkColIndex);
        freeSomeMemoryAndResetReserve(chunkID, e);
    }

    protected void freeSomeMemoryAndResetReserve(HashSet<Grids_2D_ID_int> chunkIDs, OutOfMemoryError e) {
        if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(this, chunkIDs) < 1L) {
            throw e;
        }
        env.init_MemoryReserve(this, chunkIDs, env.HandleOutOfMemoryErrorTrue);
    }

    protected void freeSomeMemoryAndResetReserve(Grids_2D_ID_int chunkID, OutOfMemoryError e) {
        if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(this) < 1L) {
            if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(this, chunkID) < 1L) {
                throw e;
            }
        }
        env.init_MemoryReserve(this, chunkID, env.HandleOutOfMemoryErrorTrue);
    }

    protected void freeSomeMemoryAndResetReserve(
            boolean handleOutOfMemoryError,
            OutOfMemoryError e) {
        if (env.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
            throw e;
        }
        env.init_MemoryReserve(handleOutOfMemoryError);
    }

    protected void freeSomeMemoryAndResetReserve(OutOfMemoryError e) {
        if (env.swapToFile_Grid2DSquareCellChunkExcept_Account(this) < 1L) {
            throw e;
        }
        env.init_MemoryReserve(this, env.HandleOutOfMemoryErrorTrue);
    }

}
