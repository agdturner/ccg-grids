/*
 * Copyright 2025 Andy Turner, University of Leeds.
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
package uk.ac.leeds.ccg.grids.d2.grid.br;

import ch.obermuhlner.math.big.BigRational;
import java.io.IOException;
import java.util.TreeMap;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkRowMajorOrderIterator;
import uk.ac.leeds.ccg.grids.d2.chunk.br.Grids_ChunkBRIteratorArrayOrMap;
import uk.ac.leeds.ccg.grids.d2.chunk.br.Grids_ChunkBRMap;
import uk.ac.leeds.ccg.grids.d2.chunk.br.Grids_ChunkBRArray;
import uk.ac.leeds.ccg.grids.d2.chunk.br.Grids_ChunkBRSinglet;
import uk.ac.leeds.ccg.grids.d2.chunk.br.Grids_ChunkBRIteratorSinglet;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridIterator;

/**
 * For iterating through the values in a Grids_GridBR. The values are
 * returned chunk by chunk in row major order. The values within each chunk are
 * also returned in row major order.
 *
 * @author Andy Turner
 * @version 1.1
 */
public class Grids_GridBRIterator extends Grids_GridIterator {

    private static final long serialVersionUID = 1L;

    /**
     * @param g The grid to iterate over.
     * @throws Exception If encountered.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    public Grids_GridBRIterator(Grids_GridBR g) throws IOException,
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
        if (chunk instanceof Grids_ChunkBRArray) {
            chunkIterator = new Grids_ChunkBRIteratorArrayOrMap(
                    (Grids_ChunkBRArray) chunk);
        } else if (chunk instanceof Grids_ChunkBRMap) {
            chunkIterator = new Grids_ChunkBRIteratorArrayOrMap(
                    (Grids_ChunkBRMap) chunk);
        } else {
            chunkIterator = new Grids_ChunkBRIteratorSinglet(
                    (Grids_ChunkBRSinglet) chunk);
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
        if (c instanceof Grids_ChunkBRArray) {
            return new Grids_ChunkBRIteratorArrayOrMap(
                    (Grids_ChunkBRArray) c);
        } else if (c instanceof Grids_ChunkBRMap) {
            return new Grids_ChunkBRIteratorArrayOrMap(
                    (Grids_ChunkBRMap) c);
        } else if (c instanceof Grids_ChunkBRSinglet) {
            return new Grids_ChunkBRIteratorSinglet(
                    (Grids_ChunkBRSinglet) c);
        } else {
            throw new Exception("Unrecognised type of chunk "
                    + this.getClass().getName()
                    + ".getChunkIterator(Chunk(" + c.toString() + "))");
        }
    }

    @Override
    public Grids_GridBR getGrid() {
        return (Grids_GridBR) grid;
    }

    /**
     * @return The next value iterating over the entire grid chunk by chunk. If
     * there is no such value, then {@code null} is returned.
     * @throws java.lang.Exception If encountered.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If there is a problem
     */
    public BigRational next() throws IOException, ClassNotFoundException, Exception {
        if (!chunkIterator.hasNext()) {
            if (gridIterator.hasNext()) {
                chunkID = gridIterator.next();
                chunk = grid.getChunk(chunkID);
                chunkIterator = getChunkIterator(chunk);
                env.checkAndMaybeFreeMemory(chunkID, env.HOOMET);
                return getChunkIterator().next();
            } else {
                return null;
            }
        } else {
            return getChunkIterator().next();
        }
    }

    @Override
    public Grids_ChunkBRIteratorArrayOrMap getChunkIterator() {
        return (Grids_ChunkBRIteratorArrayOrMap) chunkIterator;
    }
}
