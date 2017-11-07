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

import java.io.File;
import java.io.ObjectInputStream;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridImporter;

/**
 * Abstract class to be extended by all Grids_AbstractGridNumber factories.
 */
public abstract class Grids_AbstractGridNumberFactory extends Grids_AbstractGridFactory {

    protected Grids_AbstractGridNumberFactory() {
    }

    public Grids_AbstractGridNumberFactory(
            Grids_Environment ge,
            File directory,
            int chunkNRows,
            int chunkNCols,
            Grids_Dimensions dimensions) {
        super(ge);
        Directory = directory;
        ChunkNRows = chunkNRows;
        ChunkNCols = chunkNCols;
        Dimensions = dimensions;
    }

    /**
     * Sets Dimensions to dimensions.
     *
     * @param dimensions
     */
    public void setDimensions(
            Grids_Dimensions dimensions) {
        Dimensions = dimensions;
    }

    /////////////////////////
    // Create from scratch //
    /////////////////////////
    /**
     * @return Grids_AbstractGridNumber with all values as NoDataValues.
     * @param nRows The NRows for the construct.
     * @param nCols The NCols for the construct.
     */
    public Grids_AbstractGridNumber create(
            long nRows,
            long nCols) {
        return create(
                nRows,
                nCols,
                getDimensions(nRows, nCols));
    }

    /**
     * @return Grids_AbstractGridNumber with all values as _NoDataValues.
     * @param nRows The NRows for the construct.
     * @param nCols The NCols for the construct.
     * @param dimensions The cellsize and bounding box details for the
     * construct.
     */
    public Grids_AbstractGridNumber create(
            long nRows,
            long nCols,
            Grids_Dimensions dimensions) {
        return create(
                ge.getFiles().createNewFile(
                        Directory),
                nRows,
                nCols,
                dimensions,
                ge.HandleOutOfMemoryError);
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
        return create(
                Directory,
                g,
                0L,
                0L,
                g.getNRows(ge.HandleOutOfMemoryErrorTrue) - 1L,
                g.getNCols(ge.HandleOutOfMemoryErrorTrue) - 1L,
                ge.HandleOutOfMemoryError);
    }

    /**
     * @return Grids_AbstractGridNumber with values obtained from
     * grid2DSquareCell.
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
            Grids_AbstractGridNumber g,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex) {
        File file = ge.getFiles().createNewFile(Directory);
        return create(
                file,
                g,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                ge.HandleOutOfMemoryError);
    }

    /**
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
     * @param handleOutOfMemoryError
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
     * which can be opened into an object input stream and initialised as an
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
                return create(
                        Directory,
                        gridFile,
                        ois,
                        ge.HandleOutOfMemoryError);
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace(System.err);
            }
        }
        // Assume it is ESRI asciigrid
        Grids_ESRIAsciiGridImporter eagi;
        eagi = new Grids_ESRIAsciiGridImporter(
                gridFile,
                ge);
        Object[] header = eagi.readHeaderObject();
        long nCols = (Long) header[0];
        long nRows = (Long) header[1];
        //double _NoDataValue = (Double) header[5];
        eagi.close();
        String gridName = gridFile.getName().substring(0, gridFile.getName().length() - 4);
        String directoryName = gridFile.getParentFile()
                + System.getProperty("file.separator")
                + gridName
                + getClass().getName()
                + "_ChunkNrows(" + ChunkNRows + ")_ChunkNcols(" + ChunkNCols + ")";
        //this.Directory = Grids_Files.createNewFile( new File( _DirectoryName ) );
        Directory = new File(directoryName);
        Directory.mkdirs();
        return create(
                Directory,
                gridFile,
                0L,
                0L,
                nRows - 1L,
                nCols - 1L,
                ge.HandleOutOfMemoryError);
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
     * gridFile thats values are used.
     * @param handleOutOfMemoryError
     */
    public abstract Grids_AbstractGridNumber create(
            File directory,
            File gridFile,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError);

    /**
     * @return Grids_AbstractGridNumber with values obtained from gridFile.
     * @param directory The Directory for swapping to file.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     * @param handleOutOfMemoryError
     */
    public abstract Grids_AbstractGridNumber create(
            File directory,
            File gridFile,
            ObjectInputStream ois,
            boolean handleOutOfMemoryError);
}
