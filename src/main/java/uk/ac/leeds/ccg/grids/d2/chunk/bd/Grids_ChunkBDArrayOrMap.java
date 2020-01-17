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
package uk.ac.leeds.ccg.grids.d2.chunk.bd;

import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.grid.bd.Grids_GridBD;

/**
 * A simple wrapper for
 * {@link uk.ac.leeds.ccg.grids.d2.chunk.bd.Grids_ChunkBDArray} and
 * {@link uk.ac.leeds.ccg.grids.d2.chunk.bd.Grids_ChunkBDMap}.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_ChunkBDArrayOrMap extends Grids_ChunkBD {

    private static final long serialVersionUID = 1L;

    /**
     * {@link #worthClearing} is set to {@code true}.
     * @param g What {@link #grid} is set to.
     * @param i What {@link #id} is set to.
     */
    protected Grids_ChunkBDArrayOrMap(Grids_GridBD g, Grids_2D_ID_int i) {
        super(g, i, true);
    }

}
