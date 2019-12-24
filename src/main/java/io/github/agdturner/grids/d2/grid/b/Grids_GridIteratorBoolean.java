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
import io.github.agdturner.grids.d2.chunk.b.Grids_ChunkBooleanArray;
import io.github.agdturner.grids.d2.chunk.b.Grids_ChunkIteratorBooleanArray;

/**
 * For iterating through the values in a {@link Grids_GridBoolean}. The values are
 * returned chunk by chunk in row major order. The values within each chunk are
 * also returned in row major order.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridIteratorBoolean extends Grids_GridIterator {

    private static final long serialVersionUID = 1L;

    /**
     * @param g The Grids_GridBoolean to iterate over.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridIteratorBoolean(Grids_GridBoolean g) throws IOException, 
            ClassNotFoundException, Exception {
        super(g);
        GridIterator = g.getData().keySet().iterator();
        if (GridIterator.hasNext()) {
            ChunkID = GridIterator.next();
            Chunk = (Grids_ChunkBooleanArray) g.getData()
                    .get(ChunkID);
            if (Chunk == null) {
                Grid.loadChunk(ChunkID);
                Chunk = (Grids_ChunkBooleanArray) g.getData()
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
        if (Chunk instanceof Grids_ChunkBooleanArray) {
            ChunkIterator = new Grids_ChunkIteratorBooleanArray(
                    (Grids_ChunkBooleanArray) Chunk);
        }
    }

    /**
     * @param chunk
     * @return Grids_AbstractIterator to iterate over values in chunk.
     */
    @Override
    public Grids_ChunkIteratorBooleanArray getChunkIterator(Grids_Chunk chunk) {
        if (chunk instanceof Grids_ChunkBooleanArray) {
            return new Grids_ChunkIteratorBooleanArray(
                    (Grids_ChunkBooleanArray) chunk);
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
    
    public Grids_ChunkIteratorBooleanArray getChunkIterator(Grids_2D_ID_int i) 
            throws IOException, ClassNotFoundException, Exception {
        return getChunkIterator(getGrid().getChunk(i));
    }
    
    /**
     * @return The next value iterating over the entire grid chunk by chunk. If
     * there is no such value, then {@code null} is returned.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If there is a problem 
     */
    public Boolean next() throws IOException, ClassNotFoundException, Exception {
        if (!ChunkIterator.hasNext()) {
            if (GridIterator.hasNext()) {
                ChunkID = GridIterator.next();
                ChunkIterator = getChunkIterator(ChunkID);
                return next0();
            } else {
                throw new NoSuchElementException();
            }
        } else {
            return next0();
        }
    }

    private Boolean next0() throws IOException, Exception {
        boolean r = getChunkIterator().next();
        env.checkAndMaybeFreeMemory(ChunkID, env.HOOMET);
        return r;
    }
    
    @Override
    public Grids_ChunkIteratorBooleanArray getChunkIterator() {
        return (Grids_ChunkIteratorBooleanArray) ChunkIterator;
    }
}
