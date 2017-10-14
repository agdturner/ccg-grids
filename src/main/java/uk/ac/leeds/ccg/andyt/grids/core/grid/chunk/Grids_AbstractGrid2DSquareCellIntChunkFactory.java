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
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGrid2DSquareCellIntChunk;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_Grid2DSquareCellInt;

/**
 * Abstract Class for defining (an interface for) chunk factory methods. These 
 * methods generally would work as protected, but are tested externally and so 
 * are declared public. Really no user should have a chunk without a grid even 
 * if the grid contains only one chunk.
 */
public abstract class Grids_AbstractGrid2DSquareCellIntChunkFactory 
        extends Grids_AbstractGrid2DSquareCellChunkFactory {
    
    /**
     * For creating a new default AbstractGrid2DSquareCellIntChunk.
     * @return 
     */
    public abstract Grids_AbstractGrid2DSquareCellIntChunk createGrid2DSquareCellIntChunk();
    
    /**
     * Creates a new AbstractGrid2DSquareCellIntChunk containing all 
     * noDataValues that is linked to grid2DSquareCellInt via chunkID.
     * 
     * @param grid2DSquareCellInt
     * @param chunkID
     * @return 
     */
    public abstract Grids_AbstractGrid2DSquareCellIntChunk createGrid2DSquareCellIntChunk( 
            Grids_Grid2DSquareCellInt grid2DSquareCellInt, 
            Grids_2D_ID_int chunkID );
    
    /**
     * Creates a new AbstractGrid2DSquareCellIntChunk with values taken from 
     * grid2DSquareCellIntChunk.
     * 
     * @param grid2DSquareCellIntChunk
     * @param chunkID
     * @return 
     */
    public abstract Grids_AbstractGrid2DSquareCellIntChunk createGrid2DSquareCellIntChunk( 
            Grids_AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk, 
            Grids_2D_ID_int chunkID );
    
}
