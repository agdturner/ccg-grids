/*
 * Copyright (C) 2017 geoagdt.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package uk.ac.leeds.ccg.andyt.grids.core;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author geoagdt
 */
public class Grids_2D_ID_intTest {

    public Grids_2D_ID_intTest() {
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
        assertEquals(expResult, result);
        // Test 2
        i = new Grids_2D_ID_int(i);
        expResult = 6;
        result = i.getRow();
        assertEquals(expResult, result);
        // Test 3
        i = new Grids_2D_ID_int(-1, -2);
        expResult = -1;
        result = i.getRow();
        assertEquals(expResult, result);
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
        assertEquals(expResult, result);
        // Test 2
        i = new Grids_2D_ID_int(i);
        expResult = 5;
        result = i.getCol();
        assertEquals(expResult, result);
        // Test 3
        i = new Grids_2D_ID_int(-1, -2);
        expResult = -2;
        result = i.getCol();
        assertEquals(expResult, result);
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
        assertEquals(expResult, result);
        // Test 2
        object = new Grids_2D_ID_int(6, 5);
        instance = new Grids_2D_ID_int(5, 5);
        expResult = false;
        result = instance.equals(object);
        assertEquals(expResult, result);
        // Test 3
        object = new Grids_2D_ID_int(6, 5);
        instance = new Grids_2D_ID_int(5, 6);
        expResult = false;
        result = instance.equals(object);
        assertEquals(expResult, result);
        // Test 4
        object = null;
        instance = new Grids_2D_ID_int(5, 6);
        expResult = false;
        result = instance.equals(object);
        assertEquals(expResult, result);
        // Test 5
        object = new Grids_2D_ID_long(5, 6);
        instance = new Grids_2D_ID_int(5, 6);
        expResult = false;
        result = instance.equals(object);
        assertEquals(expResult, result);
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
        assertEquals(expResult, result);
        // Test 2
        t = new Grids_2D_ID_int(5, 5);
        instance = new Grids_2D_ID_int(6, 5);
        expResult = -1;
        result = instance.compareTo(t);
        assertEquals(expResult, result);
        // Test 3
        t = new Grids_2D_ID_int(6, 4);
        instance = new Grids_2D_ID_int(6, 5);
        expResult = -1;
        result = instance.compareTo(t);
        assertEquals(expResult, result);
        // Test 2
        t = new Grids_2D_ID_int(7, 5);
        instance = new Grids_2D_ID_int(6, 5);
        expResult = 1;
        result = instance.compareTo(t);
        assertEquals(expResult, result);
        // Test 2
        t = new Grids_2D_ID_int(6, 6);
        instance = new Grids_2D_ID_int(6, 5);
        expResult = 1;
        result = instance.compareTo(t);
        assertEquals(expResult, result);
        // Test 2
        t = new Grids_2D_ID_int(5, 5);
        instance = new Grids_2D_ID_int(6, 5);
        expResult = -1;
        result = instance.compareTo(t);
        assertEquals(expResult, result);
    }

}
