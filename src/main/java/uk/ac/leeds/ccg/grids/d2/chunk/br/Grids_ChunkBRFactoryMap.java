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

import ch.obermuhlner.math.big.BigRational;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.grid.br.Grids_GridBR;

/**
 * A factory for constructing {@link Grids_ChunkBRMap} instances.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkBRFactoryMap extends Grids_ChunkBRFactory {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance.
     */
    public Grids_ChunkBRFactoryMap() {
    }

    @Override
    public Grids_ChunkBRMap create(Grids_GridBR g, Grids_2D_ID_int i) {
        return new Grids_ChunkBRMap(g, i);
    }

    @Override
    public Grids_ChunkBRMap create(Grids_ChunkBR c, Grids_2D_ID_int i) {
        return new Grids_ChunkBRMap(c, i, c.getGrid().getNoDataValue());
    }

    /**
     * Creates a chunk with values taken from {@code chunk}. The chunk is put
     * (as a value) with {@code i} (as the key) into {@code g.chunkIDChunkMap}.
     *
     * @param c The chunk to get values from.
     * @param i The ID of the chunk to create.
     * @param dv The default value.
     * @return A chunk.
     */
    public Grids_ChunkBRMap create(Grids_ChunkBR c, Grids_2D_ID_int i,
            BigRational dv) {
        return new Grids_ChunkBRMap(c, i, dv);
    }

}
