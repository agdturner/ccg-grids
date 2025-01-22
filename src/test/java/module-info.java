/*
 * Copyright 2025 Centre for Computational Geography.
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

module uk.ac.leeds.ccg.grids.test {
    requires uk.ac.leeds.ccg.grids;
    
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;
    requires org.junit.jupiter.params;

    opens uk.ac.leeds.ccg.grids.core.test to org.junit.platform.commons;
//    opens uk.ac.leeds.ccg.grids.d2.test to org.junit.platform.commons;
//    opens uk.ac.leeds.ccg.grids.d2.chunk.test to org.junit.platform.commons;
//    opens uk.ac.leeds.ccg.grids.d2.chunk.b.test to org.junit.platform.commons;
//    opens uk.ac.leeds.ccg.grids.d2.chunk.bd.test to org.junit.platform.commons;
//    opens uk.ac.leeds.ccg.grids.d2.chunk.d.test to org.junit.platform.commons;
//    opens uk.ac.leeds.ccg.grids.d2.chunk.i.test to org.junit.platform.commons;
    opens uk.ac.leeds.ccg.grids.d2.grid.test to org.junit.platform.commons;
//    opens uk.ac.leeds.ccg.grids.d2.grid.b.test to org.junit.platform.commons;
//    opens uk.ac.leeds.ccg.grids.d2.grid.bd.test to org.junit.platform.commons;
//    opens uk.ac.leeds.ccg.grids.d2.grid.d.test to org.junit.platform.commons;
//    opens uk.ac.leeds.ccg.grids.d2.grid.i.test to org.junit.platform.commons;
//    opens uk.ac.leeds.ccg.grids.d2.stats.test to org.junit.platform.commons;
    opens uk.ac.leeds.ccg.grids.d2.util.test to org.junit.platform.commons;
    //opens uk.ac.leeds.ccg.grids.d3.test to org.junit.platform.commons;
//    opens uk.ac.leeds.ccg.grids.io.test to org.junit.platform.commons;
//    opens uk.ac.leeds.ccg.grids.memory.test to org.junit.platform.commons;
    opens uk.ac.leeds.ccg.grids.process.test to org.junit.platform.commons;
}
