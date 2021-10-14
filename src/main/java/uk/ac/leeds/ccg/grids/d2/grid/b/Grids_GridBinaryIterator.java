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
package uk.ac.leeds.ccg.grids.d2.grid.b;

import java.io.IOException;
import java.util.NoSuchElementException;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridIterator;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.grids.d2.chunk.b.Grids_ChunkBinaryArray;
import uk.ac.leeds.ccg.grids.d2.chunk.b.Grids_ChunkBinaryIteratorArray;

/**
 * For iterating through the values in a {@link Grids_GridBinary}. The values
 * are returned chunk by chunk in row major order. The values within each chunk
 * are also returned in row major order.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridBinaryIterator extends Grids_GridIterator {

    private static final long serialVersionUID = 1L;

    /**
     * @param g The Grids_GridBinary to iterate over.
     * @throws Exception If encountered.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    public Grids_GridBinaryIterator(Grids_GridBinary g) throws IOException,
            ClassNotFoundException, Exception {
        super(g);
        gridIterator = g.getData().keySet().iterator();
        if (gridIterator.hasNext()) {
            chunkID = gridIterator.next();
            chunk = (Grids_ChunkBinaryArray) g.getData()
                    .get(chunkID);
            if (chunk == null) {
                grid.loadChunk(chunkID);
                chunk = (Grids_ChunkBinaryArray) g.getData()
                        .get(chunkID);
            }
            initChunkIterator();
        }
    }

    /**
     * Initialises chunkIterator.
     */
    @Override
    protected final void initChunkIterator() {
        if (chunk instanceof Grids_ChunkBinaryArray) {
            chunkIterator = new Grids_ChunkBinaryIteratorArray(
                    (Grids_ChunkBinaryArray) chunk);
        }
    }

    /**
     * @param chunk The chunk for which an iterator is returned.
     * @return {@link Grids_ChunkBinaryIteratorArray} to iterate over values in
     * chunk.
     */
    @Override
    public Grids_ChunkBinaryIteratorArray getChunkIterator(Grids_Chunk chunk) {
        if (chunk instanceof Grids_ChunkBinaryArray) {
            return new Grids_ChunkBinaryIteratorArray(
                    (Grids_ChunkBinaryArray) chunk);
        } else {
            throw new Error("Unrecognised type of chunk "
                    + this.getClass().getName()
                    + ".getChunkIterator(Chunk(" + chunk.toString() + "))");
        }
    }

    @Override
    public Grids_GridBinary getGrid() {
        return (Grids_GridBinary) grid;
    }

    /**
     * 
     * @param i ID
     * @return Grids_ChunkBinaryIteratorArray
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_ChunkBinaryIteratorArray getChunkIterator(Grids_2D_ID_int i)
            throws IOException, ClassNotFoundException, Exception {
        return getChunkIterator(getGrid().getChunk(i));
    }

    /**
     * @return The next value iterating over the entire grid chunk by chunk. If
     * there is no such value, then {@code null} is returned.
     * @throws Exception If encountered.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    public boolean next() throws IOException, ClassNotFoundException, Exception {
        if (!chunkIterator.hasNext()) {
            if (gridIterator.hasNext()) {
                chunkID = gridIterator.next();
                chunk = grid.getChunk(chunkID);
                chunkIterator = getChunkIterator(chunk);
                env.checkAndMaybeFreeMemory(chunkID, env.HOOMET);
                return getChunkIterator().next();
            } else {
                throw new NoSuchElementException();
            }
        } else {
            return getChunkIterator().next();
        }
    }

    @Override
    public Grids_ChunkBinaryIteratorArray getChunkIterator() {
        return (Grids_ChunkBinaryIteratorArray) chunkIterator;
    }
}
