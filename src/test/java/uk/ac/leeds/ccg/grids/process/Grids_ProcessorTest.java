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
package uk.ac.leeds.ccg.grids.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.io.IO_Cache;
import uk.ac.leeds.ccg.io.IO_Path;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.core.Grids_Strings;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_long;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridNumber;
import uk.ac.leeds.ccg.grids.d2.grid.bd.Grids_GridBD;
import uk.ac.leeds.ccg.grids.d2.grid.bd.Grids_GridBDFactory;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridIntFactory;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridInt;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.math.number.Math_BigRational;

/**
 *
 * @author Andy Turner, AITurner
 * @version 1.0.0
 */
public class Grids_ProcessorTest {

    Generic_Environment env;
    Grids_Environment ge;
    Grids_Processor gp;

    public Grids_ProcessorTest() {
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
     * Test of mask method, of class Grids_Processor.
     * @throws Exception If encountered.
     */
    @Test
    public void testMask() throws Exception {
        System.out.println("mask");
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        // Test 1
        long nrows = 2L;
        long ncols = 3L;
        Grids_GridDouble g0 = (Grids_GridDouble) gfd.create(nrows, ncols);
        g0.setCell(0, 0, 1.0d);
        g0.setCell(0, 1, 1.0d);
        g0.setCell(0, 2, 1.0d);
        g0.setCell(1, 0, 1.0d);
        g0.setCell(1, 1, 1.0d);
        g0.setCell(1, 2, 1.0d);
        long maxNrowsToPrint = 10;
        long maxNcolsToPrint = 10;
        //g0.log(maxNrowsToPrint, maxNcolsToPrint);
        Grids_GridDouble g = (Grids_GridDouble) gfd.create(nrows, ncols);
        //g.setCell(0, 0, 0.0d);
        g.setCell(0, 1, 1.0d);
        g.setCell(0, 2, 1.0d);
        g.setCell(1, 0, 1.0d);
        g.setCell(1, 1, 1.0d);
        g.setCell(1, 2, 1.0d);
        //g.log(maxNrowsToPrint, maxNcolsToPrint);
        Grids_GridDouble mask = (Grids_GridDouble) gfd.create(g);
        //mask.log(maxNrowsToPrint, maxNcolsToPrint);
        gp.mask(g0, mask);
        //g0.log(maxNrowsToPrint, maxNcolsToPrint);
        boolean equal = mask.isSameDimensionsAndValues(g0);
        assertTrue(equal);
        // Test 2
        nrows = 2L;
        ncols = 4L;
        g0 = (Grids_GridDouble) gfd.create(nrows, ncols);
        g0.setCell(0, 0, 1.0d);
        g0.setCell(0, 1, 1.0d);
        g0.setCell(0, 2, 1.0d);
        g0.setCell(0, 3, 1.0d);
        g0.setCell(1, 0, 1.0d);
        g0.setCell(1, 1, 1.0d);
        g0.setCell(1, 2, 1.0d);
        g0.setCell(1, 3, 1.0d);
        maxNrowsToPrint = 10;
        maxNcolsToPrint = 10;
        //g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g = (Grids_GridDouble) gfd.create(nrows, ncols);
        g.setCell(0, 0, 1.0d);
        //g.setCell(0, 1, 1.0d);
        g.setCell(0, 2, 1.0d);
        //g.setCell(0, 3, 1.0d);
        //g.setCell(1, 0, 1.0d);
        g.setCell(1, 1, 1.0d);
        //g.setCell(1, 2, 1.0d);
        g.setCell(1, 3, 1.0d);
        //g.log(maxNrowsToPrint, maxNcolsToPrint);
        mask = (Grids_GridDouble) gfd.create(g);
        //mask.log(maxNrowsToPrint, maxNcolsToPrint);
        gp.mask(g0, mask);
        //g0.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = mask.isSameDimensionsAndValues(g0);
        assertTrue(equal);
    }

//    /**
//     * Test of mask method, of class Grids_Processor.
//     */
//    @Test
//    public void testMask_3args() throws Exception {
//        System.out.println("mask");
//        Grids_GridNumber g = null;
//        BigDecimal min = null;
//        BigDecimal max = null;
//        Grids_Processor instance = null;
//        instance.mask(g, min, max);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of rescale method, of class Grids_Processor.
//     */
//    @Test
//    public void testRescale_4args_1() throws Exception {
//        System.out.println("rescale");
//        Grids_GridNumber g = null;
//        String type = "";
//        double min = 0.0;
//        double max = 0.0;
//        Grids_Processor instance = null;
//        Grids_GridDouble expResult = null;
//        Grids_GridDouble result = instance.rescale(g, type, min, max);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of rescale method, of class Grids_Processor.
//     */
//    @Test
//    public void testRescale_4args_2() throws Exception {
//        System.out.println("rescale");
//        Grids_GridDouble g = null;
//        String type = "";
//        double min = 0.0;
//        double max = 0.0;
//        Grids_Processor instance = null;
//        Grids_GridDouble expResult = null;
//        Grids_GridDouble result = instance.rescale(g, type, min, max);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of rescale method, of class Grids_Processor.
//     */
//    @Test
//    public void testRescale_4args_3() throws Exception {
//        System.out.println("rescale");
//        Grids_GridInt g = null;
//        String type = "";
//        double min = 0.0;
//        double max = 0.0;
//        Grids_Processor instance = null;
//        Grids_GridDouble expResult = null;
//        Grids_GridDouble result = instance.rescale(g, type, min, max);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setLarger method, of class Grids_Processor.
//     */
//    @Test
//    public void testSetLarger() throws Exception {
//        System.out.println("setLarger");
//        Grids_GridDouble g = null;
//        HashSet<Grids_2D_ID_long> cellIDs = null;
//        Grids_Processor instance = null;
//        instance.setLarger(g, cellIDs);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSmaller method, of class Grids_Processor.
//     */
//    @Test
//    public void testSetSmaller() throws Exception {
//        System.out.println("setSmaller");
//        Grids_GridDouble g = null;
//        HashSet<Grids_2D_ID_long> cellIDs = null;
//        Grids_Processor instance = null;
//        instance.setSmaller(g, cellIDs);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addToGrid method, of class Grids_Processor.
//     */
//    @Test
//    public void testAddToGrid_3args_1() throws Exception {
//        System.out.println("addToGrid");
//        Grids_GridNumber g = null;
//        HashSet<Grids_2D_ID_long> cellIDs = null;
//        BigDecimal v = null;
//        Grids_Processor instance = null;
//        instance.addToGrid(g, cellIDs, v);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addToGrid method, of class Grids_Processor.
//     */
//    @Test
//    public void testAddToGrid_Grids_GridNumber_BigDecimal() throws Exception {
//        System.out.println("addToGrid");
//        Grids_GridNumber g = null;
//        BigDecimal v = null;
//        Grids_Processor instance = null;
//        instance.addToGrid(g, v);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addToGrid method, of class Grids_Processor.
//     */
//    @Test
//    public void testAddToGrid_3args_2() throws Exception {
//        System.out.println("addToGrid");
//        Grids_GridNumber g = null;
//        Grids_2D_ID_long[] cellIDs = null;
//        BigDecimal value = null;
//        Grids_Processor instance = null;
//        instance.addToGrid(g, cellIDs, value);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addToGrid method, of class Grids_Processor.
//     */
//    @Test
//    public void testAddToGrid_5args() throws Exception {
//        System.out.println("addToGrid");
//        Grids_GridNumber g = null;
//        Grids_GridNumber g2 = null;
//        BigDecimal w = null;
//        int dp = 0;
//        RoundingMode rm = null;
//        Grids_Processor instance = null;
//        instance.addToGrid(g, g2, w, dp, rm);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addToGrid method, of class Grids_Processor.
//     */
//    @Test
//    public void testAddToGrid_9args() throws Exception {
//        System.out.println("addToGrid");
//        Grids_GridNumber g = null;
//        Grids_GridNumber g2 = null;
//        long startRow = 0L;
//        long startCol = 0L;
//        long endRow = 0L;
//        long endCol = 0L;
//        BigDecimal w = null;
//        int dp = 0;
//        RoundingMode rm = null;
//        Grids_Processor instance = null;
//        instance.addToGrid(g, g2, startRow, startCol, endRow, endCol, w, dp, rm);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addToGrid method, of class Grids_Processor.
//     */
//    @Test
//    public void testAddToGrid_10args() throws Exception {
//        System.out.println("addToGrid");
//        Grids_GridNumber g = null;
//        Grids_GridNumber g2 = null;
//        long startRow = 0L;
//        long startCol = 0L;
//        long endRow = 0L;
//        long endCol = 0L;
//        BigDecimal[] dc = null;
//        BigDecimal w = null;
//        int dp = 0;
//        RoundingMode rm = null;
//        Grids_Processor instance = null;
//        instance.addToGrid(g, g2, startRow, startCol, endRow, endCol, dc, w, dp, rm);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getAP method, of class Grids_Processor.
//     */
//    @Test
//    public void testGetAP() {
//        System.out.println("getAP");
//        BigDecimal[] bounds = null;
//        Grids_GridNumber g2 = null;
//        Grids_2D_ID_long i1 = null;
//        Grids_2D_ID_long i2 = null;
//        Grids_2D_ID_long i3 = null;
//        BigDecimal gC = null;
//        BigDecimal g2CS = null;
//        BigDecimal g2CH = null;
//        int dp = 0;
//        RoundingMode rm = null;
//        Grids_Processor instance = null;
//        BigDecimal expResult = null;
//        BigDecimal result = instance.getAP(bounds, g2, i1, i2, i3, gC, g2CS, g2CH, dp, rm);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getAP13 method, of class Grids_Processor.
//     */
//    @Test
//    public void testGetAP13() {
//        System.out.println("getAP13");
//        BigDecimal[] bounds = null;
//        Grids_GridNumber g2 = null;
//        Grids_2D_ID_long i1 = null;
//        Grids_2D_ID_long i3 = null;
//        BigDecimal gC = null;
//        BigDecimal g2CS = null;
//        BigDecimal g2CH = null;
//        int dp = 0;
//        RoundingMode rm = null;
//        Grids_Processor instance = null;
//        BigDecimal expResult = null;
//        BigDecimal result = instance.getAP13(bounds, g2, i1, i3, gC, g2CS, g2CH, dp, rm);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getAP23 method, of class Grids_Processor.
//     */
//    @Test
//    public void testGetAP23() {
//        System.out.println("getAP23");
//        BigDecimal[] bounds = null;
//        Grids_GridNumber g2 = null;
//        Grids_2D_ID_long i2 = null;
//        Grids_2D_ID_long i3 = null;
//        BigDecimal gC = null;
//        BigDecimal g2CS = null;
//        BigDecimal g2CH = null;
//        int dp = 0;
//        RoundingMode rm = null;
//        Grids_Processor instance = null;
//        BigDecimal expResult = null;
//        BigDecimal result = instance.getAP23(bounds, g2, i2, i3, gC, g2CS, g2CH, dp, rm);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getAP3 method, of class Grids_Processor.
//     */
//    @Test
//    public void testGetAP3() {
//        System.out.println("getAP3");
//        BigDecimal[] bounds = null;
//        Grids_GridNumber g2 = null;
//        Grids_2D_ID_long i3 = null;
//        BigDecimal gC = null;
//        BigDecimal g2CS = null;
//        BigDecimal g2CH = null;
//        int dp = 0;
//        RoundingMode rm = null;
//        Grids_Processor instance = null;
//        BigDecimal expResult = null;
//        BigDecimal result = instance.getAP3(bounds, g2, i3, gC, g2CS, g2CH, dp, rm);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
    /**
     * Test of multiply method, of class Grids_Processor.
     * @throws Exception If encountered.
     */
    @Test
    public void testMultiplyDouble() throws Exception {
        System.out.println("multiplyDouble");
        Double type = 0.0d;
        Grids_GridDoubleFactory gfd = gp.gridFactoryDouble;
        long maxNrowsToPrint = 10;
        long maxNcolsToPrint = 10;
        // Test 1
        int oom = -10;
        //System.out.println("Test 1");
        Grids_GridDouble g0 = (Grids_GridDouble) gfd.create(2, 3);
        //g0.setCell(0, 0, 1.0d);
        g0.setCell(0, 1, 0d);
        g0.setCell(0, 2, 1.0d);
        //g0.setCell(1, 0, 1.0d);
        g0.setCell(1, 1, 2.0d);
        g0.setCell(1, 2, 3.0d);
        //System.out.println("");
        //System.out.println("g0");
        //g0.log(maxNrowsToPrint, maxNcolsToPrint);
        Grids_GridDouble g1 = (Grids_GridDouble) gfd.create(2, 3);
        g1.setCell(0, 0, 1.0d);
        g1.setCell(0, 1, 1.0d); 
        g1.setCell(0, 2, 2.0d);
        g1.setCell(1, 0, 1.0d);
        g1.setCell(1, 1, 4.0d);
        g1.setCell(1, 2, 0.6d);
        //System.out.println("");
        //System.out.println("g1");
        //g1.log(maxNrowsToPrint, maxNcolsToPrint);
        Grids_GridDouble er = (Grids_GridDouble) gfd.create(2, 3);
        //er.setCell(0, 0, 0.0d);
        er.setCell(0, 1, 0.0d); 
        er.setCell(0, 2, 2.0d);
        //er.setCell(1, 0, 1.0d);
        er.setCell(1, 1, 8.0d);
        er.setCell(1, 2, 1.8d);
        //System.out.println("");
        //System.out.println("er");
        //er.log(maxNrowsToPrint, maxNcolsToPrint);
        Grids_GridNumber r = gp.multiply(type, g0, g1, oom);
        //r.log(maxNrowsToPrint, maxNcolsToPrint);
        boolean equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
        // Test 2
        //System.out.println("");
        //System.out.println("Test 2");
        //System.out.println("------");
        g0 = (Grids_GridDouble) gfd.create(2, 3);
        //g0.setCell(0, 0, 1.0d);
        g0.setCell(0, 1, 0d);
        g0.setCell(0, 2, 1.0d);
        //g0.setCell(1, 0, 1.0d);
        g0.setCell(1, 1, 2.0d);
        g0.setCell(1, 2, 3.0d);
        //System.out.println("");
        //System.out.println("g0");
        //maxNrowsToPrint = 10;
        //maxNcolsToPrint = 10;
        //g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g1 = (Grids_GridDouble) gfd.create(2, 3);
        g1.setCell(0, 0, 1.0d);
        g1.setCell(0, 1, 1.0d); 
        g1.setCell(0, 2, 2.0d);
        //g1.setCell(1, 0, 1.0d);
        g1.setCell(1, 1, 4.0d);
        g1.setCell(1, 2, 0.6d);
        //System.out.println("");
        //System.out.println("g1");
        //g1.log(maxNrowsToPrint, maxNcolsToPrint);
        er = (Grids_GridDouble) gfd.create(2, 3);
        //er.setCell(0, 0, 0.0d);
        er.setCell(0, 1, 0.0d); 
        er.setCell(0, 2, 2.0d);
        //er.setCell(1, 0, 1.0d);
        er.setCell(1, 1, 8.0d);
        er.setCell(1, 2, 1.8d);
        //System.out.println("");
        //System.out.println("er");
        //er.log(maxNrowsToPrint, maxNcolsToPrint);
        r = gp.multiply(type, g0, g1, oom);
        //System.out.println("");
        //System.out.println("r");
        //r.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
        // Test 3
//        System.out.println("");
//        System.out.println("Test 3");     
//        System.out.println("------");
        g0 = (Grids_GridDouble) gfd.create(2, 3);
        g0.setCell(0, 0, 4.0d);
        g0.setCell(0, 1, 5.0d);
        //g0.setCell(0, 2, 5.0d);
        //g0.setCell(1, 0, 4.0d);
        g0.setCell(1, 1, 2.0d);
        g0.setCell(1, 2, 3.0d);
//        System.out.println("");
//        System.out.println("g0");
//        g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g1 = (Grids_GridDouble) gfd.create(4, 3);
        g1.setCell(0, 0, 1.0d);
        g1.setCell(0, 1, 4.0d); 
        g1.setCell(0, 2, 1.0d);
        //g1.setCell(1, 0, 1.0d);
        g1.setCell(1, 1, 3.0d);
        g1.setCell(1, 2, 2.0d);
        g1.setCell(2, 0, 3.0d);
        g1.setCell(2, 1, 6.0d);
        g1.setCell(2, 2, 2.0d);
        g1.setCell(3, 0, 1.0d);
        g1.setCell(3, 1, 1.0d);
        g1.setCell(3, 2, 2.0d);
//        System.out.println("");
//        System.out.println("g1");
//        g1.log(maxNrowsToPrint, maxNcolsToPrint);
        er = (Grids_GridDouble) gfd.create(2, 3);
        er.setCell(0, 0, 4.0d);
        er.setCell(0, 1, 20.0d); 
        //er.setCell(0, 2, 2.0d);
        //er.setCell(1, 0, 1.0d);
        er.setCell(1, 1, 6.0d);
        er.setCell(1, 2, 6.0d);
//        System.out.println("");
//        System.out.println("er");
//        er.log(maxNrowsToPrint, maxNcolsToPrint);
        r = gp.multiply(type, g0, g1, oom);
//        System.out.println("");
//        System.out.println("r");
//        r.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
        // Test 4
        //System.out.println("");
        //System.out.println("Test 4");
        //System.out.println("------");
        g0 = (Grids_GridDouble) gfd.create(4, 3);
        g0.setCell(0, 0, 1.0d);
        g0.setCell(0, 1, 4.0d); 
        g0.setCell(0, 2, 1.0d);
        //g0.setCell(1, 0, 1.0d);
        g0.setCell(1, 1, 3.0d);
        g0.setCell(1, 2, 2.0d);
        g0.setCell(2, 0, 3.0d);
        g0.setCell(2, 1, 6.0d);
        g0.setCell(2, 2, 2.0d);
        g0.setCell(3, 0, 1.0d);
        g0.setCell(3, 1, 1.0d);
        g0.setCell(3, 2, 2.0d);
        //System.out.println("");
        //System.out.println("g0");
        //maxNrowsToPrint = 10;
        //maxNcolsToPrint = 10;
        //g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g1 = (Grids_GridDouble) gfd.create(2, 3);
        g1.setCell(0, 0, 4.0d);
        g1.setCell(0, 1, 5.0d);
        //g1.setCell(0, 2, 5.0d);
        //g1.setCell(1, 0, 4.0d);
        g1.setCell(1, 1, 2.0d);
        g1.setCell(1, 2, 3.0d); 
        //System.out.println("");
        //System.out.println("g1");
        //g1.log(maxNrowsToPrint, maxNcolsToPrint);
        er = (Grids_GridDouble) gfd.create(4, 3);
        er.setCell(0, 0, 4.0d);
        er.setCell(0, 1, 20.0d); 
        //er.setCell(0, 2, 2.0d);
        //er.setCell(1, 0, 1.0d);
        er.setCell(1, 1, 6.0d);
        er.setCell(1, 2, 6.0d);
        //er.setCell(2, 2, 6.0d);
        //er.setCell(2, 2, 6.0d);
        //er.setCell(2, 2, 6.0d);
        //er.setCell(3, 2, 6.0d);
        //er.setCell(3, 2, 6.0d);
        //er.setCell(3, 2, 6.0d);
        //System.out.println("");
        //System.out.println("er");
        //er.log(maxNrowsToPrint, maxNcolsToPrint);
        r = gp.multiply(type, g0, g1, oom);
        //System.out.println("");
        //System.out.println("r");
        ///r.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
        // Test 5
        //System.out.println("");
        //System.out.println("Test 5");
        //System.out.println("------");
        g0 = (Grids_GridDouble) gfd.create(2, 3);
        g0.setCell(0, 0, 4.0d);
        g0.setCell(0, 1, 5.0d);
        //g0.setCell(0, 2, 5.0d);
        //g0.setCell(1, 0, 4.0d);
        g0.setCell(1, 1, 2.0d);
        g0.setCell(1, 2, 3.0d);
        //maxNrowsToPrint = 10;
        //maxNcolsToPrint = 10;
        //System.out.println("");
        //System.out.println("g0");
        //g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g1 = gfd.create(4, 3, new Grids_Dimensions(
                Math_BigRational.valueOf(0), Math_BigRational.valueOf(3), 
                Math_BigRational.valueOf(-2), Math_BigRational.valueOf(2), Math_BigRational.ONE));
        g1.setCell(0, 0, 1.0d);
        g1.setCell(0, 1, 4.0d); 
        g1.setCell(0, 2, 1.0d);
        //g1.setCell(1, 0, 1.0d);
        g1.setCell(1, 1, 3.0d);
        g1.setCell(1, 2, 2.0d);
        g1.setCell(2, 0, 3.0d);
        g1.setCell(2, 1, 6.0d);
        g1.setCell(2, 2, 2.0d);
        g1.setCell(3, 0, 1.0d);
        g1.setCell(3, 1, 1.0d);
        g1.setCell(3, 2, 2.0d);
        //System.out.println("");
        //System.out.println("g1");
        //g1.log(maxNrowsToPrint, maxNcolsToPrint);
        er = (Grids_GridDouble) gfd.create(2, 3);
        er.setCell(0, 0, 12.0d);
        er.setCell(0, 1, 30.0d); 
        //er.setCell(0, 2, 2.0d);
        //er.setCell(1, 0, 1.0d);
        er.setCell(1, 1, 2.0d);
        er.setCell(1, 2, 6.0d);
        //System.out.println("");
        //System.out.println("er");
        //er.log(maxNrowsToPrint, maxNcolsToPrint);
        r = gp.multiply(type, g0, g1, oom);
        //System.out.println("");
        //System.out.println("r");
        //r.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
        // Test 6
//        System.out.println("");
//        System.out.println("Test 6");
//        System.out.println("------");
        g0 = (Grids_GridDouble) gfd.create(4, 3);
        g0.setCell(0, 0, 1.0d);
        g0.setCell(0, 1, 4.0d); 
        g0.setCell(0, 2, 1.0d);
        //g0.setCell(1, 0, 1.0d);
        g0.setCell(1, 1, 3.0d);
        g0.setCell(1, 2, 2.0d);
        g0.setCell(2, 0, 3.0d);
        g0.setCell(2, 1, 6.0d);
        g0.setCell(2, 2, 2.0d);
        g0.setCell(3, 0, 1.0d);
        g0.setCell(3, 1, 1.0d);
        g0.setCell(3, 2, 2.0d);
//        System.out.println("");
//        System.out.println("g0");
//        g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g1 = gfd.create(4, 3, new Grids_Dimensions(
                Math_BigRational.valueOf(0), Math_BigRational.valueOf(3), 
                Math_BigRational.valueOf(2), Math_BigRational.valueOf(4), Math_BigRational.ONE));
        g1.setCell(0, 0, 4.0d);
        g1.setCell(0, 1, 5.0d);
        //g1.setCell(0, 2, 5.0d);
        //g1.setCell(1, 0, 4.0d);
        g1.setCell(1, 1, 2.0d);
        g1.setCell(1, 2, 3.0d); 
