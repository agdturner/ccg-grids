/*
 * Copyright (C) 2017 agdturner.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package uk.ac.leeds.ccg.andyt.grids.core;

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid;
import java.util.HashMap;
import java.util.HashSet;
import uk.ac.leeds.ccg.andyt.generic.memory.Generic_Memory;

/**
 *
 * @author Andy
 */
public interface Grids_Memory extends Generic_Memory {

    boolean checkAndMaybeFreeMemory(boolean hoome);

    boolean checkAndMaybeFreeMemory(Grids_AbstractGrid g, boolean hoome);

    boolean checkAndMaybeFreeMemory(Grids_AbstractGrid g,
            Grids_2D_ID_int chunkNotToSwap, boolean hoome);

    boolean checkAndMaybeFreeMemory(Grids_2D_ID_int chunkNotToSwap,
            boolean hoome);

    boolean checkAndMaybeFreeMemory(HashMap<Grids_AbstractGrid, 
            HashSet<Grids_2D_ID_int>> chunksNotToSwap, boolean hoome);

    boolean checkAndMaybeFreeMemory(Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunksNotToSwap, boolean hoome);

    long checkAndMaybeFreeMemory_Account(boolean hoome);

    long checkAndMaybeFreeMemory_Account(Grids_AbstractGrid g, boolean hoome);

    long checkAndMaybeFreeMemory_Account(Grids_AbstractGrid g,
            Grids_2D_ID_int chunkNotToSwap, boolean hoome);

    long checkAndMaybeFreeMemory_Account(Grids_2D_ID_int chunkNotToSwap,
            boolean hoome);

    long checkAndMaybeFreeMemory_Account(HashMap<Grids_AbstractGrid, 
            HashSet<Grids_2D_ID_int>> chunksNotToSwap, boolean hoome);

    long checkAndMaybeFreeMemory_Account(Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunksNotToSwap, boolean hoome);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(boolean hoome);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(Grids_AbstractGrid g,
                    boolean hoome);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(Grids_AbstractGrid g,
                    Grids_2D_ID_int chunkNotToSwap, boolean hoome);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(
                    Grids_2D_ID_int chunkNotToSwap, boolean hoome);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(HashMap<Grids_AbstractGrid,
                    HashSet<Grids_2D_ID_int>> chunksNotToSwap, boolean hoome);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            checkAndMaybeFreeMemory_AccountDetail(Grids_AbstractGrid g,
                    HashSet<Grids_2D_ID_int> chunksNotToSwap, boolean hoome);

    void initMemoryReserve(Grids_AbstractGrid g, boolean hoome);

    void initMemoryReserve(Grids_2D_ID_int chunkNotToSwap, boolean hoome);

    void initMemoryReserve(Grids_AbstractGrid g, Grids_2D_ID_int chunkNotToSwap, 
            boolean hoome);

    void initMemoryReserve(Grids_AbstractGrid g, 
            HashSet<Grids_2D_ID_int> chunksNotToSwap, boolean hoome);

    void initMemoryReserve(HashMap<Grids_AbstractGrid, 
            HashSet<Grids_2D_ID_int>> chunksNotToSwap, boolean hoome);

    long initMemoryReserve_Account(boolean hoome);

    long initMemoryReserve_Account(Grids_2D_ID_int chunkNotToSwap, 
            boolean hoome);

    long initMemoryReserve_Account(Grids_AbstractGrid g, 
            Grids_2D_ID_int chunkNotToSwap, boolean hoome);

    long initMemoryReserve_Account(HashMap<Grids_AbstractGrid, 
            HashSet<Grids_2D_ID_int>> chunksNotToSwap, boolean hoome);

    long initMemoryReserve_Account(Grids_AbstractGrid g, boolean hoome);

    long initMemoryReserve_Account(Grids_AbstractGrid g, 
            HashSet<Grids_2D_ID_int> chunksNotToSwap, boolean hoome);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(boolean hoome);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(Grids_2D_ID_int chunkNotToSwap, 
                    boolean hoome);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(Grids_AbstractGrid g, 
                    Grids_2D_ID_int chunkNotToSwap, boolean hoome);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(Grids_AbstractGrid g, 
                    HashSet<Grids_2D_ID_int> chunksNotToSwap, boolean hoome);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(Grids_AbstractGrid g, 
                    boolean hoome);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            initMemoryReserve_AccountDetail(HashMap<Grids_AbstractGrid, 
                    HashSet<Grids_2D_ID_int>> chunksNotToSwap, boolean hoome);

}
