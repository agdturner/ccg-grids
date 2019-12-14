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
package uk.ac.leeds.ccg.agdt.grids.core.grid.b;

import java.io.IOException;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridIterator;
import uk.ac.leeds.ccg.agdt.grids.core.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.agdt.grids.core.chunk.b.Grids_ChunkBoolean;
import uk.ac.leeds.ccg.agdt.grids.core.chunk.b.Grids_ChunkIteratorBoolean;

/**
 * For iterating through the values in a Grids_GridBinary. The values are
 * returned chunk by chunk in row major order. The values within each chunk are
 * also returned in row major order.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridIteratorBoolean extends Grids_GridIterator {

    private static final long serialVersionUID = 1L;

    protected Grids_GridIteratorBoolean() {
    }

    /**
     * @param g The Grids_GridBoolean to iterate over.
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public Grids_GridIteratorBoolean(
            Grids_GridBoolean g) throws IOException, ClassNotFoundException {
        super(g);
        GridIterator = g.getChunkIDChunkMap().keySet().iterator();
        if (GridIterator.hasNext()) {
            ChunkID = GridIterator.next();
            Chunk = (Grids_ChunkBoolean) g.getChunkIDChunkMap().get(ChunkID);
            if (Chunk == null) {
                Grid.loadIntoCacheChunk(ChunkID);
                Chunk = (Grids_ChunkBoolean) g.getChunkIDChunkMap().get(ChunkID);
            }
            initChunkIterator();
        }
    }

    /**
     * Initialises ChunkIterator.
     */
    @Override
    protected final void initChunkIterator() {
        if (Chunk instanceof Grids_ChunkBoolean) {
            ChunkIterator = new Grids_ChunkIteratorBoolean(
                    (Grids_ChunkBoolean) Chunk);
        }
    }

    /**
     * @param chunk
     * @return Grids_AbstractIterator to iterate over values in chunk.
     */
    @Override
    public Grids_ChunkIteratorBoolean getChunkIterator(
            Grids_Chunk chunk) {
        if (chunk instanceof Grids_ChunkBoolean) {
            return new Grids_ChunkIteratorBoolean(
                    (Grids_ChunkBoolean) chunk);
        } else {
            throw new Error("Unrecognised type of chunk "
                        + this.getClass().getName()
                        + ".getChunkIterator(Chunk(" + chunk.toString() + "))");
        }
    }

    @Override
    public Grids_GridBoolean getGrid() {
        return (Grids_GridBoolean) Grid;
    }
    
    /**
     * @return The next value iterating over the entire grid chunk by chunk. If
     * there is no such value, then {@code null} is returned.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If there is a problem 
     */
    public Boolean next() throws IOException, ClassNotFoundException {
        if (!ChunkIterator.hasNext()) {
            if (GridIterator.hasNext()) {
                ChunkID = GridIterator.next();
                Chunk = Grid.getChunk(ChunkID);
                ChunkIterator = getChunkIterator(Chunk);
                Boolean r = ((Grids_ChunkIteratorBoolean) ChunkIterator).next();
                env.checkAndMaybeFreeMemory(ChunkID, env.HOOMET);
                return r;
            } else {
                return null;
            }
        } else {
            Boolean r = ((Grids_ChunkIteratorBoolean) ChunkIterator).next();
            env.checkAndMaybeFreeMemory(ChunkID, env.HOOMET);
            return r;
        }
    }
}