//        System.out.println("");
//        System.out.println("g1");
//        g1.log(maxNrowsToPrint, maxNcolsToPrint);
        er = (Grids_GridDouble) gfd.create(4, 3);
        //er.setCell(0, 0, 4.0d);
        //er.setCell(0, 1, 2.0d); 
        //er.setCell(0, 2, 6.0d);
        //er.setCell(1, 0, 1.0d);
        //er.setCell(1, 1, 6.0d);
        //er.setCell(1, 2, 6.0d);
        er.setCell(2, 0, 12.0d);
        er.setCell(2, 1, 30.0d);
        //er.setCell(2, 2, 6.0d);
        //er.setCell(3, 0, 6.0d);
        er.setCell(3, 1, 2.0d);
        er.setCell(3, 2, 6.0d);
//        System.out.println("");
//        System.out.println("er");
//        er.log(maxNrowsToPrint, maxNcolsToPrint);
        r = gp.multiply(type, g0, g1, oom);
//        System.out.println("");
//        System.out.println("r");
//        r.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
        // Test 7
//        System.out.println("");
//        System.out.println("Test 7");
//        System.out.println("------");
        g0 = (Grids_GridDouble) gfd.create(4, 3);
        g0.setCell(0, 0, 1.0d);
        //g0.setCell(0, 1, 4.0d); 
        //g0.setCell(0, 2, 1.0d);
        g0.setCell(1, 0, 5.0d);
        g0.setCell(1, 1, 2.0d);
        g0.setCell(1, 2, 4.0d);
        g0.setCell(2, 0, 4.0d);
        g0.setCell(2, 1, 3.0d);
        g0.setCell(2, 2, 5.0d);
        //g0.setCell(3, 0, 1.0d);
        g0.setCell(3, 1, 2.0d);
        g0.setCell(3, 2, 1.0d);
