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
package uk.ac.leeds.ccg.grids.d2.grid.i;

import java.io.IOException;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridIterator;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkRowMajorOrderIterator;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkInt;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntSinglet;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntArray;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIteratorIntArrayOrMap;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIteratorIntSinglet;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntMap;

/**
 * For iterating through the values in a Grids_GridInt. The values are returned
 * chunk by chunk in row major order. The values within each chunk are also
 * returned in row major order.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridIteratorInt extends Grids_GridIterator {

    private static final long serialVersionUID = 1L;

    /**
     * @param g The {@link Grids_GridInt} to iterate over.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridIteratorInt(Grids_GridInt g) throws IOException, Exception,
            ClassNotFoundException {
        super(g);
        gridIterator = g.getData().keySet().iterator();
        if (gridIterator.hasNext()) {
            chunkID = gridIterator.next();
            chunk = (Grids_ChunkInt) g.getData().get(chunkID);
            if (chunk == null) {
                grid.loadChunk(chunkID);
                chunk = (Grids_ChunkInt) g.getData().get(chunkID);
            }
            initChunkIterator();
        }
    }

    /**
     * Initialises chunkIterator.
     */
    @Override
    protected final void initChunkIterator() {
        if (chunk instanceof Grids_ChunkIntArray) {
            chunkIterator = new Grids_ChunkIteratorIntArrayOrMap(
                    (Grids_ChunkIntArray) chunk);
        } else if (chunk instanceof Grids_ChunkIntMap) {
            chunkIterator = new Grids_ChunkIteratorIntArrayOrMap(
                    (Grids_ChunkIntMap) chunk);
        } else {
            chunkIterator = new Grids_ChunkIteratorIntSinglet(
                    (Grids_ChunkIntSinglet) chunk);
        }
    }

    /**
     * @param gc A grid chunk for which a row major order iterator is returned.
     * @return Grids_AbstractIterator to iterate over values in chunk.
     */
    @Override
    public Grids_ChunkRowMajorOrderIterator getChunkIterator(Grids_Chunk gc) throws Exception {
        if (gc instanceof Grids_ChunkIntArray) {
            return new Grids_ChunkIteratorIntArrayOrMap(
                    (Grids_ChunkIntArray) gc);
        } else if (gc instanceof Grids_ChunkIntMap) {
            return new Grids_ChunkIteratorIntArrayOrMap(
                    (Grids_ChunkIntMap) gc);
        } else if (gc instanceof Grids_ChunkIntSinglet) {
            return new Grids_ChunkIteratorIntSinglet(
                    (Grids_ChunkIntSinglet) gc);
        } else {
            throw new Exception("Unrecognised type of chunk "
                    + this.getClass().getName()
                    + ".getChunkIterator(Chunk(" + gc.toString() + "))");
        }
    }

    @Override
    public Grids_GridInt getGrid() {
        return (Grids_GridInt) grid;
    }

    /**
     * @return The next value iterating over the entire grid chunk by chunk. If
     * there is no such value, then {@code null} is returned.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If there is a problem
     */
    public Integer next() throws IOException, Exception, 
            ClassNotFoundException {
        if (!chunkIterator.hasNext()) {
            if (gridIterator.hasNext()) {
                chunkID = gridIterator.next();
                chunk = grid.getChunk(chunkID);
                chunkIterator = getChunkIterator(chunk);
                return getChunkIterator().next();
            } else {
                return null;
            }
        } else {
            return getChunkIterator().next();
        }
    }

    @Override
    public Grids_ChunkIteratorIntArrayOrMap getChunkIterator() {
        return (Grids_ChunkIteratorIntArrayOrMap) chunkIterator;
    }
}
