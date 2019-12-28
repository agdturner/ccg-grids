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
package io.github.agdturner.grids.d2.chunk.b;

/**
 * For iterating through the values in a Grids_GridChunkBinary instance. The
 * values are not returned in any particular order.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIteratorBooleanArray extends Grids_ChunkIteratorBArray {

    private static final long serialVersionUID = 1L;

    protected Boolean[][] data;

    public Grids_ChunkIteratorBooleanArray(Grids_ChunkBooleanArray chunk) {
        super(chunk);
        data = chunk.getData();
    }

    /**
     * @return the next element in the iteration.
     */
    public Boolean next() {
        next0();
        return data[row][col];
    }
}