//        System.out.println("");
//        System.out.println("g0");
//        g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g1 = gfd.create(2, 1, new Grids_Dimensions(
                Math_BigRational.valueOf(1), Math_BigRational.valueOf(2), 
                Math_BigRational.valueOf(1), Math_BigRational.valueOf(3), Math_BigRational.ONE));
        g1.setCell(0, 0, 6.0d);
        g1.setCell(1, 0, 2.0d);
//        System.out.println("");
//        System.out.println("g1");
//        g1.log(maxNrowsToPrint, maxNcolsToPrint);
        er = (Grids_GridDouble) gfd.create(4, 3);
        //er.setCell(0, 0, 4.0d);
        //er.setCell(0, 1, 2.0d); 
        //er.setCell(0, 2, 6.0d);
        //er.setCell(1, 0, 1.0d);
        er.setCell(1, 1, 12.0d);
        //er.setCell(1, 2, 6.0d);
        //er.setCell(2, 0, 12.0d);
        er.setCell(2, 1, 6.0d);
        //er.setCell(2, 2, 6.0d);
        //er.setCell(3, 0, 6.0d);
        //er.setCell(3, 1, 2.0d);
        //er.setCell(3, 2, 6.0d);
//        System.out.println("");
//        System.out.println("er");
//        er.log(maxNrowsToPrint, maxNcolsToPrint);
        r = gp.multiply(type, g0, g1, oom);
