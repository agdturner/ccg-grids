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
package uk.ac.leeds.ccg.andyt.grids.core.grid;

import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_AbstractGridStatistics;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_GridStatistics1;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_GridStatistics0;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkIntFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkInt64CellMap;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntMap;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

/**
 * A class to represent and manipulate int precision Grids_AbstractGridNumber
 * instances.
 */
public class Grids_GridInt
        extends Grids_AbstractGridNumber
        implements Serializable {

    //private static final long serialVersionUID = 1L;
    protected int NoDataValue = Integer.MIN_VALUE;

    /**
     * Creates a new Grid2DSquareCellInt. Warning!! Concurrent modification may
     * occur if _Directory is in use. If a completely new instance is wanted
     * then use: Grid2DSquareCellInt( File, Grid2DSquareCellIntAbstract,
     * Grids_AbstractGridChunkIntFactory, int, int, long, long, long, long,
     * double, HashSet) which can be accessed via a Grids_GridIntFactory.
     *
     * @param directory The File _Directory to be used for swapping.
     * @param gridFile The File _Directory containing the File names thisFile
     * that the ois was constructed from.
     * @param ois The ObjectInputStream used in first attempt to construct this.
     * @param ge
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected Grids_GridInt(
            File directory,
            File gridFile,
            ObjectInputStream ois,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        super(ge);
        Grids_GridInt.this.init(
                directory,
                gridFile,
                ois,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellInt with each cell value equal to
     * Integer.MinValue. N.B. This method can be used to test which type of
     * chunks are more efficient in different situations. If this becomes clear
     * then it may be possible to automatically optimise chunk storage.
     * Although, optimisiation is going to be difficult owing to the trade off
     * between fast access and low memory.
     *
     * @param gridStatistics The AbstractGridStatistics to accompany this.
     * @param directory The File _Directory to be used for swapping.
     * @param cf The Grids_AbstractGridChunkIntFactory prefered for creating
     * chunks.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param nRows The number of rows of cells.
     * @param nCols The number of columns of cells.
     * @param dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param ge
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected Grids_GridInt(
            Grids_AbstractGridStatistics gridStatistics,
            File directory,
            Grids_AbstractGridChunkIntFactory cf,
            int chunkNRows,
            int chunkNCols,
            long nRows,
            long nCols,
            Grids_Dimensions dimensions,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        super(ge);
        Grids_GridInt.this.init(
                gridStatistics,
                directory,
                cf,
                chunkNRows,
                chunkNCols,
                nRows,
                nCols,
                dimensions,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellInt based on values in Grid2DSquareCell.
     *
     * @param GridStatistics The AbstractGridStatistics to accompany this.
     * @param directory The File _Directory to be used for swapping.
     * @param g The Grids_AbstractGridNumber from which this will be
     * constructed.
     * @param cf The Grids_AbstractGridChunkIntFactory prefered to construct
     * chunks of this.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param startRowIndex The Grid2DSquareCell row index which is the bottom
     * most row of this.
     * @param startColIndex The Grid2DSquareCell column index which is the left
     * most column of this.
     * @param endRowIndex The Grid2DSquareCell row index which is the top most
     * row of this.
     * @param endColIndex The Grid2DSquareCell column index which is the right
     * most column of this.
     * @param ge
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected Grids_GridInt(
            Grids_AbstractGridStatistics GridStatistics,
            File directory,
            Grids_AbstractGridNumber g,
            Grids_AbstractGridChunkIntFactory cf,
            int chunkNRows,
            int chunkNCols,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        super(ge);
        Grids_GridInt.this.init(
                GridStatistics,
                directory,
                g,
                cf,
                chunkNRows,
                chunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellInt with values obtained from gridFile.
     * Currently gridFile must be a _Directory of a Grid2DSquareCellDouble or
     * Grid2DSquareCellInt or a ESRI Asciigrid format Files with a _Name ending
     * ".asc".
     *
     * @param GridStatistics The AbstractGridStatistics to accompany the
     * returned grid.
     * @param directory The File _Directory to be used for swapping.
     * @param gridFile Either a _Directory, or a formatted File with a specific
     * extension containing the data and information about the
     * Grid2DSquareCellDouble to be returned.
     * @param cf The Grids_AbstractGridChunkIntFactory prefered to construct
     * chunks of this.
     * @param chunkNRows
     * @param startRowIndex The Grid2DSquareCell row index which is the bottom
     * most row of this.
     * @param chunkNCols
     * @param startColIndex The Grid2DSquareCell column index which is the left
     * most column of this.
     * @param endRowIndex The Grid2DSquareCell row index which is the top most
     * row of this.
     * @param endColIndex The Grid2DSquareCell column index which is the right
     * most column of this.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @param ge
     */
    protected Grids_GridInt(
            Grids_AbstractGridStatistics GridStatistics,
            File directory,
            File gridFile,
            Grids_AbstractGridChunkIntFactory cf,
            int chunkNRows,
            int chunkNCols,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        super(ge);
        init(
                GridStatistics,
                directory,
                gridFile,
                cf,
                chunkNRows,
                chunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                handleOutOfMemoryError);
    }

    /**
     * @return a string description of the instance. Basically the values of
     * each field.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public String toString(
            boolean handleOutOfMemoryError) {
        try {
            String result = "Grid2DSquareCellInt( "
                    + "_NoDataValue( " + getNoDataValue(handleOutOfMemoryError) + " ), "
                    + super.toString(0, ge.HandleOutOfMemoryErrorFalse) + " )";
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunk_Account(false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return toString(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises this.
     *
     * @param g The Grids_GridInt from which the fields of this are set.
     * @param initTransientFields Iff true then transient fields of this are set
     * with those of _Grid2DSquareCellInt.
     */
    protected void init(
            Grids_GridInt g,
            boolean initTransientFields) {
        try {
            init(g);
            if (initTransientFields) {
                this.setChunkIDChunkMap(
                        g.getChunkIDChunkMap());
                //this._AbstractGrid2DSquareCell_HashSet = _Grid2DSquareCellInt._AbstractGrid2DSquareCell_HashSet;
                // Set the reference to this in the Grid Statistics
                this.getGridStatistics().init(this);
                //this._GridStatistics.Grid2DSquareCell = this;
            }
        } catch (OutOfMemoryError e) {
            ge.clearMemoryReserve();
            if (ge.swapChunk_Account(false) < 1L) {
                throw e;
            }
            ge.initMemoryReserve(
                    ge.HandleOutOfMemoryErrorTrue);
            Grids_GridInt.this.init(g, initTransientFields);
        }
    }

    /**
     * Initialises this.
     *
     * @param directory The File _Directory to be used for swapping.
     * @param gridFile The File _Directory containing the File named thisFile
     * that the ois was constructed from.
     * @param ois The ObjectInputStream used in first attempt to construct this.
     * @param _AbstractGrid2DSquareCell_HashSet A HashSet of swappable
     * Grids_AbstractGridNumber instances.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @see Grid2DSquareCellInt( File, File, ObjectInputStream, HashSet, boolean
     * )
     */
    private void init(
            File directory,
            File gridFile,
            ObjectInputStream ois,
            boolean handleOutOfMemoryError) {
        try {
            this.setDirectory(directory);
            File thisFile = new File(
                    gridFile,
                    "thisFile");
            try {
                Grids_GridInt.this.init((Grids_GridInt) ois.readObject(),
                        false);
                //true );
                ois.close();
                // Set the refernce to this in the Grid Chunks
                initChunks(gridFile);
                for (Grids_AbstractGridChunk chunk : this.getChunkIDChunkMap().values()) {
                    chunk.setGrid(this);
                }
                // Set the reference to this in the Grid Statistics
                this.getGridStatistics().init(this);
                //this._GridStatistics.Grid2DSquareCell = this;
            } catch (ClassCastException cce) {
                try {
                    ois.close();
                    ois = Generic_StaticIO.getObjectInputStream(thisFile);
                    // If the object is a Grid2DSquareCellDouble
                    Grids_GridDoubleFactory gdf;
                    gdf = new Grids_GridDoubleFactory(
                            directory,
                            ge,
                            handleOutOfMemoryError);
                    Grids_GridDouble g;
                    g = (Grids_GridDouble) gdf.create(
                            directory,
                            gridFile,
                            ois);
                    Grids_GridIntFactory gif;
                    gif = new Grids_GridIntFactory(
                            directory,
                            ge,
                            handleOutOfMemoryError);
                    Grids_GridInt g2;
                    g2 = (Grids_GridInt) gif.create(g);
                    Grids_GridInt.this.init(
                            g2,
                            false);
                    initChunks(gridFile);
                    // Set the reference to this in the Grid Statistics
                    this.getGridStatistics().init(this);
                    //this._GridStatistics.Grid2DSquareCell = this;
                } catch (IOException ioe) {
                    System.err.println(ioe.getMessage());
                    //throw ioe;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println(e.getMessage());
                //throw e;
            }
            //initGrid2DSquareCellChunks( gridFile );
            ge.addGrid(this);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunks_Account(handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                Grids_GridInt.this.init(
                        directory,
                        gridFile,
                        ois,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises this.
     *
     * @param gridsStatistics The AbstractGridStatistics to accompany this.
     * @param directory The File _Directory to be used for swapping.
     * @param cf The Grids_AbstractGridChunkIntFactory prefered for creating
     * chunks.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param nRows The number of rows of cells.
     * @param nCols The number of columns of cells.
     * @param dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param _AbstractGrid2DSquareCell_HashSet A HashSet of swappable
     * Grids_AbstractGridNumber instances.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @see Grid2DSquareCellInt( AbstractGridStatistics, File,
     * AbstractGrid2DSquareCellIntChunkFactory, int, int, long, long ,
     * BigDecimal[], HashSet, boolean );
     */
    private void init(
            Grids_AbstractGridStatistics gridsStatistics,
            File directory,
            Grids_AbstractGridChunkIntFactory cf,
            int chunkNRows,
            int chunkNCols,
            long nRows,
            long nCols,
            Grids_Dimensions dimensions,
            boolean handleOutOfMemoryError) {
        try {
            this.setDirectory(directory);
            setGridStatistics(gridsStatistics);
            // Set the reference to this in the Grid Statistics
            this.getGridStatistics().init(this);
            //this._GridStatistics.Grid2DSquareCell = this;
            this.setDirectory(directory);
            this.ChunkNRows = chunkNRows;
            this.ChunkNCols = chunkNCols;
            this.NRows = nRows;
            this.NCols = nCols;
            this.Dimensions = dimensions;
            //this._AbstractGrid2DSquareCell_HashSet = _AbstractGrid2DSquareCell_HashSet;
            ge.addGrid(this);
            this.Name = directory.getName();
            initNChunkRows();
            initNChunkCols();
            long nChunks = getNChunks();
            this.setChunkIDChunkMap(
                    new HashMap<>((int) nChunks));
            int chunkRowIndex;
            int chunkColIndex;
            int loadedChunkCount = 0;
            boolean isLoadedChunk = false;
            int cri0 = 0;
            int cci0 = 0;
            int cci1 = 0;
            String println0 = ge.initString(1000, handleOutOfMemoryError);
            String println = ge.initString(1000, handleOutOfMemoryError);
            Grids_2D_ID_int chunkID = new Grids_2D_ID_int();
            Grids_AbstractGridChunkInt _Grid2DSquareCellIntChunk = cf.createGridChunkInt(
                    this,
                    chunkID);
            for (chunkRowIndex = 0; chunkRowIndex < NChunkRows; chunkRowIndex++) {
                for (chunkColIndex = 0; chunkColIndex < NChunkCols; chunkColIndex++) {
                    do {
                        try {
                            // Try to load chunk.
                            chunkID = new Grids_2D_ID_int(
                                    chunkRowIndex,
                                    chunkColIndex);
                            _Grid2DSquareCellIntChunk = cf.createGridChunkInt(
                                    this,
                                    chunkID);
                            this.getChunkIDChunkMap().put(
                                    chunkID,
                                    _Grid2DSquareCellIntChunk);
                            isLoadedChunk = true;
                        } catch (OutOfMemoryError e) {
                            if (handleOutOfMemoryError) {
                                ge.clearMemoryReserve();
//                                ge.handleOutOfMemoryErrorOrNot(
//                                        e,
//                                        this,_NChunkCols,
//                                        _ChunkRowIndex, 
//                                        _ChunkColIndex);
                                chunkID = new Grids_2D_ID_int(
                                        chunkRowIndex,
                                        chunkColIndex);
                                if (ge.swapChunksExcept_Account(
                                        this,
                                        chunkID,
                                        false) < 1L) {
                                    throw e;
                                }
                                ge.initMemoryReserve(handleOutOfMemoryError);
                            } else {
                                throw e;
                            }
                        }
                    } while (!isLoadedChunk);
                    isLoadedChunk = false;
                    loadedChunkCount++;
                    cci1 = chunkColIndex;
                }
                println = "Done chunkRow " + chunkRowIndex;
                println = ge.println(println, println0, handleOutOfMemoryError);
            }
            ge.addGrid(this);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunks_Account(false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                Grids_GridInt.this.init(
                        gridsStatistics,
                        directory,
                        cf,
                        chunkNRows,
                        chunkNCols,
                        nRows,
                        nCols,
                        dimensions,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialise this.
     *
     * @param gridStatistics The AbstractGridStatistics to accompany this.
     * @param directory The File _Directory to be used for swapping.
     * @param g The Grids_AbstractGridNumber from which this will be
     * constructed.
     * @param cf The Grids_AbstractGridChunkIntFactory prefered to construct
     * chunks of this.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param startRowIndex The Grid2DSquareCell row index which is the bottom
     * most row of this.
     * @param startColIndex The Grid2DSquareCell column index which is the left
     * most column of this.
     * @param endRowIndex The Grid2DSquareCell row index which is the top most
     * row of this.
     * @param endColIndex The Grid2DSquareCell column index which is the right
     * most column of this.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @see Grids_GridInt#initGrid2DSquareCellInt(AbstractGridStatistics, File,
     * AbstractGrid2DSquareCell, AbstractGrid2DSquareCellIntChunkFactory, int,
     * int, long, long, long, long, HashSet, boolean)
     */
    protected final void init(
            Grids_AbstractGridStatistics gridStatistics,
            File directory,
            Grids_AbstractGridNumber g,
            Grids_AbstractGridChunkIntFactory cf,
            int chunkNRows,
            int chunkNCols,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError) {
        try {
            this.setDirectory(directory);
            this.ChunkNRows = chunkNRows;
            this.ChunkNCols = chunkNCols;
            this.NRows = endRowIndex - startRowIndex + 1L;
            this.NCols = endColIndex - startColIndex + 1L;
            this.Name = directory.getName();
            //this._AbstractGrid2DSquareCell_HashSet = _AbstractGrid2DSquareCell_HashSet;
            Dimensions = g.getDimensions(); //Temporary assignment
            initNChunkRows();
            initNChunkCols();
            long nChunks = getNChunks();
            this.setChunkIDChunkMap((HashMap<Grids_2D_ID_int, Grids_AbstractGridChunk>) new HashMap((int) nChunks));
            initDimensions(g, startRowIndex, startColIndex, handleOutOfMemoryError);
            setGridStatistics(gridStatistics);
            // Set the reference to this in the Grid Statistics
            this.getGridStatistics().init(this);
            //this._GridStatistics.Grid2DSquareCell = this;
            int chunkRowIndex = Integer.MIN_VALUE;
            int chunkColIndex = Integer.MIN_VALUE;
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
            String println0 = ge.initString(1000, handleOutOfMemoryError);
            String println = ge.initString(1000, handleOutOfMemoryError);
            Grids_2D_ID_int chunkID = new Grids_2D_ID_int();
            Grids_AbstractGridChunkInt _Grid2DSquareCellIntChunk = cf.createGridChunkInt(
                    this,
                    chunkID);
            if (gridStatistics.getClass() == Grids_GridStatistics0.class) {
                //if ( ( Grid2DSquareCell.getClass() == Grids_GridInt.class ) || ( ( ( int ) Grid2DSquareCell.getNoDataValueDouble( false ) ) == Integer.MIN_VALUE ) ) {
                if (g.getClass() == Grids_GridInt.class) {
                    for (chunkRowIndex = _intZero; chunkRowIndex < NChunkRows; chunkRowIndex++) {
                        for (chunkColIndex = _intZero; chunkColIndex < NChunkCols; chunkColIndex++) {
                            do {
                                try {
                                    // Try to load chunk.
                                    chunkID = new Grids_2D_ID_int(
                                            chunkRowIndex,
                                            chunkColIndex);
                                    _Grid2DSquareCellIntChunk = cf.createGridChunkInt(
                                            this,
                                            chunkID);
                                    chunkID = new Grids_2D_ID_int(
                                            chunkRowIndex,
                                            chunkColIndex);
                                    this.getChunkIDChunkMap().put(
                                            chunkID,
                                            _Grid2DSquareCellIntChunk);
                                    row = ((long) chunkRowIndex * (long) ChunkNRows);
                                    for (chunkRow = 0; chunkRow < ChunkNRows; chunkRow++) {
                                        col = ((long) chunkColIndex * (long) ChunkNCols);
                                        for (chunkCol = 0; chunkCol < ChunkNCols; chunkCol++) {
                                            cellInt = g.getCellInt(
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
                                } catch (OutOfMemoryError e) {
                                    if (handleOutOfMemoryError) {
                                        ge.clearMemoryReserve();
                                        chunkID = new Grids_2D_ID_int(
                                                chunkRowIndex,
                                                chunkColIndex);
                                        if (ge.swapChunksExcept_Account(
                                                this,
                                                chunkID,
                                                false) < 1L) {
                                            throw e;
                                        }
                                        ge.initMemoryReserve(handleOutOfMemoryError);
                                    } else {
                                        throw e;
                                    }
                                }
                            } while (!isLoadedChunk);
                            isLoadedChunk = false;
                            loadedChunkCount++;
                            cci1 = chunkColIndex;
                        }
                        println = null;
                        println = "Done chunkRow " + chunkRowIndex;
                        println = ge.println(println, println0, handleOutOfMemoryError);
                    }
                } else {
                    //( Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class )
                    Grids_GridDouble _Grid2DSquareCellDouble = (Grids_GridDouble) g;
                    double _Grid2DSquareCellNoDataValue = _Grid2DSquareCellDouble.NoDataValue; //getNoDataValue( handleOutOfMemoryError );
                    if (_Grid2DSquareCellNoDataValue == (int) _Grid2DSquareCellNoDataValue) {
                        this.NoDataValue = (int) _Grid2DSquareCellNoDataValue;
                    }
                    double _Grid2DSquareCellValue;
                    for (chunkRowIndex = _intZero; chunkRowIndex < NChunkRows; chunkRowIndex++) {
                        for (chunkColIndex = _intZero; chunkColIndex < NChunkCols; chunkColIndex++) {
                            do {
                                try {
                                    // Try to load chunk.
                                    chunkID = new Grids_2D_ID_int(
                                            chunkRowIndex,
                                            chunkColIndex);
                                    _Grid2DSquareCellIntChunk = cf.createGridChunkInt(
                                            this,
                                            chunkID);
                                    chunkID = new Grids_2D_ID_int(
                                            chunkRowIndex,
                                            chunkColIndex);
                                    this.getChunkIDChunkMap().put(
                                            chunkID,
                                            _Grid2DSquareCellIntChunk);
                                    row = ((long) chunkRowIndex * (long) ChunkNRows);
                                    for (chunkRow = 0; chunkRow < ChunkNRows; chunkRow++) {
                                        col = ((long) chunkColIndex * (long) ChunkNCols);
                                        for (chunkCol = 0; chunkCol < ChunkNCols; chunkCol++) {
                                            //initCellFast( row, col, Grid2DSquareCell.getCellInt( row, col ) );
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
                                                        NoDataValue);
                                            }
                                            col++;
                                        }
                                        row++;
                                    }
                                    isLoadedChunk = true;
                                } catch (OutOfMemoryError e) {
                                    if (handleOutOfMemoryError) {
                                        ge.clearMemoryReserve();
                                        chunkID = new Grids_2D_ID_int(
                                                chunkRowIndex,
                                                chunkColIndex);
                                        if (ge.swapChunksExcept_Account(
                                                this,
                                                chunkID,
                                                false) < 1L) {
                                            throw e;
                                        }
                                        ge.initMemoryReserve(handleOutOfMemoryError);
                                    } else {
                                        throw e;
                                    }
                                }
                            } while (!isLoadedChunk);
                            isLoadedChunk = false;
                            loadedChunkCount++;
                            cci1 = chunkColIndex;
                        }
                        println = null;
                        println = "Done chunkRow " + chunkRowIndex;
                        println = ge.println(println, println0, handleOutOfMemoryError);
                    }
                }
            } else {
                // _GridStatistics.getClass() == Grids_GridStatistics1.class
                // initCellFast(long,long,int) is to be used inplace of
                // initCell(long,long,int).
                if ((g.getClass() == Grids_GridInt.class)
                        || (((int) g.getNoDataValueBigDecimal(handleOutOfMemoryError).intValue()) == Integer.MIN_VALUE)) {
                    this.NoDataValue = ((Grids_GridInt) g).NoDataValue;
                    for (chunkRowIndex = _intZero; chunkRowIndex < NChunkRows; chunkRowIndex++) {
                        for (chunkColIndex = _intZero; chunkColIndex < NChunkCols; chunkColIndex++) {
                            do {
                                try {
                                    // Try to load chunk.
                                    chunkID = new Grids_2D_ID_int(
                                            chunkRowIndex,
                                            chunkColIndex);
                                    _Grid2DSquareCellIntChunk = cf.createGridChunkInt(
                                            this,
                                            chunkID);
                                    chunkID = new Grids_2D_ID_int(
                                            chunkRowIndex,
                                            chunkColIndex);
                                    this.getChunkIDChunkMap().put(
                                            chunkID,
                                            _Grid2DSquareCellIntChunk);
                                    row = ((long) chunkRowIndex * (long) ChunkNRows);
                                    for (chunkRow = _intZero; chunkRow < ChunkNRows; chunkRow++) {
                                        col = ((long) chunkColIndex * (long) ChunkNCols);
                                        for (chunkCol = _intZero; chunkCol < ChunkNCols; chunkCol++) {
                                            cellInt = g.getCellInt(
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
                                } catch (OutOfMemoryError e) {
                                    if (handleOutOfMemoryError) {
                                        ge.clearMemoryReserve();
                                        chunkID = new Grids_2D_ID_int(
                                                chunkRowIndex,
                                                chunkColIndex);
                                        if (ge.swapChunksExcept_Account(
                                                this,
                                                chunkID,
                                                false) < 1L) {
                                            throw e;
                                        }
                                        ge.initMemoryReserve(handleOutOfMemoryError);
                                    } else {
                                        throw e;
                                    }
                                }
                            } while (!isLoadedChunk);
                            isLoadedChunk = false;
                            loadedChunkCount++;
                            cci1 = chunkColIndex;
                        }
                        println = null;
                        println = "Done chunkRow " + chunkRowIndex;
                        println = ge.println(println, println0, handleOutOfMemoryError);
                    }
                } else {
                    //( Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class )
                    Grids_GridDouble _Grid2DSquareCellDouble = (Grids_GridDouble) g;
                    double _Grid2DSquareCellNoDataValue = _Grid2DSquareCellDouble.NoDataValue; //getNoDataValue( handleOutOfMemoryError );
                    if (_Grid2DSquareCellNoDataValue == (int) _Grid2DSquareCellNoDataValue) {
                        this.NoDataValue = (int) _Grid2DSquareCellNoDataValue;
                    }
                    double _Grid2DSquareCellValue;
                    for (chunkRowIndex = _intZero; chunkRowIndex < NChunkRows; chunkRowIndex++) {
                        for (chunkColIndex = _intZero; chunkColIndex < NChunkCols; chunkColIndex++) {
                            do {
                                try {
                                    // Try to load chunk.
                                    chunkID = new Grids_2D_ID_int(
                                            chunkRowIndex,
                                            chunkColIndex);
                                    _Grid2DSquareCellIntChunk = cf.createGridChunkInt(
                                            this,
                                            chunkID);
                                    chunkID = new Grids_2D_ID_int(
                                            chunkRowIndex,
                                            chunkColIndex);
                                    this.getChunkIDChunkMap().put(
                                            chunkID,
                                            _Grid2DSquareCellIntChunk);
                                    row = ((long) chunkRowIndex * (long) ChunkNRows);
                                    for (chunkRow = _intZero; chunkRow < ChunkNRows; chunkRow++) {
                                        col = ((long) chunkColIndex * (long) ChunkNCols);
                                        for (chunkCol = _intZero; chunkCol < ChunkNCols; chunkCol++) {
                                            //initCellFast( row, col, Grid2DSquareCell.getCellInt( row, col ) );
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
                                                        this.NoDataValue);
                                            }
                                            col++;
                                        }
                                        row++;
                                    }
                                    isLoadedChunk = true;
                                } catch (OutOfMemoryError e) {
                                    if (handleOutOfMemoryError) {
                                        ge.clearMemoryReserve();
                                        chunkID = new Grids_2D_ID_int(
                                                chunkRowIndex,
                                                chunkColIndex);
                                        if (ge.swapChunksExcept_Account(
                                                this,
                                                chunkID,
                                                false) < 1L) {
                                            throw e;
                                        }
                                        ge.initMemoryReserve(handleOutOfMemoryError);
                                    } else {
                                        throw e;
                                    }
                                }
                            } while (!isLoadedChunk);
                            isLoadedChunk = false;
                            loadedChunkCount++;
                            cci1 = chunkColIndex;
                        }
                        println = null;
                        println = "Done chunkRow " + chunkRowIndex;
                        println = ge.println(println, println0, handleOutOfMemoryError);
                    }
                }
            }
            ge.addGrid(this);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunk_Account(false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                Grids_GridInt.this.init(
                        gridStatistics,
                        directory,
                        g,
                        cf,
                        chunkNRows,
                        chunkNCols,
                        startRowIndex,
                        startColIndex,
                        endRowIndex,
                        endColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises this.
     *
     * @param gridStatistics The AbstractGridStatistics to accompany the
     * returned grid.
     * @param directory The File _Directory to be used for swapping.
     * @param gridFile Either a _Directory, or a formatted File with a specific
     * extension containing the data and information about the
     * Grid2DSquareCellDouble to be returned.
     * @param cf The Grids_AbstractGridChunkIntFactory prefered to construct
     * chunks of this.
     * @param chunkNRows
     * @param startRowIndex The Grid2DSquareCell row index which is the bottom
     * most row of this.
     * @param chunkNCols
     * @param startColIndex The Grid2DSquareCell column index which is the left
     * most column of this.
     * @param endRowIndex The Grid2DSquareCell row index which is the top most
     * row of this.
     * @param endColIndex The Grid2DSquareCell column index which is the right
     * most column of this.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @see Grids_GridInt#Grid2DSquareCellInt(AbstractGridStatistics, File,
     * File, AbstractGrid2DSquareCellIntChunkFactory, int, int, long, long,
     * long, long, HashSet, boolean)
     */
    protected final void init(
            Grids_AbstractGridStatistics gridStatistics,
            File directory,
            File gridFile,
            Grids_AbstractGridChunkIntFactory cf,
            int chunkNRows,
            int chunkNCols,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError) {
        try {
            this.setDirectory(directory);
            String println0 = ge.initString(1000, handleOutOfMemoryError);
            String println = ge.initString(1000, handleOutOfMemoryError);
            if (gridFile.isDirectory()) {
                /* Initialise from
                 * new File(
                 *     gridFile,
                 *     "thisFile" );
                 */
                if (true) {
                    Grids_GridIntFactory gf;
                    gf = new Grids_GridIntFactory(
                            directory,
                            chunkNRows,
                            chunkNCols,
                            cf,
                            ge,
                            handleOutOfMemoryError);
                    File thisFile = new File(
                            gridFile,
                            "thisFile");
                    ObjectInputStream ois = null;
                    ois = Generic_StaticIO.getObjectInputStream(thisFile); //ioe0.printStackTrace();
                    //throw ioe0;
                    Grids_GridInt g;
                    g = (Grids_GridInt) gf.create(
                            directory,
                            thisFile,
                            ois);
                    Grids_GridInt g2;
                    g2 = gf.create(
                            directory,
                            g,
                            startRowIndex,
                            startColIndex,
                            endRowIndex,
                            endColIndex,
                            handleOutOfMemoryError);
                    Grids_GridInt.this.init(
                            g2,
                            false);
                }
                initChunks(gridFile);
            } else {
                this.ChunkNRows = chunkNRows;
                this.ChunkNCols = chunkNCols;
                this.NRows = endRowIndex - startRowIndex + 1L;
                this.NCols = endColIndex - startColIndex + 1L;
                this.Name = directory.getName();
                initNChunkRows();
                initNChunkCols();
                long nChunks = getNChunks();
                this.setChunkIDChunkMap((HashMap<Grids_2D_ID_int, Grids_AbstractGridChunk>) new HashMap((int) nChunks));
                setGridStatistics(gridStatistics);
                // Set the reference to this in the Grid Statistics
                this.getGridStatistics().init(this);
                //this._GridStatistics.Grid2DSquareCell = this;
                String filename = gridFile.getName();
                int loadedChunkCount = 0;
                boolean isLoadedChunk = false;
                int value = Integer.MIN_VALUE;
                boolean _Grid2DSquareCellsSwapped = false;
                if (filename.endsWith("asc") || filename.endsWith("txt")) {
                    Grids_ESRIAsciiGridImporter eagi;
                    eagi = new Grids_ESRIAsciiGridImporter(
                            gridFile, ge);
                    Object[] header = eagi.readHeaderObject();
                    initDimensions(header, startRowIndex, startColIndex);
                    double gridFileNoDataValue = (Double) header[5];
                    int chunkRowIndex = Integer.MIN_VALUE;
                    int chunkColIndex = Integer.MIN_VALUE;
                    int cachedAndClearedChunkCount = 0;
                    int cri0 = 0;
                    int cci0 = 0;
                    int cri1 = 0;
                    int cci1 = 0;
                    Grids_2D_ID_int chunkID = new Grids_2D_ID_int();
                    boolean initialisedChunksToClear = true;
                    long row = Long.MIN_VALUE;
                    long col = Long.MIN_VALUE;
                    boolean isInitCellDone = false;
                    int reportN;
                    reportN = (int) (endRowIndex - startRowIndex) / 10;
                    //ChunkID nextChunkToSwap = new Grids_2D_ID_int(
                    //        this._NChunkCols,
                    //        0,
                    //        0 );
                    Grids_AbstractGridChunkInt _Grid2DSquareCellIntChunk = cf.createGridChunkInt(
                            this,
                            chunkID);
                    // Initialise Chunks
                    for (chunkRowIndex = 0; chunkRowIndex < NChunkRows; chunkRowIndex++) {
                        //for ( _ChunkRowIndex = _NChunkRows - 1; _ChunkRowIndex >= 0; _ChunkRowIndex -- ) {
                        for (chunkColIndex = 0; chunkColIndex < NChunkCols; chunkColIndex++) {
                            do {
                                try {
                                    chunkID = new Grids_2D_ID_int(
                                            chunkRowIndex,
                                            chunkColIndex);
                                    _Grid2DSquareCellIntChunk = cf.createGridChunkInt(
                                            this,
                                            chunkID);
                                    chunkID = new Grids_2D_ID_int(
                                            chunkRowIndex,
                                            chunkColIndex);
                                    this.getChunkIDChunkMap().put(
                                            chunkID,
                                            _Grid2DSquareCellIntChunk);
                                    isLoadedChunk = true;
                                } catch (OutOfMemoryError e) {
                                    if (handleOutOfMemoryError) {
                                        ge.clearMemoryReserve();
                                        chunkID = new Grids_2D_ID_int(
                                                chunkRowIndex,
                                                chunkColIndex);
                                        if (ge.swapChunksExcept_Account(
                                                this,
                                                chunkID,
                                                false) < 1L) {
                                            throw e;
                                        }
                                        ge.initMemoryReserve(handleOutOfMemoryError);
                                    } else {
                                        throw e;
                                    }
                                }
                            } while (!isLoadedChunk);
                            isLoadedChunk = false;
                            loadedChunkCount++;
                            cci1 = chunkColIndex;
                        }
                        println = null;
                        println = "Done chunkRow " + chunkRowIndex;
                        println = ge.println(println, println0, handleOutOfMemoryError);
                    }
                    println = null;
                    println = "First stage of initialisation complete. Reading data into initialised Chunks";
                    println = ge.println(println, println0, handleOutOfMemoryError);

                    // Read Data into Chunks
                    if ((int) gridFileNoDataValue == Integer.MIN_VALUE) {
                        if (gridStatistics.getClass() == Grids_GridStatistics0.class) {
                            for (row = (NRows - 1); row > -1; row--) {
                                for (col = 0; col < NCols; col++) {
                                    value = eagi.readInt();
                                    do {
                                        try {
                                            initCell(
                                                    row,
                                                    col,
                                                    value);
                                            isInitCellDone = true;
                                        } catch (OutOfMemoryError e) {
                                            if (handleOutOfMemoryError) {
                                                ge.clearMemoryReserve();
                                                chunkID = new Grids_2D_ID_int(
                                                        chunkRowIndex,
                                                        chunkColIndex);
                                                if (ge.swapChunksExcept_Account(
                                                        this,
                                                        chunkID,
                                                        false) < 1L) {
                                                    throw e;
                                                }
                                                ge.initMemoryReserve(handleOutOfMemoryError);
                                            } else {
                                                throw e;
                                            }
                                        }
                                    } while (!isInitCellDone);
                                    isInitCellDone = false;
                                }
                                if (row % reportN == 0) {
                                    println = null;
                                    println = "Done row " + row;
                                    println = ge.println(println, println0, handleOutOfMemoryError);
                                }
                            }
                        } else {
                            // _GridStatistics.getClass() == Grids_GridStatistics1.class
                            for (row = (NRows - 1); row > -1; row--) {
                                for (col = 0; col < NCols; col++) {
                                    value = eagi.readInt();
                                    do {
                                        try {
                                            initCellFast(
                                                    row,
                                                    col,
                                                    value);
                                            isInitCellDone = true;
                                        } catch (OutOfMemoryError e) {
                                            if (handleOutOfMemoryError) {
                                                ge.clearMemoryReserve();
                                                chunkID = new Grids_2D_ID_int(
                                                        chunkRowIndex,
                                                        chunkColIndex);
                                                if (ge.swapChunksExcept_Account(
                                                        this,
                                                        chunkID,
                                                        false) < 1L) {
                                                    throw e;
                                                }
                                                ge.initMemoryReserve(handleOutOfMemoryError);
                                            } else {
                                                throw e;
                                            }
                                        }
                                    } while (!isInitCellDone);
                                    isInitCellDone = false;
                                }
                                if (row % reportN == 0) {
                                    println = null;
                                    println = "Done row " + row;
                                    println = ge.println(println, println0, handleOutOfMemoryError);
                                }
                            }
                        }
                    } else {
                        int _NoDataValue = getNoDataValue(handleOutOfMemoryError);
                        if (gridStatistics.getClass() == Grids_GridStatistics0.class) {
                            for (row = (NRows - 1); row > -1; row--) {
                                for (col = 0; col < NCols; col++) {
                                    value = eagi.readInt();
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
                                        } catch (OutOfMemoryError e) {
                                            if (handleOutOfMemoryError) {
                                                ge.clearMemoryReserve();
                                                chunkID = new Grids_2D_ID_int(
                                                        chunkRowIndex,
                                                        chunkColIndex);
                                                if (ge.swapChunksExcept_Account(
                                                        this,
                                                        chunkID,
                                                        false) < 1L) {
                                                    throw e;
                                                }
                                                ge.initMemoryReserve(handleOutOfMemoryError);
                                            } else {
                                                throw e;
                                            }
                                        }
                                    } while (!isInitCellDone);
                                    isInitCellDone = false;
                                }
                                if (row % reportN == 0) {
                                    println = null;
                                    println = "Done row " + row;
                                    println = ge.println(println, println0, handleOutOfMemoryError);
                                }
                            }
                        } else {
                            // _GridStatistics.getClass() == Grids_GridStatistics1.class
                            for (row = (NRows - 1); row > -1; row--) {
                                for (col = 0; col < NCols; col++) {
                                    value = eagi.readInt();
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
                                        } catch (OutOfMemoryError e) {
                                            if (handleOutOfMemoryError) {
                                                ge.clearMemoryReserve();
                                                chunkID = new Grids_2D_ID_int(
                                                        chunkRowIndex,
                                                        chunkColIndex);
                                                if (ge.swapChunksExcept_Account(
                                                        this,
                                                        chunkID,
                                                        false) < 1L) {
                                                    throw e;
                                                }
                                                ge.initMemoryReserve(handleOutOfMemoryError);
                                            } else {
                                                throw e;
                                            }
                                        }
                                    } while (!isInitCellDone);
                                    isInitCellDone = false;
                                }
                                if (row % reportN == 0) {
                                    println = null;
                                    println = "Done row " + row;
                                    println = ge.println(println, println0, handleOutOfMemoryError);
                                }
                            }
                        }
                    }
                }
            }
            ge.addGrid(this);
            setGridStatistics(gridStatistics);
            // Set the reference to this in the Grid Statistics
            //this._GridStatistics.Grid2DSquareCell = this;
            this.getGridStatistics().init(this);
            //this._GridStatistics.Grid2DSquareCell = this;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(
                        this,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                init(
                        gridStatistics,
                        directory,
                        gridFile,
                        cf,
                        chunkNRows,
                        chunkNCols,
                        startRowIndex,
                        startColIndex,
                        endRowIndex,
                        endColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return _Grid2DSquareCellIntChunks.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_AbstractGridChunkInt getGridChunk(
            int chunkRowIndex,
            int chunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractGridChunkInt result = Grids_GridInt.this.getGridChunk(
                    chunkRowIndex,
                    chunkColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int a_ChunkID = new Grids_2D_ID_int(
                        chunkRowIndex,
                        chunkColIndex);
                if (ge.swapChunkExcept_Account(
                        this,
                        a_ChunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return Grids_GridInt.this.getGridChunk(
                        chunkRowIndex,
                        chunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return _Grid2DSquareCellIntChunks.
     */
    protected Grids_AbstractGridChunkInt getGridChunk(
            int chunkRowIndex,
            int chunkColIndex) {
        Grids_2D_ID_int a_ChunkID = new Grids_2D_ID_int(
                chunkRowIndex,
                chunkColIndex);
        return Grids_GridInt.this.getGridChunk(a_ChunkID);
    }

    /**
     * @return _Grid2DSquareCellIntChunksAbstract for the given ID
     * @param a_ChunkID
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_AbstractGridChunkInt getGridChunk(
            Grids_2D_ID_int a_ChunkID,
            boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractGridChunkInt result = Grids_GridInt.this.getGridChunk(a_ChunkID);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(
                        this,
                        a_ChunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getGridChunk(
                        a_ChunkID,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return _Grid2DSquareCellIntChunksAbstract for the given ID
     * @param a_ChunkID
     */
    protected Grids_AbstractGridChunkInt getGridChunk(
            Grids_2D_ID_int a_ChunkID) {
        boolean isInGrid = isInGrid(
                a_ChunkID);
        if (isInGrid) {
            if (!this.ChunkIDChunkMap.containsKey(a_ChunkID)) {
                loadIntoCacheChunk(
                        a_ChunkID);
            }
            return (Grids_AbstractGridChunkInt) this.getChunkIDChunkMap().get(a_ChunkID);
        }
        return null;
    }

    /**
     * If newValue and oldValue are the same then statistics won't change. A
     * test might be appropriate in set cell so that this method is not called.
     * Also want to keep track if underlying data has changed for getting
     * statistics of Grids_GridStatistics1 type.
     *
     * @param newValue The value replacing oldValue.
     * @param oldValue The value being replaced.
     * @param _NoDataValue this.NoDataValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
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
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunk_Account(false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                upDateGridStatistics(
                        newValue,
                        oldValue,
                        _NoDataValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * If newValue and oldValue are the same then statistics won't change. A
     * test might be appropriate in set cell so that this method is not called.
     * Also want to keep track if underlying data has changed for getting
     * statistics of Grids_GridStatistics1 type.
     *
     * @param newValue The value replacing oldValue.
     * @param oldValue The value being replaced.
     * @param _NoDataValue this.NoDataValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    private void upDateGridStatistics(
            int newValue,
            int oldValue,
            int _NoDataValue) {
        Grids_AbstractGridStatistics s;
        s = getGridStatistics();
        boolean handleOutOfMemoryError;
        handleOutOfMemoryError = ge.HandleOutOfMemoryError;
        if (s instanceof Grids_GridStatistics0) {
            if (oldValue != _NoDataValue) {
                BigDecimal oldValueBigDecimal = new BigDecimal(oldValue);
                getGridStatistics().setNonNoDataValueCount(
                        s.getNonNoDataValueCountBigInteger(handleOutOfMemoryError).subtract(BigInteger.ONE));
                s.setSum(s.getSumBigDecimal(handleOutOfMemoryError).subtract(oldValueBigDecimal));
                if (oldValueBigDecimal.compareTo(s.getMin(true, handleOutOfMemoryError)) == 0) {
                    s.setMinCount(s.getMinCount().subtract(BigInteger.ONE));
                }
                if (oldValueBigDecimal.compareTo(s.getMax(true, handleOutOfMemoryError)) == 0) {
                    s.setMaxCount(s.getMaxCount().subtract(BigInteger.ONE));
                }
            }
            if (newValue != _NoDataValue) {
                BigDecimal newValueBigDecimal = new BigDecimal(newValue);
                s.setNonNoDataValueCount(s.getNonNoDataValueCountBigInteger(handleOutOfMemoryError).add(BigInteger.ONE));
                s.setSum(s.getSumBigDecimal(handleOutOfMemoryError).add(newValueBigDecimal));
                if (newValueBigDecimal.compareTo(s.getMin(true, handleOutOfMemoryError)) == -1) {
                    s.setMin(newValueBigDecimal);
                    s.setMinCount(BigInteger.ONE);
                } else {
                    if (newValueBigDecimal.compareTo(s.getMin(true, handleOutOfMemoryError)) == 0) {
                        s.setMinCount(s.getMinCount().add(BigInteger.ONE));
                    } else {
                        if (s.getMinCount().compareTo(BigInteger.ONE) == -1) {
                            // The GridStatistics need recalculating
                            s.update();
                        }
                    }
                }
                if (newValueBigDecimal.compareTo(s.getMax(true, handleOutOfMemoryError)) == 1) {
                    s.setMax(newValueBigDecimal);
                    s.setMaxCount(BigInteger.ONE);
                } else {
                    if (newValueBigDecimal.compareTo(s.getMax(true, handleOutOfMemoryError)) == 0) {
                        s.setMaxCount(s.getMaxCount().add(BigInteger.ONE));
                    } else {
                        if (s.getMaxCount().compareTo(BigInteger.ONE) == -1) {
                            // The GridStatistics need recalculating
                            s.update();
                        }
                    }
                }
            }
        } else {
            if (newValue != oldValue) {
                ((Grids_GridStatistics1) s).setIsUpToDate(false);
            }
        }
    }

    /**
     * @return this.NoDataValue.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getNoDataValue(
            boolean handleOutOfMemoryError) {
        try {
            int result = getNoDataValue();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunk_Account(false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return getNoDataValue(
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return this NoDataValue Integer.MIN_VALUE.
     */
    protected final int getNoDataValue() {
        return Integer.MIN_VALUE;
    }

    /**
     * @return the this.NoDataValue converted to a BigDecimal.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public BigDecimal getNoDataValueBigDecimal(
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = BigDecimal.valueOf(getNoDataValue());
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunk_Account(false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return getNoDataValueBigDecimal(
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param cellRowIndex
     * @param cellColIndex
     * @return Value at _CellRowIndex, _CellColIndex else returns NoDataValue.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int getCell(
            long cellRowIndex,
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCell(
                    cellRowIndex,
                    cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellRowIndex),
                        getChunkColIndex(cellColIndex));
                if (ge.swapChunkExcept_Account(
                        this,
                        chunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        chunkID,
                        handleOutOfMemoryError);
                return getCell(
                        cellRowIndex,
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param cellRowIndex
     * @param cellColIndex
     * @return Value at _CellRowIndex, _CellColIndex else returns NoDataValue.
     */
    protected int getCell(
            long cellRowIndex,
            long cellColIndex) {
        int _NoDataValue = getNoDataValue();
        boolean isInGrid = isInGrid(
                cellRowIndex,
                cellColIndex);
        if (isInGrid) {
            long chunkNrows = ChunkNRows;
            long chunkNcols = ChunkNCols;
            int chunkRowIndex = getChunkRowIndex(cellRowIndex);
            int chunkColIndex = getChunkColIndex(cellColIndex);
            long chunkRowIndexLong = chunkRowIndex;
            long chunkColIndexLong = chunkColIndex;
            int chunkCellRowIndex = (int) (cellRowIndex - (chunkRowIndexLong * chunkNrows));
            int chunkCellColIndex = (int) (cellColIndex - (chunkColIndexLong * chunkNcols));
            Grids_AbstractGridChunk chunk = getChunk(
                    chunkRowIndex,
                    chunkColIndex);
            if (chunk.getClass() == Grids_GridChunkInt64CellMap.class) {
                return ((Grids_GridChunkInt64CellMap) chunk).getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        _NoDataValue,
                        false,
                        chunk.getChunkID(false));
            } else {
                if (chunk.getClass() == Grids_GridChunkIntArray.class) {
                    return ((Grids_GridChunkIntArray) chunk).getCell(
                            chunkCellRowIndex,
                            chunkCellColIndex,
                            _NoDataValue,
                            false,
                            chunk.getChunkID(false));
                } else {
                    return ((Grids_GridChunkIntMap) chunk).getCell(
                            chunkCellRowIndex,
                            chunkCellColIndex,
                            _NoDataValue);
                }
            }
        }
        return _NoDataValue;
    }

    /**
     * @param _Grid2DSquareCellChunk
     * @return Value at position given by chunk row index _ChunkRowIndex, chunk
     * column index _ChunkColIndex, chunk cell row index chunkCellRowIndex,
     * chunk cell column index chunkCellColIndex.
     * @param chunkRowIndex The chunk row index of the cell thats value is
     * returned.
     * @param chunkColIndex The chunk column index of the cell thats value is
     * returned.
     * @param chunkCellRowIndex The chunk cell row index of the cell thats value
     * is returned.
     * @param chunkCellColIndex The chunk cell column index of the cell thats
     * value is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int getCell(
            Grids_AbstractGridChunkInt _Grid2DSquareCellChunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCell(
                    _Grid2DSquareCellChunk,
                    chunkRowIndex,
                    chunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int a_ChunkID = new Grids_2D_ID_int(
                        chunkRowIndex,
                        chunkColIndex);
                if (ge.swapChunkExcept_Account(
                        this,
                        a_ChunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        a_ChunkID,
                        handleOutOfMemoryError);
                return getCell(
                        _Grid2DSquareCellChunk,
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
     * @param chunk
     * @return Value at position given by chunk row index _ChunkRowIndex, chunk
     * column index _ChunkColIndex, chunk cell row index chunkCellRowIndex,
     * chunk cell column index chunkCellColIndex.
     * @param _ChunkRowIndex The chunk row index of the cell thats value is
     * returned.
     * @param _ChunkColIndex The chunk column index of the cell thats value is
     * returned.
     * @param chunkCellRowIndex The chunk cell row index of the cell thats value
     * is returned.
     * @param chunkCellColIndex The chunk cell column index of the cell thats
     * value is returned.
     */
    protected int getCell(
            Grids_AbstractGridChunkInt chunk,
            int _ChunkRowIndex,
            int _ChunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        int _NoDataValue = getNoDataValue();
        if (chunk.getClass() == Grids_GridChunkInt64CellMap.class) {
            return ((Grids_GridChunkInt64CellMap) chunk).getCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    _NoDataValue,
                    false,
                    chunk.getChunkID(false));
        } else if (chunk.getClass() == Grids_GridChunkIntArray.class) {
            return ((Grids_GridChunkIntArray) chunk).getCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    _NoDataValue,
                    false,
                    chunk.getChunkID(false));
        } else if (chunk.getClass() == Grids_GridChunkIntMap.class) {
            return ((Grids_GridChunkIntMap) chunk).getCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    _NoDataValue,
                    false,
                    chunk.getChunkID(false));
        } else {
            return _NoDataValue;
        }
    }

    /**
     * For returning the value of the cell containing point given by
     * x-coordinate x, y-coordinate y as a int.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
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
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
                if (ge.swapChunkExcept_Account(
                        this,
                        chunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        chunkID,
                        handleOutOfMemoryError);
                return getCell(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the value of the cell containing point given by
     * x-coordinate x, y-coordinate y as a int.
     *
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
     * @return int value of the cell with Grids_2D_ID_long _CellID.
     *
     * @param cellID the Grids_2D_ID_long of the cell.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getCell(
            Grids_2D_ID_long cellID,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCell(
                    cellID.getRow(),
                    cellID.getCol());
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellID.getRow()),
                        getChunkColIndex(cellID.getCol()));
                if (ge.swapChunkExcept_Account(
                        this,
                        chunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        chunkID,
                        handleOutOfMemoryError);
                return getCell(
                        cellID,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the value at x-coordinate x, y-coordinate y and setting it
     * to newValue.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @param newValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
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
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
                if (ge.swapChunkExcept_Account(
                        this,
                        chunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        chunkID,
                        handleOutOfMemoryError);
                return setCell(
                        x,
                        y,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the value at x-coordinate x, y-coordinate y and setting it
     * to newValue.
     *
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
     * For returning the value of the cell with cell Grids_2D_ID_int cellID and
     * setting it to newValue.
     *
     * @param cellID the Grids_2D_ID_long of the cell.
     * @param newValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final int setCell(
            Grids_2D_ID_long cellID,
            int newValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = setCell(
                    cellID.getRow(),
                    cellID.getCol(),
                    newValue);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellID.getRow()),
                        getChunkColIndex(cellID.getCol()));
                if (ge.swapChunkExcept_Account(
                        this,
                        chunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        chunkID,
                        handleOutOfMemoryError);
                return setCell(
                        cellID,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the value at _CellRowIndex, _CellColIndex as a double and
     * setting it to ( int ) newValue.
     *
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
     *
     * @param _CellRowIndex
     * @param _CellColIndex
     * @param newValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
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
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (ge.swapChunkExcept_Account(
                        this,
                        chunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        chunkID,
                        handleOutOfMemoryError);
                return setCell(
                        _CellRowIndex,
                        _CellColIndex,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the value at _CellRowIndex, _CellColIndex and setting it to
     * newValue.
     *
     * @param cellRowIndex
     * @param cellColIndex
     * @param newValue
     * @return
     */
    protected int setCell(
            long cellRowIndex,
            long cellColIndex,
            int newValue) {
        int chunkRowIndex = getChunkRowIndex(cellRowIndex);
        int chunkColIndex = getChunkColIndex(cellColIndex);
        int chunkCellRowIndex = getChunkCellRowIndex(cellRowIndex);
        int chunkCellColIndex = getChunkCellColIndex(cellColIndex);
        //if ( inGrid( _CellRowIndex, _CellColIndex ) ) {
        Grids_AbstractGridChunkInt g = Grids_GridInt.this.getGridChunk(
                chunkRowIndex,
                chunkColIndex);
        return setCell(
                g,
                chunkRowIndex,
                chunkColIndex,
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
     *
     * @param chunkRowIndex
     * @param chunkColIndex
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @param newValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public int setCell(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int newValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = setCell(
                    chunkRowIndex,
                    chunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    newValue);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRowIndex,
                        chunkColIndex);
                if (ge.swapChunkExcept_Account(
                        this,
                        chunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        chunkID,
                        handleOutOfMemoryError);
                return setCell(
                        chunkRowIndex,
                        chunkColIndex,
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the value of the cell in chunk given by _ChunkRowIndex and
     * _ChunkColIndex and cell in the chunk given by chunkCellColIndex and
     * chunkCellRowIndex and setting it to newValue.
     *
     * @param chunkRowIndex
     * @param chunkColIndex
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @param newValue
     * @return
     */
    protected int setCell(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int newValue) {
        //if ( inGrid( _ChunkRowIndex, _ChunkColIndex, chunkCellRowIndex, chunkCellColIndex ) ) {
        Grids_AbstractGridChunkInt g = Grids_GridInt.this.getGridChunk(
                chunkRowIndex,
                chunkColIndex);
        return setCell(
                g,
                chunkRowIndex,
                chunkColIndex,
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
     * @param chunkRowIndex
     * @param chunkColIndex
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @param newValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int setCell(
            Grids_AbstractGridChunkInt _Grid2DSquareCellChunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int newValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = setCell(
                    _Grid2DSquareCellChunk,
                    chunkRowIndex,
                    chunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    newValue);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRowIndex,
                        chunkColIndex);
                if (ge.swapChunkExcept_Account(
                        this,
                        chunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        chunkID,
                        handleOutOfMemoryError);
                return setCell(
                        _Grid2DSquareCellChunk,
                        chunkRowIndex,
                        chunkColIndex,
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Value at _CellRowIndex, _CellColIndex and sets it to newValue.
     * @param _Grid2DSquareCellChunk
     * @param chunkRowIndex
     * @param chunkColIndex
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @param newValue
     */
    protected int setCell(
            Grids_AbstractGridChunkInt _Grid2DSquareCellChunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int newValue) {
        int _NoDataValue = getNoDataValue();
        int result = _NoDataValue;
        if (_Grid2DSquareCellChunk.getClass() == Grids_GridChunkInt64CellMap.class) {
            result = ((Grids_GridChunkInt64CellMap) _Grid2DSquareCellChunk).setCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    newValue,
                    result,
                    false);
        } else if (_Grid2DSquareCellChunk.getClass() == Grids_GridChunkIntArray.class) {
            result = ((Grids_GridChunkIntArray) _Grid2DSquareCellChunk).setCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    newValue,
                    result,
                    false);
        } else if (_Grid2DSquareCellChunk.getClass() == Grids_GridChunkIntMap.class) {
            result = ((Grids_GridChunkIntMap) _Grid2DSquareCellChunk).setCell(
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
        // Update Statistics
        upDateGridStatistics(
                newValue,
                result,
                _NoDataValue);
        return result;
    }

    /**
     * Initilises the value at _CellRowIndex, _CellColIndex.
     *
     * @param _CellRowIndex
     * @param _CellColIndex
     * @param valueToInitialise
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
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
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (ge.swapChunkExcept_Account(
                        this,
                        chunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        chunkID,
                        handleOutOfMemoryError);
                initCell(
                        _CellRowIndex,
                        _CellColIndex,
                        valueToInitialise,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initilises the value at _CellRowIndex, _CellColIndex.
     *
     * @param cellRowIndex
     * @param cellColIndex
     * @param valueToInitialise
     */
    protected void initCell(
            long cellRowIndex,
            long cellColIndex,
            int valueToInitialise) {
        boolean isInGrid = isInGrid(
                cellRowIndex,
                cellColIndex);
        if (isInGrid) {
            int chunkRowIndex = getChunkRowIndex(cellRowIndex);
            int chunkColIndex = getChunkColIndex(cellColIndex);
            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                    chunkRowIndex,
                    chunkColIndex);
            //boolean containsKey = this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.containsKey( a_ChunkID ); // Debugging code
            Grids_AbstractGridChunkInt _Grid2DSquareCellIntChunk = Grids_GridInt.this.getGridChunk(chunkID);
            _Grid2DSquareCellIntChunk.initCell(
                    (int) (cellRowIndex - ((long) chunkRowIndex * (long) ChunkNRows)),
                    (int) (cellColIndex - ((long) chunkColIndex * (long) ChunkNCols)),
                    valueToInitialise,
                    false);
            // Update Statistics
            int _NoDataValue = getNoDataValue(
                    ge.HandleOutOfMemoryErrorFalse);
            Grids_AbstractGridStatistics s;
            s = getGridStatistics();
            boolean handleOutOfMemoryError;
            handleOutOfMemoryError = ge.HandleOutOfMemoryError;

            if (valueToInitialise != _NoDataValue) {
                BigDecimal cellBigDecimal = new BigDecimal(valueToInitialise);
                s.setNonNoDataValueCount(s.getNonNoDataValueCountBigInteger(handleOutOfMemoryError).add(BigInteger.ONE));
                s.setSum(s.getSumBigDecimal(handleOutOfMemoryError).add(cellBigDecimal));
                if (cellBigDecimal.compareTo(s.getMin(false, handleOutOfMemoryError)) == -1) {
                    s.setMinCount(BigInteger.ONE);
                    s.setMin(cellBigDecimal);
                } else {
                    if (cellBigDecimal.compareTo(s.getMin(false, handleOutOfMemoryError)) == 0) {
                        s.setMinCount(s.getMinCount().add(BigInteger.ONE));
                    }
                }
                if (cellBigDecimal.compareTo(s.getMax(false, handleOutOfMemoryError)) == 1) {
                    s.setMaxCount(BigInteger.ONE);
                    s.setMax(cellBigDecimal);
                } else {
                    if (cellBigDecimal.compareTo(s.getMax(false, handleOutOfMemoryError)) == 0) {
                        s.setMaxCount(s.getMaxCount().add(BigInteger.ONE));
                    }
                }
            }
        }
    }

    /**
     * Initialises the value at _CellRowIndex, _CellColIndex and does nothing
     * about s.
     *
     * @param cellRowIndex
     * @param cellColIndex
     * @param valueToInitialise
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected void initCellFast(
            long cellRowIndex,
            long cellColIndex,
            int valueToInitialise,
            boolean handleOutOfMemoryError) {
        try {
            initCellFast(
                    cellRowIndex,
                    cellColIndex,
                    valueToInitialise);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellRowIndex),
                        getChunkColIndex(cellColIndex));
                if (ge.swapChunkExcept_Account(
                        this,
                        chunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        chunkID,
                        handleOutOfMemoryError);
                initCellFast(
                        cellRowIndex,
                        cellColIndex,
                        valueToInitialise,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises the value at cellRowIndex, cellColIndex and does nothing
     * about updating statistics.
     *
     * @param cellRowIndex
     * @param cellColIndex
     * @param valueToInitialise
     */
    protected void initCellFast(
            long cellRowIndex,
            long cellColIndex,
            int valueToInitialise) {
        boolean isInGrid = isInGrid(
                cellRowIndex,
                cellColIndex);
        if (isInGrid) {
            int chunkRowIndex = getChunkRowIndex(cellRowIndex);
            int chunkColIndex = getChunkColIndex(cellColIndex);
            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                    chunkRowIndex,
                    chunkColIndex);
            Grids_AbstractGridChunkInt _Grid2DSquareCellIntChunk = Grids_GridInt.this.getGridChunk(chunkID);
            _Grid2DSquareCellIntChunk.initCell(
                    (int) (cellRowIndex - ((long) chunkRowIndex * (long) ChunkNRows)),
                    (int) (cellColIndex - ((long) chunkColIndex * (long) ChunkNCols)),
                    valueToInitialise,
                    false);
        }
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
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
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                long _CellRowIndex = getCellRowIndex(y);
                long _CellColIndex = getCellColIndex(x);
                HashSet a_ChunkIDs = getChunkIDs(
                        distance,
                        x,
                        y,
                        _CellRowIndex,
                        _CellColIndex);
                if (ge.swapChunkExcept_Account(
                        this,
                        a_ChunkIDs,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        a_ChunkIDs,
                        handleOutOfMemoryError);
                return getCells(
                        x,
                        y,
                        distance,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
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
     * index _CellRowIndex, cell column index _CellColIndex, and radius
     * distance.
     * @param _CellRowIndex the row index for the cell thats centroid is the
     * circle centre from which cell values are returned.
     * @param _CellColIndex
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
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
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                double x = getCellXDouble(_CellColIndex);
                double y = getCellYDouble(_CellRowIndex);
                HashSet a_ChunkIDs = getChunkIDs(
                        distance,
                        x,
                        y,
                        _CellRowIndex,
                        _CellColIndex);
                if (ge.swapChunkExcept_Account(
                        this,
                        a_ChunkIDs,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        a_ChunkIDs,
                        handleOutOfMemoryError);
                return getCells(
                        _CellRowIndex,
                        _CellColIndex,
                        distance,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at centroid of cell given by cell row
     * index _CellRowIndex, cell column index _CellColIndex, and radius
     * distance.
     * @param _CellRowIndex the row index for the cell thats centroid is the
     * @param _CellColIndex circle centre from which cell values are returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
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
     * returned.
     * @param y The y-coordinate of the circle centre from which cell values are
     * returned.
     * @param cellRowIndex The row index at y.
     * @param cellColIndex
     * @param distance The radius of the circle for which intersected cell
     * values are returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int[] getCells(
            double x,
            double y,
            long cellRowIndex,
            long cellColIndex,
            double distance,
            boolean handleOutOfMemoryError) {
        try {
            return getCells(
                    x,
                    y,
                    cellRowIndex,
                    cellColIndex,
                    distance);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                HashSet chunkIDs = getChunkIDs(
                        distance,
                        x,
                        y,
                        cellRowIndex,
                        cellColIndex);
                if (ge.swapChunkExcept_Account(
                        this,
                        chunkIDs,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        chunkIDs,
                        handleOutOfMemoryError);
                return getCells(
                        x,
                        y,
                        cellRowIndex,
                        cellColIndex,
                        distance,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x The x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y The y-coordinate of the circle centre from which cell values are
     * returned.
     * @param cellRowIndex The row index at y.
     * @param cellColIndex
     * @param distance The radius of the circle for which intersected cell
     * values are returned.
     */
    protected int[] getCells(
            double x,
            double y,
            long cellRowIndex,
            long cellColIndex,
            double distance) {
        int[] cells;
        int cellDistance = (int) Math.ceil(distance / Dimensions.getCellsize().doubleValue());
        cells = new int[((2 * cellDistance) + 1) * ((2 * cellDistance) + 1)];
        long row = Long.MIN_VALUE;
        long col = Long.MIN_VALUE;
        double thisX = Double.MIN_VALUE;
        double thisY = Double.MIN_VALUE;
        int count = 0;
        for (row = cellRowIndex - cellDistance; row <= cellRowIndex + cellDistance; row++) {
            thisY = getCellYDouble(cellRowIndex);
            for (col = cellColIndex - cellDistance; col <= cellColIndex + cellDistance; col++) {
                thisX = getCellXDouble(cellColIndex);
                if (Grids_Utilities.distance(x, y, thisX, thisY) <= distance) {
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
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getNearestValueDouble(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            double result = getNearestValueDouble(
                    x,
                    y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
                if (ge.swapChunkExcept_Account(
                        this,
                        chunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        chunkID,
                        handleOutOfMemoryError);
                return getNearestValueDouble(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw e;
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
     * values is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getNearestValueDouble(
            long _CellRowIndex,
            long _CellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getNearestValueDouble(
                    _CellRowIndex,
                    _CellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(_CellRowIndex),
                        getChunkColIndex(_CellColIndex));
                if (ge.swapChunkExcept_Account(
                        this,
                        chunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(
                        this,
                        chunkID,
                        handleOutOfMemoryError);
                return getNearestValueDouble(
                        _CellRowIndex,
                        _CellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param _CellColIndex
     * @return the average of the nearest data values to position given by row
     * index rowIndex, column index colIndex as a double.
     * @param _CellRowIndex The row index from which average of the nearest data
     * values is returned.
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
     * @param cellColIndex
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex as a double.
     * @param x The x-coordinate of the point
     * @param y The y-coordinate of the point
     * @param cellRowIndex The row index from which average of the nearest data
     * values is returned.
     * @param _NoDataValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getNearestValueDouble(
            double x,
            double y,
            long cellRowIndex,
            long cellColIndex,
            int _NoDataValue,
            boolean handleOutOfMemoryError) {
        try {
            return getNearestValueDouble(
                    x,
                    y,
                    cellRowIndex,
                    cellColIndex,
                    _NoDataValue);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellRowIndex),
                        getChunkColIndex(cellColIndex));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDouble(
                        x,
                        y,
                        cellRowIndex,
                        cellColIndex,
                        _NoDataValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param cellColIndex
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex as a double.
     * @param x The x-coordinate of the point
     * @param y The y-coordinate of the point
     * @param cellRowIndex The row index from which average of the nearest data
     * values is returned.
     * @param _NoDataValue
     */
    protected double getNearestValueDouble(
            double x,
            double y,
            long cellRowIndex,
            long cellColIndex,
            int _NoDataValue) {
        Grids_2D_ID_long nearestCellID = getNearestCellID(
                x,
                y,
                cellRowIndex,
                cellColIndex);
        double nearestValue = getCell(
                cellRowIndex,
                cellColIndex);
        if (nearestValue == _NoDataValue) {
            // Find a value Seeking outwards from nearestCellID
            // Initialise visitedSet1
            HashSet visitedSet = new HashSet();
            HashSet visitedSet1 = new HashSet();
            visitedSet.add(nearestCellID);
            visitedSet1.add(nearestCellID);
            // Initialise toVisitSet1
            HashSet toVisitSet1 = new HashSet();
            long row;
            long col;
            Grids_2D_ID_long cellID0;
            boolean isInGrid;
            for (row = -1; row < 2; row++) {
                for (col = -1; col < 2; col++) {
                    if (!(row == 0 && col == 0)) {
                        isInGrid = isInGrid(
                                cellRowIndex + row,
                                cellColIndex + col);
                        if (isInGrid) {
                            cellID0 = new Grids_2D_ID_long(
                                    cellRowIndex + row,
                                    cellColIndex + col);
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
            Grids_2D_ID_long cellID1;
            while (!foundValue) {
                visitedSet2 = new HashSet();
                toVisitSet2 = new HashSet();
                iterator = toVisitSet1.iterator();
                while (iterator.hasNext()) {
                    cellID0 = (Grids_2D_ID_long) iterator.next();
                    visitedSet2.add(cellID0);
                    value = getCell(cellID0,
                            ge.HandleOutOfMemoryErrorTrue);
                    if (value != _NoDataValue) {
                        foundValue = true;
                        values.add(cellID0);
                    } else {
                        // Add neighbours to toVisitSet2
                        for (row = -1; row < 2; row++) {
                            for (col = -1; col < 2; col++) {
                                if (!(row == 0 && col == 0)) {
                                    isInGrid = isInGrid(
                                            cellID0.getRow() + row,
                                            cellID0.getCol() + col);
                                    if (isInGrid) {
                                        cellID1 = new Grids_2D_ID_long(
                                                cellID0.getRow() + row,
                                                cellID0.getCol() + col);
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
                cellID0 = (Grids_2D_ID_long) iterator.next();
                distance = Grids_Utilities.distance(
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
            Grids_2D_ID_long[] cellIDs = getCellIDs(
                    x,
                    y,
                    minDistance);
            // Go through values and find the closest
            for (int i = 0; i < cellIDs.length; i++) {
                if (!visitedSet.contains(cellIDs[i])) {
                    if (getCell(
                            cellIDs[i],
                            ge.HandleOutOfMemoryErrorTrue)
                            != _NoDataValue) {
                        distance = Grids_Utilities.distance(
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
                cellID0 = (Grids_2D_ID_long) iterator.next();
                value += getCell(
                        cellID0,
                        ge.HandleOutOfMemoryErrorTrue);
            }
            nearestValue = value / (double) closest.size();
        }
        return nearestValue;
    }

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values to point given by x-coordinate x, y-coordinate y.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_2D_ID_long[] getNearestValuesCellIDs(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long[] result = getNearestValuesCellIDs(
                    x,
                    y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValuesCellIDs(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values to point given by x-coordinate x, y-coordinate y.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */
    protected Grids_2D_ID_long[] getNearestValuesCellIDs(
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
        Grids_2D_ID_long[] cellIDs = new Grids_2D_ID_long[1];
        cellIDs[0] = getCellID(
                x,
                y);
        return cellIDs;
    }

    /**
     * @param cellColIndex
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values to position given by row index rowIndex, column index colIndex.
     * @param cellRowIndex The row index from which the cell IDs of the nearest
     * cells with data values are returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_2D_ID_long[] getNearestValuesCellIDs(
            long cellRowIndex,
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long[] result = getNearestValuesCellIDs(
                    cellRowIndex,
                    cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellRowIndex),
                        getChunkColIndex(cellColIndex));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValuesCellIDs(
                        cellRowIndex,
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values to position given by row index rowIndex, column index colIndex.
     * @param _CellRowIndex The row index from which the cell IDs of the nearest
     * cells with data values are returned.
     * @param _CellColIndex
     */
    protected Grids_2D_ID_long[] getNearestValuesCellIDs(
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
        Grids_2D_ID_long[] cellIDs = new Grids_2D_ID_long[1];
        cellIDs[0] = getCellID(
                _CellRowIndex,
                _CellColIndex);
        return cellIDs;
    }

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values nearest to point with position given by: x-coordinate x,
     * y-coordinate y; and, cell row index _CellRowIndex, cell column index
     * _CellColIndex.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param cellRowIndex The row index from which the cell IDs of the nearest
     * cells with data values are returned.
     * @param cellColIndex
     * @param _NoDataValue The no data value of the this.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public Grids_2D_ID_long[] getNearestValuesCellIDs(
            double x,
            double y,
            long cellRowIndex,
            long cellColIndex,
            int _NoDataValue,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long[] result = getNearestValuesCellIDs(
                    x,
                    y,
                    cellRowIndex,
                    cellColIndex,
                    _NoDataValue);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellRowIndex),
                        getChunkColIndex(cellColIndex));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValuesCellIDs(
                        x,
                        y,
                        cellRowIndex,
                        cellColIndex,
                        _NoDataValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values nearest to point with position given by: x-coordinate x,
     * y-coordinate y; and, cell row index _CellRowIndex, cell column index
     * _CellColIndex.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param _CellRowIndex The row index from which the cell IDs of the nearest
     * cells with data values are returned.
     * @param _CellColIndex
     * @param _NoDataValue The no data value of the this.
     */
    protected Grids_2D_ID_long[] getNearestValuesCellIDs(
            double x,
            double y,
            long _CellRowIndex,
            long _CellColIndex,
            int _NoDataValue) {
        Grids_2D_ID_long[] nearestCellIDs = new Grids_2D_ID_long[1];
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
            Grids_2D_ID_long cellID;
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
                    cellID = (Grids_2D_ID_long) iterator.next();
                    visitedSet2.add(cellID);
                    value = getCell(
                            cellID,
                            ge.HandleOutOfMemoryErrorTrue);
                    if (value != _NoDataValue) {
                        foundValue = true;
                        values.add(cellID);
                    } else {
                        // Add neighbours to toVisitSet2
                        for (row = -1; row < 2; row++) {
                            for (col = -1; col < 2; col++) {
                                if (!(row == 0 && col == 0)) {
                                    isInGrid = isInGrid(
                                            cellID.getRow() + row,
                                            cellID.getCol() + col);
                                    if (isInGrid) {
                                        cellID = getCellID(
                                                cellID.getRow() + row,
                                                cellID.getCol() + col);
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
                cellID = (Grids_2D_ID_long) iterator.next();
                distance = Grids_Utilities.distance(
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
            Grids_2D_ID_long[] cellIDs = getCellIDs(
                    x,
                    y,
                    minDistance);
            // Go through values and find the closest
            for (int i = 0; i < cellIDs.length; i++) {
                if (!visitedSet.contains(cellIDs[i])) {
                    if (getCell(cellIDs[i],
                            ge.HandleOutOfMemoryErrorTrue)
                            != _NoDataValue) {
                        distance = Grids_Utilities.distance(
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
            nearestCellIDs = new Grids_2D_ID_long[closest.size()];
            iterator = closest.iterator();
            int counter = 0;
            while (iterator.hasNext()) {
                nearestCellIDs[counter] = (Grids_2D_ID_long) iterator.next();
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
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getNearestValueDoubleDistance(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            double result = getNearestValueDoubleDistance(
                    x,
                    y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDoubleDistance(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw e;
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
     * @return the distance to the nearest data value from position given by row
     * index rowIndex, column index colIndex.
     * @param cellRowIndex The cell row index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     * @param cellColIndex
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getNearestValueDoubleDistance(
            long cellRowIndex,
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getNearestValueDoubleDistance(
                    cellRowIndex,
                    cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellRowIndex),
                        getChunkColIndex(cellColIndex));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDoubleDistance(
                        cellRowIndex,
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return the distance to the nearest data value from position given by row
     * index rowIndex, column index colIndex.
     * @param cellRowIndex The cell row index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     * @param cellColIndex
     */
    protected double getNearestValueDoubleDistance(
            long cellRowIndex,
            long cellColIndex) {
        int _NoDataValue = getNoDataValue();
        double result = getCell(
                cellRowIndex,
                cellColIndex);
        if (result == _NoDataValue) {
            result = getNearestValueDoubleDistance(
                    getCellXDouble(cellColIndex),
                    getCellYDouble(cellRowIndex),
                    cellRowIndex,
                    cellColIndex,
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
     * @param cellRowIndex The cell row index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     * @param cellColIndex The cell column index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     * @param _NoDataValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getNearestValueDoubleDistance(
            double x,
            double y,
            long cellRowIndex,
            long cellColIndex,
            int _NoDataValue,
            boolean handleOutOfMemoryError) {
        try {
            return getNearestValueDoubleDistance(
                    x,
                    y,
                    cellRowIndex,
                    cellColIndex,
                    _NoDataValue);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellRowIndex),
                        getChunkColIndex(cellColIndex));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDoubleDistance(
                        x,
                        y,
                        cellRowIndex,
                        cellColIndex,
                        _NoDataValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
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
     * distance nearest to the nearest cell value is returned.
     * @param _CellColIndex The cell column index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     */
    protected double getNearestValueDoubleDistance(
            double x,
            double y,
            long _CellRowIndex,
            long _CellColIndex,
            int _NoDataValue) {
        //            Grids_2D_ID_long nearestCellID = getNearestCellID(
        //                    x,
        //                    y,
        //                    _CellRowIndex,
        //                    _CellColIndexe );
        double result = getCell(
                _CellRowIndex,
                _CellColIndex);
        if (result == _NoDataValue) {
            // Initialisation
            long long0;
            long long1;
            long longMinus1 = -1;
            long longTwo = 2;
            long longZero = 0;
            boolean boolean0 = false;
            boolean boolean1 = false;
            boolean boolean2 = false;
            double double0 = Double.NEGATIVE_INFINITY;
            double double1 = Double.NEGATIVE_INFINITY;
            Grids_2D_ID_long nearestCellID = getNearestCellID(
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
            Grids_2D_ID_long cellID = new Grids_2D_ID_long();
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
                                ge.HandleOutOfMemoryErrorTrue);
                        if (isInGrid) {
                            cellID = getCellID(
                                    long0,
                                    long1,
                                    ge.HandleOutOfMemoryErrorTrue);
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
                    cellID = (Grids_2D_ID_long) iterator.next();
                    visitedSet2.add(cellID);
                    value = getCell(
                            cellID,
                            ge.HandleOutOfMemoryErrorTrue);
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
                                    long0 = cellID.getRow() + row;
                                    long1 = cellID.getCol() + col;
                                    isInGrid = isInGrid(
                                            long0,
                                            long1,
                                            ge.HandleOutOfMemoryErrorTrue);
                                    if (isInGrid) {
                                        cellID = getCellID(
                                                long0,
                                                long1,
                                                ge.HandleOutOfMemoryErrorTrue);
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
                cellID = (Grids_2D_ID_long) iterator.next();
                double0 = getCellXDouble(
                        cellID,
                        ge.HandleOutOfMemoryErrorTrue);
                double1 = getCellYDouble(
                        cellID,
                        ge.HandleOutOfMemoryErrorTrue);
                distance = Grids_Utilities.distance(
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
            Grids_2D_ID_long[] cellIDs = getCellIDs(
                    x,
                    y,
                    minDistance);
            // Go through values and find the closest
            for (int i = 0; i < cellIDs.length; i++) {
                if (!visitedSet.contains(cellIDs[i])) {
                    if (getCell(cellIDs[i],
                            ge.HandleOutOfMemoryErrorTrue)
                            != _NoDataValue) {
                        distance = Grids_Utilities.distance(
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
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
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
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return addToCell(
                        x,
                        y,
                        valueToAdd,
                        handleOutOfMemoryError);
            } else {
                throw e;
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
     * @return Value of the cell with cell Grids_2D_ID_int cellID and adds
     * valueToAdd to that cell.
     * @param cellID the Grids_2D_ID_long of the cell.
     * @param valueToAdd the value to be added to the cell containing the point
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int addToCell(
            Grids_2D_ID_long cellID,
            int valueToAdd,
            boolean handleOutOfMemoryError) {
        try {
            int result = addToCell(
                    cellID,
                    valueToAdd);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellID.getRow()),
                        getChunkColIndex(cellID.getCol()));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return addToCell(
                        cellID,
                        valueToAdd,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Value of the cell with cell Grids_2D_ID_int cellID and adds
     * valueToAdd to that cell.
     * @param cellID the Grids_2D_ID_long of the cell.
     * @param valueToAdd the value to be added to the cell containing the point
     */
    protected final int addToCell(
            Grids_2D_ID_long cellID,
            int valueToAdd) {
        return addToCell(
                cellID.getRow(),
                cellID.getCol(),
                valueToAdd);
    }

    /**
     * @return current value of cell with row index rowIndex and column index
     * colIndex and adds valueToAdd to that cell.
     * @param cellRowIndex the row index of the cell.
     * @param cellColIndex
     * @param valueToAdd the value to be added to the cell.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown. NB1. If cell is not
     * contained in this then then returns NoDataValue. NB2. Adding to
     * NoDataValue is done as if adding to a cell with value of 0. TODO: Check
     * Arithmetic
     */
    public int addToCell(
            long cellRowIndex,
            long cellColIndex,
            int valueToAdd,
            boolean handleOutOfMemoryError) {
        try {
            int result = addToCell(
                    cellRowIndex,
                    cellColIndex,
                    valueToAdd);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellRowIndex),
                        getChunkColIndex(cellColIndex));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return addToCell(
                        cellRowIndex,
                        cellColIndex,
                        valueToAdd,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return current value of cell with row index rowIndex and column index
     * colIndex and adds valueToAdd to that cell.
     * @param cellRowIndex the row index of the cell.
     * @param cellColIndex the col index of the cell.
     * @param valueToAdd the value to be added to the cell. NB1. If cell is not
     * contained in this then then returns NoDataValue. NB2. Adding to
     * NoDataValue is done as if adding to a cell with value of 0. TODO: Check
     * Arithmetic
     */
    protected int addToCell(
            long cellRowIndex,
            long cellColIndex,
            int valueToAdd) {
        if (isInGrid(
                cellRowIndex,
                cellColIndex,
                ge.HandleOutOfMemoryErrorFalse)) {
            int currentValue = getCell(
                    cellRowIndex,
                    cellColIndex);
            if (currentValue != NoDataValue) {
                if (valueToAdd != NoDataValue) {
                    return setCell(
                            cellRowIndex,
                            cellColIndex,
                            currentValue + valueToAdd);
                }
            } else {
                if (valueToAdd != NoDataValue) {
                    return setCell(
                            cellRowIndex,
                            cellColIndex,
                            valueToAdd);
                }
            }
        }
        return NoDataValue;
    }

    /**
     * @return an iterator over the cell value in this.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public Iterator iterator(
            boolean handleOutOfMemoryError) {
        try {
            Iterator result = new Grids_GridIntIterator(this);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunk_Account(false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return iterator(
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
}
