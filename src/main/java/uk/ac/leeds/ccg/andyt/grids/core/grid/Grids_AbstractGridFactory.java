/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGridFactory extends Grids_Object {
    
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

    public Grids_AbstractGridFactory() {
    }

    public Grids_AbstractGridFactory(Grids_Environment ge) {
        super(ge);
    }

    public Grids_AbstractGridFactory(Grids_Environment ge, int chunkNRows,
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
     * @return Grids_AbstractGrid grid with all values as false or NoDataValues.
     * @param dir The Directory for storing the grid.
     * @param nRows The NRows for the construct.
     * @param nCols The NCols for the construct.
     */
    public Grids_AbstractGrid create(File dir, long nRows, long nCols) throws IOException {
        return create(dir, nRows, nCols, getDimensions(nRows, nCols));
    }

    /**
     * @return Grids_AbstractGrid grid with all values as false or NoDataValues.
     * @param directory The Directory for storing the grid.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of Columns in the grid.
     * @param dimensions
     */
    public abstract Grids_AbstractGrid create(File directory, long nRows,
            long nCols, Grids_Dimensions dimensions) throws IOException ;

    ////////////////////////////////////////////////
    // Create from an existing Grids_AbstractGrid //
    ////////////////////////////////////////////////
    /**
     * @return Grids_AbstractGridNumber with all values from g.
     * @param dir The Directory to be used for storing the grid.
     * @param g The Grids_AbstractGridNumber from which values are obtained.
     */
    public Grids_AbstractGrid create(File dir, Grids_AbstractGrid g) throws IOException {
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
    public abstract Grids_AbstractGrid create(File dir, Grids_AbstractGrid g, 
            long startRow, long startCol,            long endRow, long endCol) throws IOException ;

    ////////////////////////
    // Create from a File //
    ////////////////////////
    /**
     * @param dir The Directory to be used for storing the grid.
     * @return Grids_AbstractGrid with values obtained from gridFile. If
     * gridFile is a Directory then it is assumed to contain a file called cache
     * which can be opened into an object input stream and initialised as an
     * instance of a class extending Grids_AbstractGridNumber.
     * @param gridFile either a Directory, or a formatted File with a specific
     * extension containing the data and information about the
     * Grids_AbstractGridNumber to be returned.
     */
    public abstract Grids_AbstractGrid create(File dir, File gridFile)throws IOException ;
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
     * @return Grids_AbstractGrid with values obtained from gridFile.
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
    public abstract Grids_AbstractGrid create(File dir, File gridFile,
            long startRow, long startCol, long endRow, long endCol)throws IOException ;

    /**
     * @return Grids_AbstractGridNumber with values obtained from gridFile.
     * @param dir The Directory to be used for storing the grid.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     */
    public abstract Grids_AbstractGrid create(File dir, File gridFile, 
            ObjectInputStream ois);

    /**
     * Return ChunkNRows.
     *
     * @return
     */
    public int getChunkNRows() {
        return ChunkNRows;
    }

    /**
     * Sets ChunkNRows to chunkNRows.
     *
     * @param chunkNRows
     */
    public void setChunkNRows(int chunkNRows) {
        ChunkNRows = chunkNRows;
    }

    /**
     * Returns ChunkNCols.
     *
     * @return
     */
    public int getChunkNCols() {
        return ChunkNCols;
    }

    /**
     * Sets ChunkNCols to chunkNCols.
     *
     * @param chunkNCols
     */
    public void setChunkNCols(int chunkNCols) {
        ChunkNCols = chunkNCols;
    }

    /**
     * Initialise Dimensions. Defaulting the origin to 0,0 and cellsize to 1.
     *
     * @param chunkNCols
     * @param chunkNRows
     */
    protected final void getDimensions(int chunkNCols, int chunkNRows) {
        Dimensions = new Grids_Dimensions(
                new BigDecimal(0L), 
                new BigDecimal(chunkNCols),
                new BigDecimal(0L),
                new BigDecimal(chunkNRows),
                new BigDecimal(1L));
    }

    /**
     * Returns Dimensions.
     *
     * @return
     */
    public Grids_Dimensions getDimensions() {
        return Dimensions;
    }

    protected Grids_Dimensions getDimensions(long nRows, long nCols) {
        Grids_Dimensions result;
        BigDecimal cellsize;
        cellsize = Dimensions.getCellsize();
        BigDecimal xMax = Dimensions.getXMin().add(new BigDecimal(nCols).multiply(cellsize));
        BigDecimal yMax = Dimensions.getYMin().add(new BigDecimal(nRows).multiply(cellsize));
        result = new Grids_Dimensions(
                Dimensions.getXMin(), 
                xMax,
                Dimensions.getYMin(), 
                yMax, 
                cellsize);
        return result;
    }
    
}