//        System.out.println("");
//        System.out.println("r");
//        r.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
    }

    /**
     * Test of multiply method, of class Grids_Processor.
     * @throws Exception If encountered.
     */
    @Test
    public void testMultiplyBigDecimal() throws Exception {
        System.out.println("multiplyBigDecimal");
        BigDecimal type = BigDecimal.ZERO;
        Grids_GridBDFactory gf = gp.gridFactoryBD;
        long maxNrowsToPrint = 10;
        long maxNcolsToPrint = 10;
        int oom = -10;
        Grids_GridBD g0;
        Grids_GridBD g1;
        Grids_GridBD er;
        Grids_GridNumber r;
        boolean equal;
        // Test 1
        //System.out.println("Test 1");
        g0 = (Grids_GridBD) gf.create(2, 3);
        //g0.setCell(0, 0, BigDecimal.ONE);
        g0.setCell(0, 1, BigDecimal.ZERO);
        g0.setCell(0, 2, BigDecimal.ONE);
        //g0.setCell(1, 0, BigDecimal.ONE);
        g0.setCell(1, 1, BigDecimal.valueOf(2));
        g0.setCell(1, 2, BigDecimal.valueOf(3));
        //System.out.println("");
        //System.out.println("g0");
        //g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g1 = (Grids_GridBD) gf.create(2, 3);
        g1.setCell(0, 0, BigDecimal.ONE);
        g1.setCell(0, 1, BigDecimal.ONE); 
        g1.setCell(0, 2, BigDecimal.valueOf(2));
        g1.setCell(1, 0, BigDecimal.ONE);
        g1.setCell(1, 1, BigDecimal.valueOf(4));
        g1.setCell(1, 2, BigDecimal.valueOf(0.6));
        //System.out.println("");
        //System.out.println("g1");
        //g1.log(maxNrowsToPrint, maxNcolsToPrint);
        er = (Grids_GridBD) gf.create(2, 3);
        //er.setCell(0, 0, BigDecimal.ZERO);
        er.setCell(0, 1, BigDecimal.ZERO); 
        er.setCell(0, 2, BigDecimal.valueOf(2));
        //er.setCell(1, 0, BigDecimal.ONE;
        er.setCell(1, 1, BigDecimal.valueOf(8));
        er.setCell(1, 2, BigDecimal.valueOf(1.8));
        //System.out.println("");
        //System.out.println("er");
        //er.log(maxNrowsToPrint, maxNcolsToPrint);
        r = gp.multiply(type, g0, g1, oom);
        //r.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
        // Test 2
        //System.out.println("");
        //System.out.println("Test 2");
        //System.out.println("------");
        g0 = (Grids_GridBD) gf.create(2, 3);
        //g0.setCell(0, 0, BigDecimal.ONE);
        g0.setCell(0, 1, BigDecimal.ZERO);
        g0.setCell(0, 2, BigDecimal.ONE);
        //g0.setCell(1, 0, BigDecimal.ONE);
        g0.setCell(1, 1, BigDecimal.valueOf(2));
        g0.setCell(1, 2, BigDecimal.valueOf(3.0d));
        //System.out.println("");
        //System.out.println("g0");
        //g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g1 = (Grids_GridBD) gf.create(2, 3);
        g1.setCell(0, 0, BigDecimal.ONE);
        g1.setCell(0, 1, BigDecimal.ONE); 
        g1.setCell(0, 2, BigDecimal.valueOf(2));
        //g1.setCell(1, 0, BigDecimal.ONE);
        g1.setCell(1, 1, BigDecimal.valueOf(4.0d));
        g1.setCell(1, 2, BigDecimal.valueOf(0.6d));
        //System.out.println("");
        //System.out.println("g1");
        //g1.log(maxNrowsToPrint, maxNcolsToPrint);
        er = (Grids_GridBD) gf.create(2, 3);
        //er.setCell(0, 0, BigDecimal.ZERO);
        er.setCell(0, 1, BigDecimal.ZERO); 
        er.setCell(0, 2, BigDecimal.valueOf(2));
        //er.setCell(1, 0, BigDecimal.ONE);
        er.setCell(1, 1, BigDecimal.valueOf(8.0d));
        er.setCell(1, 2, BigDecimal.valueOf(1.8d));
        //System.out.println("");
        //System.out.println("er");
        //er.log(maxNrowsToPrint, maxNcolsToPrint);
        r = gp.multiply(type, g0, g1, oom);
        //System.out.println("");
        //System.out.println("r");
        //r.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
        // Test 3
//        System.out.println("");
//        System.out.println("Test 3");     
//        System.out.println("------");
        g0 = (Grids_GridBD) gf.create(2, 3);
        g0.setCell(0, 0, BigDecimal.valueOf(4.0d));
        g0.setCell(0, 1, BigDecimal.valueOf(5.0d));
        //g0.setCell(0, 2, BigDecimal.valueOf(5.0d));
        //g0.setCell(1, 0, BigDecimal.valueOf(4.0d));
        g0.setCell(1, 1, BigDecimal.valueOf(2.0d));
        g0.setCell(1, 2, BigDecimal.valueOf(3.0d));
//        System.out.println("");
//        System.out.println("g0");
//        g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g1 = (Grids_GridBD) gf.create(4, 3);
        g1.setCell(0, 0, BigDecimal.valueOf(1.0d));
        g1.setCell(0, 1, BigDecimal.valueOf(4.0d)); 
        g1.setCell(0, 2, BigDecimal.valueOf(1.0d));
        //g1.setCell(1, 0, BigDecimal.valueOf(1.0d));
        g1.setCell(1, 1, BigDecimal.valueOf(3.0d));
        g1.setCell(1, 2, BigDecimal.valueOf(2.0d));
        g1.setCell(2, 0, BigDecimal.valueOf(3.0d));
        g1.setCell(2, 1, BigDecimal.valueOf(6.0d));
        g1.setCell(2, 2, BigDecimal.valueOf(2.0d));
        g1.setCell(3, 0, BigDecimal.valueOf(1.0d));
        g1.setCell(3, 1, BigDecimal.valueOf(1.0d));
        g1.setCell(3, 2, BigDecimal.valueOf(2.0d));
//        System.out.println("");
//        System.out.println("g1");
//        g1.log(maxNrowsToPrint, maxNcolsToPrint);
        er = (Grids_GridBD) gf.create(2, 3);
        er.setCell(0, 0, BigDecimal.valueOf(4.0d));
        er.setCell(0, 1, BigDecimal.valueOf(20.0d)); 
        //er.setCell(0, 2, BigDecimal.valueOf(2.0d));
        //er.setCell(1, 0, BigDecimal.valueOf(1.0d));
        er.setCell(1, 1, BigDecimal.valueOf(6.0d));
        er.setCell(1, 2, BigDecimal.valueOf(6.0d));
//        System.out.println("");
//        System.out.println("er");
//        er.log(maxNrowsToPrint, maxNcolsToPrint);
        r = gp.multiply(type, g0, g1, oom);
//        System.out.println("");
//        System.out.println("r");
//        r.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
        // Test 4
        //System.out.println("");
        //System.out.println("Test 4");
        //System.out.println("------");
        g0 = (Grids_GridBD) gf.create(4, 3);
        g0.setCell(0, 0, BigDecimal.valueOf(1.0d));
        g0.setCell(0, 1, BigDecimal.valueOf(4.0d)); 
        g0.setCell(0, 2, BigDecimal.valueOf(1.0d));
        //g0.setCell(1, 0, BigDecimal.valueOf(1.0d));
        g0.setCell(1, 1, BigDecimal.valueOf(3.0d));
        g0.setCell(1, 2, BigDecimal.valueOf(2.0d));
        g0.setCell(2, 0, BigDecimal.valueOf(3.0d));
        g0.setCell(2, 1, BigDecimal.valueOf(6.0d));
        g0.setCell(2, 2, BigDecimal.valueOf(2.0d));
        g0.setCell(3, 0, BigDecimal.valueOf(1.0d));
        g0.setCell(3, 1, BigDecimal.valueOf(1.0d));
        g0.setCell(3, 2, BigDecimal.valueOf(2.0d));
        //System.out.println("");
        //System.out.println("g0");
        //maxNrowsToPrint = 10;
        //maxNcolsToPrint = 10;
        //g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g1 = (Grids_GridBD) gf.create(2, 3);
        g1.setCell(0, 0, BigDecimal.valueOf(4.0d));
        g1.setCell(0, 1, BigDecimal.valueOf(5.0d));
        //g1.setCell(0, 2, BigDecimal.valueOf(5.0d));
        //g1.setCell(1, 0, BigDecimal.valueOf(4.0d));
        g1.setCell(1, 1, BigDecimal.valueOf(2.0d));
        g1.setCell(1, 2, BigDecimal.valueOf(3.0d)); 
        //System.out.println("");
        //System.out.println("g1");
        //g1.log(maxNrowsToPrint, maxNcolsToPrint);
        er = (Grids_GridBD) gf.create(4, 3);
        er.setCell(0, 0, BigDecimal.valueOf(4.0d));
        er.setCell(0, 1, BigDecimal.valueOf(20.0d)); 
        //er.setCell(0, 2, BigDecimal.valueOf(2.0d));
        //er.setCell(1, 0, BigDecimal.valueOf(1.0d));
        er.setCell(1, 1, BigDecimal.valueOf(6.0d));
        er.setCell(1, 2, BigDecimal.valueOf(6.0d));
        //er.setCell(2, 2, BigDecimal.valueOf(6.0d));
        //er.setCell(2, 2, BigDecimal.valueOf(6.0d));
        //er.setCell(2, 2, BigDecimal.valueOf(6.0d));
        //er.setCell(3, 2, BigDecimal.valueOf(6.0d));
        //er.setCell(3, 2, BigDecimal.valueOf(6.0d));
        //er.setCell(3, 2, BigDecimal.valueOf(6.0d));
        //System.out.println("");
        //System.out.println("er");
        //er.log(maxNrowsToPrint, maxNcolsToPrint);
        r = gp.multiply(type, g0, g1, oom);
        //System.out.println("");
        //System.out.println("r");
        ///r.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
        // Test 5
        //System.out.println("");
        //System.out.println("Test 5");
        //System.out.println("------");
        g0 = (Grids_GridBD) gf.create(2, 3);
        g0.setCell(0, 0, BigDecimal.valueOf(4.0d));
        g0.setCell(0, 1, BigDecimal.valueOf(5.0d));
        //g0.setCell(0, 2, BigDecimal.valueOf(5.0d));
        //g0.setCell(1, 0, BigDecimal.valueOf(4.0d));
        g0.setCell(1, 1, BigDecimal.valueOf(2.0d));
        g0.setCell(1, 2, BigDecimal.valueOf(3.0d));
        //maxNrowsToPrint = 10;
        //maxNcolsToPrint = 10;
        //System.out.println("");
        //System.out.println("g0");
        //g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g1 = gf.create(4, 3, new Grids_Dimensions(
                Math_BigRational.valueOf(0), Math_BigRational.valueOf(3), 
                Math_BigRational.valueOf(-2), Math_BigRational.valueOf(2), Math_BigRational.ONE));
        g1.setCell(0, 0, BigDecimal.valueOf(1.0d));
        g1.setCell(0, 1, BigDecimal.valueOf(4.0d)); 
        g1.setCell(0, 2, BigDecimal.valueOf(1.0d));
        //g1.setCell(1, 0, BigDecimal.valueOf(1.0d));
        g1.setCell(1, 1, BigDecimal.valueOf(3.0d));
        g1.setCell(1, 2, BigDecimal.valueOf(2.0d));
        g1.setCell(2, 0, BigDecimal.valueOf(3.0d));
        g1.setCell(2, 1, BigDecimal.valueOf(6.0d));
        g1.setCell(2, 2, BigDecimal.valueOf(2.0d));
        g1.setCell(3, 0, BigDecimal.valueOf(1.0d));
        g1.setCell(3, 1, BigDecimal.valueOf(1.0d));
        g1.setCell(3, 2, BigDecimal.valueOf(2.0d));
        //System.out.println("");
        //System.out.println("g1");
        //g1.log(maxNrowsToPrint, maxNcolsToPrint);
        er = (Grids_GridBD) gf.create(2, 3);
        er.setCell(0, 0, BigDecimal.valueOf(12.0d));
        er.setCell(0, 1, BigDecimal.valueOf(30.0d)); 
        //er.setCell(0, 2, BigDecimal.valueOf(2.0d));
        //er.setCell(1, 0, BigDecimal.valueOf(1.0d));
        er.setCell(1, 1, BigDecimal.valueOf(2.0d));
        er.setCell(1, 2, BigDecimal.valueOf(6.0d));
        //System.out.println("");
        //System.out.println("er");
        //er.log(maxNrowsToPrint, maxNcolsToPrint);
        r = gp.multiply(type, g0, g1, oom);
        //System.out.println("");
        //System.out.println("r");
        //r.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
        // Test 6
