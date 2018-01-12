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
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;

/**
 * Abstract class to be extended by all Grids_AbstractGridNumber factories.
 */
public abstract class Grids_AbstractGridNumberFactory
        extends Grids_AbstractGridFactory {

    protected Grids_AbstractGridNumberFactory() {
    }

    public Grids_AbstractGridNumberFactory(Grids_Environment ge, int chunkNRows,
            int chunkNCols, Grids_Dimensions dimensions) {
        super(ge);
        ChunkNRows = chunkNRows;
        ChunkNCols = chunkNCols;
        Dimensions = dimensions;
    }

    /**
     * Sets Dimensions to dimensions.
     *
     * @param dimensions
     */
    public void setDimensions(Grids_Dimensions dimensions) {
        Dimensions = dimensions;
    }

    /////////////////////////
    // Create from scratch //
    /////////////////////////
    /**
     * @return Grids_AbstractGridNumber grid with all values as NoDataValues.
     * @param dir The Directory for storing the grid.
     * @param nRows The NRows for the construct.
     * @param nCols The NCols for the construct.
     */
    public Grids_AbstractGridNumber create(File dir, long nRows, long nCols) {
        return create(dir, nRows, nCols, getDimensions(nRows, nCols));
    }

    /**
     * @return Grids_AbstractGridNumber grid with all values as NoDataValues.
     * @param directory The Directory for storing the grid.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of Columns in the grid.
     * @param dimensions
     */
    public abstract Grids_AbstractGridNumber create(File directory, long nRows,
            long nCols, Grids_Dimensions dimensions);

    //////////////////////////////////////////////////////
    // Create from an existing Grids_AbstractGridNumber //
    //////////////////////////////////////////////////////
    /**
     * @return Grids_AbstractGridNumber with all values from g.
     * @param dir The Directory to be used for storing the grid.
     * @param g The Grids_AbstractGridNumber from which values are obtained.
     */
    public Grids_AbstractGridNumber create(File dir,
            Grids_AbstractGridNumber g) {
        return create(dir, g, 0L, 0L, g.getNRows() - 1L, g.getNCols() - 1L);
    }

    /**
     * @return Grids_AbstractGridNumber with values obtained from g.
     * @param dir The Directory to be used for storing the grid.
     * @param g The Grids_AbstractGridNumber from which values are obtained.
     * @param startRow The topmost row index of grid2DSquareCell thats values
     * are used.
     * @param startCol The leftmost column index of grid2DSquareCell thats
     * values are used.
     * @param endRow The bottom row index of the grid2DSquareCell thats values
     * are used.
     * @param endCol The rightmost column index of grid2DSquareCell thats values
     * are used.
     */
    public abstract Grids_AbstractGridNumber create(File dir,
            Grids_AbstractGridNumber g, long startRow, long startCol,
            long endRow, long endCol);

    ////////////////////////
    // Create from a File //
    ////////////////////////
    /**
     * @param dir The Directory to be used for storing the grid.
     * @return Grids_AbstractGridNumber with values obtained from gridFile. If
     * gridFile is a Directory then it is assumed to contain a file called cache
     * which can be opened into an object input stream and initialised as an
     * instance of a class extending Grids_AbstractGridNumber.
     * @param gridFile either a Directory, or a formatted File with a specific
     * extension containing the data and information about the
     * Grids_AbstractGridNumber to be returned.
     */
    public abstract Grids_AbstractGridNumber create(File dir, File gridFile);
//    {
//        if (gridFile.isDirectory()) {
//            // Initialise from File(gridFile,"this")
//            File thisFile = new File(gridFile, "thisFile");
//            try {
//                ObjectInputStream ois;
//                ois = Generic_StaticIO.getObjectInputStream(thisFile);
//                return create(Directory, gridFile, ois);
//            } catch (Exception e) {
//                System.out.println(e);
//                e.printStackTrace(System.err);
//            }
//        }
//        // Assume it is ESRI asciigrid
//        Grids_ESRIAsciiGridImporter eagi;
//        eagi = new Grids_ESRIAsciiGridImporter(gridFile, ge);
//        Grids_ESRIAsciiGridHeader header = eagi.readHeaderObject();
//        long nCols = header.NCols;
//        long nRows = header.NRows;
//        //double _NoDataValue = (Double) header[5];
//        eagi.close();
//        String gridName;
//        gridName = gridFile.getName().substring(0,
//                gridFile.getName().length() - 4);
//        String dirName;
//        dirName = gridFile.getParentFile()
//                + System.getProperty("file.separator")
//                + gridName + getClass().getName()
//                + "_ChunkNrows(" + ChunkNRows
//                + ")_ChunkNcols(" + ChunkNCols + ")";
//        //this.Directory = Grids_Files.createNewFile( new File( _DirectoryName ) );
//        Directory = new File(dirName);
//        Directory.mkdirs();
//        return create(Directory, gridFile, 0L, 0L, nRows - 1L, nCols - 1L);
//    }

    /**
     * @return Grids_AbstractGridNumber with values obtained from gridFile.
     * @param dir The Directory to be used for storing the grid.
     * @param gridFile either a Directory, or a formatted File with a specific
     * extension containing the data and information about the
     * Grids_AbstractGridNumber to be returned.
     * @param startRow The topmost row index of the grid represented in gridFile
     * thats values are used.
     * @param startCol The leftmost column index of the grid represented in
     * gridFile thats values are used.
     * @param endRow The bottom row index of the grid represented in gridFile
     * thats values are used.
     * @param endCol The rightmost column index of the grid represented in
     * gridFile thats values are used.
     */
    public abstract Grids_AbstractGridNumber create(File dir, File gridFile,
            long startRow, long startCol, long endRow, long endCol);

    /**
     * @return Grids_AbstractGridNumber with values obtained from gridFile.
     * @param dir The Directory to be used for storing the grid.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     */
    public abstract Grids_AbstractGridNumber create(File dir, File gridFile, 
            ObjectInputStream ois);
}
