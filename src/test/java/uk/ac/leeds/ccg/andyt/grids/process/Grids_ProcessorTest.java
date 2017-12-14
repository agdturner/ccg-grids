/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.process;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridInt;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ImageExporter;

/**
 *
 * @author geoagdt
 */
public class Grids_ProcessorTest {
    
    public Grids_ProcessorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getTime0 method, of class Grids_Processor.
     */
    @Test
    public void testGetTime0_0args() {
        System.out.println("getTime0");
        Grids_Processor instance = new Grids_Processor();
        long expResult = 0L;
        long result = instance.getTime0();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTime0 method, of class Grids_Processor.
     */
    @Test
    public void testGetTime0_boolean() {
        System.out.println("getTime0");
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        long expResult = 0L;
        long result = instance.getTime0(handleOutOfMemoryError);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDirectory method, of class Grids_Processor.
     */
    @Test
    public void testGetDirectory() {
        System.out.println("getDirectory");
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        File expResult = null;
        File result = instance.getDirectory(handleOutOfMemoryError);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDirectory method, of class Grids_Processor.
     */
    @Test
    public void testSetDirectory_File_boolean() {
        System.out.println("setDirectory");
        File directory = null;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.setDirectory(directory, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDirectory method, of class Grids_Processor.
     */
    @Test
    public void testSetDirectory_File() {
        System.out.println("setDirectory");
        File directory = null;
        Grids_Processor instance = new Grids_Processor();
        instance.setDirectory(directory);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDirectory method, of class Grids_Processor.
     */
    @Test
    public void testSetDirectory_3args() {
        System.out.println("setDirectory");
        File directory = null;
        boolean copyLogFile = false;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.setDirectory(directory, copyLogFile, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of copyAndSetUpNewLog method, of class Grids_Processor.
     */
    @Test
    public void testCopyAndSetUpNewLog() throws Exception {
        System.out.println("copyAndSetUpNewLog");
        File newLog = null;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.copyAndSetUpNewLog(newLog, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of log method, of class Grids_Processor.
     */
    @Test
    public void testLog_String_boolean() {
        System.out.println("log");
        String string = "";
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.log(string, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of log method, of class Grids_Processor.
     */
    @Test
    public void testLog_3args() {
        System.out.println("log");
        int logIndentation = 0;
        String string = "";
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.log(logIndentation, string, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of log method, of class Grids_Processor.
     */
    @Test
    public void testLog_int_String() {
        System.out.println("log");
        int logIndentation = 0;
        String string = "";
        Grids_Processor instance = new Grids_Processor();
        instance.log(logIndentation, string);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of distance method, of class Grids_Processor.
     */
    @Test
    public void testDistance_5args() {
        System.out.println("distance");
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        double expResult = 0.0;
        double result = instance.distance(x1, y1, x2, y2, handleOutOfMemoryError);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of distance method, of class Grids_Processor.
     */
    @Test
    public void testDistance_8args() {
        System.out.println("distance");
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        int chunkCols = 0;
        int chunkRowIndex = 0;
        int chunkColIndex = 0;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        double expResult = 0.0;
        double result = instance.distance(x1, y1, x2, y2, chunkCols, chunkRowIndex, chunkColIndex, handleOutOfMemoryError);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of distance method, of class Grids_Processor.
     */
    @Test
    public void testDistance_4args() {
        System.out.println("distance");
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        Grids_Processor instance = new Grids_Processor();
        double expResult = 0.0;
        double result = instance.distance(x1, y1, x2, y2);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of angle method, of class Grids_Processor.
     */
    @Test
    public void testAngle_5args() {
        System.out.println("angle");
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        double expResult = 0.0;
        double result = instance.angle(x1, y1, x2, y2, handleOutOfMemoryError);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of angle method, of class Grids_Processor.
     */
    @Test
    public void testAngle_8args() {
        System.out.println("angle");
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        int chunkCols = 0;
        int chunkRowIndex = 0;
        int chunkColIndex = 0;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        double expResult = 0.0;
        double result = instance.angle(x1, y1, x2, y2, chunkCols, chunkRowIndex, chunkColIndex, handleOutOfMemoryError);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of angle method, of class Grids_Processor.
     */
    @Test
    public void testAngle_4args() {
        System.out.println("angle");
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        Grids_Processor instance = new Grids_Processor();
        double expResult = 0.0;
        double result = instance.angle(x1, y1, x2, y2);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mask method, of class Grids_Processor.
     */
    @Test
    public void testMask_3args() {
        System.out.println("mask");
        Grids_AbstractGridNumber g = null;
        Grids_AbstractGridNumber mask = null;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.mask(g, mask, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mask method, of class Grids_Processor.
     */
    @Test
    public void testMask_4args() {
        System.out.println("mask");
        Grids_AbstractGridNumber g = null;
        double min = 0.0;
        double max = 0.0;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.mask(g, min, max, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of rescale method, of class Grids_Processor.
     */
    @Test
    public void testRescale_5args() {
        System.out.println("rescale");
        Grids_AbstractGridNumber g = null;
        String type = "";
        double min = 0.0;
        double max = 0.0;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.rescale(g, type, min, max, handleOutOfMemoryError);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of rescale method, of class Grids_Processor.
     */
    @Test
    public void testRescale_4args_1() {
        System.out.println("rescale");
        Grids_GridDouble g = null;
        String type = "";
        double min = 0.0;
        double max = 0.0;
        Grids_Processor instance = new Grids_Processor();
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
    public void testRescale_4args_2() {
        System.out.println("rescale");
        Grids_GridInt g = null;
        String type = "";
        double min = 0.0;
        double max = 0.0;
        Grids_Processor instance = new Grids_Processor();
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.rescale(g, type, min, max);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setValueALittleBitLarger method, of class Grids_Processor.
     */
    @Test
    public void testSetValueALittleBitLarger() {
        System.out.println("setValueALittleBitLarger");
        Grids_GridDouble grid = null;
        HashSet _CellIDs = null;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.setValueALittleBitLarger(grid, _CellIDs, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setValueALittleBitSmaller method, of class Grids_Processor.
     */
    @Test
    public void testSetValueALittleBitSmaller() {
        System.out.println("setValueALittleBitSmaller");
        Grids_GridDouble grid = null;
        HashSet _CellIDs = null;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.setValueALittleBitSmaller(grid, _CellIDs, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_4args_1() {
        System.out.println("addToGrid");
        Grids_GridDouble grid = null;
        HashSet cellIDs = null;
        double value = 0.0;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.addToGrid(grid, cellIDs, value, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_3args_1() {
        System.out.println("addToGrid");
        Grids_GridDouble grid = null;
        double value = 0.0;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.addToGrid(grid, value, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_4args_2() {
        System.out.println("addToGrid");
        Grids_GridDouble grid = null;
        Grids_2D_ID_long[] cellIDs = null;
        double value = 0.0;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.addToGrid(grid, cellIDs, value, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_3args_2() {
        System.out.println("addToGrid");
        Grids_GridDouble grid = null;
        Grids_GridDouble gridToAdd = null;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.addToGrid(grid, gridToAdd, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_4args_3() {
        System.out.println("addToGrid");
        Grids_GridDouble grid = null;
        Grids_GridDouble gridToAdd = null;
        double weight = 0.0;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.addToGrid(grid, gridToAdd, weight, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_8args() {
        System.out.println("addToGrid");
        Grids_GridDouble grid = null;
        Grids_GridDouble gridToAdd = null;
        long startRowIndex = 0L;
        long startColIndex = 0L;
        long endRowIndex = 0L;
        long endColIndex = 0L;
        double weight = 0.0;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.addToGrid(grid, gridToAdd, startRowIndex, startColIndex, endRowIndex, endColIndex, weight, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_9args() {
        System.out.println("addToGrid");
        Grids_GridDouble grid = null;
        Grids_GridDouble gridToAdd = null;
        long startRowIndex = 0L;
        long startColIndex = 0L;
        long endRowIndex = 0L;
        long endColIndex = 0L;
        BigDecimal[] dimensionConstraints = null;
        double weight = 0.0;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.addToGrid(grid, gridToAdd, startRowIndex, startColIndex, endRowIndex, endColIndex, dimensionConstraints, weight, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToGrid method, of class Grids_Processor.
     */
    @Test
    public void testAddToGrid_4args_4() {
        System.out.println("addToGrid");
        Grids_GridDouble grid = null;
        File file = null;
        String type = "";
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.addToGrid(grid, file, type, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of multiply method, of class Grids_Processor.
     */
    @Test
    public void testMultiply_3args() {
        System.out.println("multiply");
        Grids_GridDouble g0 = null;
        Grids_GridDouble g1 = null;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.multiply(g0, g1, handleOutOfMemoryError);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of multiply method, of class Grids_Processor.
     */
    @Test
    public void testMultiply_Grids_GridDouble_Grids_GridDouble() {
        System.out.println("multiply");
        Grids_GridDouble g0 = null;
        Grids_GridDouble g1 = null;
        Grids_Processor instance = new Grids_Processor();
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
    public void testDivide_3args() {
        System.out.println("divide");
        Grids_GridDouble g0 = null;
        Grids_GridDouble g1 = null;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.divide(g0, g1, handleOutOfMemoryError);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of divide method, of class Grids_Processor.
     */
    @Test
    public void testDivide_Grids_GridDouble_Grids_GridDouble() {
        System.out.println("divide");
        Grids_GridDouble g0 = null;
        Grids_GridDouble g1 = null;
        Grids_Processor instance = new Grids_Processor();
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
    public void testAggregate_7args() {
        System.out.println("aggregate");
        Grids_AbstractGridNumber grid = null;
        int cellFactor = 0;
        String statistic = "";
        int rowOffset = 0;
        int colOffset = 0;
        Grids_GridDoubleFactory gridFactory = null;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.aggregate(grid, cellFactor, statistic, rowOffset, colOffset, gridFactory, handleOutOfMemoryError);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of aggregate method, of class Grids_Processor.
     */
    @Test
    public void testAggregate_5args() {
        System.out.println("aggregate");
        Grids_AbstractGridNumber grid = null;
        String statistic = "";
        Grids_Dimensions resultDimensions = null;
        Grids_GridDoubleFactory gridFactory = null;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        Grids_GridDouble expResult = null;
        Grids_GridDouble result = instance.aggregate(grid, statistic, resultDimensions, gridFactory, handleOutOfMemoryError);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRowProcessInitialData method, of class Grids_Processor.
     */
    @Test
    public void testGetRowProcessInitialData() {
        System.out.println("getRowProcessInitialData");
        Grids_GridDouble grid = null;
        int cellDistance = 0;
        long row = 0L;
        Grids_Processor instance = new Grids_Processor();
        double[][] expResult = null;
        double[][] result = instance.getRowProcessInitialData(grid, cellDistance, row);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRowProcessData method, of class Grids_Processor.
     */
    @Test
    public void testGetRowProcessData() {
        System.out.println("getRowProcessData");
        Grids_GridDouble grid = null;
        double[][] previous = null;
        int cellDistance = 0;
        long row = 0L;
        long col = 0L;
        Grids_Processor instance = new Grids_Processor();
        double[][] expResult = null;
        double[][] result = instance.getRowProcessData(grid, previous, cellDistance, row, col);
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
        Grids_AbstractGridNumber grid = null;
        File outputDirectory = null;
        Grids_ImageExporter imageExporter = null;
        String[] imageTypes = null;
        Grids_ESRIAsciiGridExporter eSRIAsciiGridExporter = null;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.output(grid, outputDirectory, imageExporter, imageTypes, eSRIAsciiGridExporter, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of outputImage method, of class Grids_Processor.
     */
    @Test
    public void testOutputImage() throws Exception {
        System.out.println("outputImage");
        Grids_AbstractGridNumber grid = null;
        File outputDirectory = null;
        Grids_ImageExporter ie = null;
        String[] imageTypes = null;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.outputImage(grid, outputDirectory, ie, imageTypes, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of outputESRIAsciiGrid method, of class Grids_Processor.
     */
    @Test
    public void testOutputESRIAsciiGrid() throws Exception {
        System.out.println("outputESRIAsciiGrid");
        Grids_AbstractGridNumber g = null;
        File outputDirectory = null;
        Grids_ESRIAsciiGridExporter eage = null;
        boolean handleOutOfMemoryError = false;
        Grids_Processor instance = new Grids_Processor();
        instance.outputESRIAsciiGrid(g, outputDirectory, eage, handleOutOfMemoryError);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