//        System.out.println("");
//        System.out.println("Test 6");
//        System.out.println("------");
        g0 = (Grids_GridBD) gf.create(4, 3);
        g0.setCell(0, 0, BigDecimal.valueOf(1.0d));
        g0.setCell(0, 1, BigDecimal.valueOf(4.0d)); 
        g0.setCell(0, 2, BigDecimal.valueOf(1.0d));
        //g0.setCell(1, 0, BigDecimal.valueOf(1.0d));
        g0.setCell(1, 1, BigDecimal.valueOf(3.0d));
        g0.setCell(1, 2, BigDecimal.valueOf(2.0d));
        g0.setCell(2, 0, BigDecimal.valueOf(3.0d));
        g0.setCell(2, 1, BigDecimal.valueOf(6.0d));
        g0.setCell(2, 2, BigDecimal.valueOf(2.0d));
        g0.setCell(3, 0, BigDecimal.valueOf(1.0d));
        g0.setCell(3, 1, BigDecimal.valueOf(1.0d));
        g0.setCell(3, 2, BigDecimal.valueOf(2.0d));
//        System.out.println("");
//        System.out.println("g0");
//        g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g1 = gf.create(4, 3, new Grids_Dimensions(
                Math_BigRational.valueOf(0), Math_BigRational.valueOf(3), 
                Math_BigRational.valueOf(2), Math_BigRational.valueOf(4), Math_BigRational.ONE));
        g1.setCell(0, 0, BigDecimal.valueOf(4.0d));
        g1.setCell(0, 1, BigDecimal.valueOf(5.0d));
        //g1.setCell(0, 2, BigDecimal.valueOf(5.0d));
        //g1.setCell(1, 0, BigDecimal.valueOf(4.0d));
        g1.setCell(1, 1, BigDecimal.valueOf(2.0d));
        g1.setCell(1, 2, BigDecimal.valueOf(3.0d)); 
//        System.out.println("");
//        System.out.println("g1");
//        g1.log(maxNrowsToPrint, maxNcolsToPrint);
        er = (Grids_GridBD) gf.create(4, 3);
        //er.setCell(0, 0, BigDecimal.valueOf(4.0d));
        //er.setCell(0, 1, BigDecimal.valueOf(2.0d)); 
        //er.setCell(0, 2, BigDecimal.valueOf(6.0d));
        //er.setCell(1, 0, BigDecimal.valueOf(1.0d));
        //er.setCell(1, 1, BigDecimal.valueOf(6.0d));
        //er.setCell(1, 2, BigDecimal.valueOf(6.0d));
        er.setCell(2, 0, BigDecimal.valueOf(12.0d));
        er.setCell(2, 1, BigDecimal.valueOf(30.0d));
        //er.setCell(2, 2, BigDecimal.valueOf(6.0d));
        //er.setCell(3, 0, BigDecimal.valueOf(6.0d));
        er.setCell(3, 1, BigDecimal.valueOf(2.0d));
        er.setCell(3, 2, BigDecimal.valueOf(6.0d));
