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

import ch.obermuhlner.math.big.BigRational;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;

/**
 * Test of Grids_Dimensions.
 * 
 * @author Andy Turner
 * @version 1.0
 */
public class Grids_DimensionsTest {

    Grids_Dimensions defaultInstance;

    public Grids_DimensionsTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        BigRational xmin = BigRational.ZERO;
        BigRational xmax = BigRational.ONE;
        BigRational ymin = BigRational.ZERO;
        BigRational ymax = BigRational.ONE;
        BigRational cellSize = BigRational.ONE;
        defaultInstance = new Grids_Dimensions(xmin, xmax, ymin, ymax,
                cellSize);
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of toString method, of class Grids_Dimensions.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        String expResult = "Grids_Dimensions[xMin=0, xMax=1, yMin=0, yMax=1, "
                + "cellsize=1]";
        String result = defaultInstance.toString();
        assertTrue(expResult.compareTo(result) == 0);
    }

    /**
     * Test of equals method, of class Grids_Dimensions.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        BigRational xmin = BigRational.ZERO;
        BigRational xmax = BigRational.ONE;
        BigRational ymin = BigRational.ZERO;
        BigRational ymax = BigRational.ONE;
        BigRational cellSize = BigRational.ONE;
        Object o = new Grids_Dimensions(xmin, xmax, ymin, ymax, cellSize);
        assertTrue(defaultInstance.equals(o));
    }

    /**
     * Test of hashCode method, of class Grids_Dimensions.
     */
    @Test
    public void testHashCode() {
        // No test.
    }

    /**
     * Test of getXMin method, of class Grids_Dimensions.
     */
    @Test
    public void testGetXMin() {
        System.out.println("getXMin");
        BigRational expResult = BigRational.ZERO;
        BigRational result = defaultInstance.getXMin();
        assertTrue(expResult.compareTo(result) == 0);
    }

    /**
     * Test of getXMax method, of class Grids_Dimensions.
     */
    @Test
    public void testGetXMax() {
        System.out.println("getXMax");
        BigRational expResult = BigRational.ONE;
        BigRational result = defaultInstance.getXMax();
        assertTrue(expResult.compareTo(result) == 0);
    }

    /**
     * Test of getYMin method, of class Grids_Dimensions.
     */
    @Test
    public void testGetYMin() {
        System.out.println("getYMin");
        BigRational expResult = BigRational.ZERO;
        BigRational result = defaultInstance.getYMin();
        assertTrue(expResult.compareTo(result) == 0);
    }

    /**
     * Test of getYMax method, of class Grids_Dimensions.
     */
    @Test
    public void testGetYMax() {
        System.out.println("getYMax");
        BigRational expResult = BigRational.ONE;
        BigRational result = defaultInstance.getYMax();
        assertTrue(expResult.compareTo(result) == 0);
    }

    /**
     * Test of getCellsize method, of class Grids_Dimensions.
     */
    @Test
    public void testGetCellsize() {
        System.out.println("getCellsize");
        BigRational expResult = BigRational.ONE;
        BigRational result = defaultInstance.getCellsize();
        assertTrue(expResult.compareTo(result) == 0);
    }

    /**
     * Test of getHalfCellsize method, of class Grids_Dimensions.
     */
    @Test
    public void testGetHalfCellsize() {
        System.out.println("getHalfCellsize");
        BigRational expResult = BigRational.valueOf(0.5d);
        BigRational result = defaultInstance.getHalfCellsize();
        assertTrue(expResult.compareTo(result) == 0);
    }

    /**
     * Test of getCellsizeSquared method, of class Grids_Dimensions.
     */
    @Test
    public void testGetCellsizeSquared() {
        System.out.println("getCellsizeSquared");
        BigRational expResult = BigRational.ONE;
        BigRational result = defaultInstance.getCellsizeSquared();
        assertTrue(expResult.compareTo(result) == 0);
    }

    /**
     * Test of getWidth method, of class Grids_Dimensions.
     */
    @Test
    public void testGetWidth() {
        System.out.println("getWidth");
        // Test 1
        BigRational expResult = BigRational.ONE;
        BigRational result = defaultInstance.getWidth();
        assertTrue(expResult.compareTo(result) == 0);
        // Test 2
        BigRational xmin = BigRational.ZERO;
        BigRational xmax = BigRational.ONE;
        BigRational ymin = BigRational.ZERO;
        BigRational ymax = BigRational.ONE;
        BigRational cellSize = BigRational.valueOf("0.1");
        Grids_Dimensions instance = new Grids_Dimensions(xmin, xmax, ymin, ymax,
                cellSize);
        expResult = BigRational.ONE;
        result = instance.getWidth();
        assertTrue(expResult.compareTo(result) == 0);
    }

    /**
     * Test of getHeight method, of class Grids_Dimensions.
     */
    @Test
    public void testGetHeight() {
        System.out.println("getHeight");
        // Test 1
        BigRational expResult = BigRational.ONE;
        BigRational result = defaultInstance.getHeight();
        assertEquals(expResult, result);
        // Test 2
        BigRational xmin = BigRational.ZERO;
        BigRational xmax = BigRational.ONE;
        BigRational ymin = BigRational.ZERO;
        BigRational ymax = BigRational.ONE;
        BigRational cellSize = BigRational.valueOf("0.1");
        Grids_Dimensions instance = new Grids_Dimensions(xmin, xmax, ymin, ymax,
                cellSize);
        expResult = BigRational.ONE;
        result = instance.getHeight();
        assertTrue(expResult.compareTo(result) == 0);
    }

    /**
     * Test of getArea method, of class Grids_Dimensions.
     */
    @Test
    public void testGetArea() {
        System.out.println("getArea");
        // Test 1
        BigRational expResult = BigRational.ONE;
        BigRational result = defaultInstance.getArea();
        assertEquals(expResult, result);
        // Test 2
        BigRational xmin = BigRational.ZERO;
        BigRational xmax = BigRational.ONE;
        BigRational ymin = BigRational.ZERO;
        BigRational ymax = BigRational.ONE;
        BigRational cellSize = BigRational.valueOf("0.1");
        Grids_Dimensions instance = new Grids_Dimensions(xmin, xmax, ymin, ymax,
                cellSize);
        expResult = BigRational.ONE;
        result = instance.getArea();
        assertTrue(expResult.compareTo(result) == 0);
    }

    /**
     * Test of intersects method, of class Grids_Dimensions.
     */
    @Test
    public void testIntersects() {
        System.out.println("intersects");
        System.out.println("intersects");
        // Test 1
        Grids_Dimensions d0 = new Grids_Dimensions(BigRational.ZERO,
                BigRational.TEN, BigRational.ZERO, BigRational.TEN,
                BigRational.ONE);
        Grids_Dimensions d1 = new Grids_Dimensions(BigRational.ZERO,
                BigRational.TEN, BigRational.ZERO, BigRational.TEN,
                BigRational.ONE);
        assertTrue(d0.intersects(d1));
        // Test 2
        d0 = new Grids_Dimensions(BigRational.ONE, BigRational.TEN,
                BigRational.ONE, BigRational.TEN, BigRational.ONE);
        d1 = new Grids_Dimensions(BigRational.ZERO, BigRational.TEN,
                BigRational.ZERO, BigRational.TEN, BigRational.ONE);
        assertTrue(d0.intersects(d1));
        // Test 3
        d0 = new Grids_Dimensions(BigRational.valueOf(-1), BigRational.valueOf(1),
                BigRational.valueOf(-1), BigRational.valueOf(1), BigRational.ONE);
        d1 = new Grids_Dimensions(BigRational.ZERO, BigRational.TEN,
                BigRational.ZERO, BigRational.TEN, BigRational.ONE);
        assertTrue(d0.intersects(d1));
        // Test 4
        d0 = new Grids_Dimensions(BigRational.valueOf(9), BigRational.valueOf(11),
                BigRational.valueOf(-1), BigRational.valueOf(1), BigRational.ONE);
        d1 = new Grids_Dimensions(BigRational.ZERO, BigRational.TEN,
                BigRational.ZERO, BigRational.TEN, BigRational.ONE);
        assertTrue(d0.intersects(d1));
        // Test 5
        d0 = new Grids_Dimensions(BigRational.valueOf(11), BigRational.valueOf(12),
                BigRational.valueOf(-1), BigRational.valueOf(1), BigRational.ONE);
        d1 = new Grids_Dimensions(BigRational.ZERO, BigRational.TEN,
                BigRational.ZERO, BigRational.TEN, BigRational.ONE);
        assertFalse(d0.intersects(d1));
        // Test 6
        d0 = new Grids_Dimensions(BigRational.valueOf(9), BigRational.valueOf(10),
                BigRational.valueOf(-2), BigRational.valueOf(-1), BigRational.ONE);
        d1 = new Grids_Dimensions(BigRational.ZERO, BigRational.TEN,
                BigRational.ZERO, BigRational.TEN, BigRational.ONE);
        assertFalse(d0.intersects(d1));
    }

}
