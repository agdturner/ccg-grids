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
package io.github.agdturner.grids.d2.chunk.d;

import java.util.Iterator;
import io.github.agdturner.grids.d2.chunk.Grids_ChunkRowMajorOrderIterator;

/**
 * For iterating through the values in a Grids_ChunkDoubleSinglet.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIteratorDoubleSinglet
        extends Grids_ChunkRowMajorOrderIterator implements Iterator<Double> {

    private static final long serialVersionUID = 1L;

    protected double v;

    /**
     * Creates a new instance of Grids_GridChunkDoubleIterator
     *
     * @param c The chunk to iterate over.
     */
    public Grids_ChunkIteratorDoubleSinglet(Grids_ChunkDoubleSinglet c) {
        super(c);
        v = c.getV();
    }

    /**
     * @return the next element in the iteration.
     */
    @Override
    public Double next() {
        next0();
        return v;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
