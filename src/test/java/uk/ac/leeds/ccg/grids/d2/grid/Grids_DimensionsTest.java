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
package uk.ac.leeds.ccg.grids.d2.grid;

import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Andy Turner
 * @version 1.0.0
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
        BigDecimal xmin = BigDecimal.ZERO;
        BigDecimal xmax = BigDecimal.ONE;
        BigDecimal ymin = BigDecimal.ZERO;
        BigDecimal ymax = BigDecimal.ONE;
        BigDecimal cellSize = BigDecimal.ONE;
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
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class Grids_Dimensions.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        BigDecimal xmin = BigDecimal.ZERO;
        BigDecimal xmax = BigDecimal.ONE;
        BigDecimal ymin = BigDecimal.ZERO;
        BigDecimal ymax = BigDecimal.ONE;
        BigDecimal cellSize = BigDecimal.ONE;
        Object o = new Grids_Dimensions(xmin, xmax, ymin, ymax, cellSize);
        boolean expResult = true;
        boolean result = defaultInstance.equals(o);
        assertEquals(expResult, result);
    }

    /**
     * Test of hashCode method, of class Grids_Dimensions.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        int expResult = 486979120;
        int result = defaultInstance.hashCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of getXMin method, of class Grids_Dimensions.
     */
    @Test
    public void testGetXMin() {
        System.out.println("getXMin");
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = defaultInstance.getXMin();
        assertEquals(expResult, result);
    }

    /**
     * Test of getXMax method, of class Grids_Dimensions.
     */
    @Test
    public void testGetXMax() {
        System.out.println("getXMax");
        BigDecimal expResult = BigDecimal.ONE;
        BigDecimal result = defaultInstance.getXMax();
        assertEquals(expResult, result);
    }

    /**
     * Test of getYMin method, of class Grids_Dimensions.
     */
    @Test
    public void testGetYMin() {
        System.out.println("getYMin");
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = defaultInstance.getYMin();
        assertEquals(expResult, result);
    }

    /**
     * Test of getYMax method, of class Grids_Dimensions.
     */
    @Test
    public void testGetYMax() {
        System.out.println("getYMax");
        BigDecimal expResult = BigDecimal.ONE;
        BigDecimal result = defaultInstance.getYMax();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCellsize method, of class Grids_Dimensions.
     */
    @Test
    public void testGetCellsize() {
        System.out.println("getCellsize");
        BigDecimal expResult = BigDecimal.ONE;
        BigDecimal result = defaultInstance.getCellsize();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHalfCellsize method, of class Grids_Dimensions.
     */
    @Test
    public void testGetHalfCellsize() {
        System.out.println("getHalfCellsize");
        BigDecimal expResult = BigDecimal.valueOf(0.5d);
        BigDecimal result = defaultInstance.getHalfCellsize();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCellsizeSquared method, of class Grids_Dimensions.
     */
    @Test
    public void testGetCellsizeSquared() {
        System.out.println("getCellsizeSquared");
        BigDecimal expResult = BigDecimal.ONE;
        BigDecimal result = defaultInstance.getCellsizeSquared();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWidth method, of class Grids_Dimensions.
     */
    @Test
    public void testGetWidth() {
        System.out.println("getWidth");
        // Test 1
        BigDecimal expResult = BigDecimal.ONE;
        BigDecimal result = defaultInstance.getWidth();
        assertEquals(expResult, result);
        // Test 2
        BigDecimal xmin = BigDecimal.ZERO;
        BigDecimal xmax = BigDecimal.ONE;
        BigDecimal ymin = BigDecimal.ZERO;
        BigDecimal ymax = BigDecimal.ONE;
        BigDecimal cellSize = new BigDecimal("0.1");
        Grids_Dimensions instance = new Grids_Dimensions(xmin, xmax, ymin, ymax,
                cellSize);
        expResult = BigDecimal.ONE;
        result = instance.getWidth();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHeight method, of class Grids_Dimensions.
     */
    @Test
    public void testGetHeight() {
        System.out.println("getHeight");
        // Test 1
        BigDecimal expResult = BigDecimal.ONE;
        BigDecimal result = defaultInstance.getHeight();
        assertEquals(expResult, result);
        // Test 2
        BigDecimal xmin = BigDecimal.ZERO;
        BigDecimal xmax = BigDecimal.ONE;
        BigDecimal ymin = BigDecimal.ZERO;
        BigDecimal ymax = BigDecimal.ONE;
        BigDecimal cellSize = new BigDecimal("0.1");
        Grids_Dimensions instance = new Grids_Dimensions(xmin, xmax, ymin, ymax,
                cellSize);
        expResult = BigDecimal.ONE;
        result = instance.getHeight();
        assertEquals(expResult, result);
    }

    /**
     * Test of getArea method, of class Grids_Dimensions.
     */
    @Test
    public void testGetArea() {
        System.out.println("getArea");
        // Test 1
        BigDecimal expResult = BigDecimal.ONE;
        BigDecimal result = defaultInstance.getArea();
        assertEquals(expResult, result);
        // Test 2
        BigDecimal xmin = BigDecimal.ZERO;
        BigDecimal xmax = BigDecimal.ONE;
        BigDecimal ymin = BigDecimal.ZERO;
        BigDecimal ymax = BigDecimal.ONE;
        BigDecimal cellSize = new BigDecimal("0.1");
        Grids_Dimensions instance = new Grids_Dimensions(xmin, xmax, ymin, ymax,
                cellSize);
        expResult = BigDecimal.ONE;
        result = instance.getArea();
        assertEquals(expResult, result);
    }

    /**
     * Test of intersects method, of class Grids_Dimensions.
     */
    @Test
    public void testIntersects() {
        System.out.println("intersects");
        System.out.println("intersects");
        // Test 1
        Grids_Dimensions d0 = new Grids_Dimensions(BigDecimal.ZERO,
                BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN,
                BigDecimal.ONE);
        Grids_Dimensions d1 = new Grids_Dimensions(BigDecimal.ZERO,
                BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN,
                BigDecimal.ONE);
        boolean expResult = true;
        boolean result = d0.intersects(d1);
        assertEquals(expResult, result);
        // Test 2
        d0 = new Grids_Dimensions(BigDecimal.ONE, BigDecimal.TEN,
                BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE);
        d1 = new Grids_Dimensions(BigDecimal.ZERO, BigDecimal.TEN,
                BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE);
        expResult = true;
        result = d0.intersects(d1);
        assertEquals(expResult, result);
        // Test 3
        d0 = new Grids_Dimensions(BigDecimal.valueOf(-1), BigDecimal.valueOf(1),
                BigDecimal.valueOf(-1), BigDecimal.valueOf(1), BigDecimal.ONE);
        d1 = new Grids_Dimensions(BigDecimal.ZERO, BigDecimal.TEN,
                BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE);
        expResult = true;
        result = d0.intersects(d1);
        assertEquals(expResult, result);
        // Test 4
        d0 = new Grids_Dimensions(BigDecimal.valueOf(9), BigDecimal.valueOf(11),
                BigDecimal.valueOf(-1), BigDecimal.valueOf(1), BigDecimal.ONE);
        d1 = new Grids_Dimensions(BigDecimal.ZERO, BigDecimal.TEN,
                BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE);
        expResult = true;
        result = d0.intersects(d1);
        assertEquals(expResult, result);
        // Test 5
        d0 = new Grids_Dimensions(BigDecimal.valueOf(11), BigDecimal.valueOf(12),
                BigDecimal.valueOf(-1), BigDecimal.valueOf(1), BigDecimal.ONE);
        d1 = new Grids_Dimensions(BigDecimal.ZERO, BigDecimal.TEN,
                BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE);
        expResult = false;
        result = d0.intersects(d1);
        assertEquals(expResult, result);
        // Test 6
        d0 = new Grids_Dimensions(BigDecimal.valueOf(9), BigDecimal.valueOf(10),
                BigDecimal.valueOf(-2), BigDecimal.valueOf(-1), BigDecimal.ONE);
        d1 = new Grids_Dimensions(BigDecimal.ZERO, BigDecimal.TEN,
                BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE);
        expResult = false;
        result = d0.intersects(d1);
        assertEquals(expResult, result);
    }

}
