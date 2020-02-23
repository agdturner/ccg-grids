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
package uk.ac.leeds.ccg.grids.d2;

import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author geoagdt
 */
public class Grids_DimensionsTest {

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
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of intersects method, of class Grids_Dimensions.
     */
    @Test
    public void testIntersects() {
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
