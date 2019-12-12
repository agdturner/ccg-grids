/*
 * Copyright 2019 Andy Turner, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.agdt.grids.core.grid;

import java.io.IOException;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_GridChunkBinary;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_GridChunkBinaryIterator;
import uk.ac.leeds.ccg.agdt.grids.utilities.Grids_AbstractIterator;

/**
 * For iterating through the values in a Grids_GridBinary. The values are
 * returned chunk by chunk in row major order. The values within each chunk are
 * also returned in row major order.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridBinaryIterator extends Grids_AbstractGridIterator {

    private static final long serialVersionUID = 1L;

    protected Grids_GridBinaryIterator() {
    }

    /**
     * @param g The Grids_GridBinary to iterate over.
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public Grids_GridBinaryIterator(
            Grids_GridBinary g) throws IOException, ClassNotFoundException {
        super(g);
        GridIterator = g.chunkIDChunkMap.keySet().iterator();
        if (GridIterator.hasNext()) {
            ChunkID = GridIterator.next();
            Chunk = (Grids_GridChunkBinary) g.chunkIDChunkMap.get(ChunkID);
            if (Chunk == null) {
                Grid.loadIntoCacheChunk(ChunkID);
                Chunk = (Grids_GridChunkBinary) g.chunkIDChunkMap.get(ChunkID);
            }
            initChunkIterator();
        }
    }

    /**
     * Initialises ChunkIterator.
     */
    @Override
    protected final void initChunkIterator() {
        if (Chunk instanceof Grids_GridChunkBinary) {
            ChunkIterator = new Grids_GridChunkBinaryIterator(
                    (Grids_GridChunkBinary) Chunk);
        }
    }

    /**
     * @param chunk
     * @return Grids_AbstractIterator to iterate over values in chunk.
     */
    @Override
    public Grids_AbstractIterator getChunkIterator(
            Grids_AbstractGridChunk chunk) {
        if (chunk instanceof Grids_GridChunkBinary) {
            return new Grids_GridChunkBinaryIterator(
                    (Grids_GridChunkBinary) chunk);
        } else {
            throw new Error("Unrecognised type of chunk "
                        + this.getClass().getName()
                        + ".getChunkIterator(Chunk(" + chunk.toString() + "))");
        }
    }

    @Override
    public Grids_GridBinary getGrid() {
        return (Grids_GridBinary) Grid;
    }
}
