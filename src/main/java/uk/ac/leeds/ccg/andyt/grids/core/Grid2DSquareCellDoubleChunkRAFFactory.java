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
import java.io.File;
import uk.ac.leeds.ccg.andyt.grids.core.AbstractGrid2DSquareCell.ChunkID;
import uk.ac.leeds.ccg.andyt.grids.utilities.FileCreator;
/**
 * A factory for constructing Grid2DSquareCellDoubleChunkRAF instances.
 */
public class Grid2DSquareCellDoubleChunkRAFFactory
        extends AbstractGrid2DSquareCellDoubleChunkFactory {
    
    /**
     * Creates a new Grid2DSquareCellDoubleChunkMapFactory.
     */
    public Grid2DSquareCellDoubleChunkRAFFactory(){}
    
    @Override
    public AbstractGrid2DSquareCellDoubleChunk createGrid2DSquareCellDoubleChunk() {
        return new Grid2DSquareCellDoubleChunkRAF();
    }
    
    @Override
    public AbstractGrid2DSquareCellDoubleChunk createGrid2DSquareCellDoubleChunk(
            Grid2DSquareCellDouble grid2DSquareCellDouble,
            ChunkID chunkID) {
        File file = FileCreator.createNewFile(
                grid2DSquareCellDouble._Directory,
                "Chunk",
                ".raf" );
        return new Grid2DSquareCellDoubleChunkRAF(
                file,
                grid2DSquareCellDouble,
                chunkID );
    }
    
    @Override
    public AbstractGrid2DSquareCellDoubleChunk createGrid2DSquareCellDoubleChunk(
            AbstractGrid2DSquareCellDoubleChunk grid2DSquareCellDoubleChunk,
            ChunkID chunkID ) {
        File file = FileCreator.createNewFile(
                grid2DSquareCellDoubleChunk.getGrid2DSquareCellDouble()._Directory,
                "Chunk",
                ".raf" );
        return new Grid2DSquareCellDoubleChunkRAF(
                file,
                grid2DSquareCellDoubleChunk,
                chunkID );
    }
    
}
