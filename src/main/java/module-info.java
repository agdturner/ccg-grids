module uk.ac.leeds.ccg.grids {
    requires transitive java.logging;
    requires java.desktop;
    requires uk.ac.leeds.ccg.generic;
    requires transitive uk.ac.leeds.ccg.math;
    exports uk.ac.leeds.ccg.grids.core;
    exports uk.ac.leeds.ccg.grids.d2;
    exports uk.ac.leeds.ccg.grids.d2.chunk;
    exports uk.ac.leeds.ccg.grids.d2.chunk.b;
    exports uk.ac.leeds.ccg.grids.d2.chunk.bd;
    exports uk.ac.leeds.ccg.grids.d2.chunk.d;
    exports uk.ac.leeds.ccg.grids.d2.chunk.i;
    exports uk.ac.leeds.ccg.grids.d2.chunk.stats;
    exports uk.ac.leeds.ccg.grids.d2.grid;
    exports uk.ac.leeds.ccg.grids.d2.grid.b;
    exports uk.ac.leeds.ccg.grids.d2.grid.bd;
    exports uk.ac.leeds.ccg.grids.d2.grid.d;
    exports uk.ac.leeds.ccg.grids.d2.grid.i;
    exports uk.ac.leeds.ccg.grids.d2.grid.stats;
    exports uk.ac.leeds.ccg.grids.d2.stats;
    exports uk.ac.leeds.ccg.grids.d2.util;
    //exports uk.ac.leeds.ccg.grids.d3;
    exports uk.ac.leeds.ccg.grids.io;
    exports uk.ac.leeds.ccg.grids.memory;
    exports uk.ac.leeds.ccg.grids.process;
}