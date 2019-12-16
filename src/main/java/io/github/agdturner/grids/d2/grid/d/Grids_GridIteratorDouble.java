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
package io.github.agdturner.grids.d2.grid.d;

import java.io.IOException;
import java.util.TreeMap;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.d2.chunk.Grids_Chunk;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkIteratorDoubleArrayOrMap;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkDoubleMap;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkDoubleArray;
import io.github.agdturner.grids.d2.chunk.Grids_ChunkRowMajorOrderIterator;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkDoubleSinglet;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkIteratorDouble;
import io.github.agdturner.grids.d2.grid.Grids_GridIterator;

/**
 * For iterating through the values in a Grids_GridDouble. The values are
 * returned chunk by chunk in row major order. The values within each chunk are
 * also returned in row major order.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridIteratorDouble extends Grids_GridIterator {

    private static final long serialVersionUID = 1L;

    protected Grids_GridIteratorDouble() {
    }

    /**
     * @param g The grid to iterate over.
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public Grids_GridIteratorDouble(Grids_GridDouble g) throws IOException, 
            ClassNotFoundException, Exception {
        super(g);
        TreeMap<Grids_2D_ID_int, Grids_Chunk> m = g.getChunkIDChunkMap();
        GridIterator = m.keySet().iterator();
        if (GridIterator.hasNext()) {
            ChunkID = GridIterator.next();
            Chunk = m.get(ChunkID);
            if (Chunk == null) {
                Grid.loadIntoCacheChunk(ChunkID);
                Chunk = m.get(ChunkID);
            }
            initChunkIterator();
        }
    }

    /**
     * Initialises ChunkIterator.
     */
    @Override
    protected final void initChunkIterator() {
        if (Chunk instanceof Grids_ChunkDoubleArray) {
            ChunkIterator = new Grids_ChunkIteratorDoubleArrayOrMap(
                    (Grids_ChunkDoubleArray) Chunk);
        } else if (Chunk instanceof Grids_ChunkDoubleMap) {
            ChunkIterator = new Grids_ChunkIteratorDoubleArrayOrMap(
                    (Grids_ChunkDoubleMap) Chunk);
        } else {
            ChunkIterator = new Grids_ChunkIteratorDouble(
                    (Grids_ChunkDoubleSinglet) Chunk);
        }
    }

    /**
     * @param chunk
     * @return Grids_AbstractIterator to iterate over values in chunk.
     */
    @Override
    public Grids_ChunkRowMajorOrderIterator getChunkIterator(
            Grids_Chunk chunk) {
        if (chunk instanceof Grids_ChunkDoubleArray) {
            return new Grids_ChunkIteratorDoubleArrayOrMap(
                    (Grids_ChunkDoubleArray) chunk);
        } else if (chunk instanceof Grids_ChunkDoubleMap) {
            return new Grids_ChunkIteratorDoubleArrayOrMap(
                    (Grids_ChunkDoubleMap) chunk);
        } else if (chunk instanceof Grids_ChunkDoubleSinglet) {
            return new Grids_ChunkIteratorDouble( 
                    (Grids_ChunkDoubleSinglet) chunk);
        } else {
            throw new Error("Unrecognised type of chunk "
                        + this.getClass().getName()
                        + ".getChunkIterator(Chunk(" + chunk.toString() + "))");
        }
    }

    @Override
    public Grids_GridDouble getGrid() {
        return (Grids_GridDouble) Grid;
    }
    
    /**
     * @return The next value iterating over the entire grid chunk by chunk. If
     * there is no such value, then {@code null} is returned.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If there is a problem 
     */
    public Double next() throws IOException, ClassNotFoundException, Exception {
        if (!ChunkIterator.hasNext()) {
            if (GridIterator.hasNext()) {
                ChunkID = GridIterator.next();
                Chunk = Grid.getChunk(ChunkID);
                ChunkIterator = getChunkIterator(Chunk);
                env.checkAndMaybeFreeMemory(ChunkID, env.HOOMET);
                return getChunkIterator().next();
            } else {
                return null;
            }
        } else {
            return getChunkIterator().next();
        }
    }

    
    @Override
    public Grids_ChunkIteratorDoubleArrayOrMap getChunkIterator() {
        return (Grids_ChunkIteratorDoubleArrayOrMap) ChunkIterator;
    }
}
