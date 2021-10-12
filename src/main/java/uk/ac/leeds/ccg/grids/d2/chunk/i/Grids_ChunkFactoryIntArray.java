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

import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridInt;

/**
 * A factory for constructing {@link Grids_ChunkIntArray} instances.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkFactoryIntArray extends Grids_ChunkFactoryInt {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance.
     */
    public Grids_ChunkFactoryIntArray() {
    }

    @Override
    public Grids_ChunkIntArray create(Grids_GridInt g, Grids_2D_ID_int i) {
        return new Grids_ChunkIntArray(g, i);
    }

    @Override
    public Grids_ChunkIntArray create(Grids_ChunkInt chunk, Grids_2D_ID_int i) {
        return new Grids_ChunkIntArray(chunk, i);
    }

}