//        System.out.println("");
//        System.out.println("er");
//        er.log(maxNrowsToPrint, maxNcolsToPrint);
        r = gp.multiply(type, g0, g1, oom);
//        System.out.println("");
//        System.out.println("r");
//        r.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
        // Test 7
//        System.out.println("");
//        System.out.println("Test 7");
//        System.out.println("------");
        g0 = (Grids_GridBD) gf.create(4, 3);
        g0.setCell(0, 0, BigDecimal.valueOf(1.0d));
        //g0.setCell(0, 1, BigDecimal.valueOf(4.0d)); 
        //g0.setCell(0, 2, BigDecimal.valueOf(1.0d));
        g0.setCell(1, 0, BigDecimal.valueOf(5.0d));
        g0.setCell(1, 1, BigDecimal.valueOf(2.0d));
        g0.setCell(1, 2, BigDecimal.valueOf(4.0d));
        g0.setCell(2, 0, BigDecimal.valueOf(4.0d));
        g0.setCell(2, 1, BigDecimal.valueOf(3.0d));
        g0.setCell(2, 2, BigDecimal.valueOf(5.0d));
        //g0.setCell(3, 0, BigDecimal.valueOf(1.0d));
        g0.setCell(3, 1, BigDecimal.valueOf(2.0d));
        g0.setCell(3, 2, BigDecimal.valueOf(1.0d));
//        System.out.println("");
//        System.out.println("g0");
//        maxNrowsToPrint = 10;
//        maxNcolsToPrint = 10;
//        g0.log(maxNrowsToPrint, maxNcolsToPrint);
        g1 = gf.create(2, 1, new Grids_Dimensions(
                Math_BigRational.valueOf(1), Math_BigRational.valueOf(2), 
                Math_BigRational.valueOf(1), Math_BigRational.valueOf(3), Math_BigRational.ONE));
        g1.setCell(0, 0, BigDecimal.valueOf(6.0d));
        g1.setCell(1, 0, BigDecimal.valueOf(2.0d));
//        System.out.println("");
//        System.out.println("g1");
//        g1.log(maxNrowsToPrint, maxNcolsToPrint);
        er = (Grids_GridBD) gf.create(4, 3);
        //er.setCell(0, 0, BigDecimal.valueOf(4.0d));
        //er.setCell(0, 1, 2.0d); 
        //er.setCell(0, 2, BigDecimal.valueOf(6.0d));
        //er.setCell(1, 0, BigDecimal.valueOf(1.0d));
        er.setCell(1, 1, BigDecimal.valueOf(12.0d));
        //er.setCell(1, 2, BigDecimal.valueOf(6.0d));
        //er.setCell(2, 0, 12.0d);
        er.setCell(2, 1, BigDecimal.valueOf(6.0d));
        //er.setCell(2, 2, BigDecimal.valueOf(6.0d));
        //er.setCell(3, 0, BigDecimal.valueOf(6.0d));
        //er.setCell(3, 1, 2.0d);
        //er.setCell(3, 2, BigDecimal.valueOf(6.0d));
//        System.out.println("");
//        System.out.println("er");
//        er.log(maxNrowsToPrint, maxNcolsToPrint);
        r = gp.multiply(type, g0, g1, oom);
//        System.out.println("");
//        System.out.println("r");
//        r.log(maxNrowsToPrint, maxNcolsToPrint);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);
        //Test for visual output grids
        g0 = (Grids_GridBD) gf.create(10, 10);
        //g0.setCell(0, 0, BigDecimal.valueOf(1.0d));
        g0.setCell(0, 1, BigDecimal.valueOf(2.0d)); 
        g0.setCell(0, 2, BigDecimal.valueOf(1.6d));
        g0.setCell(0, 3, BigDecimal.valueOf(12.0d));
        g0.setCell(0, 4, BigDecimal.valueOf(101.0d));
        g0.setCell(0, 5, BigDecimal.valueOf(2003.0d));
        g0.setCell(0, 6, BigDecimal.valueOf(30004.0d));
        g0.setCell(0, 7, BigDecimal.valueOf(300006.0d));
        g0.setCell(0, 8, BigDecimal.valueOf(3000007.0d));
        g0.setCell(0, 9, BigDecimal.valueOf(30000008.0d));
        g0.setCell(1, 0, BigDecimal.valueOf(1.0d));
        g0.setCell(1, 1, BigDecimal.valueOf(10.0d)); 
        g0.setCell(1, 2, BigDecimal.valueOf(100.0d));
        g0.setCell(1, 3, BigDecimal.valueOf(1000.0d));
        g0.setCell(1, 4, BigDecimal.valueOf(10000.0d));
        g0.setCell(1, 5, BigDecimal.valueOf(100.0d));
        g0.setCell(1, 6, BigDecimal.valueOf(100.0d));
        g0.setCell(1, 7, BigDecimal.valueOf(100.0d));
        g0.setCell(1, 8, BigDecimal.valueOf(100.0d));
        //g0.setCell(1, 9, BigDecimal.valueOf(100.0d));
        g0.setCell(2, 0, BigDecimal.valueOf(3.0d));
//        g0.setCell(2, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(2, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(2, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(2, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(2, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(2, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(2, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(2, 8, BigDecimal.valueOf(2.0d));
        g0.setCell(2, 9, BigDecimal.valueOf(3.0d));
        g0.setCell(3, 0, BigDecimal.valueOf(7.0d));
//        g0.setCell(3, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(3, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(3, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(3, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(3, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(3, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(3, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(3, 8, BigDecimal.valueOf(2.0d));
        g0.setCell(3, 9, BigDecimal.valueOf(4.0d));
        g0.setCell(4, 0, BigDecimal.valueOf(6.0d));
////        g0.setCell(4, 1, BigDecimal.valueOf(4.0d)); 
////        g0.setCell(4, 2, BigDecimal.valueOf(1.0d));
////        g0.setCell(4, 3, BigDecimal.valueOf(1.0d));
////        g0.setCell(4, 4, BigDecimal.valueOf(3.0d));
////        g0.setCell(4, 5, BigDecimal.valueOf(2.0d));
////        g0.setCell(4, 6, BigDecimal.valueOf(3.0d));
////        g0.setCell(4, 7, BigDecimal.valueOf(6.0d));
////        g0.setCell(4, 8, BigDecimal.valueOf(2.0d));
        g0.setCell(4, 9, BigDecimal.valueOf(5.0d));
        g0.setCell(5, 0, BigDecimal.valueOf(5.0d));
//        g0.setCell(5, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(5, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(5, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(5, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(5, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(5, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(5, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(5, 8, BigDecimal.valueOf(2.0d));
        g0.setCell(5, 9, BigDecimal.valueOf(6.0d));
        g0.setCell(6, 0, BigDecimal.valueOf(4.0d));
//        g0.setCell(6, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(6, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(6, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(6, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(6, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(6, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(6, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(6, 8, BigDecimal.valueOf(2.0d));
        g0.setCell(6, 9, BigDecimal.valueOf(7.0d));
        g0.setCell(7, 0, BigDecimal.valueOf(3.0d));
//        g0.setCell(7, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(7, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(7, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(7, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(7, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(7, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(7, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(7, 8, BigDecimal.valueOf(2.0d));
        g0.setCell(7, 9, BigDecimal.valueOf(8.0d));
        g0.setCell(8, 0, BigDecimal.valueOf(2.0d));
//        g0.setCell(8, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(8, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(8, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(8, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(8, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(8, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(8, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(8, 8, BigDecimal.valueOf(2.0d));
        g0.setCell(8, 9, BigDecimal.valueOf(9.0d));
        g0.setCell(9, 0, BigDecimal.valueOf(1.0d));
        g0.setCell(9, 1, BigDecimal.valueOf(2.0d)); 
        g0.setCell(9, 2, BigDecimal.valueOf(3.0d));
        g0.setCell(9, 3, BigDecimal.valueOf(4.0d));
        g0.setCell(9, 4, BigDecimal.valueOf(5.0d));
        g0.setCell(9, 5, BigDecimal.valueOf(6.0d));
        g0.setCell(9, 6, BigDecimal.valueOf(7.0d));
        g0.setCell(9, 7, BigDecimal.valueOf(8.0d));
        g0.setCell(9, 8, BigDecimal.valueOf(9.0d));
        g0.setCell(9, 9, BigDecimal.valueOf(10.0d));
        g1 = (Grids_GridBD) gf.create(10, 10);
