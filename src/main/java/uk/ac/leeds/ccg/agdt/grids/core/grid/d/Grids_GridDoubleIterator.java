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
package uk.ac.leeds.ccg.agdt.grids.core.grid.d;

import java.io.IOException;
import java.util.TreeMap;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.agdt.grids.core.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.agdt.grids.core.chunk.d.Grids_ChunkIteratorDoubleArrayOrMap;
import uk.ac.leeds.ccg.agdt.grids.core.chunk.d.Grids_ChunkDoubleMap;
import uk.ac.leeds.ccg.agdt.grids.core.chunk.d.Grids_ChunkDoubleArray;
import uk.ac.leeds.ccg.agdt.grids.core.chunk.Grids_ChunkRowMajorOrderIterator;
import uk.ac.leeds.ccg.agdt.grids.core.chunk.d.Grids_ChunkDoubleSinglet;
import uk.ac.leeds.ccg.agdt.grids.core.chunk.d.Grids_ChunkIteratorDouble;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridIterator;

/**
 * For iterating through the values in a Grids_GridDouble. The values are
 * returned chunk by chunk in row major order. The values within each chunk are
 * also returned in row major order.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridDoubleIterator extends Grids_GridIterator {

    private static final long serialVersionUID = 1L;

    protected Grids_GridDoubleIterator() {
    }

    /**
     * @param g The grid to iterate over.
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public Grids_GridDoubleIterator(Grids_GridDouble g) throws IOException, 
            ClassNotFoundException {
        super(g);
        TreeMap<Grids_2D_ID_int, Grids_Chunk> m;
        m = g.getChunkIDChunkMap();
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
    public Double next() throws IOException, ClassNotFoundException {
        if (!ChunkIterator.hasNext()) {
            if (GridIterator.hasNext()) {
                ChunkID = GridIterator.next();
                Chunk = Grid.getChunk(ChunkID);
                ChunkIterator = getChunkIterator(Chunk);
                Double r = ((Grids_ChunkIteratorDouble) ChunkIterator).next();
                env.checkAndMaybeFreeMemory(ChunkID, env.HOOMET);
                return r;
            } else {
                return null;
            }
        } else {
            Double r = ((Grids_ChunkIteratorDouble) ChunkIterator).next();
            env.checkAndMaybeFreeMemory(ChunkID, env.HOOMET);
            return r;
        }
    }
}
