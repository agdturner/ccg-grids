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

/**
 * Provides some raster data processing functionality.
 */
module uk.ac.leeds.ccg.grids {
    requires transitive uk.ac.leeds.ccg.generic;
    requires transitive uk.ac.leeds.ccg.io;
    requires transitive uk.ac.leeds.ccg.math;
    requires transitive ch.obermuhlner.math.big;
    
    exports uk.ac.leeds.ccg.grids.core;
    exports uk.ac.leeds.ccg.grids.d2;
    exports uk.ac.leeds.ccg.grids.d2.chunk;
    exports uk.ac.leeds.ccg.grids.d2.chunk.b;
    exports uk.ac.leeds.ccg.grids.d2.chunk.br;
    exports uk.ac.leeds.ccg.grids.d2.chunk.d;
    exports uk.ac.leeds.ccg.grids.d2.chunk.i;
    exports uk.ac.leeds.ccg.grids.d2.grid;
    exports uk.ac.leeds.ccg.grids.d2.grid.b;
    exports uk.ac.leeds.ccg.grids.d2.grid.br;
    exports uk.ac.leeds.ccg.grids.d2.grid.d;
    exports uk.ac.leeds.ccg.grids.d2.grid.i;
    exports uk.ac.leeds.ccg.grids.d2.stats;
    exports uk.ac.leeds.ccg.grids.d2.util;
    //exports uk.ac.leeds.ccg.grids.d3;
    exports uk.ac.leeds.ccg.grids.io;
    exports uk.ac.leeds.ccg.grids.memory;
    exports uk.ac.leeds.ccg.grids.process;
}