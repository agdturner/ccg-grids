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
package uk.ac.leeds.ccg.grids.d2.chunk.i;

import java.util.Iterator;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkRowMajorOrderIterator;

/**
 * For iterating through the values in a Grids_ChunkIntSinglet.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIteratorIntSinglet
        extends Grids_ChunkRowMajorOrderIterator implements Iterator<Integer> {

    private static final long serialVersionUID = 1L;

    protected int v;

    /**
     * Creates a new instance of Grids_GridChunkIntIterator
     *
     * @param c The chunk to iterate over.
     */
    public Grids_ChunkIteratorIntSinglet(Grids_ChunkIntSinglet c) {
        super(c);
        v = c.getValue();
    }

    /**
     * @return the next element in the iteration.
     */
    @Override
    public Integer next() {
        next0();
        return v;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
