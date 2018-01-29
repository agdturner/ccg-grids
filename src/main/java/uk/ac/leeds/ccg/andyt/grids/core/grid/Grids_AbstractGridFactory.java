/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid;

import java.io.File;
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
