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

import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.d2.chunk.Grids_ChunkFactory;
import io.github.agdturner.grids.d2.grid.i.Grids_GridInt;

/**
 * Abstract Class for defining (an interface for) chunk factory methods. These
 * methods generally would work as protected, but are tested externally and so
 * are declared public. Really no user should have a chunk without a grid even
 * if the grid contains only one chunk.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_ChunkFactoryInt
        extends Grids_ChunkFactory {

    /**
     * Creates a new Grids_ChunkInt containing all
 noDataValues that is linked to g via chunkID.
     *
     * @param g
     * @param chunkID
     * @return
     */
    public abstract Grids_ChunkInt create(
            Grids_GridInt g,
            Grids_2D_ID_int chunkID);

    /**
     * Creates a new Grids_ChunkInt with values taken from
 chunk.
     *
     * @param chunk
     * @param chunkID
     * @return
     */
    public abstract Grids_ChunkInt create(
            Grids_ChunkInt chunk,
            Grids_2D_ID_int chunkID);

}