//        g1.setCell(0, 0, BigDecimal.valueOf(1.0d));
//        g1.setCell(0, 1, BigDecimal.valueOf(2.0d)); 
//        g1.setCell(0, 2, BigDecimal.valueOf(1.6d));
//        g1.setCell(0, 3, BigDecimal.valueOf(12.0d));
//        g1.setCell(0, 4, BigDecimal.valueOf(101.0d));
//        g1.setCell(0, 5, BigDecimal.valueOf(2003.0d));
//        g1.setCell(0, 6, BigDecimal.valueOf(30004.0d));
//        g1.setCell(0, 7, BigDecimal.valueOf(300006.0d));
//        g1.setCell(0, 8, BigDecimal.valueOf(3000007.0d));
//        g1.setCell(0, 9, BigDecimal.valueOf(30000008.0d));
        g1.setCell(1, 0, BigDecimal.valueOf(1.2d));
        g1.setCell(1, 1, BigDecimal.valueOf(1.2d)); 
        g1.setCell(1, 2, BigDecimal.valueOf(1.2d));
        g1.setCell(1, 3, BigDecimal.valueOf(1.2d));
        g1.setCell(1, 4, BigDecimal.valueOf(1.2d));
        g1.setCell(1, 5, BigDecimal.valueOf(1.04d));
        g1.setCell(1, 6, BigDecimal.valueOf(1.006d));
        g1.setCell(1, 7, BigDecimal.valueOf(1.0008d));
        g1.setCell(1, 8, BigDecimal.valueOf(1.00009d));
        g1.setCell(1, 9, BigDecimal.valueOf(1.000001d));
        g1.setCell(2, 0, BigDecimal.valueOf(3.0d));
//        g1.setCell(2, 1, BigDecimal.valueOf(4.0d)); 
//        g1.setCell(2, 2, BigDecimal.valueOf(1.0d));
//        g1.setCell(2, 3, BigDecimal.valueOf(1.0d));
//        g1.setCell(2, 4, BigDecimal.valueOf(3.0d));
//        g1.setCell(2, 5, BigDecimal.valueOf(2.0d));
//        g1.setCell(2, 6, BigDecimal.valueOf(3.0d));
//        g1.setCell(2, 7, BigDecimal.valueOf(6.0d));
//        g1.setCell(2, 8, BigDecimal.valueOf(2.0d));
        g1.setCell(2, 9, BigDecimal.valueOf(3.0d));
        g1.setCell(3, 0, BigDecimal.valueOf(7.0d));
//        g1.setCell(3, 1, BigDecimal.valueOf(4.0d)); 
//        g1.setCell(3, 2, BigDecimal.valueOf(1.0d));
//        g1.setCell(3, 3, BigDecimal.valueOf(1.0d));
//        g1.setCell(3, 4, BigDecimal.valueOf(3.0d));
//        g1.setCell(3, 5, BigDecimal.valueOf(2.0d));
//        g1.setCell(3, 6, BigDecimal.valueOf(3.0d));
//        g1.setCell(3, 7, BigDecimal.valueOf(6.0d));
//        g1.setCell(3, 8, BigDecimal.valueOf(2.0d));
        g1.setCell(3, 9, BigDecimal.valueOf(4.0d));
        g1.setCell(4, 0, BigDecimal.valueOf(6.0d));
////        g0.setCell(4, 1, BigDecimal.valueOf(4.0d)); 
////        g0.setCell(4, 2, BigDecimal.valueOf(1.0d));
////        g0.setCell(4, 3, BigDecimal.valueOf(1.0d));
////        g0.setCell(4, 4, BigDecimal.valueOf(3.0d));
////        g0.setCell(4, 5, BigDecimal.valueOf(2.0d));
////        g0.setCell(4, 6, BigDecimal.valueOf(3.0d));
////        g0.setCell(4, 7, BigDecimal.valueOf(6.0d));
////        g0.setCell(4, 8, BigDecimal.valueOf(2.0d));
        g1.setCell(4, 9, BigDecimal.valueOf(5.0d));
        g1.setCell(5, 0, BigDecimal.valueOf(5.0d));
//        g0.setCell(5, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(5, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(5, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(5, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(5, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(5, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(5, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(5, 8, BigDecimal.valueOf(2.0d));
        g1.setCell(5, 9, BigDecimal.valueOf(6.0d));
        g1.setCell(6, 0, BigDecimal.valueOf(4.0d));
//        g0.setCell(6, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(6, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(6, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(6, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(6, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(6, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(6, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(6, 8, BigDecimal.valueOf(2.0d));
        g1.setCell(6, 9, BigDecimal.valueOf(7.0d));
        g1.setCell(7, 0, BigDecimal.valueOf(3.0d));
//        g0.setCell(7, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(7, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(7, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(7, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(7, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(7, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(7, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(7, 8, BigDecimal.valueOf(2.0d));
        g1.setCell(7, 9, BigDecimal.valueOf(8.0d));
        g1.setCell(8, 0, BigDecimal.valueOf(2.0d));
//        g0.setCell(8, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(8, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(8, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(8, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(8, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(8, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(8, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(8, 8, BigDecimal.valueOf(2.0d));
        g1.setCell(8, 9, BigDecimal.valueOf(9.0d));
        g1.setCell(9, 0, BigDecimal.valueOf(1.0d));
        g1.setCell(9, 1, BigDecimal.valueOf(2.0d)); 
        g1.setCell(9, 2, BigDecimal.valueOf(3.0d));
        g1.setCell(9, 3, BigDecimal.valueOf(4.0d));
        g1.setCell(9, 4, BigDecimal.valueOf(5.0d));
        g1.setCell(9, 5, BigDecimal.valueOf(6.0d));
        g1.setCell(9, 6, BigDecimal.valueOf(7.0d));
        g1.setCell(9, 7, BigDecimal.valueOf(8.0d));
        g1.setCell(9, 8, BigDecimal.valueOf(9.0d));
        g1.setCell(9, 9, BigDecimal.valueOf(10.0d));
        er = (Grids_GridBD) gf.create(10, 10);
//        g1.setCell(0, 0, BigDecimal.valueOf(1.0d));
//        g1.setCell(0, 1, BigDecimal.valueOf(2.0d)); 
//        g1.setCell(0, 2, BigDecimal.valueOf(1.6d));
//        g1.setCell(0, 3, BigDecimal.valueOf(12.0d));
//        g1.setCell(0, 4, BigDecimal.valueOf(101.0d));
//        g1.setCell(0, 5, BigDecimal.valueOf(2003.0d));
//        g1.setCell(0, 6, BigDecimal.valueOf(30004.0d));
//        g1.setCell(0, 7, BigDecimal.valueOf(300006.0d));
//        g1.setCell(0, 8, BigDecimal.valueOf(3000007.0d));
//        g1.setCell(0, 9, BigDecimal.valueOf(30000008.0d));
        er.setCell(1, 0, BigDecimal.valueOf(1.2d));
        er.setCell(1, 1, BigDecimal.valueOf(12.0d)); 
        er.setCell(1, 2, BigDecimal.valueOf(120.0d));
        er.setCell(1, 3, BigDecimal.valueOf(1200.0d));
        er.setCell(1, 4, BigDecimal.valueOf(12000.0d));
        er.setCell(1, 5, BigDecimal.valueOf(104.0d));
        er.setCell(1, 6, BigDecimal.valueOf(100.6d));
        er.setCell(1, 7, BigDecimal.valueOf(100.08d));
        er.setCell(1, 8, BigDecimal.valueOf(100.009d));
//        er.setCell(1, 9, BigDecimal.valueOf(100.0001d));
        er.setCell(2, 0, BigDecimal.valueOf(9.0d));
//        g1.setCell(2, 1, BigDecimal.valueOf(4.0d)); 
//        g1.setCell(2, 2, BigDecimal.valueOf(1.0d));
//        g1.setCell(2, 3, BigDecimal.valueOf(1.0d));
//        g1.setCell(2, 4, BigDecimal.valueOf(3.0d));
//        g1.setCell(2, 5, BigDecimal.valueOf(2.0d));
//        g1.setCell(2, 6, BigDecimal.valueOf(3.0d));
//        g1.setCell(2, 7, BigDecimal.valueOf(6.0d));
//        g1.setCell(2, 8, BigDecimal.valueOf(2.0d));
        er.setCell(2, 9, BigDecimal.valueOf(9.0d));
        er.setCell(3, 0, BigDecimal.valueOf(49.0d));
