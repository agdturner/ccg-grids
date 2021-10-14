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
package uk.ac.leeds.ccg.grids.d2.grid.d;

import java.io.IOException;
import java.util.TreeMap;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDoubleIteratorArrayOrMap;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDoubleMap;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDoubleArray;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkRowMajorOrderIterator;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDoubleSinglet;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDoubleIteratorSinglet;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridIterator;

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

    /**
     * @param g The grid to iterate over.
     * @throws Exception If encountered.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    public Grids_GridDoubleIterator(Grids_GridDouble g) throws IOException,
            ClassNotFoundException, Exception {
        super(g);
        TreeMap<Grids_2D_ID_int, Grids_Chunk> m = g.getData();
        gridIterator = m.keySet().iterator();
        if (gridIterator.hasNext()) {
            chunkID = gridIterator.next();
            chunk = m.get(chunkID);
            if (chunk == null) {
                grid.loadChunk(chunkID);
                chunk = m.get(chunkID);
            }
            initChunkIterator();
        }
    }

    /**
     * Initialises chunkIterator.
     */
    @Override
    protected final void initChunkIterator() {
        if (chunk instanceof Grids_ChunkDoubleArray) {
            chunkIterator = new Grids_ChunkDoubleIteratorArrayOrMap(
                    (Grids_ChunkDoubleArray) chunk);
        } else if (chunk instanceof Grids_ChunkDoubleMap) {
            chunkIterator = new Grids_ChunkDoubleIteratorArrayOrMap(
                    (Grids_ChunkDoubleMap) chunk);
        } else {
            chunkIterator = new Grids_ChunkDoubleIteratorSinglet(
                    (Grids_ChunkDoubleSinglet) chunk);
        }
    }

    /**
     * @param c The chunk for which the iterator is returned.
     * @return A {@link Grids_ChunkRowMajorOrderIterator} to iterate over values
     * in {@code c}.
     * @throws java.lang.Exception If encountered.
     */
    @Override
    public Grids_ChunkRowMajorOrderIterator getChunkIterator(Grids_Chunk c)
            throws Exception {
        if (c instanceof Grids_ChunkDoubleArray) {
            return new Grids_ChunkDoubleIteratorArrayOrMap(
                    (Grids_ChunkDoubleArray) c);
        } else if (c instanceof Grids_ChunkDoubleMap) {
            return new Grids_ChunkDoubleIteratorArrayOrMap(
                    (Grids_ChunkDoubleMap) c);
        } else if (c instanceof Grids_ChunkDoubleSinglet) {
            return new Grids_ChunkDoubleIteratorSinglet(
                    (Grids_ChunkDoubleSinglet) c);
        } else {
            throw new Exception("Unrecognised type of chunk "
                    + this.getClass().getName()
                    + ".getChunkIterator(Chunk(" + c.toString() + "))");
        }
    }

    @Override
    public Grids_GridDouble getGrid() {
        return (Grids_GridDouble) grid;
    }

    /**
     * @return The next value iterating over the entire grid chunk by chunk. If
     * there is no such value, then {@code null} is returned.
     * @throws java.lang.Exception If encountered.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If there is a problem
     */
    public Double next() throws IOException, ClassNotFoundException, Exception {
        if (chunkIterator.hasNext()) {
            return next0();
        } else {
            if (gridIterator.hasNext()) {
                chunkID = gridIterator.next();
                chunk = grid.getChunk(chunkID);
                chunkIterator = getChunkIterator(chunk);
                env.checkAndMaybeFreeMemory(chunkID, env.HOOMET);
                return next0();
            } else {
                return null;
            }
        }
    }

    private Double next0() throws IOException, ClassNotFoundException, Exception {
        if (chunk instanceof Grids_ChunkDoubleSinglet) {
            return ((Grids_ChunkDoubleSinglet) chunk).getV();
        } else {
            return getChunkIterator().next();
        }
    }

    @Override
    public Grids_ChunkDoubleIteratorArrayOrMap getChunkIterator() {
        return (Grids_ChunkDoubleIteratorArrayOrMap) chunkIterator;
    }
}
