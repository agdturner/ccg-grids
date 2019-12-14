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
package uk.ac.leeds.ccg.agdt.grids.core.chunk.i;

import uk.ac.leeds.ccg.agdt.grids.core.chunk.i.Grids_ChunkInt;
import uk.ac.leeds.ccg.agdt.grids.core.chunk.i.Grids_ChunkFactoryInt;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.agdt.grids.core.grid.i.Grids_GridInt;

/**
 * A factory for constructing Grid2DSquareCellIntChunkArray instances.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkFactoryIntSinglet
        extends Grids_ChunkFactoryInt {

    int DefaultValue;

    /**
     * Creates a new Grids_GridChunkIntFactory.
     */
    protected Grids_ChunkFactoryIntSinglet() {
    }

    public Grids_ChunkFactoryIntSinglet(int defaultValue) {
        DefaultValue = defaultValue;
    }

    @Override
    public Grids_ChunkInt create(
            Grids_GridInt g,
            Grids_2D_ID_int chunkID) {
        return new Grids_ChunkIntSinglet(g, chunkID, DefaultValue);
    }

    @Override
    public Grids_ChunkInt create(
            Grids_ChunkInt chunk,
            Grids_2D_ID_int chunkID) {
        return new Grids_ChunkIntSinglet(chunk.getGrid(), chunkID, DefaultValue);
    }

}
