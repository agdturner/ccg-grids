/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid;

import java.io.File;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleMap;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntMap;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_UnsignedLongPowersOf2;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGrid extends Grids_Object implements Serializable {

    /**
     * For storing individual locations mapped to a binary encoded long. This is
     * only used in Grids_GridChunkDouble64CellMap and
     * Grids_GridChunkInt64CellMap. It is stored in this to save it being stored
     * in every chunk or calculated on the fly.
     */
    protected Grids_UnsignedLongPowersOf2 UnsignedLongPowersOf2;
    //    /**
    //     * A version number for confidence in reloading serialised instances.
    //     */
    //    private static final long serialVersionUID = 1L;
    /**
     * Local _Directory used for caching. TODO If this were not transient upon
     * reloading, it would be possible to ascertain what it was which could be
     * useful.
     */
    protected transient File Directory;
    /**
     * The Grids_AbstractGridChunk data cache. A collection is used rather than
     * an array because an element of a collection set to null is available for
     * garbage collection whereas an element of an array set to null seems not
     * to be.
     */
    protected transient HashMap<Grids_2D_ID_int, Grids_AbstractGridChunk> ChunkIDChunkMap;
    /**
     * For storing the number of chunk rows.
     */
    protected int NChunkRows;
    /**
     * For storing the number of chunk columns.
     */
    protected int NChunkCols;
    /**
     * For storing the (usual) number of rows of cells in a chunk. The number of
     * rows in the final chunk row may be less.
     */
    protected int ChunkNRows;
    /**
     * For storing the (usual) number of columns of cells in a chunk. The number
     * of columns in the final chunk column may be less.
     */
    protected int ChunkNCols;
    /**
     * For storing the number of rows in the grid.
     */
    protected long NRows;
    /**
     * For storing the number of columns in the grid.
     */
    protected long NCols;
    /**
     * For storing the Name of the grid.
     */
    protected String Name;
    /**
     * Stores the cellsize, minx, miny, maxx, maxy.
     */
    protected Grids_Dimensions Dimensions;

    Grids_AbstractGrid() {
    }

    Grids_AbstractGrid(Grids_Environment ge, File directory) {
        super(ge);
        Directory = directory;
    }

    /**
     * Initialises this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap by first
     * attempting to load from new File( grid_File, "cache" );
     *
     * @param f The File directory that from which a file called cache is
     * attempted to be loaded
     * @param handleOutOfMemoryError
     */
    protected void initChunks(
            File f, boolean handleOutOfMemoryError) {
        try {
            initChunks(f);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                initChunks(f, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap by first
     * attempting to load from new File( grid_File, "cache" );
     *
     * @param f The File directory that from which a file called cache is
     * attempted to be loaded
     */
    protected void initChunks(File f) {
        File cache = new File(f, "cache");
        if (cache.exists()) {
            ObjectInputStream ois;
            ois = Generic_StaticIO.getObjectInputStream(cache);
            try {
                ChunkIDChunkMap
                        = (HashMap<Grids_2D_ID_int, Grids_AbstractGridChunk>) ois.readObject();
                ois.close();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Grids_AbstractGrid.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            ChunkIDChunkMap = new HashMap<>();
        }
    }

    /**
     * Initialises non transient Grids_AbstractGrid fields from
     * g.
     *
     * @param g The Grids_AbstractGrid from which the non transient
     * Grids_AbstractGrid fields of this are set.
     */
    protected void init(Grids_AbstractGrid g) {
        ChunkNCols = g.ChunkNCols;
        ChunkNRows = g.ChunkNRows;
        Dimensions = g.Dimensions;
        Name = g.Name;
        NChunkCols = g.NChunkCols;
        NChunkRows = g.NChunkRows;
        NCols = g.NCols;
        NRows = g.NRows;
        ge.getGrids().add(this);
    }

    /**
     * @param handleOutOfMemoryError
     * @return Grids.
     */
    public HashSet<Grids_AbstractGrid> getGrids(boolean handleOutOfMemoryError) {
        try {
            HashSet<Grids_AbstractGrid> result = ge.getGrids();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getGrids(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return the Grids_AbstractGridChunk with ChunkID.Row equal to chunkRowIndex
     * and ChunkID.Col equal to chunkColIndex.
     * @param chunkRowIndex The ChunkID.Row of the returned Grids_AbstractGridChunk.
     * @param chunkColIndex The ChunkID.Col of the returned Grids_AbstractGridChunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected Grids_AbstractGridChunk getChunk(
            int chunkRowIndex, int chunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            return getChunk(chunkRowIndex, chunkColIndex);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(chunkRowIndex, chunkColIndex, e);
                return getChunk(chunkRowIndex, chunkColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return the Grids_AbstractGridChunk with ID.chunkRowIndex equal to
     * chunkRowIndex and ID.chunkColIndex equal to chunkColIndex.
     * @param chunkRowIndex The ID.chunkRowIndex of the returned
     * Grids_AbstractGridChunk.
     * @param chunkColIndex The ID.chunkColIndex of the returned
     * Grids_AbstractGridChunk.
     */
    protected Grids_AbstractGridChunk getChunk(
            int chunkRowIndex, int chunkColIndex) {
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRowIndex, chunkColIndex);
        return getChunk(chunkID);
    }

    /**
     * @return the Grids_AbstractGridChunk with ChunkID equal to
     * chunkID.
     * @param chunkID The Grids_2D_ID_int of the
     * Grids_AbstractGridChunk to be returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_AbstractGridChunk getChunk(
            Grids_2D_ID_int chunkID, boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractGridChunk result = getChunk(chunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getChunk(chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return the Grids_AbstractGridChunk with ChunkID equal to
     * chunkID.
     *
     * @param chunkID The ChunkID of the Grids_AbstractGridChunk returned.
     */
    protected Grids_AbstractGridChunk getChunk(Grids_2D_ID_int chunkID) {
        boolean isInGrid = isInGrid(chunkID);
        if (isInGrid) {
            boolean chunkIDChunkMapContainsKey;
            chunkIDChunkMapContainsKey = getChunkIDChunkMap().containsKey(chunkID);
            if (!chunkIDChunkMapContainsKey) {
                loadIntoCacheChunk(chunkID);
            }
            return (Grids_AbstractGridChunk) ChunkIDChunkMap.get(chunkID);
        }
        return null;
    }

    /**
     * @return HashSet containing all ChunkIDs.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public HashSet<Grids_2D_ID_int> getChunkIDs(boolean handleOutOfMemoryError) {
        try {
            HashSet<Grids_2D_ID_int> result = getChunkIDs();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkIDs(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return HashSet containing all ChunkIDs.
     */
    protected HashSet<Grids_2D_ID_int> getChunkIDs() {
        HashSet<Grids_2D_ID_int> result = new HashSet<>();
        result.addAll(getChunkIDChunkMap().keySet());
        return result;
    }

    /**
     * @param handleOutOfMemoryError
     * @return Iterator over the cell values in this.
     */
    public abstract Iterator iterator(boolean handleOutOfMemoryError);

    /**
     * @return String description of this.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public String toString(boolean handleOutOfMemoryError) {
        try {
            String result = toString();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return toString(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return a string description of the Abstract fields of this instance.
     */
    @Override
    public String toString() {
        String result;
        result = "Grid(ChunkNcols(" + ChunkNCols + "),"
                + "ChunkNrows(" + ChunkNRows + "),"
                + "NChunkCols(" + NChunkCols + "),"
                + "NChunkRows(" + NChunkRows + "),"
                + "NCols(" + NCols + "),"
                + "NRows(" + NRows + "),"
                + "Directory( " + Directory + "),"
                + "Name( " + Name + "),"
                + getDimensions().toString();
        if (ChunkIDChunkMap == null) {
            result += ",ChunkID_GridChunk_Map==null";
        } else {
            result += ",ChunkID_GridChunk_Map.size(" + ChunkIDChunkMap.size() + ")";
        }
        if (UnsignedLongPowersOf2 == null) {
            result += ",UnsignedLongPowersOf2==null";
        } else {
            result += ",UnsignedLongPowersOf2(" + UnsignedLongPowersOf2.toString() + ")";
        }
        HashSet<Grids_AbstractGrid> grids;
        grids = ge.getGrids();
        if (grids == null) {
            result += ",Grids(null)";
        } else {
            result += ",Grids.size(" + grids.size() + ")";
        }
        result += ")";
        return result;
    }

    /**
     * @return Directory.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public File getDirectory(boolean handleOutOfMemoryError) {
        try {
            File result = getDirectory();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getDirectory(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Directory.
     */
    protected File getDirectory() {
        return new File(Directory.getPath());
    }

    /**
     * @return Name.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public String getName(boolean handleOutOfMemoryError) {
        try {
            String result = Name;
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getName(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Sets Name to be name.
     *
     * @param name The String this.Name is set to.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public void setName(String name, boolean handleOutOfMemoryError) {
        try {
            setName(name);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                setName(name, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Sets Name to name.
     *
     * @param name The String Name is set to.
     */
    protected void setName(String name) {
        Name = name;
    }

    /**
     * @return a basic description of this instance.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public String getBasicDescription(boolean handleOutOfMemoryError) {
        try {
            String result = getBasicDescription();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getBasicDescription(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return a basic description of this instance.
     */
    protected String getBasicDescription() {
        return "className(" + this.getClass().getName() + "),"
                + "Directory(" + this.getDirectory() + "),"
                + "nrows(" + NRows + "),"
                + "ncols(" + NCols + "),"
                + "chunkNrows(" + ChunkNRows + "),"
                + "chunkNcols(" + ChunkNCols + ")";
    }

    /**
     * Sets the Grids_AbstractGridChunk with Grids_2D_ID_int equal to _ChunkID
     * to chunk.
     *
     * @param chunk
     * @param chunkID The Grids_2D_ID_int of the chunk that is set.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public void setChunk(Grids_AbstractGridChunk chunk, Grids_2D_ID_int chunkID, 
            boolean handleOutOfMemoryError) {
        try {
            ChunkIDChunkMap.put(chunkID, chunk);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                setChunk(chunk, chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return NCols.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final long getNCols(boolean handleOutOfMemoryError) {
        try {
            long result = NCols;
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getNCols(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return NRows.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final long getNRows(boolean handleOutOfMemoryError) {
        try {
            long result = NRows;
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getNRows(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return NChunkRows.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getNChunkRows(boolean handleOutOfMemoryError) {
        try {
            int result = NChunkRows;
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getNChunkRows(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises NChunkRows.
     */
    protected final void initNChunkRows() {
        long chunkNrows = (long) ChunkNRows;
        if ((NRows % chunkNrows) != 0) {
            NChunkRows = (int) (NRows / chunkNrows) + 1;
        } else {
            NChunkRows = (int) (NRows / chunkNrows);
        }
    }

    /**
     * @return NChunkCols.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getNChunkCols(boolean handleOutOfMemoryError) {
        try {
            int result = NChunkCols;
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getNChunkCols(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return The number of chunks in this as a
     * long.
     */
    public final long getNChunks(boolean handleOutOfMemoryError) {
        try {
            long result = getNChunks();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getNChunks(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return the number of Grid2DSquareCellDoubleChunkAbstracts in this as a
     * long.
     */
    protected final long getNChunks() {
        long nChunks = (long) NChunkRows * (long) NChunkCols;
        if (nChunks > Integer.MAX_VALUE) {
            System.err.println("Error nChunks > Integer.MAX_VALUE in "
                    + this.getClass().getName() + ".getNChunks()");
            throw new Error();
        }
        return nChunks;
    }

    /**
     * Initialises NChunkCols.
     */
    protected final void initNChunkCols() {
        long chunkNCols = (long) ChunkNCols;
        if ((NCols % chunkNCols) != 0) {
            NChunkCols = (int) (NCols / chunkNCols) + 1;
        } else {
            NChunkCols = (int) (NCols / chunkNCols);
        }
    }

    /**
     * @return ChunkNRows.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkNRows(boolean handleOutOfMemoryError) {
        try {
            int result = getChunkNRows();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkNRows(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected final int getChunkNRows() {
        return ChunkNRows;
    }

    /**
     * @param chunkRowIndex
     * @return The number of rows in the chunks in the chunk row chunkRowIndex.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkNRows(int chunkRowIndex, boolean handleOutOfMemoryError) {
        try {
            int result = getChunkNRows(chunkRowIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkNRows(chunkRowIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @return The number of rows in the chunks in the chunk row chunkRowIndex.
     */
    protected final int getChunkNRows(int chunkRowIndex) {
        if (chunkRowIndex > -1 && chunkRowIndex < NChunkRows) {
            if (chunkRowIndex == (NChunkRows - 1)) {
                return getChunkNRowsFinalRowChunks();
            } else {
                return ChunkNRows;
            }
        } else {
            return 0;
        }
    }

    /**
     * @return ChunkNCols.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkNCols(boolean handleOutOfMemoryError) {
        try {
            int result = ChunkNCols;
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkNCols(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkColIndex
     * @return The number of columns in the chunks in the chunk column chunkColIndex.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkNCols(int chunkColIndex, boolean handleOutOfMemoryError) {
        try {
            int result = getChunkNCols(chunkColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkNCols(chunkColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkColIndex
     * @return The number of columns in the chunks in the chunk column chunkColIndex.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @param chunkID This is a Grids_2D_ID_int for those
     * AbstractGrid2DSquareCells not to be swapped if possible when an
     * OutOfMemoryError is encountered.
     */
    public final int getChunkNCols(int chunkColIndex,
            boolean handleOutOfMemoryError, Grids_2D_ID_int chunkID) {
        try {
            int result = getChunkNCols(chunkColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getChunkNCols(chunkColIndex, handleOutOfMemoryError, chunkID);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkColIndex
          * @return The number of columns in the chunks in the chunk column chunkColIndex.
     */
    protected final int getChunkNCols(int chunkColIndex) {
        if (chunkColIndex > -1 && chunkColIndex < NChunkCols) {
            if (chunkColIndex == (NChunkCols - 1)) {
                return getChunkNColsFinalColChunks();
            } else {
                return ChunkNCols;
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
    protected final int getChunkNrowsFinalRowChunks(boolean handleOutOfMemoryError) {
        try {
            return getChunkNRowsFinalRowChunks();
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkNrowsFinalRowChunks(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return the number of rows in the chunks in the final row.
     */
    protected final int getChunkNRowsFinalRowChunks() {
        long nChunkRowsMinusOne = (long) (NChunkRows - 1);
        long chunkNRows = (long) ChunkNRows;
        return (int) (NRows - (nChunkRowsMinusOne * chunkNRows));
    }

    /**
     * @return the number of columns in the chunks in the final column.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected final int getChunkNColsFinalColChunks(boolean handleOutOfMemoryError) {
        try {
            return getChunkNColsFinalColChunks();
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return Grids_AbstractGrid.this.getChunkNColsFinalColChunks(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return the number of columns in the chunks in the final column.
     */
    protected final int getChunkNColsFinalColChunks() {
        long nChunkColsMinusOne = (long) (NChunkCols - 1);
        long chunkNCols = (long) ChunkNCols;
        return (int) (NCols - (nChunkColsMinusOne * chunkNCols));
    }

    /**
     * @return _ChunkNRows, the number of rows in Grids_AbstractGridChunk with
     * Grids_2D_ID_int equal to _ChunkID
     * @param chunkID The Grids_2D_ID_int of the Grids_AbstractGridChunk thats
     * number of rows is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkNRows(Grids_2D_ID_int chunkID, boolean handleOutOfMemoryError) {
        try {
            int result = getChunkNRows(chunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getChunkNRows(chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return _ChunkNRows, the number of rows in Grids_AbstractGridChunk with
     * Grids_2D_ID_int equal to _ChunkID
     * @param chunkID The Grids_2D_ID_int of the Grids_AbstractGridChunk thats
     * number of rows is returned.
     */
    protected final int getChunkNRows(Grids_2D_ID_int chunkID) {
        if (chunkID.getRow() < (NChunkRows - 1)) {
            return ChunkNRows;
        } else {
            return getChunkNRowsFinalRowChunks();
        }
    }

    /**
     * @return _ChunkNCols, the number of columns in Grids_AbstractGridChunk
     * with Grids_2D_ID_int equal to _ChunkID
     * @param chunkID The Grids_2D_ID_int of the Grids_AbstractGridChunk thats
     * number of columns is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkNCols(Grids_2D_ID_int chunkID, boolean handleOutOfMemoryError) {
        try {
            int result = getChunkNCols(chunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getChunkNCols(chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return _ChunkNCols, the number of columns in Grids_AbstractGridChunk
     * with Grids_2D_ID_int equal to _ChunkID
     * @param chunkID The Grids_2D_ID_int of the Grids_AbstractGridChunk thats
     * number of columns is returned.
     */
    protected final int getChunkNCols(Grids_2D_ID_int chunkID) {
        if (chunkID.getCol() < (NChunkCols - 1)) {
            return ChunkNCols;
        } else {
            return getChunkNColsFinalColChunks();
        }
    }

    /**
     * @return Dimensions
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_Dimensions getDimensions(boolean handleOutOfMemoryError) {
        try {
            Grids_Dimensions result;
            result = getDimensions();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getDimensions(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return the Dimensions
     */
    public Grids_Dimensions getDimensions() {
        return Dimensions;
    }

    /**
     * This method is for convenience.
     *
     * @return BigDecimal equal to this.Dimensions.getCellsize().
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final BigDecimal getCellsize(boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getDimensions().getCellsize();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellsize(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * This method is for convenience.
     *
     * @return double equal to this.Dimensions[0].doubleValue.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellsizeDouble(boolean handleOutOfMemoryError) {
        try {
            double result = getCellsize(handleOutOfMemoryError).doubleValue();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellsizeDouble(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

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
            double distance, double x, double y, long cellRowIndex, long cellColIndex) {
        HashSet<Grids_2D_ID_int> result = new HashSet<>();
        long p;
        long q;
        long cellDistance = (long) Math.ceil(distance / getCellsizeDouble(false));
        double thisCellX;
        double thisCellY;
        double thisDistance;
        for (p = -cellDistance; p <= cellDistance; p++) {
            thisCellY = getCellYDouble(cellRowIndex + p);
            for (q = -cellDistance; q <= cellDistance; q++) {
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
     * @return Chunk column index for the Grids_AbstractGridChunk intersecting
     * the x-coordinate x.
     *
     * @param x The x-coordinate of the line intersecting the chunk column index
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkColIndex(double x, boolean handleOutOfMemoryError) {
        try {
            int result = getChunkColIndex(x);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkColIndex(x, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Chunk column index for the Grids_AbstractGridChunk intersecting
     * the x-coordinate x.
     * @param x The x-coordinate of the line intersecting the chunk column index
     * returned.
     */
    protected final int getChunkColIndex(double x) {
        return getChunkColIndex(getCellColIndex(x));
    }

    /**
     * @return Chunk column index for the Grids_AbstractGridChunk intersecting
     * the cell column index _CellColIndex.
     * @param cellColIndex
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getChunkColIndex(long cellColIndex, boolean handleOutOfMemoryError) {
        try {
            int result = getChunkColIndex(cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkColIndex(cellColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Chunk column index for the Grids_AbstractGridChunk intersecting
     * the cell column index _CellColIndex.
     *
     * @param cellColIndex
     */
    protected final int getChunkColIndex(long cellColIndex) {
        return (int) (cellColIndex / (long) ChunkNCols);
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
    public final long getCellColIndex(double x, boolean handleOutOfMemoryError) {
        try {
            long result = getCellColIndex(x);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellColIndex(x, handleOutOfMemoryError);
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
    protected final long getCellColIndex(double x) {
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
    public final long getCellColIndex(BigDecimal xBigDecimal, boolean handleOutOfMemoryError) {
        try {
            long result = getCellColIndex(xBigDecimal);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellColIndex(xBigDecimal, handleOutOfMemoryError);
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
    protected final long getCellColIndex(BigDecimal x_BigDecimal) {
        Grids_Dimensions gd;
        gd = getDimensions();
        BigDecimal xMinusXMin;
        xMinusXMin = x_BigDecimal.subtract(gd.getXMin());
        BigDecimal div;
        div = Generic_BigDecimal.divideRoundIfNecessary(
                xMinusXMin,
                gd.getCellsize(),
                0,
                RoundingMode.DOWN);
        return div.toBigInteger().longValue();
        //        return xMinusMinX_BigDecimal.divide(
        //                this.Dimensions[0]).toBigInteger().longValue();
    }

    /**
     * @param chunkColIndex
     * @return Cell column index for the cells in chunk column index _Col chunk
     * cell column index chunkCellColIndex.
     * @param chunkCellColIndex
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final long getCellColIndex(int chunkColIndex, int chunkCellColIndex, boolean handleOutOfMemoryError) {
        try {
            long result = getCellColIndex(chunkColIndex, chunkCellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellColIndex(chunkColIndex, chunkCellColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkColIndex
     * @return Cell column index for the cells in chunk column index _Col chunk
     * cell column index chunkCellColIndex.
     * @param chunkCellColIndex
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @param chunkID This is a Grids_2D_ID_int for those
     * AbstractGrid2DSquareCells not to be swapped if possible when an
     * OutOfMemoryError is encountered.
     */
    public final long getCellColIndex(int chunkColIndex, int chunkCellColIndex, Grids_2D_ID_int chunkID, boolean handleOutOfMemoryError) {
        try {
            long result = getCellColIndex(chunkColIndex, chunkCellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(this, chunkID, handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellColIndex(chunkColIndex, chunkCellColIndex, chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkColIndex
     * @return Cell column index for the cells in chunk column index _Col chunk
     * cell column index chunkCellColIndex.
     * @param chunkCellColIndex
     */
    protected final long getCellColIndex(int chunkColIndex, int chunkCellColIndex) {
        return ((long) chunkColIndex * (long) ChunkNCols) + (long) chunkCellColIndex;
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
    public final int getChunkCellColIndex(double x, boolean handleOutOfMemoryError) {
        try {
            int result = getChunkCellColIndex(x);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkCellColIndex(x, handleOutOfMemoryError);
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
    protected final int getChunkCellColIndex(double x) {
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
    public final int getChunkCellColIndex(long cellColIndex, boolean handleOutOfMemoryError) {
        try {
            int result = getChunkCellColIndex(cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkCellColIndex(cellColIndex, handleOutOfMemoryError);
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
    protected final int getChunkCellColIndex(long cellColIndex) {
        return (int) (cellColIndex - ((cellColIndex / ChunkNCols) * ChunkNCols));
    }

    /**
     * @param a_Random
     * @param handleOutOfMemoryError
     * @return A Random CellColIndex.
     */
    public final long getCellColIndex(Random a_Random, boolean handleOutOfMemoryError) {
        try {
            long result = getCellColIndex(a_Random);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellColIndex(a_Random, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param a_Random
     * @return A Random CellColIndex.
     */
    protected final long getCellColIndex(Random a_Random) {
        if (NCols < Integer.MAX_VALUE) {
            return a_Random.nextInt((int) NCols);
        } else {
            long col = 0;
            long colMax = 0;
            while (colMax < NCols) {
                colMax += Integer.MAX_VALUE;
                if (colMax < NCols) {
                    col += a_Random.nextInt();
                } else {
                    int colInt = (int) (colMax - NCols);
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
    public final int getChunkRowIndex(double y, boolean handleOutOfMemoryError) {
        try {
            int result = getChunkRowIndex(y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkRowIndex(y, handleOutOfMemoryError);
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
    protected final int getChunkRowIndex(double y) {
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
    public final int getChunkRowIndex(long cellRowIndex, boolean handleOutOfMemoryError) {
        try {
            int result = getChunkRowIndex(cellRowIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkRowIndex(cellRowIndex, handleOutOfMemoryError);
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
    protected final int getChunkRowIndex(long cellRowIndex) {
        return (int) (cellRowIndex / (long) ChunkNRows);
    }

    /**
     * @return Cell row Index for the cells that intersect the line with
     * y-coordinate y.
     * @param y The y-coordinate of the line thats cell row index is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final long getCellRowIndex(double y, boolean handleOutOfMemoryError) {
        try {
            long result = getCellRowIndex(y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellRowIndex(y, handleOutOfMemoryError);
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
    protected final long getCellRowIndex(double y) {
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
    public final long getCellRowIndex(BigDecimal yBigDecimal, boolean handleOutOfMemoryError) {
        try {
            long result = getCellRowIndex(yBigDecimal);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellRowIndex(yBigDecimal, handleOutOfMemoryError);
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
    protected final long getCellRowIndex(BigDecimal y_BigDecimal) {
        Grids_Dimensions gd;
        gd = getDimensions();
        BigDecimal yMinusYMin;
        yMinusYMin = y_BigDecimal.subtract(gd.getYMin());
        BigDecimal div;
        div = Generic_BigDecimal.divideRoundIfNecessary(
                yMinusYMin,
                gd.getCellsize(),
                0,
                RoundingMode.DOWN);
        return div.toBigInteger().longValue();
    }

    /**
     * @param chunkRowIndex
     * @param chunkCellRowIndex
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return long CellRowIndex, the cell row index for the cells in chunk row
     * index _Row chunk cell row index chunkCellRowIndex.
     */
    public final long getCellRowIndex(int chunkRowIndex, int chunkCellRowIndex, boolean handleOutOfMemoryError) {
        try {
            long result = getCellRowIndex(chunkRowIndex, chunkCellRowIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellRowIndex(chunkRowIndex, chunkCellRowIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @param chunkCellRowIndex
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @param chunkID This is a Grids_2D_ID_int for those
     * AbstractGrid2DSquareCells not to be swapped if possible when an
     * OutOfMemoryError is encountered.
     * @return long CellRowIndex, the cell row index for the cells in chunk row
     * index _Row chunk cell row index chunkCellRowIndex.
     */
    public final long getCellRowIndex(int chunkRowIndex, int chunkCellRowIndex, Grids_2D_ID_int chunkID, boolean handleOutOfMemoryError) {
        try {
            long result = getCellRowIndex(chunkRowIndex, chunkCellRowIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(this, chunkID, handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellRowIndex(chunkRowIndex, chunkCellRowIndex, chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * If OutOfMemoryError is caught then an attempt is made to swap an
     * Grids_AbstractGridChunk not in _Grid2DSquareCell_ChunkIDHashSet. If this
     * is not done then any Grids_AbstractGridChunk is swapped and a warning is
     * printed to sout
     *
     * @param chunkRowIndex
     * @param chunkCellRowIndex
     * @param a_Grid2DSquareCell_ChunkID_HashSet_HashMap
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return long CellRowIndex, the cell row index for the cells in chunk row
     * index _Row chunk cell row index chunkCellRowIndex.
     */
    public final long getCellRowIndex(int chunkRowIndex, int chunkCellRowIndex, HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> a_Grid2DSquareCell_ChunkID_HashSet_HashMap, boolean handleOutOfMemoryError) {
        try {
            long result = getCellRowIndex(chunkRowIndex, chunkCellRowIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(a_Grid2DSquareCell_ChunkID_HashSet_HashMap, handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(a_Grid2DSquareCell_ChunkID_HashSet_HashMap, e);
                return getCellRowIndex(chunkRowIndex, chunkCellRowIndex, a_Grid2DSquareCell_ChunkID_HashSet_HashMap, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @return CellRowIndex for the cells in chunk _Row, chunk cell column index
     * chunkCellRowIndex.
     * @param chunkCellRowIndex
     */
    protected final long getCellRowIndex(int chunkRowIndex, int chunkCellRowIndex) {
        return ((long) chunkRowIndex * (long) ChunkNRows) + (long) chunkCellRowIndex;
    }

    /**
     * @param a_Random
     * @param handleOutOfMemoryError
     * @return A Random CellRowIndex.
     */
    public final long getCellRowIndex(Random a_Random, boolean handleOutOfMemoryError) {
        try {
            long result = getCellRowIndex(a_Random);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellRowIndex(a_Random, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param a_Random
     * @return A Random CellRowIndex.
     */
    protected final long getCellRowIndex(Random a_Random) {
        if (NRows < Integer.MAX_VALUE) {
            return a_Random.nextInt((int) NRows);
        } else {
            long row = 0;
            long rowMax = 0;
            while (rowMax < NRows) {
                rowMax += Integer.MAX_VALUE;
                if (rowMax < NRows) {
                    row += a_Random.nextInt();
                } else {
                    int rowInt = (int) (rowMax - NRows);
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
    public final int getChunkCellRowIndex(double y, boolean handleOutOfMemoryError) {
        try {
            int result = getChunkCellRowIndex(y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkCellRowIndex(y, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Chunk cell row Index of the cells that intersects the line with
     * y-coordinate y.
     * @param y The y-coordinate of the line for which the chunk cell row index
     * is returned.
     */
    protected final int getChunkCellRowIndex(double y) {
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
    public final int getChunkCellRowIndex(long a_CellRowIndex, boolean handleOutOfMemoryError) {
        try {
            int result = getChunkCellRowIndex(a_CellRowIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getChunkCellRowIndex(a_CellRowIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Chunk cell row index of the cells with cell row index equal to
     * _CellRowIndex.
     * @param cellRowIndex The cell row index of the cells that chunk cell row
     * index is returned.
     */
    protected final int getChunkCellRowIndex(long cellRowIndex) {
        return (int) (cellRowIndex - ((cellRowIndex / ChunkNRows) * ChunkNRows));
    }

    /**
     * @param a_CellRowIndex
     * @param a_CellColIndex
     * @return Grids_2D_ID_long of the cell given by cell row index
     * _CellRowIndex, cell column index _CellColIndex. A Grids_2D_ID_long is
     * returned even if that cell would not be in the grid.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long getCellID(long a_CellRowIndex, long a_CellColIndex, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long result = getCellID(a_CellRowIndex, a_CellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellID(a_CellRowIndex, a_CellColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param cellRowIndex
     * @param cellColIndex
     * @return Grids_2D_ID_long of the cell given by cell row index
     * _CellRowIndex, cell column index _CellColIndex. A Grids_2D_ID_long is
     * returned even if that cell would not be in the grid.
     */
    protected final Grids_2D_ID_long getCellID(long cellRowIndex, long cellColIndex) {
        return new Grids_2D_ID_long(cellRowIndex, cellColIndex);
    }

    /**
     * @return Grids_2D_ID_long of the cell given by x-coordinate x,
     * y-coordinate y even if that cell would not be in the grid.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long getCellID(double x, double y, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long result = getCellID(x, y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellID(x, y, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long of the cell given by x-coordinate x,
     * y-coordinate y even if that cell would not be in the grid.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    protected final Grids_2D_ID_long getCellID(double x, double y) {
        return new Grids_2D_ID_long(getCellRowIndex(y), getCellColIndex(x));
    }

    /**
     * @return Grids_2D_ID_long of the cell given by x-coordinate x,
     * y-coordinate y even if that cell would not be in the grid.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long getCellID(BigDecimal x, BigDecimal y, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long result = getCellID(x, y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellID(x, y, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long of the cell given by x-coordinate x,
     * y-coordinate y even if that cell would not be in the grid.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    protected final Grids_2D_ID_long getCellID(BigDecimal x, BigDecimal y) {
        return new Grids_2D_ID_long(getCellRowIndex(y), getCellColIndex(x));
    }

    /**
     * Attempts to write this instance to Files located in the _Directory
     * returned by getDirectory(). First attempts to do this without swapping
     * out data, but if this fails because an OutOfMemoryError is encountered
     * then it retires swapping out chunks as it goes.
     *
     * @param swapToFileCache Iff true then
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap is written to new
     * File( getDirectory(), "cache" ).
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @throws java.io.IOException
     */
    public final void writeToFile(boolean swapToFileCache, boolean handleOutOfMemoryError) throws IOException {
        try {
            writeToFile(swapToFileCache);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                writeToFileSwapping(swapToFileCache);
                ge.initMemoryReserve(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to write this instance to Files located in the _Directory
     * returned by getDirectory(). Chunks are all written but no data is swapped
     *
     * @param swapToFileCache Iff true then
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap is written to new
     * File(getDirectory(),"cache").
     * @throws java.io.IOException
     */
    protected void writeToFile(boolean swapToFileCache) throws IOException {
        try {
            writeToFileChunks();
            ObjectOutputStream oos;
            File file;
            if (swapToFileCache) {
                // Write out thisCache
                file = new File(this.getDirectory(), "cache");
                oos = Generic_StaticIO.getObjectOutputStream(file);
                oos.writeObject(ChunkIDChunkMap);
                oos.flush();
                oos.close();
            }
            // Write out this.
            file = new File(this.getDirectory(), "thisFile");
            oos = Generic_StaticIO.getObjectOutputStream(file);
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
     * returned by getDirectory(). Chunks are all swapped to file.
     *
     * @param swapToFileCache Iff true then
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap is written to new
     * File(getDirectory(),"cache").
     * @throws java.io.IOException
     */
    protected void writeToFileSwapping(boolean swapToFileCache) throws IOException {
        try {
            swapChunks();
            ObjectOutputStream oos;
            File file;
            if (swapToFileCache) {
                // Write out thisCache
                file = new File(this.getDirectory(), "cache");
                oos = Generic_StaticIO.getObjectOutputStream(file);
                oos.writeObject(ChunkIDChunkMap);
                oos.flush();
                oos.close();
            }
            // Write out this.
            file = new File(this.getDirectory(), "thisFile");
            oos = Generic_StaticIO.getObjectOutputStream(file);
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
     * Grids_AbstractGridChunk in
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap. The first
     * Grids_AbstractGridChunk attempted to be written is that with a chunk row
     * index of 0, and a chunk column index of 0.
     *
     * @return Grids_2D_ID_int of the Grids_AbstractGridChunk which was swapped
     * or null.
     */
    public final Grids_2D_ID_int writeToFileChunk() {
        if (ChunkIDChunkMap.isEmpty()) {
            return null;
        }
        Grids_2D_ID_int chunkID;
        // First try to swap any chunk not in ge.getNotToSwapData(). 
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> notToSwapData;
        notToSwapData = ge.getNotToSwapData();
        Iterator<Grids_2D_ID_int> ite;
        ite = ChunkIDChunkMap.keySet().iterator();
        while (ite.hasNext()) {
            chunkID = ite.next();
            if (notToSwapData.containsKey(this)) {
                if (!notToSwapData.get(this).contains(chunkID)) {
                    writeToFileChunk(chunkID);
                    return chunkID;
                }
            } else {
                writeToFileChunk(chunkID);
                return chunkID;
            }
        }
        /**
         * If not managed to swap any chunk not in ge.getNotToSwapData(), then
         * issue a warning and swap one that is in ge.getNotToSwapData() or
         * return null.
         */
        System.out.println("Not managed to swap any chunk not in ge.getNotToSwapData()!!!");
        ite = ChunkIDChunkMap.keySet().iterator();
        while (ite.hasNext()) {
            chunkID = ite.next();
            writeToFileChunk(chunkID);
            return chunkID;
        }
        return null;
    }

    /**
     * Attempts to write to File a seriailized version of the
     * Grids_AbstractGridChunk in
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap given by _ChunkID.
     *
     * @param chunkID The ID of the Chunk. to be written.
     * @return True if Grids_AbstractGridChunk on file is up to date.
     */
    public final boolean writeToFileChunk(
            Grids_2D_ID_int chunkID) {
        boolean result = true;
        Grids_AbstractGridChunk gridChunk;
        gridChunk = ChunkIDChunkMap.get(chunkID);
        if (gridChunk != null) {
            if (!gridChunk.isSwapUpToDate()) {
                File file = new File(
                        getDirectory(),
                        chunkID.getRow() + "_" + chunkID.getCol());
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                    ObjectOutputStream oos;
                    oos = Generic_StaticIO.getObjectOutputStream(file);
                    oos.writeObject(gridChunk);
                    oos.flush();
                    oos.close();
                } catch (IOException ioe0) {
                    //ioe0.printStackTrace();
                    System.err.println(ioe0.getMessage());
                }
                System.gc();
                gridChunk.setSwapUpToDate(true, ge.HandleOutOfMemoryErrorFalse);
            }
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Attempts to write to File serialised versions of all
     * Grids_AbstractGridChunk in ChunkIDChunkMap.
     */
    protected final void writeToFileChunks() {
        Iterator ite;
        ite = ChunkIDChunkMap.keySet().iterator();
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            chunkID = (Grids_2D_ID_int) ite.next();
            if (isInCache(chunkID)) {
                writeToFileChunk(chunkID);
            }
        }
    }

    /**
     * Attempts to write to File serialized versions of those
     * Grids_AbstractGridChunk in
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap that have ID equal to
     * those in _ChunkIDs.
     *
     * @param a_ChunkID_HashSet A HashSet containing the Grids_2D_ID_int of the
     * Grids_AbstractGridChunk to be written to file.
     * @param handleOutOfMemoryError
     */
    public final void writeToFileChunks(
            HashSet<Grids_2D_ID_int> a_ChunkID_HashSet,
            boolean handleOutOfMemoryError) {
        Iterator<Grids_2D_ID_int> ite;
        ite = a_ChunkID_HashSet.iterator();
        Grids_2D_ID_int id;
        while (ite.hasNext()) {
            id = ite.next();
            if (isInCache(id)) {
                writeToFileChunk(id);
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache a
     * Grids_AbstractGridChunk in this. This is one of the lowest level memory
     * handling operation of this class.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return HashMap<Grids_AbstractGrid,ID> for accounting what was swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunk_AccountDetail(
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunk_AccountDetail();
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
            potentialPartResult = ge.tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(handleOutOfMemoryError);
            ge.combine(result, potentialPartResult);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunk_AccountDetail();
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = ge.initMemoryReserve_AccountDetail(handleOutOfMemoryError);
                ge.combine(result, potentialPartResult);
                potentialPartResult = swapChunk_AccountDetail(handleOutOfMemoryError);
                ge.combine(result, potentialPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunk_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        Grids_2D_ID_int chunkID = writeToFileChunk();
        clearFromCacheChunk(chunkID);
        HashSet<Grids_2D_ID_int> chunks = new HashSet<>(1);
        chunks.add(chunkID);
        result.put(this, chunks);
        return result;
    }

    /**
     * @param handleOutOfMemoryError
     * @return
     */
    public Grids_2D_ID_int swapChunk_AccountChunk(
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_int result;
            result = swapChunk_AccountChunk();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int result;
                result = swapChunk_AccountChunk();
                if (result == null) {
                    if (ge.swapChunk_Account(false) < 1L) {
                        throw e;
                    }
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return swapChunk_AccountChunk(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected Grids_2D_ID_int swapChunk_AccountChunk() {
        Grids_2D_ID_int id;
        id = writeToFileChunk();
        if (id != null) {
            clearFromCacheChunk(id);
        }
        return id;
    }

    protected long swapChunks_Account() {
        long result = 0L;
        int cri;
        int cci;
        Grids_2D_ID_int id;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                id = new Grids_2D_ID_int(cri, cci);
                if (writeToFileChunk(id)) {
                    clearFromCacheChunk(id);
                    result++;
                }
            }
        }
        return result;
    }

    protected long swapChunks_Account(Set<Grids_2D_ID_int> b_ChunkID_Set) {
        long result = 0L;
        Iterator<Grids_2D_ID_int> a_Iterator = b_ChunkID_Set.iterator();
        Grids_2D_ID_int id;
        while (a_Iterator.hasNext()) {
            id = a_Iterator.next();
            if (writeToFileChunk(id)) {
                clearFromCacheChunk(id);
                result++;
            }
        }
        return result;
    }

    public Grids_2D_ID_int swapChunkExcept_AccountChunk(
            HashSet<Grids_2D_ID_int> chunkIDs, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_int result;
            result = swapChunkExcept_AccountChunk(chunkIDs);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int result;
                result = swapChunkExcept_AccountChunk(chunkIDs);
                if (result == null) {
                    if (ge.swapChunk_Account(false) < 1L) {
                        throw e;
                    }
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected Grids_2D_ID_int swapChunkExcept_AccountChunk(
            HashSet<Grids_2D_ID_int> chunkIDs) {
        Grids_2D_ID_int id = null;
        int cri;
        int cci;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                id = new Grids_2D_ID_int(cri, cci);
                if (!chunkIDs.contains(id)) {
                    if (isInCache(id)) {
                        writeToFileChunk(id);
                        clearFromCacheChunk(id);
                        return id;
                    }
                }
            }
        }
        return id;
    }

    public void swapChunk(Grids_2D_ID_int chunkID, boolean handleOutOfMemoryError) {
        try {
            swapChunk(chunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                swapChunk(chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected void swapChunk(Grids_2D_ID_int chunkID) {
        if (writeToFileChunk(chunkID)) {
            clearFromCacheChunk(chunkID);
        }
    }

    /**
     * Attempts to write to file and clear from the cache a
     * Grids_AbstractGridChunk in this. This is one of the lowest level memory
     * handling operation of this class.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public void swapChunk(boolean handleOutOfMemoryError) {
        try {
            swapChunk();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                swapChunk(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected void swapChunk() {
        Grids_2D_ID_int id;
        id = writeToFileChunk();
        if (id != null) {
            clearFromCacheChunk(id);
        }
    }

    public long swapChunk_Account(boolean handleOutOfMemoryError) {
        try {
            long result = swapChunk_Account();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                long result = swapChunk_Account();
                if (result < 1L) {
                    if (ge.swapChunk_Account(false) < 1L) {
                        throw e;
                    }
                }
                result += ge.initMemoryReserve_Account(handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    public long swapChunk_Account() {
        Grids_2D_ID_int id;
        id = writeToFileChunk();
        if (id != null) {
            clearFromCacheChunk(id);
            return 1L;
        }
        return 0L;
    }

    /**
     * Attempts to write to file and clear from the cache a
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet.
     *
     * @param chunkID A Grids_2D_ID_int not to be swapped
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return The Grids_2D_ID_int of Grids_AbstractGridChunk swapped or null.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunkExcept_AccountDetail(Grids_2D_ID_int chunkID, boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunkExcept_AccountDetail(chunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(chunkID);
                if (result.isEmpty()) {
                    result = ge.swapChunkExcept_AccountDetail(
                            this, chunkID, false);
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = ge.initMemoryReserve_AccountDetail(this, chunkID, handleOutOfMemoryError);
                ge.combine(result, potentialPartResult);
                potentialPartResult = swapChunkExcept_AccountDetail(chunkID);
                ge.combine(result, potentialPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache a
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet.
     *
     * @param chunkID
     * @return The Grids_2D_ID_int of Grids_AbstractGridChunk swapped or null.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunkExcept_AccountDetail(
            Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        int cri;
        int cci;
        Grids_2D_ID_int bid;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                bid = new Grids_2D_ID_int(cri, cci);
                if (!bid.equals(chunkID)) {
                    if (isInCache(bid)) {
                        writeToFileChunk(bid);
                        clearFromCacheChunk(bid);
                        HashSet<Grids_2D_ID_int> chunks;
                        chunks = new HashSet<>(1);
                        chunks.add(bid);
                        result.put(this, chunks);
                        return result;
                    }
                }
            }
        }
        return result;
    }

    public Grids_2D_ID_int swapChunkExcept_AccountChunk(
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_int result = swapChunkExcept_AccountChunk(chunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int result;
                result = swapChunkExcept_AccountChunk(chunkID);
                if (result == null) {
                    if (ge.swapChunkExcept_Account(this, chunkID, false) < 1L) {
                        throw e;
                    }
                }
                ge.initMemoryReserve(this, chunkID, handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected Grids_2D_ID_int swapChunkExcept_AccountChunk(
            Grids_2D_ID_int chunkID) {
        Grids_2D_ID_int result = null;
        int cri;
        int cci;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                result = new Grids_2D_ID_int(cri, cci);
                if (!result.equals(chunkID)) {
                    if (isInCache(result)) {
                        writeToFileChunk(result);
                        clearFromCacheChunk(result);
                        return result;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Attempts to write to file and clear from the cache all
     * Grid2DSquareCellChunkAbstracts in this except that with ID a_ChunkID.
     *
     * @param chunkID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGridChunk swapped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunksExcept_AccountDetail(Grids_2D_ID_int chunkID, boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunksExcept_AccountDetail(chunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(chunkID);
                if (result.isEmpty()) {
                    result = ge.swapChunkExcept_AccountDetail(
                            this,
                            chunkID,
                            false);
                    if (result.isEmpty()) {
                        throw e;
                    }
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = ge.initMemoryReserve_AccountDetail(this, chunkID, handleOutOfMemoryError);
                ge.combine(result, potentialPartResult);
                potentialPartResult = swapChunksExcept_AccountDetail(chunkID, handleOutOfMemoryError);
                ge.combine(result, potentialPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache all
     * Grid2DSquareCellChunkAbstracts in this except that with Grids_2D_ID_int
     * _ChunkID.
     *
     * @param chunkID
     * @return A HashSet with the ChunkIDs of those Grids_AbstractGridChunk
     * swapped.
     */
    protected final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunksExcept_AccountDetail(Grids_2D_ID_int chunkID) {
        // Using default as not sure if what to use as initialCapacity for result...
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        HashSet<Grids_2D_ID_int> chunksSwapped = new HashSet<>();
        int cri;
        int cci;
        Grids_2D_ID_int chunkIDToSwap;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkIDToSwap = new Grids_2D_ID_int(cri, cci);
                if (!chunkID.equals(chunkIDToSwap)) {
                    if (isInCache(chunkIDToSwap)) {
                        writeToFileChunk(chunkIDToSwap);
                        clearFromCacheChunk(chunkIDToSwap);
                        chunksSwapped.add(chunkIDToSwap);
                    }
                }
            }
        }
        result.put(this, chunksSwapped);
        return result;
    }

    public void swapChunks(boolean handleOutOfMemoryError) {
        try {
            swapChunks();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                swapChunks(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    protected void swapChunks() {
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri, cci);
                if (isInCache(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearFromCacheChunk(chunkID);
                }
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache all
     * Grid2DSquareCellChunkAbstracts in this except that with ID _ChunkIDs.
     *
     * @param chunks
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGridChunk swapped.
     */
    public long swapChunksExcept_Account(
            HashSet<Grids_2D_ID_int> chunks,
            boolean handleOutOfMemoryError) {
        try {
            long result = swapChunksExcept_Account(chunks);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                long result = ge.swapChunkExcept_Account(this, chunks, false);
                if (result < 1L) {
                    result = ge.swapChunkExcept_Account(this, chunks, false);
                    if (result < 1L) {
                        throw e;
                    }
                }
                result += ge.initMemoryReserve_Account(this, chunks, handleOutOfMemoryError);
                result += swapChunksExcept_Account(chunks, handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache all
     * Grid2DSquareCellChunkAbstracts in this except that with Grids_2D_ID_int
     * _ChunkID.
     *
     * @param chunkIDs HashSet of Grids_AbstractGridChunk.ChunkIDs not to be
     * swapped.
     * @return A HashSet with the ChunkIDs of those Grids_AbstractGridChunk
     * swapped.
     */
    protected final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunksExcept_AccountDetail(
            HashSet<Grids_2D_ID_int> chunkIDs) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        HashSet<Grids_2D_ID_int> chunkIDs2 = new HashSet<>();
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri, cci);
                if (!chunkIDs.contains(chunkID)) {
                    if (isInCache(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearFromCacheChunk(chunkID);
                        chunkIDs2.add(chunkID);
                    }
                }
            }
        }
        result.put(this, chunkIDs2);
        return result;
    }

    public long swapChunkExcept_Account(
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        int cri;
        int cci;
        Grids_2D_ID_int id2;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                id2 = new Grids_2D_ID_int(cri, cci);
                if (!chunkID.equals(id2)) {
                    if (isInCache(id2)) {
                        writeToFileChunk(                                id2);
                        clearFromCacheChunk(id2);
                        return 1L;
                    }
                }
            }
        }
        return 0L;
    }

    public long swapChunksExcept_Account(
            Grids_2D_ID_int chunkID,
            boolean handleOutOfMemoryError) {
        long result = 0L;
        int cri;
        int cci;
        Grids_2D_ID_int id2;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                id2 = new Grids_2D_ID_int(cri, cci);
                if (chunkID != id2) {
                    if (isInCache(chunkID)) {
                        writeToFileChunk(                                chunkID);
                        clearFromCacheChunk(chunkID);
                        result++;
                    }
                }
            }
        }
        return result;
    }

    protected long swapChunksExcept_Account(HashSet<Grids_2D_ID_int> chunkIDs) {
        long result = 0L;
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri, cci);
                if (!chunkIDs.contains(chunkID)) {
                    if (isInCache(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearFromCacheChunk(chunkID);
                        result++;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Attempts to write to file and clear from the cache a
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet.
     *
     * @param chunks
     * @return The Grids_2D_ID_int of the Grids_AbstractGridChunk swapped or
     * null.
     */
    protected final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunkExcept_AccountDetail(
                    HashSet<Grids_2D_ID_int> chunks) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        //HashSet<ChunkID> result_ChunkID_HashSet = new HashSet<ChunkID>(1);
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri, cci);
                if (!chunks.contains(chunkID)) {
                    if (isInCache(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearFromCacheChunk(chunkID);
                        //result_ChunkID_HashSet.add(a_ChunkID);
                        result.put(this, chunks);
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
     * @return The number of Grids_AbstractGridChunk swapped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunks_AccountDetail(
            boolean handleOutOfMemoryError) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunks_AccountDetail();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunks_AccountDetail();
                if (result.isEmpty()) {
                    result = ge.swapChunk_AccountDetail(false);
                    if (result.isEmpty()) {
                        throw e;
                    }
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> potentialPartResult;
                potentialPartResult = ge.initMemoryReserve_AccountDetail(handleOutOfMemoryError);
                ge.combine(result, potentialPartResult);
                potentialPartResult = swapChunks_AccountDetail(handleOutOfMemoryError);
                ge.combine(result, potentialPartResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap seriailsed version of all
     * _ChunkID_AbstractGrid2DSquareCellChunk_HashMap. This involves writing
     * them to files and then clearing them from the cache.
     *
     * @return The number of Grids_AbstractGridChunk swapped.
     */
    protected HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunks_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result = new HashMap<>(1);
        HashSet<Grids_2D_ID_int> chunkIDs = new HashSet<>();
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri, cci);
                if (isInCache(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearFromCacheChunk(chunkID);
                    chunkIDs.add(chunkID);
                }
            }
        }
        if (chunkIDs.isEmpty()) {
            return result;
        }
        result.put(this, chunkIDs);
        return result;
    }

    /**
     * Attempts to swap seriailsed version of all Grids_AbstractGridChunk from
     * (cri0, cci0) to (cri1, cci1) in row major order. This involves writing
     * them to files and then clearing them from the cache.
     *
     * @param cri0 The chunk row index of the first Grids_AbstractGridChunk to
     * be swapped.
     * @param cci0 The chunk column index of the first Grids_AbstractGridChunk
     * to be swapped.
     * @param cri1 The chunk row index of the last Grids_AbstractGridChunk to be
     * swapped.
     * @param cci1 The chunk column index of the last Grids_AbstractGridChunk to
     * be swapped.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGridChunk swapped.
     */
    public final long swapChunks_Account(
            int cri0, int cci0, int cri1, int cci1, boolean handleOutOfMemoryError) {
        try {
            long result = swapChunks_Account(
                    cri0, cci0, cri1, cci1);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                long result = swapChunk_Account();
                if (result < 1) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                result += swapChunks_Account(
                        cri0, cci0, cri1, cci1, handleOutOfMemoryError);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap seriailsed version of all Grids_AbstractGridChunk from
     * (cri0, cci0) to (cri1, cci1) in row major order. This involves writing
     * them to files and then clearing them from the cache.
     *
     * @param cri0 The chunk row index of the first Grid2DSquareCellChunks to be
     * swapped.
     * @param cci0 The chunk column index of the first Grid2DSquareCellChunks to
     * be swapped.
     * @param cri1 The chunk row index of the last Grid2DSquareCellChunks to be
     * swapped.
     * @param cci1 The chunk column index of the last Grid2DSquareCellChunks to
     * be swapped.
     * @return The number of Grids_AbstractGridChunk swapped.
     */
    protected final long swapChunks_Account(
            int cri0, int cci0, int cri1, int cci1) {
        Grids_2D_ID_int chunkID;
        long result = 0L;
        if (cri0 != cri1) {
            for (int cci = cci0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri0, cci);
                if (isInCache(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearFromCacheChunk(chunkID);
                    result++;
                }
            }
            for (int cri = cri0 + 1; cri < cri1; cri++) {
                for (int cci = 0; cci < NChunkCols; cci++) {
                    chunkID = new Grids_2D_ID_int(cri, cci);
                    if (isInCache(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearFromCacheChunk(chunkID);
                        result++;
                    }
                }
            }
            for (int cci = 0; cci < cci1; cci++) {
                chunkID = new Grids_2D_ID_int(cri1, cci);
                if (isInCache(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearFromCacheChunk(chunkID);
                    result++;
                }
            }
        } else {
            for (int cci = cci0; cci < cci1 + 1; cci++) {
                chunkID = new Grids_2D_ID_int(cri0, cci);
                if (isInCache(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearFromCacheChunk(chunkID);
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * @return true iff grid2DSquareCellChunk given by _ChunkID is swapToFiled.
     * This must be an upToDate swapToFile.
     * @param chunkID The Grids_2D_ID_int of the grid2DSquareCellChunk tested to
     * see if it is swapToFiled.
     */
    protected final boolean isInCache(Grids_2D_ID_int chunkID) {
        HashMap m = getChunkIDChunkMap(); //DEBUG
        return getChunkIDChunkMap().containsKey(chunkID);
    }

    /**
     * For releasing a grid2DSquareCellChunk stored in memory. This is usually
     * only done after the equivallent of swapToFileChunk(ID) has been called.
     *
     * @param chunkID The Grids_2D_ID_int of the grid2DSquareCellChunk to be
     * cleared.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected final void clearFromCacheGrid2DSquareCellChunk(
            Grids_2D_ID_int chunkID, boolean handleOutOfMemoryError) {
        try {
            clearFromCacheChunk(chunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                clearFromCacheGrid2DSquareCellChunk(chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For releasing a grid2DSquareCellChunk stored in memory. This is usually
     * only done after the equivalent of swapToFileChunk(ID) has been called.
     *
     * @param chunkID The Grids_2D_ID_int of the grid2DSquareCellChunk to be
     * cleared.
     */
    protected final void clearFromCacheChunk(Grids_2D_ID_int chunkID) {
        getChunkIDChunkMap().remove(chunkID);
        System.gc();
    }

    /**
     * For releasing all Grids_AbstractGridChunk in
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected final void clearFromCacheChunks(boolean handleOutOfMemoryError) {
        try {
            clearFromCacheChunks();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                clearFromCacheChunks(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For releasing all Grids_AbstractGridChunk in
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.
     */
    protected final void clearFromCacheChunks() {
        ChunkIDChunkMap = new HashMap<>();
        System.gc();
    }

    /**
     * Attempts to load into the memory cache a Grids_AbstractGridChunk with ID
     *
     * @param chunkID The Grids_2D_ID_int of the grid2DSquareCellChunk to be
     * restored.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final void loadIntoCacheChunk(Grids_2D_ID_int chunkID, boolean handleOutOfMemoryError) {
        try {
            loadIntoCacheChunk(chunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(chunkID, handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                loadIntoCacheChunk(chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to load into the memory cache Grids_AbstractGridChunk with
     * Grids_2D_ID_int equal to _ChunkID.
     *
     *
     * @param chunkID The Grids_2D_ID_int of the Grids_AbstractGridChunk to be
     * restored.
     */
    protected final void loadIntoCacheChunk(Grids_2D_ID_int chunkID) {
        boolean isInCache = isInCache(chunkID);
        if (!isInCache) {
            File f = new File(this.getDirectory(), "" + chunkID.getRow() + "_" + chunkID.getCol());

            System.out.println(f); // DEBUGGING CODE

            Object o = Generic_StaticIO.readObject(f);

            if (this instanceof Grids_GridInt) {
                if (this.getClass() == Grids_GridInt.class) {
                    Grids_AbstractGridChunkInt chunk = null;
                    if (o.getClass() == Grids_GridChunkIntArray.class) {
                        Grids_GridChunkIntArray c;
                        c = (Grids_GridChunkIntArray) o;
                        chunk = c;
                    }
                    if (o.getClass() == Grids_GridChunkIntMap.class) {
                        Grids_GridChunkIntMap c;
                        c = (Grids_GridChunkIntMap) o;
                        chunk = c;
                    }
                    if (chunk != null) {
                        chunk.initGrid(this);
                        chunk.initChunkID(chunkID);
                        ChunkIDChunkMap.put(chunkID, chunk);
                        this.ge.setDataToSwap(true);
                        return;
                    }
                    System.err.println("Unrecognised type of Grid2DSquareCellIntChunkAbstract or null " + this.getClass().getName() + ".loadIntoCacheChunk( ChunkID( " + chunkID.toString() + " ) )");
                } else {
                    Grids_AbstractGridChunkDouble chunk = null;
                    if (o == null) {
                        int debug = 1;
                        System.out.println("No chunk loading from file " + f);
                    }
                    if (o.getClass() == Grids_GridChunkDoubleArray.class) {
                        Grids_GridChunkDoubleArray c;
                        c = (Grids_GridChunkDoubleArray) o;
                        chunk = c;
                    }
                    if (o.getClass() == Grids_GridChunkDoubleMap.class) {
                        Grids_GridChunkDoubleMap c;
                        c = (Grids_GridChunkDoubleMap) o;
                        chunk = c;
                    }
                    if (chunk != null) {
                        chunk.initGrid(this);
                        chunk.initChunkID(chunkID);
                        ChunkIDChunkMap.put(chunkID, chunk);
                        this.ge.setDataToSwap(true);
                        return;
                    }
                    System.err.println("Unrecognised type of Grid2DSquareCellDoubleChunkAbstract or null " + this.getClass().getName() + ".loadIntoCacheChunk( ChunkID( " + chunkID.toString() + " ) )");
                }
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
    public final Grids_2D_ID_long[] getCellIDs(double x, double y, double distance, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long[] result = getCellIDs(x, y, distance);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellIDs(x, y, distance, handleOutOfMemoryError);
            } else {
                throw e;
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
    protected final Grids_2D_ID_long[] getCellIDs(double x, double y, double distance) {
        return getCellIDs(x, y, getCellRowIndex(y), getCellColIndex(x), distance);
    }

    /**
     * @return a Grids_2D_ID_long[] - the cell IDs for cells thats centroids
     * would be intersected by circle with centre at centroid of cell given by
     * cell row index _CellRowIndex, cell column index _CellColIndex, and radius
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
    public final Grids_2D_ID_long[] getCellIDs(long cellRowIndex, long cellColIndex, double distance, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long[] result = getCellIDs(cellRowIndex, cellColIndex, distance);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellIDs(cellRowIndex, cellColIndex, distance, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long[] - the cell IDs for cells thats centroids
     * would be intersected by circle with centre at centroid of cell given by
     * cell row index _CellRowIndex, cell column index _CellColIndex, and radius
     * distance.
     * @param cellRowIndex the row index for the cell thats centroid is the
     * circle centre from which cell values are returned.
     * @param cellColIndex the column index for the cell thats centroid is the
     * circle centre from which cell values are returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    protected final Grids_2D_ID_long[] getCellIDs(long cellRowIndex, long cellColIndex, double distance) {
        return getCellIDs(getCellXDouble(cellColIndex), getCellYDouble(cellRowIndex), cellRowIndex, cellColIndex, distance);
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
    public Grids_2D_ID_long[] getCellIDs(double x, double y, long cellRowIndex, long cellColIndex, double distance, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long[] result = getCellIDs(x, y, cellRowIndex, cellColIndex, distance);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellIDs(x, y, cellRowIndex, cellColIndex, distance, handleOutOfMemoryError);
            } else {
                throw e;
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
            double x, double y, long cellRowIndex, long cellColIndex, double distance) {
        Grids_2D_ID_long[] a_CellIDs0;
        int cellDistance = (int) Math.ceil(distance / getCellsizeDouble(false));
        int limit = ((2 * cellDistance) + 1) * ((2 * cellDistance) + 1);
        a_CellIDs0 = new Grids_2D_ID_long[limit];
        long row;
        long col;
        double thisX;
        double thisY;
        int count = 0;
        //if ( limit > 0 ) {
        for (row = cellRowIndex - cellDistance; row <= cellRowIndex + cellDistance; row++) {
            thisY = getCellYDouble(row);
            for (col = cellColIndex - cellDistance; col <= cellColIndex + cellDistance; col++) {
                thisX = getCellXDouble(col);
                if (Grids_Utilities.distance(x, y, thisX, thisY) <= distance) {
                    a_CellIDs0[count] = new Grids_2D_ID_long(row, col);
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
    public Grids_2D_ID_long getNearestCellID(double x, double y, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long result = getNearestCellID(x, y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getNearestCellID(x, y, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Nearest cells _CellRowIndex and _CellColIndex as a long[] from ID
     * to point given by x-coordinate x, y-coordinate y.
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     */
    protected Grids_2D_ID_long getNearestCellID(double x, double y) {
        return getNearestCellID(x, y, getCellRowIndex(y), getCellColIndex(x));
    }

    /**
     * @return Nearest cells _CellRowIndex and _CellColIndex as a long[] from ID
     * to point given by cell row index _CellRowIndex, cell column index
     * _CellColIndex.
     * @param cellRowIndex the row index from which nearest cell Grids_2D_ID_int
     * is returned.
     * @param cellColIndex the column index from which nearest cell
     * Grids_2D_ID_int is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown. TODO: return
     * Grids_2D_ID_long[] as could be more than one nearest CellID
     */
    public Grids_2D_ID_long getNearestCellID(long cellRowIndex, long cellColIndex, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long result = getNearestCellID(cellRowIndex, cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getNearestCellID(cellRowIndex, cellColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Nearest cells _CellRowIndex and _CellColIndex as a long[] from ID
     * to point given by cell row index _CellRowIndex, cell column index
     * _CellColIndex.
     * @param cellRowIndex the row index from which nearest cell Grids_2D_ID_int
     * is returned.
     * @param cellColIndex the column index from which nearest cell
     * Grids_2D_ID_int is returned. TODO: return Grids_2D_ID_long[] as could be
     * more than one nearest CellID
     */
    protected Grids_2D_ID_long getNearestCellID(long cellRowIndex, long cellColIndex) {
        return getNearestCellID(getCellXDouble(cellColIndex), getCellYDouble(cellRowIndex), cellRowIndex, cellColIndex);
    }

    /**
     * @return Nearest Grids_2D_ID_long to point given by x-coordinate x,
     * y-coordinate y in position given by _CellRowIndex, _CellColIndex.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param cellRowIndex The cell row index of cell containing point.
     * @param cellColIndex The cell column index of cell containing point.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_2D_ID_long getNearestCellID(double x, double y, long cellRowIndex, long cellColIndex, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long result = getNearestCellID(x, y, cellRowIndex, cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getNearestCellID(x, y, cellRowIndex, cellColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Nearest Grids_2D_ID_long to point given by x-coordinate x,
     * y-coordinate y in position given by _CellRowIndex, _CellColIndex.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param cellRowIndex The cell row index of cell containing point.
     * @param cellColIndex The cell column index of cell containing point.
     */
    protected Grids_2D_ID_long getNearestCellID(double x, double y, long cellRowIndex, long cellColIndex) {
        Grids_2D_ID_long a_CellID;
        boolean isInGrid = isInGrid(x, y);
        if (!isInGrid) {
            long row;
            long col;
            if (x >= Dimensions.getXMax().doubleValue()) {
                col = NCols - 1;
                if (y > Dimensions.getYMax().doubleValue()) {
                    row = 0;
                } else {
                    if (y < Dimensions.getYMin().doubleValue()) {
                        row = NRows - 1;
                    } else {
                        row = getCellRowIndex(y);
                    }
                }
            } else {
                if (x < Dimensions.getXMin().doubleValue()) {
                    col = 0;
                    if (y >= Dimensions.getYMax().doubleValue()) {
                        row = 0;
                    } else {
                        if (y < Dimensions.getYMin().doubleValue()) {
                            row = NRows - 1;
                        } else {
                            row = getCellRowIndex(y);
                        }
                    }
                } else {
                    col = getCellColIndex(x);
                    if (y >= Dimensions.getYMax().doubleValue()) {
                        row = 0;
                    } else {
                        row = NRows - 1;
                    }
                }
            }
            a_CellID = new Grids_2D_ID_long(row, col);
        } else {
            a_CellID = new Grids_2D_ID_long(cellRowIndex, cellColIndex);
        }
        return a_CellID;
    }

    /**
     * @return Height of the grid.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getHeightDouble(boolean handleOutOfMemoryError) {
        try {
            double result = getHeightDouble();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getHeightDouble(handleOutOfMemoryError);
            } else {
                throw e;
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
    public final BigDecimal getHeightBigDecimal(boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getHeightBigDecimal();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getHeightBigDecimal(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Height of the grid.
     */
    protected final BigDecimal getHeightBigDecimal() {
        return Dimensions.getYMax().subtract(Dimensions.getYMin());
    }

    /**
     * @return Width of the grid as a double.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getWidthDouble(boolean handleOutOfMemoryError) {
        try {
            double result = getWidthDouble();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getWidthDouble(handleOutOfMemoryError);
            } else {
                throw e;
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
    public final BigDecimal getWidthBigDecimal(boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getWidthBigDecimal();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getWidthBigDecimal(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Width of the grid as a BigDecimal.
     */
    protected final BigDecimal getWidthBigDecimal() {
        return Dimensions.getXMax().subtract(Dimensions.getXMin());
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
    public final boolean isInGrid(BigDecimal x, BigDecimal y, boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(x, y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return isInGrid(x, y, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return true iff point given by x-coordinate x, y-coordinate y is in the
     * Grid. Anything on the boundary is considered to be in.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    protected final boolean isInGrid(BigDecimal x, BigDecimal y) {
        return x.compareTo(Dimensions.getXMin()) != -1
                && y.compareTo(Dimensions.getYMin()) != -1
                && x.compareTo(Dimensions.getXMax()) != 1
                && y.compareTo(Dimensions.getYMax()) != 1;
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
    public final boolean isInGrid(double x, double y, boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(x, y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return isInGrid(x, y, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return true iff point given by x-coordinate x, y-coordinate y is in the
     * Grid. Anything on the boundary is considered to be in.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    protected final boolean isInGrid(double x, double y) {
        return isInGrid(BigDecimal.valueOf(x), BigDecimal.valueOf(y));
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
    public final boolean isInGrid(long cellRowIndex, long cellColIndex, boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(cellRowIndex, cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return isInGrid(cellRowIndex, cellColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return true iff position given by cell row index _CellRowIndex, cell
     * column index _CellColIndex is in the Grid.
     * @param cellRowIndex The cell row index to test.
     * @param cellColIndex The cell column index to test.
     */
    protected final boolean isInGrid(long cellRowIndex, long cellColIndex) {
        return cellRowIndex >= 0 && cellRowIndex < NRows && cellColIndex >= 0 && cellColIndex < NCols;
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return true iff position given by chunk row index _Row, chunk column
     * index _Col is in the Grid.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final boolean isInGrid(int chunkRowIndex, int chunkColIndex, boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(chunkRowIndex, chunkColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return isInGrid(chunkRowIndex, chunkColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return true iff position given by chunk row index _Row, chunk column
     * index _Col is in the Grid.
     */
    protected final boolean isInGrid(int chunkRowIndex, int chunkColIndex) {
        return chunkRowIndex >= 0 && chunkRowIndex < NRows && chunkColIndex >= 0 && chunkColIndex < NCols;
    }

    /**
     * @return true iff cell given by _CellID is in the Grid.
     * @param a_CellID The Grids_2D_ID_long of a cell to test.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final boolean isInGrid(Grids_2D_ID_long a_CellID, boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(a_CellID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return isInGrid(a_CellID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return true iff cell given by _CellID is in the Grid.
     * @param i The Grids_2D_ID_long of a cell to test.
     */
    protected final boolean isInGrid(Grids_2D_ID_long i) {
        return isInGrid(i.getRow(), i.getCol());
    }

    /**
     * @return true iff Grids_2D_ID_int _ChunkID is in the Grid.
     * @param a_ChunkID The Grids_2D_ID_int of a cell to test.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final boolean isInGrid(Grids_2D_ID_int a_ChunkID, boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(a_ChunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return isInGrid(a_ChunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return true iff Grids_2D_ID_int chunkID is in the Grid.
     * @param chunkID The Grids_2D_ID_int of a cell to test.
     */
    protected final boolean isInGrid(Grids_2D_ID_int chunkID) {
        int chunkRowIndex = chunkID.getRow();
        int chunkColIndex = chunkID.getCol();
        return chunkRowIndex > -1 && chunkRowIndex < NChunkRows && chunkColIndex > -1 && chunkColIndex < NChunkCols;
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return true iff cell given by _Row, _Col, chunkCellRowIndex,
     * chunkCellColIndex is in the Grid.
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final boolean isInGrid(int chunkRowIndex, int chunkColIndex, int chunkCellRowIndex, int chunkCellColIndex, boolean handleOutOfMemoryError) {
        try {
            boolean result = isInGrid(chunkRowIndex, chunkColIndex, chunkCellRowIndex, chunkCellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return isInGrid(chunkRowIndex, chunkColIndex, chunkCellRowIndex, chunkCellColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @return true iff cell given by _Row, _Col, chunkCellRowIndex,
     * chunkCellColIndex is in the Grid.
     */
    protected final boolean isInGrid(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        return isInGrid(
                ((long) chunkRowIndex * (long) ChunkNRows) + (long) chunkCellRowIndex,
                ((long) chunkColIndex * (long) ChunkNCols) + (long) chunkCellColIndex);
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
    public final BigDecimal getCellXBigDecimal(long cellColIndex, boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellXBigDecimal(cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunk_Account(false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return getCellXBigDecimal(cellColIndex, handleOutOfMemoryError);
            } else {
                throw e;
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
    public final BigDecimal getCellXBigDecimal(long cellColIndex, int chunkRowIndex, int chunkColIndex, boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellXBigDecimal(cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellXBigDecimal(cellColIndex, chunkRowIndex, chunkColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param cellColIndex
     * @return the x-coordinate of the centroid for cells with column index
     * _CellColIndex as a BigDecimal.
     */
    protected final BigDecimal getCellXBigDecimal(long cellColIndex) {
        BigDecimal offSetFromOrigin;
        offSetFromOrigin = Dimensions.getXMin().add(
                Dimensions.getCellsize().multiply(BigDecimal.valueOf(cellColIndex)));
        BigDecimal halfCellsize = getHalfCellsize();
        return offSetFromOrigin.add(halfCellsize);
    }

    public BigDecimal getHalfCellsize() {
        BigDecimal result;
        result = Dimensions.getCellsize().divide(
                new BigDecimal("2"),
                Dimensions.getDimensionsScale() + 1,
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
    public final double getCellXDouble(long cellColIndex, boolean handleOutOfMemoryError) {
        try {
            double result = getCellXDouble(cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellXDouble(cellColIndex, handleOutOfMemoryError);
            } else {
                throw e;
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
     * @param chunkID This is a Grids_2D_ID_int for those
     * AbstractGrid2DSquareCells not to be swapped if possible when an
     * OutOfMemoryError is encountered.
     */
    public final double getCellXDouble(long cellColIndex, boolean handleOutOfMemoryError, Grids_2D_ID_int chunkID) {
        try {
            double result = getCellXDouble(cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellXDouble(cellColIndex, handleOutOfMemoryError, chunkID);
            } else {
                throw e;
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
    public final double getCellXDouble(int chunkCellColIndex, int chunkRowIndex, int chunkColIndex, boolean handleOutOfMemoryError) {
        try {
            double result = getCellXDouble(chunkCellColIndex, chunkColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellXDouble(chunkCellColIndex, chunkRowIndex, chunkColIndex, handleOutOfMemoryError);
            } else {
                throw e;
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
    protected final double getCellXDouble(int chunkCellColIndex, int chunkColIndex) {
        return getCellXDouble(getCellColIndex(chunkCellColIndex, chunkColIndex));
    }

    /**
     * @return x-coordinate of the centroid for cells with column index
     * _CellColIndex as a double.
     * @param cellColIndex The cell column index thats centroid x-coordinate is
     * returned.
     */
    protected final double getCellXDouble(long cellColIndex) {
        return getCellXBigDecimal(cellColIndex).doubleValue();
    }

    /**
     * @return x-coordinate of the centroid for cell with cell Grids_2D_ID_int
     * _CellID as a BigDecimal.
     * @param a_CellID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final BigDecimal getCellXBigDecimal(Grids_2D_ID_long a_CellID, boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellXBigDecimal(a_CellID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellXBigDecimal(a_CellID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param cellID The Grids_2D_ID_long of the cell thats centroid is
     * returned.
     * @param chunkRowIndex The chunk row index of the Grids_AbstractGridChunk
     * not to be swapped if an OutOfMemoryError is thrown.
     * @param chunkColIndex The chunk column index of the
     * Grids_AbstractGridChunk not to be swapped if an OutOfMemoryError is
     * thrown.
     * @param handleOutOfMemoryError
     * @return x-coordinate of the centroid of cell with Grids_2D_ID_long
     * _CellID as a getCellXBigDecimal.
     */
    public final BigDecimal getCellXBigDecimal(Grids_2D_ID_long cellID, int chunkRowIndex, int chunkColIndex, boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellXBigDecimal(cellID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellXBigDecimal(cellID, chunkRowIndex, chunkColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return x-coordinate of the centroid for cell with cell Grids_2D_ID_int
     * _CellID as a BigDecimal.
     * @param chunkID
     */
    protected final BigDecimal getCellXBigDecimal(Grids_2D_ID_long chunkID) {
        return getCellXBigDecimal(chunkID.getCol());
    }

    /**
     * @return x-coordinate of the centroid for cell with cell Grids_2D_ID_int
     * _CellID as a double
     * @param a_CellID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellXDouble(Grids_2D_ID_long a_CellID, boolean handleOutOfMemoryError) {
        try {
            double result = getCellXDouble(a_CellID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellXDouble(a_CellID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return x-coordinate of the centroid of cell with Grids_2D_ID_long
     * _CellID as a double.
     * @param a_CellID The Grids_2D_ID_long of the cell thats centroid is
     * returned.
     */
    public final double getCellXDouble(Grids_2D_ID_long a_CellID, int chunkRowIndex, int chunkColIndex, boolean handleOutOfMemoryError) {
        try {
            double result = getCellXDouble(a_CellID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellXDouble(a_CellID, chunkRowIndex, chunkColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param _CellID
     * @return x-coordinate of the centroid for cell with cell Grids_2D_ID_int
     * _CellID as a double
     */
    protected final double getCellXDouble(Grids_2D_ID_long _CellID) {
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
    public final BigDecimal getCellYBigDecimal(long cellRowIndex, boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellYBigDecimal(cellRowIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellYBigDecimal(cellRowIndex, handleOutOfMemoryError);
            } else {
                throw e;
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
    public final BigDecimal getCellYBigDecimal(long cellRowIndex, int chunkRowIndex, int chunkColIndex, boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellYBigDecimal(cellRowIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellYBigDecimal(cellRowIndex, chunkRowIndex, chunkColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param cellRowIndex
     * @return y-coordinate of the centroid for cells with row index
     * _CellRowIndex as a BigDecimal.
     */
    protected final BigDecimal getCellYBigDecimal(long cellRowIndex) {
        BigDecimal offSetFromOrigin;
        offSetFromOrigin = Dimensions.getYMin().add(
                Dimensions.getCellsize().multiply(BigDecimal.valueOf(cellRowIndex)));
        BigDecimal halfCellsize = getHalfCellsize();
        return offSetFromOrigin.add(halfCellsize);
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
    public final double getCellYDouble(long cellRowIndex, boolean handleOutOfMemoryError) {
        try {
            double result = getCellYDouble(cellRowIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellYDouble(cellRowIndex, handleOutOfMemoryError);
            } else {
                throw e;
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
     * @param a_ChunkID This is a Grids_2D_ID_int for those
     * AbstractGrid2DSquareCells not to be swapped if possible when an
     * OutOfMemoryError is encountered.
     */
    public final double getCellYDouble(long cellRowIndex, boolean handleOutOfMemoryError, Grids_2D_ID_int a_ChunkID) {
        try {
            double result = getCellYDouble(cellRowIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellYDouble(cellRowIndex, handleOutOfMemoryError, a_ChunkID);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkColIndex
     * @param chunkRowIndex
     * @return y-coordinate of the centroid for cells with row index
     * _CellRowIndex as a double.
     * @param chunkCellRowIndex the chunk cell column index thats centroid
     * y-coordinate is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellYDouble(int chunkCellRowIndex, int chunkRowIndex, int chunkColIndex, boolean handleOutOfMemoryError) {
        try {
            double result = getCellYDouble(chunkCellRowIndex, chunkRowIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellYDouble(chunkCellRowIndex, chunkRowIndex, chunkColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @return y-coordinate of the centroid for cells with row index
     * _CellRowIndex as a double.
     * @param chunkCellRowIndex the chunk cell column index thats centroid
     * y-coordinate is returned.
     */
    protected final double getCellYDouble(int chunkCellRowIndex, int chunkRowIndex) {
        return getCellYDouble(getCellRowIndex(chunkCellRowIndex, chunkRowIndex));
    }

    /**
     * @param cellRowIndex
     * @return y-coordinate of the centroid for cells with row index
     * _CellRowIndex as a double.
     */
    protected final double getCellYDouble(long cellRowIndex) {
        return getCellYBigDecimal(cellRowIndex).doubleValue();
    }

    /**
     * @return y-coordinate of the centroid of cell with Grids_2D_ID_long
     * _CellID as a BigDecimal.
     * @param cellID The Grids_2D_ID_long of the cell thats centroid is
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final BigDecimal getCellYBigDecimal(Grids_2D_ID_long cellID, boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellYBigDecimal(cellID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellYBigDecimal(cellID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param cellID The Grids_2D_ID_long of the cell thats centroid is
     * returned.
     * @param chunkRowIndex
     * @param chunkColIndex
     * @param handleOutOfMemoryError
     * @return y-coordinate of the centroid of cell with Grids_2D_ID_long
     * _CellID as a BigDecimal.
     */
    public final BigDecimal getCellYBigDecimal(Grids_2D_ID_long cellID, int chunkRowIndex, int chunkColIndex, boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellYBigDecimal(cellID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellYBigDecimal(cellID, chunkRowIndex, chunkColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkID
     * @return y-coordinate of the centroid of cell with Grids_2D_ID_long
     * _CellID as a BigDecimal.
     */
    protected final BigDecimal getCellYBigDecimal(Grids_2D_ID_long chunkID) {
        return getCellYBigDecimal(chunkID.getRow());
    }

    /**
     * @return y-coordinate of the centroid of cell with Grids_2D_ID_long
     * _CellID as a double.
     * @param chunkID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellYDouble(Grids_2D_ID_long chunkID, boolean handleOutOfMemoryError) {
        try {
            double result = getCellYDouble(chunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellYDouble(chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return y-coordinate of the centroid of cell with Grids_2D_ID_long
     * _CellID as a double.
     * @param chunkID The Grids_2D_ID_long of the cell thats centroid is
     * returned.
     */
    public final double getCellYDouble(Grids_2D_ID_long chunkID, int chunkRowIndex, int chunkColIndex, boolean handleOutOfMemoryError) {
        try {
            double result = getCellYDouble(chunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellYDouble(chunkID, chunkRowIndex, chunkColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkID
     * @return the y-coordinate of the centroid of cell with chunkID
     * Grids_2D_ID_long as a double.
     */
    protected final double getCellYDouble(Grids_2D_ID_long chunkID) {
        return getCellYBigDecimal(chunkID).doubleValue();
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
    public final double[] getGridBounds(boolean handleOutOfMemoryError) {
        try {
            double[] result = getGridBounds();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getGridBounds(handleOutOfMemoryError);
            } else {
                throw e;
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
        result[0] = Dimensions.getXMin().doubleValue();
        result[1] = Dimensions.getYMin().doubleValue();
        result[2] = Dimensions.getXMax().doubleValue();
        result[3] = Dimensions.getYMax().doubleValue();
        return result;
    }

    /**
     * @param halfCellsize
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
            double halfCellsize,
            long cellRowIndex,
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double[] result = getCellBoundsDoubleArray(halfCellsize, cellRowIndex, cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellBoundsDoubleArray(
                        halfCellsize, cellRowIndex, cellColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param halfCellsize
     * @param cellColIndex
     * @return double[] where; double[0] xmin, left most x-coordinate of cell at
     * (rowIndex,colIndex) double[1] ymin, lowest y-coordinate of cell at
     * (rowIndex,colIndex) double[2] xmax, right most x-coordinate of cell at
     * (rowIndex,colIndex) double[3] ymax, highest y-coordinate of cell at
     * (rowIndex,colIndex)
     * @param cellRowIndex the row index of the cell for which the bounds are
     * returned
     */
    protected final double[] getCellBoundsDoubleArray(double halfCellsize, long cellRowIndex, long cellColIndex) {
        return getCellBoundsDoubleArray(halfCellsize, getCellXDouble(cellColIndex), getCellYDouble(cellRowIndex));
    }

    /**
     * Precision may compromise result. More precision is available via
     *
     * @param halfCellsize
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
    public final double[] getCellBoundsDoubleArray(double halfCellsize, double x, double y, boolean handleOutOfMemoryError) {
        try {
            double[] result = getCellBoundsDoubleArray(halfCellsize, x, y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellBoundsDoubleArray(halfCellsize, x, y, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Precision may compromise result. More precision is available via
     * BigDecimal arithmetic.
     *
     * @param halfCellsize
     * @return double[] where; double[0] xmin, left most x-coordinate of cell
     * that intersects point at (x,y) double[1] ymin, lowest y-coordinate of
     * cell that intersects point at (x,y) double[2] xmax, right most
     * x-coordinate of cell that intersects point at (x,y) double[3] ymax,
     * highest y-coordinate of cell that intersects point at (x,y)
     * @param x the x-coordinate in the cell for which the bounds are returned
     * @param y the y-coordinate in the cell for which the bounds are returned
     */
    protected final double[] getCellBoundsDoubleArray(double halfCellsize, double x, double y) {
        double[] cellBounds = new double[4];
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
     * @param halfCellsize = Dimensions.getCellsize().divide(new
     * BigDecimal("2.0"));
     * @param x the centroid x-coordinate of the cell for which the bounds are
     * returned.
     * @param y the centroid y-coordinate of the cell for which the bounds are
     * returned.
     */
    public final Grids_Dimensions getCellDimensions(
            BigDecimal halfCellsize, BigDecimal x, BigDecimal y, boolean handleOutOfMemoryError) {
        try {
            Grids_Dimensions result = getCellDimensions(halfCellsize, x, y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellDimensions(halfCellsize, x, y, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param halfCellsize
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
    protected final Grids_Dimensions getCellDimensions(
            BigDecimal halfCellsize, BigDecimal x, BigDecimal y) {
        Grids_Dimensions result;
        result = new Grids_Dimensions(
                getCellsize(false),
                x.subtract(halfCellsize),
                y.subtract(halfCellsize),
                x.add(halfCellsize),
                y.add(halfCellsize));
        return result;
    }

    /**
     * @param halfCellsize
     * @param cellRowIndex
     * @param handleOutOfMemoryError
     * @param cellColIndex
     * @return BigDecimal[] cellBounds_BigDecimalArray;
     * cellBounds_BigDecimalArray[0] xmin, left most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[1] ymin, lowest
     * y-coordinate of cell that intersects point at (x,y)
     * cellBounds_BigDecimalArray[2] xmax, right most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[3] ymax, highest
     * y-coordinate of cell that intersects point at (x,y)
     */
    public final Grids_Dimensions getCellDimensions(
            BigDecimal halfCellsize, long cellRowIndex, long cellColIndex, boolean handleOutOfMemoryError) {
        try {
            Grids_Dimensions result = getCellDimensions(halfCellsize, cellRowIndex, cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getCellDimensions(halfCellsize, cellRowIndex, cellColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param halfCellsize
     * @param cellRowIndex
     * @param cellColIndex
     * @return BigDecimal[] cellBounds_BigDecimalArray;
     * cellBounds_BigDecimalArray[0] xmin, left most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[1] ymin, lowest
     * y-coordinate of cell that intersects point at (x,y)
     * cellBounds_BigDecimalArray[2] xmax, right most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[3] ymax, highest
     * y-coordinate of cell that intersects point at (x,y)
     */
    protected final Grids_Dimensions getCellDimensions(
            BigDecimal halfCellsize, long cellRowIndex, long cellColIndex) {
        return getCellDimensions(
                halfCellsize,
                getCellXBigDecimal(cellColIndex),
                getCellYBigDecimal(cellRowIndex));
    }

    /**
     * @return the next Grids_2D_ID_int in row major order from _ChunkID, or
     * null.
     * @param chunkID
     * @param nChunkRows
     * @param nChunkCols
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_2D_ID_int getNextChunk(Grids_2D_ID_int chunkID, int nChunkRows, int nChunkCols, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_int result = getNextChunk(chunkID, nChunkRows, nChunkCols);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNextChunk(chunkID, nChunkRows, nChunkCols, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return the next Grids_2D_ID_int in row major order from _ChunkID, or
     * null.
     * @param chunkID
     * @param nChunkRows
     * @param nChunkCols
     */
    protected Grids_2D_ID_int getNextChunk(Grids_2D_ID_int chunkID, int nChunkRows, int nChunkCols) {
        int chunkRowIndex = chunkID.getRow();
        int chunkColIndex = chunkID.getCol();
        if (chunkColIndex < nChunkCols - 1) {
            return new Grids_2D_ID_int(chunkRowIndex, chunkColIndex + 1);
        } else {
            if (chunkRowIndex < nChunkRows - 1) {
                return new Grids_2D_ID_int(chunkRowIndex + 1, 0);
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
    public Grids_2D_ID_int getPreviousChunk(Grids_2D_ID_int chunkID, int nChunkRows, int nChunkCols, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_int result = getPreviousChunk(chunkID, nChunkRows, nChunkCols);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getPreviousChunk(chunkID, nChunkRows, nChunkCols, handleOutOfMemoryError);
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
    protected Grids_2D_ID_int getPreviousChunk(Grids_2D_ID_int chunkID, int nChunkRows, int nChunkCols) {
        int chunkRowIndex = chunkID.getRow();
        int chunkColIndex = chunkID.getCol();
        if (chunkColIndex > 0) {
            return new Grids_2D_ID_int(chunkRowIndex, chunkColIndex - 1);
        } else {
            if (chunkRowIndex > 0) {
                return new Grids_2D_ID_int(chunkRowIndex - 1, 0);
            }
        }
        return null;
    }

    /**
     *
     * @param chunksNotToSwapToFile
     * @param e
     */
    protected void freeSomeMemoryAndResetReserve(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> chunksNotToSwapToFile,
            OutOfMemoryError e) {
        Grids_AbstractGrid g;
        HashSet<Grids_2D_ID_int> chunkIDs;
        Iterator<Grids_AbstractGrid> ite;
        ite = chunksNotToSwapToFile.keySet().iterator();
        while (ite.hasNext()) {
            g = ite.next();
            chunkIDs = chunksNotToSwapToFile.get(g);
            if (ge.swapChunkExcept_Account(
                    g,
                    chunkIDs,
                    false) > 0) {
                ge.initMemoryReserve(chunksNotToSwapToFile, ge.HandleOutOfMemoryErrorTrue);
                return;
            }
        }
        throw e;
    }

    protected void freeSomeMemoryAndResetReserve(int chunkRowIndex, int chunkColIndex, OutOfMemoryError e) {
        //env.clearMemoryReserve();
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRowIndex, chunkColIndex);
        freeSomeMemoryAndResetReserve(chunkID, e);
    }

    protected void freeSomeMemoryAndResetReserve(HashSet<Grids_2D_ID_int> chunkIDs, OutOfMemoryError e) {
        if (ge.swapChunkExcept_Account(this, chunkIDs, false) < 1L) {
            throw e;
        }
        ge.initMemoryReserve(this, chunkIDs, ge.HandleOutOfMemoryErrorTrue);
    }

    protected void freeSomeMemoryAndResetReserve(Grids_2D_ID_int chunkID, OutOfMemoryError e) {
        if (ge.swapChunkExcept_Account(this, false) < 1L) {
            if (ge.swapChunkExcept_Account(this, chunkID, false) < 1L) {
                throw e;
            }
        }
        ge.initMemoryReserve(this, chunkID, ge.HandleOutOfMemoryErrorTrue);
    }

    protected void freeSomeMemoryAndResetReserve(boolean handleOutOfMemoryError, OutOfMemoryError e) {
        if (ge.swapChunk_Account(handleOutOfMemoryError) < 1L) {
            throw e;
        }
        ge.initMemoryReserve(handleOutOfMemoryError);
    }

    protected void freeSomeMemoryAndResetReserve(OutOfMemoryError e) {
        if (ge.swapChunkExcept_Account(this, false) < 1L) {
            throw e;
        }
        ge.initMemoryReserve(this, ge.HandleOutOfMemoryErrorTrue);
    }

    /**
     * @return the ChunkIDChunkMap
     */
    public HashMap<Grids_2D_ID_int, Grids_AbstractGridChunk> getChunkIDChunkMap() {
        return ChunkIDChunkMap;
    }

    /**
     * @return the UnsignedLongPowersOf2
     */
    public Grids_UnsignedLongPowersOf2 getUnsignedLongPowersOf2() {
        if (UnsignedLongPowersOf2 == null) {
            UnsignedLongPowersOf2 = new Grids_UnsignedLongPowersOf2(false);
        }
        return UnsignedLongPowersOf2;
    }

    protected void initDimensions(Object[] header, long startRowIndex, long startColIndex) {
        BigDecimal xMin;
        BigDecimal yMin;
        BigDecimal xMax;
        BigDecimal yMax;
        BigDecimal cellsize;
        cellsize = (BigDecimal) header[4];
        xMin = ((BigDecimal) header[2]).add(cellsize.multiply(new BigDecimal(startColIndex)));
        yMin = ((BigDecimal) header[3]).add(cellsize.multiply(new BigDecimal(startRowIndex)));
        xMax = xMin.add(new BigDecimal(Long.toString(NCols)).multiply(cellsize));
        yMax = yMin.add(new BigDecimal(Long.toString(NRows)).multiply(cellsize));
        Dimensions = new Grids_Dimensions(xMin, xMax, yMin, yMax, cellsize);
    }

    /**
     * Assumes NRows and NCols are already initialised.
     *
     * @param g
     * @param startRowIndex
     * @param startColIndex
     * @param handleOutOfMemoryError
     */
    protected void initDimensions(
            Grids_AbstractGridNumber g,
            long startRowIndex, long startColIndex,
            boolean handleOutOfMemoryError) {
        Dimensions = g.getDimensions(handleOutOfMemoryError); // temporary assignment
        BigDecimal startColIndexBigDecimal = new BigDecimal((long) startColIndex);
        BigDecimal startRowIndexBigDecimal = new BigDecimal((long) startRowIndex);
        BigDecimal nRowsBigDecimal = new BigDecimal(NRows);
        BigDecimal nColsBigDecimal = new BigDecimal(NCols);
        BigDecimal xMin;
        BigDecimal yMin;
        BigDecimal xMax;
        BigDecimal yMax;
        BigDecimal cellsize;
        if (Dimensions == null) {
            cellsize = BigDecimal.ONE;
            xMin = startColIndexBigDecimal;
            yMin = startRowIndexBigDecimal;
            xMax = xMin.add(nColsBigDecimal);
            yMax = yMin.add(nRowsBigDecimal);
        } else {
            cellsize = Dimensions.getCellsize();
            xMin = Dimensions.getXMin().add(startColIndexBigDecimal.multiply(cellsize));
            yMin = Dimensions.getYMin().add(startRowIndexBigDecimal.multiply(cellsize));
            xMax = Dimensions.getXMin().add(nColsBigDecimal.multiply(cellsize));
            yMax = Dimensions.getYMin().add(nRowsBigDecimal.multiply(cellsize));
        }
        Dimensions = new Grids_Dimensions(xMin, xMax, yMin, yMax, cellsize);
    }

    /**
     * @return Grids_AbstractGridChunk for the given chunkID.
     * @param chunkID
     */
    protected abstract Grids_AbstractGridChunk getGridChunk(Grids_2D_ID_int chunkID);

    /**
     * @return Grids_AbstractGridChunk for the given chunkID.
     * @param chunkID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_AbstractGridChunk getGridChunk(Grids_2D_ID_int chunkID, boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractGridChunk result = getGridChunk(chunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getGridChunk(chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return Grids_AbstractGridChunk.
     */
    protected final Grids_AbstractGridChunk getGridChunk(int chunkRowIndex, int chunkColIndex) {
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                chunkRowIndex,
                chunkColIndex);
        return getGridChunk(chunkID);
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return Grids_AbstractGridChunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_AbstractGridChunk getGridChunk(int chunkRowIndex, int chunkColIndex, boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractGridChunk result = getGridChunk(chunkRowIndex, chunkColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRowIndex, chunkColIndex);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getGridChunk(chunkID, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
}
