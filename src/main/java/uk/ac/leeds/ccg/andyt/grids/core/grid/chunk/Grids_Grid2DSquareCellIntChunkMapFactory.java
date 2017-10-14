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
 * A factory for constructing Grid2DSquareCellIntChunkMap instances.
 */
public class Grids_Grid2DSquareCellIntChunkMapFactory
        extends Grids_AbstractGrid2DSquareCellIntChunkFactory {
    
    /**
     * Creates a new Grid2DSquareCellIntChunkMapFactory.
     */
    public Grids_Grid2DSquareCellIntChunkMapFactory(){}
    
    /**
     * Create new _Grid2DSquareCellIntChunk.
     * @return 
     */
    @Override
    public Grids_AbstractGrid2DSquareCellIntChunk createGrid2DSquareCellIntChunk() {
        return new Grids_Grid2DSquareCellIntChunkMap();
    }
    
    /**
     * Create new _Grid2DSquareCellIntChunk.
     * @param _Grid2DSquareCellInt
     * @return 
     */
    @Override
    public Grids_AbstractGrid2DSquareCellIntChunk createGrid2DSquareCellIntChunk(
            Grids_Grid2DSquareCellInt _Grid2DSquareCellInt,
            Grids_2D_ID_int chunkID) {
        return new Grids_Grid2DSquareCellIntChunkMap(
                _Grid2DSquareCellInt,
                chunkID );
    }
    
    /**
     * Create new _Grid2DSquareCellIntChunk.
     * @param _Grid2DSquareCellIntChunk
     * @return 
     */
    @Override
    public Grids_AbstractGrid2DSquareCellIntChunk createGrid2DSquareCellIntChunk(
            Grids_AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellIntChunk,
            Grids_2D_ID_int chunkID) {
        return new Grids_Grid2DSquareCellIntChunkMap(
                _Grid2DSquareCellIntChunk,
                chunkID );
    }
    
}
