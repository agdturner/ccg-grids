/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid;

import java.io.File;
import java.math.BigDecimal;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.statistics.Grids_GridDoubleStatistics;
import uk.ac.leeds.ccg.andyt.grids.core.grid.statistics.Grids_GridDoubleStatisticsNotUpdated;

/**
 *
 * @author geoagdt
 */
public class Grids_GridDoubleTest {

    public Grids_GridDoubleTest() {
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

//    /**
//     * Test of toString method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testToString() {
//        System.out.println("toString");
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        String expResult = "";
//        String result = instance.toString(handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getGridChunk method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetGridChunk_3args() {
//        System.out.println("getGridChunk");
//        int chunkRow = 0;
//        int chunkCol = 0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        Grids_AbstractGridChunkDouble expResult = null;
//        Grids_AbstractGridChunkDouble result = instance.getGridChunk(chunkRow, chunkCol, handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getGridChunk method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetGridChunk_int_int() {
//        System.out.println("getGridChunk");
//        int chunkRow = 0;
//        int chunkCol = 0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        Grids_AbstractGridChunkDouble expResult = null;
//        Grids_AbstractGridChunkDouble result = instance.getGridChunk(chunkRow, chunkCol);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getGridChunk method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetGrid2DSquareCellDoubleChunk_Grids_2D_ID_int_boolean() {
//        System.out.println("getGridChunk");
//        Grids_2D_ID_int chunkID = null;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        Grids_AbstractGridChunkDouble expResult = null;
//        Grids_AbstractGridChunkDouble result = instance.getGridChunk(chunkID, handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getGridChunk method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetGrid2DSquareCellDoubleChunk_Grids_2D_ID_int() {
//        System.out.println("getGridChunk");
//        Grids_2D_ID_int chunkID = null;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        Grids_AbstractGridChunkDouble expResult = null;
//        Grids_AbstractGridChunkDouble result = instance.getGridChunk(chunkID);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNoDataValue method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNoDataValue() {
//        System.out.println("getNoDataValue");
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getNoDataValue(handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNoDataValueBigDecimal method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNoDataValueBigDecimal() {
//        System.out.println("getNoDataValueBigDecimal");
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        BigDecimal expResult = null;
//        BigDecimal result = instance.getNoDataValueBigDecimal(handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of initNoDataValue method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testInitNoDataValue() {
//        System.out.println("initNoDataValue");
//        double noDataValue = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        instance.initNoDataValue(noDataValue);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    File dir;

    /**
     * Initialises dir.
     */
    protected void initDir() {
        dir = new File(System.getProperty("user.dir"));
        dir = new File(dir, "Grids");
        dir = new File(dir, "Test");
        dir.mkdirs();
        System.out.println("dir " + dir);
    }

    /**
     * Gets a Grids_Environment.
     *
     * @param dir
     * @return A new Grids_Environment with a directory et in the current users
     * working directory.
     *
     */
    protected Grids_Environment getEnvironment(File dir) {
        Grids_Environment result;
        result = new Grids_Environment(new File(dir, "Grids_Environment"));
        return result;
    }

    /**
     * Gets a Grids_Factory.
     *
     * @param ge
     * @param dir
     * @param chunkNRows
     * @param chunkNCols
     * @param noDataValue
     * @param chunkFactory
     * @param handleOutOfMemoryError
     * @return
     */
    protected Grids_GridDoubleFactory getFactory(
            Grids_Environment ge,
            File dir,
            int chunkNRows,
            int chunkNCols,
            long nRows,
            long nCols,
            double noDataValue,
            Grids_GridChunkDoubleArrayFactory chunkFactory,
            boolean handleOutOfMemoryError) {
        Grids_GridDoubleFactory result;
        result = new Grids_GridDoubleFactory(
                ge,
                ge.getFiles().getGeneratedGridDoubleFactoryDir(),
                noDataValue,
                chunkNRows,
                chunkNCols,
                new Grids_Dimensions(nRows, nCols),
                new Grids_GridDoubleStatisticsNotUpdated(ge),
                chunkFactory);
        return result;
    }

    /**
     *
     * @param nRows
     * @param nCols
     * @return
     */
    protected Grids_Dimensions getDimensions(long nRows, long nCols) {
        BigDecimal cellsize = BigDecimal.ONE;
        Grids_Dimensions result;
        BigDecimal xMin = BigDecimal.ONE;
        BigDecimal yMin = BigDecimal.ONE;
        BigDecimal xMax = new BigDecimal(nCols).multiply(cellsize);
        BigDecimal yMax = new BigDecimal(nRows).multiply(cellsize);
        result = new Grids_Dimensions(xMin, xMax, yMin, yMax, yMin);
        return result;
    }

    /**
     * Gets a Grids_GridDouble
     *
     * @param dir
     * @param name
     * @param gridDoubleFactory
     * @param nRows
     * @param handleOutOfMemoryError
     * @param nCols
     * @return
     */
    protected Grids_GridDouble getGridDouble(
            File dir,
            String name,
            Grids_GridDoubleFactory gridDoubleFactory,
            long nRows,
            long nCols,
            boolean handleOutOfMemoryError) {
        Grids_GridDouble result;
        Grids_Dimensions dimensions;
        dimensions = getDimensions(nRows, nCols);
        Grids_GridDoubleStatistics gridStatistics;
        gridStatistics = new Grids_GridDoubleStatistics(gridDoubleFactory.ge);
        result = gridDoubleFactory.create(
                gridStatistics,
                gridDoubleFactory.ge.getFiles().getGeneratedGridDoubleDir(),
                gridDoubleFactory.GridChunkDoubleFactory,
                nRows,
                nCols,
                dimensions,
                handleOutOfMemoryError);
        return result;
    }

    double noDataValue;
    boolean handleOutOfMemoryError;
    Grids_GridChunkDoubleArrayFactory chunkFactory;
    Grids_Environment ge;

    /**
     * Test create Grids_GridDouble.
     */
    @Test
    public void test1() {
        noDataValue = -9999.0d;
        handleOutOfMemoryError = true;
        chunkFactory = new Grids_GridChunkDoubleArrayFactory();
        initDir();
        ge = new Grids_Environment(dir);
        Grids_GridDoubleFactory gridFactory;

        int chunkNRows;
        int chunkNCols;
        long nRows;
        long nCols;
        String name;

        chunkNRows = 10;
        chunkNCols = 10;
        nRows = 1000;
        nCols = 1000;
        name = "TestGrid2";

        gridFactory = getFactory(
                ge,
                dir,
                chunkNRows,
                chunkNCols,
                nRows,
                nCols,
                noDataValue,
                chunkFactory,
                handleOutOfMemoryError);

        Grids_GridDouble g = getGridDouble(
                dir,
                name,
                gridFactory,
                nRows,
                nCols,
                handleOutOfMemoryError);

        int nChunkRows;
        int nChunkCols;
        nChunkRows = g.getNChunkRows(handleOutOfMemoryError);
        nChunkCols = g.getNChunkCols(handleOutOfMemoryError);
        
        
        int chunkRow;
        int chunkCol;
        int row;
        int col;

        // Set all values to 20.
        double value;
        value = 20d;

        for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
            chunkNRows = g.getChunkNRows(chunkRow, handleOutOfMemoryError);
            System.out.println("chunkNRows " + chunkNRows);
            for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                System.out.println("chunkCol " + chunkCol);
                Grids_2D_ID_int chunkID;
                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                chunkNCols = g.getChunkNCols(chunkCol, handleOutOfMemoryError);
                System.out.println("chunkNCols " + chunkNCols);
                for (row = 0; row < chunkNRows; row++) {
                    for (col = 0; col < chunkNCols; col++) {
                        g.setCell(chunkRow, chunkCol, row, col, value);
                    }
                }
                System.out.println("Done " + chunkID.toString());
            }
        }
        double expResult;
        double result;

        row = 100;
        col = 100;
        expResult = value;
        result = g.getCell(row, col, handleOutOfMemoryError);
        assertEquals(expResult, result, value);

    }

    /**
     * Test of getCell method, of class Grids_GridDouble.
     */
    @Test
    public void testGetCell_3args_1() {
        System.out.println("getCell");
        noDataValue = -9999.0d;
        handleOutOfMemoryError = true;
        chunkFactory = new Grids_GridChunkDoubleArrayFactory();
        initDir();
        ge = new Grids_Environment(dir);
        Grids_GridDoubleFactory gridFactory;

        int chunkNRows;
        int chunkNCols;
        long nRows;
        long nCols;
        long cellRowIndex;
        long cellColIndex;
        String name;

        chunkNRows = 24;
        chunkNCols = 49;
        nRows = 101;
        nCols = 100;
        name = "TestGrid1";

        gridFactory = getFactory(
                ge,
                dir,
                chunkNRows,
                chunkNCols,
                nRows,
                nCols,
                noDataValue,
                chunkFactory,
                handleOutOfMemoryError);

        Grids_GridDouble instance = getGridDouble(
                dir,
                name,
                gridFactory,
                nRows,
                nCols,
                handleOutOfMemoryError);

        double value;
        double expResult;
        double result;

        value = 20d;

        cellRowIndex = 0L;
        cellColIndex = 0L;
        instance.setCell(cellRowIndex, cellColIndex, value);
        expResult = value;
        result = instance.getCell(cellRowIndex, cellColIndex, handleOutOfMemoryError);
        assertEquals(expResult, result, 0.0);

        cellRowIndex = nRows - 1;
        cellColIndex = nCols - 1;
        instance.setCell(cellRowIndex, cellColIndex, value);
        expResult = value;
        result = instance.getCell(cellRowIndex, cellColIndex, handleOutOfMemoryError);
        assertEquals(expResult, result, 0.0);

        cellRowIndex = nRows;
        cellColIndex = nCols - 1;
        instance.setCell(cellRowIndex, cellColIndex, value);
        expResult = noDataValue;
        result = instance.getCell(cellRowIndex, cellColIndex, handleOutOfMemoryError);
        assertEquals(expResult, result, 0.0);

        cellRowIndex = nRows - 1;
        cellColIndex = nCols;
        instance.setCell(cellRowIndex, cellColIndex, value);
        expResult = noDataValue;
        result = instance.getCell(cellRowIndex, cellColIndex, handleOutOfMemoryError);
        assertEquals(expResult, result, 0.0);

    }

//    /**
//     * Test of getCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetCell_long_long() {
//        System.out.println("getCell");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getCell(cellRowIndex, cellColIndex);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetCell_6args() {
//        System.out.println("getCell");
//        Grids_AbstractGridChunkDouble grid2DSquareCellChunk = null;
//        int chunkRow = 0;
//        int chunkCol = 0;
//        int chunkCellRowIndex = 0;
//        int chunkCellColIndex = 0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getCell(grid2DSquareCellChunk, chunkRow, chunkCol, chunkCellRowIndex, chunkCellColIndex, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetCell_5args() {
//        System.out.println("getCell");
//        Grids_AbstractGridChunkDouble grid2DSquareCellChunk = null;
//        int chunkRow = 0;
//        int chunkCol = 0;
//        int chunkCellRowIndex = 0;
//        int chunkCellColIndex = 0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getCell(grid2DSquareCellChunk, chunkRow, chunkCol, chunkCellRowIndex, chunkCellColIndex);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetCell_3args_2() {
//        System.out.println("getCell");
//        double x = 0.0;
//        double y = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getCell(x, y, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetCell_double_double() {
//        System.out.println("getCell");
//        double x = 0.0;
//        double y = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getCell(x, y);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetCell_Grids_2D_ID_long_boolean() {
//        System.out.println("getCell");
//        Grids_2D_ID_long cellID = null;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getCell(cellID, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testSetCell_4args() {
//        System.out.println("setCell");
//        double x = 0.0;
//        double y = 0.0;
//        double newValue = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.setCell(x, y, newValue, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testSetCell_3args_1() {
//        System.out.println("setCell");
//        double x = 0.0;
//        double y = 0.0;
//        double newValue = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.setCell(x, y, newValue);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testSetCell_3args_2() {
//        System.out.println("setCell");
//        Grids_2D_ID_long cellID = null;
//        double newValue = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.setCell(cellID, newValue, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testSetCell_3args_3() {
//        System.out.println("setCell");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double newValue = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.setCell(cellRowIndex, cellColIndex, newValue);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testSetCell_6args_1() {
//        System.out.println("setCell");
//        int chunkRow = 0;
//        int chunkCol = 0;
//        int chunkCellRowIndex = 0;
//        int chunkCellColIndex = 0;
//        double newValue = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.setCell(chunkRow, chunkCol, chunkCellRowIndex, chunkCellColIndex, newValue, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testSetCell_5args() {
//        System.out.println("setCell");
//        int chunkRow = 0;
//        int chunkCol = 0;
//        int chunkCellRowIndex = 0;
//        int chunkCellColIndex = 0;
//        double newValue = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.setCell(chunkRow, chunkCol, chunkCellRowIndex, chunkCellColIndex, newValue);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testSetCell_7args() {
//        System.out.println("setCell");
//        Grids_AbstractGridChunkDouble chunk = null;
//        int chunkRow = 0;
//        int chunkCol = 0;
//        int chunkCellRowIndex = 0;
//        int chunkCellColIndex = 0;
//        double newValue = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.setCell(chunk, chunkRow, chunkCol, chunkCellRowIndex, chunkCellColIndex, newValue, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testSetCell_6args_2() {
//        System.out.println("setCell");
//        Grids_AbstractGridChunkDouble chunk = null;
//        int chunkRow = 0;
//        int chunkCol = 0;
//        int chunkCellRowIndex = 0;
//        int chunkCellColIndex = 0;
//        double newValue = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.setCell(chunk, chunkRow, chunkCol, chunkCellRowIndex, chunkCellColIndex, newValue);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of initCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testInitCell_4args() {
//        System.out.println("initCell");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double valueToInitialise = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        instance.initCell(cellRowIndex, cellColIndex, valueToInitialise, handleOutOfMemoryError);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of initCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testInitCell_3args() {
//        System.out.println("initCell");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double valueToInitialise = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        instance.initCell(cellRowIndex, cellColIndex, valueToInitialise);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of initCellFast method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testInitCellFast_4args() {
//        System.out.println("initCellFast");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double valueToInitialise = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        instance.initCellFast(cellRowIndex, cellColIndex, valueToInitialise, handleOutOfMemoryError);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of initCellFast method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testInitCellFast_3args() {
//        System.out.println("initCellFast");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double valueToInitialise = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        instance.initCellFast(cellRowIndex, cellColIndex, valueToInitialise);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCells method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetCells_4args_1() {
//        System.out.println("getCells");
//        double x = 0.0;
//        double y = 0.0;
//        double distance = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double[] expResult = null;
//        double[] result = instance.getCells(x, y, distance, handleOutOfMemoryError);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCells method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetCells_3args_1() {
//        System.out.println("getCells");
//        double x = 0.0;
//        double y = 0.0;
//        double distance = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double[] expResult = null;
//        double[] result = instance.getCells(x, y, distance);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCells method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetCells_4args_2() {
//        System.out.println("getCells");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double distance = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double[] expResult = null;
//        double[] result = instance.getCells(cellRowIndex, cellColIndex, distance, handleOutOfMemoryError);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCells method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetCells_3args_2() {
//        System.out.println("getCells");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double distance = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double[] expResult = null;
//        double[] result = instance.getCells(cellRowIndex, cellColIndex, distance);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCells method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetCells_6args() {
//        System.out.println("getCells");
//        double x = 0.0;
//        double y = 0.0;
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double distance = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double[] expResult = null;
//        double[] result = instance.getCells(x, y, cellRowIndex, cellColIndex, distance, handleOutOfMemoryError);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCells method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetCells_5args() {
//        System.out.println("getCells");
//        double x = 0.0;
//        double y = 0.0;
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double distance = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double[] expResult = null;
//        double[] result = instance.getCells(x, y, cellRowIndex, cellColIndex, distance);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValueDouble method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValueDouble_3args_1() {
//        System.out.println("getNearestValueDouble");
//        double x = 0.0;
//        double y = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getNearestValueDouble(x, y, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValueDouble method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValueDouble_double_double() {
//        System.out.println("getNearestValueDouble");
//        double x = 0.0;
//        double y = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getNearestValueDouble(x, y);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValueDouble method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValueDouble_3args_2() {
//        System.out.println("getNearestValueDouble");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getNearestValueDouble(cellRowIndex, cellColIndex, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValueDouble method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValueDouble_long_long() {
//        System.out.println("getNearestValueDouble");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getNearestValueDouble(cellRowIndex, cellColIndex);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValueDouble method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValueDouble_6args() {
//        System.out.println("getNearestValueDouble");
//        double x = 0.0;
//        double y = 0.0;
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double noDataValue = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getNearestValueDouble(x, y, cellRowIndex, cellColIndex, noDataValue, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValueDouble method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValueDouble_5args() {
//        System.out.println("getNearestValueDouble");
//        double x = 0.0;
//        double y = 0.0;
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double noDataValue = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getNearestValueDouble(x, y, cellRowIndex, cellColIndex, noDataValue);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValuesCellIDs method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValuesCellIDs_3args_1() {
//        System.out.println("getNearestValuesCellIDs");
//        double x = 0.0;
//        double y = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        Grids_2D_ID_long[] expResult = null;
//        Grids_2D_ID_long[] result = instance.getNearestValuesCellIDs(x, y, handleOutOfMemoryError);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValuesCellIDs method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValuesCellIDs_double_double() {
//        System.out.println("getNearestValuesCellIDs");
//        double x = 0.0;
//        double y = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        Grids_2D_ID_long[] expResult = null;
//        Grids_2D_ID_long[] result = instance.getNearestValuesCellIDs(x, y);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValuesCellIDs method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValuesCellIDs_3args_2() {
//        System.out.println("getNearestValuesCellIDs");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        Grids_2D_ID_long[] expResult = null;
//        Grids_2D_ID_long[] result = instance.getNearestValuesCellIDs(cellRowIndex, cellColIndex, handleOutOfMemoryError);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValuesCellIDs method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValuesCellIDs_long_long() {
//        System.out.println("getNearestValuesCellIDs");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        Grids_2D_ID_long[] expResult = null;
//        Grids_2D_ID_long[] result = instance.getNearestValuesCellIDs(cellRowIndex, cellColIndex);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValuesCellIDs method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValuesCellIDs_6args() {
//        System.out.println("getNearestValuesCellIDs");
//        double x = 0.0;
//        double y = 0.0;
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double _NoDataValue = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        Grids_2D_ID_long[] expResult = null;
//        Grids_2D_ID_long[] result = instance.getNearestValuesCellIDs(x, y, cellRowIndex, cellColIndex, _NoDataValue, handleOutOfMemoryError);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValuesCellIDs method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValuesCellIDs_5args() {
//        System.out.println("getNearestValuesCellIDs");
//        double x = 0.0;
//        double y = 0.0;
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double _NoDataValue = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        Grids_2D_ID_long[] expResult = null;
//        Grids_2D_ID_long[] result = instance.getNearestValuesCellIDs(x, y, cellRowIndex, cellColIndex, _NoDataValue);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValueDoubleDistance method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValueDoubleDistance_3args_1() {
//        System.out.println("getNearestValueDoubleDistance");
//        double x = 0.0;
//        double y = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getNearestValueDoubleDistance(x, y, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValueDoubleDistance method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValueDoubleDistance_double_double() {
//        System.out.println("getNearestValueDoubleDistance");
//        double x = 0.0;
//        double y = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getNearestValueDoubleDistance(x, y);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValueDoubleDistance method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValueDoubleDistance_3args_2() {
//        System.out.println("getNearestValueDoubleDistance");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getNearestValueDoubleDistance(cellRowIndex, cellColIndex, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValueDoubleDistance method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValueDoubleDistance_long_long() {
//        System.out.println("getNearestValueDoubleDistance");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getNearestValueDoubleDistance(cellRowIndex, cellColIndex);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValueDoubleDistance method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValueDoubleDistance_6args() {
//        System.out.println("getNearestValueDoubleDistance");
//        double x = 0.0;
//        double y = 0.0;
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double noDataValue = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getNearestValueDoubleDistance(x, y, cellRowIndex, cellColIndex, noDataValue, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNearestValueDoubleDistance method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testGetNearestValueDoubleDistance_5args() {
//        System.out.println("getNearestValueDoubleDistance");
//        double x = 0.0;
//        double y = 0.0;
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double noDataValue = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.getNearestValueDoubleDistance(x, y, cellRowIndex, cellColIndex, noDataValue);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addToCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testAddToCell_4args_1() {
//        System.out.println("addToCell");
//        double x = 0.0;
//        double y = 0.0;
//        double valueToAdd = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.addToCell(x, y, valueToAdd, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addToCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testAddToCell_3args_1() {
//        System.out.println("addToCell");
//        double x = 0.0;
//        double y = 0.0;
//        double valueToAdd = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.addToCell(x, y, valueToAdd);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addToCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testAddToCell_3args_2() {
//        System.out.println("addToCell");
//        Grids_2D_ID_long cellID = null;
//        double valueToAdd = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.addToCell(cellID, valueToAdd, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addToCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testAddToCell_Grids_2D_ID_long_double() {
//        System.out.println("addToCell");
//        Grids_2D_ID_long cellID = null;
//        double valueToAdd = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.addToCell(cellID, valueToAdd);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addToCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testAddToCell_4args_2() {
//        System.out.println("addToCell");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double valueToAdd = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.addToCell(cellRowIndex, cellColIndex, valueToAdd, handleOutOfMemoryError);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addToCell method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testAddToCell_3args_3() {
//        System.out.println("addToCell");
//        long cellRowIndex = 0L;
//        long cellColIndex = 0L;
//        double valueToAdd = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        double expResult = 0.0;
//        double result = instance.addToCell(cellRowIndex, cellColIndex, valueToAdd);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of initCells method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testInitCells_double_boolean() {
//        System.out.println("initCells");
//        double value = 0.0;
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        instance.initCells(value, handleOutOfMemoryError);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of initCells method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testInitCells_double() {
//        System.out.println("initCells");
//        double value = 0.0;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        instance.initCells(value);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of iterator method, of class Grids_GridDouble.
//     */
//    @Test
//    public void testIterator() {
//        System.out.println("iterator");
//        boolean handleOutOfMemoryError = false;
//        Grids_GridDouble instance = new Grids_GridDouble();
//        Iterator<Double> expResult = null;
//        Iterator<Double> result = instance.iterator(handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
