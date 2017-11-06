/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGridChunkNumberIterator extends Grids_AbstractIterator {
    
    protected Grids_AbstractGridNumber Grid;
    protected Grids_AbstractGridChunkNumber Chunk;
    
    protected Grids_AbstractGridChunkNumberIterator(){}
    
    public Grids_AbstractGridChunkNumberIterator(Grids_AbstractGridChunkNumber chunk) {
        super(chunk.ge);
        Chunk = chunk;
        Grid = Chunk.getGrid();        
    }
}
