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
package uk.ac.leeds.ccg.grids.d2.chunk.d;

import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;

/**
 * A factory for constructing Grids_ChunkDouble instances.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkDoubleFactorySinglet extends Grids_ChunkDoubleFactory {

    private static final long serialVersionUID = 1L;

    /**
     * defaultValue
     */
    double defaultValue;

    /**
     * Creates a new Grids_ChunkFactoryDoubleSinglet.
     */
    protected Grids_ChunkDoubleFactorySinglet() {
    }

    /**
     * Creates a new Grids_ChunkFactoryDoubleSinglet.
     *
     * @param dv What {@link #defaultValue} is set to.
     */
    public Grids_ChunkDoubleFactorySinglet(double dv) {
        defaultValue = dv;
    }

    @Override
    public Grids_ChunkDoubleSinglet create(Grids_GridDouble g, Grids_2D_ID_int i) {
        return new Grids_ChunkDoubleSinglet(g, i, defaultValue);
    }

    @Override
    public Grids_ChunkDoubleSinglet create(Grids_ChunkDouble c, Grids_2D_ID_int i) {
        return new Grids_ChunkDoubleSinglet(c.getGrid(), i, defaultValue);
    }

}
