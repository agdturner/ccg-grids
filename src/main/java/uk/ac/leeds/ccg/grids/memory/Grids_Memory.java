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
package uk.ac.leeds.ccg.grids.memory;

import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import java.io.IOException;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;
import java.util.HashMap;
import java.util.Set;
import uk.ac.leeds.ccg.generic.memory.Generic_Memory;

/**
 * For memory management.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public interface Grids_Memory extends Generic_Memory {

    /**
     *
     * @param hoome If {@code true} then OutOfMemoryErrors are handled.
     * @return {@code true} if there is sufficient memory to continue and throws
     * an OutOfMemoryError otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    boolean checkAndMaybeFreeMemory(boolean hoome) throws IOException,
            Exception;

    /**
     * @param g The grid from which chunks are not cleared.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return {@code true} if there is sufficient memory to continue and throws
     * an OutOfMemoryError otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    boolean checkAndMaybeFreeMemory(Grids_Grid g, boolean hoome)
            throws IOException, Exception;

    /**
     * @param g The grid from which chunks are not cleared.
     * @param notToSwap The chunk from {@code g} that is not cleared.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return {@code true} if there is sufficient memory to continue and throws
     * an OutOfMemoryError otherwise.
     * @throws java.io.IOException If encountered.
     */
    boolean checkAndMaybeFreeMemory(Grids_Grid g, Grids_2D_ID_int notToSwap,
            boolean hoome) throws IOException, Exception;

    /**
     * @param notToSwap The chunk ID of chunks not to be cleared.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return {@code true} if there is sufficient memory to continue and
     * {@code false} otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    boolean checkAndMaybeFreeMemory(Grids_2D_ID_int notToSwap, boolean hoome)
            throws IOException, Exception;

    /**
     * @param notToSwap Indicates which chunks not to clear unless desperate.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return {@code true} if there is sufficient memory to continue and {code
     * false} otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    boolean checkAndMaybeFreeMemory(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> notToSwap, boolean hoome)
            throws IOException, Exception;

    /**
     * @param g The grid from which no chunks with chunk IDs in {@code s} are
     * cleared.
     * @param notToSwap The chunk IDs of chunks in {@code g} that are not
     * cleared.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return {@code true} if there is sufficient memory to continue and
     * {@code false} otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    boolean checkAndMaybeFreeMemory(Grids_Grid g,
            Set<Grids_2D_ID_int> notToSwap, boolean hoome) throws IOException,
            Exception;

    /**
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return The number of chunks cleared.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    Grids_Account checkAndMaybeFreeMemory_Account(boolean hoome)
            throws IOException, Exception;

    /**
     * @param g A grid from which chunks are not swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    Grids_Account checkAndMaybeFreeMemory_Account(Grids_Grid g, boolean hoome)
            throws IOException, Exception;

    /**
     * @param g A grid from which the chunk with ID {@code i} is not swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @param notToSwap The chunk ID of a chunk in {@code g} that is not to be
     * swapped.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    Grids_Account checkAndMaybeFreeMemory_Account(Grids_Grid g,
            Grids_2D_ID_int notToSwap, boolean hoome) throws IOException,
            Exception;

    /**
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @param notToSwap The chunk ID of a chunks that are not to be swapped.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    Grids_Account checkAndMaybeFreeMemory_Account(Grids_2D_ID_int notToSwap,
            boolean hoome) throws IOException, Exception;

    /**
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @param notToSwap A map indicating the chunks that are not to be swapped.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    Grids_Account checkAndMaybeFreeMemory_Account(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> notToSwap,
            boolean hoome) throws IOException, Exception;

    /**
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @param g The grid from which no chunks in {@code s} are swapped.
     * @param notToSwap The chunk IDs which are not to be swapped from
     * {@code g}.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    Grids_Account checkAndMaybeFreeMemory_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> notToSwap, boolean hoome) throws IOException,
            Exception;

    /**
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return An account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(boolean hoome)
            throws IOException, Exception;

    /**
     * @param g No chunks from this grid are swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(Grids_Grid g,
            boolean hoome) throws IOException, Exception;

    /**
     * @param g The grid in which chunk with chunk ID {@code i} is not swapped.
     * @param notToSwap The chunk ID of the chunk in {@code g} that is not to be
     * swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(Grids_Grid g,
            Grids_2D_ID_int notToSwap, boolean hoome) throws IOException,
            Exception;

    /**
     * @param notToSwap The chunk ID that is not to be swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            Grids_2D_ID_int notToSwap, boolean hoome) throws IOException,
            Exception;

    /**
     * @param notToSwap Indicates the chunks ID that are not to be swapped.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> notToSwap, boolean hoome)
            throws IOException, Exception;

    /**
     * @param g A grid in which chunks in {@code s} are not swapped.
     * @param notToSwap A set of chunk IDs for which chunks in {@code g} are not
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory.
     * @return A detailed account of any swapping.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    Grids_AccountDetail checkAndMaybeFreeMemory_AccountDetail(Grids_Grid g,
            Set<Grids_2D_ID_int> notToSwap, boolean hoome) throws IOException,
            Exception;

    /**
     * @param g A grid with chunks not to be swapped out in the event of any
     * memory handling.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @throws java.io.IOException If encountered.
     */
    void initMemoryReserve(Grids_Grid g, boolean hoome) throws IOException,
            Exception;

    /**
     * @param notToSwap The ID of a chunk not to cleared in any memory
     * management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @throws java.io.IOException If encountered.
     */
    void initMemoryReserve(Grids_2D_ID_int notToSwap, boolean hoome)
            throws IOException, Exception;

    /**
     * @param g The grid in which chunk with ID {@code i} is not cleared.
     * @param notToSwap The ID of a chunk not to cleared in any memory
     * management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @throws java.io.IOException If encountered.
     */
    void initMemoryReserve(Grids_Grid g, Grids_2D_ID_int notToSwap,
            boolean hoome) throws IOException, Exception;

    /**
     * @param g The grid from which chunks with IDs in {@code s} are not
     * cleared.
     * @param notToSwap The set of chunk IDs of a chunk not to cleared from grid
     * (@code g )in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @throws IOException If encountered.
     */
    void initMemoryReserve(Grids_Grid g, Set<Grids_2D_ID_int> notToSwap,
            boolean hoome) throws IOException, Exception;

    /**
     * @param notToSwap The map of chunk IDs of a chunks not to cleared in any
     * memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @throws java.io.IOException If encountered.
     */
    void initMemoryReserve(HashMap<Grids_Grid, Set<Grids_2D_ID_int>> notToSwap,
            boolean hoome) throws IOException, Exception;

    /**
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return An account of chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    Grids_Account initMemoryReserve_Account(boolean hoome) throws IOException,
            Exception;

    /**
     * @param notToSwap The ID of a chunk not to cleared in any memory
     * management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return The number of chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    Grids_Account initMemoryReserve_Account(Grids_2D_ID_int notToSwap,
            boolean hoome) throws IOException, Exception;

    /**
     * @param g The grid in which chunk with ID {@code i} is not cleared.
     * @param notToSwap The ID of a chunk not to cleared in any memory
     * management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return An account of any clearing.
     * @throws java.io.IOException If encountered.
     */
    Grids_Account initMemoryReserve_Account(Grids_Grid g,
            Grids_2D_ID_int notToSwap, boolean hoome) throws IOException,
            Exception;

    /**
     * @param notToSwap A map containing chunk IDs of a chunks not to cleared
     * from grids in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return The number of chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    Grids_Account initMemoryReserve_Account(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> notToSwap,
            boolean hoome) throws IOException, Exception;

    /**
     * @param g The grid from which chunks are not cleared.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return The number of chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    Grids_Account initMemoryReserve_Account(Grids_Grid g, boolean hoome)
            throws IOException, Exception;

    /**
     * @param g The grid from which chunks with IDs in {@code s} are not
     * cleared.
     * @param notToSwap The set of chunk IDs of a chunk not to cleared from grid
     * (@code g )in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return The number of chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    Grids_Account initMemoryReserve_Account(Grids_Grid g,
            Set<Grids_2D_ID_int> notToSwap, boolean hoome) throws IOException,
            Exception;

    /**
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return A detailed account of any clearing.
     * @throws java.io.IOException If encountered.
     */
    Grids_AccountDetail initMemoryReserve_AccountDetail(boolean hoome)
            throws IOException, Exception;

    /**
     * @param notToSwap The ID of a chunk not to cleared in any memory
     * management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return A detailed account of any chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    Grids_AccountDetail initMemoryReserve_AccountDetail(
            Grids_2D_ID_int notToSwap, boolean hoome) throws IOException,
            Exception;

    /**
     * @param g The grid in which chunk with ID {@code i} is not cleared.
     * @param notToSwap The ID of a chunk not to cleared in any memory
     * management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return A detailed account of any chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    Grids_AccountDetail initMemoryReserve_AccountDetail(Grids_Grid g,
            Grids_2D_ID_int notToSwap, boolean hoome)
            throws IOException, Exception;

    /**
     * @param g The grid from which chunks with IDs in {@code s} are not
     * cleared.
     * @param notToSwap The set of chunk IDs of a chunk not to cleared from grid {@code
     * g} in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return A detailed account of any chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    Grids_AccountDetail initMemoryReserve_AccountDetail(Grids_Grid g,
            Set<Grids_2D_ID_int> notToSwap, boolean hoome)
            throws IOException, Exception;

    /**
     * @param g The grid from which chunks with IDs in {@code s} are not
     * cleared.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return A detailed account of any chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    Grids_AccountDetail initMemoryReserve_AccountDetail(Grids_Grid g,
            boolean hoome) throws IOException, Exception;

    /**
     * @param notToSwap A map containing chunk IDs of a chunks not to cleared
     * from grids in any memory management.
     * @param hoome If {@code true} then if an {@link OutOfMemoryError} is
     * thrown, then an attempt is made to handle it by clearing data from the
     * memory before calling this method again.
     * @return A detailed account of any chunks cleared.
     * @throws java.io.IOException If encountered.
     */
    Grids_AccountDetail initMemoryReserve_AccountDetail(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> notToSwap,
            boolean hoome) throws IOException, Exception;

}
