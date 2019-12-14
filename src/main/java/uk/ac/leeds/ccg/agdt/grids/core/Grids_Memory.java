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
package uk.ac.leeds.ccg.agdt.grids.core;

import java.io.IOException;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_Grid;
import java.util.HashMap;
import java.util.HashSet;
import uk.ac.leeds.ccg.agdt.generic.memory.Generic_Memory;

/**
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public interface Grids_Memory extends Generic_Memory {

    boolean checkAndMaybeFreeMemory(boolean hoome) throws IOException;

    boolean checkAndMaybeFreeMemory(Grids_Grid g, boolean hoome) throws IOException;

    boolean checkAndMaybeFreeMemory(Grids_Grid g,
            Grids_2D_ID_int chunkNotToSwap, boolean hoome) throws IOException;

    boolean checkAndMaybeFreeMemory(Grids_2D_ID_int chunkNotToSwap,
            boolean hoome) throws IOException;

    boolean checkAndMaybeFreeMemory(HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> chunksNotToSwap, boolean hoome) throws IOException;

    boolean checkAndMaybeFreeMemory(Grids_Grid g,
            HashSet<Grids_2D_ID_int> chunksNotToSwap, boolean hoome) throws IOException;

    long checkAndMaybeFreeMemory_Account(boolean hoome) throws IOException;

    long checkAndMaybeFreeMemory_Account(Grids_Grid g, boolean hoome) throws IOException;

    long checkAndMaybeFreeMemory_Account(Grids_Grid g,
            Grids_2D_ID_int chunkNotToSwap, boolean hoome) throws IOException;

    long checkAndMaybeFreeMemory_Account(Grids_2D_ID_int chunkNotToSwap,
            boolean hoome) throws IOException;

    long checkAndMaybeFreeMemory_Account(HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> chunksNotToSwap, boolean hoome) throws IOException;

    long checkAndMaybeFreeMemory_Account(Grids_Grid g,
            HashSet<Grids_2D_ID_int> chunksNotToSwap, boolean hoome) throws IOException;

    HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(boolean hoome) throws IOException;

    HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(Grids_Grid g,
                    boolean hoome) throws IOException;

    HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(Grids_Grid g,
                    Grids_2D_ID_int chunkNotToSwap, boolean hoome) throws IOException;

    HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(
                    Grids_2D_ID_int chunkNotToSwap, boolean hoome) throws IOException;

    HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> chunksNotToSwap, boolean hoome) throws IOException;

    HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(Grids_Grid g,
                    HashSet<Grids_2D_ID_int> chunksNotToSwap, boolean hoome) throws IOException;

    void initMemoryReserve(Grids_Grid g, boolean hoome) throws IOException;

    void initMemoryReserve(Grids_2D_ID_int chunkNotToSwap, boolean hoome) throws IOException;

    void initMemoryReserve(Grids_Grid g, Grids_2D_ID_int chunkNotToSwap,
            boolean hoome) throws IOException;

    void initMemoryReserve(Grids_Grid g,
            HashSet<Grids_2D_ID_int> chunksNotToSwap, boolean hoome) throws IOException;

    void initMemoryReserve(HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> chunksNotToSwap, boolean hoome) throws IOException;

    long initMemoryReserve_Account(boolean hoome) throws IOException;

    long initMemoryReserve_Account(Grids_2D_ID_int chunkNotToSwap,
            boolean hoome) throws IOException;

    long initMemoryReserve_Account(Grids_Grid g,
            Grids_2D_ID_int chunkNotToSwap, boolean hoome) throws IOException;

    long initMemoryReserve_Account(HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> chunksNotToSwap, boolean hoome) throws IOException;

    long initMemoryReserve_Account(Grids_Grid g, boolean hoome) throws IOException;

    long initMemoryReserve_Account(Grids_Grid g,
            HashSet<Grids_2D_ID_int> chunksNotToSwap, boolean hoome) throws IOException;

    HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(boolean hoome) throws IOException;

    HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(Grids_2D_ID_int chunkNotToSwap,
                    boolean hoome) throws IOException;

    HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(Grids_Grid g,
                    Grids_2D_ID_int chunkNotToSwap, boolean hoome) throws IOException;

    HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(Grids_Grid g,
                    HashSet<Grids_2D_ID_int> chunksNotToSwap, boolean hoome) throws IOException;

    HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(Grids_Grid g,
                    boolean hoome) throws IOException;

    HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> chunksNotToSwap, boolean hoome) throws IOException;

}
