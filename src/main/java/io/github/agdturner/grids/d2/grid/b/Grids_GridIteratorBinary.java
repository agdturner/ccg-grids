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
package io.github.agdturner.grids.d2.grid.b;

import java.io.IOException;
import java.util.NoSuchElementException;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.d2.grid.Grids_GridIterator;
import io.github.agdturner.grids.d2.chunk.Grids_Chunk;
import io.github.agdturner.grids.d2.chunk.b.Grids_ChunkBinaryArray;
import io.github.agdturner.grids.d2.chunk.b.Grids_ChunkIteratorBinaryArray;

/**
 * For iterating through the values in a {@link Grids_GridBinary}. The values
 * are returned chunk by chunk in row major order. The values within each chunk
 * are also returned in row major order.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridIteratorBinary extends Grids_GridIterator {

    private static final long serialVersionUID = 1L;

    /**
     * @param g The Grids_GridBinary to iterate over.
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public Grids_GridIteratorBinary(Grids_GridBinary g) throws IOException, 
            ClassNotFoundException, Exception {
        super(g);
        GridIterator = g.getData().keySet().iterator();
        if (GridIterator.hasNext()) {
            ChunkID = GridIterator.next();
            Chunk = (Grids_ChunkBinaryArray) g.getData()
                    .get(ChunkID);
            if (Chunk == null) {
                Grid.loadIntoCacheChunk(ChunkID);
                Chunk = (Grids_ChunkBinaryArray) g.getData()
                        .get(ChunkID);
            }
            initChunkIterator();
        }
    }

    /**
     * Initialises ChunkIterator.
     */
    @Override
    protected final void initChunkIterator() {
        if (Chunk instanceof Grids_ChunkBinaryArray) {
            ChunkIterator = new Grids_ChunkIteratorBinaryArray(
                    (Grids_ChunkBinaryArray) Chunk);
        }
    }

    /**
     * @param chunk The chunk for which an iterator is returned.
     * @return {@link Grids_ChunkIteratorBinaryArray} to iterate over values in chunk.
     */
    @Override
    public Grids_ChunkIteratorBinaryArray getChunkIterator(Grids_Chunk chunk) {
        if (chunk instanceof Grids_ChunkBinaryArray) {
            return new Grids_ChunkIteratorBinaryArray(
                    (Grids_ChunkBinaryArray) chunk);
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

    public Grids_ChunkIteratorBinaryArray getChunkIterator(Grids_2D_ID_int i)
            throws IOException, ClassNotFoundException, Exception {
        return getChunkIterator(getGrid().getChunk(i));
    }

    /**
     * @return The next value iterating over the entire grid chunk by chunk. If
     * there is no such value, then {@code null} is returned.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If there is a problem
     */
    public boolean next() throws IOException, ClassNotFoundException, Exception {
        if (!ChunkIterator.hasNext()) {
            if (GridIterator.hasNext()) {
                ChunkID = GridIterator.next();
                Chunk = Grid.getChunk(ChunkID);
                ChunkIterator = getChunkIterator(Chunk);
                env.checkAndMaybeFreeMemory(ChunkID, env.HOOMET);
                return getChunkIterator().next();
            } else {
                throw new NoSuchElementException();
            }
        } else {
            return getChunkIterator().next();
        }
    }

    @Override
    public Grids_ChunkIteratorBinaryArray getChunkIterator() {
        return (Grids_ChunkIteratorBinaryArray) ChunkIterator;
    }
}
