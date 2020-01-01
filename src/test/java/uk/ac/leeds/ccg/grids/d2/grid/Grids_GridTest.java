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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.generic.io.Generic_Path;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.core.Grids_Strings;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_long;
import uk.ac.leeds.ccg.grids.d2.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridFactoryDouble;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridFactoryInt;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_Stats;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.grids.memory.Grids_Account;
import uk.ac.leeds.ccg.grids.memory.Grids_AccountDetail;
import uk.ac.leeds.ccg.grids.process.Grids_Processor;

/**
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridTest {

    Generic_Environment env;
    Grids_Environment ge;
    Grids_Processor gp;

    public Grids_GridTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() throws Exception {
        Path dataDir = Paths.get(System.getProperty("user.home"),
                Grids_Strings.s_data);
        env = new Generic_Environment(new Generic_Defaults(
                Paths.get(dataDir.toString(), Grids_Strings.s_generic)));
        Generic_Path dir = new Generic_Path(dataDir);
        ge = new Grids_Environment(env, dir);
        gp = new Grids_Processor(ge);
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of isSameFrame method, of class Grids_Processor.
     */
    @Test
    public void testIsSameFrame() throws Exception {
        System.out.println("isSameFrame");
        Grids_GridFactoryDouble gfd = gp.gridFactoryDouble;
        // Test 1
        Grids_Grid g0 = gfd.create(10, 10);
        Grids_Grid g1 = gfd.create(10, 10);
        boolean expResult = true;
        boolean result = g0.isSameFrame(g1);
        assertEquals(expResult, result);
        // Test 2
        g0 = gfd.create(10, 10);
        g1 = gfd.create(10, 11);
        expResult = false;
        result = g0.isSameFrame(g1);
        assertEquals(expResult, result);
        // Test 2
        Grids_GridFactoryInt gfi = gp.gridFactoryInt;
        g0 = gfd.create(10, 10);
        g1 = gfi.create(10, 10);
        expResult = true;
        result = g0.isSameFrame(g1);
        assertEquals(expResult, result);
    }

//    /**
//     * Test of getCellDistance method, of class Grids_Grid.
//     */
//    @Test
//    public void testGetCellDistance() {
//        System.out.println("getCellDistance");
//        BigDecimal distance = null;
//        Grids_Grid instance = null;
//        BigDecimal expResult = null;
//        BigDecimal result = instance.getCellDistance(distance);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
