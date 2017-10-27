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
import java.io.File;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_FileCreator;

/**
 * Abstract class to be extended by all Grids_AbstractGridNumber factories.
 */
public abstract class Grids_AbstractGridFactory extends Grids_Object {

    /**
     * A Directory for swapping.
     */
    protected File Directory;
    /**
     * The number of rows in a chunk.
     */
    protected int ChunkNRows;
    /**
     * The number of columns in a chunk.
     */
    protected int ChunkNCols;
    /**
     * The Dimensions
     */
    protected Grids_Dimensions Dimensions;
//    /**
//     * A container of other Grids_AbstractGridNumber references.
//     */
//    protected HashSet _AbstractGrid2DSquareCell_HashSet;
    /**
     * The Grids_AbstractGridStatistics
     */
    protected Grids_AbstractGridStatistics GridStatistics;
    /**
     * _HandleOutOfMemoryError
     */
    protected boolean HandleOutOfMemoryError;

    protected Grids_AbstractGridFactory() {
    }

    public Grids_AbstractGridFactory(Grids_Environment ge) {
        super(ge);
    }

    /**
     * Returns a copy of this.Directory.
     *
     * @return
     */
    protected File getDirectory() {
        return new File(this.Directory.getPath());
    }

    /**
     * Returns a copy of this.Directory.
     *
     * @param handleOutOfMemoryError
     * @return
     */
    public File getDirectory(
            boolean handleOutOfMemoryError) {
        try {
            return getDirectory();
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return getDirectory(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Sets this.Directory to Directory.
     *
     * @param directory
     */
    public void setDirectory(
            File directory) {
        this.Directory = directory;
    }

    /**
     * Returns a copy of this.ChunkNRows.
     *
     * @return
     */
    public int getChunkNRows() {
        return this.ChunkNRows;
    }

    /**
     * Sets this.ChunkNRows to ChunkNRows
     *
     * @param chunkNRows
     */
    public void setChunkNRows(
            int chunkNRows) {
        this.ChunkNRows = chunkNRows;
    }

    /**
     * Returns a copy of this.ChunkNCols.
     *
     * @return
     */
    public int getChunkNCols() {
        return this.ChunkNCols;
    }

    /**
     * Sets this.ChunkNCols to ChunkNCols
     *
     * @param chunkNCols
     */
    public void setChunkNCols(
            int chunkNCols) {
        this.ChunkNCols = chunkNCols;
    }

    protected final void initDimensions(
            int chunkNCols,
            int chunkNRows) {
        this.Dimensions = new Grids_Dimensions(
                new BigDecimal(0L),
                new BigDecimal(0L),
                new BigDecimal(chunkNCols),
                new BigDecimal(chunkNRows),
                new BigDecimal(1L));
    }

    /**
     * Returns this._Dimensions
     *
     * @return
     */
    public Grids_Dimensions getDimensions() {
        return this.Dimensions;
    }

    /**
     * Sets this._Dimensions to _Dimensions
     *
     * @param dimensions
     */
    public void setDimensions(
            Grids_Dimensions dimensions) {
        this.Dimensions = dimensions;
    }

//    /**
//     * Returns this._AbstractGrid2DSquareCell_HashSet
//     */
//    public HashSet getGrids() {
//        return this._AbstractGrid2DSquareCell_HashSet;
//    }
//    
//    /**
//     * Sets this._AbstractGrid2DSquareCell_HashSet to _AbstractGrid2DSquareCell_HashSet
//     */
//    public void setGrid2DSquareCells( 
//            HashSet _AbstractGrid2DSquareCell_HashSet ) {
//        this._AbstractGrid2DSquareCell_HashSet = _AbstractGrid2DSquareCell_HashSet;
//    }
    /**
     * Returns this.GridStatistics
     *
     * @return
     */
    public Grids_AbstractGridStatistics getGridStatistics() {
//        if (this.GridStatistics.getClass() == Grids_GridStatistics0.class) {
//            return new Grids_GridStatistics0(ge);
//        }
//        if (this.GridStatistics.getClass() == Grids_GridStatistics1.class) {
//            return new Grids_GridStatistics1(ge);
//        }
        return this.GridStatistics;
    }

    /**
     * Sets this._GridStatistics to gs.
     *
     * @param gridStatistics
     */
    public final void setGridStatistics(
            Grids_AbstractGridStatistics gridStatistics) {
        this.GridStatistics = gridStatistics;
    }

    /**
     * Returns this._HandleOutOfMemoryError
     *
     * @return
     */
    protected boolean getHandleOutOfMemoryError() {
        return this.HandleOutOfMemoryError;
    }

    /**
     * Sets this._HandleOutOfMemoryError to _HandleOutOfMemoryError
     *
     * @param handleOutOfMemoryError
     */
    public void setHandleOutOfMemoryError(
            boolean handleOutOfMemoryError) {
        this.HandleOutOfMemoryError = handleOutOfMemoryError;
    }

    //////////////////////
    // Default Creation //
    //////////////////////
    /**
     * @return Grids_AbstractGridNumber loaded from this.Directory.
     */
    public Grids_AbstractGridNumber create() {
        return create(this.Directory);
    }

    /////////////////////////
    // Create from scratch //
    /////////////////////////
    /**
     * @return Grids_AbstractGridNumber with all values as NoDataValues.
     * @param nRows The _NRows for the construct.
     * @param nCols The _NCols for the construct.
     */
    public Grids_AbstractGridNumber create(
            long nRows,
            long nCols) {
        // Correct the ymax and xmax of the grid just in case...
        Grids_Dimensions dimensions;
        dimensions = getDimensions(nRows, nCols);
        return create(
                nRows,
                nCols,
                dimensions);
    }

    protected Grids_Dimensions getDimensions(
            long nRows,
            long nCols) {
        Grids_Dimensions result;
        BigDecimal cellsize;
        cellsize = Dimensions.getCellsize();
        BigDecimal xMax = Dimensions.getXMin().add(
                new BigDecimal(nCols).multiply(cellsize));
        BigDecimal yMax = Dimensions.getYMin().add(
                new BigDecimal(nRows).multiply(cellsize));
        result = new Grids_Dimensions(
                Dimensions.getXMin(), Dimensions.getYMin(), xMax, yMax, cellsize);
        return result;
    }

    /**
     * @param directory
     * @return Grids_AbstractGridNumber with all values as _NoDataValues.
     * @param nRows The NumberRows for the construct.
     * @param nCols The _NCols for the construct.
     */
    public Grids_AbstractGridNumber create(
            File directory,
            long nRows,
            long nCols) {
        // Correct the ymax and xmax of the grid just in case...
        Grids_Dimensions dimensions;
        dimensions = getDimensions(nRows, nCols);
        directory.mkdirs();
        return create(Directory,
                nRows,
                nCols,
                dimensions);
    }

    /**
     * @return Grids_AbstractGridNumber with all values as _NoDataValues.
     * @param nRows The _NRows for the construct.
     * @param nCols The _NCols for the construct.
     * @param dimensions The cellsize and bounding box details for the
     * construct.
     */
    public Grids_AbstractGridNumber create(
            long nRows,
            long nCols,
            Grids_Dimensions dimensions) {
        return create(Grids_FileCreator.createNewFile(this.Directory),
                nRows,
                nCols,
                dimensions);
    }

    /**
     * @return Grids_AbstractGridNumber with all values as _NoDataValues.
     * @param directory The Directory for swapping to file.
     * @param nRows The _NRows for the construct.
     * @param nCols The _NCols for the construct.
     * @param dimensions The cellsize and bounding box details for the
     * construct.
     */
    public Grids_AbstractGridNumber create(
            File directory,
            long nRows,
            long nCols,
            Grids_Dimensions dimensions) {
        return create(
                directory,
                nRows,
                nCols,
                dimensions,
                this.HandleOutOfMemoryError);
    }

    /**
     * @return Grids_AbstractGridNumber grid with all values as _NoDataValues.
     * @param directory The Directory for swapping to file.
     * @param nRows The _NRows for the construct.
     * @param nCols The _NCols for the construct.
     * @param dimensions
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public abstract Grids_AbstractGridNumber create(
            File directory,
            long nRows,
            long nCols,
            Grids_Dimensions dimensions,
            boolean handleOutOfMemoryError);

    //////////////////////////////////////////////////////
    // Create from an existing Grids_AbstractGridNumber //
    //////////////////////////////////////////////////////
    /**
     * @return Grids_AbstractGridNumber with all values as int values from
     * _Grid2DSquareCell.
     * @param g The Grids_AbstractGridNumber from which values are obtained.
     */
    public Grids_AbstractGridNumber create(
            Grids_AbstractGridNumber g) {
        return create(this.Directory,
                g,
                0L,
                0L,
                g.getNRows(HandleOutOfMemoryError) - 1L,
                g.getNCols(HandleOutOfMemoryError) - 1L);
    }

    /**
     * @return Grids_AbstractGridNumber with values obtained from
     * grid2DSquareCell.
     * @param grid2DSquareCell The Grids_AbstractGridNumber from which values
     * are obtained.
     * @param startRowIndex The topmost row index of grid2DSquareCell thats
     * values are used.
     * @param startColIndex The leftmost column index of grid2DSquareCell thats
     * values are used.
     * @param endRowIndex The bottom row index of the grid2DSquareCell thats
     * values are used.
     * @param endColIndex The rightmost column index of grid2DSquareCell thats
     * values are used.
     */
    public Grids_AbstractGridNumber create(
            Grids_AbstractGridNumber grid2DSquareCell,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex) {
        File file = Grids_FileCreator.createNewFile(this.Directory,
                "" + startRowIndex + "_" + startColIndex + "_" + endRowIndex + "_" + endColIndex);
        return create(
                file,
                grid2DSquareCell,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex);
    }

    /**
     * @return Grids_AbstractGridNumber with values obtained from
     * grid2DSquareCell.
     * @param directory The Directory to be used for storing data in files.
     * Grid2DSquareCellInt information.
     * @param g The Grids_AbstractGridNumber from which values are obtained.
     * @param startRowIndex The topmost row index of grid2DSquareCell thats
     * values are used.
     * @param startColIndex The leftmost column index of grid2DSquareCell thats
     * values are used.
     * @param endRowIndex The bottom row index of the grid2DSquareCell thats
     * values are used.
     * @param endColIndex The rightmost column index of grid2DSquareCell thats
     * values are used.
     */
    public Grids_AbstractGridNumber create(
            File directory,
            Grids_AbstractGridNumber g,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex) {
        return create(
                directory,
                g,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                this.HandleOutOfMemoryError);
    }

    /**
     * @param handleOutOfMemoryError
     * @return Grids_AbstractGridNumber with values obtained from
     * grid2DSquareCell.
     * @param directory The Directory to be used for storing data in files.
     * @param g The Grids_AbstractGridNumber from which values are obtained.
     * @param startRowIndex The topmost row index of grid2DSquareCell thats
     * values are used.
     * @param startColIndex The leftmost column index of grid2DSquareCell thats
     * values are used.
     * @param endRowIndex The bottom row index of the grid2DSquareCell thats
     * values are used.
     * @param endColIndex The rightmost column index of grid2DSquareCell thats
     * values are used.
     */
    public abstract Grids_AbstractGridNumber create(
            File directory,
            Grids_AbstractGridNumber g,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError);

    ////////////////////////
    // Create from a File //
    ////////////////////////
    /**
     * @return Grids_AbstractGridNumber with values obtained from gridFile. If
     * gridFile is a Directory then it is assumed to contain a file called cache
     * which can be opened into an object input stream and initailised as an
     * instance of a class extending Grids_AbstractGridNumber.
     * @param gridFile either a Directory, or a formatted File with a specific
     * extension containing the data and information about the
     * Grids_AbstractGridNumber to be returned.
     */
    public Grids_AbstractGridNumber create(
            File gridFile) {
        if (gridFile.isDirectory()) {
            // Initialise from File(gridFile,"this")
            File thisFile = new File(
                    gridFile,
                    "thisFile");
            try {
                ObjectInputStream ois;
                ois = Generic_StaticIO.getObjectInputStream(thisFile);
                return create(this.Directory,
                        gridFile,
                        ois);
            } catch (Exception e0) {
                System.out.println(e0);
                e0.printStackTrace();
            }
        }
        // Assume it is ESRI asciigrid
        Grids_ESRIAsciiGridImporter eagi;
        eagi = new Grids_ESRIAsciiGridImporter(
                gridFile,
                ge);
        Object[] header = eagi.readHeaderObject();
        long _NCols = (Long) header[0];
        long _NRows = (Long) header[1];
        double _NoDataValue = (Double) header[5];
        eagi.close();
        String gridName = gridFile.getName().substring(0, gridFile.getName().length() - 4);
        String directoryName = gridFile.getParentFile()
                + System.getProperty("file.separator")
                + gridName
                + this.getClass().getName()
                + "_chunkNrows(" + ChunkNRows + ")_chunkNcols(" + ChunkNCols + ")";
        //this.Directory = Grids_FileCreator.createNewFile( new File( _DirectoryName ) );
        this.Directory = new File(directoryName);
        this.Directory.mkdirs();
        return create(
                this.Directory,
                gridFile,
                0L,
                0L,
                _NRows - 1L,
                _NCols - 1L);
    }

    /**
     * @return Grids_AbstractGridNumber with values obtained from gridFile.
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellInt information.
     * @param gridFile either a Directory, or a formatted File with a specific
     * extension containing the data and information about the
     * Grids_AbstractGridNumber to be returned.
     * @param startRowIndex The topmost row index of the grid represented in
     * gridFile thats values are used.
     * @param startColIndex The leftmost column index of the grid represented in
     * gridFile thats values are used.
     * @param endRowIndex The bottom row index of the grid represented in
     * gridFile thats values are used.
     * @param endColIndex The rightmost column index of the grid represented in
     * gridFile thats values are used. Default:
     * _AbstractGrid2DSquareCell_HashSet to null; _HandleOutOfMemoryError to
     * true.
     */
    public Grids_AbstractGridNumber create(
            File directory,
            File gridFile,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex) {
        return create(
                directory,
                gridFile,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                this.HandleOutOfMemoryError);
    }

    /**
     * @param handleOutOfMemoryError
     * @param ge
     * @return Grids_AbstractGridNumber with values obtained from gridFile.
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellInt information.
     * @param gridFile either a Directory, or a formatted File with a specific
     * extension containing the data and information about the
     * Grids_AbstractGridNumber to be returned.
     * @param startRowIndex The topmost row index of the grid represented in
     * gridFile thats values are used.
     * @param startColIndex The leftmost column index of the grid represented in
     * gridFile thats values are used.
     * @param endRowIndex The bottom row index of the grid represented in
     * gridFile thats values are used.
     * @param endColIndex The rightmost column index of the grid represented in
     * gridFile thats values are used.
     */
    public abstract Grids_AbstractGridNumber create(
            File directory,
            File gridFile,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError);

    /////////////////////////
    // Create from a cache //
    /////////////////////////
    /**
     * @return Grids_AbstractGridNumber with values obtained from gridFile.
     * @param directory The Directory for swapping to file.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     */
    public Grids_AbstractGridNumber create(
            File directory,
            File gridFile,
            ObjectInputStream ois) {
        return create(
                directory,
                gridFile,
                ois,
                this.HandleOutOfMemoryError);
    }

    /**
     * @param handleOutOfMemoryError
     * @return Grids_AbstractGridNumber with values obtained from gridFile.
     * @param directory The Directory for swapping to file.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     */
    public abstract Grids_AbstractGridNumber create(
            File directory,
            File gridFile,
            ObjectInputStream ois,
            boolean handleOutOfMemoryError);
}
