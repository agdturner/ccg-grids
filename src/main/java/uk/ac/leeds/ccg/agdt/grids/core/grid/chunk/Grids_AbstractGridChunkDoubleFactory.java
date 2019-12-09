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
package uk.ac.leeds.ccg.agdt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridDouble;

/**
 * Abstract Class for defining (an interface for) chunk factory methods. These
 * methods generally would work as protected, but are tested externally and so
 * are declared public. Really no user should have a chunk without a grid even
 * if the grid contains only one chunk.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_AbstractGridChunkDoubleFactory
        extends Grids_AbstractGridChunkFactory {

    /**
     * For creating a new Grids_AbstractGridChunkDouble containing all
     * noDataValues that is linked to g via chunkID.
     *
     * @param g
     * @param chunkID
     * @return
     */
    public abstract Grids_AbstractGridChunkDouble create(
            Grids_GridDouble g,
            Grids_2D_ID_int chunkID);

    /**
     * Creates a new Grids_AbstractGridChunkDouble with values taken from chunk.
     *
     * @param chunk
     * @param chunkID
     * @return
     */
    public abstract Grids_AbstractGridChunkDouble create(
            Grids_AbstractGridChunkDouble chunk,
            Grids_2D_ID_int chunkID);

}
