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

import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_AbstractGridStatistics;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_GridStatistics1;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_GridStatistics0;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_FileCreator;

/**
 * Abstract class to be extended by all Grids_AbstractGrid2DSquareCell 
 factories.
 */
public abstract class Grids_AbstractGrid2DSquareCellFactory {

    protected Grids_Environment _Grids_Environment;
    /**
     * A _Directory for swapping.
     */
    protected File _Directory;
    /**
     * The number of rows in a chunk.
     */
    protected int _ChunkNRows;
    /**
     * The number of columns in a chunk.
     */
    protected int _ChunkNCols;
    /**
     * The Dimensions
     */
    protected BigDecimal[] _Dimensions;
//    /**
//     * A container of other Grids_AbstractGrid2DSquareCell references.
//     */
//    protected HashSet _AbstractGrid2DSquareCell_HashSet;
    /**
     * The Grids_AbstractGridStatistics
     */
    private Grids_AbstractGridStatistics _GridStatistics;
    /**
     * _HandleOutOfMemoryError
     */
    protected boolean _HandleOutOfMemoryError;

    /**
     * Returns a copy of this._Directory.
     * @return 
     */
    protected File get_Directory() {
        return new File(this._Directory.getPath());
    }

    /**
     * Returns a copy of this._Directory.
     * @param handleOutOfMemoryError
     * @return 
     */
    public File get_Directory(
            boolean handleOutOfMemoryError) {
        try {
            return get_Directory();
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _Grids_Environment.clear_MemoryReserve();
                if (_Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw _OutOfMemoryError;
                }
                _Grids_Environment.init_MemoryReserve(handleOutOfMemoryError);
                return get_Directory(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Sets this._Directory to _Directory.
     * @param _Directory
     */
    public void set_Directory(
            File _Directory) {
        this._Directory = _Directory;
    }

    /**
     * Returns a copy of this._ChunkNRows.
     * @return 
     */
    public int get_ChunkNRows() {
        return this._ChunkNRows;
    }

    /**
     * Sets this._ChunkNRows to _ChunkNRows
     * @param _ChunkNRows
     */
    public void set_ChunkNRows(
            int _ChunkNRows) {
        this._ChunkNRows = _ChunkNRows;
    }

    /**
     * Returns a copy of this._ChunkNCols.
     * @return 
     */
    public int get_ChunkNCols() {
        return this._ChunkNCols;
    }

    /**
     * Sets this._ChunkNCols to _ChunkNCols
     * @param _ChunkNCols
     */
    public void set_ChunkNCols(
            int _ChunkNCols) {
        this._ChunkNCols = _ChunkNCols;
    }

    /**
     * Returns this._Dimensions
     * @return 
     */
    public BigDecimal[] get_Dimensions() {
        return this._Dimensions;
    }

    /**
     * Sets this._Dimensions to _Dimensions
     * @param _Dimensions
     */
    public void set_Dimensions(
            BigDecimal[] _Dimensions) {
        this._Dimensions = _Dimensions;
    }

//    /**
//     * Returns this._AbstractGrid2DSquareCell_HashSet
//     */
//    public HashSet getGrid2DSquareCells() {
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
     * Returns this._GridStatistics
     * @return 
     */
    public Grids_AbstractGridStatistics get_GridStatistics() {
        if (this._GridStatistics.getClass() == Grids_GridStatistics0.class) {
            return new Grids_GridStatistics0();
        }
        if (this._GridStatistics.getClass() == Grids_GridStatistics1.class) {
            return new Grids_GridStatistics1();
        }
        return this._GridStatistics;
    }

    /**
     * Sets this._GridStatistics to _GridStatistics
     * @param _GridStatistics
     */
    public void set_GridStatistics(
            Grids_AbstractGridStatistics _GridStatistics) {
        this._GridStatistics = _GridStatistics;
    }

    /**
     * Returns this._HandleOutOfMemoryError
     * @return 
     */
    protected boolean getHandleOutOfMemoryError() {
        return this._HandleOutOfMemoryError;
    }

    /**
     * Sets this._HandleOutOfMemoryError to _HandleOutOfMemoryError
     * @param handleOutOfMemoryError
     */
    public void setHandleOutOfMemoryError(
            boolean handleOutOfMemoryError) {
        this._HandleOutOfMemoryError = handleOutOfMemoryError;
    }

    //////////////////////
    // Default Creation //
    //////////////////////
    /**
     * @return Grids_AbstractGrid2DSquareCell loaded from this._Directory.
     */
    public Grids_AbstractGrid2DSquareCell create() {
        return create(this._Directory);
    }

    /////////////////////////
    // Create from scratch //
    /////////////////////////
    /**
     * @return Grids_AbstractGrid2DSquareCell with all values as
 _NoDataValues.
     * @param _NRows The _NRows for the construct.
     * @param _NCols The _NCols for the construct.
     */
    public Grids_AbstractGrid2DSquareCell create(
            long _NRows,
            long _NCols) {
        // Correct the ymax and xmax of the grid just in case...
        this._Dimensions[3] = _Dimensions[1].add(
                new BigDecimal(_NCols).multiply(_Dimensions[0]));
        this._Dimensions[4] = _Dimensions[2].add(
                new BigDecimal(_NRows).multiply(_Dimensions[0]));
        return create(
                _NRows,
                _NCols,
                this._Dimensions);
    }

    /**
     * @param directory
     * @return Grids_AbstractGrid2DSquareCell with all values as
 _NoDataValues.
     * @param _NRows The _NRows for the construct.
     * @param _NCols The _NCols for the construct.
     */
    public Grids_AbstractGrid2DSquareCell create(
            File directory,
            long _NRows,
            long _NCols) {
        // Correct the ymax and xmax of the grid just in case...
        this._Dimensions[3] = _Dimensions[1].add(
                new BigDecimal(_NCols).multiply(_Dimensions[0]));
        this._Dimensions[4] = _Dimensions[2].add(
                new BigDecimal(_NRows).multiply(_Dimensions[0]));
        directory.mkdirs();
        return create(
                _Directory,
                _NRows,
                _NCols,
                this._Dimensions);
    }

    /**
     * @return Grids_AbstractGrid2DSquareCell with all values as
 _NoDataValues.
     * @param _NRows The _NRows for the construct.
     * @param _NCols The _NCols for the construct.
     * @param _Dimensions The cellsize and bounding box details for the construct.
     */
    public Grids_AbstractGrid2DSquareCell create(
            long _NRows,
            long _NCols,
            BigDecimal[] _Dimensions) {
        return create(Grids_FileCreator.createNewFile(this._Directory),
                _NRows,
                _NCols,
                _Dimensions);
    }

    /**
     * @return Grids_AbstractGrid2DSquareCell with all values as
 _NoDataValues.
     * @param _Directory The _Directory for swapping to file.
     * @param _NRows The _NRows for the construct.
     * @param _NCols The _NCols for the construct.
     * @param _Dimensions The cellsize and bounding box details for the construct.
     */
    public Grids_AbstractGrid2DSquareCell create(
            File _Directory,
            long _NRows,
            long _NCols,
            BigDecimal[] _Dimensions) {
        return create(
                _Directory,
                _NRows,
                _NCols,
                _Dimensions,
                this._Grids_Environment,
                this._HandleOutOfMemoryError);
    }

    /**
     * @return Grids_AbstractGrid2DSquareCell grid with all values as
 _NoDataValues.
     * @param _Directory The _Directory for swapping to file.
     * @param _NRows The _NRows for the construct.
     * @param _NCols The _NCols for the construct.
     * @param dimensions
     * @param _Grids_Environment 
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     *   in this method then swap operations are initiated prior to retrying.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public abstract Grids_AbstractGrid2DSquareCell create(
            File _Directory,
            long _NRows,
            long _NCols,
            BigDecimal[] dimensions,
            Grids_Environment _Grids_Environment,
            boolean handleOutOfMemoryError);

    //////////////////////////////////////////////////////
    // Create from an existing Grids_AbstractGrid2DSquareCell //
    //////////////////////////////////////////////////////
    /**
     * @return Grids_AbstractGrid2DSquareCell with all values as int values from
 _Grid2DSquareCell.
     * @param _Grid2DSquareCell The Grids_AbstractGrid2DSquareCell from which values
   are obtained.
     */
    public Grids_AbstractGrid2DSquareCell create(
            Grids_AbstractGrid2DSquareCell _Grid2DSquareCell) {
        return create(
                this._Directory,
                _Grid2DSquareCell,
                0L,
                0L,
                _Grid2DSquareCell.get_NRows(_HandleOutOfMemoryError) - 1L,
                _Grid2DSquareCell.get_NCols(_HandleOutOfMemoryError) - 1L);
    }

    /**
     * @return Grids_AbstractGrid2DSquareCell with values obtained from
 grid2DSquareCell.
     * @param grid2DSquareCell The Grids_AbstractGrid2DSquareCell from which values
   are obtained.
     * @param startRowIndex The topmost row index of grid2DSquareCell thats
     *   values are used.
     * @param startColIndex The leftmost column index of grid2DSquareCell thats
     *   values are used.
     * @param endRowIndex The bottom row index of the grid2DSquareCell thats
     *   values are used.
     * @param endColIndex The rightmost column index of grid2DSquareCell thats
     *   values are used.
     */
    public Grids_AbstractGrid2DSquareCell create(
            Grids_AbstractGrid2DSquareCell grid2DSquareCell,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex) {
        File file = Grids_FileCreator.createNewFile(
                this._Directory,
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
     * @return Grids_AbstractGrid2DSquareCell with values obtained from
 grid2DSquareCell.
     * @param _Directory The _Directory to be used for storing data in files.
     *   Grid2DSquareCellInt information.
     * @param grid2DSquareCell The Grids_AbstractGrid2DSquareCell from which values
   are obtained.
     * @param startRowIndex The topmost row index of grid2DSquareCell thats
     *   values are used.
     * @param startColIndex The leftmost column index of grid2DSquareCell thats
     *   values are used.
     * @param endRowIndex The bottom row index of the grid2DSquareCell thats
     *   values are used.
     * @param endColIndex The rightmost column index of grid2DSquareCell thats
     *   values are used.
     */
    public Grids_AbstractGrid2DSquareCell create(
            File _Directory,
            Grids_AbstractGrid2DSquareCell grid2DSquareCell,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex) {
        return create(
                _Directory,
                grid2DSquareCell,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                _Grids_Environment,
                this._HandleOutOfMemoryError);
    }

    /**
     * @param handleOutOfMemoryError
     * @return Grids_AbstractGrid2DSquareCell with values obtained from
 grid2DSquareCell.
     * @param _Directory The _Directory to be used for storing data in files.
     * @param grid2DSquareCell The Grids_AbstractGrid2DSquareCell from which values
   are obtained.
     * @param startRowIndex The topmost row index of grid2DSquareCell thats
     *   values are used.
     * @param startColIndex The leftmost column index of grid2DSquareCell thats
     *   values are used.
     * @param endRowIndex The bottom row index of the grid2DSquareCell thats
     *   values are used.
     * @param endColIndex The rightmost column index of grid2DSquareCell thats
     *   values are used.
     * @param _Grids_Environment A HashSet of swappable Grids_AbstractGrid2DSquareCell
   instances.
     */
    public abstract Grids_AbstractGrid2DSquareCell create(
            File _Directory,
            Grids_AbstractGrid2DSquareCell grid2DSquareCell,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment _Grids_Environment,
            boolean handleOutOfMemoryError);

    ////////////////////////
    // Create from a File //
    ////////////////////////
    /**
     * @return Grids_AbstractGrid2DSquareCell with values obtained from
 gridFile. If gridFile is a _Directory then it is assumed to contain a
 file called cache which can be opened into an object input stream and
 initailised as an instance of a class extending Grids_AbstractGrid2DSquareCell.
     * @param gridFile either a _Directory, or a formatted File with a specific
   extension containing the data and information about the
   Grids_AbstractGrid2DSquareCell to be returned.
     */
    public Grids_AbstractGrid2DSquareCell create(
            File gridFile) {
        if (gridFile.isDirectory()) {
            // Initialise from File(gridFile,"this")
            File thisFile = new File(
                    gridFile,
                    "thisFile");
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(
                        new BufferedInputStream(
                        new FileInputStream(thisFile)));
                return create(
                        this._Directory,
                        gridFile,
                        objectInputStream);
            } catch (Exception e0) {
                System.out.println(e0);
                e0.printStackTrace();
            }
        }
        // Assume it is ESRI asciigrid
        Grids_ESRIAsciiGridImporter _ESRIAsciigridImporter = new Grids_ESRIAsciiGridImporter(
                gridFile,
                _Grids_Environment);
        Object[] header = _ESRIAsciigridImporter.readHeaderObject();
        long _NCols = (Long) header[ 0];
        long _NRows = (Long) header[ 1];
        double _NoDataValue = (Double) header[ 5];
        _ESRIAsciigridImporter.close();
        String gridName = gridFile.getName().substring(0, gridFile.getName().length() - 4);
        String _DirectoryName = gridFile.getParentFile()
                + System.getProperty("file.separator")
                + gridName
                + this.getClass().getName()
                + "_chunkNrows(" + _ChunkNRows + ")_chunkNcols(" + _ChunkNCols + ")";
        //this._Directory = Grids_FileCreator.createNewFile( new File( _DirectoryName ) );
        this._Directory = new File(_DirectoryName);
        this._Directory.mkdirs();
        return create(
                this._Directory,
                gridFile,
                0L,
                0L,
                _NRows - 1L,
                _NCols - 1L);
    }

    /**
     * @return Grids_AbstractGrid2DSquareCell with values obtained from
 gridFile.
     * @param _Directory The _Directory to be used for storing cached
     *   Grid2DSquareCellInt information.
     * @param gridFile either a _Directory, or a formatted File with a specific
   extension containing the data and information about the
   Grids_AbstractGrid2DSquareCell to be returned.
     * @param startRowIndex The topmost row index of the grid represented in
     *   gridFile thats values are used.
     * @param startColIndex The leftmost column index of the grid represented in
     *   gridFile thats values are used.
     * @param endRowIndex The bottom row index of the grid represented in
     *   gridFile thats values are used.
     * @param endColIndex The rightmost column index of the grid represented in
     *   gridFile thats values are used.
     * Default:
     * _AbstractGrid2DSquareCell_HashSet to null;
     * _HandleOutOfMemoryError to true.
     */
    public Grids_AbstractGrid2DSquareCell create(
            File _Directory,
            File gridFile,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex) {
        return create(
                _Directory,
                gridFile,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                this._Grids_Environment,
                this._HandleOutOfMemoryError);
    }

    /**
     * @param handleOutOfMemoryError
     * @param _Grids_Environment
     * @return Grids_AbstractGrid2DSquareCell with values obtained from
 gridFile.
     * @param _Directory The _Directory to be used for storing cached
     *   Grid2DSquareCellInt information.
     * @param gridFile either a _Directory, or a formatted File with a specific
   extension containing the data and information about the
   Grids_AbstractGrid2DSquareCell to be returned.
     * @param startRowIndex The topmost row index of the grid represented in
     *   gridFile thats values are used.
     * @param startColIndex The leftmost column index of the grid represented in
     *   gridFile thats values are used.
     * @param endRowIndex The bottom row index of the grid represented in
     *   gridFile thats values are used.
     * @param endColIndex The rightmost column index of the grid represented in
     *   gridFile thats values are used.
     * @param _AbstractGrid2DSquareCell_HashSet A HashSet of swappable Grids_AbstractGrid2DSquareCell
   instances.
     */
    public abstract Grids_AbstractGrid2DSquareCell create(
            File _Directory,
            File gridFile,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment _Grids_Environment,
            boolean handleOutOfMemoryError);

    /////////////////////////
    // Create from a cache //
    /////////////////////////
    /**
     * @return Grids_AbstractGrid2DSquareCell with values obtained from 
 gridFile.
     * @param _Directory The _Directory for swapping to file.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     */
    public Grids_AbstractGrid2DSquareCell create(
            File _Directory,
            File gridFile,
            ObjectInputStream ois) {
        return create(
                _Directory,
                gridFile,
                ois,
                this._Grids_Environment,
                this._HandleOutOfMemoryError);
    }

    /**
     * @param handleOutOfMemoryError
     * @param _Grids_Environment
     * @return Grids_AbstractGrid2DSquareCell with values obtained from gridFile.
     * @param _Directory The _Directory for swapping to file.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     * @param _AbstractGrid2DSquareCell_HashSet A HashSet of swappable Grids_AbstractGrid2DSquareCell
   instances.
     */
    public abstract Grids_AbstractGrid2DSquareCell create(
            File _Directory,
            File gridFile,
            ObjectInputStream ois,
            Grids_Environment _Grids_Environment,
            boolean handleOutOfMemoryError);
}
