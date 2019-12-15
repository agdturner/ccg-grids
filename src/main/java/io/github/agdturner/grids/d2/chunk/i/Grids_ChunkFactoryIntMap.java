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
package io.github.agdturner.grids.d2.chunk.i;

import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkFactoryInt;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkInt;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.d2.grid.i.Grids_GridInt;

/**
 * A factory for constructing Grids_ChunkIntMap instances.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkFactoryIntMap
        extends Grids_ChunkFactoryInt {

    public Grids_ChunkFactoryIntMap() {
    }

    @Override
    public Grids_ChunkIntMap create(
            Grids_GridInt g,
            Grids_2D_ID_int chunkID) {
        return new Grids_ChunkIntMap(g, chunkID);
    }

    @Override
    public Grids_ChunkIntMap create(
            Grids_ChunkInt chunk,
            Grids_2D_ID_int chunkID) {
        return new Grids_ChunkIntMap(chunk, chunkID,
                chunk.getGrid().getNoDataValue());
    }

    public Grids_ChunkIntMap create(
            Grids_ChunkInt chunk,
            Grids_2D_ID_int chunkID,
            int defaultValue) {
        return new Grids_ChunkIntMap(chunk, chunkID, defaultValue);
    }

}
