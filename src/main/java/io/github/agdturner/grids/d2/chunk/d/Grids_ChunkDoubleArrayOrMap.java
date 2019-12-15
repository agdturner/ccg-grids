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

import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.d2.grid.d.Grids_GridDouble;

/**
 *
*
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_ChunkDoubleArrayOrMap 
        extends Grids_ChunkDouble {

    private static final long serialVersionUID = 1L;
    
    protected Grids_ChunkDoubleArrayOrMap() {
    }

    protected Grids_ChunkDoubleArrayOrMap(Grids_GridDouble g, 
            Grids_2D_ID_int chunkID) {
        super(g, chunkID);
    }
    
}
