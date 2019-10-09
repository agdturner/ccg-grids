/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGridChunkIterator extends Grids_AbstractIterator {
    
    protected Grids_AbstractGrid Grid;
    protected Grids_AbstractGridChunk Chunk;
    
    protected Grids_AbstractGridChunkIterator(){}
    
    public Grids_AbstractGridChunkIterator(Grids_AbstractGridChunk chunk) {
        super(chunk.env);
        Chunk = chunk;
        Grid = Chunk.getGrid();        
    }
}
