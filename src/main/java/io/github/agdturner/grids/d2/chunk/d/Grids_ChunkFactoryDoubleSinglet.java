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
 * A factory for constructing Grids_ChunkDouble instances.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkFactoryDoubleSinglet extends Grids_ChunkFactoryDouble {

    private static final long serialVersionUID = 1L;

    double DefaultValue;

    /**
     * Creates a new Grids_ChunkFactoryDoubleSinglet.
     */
    protected Grids_ChunkFactoryDoubleSinglet() {
    }

    /**
     * Creates a new Grids_ChunkFactoryDoubleSinglet.
     *
     * @param dv What {@link #DefaultValue} is set to.
     */
    public Grids_ChunkFactoryDoubleSinglet(double dv) {
        DefaultValue = dv;
    }

    @Override
    public Grids_ChunkDoubleSinglet create(Grids_GridDouble g, Grids_2D_ID_int i) {
        return new Grids_ChunkDoubleSinglet(g, i, DefaultValue);
    }

    @Override
    public Grids_ChunkDoubleSinglet create(Grids_ChunkDouble c, Grids_2D_ID_int i) {
        return new Grids_ChunkDoubleSinglet(c.getGrid(), i, DefaultValue);
    }

}
