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
package uk.ac.leeds.ccg.grids.d2.grid.test;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.io.IO_Path;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.core.Grids_Strings;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_long;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntFactory;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntFactoryArray;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntFactorySinglet;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntSinglet;
import uk.ac.leeds.ccg.grids.d2.grid.b.Grids_GridBoolean;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridIntFactory;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_Stats;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.grids.memory.Grids_Account;
import uk.ac.leeds.ccg.grids.memory.Grids_AccountDetail;
import uk.ac.leeds.ccg.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.io.IO_Cache;
import uk.ac.leeds.ccg.math.number.Math_BigRational;
import uk.ac.leeds.ccg.math.number.Math_BigRationalSqrt;

/**
 * Test class for Grids_Grid.
 *
 * @author Andy Turner
 * @version 1.0
 */
@TestMethodOrder(OrderAnnotation.class)
public class Grids_GridTest {

    Generic_Environment env;
    Grids_Environment ge;
    Grids_Processor gp;

    public Grids_GridTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() throws Exception {
        Path dataDir = Paths.get(System.getProperty("user.home"),
                Grids_Strings.s_data);
        env = new Generic_Environment(new Generic_Defaults(
                Paths.get(dataDir.toString(), Grids_Strings.s_generic)));
        IO_Path dir = new IO_Path(dataDir);
        ge = new Grids_Environment(env, dir);
        gp = new Grids_Processor(ge);
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of isSameDimensionsAndChunks method, of class Grids_Processor.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testIsSameFrame() throws Exception {
        System.out.println("isSameFrame");
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        // Test 1
        Grids_Grid g0 = gfd.create(10, 10);
        Grids_Grid g1 = gfd.create(10, 10);
        boolean expResult = true;
        boolean result = g0.isSameDimensionsAndChunks(g1);
        assertEquals(expResult, result);
        // Test 2
        g0 = gfd.create(10, 10);
        g1 = gfd.create(10, 11);
        expResult = false;
        result = g0.isSameDimensionsAndChunks(g1);
        assertEquals(expResult, result);
        // Test 2
        Grids_GridIntFactory gfi = gp.gridFactoryInt;
        g0 = gfd.create(10, 10);
        g1 = gfi.create(10, 10);
        expResult = true;
        result = g0.isSameDimensionsAndChunks(g1);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCellDistance method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetCellDistance() throws Exception {
        System.out.println("getCellDistance");
        // Test 1
        int oom = -3;
            RoundingMode rm = RoundingMode.HALF_UP;
        Math_BigRationalSqrt distance = new Math_BigRationalSqrt(Math_BigRational.TEN.pow(2), oom, rm);
        int expResult = 10;
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(10, 10);
        int result = instance.getCellDistance(distance, oom, rm);
        assertEquals(expResult, result);
        // Test 2
        distance = new Math_BigRationalSqrt(Math_BigRational.TEN.pow(2), oom, rm);
        expResult = 100;
        gfd = gp.gridFactoryDouble;
        Math_BigRational xmin = Math_BigRational.ZERO;
        Math_BigRational xmax = Math_BigRational.ONE;
        Math_BigRational ymin = Math_BigRational.ZERO;
        Math_BigRational ymax = Math_BigRational.ONE;
        Math_BigRational cellSize = Math_BigRational.valueOf("0.1");
        Grids_Dimensions dims = new Grids_Dimensions(xmin, xmax, ymin, ymax,
                cellSize);
        instance = gfd.create(10, 10, dims);
        result = instance.getCellDistance(distance, oom, rm);
        assertEquals(expResult, result);
    }

    /**
     * Test of getNCols method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetNCols() throws Exception {
        System.out.println("getNCols");
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(10, 10);
        long expResult = 10L;
        long result = instance.getNCols();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNRows method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetNRows() throws Exception {
        System.out.println("getNRows");
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(10, 10);
        long expResult = 10L;
        long result = instance.getNRows();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNChunkRows method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetNChunkRows() throws Exception {
        System.out.println("getNChunkRows");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(1000, 1000);
        int expResult = 2;
        int result = instance.getNChunkRows();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNChunkCols method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetNChunkCols() throws Exception {
        System.out.println("getNChunkCols");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(1000, 1000);
        int expResult = 2;
        int result = instance.getNChunkCols();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNChunks method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetNChunks() throws Exception {
        System.out.println("getNChunks");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(1000, 1000);
        long expResult = 4;
        long result = instance.getNChunks();
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkNRows method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkNRows_0args() throws Exception {
        System.out.println("getChunkNRows");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(1000, 1000);
        int expResult = 512;
        int result = instance.getChunkNRows();
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkNRows method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkNRows_int() throws Exception {
        System.out.println("getChunkNRows");
        // By default chunkNRows and chunkNCols are 512.
        int cr = 0;
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(1000, 1000);
        int expResult = 512;
        int result = instance.getChunkNRows(cr);
        assertEquals(expResult, result);
        cr = 1;
        expResult = 488;
        result = instance.getChunkNRows(cr);
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkNCols method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkNCols_0args() throws Exception {
        System.out.println("getChunkNCols");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(1000, 1000);
        int expResult = 512;
        int result = instance.getChunkNCols();
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkNCols method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkNCols_int() throws Exception {
        System.out.println("getChunkNCols");
        // By default chunkNRows and chunkNCols are 512.
        int cc = 0;
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(1000, 1000);
        int expResult = 512;
        int result = instance.getChunkNCols(cc);
        assertEquals(expResult, result);
        cc = 1;
        expResult = 488;
        result = instance.getChunkNCols(cc);
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkNRowsFinalRowChunk method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkNRowsFinalRowChunk() throws Exception {
        System.out.println("getChunkNRowsFinalRowChunk");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(1000, 1000);
        int expResult = 488;
        int result = instance.getChunkNRowsFinalRowChunk();
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkNColsFinalColChunk method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkNColsFinalColChunk() throws Exception {
        System.out.println("getChunkNColsFinalColChunk");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(1000, 1000);
        int expResult = 488;
        int result = instance.getChunkNColsFinalColChunk();
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkNRows method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkNRows_Grids_2D_ID_int() throws Exception {
        System.out.println("getChunkNRows");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(1000, 1000);
        int expResult = 512;
        Grids_2D_ID_int i = new Grids_2D_ID_int(0, 0);
        int result = instance.getChunkNRows(i);
        assertEquals(expResult, result);
        // Test 2
        expResult = 488;
        i = new Grids_2D_ID_int(1, 0);
        result = instance.getChunkNRows(i);
        assertEquals(expResult, result);
        // Test 3
        expResult = 512;
        i = new Grids_2D_ID_int(0, 1);
        result = instance.getChunkNRows(i);
        assertEquals(expResult, result);
        // Test 4
        expResult = 488;
        i = new Grids_2D_ID_int(1, 1);
        result = instance.getChunkNRows(i);
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkNCols method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkNCols_Grids_2D_ID_int() throws Exception {
        System.out.println("getChunkNCols");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(1000, 1000);
        int expResult = 512;
        Grids_2D_ID_int i = new Grids_2D_ID_int(0, 0);
        int result = instance.getChunkNCols(i);
        assertEquals(expResult, result);
        // Test 2
        expResult = 512;
        i = new Grids_2D_ID_int(1, 0);
        result = instance.getChunkNCols(i);
        assertEquals(expResult, result);
        // Test 3
        expResult = 488;
        i = new Grids_2D_ID_int(0, 1);
        result = instance.getChunkNCols(i);
        assertEquals(expResult, result);
        // Test 4
        expResult = 488;
        i = new Grids_2D_ID_int(1, 1);
        result = instance.getChunkNCols(i);
        assertEquals(expResult, result);
    }

//    /**
//     * Test of getChunkIDs method, of class Grids_Grid.
//     */
//    @Test
//    public void testGetChunkIDs_7args() {
//        System.out.println("getChunkIDs");
//        Math_BigRational distance = null;
//        Math_BigRational x = null;
//        Math_BigRational y = null;
//        long row = 0L;
//        long col = 0L;
//        int dp = 0;
//        RoundingMode rm = null;
//        Grids_Grid instance = null;
//        Set<Grids_2D_ID_int> expResult = null;
//        Set<Grids_2D_ID_int> result = instance.getChunkIDs(distance, x, y, row, col, dp, rm);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getChunkIDs method, of class Grids_Grid.
//     */
//    @Test
//    public void testGetChunkIDs_4args_1() {
//        System.out.println("getChunkIDs");
//        long rowMin = 0L;
//        long rowMax = 0L;
//        long colMin = 0L;
//        long colMax = 0L;
//        Grids_Grid instance = null;
//        Set<Grids_2D_ID_int> expResult = null;
//        Set<Grids_2D_ID_int> result = instance.getChunkIDs(rowMin, rowMax, colMin, colMax);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getChunkIDs method, of class Grids_Grid.
//     */
//    @Test
//    public void testGetChunkIDs_4args_2() {
//        System.out.println("getChunkIDs");
//        int crMin = 0;
//        int crMax = 0;
//        int ccMin = 0;
//        int ccMax = 0;
//        Grids_Grid instance = null;
//        Set<Grids_2D_ID_int> expResult = null;
//        Set<Grids_2D_ID_int> result = instance.getChunkIDs(crMin, crMax, ccMin, ccMax);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of getChunkCol method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkCol_Math_BigRational() throws Exception {
        System.out.println("getChunkCol");
        Math_BigRational x = Math_BigRational.valueOf(0.5d);
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        Grids_Grid instance = gfd.create(1000, 5120);
        int expResult = 0;
        int result = instance.getChunkCol(x);
        assertEquals(expResult, result);
        // Test 2
        x = Math_BigRational.valueOf(512.5d);
        expResult = 1;
        result = instance.getChunkCol(x);
        assertEquals(expResult, result);
        // Test 3
        x = Math_BigRational.valueOf(1024.5d);
        expResult = 2;
        result = instance.getChunkCol(x);
        assertEquals(expResult, result);
        // Test 4
        x = Math_BigRational.valueOf(5119.5d);
        expResult = 9;
        result = instance.getChunkCol(x);
        assertEquals(expResult, result);
        // Test 5
        x = Math_BigRational.valueOf(5120.5d);
        expResult = 10;
        result = instance.getChunkCol(x);
        assertEquals(expResult, result);
        // Test 6
        x = Math_BigRational.valueOf(-0.5d);
        expResult = -1;
        result = instance.getChunkCol(x);
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkCol method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkCol_long() throws Exception {
        System.out.println("getChunkCol");
        long col = 0L;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 1000;
        long ncols = 5120;
        Grids_Grid instance = gfd.create(nrows, ncols);
        int expResult = 0;
        int result = instance.getChunkCol(col);
        assertEquals(expResult, result);
        // Test 2
        col = -1L;
        expResult = -1;
        result = instance.getChunkCol(col);
        assertEquals(expResult, result);
        // Test 3
        col = -1L;
        expResult = -1;
        result = instance.getChunkCol(col);
        assertEquals(expResult, result);
        // Test 4
        col = -512L;
        expResult = -2;
        result = instance.getChunkCol(col);
        assertEquals(expResult, result);
        // Test 4
        col = 5119L;
        expResult = 9;
        result = instance.getChunkCol(col);
        assertEquals(expResult, result);
        // Test 4
        col = 5120L;
        expResult = 10;
        result = instance.getChunkCol(col);
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkCellCol method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkCellCol_Math_BigRational() throws Exception {
        System.out.println("getChunkCellCol");
        Math_BigRational x = Math_BigRational.ZERO;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 1000;
        long ncols = 5120;
        Grids_Grid instance = gfd.create(nrows, ncols);
        int expResult = 0;
        int result = instance.getChunkCellCol(x);
        assertEquals(expResult, result);
        // Test 2
        x = Math_BigRational.valueOf(-0.5);
        expResult = 511;
        result = instance.getChunkCellCol(x);
        assertEquals(expResult, result);
        // Test 3
        x = Math_BigRational.valueOf(5120);
        expResult = 0;
        result = instance.getChunkCellCol(x);
        assertEquals(expResult, result);
        // Test 3
        x = Math_BigRational.valueOf("5119.999999999999999999999999999");
        expResult = 511;
        result = instance.getChunkCellCol(x);
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkCellCol method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkCellCol_long() throws Exception {
        System.out.println("getChunkCellCol");
        long col = 0L;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 1000;
        long ncols = 5120;
        Grids_Grid instance = gfd.create(nrows, ncols);
        int expResult = 0;
        int result = instance.getChunkCellCol(col);
        assertEquals(expResult, result);
        // Test 2
        col = 1L;
        expResult = 1;
        result = instance.getChunkCellCol(col);
        assertEquals(expResult, result);
        // Test 3
        col = -1L;
        expResult = 511;
        result = instance.getChunkCellCol(col);
        assertEquals(expResult, result);
        // Test 4
        col = ncols;
        expResult = 0;
        result = instance.getChunkCellCol(col);
        assertEquals(expResult, result);
        // Test 5
        col = ncols - 1;
        expResult = 511;
        result = instance.getChunkCellCol(col);
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkCellRow method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkCellRow_Math_BigRational() throws Exception {
        System.out.println("getChunkCellRow");
        Math_BigRational y = Math_BigRational.ZERO;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        int chunkNRows = gfd.chunkNRows;
        long nrows = 5120;
        long ncols = 1000;
        Grids_Grid instance = gfd.create(nrows, ncols);
        int expResult = 0;
        int result = instance.getChunkCellRow(y);
        assertEquals(expResult, result);
        // Test 2
        y = Math_BigRational.valueOf("-0.5");
        expResult = chunkNRows - 1;
        result = instance.getChunkCellRow(y);
        assertEquals(expResult, result);
        // Test 3
        y = Math_BigRational.valueOf("5120");
        expResult = 0;
        result = instance.getChunkCellRow(y);
        assertEquals(expResult, result);
        // Test 3
        y = Math_BigRational.valueOf("5119.99999999999999");
        expResult = 511;
        result = instance.getChunkCellRow(y);
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkCellRow method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkCellRow_long() throws Exception {
        System.out.println("getChunkCellRow");
        long row = 0L;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        int chunkNRows = gfd.chunkNRows;
        long nrows = 5120;
        long ncols = 1000;
        Grids_Grid instance = gfd.create(nrows, ncols);
        int expResult = 0;
        int result = instance.getChunkCellRow(row);
        assertEquals(expResult, result);
        // Test 2
        row = -1L;
        expResult = chunkNRows - 1;
        result = instance.getChunkCellRow(row);
        assertEquals(expResult, result);
        // Test 3
        row = 5120L;
        expResult = 0;
        result = instance.getChunkCellRow(row);
        assertEquals(expResult, result);
        // Test 3
        row = 5119L;
        expResult = chunkNRows - 1;
        result = instance.getChunkCellRow(row);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCol method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetCol_Math_BigRational() throws Exception {
        System.out.println("getCol");
        Math_BigRational x = Math_BigRational.ZERO;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 5120;
        long ncols = 1000;
        Grids_Grid instance = gfd.create(nrows, ncols);
        long expResult = 0L;
        long result = instance.getCol(x);
        assertEquals(expResult, result);
        // Test 2
        x = Math_BigRational.valueOf("5119.999999999999999");
        expResult = 5119L;
        result = instance.getCol(x);
        assertEquals(expResult, result);
        // Test 3
        x = Math_BigRational.valueOf("5119.9999999999999999999999999999999999999999999999"
                + "9999999999999999999999999999999999999999999999999999999999");
        expResult = 5119L;
        result = instance.getCol(x);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCol method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetCol_int_int() throws Exception {
        System.out.println("getCol");
        int cc = 0;
        int ccc = 0;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        int chunkNRows = gfd.chunkNRows;
        int chunkNCols = gfd.chunkNCols;
        long nrows = 5120;
        long ncols = 1000;
        Grids_Grid instance = gfd.create(nrows, ncols);
        long expResult = 0L;
        long result = instance.getCol(cc, ccc);
        assertEquals(expResult, result);
        // Test 2
        cc = 1;
        ccc = 1;
        expResult = chunkNCols + 1;
        result = instance.getCol(cc, ccc);
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkRow method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkRow_Math_BigRational() throws Exception {
        System.out.println("getChunkRow");
        Math_BigRational y = Math_BigRational.ZERO;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        int chunkNRows = gfd.chunkNRows;
        int chunkNCols = gfd.chunkNCols;
        long nrows = 5120;
        long ncols = 1000;
        Grids_Grid instance = gfd.create(nrows, ncols);
        int expResult = 0;
        int result = instance.getChunkRow(y);
        assertEquals(expResult, result);
        // Test 2
        y = Math_BigRational.valueOf("-0.000000000000000000000000000000000000000000000000"
                + "000000000000000000000000000000000000000000000000000000000000"
                + "0000000000000000000000000000000000000000000000000000000001");
        expResult = -1;
        result = instance.getChunkRow(y);
        assertEquals(expResult, result);
    }

    /**
     * Test of getChunkRow method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetChunkRow_long() throws Exception {
        System.out.println("getChunkRow");
        long row = 0L;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        int chunkNRows = gfd.chunkNRows;
        int chunkNCols = gfd.chunkNCols;
        long nrows = 5120;
        long ncols = 1000;
        Grids_Grid instance = gfd.create(nrows, ncols);
        int expResult = 0;
        int result = instance.getChunkRow(row);
        assertEquals(expResult, result);
        // Test 2
        row = -1;
        expResult = -1;
        result = instance.getChunkRow(row);
        assertEquals(expResult, result);
        // Test 3
        row = -1;
        expResult = -1;
        result = instance.getChunkRow(row);
        assertEquals(expResult, result);
        // Test 4
        row = nrows;
        expResult = 10;
        result = instance.getChunkRow(row);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRow method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetRow_Math_BigRational() throws Exception {
        System.out.println("getRow");
        Math_BigRational y = Math_BigRational.ZERO;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        int chunkNRows = gfd.chunkNRows;
        int chunkNCols = gfd.chunkNCols;
        long nrows = 5120;
        long ncols = 1000;
        Grids_Grid instance = gfd.create(nrows, ncols);
        long expResult = 0L;
        long result = instance.getRow(y);
        assertEquals(expResult, result);
        // Test 2
        y = Math_BigRational.valueOf("-0.000000000000000000000000000000000000000000000000"
                + "000000000000000000000000000000000000000000000000000000000000"
                + "0000000000000000000000000000000000000000000000000000000001");
        expResult = -1L;
        result = instance.getRow(y);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRow method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetRow_int_int() throws Exception {
        System.out.println("getRow");
        int cr = 0;
        int ccr = 0;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        int chunkNRows = gfd.chunkNRows;
        int chunkNCols = gfd.chunkNCols;
        long nrows = 5120;
        long ncols = 1000;
        Grids_Grid instance = gfd.create(nrows, ncols);
        long expResult = 0L;
        long result = instance.getRow(cr, ccr);
        assertEquals(expResult, result);
        // Test 2
        cr = 1;
        ccr = 1;
        expResult = chunkNRows + 1;
        result = instance.getRow(cr, ccr);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCellID method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetCellID_long_long() throws Exception {
        System.out.println("getCellID");
        long row = 0L;
        long col = 0L;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 512;
        long ncols = 100;
        Grids_Grid instance = gfd.create(nrows, ncols);
        Grids_2D_ID_long expResult = new Grids_2D_ID_long(row, col);
        Grids_2D_ID_long result = instance.getCellID(row, col);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCellID method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetCellID_Math_BigRational_Math_BigRational() throws Exception {
        System.out.println("getCellID");
        Math_BigRational x = Math_BigRational.ZERO;
        Math_BigRational y = Math_BigRational.ZERO;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 512;
        long ncols = 100;
        Grids_Grid instance = gfd.create(nrows, ncols);
        Grids_2D_ID_long expResult = new Grids_2D_ID_long(0L, 0L);
        Grids_2D_ID_long result = instance.getCellID(x, y);
        assertEquals(expResult, result);
    }

    /**
     * Test of getHeight method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetHeight() throws Exception {
        System.out.println("getHeight");
        // By default chunkNRows and chunkNCols are 512 and cellSize is 1.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 512;
        long ncols = 100;
        Grids_Grid instance = gfd.create(nrows, ncols);
        Math_BigRational expResult = Math_BigRational.valueOf(nrows);
        Math_BigRational result = instance.getHeight();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWidth method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetWidth() throws Exception {
        System.out.println("getWidth");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 512;
        long ncols = 100;
        Grids_Grid instance = gfd.create(nrows, ncols);
        Math_BigRational expResult = Math_BigRational.valueOf(ncols);
        Math_BigRational result = instance.getWidth();
        assertEquals(expResult, result);
    }

    /**
     * Test of isSameDimensionsAndValues method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testIsSameDimensionsAndValues() throws Exception {
        System.out.println("isSameDimensionsAndValues");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 512;
        long ncols = 100;
        Grids_Grid g = gfd.create(nrows, ncols);
        Grids_Grid instance = gfd.create(nrows, ncols);
        boolean expResult = true;
        boolean result = instance.isSameDimensionsAndValues(g);
        assertEquals(expResult, result);
        // Test 2
        gp.gridFactoryDouble.setChunkNCols(50);
        instance = gfd.create(nrows, ncols);
        result = instance.isSameDimensionsAndValues(g);
        expResult = true;
        assertEquals(expResult, result);
        // Test 3
        ((Grids_GridDouble) instance).setCell(0, 0, 10);
        result = instance.isSameDimensionsAndValues(g);
        expResult = false;
        assertEquals(expResult, result);
        // Test 3
        ((Grids_GridDouble) g).setCell(0, 0, 10);
        result = instance.isSameDimensionsAndValues(g);
        expResult = true;
        assertEquals(expResult, result);
    }

    /**
     * Test of isSameDimensions method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testIsSameDimensions() throws Exception {
        System.out.println("isSameDimensions");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 512;
        long ncols = 100;
        Grids_Grid g = gfd.create(nrows, ncols);
        Grids_Grid instance = gfd.create(nrows, ncols);
        boolean expResult = true;
        boolean result = instance.isSameDimensionsAndValues(g);
        assertEquals(expResult, result);
        // Test 2
        gp.gridFactoryDouble.setChunkNCols(50);
        instance = gfd.create(nrows, ncols);
        result = instance.isSameDimensionsAndValues(g);
        assertEquals(expResult, result);
    }

    /**
     * Test of isSameDimensionsAndChunks method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testIsSameDimensionsAndChunks() throws Exception {
        System.out.println("isSameDimensionsAndChunks");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 512;
        long ncols = 100;
        Grids_Grid g = gfd.create(nrows, ncols);
        Grids_Grid instance = gfd.create(nrows, ncols);
        boolean expResult = true;
        boolean result = instance.isSameDimensionsAndChunks(g);
        assertEquals(expResult, result);
        // Test 2
        gp.gridFactoryDouble.setChunkNCols(50);
        instance = gfd.create(nrows, ncols);
        result = instance.isSameDimensionsAndChunks(g);
        expResult = false;
        assertEquals(expResult, result);
    }

    /**
     * Test of isCoincident method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testIsCoincident() throws Exception {
        System.out.println("isCoincident");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 512;
        long ncols = 100;
        Grids_Grid g = gfd.create(nrows, ncols);
        Grids_Grid instance = gfd.create(nrows, ncols);
        boolean expResult = true;
        boolean result = instance.isCoincident(g);
        assertEquals(expResult, result);
        // Test 2
        Math_BigRational xmin = Math_BigRational.valueOf(-1);
        Math_BigRational xmax = Math_BigRational.valueOf(99);
        Math_BigRational ymin = Math_BigRational.valueOf(-1);
        Math_BigRational ymax = Math_BigRational.valueOf(511);
        Math_BigRational cellSize = Math_BigRational.ONE;
        Grids_Dimensions d = new Grids_Dimensions(xmin, xmax, ymin, ymax,
                cellSize);
        gp.gridFactoryDouble.setDimensions(d);
        g = gfd.create(nrows, ncols, d);
        expResult = true;
        result = instance.isCoincident(g);
        assertEquals(expResult, result);
    }

    /**
     * Test of isInGrid method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testIsInGrid_Math_BigRational_Math_BigRational() throws Exception {
        System.out.println("isInGrid");
        Math_BigRational x = Math_BigRational.valueOf("0.000000000000000000000000000000000001");
        Math_BigRational y = Math_BigRational.valueOf("0.000000000000000000000000000000000001");
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 1;
        long ncols = 1;
        Grids_Grid instance = gfd.create(nrows, ncols);
        boolean expResult = true;
        boolean result = instance.isInGrid(x, y);
        assertEquals(expResult, result);
    }

    /**
     * Test of isInGrid method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testIsInGrid_long_long() throws Exception {
        System.out.println("isInGrid");
        long r = 0L;
        long c = 0L;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 1;
        long ncols = 1;
        Grids_Grid instance = gfd.create(nrows, ncols);
        boolean expResult = true;
        boolean result = instance.isInGrid(r, c);
        assertEquals(expResult, result);
    }

    /**
     * Test of isInGrid method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testIsInGrid_Grids_2D_ID_long() throws Exception {
        System.out.println("isInGrid");
        Grids_2D_ID_long i = new Grids_2D_ID_long(0L, 0L);
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 1;
        long ncols = 1;
        Grids_Grid instance = gfd.create(nrows, ncols);
        boolean expResult = true;
        boolean result = instance.isInGrid(i);
        assertEquals(expResult, result);
        // Test 2
        i = new Grids_2D_ID_long(1L, 0L);
        expResult = false;
        result = instance.isInGrid(i);
        assertEquals(expResult, result);
    }

    /**
     * Test of isInGrid method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testIsInGrid_Grids_2D_ID_int() throws Exception {
        System.out.println("isInGrid");
        Grids_2D_ID_int i = new Grids_2D_ID_int(0, 0);
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 1;
        long ncols = 1;
        Grids_Grid instance = gfd.create(nrows, ncols);
        boolean expResult = true;
        boolean result = instance.isInGrid(i);
        assertEquals(expResult, result);
        // Test 2
        i = new Grids_2D_ID_int(1, 0);
        expResult = false;
        result = instance.isInGrid(i);
        assertEquals(expResult, result);
    }

    /**
     * Test of isInGrid method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testIsInGrid_4args() throws Exception {
        System.out.println("isInGrid");
        int cr = 0;
        int cc = 0;
        int ccr = 0;
        int ccc = 0;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 1;
        long ncols = 1;
        Grids_Grid instance = gfd.create(nrows, ncols);
        boolean expResult = true;
        boolean result = instance.isInGrid(cr, cc, ccr, ccc);
        assertEquals(expResult, result);
        // Test 2
        cr = 1;
        expResult = false;
        result = instance.isInGrid(cr, cc, ccr, ccc);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCellX method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetCellX_long() throws Exception {
        System.out.println("getCellX");
        long col = 0L;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 1;
        long ncols = 1;
        Grids_Grid instance = gfd.create(nrows, ncols);
        Math_BigRational expResult = Math_BigRational.valueOf(0.5d);
        Math_BigRational result = instance.getCellX(col);
        assertTrue(result.compareTo(expResult) == 0);
    }

    /**
     * Test of getCellX method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetCellX_Grids_2D_ID_long() throws Exception {
        System.out.println("getCellX");
        Grids_2D_ID_long i = new Grids_2D_ID_long(0L, 0L);
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 1;
        long ncols = 1;
        Grids_Grid instance = gfd.create(nrows, ncols);
        Math_BigRational expResult = Math_BigRational.valueOf(0.5d);
        Math_BigRational result = instance.getCellX(i);
        assertTrue(result.compareTo(expResult) == 0);
    }

    /**
     * Test of getCellY method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetCellY_long() throws Exception {
        System.out.println("getCellY");
        long row = 0L;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 1;
        long ncols = 1;
        Grids_Grid instance = gfd.create(nrows, ncols);
        Math_BigRational expResult = Math_BigRational.valueOf(0.5d);
        Math_BigRational result = instance.getCellY(row);
        assertTrue(result.compareTo(expResult) == 0);
    }

    /**
     * Test of getCellY method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetCellY_Grids_2D_ID_long() throws Exception {
        System.out.println("getCellY");
        Grids_2D_ID_long i = new Grids_2D_ID_long(0L, 0L);
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 1;
        long ncols = 1;
        Grids_Grid instance = gfd.create(nrows, ncols);
        Math_BigRational expResult = Math_BigRational.valueOf(0.5d);
        Math_BigRational result = instance.getCellY(i);
        assertTrue(result.compareTo(expResult) == 0);
    }

    /**
     * Test of getCellBounds method, of class Grids_Grid.
     *
     * @throws Exception If encountered.
     */
    @Test
    public void testGetCellBounds() throws Exception {
        System.out.println("getCellBounds");
        long row = 0L;
        long col = 0L;
        // By default chunkNRows and chunkNCols are 512.
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long nrows = 1;
        long ncols = 1;
        Grids_Grid instance = gfd.create(nrows, ncols);
        Math_BigRational[] expResult = new Math_BigRational[4];
        expResult[0] = Math_BigRational.ZERO;
        expResult[1] = Math_BigRational.ZERO;
        expResult[2] = Math_BigRational.ONE;
        expResult[3] = Math_BigRational.ONE;
        Math_BigRational[] result = instance.getCellBounds(row, col);
        for (int i = 0; i < 4; i++) {
            assertTrue(result[i].compareTo(expResult[i]) == 0);
        }
    }

    /**
     * Test of init method, of class Grids_Grid.
     */
    @Test
    public void testInit_0args() throws Exception {
        // No Test
    }

    /**
     * Test of init method, of class Grids_Grid.
     */
    @Test
    public void testInit_Grids_Grid() throws Exception {
        // No Test
    }

    /**
     * Test of init method, of class Grids_Grid.
     */
    @Test
    public void testInit_3args() {
        // No Test
    }

    /**
     * Test of init method, of class Grids_Grid.
     */
    @Test
    public void testInit_6args() {
        // No Test
    }

    /**
     * Test of init method, of class Grids_Grid.
     */
    @Test
    public void testInit_8args() {
        // No Test
    }

    /**
     * Test of setReferenceInChunks method, of class Grids_Grid.
     */
    @Test
    public void testSetReferenceInChunks() {
        // No Test
    }

    /**
     * Test of getChunkIDs method, of class Grids_Grid.
     */
    @Test
    public void testGetChunkIDs_0args() {
        // No Test
    }

    /**
     * Test of getFieldsDescription method, of class Grids_Grid.
     */
    @Test
    public void testGetFieldsDescription() {
        // No Test
    }

    /**
     * Test of toString method, of class Grids_Grid.
     */
    @Test
    public void testToString() {
        // No Test
    }

    /**
     * Test of getDirectory method, of class Grids_Grid.
     */
    @Test
    public void testGetDirectory() {
        // No Test
    }

    /**
     * Test of getName method, of class Grids_Grid.
     */
    @Test
    public void testGetName() {
        // No Test
    }

    /**
     * Test of setName method, of class Grids_Grid.
     */
    @Test
    public void testSetName() {
        // No Test
    }

    /**
     * Test of initNChunkRows method, of class Grids_Grid.
     */
    @Test
    public void testInitNChunkRows() {
        // No Test
    }

    /**
     * Test of initNChunkCols method, of class Grids_Grid.
     */
    @Test
    public void testInitNChunkCols() {
        // No Test
    }

    /**
     * Test of getDimensions method, of class Grids_Grid.
     */
    @Test
    public void testGetDimensions() {
        // No Test
    }

    /**
     * Test of getCellsize method, of class Grids_Grid.
     */
    @Test
    public void testGetCellsize() {
        // No Test
    }

    /**
     * Test of getChunkIDs method, of class Grids_Grid.
     */
    @Test
    public void testGetChunkIDs_5args() {
        System.out.println("getChunkIDs");
        // Test 1
        int oom = -1;
            RoundingMode rm = RoundingMode.HALF_UP;
        Math_BigRationalSqrt distance = new Math_BigRationalSqrt(100, oom, rm, false);
        Math_BigRational x = Math_BigRational.valueOf(256);
        Math_BigRational y = Math_BigRational.valueOf(128);
        try {
            IO_Cache fs = new IO_Cache(ge.files.getGeneratedGridIntDir().getPath());
            Grids_ChunkIntFactorySinglet cifs = new Grids_ChunkIntFactorySinglet(0);
            Grids_ChunkIntFactoryArray cifa = new Grids_ChunkIntFactoryArray();
            int chunkNrows = 64;
            int chunkNcols = 64;
            Grids_GridIntFactory f = new Grids_GridIntFactory(ge, fs, cifs,
                    cifa, chunkNrows, chunkNcols);
            Math_BigRational xmin = Math_BigRational.valueOf(0);
            Math_BigRational ymin = Math_BigRational.valueOf(0);
            Math_BigRational xmax = Math_BigRational.valueOf(640);
            Math_BigRational ymax = Math_BigRational.valueOf(320);
            Math_BigRational cellsize = Math_BigRational.valueOf(1);
            Grids_Dimensions dim = new Grids_Dimensions(xmin, xmax, ymin, ymax, cellsize);
            int nrows = dim.getHeight().divide(dim.getCellsize()).intValue();
            int ncols = dim.getWidth().divide(dim.getCellsize()).intValue();
            Grids_Grid instance = f.create(nrows, ncols, dim);
            Set<Grids_2D_ID_int> expResult = new HashSet<>();
            expResult.add(new Grids_2D_ID_int(1, 3));
            expResult.add(new Grids_2D_ID_int(2, 4));
            expResult.add(new Grids_2D_ID_int(1, 4));
            expResult.add(new Grids_2D_ID_int(2, 3));
            Set<Grids_2D_ID_int> result = instance.getChunkIDs(distance, x, y, instance.getRow(y), instance.getCol(x), oom, rm);
            assertTrue(expResult.size() == result.size());
            result.forEach(id -> {
                assertTrue(expResult.contains(id));
            });
        } catch (Exception ex) {
            Logger.getLogger(Grids_GridTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getChunkIDs method, of class Grids_Grid.
     */
    @Test
    public void testGetChunkIDs_4args_1() {
        // No test.
    }

    /**
     * Test of getChunkIDs method, of class Grids_Grid.
     */
    @Test
    public void testGetChunkIDs_4args_2() {
        // No test.
    }

    /**
     * Test of cache method, of class Grids_Grid.
     */
    @Test
    public void testCache_0args() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunk method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunk_0args() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunk method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunk_Set() throws Exception {
        // No test.
    }

    /**
     * Test of cache method, of class Grids_Grid.
     */
    @Test
    public void testCache_Grids_2D_ID_int() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunks method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunks_Set() {
        // No test.
    }

    /**
     * Test of swapChunk_AccountDetail method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunk_AccountDetail_boolean() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunk_AccountDetail method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunk_AccountDetail_0args() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunk_AccountChunk method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunk_AccountChunk_boolean_boolean() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunk_AccountChunk method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunk_AccountChunk_0args() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunks_Account method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunks_Account_0args() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunks_Account method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunks_Account_Set() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunkExcept_AccountChunk method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunkExcept_AccountChunk_3args_1() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunkExcept_AccountChunk method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunkExcept_AccountChunk_Set() throws Exception {
        // No test.
    }

    /**
     * Test of isChunkSingleValueChunk method, of class Grids_Grid.
     */
    @Test
    public void testIsChunkSingleValueChunk() {
        // No test.
    }

    /**
     * Test of swap method, of class Grids_Grid.
     */
    @Test
    public void testSwap() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunk method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunk_Grids_2D_ID_int() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunk method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunk_boolean_boolean() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunkExcept_AccountDetail method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunkExcept_AccountDetail_3args_1() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunkExcept_AccountDetail method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunkExcept_AccountDetail_Grids_2D_ID_int() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunkExcept_AccountChunk method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunkExcept_AccountChunk_3args_2() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunkExcept_AccountChunk method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunkExcept_AccountChunk_Grids_2D_ID_int() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunksExcept_AccountDetail method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunksExcept_AccountDetail_3args_1() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunksExcept_AccountDetail method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunksExcept_AccountDetail_Grids_2D_ID_int() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunks method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunks_0args() {
        // No test.
    }

    /**
     * Test of swapChunksExcept_Account method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunksExcept_Account_3args_1() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunksExcept_AccountDetail method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunksExcept_AccountDetail_3args_2() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunksExcept method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunksExcept_Set_Grids_AccountDetail() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunkExcept_Account method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunkExcept_Account() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunkExcept method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunkExcept() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunksExcept_Account method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunksExcept_Account_3args_2() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunksExcept method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunksExcept_Grids_2D_ID_int() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunksExcept method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunksExcept_Set() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunkExcept_AccountDetail method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunkExcept_AccountDetail_3args_2() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunkExcept_AccountDetail method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunkExcept_AccountDetail_Set() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunks_AccountDetail method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunks_AccountDetail_boolean_boolean() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunks_AccountDetail method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunks_AccountDetail_0args() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunks_Account method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunks_Account_5args() throws Exception {
        // No test.
    }

    /**
     * Test of swapChunks_Account method, of class Grids_Grid.
     */
    @Test
    public void testSwapChunks_Account_4args() throws Exception {
        // No test.
    }

    /**
     * Test of isLoaded method, of class Grids_Grid.
     */
    @Test
    public void testIsLoaded() {
        // No test.
    }

    /**
     * Test of isWorthCaching method, of class Grids_Grid.
     */
    @Test
    public void testIsWorthCaching() {
        // No test.
    }

    /**
     * Test of clearChunk method, of class Grids_Grid.
     */
    @Test
    public void testClearChunk() {
        // No test.
    }

    /**
     * Test of clearChunks method, of class Grids_Grid.
     */
    @Test
    public void testClearChunks() {
        // No test.
    }

    /**
     * Test of getCellIDs method, of class Grids_Grid.
     */
    @Test
    public void testGetCellIDs_3args_1() {
        System.out.println("getCellIDs");
        try {
            {
            int oom = -1;
            RoundingMode rm = RoundingMode.HALF_UP;
            IO_Cache fs = new IO_Cache(ge.files.getGeneratedGridIntDir().getPath());
            Grids_ChunkIntFactorySinglet cifs = new Grids_ChunkIntFactorySinglet(0);
            Grids_ChunkIntFactoryArray cifa = new Grids_ChunkIntFactoryArray();
            int chunkNrows = 64;
            int chunkNcols = 64;
            Grids_GridIntFactory f = new Grids_GridIntFactory(ge, fs, cifs,
                    cifa, chunkNrows, chunkNcols);
            Math_BigRational xmax = Math_BigRational.valueOf(0);
            Math_BigRational ymax = Math_BigRational.valueOf(0);
            Math_BigRational xmin = Math_BigRational.valueOf(640);
            Math_BigRational ymin = Math_BigRational.valueOf(320);
            Math_BigRational cellsize = Math_BigRational.valueOf(1);
            Grids_Dimensions dim = new Grids_Dimensions(xmin, xmax, ymin, ymax, cellsize);
            int nrows = dim.getHeight().divide(dim.getCellsize()).intValue();
            int ncols = dim.getWidth().divide(dim.getCellsize()).intValue();
            Grids_Grid instance = f.create(nrows, ncols, dim);
            // Test 1
            Math_BigRational x = xmin;
            Math_BigRational y = ymin;
            Math_BigRationalSqrt distance = new Math_BigRationalSqrt(cellsize, oom, rm);
            HashSet<Grids_2D_ID_long> expResult = new HashSet<>();
            expResult.add(instance.getCellID(0L, 0L));
            expResult.add(instance.getCellID(-1L, -1L));
            expResult.add(instance.getCellID(-1L, 0L));
            expResult.add(instance.getCellID(0L, -1L));
            Grids_2D_ID_long[] result = instance.getCellIDs(x, y, distance, oom, rm);
            assertTrue(expResult.size() == result.length);
            for (Grids_2D_ID_long id : result) {
                assertTrue(expResult.contains(id));
            }
            }
            
            int oom = -1;
            RoundingMode rm = RoundingMode.HALF_UP;
            IO_Cache fs = new IO_Cache(ge.files.getGeneratedGridIntDir().getPath());
            Grids_ChunkIntFactorySinglet cifs = new Grids_ChunkIntFactorySinglet(0);
            Grids_ChunkIntFactoryArray cifa = new Grids_ChunkIntFactoryArray();
            int chunkNrows = 64;
            int chunkNcols = 64;
            Grids_GridIntFactory f = new Grids_GridIntFactory(ge, fs, cifs,
                    cifa, chunkNrows, chunkNcols);
            Math_BigRational xmin = Math_BigRational.valueOf(0);
            Math_BigRational ymin = Math_BigRational.valueOf(0);
            Math_BigRational xmax = Math_BigRational.valueOf(640);
            Math_BigRational ymax = Math_BigRational.valueOf(320);
            Math_BigRational cellsize = Math_BigRational.valueOf(1);
            Grids_Dimensions dim = new Grids_Dimensions(xmin, xmax, ymin, ymax, cellsize);
            int nrows = dim.getHeight().divide(dim.getCellsize()).intValue();
            int ncols = dim.getWidth().divide(dim.getCellsize()).intValue();
            Grids_Grid instance = f.create(nrows, ncols, dim);
            // Test 1
            Math_BigRational x = xmin;
            Math_BigRational y = ymin;
            Math_BigRationalSqrt distance = new Math_BigRationalSqrt(cellsize, oom, rm);
            HashSet<Grids_2D_ID_long> expResult = new HashSet<>();
            expResult.add(instance.getCellID(0L, 0L));
            expResult.add(instance.getCellID(-1L, -1L));
            expResult.add(instance.getCellID(-1L, 0L));
            expResult.add(instance.getCellID(0L, -1L));
            Grids_2D_ID_long[] result = instance.getCellIDs(x, y, distance, oom, rm);
            assertTrue(expResult.size() == result.length);
            for (Grids_2D_ID_long id : result) {
                assertTrue(expResult.contains(id));
            }
            // Test 2
            x = instance.getCellX(0L);
            y = instance.getCellY(0L);
            distance = new Math_BigRationalSqrt(cellsize.multiply(Math_BigRational.valueOf(7, 5)).pow(2), oom, rm);
            expResult = new HashSet<>();
            expResult.add(instance.getCellID(0L, 1L));
            expResult.add(instance.getCellID(1L, 0L));
            expResult.add(instance.getCellID(0L, 0L));
            expResult.add(instance.getCellID(-1L, 0L));
            expResult.add(instance.getCellID(0L, -1L));
            result = instance.getCellIDs(x, y, distance, oom, rm);
            assertTrue(expResult.size() == result.length);
            for (Grids_2D_ID_long id : result) {
                assertTrue(expResult.contains(id));
            }
            // Test 3
            x = instance.getCellX(0L);
            y = instance.getCellY(0L);
            distance = new Math_BigRationalSqrt(cellsize.multiply(Math_BigRational.valueOf(3, 2)).pow(2), oom, rm);
            expResult = new HashSet<>();
            expResult.add(instance.getCellID(1L, 1L));
            expResult.add(instance.getCellID(0L, 1L));
            expResult.add(instance.getCellID(-1L, 1L));
            expResult.add(instance.getCellID(1L, 0L));
            expResult.add(instance.getCellID(0L, 0L));
            expResult.add(instance.getCellID(-1L, 0L));
            expResult.add(instance.getCellID(1L, -1L));
            expResult.add(instance.getCellID(0L, -1L));
            expResult.add(instance.getCellID(-1L, -1L));
            result = instance.getCellIDs(x, y, distance, oom, rm);
            assertTrue(expResult.size() == result.length);
            for (Grids_2D_ID_long id : result) {
                assertTrue(expResult.contains(id));
            }
            // Test 4
            distance = new Math_BigRationalSqrt(cellsize.multiply(Math_BigRational.valueOf(21, 10)).pow(2), oom, rm);
            expResult = new HashSet<>();
            expResult.add(instance.getCellID(1L, 1L));
            expResult.add(instance.getCellID(0L, 1L));
            expResult.add(instance.getCellID(-1L, 1L));
            expResult.add(instance.getCellID(1L, 0L));
            expResult.add(instance.getCellID(0L, 0L));
            expResult.add(instance.getCellID(-1L, 0L));
            expResult.add(instance.getCellID(1L, -1L));
            expResult.add(instance.getCellID(0L, -1L));
            expResult.add(instance.getCellID(-1L, -1L));
            expResult.add(instance.getCellID(0L, 2L));
            expResult.add(instance.getCellID(0L, -2L));
            expResult.add(instance.getCellID(2L, 0L));
            expResult.add(instance.getCellID(-2L, 0L));
            result = instance.getCellIDs(x, y, distance, oom, rm);
            assertTrue(expResult.size() == result.length);
            for (Grids_2D_ID_long id : result) {
                System.out.println(id);
                assertTrue(expResult.contains(id));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Test of getCellIDs method, of class Grids_Grid covered by {@link #testGetCellIDs_3args_1()}.
     */
    @Test
    public void testGetCellIDs_3args_2() {
        // Not test.
    }

    /**
     * Test of getCellIDs method, of class Grids_Grid covered by {@link #testGetCellIDs_3args_1()}.
     */
    @Test
    public void testGetCellIDs_5args() {
        // Not test.
    }

    /**
     * Test of getNearestCellID method, of class Grids_Grid covered by
     * {@link #testGetNearestCellID_long_long()}.
     */
    @Test
    public void testGetNearestCellID_Math_BigRational_Math_BigRational() {
        // No test
    }

    /**
     * Test of getNearestCellID method, of class Grids_Grid.
     */
    @Test
    public void testGetNearestCellID_long_long() {
        System.out.println("getNearestCellID");
        try {
            IO_Cache fs = new IO_Cache(ge.files.getGeneratedGridIntDir().getPath());
            Grids_ChunkIntFactorySinglet cifs = new Grids_ChunkIntFactorySinglet(0);
            Grids_ChunkIntFactoryArray cifa = new Grids_ChunkIntFactoryArray();
            int chunkNrows = 64;
            int chunkNcols = 64;
            Grids_GridIntFactory f = new Grids_GridIntFactory(ge, fs, cifs,
                    cifa, chunkNrows, chunkNcols);
            Math_BigRational xmin = Math_BigRational.valueOf(0);
            Math_BigRational ymin = Math_BigRational.valueOf(0);
            Math_BigRational xmax = Math_BigRational.valueOf(640);
            Math_BigRational ymax = Math_BigRational.valueOf(320);
            Math_BigRational cellsize = Math_BigRational.valueOf(1);
            Grids_Dimensions dim = new Grids_Dimensions(xmin, xmax, ymin, ymax, cellsize);
            int nrows = dim.getHeight().divide(dim.getCellsize()).intValue();
            int ncols = dim.getWidth().divide(dim.getCellsize()).intValue();
            Grids_Grid instance = f.create(nrows, ncols, dim);
            long r = -1L;
            long c = -1L;
            Grids_2D_ID_long expResult = new Grids_2D_ID_long(0L, 0L);
            Grids_2D_ID_long result = instance.getNearestCellID(r, c);
            assertTrue(expResult.compareTo(result) == 0);
            r = -1L;
            c = 1L;
            expResult = new Grids_2D_ID_long(0L, 1L);
            result = instance.getNearestCellID(r, c);
            assertTrue(expResult.compareTo(result) == 0);
            r = 1L;
            c = -1L;
            expResult = new Grids_2D_ID_long(1L, 0L);
            result = instance.getNearestCellID(r, c);
            assertTrue(expResult.compareTo(result) == 0);
            r = -1L;
            c = ncols;
            expResult = new Grids_2D_ID_long(0L, ncols - 1L);
            result = instance.getNearestCellID(r, c);
            assertTrue(expResult.compareTo(result) == 0);
            r = 0;
            c = ncols - 1;
            expResult = new Grids_2D_ID_long(0L, ncols - 1L);
            result = instance.getNearestCellID(r, c);
            assertTrue(expResult.compareTo(result) == 0);
            r = -1L;
            c = ncols;
            expResult = new Grids_2D_ID_long(0L, ncols - 1L);
            result = instance.getNearestCellID(r, c);
            assertTrue(expResult.compareTo(result) == 0);
            r = nrows;
            c = ncols;
            expResult = new Grids_2D_ID_long(nrows - 1L, ncols - 1L);
            result = instance.getNearestCellID(r, c);
            assertTrue(expResult.compareTo(result) == 0);
            r = nrows;
            c = ncols - 1;
            expResult = new Grids_2D_ID_long(nrows - 1L, ncols - 1L);
            result = instance.getNearestCellID(r, c);
            assertTrue(expResult.compareTo(result) == 0);
            r = nrows - 1L;
            c = ncols;
            expResult = new Grids_2D_ID_long(nrows - 1L, ncols - 1L);
            result = instance.getNearestCellID(r, c);
            assertTrue(expResult.compareTo(result) == 0);
            r = nrows;
            c = -1L;
            expResult = new Grids_2D_ID_long(nrows - 1L, 0L);
            result = instance.getNearestCellID(r, c);
            assertTrue(expResult.compareTo(result) == 0);
            r = nrows;
            c = 0L;
            expResult = new Grids_2D_ID_long(nrows - 1L, 0L);
            result = instance.getNearestCellID(r, c);
            assertTrue(expResult.compareTo(result) == 0);
            r = nrows - 1L;
            c = -1L;
            expResult = new Grids_2D_ID_long(nrows - 1L, 0L);
            result = instance.getNearestCellID(r, c);
            assertTrue(expResult.compareTo(result) == 0);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Test of getNearestCellID method, of class Grids_Grid covered by
     * {@link #testGetNearestCellID_long_long()}.
     */
    @Test
    public void testGetNearestCellID_4args() {
        // No test.
    }

    /**
     * Test of getNextChunkID method, of class Grids_Grid.
     */
    @Test
    public void testGetNextChunkID() {
        // No test.
    }

    /**
     * Test of getPreviousChunkID method, of class Grids_Grid.
     */
    @Test
    public void testGetPreviousChunkID() {
        // No test.
    }

    /**
     * Test of freeSomeMemoryAndResetReserve method, of class Grids_Grid.
     */
    @Test
    public void testFreeSomeMemoryAndResetReserve_HashMap_OutOfMemoryError() throws Exception {
        // No test.
    }

    /**
     * Test of freeSomeMemoryAndResetReserve method, of class Grids_Grid.
     */
    @Test
    public void testFreeSomeMemoryAndResetReserve_3args() throws Exception {
        // No test.
    }

    /**
     * Test of freeSomeMemoryAndResetReserve method, of class Grids_Grid.
     */
    @Test
    public void testFreeSomeMemoryAndResetReserve_Set_OutOfMemoryError() throws Exception {
        // No test.
    }

    /**
     * Test of freeSomeMemoryAndResetReserve method, of class Grids_Grid.
     */
    @Test
    public void testFreeSomeMemoryAndResetReserve_Grids_2D_ID_int_OutOfMemoryError() throws Exception {
        // No test.
    }

    /**
     * Test of freeSomeMemoryAndResetReserve method, of class Grids_Grid.
     */
    @Test
    public void testFreeSomeMemoryAndResetReserve_OutOfMemoryError() throws Exception {
        // No test.
    }

    /**
     * Test of getData method, of class Grids_Grid.
     */
    @Test
    public void testGetData() {
        // No test.
    }

    /**
     * Test of initDimensions method, of class Grids_Grid.
     */
    @Test
    public void testInitDimensions_3args_1() {
        // No test.
    }

    /**
     * Test of initDimensions method, of class Grids_Grid.
     */
    @Test
    public void testInitDimensions_3args_2() {
        // No test.
    }

    /**
     * Test of getChunk method, of class Grids_Grid.
     */
    @Test
    public void testGetChunk_Grids_2D_ID_int() throws Exception {
        // No test.
    }

    /**
     * Test of loadChunk method, of class Grids_Grid.
     */
    @Test
    public void testLoadChunk() throws Exception {
        // No test.
    }

    /**
     * Test of getChunk method, of class Grids_Grid.
     */
    @Test
    public void testGetChunk_int_int() throws Exception {
        // No test.
    }

    /**
     * Test of getChunk method, of class Grids_Grid.
     */
    @Test
    public void testGetChunk_3args() throws Exception {
        // No test.
    }

    /**
     * Test of getStats method, of class Grids_Grid.
     */
    @Test
    public void testGetStats() {
        // No test.
    }

    /**
     * Test of getPathThisFile method, of class Grids_Grid.
     */
    @Test
    public void testGetPathThisFile() {
        // No test.
    }

    /**
     * Test of log method, of class Grids_Grid.
     */
    @Test
    public void testLog() throws Exception {
        // No test.
    }

    /**
     * Test of logBars method, of class Grids_Grid.
     */
    @Test
    public void testLogBars() {
        // No test.;
    }

    /**
     * Test of getBars method, of class Grids_Grid.
     */
    @Test
    public void testGetBars() {
        // No test.
    }

    /**
     * Test of getBarsAndDashes method, of class Grids_Grid.
     */
    @Test
    public void testGetBarsAndDashes() {
        // No test.
    }

    /**
     * Test of getColMarkers method, of class Grids_Grid.
     */
    @Test
    public void testGetColMarkers() {
        // No test.
    }

    /**
     * Test of getDashes method, of class Grids_Grid.
     */
    @Test
    public void testGetDashes() {
        // No test.
    }

    /**
     * Test of getDashes2 method, of class Grids_Grid.
     */
    @Test
    public void testGetDashes2() {
        // No test.
    }

    /**
     * Test of logRow method, of class Grids_Grid.
     */
    @Test
    public void testLogRow() throws Exception {
        // No test.
    }

    /**
     * Test of getSpaces method, of class Grids_Grid.
     */
    @Test
    public void testGetSpaces() {
        // No test.
    }
}
