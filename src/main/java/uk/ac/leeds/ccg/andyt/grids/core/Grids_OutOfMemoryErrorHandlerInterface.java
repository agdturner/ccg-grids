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
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Andy
 */
public interface Grids_OutOfMemoryErrorHandlerInterface
        extends Serializable {

    boolean tryToEnsureThereIsEnoughMemoryToContinue(
            boolean handleOutOfMemoryError);

    void tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid g, 
            boolean handleOutOfMemoryError);

    void tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid g, 
            Grids_2D_ID_int chunkNotToSwap, 
            boolean handleOutOfMemoryError);

    void tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_2D_ID_int chunkNotToSwap, boolean handleOutOfMemoryError);

    void tryToEnsureThereIsEnoughMemoryToContinue(
            HashMap<Grids_AbstractGrid, 
                    HashSet<Grids_2D_ID_int>> chunksNotToSwap, 
            boolean handleOutOfMemoryError);

    void tryToEnsureThereIsEnoughMemoryToContinue(
            Grids_AbstractGrid g, 
            HashSet<Grids_2D_ID_int> chunksNotToSwap, 
            boolean handleOutOfMemoryError);

    long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            boolean handleOutOfMemoryError);

    long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid g, boolean handleOutOfMemoryError);

    long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid g, 
            Grids_2D_ID_int chunkNotToSwap, 
            boolean handleOutOfMemoryError);

    long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_2D_ID_int chunkNotToSwap, boolean handleOutOfMemoryError);

    long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> chunksNotToSwap, 
            boolean handleOutOfMemoryError);

    long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            Grids_AbstractGrid g, 
            HashSet<Grids_2D_ID_int> chunksNotToSwap, 
            boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_AbstractGrid g, boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkNotToSwap, 
            boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_2D_ID_int chunkNotToSwap, boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> chunksNotToSwap,
            boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            Grids_AbstractGrid g, 
            HashSet<Grids_2D_ID_int> chunksNotToSwap, 
            boolean handleOutOfMemoryError);

    void initMemoryReserve(
            Grids_AbstractGrid g, boolean handleOutOfMemoryError);

    void initMemoryReserve(
            Grids_2D_ID_int chunkNotToSwap, 
            boolean handleOutOfMemoryError);

    void initMemoryReserve(
            Grids_AbstractGrid g, 
            Grids_2D_ID_int chunkNotToSwap,
            boolean handleOutOfMemoryError);

    void initMemoryReserve(
            Grids_AbstractGrid g,
            HashSet<Grids_2D_ID_int> chunksNotToSwap, 
            boolean handleOutOfMemoryError);

    void initMemoryReserve(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> chunksNotToSwap,
            boolean handleOutOfMemoryError);

    long initMemoryReserve_Account(boolean handleOutOfMemoryError);

    long initMemoryReserve_Account(
            Grids_2D_ID_int chunkNotToSwap, 
            boolean handleOutOfMemoryError);

    long initMemoryReserve_Account(
            Grids_AbstractGrid g,
            Grids_2D_ID_int chunkNotToSwap,
            boolean handleOutOfMemoryError);

    long initMemoryReserve_Account(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> chunksNotToSwap,
            boolean handleOutOfMemoryError);

    long initMemoryReserve_Account(
            Grids_AbstractGrid g, 
            boolean handleOutOfMemoryError);

    long initMemoryReserve_Account(
            Grids_AbstractGrid g, 
            HashSet<Grids_2D_ID_int> chunksNotToSwap, 
            boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> initMemoryReserve_AccountDetail(
            boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> initMemoryReserve_AccountDetail(
            Grids_2D_ID_int chunkNotToSwap, 
            boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> initMemoryReserve_AccountDetail(
            Grids_AbstractGrid g, 
            Grids_2D_ID_int chunkNotToSwap, 
            boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> initMemoryReserve_AccountDetail(
            Grids_AbstractGrid g, 
            HashSet<Grids_2D_ID_int> chunksNotToSwap, 
            boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> initMemoryReserve_AccountDetail(
            Grids_AbstractGrid g, 
            boolean handleOutOfMemoryError);

    HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> initMemoryReserve_AccountDetail(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> chunksNotToSwap, 
            boolean handleOutOfMemoryError);

}
