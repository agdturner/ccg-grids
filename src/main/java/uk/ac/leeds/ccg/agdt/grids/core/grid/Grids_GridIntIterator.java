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
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_AbstractGridChunkRowMajorOrderIterator;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_GridChunkInt;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_GridChunkIntArray;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_GridChunkIntArrayOrMapIterator;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_GridChunkIntIterator;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_GridChunkIntMap;

/**
 * For iterating through the values in a Grids_GridInt. The values are returned
 * chunk by chunk in row major order. The values within each chunk are also
 * returned in row major order.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridIntIterator extends Grids_AbstractGridIterator {

    private static final long serialVersionUID = 1L;

    protected Grids_GridIntIterator() {
    }

    /**
     *
     * @param g The Grids_GridInt to iterate over.
     */
    public Grids_GridIntIterator(Grids_GridInt g) throws IOException,
            ClassNotFoundException {
        super(g);
        GridIterator = g.chunkIDChunkMap.keySet().iterator();
        if (GridIterator.hasNext()) {
            ChunkID = GridIterator.next();
            Chunk = (Grids_AbstractGridChunkInt) g.chunkIDChunkMap.get(ChunkID);
            if (Chunk == null) {
                Grid.loadIntoCacheChunk(ChunkID);
                Chunk = (Grids_AbstractGridChunkInt) g.chunkIDChunkMap.get(ChunkID);
            }
            initChunkIterator();
        }
    }

    /**
     * Initialises ChunkIterator.
     */
    @Override
    protected final void initChunkIterator() {
        if (Chunk instanceof Grids_GridChunkIntArray) {
            ChunkIterator = new Grids_GridChunkIntArrayOrMapIterator(
                    (Grids_GridChunkIntArray) Chunk);
        } else if (Chunk instanceof Grids_GridChunkIntMap) {
            ChunkIterator = new Grids_GridChunkIntArrayOrMapIterator(
                    (Grids_GridChunkIntMap) Chunk);
        } else {
            ChunkIterator = new Grids_GridChunkIntIterator(
                    (Grids_GridChunkInt) Chunk);
        }
    }

    /**
     * @param chunk
     * @return Grids_AbstractIterator to iterate over values in chunk.
     */
    @Override
    public Grids_AbstractGridChunkRowMajorOrderIterator getChunkIterator(
            Grids_AbstractGridChunk chunk) {
        if (chunk instanceof Grids_GridChunkIntArray) {
            return new Grids_GridChunkIntArrayOrMapIterator(
                    (Grids_GridChunkIntArray) chunk);
        } else if (chunk instanceof Grids_GridChunkIntMap) {
            return new Grids_GridChunkIntArrayOrMapIterator(
                    (Grids_GridChunkIntMap) chunk);
        } else if (chunk instanceof Grids_GridChunkInt) {
            return new Grids_GridChunkIntIterator(
                    (Grids_GridChunkInt) chunk);
        } else {
            throw new Error("Unrecognised type of chunk "
                    + this.getClass().getName()
                    + ".getChunkIterator(Chunk(" + chunk.toString() + "))");
        }
    }

    @Override
    public Grids_GridInt getGrid() {
        return (Grids_GridInt) Grid;
    }

    /**
     * @return The next value iterating over the entire grid chunk by chunk. If
     * there is no such value, then {@code null} is returned.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If there is a problem 
     */
    public Integer next() throws IOException, ClassNotFoundException {
        if (!ChunkIterator.hasNext()) {
            if (GridIterator.hasNext()) {
                ChunkID = GridIterator.next();
                Chunk = Grid.getChunk(ChunkID);
                ChunkIterator = getChunkIterator(Chunk);
                Integer r = ((Grids_GridChunkIntIterator) ChunkIterator).next();
                env.checkAndMaybeFreeMemory(ChunkID, env.HOOMET);
                return r;
            } else {
                return null;
            }
        } else {
            Integer r = ((Grids_GridChunkIntIterator) ChunkIterator).next();
            env.checkAndMaybeFreeMemory(ChunkID, env.HOOMET);
            return r;
        }
    }

}
