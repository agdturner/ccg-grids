/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.utilities;

import java.awt.geom.Point2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;

/**
 *
 * @author geoagdt
 */
public class Grids_UtilitiesTest {

    public Grids_UtilitiesTest() {
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
     * Test of getTime method, of class Grids_Utilities.
     */
    @Test
    public void testGetTime() {
        System.out.println("getTime");
        long time = 0L;
        String expResult = "";
        String result;
        // Test 1
        time = 0L;
        expResult = "0 hours, 0 mins, 0 secs, 0 millisecs";
        result = Grids_Utilities.getTime(time);
        System.out.println(result);
        assertEquals(expResult, result);
        // Test 2
        time = 60L;
        expResult = "0 hours, 0 mins, 0 secs, 60 millisecs";
        result = Grids_Utilities.getTime(time);
        System.out.println(result);
        assertEquals(expResult, result);
        // Test 3
        time = 600L;
        expResult = "0 hours, 0 mins, 0 secs, 600 millisecs";
        result = Grids_Utilities.getTime(time);
        System.out.println(result);
        assertEquals(expResult, result);
        // Test 4
        time = 6000L;
        expResult = "0 hours, 0 mins, 6 secs, 0 millisecs";
        result = Grids_Utilities.getTime(time);
        System.out.println(result);
        assertEquals(expResult, result);
        // Test 5
        time = 60000L;
        expResult = "0 hours, 1 mins, 0 secs, 0 millisecs";
        result = Grids_Utilities.getTime(time);
        System.out.println(result);
        assertEquals(expResult, result);
        // Test 6
        time = 3600000L;
        expResult = "1 hours, 0 mins, 0 secs, 0 millisecs";
        result = Grids_Utilities.getTime(time);
        System.out.println(result);
        assertEquals(expResult, result);
        // Test 7
        time = 3666666L;
        expResult = "1 hours, 1 mins, 6 secs, 666 millisecs";
        result = Grids_Utilities.getTime(time);
        System.out.println(result);
        assertEquals(expResult, result);
        // Test 7
        time = 15555555L;
        expResult = "4 hours, 19 mins, 15 secs, 555 millisecs";
        result = Grids_Utilities.getTime(time);
        System.out.println(result);
        assertEquals(expResult, result);
    }

    /**
     * Test of getValueALittleBitLarger method, of class Grids_Utilities.
     */
    @Test
    public void testGetValueALittleBitLarger() {
        System.out.println("getValueALittleBitLarger");
        double value;
        double expResult;
        double result;
        // Test 1
        value = 0.0d;
        result = Grids_Utilities.getValueALittleBitLarger(value);
        expResult = 4.9E-324d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 2
        value = 4.9E-324d;
        result = Grids_Utilities.getValueALittleBitLarger(value);
        expResult = 1.0E-323d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 3
        value = 1.0E-323d;
        result = Grids_Utilities.getValueALittleBitLarger(value);
        expResult = 1.5E-323d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 4
        value = 1.0d;
        result = Grids_Utilities.getValueALittleBitLarger(value);
        expResult = 1.0000000000000002d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 5
        value = 1.0000000000000002d;
        result = Grids_Utilities.getValueALittleBitLarger(value);
        expResult = 1.0000000000000004d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 6
        value = 1000000.0d;
        result = Grids_Utilities.getValueALittleBitLarger(value);
        expResult = 1000000.0000000001d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 7
        value = 1000000.0000000001d;
        result = Grids_Utilities.getValueALittleBitLarger(value);
        expResult = 1000000.0000000002d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 8
        value = -1.0d;
        result = Grids_Utilities.getValueALittleBitLarger(value);
        expResult = -0.9999999999999999d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 9
        value = -0.9999999999999999d;
        result = Grids_Utilities.getValueALittleBitLarger(value);
        expResult = -0.9999999999999998d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 10
        value = -1000000.0d;
        result = Grids_Utilities.getValueALittleBitLarger(value);
        expResult = -999999.9999999999d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 11
        value = -999999.9999999999d;
        result = Grids_Utilities.getValueALittleBitLarger(value);
        expResult = -999999.9999999998d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getValueALittleBitSmaller method, of class Grids_Utilities.
     */
    @Test
    public void testGetValueALittleBitSmaller() {
        System.out.println("getValueALittleBitSmaller");
        double value;
        double expResult;
        double result;
        // Test 1
        value = 0.0d;
        result = Grids_Utilities.getValueALittleBitSmaller(value);
        expResult = -4.9E-324d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 2
        value = 4.9E-324d;
        result = Grids_Utilities.getValueALittleBitSmaller(value);
        expResult = 0.0d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 3
        value = 1.0E-323d;
        result = Grids_Utilities.getValueALittleBitSmaller(value);
        expResult = 4.9E-324d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 4
        value = 1.0d;
        result = Grids_Utilities.getValueALittleBitSmaller(value);
        expResult = 0.9999999999999999d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 5
        value = 1000000.0d;
        result = Grids_Utilities.getValueALittleBitSmaller(value);
        expResult = 999999.9999999999d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 6
        value = 1000000.0000000001d;
        result = Grids_Utilities.getValueALittleBitSmaller(value);
        expResult = 1000000.0d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 7
        value = -1.0d;
        result = Grids_Utilities.getValueALittleBitSmaller(value);
        expResult = -1.0000000000000002d;
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getSamplePoints method, of class Grids_Utilities.
     */
    @Test
    public void testGetSamplePoints_5args() {
        System.out.println("getSamplePoints");
        Point2D.Double point;
        Point2D.Double exp;
        Point2D.Double obs;
        double angle;
        double maxDistance;
        int nDistances;
        int nAngles;
        Point2D.Double[][] expResult;
        Point2D.Double[][] result;
        double tollerance;
        // Test 1
        point = new Point2D.Double(0.0d, 0.0d);
        angle = 0.0;
        maxDistance = 1.0;
        nDistances = 1;
        nAngles = 1;
        expResult = new Point2D.Double[nAngles][nDistances];
        expResult[0][0] = new Point2D.Double(0.0d, 1.0d);
        result = Grids_Utilities.getSamplePoints(point, angle, maxDistance, nDistances, nAngles);
        for (int a = 0; a < nAngles; a++) {
            for (int d = 0; d < nDistances; d++) {
                System.out.println(result[a][d]);
            }
        }
        // Test 2
        tollerance = 0.000000000000001d;
        //tollerance = 0.0000000000000001d;
        point = new Point2D.Double(0.0d, 0.0d);
        angle = 0.0;
        maxDistance = 1.0;
        nDistances = 1;
        nAngles = 4;
        expResult = new Point2D.Double[nAngles][nDistances];
        expResult[0][0] = new Point2D.Double(0.0d, 1.0d);
        expResult[1][0] = new Point2D.Double(1.0d, 0.0d);
        expResult[2][0] = new Point2D.Double(0.0d, -1.0d);
        expResult[3][0] = new Point2D.Double(-1.0d, 0.0d);
        result = Grids_Utilities.getSamplePoints(point, angle, maxDistance, nDistances, nAngles);
        for (int a = 0; a < nAngles; a++) {
            for (int d = 0; d < nDistances; d++) {
                exp = expResult[a][d];
                obs = result[a][d];
//                System.out.println("exp.x " + exp.x);
//                System.out.println("exp.y " + exp.y);
//                System.out.println("obs.x " + obs.x);
//                System.out.println("obs.y " + obs.y);
                assertEquals(exp.x, obs.x, tollerance);
                assertEquals(exp.y, obs.y, tollerance);
            }
        }
        // Test 3
        tollerance = 0.000000000000001d;
        //tollerance = 0.0000000000000001d;
        point = new Point2D.Double(0.0d, 0.0d);
        angle = 0.0;
        maxDistance = 2.0;
        nDistances = 2;
        nAngles = 4;
        expResult = new Point2D.Double[nAngles][nDistances];
        expResult[0][0] = new Point2D.Double(0.0d, 2.0d);
        expResult[0][1] = new Point2D.Double(0.0d, 1.0d);
        expResult[1][0] = new Point2D.Double(2.0d, 0.0d);
        expResult[1][1] = new Point2D.Double(1.0d, 0.0d);
        expResult[2][0] = new Point2D.Double(0.0d, -2.0d);
        expResult[2][1] = new Point2D.Double(0.0d, -1.0d);
        expResult[3][0] = new Point2D.Double(-2.0d, 0.0d);
        expResult[3][1] = new Point2D.Double(-1.0d, 0.0d);
        result = Grids_Utilities.getSamplePoints(point, angle, maxDistance, nDistances, nAngles);
        for (int a = 0; a < nAngles; a++) {
            for (int d = 0; d < nDistances; d++) {
                exp = expResult[a][d];
                obs = result[a][d];
//                System.out.println("exp.x " + exp.x);
//                System.out.println("exp.y " + exp.y);
//                System.out.println("obs.x " + obs.x);
//                System.out.println("obs.y " + obs.y);
                assertEquals(exp.x, obs.x, tollerance);
                assertEquals(exp.y, obs.y, tollerance);
            }
        }
        // Test 4
        tollerance = 0.000000000001d;
        //tollerance = 0.0000000000001d;
        point = new Point2D.Double(0.0d, 0.0d);
        angle = 0.0;
        maxDistance = 2000.0;
        nDistances = 2;
        nAngles = 4;
        expResult = new Point2D.Double[nAngles][nDistances];
        expResult[0][0] = new Point2D.Double(0.0d, 2000.0d);
        expResult[0][1] = new Point2D.Double(0.0d, 1000.0d);
        expResult[1][0] = new Point2D.Double(2000.0d, 0.0d);
        expResult[1][1] = new Point2D.Double(1000.0d, 0.0d);
        expResult[2][0] = new Point2D.Double(0.0d, -2000.0d);
        expResult[2][1] = new Point2D.Double(0.0d, -1000.0d);
        expResult[3][0] = new Point2D.Double(-2000.0d, 0.0d);
        expResult[3][1] = new Point2D.Double(-1000.0d, 0.0d);
        result = Grids_Utilities.getSamplePoints(point, angle, maxDistance, nDistances, nAngles);
        for (int a = 0; a < nAngles; a++) {
            for (int d = 0; d < nDistances; d++) {
                exp = expResult[a][d];
                obs = result[a][d];
//                System.out.println("exp.x " + exp.x);
//                System.out.println("exp.y " + exp.y);
//                System.out.println("obs.x " + obs.x);
//                System.out.println("obs.y " + obs.y);
                assertEquals(exp.x, obs.x, tollerance);
                assertEquals(exp.y, obs.y, tollerance);
            }
        }
        double root2;
        // Test 5
        tollerance = 0.000000000001d;
        //tollerance = 0.0000000000001d;
        point = new Point2D.Double(0.0d, 0.0d);
        angle = 0.0;
        maxDistance = 2000.0;
        nDistances = 2;
        nAngles = 8;
        root2 = Math.sqrt(2.0d);
        expResult = new Point2D.Double[nAngles][nDistances];
        expResult[0][0] = new Point2D.Double(0.0d, 2000.0d);
        expResult[0][1] = new Point2D.Double(0.0d, 1000.0d);
        expResult[1][0] = new Point2D.Double(root2 * 1000.0d, root2 * 1000.0d);
        expResult[1][1] = new Point2D.Double(root2 * 500.0d, root2 * 500.0d);
        expResult[2][0] = new Point2D.Double(2000.0d, 0.0d);
        expResult[2][1] = new Point2D.Double(1000.0d, 0.0d);
        expResult[3][0] = new Point2D.Double(root2 * 1000.0d, root2 * -1000.0d);
        expResult[3][1] = new Point2D.Double(root2 * 500.0d, root2 * -500.0d);
        expResult[4][0] = new Point2D.Double(0.0d, -2000.0d);
        expResult[4][1] = new Point2D.Double(0.0d, -1000.0d);
        expResult[5][0] = new Point2D.Double(root2 * -1000.0d, root2 * -1000.0d);
        expResult[5][1] = new Point2D.Double(root2 * -500.0d, root2 * -500.0d);
        expResult[6][0] = new Point2D.Double(-2000.0d, 0.0d);
        expResult[6][1] = new Point2D.Double(-1000.0d, 0.0d);
        expResult[7][0] = new Point2D.Double(root2 * -1000.0d, root2 * 1000.0d);
        expResult[7][1] = new Point2D.Double(root2 * -500.0d, root2 * 500.0d);
        result = Grids_Utilities.getSamplePoints(point, angle, maxDistance, nDistances, nAngles);
        for (int a = 0; a < nAngles; a++) {
            for (int d = 0; d < nDistances; d++) {
                exp = expResult[a][d];
                obs = result[a][d];
//                System.out.println("exp.x " + exp.x);
//                System.out.println("exp.y " + exp.y);
//                System.out.println("obs.x " + obs.x);
//                System.out.println("obs.y " + obs.y);
                assertEquals(exp.x, obs.x, tollerance);
                assertEquals(exp.y, obs.y, tollerance);
            }
        }
        // Test 6
        tollerance = 0.0d;
        //tollerance = 0.0000000000001d;
        point = new Point2D.Double(10000.0d, -100000.0d);
        angle = 0.0;
        maxDistance = 2000.0;
        nDistances = 2;
        nAngles = 8;
        root2 = Math.sqrt(2.0d);
        expResult = new Point2D.Double[nAngles][nDistances];
        expResult[0][0] = new Point2D.Double(
                0.0d + point.x, 2000.0d + point.y);
        expResult[0][1] = new Point2D.Double(
                0.0d + point.x, 1000.0d + point.y);
        expResult[1][0] = new Point2D.Double(
                (root2 * 1000.0d) + point.x, (root2 * 1000.0d) + point.y);
        expResult[1][1] = new Point2D.Double(
                (root2 * 500.0d) + point.x, (root2 * 500.0d) + point.y);
        expResult[2][0] = new Point2D.Double(
                (2000.0d) + point.x, (0.0d) + point.y);
        expResult[2][1] = new Point2D.Double(
                (1000.0d) + point.x, (0.0d) + point.y);
        expResult[3][0] = new Point2D.Double(
                (root2 * 1000.0d) + point.x, (root2 * -1000.0d) + point.y);
        expResult[3][1] = new Point2D.Double(
                (root2 * 500.0d) + point.x, (root2 * -500.0d) + point.y);
        expResult[4][0] = new Point2D.Double(
                (0.0d) + point.x, (-2000.0d) + point.y);
        expResult[4][1] = new Point2D.Double(
                (0.0d) + point.x, (-1000.0d) + point.y);
        expResult[5][0] = new Point2D.Double(
                (root2 * -1000.0d) + point.x, (root2 * -1000.0d) + point.y);
        expResult[5][1] = new Point2D.Double(
                (root2 * -500.0d) + point.x, (root2 * -500.0d) + point.y);
        expResult[6][0] = new Point2D.Double(
                (-2000.0d) + point.x, (0.0d) + point.y);
        expResult[6][1] = new Point2D.Double(
                (-1000.0d) + point.x, (0.0d) + point.y);
        expResult[7][0] = new Point2D.Double(
                (root2 * -1000.0d) + point.x, (root2 * 1000.0d) + point.y);
        expResult[7][1] = new Point2D.Double(
                (root2 * -500.0d) + point.x, (root2 * 500.0d) + point.y);
        result = Grids_Utilities.getSamplePoints(point, angle, maxDistance, nDistances, nAngles);
        for (int a = 0; a < nAngles; a++) {
            for (int d = 0; d < nDistances; d++) {
                exp = expResult[a][d];
                obs = result[a][d];
//                System.out.println("exp.x " + exp.x);
//                System.out.println("exp.y " + exp.y);
//                System.out.println("obs.x " + obs.x);
//                System.out.println("obs.y " + obs.y);
                assertEquals(exp.x, obs.x, tollerance);
                assertEquals(exp.y, obs.y, tollerance);
            }
        }
    }

    /**
     * Test of distance method, of class Grids_Utilities.
     */
    @Test
    public void testDistance() {
        System.out.println("distance");
        double x1;
        double y1;
        double x2;
        double y2;
        double expResult;
        double result;
        // Test 1
        x1 = 0.0;
        y1 = 0.0;
        x2 = 1.0;
        y2 = 1.0;
        expResult = Math.sqrt(2.0d);
        result = Grids_Utilities.distance(x1, y1, x2, y2);
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 2
        x1 = 2.0;
        y1 = 2.0;
        x2 = 3.0;
        y2 = 3.0;
        expResult = Math.sqrt(2.0d);
        result = Grids_Utilities.distance(x1, y1, x2, y2);
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 3
        x1 = -2.0;
        y1 = -2.0;
        x2 = -3.0;
        y2 = -3.0;
        expResult = Math.sqrt(2.0d);
        result = Grids_Utilities.distance(x1, y1, x2, y2);
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 4
        x1 = -3.0;
        y1 = -3.0;
        x2 = -2.0;
        y2 = -2.0;
        expResult = Math.sqrt(2.0d);
        result = Grids_Utilities.distance(x1, y1, x2, y2);
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of angle method, of class Grids_Utilities.
     */
    @Test
    public void testAngle() {
        System.out.println("angle");
        double x1;
        double y1;
        double x2;
        double y2;
        double expResult;
        double result;
        // Test 1
        x1 = 0.0;
        y1 = 0.0;
        x2 = 1.0;
        y2 = 1.0;
        expResult = Math.PI / 4.0d;
        result = Grids_Utilities.angle(x1, y1, x2, y2);
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 2
        x1 = 0.0;
        y1 = 0.0;
        x2 = -1.0;
        y2 = -1.0;
        expResult = (5.0d * Math.PI) / 4.0d;
        System.out.println(expResult);
//        expResult = 3.9269908169872414;
//        System.out.println(expResult);
        result = Grids_Utilities.angle(x1, y1, x2, y2);
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
        // Test 3
        x1 = 1.0;
        y1 = 0.0;
        x2 = -1.0;
        y2 = -1.0;
        System.out.println(expResult);
        expResult = (3 * Math.PI / 2.0d) - Math.atan(1.0d / 2.0d);
//        System.out.println(expResult);
//        expResult = 4.2487413713838835d;
        result = Grids_Utilities.angle(x1, y1, x2, y2);
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
    }

//    /**
//     * Test of densityPlot method, of class Grids_Utilities.
//     */
//    @Test
//    public void testDensityPlot() {
//        System.out.println("densityPlot");
//        Grids_GridDouble xGrid = null;
//        Grids_GridDouble yGrid = null;
//        int divisions = 0;
//        Grids_GridDoubleFactory gridFactory = null;
//        Object[] expResult = null;
//        Object[] result = Grids_Utilities.densityPlot(xGrid, yGrid, divisions, gridFactory);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}