//        g1.setCell(3, 1, BigDecimal.valueOf(4.0d)); 
//        g1.setCell(3, 2, BigDecimal.valueOf(1.0d));
//        g1.setCell(3, 3, BigDecimal.valueOf(1.0d));
//        g1.setCell(3, 4, BigDecimal.valueOf(3.0d));
//        g1.setCell(3, 5, BigDecimal.valueOf(2.0d));
//        g1.setCell(3, 6, BigDecimal.valueOf(3.0d));
//        g1.setCell(3, 7, BigDecimal.valueOf(6.0d));
//        g1.setCell(3, 8, BigDecimal.valueOf(2.0d));
        er.setCell(3, 9, BigDecimal.valueOf(16.0d));
        er.setCell(4, 0, BigDecimal.valueOf(36.0d));
////        g0.setCell(4, 1, BigDecimal.valueOf(4.0d)); 
////        g0.setCell(4, 2, BigDecimal.valueOf(1.0d));
////        g0.setCell(4, 3, BigDecimal.valueOf(1.0d));
////        g0.setCell(4, 4, BigDecimal.valueOf(3.0d));
////        g0.setCell(4, 5, BigDecimal.valueOf(2.0d));
////        g0.setCell(4, 6, BigDecimal.valueOf(3.0d));
////        g0.setCell(4, 7, BigDecimal.valueOf(6.0d));
////        g0.setCell(4, 8, BigDecimal.valueOf(2.0d));
        er.setCell(4, 9, BigDecimal.valueOf(25.0d));
        er.setCell(5, 0, BigDecimal.valueOf(25.0d));
//        g0.setCell(5, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(5, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(5, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(5, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(5, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(5, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(5, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(5, 8, BigDecimal.valueOf(2.0d));
        er.setCell(5, 9, BigDecimal.valueOf(36.0d));
        er.setCell(6, 0, BigDecimal.valueOf(16.0d));
//        g0.setCell(6, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(6, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(6, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(6, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(6, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(6, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(6, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(6, 8, BigDecimal.valueOf(2.0d));
        er.setCell(6, 9, BigDecimal.valueOf(49.0d));
        er.setCell(7, 0, BigDecimal.valueOf(9.0d));
//        g0.setCell(7, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(7, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(7, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(7, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(7, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(7, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(7, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(7, 8, BigDecimal.valueOf(2.0d));
        er.setCell(7, 9, BigDecimal.valueOf(64.0d));
        er.setCell(8, 0, BigDecimal.valueOf(4.0d));
//        g0.setCell(8, 1, BigDecimal.valueOf(4.0d)); 
//        g0.setCell(8, 2, BigDecimal.valueOf(1.0d));
//        g0.setCell(8, 3, BigDecimal.valueOf(1.0d));
//        g0.setCell(8, 4, BigDecimal.valueOf(3.0d));
//        g0.setCell(8, 5, BigDecimal.valueOf(2.0d));
//        g0.setCell(8, 6, BigDecimal.valueOf(3.0d));
//        g0.setCell(8, 7, BigDecimal.valueOf(6.0d));
//        g0.setCell(8, 8, BigDecimal.valueOf(2.0d));
        er.setCell(8, 9, BigDecimal.valueOf(81.0d));
        er.setCell(9, 0, BigDecimal.valueOf(1.0d));
        er.setCell(9, 1, BigDecimal.valueOf(4.0d)); 
        er.setCell(9, 2, BigDecimal.valueOf(9.0d));
        er.setCell(9, 3, BigDecimal.valueOf(16.0d));
        er.setCell(9, 4, BigDecimal.valueOf(25.0d));
        er.setCell(9, 5, BigDecimal.valueOf(36.0d));
        er.setCell(9, 6, BigDecimal.valueOf(49.0d));
        er.setCell(9, 7, BigDecimal.valueOf(64.0d));
        er.setCell(9, 8, BigDecimal.valueOf(81.0d));
        er.setCell(9, 9, BigDecimal.valueOf(100.0d));
        r = gp.multiply(type, g0, g1, oom);
        r.log(maxNrowsToPrint, maxNcolsToPrint);
        r.log(5, 5);
        r.log(8, 5);
        equal = r.isSameDimensionsAndValues(er);
        assertTrue(equal);        
    }

//
//    /**
//     * Test of divide method, of class Grids_Processor.
//     */
//    @Test
//    public void testDivide() throws Exception {
//        System.out.println("divide");
//        Grids_GridDouble g0 = null;
//        Grids_GridDouble g1 = null;
//        Grids_Processor instance = null;
//        Grids_GridDouble expResult = null;
//        Grids_GridDouble result = instance.divide(g0, g1);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of aggregate method, of class Grids_Processor.
//     */
//    @Test
//    public void testAggregate_6args_1() throws Exception {
//        System.out.println("aggregate");
//        Grids_GridNumber grid = null;
//        int cellFactor = 0;
//        String statistic = "";
//        int rowOffset = 0;
//        int colOffset = 0;
//        Grids_GridDoubleFactory gf = null;
//        Grids_Processor instance = null;
//        Grids_GridDouble expResult = null;
//        Grids_GridDouble result = instance.aggregate(grid, cellFactor, statistic, rowOffset, colOffset, gf);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNoDataValueBigDecimal method, of class Grids_Processor.
//     */
//    @Test
//    public void testGetNoDataValueBigDecimal() throws Exception {
//        System.out.println("getNoDataValueBigDecimal");
//        Grids_GridNumber g = null;
//        Grids_Processor instance = null;
//        BigDecimal expResult = null;
//        BigDecimal result = instance.getNoDataValueBigDecimal(g);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of aggregate method, of class Grids_Processor.
//     */
//    @Test
//    public void testAggregate_6args_2() throws Exception {
//        System.out.println("aggregate");
//        Grids_GridNumber grid = null;
//        String statistic = "";
//        Grids_Dimensions rD = null;
//        Grids_GridDoubleFactory gf = null;
//        int dp = 0;
//        RoundingMode rm = null;
//        Grids_Processor instance = null;
//        Grids_GridDouble expResult = null;
//        Grids_GridDouble result = instance.aggregate(grid, statistic, rD, gf, dp, rm);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRowProcessInitialData method, of class Grids_Processor.
//     */
//    @Test
//    public void testGetRowProcessInitialData() throws Exception {
//        System.out.println("getRowProcessInitialData");
//        Grids_GridDouble g = null;
//        int cellDistance = 0;
//        long row = 0L;
//        Grids_Processor instance = null;
//        double[][] expResult = null;
//        double[][] result = instance.getRowProcessInitialData(g, cellDistance, row);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRowProcessData method, of class Grids_Processor.
//     */
//    @Test
//    public void testGetRowProcessData() throws Exception {
//        System.out.println("getRowProcessData");
//        Grids_GridDouble g = null;
//        double[][] previous = null;
//        int cellDistance = 0;
//        long row = 0L;
//        long col = 0L;
//        Grids_Processor instance = null;
//        double[][] expResult = null;
//        double[][] result = instance.getRowProcessData(g, previous, cellDistance, row, col);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of output method, of class Grids_Processor.
//     */
//    @Test
//    public void testOutput() throws Exception {
//        System.out.println("output");
//        Grids_GridNumber g = null;
//        Path outDir = null;
//        Grids_ImageExporter ie = null;
//        String[] imageTypes = null;
//        Grids_ESRIAsciiGridExporter eage = null;
//        Grids_Processor instance = null;
//        instance.output(g, outDir, ie, imageTypes, eage);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of outputImage method, of class Grids_Processor.
//     */
//    @Test
//    public void testOutputImage() throws Exception {
//        System.out.println("outputImage");
//        Grids_GridNumber g = null;
//        IO_Path outDir = null;
//        Grids_ImageExporter ie = null;
//        String[] imageTypes = null;
//        boolean hoome = false;
//        Grids_Processor instance = null;
//        instance.outputImage(g, outDir, ie, imageTypes, hoome);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of outputESRIAsciiGrid method, of class Grids_Processor.
//     */
//    @Test
//    public void testOutputESRIAsciiGrid() throws Exception {
//        System.out.println("outputESRIAsciiGrid");
//        Grids_GridNumber g = null;
//        Path outDir = null;
//        Grids_ESRIAsciiGridExporter eage = null;
//        Grids_Processor instance = null;
//        instance.outputESRIAsciiGrid(g, outDir, eage);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    
}
