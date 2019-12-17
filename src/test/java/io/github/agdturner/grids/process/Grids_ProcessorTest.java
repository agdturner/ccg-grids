/*
 * Copyright 2019 Centre for Computational Geography.
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
package io.github.agdturner.grids.process;

import io.github.agdturner.grids.core.Grids_2D_ID_long;
import io.github.agdturner.grids.core.Grids_Dimensions;
import io.github.agdturner.grids.d2.grid.Grids_GridNumber;
import io.github.agdturner.grids.d2.grid.d.Grids_GridDouble;
import io.github.agdturner.grids.d2.grid.d.Grids_GridFactoryDouble;
import io.github.agdturner.grids.d2.grid.i.Grids_GridInt;
import io.github.agdturner.grids.io.Grids_ESRIAsciiGridExporter;
import io.github.agdturner.grids.io.Grids_ImageExporter;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.HashSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;

/**
 *
 * @author geoagdt
 */
public class Grids_ProcessorTest {
    
    public Grids_ProcessorTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
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
        Grids_GridNumber g = null;
        Grids_GridNumber mask = null;
        Grids_Processor instance = null;
        instance.mask(g, mask);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mask method, of class Grids_Processor.
     */
    @Test
    public void testMask_3args() throws Exception {
        System.out.println("mask");
        Grids_GridNumber g = null;
        double min = 0.0;
        double max = 0.0;
        Grids_Processor instance = null;
        instance.mask(g, min, max);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of rescale method, of class Grids_Processor.
     */
    @Test
    public void testRescale_4args_1() throws Exception {
        System.out.println("rescale");
        Grids_GridNumber g = null;
        String type = "";
        double min = 0.0;
        double max = 0.0;
        Grids_Processor instance = null;
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.rescale(g, type, min, max);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of rescale method, of class Grids_Processor.
     */
    @Test
    public void testRescale_5args_1() throws Exception {
        System.out.println("rescale");
        Grids_GridDouble g = null;
        String type = "";
        double min = 0.0;
        double max = 0.0;
        boolean hoome = false;
        Grids_Processor instance = null;
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.rescale(g, type, min, max, hoome);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of rescale method, of class Grids_Processor.
     */
    @Test
    public void testRescale_4args_2() throws Exception {
        System.out.println("rescale");
        Grids_GridDouble g = null;
        String type = "";
        double min = 0.0;
        double max = 0.0;
        Grids_Processor instance = null;
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.rescale(g, type, min, max);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of rescale method, of class Grids_Processor.
     */
    @Test
    public void testRescale_5args_2() throws Exception {
        System.out.println("rescale");
        Grids_GridInt g = null;
        String type = "";
        double min = 0.0;
        double max = 0.0;
        boolean hoome = false;
        Grids_Processor instance = null;
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.rescale(g, type, min, max, hoome);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of rescale method, of class Grids_Processor.
     */
    @Test
    public void testRescale_4args_3() throws Exception {
        System.out.println("rescale");
        Grids_GridInt g = null;
        String type = "";
        double min = 0.0;
        double max = 0.0;
        Grids_Processor instance = null;
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.rescale(g, type, min, max);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLarger method, of class Grids_Processor.
     */
    @Test
    public void testSetLarger() throws Exception {
        System.out.println("setLarger");
        Grids_GridDouble g = null;
        HashSet<Grids_2D_ID_long> cellIDs = null;
        Grids_Processor instance = null;
        instance.setLarger(g, cellIDs);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSmaller method, of class Grids_Processor.
     */
    @Test
    public void testSetSmaller() throws Exception {
        System.out.println("setSmaller");
        Grids_GridDouble g = null;
        HashSet<Grids_2D_ID_long> cellIDs = null;
        Grids_Processor instance = null;
        instance.setSmaller(g, cellIDs);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_4args() throws Exception {
        System.out.println("addToGrid");
        Grids_GridDouble grid = null;
        HashSet cellIDs = null;
        double value = 0.0;
        boolean hoome = false;
        Grids_Processor instance = null;
        instance.addToGrid(grid, cellIDs, value, hoome);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_Grids_GridDouble_double() throws Exception {
        System.out.println("addToGrid");
        Grids_GridDouble grid = null;
        double value = 0.0;
        Grids_Processor instance = null;
        instance.addToGrid(grid, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_3args_1() throws Exception {
        System.out.println("addToGrid");
        Grids_GridDouble grid = null;
        Grids_2D_ID_long[] cellIDs = null;
        double value = 0.0;
        Grids_Processor instance = null;
        instance.addToGrid(grid, cellIDs, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_3args_2() throws Exception {
        System.out.println("addToGrid");
        Grids_GridDouble g = null;
        Grids_GridDouble g2 = null;
        double w = 0.0;
        Grids_Processor instance = null;
        instance.addToGrid(g, g2, w);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_7args() throws Exception {
        System.out.println("addToGrid");
        Grids_GridDouble g = null;
        Grids_GridDouble g2 = null;
        long startRow = 0L;
        long startCol = 0L;
        long endRow = 0L;
        long endCol = 0L;
        double w = 0.0;
        Grids_Processor instance = null;
        instance.addToGrid(g, g2, startRow, startCol, endRow, endCol, w);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_8args() throws Exception {
        System.out.println("addToGrid");
        Grids_GridDouble g = null;
        Grids_GridDouble g2 = null;
        long startRow = 0L;
        long startCol = 0L;
        long endRow = 0L;
        long endCol = 0L;
        BigDecimal[] dc = null;
        double w = 0.0;
        Grids_Processor instance = null;
        instance.addToGrid(g, g2, startRow, startCol, endRow, endCol, dc, w);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_3args_3() throws Exception {
        System.out.println("addToGrid");
        Grids_GridDouble g = null;
        Path file = null;
        String type = "";
        Grids_Processor instance = null;
        instance.addToGrid(g, file, type);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of multiply method, of class Grids_Processor.
     */
    @Test
    public void testMultiply() throws Exception {
        System.out.println("multiply");
        Grids_GridDouble g0 = null;
        Grids_GridDouble g1 = null;
        Grids_Processor instance = null;
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.multiply(g0, g1);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of divide method, of class Grids_Processor.
     */
    @Test
    public void testDivide() throws Exception {
        System.out.println("divide");
        Grids_GridDouble g0 = null;
        Grids_GridDouble g1 = null;
        Grids_Processor instance = null;
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.divide(g0, g1);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of aggregate method, of class Grids_Processor.
     */
    @Test
    public void testAggregate_6args() throws Exception {
        System.out.println("aggregate");
        Grids_GridNumber grid = null;
        int cellFactor = 0;
        String statistic = "";
        int rowOffset = 0;
        int colOffset = 0;
        Grids_GridFactoryDouble gridFactory = null;
        Grids_Processor instance = null;
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.aggregate(grid, cellFactor, statistic, rowOffset, colOffset, gridFactory);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of aggregate method, of class Grids_Processor.
     */
    @Test
    public void testAggregate_4args() throws Exception {
        System.out.println("aggregate");
        Grids_GridNumber grid = null;
        String statistic = "";
        Grids_Dimensions resultDimensions = null;
        Grids_GridFactoryDouble gf = null;
        Grids_Processor instance = null;
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.aggregate(grid, statistic, resultDimensions, gf);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRowProcessInitialData method, of class Grids_Processor.
     */
    @Test
    public void testGetRowProcessInitialData() throws Exception {
        System.out.println("getRowProcessInitialData");
        Grids_GridDouble g = null;
        int cellDistance = 0;
        long row = 0L;
        Grids_Processor instance = null;
        double[][] expResult = null;
        double[][] result = instance.getRowProcessInitialData(g, cellDistance, row);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRowProcessData method, of class Grids_Processor.
     */
    @Test
    public void testGetRowProcessData() throws Exception {
        System.out.println("getRowProcessData");
        Grids_GridDouble g = null;
        double[][] previous = null;
        int cellDistance = 0;
        long row = 0L;
        long col = 0L;
        Grids_Processor instance = null;
        double[][] expResult = null;
        double[][] result = instance.getRowProcessData(g, previous, cellDistance, row, col);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of output method, of class Grids_Processor.
     */
    @Test
    public void testOutput() throws Exception {
        System.out.println("output");
        Grids_GridNumber g = null;
        Path outDir = null;
        Grids_ImageExporter ie = null;
        String[] imageTypes = null;
        Grids_ESRIAsciiGridExporter eage = null;
        Grids_Processor instance = null;
        instance.output(g, outDir, ie, imageTypes, eage);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of outputImage method, of class Grids_Processor.
     */
    @Test
    public void testOutputImage() throws Exception {
        System.out.println("outputImage");
        Grids_GridNumber g = null;
        Generic_Path outDir = null;
        Grids_ImageExporter ie = null;
        String[] imageTypes = null;
        boolean hoome = false;
        Grids_Processor instance = null;
        instance.outputImage(g, outDir, ie, imageTypes, hoome);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of outputESRIAsciiGrid method, of class Grids_Processor.
     */
    @Test
    public void testOutputESRIAsciiGrid() throws Exception {
        System.out.println("outputESRIAsciiGrid");
        Grids_GridNumber g = null;
        Path outDir = null;
        Grids_ESRIAsciiGridExporter eage = null;
        boolean hoome = false;
        Grids_Processor instance = null;
        instance.outputESRIAsciiGrid(g, outDir, eage, hoome);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
