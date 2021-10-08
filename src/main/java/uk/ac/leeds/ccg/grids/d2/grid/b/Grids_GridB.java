/*
 * Copyright 2020 Andy Turner, University of Leeds.
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
package uk.ac.leeds.ccg.grids.d2.grid.b;

import java.math.BigDecimal;
import uk.ac.leeds.ccg.io.IO_Cache;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;

/**
 * Grids with {@code Boolean} or {@code boolean} values.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_GridB extends Grids_Grid {

    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new Grids_GridBinary with each cell value equal to {@code ndv}
     * and all chunks of the same type.
     *
     * @param e The grids environment.
     * @param fs What {@link #fs} is set to.
     * @param id What {@link #fsID} is set to.
     * @throws java.lang.Exception If encountered.
     */
    protected Grids_GridB(Grids_Environment e, IO_Cache fs, long id) 
            throws Exception {
        super(e, fs, id);
    }

}
