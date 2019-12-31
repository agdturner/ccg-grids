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
package uk.ac.leeds.ccg.grids.d2.util;

import uk.ac.leeds.ccg.grids.d2.util.Grids_Utilities;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.core.Grids_Strings;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDouble;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridFactoryDouble;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridIteratorDouble;
import uk.ac.leeds.ccg.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.grids.process.Grids_Processor;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Random;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.generic.io.Generic_IO;
import uk.ac.leeds.ccg.generic.io.Generic_Path;
import uk.ac.leeds.ccg.math.Math_BigInteger;

/**
 *
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_UtilitiesTest {

    public Grids_UtilitiesTest() {
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
     * Test of getTime method, of class Grids_Utilities.
     */
    @Test
    public void testGetTime_long() {
        System.out.println("getTime");
        long time;
        String expResult;
        String result;
        // Test 1
        time = 0L;
        expResult = "days=0, hours=0, mins=0, secs=0, millisecs=0";
        result = Grids_Utilities.getTime(time);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result);
        // Test 2
        time = 60L;
        expResult = "days=0, hours=0, mins=0, secs=0, millisecs=60";
        result = Grids_Utilities.getTime(time);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result);
        // Test 3
        time = 600L;
        expResult = "days=0, hours=0, mins=0, secs=0, millisecs=600";
        result = Grids_Utilities.getTime(time);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result);
        // Test 4
        time = 6000L;
        expResult = "days=0, hours=0, mins=0, secs=6, millisecs=0";
        result = Grids_Utilities.getTime(time);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result);
        // Test 5
        time = 60000L;
        expResult = "days=0, hours=0, mins=1, secs=0, millisecs=0";
        result = Grids_Utilities.getTime(time);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result);
        // Test 6
        time = 3600000L;
        expResult = "days=0, hours=1, mins=0, secs=0, millisecs=0";
        result = Grids_Utilities.getTime(time);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result);
        // Test 7
        time = 3666666L;
        expResult = "days=0, hours=1, mins=1, secs=6, millisecs=666";
        result = Grids_Utilities.getTime(time);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result);
        // Test 7
        time = 15555555L;
        expResult = "days=0, hours=4, mins=19, secs=15, millisecs=555";
        result = Grids_Utilities.getTime(time);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result);
        // Test 8
        time = 555555555L;
        expResult = "days=6, hours=10, mins=19, secs=15, millisecs=555";
        result = Grids_Utilities.getTime(time);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result);
        // Test 9
        time = Long.MAX_VALUE;
        expResult = "days=106751991167, hours=7, mins=12, secs=55, millisecs=807";
        result = Grids_Utilities.getTime(time);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getTime method, of class Grids_Utilities.
     */
    @Test
    public void testGetTime_BigInteger() {
        System.out.println("getTime");
        BigInteger time;
        String expResult;
        String result;
        // Test 9
        time = Math_BigInteger.LONG_MAX_VALUE;
        expResult = "days=106751991167, hours=7, mins=12, secs=55, millisecs=807";
        result = Grids_Utilities.getTime(time);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result);
        // Test 9
        time = Math_BigInteger.INTEGER_MAX_VALUE;
        expResult = "days=24, hours=20, mins=31, secs=23, millisecs=647";
        result = Grids_Utilities.getTime(time);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result);
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
//        for (int a = 0; a < nAngles; a++) {
//            for (int d = 0; d < nDistances; d++) {
//                System.out.println(result[a][d]);
//            }
//        }
        Assertions.assertArrayEquals(expResult, result);
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
                Assertions.assertEquals(exp.x, obs.x, tollerance);
                Assertions.assertEquals(exp.y, obs.y, tollerance);
            }
        }
        //Assertions.assertArrayEquals(expResult, result, tollerance);
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
                Assertions.assertEquals(exp.x, obs.x, tollerance);
                Assertions.assertEquals(exp.y, obs.y, tollerance);
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
                Assertions.assertEquals(exp.x, obs.x, tollerance);
                Assertions.assertEquals(exp.y, obs.y, tollerance);
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
                Assertions.assertEquals(exp.x, obs.x, tollerance);
                Assertions.assertEquals(exp.y, obs.y, tollerance);
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
        result = Grids_Utilities.getSamplePoints(point, angle, maxDistance,
                nDistances, nAngles);
        for (int a = 0; a < nAngles; a++) {
            for (int d = 0; d < nDistances; d++) {
                exp = expResult[a][d];
                obs = result[a][d];
//                System.out.println("exp.x " + exp.x);
//                System.out.println("exp.y " + exp.y);
//                System.out.println("obs.x " + obs.x);
//                System.out.println("obs.y " + obs.y);
                Assertions.assertEquals(exp.x, obs.x, tollerance);
                Assertions.assertEquals(exp.y, obs.y, tollerance);
            }
        }
    }

    /**
     * Test of distance method, of class Grids_Utilities.
     */
    @Test
    public void testDistance_4args() {
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
        //System.out.println(result);
        Assertions.assertEquals(expResult, result, 0.0);
        // Test 2
        x1 = 2.0;
        y1 = 2.0;
        x2 = 3.0;
        y2 = 3.0;
        expResult = Math.sqrt(2.0d);
        result = Grids_Utilities.distance(x1, y1, x2, y2);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result, 0.0);
        // Test 3
        x1 = -2.0;
        y1 = -2.0;
        x2 = -3.0;
        y2 = -3.0;
        expResult = Math.sqrt(2.0d);
        result = Grids_Utilities.distance(x1, y1, x2, y2);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result, 0.0);
        // Test 4
        x1 = -3.0;
        y1 = -3.0;
        x2 = -2.0;
        y2 = -2.0;
        expResult = Math.sqrt(2.0d);
        result = Grids_Utilities.distance(x1, y1, x2, y2);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result, 0.0);
        // Test 5
        x1 = 0d;
        y1 = 0d;
        x2 = 3d;
        y2 = 4d;
        expResult = 5d;
        result = Grids_Utilities.distance(x1, y1, x2, y2);
        Assertions.assertEquals(expResult, result, 0.0);

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
        Assertions.assertEquals(expResult, result, 0.0);
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
        Assertions.assertEquals(expResult, result, 0.0);
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
        Assertions.assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of distance method, of class Grids_Utilities.
     */
    @Test
    public void testDistance_6args() {
        System.out.println("distance");
        int dp;
        RoundingMode rm;
        BigDecimal expResult;
        BigDecimal result;
        BigDecimal x1;
        BigDecimal y1;
        BigDecimal x2;
        BigDecimal y2;
        // Test 1
        dp = 1;
        rm = RoundingMode.HALF_UP;
        x1 = BigDecimal.ZERO;
        y1 = BigDecimal.ZERO;
        x2 = new BigDecimal(1);
        y2 = new BigDecimal(1);
        expResult = new BigDecimal("1.4");
        result = Grids_Utilities.distance(x1, y1, x2, y2, dp, rm);
        Assertions.assertEquals(expResult, result);
        //System.out.println(Math.sqrt(2.0d));
        // Test 2
        dp = 10;
        expResult = new BigDecimal("1.4142135624");
        result = Grids_Utilities.distance(x1, y1, x2, y2, dp, rm);
        //System.out.println(result);
        Assertions.assertEquals(expResult, result);
        // Test 3
        dp = 20;
        expResult = new BigDecimal("1.41421356237309504880");
        result = Grids_Utilities.distance(x1, y1, x2, y2, dp, rm);
        System.out.println(result);
        Assertions.assertEquals(expResult, result);
        // Test 4
        dp = 1;
        rm = RoundingMode.HALF_UP;
        x1 = new BigDecimal(-3);
        y1 = new BigDecimal(-3);
        x2 = new BigDecimal(-2);
        y2 = new BigDecimal(-2);
        expResult = new BigDecimal("1.4");
        result = Grids_Utilities.distance(x1, y1, x2, y2, dp, rm);
        Assertions.assertEquals(expResult, result);
        // Test 5
        dp = 1;
        rm = RoundingMode.HALF_UP;
        x1 = BigDecimal.ZERO;
        y1 = BigDecimal.ZERO;
        x2 = new BigDecimal(3);
        y2 = new BigDecimal(4);
        expResult = new BigDecimal("5.0");
        result = Grids_Utilities.distance(x1, y1, x2, y2, dp, rm);
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of densityPlot method, of class Grids_Utilities.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDensityPlot() throws Exception {
        System.out.println("densityPlot");
        Path dataDir = Paths.get(System.getProperty("user.home"),
                Grids_Strings.s_data);
        Generic_Environment env = new Generic_Environment(new Generic_Defaults(
                Paths.get(dataDir.toString(), Grids_Strings.s_generic)));
        Generic_Path dir = new Generic_Path(dataDir);
        Grids_Environment ge = new Grids_Environment(env, dir);
        Grids_Processor gp = new Grids_Processor(ge);
        Grids_GridFactoryDouble gfd = gp.gridFactoryDouble;
        long nrows = 10L;
        long ncols = 10L;
        int dp = 1;
        RoundingMode rm = RoundingMode.HALF_UP;
        Grids_Dimensions dimensions = new Grids_Dimensions(BigDecimal.ZERO,
                BigDecimal.valueOf(ncols), BigDecimal.ZERO,
                BigDecimal.valueOf(nrows), BigDecimal.ONE);
        Grids_GridDouble xGrid = gfd.create(nrows, ncols, dimensions);
        setRandom(ge, xGrid);
        Grids_GridDouble yGrid = gfd.create(nrows, ncols, dimensions);
        setRandom(ge, yGrid);
        int divisions = 10;
        //Object[] expResult = null;
        Object[] result = Grids_Utilities.densityPlot(xGrid, yGrid, divisions, gp, dp, rm);
        Grids_ImageExporter ie = new Grids_ImageExporter(ge);
        String type = "PNG";
        Path file = Generic_IO.createNewFile(gp.env.files.getGeneratedDir()
                .getPath(), "Test", "." + type);
        Grids_GridDouble g = (Grids_GridDouble) result[3];
        ie.toGreyScaleImage(g, gp, file, type);
        //TreeMap<Double, > colours;
        //ie.toColourImage(0 , ie.colours, Color.orange, yGrid, outtype);
        //assertArrayEquals(expResult, result);
    }

    public void setRandom(Grids_Environment env, Grids_GridDouble g) 
            throws Exception {
        Random r = new Random();
        int ncr = g.getNChunkRows();
        int ncc = g.getNChunkCols();
        for (int cr = 0; cr < ncr; cr ++) {
            int chunkNRows = g.getChunkNRows(cr);
            for (int cc = 0; cc < ncc; cc ++) {
                int chunkNCols = g.getChunkNCols(cc);
                for (int row = 0; row < chunkNRows; row++) {
                    for (int col = 0; col < chunkNCols; col++) {
                        g.setCell(cr, cc, row, col, r.nextDouble() * 100);
                    }
                }
            }
        }
    }
}
