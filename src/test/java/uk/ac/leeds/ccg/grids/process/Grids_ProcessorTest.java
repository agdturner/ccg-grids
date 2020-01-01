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
import uk.ac.leeds.ccg.generic.io.Generic_FileStore;
import uk.ac.leeds.ccg.generic.io.Generic_Path;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.core.Grids_Strings;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_long;
import uk.ac.leeds.ccg.grids.d2.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridNumber;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridFactoryDouble;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridFactoryInt;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridInt;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.grids.io.Grids_ImageExporter;

/**
 *
 * @author geoagdt
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
        Generic_Path dir = new Generic_Path(dataDir);
        ge = new Grids_Environment(env, dir);
        gp = new Grids_Processor(ge);
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of mask method, of class Grids_Processor.
     */
    @Test
    public void testMask_Grids_GridNumber_Grids_GridNumber() throws Exception {
        System.out.println("mask");
            Grids_GridFactoryDouble gfd = gp.gridFactoryDouble;
        Grids_GridDouble g0 = (Grids_GridDouble) gfd.create(2, 3);
        g0.setCell(0, 0, 1.0d);
        g0.setCell(0, 1, 1.0d);
        g0.setCell(0, 2, 1.0d);
        g0.setCell(1, 0, 1.0d);
        g0.setCell(1, 1, 1.0d);
        g0.setCell(1, 2, 1.0d);
        Grids_GridDouble g = (Grids_GridDouble) gfd.create(2, 3);
        //g.setCell(0, 0, 0.0d);
        g.setCell(0, 1, 1.0d);
        g.setCell(0, 2, 1.0d);
        g.setCell(1, 0, 1.0d);
        g.setCell(1, 1, 1.0d);
        g.setCell(1, 2, 1.0d);
        Grids_GridDouble mask = (Grids_GridDouble) gfd.create(g);
        gp.mask(g0, mask);
        boolean equal = mask.equals(g0);
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
//    /**
//     * Test of multiply method, of class Grids_Processor.
//     */
//    @Test
//    public void testMultiply() throws Exception {
//        System.out.println("multiply");
//        Grids_GridNumber g0 = null;
//        Grids_GridNumber g1 = null;
//        Grids_Processor instance = null;
//        Grids_GridDouble expResult = null;
//        Grids_GridDouble result = instance.multiply(g0, g1);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
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
//        Grids_GridFactoryDouble gf = null;
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
//        Grids_GridFactoryDouble gf = null;
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
//        Generic_Path outDir = null;
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