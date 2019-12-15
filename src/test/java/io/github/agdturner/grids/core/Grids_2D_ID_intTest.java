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
package io.github.agdturner.grids.core;

import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.core.Grids_2D_ID_long;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_2D_ID_intTest {

    public Grids_2D_ID_intTest() {
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    /**
     * Test of getRow method, of class Grids_2D_ID_int.
     */
    @Test
    public void testGetRow() {
        System.out.println("getRow");
        Grids_2D_ID_int i;
        int expResult;
        int result;
        // Test 1
        i = new Grids_2D_ID_int(6, 5);
        expResult = 6;
        result = i.getRow();
        Assertions.assertEquals(expResult, result);
        // Test 2
        i = new Grids_2D_ID_int(i);
        expResult = 6;
        result = i.getRow();
        Assertions.assertEquals(expResult, result);
        // Test 3
        i = new Grids_2D_ID_int(-1, -2);
        expResult = -1;
        result = i.getRow();
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getCol method, of class Grids_2D_ID_int.
     */
    @Test
    public void testGetCol() {
        System.out.println("getCol");
        Grids_2D_ID_int i;
        int expResult;
        int result;
        // Test 1
        i = new Grids_2D_ID_int(6, 5);
        expResult = 5;
        result = i.getCol();
        Assertions.assertEquals(expResult, result);
        // Test 2
        i = new Grids_2D_ID_int(i);
        expResult = 5;
        result = i.getCol();
        Assertions.assertEquals(expResult, result);
        // Test 3
        i = new Grids_2D_ID_int(-1, -2);
        expResult = -2;
        result = i.getCol();
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class Grids_2D_ID_int.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object object;
        Grids_2D_ID_int instance;
        boolean expResult;
        boolean result;
        // Test 1
        object = new Grids_2D_ID_int(6, 5);
        instance = new Grids_2D_ID_int(6, 5);
        expResult = true;
        result = instance.equals(object);
        Assertions.assertEquals(expResult, result);
        // Test 2
        object = new Grids_2D_ID_int(6, 5);
        instance = new Grids_2D_ID_int(5, 5);
        expResult = false;
        result = instance.equals(object);
        Assertions.assertEquals(expResult, result);
        // Test 3
        object = new Grids_2D_ID_int(6, 5);
        instance = new Grids_2D_ID_int(5, 6);
        expResult = false;
        result = instance.equals(object);
        Assertions.assertEquals(expResult, result);
        // Test 4
        object = null;
        instance = new Grids_2D_ID_int(5, 6);
        expResult = false;
        result = instance.equals(object);
        Assertions.assertEquals(expResult, result);
        // Test 5
        object = new Grids_2D_ID_long(5, 6);
        instance = new Grids_2D_ID_int(5, 6);
        expResult = false;
        result = instance.equals(object);
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of compareTo method, of class Grids_2D_ID_int.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        Grids_2D_ID_int t;
        Grids_2D_ID_int instance;
        int expResult;
        int result;
        // Test 1
        t = new Grids_2D_ID_int(6, 5);
        instance = new Grids_2D_ID_int(6, 5);
        expResult = 0;
        result = instance.compareTo(t);
        Assertions.assertEquals(expResult, result);
        // Test 2
        t = new Grids_2D_ID_int(5, 5);
        instance = new Grids_2D_ID_int(6, 5);
        expResult = -1;
        result = instance.compareTo(t);
        Assertions.assertEquals(expResult, result);
        // Test 3
        t = new Grids_2D_ID_int(6, 4);
        instance = new Grids_2D_ID_int(6, 5);
        expResult = -1;
        result = instance.compareTo(t);
        Assertions.assertEquals(expResult, result);
        // Test 2
        t = new Grids_2D_ID_int(7, 5);
        instance = new Grids_2D_ID_int(6, 5);
        expResult = 1;
        result = instance.compareTo(t);
        Assertions.assertEquals(expResult, result);
        // Test 2
        t = new Grids_2D_ID_int(6, 6);
        instance = new Grids_2D_ID_int(6, 5);
        expResult = 1;
        result = instance.compareTo(t);
        Assertions.assertEquals(expResult, result);
        // Test 2
        t = new Grids_2D_ID_int(5, 5);
        instance = new Grids_2D_ID_int(6, 5);
        expResult = -1;
        result = instance.compareTo(t);
        Assertions.assertEquals(expResult, result);
    }
}
