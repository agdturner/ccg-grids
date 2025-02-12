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
package uk.ac.leeds.ccg.grids.d2.chunk.br;

import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkFactory;
import uk.ac.leeds.ccg.grids.d2.grid.br.Grids_GridBR;

/**
 * For factories that return BigDecimal type chunks.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_ChunkBRFactory extends Grids_ChunkFactory {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance.
     */
    protected Grids_ChunkBRFactory(){}

    /**
     * Creates a chunk containing no data values. The chunk is put (as a value)
     * with {@code i} (as the key) into {@code g.chunkIDChunkMap}.
     *
     * @param g The grid.
     * @param i The chunk ID.
     * @return A chunk.
     */
    public abstract Grids_ChunkBR create(Grids_GridBR g, Grids_2D_ID_int i);

    /**
     * Creates a chunk with values taken from {@code chunk}. The chunk is put
     * (as a value) with {@code i} (as the key) into {@code g.chunkIDChunkMap}.
     *
     * @param c The chunk to get values from.
     * @param i The ID of the chunk to create.
     * @return A chunk.
     */
    public abstract Grids_ChunkBR create(Grids_ChunkBR c, Grids_2D_ID_int i);

}
